package com.itlesports.nightmaremode.mixin.entity;

import btw.block.BTWBlocks;
import btw.community.nightmaremode.NightmareMode;
import btw.entity.UrnEntity;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.entity.EntityBloodWither;
import com.itlesports.nightmaremode.entity.underworld.EntityAwakenedWither;
import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.underworld.biomes.BiomeGenShadowRealm;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.WitherRitualTemplate;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(UrnEntity.class)
public class UrnEntityMixin {
    @Inject(method = "attemptToCreateGolemOrWither", at = @At("HEAD"), cancellable = true)
    private static void summonBloodFromBone(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (world.provider.dimensionId != 0 || !isBloodWitherBodyBlock(world, x, y, z)) return;
        for (int headY = y + 1; headY <= y + 2; headY++) {
            if (isWitherHeadBlock(world, x, headY, z) && trySummonBloodWither(world, x, headY, z)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Inject(method = "attemptToCreateWither", at = @At("HEAD"), cancellable = true)
    private static void checkWitherRitual(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (world.provider.dimensionId == 0 && trySummonBloodWither(world, x, y, z)) {
            cir.setReturnValue(true);
            return;
        }
        if (!canSummon(world, x, y, z)) {
            cir.setReturnValue(false);
            return;
        }
        if (world.provider.dimensionId == 0) {
            if (NightmareMode.devMode && trySummonAwakenedWither(world, x, y, z)) {
                cir.setReturnValue(true);
            } else if (!hasValidNormalWitherRitual(world, x, y, z)) {
                cir.setReturnValue(false);
            }
            return;
        }
        if ((world.provider.dimensionId == NMFields.UNDERWORLD_DIMENSION
                && world.getBiomeGenForCoords(x, z) instanceof BiomeGenShadowRealm || NightmareMode.devMode)
                && trySummonAwakenedWither(world, x, y, z)) {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private static boolean canSummon(World world, int x, int y, int z) {
        if ((!NightmareMode.allSkillsUnlocked || NightmareMode.lockDownCreative)
                && !SkillHandler.getWorldData(world).witherSummoningUnlocked) {
            EntityPlayer player = world.getClosestPlayer(x, y, z, -1);
            if (player != null) SkillHandler.sendStatus(player, "Wither summoning requires all five ritual contributions.");
            return false;
        }
        if (y < 60 || y > 200) {
            EntityPlayer player = world.getClosestPlayer(x, y, z, -1);
            if (player != null) {
                ChatMessageComponent message = new ChatMessageComponent();
                message.addKey(y < 60 ? "bosses.wither_summon_sealevel" : "bosses.wither_summon_too_high");
                message.setColor(EnumChatFormatting.YELLOW);
                player.sendChatToPlayer(message);
            }
            return false;
        }
        return true;
    }

    @Unique
    private static boolean hasValidNormalWitherRitual(World world, int x, int y, int z) {
        for (int offset = -2; offset <= 0; offset++) {
            if (matchesShape(world, x, y, z + offset + 1, false, WitherRitualTemplate.WITHER)) return true;
            if (matchesShape(world, x + offset + 1, y, z, true, WitherRitualTemplate.WITHER)) return true;
        }
        return false;
    }

    @Unique
    private static boolean trySummonBloodWither(World world, int x, int y, int z) {
        for (int offset = -2; offset <= 0; offset++) {
            int centerZ = z + offset + 1;
            if (matchesShape(world, x, y, centerZ, false, WitherRitualTemplate.BLOOD_WITHER)) {
                if (!canSummon(world, x, y, z)) return false;
                consumeShape(world, x, y, centerZ, false);
                EntityBloodWither.summonWitherAtLocation(world, x, centerZ);
                teleportPlayersAndMakeFirstPlatform(world, x, y, centerZ);
                return true;
            }
            int centerX = x + offset + 1;
            if (matchesShape(world, centerX, y, z, true, WitherRitualTemplate.BLOOD_WITHER)) {
                if (!canSummon(world, x, y, z)) return false;
                consumeShape(world, centerX, y, z, true);
                EntityBloodWither.summonWitherAtLocation(world, centerX, z);
                teleportPlayersAndMakeFirstPlatform(world, centerX, y, z);
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean trySummonAwakenedWither(World world, int x, int y, int z) {
        for (int offset = -2; offset <= 0; offset++) {
            int centerZ = z + offset + 1;
            if (matchesShape(world, x, y, centerZ, false, -1)) {
                consumeShape(world, x, y, centerZ, false);
                EntityAwakenedWither.summonWitherAtLocation(world, x, y, centerZ);
                return true;
            }
            int centerX = x + offset + 1;
            if (matchesShape(world, centerX, y, z, true, -1)) {
                consumeShape(world, centerX, y, z, true);
                EntityAwakenedWither.summonWitherAtLocation(world, centerX, y, z);
                return true;
            }
        }
        return false;
    }

    @Unique
    private static boolean matchesShape(World world, int x, int y, int z, boolean alongX, int ritual) {
        for (int lateral = -1; lateral <= 1; lateral++) {
            int px = x + (alongX ? lateral : 0);
            int pz = z + (alongX ? 0 : lateral);
            if (!isWitherHeadBlock(world, px, y, pz) || !isBodyBlock(world, px, y - 1, pz, ritual)) {
                return false;
            }
        }
        return isBodyBlock(world, x, y - 2, z, ritual)
                && (ritual == -1 || WitherRitualTemplate.matches(world, x, y, z, alongX, ritual));
    }

    @Unique
    private static boolean isBodyBlock(World world, int x, int y, int z, int ritual) {
        if (ritual == WitherRitualTemplate.WITHER) {
            return world.getBlockId(x, y, z) == BTWBlocks.aestheticOpaque.blockID
                    && world.getBlockMetadata(x, y, z) == 15;
        }
        if (ritual == WitherRitualTemplate.BLOOD_WITHER) return isBloodWitherBodyBlock(world, x, y, z);
        return world.getBlockId(x, y, z) == NMBlocks.underStones.blockID;
    }

    @Unique
    private static boolean isBloodWitherBodyBlock(World world, int x, int y, int z) {
        return world.getBlockId(x, y, z) == NMBlocks.bloodBones.blockID
                && world.getBlockMetadata(x, y, z) == 0;
    }

    @Unique
    private static boolean isWitherHeadBlock(World world, int x, int y, int z) {
        if (world.getBlockId(x, y, z) != Block.skull.blockID) return false;
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        return tileEntity instanceof TileEntitySkull && ((TileEntitySkull) tileEntity).getSkullType() == 5;
    }

    @Unique
    private static void consumeShape(World world, int x, int y, int z, boolean alongX) {
        for (int lateral = -1; lateral <= 1; lateral++) {
            int px = x + (alongX ? lateral : 0);
            int pz = z + (alongX ? 0 : lateral);
            world.SetBlockMetadataWithNotify(px, y, pz, 8, 2);
            world.setBlock(px, y, pz, 0, 0, 2);
            world.setBlock(px, y - 1, pz, 0, 0, 2);
            world.notifyBlockChange(px, y, pz, 0);
            world.notifyBlockChange(px, y - 1, pz, 0);
        }
        world.setBlock(x, y - 2, z, 0, 0, 2);
        world.notifyBlockChange(x, y - 2, z, 0);
    }

    @Unique
    private static void teleportPlayersAndMakeFirstPlatform(World world, int x, int y, int z) {
        for (Object player : world.playerEntities) {
            if (((EntityPlayer) player).getDistanceSq(x, y, z) < 625) {
                ((EntityPlayer) player).setPositionAndUpdate(x + 0.5, 201, z - 10);
            }
        }
        for (int i = x - 2; i < x + 3; i++) {
            for (int j = z - 1; j < z + 2; j++) {
                int blockID = world.rand.nextBoolean() ? NMBlocks.specialObsidian.blockID : NMBlocks.cryingObsidian.blockID;
                world.setBlock(i, 199, j, blockID);
            }
        }
        for (int i = x - 1; i < x + 2; i++) {
            for (int j = z - 12; j < z - 9; j++) {
                int blockID = world.rand.nextBoolean() ? NMBlocks.specialObsidian.blockID : NMBlocks.cryingObsidian.blockID;
                world.setBlock(i, 199, j, blockID);
            }
        }
    }
}
