package com.veam.elemental

import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.element_rv.view.*
import kotlinx.android.synthetic.main.element_rv_heading.view.*

class MyElementsAdapter(
    val items: ArrayList<String>,
    val colorval: ArrayList<String>,
    val clickListener: ElementClickListener,
    var animate: ArrayList<Boolean>,
    val mode: Int = 0,
    val isHeading: ArrayList<Int>,
    val context: android.content.Context
) : RecyclerView.Adapter<ViewHolder>() {

    val shape:MutableMap<Int, GradientDrawable> = mutableMapOf()
    val color:MutableMap<Int, Int> = mutableMapOf()

    val localSave: SharedPreferences=context!!.getSharedPreferences(
        "elementalSave",
        android.content.Context.MODE_PRIVATE
    )
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if(viewType==0)
            ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.element_rv, parent, false),
                viewType
            )
        else
            ViewHolder(
                LayoutInflater.from(context).inflate(
                    R.layout.element_rv_heading,
                    parent,
                    false
                ), viewType
            )
    }



    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(isHeading.size>position) isHeading[position] else 0
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if((mode==0&&!localSave.getBoolean("isCombineSectionVertical",false)&&localSave.getBoolean("displayOrientationPortrait",true))||mode==2){
            holder.mainLayout.layoutParams=LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        if(localSave.getBoolean("showElementScrollAnimation", true)) {
            var doAnim = false
            if (mode == 0 && animate[position])
                doAnim = true
            if (mode == 1 && (System.currentTimeMillis() - localSave.getLong(
                    "lastElementClick",
                    0
                )) > 300 && System.currentTimeMillis() - localSave.getLong(
                    "rv/elements/setScroll",
                    0
                ) < 200
            ) {
                doAnim = true
            }
            Log.d("checkcall", " called at $position")
            if (doAnim) {
                val handler = Handler()
                holder?.elementNameTV?.alpha = 0F
                val refreshRate = localSave.getFloat("refreshRate", 60F)
                val animSpeed = 2F
                Log.d("checkcallAnim", " called at $position")
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        holder?.elementNameTV?.alpha =
                            holder?.elementNameTV?.alpha?.plus(animSpeed / refreshRate)!!
                        if (holder?.elementNameTV?.alpha < 1)
                            handler.postDelayed(this, (1000 / refreshRate).toLong())
                    }
                }, (1000 / refreshRate).toLong())
            }
        }

        holder?.elementNameTV?.text = items[position]
        if(isHeading[position]==0) {
            holder.clickListener=clickListener
            if(shape[position]==null) {
                shape[position] = GradientDrawable()
                shape[position]!!.cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,16f,context.resources.displayMetrics)
                color[position] = Color.parseColor(colorval[position])
                shape[position]!!.setColor(color[position]!!)
            }

            var r = Color.red(color[position]!!)
            var g = Color.green(color[position]!!)
            var b = Color.blue(color[position]!!)
            var a = Color.alpha(color[position]!!)
            var tot = r + g + b
            if (tot < 381)
                holder?.elementNameTV?.setTextColor(Color.WHITE)
            else
                holder?.elementNameTV?.setTextColor(Color.BLACK)
            if (a < 100) {
                holder?.elementNameTV?.setTextColor(Color.BLACK)
            }


            holder?.elementNameTV?.background = shape[position]
        }
        else{
            if(localSave.getInt("elementRVColumnCount", 1)>1) {
                val layoutParams = holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams
                layoutParams.isFullSpan = true
            }
        }

    }


}
class ViewHolder(view: View, viewType: Int) : RecyclerView.ViewHolder(view) {
    var mainLayout:LinearLayout
    var elementNameTV:TextView
    lateinit var clickListener: ElementClickListener

    init {
        if(viewType==0) {
            mainLayout = view.mainElementLayout
            elementNameTV = view.newElementName
            elementNameTV.setOnClickListener() {
                clickListener.elementClicked(layoutPosition)
            }

            elementNameTV.setOnLongClickListener {
                clickListener.showInfo(layoutPosition)
                return@setOnLongClickListener true
            }
        }
        else{
            mainLayout = view.mainElementHeadingLayout
            elementNameTV = view.headingTitle
        }
    }

}


