/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.harmonyhub;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.whistlingfish.harmony.HarmonyClient;
import net.whistlingfish.harmony.HarmonyHubListener;

import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static java.util.Collections.list;

/**
 * Handles connection and event handling for all Harmony Hub devices.
 *
 * @author Matt Tucker
 * @since 1.5.1
 */
public class HarmonyHubGateway implements ManagedService {
    private static final Logger logger = LoggerFactory.getLogger(HarmonyHubGateway.class);
    private Map<String, HarmonyClient> clients = new HashMap<String, HarmonyClient>();

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

    private static final Pattern CONFIG_PATTERN = Pattern.compile("((.*):)?(host|username|password)");

    @Override
    public synchronized void updated(Dictionary<String, ?> config) throws ConfigurationException {
        if (config != null) {
            final Map<String, HostConfig> hostConfigs = new HashMap<String, HostConfig>();

            for (String key : list(config.keys())) {
                String value = (String) config.get(key);
                Matcher m = CONFIG_PATTERN.matcher(key);
                if (!m.matches()) {
                    continue;
                }
                String qualifier = m.group(2);
                if (qualifier == null) {
                    qualifier = "";
                }
                String configKey = m.group(3);

                HostConfig hostConfig = hostConfigs.get(qualifier);
                if (hostConfig == null) {
                    hostConfig = new HostConfig();
                    hostConfigs.put(qualifier, hostConfig);
                }
                if (configKey.equals("host")) {
                    hostConfig.setHost(value);
                } else if (configKey.equals("username")) {
                    hostConfig.setUsername(value);
                } else {
                    hostConfig.setPassword(value);
                }
            }

            if (!properlyConfigured) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (Entry<String, HostConfig> entry : hostConfigs.entrySet()) {
                            String qualifier = entry.getKey();
                            HostConfig hostConfig = entry.getValue();
                            if (!hostConfig.isValid()) {
                                continue;
                            }
                            try {
                                HarmonyClient harmonyClient = HarmonyClient.getInstance();
                                harmonyClient.connect(hostConfig.getHost(), hostConfig.getUsername(),
                                        hostConfig.getPassword());
                                clients.put(qualifier, harmonyClient);
                                properlyConfigured = true;
                            } catch (Exception e) {
                                logger.error(format(//
                                        "Failed creating harmony hub connection to %s", hostConfig.getHost()), e);
                            }
                        }
                    }
                }).start();
            }
        }
    }

    protected interface ClientRunnable {
        void run(HarmonyClient client);
    }

    private void withClient(String qualifier, ClientRunnable runnable) {
        HarmonyClient client = clients.get(qualifier);
        if (client == null) {
            throw new IllegalArgumentException(format("No client '%s' defined", qualifier));
        }
        runnable.run(client);
    }

    public void pressButton(final int deviceId, final String button) {
        pressButton("", deviceId, button);
    }

    public void pressButton(String qualifier, final int deviceId, final String button) {
        if (!properlyConfigured) {
            throw new IllegalStateException(
                    "Harmony Hub Gateway is not properly configured, or the connection is not yet started");
        }
        withClient(qualifier, new ClientRunnable() {
            @Override
            public void run(HarmonyClient client) {
                client.pressButton(deviceId, button);
            }
        });
    }

    public void pressButton(final String device, final String button) {
        pressButton("", device, button);
    }

    public void pressButton(String qualifier, final String device, final String button) {
        if (!properlyConfigured) {
            throw new IllegalStateException(
                    "Harmony Hub Gateway is not properly configured, or the connection is not yet started");
        }
        withClient(qualifier, new ClientRunnable() {
            @Override
            public void run(HarmonyClient client) {
                try {
                    client.pressButton(Integer.parseInt(device), button);
                } catch (NumberFormatException e) {
                    client.pressButton(device, button);
                }
            }
        });
    }

    public void startActivity(final int activityId) {
        startActivity("", activityId);
    }

    public void startActivity(String qualifier, final int activityId) {
        if (!properlyConfigured) {
            throw new IllegalStateException(
                    "Harmony Hub Gateway is not properly configured, or the connection is not yet started");
        }
        withClient(qualifier, new ClientRunnable() {
            @Override
            public void run(HarmonyClient client) {
                client.startActivity(activityId);
            }
        });
    }

    public void startActivity(final String activity) {
        startActivity("", activity);
    }

    public void startActivity(String qualifier, final String activity) {
        if (!properlyConfigured) {
            throw new IllegalStateException(
                    "Harmony Hub Gateway is not properly configured, or the connection is not yet started");
        }
        withClient(qualifier, new ClientRunnable() {
            @Override
            public void run(HarmonyClient client) {
                try {
                    client.startActivity(Integer.parseInt(activity));
                } catch (NumberFormatException e) {
                    client.startActivityByName(activity);
                }
            }
        });
    }

    public void addListener(final HarmonyHubListener listener) {
        addListener("", listener);
    }

    public void addListener(String qualifier, final HarmonyHubListener listener) {
        withClient(qualifier, new ClientRunnable() {
            @Override
            public void run(HarmonyClient client) {
                client.addListener(listener);
            }
        });
    }

    public void removeListener(final HarmonyHubListener listener) {
        removeListener("", listener);
    }

    public void removeListener(String qualifier, final HarmonyHubListener listener) {
        withClient(qualifier, new ClientRunnable() {
            @Override
            public void run(HarmonyClient client) {
                client.removeListener(listener);
            }
        });
    }
}
