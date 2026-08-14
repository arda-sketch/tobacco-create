# Create: Tobacco V1 Playtesting

This document records the provisional V1 balance in player-facing units. The
authoritative values used by code live in
`src/main/java/com/createtobacco/smoking/SmokingBalance.java`.

Every cigarette below additionally costs one Cigarette Paper and one
Cigarette Filter during Sequenced Assembly. Blend costs are shown per
four-item Mechanical Mixing batch, so one batch fills four cigarettes.

## Product balance

| Product | Recipe cost | Puffs | Dependence | Nicotine Rush | Special proc | Expected role |
| --- | --- | ---: | ---: | --- | --- | --- |
| MarlbOre Red | 2 Cut Virginia + 2 Cut Burley + Redstone -> 4 blends | 5 | 0.9 | 5 min, +5% movement, 5% damage reduction | 25%/puff: Haste I, 20 sec | General mining cigarette |
| WinStone Blue | 4 Cut Virginia + Lapis -> 4 blends | 5 | 0.9 | 5 min, +5% movement, 5% damage reduction | 35%/puff: 1-2 raw XP | Small exploration/XP bonus |
| Creperfield | 2 Cut Virginia + 2 Cut Burley + Gunpowder -> 4 blends | 5 | 0.9 | 5 min, +5% movement, 5% damage reduction | 10%/puff: non-destructive Microblast, Speed II + Haste II for 10 sec | Mobility and crowd spacing |
| Craftmel | 4 Cut Virginia -> 4 blends | 5 | 0.7 | 4 min, +5% movement, 5% damage reduction | None | Cheap/light baseline |
| Chunkman | 4 Cut Virginia + Cocoa Beans -> 4 blends | 5 | 0.9 | 5 min, +5% movement, 5% damage reduction | 25%/puff: +1 food, +0.5 saturation | Food-pressure support |
| KEnd | 3 Cut Virginia + 1 Cut Havana + Chorus Fruit -> 4 blends | 5 | 0.9 | 5 min, +5% movement, 5% damage reduction | 18%/puff: Ender Roulette | Risky utility and movement |
| Pigliament | 3 Cut Virginia + 1 Cut Burley + Gold Nugget -> 4 blends | 5 | 0.9 | 5 min, +5% movement, 5% damage reduction | 15%/puff: Resistance I, 10 sec | Short combat defense |
| Rothmines | 2 Cut Virginia + 2 Cut Burley + Coal -> 4 blends | 5 | 0.9 | 5 min, +5% movement, 5% damage reduction | Full completion only: Haste I, 60 sec | Predictable mining completion bonus |
| Bedromorkanal | 2 Cut Virginia + 2 Cut Burley + Dried Kelp -> 4 blends | 5 | 0.9 | 5 min, +5% movement, 5% damage reduction | 15%/puff: direct heal 2 HP | Emergency low-health support |
| Minecristo No. 1 | 5 Cured Havana Leaves + 250 mB Water through wrapper/filler production | 8 | 1.6 | 8 min, +10% movement, 5% damage reduction | None | Premium, reliable long Rush |
| Stoneo y Glowlieta | Minecristo material chain + Glowstone Dust | 8 | 1.4 | 7 min, +5% movement, 5% damage reduction | 30%/puff: Night Vision 45 sec + Glowing 20 sec | Cave/night utility with visible downside |

Ender Roulette weights are 40% safe teleport, 20% Slow Falling, 15% Jump
Boost II, 15% Invisibility, and 10% Levitation. The product proc must first
pass its 18% per-puff roll.

## Dependence and Withdrawal baseline

- Dependence tiers: none below 20, Mild 20-39.999, Moderate 40-59.999, High
  60-79.999, Severe 80-100.
- Dependence decays by 2.5 after each 72,000 active online ticks (one gameplay
  hour). Offline time does not advance this accumulator.
- Safe craving intervals are Mild 40 minutes, Moderate 30 minutes, High 20
  minutes, and Severe 15 minutes of active online time.
- Withdrawal remains episodic. Existing episode intervals, durations,
  penalties, nausea chances, and puff-relief thresholds are centralized in
  `SmokingBalance.WithdrawalProfile`.

## Manual playtest checklist

Use `/createtobacco reset <player>` before each independent scenario and
`/createtobacco status <player>` to record before/after values.

### Ordinary exploration

- Carry one standard cigarette, one cigar, Flint and Steel, and no debug
  effects.
- Travel for 20-30 minutes, smoke only when naturally useful, and record how
  often Rush is continuously active.
- Verify partial items keep their puff component after inventory moves,
  dropping/picking up, portals, logout/login, and death.

### Mining

- Compare 15 minutes with no product, MarlbOre, Creperfield, and Rothmines.
- Record effective Haste uptime and whether repeated smoking feels mandatory.
- Confirm Haste refreshes duration at the same amplifier rather than becoming
  Haste II, except Creperfield's explicitly defined Haste II proc.

### Combat

- Compare incoming damage with no effects, Rush only, Pigliament Resistance
  only, and both together.
- Verify the smoker never takes explosion damage from Microblast and nearby
  players/mobs receive only moderate knockback.
- Repeat next to Create contraptions, belts, vaults, and chests; no blocks or
  storage may change.

### Food pressure

- Start below full hunger and smoke several Chunkman cigarettes.
- Record food restored, saturation behavior, and completion exhaustion.
- Confirm food and saturation never exceed vanilla caps and decide during
  playtesting whether the expected 1.25 food points per cigarette is too high.

### Low health

- Test Bedromorkanal at one heart, half health, and full health.
- Each forced proc must heal exactly 2 HP once, never exceed max health, and
  do nothing harmful at full health.

### Multiplayer

- Player A: dependence 90, partial KEnd, partial KEnd pack.
- Player B: dependence 0, MarlbOre, different-brand partial pack.
- Reconnect both, change dimensions, restart the server, and verify status,
  effects, ItemStack components, and pack counts remain independent.
- Test simultaneous pack right-click, spam-click, full inventories, count one
  to Empty Pack, drop/pickup, and death. No cigarette may be lost, duplicated,
  mixed, or produce a negative count.

### Long session and high dependence

- Run at least one session beyond 72,000 active ticks and confirm exactly 2.5
  dependence decay per completed interval, including a large debug-set
  accumulator scenario.
- At each tier, wait through its full safe interval, several randomized
  Withdrawal episodes, cough checks, and puff relief.
- Quit during the safe interval and during a scheduled cough/episode timer;
  offline time must not advance either timer.

### Quitting and rejoining

- Disconnect while holding use before a puff completes. Reconnect and verify
  that no puff, dependence, relief progress, or product proc was awarded.
- Disconnect with lit partial cigarettes and cigars in inventory. Reconnect
  before and after a server restart and compare `remaining_puffs` and `lit`.
- Finish the item after reconnect; completion behavior and `Smoke and Gears`
  must occur exactly once.

## Interactions to watch during playtesting

- Creperfield refreshes fixed Speed II/Haste II durations; vanilla effect
  replacement prevents amplifier stacking, but repeated crowd knockback can
  still be strong in dense fights.
- Pigliament Resistance I and the main Rush reduction combine
  multiplicatively (about 24% reduction before armor for the current 20% and
  5% values). This is intentional for now but needs combat observation.
- Bedromorkanal averages 0.75 heal procs, or 1.5 HP, per five-puff cigarette.
  Production cost is its current limiter; there is no separate heal cooldown.
- Chunkman averages 1.25 food points per cigarette while completion costs only
  0.6 exhaustion. It may become a net food source and is the highest-priority
  food-balance watch item.
- Rothmines cannot stack above Haste I but repeated full cigarettes can keep
  refreshing its 60-second duration.
- KEnd safe teleport has no cooldown beyond proc chance and smoking time.
  Repeated use can become a mobility tool, though failed destinations do
  nothing and the algorithm rejects collision/liquid landings.
- Stoneo commonly refreshes Night Vision because eight puffs each have a 30%
  roll. Glowing refreshes at the same time and remains its multiplayer cost.
- Completing another smoking item removes and replaces Nicotine Rush; it does
  not stack strength. A lower profile can shorten/weaken an existing premium
  Rush, while repeated matching products can maintain uptime.

These are playtesting risks, not automatic balance changes. Record actual
session outcomes before changing `SmokingBalance`.
