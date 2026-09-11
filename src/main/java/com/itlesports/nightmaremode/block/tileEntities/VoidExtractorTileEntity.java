package com.itlesports.nightmaremode.block.tileEntities;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.item.NMItems;
import com.itlesports.nightmaremode.item.items.ItemLateGameMaterial;
import com.itlesports.nightmaremode.worldgen.OverworldTierHelper;
import net.minecraft.src.*;

public class VoidExtractorTileEntity extends TileEntity implements IInventory {
    public static final int PROCESS_TICKS = 600;
    private final ItemStack[] inventory = new ItemStack[3];
    private int fuelTicks, processTicks;

    @Override public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) return;
        if (this.worldObj.provider.dimensionId != 0 || OverworldTierHelper.getRegion(this.worldObj, this.xCoord, this.zCoord)
                != OverworldTierHelper.Region.GREAT_VOID || !this.canProcess()) { this.processTicks = 0; return; }
        if (this.fuelTicks <= 0 && !this.consumeFuel()) { this.processTicks = 0; return; }
        --this.fuelTicks;
        if (++this.processTicks >= PROCESS_TICKS) { this.processTicks = 0; this.process(); }
    }

    private ItemStack result() {
        ItemStack input = this.inventory[1];
        if (input == null) return null;
        if (input.itemID == Item.ingotIron.itemID) return new ItemStack(NMItems.lateGameMaterial, 1, ItemLateGameMaterial.AETHER_INGOT);
        if (input.itemID == BTWItems.ironOreChunk.itemID) return new ItemStack(NMItems.lateGameMaterial, 1, ItemLateGameMaterial.AETHER_CHUNK);
        if (input.itemID == BTWItems.ironNugget.itemID) return new ItemStack(NMItems.lateGameMaterial, 1, ItemLateGameMaterial.AETHER_NUGGET);
        return null;
    }

    private boolean canProcess() {
        ItemStack result = this.result();
        ItemStack output = this.inventory[2];
        return result != null && (output == null || output.isItemEqual(result) && output.stackSize < output.getMaxStackSize());
    }
    private void process() {
        ItemStack result = this.result();
        if (--this.inventory[1].stackSize <= 0) this.inventory[1] = null;
        if (this.inventory[2] == null) this.inventory[2] = result; else ++this.inventory[2].stackSize;
        this.onInventoryChanged();
    }
    private boolean consumeFuel() {
        ItemStack fuel = this.inventory[0];
        if (fuel == null || fuel.itemID != Item.coal.itemID) return false;
        if (--fuel.stackSize <= 0) this.inventory[0] = null;
        this.fuelTicks = 1600; this.onInventoryChanged(); return true;
    }
    public int getFuelTicks(){return fuelTicks;} public int getProcessTicks(){return processTicks;}
    public void setFuelTicks(int value){fuelTicks=value;} public void setProcessTicks(int value){processTicks=value;}
    @Override public void writeToNBT(NBTTagCompound tag){super.writeToNBT(tag);tag.setInteger("FuelTicks",fuelTicks);tag.setInteger("ProcessTicks",processTicks);NBTTagList list=new NBTTagList();for(int i=0;i<3;i++)if(inventory[i]!=null){NBTTagCompound item=new NBTTagCompound();item.setByte("Slot",(byte)i);inventory[i].writeToNBT(item);list.appendTag(item);}tag.setTag("Items",list);}
    @Override public void readFromNBT(NBTTagCompound tag){super.readFromNBT(tag);fuelTicks=tag.getInteger("FuelTicks");processTicks=tag.getInteger("ProcessTicks");NBTTagList list=tag.getTagList("Items");for(int i=0;i<list.tagCount();i++){NBTTagCompound item=(NBTTagCompound)list.tagAt(i);int slot=item.getByte("Slot")&255;if(slot<3)inventory[slot]=ItemStack.loadItemStackFromNBT(item);}}
    @Override public int getSizeInventory(){return 3;} @Override public ItemStack getStackInSlot(int i){return inventory[i];}
    @Override public ItemStack decrStackSize(int i,int count){ItemStack s=inventory[i];if(s==null)return null;if(s.stackSize<=count){inventory[i]=null;return s;}ItemStack r=s.splitStack(count);if(s.stackSize<=0)inventory[i]=null;return r;}
    @Override public ItemStack getStackInSlotOnClosing(int i){ItemStack s=inventory[i];inventory[i]=null;return s;}
    @Override public void setInventorySlotContents(int i,ItemStack s){inventory[i]=s;if(s!=null&&s.stackSize>64)s.stackSize=64;onInventoryChanged();}
    @Override public String getInvName(){return "container.ifhyVoidExtractor";} @Override public boolean isInvNameLocalized(){return true;}
    @Override public int getInventoryStackLimit(){return 64;} @Override public boolean isUseableByPlayer(EntityPlayer p){return worldObj==null||worldObj.getBlockTileEntity(xCoord,yCoord,zCoord)==this&&p.getDistanceSq(xCoord+.5,yCoord+.5,zCoord+.5)<=64;}
    @Override public void openChest(){} @Override public void closeChest(){}
    @Override public boolean isItemValidForSlot(int slot,ItemStack stack){return slot==0&&stack!=null&&stack.itemID==Item.coal.itemID||slot==1&&stack!=null&&(stack.itemID==Item.ingotIron.itemID||stack.itemID==BTWItems.ironOreChunk.itemID||stack.itemID==BTWItems.ironNugget.itemID);}
}
