package com.itlesports.nightmaremode.rendering;

import com.itlesports.nightmaremode.fishing.FishingKrakenPose;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;

/** an articulated lateral silhouette with eight arms and two longer feeding tentacles. */
@Environment(EnvType.CLIENT)
public final class FishingKrakenRenderer {
    private static final FishingKrakenPose POSE = new FishingKrakenPose();
    private FishingKrakenRenderer() {}

    public static void draw(float x, float y, float heading, float fright, float time, float radius, float fightTicks) {
        POSE.update(time, heading, fright, fightTicks);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glRotatef(POSE.angle * 180F / (float)Math.PI, 0, 0, 1);
        GL11.glScalef(radius / 142 * POSE.scale, radius / 142 * POSE.scale, 1);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        if (fightTicks >= 0 && POSE.jet > 0.05F) {
            for (int i = 0; i < 5; i++) {
                float drift = (fightTicks / 7F + i * 0.19F) % 1;
                ellipse(30 + drift * 90, 14 + (float)Math.sin(i * 2.4) * drift * 20,
                        8 + drift * 24, 6 + drift * 16, 0.012F, 0.021F, 0.031F, (1 - drift) * POSE.jet * 0.2F, 0);
            }
        }
        for (int arm = 0; arm < 4; arm++) appendage(arm, false);
        appendage(8, false);
        fin(-1, time);
        mantle(time);
        fin(1, time);
        head(time);
        for (int arm = 4; arm < 8; arm++) appendage(arm, true);
        appendage(9, true);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private static void mantle(float time) {
        float[] xs = {-222, -211, -192, -165, -132, -99, -72, -54};
        float[] widths = {0.5F, 6, 13, 20, 25, 26, 23, 17};
        float breath = POSE.contraction;
        // longitudinal strips give the tapered mantle volume without a heavy outline.
        for (int band = 0; band < 10; band++) {
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            for (int i = 0; i < xs.length; i++) {
                for (int edge = 0; edge <= 1; edge++) {
                    float v = -1 + (band + edge) / 5F;
                    float light = (float)Math.sqrt(Math.max(0, 1 - v * v));
                    float warmth = 0.8F + 0.2F * i / (xs.length - 1F);
                    GL11.glColor4f((0.085F + light * 0.31F) * warmth,
                            0.025F + light * 0.080F, 0.036F + light * 0.082F, 1);
                    GL11.glVertex2f(xs[i], POSE.mantleY(xs[i], v * widths[i] + (xs[i] + 110) * 0.035F));
                }
            }
            GL11.glEnd();
        }
        // small irregular chromatophores follow the body instead of forming stripes.
        for (int i = 0; i < 85; i++) {
            float t = (i * 37 % 101) / 100F;
            float px = -205 + t * 144;
            float half = 7 + (float)Math.sin(t * 2.2) * 17;
            float py = ((i * 61 % 97) / 48F - 1) * half * 0.85F;
            ellipse(px, POSE.mantleY(px, py), 0.55F + i % 3 * 0.3F, 0.45F + i % 2 * 0.2F,
                    0.13F, 0.027F, 0.041F, 0.44F, 0.2F);
        }
        ellipse(-55, POSE.mantleY(-55, 0), 5, 17 * breath, 0.11F, 0.025F, 0.037F, 1, 1);
    }

    private static void fin(int side, float time) {
        float wave = POSE.finStroke * side;
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glColor4f(0.29F, 0.07F, 0.09F, 0.95F); GL11.glVertex2f(-181, POSE.mantleY(-181, side * 10));
        GL11.glColor4f(0.105F, 0.025F, 0.039F, 0.8F); GL11.glVertex2f(-221, POSE.mantleY(-221, -4));
        GL11.glVertex2f(-198, POSE.mantleY(-198, side * (24 + wave)));
        GL11.glVertex2f(-168, POSE.mantleY(-168, side * (38 + wave)));
        GL11.glColor4f(0.23F, 0.05F, 0.067F, 0.95F); GL11.glVertex2f(-144, POSE.mantleY(-144, side * 22));
        GL11.glVertex2f(-178, POSE.mantleY(-178, side * 8));
        GL11.glEnd();
    }

    private static void head(float time) {
        ellipse(-31, 0, 25, 17, 0.37F, 0.095F, 0.11F, 1, 1);
        // the siphon sits below the head; the beak remains hidden inside the arm crown.
        ellipse(-28, 14, 15, 5, 0.21F, 0.055F, 0.068F, 1, 1);
        ellipse(-14, 13, 3, 3.5F, 0.046F, 0.025F, 0.031F, 1, 1);
        ellipse(-4, 1, 13, 13, 0.24F, 0.058F, 0.075F, 1, 1);
        // a single lateral eye, with a round pupil and a restrained wet reflection.
        ellipse(-31, -7, 10, 9.5F, 0.16F, 0.075F, 0.075F, 1, 1);
        ellipse(-31, -7, 8, 8, 0.36F, 0.29F, 0.24F, 1, 0.85F);
        ellipse(-30, -7, 6.8F, 7, 0.008F, 0.013F, 0.019F, 1, 1);
        ellipse(-33, -10, 1.8F, 1.2F, 0.72F, 0.73F, 0.65F, 0.6F, 0.1F);
    }

    private static void appendage(int arm, boolean front) {
        boolean feeding = arm >= 8;
        float[] xs = POSE.x[arm], ys = POSE.y[arm], widths = POSE.width[arm];
        tube(xs, ys, widths, front);
        for (int i = feeding ? 39 : 5; i < 47; i += feeding ? 2 : 4) {
            float dx = xs[i + 1] - xs[i - 1], dy = ys[i + 1] - ys[i - 1];
            GL11.glPushMatrix();
            GL11.glTranslatef(xs[i], ys[i], 0);
            GL11.glRotatef((float)(Math.atan2(dy, dx) * 180 / Math.PI), 0, 0, 1);
            float size = widths[i] * 0.48F;
            ellipse(0, -size * 0.25F, Math.max(0.45F, size * 0.6F), size,
                    0.48F, 0.31F, 0.28F, front ? 0.9F : 0.55F, 0.8F);
            ellipse(0, -size * 0.25F, Math.max(0.2F, size * 0.27F), size * 0.45F,
                    0.09F, 0.035F, 0.04F, 0.9F, 1);
            GL11.glPopMatrix();
        }
    }

    private static void tube(float[] x, float[] y, float[] width, boolean front) {
        for (int band = 0; band < 4; band++) {
            GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
            for (int i = 0; i < x.length; i++) {
                int a = Math.max(0, i - 1), b = Math.min(x.length - 1, i + 1);
                float dx = x[b] - x[a], dy = y[b] - y[a];
                float length = Math.max(0.001F, (float)Math.hypot(dx, dy));
                for (int edge = 0; edge <= 1; edge++) {
                    float v = -1 + (band + edge) / 2F;
                    float light = (float)Math.sqrt(Math.max(0, 1 - v * v));
                    float shade = front ? 1 : 0.68F;
                    GL11.glColor4f((0.09F + light * 0.26F) * shade,
                            (0.027F + light * 0.065F) * shade, (0.04F + light * 0.065F) * shade, 1);
                    GL11.glVertex2f(x[i] - dy / length * width[i] * v, y[i] + dx / length * width[i] * v);
                }
            }
            GL11.glEnd();
        }
    }

    private static void ellipse(float x, float y, float rx, float ry, float r, float g, float b, float alpha, float edge) {
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glColor4f(r, g, b, alpha); GL11.glVertex2f(x, y);
        for (int i = 0; i <= 24; i++) {
            double angle = i * Math.PI / 12;
            GL11.glColor4f(r * 0.55F, g * 0.55F, b * 0.65F, alpha * edge);
            GL11.glVertex2f(x + (float)Math.cos(angle) * rx, y + (float)Math.sin(angle) * ry);
        }
        GL11.glEnd();
    }
}
