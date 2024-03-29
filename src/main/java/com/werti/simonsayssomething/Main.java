package com.werti.simonsayssomething;

import com.werti.Stdafx;
import com.werti.StrRes;
import com.werti.simonsayssomething.EventHandler.CommandExecuterSimon;
import com.werti.simonsayssomething.EventHandler.GameHandler;
import com.werti.simonsayssomething.EventHandler.MenuHandler;
import com.werti.simonsayssomething.GUI.SimonGameMenu;
import com.werti.simonsayssomething.GUI.SimonLobbyMenu;
import com.werti.simonsayssomething.GUI.SimonMenuFixture;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import static com.werti.StrRes.ConfigValue;

public class Main extends JavaPlugin
{
  // TODO: Chat Handling so only players can see a "SIMON SAYS ..." message
  // TODO: Jeder hat 1 oder 3 Leben, unter ihm in Wolle angezeigt ( Goldblock / Wolleblock ) bzw 1-x leben aber dann nur mit chatnachricht
  // TODO: Simon kann höhe von sich selbst anpassen
  // TODO AND / ODER ETC (SPRINGE & SCHAUE NACH UNTEN ODER SO)
  // IDEA: SPRINGEN, WERFEN, DREHEN, IN EINE RICHTUNG SCHAUEN (AUCH OBEN UND UNTEN)

  @Override
  public void onEnable()
  {
    //TODO: Add Logging to the enabling steps
    setDefaultConfigValues();

    getConfigValues();

    Stdafx.server = getServer();

    Stdafx.plugin = this;

    Stdafx.bukkitScheduler = Bukkit.getScheduler();

    Stdafx.simonLobbyMenu = new SimonLobbyMenu();
    Stdafx.simonGameMenu = new SimonGameMenu();

    this.getCommand("simon").setExecutor(new CommandExecuterSimon());

    Stdafx.server.getPluginManager().registerEvents(new MenuHandler(), this);
    Stdafx.server.getPluginManager().registerEvents(new GameHandler(), this);

    // Add events for menus (inventories)
    for (SimonMenuFixture simonMenuFixture : SimonMenuFixture.getSimonMenuList())
    {
      Stdafx.server.getPluginManager().registerEvents(simonMenuFixture, this);
    }
  }

  @Override
  public void onDisable()
  {
    // Only replace the platforms with the original blocks as the server is plugin is being disabled anyway
    for (SimonGame simonGame : SimonGame.getCurrentSimonGames())
    {
      simonGame.getPlatformGenerator().removePlayer(simonGame.getSimon());

      for (SimonPlayer simonPlayer : simonGame.getPlayerList())
      {
        simonGame.getPlatformGenerator().removePlayer(simonPlayer);
      }
    }

    // Remove all menus from everyone on the server
    SimonMenuFixture.removeAllMenus();
  }

  // Initialization method, can only be used in onEnable()
  // Setup of the config.yml
  private void setDefaultConfigValues()
  {
    Stdafx.defConfig = this.getConfig();

    // Add all Config Values to default Config
    for (ConfigValue configValue : ConfigValue.values())
    {
      Stdafx.defConfig.addDefault(configValue.getName(), configValue.getValue());
    }

    Stdafx.defConfig.options().copyDefaults(true);
    saveConfig();
  }

  private void getConfigValues()
  {
    Stdafx.MenuSlotNumber = Stdafx.defConfig.getInt(StrRes.ConfigValue.MenuSlotNumber.getName());

    // Gets Modifiers out of config file and translates them into ChatColor Format
    // Only required once in hopes that the config won't be changed while running
    Stdafx.MenuNameModifiers = ChatColor.translateAlternateColorCodes('$',
            Stdafx.defConfig.getString(StrRes.ConfigValue.MenuNameModifiers.getName()));

    Stdafx.highlightColor = ChatColor.translateAlternateColorCodes('$',
                                                                   Stdafx.defConfig.getString(ConfigValue.HighlightColor.getName()));

    // Gets Material from the config (needs Casting)
    Stdafx.BackgroundMaterial = Material.getMaterial(Stdafx.defConfig.getString(ConfigValue.BackgroundMaterial.getName()));

    Stdafx.SimonMaxHeight = Stdafx.defConfig.getInt(ConfigValue.SimonMaxHeight.getName());

    Stdafx.Tickrate = Stdafx.defConfig.getInt(ConfigValue.Tickrate.getName());

    Stdafx.PlayerLimit = Stdafx.defConfig.getInt(ConfigValue.PlayerLimit.getName());
    if (Stdafx.PlayerLimit > 12 || Stdafx.PlayerLimit < 1)
    {
      getLogger().warning("Player-Limit must be betweent 1 and 12! Defaulting to 12..");
      Stdafx.PlayerLimit = 12;
    }
  }
}

