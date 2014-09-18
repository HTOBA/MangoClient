package com.special.ResideMenuDemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.transition.ChangeBounds;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.grallery3d.*;
import com.example.mangoclient.R;

import android.widget.TextView;
import android.app.Activity;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class RecbookFragment_2 extends Fragment {

	private TextView tvTitle;
	private GalleryView gallery;
	private ImageAdapter_1 adapter;
	private View recview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		recview = inflater.inflate(R.layout.recbook_2, container, false);
		initRes();
		return recview;
	}

	private void initRes() {
		tvTitle = (TextView) recview.findViewById(R.id.tvTitle_2);
		gallery = (GalleryView) recview.findViewById(R.id.mygallery_2);

		adapter = new ImageAdapter_1(getActivity());
		adapter.createReflectedImages();
		gallery.setAdapter(adapter);

		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				tvTitle.setText(adapter.titles[position]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		gallery.setOnItemClickListener(new OnItemClickListener() { // 设置点击事件监听
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getActivity(), "correct!", Toast.LENGTH_LONG)
						.show();
//				 SettingsFragment settingsfragment = new SettingsFragment();
//				 FragmentTransaction transaction =getFragmentManager().beginTransaction();
//				 transaction.replace(R.id.main_fragment,settingsfragment);
//				 transaction.commit();
//				 //change.changeFragment(new SettingsFragment());
			}
		});
	}
}
