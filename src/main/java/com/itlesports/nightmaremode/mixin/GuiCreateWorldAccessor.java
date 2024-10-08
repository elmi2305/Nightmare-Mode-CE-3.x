package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.GuiCreateWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiCreateWorld.class)
public interface GuiCreateWorldAccessor {
    @Accessor("difficultyID")
    int getDifficultyID();
    @Accessor("difficultyID")
    void setDifficultyID(int par1);
    @Accessor("lockDifficulty")
    void setLockDifficulty(boolean par2);
}
