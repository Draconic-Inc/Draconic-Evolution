#version 150

#moj_import <brandonscore:math.glsl>

in vec4 vertexColor;

//uniform vec4 ColorModulator;
uniform vec2 ScreenPos;
uniform vec2 ScreenSize;
uniform float Intensity;

out vec4 fragColor;

float noise(float t)
{
    return snoise(vec3(t), 2);
}

vec3 lensflare(vec2 uv, vec2 pos)
{
    vec2 main = uv-pos;
    vec2 uvd = uv*(length(uv));

    float ang = atan(main.x, main.y);

    float f0 = 1.0/(length(uv-pos)*16.0+2.5);
    f0 *= min(1.0, Intensity * 10.0);

    f0 = f0+f0*(sin(noise((pos.x+pos.y)*2.2+ang*4.0+5.954)*16.0)*.1+.8);
    f0 += Intensity;

    float f2 = max(1.0/(1.0+32.0*pow(length(uvd+0.8*pos), 2.0)), .0)*00.25;
    float f22 = max(1.0/(1.0+32.0*pow(length(uvd+0.85*pos), 2.0)), .0)*00.23;
    float f23 = max(1.0/(1.0+32.0*pow(length(uvd+0.9*pos), 2.0)), .0)*00.21;
    vec3 c = vec3(.0);
    c = c*1.3 - vec3(length(uvd)*.05);
    c+=vec3(f0);

    return c;
}

vec3 cc(vec3 color, float factor, float factor2)
{
    float w = color.x+color.y+color.z;
    return mix(color, vec3(w)*factor, w*factor2);
}

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize.xy - 0.5;
    uv.x *= ScreenSize.x/ScreenSize.y;//fix aspect ratio
    vec3 flarPos = vec3(ScreenPos.x - 0.5, ScreenPos.y - 0.5, 0.5);
    flarPos.x *= ScreenSize.x/ScreenSize.y;//fix aspect ratio

    vec3 color = vec3(1.4, 1.2, 1.0)*lensflare(uv, flarPos.xy);
    color -= .015;
    color = cc(color, .5, .1);
    float alpha = (color.x + color.y + color.z) / 3;
    fragColor = vec4(color, alpha);


    //    vec4 color = vertexColor;
        if (fragColor.a == 0.0) {
            discard;
        }
    //    fragColor = color * ColorModulator;
}
