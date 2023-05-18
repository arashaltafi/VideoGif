package com.arash.altafi.videogif.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.arash.altafi.videogif.R
import com.arash.altafi.videogif.utils.MyConstants.CUSTOM_MENU_ITEM_VIEW_TYPE_GIF_FINAL_DELAY
import com.arash.altafi.videogif.utils.MyConstants.GIF_FINAL_DELAY_MAP
import com.arash.altafi.videogif.databinding.ActivitySettingsBinding
import com.arash.altafi.videogif.utils.MySettings

class SettingsActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySettingsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.materialToolbar)
        setFinishOnTouchOutside(true)
        with(binding.msRememberGifOptions) {
            isChecked = MySettings.rememberGifOptions
            setOnCheckedChangeListener { _, isChecked ->
                MySettings.rememberGifOptions = isChecked
                if (!isChecked) {
                    with(MySettings) {
                        previousGifConfigSpeed = INT_PREVIOUS_GIF_CONFIG_UNKNOWN_VALUE
                        previousGifConfigResolution = INT_PREVIOUS_GIF_CONFIG_UNKNOWN_VALUE
                        previousGifConfigFrameRate = INT_PREVIOUS_GIF_CONFIG_UNKNOWN_VALUE
                        previousGifConfigColorQuality = INT_PREVIOUS_GIF_CONFIG_UNKNOWN_VALUE
                    }
                }
            }
        }
        with(binding.msAnalyzeVideoSlowly) {
            isChecked = MySettings.analyzeVideoSlowly
            setOnCheckedChangeListener { _, isChecked ->
                MySettings.analyzeVideoSlowly = isChecked
            }
        }
        with(binding.msAlwaysShowMoreOptionsWhenConvertingGif) {
            isChecked = MySettings.alwaysShowMoreOptionsWhenConvertingGif
            setOnCheckedChangeListener { _, isChecked ->
                MySettings.alwaysShowMoreOptionsWhenConvertingGif = isChecked
            }
        }
        binding.cmivFinalDelay.apply {
            setUpWithDropDownConfig(GIF_FINAL_DELAY_MAP, CUSTOM_MENU_ITEM_VIEW_TYPE_GIF_FINAL_DELAY)
            setSelectedValue(MySettings.gifFinalDelay)
        }
        binding.mbDone.setOnClickListener { finish() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_close, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_close -> finish()
        }
        return true
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }
}