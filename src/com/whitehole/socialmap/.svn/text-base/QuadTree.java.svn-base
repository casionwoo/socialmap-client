package com.whitehole.socialmap;

import java.net.*;
import java.util.*;

import com.google.android.gms.maps.model.*;
import com.whitehole.socialmap.R;

public class QuadTree {
	
	Node root;
	final double limitHorizenal = 0.000016;
	
//	IMG076: lat:37.6099355 lng: 126.9974889
//	IMG065: lat:37.610015869140625 lng: 126.9977035522461
//	IMG072: lat:37.60982131958008 lng: 126.99771881103516
//
//
//	세로 vertival (IMG065 ~ IMG072):  0.00001525878906
//	가로 horizenal (IMG076 ~ IMG065): 0.000080369140625
	
	public QuadTree() {

		// root definition
		root = new Node(new LatLngBounds(new LatLng(-90, -180), new LatLng(90, 180)), new LatLng(0, 0), 1);
	}
	
	//quad tree build
	public void build(){
		
		subQuadTree(root);
		
	}
	
	//해당 노드를 4개의 자식 노드로 분할
	public void subQuadTree(Node n){
		
		if(n.depth!=1 && n.bounds.northeast.longitude - n.bounds.southwest.longitude < limitHorizenal){
			//Log.i("Activity", "leaf node depth: " + n.depth);
			return;
		}
		
		addChild(n);
		subQuadTree(n.NE);
		subQuadTree(n.NW);
		subQuadTree(n.SE);
		subQuadTree(n.SW);
		
	}
	
	// 자식 노드 추가
	public Node addChild(Node parent){
		
		LatLng topCenter = new LatLng(parent.bounds.northeast.latitude, parent.center.longitude);
		LatLng leftCenter = new LatLng(parent.center.latitude, parent.bounds.southwest.longitude);
		LatLng rightCenter = new LatLng(parent.center.latitude, parent.bounds.northeast.longitude);
		LatLng bottomCenter = new LatLng(parent.bounds.southwest.latitude, parent.center.longitude);
		
		LatLng NECenter = new LatLng(parent.center.latitude + (parent.bounds.northeast.latitude - parent.center.latitude)/2, parent.center.longitude + (parent.bounds.northeast.longitude - parent.center.longitude)/2);
		LatLng NWCenter = new LatLng(leftCenter.latitude + (topCenter.latitude - leftCenter.latitude)/2, leftCenter.longitude + (topCenter.longitude - leftCenter.longitude)/2);
		LatLng SECenter = new LatLng(bottomCenter.latitude + (rightCenter.latitude - bottomCenter.latitude)/2, bottomCenter.longitude + (rightCenter.longitude -  bottomCenter.longitude)/2);
		LatLng SWCenter = new LatLng(parent.bounds.southwest.latitude + (parent.center.latitude - parent.bounds.southwest.latitude)/2, parent.bounds.southwest.longitude + (parent.center.longitude - parent.bounds.southwest.longitude)/2);
		
		
		parent.setNE(new Node(new LatLngBounds(parent.center, parent.bounds.northeast), NECenter, parent.depth+1));
		parent.setNW(new Node(new LatLngBounds(leftCenter, topCenter), NWCenter, parent.depth+1));
		parent.setSE(new Node(new LatLngBounds(bottomCenter, rightCenter), SECenter, parent.depth+1));
		parent.setSW(new Node(new LatLngBounds(parent.bounds.southwest, parent.center), SWCenter, parent.depth+1));
		
		return parent;
	}
	
	//해당 좌표가 어떤 노드에 속하는지 탐색하여 해당 노드를 반환하는 함수
	public Node searchNode(Node node, LatLng latLng){
		//root.NE.bounds.contains(point)
		
		Node returnNode = node;
		
//		if(node.isLeaf())
//			returnNode = node;
//		else if(node.NE.bounds.contains(latLng)){
//			returnNode = searchNode(node.NE, latLng);
//		}
//		else if(node.NW.bounds.contains(latLng)){
//			returnNode = searchNode(node.NW, latLng);
//		}
//		else if(node.SE.bounds.contains(latLng)){
//			returnNode = searchNode(node.SE, latLng);
//		}
//		else{
//			returnNode = searchNode(node.SW, latLng);
//		}
		
		while(!returnNode.isLeaf()){
		
			if(returnNode.NE.bounds.contains(latLng)){
				returnNode = returnNode.NE;
			}
			else if(returnNode.NW.bounds.contains(latLng)){
				returnNode = returnNode.NW;
			}
			else if(returnNode.SE.bounds.contains(latLng)){
				returnNode = returnNode.SE;
			}
			else{
				returnNode = returnNode.SW;
			}
			
		}
		
		
		return returnNode;
	}
	
	public void insertImage(Image image){
		Node node = searchNode(root, image.imageLatLng);
		node.addImage(image);
	}
	
}

class Node{
	
	Node NW=null, NE=null, SE=null, SW=null;   	// four subtrees 

	
	LatLngBounds bounds=null;		// 노드의 왼아래, 오른위 범위
	LatLng center=null;				// 중점의 좌표 (편의상 )
	
	List<Image> images=null;			// 리프 노드라면 해당 노드의 바운드에 속해 있는 이미지들을 포함할 것임
	int depth;
	
	
	public Node(LatLngBounds bounds, LatLng center, int depth) {
		images = new ArrayList<Image>();
		
		this.bounds = bounds;
		this.center = center;
		this.depth = depth;
	}

	public void addImage(Image image){
		images.add(image);
	}
	
	public boolean isLeaf(){
		return (NW==null && NW==null && SE==null && SW==null);
	}

	public LatLng getCenter() {
		return center;
	}

	public void setCenter(LatLng center) {
		this.center = center;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public Node getNW() {
		return NW;
	}

	public void setNW(Node nW) {
		NW = nW;
	}

	public Node getNE() {
		return NE;
	}

	public void setNE(Node nE) {
		NE = nE;
	}

	public Node getSE() {
		return SE;
	}

	public void setSE(Node sE) {
		SE = sE;
	}

	public Node getSW() {
		return SW;
	}

	public void setSW(Node sW) {
		SW = sW;
	}
	
	
}

class Image{
	
	URL imageUrl;
	LatLng imageLatLng;
	long dateTaken;
	int from;							//0: 로컬갤러리, 1: 페이스북
	int thumnailID;						//로컬 갤러리 사진일 경우 섬네일을 가져오기 위한 id
	Marker marker;						//해당 이미지를 표현할 마커
	List<Integer> label;				//라벨 기능
	
	public Image(URL imageUrl, LatLng imageLatLng, long dateTaken,
			int from, int thumnailID) {
		
		this.imageUrl = imageUrl;
		this.imageLatLng = imageLatLng;
		this.dateTaken = dateTaken;
		this.from = from;
		this.thumnailID = thumnailID;
	}
	
}
