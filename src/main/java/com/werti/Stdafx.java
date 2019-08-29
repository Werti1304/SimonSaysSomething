package com.werti;

import com.werti.simonsayssomething.GUI.SimonGameMenu;
import com.werti.simonsayssomething.GUI.SimonLobbyMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

// Standard-Include, everything in here is usable AFTER onEnable()
public class Stdafx
{
  public static FileConfiguration defConfig;

  public static JavaPlugin plugin;

  public static Server server;

  public static BukkitScheduler bukkitScheduler;

  public static final int defaultMenuSlot = 8;

  // Config values
  public static int MenuSlotNumber;
  public static String MenuNameModifiers;
  public static String highlightColor;
  public static Material BackgroundMaterial;
  public static int SimonMaxHeight;
  public static int Tickrate;
  public static int PlayerLimit;

  public static String textColor = ChatColor.RESET.toString() + ChatColor.GRAY;
  public static String adminTextColor = ChatColor.RED.toString();
  public static String errorTextColor = ChatColor.DARK_RED.toString();

  public static int InviteTimeout = 30; // In seconds
  public static String defaultCommand = "simon says";
  public static String adminPermission = "SimonSaysSomething.Admin";
  // All openable menus
  public static SimonLobbyMenu simonLobbyMenu;
  public static final Material toggleGameMaterial = Material.ANVIL;

  public static final Material initGameMaterial = Material.CLAY_BRICK;
  public static final Material startGameMaterial = Material.GOLD_INGOT;
  public static final Material endGameMaterial = Material.NETHER_BRICK_ITEM;
  public static final Material inviteGameMaterial = Material.PAPER;
  public static SimonGameMenu simonGameMenu;
}
