package com.itlesports.nightmaremode.util.underworld.postprocessing;

public class CinematicPostProcessor extends AbstractPostProcessor {

    public static final CinematicPostProcessor INSTANCE = new CinematicPostProcessor();

    private final long startNanos = System.nanoTime();

    private int locTexel               = -1;
    private int locExposure            = -1;
    private int locBloomThreshold      = -1;
    private int locBloomIntensity      = -1;
    private int locContrast            = -1;
    private int locSaturation          = -1;
    private int locShadowTint          = -1;
    private int locHighlightTint       = -1;
    private int locVignetteStrength    = -1;
    private int locChromaticAberration = -1;
    private int locGrainAmount         = -1;
    private int locSharpenStrength     = -1;
    private int locTime                = -1;

    private float exposure            = 1.2f;
    private float bloomThreshold      = 0.65f;
    private float bloomIntensity      = 0.55f;
    private float contrast            = 1.08f;
    private float saturation          = 1.15f;
    private float vignetteStrength    = 0.35f;
    private float chromaticAberration = 0.0030f;
    private float grainAmount         = 0.025f;
    private float sharpenStrength     = 0.40f;
    private float shadowTintR = 0.95f, shadowTintG = 1.00f, shadowTintB = 1.05f;
    private float highlightTintR = 1.05f, highlightTintG = 1.00f, highlightTintB = 0.93f;

    private static final String FRAG =
            "uniform sampler2D uTexture;\n" +
                    "uniform vec2  uTexel;\n" +
                    "uniform float uExposure;\n" +
                    "uniform float uBloomThreshold;\n" +
                    "uniform float uBloomIntensity;\n" +
                    "uniform float uContrast;\n" +
                    "uniform float uSaturation;\n" +
                    "uniform vec3  uShadowTint;\n" +
                    "uniform vec3  uHighlightTint;\n" +
                    "uniform float uVignetteStrength;\n" +
                    "uniform float uChromaticAberration;\n" +
                    "uniform float uGrainAmount;\n" +
                    "uniform float uSharpenStrength;\n" +
                    "uniform float uTime;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "\n" +
                    "float luma(vec3 c) {\n" +
                    "    return dot(c, vec3(0.299, 0.587, 0.114));\n" +
                    "}\n" +
                    "\n" +
                    "float hash(vec2 p) {\n" +
                    "    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);\n" +
                    "}\n" +
                    "\n" +
                    "vec3 acesFilm(vec3 x) {\n" +
                    "    float a = 2.51;\n" +
                    "    float b = 0.03;\n" +
                    "    float c = 2.43;\n" +
                    "    float d = 0.59;\n" +
                    "    float e = 0.14;\n" +
                    "    return clamp((x * (a * x + b)) / (x * (c * x + d) + e), 0.0, 1.0);\n" +
                    "}\n" +
                    "\n" +
                    "void main() {\n" +
                    "    vec2 centerDir = vTexCoord - vec2(0.5);\n" +
                    "    vec2 caOffset  = centerDir * length(centerDir) * uChromaticAberration;\n" +
                    "\n" +
                    "    vec3 base;\n" +
                    "    base.r = texture2D(uTexture, vTexCoord + caOffset).r;\n" +
                    "    base.g = texture2D(uTexture, vTexCoord).g;\n" +
                    "    base.b = texture2D(uTexture, vTexCoord - caOffset).b;\n" +
                    "\n" +
                    "    vec2 t1 = uTexel * 2.0;\n" +
                    "    vec2 t2 = uTexel * 5.0;\n" +
                    "    vec3 bloom = vec3(0.0);\n" +
                    "    bloom += texture2D(uTexture, vTexCoord + vec2( t1.x,  0.0)).rgb;\n" +
                    "    bloom += texture2D(uTexture, vTexCoord + vec2(-t1.x,  0.0)).rgb;\n" +
                    "    bloom += texture2D(uTexture, vTexCoord + vec2( 0.0,  t1.y)).rgb;\n" +
                    "    bloom += texture2D(uTexture, vTexCoord + vec2( 0.0, -t1.y)).rgb;\n" +
                    "    bloom += texture2D(uTexture, vTexCoord + vec2( t2.x,  t2.y)).rgb;\n" +
                    "    bloom += texture2D(uTexture, vTexCoord + vec2(-t2.x,  t2.y)).rgb;\n" +
                    "    bloom += texture2D(uTexture, vTexCoord + vec2( t2.x, -t2.y)).rgb;\n" +
                    "    bloom += texture2D(uTexture, vTexCoord + vec2(-t2.x, -t2.y)).rgb;\n" +
                    "    bloom *= 0.125;\n" +
                    "    float bloomMask = smoothstep(uBloomThreshold, uBloomThreshold + 0.2, luma(bloom));\n" +
                    "    bloom *= bloomMask * uBloomIntensity;\n" +
                    "\n" +
                    "    vec3 nN = texture2D(uTexture, vTexCoord + vec2(0.0,  uTexel.y)).rgb;\n" +
                    "    vec3 nS = texture2D(uTexture, vTexCoord + vec2(0.0, -uTexel.y)).rgb;\n" +
                    "    vec3 nE = texture2D(uTexture, vTexCoord + vec2( uTexel.x, 0.0)).rgb;\n" +
                    "    vec3 nW = texture2D(uTexture, vTexCoord + vec2(-uTexel.x, 0.0)).rgb;\n" +
                    "    vec3 detail = base - (nN + nS + nE + nW) * 0.25;\n" +
                    "\n" +
                    "    vec3 color = base + bloom;\n" +
                    "    color *= uExposure;\n" +
                    "    color  = acesFilm(color);\n" +
                    "    color  = (color - 0.5) * uContrast + 0.5;\n" +
                    "\n" +
                    "    float l = luma(color);\n" +
                    "    color = mix(vec3(l), color, uSaturation);\n" +
                    "    color *= mix(uShadowTint, uHighlightTint, smoothstep(0.0, 1.0, l));\n" +
                    "    color += detail * uSharpenStrength;\n" +
                    "\n" +
                    "    float dist = length(centerDir);\n" +
                    "    float vigFactor = smoothstep(0.3, 0.8, dist);\n" +
                    "    color *= mix(1.0, 1.0 - uVignetteStrength, vigFactor);\n" +
                    "\n" +
                    "    float grain = hash(vTexCoord / uTexel + uTime) - 0.5;\n" +
                    "    color += grain * uGrainAmount;\n" +
                    "\n" +
                    "    gl_FragColor = vec4(clamp(color, 0.0, 1.0), 1.0);\n" +
                    "}\n";

    public void setExposure(float v)             { exposure = v; }
    public void setBloomThreshold(float v)        { bloomThreshold = v; }
    public void setBloomIntensity(float v)        { bloomIntensity = v; }
    public void setContrast(float v)              { contrast = v; }
    public void setSaturation(float v)            { saturation = v; }
    public void setVignetteStrength(float v)      { vignetteStrength = v; }
    public void setChromaticAberration(float v)   { chromaticAberration = v; }
    public void setGrainAmount(float v)           { grainAmount = v; }
    public void setSharpenStrength(float v)       { sharpenStrength = v; }
    public void setShadowTint(float r, float g, float b)    { shadowTintR = r; shadowTintG = g; shadowTintB = b; }
    public void setHighlightTint(float r, float g, float b) { highlightTintR = r; highlightTintG = g; highlightTintB = b; }

    @Override protected String getVertexShaderSource()   { return STANDARD_VERTEX_SHADER; }
    @Override protected String getFragmentShaderSource() { return FRAG; }

    @Override
    protected void onShaderLinked() {
        locTexel               = locOf("uTexel");
        locExposure             = locOf("uExposure");
        locBloomThreshold       = locOf("uBloomThreshold");
        locBloomIntensity       = locOf("uBloomIntensity");
        locContrast             = locOf("uContrast");
        locSaturation           = locOf("uSaturation");
        locShadowTint           = locOf("uShadowTint");
        locHighlightTint        = locOf("uHighlightTint");
        locVignetteStrength     = locOf("uVignetteStrength");
        locChromaticAberration  = locOf("uChromaticAberration");
        locGrainAmount          = locOf("uGrainAmount");
        locSharpenStrength      = locOf("uSharpenStrength");
        locTime                 = locOf("uTime");
    }

    @Override
    protected void uploadUniforms(int width, int height) {
        uniform2f(locTexel, 1.0f / width, 1.0f / height);
        uniform1f(locExposure, exposure);
        uniform1f(locBloomThreshold, bloomThreshold);
        uniform1f(locBloomIntensity, bloomIntensity);
        uniform1f(locContrast, contrast);
        uniform1f(locSaturation, saturation);
        uniform3f(locShadowTint, shadowTintR, shadowTintG, shadowTintB);
        uniform3f(locHighlightTint, highlightTintR, highlightTintG, highlightTintB);
        uniform1f(locVignetteStrength, vignetteStrength);
        uniform1f(locChromaticAberration, chromaticAberration);
        uniform1f(locGrainAmount, grainAmount);
        uniform1f(locSharpenStrength, sharpenStrength);
        uniform1f(locTime, (System.nanoTime() - startNanos) / 1000000000.0f);
    }
}