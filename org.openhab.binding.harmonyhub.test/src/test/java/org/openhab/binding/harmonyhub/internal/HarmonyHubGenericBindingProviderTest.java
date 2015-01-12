package org.openhab.binding.harmonyhub.internal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.model.item.binding.BindingConfigParseException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

import static org.junit.Assert.assertThat;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HarmonyHubGenericBindingProviderTest {

    private BindingConfig createdBindingConfig;
    private HarmonyHubGenericBindingProvider provider = new HarmonyHubGenericBindingProvider() {
        @Override
        protected void addBindingConfig(Item item, BindingConfig config) {
            createdBindingConfig = config;
            super.addBindingConfig(item, config);
        }
    };

    @Mock
    private Item item;

    @Before
    public void setUp() {
        when(item.getName()).thenReturn("item name");
    }

    @Test
    public void parseBindingCurrentActivity() throws BindingConfigParseException {
        verifyBindingCreates("<[currentActivity]", BindingConfigCurrentActivity.class);
    }

    @Test
    public void parseBindingCurrentActivityWithHost() throws BindingConfigParseException {
        BindingConfigCurrentActivity binding = //
                verifyBindingCreates("<[hostname:currentActivity]", BindingConfigCurrentActivity.class);
        assertThat(binding.getHostname(), is("hostname"));
    }

    @Test
    public void parseBindingStartActivity() throws BindingConfigParseException {
        BindingConfigStartActivity binding = //
                verifyBindingCreates(">[start:Watch TV]", BindingConfigStartActivity.class);
        assertThat(binding.getActivity(), is("Watch TV"));
    }

    @Test
    public void parseBindingStartAnyActivity() throws BindingConfigParseException {
        BindingConfigStartActivity binding = //
                verifyBindingCreates(">[start]", BindingConfigStartActivity.class);
        assertThat(binding.getActivity(), is(nullValue()));
    }

    @Test
    public void parseBindingStartAnyActivityWithHost() throws BindingConfigParseException {
        BindingConfigStartActivity binding = //
                verifyBindingCreates(">[hostname:start]", BindingConfigStartActivity.class);
        assertThat(binding.getActivity(), is(nullValue()));
        assertThat(binding.getHostname(), is("hostname"));
    }

    @Test
    public void parseBindingPressButton() throws BindingConfigParseException {
        BindingConfigPressButton binding = //
                verifyBindingCreates(">[press:Amp:Mute]", BindingConfigPressButton.class);
        assertThat(binding.getDevice(), is("Amp"));
        assertThat(binding.getButton(), is("Mute"));
    }

    @Test
    public void parseBindingPressAnyButton() throws BindingConfigParseException {
        BindingConfigPressButton binding = //
                verifyBindingCreates(">[press:Amp]", BindingConfigPressButton.class);
        assertThat(binding.getDevice(), is("Amp"));
        assertThat(binding.getButton(), is(nullValue()));
    }

    @Test
    public void parseBindingPressAnyButtonWithHost() throws BindingConfigParseException {
        BindingConfigPressButton binding = //
                verifyBindingCreates(">[hostname:press:Amp]", BindingConfigPressButton.class);
        assertThat(binding.getDevice(), is("Amp"));
        assertThat(binding.getButton(), is(nullValue()));
        assertThat(binding.getHostname(), is("hostname"));
    }

    private <T extends HarmonyHubBindingConfig> T verifyBindingCreates(String bindingConfig, Class<T> clazz)
            throws BindingConfigParseException {
        provider.processBindingConfiguration("test context", item, bindingConfig);
        assertThat(createdBindingConfig, is(clazz));
        return clazz.cast(createdBindingConfig);
    }

}
