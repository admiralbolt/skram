package com.avi.skram.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class MegaChestItemRenderer implements NoDataSpecialModelRenderer {

  private final ChestModel model;
  private final float openness;
  private final Identifier texture;

  public MegaChestItemRenderer(ChestModel model, float openness, Identifier texture) {
    this.model = model;
    this.openness = openness;
    this.texture = texture;
  }

  @Override
  public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
    submitNodeCollector.submitModel(
      this.model,
      this.openness,
      poseStack,
      this.texture,
      lightCoords,
      overlayCoords,
      outlineColor
    );
  }

  @Override
  public void getExtents(Consumer<Vector3fc> output) {
    PoseStack poseStack = new PoseStack();
    this.model.setupAnim(this.openness);
    this.model.root().getExtentsForGui(poseStack, output);
  }

  public static record Unbaked(Identifier texture, float openness, ChestType chestType) implements NoDataSpecialModelRenderer.Unbaked {
    public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(
      Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture),
      Codec.FLOAT.optionalFieldOf("openness", 0.0F).forGetter(Unbaked::openness),
      ChestType.CODEC.optionalFieldOf("chest_type", ChestType.SINGLE).forGetter(Unbaked::chestType)
    ).apply(i, Unbaked::new));

    public Unbaked(Identifier texture, ChestType chestType) {
      this(texture, 0.0F, chestType);
    }

    public Unbaked(Identifier texture) {
      this(texture, 0.0F, ChestType.SINGLE);
    }

    @Override
    public MapCodec<Unbaked> type() {
      return MAP_CODEC;
    }

    @Override
    public MegaChestItemRenderer bake(SpecialModelRenderer.BakingContext context) {
      ChestModel model = new ChestModel(context.entityModelSet().bakeLayer(ChestRenderer.LAYERS.select(this.chestType)));
      return new MegaChestItemRenderer(model, this.openness, this.texture);
    }
  }
}
