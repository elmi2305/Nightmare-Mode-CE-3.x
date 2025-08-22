package com.itlesports.nightmaremode.rendering;

import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.entity.EntityBloodZombie;
import com.itlesports.nightmaremode.entity.EntityZombieImposter;
import net.minecraft.src.EntityZombie;
import net.minecraft.src.RenderZombie;
import net.minecraft.src.ResourceLocation;
import org.spongepowered.asm.mixin.Unique;

public class RenderZombieVariant extends RenderZombie {
    private static final ResourceLocation bloodZombieTexture0 = new ResourceLocation("textures/entity/zombieFleshFrame00.png");
    private static final ResourceLocation bloodZombieTexture1 = new ResourceLocation("textures/entity/zombieFleshFrame11.png");
    private static final ResourceLocation bloodZombieTexture2 = new ResourceLocation("textures/entity/zombieFleshFrame22.png");
    private static final ResourceLocation bloodZombieTexture3 = new ResourceLocation("textures/entity/zombieFleshFrame33.png");
    private static final ResourceLocation bloodZombieTexture4 = new ResourceLocation("textures/entity/zombieFleshFrame44.png");


    private static final ResourceLocation imposterZombieTexture = new ResourceLocation("textures/entity/zombieImposter.png");



    private static final ResourceLocation shadowZombieTextures = new ResourceLocation("textures/entity/shadowzombie.png");
    private static final ResourceLocation shadowZombieTexturesEclipse = new ResourceLocation("textures/entity/shadowzombieEclipse.png");

    @Override protected ResourceLocation func_110863_a(EntityZombie par1EntityZombie) {
        if(par1EntityZombie instanceof EntityBloodZombie){
            return this.getTextureForTicksExisted(par1EntityZombie.ticksExisted);
        }
        if(par1EntityZombie instanceof EntityZombieImposter){
            return imposterZombieTexture;
        }
        return NMUtils.getIsMobEclipsed(par1EntityZombie) ? shadowZombieTexturesEclipse : shadowZombieTextures;
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
