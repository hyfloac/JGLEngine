#version 330

layout(location = 0) in vec2 in_vertex;
layout(location = 1) in vec2 in_texCoord;

out vec2 out_texCoord;

uniform vec2 translation;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main()
{
	gl_Position = vec4(in_vertex + translation * vec2(0.0F, -0.0F), 0.0F, -5.0F);
//	out_vertex.x += translation.x;
//	out_vertex.y += translation.y;
	out_texCoord = in_texCoord;
}
