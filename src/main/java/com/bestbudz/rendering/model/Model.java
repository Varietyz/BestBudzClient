package com.bestbudz.rendering.model;

import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.engine.gpu.RS317GPUInterface;
import com.bestbudz.network.CacheManagerBase;
import com.bestbudz.network.Stream;
import com.bestbudz.rendering.Animable;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import com.bestbudz.rendering.SequenceFrame;
import com.bestbudz.rendering.animation.SkinList;
import com.bestbudz.util.compression.ModelHeader;
import com.bestbudz.engine.core.gamerender.WorldController;
import java.util.Objects;

public class Model extends Animable {

	public static boolean[] newmodel;
	public static int anInt1620;
	public static final Model aModel_1621 = new Model();
	public static boolean aBoolean1684;
	public static int anInt1685;
	public static int anInt1686;
	public static int modelCount;
	public static final int[] modelHashes = new int[1000];
	public static int[] modelIntArray1;
	public static int[] modelIntArray2;
	static ModelHeader[] aModelHeaderArray1661;
	static CacheManagerBase aCacheManagerBase_1662;
	static int[] modelIntArray3;
	static int[] modelIntArray4;

	private static final ThreadLocal<WorkSpace> workspace = ThreadLocal.withInitial(WorkSpace::new);

	private static class WorkSpace {
		boolean[] aBooleanArray1663 = new boolean[8000];
		boolean[] aBooleanArray1664 = new boolean[8000];
		final int[] anIntArray1665 = new int[8000];
		int[] anIntArray1666 = new int[8000];
		int[] anIntArray1667 = new int[8000];
		int[] anIntArray1668 = new int[8000];
		int[] anIntArray1669 = new int[8000];
		int[] anIntArray1670 = new int[8000];
		int[] anIntArray1671 = new int[2000];
		int[][] anIntArrayArray1672 = new int[2000][512];
		int[] anIntArray1673 = new int[12];
		int[][] anIntArrayArray1674 = new int[12][2000];
		int[] anIntArray1675 = new int[2000];
		int[] anIntArray1676 = new int[2000];
		int[] anIntArray1677 = new int[12];
		final int[] anIntArray1678 = new int[10];
		final int[] anIntArray1679 = new int[10];
		final int[] anIntArray1680 = new int[10];

		private int[] tempVertexBuffer = new int[2000];
		private int[] tempTriangleBuffer = new int[2000];
		private int[] tempColorBuffer = new int[2000];
		private int[] tempAlphaBuffer = new int[2000];

		void ensureCapacity(int vertices, int triangles) {
			if (tempVertexBuffer.length < vertices) {
				tempVertexBuffer = new int[vertices + 1000];
			}
			if (tempTriangleBuffer.length < triangles) {
				tempTriangleBuffer = new int[triangles + 1000];
				tempColorBuffer = new int[triangles + 1000];
				tempAlphaBuffer = new int[triangles + 1000];
			}
		}

		void clear() {

			java.util.Arrays.fill(aBooleanArray1663, 0, Math.min(8000, aBooleanArray1663.length), false);
			java.util.Arrays.fill(aBooleanArray1664, 0, Math.min(8000, aBooleanArray1664.length), false);
			java.util.Arrays.fill(anIntArray1671, 0, Math.min(1500, anIntArray1671.length), 0);
			java.util.Arrays.fill(anIntArray1673, 0, Math.min(12, anIntArray1673.length), 0);
			java.util.Arrays.fill(anIntArray1677, 0, Math.min(12, anIntArray1677.length), 0);
		}
	}

	static int anInt1681;
	static int anInt1682;
	static int anInt1683;

	static {
		modelIntArray1 = Rasterizer.sinTable;
		modelIntArray2 = Rasterizer.cosTable;
		modelIntArray3 = Rasterizer.colorPalette;
		modelIntArray4 = Rasterizer.reciprocalTable;
	}

	public int vertexCount;
	public int[] verticesX;
	public int[] verticesY;
	public int[] verticesZ;
	public int triangleCount;
	public int[] triangleVertexA;
	public int[] triangleVertexB;
	public int[] triangleVertexC;
	public int[] triangleColorA;
	public int[] triangleColorB;
	public int[] triangleColorC;
	public int[] triangleInfo;
	public int[] trianglePriorities;
	public int[] triangleAlpha;
	public int[] triangleColors;
	public int anInt1641;
	public int textureTriangleCount;
	public int[] textureTriangleA;
	public int[] textureTriangleB;
	public int[] textureTriangleC;
	public int anInt1646;
	public int anInt1647;
	public int anInt1648;
	public int anInt1649;
	public int diameter;
	public int maxY;
	public int boundingRadius;
	public int boundingCylinderRadius;
	public int anInt1654;
	public int[] vertexLabels;
	public int[] triangleLabels;
	public int[][] anIntArrayArray1657;
	public int[][] anIntArrayArray1658;
	public boolean aBoolean1659;
	public Point3D[] aPoint3DArray1660;
	private boolean aBoolean1618;

	public Model(int modelId) {
		byte[] is = aModelHeaderArray1661[modelId].modelData;
		if (is[is.length - 1] == -1 && is[is.length - 2] == -1)
			decodeModel622(is, modelId);
		else
			decodeOldModel(modelId);
		if (newmodel[modelId]) {
			if (trianglePriorities != null) {
				if (modelId >= 1 && modelId <= 65535) {
					for (int index = 0; index < trianglePriorities.length; index++) {
						trianglePriorities[index] = 10;
					}
				}
			}
		}
	}

	private Model() {
		aBoolean1618 = true;
		aBoolean1659 = false;
		if (!true)
			aBoolean1618 = false;
	}

	public Model(int i, Model[] amodel) {
		aBoolean1618 = true;
		aBoolean1659 = false;
		anInt1620++;
		boolean flag = false;
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		vertexCount = 0;
		triangleCount = 0;
		textureTriangleCount = 0;
		anInt1641 = -1;
		for (int k = 0; k < i; k++) {
			Model model = amodel[k];
			if (model != null) {
				vertexCount += model.vertexCount;
				triangleCount += model.triangleCount;
				textureTriangleCount += model.textureTriangleCount;
				flag |= model.triangleInfo != null;
				if (model.trianglePriorities != null) {
					flag1 = true;
				} else {
					if (anInt1641 == -1)
						anInt1641 = model.anInt1641;
					if (anInt1641 != model.anInt1641)
						flag1 = true;
				}
				flag2 |= model.triangleAlpha != null;
				flag3 |= model.triangleLabels != null;
			}
		}

		verticesX = new int[vertexCount];
		verticesY = new int[vertexCount];
		verticesZ = new int[vertexCount];
		vertexLabels = new int[vertexCount];
		triangleVertexA = new int[triangleCount];
		triangleVertexB = new int[triangleCount];
		triangleVertexC = new int[triangleCount];
		textureTriangleA = new int[textureTriangleCount];
		textureTriangleB = new int[textureTriangleCount];
		textureTriangleC = new int[textureTriangleCount];
		if (flag)
			triangleInfo = new int[triangleCount];
		if (flag1)
			trianglePriorities = new int[triangleCount];
		if (flag2)
			triangleAlpha = new int[triangleCount];
		if (flag3)
			triangleLabels = new int[triangleCount];
		triangleColors = new int[triangleCount];
		vertexCount = 0;
		triangleCount = 0;
		textureTriangleCount = 0;
		int l = 0;
		for (int i1 = 0; i1 < i; i1++) {
			Model model_1 = amodel[i1];
			if (model_1 != null) {
				for (int j1 = 0; j1 < model_1.triangleCount; j1++) {
					if (flag)
						if (model_1.triangleInfo == null) {
							triangleInfo[triangleCount] = 0;
						} else {
							int k1 = model_1.triangleInfo[j1];
							if ((k1 & 2) == 2)
								k1 += l << 2;
							triangleInfo[triangleCount] = k1;
						}
					if (flag1)
						if (model_1.trianglePriorities == null)
							trianglePriorities[triangleCount] = model_1.anInt1641;
						else
							trianglePriorities[triangleCount] = model_1.trianglePriorities[j1];
					if (flag2)
						if (model_1.triangleAlpha == null)
							triangleAlpha[triangleCount] = 0;
						else
							triangleAlpha[triangleCount] = model_1.triangleAlpha[j1];

					if (flag3 && model_1.triangleLabels != null)
						triangleLabels[triangleCount] = model_1.triangleLabels[j1];
					triangleColors[triangleCount] = model_1.triangleColors[j1];
					triangleVertexA[triangleCount] = mergeVertex(model_1, model_1.triangleVertexA[j1]);
					triangleVertexB[triangleCount] = mergeVertex(model_1, model_1.triangleVertexB[j1]);
					triangleVertexC[triangleCount] = mergeVertex(model_1, model_1.triangleVertexC[j1]);
					triangleCount++;
				}

				for (int l1 = 0; l1 < model_1.textureTriangleCount; l1++) {
					textureTriangleA[textureTriangleCount] = mergeVertex(model_1, model_1.textureTriangleA[l1]);
					textureTriangleB[textureTriangleCount] = mergeVertex(model_1, model_1.textureTriangleB[l1]);
					textureTriangleC[textureTriangleCount] = mergeVertex(model_1, model_1.textureTriangleC[l1]);
					textureTriangleCount++;
				}

				l += model_1.textureTriangleCount;
			}
		}
	}

	public Model(Model[] amodel) {
		int i = 2;
		aBoolean1618 = true;
		aBoolean1659 = false;
		anInt1620++;
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		boolean flag4 = false;
		vertexCount = 0;
		triangleCount = 0;
		textureTriangleCount = 0;
		anInt1641 = -1;
		for (int k = 0; k < i; k++) {
			Model model = amodel[k];
			if (model != null) {
				vertexCount += model.vertexCount;
				triangleCount += model.triangleCount;
				textureTriangleCount += model.textureTriangleCount;
				flag1 |= model.triangleInfo != null;
				if (model.trianglePriorities != null) {
					flag2 = true;
				} else {
					if (anInt1641 == -1)
						anInt1641 = model.anInt1641;
					if (anInt1641 != model.anInt1641)
						flag2 = true;
				}
				flag3 |= model.triangleAlpha != null;
				flag4 |= model.triangleColors != null;
			}
		}

		verticesX = new int[vertexCount];
		verticesY = new int[vertexCount];
		verticesZ = new int[vertexCount];
		triangleVertexA = new int[triangleCount];
		triangleVertexB = new int[triangleCount];
		triangleVertexC = new int[triangleCount];
		triangleColorA = new int[triangleCount];
		triangleColorB = new int[triangleCount];
		triangleColorC = new int[triangleCount];
		textureTriangleA = new int[textureTriangleCount];
		textureTriangleB = new int[textureTriangleCount];
		textureTriangleC = new int[textureTriangleCount];
		if (flag1)
			triangleInfo = new int[triangleCount];
		if (flag2)
			trianglePriorities = new int[triangleCount];
		if (flag3)
			triangleAlpha = new int[triangleCount];
		if (flag4)
			triangleColors = new int[triangleCount];
		vertexCount = 0;
		triangleCount = 0;
		textureTriangleCount = 0;
		int i1 = 0;
		for (int j1 = 0; j1 < i; j1++) {
			Model model_1 = amodel[j1];
			if (model_1 != null) {
				int k1 = vertexCount;
				for (int l1 = 0; l1 < model_1.vertexCount; l1++) {
					verticesX[vertexCount] = model_1.verticesX[l1];
					verticesY[vertexCount] = model_1.verticesY[l1];
					verticesZ[vertexCount] = model_1.verticesZ[l1];
					vertexCount++;
				}

				for (int i2 = 0; i2 < model_1.triangleCount; i2++) {
					triangleVertexA[triangleCount] = model_1.triangleVertexA[i2] + k1;
					triangleVertexB[triangleCount] = model_1.triangleVertexB[i2] + k1;
					triangleVertexC[triangleCount] = model_1.triangleVertexC[i2] + k1;
					triangleColorA[triangleCount] = model_1.triangleColorA[i2];
					triangleColorB[triangleCount] = model_1.triangleColorB[i2];
					triangleColorC[triangleCount] = model_1.triangleColorC[i2];
					if (flag1)
						if (model_1.triangleInfo == null) {
							triangleInfo[triangleCount] = 0;
						} else {
							int j2 = model_1.triangleInfo[i2];
							if ((j2 & 2) == 2)
								j2 += i1 << 2;
							triangleInfo[triangleCount] = j2;
						}
					if (flag2)
						if (model_1.trianglePriorities == null)
							trianglePriorities[triangleCount] = model_1.anInt1641;
						else
							trianglePriorities[triangleCount] = model_1.trianglePriorities[i2];
					if (flag3)
						if (model_1.triangleAlpha == null)
							triangleAlpha[triangleCount] = 0;
						else
							triangleAlpha[triangleCount] = model_1.triangleAlpha[i2];
					if (flag4 && model_1.triangleColors != null)
						triangleColors[triangleCount] = model_1.triangleColors[i2];

					triangleCount++;
				}

				for (int k2 = 0; k2 < model_1.textureTriangleCount; k2++) {
					textureTriangleA[textureTriangleCount] = model_1.textureTriangleA[k2] + k1;
					textureTriangleB[textureTriangleCount] = model_1.textureTriangleB[k2] + k1;
					textureTriangleC[textureTriangleCount] = model_1.textureTriangleC[k2] + k1;
					textureTriangleCount++;
				}

				i1 += model_1.textureTriangleCount;
			}
		}

		calculateBounds();
	}

	public Model(boolean flag, boolean flag1, boolean flag2, Model model) {
		aBoolean1618 = true;
		aBoolean1659 = false;
		anInt1620++;
		vertexCount = model.vertexCount;
		triangleCount = model.triangleCount;
		textureTriangleCount = model.textureTriangleCount;
		if (flag2) {
			verticesX = model.verticesX;
			verticesY = model.verticesY;
			verticesZ = model.verticesZ;
		} else {
			verticesX = new int[vertexCount];
			verticesY = new int[vertexCount];
			verticesZ = new int[vertexCount];
			for (int j = 0; j < vertexCount; j++) {
				verticesX[j] = model.verticesX[j];
				verticesY[j] = model.verticesY[j];
				verticesZ[j] = model.verticesZ[j];
			}
		}
		if (flag) {
			triangleColors = model.triangleColors;
		} else {
			triangleColors = new int[triangleCount];
			System.arraycopy(model.triangleColors, 0, triangleColors, 0, triangleCount);
		}
		if (flag1) {
			triangleAlpha = model.triangleAlpha;
		} else {
			triangleAlpha = new int[triangleCount];
			if (model.triangleAlpha == null) {
				for (int l = 0; l < triangleCount; l++)
					triangleAlpha[l] = 0;
			} else {
				System.arraycopy(model.triangleAlpha, 0, triangleAlpha, 0, triangleCount);
			}
		}
		vertexLabels = model.vertexLabels;
		triangleLabels = model.triangleLabels;
		triangleInfo = model.triangleInfo;
		triangleVertexA = model.triangleVertexA;
		triangleVertexB = model.triangleVertexB;
		triangleVertexC = model.triangleVertexC;
		trianglePriorities = model.trianglePriorities;
		anInt1641 = model.anInt1641;
		textureTriangleA = model.textureTriangleA;
		textureTriangleB = model.textureTriangleB;
		textureTriangleC = model.textureTriangleC;
	}

	public Model(boolean flag, boolean flag1, Model model) {
		aBoolean1618 = true;
		aBoolean1659 = false;
		anInt1620++;
		vertexCount = model.vertexCount;
		triangleCount = model.triangleCount;
		textureTriangleCount = model.textureTriangleCount;
		if (flag) {
			verticesY = new int[vertexCount];
			System.arraycopy(model.verticesY, 0, verticesY, 0, vertexCount);
		} else {
			verticesY = model.verticesY;
		}
		if (flag1) {
			triangleColorA = new int[triangleCount];
			triangleColorB = new int[triangleCount];
			triangleColorC = new int[triangleCount];
			for (int k = 0; k < triangleCount; k++) {
				triangleColorA[k] = model.triangleColorA[k];
				triangleColorB[k] = model.triangleColorB[k];
				triangleColorC[k] = model.triangleColorC[k];
			}

			triangleInfo = new int[triangleCount];
			if (model.triangleInfo == null) {
				for (int l = 0; l < triangleCount; l++)
					triangleInfo[l] = 0;
			} else {
				System.arraycopy(model.triangleInfo, 0, triangleInfo, 0, triangleCount);
			}
			super.vertices = new Point3D[vertexCount];
			for (int j1 = 0; j1 < vertexCount; j1++) {
				Point3D point3D = super.vertices[j1] = new Point3D();
				Point3D point3D_1 = model.vertices[j1];
				point3D.normalX = point3D_1.normalX;
				point3D.normalY = point3D_1.normalY;
				point3D.normalZ = point3D_1.normalZ;
				point3D.vertexCount = point3D_1.vertexCount;
			}

			aPoint3DArray1660 = model.aPoint3DArray1660;
		} else {
			triangleColorA = model.triangleColorA;
			triangleColorB = model.triangleColorB;
			triangleColorC = model.triangleColorC;
			triangleInfo = model.triangleInfo;
		}
		verticesX = model.verticesX;
		verticesZ = model.verticesZ;
		triangleColors = model.triangleColors;
		triangleAlpha = model.triangleAlpha;
		trianglePriorities = model.trianglePriorities;
		anInt1641 = model.anInt1641;
		triangleVertexA = model.triangleVertexA;
		triangleVertexB = model.triangleVertexB;
		triangleVertexC = model.triangleVertexC;
		textureTriangleA = model.textureTriangleA;
		textureTriangleB = model.textureTriangleB;
		textureTriangleC = model.textureTriangleC;
		super.modelHeight = model.modelHeight;

		diameter = model.diameter;
		boundingCylinderRadius = model.boundingCylinderRadius;
		boundingRadius = model.boundingRadius;
		anInt1646 = model.anInt1646;
		anInt1648 = model.anInt1648;
		anInt1649 = model.anInt1649;
		anInt1647 = model.anInt1647;
	}

	public static void clearCache() {
		aModelHeaderArray1661 = null;
		aCacheManagerBase_1662 = null;
		modelIntArray1 = null;
		modelIntArray2 = null;
		modelIntArray3 = null;
		modelIntArray4 = null;

		workspace.remove();
	}

	public static void cacheModelHeader(byte[] abyte0, int j) {
		try {
			if (abyte0 == null) {
				ModelHeader modelHeader = aModelHeaderArray1661[j] = new ModelHeader();
				modelHeader.vertexCount = 0;
				modelHeader.triangleCount = 0;
				modelHeader.textureTriangleCount = 0;
				return;
			}
			Stream stream = new Stream(abyte0);
			stream.position = abyte0.length - 18;
			ModelHeader modelHeader_1 = aModelHeaderArray1661[j] = new ModelHeader();
			modelHeader_1.modelData = abyte0;
			modelHeader_1.vertexCount = stream.readUnsignedWord();
			modelHeader_1.triangleCount = stream.readUnsignedWord();
			modelHeader_1.textureTriangleCount = stream.readUnsignedByte();
			int k = stream.readUnsignedByte();
			int l = stream.readUnsignedByte();
			int i1 = stream.readUnsignedByte();
			int j1 = stream.readUnsignedByte();
			int k1 = stream.readUnsignedByte();
			int l1 = stream.readUnsignedWord();
			int i2 = stream.readUnsignedWord();
			int j2 = stream.readUnsignedWord();
			int k2 = stream.readUnsignedWord();
			int l2 = 0;
			modelHeader_1.vertexFlagsOffset = l2;
			l2 += modelHeader_1.vertexCount;
			modelHeader_1.triangleTypesOffset = l2;
			l2 += modelHeader_1.triangleCount;
			modelHeader_1.trianglePrioritiesOffset = l2;
			if (l == 255)
				l2 += modelHeader_1.triangleCount;
			else
				modelHeader_1.trianglePrioritiesOffset = -l - 1;
			modelHeader_1.triangleLabelsOffset = l2;
			if (j1 == 1)
				l2 += modelHeader_1.triangleCount;
			else
				modelHeader_1.triangleLabelsOffset = -1;
			modelHeader_1.triangleInfoOffset = l2;
			if (k == 1)
				l2 += modelHeader_1.triangleCount;
			else
				modelHeader_1.triangleInfoOffset = -1;
			modelHeader_1.vertexLabelsOffset = l2;
			if (k1 == 1)
				l2 += modelHeader_1.vertexCount;
			else
				modelHeader_1.vertexLabelsOffset = -1;
			modelHeader_1.triangleAlphaOffset = l2;
			if (i1 == 1)
				l2 += modelHeader_1.triangleCount;
			else
				modelHeader_1.triangleAlphaOffset = -1;
			modelHeader_1.triangleVerticesOffset = l2;
			l2 += k2;
			modelHeader_1.triangleColorsOffset = l2;
			l2 += modelHeader_1.triangleCount * 2;
			modelHeader_1.textureTrianglesOffset = l2;
			l2 += modelHeader_1.textureTriangleCount * 6;
			modelHeader_1.vertexXOffset = l2;
			l2 += l1;
			modelHeader_1.vertexYOffset = l2;
			l2 += i2;
			modelHeader_1.vertexZOffset = l2;
		} catch (Exception _ex) {
			throw new RuntimeException(_ex);
		}
	}

	public static void initializeCache(int i, CacheManagerBase cacheManagerBase) {
		aModelHeaderArray1661 = new ModelHeader[80000];
		newmodel = new boolean[100000];
		aCacheManagerBase_1662 = cacheManagerBase;
	}

	public static void removeFromCache(int j) {
		aModelHeaderArray1661[j] = null;
	}

	public static Model loadModelFromCache(int id) {
		if (aModelHeaderArray1661 == null)
			return null;
		ModelHeader modelHeader = aModelHeaderArray1661[id];
		if (modelHeader == null) {
			aCacheManagerBase_1662.requestModel(id);
			return null;
		} else {
			return new Model(id);
		}
	}

	public static boolean isModelCached(int i) {
		if (aModelHeaderArray1661 == null)
			return false;

		ModelHeader modelHeader = aModelHeaderArray1661[i];
		if (modelHeader == null) {
			aCacheManagerBase_1662.requestModel(i);
			return false;
		} else {
			return true;
		}
	}

	public static int adjustColorBrightness(int i, int j, int k) {
		if (i == 65535)
			return 0;
		if ((k & 2) == 2) {
			if (j < 0)
				j = 0;
			else if (j > 127)
				j = 127;
			j = 127 - j;
			return j;
		}

		j = j * (i & 0x7f) >> 7;
		if (j < 2)
			j = 2;
		else if (j > 126)
			j = 126;
		return (i & 0xff80) + j;
	}

	private void removeColor() {
		if (triangleColors != null) {
			for (int triangle = 0; triangle < triangleCount; triangle++) {
				if (triangle < triangleColors.length) {
					if (triangleColors[triangle] == 37798) {
						triangleVertexA[triangle] = 0;
						triangleVertexB[triangle] = 0;
						triangleVertexC[triangle] = 0;
					}
				}
			}
		}
	}

	public void decodeModel525(byte[] abyte0, int modelID) {
		Stream nc1 = new Stream(abyte0);
		Stream nc2 = new Stream(abyte0);
		Stream nc3 = new Stream(abyte0);
		Stream nc4 = new Stream(abyte0);
		Stream nc5 = new Stream(abyte0);
		Stream nc6 = new Stream(abyte0);
		Stream nc7 = new Stream(abyte0);
		nc1.position = abyte0.length - 23;
		int numVertices = nc1.readUnsignedWord();
		int numTriangles = nc1.readUnsignedWord();
		int numTexTriangles = nc1.readUnsignedByte();
		ModelHeader ModelDef_1 = aModelHeaderArray1661[modelID] = new ModelHeader();
		ModelDef_1.modelData = abyte0;
		ModelDef_1.vertexCount = numVertices;
		ModelDef_1.triangleCount = numTriangles;
		ModelDef_1.textureTriangleCount = numTexTriangles;
		int l1 = nc1.readUnsignedByte();
		boolean bool = (~(0x1 & l1)) == -2;
		int i2 = nc1.readUnsignedByte();
		int j2 = nc1.readUnsignedByte();
		int k2 = nc1.readUnsignedByte();
		int l2 = nc1.readUnsignedByte();
		int i3 = nc1.readUnsignedByte();
		int j3 = nc1.readUnsignedWord();
		int k3 = nc1.readUnsignedWord();
		int l3 = nc1.readUnsignedWord();
		int i4 = nc1.readUnsignedWord();
		int j4 = nc1.readUnsignedWord();
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		byte[] x = null;
		byte[] O = null;
		byte[] J = null;
		byte[] F = null;
		byte[] cb = null;
		byte[] gb = null;
		byte[] lb = null;
		int[] kb = null;
		int[] y = null;
		int[] N = null;
		short[] D = null;
		int[] triangleColours2;
		if (numTexTriangles > 0) {
			O = new byte[numTexTriangles];
			nc1.position = 0;
			for (int j5 = 0; j5 < numTexTriangles; j5++) {
				byte byte0 = O[j5] = nc1.readSignedByte();
				if (byte0 == 0)
					k4++;
				if (byte0 >= 1 && byte0 <= 3)
					l4++;
				if (byte0 == 2)
					i5++;
			}
		}
		int k5 = numTexTriangles;
		int l5 = k5;
		k5 += numVertices;
		int i6 = k5;
		if (l1 == 1)
			k5 += numTriangles;
		int j6 = k5;
		k5 += numTriangles;
		int k6 = k5;
		if (i2 == 255)
			k5 += numTriangles;
		int l6 = k5;
		if (k2 == 1)
			k5 += numTriangles;
		int i7 = k5;
		if (i3 == 1)
			k5 += numVertices;
		int j7 = k5;
		if (j2 == 1)
			k5 += numTriangles;
		int k7 = k5;
		k5 += i4;
		int l7 = k5;
		if (l2 == 1)
			k5 += numTriangles * 2;
		int i8 = k5;
		k5 += j4;
		int j8 = k5;
		k5 += numTriangles * 2;
		int k8 = k5;
		k5 += j3;
		int l8 = k5;
		k5 += k3;
		int i9 = k5;
		k5 += l3;
		int j9 = k5;
		k5 += k4 * 6;
		int k9 = k5;
		k5 += l4 * 6;
		int l9 = k5;
		k5 += l4 * 6;
		int i10 = k5;
		k5 += l4;
		int j10 = k5;
		k5 += l4;
		int k10 = k5;
		int[] vertexX = new int[numVertices];
		int[] vertexY = new int[numVertices];
		int[] vertexZ = new int[numVertices];
		int[] facePoint1 = new int[numTriangles];
		int[] facePoint2 = new int[numTriangles];
		int[] facePoint3 = new int[numTriangles];
		vertexLabels = new int[numVertices];
		triangleInfo = new int[numTriangles];
		trianglePriorities = new int[numTriangles];
		triangleAlpha = new int[numTriangles];
		triangleLabels = new int[numTriangles];
		if (i3 == 1)
			vertexLabels = new int[numVertices];
		if (bool)
			triangleInfo = new int[numTriangles];
		if (i2 == 255)
			trianglePriorities = new int[numTriangles];
		else {
		}
		if (j2 == 1)
			triangleAlpha = new int[numTriangles];
		if (k2 == 1)
			triangleLabels = new int[numTriangles];
		if (l2 == 1)
			D = new short[numTriangles];
		if (l2 == 1 && numTexTriangles > 0)
			x = new byte[numTriangles];
		triangleColours2 = new int[numTriangles];
		int[] texTrianglesPoint1 = null;
		int[] texTrianglesPoint2 = null;
		int[] texTrianglesPoint3 = null;
		if (numTexTriangles > 0) {
			texTrianglesPoint1 = new int[numTexTriangles];
			texTrianglesPoint2 = new int[numTexTriangles];
			texTrianglesPoint3 = new int[numTexTriangles];
			if (l4 > 0) {
				kb = new int[l4];
				N = new int[l4];
				y = new int[l4];
				gb = new byte[l4];
				lb = new byte[l4];
				F = new byte[l4];
			}
			if (i5 > 0) {
				cb = new byte[i5];
				J = new byte[i5];
			}
		}
		nc1.position = l5;
		nc2.position = k8;
		nc3.position = l8;
		nc4.position = i9;
		nc5.position = i7;
		int l10 = 0;
		int i11 = 0;
		int j11 = 0;
		for (int k11 = 0; k11 < numVertices; k11++) {
			int l11 = nc1.readUnsignedByte();
			int j12 = 0;
			if ((l11 & 1) != 0)
				j12 = nc2.readSmartSigned();
			int l12 = 0;
			if ((l11 & 2) != 0)
				l12 = nc3.readSmartSigned();
			int j13 = 0;
			if ((l11 & 4) != 0)
				j13 = nc4.readSmartSigned();
			vertexX[k11] = l10 + j12;
			vertexY[k11] = i11 + l12;
			vertexZ[k11] = j11 + j13;
			l10 = vertexX[k11];
			i11 = vertexY[k11];
			j11 = vertexZ[k11];
			if (vertexLabels != null)
				vertexLabels[k11] = nc5.readUnsignedByte();
		}
		nc1.position = j8;
		nc2.position = i6;
		nc3.position = k6;
		nc4.position = j7;
		nc5.position = l6;
		nc6.position = l7;
		nc7.position = i8;
		for (int i12 = 0; i12 < numTriangles; i12++) {
			triangleColours2[i12] = nc1.readUnsignedWord();
			if (l1 == 1) {
				triangleInfo[i12] = nc2.readSignedByte();
				if (triangleInfo[i12] == 2)
					triangleColours2[i12] = 65535;
				triangleInfo[i12] = 0;
			}
			if (i2 == 255) {
				trianglePriorities[i12] = nc3.readSignedByte();
			}
			if (j2 == 1) {
				triangleAlpha[i12] = nc4.readSignedByte();
				if (triangleAlpha[i12] < 0)
					triangleAlpha[i12] = (256 + triangleAlpha[i12]);
			}
			if (k2 == 1)
				triangleLabels[i12] = nc5.readUnsignedByte();
			if (l2 == 1)
				D[i12] = (short) (nc6.readUnsignedWord() - 1);
			if (x != null)
				if (D[i12] != -1)
					x[i12] = (byte) (nc7.readUnsignedByte() - 1);
				else
					x[i12] = -1;
		}
		nc1.position = k7;
		nc2.position = j6;
		int k12 = 0;
		int i13 = 0;
		int k13 = 0;
		int l13 = 0;
		for (int i14 = 0; i14 < numTriangles; i14++) {
			int j14 = nc2.readUnsignedByte();
			if (j14 == 1) {
				k12 = nc1.readSmartSigned() + l13;
				l13 = k12;
				i13 = nc1.readSmartSigned() + l13;
				l13 = i13;
				k13 = nc1.readSmartSigned() + l13;
				l13 = k13;
				facePoint1[i14] = k12;
				facePoint2[i14] = i13;
				facePoint3[i14] = k13;
			}
			if (j14 == 2) {
				i13 = k13;
				k13 = nc1.readSmartSigned() + l13;
				l13 = k13;
				facePoint1[i14] = k12;
				facePoint2[i14] = i13;
				facePoint3[i14] = k13;
			}
			if (j14 == 3) {
				k12 = k13;
				k13 = nc1.readSmartSigned() + l13;
				l13 = k13;
				facePoint1[i14] = k12;
				facePoint2[i14] = i13;
				facePoint3[i14] = k13;
			}
			if (j14 == 4) {
				int l14 = k12;
				k12 = i13;
				i13 = l14;
				k13 = nc1.readSmartSigned() + l13;
				l13 = k13;
				facePoint1[i14] = k12;
				facePoint2[i14] = i13;
				facePoint3[i14] = k13;
			}
		}
		nc1.position = j9;
		nc2.position = k9;
		nc3.position = l9;
		nc4.position = i10;
		nc5.position = j10;
		nc6.position = k10;
		for (int k14 = 0; k14 < numTexTriangles; k14++) {
			int i15 = O[k14] & 0xff;
			if (i15 == 0) {
				texTrianglesPoint1[k14] = nc1.readUnsignedWord();
				texTrianglesPoint2[k14] = nc1.readUnsignedWord();
				texTrianglesPoint3[k14] = nc1.readUnsignedWord();
			}
			if (i15 == 1) {
				texTrianglesPoint1[k14] = nc2.readUnsignedWord();
				texTrianglesPoint2[k14] = nc2.readUnsignedWord();
				texTrianglesPoint3[k14] = nc2.readUnsignedWord();
				kb[k14] = nc3.readUnsignedWord();
				N[k14] = nc3.readUnsignedWord();
				y[k14] = nc3.readUnsignedWord();
				gb[k14] = nc4.readSignedByte();
				lb[k14] = nc5.readSignedByte();
				F[k14] = nc6.readSignedByte();
			}
			if (i15 == 2) {
				texTrianglesPoint1[k14] = nc2.readUnsignedWord();
				texTrianglesPoint2[k14] = nc2.readUnsignedWord();
				texTrianglesPoint3[k14] = nc2.readUnsignedWord();
				kb[k14] = nc3.readUnsignedWord();
				N[k14] = nc3.readUnsignedWord();
				y[k14] = nc3.readUnsignedWord();
				gb[k14] = nc4.readSignedByte();
				lb[k14] = nc5.readSignedByte();
				F[k14] = nc6.readSignedByte();
				cb[k14] = nc6.readSignedByte();
				J[k14] = nc6.readSignedByte();
			}
			if (i15 == 3) {
				texTrianglesPoint1[k14] = nc2.readUnsignedWord();
				texTrianglesPoint2[k14] = nc2.readUnsignedWord();
				texTrianglesPoint3[k14] = nc2.readUnsignedWord();
				kb[k14] = nc3.readUnsignedWord();
				N[k14] = nc3.readUnsignedWord();
				y[k14] = nc3.readUnsignedWord();
				gb[k14] = nc4.readSignedByte();
				lb[k14] = nc5.readSignedByte();
				F[k14] = nc6.readSignedByte();
			}
		}
		if (i2 != 255) {
			for (int i12 = 0; i12 < numTriangles; i12++)
				trianglePriorities[i12] = i2;
		}
		triangleColors = triangleColours2;
		vertexCount = numVertices;
		triangleCount = numTriangles;
		verticesX = vertexX;
		verticesY = vertexY;
		verticesZ = vertexZ;
		triangleVertexA = facePoint1;
		triangleVertexB = facePoint2;
		triangleVertexC = facePoint3;
	}

	public void scaleDown(int i) {
		for (int i1 = 0; i1 < vertexCount; i1++) {
			verticesX[i1] = verticesX[i1] / i;
			verticesY[i1] = verticesY[i1] / i;
			verticesZ[i1] = verticesZ[i1] / i;
		}
	}

	public void decodeModel622(byte[] abyte0, int modelID) {
		Stream nc1 = new Stream(abyte0);
		Stream nc2 = new Stream(abyte0);
		Stream nc3 = new Stream(abyte0);
		Stream nc4 = new Stream(abyte0);
		Stream nc5 = new Stream(abyte0);
		Stream nc6 = new Stream(abyte0);
		Stream nc7 = new Stream(abyte0);
		nc1.position = abyte0.length - 23;
		int numVertices = nc1.readUnsignedWord();
		int numTriangles = nc1.readUnsignedWord();
		int numTexTriangles = nc1.readUnsignedByte();
		ModelHeader ModelDef_1 = aModelHeaderArray1661[modelID] = new ModelHeader();
		ModelDef_1.modelData = abyte0;
		ModelDef_1.vertexCount = numVertices;
		ModelDef_1.triangleCount = numTriangles;
		ModelDef_1.textureTriangleCount = numTexTriangles;
		int l1 = nc1.readUnsignedByte();
		boolean bool = (~(0x1 & l1)) == -2;
		boolean bool_26_ = (0x8 & l1) == 8;
		if (!bool_26_) {
			decodeModel525(abyte0, modelID);
			return;
		}
		int newformat = 0;
		nc1.position -= 7;
		newformat = nc1.readUnsignedByte();
		nc1.position += 6;
		if (newformat == 15)
			newmodel[modelID] = true;
		int i2 = nc1.readUnsignedByte();
		int j2 = nc1.readUnsignedByte();
		int k2 = nc1.readUnsignedByte();
		int l2 = nc1.readUnsignedByte();
		int i3 = nc1.readUnsignedByte();
		int j3 = nc1.readUnsignedWord();
		int k3 = nc1.readUnsignedWord();
		int l3 = nc1.readUnsignedWord();
		int i4 = nc1.readUnsignedWord();
		int j4 = nc1.readUnsignedWord();
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		byte[] x = null;
		byte[] O = null;
		byte[] J = null;
		byte[] F = null;
		byte[] cb = null;
		byte[] gb = null;
		byte[] lb = null;
		int[] kb = null;
		int[] y = null;
		int[] N = null;
		short[] D = null;
		int[] triangleColours2;
		if (numTexTriangles > 0) {
			O = new byte[numTexTriangles];
			nc1.position = 0;
			for (int j5 = 0; j5 < numTexTriangles; j5++) {
				byte byte0 = O[j5] = nc1.readSignedByte();
				if (byte0 == 0)
					k4++;
				if (byte0 >= 1 && byte0 <= 3)
					l4++;
				if (byte0 == 2)
					i5++;
			}
		}
		int k5 = numTexTriangles;
		int l5 = k5;
		k5 += numVertices;
		int i6 = k5;
		if (bool)
			k5 += numTriangles;
		if (l1 == 1)
			k5 += numTriangles;
		int j6 = k5;
		k5 += numTriangles;
		int k6 = k5;
		if (i2 == 255)
			k5 += numTriangles;
		int l6 = k5;
		if (k2 == 1)
			k5 += numTriangles;
		int i7 = k5;
		if (i3 == 1)
			k5 += numVertices;
		int j7 = k5;
		if (j2 == 1)
			k5 += numTriangles;
		int k7 = k5;
		k5 += i4;
		int l7 = k5;
		if (l2 == 1)
			k5 += numTriangles * 2;
		int i8 = k5;
		k5 += j4;
		int j8 = k5;
		k5 += numTriangles * 2;
		int k8 = k5;
		k5 += j3;
		int l8 = k5;
		k5 += k3;
		int i9 = k5;
		k5 += l3;
		int j9 = k5;
		k5 += k4 * 6;
		int k9 = k5;
		k5 += l4 * 6;
		int i_59_ = 6;
		if (newformat != 14) {
			if (newformat >= 15)
				i_59_ = 9;
		} else
			i_59_ = 7;
		int l9 = k5;
		k5 += i_59_ * l4;
		int i10 = k5;
		k5 += l4;
		int j10 = k5;
		k5 += l4;
		int k10 = k5;
		int[] vertexX = new int[numVertices];
		int[] vertexY = new int[numVertices];
		int[] vertexZ = new int[numVertices];
		int[] facePoint1 = new int[numTriangles];
		int[] facePoint2 = new int[numTriangles];
		int[] facePoint3 = new int[numTriangles];
		vertexLabels = new int[numVertices];
		triangleInfo = new int[numTriangles];
		trianglePriorities = new int[numTriangles];
		triangleAlpha = new int[numTriangles];
		triangleLabels = new int[numTriangles];
		if (i3 == 1)
			vertexLabels = new int[numVertices];
		if (bool)
			triangleInfo = new int[numTriangles];
		if (i2 == 255)
			trianglePriorities = new int[numTriangles];
		else {
		}
		if (j2 == 1)
			triangleAlpha = new int[numTriangles];
		if (k2 == 1)
			triangleLabels = new int[numTriangles];
		if (l2 == 1)
			D = new short[numTriangles];
		if (l2 == 1 && numTexTriangles > 0)
			x = new byte[numTriangles];
		triangleColours2 = new int[numTriangles];
		int[] texTrianglesPoint1 = null;
		int[] texTrianglesPoint2 = null;
		int[] texTrianglesPoint3 = null;
		if (numTexTriangles > 0) {
			texTrianglesPoint1 = new int[numTexTriangles];
			texTrianglesPoint2 = new int[numTexTriangles];
			texTrianglesPoint3 = new int[numTexTriangles];
			if (l4 > 0) {
				kb = new int[l4];
				N = new int[l4];
				y = new int[l4];
				gb = new byte[l4];
				lb = new byte[l4];
				F = new byte[l4];
			}
			if (i5 > 0) {
				cb = new byte[i5];
				J = new byte[i5];
			}
		}
		nc1.position = l5;
		nc2.position = k8;
		nc3.position = l8;
		nc4.position = i9;
		nc5.position = i7;
		int l10 = 0;
		int i11 = 0;
		int j11 = 0;
		for (int k11 = 0; k11 < numVertices; k11++) {
			int l11 = nc1.readUnsignedByte();
			int j12 = 0;
			if ((l11 & 1) != 0)
				j12 = nc2.readSmartSigned();
			int l12 = 0;
			if ((l11 & 2) != 0)
				l12 = nc3.readSmartSigned();
			int j13 = 0;
			if ((l11 & 4) != 0)
				j13 = nc4.readSmartSigned();
			vertexX[k11] = l10 + j12;
			vertexY[k11] = i11 + l12;
			vertexZ[k11] = j11 + j13;
			l10 = vertexX[k11];
			i11 = vertexY[k11];
			j11 = vertexZ[k11];
			if (vertexLabels != null)
				vertexLabels[k11] = nc5.readUnsignedByte();
		}
		nc1.position = j8;
		nc2.position = i6;
		nc3.position = k6;
		nc4.position = j7;
		nc5.position = l6;
		nc6.position = l7;
		nc7.position = i8;
		for (int i12 = 0; i12 < numTriangles; i12++) {
			triangleColours2[i12] = nc1.readUnsignedWord();
			if (l1 == 1) {
				triangleInfo[i12] = nc2.readSignedByte();
				if (triangleInfo[i12] == 2)
					triangleColours2[i12] = 65535;
				triangleInfo[i12] = 0;
			}
			if (i2 == 255) {
				trianglePriorities[i12] = nc3.readSignedByte();
			}
			if (j2 == 1) {
				triangleAlpha[i12] = nc4.readSignedByte();
				if (triangleAlpha[i12] < 0)
					triangleAlpha[i12] = (256 + triangleAlpha[i12]);
			}
			if (k2 == 1)
				triangleLabels[i12] = nc5.readUnsignedByte();
			if (l2 == 1)
				D[i12] = (short) (nc6.readUnsignedWord() - 1);
			if (x != null)
				if (D[i12] != -1)
					x[i12] = (byte) (nc7.readUnsignedByte() - 1);
				else
					x[i12] = -1;
		}
		nc1.position = k7;
		nc2.position = j6;
		int k12 = 0;
		int i13 = 0;
		int k13 = 0;
		int l13 = 0;
		for (int i14 = 0; i14 < numTriangles; i14++) {
			int j14 = nc2.readUnsignedByte();
			if (j14 == 1) {
				k12 = nc1.readSmartSigned() + l13;
				l13 = k12;
				i13 = nc1.readSmartSigned() + l13;
				l13 = i13;
				k13 = nc1.readSmartSigned() + l13;
				l13 = k13;
				facePoint1[i14] = k12;
				facePoint2[i14] = i13;
				facePoint3[i14] = k13;
			}
			if (j14 == 2) {
				i13 = k13;
				k13 = nc1.readSmartSigned() + l13;
				l13 = k13;
				facePoint1[i14] = k12;
				facePoint2[i14] = i13;
				facePoint3[i14] = k13;
			}
			if (j14 == 3) {
				k12 = k13;
				k13 = nc1.readSmartSigned() + l13;
				l13 = k13;
				facePoint1[i14] = k12;
				facePoint2[i14] = i13;
				facePoint3[i14] = k13;
			}
			if (j14 == 4) {
				int l14 = k12;
				k12 = i13;
				i13 = l14;
				k13 = nc1.readSmartSigned() + l13;
				l13 = k13;
				facePoint1[i14] = k12;
				facePoint2[i14] = i13;
				facePoint3[i14] = k13;
			}
		}
		nc1.position = j9;
		nc2.position = k9;
		nc3.position = l9;
		nc4.position = i10;
		nc5.position = j10;
		nc6.position = k10;
		for (int k14 = 0; k14 < numTexTriangles; k14++) {
			int i15 = O[k14] & 0xff;
			if (i15 == 0) {
				texTrianglesPoint1[k14] = nc1.readUnsignedWord();
				texTrianglesPoint2[k14] = nc1.readUnsignedWord();
				texTrianglesPoint3[k14] = nc1.readUnsignedWord();
			}
			if (i15 == 1) {
				texTrianglesPoint1[k14] = nc2.readUnsignedWord();
				texTrianglesPoint2[k14] = nc2.readUnsignedWord();
				texTrianglesPoint3[k14] = nc2.readUnsignedWord();
				if (newformat < 15) {
					Objects.requireNonNull(kb)[k14] = nc3.readUnsignedWord();
					if (newformat == 14)
						Objects.requireNonNull(N)[k14] = nc3.read3BytesBE(-1);
					else
						Objects.requireNonNull(N)[k14] = nc3.readUnsignedWord();
					Objects.requireNonNull(y)[k14] = nc3.readUnsignedWord();
				} else {
					Objects.requireNonNull(kb)[k14] = nc3.read3BytesBE(-1);
					Objects.requireNonNull(N)[k14] = nc3.read3BytesBE(-1);
					Objects.requireNonNull(y)[k14] = nc3.read3BytesBE(-1);
				}
				Objects.requireNonNull(gb)[k14] = nc4.readSignedByte();
				Objects.requireNonNull(lb)[k14] = nc5.readSignedByte();
				Objects.requireNonNull(F)[k14] = nc6.readSignedByte();
			}
			if (i15 == 2) {
				texTrianglesPoint1[k14] = nc2.readUnsignedWord();
				texTrianglesPoint2[k14] = nc2.readUnsignedWord();
				texTrianglesPoint3[k14] = nc2.readUnsignedWord();
				if (newformat >= 15) {
					Objects.requireNonNull(kb)[k14] = nc3.read3BytesBE(-1);
					Objects.requireNonNull(N)[k14] = nc3.read3BytesBE(-1);
					Objects.requireNonNull(y)[k14] = nc3.read3BytesBE(-1);
				} else {
					Objects.requireNonNull(kb)[k14] = nc3.readUnsignedWord();
					if (newformat < 14)
						Objects.requireNonNull(N)[k14] = nc3.readUnsignedWord();
					else
						Objects.requireNonNull(N)[k14] = nc3.read3BytesBE(-1);
					Objects.requireNonNull(y)[k14] = nc3.readUnsignedWord();
				}
				Objects.requireNonNull(gb)[k14] = nc4.readSignedByte();
				Objects.requireNonNull(lb)[k14] = nc5.readSignedByte();
				Objects.requireNonNull(F)[k14] = nc6.readSignedByte();
				Objects.requireNonNull(cb)[k14] = nc6.readSignedByte();
				Objects.requireNonNull(J)[k14] = nc6.readSignedByte();
			}
			if (i15 == 3) {
				texTrianglesPoint1[k14] = nc2.readUnsignedWord();
				texTrianglesPoint2[k14] = nc2.readUnsignedWord();
				texTrianglesPoint3[k14] = nc2.readUnsignedWord();
				if (newformat < 15) {
					Objects.requireNonNull(kb)[k14] = nc3.readUnsignedWord();
					if (newformat < 14)
						Objects.requireNonNull(N)[k14] = nc3.readUnsignedWord();
					else
						Objects.requireNonNull(N)[k14] = nc3.read3BytesBE(-1);
					Objects.requireNonNull(y)[k14] = nc3.readUnsignedWord();
				} else {
					Objects.requireNonNull(kb)[k14] = nc3.read3BytesBE(-1);
					Objects.requireNonNull(N)[k14] = nc3.read3BytesBE(-1);
					Objects.requireNonNull(y)[k14] = nc3.read3BytesBE(-1);
				}
				Objects.requireNonNull(gb)[k14] = nc4.readSignedByte();
				Objects.requireNonNull(lb)[k14] = nc5.readSignedByte();
				Objects.requireNonNull(F)[k14] = nc6.readSignedByte();
			}
		}
		if (i2 != 255) {
			for (int i12 = 0; i12 < numTriangles; i12++)
				trianglePriorities[i12] = i2;
		}
		triangleColors = triangleColours2;
		vertexCount = numVertices;
		triangleCount = numTriangles;
		verticesX = vertexX;
		verticesY = vertexY;
		verticesZ = vertexZ;
		triangleVertexA = facePoint1;
		triangleVertexB = facePoint2;
		triangleVertexC = facePoint3;
		scaleDown(4);
	}

	private void decodeOldModel(int i) {
		int j = -870;
		aBoolean1618 = true;
		aBoolean1659 = false;
		anInt1620++;
		ModelHeader modelHeader = aModelHeaderArray1661[i];
		vertexCount = modelHeader.vertexCount;
		triangleCount = modelHeader.triangleCount;
		textureTriangleCount = modelHeader.textureTriangleCount;
		verticesX = new int[vertexCount];
		verticesY = new int[vertexCount];
		verticesZ = new int[vertexCount];
		triangleVertexA = new int[triangleCount];
		triangleVertexB = new int[triangleCount];
		triangleVertexC = new int[triangleCount];
		textureTriangleA = new int[textureTriangleCount];
		textureTriangleB = new int[textureTriangleCount];
		textureTriangleC = new int[textureTriangleCount];
		if (modelHeader.vertexLabelsOffset >= 0)
			vertexLabels = new int[vertexCount];
		if (modelHeader.triangleInfoOffset >= 0)
			triangleInfo = new int[triangleCount];
		if (modelHeader.trianglePrioritiesOffset >= 0)
			trianglePriorities = new int[triangleCount];
		else
			anInt1641 = -modelHeader.trianglePrioritiesOffset - 1;
		if (modelHeader.triangleAlphaOffset >= 0)
			triangleAlpha = new int[triangleCount];
		if (modelHeader.triangleLabelsOffset >= 0)
			triangleLabels = new int[triangleCount];
		triangleColors = new int[triangleCount];
		Stream stream = new Stream(modelHeader.modelData);
		stream.position = modelHeader.vertexFlagsOffset;
		Stream stream_1 = new Stream(modelHeader.modelData);
		stream_1.position = modelHeader.vertexXOffset;
		Stream stream_2 = new Stream(modelHeader.modelData);
		stream_2.position = modelHeader.vertexYOffset;
		Stream stream_3 = new Stream(modelHeader.modelData);
		stream_3.position = modelHeader.vertexZOffset;
		Stream stream_4 = new Stream(modelHeader.modelData);
		stream_4.position = modelHeader.vertexLabelsOffset;
		int k = 0;
		int l = 0;
		int i1 = 0;
		for (int j1 = 0; j1 < vertexCount; j1++) {
			int k1 = stream.readUnsignedByte();
			int i2 = 0;
			if ((k1 & 1) != 0)
				i2 = stream_1.readSmartSigned();
			int k2 = 0;
			if ((k1 & 2) != 0)
				k2 = stream_2.readSmartSigned();
			int i3 = 0;
			if ((k1 & 4) != 0)
				i3 = stream_3.readSmartSigned();
			verticesX[j1] = k + i2;
			verticesY[j1] = l + k2;
			verticesZ[j1] = i1 + i3;
			k = verticesX[j1];
			l = verticesY[j1];
			i1 = verticesZ[j1];
			if (vertexLabels != null)
				vertexLabels[j1] = stream_4.readUnsignedByte();
		}
		stream.position = modelHeader.triangleColorsOffset;
		stream_1.position = modelHeader.triangleInfoOffset;
		stream_2.position = modelHeader.trianglePrioritiesOffset;
		stream_3.position = modelHeader.triangleAlphaOffset;
		stream_4.position = modelHeader.triangleLabelsOffset;
		for (int l1 = 0; l1 < triangleCount; l1++) {
			triangleColors[l1] = stream.readUnsignedWord();
			if (triangleInfo != null)
				triangleInfo[l1] = stream_1.readUnsignedByte();
			if (trianglePriorities != null)
				trianglePriorities[l1] = stream_2.readUnsignedByte();
			if (triangleAlpha != null) {
				triangleAlpha[l1] = stream_3.readUnsignedByte();
			}
			if (triangleLabels != null)
				triangleLabels[l1] = stream_4.readUnsignedByte();
		}
		stream.position = modelHeader.triangleVerticesOffset;
		stream_1.position = modelHeader.triangleTypesOffset;
		int j2 = 0;
		int l2 = 0;
		int j3 = 0;
		int k3 = 0;
		for (int l3 = 0; l3 < triangleCount; l3++) {
			int i4 = stream_1.readUnsignedByte();
			if (i4 == 1) {
				j2 = stream.readSmartSigned() + k3;
				k3 = j2;
				l2 = stream.readSmartSigned() + k3;
				k3 = l2;
				j3 = stream.readSmartSigned() + k3;
				k3 = j3;
				triangleVertexA[l3] = j2;
				triangleVertexB[l3] = l2;
				triangleVertexC[l3] = j3;
			}
			if (i4 == 2) {
				l2 = j3;
				j3 = stream.readSmartSigned() + k3;
				k3 = j3;
				triangleVertexA[l3] = j2;
				triangleVertexB[l3] = l2;
				triangleVertexC[l3] = j3;
			}
			if (i4 == 3) {
				j2 = j3;
				j3 = stream.readSmartSigned() + k3;
				k3 = j3;
				triangleVertexA[l3] = j2;
				triangleVertexB[l3] = l2;
				triangleVertexC[l3] = j3;
			}
			if (i4 == 4) {
				int k4 = j2;
				j2 = l2;
				l2 = k4;
				j3 = stream.readSmartSigned() + k3;
				k3 = j3;
				triangleVertexA[l3] = j2;
				triangleVertexB[l3] = l2;
				triangleVertexC[l3] = j3;
			}
		}
		stream.position = modelHeader.textureTrianglesOffset;
		for (int j4 = 0; j4 < textureTriangleCount; j4++) {
			textureTriangleA[j4] = stream.readUnsignedWord();
			textureTriangleB[j4] = stream.readUnsignedWord();
			textureTriangleC[j4] = stream.readUnsignedWord();
		}
	}

	public void copyModel(Model model, boolean flag) {
		WorkSpace ws = workspace.get();
		ws.ensureCapacity(model.vertexCount, model.triangleCount);

		vertexCount = model.vertexCount;
		triangleCount = model.triangleCount;
		textureTriangleCount = model.textureTriangleCount;

		verticesX = ws.tempVertexBuffer;
		verticesY = new int[vertexCount];
		verticesZ = new int[vertexCount];

		for (int k = 0; k < vertexCount; k++) {
			verticesX[k] = model.verticesX[k];
			verticesY[k] = model.verticesY[k];
			verticesZ[k] = model.verticesZ[k];
		}

		if (flag) {
			triangleAlpha = model.triangleAlpha;
		} else {
			triangleAlpha = ws.tempAlphaBuffer;
			if (model.triangleAlpha == null) {
				java.util.Arrays.fill(triangleAlpha, 0, triangleCount, 0);
			} else {
				System.arraycopy(model.triangleAlpha, 0, triangleAlpha, 0, triangleCount);
			}
		}
		triangleInfo = model.triangleInfo;
		triangleColors = model.triangleColors;
		trianglePriorities = model.trianglePriorities;
		anInt1641 = model.anInt1641;
		anIntArrayArray1658 = model.anIntArrayArray1658;
		anIntArrayArray1657 = model.anIntArrayArray1657;
		triangleVertexA = model.triangleVertexA;
		triangleVertexB = model.triangleVertexB;
		triangleVertexC = model.triangleVertexC;
		triangleColorA = model.triangleColorA;
		triangleColorB = model.triangleColorB;
		triangleColorC = model.triangleColorC;
		textureTriangleA = model.textureTriangleA;
		textureTriangleB = model.textureTriangleB;
		textureTriangleC = model.textureTriangleC;
	}

	private int mergeVertex(Model model, int i) {
		int j = -1;
		int k = model.verticesX[i];
		int l = model.verticesY[i];
		int i1 = model.verticesZ[i];
		for (int j1 = 0; j1 < vertexCount; j1++) {
			if (k != verticesX[j1] || l != verticesY[j1] || i1 != verticesZ[j1])
				continue;
			j = j1;
			break;
		}

		if (j == -1) {
			verticesX[vertexCount] = k;
			verticesY[vertexCount] = l;
			verticesZ[vertexCount] = i1;
			if (model.vertexLabels != null)
				vertexLabels[vertexCount] = model.vertexLabels[i];
			j = vertexCount++;
		}
		return j;
	}

	public void calculateBounds() {
		super.modelHeight = 0;
		diameter = 0;
		maxY = 0;
		for (int i = 0; i < vertexCount; i++) {
			int j = verticesX[i];
			int k = verticesY[i];
			int l = verticesZ[i];
			if (-k > super.modelHeight)
				super.modelHeight = -k;
			if (k > maxY)
				maxY = k;
			int i1 = j * j + l * l;
			if (i1 > diameter)
				diameter = i1;
		}
		diameter = (int) (Math.sqrt(diameter) + 0.98999999999999999D);
		boundingCylinderRadius = (int) (Math.sqrt(diameter * diameter + super.modelHeight * super.modelHeight) + 0.98999999999999999D);
		boundingRadius = boundingCylinderRadius + (int) (Math.sqrt(diameter * diameter + maxY * maxY) + 0.98999999999999999D);
	}

	public void calculateVerticalBounds() {
		super.modelHeight = 0;
		maxY = 0;
		for (int i = 0; i < vertexCount; i++) {
			int j = verticesY[i];
			if (-j > super.modelHeight)
				super.modelHeight = -j;
			if (j > maxY)
				maxY = j;
		}

		boundingCylinderRadius = (int) (Math.sqrt(diameter * diameter + super.modelHeight * super.modelHeight) + 0.98999999999999999D);
		boundingRadius = boundingCylinderRadius + (int) (Math.sqrt(diameter * diameter + maxY * maxY) + 0.98999999999999999D);
	}

	public void calculateBoundingBox(int i) {
		super.modelHeight = 0;
		diameter = 0;
		maxY = 0;
		anInt1646 = 0xf423f;
		anInt1647 = 0xfff0bdc1;
		anInt1648 = 0xfffe7961;
		anInt1649 = 0x1869f;
		for (int j = 0; j < vertexCount; j++) {
			int k = verticesX[j];
			int l = verticesY[j];
			int i1 = verticesZ[j];
			if (k < anInt1646)
				anInt1646 = k;
			if (k > anInt1647)
				anInt1647 = k;
			if (i1 < anInt1649)
				anInt1649 = i1;
			if (i1 > anInt1648)
				anInt1648 = i1;
			if (-l > super.modelHeight)
				super.modelHeight = -l;
			if (l > maxY)
				maxY = l;
			int j1 = k * k + i1 * i1;
			if (j1 > diameter)
				diameter = j1;
		}

		diameter = (int) Math.sqrt(diameter);
		boundingCylinderRadius = (int) Math.sqrt(diameter * diameter + super.modelHeight * super.modelHeight);
		if (i != 21073) {
		} else {
			boundingRadius = boundingCylinderRadius + (int) Math.sqrt(diameter * diameter + maxY * maxY);
		}
	}

	public void calculateNormals() {
		if (vertexLabels != null) {
			int[] ai = new int[256];
			int j = 0;
			for (int l = 0; l < vertexCount; l++) {
				int j1 = vertexLabels[l];
				ai[j1]++;
				if (j1 > j)
					j = j1;
			}

			anIntArrayArray1657 = new int[j + 1][];
			for (int k1 = 0; k1 <= j; k1++) {
				anIntArrayArray1657[k1] = new int[ai[k1]];
				ai[k1] = 0;
			}

			for (int j2 = 0; j2 < vertexCount; j2++) {
				int l2 = vertexLabels[j2];
				anIntArrayArray1657[l2][ai[l2]++] = j2;
			}

			vertexLabels = null;
		}
		if (triangleLabels != null) {
			int[] ai1 = new int[256];
			int k = 0;
			for (int i1 = 0; i1 < triangleCount; i1++) {
				int l1 = triangleLabels[i1];
				ai1[l1]++;
				if (l1 > k)
					k = l1;
			}

			anIntArrayArray1658 = new int[k + 1][];
			for (int i2 = 0; i2 <= k; i2++) {
				anIntArrayArray1658[i2] = new int[ai1[i2]];
				ai1[i2] = 0;
			}

			for (int k2 = 0; k2 < triangleCount; k2++) {
				int i3 = triangleLabels[k2];
				anIntArrayArray1658[i3][ai1[i3]++] = k2;
			}

			triangleLabels = null;
		}
	}

	public void interpolateFrames(int frame, int nextFrame, int cycle, int length) {
		if (anIntArrayArray1657 == null || frame == -1) {
			return;
		}

		final SequenceFrame currAnim = SequenceFrame.getFrame(frame);
		if (currAnim == null) {
			return;
		}

		final SkinList skinList = currAnim.skinList;
		if (skinList == null) {
			return;
		}

		anInt1681 = 0;
		anInt1682 = 0;
		anInt1683 = 0;

		final SequenceFrame nextAnim = getValidNextFrame(nextFrame, skinList);

		if (nextAnim == null) {
			processCurrentFrameOnly(currAnim, skinList);
		} else {
			processInterpolatedFrames(currAnim, nextAnim, skinList, cycle, length);
		}
	}

	private SequenceFrame getValidNextFrame(int nextFrame, SkinList skinList) {
		if (nextFrame == -1) {
			return null;
		}

		final SequenceFrame nextAnim = SequenceFrame.getFrame(nextFrame);
		if (nextAnim == null || nextAnim.skinList != skinList) {
			return null;
		}

		return nextAnim;
	}

	private void processCurrentFrameOnly(SequenceFrame currAnim, SkinList skinList) {
		final int frameCount = currAnim.transformCount;
		final int[] frameIndices = currAnim.transformIndices;
		final int[] frameX = currAnim.xTransforms;
		final int[] frameY = currAnim.yTransforms;
		final int[] frameZ = currAnim.zTransforms;
		final int[] opcodes = skinList.transformTypes;
		final int[][] transformations = skinList.childIndices;

		for (int i = 0; i < frameCount; i++) {
			final int animIndex = frameIndices[i];
			transformVertices(opcodes[animIndex], transformations[animIndex], frameX[i], frameY[i], frameZ[i]);
		}
	}

	private void processInterpolatedFrames(SequenceFrame currAnim, SequenceFrame nextAnim, SkinList skinList, int cycle, int length) {
		final int skinLength = skinList.length;
		final int[] opcodes = skinList.transformTypes;
		final int[][] transformations = skinList.childIndices;

		final int currFrameCount = currAnim.transformCount;
		final int[] currIndices = currAnim.transformIndices;
		final int[] currX = currAnim.xTransforms;
		final int[] currY = currAnim.yTransforms;
		final int[] currZ = currAnim.zTransforms;

		final int nextFrameCount = nextAnim.transformCount;
		final int[] nextIndices = nextAnim.transformIndices;
		final int[] nextX = nextAnim.xTransforms;
		final int[] nextY = nextAnim.yTransforms;
		final int[] nextZ = nextAnim.zTransforms;

		final float interpolationRatio = (float) cycle / length;
		final float invRatio = 1.0f - interpolationRatio;

		int currFrameId = 0;
		int nextFrameId = 0;

		for (int index = 0; index < skinLength; index++) {
			final boolean hasCurrFrame = currFrameId < currFrameCount && currIndices[currFrameId] == index;
			final boolean hasNextFrame = nextFrameId < nextFrameCount && nextIndices[nextFrameId] == index;

			if (!hasCurrFrame && !hasNextFrame) {
				continue;
			}

			final int opcode = opcodes[index];
			final int defaultModifier = (opcode == 3) ? 128 : 0;

			final int currAnimX, currAnimY, currAnimZ;
			if (hasCurrFrame) {
				currAnimX = currX[currFrameId];
				currAnimY = currY[currFrameId];
				currAnimZ = currZ[currFrameId];
				currFrameId++;
			} else {
				currAnimX = defaultModifier;
				currAnimY = defaultModifier;
				currAnimZ = defaultModifier;
			}

			final int nextAnimX, nextAnimY, nextAnimZ;
			if (hasNextFrame) {
				nextAnimX = nextX[nextFrameId];
				nextAnimY = nextY[nextFrameId];
				nextAnimZ = nextZ[nextFrameId];
				nextFrameId++;
			} else {
				nextAnimX = defaultModifier;
				nextAnimY = defaultModifier;
				nextAnimZ = defaultModifier;
			}

			final int interpolatedX, interpolatedY, interpolatedZ;
			if (opcode == 2) {
				interpolatedX = interpolateRotation(currAnimX, nextAnimX, interpolationRatio);
				interpolatedY = interpolateRotation(currAnimY, nextAnimY, interpolationRatio);
				interpolatedZ = interpolateRotation(currAnimZ, nextAnimZ, interpolationRatio);
			} else {
				interpolatedX = Math.round(currAnimX * invRatio + nextAnimX * interpolationRatio);
				interpolatedY = Math.round(currAnimY * invRatio + nextAnimY * interpolationRatio);
				interpolatedZ = Math.round(currAnimZ * invRatio + nextAnimZ * interpolationRatio);
			}

			transformVertices(opcode, transformations[index], interpolatedX, interpolatedY, interpolatedZ);
		}
	}

	private int interpolateRotation(int current, int next, float ratio) {
		int delta = (next - current) & 0xFF;
		if (delta >= 128) {
			delta -= 256;
		}
		return (current + Math.round(delta * ratio)) & 0xFF;
	}

	public void applyTransformation(int i) {
		if (anIntArrayArray1657 == null)
			return;
		if (i == -1)
			return;
		SequenceFrame class36 = SequenceFrame.getFrame(i);
		if (class36 == null)
			return;
		SkinList skinList = class36.skinList;
		anInt1681 = 0;
		anInt1682 = 0;
		anInt1683 = 0;
		for (int k = 0; k < class36.transformCount; k++) {
			int l = class36.transformIndices[k];
			transformVertices(skinList.transformTypes[l], skinList.childIndices[l], class36.xTransforms[k], class36.yTransforms[k], class36.zTransforms[k]);
		}
	}

	public void applyMixedTransformation(int[] ai, int j, int k) {
		if (k == -1)
			return;
		if (ai == null || j == -1) {
			applyTransformation(k);
			return;
		}
		SequenceFrame class36 = SequenceFrame.getFrame(k);
		if (class36 == null)
			return;
		SequenceFrame class36_1 = SequenceFrame.getFrame(j);
		if (class36_1 == null) {
			applyTransformation(k);
			return;
		}
		SkinList skinList = class36.skinList;
		anInt1681 = 0;
		anInt1682 = 0;
		anInt1683 = 0;
		int l = 0;
		int i1 = ai[l++];
		for (int j1 = 0; j1 < class36.transformCount; j1++) {
			int k1;
			for (k1 = class36.transformIndices[j1]; k1 > i1; i1 = ai[l++])
				;
			if (k1 != i1 || skinList.transformTypes[k1] == 0)
				transformVertices(skinList.transformTypes[k1], skinList.childIndices[k1], class36.xTransforms[j1], class36.yTransforms[j1], class36.zTransforms[j1]);
		}

		anInt1681 = 0;
		anInt1682 = 0;
		anInt1683 = 0;
		l = 0;
		i1 = ai[l++];
		for (int l1 = 0; l1 < class36_1.transformCount; l1++) {
			int i2;
			for (i2 = class36_1.transformIndices[l1]; i2 > i1; i1 = ai[l++])
				;
			if (i2 == i1 || skinList.transformTypes[i2] == 0)
				transformVertices(skinList.transformTypes[i2], skinList.childIndices[i2], class36_1.xTransforms[l1], class36_1.yTransforms[l1], class36_1.zTransforms[l1]);
		}
	}

	private void transformVertices(int i, int[] ai, int j, int k, int l) {
		int i1 = ai.length;
		if (i == 0) {
			int j1 = 0;
			anInt1681 = 0;
			anInt1682 = 0;
			anInt1683 = 0;
			for (int l3 : ai) {
				if (l3 < anIntArrayArray1657.length) {
					int[] ai5 = anIntArrayArray1657[l3];
					for (int j6 : ai5) {
						anInt1681 += verticesX[j6];
						anInt1682 += verticesY[j6];
						anInt1683 += verticesZ[j6];
						j1++;
					}
				}
			}

			if (j1 > 0) {
				anInt1681 = anInt1681 / j1 + j;
				anInt1682 = anInt1682 / j1 + k;
				anInt1683 = anInt1683 / j1 + l;
				return;
			} else {
				anInt1681 = j;
				anInt1682 = k;
				anInt1683 = l;
				return;
			}
		}
		if (i == 1) {
			for (int l2 : ai) {
				if (l2 < anIntArrayArray1657.length) {
					int[] ai1 = anIntArrayArray1657[l2];
					for (int j5 : ai1) {
						verticesX[j5] += j;
						verticesY[j5] += k;
						verticesZ[j5] += l;
					}
				}
			}
			return;
		}
		if (i == 2) {
			for (int i3 : ai) {
				if (i3 < anIntArrayArray1657.length) {
					int[] ai2 = anIntArrayArray1657[i3];
					for (int k5 : ai2) {
						verticesX[k5] -= anInt1681;
						verticesY[k5] -= anInt1682;
						verticesZ[k5] -= anInt1683;
						int k6 = (j & 0xff) * 8;
						int l6 = (k & 0xff) * 8;
						int i7 = (l & 0xff) * 8;
						if (i7 != 0) {
							int j7 = modelIntArray1[i7];
							int i8 = modelIntArray2[i7];
							int l8 = verticesY[k5] * j7 + verticesX[k5] * i8 >> 16;
							verticesY[k5] = verticesY[k5] * i8 - verticesX[k5] * j7 >> 16;
							verticesX[k5] = l8;
						}
						if (k6 != 0) {
							int k7 = modelIntArray1[k6];
							int j8 = modelIntArray2[k6];
							int i9 = verticesY[k5] * j8 - verticesZ[k5] * k7 >> 16;
							verticesZ[k5] = verticesY[k5] * k7 + verticesZ[k5] * j8 >> 16;
							verticesY[k5] = i9;
						}
						if (l6 != 0) {
							int l7 = modelIntArray1[l6];
							int k8 = modelIntArray2[l6];
							int j9 = verticesZ[k5] * l7 + verticesX[k5] * k8 >> 16;
							verticesZ[k5] = verticesZ[k5] * k8 - verticesX[k5] * l7 >> 16;
							verticesX[k5] = j9;
						}
						verticesX[k5] += anInt1681;
						verticesY[k5] += anInt1682;
						verticesZ[k5] += anInt1683;
					}
				}
			}
			return;
		}
		if (i == 3) {
			for (int j3 : ai) {
				if (j3 < anIntArrayArray1657.length) {
					int[] ai3 = anIntArrayArray1657[j3];
					for (int l5 : ai3) {
						verticesX[l5] -= anInt1681;
						verticesY[l5] -= anInt1682;
						verticesZ[l5] -= anInt1683;
						verticesX[l5] = (verticesX[l5] * j) / 128;
						verticesY[l5] = (verticesY[l5] * k) / 128;
						verticesZ[l5] = (verticesZ[l5] * l) / 128;
						verticesX[l5] += anInt1681;
						verticesY[l5] += anInt1682;
						verticesZ[l5] += anInt1683;
					}
				}
			}
			return;
		}
		if (i == 5 && anIntArrayArray1658 != null && triangleAlpha != null) {
			for (int k3 : ai) {
				if (k3 < anIntArrayArray1658.length) {
					int[] ai4 = anIntArrayArray1658[k3];
					for (int i6 : ai4) {
						triangleAlpha[i6] += j * 8;
						if (triangleAlpha[i6] < 0)
							triangleAlpha[i6] = 0;
						if (triangleAlpha[i6] > 255)
							triangleAlpha[i6] = 255;
					}
				}
			}
		}
	}

	public void rotateY180() {
		for (int j = 0; j < vertexCount; j++) {
			int k = verticesX[j];
			verticesX[j] = verticesZ[j];
			verticesZ[j] = -k;
		}
	}

	public void rotateX(int i) {
		int k = modelIntArray1[i];
		int l = modelIntArray2[i];
		for (int i1 = 0; i1 < vertexCount; i1++) {
			int j1 = verticesY[i1] * l - verticesZ[i1] * k >> 16;
			verticesZ[i1] = verticesY[i1] * k + verticesZ[i1] * l >> 16;
			verticesY[i1] = j1;
		}
	}

	public void translateCoords(int x, int y, int z) {
		for (int i1 = 0; i1 < vertexCount; i1++) {
			verticesX[i1] += x;
			verticesY[i1] += y;
			verticesZ[i1] += z;
		}
	}

	public void replaceColor(int og, int mod) {
		for (int k = 0; k < triangleCount; k++)
			if (triangleColors[k] == og)
				triangleColors[k] = mod;
	}

	public void mirrorZ() {
		for (int j = 0; j < vertexCount; j++)
			verticesZ[j] = -verticesZ[j];
		for (int k = 0; k < triangleCount; k++) {
			int l = triangleVertexA[k];
			triangleVertexA[k] = triangleVertexC[k];
			triangleVertexC[k] = l;
		}
	}

	public void modelScale(int x, int y, int z) {
		for (int i1 = 0; i1 < vertexCount; i1++) {
			verticesX[i1] = (verticesX[i1] * x) / 128;
			verticesY[i1] = (verticesY[i1] * y) / 128;
			verticesZ[i1] = (verticesZ[i1] * z) / 128;
		}
	}

	public final void applyLighting(int i, int j, int k, int l, int i1, boolean flag) {
		int j1 = (int) Math.sqrt(k * k + l * l + i1 * i1);
		int k1 = j * j1 >> 8;
		if (triangleColorA == null) {
			triangleColorA = new int[triangleCount];
			triangleColorB = new int[triangleCount];
			triangleColorC = new int[triangleCount];
		}
		if (super.vertices == null) {
			super.vertices = new Point3D[vertexCount];
			for (int l1 = 0; l1 < vertexCount; l1++)
				super.vertices[l1] = new Point3D();
		}
		for (int triangle = 0; triangle < triangleCount; triangle++) {
			removeColor();
		}
		for (int i2 = 0; i2 < triangleCount; i2++) {
			if (triangleColors != null && triangleAlpha != null)
				if (triangleColors[i2] == 65535 || triangleColors[i2] == 16705)
					triangleAlpha[i2] = 255;
			int j2 = triangleVertexA[i2];
			int l2 = triangleVertexB[i2];
			int i3 = triangleVertexC[i2];
			int j3 = verticesX[l2] - verticesX[j2];
			int k3 = verticesY[l2] - verticesY[j2];
			int l3 = verticesZ[l2] - verticesZ[j2];
			int i4 = verticesX[i3] - verticesX[j2];
			int j4 = verticesY[i3] - verticesY[j2];
			int k4 = verticesZ[i3] - verticesZ[j2];
			int l4 = k3 * k4 - j4 * l3;
			int i5 = l3 * i4 - k4 * j3;
			int j5;
			for (j5 = j3 * j4 - i4 * k3; l4 > 8192 || i5 > 8192 || j5 > 8192 || l4 < -8192 || i5 < -8192 || j5 < -8192; j5 >>= 1) {
				l4 >>= 1;
				i5 >>= 1;
			}

			int k5 = (int) Math.sqrt(l4 * l4 + i5 * i5 + j5 * j5);
			if (k5 == 0)
				k5 = 1;
			l4 = (l4 * 256) / k5;
			i5 = (i5 * 256) / k5;
			j5 = (j5 * 256) / k5;

			if (triangleInfo == null || (triangleInfo[i2] & 1) == 0) {
				Point3D point3D_2 = super.vertices[j2];
				point3D_2.normalX += l4;
				point3D_2.normalY += i5;
				point3D_2.normalZ += j5;
				point3D_2.vertexCount++;
				point3D_2 = super.vertices[l2];
				point3D_2.normalX += l4;
				point3D_2.normalY += i5;
				point3D_2.normalZ += j5;
				point3D_2.vertexCount++;
				point3D_2 = super.vertices[i3];
				point3D_2.normalX += l4;
				point3D_2.normalY += i5;
				point3D_2.normalZ += j5;
				point3D_2.vertexCount++;
			} else {
				int l5 = i + (k * l4 + l * i5 + i1 * j5) / (k1 + k1 / 2);
				triangleColorA[i2] = adjustColorBrightness(Objects.requireNonNull(triangleColors)[i2], l5, triangleInfo[i2]);
			}
		}

		if (flag) {
			calculateVertexLighting(i, k1, k, l, i1);
		} else {
			aPoint3DArray1660 = new Point3D[vertexCount];
			for (int k2 = 0; k2 < vertexCount; k2++) {
				Point3D point3D = super.vertices[k2];
				Point3D point3D_1 = aPoint3DArray1660[k2] = new Point3D();
				point3D_1.normalX = point3D.normalX;
				point3D_1.normalY = point3D.normalY;
				point3D_1.normalZ = point3D.normalZ;
				point3D_1.vertexCount = point3D.vertexCount;
			}
		}
		if (flag) {
			calculateBounds();
		} else {
			calculateBoundingBox(21073);
		}
	}

	public final void calculateVertexLighting(int i, int j, int k, int l, int i1) {
		for (int j1 = 0; j1 < triangleCount; j1++) {
			int k1 = triangleVertexA[j1];
			int i2 = triangleVertexB[j1];
			int j2 = triangleVertexC[j1];
			if (triangleInfo == null) {
				int i3 = triangleColors[j1];
				Point3D point3D = super.vertices[k1];
				int k2 = i + (k * point3D.normalX + l * point3D.normalY + i1 * point3D.normalZ) / (j * point3D.vertexCount);
				triangleColorA[j1] = adjustColorBrightness(i3, k2, 0);
				point3D = super.vertices[i2];
				k2 = i + (k * point3D.normalX + l * point3D.normalY + i1 * point3D.normalZ) / (j * point3D.vertexCount);
				triangleColorB[j1] = adjustColorBrightness(i3, k2, 0);
				point3D = super.vertices[j2];
				k2 = i + (k * point3D.normalX + l * point3D.normalY + i1 * point3D.normalZ) / (j * point3D.vertexCount);
				triangleColorC[j1] = adjustColorBrightness(i3, k2, 0);
			} else if ((triangleInfo[j1] & 1) == 0) {
				int j3 = triangleColors[j1];
				int k3 = triangleInfo[j1];
				Point3D point3D_1 = super.vertices[k1];
				int l2 = i + (k * point3D_1.normalX + l * point3D_1.normalY + i1 * point3D_1.normalZ) / (j * point3D_1.vertexCount);
				triangleColorA[j1] = adjustColorBrightness(j3, l2, k3);
				point3D_1 = super.vertices[i2];
				l2 = i + (k * point3D_1.normalX + l * point3D_1.normalY + i1 * point3D_1.normalZ) / (j * point3D_1.vertexCount);
				triangleColorB[j1] = adjustColorBrightness(j3, l2, k3);
				point3D_1 = super.vertices[j2];
				l2 = i + (k * point3D_1.normalX + l * point3D_1.normalY + i1 * point3D_1.normalZ) / (j * point3D_1.vertexCount);
				triangleColorC[j1] = adjustColorBrightness(j3, l2, k3);
			}
		}

		super.vertices = null;
		aPoint3DArray1660 = null;
		vertexLabels = null;
		triangleLabels = null;
		if (triangleInfo != null) {
			for (int l1 = 0; l1 < triangleCount; l1++)
				if ((triangleInfo[l1] & 2) == 2)
					return;
		}
		triangleColors = null;
	}

	public final void renderAtFixedPosition(int j, int k, int l, int i1, int j1, int k1) {
		if (RS317GPUInterface.isActive()) {

			RS317GPUInterface.renderModel(this, i1, j1, k1, j, k, l, 64);
			return;
		}
		int i = 0;
		int l1 = Rasterizer.viewportCenterX;
		int i2 = Rasterizer.viewportCenterY;
		int j2 = modelIntArray1[i];
		int k2 = modelIntArray2[i];
		int l2 = modelIntArray1[j];
		int i3 = modelIntArray2[j];
		int j3 = modelIntArray1[k];
		int k3 = modelIntArray2[k];
		int l3 = modelIntArray1[l];
		int i4 = modelIntArray2[l];
		int j4 = j1 * l3 + k1 * i4 >> 16;
		for (int k4 = 0; k4 < vertexCount; k4++) {
			int l4 = verticesX[k4];
			int i5 = verticesY[k4];
			int j5 = verticesZ[k4];
			if (k != 0) {
				int k5 = i5 * j3 + l4 * k3 >> 16;
				i5 = i5 * k3 - l4 * j3 >> 16;
				l4 = k5;
			}
			if (j != 0) {
				int i6 = j5 * l2 + l4 * i3 >> 16;
				j5 = j5 * i3 - l4 * l2 >> 16;
				l4 = i6;
			}
			l4 += i1;
			i5 += j1;
			j5 += k1;
			int j6 = i5 * i4 - j5 * l3 >> 16;
			j5 = i5 * l3 + j5 * i4 >> 16;
			i5 = j6;

			WorkSpace ws = workspace.get();
			ws.anIntArray1667[k4] = j5 - j4;

			ws.anIntArray1665[k4] = l1 + (l4 << 9) / j5;
			ws.anIntArray1666[k4] = i2 + (i5 << 9) / j5;
			if (textureTriangleCount > 0) {
				ws.anIntArray1668[k4] = l4;
				ws.anIntArray1669[k4] = i5;
				ws.anIntArray1670[k4] = j5;
			} else {
				ws.anIntArray1670[k4] = j5;
			}
		}

		try {
			renderTriangles(false, false, 0);
		} catch (Exception _ex) {
			throw new RuntimeException(_ex);
		}
	}

	public final void render(int rotation, int sinVertical, int cosVertical, int sinHorizontal, int cosHorizontal, int worldX, int worldY, int worldZ, int id) {
		if (RS317GPUInterface.isActive()) {

			RS317GPUInterface.renderModel(this, worldX, worldY, worldZ, rotation, 0, 0, 64);
			return;
		}
		int j2 = worldZ * cosHorizontal - worldX * sinHorizontal >> 16;
		int k2 = worldY * sinVertical + j2 * cosVertical >> 16;
		int l2 = diameter * cosVertical >> 16;
		int i3 = k2 + l2;
		if (i3 <= 50 || k2 >= 3500)
			return;
		int j3 = worldZ * sinHorizontal + worldX * cosHorizontal >> 16;
		int k3 = j3 - diameter << WorldController.viewDistance;
		if (k3 / i3 >= DrawingArea.centerY)
			return;
		int l3 = j3 + diameter << WorldController.viewDistance;
		if (l3 / i3 <= -DrawingArea.centerY)
			return;
		int i4 = worldY * cosVertical - j2 * sinVertical >> 16;
		int j4 = diameter * sinVertical >> 16;
		int k4 = i4 + j4 << WorldController.viewDistance;
		if (k4 / i3 <= -DrawingArea.centerYHalf)
			return;
		int l4 = j4 + (super.modelHeight * cosVertical >> 16);
		int i5 = i4 - l4 << WorldController.viewDistance;
		if (i5 / i3 >= DrawingArea.centerYHalf)
			return;
		int j5 = l2 + (super.modelHeight * sinVertical >> 16);
		boolean flag = k2 - j5 <= 50;
		boolean flag1 = false;
		if (id > 0 && aBoolean1684) {
			int k5 = k2 - l2;
			if (k5 <= 50)
				k5 = 50;
			if (j3 > 0) {
				k3 /= i3;
				l3 /= k5;
			} else {
				l3 /= i3;
				k3 /= k5;
			}
			if (i4 > 0) {
				i5 /= i3;
				k4 /= k5;
			} else {
				k4 /= i3;
				i5 /= k5;
			}
			int i6 = anInt1685 - Rasterizer.viewportCenterX;
			int k6 = anInt1686 - Rasterizer.viewportCenterY;
			if (i6 > k3 && i6 < l3 && k6 > i5 && k6 < k4)
				if (aBoolean1659)
					modelHashes[modelCount++] = id;
				else
					flag1 = true;
		}
		int l5 = Rasterizer.viewportCenterX;
		int j6 = Rasterizer.viewportCenterY;
		int l6 = 0;
		int i7 = 0;
		if (rotation != 0) {
			l6 = modelIntArray1[rotation];
			i7 = modelIntArray2[rotation];
		}

		WorkSpace ws = workspace.get();
		for (int j7 = 0; j7 < vertexCount; j7++) {
			int k7 = verticesX[j7];
			int l7 = verticesY[j7];
			int i8 = verticesZ[j7];
			if (rotation != 0) {
				int j8 = i8 * l6 + k7 * i7 >> 16;
				i8 = i8 * i7 - k7 * l6 >> 16;
				k7 = j8;
			}
			k7 += worldX;
			l7 += worldY;
			i8 += worldZ;
			int k8 = i8 * sinHorizontal + k7 * cosHorizontal >> 16;
			i8 = i8 * cosHorizontal - k7 * sinHorizontal >> 16;
			k7 = k8;
			k8 = l7 * cosVertical - i8 * sinVertical >> 16;
			i8 = l7 * sinVertical + i8 * cosVertical >> 16;
			l7 = k8;
			ws.anIntArray1667[j7] = i8 - k2;
			if (i8 >= 50) {
				ws.anIntArray1665[j7] = l5 + (k7 << WorldController.viewDistance) / i8;
				ws.anIntArray1666[j7] = j6 + (l7 << WorldController.viewDistance) / i8;
			} else {
				ws.anIntArray1665[j7] = -5000;
				flag = true;
			}
			if (flag || textureTriangleCount > 0) {
				ws.anIntArray1668[j7] = k7;
				ws.anIntArray1669[j7] = l7;
				ws.anIntArray1670[j7] = i8;
			} else {
				ws.anIntArray1670[j7] = i8;
			}
		}

		try {
			renderTriangles(flag, flag1, id);
		} catch (Exception _ex) {
			throw new RuntimeException(_ex);
		}
	}

	private void renderTriangles(boolean flag, boolean flag1, int i) {
		WorkSpace ws = workspace.get();
		ws.clear();

		for (int j = 0; j < boundingRadius && j < ws.anIntArray1671.length; j++)
			ws.anIntArray1671[j] = 0;

		for (int k = 0; k < triangleCount; k++)
			if (triangleInfo == null || triangleInfo[k] != -1) {
				int l = triangleVertexA[k];
				int k1 = triangleVertexB[k];
				int j2 = triangleVertexC[k];
				int i3 = ws.anIntArray1665[l];
				int l3 = ws.anIntArray1665[k1];
				int k4 = ws.anIntArray1665[j2];
				if (flag && (i3 == -5000 || l3 == -5000 || k4 == -5000)) {
					ws.aBooleanArray1664[k] = true;
					int j5 = (ws.anIntArray1667[l] + ws.anIntArray1667[k1] + ws.anIntArray1667[j2]) / 3 + boundingCylinderRadius;
					ws.anIntArrayArray1672[j5][ws.anIntArray1671[j5]++] = k;
				} else {
					if (flag1 && isPointInTriangle(anInt1685, anInt1686, ws.anIntArray1666[l], ws.anIntArray1666[k1], ws.anIntArray1666[j2], i3, l3, k4)) {
						modelHashes[modelCount++] = i;
						flag1 = false;
					}
					if ((i3 - l3) * (ws.anIntArray1666[j2] - ws.anIntArray1666[k1]) - (ws.anIntArray1666[l] - ws.anIntArray1666[k1]) * (k4 - l3) > 0) {
						ws.aBooleanArray1664[k] = false;
						ws.aBooleanArray1663[k] = i3 < 0 || l3 < 0 || k4 < 0 || i3 > DrawingArea.centerX || l3 > DrawingArea.centerX || k4 > DrawingArea.centerX;

						int avgDepth = (ws.anIntArray1667[l] + ws.anIntArray1667[k1] + ws.anIntArray1667[j2]) / 3 + boundingCylinderRadius;

						if (vertexCount > 100) {

							int maxDepth = Math.max(ws.anIntArray1667[l], Math.max(ws.anIntArray1667[k1], ws.anIntArray1667[j2])) + boundingCylinderRadius;
							avgDepth = maxDepth;
						}

						ws.anIntArrayArray1672[avgDepth][ws.anIntArray1671[avgDepth]++] = k;
					}
				}
			}

		if (trianglePriorities == null) {
			for (int i1 = boundingRadius - 1; i1 >= 0; i1--) {
				int l1 = ws.anIntArray1671[i1];
				if (l1 > 0) {
					int[] ai = ws.anIntArrayArray1672[i1];
					for (int j3 = 0; j3 < l1; j3++)
						renderTriangle(ai[j3]);
				}
			}
			return;
		}

		for (int j1 = 0; j1 < 12; j1++) {
			ws.anIntArray1673[j1] = 0;
			ws.anIntArray1677[j1] = 0;
		}

		for (int i2 = boundingRadius - 1; i2 >= 0; i2--) {
			int k2 = ws.anIntArray1671[i2];
			if (k2 > 0) {
				int[] ai1 = ws.anIntArrayArray1672[i2];
				for (int i4 = 0; i4 < k2; i4++) {
					int l4 = ai1[i4];
					int l5 = trianglePriorities[l4];
					int j6 = ws.anIntArray1673[l5]++;
					ws.anIntArrayArray1674[l5][j6] = l4;
					if (l5 < 10)
						ws.anIntArray1677[l5] += i2;
					else if (l5 == 10)
						ws.anIntArray1675[j6] = i2;
					else
						ws.anIntArray1676[j6] = i2;
				}
			}
		}

		int l2 = 0;
		if (ws.anIntArray1673[1] > 0 || ws.anIntArray1673[2] > 0)
			l2 = (ws.anIntArray1677[1] + ws.anIntArray1677[2]) / (ws.anIntArray1673[1] + ws.anIntArray1673[2]);
		int k3 = 0;
		if (ws.anIntArray1673[3] > 0 || ws.anIntArray1673[4] > 0)
			k3 = (ws.anIntArray1677[3] + ws.anIntArray1677[4]) / (ws.anIntArray1673[3] + ws.anIntArray1673[4]);
		int j4 = 0;
		if (ws.anIntArray1673[6] > 0 || ws.anIntArray1673[8] > 0)
			j4 = (ws.anIntArray1677[6] + ws.anIntArray1677[8]) / (ws.anIntArray1673[6] + ws.anIntArray1673[8]);
		int i6 = 0;
		int k6 = ws.anIntArray1673[10];
		int[] ai2 = ws.anIntArrayArray1674[10];
		int[] ai3 = ws.anIntArray1675;
		if (i6 == k6) {
			k6 = ws.anIntArray1673[11];
			ai2 = ws.anIntArrayArray1674[11];
			ai3 = ws.anIntArray1676;
		}
		int i5;
		if (i6 < k6)
			i5 = ai3[i6];
		else
			i5 = -1000;
		for (int l6 = 0; l6 < 10; l6++) {
			while (l6 == 0 && i5 > l2) {
				renderTriangle(ai2[i6++]);
				if (i6 == k6 && ai2 != ws.anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = ws.anIntArray1673[11];
					ai2 = ws.anIntArrayArray1674[11];
					ai3 = ws.anIntArray1676;
				}
				if (i6 < k6)
					i5 = ai3[i6];
				else
					i5 = -1000;
			}
			while (l6 == 3 && i5 > k3) {
				renderTriangle(ai2[i6++]);
				if (i6 == k6 && ai2 != ws.anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = ws.anIntArray1673[11];
					ai2 = ws.anIntArrayArray1674[11];
					ai3 = ws.anIntArray1676;
				}
				if (i6 < k6)
					i5 = ai3[i6];
				else
					i5 = -1000;
			}
			while (l6 == 5 && i5 > j4) {
				renderTriangle(ai2[i6++]);
				if (i6 == k6 && ai2 != ws.anIntArrayArray1674[11]) {
					i6 = 0;
					k6 = ws.anIntArray1673[11];
					ai2 = ws.anIntArrayArray1674[11];
					ai3 = ws.anIntArray1676;
				}
				if (i6 < k6)
					i5 = ai3[i6];
				else
					i5 = -1000;
			}
			int i7 = ws.anIntArray1673[l6];
			int[] ai4 = ws.anIntArrayArray1674[l6];
			for (int j7 = 0; j7 < i7; j7++)
				renderTriangle(ai4[j7]);
		}

		while (i5 != -1000) {
			renderTriangle(ai2[i6++]);
			if (i6 == k6 && ai2 != ws.anIntArrayArray1674[11]) {
				i6 = 0;
				ai2 = ws.anIntArrayArray1674[11];
				k6 = ws.anIntArray1673[11];
				ai3 = ws.anIntArray1676;
			}
			if (i6 < k6)
				i5 = ai3[i6];
			else
				i5 = -1000;
		}
	}

	private void renderTriangle(int i) {
		WorkSpace ws = workspace.get();
		if (ws.aBooleanArray1664[i]) {
			renderClippedTriangle(i);
			return;
		}
		int j = triangleVertexA[i];
		int k = triangleVertexB[i];
		int l = triangleVertexC[i];
		Rasterizer.enableClipping = ws.aBooleanArray1663[i];
		if (triangleAlpha == null)
			Rasterizer.alphaBlendValue = 0;
		else
			Rasterizer.alphaBlendValue = triangleAlpha[i];
		int i1;
		if (triangleInfo == null)
			i1 = 0;
		else
			i1 = triangleInfo[i] & 3;
		if (i1 == 0) {
			Rasterizer.renderFlatTriangle(ws.anIntArray1666[j], ws.anIntArray1666[k], ws.anIntArray1666[l], ws.anIntArray1665[j], ws.anIntArray1665[k], ws.anIntArray1665[l], triangleColorA[i], triangleColorB[i], triangleColorC[i], ws.anIntArray1670[j], ws.anIntArray1670[k], ws.anIntArray1670[l]);
			return;
		}
		if (i1 == 1) {
			Rasterizer.renderSolidTriangle(ws.anIntArray1666[j], ws.anIntArray1666[k], ws.anIntArray1666[l], ws.anIntArray1665[j], ws.anIntArray1665[k], ws.anIntArray1665[l], modelIntArray3[triangleColorA[i]], ws.anIntArray1670[j], ws.anIntArray1670[k], ws.anIntArray1670[l]);
			return;
		}
		if (i1 == 2) {
			int j1 = triangleInfo[i] >> 2;
			int l1 = textureTriangleA[j1];
			int j2 = textureTriangleB[j1];
			int l2 = textureTriangleC[j1];
			Rasterizer.renderTexturedTriangle(ws.anIntArray1666[j], ws.anIntArray1666[k], ws.anIntArray1666[l], ws.anIntArray1665[j], ws.anIntArray1665[k], ws.anIntArray1665[l], triangleColorA[i], triangleColorB[i], triangleColorC[i], ws.anIntArray1668[l1], ws.anIntArray1668[j2], ws.anIntArray1668[l2], ws.anIntArray1669[l1], ws.anIntArray1669[j2], ws.anIntArray1669[l2], ws.anIntArray1670[l1], ws.anIntArray1670[j2], ws.anIntArray1670[l2], triangleColors[i], ws.anIntArray1670[j], ws.anIntArray1670[k], ws.anIntArray1670[l]);
			return;
		}
		int k1 = triangleInfo[i] >> 2;
		int i2 = textureTriangleA[k1];
		int k2 = textureTriangleB[k1];
		int i3 = textureTriangleC[k1];
		Rasterizer.renderTexturedTriangle(ws.anIntArray1666[j], ws.anIntArray1666[k], ws.anIntArray1666[l], ws.anIntArray1665[j], ws.anIntArray1665[k], ws.anIntArray1665[l], triangleColorA[i], triangleColorA[i], triangleColorA[i], ws.anIntArray1668[i2], ws.anIntArray1668[k2], ws.anIntArray1668[i3], ws.anIntArray1669[i2], ws.anIntArray1669[k2], ws.anIntArray1669[i3], ws.anIntArray1670[i2], ws.anIntArray1670[k2], ws.anIntArray1670[i3], triangleColors[i], ws.anIntArray1670[j], ws.anIntArray1670[k], ws.anIntArray1670[l]);
	}

	private void renderClippedTriangle(int i) {
		if (triangleColors != null)
			if (triangleColors[i] == 65535)
				return;
		int j = Rasterizer.viewportCenterX;
		int k = Rasterizer.viewportCenterY;
		int l = 0;
		int i1 = triangleVertexA[i];
		int j1 = triangleVertexB[i];
		int k1 = triangleVertexC[i];

		WorkSpace ws = workspace.get();
		int l1 = ws.anIntArray1670[i1];
		int i2 = ws.anIntArray1670[j1];
		int j2 = ws.anIntArray1670[k1];

		if (l1 >= 50) {
			ws.anIntArray1678[l] = ws.anIntArray1665[i1];
			ws.anIntArray1679[l] = ws.anIntArray1666[i1];
			ws.anIntArray1680[l++] = triangleColorA[i];
		} else {
			int k2 = ws.anIntArray1668[i1];
			int k3 = ws.anIntArray1669[i1];
			int k4 = triangleColorA[i];
			if (j2 >= 50) {
				int k5 = (50 - l1) * modelIntArray4[j2 - l1];
				ws.anIntArray1678[l] = j + (k2 + ((ws.anIntArray1668[k1] - k2) * k5 >> 16) << WorldController.viewDistance) / 50;
				ws.anIntArray1679[l] = k + (k3 + ((ws.anIntArray1669[k1] - k3) * k5 >> 16) << WorldController.viewDistance) / 50;
				ws.anIntArray1680[l++] = k4 + ((triangleColorC[i] - k4) * k5 >> 16);
			}
			if (i2 >= 50) {
				int l5 = (50 - l1) * modelIntArray4[i2 - l1];
				ws.anIntArray1678[l] = j + (k2 + ((ws.anIntArray1668[j1] - k2) * l5 >> 16) << WorldController.viewDistance) / 50;
				ws.anIntArray1679[l] = k + (k3 + ((ws.anIntArray1669[j1] - k3) * l5 >> 16) << WorldController.viewDistance) / 50;
				ws.anIntArray1680[l++] = k4 + ((triangleColorB[i] - k4) * l5 >> 16);
			}
		}
		if (i2 >= 50) {
			ws.anIntArray1678[l] = ws.anIntArray1665[j1];
			ws.anIntArray1679[l] = ws.anIntArray1666[j1];
			ws.anIntArray1680[l++] = triangleColorB[i];
		} else {
			int l2 = ws.anIntArray1668[j1];
			int l3 = ws.anIntArray1669[j1];
			int l4 = triangleColorB[i];
			if (l1 >= 50) {
				int i6 = (50 - i2) * modelIntArray4[l1 - i2];
				ws.anIntArray1678[l] = j + (l2 + ((ws.anIntArray1668[i1] - l2) * i6 >> 16) << WorldController.viewDistance) / 50;
				ws.anIntArray1679[l] = k + (l3 + ((ws.anIntArray1669[i1] - l3) * i6 >> 16) << WorldController.viewDistance) / 50;
				ws.anIntArray1680[l++] = l4 + ((triangleColorA[i] - l4) * i6 >> 16);
			}
			if (j2 >= 50) {
				int j6 = (50 - i2) * modelIntArray4[j2 - i2];
				ws.anIntArray1678[l] = j + (l2 + ((ws.anIntArray1668[k1] - l2) * j6 >> 16) << WorldController.viewDistance) / 50;
				ws.anIntArray1679[l] = k + (l3 + ((ws.anIntArray1669[k1] - l3) * j6 >> 16) << WorldController.viewDistance) / 50;
				ws.anIntArray1680[l++] = l4 + ((triangleColorC[i] - l4) * j6 >> 16);
			}
		}
		if (j2 >= 50) {
			ws.anIntArray1678[l] = ws.anIntArray1665[k1];
			ws.anIntArray1679[l] = ws.anIntArray1666[k1];
			ws.anIntArray1680[l++] = triangleColorC[i];
		} else {
			int i3 = ws.anIntArray1668[k1];
			int i4 = ws.anIntArray1669[k1];
			int i5 = triangleColorC[i];
			if (i2 >= 50) {
				int k6 = (50 - j2) * modelIntArray4[i2 - j2];
				ws.anIntArray1678[l] = j + (i3 + ((ws.anIntArray1668[j1] - i3) * k6 >> 16) << WorldController.viewDistance) / 50;
				ws.anIntArray1679[l] = k + (i4 + ((ws.anIntArray1669[j1] - i4) * k6 >> 16) << WorldController.viewDistance) / 50;
				ws.anIntArray1680[l++] = i5 + ((triangleColorB[i] - i5) * k6 >> 16);
			}
			if (l1 >= 50) {
				int l6 = (50 - j2) * modelIntArray4[l1 - j2];
				ws.anIntArray1678[l] = j + (i3 + ((ws.anIntArray1668[i1] - i3) * l6 >> 16) << WorldController.viewDistance) / 50;
				ws.anIntArray1679[l] = k + (i4 + ((ws.anIntArray1669[i1] - i4) * l6 >> 16) << WorldController.viewDistance) / 50;
				ws.anIntArray1680[l++] = i5 + ((triangleColorA[i] - i5) * l6 >> 16);
			}
		}
		int j3 = ws.anIntArray1678[0];
		int j4 = ws.anIntArray1678[1];
		int j5 = ws.anIntArray1678[2];
		int i7 = ws.anIntArray1679[0];
		int j7 = ws.anIntArray1679[1];
		int k7 = ws.anIntArray1679[2];
		if ((j3 - j4) * (k7 - j7) - (i7 - j7) * (j5 - j4) > 0) {
			Rasterizer.enableClipping = false;
			if (l == 3) {
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > DrawingArea.centerX || j4 > DrawingArea.centerX || j5 > DrawingArea.centerX)
					Rasterizer.enableClipping = true;
				int l7;
				if (triangleInfo == null)
					l7 = 0;
				else
					l7 = triangleInfo[i] & 3;
				if (l7 == 0)
					Rasterizer.renderFlatTriangle(i7, j7, k7, j3, j4, j5, ws.anIntArray1680[0], ws.anIntArray1680[1], ws.anIntArray1680[2], -1F, -1F, -1F);
				else if (l7 == 1)
					Rasterizer.renderSolidTriangle(i7, j7, k7, j3, j4, j5, modelIntArray3[triangleColorA[i]], -1F, -1F, -1F);
				else if (l7 == 2) {
					int j8 = triangleInfo[i] >> 2;
					int k9 = textureTriangleA[j8];
					int k10 = textureTriangleB[j8];
					int k11 = textureTriangleC[j8];
					Rasterizer.renderTexturedTriangle(i7, j7, k7, j3, j4, j5, ws.anIntArray1680[0], ws.anIntArray1680[1], ws.anIntArray1680[2], ws.anIntArray1668[k9], ws.anIntArray1668[k10], ws.anIntArray1668[k11], ws.anIntArray1669[k9], ws.anIntArray1669[k10], ws.anIntArray1669[k11], ws.anIntArray1670[k9], ws.anIntArray1670[k10], ws.anIntArray1670[k11], Objects.requireNonNull(triangleColors)[i], ws.anIntArray1670[i1], ws.anIntArray1670[j1], ws.anIntArray1670[k1]);
				} else {
					int k8 = triangleInfo[i] >> 2;
					int l9 = textureTriangleA[k8];
					int l10 = textureTriangleB[k8];
					int l11 = textureTriangleC[k8];
					Rasterizer.renderTexturedTriangle(i7, j7, k7, j3, j4, j5, triangleColorA[i], triangleColorA[i], triangleColorA[i], ws.anIntArray1668[l9], ws.anIntArray1668[l10], ws.anIntArray1668[l11], ws.anIntArray1669[l9], ws.anIntArray1669[l10], ws.anIntArray1669[l11], ws.anIntArray1670[l9], ws.anIntArray1670[l10], ws.anIntArray1670[l11], Objects.requireNonNull(triangleColors)[i], ws.anIntArray1670[i1], ws.anIntArray1670[j1], ws.anIntArray1670[k1]);
				}
			}
			if (l == 4) {
				if (j3 < 0 || j4 < 0 || j5 < 0 || j3 > DrawingArea.centerX || j4 > DrawingArea.centerX || j5 > DrawingArea.centerX || ws.anIntArray1678[3] < 0 || ws.anIntArray1678[3] > DrawingArea.centerX)
					Rasterizer.enableClipping = true;
				int i8;
				if (triangleInfo == null)
					i8 = 0;
				else
					i8 = triangleInfo[i] & 3;
				if (i8 == 0) {
					Rasterizer.renderFlatTriangle(i7, j7, k7, j3, j4, j5, ws.anIntArray1680[0], ws.anIntArray1680[1], ws.anIntArray1680[2], -1F, -1F, -1F);
					Rasterizer.renderFlatTriangle(i7, k7, ws.anIntArray1679[3], j3, j5, ws.anIntArray1678[3], ws.anIntArray1680[0], ws.anIntArray1680[2], ws.anIntArray1680[3], ws.anIntArray1670[i1], ws.anIntArray1670[j1], ws.anIntArray1670[k1]);
					return;
				}
				if (i8 == 1) {
					int l8 = modelIntArray3[triangleColorA[i]];
					Rasterizer.renderSolidTriangle(i7, j7, k7, j3, j4, j5, l8, -1F, -1F, -1F);
					Rasterizer.renderSolidTriangle(i7, k7, ws.anIntArray1679[3], j3, j5, ws.anIntArray1678[3], l8, ws.anIntArray1670[i1], ws.anIntArray1670[j1], ws.anIntArray1670[k1]);
					return;
				}
				if (i8 == 2) {
					int i9 = triangleInfo[i] >> 2;
					int i10 = textureTriangleA[i9];
					int i11 = textureTriangleB[i9];
					int i12 = textureTriangleC[i9];
					Rasterizer.renderTexturedTriangle(i7, j7, k7, j3, j4, j5, ws.anIntArray1680[0], ws.anIntArray1680[1], ws.anIntArray1680[2], ws.anIntArray1668[i10], ws.anIntArray1668[i11], ws.anIntArray1668[i12], ws.anIntArray1669[i10], ws.anIntArray1669[i11], ws.anIntArray1669[i12], ws.anIntArray1670[i10], ws.anIntArray1670[i11], ws.anIntArray1670[i12], Objects.requireNonNull(triangleColors)[i], ws.anIntArray1670[i1], ws.anIntArray1670[j1], ws.anIntArray1670[k1]);
					Rasterizer.renderTexturedTriangle(i7, k7, ws.anIntArray1679[3], j3, j5, ws.anIntArray1678[3], ws.anIntArray1680[0], ws.anIntArray1680[2], ws.anIntArray1680[3], ws.anIntArray1668[i10], ws.anIntArray1668[i11], ws.anIntArray1668[i12], ws.anIntArray1669[i10], ws.anIntArray1669[i11], ws.anIntArray1669[i12], ws.anIntArray1670[i10], ws.anIntArray1670[i11], ws.anIntArray1670[i12], triangleColors[i], ws.anIntArray1670[i1], ws.anIntArray1670[j1], ws.anIntArray1670[k1]);
					return;
				}
				int j9 = triangleInfo[i] >> 2;
				int j10 = textureTriangleA[j9];
				int j11 = textureTriangleB[j9];
				int j12 = textureTriangleC[j9];
				Rasterizer.renderTexturedTriangle(i7, j7, k7, j3, j4, j5, triangleColorA[i], triangleColorA[i], triangleColorA[i], ws.anIntArray1668[j10], ws.anIntArray1668[j11], ws.anIntArray1668[j12], ws.anIntArray1669[j10], ws.anIntArray1669[j11], ws.anIntArray1669[j12], ws.anIntArray1670[j10], ws.anIntArray1670[j11], ws.anIntArray1670[j12], triangleColors[i], ws.anIntArray1670[i1], ws.anIntArray1670[j1], ws.anIntArray1670[k1]);
				Rasterizer.renderTexturedTriangle(i7, k7, ws.anIntArray1679[3], j3, j5, ws.anIntArray1678[3], triangleColorA[i], triangleColorA[i], triangleColorA[i], ws.anIntArray1668[j10], ws.anIntArray1668[j11], ws.anIntArray1668[j12], ws.anIntArray1669[j10], ws.anIntArray1669[j11], ws.anIntArray1669[j12], ws.anIntArray1670[j10], ws.anIntArray1670[j11], ws.anIntArray1670[j12], triangleColors[i], ws.anIntArray1670[i1], ws.anIntArray1670[j1], ws.anIntArray1670[k1]);
			}
		}
	}

	private boolean isPointInTriangle(int i, int j, int k, int l, int i1, int j1, int k1, int l1) {
		if (j < k && j < l && j < i1)
			return false;
		if (j > k && j > l && j > i1)
			return false;
		if (i < j1 && i < k1 && i < l1)
			return false;
		return i <= j1 || i <= k1 || i <= l1;
	}
}
