# DropCrates

An automatic events plugin that drops loot crates around the world on a fixed schedule and announces their location in chat.

## Initial Ask

I want to place a schematic at random coordinates around the world on a fixed schedule and broadcast the coordinates in chat when the schematic is placed. The schematic will be a simple build that contains multiple chests, which will need to be filled with random loot. Players will hopefully compete to get to the schematic first to get the loot.

Broadcasts should consist of:
- A broadcast 15 minutes before the schematic spawns notifying players a new event is coming
- A broadcast when the schematic spawns announcing the coordinates

AC:
- Configurable interval for event
- Configurable messages
- Configurable loot (see details below)
- Configurable schematic to use

TC:
- Expose JakesRTP as an API to use for generating the coordinates (this is a very efficient and powerful coordinate generation system we already use)
- Reuse pool/weight system from Expeditions for the loot generation
- On scheduled trigger, if 0 players are online, skip event
- On scheduled trigger, if previous location was not yet looted, skip event and re-broadcast location

## Configurable loot system

I want to have advanced control over the contents of the chests with multiple layers of randomness.

1. I want each chest to have a theme, and I want to choose that theme randomly. In the main config - I want to provide a list of "enabled loottables" along with a "weight" for each one that determines the likelihood of that loottable being used for any particular chest.

2. I want a separate directory where I can create an infinite number of loottable configs. Each loottable will be it's own file, and the name of the file will determine the name of the loottable to be used in the main config's "enabled loottables" list

3. Each loottable config should use the pool/weight system from our Expeditions plugin - which allows me to configure multiple pools to make up each loottable. Reach out if you need more details on how this works.

4. Add additional functionality to the pool/weight system to allow a random number of rolls for each pool, rather than a fixed number of rolls every time

5. Summary: Every time the event is triggered, for each chest within the schematic, choose a random loottable from the "enabled loottables" to use to fill that chest. This makes every event unique as the chests will always have different contents.

## References

- this plugin contains the loottable functionality I've been explaining - https://github.com/SemiVanilla-MC/Expeditions-2.0
- this plugin contains the ability to spawn in a schematic at coordinates provided - https://github.com/SemiVanilla-MC/JoinHandler ( I think the code is actually contained in a utils plugin called "CookieCore" that this plugin imports)
- this is our fork of JakesRTP that you may need to modify to add an API so you can use it's logic to generate coordinates - https://github.com/SemiVanilla-MC/JakesRTP
