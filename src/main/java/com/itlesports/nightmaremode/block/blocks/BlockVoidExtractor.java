package com.itlesports.nightmaremode.block.blocks;

import api.item.util.ItemUtils;
import btw.BTWMod;
import btw.block.BTWBlocks;
import btw.client.render.util.RenderUtils;
import com.itlesports.nightmaremode.block.tileEntities.VoidExtractorTileEntity;
import com.itlesports.nightmaremode.nmgui.ContainerVoidExtractor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

public class BlockVoidExtractor extends BlockContainer {
    @Environment(EnvType.CLIENT) private Icon sideIcon,topIcon,bottomIcon;
    public BlockVoidExtractor(int id){super(id,Material.iron);setHardness(8).setResistance(30);setPicksEffectiveOn();setStepSound(BTWBlocks.oreStepSound);setCreativeTab(CreativeTabs.tabRedstone);setUnlocalizedName("ifhyVoidExtractor");}
    @Override public boolean isOpaqueCube(){return false;} @Override public boolean renderAsNormalBlock(){return false;} @Override public boolean renderBlock(RenderBlocks r,int x,int y,int z){return false;}
    @Override @Environment(EnvType.CLIENT) public void registerIcons(IconRegister r){sideIcon=r.registerIcon("nightmare:ifhyExtractorAciditySide");topIcon=r.registerIcon("nightmare:ifhyExtractorMoistureTop");bottomIcon=r.registerIcon("nightmare:ifhyExtractorPorosityBottom");blockIcon=sideIcon;}
    @Override @Environment(EnvType.CLIENT) public Icon getIcon(int side,int meta){return side==0?bottomIcon:side==1?topIcon:sideIcon;}
    @Override @Environment(EnvType.CLIENT) public void renderBlockAsItem(RenderBlocks r,int m,float b){r.setRenderBounds(0,0,0,1,.75,1);RenderUtils.renderInvBlockWithMetadata(r,this,-.5F,-.5F,-.5F,m);r.setRenderBounds(0,0,0,1,1,1);}
    @Override public TileEntity createNewTileEntity(World w){return new VoidExtractorTileEntity();}
    @Override public boolean onBlockActivated(World w,int x,int y,int z,EntityPlayer p,int side,float hx,float hy,float hz){if(w.isRemote)return true;TileEntity t=w.getBlockTileEntity(x,y,z);if(t instanceof VoidExtractorTileEntity e)BTWMod.serverOpenCustomInterface((EntityPlayerMP)p,new ContainerVoidExtractor(p.inventory,e),ContainerVoidExtractor.ID);return true;}
    @Override public void breakBlock(World w,int x,int y,int z,int id,int meta){TileEntity t=w.getBlockTileEntity(x,y,z);if(!w.isRemote&&t instanceof VoidExtractorTileEntity e)for(int i=0;i<e.getSizeInventory();i++){ItemStack s=e.getStackInSlot(i);if(s!=null)ItemUtils.ejectStackFromBlockTowardsFacing(w,x,y,z,s,1);}super.breakBlock(w,x,y,z,id,meta);}
}
