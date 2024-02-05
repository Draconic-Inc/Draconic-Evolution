#version 150

#moj_import <brandonscore:math.glsl>

uniform float Time;
uniform float Power;
uniform float Fade;
uniform float Startup;
uniform int Type; //0 input, 1 output, 2 injector

in vec4 Color;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec3 tcoord = vec3(texCoord0, 0);
    float color = 0.8 - ((1 - Power) * 1.5);// + (1 * p.y);
    vec3 coord = tcoord;

    vec4 c;

    if (Type == 0) { // Shield
        coord.x += Time * 0.5;
        coord.y += Time * 2;
        coord.y += Fade;
        coord.y *= 0.2;

        float density = 8;
        for(int i = 1; i <= 7; i++)
        {
            float sum = pow(2.0, float(i));
            color += (1.5 / sum) * snoise(coord + vec3(0, 0, Time*0.05), sum*density);
        }
        color *= 4 * (1.25 - Power);
        color -= tcoord.y * Fade * 15;// + 60.5;
        color -= (1 - tcoord.y) * (1 - tcoord.y) * 1.5 * (1 - Fade);// + 60.5;

        float co = abs(1 - (color * 2));
        float r = 0.4 * color;
        float g = 2;
        float b = 4;
        color *= Startup;
        co *= Startup;
        c = vec4(co * r, pow(co, 2.0) * g, pow(co ,3.0) * b, max((color - 0.25) * 1.5, 0.2 * tcoord.y * Startup));//0.05
    } else if (Type == 1) { // Extraction
        coord.x += Time * -0.5;
        coord.y -= Time;

        float density = 6;
        for(int i = 1; i <= 7; i++)
        {
            float sum = pow(2.0, float(i));
            color += (1.5 / sum) * snoise(coord + vec3(0, 0, Time*0.05), sum*density);
        }
        color *= 4 * (1.25 - Power);
        color -= tcoord.y * Fade * 15;
        color -= (1 - tcoord.y) * (1 - tcoord.y) * 1.5 * (1 - Fade);
        color *= Startup;

        c = vec4(color, pow(max(color,0),2)*0.4, pow(max(color,0),3)*0.15 , color);
    } else if (Type == 2) { // Injector
        coord.y += Time * 2;
        coord.y += Fade;
        coord.y *= 0.2;

        float density = 8;
        for(int i = 1; i <= 7; i++)
        {
            float sum = pow(2.0, float(i));
            color += (1.5 / sum) * snoise(coord + vec3(0, 0, Time*0.05), sum*density);
        }
        color *= 4 * (1.25 - Power);
        color -= tcoord.y * Fade * 15;// + 60.5;
        color -= (1 - tcoord.y) * (1 - tcoord.y) * 1.5 * (1 - Fade);// + 60.5;

        float co = abs(1 - (color * 2));
        float r = 4;
        float g = 0.3 * color;
        float b = 0.1 * color;
        c = vec4(co * r, pow(co, 2.0) * g, pow(co ,3.0) * b, max((color - 0.25) * 1.5, 0.2 * tcoord.y * (1 - Fade)));//0.05
        c *= Startup;
    }







//    vec3 coord = vec3(texCoord0, 0);
//    float iCap = max(0, min(1, intensity));
//    float yModifier = max(0, max(1 - (coord.y * 5), (coord.y - 0.8) * 5));
//    float brightness = (1.4 + (yModifier * 1.2 * iCap)) * max(-0.4, intensity);
//    float offline = min(0, intensity) * -1;
//
//    float density = 10;
//    for(int i = 1; i <= 7; i++)
//    {
//        float power = pow(2, float(i));
//        brightness += (1.5 / power) * max(snoise(coord + vec3(0, 0, time * 0.01), power * density), -1 + (offline * 1.5));
//    }
//
//    vec4 colour = vec4(brightness, pow(brightness, 2.0) * 0.4 * iCap, pow(brightness ,3.0) * 0.15 * iCap, 1.0);
//
    fragColor = c;
}
