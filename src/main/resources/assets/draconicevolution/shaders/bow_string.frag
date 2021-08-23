#version 120

uniform float time;
uniform int tier;
varying vec3 position;

vec4 type = vec4[](vec4(0.1, 0.5, 0.8, 1), vec4(0.55, 0.25, 0.65, 1), vec4(0.7 ,0.4 ,0.2, 1), vec4(0.55 ,0.2 ,0.1, 0.2))[tier];

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

    float yPos = coord.y;

    float xDist = coord.x = abs(coord.x - 0.5) * 0.8;

    float brightness = (0.4 - xDist) * 8;

    float flair = (snoise(vec3(0, (coord.y * 0.05) + (time * -0.2), 0), 20) * 0.5);
    brightness *= max(1, 1 + flair);
    brightness = max(brightness, 1);

    gl_FragColor = vec4(pow(brightness * type.r, 3 * (1.0-type.r)), pow(brightness * type.g, 3*(1.0-type.g)), pow(brightness * type.b, 3*(1.0-type.b)), (0.35 - xDist) * 10);
}
