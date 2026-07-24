package com.itlesports.nightmaremode.item.items.template;

import com.itlesports.nightmaremode.util.NMFields;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Icon;
import net.minecraft.src.IconRegister;
import net.minecraft.src.ItemStack;

import java.util.List;

/**
 * A non-interactive skill reward item. Each metadata value is a distinct book,
 * while all books share this one item ID. Add a metadata value to the relevant
 * NMFields loot pool, a language entry, and a knowledgeBook_<number>.png sprite.
 */
public class ItemKnowledgeBook extends NMItem {
    private final Icon[] bookIcons = new Icon[NMFields.KNOWLEDGE_BOOK_COUNT];

    public ItemKnowledgeBook(int itemId) {
        super(itemId);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setUnlocalizedName("nmKnowledgeBook");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        for (int metadata = 0; metadata < this.bookIcons.length; ++metadata) {
            this.bookIcons[metadata] = register.registerIcon("nightmare:knowledgeBook_" + (metadata + 1));
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconFromDamage(int metadata) {
        return this.bookIcons[this.clampMetadata(metadata)];
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + (this.clampMetadata(stack.getItemDamage()) + 1);
    }

    @Override
    public int getMetadata(int damage) {
        return this.clampMetadata(damage);
    }

    @Override
    public boolean getHasSubtypes() {
        return true;
    }

    @Override
    public void getSubItems(int itemId, CreativeTabs creativeTab, List itemList) {
        for (int metadata = 0; metadata < this.bookIcons.length; ++metadata) {
            itemList.add(new ItemStack(itemId, 1, metadata));
        }
    }

    private int clampMetadata(int metadata) {
        return Math.max(0, Math.min(this.bookIcons.length - 1, metadata));
    }
}
