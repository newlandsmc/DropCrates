package me.byteful.plugin.dropcrates.loot;

import me.byteful.plugin.dropcrates.DropCratesPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class LootItem {
  private final String id;
  private final double weight;
  private final Material item;
  private final int min, max;

  public LootItem(DropCratesPlugin plugin, ConfigurationSection cs) {
    this.id = cs.getName();
    this.weight = cs.getDouble("weight");
    this.item = Material.getMaterial(Objects.requireNonNull(cs.getString("item")));
    if (this.item == null) {
      plugin.getLogger().severe("Failed to load material for LootItem (" + this.id + ") | Material (" + cs.getString("item") + ")");
      plugin.getLogger().severe("Item has been skipped...");
      throw new RuntimeException();
    }
    this.min = cs.getInt("min");
    this.max = cs.getInt("max");
  }

  public String getId() {
    return id;
  }

  public double getWeight() {
    return weight;
  }

  public Material getItem() {
    return item;
  }

  public ItemStack buildItemStack() {
    return new ItemStack(item, ThreadLocalRandom.current().nextInt(min, max + 1));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LootItem lootItem = (LootItem) o;
    return Double.compare(lootItem.weight, weight) == 0 && Objects.equals(id, lootItem.id) && item == lootItem.item;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, weight, item);
  }

  @Override
  public String toString() {
    return "LootItem{" +
      "id='" + id + '\'' +
      ", weight=" + weight +
      ", item=" + item +
      '}';
  }
}