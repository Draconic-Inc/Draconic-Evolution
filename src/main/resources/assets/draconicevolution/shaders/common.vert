#version 150

varying vec3 position;

void main() {
    position = (gl_ModelViewMatrix * gl_Vertex).xyz + gl_Normal;

    gl_FrontColor = gl_Color;
    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
