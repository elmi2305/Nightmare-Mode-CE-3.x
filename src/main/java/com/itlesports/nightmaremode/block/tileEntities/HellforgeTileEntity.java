package com.itlesports.nightmaremode.block.tileEntities;

import btw.block.BTWBlocks;
import btw.block.tileentity.OvenTileEntity;
import btw.block.tileentity.TileEntityDataPacketHandler;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import btw.world.util.BlockPos;
import com.itlesports.nightmaremode.item.NMItems;
import net.minecraft.src.*;

import java.util.*;

public class HellforgeTileEntity extends TileEntityFurnace implements TileEntityDataPacketHandler {

    private static final float CHANCE_OF_FIRE_SPREAD = 0.01F;
    public static final int FUEL_LIMIT = 16000 * 8;
    private boolean lightOnNextUpdate = false;
    private ItemStack cookStack = null;
    private int unlitFuelBurnTime = 0;
    private int visualFuelLevel = 0;
    private final int brickBurnTimeMultiplier = 4;
    private final int cookTimeMultiplier = 4;
    private final int maxFuelBurnTime = 14200;
    private final int visualFuelLevelIncrement = 1600;
    private final int visualSputterFuelLevel = 400;


    public void updateEntity() {
        boolean bWasBurning = true;
        boolean bInventoryChanged = false;
        if (this.furnaceBurnTime > 0) {
            --this.furnaceBurnTime;
        }



        if (!this.worldObj.isRemote) {


            this.furnaceBurnTime += this.unlitFuelBurnTime;
            this.unlitFuelBurnTime = 0;
            this.lightOnNextUpdate = false;

            if (this.canSmelt()) {
                ++this.furnaceCookTime;
                if (this.furnaceCookTime >= this.getCookTimeForCurrentItem()) {
                    this.furnaceCookTime = 0;
                    this.smeltItem();
                    this.tryEjectToChest();
                    bInventoryChanged = true;
                }
            } else {
                this.furnaceCookTime = 0;
            }


            BlockFurnace furnaceBlock = (BlockFurnace) Block.blocksList[this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord)];
            if (bWasBurning != this.isBurning()) {
                bInventoryChanged = true;
                furnaceBlock.updateFurnaceBlockState(this.furnaceBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord, false);
            }

            this.updateCookStack();
            this.updateVisualFuelLevel();
        }

        if (bInventoryChanged) {
            this.onInventoryChanged();
        }

    }

    public String getInvName() {
        return "container.nmHellForge";
    }

    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("fcUnlitFuel")) {
            this.unlitFuelBurnTime = tag.getInteger("fcUnlitFuel");
        }

        if (tag.hasKey("fcVisualFuel")) {
            this.visualFuelLevel = tag.getByte("fcVisualFuel");
        }

    }

    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("fcUnlitFuel", this.unlitFuelBurnTime);
        tag.setByte("fcVisualFuel", (byte)this.visualFuelLevel);
    }
    public static final Map<Integer, Integer> FUEL_MAP = new HashMap<Integer, Integer>();

    static {
        FUEL_MAP.put(Block.netherrack.blockID, 4000);
        FUEL_MAP.put(BTWItems.nethercoal.itemID, 100000);
    }

    public static int getFuelValue(int id) {
        Integer value = FUEL_MAP.get(id);
        return value == null ? 0 : value;
    }
    private void tryEjectToChest() {
        ItemStack output = this.getStackInSlot(2);
        if (output == null) return;

        int[][] offsets = {
                { 1, 0, 0 },
                { -1, 0, 0 },
                { 0, 1, 0 },
                { 0, -1, 0 },
                { 0, 0, 1 },
                { 0, 0, -1 }
        };

        for (int[] off : offsets) {
            TileEntity te = this.worldObj.getBlockTileEntity(
                    this.xCoord + off[0],
                    this.yCoord + off[1],
                    this.zCoord + off[2]
            );

            if (te instanceof IInventory) {
                ItemStack stackToInsert = output.copy();
                IInventory inv = (IInventory) te;

                for (int slot = 0; slot < inv.getSizeInventory() && stackToInsert.stackSize > 0; slot++) {
                    ItemStack slotStack = inv.getStackInSlot(slot);

                    if (slotStack == null) {
                        inv.setInventorySlotContents(slot, stackToInsert);
                        stackToInsert = null;
                        break;
                    } else if (slotStack.isItemEqual(stackToInsert) &&
                            ItemStack.areItemStackTagsEqual(slotStack, stackToInsert) &&
                            slotStack.stackSize < slotStack.getMaxStackSize()) {
                        int transferable = Math.min(
                                stackToInsert.stackSize,
                                slotStack.getMaxStackSize() - slotStack.stackSize
                        );
                        slotStack.stackSize += transferable;
                        stackToInsert.stackSize -= transferable;
                    }
                }

                if (stackToInsert == null || stackToInsert.stackSize <= 0) {
                    this.setInventorySlotContents(2, null);
                } else {
                    this.setInventorySlotContents(2, stackToInsert);
                }

                inv.onInventoryChanged();
                break; // only insert into first chest found
            }
        }
    }


    public int getItemBurnTime(ItemStack stack) {
        if(stack != null && FUEL_MAP.containsKey(stack.itemID)){
            return getFuelValue(stack.itemID);
        }
        return super.getItemBurnTime(stack);
    }


    protected int getCookTimeForCurrentItem() {
        float multiplier;
        if(this.furnaceBurnTime == 0){
            multiplier = 0.2f;
        } else{
            multiplier = (float) Math.min((this.furnaceBurnTime / FUEL_LIMIT) * 8, 16);
        }
//        System.out.println("multiplier: " + multiplier);
//        System.out.println("current cook time: " + (super.getCookTimeForCurrentItem() / multiplier));
        return (int)(super.getCookTimeForCurrentItem() / multiplier);
    }

    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        if (this.cookStack != null) {
            NBTTagCompound cookTag = new NBTTagCompound();
            this.cookStack.writeToNBT(cookTag);
            tag.setCompoundTag("x", cookTag);
        }

        tag.setByte("y", (byte)this.visualFuelLevel);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    public void readNBTFromPacket(NBTTagCompound tag) {
        NBTTagCompound cookTag = tag.getCompoundTag("x");
        if (cookTag != null) {
            this.cookStack = ItemStack.loadItemStackFromNBT(cookTag);
        }

        this.visualFuelLevel = tag.getByte("y");
        this.worldObj.markBlockRangeForRenderUpdate(this.xCoord, this.yCoord, this.zCoord, this.xCoord, this.yCoord, this.zCoord);
    }

    public boolean attemptToLight() {
        if (this.unlitFuelBurnTime > 0) {
            this.lightOnNextUpdate = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean hasValidFuel() {
        return this.unlitFuelBurnTime > 0;
    }

    private void updateCookStack() {
        ItemStack newCookStack = this.furnaceItemStacks[0];
        if (newCookStack == null) {
            newCookStack = this.furnaceItemStacks[2];
            if (newCookStack == null) {
                newCookStack = this.furnaceItemStacks[1];
            }
        }

        if (!ItemStack.areItemStacksEqual(newCookStack, this.cookStack)) {
            this.setCookStack(newCookStack);
        }

    }

    public void setCookStack(ItemStack stack) {
        if (stack != null) {
            this.cookStack = stack.copy();
        } else {
            this.cookStack = null;
        }

        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public ItemStack getCookStack() {
        return this.cookStack;
    }

    public void givePlayerCookStack(EntityPlayer player, int iFacing) {
        if (!this.worldObj.isRemote) {
            this.ejectAllNotCookStacksToFacing(player, iFacing);
        }

        ItemUtils.givePlayerStackOrEjectFromTowardsFacing(player, this.cookStack, this.xCoord, this.yCoord, this.zCoord, iFacing);
        this.furnaceItemStacks[0] = null;
        this.furnaceItemStacks[1] = null;
        this.furnaceItemStacks[2] = null;
        this.setCookStack(null);
        this.onInventoryChanged();
    }

    private void ejectAllNotCookStacksToFacing(EntityPlayer player, int iFacing) {
        if (this.furnaceItemStacks[0] != null && !ItemStack.areItemStacksEqual(this.furnaceItemStacks[0], this.cookStack)) {
            ItemUtils.ejectStackFromBlockTowardsFacing(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.furnaceItemStacks[0], iFacing);
            this.furnaceItemStacks[0] = null;
        }

        if (this.furnaceItemStacks[1] != null && !ItemStack.areItemStacksEqual(this.furnaceItemStacks[1], this.cookStack)) {
            ItemUtils.ejectStackFromBlockTowardsFacing(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.furnaceItemStacks[1], iFacing);
            this.furnaceItemStacks[1] = null;
        }

        if (this.furnaceItemStacks[2] != null && !ItemStack.areItemStacksEqual(this.furnaceItemStacks[2], this.cookStack)) {
            ItemUtils.ejectStackFromBlockTowardsFacing(this.worldObj, this.xCoord, this.yCoord, this.zCoord, this.furnaceItemStacks[2], iFacing);
            this.furnaceItemStacks[2] = null;
        }

        this.onInventoryChanged();
    }

    public void addCookStack(ItemStack stack) {
        this.furnaceItemStacks[0] = stack;
        this.onInventoryChanged();
    }

    public int attemptToAddFuel(ItemStack stack) {
        int iTotalBurnTime = this.unlitFuelBurnTime + this.furnaceBurnTime;
        int iDeltaBurnTime = FUEL_LIMIT - 1400 - iTotalBurnTime;
        int iNumItemsBurned = 0;
        if (iDeltaBurnTime > 0) {
            iNumItemsBurned = iDeltaBurnTime / this.getItemBurnTime(stack);
            if (iNumItemsBurned == 0 && this.getVisualFuelLevel() <= 2) {
                iNumItemsBurned = 1;
            }

            if (iNumItemsBurned > 0) {
                if (iNumItemsBurned > stack.stackSize) {
                    iNumItemsBurned = stack.stackSize;
                }

                this.unlitFuelBurnTime += (this.getItemBurnTime(stack) * 16) * iNumItemsBurned;
                this.onInventoryChanged();
            }
        }

        return iNumItemsBurned;
    }

    private void updateVisualFuelLevel() {
        int iTotalBurnTime = this.unlitFuelBurnTime + this.furnaceBurnTime;
        int iNewFuelLevel = 0;
        if (iTotalBurnTime > 0) {
            if (iTotalBurnTime < 400) {
                iNewFuelLevel = 1;
            } else {
                iNewFuelLevel = iTotalBurnTime / 16000 + 2;
//                iNewFuelLevel = iTotalBurnTime / 1600 + 2;
            }
        }

        this.setVisualFuelLevel(iNewFuelLevel);
    }

    public int getVisualFuelLevel() {
        return this.visualFuelLevel;
    }

    public void setVisualFuelLevel(int iLevel) {
        if (this.visualFuelLevel != iLevel) {
            this.visualFuelLevel = iLevel;
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }

    }

    public static int calcRedstoneFromOven(HellforgeTileEntity tileEntity) {
        boolean hasInventory = tileEntity.getCookStack() != null || tileEntity.furnaceItemStacks[0] != null;
        return hasInventory ? 15 : 0;
    }

    @Override
    public int[] getSlotsForFace(int i) {
        return new int[0];
    }
}
