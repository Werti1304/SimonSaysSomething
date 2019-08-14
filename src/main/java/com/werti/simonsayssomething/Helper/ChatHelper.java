package com.werti.simonsayssomething.Helper;

import com.werti.Stdafx;
import com.werti.StrRes;
import com.werti.simonsayssomething.SimonPlayer;
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
    return Stdafx.HighlightColor + string + Stdafx.textColor;
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
      return StrRes.Command.StartNewGame.getCommand();
    }

    StringBuilder commandString = new StringBuilder("/simon says " + ChatHelper.highlightString(command.getCommand()));

    String[] arguments = command.getArguments();

    for (String argument : arguments)
    {
      commandString.append(" <").append(argument).append(">");
    }

    return commandString.toString();
  }
}
