[text](https://docs.google.com/document/d/1-VcbbPBpb_vwS5PSUSijhbOXk7AuvfNhEnpWgsr_rOw/edit?usp=sharing)

# Poker Pot Calculation Trainer 🃏💰

This is a JavaFX-based game to help practise mental maths for poker pot calculation. It randomly generates a set of poker chips with different values, and the goal is to mentally calculate the total. Useful for new dealers - set the colour of the chips to the set you use.

I made this trainer to help the new tournament dealers at my university's poker society.

If using the .jar file to run, then the default values are:

- 1 Chips = £1 (blue)
- 5 Chips = 0.05p (yellow)
- 25 Chips = 0.25p (pink)

Then (ensuring Java 21+ is installed) use the following command to run it:

```
java -jar java-poker-pot-trainer-1.0-SNAPSHOT.jar
```

## Extra Features

✅ Easy to change chip colour in the code, navigate to `App.java`, and search for `addChipToStack` and `getColorForValue` and adjust colours there.
✅ Tracks your time for each round and logs how long you take in stats menu.
✅ No need for the mouse! It auto-focuses the input box every round.

## How to run

Ensure you have Java JDK 21 of higher and Maven installed.

```
cd java-poker-pot-trainer
mvn javafx:run
```

## Plans

- Aim to add other training modes (example pot splitting calculations)
- Add GUI settings to change chip values and colours
- Will implement based on demands of uni's poker society dealers (:
