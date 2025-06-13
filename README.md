
DISCLAIMER

GroovyEngine is still a heavy WIP and game breaking Bugs and glitches are to be expected. Use at your own risk.

---

GroovyEngine

GroovyEngine is a lightweight scripting layer for Minecraft modding that makes it drastically easier to prototype and build mods using Groovy.

Instead of compiling full Java mods, just drop scripts in a .minecraft/GroovyEngine/ folder and get live behavior in-game.

Features

📜 Groovy Scripting API — Write mods with scripts instead of full Java projects.

🔁 Live Event Hooks — Register to events like blockBreak, playerJoin, tick, and more.

📦 Easy Registration — Register items, blocks, and other content through simple script bindings.

🛠 Script Folder Structure — Load client/server scripts based on subfolder placement.

🧩 Modular Design — Other mods can register new bindings or extend GroovyEngine.

🖼️ ImGui Debug GUI — In-game GUI for editing scripts and seeing output.

🎨 Future: Custom Shaders — Define client-side shaders via scripts.

🧱 Future: Block Model Tools — Build and preview models for block entities in-game.

🛑 Planned: Class Blacklisting — Protect sensitive Minecraft classes from access by scripts.

🔧 Future: UI Components — Easily define custom in-game UIs via Groovy.


Getting Started

Drop .groovy scripts into:

.minecraft/GroovyEngine/client/  # Runs on client only
.minecraft/GroovyEngine/server/  # Runs on server only

Scripts automatically reload on game launch. Add behaviors, register content, or listen to game events instantly.

Example

Events.on("playerJoin", { ctx ->
    Logger.info("Welcome " + ctx.player.name)
})

Events.on("registerItem) { ctx->
ItemBuilder("example_item").displayName("A Custom Item made with GroovyEngine").build()
}

---

