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
// REPLACE the calcCameraPos() method in Camera class with this version
	public static void calcCameraPos()
	{
		int i = cameraOffsetX * 128 + 64;
		int j = cameraOffsetY * 128 + 64;
		int k = getTerrainHeight(plane, j, i) - cameraOffsetZ;

		if (xCameraPos < i)
		{
			xCameraPos += cameraRotationX + ((i - xCameraPos) * cameraRotationY) / 1000;
			if (xCameraPos > i)
				xCameraPos = i;
		}
		if (xCameraPos > i)
		{
			xCameraPos -= cameraRotationX + ((xCameraPos - i) * cameraRotationY) / 1000;
			if (xCameraPos < i)
				xCameraPos = i;
		}
		if (zCameraPos < k)
		{
			zCameraPos += cameraRotationX + ((k - zCameraPos) * cameraRotationY) / 1000;
			if (zCameraPos > k)
				zCameraPos = k;
		}
		if (zCameraPos > k)
		{
			zCameraPos -= cameraRotationX + ((zCameraPos - k) * cameraRotationY) / 1000;
			if (zCameraPos < k)
				zCameraPos = k;
		}
		if (yCameraPos < j)
		{
			yCameraPos += cameraRotationX + ((j - yCameraPos) * cameraRotationY) / 1000;
			if (yCameraPos > j)
				yCameraPos = j;
		}
		if (yCameraPos > j)
		{
			yCameraPos -= cameraRotationX + ((yCameraPos - j) * cameraRotationY) / 1000;
			if (yCameraPos < j)
				yCameraPos = j;
		}

		i = interfaceScrollX * 128 + 64;
		j = interfaceScrollY * 128 + 64;
		k = getTerrainHeight(plane, j, i) - scrollableAreaHeight;
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
			yCameraCurve += scrollableAreaWidth + ((l1 - yCameraCurve) * scrollPosition) / 1000;
			if (yCameraCurve > l1)
				yCameraCurve = l1;
		}
		if (yCameraCurve > l1)
		{
			yCameraCurve -= scrollableAreaWidth + ((yCameraCurve - l1) * scrollPosition) / 1000;
			if (yCameraCurve < l1)
				yCameraCurve = l1;
		}

		// FIXED: Improved angle wrapping that prevents coordinate system inversion
		int targetAngle = i2;
		int currentAngle = xCameraCurve;

		// Normalize angles to 0-2047 range
		targetAngle = targetAngle & 0x7ff;
		currentAngle = currentAngle & 0x7ff;

		// Calculate shortest angular distance
		int angleDiff = targetAngle - currentAngle;
		if (angleDiff > 1024) {
			angleDiff -= 2048;
		} else if (angleDiff < -1024) {
			angleDiff += 2048;
		}

		// CRITICAL FIX: Prevent angles that cause coordinate system flipping
		// These angle ranges (90°-270°) cause the transformation matrix to invert
		int proposedAngle;
		if (angleDiff > 0) {
			proposedAngle = (currentAngle + scrollableAreaWidth + (angleDiff * scrollPosition) / 1000) & 0x7ff;
		} else {
			proposedAngle = (currentAngle - scrollableAreaWidth + ((-angleDiff) * scrollPosition) / 1000) & 0x7ff;
		}

		// Check if the proposed angle would cause coordinate system issues
		// Angles around 90°-270° (512-1536 in our 0-2047 system) can cause problems
		boolean wouldCauseFlip = (proposedAngle > 400 && proposedAngle < 1648);

		if (wouldCauseFlip) {
			// Constrain the angle to safe ranges that don't cause coordinate flips
			if (proposedAngle > 1024) {
				// If trying to go past 180°, clamp to just before problem zone
				proposedAngle = 400;
			} else {
				// If trying to go past 90°, clamp to just after problem zone
				proposedAngle = 1648;
			}
		}

		xCameraCurve = proposedAngle;

		// Alternative approach: Use modulo arithmetic to keep angles in safe ranges
		// Uncomment this instead of the above if you prefer different behavior:
	/*
	if (angleDiff > 0) {
		xCameraCurve += scrollableAreaWidth + (angleDiff * scrollPosition) / 1000;
	} else {
		xCameraCurve -= scrollableAreaWidth + ((-angleDiff) * scrollPosition) / 1000;
	}

	// Keep in 0-2047 range and avoid problem angles
	xCameraCurve = xCameraCurve & 0x7ff;

	// If we end up in the problem zone, skip to the safe side
	if (xCameraCurve > 400 && xCameraCurve < 1648) {
		if (xCameraCurve < 1024) {
			xCameraCurve = 400;  // Stay in safe zone before 90°
		} else {
			xCameraCurve = 1648; // Jump to safe zone after 270°
		}
	}
	*/
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
			int x = myStoner.x + selectedSpellIndex;
			int y = myStoner.y + rightClickMenuOption;
			double rotSpeed = 2;
			screenGliding = 0;

			// ✅ GOOD: Camera position updates for view matrix only
			// This should NOT affect world loading or LOD decisions
			if (cameraX - x < -500 || cameraX - x > 500 || cameraZ - y < -500 || cameraZ - y > 500) {
				cameraX = x;
				cameraZ = y;
			}

			// ✅ Camera smoothing for view only
			if (cameraX != x) {
				cameraX += (x - cameraX) / 16;
			}
			if (cameraZ != y) {
				cameraZ += (y - cameraZ) / 16;
			}
			if (keyArray[1] == 1)
			{
				cameraYawVelocity += (-24 - cameraYawVelocity) / rotSpeed;
				screenGliding++;
			}
			else if (keyArray[2] == 1)
			{
				cameraYawVelocity += (24 - cameraYawVelocity) / rotSpeed;
				screenGliding++;
			}
			else
			{
				if (screenGliding >= 10)
				{
					if (cameraYawVelocity > 0)
					{
						cameraYawVelocity--;
					}
					else if (cameraYawVelocity < 0)
					{
						cameraYawVelocity++;
					}
				}
				else
				{
					cameraYawVelocity /= rotSpeed;
				}
			}
			if (keyArray[3] == 1)
			{
				cameraPitchVelocity += (12 - cameraPitchVelocity) / rotSpeed;
				screenGliding++;
			}
			else if (keyArray[4] == 1)
			{
				cameraPitchVelocity += (-12 - cameraPitchVelocity) / rotSpeed;
				screenGliding++;
			}
			else
			{
				if (screenGliding >= 10)
				{
					if (cameraPitchVelocity > 0)
					{
						cameraPitchVelocity--;
					}
					else if (cameraPitchVelocity < 0)
					{
						cameraPitchVelocity++;
					}
				}
				else
				{
					cameraPitchVelocity /= rotSpeed;
				}
			}
			minimapRotation = minimapRotation + cameraYawVelocity / (int) rotSpeed & 0x7ff;
			minCameraHeight += cameraPitchVelocity / rotSpeed;
			if (minCameraHeight < 128)
			{
				minCameraHeight = 128;
			}
			if (minCameraHeight > 383)
			{
				minCameraHeight = 383;
			}
			int l = cameraX >> 7;
			int i1 = cameraZ >> 7;
			int j1 = getTerrainHeight(plane, cameraZ, cameraX);
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
			packetTimer++;
			if (packetTimer > 1512)
			{
				packetTimer = 0;
				stream.writeEncryptedOpcode(77);
				stream.writeByte(0);
				int i2 = stream.position;
				stream.writeByte((int) (Math.random() * 256D));
				stream.writeByte(101);
				stream.writeByte(233);
				stream.writeWord(45092);
				if ((int) (Math.random() * 2D) == 0)
					stream.writeWord(35784);
				stream.writeByte((int) (Math.random() * 256D));
				stream.writeByte(64);
				stream.writeByte(38);
				stream.writeWord((int) (Math.random() * 65536D));
				stream.writeWord((int) (Math.random() * 65536D));
				stream.writePacketLength(stream.position - i2);
			}
			int j2 = k1 * 192;
			if (j2 > 0x17f00)
				j2 = 0x17f00;
			if (j2 < 32768)
				j2 = 32768;
			if (j2 > stonerHeight)
			{
				stonerHeight += (j2 - stonerHeight) / 24;
				return;
			}
			if (j2 < stonerHeight)
			{
				stonerHeight += (j2 - stonerHeight) / 80;
			}
		}
		catch (Exception _ex)
		{
			Signlink.reporterror("glfc_ex " + myStoner.x + "," + myStoner.y + "," + cameraX + "," + cameraZ + ","
				+ inventoryOffsetX + "," + inventoryOffsetY + "," + baseX + "," + baseY);
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
