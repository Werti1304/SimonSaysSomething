package com.werti.simonsayssomething.EventHandler;

import com.werti.simonsayssomething.GUI.SimonMenuFixture;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MenuHandler implements Listener
{
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event)
  {
    Action action = event.getAction();

    // Checks if the action really was a right-click
    if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK)
    {
      return;
    }

    ItemStack item = event.getItem();

    if (item == null)
    {
      return;
    }

    SimonMenuFixture simonMenuFixture = SimonMenuFixture.getByItem(item);

    // If the menu the players is holding exists, open it
    // No isMenu() function of any kind was used because we would need to iterate 2x through the menu-list to open the menu
    // and this event should be at least optimized to some level
    if (simonMenuFixture != null)
    {
      simonMenuFixture.openInventory(event.getPlayer());
    }
  }

  /*
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event)
  {
    // Check if it's the right inventory
    if (Stdafx.simonMenu.getInventory().getHolder() != event.getInventory().getHolder())
    {
      return;
    }

    HumanEntity humanEntity = event.getWhoClicked();

    // Check if a player cliked
    if (!(humanEntity instanceof Player))
    {
      return;
    }

    Player player = (Player) humanEntity;
    SimonPlayer simonPlayer = SimonPlayer.get(player);

    event.setCancelled(true);
    player.closeInventory();

    // Check if the player even is in a game
    if (simonPlayer == null)
    {
      SimonPlayer.sendMessage(player, "You're not in a game!");
      return;
    }

    SimonGame gameofPlayer = SimonGame.getGameByPlayer(simonPlayer);

    // Check if the player is Simon
    if (simonPlayer != gameofPlayer.getSimon())
    {
      simonPlayer.sendMessage("You're not Simon!");
      return;
    }

    if (SimonGame.getGameByPlayer(simonPlayer).getGameState() != SimonGame.GameState.InProgress)
    {
      simonPlayer.sendMessage("Game hasn't started yet!");
      return;
    }

    if (Stdafx.BackgroundMaterial == event.getCurrentItem().getType())
    {
      return;
    }

    int slot = event.getSlot();

    // Todo: "Simon.print()" or something like this
    //Bukkit.broadcastMessage("Simon says " + Stdafx.simonMenu.getInvSorting().get(slot).getChatMessage());

    event.getWhoClicked().closeInventory();
  } */
}
