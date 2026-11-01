# This is a comment inside my custom function
# It runs when the advancement is completed

# Give the player a special item
give @s minecraft:netherite_scrap 1

# Play a celebratory sound effect at the player's position
playsound minecraft:ui.toast.challenge_complete master @s ~ ~ ~ 1.0 1.0

# Broadcast a message using the player's name
tellraw @a [{"text":"[Mod] ","color":"dark_green"},{"selector":"@s","color":"green"},{"text":" just ran a custom function!"}]