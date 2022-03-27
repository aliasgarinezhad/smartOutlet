package ir.noavar.outlet;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class ApiService {

    private Context context;
    String apiUrl;

    public ApiService(Context context) {
        this.context = context;
        apiUrl = "https://mamatirnoavar.ir/switchs/user_ma.php";
    }

    public void checkUserPass(String sn, String pass, final OnUserPassCheckRecieved onUserPassCheckRecieved) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sn", sn);
            jsonObject.put("pass", pass);
            jsonObject.put("request_type", "checkuserpassswitch");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String ok = response.getString("ok");
                    String msg = response.getString("result");
                    if (ok.equalsIgnoreCase("true")) {
                      /*  String idmodel = response.getString("idmodel");
                        String shopid = response.getString("shopid");
                        String zaman = response.getString("zaman");*/
                        onUserPassCheckRecieved.onRecieved(ok, msg);
                        // FunctionsClass.showErrorSnak(LoginActivity.this, FunctionsClass.getServerErrors(msg));
                    } else {
                        String err = response.getString("result");
                        onUserPassCheckRecieved.onRecieved(ok, err);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onUserPassCheckRecieved.onRecieved("false", "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onUserPassCheckRecieved.onRecieved("false", "1000");
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(8000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }

    public interface OnUserPassCheckRecieved {
        void onRecieved(String ok, String msg);
    }


    public void setOnOff(String sn, String pass, String status, final OnOffCheckRecieved onOffCheckRecieved) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("sn", sn);
            jsonObject.put("pass", pass);
            jsonObject.put("status", status);
            jsonObject.put("request_type", "setonoff");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String ok = response.getString("ok");
                    String msg = response.getString("result");
                    if (ok.equalsIgnoreCase("true")) {
                      /*  String idmodel = response.getString("idmodel");
                        String shopid = response.getString("shopid");
                        String zaman = response.getString("zaman");*/
                        onOffCheckRecieved.onRecieved(ok, msg);
                        // FunctionsClass.showErrorSnak(LoginActivity.this, FunctionsClass.getServerErrors(msg));
                    } else {
                        String err = response.getString("result");
                        onOffCheckRecieved.onRecieved(ok, err);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onOffCheckRecieved.onRecieved("false", "");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onOffCheckRecieved.onRecieved("false", "1000");
            }
        });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(8000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(jsonArrayRequest);
    }

    public interface OnOffCheckRecieved {
        void onRecieved(String ok, String msg);
    }

}
