package com.avi.skram.attachments;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.nio.charset.Charset;

public class SpellItem implements ValueIOSerializable {

  public static String NAME = "spell_item";
  public static StreamCodec<ByteBuf, SpellItem> STREAM_CODEC = StreamCodec.ofMember(SpellItem::encode, SpellItem::new);

  public String spellName;

  public SpellItem() {

  }

  public SpellItem(String spellName) {
    this.spellName = spellName;
  }

  public SpellItem(ByteBuf buffer) {
    this.spellName = buffer.readString(buffer.readInt(), Charset.defaultCharset());
  }

  public void encode(ByteBuf buffer) {
    buffer.writeInt(this.spellName.length());
    buffer.writeCharSequence(this.spellName, Charset.defaultCharset());
  }

  @Override
  public void serialize(ValueOutput output) {
    output.putString("spellName", this.spellName);
  }

  @Override
  public void deserialize(ValueInput input) {
    this.spellName = input.getStringOr("spellName", "");
  }
}
