/*
with a projection matrix P and a position vector X:

     P      *   X   =   PX   -> dehomogenize

( ? ? ? ? )   ( x )   ( x' )    ( x' / w' )
( ? ? ? ? ) * ( y ) = ( y' ) -> ( y' / w' )
( ? ? a b )   ( z )   ( z' )    ( z' / w' )
( ? ? c d )   ( 1 )   ( w' )


so the depth value is:
depth = (z * a + b) / (z * c + d)
=>
z = (d * depth - b) / (a - c * depth)

with:
inverseDepthFunction = (a, b, c, d)
*/

float getLinearDepth(float depthSample, vec4 inverseDepthFunction, vec2 uv){
	// transform to unit cube (-1, 1)
	depthSample = depthSample * 2 - 1;

	// inverse depth buffer function
	return -(inverseDepthFunction.w * depthSample - inverseDepthFunction.y)
	/(inverseDepthFunction.x - inverseDepthFunction.z * depthSample);
}

float getLinearDepth(sampler2D textureDepth, vec4 inverseDepthFunction, vec2 uv){
	// transform to unit cube (-1, 1)
	float depthSample = texture(textureDepth, uv).x * 2 - 1;

	// inverse depth buffer function
	return -(inverseDepthFunction.w * depthSample - inverseDepthFunction.y)
	       /(inverseDepthFunction.x - inverseDepthFunction.z * depthSample);
}

vec3 getViewRayStart(vec3 unitRayCenterStart, vec3 unitRayRightStart, vec3 unitRayTopStart, vec2 uv){
	uv = uv * 2.0 - 1.0;

	return uv.x * unitRayRightStart + uv.y * unitRayTopStart + unitRayCenterStart;
}

vec3 getViewRayDir(vec3 unitRayCenterDir, vec3 unitRayRightDir, vec3 unitRayTopDir, vec2 uv){
	uv = uv * 2.0 - 1.0;

	return uv.x * unitRayRightDir + uv.y * unitRayTopDir + unitRayCenterDir;
}

vec3 getPositionReconstruct(
	sampler2D textureDepth, vec4 inverseDepthFunction,
	vec3 unitRayCenterStart, vec3 unitRayCenterDir, vec3 unitRayRightStart, vec3 unitRayRightDir, vec3 unitRayTopStart, vec3 unitRayTopDir,
	vec2 uv
){
	return getLinearDepth(textureDepth, inverseDepthFunction, uv)
		* getViewRayDir(unitRayCenterDir, unitRayRightDir, unitRayTopDir, uv)
		+ getViewRayStart(unitRayCenterStart, unitRayRightStart, unitRayTopStart, uv);
}

vec3 getPositionReconstruct(
	sampler2D textureDepth, vec4 inverseDepthFunction,
	vec3 unitRayCenterStart, vec3 unitRayCenterDir, vec3 unitRayRightStart, vec3 unitRayRightDir, vec3 unitRayTopStart, vec3 unitRayTopDir,
	vec2 uv, out float depth
){
	depth = getLinearDepth(textureDepth, inverseDepthFunction, uv);
	return depth
		* getViewRayDir(unitRayCenterDir, unitRayRightDir, unitRayTopDir, uv)
		+ getViewRayStart(unitRayCenterStart, unitRayRightStart, unitRayTopStart, uv);
}
