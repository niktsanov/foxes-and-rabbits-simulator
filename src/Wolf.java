import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a wolf.
 * Wolves age, move, eat mainly foxes, however if they are too hungry they eat rabbits,, and die.
 *
 * @author Nikolay Tsanov
 */
public class Wolf extends BattleOrganism
{
    // Characteristics shared by all wolves (class variables).

    // The max strength for a wolf
    private static final int MAX_STRENGTH = 100;
    // How much a wolf can eat
    private static final int MAX_FOOD_LEVEL = 12;
    // The age at which a wolf can start to breed.
    private static final int BREEDING_AGE = 35;
    // The age to which a wolf can live.
    private static final int MAX_AGE = 160;
    // The likelihood of a wolf breeding.
    private static final double BREEDING_PROBABILITY = 0.07;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;

    // Individual characteristics (instance fields).

    // The wolf's food level, which is increased by eating rabbits.
    private int foodLevel;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create a wolf. A wolf can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field     The field currently occupied.
     * @param location  The location within the field.
     */
    public Wolf(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if (randomAge) {
            super.setAge(rand.nextInt(MAX_AGE));
            this.foodLevel = rand.nextInt(MAX_FOOD_LEVEL);
        } else {
            this.foodLevel = MAX_FOOD_LEVEL;
        }
        // Always assign random strength so the simulation can be more interesting
        this.setStrengthLevel(rand.nextInt(this.getMaxStrengthLevel()));
    }

    /**
     * This is what the wolf does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     *
     * @param newWolves A list to return newly born wolves.
     */
    public void act(List<Organism> newWolves)
    {
        incrementAge();
        decrementFoodLevel();
        decrementStrength();
        if (isAlive()) {
            giveBirth(newWolves);
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if (newLocation == null) {
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if (newLocation != null) {
                setLocation(newLocation);
            } else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Wolves attack mainly foxes, however, if they are really hungry and there is a rabbit nearby,
     * they eat the rabbit, which increases their food levels just a bit and they get a bit of strength from
     * that. If they kill a fox, they get full and this also increases their strength more.
     *
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();

        // Backup rabbit that will be eaten in case there are no foxes around and the wolf is too hungry
        Rabbit randomRabbit = null;

        while (it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);

            // The wolf first searches for a fox in all nearby locations
            if (animal instanceof Fox) {
                Fox fox = (Fox) animal;
                if (fox.isAlive()) {
                    fox.setDead();
                    this.foodLevel = MAX_FOOD_LEVEL;
                    this.incrementStrength(5);
                    // Remove the dead fox from the field.
                    return where;
                }
            } else if (animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if (rabbit.isAlive()) randomRabbit = rabbit;
            }
        }

        // If no fox was found around and the hunger level of the wolf is low, then the wolf eats a rabbit if there is one.
        if (this.foodLevel <= 2 && randomRabbit != null) {
            Location where = randomRabbit.getLocation();
            randomRabbit.setDead();
            this.incrementFoodLevel(4);
            this.incrementStrength(1);

            // Remove the dead rabbit from the field.
            return where;
        }

        return null;
    }

    /**
     * Check whether or not this wolf is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newWolves A list to return newly born wolves.
     */
    private void giveBirth(List<Organism> newWolves)
    {
        // New wolves are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Wolf young = new Wolf(false, field, loc);
            newWolves.add(young);
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     *
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if (canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * Set the current food level.
     *
     * @param foodLevel amount that is added to the food level
     */
    protected void incrementFoodLevel(int foodLevel)
    {
        this.foodLevel = this.foodLevel + foodLevel;
        if (this.foodLevel > MAX_FOOD_LEVEL) {
            this.foodLevel = MAX_FOOD_LEVEL;
        }
    }

    /**
     * Make this wolf more hungry. This could result in the wolf's death.
     */
    private void decrementFoodLevel()
    {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Return the max strength of the wolf
     *
     * @return MAX_STRENGTH an integer between 0 and 100
     */
    protected int getMaxStrengthLevel()
    {
        return MAX_STRENGTH;
    }

    /**
     * Return the maximum allowed age for a wolf.
     *
     * @return MAX_AGE
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    /**
     * Return max food level.
     *
     * @return int MAX_FOOD_LEVEL
     */
    protected int getMaxFoodLevel()
    {
        return MAX_FOOD_LEVEL;
    }

    /**
     * Returns the wolf's breeding age
     *
     * @return int BREEDING_AGE
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
}
