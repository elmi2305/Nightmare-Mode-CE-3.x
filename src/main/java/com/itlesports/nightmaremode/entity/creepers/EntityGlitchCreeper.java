package com.itlesports.nightmaremode.entity.creepers;

import btw.community.nightmaremode.NightmareMode;
import com.itlesports.nightmaremode.util.NMFields;
import com.itlesports.nightmaremode.util.NMUtils;
import net.minecraft.src.*;

public class EntityGlitchCreeper extends EntityCreeperVariant {
    public EntityGlitchCreeper(World w) {
        super(w);
        this.variantType = NMFields.PACKET_CREEPER_GLITCH;
        this.explosionMultiplier = 0;
        this.explosionRadius = 0;
        this.fuseTime = 80;
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute((16 + NMUtils.getWorldProgress() * 2) * NMUtils.getNiteMultiplier());
    }

    @Override
    public boolean getCanSpawnHere() {
        return super.getCanSpawnHere() && NightmareMode.moreVariants && this.rand.nextInt(10) == 0;
    }

    @Override
    protected void onDeathEffect() {
        EntityPlayerMP player = null;
        if(this.getAttackTarget() instanceof EntityPlayerMP targetPlayer){
            player = targetPlayer;
        }

        if (player != null) {
            if (!NightmareMode.devMode ||true) {
                player.setDead();
            }

            player.playerNetServerHandler.kickPlayerFromServer(generateGarbageData(1024));
        }
        super.onDeathEffect();
    }

    public static String generateGarbageData(int length) {
        String[] debugWords = {
                "DEBUG", "ERROR", "NULL", "EXCEPTION",
                "STACK", "MEMORY", "PACKET", "ENTITY",
                "CRASH", "LOADING", "INVALID", "CORRUPT"
        };

        java.util.Random rand = new java.util.Random();
        StringBuilder sb = new StringBuilder(length + 64);

        while (sb.length() < length) {
            if (rand.nextInt(24) == 0) {
                String word = debugWords[rand.nextInt(debugWords.length)];
                int scramble = rand.nextInt(3);

                if (scramble == 1) {
                    char[] chars = word.toCharArray();
                    for (int i = chars.length - 1; i > 0; i--) {
                        int j = rand.nextInt(i + 1);
                        char t = chars[i];
                        chars[i] = chars[j];
                        chars[j] = t;
                    }
                    word = new String(chars);
                } else if (scramble == 2) {
                    word = word.substring(0, word.length() / 2)
                            + (char)(0x2500 + rand.nextInt(256))
                            + word.substring(word.length() / 2);
                }

                sb.append(word);
            } else {
                int type = rand.nextInt(6);

                switch (type) {
                    case 0:
                        sb.append((char) ('A' + rand.nextInt(26)));
                        break;
                    case 1:
                        sb.append((char) ('a' + rand.nextInt(26)));
                        break;
                    case 2:
                        sb.append((char) ('0' + rand.nextInt(10)));
                        break;
                    case 3:
                        sb.append((char) (0x00A0 + rand.nextInt(0x017F - 0x00A0)));
                        break;
                    case 4:
                        sb.append((char) (0x2500 + rand.nextInt(256)));
                        break;
                    default:
                        sb.append((char) (0x0370 + rand.nextInt(0x03FF - 0x0370)));
                        break;
                }
            }
        }

        return sb.substring(0, length);
    }
}
