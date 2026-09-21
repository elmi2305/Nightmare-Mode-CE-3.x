package com.itlesports.nightmaremode.integration.emi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import emi.dev.emi.emi.api.recipe.EmiCraftingRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeManager;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.recipe.EmiShapedRecipe;
import emi.dev.emi.emi.recipe.EmiShapelessRecipe;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.src.Gui;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Minecraft;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.RenderItem;
import net.minecraft.src.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** separate, opt-in presentation assets; never used by the recipe query tool. */
public final class RecipeCardExporter {
    private static final int TILE = 20;
    private static final int COLUMNS = 32;
    private static volatile EmiRecipeManager pending;

    private RecipeCardExporter() {}

    public static void queue(EmiRecipeManager manager) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()
                && "1".equals(System.getenv("NIGHTMARE_RECIPE_CARDS"))) {
            pending = manager;
        }
    }

    public static void exportPending() {
        EmiRecipeManager manager = pending;
        if (manager == null) {
            return;
        }
        pending = null;
        try {
            export(manager);
        } catch (Exception exception) {
            System.err.println("[recipe-cards] export failed: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private static void export(EmiRecipeManager manager) throws Exception {
        Minecraft mc = Minecraft.getMinecraft();
        Path directory = FabricLoader.getInstance().getGameDir().resolve("recipe-cards");
        Files.createDirectories(directory);
        // a catalog is published only after all assets succeed; stale catalogs must not survive failure.
        Files.deleteIfExists(directory.resolve("catalog.json"));
        Map<String, Integer> iconIds = new LinkedHashMap<>();
        List<ItemStack> icons = new ArrayList<>();
        JsonArray items = new JsonArray();
        JsonArray recipes = new JsonArray();
        int skipped = 0;
        for (EmiRecipe recipe : manager.getRecipes()) {
            // dynamic crafting subclasses need their own layout adapters.
            if (recipe.getClass() != EmiShapedRecipe.class && recipe.getClass() != EmiShapelessRecipe.class
                    && recipe.getClass() != EmiCraftingRecipe.class) {
                if (recipe instanceof EmiCraftingRecipe) skipped++;
                continue;
            }
            if (recipe.getInputs().size() > 9 || recipe.getOutputs().isEmpty()) {
                skipped++;
                continue;
            }
            JsonObject value = new JsonObject();
            value.addProperty("id", recipe.getId() == null ? "anonymous:" + recipes.size() : recipe.getId().toString());
            value.addProperty("station", "crafting_table");
            value.addProperty("width", 3);
            value.addProperty("height", 3);
            value.addProperty("shapeless", ((EmiCraftingRecipe) recipe).shapeless);
            JsonArray slots = new JsonArray();
            boolean supported = true;
            for (int slot = 0; slot < 9; slot++) {
                EmiIngredient ingredient = slot < recipe.getInputs().size() ? recipe.getInputs().get(slot) : EmiStack.EMPTY;
                if (ingredient.isEmpty()) {
                    slots.add(JsonNull.INSTANCE);
                    continue;
                }
                JsonArray alternatives = new JsonArray();
                for (EmiStack alternative : ingredient.getEmiStacks()) {
                    int icon = remember(alternative, ingredient.getAmount(), iconIds, icons, items);
                    if (icon >= 0) alternatives.add(new com.google.gson.JsonPrimitive(icon));
                }
                if (alternatives.size() == 0) supported = false;
                slots.add(alternatives);
            }
            JsonArray outputs = new JsonArray();
            for (EmiStack output : recipe.getOutputs()) {
                int icon = remember(output, output.getAmount(), iconIds, icons, items);
                if (icon < 0) supported = false;
                else outputs.add(new com.google.gson.JsonPrimitive(icon));
            }
            if (!supported) {
                skipped++;
                continue;
            }
            value.add("slots", slots);
            value.add("outputs", outputs);
            recipes.add(value);
        }
        BufferedImage atlas = renderIcons(mc, icons);
        if (!ImageIO.write(atlas, "png", directory.resolve("items.png").toFile())) {
            throw new IllegalStateException("PNG encoder unavailable");
        }
        try (InputStream font = mc.getResourceManager().getResource(
                new ResourceLocation("textures/font/ascii.png")).getInputStream()) {
            Files.copy(font, directory.resolve("font.png"), StandardCopyOption.REPLACE_EXISTING);
        }
        JsonObject root = new JsonObject();
        root.addProperty("format", 1);
        root.addProperty("tile_size", TILE);
        root.addProperty("atlas_columns", COLUMNS);
        root.addProperty("skipped_dynamic_or_unsupported", skipped);
        root.add("items", items);
        root.add("recipes", recipes);
        Path temporary = directory.resolve("catalog.json.tmp");
        Files.writeString(temporary, new Gson().toJson(root), StandardCharsets.UTF_8);
        Files.move(temporary, directory.resolve("catalog.json"), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("[recipe-cards] wrote " + recipes.size() + " crafting recipes and "
                + icons.size() + " icons; skipped " + skipped + " dynamic/unsupported recipes");
    }

    private static int remember(EmiStack stack, long amount, Map<String, Integer> ids,
                                List<ItemStack> icons, JsonArray items) {
        ItemStack item = stack.getItemStack();
        if (item == null || amount < 1 || amount > Integer.MAX_VALUE) return -1;
        String key = item.itemID + "@" + item.getItemDamage() + ":" + amount + ":" + item.stackTagCompound;
        Integer known = ids.get(key);
        if (known != null) return known;
        int index = icons.size();
        ids.put(key, index);
        ItemStack copy = item.copy();
        copy.stackSize = (int) amount;
        icons.add(copy);
        JsonObject value = new JsonObject();
        value.addProperty("name", stack.getName().getString());
        value.addProperty("item_id", item.itemID);
        value.addProperty("meta", item.getItemDamage());
        value.addProperty("amount", amount);
        items.add(value);
        return index;
    }

    private static BufferedImage renderIcons(Minecraft mc, List<ItemStack> icons) {
        BufferedImage atlas = new BufferedImage(COLUMNS * TILE,
                Math.max(1, (icons.size() + COLUMNS - 1) / COLUMNS) * TILE, BufferedImage.TYPE_INT_RGB);
        ByteBuffer pixels = BufferUtils.createByteBuffer(TILE * TILE * 4);
        RenderItem renderer = new RenderItem();
        int matrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushClientAttrib(GL11.GL_CLIENT_PIXEL_STORE_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0, TILE, TILE, 0, 1000, 3000);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        try {
            GL11.glViewport(0, 0, TILE, TILE);
            GL11.glReadBuffer(GL11.GL_BACK);
            GL11.glDrawBuffer(GL11.GL_BACK);
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glPixelStorei(GL11.GL_PACK_ROW_LENGTH, 0);
            GL11.glPixelStorei(GL11.GL_PACK_SKIP_ROWS, 0);
            GL11.glPixelStorei(GL11.GL_PACK_SKIP_PIXELS, 0);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glColorMask(true, true, true, true);
            for (int index = 0; index < icons.size(); index++) {
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();
                GL11.glTranslatef(0, 0, -2000);
                GL11.glDepthMask(true);
                GL11.glClearColor(139 / 255f, 139 / 255f, 139 / 255f, 1);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                GL11.glColor4f(1, 1, 1, 1);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                RenderHelper.enableGUIStandardItemLighting();
                ItemStack item = icons.get(index);
                renderer.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.renderEngine, item, 2, 2);
                RenderHelper.disableStandardItemLighting();
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                if (item.stackSize != 1) {
                    String count = Integer.toString(item.stackSize);
                    mc.fontRenderer.drawStringWithShadow(count, 19 - mc.fontRenderer.getStringWidth(count), 11, 0xffffff);
                }
                Gui.drawRect(0, 0, TILE, 1, 0xff373737);
                Gui.drawRect(0, 1, 1, TILE, 0xff373737);
                Gui.drawRect(1, TILE - 1, TILE, TILE, 0xffffffff);
                Gui.drawRect(TILE - 1, 1, TILE, TILE, 0xffffffff);
                pixels.clear();
                GL11.glReadPixels(0, 0, TILE, TILE, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
                for (int y = 0; y < TILE; y++) {
                    for (int x = 0; x < TILE; x++) {
                        int offset = (y * TILE + x) * 4;
                        atlas.setRGB(index % COLUMNS * TILE + x, index / COLUMNS * TILE + TILE - 1 - y,
                                (pixels.get(offset) & 255) << 16 | (pixels.get(offset + 1) & 255) << 8
                                        | pixels.get(offset + 2) & 255);
                    }
                }
            }
        } finally {
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glMatrixMode(matrixMode);
            GL11.glPopClientAttrib();
            GL11.glPopAttrib();
        }
        return atlas;
    }
}
