package com.werti.simonsayssomething.GUI;

import com.werti.Stdafx;
import com.werti.simonsayssomething.SimonGame;
import com.werti.simonsayssomething.SimonPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SimonLobbyMenu extends SimonMenuFixture
{
  public SimonLobbyMenu()
  {
    super(new SimonSaysItemSafe(Material.GOLD_INGOT, "Simon Says Menu"), 9, "Simon Says Menu");
  }

  @Override
  void initializeItems()
  {
    SimonSaysItem placeHolder = SimonSaysItem.getSpacer();

    setItem(new SimonSaysItem(Stdafx.initGameMaterial, ChatColor.GOLD + "Initialize"), 2);

    setItem(new SimonSaysItem(Stdafx.startGameMaterial, ChatColor.GREEN + "Start"), 4);

    setItem(new SimonSaysItem(Stdafx.endGameMaterial, ChatColor.DARK_BLUE + "End Game"), 6);

    addPlaceHolders();
  }

  @Override
  void itemClick(InventoryClickEvent clickEvent, SimonGame simonGame, SimonPlayer simonPlayer)
  {
    Material clickedMaterial = clickEvent.getCurrentItem().getType();

    if (clickedMaterial == Stdafx.initGameMaterial)
    {
      simonGame.init();
    }
    else if (clickedMaterial == Stdafx.startGameMaterial)
    {
      simonGame.start();
    }
    else if (clickedMaterial == Stdafx.endGameMaterial)
    {
      simonGame.endGame();
    }
  }
}
