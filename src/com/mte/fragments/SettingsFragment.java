package com.mte.fragments;

import java.util.ArrayList;
import java.util.List;
import com.mte.speak.R;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;

public class SettingsFragment extends BaseFragment implements OnSeekBarChangeListener, OnItemSelectedListener, OnCheckedChangeListener {
	public static final String TAG = "SettingsFragment";
	private List<String> items = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings, container, false);		
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		SharedPreferences settings = this.getActivity().getSharedPreferences(TAG, 0);		
		
		Boolean engine = settings.getBoolean("engine", true);
		int p = settings.getInt("p", 70);
		int g = settings.getInt("g", 2);
		int s = settings.getInt("s", 175);
		int a = settings.getInt("a", 100);
		String v = settings.getString("v", "f3");
		
		CheckBox setting_engine = (CheckBox)this.getView().findViewById(R.id.speech_engine);
		SeekBar setting_p = (SeekBar)this.getView().findViewById(R.id.setting_p);
		SeekBar setting_g = (SeekBar)this.getView().findViewById(R.id.setting_g);
		SeekBar setting_s = (SeekBar)this.getView().findViewById(R.id.setting_s);
		SeekBar setting_a = (SeekBar)this.getView().findViewById(R.id.setting_a);
		Spinner setting_v = (Spinner)this.getView().findViewById(R.id.setting_v);
		
		setting_engine.setOnCheckedChangeListener(this);
		setting_p.setOnSeekBarChangeListener(this);
		setting_g.setOnSeekBarChangeListener(this);
		setting_s.setOnSeekBarChangeListener(this);
		setting_a.setOnSeekBarChangeListener(this);
		setting_v.setOnItemSelectedListener(this);
		items = new ArrayList<String>();
        items.add("0"); items.add("1"); items.add("2"); items.add("3"); items.add("m"); items.add("m1"); items.add("m2");
        items.add("m3"); items.add("m4"); items.add("f1"); items.add("f2"); items.add("f3"); items.add("f4"); items.add("f5");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1,items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		setting_v.setAdapter(adapter);
		
		setting_engine.setChecked(engine);
		setting_p.setProgress(p);
		setting_g.setProgress(g);
		setting_s.setProgress(s);
		setting_a.setProgress(a);
		setting_v.setSelection(items.indexOf(v));
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(TAG, 0).edit();
		editor.putString("v", items.get(position));
		editor.commit();	
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) { }

	@Override
	public void onProgressChanged(SeekBar seekBar, int process, boolean arg2) {
		SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(TAG, 0).edit();
		switch(seekBar.getId()){
			case R.id.setting_p:{
				editor.putInt("p", process);
				break;
			}
			case R.id.setting_g:{
				editor.putInt("g", process);
				break;
			}
			case R.id.setting_s:{
				editor.putInt("s", process);
				break;
			}
			case R.id.setting_a:{
				editor.putInt("a", process);
				break;
			}
		}
		editor.commit();		
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {}
	@Override
	public void onStopTrackingTouch(SeekBar arg0) {}
	@Override
	public void onCheckedChanged(CompoundButton checkBox, boolean engine) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(TAG, 0).edit();
		switch(checkBox.getId()){
			case R.id.speech_engine:
				editor.putBoolean("engine", engine);
				break;
		}
		editor.commit();
	}
}
