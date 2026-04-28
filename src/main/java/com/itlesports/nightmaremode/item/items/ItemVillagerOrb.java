package com.itlesports.nightmaremode.item.items;

import com.itlesports.nightmaremode.util.NMUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.List;

import static net.minecraft.src.EntityVillager.*;

public class ItemVillagerOrb extends Item {

    private Icon[] professionIcons;

    public ItemVillagerOrb(int itemId) {
        super(itemId);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setTextureName("nightmare:villager_orb");
        this.setHasSubtypes(true);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Icon getIconFromDamage(int damage) {
        int profession = NMUtils.VillagerMetaCodec.getProfession(damage);
        profession = MathHelper.clamp_int(profession, 0, professionIcons.length - 1);
        return professionIcons[profession];
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        professionIcons = new Icon[6];
        for (int i = 0; i < 6; i++) {
            String prof = getTextureForProfession(i);
            professionIcons[i] = par1IconRegister.registerIcon("nightmare:villagerOrb_" + prof);
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return stack; // Only spawn on server side
        }

        // Get metadata from item
        int meta = stack.getItemDamage();
        int profession = NMUtils.VillagerMetaCodec.getProfession(meta);
        int level = NMUtils.VillagerMetaCodec.getLevel(meta);

        // Raycast to find where player is looking
        MovingObjectPosition hit = this.getMovingObjectPositionFromPlayer(world, player, false);

        double spawnX, spawnY, spawnZ;

        if (hit != null && hit.typeOfHit == EnumMovingObjectType.TILE) {
            // Player clicked on a block - spawn on top of it
            int x = hit.blockX;
            int y = hit.blockY;
            int z = hit.blockZ;

            // Get the face that was clicked
            int face = hit.sideHit;

            // Offset by the face direction
            x += Facing.offsetsXForSide[face];
            y += Facing.offsetsYForSide[face];
            z += Facing.offsetsZForSide[face];

            spawnX = x + 0.5;
            spawnY = y;
            spawnZ = z + 0.5;
        } else {
            // No block hit - spawn in front of player
            Vec3 look = player.getLookVec();
            spawnX = player.posX + look.xCoord * 2.0;
            spawnY = player.posY;
            spawnZ = player.posZ + look.zCoord * 2.0;
        }

        // Create and configure the villager
        EntityVillager villager = EntityVillager.createVillagerFromProfession(world, profession);

        if (villager != null) {
            villager.setTradeLevel(level);
            villager.setLocationAndAngles(spawnX, spawnY, spawnZ, player.rotationYaw + 180.0F, 0.0F);
            villager.onSpawnWithEgg((EntityLivingData)null);

            world.spawnEntityInWorld(villager);

            // Play spawn sound
            world.playSoundAtEntity(villager, "mob.villager.default", 1.0F, 1.0F);

            // Decrease stack size
            stack.stackSize--;

            return stack;
        }

        return stack;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        int meta = stack.getItemDamage();
        int profession = NMUtils.VillagerMetaCodec.getProfession(meta);
        int level = NMUtils.VillagerMetaCodec.getLevel(meta);

        list.add(I18n.getString("villager.profession") + ": " +
                I18n.getString("entity.addonVillager" + getTextureForProfession(profession) + ".name"));
        list.add(I18n.getString("villager.level") + ": " + level);

        super.addInformation(stack, player, list, advanced);
    }

    private String getTextureForProfession(int profession) {
        return switch (profession) {
            case PROFESSION_ID_FARMER -> "Farmer";
            case PROFESSION_ID_LIBRARIAN -> "Librarian";
            case PROFESSION_ID_BLACKSMITH -> "Blacksmith";
            case PROFESSION_ID_BUTCHER -> "Butcher";
            case PROFESSION_ID_PRIEST -> "Priest";
            case 5 -> "Nightmare";
            default -> "Farmer";
        };
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int profession = NMUtils.VillagerMetaCodec.getProfession(stack.getItemDamage());
        return super.getUnlocalizedName() + "." + getTextureForProfession(profession).toLowerCase();
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public boolean getHasSubtypes() {
        return true;
    }

    @Override
    public void getSubItems(int itemId, CreativeTabs creativeTab, List itemList) {
        for (int profession = 0; profession < 6; profession++) {
            int meta = NMUtils.VillagerMetaCodec.packItemMeta(profession, 1);
            itemList.add(new ItemStack(itemId, 1, meta));
        }
    }
}