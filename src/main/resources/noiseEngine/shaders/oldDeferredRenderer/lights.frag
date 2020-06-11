#version 330 core

#include positionReconstruction.glsl

uniform sampler2D textureNormal;

uniform vec2 inverseResolution;

layout (location = 0) out vec4 color;

in DATA
{
	vec3 position;
	vec3 color;
	float reach;
	float intensity;
} frag_in;

float getLightIntensity(vec3 normal, vec3 lightDir){
	return max(-dot(normal, lightDir), 0.0);
}

vec4 calcLight(vec3 lightPos, vec3 lightColor, vec3 worldPosition, vec3 worldNormal, float lightReach, float lightIntensity){
	vec3 lightDir = worldPosition - lightPos;
	lightIntensity *= (1.0 - length(lightDir) / lightReach);
	lightIntensity = max(lightIntensity, 0.0);

	lightDir = normalize(lightDir);
	lightIntensity *= getLightIntensity(worldNormal, lightDir);
	lightColor *= lightIntensity;
	
	return vec4(lightColor, 1.0);
}

void main(){
	vec2 screenPosition = gl_FragCoord.xy * inverseResolution;
	vec3 worldNormal = (texture(textureNormal, screenPosition).xyz * 2.0) - 1.0;
	vec3 worldPosition = getPositionReconstruct(screenPosition);

	//sunlight is currently calculated in final.frag
	/*vec3 sunLightDirection = normalize(vec3(-1.0, -1.0, -1.0)) * 1.0;
	float bright = max(getLightIntensity(worldNormal, sunLightDirection), 0.5);
	vec3 sunColor = vec3(1.0, 1.0, 0.9);
	worldLight += vec4(sunColor * bright, 0.0);*/

	color = calcLight(frag_in.position, frag_in.color, worldPosition, worldNormal, frag_in.reach, frag_in.intensity);
}
