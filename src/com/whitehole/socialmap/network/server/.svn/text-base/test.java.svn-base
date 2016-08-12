package com.whitehole.socialmap.network.server;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

import com.whitehole.socialmap.R;
import com.whitehole.socialmap.network.SPicture;

public class test extends Activity {
	
	private SPicture sPicture = new SPicture();
	private ArrayList<SPicture> array = new ArrayList<SPicture>();
	private PostDeleteSever postPublicPhotos = new PostDeleteSever();
	private GetPhotos getPhotos = new GetPhotos();
	
	ArrayList<String> delete_pic = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hot_cool); 
		
		TabHost tabHost = (TabHost)findViewById(R.id.tabHost);
		
		  // TabHost 를 findViewById 로 생성한 후 Tab 추가 전에 꼭 실행해 주어야 함.
	    tabHost.setup();
	    
	    // 새로운 Tab 을 생성하기 위한 Tab 객체 생성
	    TabHost.TabSpec spec;
	    
	    // 첫 번째 Tab 설정 및 등록
	    spec = tabHost.newTabSpec( "Tab 00" ); 	// 새 Tab 생성
	    spec.setIndicator("Hot Place");				// Tab 제목
	    spec.setContent(R.id.layout);			// Tab 내용
	    tabHost.addTab(spec);					// 생성 된 Tab 등록
	    
	    /*Button btn = (Button)findViewById(R.id.test);
        <Button
        android:id="@+id/test"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:text="Test Button"
        android:layout_weight="1"
        />*/
	    
	    // 두 번째 Tab 설정 및 등록        
	    spec = tabHost.newTabSpec( "Tab 01" ); 	// 새 Tab 생성
	    spec.setIndicator("Coming soon");			// Tab 제목
	    spec.setContent(R.id.layout);		// Tab 내용
	    tabHost.addTab(spec);					// 생성 된 Tab 등록
	    
	    for(int tab=0; tab<tabHost.getTabWidget().getChildCount(); ++tab){
	    	tabHost.getTabWidget().getChildAt(tab).getLayoutParams().height=70;
	    }
	    
	    // 첫 번째 탭을 활성화 
	    tabHost.setCurrentTab( 0 );
		
				
		delete_pic.add("21313265456465");
		delete_pic.add("11111111111111");
		
		/*btn.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				Log.i("TEST", "Button Clicked");
				getPhotos.GetClusteredPhotos("107214942812014",37.56, 126.94, 37.54, 126.92);
			}
		});*/
	}
}