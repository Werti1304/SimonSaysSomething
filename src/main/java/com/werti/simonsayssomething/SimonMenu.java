package com.werti.simonsayssomething;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class SimonMenu implements InventoryHolder
{
  private static UUID uuid = UUID.randomUUID();
  private HashMap<Integer, SimonSaysOption> invSorting = new HashMap<>();
  private Inventory inventory = createMenu();

  public static ItemStack getMenuItem()
  {
    ItemStack menu = new ItemStack(Material.PAPER);

    ItemMeta menuMeta = menu.getItemMeta();

    // Hides enchantments of item
    menuMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    // Adds an enchantment (Type not important)
    menuMeta.addEnchant(Enchantment.DURABILITY, 1, true);

    menuMeta.setDisplayName(ChatColor.ITALIC + "Menu");

    // Add unique identifier to item
    menuMeta.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', "&0") + uuid.toString()));

    menu.setItemMeta(menuMeta);

    return menu;
  }

  public static boolean isMenu(ItemStack menu)
  {
    // If it's not a piece of paper, it can't be the menu
    if (menu.getType() != Material.PAPER)
    {
      return false;
    }

    // Check for Meta-Information
    if (!menu.hasItemMeta())
    {
      return false;
    }

    ItemMeta menuMeta = menu.getItemMeta();

    // Check for item lore (needed for UUID)
    if (!menuMeta.hasLore())
    {
      return false;
    }

    String lore = ChatColor.stripColor(menuMeta.getLore().get(0));

    // Check if the lore is equal to the menu uuid
    return Objects.equals(lore, uuid.toString());
  }

  private Inventory createMenu()
  {
    Inventory gui = Bukkit.createInventory(this, 9, ChatColor.RED + "Simon says...");

    // TODO: Add an option to make them jump x times
    SimonSaysOption sayJump = new SimonSaysOption("Jump!",
            "Tells Players to jump 1 time",
            "jump!",
            ChatColor.GREEN,
            Material.FEATHER);

    invSorting.put(4, sayJump);

    for (int i = 0; i < 9; i++)
    {
      if (invSorting.containsKey(i))
      {
        gui.setItem(i, invSorting.get(i).getItem());
      }
      else
      {
        gui.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
      }
    }

    return gui;
  }

  public Inventory getInventory()
  {
    return inventory;
  }

  public HashMap<Integer, SimonSaysOption> getInvSorting()
  {
    return invSorting;
  }
}
