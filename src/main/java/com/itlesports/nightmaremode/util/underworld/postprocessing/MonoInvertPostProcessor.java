package com.itlesports.nightmaremode.util.underworld.postprocessing;


public final class MonoInvertPostProcessor extends AbstractPostProcessor {

    public static final MonoInvertPostProcessor INSTANCE = new MonoInvertPostProcessor();

    private float brightness = 0.85f;
    private int   locBrightness = -1;

    private MonoInvertPostProcessor() {}
    public void setBrightness(float brightness) { this.brightness = brightness; }
    public float getBrightness() { return brightness; }

    @Override protected String getVertexShaderSource() { return STANDARD_VERTEX_SHADER; }

    @Override
    protected void onShaderLinked() {
        locBrightness = locOf("uBrightness");
    }

    @Override
    protected void uploadUniforms(int width, int height) {
        uniform1f(locBrightness, brightness);
    }

    @Override
    protected String getFragmentShaderSource() {
        return
                "uniform sampler2D uTexture;\n" +
                        "uniform float uBrightness;\n" +
                        "uniform float uMixAmount;\n" +
                        "varying vec2 vTexCoord;\n" +
                        "void main() {\n" +
                        "    vec4  c      = texture2D(uTexture, vTexCoord);\n" +
                        "    float lum    = dot(c.rgb, vec3(0.299, 0.587, 0.114)) * uBrightness;\n" +
                        "    vec4  effect = vec4(0.5 - lum, 0.5 - lum, 0.5 - lum, c.a);\n" +
                        "    gl_FragColor = mix(c, effect, uMixAmount);\n" +
                        "}\n";
    }
}