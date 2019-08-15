package com.werti.simonsayssomething.Commands;

import com.werti.Stdafx;
import com.werti.StrRes;
import com.werti.simonsayssomething.Helper.ChatHelper;
import com.werti.simonsayssomething.SimonGame;
import com.werti.simonsayssomething.SimonPlayer;
import org.bukkit.entity.Player;

public class CommandSwitchAdmin
{
  /**
   * @return CommandSwitch succeeded
   */
  public static boolean commandSwitch(Player player, StrRes.AdminCommand command, String[] args)
  {
    if (command.getRequirement() == StrRes.Requirement.None)
    {
      return commandSwitchGeneral(player, command, args);
    }

    if (SimonPlayer.isASimonPlayer(player))
    {
      return commandSwitchInside(SimonPlayer.get(player), command, args);
    }
    else
    {
      return commandSwitchOutside(player, command, args);
    }

  }

  private static boolean commandSwitchGeneral(Player player, StrRes.AdminCommand command, String args[])
  {
    switch (command)
    {
      case ListGamesGlobal:
        listGamesGlobal(player);
        return true;
      case ListPlayersGlobal:
        listPlayersGlobal(player);
        return true;
    }

    return false;
  }

  private static boolean commandSwitchOutside(Player player, StrRes.AdminCommand command, String args[])
  {
    return false;
  }

  private static boolean commandSwitchInside(SimonPlayer player, StrRes.AdminCommand command, String args[])
  {
    switch (command)
    {
      case AddPlayer:
        addPlayer(player, args[2]);
        return true;
    }
    return false;
  }

  private static void listGamesGlobal(Player player)
  {
    int currentSimonGamesCount = SimonGame.getCurrentSimonGames().size();

    String singular1 = (currentSimonGamesCount == 1 ? "is" : "are");
    String singular2 = (currentSimonGamesCount == 1 ? "" : "s");

    SimonPlayer.sendMessage(player, "There " + singular1 + " currently "
                                    + ChatHelper.highlightString(Integer.toString(currentSimonGamesCount))
                                    + " Simon Says Game" + singular2 + (currentSimonGamesCount == 0 ? "." : ":"));
    for (SimonGame simonGame : SimonGame.getCurrentSimonGames())
    {
      SimonPlayer.sendMessage(player,
                              ChatHelper.getNameInFormat(simonGame.getSimon()) + " | "
                              + ChatHelper.highlightString(simonGame.getGameState().toString()));
    }
  }

  private static void listPlayersGlobal(Player player)
  {
    int currentSimonPLayersCount = SimonPlayer.getCurrentSimonPlayers().values().size();

    String singular1 = (currentSimonPLayersCount == 1 ? "is" : "are");
    String singular2 = (currentSimonPLayersCount == 1 ? "" : "s");

    SimonPlayer.sendMessage(player, "There " + singular1 + " currently "
                                    + ChatHelper.highlightString(Integer.toString(currentSimonPLayersCount))
                                    + " Simon Says Player" + singular2 + (currentSimonPLayersCount == 0 ? "." : ":"));

    for (SimonPlayer simonPlayer : SimonPlayer.getCurrentSimonPlayers().values())
    {
      String sendString = ChatHelper.getNameInFormat(simonPlayer);
      if (!simonPlayer.isSimon())
      {
        sendString += " in the game of " + ChatHelper.getNameInFormat(simonPlayer.getSimonGame().getSimon());
      }

      SimonPlayer.sendMessage(player, sendString);
    }
  }

  private static void addPlayer(SimonPlayer simon, String name)
  {
    Player player = Stdafx.server.getPlayer(name);

    if (player == null)
    {
      simon.sendMessage(StrRes.SimonGameError.PlayerNotFound);
      return;
    }

    simon.sendMessage(Stdafx.adminTextColor + "You added " + ChatHelper.getNameInFormat(player) + Stdafx.adminTextColor
                      + " to your game!");

    SimonPlayer simonPlayer = simon.getSimonGame().addPlayer(player);
  }
}
