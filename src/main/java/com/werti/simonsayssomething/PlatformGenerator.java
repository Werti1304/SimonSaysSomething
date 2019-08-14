package com.werti.simonsayssomething;

import com.werti.Stdafx;
import com.werti.StrRes.PlatformError;
import com.werti.simonsayssomething.Helper.Coords;
import com.werti.simonsayssomething.Helper.LocationHelper;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Iterator;

public class PlatformGenerator
{
  private SimonGame simonGame;
  private SimonPlayer simon;
  // When set, the players may choose their own locations to play
  private boolean freeMode;
  ArrayList<Coords> coordsList = new ArrayList<>();

  public PlatformGenerator(SimonGame simonGame, boolean freeMode)
  {
    this.simonGame = simonGame;
    this.freeMode = freeMode;

    this.simon = simonGame.getSimon();
  }

  public void removePlatform(SimonPlayer simonPlayer)
  {
    // Check if blocks were even changed
    if (simonPlayer.getOldBlocks().isEmpty())
    {
      return;
    }

    Location platformLocation = simonPlayer.getGameLocation().getBlock().getLocation();
    platformLocation.subtract(0, 1, 0);

    for (Material oldBlock : simonPlayer.getOldBlocks())
    {
      platformLocation.getBlock().setType(oldBlock);

      platformLocation.add(0, 1, 0);
    }
  }

  public PlatformError getPlatformLocation()
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
      getPlayerLocationsFreeMode();
    }
    else
    {
      getPlayerLocationsFixedMode();

      // Adjusts relative platform to facing of player
      LocationHelper.adjustRelativePositionToPlayer(LocationHelper.getCardinalDirection(simon.getGameLocation()),
              coordsList);
    }

    if (!havePlayersEnoughSpace())
    {
      return PlatformError.PlayerNotEnoughFreeBlocks;
    }

    setGameLocations(yaw);

    // Teleport all players to their positions
    for (SimonPlayer simonPlayer : simonGame.getPlayerList())
    {
      simonPlayer.teleportToGameLocation();
    }

    return PlatformError.None;
  }

  // Fills coordslist
  private void getPlayerLocationsFreeMode()
  {
    for (SimonPlayer simonPlayer : simonGame.getPlayerList())
    {
      Location gameLocation = simonPlayer.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);

      gameLocation.subtract(simon.getGameLocation());

      coordsList.add(new Coords(gameLocation));
    }
  }

  // Fills coordslist
  private void getPlayerLocationsFixedMode()
  {
    for (int i = 0; i < 4; i++)
    {
      for (int c = 0; c < 3; c++)
      {
        // Formulas that calculate relative distance of fixed player position relative to Simons position
        int x = 6 - (c * 2);
        int y = 0;
        int z = -3 + i * 2;

        coordsList.add(new Coords(x, y, z));
      }
    }
  }

  private void setGameLocations(float yaw)
  {
    // Iterate through both all players and available locations
    Iterator<Coords> availableLocationsIterator = coordsList.iterator();
    Iterator<SimonPlayer> simonPlayerIterator = simonGame.getPlayerList().iterator();

    while (availableLocationsIterator.hasNext() && simonPlayerIterator.hasNext())
    {
      setPlayerGameLocation(yaw, availableLocationsIterator.next(), simonPlayerIterator.next());
    }
  }

  public void saveChangedBlocks()
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
      simonPlayer.getOldBlocks().add(coords.getLocation(simon.getWorld()).getBlock().getType());

      coords.add(0, 1, 0);
    }
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

    // Add relative coordinates to absolute position of simon
    location.add(coords.getX(), coords.getY(), coords.getZ());

    // Set the players location while in the game
    player.setGameLocation(location);
  }

  private boolean havePlayersEnoughSpace()
  {
    ArrayList<Coords> coordsCheckList = new ArrayList<>();

    coordsCheckList.add(new Coords(0, 0, 1));
    coordsCheckList.add(new Coords(1, 0, 0));
    coordsCheckList.add(new Coords(0, 0, -1));
    coordsCheckList.add(new Coords(-1, 0, 0));

    coordsCheckList.add(new Coords(-1, 1, 0));
    coordsCheckList.add(new Coords(0, 1, -1));
    coordsCheckList.add(new Coords(1, 1, 0));
    coordsCheckList.add(new Coords(0, 1, 1));

    coordsCheckList.add(new Coords(0, 2, 0));

    for (Coords coords : coordsList)
    {
      for (Coords checkCoords : coordsCheckList)
      {
        checkCoords.add(coords);
      }

      if (!LocationHelper.blocksAreFree(simon.getGameLocation(), coordsCheckList))
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

  private void setSimonGameLocation(float yaw)
  {
    Location simonLocation = simon.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);

    simonLocation.setPitch(0.5f); // Head is looking to the front
    simonLocation.setYaw(yaw);

    // After everything is configured, set the gamelocation for simon
    simon.setGameLocation(simonLocation);
  }

  // A "platform" includes all blocks generated for a Simon Says Game
  public void generatePlatform()
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
