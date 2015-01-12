/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.harmonyhub.internal;

import net.whistlingfish.openhab.binding.AnnotationBasedBindingProvider;

import org.openhab.binding.harmonyhub.HarmonyHubBindingProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for parsing the binding configuration.
 *
 * @author Matt Tucker
 * @since 1.5.1
 */
public class HarmonyHubGenericBindingProvider extends
        AnnotationBasedBindingProvider<HarmonyHubBindingConfig, HarmonyHubBinding, HarmonyHubGenericBindingProvider>
        implements HarmonyHubBindingProvider {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(HarmonyHubBinding.class);

    @SuppressWarnings("unchecked")
    public HarmonyHubGenericBindingProvider() {
        super(HarmonyHubBindingConfig.class, //
                BindingConfigPressButton.class, //
                BindingConfigStartActivity.class, //
                BindingConfigCurrentActivity.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBindingType() {
        return "harmonyhub";
    }
}
