# Create: Tobacco — Project Context

## Fixed stack

- Minecraft: 1.21.1
- NeoForge: 21.1.228
- Java: 21
- Create: 6.0.10
- Create Maven artifact: `com.simibubi.create:create-1.21.1:6.0.10-223`
- Gradle toolchain: NeoForge ModDevGradle
- IDE: Visual Studio Code
- Mod id: `create_tobacco`
- Base package: `com.createtobacco`

## Current phase

Phase 3 — Basic Create processing.

The Phase 0 skeleton loads with Create on both the development client and the
dedicated development server. Phase 1 added the requested basic items,
translations, item models, textures, and creative-tab entries. Phase 2 adds
Virginia, Burley, and Havana farmland crops with eight growth stages,
vanilla-style random growth and bone meal support, and age-aware loot. Phase 3
adds data-driven smoking, milling, cutting, and pressing recipes using the
Create 6.0.10 recipe formats.

## Explicitly out of scope

Do not implement wild tobacco world generation, new machines or blocks,
smoking behavior, cigarettes, status-effect behavior, particles, data
payloads, attachments, or other gameplay mechanics before the next phase is
explicitly started.

## Verification

```powershell
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
```

The dedicated server uses `runs/server`. Its first start creates `eula.txt`; the
EULA must be reviewed and accepted by the developer before a full server start.
