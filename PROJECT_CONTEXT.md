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

Phase 13 — Operator development debug commands.

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

Phase 8 registers a custom translucent `tobacco_smoke` particle backed by the
eight vanilla animated smoke sprites. Successful server-authoritative puffs
send a small cloud near the player's mouth. While a lit item is actively held
in its 24-tick use action, the client creates a sparse local wisp near the used
hand without continuous networking. Smoking keeps the vanilla `DRINK` use
animation so first-person and tracked third-person players use Minecraft's
normal synchronized use-item pose. Position constants live in
`AbstractSmokingItem` for later visual tuning.

Phase 9 adds persistent player `SmokingData` through NeoForge 1.21.1 Data
Attachments. Its codec stores dependence, active satisfaction time, decay
accumulator, the reserved withdrawal countdown, and relief-puff count;
`copyOnDeath()` preserves it across respawn. Only active server-player ticks
advance the timers. Dependence decays by 2.5 every 72,000 active ticks and is
clamped to 0–100. Each cigarette puff adds 0.18 dependence (0.9 total), while
each cigar puff adds 0.175 (1.4 total). Only the final puff resets
`activeTicksSinceSatisfied`, adds completion exhaustion, and replaces Nicotine
Rush with a fresh duration: 6,000 ticks for cigarettes or 8,400 for cigars.
Nicotine Rush grants 5% movement speed and its separate server damage hook
reduces incoming damage by 5%. Withdrawal episodes and brand-specific effects
remain unimplemented.

Phase 10 adds a server-side episodic Withdrawal scheduler. Dependence tiers are
none below 20, Mild at 20–39.999, Moderate at 40–59.999, High at 60–79.999,
and Severe at 80–100. After a completed smoking item, active online safe times
are respectively 30, 20, 15, and 10 minutes. Once safe time ends, the server
randomizes every episode interval: 6–10, 4–7, 3–5, or 2–4 minutes. Withdrawal
is applied only for an episode (30/40/50/60 seconds), with exact movement
penalties of 3/5/7/10% and block-breaking penalties of 5/8/12/15%. Episode
starts have a tier-based 5/15/25/35% chance of 3–5 seconds of vanilla Nausea.
Successful puffs count relief only while Withdrawal is active; 2/3/4/5 puffs
remove the current episode without resetting `activeTicksSinceSatisfied`.
Completing the whole item still performs the only satisfaction reset.

Lit smoking items now use Minecraft's built-in item bar to show the remaining
puff ratio, and their tooltip shows the exact current/default puff count. This
is item-local UI rather than a persistent player HUD.

Phase 11 centralizes provisional product balance in `SmokingBalance` and all
production proc/completion behavior in `SmokingEffects`. `AbstractSmokingItem`
only reports a genuinely completed server puff and final consumption. Product
design is now final for this phase:

- MarlbOre Red: Redstone; 25% per puff for Haste I (20 seconds).
- WinStone Blue: Lapis; 35% per puff for 1–2 raw experience points.
- Creperfield: Gunpowder; 10% per puff for a non-destructive Microblast. It
  plays sound/particles, moderately pushes living entities within 3.25 blocks,
  and grants the smoker Speed II plus Haste II for 10 seconds. It never creates
  an explosion, block damage, fire, or direct damage.
- Craftmel: basic/light; no proc, 0.7 total dependence, 4-minute Nicotine Rush.
- Chunkman: Cocoa; 25% per puff for one food point and 0.5 saturation.
- KEnd: Chorus Fruit; 18% per puff for Ender Roulette. Outcomes are safe
  vanilla-style chorus teleport (40%), Slow Falling I (20%), Jump Boost II
  (15%), Invisibility (15%), or Levitation I (10%).
- Pigliament: Gold; 15% per puff for Resistance I (10 seconds).
- Rothmines: Coal; no puff proc, but full consumption grants Haste I for 60
  seconds in addition to normal completion.
- Bedromorkanal: Dried Kelp; 15% per puff for direct 2 HP healing.
- Minecristo No. 1: premium standard eight-puff cigar; no magic proc, 1.6 total
  dependence and an 8-minute stronger Nicotine Rush completion profile.
- Stoneo y Glowlieta: Glowstone eight-puff cigar; 30% per puff grants Night
  Vision for 45 seconds and Glowing for 20 seconds together.

Dependence tiers at or above 20 schedule independent cough checks every 2–4
active online minutes. Tier chances are 5/10/18/28%. A cough stops current item
use safely, plays a sound and smoke, and applies Slowness I for five seconds;
it causes no damage and does not modify dependence. The cough timer is part of
the persistent player attachment and never advances while offline.

`cobbliba_maduro` remains registered only for compatibility with existing
development worlds, but it is no longer in the active creative cigar roster.

Phase 12 adds `empty_cigarette_pack` and one generic `cigarette_pack`. Pack
contents are an immutable, persistent, network-synchronized Data Component
containing a validated finished-cigarette registry ID and count 1–10. Only the
nine active cigarette products are accepted; cigars and unknown IDs are
rejected. Packs never mix brands and are non-stackable while populated.

Create Cardboard is pressed into two Empty Cigarette Packs. Nine Create 6.0.10
compacting recipes each consume one Empty Pack and ten identical, full, unlit
finished cigarettes. Data-component ingredients reject lit or partially used
cigarettes, preventing puff restoration through repacking. Recipe outputs set
the generic pack's brand/count component directly, so no custom machine or
custom recipe serializer is necessary. Basin automation remains compatible
with belts, funnels, chutes, and Mechanical Arms.

Right-click extraction is server-authoritative. The pack first attempts to add
exactly one fresh saved-brand cigarette to inventory; only a successful full
insert decrements the component. Count one converts atomically into an Empty
Pack. Tooltip shows the stored cigarette display name and `count / 10`.

Phase 13 adds the operator-only `/createtobacco` development command tree
(permission level 2). `status` reports exact dependence/tier, active elapsed
time, safe craving interval/remaining time, craving state, active Withdrawal,
next episode, relief progress, and cough timer. Dependence can be queried,
set, or added with 0–100 clamping; craving elapsed time can be simulated or
reset. Withdrawal and cough commands reuse `WithdrawalSystem` and
`CoughingSystem`, not parallel debug implementations.

Product effect commands call the production `SmokingEffects` handler without
the random roll. KEnd outcome commands call the same `EnderRoulette` and safe
chorus teleport implementation as real puffs. Optional completion commands
invoke the real final-consumption handler, including product profile,
Nicotine Rush, Rothmines bonus, exhaustion, and satisfaction reset. The reset
command clears only Create Tobacco persistent smoking state and mod effects.

## Explicitly out of scope

Do not implement wild tobacco world generation, new machines or blocks,
custom ignition tools, cigarette
butts, additional cigarette products, or additional cigars before a later
phase explicitly starts.

## Verification

```powershell
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
```

The dedicated server uses `runs/server`. Its first start creates `eula.txt`; the
EULA must be reviewed and accepted by the developer before a full server start.
