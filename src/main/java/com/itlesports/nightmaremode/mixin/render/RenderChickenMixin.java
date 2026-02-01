package com.itlesports.nightmaremode.mixin.render;

import com.itlesports.nightmaremode.NMUtils;
import net.minecraft.src.EntityChicken;
import net.minecraft.src.RenderChicken;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(RenderChicken.class)
public class RenderChickenMixin {
    @Unique private static final ResourceLocation CHICKEN_0 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse0.png");
    @Unique private static final ResourceLocation CHICKEN_1 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse1.png");
    @Unique private static final ResourceLocation CHICKEN_2 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse2.png");
    @Unique private static final ResourceLocation CHICKEN_3 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse3.png");
    @Unique private static final ResourceLocation CHICKEN_4 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse4.png");
    @Unique private static final ResourceLocation CHICKEN_5 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse5.png");
    @Unique private static final ResourceLocation CHICKEN_6 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse6.png");
    @Unique private static final ResourceLocation CHICKEN_7 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse7.png");
    @Unique private static final ResourceLocation CHICKEN_8 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse8.png");
    @Unique private static final ResourceLocation CHICKEN_9 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse9.png");
    @Unique private static final ResourceLocation CHICKEN_10 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse10.png");
    @Unique private static final ResourceLocation CHICKEN_11 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse11.png");
    @Unique private static final ResourceLocation CHICKEN_12 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse12.png");
    @Unique private static final ResourceLocation CHICKEN_13 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse13.png");
    @Unique private static final ResourceLocation CHICKEN_14 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse14.png");
    @Unique private static final ResourceLocation CHICKEN_15 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse15.png");
    @Unique private static final ResourceLocation CHICKEN_16 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse16.png");
    @Unique private static final ResourceLocation CHICKEN_17 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse17.png");
    @Unique private static final ResourceLocation CHICKEN_18 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse18.png");
    @Unique private static final ResourceLocation CHICKEN_19 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse19.png");
    @Unique private static final ResourceLocation CHICKEN_20 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse20.png");
    @Unique private static final ResourceLocation CHICKEN_21 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse21.png");
    @Unique private static final ResourceLocation CHICKEN_22 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse22.png");
    @Unique private static final ResourceLocation CHICKEN_23 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse23.png");
    @Unique private static final ResourceLocation CHICKEN_24 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse24.png");
    @Unique private static final ResourceLocation CHICKEN_25 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse25.png");
    @Unique private static final ResourceLocation CHICKEN_26 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse26.png");
    @Unique private static final ResourceLocation CHICKEN_27 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse27.png");
    @Unique private static final ResourceLocation CHICKEN_28 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse28.png");
    @Unique private static final ResourceLocation CHICKEN_29 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse29.png");
    @Unique private static final ResourceLocation CHICKEN_30 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse30.png");
    @Unique private static final ResourceLocation CHICKEN_31 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse31.png");
    @Unique private static final ResourceLocation CHICKEN_32 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse32.png");
    @Unique private static final ResourceLocation CHICKEN_33 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse33.png");
    @Unique private static final ResourceLocation CHICKEN_34 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse34.png");
    @Unique private static final ResourceLocation CHICKEN_35 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse35.png");
    @Unique private static final ResourceLocation CHICKEN_36 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse36.png");
    @Unique private static final ResourceLocation CHICKEN_37 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse37.png");
    @Unique private static final ResourceLocation CHICKEN_38 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse38.png");
    @Unique private static final ResourceLocation CHICKEN_39 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse39.png");
    @Unique private static final ResourceLocation CHICKEN_40 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse40.png");
    @Unique private static final ResourceLocation CHICKEN_41 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse41.png");
    @Unique private static final ResourceLocation CHICKEN_42 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse42.png");
    @Unique private static final ResourceLocation CHICKEN_43 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse43.png");
    @Unique private static final ResourceLocation CHICKEN_44 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse44.png");
    @Unique private static final ResourceLocation CHICKEN_45 = new ResourceLocation("nightmare:textures/entity/chicken/chickenEclipse45.png");

    @Inject(method = "getChickenTextures", at = @At("HEAD"),cancellable = true)
    private void chickenTextures(EntityChicken par1EntityChicken, CallbackInfoReturnable<ResourceLocation> cir){
        if(NMUtils.getIsMobEclipsed(par1EntityChicken)){
            cir.setReturnValue(this.getResourceLocation(par1EntityChicken.ticksExisted % 90));
        }
    }

    @Unique private ResourceLocation getResourceLocation(int index){
        if(index > 45){
            index = 90 - index;
        }
        return chickenTextures.get(index);
    }
    @Unique
    private static final List<ResourceLocation> chickenTextures = new ArrayList<>(Arrays.asList(
            CHICKEN_0,
            CHICKEN_1,
            CHICKEN_2,
            CHICKEN_3,
            CHICKEN_4,
            CHICKEN_5,
            CHICKEN_6,
            CHICKEN_7,
            CHICKEN_8,
            CHICKEN_9,
            CHICKEN_10,
            CHICKEN_11,
            CHICKEN_12,
            CHICKEN_13,
            CHICKEN_14,
            CHICKEN_15,
            CHICKEN_16,
            CHICKEN_17,
            CHICKEN_18,
            CHICKEN_19,
            CHICKEN_20,
            CHICKEN_21,
            CHICKEN_22,
            CHICKEN_23,
            CHICKEN_24,
            CHICKEN_25,
            CHICKEN_26,
            CHICKEN_27,
            CHICKEN_28,
            CHICKEN_29,
            CHICKEN_30,
            CHICKEN_31,
            CHICKEN_32,
            CHICKEN_33,
            CHICKEN_34,
            CHICKEN_35,
            CHICKEN_36,
            CHICKEN_37,
            CHICKEN_38,
            CHICKEN_39,
            CHICKEN_40,
            CHICKEN_41,
            CHICKEN_42,
            CHICKEN_43,
            CHICKEN_44,
            CHICKEN_45
    ));


}
