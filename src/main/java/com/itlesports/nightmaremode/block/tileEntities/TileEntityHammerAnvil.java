package com.itlesports.nightmaremode.block.tileEntities;

import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;

public class TileEntityHammerAnvil extends TileEntity {
    private static final int SOUND_INTERVAL_TICKS = 2;

    protected int maxUses = -1;
    protected int usesRemaining = -1;
    private int queuedHammerSounds;
    private int soundDelay;
    private boolean breakWhenSoundsFinish;

    public TileEntityHammerAnvil() {
    }

    protected TileEntityHammerAnvil(int maxUses) {
        this.maxUses = maxUses;
        this.usesRemaining = maxUses;
    }

    @Override
    public void updateEntity() {
        if (this.worldObj == null || this.worldObj.isRemote) {
            return;
        }

        if (this.queuedHammerSounds <= 0) {
            if (this.breakWhenSoundsFinish) {
                this.breakAnvil();
            }
            return;
        }

        if (this.soundDelay > 0) {
            --this.soundDelay;
            return;
        }

        this.worldObj.playSoundEffect(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D,
                "random.anvil_use", 0.5F, this.worldObj.rand.nextFloat() * 0.25F + 1.25F);
        --this.queuedHammerSounds;
        this.soundDelay = SOUND_INTERVAL_TICKS;

        if (this.queuedHammerSounds <= 0 && this.breakWhenSoundsFinish) {
            this.breakAnvil();
        }
    }

    public boolean canSpendHits(int hits) {
        return !this.isBusy() && (this.maxUses < 0 || (!this.breakWhenSoundsFinish && this.usesRemaining >= hits));
    }

    public void spendHits(int hits) {
        if (this.maxUses >= 0) {
            this.usesRemaining = Math.max(0, this.usesRemaining - hits);
            if (this.usesRemaining <= 0) {
                this.breakWhenSoundsFinish = true;
            }
        }
        this.queueHammerSounds(hits);
        this.syncState();
    }

    public int getUsesRemaining() {
        return this.usesRemaining;
    }

    public int getMaxUses() {
        return this.maxUses;
    }

    public boolean isLimited() {
        return this.maxUses >= 0;
    }

    public boolean isBusy() {
        return this.queuedHammerSounds > 0;
    }

    public boolean isWaitingToBreak() {
        return this.breakWhenSoundsFinish && this.usesRemaining <= 0;
    }

    private void breakAnvil() {
        this.worldObj.playAuxSFX(2001, this.xCoord, this.yCoord, this.zCoord,
                this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord));
        this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
    }

    private void queueHammerSounds(int hits) {
        this.queuedHammerSounds += Math.max(1, hits);
    }

    protected void syncState() {
        if (this.worldObj != null && !this.worldObj.isRemote) {
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("MaxUses")) {
            this.maxUses = tag.getInteger("MaxUses");
        }
        if (tag.hasKey("UsesRemaining")) {
            this.usesRemaining = tag.getInteger("UsesRemaining");
        }
        this.queuedHammerSounds = tag.getInteger("QueuedHammerSounds");
        this.soundDelay = tag.getInteger("SoundDelay");
        this.breakWhenSoundsFinish = tag.getBoolean("BreakWhenSoundsFinish");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("MaxUses", this.maxUses);
        tag.setInteger("UsesRemaining", this.usesRemaining);
        tag.setInteger("QueuedHammerSounds", this.queuedHammerSounds);
        tag.setInteger("SoundDelay", this.soundDelay);
        tag.setBoolean("BreakWhenSoundsFinish", this.breakWhenSoundsFinish);
    }
}
