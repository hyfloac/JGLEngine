#version 330

layout(location = 0) in vec3 in_vertex;
layout(location = 1) in vec2 in_texCoord;
layout(location = 2) in vec3 in_normal;

out vec3 out_vertex;
out vec2 out_texCoord;
out vec3 out_normal;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main()
{
	vec4 pos = modelViewMatrix * vec4(in_vertex, 1.0);
	/* Make the world feel truly 3D */
	gl_Position = projectionMatrix * modelViewMatrix * vec4(in_vertex, 1.0);
	out_vertex = pos.xyz;
	out_texCoord = in_texCoord;
	/* No idea what is actually hapenning here but is required */
	out_normal = normalize(modelViewMatrix * vec4(in_normal, 0.0)).xyz;
}
