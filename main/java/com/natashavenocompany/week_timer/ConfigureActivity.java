package com.natashavenocompany.week_timer;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.CheckBox;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by natas on 16/12/14.
 */

// 小部件详细设置界面
public class ConfigureActivity extends AppCompatActivity {

    Date start_date;
    Date end_date;
    TextView txt_start_time;
    TextView txt_end_time;
    TextView txt_title;
    TextView txt_content;
    TextView txt_color_str;
    CheckBox bool_show_title;
    TextView msg_sum;
    Button btn_ok;
    int sum_day;
    private int mAppWidgetId;
    private Context context;
    public static final String CLICK_ACTION = "com.natashavenocompany.week_timer.CLICKED";

    // 显示日期选择器
    public void showDatePickerDialog(final Context context, final Date date, final TextView changeTextView) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String datestring = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
                        date.setYear(year);
                        date.setMonth(monthOfYear);
                        date.setDate(dayOfMonth);
                        changeTextView.setText(datestring);
                        updateSumDays();
                        Log.i("DatePicker", datestring);
                    }
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    // 更新总天数计算
    private void updateSumDays() {
        start_date = new Date(Date.parse((String) txt_start_time.getText()));
        end_date = new Date(Date.parse((String) txt_end_time.getText()));
        sum_day = (int) ((end_date.getTime() - start_date.getTime()) / 86400000);
        msg_sum = (TextView) findViewById(R.id.msg_sum);
        msg_sum.setText("共计" + (sum_day > 0 ? sum_day : 0) + "天");

    }

    // 初始化设置面板，（读取配置，或者设置默认配置）
    private void initConfigurePanel() {
        String uniqueKey = String.valueOf(mAppWidgetId);
        Configure_util configure_util = new Configure_util(context, uniqueKey);
        JSONObject jsonObject = configure_util.loads_json(uniqueKey, true);
        Log.i("load json", jsonObject.toString());
        txt_start_time.setText(jsonObject.optString("start_time"));
        txt_end_time.setText(jsonObject.optString("end_time"));
        txt_title.setText(jsonObject.optString("title"));
        txt_content.setText(jsonObject.optString("content"));
        bool_show_title.setChecked(jsonObject.optBoolean("bool_show_title"));
        // 读取颜色配置
        String color_str = jsonObject.optString("txt_color_str", "nonset");
        if (color_str.equals("nonset")){    // 初始设置，读取默认值（上次使用的值）
            Configure_util configure_util_color_set = new Configure_util(context, "COLOR_SET");
            JSONObject jsonObject_load_color_set = configure_util_color_set.loads_json("COLOR_SET", false);
            color_str = jsonObject_load_color_set.optString("last_default_color", "#FFFFF0");
        }
        txt_color_str.setText(color_str);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_view);

        context = this;
        txt_start_time = (TextView) findViewById(R.id.txt_start_time);
        txt_end_time = (TextView) findViewById(R.id.txt_end_time);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_content =(TextView) findViewById(R.id.txt_content);
        txt_color_str = (TextView) findViewById(R.id.txt_color_str);
        bool_show_title = (CheckBox) findViewById(R.id.bool_show_title);
        btn_ok = (Button) findViewById(R.id.btn_ok);

        // 从Intent中获取到当前配置的小部件ID
        final Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        final Intent resultValue = new Intent();

        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        initConfigurePanel();
        updateSumDays();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

        // 日期设置按钮事件
        // 开始日期
        txt_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_date = new Date(Date.parse((String) txt_start_time.getText()));
                showDatePickerDialog(ConfigureActivity.this, start_date, txt_start_time);
            }
        });

        //结束日期
        txt_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                end_date = new Date(Date.parse((String) txt_end_time.getText()));
                showDatePickerDialog(ConfigureActivity.this, end_date, txt_end_time);
            }
        });

        // 确定按钮
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 保存配置
                String uniqueKey = String.valueOf(mAppWidgetId);
                Configure_util configure_util = new Configure_util(context, uniqueKey);
                Configure_util configure_util_color_set = new Configure_util(context, "COLOR_SET");
                JSONObject jsonObject = configure_util.loads_json(uniqueKey, true);
                JSONObject jsonObject_load_color_set = configure_util.loads_json("COLOR_SET", false);
                try {
                    jsonObject.put("start_time", txt_start_time.getText());
                    jsonObject.put("end_time", txt_end_time.getText());
                    jsonObject.put("title", txt_title.getText());
                    jsonObject.put("content", txt_content.getText());
                    jsonObject.put("txt_color_str", txt_color_str.getText());
                    jsonObject.put("bool_show_title", bool_show_title.isChecked());
                    jsonObject_load_color_set.put("last_default_color", txt_color_str.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                configure_util.dumps_json(uniqueKey, jsonObject);
                configure_util_color_set.dumps_json("COLOR_SET", jsonObject_load_color_set);
                Log.i("save json", jsonObject.toString());
                Log.i("save json", jsonObject_load_color_set.toString());
//                resultValue.putExtra("start_time", txt_start_time.getText());
//                resultValue.putExtra("end_time", txt_end_time.getText());
                setResult(RESULT_OK, resultValue);

                Log.i("Set", String.valueOf(mAppWidgetId));
                // 关闭配置页面
                finish();

                // 延迟发送更新广播，目的是回到桌面后更新小部件视图
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(800);
                        } catch (InterruptedException e) {
//                            e.printStackTrace();
                        } finally {
                            // 发送更新广播
                            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, desktopWidgetProvider.class);
                            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{mAppWidgetId});
                            sendBroadcast(intent);
                        }
                    }
                }.start();
            }
        });
    }
}
