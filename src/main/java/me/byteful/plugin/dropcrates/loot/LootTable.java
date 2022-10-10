package me.byteful.plugin.dropcrates.loot;

import me.byteful.plugin.dropcrates.DropCratesPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LootTable {
  private final String name;
  private final Set<LootPool> pools = new HashSet<>();

  public LootTable(DropCratesPlugin plugin, File file) {
    this.name = file.getName();
    final YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
    config.getValues(false).forEach((pool, data) -> {
      if (data instanceof ConfigurationSection) {
        pools.add(new LootPool(plugin, (ConfigurationSection) data));
        plugin.getLogger().info("Loaded lootpool: " + pool);
      }
    });
  }

  public List<ItemStack> buildItemList() {
    final List<ItemStack> list = new ArrayList<>();

    for (LootPool pool : pools) {
      list.addAll(pool.getItems());
    }

    return list;
  }

  public String getName() {
    return name;
  }
}