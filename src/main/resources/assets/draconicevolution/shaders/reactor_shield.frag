#version 120

uniform float time;
uniform float intensity;// In this case field power
uniform sampler2D texture;
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
    float power = intensity;//(sin(time) + 1) / 2;

    vec3 coord = vec3(gl_TexCoord[0]);
    float yModifier = max(0, max(1 - (coord.y * 10), (coord.y - 0.9) * 10));
    float brightness = 1.2 + (yModifier * -2) + 1 - min(1, power * 3);  //(1.4 + (yModifier * 1.2 * iCap)) * max(-0.4, intensity);

    coord.x += time / 10;
    coord.y += time / 5;
    coord.x -= coord.y / 6;
    coord.y *= 0.2;

    float density = 31;
    for(int i = 1; i <= 7; i++)
    {
        float power = pow(2, float(i));
        brightness += (1.5 / power) * max(snoise(coord + vec3(0, 0, time * 0.006), power * density), -1);//0.006 controls animation speed
    }

    float po = max(brightness, 0.8);
    float r = (0.5 - (power * 2));
    float g = power + yModifier * 0.5;
    float b = (power * 2) + yModifier * 0.5;

    vec4 colour = vec4(po * r, pow(po, 2.0) * g, pow(po ,3.0) * b, max(0.1, 2 - brightness));

	gl_FragColor = colour;
}
