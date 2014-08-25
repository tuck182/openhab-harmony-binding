/**
 * Copyright (c) 2010-2013, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.action.harmonyhub.internal;

import org.openhab.core.scriptengine.action.ActionService;
import org.openhab.io.harmonyhub.HarmonyHubGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class registers an OSGi service for the HarmonyHub action.
 * 
 * @author Matt Tucker
 * @since 1.5.1
 */
public class HarmonyHubActionService implements ActionService {
	private static final Logger logger = LoggerFactory
			.getLogger(HarmonyHubActionService.class);

	private static HarmonyHubGateway harmonyHubGateway;

	public HarmonyHubActionService() {
	}

	public void activate() {
	}

	public void deactivate() {
		// deallocate Resources here that are no longer needed and
		// should be reset when activating this binding again
	}

	public String getActionClassName() {
		return HarmonyHub.class.getCanonicalName();
	}

	public Class<?> getActionClass() {
		return HarmonyHub.class;
	}

	public static boolean isProperlyConfigured() {
		return harmonyHubGateway.isProperlyConfigured();
	}

	public synchronized void setHarmonyHubGateway(
			HarmonyHubGateway harmonyHubGateway) {
		logger.debug("harmony client set");
		HarmonyHubActionService.harmonyHubGateway = harmonyHubGateway;
	}

	public synchronized void removeHarmonyHubGateway(
			HarmonyHubGateway harmonyHubGateway) {
		logger.debug("harmony client removed");
		if (HarmonyHubActionService.harmonyHubGateway == harmonyHubGateway) {
			harmonyHubGateway = null;
		}
	}

	public static HarmonyHubGateway gateway() {
		return harmonyHubGateway;
	}
}
