package com.itlesports.nightmaremode.skill;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

@FunctionalInterface
public interface SkillUnlockAction {
    void apply(EntityPlayer player, World world);
}
