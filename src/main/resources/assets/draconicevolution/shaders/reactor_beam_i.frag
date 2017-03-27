#version 120

uniform float time;
uniform float power;
uniform float fade;
uniform float startup;
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
    vec3 tcoord = vec3(gl_TexCoord[0]);
	float color = 0.8 - ((1 - power) * 1.5);// + (1 * p.y);
	vec3 coord = tcoord;

    coord.x += time * 0.5;
	coord.y += time * 2;
    coord.y += fade;
    coord.y *= 0.2;

    float density = 8;
	for(int i = 1; i <= 7; i++)
	{
		float sum = pow(2.0, float(i));
		color += (1.5 / sum) * snoise(coord + vec3(0, 0, time*0.05), sum*density);
	}
	color *= 4 * (1.25 - power);
	color -= tcoord.y * fade * 15;// + 60.5;
    color -= (1 - tcoord.y) * (1 - tcoord.y) * 1.5 * (1 - fade);// + 60.5;

    float co = abs(1 - (color * 2));
	float r = 0.4 * color;
    float g = 2;
    float b = 4;
    color *= startup;
    co *= startup;
    vec4 c = vec4(co * r, pow(co, 2.0) * g, pow(co ,3.0) * b, max((color - 0.25) * 1.5, 0.2 * tcoord.y * startup));//0.05

    gl_FragColor = c;
}
