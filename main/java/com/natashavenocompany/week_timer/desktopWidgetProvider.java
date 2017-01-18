package com.natashavenocompany.week_timer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.RemoteViews;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by natas on 16/12/14.
 */

// 实现AppWidgetProvider，用来被系统小部件更新逻辑调度
public class desktopWidgetProvider extends AppWidgetProvider {
    static String TAG = "FROM widgt";
    private RemoteViews mRemoteViews;
    public static final String CLICK_ACTION = "com.natashavenocompany.week_timer.CLICKED";
    String start_time;
    String end_time;
    String txt_color_str;
    String widget_title;
    String txt_title;
    int sum_day;
    int txt_color_int;
    int progressBarDay;
    Date start_date;
    Date end_date;
    Date today;
    Boolean isTimerStart;

    // 更新进度条所需数据
    private void updateProgressBar(Context context, int appWidgetId) {
        String uniqueKey = String.valueOf(appWidgetId);
        Configure_util configure_util = new Configure_util(context, uniqueKey);
        JSONObject jsonObject = configure_util.loads_json(uniqueKey, true);
        Log.i("update load json", jsonObject.toString());
        start_time = jsonObject.optString("start_time");
        end_time = jsonObject.optString("end_time");
        txt_color_str = jsonObject.optString("txt_color_str", "#FFFFFF");
        widget_title = jsonObject.optString("title");
        txt_color_int = Color.parseColor(txt_color_str);
        today = new Date();
        start_date = new Date(Date.parse(start_time));
        end_date = new Date(Date.parse(end_time));
        sum_day = (int) ((end_date.getTime() - start_date.getTime()) / 86400000);
        progressBarDay = (int) ((end_date.getTime() - today.getTime()) / 86400000) + 1;

        // 数值错误修正
        sum_day = sum_day >= 0 ? sum_day : 0;
        progressBarDay = progressBarDay > 0 ? progressBarDay > sum_day ? sum_day : progressBarDay : 0;
    }

    // 确定RemoteViews样式，小部件在桌面上的样式确定
    private void getRemoteViewsStyle(Context context, int widgetID) {
        int NON_ACTIVITY_STYLE = R.layout.widget_view_nonactivity;
        int ACTIVITY_STYLE = R.layout.widget_view_activity;
        int ACTIVITY_STYLE_WITH_TITLE = R.layout.widget_view_activity_with_title;
        int NON_ACTIVITY_STYLE_WITH_TITLE = R.layout.widget_view_nonactivity_with_title;
        int layoutStyleId;
        // 确定是否显示标题
        String uniqueKey = String.valueOf(widgetID);
        Configure_util configure_util = new Configure_util(context, uniqueKey);
        JSONObject jsonObject = configure_util.loads_json(uniqueKey, true);
        boolean bool_show_title = jsonObject.optBoolean("bool_show_title");
        txt_title = jsonObject.optString("title");
        // 确定进度条颜色
        if (today.getTime() - start_date.getTime() > 0) {
            if (bool_show_title) {
                layoutStyleId = ACTIVITY_STYLE_WITH_TITLE;
            } else {
                layoutStyleId = ACTIVITY_STYLE;
            }
            isTimerStart = true;
        } else {
            if (bool_show_title) {
                layoutStyleId = NON_ACTIVITY_STYLE_WITH_TITLE;
            } else {
                layoutStyleId = NON_ACTIVITY_STYLE;
            }
            isTimerStart = false;
        }
        mRemoteViews = new RemoteViews(context.getPackageName(), layoutStyleId);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.i(TAG, "onDeleted");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.i(TAG, "onDisabled");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.i(TAG, "onEnabled");
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        int widget_id;
        Log.i(TAG, "onReceive : action = " + intent.getAction());

        if (intent.getAction().equals(CLICK_ACTION)) {
            widget_id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
            Log.i(TAG, "widget_id:" + widget_id);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(widget_id, mRemoteViews);

            // 测试更新
            onWidgetUpdate(context, appWidgetManager, widget_id);
        }
    }

    // 小部件更新时执行这个部分
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.i(TAG, "onUpdate");

        // 依次更新所有的小部件
        final int counter = appWidgetIds.length;
        Log.i(TAG, "counter = " + counter);
        for (int i = 0; i < counter; i++) {
            int appWidgetId = appWidgetIds[i];
            onWidgetUpdate(context, appWidgetManager, appWidgetId);
        }
    }

    // 传进来一个appWidgetId，更新单个小部件
    public void onWidgetUpdate(Context context,
                               AppWidgetManager appWidgeManger, int appWidgetId) {

        Log.i(TAG, "appWidgetId = " + appWidgetId);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        // 计算并更新进度条
        updateProgressBar(context, appWidgetId);

        // 根据进度确定进度条的样式
        getRemoteViewsStyle(context, appWidgetId);
        mRemoteViews.setProgressBar(R.id.progressbar, sum_day, progressBarDay, false);
        mRemoteViews.setTextViewText(R.id.txt_center, String.valueOf(progressBarDay));
        mRemoteViews.setTextViewText(R.id.txt_widget_title, txt_title);
        mRemoteViews.setTextColor(R.id.txt_center, txt_color_int);
        appWidgeManger.updateAppWidget(appWidgetId, mRemoteViews);

        // "窗口小部件"点击事件绑定
//        Intent intentClick = new Intent(context, ConfigureActivity.class);    // 点击显示配置页面
        Intent intentClick = new Intent(context, WidgetDetailActivity.class);   // 点击展示详情页面
        intentClick.setAction(CLICK_ACTION);
        // 把一些用到的变量存到Intent里面传递给配置activity
        intentClick.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intentClick.putExtra("day_left", progressBarDay);
        intentClick.putExtra("isTimerStart", isTimerStart);
        intentClick.putExtra("sum_day", sum_day);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intentClick, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.progressbar, pendingIntent);
        mRemoteViews.setOnClickPendingIntent(R.id.txt_center, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, mRemoteViews);    // 更新小部件视图
    }
}