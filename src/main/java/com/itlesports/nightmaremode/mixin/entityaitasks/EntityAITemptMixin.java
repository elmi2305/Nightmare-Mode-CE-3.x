package com.itlesports.nightmaremode.mixin.entityaitasks;

import com.itlesports.nightmaremode.util.NMFishFood;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityAITempt.class)
public class EntityAITemptMixin {
    @Shadow private EntityCreature temptedEntity;

    @Redirect(method = {"shouldExecute", "shouldContinueFollowing"}, at = @At(value = "FIELD",
            target = "Lnet/minecraft/src/ItemStack;itemID:I"))
    private int acceptOcelotFish(ItemStack stack) {
        return this.temptedEntity instanceof EntityOcelot ? NMFishFood.ocelotFoodID(stack) : stack.itemID;
    }
}
