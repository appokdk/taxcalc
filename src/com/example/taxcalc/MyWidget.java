package com.example.taxcalc;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

public class MyWidget extends AppWidgetProvider {
	public static final String EXTRA_WIDGET_ID = "WIDGET_ID";
	
	public static void initWidget(Context context, int widgetId) {
		RemoteViews views = MyWidget.getRemoteViews(context, widgetId);
		views.setTextViewText(R.id.raw_value, context.getString(R.string.please_tap_me));
		views.setViewPadding(R.id.raw_value, 0, context.getResources().getDimensionPixelSize(R.dimen.raw_value_padding_top), 0, 0);
		views.setTextViewText(R.id.calculated_value, "");
		views.setViewPadding(R.id.calculated_value, 0, context.getResources().getDimensionPixelSize(R.dimen.calculated_value_padding_top), 0, 0);
		AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views);
	}
	
	public static void sendSetValueMessage(Context context, int widgetId, int amount) {
		Intent intent = new Intent();
		intent.setAction(MyWidgetReceiver.ACTION_SET_VALUE);
		intent.putExtra(EXTRA_WIDGET_ID, widgetId);
		intent.putExtra(MyWidgetReceiver.EXTRA_AMOUNT, amount);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		try {
			pendingIntent.send();
		}
		catch(Exception e) {
			Log.d("taxcalc.MyWidget", "sendSetAmountMessage:"+e.getMessage());
		}
	}
	
	public static void setValues(Context context, int widgetId, String rawValue, String calculatedValue) {
		RemoteViews views = getRemoteViews(context, widgetId);
		views.setTextViewText(R.id.raw_value, rawValue);
		views.setTextViewText(R.id.calculated_value, calculatedValue);
		views.setTextViewTextSize(R.id.calculated_value, TypedValue.COMPLEX_UNIT_PX, shrinkCalculatedValueTextSize(context, calculatedValue));
		AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views);
	}
	
	public static void setValues(Context context, int widgetId, String rawValue, int rawValuePadding, String calculatedValue, int calculatedValuePadding) {
		RemoteViews views = getRemoteViews(context, widgetId);
		views.setTextViewText(R.id.raw_value, rawValue);
		views.setViewPadding(R.id.raw_value, 0, rawValuePadding, 0, 0);
		views.setTextViewText(R.id.calculated_value, calculatedValue);
		views.setTextViewTextSize(R.id.calculated_value, TypedValue.COMPLEX_UNIT_PX, shrinkCalculatedValueTextSize(context, calculatedValue));
		views.setViewPadding(R.id.calculated_value, 0, calculatedValuePadding, 0, 0);
		AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views);
	}
	
	private static float shrinkCalculatedValueTextSize(Context context, String text) {
		float textWidth = context.getResources().getDimension(R.dimen.calculated_value_width);
		float textSize = context.getResources().getDimension(R.dimen.calculated_value_text_size);
		Paint paint = new Paint();
		paint.setTextSize(textSize);
		
		while(paint.measureText(text) > textWidth) {
			textSize--;
			paint.setTextSize(textSize);
		}
		
		return textSize;
	}
	
	public static RemoteViews getRemoteViews(Context context, int widgetId) {
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main_widget);
		Intent intent = new Intent(context, CalculatorActivity.class);
		intent.putExtra(EXTRA_WIDGET_ID, widgetId);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.root_layout, pendingIntent);
		
		return views;
	}
}
