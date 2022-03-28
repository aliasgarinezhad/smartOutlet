package ir.noavar.outlet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.noavar.outlet.ui.theme.MyApplicationTheme
import org.json.JSONObject

class AddActivity : AppCompatActivity() {

    private var devices = mutableListOf<Device>()

    private var serialNumber by mutableStateOf("")
    private var password by mutableStateOf("")
    private var name by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Page()
        }
        loadFromMemory()
    }

    private fun addNewDevice(serialNumber: String, password: String, name: String) {

        val apiUrl = "https://mamatirnoavar.ir/switchs/user_ma.php"

        val jsonObject = JSONObject()
        jsonObject.put("sn", serialNumber)
        jsonObject.put("pass", password)
        jsonObject.put("request_type", "checkuserpassswitch")

        val jsonArrayRequest = JsonObjectRequest(Request.Method.POST, apiUrl, jsonObject, {

            val ok = it.getString("ok")
            val msg = it.getString("result")
            if (ok.equals("true", ignoreCase = true)) {
                devices.add(
                    Device(
                        serialNumber = serialNumber,
                        password = password,
                        name = name
                    )
                )
                saveToMemory()
                FunctionsClass.showErrorSnack(this, FunctionsClass.getServerErrors(msg))
                startActivity(Intent(this, LocalActivity::class.java))

            } else {
                FunctionsClass.showErrorSnack(this, FunctionsClass.getServerErrors(msg))
            }
        }, {
            FunctionsClass.showErrorSnack(this, FunctionsClass.getServerErrors("1000"))
        })

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(
            8000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        Volley.newRequestQueue(this).add(jsonArrayRequest)
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

    @Composable
    fun Page() {
        MyApplicationTheme {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Scaffold(
                    topBar = { AppBar() },
                    content = { Content() },
                )
            }
        }
    }

    @Composable
    fun Content() {

        Column(modifier = Modifier.fillMaxSize()) {
            DeviceSerialNumberTextField()
            DevicePasswordTextField()
            DeviceNameTextField()
            Button(
                onClick = {
                    addNewDevice(serialNumber, password, name)
                    devices.add(
                        Device(
                            serialNumber = "78945",
                            password = "6789",
                            name = "یخچال",
                            status = false
                        )
                    )
                    saveToMemory()
                },
                modifier = Modifier
                    .padding(top = 20.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "افزودن")
            }
        }
    }

    @Composable
    fun AppBar() {

        TopAppBar(

            title = {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "اضافه کردن", textAlign = TextAlign.Center,
                    )
                }
            },
        )
    }

    @Composable
    fun DeviceSerialNumberTextField() {

        OutlinedTextField(
            value = serialNumber, onValueChange = {
                serialNumber = it
            },
            modifier = Modifier
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth(),
            label = { Text(text = "شماره سریال") }
        )
    }

    @Composable
    fun DevicePasswordTextField() {

        OutlinedTextField(
            value = password, onValueChange = {
                password = it
            },
            modifier = Modifier
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth(),
            label = { Text(text = "رمز عبور") }
        )
    }

    @Composable
    fun DeviceNameTextField() {

        OutlinedTextField(
            value = name, onValueChange = {
                name = it
            },
            modifier = Modifier
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth(),
            label = { Text(text = "یک نام دلخواه برای دستگاه انتخاب کنید") }
        )
    }
}