package com.itlesports.nightmaremode.util.underworld.postprocessing;

public class HdrPostProcessor extends AbstractPostProcessor {

    public static final HdrPostProcessor INSTANCE = new HdrPostProcessor();

    private int   locExposure = -1;
    private int   locGamma    = -1;
    private float exposure    = 1.0f;
    private float gamma       = 1.2f;
    private int   locContrast   = -1;
    private int   locBrightness = -1;
    private float contrast      = 1.2f;
    private float brightness    = 0.0f;
    private int   locTexel    = -1;
    private int   locStrength = -1;
    private float strength    = 0.9f;

    private static final String FRAG =
            "uniform sampler2D uTexture;\n" +
                    "uniform float uContrast;\n" +
                    "uniform float uBrightness;\n" +
                    "uniform float uExposure;\n" +
                    "uniform float uGamma;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "uniform vec2  uTexel;\n" +
                    "uniform float uStrength;\n" +
                    "void main() {\n" +
                    "    vec3 c = texture2D(uTexture, vTexCoord).rgb;\n" +
                    "    c *= uExposure;\n" +
                    "    c = c / (c + vec3(1.0));\n" +
                    "    c = pow(c, vec3(1.0 / uGamma));\n" +
                    "    c = (c - 0.5) * uContrast + 0.5 + uBrightness;\n" +
                    "    c = clamp(c, 0.0, 1.0);\n" +
                    "    vec3 n = texture2D(uTexture, vTexCoord + vec2( 0.0,  uTexel.y)).rgb;\n" +
                    "    vec3 s = texture2D(uTexture, vTexCoord + vec2( 0.0, -uTexel.y)).rgb;\n" +
                    "    vec3 e = texture2D(uTexture, vTexCoord + vec2( uTexel.x,  0.0)).rgb;\n" +
                    "    vec3 w = texture2D(uTexture, vTexCoord + vec2(-uTexel.x,  0.0)).rgb;\n" +
                    "    n *= uExposure;\n" +
                    "    n = n / (n + vec3(1.0));\n" +
                    "    n = pow(n, vec3(1.0 / uGamma));\n" +
                    "    n = clamp((n - 0.5) * uContrast + 0.5 + uBrightness, 0.0, 1.0);\n" +
                    "    s *= uExposure;\n" +
                    "    s = s / (s + vec3(1.0));\n" +
                    "    s = pow(s, vec3(1.0 / uGamma));\n" +
                    "    s = clamp((s - 0.5) * uContrast + 0.5 + uBrightness, 0.0, 1.0);\n" +
                    "    e *= uExposure;\n" +
                    "    e = e / (e + vec3(1.0));\n" +
                    "    e = pow(e, vec3(1.0 / uGamma));\n" +
                    "    e = clamp((e - 0.5) * uContrast + 0.5 + uBrightness, 0.0, 1.0);\n" +
                    "    w *= uExposure;\n" +
                    "    w = w / (w + vec3(1.0));\n" +
                    "    w = pow(w, vec3(1.0 / uGamma));\n" +
                    "    w = clamp((w - 0.5) * uContrast + 0.5 + uBrightness, 0.0, 1.0);\n" +
                    "    vec3 sharp = c + uStrength * (4.0 * c - n - s - e - w);\n" +
                    "    gl_FragColor = vec4(clamp(sharp, 0.0, 1.0), 1.0);\n" +
                    "}\n";

    public void setExposure(float e) { exposure = e; }
    public void setGamma(float g)    { gamma    = g; }
    public void setContrast(float c)   { contrast    = c; }
    public void setBrightness(float b) { brightness  = b; }

    @Override protected String getVertexShaderSource()   { return STANDARD_VERTEX_SHADER; }
    @Override protected String getFragmentShaderSource() { return FRAG; }

    @Override
    protected void onShaderLinked() {
        locExposure = locOf("uExposure");
        locGamma    = locOf("uGamma");
        locContrast   = locOf("uContrast");
        locBrightness = locOf("uBrightness");
        locTexel    = locOf("uTexel");
        locStrength = locOf("uStrength");
    }

    @Override
    protected void uploadUniforms(int width, int height) {
        uniform1f(locExposure, exposure);
        uniform1f(locGamma,    gamma);
        uniform1f(locContrast,   contrast);
        uniform1f(locBrightness, brightness);
        uniform2f(locTexel, 1.0f / width, 1.0f / height);
        uniform1f(locStrength, strength);
    }
}