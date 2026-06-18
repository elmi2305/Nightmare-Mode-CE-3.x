package com.itlesports.nightmaremode.util.underworld.postprocessing;


public class InversionPostProcessor extends AbstractPostProcessor {

    public static final InversionPostProcessor INSTANCE = new InversionPostProcessor();

    private static final String FRAG =
            "uniform sampler2D uTexture;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "void main() {\n" +
                    "    vec4 c = texture2D(uTexture, vTexCoord);\n" +
                    "    gl_FragColor = vec4(1.0 - c.rgb, c.a);\n" +
                    "}\n";

    @Override protected String getVertexShaderSource()   { return STANDARD_VERTEX_SHADER; }
    @Override protected String getFragmentShaderSource() { return FRAG; }
}