#define BRDF_EPSILON 0.00000001
#define BRDF_PI 3.1415926

float distributionGGX(float dotNH, float roughness) {
	float alpha = roughness * roughness;
	float alpha2 = alpha * alpha;
	float denom = (dotNH * dotNH) * (alpha2 - 1.0) + 1.0;
	return alpha2 / max(BRDF_EPSILON, (BRDF_PI * denom * denom));
}

float geometrySmith(float dotNV, float dotNL, float roughness) {
	//float roughness1 = roughness + 1.0;
	//float k = (roughness1 * roughness1) / 8.0;
	float k = roughness / 2.0;

	float ggxV = dotNV / max(BRDF_EPSILON, dotNV * (1.0 - k) + k);
	float ggxL = dotNL / max(BRDF_EPSILON, dotNL * (1.0 - k) + k);

	return ggxV * ggxL;
}

float fresnelSchlick(float dotHV, float baseReflectivity) {
	return baseReflectivity + (1.0 - baseReflectivity) * pow(max(BRDF_EPSILON, 1.0 - dotHV), 5.0);
}

vec3 fresnelSchlick(float dotHV, vec3 baseReflectivity) {
	return baseReflectivity + (vec3(1.0) - baseReflectivity) * pow(max(BRDF_EPSILON, 1.0 - dotHV), 5.0);
}

/**
calculates the reflectance of the surface.
l = light vector. Normalized vector pointing from the surface to the light source.
n = normal vector. Normalized normal vector of the surface.
l = view vector. Normalized vector pointing from the surface to the camera source.
albedo = albedo color of the surface
metalness = metalness of the surface
roughness = roughness of the surface
reflectivity = reflectivity of the surface
lightColor = color of the light
lightIntensity = intensity of the light
*/
vec3 brdf(
	vec3 l, vec3 n, vec3 v, vec3 albedo, float metalness, float roughness, float reflectivity, // surface parameters
	vec3 lightColor, float lightIntensity // light parameters
) {
	vec3 h = normalize(l + v);

	float dotNH = max(0.0, dot(n, h));
	float dotHV = max(0.0, dot(h, v));
	float dotNV = max(0.0, dot(n, v));
	float dotNL = max(0.0, dot(n, l));

	vec3 baseReflectivity = mix(vec3(reflectivity), albedo, metalness);

	float D = distributionGGX(dotNH, roughness);
	vec3 F = fresnelSchlick(dotHV, baseReflectivity);
	float G  = geometrySmith(dotNV, dotNL, roughness);

	vec3 specular = D * F * G;
	specular = specular / max(BRDF_EPSILON, 4.0 * dotNL * dotNV);

	vec3 diffuse = (vec3(1.0) - F);
	diffuse *= (1.0 - metalness);
	diffuse *= (albedo / BRDF_PI);

	return (diffuse + specular) * (lightColor * lightIntensity) * dotNL;
}
