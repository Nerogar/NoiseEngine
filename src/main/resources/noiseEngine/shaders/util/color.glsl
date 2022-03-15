vec3 srgbToLinear(vec3 color) {
	return pow(color, vec3(2.2));
}

vec3 linearToSrgb(vec3 color) {
	return pow(color, vec3(1.0 / 2.2));
}

float luminance(vec3 color) {
	return dot(color, vec3(0.299, 0.587, 0.114));
}