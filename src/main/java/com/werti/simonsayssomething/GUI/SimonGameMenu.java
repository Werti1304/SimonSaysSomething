package com.werti.simonsayssomething.GUI;

import com.werti.simonsayssomething.SimonPlayer;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SimonGameMenu extends SimonMenuFixture
{
  public SimonGameMenu(SimonSaysItemSafe menuOpeningItem, int size, String title)
  {
    super(menuOpeningItem, size, title);
  }

  @Override
  void initializeItems()
  {

  }

  @Override
  void itemClick(InventoryClickEvent clickEvent, SimonPlayer simonPlayer)
  {

  }
}
