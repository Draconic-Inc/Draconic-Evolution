#version 150

in vec3 Position;
in vec2 UV2;
in vec2 UV0;

uniform mat4 ProjMat;
uniform mat4 ModelViewMat;

out vec3 fPos;
out vec2 FaceMod;
out vec2 texCoord0;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    fPos = (ModelViewMat * vec4(Position, 1.0)).xyz;
    FaceMod = UV2;
    texCoord0 = UV0;
}
