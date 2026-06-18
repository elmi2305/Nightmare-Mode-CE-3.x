package com.itlesports.nightmaremode.util.underworld.postprocessing;

public class SharpenPostProcessor extends AbstractPostProcessor {

    public static final SharpenPostProcessor INSTANCE = new SharpenPostProcessor();

    private int   locTexel    = -1;
    private int   locStrength = -1;
    private float strength    = 0.9f;

    private static final String FRAG =
            "uniform sampler2D uTexture;\n" +
                    "uniform vec2  uTexel;\n" +
                    "uniform float uStrength;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "void main() {\n" +
                    "    vec3 c  = texture2D(uTexture, vTexCoord).rgb;\n" +
                    "    vec3 n  = texture2D(uTexture, vTexCoord + vec2( 0.0,  uTexel.y)).rgb;\n" +
                    "    vec3 s  = texture2D(uTexture, vTexCoord + vec2( 0.0, -uTexel.y)).rgb;\n" +
                    "    vec3 e  = texture2D(uTexture, vTexCoord + vec2( uTexel.x,  0.0)).rgb;\n" +
                    "    vec3 w  = texture2D(uTexture, vTexCoord + vec2(-uTexel.x,  0.0)).rgb;\n" +
                    "    vec3 sharp = c + uStrength * (4.0 * c - n - s - e - w);\n" +
                    "    gl_FragColor = vec4(clamp(sharp, 0.0, 1.0), 1.0);\n" +
                    "}\n";

    public void setStrength(float s) { strength = s; }

    @Override protected String getVertexShaderSource()   { return STANDARD_VERTEX_SHADER; }
    @Override protected String getFragmentShaderSource() { return FRAG; }

    @Override
    protected void onShaderLinked() {
        locTexel    = locOf("uTexel");
        locStrength = locOf("uStrength");
    }

    @Override
    protected void uploadUniforms(int width, int height) {
        uniform2f(locTexel, 1.0f / width, 1.0f / height);
        uniform1f(locStrength, strength);
    }
}