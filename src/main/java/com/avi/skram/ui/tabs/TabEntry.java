package com.avi.skram.ui.tabs;

import net.minecraft.client.gui.layouts.Layout;

import java.util.function.Supplier;

public record TabEntry(String key, Layout indexContent, int indexWidth, int indexHeight, Supplier<Layout> details) {
}
