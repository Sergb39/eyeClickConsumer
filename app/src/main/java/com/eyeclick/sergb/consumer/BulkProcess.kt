package com.eyeclick.sergb.consumer

import android.util.Log

class BulkProcess {


    /**
     * excepting String and temp hashMap and populating it
     */
    fun processBulk(bulk: String, histogram: MutableMap<Char, Int>): MutableMap<Char, Int> {

        for (c in bulk) {
            histogram[c] = (histogram[c] ?: 0) + 1
        }

        return histogram
    }


    // string whitespace
    fun stripWhitespace(text: String): String {
        return text.replace("     ", "")
    }

    /**
     * calculating Histogram average
     */
    fun calculateHistogram(histogram: MutableMap<Char, Int>,size:Int): MutableMap<Char, Float>{
        val newHist: MutableMap<Char, Float> = HashMap()

        histogram.forEach { key: Char, value ->
            Log.d("YHY", ""+value + " /" + size )
            newHist[key] = value.toFloat() / size
        }
        return newHist
    }
}