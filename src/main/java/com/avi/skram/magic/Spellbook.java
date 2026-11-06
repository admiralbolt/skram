package com.avi.skram.magic;

import com.avi.skram.magic.spells.SpellMinorHeal;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class Spellbook implements ValueIOSerializable {

  public static String NAME = "spellbook";
  public static StreamCodec<ByteBuf, Spellbook> STREAM_CODEC = StreamCodec.ofMember(Spellbook::encode, Spellbook::new);


  public Set<String> knownSpells = new HashSet<>();

  public Spellbook() {
    this.knownSpells.add(SpellMinorHeal.NAME);
  }

  public Spellbook(ByteBuf buffer) {
    int totalLength = buffer.readInt();
    for (int i = 0; i < totalLength; ++i) {
      this.knownSpells.add(buffer.readString(buffer.readInt(), Charset.defaultCharset()));
    }
  }

  public void encode(ByteBuf buffer) {
    buffer.writeInt(this.knownSpells.size());
    // For each spell we write the length and then the string itself.
    for (String spell : this.knownSpells) {
      buffer.writeInt(spell.length());
      buffer.writeCharSequence(spell, Charset.defaultCharset());
    }
  }

  @Override
  public void serialize(ValueOutput output) {
    output.putInt("totalSpells", this.knownSpells.size());
    int i = 0;
    for (String spell : knownSpells) {
      output.putString("spell_" + i, spell);
      i++;
    }
  }

  @Override
  public void deserialize(ValueInput input) {
    int totalSpells = input.getIntOr("totalSpells", 0);
    for (int i = 0; i < totalSpells; ++i) {
      this.knownSpells.add(input.getStringOr("spell_" + i, ""));
    }
  }
}
