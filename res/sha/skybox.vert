#version 120

attribute vec3 in_position;
attribute vec2 in_texCoords;

varying vec2 texCoords;

void main() {
	texCoords = in_texCoords;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}