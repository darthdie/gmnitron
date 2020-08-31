# Dice Commands

## General Commands

### !min
Rolls a dice pool and returns the min die (+ modifier).

Parameters:

- Die #1 - The size of the die.
- Die #2 - The size of the die.
- Die #3 - The size of the die.
- Modifier - Optional - A modifier to apply to the result.

!!! Examples
    ```
    !min d12 d6 d8

    !min d6 d6 d6 - 1

    !min d4 d4 d4 + 5
    ```

### !mid
Rolls a dice pool and returns the mid die (+ modifier).

Parameters:

- Die #1 - The size of the die.
- Die #2 - The size of the die.
- Die #3 - The size of the die.
- Modifier - Optional - A modifier to apply to the result.

!!! Examples
    ```
    !mid d12 d6 d8

    !mid d6 d6 d6 - 1

    !mid d4 d4 d4 + 5
    ```

### !max
Rolls a dice pool and returns the max die (+ modifier).

Parameters:

- Die #1 - The size of the die.
- Die #2 - The size of the die.
- Die #3 - The size of the die.
- Modifier - Optional - A modifier to apply to the result.

!!! Examples
    ```
    !max d12 d6 d8

    !max d6 d6 d6 - 1

    !max d4 d4 d4 + 5
    ```

## Action Commands

### !reaction
Returns the die result (+ modifier).

Parameters:

- Die - The size of the die.
- Modifier - Optional - A modifier to apply to the result.

!!! Examples
    ```
    !reaction d12

    !reaction d6 + 2
    ```

### !overcome
Returns the result of an overcome action using the effect die from the dice pool (+ modifier).

Parameters:

- Effect Die - Which die to use. (Min/Mid/Max)
- Die #1 - The size of the die.
- Die #2 - The size of the die.
- Die #3 - The size of the die.
- Modifier - Optional - A modifier to apply to the result.

!!! Examples
    ```
    !overcome mid d6 d6 d6

    !overcome max d4 d4 d4 + 2
    ```

## Modifier Commands

### !boost
Returns the positive mod size based on the result of the effect die from the dice pool (+ modifier).

Parameters:

- Effect Die - Which die to use. (Min/Mid/Max)
- Die #1 - The size of the die.
- Die #2 - The size of the die.
- Die #3 - The size of the die.
- Modifier - Optional - A modifier to apply to the result.

!!! Examples
    ```
    !boost mid d6 d6 d6

    !boost max d4 d4 d4 + 2
    ```

### !hinder
Returns the negative mod size based on the result of the effect die from the dice pool (+ modifier).

Parameters:

- Effect Die - Which die to use. (Min/Mid/Max)
- Die #1 - The size of the die.
- Die #2 - The size of the die.
- Die #3 - The size of the die.
- Modifier - Optional - A modifier to apply to the result.

!!! Examples
    ```
    !hinder mid d6 d6 d6

    !hinder max d4 d4 d4 + 2
    ```

## Enemy Commands

### !minion
Rolls minion die/dice (+ modifier), and optionally rolls against a save.

Parameters:

- Die - The size of the die.
- Modifier - Optional - A modifier to apply to the result.
- Save vs. - Optional - A number to save versus.

!!! Examples
    ```
    !minion d8

    !minion d6 + 2

    !minion d12 v5

    !minion d8 + 2 v8
    ```

!!! hint "Advanced Example"
    You can roll multiple minion dice and/or have them roll against a save vs. by separating minions with a comma:

    ```
    !minion d8, d6, d4

    !minion d4 + 2, d4

    !minion d8, d8 + 1, d6 v4
    ```

### !lt
Rolls a lieutenant die (+ modifier), and optionally rolls against a save.

Parameters:

- Die - The size of the die.
- Modifier - Optional - A modifier to apply to the result.
- Save vs. - Optional - A number to save versus.

!!! Examples
    ```
    !lt d8

    !lt d6 + 2

    !lt d12 v5

    !lt d8 + 2 v8
    ```

## Other

### !chuck

Chucks the dice and re-initalizes the random number generator.

!!! Examples
    ```
    !chuck
    ```