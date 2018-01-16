#version 120

varying vec4 color;
varying vec4 fragPos;

uniform vec3 fogColor;
uniform float viewDist;
uniform vec3 camPos;

void main() {
	float dist = length(fragPos) / viewDist * 6.58 - 2.85;
	if (dist > 1)
		dist = 1;
	if (dist < 0)
		dist = 0;
	gl_FragColor = mix(color, vec4(fogColor, 1.0), dist);
}