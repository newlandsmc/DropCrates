package me.byteful.plugin.dropcrates.schematic.impl;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import me.byteful.plugin.dropcrates.schematic.Schematic;
import org.bukkit.Location;
import redempt.redlib.region.CuboidRegion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class WorldEditSchematic implements Schematic {
  private final Clipboard clipboard;
  private final String name;

  public WorldEditSchematic(File file) {
    this.name = file.getName();
    final ClipboardFormat format = ClipboardFormats.findByFile(file);
    try {
      this.clipboard = format.getReader(new FileInputStream(file)).read();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public CuboidRegion paste(Location location) {
    final World world = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
    final com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);
    final EditSession session = WorldEdit.getInstance().newEditSession(world);
    final Operation op = new ClipboardHolder(clipboard).createPaste(session).to(loc.toVector().toBlockPoint()).ignoreAirBlocks(false).build();

    try {
      Operations.complete(op);
      session.close();
    } catch (WorldEditException e) {
      throw new RuntimeException(e);
    }

    final Region region = clipboard.getRegion();
    final BlockVector3 offset = region.getMinimumPoint().subtract(clipboard.getOrigin());
    final Vector3 realMin = loc.toVector().add(offset.toVector3());
    final Vector3 realMax = realMin.add(region.getMaximumPoint().subtract(region.getMinimumPoint()).toVector3()).add(1, 0, 1);

    System.out.println("MIN POINT: " + realMin);
    System.out.println("MAX POINT: " + realMax);

    return new CuboidRegion(new Location(location.getWorld(), realMin.getX(), realMin.getY(), realMin.getZ()), new Location(location.getWorld(), realMax.getX(), realMax.getY(), realMax.getZ()));
  }
}