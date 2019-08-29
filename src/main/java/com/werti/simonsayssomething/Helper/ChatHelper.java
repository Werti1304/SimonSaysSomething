package com.werti.simonsayssomething.Helper;

import com.werti.Stdafx;
import com.werti.StrRes;
import com.werti.simonsayssomething.SimonPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatHelper
{
  public static String constructClickableString(String message, String command)
  {
    return "{\"text\":\"" + message + "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/simon says" + command
           + "\"}}";
  }

  public static String getNameInFormat(SimonPlayer simonPlayer)
  {
    return simonPlayer.getPlayerType().getColor() + simonPlayer.getName() + Stdafx.textColor;
  }

  public static String getNameInFormat(Player player)
  {
    return StrRes.PlayerType.None.getColor() + player.getName() + Stdafx.textColor;
  }

  public static String highlightString(String string)
  {
    return Stdafx.highlightColor + string + Stdafx.textColor;
  }

  public static String getFullCommandWithoutParams(StrRes.Command command)
  {
    if (command == StrRes.Command.StartNewGame)
    {
      return StrRes.Command.StartNewGame.getCommand();
    }

    return "/simon says " + command.getCommand();
  }

  public static String getFullCommand(StrRes.Command command)
  {
    if (command == StrRes.Command.StartNewGame)
    {
      return Stdafx.textColor + "/" + ChatHelper.highlightString(StrRes.Command.StartNewGame.getCommand());
    }

    return Stdafx.textColor + "/" + Stdafx.highlightColor + getCommand(command);
  }

  public static String getFullCommand(StrRes.AdminCommand command)
  {
    return "/" + ChatColor.RED + getCommand(command);
  }

  private static String getCommand(StrRes.SimonCommand simonCommand)
  {
    StringBuilder commandString = new StringBuilder("simon says " + simonCommand.getCommand());

    String[] arguments = simonCommand.getArguments();

    commandString.append(Stdafx.textColor);

    for (String argument : arguments)
    {
      commandString.append(" <").append(argument).append(">");
    }

    commandString.append(" - ");

    commandString.append(simonCommand.getDescription());

    return commandString.toString();
  }
}
