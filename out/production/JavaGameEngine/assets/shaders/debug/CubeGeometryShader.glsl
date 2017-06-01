#version 330

layout(points) in;
layout(triangle_strip, max_vertices = 8) out;

in vec3 baseColor[];

out vec3 color;


uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main()
{
    vec4 offset = vec4(-1.0, 1.0, 0.0, 0.0);
    vec4 vertexPos = offset + gl_in[0].gl_Position;
    gl_Position = projectionMatrix * modelViewMatrix * vertexPos;
    color = baseColor[0] * vec3(1.0, 0.0, 0.0);
    EmitVertex();

    offset = vec4(-1.0, 1.0, 0.0, 0.0);
    vertexPos = offset + gl_in[0].gl_Position;
    gl_Position = projectionMatrix * modelViewMatrix * vertexPos;
    color = baseColor[0] * vec3(1.0, 0.0, 0.0);
    EmitVertex();

    offset = vec4(-1.0, 1.0, 0.0, 0.0);
    vertexPos = offset + gl_in[0].gl_Position;
    gl_Position = projectionMatrix * modelViewMatrix * vertexPos;
    color = baseColor[0] * vec3(1.0, 0.0, 0.0);
    EmitVertex();

    offset = vec4(-1.0, 1.0, 0.0, 0.0);
    vertexPos = offset + gl_in[0].gl_Position;
    gl_Position = projectionMatrix * modelViewMatrix * vertexPos;
    color = baseColor[0] * vec3(1.0, 0.0, 0.0);
    EmitVertex();

    EndPrimitive();
}
