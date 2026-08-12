package com.itlesports.nightmaremode.util;

import btw.block.BTWBlocks;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NBTTagString;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public final class LibrarianStoryBook {
    private static final String SPECIAL_KEY_BASE64 = "G1YH6ohQqrksLwc/V0BatQ==";
    private static final List<List<Question>> QUESTIONS = List.of(
            Collections.emptyList(),
            List.of(
                    torches("How many torches did Steve carry after the supply was split at the start of the trip?", 20),
                    looseCobblestone("How many cobblestone blocks did it take to build the emergency shelter beside the ravine?", 31),
                    exact("How many arrows were in Steve's quiver when they left the house that morning?", Item.arrow, 42, "arrows")
            ),
            List.of(
                    exact("How many diamonds did Alex mine from the vein in the crevice?", Item.diamond, 9, "diamonds"),
                    exact("How many redstone dust did Steve get from the vein near the destroyed spawner using the Fortune II pickaxe?", Item.redstone, 34, "redstone dust"),
                    torches("How many torches did Alex have left by the time he reached the fork near the second lava pool?", 11)
            ),
            List.of(
                    looseCobblestone("How many blocks of cobblestone did it take to wall off the zombie pigmen on the bridge the first time?", 18),
                    exact("How many ender pearls were in the fortress chest?", Item.enderPearl, 4, "ender pearls"),
                    looseCobblestone("How many cobblestone blocks did Steve have left after bridging the collapsed walkway over the lava chasm?", 5)
            )
    );

    private LibrarianStoryBook() {}

    public static ItemStack create(int story, int question, String questToken) {
        String text;
        try {
            text = decryptResource("/story/story" + story + ".txt");

        } catch (Exception exception) {
            text = "This volume could not be read. Return it to its librarian.";
        }
        ItemStack book = new ItemStack(Item.writtenBook);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("title", "Librarian's Story " + story);
        tag.setString("author", "Village Librarian");
        tag.setString("NMQuestToken", questToken);
        tag.setBoolean("NMLibrarianStory", true);
        tag.setInteger("NMStory", story);
        tag.setInteger("NMQuestion", question);
        NBTTagList pages = new NBTTagList();
        for (String page : paginate(text)) pages.appendTag(new NBTTagString("", page));
        tag.setTag("pages", pages);
        book.setTagCompound(tag);
        return book;
    }

    public static List<Question> questionsForStory(int story) {
        if (story <= 0 || story >= QUESTIONS.size()) return Collections.emptyList();
        return QUESTIONS.get(story);
    }

    public static Question getQuestion(int story, int question) {
        List<Question> questions = questionsForStory(story);
        return question >= 0 && question < questions.size() ? questions.get(question) : null;
    }

    public static String decryptResource(String path) throws Exception {
        try (InputStream input = LibrarianStoryBook.class.getResourceAsStream(path)) {
            if (input == null) throw new IllegalStateException("missing story resource " + path);
            byte[] packed = Base64.getMimeDecoder().decode(new String(input.readAllBytes(), StandardCharsets.UTF_8));
            if (packed.length < 32) throw new IllegalArgumentException("invalid story resource");
            byte[] iv = new byte[16];
            byte[] ciphertext = new byte[packed.length - iv.length];
            System.arraycopy(packed, 0, iv, 0, iv.length);
            System.arraycopy(packed, iv.length, ciphertext, 0, ciphertext.length);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key(), new IvParameterSpec(iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        }
    }

    public static String encryptForResource(String text) throws Exception {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key(), new IvParameterSpec(iv));
        byte[] ciphertext = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        byte[] packed = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, packed, 0, iv.length);
        System.arraycopy(ciphertext, 0, packed, iv.length, ciphertext.length);
        return Base64.getEncoder().encodeToString(packed);
    }

    private static SecretKeySpec key() {
        byte[] decoded = Base64.getDecoder().decode(SPECIAL_KEY_BASE64);
        if (decoded.length != 16 && decoded.length != 24 && decoded.length != 32) {
            throw new IllegalStateException("Invalid AES key length: " + decoded.length);
        }
        return new SecretKeySpec(decoded, "AES");
    }

    private static List<String> paginate(String text) {
        List<String> pages = new ArrayList<>();
        String remaining = text == null ? "" : text.trim();
        while (!remaining.isEmpty()) {
            int end = Math.min(240, remaining.length());
            if (end < remaining.length()) {
                int breakAt = remaining.lastIndexOf(' ', end);
                if (breakAt > 120) end = breakAt;
            }
            pages.add(remaining.substring(0, end).trim());
            remaining = remaining.substring(end).trim();
        }
        if (pages.isEmpty()) pages.add("");
        return pages;
    }

    private static Question exact(String prompt, Item item, int amount, String answerName) {
        return new Question(prompt, item.itemID, -1, amount, AnswerKind.EXACT_ITEM, answerName);
    }

    private static Question torches(String prompt, int amount) {
        return new Question(prompt, -1, -1, amount, AnswerKind.TORCH, "torches");
    }

    private static Question looseCobblestone(String prompt, int amount) {
        return new Question(prompt, BTWBlocks.looseCobblestone.blockID, -1, amount,
                AnswerKind.LOOSE_COBBLESTONE, "loose cobblestone");
    }

    public enum AnswerKind {
        EXACT_ITEM,
        TORCH,
        LOOSE_COBBLESTONE
    }

    public record Question(String prompt, int answerItemId, int answerMetadata, int answerAmount,
                           AnswerKind answerKind, String answerName) {
        public String requestText() {
            return this.prompt + " Return your answer as exactly that many " + this.answerName + ".";
        }

        public boolean matches(ItemStack stack) {
            if (stack == null || stack.stackSize != this.answerAmount) return false;
            return switch (this.answerKind) {
                case TORCH -> stack.itemID == BTWBlocks.finiteUnlitTorch.blockID
                        || stack.itemID == BTWBlocks.finiteBurningTorch.blockID
                        || stack.itemID == BTWBlocks.infiniteUnlitTorch.blockID
                        || stack.itemID == BTWBlocks.infiniteBurningTorch.blockID;
                case LOOSE_COBBLESTONE -> stack.itemID == BTWBlocks.looseCobblestone.blockID;
                case EXACT_ITEM -> stack.itemID == this.answerItemId
                        && (this.answerMetadata < 0 || stack.getItemDamage() == this.answerMetadata);
            };
        }
    }
}
