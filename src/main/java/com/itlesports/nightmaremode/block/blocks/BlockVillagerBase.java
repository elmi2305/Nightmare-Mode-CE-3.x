package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.NMUtils;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityVillagerContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;


import java.util.Random;

import static net.minecraft.src.EntityVillager.*;

public class BlockVillagerBase extends NMBlockContainer{
    public BlockVillagerBase(int par1) {
        super(par1, Material.cloth);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setHardness(2.0f);
        this.setAxesEffectiveOn(true);
        this.setBuoyancy(1.0f);
        this.setFireProperties(5, 20);
        this.setStepSound(soundClothFootstep);
        this.setUnlocalizedName("nmVillagerBlock");
        this.setTextureName("nightmare:nmVillagerBlock");
    }

    public boolean hasTileEntity() {
        return true;
    }


    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityVillagerContainer();
    }

    private Icon[][] iconFront, iconSide, iconBack, iconTop, iconBottom;

    @Override
    @Environment(value=EnvType.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        // Load one texture for each profession (example: 6 professions)

        int professionCount = 6; // 0–5
        iconFront = new Icon[professionCount][];
        iconSide = new Icon[professionCount][];
        iconBack = new Icon[professionCount][];
        iconTop = new Icon[professionCount][];
        iconBottom = new Icon[professionCount][];

        for (int i = 0; i < professionCount; i++) {
            String prof = getTextureForProfession(i);
            iconTop[i] = new Icon[] { iconRegister.registerIcon("nightmare:villagers/villager_" + prof + "_top") };
            iconBottom[i] = new Icon[] { iconRegister.registerIcon("nightmare:villagers/villager_" + prof + "_bottom") };
            iconFront[i] = new Icon[] { iconRegister.registerIcon("nightmare:villagers/villager_" + prof + "_front") };
            iconSide[i] = new Icon[] { iconRegister.registerIcon("nightmare:villagers/villager_" + prof + "_side") };
            iconBack[i] = new Icon[] { iconRegister.registerIcon("nightmare:villagers/villager_" + prof + "_back") };
        }
    }



    @Override
    @Environment(value=EnvType.CLIENT)
    public Icon getIcon(int side, int metadata) {
        int facing = metadata & 3;
        int profession = NMUtils.VillagerMetaCodec.getProfession(metadata);

        if (side == 1) return iconTop[profession][0];
        if (side == 0) return iconBottom[profession][0];

        profession = MathHelper.clamp_int(profession, 0, iconFront.length - 1);

        // directional sides
        if (side == 4) return facing == 1 ? iconFront[profession][0] : iconSide[profession][0];
        if (side == 5) return facing == 3 ? iconFront[profession][0] : iconSide[profession][0];
        if (side == 2) return facing == 0 ? iconFront[profession][0] : iconBack[profession][0];
        if (side == 3) return facing == 2 ? iconFront[profession][0] : iconBack[profession][0];

        return iconSide[profession][0];
    }


    private String getTextureForProfession(int profession){
        if(profession == PROFESSION_ID_FARMER){
            return "farmer";
        } else if(profession == PROFESSION_ID_LIBRARIAN){
            return "librarian";
        } else if(profession == PROFESSION_ID_BLACKSMITH){
            return "blacksmith";
        } else if(profession == PROFESSION_ID_BUTCHER){
            return "butcher";
        } else if(profession == PROFESSION_ID_PRIEST){
            return "priest";
        } else if(profession == 5){
            return "nightmare";
        }
        return null;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        TileEntityVillagerContainer te = (TileEntityVillagerContainer) world.getBlockTileEntity(x, y, z);
        if (te != null) {
            te.readFromItemMeta(stack.getItemDamage());
            world.setBlockMetadata(x,y,z, stack.getItemDamage());
        }
        super.onBlockPlacedBy(world,x,y,z,entity,stack);
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int par5, float par6, int par7) {
            int profession = NMUtils.VillagerMetaCodec.getProfession(par5);
            int level = NMUtils.VillagerMetaCodec.getLevel(par5);
            int encodedMeta = NMUtils.VillagerMetaCodec.packMeta(profession, level);

            ItemStack drop = new ItemStack(this.blockID, 1, encodedMeta);
            this.dropBlockAsItem_do(world, x, y, z, drop);
    }

    @Override
    public int quantityDropped(Random par1Random) {
        return 1;
    }

    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k) {
        int iMetadata = world.getBlockMetadata(i, j, k);
        return this.createStackedBlock(iMetadata);
    }
    public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int iMetadata, float fChanceOfDrop) {
        dropBlockAsItemWithChance(world,i,j,k,iMetadata,fChanceOfDrop, 0);
        return true;
    }


    //    @Override
//    public void breakBlock(World world, int x, int y, int z, int oldBlockId, int oldMeta) {
//        if (!world.isRemote) {
//            TileEntityVillagerContainer te = (TileEntityVillagerContainer) world.getBlockTileEntity(x, y, z);
//            if (te != null) {
//                int profession = te.getProfession();
//                int level = te.getLevel();
//                int encodedMeta = NMUtils.VillagerMetaCodec.packMeta(profession, level);
//
//                ItemStack drop = new ItemStack(this.blockID, 1, encodedMeta);
//                this.dropBlockAsItem_do(world, x, y, z, drop);
//            }
//        }
//
//        world.removeBlockTileEntity(x, y, z);
//    }

}
