package it.unical.mat.lifetune.fragment

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
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
class MyActivitiesFragment : Fragment() {

    private var runningData: ArrayList<FitnessChartEntry> = ArrayList()
    private var onBicycleData: ArrayList<FitnessChartEntry> = ArrayList()
    private var walkingData: ArrayList<FitnessChartEntry> = ArrayList()

    private var totalActiveTime = 0f
    private var totalSteps = 0f
    private var totalDistance = 0f
    private var totalCalories = 0f

    private val fitnessCalendar = Calendar.getInstance()

    private var fitnessHours: ArrayList<Float> = ArrayList()

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

        AppDialog.showProgress(R.string.progress_dialog_waiting_message, context!!)

        Fitness.getHistoryClient(activity!!, account)
                .readData(readRequest)
                .addOnCompleteListener({ dataReadResponse ->
                    Log.d(TAG, "Fitness.getHistoryClient#addOnCompleteListener: buckets.size=${dataReadResponse.result.buckets.size}")

                    convertBucketsToBarChartData(dataReadResponse.result.buckets)
                })
                .addOnSuccessListener({
                    Log.d(TAG, "Fitness.getHistoryClient#addOnSuccessListener")

                    isLoadingFitnessData = false

                    AppDialog.hideProgress(context!!)

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
                .addOnFailureListener({
                    Log.d(TAG, "Fitness.getHistoryClient#addOnFailureListener")

                    isLoadingFitnessData = false

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

            totalActiveTime += endMs - startMs

            val hour = hourFormat.format(startMs).toFloat()

            Log.d(TAG, "-- activity=${bucket.activity}")

            if (bucket.activity in ACCEPTED_FITNESS_ACTIVITIES) {

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
        hours.forEach { hour ->
            val fitnessChartEntry = FitnessChartEntry(hour)

            if (!entries.contains(fitnessChartEntry)) {
                entries.add(fitnessChartEntry)
            }
        }

        entries.sortBy { it.hour }
    }

    private fun updateSummaryInfo(_totalActiveTime: Float, _totalSteps: Float,
                                  _totalDistance: Float, _totalCalories: Float) {
        val activeTimeLong = _totalActiveTime.toLong()
        val activeTimeMinutes = TimeUnit.MILLISECONDS.toMinutes(activeTimeLong)
        val activeTimeSeconds = TimeUnit.MILLISECONDS.toSeconds(activeTimeLong) - TimeUnit.MINUTES.toSeconds(activeTimeMinutes)

        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())

        val distanceKm = Math.round(_totalDistance / 1000)
        val distanceM = (_totalDistance - distanceKm * 1000).toInt()

        val distanceKmStr = if (distanceKm > 0) "${distanceKm}km" else ""
        val distanceMStr = if (distanceM > 0) "${distanceM}m" else ""

        total_active_time.text = String.format("%dmin %dsec", activeTimeSeconds, activeTimeSeconds)

        total_steps.text = numberFormat.format(_totalSteps.toInt())
        total_distance.text = "$distanceKmStr $distanceMStr"
        total_calories.text = Math.round(_totalCalories).toString()
    }

    private fun getHoursAxisValueFormatter(_fitnessHours: List<Float>): IAxisValueFormatter {

        return IAxisValueFormatter { value, axis ->
            val valInt = value.toInt()
            val size = _fitnessHours.count()

            if (valInt in 0..(size - 1)) _fitnessHours[valInt].toInt().toString() + "h" else ""
        }
    }

    private fun updateFitnessChart(chart: BarChart, type: Int,
                                   _runningData: List<FitnessChartEntry>, _walkingData: List<FitnessChartEntry>,
                                   _onBicycleData: List<FitnessChartEntry>, _fitnessHours: List<Float>) {

        val runningEntries = _runningData.map { BarEntry(it.hour, it.getValueByType(type)) }
        val walkingEntries = _walkingData.map { BarEntry(it.hour, it.getValueByType(type)) }
        val onBicycleEntries = _onBicycleData.map { BarEntry(it.hour, it.getValueByType(type)) }

        val groupBarSize = _fitnessHours.size

        Log.d(TAG, "runningEntries.size=${runningEntries.size}, walkingEntries.size=${onBicycleEntries.size}, walkingEntries.size=${walkingEntries.size}")

        Log.d(TAG, "updateFitnessChart: groupBarSize=$groupBarSize, fitnessHours=$_fitnessHours")

        val runningDataSet = BarDataSet(runningEntries, CHART_SERIES_TITLE_RUNNING)
        runningDataSet.color = CHART_SERIES_COLOR_RUNNING

        val onBicycleDataSet = BarDataSet(onBicycleEntries, CHART_SERIES_TITLE_BIKING)
        onBicycleDataSet.color = CHART_SERIES_COLOR_BIKING

        val walkingDataSet = BarDataSet(walkingEntries, CHART_SERIES_TITLE_WALKING)
        walkingDataSet.color = CHART_SERIES_COLOR_WALKING

        val chartData = BarData(walkingDataSet, runningDataSet, onBicycleDataSet)

        // https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/BarChartActivityMultiDataset.java#L180
        // (barWith + barSpace) * numberOfBars + groupSpace = 1
        val groupSpace = 0.03f
        val barSpace = 0.01f
        val barWidth = 0.31f

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
        chart.xAxis.labelCount = groupBarSize
        chart.xAxis.setCenterAxisLabels(true)
        chart.xAxis.setDrawGridLines(true)
        chart.xAxis.granularity = 1f
        chart.xAxis.isGranularityEnabled = true
        chart.xAxis.axisMinimum = 0f
        chart.xAxis.axisMaximum = chart.barData.getGroupWidth(groupSpace, barSpace) * groupBarSize

        chart.rendererXAxis.paintAxisLabels.textAlign = Paint.Align.CENTER

        chart.groupBars(0f, groupSpace, barSpace)

        chart.legend.setDrawInside(false)

        chart.xAxis.valueFormatter = getHoursAxisValueFormatter(_fitnessHours)

        chart.invalidate()
    }

    companion object {
        val TAG = MyActivitiesFragment::class.java.simpleName

        val CHART_SERIES_TITLE_RUNNING = "Running"
        val CHART_SERIES_TITLE_WALKING = "Walking"
        val CHART_SERIES_TITLE_BIKING = "Biking"

        val CHART_SERIES_COLOR_RUNNING = Color.parseColor("#3572b0")
        val CHART_SERIES_COLOR_WALKING = Color.parseColor("#14892c")
        val CHART_SERIES_COLOR_BIKING = Color.parseColor("#d04437")

        val ACCEPTED_FITNESS_ACTIVITIES = arrayOf(FitnessActivity.RUNNING, FitnessActivity.ON_BICYCLE, FitnessActivity.WALKING)

        fun newInstance(): MyActivitiesFragment {
            val fragment = MyActivitiesFragment()

            return fragment
        }
    }
}