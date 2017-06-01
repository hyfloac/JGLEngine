#version 330

in vec3 out_vertex;
in vec2 out_texCoord;
in vec3 out_normal;

/* The color of a pixel */
out vec4 fragmentColor;

/* Brightness of light or something. */
struct Attenuation
{
    float constant;
    float linear;
    float exponent;
};

/* Lights! */
struct PointLight
{
    vec3 color;
    vec3 position;
    float intensity;
    Attenuation att;
};

/* Texture, Color, lighting */
struct Material
{
	vec4 color;
	int useColor;
	int useTexture;
	int useLight;
	float reflectance;
};

/* Something for textures */
uniform sampler2D textureSampler;
/* Ambient light level (might support colored light) */
uniform vec3 ambientLight;
/* Reflectivity */
uniform float specularPower;
/* The Models material */
uniform Material material;
/* Only supports one light in a scene */
uniform PointLight pointLight;
/* The camera position */
uniform vec3 cameraPos;

/* Per Vertex light calculation */
/* Watch this: https://youtu.be/GZ_1xOm-3qU */
/* And this: https://youtu.be/bcxX0R8nnDs */
vec4 calcPointLight(PointLight light, vec3 position, vec3 normal)
{
    vec4 diffuseColor = vec4(0.0, 0.0, 0.0, 0.0);
    vec4 specColor = vec4(0.0, 0.0, 0.0, 0.0);

    vec3 lightDirection = light.position - position;
    vec3 toLightSource  = normalize(lightDirection);
    float diffuseFactor = max(dot(normal, toLightSource), 0.0);
    diffuseColor = vec4(light.color, 1.0) * light.intensity * diffuseFactor;

    vec3 cameraDirection = normalize(cameraPos - position);
    vec3 fromLightSource = -toLightSource;
    vec3 reflectedLight = normalize(reflect(fromLightSource, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specColor = specularFactor * material.reflectance * vec4(light.color, 1.0);

    float dist = length(lightDirection);
    float attenuationInv = light.att.constant + light.att.linear * dist + light.att.exponent * dist * dist;
    return (diffuseColor + specColor) / attenuationInv;
}

void main()
{
    /* This allows us to use a texture and add a color overlay. */
    vec4 baseColor = vec4(0.0, 0.0, 0.0, 0.0);
    if(material.useColor == 1) { baseColor += material.color; }

    if(material.useTexture == 1)
    {
        vec4 texture = texture(textureSampler, out_texCoord);
        if(texture.w < 0.5) { discard; } /* Cheap Transparency */
        baseColor += texture;
    }

    if(material.useLight == 1)
    {
        vec4 totalLight = vec4(ambientLight, 1.0) + calcPointLight(pointLight, out_vertex, out_normal);
        fragmentColor = baseColor * totalLight;
    } else { fragmentColor = baseColor; }
}
