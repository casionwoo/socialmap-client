package com.whitehole.socialmap;

import com.whitehole.socialmap.R;
import com.whitehole.socialmap.hotcool.HotCoolPlace;
import com.whitehole.socialmap.more.*;
import com.whitehole.socialmap.network.server.test;
import com.whitehole.socialmap.upload.*;

import android.os.Bundle;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import android.widget.TabHost.*;

public class MainActivity extends TabActivity{
	//public class MainActivity extends MapActivity {
	TabHost mTab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_map);
		TabHost mTab = getTabHost();
		
		LayoutInflater inflater = LayoutInflater.from(this);
		inflater.inflate(R.layout.activity_tab, mTab.getTabContentView(), true);
		
		mTab.addTab(mTab.newTabSpec("tag1").setIndicator("Map", getResources().getDrawable(R.drawable.tap_earth_selector)).setContent(new Intent(this, MapTestActivity.class)));
		
		mTab.addTab(mTab.newTabSpec("tag2").setIndicator("Hot & Cool", getResources().getDrawable(R.drawable.tap_place_selector)).setContent(new Intent(this, HotCoolPlace.class)));
		
//		mTab.addTab(mTab.newTabSpec("tag3").setIndicator("Upload").setContent(new Intent(this, PostPicture.class)));
		
//		mTab.addTab(mTab.newTabSpec("tag4").setIndicator("Label").setContent(R.id.opt_compiler));
		
		mTab.addTab(mTab.newTabSpec("tag5").setIndicator("More", getResources().getDrawable(R.drawable.tap_more_selector)).setContent(new Intent(this, MoreMainActivity.class)));
		
		//탭 배경 색 지정
		//mTab.setBackgroundColor(Color.parseColor(""));
		
		//탭 색 지정
//		for(int i=0; i < mTab.getTabWidget().getChildCount(); i++){
//			if(i==0)
//				mTab.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#C4E4D9"));
//			else
//				mTab.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#2F6143"));
//		}
	}

//	@Override
//	public void onTabChanged(String arg0) {
//		for(int i=0; i < mTab.getTabWidget().getChildCount(); i++){
//			mTab.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#2F6143"));
//		}
//		
//		mTab.getTabWidget().getChildAt(mTab.getCurrentTab()).setBackgroundColor(Color.parseColor("#C4E4D9"));
//	}
}
