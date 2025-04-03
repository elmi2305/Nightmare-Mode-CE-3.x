package com.itlesports.nightmaremode;

import emi.dev.emi.emi.config.EmiConfig;
import emi.dev.emi.emi.platform.EmiAgnos;
import emi.dev.emi.emi.runtime.EmiLog;

import java.io.File;
import java.io.FileWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class NMConfig {
//    @Comment(value="Every night is a blood moon.")
//    @ConfigValue(value="nm.bloodmare")
//
//
//
//
//    @Target(value={ElementType.FIELD})
//    @Retention(value= RetentionPolicy.RUNTIME)
//    public static @interface Comment {
//        public String value();
//    }
//    @Target(value={ElementType.FIELD})
//    @Retention(value=RetentionPolicy.RUNTIME)
//    public static @interface ConfigValue {
//        public String value();
//    }

//    public static boolean bloodmare;
//
//
//
//
//    static {
//        bloodmare = true;
//    }

//    public static void writeConfig() {
//        try {
//            FileWriter writer = new FileWriter(NMConfig.getConfigFile());
//            writer.write(NMConfig.getSavedConfig());
//            writer.close();
//        } catch (Exception e) {
//            EmiLog.error("Error writing config");
//            e.printStackTrace();
//        }
//    }
//    private static File getConfigFile() {
//        String s = System.getProperty("emi.config");
//        if (s != null) {
//            File f = new File(s);
//            if (f.exists() && f.isFile()) {
//                return f;
//            }
//            EmiLog.error("System property 'emi.config' set to '" + s + "' but does not point to real file, using default config.");
//        }
//        return new File(EmiAgnos.getConfigDirectory().toFile(), "emi.css");
//    }
}
