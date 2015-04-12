package ossindex.model;

import java.util.Random;

public class PhotoGallery {
	private String objectId;
	private String key;
	private String category;
	private String title;
	private int commentCount = 0;
	private int hit;
	private String uploader;
	
	public PhotoGallery(String key) {
		String[] strs = key.split("/");
		
		this.key = key;
		this.category = strs[0];
		this.title = strs[strs.length - 1];

		Random rd = new Random();

		hit = rd.nextInt(10000);
	}
	public PhotoGallery() {
		// TODO Auto-generated constructor stub
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	public String getUploader() {
		return uploader;
	}

	public void setUploader(String uploader) {
		this.uploader = uploader;
	}
}
