package me.byteful.plugin.dropcrates.crate;

import me.byteful.plugin.dropcrates.DropCratesPlugin;
import me.byteful.plugin.dropcrates.util.BlockLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redempt.redlib.misc.FormatUtils;

import java.util.concurrent.TimeUnit;

public class CrateSpawnRunnable extends BukkitRunnable {
  private final DropCratesPlugin plugin;

  public CrateSpawnRunnable(DropCratesPlugin plugin) {
    this.plugin = plugin;
  }

  public static void scheduleForConfiguredTime(DropCratesPlugin plugin) {
    if (!plugin.shouldSpawnCrates()) {
      plugin.getLogger().severe("An error has caused DropCrates to pause crate spawning. Please restart/reload the server after fixing this issue.");

      return;
    }

    final TimeUnit unit = TimeUnit.valueOf(plugin.getConfig().getString("delay.unit").toUpperCase().replace(" ", "_"));
    final long amount = plugin.getConfig().getLong("delay.time");
    new CrateSpawnRunnable(plugin).runTaskLater(plugin, 20L * unit.toSeconds(amount));
    plugin.getLogger().info("Scheduled crate spawn time for " + amount + " " + unit.name() + ".");
  }

  private static void broadcast(String message) {
    for (Player plr : Bukkit.getOnlinePlayers()) {
      plr.sendMessage(FormatUtils.color(message));
    }
  }

  @Override
  public void run() {
    if (Bukkit.getOnlinePlayers().size() == 0) {
      scheduleForConfiguredTime(plugin);

      return;
    }

    if (!plugin.getCrateManager().isLastDropLooted()) {
      scheduleForConfiguredTime(plugin);
      final BlockLocation loc = plugin.getCrateManager().getLastCrate().getLocation();
      for (String msg : plugin.getConfig().getStringList("messages.crate_exists_announcement")) {
        broadcast(msg.replace("{x}", "" + loc.getX()).replace("{y}", "" + loc.getY()).replace("{z}", "" + loc.getZ()));
      }

      return;
    }

    final int minutes = plugin.getConfig().getInt("crate_drop_delay");

    for (String msg : plugin.getConfig().getStringList("messages.pre_drop_announcement")) {
      broadcast(msg.replace("{minutes}", "" + minutes));
    }

    new BukkitRunnable() {
      @Override
      public void run() {
        // 15 minutes have passed, spawn crate
        plugin.getCrateManager().dropCrate(plugin, Bukkit.getWorld("world"));
        final BlockLocation loc = plugin.getCrateManager().getLastCrate().getLocation();
        for (String msg : plugin.getConfig().getStringList("messages.crate_dropped_announcement")) {
          broadcast(msg.replace("{x}", "" + loc.getX()).replace("{y}", "" + loc.getY()).replace("{z}", "" + loc.getZ()));
        }
        scheduleForConfiguredTime(plugin);
      }
    }.runTaskLater(plugin, 20L * 60L * minutes);
  }
}