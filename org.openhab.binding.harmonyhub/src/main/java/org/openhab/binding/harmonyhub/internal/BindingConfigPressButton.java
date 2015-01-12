package org.openhab.binding.harmonyhub.internal;

import net.whistlingfish.openhab.binding.BindingConfigProperty;
import net.whistlingfish.openhab.binding.BindingConfigType;
import net.whistlingfish.openhab.binding.Optional;

import org.openhab.core.types.Command;
import org.openhab.core.types.UnDefType;

import static net.whistlingfish.openhab.binding.BindingDirection.OUT;

@BindingConfigType(name = "press", direction = OUT)
public class BindingConfigPressButton extends HarmonyHubBindingConfig {

    @BindingConfigProperty(0)
    protected String device;

    @Optional
    @BindingConfigProperty(1)
    protected String button;

    @Override
    public void receiveCommand(final HarmonyHubBinding binding, Command command) {
        if (button == null) {
            try {
                binding.pressButon(qualifier, device, command.toString());
            } finally {
                binding.postUpdate(item.getName(), UnDefType.NULL);
            }
        } else {
            binding.pressButon(qualifier, device, button);
        }
    }

    protected String getDevice() {
        return device;
    }

    protected String getButton() {
        return button;
    }
}
