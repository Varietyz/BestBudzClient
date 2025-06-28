package com.bestbudz.util.compression;

public final class ModelHeader
{

  public byte[] modelData;
  public int vertexCount;
  public int triangleCount;
  public int textureTriangleCount;
  public int vertexFlagsOffset;
  public int vertexXOffset;
  public int vertexYOffset;
  public int vertexZOffset;
  public int vertexLabelsOffset;
  public int triangleVerticesOffset;
  public int triangleTypesOffset;
  public int triangleColorsOffset;
  public int triangleInfoOffset;
  public int trianglePrioritiesOffset;
  public int triangleAlphaOffset;
  public int triangleLabelsOffset;
  public int textureTrianglesOffset;
  public ModelHeader() {}
}
