/**
 * An abstract class that is used to add additional "fight" functionality to an organism.
 * If it is implemented, the organism will have a strength level, max strength level etc.
 *
 * @author Nikolay Tsanov
 */
public abstract class FightableOrganism extends Organism
{
    protected FightableOrganism(Field field, Location location)
    {
        super(field, location);
    }

    // Current strength for the organism
    protected int strengthLevel;

    /**
     * Return the current strengthLevel
     *
     * @return int strengthLevel
     */
    protected int getStrengthLevel()
    {
        return this.strengthLevel;
    }

    /**
     * Get the maximum strength level for the organism.
     *
     * @return int An amount representing a maximum level that cannot be exceeded by an organism
     */
    protected abstract int getMaxStrengthLevel();

    /**
     * Increment the strength of the organism
     *
     * @param level An amount that will be added to the strength level.
     *              It cannot exceed the maximum strength level of the organism
     */
    protected void incrementStrength(int level)
    {
        this.strengthLevel = this.strengthLevel + level;
        if (this.strengthLevel > getMaxStrengthLevel()) {
            this.strengthLevel = getMaxStrengthLevel();
        }
    }
}
