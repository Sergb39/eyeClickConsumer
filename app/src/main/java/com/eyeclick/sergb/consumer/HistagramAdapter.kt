package com.eyeclick.sergb.consumer

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class HistagramAdapter(dataset: MutableMap<Char, Float>?) :
        RecyclerView.Adapter<HistagramAdapter.ViewHolder>() {
    val myDataSet: ArrayList<hist>? = ArrayList()

    // convert Map to array with hist object
    init {
        dataset?.forEach { c: Char, f: Float -> myDataSet?.add(hist(c, f)) }
    }

    // set new data to Adapter
    fun setItems(dataset: MutableMap<Char, Float>?){
        // clear list
        myDataSet?.clear()
        // convert Map to array with hist object
        dataset?.forEach { c: Char, f: Float -> myDataSet?.add(hist(c, f)) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val charTV: TextView = view.findViewById(R.id.item_char)
        val avgTV: TextView = view.findViewById(R.id.item_avg)
    }

    // Create views
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): HistagramAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_row, parent, false) as View
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.charTV.text = myDataSet!![position].char + ""
        holder.avgTV.text = myDataSet!![position].avg.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataSet!!.size

    // object to help convert hashMap to List
    inner class hist(val char: Char, val avg: Float)
}