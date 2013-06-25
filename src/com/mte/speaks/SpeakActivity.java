package com.mte.speaks;

import java.util.ArrayList;
import java.util.List;
import com.google.tts.TTS;
import com.google.tts.TTS.InitListener;
import com.mte.adapters.PagerAdapter;
import com.mte.fragments.*;
import com.mte.speaks.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;

@SuppressWarnings("deprecation")
public class SpeakActivity extends FragmentActivity implements OnPageChangeListener, InitListener{
	private SpeakFragment speakFragment = null;
	private SmsFragment smsFragment = null;
	private SettingsFragment settingsFragment = null;
	private List<Fragment> fList = new ArrayList<Fragment>();
	private PagerAdapter adapter;
	private Intent sharedIntent = null;
	private TTS tts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speak);
		Log.d("TAG", "here");
		tts = new TTS(this,this, true);
		
		speakFragment = new SpeakFragment();
		speakFragment.setTts(tts);
		speakFragment.setOnCompleteListener(new OnCompleteListener(){
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				if(sharedIntent != null){
					String sharedText = sharedIntent.getStringExtra(Intent.EXTRA_TEXT);
				    if (sharedText != null) {
				        // Update UI to reflect text being shared
				    	speakFragment.setContent(sharedText);
				    }
				}
				sharedIntent = null;
			}			
		});
		smsFragment = new SmsFragment();
		settingsFragment = new SettingsFragment();
		
		fList.add(this.speakFragment);
		fList.add(this.smsFragment);
		fList.add(this.settingsFragment);
		
		ViewPager pager = (ViewPager)this.findViewById(R.id.viewpager);
		adapter = new PagerAdapter(this.getSupportFragmentManager(), fList, this);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(this);
		
		Intent intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();
	    //TitlePageIndicator indicator;
	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	        if ("text/plain".equals(type)) {
	        	sharedIntent = intent;
	        }
	    }
	    
	    BroadcastReceiver receiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				// TODO Auto-generated method stub
				smsFragment.resuse();
			}
		};
		
		IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(1000);
		this.registerReceiver(receiver, intentFilter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.speak, menu);
		return false;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}
	@Override
	public void onPageSelected(int position) {
		switch (position) {
		case 0:
			this.setTitle(R.string.app_name);
			break;
		case 1:
			this.setTitle(R.string.sms);
			this.smsFragment.resuse();
			break;
		case 2:
			this.setTitle(R.string.action_settings);
			break;
		}
	}

	@Override
	public void onInit(int arg0) {
		// TODO Auto-generated method stub
		this.tts.setLanguage("vn");
		this.tts.speak("chào mừng sử dụng Speak", 0, null);
	}
}
