package com.whitehole.socialmap.network;


public class SPicture {
	public String pictureID;				//사진의 public, private 여부 요청 시 필요한 사진의 ID
	public int isPublic;					// 사진이 퍼블릭인가 여부
											// 0: 디폴트 값. private.
											// 1: public
											// 2: 친구 사진일 경우 해당
	
											//네트워크 함수에서 사용
											// 4: 해당 사진이 페이스북에서 삭제된 경우
											// 5: 해당 사진이 public -> private
											// 6: 해당 사진이 private -> public
	
	public double latitude, longitude;		// 사진의 위치 정보 (위도, 경도)
	public String commentURL;					// 사진의 댓글 링크 주소
	public String imageURL;					// 원본 사진의 URL
	public String smallImageURL;				// 작은 사진의 URL
	public long makeTime;						// 사진의 업로드 시간 (초 단위로 환산) UTC
	public String makeTimeS;					// 사진의 업로드 시간 (페이스북)
	public String record;						// 사진과 함께 게시한 글
	public String ownerName;					// 사진 소유자 이름
	public String ownerID;						// 사진 소유자의 고유 아이디
	public String ownerThumURL;				// 사진 소유자 페북 썸네일 URL
	public String placeName;				// 사진이 속한 페이스북 위치 페이지 이름
	
	public static final String PICTUREID = "pictureID";
	public static final String ISPUBLIC = "isPublic";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String COMMENTURL = "commentURL";
	public static final String IMAGEURL = "iamgeURL";
	public static final String SMALLIMAGEURL = "smallImageURL";
	public static final String RECORD = "record";
	public static final String MAKETIME = "makeTime";
	public static final String MAKETIMES = "makeTimeS";
	public static final String OWNERNAME = "ownerName";
	public static final String OWNERID = "ownerID";
	public static final String OWNERTHUMURL = "ownerThumURL";
	public static final String PLACENAME = "placeName";
	
	
	public SPicture(){
		isPublic = 0;	// 디폴트값 셋팅
	}

	public SPicture(String pictureID, int isPublic, double latitude, double longitude,
			String commentURL, String imageURL, String smallImageURL,
			long makeTime, String makeTimeS, String record, String ownerName, String ownerID, String ownerThumURL,
			String placeName) {
		super();
		this.pictureID = pictureID;
		this.isPublic = isPublic;
		this.latitude = latitude;
		this.longitude = longitude;
		this.commentURL = commentURL;
		this.imageURL = imageURL;
		this.smallImageURL = smallImageURL;
		this.makeTime = makeTime;
		this.makeTimeS = makeTimeS;
		this.record = record;
		this.ownerName = ownerName;
		this.ownerID = ownerID;
		this.ownerThumURL = ownerThumURL;
		this.placeName = placeName;
	}
	
	
}
