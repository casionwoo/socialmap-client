package com.whitehole.socialmap.hotcool;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.novoda.imageloader.core.ImageManager;
import com.novoda.imageloader.core.LoaderSettings;
import com.novoda.imageloader.core.cache.LruBitmapCache;
import com.novoda.imageloader.core.loader.Loader;
import com.novoda.imageloader.core.model.ImageTag;
import com.novoda.imageloader.core.model.ImageTagFactory;
import com.whitehole.socialmap.R;
import com.whitehole.socialmap.etc.*;
import com.whitehole.socialmap.network.SPicture;
import com.whitehole.socialmap.network.server.GetHotCool;

public class PreHotPlace extends Activity implements OnScrollListener, OnItemClickListener {

	private CustomAdapter mAdapter;
	private ListView mListView;
	private LayoutInflater mInflater;
	private List<SPicture> mRowList;

	private boolean mLockListView;

	TextView mPreHotName;
	TextView mPreHotPlaceLocation;
	ImageView mPreHotPlaceImage;

	List<SPicture> sp;

	// 핫플레이스 존재하지 않을 경우 토스트메시지 출력
	Toast mToast = null;

	//이미지로더 변수
	static ImageManager imageManager;
	Loader imageLoader;
	ImageTagFactory imageTagFactory;

	private GetHotCool getHotPlace = new GetHotCool();

	public final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssZ" , Locale.KOREA);
	
	public final SimpleDateFormat DATE_PRE_HOT_FORMAT = new SimpleDateFormat(
			"yyyy년 MM월 dd일 E요일 HH시" , Locale.KOREA);
	
	public final SimpleDateFormat DATE_PRE_FORMAT = new SimpleDateFormat(
			"yyyy년 MM월 dd일 E요일 HH시 mm분" , Locale.KOREA);
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sub_hotcool_prehot);

		//이미지 로더 셋팅
		LoaderSettings settings = new LoaderSettings.SettingsBuilder().withCacheManager(new LruBitmapCache(this))
				.withDisconnectOnEveryCall(true).build(this);

		imageManager = new ImageManager(this, settings);
		imageLoader = imageManager.getLoader();

		imageTagFactory = new ImageTagFactory(this, R.drawable.ic_launcher);
		imageTagFactory.setErrorImageId(R.drawable.ic_launcher);

		sp = getHotPlace.GetPreHotPlace();
		Log.i("PRE SP SIZE", ""+sp.size());

		if(sp.size() == 0)
		{
			mToast = Toast.makeText(this, "SORRY, There is NO HOTPLACE! :(", Toast.LENGTH_LONG);
			mToast.show();
		}
		spParser(sp);

		Button btn = (Button)findViewById(R.id.close);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});		

		// 멤버 변수 초기화
		mRowList = new ArrayList<SPicture>(sp);
		mLockListView = true;

		// 어댑터와 리스트뷰 초기화
		mAdapter = new CustomAdapter(this, sp);
		mListView = (ListView)findViewById(R.id.prehotlistview);

		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(this);
	}

	private void spParser(List<SPicture> sp){
		for(SPicture _sp : sp){
			addItems(_sp);
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
		// 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고
		// 가정합니다.
		int count = totalItemCount - visibleItemCount;
		Log.i("totalItemCount =", ""+totalItemCount);
		Log.i("visibleItemCount =", ""+visibleItemCount);
		Log.i("firstVisibleItem =", ""+firstVisibleItem);

		List<SPicture> sp;
		if(firstVisibleItem > count && totalItemCount != 0 && mLockListView == false)
		{
			Log.i("111111111111111111", "Loading next items");
			//sp = getHotPlace.GetHotPlace();
			//if(sp.size() == 0)
			//	return;
			//for(SPicture _sp : sp){
			//	addItems(_sp);
			//}
		}
		if(firstVisibleItem == count && totalItemCount != 0 && mLockListView == false)
		{
			Log.i("111111111111111111", "Loading next items22222222");
			mLockListView = true;
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
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
		handler.postDelayed(run, 5000);
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

			String preTime = null;
			Date time = null;
			convertView = mInflater.inflate(R.layout.sub_hotcool_prehot_row, parent, false);

			// 항목 뷰를 초기화
			TextView tvpretime = (TextView) convertView.findViewById(R.id.stdtime);
			TextView tvname = (TextView) convertView.findViewById(R.id.pre_name);
			TextView tvlocation = (TextView) convertView.findViewById(R.id.pre_location);
			ImageView tvimg = (ImageView) convertView.findViewById(R.id.pre_img);
			TextView tvtime = (TextView) convertView.findViewById(R.id.pre_time);
			
			preTime = mRowList.get(finalPosition).makeTimeS; 
			try {
				 time = DATE_FORMAT.parse(preTime);
			} catch (ParseException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			
			tvpretime.setText(DATE_PRE_HOT_FORMAT.format(time).toString()+"~");

			try {
				tvname.setText(URLDecoder.decode(mRowList.get(finalPosition).ownerName, "EUC-KR"));
			} catch (UnsupportedEncodingException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			try {
				tvlocation.setText(URLDecoder.decode(mRowList.get(finalPosition).placeName, "EUC-KR"));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			tvtime.setText(DATE_PRE_FORMAT.format(time).toString());

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
//		String str_imgurl = sp.get(position).imageURL;
//		String str_placename = sp.get(position).placeName;
//		String str_ownername = sp.get(position).ownerName;
//		String str_ownerthumurl = sp.get(position).ownerThumURL;
//		String str_record = sp.get(position).record;
//		
		Intent intent = new Intent(this, SelectedHotImg.class);
//		intent.putExtra("imgurl", str_imgurl);
//		intent.putExtra("placename", str_placename);
//		intent.putExtra("ownername", str_ownername);
//		intent.putExtra("ownerthumurl", str_ownerthumurl);
//		intent.putExtra("record", str_record);
		
		ApplicationClass applicationClass = (ApplicationClass)getApplicationContext();
		applicationClass.tmpHotSPicture = sp.get(position);
		
		startActivity(intent);
	}
}