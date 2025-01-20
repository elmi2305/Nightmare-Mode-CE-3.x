package com.itlesports.nightmaremode.mixin.render;

import btw.item.items.ArmorItemMod;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderBiped.class)
public abstract class RenderBipedMixin extends RenderLiving {
    @Unique private ResourceLocation BLOOD_ARMOR_LAYER_1 = new ResourceLocation("textures/armor/bloodArmorLayer1.png");
    @Unique private ResourceLocation BLOOD_ARMOR_LAYER_2 = new ResourceLocation("textures/armor/bloodArmorLayer2.png");

    public RenderBipedMixin(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);
    }

    @ModifyArg(method = "shouldRenderPassModArmor",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBiped;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation manageBloodMoonArmorRendering(ResourceLocation par1){
        return BLOOD_ARMOR_LAYER_1;
    }

    @ModifyArg(method = "loadSecondLayerOfModArmorTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/RenderBiped;bindTexture(Lnet/minecraft/src/ResourceLocation;)V"))
    private ResourceLocation manageBloodArmorRenderingLayer2(ResourceLocation par1){
        return BLOOD_ARMOR_LAYER_2;
    }

    // TODO: decide if enemies should ever wear blood armor


    @Inject(method = "shouldRenderPassModArmor",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBiped;bindTexture(Lnet/minecraft/src/ResourceLocation;)V",shift = At.Shift.AFTER))
    private void manageBloodMoonArmorRendering(ItemStack stack, int iArmorSlot, ArmorItemMod armorItem, CallbackInfoReturnable<Integer> cir){
        if(armorItem != null && armorItem.itemID == NMItems.bloodChestplate.itemID){
            this.bindTexture(BLOOD_ARMOR_LAYER_1);
        }
    }

    @Inject(method = "loadSecondLayerOfModArmorTexture",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderBiped;bindTexture(Lnet/minecraft/src/ResourceLocation;)V",shift = At.Shift.AFTER))
    private void manageBloodMoonArmorRenderingLayer2(int iArmorSlot, ArmorItemMod armorItem, CallbackInfo ci){
        System.out.println(iArmorSlot);
        System.out.println(armorItem.itemID == NMItems.bloodChestplate.itemID);
        if(armorItem.itemID == NMItems.bloodChestplate.itemID){
            this.bindTexture(BLOOD_ARMOR_LAYER_2);
        }
    }
}
