package com.itlesports.nightmaremode.mixin;

import com.itlesports.nightmaremode.util.DivingTankAttachment;
import com.itlesports.nightmaremode.util.DivingMaskAttachment;
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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @ModifyArg(method = "getTooltip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/Item;addInformation(Lnet/minecraft/src/ItemStack;Lnet/minecraft/src/EntityPlayer;Ljava/util/List;Z)V"), index = 2)
    private java.util.List addAttachedTankTooltip(java.util.List tooltip) {
        DivingTankAttachment.addTooltip((ItemStack)(Object)this, tooltip);
        DivingMaskAttachment.addTooltip((ItemStack)(Object)this, tooltip);
        return tooltip;
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/ItemStack;getAttributeModifiers()Lcom/google/common/collect/Multimap;"))
    private com.google.common.collect.Multimap combineTankTooltipHunger(ItemStack stack) {
        com.google.common.collect.Multimap attributes = stack.getAttributeModifiers();
        return DivingTankAttachment.hasAttachment(stack) || DivingMaskAttachment.hasAttachment(stack)
                ? DivingTankAttachment.combineTooltipHunger(attributes) : attributes;
    }

    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), cancellable = true)
    private void addTankWeight(CallbackInfoReturnable<com.google.common.collect.Multimap> cir) {
        if (!DivingTankAttachment.hasAttachment((ItemStack)(Object)this)) return;
        com.google.common.collect.Multimap modifiers = com.google.common.collect.HashMultimap.create(cir.getReturnValue());
        modifiers.put(btw.entity.attribute.BTWAttributes.armorWeight.getAttributeUnlocalizedName(),
                new net.minecraft.src.AttributeModifier(java.util.UUID.fromString("94a960cb-a662-48af-a180-e70fb34f69cd"), "Diving Tank Weight", 7.0D, 0));
        modifiers.put(btw.entity.attribute.BTWAttributes.hungerCost.getAttributeUnlocalizedName(),
                new api.entity.attribute.CustomDisplayAttributeModifier(java.util.UUID.fromString("53b1eaac-e41b-44d0-93bb-fb04796ac5fb"),
                        "Diving Tank Hunger", 0.16D, api.entity.attribute.AttributeOperation.ADDITIVE,
                        modifier -> "attribute.name." + btw.entity.attribute.BTWAttributes.hungerCost.getAttributeUnlocalizedName() + ".medium"));
        cir.setReturnValue(modifiers);
    }

    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), cancellable = true)
    private void addMaskWeight(CallbackInfoReturnable<com.google.common.collect.Multimap> cir) {
        if (!DivingMaskAttachment.hasAttachment((ItemStack)(Object)this)) return;
        com.google.common.collect.Multimap modifiers = com.google.common.collect.HashMultimap.create(cir.getReturnValue());
        modifiers.put(btw.entity.attribute.BTWAttributes.armorWeight.getAttributeUnlocalizedName(),
                new net.minecraft.src.AttributeModifier(java.util.UUID.fromString("bdfc25f0-f82d-4e86-9572-6193cf5df416"), "Diving Mask Weight", 3.0D, 0));
        modifiers.put(btw.entity.attribute.BTWAttributes.hungerCost.getAttributeUnlocalizedName(),
                new api.entity.attribute.CustomDisplayAttributeModifier(java.util.UUID.fromString("3cb2051f-20a2-4e2d-8ed9-e8fb4bb72109"),
                        "Diving Mask Hunger", 0.07D, api.entity.attribute.AttributeOperation.ADDITIVE,
                        modifier -> "attribute.name." + btw.entity.attribute.BTWAttributes.hungerCost.getAttributeUnlocalizedName() + ".tiny"));
        cir.setReturnValue(modifiers);
    }

    @Inject(method = "damageItem", at = @At("RETURN"))
    private void damageAttachedTank(int amount, EntityLivingBase wearer, CallbackInfo ci) {
        ItemStack host = (ItemStack)(Object)this;
        DivingMaskAttachment.damage(host, amount, wearer);
        if (amount <= 0 || wearer == null || wearer.worldObj.isRemote
                || wearer instanceof EntityPlayer player && player.capabilities.isCreativeMode
                || !DivingTankAttachment.hasAttachment(host)) return;
        ItemStack tank = DivingTankAttachment.getTank(host);
        if (tank == null) return;
        if (host.stackSize <= 0) {
            DivingTankAttachment.setTank(host, null);
            wearer.entityDropItem(tank, 0.0F);
        } else {
            tank.setItemDamage(Math.min(tank.getMaxDamage(), tank.getItemDamage() + amount));
            DivingTankAttachment.setTank(host, tank);
        }
    }

    @Inject(method = "isStackable", at = @At("HEAD"), cancellable = true)
    private void allowPerishableFoodStacking(CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        if (NMFoodSpoilage.isPerishable(stack)) cir.setReturnValue(stack.getMaxStackSize() > 1);
    }

    @org.spongepowered.asm.mixin.injection.ModifyVariable(method = "damageItem", at = @At("HEAD"), argsOnly = true)
    private int reduceEasyToolWear(int amount, int originalAmount, EntityLivingBase user) {
        if (amount <= 0 || user == null || user.worldObj.isRemote || !com.itlesports.nightmaremode.world.BalanceProfile.isEasy()
                || btw.community.nightmaremode.NightmareMode.testEasyDurability) return amount;
        ItemStack stack = (ItemStack)(Object)this;
        if (!NMItemStackUtils.isValid(stack) || !stack.isItemStackDamageable()
                || stack.getItemDamage() >= stack.getMaxDamage() - Math.max(10, amount)) return amount;
        Item item = stack.getItem();
        if (!(item instanceof net.minecraft.src.ItemTool || item instanceof net.minecraft.src.ItemArmor
                || item instanceof net.minecraft.src.ItemSword || item instanceof net.minecraft.src.ItemBow || item instanceof net.minecraft.src.ItemFishingRod || item instanceof net.minecraft.src.ItemShears
                || item instanceof com.itlesports.nightmaremode.item.items.ItemSoulFlint
                || item instanceof btw.item.items.ChiselItem || item instanceof com.itlesports.nightmaremode.item.items.template.ItemKnife
                || item instanceof ItemHammer || item instanceof com.itlesports.nightmaremode.item.items.ItemScythe
                || item instanceof com.itlesports.nightmaremode.item.items.ItemLeafRake)) return amount;
        return amount / 2 + (amount % 2 != 0 && user.getRNG().nextBoolean() ? 1 : 0);
    }

    @Inject(method = "attemptDamageItem", at = @At("HEAD"), cancellable = true)
    private void breakOnFinalDurabilityUse(int amount, java.util.Random random, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = (ItemStack)(Object)this;
        if (NMItemStackUtils.shouldBreakOnUse(stack, amount)) {
            stack.setItemDamage(stack.getMaxDamage());
            cir.setReturnValue(true);
        }
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
        if (amount > 0 && !NMItemStackUtils.shouldBreakOnUse(stack, amount)
                && stack.getItem() instanceof ItemHammer && user instanceof EntityPlayer player
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
