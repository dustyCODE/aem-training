package com.globant.julian.cacharriando.core.services;

public interface IProxySetterService {

	public void setConfig(boolean isEnabled, String host, String port);
	public void setConfig(boolean isEnabled, String host, String port, String user, String password);
	public void removeConfig();
	
}
