package com.bestbudz.rendering.animation;

import com.bestbudz.network.Stream;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

  public SkinList(JsonObject json) {
    length = json.get("boneCount").getAsInt();
    transformTypes = new int[length];
    childIndices = new int[length][];

    JsonArray types = json.getAsJsonArray("transformTypes");
    for (int j = 0; j < length && j < types.size(); j++) {
      transformTypes[j] = types.get(j).getAsInt();
    }

    JsonArray children = json.getAsJsonArray("childIndices");
    for (int j = 0; j < length && j < children.size(); j++) {
      JsonArray inner = children.get(j).getAsJsonArray();
      childIndices[j] = new int[inner.size()];
      for (int l = 0; l < inner.size(); l++) {
        childIndices[j][l] = inner.get(l).getAsInt();
      }
    }
    for (int j = children.size(); j < length; j++) {
      childIndices[j] = new int[0];
    }
  }
}
