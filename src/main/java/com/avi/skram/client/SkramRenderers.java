package com.avi.skram.client;

import com.avi.skram.Skram;
import com.avi.skram.client.renderers.ColoredProjectileRenderer;
import com.avi.skram.client.renderers.IcicleRenderer;
import com.avi.skram.entity.SkramEntities;
import net.minecraft.client.renderer.entity.GoatRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Skram.MODID)
public class SkramRenderers {

  @SubscribeEvent
  public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(SkramEntities.COLORED_PROJECTILE.get(), ColoredProjectileRenderer::new);
    event.registerEntityRenderer(SkramEntities.DECOY.get(), GoatRenderer::new);
    event.registerEntityRenderer(SkramEntities.ICICLE.get(), IcicleRenderer::new);
  }

}
