package com.avi.skram.client;

import com.avi.skram.Skram;
import com.avi.skram.client.renderers.ColoredProjectileRenderer;
import com.avi.skram.entity.SkramEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Skram.MODID)
public class SkramRenderers {

  @SubscribeEvent // on the mod event bus only on the physical client
  public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(SkramEntities.COLORED_PROJECTILE.get(), ColoredProjectileRenderer::new);
  }

}
