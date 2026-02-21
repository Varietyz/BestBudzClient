package com.bestbudz.graphics.sprite;

import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.network.Stream;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public final class Sprite extends DrawingArea
{

	public int[] myPixels;
	public int myWidth;
	public int myHeight;
	public int drawOffsetX;
	public int originalWidth;
	public int originalHeight;
	public int drawOffsetY;

	public Sprite(int i, int j) {
		myPixels = new int[i * j];
		myWidth = originalWidth = i;
		myHeight = originalHeight = j;
		drawOffsetX = drawOffsetY = 0;
	}

	public Sprite(byte[] abyte0, Component component) {
		try {
			Image image = Toolkit.getDefaultToolkit().createImage(abyte0);
			MediaTracker mediatracker = new MediaTracker(component);
			mediatracker.addImage(image, 0);
			mediatracker.waitForAll();
			myWidth = image.getWidth(component);
			myHeight = image.getHeight(component);
			originalWidth = myWidth;
			originalHeight = myHeight;
			drawOffsetX = 0;
			drawOffsetY = 0;
			myPixels = new int[myWidth * myHeight];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, myWidth, myHeight, myPixels, 0, myWidth);
			pixelgrabber.grabPixels();
		} catch (Exception _ex) {
			System.out.println("Error converting jpg");
		}
	}

	public Sprite(URL url) {
		try {
			if (url == null) {
				return;
			}
			BufferedImage image = ImageIO.read(url.openStream());
			myWidth = image.getWidth();
			myHeight = image.getHeight();
			originalWidth = myWidth;
			originalHeight = myHeight;
			drawOffsetX = 0;
			drawOffsetY = 0;
			myPixels = new int[myWidth * myHeight];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, myWidth, myHeight, myPixels, 0, myWidth);
			pixelgrabber.grabPixels();
		} catch (Exception exception) {
			System.out.println(exception);
		}
	}

	public Sprite(Sprite sprite_a, int x, int y, int w, int h) {
		try {
			ImageIcon Sprite = new ImageIcon(getSprite(sprite_a));
			BufferedImage bi = toBufferedImage(Sprite.getImage()).getSubimage(x, y, w, h);
			ImageIcon b = new ImageIcon(bi);
			Image image = b.getImage();
			ImageIcon sprite = new ImageIcon(image);
			myWidth = sprite.getIconWidth();
			myHeight = sprite.getIconHeight();
			originalWidth = myWidth;
			originalHeight = myHeight;
			drawOffsetX = 0;
			drawOffsetY = 0;
			myPixels = new int[myWidth * myHeight];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, myWidth, myHeight, myPixels, 0, myWidth);
			pixelgrabber.grabPixels();
			setTransparency(0, 0, 0);
		} catch (Exception _ex) {
			System.out.println(_ex);
		}
	}

	public static Sprite createEmptySprite() {
		return new Sprite();
	}

	public Sprite() {

		myWidth = 1;
		myHeight = 1;
		myPixels = new int[1];
		myPixels[0] = 0;
		drawOffsetX = 0;
		drawOffsetY = 0;
		originalWidth = 1;
		originalHeight = 1;
	}

	public Sprite(byte[] spriteData) {
		try {
			Image image = Toolkit.getDefaultToolkit().createImage(spriteData);
			ImageIcon sprite = new ImageIcon(image);
			myWidth = sprite.getIconWidth();
			myHeight = sprite.getIconHeight();
			originalWidth = myWidth;
			originalHeight = myHeight;
			drawOffsetX = 0;
			drawOffsetY = 0;
			myPixels = new int[myWidth * myHeight];
			PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, myWidth, myHeight, myPixels, 0, myWidth);
			pixelgrabber.grabPixels();
			setTransparency(255, 0, 255);
		} catch (Exception _ex) {
			System.out.println(_ex);
		}
	}

	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		image = new ImageIcon(image).getImage();
		boolean hasAlpha = false;
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			int transparency = Transparency.OPAQUE;
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e)
		{
			throw new RuntimeException(e);
		}
		if (bimage == null) {
			int type = BufferedImage.TYPE_INT_RGB;
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}
		Graphics2D g = bimage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bimage;
	}

	public static Image getSprite(Sprite sprite) {
		if (sprite == null) {
			return null;
		}
		return pixelsToImage(sprite.myWidth, sprite.myHeight, sprite.myPixels);
	}

	public static BufferedImage pixelsToImage(int width, int height, int[] pixels) {
		BufferedImage bufferedimage = new BufferedImage(width, height, 1);
		bufferedimage.setRGB(0, 0, width, height, pixels, 0, width);
		Graphics2D graphics2d = bufferedimage.createGraphics();
		graphics2d.dispose();
		return bufferedimage;
	}

	public void drawTransparentSprite(int i, int j, int opacity) {
		i += drawOffsetX;
		j += drawOffsetY;
		int i1 = i + j * DrawingArea.width;
		int j1 = 0;
		int k1 = myHeight;
		int l1 = myWidth;
		int i2 = DrawingArea.width - l1;
		int j2 = 0;
		if (j < DrawingArea.topY) {
			int k2 = DrawingArea.topY - j;
			k1 -= k2;
			j = DrawingArea.topY;
			j1 += k2 * l1;
			i1 += k2 * DrawingArea.width;
		}
		if (j + k1 > DrawingArea.bottomY)
			k1 -= (j + k1) - DrawingArea.bottomY;
		if (i < DrawingArea.topX) {
			int l2 = DrawingArea.topX - i;
			l1 -= l2;
			i = DrawingArea.topX;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if (i + l1 > DrawingArea.bottomX) {
			int i3 = (i + l1) - DrawingArea.bottomX;
			l1 -= i3;
			j2 += i3;
			i2 += i3;
		}
		if (!(l1 <= 0 || k1 <= 0)) {
			copyPixelsAlpha(j1, l1, DrawingArea.pixels, myPixels, j2, k1, i2, opacity, i1);
		}
	}

	private void set24BitPixels(int width, int height, int[] destPixels, int[] srcPixels, int destOffset,
								int srcOffset, int destStep, int srcStep) {
		int srcColor;
		int destAlpha;
		for (int loop = -height; loop < 0; loop++) {
			for (int loop2 = -width; loop2 < 0; loop2++) {
				int srcAlpha = ((this.myPixels[srcOffset] >> 24) & 255);
				destAlpha = 256 - srcAlpha;
				srcColor = srcPixels[srcOffset++];
				if (srcColor != 0 && srcColor != 0xffffff) {
					int destColor = destPixels[destOffset];
					destPixels[destOffset++] = ((srcColor & 0xff00ff) * srcAlpha + (destColor & 0xff00ff) * destAlpha
							& 0xff00ff00)
							+ ((srcColor & 0xff00) * srcAlpha + (destColor & 0xff00) * destAlpha & 0xff0000) >> 8;
				} else {
					destOffset++;
				}
			}
			destOffset += destStep;
			srcOffset += srcStep;
		}
	}

	public void setTransparency(int transRed, int transGreen, int transBlue) {
		for (int index = 0; index < myPixels.length; index++)
			if (((myPixels[index] >> 16) & 255) == transRed && ((myPixels[index] >> 8) & 255) == transGreen
					&& (myPixels[index] & 255) == transBlue)
				myPixels[index] = 0;
	}

	public void adjustBrightness(int i, int j, int k) {

		if (myPixels == null) {
			return;
		}

		for (int i1 = 0; i1 < myPixels.length; i1++) {
			int j1 = myPixels[i1];
			if (j1 != 0) {
				int k1 = j1 >> 16 & 0xff;
				k1 += i;
				if (k1 < 1)
					k1 = 1;
				else if (k1 > 255)
					k1 = 255;
				int l1 = j1 >> 8 & 0xff;
				l1 += j;
				if (l1 < 1)
					l1 = 1;
				else if (l1 > 255)
					l1 = 255;
				int i2 = j1 & 0xff;
				i2 += k;
				if (i2 < 1)
					i2 = 1;
				else if (i2 > 255)
					i2 = 255;
				myPixels[i1] = (k1 << 16) + (l1 << 8) + i2;
			}
		}
	}

	public void drawOpaque(int i, int j) {
		i += drawOffsetX;
		j += drawOffsetY;
		int l = i + j * DrawingArea.width;
		int i1 = 0;
		int j1 = myHeight;
		int k1 = myWidth;
		int l1 = DrawingArea.width - k1;
		int i2 = 0;
		if (j < DrawingArea.topY) {
			int j2 = DrawingArea.topY - j;
			j1 -= j2;
			j = DrawingArea.topY;
			i1 += j2 * k1;
			l += j2 * DrawingArea.width;
		}
		if (j + j1 > DrawingArea.bottomY)
			j1 -= (j + j1) - DrawingArea.bottomY;
		if (i < DrawingArea.topX) {
			int k2 = DrawingArea.topX - i;
			k1 -= k2;
			i = DrawingArea.topX;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if (i + k1 > DrawingArea.bottomX) {
			int l2 = (i + k1) - DrawingArea.bottomX;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if (k1 <= 0 || j1 <= 0) {
		} else {
			copyPixelsOpaque(l, k1, j1, i2, i1, l1, myPixels, DrawingArea.pixels);
		}
	}

	private void copyPixelsOpaque(int i, int j, int k, int l, int i1, int k1, int[] ai, int[] ai1) {
		int l1 = -(j >> 2);
		j = -(j & 3);
		for (int i2 = -k; i2 < 0; i2++) {
			for (int j2 = l1; j2 < 0; j2++) {
				ai1[i++] = ai[i1++];
				ai1[i++] = ai[i1++];
				ai1[i++] = ai[i1++];
				ai1[i++] = ai[i1++];
			}

			for (int k2 = j; k2 < 0; k2++)
				ai1[i++] = ai[i1++];

			i += k1;
			i1 += l;
		}
	}

	public void drawSprite1(int i, int j) {
		drawSprite1(i, j, 128, false);
	}

	public void drawSprite1(int i, int j, int k) {
		drawSprite1(i, j, k, false);
	}

	public void drawSprite1(int i, int j, int k, boolean overrideCanvas) {
		i += drawOffsetX;
		j += drawOffsetY;
		int i1 = i + j * DrawingArea.width;
		int j1 = 0;
		int k1 = myHeight;
		int l1 = myWidth;
		int i2 = DrawingArea.width - l1;
		int j2 = 0;
		if (!(overrideCanvas && j > 0) && j < DrawingArea.topY) {
			int k2 = DrawingArea.topY - j;
			k1 -= k2;
			j = DrawingArea.topY;
			j1 += k2 * l1;
			i1 += k2 * DrawingArea.width;
		}
		if (j + k1 > DrawingArea.bottomY)
			k1 -= (j + k1) - DrawingArea.bottomY;
		if (!overrideCanvas && i < DrawingArea.topX) {
			int l2 = DrawingArea.topX - i;
			l1 -= l2;
			i = DrawingArea.topX;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if (i + l1 > DrawingArea.bottomX) {
			int i3 = (i + l1) - DrawingArea.bottomX;
			l1 -= i3;
			j2 += i3;
			i2 += i3;
		}
		if (!(l1 <= 0 || k1 <= 0)) {

			if (DrawingArea.pixels == null) {
				return;
			}
			if (myPixels == null) {
				return;
			}
			copyPixelsAlpha(j1, l1, DrawingArea.pixels, myPixels, j2, k1, i2, k, i1);
		}
	}

	public void drawSprite(int i, int k) {
		i += drawOffsetX;
		k += drawOffsetY;
		int l = i + k * DrawingArea.width;
		int i1 = 0;
		int j1 = myHeight;
		int k1 = myWidth;
		int l1 = DrawingArea.width - k1;
		int i2 = 0;
		if (k < DrawingArea.topY) {
			int j2 = DrawingArea.topY - k;
			j1 -= j2;
			k = DrawingArea.topY;
			i1 += j2 * k1;
			l += j2 * DrawingArea.width;
		}
		if (k + j1 > DrawingArea.bottomY)
			j1 -= (k + j1) - DrawingArea.bottomY;
		if (i < DrawingArea.topX) {
			int k2 = DrawingArea.topX - i;
			k1 -= k2;
			i = DrawingArea.topX;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if (i + k1 > DrawingArea.bottomX) {
			int l2 = (i + k1) - DrawingArea.bottomX;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if (!(k1 <= 0 || j1 <= 0)) {

			if (DrawingArea.pixels == null) {
				return;
			}
			if (myPixels == null) {
				return;
			}

			copyPixelsTransparent(DrawingArea.pixels, myPixels, i1, l, k1, j1, l1, i2);
		}
	}

	public void drawSprite(int i, int k, int color) {
		int tempWidth = myWidth + 2;
		int tempHeight = myHeight + 2;
		int[] tempArray = new int[tempWidth * tempHeight];
		for (int x = 0; x < myWidth; x++) {
			for (int y = 0; y < myHeight; y++) {
				if (myPixels[x + y * myWidth] != 0)
					tempArray[(x + 1) + (y + 1) * tempWidth] = myPixels[x + y * myWidth];
			}
		}
		for (int x = 0; x < tempWidth; x++) {
			for (int y = 0; y < tempHeight; y++) {
				if (tempArray[(x) + (y) * tempWidth] == 0) {
					if (x < tempWidth - 1 && tempArray[(x + 1) + ((y) * tempWidth)] > 0
							&& tempArray[(x + 1) + ((y) * tempWidth)] != 0xffffff) {
						tempArray[(x) + (y) * tempWidth] = color;
					}
					if (x > 0 && tempArray[(x - 1) + ((y) * tempWidth)] > 0
							&& tempArray[(x - 1) + ((y) * tempWidth)] != 0xffffff) {
						tempArray[(x) + (y) * tempWidth] = color;
					}
					if (y < tempHeight - 1 && tempArray[(x) + ((y + 1) * tempWidth)] > 0
							&& tempArray[(x) + ((y + 1) * tempWidth)] != 0xffffff) {
						tempArray[(x) + (y) * tempWidth] = color;
					}
					if (y > 0 && tempArray[(x) + ((y - 1) * tempWidth)] > 0
							&& tempArray[(x) + ((y - 1) * tempWidth)] != 0xffffff) {
						tempArray[(x) + (y) * tempWidth] = color;
					}
				}
			}
		}
		i--;
		k--;
		i += drawOffsetX;
		k += drawOffsetY;
		int l = i + k * DrawingArea.width;
		int i1 = 0;
		int j1 = tempHeight;
		int k1 = tempWidth;
		int l1 = DrawingArea.width - k1;
		int i2 = 0;
		if (k < DrawingArea.topY) {
			int j2 = DrawingArea.topY - k;
			j1 -= j2;
			k = DrawingArea.topY;
			i1 += j2 * k1;
			l += j2 * DrawingArea.width;
		}
		if (k + j1 > DrawingArea.bottomY) {
			j1 -= (k + j1) - DrawingArea.bottomY;
		}
		if (i < DrawingArea.topX) {
			int k2 = DrawingArea.topX - i;
			k1 -= k2;
			i = DrawingArea.topX;
			i1 += k2;
			l += k2;
			i2 += k2;
			l1 += k2;
		}
		if (i + k1 > DrawingArea.bottomX) {
			int l2 = (i + k1) - DrawingArea.bottomX;
			k1 -= l2;
			i2 += l2;
			l1 += l2;
		}
		if (!(k1 <= 0 || j1 <= 0)) {

			if (DrawingArea.pixels == null) {
				return;
			}
			if (myPixels == null) {
				return;
			}
			copyPixelsTransparent(DrawingArea.pixels, tempArray, i1, l, k1, j1, l1, i2);
		}
	}

	private void copyPixelsTransparent(int[] ai, int[] ai1, int j, int k, int l, int i1, int j1, int k1) {
		int i;
		int l1 = -(l >> 2);
		l = -(l & 3);
		for (int i2 = -i1; i2 < 0; i2++) {
			for (int j2 = l1; j2 < 0; j2++) {
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
			}

			for (int k2 = l; k2 < 0; k2++) {
				i = ai1[j++];
				if (i != 0 && i != -1) {
					ai[k++] = i;
				} else {
					k++;
				}
			}
			k += j1;
			j += k1;
		}
	}

	private void copyPixelsAlpha(int i, int j, int[] ai, int[] ai1, int l, int i1, int j1, int k1, int l1) {
		int k;
		int j2 = 256 - k1;
		for (int k2 = -i1; k2 < 0; k2++) {
			for (int l2 = -j; l2 < 0; l2++) {
				k = ai1[i++];
				if (k != 0) {
					int i3 = ai[l1];
					ai[l1++] = ((k & 0xff00ff) * k1 + (i3 & 0xff00ff) * j2 & 0xff00ff00)
							+ ((k & 0xff00) * k1 + (i3 & 0xff00) * j2 & 0xff0000) >> 8;
				} else {
					l1++;
				}
			}

			l1 += j1;
			i += l;
		}
	}

	public void drawRotatedSpriteAt(int drawX, int drawY, int angle, int zoom) {
		int size = 33;
		int center = size / 2;

		int spriteOffsetX = 9;
		int spriteOffsetY = 10;

		int sin = (int)(Math.sin(angle / 326.11) * 65536.0);
		int cos = (int)(Math.cos(angle / 326.11) * 65536.0);

		sin = sin * zoom >> 8;
		cos = cos * zoom >> 8;

		int radiusSquared = center * center;

		for (int y = -center; y < center; y++) {
			int baseU = y * sin;
			int baseV = y * cos;

			int destY = drawY + center + y;
			if (destY < 0 || destY >= DrawingArea.height) continue;

			for (int x = -center; x < center; x++) {
				if ((x * x + y * y) > radiusSquared) continue;

				int u = (x * cos + baseU) >> 16;
				int v = (baseV - x * sin) >> 16;

				int srcX = u + center + spriteOffsetX;
				int srcY = v + center + spriteOffsetY;

				if (srcX < 0 || srcX >= myWidth || srcY < 0 || srcY >= myHeight) continue;

				int color = myPixels[srcX + srcY * myWidth];
				if (color == 0) continue;

				int destX = drawX + center + x;
				if (destX < 0 || destX >= DrawingArea.width) continue;

				DrawingArea.pixels[destX + destY * DrawingArea.width] = color;
			}
		}
	}

	public void drawARGBSprite(int xPos, int yPos) {
		drawARGBSprite(xPos, yPos, 256);
	}

	public void drawARGBSprite(int xPos, int yPos, int alpha) {
		xPos += drawOffsetX;
		yPos += drawOffsetY;
		int i1 = xPos + yPos * DrawingArea.width;
		int j1 = 0;
		int spriteHeight = myHeight;
		int spriteWidth = myWidth;
		int i2 = DrawingArea.width - spriteWidth;
		int j2 = 0;
		if (yPos < DrawingArea.topY) {
			int k2 = DrawingArea.topY - yPos;
			spriteHeight -= k2;
			yPos = DrawingArea.topY;
			j1 += k2 * spriteWidth;
			i1 += k2 * DrawingArea.width;
		}
		if (yPos + spriteHeight > DrawingArea.bottomY)
			spriteHeight -= (yPos + spriteHeight) - DrawingArea.bottomY;
		if (xPos < DrawingArea.topX) {
			int l2 = DrawingArea.topX - xPos;
			spriteWidth -= l2;
			xPos = DrawingArea.topX;
			j1 += l2;
			i1 += l2;
			j2 += l2;
			i2 += l2;
		}
		if (xPos + spriteWidth > DrawingArea.bottomX) {
			int i3 = (xPos + spriteWidth) - DrawingArea.bottomX;
			spriteWidth -= i3;
			j2 += i3;
			i2 += i3;
		}
		if (!(spriteWidth <= 0 || spriteHeight <= 0)) {
			renderARGBPixels(spriteWidth, spriteHeight, myPixels, DrawingArea.pixels, i1, alpha, j1, j2, i2);
		}
	}

	private void renderARGBPixels(int spriteWidth, int spriteHeight, int[] spritePixels, int[] renderAreaPixels,
								  int pixel, int alphaValue, int i, int l, int j1) {
		int pixelColor;
		int alphaLevel;
		int alpha = alphaValue;
		for (int height = -spriteHeight; height < 0; height++) {
			for (int width = -spriteWidth; width < 0; width++) {
				alphaValue = ((myPixels[i] >> 24) & (alpha - 1));
				alphaLevel = 256 - alphaValue;
				if (alphaLevel > 256) {
					alphaValue = 0;
				}
				if (alpha == 0) {
					alphaLevel = 256;
					alphaValue = 0;
				}
				pixelColor = spritePixels[i++];
				if (pixelColor != 0) {
					int pixelValue = renderAreaPixels[pixel];
					renderAreaPixels[pixel++] = ((pixelColor & 0xff00ff) * alphaValue
							+ (pixelValue & 0xff00ff) * alphaLevel & 0xff00ff00)
							+ ((pixelColor & 0xff00) * alphaValue + (pixelValue & 0xff00) * alphaLevel & 0xff0000) >> 8;
				} else {
					pixel++;
				}
			}
			pixel += j1;
			i += l;
		}
	}

}
