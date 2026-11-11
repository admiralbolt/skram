package com.avi.skram.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.ChestType;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class MegaChestItemRenderer implements NoDataSpecialModelRenderer {
  public static final Identifier MEGA_CHEST = Identifier.fromNamespaceAndPath("skram", "entity/chests/mega_chest");

  private final SpriteGetter sprites;
  private final ChestModel model;
  private final SpriteId sprite;
  private final float openness;

  public MegaChestItemRenderer(SpriteGetter sprites, ChestModel model, SpriteId sprite, float openness) {
    this.sprites = sprites;
    this.model = model;
    this.sprite = sprite;
    this.openness = openness;
  }

  public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
    submitNodeCollector.submitModel(this.model, this.openness, poseStack, lightCoords, overlayCoords, -1, this.sprite, this.sprites, outlineColor);
  }

  public void getExtents(Consumer<Vector3fc> output) {
    PoseStack poseStack = new PoseStack();
    this.model.setupAnim(this.openness);
    this.model.root().getExtentsForGui(poseStack, output);
  }

  public static record Unbaked(Identifier texture, float openness, ChestType chestType) implements NoDataSpecialModelRenderer.Unbaked {
    public static final MapCodec<net.minecraft.client.renderer.special.ChestSpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("texture").forGetter(net.minecraft.client.renderer.special.ChestSpecialRenderer.Unbaked::texture), Codec.FLOAT.optionalFieldOf("openness", 0.0F).forGetter(net.minecraft.client.renderer.special.ChestSpecialRenderer.Unbaked::openness), ChestType.CODEC.optionalFieldOf("chest_type", ChestType.SINGLE).forGetter(net.minecraft.client.renderer.special.ChestSpecialRenderer.Unbaked::chestType)).apply(i, net.minecraft.client.renderer.special.ChestSpecialRenderer.Unbaked::new));

    public Unbaked(Identifier texture, ChestType chestType) {
      this(texture, 0.0F, chestType);
    }

    public Unbaked(Identifier texture) {
      this(texture, 0.0F, ChestType.SINGLE);
    }

    public MapCodec<net.minecraft.client.renderer.special.ChestSpecialRenderer.Unbaked> type() {
      return MAP_CODEC;
    }

    public net.minecraft.client.renderer.special.ChestSpecialRenderer bake(SpecialModelRenderer.BakingContext context) {
      ChestModel model = new ChestModel(context.entityModelSet().bakeLayer((ModelLayerLocation) ChestRenderer.LAYERS.select(this.chestType)));
      SpriteId fullTexture = Sheets.CHEST_MAPPER.apply(MEGA_CHEST);
      return new net.minecraft.client.renderer.special.ChestSpecialRenderer(context.sprites(), model, fullTexture, this.openness);
    }
  }
}
