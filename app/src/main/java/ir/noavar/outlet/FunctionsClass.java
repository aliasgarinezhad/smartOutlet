package ir.noavar.outlet;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

public class FunctionsClass {

    public static String getServerErrors(String errCode) {
        switch (errCode) {
            case "1000":
                return "عدم دسترسی به سرور!!! اتصال اینترنت خود را چک کنید.";
            case "1001":
                return "شماره سریال و یا رمز عبور اشتباه است.";
            case "1002":
                return "عدم دسترسی به سرور!!!";
            case "2000":
                return "با موفقیت اضافه شد.";
            case "2001":
                return "فرمان ارسال شد.";
            default:
                return "خطا نا مشخص!!!";
        }
    }
    public static void showErrorSnak(Activity activity, String errorMessage) {
        View view = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG);
        TextView tv = (TextView) (snackbar.getView()).findViewById(R.id.snackbar_text);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
//        tv.setTypeface(MyApplication.getSans(activity));
        snackbar.show();
    }

    public static String numToFarsi(String tmpcon) {
        String tmp;
        tmp = tmpcon + "";
        tmp = tmp.replaceAll("0", "۰");
        tmp = tmp.replaceAll("1", "۱");
        tmp = tmp.replaceAll("2", "۲");
        tmp = tmp.replaceAll("3", "۳");
        tmp = tmp.replaceAll("4", "۴");
        tmp = tmp.replaceAll("5", "۵");
        tmp = tmp.replaceAll("6", "۶");
        tmp = tmp.replaceAll("7", "۷");
        tmp = tmp.replaceAll("8", "۸");
        tmp = tmp.replaceAll("9", "۹");
        return tmp;
    }

    public static String numToEnglish(String tmpcon) {
        String tmp;
        tmp = tmpcon + "";
        tmp = tmp.replaceAll("۰", "0");
        tmp = tmp.replaceAll("۱", "1");
        tmp = tmp.replaceAll("۲", "2");
        tmp = tmp.replaceAll("۳", "3");
        tmp = tmp.replaceAll("۴", "4");
        tmp = tmp.replaceAll("۵", "5");
        tmp = tmp.replaceAll("۶", "6");
        tmp = tmp.replaceAll("۷", "7");
        tmp = tmp.replaceAll("۸", "8");
        tmp = tmp.replaceAll("۹", "9");
        return tmp;
    }

}
