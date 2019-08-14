package com.werti.simonsayssomething;

import com.werti.Stdafx;
import com.werti.StrRes;
import com.werti.StrRes.SimonGameError;
import com.werti.simonsayssomething.BukkitRunnables.Timer;
import com.werti.simonsayssomething.Helper.ChatHelper;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandExecuter
{
  // Switch through the command that comes after the "simon says" part
  public static void switchCommandsInRound(SimonPlayer player, String args[])
  {
    boolean isSimon = false;
    boolean isPlayer = false;

    // Check if the player already is in one of the simon games
    for (SimonGame simonGame : SimonGame.getCurrentSimonGames())
    {
      if (simonGame.getSimon() == player)
      {
        isSimon = true;
      }
      else if (simonGame.getPlayerList().contains(player))
      {
        isPlayer = true;
      }
    }

    args[1] = args[1].toLowerCase();

    StrRes.Command command = StrRes.Command.getCommandFromString(args[1]);

    if (command == null)
    {
      displaySimonSaysCommandHelp(player.getPlayer());
      return;
    }

    switch (command.getRequirement())
    {
      case None:
      case InAGame:
        break;
      case OutSideGame:
        player.sendMessage(SimonGameError.IsInGame);
        return;
      case IsSimon:
        if (!isSimon)
        {
          player.sendMessage(SimonGameError.NotSimon);
          return;
        }
        break;
      case IsPlayer:
        if (!isPlayer)
        {
          player.sendMessage(SimonGameError.NotPlayer);

          // Special message when Simon tries to leave the game
          if (isSimon && command == StrRes.Command.LeaveGame)
          {
            player.sendMessage(SimonGameError.LeaveGameAsSimon);
          }
          return;
        }
        break;
    }

    // Get Game of this player
    SimonGame simonGameOfPlayer = SimonGame.getGameByPlayer(player);

    // Commands that don't need any additional arguments
    switch (command)
    {
      case InitGame:
        init(simonGameOfPlayer);
        return;
      case StartGame:
        start(simonGameOfPlayer);
        return;
      case LeaveGame:
        leave(player);
        return;
      case EndGame:
        End(simonGameOfPlayer);
        return;
      case ListPlayers:
        listPlayers(simonGameOfPlayer, player);
        return;
    }

    // Commands that need 1 additional argument
    if (args.length <= 2)
    {
      player.sendMessage(ChatHelper.getFullCommand(command));
      return;
    }

    switch (command)
    {
      case InvitePlayers:
        invitePlayer(simonGameOfPlayer, args[2]);
        break;
    }
  }

  private static void End(SimonGame simonGameOfPlayer)
  {
    simonGameOfPlayer.endGame();
  }

  private static void leave(SimonPlayer simonPlayer)
  {
    simonPlayer.playerLeave(false);
  }

  public static void switchCommandsOutsideRound(Player player, String args[])
  {
    args[1] = args[1].toLowerCase();

    StrRes.Command command = StrRes.Command.getCommandFromString(args[1]);

    if (command == null)
    {
      displaySimonSaysCommandHelp(player.getPlayer());
      return;
    }

    if (command.getRequirement() != StrRes.Command.Requirement.OutSideGame
        && command.getRequirement() != StrRes.Command.Requirement.None)
    {
      SimonPlayer.sendMessage(player, SimonGameError.NotASimonSaysPlayer);
      return;
    }

    // Commands that need 1 additional argument
    if (args.length <= 2)
    {
      SimonPlayer.sendMessage(player, ChatHelper.getFullCommand(command));
      return;
    }

    switch (command)
    {
      case AcceptInvite:
        inviteResponse(player, args[2], true);
        break;
      case DeclineInvite:
        inviteResponse(player, args[2], false);
        break;
      default:
        SimonPlayer.sendMessage(player, SimonGameError.NotASimonSaysPlayer);
        break;
    }
  }

  /**
   * @param targetPlayer Player that responds to invite
   * @param name         Name of player he wants to accept/decline
   * @param joinGame     Whether he wants to accept or decline
   */
  private static void inviteResponse(Player targetPlayer, String name, boolean joinGame)
  {
    Player player = Stdafx.server.getPlayer(name);

    if (player == null)
    {
      SimonPlayer.sendMessage(targetPlayer, StrRes.SimonGameError.SimonGameNotFound);
      return;
    }

    SimonGame simonGame = SimonGame.getGameByPlayer(player);

    if (simonGame == null)
    {
      SimonPlayer.sendMessage(targetPlayer, SimonGameError.SimonGameNotFound);
      return;
    }

    // Check if the player even has an invite
    if (!(simonGame.getInvitedPlayersList().contains(targetPlayer)))
    {
      SimonPlayer.sendMessage(targetPlayer,
              "You have no pending invites from " + ChatHelper.getNameInFormat(player) + "!");
      return;
    }

    if (joinGame)
    {
      simonGame.getSimon().sendMessage(
              ChatHelper.getNameInFormat(targetPlayer) + " has " + ChatColor.GREEN + "accepted" + Stdafx.textColor
              + " your invite!");
      simonGame.addPlayer(targetPlayer);
    }
    else
    {
      simonGame.getSimon().sendMessage(
              ChatHelper.getNameInFormat(targetPlayer) + " has " + ChatColor.RED + "declined" + Stdafx.textColor
              + " your invite!");
    }
  }

  private static void invitePlayer(SimonGame simonGame, String name)
  {
    Player invitedPlayer = Stdafx.server.getPlayer(name);
    SimonPlayer simon = simonGame.getSimon();

    if (invitedPlayer == null)
    {
      simon.sendMessage(SimonGameError.PlayerNotFound);
      return;
    }

    if (SimonPlayer.isASimonPlayer(invitedPlayer))
    {
      simon.sendMessage(ChatHelper.getNameInFormat(invitedPlayer) + " is already in a game!");
      return;
    }

    simonGame.invitePlayer(invitedPlayer);
  }

  private static void init(SimonGame simonGame)
  {
    simonGame.init();
  }

  private static void start(SimonGame simonGame)
  {
    if (simonGame.getGameState() != SimonGame.GameState.WaitingForStart)
    {
      simonGame.getSimon().sendMessage(SimonGameError.InvalidStateForStart);
      return;
    }
    simonGame.setGameState(SimonGame.GameState.InProgress);

    simonGame.getSimon().sendMessage("Starting game now!");

    Timer timer = new Timer(simonGame, Timer.Action.StartGame, 5);

    simonGame.teleportToGame();

    timer.execute();
  }

  public static void listPlayers(SimonGame simonGame, SimonPlayer sender)
  {
    if (simonGame.getPlayerList().isEmpty())
    {
      sender.sendMessage("There are no players in your Simon Says Game yet!");
      return;
    }

    // Shows number of players in the game
    sender.sendMessage("There are " + Stdafx.HighlightColor + simonGame.getPlayerList().size() + Stdafx.textColor + "/"
                       + Stdafx.HighlightColor + Stdafx.PlayerLimit + Stdafx.textColor + " Players in this game!");

    sender.sendMessage(getSimonPlayerString(simonGame.getSimon()));
    for (SimonPlayer simonPlayer : simonGame.getPlayerList())
    {
      sender.sendMessage(getSimonPlayerString(simonPlayer));
    }
  }

  private static String getSimonPlayerString(SimonPlayer simonPlayer)
  {
    return simonPlayer.getPlayerType().getColor() + simonPlayer.getPlayerType().getName() + Stdafx.textColor + " "
           + simonPlayer.getName();
  }

  public static void displaySimonSaysCommandHelp(Player sender)
  {
    SimonPlayer.sendMessage(sender, "/simon says <init/leave/invite/list>");
  }
}
