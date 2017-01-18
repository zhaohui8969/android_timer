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
// 配置保存读取用的工具类，使用sharedpreferences保存配置
public class Configure_util {
    SharedPreferences sharedpreferences;
    private Context context;

    public Configure_util(Context context, String sharePrefix) {
        this.context = context;
        sharedpreferences = context.getSharedPreferences(sharePrefix, Context.MODE_PRIVATE);
    }

    // 读取配置
    // String sharePrefix 配置前缀
    // Boolean setDateNow 为True时读不到就返回今天，否则返回空的JSONObject
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

    // 保存配置
    // String sharePrefix 配置前缀
    void dumps_json(String sharePrefix, JSONObject jsonObject) {
        final SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(sharePrefix, jsonObject.toString());
        editor.commit();
    }
}
