package com.arash.altafi.videogif.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.LinearLayoutCompat
import com.arash.altafi.videogif.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.Slider
import com.google.android.material.textview.MaterialTextView
import com.arash.altafi.videogif.utils.MyConstants.CUSTOM_MENU_ITEM_VIEW_TYPE_GIF_CHIPS
import com.arash.altafi.videogif.utils.MyConstants.CUSTOM_MENU_ITEM_VIEW_TYPE_GIF_FINAL_DELAY
import com.arash.altafi.videogif.utils.MyConstants.CUSTOM_MENU_ITEM_VIEW_TYPE_GIF_SPEED
import com.arash.altafi.videogif.utils.MyConstants.UNKNOWN_INT
import com.arash.altafi.videogif.utils.MyToolbox.getKeyByValue
import com.arash.altafi.videogif.databinding.ViewCustomMenuItemBinding

class CustomMenuItemView(context: Context, attrs: AttributeSet) :
    LinearLayoutCompat(context, attrs) {

    private val binding: ViewCustomMenuItemBinding
    private val mtvTitle: MaterialTextView
    private val mtvSubTitle: MaterialTextView
    private val mtvSelectedKey: MaterialTextView
    private var customMenuItemViewType: Int = UNKNOWN_INT
    private lateinit var allOptionsMap: LinkedHashMap<String, Int>
    private lateinit var sliderInPopupView: Slider
    private lateinit var chipGroup: ChipGroup

    init {
        binding = ViewCustomMenuItemBinding.inflate(LayoutInflater.from(context), this, true)
        mtvTitle = binding.mtvTitle
        mtvSubTitle = binding.mtvSubTitle
        mtvSelectedKey = binding.mtvSelectedKey
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CustomMenuItemView,
            0, 0
        ).apply {
            mtvTitle.text = getString(R.styleable.CustomMenuItemView_title)
            mtvSubTitle.text = getString(R.styleable.CustomMenuItemView_subTitle)
            mtvSelectedKey.text = getString(R.styleable.CustomMenuItemView_selectedKey)
            recycle()
        }
        if (!mtvSubTitle.text.isNullOrBlank()) {
            mtvSubTitle.visibility = VISIBLE
        } else {
            mtvSubTitle.visibility = GONE
        }
        if (mtvSelectedKey.text.isNullOrBlank()) {
            mtvSelectedKey.visibility = GONE
        } else {
            mtvSelectedKey.visibility = VISIBLE
        }
    }

    fun selectedValue() = allOptionsMap[mtvSelectedKey.text]!!

    @SuppressLint("InflateParams")
    fun setUpWithDropDownConfig(
        allOptionsMap: LinkedHashMap<String, Int>,
        customMenuItemViewType: Int,
    ) {
        this.allOptionsMap = allOptionsMap
        this.customMenuItemViewType = customMenuItemViewType
        when (customMenuItemViewType) {
            CUSTOM_MENU_ITEM_VIEW_TYPE_GIF_SPEED -> {
                val popupView =
                    LayoutInflater.from(context).inflate(R.layout.view_popup_config_slider, null)
                val popupWindow by lazy {
                    PopupWindow(
                        popupView,
                        binding.root.width,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        true
                    ).apply {
                        elevation = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            MyConstants.POPUP_WINDOW_ELEVATION,
                            resources.displayMetrics
                        )
                        isOutsideTouchable = true
                    }
                }
                sliderInPopupView = popupView.findViewById<Slider>(R.id.slider).apply {
                    addOnChangeListener { it, value, _ ->
                        val speedText = allOptionsMap.keys.elementAt((value).toInt())
                        mtvSelectedKey.text = speedText
                        popupView.findViewById<MaterialTextView>(R.id.mtv_guide).apply {
                            if (selectedValue() == MyConstants.GIF_SPEED_GLANCE_MODE) {
                                text = context.getString(R.string.glance_mode_guide_text)
                                visibility = VISIBLE
                            } else {
                                visibility = GONE
                            }
                        }
                        it.setLabelFormatter {
                            return@setLabelFormatter speedText
                        }
                    }
                    valueFrom = 0f
                    valueTo = MyConstants.GIF_SPEED_MAP.size.toFloat() - 1
                    value = MyConstants.GIF_SPEED_MAP.keys.indexOf(mtvSelectedKey.text).toFloat()
                    setLabelFormatter { return@setLabelFormatter mtvSelectedKey.text.toString() }
                }
                binding.root.setOnClickListener {
                    popupWindow.showAsDropDown(it)
                }
            }
            CUSTOM_MENU_ITEM_VIEW_TYPE_GIF_CHIPS -> {
                val menuItemList = allOptionsMap.keys
                val popupView =
                    LayoutInflater.from(context).inflate(R.layout.view_popup_config_chips, null)
                val popupWindow by lazy {
                    PopupWindow(
                        popupView,
                        binding.root.width,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        true
                    ).apply {
                        elevation = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            MyConstants.POPUP_WINDOW_ELEVATION,
                            resources.displayMetrics
                        )
                        isOutsideTouchable = true
                    }
                }
                chipGroup = popupView.findViewById(R.id.chip_group)
                menuItemList.forEach { menuItemName ->
                    chipGroup.addView(
                        Chip(
                            ContextThemeWrapper(
                                context,
                                com.google.android.material.R.style.Widget_Material3_Chip_Filter
                            )
                        ).apply {
                            text = menuItemName
                            chipBackgroundColor = MyToolbox.createColorStateList(
                                arrayOf(
                                    android.R.attr.state_checked to R.color.blue_light,
                                    android.R.attr.state_checkable to R.color.light
                                )
                            )
                            isCheckable = true
                            isCheckedIconVisible = false
                            if (text == mtvSelectedKey.text) {
                                isChecked = true
                            }
                            setOnClickListener { clickedChip ->
                                (clickedChip as Chip).isChecked = true
                                mtvSelectedKey.text = clickedChip.text
                                popupWindow.dismiss()
                            }
                        }
                    )
                }
                binding.root.setOnClickListener {
                    popupWindow.showAsDropDown(it)
                }
            }
            CUSTOM_MENU_ITEM_VIEW_TYPE_GIF_FINAL_DELAY -> {
                val popupView =
                    LayoutInflater.from(context).inflate(R.layout.view_popup_config_slider, null)
                val popupWindow by lazy {
                    PopupWindow(
                        popupView,
                        binding.root.width,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        true
                    ).apply {
                        elevation = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            MyConstants.POPUP_WINDOW_ELEVATION,
                            resources.displayMetrics
                        )
                        isOutsideTouchable = true
                    }
                }
                sliderInPopupView = popupView.findViewById<Slider>(R.id.slider).apply {
                    addOnChangeListener { it, value, _ ->
                        val selectedText = allOptionsMap.keys.elementAt((value).toInt())
                        mtvSelectedKey.text = selectedText
                        MySettings.gifFinalDelay = selectedValue()
                        it.setLabelFormatter {
                            return@setLabelFormatter selectedText
                        }
                    }
                    valueTo = allOptionsMap.size.toFloat() - 1
                    value = allOptionsMap.keys.indexOf(mtvSelectedKey.text).toFloat()
                    setLabelFormatter { return@setLabelFormatter mtvSelectedKey.text.toString() }
                }
                binding.root.setOnClickListener {
                    popupWindow.showAsDropDown(it)
                }
            }
            else -> {
                throw IllegalArgumentException("customMenuItemViewType = $customMenuItemViewType")
            }
        }
    }

    fun setSelectedValue(value: Int) {
        mtvSelectedKey.text = allOptionsMap.getKeyByValue(value)
        when (customMenuItemViewType) {
            CUSTOM_MENU_ITEM_VIEW_TYPE_GIF_SPEED, CUSTOM_MENU_ITEM_VIEW_TYPE_GIF_FINAL_DELAY -> {
                sliderInPopupView.value = allOptionsMap.values.indexOf(value).toFloat()
            }
            CUSTOM_MENU_ITEM_VIEW_TYPE_GIF_CHIPS -> {
                val outViews: ArrayList<View> = ArrayList()
                chipGroup.findViewsWithText(
                    outViews,
                    allOptionsMap.getKeyByValue(value),
                    FIND_VIEWS_WITH_TEXT
                )
                (outViews.first() as Chip).isChecked = true
            }
            else -> {
                throw IllegalArgumentException("customMenuItemViewType = $customMenuItemViewType")
            }
        }
    }
}