package me.byteful.plugin.dropcrates.crate;

import me.byteful.lib.ppd.PersistentPluginData;
import me.byteful.plugin.dropcrates.DropCratesPlugin;
import me.byteful.plugin.dropcrates.loot.LootTable;
import me.byteful.plugin.dropcrates.loot.LootTableManager;
import me.byteful.plugin.dropcrates.schematic.Schematic;
import me.byteful.plugin.dropcrates.schematic.SchematicManager;
import me.byteful.plugin.dropcrates.util.BlockLocation;
import me.byteful.plugin.dropcrates.util.RandomLocation;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.region.CuboidRegion;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class CrateManager implements Listener {
  private final PersistentPluginData data;
  private final SchematicManager schematicManager;
  private final LootTableManager lootTableManager;
  private DroppedCrate lastDroppedCrate;

  public CrateManager(PersistentPluginData data, SchematicManager schematicManager, LootTableManager lootTableManager) {
    this.data = data;
    this.schematicManager = schematicManager;
    this.lootTableManager = lootTableManager;
    if (data.exists("lastDroppedCrate")) {
      this.lastDroppedCrate = data.get("lastDroppedCrate", DroppedCrate.class).orElseThrow();
    }
  }

  public void dropCrate(DropCratesPlugin plugin, World world) {
    lastDroppedCrate = new DroppedCrate(new BlockLocation(RandomLocation.generateRandomLocation(plugin.getConfig().getString("rtp_profile"), world)));
    save();

    final Schematic schematic = schematicManager.getSchematic();
    final CuboidRegion region = schematic.paste(lastDroppedCrate.getLocation().toBukkitLocation());
    region.forEachBlock(block -> {
      if (block.getType() != Material.CHEST) {
        return;
      }

      plugin.getLogger().info("Found a chest in schematic: " + schematic.getName());
      final Chest chest = (Chest) block.getState();
      final Inventory inv = chest.getBlockInventory();
      inv.clear();
      final LootTable table = lootTableManager.getLootTable();
      List<ItemStack> items = table.buildItemList();
      if (items.size() > inv.getSize()) {
        items = items.subList(0, inv.getSize());
      }
      plugin.getLogger().info("Choosing loot table: " + table.getName() + " | with " + items.size() + " items");
      boolean[] chosen = new boolean[inv.getSize()];
      for (ItemStack item : items) {
        int slot;

        do {
          slot = ThreadLocalRandom.current().nextInt(inv.getSize());
        } while (chosen[slot]);

        chosen[slot] = true;
        inv.setItem(slot, item);
      }
      lastDroppedCrate.getChestLocations().add(new BlockLocation(block.getLocation()));
    });
    save();
  }

  public boolean isLastDropLooted() {
    return lastDroppedCrate == null || lastDroppedCrate.isLooted();
  }

  @EventHandler
  public void onChestLoot(PlayerInteractEvent event) {
    if (isLastDropLooted() || event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.hasBlock() || event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.CHEST) {
      return;
    }

    if (!lastDroppedCrate.hasChest(event.getClickedBlock().getLocation())) {
      return;
    }

    lastDroppedCrate.setLooted(true);
    save();
  }

  public DroppedCrate getLastCrate() {
    return lastDroppedCrate;
  }

  public void save() {
    data.set("lastDroppedCrate", lastDroppedCrate);
  }
}