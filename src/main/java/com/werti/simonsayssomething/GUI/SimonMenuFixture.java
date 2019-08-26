package com.werti.simonsayssomething.GUI;

import com.werti.Stdafx;
import com.werti.simonsayssomething.SimonGame;
import com.werti.simonsayssomething.SimonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public abstract class SimonMenuFixture implements InventoryHolder, Listener
{
  private static ArrayList<SimonMenuFixture> currentSimonMenuFixtures = new ArrayList<>();
  private final Inventory inventory;
  private String title;
  private SimonSaysItemSafe menuOpeningItem;

  public SimonMenuFixture(SimonSaysItemSafe menuOpeningItem, int size, String title)
  {
    this.inventory = Bukkit.createInventory(this, size, title);
    this.menuOpeningItem = menuOpeningItem;
    this.title = title;

    currentSimonMenuFixtures.add(this);

    initializeItems();
  }

  public static ArrayList<SimonMenuFixture> getCurrentSimonMenuFixtures()
  {
    return currentSimonMenuFixtures;
  }

  @Nullable
  public static SimonMenuFixture getByItem(ItemStack item)
  {
    for (SimonMenuFixture simonMenuFixture : currentSimonMenuFixtures)
    {
      if (simonMenuFixture.equalsItemStack(item))
      {
        return simonMenuFixture;
      }
    }
    return null;
  }

  /**
   * Used to add items, is automatically called in the constructor
   */
  abstract void initializeItems();

  /**
   * What should happen when an item was clicked
   * <p>
   * Event-Cancelling has to be set to true in order to make the item movable
   */
  abstract void itemClick(InventoryClickEvent clickEvent, SimonGame simonGame, SimonPlayer simonPlayer);

  /**
   * Event when something in the inventory is clicked upon
   */
  @EventHandler
  public void onInventoryClick(InventoryClickEvent clickEvent)
  {
    // Check if it's the right inventory
    if (Stdafx.simonLobbyMenu.getInventory().getHolder() != clickEvent.getInventory().getHolder())
    {
      return;
    }

    HumanEntity humanEntity = clickEvent.getWhoClicked();

    // Check if a player clicked
    if (!(humanEntity instanceof Player))
    {
      return;
    }

    Player player = (Player) humanEntity;
    SimonPlayer simonPlayer = SimonPlayer.get(player);

    clickEvent.setCancelled(true);
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

    if (Stdafx.BackgroundMaterial == clickEvent.getCurrentItem().getType())
    {
      return;
    }

    clickEvent.setCancelled(true);

    itemClick(clickEvent, gameofPlayer, simonPlayer);
  }

  @Override
  public Inventory getInventory()
  {
    return inventory;
  }

  public void setItem(ItemStack simonSaysItem, int slot)
  {
    inventory.setItem(slot, simonSaysItem);
  }

  public void addItem(ItemStack simonSaysItem)
  {
    int firstAvailableSlot = inventory.firstEmpty();

    if (firstAvailableSlot == -1)
    {
      Stdafx.plugin.getLogger().warning(
          "No more space: Couldn't add item " + simonSaysItem.getItemMeta().getDisplayName()
          + " to a bukkit inventory titled "
          + title);

      return;
    }

    setItem(simonSaysItem, firstAvailableSlot);
  }

  /**
   * Adds placeholders for every unoccupied slot
   */
  public void addPlaceHolders()
  {
    int nextSlot = inventory.firstEmpty();
    final SimonSaysItem spacer = SimonSaysItem.getSpacer();

    while (nextSlot != -1)
    {
      setItem(spacer, nextSlot);

      nextSlot = inventory.firstEmpty();
    }
  }

  public void openInventory(Player player)
  {
    player.openInventory(this.getInventory());
  }

  /**
   * Give item necessary to open the menu to a player
   *
   * @param player Player to give the menu to
   * @param slot   Slot where the menu-opening-item should be placed into
   */
  public void giveToPlayer(Player player, int slot)
  {
    player.getInventory().setItem(slot, menuOpeningItem);
  }

  public void delete()
  {
    currentSimonMenuFixtures.remove(this);
  }

  public String getTitle()
  {
    return title;
  }

  public SimonSaysItem getMenuOpeningItem()
  {
    return menuOpeningItem;
  }

  public boolean equalsItemStack(ItemStack item)
  {
    // Firstly, check if there even is an item to check (just to make sure)
    if (item == null)
    {
      return false;
    }

    // If it's not a piece of paper, it can't be the menu
    if (item.getType() != menuOpeningItem.getType())
    {
      return false;
    }

    // Check for Meta-Information
    if (!item.hasItemMeta())
    {
      return false;
    }

    ItemMeta menuMeta = item.getItemMeta();

    // Check for item lore (needed for UUID)
    if (!menuMeta.hasLore())
    {
      return false;
    }

    // The UUID is always the last entry in the lore
    int loreIndex = menuMeta.getLore().size() - 1;
    String lore = ChatColor.stripColor(menuMeta.getLore().get(menuMeta.getLore().size() - 1));

    // Check if the lore is equal to the menu uuid
    return Objects.equals(lore, menuOpeningItem.getUUID().toString());
  }
}
