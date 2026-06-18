package com.itlesports.nightmaremode.util.underworld.postprocessing;

public class PosterizationPostProcessor extends AbstractPostProcessor {

    public static final PosterizationPostProcessor INSTANCE = new PosterizationPostProcessor();

    private int   locLevels = -1;
    private float levels    = 4.0f;

    private static final String FRAG =
            "uniform sampler2D uTexture;\n" +
                    "uniform float uLevels;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "void main() {\n" +
                    "    vec4 c = texture2D(uTexture, vTexCoord);\n" +
                    "    float n = uLevels - 1.0;\n" +
                    "    vec3 p = floor(c.rgb * n + 0.5) / n;\n" +
                    "    gl_FragColor = vec4(p, c.a);\n" +
                    "}\n";

    public void setLevels(float l) { levels = Math.max(2.0f, l); }

    @Override protected String getVertexShaderSource()   { return STANDARD_VERTEX_SHADER; }
    @Override protected String getFragmentShaderSource() { return FRAG; }

    @Override
    protected void onShaderLinked() {
        locLevels = locOf("uLevels");
    }

    @Override
    protected void uploadUniforms(int width, int height) {
        uniform1f(locLevels, levels);
    }
}