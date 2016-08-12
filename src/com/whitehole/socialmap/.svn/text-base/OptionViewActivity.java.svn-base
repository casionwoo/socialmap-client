package com.whitehole.socialmap;

import java.util.*;

import com.whitehole.socialmap.R;
import com.whitehole.socialmap.etc.*;
import com.whitehole.socialmap.network.*;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.ExpandableListView.OnChildClickListener;

public class OptionViewActivity extends Activity {
	private ApplicationClass applicationClass;
	
	ExpandableListView mList;
	
	public static String[] viewlist;
	ArrayList<ArrayList<String>> leaf = new ArrayList<ArrayList<String>>();
	
	//ArrayList<String> friends = new ArrayList<String>();	//친구 목록 리스트
	ArrayList<FriendsList> friends;
	//ArrayList<String> friends;
	ArrayList<String> labels = new ArrayList<String>();		//label 리스트
	
	int[] selectedGroup; 
	//ArrayList<String> friendsData;
	ArrayList<FriendsList> friendsData;
	
	ArrayList<String> labelsData;
	
	ConnectivityManager mgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_option_view);
		
		viewlist = new String[]{
				getResources().getString(R.string.my_phone_photos),
				getResources().getString(R.string.my_facebook_photos),
				getResources().getString(R.string.all_user_photos),
				getResources().getString(R.string.label_photos),
				getResources().getString(R.string.facebook_friends_photos)
		};
		
		mgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		
		//어플리케이션의 변수들을 초기화
		applicationClass = (ApplicationClass)getApplicationContext();
		
		selectedGroup = new int[viewlist.length];
		friendsData = new ArrayList<FriendsList>();
		labelsData = new ArrayList<String>();
		
		GetFriendsList gf = new GetFriendsList();
		
		// 친구목록 불러오는 네트워크 함수
		if(applicationClass.session != null &&
				applicationClass.session.isOpened() && mgr.getActiveNetworkInfo() != null)	
			friends = gf.getmyfriends();
		else if(mgr.getActiveNetworkInfo() == null)
			// 네트워크 연결되어있지 않음
			Toast.makeText(OptionViewActivity.this, R.string.network_connect,
	                Toast.LENGTH_SHORT).show();
		
		if(friends == null)
			friends = new ArrayList<FriendsList>();
		
		Comparator<FriendsList> fCompare = new Comparator<FriendsList>(){

			@Override
			public int compare(FriendsList lhs, FriendsList rhs) {
				return lhs.name.compareTo(rhs.name);
			}
		};
		
		//친구이름을 이름순으로 정렬
		Collections.sort(friends, fCompare);
		
		//TODO: 라벨 목록 불러와 할당
		//labels = 
		
		//할당 했다고 가정하고 테스트
//		friends.add("홍길동");
//		friends.add("김철수");
//		friends.add("바보");
//		friends.add("EH");
//		labels.add("test1");
//		labels.add("미국");
		
		
		mList = (ExpandableListView) findViewById(R.id.option_view_list);
		
		//화살표 없애기
		mList.setGroupIndicator(null);
		
		List<Map<String, String>> viewlistData = new ArrayList<Map<String,String>>();
		//List<List<Map<String, String>>> friendsData = new ArrayList<List<Map<String,String>>>();
		//List<List<Map<String, String>>> labelsData = new ArrayList<List<Map<String,String>>>();
		List<List<Map<String, String>>> leafData = new ArrayList<List<Map<String,String>>>();

		for(int i=0; i< viewlist.length; i++){
			List<Map<String, String>> children = new ArrayList<Map<String,String>>();
			Map<String, String> ViewList = new HashMap<String, String>();
			ViewList.put("viewList", viewlist[i]);
			viewlistData.add(ViewList);
			
			//내 갤러리 사진, 내 SNS 사진, 라벨 별 사진에 들어갈 자식은 없음
			//전체보기에 들어갈 자식은 없음	
			Map<String, String> item1 = new HashMap<String, String>();
			
//			if(i==0|| i==1 || i==4){
//				item1.put("leaf", viewlist[i]);
//				children.add(item1);
//			}
//			else 
			if(i==4){
				for(FriendsList s: friends){
					Map<String, String> item2 = new HashMap<String, String>();
					item2.put("leaf", s.name);
					children.add(item2);
				}
			}
			else{
//				for(String s: labels){
//					Map<String, String> item2 = new HashMap<String, String>();
//					item2.put("leaf", s);
//					children.add(item2);
//				}
				
				item1.put("leaf", viewlist[i]);
				children.add(item1);
			}
			
			leafData.add(children);	
		}
		
		ExpandableListAdapter adapter = new SimpleExpandableListAdapter(this, 
																		viewlistData,
																		R.layout.my_expandable_list_layout, 
																		new String[]{"viewList"}, 
																		new int[]{android.R.id.text1}, 
																		
																		leafData, 
																		android.R.layout.simple_list_item_multiple_choice, 
																		new String[]{"leaf"}, 
																		new int[]{android.R.id.text1});
		mList.setAdapter(adapter);
		mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mList.setOnChildClickListener(mItemClickListener);
		
		//모든 그룹을 펼쳐 놓음
		mList.expandGroup(0);
		mList.expandGroup(1);
		mList.expandGroup(2);
		mList.expandGroup(3);
		mList.expandGroup(4);
		

		//그룹을 클릭했을때 접히지 않게 고정
		mList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener(){

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
	
				return true;
			}
			
		});
		
		Button cancel = (Button) findViewById(R.id.view_option_cancel);
		cancel.setOnClickListener(new View.OnClickListener (){

			@Override
			public void onClick(View v) {
				//현재 액티비티 종료
				finish();
			}
			
		});
		
		Button select = (Button) findViewById(R.id.view_option_select);
		select.setOnClickListener(new View.OnClickListener (){

			@Override
			public void onClick(View v) {
				//TODO: 선택 정보 전송 후: 어플리케이션 클래스에 정보저장
//				String tdata = "정보 전송!!!!!";
//				
//				Intent intent = new Intent(OptionViewActivity.this, MapTestActivity.class);
//				intent.putExtra("tdata", tdata);
				
				//Test
//				Log.w(ACCOUNT_SERVICE, applicationClass.selectedGroup.length+"");
//				for(int i=0; i < viewlist.length; i++)
//				{
//					Log.w(ACCOUNT_SERVICE, applicationClass.selectedGroup[i]+"");
//				}
//				Log.w(ACCOUNT_SERVICE,"select friends:");
//				for(String s: applicationClass.friendsData)
//					Log.w(ACCOUNT_SERVICE, s);
//				
//				Log.w(ACCOUNT_SERVICE,"select labels:");
//				for(String s: applicationClass.labelsData)
//					Log.w(ACCOUNT_SERVICE, s);
				
				//swap
				int tmp = selectedGroup[2];
				selectedGroup[2] = selectedGroup[4];
				selectedGroup[4] = tmp;
				
				for(int i=0; i < selectedGroup.length; i++){
					if(selectedGroup[i]==1){
						applicationClass.isSelectOptionView = true;
						
						applicationClass.selectedGroup = selectedGroup;
						applicationClass.friendsData = friendsData;
						//applicationClass.labelsData = labelsData;
						
						break;
					}		
				}
					
				
				//현재 액티비티 종료
				finish();
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_option_view, menu);
		return true;
	}

	ExpandableListView.OnChildClickListener mItemClickListener = 
			new OnChildClickListener() {

				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
					
					//항목 체크 액션
					int position;
					int groupPos;
					
					//v.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(section));
					
//					
//					if(groupPosition==0)
//						position = groupPosition + childPosition + 1;
//					else if(groupPosition==1)
//						//position = groupPosition*2 + childPosition + 1;
//						position = parent.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(groupPosition)) + childPosition + 1;
//					else if(groupPosition==2)
//						position = groupPosition*2 + childPosition + 1;
//					else if(groupPosition==3)
//						position = friends.size() + 1 + childPosition + 5;
//					else
//						position = labels.size() + 1 + friends.size() + 1 + childPosition + 5;
					
					// 선택한 해당 그룹으로 부터 포지션 얻기
					position = parent.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(groupPosition)) + childPosition + 1;
					
					//특정 포지션으로 부터 그룹 얻기!!!
					//ExpandableListView.getPackedPositionGroup(mView.getExpandableListPosition(position));
					groupPos = ExpandableListView.getPackedPositionGroup(parent.getExpandableListPosition(position));
					
					//체크해제
					if(parent.isItemChecked(position) ){
						parent.setItemChecked(position, false);
						//친구일 경우
						if(groupPos == 4){
							friendsData.remove(friends.get(childPosition));
							
							if(friendsData.size()==0)
								//selectedGroup[groupPos] = 0;
								selectedGroup[groupPos] = 0;
						}
						//라벨일 경우
//						else if(groupPos == 3){
//							labelsData.remove(labels.get(childPosition));
//							
//							if(labelsData.size()==0)
//								selectedGroup[groupPos] = 0;
//						}
						//그 외
						else
							selectedGroup[groupPos] = 0;
					}
					//체크적용
					else{
						parent.setItemChecked(position, true);
						
						//친구일 경우
						if(groupPos == 4 && friendsData.contains(friends.get(childPosition)) == false)
							friendsData.add(friends.get(childPosition));
						//라벨일 경우
//						else if(groupPos == 3 && labelsData.contains(labels.get(childPosition)) == false)
//							labelsData.add(labels.get(childPosition));
						
						selectedGroup[groupPos] = 1;	
					}
						//parent.getCheckedItemPositions();
						
						//parent.getChildAt(0).setEnabled(false);
					
					// TODO 다른 그룹 체크 비활성화 하기
					parent.setSaveEnabled(true);
					return false;
				}
			};
}
