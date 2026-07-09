# CONTRIBUTORS.md

Thanks for taking an interest in contributing to this project.

This file has two main sections: a guide to the codebase, and a guide to Nightmare Mode's vision. This is meant to explain both clearly.

## Codebase Guide

The NM repository has a lot of utilities within it. These are usually found within the /util/ directory.

### Utility classes

*NMUtils.java
The main utility class. Contains various methods that are queried from around the codebase, from checking static booleans, to events, and everything inbetween. It contains:
- Lists of various items (item pools)
- Methods for world state and world events (such as blood moons)
- Static methods called from multiple places (such as the spawning logic for events, config behavior, world name setup, scroll metadata, villager orb metadata)
- World time reading (getting the day count, finding out when the next blood moon)
- Methods for playing custom music and sounds
and more.

*NMInitializer.java
The main intializer class. Initializes:
- Recipes for all crafting stations
- Trades for all villager types
- Beacon effects for custom beacons
- Mob spawning for all mobs
- Achievement edits (via api)
- Trade edits (via TradeTweaks.java)
- Config file (via NMConfUtils.java)
- Developer tools
- Miscellaneous

* NMBlocks.java
Initializes and contains all NM blocks

* NMItems.java
Initializes and contains all NM items

* NMPostItems.java
Initializes and contains items that need to be initialized at the very end, such as items that grant achievements.
This is simply a matter of initialization order. 99% of items are initialized in NMItems.java

* NMTags.java
Initializes EMI tags for NM's items. Used to make certain recipes accept multiple items without being classified as entirely different recipes.

* SteelLockerNet.java
Registers the steel locker

*NMEntityMapper.java
Manages all Entity mappings. for entities to function properly, they must have a mapping. this is also where their spawn eggs are initialized. for more info, see the entity creation setup

*NightmareKeyBindings.java
Used for custom keybind management.

*NMFields.java
a large public static library of fields used around the codebase, all in one place and freely tweakable (although tweaking them is not recommended). The main point of this class is to contain numeric fields used around the codebase to avoid hardcoding them

*NMConfUtils.java
Utility class for NM's config modes. These are difficult challenge modes. Contains various methods to do with checking configs, saving config data to the world, managing config completion, creating and modifying the config file along with data encryption, individual config enums

*NMSanityUtils.java
Manages the sanity mechanic. Contains methods for calculating sanity drain. Used exclusively for the Underworld.

*NightmareModeAddon.java
Contains registries for custom sounds / music, along with packet handling. An extension of `NightmareMode.java` (the central mod class)

Utility directories:
/underworld/ - used for various underworld utilities, such as the fear mechanic, summon ritual, adding objects to the skybox and postprocessing (mostly unused). Anything related to the underworld goes here, so it's all mostly unused

/tradetweaks/ - API classes for convenient trade initialization. called in NMInitializer

/tpa/ - TPA related classes for multiplayer

/network/ - currently only used for the steel locker network initialization

/interfaces/ - various interfaces used exclusively for interface injection around the code. see the chapter on interface injection for more info

/commands/ - contains various custom in-game commands, all of which are initialized in `initialize()` of `NightmareMode.java` 

/elements/ - Misc initializers:
	- NMEvents.java: contains initialization and utility methods for NM's custom events (such as the slime rain) - not to be confused with blood moons and eclipses
	- NMDifficultyParam: contains difficulty parameters. currently deprecated and mostly unused
	- NMDamageSource: contains damage sources for NM. 
	- NMBeaconEffects: contains initialization for custom beacon effects made with NM blocks
	- LogSettings: util class for logging player griefing activities in multiplayer
	- BloodSawCraftingManager: unused crafting manager class for the blood saw


Key initialization point:
`initialize()` of `NightmareMode.java` is where NM initializes all of its elements

### Mixins

For a guide on the mixin library, please see the official fabric documentation:
https://wiki.fabricmc.net/tutorial:mixin_introduction
https://wiki.fabricmc.net/tutorial:mixin_examples
https://wiki.fabricmc.net/tutorial:mixin_tips

For a practical list of examples, see MIXINS.md

Mixins are the main tool to work with. large modifications require extensive mixin knowledge. Mixins must be placed within the /mixin/ directory, preferably in the correct subfolder. They can easily be added to `nightmaremode.mixins.json` by using the MC Dev Plugin. 
If a mixin for a class already exists, use that one - do not create multiple mixins for the same class.
The naming convention is simple: [Base Class]Mixin.java. if the base class has a very long or unpleasant name, it can be shortened, but this is sparingly done (example: BeaconEffectLocationListMixni -> BELocationListMixin, placed in its own directory for 0 ambiguity). in 99.9% of cases, stick to the defined naming convention.
Methods inside mixins can be named whatever. ideally they should describe what the particular mixin is for / what it does in a few words. 
comments are not strictly required, but are recommended. this includes adding comments to classes / mixins written by elmi (or any other contributor)

Incredibly useful mixins:
WorldMixin.java
WorldServerMixin.java
EntityPlayerMixin.java
BTWRenderMapperMixin.java
EntityRendererMixin.java - used for all sorts of rendering shenanigans
various mob / entity mixins, especially mixins for the EntityMob class tree, which goes as follows:
EntityMob -> EntityCreature -> EntityLiving -> EntityLivingBase -> Entity 

### Localization

NM is localized to a number of different languages. Localization is the final step and happens right before the new release is published. during development, only localize the english file (EN_US.lang, located in /resources/lang/), and only commit it when the release is ready to be published, so other lang files can be localized based on it. Optionally translate them yourself to reduce the burden on elmi.

### Entities

Adding an Entity to NM requires:
- The entity class itself, containing its behavior and attributes
- the entity must be mapped in `NMEntityMapper.java`
- the entity must have a valid renderer. This can be an existing renderer such as the zombie - in which case the rendering is managed by the mixin. or it can be a unique renderer class, which must be located in /rendering/entities. Regardless of the approoach, the renderer must be mapped in `BTWRenderMapperMixin.java`
- if entity has a custom packet, that packet must be registered in `NightmareModeAddon.addPacketManagementForCustomEntities()` and the ID must be included in `NMFields.java`
- the entity's name should be localized
- the entity will not spawn unless it is added to the spawn table `NMInitializer.initMobSpawning()`

Entities classes are placed in the /entity/ directory. Localization is done in the localization files within /resources/lang. Packet ints are saved in NMFields.java, and an entity packet is built in NightmareModeAddon.java, the same way all of the other entities do it (unless custom handling is necessary, in which case it's a bit more nuanced than a copy-and-paste job)
Adding variants is a bit lackluster. Skeleton variants are managed via the skeleton type in EntitySkeletonMixin. Zombie variants are created by initializing classes which extend EntityZombie, and creeper variants are created using flexible EntityCreeperVariant class with API. 

Note that modifying EntityCreeperMixin requires that feature to be mirrored in EntityCreeperVariant, if it is intended for variants to also exhibit the behavior in question.

### Blocks

Blocks are registered entirely in NMBlocks.java, and immediately added to the itemlist right after initialization. Be mindful of the block templates located in /block/blocks/templates/, they are very useful. There are templates for NM Containers, Ground layer blocks (like snow), custom logs, multitextured blocks, multitextured blocks with metadata support, and more.

Regardless of the nature of the block, it is imperative that the block overrides `getModId()` (or extends a template class which overrides it automatically)

### Items

Items are registered and added in NMItems.java and NMPostItems.java. The latter is used for items that need to be initialized after everything else is done initializing (namely the achievements). 99% of the items go in NMItems.java

Be mindful of the template items in /item/items/templates/, they can be very useful. Additionally, if a block needs custom handling for its item form (such as obsidian, which has 2 items/blocks with metadata), it needs a custom NMItemBlock instance.

### Achievements

Editing existing achievements is done through the NMInitializer API. See `NMInitializer.manipulateAchievements()`.

Achievements and achievement helpers may be found in the /achievements/ directory.

NMAchievements.java is for custom achievements, which are built based on the pattern. They use Achievement Event classes as a means of grouping multiple achievements together based on their similarity. If the BTW Source does not have an adequate Achievement Event that can properly handle the condition your achievement desires, a custom achievement event can be added to `NMAchievementEvents.java`. Note that when an achievement event is added, it must be queried server-side, and the information it requires must be provided. 
Example:                     
`AchievementEventDispatcher.triggerEvent(NMAchievementEvents.TimeItemEvent.class, player, new NMAchievementEvents.TimeItemEvent.Context(player, worldTime));`

### Miscellaneous

AITasks are created in the /AItasks/ directory. GUI elements belong in /nmgui/

### Access Widener and Accessor Interfaces

When a method is private and needs to be called, it is preferable to implement an Accessor interface. These interfaces are usually denoted with an -Accessor or -Invoker or -Access suffix in their name. They are located in /mixin/interfaces/.

They are mixin interfaces that expose methods or fields. Not to be confused with interface injection - where we add our own methods and fields via interfaces. Accessor interfaces allow us to read private or protected fields, Invoker interfaces allow us to invoke private or protected methods. They are used by casting the object with the interface and then calling the desired method. 
Example: 
((EntityZombieAccessor)zombie).getZombieField();
Accessor and invoker interfaces do NOT need to be implemented via Java's `implements` keyword. 

The access widener is a way to access private classes or private constructors. It should only be used if accessor interfaces cannot implement the desired behavior.


### Interface Injection

Suppose we want to add our own fields to a class such as the Zombie. We cannot add fields to the Zombie Mixin, because mixins cannot be instantiated as objects, and thus we could not ever call any fields we were attempting to store in the mixin class.

This is solved by using interface injection. Interfaces used for interface injection are in /util/interfaces/ and are commonly suffixed with -Ext, denoting the fact they are an extension of their base class, allowing us to store additional fields.

The mixin must implement the extension interface and implement all of its methods in a valid way. Once that is done, the interface's methods can be called on objects the mixin is targetting.

Example: EntityZombieMixin implements EntityZombieExt which exposes a method `int getZombieTimer();`. EntityZombieMixin implements this method with a field called `zombieTimer`. When we want to query this value, we call `((EntityZombieExt)myZombie).getZombieTimer();`

Interface injection is incredibly useful.

### Packets - Client and Server communication

Packets are initialized in `NightmareMode.java` and given specific channels. Keep the channel name small. Example: "nm|BMEC", "nm|Dir", "nm|stat" etc.

Be mindful of whether your packet is intended to be client -> server, or server-> client, or both. Follow the examples for packet creation, since there are plenty. Interface injection may need to be used to expose fields for packets to send / receive. 

For more packet examples not included in `NightmareMode.java`, see `MixinNetClientHandler.java` for vehicle packets and `BTWContainersMixin.java` for containers.

# What is Nightmare Mode, from a design perspective?

The goal of Nightmare Mode is to make a very difficult mod that is still fun to play. A major priority is avoiding mechanics that create stall, while favoring mechanics that reward player skill, quick thinking, and strategy.

Most Nightmare Mode features exist for one of these reasons:

* They fix a problem. When addressing a player complaint, the issue should be clearly understood and directly solved in a way that makes logical sense. A feature should feel expectable. The first time a player encounters it, they should understand how it works.
* They reduce tedium or repetition. Repetitive actions take away from the game. Avoid fetch quests, villager trading, waiting around, and similar busywork.
* They make the game harder. It helps if you have actually played the mod. Some areas are weaker in difficulty, such as the gaps before major spikes, like late early game before hardmode, or late hardmode before the Wither.

Nightmare Mode is intended to be one of the hardest Minecraft modded experiences. It should earn that reputation through mechanical difficulty, strategy, and player decision-making, not through luck-based gimmicks. Any luck-based feature should be heavily scrutinized and thoroughly tested before it is added.

Difficulty should always be counterable through correct play.

## Things to avoid

### Feature bloat

Do not add insignificant features that make the mod more complicated without improving it. That often forces players to rely on a wiki just to understand basic mechanics.

This also includes oversaturating an area that already has too much content.

Examples:

* adding new mobs to the early game
* mobs riding other mobs
* arbitrary random bonuses, such as "bloodmoon tools attract mobs"

### Balance concerns

Some features are more of a liability than a solution. Players often suggest more armor or more weapons for the early game, but that kind of change is usually a bad idea. The implementation may be simple, but balancing it can take days of work.

These features create technical and design overhead that is usually not worth it.

Examples:

* throwable rocks
* spears
* new ores with full armor and weapon sets

### Unintuitive mechanics

This includes anything the player would not be able to understand from normal play or from observing it in action.

Examples:

* changing recipes so they differ from standard BTW
* recipes that do not make sense, such as straw turning into potato
* mob abilities that are unintuitive, such as a mob having a 1 percent chance to do something random
* broken effects, such as regular spiders having a 1 percent chance to inflict food poisoning

Anything that gives a mob a rare chance to do something absurd will probably be rejected. For example, witches striking the player with lightning would not be a good fit.

Complicated solutions should usually be reserved for patching holes in the design. That is not ideal, but some existing features, such as grindable hemp seeds, are examples of unintuitive mechanics that still improve gameplay.

### Known suggestions that are unlikely to be added

* bleeding
* throwable rocks
* additional combat weapons, such as spears or guns
* anything that makes Nightmare Mode easier
* phantoms
* sleeping penalties or buffs for sleeping
* additional potion effects
* post 1.12 vanilla Minecraft features, such as bees, frogs, or piglins
* changes to world generation

## Questions to ask before adding a feature

* Will players encounter this feature? How often?
* Does it have a large impact on gameplay? Is it too intrusive?
* Does it fit Nightmare Mode's purpose as a difficulty addon?
* Does it make sense? Can the player logically justify why it exists?

For example, animals running from predators makes sense, so they should run from the player if the player is making too much noise. On the other hand, why would a witch gain 150% range from sitting on a slime? Why should the player expect that?

## Emergent gameplay versus difficulty

Finding the right balance for a feature can be difficult. Prioritize fun over difficulty when you have to choose, but do not add one without the other.

## Testing considerations

### Tests done immediately

* The client must boot successfully.
* The feature must produce the intended effect without breaking player immersion.
* The feature must not conflict with systems already implemented in Nightmare Mode.

### Tests done before a new release

* Localization must be completed for all applicable languages.
* The multiplayer server must boot correctly, and the feature must work properly in multiplayer.

### Optional tests before a new release

* The patch must not break worlds made on an earlier release.
* The patch must not break instances that were created with an earlier release.

