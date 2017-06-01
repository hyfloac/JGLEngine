package com.github.hyfloac.jglengine.shader;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Light
{
    public static class Attenuation
    {
        public float constant;
        public float linear;
        public float exponent;

        public Attenuation() {}

        public Attenuation(float constant, float linear, float exponent)
        {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        public Attenuation(Attenuation att)
        {
            constant = att.constant;
            linear = att.linear;
            exponent = att.exponent;
        }
    }

    public static class PointLight extends Light
    {
        public Vector3f color;
        public Vector3f position;
        public float intensity;
        public Attenuation att;

        public PointLight() {}

        public PointLight(Vector3f color, Vector3f position, float intensity, Attenuation att)
        {
            this.color = color;
            this.position = position;
            this.intensity = intensity;
            this.att = att;
        }

        public PointLight(PointLight pl)
        {
            color = new Vector3f(pl.color);
            position = new Vector3f(pl.position);
            intensity = pl.intensity;
            att = new Attenuation(pl.att);
        }
    }

    public static class Material
    {
        public Vector4f color;
        public boolean useColor;
        public boolean useTexture;
        public boolean useLight;
        public float reflectance;

        public Material() {}

        public Material(Vector4f color, boolean useColor, boolean useTexture, boolean useLight, float reflectance)
        {
            this.color = color;
            this.useColor = useColor;
            this.useTexture = useTexture;
            this.useLight = useLight;
            this.reflectance = reflectance;
        }

        public Material(Material mat)
        {
            color = new Vector4f(mat.color);
            useColor = mat.useColor;
            useTexture = mat.useTexture;
            useLight = mat.useLight;
            reflectance = mat.reflectance;
        }
    }
}
