#version 150

#moj_import <fog.glsl>
#moj_import <brandonscore:math.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

uniform vec4 BaseColor;
uniform float Activation;
uniform float Time;

in vec3 fPos;
in vec3 vPos;
in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;
in vec3 vNorm;

out vec4 fragColor;

void main() {
    vec3 coord = vec3(texCoord0, 0);
    float value = 0;

    float rad = (distance(coord.xy, vec2(0.5, 0.34375)) * 5);
    float level = max(0, (rad - (Activation * 5)) * 1024);

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

    value = min(min(abs(rad - t1), abs(rad - t2)), abs(rad - t3));
    value += level;

    vec4 colour = vec4(BaseColor.rgb, max(0.1, 1. - pow(value, .3)) * (1 - level) * BaseColor.a);

    colour *= vertexColor * ColorModulator;
    colour.rgb = mix(overlayColor.rgb, colour.rgb, overlayColor.a);
    colour *= lightMapColor;

    fragColor = linear_fog(colour, vertexDistance, FogStart, FogEnd, FogColor);
}
