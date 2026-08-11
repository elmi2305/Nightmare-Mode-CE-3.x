package com.itlesports.nightmaremode.util;

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
    public static final List<Question> QUESTIONS = Collections.emptyList();

    private LibrarianStoryBook() {}

    public static ItemStack create(int story, String questToken) {
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
        NBTTagList pages = new NBTTagList();
        for (String page : paginate(text)) pages.appendTag(new NBTTagString("", page));
        tag.setTag("pages", pages);
        book.setTagCompound(tag);
        return book;
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

    public record Question(String prompt, int answerItemId, int answerMetadata) {}
}
