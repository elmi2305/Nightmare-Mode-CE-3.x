package com.itlesports.nightmaremode.mixin;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.util.NMInventoryLocks;
import com.itlesports.nightmaremode.util.interfaces.EntityPlayerExt;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryPlayer.class)
public class InventoryPlayerMixin {
    @Shadow public ItemStack[] mainInventory;
    @Shadow public int currentItem;
    @Shadow public EntityPlayer player;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void perfectStart(EntityPlayer par1EntityPlayer, CallbackInfo ci){
        InventoryPlayer inv = (InventoryPlayer)(Object)this;

        if (NightmareMode.perfectStart) {
            inv.addItemStackToInventory(new ItemStack(BTWBlocks.idleOven));
            inv.addItemStackToInventory(new ItemStack(Item.axeStone));
            inv.addItemStackToInventory(new ItemStack(BTWItems.tangledWeb));
            inv.addItemStackToInventory(new ItemStack(BTWBlocks.looseDirtSlab, 16));
            inv.addItemStackToInventory(new ItemStack(BTWItems.stone, 8));
        }
        if(NightmareMode.isAprilFools){
            inv.addItemStackToInventory(new ItemStack(NMItems.creeperBallSoup));
            inv.addItemStackToInventory(new ItemStack(BTWItems.bedroll,64));
        }
        if(NightmareMode.extraArmor){
            inv.addItemStackToInventory(new ItemStack(Item.bootsLeather));
            inv.addItemStackToInventory(new ItemStack(BTWItems.woolLeggings));
            inv.addItemStackToInventory(new ItemStack(BTWItems.woolChest));
            inv.addItemStackToInventory(new ItemStack(BTWItems.woolHelmet));
        }


        if (NightmareMode.devMode) {
            inv.addItemStackToInventory(new ItemStack(NMBlocks.underworldPortal));
//            inv.addItemStackToInventory(new ItemStack(397, 64, 5));
//            inv.addItemStackToInventory(new ItemStack(NMBlocks.underStones.blockID, 64, 0));
//            inv.addItemStackToInventory(new ItemStack(BTWItems.soulUrn, 16));
//            inv.addItemStackToInventory(new ItemStack(BTWItems.plateBreastplate));
//            inv.addItemStackToInventory(new ItemStack(BTWItems.plateLeggings));
//            inv.addItemStackToInventory(new ItemStack(BTWItems.plateBoots));
//            inv.addItemStackToInventory(new ItemStack(BTWItems.plateHelmet));
            inv.addItemStackToInventory(new ItemStack(NMItems.rifle));
            inv.addItemStackToInventory(new ItemStack(Block.obsidian));
            inv.addItemStackToInventory(new ItemStack(Item.fireballCharge));




//            inv.addItemStackToInventory(new ItemStack(NMBlocks.bloodBonesUpgraded));
//            inv.addItemStackToInventory(new ItemStack(BTWItems.steelSword));
//            inv.addItemStackToInventory(new ItemStack(BTWItems.plateBoots));
//            inv.addItemStackToInventory(new ItemStack(BTWItems.plateLeggings));
//            inv.addItemStackToInventory(new ItemStack(BTWItems.plateBreastplate));
//            inv.addItemStackToInventory(new ItemStack(Item.netherStar));
//            inv.addItemStackToInventory(new ItemStack(NMItems.rifle));
//            inv.addItemStackToInventory(new ItemStack(BTWItems.cementBucket));
//            inv.addItemStackToInventory(new ItemStack(BTWItems.heartyStew, 64));
//            inv.addItemStackToInventory(new ItemStack(Item.appleGold, 64, 1));
//            inv.addItemStackToInventory(new ItemStack(373, 64, 16421));
        }
    }

    @Inject(method = "getCurrentItem", at = @At("HEAD"), cancellable = true)
    private void useOnlyUnlockedHotbarSlots(CallbackInfoReturnable<ItemStack> cir) {
        if (!NMInventoryLocks.isMainInventorySlotUnlocked(this.player, this.currentItem)) {
            this.currentItem = 0;
            cir.setReturnValue(this.mainInventory[0]);
        }
    }

    @Inject(method = "changeCurrentItem", at = @At("HEAD"), cancellable = true)
    private void cycleOnlyUnlockedHotbarSlots(int direction, CallbackInfo ci) {
        int hotbarSlots = NMInventoryLocks.getUnlockedHotbarSlots(this.player);
        if (hotbarSlots >= 9) {
            return;
        }

        if (direction > 0) {
            direction = 1;
        }
        if (direction < 0) {
            direction = -1;
        }

        this.currentItem -= direction;
        while (this.currentItem < 0) {
            this.currentItem += hotbarSlots;
        }
        while (this.currentItem >= hotbarSlots) {
            this.currentItem -= hotbarSlots;
        }
        ci.cancel();
    }

    @Inject(method = "setCurrentItem", at = @At("RETURN"))
    private void keepCurrentItemUnlocked(int itemId, int itemDamage, boolean matchDamage, boolean useCreativePickBlock, CallbackInfo ci) {
        this.nightmareMode$clampCurrentItem();
    }

    @Inject(method = "getStrVsBlock", at = @At("HEAD"))
    private void clampBeforeCalculatingBlockStrength(World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        this.nightmareMode$clampCurrentItem();
    }

    @Inject(method = "getStrVsBlock", at = @At("RETURN"), cancellable = true)
    private void applySkillBlockBreakSpeed(World world, Block block, int x, int y, int z, CallbackInfoReturnable<Float> cir) {
        if (this.player instanceof EntityPlayerExt ext) {
            cir.setReturnValue(cir.getReturnValueF() * (1.0F + ext.nightmareMode$getSkillBlockBreakSpeedBonus()));
        }
    }

    @Inject(method = "canHarvestBlock", at = @At("HEAD"))
    private void clampBeforeCheckingHarvest(World world, Block block, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        this.nightmareMode$clampCurrentItem();
    }

    @Inject(method = "getInventorySlotContainItem", at = @At("HEAD"), cancellable = true)
    private void findOnlyUnlockedItemSlots(int itemId, CallbackInfoReturnable<Integer> cir) {
        for (int slot = 0; slot < this.mainInventory.length; ++slot) {
            if (!NMInventoryLocks.isMainInventorySlotUnlocked(this.player, slot)) {
                continue;
            }
            if (this.mainInventory[slot] != null && this.mainInventory[slot].itemID == itemId) {
                cir.setReturnValue(slot);
                return;
            }
        }
        cir.setReturnValue(-1);
    }

    @Inject(method = "getInventorySlotContainItemAndDamage", at = @At("HEAD"), cancellable = true)
    private void findOnlyUnlockedItemDamageSlots(int itemId, int itemDamage, CallbackInfoReturnable<Integer> cir) {
        for (int slot = 0; slot < this.mainInventory.length; ++slot) {
            if (!NMInventoryLocks.isMainInventorySlotUnlocked(this.player, slot)) {
                continue;
            }
            if (this.mainInventory[slot] != null
                    && this.mainInventory[slot].itemID == itemId
                    && this.mainInventory[slot].getItemDamage() == itemDamage) {
                cir.setReturnValue(slot);
                return;
            }
        }
        cir.setReturnValue(-1);
    }

    @Inject(method = "storeItemStack", at = @At("HEAD"), cancellable = true)
    private void stackOnlyIntoUnlockedSlots(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        for (int slot = 0; slot < this.mainInventory.length; ++slot) {
            if (!NMInventoryLocks.isMainInventorySlotUnlocked(this.player, slot)) {
                continue;
            }
            if (this.mainInventory[slot] != null
                    && this.mainInventory[slot].itemID == stack.itemID
                    && this.mainInventory[slot].isStackable()
                    && this.mainInventory[slot].stackSize < this.mainInventory[slot].getMaxStackSize()
                    && this.mainInventory[slot].stackSize < ((InventoryPlayer)(Object)this).getInventoryStackLimit()
                    && (!this.mainInventory[slot].getHasSubtypes() || this.mainInventory[slot].getItemDamage() == stack.getItemDamage())
                    && ItemStack.areItemStackTagsEqual(this.mainInventory[slot], stack)) {
                cir.setReturnValue(slot);
                return;
            }
        }
        cir.setReturnValue(-1);
    }

    @Inject(method = "getFirstEmptyStack", at = @At("HEAD"), cancellable = true)
    private void findOnlyUnlockedEmptySlots(CallbackInfoReturnable<Integer> cir) {
        for (int slot = 0; slot < this.mainInventory.length; ++slot) {
            if (NMInventoryLocks.isMainInventorySlotUnlocked(this.player, slot) && this.mainInventory[slot] == null) {
                cir.setReturnValue(slot);
                return;
            }
        }
        cir.setReturnValue(-1);
    }

    @Inject(method = "hasItemStack", at = @At("HEAD"), cancellable = true)
    private void checkOnlyUnlockedItemStacks(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        for (ItemStack armorStack : ((InventoryPlayer)(Object)this).armorInventory) {
            if (armorStack != null && armorStack.isItemEqual(stack)) {
                cir.setReturnValue(true);
                return;
            }
        }

        for (int slot = 0; slot < this.mainInventory.length; ++slot) {
            if (!NMInventoryLocks.isMainInventorySlotUnlocked(this.player, slot)) {
                continue;
            }
            if (this.mainInventory[slot] != null && this.mainInventory[slot].isItemEqual(stack)) {
                cir.setReturnValue(true);
                return;
            }
        }
        cir.setReturnValue(false);
    }

    @Unique
    private void nightmareMode$clampCurrentItem() {
        int hotbarSlots = NMInventoryLocks.getUnlockedHotbarSlots(this.player);
        if (this.currentItem < 0) {
            this.currentItem = 0;
        } else if (this.currentItem >= hotbarSlots) {
            this.currentItem = hotbarSlots - 1;
        }
    }
}
