#version 150

#moj_import <brandonscore:math.glsl>
//#moj_import <brandonscore:chaos.glsl>

//uniform sampler2D Sampler0;
uniform float Time;
uniform float Activation;
uniform vec3 EffectColour;
uniform vec3 FrameColour;
uniform vec3 InnerTriColour;

in vec4 Color;
in vec3 fPos;
in vec2 FaceMod;
in vec2 texCoord0;

out vec4 fragColor;

const vec2 center = vec2(0.68, 0.68);//The center of the effect triangle
const float edgeOut = 1.5/16.;
const float edgeCore = 3/16.;
const float edgeIn = 0.5/16.;
const float edgeWidth = edgeOut + edgeCore + edgeIn;
const float edgeSteps = 48;

float shapeSquare() {
    vec2 vec = abs(texCoord0 - 0.5) * 2.0;
    return max(vec.x, vec.y);
}

//Return 1 at edges and fades to 0 at center of shape
float shapeTriangle() {
    vec2 vec = abs(texCoord0 - 0.5) * 2.0;
    vec -= (1.0 - vec);
    float corner = max(vec.x, vec.y);//Dist from top and left
    float diag = distToLine(vec2(1.0, 0.0), vec2(0.0, 1.0), texCoord0);

    if (diag < 0.0125) {
        diag = 1 - (diag * 4);
    } else {
        diag -= 0.0125;
        diag = 0.95 - (diag * 5.4);
    }

    float val = max(corner, diag);
    //Adjust result so that it actually ranges from 0 ro 1.
    val += 0.305;
    val /= 1.305;
    return val;
}

vec3 shieldEffect(float shapePos) {
    //Render Inner Effect
    float pos = shapePos / (1.0 - edgeWidth + 0.07);//1 to 0 at center
    float posInv = 1 - pos;
    float spotAnim = Time / 200.0;
    float value = 1.1;
    //    float value = mod(spotAnim, 1.0);
    //        vec2 rand = FaceMod * 100;
    vec2 offsetTc = texCoord0 - center;
    vec3 coord = vec3(atan(offsetTc.x, offsetTc.y)/6.2832+.5, length(offsetTc)*.4, .5);
    coord += vec3(0., Time*.05, Time*.01);
    //        coord.xy += rand;

    value += snoise(vec3(coord.x, coord.y, 0), 1 * 32) * (1.5 / 1) * max(0, posInv - 0.1) * 1.125;
    value += snoise(vec3(coord.x, coord.y, 0), 2 * 32) * (1.5 / 2) * max(0, posInv - 0.1) * 1.125;
    value += snoise(vec3(coord.x, coord.y, 0), 4 * 32) * (1.5 / 4) * max(0, posInv - 0.1) * 1.125;

    // Controls the fade out to nothing at the center
    float fade = pow(posInv + 1, 6);
    value -= (fade - 1) / 20;

    if (value >= 0.50 && posInv > 0.07) {
        float res = 0.5;//0.15;
        if (mod(value, res) > res / 2.0 && value < ((Activation - 0.5) * 2)) {
            float cVal = min((value) * 2, 1);
//            vec3 r = vec3(1) - EffectColour;
            vec3 c = vec3(cVal, cVal, cVal) * EffectColour;
            //            c.r += min(r.r, r.r * (value-0.5) * 0.8);
            //            c.g += min(r.g, r.g * (value-0.5) * 0.8);
            //            c.b += min(r.b, r.b * (value-0.5) * 0.8);
//            return vec3(min(1, (value-0.5)), cVal, cVal) * EffectColour;
                        return c;
        } else {
            discard;
        }
    } else {
        discard;
    }

    //    if (value >= 0.50 && posInv > 0.07) {
    //        float res = 0.15;
    //        if (mod(value, res) < res / 2.0) {
    //            float cVal = min((value) * 2, 1);
    //            return vec4(0, cVal, cVal, 1);
    //        } else {
    //            discard;
    //        }
    //    } else {
    //        discard;
    //    }


    //    return vec4(0, 0, 0, 0);
}

vec3 rotatingTriEffect(float shapeInv) {
    //Render outer solid segment
    vec2 dirvec = center - texCoord0;
    float angle = M_PI + atan(dirvec.y, dirvec.x);
    angle /= (M_PI * 2);

    if (shapeInv >= edgeOut && shapeInv <= edgeOut + edgeCore) {
        if (angle > (Activation - 0.5) * 2) discard;
        angle += Time / 100.0;
        float angleStep = floor(angle * edgeSteps) / edgeSteps;
        float colourMod = -0.3 + (random(angleStep * 10) * 0.3);
        //        return vec4(colourMod, 0, 0, 1);
        //        return vec4(vec3(1, 0, 0) + colourMod, 1);
        return InnerTriColour + colourMod;//Rotating Core Colour
    } else {
        if (angle > Activation * 2) discard;
        float angleStep = floor(angle * edgeSteps) / edgeSteps;
        float colourMod = -0.1 + (random(angleStep * 10) * 0.2);
        //        return vec4(0, randCol, randCol, 1);
        return FrameColour + colourMod;//Edge Colours
        //        return vec4(vec3(0, 1, 1) + colourMod, 1);
    }
}


//Triangle top left
//Tex 0,0 is bottom right
void main() {
    if (texCoord0.x + texCoord0.y < 1) {
        discard;//Discard unseen half of texture
    }

    float shape = shapeTriangle();
    float shapeInv = 1.0 - shape;

    //Customizable Colours? Colourization module?

    if (shapeInv < edgeWidth) {
        fragColor = vec4(rotatingTriEffect(shapeInv), 1);
    } else {
        //        fragColor = chaos(Time * 20, TestInB, TestInC, 0.5, FaceMod, fPos, Sampler0);
        //        fragColor += shieldEffect(shape);
        fragColor = vec4(shieldEffect(shape), 1);
    }
}


//vec4 debugBands(float val, float res) {
//    vec4 colA = val > 1 ? vec4(val - 1.0, 0, 0, 1) : val < 0 ? vec4(0, 0, 0 + (val * -1), 1) : vec4(0, val, 0, 1);
//    vec4 colB = val > 1 ? vec4(0.5, 0, 0, 1) : val < 0 ? vec4(0, 0, 0.5, 1) : vec4(0.5, 0.5, 0, 1);
//
//    if (mod(val, res) > res / 2.0) {
//        return colA;
//    } else {
//        return colB;
//    }
//}



















