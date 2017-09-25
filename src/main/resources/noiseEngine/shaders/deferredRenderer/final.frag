#version 330 core

#parameter AO_ENABLED //#define AO_ENABLED 1/0

#include positionReconstruction.glsl

uniform sampler2D textureColor;
uniform sampler2D textureNormal;
uniform sampler2D textureLight;
uniform sampler2D textureLights;
uniform sampler2D textureEffects;
uniform samplerCube textureReflection;

uniform vec2 inverseResolution;

uniform vec3 sunLightColor;
uniform vec3 sunLightDirection;
uniform float sunLightBrightness;
uniform float minAmbientBrightness;

uniform float aoSize; //the size of the ao effect in space units
uniform float aoStrength; //the multiplier of the ao effect
uniform float ssUnitSize; //the space unit size at one unit into the space

layout (location = 0) out vec4 color;

in DATA
{
	vec2 uv;
} frag_in;

//source: http://byteblacksmith.com/improvements-to-the-canonical-one-liner-glsl-rand-for-opengl-es-2-0/
/*highp float rand(vec2 co) {
    highp float a = 12.9898;
    highp float b = 78.233;
    highp float c = 43758.5453;
    highp float dt= dot(co.xy ,vec2(a,b));
    highp float sn= mod(dt,3.14);
    return fract(sin(sn) * c);
}*/

vec3 getNormal(vec2 uv){
	return (texture(textureNormal, uv).xyz * 2.0) - 1.0;
}

#if AO_ENABLED
float getAOSample(vec3 pos, vec3 normal, vec2 uv){
	uv = clamp(uv, vec2(0.0), vec2(0.999));
	
	vec3 positionSample = getPositionReconstruct(uv);
	//vec3 normalSample = getNormal(uv);
	
	vec3 diff = positionSample - pos;
	vec3 diffNorm = normalize(diff);
	
	float distMult = (1.0 - (length(diff) / (aoSize * 1.0)));
	
	float occlusion = clamp(dot(normal, diffNorm), 0.0, 1.0) * max(distMult, 0.0);

	// debug code for ao discs
	/*if(uv.x > 0.5 && uv.y > 0.5   &&   uv.x < 0.508 && uv.y < 0.508){
		return 100.0;
	} else if (uv.x > 0.2 && uv.y > 0.5   &&   uv.x < 0.208 && uv.y < 0.508) {
		return 100.0;
    } else if (uv.x > 0.8 && uv.y > 0.5   &&   uv.x < 0.808 && uv.y < 0.508) {
		return 100.0;
	} else if (uv.x > 0.5 && uv.y > 0.2   &&   uv.x < 0.508 && uv.y < 0.208) {
		return 100.0;
	} else if (uv.x > 0.5 && uv.y > 0.8   &&   uv.x < 0.508 && uv.y < 0.808) {
		return 100.0;
	}*/

	return occlusion;
}

float getAO(vec2 uv, vec3 pos, vec3 normal, float z){
	//generate an angle for rotating the sample pattern
	ivec2 band = ivec2( uv / inverseResolution);
	band.x = band.x & 1;
	band.y = band.y & 1;
	float angle = (float(band.x) + float(2 * band.y)) * (3.1415926535 / 2.0);

	mat2 rotMat = mat2(
		cos(angle), -sin(angle),
		sin(angle), cos(angle)
	);

	float diskSize = (aoSize * ssUnitSize) / (inverseDepthFunction.z * -z + inverseDepthFunction.w);

	float occlusion = 0.0;

	if (diskSize > 10) {

		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(-0.09 , -0.13) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(0.36 , 0.11) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(0.75 , 0.11) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(-0.94 , -0.19) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(-0.35 , 0.15) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(0.31 , 0.32) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(-0.64 , -0.34) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(0.93 , 0.4) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(-0.73 , 0.11) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(0.62 , -0.35) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(-0.49 , -0.52) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(0.58 , -0.85) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(0.97 , -0.14) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(0.43 , 0.87) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(0.61 , 0.83) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(-0.72 , -0.7) * inverseResolution));

		occlusion *= 1.0 / 16.0;
	} else { //for small diskSize: fall back to 4 samples
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(0.269 , 0.266) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(-0.959 , -0.348) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(-0.952 , 0.326) * inverseResolution));
		occlusion += getAOSample(pos, normal, uv + diskSize * (rotMat * vec2(-0.709 , -0.735) * inverseResolution));
		
		occlusion *= 1.0 / 4.0;
	}
	
	occlusion *= aoStrength;
	
	return occlusion;
}
#endif //AO_ENABLED

void main(){
	//samples
	vec3 colorSample = texture(textureColor, frag_in.uv).rgb;
	vec3 normalSample = getNormal(frag_in.uv);
	vec4 lightSample = texture(textureLight, frag_in.uv);
	vec3 lightsSample = texture(textureLights, frag_in.uv).xyz;
	float depth;
	vec3 positionSample = getPositionReconstruct(frag_in.uv, depth);
	vec4 effectsSample = texture(textureEffects, frag_in.uv);

	//sunlight
	float sunBright = max(-dot(normalSample, sunLightDirection) * sunLightBrightness, minAmbientBrightness);
	vec3 sunLight = sunLightColor * sunBright;

	//specular + reflections
	vec3 viewDirection = normalize(unitRayCenterStart - positionSample);
	vec3 sunReflectionDirection = reflect(sunLightDirection, normalSample);
	vec3 viewReflectionDirection = reflect(viewDirection, normalSample);

	vec4 skyReflectColor = texture(textureReflection, viewReflectionDirection);

	//specular
	float specularIntensity = max(dot(viewDirection, sunReflectionDirection), 0.0);
	specularIntensity = pow(specularIntensity, exp2(lightSample.a * 7.0)) * lightSample.b;

	//ao
#if AO_ENABLED
	float ao = getAO(frag_in.uv, positionSample, normalSample, depth);
#else
	float ao = 0.0;
#endif

	//final
	color.rgb = (colorSample * (sunLight + lightsSample)) - vec3(ao) + specularIntensity * sunLightColor;
	color = mix(color, skyReflectColor, lightSample.g);

	//effects
	color = mix(color, effectsSample, effectsSample.a);

	// debug output for light only
	//color.rgb = (sunLight + lightsSample) * ao * 0.5 + specularIntensity * sunLightColor;

}
