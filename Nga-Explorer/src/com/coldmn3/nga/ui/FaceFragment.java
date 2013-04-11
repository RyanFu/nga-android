package com.coldmn3.nga.ui;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.coldmn3.nga.R;
import com.coldmn3.nga.adapter.EmoticonAdapter;

public final class FaceFragment extends Fragment {
	private static final String KEY_CONTENT = "TestFragment:Content";
	EmoticonAdapter adapter;
	GridView gridView;

	public static FaceFragment newInstance(String content) {
		FaceFragment fragment = new FaceFragment();

		Bundle args = new Bundle();
		args.putString("catory", content);
		fragment.setArguments(args);

		return fragment;
	}

	private String mContent = "???";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.face_fragment, null);
		gridView = (GridView) view.findViewById(R.id.face);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle bundle = getArguments();
		String catory = bundle.getString("catory");
		adapter = new EmoticonAdapter(getActivity(), catory);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bitmap bitmap = (Bitmap) FaceFragment.this.adapter.getItem(position);
				Matrix matrix = new Matrix();
				matrix.postScale(0.4f, 0.4f);
				Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
				ImageSpan imageSpan = new ImageSpan(getActivity(), b);
				String imageDes = FaceFragment.this.adapter.getFaceURL(position);
				SpannableString spannableString = new SpannableString(imageDes);
				spannableString.setSpan(imageSpan, 0, imageDes.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

		});

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}
}
