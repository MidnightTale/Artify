package net.hynse.artify;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
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

import java.util.Map;
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
        if (!(event.getEntity() instanceof Painting)) {
            return;
        }

        Painting paintingEntity = (Painting) event.getEntity();
        CustomPainting customPainting = getRandomCustomPainting();

        if (customPainting != null) {
            Block block = paintingEntity.getLocation().getBlock();
            BlockFace facing = paintingEntity.getFacing();

            // Find suitable location
            Block targetBlock = findTargetBlock(block, facing);

            // Place custom painting
            placeCustomPainting(customPainting, targetBlock, facing);
        }
    }

    private CustomPainting getRandomCustomPainting() {
        Map<Integer, CustomPainting> paintings = CustomPainting.PAINTINGS;
        if (paintings.isEmpty()) {
            return null;
        }

        int randomIndex = random.nextInt(paintings.size());
        return paintings.get(randomIndex + 1);
    }

    private Block findTargetBlock(Block block, BlockFace facing) {
        Block targetBlock = block.getRelative(facing.getOppositeFace());
        while (targetBlock.getType().isAir()) {
            targetBlock = targetBlock.getRelative(facing);
        }
        return targetBlock;
    }

    private void placeCustomPainting(CustomPainting customPainting, Block targetBlock, BlockFace facing) {
        for (int y = customPainting.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < customPainting.getWidth(); x++) {
                Block relative = targetBlock.getRelative(facing, x).getRelative(facing.getOppositeFace(), y);
                if (relative.getType().isAir() && isSpaceAvailable(relative)) {
                    // Place item frame
                    ItemFrame itemFrame = (ItemFrame) relative.getWorld().spawnEntity(relative.getLocation(), EntityType.ITEM_FRAME);
                    int mapId = customPainting.getMapIds()[(y * customPainting.getWidth()) + x];
                    setMapItem(itemFrame, mapId);
                    setPaintingData(itemFrame);
                }
            }
        }
    }

    private boolean isSpaceAvailable(Block block) {
        return block.getRelative(BlockFace.UP).getType().isAir() && block.getRelative(BlockFace.DOWN).getType().isSolid();
    }

    private void setMapItem(ItemFrame itemFrame, int mapId) {
        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();
        if (mapMeta != null) {
            MapView mapView = Bukkit.getServer().getMap(mapId);
            mapMeta.setMapView(mapView);
            mapItem.setItemMeta(mapMeta);
        }
        itemFrame.setItem(mapItem);
    }

    private void setPaintingData(ItemFrame itemFrame) {
        itemFrame.getPersistentDataContainer().set(new NamespacedKey(plugin, "custom_painting"), PersistentDataType.INTEGER, 1);
    }
}
