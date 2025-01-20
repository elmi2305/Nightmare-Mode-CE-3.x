package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RenderPlayer.class)
public abstract class RenderPlayerMixin extends RendererLivingEntity {

    @Unique private ResourceLocation BLOOD_ARMOR_LAYER_1 = new ResourceLocation("textures/armor/bloodArmorLayer1.png");
    @Unique private ResourceLocation BLOOD_ARMOR_LAYER_2 = new ResourceLocation("textures/armor/bloodArmorLayer2.png");

    public RenderPlayerMixin(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);
    }

    @Inject(method = "setArmorModel",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/src/RenderPlayer;bindTexture(Lnet/minecraft/src/ResourceLocation;)V",shift = At.Shift.AFTER),locals = LocalCapture.CAPTURE_FAILHARD)
    private void manageBloodMoonArmorRendering1(AbstractClientPlayer par1AbstractClientPlayer, int par2, float par3, CallbackInfoReturnable<Integer> cir, ItemStack var4, Item var5, ItemArmor armorItem, ResourceLocation armorTextureLoc){
        if(armorItem.itemID == NMItems.bloodHelmet.itemID || armorItem.itemID == NMItems.bloodBoots.itemID || armorItem.itemID == NMItems.bloodChestplate.itemID) {
            this.bindTexture(BLOOD_ARMOR_LAYER_1);
        } else if(armorItem.itemID == NMItems.bloodLeggings.itemID){
            this.bindTexture(BLOOD_ARMOR_LAYER_2);
        }
    }
}
