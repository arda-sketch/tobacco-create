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

Phase 0 — Skeleton + Create.

The deliverable is an otherwise empty addon that loads with Create on both the
development client and the dedicated development server. Phase 0 contains only
the main mod entry point, empty registry infrastructure, a base creative tab,
build metadata, and run configurations.

## Explicitly out of scope

Do not implement tobacco, cigarettes, recipes, crops, status-effect behavior,
particles, data payloads, attachments, or other gameplay mechanics before the
next phase is explicitly started.

## Verification

```powershell
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
```

The dedicated server uses `runs/server`. Its first start creates `eula.txt`; the
EULA must be reviewed and accepted by the developer before a full server start.
