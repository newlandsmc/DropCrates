package me.byteful.plugin.dropcrates.crate;

import me.byteful.plugin.dropcrates.util.BlockLocation;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DroppedCrate {
  private final BlockLocation location;
  private final List<BlockLocation> chestLocations = new ArrayList<>();
  private boolean isLooted = false;

  public DroppedCrate(BlockLocation location) {
    this.location = location;
  }

  public BlockLocation getLocation() {
    return location;
  }

  public List<BlockLocation> getChestLocations() {
    return chestLocations;
  }

  public boolean hasChest(Location location) {
    return chestLocations.stream().map(BlockLocation::toBukkitLocation).anyMatch(loc -> loc.equals(location));
  }

  public boolean isLooted() {
    return isLooted;
  }

  public void setLooted(boolean looted) {
    isLooted = looted;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DroppedCrate that = (DroppedCrate) o;
    return isLooted == that.isLooted && Objects.equals(location, that.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(location, isLooted);
  }

  @Override
  public String toString() {
    return "DroppedCrate{" +
      "location=" + location +
      ", isLooted=" + isLooted +
      '}';
  }
}