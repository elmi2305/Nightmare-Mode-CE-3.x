package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.item.items.ItemHammer;
import com.itlesports.nightmaremode.item.items.ItemGlassArmor;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.util.NMItemStackUtils;
import com.itlesports.nightmaremode.util.NMFoodSpoilage;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "isStackable", at = @At("HEAD"), cancellable = true)
    private void allowPerishableFoodStacking(CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        if (NMFoodSpoilage.isPerishable(stack)) cir.setReturnValue(stack.getMaxStackSize() > 1);
    }

    @org.spongepowered.asm.mixin.injection.ModifyVariable(method = "damageItem", at = @At("HEAD"), argsOnly = true)
    private int reduceEasyToolWear(int amount, int originalAmount, EntityLivingBase user) {
        if (amount <= 0 || user == null || user.worldObj.isRemote || !com.itlesports.nightmaremode.world.BalanceProfile.isEasy()) return amount;
        Item item = ((ItemStack)(Object)this).getItem();
        if (!(item instanceof net.minecraft.src.ItemTool || item instanceof net.minecraft.src.ItemArmor
                || item instanceof net.minecraft.src.ItemSword || item instanceof net.minecraft.src.ItemBow || item instanceof net.minecraft.src.ItemFishingRod || item instanceof net.minecraft.src.ItemShears
                || item instanceof com.itlesports.nightmaremode.item.items.ItemSoulFlint
                || item instanceof btw.item.items.ChiselItem || item instanceof com.itlesports.nightmaremode.item.items.template.ItemKnife
                || item instanceof ItemHammer || item instanceof com.itlesports.nightmaremode.item.items.ItemScythe
                || item instanceof com.itlesports.nightmaremode.item.items.ItemLeafRake)) return amount;
        return amount / 2 + (amount % 2 != 0 && user.getRNG().nextBoolean() ? 1 : 0);
    }
    @Inject(method = {"shouldApplyAttributesWhenHeld", "shouldApplyAttributesWhenWorn"},
            at = @At("HEAD"), cancellable = true)
    private void ignoreAttributesForUnregisteredItem(CallbackInfoReturnable<Boolean> cir) {
        if (!NMItemStackUtils.isValid((ItemStack)(Object)this)) cir.setReturnValue(false);
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void writeExtendedDamage(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        if (stack.itemID >= 0 && stack.itemID < Item.itemsList.length
                && Item.itemsList[stack.itemID] != null && stack.getMaxDamage() > Short.MAX_VALUE) {
            tag.setInteger("nmFullDamage", stack.getItemDamage());
        }
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    private void readExtendedDamage(NBTTagCompound tag, CallbackInfo ci) {
        ItemStack stack = (ItemStack)(Object)this;
        if (tag.hasKey("nmFullDamage") && stack.itemID >= 0 && stack.itemID < Item.itemsList.length
                && Item.itemsList[stack.itemID] != null && stack.getMaxDamage() > Short.MAX_VALUE) {
            stack.setItemDamage(tag.getInteger("nmFullDamage"));
        }
    }

    @Inject(method = "damageItem", at = @At("HEAD"), cancellable = true)
    private void preserveHammerDurability(int amount, EntityLivingBase user, CallbackInfo ci) {
        ItemStack stack = (ItemStack)(Object)this;
        if (amount > 0 && stack.getItem() instanceof ItemHammer && user instanceof EntityPlayer player
                && player.rand.nextFloat() < SkillHandler.getPlayerData(player).hammerDurabilitySaveChance) {
            ci.cancel();
        }
    }

    @Inject(method = "damageItem", at = @At("RETURN"))
    private void punishGlassArmorBreakage(int amount, EntityLivingBase user, CallbackInfo ci) {
        ItemStack stack = (ItemStack)(Object)this;
        if (amount > 0 && stack.stackSize == 0 && stack.getItem() instanceof ItemGlassArmor
                && user instanceof EntityPlayer && !user.worldObj.isRemote) {
            user.attackEntityFrom(net.minecraft.src.DamageSource.magic, 2.0F);
        }
    }
}
