package com.itlesports.nightmaremode.mixin.entity;

import btw.community.nightmaremode.NightmareMode;
import btw.entity.UrnEntity;
import com.itlesports.nightmaremode.entity.EntityBloodWither;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.underworld.EntityAwakenedWither;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenShadowRealm;
import com.itlesports.nightmaremode.util.NMFields;
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
            text2.addKey("bosses.wither_summon_sealevel");
            text2.setColor(EnumChatFormatting.YELLOW);
            world.getClosestPlayer(i,j,k,-1).sendChatToPlayer(text2);
            cir.setReturnValue(false);
        } else if(j > 200){
            ChatMessageComponent text2 = new ChatMessageComponent();
            text2.addKey("bosses.wither_summon_too_high");
            text2.setColor(EnumChatFormatting.YELLOW);
            world.getClosestPlayer(i,j,k,-1).sendChatToPlayer(text2);
            cir.setReturnValue(false);
        }
        // this prints a chat message for every single wither-creating block in the soul urns range. but it works so I'm not complaining
    }

    @Inject(method = "attemptToCreateWither", at = @At("HEAD"),cancellable = true)
    private static void summonBloodWither(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir){
        if (y >= 2) {
            if (world.provider.dimensionId == 0) {
                for(int temp = -2; temp <= 0; ++temp) {
                    if (
                            isBloodWitherBodyBlock(world, x, y - 1, z + temp)
                            && isBloodWitherBodyBlock(world, x, y - 1, z + temp + 1)
                            && isBloodWitherBodyBlock(world, x, y - 2, z + temp + 1)
                            && isBloodWitherBodyBlock(world, x, y - 1, z + temp + 2)
                            && isWitherHeadBlock(world, x, y, z + temp)
                            && isWitherHeadBlock(world, x, y, z + temp + 1)
                            && isWitherHeadBlock(world, x, y, z + temp + 2)) {

                        world.SetBlockMetadataWithNotify(x, y, z + temp, 8, 2);
                        world.SetBlockMetadataWithNotify(x, y, z + temp + 1, 8, 2);
                        world.SetBlockMetadataWithNotify(x, y, z + temp + 2, 8, 2);
                        world.setBlock(x, y, z + temp, 0, 0, 2);
                        world.setBlock(x, y, z + temp + 1, 0, 0, 2);
                        world.setBlock(x, y, z + temp + 2, 0, 0, 2);
                        world.setBlock(x, y - 1, z + temp, 0, 0, 2);
                        world.setBlock(x, y - 1, z + temp + 1, 0, 0, 2);
                        world.setBlock(x, y - 1, z + temp + 2, 0, 0, 2);
                        world.setBlock(x, y - 2, z + temp + 1, 0, 0, 2);
                        EntityBloodWither.summonWitherAtLocation(world, x, z + temp + 1);
                        world.notifyBlockChange(x, y, z + temp, 0);
                        world.notifyBlockChange(x, y, z + temp + 1, 0);
                        world.notifyBlockChange(x, y, z + temp + 2, 0);
                        world.notifyBlockChange(x, y - 1, z + temp, 0);
                        world.notifyBlockChange(x, y - 1, z + temp + 1, 0);
                        world.notifyBlockChange(x, y - 1, z + temp + 2, 0);
                        world.notifyBlockChange(x, y - 2, z + temp + 1, 0);
                        teleportPlayersAndMakeFirstPlatform(world,x,y,z);
                        cir.setReturnValue(true);
                    }



                    for(temp = -2; temp <= 0; ++temp) {
                        if (
                                isBloodWitherBodyBlock(world, x + temp, y - 1, z)
                                && isBloodWitherBodyBlock(world, x + temp + 1, y - 1, z)
                                && isBloodWitherBodyBlock(world, x + temp + 1, y - 2, z)
                                && isBloodWitherBodyBlock(world, x + temp + 2, y - 1, z)
                                && isWitherHeadBlock(world, x + temp, y, z)
                                && isWitherHeadBlock(world, x + temp + 1, y, z)
                                && isWitherHeadBlock(world, x + temp + 2, y, z)) {

                            world.SetBlockMetadataWithNotify(x + temp, y, z, 8, 2);
                            world.SetBlockMetadataWithNotify(x + temp + 1, y, z, 8, 2);
                            world.SetBlockMetadataWithNotify(x + temp + 2, y, z, 8, 2);
                            world.setBlock(x + temp, y, z, 0, 0, 2);
                            world.setBlock(x + temp + 1, y, z, 0, 0, 2);
                            world.setBlock(x + temp + 2, y, z, 0, 0, 2);
                            world.setBlock(x + temp, y - 1, z, 0, 0, 2);
                            world.setBlock(x + temp + 1, y - 1, z, 0, 0, 2);
                            world.setBlock(x + temp + 2, y - 1, z, 0, 0, 2);
                            world.setBlock(x + temp + 1, y - 2, z, 0, 0, 2);
                            EntityBloodWither.summonWitherAtLocation(world, x + temp + 1, z);
                            world.notifyBlockChange(x + temp, y, z, 0);
                            world.notifyBlockChange(x + temp + 1, y, z, 0);
                            world.notifyBlockChange(x + temp + 2, y, z, 0);
                            world.notifyBlockChange(x + temp, y - 1, z, 0);
                            world.notifyBlockChange(x + temp + 1, y - 1, z, 0);
                            world.notifyBlockChange(x + temp + 2, y - 1, z, 0);
                            world.notifyBlockChange(x + temp + 1, y - 2, z, 0);
                            teleportPlayersAndMakeFirstPlatform(world,x,y,z);
                            cir.setReturnValue(true);
                        }
                    }
                }
            }



            // === AWAKENED WITHER ===
            // === AWAKENED WITHER ===
            // === AWAKENED WITHER ===

            if ((world.provider.dimensionId == NMFields.UNDERWORLD_DIMENSION && world.getBiomeGenForCoords(x,z) instanceof BiomeGenShadowRealm) || NightmareMode.devMode) {
                for(int temp = -2; temp <= 0; ++temp) {
                    if (
                            isAwakenedWitherBodyBlock(world, x, y - 1, z + temp)
                            && isAwakenedWitherBodyBlock(world, x, y - 1, z + temp + 1)
                            && isAwakenedWitherBodyBlock(world, x, y - 2, z + temp + 1)
                            && isAwakenedWitherBodyBlock(world, x, y - 1, z + temp + 2)
                            && isWitherHeadBlock(world, x, y, z + temp)
                            && isWitherHeadBlock(world, x, y, z + temp + 1)
                            && isWitherHeadBlock(world, x, y, z + temp + 2)) {

                        world.SetBlockMetadataWithNotify(x, y, z + temp, 8, 2);
                        world.SetBlockMetadataWithNotify(x, y, z + temp + 1, 8, 2);
                        world.SetBlockMetadataWithNotify(x, y, z + temp + 2, 8, 2);
                        world.setBlock(x, y, z + temp, 0, 0, 2);
                        world.setBlock(x, y, z + temp + 1, 0, 0, 2);
                        world.setBlock(x, y, z + temp + 2, 0, 0, 2);
                        world.setBlock(x, y - 1, z + temp, 0, 0, 2);
                        world.setBlock(x, y - 1, z + temp + 1, 0, 0, 2);
                        world.setBlock(x, y - 1, z + temp + 2, 0, 0, 2);
                        world.setBlock(x, y - 2, z + temp + 1, 0, 0, 2);
                        EntityAwakenedWither.summonWitherAtLocation(world, x,y, z + temp + 1);
                        world.notifyBlockChange(x, y, z + temp, 0);
                        world.notifyBlockChange(x, y, z + temp + 1, 0);
                        world.notifyBlockChange(x, y, z + temp + 2, 0);
                        world.notifyBlockChange(x, y - 1, z + temp, 0);
                        world.notifyBlockChange(x, y - 1, z + temp + 1, 0);
                        world.notifyBlockChange(x, y - 1, z + temp + 2, 0);
                        world.notifyBlockChange(x, y - 2, z + temp + 1, 0);
                        cir.setReturnValue(true);
                    }
                }

                for(int iTempIOffset = -2; iTempIOffset <= 0; ++iTempIOffset) {
                    if (isAwakenedWitherBodyBlock(world, x + iTempIOffset, y - 1, z) && isAwakenedWitherBodyBlock(world, x + iTempIOffset + 1, y - 1, z) && isAwakenedWitherBodyBlock(world, x + iTempIOffset + 1, y - 2, z) && isAwakenedWitherBodyBlock(world, x + iTempIOffset + 2, y - 1, z) && isWitherHeadBlock(world, x + iTempIOffset, y, z) && isWitherHeadBlock(world, x + iTempIOffset + 1, y, z) && isWitherHeadBlock(world, x + iTempIOffset + 2, y, z)) {
                        world.SetBlockMetadataWithNotify(x + iTempIOffset, y, z, 8, 2);
                        world.SetBlockMetadataWithNotify(x + iTempIOffset + 1, y, z, 8, 2);
                        world.SetBlockMetadataWithNotify(x + iTempIOffset + 2, y, z, 8, 2);
                        world.setBlock(x + iTempIOffset, y, z, 0, 0, 2);
                        world.setBlock(x + iTempIOffset + 1, y, z, 0, 0, 2);
                        world.setBlock(x + iTempIOffset + 2, y, z, 0, 0, 2);
                        world.setBlock(x + iTempIOffset, y - 1, z, 0, 0, 2);
                        world.setBlock(x + iTempIOffset + 1, y - 1, z, 0, 0, 2);
                        world.setBlock(x + iTempIOffset + 2, y - 1, z, 0, 0, 2);
                        world.setBlock(x + iTempIOffset + 1, y - 2, z, 0, 0, 2);
                        EntityAwakenedWither.summonWitherAtLocation(world, x + iTempIOffset + 1,y, z);
                        world.notifyBlockChange(x + iTempIOffset, y, z, 0);
                        world.notifyBlockChange(x + iTempIOffset + 1, y, z, 0);
                        world.notifyBlockChange(x + iTempIOffset + 2, y, z, 0);
                        world.notifyBlockChange(x + iTempIOffset, y - 1, z, 0);
                        world.notifyBlockChange(x + iTempIOffset + 1, y - 1, z, 0);
                        world.notifyBlockChange(x + iTempIOffset + 2, y - 1, z, 0);
                        world.notifyBlockChange(x + iTempIOffset + 1, y - 2, z, 0);
                        cir.setReturnValue(true);
                    }
                }
            }
        }
    }
    @Unique
    private static boolean isBloodWitherBodyBlock(World world, int i, int j, int k) {
        return world.getBlockId(i, j, k) == NMBlocks.bloodBones.blockID;
    }
    @Unique
    private static boolean isAwakenedWitherBodyBlock(World world, int i, int j, int k) {
        return world.getBlockId(i, j, k) == NMBlocks.underStones.blockID;
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
