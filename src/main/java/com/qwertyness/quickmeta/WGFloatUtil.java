package com.qwertyness.quickmeta;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.BukkitPlayer;

public class WGFloatUtil {
	public static boolean wgEligible(QuickMeta plugin, Player player, Location loc) {
		if (!plugin.getWGPlugin().getGlobalRegionManager().get(loc.getWorld()).getApplicableRegions(loc).canBuild(new BukkitPlayer(plugin.getWGPlugin(), player))) {
			player.sendMessage(ChatColor.RED + "You cannot use tools in a protected WorldGuard region!");
			return false;
		}
		return true;
	}
}
