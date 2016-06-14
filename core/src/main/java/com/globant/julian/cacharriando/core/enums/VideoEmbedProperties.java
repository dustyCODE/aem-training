package com.globant.julian.cacharriando.core.enums;

public enum VideoEmbedProperties {
	
	TYPE_PROP(0, "type-video"),
	KEY_PROP(0, "key-video"),
	TTILE_PROP(0, "title-video");
	
	VideoEmbedProperties(int id, String name) {
		this.id = id;
		this.name = name;
	}

	private int id;
	private String name;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
