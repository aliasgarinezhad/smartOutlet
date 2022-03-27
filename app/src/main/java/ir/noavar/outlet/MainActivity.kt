package ir.noavar.outlet

import android.content.Intent
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    private lateinit var btn_on: Button
    private lateinit var btn_off: Button
    private lateinit var btn_local: Button
    private lateinit var switchlist: Spinner
    private var devices = mutableListOf<Device>()

    override fun onResume() {
        super.onResume()
        setContentView(R.layout.activity_main)
        switchlist = findViewById(R.id.am_sp_sw)
        btn_on = findViewById(R.id.main_btn_on)
        btn_off = findViewById(R.id.main_btn_off)
        btn_local = findViewById(R.id.main_btn_local)
        val fab = findViewById<TextView>(R.id.fab)

        loadFromMemory()

        btn_on.setOnClickListener {

            val deviceIndex = devices.indexOfLast {
                it.name == FunctionsClass.numToEnglish(
                    switchlist.selectedItem.toString()
                )
            }

            setonoff(devices[deviceIndex].serialNumber, devices[deviceIndex].password, "1")
        }
        btn_off.setOnClickListener {

            val deviceIndex = devices.indexOfLast {
                it.name == FunctionsClass.numToEnglish(
                    switchlist.selectedItem.toString()
                )
            }

            setonoff(devices[deviceIndex].serialNumber, devices[deviceIndex].password, "0")
        }
        btn_local.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    LocalActivity::class.java
                )
            )
        }
        fab.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AddActivity::class.java
                )
            )
        }
        setSearchLayout()
    }

    fun setSearchLayout() {

        val switch1 = mutableListOf("انتخاب کنید")
        for (i in 1 until devices.size) {
            switch1.add(FunctionsClass.numToFarsi(devices[i].name))
        }
        val adapter = MySpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, switch1)
        switchlist.adapter = adapter
    }

    private fun setonoff(sn: String?, pass: String?, status: String) {
        apiService = ApiService(this)
        apiService.setOnOff(sn, pass, status) { ok: String, msg: String? ->
            if (ok.equals("true", ignoreCase = true)) {
                FunctionsClass.showErrorSnak(this, FunctionsClass.getServerErrors(msg))
            } else {
                FunctionsClass.showErrorSnak(this, FunctionsClass.getServerErrors(msg))
            }
        }
    }

    private fun loadFromMemory() {

        val type = object : TypeToken<List<Device>>() {}.type
        val memory = PreferenceManager.getDefaultSharedPreferences(this)
        devices = Gson().fromJson(
            memory.getString("devices", ""),
            type
        ) ?: mutableListOf()
    }
}