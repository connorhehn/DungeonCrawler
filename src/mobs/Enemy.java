package mobs;

import combat.CombatType;
import combat.Combatable;

public class Enemy implements Combatable {
    private int healthPoints;
    private int defenseLevel;
    private int attackLevel;

    public Enemy() {
        healthPoints = (int)(Math.random() * 6 + 5);
        defenseLevel = (int)(Math.random() * 3 + 1);
        attackLevel = (int)(Math.random() * 10 + 1);
    }

    public int getAttackLevel() {
        return attackLevel;
    }

    @Override
    public int attack(CombatType combatType) {
        return (int)(Math.random() * 5 + 1);
    }

    @Override
    public boolean defend(int attackLevel, CombatType combatType) {
        return defenseLevel >= attackLevel;
    }

    @Override
    public boolean takeDamage(int damage) {
        healthPoints = Math.max(healthPoints - damage, 0); // assigns zero if health goes below zero
        return healthPoints == 0;
    }
}
