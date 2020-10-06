package ru.vilture.aquahemist

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.util.*
import kotlin.collections.ArrayList

class DBHandler(private var context: Context) :
    SQLiteOpenHelper(
        context,
        DATABASE_NAME, null,
        DATABASE_VERS
    ) {
    override fun onCreate(db: SQLiteDatabase?) {
        try {
            val sql1 =
                "CREATE TABLE Results (date TEXT NOT NULL,ph TEXT," +
                        "gh TEXT,kh TEXT,no2 TEXT,no3 TEXT,po4 TEXT,nh3 TEXT,co2 TEXT,PRIMARY KEY(date))"

            db?.execSQL(sql1)

        } catch (e: SQLException) {
            Diagnostics.e(this, "Ошибка создании БД").append()
            Toast.makeText(context, "Ошибка создании БД", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        try {
            when (oldVersion) {
                1 -> {
                }

            }
        } catch (e: SQLException) {
            Diagnostics.e(this, "Ошибка при обновлении БД").append()
            Toast.makeText(context, "Ошибка при обновлении БД", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val DATABASE_NAME = "aquachemist"
        const val DATABASE_VERS = 1
        const val TAB_NAME = "Results"
    }


    private fun getColValue(sqlres: Cursor, colname: String): String {
        return sqlres.getString(sqlres.getColumnIndex(colname)) ?: ""
    }

    @SuppressLint("SimpleDateFormat")
    fun readData(date: String): MutableList<Results> {
        val list: MutableList<Results> = ArrayList()
        val rdb = this.readableDatabase

        val result =
            rdb.query(
                TAB_NAME, null, "date=?", arrayOf(date), null, null, null
            )

        if (result.moveToFirst() && result != null) {
            do {
                val actions = Results()

                actions.date = result.getString(result.getColumnIndex("date"))
                actions.ph = result.getString(result.getColumnIndex("ph"))
                actions.gh = result.getString(result.getColumnIndex("gh"))
                actions.kh = result.getString(result.getColumnIndex("kh"))
                actions.no2 = result.getString(result.getColumnIndex("no2"))
                actions.no3 = result.getString(result.getColumnIndex("no3"))
                actions.po4 = result.getString(result.getColumnIndex("po4"))
                actions.nh3 = result.getString(result.getColumnIndex("nh3"))
                actions.co2 = result.getString(result.getColumnIndex("co2"))

                list.add(actions)

            } while (result.moveToNext())
        }

        result.close()
        rdb.close()
        return list
    }

    fun addData(values: HashMap<String, String>): DbHandlerResult {
        val res = DbHandlerResult()
        var insert = false
        val cv = ContentValues()
        val rdb = this.readableDatabase
        val wdb = this.writableDatabase

        try {
            for (p in values.entries) {
                when (p.key) {
                    "date" -> cv.put("date", p.value)
                    "ph" -> cv.put("ph", p.value)
                    "gh" -> cv.put("gh", p.value)
                    "kh" -> cv.put("kh", p.value)
                    "no2" -> cv.put("no2", p.value)
                    "no3" -> cv.put("no3", p.value)
                    "po4" -> cv.put("po4", p.value)
                    "nh3" -> cv.put("nh3", p.value)
                    "co2" -> cv.put("co2", p.value)
                }
                if (p.value.isNotEmpty() && (p.key != "date" && p.key != "co2"))
                    insert = true
            }

            val curr = rdb.query(
                TAB_NAME,
                null,
                "date=?",
                arrayOf(cv["date"].toString()),
                null,
                null,
                null,
                null
            )
            val exists = curr.count > 0
            curr.close()
            if (exists) {
                wdb.delete(TAB_NAME, "date=?", arrayOf(cv["date"].toString()))
            }

            if (insert) {
                if (wdb.insert(TAB_NAME, null, cv) == -1L) {
                    throw Exception("Ошибка записи в БД")
                }
            }

            res.isOk = true
        } catch (ex: Exception) {
            ex.printStackTrace()
            res.message = ex.message.toString()
            res.isOk = false
        } finally {
            wdb.close()
        }

        return res
    }


    fun getExistDate(): ArrayList<String> {
        var list = ArrayList<String>()
        val rdb = this.readableDatabase

        val result =
            rdb.query(
                TAB_NAME, null, null, null, null, null, null
            )

        if (result.moveToFirst() && result != null) {
            do {
                list.add(result.getString(result.getColumnIndex("date")))
            } while (result.moveToNext())
        }

        result.close()
        rdb.close()
        return list
    }
}

class Results {
    var date: String = ""
    var ph: String = ""
    var gh: String = ""
    var kh: String = ""
    var no2: String = ""
    var no3: String = ""
    var po4: String = ""
    var nh3: String = ""
    var co2: String = ""
}

class DbHandlerResult {
    var isOk: Boolean = false
    var message: String = ""
}