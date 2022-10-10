package me.byteful.plugin.dropcrates.loot;

import me.byteful.plugin.dropcrates.DropCratesPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.misc.WeightedRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LootPool {
  private final int min, max;
  //private final List<LootItem> items = new ArrayList<>();
  private final WeightedRandom<LootItem> items = new WeightedRandom<>();

  public LootPool(DropCratesPlugin plugin, ConfigurationSection cs) {
    this.min = cs.getInt("rolls.min");
    this.max = cs.getInt("rolls.max");
    cs.getConfigurationSection("items").getValues(false).forEach((item, data) -> {
      if (data instanceof ConfigurationSection) {
        //this.items.add(new LootItem((ConfigurationSection) data));
        LootItem lootItem;
        try {
          lootItem = new LootItem(plugin, (ConfigurationSection) data);
        } catch (RuntimeException ignored) {
          return;
        }
        this.items.set(lootItem, ((ConfigurationSection) data).getDouble("weight"));
      }
    });
  }

  public List<ItemStack> getItems() {
    final List<ItemStack> list = new ArrayList<>();
    final int rolls = ThreadLocalRandom.current().nextInt(min, max + 1);
    for (int i = 0; i < rolls; i++) {
      list.add(items.roll().buildItemStack());
    }
    return list;
  }
}