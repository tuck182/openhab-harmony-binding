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
import java.util.Set;

import net.whistlingfish.harmony.HarmonyHubListener;

import org.openhab.binding.harmonyhub.HarmonyHubBindingProvider;
import org.openhab.core.binding.AbstractBinding;
import org.openhab.core.binding.BindingProvider;
import org.openhab.core.types.Command;
import org.openhab.core.types.State;
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
public class HarmonyHubBinding extends AbstractBinding<HarmonyHubBindingProvider> {
    private static final Logger logger = LoggerFactory.getLogger(HarmonyHubBinding.class);

    private HarmonyHubGateway harmonyHubGateway;
    private Map<HarmonyHubGateway, Set<HarmonyHubListener>> listeners = new HashMap<>();

    public void activate() {
        logger.debug("HarmonyHubBinding activated");
    }

    public synchronized void deactivate() {
        logger.debug("HarmonyHubBinding deactivated");
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void bindingChanged(BindingProvider provider, String itemName) {
        super.bindingChanged(provider, itemName);

        if (provider instanceof HarmonyHubBindingProvider) {
            HarmonyHubBindingProvider bindingProvider = (HarmonyHubBindingProvider) provider;
            if (bindingProvider.isInBound(itemName)) {
                bindingProvider.bind(this, itemName);
            }
        }
    }

    private void allBindingsChanged() {
        for (BindingProvider provider : providers) {
            allBindingsChanged(provider);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void allBindingsChanged(BindingProvider provider) {
        super.allBindingsChanged(provider);

        for (String itemName : provider.getItemNames()) {
            bindingChanged(provider, itemName);
        }
    }

    /* default */void registerListener(HarmonyHubBindingProvider provider, final String itemName, HarmonyHubListener listener) {
        if (harmonyHubGateway == null) {
            logger.debug(format("registerListener(%s, %s); gateway is not set", provider, itemName));
            return;
        }

        if (!provider.isInBound(itemName))
            return;

        logger.debug(format("registerWatch(%s, %s); adding listener", provider, itemName));
        listeners.get(harmonyHubGateway).add(listener);
        harmonyHubGateway.addListener(listener);
    }

    public synchronized void addHarmonyHubGateway(HarmonyHubGateway harmonyHubGateway) {
        this.harmonyHubGateway = harmonyHubGateway;
        listeners.put(harmonyHubGateway, new HashSet<HarmonyHubListener>());
        allBindingsChanged();
    }

    public synchronized void removeHarmonyHubGateway(HarmonyHubGateway harmonyHubGateway) {
        if (this.harmonyHubGateway == harmonyHubGateway) {
            for (HarmonyHubListener listener : listeners.remove(harmonyHubGateway)) {
                harmonyHubGateway.removeListener(listener);
            }
            this.harmonyHubGateway = null;
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    protected void internalReceiveCommand(String itemName, Command command) {
        // the code being executed when a command was sent on the openHAB
        // event bus goes here. This method is only called if one of the
        // BindingProviders provide a binding for the given 'itemName'.
        logger.debug(format("internalReceiveCommand(%s, %s:%s)", itemName, command.getClass().getName(), command));

        for (HarmonyHubBindingProvider provider : providers) {
            if (provider.providesBindingFor(itemName) && !provider.isInBound(itemName)) {
                provider.bind(this, itemName);
            }
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    protected void internalReceiveUpdate(String itemName, State newState) {
        // the code being executed when a state was sent on the openHAB
        // event bus goes here. This method is only called if one of the
        // BindingProviders provide a binding for the given 'itemName'.
        logger.debug(format("internalReceiveUpdate(%s, %s:%s)", itemName, newState.getClass().getName(), newState));
    }

    public void postUpdate(String itemName, State state) {
        eventPublisher.postUpdate(itemName, state);
    }

    public void startActivity(String activity) {
        if (harmonyHubGateway == null) {
            logger.warn(format("can't perform startActivity; gateway is not set"));
            return;
        }
        harmonyHubGateway.startActivity(activity);
    }

    public void pressButon(String deviceId, String button) {
        if (harmonyHubGateway == null) {
            logger.warn(format("can't perform pressButton; gateway is not set"));
            return;
        }
        harmonyHubGateway.pressButton(deviceId, button);
    }
}
