package net.hynse.artify;

import java.util.HashMap;
import java.util.Map;

public class CustomPainting {
    private final int width;
    private final int height;
    private final int[] mapIds;

    public CustomPainting(int width, int height, int[] mapIds) {
        this.width = width;
        this.height = height;
        this.mapIds = mapIds;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getMapIds() {
        return mapIds;
    }

    // Define your custom paintings here
    public static final Map<Integer, CustomPainting> PAINTINGS = new HashMap<>();

    static {
        PAINTINGS.put(1, new CustomPainting(1, 1, new int[]{1}));
        PAINTINGS.put(2, new CustomPainting(2, 1, new int[]{2, 3}));
        PAINTINGS.put(3, new CustomPainting(2, 2, new int[]{4, 5, 6, 7}));
        // Add more paintings as needed
    }
}
