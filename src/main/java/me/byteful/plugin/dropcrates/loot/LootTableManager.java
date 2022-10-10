package me.byteful.plugin.dropcrates.loot;

import me.byteful.plugin.dropcrates.DropCratesPlugin;
import redempt.redlib.misc.WeightedRandom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class LootTableManager {
  private final WeightedRandom<LootTable> tables = new WeightedRandom<>();

  public LootTableManager(DropCratesPlugin plugin) {
    final File folder = new File(plugin.getDataFolder(), "loottables");
    if (!folder.exists()) {
      folder.mkdirs();
    }
    for (String data : plugin.getConfig().getStringList("enabled_loottables")) {
      final String[] split = data.split(",");
      final String name = split[0];
      final File file = new File(folder, name.endsWith(".yml") ? name : name + ".yml"); //plugin.getDataFolder().toPath().resolve("loottables").resolve(name.endsWith(".yml") ? name : name + ".yml").toFile();
      if (!file.exists()) {
        final InputStream resource = plugin.getResource("loottables/" + file.getName());
        if (resource != null) {
          try {
            Files.copy(resource, file.toPath());
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        } else {
          plugin.getLogger().warning("Failed to find file: loottables/" + file.getName());

          continue;
        }
      }
      tables.set(new LootTable(plugin, file), Double.parseDouble(split[1]));
      plugin.getLogger().info("Loaded loottable: " + name);
    }

    if (tables.getWeights().size() == 0) {
      plugin.setShouldSpawnCrates(false);
      plugin.getLogger().severe("No loottables were registered! DropCrates will pause crate spawning until this issue is sorted out.");
    }
  }

  public LootTable getLootTable() {
    return tables.roll();
  }
}