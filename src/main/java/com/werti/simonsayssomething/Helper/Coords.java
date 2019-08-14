package com.werti.simonsayssomething.Helper;

import org.bukkit.Location;
import org.bukkit.World;

public class Coords
{
  private int x;
  private int y;
  private int z;

  public Coords(int x, int y, int z)
  {
    set(x, y, z);
  }

  public Coords(Location location)
  {
    set(location.getBlockX(), location.getBlockY(), location.getBlockZ());
  }

  public Coords add(Location location)
  {
    add(location.getBlockX(), location.getBlockY(), location.getBlockZ());

    return this;
  }

  public void add(Coords coords)
  {
    add(coords.getX(), coords.getY(), coords.getZ());
  }

  public void add(double x, double y, double z)
  {
    this.x += x;
    this.y += y;
    this.z += z;
  }

  public void set(double x, double y, double z)
  {
    set(((int) x), ((int) y), ((int) z));
  }

  public void set(int x, int y, int z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Coords copyByValue()
  {
    return new Coords(x, y, z);
  }

  public Location getLocation(World world)
  {
    return new Location(world, x, y, z);
  }

  public int getX()
  {
    return x;
  }

  public void setX(int x)
  {
    this.x = x;
  }

  public int getY()
  {
    return y;
  }

  public void setY(int y)
  {
    this.y = y;
  }

  public int getZ()
  {
    return z;
  }

  public void setZ(int z)
  {
    this.z = z;
  }

  public void subtract(Coords coords)
  {
    subtract(coords.getX(), coords.getY(), coords.getZ());
  }

  public void subtract(int x, int y, int z)
  {
    this.x -= x;
    this.y -= y;
    this.z -= z;
  }
}