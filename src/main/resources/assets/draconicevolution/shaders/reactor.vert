#version 120

varying vec3 position;

void main()
{
//    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;TODO I dont think i actually need this
//    position = (gl_ModelViewMatrix * gl_Vertex).xyz;

    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    gl_TexCoord[0] = gl_MultiTexCoord0;
    gl_FrontColor = gl_Color;

    position = (gl_ModelViewMatrix * gl_Vertex).xyz;
}

//#version 330

//uniform mat4 projectionMatrix;
//uniform mat4 modelViewMatrix;
//uniform mat4 normalMatrix;
//
//layout (location = 0) in vec3 inPosition;
//layout (location = 1) in vec2 inCoord;
//layout (location = 2) in vec3 inNormal;
//
//out vec2 texCoord;
//
//smooth out vec3 vNormal;
//
//void main()
//{
//   gl_Position = projectionMatrix*modelViewMatrix*vec4(inPosition, 1.0);
//   texCoord = inCoord;
//   vec4 vRes = normalMatrix*vec4(inNormal, 0.0);
//   vNormal = vRes.xyz;
//}