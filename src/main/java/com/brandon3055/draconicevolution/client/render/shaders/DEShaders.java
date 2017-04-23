package com.brandon3055.draconicevolution.client.render.shaders;

import codechicken.lib.render.shader.ShaderProgram;
import codechicken.lib.render.shader.pipeline.CCShaderPipeline;
import codechicken.lib.render.shader.pipeline.attribute.IShaderOperation;
import com.brandon3055.draconicevolution.DEConfig;
import gnu.trove.map.hash.TObjectFloatHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.ARBShaderObjects;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class DEShaders {

    public static int reactorOpID;
    public static ShaderProgram reactor;
    public static ShaderProgram reactorShield;
    public static ReactorOperation reactorOp;

    public static int reactorBeamOpID;
    public static ShaderProgram reactorBeamI;
    public static ShaderProgram reactorBeamO;
    public static ShaderProgram reactorBeamE;
    public static ReactorBeamOperation reactorBeamOp;

    public static int crystalOpID;
    public static ShaderProgram energyCrystal;
    public static ECrystalOperation eCrystalOp;

    public static int explosionOverlayOpID;
    public static ShaderProgram explosionOverlay;
    public static ExplosionOverlayOp explosionOverlayOp;

    public static int explosionWaveOpID;
    public static ShaderProgram explosionBlastWave;
    public static ShaderProgram explosionLeadingWave;
    public static ShaderProgram explosionCoreEffect;
    public static ExplosionWaveOp explosionWaveOp;

    static {
        initReactorShader();
        initReactorShieldShader();
        initEnergyCrystalShader();
        initReactorBeams();
        initExplosionOverlay();
        initExplosionWave();
    }

    public static void initReactorShader() {
        if (reactor != null) {
            reactor.cleanup();
        }

        reactorOpID = CCShaderPipeline.registerOperation();
        reactor = new ShaderProgram();
        reactor.attachFrag("/assets/draconicevolution/shaders/reactor.frag");
        reactor.attachShaderOperation(reactorOp = new ReactorOperation());
        reactor.validate();
    }

    public static void initReactorShieldShader() {
        if (reactorShield != null) {
            reactorShield.cleanup();
        }

        reactorShield = new ShaderProgram();
        reactorShield.attachFrag("/assets/draconicevolution/shaders/reactor_shield.frag");
        reactorShield.attachShaderOperation(reactorOp);
        reactorShield.validate();
    }

    public static void initReactorBeams() {
        if (reactorBeamI != null) {
            reactorBeamI.cleanup();
        }

        reactorBeamOpID = CCShaderPipeline.registerOperation();
        reactorBeamI = new ShaderProgram();
        reactorBeamI.attachFrag("/assets/draconicevolution/shaders/reactor_beam_i.frag");
        reactorBeamI.attachShaderOperation(reactorBeamOp = new ReactorBeamOperation());
        reactorBeamI.validate();

        if (reactorBeamO != null) {
            reactorBeamO.cleanup();
        }

        reactorBeamO = new ShaderProgram();
        reactorBeamO.attachFrag("/assets/draconicevolution/shaders/reactor_beam_o.frag");
        reactorBeamO.attachShaderOperation(reactorBeamOp);
        reactorBeamO.validate();

        if (reactorBeamE != null) {
            reactorBeamE.cleanup();
        }

        reactorBeamE = new ShaderProgram();
        reactorBeamE.attachFrag("/assets/draconicevolution/shaders/reactor_beam_e.frag");
        reactorBeamE.attachShaderOperation(reactorBeamOp);
        reactorBeamE.validate();
    }

    public static void initEnergyCrystalShader() {
        if (energyCrystal != null) {
            energyCrystal.cleanup();
        }

        crystalOpID = CCShaderPipeline.registerOperation();
        energyCrystal = new ShaderProgram();
        energyCrystal.attachFrag("/assets/draconicevolution/shaders/energy_crystal.frag");
        energyCrystal.attachVert("/assets/draconicevolution/shaders/energy_crystal.vert");
        energyCrystal.attachShaderOperation(eCrystalOp = new ECrystalOperation());
        energyCrystal.validate();
    }

    public static void initExplosionOverlay() {
        if (explosionOverlay != null) {
            explosionOverlay.cleanup();
        }

        explosionOverlayOpID = CCShaderPipeline.registerOperation();
        explosionOverlay = new ShaderProgram();
        explosionOverlay.attachFrag("/assets/draconicevolution/shaders/explosion_overlay.frag");
        explosionOverlay.attachShaderOperation(explosionOverlayOp = new ExplosionOverlayOp());
        explosionOverlay.validate();
    }

    public static void initExplosionWave() {
        if (explosionBlastWave != null) {
            explosionBlastWave.cleanup();
        }
        if (explosionLeadingWave != null) {
            explosionLeadingWave.cleanup();
        }

        explosionWaveOpID = CCShaderPipeline.registerOperation();
        explosionBlastWave = new ShaderProgram();
        explosionBlastWave.attachFrag("/assets/draconicevolution/shaders/explosion_blast_wave.frag");
        explosionBlastWave.attachShaderOperation(explosionWaveOp = new ExplosionWaveOp());
        explosionBlastWave.validate();

        explosionLeadingWave = new ShaderProgram();
        explosionLeadingWave.attachFrag("/assets/draconicevolution/shaders/explosion_leading_wave.frag");
        explosionLeadingWave.attachShaderOperation(explosionWaveOp);
        explosionLeadingWave.validate();

        explosionCoreEffect = new ShaderProgram();
        explosionCoreEffect.attachFrag("/assets/draconicevolution/shaders/explosion_core_effect.frag");
        explosionCoreEffect.attachShaderOperation(explosionWaveOp);
        explosionCoreEffect.validate();
    }

    public static class ReactorOperation implements IShaderOperation {
        public float intensity = 0;
        public float animation = 0;

        @Override
        public boolean load(ShaderProgram program) {
            return true;
        }

        @Override
        public void operate(ShaderProgram program) {
            int time = program.getUniformLoc("time");
            ARBShaderObjects.glUniform1fARB(time, animation);

            int intensity = program.getUniformLoc("intensity");
            ARBShaderObjects.glUniform1fARB(intensity, this.intensity);
        }

        @Override
        public int operationID() {
            return reactorOpID;
        }

        public void setAnimation(float animation) {
            this.animation = animation;
        }

        //Ranges
        //-1 - 0 Cold start range
        // 0 - 1 Normal Temp Range
        // 1 - 1.4 Overload temp range
        public void setIntensity(float intensity) {
            this.intensity = intensity;
        }
    }

    public static class ReactorBeamOperation implements IShaderOperation {
        public float power = 0;
        public float animation = 0;
        public float fade = 0;
        public float startup = 0;

        @Override
        public boolean load(ShaderProgram program) {
            return true;
        }

        @Override
        public void operate(ShaderProgram program) {
            int time = program.getUniformLoc("time");
            ARBShaderObjects.glUniform1fARB(time, this.animation);

            int intensity = program.getUniformLoc("power");
            ARBShaderObjects.glUniform1fARB(intensity, this.power);

            int fade = program.getUniformLoc("fade");
            ARBShaderObjects.glUniform1fARB(fade, this.fade);

            int startup = program.getUniformLoc("startup");
            ARBShaderObjects.glUniform1fARB(startup, this.startup);
        }

        @Override
        public int operationID() {
            return reactorOpID;
        }

        public void setAnimation(float animation) {
            this.animation = animation;
        }

        public void setPower(float power) {
            this.power = power;
        }

        public void setFade(float fade) {
            this.fade = fade;
        }

        public void setStartup(float startup) {
            this.startup = startup;
        }
    }

    public static class ECrystalOperation implements IShaderOperation {
        public int type = 0;
        public float animation = 0;
        public float angleX = 0;
        public float angleY = 0;
        public float mipmap = 1;
        private final TObjectFloatHashMap<ShaderProgram> timeCache = new TObjectFloatHashMap<>();
        private final TObjectFloatHashMap<ShaderProgram> mipmapCache = new TObjectFloatHashMap<>();
        private final TObjectIntHashMap<ShaderProgram> typeCache = new TObjectIntHashMap<>();
        private final TObjectFloatHashMap<ShaderProgram> angleCache = new TObjectFloatHashMap<>();

        @Override
        public boolean load(ShaderProgram program) {
            return true;
        }

        @Override
        public void operate(ShaderProgram program) {
            if (animation != timeCache.get(program)) {
                int time = program.getUniformLoc("time");
                ARBShaderObjects.glUniform1fARB(time, animation);
                timeCache.put(program, animation);
            }

            if (this.mipmap != mipmapCache.get(program)) {
                int mipmap = program.getUniformLoc("mipmap");
                ARBShaderObjects.glUniform1fARB(mipmap, this.mipmap);
                mipmapCache.put(program, this.mipmap);
            }

            if (this.type != typeCache.get(program)) {
                int type = program.getUniformLoc("type");
                ARBShaderObjects.glUniform1iARB(type, this.type);
                typeCache.put(program, this.type);
            }

            if (angleX * angleY != angleCache.get(program)) {
                int angle = program.getUniformLoc("angle");
                ARBShaderObjects.glUniform2fARB(angle, angleX, angleY);
                angleCache.put(program, angleX * angleY);
            }
//
//            int time = program.getUniformLoc("time");
//            ARBShaderObjects.glUniform1fARB(time, animation);
//            int mipmap = program.getUniformLoc("mipmap");
//            ARBShaderObjects.glUniform1fARB(mipmap, this.mipmap);
//            int type = program.getUniformLoc("type");
//            ARBShaderObjects.glUniform1iARB(type, this.type);
//            int angle = program.getUniformLoc("angle");
//            ARBShaderObjects.glUniform2fARB(angle, angleX, angleY);
        }

        @Override
        public int operationID() {
            return crystalOpID;
        }

        public void setAnimation(float animation) {
            this.animation = animation;
        }

        public void setType(int type) {
            this.type = type;
        }

        public void setAngle(float angleX, float angleY) {
            this.angleX = angleX;
            this.angleY = angleY;
        }

        public void setMipmap(float mipmap) {
            this.mipmap = mipmap;
        }
    }

    public static class ExplosionOverlayOp implements IShaderOperation {
        float screenX = 0;
        float screenY = 0;
        float intensity = 0;

        @Override
        public boolean load(ShaderProgram program) {
            return true;
        }

        @Override
        public void operate(ShaderProgram program) {
            int pos = program.getUniformLoc("screenPos");
            ARBShaderObjects.glUniform2fARB(pos, screenX, screenY);

            int intensity = program.getUniformLoc("intensity");
            ARBShaderObjects.glUniform1fARB(intensity, this.intensity);

            int screenSize = program.getUniformLoc("screenSize");
            ARBShaderObjects.glUniform2fARB(screenSize, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        }

        @Override
        public int operationID() {
            return reactorOpID;
        }

        public void setScreenPos(float x, float y) {
            this.screenX = x;
            this.screenY = y;
        }

        public void setIntensity(float intensity) {
            this.intensity = intensity;
        }
    }

    public static class ExplosionWaveOp implements IShaderOperation {
        float time = 0;
        float scale = 0;
        float alpha = 1;

        @Override
        public boolean load(ShaderProgram program) {
            return true;
        }

        @Override
        public void operate(ShaderProgram program) {
            int t = program.getUniformLoc("time");
            ARBShaderObjects.glUniform1fARB(t, time);

            int s = program.getUniformLoc("scale");
            ARBShaderObjects.glUniform1fARB(s, scale);

            int a = program.getUniformLoc("alpha");
            ARBShaderObjects.glUniform1fARB(a, alpha);
        }

        @Override
        public int operationID() {
            return reactorOpID;
        }

        public void setTime(float time) {
            this.time = time;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        public void setAlpha(float alpha) {
            this.alpha = alpha;
        }
    }

    public static boolean useShaders() {
        return OpenGlHelper.shadersSupported && DEConfig.useShaders;
    }
}
