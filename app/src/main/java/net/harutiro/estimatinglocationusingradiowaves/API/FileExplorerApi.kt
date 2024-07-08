package net.harutiro.estimatinglocationusingradiowaves.API

import android.content.Context
import android.os.Environment
import java.io.File

class FileExplorerApi(
    private val context: Context,
) {
    val filePath: String = "${context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}"

    // filePathにあるファイル一覧を取得
    fun scanFile(): List<File>{
        val file = File(filePath)
        return file.listFiles()?.toList() ?: emptyList()
    }
}