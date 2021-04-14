#version 120

uniform float time;
uniform int tier;
uniform vec4 baseColour;
varying vec3 position;

//vec4 type = vec4[](vec4(0.0, 0.5, 0.8, 1), vec4(0.55, 0.0, 0.65, 1), vec4(0.8 ,0.5 ,0.1, 1), vec4(0.75 ,0.05 ,0.05, 0.2))[tier];

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

    coord.x += (position.x / 10);
    coord.y += (position.y / 10);

    float density = 8;
    float brightness = baseColour.w;
    for(int i = 1; i <= 5; i++) {
        float power = pow(2, float(i));
        brightness += (2 / power) * max(snoise(coord + vec3(time * -0.05, time * 0.16, time * -0.02), power * density), -1);
    }
    brightness = max(brightness, 0);

    vec4 colour = vec4(pow(brightness * baseColour.r, 3 * (1.0-baseColour.r)), pow(brightness * baseColour.g, 3*(1.0-baseColour.g)), pow(brightness * baseColour.b, 3*(1.0-baseColour.b)), 1.0);
	gl_FragColor = colour;
}
