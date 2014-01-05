#version 130

uniform sampler2DShadow shadowMap;
uniform sampler2D comp;
uniform float sunAngle;

in vec4 shadowCoord;
in vec3 color;
in float br;
in vec4 texCoord;
in vec3 lpos, normal;

void main() {
	vec4 col = texture2D(comp, texCoord.st);
	col.rgb *= br;
	
	float s = dot(lpos.xyz, lpos.xyz) / 2048.0;

	float shadow = 20.0;
	
	float smoothfactor = 1.0;
	
	for(float y = -.01 * smoothfactor; y <= .01 * smoothfactor; y += 0.0025 * smoothfactor) {
		for(float x = -.01 * smoothfactor; x <= .01 * smoothfactor; x += 0.0025 * smoothfactor) {
			shadow += textureProj(shadowMap, shadowCoord + vec4(x * s, y * s, 0, 0));
		}
	}
	shadow /= 9.0 * 9.0 + 20.0;

	
	if(sunAngle < 70 && sunAngle > -10) {
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
	
	col.rgb *= shadow;
	
	gl_FragColor = col;
}