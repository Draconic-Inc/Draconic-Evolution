#version 150

#moj_import <light.glsl>
#moj_import <fog.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;
uniform mat4 ModelMat;
uniform mat3 IViewRotMat;
uniform int FogShape;

uniform bool SimpleLight;
uniform bool DisableLight;
uniform vec3 Light0_Direction;
uniform vec3 Light1_Direction;

uniform bool DisableOverlay;

uniform ivec2 UV1Override;
uniform ivec2 UV2Override;

out vec3 fPos;
out vec3 vPos;
out float vertexDistance;
out vec4 vertexColor;
out vec4 lightMapColor;
out vec4 overlayColor;
out vec2 texCoord0;
out vec4 normal;
out vec3 vNorm;

void main() {
    vPos = Position;
    fPos = (ModelViewMat * ModelMat * vec4(Position, 1.0)).xyz;
    gl_Position = ProjMat * ModelViewMat * ModelMat * vec4(Position, 1.0);

    vertexDistance = fog_distance(ModelViewMat, IViewRotMat * Position, FogShape);

    ivec2 uv1 = UV1;
    if (UV1Override.x != -1 && UV1Override.y != -1) {
        uv1 = UV1Override;
    }
    ivec2 uv2 = UV2;
    if (UV2Override.x != -1 && UV2Override.y != -1) {
        uv2 = UV2Override;
    }

    if (DisableLight) {
        vertexColor = Color;
        lightMapColor = vec4(1.0);
    } else if (SimpleLight) {
        vertexColor = Color * minecraft_sample_lightmap(Sampler2, uv2);
        lightMapColor = vec4(1.0);
    } else {
        vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color);
        lightMapColor = texelFetch(Sampler2, uv2 / 16, 0);
    }

    if (DisableOverlay) {
        overlayColor = vec4(1.0);
    } else {
        overlayColor = texelFetch(Sampler1, uv1, 0);
    }

    texCoord0 = UV0;
    normal = ProjMat * ModelViewMat * ModelMat * vec4(Normal, 0.0);
    vNorm = Normal;
}
