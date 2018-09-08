# GMnitron

A Sentinel Comics RPG Discord bot that has game-specific die rolling commands as well as scene/initiative tracking to help run games via Discord.

You can invite the bot via: [This Invite Link](https://discordapp.com/oauth2/authorize?client_id=425385281890418710&scope=bot)

## Commands

#### Legend
---

Parentheses "()" represent required arguments.

Square brackets "[]" represent optional arguments.

#### Index

##### Die Commands
* [Min](#min)
* [Mid](#mid)
* [Max](#max)
* [Reaction](#reaction)
* [Overcome](#overcome)
* [Boost](#boost)
* [Hinder](#hinder)
* [Minion](#minion)
* [Lieutenant](#lieutenant)

##### Scene Commands
* [Establish](#establish)
* [Hand Off](#hand-off)
* [Hand Off To](#hand-off-to)
* [Advance](#advance)
* [Introduce](#introduce)
* [Ambush](#ambush)
* [Erase](#erase)
* [Recap](#recap)
* [Current](#current)

##### Useless/Dumb/"Fun" commands
* [Censor](#censor)
* [Died](#died)
* [Evil](#evil)
* [Plan](#plan)
* [Gloat](#gloat)
* [Insult](#insult)
* [Cult](#cult)
* [Date](#date)

##### Universes
* [Inverse](#inverse)
* [Ahora!](#ahora)
* [Animal-Verse](#animalverse)
* [Arataki](#arataki)
* [Iron Legacy](#ironlegacy)
* [Meat-Verse](#meatverse)
* [Quando?](#quando)
* [Xtremeverse](#xtremeverse)

#### Die & Dice Pool Rolling
---

##### Min
```
!min (die 1) (die 2) (die 3) [modifier]
Returns the min die (+ modifier) from the dice pool.
```

```
!min 8 12 12
Rolled 3 (d12: 3, d8: 4, d12: 7)
```

##### Mid
```
!mid (die 1) (die 2) (die 3) [modifier]
Returns the mid die (+ modifier) from the dice pool.
```

```
!mid d8 d12 d4 - 2
Rolled 5 = 7 - 2 (d4: 4, d8: 7, d12: 9)
```

##### Max
```
!max (die 1) (die 2) (die 3) [modifier]
Returns the max die (+ modifier) from the dice pool.
```

```
!max d8 d6 d12 +2
Rolled 6 = 4 + 2 (d12: 2, d8: 3, d6: 4)
```

##### Reaction
```
!reaction (die) [modifier]
Returns the die result (+ modifier).
```

```
!reaction d6 + 2
Rolled 7 = 5 + 2
```

##### Overcome
```
!overcome (min/mid/max) (die 1) (die 2) (die 3) [modifier]
Returns the result of an overcome action using the effect die from the dice pool (+ modifier).
```

```
!overcome mid d8 d6 d6
Rolled 5 (5, 5, 5).
Action succeeds, but with a minor twist.
```

##### Boost
```
!boost (min/mid/max) (die 1) (die 2) (die 3) [modifier]
Returns the positive mod size based on the result of the effect die from the dice pool (+ modifier).
```

```
!boost min d8 d8 d8 + 1
Rolled 4 = 3 + 1 (3, 5, 7).
Mod size: +2
```

##### Hinder
```
!hinder (min/mid/max) (die 1) (die 2) (die 3) [modifier]
Returns the negative mod size based on the result of the effect die from the dice pool (+ modifier).
```

```
!hinder mid d4 d6 d8
Rolled 1 (1, 1, 2).
Mod size: -1
```

##### Minion
```
!minion (die) [modifier] [save vs.]
Rolls minion die/dice (+ modifier), and optionally rolls against a save.
```

```
!minion d6 +1
Rolled 7 = 6 + 1

!minion d6 v5
Rolled 4 vs. 5 
The Minion is defeated!

!minion d6, d8 + 1, d12 v5
Rolled 6 vs. 5 
The Minion is reduced to a d4.

Rolled 8 = 7 + 1 vs. 5 
The Minion is reduced to a d6.

Rolled 10 vs. 5 
The Minion is reduced to a d10.
```

##### Lieutenant
```
!lt (die) [modifier] [save vs.]
Rolls a lieutenant die (+ modifier), and optionally rolls against a save.
```

```
!lt d6
Rolled 5

!lt d6 v5
Rolled 3 vs. 5 
The Lieutenant is reduced to a d4.
```

#### Scenes
---

##### Establish
```
!establish (# of green ticks) (# of yellow ticks) (# of red ticks) (actors)
Establishes the scene with the given number of ticks, and the 'actors' in the scene/round. The actors argument works with @mentions.
```

```
!establish 2 4 2 Scene Baddies Tachyon Wraith "Absolute Zero" Legacy
The Story so Far

It is currently a Green status. There are 2 Green boxes, 4 Yellow boxes, and 2 Red boxes left.

Absolute Zero hasn't acted this round.
Baddies hasn't acted this round.
Legacy hasn't acted this round.
Scene hasn't acted this round.
Tachyon hasn't acted this round.
Wraith hasn't acted this round.
```

or with @mentions

```
!establish 2 4 2 Scene Baddies Tachyon Wraith "Absolute Zero" @DarthDie
The Story so Far

It is currently a Green status. There are 2 Green boxes, 4 Yellow boxes, and 2 Red boxes left.

Absolute Zero hasn't acted this round.
Baddies hasn't acted this round.
@DarthDie hasn't acted this round.
Scene hasn't acted this round.
Tachyon hasn't acted this round.
Wraith hasn't acted this round.
```

##### Hand-off
```
!hand-off (from actor) (to actor)
Hand off the scene to the given actor.
```

```
!hand-off "Absolute Zero" "Baddies"
The Story so Far

It is currently a Green status. There are 2 Green boxes, 4 Yellow boxes, and 2 Red boxes left.

Baddies is the current actor.

Absolute Zero has acted this round.

Legacy hasn't acted this round.
Scene hasn't acted this round.
Tachyon hasn't acted this round.
Wraith hasn't acted this round.
```

##### Hand-off-to
```
!hand-off-to (to actor)
Hands off the scene from the current user to actor. Used by people who were added in !establish via a @mention.
```

```
DarthDie: !hand-off-to Wraith
The Story so Far

It is currently a Green status. There are 2 Green boxes, 4 Yellow boxes, and 2 Red boxes left.

Wraith is the current actor.

Absolute Zero has acted this round.
@DarthDie has acted this round.

Baddies hasn't acted this round.
Scene hasn't acted this round.
Tachyon hasn't acted this round.
Wraith hasn't acted this round.
```

##### Advance
```
!advance
Tick off the next box in the scene.
```

```
!advance
The Story so Far

It is currently a Green status. There are 1 Green boxes, 4 Yellow boxes, and 2 Red boxes left.

Absolute Zero has acted this round.

Baddies hasn't acted this round.
Legacy hasn't acted this round.
Scene hasn't acted this round.
Tachyon hasn't acted this round.
Wraith hasn't acted this round.
```

##### Introduce
```
!introduce
Adds an actor to the scene that can act next round.
```

```
!introduce Baron Blade
The Story so Far

It is currently a Green status. There are 2 Green boxes, 3 Yellow boxes, and 2 Red boxes left.

Absolute Zero is the current actor.

Baron Blade has acted this round.

Legacy hasn't acted this round.
The Wraith hasn't acted this round.
```

##### Ambush
```
!ambush
Adds an actor to the scene that acts this round.
```

```
!ambush Baron Blade
The Story so Far

It is currently a Green status. There are 2 Green boxes, 4 Yellow boxes, and 2 Red boxes left.

Absolute Zero is the current actor.

Legacy hasn't acted this round.
The Wraith hasn't acted this round.
Baron Blade hasn't acted this round.
```

##### Erase
```
!erase
Removes an actor from the scene/initiative.
```

```
!erase Baron Blade
The Story so Far

It is currently a Green status. There are 2 Green boxes, 3 Yellow boxes, and 2 Red boxes left.

Absolute Zero is the current actor.

Legacy hasn't acted this round.
The Wraith hasn't acted this round.
```

##### Recap
```
!recap
Gives a recap of the scene and initiative.
```

```
!recap
The Story so Far

It is currently a Green status. There are 2 Green boxes, 3 Yellow boxes, and 2 Red boxes left.

Absolute Zero is the current actor.

Legacy hasn't acted this round.
The Wraith hasn't acted this round.
```

##### Current
```
!recap
Displays the current actor.
```

```
!current
Inductor is the current actor.
```

#### Useless/Dumb/"Fun"
---

##### Censor
```
!censor (message to censor)
'Censors' a message in true Letters Page form. (Basically it replaces capital letters)
```

```
!censor my name is Inigio Montoya, you killed my father. prepare to die.
my name is smhnigio smhontoya, you killed my father. prepare to die.

!censor Batman vs Superman
smhatman vs smhuperman
```

##### Died
```
!died
Links to the "and then they died" gif. For reasons.
```

##### Evil
```
!evil
Links to a gif of Christopher laughing maniacally.
```

##### Plan
```
!plan
Links to a picture of Biomancer having everything go according to plan.
```

##### Gloat
```
!gloat
Supplies a random gloating SC quote.
```

##### Insult
```
!gloat
Supplies a random insulting SC quote.
```

##### Cult
```
!cult
Links to the Cult of Gloom plotting in the ruins of Atlantis
```

##### Date
```
!date
Tells you the current date.
```

#### Universes
---

##### Inverse
```
!cult 'character name'
Gives information about the character from the inverse universe
```

##### Ahora
```
!ahora | !ahora! 'character name'
Gives information about the character from the ¡Ahora! universe
```

##### Animalverse
```
!animal | !animal-verse | !animalverse 'character name'
Gives information about the character from the Animal-Verse
```

##### Arataki
```
!arataki 'character name'
Gives information about the character from Arataiki's universe
```

##### Iron Legacy
```
!iron-legacy | !ironlegacy 'character name'
Gives information about the character from The Iron Legacy universe
```

##### Meatverse
```
!meat-verse | !meatverse 'character name'
Gives information about the character from Meat-Verse universe
```

##### Quando
```
!quando | !quando? 'character name'
Gives information about the character from the ¿Quando? universe
```

##### Xtremeverse
```
!xtremeverse 'character name'
Gives information about the character from the Xtremeverse
```

## Development

To develop, install https://github.com/technomancy/leiningen

Then follow the steps to use the clj-discord library, https://github.com/yotsov/clj-discord#to-use-this-library

You'll need to set the GMNITRON_BOT_TOKEN & MONGODB_URI environment variables, which is the discord bot token and mongodb URI respectively.

To run the bot locally, use the `lein run` command.

## Todo/Upcoming Possible Features

* [Internal] Scene cleanup after 30 days
* [Internal] Some unit testing...

## Possible Features

* Mod tracking - Might be too much overhead
* Minion tracking - Sorta have this already with the minion/lt commands

## License

Copyright © 2018 Briar Bowser

The Sentinel Comics RPG is copyright [Greater than Games LLC.](https://greaterthangames.com/)