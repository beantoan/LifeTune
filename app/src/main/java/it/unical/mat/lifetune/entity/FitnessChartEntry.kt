package it.unical.mat.lifetune.entity

/**
 * Created by beantoan on 1/10/18.
 */
class FitnessChartEntry(var hour: Float,
                        var steps: Float = 0f,
                        var distance: Float = 0f,
                        var calories: Float = 0f) {

    fun addFitnessData(_steps: Float, _distance: Float, _calories: Float) {
        steps += _steps
        distance += _distance
        calories += _calories
    }

    fun getValueByType(type: Int): Float {
        return when (type) {
            TYPE_STEPS -> steps
            TYPE_DISTANCE -> distance
            TYPE_CALORIES -> calories
            else -> 0f
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is FitnessChartEntry) hour == other.hour else false
    }

    companion object {
        val TYPE_CALORIES = 1
        val TYPE_DISTANCE = 2
        val TYPE_STEPS = 3
    }
}