package com.werti;

import org.bukkit.ChatColor;

import java.util.Objects;

import static com.werti.StrRes.Command.Requirement.*;

// String Resources
public class StrRes
{
  public enum Command
  {
    // Player Commands
    StartNewGame("Simon", OutSideGame),
    InitGame("init", IsSimon),
    // Initialize game (platform)
    StartGame("start", IsSimon),
    // Start Game
    LeaveGame("leave", InAGame),
    //TODO functionality
    InvitePlayers("invite", IsSimon, "Player"),
    //TODO functionality
    ListPlayers("list", InAGame),
    //TODO functionality
    AcceptInvite("accept", OutSideGame, "Player"),
    // Accept Invite
    DeclineInvite("decline", OutSideGame, "Player"); // Decline Invite

    // Admin Commands
    /*AddPlayer("simon admin add <PlayerName>"), //TODO functionality
    ListGames("simon admin listgames"), //TODO functionality
    ListAllPlayers("simon admin listallplayers"), //TODO functionality
    BanPlayer("simon admin ban <PlayerName>"), //TODO functionality
    RemoveOldItems("simon admin removeold"); //TODO functionality
    */

    Requirement requirement;
    private String command;
    private String[] arguments;

    Command(String command, Requirement requirement, String... arguments)
    {
      this.command = command;
      this.arguments = arguments;
      this.requirement = requirement;
    }

    public static Command getCommandFromString(String string)
    {
      for (Command command : Command.values())
      {
        if (Objects.equals(string, command.getCommand()))
        {
          return command;
        }
      }
      return null;
    }

    public Requirement getRequirement()
    {
      return requirement;
    }

    public String[] getArguments()
    {
      return arguments;
    }

    public String getCommand()
    {
      return command;
    }

    public enum Requirement
    {
      OutSideGame,
      // Has to be in no game
      InAGame,
      // Only has to be in a game no matter who
      IsSimon,
      // Has to be in a game and the simon of the game
      IsPlayer,
      // has to be in a game and being a player
      None
    }
  }

  public enum ConfigValue
  {
    //All elements of the config by their name and default-value
    MenuSlotNumber("Menu-Slot", 8),
    MenuNameModifiers("Menu-Name-Modifier", "$o"),
    HighlightColor("Simon-Says-Color", "$a"),
    BackgroundMaterial("Background-Material", "STAINED_GLASS_PANE"),
    SimonMaxHeight("Simon-Height-Max", 3),
    // Describes how many additional blocks simon is allowed to set himself in the air
    Tickrate("Server-Tickrate", 20),
    PlayerLimit("Game-Players-Limit", 12);

    private String name;
    private Object defaultValue;

    ConfigValue(String name, Object defaultValue)
    {
      this.name = name;
      this.defaultValue = defaultValue;
    }

    public String getName()
    {
      return name;
    }

    public Object getValue()
    {
      return defaultValue;
    }
  }

  public enum PlayerType
  {
    Simon("Simon", ChatColor.GREEN),
    Player("Player", ChatColor.BLUE),
    // Todo: Make a floor for the players
    Banned("banned", ChatColor.RED),
    // Todo: Unbanning mechanism after x seconds so the player can join another game again
    Spectator("Spectator", ChatColor.YELLOW),
    // Todo: Let them spectate the spectacle / Make a pushy circle around the players
    None("None", ChatColor.LIGHT_PURPLE);

    private String name;
    private ChatColor color;

    PlayerType(String name, ChatColor color)
    {
      this.name = name;
      this.color = color;
    }

    public String getName()
    {
      return name;
    }

    public ChatColor getColor()
    {
      return color;
    }
  }

  public enum SimonGameError implements SimonError
  {
    //All elements of the config by their name and default-value
    NotASimonSaysPlayer("You must be part of a Simon Says Game to do that!"),
    NotSimon("You must be Simon to do that!"),
    NotPlayer("You must be a Player of a Simon Says Game to do that!"),
    AlreadyInAGame("You are already part of a Simon Says Game!"),
    IsInGame("You can't use this while being in a Simon Game!"),
    InvalidCommandSender("You must be a player to use this command!"),
    InvalidStateForInit("Game can't be initialized now!"),
    InvalidStateForStart("Game can't be started now!"),
    PlayerNotFound("I couldn't find this player on your server!"),
    SimonGameNotFound("I couldn't find this game!"),
    CouldntStartGame("Couldn't Start game! Has Simon left?");

    private String error;

    SimonGameError(String error)
    {
      this.error = error;
    }

    public String getError()
    {
      return error;
    }
  }

  public enum PlatformError implements SimonError
  {
    //All elements of the config by their name and default-value
    SimonNotEnoughFreeBlocks(
            "There has to be at least space for " + Stdafx.HighlightColor + Stdafx.SimonMaxHeight + ChatColor.GRAY
            + " blocks above Simon!"),
    PlayerNotEnoughFreeBlocks("There isn't enough space for all players!"),
    None("");

    private String error;

    PlatformError(String error)
    {
      this.error = error;
    }

    public String getError()
    {
      return error;
    }
  }

  public interface SimonError
  {
    String getError();
  }
}
