/**
 * Copyright (c) 2010-2014, openHAB.org and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.harmonyhub;

import org.openhab.binding.harmonyhub.internal.HarmonyHubBinding;
import org.openhab.core.binding.BindingProvider;

/**
 * @author Matt Tucker
 * @since 1.5.1
 */
public interface HarmonyHubBindingProvider extends BindingProvider {
    void bind(HarmonyHubBinding harmonyHubBinding, String itemName);
}
