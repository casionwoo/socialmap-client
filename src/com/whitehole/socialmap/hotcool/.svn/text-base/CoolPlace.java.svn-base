package com.whitehole.socialmap.hotcool;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.cache.LruBitmapCache;
import com.novoda.imageloader.core.loader.Loader;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;

import com.whitehole.socialmap.R;
import com.whitehole.socialmap.network.SPicture;
import com.whitehole.socialmap.network.server.GetCool;
import com.whitehole.socialmap.network.server.CoolSPicture;
import com.whitehole.socialmap.network.server.CCoolSPicture;

public class CoolPlace extends Activity implements OnItemClickListener
{
	private static final String LOG = "DynamicListViewActivity";
	private CustomAdapter mAdapter;
	private ListView mListView;
	private TextView mTextView;
	private LayoutInflater mInflater;
	
	private List<SPicture> mRowList;

	private boolean mLockListView;
	
	
//	List<SPicture> sp;
	CCoolSPicture sp;
	
	//이미지로더 변수
	static ImageManager imageManager;
	Loader imageLoader;
	ImageTagFactory imageTagFactory;
	
	private GetCool getCoolPlace = new GetCool();
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_hotcool_cool);
        
        //이미지 로더 셋팅
        LoaderSettings settings = new LoaderSettings.SettingsBuilder().withCacheManager(new LruBitmapCache(this))
      	      .withDisconnectOnEveryCall(true).build(this);

  	    imageManager = new ImageManager(this, settings);
  	    imageLoader = imageManager.getLoader();
  	    
  	    imageTagFactory = new ImageTagFactory(this, R.drawable.ic_launcher);
  	    imageTagFactory.setErrorImageId(R.drawable.ic_launcher);
  	    
  	    sp = getCoolPlace.GetCoolPlace();
  	    if(sp.getData().get(0).getPhotos().get(0).isPublic == 204)
  	    {
	        mListView = (ListView) findViewById(R.id.ClistView);
	        mTextView = (TextView) findViewById(R.id.CtextView);
	        
	        mTextView.setText("SORRY, There is NO COOLPLACE!.");
  	    }
  	    else
  	    {
  	    	
  	    	
  	    	GetFirstPlace(sp);
	        // 멤버 변수 초기화
	        mRowList = new ArrayList<SPicture>();
	        mLockListView = true;
	        
	        // 어댑터와 리스트뷰 초기화
	        mAdapter = new CustomAdapter(this, mRowList);
	        mListView = (ListView) findViewById(R.id.ClistView);
	        
	       	mListView.setAdapter(mAdapter);

	        mListView.setOnItemClickListener(this);
  	    }
	}
    
    // 각 사진의 첫번째 장소만 ArrayList에 추가
    private void GetFirstPlace(CCoolSPicture sp2) {
		// TODO Auto-generated method stub
    	Log.e("sp2.data size", "size = " + sp2.data.size());
		for(int i=0; i<sp2.data.size(); i++){
						
			if(sp2.data.get(i).photos.size() > 0){
				Log.e("sp2.data.get.photo size", "size= " + sp.getData().get(i).getPhotos().size());
				
				addItems(sp2.data.get(i).photos.get(0));
				
				Log.e("first place", sp2.getData().get(i).getPhotos().get(0).placeName);
			}
		}
	}

	/**
	 * 임의의 방법으로 더미 아이템을 추가합니다.
	 */
	private void addItems(final SPicture sp)
	{
		// 아이템을 추가하는 동안 중복 요청을 방지하기 위해 락을 걸어둡니다.
		mLockListView = true;
		
		Runnable run = new Runnable()
		{
			@Override
			public void run()
			{	                
				mRowList.add(sp);
				
				// 모든 데이터를 로드하여 적용하였다면 어댑터에 알리고 리스트뷰의 락을 해제합니다.
				mAdapter.notifyDataSetChanged();
				mLockListView = false;
			}
		};
		
		// 속도의 딜레이를 구현하기 위한 꼼수
		Handler handler = new Handler();
		handler.postDelayed(run, 500);
	}
	
	class CustomAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;
		private int resourceLayoutId;
		private List<SPicture> mRowList;

		public CustomAdapter(Context context, List<SPicture> mRowList)
		{
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			//resourceLayoutId = textViewResourceId;
			this.mRowList = mRowList;
		}

		@Override
		public int getCount()
		{
			return mRowList.size();
		}

		@Override
		public Object getItem(int position)
		{
			return mRowList.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			
			final int finalPosition = position;
			
			convertView = mInflater.inflate(R.layout.sub_hotcool_coolrow, parent, false);
		
			
			// 항목 뷰를 초기화
			ImageView tvimg = (ImageView) convertView.findViewById(R.id.cool_img);
			TextView tvlocation = (TextView) convertView.findViewById(R.id.cool_location);
			TextView tvname = (TextView) convertView.findViewById(R.id.cool_name);

			tvlocation.setText(mRowList.get(finalPosition).placeName);
			Log.e("CoolplaceName", mRowList.get(finalPosition).placeName);
			tvname.setText(mRowList.get(finalPosition).ownerName);
			
			ImageTag tag = imageTagFactory.build(mRowList.get(finalPosition).smallImageURL);
			tvimg.setTag(tag);
	    	//이미지 로더에 로드
	        imageLoader.load(tvimg);

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		
		CoolSPicture cs = sp.getData().get(position);
		Gson gson = new Gson();
		
		String strcs = gson.toJson(cs);
		Intent intent = new Intent(this, CoolClickActivity.class);
		intent.putExtra("coolplace", strcs);
		
		startActivity(intent);
	}
}
