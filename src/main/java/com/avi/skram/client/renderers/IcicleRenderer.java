package com.avi.skram.client.renderers;

import com.avi.skram.Skram;
import com.avi.skram.entity.projectile.Icicle;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;


public class IcicleRenderer extends ArrowRenderer<Icicle, ArrowRenderState> {

  public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Skram.MODID, "textures/entity/projectiles/icicle.png");

  public IcicleRenderer(EntityRendererProvider.Context context) {
    super(context);
  }

  @Override
  public ArrowRenderState createRenderState() {
    return new ArrowRenderState();
  }


  @Override
  public @NonNull Identifier getTextureLocation(ArrowRenderState entity) {
    return TEXTURE;
  }
}

