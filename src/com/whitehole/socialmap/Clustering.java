package com.whitehole.socialmap;

import java.util.*;

import android.annotation.*;
import android.database.*;
import android.provider.MediaStore.Images.*;
import android.util.*;

import com.google.android.gms.maps.model.*;
import com.whitehole.socialmap.R;
import com.whitehole.socialmap.network.*;

public class Clustering
{
	Cursor cursor;
	ArrayList<Cluster> clusters;		//현재 존재하는 군집들
	//double lat, lng;
	double threshold;			//임계값
	int cluNum;					//초기 군집 개수
	//private final Object syncObj1 = new Object();
	int from;					//해당 군집화가 어디에서 일어나는지 표시
								//0: 로컬 갤러리 사진
								//1: 페이스북 사진 ...
								//4: 라벨 사진
	
	public Clustering(Cursor cursor, double threshold, int from) {
		this.cursor = cursor;
		this.threshold = threshold;
		clusters = new ArrayList<Cluster>();
		clusters.add(new Cluster());
		this.from = from;
		
		initSequential();
	}
	
	public Clustering(ArrayList<Cluster> clusters){
		this.clusters = clusters;
	}
	
	// 순차 알고리즘을 사용하여 초기 군집들과 군집 수를 결정
	public void initSequential(){
		CluPoint tmpP1, tmpP2;		//P1은 커서를 탐색하는 점
									//P2는 클러스터를 탐색할때 그 군집 중심점
		double minDis, lat, lng, dis;
		int minCluIdx, idx;
		long dtt;
		
		String curLat = "", curLng = "";
		String dateTakenTime = "";
		
		if(from == 0){
			curLat = ImageColumns.LATITUDE;
			curLng = ImageColumns.LONGITUDE;
			dateTakenTime = ImageColumns.DATE_TAKEN;
		}
		else if(from == 1 || from == 4){
			curLat = SPicture.LATITUDE;
			curLng = SPicture.LONGITUDE;
			dateTakenTime = SPicture.MAKETIME;
		}
		
		if(cursor.moveToFirst()){
			
			//초기의 군집은 하나이고, 하나의 점만 존재
			lat = cursor.getDouble(cursor.getColumnIndex(curLat));
			lng = cursor.getDouble(cursor.getColumnIndex(curLng));
			dtt = cursor.getLong(cursor.getColumnIndex(dateTakenTime));
			
			//Log.w("dateTaken", dtt+"");
			
			clusters.get(0).addPoint(new CluPoint(cursor.getPosition(), new LatLng(lat, lng), dtt));
				
			while(cursor.moveToNext()){
				
				lat = cursor.getDouble(cursor.getColumnIndex(curLat));
				lng = cursor.getDouble(cursor.getColumnIndex(curLng));
				dtt = cursor.getLong(cursor.getColumnIndex(dateTakenTime));
				
				tmpP1 = new CluPoint(cursor.getPosition(), new LatLng(lat, lng), dtt);
				
				
				minDis = Math.abs(clusters.get(0).points.get(clusters.get(0).cluCenterIdx).latlng.latitude-tmpP1.latlng.latitude) + Math.abs(clusters.get(0).points.get(clusters.get(0).cluCenterIdx).latlng.longitude-tmpP1.latlng.longitude);
				minCluIdx = 0;
				
				idx=-1;
				//커서의 특정 점과 가장 가까운 군집을 찾는다
				for(Cluster c: clusters){
					
					idx++;
					tmpP2 = c.points.get(c.cluCenterIdx);
					
					dis = Math.abs(tmpP1.latlng.latitude-tmpP2.latlng.latitude) + Math.abs(tmpP1.latlng.longitude-tmpP2.latlng.longitude);
					
					if(dis < minDis){
						minDis = dis;
						minCluIdx = idx;
					}
				}
				
				//만일 최소거리가 임계값 보다 작거나 같으면
				//최소거리인 군집에 해당 점을 넣는다.
				if(minDis <= threshold){
					this.clusters.get(minCluIdx).addPoint(tmpP1);
				}
				//그렇지 않으면 새로운 군집을 만든다.
				else{
					this.clusters.add(new Cluster(tmpP1));
				}
				
			}
		}
		cluNum = clusters.size();
	}
	
	//k-medoids 알고리즘을 이용하여 현재 커서의 사진들을 군집화 함
	public void kMedoids(){
		
		boolean modifyCluster = false;		//군집 배정이 반복문 이전의 배정과 같다면 true
		int minCluIdx, idx, idxx, idxxx;
		double minDis, dis;

		//synchronized (syncObj1) {
			while(!modifyCluster){
				
				modifyCluster = true;
				
				idx=-1;
				//모든 점을 가장 가까운 군집중심에 배치
				//TODO for each 문의 동기화 문제 처리
	
				
				for(Cluster c: (ArrayList<Cluster>)clusters.clone()){
					
					idx++;
					
					for(CluPoint p: (ArrayList<CluPoint>)c.points.clone()){
						
						minCluIdx = idx;
						minDis = Math.abs(c.points.get(c.cluCenterIdx).latlng.latitude - p.latlng.latitude) + Math.abs(c.points.get(c.cluCenterIdx).latlng.longitude - p.latlng.longitude);
						
						idxx=-1;
						for(Cluster cc: (ArrayList<Cluster>)clusters.clone()){
							idxx++;
							dis = Math.abs(cc.points.get(cc.cluCenterIdx).latlng.latitude - p.latlng.latitude) + Math.abs(cc.points.get(cc.cluCenterIdx).latlng.longitude - p.latlng.longitude);
							
							if(dis < minDis){
								minCluIdx = idxx;
								minDis = dis;
							}
								
						}
						
						if(minCluIdx != idx){
							clusters.get(idx).deletePoint(p);
							clusters.get(minCluIdx).addPoint(p);
							modifyCluster = false;
						}
					}
				}
			}
		//}
			
			//찍은 시간순으로 클러스터 안의 각 점들을 소팅하기
			//수정 -> 찍은 시간순으로 하되 최근사진을 앞쪽에 배치하고
			//대표사진을 군집의 가장 최근 사진으로 변경
			for(Cluster ccc : clusters){
				//군집의 중심의 포인트 인덱스를 저장
				//CluPoint tmpc = ccc.points.get(ccc.cluCenterIdx);
				
				//Log.w("points", ccc.points.size()+"");
				Collections.sort(ccc.points, pCompare);
				
				ccc.cluCenterIdx = 0;
			}
	}
	
	Comparator<CluPoint> pCompare = new Comparator<CluPoint>(){

		@Override
		public int compare(CluPoint lhs, CluPoint rhs) {
			if(lhs.dateTaken - rhs.dateTaken > 0)
				return -1;
			else if(lhs.dateTaken - rhs.dateTaken == 0)
				return 0;
			else
				return 1;
			
			//return (int) (lhs.dateTaken - rhs.dateTaken);
		}
	};

	
}

//하나의 군집을 정의하는 클래스
class Cluster{
	ArrayList<CluPoint> points = null;	//해당 군집에 속하는 점 리스트
	int cluCenterIdx;				//해당 군집의 군집 중심점의 점 리스트의 인덱스
	

	public Cluster() {
		points = new ArrayList<CluPoint>();
	}
	
	public Cluster(CluPoint point){
		points = new ArrayList<CluPoint>();
		this.addPoint(point);
	}

	public void addPoint(CluPoint point){
		//synchronized (syncObj1) {
			this.points.add(point);
			calculateClusterCenterP();
		//}
	}
	
	@SuppressLint("UseValueOf")
	public void deletePoint(CluPoint point){
		//synchronized (syncObj1) {
			this.points.remove(point);
			calculateClusterCenterP();
		//}
	}
	
	//군집 중심을 결정하는 함수
	//해당 군집의 군집 중심점을 계산하여 그 점의 점 리스트 인덱스를 저장하는 함수
	public void calculateClusterCenterP(){
		double averLat=0.0, averLng=0.0;	//군집에 속하는 모든점들의 평균 좌표
		int minDisPos, idx;					
		double dis, minDis;
		
		this.cluCenterIdx = 0;
		
		if(points.size() > 1){
			
			for(CluPoint p: points){
				averLat += p.latlng.latitude;
				averLng += p.latlng.longitude;
			}
			
			averLat /= points.size();
			averLng /= points.size();
			
			minDis = Math.abs(points.get(0).latlng.latitude-averLat) + Math.abs(points.get(0).latlng.longitude-averLng);
			
			idx=-1;
			//평균 좌표와의 거리가 가장 작은 점을 찾아 해당 군집의 중심으로 선정
			for(CluPoint p: points){
				idx++;
				dis = Math.abs(p.latlng.latitude-averLat) + Math.abs(p.latlng.longitude-averLng);
				
				if(dis < minDis){
					minDis = dis;
					this.cluCenterIdx = idx;
				}
			}
		}
	}
}

class CluPoint {
	int cursorPosition;		//해당 점의 위치에 있는 사진의 커서 포지션을 저장
	LatLng latlng;			//해당 점의 좌표
	long dateTaken;			//해당 사진 찍은 시간
	
	public CluPoint(int cursorPosition, LatLng latlng, long dtt) {
		
		this.cursorPosition = cursorPosition;
		this.latlng = latlng;
		this.dateTaken = dtt;
	}

//	@Override
//	public int compareTo(CluPoint arg0) {
//		
//		if(dateTaken > arg0.dateTaken)
//			return 1;
//		else if(dateTaken == arg0.dateTaken)
//			return 0;
//		else
//			return -1;
//	}
}