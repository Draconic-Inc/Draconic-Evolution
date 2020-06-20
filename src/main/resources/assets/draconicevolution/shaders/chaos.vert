#version 120

varying vec3 position;
varying vec3 pos_mod;


void main()
{
    pos_mod = gl_Normal / 100;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	position = (gl_ModelViewMatrix * gl_Vertex).xyz;
}