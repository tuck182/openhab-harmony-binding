package org.openhab.binding.harmonyhub.internal;

import net.whistlingfish.openhab.binding.BindingConfigProperty;
import net.whistlingfish.openhab.binding.BindingConfigType;
import net.whistlingfish.openhab.binding.Optional;

import org.openhab.core.types.Command;
import org.openhab.core.types.UnDefType;

import static net.whistlingfish.openhab.binding.BindingDirection.OUT;

@BindingConfigType(name = "start", direction = OUT)
public class BindingConfigStartActivity extends HarmonyHubBindingConfig {

    @Optional
    @BindingConfigProperty(0)
    private String activity;

    @Override
    public void receiveCommand(HarmonyHubBinding binding, Command command) {
        if (activity != null) {
            try {
                binding.startActivity(qualifier, activity);
            } finally {
                binding.postUpdate(item.getName(), UnDefType.NULL);
            }
        } else {
            binding.startActivity(qualifier, command.toString());
        }
    }

    protected String getActivity() {
        return activity;
    }
}
