package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.FullBlock;
import api.item.items.PickaxeItem;
import api.item.util.ItemUtils;
import api.util.MiscUtils;
import api.world.difficulty.DifficultyParam;
import btw.block.blocks.NetherrackBlock;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.items.ItemTungstenPickaxe;
import com.itlesports.nightmaremode.entity.EntityNetherFish;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.item.items.bloodItems.ItemBloodPickaxe;
import com.itlesports.nightmaremode.item.items.ItemNetherrackPickaxe;
import com.itlesports.nightmaremode.item.items.ItemSoulFlint;
import com.itlesports.nightmaremode.skill.SkillHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;


@Mixin(NetherrackBlock.class)
public class NetherrackBlockMixin extends FullBlock {
    @Unique private boolean shouldDropDust = true;
    @Unique private Icon nightmareMode$chippedIcon;
    @Unique private Icon nightmareMode$tierOneIcon;
    @Unique private Icon nightmareMode$tierTwoIcon;
    @Unique private Icon nightmareMode$deadzoneIcon;

    protected NetherrackBlockMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setHardnessOfBlock(int iBlockID, CallbackInfo ci){
        this.setHardness(6f);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        this.nightmareMode$trySpawnNetherFish(world, x, y, z, meta);
        ItemStack held = player.getCurrentEquippedItem();
        if (meta == 1) {
            if (!world.isRemote) {
                player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
            }
            return;
        }
        if (held != null && held.getItem() instanceof ItemSoulFlint) {
            if (!world.isRemote) {
                ItemUtils.ejectStackFromBlockTowardsFacing(world, x, y, z,
                        new ItemStack(BTWItems.groundNetherrack),
                        MiscUtils.convertOrientationToFlatBlockFacingReversed(player));
                held.damageItem(1, player);
                player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
            }
//
            onBlockDestroyedWithImproperTool(world,player,x,y,z,meta);
            return;
        }
        if (held != null && held.getItem() instanceof ItemNetherrackPickaxe) {
            if (!world.isRemote) {
                this.dropBlockAsItem_do(world, x, y, z, new ItemStack(Block.netherrack, 1, meta));
                player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
            }
            return;
        }
        super.harvestBlock(world, player, x, y, z, meta);
    }

    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || !(held.getItem() instanceof ItemSoulFlint)) {
            this.nightmareMode$trySpawnNetherFish(world, x, y, z, metadata);
        }
        if (!world.isRemote && metadata == 0
                && (held == null || !(held.getItem() instanceof PickaxeItem))
//                && (held == null || !(held.getItem() instanceof ItemSoulFlint))
        ) {
            world.setBlock(x, y, z, Block.netherrack.blockID, 1, 3);
            player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        }
    }

    @Override
    public int idDropped(int metadata, Random random, int fortune) {
        return metadata == 1 ? -1 : super.idDropped(metadata, random, fortune);
    }

    @Override
    public int damageDropped(int metadata) {
        return metadata >= 2 && metadata <= 3 ? metadata : super.damageDropped(metadata);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getDamageValue(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        this.nightmareMode$chippedIcon = register.registerIcon("nightmare:ifhyChippedNetherrack");
        this.nightmareMode$tierOneIcon = register.registerIcon("nightmare:ifhyToughNetherrack");
        this.nightmareMode$tierTwoIcon = register.registerIcon("nightmare:ifhyDenserNetherrack");
        this.nightmareMode$deadzoneIcon = register.registerIcon("nightmare:ifhyDeadzoneNetherrack");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        if (metadata == 1 && this.nightmareMode$chippedIcon != null) {
            return this.nightmareMode$chippedIcon;
        }
        if (metadata == 2 && this.nightmareMode$tierOneIcon != null) {
            return this.nightmareMode$tierOneIcon;
        }
        if (metadata == 3 && this.nightmareMode$tierTwoIcon != null) {
            return this.nightmareMode$tierTwoIcon;
        }
        if (metadata == 4 && this.nightmareMode$deadzoneIcon != null) {
            return this.nightmareMode$deadzoneIcon;
        }
        return super.getIcon(side, metadata);
    }

    @Override
    public int getEfficientToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
        return 4;
    }

    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
        return 4;
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int i, int j, int k) {
        int metadata = world.getBlockMetadata(i, j, k);
        if (metadata == 4) {
            return 0.0F;
        }
        ItemStack held = player.getCurrentEquippedItem();

        if (!SkillHandler.getPlayerData(player).canMineNetherrack || held == null) {
            return 0.0F;
        }

        if (held.getItem() instanceof ItemSoulFlint || held.getItem() instanceof ItemNetherrackPickaxe) {
            return this.nightmareMode$applyTierHardness(
                    player.getCurrentPlayerStrVsBlock(this, i, j, k) / this.blockHardness / 30.0F,
                    metadata);
        }
        if (held.getItem() instanceof ItemTungstenPickaxe) {
            return this.nightmareMode$applyTierHardness(
                    player.getCurrentPlayerStrVsBlock(this, i, j, k) / this.blockHardness / 10.0F,
                    metadata);
        }
        if (held.getItem() instanceof ItemBloodPickaxe) {
            float fRelativeHardness = player.getCurrentPlayerStrVsBlock(this, i, j, k) / this.blockHardness;
            int count = NMUtils.getBloodArmorWornCount(player);
            float armorMult = count > 0 ? ((float) count / 4 + 1): 1.0f;
            return this.nightmareMode$applyTierHardness(
                    fRelativeHardness / (200.0f * world.getDifficultyParameter(DifficultyParam.NoToolBlockHardnessMultiplier.class) * armorMult),
                    metadata);
        }

        return this.nightmareMode$applyTierHardness(
                super.getPlayerRelativeBlockHardness(player, world, i, j, k), metadata);
    }

    @Override
    public float getExplosionResistance(Entity entity, World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) == 4
                ? 6000000.0F
                : super.getExplosionResistance(entity, world, x, y, z);
    }

    @Unique
    private float nightmareMode$applyTierHardness(float hardness, int metadata) {
        if (metadata == 2) {
            return hardness / 2.0F;
        }
        if (metadata == 3) {
            return hardness / 4.0F;
        }
        return hardness;
    }

    @Unique
    private void nightmareMode$trySpawnNetherFish(World world, int x, int y, int z, int metadata) {
        if (world.isRemote || (metadata != 2 && metadata != 3) || world.rand.nextInt(5) != 0) {
            return;
        }
        EntityNetherFish fish = new EntityNetherFish(world);
        fish.setLocationAndAngles(x + 0.5D, y + 0.1D, z + 0.5D,
                world.rand.nextFloat() * 360.0F, 0.0F);
        world.spawnEntityInWorld(fish);
        fish.spawnExplosionParticle();
    }

    @Override
    protected void dropItemsIndividually(World world, int i, int j, int k, int iIDDropped, int iPileCount, int iDamageDropped, float fChanceOfPileDrop) {
        if (this.shouldDropDust) {
            for (int iTempCount = 0; iTempCount < iPileCount; ++iTempCount) {
                ItemStack stack = new ItemStack(BTWItems.groundNetherrack, 1, iDamageDropped);
                this.dropBlockAsItem_do(world, i, j, k, stack);
            }
        }
    }

    @Override
    public void dropItemsOnDestroyedByExplosion(World world, int i, int j, int k, Explosion explosion) {
        if (!world.isRemote && this.shouldDropDust) {
            this.dropItemsIndividually(world, i, j, k, BTWItems.groundNetherrack.itemID, 4, 0, 0.75f);
        }
    }

    @Override
    public float getExplosionResistance(Entity explosionEntity) {
        if(explosionEntity instanceof EntityTNTPrimed){
            this.shouldDropDust = true;
            return 2f;
        }
        this.shouldDropDust = false;
        return 4f;
    }
}
