package ossindex.model;

public class PhotoGallery {
	private String key;
	private String category;
	private String title;
	private int commentCount = 0;
	
	public PhotoGallery(String key) {
		String[] strs = key.split("/");
		
		this.key = key;
		this.category = strs[0];
		this.title = strs[strs.length - 1];
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
}
