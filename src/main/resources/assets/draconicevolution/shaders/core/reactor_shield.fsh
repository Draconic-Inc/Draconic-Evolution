#version 150

#moj_import <brandonscore:math.glsl>

uniform float time;
uniform float intensity;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    float power = intensity;

    vec3 coord = vec3(texCoord0, 0);
    float yModifier = max(0, max(1 - (coord.y * 10), (coord.y - 0.9) * 10));
    float brightness = 1.2 + (yModifier * -2) + 1 - min(1, power * 3);  //(1.4 + (yModifier * 1.2 * iCap)) * max(-0.4, intensity);

    coord.x += time / 10;
    coord.y += time / 5;
    coord.x -= coord.y / 6;
    coord.y *= 0.2;

    float density = 31;
    for(int i = 1; i <= 7; i++)
    {
        float power = pow(2, float(i));
        brightness += (1.5 / power) * max(snoise(coord + vec3(0, 0, time * 0.006), power * density), -1);//0.006 controls animation speed
    }

    float po = max(brightness, 0.8);
    float r = (0.5 - (power * 2));
    float g = power + yModifier * 0.5;
    float b = (power * 2) + yModifier * 0.5;

    vec4 colour = vec4(po * r, pow(po, 2.0) * g, pow(po ,3.0) * b, max(0.1, 2 - brightness));

    fragColor = colour;
}
