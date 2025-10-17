package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.inventory.container.WorkbenchContainer;
import com.itlesports.nightmaremode.item.items.ItemAdvancedHorseArmor;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorkbenchContainer.class)
public class WorkbenchContainerMixin extends ContainerWorkbench {
    @Shadow public World world;

    public WorkbenchContainerMixin(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5) {
        super(par1InventoryPlayer, par2World, par3, par4, par5);
    }

    @Redirect(method = "transferStackInSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Slot;getStack()Lnet/minecraft/src/ItemStack;"))
    private ItemStack aprilFoolsDurabilityOnShiftClick(Slot instance){
        if (NightmareMode.isAprilFools && instance.slotNumber == 0) {
            double gaussian = this.world.rand.nextGaussian();
            double normalized = (gaussian + 3) / 6;
            int damage = (int) (Math.max(0, Math.min(1, normalized)) * instance.getStack().getMaxDamage() - 1);
            instance.getStack().attemptDamageItem(damage, this.world.rand);
        }
        if(instance.getStack() != null && instance.getStack().getItem() instanceof ItemAdvancedHorseArmor && instance.slotNumber == 0){
            instance.getStack().setItemDamage(instance.getStack().getMaxDamage());
        }

        return instance.getStack();
    }
}
