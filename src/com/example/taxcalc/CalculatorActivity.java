package com.example.taxcalc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CalculatorActivity extends Activity {
	private int mWidgetId;
	private Calculator mCalculator;
	private TextView mDisplayValue;
	private TextView mDisplayOperator;
	private TextView mOkButton;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.calc_activity);
		
		mWidgetId = getIntent().getIntExtra(MyWidget.EXTRA_WIDGET_ID, 0);
		mDisplayValue = (TextView)findViewById(R.id.display);
		mDisplayOperator = (TextView)findViewById(R.id.operator);
		mOkButton = (TextView)findViewById(R.id.ok);
		mCalculator = new Calculator();
	}
	
	public void onNumButtonClick(View v) {
		int num = Integer.parseInt(((TextView)v).getText().toString());
		mCalculator.put(num);
		mDisplayValue.setText(String.format("%,3d", mCalculator.getDisplayValue()));
	}
	
	public void onBackButtonClick(View v) {
		mCalculator.delete();
		mDisplayValue.setText(String.format("%,3d", mCalculator.getDisplayValue()));
		mDisplayOperator.setText(mCalculator.getDisplayOperator());
	}
	
	public void onAddButtonClick(View v) {
		mCalculator.put(Calculator.ADD);
		mDisplayOperator.setText(mCalculator.getDisplayOperator());
		mOkButton.setText("=");
	}
	
	public void onClearButtonClick(View v) {
		mCalculator.clear();
		mDisplayOperator.setText("");
		mDisplayValue.setText("");
	}

	public void onOkButtonClick(View v) {
		if(mOkButton.getText().equals("=")) {
			mCalculator.calculate();
			mDisplayValue.setText(String.format("%,3d", mCalculator.getDisplayValue()));
			mDisplayOperator.setText(mCalculator.getDisplayOperator());
			mOkButton.setText("OK");
		}
		else {
			MyWidget.sendSetValueMessage(this, mWidgetId, mCalculator.getDisplayValue());
			finish();
		}
	}
	
	public void onCloseButtonClick(View v) {
		finish();
	}
}
