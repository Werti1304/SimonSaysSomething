package com.werti.simonsayssomething.EventHandler;

import com.werti.StrRes;
import com.werti.StrRes.SimonGameError;
import com.werti.simonsayssomething.Commands.CommandSwitch;
import com.werti.simonsayssomething.Commands.CommandSwitchAdmin;
import com.werti.simonsayssomething.SimonGame;
import com.werti.simonsayssomething.SimonPlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandExecuterSimon implements CommandExecutor
{
  @Override
  public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] args)
  {
    // Firstly check if the sender is even a Player
    if (!(commandSender instanceof Player))
    {
      SimonPlayer.sendMessage(commandSender, SimonGameError.InvalidCommandSender);
      return true;
    }

    Player player = (Player) commandSender;

    SimonPlayer simonPlayer = SimonPlayer.get(player);

    if (args.length == 0)
    {
      // Check if the player already is a player in a Simon-Game
      if (simonPlayer != null)
      {
        simonPlayer.sendMessage(SimonGameError.AlreadyInAGame);
        return true;
      }

      // Make him into a Simon
      SimonPlayer newSimon = SimonPlayer.add(player, StrRes.PlayerType.Simon);

      // Then start a new game with him as simon
      // Todo: Add new permission to start a new game
      SimonGame simonGame = new SimonGame(newSimon);
      return true;
    }

    if (args.length == 1 || !Objects.equals(args[0], "says"))
    {
      CommandSwitch.displaySimonSaysCommandHelp(simonPlayer.getPlayer());
      return true;
    }

    args[1] = args[1].toLowerCase();

    if (SimonPlayer.isAdmin(player))
    {
      StrRes.AdminCommand adminCommand = StrRes.AdminCommand.getCommandFromString(args[1]);

      if (adminCommand != null)
      {
        if (!CommandSwitchAdmin.commandSwitch(player, adminCommand, args))
        {
          CommandSwitch.displaySimonSaysCommandHelp(player);
          return true;
        }
        else
        {
          return true;
        }
      }
    }

    StrRes.Command strCommand = StrRes.Command.getCommandFromString(args[1]);

    if (strCommand == null)
    {
      CommandSwitch.displaySimonSaysCommandHelp(player.getPlayer());
      return true;
    }

    if (simonPlayer == null)
    {
      // Commands that can be used without being already in a game
      CommandSwitch.switchCommandsOutsideRound(player, strCommand, args);
    }
    else
    {
      CommandSwitch.switchCommandsInRound(simonPlayer, strCommand, args);
    }
    return true;

  }
}
