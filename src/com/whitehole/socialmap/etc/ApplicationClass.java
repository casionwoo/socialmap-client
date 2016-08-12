package com.whitehole.socialmap.etc;

import java.util.*;

import com.facebook.Session;
import com.google.android.gms.maps.model.*;
import com.whitehole.socialmap.*;
import com.whitehole.socialmap.database.*;
import com.whitehole.socialmap.network.*;

import android.app.*;
import android.content.res.*;
import android.database.*;

public class ApplicationClass extends Application {
	
	public String appID = "";
	public String userID = "";
	public boolean isSelectOptionView = false;		//optionView Activity에서 보기 선택한 후 확인 버튼을 눌렀을 때를 캐치하는 변수
	public int[] selectedGroup = null;		// optionView Activity에서 선택된 그룹 리스트
											// 0: 내 핸드폰 사진, 1: 내페이스북 사진, 2: 친구 페이스북 사진, 3: 라벨 사진, 4: 전체 사용자 사진
	public ArrayList<FriendsList> friendsData = null;			//선택된 그룹이 친구일 경우 선택 친구 목록 리스트를 저장
	public ArrayList<String> labelsData = null;
	public Session session = null;
	public int thumWidth = 0, thumHeight = 0;
	public int backKey = 1;
	public long backTime = 0;
	
	//리스트로 보기 시 사용
	public Clustering localClu = null, facebookClu = null, favoriteClu = null;
	public ArrayList<SPicture> allviewPhotos = null;
	public LatLngBounds curLoc = null;
	public Cursor locCur = null, FacCur = null, favCur = null;
	
	public boolean uploadPicture = false;
	
	public boolean reDrawGall = false;
	
	public boolean modifyPicture = false;
	public ArrayList<String> deletePicPaths = new ArrayList<String>();
	public boolean modPictureGrid = false;
	public int deletePictureGridIdx = -1;
	
	public boolean addPicture = false;
	
	//public boolean addPictureGrid = false;
	public ArrayList<String> addPicIds = new ArrayList<String>();
	public ArrayList<String> deletePicIds = new ArrayList<String>();
	
	public ArrayList<SPicture> alluserfavList = new ArrayList<SPicture>();
	public SPicture tmpSPicture = null;
	public SPicture tmpHotSPicture = null;
	
	/** onCreate()     * 액티비티, 리시버, 서비스가 생성되기전 어플리케이션이 시작 중일때     * Application onCreate() 메서드가 만들어 진다고 나와 있습니다.      * by. Developer 사이트      */    
	@Override    
	public void onCreate() {
		super.onCreate();    
		}     
	
	/**     * onConfigurationChanged()     * 컴포넌트가 실행되는 동안 단말의 화면이 바뀌면 시스템이 실행 한다.     */    
	@Override    
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);    
		}
}
