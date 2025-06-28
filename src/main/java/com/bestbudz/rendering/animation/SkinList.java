package com.bestbudz.rendering.animation;

import com.bestbudz.network.Stream;

public final class SkinList
{

  public final int[] transformTypes;
  public final int[][] childIndices;
  public final int length;

  public SkinList(Stream buffer) {
    length = buffer.readUnsignedWord();
    transformTypes = new int[length];
    childIndices = new int[length][];
    for (int j = 0; j < length; j++) {
      transformTypes[j] = buffer.readUnsignedWord();
    }
    for (int j = 0; j < length; j++) {
      childIndices[j] = new int[buffer.readUnsignedWord()];
    }
    for (int j = 0; j < length; j++) {
      for (int l = 0; l < childIndices[j].length; l++) {
        childIndices[j][l] = buffer.readUnsignedWord();
      }
    }
  }
}
