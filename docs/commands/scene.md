# Scene Commands

### !establish
Establishes the scene with the given number of ticks, and the 'actors' in the scene/round. The actors argument works with @mentions.

Parameters:

- Green Ticks - The number of green ticks to have in the scene.
- Yellow Ticks - The number of yellow ticks to have in the scene.
- Red Ticks - The number of red ticks to have in the scene.
- Actors - A list of actors separated with a space.

!!! Examples
    ```
    !establish 2 4 2 Tachyon Wraith Omnitron

    !establish 3 3 3 Legacy "Absolute Zero" Bunker "Baron Blade"

    !establish 2 2 2 @real_person @thetruthisinhere Isis
    ```

### !hand off
Hand the scene off to the given actor.

Parameters:

- From Actor - The actor to hand off from.
- To Actor - The actor to hand off to.

!!! Examples
    ```
    !hand off Legacy Wraith
    ```

### !hand off to
Hands off the scene from the current user to actor. Used by people who were added in !establish via a @mention.

!!! Examples
    ```
    !hand off to Bunker
    ```

### !advance
Tick off the next box in the scene.

!!! Examples
    ```
    !advance
    ```

### !introduce
Adds an actor to the scene that can act next round.

Parameters:

- Actor Name - The name of the actor to introduce.

!!! Examples
    ```
    !introduce Baron Blade
    ```

### !ambush
Adds an actor to the scene that acts this round.

!!! Examples
    ```
    !ambush Baron Blade
    ```

### !erase
Removes an actor from the scene/initiative.

!!! Examples
    ```
    !erase Baron Blade
    ```

### !recap
Gives a recap of the scene and initiative.

!!! Examples
    ```
    !recap
    ```

### !current
Displays the current actor.

!!! Examples
    ```
    !current
    ```
