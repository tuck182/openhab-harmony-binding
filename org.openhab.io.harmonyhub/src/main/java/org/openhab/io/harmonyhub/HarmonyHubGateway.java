/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.harmonyhub;

import java.util.Dictionary;

import net.whistlingfish.harmony.ActivityChangeListener;
import net.whistlingfish.harmony.HarmonyClient;
import net.whistlingfish.harmony.HarmonyHubListener;

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
    private static final Logger logger = LoggerFactory.getLogger(HarmonyHubGateway.class);
    private HarmonyClient harmonyClient = HarmonyClient.getInstance();

    @SuppressWarnings("unused")
    private String host;
    @SuppressWarnings("unused")
    private String username;
    @SuppressWarnings("unused")
    private String password;

    private volatile boolean properlyConfigured = false;

    public void activate() {
        logger.info("HarmonyHub gateway activated");
    }

    public void deactivate() {
        logger.info("HarmonyHub gateway deactivated");
    }

    public boolean isProperlyConfigured() {
        return properlyConfigured;
    }

    @Override
    public synchronized void updated(Dictionary<String, ?> config) throws ConfigurationException {
        if (config != null) {
            final String host = (String) config.get("host");
            if (host != null) {
                this.host = host;
            }
            final String username = (String) config.get("username");
            if (username != null) {
                this.username = username;
            }
            final String password = (String) config.get("password");
            if (password != null) {
                this.password = password;
            }
            if (!properlyConfigured && host != null && username != null && password != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        harmonyClient.connect(host, username, password);
                        properlyConfigured = true;
                    }
                }).start();
            }
        }
    }

    public void pressButton(int deviceId, String button) {
        if (!properlyConfigured) {
            throw new IllegalStateException(
                    "Harmony Hub Gateway is not properly configured, or the connection is not yet started");
        }
        harmonyClient.pressButton(deviceId, button);
    }

    public void pressButton(String device, String button) {
        if (!properlyConfigured) {
            throw new IllegalStateException(
                    "Harmony Hub Gateway is not properly configured, or the connection is not yet started");
        }
        try {
            harmonyClient.pressButton(Integer.parseInt(device), button);
        } catch (NumberFormatException e) {
            harmonyClient.pressButton(device, button);
        }
    }

    public void startActivity(int activityId) {
        if (!properlyConfigured) {
            throw new IllegalStateException(
                    "Harmony Hub Gateway is not properly configured, or the connection is not yet started");
        }
        harmonyClient.startActivity(activityId);
    }

    public void startActivity(String activity) {
        if (!properlyConfigured) {
            throw new IllegalStateException(
                    "Harmony Hub Gateway is not properly configured, or the connection is not yet started");
        }
        try {
            harmonyClient.startActivity(Integer.parseInt(activity));
        } catch (NumberFormatException e) {
            harmonyClient.startActivityByName(activity);
        }
    }

    public void addListener(HarmonyHubListener listener) {
        harmonyClient.addListener(listener);
    }

    public void removeListener(HarmonyHubListener listener) {
        harmonyClient.removeListener(listener);
    }
}
