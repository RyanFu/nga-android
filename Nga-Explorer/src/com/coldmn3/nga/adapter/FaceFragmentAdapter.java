package com.coldmn3.nga.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.coldmn3.nga.ui.FaceFragment;

public class FaceFragmentAdapter extends FragmentPagerAdapter {
	private int pageCount = 8;

	public FaceFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return FaceFragment.newInstance(String.valueOf(position));
	}

	@Override
	public int getCount() {
		return pageCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return String.valueOf(position + 1);
	}

	public void setCount(int count) {

		notifyDataSetChanged();
	}
}