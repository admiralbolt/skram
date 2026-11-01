# I'm sure there's a datagen way of doing this, but I'm lazy and this is fast.
SKILLS = ["acrobatics", "athletics", "botany", "brewing", "cooking", "demolition", "digging", "farming", "fishing", "health", "husbandry", "melee", "mercantile", "mining", "ranged", "sailing", "smithing", "stealth", "woodcutting"]

COMMAND_TEMPLATE = "skram setskill @s %s xpmult %s"

for skill in SKILLS:
  for amount in [1, 2, 3]:
    with open(f"{skill}_xp_mult_up{amount}.mcfunction", "w") as wh:
      wh.write(COMMAND_TEMPLATE % (skill, (amount / 100)))

