package com.itlesports.nightmaremode.entity;

import com.itlesports.nightmaremode.skill.SkillHandler;
import com.itlesports.nightmaremode.skill.WorldSkillData;
import net.minecraft.src.DamageSource;
import net.minecraft.src.ChatMessageComponent;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityVillager;
import net.minecraft.src.MerchantRecipe;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public abstract class EntityNetherPostVillager extends EntityVillager {
    private String postGroup = "";
    private boolean professionCompleted;

    protected EntityNetherPostVillager(World world, int profession) {
        super(world, profession);
        this.setTradeLevel(1);
        this.setPersistent(true);
        this.isImmuneToFire = true;
    }

    public abstract int getPostTier();

    public void setPostGroup(int centerX, int centerZ) {
        this.postGroup = centerX + ":" + centerZ;
    }

    public boolean hasCompletedProfession() {
        return this.professionCompleted;
    }

    public String getPostGroup() {
        return this.postGroup;
    }

    @Override
    protected boolean isMovementBlocked() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean getCanCreatureTypeBePossessed() {
        return false;
    }

    @Override
    protected void dropFewItems(boolean killedByPlayer, int looting) {
    }

    @Override
    public void useRecipe(MerchantRecipe recipe) {
        int previousLevel = this.getCurrentTradeLevel();
        super.useRecipe(recipe);
        if (!this.worldObj.isRemote && previousLevel < 5 && this.getCurrentTradeLevel() >= 5) {
            this.professionCompleted = true;
            this.updatePostCompletion();
        }
    }

    @Override
    public boolean interact(EntityPlayer player) {
        if (this.getPostTier() == 3) {
            WorldSkillData data = SkillHandler.getWorldData(this.worldObj);
            if (!data.netherVillagerTier1Complete || !data.netherVillagerTier2Complete) {
                if (!this.worldObj.isRemote) {
                    player.sendChatToPlayer(ChatMessageComponent.createFromText(
                            "This post will not trade until the Tier 1 and Tier 2 posts are complete."));
                }
                return false;
            }
        }
        return super.interact(player);
    }

    private void updatePostCompletion() {
        int completed = 0;
        for (Object entry : this.worldObj.loadedEntityList) {
            if (entry instanceof EntityNetherPostVillager villager
                    && villager.getPostTier() == this.getPostTier()
                    && villager.hasCompletedProfession()
                    && this.postGroup.equals(villager.getPostGroup())) {
                ++completed;
            }
        }
        if (completed < 4) {
            return;
        }

        WorldSkillData data = SkillHandler.getWorldData(this.worldObj);
        boolean newlyCompleted = false;
        if (this.getPostTier() == 1) {
            newlyCompleted = !data.netherVillagerTier1Complete;
            data.netherVillagerTier1Complete = true;
        } else if (this.getPostTier() == 2) {
            newlyCompleted = !data.netherVillagerTier2Complete;
            data.netherVillagerTier2Complete = true;
        } else {
            newlyCompleted = !data.netherVillagerTier3Complete;
            data.netherVillagerTier3Complete = true;
            data.endAccessUnlocked = true;
        }
        EntityPlayer customer = this.getCustomer();
        if (newlyCompleted && customer != null) {
            customer.sendChatToPlayer(ChatMessageComponent.createFromText(
                    "Nether villager post Tier " + this.getPostTier() + " complete: 4/4 villagers."));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setString("NmNetherPostGroup", this.postGroup);
        tag.setBoolean("NmPostProfessionComplete", this.professionCompleted);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.postGroup = tag.getString("NmNetherPostGroup");
        this.professionCompleted = tag.getBoolean("NmPostProfessionComplete") || this.getCurrentTradeLevel() >= 5;
    }
}
