uniform sampler2D textureDepth;

// camera uniforms
uniform vec3 unitRayCenterStart;
uniform vec3 unitRayCenterDir;
uniform vec3 unitRayRightStart;
uniform vec3 unitRayRightDir;
uniform vec3 unitRayTopStart;
uniform vec3 unitRayTopDir;

uniform vec4 inverseDepthFunction;


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
float getLinearDepth(vec2 uv){
	// transform to unit cube (-1, 1)
	float depthSample = texture(textureDepth, uv).x * 2 - 1;

	// inverse depth buffer function
	return -(inverseDepthFunction.w * depthSample - inverseDepthFunction.y)
	       /(inverseDepthFunction.x - inverseDepthFunction.z * depthSample);
}

vec3 getViewRayStart(vec2 uv){
	uv = uv * 2.0 - 1.0;

	return uv.x * unitRayRightStart + uv.y * unitRayTopStart + unitRayCenterStart;
}

vec3 getViewRayDir(vec2 uv){
	uv = uv * 2.0 - 1.0;

	return uv.x * unitRayRightDir + uv.y * unitRayTopDir + unitRayCenterDir;
}

vec3 getPositionReconstruct(vec2 uv){
	return getLinearDepth(uv) * getViewRayDir(uv) + getViewRayStart(uv);
}

vec3 getPositionReconstruct(vec2 uv, out float depth){
	depth = getLinearDepth(uv);
	return depth * getViewRayDir(uv) + getViewRayStart(uv);
}
