package com.itlesports.nightmaremode.mixin.blocks;

import api.block.blocks.FullBlock;
import api.item.items.PickaxeItem;
import api.item.util.ItemUtils;
import api.util.MiscUtils;
import api.world.difficulty.DifficultyParam;
import btw.block.blocks.NetherrackBlock;
import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.items.ItemTungstenPickaxe;
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

    protected NetherrackBlockMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setHardnessOfBlock(int iBlockID, CallbackInfo ci){
        this.setHardness(6f);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
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
                this.dropBlockAsItem_do(world, x, y, z, new ItemStack(Block.netherrack));
                player.addStat(StatList.mineBlockStatArray[this.blockID], 1);
            }
            return;
        }
        super.harvestBlock(world, player, x, y, z, meta);
    }

    @Override
    public void onBlockDestroyedWithImproperTool(World world, EntityPlayer player, int x, int y, int z, int metadata) {
        ItemStack held = player.getCurrentEquippedItem();
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
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        this.nightmareMode$chippedIcon = register.registerIcon("nightmare:ifhyChippedNetherrack");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        return metadata == 1 && this.nightmareMode$chippedIcon != null
                ? this.nightmareMode$chippedIcon
                : super.getIcon(side, metadata);
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
        ItemStack held = player.getCurrentEquippedItem();

        if (!SkillHandler.getPlayerData(player).canMineNetherrack || held == null) {
            return 0.0F;
        }

        if (held.getItem() instanceof ItemSoulFlint || held.getItem() instanceof ItemNetherrackPickaxe) {
            return player.getCurrentPlayerStrVsBlock(this, i, j, k) / this.blockHardness / 30.0F;
        }
        if (held.getItem() instanceof ItemTungstenPickaxe) {
            return player.getCurrentPlayerStrVsBlock(this, i, j, k) / this.blockHardness / 15.0F;
        }
        if (held.getItem() instanceof ItemBloodPickaxe) {
            float fRelativeHardness = player.getCurrentPlayerStrVsBlock(this, i, j, k) / this.blockHardness;
            int count = NMUtils.getBloodArmorWornCount(player);
            float armorMult = count > 0 ? ((float) count / 4 + 1): 1.0f;
            return fRelativeHardness / (200.0f * world.getDifficultyParameter(DifficultyParam.NoToolBlockHardnessMultiplier.class) * armorMult);
        }

        return super.getPlayerRelativeBlockHardness(player, world, i, j, k);
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
