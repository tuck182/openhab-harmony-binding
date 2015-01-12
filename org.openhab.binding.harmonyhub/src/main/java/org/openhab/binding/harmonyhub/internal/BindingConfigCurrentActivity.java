package org.openhab.binding.harmonyhub.internal;

import net.whistlingfish.harmony.ActivityChangeListener;
import net.whistlingfish.harmony.config.Activity;
import net.whistlingfish.openhab.binding.BindingConfigType;

import org.openhab.core.library.types.StringType;

import static net.whistlingfish.openhab.binding.BindingDirection.IN;

@BindingConfigType(name = "currentActivity", direction = IN)
public class BindingConfigCurrentActivity extends HarmonyHubBindingConfig {
    @Override
    public void bind(final HarmonyHubBinding binding) {
        final String itemName = item.getName();

        binding.registerListener(qualifier, itemName, new ActivityChangeListener() {
            @Override
            public void activityStarted(Activity activity) {
                binding.postUpdate(itemName, new StringType(activity.getLabel()));
            }
        });
    }
}
