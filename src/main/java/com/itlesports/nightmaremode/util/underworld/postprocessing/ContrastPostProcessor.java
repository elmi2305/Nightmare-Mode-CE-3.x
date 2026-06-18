package com.itlesports.nightmaremode.util.underworld.postprocessing;

public class ContrastPostProcessor extends AbstractPostProcessor {

    public static final ContrastPostProcessor INSTANCE = new ContrastPostProcessor();

    private int   locContrast   = -1;
    private int   locBrightness = -1;
    private float contrast      = 1.2f;
    private float brightness    = 0.0f;

    private static final String FRAG =
            "uniform sampler2D uTexture;\n" +
                    "uniform float uContrast;\n" +
                    "uniform float uBrightness;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "void main() {\n" +
                    "    vec3 c = texture2D(uTexture, vTexCoord).rgb;\n" +
                    "    c = (c - 0.5) * uContrast + 0.5 + uBrightness;\n" +
                    "    gl_FragColor = vec4(clamp(c, 0.0, 1.0), 1.0);\n" +
                    "}\n";

    public void setContrast(float c)   { contrast    = c; }
    public void setBrightness(float b) { brightness  = b; }

    @Override protected String getVertexShaderSource()   { return STANDARD_VERTEX_SHADER; }
    @Override protected String getFragmentShaderSource() { return FRAG; }

    @Override
    protected void onShaderLinked() {
        locContrast   = locOf("uContrast");
        locBrightness = locOf("uBrightness");
    }

    @Override
    protected void uploadUniforms(int width, int height) {
        uniform1f(locContrast,   contrast);
        uniform1f(locBrightness, brightness);
    }
}