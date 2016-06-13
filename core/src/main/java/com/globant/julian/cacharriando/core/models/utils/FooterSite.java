package com.globant.julian.cacharriando.core.models.utils;

public class FooterSite {

	private String label;
	private String url;
	private boolean noFollow;
	

	public FooterSite() {
	
	}

	public FooterSite(String label, String url, boolean noFollow) {
		this.label = label;
		this.url = url;
		this.noFollow = noFollow;
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isNoFollow() {
		return noFollow;
	}
	public void setNoFollow(boolean noFollow) {
		this.noFollow = noFollow;
	}

	
}
