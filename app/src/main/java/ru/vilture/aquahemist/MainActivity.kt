package ru.vilture.aquahemist

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.*
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), OnDateSelectedListener, OnMonthChangedListener {

    @SuppressLint("SimpleDateFormat")
    private var curDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    private var dbdoc: MutableList<Results> = ArrayList()
    private var values = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mcv.state().edit()
            .setFirstDayOfWeek(Calendar.MONDAY)
            .setCalendarDisplayMode(CalendarMode.MONTHS)
            .commit()

        mcv.currentDate = CalendarDay.today()
        mcv.setDateSelected(CalendarDay.today(), true)
        dbdoc = DBHandler(this).readData(curDate)

        mcv.setOnDateChangedListener(this)
        mcv.setOnMonthChangedListener(this)

        existDate()

        loadResults(dbdoc)

        action.setOnClickListener {
            save(curDate)
        }


        ph.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                rsPH(ph)
                rsNH3(nh3)
                co2()
            }
        })
        kh.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                rsKH(kh)
                co2()
            }
        })
        gh.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                rsGH(gh)
            }
        })
        no2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                rsNO2(no2)
            }
        })
        no3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                rsNO3(no3)
            }
        })
        po4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                rsPO4(po4)
            }
        })
        nh3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                rsNH3(nh3)
            }
        })

        // обновление экрана
        swipe_refresh.setOnRefreshListener {
            swipe_refresh.isRefreshing = true
            swipe_refresh.isRefreshing = false
        }
    }


    override fun onDateSelected(
        widget: MaterialCalendarView,
        date: CalendarDay,
        selected: Boolean
    ) {
        hideKeyboard()

        curDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date.date)
        dbdoc = DBHandler(this).readData(curDate)

        loadResults(dbdoc)
    }

    override fun onMonthChanged(widget: MaterialCalendarView?, date: CalendarDay?) {
        hideKeyboard()
    }

    private fun loadResults(dbdoc: MutableList<Results>) {
        values.clear()

        ph.text.clear()
        gh.text.clear()
        kh.text.clear()
        no2.text.clear()
        no3.text.clear()
        po4.text.clear()
        nh3.text.clear()
        co2.text.clear()

        rsPH.setBackgroundColor(Color.TRANSPARENT)
        rsKH.setBackgroundColor(Color.TRANSPARENT)
        rsGH.setBackgroundColor(Color.TRANSPARENT)
        rsNO2.setBackgroundColor(Color.TRANSPARENT)
        rsNO3.setBackgroundColor(Color.TRANSPARENT)
        rsNH3.setBackgroundColor(Color.TRANSPARENT)
        rsPO4.setBackgroundColor(Color.TRANSPARENT)
        rsPH.text = ""
        rsKH.text = ""
        rsGH.text = ""
        rsNO2.text = ""
        rsNO3.text = ""
        rsNH3.text = ""
        rsPO4.text = ""

        if (dbdoc.isNotEmpty()) {
            for (i in 0 until dbdoc.size) {
                ph.text.append(dbdoc[i].ph)
                gh.text.append(dbdoc[i].gh)
                kh.text.append(dbdoc[i].kh)
                no2.text.append(dbdoc[i].no2)
                no3.text.append(dbdoc[i].no3)
                po4.text.append(dbdoc[i].po4)
                nh3.text.append(dbdoc[i].nh3)
                co2.text.append(dbdoc[i].co2)

                rsPH(ph)
                rsGH(gh)
                rsKH(kh)
                rsNO2(no2)
                rsNO3(no3)
                rsPO4(po4)
                rsNH3(nh3)
                co2()
            }
        }
    }

    private fun save(date: String) {
        values["date"] = date
        values["ph"] = ph.text.toString()
        values["kh"] = kh.text.toString()
        values["gh"] = gh.text.toString()
        values["no2"] = no2.text.toString()
        values["no3"] = no3.text.toString()
        values["po4"] = po4.text.toString()
        values["nh3"] = nh3.text.toString()
        values["co2"] = co2.text.toString()



        val resSave =
            DBHandler(this).addData(values)
        if (!resSave.isOk)
            Toast.makeText(this, resSave.message, Toast.LENGTH_LONG).show()
        else
            Toast.makeText(this, "Сохранено", Toast.LENGTH_LONG).show()
        existDate()
        mcv.invalidateDecorators()
    }

    private fun existDate() {
        val dates = DBHandler(this).getExistDate()

        for (p in dates) {
            mcv.addDecorators(
                CurrentDayDecorator(
                    this,
                    CalendarDay.from(
                        p.substring(0, 4).toInt(),
                        p.substring(4, 6).toInt() - 1,
                        p.substring(6, 8).toInt()
                    )
                )
            )
        }

    }


    private fun hideKeyboard() {
        val imm =
            this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        var view = this.currentFocus
        if (view == null) {
            view = View(this)
        }

        ph.clearFocus()
        ph.isCursorVisible = false
        kh.clearFocus()
        kh.isCursorVisible = false
        gh.clearFocus()
        gh.isCursorVisible = false
        no2.clearFocus()
        no2.isCursorVisible = false
        no3.clearFocus()
        no3.isCursorVisible = false
        po4.clearFocus()
        po4.isCursorVisible = false
        nh3.clearFocus()
        nh3.isCursorVisible = false

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun rsPH(ph: EditText?) {
        if (ph?.text!!.isEmpty()) {
            rsPH.setBackgroundColor(Color.TRANSPARENT)
            rsPH.text = ""
            return
        }
        val double = ph.text.toString().toDouble()
        if (double >= 7.4) rsPH.setBackgroundResource(R.color.orange)
        if (double >= 8.0) rsPH.setBackgroundResource(R.color.red)
        if (double <= 6.7) rsPH.setBackgroundResource(R.color.yellow)
        if (double in 6.8..7.3) rsPH.setBackgroundResource(R.color.green)
    }

    private fun rsKH(kh: EditText?) {
        if (kh?.text!!.isEmpty()) {
            rsKH.setBackgroundColor(Color.TRANSPARENT)
            rsKH.text = ""
            return
        }
        val double = kh.text.toString().toDouble()
        if (double >= 6.1) rsKH.setBackgroundResource(R.color.orange)
        if (double >= 12.0) rsKH.setBackgroundResource(R.color.red)
        if (double <= 2.9) rsKH.setBackgroundResource(R.color.yellow)
        if (double in 3.0..6.0) rsKH.setBackgroundResource(R.color.green)
    }

    private fun rsGH(gh: EditText?) {
        if (gh?.text!!.isEmpty()) {
            rsGH.setBackgroundColor(Color.TRANSPARENT)
            rsGH.text = ""
            return
        }
        val double = gh.text.toString().toDouble()
        if (double >= 8.1) rsGH.setBackgroundResource(R.color.orange)
        if (double >= 10.0) rsGH.setBackgroundResource(R.color.red)
        if (double <= 3.9) rsGH.setBackgroundResource(R.color.yellow)
        if (double in 4.0..8.0) rsGH.setBackgroundResource(R.color.green)
    }

    private fun rsNO2(no2: EditText?) {
        if (no2?.text!!.isEmpty()) {
            rsNO2.setBackgroundColor(Color.TRANSPARENT)
            rsNO2.text = ""
            return
        }
        val double = no2.text.toString().toDouble()
        if (double >= 0.3) rsNO2.setBackgroundResource(R.color.orange)
        if (double >= 2.0) rsNO2.setBackgroundResource(R.color.red)
        if (double == 0.0) rsNO2.setBackgroundResource(R.color.green)
        if (double in 0.1..0.5) rsNO2.setBackgroundResource(R.color.yellow)
    }

    private fun rsNO3(no3: EditText?) {
        if (no3?.text!!.isEmpty()) {
            rsNO3.setBackgroundColor(Color.TRANSPARENT)
            rsNO3.text = ""
            return
        }
        val double = no3.text.toString().toDouble()
        if (double >= 5.1) rsNO3.setBackgroundResource(R.color.yellow)
        if (double >= 10.1) rsNO3.setBackgroundResource(R.color.orange)
        if (double >= 40.0) rsNO3.setBackgroundResource(R.color.red)
        if (double in 0.0..5.0) rsNO3.setBackgroundResource(R.color.green)
    }

    private fun rsPO4(po4: EditText?) {
        if (po4?.text!!.isEmpty()) {
            rsPO4.setBackgroundColor(Color.TRANSPARENT)
            rsPO4.text = ""
            return
        }
        val double = po4.text.toString().toDouble()
        if (double <= 0.4) rsPO4.setBackgroundResource(R.color.yellow)
        if (double >= 2.1) rsPO4.setBackgroundResource(R.color.orange)
        if (double >= 5.0) rsPO4.setBackgroundResource(R.color.red)
        if (double in 0.5..2.0) rsPO4.setBackgroundResource(R.color.green)
    }

    private fun rsNH3(nh3: EditText?) {
        if (nh3?.text!!.isEmpty() || ph?.text!!.isEmpty()) {
            rsNH3.setBackgroundColor(Color.TRANSPARENT)
            rsNH3.text = ""
            return
        }
        val double = nh3.text.toString().toDouble()
        if (double == 0.0) {
            rsNH3.setBackgroundResource(R.color.white)
            rsNH3.text = "0.000"
        }

        if (double in 0.1..1.9) {
            if (ph.text.toString().toDouble() in 0.0..7.4) {
                rsNH3.setBackgroundResource(R.color.white)
                rsNH3.text = "0.006"
            }
            if (ph.text.toString().toDouble() in 7.5..7.9) {
                rsNH3.setBackgroundResource(R.color.white)
                rsNH3.text = "0.018"
            }
            if (ph.text.toString().toDouble() in 8.0..8.4) {
                rsNH3.setBackgroundResource(R.color.yellow)
                rsNH3.text = "0.054"
            }
            if (ph.text.toString().toDouble() in 8.5..8.9) {
                rsNH3.setBackgroundResource(R.color.yellow)
                rsNH3.text = "0.154"
            }
            if (ph.text.toString().toDouble() in 9.0..9.4) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = "0.365"
            }
            if (ph.text.toString().toDouble() in 9.5..9.9) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = "0.645"
            }
            if (ph.text.toString().toDouble() >= 10) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = ">0.852"
            }
        }


        if (double in 2.0..4.9) {
            if (ph.text.toString().toDouble() in 0.0..7.4) {
                rsNH3.setBackgroundResource(R.color.white)
                rsNH3.text = "0.014"
            }
            if (ph.text.toString().toDouble() in 7.5..7.9) {
                rsNH3.setBackgroundResource(R.color.yellow)
                rsNH3.text = "0.036"
            }
            if (ph.text.toString().toDouble() in 8.0..8.4) {
                rsNH3.setBackgroundResource(R.color.yellow)
                rsNH3.text = "0.109"
            }
            if (ph.text.toString().toDouble() in 8.5..8.9) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = "0.308"
            }
            if (ph.text.toString().toDouble() in 9.0..9.4) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = "0.731"
            }
            if (ph.text.toString().toDouble() in 9.5..9.9) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = "1.230"
            }
            if (ph.text.toString().toDouble() >= 10) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = ">1.704"
            }
        }

        if (double in 5.0..9.9) {
            if (ph.text.toString().toDouble() in 0.0..7.4) {
                rsNH3.setBackgroundResource(R.color.yellow)
                rsNH3.text = "0.029"
            }
            if (ph.text.toString().toDouble() in 7.5..7.9) {
                rsNH3.setBackgroundResource(R.color.yellow)
                rsNH3.text = "0.090"
            }
            if (ph.text.toString().toDouble() in 8.0..8.4) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = "0.272"
            }
            if (ph.text.toString().toDouble() in 8.5..8.9) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = "0.770"
            }
            if (ph.text.toString().toDouble() in 9.0..9.4) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = "1.827"
            }
            if (ph.text.toString().toDouble() in 9.5..9.9) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = "3.227"
            }
            if (ph.text.toString().toDouble() >= 10) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = ">4.260"
            }
        }

        if (double >= 10.0) {
            if (ph.text.toString().toDouble() in 0.0..7.4) {
                rsNH3.setBackgroundResource(R.color.yellow)
                rsNH3.text = "0.057"
            }
            if (ph.text.toString().toDouble() in 7.5..7.9) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = "0.179"
            }
            if (ph.text.toString().toDouble() in 8.0..8.4) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = "0.544"
            }
            if (ph.text.toString().toDouble() in 8.5..8.9) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = "1.540"
            }
            if (ph.text.toString().toDouble() in 9.0..9.4) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = "3.653"
            }
            if (ph.text.toString().toDouble() in 9.5..9.9) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = "6.454"
            }
            if (ph.text.toString().toDouble() >= 10) {
                rsNH3.setBackgroundResource(R.color.red)
                rsNH3.text = ">8.529"
            }
        }

    }

    private fun co2() {
        if (kh?.text!!.isEmpty() || ph?.text!!.isEmpty()) {
            co2.setBackgroundColor(Color.TRANSPARENT)
            co2.text.append("Введите pH и kH значения")
            return
        }

        co2.text.clear()
        val dph = ph.text.toString().toDouble()
        val dkh = kh.text.toString().toDouble()

        if (dkh in 0.0..0.9) {
            if (dph <= 6.1) {
                co2.setBackgroundResource(R.color.green)
                co2.text.append("Оптимально")
            } else {
                co2.setBackgroundResource(R.color.blue)
                co2.text.append("Мало")
            }
        }

        if (dkh in 1.0..1.9) {
            if (dph <= 6.3) {
                co2.setBackgroundResource(R.color.green)
                co2.text.append("Оптимально")
            } else {
                co2.setBackgroundResource(R.color.blue)
                co2.text.append("Мало")
            }
        }

        if (dkh in 2.0..2.9) {
            if (dph <= 6.3) {
                co2.setBackgroundResource(R.color.red)
                co2.text.append("Много")

            }
            if (dph in 6.4..6.6) {
                co2.setBackgroundResource(R.color.green)
                co2.text.append("Оптимально")
            }
            if (dph >= 6.7) {
                co2.setBackgroundResource(R.color.blue)
                co2.text.append("Мало")
            }
        }

        if (dkh in 3.0..3.9) {
            if (dph <= 6.3) {
                co2.setBackgroundResource(R.color.red)
                co2.text.append("Много")

            }
            if (dph in 6.4..6.9) {
                co2.setBackgroundResource(R.color.green)
                co2.text.append("Оптимально")
            }
            if (dph >= 7.0) {
                co2.setBackgroundResource(R.color.blue)
                co2.text.append("Мало")
            }
        }

        if (dkh in 4.0..5.9) {
            if (dph <= 6.6) {
                co2.setBackgroundResource(R.color.red)
                co2.text.append("Много")

            }
            if (dph in 6.7..7.1) {
                co2.setBackgroundResource(R.color.green)
                co2.text.append("Оптимально")
            }
            if (dph >= 7.2) {
                co2.setBackgroundResource(R.color.blue)
                co2.text.append("Мало")
            }
        }

        if (dkh in 6.0..6.9) {
            if (dph <= 6.9) {
                co2.setBackgroundResource(R.color.red)
                co2.text.append("Много")

            }
            if (dph in 7.0..7.1) {
                co2.setBackgroundResource(R.color.green)
                co2.text.append("Оптимально")
            }
            if (dph >= 7.2) {
                co2.setBackgroundResource(R.color.blue)
                co2.text.append("Мало")
            }
        }

        if (dkh in 7.0..10.9) {
            if (dph <= 6.9) {
                co2.setBackgroundResource(R.color.red)
                co2.text.append("Много")

            }
            if (dph in 7.0..7.3) {
                co2.setBackgroundResource(R.color.green)
                co2.text.append("Оптимально")
            }
            if (dph >= 7.4) {
                co2.setBackgroundResource(R.color.blue)
                co2.text.append("Мало")
            }
        }

        if (dkh in 11.0..12.9) {
            if (dph <= 7.1) {
                co2.setBackgroundResource(R.color.red)
                co2.text.append("Много")

            }
            if (dph in 7.2..7.3) {
                co2.setBackgroundResource(R.color.green)
                co2.text.append("Оптимально")
            }
            if (dph >= 7.4) {
                co2.setBackgroundResource(R.color.blue)
                co2.text.append("Мало")
            }
        }

        if (dkh in 13.0..18.9) {
            if (dph <= 7.1) {
                co2.setBackgroundResource(R.color.red)
                co2.text.append("Много")

            }
            if (dph in 7.2..7.7) {
                co2.setBackgroundResource(R.color.green)
                co2.text.append("Оптимально")
            }
            if (dph >= 7.8) {
                co2.setBackgroundResource(R.color.blue)
                co2.text.append("Мало")
            }
        }

        if (dkh >= 19.0) {
            if (dph <= 7.3) {
                co2.setBackgroundResource(R.color.red)
                co2.text.append("Много")

            }
            if (dph in 7.4..7.7) {
                co2.setBackgroundResource(R.color.green)
                co2.text.append("Оптимально")
            }
            if (dph >= 7.8) {
                co2.setBackgroundResource(R.color.blue)
                co2.text.append("Мало")
            }
        }
    }

}

class CurrentDayDecorator(context: Activity, currentDay: CalendarDay) : DayViewDecorator {
    private val drawable: Drawable?
    private var myDay = currentDay
    private val colorDrawable = ColorDrawable(ContextCompat.getColor(context, R.color.colorExist))

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return day == myDay
    }

    override fun decorate(view: DayViewFacade) {
        view.setSelectionDrawable(drawable!!)
    }

    init {
        drawable = colorDrawable
    }
}

