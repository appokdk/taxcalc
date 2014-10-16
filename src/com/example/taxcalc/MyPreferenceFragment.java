package com.example.taxcalc;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

public class MyPreferenceFragment extends PreferenceFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		refreshSummary();
	}
	
	private OnSharedPreferenceChangeListener mChangeListener = new OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(SharedPreferences preference, String key) {
			refreshSummary();
		}
	};
	
	private void refreshSummary() {
		ListPreference prefTaxRate = (ListPreference)findPreference(getString(R.string.pref_key_tax_rate));
		ListPreference prefCalcMethod = (ListPreference)findPreference(getString(R.string.pref_key_calc_method));
		
		if(prefTaxRate.getEntry() != null) {
			prefTaxRate.setSummary(prefTaxRate.getEntry());
		}
		
		if(prefCalcMethod.getEntry() != null) {
			prefCalcMethod.setSummary(prefCalcMethod.getEntry());
		}
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(mChangeListener);
	}
	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mChangeListener);
	}
	
	public static Value getPrefValue(Context context) {
		Value value = new Value();
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		value.taxRate = Float.parseFloat(pref.getString(context.getString(R.string.pref_key_tax_rate), "1.08"));
		value.calcMethod = Integer.parseInt(pref.getString(context.getString(R.string.pref_key_calc_method), "0"));
		
		return value;
	}
	
	public static class Value {
		public static final int CALC_METHOD_INCLUDE_TAX = 0;
		public static final int CALC_METHOD_WITHOUT_TAX = 1;
		
		public float taxRate;
		public int calcMethod;
	}
}
