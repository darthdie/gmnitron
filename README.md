# GMnitron

A SCRPG Discord bot.

Provides dice rolling, scene tracking, and initiative tracking.

You can invite the bot via: [This Invite Link](https://discordapp.com/oauth2/authorize?client_id=425385281890418710&scope=bot)

## Commands

#### Dice Rolling
```
!min (die 1) (die 2) (die 3) [modifier]
!mid (die 1) (die 2) (die 3) [modifier]
!max (die 1) (die 2) (die 3) [modifier]

!reaction (die) [modifier]

!overcome (min/mid/max) (die 1) (die 2) (die 3) [modifier]

!boost (min/mid/max) (die 1) (die 2) (die 3) [modifier]
!hinder (min/mid/max) (die 1) (die 2) (die 3) [modifier]

!minion (die) [modifier] [save vs.]
!lt (die) [modifier] [save vs.]
```

#### Examples

```
!min 8 12 12
Rolled 3 (d12: 3, d8: 4, d12: 7)

!max d8 d6 d12 +2
Rolled 10 = 8 + 2 (d8: 3, d6: 5, d12: 8)

!reaction d6 + 2
Rolled 7 = 5 + 2

!minion d6 +1
Rolled 7 = 6 + 1

!minion d6 v5
Rolled 4 vs. 5 
The Minion is defeated!

!lt d6 v5
Rolled 3 vs. 5 
The Lieutenant is reduced to a d4.

!overcome mid d8 d6 d6
Rolled 5 (5, 5, 5).
Action succeeds, but with a minor twist.

!boost min d8 d8 d8 + 1
Rolled 4 = 3 + 1 (3, 5, 7).
Mod size: +2

!hinder mid d4 d6 d8
Rolled 1 (1, 1, 2).
Mod size: -1
```
---
#### Scenes

```
!establish (# of green ticks) (# of yellow ticks) (# of red ticks) (actors)
Establishes the scene with the given number of ticks, and the 'actors' in the scene/round. Also works with mentions.

!hand-off (from actor) (to actor)
Hand off the scene to the given actor.

!hand-off-to (to actor)
Hands off the scene from the current user to actor. Used

!advance
Tick off the next box in the scene.

!recap
Give a recap of the scene and initiative.

!introduce
Adds an actor to the scene/initiative.

!erase
Removes an actor from the scene/initiative.
```

#### Examples

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

or

!establish 2 4 2 Scene Baddies Tachyon Wraith "Absolute Zero" @DarthDie
The Story so Far

It is currently a Green status. There are 2 Green boxes, 4 Yellow boxes, and 2 Red boxes left.

Absolute Zero hasn't acted this round.
Baddies hasn't acted this round.
@DarthDie hasn't acted this round.
Scene hasn't acted this round.
Tachyon hasn't acted this round.
Wraith hasn't acted this round.


!hand-off "Absolute Zero"
The Story so Far

It is currently a Green status. There are 2 Green boxes, 4 Yellow boxes, and 2 Red boxes left.

Absolute Zero has acted this round.

Baddies hasn't acted this round.
Legacy hasn't acted this round.
Scene hasn't acted this round.
Tachyon hasn't acted this round.
Wraith hasn't acted this round.


DarthDie
!hand-off-to Wraith
The Story so Far

It is currently a Green status. There are 2 Green boxes, 4 Yellow boxes, and 2 Red boxes left.

Wraith is the current actor.

Absolute Zero has acted this round.
@DarthDie has acted this round.

Baddies hasn't acted this round.
Scene hasn't acted this round.
Tachyon hasn't acted this round.
Wraith hasn't acted this round.


!advance
The Story so Far

It is currently a Green status. There are 1 Green boxes, 4 Yellow boxes, and 2 Red boxes left.

Absolute Zero has acted this round.

Baddies hasn't acted this round.
Legacy hasn't acted this round.
Scene hasn't acted this round.
Tachyon hasn't acted this round.
Wraith hasn't acted this round.


!introduce "Baron Blade"
The Story so Far

It is currently a Green status. There are 2 Green boxes, 3 Yellow boxes, and 2 Red boxes left.

Absolute Zero is the current actor.

Legacy hasn't acted this round.
The Wraith hasn't acted this round.
Baron Blade hasn't acted this round.


!erase "Baron Blade"
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

* Minion tracking
* Mod tracking?
* [Internal] Scene cleanup after 30 days

## License

Copyright © 2018 Briar Bowser