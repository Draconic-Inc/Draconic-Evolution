#version 150

uniform float time;
uniform vec4 baseColour;
varying vec3 position;
uniform float activation;


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

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec3 coord = vec3(gl_TexCoord[0]);
    float colour = 0;

    //Less than 0 disables pixel
    float rad = (distance(coord.xy, vec2(0.5, 0.34375)) * 5);
    float level = clamp((coord.x - activation) * 1024, 0, 1);

    float density = 8;
    float noise = 0;
    for(int i = 1; i <= 5; i++) {
        float power = pow(2, float(i));
        noise += (2 / power) * max(snoise((coord * 0.5) + vec3(time * 0.0, time * 0.00, time * -0.01), power * density), -1);
    }
    noise *= 0.5;

    float t1 = -1 + mod(time, 6.0) + noise;
    float t2 = -1 + mod(time + 2, 6.0) + noise;
    float t3 = -1 + mod(time + 4, 6.0) + noise;

    colour = min(min(abs(rad - t1), abs(rad - t2)), abs(rad - t3));
    colour += level;

    gl_FragColor = vec4(baseColour.rgb, max(0.1, 1. - pow(colour, .3)) * (1 - level) * baseColour.a);
}
