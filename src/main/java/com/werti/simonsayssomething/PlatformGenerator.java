package com.werti.simonsayssomething;

import com.werti.Stdafx;
import com.werti.StrRes.PlatformError;
import com.werti.simonsayssomething.Helper.Coords;
import com.werti.simonsayssomething.Helper.LocationHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PlatformGenerator
{
  private SimonGame simonGame;
  private SimonPlayer simon;
  // When set, the players may choose their own locations to play
  private boolean freeMode;
  // Checklist that will be used to check coordinates
  ArrayList<Coords> coordsCheckList = new ArrayList<>();

  public PlatformGenerator(SimonGame simonGame, boolean freeMode)
  {
    this.simonGame = simonGame;
    this.freeMode = freeMode;

    this.simon = simonGame.getSimon();

    initCheckList();
  }

  void removePlayer(SimonPlayer simonPlayer)
  {
    // Check if blocks were even changed
    if (simonPlayer.getSavedBlocks().isEmpty())
    {
      return;
    }

    Location platformLocation = simonPlayer.getGameLocation().getBlock().getLocation();
    platformLocation.subtract(0, 1, 0);

    for (Material oldBlockMaterial : simonPlayer.getSavedBlocks().keySet())
    {
      platformLocation.getBlock().setType(oldBlockMaterial);
      platformLocation.getBlock().setData(simonPlayer.getSavedBlocks().get(oldBlockMaterial));

      platformLocation.add(0, 1, 0);
    }
  }

  // The functions must be called in a correct order, therefore it's packed in an execute-function
  // Gets platform position, sets player positions, saves blocks that will be changed, checks for enough space and,
  // when nothing went wrong, generates the platform
  PlatformError execute()
  {
    PlatformError platformError;

    if (!hasSimonEnoughSpace())
    {
      return PlatformError.SimonNotEnoughFreeBlocks;
    }

    setSimonGameLocation();

    platformError = saveChangedBlock(simon);
    if (platformError != PlatformError.None)
    {
      return platformError;
    }

    // Generates the platform for simon
    setPlayerBlocks(simon.getGameLocation(), Material.DIAMOND_BLOCK);

    // Adds platform and locations for every player currently in the game
    for (SimonPlayer simonPlayer : simonGame.getPlayerList())
    {
      platformError = addPlayer(simonPlayer);
      if (platformError != PlatformError.None)
      {
        return platformError;
      }
    }

    return PlatformError.None;
  }

  PlatformError addPlayer(SimonPlayer simonPlayer)
  {
    Coords playerCoords;

    playerCoords = getNextFreePlatform(simonPlayer);

    if (playerCoords == null)
    {
      return PlatformError.PlayerMaximumReached;
    }

    if (!playerHasSpace(playerCoords))
    {
      return PlatformError.PlayerNotEnoughFreeBlocks;
    }

    setPlayerGameLocation(playerCoords, simonPlayer);

    PlatformError platformError = saveChangedBlock(simonPlayer);
    if (platformError != PlatformError.None)
    {
      return platformError;
    }

    setPlayerBlocks(simonPlayer.getGameLocation(), Material.GOLD_BLOCK);

    return PlatformError.None;
  }

  private void initCheckList()
  {
    // No loops for better readability (this time!)

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

  private Coords getNextFreePlatform(SimonPlayer simonPlayer)
  {
    if (freeMode)
    {
      return new Coords(
          simonPlayer
              .getLocation()
              .getBlock()
              .getLocation()
              .add(0.5, 0, 0.5)
              .subtract(simon.getGameLocation()));
    }
    else
    {
      int playerCount = simonGame.getPlayerList().size() - 1;

      int x = 6 - ((int) Math.floor(playerCount / 4) * 2);
      int y = 0;
      int z = -3 + ((int) Math.floor(playerCount % 4) * 2);

      Coords coords = new Coords(x, y, z);

      LocationHelper.CardinalDirection cardinalDirection = LocationHelper.getCardinalDirection(simon.getGameLocation().getYaw());

      LocationHelper.adjustRelativePositionToPlayer(cardinalDirection, coords);

      if (x < 0)
      {
        return null;
      }

      return coords;
    }
  }

  @NotNull
  private Coords getPlayerLocationFreeMode(SimonPlayer simonPlayer)
  {
    Location gameLocation = simonPlayer.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);

    gameLocation.subtract(simon.getGameLocation());

    return new Coords(gameLocation);
  }

  private PlatformError saveChangedBlock(SimonPlayer simonPlayer)
  {
    Location location = simonPlayer.getGameLocation().clone();

    location.subtract(0, 1, 0);

    // Reset the saved blocks before the procedure
    simonPlayer.getSavedBlocks().clear();

    for (int k = 0; k < 3; k++)
    {
      Block block = location.getBlock();

      Material material = block.getType();

      if (material == Material.DOUBLE_PLANT)
      {
        return PlatformError.InvalidMaterialSave;
      }

      simonPlayer.getSavedBlocks().put(block.getType(), block.getData());

      location.add(0, 1, 0);
    }
    return PlatformError.None;
  }

  private void setPlayerGameLocation(Coords coords, SimonPlayer simonPlayer)
  {
    Location location = simon.getGameLocation().clone();

    // For now, the player will, regardless of position, have its gameLocation facing the opposite direction of simon
    float yaw = LocationHelper.mirrorYaw(simon.getGameLocation().getYaw());

    // Set Head/Body-Rotation
    location.setYaw(yaw);

    // Add relative coordinates to absolute position of simon
    location.add(coords.getX(), coords.getY(), coords.getZ());

    // Set the players location while in the game
    simonPlayer.setGameLocation(location);
  }

  private boolean playerHasSpace(Coords coords)
  {
    ArrayList<Coords> coordsToCheck = new ArrayList<>();

    Coords playerCoords;

    for (Coords checkCoords : coordsCheckList)
    {
      playerCoords = coords.clone();

      playerCoords.add(checkCoords);

      coordsToCheck.add(playerCoords.clone());
    }

    return LocationHelper.blocksAreFree(simon.getGameLocation(), coordsToCheck);
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

  private void setSimonGameLocation()
  {
    Location simonLocation = simon.getLocation().getBlock().getLocation().add(0.5, 0, 0.5);

    simonLocation.setPitch(0.5f); // Head is looking to the front

    // Rounding the yaw of Simon to one of the 4 cardinal points
    simonLocation.setYaw(LocationHelper.roundYaw(simon.getLocation().getYaw()));

    // After everything is configured, set the gamelocation for simon
    simon.setGameLocation(simonLocation);
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