package com.werti.simonsayssomething.GUI;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

public class SimonSaysItemSafe extends SimonSaysItem
{
  private UUID uuid;

  SimonSaysItemSafe(Material material, boolean glowing, String name, String... lore)
  {
    super(material, glowing, name, lore);

    initialize();
  }

  SimonSaysItemSafe(Material material, boolean glowing, String... lore)
  {
    super(material, glowing, lore);

    initialize();
  }

  SimonSaysItemSafe(Material material, String name, String... lore)
  {
    super(material, name, lore);

    initialize();
  }

  SimonSaysItemSafe(Material material)
  {
    super(material);

    initialize();
  }

  private void initialize()
  {
    uuid = UUID.randomUUID();

    // Add unique identifier to item
    String encodedUuid = ChatColor.translateAlternateColorCodes('&', "&0") + uuid.toString();

    ItemMeta itemMeta = getItemMeta();

    ArrayList<String> itemLore;

    if (itemMeta.hasLore())
    {
      itemLore = (ArrayList<String>) itemMeta.getLore();
    }
    else
    {
      itemLore = new ArrayList<>();
    }

    itemLore.add(encodedUuid);

    itemMeta.setLore(itemLore);

    setItemMeta(itemMeta);
  }

  public UUID getUUID()
  {
    return uuid;
  }
}
