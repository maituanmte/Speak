package com.mte.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.mte.speaks.R;
import com.mte.adapters.MessageAdapter;
import com.mte.adapters.MessageAdapter.OnSpeechMessageListener;
import com.mte.http.Http;
import com.mte.http.OnResponseListener;
import com.mte.models.Message;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SmsFragment extends BaseFragment implements OnResponseListener,
		OnInitListener {
	public static final String TAG = "SmsFragment";

	private List<Message> messages = new ArrayList<Message>();
	private MessageAdapter adapter = null;
	private TextToSpeech tts;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_sms, container, false);
		return view;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		ListView messagesView = (ListView) this.getView().findViewById(
				R.id.list_sms);
		adapter = new MessageAdapter(this.getActivity(), messages);
		messagesView.setAdapter(adapter);
		adapter.setOnSpeechMessageListener(new OnSpeechMessageListener() {

			@Override
			public void onSpeech(Message message) {
				// TODO Auto-generated method stub
				readMessage(message);
			}
		});
		this.resuse();
		tts = new TextToSpeech(this.getActivity(), this);
	}
	
	public void resuse(){
		this.loadMessages();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();		
	}

	private void loadMessages() {
		messages.clear();
		ContentResolver contentResolver = this.getActivity()
				.getContentResolver();
		Cursor cursor = contentResolver.query(Uri.parse("content://sms/"),
				null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Message message = new Message();
				message.setId(cursor.getString(cursor
						.getColumnIndexOrThrow("_id")));
				message.setAddress(cursor.getString(cursor
						.getColumnIndexOrThrow("address")));
				message.setBody(cursor.getString(cursor
						.getColumnIndexOrThrow("body")));
				message.setDate(cursor.getString(cursor
						.getColumnIndexOrThrow("date")));
				message.setRead(cursor.getString(cursor
						.getColumnIndexOrThrow("read")));
				if (cursor.getString(cursor.getColumnIndexOrThrow("type"))
						.contains("1")) {
					message.setType("inbox");
				} else {
					message.setType("sent");
				}
				messages.add(message);

			} while (cursor.moveToNext());
		}
	}

	private void readMessage(Message message) {
		Http http = new Http();
		SharedPreferences settings = getActivity().getSharedPreferences(
				SettingsFragment.TAG, 0);
		Boolean speechEngine = settings.getBoolean("engine", true);
		int p = settings.getInt("p", 70);
		int g = settings.getInt("g", 2);
		int s = settings.getInt("s", 175);
		int a = settings.getInt("a", 100);
		String v = settings.getString("v", "f3");
		if (!speechEngine) {
			http.setParam("text", message.getBody());
			http.setParam("p", String.valueOf(p));
			http.setParam("s", String.valueOf(s));
			http.setParam("g", String.valueOf(g));
			http.setParam("a", String.valueOf(a));
			http.setParam("v", v);

			http.setUrl(Http.TTS_URL + "/texttospeech/espeak.php");

			http.setOnResponseListener(this);
			http.request();
		} else {
			// tts.setSpeechRate(s);
			tts.speak(message.getBody(), TextToSpeech.QUEUE_FLUSH, null);
		}
		ContentResolver contentResolver = this.getActivity().getContentResolver();
		ContentValues content = new ContentValues();
		content.put("read", "1");
		contentResolver.update(Uri.parse("content://sms/"), content, "_id='"+message.getId()+"'", null);
		this.resuse();
	}

	@Override
	public void onResponse(String response) {
		// TODO Auto-generated method stub
		if (response != null) {
			Uri uri = Uri.parse(Http.TTS_URL + response);
			MediaPlayer media = MediaPlayer.create(getActivity(), uri);
			media.start();
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
