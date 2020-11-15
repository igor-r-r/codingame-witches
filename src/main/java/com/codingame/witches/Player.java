package com.codingame.witches;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
public class Player {

    private static final int INVENTORY_MAX = 10;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // game loop
        while (true) {
            Game game = new Game(in);

            List<Action> potions = game.actions.stream().filter(a -> a.actionType.equals("BREW")).collect(Collectors.toList());
            List<Action> affordablePotions = potions.stream().filter(c -> actionAffordable(c, game.me)).collect(Collectors.toList());

            List<Action> spells = game.actions.stream().filter(a -> a.actionType.equals("CAST")).collect(Collectors.toList());
            //List<Action> affordableSpells = spells.stream().filter(c -> actionAffordable(c, game.me)).collect(Collectors.toList());
            //List<Action> castableSpells = spells.stream().filter(c -> c.castable).collect(Collectors.toList());

            if (affordablePotions.size() > 0) {
                Action action = potions.stream()
                        .filter(c -> actionAffordable(c, game.me))
                        .max(Comparator.comparing(Action::getPrice))
                        .get();
                System.out.println("BREW " + action.actionId);
            } else {
                List<Action> canCastSpells = spells.stream()
                        .filter(spell -> spell.castable && actionAffordable(spell, game.me))
                        .filter(spell -> fitsInventory(spell, game.me))
                        .filter(spell -> !isEnough(spell, potions, game.me))
                        .collect(Collectors.toList());

                if (canCastSpells.size() > 0) {
                    canCastSpells.forEach(System.err::println);
                    Collections.shuffle(canCastSpells);
                    canCastSpells.forEach(System.err::println);

                    System.out.println("CAST " + canCastSpells.get(0).actionId);
                } else {
                    System.out.println("REST");
                }
            }

            // in the first league: BREW <id> | WAIT; later: BREW <id> | CAST <id> [<times>] | LEARN <id> | REST | WAIT
        }
    }

    private static boolean isEnough(Action spell, List<Action> potions, Witch me) {
        if (spell.delta0 > 0) {
            return potions.stream().allMatch(potion -> me.inv0 + potion.delta0 >= 0);
        }

        if (spell.delta1 > 0) {
            return potions.stream().allMatch(potion -> me.inv1 + potion.delta1 >= 0);
        }

        if (spell.delta2 > 0) {
            return potions.stream().allMatch(potion -> me.inv2 + potion.delta2 >= 0);
        }

        if (spell.delta3 > 0) {
            return potions.stream().allMatch(potion -> me.inv3 + potion.delta3 >= 0);
        }

        return false;
    }

    private static boolean actionAffordable(Action action, Witch me) {
        return me.inv0 + action.delta0 >= 0
                && me.inv1 + action.delta1 >= 0
                && me.inv2 + action.delta2 >= 0
                && me.inv3 + action.delta3 >= 0;
    }

    private static boolean fitsInventory(Action spell, Witch me) {
        int spellSum = spell.delta0 + spell.delta1 + spell.delta2 + spell.delta3;
        int invSum = me.inv0 + me.inv1 + me.inv2 + me.inv3;
        return invSum + spellSum <= INVENTORY_MAX;

    }

}

class Game {

    List<Action> actions = new ArrayList<>();
    Witch me = new Witch();
    Witch enemy = new Witch();

    public Game(Scanner in) {
        initFromScanner(in);
    }

    private void initFromScanner(Scanner in) {
        int actionCount = in.nextInt(); // the number of spells and recipes in play
        for (int i = 0; i < actionCount; i++) {
            Action action = new Action();

            action.actionId = in.nextInt(); // the unique ID of this spell or recipe
            action.actionType = in.next(); // in the first league: BREW; later: CAST, OPPONENT_CAST, LEARN, BREW
            action.delta0 = in.nextInt(); // tier-0 ingredient change
            action.delta1 = in.nextInt(); // tier-1 ingredient change
            action.delta2 = in.nextInt(); // tier-2 ingredient change
            action.delta3 = in.nextInt(); // tier-3 ingredient change
            action.price = in.nextInt(); // the price in rupees if this is a potion
            action.tomeIndex =
                    in.nextInt(); // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax
            action.taxCount =
                    in.nextInt(); // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell
            action.castable = in.nextInt() != 0; // in the first league: always 0; later: 1 if this is a castable player spell
            action.repeatable = in.nextInt() != 0; // for the first two leagues: always 0; later: 1 if this is a repeatable player spell
            actions.add(action);
        }

        me.inv0 = in.nextInt(); // tier-0 ingredients in inventory
        me.inv1 = in.nextInt();
        me.inv2 = in.nextInt();
        me.inv3 = in.nextInt();
        me.score = in.nextInt(); // amount of rupees

        enemy.inv0 = in.nextInt(); // tier-0 ingredients in inventory
        enemy.inv1 = in.nextInt();
        enemy.inv2 = in.nextInt();
        enemy.inv3 = in.nextInt();
        enemy.score = in.nextInt(); // amount of rupees
    }
}

class Action {

    int actionId; // the unique ID of this spell or recipe
    String actionType; // in the first league: BREW; later: CAST, OPPONENT_CAST, LEARN, BREW
    int delta0; // tier-0 ingredient change
    int delta1; // tier-1 ingredient change
    int delta2; // tier-2 ingredient change
    int delta3; // tier-3 ingredient change

    int price; // the price in rupees if this is a potion

    int tomeIndex; // in the first two leagues: always 0; later: the index in the tome if this is a tome spell, equal to the read-ahead tax
    int taxCount; // in the first two leagues: always 0; later: the amount of taxed tier-0 ingredients you gain from learning this spell
    boolean castable; // in the first league: always 0; later: 1 if this is a castable player spell
    boolean repeatable; // for the first two leagues: always 0; later: 1 if this is a repeatable player spell

    public int getPrice() {
        return price;
    }
}

class Witch {

    int inv0; // tier-0 ingredients in inventory
    int inv1;
    int inv2;
    int inv3;
    int score; // amount of rupees
}