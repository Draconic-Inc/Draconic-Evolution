#version 150

uniform float time;
uniform float scale;
uniform float alpha;
#define M_PI 3.1415926535897932384626433832795


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
    float power = 0.5;

    vec3 tCoord = vec3(gl_TexCoord[0]);
    vec2 p = -.5 + tCoord.xy;
    float yModifier = abs(tCoord.y - 0.5) * (4 - scale);

    float brightness = (yModifier * 2) + (3.*length(yModifier));

    vec3 coord = vec3(p.x * 6, length(p.y)*0.5, .5);

    for (int i = 1; i <= 7; i++)
    {
        float power = pow(2.0, float(i));
        brightness += (1.5 / power) * snoise(coord + vec3(0., -time*.05, time*.01), power*16.);
    }

    gl_FragColor = vec4(brightness, pow(max(brightness, 0.), 2.)*0.4, pow(max(brightness, 0.), 3.)*0.15, max(0, 2 - brightness) * alpha);
}
