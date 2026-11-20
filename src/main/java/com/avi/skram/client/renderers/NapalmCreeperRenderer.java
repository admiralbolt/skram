package com.avi.skram.client.renderers;

import com.avi.skram.Skram;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class NapalmCreeperRenderer extends CreeperRenderer {

  public static final Identifier NAPALM_CREEPER_TEXTURE = Identifier.fromNamespaceAndPath(Skram.MODID, "textures/entity/napalm_creeper.png");

  public NapalmCreeperRenderer(EntityRendererProvider.Context context) {
    super(context);
  }

  @Override
  public @NonNull Identifier getTextureLocation(@NonNull CreeperRenderState state) {
    return NAPALM_CREEPER_TEXTURE;
  }

}
