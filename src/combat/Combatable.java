package combat;

public interface Combatable {
    /**
     * Attempt to attack the opponent
     *
     * @return amount of damage to deal on a successful attack
     */
    int attack(CombatType combatType);

    /**
     * Defend against an oncoming attack
     *
     * @param attackLevel the attack level of the opponent
     * @param combatType what to defend with
     *
     * @return true if the defense against the attack was successful, false if the attack hits
     */
    boolean defend(int attackLevel, CombatType combatType);

    /**
     * Take damage from a successful attack
     *
     * @param damage the amount of health points to lose
     *
     * @return true if HP remains, false if HP hits zero
     */
    boolean takeDamage(int damage);
}
