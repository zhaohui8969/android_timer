package com.natashavenocompany.week_timer;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;


/**
 * Created by natas on 16/12/15.
 */

// 详情页面
public class WidgetDetailActivity extends Activity {
    TextView txt_start_time;
    TextView txt_left_time;
    TextView txt_end_time;
    TextView txt_title;
    TextView txt_content;
    Button btn_detail_configure;
    Context context;
    Intent intent;
    int mAppWidgetId;
    int day_left;
    int sum_day;
    Boolean isTimerStart;
    LinearLayout layout_detail_view;

    // 设置面板显示内容
    private void initSetText() {
        String uniqueKey = String.valueOf(mAppWidgetId);
        Configure_util configure_util = new Configure_util(context, uniqueKey);
        JSONObject jsonObject = configure_util.loads_json(uniqueKey, true);
        Log.i("load json", jsonObject.toString());
        txt_start_time.setText(jsonObject.optString("start_time"));
        txt_end_time.setText(jsonObject.optString("end_time"));
        txt_title.setText(jsonObject.optString("title"));
        txt_content.setText(jsonObject.optString("content"));
        if (isTimerStart) {
            txt_left_time.setText(String.format("共计%d天，还有%d天", sum_day, day_left));
        } else {
            txt_left_time.setText(String.format("共计%d天，计时还未开始", day_left));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_view);

        txt_start_time = (TextView) findViewById(R.id.txt_start_time);
        txt_end_time = (TextView) findViewById(R.id.txt_end_time);
        txt_left_time = (TextView) findViewById(R.id.txt_left_time);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_content = (TextView) findViewById(R.id.txt_content);
        btn_detail_configure = (Button) findViewById(R.id.btn_detail_configure);
        layout_detail_view = (LinearLayout) findViewById(R.id.layout_detail_view);
        context = this;

        // 获取intent中的widgetID
        intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.i("Detail_View", "no widgetid in intent");
            finish();
        }

        // 获取indent中的剩余日期
        day_left = intent.getIntExtra("day_left", 0);
        isTimerStart = intent.getBooleanExtra("isTimerStart", false);
        sum_day = intent.getIntExtra("sum_day", 0);

        // 设置面板显示内容
        initSetText();

        // 修改按钮跳转至修改界面
        btn_detail_configure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentClick = new Intent(context, ConfigureActivity.class);
                intentClick.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                startActivity(intentClick);
                finish();
            }
        });

        // 界面点击关闭设置
        layout_detail_view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
