package com.skapps.android.csicodathonproject.machinelearning

import android.content.Context
import android.util.Log
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.text.nlclassifier.BertNLClassifier
import java.io.IOException


/**
 * Created by Syed Umair on 19/12/2020.
 */

private const val TAG = "TextClassificationClien"
const val MODEL_PATH = "model.tflite"
class TextClassificationClient(private val context: Context) {

    private var classifier: BertNLClassifier? = null

    fun load() {
        try {
            classifier = BertNLClassifier.createFromFile(context, MODEL_PATH)
        } catch (e: IOException) {
            Log.e(TAG, "${e.message}")
        }
    }

    fun unload() {
        classifier?.close()
        classifier = null
    }

    fun classify(text: String?): List<Result> {
        val apiResults: List<Category> = classifier!!.classify(text)
        val results: MutableList<Result> = ArrayList(apiResults.size)
        for (i in apiResults.indices) {
            val category: Category = apiResults[i]
            results.add(Result("" + i, category.label, category.score))
        }
        results.sort()
        return results
    }
}