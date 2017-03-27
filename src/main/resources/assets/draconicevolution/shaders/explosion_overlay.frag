#version 120

uniform vec2 screenPos;
uniform vec2 screenSize;
uniform float intensity;

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

float noise(float t)
{
	return snoise(vec3(t), 2);
}

vec3 lensflare(vec2 uv,vec2 pos)
{
	vec2 main = uv-pos;
	vec2 uvd = uv*(length(uv));

	float ang = atan(main.x,main.y);

	float f0 = 1.0/(length(uv-pos)*16.0+2.5);
	f0 *= min(1.0, intensity * 10.0);

	f0 = f0+f0*(sin(noise((pos.x+pos.y)*2.2+ang*4.0+5.954)*16.0)*.1+.8);
	f0 += intensity;

	float f2 = max(1.0/(1.0+32.0*pow(length(uvd+0.8*pos),2.0)),.0)*00.25;
	float f22 = max(1.0/(1.0+32.0*pow(length(uvd+0.85*pos),2.0)),.0)*00.23;
	float f23 = max(1.0/(1.0+32.0*pow(length(uvd+0.9*pos),2.0)),.0)*00.21;
	vec3 c = vec3(.0);
	c = c*1.3 - vec3(length(uvd)*.05);
	c+=vec3(f0);

	return c;
}

vec3 cc(vec3 color, float factor,float factor2)
{
	float w = color.x+color.y+color.z;
	return mix(color,vec3(w)*factor,w*factor2);
}

void main() {
	vec2 uv = gl_FragCoord.xy / screenSize.xy - 0.5;
	uv.x *= screenSize.x/screenSize.y; //fix aspect ratio
	vec3 flarPos = vec3(screenPos.x - 0.5, screenPos.y - 0.5, 0.5);
	flarPos.x *= screenSize.x/screenSize.y; //fix aspect ratio

	vec3 color = vec3(1.4,1.2,1.0)*lensflare(uv, flarPos.xy);
	color -= .015;
	color = cc(color,.5,.1);
	float alpha = (color.x + color.y + color.z) / 3;
	gl_FragColor = vec4(color, alpha);
}
