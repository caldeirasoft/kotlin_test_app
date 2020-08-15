package com.caldeirasoft.basicapp.data.model.file

import android.content.Context
import android.text.style.TtsSpan
import com.caldeirasoft.castly.domain.model.file.FileManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.*
import java.lang.StringBuilder
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

class FileManagerImpl(private val context: Context) : FileManager {
    override fun writeFile(filename: String, content: String) {
        val dir = context.cacheDir
        File(dir, filename).also {
            it.writeText(content)
        }
    }

    override fun readFile(localPath: String): File =
            File(context.cacheDir, localPath)

    override fun readFile(file: File): String? {
        try {
            val text = StringBuilder()
            file.forEachLine { line ->
                text.append(line)
            }
            return text.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}