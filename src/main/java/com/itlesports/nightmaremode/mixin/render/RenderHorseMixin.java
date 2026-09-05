package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.item.items.ItemAlloyHorseArmor;
import com.itlesports.nightmaremode.rendering.AlloyHorseTexture;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(RenderHorse.class)
public abstract class RenderHorseMixin {
    @Unique private static final ResourceLocation HORSE_ECLIPSE = new ResourceLocation("nightmare:textures/entity/horseEclipse.png");

    @Inject(method = "func_110849_a", at = @At("HEAD"),cancellable = true)
    private void horseEclipseTextures(EntityHorse par1, CallbackInfoReturnable<ResourceLocation> cir){
        ItemAlloyHorseArmor.Material material = ItemAlloyHorseArmor.Material.fromArmorIndex(par1.func_110241_cb());
        if (material != null) {
            boolean eclipsed = NMUtils.getIsMobEclipsed(par1);
            ResourceLocation texture = new ResourceLocation("nightmare:horse_armor/" + material.armorIndex()
                    + "/" + (eclipsed ? "eclipse" : par1.getHorseTexture()));
            TextureManager manager = Minecraft.getMinecraft().getTextureManager();
            if (manager.getTexture(texture) == null) {
                String[] layers = java.util.Arrays.copyOf(par1.getVariantTexturePaths(), 3);
                if (eclipsed) {
                    layers[0] = HORSE_ECLIPSE.toString();
                    layers[1] = null;
                }
                manager.loadTexture(texture, new AlloyHorseTexture(layers, material.color));
            }
            cir.setReturnValue(texture);
            return;
        }
        if (NMUtils.getIsMobEclipsed(par1)) {
            cir.setReturnValue(HORSE_ECLIPSE);
        }
    }
    @Redirect(method = "func_110849_a", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityHorse;getHungerLevel()I"))
    private int baseHungerIfTamed(EntityHorse instance){
        if(instance.isTame() && instance.func_110241_cb() != 0){
            return 0; // wearing armor
            // hacky fix. other stuff I tried to do (like fixing the render itself) didn't work
        }
        return instance.getHungerLevel();
    }
}
