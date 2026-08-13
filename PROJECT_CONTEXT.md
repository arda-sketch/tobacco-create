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

Phase 7 — Smoking item state and basic smoking.

The Phase 0 skeleton loads with Create on both the development client and the
dedicated development server. Phase 1 added the requested basic items,
translations, item models, textures, and creative-tab entries. Phase 2 adds
Virginia, Burley, and Havana farmland crops with eight growth stages,
vanilla-style random growth and bone meal support, and age-aware loot. Phase 3
adds data-driven smoking, milling, cutting, and pressing recipes using the
Create 6.0.10 recipe formats. Phase 4.5 establishes the final roster of nine
heatless, data-driven prepared tobacco blends: MarlbOre Red, WinStone Blue,
Creperfield, Craftmel, Chunkman, KEnd, Pigliament, Rothmines, and
Bedromorkanal.

Mechanical Mixing produces prepared tobacco blends only. Cigarette Paper and
Cigarette Filters are not mixing ingredients. A later Phase 5 will combine
Paper, one Prepared Tobacco Blend, and a Filter through Create Sequenced
Assembly to make a finished cigarette.

Phase 5 registers nine non-smokable finished `CigaretteItem` products and
assembles each through one-pass Create Sequenced Assembly: Cigarette Paper,
brand-specific Prepared Tobacco Blend deployed, Mechanical Press, Cigarette
Filter deployed, Mechanical Saw, then the matching finished cigarette. All
nine recipes share one hidden `incomplete_cigarette`; Create's sequenced
assembly data component binds it to the selected brand recipe after step one.

Phase 6 adds a separate cigar production chain. Four Cured Havana Leaves plus
250 mB Water compact into a Fermented Havana Tobacco Bundle. Three Cured
Havana Leaves, one Cured Burley Leaf, and 250 mB Water compact into a Mixed
Fermented Tobacco Bundle. Sawing produces `cigar_filler` or
`mixed_cigar_filler`; pressing one Cured Havana Leaf produces a Cigar Wrapper.
Minecristo No. 1 deploys Havana filler onto the wrapper and presses it;
Cobbliba Maduro deploys mixed filler and presses it. Both assemblies use one
hidden `incomplete_cigar`, one Deployer, one Press, and no loops.

Phase 7 introduces the immutable `SmokingItemState` data component with
`remainingPuffs` and `lit`. Finished cigarettes default to five puffs and
finished cigars to eight; both default to unlit and are non-stackable because
the state belongs to each ItemStack. `AbstractSmokingItem` provides shared
server-authoritative ignition and puff consumption. An unlit smoking item can
only be ignited with vanilla Flint and Steel held in the other hand; ignition
damages it once and produces a small sound/particle effect. A lit item requires
24 ticks (1.2 seconds) of uninterrupted use per puff. Early release consumes
nothing, successful completion decrements the data component, and the final
puff removes the item without producing a butt.

## Future brand concepts (do not implement yet)

- MarlbOre Red: redstone theme; about 25% per puff for Haste I, about 20 seconds.
- WinStone Blue: lapis theme; about 35% per puff for 1–2 raw experience points.
- Creperfield: about 8–10% per puff for a non-destructive Microblast, optional
  nearby knockback, Speed II and Haste II for about 8–10 seconds. It must not
  use a normal destructive explosion, fire, or direct explosion damage.
- Craftmel: basic cheap cigarette, no random proc, slightly weaker completion
  profile and slightly lower dependence.
- Chunkman: about 20–25% per puff to restore about one food point and a very
  small amount of saturation; exact values require later balancing.
- KEnd: about 12–15% per puff for safe Chorus Fruit-like teleportation up to
  roughly eight blocks; use valid safe destinations, never naive `setPos`.
- Pigliament: about 15% per puff for Resistance I, about 10 seconds.
- Rothmines: no random proc; after full consumption add Haste I for about 60
  seconds to the normal completion mechanics.
- Bedromorkanal: about 15% per puff to heal 2 HP without exceeding max health;
  use direct healing unless playtesting changes the design.

Future smoking design: Minecristo No. 1 is intended as a Havana-focused premium
eight-puff cigar with a longer/stronger base completion profile and higher
dependence. Stoneo y Glowlieta remains a future eight-puff Havana plus Glowstone
cigar; Glowstone belongs in the filler, not the wrapper, and its future proc is
Night Vision for about 45 seconds plus Glowing for about 20 seconds at about
30% chance per puff. None of this behavior is implemented yet.

## Explicitly out of scope

Do not implement wild tobacco world generation, new machines or blocks,
dependence, Withdrawal, Nicotine Rush, status-effect behavior, brand effects,
custom smoke particles, custom animations, packs, custom ignition tools,
cigarette butts, additional cigarette products, or additional cigars before a
later phase explicitly starts. Phase 7 contains only the basic vanilla-particle
ignition/puff feedback and the `SmokingItemState` component.

## Verification

```powershell
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
```

The dedicated server uses `runs/server`. Its first start creates `eula.txt`; the
EULA must be reviewed and accepted by the developer before a full server start.
