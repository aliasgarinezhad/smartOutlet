package ir.noavar.outlet

data class Device(
    val serialNumber: String,
    val password: String,
    val name: String,
    var status : Boolean = false
)