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

PAL Migration 01 — migrate smoking player animation from manual NeoForge arm transforms to Player Animation Library while preserving gameplay.

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
Cigarette Filters are not mixing ingredients. Finished cigarettes combine
Paper, one Prepared Tobacco Blend, and a Filter through Create Sequenced
Assembly.

Phase 5 registers nine non-smokable finished `CigaretteItem` products and
assembles each through one-pass Create Sequenced Assembly: Cigarette Paper,
brand-specific Prepared Tobacco Blend deployed, Mechanical Press, Cigarette
Filter deployed, Mechanical Saw, then the matching finished cigarette. All
nine recipes share one hidden `incomplete_cigarette`; Create's sequenced
assembly data component binds it to the selected brand recipe after step one.

Phase 6 adds a separate cigar production chain. Four Cured Havana Leaves plus
250 mB Water compact into a Fermented Havana Tobacco Bundle. Sawing produces
`cigar_filler`; pressing one Cured Havana Leaf produces a Cigar Wrapper.
Minecristo No. 1 deploys Havana filler onto the wrapper and presses it.
Stoneo y Glowlieta first mixes Cigar Filler with Glowstone Dust to create
`glowstone_cigar_filler`, then uses the same one-Deployer/one-Press assembly.
The abandoned Cobbliba/Mixed Filler development chain has been removed.

Phase 7 introduces the immutable `SmokingItemState` data component with
`remainingPuffs`, `lit`, and `burnTicksRemaining`. Finished cigarettes default
to five puffs and finished cigars to eight; both default to unlit and are
non-stackable because the state belongs to each ItemStack.
`AbstractSmokingItem` provides shared server-authoritative ignition and puff
consumption. An unlit smoking item can only be ignited with vanilla Flint and
Steel held in the other hand; ignition damages it once and starts its natural
smoulder timer. A lit item requires 24 ticks (1.2 seconds) of uninterrupted use
per puff. Active smoking is one continuous vanilla use action: while the button
is held, exactly one server-authoritative puff completes every 24 ticks. Releasing
early leaves the unfinished interval unconsumed; holding continues into the next
puff without lowering the hand. Each completed puff immediately updates the data
component and refreshes the smoulder interval, and the final puff removes the item
without producing a butt. Passive smouldering consumes one puff per
60 seconds for cigarettes and 90 seconds for cigars while the smoking item is
in a player's ordinary inventory. Passive burn pauses during an active manual
puff, so the same interval cannot naturally expire and also be consumed by the
manual completion. Passive burn never grants dependence, Withdrawal relief,
brand procs, or completion rewards.

Phase 8 registers a custom translucent `tobacco_smoke` particle backed by the
eight vanilla animated smoke sprites. Successful server-authoritative puffs
send a larger cloud near the player's mouth. While a lit item is actively held
in its 24-tick use action, the client creates a sparse local wisp near the used
hand without continuous networking. Smoking keeps `UseAnim.NONE`, but player posing is now delegated to Player Animation Library (PAL). The client registers a dedicated smoking animation layer and selects physical right/left animations from vanilla-synchronized item-use state. Each use plays a short `smoking_raise` once and then loops `smoking_hold` for as long as the button remains held; the hold loop is 1.2 seconds so its inhale beat stays aligned with the 24-tick server puff cadence. PAL drives the same Blockbench-authored animation in third person and first person (`THIRD_PERSON_MODEL`), including separate `right_item`/`left_item` bones for cigarette orientation. The old manual `IClientItemExtensions` transform and NeoForge ArmPose enum extension are removed. Component updates from natural smouldering still do not trigger a hand re-equip animation, and active use may continue when only the smoking-state component changes.

Rapid smoking is tracked as runtime-only server state: a gap of 30 ticks (1.5 seconds) or more between successful puffs resets the streak. Puff 3 has a 20% chance for Nausea I for 2 seconds; puff 4 has a 40% chance for Nausea II for 2 seconds; puff 5+ has a 60% chance for Nausea II for 5 seconds. The streak is intentionally not serialized and grants no extra benefit.

Phase 9 adds persistent player `SmokingData` through NeoForge 1.21.1 Data
Attachments. Its codec stores dependence, active satisfaction time, decay
accumulator, the reserved withdrawal countdown, and relief-puff count;
`copyOnDeath()` preserves it across respawn. Only active server-player ticks
advance the timers. Dependence decays by 2.5 every 72,000 active ticks and is
clamped to 0–100. Each cigarette puff adds 0.18 dependence (0.9 total), while
each cigar puff adds 0.175 (1.4 total). Only the final puff resets
`activeTicksSinceSatisfied`, adds completion exhaustion, and replaces Nicotine
Rush with a fresh duration: 6,000 ticks for cigarettes or 8,400 for cigars.
Nicotine Rush grants 5% movement speed for the standard profile and its
separate server damage hook reduces incoming damage by the profile value.
Withdrawal episodes and brand-specific effects are implemented in later
sections below.

Phase 10 adds a server-side episodic Withdrawal scheduler. Dependence tiers are
none below 20, Mild at 20–39.999, Moderate at 40–59.999, High at 60–79.999,
and Severe at 80–100. After a completed smoking item, active online safe times
are respectively 40, 30, 20, and 15 minutes. Once safe time ends, the server
randomizes every episode interval: 6–10, 4–7, 3–5, or 2–4 minutes. Withdrawal
is applied only for an episode (30/40/50/60 seconds), with exact movement
penalties of 3/5/7/10% and block-breaking penalties of 5/8/12/15%. Episode
starts have a tier-based 5/15/25/35% chance of 3–5 seconds of vanilla Nausea.
Successful puffs relieve an active Withdrawal episode immediately by one
visible tier per puff: Severe IV -> High III -> Moderate II -> Mild I -> clear.
The remaining episode duration is preserved while its amplifier is lowered;
Nausea is rolled only when an episode starts, not on downgrade. Puff relief does
not reset `activeTicksSinceSatisfied`; completing the whole item still performs
the only satisfaction reset.

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
  plays sound/particles, moderately pushes living entities within 5.0 blocks,
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

Phase 12 originally introduced a generic data-driven pack, but V1 Polish 01
replaces that design because ten non-stackable cigarettes cannot physically
fit into a Create Basin alongside the Empty Pack. Packaging is now
brand-specific. `empty_cigarette_pack` remains a shared input, while nine
finished pack items (`<brand>_pack`) each store only an immutable remaining
count 1–10; the pack item itself defines the cigarette type.

Each full pack is made in a 4x3 Create Mechanical Crafting grid from ten full,
unlit cigarettes of the matching brand, one Empty Cigarette Pack, and one
brand dye. The fixed dye map is MarlbOre Red=red, WinStone Blue=blue,
Creperfield=lime, Craftmel=orange, Chunkman=brown, KEnd=purple,
Pigliament=yellow, Rothmines=black, and Bedromorkanal=light blue. Cigars are
never valid pack contents. Right-click extraction remains
server-authoritative: exactly one fresh cigarette is inserted into player
inventory before count is decremented, and count one converts to an Empty Pack.
The `Pack It Up` advancement accepts any brand pack at count 10.

V1 Polish 01 also adds a non-placeable `cigarette_case` portable container.
Right-click opens a 5x3 (15 slot) menu. Slots accept only the nine active
cigarettes and two active cigars, including lit and partially smoked ItemStacks,
so their Data Components are preserved. The case stores its inventory through
the vanilla `minecraft:container` Data Component. Natural smouldering is paused
while a smoking item is nested inside the closed case.

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

Phase 14 adds a compact four-entry advancement branch. `Golden Leaf` is
awarded for obtaining any cured Virginia, Burley, or Havana leaf, and
`Industrial Habit` for obtaining any of the nine active finished cigarettes.
Both use vanilla `inventory_changed` criteria. `Pack It Up` uses a dedicated
server criterion and is awarded only while any brand-specific Cigarette Pack
contains a validated count component of 10; Empty Packs and partial
packs do not qualify. `Smoke and Gears` is fired from the common final-puff
completion handler only for the nine active cigarettes, Minecristo No. 1, or
Stoneo y Glowlieta. Ignition, item acquisition, and partial smoking never fire
it.

Phase 15 audited the common/client boundary, player attachment lifetime,
ItemStack Data Components, product effects, coughing, packs, and teleport
safety. The only code change is consistent `SmokingItemState` validation:
both persistent and network construction now reject puff counts outside
0–64. Normal products remain fixed to their five- or eight-puff profiles.

A real local dedicated-server regression used two simultaneous clients,
`Phase15A` and `Phase15B`. Their dependence values (90 and 0), partial smoking
items, and different partial packs remained independent. Server-console debug
handlers applied MarlbOre and KEnd behavior once, and Phase15A travelled
Overworld → Nether → End → Overworld without losing its attachment. After
disconnect and a full server restart, Phase15A still had dependence 90.000, a
lit KEnd with three puffs, and a KEnd pack with count four. Only reconnect
ticks advanced active time; the offline interval did not advance dependence,
craving, or cough scheduling. The dedicated server loaded all 2,828 recipes
and 2,478 advancements without client-class linkage or mod errors.

The audit also confirms that product rolls, XP, food, healing, effects,
Microblast knockback, puff completion, and pack extraction run only from
server-side paths. Microblast never creates an Explosion and therefore cannot
damage blocks, fire, Create contraptions, or storage. Client imports remain
isolated under `client.particle` with a `Dist.CLIENT` subscriber. Death copies
SmokingData through the attachment's `copyOnDeath()`, while ItemStack state
uses Minecraft's normal persistent and network-synchronized Data Components.

Phase 16 makes `SmokingBalance` the single source of truth for provisional V1
product, Nicotine Rush, dependence decay, cough, Withdrawal, and KEnd values.
Every active product now has an explicit `SmokingProfile` containing puffs,
total dependence, Rush duration/amplifier/movement/damage reduction,
completion exhaustion, and special proc chance. Product effect durations,
values, Ender Roulette weights, teleport limits, and Withdrawal profiles live
beside those profiles instead of being distributed across handlers, effects,
attachments, and enums. Runtime behavior is otherwise unchanged, except that
the Phase 16 requested craving safe intervals are now Mild 40, Moderate 30,
High 20, and Severe 15 active minutes.

`PLAYTESTING.md` documents recipe cost, puffs, dependence, Rush, special proc,
and intended role for all nine cigarettes plus Minecristo No. 1 and Stoneo y
Glowlieta. It also contains manual exploration, mining, combat, food, health,
multiplayer, long-session, high-dependence, and reconnect tests, plus a list of
interactions that should be observed before any future balance changes. Phase
16 is the V1 feature-complete boundary; subsequent work is manual playtesting
and polish, not a new gameplay phase.

## V1 Polish 02 survival/worldgen and crop economy

Three separate wild tobacco blocks provide the survival entry point:
`wild_virginia_tobacco`, `wild_burley_tobacco`, and `wild_havana_tobacco`.
Each is a true two-block `DoublePlantBlock` with lower/upper visual halves,
separate from the compact one-block cultivated crop. Virginia targets temperate
plains/forest biomes, Burley targets meadow/windswept/savanna biomes, and Havana
targets jungle biomes. Their placed-feature rarity filters are currently 42,
50, and 30 respectively. A patch still appears as a recognizable local colony,
but colonies are much less common than in Polish 01. Each candidate re-resolves
its local `MOTION_BLOCKING_NO_LEAVES` surface before placing both halves, avoiding
the old one-block-depression effect and reducing jungle canopy placement.

Breaking a wild plant always yields its matching seed, has a 25% chance for a
second seed, and a 65% chance for one Fresh Leaf. Wild plants are therefore a
useful discovery reward but are still primarily the entry point to farming. A
mature cultivated crop yields two Fresh Leaves guaranteed, has a 35% chance for
a third Fresh Leaf, always returns one seed, and has a 35% chance for a second
seed. Immature cultivated crops still return their planted seed. Millstone
yields are unchanged; the economy increase is deliberately placed at the farm
harvest stage rather than multiplied again during processing.

## Explicitly out of scope

Do not implement new processing machines, custom ignition tools, cigarette
butts, additional cigarette products, or additional cigars before a later
explicit design decision.

## Verification

```powershell
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat runServer
```

The dedicated server uses `runs/server`. Its first start creates `eula.txt`; the
EULA must be reviewed and accepted by the developer before a full server start.
