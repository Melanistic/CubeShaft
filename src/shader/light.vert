#version 130

out vec4 shadowCoord;
out vec3 color;
out float br;
out vec4 texCoord;
out vec3 lpos, normal;

void main() {
	vec4 vPos = gl_ModelViewMatrix * gl_Vertex;
	shadowCoord = gl_TextureMatrix[7] * vPos;
	
	color = vec3(1, 1, 1);
	
	vec3 vnormal = gl_NormalMatrix*gl_Normal;
	
	lpos = (gl_LightSource[0].position.xyz - vPos.xyz).xyz;
	br = clamp(dot(vnormal, normalize(lpos)), 0.0, 1.0);
	
	normal = gl_NormalMatrix * gl_Normal;
	
	texCoord = gl_MultiTexCoord0;
	gl_Position = ftransform();
}