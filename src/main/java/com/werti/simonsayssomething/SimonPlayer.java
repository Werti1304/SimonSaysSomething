package com.werti.simonsayssomething;

import com.werti.Stdafx;
import com.werti.StrRes;
import com.werti.simonsayssomething.Helper.ChatHelper;
import com.werti.simonsayssomething.Helper.LocationHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class SimonPlayer
{
  private static final ChatColor messageColor = ChatColor.GRAY;
  private static final String prefix = "Simon";
  private static HashMap<Player, SimonPlayer> currentSimonPlayers = new HashMap<>();
  private Player player;
  private StrRes.PlayerType playerType;
  private Location gameLocation;
  private SimonGame simonGame;
  private boolean warningTimeoutRunning = false;
  private LinkedHashMap<Material, Byte> oldBlocks = new LinkedHashMap<>(); // Blocks that are remove for the play-area

  public static void sendMessage(CommandSender player, String message)
  {
    player.sendMessage(
        "[" + Stdafx.highlightColor + prefix + ChatColor.RESET + "] " + Stdafx.textColor + messageColor + message);
  }

  private SimonPlayer(Player player, StrRes.PlayerType playerType)
  {
    this.player = player;
    this.playerType = playerType;
  }

  public static void sendRawMessage(Player player, String message)
  {
    player.sendMessage(
        "[" + Stdafx.highlightColor + prefix + ChatColor.RESET + "] " + Stdafx.textColor + messageColor + message);
  }

  // Returns a SimonPlayer. If the player isn't a SimonPLayer, the function returns null
  public static SimonPlayer get(Player simonPlayer)
  {
    if (currentSimonPlayers.containsKey(simonPlayer))
    {
      return currentSimonPlayers.get(simonPlayer);
    }
    return null;
  }

  // Sets a new SimonPlayer
  public static SimonPlayer add(Player simonPlayer, StrRes.PlayerType playerType)
  {
    SimonPlayer player = new SimonPlayer(simonPlayer, playerType);

    currentSimonPlayers.put(simonPlayer, player);

    return player;
  }

  public LinkedHashMap<Material, Byte> getOldBlocks()
  {
    return oldBlocks;
  }

  public static void sendMessage(CommandSender player, StrRes.SimonError simonGameError)
  {
    sendMessage(player, simonGameError.getError());
  }

  public void playerLeave(boolean silent, boolean forced)
  {
    if (player.isOnline() && !silent)
    {
      if (forced)
      {
        sendMessage("You were kicked out of " + ChatHelper.getNameInFormat(simonGame.getSimon()) + "s game!");
      }
      else
      {
        sendMessage("You've left the game of " + ChatHelper.getNameInFormat(simonGame.getSimon()) + "!");
      }
    }

    simonGame.playerLeave(this, silent);

    currentSimonPlayers.remove(this.getPlayer());

    // From this point on, we're waiting for the Java Garbage Trucks to come
  }

  public static boolean isASimonPlayer(Player player)
  {
    return !(get(player) == null);
  }

  public void sendMessage(String message)
  {
    sendMessage(player, message);
  }

  public void sendMessage(StrRes.SimonError simonGameError)
  {
    sendMessage(player, simonGameError);
  }

  public void teleportToGameLocation()
  {
    // Only teleport to location if they're not on the right block anymore
    if (!LocationHelper.blockCoordsEqual(gameLocation, getLocation()))
    {
      teleport(gameLocation);
    }
  }

  public boolean isSimon()
  {
    return playerType == StrRes.PlayerType.Simon;
  }

  public boolean isPlayer()
  {
    return playerType == StrRes.PlayerType.Player;
  }

  public Location getLocation()
  {
    return player.getLocation();
  }

  public Inventory getInventory()
  {
    return player.getInventory();
  }

  public String getName()
  {
    return player.getName();
  }

  public void closeInventory()
  {
    player.closeInventory();
  }

  public World getWorld()
  {
    return player.getWorld();
  }

  public void teleport(Location location)
  {
    player.teleport(location);
  }

  public Location getGameLocation()
  {
    return gameLocation;
  }

  public void setGameLocation(Location gameLocation)
  {
    this.gameLocation = gameLocation;
  }

  @NotNull
  public StrRes.PlayerType getPlayerType()
  {
    return playerType;
  }

  public void setPlayerType(StrRes.PlayerType playerType)
  {
    this.playerType = playerType;

    sendMessage("You're now" + playerType.getColor() + playerType.getName() + Stdafx.textColor + "!");
  }

  public Player getPlayer()
  {
    return player;
  }

  public SimonGame getSimonGame()
  {
    return simonGame;
  }

  void setSimonGame(SimonGame simonGame)
  {
    this.simonGame = simonGame;
  }

  public void startWarningTimeout(int timeoutInSeconds)
  {
    this.warningTimeoutRunning = true;

    Stdafx.bukkitScheduler.runTaskLater(Stdafx.plugin,
            () -> warningTimeoutRunning = false,
            timeoutInSeconds * Stdafx.Tickrate);
  }

  public boolean isWarningTimeoutRunning()
  {
    return warningTimeoutRunning;
  }
}