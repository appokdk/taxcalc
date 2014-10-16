package com.example.taxcalc;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ConfigActivity extends Activity {
	private int mWidgetId = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.config_activity);
	}
	
	public void onResume() {
		super.onResume();
		
		mWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
		setResult(RESULT_CANCELED, resultValue);
	}
	
	public void onOkClick(View v) {	
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
		setResult(RESULT_OK, resultValue);
		
		//ウィジェット初期化
		MyWidget.initWidget(this, mWidgetId);
		
		finish();
	}
}
