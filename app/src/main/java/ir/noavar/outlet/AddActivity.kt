package ir.noavar.outlet

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import com.android.volley.NoConnectionError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.noavar.outlet.ui.theme.CustomSnackBar
import ir.noavar.outlet.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddActivity : ComponentActivity() {

    private var serialNumber by mutableStateOf("")
    private var password by mutableStateOf("")
    private var name by mutableStateOf("")
    private var state = SnackbarHostState()
    private var openIsDeviceConfiguredDialog by mutableStateOf(false)
    private var devices = mutableListOf<Device>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Page()
        }
        loadFromMemory()
    }

    private fun addNewDevice(serialNumber: String, password: String, name: String) {

        if (serialNumber == "") {
            CoroutineScope(Dispatchers.Default).launch {
                state.showSnackbar(
                    "لطفا شماره سریال پریز را وارد کنید.",
                    null,
                    SnackbarDuration.Long
                )
            }
            return
        }

        if (password == "") {
            CoroutineScope(Dispatchers.Default).launch {
                state.showSnackbar(
                    "لطفا رمز عبور پریز را وارد کنید.",
                    null,
                    SnackbarDuration.Long
                )
            }
            return
        }

        if (name == "") {
            CoroutineScope(Dispatchers.Default).launch {
                state.showSnackbar(
                    "لطفا نام دستگاه را وارد کنید.",
                    null,
                    SnackbarDuration.Long
                )
            }
            return
        }

        val apiUrl =
            "http://mamatirnoavar.ir/switchs/checkSerialNumberPassword.php"

        val jsonArrayRequest = object : StringRequest(Method.POST, apiUrl, {

            when {
                it.contains("1000") -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        state.showSnackbar(
                            "شماره سریال یا رمز عبور اشتباه است.",
                            null,
                            SnackbarDuration.Long
                        )
                    }
                }

                it.contains("3000") -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        state.showSnackbar(
                            "مشکلی در سرور پیش آمده است. لطفا دوباره امتحان کنید.",
                            null,
                            SnackbarDuration.Long
                        )
                    }
                }

                it.contains("2000") -> {

                    CoroutineScope(Dispatchers.Default).launch {
                        state.showSnackbar(
                            "با موفقیت اضافه شد.",
                            null,
                            SnackbarDuration.Long
                        )
                    }
                    openIsDeviceConfiguredDialog = true
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
        }) {
            override fun getBody(): ByteArray {
                return "serialNumber=$serialNumber&password=$password".toByteArray()
            }
        }

        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }

    private fun loadFromMemory() {

        val type = object : TypeToken<List<Device>>() {}.type
        val memory = PreferenceManager.getDefaultSharedPreferences(this)
        devices = Gson().fromJson(
            memory.getString("devices", ""),
            type
        ) ?: mutableListOf()
    }

    private fun saveToMemory() {

        val memory = PreferenceManager.getDefaultSharedPreferences(this)
        val memoryEditor = memory.edit()

        memoryEditor.putString("devices", Gson().toJson(devices).toString())

        memoryEditor.apply()
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

            if (openIsDeviceConfiguredDialog) {
                AccountAlertDialog()
            }

            DeviceSerialNumberTextField()
            DevicePasswordTextField()
            DeviceNameTextField()
            Button(
                onClick = {
                    addNewDevice(serialNumber, password, name)
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
            value = serialNumber,
            onValueChange = {
                serialNumber = it
            },
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            label = { Text(text = "شماره سریال پریز را وارد کنید") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
        )
    }

    @Composable
    fun DevicePasswordTextField() {

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            label = { Text(text = "رمز عبور پریز را وارد کنید") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
        )
    }

    @Composable
    fun DeviceNameTextField() {

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            label = { Text(text = "نام دستگاه را وارد کنید (مانند کولر)") },
            singleLine = true,
        )
    }

    @Composable
    fun AccountAlertDialog() {

        AlertDialog(
            onDismissRequest = {
                openIsDeviceConfiguredDialog = false
            },
            buttons = {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "آیا قبلا پریز را به اینترنت متصل کرده اید؟ در غیر این صورت وارد صفحه تنظیمات دستگاه شوید و نام و رمز عبور مودم خانگی تان را برای دستگاه تعریف کنید.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = {
                                openIsDeviceConfiguredDialog = false
                                devices.add(
                                    Device(
                                        serialNumber = serialNumber,
                                        password = password,
                                        name = name
                                    )
                                )
                                saveToMemory()

                                Intent(this@AddActivity, MainActivity::class.java).apply {
                                    flags += Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(this)
                                }
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(text = "بله")
                        }

                        Button(
                            onClick = {
                                openIsDeviceConfiguredDialog = false
                                Intent(this@AddActivity, DeviceSettingActivity::class.java).apply {
                                    this.putExtra("serialNumber", serialNumber)
                                    this.putExtra("password", password)
                                    this.putExtra("name", name)
                                    startActivity(this)
                                }
                            },
                        ) {
                            Text(text = "خیر، ورود به تنظیمات دستگاه")
                        }
                    }
                }
            }
        )
    }
}