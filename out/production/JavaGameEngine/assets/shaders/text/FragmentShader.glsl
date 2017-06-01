#version 330

in vec2 out_texCoord;

/* The color of a pixel */
out vec4 fragmentColor;

/* Texture, Color, lighting */
struct Material
{
	vec4 color;
	int useColor;
	int useTexture;
};

/* Something for textures */
uniform sampler2D textureSampler;
/* The Models material */
uniform Material material;

void main()
{
//    fragmentColor = vec4(material.color.xyz, texture(textureSampler, out_texCoord).a);
    fragmentColor = texture(textureSampler, out_texCoord) + material.color;
}
