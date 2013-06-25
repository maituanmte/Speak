package com.mte.fragments;

import java.util.ArrayList;
import java.util.List;
import com.mte.adapters.HistoryAdapter;
import com.mte.adapters.HistoryAdapter.OnRespeechListener;
import com.mte.http.Http;
import com.mte.http.OnResponseListener;
import com.mte.models.History;
import com.mte.speaks.R;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.google.tts.TTS;

@SuppressWarnings("deprecation")
public class SpeakFragment extends BaseFragment implements OnClickListener,
		TextWatcher, OnResponseListener, OnSeekBarChangeListener {
	public static final String TAG = "SpeakFragment";
	private HistoryAdapter adapter;
	private List<History> histories = new ArrayList<History>();
	private TTS tts;
	private EditText contentEdit;
	private ImageView playBt;
	private SeekBar seekBar;
	private MediaPlayer media = null;

	private Boolean changed = false;
	private Boolean temChanged = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_speak, container, false);
		ListView historiesView = (ListView) view.findViewById(R.id.histories);
		contentEdit = (EditText) view.findViewById(R.id.content);
		contentEdit.addTextChangedListener(this);
		playBt = (ImageView) view.findViewById(R.id.play);
		playBt.setOnClickListener(this);
		seekBar = (SeekBar) view.findViewById(R.id.seek_play);
		seekBar.setOnSeekBarChangeListener(this);
		adapter = new HistoryAdapter(this.getActivity(), histories);
		adapter.setOnRespeechListener(new OnRespeechListener() {
			@Override
			public void respeech(History his) {
				// TODO Auto-generated method stub
				readHistory(his);
			}
		});
		historiesView.setAdapter(adapter);
		return view;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		this.temChanged = this.changed;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		this.changed = this.temChanged;
	}

	public void setContent(String content) {
		if (contentEdit != null)
			contentEdit.setText(content);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
			case R.id.play: {
				if (this.changed) {
					if (this.contentEdit.toString().length() > 0) {
						History his = new History();
						his.setContent(this.contentEdit.getText().toString());
						this.histories.add(his);
						this.adapter.notifyDataSetChanged();
						this.changed = false;
						readHistory(his);
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void readHistory(History his) {
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
			http.setParam("text", his.getContent());
			http.setParam("p", String.valueOf(p));
			http.setParam("s", String.valueOf(s));
			http.setParam("g", String.valueOf(g));
			http.setParam("a", String.valueOf(a));
			http.setParam("v", v);

			http.setUrl(Http.TTS_URL + "/texttospeech/espeak.php");

			http.setOnResponseListener(SpeakFragment.this);
			http.request();
		} else {
			this.tts.speak(his.getContent(), 0, null);
		}
	}

	@Override
	public void afterTextChanged(Editable editor) {
		this.changed = true;
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onResponse(String response) {
		// TODO Auto-generated method stub
		if (response != null) {
			final Uri uri = Uri.parse(Http.TTS_URL + response);
			media = MediaPlayer.create(getActivity(), uri);
			media.setAudioStreamType(AudioManager.STREAM_MUSIC);
			media.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
				@Override
				public void onBufferingUpdate(MediaPlayer media, int percent) {
					// TODO Auto-generated method stub
					seekBar.setProgress(media.getCurrentPosition());
				}
			});
			seekBar.setProgress(0);
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					int current = 0;
					int total = media.getDuration();
					seekBar.setMax(total);
					media.start();
					while (current < total && media.isPlaying()) {
						try {
							current = media.getCurrentPosition();
							seekBar.setProgress(current);
						} catch (Exception e) {
							return;
						}
					}
				}
			});
			thread.start();
		}
	}

	public void setTts(TTS tts) {
		this.tts = tts;
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		if (fromUser && media != null) {
			media.seekTo(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}
