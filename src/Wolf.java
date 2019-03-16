import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a fox.
 * Foxes age, move, eat rabbits, and die.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 2011.07.31
 */
public class Wolf extends Animal
{
    // Characteristics shared by all wolves (class variables).
    // How much a wolf can eat
    private static final int MAX_FOOD_LEVEL = 12;

    // The age at which a wolf can start to breed.
    private static final int BREEDING_AGE = 45;
    // The age to which a wolf can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a wolf breeding.
    private static final double BREEDING_PROBABILITY = 0.05;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The wolf's food level, which is increased by eating rabbits.
    private int foodLevel;

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
            foodLevel = rand.nextInt(MAX_FOOD_LEVEL);
        } else {
            foodLevel = MAX_FOOD_LEVEL;
        }
    }

    /**
     * This is what the wolf does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     *
     * @param newWolves A list to return newly born wolves.
     */
    public void act(List<Animal> newWolves)
    {
        incrementAge();
        incrementHunger();
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
     * Increase the age. This could result in the wolf's death.
     */
    protected void incrementAge()
    {
        super.incrementAge();
        if (this.getAge() > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this wolf more hungry. This could result in the wolf's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     *
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while (it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);

            /**
             *  The wolf only hunts if he is hungry, otherwise it doesn't kill. If it gets hungry,
             *  it first checks if there is a fox around, otherwise, it eats a rabbit only if it is really
             *  hungry, to keep it alive..
             */
            if(this.foodLevel < MAX_FOOD_LEVEL) {
                if (animal instanceof Fox) {
                    Fox fox = (Fox) animal;
                    if (fox.isAlive()) {
                        fox.setDead();
                        //                    this.foodLevel++;
                        this.foodLevel = MAX_FOOD_LEVEL;
                        // Remove the dead fox from the field.
                        return where;
                    }
                } else if(animal instanceof Rabbit && this.foodLevel <= 2) {
                    Rabbit rabbit = (Rabbit) animal;
                    if (rabbit.isAlive()) {
                        rabbit.setDead();
                        //                    this.foodLevel++;
                        this.foodLevel = this.foodLevel + 2;
                        // Remove the dead fox from the field.
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this wolf is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newWolves A list to return newly born wolves.
     */
    private void giveBirth(List<Animal> newWolves)
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
     * Returns the wolf's breeding age
     *
     * @return int BREEDING_AGE
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
}
