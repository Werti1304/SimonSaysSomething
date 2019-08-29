package com.werti.simonsayssomething.GUI;

import com.werti.Stdafx;
import com.werti.simonsayssomething.SimonPlayer;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SimonGameMenu extends SimonMenuFixture
{
  public SimonGameMenu()
  {
    super(new SimonSaysItemSafe(Material.STICK, "Simon Game Menu"), 27, "Simon Game Menu");
  }

  @Override
  void initializeItems()
  {
    setItem(new SimonSaysItem(Material.DISPENSER, Stdafx.highlightColor + "Look left!"), 9);

    setItem(new SimonSaysItem(Material.DISPENSER, Stdafx.highlightColor + "Look right!"), 11);

    setItem(new SimonSaysItem(Material.FEATHER, Stdafx.highlightColor + "Jump!"), 13);

    setItem(new SimonSaysItem(Material.TRIPWIRE_HOOK, Stdafx.highlightColor + "Move Left!"), 15);

    setItem(new SimonSaysItem(Material.TRIPWIRE_HOOK, Stdafx.highlightColor + "Move right!"), 17);

    addPlaceHolders();
  }

  @Override
  void itemClick(InventoryClickEvent clickEvent, SimonPlayer simonPlayer)
  {

  }
}
