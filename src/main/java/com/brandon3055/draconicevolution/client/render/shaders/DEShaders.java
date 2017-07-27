package com.brandon3055.draconicevolution.client.render.shaders;

import codechicken.lib.render.shader.ShaderObject;
import com.brandon3055.draconicevolution.DEConfig;
import net.minecraft.client.renderer.OpenGlHelper;

import java.io.IOException;

import static codechicken.lib.render.shader.ShaderHelper.getStream;
import static codechicken.lib.render.shader.ShaderHelper.readShader;
import static codechicken.lib.render.shader.ShaderObject.ShaderType.FRAGMENT;
import static codechicken.lib.render.shader.ShaderObject.ShaderType.VERTEX;

/**
 * Created by brandon3055 on 6/11/2016.
 */
public class DEShaders {

    public static ShaderObject reactor;
    public static ShaderObject reactorShield;


    //public static int reactorOpID;
    //public static ShaderProgram reactor;
    //public static ShaderProgram reactorShield;
    //public static ReactorOperation reactorOp;

    //public static int reactorBeamOpID;
    public static ShaderObject reactorBeamI;
    public static ShaderObject reactorBeamO;
    public static ShaderObject reactorBeamE;
    //public static ReactorBeamOperation reactorBeamOp;

    //public static int crystalOpID;
    public static ShaderObject energyCrystal_V;
    public static ShaderObject energyCrystal_F;
    //public static ECrystalOperation eCrystalOp;

    //public static int explosionOverlayOpID;
    public static ShaderObject explosionOverlay;
    //public static ExplosionOverlayOp explosionOverlayOp;

    //public static int explosionWaveOpID;
    public static ShaderObject explosionBlastWave;
    public static ShaderObject explosionLeadingWave;
    public static ShaderObject explosionCoreEffect;
    //public static ExplosionWaveOp explosionWaveOp;

    static {
        if (OpenGlHelper.shadersSupported && DEConfig.useShaders) {
            try {
                initShaders();
            } catch (IOException e) {
                throw new RuntimeException("Unable to initialize DEShaders.", e);
            }
        }
    }

    public static void initShaders() throws IOException {
        dispose(reactor);
        dispose(reactorShield);
        dispose(reactorBeamI);
        dispose(reactorBeamO);
        dispose(reactorBeamE);
        dispose(energyCrystal_V);
        dispose(explosionOverlay);
        dispose(explosionBlastWave);
        dispose(explosionLeadingWave);
        dispose(explosionCoreEffect);

        //reactorOp
        reactor = new ShaderObject(FRAGMENT, readShader(getStream("/assets/draconicevolution/shaders/reactor.frag")));
        reactorShield = new ShaderObject(FRAGMENT, readShader(getStream("/assets/draconicevolution/shaders/reactor_shield.frag")));

        //reactorBeamOp
        reactorBeamI = new ShaderObject(FRAGMENT, readShader(getStream("/assets/draconicevolution/shaders/reactor_beam_i.frag")));
        reactorBeamO = new ShaderObject(FRAGMENT, readShader(getStream("/assets/draconicevolution/shaders/reactor_beam_o.frag")));
        reactorBeamE = new ShaderObject(FRAGMENT, readShader(getStream("/assets/draconicevolution/shaders/reactor_beam_e.frag")));

        //eCrystalOp
        energyCrystal_V = new ShaderObject(VERTEX, readShader(getStream("/assets/draconicevolution/shaders/energy_crystal.vert")));
        energyCrystal_F = new ShaderObject(FRAGMENT, readShader(getStream("/assets/draconicevolution/shaders/energy_crystal.frag")));

        //explosionOverlayOp
        explosionOverlay = new ShaderObject(FRAGMENT, readShader(getStream("/assets/draconicevolution/shaders/explosion_overlay.frag")));

        //explosionWaveOp
        explosionBlastWave = new ShaderObject(FRAGMENT, readShader(getStream("/assets/draconicevolution/shaders/explosion_blast_wave.frag")));
        explosionLeadingWave = new ShaderObject(FRAGMENT, readShader(getStream("/assets/draconicevolution/shaders/explosion_leading_wave.frag")));
        explosionCoreEffect = new ShaderObject(FRAGMENT, readShader(getStream("/assets/draconicevolution/shaders/explosion_core_effect.frag")));
    }

//    public static class ExplosionWaveOp implements IShaderOperation {
//        float time = 0;
//        float scale = 0;
//        float alpha = 1;
//
//        @Override
//        public boolean load(ShaderProgram program) {
//            return true;
//        }
//
//        @Override
//        public void operate(ShaderProgram program) {
//            int t = program.getUniformLoc("time");
//            ARBShaderObjects.glUniform1fARB(t, time);
//
//            int s = program.getUniformLoc("scale");
//            ARBShaderObjects.glUniform1fARB(s, scale);
//
//            int a = program.getUniformLoc("alpha");
//            ARBShaderObjects.glUniform1fARB(a, alpha);
//        }
//
//        @Override
//        public int operationID() {
//            return reactorOpID;
//        }
//
//        public void setTime(float time) {
//            this.time = time;
//        }
//
//        public void setScale(float scale) {
//            this.scale = scale;
//        }
//
//        public void setAlpha(float alpha) {
//            this.alpha = alpha;
//        }
//    }

    public static boolean useShaders() {
        return OpenGlHelper.shadersSupported && DEConfig.useShaders;
    }

    private static void dispose(ShaderObject object) {
        if (object != null) {
            object.disposeObject();
        }
    }
}
