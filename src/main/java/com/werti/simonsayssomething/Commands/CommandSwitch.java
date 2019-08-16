package com.werti.simonsayssomething.Commands;

import com.werti.Stdafx;
import com.werti.StrRes;
import com.werti.StrRes.SimonGameError;
import com.werti.simonsayssomething.BukkitRunnables.Timer;
import com.werti.simonsayssomething.Helper.ChatHelper;
import com.werti.simonsayssomething.SimonGame;
import com.werti.simonsayssomething.SimonPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandSwitch
{
  // Switch through the command that comes after the "simon says" part
  public static void switchCommandsInRound(SimonPlayer simonPlayer, StrRes.Command command, String args[])
  {
    boolean isSimon = false;
    boolean isPlayer = false;

    // Check if the player already is in one of the simon games
    for (SimonGame simonGame : SimonGame.getCurrentSimonGames())
    {
      if (simonGame.getSimon() == simonPlayer)
      {
        isSimon = true;
      }
      else if (simonGame.getPlayerList().contains(simonPlayer))
      {
        isPlayer = true;
      }
    }

    switch (command.getRequirement())
    {
      case None:
      case InAGame:
        break;
      case OutSideGame:
        simonPlayer.sendMessage(SimonGameError.IsInGame);
        return;
      case IsSimon:
        if (!isSimon)
        {
          simonPlayer.sendMessage(SimonGameError.NotSimon);
          return;
        }
        break;
      case IsPlayer:
        if (!isPlayer)
        {
          simonPlayer.sendMessage(SimonGameError.NotPlayer);

          // Special message when Simon tries to leave the game
          if (isSimon && command == StrRes.Command.LeaveGame)
          {
            simonPlayer.sendMessage(SimonGameError.LeaveGameAsSimon);
          }
          return;
        }
        break;
    }

    // Get Game of this player
    SimonGame simonGameOfPlayer = SimonGame.getGameByPlayer(simonPlayer);

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
        leave(simonPlayer);
        return;
      case EndGame:
        End(simonGameOfPlayer);
        return;
      case ListPlayers:
        listPlayers(simonGameOfPlayer, simonPlayer);
        return;
      case Help:
        displayHelp(simonPlayer);
        return;
    }

    // Commands that need 1 additional argument
    if (args.length <= 2)
    {
      simonPlayer.sendMessage(ChatHelper.getFullCommand(command));
      return;
    }

    switch (command)
    {
      case InvitePlayers:
        invitePlayer(simonGameOfPlayer, args[2]);
        break;
      case KickPlayer:
        kickPlayer(simonGameOfPlayer, args[2]);
        return;
    }
  }

  public static void switchCommandsOutsideRound(Player player, StrRes.Command command, String args[])
  {
    if (command.getRequirement() != StrRes.Requirement.OutSideGame
        && command.getRequirement() != StrRes.Requirement.None)
    {
      SimonPlayer.sendMessage(player, SimonGameError.NotASimonSaysPlayer);
      return;
    }

    switch (command)
    {
      case Help:
        displayHelp(player);
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

  public static void startNewGame(Player player)
  {
    // Make him into a Simon
    SimonPlayer newSimon = SimonPlayer.add(player, StrRes.PlayerType.Simon);

    // Then start a new game with him as simon
    // Todo: Add new permission to start a new game
    SimonGame simonGame = new SimonGame(newSimon);
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
    sender.sendMessage("There are " + Stdafx.highlightColor + simonGame.getPlayerList().size() + Stdafx.textColor + "/"
                       + Stdafx.highlightColor + Stdafx.PlayerLimit + Stdafx.textColor + " Players in this game!");

    sender.sendMessage(getSimonPlayerString(simonGame.getSimon()));
    for (SimonPlayer simonPlayer : simonGame.getPlayerList())
    {
      sender.sendMessage(getSimonPlayerString(simonPlayer));
    }
  }

  private static void kickPlayer(SimonGame simonGame, String name)
  {
    Player invitedPlayer = Stdafx.server.getPlayer(name);
    SimonPlayer simon = simonGame.getSimon();

    if (invitedPlayer == null)
    {
      simon.sendMessage(SimonGameError.PlayerNotFound);
      return;
    }

    if (!SimonPlayer.isASimonPlayer(invitedPlayer))
    {
      simon.sendMessage(SimonGameError.NotInGame);
      return;
    }

    SimonPlayer simonPlayer = SimonPlayer.get(invitedPlayer);

    simonPlayer.playerLeave(false, true);
  }

  private static String getSimonPlayerString(SimonPlayer simonPlayer)
  {
    return simonPlayer.getPlayerType().getColor() + simonPlayer.getPlayerType().getName() + Stdafx.textColor + " "
           + simonPlayer.getName();
  }

  public static void displaySimonSaysCommandHelp(Player sender)
  {
    SimonPlayer.sendMessage(sender, "/simon says help");
  }

  private static void End(SimonGame simonGameOfPlayer)
  {
    simonGameOfPlayer.endGame();
  }

  private static void leave(SimonPlayer simonPlayer)
  {
    simonPlayer.playerLeave(false, false);
  }

  public static void displayHelp(Player sender)
  {
    SimonPlayer.sendMessage(sender, "All Simon Says commands available to you:");

    for (StrRes.Command command : StrRes.Command.values())
    {
      if (command.getRequirement() == StrRes.Requirement.OutSideGame
          || command.getRequirement() == StrRes.Requirement.None)
      {
        SimonPlayer.sendMessage(sender, ChatHelper.getFullCommand(command));
      }
    }

    if (SimonPlayer.isAdmin(sender))
    {
      for (StrRes.AdminCommand command : StrRes.AdminCommand.values())
      {
        if (command.getRequirement() == StrRes.Requirement.OutSideGame
            || command.getRequirement() == StrRes.Requirement.None)
        {
          SimonPlayer.sendMessage(sender, ChatHelper.getFullCommand(command));
        }
      }
    }
  }

  public static void displayHelp(SimonPlayer simonPlayer)
  {
    simonPlayer.sendMessage("All Simon Says commands available to you:");


    for (StrRes.Command command : StrRes.Command.values())
    {
      StrRes.Requirement requirement = command.getRequirement();

      if (requirement == StrRes.Requirement.InAGame
          || requirement == StrRes.Requirement.None
          || (requirement == StrRes.Requirement.IsSimon && simonPlayer.isSimon())
          || (requirement == StrRes.Requirement.IsPlayer && simonPlayer.isPlayer()))
      {

        simonPlayer.sendMessage(ChatHelper.getFullCommand(command));
      }
    }

    if (simonPlayer.isAdmin())
    {
      for (StrRes.AdminCommand command : StrRes.AdminCommand.values())
      {
        StrRes.Requirement requirement = command.getRequirement();

        if (requirement == StrRes.Requirement.InAGame
            || requirement == StrRes.Requirement.None
            || (requirement == StrRes.Requirement.IsSimon && simonPlayer.isSimon())
            || (requirement == StrRes.Requirement.IsPlayer && simonPlayer.isPlayer()))
        {
          simonPlayer.sendMessage(ChatHelper.getFullCommand(command));
        }
      }
    }
  }
}
