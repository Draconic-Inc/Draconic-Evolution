#version 150

#moj_import <brandonscore:math.glsl>

uniform float Time;
uniform vec3 Colour;
uniform float Mipmap;
//uniform vec2 Angle;

in vec2 texCoord0;
in vec3 fPos;

out vec4 fragColor;

void main() {
    float intensity = 1;
    vec3 coord = vec3(texCoord0, 0);
    float timeOffset = Time / 15.0;

    coord.x += timeOffset + (fPos.x / 10);// - (angle.x * 2);
    coord.y += (fPos.y / 10);

    float density = 15;//float(int(15.0 * mipmap));
    float brightness = 1;
    for(int i = 1; i <= 7; i++)
    {
        float power = pow(2, float(i));
        brightness += (1.5 / power) * max(snoise(coord + vec3(0, 0, Time * 0.01), power * density), -1);
    }

    brightness = ((1 - Mipmap) * brightness) + (Mipmap * 1.2) ;//max(brightness, mipmap);

    vec4 colour = vec4(brightness * Colour.r, pow(brightness, 2.0) * Colour.g, pow(brightness ,3.0) * Colour.b, 1.0);
    fragColor = colour;
}