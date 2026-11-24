package com.avi.skram.client.renderers;

import com.avi.skram.entity.projectile.ThrownFlower;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class ThrownFlowerRenderer extends ThrownItemRenderer<ThrownFlower> {

  public ThrownFlowerRenderer(EntityRendererProvider.Context context) {
    super(context);
  }

}
