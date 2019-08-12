package com.werti.simonsayssomething;

import com.werti.Stdafx;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class SimonSaysOption
{
  private static String modifiers;
  private String name;
  private String description;
  private String chatMessage;
  private ChatColor chatColor;
  private Material material;

  /**
   * @param name        Unformatted name that the option will have
   * @param description Description of the action (e.g. "Make Players jump 1 time"
   * @param chatMessage Message that Simon will broadcast to the group
   * @param chatColor   Color of the Item in the Menu
   * @param material    Material of the Item in the Menu
   */
  public SimonSaysOption(String name, String description, String chatMessage, ChatColor chatColor, Material material)
  {
    this.name = name;
    this.description = description;
    this.chatMessage = chatMessage;
    this.chatColor = chatColor;
    this.material = material;
  }

  public ItemStack getItem()
  {
    ArrayList<String> lore = new ArrayList<>();
    lore.add(description);

    ItemStack option = new ItemStack(material);

    ItemMeta optionMeta = option.getItemMeta();

    optionMeta.setDisplayName(Stdafx.MenuNameModifiers + chatColor + name);
    optionMeta.setLore(lore);

    option.setItemMeta(optionMeta);

    return option;
  }

  public String getChatMessage()
  {
    return chatMessage;
  }
}
