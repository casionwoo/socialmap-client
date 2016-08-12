package com.whitehole.socialmap.more;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.whitehole.socialmap.R;
import com.whitehole.socialmap.network.server.GetNotice;
import com.whitehole.socialmap.network.server.Notice_List;


import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.app.Activity;

public class sub_notice_activity extends Activity {
	ExpandableListView mList;
	List<Notice_List> noti;
	
	private GetNotice getnoti = new GetNotice();
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sub_notice_activity);

		mList = (ExpandableListView)findViewById(R.id.noticelist);
		
		noti = getnoti.GetTotalNotice();
		
		List<Map<String, String>> noticeData = new ArrayList<Map<String, String>>();
		List<List<Map<String, String>>> textData = new ArrayList<List<Map<String, String>>>();
		
		// List에 추가할 요소 List를 만듬
         for (int i = 0; i < noti.size(); i++) {
        	 Map<String, String> notice = new HashMap<String, String>();
             notice.put("subject", noti.get(i).Subject);
             noticeData.add(notice);

             List<Map<String, String>> children = new ArrayList<Map<String, String>>();
             Map<String, String> text = new HashMap<String, String>();
             text.put("content", noti.get(i).Content);
             children.add(text);
             
             textData.add(children);
         }
         
         // ExpandableList에 추가
         ExpandableListAdapter adapter = new SimpleExpandableListAdapter(
                 this,
                 // 화면에 뿌려줄 데이터 호출
                 noticeData,
                 // 사용할 리스트뷰 호출
                 android.R.layout.simple_expandable_list_item_1,
                 // 뿌려줄 값의 hash의 key를 적어준다.
                 new String[] { "subject" },
                 // 뿌려줄 TextView를 불러온다.
                 new int[] { android.R.id.text1 },
                 textData,
                 android.R.layout.simple_expandable_list_item_1,
                 new String[] { "content" },
                 new int[] { android.R.id.text1 }
   );
   mList.setAdapter(adapter);
   }
}