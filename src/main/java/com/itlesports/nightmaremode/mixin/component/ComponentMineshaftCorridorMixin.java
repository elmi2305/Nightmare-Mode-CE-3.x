package com.itlesports.nightmaremode.mixin.component;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.KnowledgeBookLoot;
import com.itlesports.nightmaremode.util.NMFields;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ComponentMineshaftCorridor.class)
public class ComponentMineshaftCorridorMixin {

    @Inject(method = "filterChestMinecartContents", at = @At("TAIL"))
    private void injectArrowsIntoMinecart(EntityMinecartChest minecart, CallbackInfo ci) {
        KnowledgeBookLoot.addBookIfRolled(minecart, minecart.rand, NMFields.KNOWLEDGE_BOOKS_MINESHAFT, 4);
        if (minecart.posY < 24) {
            for (int iSlot = 0; iSlot < minecart.getSizeInventory(); ++iSlot) {
                if (minecart.rand.nextInt(16) == 0) continue;

                ItemStack stack = minecart.getStackInSlot(iSlot);
                if (stack == null) {
                    minecart.setInventorySlotContents(iSlot, new ItemStack(NMItems.magicArrow, minecart.rand.nextInt(4) + 2));
                    break;
                }
            }
        }
    }
}
