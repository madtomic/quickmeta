package com.qwertyness.quickmeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class QuickMeta extends JavaPlugin {
	private WorldGuardPlugin worldGuard;
	public boolean usingWG;
	public boolean usingPM;
	private List<String> disabledWorlds;
	private List<Material> diabledBlocks;
	private boolean allowPaintings;

	public void onEnable() {
		FileConfiguration fc = YamlConfiguration.loadConfiguration(this.getResource("config.yml"));
		this.getConfig().setDefaults(fc);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		this.saveDefaultConfig();
		
		//Initialize plugin instances.
		if (this.getServer().getPluginManager().getPlugin("WorldGuard") != null && this.getConfig().getBoolean("supportWorldGuard")) {
			this.worldGuard = WorldGuardPlugin.inst();
			this.usingWG = true;
		}
		else {
			this.worldGuard = null;
			this.usingWG = false;
		}
		
		if (this.getServer().getPluginManager().getPlugin("PlotMe") != null && this.getConfig().getBoolean("supportPlotMe")) {
			this.usingPM = true;
		}
		else {
			this.usingPM = false;
		}
		
		//Initialize Blacklists
		this.disabledWorlds = this.getConfig().getStringList("disabledWorlds");
		
		List<String> materials = this.getConfig().getStringList("disabledBlocks");
		this.diabledBlocks = new ArrayList<Material>();
		for (String material : materials) {
			this.diabledBlocks.add(Material.getMaterial(material));
		}
		this.allowPaintings = this.getConfig().getBoolean("allowPaintings");
		
		//Enable listeners
		this.getServer().getPluginManager().registerEvents(new MetaListener(this), this);
		this.getServer().getPluginManager().registerEvents(new BiomeListener(this), this);
		this.getServer().getPluginManager().registerEvents(new MobListener(this), this);
		
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player player = (Player) sender;
		if (command.getName().equalsIgnoreCase("biometool")) {
			if (args.length > 1) {
				if (args[0].equalsIgnoreCase("select")) {
					Biome biome;
					try {
						biome = Biome.valueOf(args[1]);
					} catch(Exception e){
						String biomeValues = "[";
						for (Biome b : Biome.values()) {
							biomeValues += b.name() + ", ";
						}
						biomeValues += "]";
						player.sendMessage(ChatColor.RED + "Not a biome!");
						player.sendMessage(ChatColor.RED + "Valid biomes: " + biomeValues);
						return false;
					}
					
					if (player.getItemInHand().getType() != this.getBiomeToolType() || !player.getItemInHand().getItemMeta().getDisplayName().contains("BiomeTool")) {
						player.sendMessage(ChatColor.RED + "You must be holding a biome tool to use /biometool select!");
						return false;
					}
					ItemStack tool = player.getItemInHand();
					ItemMeta meta = tool.getItemMeta();
					meta.setDisplayName(ChatColor.GREEN + "BiomeTool - " + biome.name());
					tool.setItemMeta(meta);
					return true;
				}
				else {
					player.sendMessage(ChatColor.RED + "Invalid syntax! /biometool or /biometool select <biome_name>");
					return false;
				}
			}
			ItemStack tool = new ItemStack(this.getBiomeToolType());
			ItemMeta meta = tool.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "BiomeTool - FOREST");
			meta.setLore(Arrays.asList(ChatColor.GOLD + "Right click with tool to choose Biome.",
					ChatColor.GOLD + "Left click on block to set biome.", 
					ChatColor.GOLD + "Select biome with /biometool select <biome_name>"));
			tool.setItemMeta(meta);
			player.getInventory().addItem(tool);
		}
		
		if (command.getName().equalsIgnoreCase("metatool")) {
			ItemStack tool = new ItemStack(this.getMetaToolType());
			ItemMeta meta = tool.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "MetaTool");
			meta.setLore(Arrays.asList(ChatColor.GOLD + "Right or left click block with tool to cycle block meta.",
					ChatColor.GOLD + "Sneak and left click block to copy block meta.", 
					ChatColor.GOLD + "Sneak and right click block to paste block meta."));
			tool.setItemMeta(meta);
			player.getInventory().addItem(tool);
		}
		
		if (command.getName().equalsIgnoreCase("entitytool")) {
			EntityType type = EntityType.ZOMBIE;
			if (args.length > 1) {
				if (args[0].equalsIgnoreCase("select")) {
					
					if (this.getValidEntityTypes().contains(args[1])){
						type = EntityType.valueOf(this.getValidEntityTypes().get(this.getValidEntityTypes().indexOf(args[1])));
					} else {
						String entityValues = "[";
						for (String string : this.getValidEntityTypes()) {
							entityValues += string + ", ";
						}
						entityValues += "]";
						player.sendMessage(ChatColor.RED + "Not an Entity!");
						player.sendMessage(ChatColor.RED + "Valid entities: " + entityValues);
						return false;
					}
				}
				if (player.getItemInHand().getType() != this.getEntityToolType() || !player.getItemInHand().getItemMeta().getDisplayName().contains("EntityTool")) {
					player.sendMessage(ChatColor.RED + "You must be holding an entity tool to use /entitytool select!");
				}
				ItemStack is = player.getItemInHand();
				ItemMeta meta = is.getItemMeta();
				meta.setDisplayName(ChatColor.GREEN + "EntityTool - " + type.name());
				is.setItemMeta(meta);
				return true;
			}
			ItemStack tool = new ItemStack(this.getEntityToolType());
			ItemMeta meta = tool.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN + "EntityTool - " + type.name());
			meta.setLore(Arrays.asList(ChatColor.GOLD + "Right click to cycle through mob types.",
				ChatColor.GOLD + "Left click mob to apply type.",
				ChatColor.GOLD + "Shift left click mob to cycle through meta."));
			tool.setItemMeta(meta);
			player.getInventory().addItem(tool);
			
		}
		
		if (command.getName().equalsIgnoreCase("quickmeta")) {
			if (args.length <= 0) {
				player.sendMessage(ChatColor.GREEN + "----- QuickMeta -----");
				player.sendMessage(ChatColor.GREEN + "Author: " + ChatColor.GOLD + "Qwertyness");
				player.sendMessage(ChatColor.GREEN + "Version: " + ChatColor.GOLD + this.getDescription().getVersion());
				player.sendMessage(ChatColor.GREEN + "BukkitDev page: " + ChatColor.GOLD + "http://dev.bukkit.org/bukkit-plugins/quickmeta/");
			}
			else {
				if (args[0].equalsIgnoreCase("reload")) {
					if (player.hasPermission("quickmeta.reload") || player.isOp()) {
						this.getServer().getPluginManager().disablePlugin(this);
						this.getServer().getPluginManager().enablePlugin(this);
					}
				}
			}
		}
		return true;
	}
	
	public Material getMetaToolType() {
		return Material.getMaterial(this.getConfig().getString("metaTool"));
	}
	
	public Material getBiomeToolType() {
		return Material.getMaterial(this.getConfig().getString("biomeTool"));
	}
	
	public Material getEntityToolType() {
		return Material.getMaterial(this.getConfig().getString("entityTool"));
	}
	
	public List<String> getDisabledWorlds() {
		return this.disabledWorlds;
	}
	
	public boolean getAllowPaintings() {
		return this.allowPaintings;
	}
	
	public List<Material> getDisabledBlocks() {
		return this.diabledBlocks;
	}
	
	public WorldGuardPlugin getWGPlugin() {
		return this.worldGuard;
	}
	public List<String> getValidEntityTypes() {
		return Arrays.asList("BAT", "BLAZE", "CAVE_SPIDER", "CHICKEN", "COW", "CREEPER", "ENDERMAN", "GHAST", "GIANT", "HORSE", "IRON_GOLEM", "MAGMA_CUBE",
				"MUSHROOM_COW", "OCELOT", "PIG", "PIG_ZOMBIE", "SHEEP", "SILVERFISH", "SKELETON", "SLIME", "SNOWMAN", "SPIDER", "SQUID", "VILLAGER",
				"WITCH", "WOLF", "ZOMBIE");
	}
}
