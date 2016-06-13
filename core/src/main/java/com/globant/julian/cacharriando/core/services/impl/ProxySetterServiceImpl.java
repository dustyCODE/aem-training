package com.globant.julian.cacharriando.core.services.impl;

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.julian.cacharriando.core.services.IProxySetterService;

@Component(immediate = true, metatype = true, label = "Proxy Setter Service")
@Service(value = IProxySetterService.class)
public class ProxySetterServiceImpl implements IProxySetterService {

	private final Logger LOGGER = LoggerFactory.getLogger(ProxySetterServiceImpl.class);

	private final String DEFAULT_PROXY_HOST = "0.0.0.0";
	private final String DEFAULT_PROXY_PORT = "0000";
	private final boolean DEFAULT_PROXY_ENABLED= false;
	
	@Property(label = "Proxy enabled", description = "Choose if you want to set the Http Proxy", boolValue = false)
	public static final String PROXY_ENABLED = "proxyIsEnabled";
	private boolean proxyIsEnabled;
	
	@Property(label = "Proxy host", description = "Set the host of your proxy server")
	public static final String HOSTNAME = "host";
	private String host;

	@Property(label = "Proxy port", description = "Set the port of your proxy server")
	public static final String PORT = "port";
	private String port;
	
	@Activate
	protected void activate(final Map<String, Object> config) {
		configure(config);
	}
	
	@Deactivate
	protected void deactivate(){
		LOGGER.info("************* Proxy Component has been deactivated! *************");
		this.removeConfig();
	}

	public void removeConfig() {
		this.proxyIsEnabled = false;
		this.host = "";
		this.port = "";
	}

	private void configure(final Map<String, Object> config) {
		this.proxyIsEnabled = PropertiesUtil.toBoolean(config.get(ProxySetterServiceImpl.PROXY_ENABLED), this.DEFAULT_PROXY_ENABLED);
		this.host = PropertiesUtil.toString(config.get(ProxySetterServiceImpl.HOSTNAME), this.DEFAULT_PROXY_HOST);
		this.port = PropertiesUtil.toString(config.get(ProxySetterServiceImpl.PORT), this.DEFAULT_PROXY_PORT);
		LOGGER.info("Proxy configuration changed to: host='{}'' and port='{}'", this.host, this.port);
		this.setConfig(this.proxyIsEnabled, this.host, this.port);
	}

	@Override
	public void setConfig(boolean isEnabled, String host, String port) {
		System.setProperty("http.proxySet", isEnabled ? "true" : "false");
		if (isEnabled){
			LOGGER.info("********** EJECUTANDO PROXY SETTER *****************");
			System.setProperty("http.proxyHost", host);
			System.setProperty("http.proxyPort", port);
		}else{
			System.setProperty("http.proxyHost", this.DEFAULT_PROXY_HOST);
			System.setProperty("http.proxyPort", this.DEFAULT_PROXY_PORT);
		}
	}

	@Override
	public void setConfig(boolean isEnabled, String host, String port, String user, String password) {
		//
	}

}
