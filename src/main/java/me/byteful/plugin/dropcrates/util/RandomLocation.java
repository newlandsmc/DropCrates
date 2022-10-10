package me.byteful.plugin.dropcrates.util;

import biz.donvi.jakesRTP.JakesRtpPlugin;
import biz.donvi.jakesRTP.RandomTeleporter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class RandomLocation {
  public static Location generateRandomLocation(String profileName, World world) {
    final RandomTeleporter teleporter = JavaPlugin.getPlugin(JakesRtpPlugin.class).getRandomTeleporter();
    try {
      return teleporter.getRtpLocation(teleporter.getRtpSettingsByName(profileName), world.getSpawnLocation(), true);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}