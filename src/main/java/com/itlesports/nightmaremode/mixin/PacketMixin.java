package com.itlesports.nightmaremode.mixin;

import net.minecraft.src.ItemStack;
import net.minecraft.src.Item;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.DataInput;
import java.io.DataOutput;

@Mixin(Packet.class)
public class PacketMixin {
    @Inject(method = "writeItemStack", at = @At("HEAD"))
    private static void writeExtendedDamage(ItemStack stack, DataOutput output, CallbackInfo ci) {
        if (stack == null || stack.itemID < 0 || stack.itemID >= Item.itemsList.length
                || Item.itemsList[stack.itemID] == null || stack.getMaxDamage() <= Short.MAX_VALUE) {
            return;
        }
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger("nmFullDamage", stack.getItemDamage());
    }

    @Inject(method = "readItemStack", at = @At("RETURN"))
    private static void readExtendedDamage(DataInput input, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        if (stack != null && stack.itemID >= 0 && stack.itemID < Item.itemsList.length
                && Item.itemsList[stack.itemID] != null && stack.getMaxDamage() > Short.MAX_VALUE && stack.hasTagCompound()
                && stack.getTagCompound().hasKey("nmFullDamage")) {
            stack.setItemDamage(stack.getTagCompound().getInteger("nmFullDamage"));
        }
    }
}
