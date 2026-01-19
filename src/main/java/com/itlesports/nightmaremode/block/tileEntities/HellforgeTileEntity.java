package com.itlesports.nightmaremode.block.tileEntities;

import api.block.TileEntityDataPacketHandler;
import api.item.util.ItemUtils;
import btw.item.BTWItems;
import net.minecraft.src.*;

import java.util.*;

public class HellforgeTileEntity extends TileEntityFurnace implements TileEntityDataPacketHandler {

    public static final int FUEL_LIMIT = 132000;
    private ItemStack cookStack = null;
    private int unlitFuelBurnTime = 0;
    private int visualFuelLevel = 0;


    public void updateEntity() {
        boolean bWasBurning = true;
        boolean bInventoryChanged = false;
        if (this.furnaceBurnTime > 0) {
            --this.furnaceBurnTime;
        }



        if (!this.worldObj.isRemote) {


            this.furnaceBurnTime += this.unlitFuelBurnTime;
            this.unlitFuelBurnTime = 0;

            if (this.canSmelt()) {
                ++this.furnaceCookTime;
                if (this.furnaceCookTime >= this.getCookTimeForCurrentItem()) {
                    this.furnaceCookTime = 0;
                    this.smeltItem();
                    this.tryEjectToSideChests();
                    this.tryConsumeNetherrackBelow();
//                    this.tryPullFromTopHopper();
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
    public static final Map<Integer, Integer> FUEL_MAP = new HashMap<>();

    static {
        FUEL_MAP.put(BTWItems.groundNetherrack.itemID, 400);
        FUEL_MAP.put(Block.netherrack.blockID, 3200);
        FUEL_MAP.put(BTWItems.nethercoal.itemID, 65000);
    }

    public static int getFuelValue(int id) {
        Integer value = FUEL_MAP.get(id);
        return value == null ? 0 : value;
    }
    private int[] getForwardFromMeta(int meta) {
        return switch (meta) {
            case 2 -> new int[]{0, -1}; // north
            case 3 -> new int[]{0, 1}; // south
            case 4 -> new int[]{-1, 0}; // west
            case 5 -> new int[]{1, 0}; // east
            default -> new int[]{0, -1}; // fallback (north)
        };
    }

    private void tryEjectToSideChests() {
        ItemStack output = this.getStackInSlot(2);
        if (output == null) return;

        int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        int[] f = getForwardFromMeta(meta);
        // left = rotate forward 90deg CCW: (-dz, dx)
        int leftX = -f[1], leftZ = f[0];
        // right = rotate forward 90deg CW: (dz, -dx)
        int rightX = f[1], rightZ = -f[0];

        int[][] sides = { {leftX, leftZ}, {rightX, rightZ} };

        for (int[] s : sides) {
            int tx = this.xCoord + s[0];
            int ty = this.yCoord;
            int tz = this.zCoord + s[1];

            TileEntity te = this.worldObj.getBlockTileEntity(tx, ty, tz);
            // Only chests (not all IInventory)
            if (te instanceof TileEntityChest inv) {
                ItemStack remaining = output.copy();

                for (int slot = 0; slot < inv.getSizeInventory() && remaining.stackSize > 0; slot++) {
                    ItemStack slotStack = inv.getStackInSlot(slot);

                    if (slotStack == null) {
                        // can insert whole remaining stack
                        inv.setInventorySlotContents(slot, remaining);
                        remaining = null;
                        break;
                    } else if (slotStack.isItemEqual(remaining) &&
                            ItemStack.areItemStackTagsEqual(slotStack, remaining) &&
                            slotStack.stackSize < slotStack.getMaxStackSize()) {
                        int transferable = Math.min(
                                remaining.stackSize,
                                slotStack.getMaxStackSize() - slotStack.stackSize
                        );
                        slotStack.stackSize += transferable;
                        remaining.stackSize -= transferable;
                    }
                }

                if (remaining == null || remaining.stackSize <= 0) {
                    this.setInventorySlotContents(2, null);
                } else {
                    this.setInventorySlotContents(2, remaining);
                }

                inv.onInventoryChanged();
                this.onInventoryChanged();
                break; // only insert into the first valid chest found (left preferred)
            }
        }
    }

    private void tryConsumeNetherrackBelow() {
        TileEntity te = this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);
        if (!(te instanceof IInventory inv)) return;

        for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
            ItemStack s = inv.getStackInSlot(slot);
            if (s == null) continue;

            boolean isValidFuel = (s.itemID == Block.netherrack.blockID || s.itemID == BTWItems.nethercoal.itemID) || s.itemID == BTWItems.groundNetherrack.itemID;

            if (isValidFuel) {
                if (this.attemptToAddFuel(s) > 0) {
                    s.stackSize -= 1;
                    if (s.stackSize <= 0) inv.setInventorySlotContents(slot, null);
                    else inv.setInventorySlotContents(slot, s);
                }
                inv.onInventoryChanged();

                this.onInventoryChanged();
                break;
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
            multiplier = 0.5f;
        } else{
            multiplier = (float) Math.min((((float) this.furnaceBurnTime / FUEL_LIMIT) * 6f), 6f);
        }
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

                this.unlitFuelBurnTime += this.getItemBurnTime(stack) * iNumItemsBurned;
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
