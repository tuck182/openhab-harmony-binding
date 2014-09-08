package org.openhab.binding.harmonyhub.internal;

import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhab.binding.harmonyhub.internal.HarmonyHubGenericBindingProvider.HarmonyHubBindingConfig;
import org.openhab.binding.harmonyhub.internal.HarmonyHubGenericBindingProvider.HarmonyHubCurrentActivityBindingConfig;
import org.openhab.binding.harmonyhub.internal.HarmonyHubGenericBindingProvider.HarmonyHubPressButtonBindingConfig;
import org.openhab.binding.harmonyhub.internal.HarmonyHubGenericBindingProvider.HarmonyHubStartActivityBindingConfig;
import org.openhab.core.binding.BindingConfig;
import org.openhab.core.items.Item;
import org.openhab.model.item.binding.BindingConfigParseException;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HarmonyHubGenericBindingProviderTest {

    private BindingConfig createdBindingConfig;
    private HarmonyHubGenericBindingProvider provider = new HarmonyHubGenericBindingProvider() {
        protected BindingConfig parseBindingConfig(Pattern pattern, Item item, String bindingConfig)
                throws BindingConfigParseException {
            createdBindingConfig = super.parseBindingConfig(pattern, item, bindingConfig);
            return createdBindingConfig;
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
        verifyBindingCreates("<[currentActivity]", HarmonyHubCurrentActivityBindingConfig.class);
    }

    @Test
    public void parseBindingStartActivity() throws BindingConfigParseException {
        HarmonyHubStartActivityBindingConfig binding = //
                verifyBindingCreates(">[start:Watch TV]", HarmonyHubStartActivityBindingConfig.class);
        assertThat(binding.getActivity(), is("Watch TV"));
    }

    @Test
    public void parseBindingPressButton() throws BindingConfigParseException {
        HarmonyHubPressButtonBindingConfig binding = //
                verifyBindingCreates(">[press:Amp:Mute]", HarmonyHubPressButtonBindingConfig.class);
        assertThat(binding.getDevice(), is("Amp"));
        assertThat(binding.getButton(), is("Mute"));
    }

    private <T extends HarmonyHubBindingConfig> T verifyBindingCreates(String bindingConfig, Class<T> clazz)
            throws BindingConfigParseException {
        provider.processBindingConfiguration("test context", item, bindingConfig);
        assertThat(createdBindingConfig, is(clazz));
        return clazz.cast(createdBindingConfig);
    }

}
