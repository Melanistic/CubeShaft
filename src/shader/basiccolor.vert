varying vec4 col;

void main() {
	col = gl_Color;
	gl_Position = ftransform();
}