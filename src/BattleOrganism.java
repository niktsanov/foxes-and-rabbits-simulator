/**
 * An abstract class that is used to add additional "fight" functionality to an organism.
 * If it is implemented, the organism will have a strength level, max strength level etc.
 *
 * @author Nikolay Tsanov
 */
public abstract class BattleOrganism extends Organism
{
    // Current strength for the organism
    protected int strengthLevel;

    /**
     * Call back the superclass constructor.
     *
     * @param field    Field instance that will be passed to the superclass
     * @param location Location instance that will be passed to the superclass
     */
    protected BattleOrganism(Field field, Location location)
    {
        super(field, location);
    }

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
     * Assign a value to the strength level of the organism.
     *
     * @param strengthLevel Integer that will be assigned as a current strength of the organism
     */
    protected void setStrengthLevel(int strengthLevel)
    {
        this.strengthLevel = strengthLevel;
    }

    /**
     * Get the maximum strength level for the organism.
     *
     * @return int An amount representing a maximum level that cannot be exceeded by an organism
     */
    abstract protected int getMaxStrengthLevel();

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

    protected void decrementStrength() {
        this.decrementStrength(1);
    }

    /**
     * Decrement the current strength levels
     */
    protected void decrementStrength(int level)
    {
        if (this.strengthLevel - level < 0) this.strengthLevel = 0;
        else this.strengthLevel = this.strengthLevel - level;
    }
}
