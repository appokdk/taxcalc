package com.example.taxcalc;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.Spinner;

public class ConfigActivity extends Activity {
	private int mWidgetId = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.config_activity);
		
		
	}
	
	public void onResume() {
		super.onResume();
		
		//システムへの戻り値
		mWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
		setResult(RESULT_CANCELED, resultValue);
	}
	
	public void onOkClick(View v) {	
		//システムへの戻り値
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
		setResult(RESULT_OK, resultValue);
		
		//ウィジェット初期化
		MyWidget.initWidget(this, mWidgetId);
		
		finish();
	}
}
