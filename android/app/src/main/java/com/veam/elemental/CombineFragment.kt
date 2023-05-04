package com.veam.elemental

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.TransitionManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.android.synthetic.main.element_info.*
import kotlinx.android.synthetic.main.fragment_combine.*
import kotlinx.android.synthetic.main.fragment_combine.view.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CombineFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CombineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class CombineFragment : Fragment(),ElementClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    lateinit var localSave:SharedPreferences
    lateinit var onLoadedListener:OnLoaded

    var user:String=""
    var totalElementCount=0
    var combineCS=false
    var infoOpen=false
    var settedRV=false
    var lockedElementsFrom=0
    var islocked:ArrayList<Boolean> = ArrayList()


    var selectedElementsID:ArrayList<Int> = ArrayList()
    var selectedElements:ArrayList<String> = ArrayList()
    var colorOfSelectedElements:ArrayList<String> = ArrayList()

    var newVoteElementID:ArrayList<Int> = ArrayList()
    var newVoteElementName:ArrayList<String> = ArrayList()
    var newVoteElementColor:ArrayList<String> = ArrayList()

    var elementsUnlockedID: ArrayList<String> = ArrayList()
    val elementsUnlocked: ArrayList<String> = ArrayList()
    val colorOfElement: ArrayList<String> = ArrayList()

    val searchElementID:ArrayList<String> = ArrayList()
    val searchElement:ArrayList<String> = ArrayList()
    val colorOfSearchElement:ArrayList<String> = ArrayList()

    val lockedElementID:ArrayList<String> = ArrayList()
    val lockedElement:ArrayList<String> = ArrayList()
    val colorOfLockedElement:ArrayList<String> = ArrayList()

    val pinElementID:ArrayList<String> = ArrayList()
    val pinElementName:ArrayList<String> = ArrayList()
    val pinElementColor:ArrayList<String> = ArrayList()

    val database = Firebase.database
    var isHeading:ArrayList<Int> = ArrayList()


    var elementTitles:ArrayList<String> = ArrayList()
    var combinationList:ArrayList<ArrayList<String>> = ArrayList()
    var combinationElementColor:ArrayList<String> = ArrayList()
    var combinationListColor:ArrayList<ArrayList<String>> = ArrayList()



    var loadHandler: Handler? = null
    var elementsLoaded=false
    var combinationLoaded=false
    var unlockedElementsLoaded=false


    lateinit var  attributes:AudioAttributes
    lateinit var soundPool: SoundPool
    var  soundElementClickedID =0
    var soundCombineFailID=0
    var soundElementInfoOpenID=0


    lateinit var rewardedAd:RewardedAd


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onLoadedListener=activity as OnLoaded
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release();
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view:View =inflater.inflate(R.layout.fragment_combine, container, false)


        localSave=activity!!.getSharedPreferences("elementalSave",Context.MODE_PRIVATE)
        user= FirebaseAuth.getInstance().currentUser!!.uid


        onLoadedListener.onLoadStart()

        //Setting sound pool
        attributes=AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        soundPool=SoundPool.Builder().setMaxStreams(5).setAudioAttributes(attributes).build()
        soundElementClickedID=soundPool.load(context!!,R.raw.elementclicked,1)
        soundCombineFailID=soundPool.load(context!!,R.raw.combine_fail,1)
        soundElementInfoOpenID=soundPool.load(context!!,R.raw.info_open,1)

        checkUserAndLoadView(view)


        return view
    }

    private fun checkUserAndLoadView(view: View){
        CustomServer.CheckAndCreateNewUser(user,object :ServerListener{
            override fun runWithValue(value: String) {
                setSync(view)
                setCoin(view)
                setGetCoin(view)
                setAddRandomElement(view)
                setRandomElementAnim(view)
                setHints(view)
                setElementSettings(view)
                setSuggestNew(view)
            }
        },context!!).execute()
    }




    private fun setSuggestNew(view: View){
        view.suggestNew.visibility= View.GONE
        view.closeSuggestNew.visibility = View.GONE
        view.closeSuggestNew.setOnClickListener {
            view.suggestNew.visibility= View.GONE
            view.closeSuggestNew.visibility = View.GONE
        }
        view.suggestNew.setOnClickListener {
            openCreateVote(view)
        }
    }

    private fun openCreateVote(view: View){
        val inflater:LayoutInflater = LayoutInflater.from(context)

        // Inflate a custom viewInner using layout inflater
        val viewInner = inflater.inflate(R.layout.create_vote,null)

        val popupWindow = PopupWindow(
            viewInner, // Custom viewInner to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )

        popupWindow.isFocusable = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 25.0F
        }



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Create a new slide animation for popup window enter transition
            val fade = android.transition.Fade()

            popupWindow.enterTransition = fade

            // Slide animation for popup window exit transition
            popupWindow.exitTransition = fade

        }

        val colorButton = viewInner.findViewById<TextView>(R.id.selectColor)
        val createVote = viewInner.findViewById<TextView>(R.id.createVoteButton)
        val newElementName = viewInner.findViewById<EditText>(R.id.newElementName)
        val newElementLayout = viewInner.findViewById<LinearLayout>(R.id.previewLayout)
        val newElementHexColor = viewInner.findViewById<EditText>(R.id.newElementHexValue)
        val openSettings = viewInner.findViewById<TextView>(R.id.newElementNameSettings)
        val toggleAutoCapital = viewInner.findViewById<TextView>(R.id.newElementNameAutoCapital)
        val madeUsing0 = viewInner.findViewById<TextView>(R.id.madeUsing1)
        val madeUsing1 = viewInner.findViewById<TextView>(R.id.madeUsing2)
        val madeUsing2 = viewInner.findViewById<TextView>(R.id.madeUsing3)
        val madeUsing3 = viewInner.findViewById<TextView>(R.id.madeUsing4)
        val suggestion1 = viewInner.findViewById<TextView>(R.id.suggestion1)
        val suggestion2 = viewInner.findViewById<TextView>(R.id.suggestion2)
        val suggestion3 = viewInner.findViewById<TextView>(R.id.suggestion3)
        var newElementColor= ""
        var createNewVoteCS=false
        var autoCapital=true
        var autoTurnOnAutoCapital=true
        var allElements:ArrayList<String> = ArrayList()
        var suggestionName:ArrayList<String> = ArrayList()
        var suggestionColor:ArrayList<String> = ArrayList()

        allElements.addAll(elementsUnlockedID)
        allElements.addAll(lockedElementID)
        allElements.sortBy { localSave.getString("elements/$it/name","").toString().length }

        openSettings.setOnClickListener {
            if(toggleAutoCapital.visibility==View.VISIBLE)
                toggleAutoCapital.visibility=View.GONE
            else
                toggleAutoCapital.visibility=View.VISIBLE
        }

        toggleAutoCapital.setOnClickListener {
            autoCapital=!autoCapital
            autoTurnOnAutoCapital = autoCapital
            lowLightButton(listOf(toggleAutoCapital),if(autoCapital) 1 else 0,if(autoCapital) 0 else 1)
        }

        newElementHexColor.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                newElementColor=updateNewElementColor(p0.toString(),colorButton,newElementLayout, newElementName,newElementHexColor,false)
            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        newElementName.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var text=newElementName.text.toString()
                if(text.length>0){
                    suggestionName.clear()
                    suggestionColor.clear()
                    if(autoTurnOnAutoCapital)
                        autoCapital=true
                    var i=0
                    while(i<allElements.size){
                        val comptext=localSave.getString("elements/${allElements[i]}/name","").toString()
                        if(comptext == text){
                            if(newElementColor==localSave.getString("elements/${allElements[i]}/color","").toString()) {
                                suggestionName.clear()
                                suggestionColor.clear()
                                autoCapital=false

                                break
                            }
                        }


                        if(comptext.contains(text,true)){
                            suggestionName.add(localSave.getString("elements/${allElements[i]}/name","").toString())
                            suggestionColor.add(localSave.getString("elements/${allElements[i]}/color","").toString())
                        }
                        if(suggestionName.size>=3)
                            break
                        i++
                    }
                    suggestion1.visibility=View.GONE
                    suggestion2.visibility=View.GONE
                    suggestion3.visibility=View.GONE
                    if(suggestionName.size>0){
                        suggestion1.visibility= VISIBLE
                        setBackgroundAndText(suggestion1,suggestionName[0],suggestionColor[0])
                    }
                    if(suggestionName.size>1){
                        suggestion2.visibility= VISIBLE
                        setBackgroundAndText(suggestion2,suggestionName[1],suggestionColor[1])
                    }
                    if(suggestionName.size>2){
                        suggestion3.visibility= VISIBLE
                        setBackgroundAndText(suggestion3,suggestionName[2],suggestionColor[2])
                    }
                }
                else{
                    suggestion1.visibility=View.GONE
                    suggestion2.visibility=View.GONE
                    suggestion3.visibility=View.GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })


        suggestion1.setOnClickListener {
            updateNewElementColor(suggestionColor[0],colorButton,newElementLayout, newElementName,newElementHexColor)
            newElementColor=suggestionColor[0]
            newElementName.setText(suggestionName[0])
        }
        suggestion2.setOnClickListener {
            updateNewElementColor(suggestionColor[1],colorButton,newElementLayout, newElementName,newElementHexColor)
            newElementColor=suggestionColor[1]
            newElementName.setText(suggestionName[1])
        }
        suggestion3.setOnClickListener {
            updateNewElementColor(suggestionColor[2],colorButton,newElementLayout, newElementName,newElementHexColor)
            newElementColor=suggestionColor[2]
            newElementName.setText(suggestionName[2])
        }

        

        setBackgroundAndText(madeUsing0,newVoteElementName[0],newVoteElementColor[0])
        setBackgroundAndText(madeUsing1,newVoteElementName[1],newVoteElementColor[1])

        if(newVoteElementID.size>2)
            setBackgroundAndText(madeUsing2,newVoteElementName[2],newVoteElementColor[2])
        else
            madeUsing2.visibility=View.GONE

        if(newVoteElementID.size>3)
            setBackgroundAndText(madeUsing3,newVoteElementName[3],newVoteElementColor[3])
        else
            madeUsing3.visibility=View.GONE
        
        colorButton.setOnClickListener {
            val colorPickerDialog = ColorPickerDialog.Builder(activity)

            if(newElementColor!="")
                colorPickerDialog.colorPickerView.setInitialColor(Color.parseColor(newElementColor))
            colorPickerDialog.setTitle("Pick Color")
                .setPreferenceName("ElementColorPicker")
                .setPositiveButton("OK",object: ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        newElementColor="#"+envelope!!.hexCode
                        updateNewElementColor(newElementColor,colorButton, newElementLayout, newElementName,newElementHexColor)
                    }
                })
            val bubbleFlag=BubbleFlag(activity)
            bubbleFlag.flagMode=FlagMode.FADE
            colorPickerDialog.colorPickerView.flagView=bubbleFlag


            colorPickerDialog.show()
        }

        createVote.setOnClickListener {
            if(!createNewVoteCS){
                createNewVoteCS=true
                if(newElementColor!="" && newElementName.text.isNotEmpty()) {
                    newVoteElementID.sort()
                    val id:ArrayList<String> = ArrayList()
                    newVoteElementID.forEach {
                        id.add(it.toString())
                    }
                    var combination = id.joinToString(",")

                    CustomServer.GetValue("combination2/$combination",object :ServerListener{
                        override fun runWithValue(value: String) {
                            var contains = false
                            if(value!="undefined")
                                contains=true
                            if(!contains){
                                CustomServer.GetValue("users/$user/bal",object :ServerListener{
                                    override fun runWithValue(value: String) {
                                        if(value.toLong()>=10) {
                                            val newProperName = if(autoCapital)
                                                newElementName.text.toString().trim().split(" ")
                                                    .joinToString(" ") { it.capitalize() }
                                            else
                                                newElementName.text.toString().trim()
                                            CustomServer.AddVote(newProperName,newElementColor,combination,user,context!!).execute()
                                            newElementName.setText("")
                                            Toast.makeText(activity, "Vote added", Toast.LENGTH_LONG).show()
                                            view.suggestNew.visibility= View.GONE
                                            view.closeSuggestNew.visibility = View.GONE
                                            popupWindow.dismiss()
                                        }
                                        else{
                                            Toast.makeText(activity, "Insufficient balance!", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },context!!).execute()

                            }
                            else{
                                Toast.makeText(activity, "Combination already exists", Toast.LENGTH_SHORT).show()
                            }
                            createNewVoteCS=false
                        }
                    },context!!).execute()
                }
                else {
                    Toast.makeText(activity, "Please enter the info to create a vote!", Toast.LENGTH_LONG).show()
                    createNewVoteCS=false
                }

            }

        }

        TransitionManager.beginDelayedTransition(combineFragment)
        popupWindow.showAtLocation(
            combineFragment, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            0 // Y offset
        )
    }

    private fun updateNewElementColor(newColor:String,colorButton:TextView,newElementLayout:LinearLayout,newElementName:EditText,newElementHexColor:EditText,updateHex:Boolean=true): String {
        var color=newColor
        if(!color.startsWith("#"))
            color="#$color"
        if(color.length==7)
            color = "#FF${color.substring(1)}"
        color=color.toUpperCase()
        if(Regex("^#([A-F0-9]{8})").matches(color)) {
            if(updateHex)
                newElementHexColor.setText(color)
            var shape = GradientDrawable()
            shape.setColor(Color.parseColor(color))
            shape.cornerRadius = 32F
            colorButton.background = shape
            newElementLayout.background = shape
            if (Color.red(Color.parseColor(color)) > 150 && Color.green(Color.parseColor(color)) > 150 && Color.blue(
                    Color.parseColor(color)
                ) > 150
            ) {
                colorButton.setTextColor(Color.parseColor("#202020"))
                newElementName.setTextColor(Color.parseColor("#202020"))
            } else {
                colorButton.setTextColor(Color.parseColor("#FFFFFF"))
                newElementName.setTextColor(Color.parseColor("#FFFFFF"))
            }
            return color
        }
        color="#00000000"
        if(updateHex)
            newElementHexColor.setText(color)
        var shape = GradientDrawable()
        shape.setColor(Color.parseColor(color))
        shape.cornerRadius = 32F
        colorButton.background = shape
        newElementLayout.background = shape
        if (Color.red(Color.parseColor(color)) > 150 && Color.green(Color.parseColor(color)) > 150 && Color.blue(
                Color.parseColor(color)
            ) > 150
        ) {
            colorButton.setTextColor(Color.parseColor("#202020"))
            newElementName.setTextColor(Color.parseColor("#202020"))
        } else {
            colorButton.setTextColor(Color.parseColor("#FFFFFF"))
            newElementName.setTextColor(Color.parseColor("#FFFFFF"))
        }
        return ""
    }

    private fun setBackgroundAndText(tv:TextView,text:String,color: String){
        tv.text=text
        var shape= GradientDrawable()
        shape.cornerRadius=32F
        shape.setColor(Color.parseColor(color))
        tv.background=shape
        if(Color.red(Color.parseColor(color))>150&&Color.green(Color.parseColor(color))>150&&Color.blue(Color.parseColor(color))>150)
            tv.setTextColor(Color.parseColor("#202020"))
        else
            tv.setTextColor(Color.parseColor("#FFFFFF"))
    }


    private fun setElementSettings(view: View){
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (view==null)
                    handler.postDelayed(this, 200)
                else{
                    view.openElementsSetting.setOnClickListener {
                        val inflater:LayoutInflater = LayoutInflater.from(context)

                        // Inflate a custom viewInner using layout inflater
                        val viewInner = inflater.inflate(R.layout.element_settings,null)

                        val popupWindow = PopupWindow(
                            viewInner, // Custom viewInner to show in popup window
                            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
                        )

                        popupWindow.isFocusable = true

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            popupWindow.elevation = 25.0F
                        }

                        viewInner.setOnTouchListener { v, event -> //Close the window when clicked
                            popupWindow.dismiss()
                            true
                        }

                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            // Create a new slide animation for popup window enter transition
                            val fade = android.transition.Fade()

                            popupWindow.enterTransition = fade

                            // Slide animation for popup window exit transition
                            popupWindow.exitTransition = fade

                        }

                        val showCategories = viewInner.findViewById<TextView>(R.id.showCategories)
                        val showLockedElements = viewInner.findViewById<TextView>(R.id.showLockedElements)
                        val splitLockedElements = viewInner.findViewById<TextView>(R.id.splitLockedElements)
                        val resetCombineElementsAfterCombination = viewInner.findViewById<TextView>(R.id.resetCombineElementsAfterSuccess)
                        val showElementScrollAnimation = viewInner.findViewById<TextView>(R.id.showElementScrollAnimation)
                        val show1Column = viewInner.findViewById<TextView>(R.id.columnCount1)
                        val show2Column = viewInner.findViewById<TextView>(R.id.columnCount2)
                        val show3Column = viewInner.findViewById<TextView>(R.id.columnCount3)
                        val show4Column = viewInner.findViewById<TextView>(R.id.columnCount4)
                        val columnButtons = listOf<TextView>(show1Column,show2Column,show3Column,show4Column)
                        val sortMode = viewInner.findViewById<TextView>(R.id.elementSortMode)
                        val sortType = viewInner.findViewById<TextView>(R.id.elementSortType)

                        setSortText(sortMode,sortType)
                        sortMode.setOnClickListener {
                            var current = localSave.getInt("sortMode",0)
                            current++
                            current%=4
                            localSave.edit().putInt("sortMode",current).apply()
                            setSortText(sortMode,sortType)
                            val temp = searchText.text.toString()
                            searchText.setText("$temp change it")
                            searchText.setText("$temp")
                        }
                        sortType.setOnClickListener {
                            var current = localSave.getInt("sortType",0)
                            current++
                            current%=2
                            localSave.edit().putInt("sortType",current).apply()
                            setSortText(sortMode,sortType)
                            val temp = searchText.text.toString()
                            searchText.setText("$temp change it")
                            searchText.setText("$temp")
                        }


                        if(localSave.getBoolean("showLockedElements",true)) {
                            splitLockedElements.visibility=View.VISIBLE
                        }
                        else{
                            splitLockedElements.visibility=View.GONE
                        }


                        showLockedElements.setOnClickListener {
                            if(localSave.getBoolean("showLockedElements",true)) {
                                lowLightButton(listOf(showLockedElements),0,1)
                                localSave.edit().putBoolean("showLockedElements", false).apply()
                                splitLockedElements.visibility=View.GONE
                                updateElements()
                            }
                            else{
                                lowLightButton(listOf(showLockedElements),1,0)
                                localSave.edit().putBoolean("showLockedElements", true).apply()
                                splitLockedElements.visibility=View.VISIBLE
                                updateElements()
                            }
                        }

                        resetCombineElementsAfterCombination.setOnClickListener {
                            if(localSave.getBoolean("resetCombineElementsAfterCombination",true)) {
                                lowLightButton(listOf(resetCombineElementsAfterCombination),0,1)
                                localSave.edit().putBoolean("resetCombineElementsAfterCombination", false).apply()
                            }
                            else{
                                lowLightButton(listOf(resetCombineElementsAfterCombination),1,0)
                                localSave.edit().putBoolean("resetCombineElementsAfterCombination", true).apply()
                            }
                        }

                        splitLockedElements.setOnClickListener {
                            if(localSave.getBoolean("splitLockedElements",true)) {
                                lowLightButton(listOf(splitLockedElements),0,1)
                                localSave.edit().putBoolean("splitLockedElements", false).apply()
                                updateElements()
                            }
                            else{
                                lowLightButton(listOf(splitLockedElements),1,0)
                                localSave.edit().putBoolean("splitLockedElements", true).apply()
                                updateElements()
                            }
                        }

                        showElementScrollAnimation.setOnClickListener {
                            if(localSave.getBoolean("showElementScrollAnimation",true)) {
                                lowLightButton(listOf(showElementScrollAnimation),0,1)
                                localSave.edit().putBoolean("showElementScrollAnimation", false).apply()
                                updateElements()
                            }
                            else{
                                lowLightButton(listOf(showElementScrollAnimation),1,0)
                                localSave.edit().putBoolean("showElementScrollAnimation", true).apply()
                                updateElements()
                            }
                        }

                        showCategories.setOnClickListener {
                            if(localSave.getBoolean("showCategories",false)) {
                                lowLightButton(listOf(showCategories),0,1)
                                localSave.edit().putBoolean("showCategories", false).apply()
                                updateElements()
                            }
                            else{
                                lowLightButton(listOf(showCategories),1,0)
                                localSave.edit().putBoolean("showCategories", true).apply()
                                updateElements()
                            }
                        }
                        lowLightButton(listOf(showCategories),if(localSave.getBoolean("showCategories",false)) 1 else 0)
                        lowLightButton(listOf(showLockedElements),if(localSave.getBoolean("showLockedElements",true)) 1 else 0)
                        lowLightButton(listOf(splitLockedElements),if(localSave.getBoolean("splitLockedElements",true)) 1 else 0)
                        lowLightButton(listOf(showElementScrollAnimation),if(localSave.getBoolean("showElementScrollAnimation",true)) 1 else 0)
                        lowLightButton(listOf(resetCombineElementsAfterCombination),if(localSave.getBoolean("resetCombineElementsAfterCombination",true)) 1 else 0)

                        lowLightButton(columnButtons,localSave.getInt("elementRVColumnCount",1))
                        show1Column.setOnClickListener {
                            lowLightButton(columnButtons,1,localSave.getInt("elementRVColumnCount",1))
                            localSave.edit().putInt("elementRVColumnCount", 1).apply()
                            updateElements()
                        }
                        show2Column.setOnClickListener {
                            lowLightButton(columnButtons,2,localSave.getInt("elementRVColumnCount",1))
                            localSave.edit().putInt("elementRVColumnCount", 2).apply()
                            updateElements()
                        }
                        show3Column.setOnClickListener {
                            lowLightButton(columnButtons,3,localSave.getInt("elementRVColumnCount",1))
                            localSave.edit().putInt("elementRVColumnCount", 3).apply()
                            updateElements()
                        }
                        show4Column.setOnClickListener {
                            lowLightButton(columnButtons,4,localSave.getInt("elementRVColumnCount",1))
                            localSave.edit().putInt("elementRVColumnCount", 4).apply()
                            updateElements()
                        }

                        TransitionManager.beginDelayedTransition(combineFragment)
                        popupWindow.showAtLocation(
                            combineFragment, // Location to display popup window
                            Gravity.CENTER, // Exact position of layout to display popup
                            0, // X offset
                            0 // Y offset
                        )
                    }
                }
            }
        }, 200)
    }

    fun setSortText(sortMode:TextView,sortType:TextView){
        if(localSave.getInt("sortMode",0)==0){
            sortMode.text = "Last Unlocked"
        }
        if(localSave.getInt("sortMode",0)==1){
            sortMode.text = "Alphabetical"
        }
        if(localSave.getInt("sortMode",0)==2){
            sortMode.text = "ID number"
        }
        if(localSave.getInt("sortMode",0)==3){
            sortMode.text = "Comments"
        }

        if(localSave.getInt("sortType",0)==0){
            sortType.text = "↓"
        }
        if(localSave.getInt("sortType",0)==1){
            sortType.text = "↑"
        }
    }


    fun lowLightButton(buttons:List<TextView>,selected:Int,previousSelected:Int=-1){
        val handler = Handler()
        var alpha=1F
        val refreshRate = localSave.getFloat("refreshRate", 60F)
        val animSpeed=2F
        var i=0
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (alpha>0.5) {
                    i=0
                    buttons.forEach {
                        if(i==selected-1)
                            it.alpha=0.5F+(1-alpha)
                        else if(i==previousSelected-1)
                            it.alpha=alpha
                        else
                            it.alpha=0.5F
                        i++
                    }

                    alpha-=animSpeed/refreshRate
                    handler.postDelayed(this, (1000 / refreshRate).toLong())
                }
                else{
                    i=0
                    buttons.forEach {
                        if(i==selected-1)
                            it.alpha=1F
                        else
                            it.alpha=0.5F
                        i++
                    }

                }
            }
        }, (1000 / refreshRate).toLong())
    }



    private fun setLockedElements(){
        var i:Long=0
        lockedElementID.clear()
        lockedElement.clear()
        colorOfLockedElement.clear()
        val unlockedElements = localSave.getString("unlockedElements","").toString().split(",")
        while(i<localSave.getLong("totalCombinations",0)){
            val id=localSave.getString("combinations/$i/makes","").toString()
            if(!unlockedElements.contains(id)){
                val name=localSave.getString("elements/$id/name","Failed to get name $i").toString()
                if(!lockedElement.contains(name)&&!elementsUnlocked.contains(name)) {
                    lockedElementID.add(id)
                    lockedElement.add(name)
                    colorOfLockedElement.add("#55555555")
                }
            }
            i++
        }
    }



    fun setSync(view: View){
        var handler=object :Handler(){
            override fun handleMessage(msg: Message) {
                onLoadedListener.onLoaded()
            }
        }
        loadHandler=handler
        sync(view)
    }
    
    private fun sync(view: View){
        localSave.edit().putBoolean("isSyncing",true).apply()
        localSave.edit().putLong("lastSync",System.currentTimeMillis()).apply()
        setTotalCount()
        initSync(view)

        setCategories()
        setNotificationID()
    }

    private fun initSync(view: View){
        CustomServer.InitSync(user,localSave.getLong("totalElements", 0).toString(), "0",localSave.getLong("totalCombinations", 0).toString(),"0",object : ServerListener{
            override fun runWithValue(value: String) {

                try {

                    val data = JSONObject(value)

                    with(localSave.edit()) {
                        data.getJSONObject("elements").keys().forEach {
                            putString(
                                "elements/${it}/name",
                                data.getJSONObject("elements").getJSONObject("$it").getString("name")
                            )
                            putString(
                                "elements/${it}/color",
                                data.getJSONObject("elements").getJSONObject("$it").getString("color")
                            )
                            putString(
                                "elements/${it}/by",
                                data.getJSONObject("elements").getJSONObject("$it").getString("by")
                            )
                            putString(
                                "elements/${it}/time",
                                data.getJSONObject("elements").getJSONObject("$it").getString("time")
                            )
                        }
                        elementsLoaded = true
                        data.getJSONObject("combinations").keys().forEach {
                            putString(
                                "combinations/${it}/makes",
                                data.getJSONObject("combinations").getJSONObject("$it").getString("makes")
                            )
                            putString(
                                "combinations/${it}/from",
                                data.getJSONObject("combinations").getJSONObject("$it").getString("from")
                            )
                        }
                        combinationLoaded = true
                        data.getJSONObject("comments").getJSONObject("count").keys().forEach {
                            putInt("elements/$it/comments/count",data.getJSONObject("comments").getJSONObject("count").getInt(it))
                        }

                        var keys:ArrayList<String> = ArrayList()
                        data.keys().forEach {
                            keys.add(it)
                        }
                        if(keys.contains("username"))
                            putString("username",data.getString("username")).apply()
                        if(keys.contains("refer"))
                            putString("refer",data.getString("refer")).apply()
                        if(keys.contains("status"))
                            putString("status",data.getString("status")).apply()
                        if(keys.contains("mods")){
                            data.getJSONObject("mods").keys().forEach {
                                putString("mods/$it",data.getJSONObject("mods").getString(it)).apply()
                            }
                        }


                        data.getJSONObject("price").keys().forEach {
                            Log.d("checkit","hmm $it")
                            data.getJSONObject("price").getString(it).toIntOrNull()?.let { it1 ->
                                putInt("price/$it", it1)
                            }
                        }
                        apply()
                    }

                    if(data.getString("unlocked").split(",").size>=4) {
                        localSave.edit().putString("unlockedElements", data.getString("unlocked")).apply()
                        unlockedElementsLoaded=true
                        setTotalCount()
                        checkLoadComplete()
                        setElementsInView(view)
                        setCombineSectionMode()
                    }
                    else {
                        CustomServer.CheckAndCreateNewUser(user,object :ServerListener{
                            override fun runWithValue(value: String) {
                                unlockedElementSync(view)
                            }
                        },context!!).execute()

                    }
                } catch (e: Exception) {
                    Log.e("errorOnSyncingElements", e.stackTrace.joinToString("\n"))
                }
            }
        },context!!).execute()
    }

    private fun checkLoadComplete(){
        loadHandler?.let {
            if(elementsLoaded&&combinationLoaded&&unlockedElementsLoaded)
                broadcastCompleteSync(it)
        }
    }
    private fun broadcastCompleteSync(handler: Handler){
        localSave.edit().putBoolean("isSyncing",false).apply()
        var message = Message.obtain()
        var b=Bundle()
        b.putString("sync","complete")
        message.data=b
        handler.sendMessage(message)
    }
    private fun setNotificationID(){
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener {
            if (!it.isSuccessful) {
                return@OnCompleteListener
            }

            val token = it.result?.token

            if (token != null && context!=null) {
                CustomServer.SetFCMToken(user,token,context!!).execute()
            }
        })

    }



    private fun setCategories(){

        if(localSave.getBoolean("categoryFirstSynced",false)) {
            CustomServer.GetCategorizedElements(user, object : ServerListener {
                override fun runWithValue(value: String) {
                    val elements = value.split(",")
                    elements.forEach {
                        if (localSave.getString("category/$it", "") == "") {
                            val staticID = it
                            if (context != null){
                                CustomServer.GetCategory(user, staticID, object : ServerListener {
                                    override fun runWithValue(value: String) {
                                        localSave.edit().putString("category/$staticID", value)
                                            .apply()
                                        value.split(",").forEach { it2 ->
                                            if (it2 != "") {
                                                val uniqueCatStr =
                                                    localSave.getString("uniqueCategories", "")
                                                        .toString()
                                                if (uniqueCatStr != "") {
                                                    val uniqueCat: ArrayList<String> = ArrayList()
                                                    uniqueCat.addAll(uniqueCatStr.split(","))
                                                    if (!uniqueCat.contains(it2)) {
                                                        uniqueCat.add(it2)
                                                        localSave.edit().putString(
                                                            "uniqueCategories",
                                                            uniqueCat.joinToString(",")
                                                        ).apply()
                                                    }
                                                }

                                                val currselStr = localSave.getString(
                                                    "categoryToElement/$it2",
                                                    ""
                                                ).toString()
                                                val currSelection: ArrayList<String> = ArrayList()
                                                if (currselStr != "") {
                                                    currSelection.addAll(currselStr.split(","))
                                                }
                                                if (currSelection.contains(staticID)) {
                                                    currSelection.add(staticID)
                                                    localSave.edit().putString(
                                                        "categoryToElement/$it2",
                                                        currSelection.joinToString(",")
                                                    ).apply()
                                                }
                                            }
                                        }
                                    }
                                }, context!!).execute()
                            }
                        }
                    }
                }
            }, context!!).execute()
        }
        else{
            CustomServer.GetAllCategory(user,object :ServerListener{
                override fun runWithValue(value: String) {
                    try{
                        val data=JSONObject(value)
                        var uniquecats: ArrayList<String> = ArrayList()
                        data.keys().forEach {
                            localSave.edit().putString("category/$it",data.getString(it)).apply()
                            data.getString(it).split(",").forEach {it2 ->

                                if(!uniquecats.contains(it2)){
                                    uniquecats.add(it2)
                                }
                                val currSelStr=localSave.getString("categoryToElement/${it2}","").toString()
                                val currSelection: ArrayList<String> = ArrayList()
                                if(currSelStr!=""){
                                    currSelection.addAll(currSelStr.split(","))
                                }
                                if(!currSelection.contains(it)){
                                    currSelection.add(it)
                                    localSave.edit().putString("categoryToElement/$it2",currSelection.joinToString(",")).apply()
                                }
                            }
                        }

                        localSave.edit().putString("uniqueCategories",uniquecats.joinToString(",")).apply()
                        localSave.edit().putBoolean("categoryFirstSynced",true).apply()
                    }catch (e:Exception){
                        Log.e("Error Combination Sync","${e.stackTrace}")
                    }
                }
            },context!!).execute()
        }
    }
    private fun setTotalCount(){
        var i:Long=0
        while(localSave.getString("elements/$i/by","").toString()!="" && localSave.getString("elements/$i/by","").toString()!="null"){
            i++
        }

        localSave.edit().putLong("totalElements",i).apply()

        i=0
        while(localSave.getString("combinations/$i/from","").toString()!=""&&localSave.getString("combinations/$i/from","").toString()!="null"){
            i++
        }

        localSave.edit().putLong("totalCombinations",i).apply()
    }


    private fun setElementSync(view: View){
        if(context!=null) {
            CustomServer.GetElements(
                localSave.getLong("totalElements", 0),
                0,
                object : ServerListener {
                    override fun runWithValue(value: String) {
                        var i = localSave.getLong("totalElements", 0).toInt()
                        var j = 0
                        try {
                            val data = JSONObject(value)

                            with(localSave.edit()) {
                                while (j < data.length()) {
                                    putString(
                                        "elements/${i}/name",
                                        data.getJSONObject("$i").getString("name")
                                    )
                                    putString(
                                        "elements/${i}/color",
                                        data.getJSONObject("$i").getString("color")
                                    )
                                    putString(
                                        "elements/${i}/by",
                                        data.getJSONObject("$i").getString("by")
                                    )
                                    putString(
                                        "elements/${i}/time",
                                        data.getJSONObject("$i").getString("time")
                                    )
                                    i++
                                    j++
                                }
                                commit()
                            }
                            with(localSave.edit()) {
                                putLong("totalElements", i.toLong())
                                commit()
                            }

                        } catch (e: Exception) {
                            Log.e("errorOnSyncingElements", e.stackTrace.joinToString("\n"))
                        }
                    }
                },
                context!!
            ).execute()
        }
    }

    private fun setCombinationSync(view: View){
        if(context!=null) {
            CustomServer.GetCombinations(
                localSave.getLong("totalCombinations", 0),
                0,
                object : ServerListener {
                    override fun runWithValue(value: String) {
                        var i = localSave.getLong("totalCombinations", 0).toInt()
                        var j = 0
                        try {
                            val data = JSONObject(value)
                            with(localSave.edit()) {
                                while (j < data.length()) {
                                    putString(
                                        "combinations/${i}/makes",
                                        data.getJSONObject("$i").getString("makes")
                                    )
                                    putString(
                                        "combinations/${i}/from",
                                        data.getJSONObject("$i").getString("from")
                                    )
                                    i++
                                    j++
                                }
                                commit()
                            }
                            with(localSave.edit()) {
                                putLong("totalCombinations", i.toLong())
                                commit()
                            }
                            combinationLoaded = true
                            checkLoadComplete()
                        } catch (e: Exception) {
                            Log.e("errorOnSyncingElements", e.stackTrace.joinToString("\n"))
                        }
                    }
                },
                context!!
            ).execute()
        }
    }



    private fun unlockedElementSync(view: View){

        CustomServer.GetValue("users/$user/elements",object :ServerListener{
            override fun runWithValue(value: String) {
                if(value.split(",").size>=4) {
                    localSave.edit().putString("unlockedElements", value).apply()
                    unlockedElementsLoaded=true
                    checkLoadComplete()
                    setElementsInView(view)
                }
                else {
                    CustomServer.CheckAndCreateNewUser(user,object :ServerListener{
                        override fun runWithValue(value: String) {
                            unlockedElementSync(view)
                        }
                    },context!!).execute()

                }
            }
        },context!!).execute()
    }

    interface OnLoaded{
        fun onLoadStart(){}
        fun onLoaded(){}
    }

    override fun onResume() {
        try{
            if(System.currentTimeMillis()-localSave.getLong("lastAdAdded",0)>1080000){
                localSave.edit().putLong("lastAdAdded",System.currentTimeMillis()).apply()
                localSave.edit().putInt("availableAd",3).apply()
            }
            CustomServer.SetOnline(user,context!!).execute()
            view?.let { setCoin(it) }
        }
        catch(e:Exception){
            Log.e("errorOnResume","Bal not updated, ${e.message}")
        }
        super.onResume()
    }


    private fun setHints(rootView: View){
        rootView.openHints.setOnClickListener {
            val inflater:LayoutInflater = LayoutInflater.from(context)

            // Inflate a custom view using layout inflater
            val view = inflater.inflate(R.layout.combination_hint,null)

            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                LinearLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            popupWindow.isFocusable = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.elevation = 25.0F
            }

            view.setOnTouchListener { v, event -> //Close the window when clicked
                popupWindow.dismiss()
                true
            }

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                // Create a new slide animation for popup window enter transition
                val fade = android.transition.Fade()

                popupWindow.enterTransition = fade

                // Slide animation for popup window exit transition
                popupWindow.exitTransition = fade

            }

            val unlockNewElementButton=view.findViewById<TextView>(R.id.unlockNewElement)
            val getSingleElementButton=view.findViewById<TextView>(R.id.unlockSingleElement)

            unlockNewElementButton.text = "Whole Random Element [-${localSave.getInt("price/randomElementHint",0)} \uD83D\uDCB5]"
            getSingleElementButton.text = "Single element for the combination [-${localSave.getInt("price/singleElementHint", 0)} \uD83D\uDCB5]"

            unlockNewElementButton.setOnClickListener {
                var tempi=0
                val combinationFrom:ArrayList<ArrayList<String>> = ArrayList()
                val combinationMakes:ArrayList<String> = ArrayList()
                while(localSave.getString("combinations/$tempi/makes","")!=""&&localSave.getString("combinations/$tempi/makes","")!="null"){
                    if(!elementsUnlockedID.contains(localSave.getString("combinations/$tempi/makes",""))){
                        var contains=true
                        val from=ArrayList(localSave.getString("combinations/$tempi/from","").toString().split(","))
                        from.forEach {
                            if(!elementsUnlockedID.contains(it))
                                contains=false
                        }
                        if(contains) {
                            combinationMakes.add(localSave.getString("combinations/$tempi/makes", "").toString())
                            combinationFrom.add(from)
                        }
                    }
                    tempi++
                }
                if(combinationFrom.size>0) {
                    tempi = (Math.random() * (combinationFrom.size - 1)).toInt()
                    selectedElementsID.clear()
                    combinationFrom[tempi].forEach {
                        selectedElementsID.add(it.toInt())
                    }

                    CustomServer.GetValue("users/$user/bal",object :ServerListener{
                        override fun runWithValue(value: String) {
                            if(value.toLong()>localSave.getInt("price/randomElementHint",0)) {
                                CustomServer.UpdateBal(user,"${value.toLong()-localSave.getInt("price/randomElementHint",0)}",context!!).execute()
                                CustomServer.AddLog4(user,"-${localSave.getInt("price/randomElementHint",0)}",selectedElements.joinToString (" + " ),context!!).execute()
                                setCoin(view)
                            }
                            else {
                                selectedElementsID.clear()
                                Toast.makeText(activity, "Insufficient balance!", Toast.LENGTH_SHORT).show()
                            }
                            updateElements()
                        }
                    },context!!).execute()

                    popupWindow.dismiss()
                }
            }

            getSingleElementButton.setOnClickListener {
                var tempi=0
                val combinationFrom:ArrayList<ArrayList<String>> = ArrayList()
                val combinationMakes:ArrayList<String> = ArrayList()
                while(localSave.getString("combinations/$tempi/makes","")!=""&&localSave.getString("combinations/$tempi/makes","")!="null"){
                    if(!elementsUnlockedID.contains(localSave.getString("combinations/$tempi/makes",""))){
                        var contains=true
                        var inSelected=true
                        val from=ArrayList(localSave.getString("combinations/$tempi/from","").toString().split(","))
                        from.forEach {
                            if(!elementsUnlockedID.contains(it))
                                contains=false
                        }

                        selectedElementsID.forEach {
                            if(!from.contains(it.toString()))
                                inSelected=false
                        }


                        if(contains&&inSelected) {
                            combinationMakes.add(localSave.getString("combinations/$tempi/makes", "").toString())
                            combinationFrom.add(from)
                        }
                    }
                    tempi++
                }
                if(combinationFrom.size>0) {
                    tempi = (Math.random() * (combinationFrom.size - 1)).toInt()

                    if(combinationFrom[tempi].size==selectedElementsID.size){
                        Toast.makeText(activity, "Combine the current selected items!", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val recipe:ArrayList<Int> = ArrayList()
                        val selElm:ArrayList<Int> = ArrayList()
                        combinationFrom[tempi].forEach {
                            it.toIntOrNull()?.let { it1 -> recipe.add(it1) }
                        }
                        selElm.addAll(selectedElementsID)
                        selElm.sort()
                        recipe.sort()

                        selElm.forEach {
                            recipe.remove(it)
                        }
                        Log.d("checkr","hmm   "+recipe.joinToString(","))
                        selectedElementsID.add(recipe[0])

                        CustomServer.GetValue("users/$user/bal",object :ServerListener{
                            override fun runWithValue(value: String) {
                                if(value.toLong()>localSave.getInt("price/singleElementHint",0)) {
                                    updateElements()
                                    CustomServer.UpdateBal(user,"${value.toLong()-localSave.getInt("price/singleElementHint",0)}",context!!).execute()
                                    if(combinationFrom[tempi].size==selectedElementsID.size)
                                        CustomServer.AddLog4(user,"-${localSave.getInt("price/singleElementHint",0)}",selectedElements.joinToString (" + " ),context!!).execute()
                                    else
                                        CustomServer.AddLog4(user,"-${localSave.getInt("price/singleElementHint",0)}",selectedElements.joinToString (" + " )+" + Something...",context!!).execute()
                                    setCoin(view)
                                }
                                else {
                                    selectedElementsID.clear()
                                    Toast.makeText(activity, "Insufficient balance!", Toast.LENGTH_SHORT).show()
                                }

                            }
                        },context!!).execute()
                    }
                    popupWindow.dismiss()
                }
                else{
                    Toast.makeText(activity, "No combination exist with selected items, try another combination", Toast.LENGTH_SHORT).show()
                }
            }


            TransitionManager.beginDelayedTransition(combineFragment)
            popupWindow.showAtLocation(
                combineFragment, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
            )
        }
    }

    private fun setRandomElementAnim(view: View){
        val handler=Handler()
        handler.postDelayed(object:Runnable{
            override fun run() {
                view.addRandomElement.text= arrayListOf<String>("⚀","⚁","⚂","⚃","⚄","⚅").random()
                view.addRandomElement.setTextColor(Color.rgb((0..255).random(),(0..255).random(),(0..255).random()))
                handler.postDelayed(this,1000)
            }
        },1000)

    }

    private fun setAddRandomElement(view: View){
        view.addRandomElement.setOnClickListener {
            if (selectedElementsID.size < 4) {
                val random = elementsUnlockedID.random()
                if(random.toIntOrNull()!=null)
                    selectedElementsID.add(random.toInt())
            } else
                Toast.makeText(
                    activity,
                    "Maximum 4 elements are allowed!",
                    Toast.LENGTH_SHORT
                ).show()
            updateElements()
        }
    }

    private fun setElementsInView(view: View){
        
        view.yourElementsHeading.text = "Your Elements (" + localSave.getString("unlockedElements","0,1,2,3")!!.split(",").size+"/${localSave.getString("unlockedElements","0,1,2,3")!!.split(",").size+lockedElementID.size})"
        setLockedElements()
        getElementsID()
        setCombineButton(view)
        setSearch(view)
    }





    







    private fun setGetCoin(view: View){
        loadRewardedAd()

        view.balance.setOnClickListener {
            openGetCoin(view)
        }
    }

    private fun loadRewardedAd(){
        MobileAds.initialize(context!!)
        val adRequest = AdRequest.Builder().build()
        //My ID
        rewardedAd = RewardedAd(context, "ca-app-pub-5197879139590015/8447837326")
        //Test ID
//        rewardedAd = RewardedAd(context, "ca-app-pub-3940256099942544/5224354917")
//        val testDeviceIds = Arrays.asList("127AF145B25E37690D219CC49947CA86")
//        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
//        MobileAds.setRequestConfiguration(configuration)
//        adRequest.isTestDevice(context!!)
        val adLoadCallback = object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                // Ad successfully loaded.
            }
            override fun onRewardedAdFailedToLoad(errorCode: Int) {

            }
        }
        rewardedAd.loadAd(adRequest, adLoadCallback)
    }

    private fun openGetCoin(view: View){
        val inflater:LayoutInflater = LayoutInflater.from(context)

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.get_coins,null)

        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )

        popupWindow.isFocusable = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 25.0F
        }

        view.setOnTouchListener { v, event -> //Close the window when clicked
            popupWindow.dismiss()
            true
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Create a new slide animation for popup window enter transition
            val fade = android.transition.Fade()

            popupWindow.enterTransition = fade

            // Slide animation for popup window exit transition
            popupWindow.exitTransition = fade

        }

        val watchAdBtn = view.findViewById<TextView>(R.id.watchAd)
        watchAdBtn.setOnClickListener {
            if(localSave.getInt("availableAd",0)>0) {
                if (rewardedAd.isLoaded) {
                    val activityContext = activity
                    val adCallback = object : RewardedAdCallback() {
                        override fun onRewardedAdOpened() {

                        }

                        override fun onRewardedAdClosed() {
                            loadRewardedAd()
                        }

                        override fun onUserEarnedReward(p0: RewardItem) {
                            localSave.edit().putInt("availableAd",localSave.getInt("availableAd",0)-1).apply()
                            CustomServer.GetValue("users/$user/bal", object : ServerListener {
                                override fun runWithValue(value: String) {
                                    CustomServer.UpdateBal(user, "${value.toLong() + 5}", context!!)
                                        .execute()
                                }
                            }, context!!).execute()
                            CustomServer.AddLog0(user, "+5", "Reward for watching ad", context!!)
                                .execute()
                            setCoin(view)
                        }

                        override fun onRewardedAdFailedToShow(errorCode: Int) {
                            Toast.makeText(activity, "Ad failed to show", Toast.LENGTH_SHORT).show()
                            loadRewardedAd()
                        }
                    }
                    rewardedAd.show(activityContext, adCallback)
                } else {
                    Toast.makeText(
                        activity,
                        "No ads available, try again later!",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadRewardedAd()
                }
            }
            else {
                var timeDiff="Ads will be available in a while."
                val diff=localSave.getLong("lastAdAdded",0)+10800000-System.currentTimeMillis()
                if(System.currentTimeMillis()-localSave.getLong("lastAdAdded",0)>10800000){
                    localSave.edit().putLong("lastAdAdded",System.currentTimeMillis()).apply()
                    localSave.edit().putInt("availableAd",3).apply()
                }
                if (diff<5000){
                    timeDiff = "Please wait few seconds to get new ad"
                }
                else if (diff<60000){
                    timeDiff = "Please wait for "+ TimeUnit.MILLISECONDS.toSeconds(diff).toString() + " second/s."
                }

                else if (diff<3600000){
                    timeDiff = "Please wait for "+ TimeUnit.MILLISECONDS.toMinutes(diff).toString() + " minute/s."
                }
                else{
                    timeDiff = "Please wait for "+ TimeUnit.MILLISECONDS.toHours(diff).toString() + " hour/s."
                }
                Toast.makeText(
                    activity,
                    timeDiff,
                    Toast.LENGTH_SHORT
                ).show()


            }
        }


        TransitionManager.beginDelayedTransition(combineFragment)
        popupWindow.showAtLocation(
            combineFragment, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            0 // Y offset
        )
    }



    private fun setCoin(view: View) {
        CustomServer.GetValue("users/$user/bal",object :ServerListener{
            override fun runWithValue(value: String) {
                if(view.balance!=null)
                    view.balance.text="+ $value \uD83D\uDCB5"
            }
        },context!!).execute()
    }


    fun getElementsID(){

        searchElementID.clear()
        searchElement.clear()
        colorOfSearchElement.clear()
        elementsUnlockedID=ArrayList(localSave.getString("unlockedElements","0,1,2,3")!!.split(","))
        if(context!=null)
            CustomServer.UpdateUnlocked(user,"${elementsUnlockedID.size}",context!!).execute()

        getElements()
    }

    fun getElements(){
        totalElementCount=localSave.getLong("totalElements",0).toInt()

        elementsUnlocked.clear()
        colorOfElement.clear()

        searchElementID.addAll(elementsUnlockedID)

        elementsUnlockedID.forEach {
            localSave.getString("elements/$it/name","No name found")?.let { it1 -> elementsUnlocked.add(it1) }
            localSave.getString("elements/$it/color","#55555555")?.let { it1 -> colorOfElement.add(it1) }
        }
        updateElements()
    }



    fun updateElements(p0:String?=null){
        searchElementID.clear()
        searchElement.clear()
        colorOfSearchElement.clear()
        isHeading.clear()
        islocked.clear()

        var unlockedElementsID:ArrayList<String> = ArrayList()
        var unlockedElementsName:ArrayList<String> = ArrayList()
        var unlockedElementsColor:ArrayList<String> = ArrayList()

        var category:MutableMap<String,ArrayList<ArrayList<String>>> = mutableMapOf()

        //Add elements to array list
        var i=0
        elementsUnlocked.forEach {
            if(!p0.isNullOrEmpty()&&!p0.startsWith("cat:")) {
                var addItem=false
                if(p0.startsWith("exact:")){
                    if(it.equals(p0.substring(6),true))
                        addItem=true
                }
                else{
                    if (it.contains(p0, true))
                        addItem=true
                }
                if (addItem) {
                    unlockedElementsID.add(elementsUnlockedID[i])
                    unlockedElementsName.add(it)
                    unlockedElementsColor.add(colorOfElement[i])
                }
            }
            else{
                unlockedElementsID.add(elementsUnlockedID[i])
                unlockedElementsName.add(it)
                unlockedElementsColor.add(colorOfElement[i])
            }
            i++
        }



        searchElementID.addAll(unlockedElementsID)
        searchElement.addAll(unlockedElementsName)
        colorOfSearchElement.addAll(unlockedElementsColor)
        islocked.addAll(Array(unlockedElementsID.size) { false })


        if(localSave.getBoolean("showLockedElements",true)&&!localSave.getBoolean("splitLockedElements",true)){
            i=0
            lockedElement.forEach {
                if(!p0.isNullOrEmpty()&&!p0.startsWith("cat:")) {
                    var addItem=false
                    if(p0.startsWith("exact:")){
                        if(it.equals(p0.substring(6),true))
                            addItem=true
                    }
                    else{
                        if (it.contains(p0, true))
                            addItem=true
                    }
                    if (addItem) {
                        searchElementID.add(lockedElementID[i])
                        searchElement.add(it)
                        colorOfSearchElement.add(colorOfLockedElement[i])
                        islocked.add(true)
                    }
                }
                else{
                    searchElementID.add(lockedElementID[i])
                    searchElement.add(it)
                    colorOfSearchElement.add(colorOfLockedElement[i])
                    islocked.add(true)
                }
                i++
            }

        }


        //setting sort
        if(localSave.getInt("sortMode",0)==1||localSave.getInt("sortMode",0)==2||localSave.getInt("sortMode",0)==3) {
            lateinit var sortElement: List<Element>
            lateinit var lockedSort: List<Element>
            if(localSave.getInt("sortMode",0)==1) {
                if (localSave.getInt("sortType", 0) == 0) {
                    sortElement = searchElementID.mapIndexed { index, s -> Element(s.toInt(), searchElement[index], colorOfSearchElement[index], islocked[index]) }.sortedBy { it.name }
                    if(localSave.getBoolean("showLockedElements",true))
                        lockedSort = lockedElementID.mapIndexed { index, s -> Element(s.toInt(), lockedElement[index], colorOfLockedElement[index]) }.sortedBy { it.name }
                }
                else{
                    sortElement = searchElementID.mapIndexed { index, s -> Element(s.toInt(), searchElement[index], colorOfSearchElement[index], islocked[index]) }.sortedByDescending { it.name }
                    if(localSave.getBoolean("showLockedElements",true))
                        lockedSort = lockedElementID.mapIndexed { index, s -> Element(s.toInt(), lockedElement[index], colorOfLockedElement[index]) }.sortedByDescending { it.name }
                }
            }
            else if(localSave.getInt("sortMode",0)==2){
                if (localSave.getInt("sortType", 0) == 0) {
                    sortElement = searchElementID.mapIndexed { index, s -> Element(s.toInt(), searchElement[index], colorOfSearchElement[index], islocked[index]) }.sortedBy { it.id }
                    if(localSave.getBoolean("showLockedElements",true))
                        lockedSort = lockedElementID.mapIndexed { index, s -> Element(s.toInt(), lockedElement[index], colorOfLockedElement[index]) }.sortedBy { it.id }
                }
                else{
                    sortElement = searchElementID.mapIndexed { index, s -> Element(s.toInt(), searchElement[index], colorOfSearchElement[index], islocked[index]) }.sortedByDescending { it.id }
                    if(localSave.getBoolean("showLockedElements",true))
                        lockedSort = lockedElementID.mapIndexed { index, s -> Element(s.toInt(), lockedElement[index], colorOfLockedElement[index]) }.sortedByDescending { it.id }
                }
            }
            else{
                if (localSave.getInt("sortType", 0) == 0) {
                    sortElement = searchElementID.mapIndexed { index, s -> Element(s.toInt(), searchElement[index], colorOfSearchElement[index], islocked[index]) }.sortedBy { localSave.getInt("elements/${it.id}/comments/count",0)}
                    if(localSave.getBoolean("showLockedElements",true))
                        lockedSort = lockedElementID.mapIndexed { index, s -> Element(s.toInt(), lockedElement[index], colorOfLockedElement[index]) }.sortedBy { localSave.getInt("elements/${it.id}/comments/count",0)}
                }
                else{
                    sortElement = searchElementID.mapIndexed { index, s -> Element(s.toInt(), searchElement[index], colorOfSearchElement[index], islocked[index]) }.sortedByDescending { localSave.getInt("elements/${it.id}/comments/count",0) }
                    if(localSave.getBoolean("showLockedElements",true))
                        lockedSort = lockedElementID.mapIndexed { index, s -> Element(s.toInt(), lockedElement[index], colorOfLockedElement[index]) }.sortedByDescending { localSave.getInt("elements/${it.id}/comments/count",0) }
                }
            }
            islocked.clear()

            islocked.addAll(sortElement.map { it.isLocked })


            searchElementID.clear()
            searchElement.clear()
            colorOfSearchElement.clear()

            searchElementID.addAll(sortElement.map { it.id.toString() })
            searchElement.addAll(sortElement.map { it.name })
            colorOfSearchElement.addAll(sortElement.map { it.color })


            if(localSave.getBoolean("showLockedElements",true)){
                lockedElementID.clear()
                lockedElement.clear()
                colorOfLockedElement.clear()
                lockedElementID.addAll(lockedSort!!.map { it.id.toString() })
                lockedElement.addAll(lockedSort!!.map { it.name })
                colorOfLockedElement.addAll(lockedSort!!.map { it.color })
            }
        }
        else{
            if (localSave.getInt("sortType", 0) == 0){
                searchElement.reverse()
                searchElementID.reverse()
                colorOfSearchElement.reverse()
                islocked.reverse()

                if(localSave.getBoolean("showLockedElements",true)&&localSave.getBoolean("splitLockedElements",true)){
                    lockedElementID.reverse()
                    lockedElement.reverse()
                    colorOfLockedElement.reverse()
                }
            }
        }


        //Go through categories
        i=0
        var catSearch=""
        if(!p0.isNullOrEmpty()){
            if(p0.startsWith("cat:")){
                catSearch=p0.substring(4)
                catSearch=catSearch.trim()
            }
        }
        Log.d("checkcat","hmm $catSearch")
        if(localSave.getBoolean("showCategories",false)||catSearch!=""){
            searchElementID.forEach {
                if(!islocked[i]) {
                    val categoriesString = localSave.getString("category/$it", "").toString()
                    if (categoriesString != "") {
                        val categories = categoriesString.split(",")
                        categories.forEach { it1 ->
                            if (category[it1] == null) {
                                var tempID: ArrayList<String> = ArrayList()
                                var tempName: ArrayList<String> = ArrayList()
                                var tempColor: ArrayList<String> = ArrayList()
                                var temp: ArrayList<ArrayList<String>> = ArrayList()
                                temp.add(tempID)
                                temp.add(tempName)
                                temp.add(tempColor)
                                category[it1] = temp
                            }

                            if (catSearch == "") {
                                category[it1]?.get(0)?.add(it)
                                category[it1]?.get(1)?.add(searchElement[i])
                                category[it1]?.get(2)?.add(colorOfSearchElement[i])
                            } else {
                                if (it1.contains(catSearch, true)) {
                                    category[it1]?.get(0)?.add(it)
                                    category[it1]?.get(1)?.add(searchElement[i])
                                    category[it1]?.get(2)?.add(colorOfSearchElement[i])
                                }
                            }
                        }
                    }
                }
                i++
            }
        }

        val sortedID:ArrayList<String> = ArrayList()
        val sortedName:ArrayList<String> = ArrayList()
        val sortedColor:ArrayList<String> = ArrayList()
        val sortedIsLocked:ArrayList<Boolean> = ArrayList()
        sortedID.addAll(searchElementID)
        sortedName.addAll(searchElement)
        sortedColor.addAll(colorOfSearchElement)
        sortedIsLocked.addAll(islocked)

        searchElementID.clear()
        searchElement.clear()
        colorOfSearchElement.clear()
        islocked.clear()
        isHeading.clear()


        //Showing categories and its search result
        category.forEach {

            //Show all if show categories is on
            if(localSave.getBoolean("showCategories",false)){
                if(it.value[0].size>0) {
                    searchElementID.add("-1")
                    searchElement.add(it.key)
                    colorOfSearchElement.add("#FFFFFFFF")
                    isHeading.add(1)
                    islocked.add(false)
                    searchElementID.addAll(it.value[0])
                    searchElement.addAll(it.value[1])
                    colorOfSearchElement.addAll(it.value[2])
                    isHeading.addAll(Array(it.value[0].size) { 0 })
                    islocked.addAll(Array(it.value[0].size) { false })
                }
            }
            else{
                var i=0
                it.value[0].forEach {it1->
                    if(!searchElementID.contains(it1)){
                        searchElementID.add(it1)
                        searchElement.add(it.value[1][i])
                        colorOfSearchElement.add(it.value[2][i])
                        isHeading.add(0)
                        islocked.add(false)
                    }
                    i++
                }
            }
        }

        //Set categories for unlocked/all
        if(localSave.getBoolean("showCategories",false)){
            searchElementID.add("-1")
            if(localSave.getBoolean("splitLockedElements",true))
                searchElement.add("Unlocked Elements")
            else
                searchElement.add("All Elements")
            colorOfSearchElement.add("#FFFFFFFF")
            isHeading.add(1)
            islocked.add(false)
        }
        searchElementID.addAll(sortedID)
        searchElement.addAll(sortedName)
        colorOfSearchElement.addAll(sortedColor)
        islocked.addAll(sortedIsLocked)
        isHeading.addAll(Array(sortedID.size) { 0 })

        //If split locked elements
        if(localSave.getBoolean("showLockedElements",true)&&localSave.getBoolean("splitLockedElements",true)){
            //Setting category for locked
            if(localSave.getBoolean("showCategories",false)){
                searchElementID.add("-1")
                searchElement.add("Locked Elements")
                colorOfSearchElement.add("#FFFFFFFF")
                isHeading.add(1)
                islocked.add(false)
            }
            i=0
            lockedElement.forEach {
                if(!p0.isNullOrEmpty()) {
                    if (it.contains(p0, true)) {
                        searchElementID.add(lockedElementID[i])
                        searchElement.add(it)
                        colorOfSearchElement.add(colorOfLockedElement[i])
                        isHeading.add(0)
                        islocked.add(true)
                    }
                }
                else{
                    searchElementID.add(lockedElementID[i])
                    searchElement.add(it)
                    colorOfSearchElement.add(colorOfLockedElement[i])
                    isHeading.add(0)
                    islocked.add(true)
                }
                i++
            }
        }

//        if(!p0.isNullOrEmpty()){
//            var searchTerm=""
//            if(p0.startsWith("cat:"))
//                searchTerm=p0.substring(4)
//            if(p0.startsWith("category:"))
//                searchTerm=p0.substring(9)
//            if(searchTerm!=""){
//                val uniquecats = localSave.getString("uniqueCategories","").toString().split(",")
//                uniquecats.forEach {
//                    if(it.contains(searchTerm,true)){
//                        val addElement=localSave.getString("categoryToElement/$it","").toString().split(",")
//                        addElement.forEach{
//                            if(it.toIntOrNull()!=null) {
//                                if(!searchElementID.contains(it)) {
//                                    searchElementID.add(it)
//                                    searchElement.add(localSave.getString("elements/$it/name", "Failed to load $it").toString())
//                                    colorOfSearchElement.add(localSave.getString("elements/$it/color", "#55555555").toString())
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        if(localSave.getInt("sortMode",0)==1||localSave.getInt("sortMode",0)==2||localSave.getInt("sortMode",0)==3) {
//            var sortElement:List<Element>
//            var lockedSort:List<Element>
//            if(localSave.getInt("sortMode",0)==1) {
//                if (localSave.getInt("sortType", 0) == 0) {
//                    sortElement = searchElementID.mapIndexed { index, s -> Element(s.toInt(), searchElement[index], colorOfSearchElement[index]) }.sortedBy { it.name }
//                    lockedSort = lockedElementID.mapIndexed { index, s -> Element(s.toInt(), lockedElement[index], colorOfLockedElement[index]) }.sortedBy { it.name }
//                }
//                else{
//                    sortElement = searchElementID.mapIndexed { index, s -> Element(s.toInt(), searchElement[index], colorOfSearchElement[index]) }.sortedByDescending { it.name }
//                    lockedSort = lockedElementID.mapIndexed { index, s -> Element(s.toInt(), lockedElement[index], colorOfLockedElement[index]) }.sortedByDescending { it.name }
//                }
//            }
//            else if(localSave.getInt("sortMode",0)==2){
//                if (localSave.getInt("sortType", 0) == 0) {
//                    sortElement = searchElementID.mapIndexed { index, s -> Element(s.toInt(), searchElement[index], colorOfSearchElement[index]) }.sortedBy { it.id }
//                    lockedSort = lockedElementID.mapIndexed { index, s -> Element(s.toInt(), lockedElement[index], colorOfLockedElement[index]) }.sortedBy { it.id }
//                }
//                else{
//                    sortElement = searchElementID.mapIndexed { index, s -> Element(s.toInt(), searchElement[index], colorOfSearchElement[index]) }.sortedByDescending { it.id }
//                    lockedSort = lockedElementID.mapIndexed { index, s -> Element(s.toInt(), lockedElement[index], colorOfLockedElement[index]) }.sortedByDescending { it.id }
//                }
//                if(localSave.getBoolean("showLockedElements",true)&&!localSave.getBoolean("splitLockedElements",true)){
//
//                }
//            }
//            else{
//                if (localSave.getInt("sortType", 0) == 0) {
//                    sortElement = searchElementID.mapIndexed { index, s -> Element(s.toInt(), searchElement[index], colorOfSearchElement[index]) }.sortedBy { localSave.getInt("elements/${it.id}/comments/count",0)}
//                    lockedSort = lockedElementID.mapIndexed { index, s -> Element(s.toInt(), lockedElement[index], colorOfLockedElement[index]) }.sortedBy { localSave.getInt("elements/${it.id}/comments/count",0)}
//                }
//                else{
//                    sortElement = searchElementID.mapIndexed { index, s -> Element(s.toInt(), searchElement[index], colorOfSearchElement[index]) }.sortedByDescending { localSave.getInt("elements/${it.id}/comments/count",0) }
//                    lockedSort = lockedElementID.mapIndexed { index, s -> Element(s.toInt(), lockedElement[index], colorOfLockedElement[index]) }.sortedByDescending { localSave.getInt("elements/${it.id}/comments/count",0) }
//                }
//            }
//            searchElementID.clear()
//            searchElement.clear()
//            colorOfSearchElement.clear()
//
//            searchElementID.addAll(sortElement.map { it.id.toString() })
//            searchElement.addAll(sortElement.map { it.name })
//            colorOfSearchElement.addAll(sortElement.map { it.color })
//
//
//
//            lockedElementID.clear()
//            lockedElement.clear()
//            colorOfLockedElement.clear()
//            lockedElementID.addAll(lockedSort.map { it.id.toString() })
//            lockedElement.addAll(lockedSort.map { it.name })
//            colorOfLockedElement.addAll(lockedSort.map { it.color })
//        }
//        else{
//            if (localSave.getInt("sortType", 0) == 0){
//                searchElement.reverse()
//                searchElementID.reverse()
//                colorOfSearchElement.reverse()
//
//                lockedElementID.reverse()
//                lockedElement.reverse()
//                colorOfLockedElement.reverse()
//            }
//        }
//
//
//        lockedElementsFrom=searchElementID.size
//        var index = 0
//        if(localSave.getBoolean("showCategories",false)){
//            val catElements= mutableMapOf<String,ArrayList<String>>()
//            searchElementID.forEach {
//                if(it!="-1"){
//                    if(localSave.getString("category/$it","")!=""){
//                        if(catElements[localSave.getString("category/$it","")].isNullOrEmpty()){
//                            val tempArrayList:ArrayList<String> = ArrayList()
//                            catElements[localSave.getString("category/$it","").toString()]=tempArrayList
//                        }
//                        catElements[localSave.getString("category/$it","").toString()]!!.add(it)
//                    }
//                }
//            }
//
//            catElements.forEach {
//                searchElementID.add(index, "-1")
//                searchElement.add(index, it.key)
//                colorOfSearchElement.add(index, "#FFFFFFFF")
//                isHeading.add(index, 1)
//                lockedElementsFrom++
//                index++
//                it.value.forEach {
//                    searchElementID.add(index,it)
//                    searchElement.add(index,localSave.getString("elements/$it/name", "Failed to load $it").toString())
//                    colorOfSearchElement.add(index,localSave.getString("elements/$it/color", "#55555555").toString())
//                    isHeading.add(0)
//                    lockedElementsFrom++
//                    index++
//                }
//            }
//        }
//
//
//        if(localSave.getBoolean("showLockedElements",true)&&localSave.getBoolean("splitLockedElements",true)) {
//
//
//            i = 0
//            lockedElement.forEach {
//                if(!p0.isNullOrEmpty()) {
//                    if (it.contains(p0, true)) {
//                        searchElementID.add(lockedElementID[i])
//                        searchElement.add(it)
//                        colorOfSearchElement.add(colorOfLockedElement[i])
//                    }
//                }
//                else{
//                    searchElementID.add(lockedElementID[i])
//                    searchElement.add(it)
//                    colorOfSearchElement.add(colorOfLockedElement[i])
//                }
//                i++
//            }
//        }
//        isHeading.addAll(Array(searchElementID.size-index) { 0 })
//        if(localSave.getBoolean("showCategories",false)) {
//            searchElementID.add(index, "-1")
//            searchElement.add(index, "Unlocked Elements")
//            colorOfSearchElement.add(index, "#FFFFFFFF")
//            isHeading.add(index, 1)
//            lockedElementsFrom++
//            if(localSave.getBoolean("showLockedElements",false)&&localSave.getBoolean("splitLockedElements",true)) {
//                searchElementID.add(lockedElementsFrom, "-1")
//                searchElement.add(lockedElementsFrom, "Locked Elements")
//                colorOfSearchElement.add(lockedElementsFrom, "#FFFFFFFF")
//                isHeading.add(lockedElementsFrom, 1)
//            }
//        }
        setCombineElements()
        setUnlockedElements()

    }

    fun setCombineSectionMode(){
        view?.showCombineVertical?.setOnClickListener {
            if(localSave.getBoolean("isCombineSectionVertical",false))
                localSave.edit().putBoolean("isCombineSectionVertical",false).apply()
            else
                localSave.edit().putBoolean("isCombineSectionVertical",true).apply()

            setCombineElements()
        }
    }

    fun setPinElements(elementAdded: Boolean=false){
        if(pins_rv!=null&&context!=null){
            localSave.edit().putBoolean("displayOrientationPortrait",resources.configuration.orientation==Configuration.ORIENTATION_PORTRAIT).apply()
            if(localSave.getBoolean("displayOrientationPortrait",true)){
                if(pinElementID.size>0){
                    elements_rv.setBackgroundColor(ContextCompat.getColor(context!!,R.color.layoutBG))
                }
                else{
                    context?.let { ContextCompat.getDrawable(it,R.drawable.rounded_bottom_light_gray) }
                }
            }

            var animateElement:ArrayList<Boolean> = ArrayList()
            if(animateElement.isEmpty()){
                var i=0
                while(i<pinElementID.size){
                    animateElement.add(false)
                    i++
                }
            }
            if(elementAdded){
                animateElement[animateElement.size-1]=true
            }
            val tempHeading:ArrayList<Int> = ArrayList()
            tempHeading.addAll(Array(pinElementID.size){0})
            pins_rv.layoutManager=LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)
            pins_rv.adapter=MyElementsAdapter(pinElementName, pinElementColor,
                object : ElementClickListener {
                    override fun elementClicked(pos: Int) {
                        soundPool.play(
                            soundElementClickedID,
                            localSave.getFloat("SFXVolume", 1F),
                            localSave.getFloat("SFXVolume", 1F),
                            0,
                            0,
                            1F
                        )
                        if (selectedElementsID.size < 4 && pos<searchElementID.size) {
                            if(searchElementID[pos].toIntOrNull()!=null)
                                selectedElementsID.add(pinElementID[pos].toInt())
                            setCombineElements(true)
                        } else
                            Toast.makeText(
                                activity,
                                "Maximum 4 elements are allowed!",
                                Toast.LENGTH_SHORT
                            ).show()


                    }

                    override fun showInfo(pos: Int) {
                        pinElementID.removeAt(pos)
                        pinElementName.removeAt(pos)
                        pinElementColor.removeAt(pos)
                        setPinElements()
                    }

                },animateElement,2,tempHeading, activity!!
            )
        }
    }

    fun setCombineElements(elementAdded:Boolean=false){
        selectedElements.clear()
        colorOfSelectedElements.clear()
        selectedElementsID.forEach {

            selectedElements.add(elementsUnlocked[elementsUnlockedID.indexOf(it.toString())])
            colorOfSelectedElements.add(colorOfElement[elementsUnlockedID.indexOf(it.toString())])
        }
        var animateElement:ArrayList<Boolean> = ArrayList()
        if(animateElement.isEmpty()){
            var i=0
            while(i<selectedElementsID.size){
                animateElement.add(false)
                i++
            }
        }
        if(elementAdded){
            animateElement[animateElement.size-1]=true
        }
        var combineIsEmpty=false
        if(selectedElements.isEmpty()){
            selectedElements.add("Tap an element from \"Your Elements\" to add into combine section")
            colorOfSelectedElements.add("#AAAAAAAA")
            animateElement.add(true)
            combineIsEmpty=true
        }

        if(combine_rv!=null) {
            localSave.edit().putLong("lastElementClick",System.currentTimeMillis()).apply()
            view!!.yourElementsHeading.text = "Your Elements (${elementsUnlockedID.size}/${elementsUnlockedID.size+lockedElementID.size})"
            localSave.edit().putBoolean("displayOrientationPortrait",resources.configuration.orientation==Configuration.ORIENTATION_PORTRAIT).apply()
            if(localSave.getBoolean("displayOrientationPortrait",true)){
                if(localSave.getBoolean("isCombineSectionVertical",false)) {
                    var layoutParams=view!!.showCombineVertical?.layoutParams
                    layoutParams?.height=ConstraintLayout.LayoutParams.WRAP_CONTENT
                    view!!.showCombineVertical?.layoutParams=layoutParams
                    view!!.showCombineVertical?.text="ᐯ"
                    view!!.showCombineVertical?.background=
                        context?.let { ContextCompat.getDrawable(it,R.drawable.rounded_bottomleft_primary) }
                    val changeConst=combine_rv.layoutParams as ConstraintLayout.LayoutParams
                    changeConst.endToStart=view!!.combineButton.id
                    combine_rv.layoutParams=changeConst
                    combine_rv.requestLayout()
                    combine_rv.layoutManager = LinearLayoutManager(activity!!)
                }
                else {
                    var layoutParams = view!!.showCombineVertical?.layoutParams
                    layoutParams?.height = 0
                    view!!.showCombineVertical?.layoutParams = layoutParams
                    view!!.showCombineVertical?.text = "ᐱ"
                    context?.let {
                        ContextCompat.getColor(
                            it, R.color.colorPrimary
                        )
                    }?.let { view!!.showCombineVertical?.setBackgroundColor(it) }


                    val changeConst = combine_rv.layoutParams as ConstraintLayout.LayoutParams
                    changeConst.endToStart = view!!.showCombineVertical!!.id
                    combine_rv.layoutParams = changeConst
                    combine_rv.requestLayout()
                    combine_rv.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)
                }
            }
            else{
                combine_rv.layoutManager = LinearLayoutManager(activity!!)
            }


            val tempHeading:ArrayList<Int> = ArrayList()
            tempHeading.addAll(Array(selectedElements.size){0})
            combine_rv.adapter = MyElementsAdapter(selectedElements,
                colorOfSelectedElements, object : ElementClickListener {
                    override fun elementClicked(pos: Int) {
                        if(!combineIsEmpty) {
                            if(pos>-1 && pos<selectedElementsID.size)
                                selectedElementsID.removeAt(pos)
                            setCombineElements()
                        }
                    }
                },animateElement,0,tempHeading, activity!!
            )

        }
    }
    fun setUnlockedElements(){
        var animateElement:ArrayList<Boolean> = ArrayList()

        if(animateElement.isEmpty()){
            var i=0
            while(i<searchElementID.size){
                animateElement.add(true)
                i++
            }

        }
        if(elements_rv!=null){
            if(localSave.getBoolean("showElementScrollAnimation",true)){
                if(!settedRV){
                    elements_rv.addOnScrollListener(object :RecyclerView.OnScrollListener(){
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            if(System.currentTimeMillis()-localSave.getLong("rv/elements/lastScroll",0)<200)
                                localSave.edit().putLong("rv/elements/setScroll",System.currentTimeMillis()).apply()
                            localSave.edit().putLong("rv/elements/lastScroll",System.currentTimeMillis()).apply()
                        }
                    })
                    settedRV=true
                }
            }
            var oldScroll = view!!.elements_rv.layoutManager?.onSaveInstanceState()
            if(localSave.getInt("elementRVColumnCount",1)==1)
                elements_rv.layoutManager = LinearLayoutManager(activity!!)
            else
                elements_rv.layoutManager = StaggeredGridLayoutManager(localSave.getInt("elementRVColumnCount",1),StaggeredGridLayoutManager.VERTICAL)
            elements_rv.adapter = MyElementsAdapter(searchElement, colorOfSearchElement,
                object : ElementClickListener {
                    override fun elementClicked(pos: Int) {
                        if (!islocked[pos]) {
                            soundPool.play(
                                soundElementClickedID,
                                localSave.getFloat("SFXVolume", 1F),
                                localSave.getFloat("SFXVolume", 1F),
                                0,
                                0,
                                1F
                            )
                            if (selectedElementsID.size < 4 && pos<searchElementID.size) {
                                if(searchElementID[pos].toIntOrNull()!=null)
                                    selectedElementsID.add(searchElementID[pos].toInt())
                                setCombineElements(true)
                            } else
                                Toast.makeText(
                                    activity,
                                    "Maximum 4 elements are allowed!",
                                    Toast.LENGTH_SHORT
                                ).show()

                        }
                    }

                    override fun showInfo(pos: Int) {
                        if(!islocked[pos])
                            showElementInfo(searchElementID[pos].toInt())
                    }

                },animateElement,1,isHeading, activity!!
            )
            view!!.elements_rv.layoutManager!!.onRestoreInstanceState(oldScroll)
        }

    }

    fun openProfile(id:String){
        val inflater:LayoutInflater = LayoutInflater.from(context)

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.profile,null)

        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )

        popupWindow.isFocusable = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 25.0F
        }


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Create a new slide animation for popup window enter transition
            val fade = android.transition.Fade()

            popupWindow.enterTransition = fade

            // Slide animation for popup window exit transition
            popupWindow.exitTransition = fade

        }


        val nameTV = view.findViewById<TextView>(R.id.profileName)
        val unlockedTV = view.findViewById<TextView>(R.id.profileElementsUnlocked)
        val createdTV = view.findViewById<TextView>(R.id.profileElementsCreated)
        val balanceTV = view.findViewById<TextView>(R.id.profileBalance)
        val rankTV = view.findViewById<TextView>(R.id.profileRank)
        val onlineTV = view.findViewById<TextView>(R.id.profileOnline)
        val statusTV = view.findViewById<TextView>(R.id.profileStatus)
        val reportUsername = view.findViewById<TextView>(R.id.reportUsername)
        val removeUsername = view.findViewById<TextView>(R.id.removeUsername)
        val mainLayout = view.findViewById<LinearLayout>(R.id.mainProfileLayout)

        var menuOpen=false
        var isReporting=false
        var isMod=false
        var modCheck=false

        fun toggleMenu(){
            if(!isReporting){
                if(menuOpen){
                    menuOpen=false
                    reportUsername.visibility=View.GONE
                    removeUsername.visibility=View.GONE
                }
                else{
                    menuOpen = true
                    reportUsername.visibility=View.VISIBLE
                    if (modCheck) {
                        if (isMod) {
                            reportUsername.visibility = View.GONE
                            removeUsername.visibility = View.VISIBLE
                        }
                    } else {
                        CustomServer.IsMod(
                            FirebaseAuth.getInstance().uid.toString(),
                            "0",
                            object : ServerListener {
                                override fun runWithValue(value: String) {
                                    if (value == "1") {
                                        isMod = true
                                        reportUsername.visibility = View.GONE
                                        removeUsername.visibility = View.VISIBLE
                                    }
                                    modCheck = true
                                }
                            },
                            context!!
                        ).execute()
                    }

                }
            }
        }

        reportUsername.setOnClickListener {
            if (!isReporting) {
                isReporting = true
                CustomServer.ReportUsername(
                    FirebaseAuth.getInstance().uid.toString(),
                    id,
                    object : ServerListener {
                        override fun runWithValue(value: String) {
                            if (value == "1") {
                                isReporting = false
                                toggleMenu()
                                Toast.makeText(context,"Reported", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    context!!
                ).execute()
            }
        }

        removeUsername.setOnClickListener {
            if (!isReporting) {
                isReporting = true
                CustomServer.ResetUsername(
                    FirebaseAuth.getInstance().uid.toString(),
                    id,
                    object : ServerListener {
                        override fun runWithValue(value: String) {
                            if (value == "1") {
                                isReporting = false
                                toggleMenu()
                                Toast.makeText(context,"Removed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    context!!
                ).execute()
            }
        }

        mainLayout.setOnLongClickListener {
            toggleMenu()
            true
        }



        CustomServer.GetProfile(id,object :ServerListener{
            override fun runWithValue(value: String) {
                try{
                    val data=JSONObject(value)
                    nameTV.text = data.getString("name")
                    statusTV.text = data.getString("status")
                    unlockedTV.text = "${data.getString("unlocked")}"
                    rankTV.text = "${data.getString("rank")}"
                    createdTV.text = "${data.getString("elementsCreated")}"
                    balanceTV.text = "${data.getString("bal")}"
                    if(data.getString("online")=="1")
                        onlineTV.text = "Online"
                    else{
                        onlineTV.text=onlineTV.text.toString()+"\n\n"
                        if(data.getString("lastOnline")!="0") {
                            val diff =
                                System.currentTimeMillis() - data.getString("lastOnline").toLong()
                            if (diff < 5000) {
                                onlineTV.text = " A few seconds ago"
                            } else if (diff < 60000) {
                                onlineTV.text =
                                    TimeUnit.MILLISECONDS.toSeconds(
                                        diff
                                    ).toString() + " seconds ago"
                            } else if (diff < 3600000) {
                                onlineTV.text =
                                    TimeUnit.MILLISECONDS.toMinutes(
                                        diff
                                    ).toString() + " minutes ago"
                            } else if (diff < 86400000) {
                                onlineTV.text =
                                    TimeUnit.MILLISECONDS.toHours(
                                        diff
                                    ).toString() + " hours ago"
                            } else {
                                var daysAgo = TimeUnit.MILLISECONDS.toDays(diff)
                                if (daysAgo < 30) {
                                    onlineTV.text =
                                        daysAgo.toString() + " days ago"
                                } else if (daysAgo < 360) {
                                    onlineTV.text =
                                        (daysAgo / 30).toString() + " months and " + (daysAgo % 30).toString() + " days ago"
                                } else {
                                    onlineTV.text =
                                        (daysAgo / 360).toString() + " years, " + ((daysAgo % 360) / 30).toString() + " months" + ((daysAgo % 360) % 30).toString() + " ago"
                                }
                            }
                        }
                        else
                            onlineTV.text = "Offline"
                    }
                }catch (e:Exception){
                    Log.e("errorDisplayingProfile","${e.message}")
                }

            }
        },context!!).execute()


        TransitionManager.beginDelayedTransition(combineFragment)

        popupWindow.showAtLocation(
            combineFragment, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            0 // Y offset
        )
    }

    fun showElementInfo(pos: Int){

        val inflater:LayoutInflater = LayoutInflater.from(context)

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.element_info,null)

        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )

        popupWindow.setOnDismissListener {
            infoOpen=false
        }

        popupWindow.isFocusable = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 25.0F
        }

        view.setOnTouchListener { v, event -> //Close the window when clicked
            popupWindow.dismiss()
            true
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Create a new slide animation for popup window enter transition
            val fade = android.transition.Fade()

            popupWindow.enterTransition = fade

            // Slide animation for popup window exit transition
            popupWindow.exitTransition = fade

        }

        val titleTV = view.findViewById<TextView>(R.id.titleTV)
        val rootLayout = view.findViewById<ConstraintLayout>(R.id.mainLayout)
        val createdInfo = view.findViewById<TextView>(R.id.createdInfo)
        val combine_rv = view.findViewById<RecyclerView>(R.id.using_rv)
        val combine_Layout = view.findViewById<LinearLayout>(R.id.usingCanvas)
        val viewSwitch = view.findViewById<TextView>(R.id.switchView)

        val elementInfoCanvas = ElementInfo(context!!)
        val combinationET=view.findViewById<EditText>(R.id.categories)
        val recommendedButton=view.findViewById<TextView>(R.id.getRecommendedCat)
        val idTV=view.findViewById<TextView>(R.id.elementID)

        val fireLayout=view.findViewById<LinearLayout>(R.id.fire)
        val earthLayout=view.findViewById<LinearLayout>(R.id.earth)
        val waterLayout=view.findViewById<LinearLayout>(R.id.water)
        val airLayout=view.findViewById<LinearLayout>(R.id.air)

        val openCommentBtn=view.findViewById<TextView>(R.id.elementComments)

        val pinElement=view.findViewById<TextView>(R.id.pinElement)

        openCommentBtn.text = "${localSave.getInt("elements/$pos/comments/count",0)} \uD83D\uDCAC"

        soundPool.play(soundElementInfoOpenID, localSave.getFloat("SFXVolume", 1F), localSave.getFloat("SFXVolume", 1F), 0, 0, 1F)


        if(pinElementID.contains(pos.toString())){
            lowLightButton(listOf(pinElement),1,0)
        }
        else{
            lowLightButton(listOf(pinElement),0,1)
        }
        pinElement.setOnClickListener {
            if(pinElementID.contains(pos.toString())){
                val removeAt=pinElementID.indexOf(pos.toString())
                pinElementID.removeAt(removeAt)
                pinElementName.removeAt(removeAt)
                pinElementColor.removeAt(removeAt)
                lowLightButton(listOf(pinElement),0,1)
                setPinElements()
            }
            else{
                pinElementID.add(pos.toString())
                pinElementName.add(localSave.getString("elements/$pos/name","").toString())
                pinElementColor.add(localSave.getString("elements/$pos/color","").toString())
                lowLightButton(listOf(pinElement),1,0)
                setPinElements(true)
            }
        }

        createdInfo.setOnClickListener {
            openProfile(localSave.getString("elements/$pos/by","").toString())
        }

        elementInfoCanvas.setElementInfo(pos)

        openCommentBtn.setOnClickListener {
            openComments(pos.toString())
        }

        recommendedButton.visibility=View.GONE
        if(localSave.getString("category/$pos","")==""){
            recommendedButton.visibility=View.VISIBLE
        }
        combinationET.setText(localSave.getString("category/$pos",""))
        combinationET.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if(p0.isNullOrEmpty()&&p0.toString() !=localSave.getString("category/$pos","-1")){
                    val prevVal=localSave.getString("category/$pos","").toString()
                    if(prevVal!=""){
                        prevVal.split(",").forEach {
                            val curselstr=localSave.getString("categoryToElement/$it","").toString()
                            val currSelection: ArrayList<String> = ArrayList()
                            if(curselstr!="")
                                currSelection.addAll(curselstr.split(","))

                            if(currSelection.contains("$pos")){
                                currSelection.remove("$pos")
                                localSave.edit().putString("categoryToElement/$it",currSelection.joinToString(",")).apply()
                            }
                        }
                    }
                    localSave.edit().putString("category/$pos","").apply()
                    CustomServer.SetCategory(user,"$pos","",context!!).execute()
                    recommendedButton.visibility= VISIBLE
                    recommendedButton.text = "Recommended Category"
                    updateElements()
                }
                else{
                    if(p0.toString() != localSave.getString("category/$pos","-1")) {

                        recommendedButton.visibility= VISIBLE
                        recommendedButton.text = "Save"
                    }
                    else
                        recommendedButton.visibility=View.GONE
                }
            }
        })
        recommendedButton.setOnClickListener {
            if(combinationET.text.isNullOrEmpty()){
                CustomServer.GetRecommendedCategory("$pos",object :ServerListener{
                    override fun runWithValue(value: String) {
                        if(value.isNotEmpty()) {
                            combinationET.setText(value)
                        }
                        else
                            Toast.makeText(activity, "No Recommendation yet, try again later!", Toast.LENGTH_SHORT).show()
                    }
                },context!!).execute()
            }
            else{
                CustomServer.SetCategory(user,"$pos","${combinationET.text}",context!!).execute()
                val prevVal=localSave.getString("category/$pos","").toString()
                if(prevVal!=""){
                    prevVal.split(",").forEach {
                        val curselstr=localSave.getString("categoryToElement/$it","").toString()
                        val currSelection: ArrayList<String> = ArrayList()
                        if(curselstr!="")
                            currSelection.addAll(curselstr.split(","))

                        if(currSelection.contains("$pos")){
                            currSelection.remove("$pos")
                            localSave.edit().putString("categoryToElement/$it",currSelection.joinToString(",")).apply()
                        }
                    }
                }
                val newComb=combinationET.text.toString()
                if(newComb!=""){
                    newComb.split(",").forEach {
                        val uniquecatStr=localSave.getString("uniqueCategories","").toString()
                        val uniqueCat:ArrayList<String> = ArrayList()
                        if(uniquecatStr!="")
                            uniqueCat.addAll(uniquecatStr.split(","))
                        if(!uniqueCat.contains(it)){
                            uniqueCat.add(it)
                            localSave.edit().putString("uniqueCategories",uniqueCat.joinToString(",")).apply()
                        }

                        val curselstr=localSave.getString("categoryToElement/$it","").toString()
                        val currSelection: ArrayList<String> = ArrayList()
                        if(curselstr!="")
                            currSelection.addAll(curselstr.split(","))

                        if(!currSelection.contains("$pos")){
                            currSelection.add("$pos")
                            localSave.edit().putString("categoryToElement/$it",currSelection.joinToString(",")).apply()
                        }
                    }
                }
                localSave.edit().putString("category/$pos","${newComb}").apply()
                recommendedButton.visibility=View.GONE
                updateElements()
            }
        }
        if(localSave.getBoolean("usingList",false)) {
            combine_rv.visibility = View.GONE
        }
        else {
            combine_Layout.visibility = View.GONE

            viewSwitch.text="List View"
        }



        viewSwitch.setOnClickListener {
            if(localSave.getBoolean("usingList",false)){
                combine_rv.visibility= VISIBLE
                combine_Layout.visibility= View.GONE
                viewSwitch.text="List View"
                localSave.edit().putBoolean("usingList",false).apply()
            }
            else{
                combine_rv.visibility=View.GONE
                combine_Layout.visibility= VISIBLE
                viewSwitch.text="Hierarchical View"
                localSave.edit().putBoolean("usingList",true).apply()
            }
        }


        titleTV.text = localSave.getString("elements/$pos/name","Element not found in sync, try again later")
        idTV.text= "#$pos"
        elementTitles.clear()
        combinationList.clear()
        combinationElementColor.clear()
        combinationListColor.clear()


        var combinationList:ArrayList<String> = ArrayList()
        combinationList.add(pos.toString())
        combinationList.addAll(addToCombinationList(pos).split(","))
        if(pos<4){
            combinationList.clear()
            combinationList.add(pos.toString())
        }

        fireLayout.layoutParams= LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,5, localSave.getInt("elements/$pos/count0",0).toFloat())
        earthLayout.layoutParams= LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,5, localSave.getInt("elements/$pos/count1",0).toFloat())
        waterLayout.layoutParams= LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,5, localSave.getInt("elements/$pos/count2",0).toFloat())
        airLayout.layoutParams= LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,5, localSave.getInt("elements/$pos/count3",0).toFloat())

        if(combine_rv!=null){
            combine_rv.layoutManager=LinearLayoutManager(activity!!)
            combine_rv.adapter=CombinationAdapter(combinationList,activity!!)

            combine_Layout.addView(elementInfoCanvas)
        }



        if(localSave.getString("elements/$pos/by","")!=""){
            CustomServer.GetValue("users/${localSave.getString("elements/$pos/by","")}/name",object :ServerListener{
                override fun runWithValue(value: String) {
                    createdInfo.text = "By:"+value

                    val diff= System.currentTimeMillis()-localSave.getString("elements/$pos/time","Element not found in sync, try again later")!!.toLong()
                    if (diff<5000){
                        createdInfo.text = createdInfo.text.toString()+" (A few seconds ago)"
                    }
                    else if (diff<60000){
                        createdInfo.text = createdInfo.text.toString()+" ("+ TimeUnit.MILLISECONDS.toSeconds(diff).toString() + " seconds ago)"
                    }

                    else if (diff<3600000){
                        createdInfo.text = createdInfo.text.toString()+" ("+ TimeUnit.MILLISECONDS.toMinutes(diff).toString() + " minutes ago)"
                    }

                    else if (diff<86400000){
                        createdInfo.text = createdInfo.text.toString()+" ("+ TimeUnit.MILLISECONDS.toHours(diff).toString() + " hours ago)"
                    }
                    else{
                        var daysAgo= TimeUnit.MILLISECONDS.toDays(diff)
                        if(daysAgo<30){
                            createdInfo.text = createdInfo.text.toString()+" ("+daysAgo.toString() + " days ago)"
                        }
                        else if(daysAgo<360){
                            createdInfo.text = createdInfo.text.toString()+" ("+(daysAgo/30).toString() + " months and " + (daysAgo%30).toString() + " days ago)"
                        }
                        else{
                            createdInfo.text = createdInfo.text.toString()+" ("+(daysAgo/360).toString() + " years, " + ((daysAgo%360)/30).toString() + " months"+((daysAgo%360)%30).toString()+" ago)"
                        }
                    }
                }
            },context!!).execute()
        }


        rootLayout.setOnClickListener {
            popupWindow.dismiss()
        }
        TransitionManager.beginDelayedTransition(combineFragment)
        if(!infoOpen) {
            infoOpen = true
            popupWindow.showAtLocation(
                combineFragment, // Location to display popup window
                Gravity.CENTER, // Exact position of layout to display popup
                0, // X offset
                0 // Y offset
            )
        }
    }

    fun openComments(id:String){

        val inflater:LayoutInflater = LayoutInflater.from(context)

        // Inflate a custom view using layout inflater
        val view = inflater.inflate(R.layout.comments,null)

        val popupWindow = PopupWindow(
            view, // Custom view to show in popup window
            LinearLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
            LinearLayout.LayoutParams.WRAP_CONTENT // Window height
        )

        popupWindow.isFocusable = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 25.0F
        }

        view.setOnTouchListener { v, event -> //Close the window when clicked
            popupWindow.dismiss()
            true
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // Create a new slide animation for popup window enter transition
            val fade = android.transition.Fade()

            popupWindow.enterTransition = fade

            // Slide animation for popup window exit transition
            popupWindow.exitTransition = fade

        }


        val chatRV = view.findViewById<RecyclerView>(R.id.chatRVLayout)
        val sendMsgBtn = view.findViewById<TextView>(R.id.sendChat)
        val msgET = view.findViewById<EditText>(R.id.chatMsg)
        msgET.hint="Costs ${localSave.getInt("price/elementComment",5)} \uD83D\uDCB5"
        sendMsgBtn.setOnClickListener {
            val msgByUser = msgET.text.toString().trim()
            if (msgByUser.isNotEmpty()) {
                CustomServer.AddElementComment(user, id, msgByUser,object :ServerListener{
                    override fun runWithValue(value: String) {
                        if(value=="long")
                            Toast.makeText(activity, "Comment exceeds character limit, shorten your message.", Toast.LENGTH_SHORT).show()
                        if(value=="bal")
                            Toast.makeText(activity, "Insufficient balance.", Toast.LENGTH_SHORT).show()
                    }
                },context!!).execute()
                setCommentsChats(id,chatRV)
                msgET.setText("")
            }
        }

        setCommentsChats(id,chatRV)


        TransitionManager.beginDelayedTransition(combineFragment)

        popupWindow.showAtLocation(
            combineFragment, // Location to display popup window
            Gravity.CENTER, // Exact position of layout to display popup
            0, // X offset
            0 // Y offset
        )
    }

    fun setCommentsChats(id:String,chatRV: RecyclerView){
        CustomServer.GetElementComment(id,object :ServerListener{
            override fun runWithValue(value: String) {
                val username:ArrayList<String> = ArrayList()
                val msg:ArrayList<String> = ArrayList()
                val userID:ArrayList<String> = ArrayList()
                val commentID:ArrayList<String> = ArrayList()
                if(value!="{}") {

                    val data=JSONObject(value)
                    val requiredUsernames:ArrayList<String> = ArrayList()
                    data.keys().forEach {
                        commentID.add(it)
                        userID.add(data.getJSONObject(it).getString("user"))
                        if(!requiredUsernames.contains(data.getJSONObject(it).getString("user")))
                            requiredUsernames.add(data.getJSONObject(it).getString("user"))
                    }
                    CustomServer.GetUsername(requiredUsernames.joinToString(","),object :ServerListener{
                        override fun runWithValue(value: String) {
                            val usernames=JSONObject(value)
                            data.keys().forEach {
                                username.add(usernames.getString(data.getJSONObject(it).getString("user")))
                                msg.add(data.getJSONObject(it).getString("msg"))
                            }
                            if(chatRV!=null){
                                chatRV.layoutManager=LinearLayoutManager(context!!)
                                chatRV.adapter=CommentAdapter(username,msg,userID,null,combineFragment,id,commentID,context!!)
                                chatRV.scrollToPosition(username.size-1)
                            }
                        }
                    },context!!).execute()
                }
                else{

                    username.add("No comments yet!")
                    msg.add(" ")
                    userID.add("0")
                    commentID.add("0")
                    if(chatRV!=null){
                        chatRV.layoutManager=LinearLayoutManager(context!!)
                        chatRV.adapter=CommentAdapter(username,msg,userID,null,combineFragment,id,commentID,context!!)
                        chatRV.scrollToPosition(username.size-1)
                    }
                }
            }
        },context!!).execute()
    }

    fun addToCombinationList(position: Int): String {

        localSave.getString("elements/$position/name","Element not found in sync, try again later")?.let { elementTitles.add(it) }
        localSave.getString("elements/$position/color","#55555555")?.let { combinationElementColor.add(it) }
        if(position>3) {
            var combinationID=""
            var combinationIDTree=""
            if(localSave.getString("elements/$position/combinationID","").toString()=="") {
                var i = 0
                while (i < localSave.getLong("totalCombinations", 0)) {
                    if (localSave.getString("combinations/$i/makes", "0").toString()
                            .toIntOrNull() == position
                    ) {
                        val tempComb =
                            localSave.getString("combinations/$i/from", "0").toString().split(",")
                        if (elementsUnlockedID.containsAll(tempComb)) {
                            combinationID =
                                localSave.getString("combinations/$i/from", "0").toString()
                            break
                        }
                    }
                    i++
                }
                localSave.edit().putString("elements/$position/combinationID", combinationID).apply()

                combinationIDTree=combinationID
                combinationID.split(",").forEach {
                    if (it.toIntOrNull() != null) {
                        if(it.toInt()!=position) {
                            if(it.toInt()>3)
                                combinationIDTree = "$combinationIDTree,${addToCombinationList(it.toInt())}"
                            else
                                addToCombinationList(it.toInt())
                            var i=0
                            while(i<4){
                                var current = localSave.getInt("elements/$position/count$i",0)
                                current+=localSave.getInt("elements/$it/count$i",0)
                                localSave.edit().putInt("elements/$position/count$i",current).apply()
                                i++
                            }
                        }
                    }
                }

                localSave.edit().putString("elements/$position/combinationIDTree", combinationIDTree).apply()
            }
            else {
                combinationIDTree = localSave.getString("elements/$position/combinationIDTree", "").toString()
            }
            return combinationIDTree
        }
        else{
            var i=0
            while(i<4){
                if(i==position)
                    localSave.edit().putInt("elements/$position/count$i",1).apply()
                else
                    localSave.edit().putInt("elements/$position/count$i",0).apply()
                i++
            }

        }


        return ""
    }



    private fun setSearch(view: View) {
        view.searchText.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if(!p0.isNullOrEmpty())
                    updateElements(p0.toString())
                else
                    updateElements()

            }

        })
    }

    private fun setCombineButton(view: View) {
        view.combineButton.setOnClickListener {
            if(!combineCS){
                combineCS=true
                var newElement = -1

                val combineSelectedID:ArrayList<Int> = ArrayList()
                val combineSelectedID2:ArrayList<Int> = ArrayList()
                val combineSelectedName:ArrayList<String> = ArrayList()
                val combineSelectedColor:ArrayList<String> = ArrayList()
                combineSelectedID.addAll(selectedElementsID)
                combineSelectedID2.addAll(selectedElementsID)
                combineSelectedName.addAll(selectedElements)
                combineSelectedColor.addAll(colorOfSelectedElements)
                combineSelectedID.sort()
                if(combineSelectedID.isNotEmpty()){
                    if(combineSelectedID.size>1) {
                        CustomServer.CombineElements(user,combineSelectedID.joinToString(","),object :ServerListener{
                            override fun runWithValue(value: String) {
                                try {
                                    val data = JSONObject(value)
                                    if(data.getString("type")=="new"){
                                        if(localSave.getBoolean("resetCombineElementsAfterCombination",true))
                                            selectedElementsID.clear()
                                        setSync(view)
                                        view.balance.text="+ ${data.getString("bal")} \uD83D\uDCB5"
                                        data.getString("elementID").toIntOrNull()?.let { it1 -> showElementInfo(it1) }
                                    }
                                    else if (data.getString("type")=="exist"){
                                        if(localSave.getBoolean("resetCombineElementsAfterCombination",true))
                                            selectedElementsID.clear()
                                        Toast.makeText(activity, "Element already exists", Toast.LENGTH_SHORT).show()
                                        updateElements()
                                        data.getString("elementID").toIntOrNull()?.let { it1 -> showElementInfo(it1) }
                                    }
                                    else if (data.getString("type")=="noCombination"){
                                        Toast.makeText(activity, "No such combination exist! If you think it should make an element, create a vote!", Toast.LENGTH_SHORT).show()
                                        view.suggestNew.visibility = VISIBLE
                                        view.closeSuggestNew.visibility = VISIBLE
                                        newVoteElementID.clear()
                                        newVoteElementColor.clear()
                                        newVoteElementName.clear()
                                        newVoteElementID.addAll(combineSelectedID2)
                                        newVoteElementColor.addAll(combineSelectedColor)
                                        newVoteElementName.addAll(combineSelectedName)
                                        view.suggestNew.text = "Suggest ${newVoteElementName.joinToString("+")}"
                                        soundPool.play(soundCombineFailID, localSave.getFloat("SFXVolume", 1F), localSave.getFloat("SFXVolume", 1F), 0, 0, 1F)
                                    }
                                    else{
                                        Toast.makeText(activity, "Error occurred :(", Toast.LENGTH_SHORT).show()
                                    }
                                }catch (e: Exception) {
                                    Log.e("errorOnCombining", e.stackTrace.joinToString("\n"))
                                }
                                combineCS = false
                            }
                        },context!!).execute()

                    }
                    else{
                        Toast.makeText(activity,"At least two elements required for a combination",Toast.LENGTH_SHORT).show()
                        combineCS=false
                    }
                }
                else{
                    Toast.makeText(activity,"Please select elements to combine",Toast.LENGTH_SHORT).show()
                    combineCS=false
                }

            }


        }
    }


    // TODO: Rename method, update argument and hook method into UI event






    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CombineFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CombineFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

interface ElementClickListener{
    fun elementClicked(pos:Int){}
    fun showInfo(pos:Int){}
}

data class Element(val id:Int,val name:String,val color: String,val isLocked:Boolean=false)
