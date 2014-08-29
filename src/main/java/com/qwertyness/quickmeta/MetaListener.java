package com.qwertyness.quickmeta;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("deprecation")
public class MetaListener implements Listener {

	private QuickMeta plugin;
	private HashMap<String, Block> activeCopies = new HashMap<String, Block>();
	
	public MetaListener(QuickMeta plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.isSneaking()) {
			return;
		}
		if (!this.isEligible(player, event) || !player.hasPermission("quickmeta.meta.use.cycle")) {
			return;
		}
		if (!this.canChangeBlock(player, event.getClickedBlock().getLocation())) {
			return;
		}
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			event.getClickedBlock().setData((byte)this.getNextId(event.getClickedBlock().getData()));
		}
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			event.getClickedBlock().setData((byte)this.getPreviousId(event.getClickedBlock().getData()));
		}
		Block block = player.getTargetBlock(null, 5);
		ItemMeta im = player.getItemInHand().getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "MetaTool - " + block.getType().toString() + ":" + ChatColor.GOLD + block.getData());
		player.getItemInHand().setItemMeta(im);
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onSneakInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!player.isSneaking()) {
			return;
		}
		if (!this.isEligible(player, event) || !player.hasPermission("quickmeta.meta.use.copy")) {
			return;
		}
		
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			this.activeCopies.put(event.getPlayer().getName(), event.getClickedBlock());
		}
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (!this.canChangeBlock(player, event.getClickedBlock().getLocation())) {
				return;
			}
			try {
				event.getClickedBlock().setType(this.activeCopies.get(event.getPlayer().getName()).getType());
				event.getClickedBlock().setData(this.activeCopies.get(event.getPlayer().getName()).getData());
				Block block = player.getTargetBlock(null, 5);
				ItemMeta im = player.getItemInHand().getItemMeta();
				im.setDisplayName(ChatColor.GREEN + "MetaTool - " + block.getType().toString() + ":" + ChatColor.GOLD + block.getData());
				player.getItemInHand().setItemMeta(im);
				event.setCancelled(true);
			} catch(NullPointerException e) {
				player.sendMessage(ChatColor.RED + "You must make a MetaData selection first! Shift right click a block with this tool to select.");
			}
		}
		event.setCancelled(true);
	}
	@EventHandler
	public void paintingChange(PaintingBreakByEntityEvent event) {
		if (!(event.getRemover() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getRemover();
		Painting painting = event.getPainting();
		if (!player.hasPermission("quickmeta.meta.use.cycle")) {
			return;
		}
		if (player.getItemInHand().getType() != plugin.getMetaToolType() || !player.getItemInHand().getItemMeta().getDisplayName().contains("MetaTool")) {
			return;
		}
		if (!player.hasPermission("quickmeta.bypass.world")) {
			if (this.plugin.getDisabledWorlds().contains(player.getWorld().getName())) {
				player.sendMessage(ChatColor.RED + "You can not use the " + ChatColor.GOLD + "Meta Tool" + ChatColor.RED + " in this world!");
				return;
			}
		}
		painting.setArt(getNextArt(painting.getArt()));
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("quickmeta.meta.use.cycle") && !player.hasPermission("quickmeta.meta.use.copy")) {
			return;
		}
		if (player.getItemInHand().getType() != plugin.getMetaToolType() || !player.getItemInHand().getItemMeta().getDisplayName().contains("MetaTool")) {
			return;
		}
		Block block = player.getTargetBlock(null, 5);
		ItemMeta im = player.getItemInHand().getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "MetaTool - " + block.getType().toString() + ":" + ChatColor.GOLD + block.getData());
		player.getItemInHand().setItemMeta(im);
	}
	
	public int getNextId(int currentId) {
		if (currentId == 15) {
			return 0;
		}
		return currentId + 1;
	}
	
	public int getPreviousId(int currentId) {
		if (currentId == 0) {
			return 15;
		}
		return currentId - 1;
	}
	
	public Art getNextArt(Art art) {
		if (Art.values().length-1 == Arrays.asList(Art.values()).indexOf(art)) {
			return Art.values()[0];
		}
		return Art.values()[Arrays.asList(Art.values()).indexOf(art)+1];
	}
	
	public boolean isEligible(Player player, PlayerInteractEvent event) {
		if (player.getItemInHand().getType() != plugin.getMetaToolType() || !player.getItemInHand().getItemMeta().getDisplayName().contains("MetaTool")) {
			return false;
		}
		if (!player.hasPermission("quickmeta.bypass.world")) {
			if (this.plugin.getDisabledWorlds().contains(player.getWorld().getName())) {
				player.sendMessage(ChatColor.RED + "You can not use the " + ChatColor.GOLD + "Meta Tool" + ChatColor.RED + " in this world!");
				return false;
			}
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
			return false;
		}
		if (!player.hasPermission("quickmeta.bypass.block")) {
			if (this.plugin.getDisabledBlocks().contains(event.getClickedBlock())) {
				player.sendMessage(ChatColor.RED + "You cannot use " + ChatColor.GOLD + "Meta Tool" + ChatColor.RED +  " on this block!");
				return false;
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
