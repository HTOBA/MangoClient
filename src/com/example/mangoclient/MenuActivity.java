package com.example.mangoclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.special.ResideMenuDemo.CalendarFragment;
import com.special.ResideMenuDemo.HomeFragment;
import com.special.ResideMenuDemo.RecbookFragment;
import com.special.ResideMenuDemo.SettingsFragment;
import com.example.mangoclient.R;

public class MenuActivity extends FragmentActivity implements
		View.OnClickListener {

	private ResideMenu resideMenu;
	private MenuActivity mContext;
	private ResideMenuItem itemHome;
	private ResideMenuItem itemProfile;
	private ResideMenuItem itemCalendar;
	private ResideMenuItem itemSettings;
	private Intent intent;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmain);
		mContext = this;
		setUpMenu();
		changeFragment(new HomeFragment());

	}

	private void setUpMenu() {

		
		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.drawable.menu_background);
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(menuListener);
		resideMenu.setScaleValue(0.6f);
		resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
		resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);

		
		itemHome = new ResideMenuItem(this, R.drawable.icon_home, "Home");
		itemProfile = new ResideMenuItem(this, R.drawable.icon_hot,
				"Hot&New");
		itemCalendar = new ResideMenuItem(this, R.drawable.icon_search,
				"Search");
		itemSettings = new ResideMenuItem(this, R.drawable.icon_settings,
				"About us");

		itemHome.setOnClickListener(this);
		itemProfile.setOnClickListener(this);
		itemCalendar.setOnClickListener(this);
		itemSettings.setOnClickListener(this);

		resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemCalendar, ResideMenu.DIRECTION_RIGHT);
		resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_RIGHT);

		
		findViewById(R.id.title_bar_left_menu).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
					}
				});
		findViewById(R.id.title_bar_right_menu).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);// 滑动
					}
				});
	}

	@Override
	public void onClick(View view) {

		if (view == itemHome) {
			changeFragment(new HomeFragment());
		} else if (view == itemProfile) {
			changeFragment(new RecbookFragment());
		} else if (view == itemCalendar) {
			//changeFragment(new CalendarFragment());
			intent = new Intent(MenuActivity.this,MainActivity.class);
			startActivity(intent);
		} else if (view == itemSettings) {
			changeFragment(new SettingsFragment());
//			intent = new Intent(MenuActivity.this,MyInfoActivity.class);
//			startActivity(intent);
		}

		resideMenu.closeMenu();
	}

	private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
		@Override
		public void openMenu() {
			// Toast.makeText(mContext, "Menu is opened!",
			// Toast.LENGTH_SHORT).show();
		}

		@Override
		public void closeMenu() {
			// Toast.makeText(mContext, "Menu is closed!",
			// Toast.LENGTH_SHORT).show();
		}
	};

	private void changeFragment(Fragment targetFragment) {
		resideMenu.clearIgnoredViewList();
		getSupportFragmentManager().beginTransaction()// �?���?��事务
				.replace(R.id.main_fragment, targetFragment, "fragment")// 向容器中加入碎片
				// .addToBackStack(null)//返回前面的栈
				.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)// 设置动画效果
				.commit();
	}

	public ResideMenu getResideMenu() {
		return resideMenu;
	}
}
