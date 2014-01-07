#version 130

#define DIFFUSE_CONTRAST 0.8

//#define SHADOWS
//#define SHADOWS_SOFT
//#define SHADOWS_SMOOTH_EDGES

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
#ifdef SHADOWS

	vec4 newShadowCoord = shadowCoord;

#ifdef SHADOWS_SOFT
	float s = 1.0 / 4096.0;

	float shadow = 0.0;
	
	if(shadowCoord.w > 1.0) {
		float numLoops = 0.0;
		
		float time = 1.0;
		
		float minmax = 1.0 * time;
		float inc = 0.25 * time;
		
		for(float y = -minmax; y <= minmax; y += inc) {
			for(float x = -minmax; x <= minmax; x += inc) {
				shadow += shadow2DProj(shadowMap, newShadowCoord + vec4(x * s, y * s, 0, 0)).x;
				numLoops += 1.0;
			}
		}
		
		shadow /= numLoops;
	}
	
	return shadow;
#elif defined(SHADOWS_SMOOTH_EDGES)
	float s = 1.0 / 4096.0;

	float shadow = 0.0;
	float numLoops = 0.0;
	
	float time = 4.0;
	
	float minmax = .01 * time;
	float inc = .0025 * time;
	
	for(float y = -minmax; y <= minmax; y += inc) {
		for(float x = -minmax; x <= minmax; x += inc) {
			shadow += textureProj(shadowMap, newShadowCoord + vec4(x * s, y * s, 0, 0));
			numLoops += 1.0;
		}
	}
	
	shadow /= numLoops;
	shadow = shadow < 0.5 ? 0.0 : 1.0;
	
	return shadow;
#else
	return textureProj(shadowMap, newShadowCoord);
#endif
	
#else
	return 1.0;
#endif
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
*/
	col.rgb *= calcDiffuseLightingWithShadows();
	
//	float colorsPerChannel = 4.0;
//	col.r = float(int(col.r*colorsPerChannel)/colorsPerChannel);
//	col.g = float(int(col.g*colorsPerChannel)/colorsPerChannel);
//	col.b = float(int(col.b*colorsPerChannel)/colorsPerChannel);
	
	gl_FragColor = col;
}