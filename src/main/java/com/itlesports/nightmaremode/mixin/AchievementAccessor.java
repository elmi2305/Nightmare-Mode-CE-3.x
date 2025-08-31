package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.Achievement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Achievement.class)
public interface AchievementAccessor {
    @Accessor("parentAchievements")
    Achievement[] getParents();
    @Accessor("parentAchievements")
    void setParentAchievements(Achievement[] parents);
}
