package com.avi.skram.ui.tabs;

import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public record TabEntry(String key, Component label, Identifier icon, int iconTextureSize, Supplier<Layout> details) {
}
