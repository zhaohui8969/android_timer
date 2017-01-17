package com.natashavenocompany.week_timer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by natas on 16/12/14.
 */
public class Configure_util {
    SharedPreferences sharedpreferences;
    private Context context;

    public Configure_util(Context context, String sharePrefix) {
        this.context = context;
        sharedpreferences = context.getSharedPreferences(sharePrefix, Context.MODE_PRIVATE);
    }

    // 读取配置，读不到就返回今天
    JSONObject loads_json(String sharePrefix, Boolean setDateNow) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(sharedpreferences.getString(sharePrefix, ""));
        } catch (JSONException e) {
            Log.i("EXCP", "not jsonobj");
            if (setDateNow) {
                Date dateNow = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(dateNow);
                String datestring = String.format("%d/%d/%d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
                jsonObject = new JSONObject();
                jsonObject.put("start_time", datestring);
                jsonObject.put("end_time", datestring);
            }
        } finally {
            return jsonObject;
        }
    }

    void dumps_json(String sharePrefix, JSONObject jsonObject) {
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(sharePrefix, jsonObject.toString());
        editor.commit();
    }
}
