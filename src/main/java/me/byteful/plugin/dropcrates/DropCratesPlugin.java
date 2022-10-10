package me.byteful.plugin.dropcrates;

import me.byteful.lib.ppd.PersistentPluginData;
import me.byteful.lib.ppd.StorageLocation;
import me.byteful.plugin.dropcrates.crate.CrateManager;
import me.byteful.plugin.dropcrates.crate.CrateSpawnRunnable;
import me.byteful.plugin.dropcrates.loot.LootTableManager;
import me.byteful.plugin.dropcrates.schematic.SchematicManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class DropCratesPlugin extends JavaPlugin {
  private SchematicManager schematicManager;
  private CrateManager crateManager;
  private PersistentPluginData data;
  private LootTableManager lootTableManager;
  private boolean shouldSpawnCrates = true;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    schematicManager = new SchematicManager(this);
    lootTableManager = new LootTableManager(this);
    data = PersistentPluginData.initialize(this, "data", StorageLocation.INSIDE_PLUGIN_FOLDER, true);
    crateManager = new CrateManager(data, schematicManager, lootTableManager);
    Bukkit.getPluginManager().registerEvents(crateManager, this);

    CrateSpawnRunnable.scheduleForConfiguredTime(this);
  }

  @Override
  public void onDisable() {
    try {
      if (data != null) {
        data.save();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public CrateManager getCrateManager() {
    return crateManager;
  }

  public SchematicManager getSchematicManager() {
    return schematicManager;
  }

  public PersistentPluginData getData() {
    return data;
  }

  public LootTableManager getLootTableManager() {
    return lootTableManager;
  }

  public void setShouldSpawnCrates(boolean bool) {
    this.shouldSpawnCrates = bool;
  }

  public boolean shouldSpawnCrates() {
    return shouldSpawnCrates;
  }
}
