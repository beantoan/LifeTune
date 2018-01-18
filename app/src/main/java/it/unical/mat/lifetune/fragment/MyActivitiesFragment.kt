package it.unical.mat.lifetune.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.activity.MainActivity
import it.unical.mat.lifetune.entity.FitnessActivity
import it.unical.mat.lifetune.entity.FitnessChartEntry
import it.unical.mat.lifetune.util.AppDialog
import kotlinx.android.synthetic.main.fragment_my_activities.*
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * Created by beantoan on 11/17/17.
 */
class MyActivitiesFragment : BaseFragment() {

    private var runningData: ArrayList<FitnessChartEntry> = ArrayList()
    private var onBicycleData: ArrayList<FitnessChartEntry> = ArrayList()
    private var walkingData: ArrayList<FitnessChartEntry> = ArrayList()

    private var totalActiveTime = 0f
    private var totalSteps = 0f
    private var totalDistance = 0f
    private var totalCalories = 0f

    private var fitnessHours: ArrayList<Float> = ArrayList()

    private val fitnessCalendar = Calendar.getInstance()

    private var mainActivity: MainActivity? = null

    private var isLoadingFitnessData = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val rootView = inflater.inflate(R.layout.fragment_my_activities, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onViewCreatedTasks()
    }

    override fun onResume() {
        super.onResume()

        onResumeTasks()
    }

    private fun onViewCreatedTasks() {
        Log.d(TAG, "onViewCreatedTasks")

        mainActivity = activity as MainActivity

        mainActivity!!.setupToggleDrawer(toolbar)

        setupFitnessDateInput()

        setupShareFitnessChartEvents()
    }

    private fun onResumeTasks() {
        Log.d(TAG, "onResumeTasks")

        readFitnessData()
    }

    private fun setupFitnessDateInput() {
        fitness_date.inputType = InputType.TYPE_NULL

        fitness_date.setOnClickListener {
            AppDialog.showDatePickerDialog(mainActivity!!, fitness_date,
                    fitnessCalendar.get(Calendar.YEAR), fitnessCalendar.get(Calendar.MONTH), fitnessCalendar.get(Calendar.DAY_OF_MONTH),
                    false, object : AppDialog.DateSetListener {
                override fun onSetListener(_year: Int, _month: Int, _day: Int) {

                    fitnessCalendar.set(_year, _month, _day)

                    readFitnessData()
                }
            })
        }

        val dateFormat = DateFormat.getDateInstance(DateFormat.LONG)

        fitness_date.setText(dateFormat.format(fitnessCalendar.timeInMillis))
    }

    private fun readFitnessData() {

        val fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .addDataType(DataType.TYPE_STEP_COUNT_CADENCE)
                .addDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .addDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED)
                .addDataType(DataType.AGGREGATE_DISTANCE_DELTA)
                .addDataType(DataType.TYPE_DISTANCE_DELTA)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                .build()

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), fitnessOptions)) {
            return
        }

        if (isLoadingFitnessData) {
            return
        }

        isLoadingFitnessData = true

        resetFitnessData()

        val dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM)

        fitnessCalendar.set(Calendar.HOUR_OF_DAY, 0)
        fitnessCalendar.set(Calendar.MINUTE, 0)
        fitnessCalendar.set(Calendar.SECOND, 0)
        val startTime = fitnessCalendar.timeInMillis

        fitnessCalendar.set(Calendar.HOUR_OF_DAY, 23)
        fitnessCalendar.set(Calendar.MINUTE, 59)
        fitnessCalendar.set(Calendar.SECOND, 59)
        val endTime = fitnessCalendar.timeInMillis

        Log.d(TAG, "Start time: " + dateTimeFormat.format(startTime))
        Log.d(TAG, "End time: " + dateTimeFormat.format(endTime))

        val ds = DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.google.android.gms")
                .build()

        val readRequest = DataReadRequest.Builder()
//                .aggregate(ds, DataType.AGGREGATE_STEP_COUNT_DELTA)
//                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
//                .bucketByActivityType(1, TimeUnit.MINUTES)

                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .bucketByActivitySegment(1, TimeUnit.MINUTES)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .enableServerQueries()
                .build()

        val account = GoogleSignIn.getLastSignedInAccount(activity)

        showProgressBar(progress_bar)

        Fitness.getHistoryClient(activity!!, account)
                .readData(readRequest)
                .addOnCompleteListener(activity!!, { dataReadResponse ->
                    Log.d(TAG, "Fitness.getHistoryClient#addOnCompleteListener: buckets.size=${dataReadResponse.result.buckets.size}")

                    convertBucketsToBarChartData(dataReadResponse.result.buckets)
                })
                .addOnSuccessListener(activity!!, {
                    Log.d(TAG, "Fitness.getHistoryClient#addOnSuccessListener")

                    isLoadingFitnessData = false

                    hideProgressBar(progress_bar)

                    fitnessHours.sortBy { it }

                    addLackedHoursToEntries(fitnessHours, runningData)
                    addLackedHoursToEntries(fitnessHours, walkingData)
                    addLackedHoursToEntries(fitnessHours, onBicycleData)

                    updateSummaryInfo(totalActiveTime, totalSteps, totalDistance, totalCalories)

                    updateFitnessChart(steps_chart, FitnessChartEntry.TYPE_STEPS,
                            runningData, walkingData, onBicycleData, fitnessHours)

                    updateFitnessChart(distance_chart, FitnessChartEntry.TYPE_DISTANCE,
                            runningData, walkingData, onBicycleData, fitnessHours)

                    updateFitnessChart(calories_chart, FitnessChartEntry.TYPE_CALORIES,
                            runningData, walkingData, onBicycleData, fitnessHours)
                })
                .addOnFailureListener(activity!!, {
                    Log.d(TAG, "Fitness.getHistoryClient#addOnFailureListener")

                    isLoadingFitnessData = false

                    hideProgressBar(progress_bar)

                    AppDialog.error(R.string.fitness_read_history_error_title, R.string.fitness_read_history_error_message, activity!!)
                })
    }

    private fun resetFitnessData() {
        fitnessHours.clear()

        runningData.clear()
        walkingData.clear()
        onBicycleData.clear()

        totalActiveTime = 0f
        totalSteps = 0f
        totalDistance = 0f
        totalCalories = 0f
    }

    private fun convertBucketsToBarChartData(buckets: List<Bucket>) {

        val dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM)
        val hourFormat = SimpleDateFormat("k")

        buckets.forEach { bucket ->
            val startMs = bucket.getStartTime(TimeUnit.MILLISECONDS)
            val endMs = bucket.getEndTime(TimeUnit.MILLISECONDS)
            val start = dateTimeFormat.format(startMs)
            val end = dateTimeFormat.format(endMs)

            val hour = hourFormat.format(startMs).toFloat()

            Log.d(TAG, "-- activity=${bucket.activity}")

            if (bucket.activity in ACCEPTED_FITNESS_ACTIVITIES) {

                totalActiveTime += endMs - startMs

                if (!fitnessHours.contains(hour)) {
                    fitnessHours.add(hour)
                }
                
                bucket.dataSets.forEach { dataSet ->

                    dataSet.dataPoints.forEach { dataPoint ->
                        val fields = dataPoint.dataType.fields

                        val steps = if (fields.contains(Field.FIELD_STEPS)) dataPoint.getValue(Field.FIELD_STEPS).toString().toFloat() else 0f
                        val distance = if (fields.contains(Field.FIELD_DISTANCE)) dataPoint.getValue(Field.FIELD_DISTANCE).toString().toFloat() else 0f
                        val calories = if (fields.contains(Field.FIELD_CALORIES)) dataPoint.getValue(Field.FIELD_CALORIES).toString().toFloat() else 0f

                        totalSteps += steps
                        totalDistance += distance
                        totalCalories += calories

                        var existedHour = false

                        when (bucket.activity) {
                            FitnessActivity.RUNNING -> {
                                for (runningDatum in runningData) {
                                    if (runningDatum.hour == hour) {
                                        existedHour = true
                                        runningDatum.addFitnessData(steps, distance, calories)
                                        break
                                    }
                                }

                                if (!existedHour) {
                                    runningData.add(FitnessChartEntry(hour, steps, distance, calories))
                                }
                            }
                            FitnessActivity.ON_BICYCLE -> {
                                for (onBicycleDatum in onBicycleData) {
                                    if (onBicycleDatum.hour == hour) {
                                        existedHour = true
                                        onBicycleDatum.addFitnessData(steps, distance, calories)
                                        break
                                    }
                                }

                                if (!existedHour) {
                                    onBicycleData.add(FitnessChartEntry(hour, steps, distance, calories))
                                }
                            }
                            FitnessActivity.WALKING -> {

                                for (walkingDatum in walkingData) {
                                    if (walkingDatum.hour == hour) {
                                        existedHour = true
                                        walkingDatum.addFitnessData(steps, distance, calories)
                                        break
                                    }
                                }

                                if (!existedHour) {
                                    walkingData.add(FitnessChartEntry(hour, steps, distance, calories))
                                }
                            }
                        }

                        Log.d(TAG, "-- activity=${bucket.activity} - steps=$steps - calories=$calories - distance=$distance - start=$start - end=$end - hour=$hour")
                    }
                }
            }
        }
    }

    private fun addLackedHoursToEntries(hours: List<Float>, entries: ArrayList<FitnessChartEntry>) {
        if (entries.isEmpty()) {
            return
        }

        hours.forEach { hour ->
            val fitnessChartEntry = FitnessChartEntry(hour)

            if (!entries.contains(fitnessChartEntry)) {
                entries.add(fitnessChartEntry)
            }
        }

        entries.sortBy { it.hour }
    }

    private fun convertMilliSecondsToHuman(time: Float): String {
        val timeLong = time.toLong()
        val timeHours = TimeUnit.MILLISECONDS.toHours(timeLong)
        val timeMinutes = TimeUnit.MILLISECONDS.toMinutes(timeLong) - TimeUnit.HOURS.toMinutes(timeHours)

        val timeHoursStr = if (timeHours > 0) "${timeHours}h" else ""
        val timeMinutesStr = if (timeMinutes > 0) "${timeMinutes}m" else ""
        val timeSpace = if (timeHoursStr == "" && timeMinutesStr == "") "0" else
            if (timeHoursStr == "" || timeMinutesStr == "") "" else " "

        return "$timeHoursStr$timeSpace$timeMinutesStr"
    }

    private fun convertDistanceToHuman(distance: Float): String {
        val distanceKm = Math.round(distance / 1000)
        val distanceM = (distance - distanceKm * 1000).toInt()

        val distanceKmStr = if (distanceKm > 0) "${distanceKm}km" else ""
        val distanceMStr = if (distanceM > 0) "${distanceM}m" else ""
        val distanceSpace = if (distanceKmStr == "" && distanceMStr == "") "0" else
            if (distanceKmStr == "" || distanceMStr == "") "" else " "

        return "$distanceKmStr$distanceSpace$distanceMStr"
    }

    private fun updateSummaryInfo(_totalActiveTime: Float, _totalSteps: Float,
                                  _totalDistance: Float, _totalCalories: Float) {

        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

        total_active_time.text = convertMilliSecondsToHuman(_totalActiveTime)
        total_steps.text = numberFormat.format(_totalSteps.toInt())
        total_distance.text = convertDistanceToHuman(_totalDistance)
        total_calories.text = Math.round(_totalCalories).toString()
    }

    private fun getHoursAxisValueFormatter(_fitnessHours: List<Float>): IAxisValueFormatter {
        return IAxisValueFormatter { value, axis ->
            val valInt = value.toInt()
            val size = _fitnessHours.count()

            if (valInt in 0..(size - 1)) _fitnessHours[valInt].toInt().toString() else ""
        }
    }

    private fun getDefaultAxisValueFormatter(): IAxisValueFormatter {
        return IAxisValueFormatter { value, axis ->
            value.toInt().toString()
        }
    }

    /**
     * https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/BarChartActivityMultiDataset.java#L180
     * (barWith + barSpace) * numberOfBars + groupSpace = 1
     *
     * return List (groupSpace, barSpace, barWidth)
     */
    private fun getGroupedBarChartParams(numberOfBarInGroup: Int): List<Float> {
        return when (numberOfBarInGroup) {
            3 -> {
                listOf(0.04f, 0.01f, 0.31f)
            }
            2 -> {
                listOf(0.04f, 0.01f, 0.47f)
            }
            else -> {
                listOf(0f, 0f, 0.8f)
            }
        }
    }

    private fun generateBarDataSet(chartData: BarData, type: Int, rawData: List<FitnessChartEntry>,
                                   chartTitle: String, chartColor: Int): Int {
        if (rawData.isEmpty()) {
            return 0
        }

        val entries = rawData.map { BarEntry(it.hour, it.getValueByType(type)) }

        val dataSet = BarDataSet(entries, chartTitle)
        dataSet.color = chartColor

        chartData.addDataSet(dataSet)

        return 1
    }

    private fun generateChartData(type: Int,
                                  _runningData: List<FitnessChartEntry>,
                                  _walkingData: List<FitnessChartEntry>,
                                  _onBicycleData: List<FitnessChartEntry>): Pair<Int, BarData> {

        val chartData = BarData()

        var numberOfBarInGroup = 0

        numberOfBarInGroup += generateBarDataSet(chartData, type, _walkingData,
                CHART_SERIES_TITLE_WALKING, CHART_SERIES_COLOR_WALKING)

        numberOfBarInGroup += generateBarDataSet(chartData, type, _runningData,
                CHART_SERIES_TITLE_RUNNING, CHART_SERIES_COLOR_RUNNING)

        numberOfBarInGroup += generateBarDataSet(chartData, type, _onBicycleData,
                CHART_SERIES_TITLE_BIKING, CHART_SERIES_COLOR_BIKING)

        return Pair(numberOfBarInGroup, chartData)
    }

    private fun updateFitnessChart(chart: BarChart, type: Int,
                                   _runningData: List<FitnessChartEntry>, _walkingData: List<FitnessChartEntry>,
                                   _onBicycleData: List<FitnessChartEntry>, _fitnessHours: List<Float>) {

        val (numberOfBarInGroup, chartData) = generateChartData(type, _runningData, _walkingData, _onBicycleData)

        val (groupSpace, barSpace, barWidth) = getGroupedBarChartParams(numberOfBarInGroup)

        Log.d(TAG, "updateFitnessChart: _walkingData.size=${_walkingData.size}, _runningData.size=${_runningData.size}, _onBicycleData.size=${_onBicycleData.size}")
        Log.d(TAG, "updateFitnessChart: fitnessHours=$_fitnessHours")
        Log.d(TAG, "updateFitnessChart: numberOfBarInGroup=$numberOfBarInGroup, groupSpace=$groupSpace, barSpace=$barSpace, barWidth=$barWidth")

        chartData.setDrawValues(true)
        chartData.barWidth = barWidth

        chart.clear()

        chart.data = chartData

        chart.setDrawValueAboveBar(true)
        chart.setPinchZoom(false)
        chart.setScaleEnabled(false)
        chart.setDrawBarShadow(false)
        chart.setDrawGridBackground(false)
        chart.animateY(3000)

        chart.description.isEnabled = false

        chart.axisRight.isEnabled = false

        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.granularity = 1f
        chart.xAxis.isGranularityEnabled = true
        chart.xAxis.setDrawGridLines(true)

        chart.rendererXAxis.paintAxisLabels.textAlign = Paint.Align.CENTER

        chart.legend.setDrawInside(false)

        if (numberOfBarInGroup < 2) {
            val maxHour = _fitnessHours.max()?.toInt() ?: -1
            val minHour = (_fitnessHours.min() ?: 0.5f) - 0.5f

            chart.xAxis.axisMinimum = minHour
            chart.xAxis.axisMaximum = maxHour + 1f

            chart.xAxis.labelCount = maxHour
            chart.xAxis.valueFormatter = getDefaultAxisValueFormatter()
            chart.xAxis.setCenterAxisLabels(false)
        } else {
            val groupBarSize = _fitnessHours.size

            chart.xAxis.labelCount = groupBarSize
            chart.xAxis.axisMinimum = 0f
            chart.xAxis.axisMaximum = chart.barData.getGroupWidth(groupSpace, barSpace) * groupBarSize
            chart.xAxis.valueFormatter = getHoursAxisValueFormatter(_fitnessHours)
            chart.xAxis.setCenterAxisLabels(true)

            chart.groupBars(0f, groupSpace, barSpace)
        }

        chart.notifyDataSetChanged()

        chart.invalidate()
    }

    private fun shareFitnessChart(chart: BarChart, fitnessType: Int) {
        Log.d(TAG, "shareFitnessChart")

        val fileName = fitnessCalendar.timeInMillis.toString()

        val dateFormat = DateFormat.getDateInstance(DateFormat.LONG)
        val dateTitle = dateFormat.format(fitnessCalendar.timeInMillis)

        val bitmapPath = MediaStore.Images.Media.insertImage(activity!!.contentResolver, chart.chartBitmap, fileName, null)
        val bitmapUri = Uri.parse(bitmapPath)

        val caption = when (fitnessType) {
            FitnessChartEntry.TYPE_CALORIES -> {
                "I burned ${totalCalories.toInt()} calories on $dateTitle"
            }
            FitnessChartEntry.TYPE_DISTANCE -> {
                val distance = convertDistanceToHuman(totalDistance)

                "I walked $distance on $dateTitle"
            }
            FitnessChartEntry.TYPE_STEPS -> {
                "I stepped ${totalSteps.toInt()} on $dateTitle"
            }
            else -> "My activities on $dateTitle"
        }

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, caption)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
        sendIntent.type = "image/jpeg"
        startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.share_playlist_dialog_title)))
    }

    private fun setupShareFitnessChartEvents() {
        Log.d(TAG, "setupShareFitnessChartEvents")

        share_calories.setOnClickListener { shareFitnessChart(calories_chart, FitnessChartEntry.TYPE_CALORIES) }

        share_distance.setOnClickListener { shareFitnessChart(distance_chart, FitnessChartEntry.TYPE_DISTANCE) }

        share_steps.setOnClickListener { shareFitnessChart(steps_chart, FitnessChartEntry.TYPE_STEPS) }
    }

    companion object {
        val TAG = MyActivitiesFragment::class.java.simpleName

        val CHART_SERIES_TITLE_RUNNING = "Running"
        val CHART_SERIES_TITLE_WALKING = "Walking"
        val CHART_SERIES_TITLE_BIKING = "Biking"

        val CHART_SERIES_COLOR_RUNNING = Color.parseColor("#f6c342")
        val CHART_SERIES_COLOR_WALKING = Color.parseColor("#14892c")
        val CHART_SERIES_COLOR_BIKING = Color.parseColor("#d04437")

        val ACCEPTED_FITNESS_ACTIVITIES = arrayOf(FitnessActivity.RUNNING, FitnessActivity.ON_BICYCLE, FitnessActivity.WALKING)

        fun newInstance(): MyActivitiesFragment {
            val fragment = MyActivitiesFragment()

            return fragment
        }
    }
}