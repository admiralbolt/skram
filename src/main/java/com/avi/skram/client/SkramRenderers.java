package com.avi.skram.client;

import com.avi.skram.Skram;
import com.avi.skram.blocks.SkramBlocks;
import com.avi.skram.client.renderers.ColoredProjectileRenderer;
import com.avi.skram.client.renderers.IcicleRenderer;
import com.avi.skram.client.renderers.MegaChestItemRenderer;
import com.avi.skram.client.renderers.MegaChestRenderer;
import com.avi.skram.entity.SkramEntities;
import net.minecraft.client.renderer.entity.GoatRenderer;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

@EventBusSubscriber(modid = Skram.MODID, value = Dist.CLIENT)
@Mod(value = Skram.MODID, dist = Dist.CLIENT)
public class SkramRenderers {

  @SubscribeEvent
  public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(SkramEntities.COLORED_PROJECTILE.get(), ColoredProjectileRenderer::new);
    event.registerEntityRenderer(SkramEntities.DECOY.get(), GoatRenderer::new);
    event.registerEntityRenderer(SkramEntities.ICICLE.get(), IcicleRenderer::new);
    event.registerBlockEntityRenderer(
      SkramBlocks.GIGA_CHEST_BLOCK_ENTITY.get(),
      context -> new MegaChestRenderer<>(context, MegaChestRenderer.GIGA_CHEST_TEXTURE)
    );
    event.registerBlockEntityRenderer(SkramBlocks.MEGA_CHEST_BLOCK_ENTITY.get(), MegaChestRenderer::new);
  }

  @SubscribeEvent
  public static void registerSpecialModels(RegisterSpecialModelRendererEvent event) {
    event.register(
      Identifier.fromNamespaceAndPath(Skram.MODID, "mega_chest_special"),
      MegaChestItemRenderer.Unbaked.MAP_CODEC
    );
  }

}
