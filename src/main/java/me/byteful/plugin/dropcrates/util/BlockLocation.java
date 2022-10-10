package me.byteful.plugin.dropcrates.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Objects;

// Serializable to JSON without any issues unlike Bukkit's location.
// Also, immutable and uses integers for direct block compatibility.
public final class BlockLocation {
  private final String world;
  private final int x, y, z;

  public BlockLocation(String world, int x, int y, int z) {
    this.world = world;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public BlockLocation(Location loc) {
    this(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
  }

  public String getWorld() {
    return world;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getZ() {
    return z;
  }

  public Location toBukkitLocation() {
    return new Location(Bukkit.getWorld(world), x, y, z);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BlockLocation that = (BlockLocation) o;
    return x == that.x && y == that.y && z == that.z && Objects.equals(world, that.world);
  }

  @Override
  public int hashCode() {
    return Objects.hash(world, x, y, z);
  }

  @Override
  public String toString() {
    return "BlockLocation{" +
      "world='" + world + '\'' +
      ", x=" + x +
      ", y=" + y +
      ", z=" + z +
      '}';
  }
}