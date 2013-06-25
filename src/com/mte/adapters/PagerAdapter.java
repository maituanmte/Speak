package com.mte.adapters;

import java.util.List;
import com.mte.speaks.R;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> fragments;
	private Context context;

	public PagerAdapter(FragmentManager fm, List<Fragment> fragments,
			Context context) {
		super(fm);
		this.fragments = fragments;
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		return this.fragments.get(position);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			return this.context.getResources().getString(R.string.app_name);
		case 1:
			return this.context.getResources().getString(R.string.sms);
		case 2:
			return this.context.getResources().getString(R.string.action_settings);
		}
		return super.getPageTitle(position);
	}
}
