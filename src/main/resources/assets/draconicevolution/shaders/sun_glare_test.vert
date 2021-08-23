#version 120

varying vec3 position;

void main()
{
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    position = (gl_ModelViewMatrix * gl_Vertex).xyz;
}