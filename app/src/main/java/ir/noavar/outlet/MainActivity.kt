package ir.noavar.outlet

import androidx.appcompat.app.AppCompatActivity
import ir.noavar.outlet.ApiService
import android.widget.Spinner
import ir.noavar.outlet.R
import android.widget.TextView
import android.content.Intent
import android.view.View
import android.widget.Button
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.noavar.outlet.LocalActivity
import ir.noavar.outlet.AddActivity
import ir.noavar.outlet.switchlist
import ir.noavar.outlet.FunctionsClass
import ir.noavar.outlet.MySpinnerAdapter
import ir.noavar.outlet.ApiService.OnOffCheckRecieved
import java.util.ArrayList

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
                    this@MainActivity,
                    LocalActivity::class.java
                )
            )
        }
        fab.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    AddActivity::class.java
                )
            )
        }
        setSearchLayout()
    }

    fun setSearchLayout() {
        val switchList: List<switchlist>? = null /*dataBaseOpenHelper.getSwitchList();*/
        val switch1: MutableList<String> = ArrayList()
        switch1.add("انتخاب کنید")
        for (i in 1 until switchList!!.size) {
            switch1.add(FunctionsClass.numToFarsi(switchList[i].nameId))
        }
        val adapter = MySpinnerAdapter(this, android.R.layout.simple_spinner_dropdown_item, switch1)
        switchlist.adapter = adapter
    }

    private fun setonoff(sn: String?, pass: String?, status: String) {
        apiService = ApiService(this@MainActivity)
        apiService.setOnOff(sn, pass, status) { ok: String, msg: String? ->
            if (ok.equals("true", ignoreCase = true)) {
                FunctionsClass.showErrorSnak(this@MainActivity, FunctionsClass.getServerErrors(msg))
            } else {
                FunctionsClass.showErrorSnak(this@MainActivity, FunctionsClass.getServerErrors(msg))
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