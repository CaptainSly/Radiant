#version 330

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 texCoords;

out vec3 outPosition;
out vec3 outNormal;
out vec2 outTextCoords;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

void main()
{
	mat4 modelViewMatrix = view * model;
	vec4 mvPosition = modelViewMatrix * vec4(position, 1.0);
    gl_Position = projection * mvPosition;
	outPosition = mvPosition.xyz;
	outNormal = normalize(modelViewMatrix * vec4(normal, 0.0)).xyz;
	outTextCoords = texCoords;
}