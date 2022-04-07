package ir.noavar.outlet

import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NoConnectionError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ir.noavar.outlet.ui.theme.CustomSnackBar
import ir.noavar.outlet.ui.theme.MyApplicationTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceSettingActivity : ComponentActivity() {

    private var routerSSID by mutableStateOf("")
    private var routerPassword by mutableStateOf("")
    private var state = SnackbarHostState()
    private var openConnectToDeviceDialog by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Page()
        }
    }

    private fun addNewDevice(
        routerSSID: String,
        routerPassword: String
    ) {

        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        if(!wifiManager.isWifiEnabled) {
            openConnectToDeviceDialog = true
            return
        }

        val apiUrl = "http://192.168.4.1/"

        val stringRequest = object : StringRequest(Method.POST, apiUrl, {

            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }, {
            when (it) {
                is NoConnectionError -> {
                    openConnectToDeviceDialog = true
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
                return "local_name=smart_switch&local_password=12345678&router_name=$routerSSID&router_password=$routerPassword&save".toByteArray()
            }
        }

        stringRequest.retryPolicy = DefaultRetryPolicy(
            8000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        Volley.newRequestQueue(this).add(stringRequest)
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

            if (openConnectToDeviceDialog) {
                AccountAlertDialog()
            }

            RouterSSIDTextField()
            RouterPasswordTextField()

            Button(
                onClick = {
                    addNewDevice(routerSSID, routerPassword)
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
                        "تنظیمات دستگاه", textAlign = TextAlign.Center,
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
    fun RouterSSIDTextField() {

        OutlinedTextField(
            value = routerSSID, onValueChange = {
                routerSSID = it
            },
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            label = { Text(text = "نام وای فای خانگی (مودم ثابت)") }
        )
    }

    @Composable
    fun RouterPasswordTextField() {

        OutlinedTextField(
            value = routerPassword, onValueChange = {
                routerPassword = it
            },
            modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            label = { Text(text = "رمز وای فای خانگی") }
        )
    }

    @Composable
    fun AccountAlertDialog() {

        AlertDialog(
            onDismissRequest = {
                openConnectToDeviceDialog = false
            },
            buttons = {

                Column(
                    modifier = Modifier
                        .width(320.dp)
                        .height(180.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "لطفا پریز را به برق متصل نموده و بعد از چند ثانیه، دکمه زیر را فشار داده و در لیست وای فای ها، به وای فای smart_switch با رمز 12345678 متصل شوید و به برنامه برگردید.",
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
                            .padding(bottom = 16.dp)
                    ) {
                        Button(
                            onClick = {
                                openConnectToDeviceDialog = false
                                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                            },
                        ) {
                            Text(text = "اتصال به وای فای")
                        }
                    }
                }
            }
        )
    }
}