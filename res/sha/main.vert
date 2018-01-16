#version 120

attribute vec3 in_position;
attribute vec4 in_color;

varying vec4 color;
varying vec4 fragPos;

void main() {
	color = in_color;
	fragPos = gl_ModelViewMatrix * gl_Vertex;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}