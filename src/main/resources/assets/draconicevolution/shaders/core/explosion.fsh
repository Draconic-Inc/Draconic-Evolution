#version 150

#moj_import <brandonscore:math.glsl>

uniform float Time;
uniform float Scale;
uniform float Alpha;
uniform int Type;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    float power = 0.5;
    vec3 tCoord = vec3(texCoord0, 0);
    vec2 p = -.5 + tCoord.xy;

    if (Type == 0) { //Core effect
        float yModifier = abs(tCoord.y - 0.5) * (0.5 + Scale);
        float brightness = (yModifier * 2) + (3.*length(yModifier));
        vec3 coord = vec3(p.x * 6, length(p.y)*0.5, .5);

        float density = 1.0 + (Scale);
        for (int i = 1; i <= 7; i++)
        {
            float power = pow(2.0, float(i));
            brightness += (1.5 / power) * snoise(coord + vec3(0., -Time*.05, -Time*.01), power*16.);
        }

        fragColor = vec4(brightness, pow(max(brightness, 0.), 2.)*0.4, pow(max(brightness, 0.), 3.)*0.15, max(0, 2 - brightness) * Alpha);
    }
    else if (Type == 1) { //Blast Wave
        float yModifier = abs(tCoord.y - 0.5) * (4 - Scale);
        float brightness = (yModifier * 2) + (3.*length(yModifier));
        vec3 coord = vec3(p.x * 6, length(p.y)*0.5, .5);

        for (int i = 1; i <= 7; i++)
        {
            float power = pow(2.0, float(i));
            brightness += (1.5 / power) * snoise(coord + vec3(0., -Time*.05, Time*.01), power*16.);
        }

        fragColor = vec4(brightness, pow(max(brightness, 0.), 2.)*0.4, pow(max(brightness, 0.), 3.)*0.15, max(0, 2 - brightness) * Alpha);
    }
    else { //Leading Wave
        float yModifier = abs(tCoord.y - 0.5) * (4 - Scale);
        float brightness = (yModifier * 2) + (3.*length(yModifier));
        vec3 coord = vec3(p.x * 6, length(p.y)*0.5, .5);

        for (int i = 1; i <= 7; i++)
        {
            float power = pow(2.0, float(i));
            brightness += (1.5 / power) * snoise(coord + vec3(0., -Time*.05, Time*.01), power*16.);
        }

        yModifier = 0;
        float po = max(brightness, 0.8);
        float r = (0.5 - (power * 2));
        float g = power + yModifier * 0.5;
        float b = (power * 2) + yModifier * 0.5;

        vec4 colour = vec4(po * r, pow(po, 2.0) * g, pow(po, 3.0) * b, max(0, 2 - brightness) * Alpha);
        fragColor = colour;
    }
}
