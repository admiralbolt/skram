package com.avi.skram.magic;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.nio.charset.Charset;

public class SpellCastingInfo implements ValueIOSerializable {

  public static String NAME = "spell_casting_info";
  public static StreamCodec<ByteBuf, SpellCastingInfo> STREAM_CODEC = StreamCodec.ofMember(SpellCastingInfo::encode, SpellCastingInfo::new);

  public double currentMana = SkramMagic.DEFAULT_MAX_MANA;
  public boolean isCasting = false;
  public int castingTimer = 0;
  public String spellName = "";

  public SpellCastingInfo() {

  }

  public SpellCastingInfo(ByteBuf buffer) {
    this.currentMana = buffer.readDouble();
    this.isCasting = buffer.readBoolean();
    this.castingTimer = buffer.readInt();
    this.spellName = buffer.readString(buffer.readInt(), Charset.defaultCharset());
  }

  public void encode(ByteBuf buffer) {
    buffer.writeDouble(this.currentMana);
    buffer.writeBoolean(this.isCasting);
    buffer.writeInt(this.castingTimer);
    buffer.writeInt(this.spellName.length());
    buffer.writeCharSequence(this.spellName, Charset.defaultCharset());
  }

  @Override
  public void serialize(ValueOutput output) {
    output.putDouble("currentMana", this.currentMana);
    output.putBoolean("isCasting", this.isCasting);
    output.putInt("castingTimer", this.castingTimer);
    output.putString("spellName", this.spellName);
  }

  @Override
  public void deserialize(ValueInput input) {
    this.currentMana = input.getDoubleOr("currentMana", SkramMagic.DEFAULT_MAX_MANA);
    this.isCasting = input.getBooleanOr("isCasting", false);
    this.castingTimer = input.getIntOr("castingTimer", 0);
    this.spellName = input.getStringOr("spellName", "");
  }

}
