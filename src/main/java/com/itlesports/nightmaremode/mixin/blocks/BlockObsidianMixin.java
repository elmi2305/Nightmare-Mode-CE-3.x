package com.itlesports.nightmaremode.mixin.blocks;

import api.item.items.PickaxeItem;
import com.itlesports.nightmaremode.entity.EntityObsidianFish;
import com.itlesports.nightmaremode.item.NMItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;

@Mixin(BlockObsidian.class)
public class BlockObsidianMixin extends Block {
    @Unique private boolean shouldDropBlock = false;

    protected BlockObsidianMixin(int par1, Material par2Material) {
        super(par1, par2Material);
    }
    @Inject(method = "<init>", at = @At("TAIL"))
    private void lowerResistance(int par1, CallbackInfo ci){
        this.setHardness(40);
        this.setResistance(64);
    }

    @Override
    public int idDropped(int meta, Random rand, int fortune) {
        return meta == 0 ? NMItems.obsidianShard.itemID : Block.obsidian.blockID;
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int i, int j, int k, int iMetadata, float fChance, int iFortuneModifier) {
        if (!world.isRemote) {
            if (this.shouldDropBlock) {
                this.dropItemsIndividually(world, i, j, k, Block.obsidian.blockID, 1, 0, 1.0F);
                this.shouldDropBlock = false;
            } else{
                this.dropItemsIndividually(world, i, j, k, NMItems.obsidianShard.itemID, Math.min(world.rand.nextInt(4) + 3 + iFortuneModifier, 8), 0, 1.0F);
            }
        }
    }


    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        super.onBlockHarvested(world, x, y, z, meta, player);
        if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof PickaxeItem pick && pick.toolMaterial.getHarvestLevel() > 3) {
            this.shouldDropBlock = true;
        }

        if (meta == 0 && !this.shouldDropBlock) {
            if(world.rand.nextInt(8) == 0){
                EntityObsidianFish fish = new EntityObsidianFish(world);
                fish.setPositionAndUpdate(x + 0.5, y + 0.1, z + 0.5);
                fish.setAttackTarget(player);
                world.spawnEntityInWorld(fish);
            }
        }

    }




    // custom metadata for crude obsidian

    @Unique
    @Environment(EnvType.CLIENT)
    private Icon[] nm_obsidianIcons;

    @Override
    @Environment(value=EnvType.CLIENT)
    public void registerIcons(IconRegister reg) {
        nm_obsidianIcons = new Icon[4];
        nm_obsidianIcons[0] = reg.registerIcon("minecraft:obsidian");
        nm_obsidianIcons[1] = reg.registerIcon("nightmare:nmCrudeObsidian");
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
        if (meta >= 0 && meta < nm_obsidianIcons.length) {
            return (nm_obsidianIcons[meta]);
        }
        return super.getIcon(side,meta); // default obsidian icon
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void getSubBlocks(int blockID, CreativeTabs creativeTabs, List list) {
        list.add(new ItemStack(blockID, 1, 0));
        list.add(new ItemStack(blockID, 1, 1));
    }
    @Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {
        super.onBlockAdded(par1World, par2, par3, par4);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public int getDamageValue(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public int damageDropped(int iMetadata) {
        return iMetadata;
    }

}
