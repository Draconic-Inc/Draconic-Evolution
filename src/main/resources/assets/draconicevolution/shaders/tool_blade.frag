#version 120

uniform float time;
uniform int tier;
uniform vec4 baseColour;
varying vec3 position;

//vec4 type = vec4[](vec4(0.0, 0.5, 0.8, 1), vec4(0.55, 0.0, 0.65, 1), vec4(0.8 ,0.5 ,0.1, 1), vec4(0.75 ,0.05 ,0.05, 0.5))[tier];

float ils = -3; //Inner layer speed
float ols = 6;  //Outer layer speed

float snoise(vec3 uv, float res){
    const vec3 s = vec3(1e0, 1e2, 1e3);
    uv *= res;
    vec3 uv0 = floor(mod(uv, res))*s;
    vec3 uv1 = floor(mod(uv+vec3(1.0), res))*s;
    vec3 f = fract(uv);
    f = f*f*(3.0-2.0*f);
    vec4 v = vec4(uv0.x+uv0.y+uv0.z, uv1.x+uv0.y+uv0.z, uv0.x+uv1.y+uv0.z, uv1.x+uv1.y+uv0.z);
    vec4 r = fract(sin(v*1e-1)*1e3);
    float r0 = mix(mix(r.x, r.y, f.x), mix(r.z, r.w, f.x), f.y);
    r = fract(sin((v + uv1.z - uv0.z)*1e-1)*1e3);
    float r1 = mix(mix(r.x, r.y, f.x), mix(r.z, r.w, f.x), f.y);
    return mix(r0, r1, f.z) * 2.0 - 1.0;
}


void main() {
    vec3 coord = vec3(gl_TexCoord[0]);
    coord.x *= 3.5; //X Compression factor
    coord.y *= 8; //Y Compression factor

    float brightness = 6.0 * coord.x;
    coord.y += coord.x * 0.5;

    float density = 8;
    for(int i = 1; i <= 5; i++) {
        float power = pow(2, float(i));
        brightness += (2 / power) * max(snoise(coord + vec3(time * 0.01 * ils, time * -0.03 * ils, time * 0.02), power * density), -1);

        vec3 oc = vec3(coord.xyz);
        for(float l = 1; l <= 2; l++) {
           float od = 8 + (l * 3);
           oc.xy *= 1.5;
           brightness += (1 / power) * max(snoise(oc + vec3(time * -0.02 * (l + 1) * ols, time * 0.02 * (l + 1) * ols, time * -0.02), power * od), -1);
        }
    }

    brightness = max(brightness, 0) * ((baseColour.w / 0.2) * 0.5);
    gl_FragColor = vec4(pow(brightness * baseColour.r, 3 * (1.0-baseColour.r)), pow(brightness * baseColour.g, 3*(1.0-baseColour.g)), pow(brightness * baseColour.b, 3*(1.0-baseColour.b)), 1.0);
}