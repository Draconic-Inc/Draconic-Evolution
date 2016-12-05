#version 120

uniform float time;
uniform float intensity;


float snoise(vec3 uv, float res){
    const vec3 s = vec3(1e0, 1e2, 1e3);
    uv *= res;
    vec3 uv0 = floor(mod(uv, res))*s;
    vec3 uv1 = floor(mod(uv+vec3(1.), res))*s;
    vec3 f = fract(uv);
    f = f*f*(3.0-2.0*f);
    vec4 v = vec4(uv0.x+uv0.y+uv0.z, uv1.x+uv0.y+uv0.z, uv0.x+uv1.y+uv0.z, uv1.x+uv1.y+uv0.z);
    vec4 r = fract(sin(v*1e-1)*1e3);
    float r0 = mix(mix(r.x, r.y, f.x), mix(r.z, r.w, f.x), f.y);
    r = fract(sin((v + uv1.z - uv0.z)*1e-1)*1e3);
    float r1 = mix(mix(r.x, r.y, f.x), mix(r.z, r.w, f.x), f.y);
    return mix(r0, r1, f.z)*2.-1.;
}

void main() {
    vec3 coord = vec3(gl_TexCoord[0]);
    float iCap = 1;//max(0, min(1, intensity));
    float yModifier = max(0, max(0.5 - (coord.y * 5), (coord.y - 0.8) * 5) - 0.5);
    float brightness = 1.2;//(1.4 + (yModifier * 1.2 * iCap)) * max(-0.4, intensity);
    float offline = 0;//min(0, intensity) * -1;

    float density = 40;
    for(int i = 1; i <= 7; i++)
    {
        float power = pow(2, float(i));
        brightness += (1.5 / power) * max(snoise(coord + vec3(0, 0, time * 0.01), power * density), -1 + (offline * 1.5));
    }

    float r = 0.1;
    float g = 1;
    float b = 1;

    vec4 colour = vec4(brightness * r, pow(brightness, 2.0) * g * iCap, pow(brightness ,3.0) * b * iCap, max(0.1, 2 - brightness));

	gl_FragColor = colour;
}
