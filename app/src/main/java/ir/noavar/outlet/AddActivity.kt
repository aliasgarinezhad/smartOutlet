package ir.noavar.outlet

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var name: EditText
    private var devices = mutableListOf<Device>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        username = findViewById(R.id.add_et_sn)
        password = findViewById(R.id.add_et_pass)
        name = findViewById(R.id.add_et_name)
        val btnLogin = findViewById<Button>(R.id.add_btn_apply)
        btnLogin.setOnClickListener { _ ->
            checkUserPass(FunctionsClass.numToEnglish(username.text.toString()), FunctionsClass.numToEnglish(password.text.toString()), FunctionsClass.numToEnglish(name.text.toString()))
        }
        loadFromMemory()
    }

    private fun checkUserPass(sn: String, pass: String, name: String) {
        apiService = ApiService(this@AddActivity)
        apiService.checkUserPass(sn, pass) { ok: String, msg: String ->
            if (ok.equals("true", ignoreCase = true)) {

                devices.add(Device(
                        serialNumber = sn,
                        password = pass,
                        name = name
                ))
                saveToMemory()

                FunctionsClass.showErrorSnak(this@AddActivity, FunctionsClass.getServerErrors(msg))

            } else {
                FunctionsClass.showErrorSnak(this@AddActivity, FunctionsClass.getServerErrors(msg))
            }
        }
    }

    private fun saveToMemory() {

        val memory = PreferenceManager.getDefaultSharedPreferences(this)
        val memoryEditor = memory.edit()

        memoryEditor.putString("devices", Gson().toJson(devices).toString())

        memoryEditor.apply()
    }

    private fun loadFromMemory() {

        val type = object : TypeToken<List<Device>>() {}.type
        val memory = PreferenceManager.getDefaultSharedPreferences(this)
        devices = Gson().fromJson(
                memory.getString("devices", ""),
                type
        ) ?: mutableListOf()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return true
    }
}