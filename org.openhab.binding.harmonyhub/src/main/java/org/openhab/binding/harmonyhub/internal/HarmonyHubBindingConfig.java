package org.openhab.binding.harmonyhub.internal;

import net.whistlingfish.openhab.binding.AnnotationBasedBindingConfig;
import net.whistlingfish.openhab.binding.BindingConfigQualifier;

import org.openhab.core.types.Command;
import org.openhab.core.types.State;

public abstract class HarmonyHubBindingConfig extends
        AnnotationBasedBindingConfig<HarmonyHubBindingConfig, HarmonyHubBinding, HarmonyHubGenericBindingProvider> {

    @BindingConfigQualifier
    protected String qualifier = "";

    @Override
    public void bind(HarmonyHubBinding binding) {
    }

    @Override
    public void receiveCommand(HarmonyHubBinding binding, Command command) {
    }

    @Override
    public void receiveUpdate(HarmonyHubBinding binding, State newState) {
    }

    protected String getHostname() {
        return qualifier;
    }
}
