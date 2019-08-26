package com.werti.simonsayssomething.GUI;

import com.werti.Stdafx;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class SimonSaysItem extends ItemStack
{
  /**
   * @param material Material to be used
   * @param name     Name of the item
   * @param glowing  Whether the item should be glowing (e.g. seem enchanted)
   * @param lore     the items' description
   */
  SimonSaysItem(Material material, boolean glowing, String name, String... lore)
  {
    super(material);
    addItemMeta(glowing, name, lore);
  }

  SimonSaysItem(Material material, boolean glowing, String... lore)
  {
    super(material);
    addItemMeta(glowing, null, lore);
  }

  SimonSaysItem(Material material, String name, String... lore)
  {
    super(material);
    addItemMeta(false, name, lore);
  }

  SimonSaysItem(Material material)
  {
    super(material);
    addItemMeta(false, null);
  }

  public static SimonSaysItem getSpacer()
  {
    return new SimonSaysItem(Stdafx.BackgroundMaterial,
                             ChatColor.DARK_PURPLE.toString() + ChatColor.ITALIC + "Nothing");
  }

  private void addItemMeta(boolean glowing, String name, String... lore)
  {
    ItemMeta itemMeta = super.getItemMeta();

    if (lore.length != 0)
    {
      itemMeta.setLore(Arrays.asList(lore));
    }

    if (name != null)
    {
      itemMeta.setDisplayName(name);
    }

    // Just add an hidden "Luck"-Enchantment to the item and hide it,
    // everything else seems overcomplicated for our purposes
    if (glowing)
    {
      // Magic parameters i & b:
      // i -> Level of Enchantment
      // b -> Should the enchantment ignore enchantment-limit of our item
      itemMeta.addEnchant(Enchantment.LUCK, 1, true);
      itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    super.setItemMeta(itemMeta);
  }

  public ItemStack clone()
  {
    return super.clone();
  }
}
