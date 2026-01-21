package com.itlesports.nightmaremode.block.blocks;


import btw.BTWMod;
import com.itlesports.nightmaremode.block.tileEntities.TileEntityDisenchantmentTable;
import com.itlesports.nightmaremode.nmgui.ContainerDisenchantment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

public class BlockDisenchantmentTable
        extends NMBlockContainer {
    private Icon field_94461_a;
    private Icon field_94460_b;

    public BlockDisenchantmentTable(int par1) {
        super(par1, Material.rock);
        this.initBlockBounds(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
        this.setLightOpacity(0);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public Icon getIcon(int par1, int par2) {
        return par1 == 0 ? this.field_94460_b : (par1 == 1 ? this.field_94461_a : this.blockIcon);
    }

    @Override
    public TileEntity createNewTileEntity(World par1World) {
        return new TileEntityDisenchantmentTable();
    }

    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer p, int par6, float par7, float par8, float par9) {
        if (par1World.isRemote) {
            return true;
        }
        TileEntityDisenchantmentTable freezer = this.getInventory(par1World, par2, par3, par4);
        ContainerDisenchantment container = new ContainerDisenchantment( p.inventory, freezer);
        BTWMod.serverOpenCustomInterface((EntityPlayerMP)p, container, ContainerDisenchantment.ID);

        return true;
    }

    public TileEntityDisenchantmentTable getInventory(World par1World, int par2, int par3, int par4)
    {
        Object var5 = par1World.getBlockTileEntity(par2, par3, par4);

        if (!(var5 instanceof TileEntityDisenchantmentTable))
        {
            return null;
        }
        else if (par1World.isBlockNormalCube(par2, par3 + 1, par4))
        {
            return null;
        }
        else
        {
            return (TileEntityDisenchantmentTable)var5;
        }
    }
    @Override
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
        super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLivingBase, par6ItemStack);
        if (par6ItemStack.hasDisplayName()) {
            ((TileEntityDisenchantmentTable) par1World.getBlockTileEntity(par2, par3, par4)).func_94134_a(par6ItemStack.getDisplayName());
        }
    }

    @Override
    public int getMobilityFlag() {
        return 2;
    }

    @Override
    public int getHarvestToolLevel(IBlockAccess blockAccess, int i, int j, int k) {
        return 3;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int iNeighborI, int iNeighborJ, int iNeighborK, int iSide) {
        if (iSide != 1) {
            return super.shouldSideBeRendered(blockAccess, iNeighborI, iNeighborJ, iNeighborK, iSide);
        }
        return true;
    }

    @Override
    @Environment(value = EnvType.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        super.randomDisplayTick(world, x, y, z, rand);
        this.displayMagicLetters(world, x, y, z, rand);
    }

    @Environment(value = EnvType.CLIENT)
    private void displayMagicLetters(World world, int x, int y, int z, Random rand) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityDisenchantmentTable) {
            TileEntityDisenchantmentTable enchanterEntity = (TileEntityDisenchantmentTable) tileEntity;
            if (enchanterEntity.playerNear) {
                for (int i = 0; i < 16; ++i) {
                    int targetZ;
                    int targetY;
                    int targetX = rand.nextInt(7) - 3 + x;
                    if (!BlockBookshelf.isBookshelfValidForEnchanting(world, targetX, targetY = rand.nextInt(5) - 2 + y, targetZ = rand.nextInt(7) - 3 + z))
                        continue;
                    Vec3 velocity = Vec3.createVectorHelper(targetX - x, targetY - y - 1, targetZ - z);
                    world.spawnParticle("enchantmenttable", (double) x + 0.5, (double) y + 1.5, (double) z + 0.5, velocity.xCoord, velocity.yCoord, velocity.zCoord);
                }
            }
        }
    }

    @Override
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon(this.getTextureName() + "_side");
        this.field_94461_a = par1IconRegister.registerIcon(this.getTextureName() + "_top");
        this.field_94460_b = par1IconRegister.registerIcon(this.getTextureName() + "_bottom");
    }
}