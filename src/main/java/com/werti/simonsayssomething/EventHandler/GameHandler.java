package com.werti.simonsayssomething.EventHandler;

import com.werti.StrRes;
import com.werti.simonsayssomething.Helper.LocationHelper;
import com.werti.simonsayssomething.SimonGame;
import com.werti.simonsayssomething.SimonPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameHandler implements Listener
{
  // TODO: Leave/end-mechanic on Server-leave
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    if (!SimonPlayer.isASimonPlayer(event.getPlayer()))
    {
      return;
    }

    SimonPlayer simonPlayer = SimonPlayer.get(event.getPlayer());

    simonPlayer.playerLeave(false);
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event)
  {
    // Check if the Player that has moved even is a part of a Simon Says Game
    if (!SimonPlayer.isASimonPlayer(event.getPlayer()))
    {
      return;
    }

    SimonPlayer simonPlayer = SimonPlayer.get(event.getPlayer());

    // Simon / Spectators are still allowed to move
    if (simonPlayer.getPlayerType() != StrRes.PlayerType.Player)
    {
      return;
    }

    // Players are allowed to move as long as the game isn't in progress
    if (simonPlayer.getSimonGame().getGameState() != SimonGame.GameState.InProgress)
    {
      return;
    }

    if (!LocationHelper.blockCoordsEqual(event.getTo(), simonPlayer.getGameLocation()))
    {
      if (!simonPlayer.isWarningTimeoutRunning())
      {
        simonPlayer.sendMessage("You're not allowed to leave your block right now!");
        simonPlayer.startWarningTimeout(10);
      }
      event.setCancelled(true);
    }
  }
}
