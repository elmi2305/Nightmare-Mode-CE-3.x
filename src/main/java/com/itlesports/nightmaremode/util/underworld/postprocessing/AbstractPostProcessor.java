package com.itlesports.nightmaremode.util.underworld.postprocessing;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;

public abstract class AbstractPostProcessor {

    protected static final String STANDARD_VERTEX_SHADER =
            "varying vec2 vTexCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = ftransform();\n" +
                    "    vTexCoord   = gl_MultiTexCoord0.xy;\n" +
                    "}\n";

    private int framebufferId     = -1;
    private int colorTextureId    = -1;
    private int depthBufferId     = -1;
    private int framebufferWidth  = -1;
    private int framebufferHeight = -1;

    protected int shaderProgramId  = -1;
    private   int vertexShaderId   = -1;
    private   int fragmentShaderId = -1;

    protected int locTexture  = -1;
    protected int locMixAmount = -1;

    private   boolean capturing            = false;
    private   boolean targetEnabled        = false;
    private   float   transitionProgress   = 0.0f;
    private   float   transitionFrom       = 0.0f;
    private   long    transitionStartMs    = -1L;
    private   long    transitionDurationMs = 1000L;
    protected boolean shaderReady          = false;

    protected abstract String getVertexShaderSource();
    protected abstract String getFragmentShaderSource();
    protected void onShaderLinked() {}
    protected void uploadUniforms(int width, int height) {}

    public void    setEnabled(boolean enabled) {
        if (this.targetEnabled == enabled) return;
        this.targetEnabled     = enabled;
        this.transitionFrom    = transitionProgress;
        this.transitionStartMs = System.currentTimeMillis();
    }
    public boolean isEnabled()                    { return targetEnabled; }
    public void    setTransitionDuration(long ms) { this.transitionDurationMs = ms; }
    public float   getTransitionProgress()        { return transitionProgress; }

    public  boolean isActive() { return transitionProgress > 0.0f || targetEnabled; }

    public void beginCapture(int width, int height) {
        if (!isActive() || width <= 0 || height <= 0) return;
        ensureFramebuffer(width, height);
        capturing = true;
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, framebufferId);
        GL11.glViewport(0, 0, width, height);
    }

    public void endCaptureAndPresent(int width, int height) {
        updateTransition();

        if (!capturing) return;
        capturing = false;

        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
        GL11.glViewport(0, 0, width, height);

        if (!shaderReady || transitionProgress <= 0.0f) return;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, width, 0.0, height, -1.0, 1.0);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTextureId);

        ARBShaderObjects.glUseProgramObjectARB(shaderProgramId);
        if (locTexture   >= 0) ARBShaderObjects.glUniform1iARB(locTexture, 0);
        if (locMixAmount >= 0) ARBShaderObjects.glUniform1fARB(locMixAmount, transitionProgress);
        uploadUniforms(width, height);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0.0f, 0.0f); GL11.glVertex2f(0.0f,  0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f); GL11.glVertex2f(width, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f); GL11.glVertex2f(width, height);
        GL11.glTexCoord2f(0.0f, 1.0f); GL11.glVertex2f(0.0f,  height);
        GL11.glEnd();

        ARBShaderObjects.glUseProgramObjectARB(0);

        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public void delete() {
        deleteShader();
        deleteFramebuffer();
    }

    protected int  locOf(String name) { return ARBShaderObjects.glGetUniformLocationARB(shaderProgramId, name); }
    protected void uniform1i(int loc, int v) { if (loc >= 0) ARBShaderObjects.glUniform1iARB(loc, v); }
    protected void uniform1f(int loc, float v) { if (loc >= 0) ARBShaderObjects.glUniform1fARB(loc, v); }
    protected void uniform2f(int loc, float x, float y) { if (loc >= 0) ARBShaderObjects.glUniform2fARB(loc, x, y); }
    protected void uniform3f(int loc, float x, float y, float z) { if (loc >= 0) ARBShaderObjects.glUniform3fARB(loc, x, y, z); }
    protected void uniform4f(int loc, float x, float y, float z, float w) { if (loc >= 0) ARBShaderObjects.glUniform4fARB(loc, x, y, z, w); }

    private void updateTransition() {
        if (transitionStartMs < 0L) return;
        float t = transitionDurationMs > 0
                ? (float)(System.currentTimeMillis() - transitionStartMs) / transitionDurationMs
                : 1.0f;
        if (t >= 1.0f) { t = 1.0f; transitionStartMs = -1L; }
        float target = targetEnabled ? 1.0f : 0.0f;
        transitionProgress = transitionFrom + (target - transitionFrom) * t;
    }

    private void ensureFramebuffer(int width, int height) {
        if (framebufferId != -1 && framebufferWidth == width && framebufferHeight == height) return;

        deleteFramebuffer();
        framebufferWidth  = width;
        framebufferHeight = height;

        colorTextureId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTextureId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

        framebufferId = EXTFramebufferObject.glGenFramebuffersEXT();
        depthBufferId = EXTFramebufferObject.glGenRenderbuffersEXT();

        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, framebufferId);
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthBufferId);
        EXTFramebufferObject.glRenderbufferStorageEXT(
                EXTFramebufferObject.GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, width, height);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(
                EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT,
                EXTFramebufferObject.GL_RENDERBUFFER_EXT, depthBufferId);
        EXTFramebufferObject.glFramebufferTexture2DEXT(
                EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
                GL11.GL_TEXTURE_2D, colorTextureId, 0);

        int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
        EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);

        if (status != EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT) {
            deleteFramebuffer();
            throw new IllegalStateException(
                    getClass().getSimpleName() + ": framebuffer incomplete (status=0x" +
                            Integer.toHexString(status) + ")");
        }

        ensureShader();
    }

    private void ensureShader() {
        if (shaderReady) return;
        try {
            vertexShaderId   = compileShader(ARBVertexShader.GL_VERTEX_SHADER_ARB,     getVertexShaderSource());
            fragmentShaderId = compileShader(ARBFragmentShader.GL_FRAGMENT_SHADER_ARB, getFragmentShaderSource());

            shaderProgramId = ARBShaderObjects.glCreateProgramObjectARB();
            ARBShaderObjects.glAttachObjectARB(shaderProgramId, vertexShaderId);
            ARBShaderObjects.glAttachObjectARB(shaderProgramId, fragmentShaderId);
            ARBShaderObjects.glLinkProgramARB(shaderProgramId);

            if (ARBShaderObjects.glGetObjectParameteriARB(shaderProgramId,
                    ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
                String log = ARBShaderObjects.glGetInfoLogARB(shaderProgramId, 512);
                throw new IllegalStateException(getClass().getSimpleName() + ": link failed:\n" + log);
            }

            locTexture   = locOf("uTexture");
            locMixAmount = locOf("uMixAmount");
            onShaderLinked();

            shaderReady = true;
        } catch (Throwable t) {
            shaderReady        = false;
            targetEnabled      = false;
            transitionProgress = 0.0f;
            transitionStartMs  = -1L;
            t.printStackTrace();
        }
    }

    private int compileShader(int type, String source) {
        int id = ARBShaderObjects.glCreateShaderObjectARB(type);
        ARBShaderObjects.glShaderSourceARB(id, source);
        ARBShaderObjects.glCompileShaderARB(id);
        if (ARBShaderObjects.glGetObjectParameteriARB(id,
                ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
            String log = ARBShaderObjects.glGetInfoLogARB(id, 512);
            throw new IllegalStateException(getClass().getSimpleName() + ": compile failed:\n" + log);
        }
        return id;
    }

    private void deleteShader() {
        if (shaderProgramId  != -1) { ARBShaderObjects.glDeleteObjectARB(shaderProgramId);  shaderProgramId  = -1; }
        if (vertexShaderId   != -1) { ARBShaderObjects.glDeleteObjectARB(vertexShaderId);   vertexShaderId   = -1; }
        if (fragmentShaderId != -1) { ARBShaderObjects.glDeleteObjectARB(fragmentShaderId); fragmentShaderId = -1; }
        shaderReady = false;
    }

    private void deleteFramebuffer() {
        if (colorTextureId != -1) { GL11.glDeleteTextures(colorTextureId);                            colorTextureId  = -1; }
        if (depthBufferId  != -1) { EXTFramebufferObject.glDeleteRenderbuffersEXT(depthBufferId);     depthBufferId   = -1; }
        if (framebufferId  != -1) { EXTFramebufferObject.glDeleteFramebuffersEXT(framebufferId);      framebufferId   = -1; }
        framebufferWidth  = -1;
        framebufferHeight = -1;
    }
}