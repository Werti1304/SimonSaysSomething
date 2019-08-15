package com.werti;

import com.werti.simonsayssomething.Helper.ChatHelper;
import org.bukkit.ChatColor;

import java.util.Objects;

import static com.werti.StrRes.Requirement.*;

// String Resources
public class StrRes
{
  public enum Command implements SimonCommand
  {
    // Player Commands
    StartNewGame("simon", OutSideGame, "Start a new game"),
    InitGame("init", IsSimon, "Initialize the game"),
    // Initialize the round TODO: let players join when waiting for start
    StartGame("start", IsSimon, "Start the game (After initialization"),
    // Start the round
    EndGame("end", IsSimon, "End the game"),
    KickPlayer("kick", IsSimon, "Kick a player from your game", "Player"),
    LeaveGame("leave", IsPlayer, "Leave your game"),
    InvitePlayers("invite", IsSimon, "Invite a player to your game", "Player"),
    ListPlayers("list", InAGame, "Shows you a list of the players"),
    AcceptInvite("accept", OutSideGame, "Accept an invitation", "Player"),    // Accept Invite
    DeclineInvite("decline", OutSideGame, "Decline an invitation", "Player"), // Decline Invite
    Help("help", None, "Displays help");

    Requirement requirement;
    private String command;
    private String description;
    private String[] arguments;

    Command(String command, Requirement requirement, String description, String... arguments)
    {
      this.command = command;
      this.arguments = arguments;
      this.description = description;
      this.requirement = requirement;
    }

    public static com.werti.StrRes.Command getCommandFromString(String string)
    {
      for (com.werti.StrRes.Command command : StrRes.Command.values())
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

    public String getDescription()
    {
      return description;
    }
  }

  // Admin-Commands are always tried first when an admin executes a command
  public enum AdminCommand implements SimonCommand
  {
    // Player Commands
    AddPlayer("add", InAGame, "Add a player to your game", "Player"),
    ListGamesGlobal("listgames", None, "List all games"),
    ListPlayersGlobal("listplayers", None, "List all players");

    // Admin Commands
    /*AddPlayer("simon admin add <PlayerName>"), //TODO functionality
    ListGames("simon admin listgames"), //TODO functionality
    ListAllPlayers("simon admin listallplayers"), //TODO functionality
    BanPlayer("simon admin ban <PlayerName>"), //TODO functionality
    RemoveOldItems("simon admin removeold"); //TODO functionality
    */

    Requirement requirement;
    private String command;
    private String description;
    private String[] arguments;

    AdminCommand(String command, Requirement requirement, String description, String... arguments)
    {
      this.command = command;
      this.arguments = arguments;
      this.description = description;
      this.requirement = requirement;
    }

    public static AdminCommand getCommandFromString(String string)
    {
      for (AdminCommand command : AdminCommand.values())
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

    public String getDescription()
    {
      return description;
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
    NotInGame("This player is not in your game!"),
    InvalidCommandSender("You must be a player to use this command!"),
    InvalidStateForInit("Game can't be initialized now!"),
    InvalidStateForStart("Game can't be started now!"),
    PlayerNotFound("I couldn't find this player on your server!"),
    SimonGameNotFound("I couldn't find this game!"),
    CouldntStartGame("Couldn't Start game! Has Simon left?"),
    GameAlreadyStarted("The game has already started!"),
    LeaveGameAsSimon("If you wish to end the game and thus, leave, type /simon says " + Command.EndGame.getCommand()),
    MaximumPlayersReached("The maximum of " + ChatHelper.highlightString(Integer.toString(Stdafx.PlayerLimit)));

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
        "There has to be at least space for " + Stdafx.highlightColor + Stdafx.SimonMaxHeight + ChatColor.GRAY
        + " blocks above Simon!"),
    PlayerNotEnoughFreeBlocks("There isn't enough space for all players!"),
    PlayerMaximumReached("The game is full!"),
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

  public interface SimonCommand
  {
    public Requirement getRequirement();

    public String[] getArguments();

    public String getCommand();

    public String getDescription();
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
