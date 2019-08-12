package com.werti.simonsayssomething.EventHandler;

import com.werti.StrRes;
import com.werti.StrRes.SimonGameError;
import com.werti.simonsayssomething.CommandExecuter;
import com.werti.simonsayssomething.SimonGame;
import com.werti.simonsayssomething.SimonPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandExecuterSimon implements CommandExecutor
{
  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
  {
    // Firstly check if the sender is even a Player
    if (!(commandSender instanceof Player))
    {
      SimonPlayer.sendMessage(commandSender, SimonGameError.InvalidCommandSender);
      return true;
    }

    Player player = (Player) commandSender;

    SimonPlayer playerSender = SimonPlayer.get(player);

    if (strings.length == 0)
    {
      // Check if the player already is a player in a Simon-Game
      if (playerSender != null)
      {
        playerSender.sendMessage(SimonGameError.AlreadyInAGame);
        return true;
      }

      // Make him into a Simon
      SimonPlayer newSimon = SimonPlayer.add(player, StrRes.PlayerType.Simon);

      // Then start a new game with him as simon
      // Todo: Add new permission to start a new game
      SimonGame simonGame = new SimonGame(newSimon);
      return true;
    }

    if (strings.length == 1 || !Objects.equals(strings[0], "says"))
    {
      CommandExecuter.displaySimonSaysCommandHelp(playerSender.getPlayer());
      return true;
    }

    if (playerSender == null)
    {
      // Commands that can be used without being already in a game
      CommandExecuter.switchCommandsOutsideRound(player, strings);
    }
    else
    {
      CommandExecuter.switchCommandsInRound(playerSender, strings);
    }
    return true;

  }
}
