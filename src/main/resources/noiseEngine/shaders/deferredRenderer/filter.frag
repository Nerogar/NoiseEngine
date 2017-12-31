#version 330 core

uniform sampler2D textureColor;

uniform vec2 inverseResolution;

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

vec3 fxaaBlur(vec3 color, vec3 cNW, vec3 cNE, vec3 cSW, vec3 cSE, sampler2D textureColor){
	float lumin = fxaaLuminosity(color);
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

	if (dot(dir, dir) > 0.25) {
		//return vec3(dir, 0.0);
		
		dir *= inverseResolution;
		return 0.5 * color + texture2D(textureColor, frag_in.uv + dir).rgb * 0.5;
	} else {
		//return vec3(1.0);
		return color;
	}
}

void main(){
	vec4 colorSample = texture(textureColor, frag_in.uv);
	vec4 colorSampleNW = texture(textureColor, frag_in.uvNW);
	vec4 colorSampleNE = texture(textureColor, frag_in.uvNE);
	vec4 colorSampleSW = texture(textureColor, frag_in.uvSW);
	vec4 colorSampleSE = texture(textureColor, frag_in.uvSE);

	vec3 fxaaColorSample = fxaaBlur(colorSample.rgb, colorSampleNW.rgb, colorSampleNE.rgb, colorSampleSW.rgb, colorSampleSE.rgb, textureColor);

	color.rgb = fxaaColorSample;
	color.a = colorSample.a;
}
