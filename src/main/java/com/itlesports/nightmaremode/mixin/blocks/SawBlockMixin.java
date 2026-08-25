package com.itlesports.nightmaremode.mixin.blocks;

import btw.block.blocks.SawBlock;
import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import net.minecraft.src.Block;
import net.minecraft.src.DamageSource;
import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SawBlock.class)
public class SawBlockMixin {

    @ModifyArg(method = "scheduleUpdateIfRequired", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;scheduleBlockUpdate(IIIII)V", ordinal = 1), index = 4)
    private int lowerSawCuttingTime(int par1){
        return 120;
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
