import java.util.List;

/**
 * A class representing shared characteristics of organisms.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 2011.07.31
 */
public abstract class Organism
{
    // Whether the organism is alive or not.
    private boolean alive;
    // The organism's field.
    private Field field;
    // The organism's position in the field.
    private Location location;

    // The organism's age
    private int age;

    /**
     * Create a new organism at location in field.
     *
     * @param field    The field currently occupied.
     * @param location The location within the field.
     */
    public Organism(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }

    /**
     * Sets the organism's age
     *
     * @param int age
     */
    protected void setAge(int age)
    {
        this.age = age;
    }

    /**
     * Returns the organism's age
     *
     * @return int age
     */
    protected int getAge()
    {
        return this.age;
    }

    /**
     * Increments the organism's age
     */
    protected void incrementAge()
    {
        this.age++;
    }

    /**
     * Make this organism act - that is: make it do
     * whatever it wants/needs to do.
     *
     * @param newOrganisms A list to receive newly born organisms.
     */
    abstract public void act(List<Organism> newOrganisms);

    /**
     * Check whether the organism is alive or not.
     *
     * @return true if the organism is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the organism is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if (location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the organism's location.
     *
     * @return The organism's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the organism at the new location in the given field.
     *
     * @param newLocation The organism's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if (location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Returns the breeding age of a specific specie
     *
     * @return int
     */
    abstract protected int getBreedingAge();

    protected Boolean canBreed()
    {
        return this.getAge() >= this.getBreedingAge();
    }

    /**
     * Return the organism's field.
     *
     * @return The organism's field.
     */
    protected Field getField()
    {
        return field;
    }
}
