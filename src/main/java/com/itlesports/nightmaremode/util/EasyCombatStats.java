package com.itlesports.nightmaremode.util;

import com.itlesports.nightmaremode.world.BalanceProfile;
import net.minecraft.src.AttributeInstance;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.SharedMonsterAttributes;

public record EasyCombatStats(double health, Double damage, Double knockbackResistance) {
    public static EasyCombatStats beforeLoad(EntityLivingBase mob) {
        if (!BalanceProfile.isCombatEasy()) return null;
        AttributeInstance damage = mob.getEntityAttribute(SharedMonsterAttributes.attackDamage);
        AttributeInstance resistance = mob.getEntityAttribute(SharedMonsterAttributes.knockbackResistance);
        return new EasyCombatStats(mob.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue(),
                damage == null ? null : damage.getBaseValue(), resistance == null ? null : resistance.getBaseValue());
    }

    public static void afterLoad(EntityLivingBase mob, EasyCombatStats defaults) {
        if (defaults == null) return;
        mob.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(defaults.health());
        AttributeInstance damage = mob.getEntityAttribute(SharedMonsterAttributes.attackDamage);
        if (damage != null && defaults.damage() != null) damage.setAttribute(defaults.damage());
        AttributeInstance resistance = mob.getEntityAttribute(SharedMonsterAttributes.knockbackResistance);
        if (resistance != null && defaults.knockbackResistance() != null) resistance.setAttribute(defaults.knockbackResistance());
        mob.setHealth(Math.min(mob.getHealth(), mob.getMaxHealth()));
    }
}
