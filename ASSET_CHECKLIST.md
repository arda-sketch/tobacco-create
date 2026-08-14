# Create: Tobacco — V1 Asset Checklist

This file is the art pass checklist after V1 Polish 01. Gameplay may use temporary
Minecraft/Create textures until the matching custom asset is drawn.

## Art direction

- Target look: roughly 70% vanilla Minecraft, 30% Create.
- Keep silhouettes readable at native resolution; avoid anti-aliasing.
- Organic tobacco assets: muted greens, ochres, tobacco browns.
- Industrial/packaging assets: cream paper, cardboard, brass/copper accents.
- Prefer a small palette per icon (usually 4–7 colors).
- Item icons should normally be 16x16 PNG with transparent background.
- Crop/wild plant textures should be 16x16 PNG with transparent background.
- Mob-effect icons should be 18x18 PNG.

## A. REQUIRED — cultivated crop stages (24)

These are currently required by the crop block models and should be drawn first.
Each variety needs eight visually distinct stages, stage0 through stage7.

### Virginia

- `assets/create_tobacco/textures/block/virginia_tobacco_stage0.png`
- `.../virginia_tobacco_stage1.png`
- `.../virginia_tobacco_stage2.png`
- `.../virginia_tobacco_stage3.png`
- `.../virginia_tobacco_stage4.png`
- `.../virginia_tobacco_stage5.png`
- `.../virginia_tobacco_stage6.png`
- `.../virginia_tobacco_stage7.png`

Visual idea: lighter/brighter broad leaves, relatively upright mature silhouette.

### Burley

- `.../burley_tobacco_stage0.png` through `.../burley_tobacco_stage7.png`

Visual idea: darker, broader/heavier leaves; mature plant should be visibly distinct
from Virginia even without reading the item name.

### Havana

- `.../havana_tobacco_stage0.png` through `.../havana_tobacco_stage7.png`

Visual idea: rich green, slightly narrower/longer leaves, tropical feel. The final
stage can be the tallest-looking of the three without changing the actual hitbox.

### Suggested stage progression

- 0: tiny two-leaf sprout
- 1: 3–4 leaves
- 2: low rosette
- 3: stem becomes visible
- 4: mid-sized plant
- 5: broad leaf mass
- 6: nearly mature, fuller top
- 7: mature harvest silhouette

Do not simply recolor one identical image for all three varieties; keep a family
resemblance but vary leaf shape and silhouette.

## B. REQUIRED — wild tobacco plants (6 textures / 3 plants)

Each wild plant is two blocks tall and therefore needs a lower and upper texture:

- `assets/create_tobacco/textures/block/wild_virginia_tobacco_bottom.png`
- `assets/create_tobacco/textures/block/wild_virginia_tobacco_top.png`
- `assets/create_tobacco/textures/block/wild_burley_tobacco_bottom.png`
- `assets/create_tobacco/textures/block/wild_burley_tobacco_top.png`
- `assets/create_tobacco/textures/block/wild_havana_tobacco_bottom.png`
- `assets/create_tobacco/textures/block/wild_havana_tobacco_top.png`

Recommended: make each wild plant recognizably related to cultivated stage7, but
not identical. The lower half should carry the broad basal leaves and stem; the
upper half should continue the stem with smaller upper leaves/flowering top. Wild
plants should look irregular and naturally tall, while stage7 remains the tidy,
compact farm version.

## C. SEEDS (3) — existing, redraw if desired

- `textures/item/virginia_seeds.png`
- `textures/item/burley_seeds.png`
- `textures/item/havana_seeds.png`

Keep subtle color differences rather than three dramatically different seed shapes.

## D. LEAVES (6) — existing, good candidates for a coherent redraw

Fresh:
- `fresh_virginia_leaf.png`
- `fresh_burley_leaf.png`
- `fresh_havana_leaf.png`

Cured:
- `cured_virginia_leaf.png`
- `cured_burley_leaf.png`
- `cured_havana_leaf.png`

Fresh should read green at a glance; cured should read golden/brown at a glance.
Try to preserve each variety's leaf silhouette between fresh and cured versions.

## E. CUT TOBACCO (3) — existing

- `cut_virginia_tobacco.png`
- `cut_burley_tobacco.png`
- `cut_havana_tobacco.png`

Make these look like small shredded piles rather than miniature whole leaves.

## F. CIGARETTE COMPONENTS (2) — existing

- `cigarette_paper.png`
- `cigarette_filter.png`

Paper: thin cream strip / small stack of strips.
Filter: short tan cylinder or compact filter bundle.

## G. PREPARED BLENDS (9)

Existing custom textures:
- `marlbore_red_blend.png`
- `winstone_blue_blend.png`
- `creperfield_blend.png`
- `craftmel_blend.png`
- `chunkman_blend.png`
- `kend_blend.png`
- `pigliament_blend.png`
- `rothmines_blend.png`

Bedromorkanal currently falls back to vanilla dried kelp and needs its own art:
- `bedromorkanal_blend.png`

Blend rule: the tobacco pile should remain dominant and the additive should be a
small readable accent (red spark, blue fleck, cocoa brown, chorus purple, etc.).

## H. FINISHED CIGARETTES (9) — recommended high-priority redraw

At present most finished cigarettes visually reuse their blend. Give every finished
product a real cigarette silhouette:

- `marlbore_red.png`
- `winstone_blue.png`
- `creperfield.png`
- `craftmel.png`
- `chunkman.png`
- `kend.png`
- `pigliament.png`
- `rothmines.png`
- `bedromorkanal.png`

Keep the basic cigarette shape consistent, with small brand differences in filter,
paper stripe, tip color, or decorative pixels. Do not make nine unrelated shapes.

### Lit-state art

The current V1 code stores lit state in a Data Component but does not yet swap the
item model by component. If a later visual patch adds a component-aware model,
prepare either:

- nine `_lit` variants, or
- one reusable ember/ash overlay approach.

Do not draw all nine lit variants yet unless you want to; this can be a later polish.

## I. CIGAR PRODUCTION ITEMS (4) — recommended redraw

Currently several reuse leaf/cut-tobacco art:

- `fermented_havana_tobacco_bundle.png`
  - compact pressed brown bundle, tied/stacked leaf look
- `cigar_filler.png`
  - coarser long-cut Havana filler
- `cigar_wrapper.png`
  - flattened single cured wrapper leaf
- `glowstone_cigar_filler.png`
  - cigar filler with subtle warm gold Glowstone flecks

## J. FINISHED CIGARS (2) — high priority

- `minecristo_no_1.png`
- `stoneo_y_glowlieta.png`

Minecristo: classic dark-brown cigar, premium band/detail.
Stoneo y Glowlieta: same cigar family, but warmer/golden band or faint glow accent.
Do not simply reuse a cured leaf texture.

## K. TRANSITIONAL CREATE ITEMS (2) — low priority

These normally exist only during Sequenced Assembly and need not be beautiful:

- `incomplete_cigarette.png`
- `incomplete_cigar.png`

A simple unfinished paper/tobacco rod and unfinished cigar are enough. They should
not compete visually with finished products.

## L. PACKAGING (10) — high priority

Shared empty pack:
- `empty_cigarette_pack.png`

Brand packs:
- `marlbore_red_pack.png` — red identity
- `winstone_blue_pack.png` — blue identity
- `creperfield_pack.png` — lime/creeper identity
- `craftmel_pack.png` — orange/sandy identity
- `chunkman_pack.png` — cocoa/brown identity
- `kend_pack.png` — purple/End identity
- `pigliament_pack.png` — gold/yellow identity
- `rothmines_pack.png` — black/coal identity
- `bedromorkanal_pack.png` — light-blue/sea identity

All nine should share the same physical pack silhouette/template. Change label,
stripe, logo geometry, and palette rather than the entire shape. Avoid real-world
logos; parody names are enough.

## M. CIGARETTE CASE / PORTSIGAR (1) — high priority

- `cigarette_case.png`

Suggested look: compact brass/iron cigarette case, dark leather inset, small hinge or
clasp. It should read as a premium personal container rather than a chest/backpack.

The current GUI is programmatically drawn, so no GUI PNG is required for V1.
Optional later GUI art can use a 176x168-style inventory panel if desired.

## N. MOB EFFECT ICONS (2) — required for polished HUD/inventory

18x18 PNG:

- `textures/mob_effect/nicotine_rush.png`
  - subtle wind/leaf/smoke motif; should not resemble vanilla Speed too closely
- `textures/mob_effect/withdrawal.png`
  - muted/gray broken smoke or shaking-leaf motif

## O. SMOKE PARTICLE SET (recommended polish, 8 frames)

The current particle deliberately reuses vanilla `generic_0..7`, so it works without
custom art. For a recognizable tobacco-smoke look later, draw:

- `textures/particle/tobacco_smoke_0.png`
- `.../tobacco_smoke_1.png`
- ...
- `.../tobacco_smoke_7.png`

Recommended 8x8 or 16x16, soft gray shapes with transparent edges. The animation
should expand and dissipate rather than look like eight unrelated clouds.

## P. SOUNDS (not drawings, but part of the same polish pass)

Current V1 avoids the drinking sound and uses existing game sounds. For a later audio
pass prepare original/appropriately licensed `.ogg` files:

- `sounds/smoking/puff.ogg`
- `sounds/smoking/exhale.ogg` (optional; can be combined with puff)
- `sounds/smoking/cough_1.ogg`
- `sounds/smoking/cough_2.ogg` (optional variation)
- `sounds/pack/open.ogg` or `pack_take.ogg`
- `sounds/case/open.ogg`
- `sounds/case/close.ogg`

Do not ship audio copied from unrelated games/videos.

## Q. Deferred release/metadata art

Not required for the current private playtest:

- mod logo
- README screenshots
- CurseForge/Modrinth banner
- homepage art

## Recommended drawing order

1. 24 crop stages + 6 wild half-textures (3 two-block plants) (fixes the largest visible missing-art problem).
2. 9 finished cigarettes + 2 finished cigars.
3. Empty pack + 9 brand packs + cigarette case.
4. Cigar production intermediates.
5. Effect icons.
6. Redraw seeds/leaves/cut tobacco/blends only where the current style no longer fits.
7. Custom smoke frames and audio last.
