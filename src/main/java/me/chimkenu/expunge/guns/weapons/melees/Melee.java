package me.chimkenu.expunge.guns.weapons.melees;

import me.chimkenu.expunge.guns.weapons.Weapon;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public abstract class Melee extends Weapon {

    public Melee(double damage, int range, int cooldown, int entitiesToHit, String name, Material material) {
        super(damage, range, cooldown, entitiesToHit, name, material);
    }

    @Override
    public ItemStack getWeapon() {
        ItemStack melee = new ItemStack(getMaterial());
        ItemMeta meta = melee.getItemMeta();
        if (meta != null) {
            AttributeModifier modifier = new AttributeModifier(UUID.fromString("0a4af6ae-896d-458e-8712-ed8845740753"), "generic.attack_damage", 0.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, modifier);
            modifier = new AttributeModifier(UUID.fromString("d0538ce4-708b-463b-ac91-cfd57d6adbd2"), "generic.attack_speed", 1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, modifier);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getName()));
        }
        melee.setItemMeta(meta);
        return melee;
    }
}
