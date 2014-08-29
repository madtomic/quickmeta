package com.qwertyness.quickmeta;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Horse.Color;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MobListener implements Listener {
	private QuickMeta plugin;
	
	public MobListener(QuickMeta plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void mobChange(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		if (!(event.getEntity() instanceof LivingEntity) || event.getEntity() instanceof Player) {
			return;
		}
		Player player = (Player) event.getDamager();
		LivingEntity entity = (LivingEntity) event.getEntity();
		if (player.isSneaking()) {
			return;
		}
		if (!player.hasPermission("quickmeta.meta.use")) {
			return;
		}
		if (player.getItemInHand().getType() != plugin.getEntityToolType() || !player.getItemInHand().getItemMeta().getDisplayName().contains("EntityTool")) {
			return;
		}
		if (!player.hasPermission("quickmeta.bypass.world")) {
			if (this.plugin.getDisabledWorlds().contains(player.getWorld().getName())) {
				player.sendMessage(ChatColor.RED + "You can not use the " + ChatColor.GOLD + "Entity Tool" + ChatColor.RED + " in this world!");
				return;
			}
		}
		Location loc = entity.getLocation();
		entity.remove();
		String name = player.getItemInHand().getItemMeta().getDisplayName();
		loc.getWorld().spawnEntity(loc, EntityType.valueOf(name.substring(name.indexOf("-")+2)));
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("quickmeta.entity.use")) {
			return;
		}
		if (player.getItemInHand().getType() != plugin.getEntityToolType() || !player.getItemInHand().getItemMeta().getDisplayName().contains("EntityTool")) {
			return;
		}
		if (!player.hasPermission("quickmeta.bypass.world")) {
			if (this.plugin.getDisabledWorlds().contains(player.getWorld().getName())) {
				player.sendMessage(ChatColor.RED + "You can not use the " + ChatColor.GOLD + "Entity Tool" + ChatColor.RED + " in this world!");
				return;
			}
		}
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack is = player.getItemInHand();
			String name = is.getItemMeta().getDisplayName();
			ItemMeta meta = is.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "EntityTool - " + getNextEntityType(EntityType.valueOf(name.substring(name.indexOf("-")+2))));
			is.setItemMeta(meta);
		}
	}
	
	@EventHandler
	public void metaChange(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) {
			return;
		}
		if (!(event.getEntity() instanceof LivingEntity)) {
			return;
		}
		Player player = (Player) event.getDamager();
		LivingEntity entity = (LivingEntity) event.getEntity();
		if (!player.isSneaking()) {
			return;
		}
		if (!player.hasPermission("quickmeta.entity.use")) {
			return;
		}
		if (player.getItemInHand().getType() != plugin.getEntityToolType() || !player.getItemInHand().getItemMeta().getDisplayName().contains("EntityTool")) {
			return;
		}
		if (!player.hasPermission("quickmeta.bypass.world")) {
			if (this.plugin.getDisabledWorlds().contains(player.getWorld().getName())) {
				player.sendMessage(ChatColor.RED + "You can not use the " + ChatColor.GOLD + "Entity Tool" + ChatColor.RED + " in this world!");
				return;
			}
		}
		
		//Entity meta change code.
		if (entity instanceof Villager) {
			Villager villager = (Villager) entity;
			if (Profession.values().length-1 == Arrays.asList(Profession.values()).indexOf(villager.getProfession())) {
				villager.setProfession(Profession.values()[0]);
			}
			else {
				villager.setProfession(Profession.values()[Arrays.asList(Profession.values()).indexOf(villager.getProfession())+1]);
			}
			
		}
		if (entity instanceof Horse) {
			Horse horse = (Horse) entity;
			if (Color.values().length-1 == Arrays.asList(Color.values()).indexOf(horse.getColor()) && Variant.values().length-1 == Arrays.asList(Variant.values()).indexOf(horse.getVariant())) {
				horse.setColor(Color.values()[0]);
				horse.setVariant(Variant.values()[0]);
			}
			else {
				if (Variant.values().length-1 == Arrays.asList(Variant.values()).indexOf(horse.getVariant())) {
					horse.setColor(Color.values()[Arrays.asList(Color.values()).indexOf(horse.getColor())+1]);
					horse.setVariant(Variant.values()[0]);
				}
				else {
					horse.setVariant(Variant.values()[Arrays.asList(Variant.values()).indexOf(horse.getVariant())+1]);
				}
			}
		}
		if (entity instanceof Sheep) {
			Sheep sheep = (Sheep) entity;
			if (DyeColor.values().length-1 == Arrays.asList(DyeColor.values()).indexOf(sheep.getColor())) {
				sheep.setColor(DyeColor.values()[0]);
			}
			else {
				sheep.setColor(DyeColor.values()[Arrays.asList(DyeColor.values()).indexOf(sheep.getColor())+1]);
			}
		}
		if (entity instanceof Ocelot) {
			Ocelot ocelot = (Ocelot) entity;
			ocelot.setTamed(true);
			ocelot.setOwner(player);
			if (Ocelot.Type.values().length-1 == Arrays.asList(Ocelot.Type.values()).indexOf(ocelot.getType())) {
				ocelot.setCatType(Ocelot.Type.values()[0]);
			}
			else {
				ocelot.setCatType(Ocelot.Type.values()[Arrays.asList(Ocelot.Type.values()).indexOf(ocelot.getType())+1]);
			}
		}
		if (entity instanceof Wolf) {
			Wolf wolf = (Wolf) entity;
			wolf.setTamed(true);
			wolf.setOwner(player);
			if (DyeColor.values().length-1 == Arrays.asList(DyeColor.values()).indexOf(wolf.getCollarColor())) {
				wolf.setCollarColor(DyeColor.values()[0]);
			}
			else {
				wolf.setCollarColor(DyeColor.values()[Arrays.asList(DyeColor.values()).indexOf(wolf.getCollarColor())+1]);
			}
		}
		if (entity instanceof Skeleton) {
			Skeleton skeleton = (Skeleton) entity;
			if (Skeleton.SkeletonType.values().length-1 == Arrays.asList(Skeleton.SkeletonType.values()).indexOf(skeleton.getSkeletonType())) {
				skeleton.setSkeletonType(Skeleton.SkeletonType.values()[0]);
			}
			else {
				skeleton.setSkeletonType(Skeleton.SkeletonType.values()[Arrays.asList(Skeleton.SkeletonType.values()).indexOf(skeleton.getSkeletonType())+1]);
			}
		}
		if (entity instanceof Zombie) {
			Zombie zombie = (Zombie) entity;
			if (zombie.isVillager()) {
				zombie.setVillager(false);
			}
			else {
				zombie.setVillager(true);
			}
		}
		event.setCancelled(true);
	}
	
	public EntityType getNextEntityType(EntityType type) {
		switch(type) {
			default: return EntityType.ZOMBIE;
			case BAT: return EntityType.BLAZE;
			case BLAZE: return EntityType.CAVE_SPIDER;
			case CAVE_SPIDER: return EntityType.CHICKEN;
			case CHICKEN: return EntityType.COW;
			case COW: return EntityType.CREEPER;
			case CREEPER: return EntityType.ENDERMAN;
			case ENDERMAN: return EntityType.GHAST;
			case GHAST: return EntityType.GIANT;
			case GIANT: return EntityType.HORSE;
			case HORSE: return EntityType.IRON_GOLEM;
			case IRON_GOLEM: return EntityType.MAGMA_CUBE;
			case MAGMA_CUBE: return EntityType.MUSHROOM_COW;
			case MUSHROOM_COW: return EntityType.OCELOT;
			case OCELOT: return EntityType.PIG;
			case PIG: return EntityType.PIG_ZOMBIE;
			case PIG_ZOMBIE: return EntityType.SHEEP;
			case SHEEP: return EntityType.SILVERFISH;
			case SILVERFISH: return EntityType.SKELETON;
			case SKELETON: return EntityType.SLIME;
			case SLIME: return EntityType.SNOWMAN;
			case SNOWMAN: return EntityType.SPIDER;
			case SPIDER: return EntityType.SQUID;
			case SQUID: return EntityType.VILLAGER;
			case VILLAGER: return EntityType.WITCH;
			case WITCH: return EntityType.WOLF;
			case WOLF: return EntityType.ZOMBIE;
			case ZOMBIE: return EntityType.BAT;
		}
	}
}
