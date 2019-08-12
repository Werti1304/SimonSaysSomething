package com.werti.simonsayssomething;

import com.werti.Stdafx;
import com.werti.StrRes.PlatformError;
import com.werti.simonsayssomething.Helper.Coords;
import com.werti.simonsayssomething.Helper.LocationHelper;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;

public class PlatformGenerator
{
  private SimonGame simonGame;
  private SimonPlayer simon;
  // When set, the players may choose their own locations to play
  private boolean freeMode;

  public PlatformGenerator(SimonGame simonGame, boolean freeMode)
  {
    this.simonGame = simonGame;
    this.freeMode = freeMode;

    this.simon = simonGame.getSimon();
  }

  public static HashMap<Location, SimonPlayer> getDemandedPlayerLocations()
  {
    return null;
  }

  public PlatformError isSuitedLocation()
  {
    if (!hasSimonEnoughSpace())
    {
      return PlatformError.SimonNotEnoughFreeBlocks;
    }

    // First we round the yaw of Simon to one of the 4 cardinal points
    float yaw = LocationHelper.roundYaw(simon.getLocation().getYaw());

    setSimonGameLocation(yaw);

    yaw = LocationHelper.mirrorYaw(yaw);

    if (freeMode)
    {
      ArrayList<Coords> coordsFreeMode = doPlayerLocationsFreeMode();

      if (havePlayersEnoughSpace(coordsFreeMode))
      {
        generatePlatform(coordsFreeMode);
      }
      else
      {
        return PlatformError.PlayerNotEnoughFreeBlocks;
      }
    }
    else
    {
      ArrayList<Coords> coordsFixedMode = doPlayerLocationsFixedMode(yaw);

      if (havePlayersEnoughSpace(coordsFixedMode))
      {
        generatePlatform(coordsFixedMode);
      }
      else
      {
        return PlatformError.PlayerNotEnoughFreeBlocks;
      }

      // Teleport all players to their positions
      for (SimonPlayer simonPlayer : simonGame.getPlayerList())
      {
        simonPlayer.teleportToGameLocation();
      }
    }

    // Set Simons Block last so we don't have to remove him when something above goes wrong
    setSimonBlock();

    return PlatformError.None;
  }

  private ArrayList<Coords> doPlayerLocationsFreeMode()
  {
    ArrayList<Coords> coordsList = new ArrayList<>();

    for (SimonPlayer simonPlayer : simonGame.getPlayerList())
    {
      Location location = simonPlayer.getLocation();
      location.subtract(simon.getGameLocation());

      coordsList.add(new Coords(location));

      simonPlayer.setGameLocation(location);
    }

    return coordsList;
  }

  private ArrayList<Coords> doPlayerLocationsFixedMode(float yaw)
  {
    ArrayList<Coords> coordsList = new ArrayList<>();

    for (int i = 0; i < 4; i++)
    {
      for (int c = 0; c < 3; c++)
      {
        // The goldblocks are only generated if players are there for them
        if (simonGame.getPlayerList().size() >= ((c * 4) + i) + 1)
        {
          // Formulas that calculate relative distance of fixed player position relative to Simons position
          int x = 6 - (c * 2);
          int y = 0;
          int z = -3 + i * 2;

          Coords coords = new Coords(x, y, z);

          coordsList.add(coords);

          // Formula to get the player out of the playerlist
          SimonPlayer player = simonGame.getPlayerList().get((c * 4) + i);

          // Set the players position for the game
          setPlayerGameLocation(yaw, coords.copyByValue(), player);
        }
      }
    }

    return coordsList;
  }

  private void setPlayerGameLocation(float yaw, Coords coords, SimonPlayer player)
  {
    Location location = simon.getGameLocation().getBlock().getLocation();

    // Get middle of block
    location.add(0.5, 0, 0.5);

    // Set Head-"height"
    location.setPitch(0.5f);

    // Set Head/Body-Rotation
    location.setYaw(yaw);

    LocationHelper.adjustRelativePositionToPlayer(LocationHelper.getCardinalDirection(simon.getLocation()), coords);

    // Add relative coordinates to absolute position of simon
    location.add(coords.getX(), coords.getY(), coords.getZ());

    // Set the players location while in the game
    player.setGameLocation(location);
  }

  private boolean havePlayersEnoughSpace(ArrayList<Coords> coordsList)
  {
    ArrayList<Coords> coordsCheckList = new ArrayList<>();

    coordsCheckList.add(new Coords(-1, 1, 0));
    coordsCheckList.add(new Coords(0, 1, -1));
    coordsCheckList.add(new Coords(0, 1, 0));
    coordsCheckList.add(new Coords(0, 2, 0));
    coordsCheckList.add(new Coords(1, 1, 0));
    coordsCheckList.add(new Coords(0, 1, 1));

    for (Coords coords : coordsList)
    {
      for (Coords checkCoords : coordsCheckList)
      {
        checkCoords.add(coords);
      }

      if (!LocationHelper.blocksAreFree(coords.getLocation(simon.getWorld()), coordsCheckList))
      {
        return false;
      }

      for (Coords checkCoords : coordsCheckList)
      {
        checkCoords.subtract(coords);
      }
    }

    return true;
  }

  private void generatePlatform(ArrayList<Coords> coordsList)
  {
    for (Coords coords : coordsList)
    {
      coords.add(0, -1, 0);
    }

    LocationHelper.setBlocks(simon.getLocation(), coordsList, Material.GOLD_BLOCK);
  }

  private void setSimonGameLocation(float yaw)
  {
    Location simonLocation = simon.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);

    simonLocation.setPitch(0.5f); // Head is looking to the front
    simonLocation.setYaw(yaw);

    // After everything is configured, set the gamelocation for simon
    simon.setGameLocation(simonLocation);
  }

  private void setSimonBlock()
  {
    simon.getLocation().subtract(0, 1, 0).getBlock().setType(Material.DIAMOND_BLOCK);
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
