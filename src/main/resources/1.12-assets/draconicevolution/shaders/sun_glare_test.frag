#version 120
//
#define M_PI 3.1415926535897932384626433832795

//
uniform sampler2D texture;
uniform float time;

uniform float yaw;
uniform float pitch;

uniform float displayW;
uniform float displayH;
//
varying vec3 position;
//
uniform float alpha;
//
//mat4 rotationMatrix(vec3 axis, float angle)
//{
//
//
//    axis = normalize(axis);
//    float s = sin(angle);
//    float c = cos(angle);
//    float oc = 1.0 - c;
//
//    return mat4(oc * axis.x * axis.x + c,           oc * axis.x * axis.y - axis.z * s,  oc * axis.z * axis.x + axis.y * s,  0.0,
//                oc * axis.x * axis.y + axis.z * s,  oc * axis.y * axis.y + c,           oc * axis.y * axis.z - axis.x * s,  0.0,
//                oc * axis.z * axis.x - axis.y * s,  oc * axis.y * axis.z + axis.x * s,  oc * axis.z * axis.z + c,           0.0,
//                0.0,                                0.0,                                0.0,                                1.0);
//}
//
//vec3 mod289(vec3 x) {
//  return x - floor(x * (1.0 / 289.0)) * 289.0;
//}
//
//vec2 mod289(vec2 x) {
//  return x - floor(x * (1.0 / 289.0)) * 289.0;
//}
//
//vec3 permute(vec3 x) {
//  return mod289(((x*34.0)+1.0)*x);
//}
//
//float snoise(vec2 v)
//  {
//  const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0
//                      0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
//                     -0.577350269189626,  // -1.0 + 2.0 * C.x
//                      0.024390243902439); // 1.0 / 41.0
//// First corner
//  vec2 i  = floor(v + dot(v, C.yy) );
//  vec2 x0 = v -   i + dot(i, C.xx);
//
//// Other corners
//  vec2 i1;
//  //i1.x = step( x0.y, x0.x ); // x0.x > x0.y ? 1.0 : 0.0
//  //i1.y = 1.0 - i1.x;
//  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
//  // x0 = x0 - 0.0 + 0.0 * C.xx ;
//  // x1 = x0 - i1 + 1.0 * C.xx ;
//  // x2 = x0 - 1.0 + 2.0 * C.xx ;
//  vec4 x12 = x0.xyxy + C.xxzz;
//  x12.xy -= i1;
//
//// Permutations
//  i = mod289(i); // Avoid truncation effects in permutation
//  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
//		+ i.x + vec3(0.0, i1.x, 1.0 ));
//
//  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
//  m = m*m ;
//  m = m*m ;
//
//// Gradients: 41 points uniformly over a line, mapped onto a diamond.
//// The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)
//
//  vec3 x = 2.0 * fract(p * C.www) - 1.0;
//  vec3 h = abs(x) - 0.5;
//  vec3 ox = floor(x + 0.5);
//  vec3 a0 = x - ox;
//
//// Normalise gradients implicitly by scaling m
//// Approximation of: m *= inversesqrt( a0*a0 + h*h );
//  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );
//
//// Compute final noise value at P
//  vec3 g;
//  g.x  = a0.x  * x0.x  + h.x  * x0.y;
//  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
//  return 130.0 * dot(m, g);
//}

vec3 doSample(vec2 uv, float t) {
//
//    float dx = uv.x - 0.5;
//    float dy = uv.y - 0.5;
//    float offset = 0;

    vec4 col = texture2D(texture, uv);

//    if ((dx * dx + dy * dy) < 0.001) {
//        offset = 40;
//    }

//    vec4 col = vec4(max(0.5 - ((dx * dx + dy * dy)), 0.3));//texture2D(texture, uv);



    if((col.r + col.g + col.b) / 3.0 < t) {
        return vec3(0.0);
    }

    col = clamp(col - t, 0.0, 1.0);
    return col.xyz;
}

void main() {
    // background colour
//
//    vec4 col = vec4(0.047, 0.035, 0.063, 1) + snoise(gl_FragCoord.xy) * vec4(0.0196, 0.0216, 0.0235, 0);
//
//    // get ray from camera to fragment
//    vec4 dir = normalize(vec4( -position, 0));
//
//	// rotate the ray to show the right bit of the sphere for the angle
//	float sb = sin(pitch);
//	float cb = cos(pitch);
//	dir = normalize(vec4(dir.x, dir.y * cb - dir.z * sb, dir.y * sb + dir.z * cb, 0));
//
//	float sa = sin(-yaw);
//	float ca = cos(-yaw);
//	dir = normalize(vec4(dir.z * sa + dir.x * ca, dir.y, dir.z * ca - dir.x * sa, 0));
//
//	vec4 ray;
//
//	// draw the layers
//	for (int i=0; i<16; i++) {
//		int mult = 16-i;
//
//		// get semi-random stuff
//		int j = i + 7;
//		float rand1 = (j * j * 4321 + j * 8) * 2.0F;
//		int k = j + 1;
//		float rand2 = (k * k * k * 239 + k * 37) * 3.6F;
//		float rand3 = rand1 * 347.4 + rand2 * 63.4;
//
//		// random rotation matrix by random rotation around random axis
//		vec3 axis = normalize(vec3(sin(mod(rand1, 2*M_PI)), sin(mod(rand2, 2*M_PI)) , cos(mod(rand3, 2*M_PI))));
//
//		// apply
//		ray = dir * rotationMatrix(axis, mod(rand3, 2*M_PI));
//
//		// calcuate the UVs from the final ray
//		float u = 0.5 + (atan(ray.z,ray.x)/(2*M_PI));
//		float v = 0.5 + (asin(ray.y)/M_PI);
//
//		// get UV scaled for layers and offset by time;
//		float scale = mult*0.5 + 2.75;
//		vec2 tex = vec2( u * scale, (v + time * 0.00006) * scale * 0.6 );
//
//		// sample the texture
//		vec4 tcol = texture2D(texture, tex);
//
//		// set the alpha, blending out at the bunched ends
//		float a = tcol.r * (0.05 + (1.0/mult) * 0.65) * (1-smoothstep(0.15, 0.48, abs(v-0.5)));
//
//		// get end-portal-y colours
//		float r = (mod(rand1, 29.0)/29.0) * 0.5 + 0.1;
//    	float g = (mod(rand2, 35.0)/35.0) * 0.5 + 0.4;
//    	float b = (mod(rand1, 17.0)/17.0) * 0.5 + 0.5;
//
//		// mix the colours
//		col = col*(1-a) + vec4(r,g,b,1)*a;
//	}
//
//	float br = clamp(2.5*gl_FragCoord.w + alpha,0,1);
//
//    col = col * br + vec4(0.047, 0.035, 0.063, 1) * (1-br);
//
//    // increase the brightness of flashing ducts
//    col.rgb = clamp(col.rgb * (1+alpha*4),0,1);
//
//    col.a = 1;
//
////
////	vec2 tex = vec2(500, 800);
////	// sample the texture
//	vec4 tcol = texture2D(texture, vec2(gl_FragCoord.x / displayW, gl_FragCoord.y / displayH));
//    tcol.a = 1;



    int samples = 256;
    float threshold = 0.2;
    float intensity = 1.4;

	vec2 uv = vec2(gl_FragCoord.x / displayW, gl_FragCoord.y / displayH);
	vec4 outColour = texture2D(texture, uv);
//	vec4 outColour = vec4(0.0, 0.0, 0.0, 0.0);//texture2D(texture, uv);

    vec2 lightSource = vec2(0.5);//vec2(.25+.25*cos(time*1.5),.75+.15*sin(time));

    vec3 sum 	 = vec3(0, 0, 0);
    float weight = 1.0 / float(samples);
    for(int i = 0; i < samples; i++) {
        vec2 dir = lightSource - uv;

//        float minDir = 0;
//        if (abs(dir.x) < minDir && abs(dir.y) < minDir) {
//            continue;
//        }

        sum += doSample(uv, threshold);

        uv += dir * 0.01;
    }

    outColour.rgb += sum * weight * intensity;
    outColour.a = 1;
//    outColour.a = ((outColour.x + outColour.y + outColour.z) / 3.0);
//
    gl_FragColor = outColour;
}

//void main() {
////         gl_FragColor = vec4(position.x, position.y, position.z, 0.5);
//
//
//
//    gl_FragColor = vec4(texture.x, position.y, position.z, 0.5);
//}