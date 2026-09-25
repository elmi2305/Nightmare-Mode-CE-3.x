package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.SawBlock;
import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.minecraft.src.Block;
import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SawBlock.class)
public class SawBlockMixin {

    @Redirect(method = "scheduleUpdateIfRequired", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/src/World;scheduleBlockUpdate(IIIII)V", ordinal = 1))
    private void nightmareMode$applySawSkillSpeed(World world, int x, int y, int z, int blockId, int ticks) {
        EntityPlayer player = world.getClosestPlayer(x + 0.5D, y + 0.5D, z + 0.5D, 16.0D);
        float bonus = player == null ? 0.0F : SkillHandler.getPlayerData(player).machineSpeedBonus;
        world.scheduleBlockUpdate(x, y, z, blockId, Math.max(1, Math.round(120.0F / (1.0F + bonus))));
    }

    @Redirect(method = "sawBlockToFront", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/Block;onBlockSawed(Lnet/minecraft/src/World;IIIIII)Z"))
    private boolean polluteWhenSawingBlock(Block target, World world, int x, int y, int z, int sawX, int sawY, int sawZ) {
        boolean sawed = target.onBlockSawed(world, x, y, z, sawX, sawY, sawZ);
        if (sawed) ChunkPollutionManager.pollute(world, sawX, sawY, sawZ, 6.0F);
        return sawed;
    }

    @Redirect(method = "onEntityCollidedWithBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/EntityLivingBase;attackEntityFrom(Lnet/minecraft/src/DamageSource;F)Z"))
    private boolean polluteWhenSawKills(EntityLivingBase target, DamageSource source, float damage) {
        boolean lethal = target.getHealth() <= damage;
        boolean hit = target.attackEntityFrom(source, damage);
        if (hit && lethal) {
            ChunkPollutionManager.pollute(target.worldObj, (int)Math.floor(target.posX), (int)Math.floor(target.posY), (int)Math.floor(target.posZ), 45.0F);
        }
        return hit;
    }
}
