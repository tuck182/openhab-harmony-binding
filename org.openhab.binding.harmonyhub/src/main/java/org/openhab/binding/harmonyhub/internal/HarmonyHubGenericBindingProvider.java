/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.harmonyhub.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.whistlingfish.harmony.ActivityChangeListener;
import net.whistlingfish.harmony.config.Activity;

import org.openhab.binding.harmonyhub.HarmonyHubBindingProvider;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.core.library.types.StringType;
import org.openhab.model.item.binding.AbstractGenericBindingProvider;
import org.openhab.model.item.binding.BindingConfigParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

/**
 * This class is responsible for parsing the binding configuration.
 * 
 * @author Matt Tucker
 * @since 1.5.1
 */
public class HarmonyHubGenericBindingProvider extends AbstractGenericBindingProvider implements
HarmonyHubBindingProvider {
    private static final Logger logger = LoggerFactory.getLogger(HarmonyHubBinding.class);

    private static final Pattern INCOMING_CONFIG_PATTERN = Pattern.compile("<\\[(currentActivity)\\]");
    private static final Pattern OUTGOING_CONFIG_PATTERN = Pattern.compile(">\\[(press|start):(.*?)(?::(.*?))?\\]");

    private Map<String, ConfigCreator> configCreators = new HashMap<>();
    {
        configCreators.put("currentActivity", new ConfigCreator() {
            public BindingConfig create(Matcher matcher) {
                return new HarmonyHubCurrentActivityBindingConfig();
            }
        });
        configCreators.put("start", new ConfigCreator() {
            public BindingConfig create(Matcher matcher) {
                return new HarmonyHubStartActivityBindingConfig(matcher.group(2));
            }
        });
        configCreators.put("press", new ConfigCreator() {
            public BindingConfig create(Matcher matcher) {
                return new HarmonyHubPressButtonBindingConfig(matcher.group(2), matcher.group(3));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public String getBindingType() {
        return "harmonyhub";
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void validateItemType(Item item, String bindingConfig) throws BindingConfigParseException {
        // if (!(item instanceof SwitchItem || item instanceof DimmerItem)) {
        // throw new BindingConfigParseException("item '" + item.getName()
        // + "' is of type '" + item.getClass().getSimpleName()
        // +
        // "', only Switch- and DimmerItems are allowed - please check your *.items configuration");
        // }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processBindingConfiguration(String context, Item item, String bindingConfig)
            throws BindingConfigParseException {
        super.processBindingConfiguration(context, item, bindingConfig);

        if (bindingConfig.startsWith("<")) {
            addBindingConfig(item, parseBindingConfig(INCOMING_CONFIG_PATTERN, item, bindingConfig));
        } else if (bindingConfig.startsWith(">")) {
            addBindingConfig(item, parseBindingConfig(OUTGOING_CONFIG_PATTERN, item, bindingConfig));
        } else {
            throw new BindingConfigParseException("Item '" + item.getName() + "' does not start with < or >.");
        }
    }

    protected BindingConfig parseBindingConfig(Pattern pattern, Item item, String bindingConfig)
            throws BindingConfigParseException {
        Matcher matcher = pattern.matcher(bindingConfig);
        if (!matcher.matches())
            throw new BindingConfigParseException(format("Config '%s' for item '%s' could not be parsed.",
                    bindingConfig, item.getName()));
        return configCreators.get(matcher.group(1)).create(matcher);
    }

    @Override
    public void bind(HarmonyHubBinding binding, String itemName) {
        BindingConfig bindingConfig = bindingConfigs.get(itemName);
        if (!(bindingConfig instanceof HarmonyHubBindingConfig))
            return;
        ((HarmonyHubBindingConfig) bindingConfig).bind(binding, itemName);
    }

    public interface ConfigCreator {
        BindingConfig create(Matcher matcher);
    }

    public interface HarmonyHubBindingConfig extends BindingConfig {
        void bind(HarmonyHubBinding binding, String itemName);

        boolean isIncoming();
    }

    public static abstract class HarmonyHubIncomingBindingConfig implements HarmonyHubBindingConfig {
        public boolean isIncoming() {
            return true;
        }
    }

    public static abstract class HarmonyHubOutgoingBindingConfig implements HarmonyHubBindingConfig {
        public boolean isIncoming() {
            return false;
        }
    }

    public class HarmonyHubCurrentActivityBindingConfig extends HarmonyHubIncomingBindingConfig {
        @Override
        public void bind(final HarmonyHubBinding binding, final String itemName) {
            binding.registerListener(HarmonyHubGenericBindingProvider.this, itemName, new ActivityChangeListener() {
                @Override
                public void activityStarted(Activity activity) {
                    binding.postUpdate(itemName, new StringType(activity.getLabel()));
                }
            });
        }
    }

    public class HarmonyHubStartActivityBindingConfig extends HarmonyHubOutgoingBindingConfig {
        private final String activity;

        public HarmonyHubStartActivityBindingConfig(String activity) {
            this.activity = activity;
        }

        @Override
        public void bind(HarmonyHubBinding binding, String itemName) {
            binding.startActivity(activity);
        }

        protected String getActivity() {
            return activity;
        }
    }

    public class HarmonyHubPressButtonBindingConfig extends HarmonyHubOutgoingBindingConfig {
        private final String device;
        private final String button;

        public HarmonyHubPressButtonBindingConfig(String device, String button) {
            this.device = device;
            this.button = button;
        }

        @Override
        public void bind(HarmonyHubBinding binding, String itemName) {
            binding.pressButon(device, button);
        }

        protected String getDevice() {
            return device;
        }

        protected String getButton() {
            return button;
        }
    }

    @Override
    public boolean isInBound(String itemName) {
        BindingConfig bindingConfig = bindingConfigs.get(itemName);
        if (!(bindingConfig instanceof HarmonyHubBindingConfig))
            return false;
        return ((HarmonyHubBindingConfig) bindingConfig).isIncoming();
    }
}
