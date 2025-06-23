package com.bestbudz.engine.core.gamerender;

import com.bestbudz.cache.Signlink;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.ui.handling.input.Keyboard.keyArray;
import com.bestbudz.rendering.model.Model;
import static com.bestbudz.world.TerrainHeight.getTerrainHeight;

public class Camera extends Client
{
	// ===== ADD THESE FIELDS TO THE Camera CLASS =====
// Add these static fields at the top of the Camera class
	private static int minValidXCameraPos = Integer.MIN_VALUE;
	private static int maxValidXCameraPos = Integer.MAX_VALUE;
	private static int minValidYCameraPos = Integer.MIN_VALUE;
	private static int maxValidYCameraPos = Integer.MAX_VALUE;
	private static boolean boundsLearned = false;

	// ===== REPLACE THE calcCameraPos() METHOD =====
	public static void calcCameraPos()
	{
		int i = anInt1098 * 128 + 64;
		int j = anInt1099 * 128 + 64;
		int k = getTerrainHeight(plane, j, i) - anInt1100;

		// Store the original camera positions before modification
		int originalXCameraPos = xCameraPos;
		int originalYCameraPos = yCameraPos;

		if (xCameraPos < i)
		{
			xCameraPos += anInt1101 + ((i - xCameraPos) * anInt1102) / 1000;
			if (xCameraPos > i)
				xCameraPos = i;
		}
		if (xCameraPos > i)
		{
			xCameraPos -= anInt1101 + ((xCameraPos - i) * anInt1102) / 1000;
			if (xCameraPos < i)
				xCameraPos = i;
		}
		if (zCameraPos < k)
		{
			zCameraPos += anInt1101 + ((k - zCameraPos) * anInt1102) / 1000;
			if (zCameraPos > k)
				zCameraPos = k;
		}
		if (zCameraPos > k)
		{
			zCameraPos -= anInt1101 + ((zCameraPos - k) * anInt1102) / 1000;
			if (zCameraPos < k)
				zCameraPos = k;
		}
		if (yCameraPos < j)
		{
			yCameraPos += anInt1101 + ((j - yCameraPos) * anInt1102) / 1000;
			if (yCameraPos > j)
				yCameraPos = j;
		}
		if (yCameraPos > j)
		{
			yCameraPos -= anInt1101 + ((yCameraPos - j) * anInt1102) / 1000;
			if (yCameraPos < j)
				yCameraPos = j;
		}

		// APPLY BOUNDS CLAMPING HERE - before the final camera calculations
		clampCameraToBounds();

		i = anInt995 * 128 + 64;
		j = anInt996 * 128 + 64;
		k = getTerrainHeight(plane, j, i) - anInt997;
		int l = i - xCameraPos;
		int i1 = k - zCameraPos;
		int j1 = j - yCameraPos;
		int k1 = (int) Math.sqrt(l * l + j1 * j1);
		int l1 = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
		int i2 = (int) (Math.atan2(l, j1) * -325.94900000000001D) & 0x7ff;
		if (l1 < 128)
			l1 = 128;
		if (l1 > 383)
			l1 = 383;
		if (yCameraCurve < l1)
		{
			yCameraCurve += anInt998 + ((l1 - yCameraCurve) * anInt999) / 1000;
			if (yCameraCurve > l1)
				yCameraCurve = l1;
		}
		if (yCameraCurve > l1)
		{
			yCameraCurve -= anInt998 + ((yCameraCurve - l1) * anInt999) / 1000;
			if (yCameraCurve < l1)
				yCameraCurve = l1;
		}
		int j2 = i2 - xCameraCurve;
		if (j2 > 1024)
			j2 -= 2048;
		if (j2 < -1024)
			j2 += 2048;
		if (j2 > 0)
		{
			xCameraCurve += anInt998 + (j2 * anInt999) / 1000;
			xCameraCurve &= 0x7ff;
		}
		if (j2 < 0)
		{
			xCameraCurve -= anInt998 + (-j2 * anInt999) / 1000;
			xCameraCurve &= 0x7ff;
		}
		int k2 = i2 - xCameraCurve;
		if (k2 > 1024)
			k2 -= 2048;
		if (k2 < -1024)
			k2 += 2048;
		if (k2 < 0 && j2 > 0 || k2 > 0 && j2 < 0)
			xCameraCurve = i2;
	}

// ===== REPLACE THE clampCameraToBounds() METHOD IN Camera CLASS =====
	/**
	 * Simple bounds clamping - elevation is now handled in checkAndApplyElevation()
	 */
	private static void clampCameraToBounds()
	{
		// This method is now simplified since elevation is handled elsewhere
		if (boundsLearned) {
			// Simple clamping without elevation logic
			if (xCameraPos < minValidXCameraPos) {
				xCameraPos = minValidXCameraPos;
			} else if (xCameraPos > maxValidXCameraPos) {
				xCameraPos = maxValidXCameraPos;
			}

			if (yCameraPos < minValidYCameraPos) {
				yCameraPos = minValidYCameraPos;
			} else if (yCameraPos > maxValidYCameraPos) {
				yCameraPos = maxValidYCameraPos;
			}
		}
	}

	public static void updateCameraPosition()
	{
		try
		{
			int x = myStoner.x + anInt1278;
			int y = myStoner.y + anInt1131;
			double rotSpeed = 2;
			screenGliding = 0;

			// ✅ GOOD: Camera position updates for view matrix only
			// This should NOT affect world loading or LOD decisions
			if (anInt1014 - x < -500 || anInt1014 - x > 500 || anInt1015 - y < -500 || anInt1015 - y > 500) {
				anInt1014 = x;
				anInt1015 = y;
			}

			// ✅ Camera smoothing for view only
			if (anInt1014 != x) {
				anInt1014 += (x - anInt1014) / 16;
			}
			if (anInt1015 != y) {
				anInt1015 += (y - anInt1015) / 16;
			}
			if (keyArray[1] == 1)
			{
				anInt1186 += (-24 - anInt1186) / rotSpeed;
				screenGliding++;
			}
			else if (keyArray[2] == 1)
			{
				anInt1186 += (24 - anInt1186) / rotSpeed;
				screenGliding++;
			}
			else
			{
				if (screenGliding >= 10)
				{
					if (anInt1186 > 0)
					{
						anInt1186--;
					}
					else if (anInt1186 < 0)
					{
						anInt1186++;
					}
				}
				else
				{
					anInt1186 /= rotSpeed;
				}
			}
			if (keyArray[3] == 1)
			{
				anInt1187 += (12 - anInt1187) / rotSpeed;
				screenGliding++;
			}
			else if (keyArray[4] == 1)
			{
				anInt1187 += (-12 - anInt1187) / rotSpeed;
				screenGliding++;
			}
			else
			{
				if (screenGliding >= 10)
				{
					if (anInt1187 > 0)
					{
						anInt1187--;
					}
					else if (anInt1187 < 0)
					{
						anInt1187++;
					}
				}
				else
				{
					anInt1187 /= rotSpeed;
				}
			}
			minimapInt1 = minimapInt1 + anInt1186 / (int) rotSpeed & 0x7ff;
			anInt1184 += anInt1187 / rotSpeed;
			if (anInt1184 < 128)
			{
				anInt1184 = 128;
			}
			if (anInt1184 > 383)
			{
				anInt1184 = 383;
			}
			int l = anInt1014 >> 7;
			int i1 = anInt1015 >> 7;
			int j1 = getTerrainHeight(plane, anInt1015, anInt1014);
			int k1 = 0;
			if (l > 3 && i1 > 3 && l < 100 && i1 < 100)
			{
				for (int l1 = l - 4; l1 <= l + 4; l1++)
				{
					for (int k2 = i1 - 4; k2 <= i1 + 4; k2++)
					{
						int l2 = plane;
						if (l2 < 3 && (byteGroundArray[1][l1][k2] & 2) == 2)
							l2++;
						int i3 = j1 - intGroundArray[l2][l1][k2];
						if (i3 > k1)
							k1 = i3;
					}

				}

			}
			anInt1005++;
			if (anInt1005 > 1512)
			{
				anInt1005 = 0;
				stream.createFrame(77);
				stream.writeWordBigEndian(0);
				int i2 = stream.currentOffset;
				stream.writeWordBigEndian((int) (Math.random() * 256D));
				stream.writeWordBigEndian(101);
				stream.writeWordBigEndian(233);
				stream.writeWord(45092);
				if ((int) (Math.random() * 2D) == 0)
					stream.writeWord(35784);
				stream.writeWordBigEndian((int) (Math.random() * 256D));
				stream.writeWordBigEndian(64);
				stream.writeWordBigEndian(38);
				stream.writeWord((int) (Math.random() * 65536D));
				stream.writeWord((int) (Math.random() * 65536D));
				stream.writeBytes(stream.currentOffset - i2);
			}
			int j2 = k1 * 192;
			if (j2 > 0x17f00)
				j2 = 0x17f00;
			if (j2 < 32768)
				j2 = 32768;
			if (j2 > anInt984)
			{
				anInt984 += (j2 - anInt984) / 24;
				return;
			}
			if (j2 < anInt984)
			{
				anInt984 += (j2 - anInt984) / 80;
			}
		}
		catch (Exception _ex)
		{
			Signlink.reporterror("glfc_ex " + myStoner.x + "," + myStoner.y + "," + anInt1014 + "," + anInt1015 + ","
				+ anInt1069 + "," + anInt1070 + "," + baseX + "," + baseY);
			throw new RuntimeException("eek");
		}
	}

	public static void setCameraPos(int j, int k, int l, int i1, int j1, int k1)
	{
		int l1 = 2048 - k & 0x7ff;
		int i2 = 2048 - j1 & 0x7ff;
		int j2 = 0;
		int k2 = 0;
		int l2 = j;
		if (l1 != 0)
		{
			int i3 = Model.modelIntArray1[l1];
			int k3 = Model.modelIntArray2[l1];
			int i4 = k2 * k3 - l2 * i3 >> 16;
			l2 = k2 * i3 + l2 * k3 >> 16;
			k2 = i4;
		}
		if (i2 != 0)
		{
			int j3 = Model.modelIntArray1[i2];
			int l3 = Model.modelIntArray2[i2];
			int j4 = l2 * j3 + j2 * l3 >> 16;
			l2 = l2 * l3 - j2 * j3 >> 16;
			j2 = j4;
		}
		xCameraPos = l - j2;
		zCameraPos = i1 - k2;
		yCameraPos = k1 - l2;
		yCameraCurve = k;
		xCameraCurve = j1;
	}

}
