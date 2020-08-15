package com.caldeirasoft.castly.domain.model.file

import java.io.File
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

interface FileManager {
    fun writeFile(filename: String, content: String)

    fun readFile(localPath: String): File

    fun readFile(file: File): String?
}