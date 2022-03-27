package ir.noavar.outlet

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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.noavar.outlet.ui.theme.MyApplicationTheme

class AddActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
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

    private fun checkUserPass(sn: String, pass: String, name: String) {
        apiService = ApiService(this@AddActivity)
        apiService.checkUserPass(sn, pass) { ok: String, msg: String ->
            if (ok.equals("true", ignoreCase = true)) {

                devices.add(
                    Device(
                        serialNumber = sn,
                        password = pass,
                        name = name
                    )
                )
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
                onClick = { checkUserPass(serialNumber, password, name) },
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