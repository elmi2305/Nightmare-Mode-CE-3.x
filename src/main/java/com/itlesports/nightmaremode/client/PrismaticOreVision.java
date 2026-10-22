package com.itlesports.nightmaremode.client;

import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.BlockOreNode;
import com.itlesports.nightmaremode.util.ArmorSetHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class PrismaticOreVision {
    private record Deposit(int x, int y, int z, int blockID, int color) {}
    private static final ResourceLocation PARTICLES = new ResourceLocation("textures/particle/particles.png");
    private static final List<Deposit> DEPOSITS = new ArrayList<>();
    private static World lastWorld;
    private static long lastScan = Long.MIN_VALUE;
    private static int lastX, lastY, lastZ;
    private PrismaticOreVision() {}

    public static int color(Block block) {
        if (block == null) return -1;
        if (block == NMBlocks.nickelOre || block == NMBlocks.nickelOreNode) return 0xFFD635;
        if (block == NMBlocks.lithiumOre || block == NMBlocks.lithiumOreNode) return 0xA8ADB8;
        if (block == Block.oreDiamond) return 0x399AFF;
        if (block == Block.oreEmerald) return 0x3BED69;
        if (block == NMBlocks.mercuryOre || block == NMBlocks.mercuryOreNode) return 0xB95FFF;
        if (block == NMBlocks.crystalPocket) return 0xFF83D5;
        if (block == NMBlocks.tungstenOreNode) return 0xFF9438;
        if (block == NMBlocks.coalOreNode) return 0x71645B;
        if (block == NMBlocks.denseNetherrackCoreNode) return 0xFF4339;
        if (block == NMBlocks.deadzoneShardNode) return 0xE8EBEC;
        if (block == NMBlocks.gravititeNode) return 0x7866A8;
        if (block == NMBlocks.solarQuartzNode) return 0xF0B95A;
        if (block == NMBlocks.abyssNode) return 0x287F91;
        if (block == NMBlocks.cryoliteNode) return 0xA7E8F6;
        return block instanceof BlockOreNode ? block.getRenderColor(0) : -1;
    }

    public static void render(Minecraft minecraft, float partialTicks) {
        EntityPlayer player = minecraft.thePlayer;
        World world = minecraft.theWorld;
        if (player == null || world == null || !player.isEntityAlive() || !ArmorSetHelper.isWearingPrismaticSet(player)) {
            DEPOSITS.clear();
            lastWorld = null;
            return;
        }
        int px = MathHelper.floor_double(player.posX);
        int py = MathHelper.floor_double(player.boundingBox.minY);
        int pz = MathHelper.floor_double(player.posZ);
        long tick = world.getTotalWorldTime();
        if (lastWorld != world || px != lastX || py != lastY || pz != lastZ || tick - lastScan >= 5 || tick < lastScan) {
            DEPOSITS.clear();
            for (int x = px - 6; x <= px + 6; ++x) {
                for (int y = Math.max(0, py - 6); y <= Math.min(255, py + 6); ++y) {
                    for (int z = pz - 6; z <= pz + 6; ++z) {
                        if (!world.blockExists(x, y, z)) continue;
                        int id = world.getBlockId(x, y, z);
                        int tint = color(Block.blocksList[id]);
                        if (tint >= 0) DEPOSITS.add(new Deposit(x, y, z, id, tint));
                    }
                }
            }
            lastWorld = world;
            lastScan = tick;
            lastX = px; lastY = py; lastZ = pz;
        }
        if (DEPOSITS.isEmpty()) return;
        EntityLivingBase camera = minecraft.renderViewEntity;
        if (camera == null) return;
        double cameraX = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * partialTicks;
        double cameraY = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * partialTicks;
        double cameraZ = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * partialTicks;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_FOG);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.001F);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            minecraft.getTextureManager().bindTexture(PARTICLES);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            tessellator.setBrightness(0xF000F0);
            for (Deposit deposit : DEPOSITS) {
                if (world.getBlockId(deposit.x, deposit.y, deposit.z) != deposit.blockID) continue;
                long seed = deposit.x * 73428767L ^ deposit.y * 912931L ^ deposit.z * 438289L;
                float age = ((tick % 48 + partialTicks + Math.floorMod(seed, 48L)) % 48) / 48.0F;
                float alpha = (float)Math.sin(age * Math.PI) * 0.7F;
                for (int puff = 0; puff < 3; ++puff) {
                    float offset = (puff - 1) * 0.16F;
                    drawSmoke(tessellator, deposit.x + 0.5D + offset - cameraX,
                            deposit.y + 0.35D + age * 0.25D + puff * 0.08D - cameraY,
                            deposit.z + 0.5D - offset - cameraZ, 0.13F + age * 0.12F,
                            deposit.color, alpha, age);
                }
            }
            tessellator.draw();
        } finally {
            GL11.glPopAttrib();
        }
    }

    private static void drawSmoke(Tessellator tessellator, double x, double y, double z,
                                   float size, int color, float alpha, float age) {
        float u = (7 - Math.min(7, (int)(age * 8))) / 16.0F;
        float u1 = u + 0.0624375F;
        float v1 = 0.0624375F;
        float rx = ActiveRenderInfo.rotationX * size;
        float rz = ActiveRenderInfo.rotationZ * size;
        float ryz = ActiveRenderInfo.rotationXY * size;
        float rxy = ActiveRenderInfo.rotationYZ * size;
        float rxz = ActiveRenderInfo.rotationXZ * size;
        tessellator.setColorRGBA_F((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, alpha);
        tessellator.addVertexWithUV(x - rx - rxy, y - rxz, z - rz - ryz, u1, v1);
        tessellator.addVertexWithUV(x - rx + rxy, y + rxz, z - rz + ryz, u1, 0);
        tessellator.addVertexWithUV(x + rx + rxy, y + rxz, z + rz + ryz, u, 0);
        tessellator.addVertexWithUV(x + rx - rxy, y - rxz, z + rz - ryz, u, v1);
    }
}
