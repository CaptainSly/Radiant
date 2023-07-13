#version 330

in vec2 outTextCoords;

out vec4 fragColor;

uniform sampler2D txtSampler;
uniform vec4 diffuse;
uniform int hasTexture;

void main() {
    if (hasTexture == 1)
        fragColor = texture(txtSampler, outTextCoords);
    else
        fragColor = diffuse;
}