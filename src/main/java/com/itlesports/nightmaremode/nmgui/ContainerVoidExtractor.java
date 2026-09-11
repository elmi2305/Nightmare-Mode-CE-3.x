package com.itlesports.nightmaremode.nmgui;

import btw.item.BTWItems;
import com.itlesports.nightmaremode.block.tileEntities.VoidExtractorTileEntity;
import net.minecraft.src.*;

public class ContainerVoidExtractor extends Container {
    public static final int ID=53; private final VoidExtractorTileEntity extractor; private int lastFuel=-1,lastProcess=-1;
    public ContainerVoidExtractor(InventoryPlayer playerInventory,VoidExtractorTileEntity extractor){this.extractor=extractor;addSlotToContainer(new Slot(extractor,0,44,35));addSlotToContainer(new Slot(extractor,1,80,35));addSlotToContainer(new Slot(extractor,2,116,35){@Override public boolean isItemValid(ItemStack s){return false;}});for(int r=0;r<3;r++)for(int c=0;c<9;c++)addSlotToContainer(new Slot(playerInventory,c+r*9+9,8+c*18,84+r*18));for(int c=0;c<9;c++)addSlotToContainer(new Slot(playerInventory,c,8+c*18,142));}
    @Override public void onCraftGuiOpened(ICrafting c){super.onCraftGuiOpened(c);c.sendProgressBarUpdate(this,0,extractor.getFuelTicks());c.sendProgressBarUpdate(this,1,extractor.getProcessTicks());}
    @Override public void detectAndSendChanges(){super.detectAndSendChanges();int f=extractor.getFuelTicks(),p=extractor.getProcessTicks();for(Object o:crafters){ICrafting c=(ICrafting)o;if(f!=lastFuel)c.sendProgressBarUpdate(this,0,f);if(p!=lastProcess)c.sendProgressBarUpdate(this,1,p);}lastFuel=f;lastProcess=p;}
    @Override public void updateProgressBar(int id,int value){if(id==0)extractor.setFuelTicks(value);if(id==1)extractor.setProcessTicks(value);}
    @Override public boolean canInteractWith(EntityPlayer p){return extractor.isUseableByPlayer(p);}
    @Override public ItemStack transferStackInSlot(EntityPlayer p,int index){if(index<0||index>=inventorySlots.size())return null;Slot slot=(Slot)inventorySlots.get(index);if(slot==null||!slot.getHasStack())return null;ItemStack s=slot.getStack(),copy=s.copy();if(index<3){if(!mergeItemStack(s,3,39,true))return null;}else if(s.itemID==Item.coal.itemID){if(!mergeItemStack(s,0,1,false))return null;}else if(s.itemID==Item.ingotIron.itemID||s.itemID==BTWItems.ironOreChunk.itemID||s.itemID==BTWItems.ironNugget.itemID){if(!mergeItemStack(s,1,2,false))return null;}else return null;if(s.stackSize<=0)slot.putStack(null);else slot.onSlotChanged();return s.stackSize==copy.stackSize?null:copy;}
}
