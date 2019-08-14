package com.werti.simonsayssomething.Helper;

import com.werti.simonsayssomething.SimonPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class LocationHelper
{
  public static float roundYaw(float simonYaw)
  {
    switch (getCardinalDirection(simonYaw))
    {
      case SOUTH:
        return 0;
      case WEST:
        return 90;
      case NORTH:
        return 180;
      case EAST:
        return -90;
      default:
        return 0;
    }
  }

  public static boolean blockCoordsEqual(Location location1, Location location2)
  {
    return location1.getBlockX() == location2.getBlockX() && location1.getBlockY() == location2.getBlockY()
           && location1.getBlockZ() == location2.getBlockZ();
  }

  public static CardinalDirection getCardinalDirection(Player player)
  {
    return getCardinalDirection(player.getLocation());
  }

  public static CardinalDirection getCardinalDirection(Location location)
  {
    float yaw = location.getYaw();

    return getCardinalDirection(yaw);
  }

  public static CardinalDirection getCardinalDirection(float yaw)
  {
    if (yaw < 0)
    {
      yaw += 360;
    }
    if (yaw >= 315 || yaw < 45)
    {
      return CardinalDirection.SOUTH;
    }
    else if (yaw < 135)
    {
      return CardinalDirection.WEST;
    }
    else if (yaw < 225)
    {
      return CardinalDirection.NORTH;
    }
    else if (yaw < 315)
    {
      return CardinalDirection.EAST;
    }
    return CardinalDirection.NORTH;
  }

  /**
   * One-way: can only be used one time
   *
   * @param cardinalDirection direction the Zero-Point is facing (Simon)
   * @param coordsList        List of Coordinates
   */
  public static void adjustRelativePositionToPlayer(CardinalDirection cardinalDirection, ArrayList<Coords> coordsList)
  {
    // First, set the relative blocks in the right position to the
    // (Rotate / Mirror the structure relative to the player's cardinal direction (facing)
    for (Coords coords : coordsList)
    {
      Coords block = coords.copyByValue();

      adjustRelativePositionToPlayer(cardinalDirection, block);
    }
  }

  /**
   * One-way: can only be used one time
   * @param cardinalDirection direction the Zero-Point is facing (Simon)
   * @param block block that needs to be adjusted
   */
  public static void adjustRelativePositionToPlayer(CardinalDirection cardinalDirection, Coords block)
  {
    switch (cardinalDirection)
    {
      case EAST:
        block.set(block.getX(), block.getY(), block.getZ());
        break;
      case SOUTH:
        block.set(-block.getZ(), block.getY(), block.getX());
        break;
      case WEST:
        block.set(-block.getX(), block.getY(), -block.getZ());
        break;
      case NORTH:
        block.set(block.getZ(), block.getY(), -block.getX());
        break;
    }
  }

  public static void teleportPlayerOppositeFacing(Location startLocation, Coords coords, SimonPlayer player)
  {
    CardinalDirection cardinalDirection = getCardinalDirection(startLocation);

    adjustRelativePositionToPlayer(cardinalDirection, coords);

    Coords teleportCoords = coords.copyByValue();

    teleportCoords.add(startLocation);

    Location teleportLocation = new Location(startLocation.getWorld(),
            teleportCoords.getX(),
            teleportCoords.getY(),
            teleportCoords.getZ());

    // Add "facing" of player
    teleportLocation.setYaw(startLocation.getYaw());
    teleportLocation.setPitch(startLocation.getPitch());

    player.teleport(teleportLocation);
  }

  public static void setBlocks(Location startLocation, ArrayList<Coords> coordsList, Material material)
  {
    for (Coords coords : coordsList)
    {
      setBlock(startLocation, coords, material, true);
    }
  }

  public static void setBlock(Location startLocation, Location location, Material material)
  {
    // Set material of block
    location.getBlock().setType(material);
  }

  public static void setBlock(Location startLocation, Coords coords, Material material, boolean relativeCoords)
  {
    Coords absoluteCoords = coords.copyByValue();

    // Get absolute Coords
    if (relativeCoords)
    {
      coords.add(startLocation);
    }

    // Set the material to the relative coords
    setBlock(startLocation,
            new Location(startLocation.getWorld(), absoluteCoords.getX(), absoluteCoords.getY(), absoluteCoords.getZ()), material);
  }

  public static float mirrorYaw(float yaw)
  {
    if (yaw > 0)
    {
      yaw -= 180;
    }
    else
    {
      yaw += 180;
    }
    return yaw;
  }

  /**
   * @param startLocation Absolute position of the Startlocation
   * @param coordsList     list of block-positions relative to the startLocation
   * @return Whether all blocks are free (Material.Air)
   */
  public static boolean blocksAreFree(Location startLocation, ArrayList<Coords> coordsList)
  {
    CardinalDirection cardinalDirection = getCardinalDirection(startLocation);

    for (Coords coords : coordsList)
    {
      Coords block = coords.copyByValue();
      // Add the locations so the block-position is absolute
      block.add(startLocation);

      Location blockLocation = new Location(startLocation.getWorld(), block.getX(), block.getY(), block.getZ());

      if (blockLocation.getBlock().getType().isSolid())
      {
        return false;
      }
    }
    return true;
  }

  public enum CardinalDirection
  {
    SOUTH,
    WEST,
    NORTH,
    EAST
  }
}
