package com.globant.julian.cacharriando.core.models.utils;

public class PageBlog {

	private String title;
	private String content;
	private String imageUrl;
	private String url;
	
	private static PageBlog INSTANCE;

	private PageBlog() {
	}

	public PageBlog(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "{" + "'title':'" + title + "'," + "'content':'" + content + '\'' + '}';
	}
	
	
	//singleton
	public static PageBlog getInstance(){
		INSTANCE = new PageBlog();
		return INSTANCE;
	}
	

}
