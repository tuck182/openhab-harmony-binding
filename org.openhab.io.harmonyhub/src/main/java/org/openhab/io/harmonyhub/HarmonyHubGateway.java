/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.harmonyhub;

import java.util.Dictionary;

import net.whistlingfish.harmony.HarmonyClient;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles connection and event handling for all Harmony Hub devices.
 * 
 * @author Matt Tucker
 * @since 1.5.1
 */
public class HarmonyHubGateway implements ManagedService {
	private static final Logger logger = LoggerFactory
			.getLogger(HarmonyHubGateway.class);
	private HarmonyClient harmonyClient = HarmonyClient.getInstance();
	
	@SuppressWarnings("unused")
	private String host;
	@SuppressWarnings("unused")
	private String username;
	@SuppressWarnings("unused")
	private String password;
	
	private boolean properlyConfigured = false;

	public void activate() {
		logger.info("Harmony hub service activated");
	}

	public void deactivate() {
		logger.info("Harmony hub service deactivated");
	}

	public boolean isProperlyConfigured() {
		return properlyConfigured;
	}

	@Override
	public synchronized void updated(Dictionary<String, ?> config)
			throws ConfigurationException {
		if (config != null) {
			String host = (String) config.get("host");
			if (host != null) {
				this.host = host;
			}
			String username = (String) config.get("username");
			if (username != null) {
				this.username = username;
			}
			String password = (String) config.get("password");
			if (password != null) {
				this.password = password;
			}
			if (!properlyConfigured && host != null && username != null && password != null) {
				harmonyClient.connect(host, username, password);
				properlyConfigured = true;
			}
		}
	}

	public void pressButton(String deviceId, String button) {
		harmonyClient.pressButton(deviceId, button);
	}

	public void startActivityByName(String label) {
		harmonyClient.startActivityByName(label);
	}

	public void startActivity(int activityId) {
		harmonyClient.startActivity(activityId);
	}
}
