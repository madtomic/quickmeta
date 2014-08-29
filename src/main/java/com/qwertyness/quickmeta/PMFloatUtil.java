package com.qwertyness.quickmeta;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme.PlotManager;

public class PMFloatUtil {

	public static boolean pmEligible(Location loc, Player player) {
		if (PlotManager.isPlotWorld(loc)) {
			if (!PlotManager.getPlotById(loc).owner.equalsIgnoreCase(player.getName()) && !PlotManager.getPlotById(loc).isAllowed(player.getName())) {
				player.sendMessage(ChatColor.RED + "You cannot use tools outside of your plots!");
				return false;
			}
			/*
			 * 1.8 UUID code
			if (!plot.ownerId == player.getUniqueId() && !plot.isAllowed(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED + "You cannot use the " + ChatColor.GOLD + "Meta Tool" + ChatColor.RED + " outside of your plots!");
				return false;
			}
			*/
			
		}
		return true;
	}
}
