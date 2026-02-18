package com.itlesports.nightmaremode.rendering.entities;

import com.itlesports.nightmaremode.entity.underworld.FlowerZombie;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.entity.EntityBloodZombie;
import com.itlesports.nightmaremode.entity.EntityZombieImposter;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.RenderZombie;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Unique;

public class RenderZombieVariant extends RenderZombie {
    private static final ResourceLocation bloodZombieTexture0 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame00.png");
    private static final ResourceLocation bloodZombieTexture1 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame11.png");
    private static final ResourceLocation bloodZombieTexture2 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame22.png");
    private static final ResourceLocation bloodZombieTexture3 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame33.png");
    private static final ResourceLocation bloodZombieTexture4 = new ResourceLocation("nightmare:textures/entity/zombieFleshFrame44.png");


    private static final ResourceLocation imposterZombieTexture = new ResourceLocation("nightmare:textures/entity/zombieImposter.png");

    private static final ResourceLocation flowerZombieTextures = new ResourceLocation("nightmare:textures/entity/flowerZombie.png");



    private static final ResourceLocation shadowZombieTextures = new ResourceLocation("nightmare:textures/entity/shadowzombie.png");
    private static final ResourceLocation shadowZombieTexturesEclipse = new ResourceLocation("nightmare:textures/entity/shadowzombieEclipse.png");

    @Override protected ResourceLocation func_110863_a(EntityZombie zombie) {
        if(zombie instanceof EntityBloodZombie){
            return this.getTextureForTicksExisted(zombie.ticksExisted);
        }
        if(zombie instanceof EntityZombieImposter){
            return imposterZombieTexture;
        }
        if(zombie instanceof FlowerZombie){
            return flowerZombieTextures;
        }
        return NMUtils.getIsMobEclipsed(zombie) ? shadowZombieTexturesEclipse : shadowZombieTextures;
    }

    @Unique
    private ResourceLocation getTextureForTicksExisted(int ticks){
        int ticksMod100 = ticks % 100;
        if(ticksMod100 < 88){
            return bloodZombieTexture0;
        }
        else if(ticksMod100 < 91){
            return bloodZombieTexture1;
        }
        else if(ticksMod100 < 94){
            return bloodZombieTexture2;
        }
        else if(ticksMod100 < 97){
            return bloodZombieTexture3;
        }
        else {
            return bloodZombieTexture4;
        }
    }
}
