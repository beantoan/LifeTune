package it.unical.mat.lifetune.fragment

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import it.unical.mat.lifetune.BuildConfig
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.activity.MainActivity
import it.unical.mat.lifetune.entity.FitnessActivity
import it.unical.mat.lifetune.entity.FitnessChartEntry
import kotlinx.android.synthetic.main.fragment_my_activities.*
import java.text.DateFormat
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
    private var fitnessHours: ArrayList<Float> = ArrayList()

    private var mainActivity: MainActivity? = null

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
    }


    private fun onResumeTasks() {
        Log.d(TAG, "onResumeTasks")

        readFitnessData()
    }

    private fun readFitnessData() {

        val dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM)

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2018)
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 10)

        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val startTime = cal.timeInMillis

        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val endTime = cal.timeInMillis

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

        Fitness.getHistoryClient(activity!!, account)
                .readData(readRequest)
                .addOnCompleteListener({ dataReadResponse ->
                    Log.d(TAG, "Fitness.getHistoryClient#addOnCompleteListener: buckets.size=${dataReadResponse.result.buckets.size}")

                    convertBucketsToBarChartData(dataReadResponse.result.buckets)
                })
                .addOnSuccessListener({
                    Log.d(TAG, "Fitness.getHistoryClient#addOnSuccessListener")

                    addLackedHoursToEntries(fitnessHours, runningData)
                    addLackedHoursToEntries(fitnessHours, walkingData)
                    addLackedHoursToEntries(fitnessHours, onBicycleData)

                    updateFitnessChart(steps_chart, FitnessChartEntry.TYPE_STEPS)
                    updateFitnessChart(distance_chart, FitnessChartEntry.TYPE_DISTANCE)
                    updateFitnessChart(calories_chart, FitnessChartEntry.TYPE_CALORIES)
                })
                .addOnFailureListener({
                    Log.d(TAG, "Fitness.getHistoryClient#addOnFailureListener")
                })
    }

    private fun convertBucketsToBarChartData(buckets: List<Bucket>) {
        runningData.clear()
        walkingData.clear()
        onBicycleData.clear()

        val acceptedActivities = arrayOf(FitnessActivity.RUNNING, FitnessActivity.ON_BICYCLE, FitnessActivity.WALKING)

        val dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM)
        val hourFormat = SimpleDateFormat("k")

        buckets.forEach { bucket ->
            val start = dateTimeFormat.format(bucket.getStartTime(TimeUnit.MILLISECONDS))
            val end = dateTimeFormat.format(bucket.getEndTime(TimeUnit.MILLISECONDS))

            val hour = hourFormat.format(bucket.getStartTime(TimeUnit.MILLISECONDS)).toFloat()


            Log.d(TAG, "-- activity=${bucket.activity}")

            if (bucket.activity in acceptedActivities) {

                if (!fitnessHours.contains(hour)) {
                    fitnessHours.add(hour)
                }
                
                bucket.dataSets.forEach { dataSet ->

                    dataSet.dataPoints.forEach { dataPoint ->
                        val fields = dataPoint.dataType.fields

                        val steps = if (fields.contains(Field.FIELD_STEPS)) dataPoint.getValue(Field.FIELD_STEPS).toString().toFloat() else 0f
                        val distance = if (fields.contains(Field.FIELD_DISTANCE)) dataPoint.getValue(Field.FIELD_DISTANCE).toString().toFloat() else 0f
                        val calories = if (fields.contains(Field.FIELD_CALORIES)) dataPoint.getValue(Field.FIELD_CALORIES).toString().toFloat() else 0f

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
    }

    private fun updateFitnessChart(chart: BarChart, type: Int) {
        val runningEntries = runningData.map { BarEntry(it.hour, it.getValueByType(type)) }
        val walkingEntries = walkingData.map { BarEntry(it.hour, it.getValueByType(type)) }
        val onBicycleEntries = onBicycleData.map { BarEntry(it.hour, it.getValueByType(type)) }
        
        Log.d(TAG, "runningEntries.size=${runningEntries.size}")
        Log.d(TAG, "walkingEntries.size=${onBicycleEntries.size}")
        Log.d(TAG, "walkingEntries.size=${walkingEntries.size}")

        val groupBarSize = fitnessHours.size

        val runningDataSet = BarDataSet(runningEntries, CHART_SERIES_TITLE_RUNNING)
        runningDataSet.color = CHART_SERIES_COLOR_RUNNING

        val onBicycleDataSet = BarDataSet(onBicycleEntries, CHART_SERIES_TITLE_BIKING)
        onBicycleDataSet.color = CHART_SERIES_COLOR_BIKING

        val walkingDataSet = BarDataSet(walkingEntries, CHART_SERIES_TITLE_WALKING)
        walkingDataSet.color = CHART_SERIES_COLOR_WALKING

        val chartData = BarData(walkingDataSet, runningDataSet, onBicycleDataSet)

        // https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/BarChartActivityMultiDataset.java#L180
        // (barWith + barSpace) * numberOfBars + groupSpace = 1
        val groupSpace = 0.02f
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

        chart.xAxis.setValueFormatter { value, axis ->
            if (value >= 0 && value < groupBarSize) {
                fitnessHours[value.toInt()].toInt().toString() + "h"
            } else {
                ""
            }
        }

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

        val FENCE_KEY = "MY_ACTIVITIES_FENCE_KEY"
        val FENCE_RECEIVER_ACTION = BuildConfig.APPLICATION_ID + ".FENCE_RECEIVER_ACTION"

        fun newInstance(): MyActivitiesFragment {
            val fragment = MyActivitiesFragment()

            return fragment
        }
    }
}