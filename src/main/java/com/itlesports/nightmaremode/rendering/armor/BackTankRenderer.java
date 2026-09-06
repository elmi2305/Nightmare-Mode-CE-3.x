package com.itlesports.nightmaremode.rendering.armor;

import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.rendering.entities.models.ModelBackTank;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.ResourceLocation;
import org.lwjgl.opengl.GL11;

public final class BackTankRenderer {
    private static final ResourceLocation OXYGEN_TANK_TEXTURE =
            new ResourceLocation("nightmare:textures/armor/ifhyOxygenTank.png");
    private static final ResourceLocation DIVING_TANK_TEXTURE =
            new ResourceLocation("nightmare:textures/armor/ifhyDivingTank.png");
    private static final ResourceLocation SUN_RESERVOIR_TEXTURE =
            new ResourceLocation("nightmare:textures/armor/ifhySunReservoir.png");

    private static final ModelBackTank MODEL = new ModelBackTank();

    private BackTankRenderer() {
    }

    public static void render(EntityLivingBase entity, ModelBiped bipedModel, float scale) {
        ItemStack chest = entity.getCurrentItemOrArmor(3);
        ResourceLocation texture = getTexture(chest);
        if (texture == null) {
            return;
        }

        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPushMatrix();
        if (bipedModel.isChild) {
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            GL11.glTranslatef(0.0F, 24.0F * scale, 0.0F);
        }
        bipedModel.bipedBody.postRender(scale);
        MODEL.render(scale);
        GL11.glPopMatrix();
    }

    private static ResourceLocation getTexture(ItemStack chest) {
        if (chest == null) {
            return null;
        }
        if (chest.getItem() == NMItems.oxygenTank) {
            return OXYGEN_TANK_TEXTURE;
        }
        if (chest.getItem() == NMItems.divingTank) {
            return DIVING_TANK_TEXTURE;
        }
        if (chest.getItem() == NMItems.sunReservoir) {
            return SUN_RESERVOIR_TEXTURE;
        }
        return null;
    }
}
