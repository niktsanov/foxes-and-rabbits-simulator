import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a hunter.
 * Foxes age, move, eat rabbits, and die.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 2011.07.31
 */
public class Hunter extends Organism
{
    // Characteristics shared by all hunters (class variables).

    // The max strength for a hunter
    private static final int MAX_STRENGTH = 100;
    // The max food level for a hunter
    private static final int MAX_FOOD_LEVEL = 12;
    // The age at which a hunter can start to breed.
    private static final int BREEDING_AGE = 60;
    // The age to which a hunter can live.
    private static final int MAX_AGE = 400;
    // The likelihood of a hunter breeding.
    private static final double BREEDING_PROBABILITY = 0.06;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The hunter's food level, which is increased by eating rabbits.
    private int foodLevel;

    private int strengthLevel;

    /**
     * Create a hunter. A hunter can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the hunter will have random age and hunger level.
     * @param field     The field currently occupied.
     * @param location  The location within the field.
     */
    public Hunter(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if (randomAge) {
            super.setAge(rand.nextInt(MAX_AGE));
//            this.strengthLevel = rand.nextInt(MAX_STRENGTH);
            this.foodLevel = rand.nextInt(MAX_FOOD_LEVEL);
        } else {
            this.foodLevel = MAX_FOOD_LEVEL;
//            this.strengthLevel = MAX_STRENGTH;
        }
        this.strengthLevel = rand.nextInt(MAX_STRENGTH);
    }

    /**
     * This is what the hunter does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     *
     * @param newHunter A list to return newly born hunters.
     */
    public void act(List<Organism> newHunter)
    {
        incrementAge();
        incrementHunger();
        decrementStrengthLevel();
        if (isAlive()) {
            giveBirth(newHunter);
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if (newLocation == null && isAlive()) {
                // No food found - try to move to a free location.
                // The hunter could be dead.
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
     * Increase the age. This could result in the hunter's death.
     */
    protected void incrementAge()
    {
        super.incrementAge();
        if (this.getAge() > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this hunter more hungry. This could result in the hunter's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Decrement the current strength levels
     */
    private void decrementStrengthLevel()
    {
        this.strengthLevel--;
        if (this.strengthLevel < 0) this.strengthLevel = 0;
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

        Rabbit randomRabbit = null; // backup rabbit
        ArrayList<Wolf> wolves = new ArrayList<Wolf>();

        while (it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);

            if (animal instanceof Wolf) {
                Wolf wolf = (Wolf) animal;
                if (wolf.isAlive()) wolves.add(wolf);
            } else if (animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if (rabbit.isAlive()) randomRabbit = rabbit;
            }
        }

        if (wolves.size() == 0) {
            if (randomRabbit != null) {
                Location where = randomRabbit.getLocation();

                randomRabbit.setDead();
                this.incrementStrength(5);
                this.incrementFoodLevel(6);

                // Remove the dead rabbit from the field.
                return where;
            }
        } else if (wolves.size() > 1) {
            int totalWolfStrength = 0;

            for (Wolf wolf : wolves) {
                totalWolfStrength += wolf.getStrengthLevel();
            }

            if (this.getStrengthLevel() >= totalWolfStrength) {
                this.incrementStrength(10);
                this.foodLevel = MAX_FOOD_LEVEL;
                Location where = wolves.get(0).getLocation();

                for (Wolf wolf : wolves) {
                    wolf.setDead();
                }

                System.out.println("Hunter wins! Hunter: " + this.getStrengthLevel() + " Pack:" + totalWolfStrength);
                return where;
            } else {
                System.out.println("The pack wins! Hunter: " + this.getStrengthLevel() + " Pack: " + totalWolfStrength + " \n ---");
                for (Wolf wolf : wolves) {
                    wolf.incrementStrength(3);
                    wolf.incrementFoodLevel(5);
                }
            }
        } else {
            // They are in random
            Wolf wolf = wolves.get(0);
            Location where = wolf.getLocation();
            System.out.println("Wolf: " + wolf.getStrengthLevel());
            System.out.println("Hunter: " + this.getStrengthLevel());

            if (this.getStrengthLevel() > wolf.getStrengthLevel()) {

                wolf.setDead();
                this.foodLevel = MAX_FOOD_LEVEL;
                this.incrementStrength(10);

                System.out.println("Hunter wins! \n ---");
                return where;
            } else if (this.getStrengthLevel() == wolf.getStrengthLevel()) {
                boolean randWin = rand.nextBoolean();

                if (randWin) {
                    wolf.setDead();
                    this.foodLevel = MAX_FOOD_LEVEL;
                    this.incrementStrength(10);

                    System.out.println("Hunter wins! \n ---");
                    return where;
                } else {
                    this.setDead();
                    wolf.incrementFoodLevel(wolf.getMaxFoodLevel());
                    wolf.incrementStrength(3);

                    System.out.println("Wolf wins! \n ---");
                }

            } else {
                this.setDead();
                wolf.incrementFoodLevel(wolf.getMaxFoodLevel());
                wolf.incrementStrength(3);

                System.out.println("Wolf wins! \n ---");
            }
        }


        return null;
    }

    /**
     * Set the current food level.
     *
     * @param foodLevel
     */
    protected void incrementFoodLevel(int foodLevel)
    {
        this.foodLevel = this.foodLevel + foodLevel;
        if (this.foodLevel > MAX_FOOD_LEVEL) {
            this.foodLevel = MAX_FOOD_LEVEL;
        }
    }

    /**
     * Check whether or not this hunter is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newFoxes A list to return newly born hunteres.
     */
    private void giveBirth(List<Organism> newFoxes)
    {
        // New hunteres are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Hunter young = new Hunter(false, field, loc);
            newFoxes.add(young);
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
     * Return the current strengthLevel
     *
     * @return int strengthLevel
     */
    protected int getStrengthLevel()
    {
        return this.strengthLevel;
    }

    /**
     * Increment the strength of the wolf with a value
     *
     * @param level
     */
    protected void incrementStrength(int level)
    {
        this.strengthLevel = this.strengthLevel + level;
        if (this.strengthLevel > MAX_STRENGTH) {
            this.strengthLevel = MAX_STRENGTH;
        }
    }

    /**
     * Returns the hunter's breeding age
     *
     * @return int BREEDING_AGE
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }
}
