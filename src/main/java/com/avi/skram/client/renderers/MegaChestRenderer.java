package com.avi.skram.client.renderers;

import com.avi.skram.Skram;
import com.avi.skram.blocks.MegaChestBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

public class MegaChestRenderer<T extends BaseContainerBlockEntity & LidBlockEntity> implements BlockEntityRenderer<T, ChestRenderState> {

  private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
    Skram.MODID,
    "textures/entity/chests/mega_chest.png"
  );

  private final ChestModel model;

  public MegaChestRenderer(BlockEntityRendererProvider.Context context) {
    this.model = new ChestModel(context.bakeLayer(ModelLayers.CHEST));
  }

  @Override
  public ChestRenderState createRenderState() {
    return new ChestRenderState();
  }

  @Override
  public void extractRenderState(
    T blockEntity,
    ChestRenderState state,
    float partialTicks,
    Vec3 cameraPosition,
    ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
  ) {
    BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

    BlockState blockState = blockEntity.getBlockState();
    state.type = ChestType.SINGLE;
    state.facing = blockState.hasProperty(MegaChestBlock.FACING)
      ? blockState.getValue(MegaChestBlock.FACING)
      : Direction.NORTH;
    state.open = blockEntity.getOpenNess(partialTicks);
  }

  @Override
  public void submit(
    ChestRenderState state,
    PoseStack poseStack,
    SubmitNodeCollector submitNodeCollector,
    CameraRenderState camera
  ) {
    poseStack.pushPose();
    poseStack.mulPose(modelTransformation(state.facing));

    float open = 1.0F - state.open;
    open = 1.0F - open * open * open;

    submitNodeCollector.submitModel(
      this.model,
      open,
      poseStack,
      RenderTypes.entityCutoutCull(TEXTURE),
      state.lightCoords,
      OverlayTexture.NO_OVERLAY,
      0
    );

    if (state.breakProgress != null) {
      submitNodeCollector.order(1).submitCrumblingOverlay(
        this.model,
        open,
        poseStack,
        RenderTypes.entityCutoutCull(TEXTURE),
        state.lightCoords,
        OverlayTexture.NO_OVERLAY,
        0,
        state.breakProgress
      );
    }

    poseStack.popPose();
  }

  private static Transformation modelTransformation(Direction facing) {
    return new Transformation(
      new Matrix4f().rotationAround(Axis.YP.rotationDegrees(-facing.toYRot()), 0.5F, 0.0F, 0.5F)
    );
  }
}
