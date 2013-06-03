package com.mte.adapters;

import java.util.ArrayList;
import java.util.List;
import com.mte.models.History;
import com.mte.speak.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class HistoryAdapter extends BaseAdapter {
	private List<History> histories;
	private Context context;
	private LayoutInflater mInflater;
	private List<OnRespeechListener> listeners = new ArrayList<OnRespeechListener>();
	
	public HistoryAdapter(Context context, List<History> histories) {
		this.context = context;
		this.histories = histories;
		mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.histories.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return this.histories.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View getView(int pos, View view, ViewGroup viewGroup) {
		ViewHolder holder;
		if(view == null || view.getTag() == null){
			view = this.mInflater.inflate(R.layout.list_his_row, null);
			holder = new ViewHolder();
			holder.content = (TextView)view.findViewById(R.id.his_content);
			holder.sound = (ImageButton)view.findViewById(R.id.his_sound);
			view.setTag(holder);
		}else{
			holder = (ViewHolder) view.getTag();
		}
		
		final History his = this.histories.get(pos);
		holder.content.setText(his.getSummary());
		holder.sound.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dispatchEvent(his);
			}});
		return view;
	}
	
	public void setOnRespeechListener(OnRespeechListener listener){
		listeners.add(listener);
	}
	
	public void dispatchEvent(History his){
		for(OnRespeechListener listener : listeners){
			listener.respeech(his);
		}
	}
	
	public interface OnRespeechListener{
		public void respeech(History his);
	}

	private class ViewHolder{
		public ImageButton sound;
		public TextView content;
	}
}
