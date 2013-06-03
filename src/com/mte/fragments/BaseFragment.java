package com.mte.fragments;

import java.util.ArrayList;
import java.util.List;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
	List<OnCompleteListener> listeners = new ArrayList<OnCompleteListener>();
	public void setOnCompleteListener(OnCompleteListener listener){
		listeners.add(listener);
	}
	
	public void dispatchOnCompleteListener(){
		for(OnCompleteListener listener: listeners){
			listener.onComplete();
		}
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		dispatchOnCompleteListener();
	}
	
}
