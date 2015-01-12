/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.harmonyhub.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.whistlingfish.harmony.HarmonyHubListener;
import net.whistlingfish.openhab.binding.AnnotationBasedBinding;

import org.openhab.io.harmonyhub.HarmonyHubGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

/**
 * Implement this class if you are going create an actively polling service like querying a Website/Device.
 *
 * @author Matt Tucker
 * @since 1.5.1
 */
public class HarmonyHubBinding extends
        AnnotationBasedBinding<HarmonyHubBindingConfig, HarmonyHubBinding, HarmonyHubGenericBindingProvider> {
    private static final Logger logger = LoggerFactory.getLogger(HarmonyHubBinding.class);

    private HarmonyHubGateway harmonyHubGateway;
    private Map<HarmonyHubGateway, Map<String, Set<HarmonyHubListener>>> listeners = new HashMap<HarmonyHubGateway, Map<String, Set<HarmonyHubListener>>>();

    public HarmonyHubBinding() {
        super(HarmonyHubBinding.class, HarmonyHubGenericBindingProvider.class);
    }

    @Override
    public void activate() {
        logger.debug("HarmonyHubBinding activated");
    }

    @Override
    public synchronized void deactivate() {
        logger.debug("HarmonyHubBinding deactivated");
    }

    /* default */void registerListener(String qualifier, String itemName, HarmonyHubListener listener) {
        if (harmonyHubGateway == null) {
            logger.debug(format("registerListener(%s, %s); gateway is not set", itemName));
            return;
        }

        logger.debug(format("registerWatch(%s, %s); adding listener", itemName));
        Set<HarmonyHubListener> hostListeners = listeners.get(harmonyHubGateway).get(qualifier);
        if (hostListeners == null) {
            hostListeners = new HashSet<HarmonyHubListener>();
            listeners.get(harmonyHubGateway).put(qualifier, hostListeners);
        }
        hostListeners.add(listener);
        harmonyHubGateway.addListener(qualifier, listener);
    }

    public synchronized void addHarmonyHubGateway(HarmonyHubGateway harmonyHubGateway) {
        this.harmonyHubGateway = harmonyHubGateway;
        properlyConfigured = true;
        listeners.put(harmonyHubGateway, new HashMap<String, Set<HarmonyHubListener>>());
        allBindingsChanged();
    }

    public synchronized void removeHarmonyHubGateway(HarmonyHubGateway harmonyHubGateway) {
        if (this.harmonyHubGateway == harmonyHubGateway) {
            for (Entry<String, Set<HarmonyHubListener>> entry : listeners.remove(harmonyHubGateway).entrySet()) {
                for (HarmonyHubListener listener : entry.getValue()) {
                    harmonyHubGateway.removeListener(entry.getKey(), listener);
                }
            }
            this.harmonyHubGateway = null;
        }
    }

    public void startActivity(String qualifier, String activity) {
        if (harmonyHubGateway == null) {
            logger.warn(format("can't perform startActivity; gateway is not set"));
            return;
        }
        harmonyHubGateway.startActivity(qualifier, activity);
    }

    public void pressButon(String qualifier, String deviceId, String button) {
        if (harmonyHubGateway == null) {
            logger.warn(format("can't perform pressButton; gateway is not set"));
            return;
        }
        harmonyHubGateway.pressButton(qualifier, deviceId, button);
    }
}
