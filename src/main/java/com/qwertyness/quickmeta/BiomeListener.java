package com.qwertyness.quickmeta;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BiomeListener implements Listener {

	private QuickMeta plugin;
	
	public BiomeListener(QuickMeta plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getPlayer().isSneaking()) {
			return;
		}
		if (!this.isEligible(event.getPlayer(), event)) {
			return;
		}
		
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (!this.canChangeBlock(event.getPlayer(), event.getClickedBlock().getLocation())) {
				return;
			}
			String name = event.getPlayer().getItemInHand().getItemMeta().getDisplayName();
			Biome biome = Biome.valueOf(name.substring(name.indexOf("-") + 2));
			Block block = event.getClickedBlock();
			block.getWorld().setBiome(block.getLocation().getBlockX(), block.getLocation().getBlockZ(), biome);
			block.getWorld().refreshChunk(block.getChunk().getX(), block.getChunk().getZ());
		}
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Biome[] biomes = Biome.values();
			String name = event.getPlayer().getItemInHand().getItemMeta().getDisplayName();
			Biome currentBiome = Biome.valueOf(name.substring(name.indexOf("-") + 2));
			int currentIndex = Arrays.asList(biomes).indexOf(currentBiome);
			ItemStack tool = event.getPlayer().getItemInHand();
			ItemMeta meta = tool.getItemMeta();
			if (Biome.values().length-1 == currentIndex) {
				meta.setDisplayName(ChatColor.GREEN + "BiomeTool - " + biomes[0].name());
			}
			else {
				meta.setDisplayName(ChatColor.GREEN + "BiomeTool - " + biomes[currentIndex + 1].name());
			}
			
			tool.setItemMeta(meta);
		}
		event.setCancelled(true);
	}
	
	@EventHandler
	public void biomeCopyandPaste(PlayerInteractEvent event) {
		Player player = (Player) event.getPlayer();
		if (!player.isSneaking()) {
			return;
		}
		if (!this.isEligible(player, event)) {
			return;
		}
		
		if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			ItemMeta im = player.getItemInHand().getItemMeta();
			im.setDisplayName(ChatColor.GREEN + "BiomeTool - " + block.getBiome().name());
			player.getItemInHand().setItemMeta(im);
		}
		event.setCancelled(true);
	}
	
	public boolean isEligible(Player player, PlayerInteractEvent event) {
		if (!player.hasPermission("quickmeta.biome.use")) {
			return false;
		}
		if (player.getItemInHand().getType() != plugin.getBiomeToolType() || !player.getItemInHand().getItemMeta().getDisplayName().contains("BiomeTool")) {
			return false;
		}
		if (!player.hasPermission("quickmeta.bypass.world")) {
			if (this.plugin.getDisabledWorlds().contains(player.getWorld().getName())) {
				player.sendMessage(ChatColor.RED + "You can not use the " + ChatColor.GOLD + "Biome Tool" + ChatColor.RED + " in this world!");
				return false;
			}
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (!player.hasPermission("quickmeta.bypass.block")) {
				if (this.plugin.getDisabledBlocks().contains(event.getClickedBlock())) {
					player.sendMessage(ChatColor.RED + "You cannot use " + ChatColor.GOLD + "Biome Tool" + ChatColor.RED +  " on this block!");
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean canChangeBlock(Player player, Location loc) {

		//WorldGuard Implementation
		if (this.plugin.usingWG) {
			if (!WGFloatUtil.wgEligible(this.plugin, player, loc)) {
				return false;
			}
		}
		
		//PlotMe Implementation
		if (this.plugin.usingPM) {
			if (!PMFloatUtil.pmEligible(loc, player)) {
				return false;
			}
		}
		return true;
	}
}
