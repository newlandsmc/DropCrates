package me.byteful.plugin.dropcrates.schematic;

import me.byteful.plugin.dropcrates.DropCratesPlugin;
import me.byteful.plugin.dropcrates.schematic.impl.WorldEditSchematic;
import redempt.redlib.misc.WeightedRandom;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class SchematicManager {
  private final WeightedRandom<Schematic> schematics = new WeightedRandom<>();

  public SchematicManager(DropCratesPlugin plugin) {
    final File folder = new File(plugin.getDataFolder(), "schematics");
    if (!folder.exists()) {
      folder.mkdirs();
    }
    for (String schemData : plugin.getConfig().getStringList("schematics")) {
      final String[] split = schemData.split(",");
      final File file = new File(folder, split[0]); //plugin.getDataFolder().toPath().resolve("schematics").resolve(split[0]).toFile();
      if (!file.exists()) {
        final InputStream resource = plugin.getResource("schematics/" + file.getName());
        if (resource != null) {
          try {
            Files.copy(resource, file.toPath());
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        } else {
          plugin.getLogger().warning("Failed to find file: schematics/" + file.getName());

          continue;
        }
      }
      // TODO if more implementations added, use if/else or switch case to select correct impl
      final WorldEditSchematic schem = new WorldEditSchematic(file);
      schematics.set(schem, Double.parseDouble(split[1]));
      plugin.getLogger().info("Loaded schematic: " + split[0]);
    }

    if (schematics.getWeights().size() == 0) {
      plugin.getLogger().severe("No schematics were registered! DropCrates will pause crate spawning until this issue is sorted out.");
      plugin.setShouldSpawnCrates(false);
    }
  }

  public Schematic getSchematic() {
    return schematics.roll();
  }
}