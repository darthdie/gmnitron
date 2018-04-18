# GMnitron

A Sentinel Comics RPG Discord bot that has game-specific die rolling commands as well as scene/initiative tracking to help run games via Discord.

You can invite the bot via: [This Invite Link](https://discordapp.com/oauth2/authorize?client_id=425385281890418710&scope=bot)

## Commands

#### Legend
---

Parentheses "()" represent required arguments.

Square brackets "[]" represent optional arguments.

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
Rolls a minion die (+ modifier), and optionally rolls against a save.
```

```
!minion d6 +1
Rolled 7 = 6 + 1

!minion d6 v5
Rolled 4 vs. 5 
The Minion is defeated!
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
Adds an actor to the scene.
```

```
!introduce "Baron Blade"
The Story so Far

It is currently a Green status. There are 2 Green boxes, 3 Yellow boxes, and 2 Red boxes left.

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
!erase "Baron Blade"
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