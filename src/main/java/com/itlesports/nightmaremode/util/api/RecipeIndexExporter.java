package com.itlesports.nightmaremode.util.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.recipe.EmiRecipeManager;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.shims.java.net.minecraft.text.OrderedText;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.src.AnvilConverterException;
import net.minecraft.src.ISaveFormat;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Item;
import net.minecraft.src.Minecraft;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.ResourceLocation;
import net.minecraft.src.SaveFormatComparator;
import com.itlesports.nightmaremode.block.NMBlocks;
import com.itlesports.nightmaremode.underworld.crafting.UnderforgeRecipe;
import com.itlesports.nightmaremode.underworld.crafting.UnderforgeRecipeManager;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** exports EMI's baked recipe graph for the repository's offline analysis tool. */
public final class RecipeIndexExporter {
    public static final String FILE_NAME = "nightmare-recipe-index.json";
    public static final String STOP_REQUEST_FILE = ".nightmare-stop-client";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final long AUTOMATION_TIMEOUT_MS = 180_000L;
    private static final Set<String> EXCLUDED_CATEGORIES = Set.of(
            "fuel", "foods", "tags", "hopper filtering", "world interaction");
    private static volatile boolean automatedExportComplete;
    private static volatile boolean automatedStopConsumed;
    private static volatile String automatedExportFailure;
    private static long automatedExportStartedAt;

    private RecipeIndexExporter() {
    }

    public static void exportDevelopmentIndex(EmiRecipeManager manager) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment() || manager == null) {
            return;
        }

        Path output = FabricLoader.getInstance().getGameDir().resolve(FILE_NAME);
        Path temporary = output.resolveSibling(FILE_NAME + ".tmp");
        try {
            JsonObject root = buildIndex(manager);
            Files.writeString(temporary, GSON.toJson(root), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, output, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, output, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("[recipe-index] wrote " + root.get("recipe_count").getAsInt()
                    + " recipes to " + output.toAbsolutePath());
            automatedExportComplete = true;
        } catch (Exception exception) {
            try {
                Files.deleteIfExists(temporary);
            } catch (IOException ignored) {
            }
            System.err.println("[recipe-index] export failed: " + exception.getMessage());
            exception.printStackTrace();
            if (isAutomatedExport()) {
                failAutomatedExport("export failed: " + exception.getMessage());
            }
        }
    }

    public static void startAutomatedExport(Minecraft minecraft) {
        if (!isAutomatedExport()) {
            return;
        }
        automatedExportStartedAt = System.currentTimeMillis();
        try {
            ISaveFormat saveFormat = minecraft.getSaveLoader();
            List<?> saves = saveFormat.getSaveList();
            SaveFormatComparator newest = saves.stream()
                    .filter(SaveFormatComparator.class::isInstance)
                    .map(SaveFormatComparator.class::cast)
                    .max(Comparator.comparingLong(SaveFormatComparator::getLastTimePlayed))
                    .orElse(null);
            if (newest == null) {
                failAutomatedExport("no development saves are available");
                return;
            }
            if (newest.requiresConversion()) {
                failAutomatedExport("newest save requires manual conversion: " + newest.getFileName());
                return;
            }
            if (!saveFormat.canLoadWorld(newest.getFileName())) {
                failAutomatedExport("newest save cannot be loaded: " + newest.getFileName());
                return;
            }

            minecraft.displayGuiScreen(null);
            if (saveFormat.isWorldGlobal(newest.getFileName())) {
                minecraft.launchIntegratedServerHostile(newest.getFileName(), newest.getDisplayName(), null);
            } else {
                minecraft.launchIntegratedServer(newest.getFileName(), newest.getDisplayName(), null);
            }
        } catch (AnvilConverterException exception) {
            failAutomatedExport("could not list saves: " + exception.getMessage());
        } catch (RuntimeException exception) {
            failAutomatedExport("could not load newest save: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    public static boolean consumeAutomatedStopRequest() {
        if (!isAutomatedExport() || automatedStopConsumed) {
            return false;
        }
        if (!automatedExportComplete && automatedExportFailure == null
                && automatedExportStartedAt > 0
                && System.currentTimeMillis() - automatedExportStartedAt >= AUTOMATION_TIMEOUT_MS) {
            failAutomatedExport("timed out waiting for EMI to bake");
        }
        if (automatedExportComplete || automatedExportFailure != null) {
            automatedStopConsumed = true;
            return true;
        }
        return false;
    }

    public static boolean consumeDevelopmentStopRequest() {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return false;
        }
        Path request = FabricLoader.getInstance().getGameDir().resolve(STOP_REQUEST_FILE);
        try {
            if (Files.deleteIfExists(request)) {
                return true;
            }
        } catch (IOException exception) {
            System.err.println("[recipe-index] could not consume shutdown request: " + exception.getMessage());
        }
        return false;
    }

    private static boolean isAutomatedExport() {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return false;
        }
        String environment = System.getenv("NIGHTMARE_RECIPE_EXPORT");
        return Boolean.getBoolean("nightmare.recipeExport")
                || "1".equals(environment)
                || "true".equalsIgnoreCase(environment);
    }

    private static void failAutomatedExport(String message) {
        automatedExportFailure = message;
        System.err.println("[recipe-index] automation failed: " + message);
    }

    static JsonObject buildIndex(EmiRecipeManager manager) {
        JsonObject root = new JsonObject();
        List<EmiRecipe> recipes = new ArrayList<>();
        for (EmiRecipe recipe : manager.getRecipes()) {
            if (shouldExport(recipe)) {
                recipes.add(recipe);
            }
        }

        List<EmiRecipeCategory> categoriesToExport = new ArrayList<>();
        for (EmiRecipeCategory category : manager.getCategories()) {
            boolean used = recipes.stream().anyMatch(recipe -> recipe.getCategory().equals(category));
            if (used) {
                categoriesToExport.add(category);
            }
        }
        root.addProperty("format", 1);
        int underforgeRecipeCount = UnderforgeRecipeManager.getRecipes().size();
        root.addProperty("recipe_count", recipes.size() + underforgeRecipeCount);
        root.addProperty("category_count", categoriesToExport.size() + (underforgeRecipeCount > 0 ? 1 : 0));

        JsonArray categories = new JsonArray();
        for (EmiRecipeCategory category : categoriesToExport) {
            JsonObject value = new JsonObject();
            value.addProperty("id", string(category.getId()));
            value.addProperty("name", category.getName().getString());
            JsonArray workstations = new JsonArray();
            for (EmiIngredient workstation : manager.getWorkstations(category)) {
                workstations.add(ingredient(workstation));
            }
            value.add("workstations", workstations);
            categories.add(value);
        }
        if (underforgeRecipeCount > 0) {
            JsonObject value = new JsonObject();
            value.addProperty("id", "nightmare:underforge");
            value.addProperty("name", "Underforge");
            JsonArray workstations = new JsonArray();
            workstations.add(ingredient(EmiStack.of(new ItemStack(NMBlocks.underforge))));
            value.add("workstations", workstations);
            categories.add(value);
        }
        root.add("categories", categories);

        JsonArray serializedRecipes = new JsonArray();
        int anonymousIndex = 0;
        for (EmiRecipe recipe : recipes) {
            JsonObject value = new JsonObject();
            ResourceLocation recipeId = recipe.getId();
            value.addProperty("id", recipeId == null ? "anonymous:" + anonymousIndex++ : recipeId.toString());
            value.addProperty("class", recipe.getClass().getName());
            value.addProperty("category", string(recipe.getCategory().getId()));
            value.add("inputs", ingredients(recipe.getInputs()));
            value.add("catalysts", ingredients(recipe.getCatalysts()));

            JsonArray outputs = new JsonArray();
            for (EmiStack output : recipe.getOutputs()) {
                outputs.add(stack(output, true));
            }
            value.add("outputs", outputs);
            value.add("details", details(recipe));

            serializedRecipes.add(value);
        }
        int underforgeIndex = 0;
        for (UnderforgeRecipe recipe : UnderforgeRecipeManager.getRecipes()) {
            JsonObject value = new JsonObject();
            value.addProperty("id", "nightmare:underforge/" + underforgeIndex++);
            value.addProperty("class", UnderforgeRecipe.class.getName());
            value.addProperty("category", "nightmare:underforge");
            JsonArray inputs = new JsonArray();
            addUnderforgeIngredient(inputs, recipe.getBase());
            addUnderforgeIngredient(inputs, recipe.getMetal());
            addUnderforgeIngredient(inputs, recipe.getFlux());
            addUnderforgeIngredient(inputs, recipe.getFuel());
            value.add("inputs", inputs);
            value.add("catalysts", new JsonArray());
            JsonArray outputs = new JsonArray();
            outputs.add(stack(EmiStack.of(recipe.getOutputTemplate()), true));
            value.add("outputs", outputs);
            JsonObject details = new JsonObject();
            details.addProperty("duration_ticks", 100);
            details.addProperty("method", "underforge");
            value.add("details", details);
            serializedRecipes.add(value);
        }
        root.add("recipes", serializedRecipes);
        return root;
    }

    private static void addUnderforgeIngredient(JsonArray inputs, ItemStack stack) {
        if (stack != null) inputs.add(ingredient(EmiStack.of(stack)));
    }

    private static JsonArray ingredients(List<EmiIngredient> ingredients) {
        JsonArray values = new JsonArray();
        for (EmiIngredient ingredient : ingredients) {
            if (!ingredient.isEmpty()) {
                values.add(ingredient(ingredient));
            }
        }
        return values;
    }

    private static JsonObject ingredient(EmiIngredient ingredient) {
        JsonObject value = new JsonObject();
        value.addProperty("amount", ingredient.getAmount());
        value.addProperty("chance", ingredient.getChance());
        JsonArray alternatives = new JsonArray();
        Map<String, EmiStack> unique = new LinkedHashMap<>();
        for (EmiStack alternative : ingredient.getEmiStacks()) {
            unique.putIfAbsent(key(alternative), alternative);
        }
        for (EmiStack alternative : unique.values()) {
            alternatives.add(stack(alternative, false));
        }
        value.add("alternatives", alternatives);
        return value;
    }

    private static JsonObject stack(EmiStack stack, boolean includeRemainder) {
        JsonObject value = new JsonObject();
        ResourceLocation id = stack.getId();
        value.addProperty("key", key(stack));
        value.addProperty("id", string(id));
        value.addProperty("name", stack.getName().getString());
        value.addProperty("amount", stack.getAmount());
        value.addProperty("chance", stack.getChance());

        ItemStack itemStack = stack.getItemStack();
        if (itemStack != null) {
            value.addProperty("item_id", itemStack.itemID);
            value.addProperty("meta", itemStack.getItemDamage());
            value.addProperty("unlocalized_name", itemStack.getUnlocalizedName());
        }
        NBTTagCompound nbt = stack.getNbt();
        if (nbt != null && !nbt.hasNoTags()) {
            value.addProperty("nbt", nbt.toString());
        }
        if (includeRemainder && stack.getRemainder() != null && !stack.getRemainder().isEmpty()) {
            value.add("remainder", stack(stack.getRemainder(), false));
        }
        return value;
    }

    private static String key(EmiStack stack) {
        ItemStack itemStack = stack.getItemStack();
        StringBuilder key = new StringBuilder(string(stack.getId()));
        if (itemStack != null) {
            key.append('@').append(itemStack.getItemDamage());
        }
        NBTTagCompound nbt = stack.getNbt();
        if (nbt != null && !nbt.hasNoTags()) {
            key.append(nbt);
        }
        return key.toString();
    }

    private static JsonObject details(EmiRecipe recipe) {
        JsonObject details = new JsonObject();
        reflect(details, recipe, "hits", "hits");
        reflect(details, recipe, "experienceCost", "experience_cost");
        reflect(details, recipe, "blockRecipe", "placed_block");
        reflect(details, recipe, "duration", "duration_ticks");
        reflect(details, recipe, "method", "method");
        reflect(details, recipe, "description", "description");
        reflect(details, recipe, "requiredFluid", "required_fluid");
        reflect(details, recipe, "resultingFluid", "resulting_fluid");
        reflect(details, recipe, "consumesFluid", "consumes_fluid");
        reflect(details, recipe, "requiredHeat", "heat");
        reflect(details, recipe, "requiredStir", "stirs");
        reflect(details, recipe, "stoked", "stoked");
        reflect(details, recipe, "rotations", "rotations");
        reflect(details, recipe, "fuelMultiplier", "fuel_multiplier");
        reflect(details, recipe, "infiniBurn", "infinite_burn");
        reflect(details, recipe, "shapeless", "shapeless");
        reflectText(details, recipe, "text", "description");
        return details;
    }

    private static boolean shouldExport(EmiRecipe recipe) {
        String category = recipe.getCategory().getName().getString().trim().toLowerCase(Locale.ROOT);
        if (EXCLUDED_CATEGORIES.contains(category)) {
            return false;
        }
        if (!"brewing".equals(category)) {
            return true;
        }
        for (EmiStack output : recipe.getOutputs()) {
            ItemStack stack = output.getItemStack();
            if (stack == null || stack.itemID != Item.potion.itemID) {
                return true;
            }
        }
        return false;
    }

    private static void reflectText(JsonObject destination, Object source, String fieldName, String outputName) {
        Class<?> type = source.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(source);
                if (value instanceof Iterable<?> lines) {
                    StringBuilder text = new StringBuilder();
                    for (Object line : lines) {
                        String content = line instanceof OrderedText ordered ? ordered.asString() : String.valueOf(line);
                        if (!content.isBlank()) {
                            if (!text.isEmpty()) {
                                text.append('\n');
                            }
                            text.append(content);
                        }
                    }
                    if (!text.isEmpty()) {
                        destination.addProperty(outputName, text.toString());
                    }
                }
                return;
            } catch (NoSuchFieldException exception) {
                type = type.getSuperclass();
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return;
            }
        }
    }

    private static void reflect(JsonObject destination, Object source, String fieldName, String outputName) {
        Class<?> type = source.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(source);
                if (value instanceof Number number) {
                    destination.addProperty(outputName, number);
                } else if (value instanceof Boolean bool) {
                    destination.addProperty(outputName, bool);
                } else if (value != null && (value instanceof String || value instanceof Enum<?>)) {
                    destination.addProperty(outputName, value.toString());
                }
                return;
            } catch (NoSuchFieldException exception) {
                type = type.getSuperclass();
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return;
            }
        }
    }

    private static String string(Object value) {
        return value == null ? "unknown" : value.toString();
    }
}
