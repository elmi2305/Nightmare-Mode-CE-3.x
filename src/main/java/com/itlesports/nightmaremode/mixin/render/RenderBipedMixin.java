package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.rendering.armor.BackTankRenderer;
import btw.item.items.ArmorItemDiamond;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderBiped.class)
public abstract class RenderBipedMixin extends RenderLiving {
    @Shadow protected ModelBiped modelBipedMain;

    public RenderBipedMixin(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);
    }

    @Inject(method = "func_110857_a", at = @At("HEAD"),cancellable = true)
    private static void allowRenderingDiamondArmor(ItemArmor par0ItemArmor, int par1, CallbackInfoReturnable<ResourceLocation> cir) {
        if (par0ItemArmor instanceof ArmorItemDiamond) {
            int layer = (par1 == 2) ? 2 : 1;
            String texture = String.format("nightmare:textures/armor/nm_diamond_layer_%d.png", layer);
            cir.setReturnValue(new ResourceLocation(texture));
        }
    }

    @Inject(method = "func_130005_c", at = @At("RETURN"))
    private void renderBackTank(EntityLiving entity, float partialTicks, CallbackInfo ci) {
        BackTankRenderer.render(entity, this.modelBipedMain, 0.0625F);
    }
}
