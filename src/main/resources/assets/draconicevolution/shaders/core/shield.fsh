#version 150

#moj_import <brandonscore:math.glsl>

uniform float Time;
uniform float Activation;
uniform vec4 BaseColour;
uniform int BarMode;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec3 coord = vec3(texCoord0, 0);
    float colour = 0;

    float rad = (distance(coord.xy, vec2(0.5, 0.34375)) * 5);
    float level = BarMode == 0 ? max(0, (rad - (Activation * 5)) * 1024) : clamp((coord.x - Activation) * 1024, 0, 1);

    float density = 8;
    float noise = 0;
    for(int i = 1; i <= 5; i++) {
        float power = pow(2, float(i));
        noise += (2 / power) * max(snoise((coord * 0.5) + vec3(Time * 0.0, Time * 0.00, Time * -0.01), power * density), -1);
    }
    noise *= 0.5;

    float t1 = -1 + mod(Time, 6.0) + noise;
    float t2 = -1 + mod(Time + 2, 6.0) + noise;
    float t3 = -1 + mod(Time + 4, 6.0) + noise;

    colour = min(min(abs(rad - t1), abs(rad - t2)), abs(rad - t3));
    colour += level;

    fragColor = vec4(BaseColour.rgb, max(0.1, 1. - pow(colour, .3)) * (1 - level) * BaseColour.a);
}
