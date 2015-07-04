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
	vec2 uvNW;
	vec2 uvNE;
	vec2 uvSW;
	vec2 uvSE;
} frag_in;

float fxaaLuminosity(vec3 color){
	return dot(color, vec3(0.25, 0.65, 0.1));
}

vec3 fxaaBlur(vec3 c, vec3 cNW, vec3 cNE, vec3 cSW, vec3 cSE, sampler2D textureColor){
	float lumin = fxaaLuminosity(c);
	float luminNW = fxaaLuminosity(cNW);
	float luminNE = fxaaLuminosity(cNE);
	float luminSW = fxaaLuminosity(cSW);
	float luminSE = fxaaLuminosity(cSE);

	vec2 dir;
	dir.x = (luminNW + luminNE) - (luminSW + luminSE); 
	dir.y = (luminNW + luminSW) - (luminNE + luminSE);

	//float blurMult = 1.0 / (max(abs(dir.x), abs(dir.y)));
	//dir = clamp(dir * blurMult, vec2(-5.0), vec2(5.0));
	//dir *= blurMult;

	if(dot(dir, dir) > 0.15){
		dir *= inverseResolution;
		return 0.5 * c + texture2D(textureColor, frag_in.uv + dir).rgb * 0.5;
	}else{
		return c;
	}
}

float pow16(float f){
	f *= f;
	f *= f;
	f *= f;
	f *= f;
	return f;
}

void main(){
	vec4 colorSample = texture(textureColor, frag_in.uv);
	vec4 colorSampleNW = texture(textureColor, frag_in.uvNW);
	vec4 colorSampleNE = texture(textureColor, frag_in.uvNE);
	vec4 colorSampleSW = texture(textureColor, frag_in.uvSW);
	vec4 colorSampleSE = texture(textureColor, frag_in.uvSE);

	//samples
	vec3 fxaaColorSample = fxaaBlur(colorSample.rgb, colorSampleNW.rgb, colorSampleNE.rgb, colorSampleSW.rgb, colorSampleSE.rgb, textureColor);
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
	//specularIntensity = pow16(specularIntensity) * lightSample.b;
	specularIntensity = pow16(specularIntensity) * lightSample.b;

	//final
	color.rgb = fxaaColorSample * (sunLight + lightsSample) * lightSample.x + specularIntensity * sunLightColor;
	color = mix(color, skyReflectColor, lightSample.y);

	//color.rgb = (sunLight + lightsSample) * lightSample.x + specularIntensity * sunLight;
	//color.rgb = specularIntensity * sunLightColor;
}
