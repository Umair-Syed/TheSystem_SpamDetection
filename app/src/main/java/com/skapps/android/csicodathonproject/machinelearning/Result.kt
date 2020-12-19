package com.skapps.android.csicodathonproject.machinelearning

/** An immutable result returned by a TextClassifier describing what was classified.  */
class Result(
    /**
     * A unique identifier for what has been classified. Specific to the class, not the instance of
     * the object.
     */
    val id: String?,
    /** Display name for the result.  */
    val title: String?,
    /** A sortable score for how good the result is relative to others. Higher should be better.  */
    val confidence: Float?
) : Comparable<Result?> {

    override fun toString(): String {
        var resultString = ""
        if (id != null) {
            resultString += "[$id] "
        }
        if (title != null) {
            resultString += "$title "
        }
        if (confidence != null) {
            resultString += String.format("(%.1f%%) ", confidence * 100.0f)
        }
        return resultString.trim { it <= ' ' }
    }

    override fun compareTo(other: Result?): Int {
        return other?.confidence!!.compareTo(confidence!!)
    }
}