package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.NightmareUtils;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityFishHook;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityFishHook.class)
public abstract class EntityFishHookMixin extends Entity {
    public EntityFishHookMixin(World par1World) {
        super(par1World);
    }

    @ModifyConstant(method = "checkForBite", constant = @Constant(intValue = 8))
    private int increaseBiteOdds(int constant){
        if (NightmareUtils.getIsBloodMoon()) {
            return 16;
        }
        return 10;
    }
}
