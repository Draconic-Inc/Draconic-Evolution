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

const float ils = -3; //Inner layer speed
const float ols = 6;  //Outer layer speed

void main() {
    vec3 coord = vec3(texCoord0, 0.0);
    coord.x *= 3.5;//X Compression factor
    coord.y *= 8;//Y Compression factor

    float brightness = 6.0 * coord.x;
    coord.y += coord.x * 0.5;

    float density = 8;
    for (int i = 1; i <= 5; i++) {
        float power = pow(2, float(i));
        brightness += (2 / power) * max(snoise(coord + vec3(Time * 0.01 * ils, Time * -0.03 * ils, Time * 0.02), power * density), -1);

        vec3 oc = vec3(coord.xyz);
        for (float l = 1; l <= 2; l++) {
            float od = 8 + (l * 3);
            oc.xy *= 1.5;
            brightness += (1 / power) * max(snoise(oc + vec3(Time * -0.02 * (l + 1) * ols, Time * 0.02 * (l + 1) * ols, Time * -0.02), power * od), -1);
        }
    }

    brightness = max(brightness, 0) * ((BaseColor.w / 0.2) * 0.5);
    vec4 color = vec4(pow(brightness * BaseColor.r, 3 * (1.0-BaseColor.r)), pow(brightness * BaseColor.g, 3*(1.0-BaseColor.g)), pow(brightness * BaseColor.b, 3*(1.0-BaseColor.b)), 1.0);

    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
