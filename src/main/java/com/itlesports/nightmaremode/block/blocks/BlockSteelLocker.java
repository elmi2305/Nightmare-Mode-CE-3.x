package com.itlesports.nightmaremode.block.blocks;

import btw.block.BTWBlocks;
import com.itlesports.nightmaremode.block.tileEntities.TileEntitySteelLocker;
import com.itlesports.nightmaremode.network.SteelLockerNet;
import com.itlesports.nightmaremode.rendering.ContainerSteelLocker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Random;

public class BlockSteelLocker extends BlockContainer {
    private final Random random = new Random();
    public static final int CUSTOM_WINDOW_TYPE = 40;
    public final int chestType;

    public BlockSteelLocker(int id, int type) {
        super(id, BTWBlocks.soulforgedSteelMaterial);
        this.chestType = type;
        setCreativeTab(CreativeTabs.tabDecorations);
        setHardness(50f);
        setResistance(2000f);
        setAxesEffectiveOn();
        setBuoyant();
        initBlockBounds(0.0625F,0,0.0625F,0.9375F,0.875F,0.9375F);
        setStepSound(soundMetalFootstep);
        setUnlocalizedName("nmSteelLocker");
    }

    @Override public boolean isOpaqueCube(){ return false; }
    @Override public boolean renderAsNormalBlock(){ return false; }
    @Override public int getRenderType(){ return 22; }

    @Override
    public void onBlockPlacedBy(World world,int x,int y,int z,EntityLivingBase placer,ItemStack stack){
        int north = world.getBlockId(x,y,z-1);
        int south = world.getBlockId(x,y,z+1);
        int west  = world.getBlockId(x-1,y,z);
        int east  = world.getBlockId(x+1,y,z);

        byte facing;
        int rot = MathHelper.floor_double(placer.rotationYaw * 4.0D / 360.0D + 0.5D) & 3;
        if (rot==0) facing=2;
        else if (rot==1) facing=5;
        else if (rot==2) facing=3;
        else facing=4;

        if (north != blockID && south != blockID && west != blockID && east != blockID){
            world.setBlockMetadataWithNotify(x,y,z,facing,3);
        } else {
            if ((north==blockID || south==blockID) && (facing==4 || facing==5)){
                if (north==blockID) world.setBlockMetadataWithNotify(x,y,z-1,facing,3);
                else world.setBlockMetadataWithNotify(x,y,z+1,facing,3);
                world.setBlockMetadataWithNotify(x,y,z,facing,3);
            }
            if ((west==blockID || east==blockID) && (facing==2 || facing==3)){
                if (west==blockID) world.setBlockMetadataWithNotify(x-1,y,z,facing,3);
                else world.setBlockMetadataWithNotify(x+1,y,z,facing,3);
                world.setBlockMetadataWithNotify(x,y,z,facing,3);
            }
        }

        if (stack.hasDisplayName()){
            TileEntity te = world.getBlockTileEntity(x,y,z);
            if (te instanceof TileEntitySteelLocker chest){
                chest.setChestGuiName(stack.getDisplayName());
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world,int x,int y,int z,EntityPlayer player,int side,float hitX,float hitY,float hitZ){
        TileEntity te = world.getBlockTileEntity(x,y,z);
        if (!(te instanceof TileEntitySteelLocker chest)) return false;
        if (world.isRemote) return true;

        EntityPlayerMP mp = (EntityPlayerMP)player;
        int windowId = mp.incrementAndGetWindowID();

        ContainerSteelLocker container = new ContainerSteelLocker(mp.inventory, chest);
        container.windowId = windowId;

        mp.playerNetServerHandler.sendPacketToPlayer(
                new Packet100OpenWindow(
                        windowId,
                        CUSTOM_WINDOW_TYPE,
                        chest.getInvName(),
                        chest.getSizeInventory(),
                        chest.isInvNameLocalized()
                )
        );

        sendCoordSupplement(mp, windowId, x, y, z);

        chest.openChest();
        mp.openContainer = container;
        container.onCraftGuiOpened(mp);
        return true;
    }

    private void sendCoordSupplement(EntityPlayerMP mp, int windowId, int x, int y, int z){
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(bos)){
            dos.writeByte(1);
            dos.writeInt(windowId);
            dos.writeInt(x);
            dos.writeInt(y);
            dos.writeInt(z);
            Packet250CustomPayload pkt =
                    new Packet250CustomPayload(SteelLockerNet.CHANNEL, bos.toByteArray());
            mp.playerNetServerHandler.sendPacketToPlayer(pkt);
        } catch (Exception ignored){}
    }

    @Override
    public void breakBlock(World world,int x,int y,int z,int p5,int p6){
        TileEntitySteelLocker te = (TileEntitySteelLocker)world.getBlockTileEntity(x,y,z);
        if (te != null){
            for (int i=0;i<te.getSizeInventory();i++){
                ItemStack st = te.getStackInSlot(i);
                if (st != null){
                    float fx = random.nextFloat()*0.8F+0.1F;
                    float fy = random.nextFloat()*0.8F+0.1F;
                    float fz = random.nextFloat()*0.8F+0.1F;
                    while(st.stackSize>0){
                        int take = random.nextInt(21)+10;
                        if (take>st.stackSize) take = st.stackSize;
                        st.stackSize -= take;
                        ItemStack drop = new ItemStack(st.itemID,take,st.getItemDamage());
                        EntityItem ei = new EntityItem(world,x+fx,y+fy,z+fz, drop);
                        float vel = 0.05F;
                        ei.motionX = random.nextGaussian()*vel;
                        ei.motionY = random.nextGaussian()*vel+0.2F;
                        ei.motionZ = random.nextGaussian()*vel;
                        if (st.hasTagCompound()){
                            ei.getEntityItem().setTagCompound((NBTTagCompound)st.getTagCompound().copy());
                        }
                        world.spawnEntityInWorld(ei);
                    }
                }
            }
            world.func_96440_m(x,y,z,p5);
        }
        super.breakBlock(world,x,y,z,p5,p6);
    }

    public IInventory getInventory(World world,int x,int y,int z){
        TileEntity tile = world.getBlockTileEntity(x,y,z);
        if (!(tile instanceof TileEntitySteelLocker chest)) return null;
        if (world.isBlockRedstoneConductor(x,y+1,z)) return null;
        if (isOcelotBlockingChest(world,x,y,z)) return null;
        return chest;
    }

    @Override
    public TileEntity createNewTileEntity(World w){ return new TileEntitySteelLocker(); }

    private static boolean isOcelotBlockingChest(World w,int x,int y,int z){
        for (Object o: w.getEntitiesWithinAABB(EntityOcelot.class,
                AxisAlignedBB.getAABBPool().getAABB(x,y+1,z,x+1,y+2,z+1))){
            if (((EntityOcelot)o).isSitting()) return true;
        }
        return false;
    }

    @Override public boolean hasComparatorInputOverride(){ return true; }
    @Override
    public int getComparatorInputOverride(World w,int x,int y,int z,int side){
        return Container.calcRedstoneFromInventory(getInventory(w,x,y,z));
    }

    public boolean canProvidePower() {
        return chestType == 0;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess acc,int x,int y,int z,int side){
        if (!canProvidePower()) return 0;
        TileEntity te = acc.getBlockTileEntity(x,y,z);
        if (te instanceof TileEntitySteelLocker chest){
            return MathHelper.clamp_int(chest.numUsingPlayers,0,15);
        }
        return 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess acc,int x,int y,int z,int side){
        return side==1?isProvidingWeakPower(acc,x,y,z,side):0;
    }

    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks rb,int i,int j,int k){ return false; }

    @Override
    public void registerIcons(IconRegister reg){
        this.blockIcon = reg.registerIcon("lockerSteel_particle");
    }

}