package de.nerogar.noise.render;

import de.nerogar.noise.util.Color;

public class Material {

	private String name;

	private Color  albedoColor;
	private String albedoTexture;

	private String normalMap;

	private float  specularIntensity;
	private String specularIntensityMap;

	private float  specularExponent;
	private String specularExponentMap;

	private Color  emissionColor;
	private String emissionTexture;

	public String getName()                                          { return name; }

	public void setName(String name)                                 { this.name = name; }

	public Color getAlbedoColor()                                    { return albedoColor; }

	public void setAlbedoColor(Color albedoColor)                    { this.albedoColor = albedoColor; }

	public String getAlbedoTexture()                                 { return albedoTexture; }

	public void setAlbedoTexture(String albedoTexture)               { this.albedoTexture = albedoTexture; }

	public String getNormalMap()                                     { return normalMap; }

	public void setNormalMap(String normalMap)                       { this.normalMap = normalMap; }

	public float getSpecularIntensity()                              { return specularIntensity; }

	public void setSpecularIntensity(float specularIntensity)        { this.specularIntensity = specularIntensity; }

	public String getSpecularIntensityMap()                          { return specularIntensityMap; }

	public void setSpecularIntensityMap(String specularIntensityMap) { this.specularIntensityMap = specularIntensityMap; }

	public float getSpecularExponent()                               { return specularExponent; }

	public void setSpecularExponent(float specularExponent)          { this.specularExponent = specularExponent; }

	public String getSpecularExponentMap()                           { return specularExponentMap; }

	public void setSpecularExponentMap(String specularExponentMap)   { this.specularExponentMap = specularExponentMap; }

	public Color getEmissionColor()                                  { return emissionColor; }

	public void setEmissionColor(Color emissionColor)                { this.emissionColor = emissionColor; }

	public String getEmissionTexture()                               { return emissionTexture; }

	public void setEmissionTexture(String emissionTexture)           { this.emissionTexture = emissionTexture; }
}
