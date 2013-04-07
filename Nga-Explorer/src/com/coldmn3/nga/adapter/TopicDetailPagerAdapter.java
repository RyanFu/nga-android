package com.coldmn3.nga.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.coldmn3.nga.ui.TopicDetailFragment;

public class TopicDetailPagerAdapter extends FragmentStatePagerAdapter {

	private int mPageCount;

	public TopicDetailPagerAdapter(FragmentManager fm, int pageCount) {
		super(fm);
		this.mPageCount = pageCount;
	}

	@Override
	public Fragment getItem(int position) {
		return TopicDetailFragment.newInstance(String.valueOf(position + 1));
	}

	@Override
	public int getCount() {
		return mPageCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return String.valueOf(position + 1);
	}

}
