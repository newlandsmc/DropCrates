package me.byteful.plugin.dropcrates.schematic;

import org.bukkit.Location;
import redempt.redlib.region.CuboidRegion;

public interface Schematic {
  String getName();

  CuboidRegion paste(Location location);
}