package com.mte.adapters;

import java.util.ArrayList;
import java.util.List;
import com.mte.models.Message;
import com.mte.speak.R;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MessageAdapter extends ArrayAdapter<Message> implements ListAdapter {
	private Activity context;
	List<Message> messages;
	private List<OnSpeechMessageListener> listeners = new ArrayList<OnSpeechMessageListener>();
	
	public MessageAdapter(Activity context, List<Message> messages) {
		super(context, R.layout.sms_list, messages);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.messages = messages;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		if(convertView == null){	
			LayoutInflater inflater = this.context.getLayoutInflater();
			convertView = inflater.inflate(R.layout.message_row, null);				
			ViewHolder holder = new ViewHolder();
			holder.Address = (TextView) convertView.findViewById(R.id.sms_phone);
			holder.Content = (TextView) convertView.findViewById(R.id.row_content);
			holder.Speech = (ImageButton)convertView.findViewById(R.id.sms_sound);
			convertView.setTag(holder);			
		}
		
		ViewHolder holder = (ViewHolder)convertView.getTag();
		final Message message = this.messages.get(position);
		holder.Address.setText(message.getAddress());
		if(message.getRead().contains("0")){
			holder.Address.setTypeface(null, Typeface.BOLD);
		}
		holder.Content.setText(message.getBody());
		holder.Speech.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dispatchEvent(message);
			}			
		});
		return convertView;
	}
	
	public void setOnSpeechMessageListener(OnSpeechMessageListener listener){
		listeners.add(listener);
	}
	
	public void dispatchEvent(Message message){
		for(OnSpeechMessageListener listener : listeners){
			listener.onSpeech(message);
		}
	}
	
	public interface OnSpeechMessageListener{
		public void onSpeech(Message message);
	}
	
	private static class ViewHolder{
		ImageButton Speech;
		TextView Address;
		TextView Content;
	}
}
