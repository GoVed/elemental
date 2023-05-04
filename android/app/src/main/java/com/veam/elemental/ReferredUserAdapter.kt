package com.veam.elemental

import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class ReferredUserAdapter(val name: ArrayList<String>, val context: android.content.Context) : RecyclerView.Adapter<ViewHolder>() {

    lateinit var localSave: SharedPreferences
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        localSave =
            context!!.getSharedPreferences("elementalSave", android.content.Context.MODE_PRIVATE)
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.element_rv, parent, false),0)
    }

    override fun getItemCount(): Int {
        return name.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mainLayout.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.MATCH_PARENT)
        holder.elementNameTV.text=name[position]
        var shape = GradientDrawable()
        shape.cornerRadius=32F
        val random = Random()
        var r=random.nextInt(256)
        var g=random.nextInt(256)
        var b=random.nextInt(256)
        shape.setColor(Color.rgb(r,g,b))
        var tot = r+g+b
        if(tot<381)
            holder?.elementNameTV?.setTextColor(Color.WHITE)
        else
            holder?.elementNameTV?.setTextColor(Color.BLACK)
        holder.elementNameTV.background=shape
    }
}