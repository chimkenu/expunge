package me.chimkenu.expunge.guns.guns;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

public class Pistol extends Gun {
    public Pistol() {
        super(7, 1, 20, 5, 34, 15, 5000, 1, Particle.CRIT, Material.WOODEN_HOE, "&6Pistol", Sound.ENTITY_IRON_GOLEM_HURT, 1.8f);
    }
}
