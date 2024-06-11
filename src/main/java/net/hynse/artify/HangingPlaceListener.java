package net.hynse.artify;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HangingPlaceListener implements Listener {
    private final Plugin plugin;
    private final Random random;

    public HangingPlaceListener(Plugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onHangingPlace(HangingPlaceEvent event) {
        // Check if the placed entity is a Painting
        if (!(event.getEntity() instanceof Painting)) {
            return;
        }

        Painting paintingEntity = (Painting) event.getEntity();

        // Check for available space
        List<CustomPainting> fittingPaintings = getFittingPaintings(paintingEntity);
        if (fittingPaintings.isEmpty()) {
            return;
        }

        // Define the probability threshold (e.g., 50% chance)
        double probability = 0.5;
        if (random.nextDouble() > probability) {
            return;
        }

        // Randomly select a fitting painting
        CustomPainting selectedPainting = fittingPaintings.get(random.nextInt(fittingPaintings.size()));

        // Remove the painting entity (vanilla painting)
        paintingEntity.remove();

        // Place custom paintings using item frames
        placeCustomPainting(paintingEntity, selectedPainting);
    }

    private List<CustomPainting> getFittingPaintings(Painting paintingEntity) {
        List<CustomPainting> fittingPaintings = new ArrayList<>();
        for (CustomPainting painting : CustomPainting.PAINTINGS.values()) {
            if (hasEnoughSpace(paintingEntity, painting)) {
                fittingPaintings.add(painting);
            }
        }
        return fittingPaintings;
    }

    private boolean hasEnoughSpace(Painting paintingEntity, CustomPainting painting) {
    int width = painting.getWidth();
    int height = painting.getHeight();
    Block block = paintingEntity.getLocation().getBlock();
    BlockFace facing = paintingEntity.getFacing(); 

    // Check the surrounding blocks based on the painting size
    for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
            Block relative = block.getRelative(facing, x).getRelative(facing.getOppositeFace(), y); // Use BlockFace
            if (!relative.getType().isAir()) {
                return false;
            }
        }
    }

    return true;
}


    private void placeCustomPainting(Painting paintingEntity, CustomPainting painting) {
        int width = painting.getWidth();
        int height = painting.getHeight();
        Block block = paintingEntity.getLocation().getBlock();

        // Place item frames and set map items
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Block relative = block.getRelative(paintingEntity.getFacing(), x).getRelative(paintingEntity.getFacing().getOppositeFace(), y);
                ItemFrame itemFrame = (ItemFrame) paintingEntity.getWorld().spawnEntity(relative.getLocation(), org.bukkit.entity.EntityType.ITEM_FRAME);
                int mapId = painting.getMapIds()[(y * width) + x];

                ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
                MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();
                if (mapMeta != null) {
                    MapView mapView = Bukkit.getServer().getMap(mapId);
                    mapMeta.setMapView(mapView);
                    mapItem.setItemMeta(mapMeta);
                }

                itemFrame.setItem(mapItem);
                itemFrame.getPersistentDataContainer().set(new NamespacedKey(plugin, "custom_painting"), PersistentDataType.INTEGER, mapId);
            }
        }
    }
}