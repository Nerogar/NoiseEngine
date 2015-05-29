vec3 fade(vec3 t) {
	return t*t*t*(t*(t*6.0-15.0)+10.0);
}

float mod1061(float x){//0.0009425070
	return x - floor(x * 0.0009425070) * 1061.0; 
}

float mod1259(float x){//0.0007942811
	return x - floor(x * 0.0007942811) * 1259.0;
}

float mod1031(float x){//0.0009699321
	return x - floor(x * 0.0009699321) * 1031.0;
}

vec3 randPreprocess(vec3 s){	
	s.x = mod1061((s.x * 823.0) + 856.0);
	s.y = mod1259((s.y * 883.0) + 678.0);
	s.z = mod1031((s.z * 793.0) + 789.0);

	return s;
}

float rand(float s1, float s2, float s3){
	vec3 v1 = vec3(s1, s2, s3);
	vec3 v2 = vec3(s2, s3, s1);
	vec3 v3 = vec3(s3, s1, s2);

	s1 = mod1061(dot(v1, v2));
	s2 = mod1259(dot(v2, v3));
	s3 = mod1031(dot(v3, v1));

	return mod1031(mod1259(s1 * s2) * s3) * 0.0009699321;
}

float perlinIteration3D(vec3 pos){
	vec3 pos0 = floor(pos);
	vec3 pos1 = pos0 + vec3(1.0);
	vec3 posFract = fade(fract(pos));

	pos0 = randPreprocess(pos0);
	pos1 = randPreprocess(pos1);

	vec4 rand0 = vec4(	rand(pos0.x, pos0.y, pos0.z),
						rand(pos1.x, pos0.y, pos0.z),
						rand(pos0.x, pos1.y, pos0.z),
						rand(pos1.x, pos1.y, pos0.z));

	vec4 rand1 = vec4(	rand(pos0.x, pos0.y, pos1.z),
						rand(pos1.x, pos0.y, pos1.z),
						rand(pos0.x, pos1.y, pos1.z),
						rand(pos1.x, pos1.y, pos1.z));

	vec4 randMix1 = mix(rand0, rand1, posFract.z);
	vec2 randMix11 = mix(randMix1.xy, randMix1.zw, posFract.y);
	float randMix = mix(randMix11.x, randMix11.y, posFract.x);

	return randMix;
}

//combinations

float perlin3D1(vec3 pos){
	return perlinIteration3D(pos);
}

float perlin3D2(vec3 pos){
	float sum = 0.0;

	sum += perlinIteration3D(pos * 1.0) * 0.6666;
	sum += perlinIteration3D(pos * 2.0) * 0.3333;

	return sum;
}

float perlin3D3(vec3 pos){
	float sum = 0.0;

	sum += perlinIteration3D(pos * 1.0) * 0.5454;
	sum += perlinIteration3D(pos * 2.0) * 0.2727;
	sum += perlinIteration3D(pos * 4.0) * 0.1818;

	return sum;
}

float perlin3D4(vec3 pos){
	float sum = 0.0;

	sum += perlinIteration3D(pos * 1.0) * 0.4799;
	sum += perlinIteration3D(pos * 2.0) * 0.2399;
	sum += perlinIteration3D(pos * 4.0) * 0.16;
	sum += perlinIteration3D(pos * 8.0) * 0.1199;

	return sum;
}

float perlin3D5(vec3 pos){
	float sum = 0.0;

	sum += perlinIteration3D(pos * 1.0) * 0.4379;
	sum += perlinIteration3D(pos * 2.0) * 0.2189;
	sum += perlinIteration3D(pos * 4.0) * 0.1459;
	sum += perlinIteration3D(pos * 8.0) * 0.1094;
	sum += perlinIteration3D(pos * 16.0) * 0.0875;

	return sum;
}

float perlin3D6(vec3 pos){
	float sum = 0.0;

	sum += perlinIteration3D(pos * 1.0) * 0.4081;
	sum += perlinIteration3D(pos * 2.0) * 0.2040;
	sum += perlinIteration3D(pos * 4.0) * 0.1360;
	sum += perlinIteration3D(pos * 8.0) * 0.1020;
	sum += perlinIteration3D(pos * 16.0) * 0.0816;
	sum += perlinIteration3D(pos * 32.0) * 0.0680;

	return sum;
}

float perlin3D7(vec3 pos){
	float sum = 0.0;

	sum += perlinIteration3D(pos * 1.0) * 0.3856;
	sum += perlinIteration3D(pos * 2.0) * 0.1928;
	sum += perlinIteration3D(pos * 4.0) * 0.1285;
	sum += perlinIteration3D(pos * 8.0) * 0.0964;
	sum += perlinIteration3D(pos * 16.0) * 0.0771;
	sum += perlinIteration3D(pos * 32.0) * 0.0642;
	sum += perlinIteration3D(pos * 64.0) * 0.0550;

	return sum;
}

float perlin3D8(vec3 pos){
	float sum = 0.0;

	sum += perlinIteration3D(pos * 1.0) * 0.3679;
	sum += perlinIteration3D(pos * 2.0) * 0.1839;
	sum += perlinIteration3D(pos * 4.0) * 0.1226;
	sum += perlinIteration3D(pos * 8.0) * 0.0919;
	sum += perlinIteration3D(pos * 16.0) * 0.0735;
	sum += perlinIteration3D(pos * 32.0) * 0.0613;
	sum += perlinIteration3D(pos * 64.0) * 0.0525;
	sum += perlinIteration3D(pos * 128.0) * 0.0459;

	return sum;
}
