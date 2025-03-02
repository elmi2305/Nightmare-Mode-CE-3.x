package com.itlesports.nightmaremode.mixin;

import btw.entity.UrnEntity;
import com.itlesports.nightmaremode.entity.EntityBloodWither;
import com.itlesports.nightmaremode.block.NMBlocks;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UrnEntity.class)
public class UrnEntityMixin {
    @Inject(method = "attemptToCreateWither", at = @At("HEAD"),cancellable = true)
    private static void witherSummoningRestrictions(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
        if(j < 60){
            ChatMessageComponent text2 = new ChatMessageComponent();
            text2.addText("The Wither must be summoned above sea level.");
            text2.setColor(EnumChatFormatting.BLACK);
            world.getClosestPlayer(i,j,k,-1).sendChatToPlayer(text2);
            cir.setReturnValue(false);
        } else if(j > 200){
            ChatMessageComponent text2 = new ChatMessageComponent();
            text2.addText("The Wither cannot be summoned this high.");
            text2.setColor(EnumChatFormatting.BLACK);
            world.getClosestPlayer(i,j,k,-1).sendChatToPlayer(text2);
            cir.setReturnValue(false);
        }
        // this prints a chat message for every single wither-creating block in the soul urns range. but it works so I'm not complaining
    }

    @Inject(method = "attemptToCreateWither", at = @At("HEAD"),cancellable = true)
    private static void summonBloodWither(World world, int i, int j, int k, CallbackInfoReturnable<Boolean> cir){
        if (j >= 2 && world.provider.dimensionId == 0) {
            for(int iTempKOffset = -2; iTempKOffset <= 0; ++iTempKOffset) {
                if (isWitherBodyBlock(world, i, j - 1, k + iTempKOffset) && isWitherBodyBlock(world, i, j - 1, k + iTempKOffset + 1) && isWitherBodyBlock(world, i, j - 2, k + iTempKOffset + 1) && isWitherBodyBlock(world, i, j - 1, k + iTempKOffset + 2) && isWitherHeadBlock(world, i, j, k + iTempKOffset) && isWitherHeadBlock(world, i, j, k + iTempKOffset + 1) && isWitherHeadBlock(world, i, j, k + iTempKOffset + 2)) {
                    world.SetBlockMetadataWithNotify(i, j, k + iTempKOffset, 8, 2);
                    world.SetBlockMetadataWithNotify(i, j, k + iTempKOffset + 1, 8, 2);
                    world.SetBlockMetadataWithNotify(i, j, k + iTempKOffset + 2, 8, 2);
                    world.setBlock(i, j, k + iTempKOffset, 0, 0, 2);
                    world.setBlock(i, j, k + iTempKOffset + 1, 0, 0, 2);
                    world.setBlock(i, j, k + iTempKOffset + 2, 0, 0, 2);
                    world.setBlock(i, j - 1, k + iTempKOffset, 0, 0, 2);
                    world.setBlock(i, j - 1, k + iTempKOffset + 1, 0, 0, 2);
                    world.setBlock(i, j - 1, k + iTempKOffset + 2, 0, 0, 2);
                    world.setBlock(i, j - 2, k + iTempKOffset + 1, 0, 0, 2);
                    EntityBloodWither.summonWitherAtLocation(world, i, k + iTempKOffset + 1);
                    world.notifyBlockChange(i, j, k + iTempKOffset, 0);
                    world.notifyBlockChange(i, j, k + iTempKOffset + 1, 0);
                    world.notifyBlockChange(i, j, k + iTempKOffset + 2, 0);
                    world.notifyBlockChange(i, j - 1, k + iTempKOffset, 0);
                    world.notifyBlockChange(i, j - 1, k + iTempKOffset + 1, 0);
                    world.notifyBlockChange(i, j - 1, k + iTempKOffset + 2, 0);
                    world.notifyBlockChange(i, j - 2, k + iTempKOffset + 1, 0);
                    teleportPlayersAndMakeFirstPlatform(world,i,j,k);
                    cir.setReturnValue(true);
                }
            }

            for(int iTempIOffset = -2; iTempIOffset <= 0; ++iTempIOffset) {
                if (isWitherBodyBlock(world, i + iTempIOffset, j - 1, k) && isWitherBodyBlock(world, i + iTempIOffset + 1, j - 1, k) && isWitherBodyBlock(world, i + iTempIOffset + 1, j - 2, k) && isWitherBodyBlock(world, i + iTempIOffset + 2, j - 1, k) && isWitherHeadBlock(world, i + iTempIOffset, j, k) && isWitherHeadBlock(world, i + iTempIOffset + 1, j, k) && isWitherHeadBlock(world, i + iTempIOffset + 2, j, k)) {
                    world.SetBlockMetadataWithNotify(i + iTempIOffset, j, k, 8, 2);
                    world.SetBlockMetadataWithNotify(i + iTempIOffset + 1, j, k, 8, 2);
                    world.SetBlockMetadataWithNotify(i + iTempIOffset + 2, j, k, 8, 2);
                    world.setBlock(i + iTempIOffset, j, k, 0, 0, 2);
                    world.setBlock(i + iTempIOffset + 1, j, k, 0, 0, 2);
                    world.setBlock(i + iTempIOffset + 2, j, k, 0, 0, 2);
                    world.setBlock(i + iTempIOffset, j - 1, k, 0, 0, 2);
                    world.setBlock(i + iTempIOffset + 1, j - 1, k, 0, 0, 2);
                    world.setBlock(i + iTempIOffset + 2, j - 1, k, 0, 0, 2);
                    world.setBlock(i + iTempIOffset + 1, j - 2, k, 0, 0, 2);
                    EntityBloodWither.summonWitherAtLocation(world, i + iTempIOffset + 1, k);
                    world.notifyBlockChange(i + iTempIOffset, j, k, 0);
                    world.notifyBlockChange(i + iTempIOffset + 1, j, k, 0);
                    world.notifyBlockChange(i + iTempIOffset + 2, j, k, 0);
                    world.notifyBlockChange(i + iTempIOffset, j - 1, k, 0);
                    world.notifyBlockChange(i + iTempIOffset + 1, j - 1, k, 0);
                    world.notifyBlockChange(i + iTempIOffset + 2, j - 1, k, 0);
                    world.notifyBlockChange(i + iTempIOffset + 1, j - 2, k, 0);
                    teleportPlayersAndMakeFirstPlatform(world,i,j,k);
                    cir.setReturnValue(true);
                }
            }
        }
    }
    @Unique
    private static boolean isWitherBodyBlock(World world, int i, int j, int k) {
        return world.getBlockId(i, j, k) == NMBlocks.bloodBones.blockID;
    }

    @Unique
    private static boolean isWitherHeadBlock(World world, int i, int j, int k) {
        int iBlockID = world.getBlockId(i, j, k);
        if (iBlockID == Block.skull.blockID) {
            TileEntity tileEntity = world.getBlockTileEntity(i, j, k);
            if (tileEntity instanceof TileEntitySkull) {
                return ((TileEntitySkull)tileEntity).getSkullType() == 5;
            }
        }

        return false;
    }
    @Unique private static void teleportPlayersAndMakeFirstPlatform(World world, int x,int y, int z){
        for(Object player : world.playerEntities) {
            if (((EntityPlayer)player).getDistanceSq(x,y,z) < 625) {
                ((EntityPlayer)player).setPositionAndUpdate(x + 0.5, 201, z - 10);
            }
        }

        for(int i = x - 2; i < x + 3; i++){
            for(int j = z - 1; j < z + 2; j++){
                int blockID = world.rand.nextBoolean() ? NMBlocks.specialObsidian.blockID : NMBlocks.cryingObsidian.blockID;
                world.setBlock(i,199,j,blockID);
            }
        }

        for(int i = x - 1; i < x + 2; i++){
            for(int j = z - 12; j < z - 9; j++){
                int blockID = world.rand.nextBoolean() ? NMBlocks.specialObsidian.blockID : NMBlocks.cryingObsidian.blockID;
                world.setBlock(i,199,j,blockID);
            }
        }
    }
}
