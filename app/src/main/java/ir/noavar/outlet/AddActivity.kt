package ir.noavar.outlet

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NoConnectionError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.noavar.outlet.ui.theme.CustomSnackBar
import ir.noavar.outlet.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class AddActivity : ComponentActivity() {

    private var devices = mutableListOf<Device>()

    private var serialNumber by mutableStateOf("")
    private var password by mutableStateOf("")
    private var name by mutableStateOf("")
    private var state = SnackbarHostState()

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

            when (it.getString("result")) {
                "1001" -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        state.showSnackbar(
                            "شماره سریال یا رمز عبور اشتباه است.",
                            null,
                            SnackbarDuration.Long
                        )
                    }
                }
                "2000" -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        state.showSnackbar(
                            "با موفقیت اضافه شد.",
                            null,
                            SnackbarDuration.Long
                        )
                    }
                }

                "1002" -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        state.showSnackbar(
                            "سرور در دسترس نیست. لطفا دوباره امتحان کنید.",
                            null,
                            SnackbarDuration.Long
                        )
                    }
                }

                else -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        state.showSnackbar(
                            it.toString(),
                            null,
                            SnackbarDuration.Long
                        )
                    }
                }
            }

            if (it.getString("ok").equals("true", ignoreCase = true)) {
                devices.add(
                    Device(
                        serialNumber = serialNumber,
                        password = password,
                        name = name
                    )
                )
                saveToMemory()
                startActivity(Intent(this, LocalActivity::class.java))
            }
        }, {
            when (it) {
                is NoConnectionError -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        state.showSnackbar(
                            "اینترنت قطع است. لطفا اینترنت سیم کارت یا شبکه وای فای را فعال کنید.",
                            null,
                            SnackbarDuration.Long
                        )
                    }
                    //startActivity(Intent(this, LocalActivity::class.java))
                    startActivity(Intent(this, DeviceSettingActivity::class.java))
                }
                else -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        state.showSnackbar(
                            it.toString(),
                            null,
                            SnackbarDuration.Long
                        )
                    }
                }
            }
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
                    snackbarHost = { CustomSnackBar(state) },
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
                    devices.clear()
                    devices.add(
                        Device(
                            serialNumber = "11111",
                            password = "7777",
                            name = "یخچال",
                            status = false
                        )
                    )
                    devices.add(
                        Device(
                            serialNumber = "22222",
                            password = "8888",
                            name = "کولر آبی",
                            status = false
                        )
                    )
                    devices.add(
                        Device(
                            serialNumber = "33333",
                            password = "9999",
                            name = "پنکه",
                            status = false
                        )
                    )
                    saveToMemory()
                },
                modifier = Modifier
                    .padding(top = 16.dp)
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 0.dp, end = 50.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "اضافه کردن دستگاه جدید", textAlign = TextAlign.Center,
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { finish() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_arrow_forward_24),
                        contentDescription = ""
                    )
                }
            }
        )

    }

    @Composable
    fun DeviceSerialNumberTextField() {

        OutlinedTextField(
            value = serialNumber, onValueChange = {
                serialNumber = it
            },
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            label = { Text(text = "شماره سریال پریز را وارد کنید") }
        )
    }

    @Composable
    fun DevicePasswordTextField() {

        OutlinedTextField(
            value = password, onValueChange = {
                password = it
            },
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            label = { Text(text = "رمز عبور پریز را وارد کنید") }
        )
    }

    @Composable
    fun DeviceNameTextField() {

        OutlinedTextField(
            value = name, onValueChange = {
                name = it
            },
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            label = { Text(text = "نام دستگاه را وارد کنید (مانند کولر)") }
        )
    }
}