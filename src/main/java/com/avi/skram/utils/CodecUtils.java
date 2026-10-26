package com.avi.skram.utils;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class CodecUtils {

  public static final StreamCodec<ByteBuf, boolean[]> BOOLEAN_ARRAY_STREAM_CODEC = new StreamCodec<>() {
    @Override
    public boolean[] decode(ByteBuf buffer) {
      int length = buffer.readInt();
      boolean[] array = new boolean[length];
      for (int i = 0; i < length; i++) {
        array[i] = buffer.readBoolean();
      }
      return array;
    }

    @Override
    public void encode(ByteBuf buffer, boolean[] value) {
      buffer.writeInt(value.length);
      for (boolean b : value) {
        buffer.writeBoolean(b);
      }
    }
  };
}
