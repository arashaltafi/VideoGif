package com.arash.altafi.videogif.utils

import android.annotation.SuppressLint
import android.content.*
import android.content.res.ColorStateList
import android.database.Cursor
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.arash.altafi.videogif.BuildConfig
import com.arash.altafi.videogif.App.Companion.context
import java.io.File
import java.util.*

object MyToolbox {

    fun <K, V> LinkedHashMap<K, V>.getKeyByValue(value: V): K =
        this.filter { it.value == value }.keys.first()

    @SuppressLint("SimpleDateFormat")
    fun getTimeYMDHMS(): String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

    fun logging(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg)
        }
    }

    fun createColorStateList(list: Array<Pair<Int, Int>>) =
        ColorStateList(
            list.map { intArrayOf(it.first) }.toTypedArray(),
            list.map { context.resources.getColor(it.second, context.theme) }.toIntArray()
        )

    fun keepNDecimalPlaces(double: Double, n: Int) = String.format("%.${n}f", double)

    @SuppressLint("Recycle")
    fun getFileSizeFromUri(uri: Uri) = when (uri.scheme) {
        "content" -> context.contentResolver.openAssetFileDescriptor(uri, "r")!!.length
        "file" -> File(uri.path!!).length()
        else -> throw IllegalArgumentException("uri.scheme = ${uri.scheme}")
    }

    @SuppressLint("Range")
    fun getFileNameFromUri(uri: Uri, removeSuffix: Boolean): String {
        val fileName: String
        val cursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        cursor.moveToFirst()
        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        cursor.close()
        return if (!fileName.contains('.') || !removeSuffix) {
            fileName
        } else {
            with(fileName) { substring(0, lastIndexOf('.')) }
        }
    }
}
