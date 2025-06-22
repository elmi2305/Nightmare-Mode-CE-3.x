package com.itlesports.nightmaremode.rendering;

import com.itlesports.nightmaremode.entity.EntityFauxVillager;
import net.minecraft.src.*;

public class RenderFauxVillager extends RenderLiving {
    protected ModelVillager villagerModel;
    public static final ResourceLocation VILLAGER_TEXTURE = new ResourceLocation("textures/entity/fauxVillager.png");
    public static final ResourceLocation VILLAGER_TEXTURE_RED = new ResourceLocation("textures/entity/fauxVillagerRed.png");

    public RenderFauxVillager() {
        super(new ModelVillager(0.0f), 0.5f);
        this.villagerModel = (ModelVillager)this.mainModel;
        this.setRenderPassModel(this.villagerModel);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity var1) {
        boolean redEyes = false;
        if(var1 instanceof EntityFauxVillager villager){
             redEyes = villager.getAngerTicks() >= 800;
        }
        return redEyes ? VILLAGER_TEXTURE_RED : VILLAGER_TEXTURE;
    }
}
