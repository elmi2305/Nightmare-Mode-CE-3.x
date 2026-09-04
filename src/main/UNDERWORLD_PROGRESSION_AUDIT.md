# Underworld progression audit

This document is the migration plan and implementation contract for the Underworld chapter. The dimension is no longer designed as a Steel-equipped expedition. Entering it starts a separate, self-contained player progression with a dedicated inventory, equipment ladder, and survival economy.

The existing portal ritual, terrain, biomes, mobs, structures, sanity systems, and item/block registrations remain useful foundations. The current Steel-to-Titanium-to-Tungsten upgrade chain is explicitly superseded and its recipe registration is disabled while the new progression is designed.

## Core decision: a separate Underworld inventory

The Underworld has its own player inventory state. A player crossing an Underworld rift leaves their Overworld inventory and carried equipment safely in their Overworld state, then receives the inventory state belonging to that player in the Underworld. Returning restores the Overworld state and preserves the Underworld state for the next visit.

This is a progression boundary, not a set of dimension-specific nerfs. Steel armor, potions, infinite-ammunition bows, enchanted gear, and other Overworld safety systems do not need exceptions, resistance modifiers, or item-by-item restrictions because they do not cross the rift by default. Steel can therefore be a powerful late Underworld material without weakening its Overworld identity.

### Required boundary rules

- Maintain one persistent Underworld inventory state per player; it must never be shared by players or rifts.
- Swap the complete carried-loadout state atomically after a successful dimension transfer. The precise implementation scope must include the main inventory, armor, held/cursor stack, and every other player-carried equipment surface exposed by the installed BTW version.
- Clear active potion effects on entering the Underworld and restore the saved effect state on return. Permanent or long-duration effects must not silently bypass the progression boundary.
- Underworld death, drops, respawns, and recovery operate only on the Underworld state. The player must not be able to lose, duplicate, or retrieve their Overworld inventory through Underworld death handling.
- Save and restore correctly through logout, server restart, forced dimension transfer, and portal failure. A failed transition must leave the player in exactly one valid state.
- Do not allow ordinary inventory transfer between dimensions. Any future cross-dimension rewards must be explicit extraction, ritual, or conversion mechanics with a defined cost and output.

### Open policy decisions

- The present Portal Core still has its post-Wither/Steel altar access gate. Whether that gate remains, moves earlier, or becomes an alternate campaign-start route is separate from the inventory decision and must be chosen before final balance work.
- Decide which non-item player state is shared versus dimension-local: XP, hunger/saturation, health, respawn point, achievements, and any BTW-specific player capabilities. The default should favor clear separation whenever shared state could trivialize survival.
- Define how Underworld rewards affect the Overworld. The default is no direct item export; late-game export must be deliberately designed rather than inherited from inventory access.

## Target progression model

The Underworld should echo the cadence of early Minecraft/BTW progression without duplicating its content. Players collect native resources, process them through appropriate stations, obtain ingots or equivalent crafting materials, and craft tools and armor directly in a crafting-table-style recipe progression.

Equipment is not upgraded by consuming a prior tool or armor piece. Each tier is a new crafted object made from the resources earned at that tier. This keeps resource acquisition, processing, and crafting visible to the player; it avoids the fragile and uninteresting Steel-base upgrade requirement.

### Intended route

1. **Arrive unprepared and establish survival.** The starting biome supplies the first shelter, food/light, and primitive material loop without imported equipment.
2. **Craft the first local tools.** A native early tier opens the first terrain and resource gates. The existing Rift Workbench may serve this role if its recipe and identity fit the final loop.
3. **Earn the first metal tier.** Mine a locally gated ore, process it through the appropriate native station, and craft the first proper tools and armor from its ingots.
4. **Expand through biome mechanics.** Each biome contributes a distinct survival pressure, material, or processing requirement instead of merely a stronger ore.
5. **Open the deep layer and later metals.** Tool gates, strata, dungeons, and hostile-biome resources form the middle and late ladder. Titanium and Tungsten are candidates for positions in that ladder, not fixed starting assumptions.
6. **Earn Steel as a late Underworld metal.** Steel is mined and processed in the Underworld as a meaningful high-tier payoff; it is not imported as entry equipment or used as an upgrade base.
7. **Complete the final Underworld branch.** The late equipment, dungeon, organic/sanity systems, and End-component route converge in a deliberate endgame reward loop.

## Crafting and station rules

- Ore or raw material -> processing station -> ingot/material -> crafting-table-style tool and armor recipe is the default equipment pattern.
- Stations should perform processing, transformation, environmental interaction, or special recipes. They should not exist only to consume a previous equipment tier and output its replacement.
- The Underforge block, GUI, tile entity, packet/container wiring, and EMI category are retained as implementation assets. Its eventual identity is intentionally undecided: it may become a repurposed processing station or a genuinely distinct custom recipe station.
- Until that role is defined, the Underforge block recipe and all current Underforge recipe defaults are disabled. The old tool and armor upgrade registrations are also separately disabled so they cannot reappear if basic Underforge recipes are restored during prototyping.

## Current implementation status

| Area | Status | Migration direction |
|------|--------|---------------------|
| Portal ritual and linked rifts | implemented | retain; revisit access gate separately |
| Separate player inventory state | implemented for main/armor/Ender inventories and potion effects | verify with portal, death, restart, and multiplayer playtests |
| Rift Workbench | implemented | evaluate as native early crafting table |
| Soul lighting and biome materials | implemented | retain and rebalance around local entry progression |
| Titanium/Tungsten ores, gear, and harvest gates | implemented for Steel entry | retier after the first native tools and metals are designed |
| Underforge block and interface | implemented | retain, repurpose later |
| Underforge recipes and equipment upgrades | disabled | replace with direct material-processing and crafting recipes |
| Big Mushroom and End-component route | implemented around old tiers | re-gate after the final material ladder is chosen |

## Implementation plan

1. **Inventory-state verification.** The player NBT now persists distinct Overworld and Underworld snapshots for main/armor/Ender inventories, selected hotbar slot, and active potion effects. Rift travel swaps them through the central dimension-transfer path; Underworld death clears only the Underworld snapshot. Exercise portal travel, death, logout, restart, and multiplayer isolation before adding progression content.
2. **Native bootstrap.** Define the Underworld's equivalent of early resource gathering, shelter, light, food, and first tools. Ensure a new Underworld state can make meaningful progress from its rift landing without Steel or an imported station.
3. **Material ladder.** Choose the exact tier order, ore locations, required tools, processing steps, armor values, and biome dependencies. Place Steel late in this ladder; retier Titanium, Tungsten, Underrock, and dungeon access only after that order is fixed.
4. **Direct recipes.** Add processed-material recipes for every tool and armor set. Keep the patterns legible and crafting-table-shaped unless a material has a strong reason to use a specialized station.
5. **Station identity.** Decide whether the Underforge is processing, multi-input ritual crafting, or another bespoke loop. Add only recipes that support that identity, then restore its recipe registration and EMI presentation.
6. **Rewards and final route.** Reconnect sanity upgrades, dungeons, lenses/Eyes, achievements, and any approved cross-dimension extraction to the completed ladder.
7. **Balance and migration testing.** Test fresh-player entry, portal transition recovery, all death paths, relog/restart persistence, multiplayer behavior, and each progression gate in a fresh Underworld region. World-generation balance must be inspected in new chunks.

## Retained balance intent

- Flower Fields remain a high-pressure biome, and the Big Mushroom remains a late dungeon unless the material-ladder audit gives a strong reason to move it.
- Blightlands remains the intended safe rift destination and early material hub, but its exact early-resource role will be redesigned for an unarmed player.
- Verdant and Mycelial Hearts remain promising ordered organic sanity milestones; their ingredients and gates must follow the new material ladder.
- Feature classification, structure registration, full-height caves/strata, fog rendering, portal localization, and `devMode` diagnostics are independent technical decisions and remain valid.

## Disabled legacy design

- Do not balance Underworld threats around players arriving in Steel.
- Do not add Underworld-only armor resistance modifiers, potion exceptions, bow restrictions, or similar patches to compensate for imported Overworld power.
- Do not restore Steel-to-Titanium, Titanium-to-Tungsten, or armor upgrade recipes. Future equipment uses newly crafted tools and armor made from native processed materials.
- Do not treat the currently disabled Underforge recipes as a final specification. They are retained only as code/assets that may inform a later repurpose.
