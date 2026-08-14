# Create: Tobacco — V1 Polish 01 Manual Test Plan

Run these tests after applying the patch. Do not commit the patch until `build` and
the critical tests are green.

## 0. Build / boot

```powershell
.\gradlew.bat --stop
.\gradlew.bat build
.\gradlew.bat runClient
```

Expected:
- build succeeds;
- Create 6.0.10 and Create: Tobacco load;
- integrated world opens without registry/datapack errors.

For dedicated-server smoke test later:

```powershell
.\gradlew.bat runServer
```

## 1. Wild tobacco survival entry

IMPORTANT: worldgen appears only in newly generated chunks. Prefer a fresh test world.

Expected biome groups:

- Virginia: Plains / Sunflower Plains / Forest / Flower Forest / Birch Forest / Old Growth Birch Forest.
- Burley: Meadow / Windswept Hills / Windswept Forest / Savanna / Savanna Plateau.
- Havana: Jungle / Sparse Jungle / Bamboo Jungle.

Useful commands:

```mcfunction
/locate biome minecraft:plains
/locate biome minecraft:meadow
/locate biome minecraft:jungle
```

If you need to inspect the blocks directly before drawing textures:

```mcfunction
/setblock ~ ~ ~ create_tobacco:wild_virginia_tobacco
/setblock ~2 ~ ~ create_tobacco:wild_burley_tobacco
/setblock ~4 ~ ~ create_tobacco:wild_havana_tobacco
```

Break at least 20 of each (use `/setblock` repeatedly if necessary):
- always at least one corresponding seed;
- sometimes a second seed (~50%);
- occasionally a matching fresh leaf (~25%);
- no wrong variety drops.

Worldgen frequency is intentionally provisional. Record whether each plant feels:
`too rare / good / too common`.

## 2. Cobbliba cleanup

JEI/search should no longer expose:

- Cobbliba Maduro
- Mixed Fermented Tobacco Bundle
- Mixed Cigar Filler

Commands such as these should fail as unknown items:

```mcfunction
/give @s create_tobacco:cobbliba_maduro
/give @s create_tobacco:mixed_cigar_filler
```

## 3. Natural smouldering

Give and ignite a cigarette:

```mcfunction
/give @s create_tobacco:craftmel
/give @s minecraft:flint_and_steel
```

Expected:
- ignition starts the burn countdown;
- tooltip shows remaining puffs plus approximate burn seconds;
- a cigarette left in normal player inventory loses one puff after about 60 seconds;
- after all five natural burn intervals it disappears;
- natural burn gives NO dependence, brand proc, Withdrawal relief, Rush, advancement or completion bonus;
- a cigar burns one puff roughly every 90 seconds;
- a manual successful puff refreshes that item's current burn interval.

Also test logout/reconnect and moving the item between inventory slots.

## 4. Smoking animation / smoke

Use F5 and, ideally, a second player.

Expected:
- no vanilla drinking bob;
- first-person hand moves smoothly toward the mouth over the first few ticks;
- held pose is stable for the rest of the 1.2 second puff;
- third-person player has a visible one-arm mouth pose;
- early release still consumes no puff;
- a successful puff produces a visibly larger smoke cloud;
- held lit item produces small intermittent smoke;
- puff sound is no longer a drinking sound.

Record visual tuning notes such as:
`too high / too low / too close / wrong rotation / third-person looks odd`.
The transform values are intentionally easy to tune in the next patch.

## 5. Creperfield enlarged Microblast

Deterministic test:

```mcfunction
/createtobacco effect trigger @s creperfield
```

Test beside:
- dirt/glass blocks;
- a chest;
- Create belts/shafts;
- living mobs;
- another player if possible.

Expected:
- larger, obvious explosion-like visual;
- no block destruction;
- no fire;
- smoker takes no explosion damage;
- nearby living entities are pushed moderately within ~4.5 blocks;
- Speed II + Haste II are granted for the configured short duration.

## 6. Withdrawal debug command

Reset, then trigger severe directly:

```mcfunction
/createtobacco reset @s
/createtobacco withdrawal trigger @s severe
/createtobacco status @s
```

Expected:
- command prepares dependence and elapsed craving state automatically;
- Severe Withdrawal remains after the next tick instead of immediately disappearing;
- status shows a Severe-valid dependence/timer state.

Repeat mild/moderate/high if desired.

## 7. Brand-specific packs — Mechanical Crafting

Each full pack is a 4x3 Mechanical Crafter recipe:

```text
CCCC
CPCD
CCCC
```

- `C`: ten identical FULL, UNLIT cigarettes of the target brand
- `P`: Empty Cigarette Pack
- `D`: brand dye

Dyes:

- MarlbOre Red: Red Dye
- WinStone Blue: Blue Dye
- Creperfield: Lime Dye
- Craftmel: Orange Dye
- Chunkman: Brown Dye
- KEnd: Purple Dye
- Pigliament: Yellow Dye
- Rothmines: Black Dye
- Bedromorkanal: Light Blue Dye

Expected:
- all twelve ingredients physically fit in 4x3;
- correct brand produces correct brand pack;
- output starts `10 / 10`;
- a lit cigarette must NOT match;
- a partially smoked cigarette must NOT match;
- wrong brand must NOT match;
- cigars must NOT match.

Quick item setup:

```mcfunction
/give @s create_tobacco:empty_cigarette_pack 16
/give @s create_tobacco:marlbore_red 10
/give @s minecraft:red_dye
```

Repeat with each brand after the first recipe is confirmed.

## 8. Brand pack extraction

For any crafted pack:
- tooltip is the pack's brand name + `10 / 10`;
- right click gives exactly one fresh matching cigarette;
- pack becomes `9 / 10`;
- spam click never duplicates;
- full inventory does not lose a cigarette or decrement count;
- `1 / 10 -> right click` replaces the pack with Empty Cigarette Pack;
- drop/pickup preserves count;
- death/inventory move preserves count.

Quick direct full-pack commands (default component is full):

```mcfunction
/give @s create_tobacco:marlbore_red_pack
/give @s create_tobacco:kend_pack
/give @s create_tobacco:bedromorkanal_pack
```

## 9. Cigarette Case / Портсигар

Craft recipe:

```text
Brass Sheet | Iron Nugget | Brass Sheet
Leather     | Chest       | Leather
Brass Sheet | Iron Nugget | Brass Sheet
```

Quick test:

```mcfunction
/give @s create_tobacco:cigarette_case
/give @s create_tobacco:kend
/give @s create_tobacco:minecristo_no_1
/give @s minecraft:diamond
```

Expected:
- right click opens 5x3 = 15 case slots;
- all nine cigarettes and both cigars can be inserted;
- vanilla diamond/food/tools cannot be inserted;
- partially smoked and lit smoking items can be inserted;
- their remaining-puff/lit Data Components survive closing/reopening;
- shift-click transfers allowed items into the case;
- source case cannot be moved out of its own main-hand hotbar slot while open;
- closing/reopening, drop/pickup and relog preserve contents;
- case cannot contain another case.

Design choice for this patch: a lit item stored INSIDE the closed case does not
progress passive burn. Treat the metal case as extinguishing/suppressing the ember.
If this feels wrong in play, flag it for Polish 02.

## 10. RNG regression

Normal smoking should roll a product effect only once per completed puff.
Holding the use button for 24 ticks must not make 24 rolls.

Practical sanity test:
- smoke several Creperfield/KEnd/Bedromorkanal items normally;
- compare observed proc frequency to the configured low probabilities;
- cancel puffs early repeatedly; cancelled puffs must never proc and never consume a puff.

Deterministic debug handlers remain the preferred functional test:

```mcfunction
/createtobacco effect trigger @s marlbore_red
/createtobacco effect trigger @s winstone_blue
/createtobacco effect trigger @s creperfield
/createtobacco effect trigger @s chunkman
/createtobacco effect trigger @s pigliament
/createtobacco effect trigger @s bedromorkanal
/createtobacco effect trigger @s stoneo_y_glowlieta
/createtobacco kend trigger @s random
```

## 11. Critical dedicated-server regression

At minimum test with one connected client:
- open/modify cigarette case;
- put a partial cigarette in it and relog;
- extract from a brand pack;
- trigger Creperfield;
- trigger KEnd teleport;
- trigger Withdrawal;
- ensure server log has no client-class linkage errors.

With two clients, verify their cases/packs/player SmokingData stay independent.

## Commit only after critical tests pass

```powershell
git status
git add .
git commit -m "V1 polish: survival tobacco packaging and smoking UX"
```
