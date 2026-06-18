package com.itlesports.nightmaremode.util.underworld.postprocessing;

public class ChromaticAberrationPostProcessor extends AbstractPostProcessor {

    public static final ChromaticAberrationPostProcessor INSTANCE = new ChromaticAberrationPostProcessor();

    private int   locStrength = -1;
    private float strength    = 0.005f;

    private static final String FRAG =
            "uniform sampler2D uTexture;\n" +
                    "uniform float uStrength;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "void main() {\n" +
                    "    vec2 dir = vTexCoord - vec2(0.5);\n" +
                    "    float r = texture2D(uTexture, vTexCoord + dir * uStrength).r;\n" +
                    "    float g = texture2D(uTexture, vTexCoord).g;\n" +
                    "    float b = texture2D(uTexture, vTexCoord - dir * uStrength).b;\n" +
                    "    gl_FragColor = vec4(r, g, b, 1.0);\n" +
                    "}\n";

    public void setStrength(float s) { strength = s; }

    @Override protected String getVertexShaderSource()   { return STANDARD_VERTEX_SHADER; }
    @Override protected String getFragmentShaderSource() { return FRAG; }

    @Override
    protected void onShaderLinked() {
        locStrength = locOf("uStrength");
    }

    @Override
    protected void uploadUniforms(int width, int height) {
        uniform1f(locStrength, strength);
    }
}