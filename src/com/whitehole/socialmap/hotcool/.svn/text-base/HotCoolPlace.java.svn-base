package com.whitehole.socialmap.hotcool;

import java.util.ArrayList;

import android.R.anim;
import android.app.ActivityGroup;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.whitehole.socialmap.network.SPicture;
import com.whitehole.socialmap.*;

public class HotCoolPlace extends ActivityGroup {
	
	private SPicture sPicture = new SPicture();
	private ArrayList<SPicture> array = new ArrayList<SPicture>();

	ArrayList<String> delete_pic = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hot_cool); 
		
		TabHost tabHost = (TabHost)findViewById(R.id.tabHost);

		  // TabHost 를 findViewById 로 생성한 후 Tab 추가 전에 꼭 실행해 주어야 함.
	    tabHost.setup(this.getLocalActivityManager());
	    
	    // 새로운 Tab 을 생성하기 위한 Tab 객체 생성
	    TabHost.TabSpec spec;
	    
	    // 첫 번째 Tab 설정 및 등록
	    spec = tabHost.newTabSpec("Tab1"); 	// 새 Tab 생성
	    spec.setIndicator("Hot Place");				// Tab 제목
	    spec.setContent(new Intent(this, HotPlace.class));			// Tab 내용
	    
	    tabHost.addTab(spec);					// 생성 된 Tab 등록
	    
	    // 두 번째 Tab 설정 및 등록        
	    spec = tabHost.newTabSpec("Tab2"); 	// 새 Tab 생성
	    spec.setIndicator("Cool Place");			// Tab 제목
	    spec.setContent(new Intent(this, CoolPlace.class));		// Tab 내용
	    tabHost.addTab(spec);					// 생성 된 Tab 등록
	    
	    for(int tab=0; tab<tabHost.getTabWidget().getChildCount(); ++tab){
	    	tabHost.getTabWidget().getChildAt(tab).getLayoutParams().height=70;
	    }
	    
	    // 첫 번째 탭을 활성화 
	    tabHost.setCurrentTab(0);

	}
}