package ir.noavar.smartoutlet

import android.content.Intent
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import com.android.volley.NoConnectionError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ir.noavar.smartoutlet.ui.theme.CustomSnackBar
import ir.noavar.smartoutlet.ui.theme.MyApplicationTheme
import ir.noavar.smartoutlet.ui.theme.doneColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var openClearDialog by mutableStateOf(false)
    private var selectMode by mutableStateOf(false)
    private var devices = mutableStateListOf<Device>()
    private var state = SnackbarHostState()
    private var selectedDevices = mutableStateListOf<Device>()

    override fun onResume() {
        super.onResume()

        setContent {
            Page()
        }

        loadFromMemory()
    }

    private fun setOnOff(serialNumber: String, password: String, status: String) {

        val apiUrl =
            "http://mamatirnoavar.ir/switchs/setDeviceParameters.php"

        val jsonArrayRequest = object : StringRequest(Method.POST, apiUrl, {

            when {
                it.contains("1000") -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        state.showSnackbar(
                            "شماره سریال یا رمز عبور دستگاه اشتباه است.",
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
                            "فرمان با موفقیت ارسال شد",
                            null,
                            SnackbarDuration.Long
                        )
                    }

                    val deviceIndex = devices.indexOfLast { it1 ->
                        it1.serialNumber == serialNumber
                    }
                    devices[deviceIndex].status = status == "1"
                    val temp = mutableListOf<Device>()
                    temp.addAll(devices)
                    devices.clear()
                    devices.addAll(temp)
                    saveToMemory()
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
                return "serialNumber=$serialNumber&password=$password&status=$status".toByteArray()
            }
        }

        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }

    private fun loadFromMemory() {

        val type = object : TypeToken<SnapshotStateList<Device>>() {}.type
        val memory = PreferenceManager.getDefaultSharedPreferences(this)
        devices = Gson().fromJson(
            memory.getString("devices", ""),
            type
        ) ?: mutableStateListOf()
    }

    private fun saveToMemory() {

        val memory = PreferenceManager.getDefaultSharedPreferences(this)
        val memoryEditor = memory.edit()

        memoryEditor.putString("devices", Gson().toJson(devices).toString())

        memoryEditor.apply()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == 4) {
            if (selectMode) {
                selectedDevices.clear()
                selectMode = false
            } else {
                finish()
            }
        }
        return true
    }

    @Composable
    fun Page() {
        MyApplicationTheme {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Scaffold(
                    topBar = { if (selectMode) SelectedAppBar() else AppBar() },
                    content = { Content() },
                    snackbarHost = { CustomSnackBar(state) },
                    floatingActionButton = { OpenCheckInButton() },
                    floatingActionButtonPosition = FabPosition.Center,
                )
            }
        }
    }

    @Composable
    fun Content() {

        Column {

            if (openClearDialog) {
                AccountAlertDialog()
            }

            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(devices.size) { i ->
                    LazyColumnItem(i)
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun LazyColumnItem(i: Int) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                .background(
                    MaterialTheme.colors.onPrimary,
                    shape = MaterialTheme.shapes.small
                )
                .fillMaxWidth()
                .combinedClickable(
                    onLongClick = {
                        if (!selectMode) {
                            selectMode = true
                            selectedDevices.add(devices[i])
                        } else {
                            if (devices[i] !in selectedDevices) {
                                selectedDevices.add(devices[i])
                            } else {
                                selectedDevices.remove(devices[i])
                                if (selectedDevices.size == 0) {
                                    selectMode = false
                                }
                            }
                        }
                    },
                    onClick = {
                        if (selectMode) {
                            if (devices[i] !in selectedDevices) {
                                selectedDevices.add(devices[i])
                            } else {
                                selectedDevices.remove(devices[i])
                                if (selectedDevices.size == 0) {
                                    selectMode = false
                                }
                            }
                        }
                    }
                )
        ) {

            if (devices[i] in selectedDevices) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_check_circle_24),
                    tint = doneColor,
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterVertically)
                )
            }

            Text(
                text = "نام: " + devices[i].name,
                modifier = Modifier
                    .weight(1F)
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp)
            )
            Text(
                text = "حالت: " + if (!devices[i].status) "خاموش" else "روشن",
                modifier = Modifier
                    .weight(1F)
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp)
            )

            Switch(
                modifier = Modifier
                    .weight(1F)
                    .padding(start = 16.dp),
                checked = devices[i].status,
                onCheckedChange = {
                    if (it) {
                        setOnOff(devices[i].serialNumber, devices[i].password, "1")
                    } else {
                        setOnOff(devices[i].serialNumber, devices[i].password, "0")
                    }
                })
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
                        "لیست دستگاه ها", textAlign = TextAlign.Center,
                    )
                }
            },
        )
    }

    @Composable
    fun SelectedAppBar() {

        TopAppBar(

            navigationIcon = {
                IconButton(onClick = {
                    selectedDevices.clear()
                    selectMode = false
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_arrow_forward_24),
                        contentDescription = ""
                    )
                }
            },

            title = {},

            actions = {

                IconButton(onClick = {
                    selectedDevices.clear()
                    selectedDevices.addAll(devices)
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_select_all_24),
                        contentDescription = ""
                    )
                }

                IconButton(onClick = {
                    openClearDialog = true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                        contentDescription = ""
                    )
                }
            }
        )
    }

    @Composable
    fun OpenCheckInButton() {
        ExtendedFloatingActionButton(
            onClick = { startActivity(Intent(this, AddActivity::class.java)) },
            text = { Text("اضافه کردن دستگاه جدید") },
            icon = { Icon(Icons.Filled.Add, contentDescription = "") },
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }

    @Composable
    fun AccountAlertDialog() {

        AlertDialog(
            onDismissRequest = {
                openClearDialog = false
            },
            buttons = {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "مواردی که علامت زده اید پاک شوند؟",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        style = MaterialTheme.typography.h2,
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
                                devices.removeAll(selectedDevices)
                                saveToMemory()
                                selectedDevices.clear()
                                selectMode = false
                                openClearDialog = false
                            },
                            modifier = Modifier.padding(end = 24.dp)
                        ) {
                            Text(text = "بله")
                        }

                        Button(
                            onClick = {
                                openClearDialog = false
                            },
                        ) {
                            Text(text = "خیر")
                        }
                    }
                }
            }
        )
    }
}