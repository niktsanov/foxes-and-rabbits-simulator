import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 2011.07.31
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;

    // The probability that a rabbit will be created in any given grid position.
    private static final double RABBIT_CREATION_PROBABILITY = 0.08;
    // The probability that a fox will be created in any given grid position.
    private static final double FOX_CREATION_PROBABILITY = 0.02;
    // The probability that a wolf will be created in any given grid position.
    private static final double WOLF_CREATION_PROBABILITY = 0.007;
    // The probability that a hunter will be created in any given grid position.
    private static final double HUNTER_CREATION_PROBABILITY = 0.005;

    // List of organisms in the field.
    private List<Organism> organisms;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;

    private boolean showRabbits = true, showFoxes = true, showWolves = true, showHunters = true;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }

    /**
     * Create a simulation field with the given size.
     *
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if (width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        organisms = new ArrayList<Organism>();
        field = new Field(depth, width);

        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * The simulation will run even though there might be no organisms left.
     *
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for (int step = 1; step <= numSteps; step++) {
            simulateOneStep();
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn organisms.
        List<Organism> newOrganisms = new ArrayList<Organism>();
        // Let all rabbits act.
        for (Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            organism.act(newOrganisms);
            if (!organism.isAlive()) {
                it.remove();
            }
        }

        // Add the newly born foxes and rabbits to the main lists.
        organisms.addAll(newOrganisms);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        organisms.clear();
        populate();
    }

    /**
     * Get the field
     *
     * @return Field field
     */
    public Field getField()
    {
        return field;
    }

    /**
     * Return the current step of the program
     *
     * @return int
     */
    public int getStep()
    {
        return step;
    }

    /**
     * Set whether the rabbits should be shown in the simulation
     *
     * @param showRabbits true or false
     */
    public void setShowRabbits(boolean showRabbits)
    {
        this.showRabbits = showRabbits;
    }

    /**
     * Set whether the foxes should be shown in the simulation
     *
     * @param showFoxes true or false
     */
    public void setShowFoxes(boolean showFoxes)
    {
        this.showFoxes = showFoxes;
    }

    /**
     * Set whether the hunters should be shown in the simulation
     *
     * @param showHunters true or false
     */
    public void setShowHunters(boolean showHunters)
    {
        this.showHunters = showHunters;
    }

    /**
     * Set whether the wolves should be shown in the simulation
     *
     * @param showWolves true or false
     */
    public void setShowWolves(boolean showWolves)
    {
        this.showWolves = showWolves;
    }

    /**
     * Randomly populate the field with foxes, rabbits, wolves and hunters.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {

                if (this.showRabbits && rand.nextDouble() <= RABBIT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rabbit rabbit = new Rabbit(true, field, location);
                    organisms.add(rabbit);
                } else if (this.showFoxes && rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fox fox = new Fox(true, field, location);
                    organisms.add(fox);
                } else if (this.showWolves && rand.nextDouble() <= WOLF_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Wolf wolf = new Wolf(true, field, location);
                    organisms.add(wolf);
                } else if (this.showHunters && rand.nextDouble() <= HUNTER_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Hunter hunter = new Hunter(true, field, location);
                    organisms.add(hunter);
                }

                // else leave the location empty.
            }
        }
    }
}
