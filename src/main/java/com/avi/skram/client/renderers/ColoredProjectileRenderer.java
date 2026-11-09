package com.avi.skram.client.renderers;

import com.avi.skram.Skram;
import com.avi.skram.entity.projectile.SkramColoredProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.projectile.WindChargeModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import java.awt.*;

public class ColoredProjectileRenderer extends EntityRenderer<SkramColoredProjectile, EntityRenderState> {
  private static final Identifier TEXTURE_LOCATION = Identifier.fromNamespaceAndPath(Skram.MODID, "textures/entity/projectiles/colored_projectile.png");
  private final WindChargeModel model;

  public static Color green = new Color(0, 255, 0, 255);

  public ColoredProjectileRenderer(EntityRendererProvider.Context context) {
    super(context);
    this.model = new WindChargeModel(context.bakeLayer(ModelLayers.WIND_CHARGE));
  }

  @Override
  public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
    submitNodeCollector.submitModel(
      this.model,
      state,
      poseStack,
      RenderTypes.breezeWind(TEXTURE_LOCATION, this.xOffset(state.ageInTicks) % 1.0F, 0.0F),
      state.lightCoords,
      OverlayTexture.NO_OVERLAY,
      state.outlineColor,
      null,
      state.outlineColor
    );
    super.submit(state, poseStack, submitNodeCollector, camera);
  }


  protected float xOffset(float t) {
    return t * 0.03F;
  }

  @Override
  public void extractRenderState(SkramColoredProjectile entity, EntityRenderState state, float partialTicks) {
    super.extractRenderState(entity, state, partialTicks);
    state.outlineColor = entity.getColor();
  }

  @Override
  public EntityRenderState createRenderState() {
    return new EntityRenderState();
  }

}