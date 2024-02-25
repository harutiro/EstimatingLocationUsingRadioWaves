package net.harutiro.estimatinglocationusingradiowaves.API

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import net.harutiro.estimatinglocationusingradiowaves.Utils.DateUtils
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter

class OtherFileStorageApi(
    private val context: Context,
    name: String,
    private val queue: ArrayDeque<String>
) {

    //true=追記, false=上書き
    val fileAppend: Boolean = true

    // 全てのfileの前につける名前（自由）
    val fileNameBace: String = DateUtils.getNowDate()
    var fileName: String = fileNameBace.plus(name)

    // 拡張子
    val extension: String = ".csv"

    //内部ストレージのDocumentのURL
    val filePath: String =
        "${context.applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/${fileName}${extension}"

    // Queueのデータをファイルに保存する周期
    val delayMillis: Long = 1000L

    // Handler のオブジェクトを生成
    val handler = Handler(Looper.getMainLooper())
    val runnable = object : Runnable {
        override fun run() {
            // 1:コピー作成
            val pressureCopy = queue.toArray()
            // 2:本体をクリア
            queue.clear()
            // 3:コピーをファイルに書き込む
            saveArrayDeque(pressureCopy)
            // 指定時間毎に繰り返す
            handler.postDelayed(this, delayMillis)
        }
    }

    // 一行書き込むやつ．今回は使わない．
    fun writeText(text: String) {
        val fil = FileWriter(filePath, fileAppend)
        val pw = PrintWriter(BufferedWriter(fil))
        pw.println(text)
        pw.close()
    }

    // 別スレッドを Runnable で作成
    fun saveAtBatch() {
        // 別スレッドを実行
        handler.post(runnable)
    }

    fun stop() {
        // 別スレッドを停止
        handler.removeCallbacks(runnable)
    }

    fun saveArrayDeque(copy: Array<Any?>) {
        val fil = FileWriter(filePath, fileAppend)
        val pw = PrintWriter(BufferedWriter(fil))
        for (item in copy) {
            pw.println(item.toString())
        }
        pw.close()
    }

}