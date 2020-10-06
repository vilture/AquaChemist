package ru.vilture.aquahemist

import android.content.Context
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*


object Diagnostics {
    const val TAG = "Diagnostics"
    var DEBUG = BuildConfig.DEBUG
    var Context: Context? = null
    var FileName = ""
    fun i(msg: String?): DiagnosticLog {
        return i(TAG, msg)
    }

    fun i(tag: String?, msg: String?): DiagnosticLog {
        var msg = Date().toString() + " " + msg
        msg = if (TextUtils.isEmpty(msg)) "" else msg
        Log.i(tag, msg)
        return DiagnosticLog(msg)
    }

    fun e(tag: String?, msg: String?): DiagnosticLog {
        var msg = Date().toString() + " " + msg
        msg = if (TextUtils.isEmpty(msg)) "" else msg
        Log.e(tag, msg)
        return DiagnosticLog(msg)
    }

    fun i(caller: Any, msg: String): DiagnosticLog {
        return i(caller, TAG, msg)
    }

    fun i(caller: Any, tag: String?, msg: String): DiagnosticLog {
        return i(tag, caller.javaClass.simpleName + "." + msg)
    }

    fun e(caller: Any, msg: String): DiagnosticLog {
        return e(caller, TAG, msg)
    }

    fun e(caller: Any, tag: String?, msg: String): DiagnosticLog {
        return e(tag, caller.javaClass.simpleName + "." + msg)
    }

    fun e(msg: String?): DiagnosticLog {
        return e(TAG, msg)
    }

    fun getLogFile(filename: String): File? {
        return File(Environment.getExternalStorageDirectory(), "$filename.log")
    }

    fun getContext(context: Context) {
        Context = context
    }

    fun createLog(fname: String) {
        var filename = ""
        if (fname.isEmpty()) {
            try {
                if (Context == null)
                    throw Exception("Не задан контекст логгирования")

                if (BuildConfig.DEBUG) {
                    filename = "LOG_DEBUG"
                } else {
                    filename = "LOG_RELEASE"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            filename = fname
        }

        val file = getLogFile(filename)
        if (file!!.exists()){
            // наращиваем файл до 500kб
            if (file.length() / 1024 >= 500) {
                file.delete()

                try {
                    file.createNewFile()
                    appendLog(filename, "Created at " + Date().toString())
                    FileName = filename
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                appendLog(filename, "продолжаем лог после закрытия")
            }
        } else {
            try {
                file.createNewFile()
                appendLog(filename, "Created at " + Date().toString())
                FileName = filename
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    fun appendLog(filename: String, line: String?) {
        val file = getLogFile(filename)
        if (!file!!.exists()) createLog(filename)
        try {
            val bufferedWriter =
                BufferedWriter(FileWriter(file, true))
            bufferedWriter.write(line)
            bufferedWriter.newLine()
            bufferedWriter.flush()
            bufferedWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    class DiagnosticLog(private val msg: String?) {
        fun append() {
            appendLog(FileName, msg)
        }

    }
}