package com.example.taxcalc;

import com.example.taxcalc.MyPreferenceFragment.Value;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

public class MyWidgetReceiver extends BroadcastReceiver {	
	public static final String ACTION_RESET = "APPWIDGET_RESET";
	public static final String ACTION_SET_VALUE = "APPWIDGET_SET_VALUE";
	public static final String ACTION_ANIMATION = "APPWIDGET_ANIMATION";
	public static final String EXTRA_RAW_VALUE = "EXTRA_RAW_VALUE";
	public static final String EXTRA_CALCULATED_VALUE = "EXTRA_CALCULATED_VALUE";
	public static final String EXTRA_INTERPOLATED_TIME = "EXTRA_INTERPOLATED_TIME";
	public static final String EXTRA_AMOUNT = "EXTRA_AMOUNT";
	
	private static PendingIntent alarmReset;
	private static final String CURRENCY_FORMAT = "\u00A5 %,3d";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		int widgetId = intent.getIntExtra(MyWidget.EXTRA_WIDGET_ID, 0);
		
		if(intent.getAction().equals(ACTION_RESET)) {
			MyWidget.initWidget(context, widgetId);
		}
		
		if(intent.getAction().equals(ACTION_SET_VALUE)) {
			if(alarmReset != null) {
				AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				alarmManager.cancel(alarmReset);
				alarmReset = null;
			}
			
			MyPreferenceFragment.Value prefValue = MyPreferenceFragment.getPrefValue(context);
			int value = intent.getIntExtra(EXTRA_AMOUNT, 0);
			String rawValue, calculatedValue;
			
			rawValue = String.format(CURRENCY_FORMAT, value);
			
			if(prefValue.calcMethod == Value.CALC_METHOD_INCLUDE_TAX) {
				//税込み
				value = (int)((float)value * prefValue.taxRate);
			}
			else {
				//税抜き
				value = (int)(Math.round((float)value / prefValue.taxRate));
			}
			
			calculatedValue = String.format(CURRENCY_FORMAT, value);
			
			//ウィジェットに値セット
			MyWidget.setValues(context, widgetId, rawValue, calculatedValue);
			
			//アニメーション用
			sendAnimIntent(context, widgetId, rawValue, calculatedValue, 0f);
		}
		
		if(intent.getAction().equals(ACTION_ANIMATION)) {			
			String rawValue = intent.getStringExtra(EXTRA_RAW_VALUE);
			String calculatedValue = intent.getStringExtra(EXTRA_CALCULATED_VALUE);
			float interpolatedTime = intent.getFloatExtra(EXTRA_INTERPOLATED_TIME, 0f);
			float rawValuePadding = context.getResources().getDimension(R.dimen.raw_value_padding_top);
			float calculatedValuePadding = context.getResources().getDimension(R.dimen.calculated_value_padding_top);
			
			//paddingを減らす　下から上に移動するように見える
			interpolatedTime += 0.1f;
			rawValuePadding = Math.max(rawValuePadding - rawValuePadding * interpolatedTime, 0f);
			calculatedValuePadding = Math.max(calculatedValuePadding - calculatedValuePadding * interpolatedTime, 0f);
			MyWidget.setValues(context, widgetId, rawValue, (int)rawValuePadding, calculatedValue, (int)calculatedValuePadding);
			
			if(interpolatedTime < 1.0f) {
				//アニメーション用
				sendAnimIntent(context, widgetId, rawValue, calculatedValue, interpolatedTime);
			}
			else {
				//2分後に表示をリセット
				Intent resetIntent = new Intent();
				resetIntent.setAction(ACTION_RESET);
				resetIntent.putExtra(MyWidget.EXTRA_WIDGET_ID, widgetId);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + (2 * 60 * 1000), pendingIntent);
				MyWidgetReceiver.alarmReset = pendingIntent;
			}
		}
	}
	
	private void sendAnimIntent(Context context, int widgetId, String rawValue, String calculatedValue, float interpolatedTime) {
		//10msごとに表示を更新してアニメーションする
		Intent animIntent = new Intent();
		animIntent.setAction(ACTION_ANIMATION);
		animIntent.putExtra(EXTRA_RAW_VALUE, rawValue);
		animIntent.putExtra(EXTRA_CALCULATED_VALUE, calculatedValue);
		animIntent.putExtra(EXTRA_INTERPOLATED_TIME, interpolatedTime);
		animIntent.putExtra(MyWidget.EXTRA_WIDGET_ID, widgetId);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, animIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 10, pendingIntent);
	}
}
