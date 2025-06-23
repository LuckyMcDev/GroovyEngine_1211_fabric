#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;
uniform vec2 InSize;

in vec2 texCoord;
out vec4 fragColor;

float rand(vec2 co) {
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main() {
    vec2 uv = texCoord - 0.5;
    float dist = dot(uv, uv);
    uv += uv * dist * 0.2;
    uv += 0.5;

    float shift = 1.0 / InSize.x;
    float r = texture(DiffuseSampler, uv + vec2(shift, 0)).r;
    float g = texture(DiffuseSampler, uv).g;
    float b = texture(DiffuseSampler, uv - vec2(shift, 0)).b;

    vec3 color = vec3(r, g, b);

    float scanline = sin(texCoord.y * InSize.y * 1.5) * 0.04;
    color -= scanline;

    float flicker = 0.02 * rand(vec2(Time, texCoord.y));
    color *= 1.0 - flicker;

    fragColor = vec4(color, 1.0);
}
