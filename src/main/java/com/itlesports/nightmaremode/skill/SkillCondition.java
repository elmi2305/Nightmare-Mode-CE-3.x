package com.itlesports.nightmaremode.skill;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

@FunctionalInterface
public interface SkillCondition {
    boolean test(EntityPlayer player, World world);
}
