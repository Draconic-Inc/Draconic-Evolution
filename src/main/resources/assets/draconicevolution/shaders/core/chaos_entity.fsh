#version 150

#moj_import <fog.glsl>
#moj_import <draconicevolution:math.glsl>
#moj_import <draconicevolution:chaos.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

uniform float Time;
uniform float Yaw;
uniform float Pitch;
uniform float Alpha;

in vec3 fPos;
in float vertexDistance;
in vec4 vertexColor;
in vec4 lightMapColor;
in vec4 overlayColor;
in vec2 texCoord0;
in vec4 normal;
in vec2 posMod;

out vec4 fragColor;

void main() {
    vec4 color = chaos(Sampler0, Time, Yaw, Pitch, Alpha, fPos, posMod);

    color *= vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);
    color *= lightMapColor;

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}
