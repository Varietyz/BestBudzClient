import graphics.LightingSystem;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class EnvironmentalLighting {
    public static final int TORCH_LIGHT = 0;
    public static final int FIRE_LIGHT = 1;
    public static final int SPELL_LIGHT = 2;
    public static final int ITEM_LIGHT = 3;
    
    private static final Map<Integer, LightProperties> LIGHT_TYPES = new HashMap<>();
    static {
        LIGHT_TYPES.put(TORCH_LIGHT, new LightProperties(new Color(255, 200, 100), 100f, 0.6f));
        LIGHT_TYPES.put(FIRE_LIGHT, new LightProperties(new Color(255, 150, 50), 150f, 0.8f));
        LIGHT_TYPES.put(SPELL_LIGHT, new LightProperties(new Color(100, 200, 255), 80f, 0.7f));
        LIGHT_TYPES.put(ITEM_LIGHT, new LightProperties(new Color(200, 200, 255), 50f, 0.5f));
    }
    
    public static class LightProperties {
        public final Color color;
        public final float radius;
        public final float intensity;
        
        public LightProperties(Color color, float radius, float intensity) {
            this.color = color;
            this.radius = radius;
            this.intensity = intensity;
        }
    }
    
    public static LightProperties getLightProperties(int type) {
        return LIGHT_TYPES.get(type);
    }
    
    public static void addLightToSystem(LightingSystem lighting, int type, float x, float y) {
        LightProperties props = LIGHT_TYPES.get(type);
        if (props != null) {
            lighting.addLight(x, y, props.radius, props.color, props.intensity);
        }
    }
    
    public static void addCustomLight(LightingSystem lighting, float x, float y, 
                                    Color color, float radius, float intensity) {
        lighting.addLight(x, y, radius, color, intensity);
    }
} 