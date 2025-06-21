


# GroovyEngine

A lightweight scripting layer for Minecraft Fabric that transforms mod development through Groovy scripting. Instead of compiling full Java mods, drop scripts into your `.minecraft/GroovyEngine/` folder and watch your ideas come to life instantly.

## ⚠️ Important Notice

**GroovyEngine is currently in heavy development and game-breaking bugs are expected. Use at your own risk.** [1](#0-0) 

Ai generated wiki until I Write my own
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/LuckyMcDev/GroovyEngine_1211_fabric)

## 🚀 Features

### Core Scripting Engine
- **Groovy Scripting API** - Write mods using Groovy scripts instead of full Java projects [2](#0-1) 
- **Live Event Hooks** - Register to events like blockBreak, playerJoin, tick, and more [3](#0-2) 
- **Script Folder Structure** - Automatic client/server script separation based on subfolder placement [4](#0-3) 

### Content Creation
- **Easy Registration** - Register items, blocks, and other content through simple script bindings [5](#0-4) 
- **Automatic Asset Generation** - Automatically generates resource packs and datapacks for your content [6](#0-5) 
- **Builder Pattern APIs** - Intuitive builders for items, blocks, recipes, and shaders [7](#0-6) 

### Development Tools
- **ImGui Debug GUI** - In-game interface for editing scripts and viewing output [8](#0-7) 
- **Integrated Editor** - Built-in script editor with syntax highlighting and documentation [9](#0-8) 
- **Live Script Reloading** - Scripts automatically reload on datapack reload [10](#0-9) 

### Advanced Features
- **Custom Shaders** - Define client-side shaders via scripts (Future) [11](#0-10) 
- **Block Model Tools** - Build and preview models for block entities in-game (Future) [12](#0-11) 
- **UI Components** - Easily define custom in-game UIs via Groovy (Future) [13](#0-12) 
- **Modular Design** - Other mods can register new bindings or extend GroovyEngine [14](#0-13) 

## 📦 Installation

### Requirements
- **Minecraft**: 1.21.1 [15](#0-14) 
- **Java**: 21 or higher [16](#0-15) 
- **Fabric Loader**: 0.16.14 or higher [17](#0-16) 
- **Fabric API**: Latest version [18](#0-17) 

### Dependencies
The mod automatically includes these dependencies:
- **OpenLoader** - For datapack and resourcepack loading [19](#0-18) 
- **ImGui Java** - For the in-game editor interface [20](#0-19) 
- **Satin** - For shader functionality [21](#0-20) 

## 🎯 Quick Start

### Basic Setup
1. Install the mod in your Fabric mods folder
2. Launch Minecraft - GroovyEngine will automatically create the required folder structure [22](#0-21) 
3. Navigate to `.minecraft/GroovyEngine/scripts/` to find your script folders:
   - `client/` - Scripts that run only on the client side
   - `server/` - Scripts that run only on the server side
   - `main/` - Scripts that run on both sides

### Your First Script

Create a file called `welcome.groovy` in the appropriate folder:

```groovy
// Listen for player join events
Events.on("playerJoin", { ctx ->
    Logger.info("Welcome " + ctx.player.name + "!")
})

// Register a custom item
Events.on("registerItem", { ctx ->
    ItemBuilder("welcome_stick")
        .displayName("Welcome Stick")
        .tooltip("A friendly greeting!")
        .build()
})
```

## 🏗️ Project Structure

```
src/main/java/io/github/luckymcdev/groovyengine/
├── editor/          # ImGui-based in-game editor
├── scripting/       # Core scripting engine and APIs
│   ├── builders/    # Builder APIs for content creation
│   ├── eventservice/# Event system for game hooks
│   └── core/        # Script loading and management
├── generators/      # Asset generation system
├── imgui/           # ImGui integration
└── util/            # Utility classes
```

## 🔧 Development

### Building from Source
1. Clone the repository
2. Ensure you have Java 21 installed [23](#0-22) 
3. Run `./gradlew build`

### Architecture
- **Main Entry Point**: GroovyEngine.java initializes the mod and script manager [24](#0-23) 
- **Script Management**: GroovyScriptManager handles script loading and execution [25](#0-24) 
- **Event System**: EventRegistry manages game event hooks and script callbacks [26](#0-25) 

## 📄 License

This project is released under the **CC0 1.0 Universal** license, dedicating it to the public domain. [27](#0-26) 

## 👤 Author

Created by **Lucky.dev** [28](#0-27) 

## 🤝 Contributing

GroovyEngine is open to contributions! Whether you're fixing bugs, adding features, or improving documentation, your help is welcome.

## 📚 Additional Resources

- **In-Game Documentation**: Press the editor key (configurable) to access the built-in documentation
- **Wiki**: Check the project wiki for detailed API documentation and examples
- **Issues**: Report bugs and request features on the project's issue tracker

---

**Note**: GroovyEngine aims to make Minecraft modding accessible to scripters and rapid prototypers. While it may not replace full Java mods for complex projects, it excels at quick experiments, data-driven content, and educational purposes. [29](#0-28) 

## Notes

This README provides a comprehensive overview of the GroovyEngine project based on the current codebase. The project is a Minecraft Fabric mod that enables Groovy-based scripting for mod development, featuring an integrated editor, automatic asset generation, and a rich API for content creation. All claims in this README are supported by citations from the actual codebase, ensuring accuracy and traceability to the source material.
