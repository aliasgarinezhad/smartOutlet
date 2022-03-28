package ir.noavar.outlet

import android.app.Activity
import android.view.View
import com.google.android.material.snackbar.Snackbar
import android.widget.TextView

object FunctionsClass {
    fun getServerErrors(errCode: String): String {
        return when (errCode) {
            "1000" -> "عدم دسترسی به سرور!!! اتصال اینترنت خود را چک کنید."
            "1001" -> "شماره سریال و یا رمز عبور اشتباه است."
            "1002" -> "عدم دسترسی به سرور!!!"
            "2000" -> "با موفقیت اضافه شد."
            "2001" -> "فرمان ارسال شد."
            else -> "خطا نا مشخص!!!"
        }
    }

    fun showErrorSnack(activity: Activity, errorMessage: String) {
        val view = activity.window.decorView.findViewById<View>(android.R.id.content)
        val snackBar = Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG)
        val tv = snackBar.view.findViewById<TextView>(R.id.snackbar_text)
        tv.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
        snackBar.show()
    }
}