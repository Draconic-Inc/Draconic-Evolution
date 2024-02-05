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

    float yPos = coord.y;

    float xDist = coord.x = abs(coord.x - 0.5) * 0.8;

    float brightness = (0.4 - xDist) * 8;

    float flair = (snoise(vec3(0, (coord.y * 0.05) + (Time * -0.2), 0), 20) * 0.5);
    brightness *= max(1, 1 + flair);
    brightness = max(brightness, 1);

    vec4 color = vec4(pow(brightness * BaseColor.r, 3 * (1.0-BaseColor.r)), pow(brightness * BaseColor.g, 3*(1.0-BaseColor.g)), pow(brightness * BaseColor.b, 3*(1.0-BaseColor.b)), (0.35 - xDist) * 10);

    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
