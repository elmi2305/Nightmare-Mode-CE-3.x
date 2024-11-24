package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.ComponentMineshaftCorridor;
import net.minecraft.src.ItemEnchantedBook;
import net.minecraft.src.WeightedRandomChestContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(ComponentMineshaftCorridor.class)
public class ComponentMineshaftCorridorMixin {
    @Redirect(method = "addComponentParts", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/ItemEnchantedBook;func_92114_b(Ljava/util/Random;)Lnet/minecraft/src/WeightedRandomChestContent;"))
    private WeightedRandomChestContent increaseManuscriptChance(ItemEnchantedBook instance, Random par1Random){
        return instance.func_92112_a(par1Random,1,1,5);
    }
}
