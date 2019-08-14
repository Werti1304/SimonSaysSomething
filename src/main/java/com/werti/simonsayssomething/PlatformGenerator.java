package com.werti.simonsayssomething;

import com.werti.Stdafx;
import com.werti.StrRes.PlatformError;
import com.werti.simonsayssomething.Helper.Coords;
import com.werti.simonsayssomething.Helper.LocationHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ListIterator;

public class PlatformGenerator
{
  private SimonGame simonGame;
  private SimonPlayer simon;
  // When set, the players may choose their own locations to play
  private boolean freeMode;
  LinkedHashMap<Coords, SimonPlayer> coordsMap = new LinkedHashMap<>();
  // Checklist that will be used to check coordinates
  ArrayList<Coords> coordsCheckList = new ArrayList<>();

  public PlatformGenerator(SimonGame simonGame, boolean freeMode)
  {
    this.simonGame = simonGame;
    this.freeMode = freeMode;

    this.simon = simonGame.getSimon();

    initCheckList();
  }

  void removePlatform(SimonPlayer simonPlayer)
  {
    // Check if blocks were even changed
    if (simonPlayer.getOldBlocks().isEmpty())
    {
      return;
    }

    Location platformLocation = simonPlayer.getGameLocation().getBlock().getLocation();
    platformLocation.subtract(0, 1, 0);

    for (Material oldBlockMaterial : simonPlayer.getOldBlocks().keySet())
    {
      platformLocation.getBlock().setType(oldBlockMaterial);
      platformLocation.getBlock().setData(simonPlayer.getOldBlocks().get(oldBlockMaterial));

      platformLocation.add(0, 1, 0);
    }
  }

  // The functions must be called in a correct order, therefore it's packed in an execute-function
  // Gets platform position, sets player positions, saves blocks that will be changed, checks for enough space and,
  // when nothing went wrong, generates the platform
  PlatformError execute()
  {
    if (!hasSimonEnoughSpace())
    {
      return PlatformError.SimonNotEnoughFreeBlocks;
    }

    setSimonGameLocation();

    PlatformError error = fillCoordsMap();

    if (fillCoordsMap() != PlatformError.None)
    {
      return error;
    }

    setGameLocations();

    saveChangedBlocks();

    generatePlatform();

    return PlatformError.None;
  }

  PlatformError addNewPlayer(SimonPlayer simonPlayer)
  {
    Coords playerCoords;

    playerCoords = getNextFreePlatform(simonPlayer);

    if (playerCoords == null)
    {
      return PlatformError.PlayerMaximumReached;
    }

    // For now, the player will, regardless of position, have its gameLocation facing the opposite direction of simon
    float yaw = LocationHelper.mirrorYaw(simon.getGameLocation().getYaw());

    setPlayerGameLocation(yaw, playerCoords, simonPlayer);

    saveChangedBlock(simonPlayer);

    setPlayerBlocks(simonPlayer.getGameLocation(), Material.GOLD_BLOCK);

    return PlatformError.None;
  }

  private void initCheckList()
  {
    // No loops for better readability

    coordsCheckList.add(new Coords(0, 0, 1));
    coordsCheckList.add(new Coords(1, 0, 0));
    coordsCheckList.add(new Coords(0, 0, -1));
    coordsCheckList.add(new Coords(-1, 0, 0));

    coordsCheckList.add(new Coords(-1, 1, 0));
    coordsCheckList.add(new Coords(0, 1, -1));
    coordsCheckList.add(new Coords(1, 1, 0));
    coordsCheckList.add(new Coords(0, 1, 1));

    coordsCheckList.add(new Coords(0, 2, 0));
  }

  @NotNull
  private Coords getPlayerLocationFreeMode(SimonPlayer simonPlayer)
  {
    Location gameLocation = simonPlayer.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);

    gameLocation.subtract(simon.getGameLocation());

    return new Coords(gameLocation);
  }

  @Nullable
  private Coords getNextFreePlatform(SimonPlayer simonPlayer)
  {
    if (freeMode)
    {
      return getPlayerLocationFreeMode(simonPlayer);
    }
    else
    {
      for (Coords coords : coordsMap.keySet())
      {
        if (coordsMap.get(coords) == null)
        {
          coordsMap.replace(coords, simonPlayer);
          return coords;
        }
      }
      return null;
    }
  }

  private PlatformError fillCoordsMap()
  {
    if (freeMode)
    {
      getPlayerLocationsFreeMode();
    }
    else
    {
      getPlayerLocationsFixedMode();
    }

    for (SimonPlayer simonPlayer : simonGame.getPlayerList())
    {
      Coords coords = getNextFreePlatform(simonPlayer);

      coordsMap.replace(coords, simonPlayer);
    }

    if (!havePlayersEnoughSpace())
    {
      return PlatformError.PlayerNotEnoughFreeBlocks;
    }

    return PlatformError.None;
  }

  // Fills coordslist
  private void getPlayerLocationsFreeMode()
  {
    for (SimonPlayer simonPlayer : simonGame.getPlayerList())
    {
      Coords gameCoords = getPlayerLocationFreeMode(simonPlayer);

      coordsMap.put(gameCoords, simonPlayer);
    }
  }

  // Fills coordslist
  private void getPlayerLocationsFixedMode()
  {
    ListIterator<SimonPlayer> simonPlayerListIterator = simonGame.getPlayerList().listIterator();

    LocationHelper.CardinalDirection simonDirection = LocationHelper.getCardinalDirection(simon);

    for (int i = 0; i < 3; i++)
    {
      for (int c = 0; c < 4; c++)
      {
        // Formulas that calculate relative distance of fixed player position relative to Simons position
        int x = 6 - (i * 2);
        int y = 0;
        int z = -3 + c * 2;

        Coords newCoords = new Coords(x, y, z);

        LocationHelper.adjustRelativePositionToPlayer(simonDirection, newCoords);

        if (simonPlayerListIterator.hasNext())
        {
          coordsMap.put(newCoords, simonPlayerListIterator.next());
        }
        else
        {
          coordsMap.put(newCoords, null);
        }
      }
    }
  }

  private void setGameLocations()
  {
    // For now, the player will, regardless of position, have its gameLocation facing the opposite direction of simon
    float yaw = LocationHelper.mirrorYaw(simon.getGameLocation().getYaw());

    for (Coords coords : coordsMap.keySet())
    {
      SimonPlayer simonPlayer = coordsMap.get(coords);

      if (simonPlayer != null)
      {
        setPlayerGameLocation(yaw, coords, coordsMap.get(coords));
      }
    }
  }

  private void saveChangedBlocks()
  {
    saveChangedBlock(simon);

    for (SimonPlayer simonPlayer : simonGame.getPlayerList())
    {
      saveChangedBlock(simonPlayer);
    }
  }

  private void saveChangedBlock(SimonPlayer simonPlayer)
  {
    Coords coords = new Coords(simonPlayer.getGameLocation());

    coords.subtract(0, 1, 0);

    for (int k = 0; k < 3; k++)
    {
      Block block = coords.getLocation(simon.getWorld()).getBlock();

      simonPlayer.getOldBlocks().put(block.getType(), block.getData());

      coords.add(0, 1, 0);
    }
  }

  private void setPlayerGameLocation(float yaw, Coords coords, SimonPlayer simonPlayer)
  {
    Location location = simon.getGameLocation().getBlock().getLocation();

    // Get middle of block
    location.add(0.5, 0, 0.5);

    // Set Head-"height"
    location.setPitch(0.5f);

    // Set Head/Body-Rotation
    location.setYaw(yaw);

    // Add relative coordinates to absolute position of simon
    location.add(coords.getX(), coords.getY(), coords.getZ());

    // Set the players location while in the game
    simonPlayer.setGameLocation(location);
  }

  private boolean havePlayersEnoughSpace()
  {
    for (Coords coords : coordsMap.keySet())
    {
      if (coordsMap.get(coords) != null)
      {
        playerHasSpace(coords);
      }
    }
    return true;
  }

  private boolean playerHasSpace(Coords coords)
  {
    ArrayList<Coords> coordsToCheck = new ArrayList<>();

    Coords playerCoords;

    for (Coords checkCoords : coordsCheckList)
    {
      playerCoords = coords.copyByValue();

      playerCoords.add(checkCoords);

      coordsToCheck.add(playerCoords.copyByValue());
    }

    return LocationHelper.blocksAreFree(simon.getGameLocation(), coordsCheckList);
  }

  private void setSimonGameLocation()
  {
    Location simonLocation = simon.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);

    simonLocation.setPitch(0.5f); // Head is looking to the front

    // Rounding the yaw of Simon to one of the 4 cardinal points
    simonLocation.setYaw(LocationHelper.roundYaw(simon.getLocation().getYaw()));

    // After everything is configured, set the gamelocation for simon
    simon.setGameLocation(simonLocation);
  }

  // A "platform" includes all blocks generated for a Simon Says Game
  private void generatePlatform()
  {
    // Generates the platform for simon
    setPlayerBlocks(simon.getGameLocation(), Material.DIAMOND_BLOCK);

    // Generates the platform for the players
    for (SimonPlayer simonPlayer : simonGame.getPlayerList())
    {
      setPlayerBlocks(simonPlayer.getGameLocation(), Material.GOLD_BLOCK);
    }
  }

  private void setPlayerBlocks(Location absoluteLocation, Material material)
  {
    Coords absoluteCoords = new Coords(absoluteLocation);

    absoluteCoords.subtract(0, 1, 0);

    LocationHelper.setBlock(simon.getGameLocation(), absoluteCoords, material, false);

    for (int i = 0; i < 2; i++)
    {
      absoluteCoords.add(0, 1, 0);

      LocationHelper.setBlock(simon.getGameLocation(), absoluteCoords, Material.AIR, false);
    }
  }

  private boolean hasSimonEnoughSpace()
  {
    ArrayList<Coords> blockList = new ArrayList<>();

    Location playerLocation = simon.getLocation().add(0, 2, 0);

    // Check if Simon has at least x Blocks free above him
    for (int i = 0; i < Stdafx.SimonMaxHeight; i++)
    {
      blockList.add(new Coords(0, i, 0));
    }

    return LocationHelper.blocksAreFree(playerLocation, blockList);
  }
}
