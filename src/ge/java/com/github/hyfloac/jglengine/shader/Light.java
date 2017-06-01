package com.github.hyfloac.jglengine.shader;

import com.github.vitrifiedcode.javautilities.math.MathUtil;
import com.github.vitrifiedcode.javautilities.string.StringUtil;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Light
{
    public static class Attenuation implements Cloneable
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

        @Override
        public boolean equals(Object obj)
        {
            if(!(obj instanceof Attenuation)) { return false; }
            Attenuation a = (Attenuation) obj;
            return !(!MathUtil.epsilonEquals(a.constant, constant) || !MathUtil.epsilonEquals(a.linear, linear) || !MathUtil.epsilonEquals(a.exponent, exponent));
        }

        @Override
        public int hashCode() { return (int) ((constant * linear / 23.345F + exponent) * 3); }

        @Override
        public Attenuation clone() { return new Attenuation(this); }

        @Override
        public String toString() { return StringUtil.build(super.toString(), ": { \"constant\": \"", constant, "\", \"linear\": \"", linear, "\", \"exponent\": \"", exponent, "\" };"); }
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

        @Override
        public boolean equals(Object obj)
        {
            if(!(obj instanceof PointLight)) { return false; }
            PointLight p = (PointLight) obj;
            return !(!p.color.equals(color) || !p.position.equals(position) || !MathUtil.epsilonEquals(p.intensity, intensity) || !p.att.equals(att));
        }

        @Override
        public int hashCode() { return (int) ((color.hashCode() * position.hashCode() / 1.64 + intensity) * 3) << 3 + att.hashCode(); }

        @Override
        public PointLight clone() { return new PointLight(this); }

        @Override
        public String toString() { return StringUtil.build(super.toString(), ": { \"color\": \"", color.toString(), "\", \"position\": \"", position.toString(), "\", \"intensity\": \"", intensity, "\", \"attenuation\": \"", att.toString(), "\" };"); }
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

        @Override
        public boolean equals(Object obj)
        {
            if(!(obj instanceof Material)) { return false; }
            Material m = (Material) obj;
            return !(!m.color.equals(color) || m.useColor != useColor || m.useTexture != useTexture || m.useLight != useLight || !MathUtil.epsilonEquals(m.reflectance, reflectance));
        }

        @Override
        public int hashCode() { return ((int) ((color.hashCode() * (useColor ? 23 : -2) + reflectance) * 3) << (useLight ? 1 : 4)) + (useTexture ? 935 : -2358); }

        @Override
        public Material clone() { return new Material(this); }

        @Override
        public String toString() { return StringUtil.build(super.toString(), ": { \"color\": \"", color.toString(), "\", \"useColor\": \"", useColor, "\", \"useTexture\": \"", useTexture, "\", \"useLight\": \"", useLight, "\", \"reflectance\": \"", reflectance, "\" };"); }
    }
}
