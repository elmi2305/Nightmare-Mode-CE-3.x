package com.itlesports.nightmaremode.block.blocks;

import com.itlesports.nightmaremode.block.blocks.templates.NMBlockContainer;
import com.itlesports.nightmaremode.util.NMUtils;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityVillagerContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;


import java.util.Random;

import static net.minecraft.src.EntityVillager.*;

public class BlockVillagerBase extends NMBlockContainer {
    public BlockVillagerBase(int par1) {
        super(par1, Material.cloth);
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
        int facing = NMUtils.VillagerMetaCodec.getFacing(metadata);
        int profession = NMUtils.VillagerMetaCodec.getProfession(metadata);

        // Clamp profession to valid range
        profession = MathHelper.clamp_int(profession, 0, iconFront.length - 1);

        if (side == 1) return iconTop[profession][0];
        if (side == 0) return iconBottom[profession][0];

        // Directional sides based on facing
        if (side == 2) return facing == 0 ? iconFront[profession][0] : iconBack[profession][0];
        if (side == 3) return facing == 2 ? iconFront[profession][0] : iconBack[profession][0];
        if (side == 4) return facing == 1 ? iconFront[profession][0] : iconSide[profession][0];
        if (side == 5) return facing == 3 ? iconFront[profession][0] : iconSide[profession][0];

        return iconSide[profession][0];
    }
    private String getTextureForProfession(int profession) {
        switch (profession) {
            case PROFESSION_ID_FARMER:
                return "farmer";
            case PROFESSION_ID_LIBRARIAN:
                return "librarian";
            case PROFESSION_ID_BLACKSMITH:
                return "blacksmith";
            case PROFESSION_ID_BUTCHER:
                return "butcher";
            case PROFESSION_ID_PRIEST:
                return "priest";
            case 5:
                return "nightmare";
            default:
                return "farmer"; // Fallback instead of null
        }
    }
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        // Calculate facing direction from player rotation
        int facing = MathHelper.floor_double((entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        // Get profession and level from item
        int itemMeta = stack.getItemDamage();
        int profession = NMUtils.VillagerMetaCodec.getProfession(itemMeta);
        int level = NMUtils.VillagerMetaCodec.getLevel(itemMeta);

        // Combine all three values into block metadata
        int blockMeta = NMUtils.VillagerMetaCodec.packMeta(profession, level, facing);
        world.setBlockMetadata(x, y, z, blockMeta);

        // Update tile entity
        TileEntityVillagerContainer te = (TileEntityVillagerContainer) world.getBlockTileEntity(x, y, z);
        if (te != null) {
            te.setProfession(profession);
            te.setLevel(level);
        }

        super.onBlockPlacedBy(world, x, y, z, entity, stack);
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int metadata, float par6, int par7) {
        // Extract profession and level, strip facing
        int profession = NMUtils.VillagerMetaCodec.getProfession(metadata);
        int level = NMUtils.VillagerMetaCodec.getLevel(metadata);
        int itemMeta = NMUtils.VillagerMetaCodec.packItemMeta(profession, level);

        ItemStack drop = new ItemStack(this.blockID, 1, itemMeta);
        this.dropBlockAsItem_do(world, x, y, z, drop);
    }

    @Override
    public int quantityDropped(Random par1Random) {
        return 1;
    }

    @Override
    public ItemStack getStackRetrievedByBlockDispenser(World world, int i, int j, int k) {
        int blockMeta = world.getBlockMetadata(i, j, k);

        // Convert block metadata to item metadata (strip facing)
        int itemMeta = NMUtils.VillagerMetaCodec.toItemMeta(blockMeta);

        return new ItemStack(this.blockID, 1, itemMeta);
    }
    public boolean dropComponentItemsOnBadBreak(World world, int i, int j, int k, int metadata, float fChanceOfDrop) {
        dropBlockAsItemWithChance(world, i, j, k, metadata, fChanceOfDrop, 0);
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
