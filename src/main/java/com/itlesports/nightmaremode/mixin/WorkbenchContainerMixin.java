package com.itlesports.nightmaremode.mixin;

import btw.community.nightmaremode.NightmareMode;
import btw.inventory.container.WorkbenchContainer;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.items.ItemAdvancedHorseArmor;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorkbenchContainer.class)
public class WorkbenchContainerMixin extends ContainerWorkbench {
    @Shadow public World world;
    @Shadow public int blockX;
    @Shadow public int blockY;
    @Shadow public int blockZ;

    public WorkbenchContainerMixin(InventoryPlayer par1InventoryPlayer, World par2World, int par3, int par4, int par5) {
        super(par1InventoryPlayer, par2World, par3, par4, par5);
    }

    @Inject(method = "canInteractWith", at = @At("HEAD"), cancellable = true)
    private void addNetherCraftingTable(EntityPlayer p, CallbackInfoReturnable<Boolean> cir) {
        int iBlockID = this.world.getBlockId(this.blockX, this.blockY, this.blockZ);
        if(iBlockID == NMBlocks.netherWorkbench.blockID){
            cir.setReturnValue(true);
        }

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
