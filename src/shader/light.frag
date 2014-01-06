#version 130

#define DIFFUSE_CONTRAST 0.8

uniform sampler2DShadow shadowMap;
uniform sampler2D comp;
uniform float sunAngle;

in vec4 shadowCoord;
in vec3 color;
in float br;
in vec4 texCoord;
in vec3 lpos, normal;

float getValueWithDiffuseContrastClamping(float val) {
	return val * DIFFUSE_CONTRAST + (1.0 - DIFFUSE_CONTRAST);
}

float calcShadowFactor() {
	float s = dot(lpos.xyz, lpos.xyz) / 2048.0;

	float shadow = 0.0;
	float numLoops = 0.0;
	
	for(float y = -.01; y <= .01; y += 0.005) {
		for(float x = -.01; x <= .01; x += 0.005) {
			shadow += textureProj(shadowMap, shadowCoord + vec4(x * s, y * s, 0, 0));
			numLoops += 1.0;
		}
	}

	return (shadow / numLoops);
}

float calcDiffuseLighting() {
	return getValueWithDiffuseContrastClamping(br);
}

float calcDiffuseLightingWithShadows() {
	float shadowFactor = calcShadowFactor();
	float diffuse = br * shadowFactor;
	return getValueWithDiffuseContrastClamping(diffuse);
}

void main() {
	vec4 col = texture2D(comp, texCoord.st);

/*	if(sunAngle < 70 && sunAngle > -10) {
		float multiplier = 1.0 / 6400.0 * (sunAngle + 10) * (sunAngle + 10) ;
		shadow *= multiplier;
	}
	if(sunAngle > 110 && sunAngle < 190) {
		float multiplier = 1.0 / 6400.0 * (190 - sunAngle) * (190 - sunAngle);
		shadow *= multiplier;
	}
	if(shadow > 1.0)
		shadow = 1.0;
	if(shadow < 0.2)
		shadow = 0.2;
*/	

	col.rgb *= calcDiffuseLightingWithShadows();
	
	gl_FragColor = col;
}