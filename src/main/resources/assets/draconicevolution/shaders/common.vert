#version 120

varying vec3 position;
varying vec3 normal;

void main() {
    position = (gl_ModelViewMatrix * gl_Vertex).xyz + gl_Normal;
    normal = gl_Normal;

    gl_FrontColor = gl_Color;
    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
