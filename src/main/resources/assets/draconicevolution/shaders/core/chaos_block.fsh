#version 150

#moj_import <fog.glsl>
#moj_import <brandonscore:math.glsl>
#moj_import <brandonscore:chaos.glsl>

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
in vec2 texCoord0;
in vec4 normal;
in vec2 posMod;

out vec4 fragColor;

void main() {
    vec4 col = chaos(Sampler0, Time, Yaw, Pitch, Alpha, fPos, posMod);

    col *= vertexColor * ColorModulator;

    fragColor = linear_fog(col, vertexDistance, FogStart, FogEnd, FogColor);
}
