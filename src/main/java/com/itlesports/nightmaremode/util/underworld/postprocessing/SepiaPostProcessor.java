package com.itlesports.nightmaremode.util.underworld.postprocessing;


public final class SepiaPostProcessor extends AbstractPostProcessor {

    public static final SepiaPostProcessor INSTANCE = new SepiaPostProcessor();
    private SepiaPostProcessor() {}

    @Override protected String getVertexShaderSource() { return STANDARD_VERTEX_SHADER; }

    @Override
    protected String getFragmentShaderSource() {
        return
                "uniform sampler2D uTexture;\n" +
                        "varying vec2 vTexCoord;\n" +
                        "void main() {\n" +
                        "    vec4  c = texture2D(uTexture, vTexCoord);\n" +
                        "    float r = dot(c.rgb, vec3(0.393, 0.769, 0.189));\n" +
                        "    float g = dot(c.rgb, vec3(0.349, 0.686, 0.168));\n" +
                        "    float b = dot(c.rgb, vec3(0.272, 0.534, 0.131));\n" +
                        "    gl_FragColor = vec4(clamp(r, 0.0, 1.0),\n" +
                        "                       clamp(g, 0.0, 1.0),\n" +
                        "                       clamp(b, 0.0, 1.0),\n" +
                        "                       c.a);\n" +
                        "}\n";
    }
}