#version 330 core

uniform sampler2D textureColor;
uniform sampler2D textureNormal;
uniform sampler2D texturePosition;
uniform sampler2D textureLight;
uniform sampler2D textureLights;
uniform samplerCube textureReflection;

uniform vec2 inverseResolution;
uniform vec3 cameraPosition;

uniform vec3 sunLightColor;
uniform vec3 sunLightDirection;
uniform float minAmbientBrightness;

layout (location = 0) out vec4 color;

in DATA
{
	vec2 uv;
} frag_in;

void main(){
	//samples
	vec3 colorSample = texture(textureColor, frag_in.uv).rgb;
	vec3 normalSample = texture(textureNormal, frag_in.uv).xyz;
	vec4 lightSample = texture(textureLight, frag_in.uv);
	vec3 lightsSample = texture(textureLights, frag_in.uv).xyz;
	vec3 positionSample = texture(texturePosition, frag_in.uv).xyz;

	//sunlight
	float sunBright = max(-dot(normalSample, sunLightDirection), minAmbientBrightness);
	vec3 sunLight = sunLightColor * sunBright;

	//specular + reflections
	vec3 viewDirection = normalize(cameraPosition - positionSample);
	vec3 sunReflectionDirection = reflect(sunLightDirection, normalSample);
	vec3 viewReflectionDirection = reflect(viewDirection, normalSample);

	vec4 skyReflectColor = texture(textureReflection, viewReflectionDirection);

	//specular
	float specularIntensity = max(dot(viewDirection, normalize(sunReflectionDirection)), 0.0);
	specularIntensity = pow(specularIntensity, exp2(lightSample.a * 7.0)) * lightSample.b;

	//final
	color.rgb = colorSample * (sunLight + lightsSample) * lightSample.x + specularIntensity * sunLightColor;
	color = mix(color, skyReflectColor, lightSample.y);

	//color.rgb = (sunLight + lightsSample) * lightSample.x + specularIntensity * sunLight;
	//color.rgb = specularIntensity * sunLightColor;
}
