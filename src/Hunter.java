import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A model of a hunter.
 * Hunters hunt wolves and feed on them. If there are no wolves around, the hunters eat rabbits to survive.
 * They pick up fights with wolves, however, they can be defeated if they have lower strength than the opponent.
 * If they are many wolves around a hunter, then this is considered to be a pack of wolves, so the hunter attacks the whole
 * pack (with the combined strength of all of the wolves).
 *
 * @author Nikolay Tsanov
 */
public class Hunter extends BattleOrganism
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

    // Individual characteristics (instance fields).

    // The hunter's food level, which is increased by eating rabbits.
    private int foodLevel;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

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
            this.foodLevel = rand.nextInt(MAX_FOOD_LEVEL);
        } else {
            this.foodLevel = MAX_FOOD_LEVEL;
        }

        // Always assign random strength so the simulation can be more interesting
        this.setStrengthLevel(rand.nextInt(this.getMaxStrengthLevel()));
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
        decrementFoodLevel();
        decrementStrength();
        if (isAlive()) {
            giveBirth(newHunter);
            // Move towards a source of food if found.
            Location newLocation = findFood();
            // Check if the hunter is still alive, otherwise don't move.
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
     * Search for wolves around, if so pick a fight with them, otherwise search for a rabbit to kill.
     * If there are many wolves around the hunter, then it is considered a pack, fight with the pack.
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

        // We check if there are any wolves around the hunter.
        // If there are none around, then we check if there is a rabbit and kill it.
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
            // If there are more than one, then it is considered a pack.
            int totalWolfStrength = 0;

            // We get the total strength of all wolves inside the pack.
            for (Wolf wolf : wolves) {
                totalWolfStrength += wolf.getStrengthLevel();
            }

            // If the strength of the hunter is bigger or the same as the pack's strength, it kills all of the wolves
            // and moves to one of the locations.
            if (this.getStrengthLevel() >= totalWolfStrength) {
                this.incrementStrength(10);
                this.foodLevel = MAX_FOOD_LEVEL;

                // Since the wolves are in a random order, we just take the first one.
                Location where = wolves.get(0).getLocation();

                for (Wolf wolf : wolves) {
                    wolf.setDead();
                }

                System.out.println("Hunter wins! Hunter: " + this.getStrengthLevel() + " Pack:" + totalWolfStrength + " \n ---");
                return where;
            } else {
                // Otherwise the pack wins. All of the wolves get strength and food from the fight.
                System.out.println("The pack wins! Hunter: " + this.getStrengthLevel() + " Pack: " + totalWolfStrength + " \n ---");
                // The hunter is killed, so he is set as dead
                this.setDead();

                for (Wolf wolf : wolves) {
                    wolf.incrementStrength(3);
                    wolf.incrementFoodLevel(5);
                }
            }
        } else {
            // Otherwise, there is only one wolf around the hunter.
            // We compare their strength levels and the toughest wins.
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

                // When the wolf and the hunter have the same strength levels. the victory is on random
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
     * Check whether or not this hunter is to give birth at this step.
     * New births will be made into free adjacent locations.
     *
     * @param newHunters A list to return newly born hunters.
     */
    private void giveBirth(List<Organism> newHunters)
    {
        // New hunters are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Hunter young = new Hunter(false, field, loc);
            newHunters.add(young);
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
     * @param foodLevel the amount that will be added to the foodLevel; don't exceed the max value
     */
    protected void incrementFoodLevel(int foodLevel)
    {
        this.foodLevel = this.foodLevel + foodLevel;
        if (this.foodLevel > MAX_FOOD_LEVEL) {
            this.foodLevel = MAX_FOOD_LEVEL;
        }
    }

    /**
     * Make this hunter more hungry. This could result in the hunter's death.
     */
    private void decrementFoodLevel()
    {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Return the max strength level of the hunter.
     *
     * @return MAX_STRENGTH Representing the value that cannot be exceeded for strength
     */
    protected int getMaxStrengthLevel()
    {
        return MAX_STRENGTH;
    }

    /**
     * Returns the hunter's breeding age
     *
     * @return int BREEDING_AGE Representing the value that cannot be exceeded for breeding age
     */
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    /**
     * Return the maximum age that is allowed for a hunter.
     *
     * @return MAX_AGE Representing the value that cannot be exceeded for age
     */
    protected int getMaxAge()
    {
        return MAX_AGE;
    }
}
