package com.itlesports.nightmaremode.block.tileEntities;

import api.item.util.ItemUtils;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.block.blocks.BlockOreNode;
import com.itlesports.nightmaremode.agriculture.ChunkPollutionManager;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Block;
import net.minecraft.src.Facing;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;

public class MinerDrillTileEntity extends TileEntity implements IInventory {
    private ItemStack fuelStack;
    private int fuelTicks;
    private int processingTicks;
    private int machineTier = 1;
    private boolean blockedByMaterial;

    public MinerDrillTileEntity() {}

    public MinerDrillTileEntity(int machineTier) {
        this.machineTier = Math.max(1, machineTier);
    }

    @Override
    public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }

        int[] target = this.findNodeTarget();
        if (target == null) {
            this.processingTicks = 0;
            this.blockedByMaterial = false;
            this.setActive(false);
            return;
        }

        BlockOreNode node = (BlockOreNode)net.minecraft.src.Block.blocksList[this.worldObj.getBlockId(target[0], target[1], target[2])];
        if (node.getRequiredDrillTier() > this.machineTier) {
            this.processingTicks = 0;
            this.setActive(false);
            if (!this.blockedByMaterial) {
                this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D,
                        "random.break", 0.7F, 0.75F);
            }
            this.blockedByMaterial = true;
            return;
        }
        this.blockedByMaterial = false;

        if (this.machineTier < 4 && this.fuelTicks <= 0 && !this.consumeCoal()) {
            this.processingTicks = 0;
            this.setActive(false);
            return;
        }

        this.setActive(true);
        if (this.machineTier < 4) --this.fuelTicks;
        if (++this.processingTicks < this.getProcessingTicksPerItem()) {
            return;
        }

        this.processingTicks = 0;
        ItemStack output = node.mineNodeByMachine(this.worldObj, target[0], target[1], target[2]);
        if (output != null) {
            if (node == NMBlocks.mercuryOreNode) output.stackSize = 1;
            else if (this.machineTier >= 4) output.stackSize *= 3;
            int facing = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) & 7;
            ItemUtils.ejectStackFromBlockTowardsFacing(this.worldObj, this.xCoord, this.yCoord, this.zCoord,
                    output, Block.getOppositeFacing(facing));
        }
        this.onInventoryChanged();
    }

    protected int getBaseProcessingTicks() {
        return this.machineTier >= 4 ? 40 : 200;
    }

    protected int getBaseFuelTicksPerCoal() {
        return 1600;
    }

    public int getProcessingTicksPerItem() {
        return this.getBaseProcessingTicks();
    }

    public int getFuelTicksPerCoal() {
        return this.getBaseFuelTicksPerCoal();
    }

    public int getProcessingProgress() {
        return this.processingTicks;
    }

    public int getFuelTicks() {
        return this.fuelTicks;
    }

    public int getMachineTier() {
        return this.machineTier;
    }

    private boolean consumeCoal() {
        if (this.fuelStack == null || this.fuelStack.itemID != Item.coal.itemID || this.fuelStack.stackSize <= 0) {
            return false;
        }
        --this.fuelStack.stackSize;
        if (this.fuelStack.stackSize <= 0) {
            this.fuelStack = null;
        }
        this.fuelTicks = this.getFuelTicksPerCoal();
        float pollution = switch (this.machineTier) {
            case 1 -> 90.0F;
            case 2 -> 55.0F;
            case 3 -> 25.0F;
            default -> 0.0F;
        };
        ChunkPollutionManager.pollute(this.worldObj, this.xCoord, this.yCoord, this.zCoord, pollution);
        this.onInventoryChanged();
        return true;
    }

    private int[] findNodeTarget() {
        int facing = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord) & 7;
        int targetX = this.xCoord + Facing.offsetsXForSide[facing];
        int targetY = this.yCoord + Facing.offsetsYForSide[facing];
        int targetZ = this.zCoord + Facing.offsetsZForSide[facing];
        if (this.worldObj.getBlockId(targetX, targetY, targetZ) > 0
                && net.minecraft.src.Block.blocksList[this.worldObj.getBlockId(targetX, targetY, targetZ)] instanceof BlockOreNode) {
            return new int[]{targetX, targetY, targetZ};
        }
        return null;
    }

    private void setActive(boolean active) {
        int metadata = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
        int updated = active ? metadata | 8 : metadata & 7;
        if (updated != metadata) {
            this.worldObj.setBlockMetadataWithNotify(this.xCoord, this.yCoord, this.zCoord, updated, 3);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("FuelTicks", this.fuelTicks);
        tag.setInteger("ProcessingTicks", this.processingTicks);
        tag.setInteger("MachineTier", this.machineTier);
        if (this.machineTier < 4 && this.fuelStack != null) {
            NBTTagCompound itemTag = new NBTTagCompound();
            this.fuelStack.writeToNBT(itemTag);
            tag.setCompoundTag("FuelStack", itemTag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.fuelTicks = tag.getInteger("FuelTicks");
        this.processingTicks = tag.getInteger("ProcessingTicks");
        this.machineTier = tag.hasKey("MachineTier") ? Math.max(1, tag.getInteger("MachineTier")) : 1;
        if (this.machineTier < 4 && tag.hasKey("FuelStack")) {
            this.fuelStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("FuelStack"));
        } else if (this.machineTier >= 4) {
            this.fuelStack = null;
            this.fuelTicks = 0;
        }
    }

    @Override public int getSizeInventory() { return this.machineTier >= 4 ? 0 : 1; }
    @Override public ItemStack getStackInSlot(int slot) { return this.machineTier >= 4 ? null : this.fuelStack; }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if (this.machineTier >= 4) return null;
        if (this.fuelStack == null) return null;
        if (this.fuelStack.stackSize <= count) {
            ItemStack result = this.fuelStack;
            this.fuelStack = null;
            return result;
        }
        ItemStack result = this.fuelStack.splitStack(count);
        if (this.fuelStack.stackSize <= 0) this.fuelStack = null;
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (this.machineTier >= 4) return null;
        ItemStack result = this.fuelStack;
        this.fuelStack = null;
        return result;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if (this.machineTier >= 4) return;
        this.fuelStack = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) stack.stackSize = this.getInventoryStackLimit();
        this.onInventoryChanged();
    }

    @Override public String getInvName() { return "container.ifhyMinerDrill"; }
    @Override public boolean isInvNameLocalized() { return true; }
    @Override public int getInventoryStackLimit() { return 64; }
    @Override public boolean isUseableByPlayer(net.minecraft.src.EntityPlayer player) {
        return this.worldObj == null || this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
                && player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }
    @Override public void openChest() {}
    @Override public void closeChest() {}
    @Override public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return this.machineTier < 4 && slot == 0 && stack != null && stack.itemID == Item.coal.itemID;
    }
}
