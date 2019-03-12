public class FnRMain
{

    /**
     * Main method from where the simulation starts
     *
     * @param args
     */
    public static void main(String[] args)
    {
        Simulator s = new Simulator(100, 100);
        s.simulate(500);
    }
}
