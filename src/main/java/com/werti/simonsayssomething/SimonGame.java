package com.werti.simonsayssomething;

import com.werti.Stdafx;
import com.werti.StrRes;
import com.werti.simonsayssomething.BukkitRunnables.Timer;
import com.werti.simonsayssomething.Helper.ChatHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class SimonGame
{
  private static ArrayList<SimonGame> currentSimonGames = new ArrayList<>();
  private ArrayList<SimonPlayer> playerList = new ArrayList<>();
  private ArrayList<Player> invitedPlayersList = new ArrayList<>();
  private SimonPlayer simon;
  private GameState gameState = GameState.None;
  private PlatformGenerator platformGenerator;

  private boolean freeMode = false;

  // At this point we're sure that the player is allowed to create a new SimonGame-Game
  public SimonGame(SimonPlayer simon)
  {
    currentSimonGames.add(this);

    this.simon = simon;

    this.platformGenerator = new PlatformGenerator(this, freeMode);

    simon.setSimonGame(this);

    this.simon.sendMessage("You're now Simon! Here, have a piece of paper!");

    Stdafx.simonLobbyMenu.giveToPlayer(simon.getPlayer(), Stdafx.defaultMenuSlot);

    setGameState(GameState.WaitingForInit);
  }

  @org.jetbrains.annotations.Nullable
  public static SimonGame getGameByPlayer(Player player)
  {
    return getGameByPlayer(SimonPlayer.get(player));
  }

  @org.jetbrains.annotations.Nullable
  public static SimonGame getGameByPlayer(SimonPlayer player)
  {
    for (SimonGame simonGame : currentSimonGames)
    {
      if (simonGame.containsPlayer(player))
      {
        return simonGame;
      }
    }
    return null;
  }

  public static ArrayList<SimonGame> getCurrentSimonGames()
  {
    return currentSimonGames;
  }

  public boolean containsPlayer(Player player)
  {
    return containsPlayer(SimonPlayer.get(player));
  }

  public boolean containsPlayer(SimonPlayer player)
  {
    if (player == simon)
    {
      return true;
    }
    for (SimonPlayer simonPlayer : playerList)
    {
      if (player == simonPlayer)
      {
        return true;
      }
    }
    return false;
  }

  public void broadcast(String message)
  {
    simon.sendMessage(message);

    broadcastToPlayers(message);
  }

  public void broadcast(StrRes.SimonError error)
  {
    simon.sendMessage(error);

    broadcastToPlayers(error.getError());
  }

  private void broadcastToPlayers(String message)
  {
    for (SimonPlayer player : playerList)
    {
      player.sendMessage(message);
    }
  }

  /**
   * @param player player to add to the Game
   * @return Last added player
   */
  public SimonPlayer addPlayer(Player player)
  {
    if (gameState == GameState.InProgress)
    {
      simon.sendMessage(StrRes.SimonGameError.GameAlreadyStarted);
      return null;
    }

    if (playerList.size() >= Stdafx.PlayerLimit)
    {
      simon.sendMessage(StrRes.SimonGameError.MaximumPlayersReached);
      return null;
    }

    SimonPlayer simonPlayer = SimonPlayer.add(player, StrRes.PlayerType.Player);

    // Write for the player that has joined
    simonPlayer.sendMessage("You've joined the game of " + ChatHelper.getNameInFormat(simon) + "!");

    // Write for the rest of the game
    broadcast(ChatHelper.getNameInFormat(simonPlayer) + " has joined the game!");

    if (gameState == GameState.WaitingForStart)
    {
      // Adds gameLocation and platform for a new player
      platformGenerator.addPlayer(simonPlayer);

      simonPlayer.teleportToGameLocation();
    }

    invitedPlayersList.remove(player);
    playerList.add(simonPlayer);
    simonPlayer.setSimonGame(this);

    return simonPlayer;
  }

  public void init()
  {
    if (gameState != SimonGame.GameState.WaitingForInit)
    {
      simon.sendMessage(StrRes.SimonGameError.InvalidStateForInit);
      return;
    }

    StrRes.PlatformError platformError = platformGenerator.execute();

    if (platformError != StrRes.PlatformError.None)
    {
      simon.sendMessage(platformError);
      return;
    }

    teleportPlayersToGame();

    setGameState(SimonGame.GameState.WaitingForStart);
    simon.sendMessage("Game is ready to start!");
  }

  public void start()
  {
    if (gameState != SimonGame.GameState.WaitingForStart)
    {
      simon.sendMessage(StrRes.SimonGameError.InvalidStateForStart);
      return;
    }
    setGameState(SimonGame.GameState.InProgress);

    simon.sendMessage("Starting game now!");

    Timer timer = new Timer(this, Timer.Action.StartGame, 5);

    teleportToGame();

    timer.execute();
  }

  public void startSequenceAfterTimer()
  {
    // Todo: If check > 0 player in game

    // Check if the gamestate changes during the start (Timer running)
    if (gameState != GameState.InProgress)
    {
      broadcast(StrRes.SimonGameError.CouldntStartGame);

      endGame();

      return;
    }

    teleportToGame();
    broadcast("The Game has started! Simon says listen to me!");
  }

  /**
   * WARNING: Only removes SimonPlayer from a game, not the Player from it's status.
   * To remove him generally, call SimonPlayer.playerLeave
   *
   * @param simonPlayer SimonPlayer that leaves the game
   */
  void playerLeave(SimonPlayer simonPlayer, boolean silent)
  {
    if (gameState == GameState.InProgress || gameState == GameState.WaitingForStart || gameState == GameState.Ended)
    {
      platformGenerator.removePlayer(simonPlayer);
    }

    playerList.remove(simonPlayer);

    if (!silent)
    {
      broadcast(ChatHelper.getNameInFormat(simonPlayer) + " has left the game! (" + Stdafx.highlightColor
                + playerList.size() + "/" + Stdafx.PlayerLimit + Stdafx.textColor + ")");
    }
  }

  public void endGame()
  {
    setGameState(GameState.Ended);

    currentSimonGames.remove(this);

    simon.playerLeave(true, false);

    simon.getInventory().setItem(Stdafx.defaultMenuSlot, new ItemStack(Material.AIR));

    broadcast("The game has ended!");
    if (!playerList.isEmpty())
    {
      // Using a manual loops because lists don't allow changing while being iterated
      for (int i = 0; i < playerList.size(); i++)
      {
        playerList.get(i).playerLeave(true, false);
      }
    }
  }

  public void teleportToGame()
  {
    simon.teleportToGameLocation();

    teleportPlayersToGame();
  }

  public void teleportPlayersToGame()
  {
    for (SimonPlayer simonPlayer : playerList)
    {
      simonPlayer.teleportToGameLocation();
    }
  }

  public void invitePlayer(Player player)
  {
    simon.sendMessage("You invited " + ChatHelper.getNameInFormat(player) + " to your game!");

    broadcastToPlayers(ChatHelper.getNameInFormat(player) + " was invited to the game!");

    SimonPlayer.sendMessage(player, ChatHelper.getNameInFormat(simon) + " has invited you to their game!");
    SimonPlayer.sendMessage(player,
                            "To accept, type " + Stdafx.highlightColor
                            + ChatHelper.getFullCommandWithoutParams(StrRes.Command.AcceptInvite) + " " + simon.getName());
    SimonPlayer.sendMessage(player,
                            "To decline, type " + Stdafx.highlightColor
                            + ChatHelper.getFullCommandWithoutParams(StrRes.Command.DeclineInvite) + " " + simon.getName());

    invitedPlayersList.add(player);

    Stdafx.bukkitScheduler.runTaskLater(Stdafx.plugin, () ->
    {
      if (invitedPlayersList.contains(player))
      {
        invitedPlayersList.remove(player);
        SimonPlayer.sendMessage(player, "Your invite from " + ChatHelper.getNameInFormat(simon) + " has expired!");
        simon.sendMessage("The invite to " + ChatHelper.getNameInFormat(player) + " has expired!");
      }

    }, Stdafx.Tickrate * Stdafx.InviteTimeout);
  }

  public ArrayList<Player> getInvitedPlayersList()
  {
    return invitedPlayersList;
  }

  public ArrayList<SimonPlayer> getPlayerList()
  {
    return playerList;
  }

  public SimonPlayer getSimon()
  {
    return simon;
  }

  public GameState getGameState()
  {
    return gameState;
  }

  public PlatformGenerator getPlatformGenerator()
  {
    return platformGenerator;
  }

  public void setGameState(GameState gameState)
  {
    this.gameState = gameState;
  }

  public void setFreeMode(boolean freeMode)
  {
    this.freeMode = freeMode;
  }

  public enum GameState
  {
    None,
    WaitingForInit,
    WaitingForStart,
    InProgress,
    Ended
  }
}
