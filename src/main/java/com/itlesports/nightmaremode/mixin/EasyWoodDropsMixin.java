package com.itlesports.nightmaremode.mixin;

import api.item.util.ItemUtils;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.world.BalanceProfile;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemUtils.class)
public abstract class EasyWoodDropsMixin {
    @ModifyVariable(method = "ejectStackFromBlockTowardsFacing", at = @At("HEAD"), argsOnly = true)
    private static ItemStack improveGatheredWood(ItemStack stack, World world, int x, int y, int z, ItemStack original, int side) {
        Block source = Block.blocksList[world.getBlockId(x, y, z)];
        if (!BalanceProfile.isEasy() || world.isRemote || stack == null || source == null || source.blockMaterial != Material.wood
                || !(stack.itemID == Item.stick.itemID || stack.itemID == NMItems.twig.itemID
                || stack.itemID == btw.item.BTWItems.bark.itemID || stack.itemID == btw.item.BTWItems.sawDust.itemID)) return stack;
        ItemStack result = stack.copy();
        result.stackSize *= 3;
        return result;
    }
}
