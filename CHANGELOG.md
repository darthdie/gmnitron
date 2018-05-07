# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]
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