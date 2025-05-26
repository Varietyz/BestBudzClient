import java.awt.Color;

/**
 * 
 * @author Zion
 *
 */
public class FogHandler {

	private int fogColor = 0xB3B281; // Default fog color
	private int nightColor = 0x000014; // Dark blue for night
	private int dawnColor = 0x645046; // Orange tint for dawn/dusk
	private float fogIntensity = 0.0f;
	private int fogStartDistance = 2000;
	private int fogEndDistance = 3000;
	
	public void updateFog() {
		// Calculate time of day (24-hour cycle)
		int timeOfDay = (int)((System.currentTimeMillis() / 60000) % 2400);
		
		// Night time (8 PM - 6 AM)
		if (timeOfDay >= 2000 || timeOfDay <= 600) {
			fogColor = nightColor;
			fogIntensity = 0.5f; // 50% darkness
		}
		// Dawn/Dusk
		else if (timeOfDay >= 1800 || timeOfDay <= 800) {
			fogColor = dawnColor;
			fogIntensity = 0.25f; // 25% darkness
		}
		// Day time
		else {
			fogColor = 0xB3B281;
			fogIntensity = 0.0f;
		}
	}
	
	public Color getFogColor() {
		return new Color(
			(fogColor >> 16) & 0xFF,
			(fogColor >> 8) & 0xFF,
			fogColor & 0xFF,
			(int)(fogIntensity * 255)
		);
	}
	
	public void renderFog(int[] colorBuffer, float[] depthBuffer) {
		for (int pixel = 0; pixel < colorBuffer.length; pixel++) {
			if (depthBuffer[pixel] >= fogEndDistance) {
				colorBuffer[pixel] = fogColor;
			} else if (depthBuffer[pixel] >= fogStartDistance) {
				float fogAmount = (depthBuffer[pixel] - fogStartDistance) / (fogEndDistance - fogStartDistance);
				fogAmount *= fogIntensity;
				colorBuffer[pixel] = FogUtil.mix(colorBuffer[pixel], fogColor, fogAmount);
			}
		}
	}
}