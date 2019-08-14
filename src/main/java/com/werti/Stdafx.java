package com.werti;

import com.werti.simonsayssomething.SimonMenu;
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

  public static SimonMenu simonMenu;

  public static JavaPlugin plugin;

  public static Server server;

  public static BukkitScheduler bukkitScheduler;

  // Config values
  public static int MenuSlotNumber;
  public static String MenuNameModifiers;
  public static String highlightColor;
  public static Material BackgroundMaterial;
  public static int SimonMaxHeight;
  public static int Tickrate;
  public static int PlayerLimit;

  public static String textColor = ChatColor.RESET.toString() + ChatColor.GRAY;
  public static int InviteTimeout = 30; // In seconds
  public static String defaultCommand = "simon says";
}
