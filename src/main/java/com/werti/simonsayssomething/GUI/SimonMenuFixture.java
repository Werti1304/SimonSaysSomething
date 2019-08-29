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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public abstract class SimonMenuFixture implements InventoryHolder, Listener
{
  private static ArrayList<SimonMenuFixture> simonMenuList = new ArrayList<>();

  private Inventory inventory;
  private String title;
  private SimonSaysItemSafe menuOpeningItem;

  public SimonMenuFixture(SimonSaysItemSafe menuOpeningItem, int size, String title)
  {
    this.inventory = Bukkit.createInventory(this, size, title);
    this.menuOpeningItem = menuOpeningItem;
    this.title = title;

    simonMenuList.add(this);

    initializeItems();
  }

  public static ArrayList<SimonMenuFixture> getSimonMenuList()
  {
    return simonMenuList;
  }

  /**
   * Get a menu (/inventory) by the item that's supposed to open it
   * (e.g. get the EnderChest-Menu by right-clicking the EnderChest-Item)
   * Returns null when no associated menu is found (which is the case most of the time)
   *
   * @param item Item to be checked
   * @return SimonMenu associated with this item
   */
  @Nullable
  public static SimonMenuFixture getByItem(ItemStack item)
  {
    for (SimonMenuFixture simonMenuFixture : simonMenuList)
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
   * Event-Cancelling has to be set to true in order to make the item movable
   */
  abstract void itemClick(InventoryClickEvent clickEvent, SimonPlayer simonPlayer);

  /**
   * TODO: Evaluate if adding every player that has a menuOpeningItem to a list and just removing from there is a good idea
   * Removes all menus from everyone on the server
   * This function has to get every item of everyone on the server (for now), so use with caution
   */
  public static void removeAllMenus()
  {
    for (Player player : Stdafx.server.getOnlinePlayers())
    {
      for (ItemStack itemStack : player.getInventory().getContents())
      {
        for (SimonMenuFixture simonMenuFixture : SimonMenuFixture.getSimonMenuList())
        {
          if (simonMenuFixture.equalsItemStack(itemStack))
          {
            player.getInventory().remove(itemStack);
          }
        }
      }
    }
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
   * Close inventory of a player after 1 tick
   * Mainly used for Events that don't support closing the inventory in the same tick
   *
   * @param player Player which inventory should be closed
   */
  void closeInventoryNextTick(Player player)
  {
    new BukkitRunnable()
    {
      @Override
      public void run()
      {
        player.closeInventory();
      }
    }.runTaskLater(Stdafx.plugin, 1); // 1 Tick / Lowest delay possible
  }

  /**
   * Give item necessary to open the menu to a player
   * @param player Player to give the menu to
   * @param slot   Slot where the menu-opening-item should be placed into
   */
  public void giveToPlayer(Player player, int slot)
  {
    player.getInventory().setItem(slot, menuOpeningItem);
  }

  /**
   * Displays newly added/changed items by simple opening the updated inventory
   *
   * @param player which players' inventory should be updated
   */
  void refreshMenu(Player player)
  {
    // Simple opening instead of closing then opening prevents the cursor to reset to the center
    openInventory(player);
  }

  public void delete()
  {
    simonMenuList.remove(this);
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

  /**
   * Event when something in the inventory is clicked upon
   */
  @EventHandler
  public void onInventoryClick(InventoryClickEvent clickEvent)
  {
    boolean validMenu = false;

    // Check if it's the right inventory
    for (SimonMenuFixture simonMenuFixture : SimonMenuFixture.getSimonMenuList())
    {
      if (simonMenuFixture.getInventory().getHolder() != clickEvent.getInventory().getHolder())
      {
        validMenu = true;
      }
    }

    if (!validMenu)
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

    // Check if the player even is in a game
    if (simonPlayer == null)
    {
      SimonPlayer.sendMessage(player, "You're not in a game!");
      return;
    }

    SimonGame gameofPlayer = simonPlayer.getSimonGame();

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

    itemClick(clickEvent, simonPlayer);
  }
}
