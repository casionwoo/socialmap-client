package com.whitehole.socialmap.more;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.whitehole.socialmap.R;


public class sub_help_activity extends Activity implements OnClickListener  {
	private final int COUNT = 7;			// ������ ����
	private int hPrevPosition;						//���� ���õǾ�� ������ ��
	
	private ViewPager hPager;			
	private LinearLayout hPageMark;			//���� �� ������ ���� ��Ÿ���� ��	
	private Button hPrev, hNext;			// ��ư

	private int hImage[] = {
			R.drawable.helpcam1,R.drawable.helpcam2,
			R.drawable.helpcam3,R.drawable.helpcam4,
			R.drawable.helpcam5,R.drawable.helpcam6,
			R.drawable.helpcam7
	};
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sub_help_activity);
		
		hPageMark = (LinearLayout)findViewById(R.id.helpmark);		// ��� ������ ���� ��
		
		hPager = (ViewPager)findViewById(R.id.helppager);
		hPager.setAdapter(new HelpPagerAdapter(getApplicationContext()));
		
		hPager.setOnPageChangeListener(new OnPageChangeListener(){
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onPageSelected(int position){
				// TODO Auto-generated method stub
				hPageMark.getChildAt(hPrevPosition).setBackgroundResource(R.drawable.page_not);	//���� �������� �ش��ϴ� ������ ǥ�� �̹��� ����
				hPageMark.getChildAt(position).setBackgroundResource(R.drawable.page_select);		//���� �������� �ش��ϴ� ������ ǥ�� �̹��� ����
				hPrevPosition = position;				//���� ������ ���� ����� ����
				}
			});

		
		initPageMark();	//���� ������ ǥ���ϴ� �� �ʱ�ȭ
		
		hPrev = (Button)findViewById(R.id.helpprev);		//���� ���������� �̵� ��ư
		hPrev.setOnClickListener(this);
		
		hNext = (Button)findViewById(R.id.helpnext);		//���� ���������� �̵� ��ư
		hNext.setOnClickListener(this);
	}
	//����� ���� ������ ǥ���ϴ� �� �ʱ�ȭ
		private void initPageMark(){
			for(int i=0; i<COUNT; i++)
			{
				ImageView iv = new ImageView(getApplicationContext());	//������ ǥ�� �̹��� �� ��
				iv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				//ù ������ ǥ�� �̹��� �̸� ���õ� �̹�����
				if(i==0)
					iv.setBackgroundResource(R.drawable.page_select);
				else	//�������� ���þȵ� �̹�����
					iv.setBackgroundResource(R.drawable.page_not);

				//LinearLayout�� �߰�
				hPageMark.addView(iv);
			}
			hPrevPosition = 0;	//���� ������ �� �ʱ�ȭ
		}
	
	//Pager ����� ����
    private class HelpPagerAdapter extends PagerAdapter{
		private Context mContext;
    	//private LayoutInflater mInflater;
    	
    	public HelpPagerAdapter( Context con) { super(); mContext = con; }
    	
    	@Override public int getCount() { return COUNT; }
    	

    	// �������� ����� �䰴ü �� �� ���
    	@Override public Object instantiateItem(View pager, int position) {
    		
    		ImageView iv = new ImageView(mContext);
			iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			iv.setImageResource(hImage[position]);

    		((ViewPager)pager).addView(iv, 0);
			return iv; 
    	}
    	
    	//�� ��ü ����
		@Override public void destroyItem(View pager, int position, Object view) {
			((ViewPager)pager).removeView((View)view);
		}

		// instantiateItem�޼ҵ忡�� ���� ��ü�� �̿��� ������
		@Override public boolean isViewFromObject(View view, Object obj) { return view == obj; }
		
		
		@Override public void finishUpdate(View arg0) {}
		@Override public void restoreState(Parcelable arg0, ClassLoader arg1) {}
		@Override public Parcelable saveState() { return null; }
		@Override public void startUpdate(View arg0) {}
    }

	public void onClick(View v) {
		int view = v.getId();
		if(view == R.id.helpprev){		//���� ��ư
			int cur = hPager.getCurrentItem();	//���� ������ ������
			if(cur > 0)				//ù �������� �ƴϸ�
				hPager.setCurrentItem(cur-1, true);	//���� �������� �̵�
			else						//ù ������ �̸�
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.first_page), Toast.LENGTH_SHORT).show();	//�޽��� ���
		}
		else if(view == R.id.helpnext){	//���� ��ư
			int cur = hPager.getCurrentItem();	//���� ������ ������
			if(cur < COUNT-1)		//������ �������� �ƴϸ�
				hPager.setCurrentItem(cur+1, true);	//���� �������� �̵�
			else{						//������ ������ �̸�
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.last_page), Toast.LENGTH_SHORT).show();	//�޽��� ���
				finish();
			}
		}
	}
}
