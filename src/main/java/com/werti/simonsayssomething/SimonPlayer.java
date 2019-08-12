package com.werti.simonsayssomething;

import com.werti.Stdafx;
import com.werti.StrRes;
import com.werti.simonsayssomething.Helper.LocationHelper;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

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

  private SimonPlayer(Player player, StrRes.PlayerType playerType)
  {
    this.player = player;
    this.playerType = playerType;
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

  public static void sendMessage(CommandSender player, String message)
  {
    player.sendMessage(
            "[" + Stdafx.HighlightColor + prefix + ChatColor.RESET + "] " + Stdafx.textColor + messageColor + message);
  }

  public static void sendMessage(CommandSender player, StrRes.SimonError simonGameError)
  {
    sendMessage(player, simonGameError.getError());
  }

  public static void sendRawMessage(Player player, String message)
  {
    player.sendMessage(
            "[" + Stdafx.HighlightColor + prefix + ChatColor.RESET + "] " + Stdafx.textColor + messageColor + message);
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