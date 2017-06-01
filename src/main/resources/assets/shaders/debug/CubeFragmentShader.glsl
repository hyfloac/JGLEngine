#version 330

in vec3 out_vertex;
in vec2 out_texCoord;
in vec3 out_normal;

out vec4 fragmentColor;

struct Material
{
	vec4 color;
	int useColor;
	int useTexture;
	int useLight;
	float reflectance;
};

uniform sampler2D textureSampler;
uniform Material material;
uniform vec3 cameraPos;

void main()
{
    vec4 baseColor = vec4(0.0, 0.0, 0.0, 0.0);
    if(material.useColor == 1) { baseColor += material.color; }

    if(material.useTexture == 1)
    {
        vec4 texture = texture(textureSampler, out_texCoord);
        if(texture.w < 0.5) { discard; }
        baseColor += texture;
    }
    fragmentColor = baseColor;
}
