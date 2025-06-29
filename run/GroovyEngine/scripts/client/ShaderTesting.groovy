//priority=2

Logger.info("This is a log line from the ingame editor")

def blitShader = ShaderManager.create("groovyengine:blit")
        .path("groovyengine:shaders/post/blit.json")

blitShader.build();

ShaderManager.register("Crt Shader", blitShader);


