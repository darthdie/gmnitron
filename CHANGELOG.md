# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]

## 0.5.0
Fixed images.

BREAKING CHANGE FOR THOSE HOSTING:
I changed MONGODB_URI to DB_URI

## 0.4.9
GMnitron once again thought it could take the day off.

## 0.4.8
Fixed mod roll function.

## 0.4.7
Emergency fix to try and have GMnitron show up for work.

## 0.4.6
Calmed GMnitron a little bit when it comes to responding to human errors.
D4 minions can now, as is proper, survive to live another day.
GMnitron no longer scolds you when rolling a single mid die.

## 0.4.5
Expanded !editor command.
!min, !mid, and !max are now much less strict. They now support any number of dice and any order.

## 0.4.4
Added !editor, !chuck, and !proletariat commands.

## 0.4.0
Added !evil command.
Fixed incorrect deprecation message.
Updated scene tracker to advance to next zone upon ticking the last of the previous zone.

## 0.3.9
Added compound !minion rolls, e.g.
!minion d8, d6 v5

Added !ambush command to add an actor ready to act to the scene.

**breaking change(s):**
!introduce now adds an actor as already having acted this round.

## 0.3.8
Added !gloat and !insult commands.

## 0.3.7
Added !plan command.

## 0.3.6
Added help support for aliased commands.

## 0.3.5
!hand-off & !hand-off-to are now !hand off & !hand off to respectively.
Fixed !pass commands' deprecation message.
Added aliases for !introduce (!add) and !erase (!remove).
Added !current command, which displays the current actor.

## 0.3.4
Changed !censor to prepend/replace with "shm" rather than "smh", and now keeps all starting vowels.

## 0.3.3
Updated !hand-off to support name changes.