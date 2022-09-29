#version 150

#moj_import <brandonscore:math.glsl>

vec3 explode(vec2 uv, vec2 pos, float anim, float maxRad) {
    float rad = anim * maxRad;
    vec3 color = vec3(0);

    //Inner Radious
    float ir = clamp((-maxRad * anim) + maxRad, 0.0, maxRad);
    color = vec3(.45, .05, .02);

    //Base Background colour Circle.
    vec3 c = circle(uv, pos, rad, 0.04 * maxRad, color);

    //Inner Circle
    c -= circle(uv, pos, rad, ir, color);
    c *= 2.;

    //Initial Explosion Flash
    c += circle(uv, pos, (-maxRad * anim) + (.85 * maxRad), (.8 * maxRad), vec3(1.0, .84, .23));
    return max(vec3(0), c);
}

vec4 chaos(sampler2D sampler0, float time, float yaw, float pitch, float alpha, vec3 fPos, vec2 posMod) {
    vec4 col = vec4(0, 0, 0, 1);//vec4(0.047, 0.035, 0.063, 1) + (snoise(gl_FragCoord.xy) * vec4(0.02, 0.02, 0.02, 1));

    // get ray from camera to fragment
    vec4 dir = normalize(vec4(-fPos, 0));

    // rotate the ray to show the right bit of the sphere for the angle
    float sb = sin(pitch);
    float cb = cos(pitch);
    dir = normalize(vec4(dir.x, dir.y * cb - dir.z * sb, dir.y * sb + dir.z * cb, 0));

    float sa = sin(-yaw);
    float ca = cos(-yaw);
    dir = normalize(vec4(dir.z * sa + dir.x * ca, dir.y, dir.z * ca - dir.x * sa, 0));

    vec4 ray;

    // draw the layers
    for (int i=0; i<16; i++) {
        int mult = 16-i;

        // get semi-random stuff
        int j = i + 7;
        float rand1 = (j * j * 4321 + j * 8) * 2.0F;
        int k = j + 1;
        float rand2 = (k * k * k * 239 + k * 37) * 3.6F;
        float rand3 = rand1 * 347.4 + rand2 * 63.4;

        // random rotation matrix by random rotation around random axis
        vec3 axis = normalize(vec3(sin(mod(rand1, 2*M_PI)), sin(mod(rand2, 2*M_PI)), cos(mod(rand3, 2*M_PI))));

        // apply
        ray = dir * rotationMatrix(axis, mod(rand3, 2*M_PI));

        // calcuate the UVs from the final ray
        float u = 0.5 + (atan(ray.z, ray.x)/(2*M_PI)) + posMod.x;
        float v = 0.5 + (asin(ray.y)/M_PI) + posMod.y;

        // get UV scaled for layers and offset by time;
        float scale = mult*0.5 + 2.75;
        vec2 tex = vec2(u * scale, (v + time * 0.00006) * scale * 0.6);

        // sample the texture
        vec4 tcol = texture(sampler0, tex);

        // set the alpha, blending out at the bunched ends
        float a = tcol.r * (0.05 + (1.0/mult) * 0.65) * (1-smoothstep(0.15, 0.48, abs(v-0.5)));

        // get end-portal-y colors
        float r = (mod(rand1, 35.0)/35.0) * 0.5 + 0.5;
        float g = (mod(rand2, 5.0)/5.0) * 0.1 + 0.05;
        float b = (mod(rand1, 2.0)/2.0) * 0.1 + 0.05;

        // add Splosions!
        for (int i2 = 1; i2 < 4; i2++) {
            float t = mod(time + (mult * 234.234), 2000);
            float expTime = (t / 30) + (4.2424242 * i * i2);
            float expPosRand = floor(expTime) * i2;

            vec3 exc = explode(vec2(u, v), randVec2(expPosRand), mod(expTime, 1.0), 0.003 * mult);
            r += exc.r * (32 - mult);
            g += exc.g * (32 - mult);
            b += exc.b * (32 - mult);
        }

        // mix the colors
        col = col*(1-a) + vec4(r, g, b, 1)*a;
    }

    float br = clamp(2.5*gl_FragCoord.w + alpha, 0, 1);

    col = col * br + vec4(0.047, 0.035, 0.063, 1) * (1-br);

    // increase the brightness of flashing ducts
    col.rgb = clamp(col.rgb * (1+alpha*4), 0, 1);

    col.a = 1;

    return col;
}
