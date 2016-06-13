package com.globant.julian.cacharriando.core.services.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globant.julian.cacharriando.core.services.HandleForm;

/**
 * This is a component cause exposes and/or consume a service(s)
 * @author julian.herrera
 *
 */
@Component
@Service
public class HandleFormImpl implements HandleForm {

	private static final Logger LOGGER = LoggerFactory.getLogger(HandleFormImpl.class);
	
	@Override
	public void processData(String contactName, String description) {
		
		LOGGER.info("Info processed.  Contact name: {} and description {} ", contactName, description);

	}

}
