package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.item.items.ItemHammer;
import com.itlesports.nightmaremode.item.items.ItemGlassArmor;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.util.NMItemStackUtils;
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
