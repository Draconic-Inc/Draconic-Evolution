#version 120

uniform float time;
uniform int type;
uniform float mipmap;
uniform vec2 angle;
varying vec3 position;

vec3 types[3] = vec3[](vec3(0.0, 0.2, 0.3), vec3(0.47, 0.0, 0.58), vec3(1.0 ,0.4 ,0.1));


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
    float intensity = 1;
    vec3 coord = vec3(gl_TexCoord[0]);
    float timeOffset = time / 15.0;

    coord.x += timeOffset + (position.x / 10);// - (angle.x * 2);
    coord.y += (position.y / 10);

    float density = 15;//float(int(15.0 * mipmap));
    float brightness = 1;
    for(int i = 1; i <= 7; i++)
    {
        float power = pow(2, float(i));
        brightness += (1.5 / power) * max(snoise(coord + vec3(0, 0, time * 0.01), power * density), -1);
    }

    brightness = ((1 - mipmap) * brightness) + (mipmap * 1.2) ;//max(brightness, mipmap);

    vec4 colour = vec4(brightness * types[type].r, pow(brightness, 2.0) * types[type].g, pow(brightness ,3.0) * types[type].b, 1.0);
	gl_FragColor = colour;
}
