package com.mte.speaks;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.mte.adapters.PagerAdapter;
import com.mte.fragments.*;
import com.mte.speak.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;

public class SpeakActivity extends FragmentActivity implements OnPageChangeListener, OnInitListener{
	private SpeakFragment speakFragment = null;
	private SmsFragment smsFragment = null;
	private SettingsFragment settingsFragment = null;
	private List<Fragment> fList = new ArrayList<Fragment>();
	private PagerAdapter adapter;
	private Intent sharedIntent = null;
	private TextToSpeech tts;
	
	private static final int REQ_TTS_STATUS_CHECK = 0;
	private static final String TAG = "SpeakActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speak);
		
		Intent checkIntent = new Intent();
	    checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);          
	    startActivityForResult(checkIntent, REQ_TTS_STATUS_CHECK);
		
		speakFragment = new SpeakFragment();
		speakFragment.setOnCompleteListener(new OnCompleteListener(){
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				speakFragment.setTts(tts);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, arg2);
		if (requestCode == REQ_TTS_STATUS_CHECK) {
            switch (resultCode) {
            case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
                tts = new TextToSpeech(this, this);
                break;
            case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA:
            case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA:
            case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME:
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
                break;
            case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:
            default:
                Log.e(TAG, "Got a failure. TTS not available");
            }
        }
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
	public void onInit(int status) {
		// TODO Auto-generated method stub
		if (status == TextToSpeech.SUCCESS) {
			int result = tts.setLanguage(Locale.US);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}
}
