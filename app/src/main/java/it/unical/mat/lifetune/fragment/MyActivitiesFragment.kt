package it.unical.mat.lifetune.fragment

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.jjoe64.graphview.series.DataPoint
import it.unical.mat.lifetune.BuildConfig
import it.unical.mat.lifetune.R
import it.unical.mat.lifetune.activity.MainActivity
import it.unical.mat.lifetune.entity.FitnessActivity
import kotlinx.android.synthetic.main.fragment_my_activities.*
import org.apache.commons.lang3.tuple.MutablePair
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * Created by beantoan on 11/17/17.
 */
class MyActivitiesFragment : Fragment() {

    private var runningData: ArrayList<MutablePair<Float, Float>> = ArrayList()
    private var onFootData: ArrayList<MutablePair<Float, Float>> = ArrayList()
    private var onBicycleData: ArrayList<MutablePair<Float, Float>> = ArrayList()
    private var walkingData: ArrayList<MutablePair<Float, Float>> = ArrayList()
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val rootView = inflater.inflate(R.layout.fragment_my_activities, container, false)

        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()

        onStartTasks()
    }

    override fun onResume() {
        super.onResume()

        onResumeTasks()
    }

    override fun onPause() {
        super.onPause()

        onPauseTasks()
    }

    override fun onStop() {
        onStopTasks()

        super.onStop()
    }

    private fun onStartTasks() {
        Log.d(TAG, "onStartTasks")
    }

    private fun onResumeTasks() {
        Log.d(TAG, "onResumeTasks")

        readFitnessData()
    }

    private fun onPauseTasks() {
        Log.d(TAG, "onPauseTasks")

        Awareness.getFenceClient(activity).updateFences(FenceUpdateRequest.Builder()
                .removeFence(FENCE_KEY)
                .build())
                .addOnSuccessListener { Log.d(TAG, "Fence was successfully unregistered.") }
                .addOnFailureListener { e -> Log.e(TAG, "Fence could not be unregistered: ", e) }
    }

    private fun onStopTasks() {
        Log.d(TAG, "onStopTasks")


    }

    private fun readFitnessData() {

        val dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM)

        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 2018)
        cal.set(Calendar.MONTH, Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, 1)

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
                .aggregate(ds, DataType.AGGREGATE_STEP_COUNT_DELTA)
//                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
//                .bucketByActivityType(1, TimeUnit.MINUTES)
                .bucketByActivitySegment(1, TimeUnit.MINUTES)
//                .bucketBySession(1, TimeUnit.DAYS)
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

                    updateFitnessChart()
                })
                .addOnFailureListener({
                    Log.d(TAG, "Fitness.getHistoryClient#addOnFailureListener")
                })


//        val sessionRequest = SessionReadRequest.Builder()
//                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
////                .read(DataType.TYPE_STEP_COUNT_DELTA)
//                .read(ds)
//                .enableServerQueries()
//                .build()
//
//        Fitness.getSessionsClient(activity!!, account)
//                .readSession(sessionRequest)
//                .addOnCompleteListener({ sessionReadResponse ->
//                    Log.d(TAG, "Fitness.getSessionsClient#addOnCompleteListener: sessions.size=${sessionReadResponse.result.sessions.size}")
//
//                    sessionReadResponse.result.sessions.forEach {
//                        Log.d(TAG, it.toString())
//                    }
//                })
//                .addOnSuccessListener({ sessionReadResponse ->
//
//                })
//                .addOnFailureListener({
//                    Log.d(TAG, "Fitness.getSessionsClient#addOnFailureListener")
//
//                })
    }

    private fun convertBucketsToBarChartData(buckets: List<Bucket>) {
        runningData.clear()
        walkingData.clear()
        onFootData.clear()
        onBicycleData.clear()

        val acceptedActivities = arrayOf(FitnessActivity.RUNNING, FitnessActivity.ON_BICYCLE,
                FitnessActivity.ON_FOOT, FitnessActivity.WALKING)

        val dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM)
        val hourFormat = SimpleDateFormat("k")

        val hoursForXAxis = ArrayList<Float>()

        buckets.forEach { bucket ->
            val start = dateTimeFormat.format(bucket.getStartTime(TimeUnit.MILLISECONDS))
            val end = dateTimeFormat.format(bucket.getEndTime(TimeUnit.MILLISECONDS))

            val hour = hourFormat.format(bucket.getStartTime(TimeUnit.MILLISECONDS)).toFloat()

            if (!hoursForXAxis.contains(hour)) {
                hoursForXAxis.add(hour)
            }

            Log.d(TAG, "---- activity=${bucket.activity}")

            if (bucket.activity in acceptedActivities) {

                bucket.dataSets.forEach { dataSet ->

                    dataSet.dataPoints.forEach { dataPoint ->
                        dataPoint.dataType.fields.forEach { field ->
                            val value = dataPoint.getValue(field).toString().toFloat()

                            var existedHour = false

                            when (bucket.activity) {
                                FitnessActivity.RUNNING -> {
                                    for (runningDatum in runningData) {
                                        if (runningDatum.left == hour) {
                                            existedHour = true
                                            runningDatum.right += value
                                            break
                                        }
                                    }

                                    if (!existedHour) {
                                        runningData.add(MutablePair(hour, value))
                                    }
                                }
                                FitnessActivity.ON_BICYCLE -> {
                                    for (onBicycleDatum in onBicycleData) {
                                        if (onBicycleDatum.left == hour) {
                                            existedHour = true
                                            onBicycleDatum.right += value
                                            break
                                        }
                                    }

                                    if (!existedHour) {
                                        onBicycleData.add(MutablePair(hour, value))
                                    }
                                }
                                FitnessActivity.ON_FOOT -> {
                                    for (onFootDatum in onFootData) {
                                        if (onFootDatum.left == hour) {
                                            existedHour = true
                                            onFootDatum.right += value
                                            break
                                        }
                                    }

                                    if (!existedHour) {
                                        onFootData.add(MutablePair(hour, value))
                                    }
                                }
                                FitnessActivity.WALKING -> {

                                    for (walkingDatum in walkingData) {
                                        if (walkingDatum.left == hour) {
                                            existedHour = true
                                            walkingDatum.right += value
                                            break
                                        }
                                    }

                                    if (!existedHour) {
                                        walkingData.add(MutablePair(hour, value))
                                    }
                                }
                            }


                            Log.d(TAG, "---- bucket.activity=${bucket.activity} - dataPoint.field=${field.name} - dataPoint.value=$value - start=$start - end=$end - hour=$hour")
                        }
                    }
                }
            }
        }
    }

    private fun updateFitnessChart() {
        val runningEntries = ArrayList<BarEntry>()
        val walkingEntries = ArrayList<BarEntry>()
        val onBicycleEntries = ArrayList<BarEntry>()
        val onFootEntries = ArrayList<BarEntry>()

        val dataPoints = ArrayList<DataPoint>()
        var minX = 999f
        var maxX = 0f

        runningData.forEach {
            val et = BarEntry(it.left, it.right)

            runningEntries.add(et)
        }

        walkingData.forEach {
            val et = BarEntry(it.left, it.right)

            walkingEntries.add(et)

            dataPoints.add(DataPoint(it.left.toDouble(), it.right.toDouble()))

            if (minX > it.left) {
                minX = it.left
            }

            if (maxX < it.left) {
                maxX = it.left
            }
        }

        onBicycleData.forEach {
            val et = BarEntry(it.left, it.right)

            onBicycleEntries.add(et)
        }

        onFootData.forEach {
            val et = BarEntry(it.left, it.right)

            onFootEntries.add(et)
        }

        Log.d(TAG, "runningEntries.size=${runningEntries.size}")
        Log.d(TAG, "walkingEntries.size=${onBicycleEntries.size}")
        Log.d(TAG, "onFootEntries.size=${onFootEntries.size}")
        Log.d(TAG, "walkingEntries.size=${walkingEntries.size}")

        val runningSeries = BarDataSet(runningEntries, "Running")
        runningSeries.color = Color.RED

        val onBicycleSeries = BarDataSet(onBicycleEntries, "Bicycle")
        onBicycleSeries.color = Color.BLUE

        val onFootSeries = BarDataSet(onFootEntries, "On Foot")
        onFootSeries.color = Color.GREEN

        val walkingSeries = BarDataSet(walkingEntries, "Walking")
        walkingSeries.color = Color.YELLOW

        val chartData = BarData(runningSeries, onBicycleSeries, onFootSeries, walkingSeries)

        chart.clear()
        chart.data = chartData

        chart.setDrawValueAboveBar(true)
        chart.setPinchZoom(false)
        chart.setScaleEnabled(false)
        chart.setDrawBarShadow(false)
        chart.setDrawGridBackground(false)

        chart.axisRight.isEnabled = false

        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.labelCount = maxX.toInt()
        chart.xAxis.setCenterAxisLabels(false)
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.granularity = 1f
        chart.xAxis.isGranularityEnabled = true

        chart.rendererXAxis.paintAxisLabels.textAlign = Paint.Align.CENTER

        chart.description.isEnabled = false

        chart.legend.setDrawInside(false)

        chart.invalidate()
    }


    companion object {
        val TAG = MyActivitiesFragment::class.java.simpleName

        val FENCE_KEY = "MY_ACTIVITIES_FENCE_KEY"
        val FENCE_RECEIVER_ACTION = BuildConfig.APPLICATION_ID + ".FENCE_RECEIVER_ACTION"

        fun newInstance(): MyActivitiesFragment {
            val fragment = MyActivitiesFragment()

            return fragment
        }
    }
}