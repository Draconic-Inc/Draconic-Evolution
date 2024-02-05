#version 150

#moj_import <fog.glsl>
#moj_import <brandonscore:math.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

uniform vec4 BaseColor;

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
    vec3 coord = vec3(texCoord0, 0.0);
    coord += vNorm.xyz * 0.2;

    float colour = 0;

    float rad = (distance(coord.xy, vec2(0.5, 0.34375)) * 5);

    float density = 8;
    float noise = 0;
    for (int i = 1; i <= 5; i++) {
        float power = pow(2, float(i));
        noise += (3 / power) * max(snoise((coord * 0.5) + vec3(0, 0, Time * -0.02), power * density), -1);
    }
    noise *= 0.5;

    float t1 = -1 + mod(Time, 8.0) + noise;
    float t2 = -1 + mod(Time + 2, 8.0) + noise;
    float t3 = -1 + mod(Time + 4, 8.0) + noise;
    float t4 = -1 + mod(Time + 6, 8.0) + noise;

    colour = min(min(abs(rad - t1), abs(rad - t2)), min(abs(rad - t3), abs(rad - t4)));
    colour = max(0, 1. - pow(colour, .5));

    vec4 color =vec4(BaseColor.rgb * colour, 1);

    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
