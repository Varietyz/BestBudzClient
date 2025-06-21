package com.bestbudz.cache;

import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.data.items.ItemDef;
import static com.bestbudz.data.items.ItemDef.getSprite;
import static com.bestbudz.entity.pets.PetItemCreator.totalItemsAvailable;
import com.bestbudz.graphics.sprite.Sprite;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SpriteDumper
{
	public static void dumpItemSprites() {
		System.out.println("Starting item sprite dump...");
		int successCount = 0;
		int skipCount = 0;
		int errorCount = 0;
		int alreadyExistsCount = 0;

		int maxItemId = Math.max(ItemDef.totalItems, totalItemsAvailable); // Extend to include pet items
		for (int id = 0; id < maxItemId; id++) {
			try {
				ItemDef item = getItemDefinition(id);
				if (item == null) {
					System.out.println("Skipping item ID " + id + ": ItemDef is null");
					skipCount++;
					continue;
				}

				Sprite sprite = getSprite(id, 1, 100); // Adjust parameters as needed
				if (sprite == null) {
					System.out.println("Skipping item ID " + id + ": Sprite is null");
					skipCount++;
					continue;
				}

				String filename = "dump/items/" + id + ".png";

				// Check if file already exists
				File outputFile = new File(filename);
				if (outputFile.exists()) {
					System.out.println("Skipping item ID " + id + ": File already exists");
					alreadyExistsCount++;
					continue;
				}
				boolean success = saveSpriteAsImage(sprite, filename);

				if (success) {
					successCount++;
					if (successCount % 100 == 0) { // Progress update every 100 items
						System.out.println("Progress: " + successCount + " sprites saved so far...");
					}
				} else {
					errorCount++;
				}

			} catch (Exception e) {
				System.err.println("Error processing item ID " + id + ": " + e.getMessage());
				e.printStackTrace();
				errorCount++;
			}
		}

		System.out.println("Item sprite dump completed!");
		System.out.println("Successfully saved: " + successCount + " sprites");
		System.out.println("Skipped: " + skipCount + " items");
		System.out.println("Already existed: " + alreadyExistsCount + " items");
		System.out.println("LoadingErrorScreen: " + errorCount + " items");
	}

	public static boolean saveSpriteAsImage(Sprite sprite, String path) {
		try {
			System.out.println("Saving sprite: " + path + " (Size: " + sprite.myWidth + "x" + sprite.myHeight + ")");

			// Validate sprite dimensions
			if (sprite.myWidth <= 0 || sprite.myHeight <= 0) {
				System.err.println("Invalid sprite dimensions: " + sprite.myWidth + "x" + sprite.myHeight);
				return false;
			}

			if (sprite.myPixels == null) {
				System.err.println("Sprite pixels array is null for: " + path);
				return false;
			}

			if (sprite.myPixels.length != sprite.myWidth * sprite.myHeight) {
				System.err.println("Pixel array length mismatch. Expected: " +
					(sprite.myWidth * sprite.myHeight) + ", Got: " + sprite.myPixels.length);
				return false;
			}

			BufferedImage image = new BufferedImage(sprite.myWidth, sprite.myHeight, BufferedImage.TYPE_INT_ARGB);

			int transparentPixels = 0;
			int opaquePixels = 0;

			for (int x = 0; x < sprite.myWidth; x++) {
				for (int y = 0; y < sprite.myHeight; y++) {
					int pixelIndex = x + y * sprite.myWidth;
					int pixel = sprite.myPixels[pixelIndex];

					// Make pixel 0 transparent, keep others opaque
					if (pixel == 0) {
						image.setRGB(x, y, 0x00000000); // Fully transparent
						transparentPixels++;
					} else {
						// Ensure alpha channel is fully opaque for non-zero pixels
						int rgbaPixel = pixel | 0xFF000000;
						image.setRGB(x, y, rgbaPixel);
						opaquePixels++;
					}
				}
			}

			// Create output directory
			File output = new File(path);
			File parentDir = output.getParentFile();
			if (parentDir != null && !parentDir.exists()) {
				boolean dirsCreated = parentDir.mkdirs();
				if (!dirsCreated && !parentDir.exists()) {
					System.err.println("Failed to create directories for: " + path);
					return false;
				}
			}

			// Write the image
			boolean writeSuccess = ImageIO.write(image, "png", output);
			if (!writeSuccess) {
				System.err.println("Failed to write image: " + path);
				return false;
			}

			System.out.println("Successfully saved: " + path +
				" (Transparent: " + transparentPixels + ", Opaque: " + opaquePixels + ")");
			return true;

		} catch (Exception e) {
			System.err.println("Error saving sprite to " + path + ": " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
