package com.coldmn3.nga.bean;

public class Entity extends Base {

	protected int entityId;

	protected String cacheKey;

	public int getEntityId() {
		return entityId;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}
}
