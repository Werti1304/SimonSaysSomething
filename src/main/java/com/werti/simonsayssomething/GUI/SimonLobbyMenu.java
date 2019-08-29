package com.werti.simonsayssomething.GUI;

import com.werti.Stdafx;
import com.werti.StrRes;
import com.werti.simonsayssomething.Helper.ChatHelper;
import com.werti.simonsayssomething.SimonGame;
import com.werti.simonsayssomething.SimonPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;

public class SimonLobbyMenu extends SimonMenuFixture
{
  public SimonLobbyMenu()
  {
    super(new SimonSaysItemSafe(Material.GOLD_INGOT, "Simon Lobby Menu"), 9, "Simon Lobby Menu");
  }

  @Override
  void initializeItems()
  {
    SimonSaysItem placeHolder = SimonSaysItem.getSpacer();

    setItem(new SimonSaysItem(Stdafx.inviteGameMaterial,
                              ChatColor.DARK_PURPLE.toString() + ChatColor.ITALIC + "Invite Players",
                              ChatHelper.getFullCommand(StrRes.Command.InvitePlayers)), 0);

    setItem(new SimonSaysItem(Stdafx.initGameMaterial, ChatColor.GOLD + "Initialize",
                              ChatHelper.getFullCommand(StrRes.Command.InitGame)), 2);

    setItem(new SimonSaysItem(Stdafx.startGameMaterial, ChatColor.GREEN + "Start",
                              ChatHelper.getFullCommand(StrRes.Command.StartGame)), 4);

    setItem(new SimonSaysItem(Stdafx.endGameMaterial, ChatColor.DARK_BLUE + "End Game",
                              ChatHelper.getFullCommand(StrRes.Command.EndGame)), 6);

    setItem(new SimonSaysItem(Stdafx.toggleGameMaterial, ChatColor.BLUE.toString() + ChatColor.ITALIC + "Toggle Mode",
                              ChatHelper.getFullCommand(StrRes.Command.KickPlayer)), 8);

    addPlaceHolders();
  }

  @Override
  void itemClick(InventoryClickEvent clickEvent, SimonPlayer simonPlayer)
  {
    SimonGame simonGame = simonPlayer.getSimonGame();
    Material clickedMaterial = clickEvent.getCurrentItem().getType();

    boolean closeInventory = false;

    if (clickedMaterial == Stdafx.initGameMaterial)
    {
      simonGame.init();
    }
    else if (clickedMaterial == Stdafx.startGameMaterial)
    {
      simonGame.start();
      closeInventory = true;
    }
    else if (clickedMaterial == Stdafx.endGameMaterial)
    {
      simonGame.endGame();
      closeInventory = true;
    }
    else if (clickedMaterial == Stdafx.inviteGameMaterial)
    {
      // For now, when the players clicks on "Invite players", we just display him the necessary command
      simonPlayer.sendMessage(StrRes.SimonGameError.NotImplemented);
    }
    else if (clickedMaterial == Stdafx.toggleGameMaterial)
    {
      if (simonGame.getGameState() == SimonGame.GameState.WaitingForInit)
      {
        simonGame.setFreeMode(!simonGame.isFreeMode());
        simonPlayer.sendMessage(
            "Game set to " + Stdafx.highlightColor + (simonGame.isFreeMode() ? "Free-Mode" : "Fixed-Mode"));
        simonPlayer.getPlayer().playSound(simonPlayer.getLocation(), Sound.ANVIL_LAND, 0.5f, 1.0f);
      }
      else
      {
        simonPlayer.sendMessage(StrRes.SimonGameError.GameAlreadyInitialized);
        closeInventory = true;
      }
    }

    if (closeInventory)
    {
      // As documented in the javadocs, the inventory can't be closed in the InventoryClickEvent,
      // so we schedule it directly after that
      closeInventoryNextTick(simonPlayer.getPlayer());
    }
  }
}
