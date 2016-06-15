package com.globant.julian.cacharriando.core.models.utils;

public class PageBlog {

	private String title;
	private String content;
	private String imageUrl;

	public PageBlog() {
	}

	public PageBlog(String title, String content, String imageUrl) {
		this.title = title;
		this.content = content;
		this.imageUrl = imageUrl;
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

	@Override
	public String toString() {
		return "{" + "'title':'" + title + "'," + "'content':'" + content + '\'' + '}';
	}

}
