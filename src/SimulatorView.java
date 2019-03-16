import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.Timer;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 *
 * @author David J. Barnes and Michael Kölling
 * @version 2011.07.31
 */
public class SimulatorView extends JFrame implements ActionListener
{
    // Default speed/time of the simulation
    private static final int DEFAULT_TIMER_DELAY = 40;
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_HEIGHT = 80;
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;
    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    // The current height of the window
    private int height;
    // The current width of the window
    private int width;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private JLabel stepLabel, population;
    private JButton runButton, stopButton, resetButton, quitButton, nextStepButton;
    private JComboBox simulationSpeed;
    private FieldView fieldView;

    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;
    // The simulator that populates the data
    private Simulator simulator;

    private Timer simTimer;

    public SimulatorView()
    {
        this(DEFAULT_HEIGHT, DEFAULT_WIDTH);
    }

    /**
     * Create a view of the given width and height.
     *
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width)
    {
        if (height <= 0 || width <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            this.height = DEFAULT_HEIGHT;
            this.width = DEFAULT_WIDTH;
        } else {
            this.height = height;
            this.width = width;
        }

        this.simulator = new Simulator(this.height, this.width);
        simTimer = new Timer(DEFAULT_TIMER_DELAY, this);

        this.loadGUI();

        // Start state
        this.showStatus(this.simulator.getStep(), this.simulator.getField());
    }

    private void loadGUI()
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<Class, Color>();

        // Set title of the window
        setTitle("Fox and Rabbit Simulation");

        // If the user hits the X on the window, it stops the program
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // The main container for the window
        Container container = getContentPane();

        // TOP PANEL
        JPanel topPanel = new JPanel(new GridLayout(1, 1, 8, 8));
        stepLabel = new JLabel(STEP_PREFIX, SwingConstants.LEFT);
        topPanel.add(stepLabel, SwingConstants.CENTER);

        population = new JLabel(POPULATION_PREFIX, SwingConstants.RIGHT);
        topPanel.add(population);
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // MIDDLE PANEL
        JPanel middlePanel = new JPanel();
        fieldView = new FieldView(this.height, this.width);
        middlePanel.add(fieldView, BorderLayout.CENTER);

        // BOTTOM PANEL
        JPanel bottomPanel = new JPanel(new GridLayout(1, 5));

        String[] speeds = {"Very Slow", "Slow", "Normal", "Fast", "Very Fast"};
        this.simulationSpeed = new JComboBox<>(speeds);
        this.simulationSpeed.setSelectedIndex(2);
        bottomPanel.add(this.simulationSpeed);
        this.simulationSpeed.addActionListener(this);

        this.runButton = new JButton("Run");
        bottomPanel.add(this.runButton);
        this.runButton.addActionListener(this);

        this.stopButton = new JButton("Stop");
        bottomPanel.add(this.stopButton);
        this.stopButton.addActionListener(this);
        this.stopButton.setEnabled(false);

        this.resetButton = new JButton("Reset");
        bottomPanel.add(this.resetButton);
        this.resetButton.addActionListener(this);

        this.nextStepButton = new JButton("Next Step");
        bottomPanel.add(this.nextStepButton);
        this.nextStepButton.addActionListener(this);

        this.quitButton = new JButton("Quit");
        bottomPanel.add(this.quitButton);
        this.quitButton.addActionListener(this);

        // Bind everything to the container
        container.add(topPanel, BorderLayout.PAGE_START);
        container.add(middlePanel, BorderLayout.CENTER);
        container.add(bottomPanel, BorderLayout.PAGE_END);

        // Fit all elements on the window
        pack();
        // Set the window not to be resizable
        setResizable(false);
        // Show the window in the middle of the screen, it is more user friendly
        setLocationRelativeTo(null);
        // Set the window to be visible
        setVisible(true);

        this.setColor(Rabbit.class, Color.orange);
        this.setColor(Fox.class, Color.blue);
        this.setColor(Wolf.class, Color.DARK_GRAY);
        this.setColor(Hunter.class, Color.red);
    }

    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == this.quitButton) {
            dispose();
            System.exit(0);
        } else if (event.getSource() == this.runButton) {
            this.nextStepButton.setEnabled(false);
            this.resetButton.setEnabled(false);
            this.runButton.setEnabled(false);
            this.stopButton.setEnabled(true);
            this.simulationSpeed.setEnabled(false);

            this.simTimer.start();
        } else if (event.getSource() == this.resetButton) {
            this.simTimer.stop();
            this.simulator.reset();
            this.showStatus(this.simulator.getStep(), this.simulator.getField());
        } else if (event.getSource() == this.stopButton) {
            this.resetButton.setEnabled(true);
            this.runButton.setEnabled(true);
            this.stopButton.setEnabled(false);
            this.nextStepButton.setEnabled(true);
            this.simulationSpeed.setEnabled(true);

            this.simTimer.stop();
        } else if (event.getSource() == this.simTimer) {
            if (this.isViable(this.simulator.getField())) {
                this.simulator.simulateOneStep();
            } else {
                // Otherwise, stop the timer and reset
                JOptionPane.showMessageDialog(this, "The simulation has finished. \n" + stats.getPopulationDetails(this.simulator.getField()), "Simulation Result", JOptionPane.PLAIN_MESSAGE);

                this.simTimer.stop();
                this.simulator.reset();
                this.stopButton.setEnabled(false);
                this.runButton.setEnabled(true);
                this.resetButton.setEnabled(true);
                this.nextStepButton.setEnabled(true);
                this.simulationSpeed.setEnabled(true);
            }
            this.showStatus(this.simulator.getStep(), this.simulator.getField());
        } else if (event.getSource() == this.nextStepButton) {
            this.simulator.simulateOneStep();
            this.showStatus(this.simulator.getStep(), this.simulator.getField());
        } else if (event.getSource() == this.simulationSpeed) {

            switch (this.simulationSpeed.getSelectedIndex()) {
                case 0:
                    this.simTimer.setDelay(DEFAULT_TIMER_DELAY * 10);
                    break;
                case 1:
                    this.simTimer.setDelay(DEFAULT_TIMER_DELAY * 2);
                    break;
                case 3:
                    this.simTimer.setDelay(DEFAULT_TIMER_DELAY / 2);
                    break;
                case 4:
                    this.simTimer.setDelay(DEFAULT_TIMER_DELAY / 10);
                    break;
                default:
                    this.simTimer.setDelay(30);
                    break;
            }
            //            System.out.println("Current delay:" + this.simTimer.getDelay() + " sec");
        }

    }

    /**
     * Return the current simulator
     *
     * @return Simulator
     */
    public Simulator getSimulator()
    {
        return this.simulator;
    }

    /**
     * Define a color to be used for a given class of animal.
     *
     * @param animalClass The animal's Class object.
     * @param color       The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class animalClass)
    {
        Color col = colors.get(animalClass);
        if (col == null) {
            // no color defined for this class
            return UNKNOWN_COLOR;
        } else {
            return col;
        }
    }

    /**
     * Show the current status of the field.
     *
     * @param step  Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field)
    {
        if (!isVisible()) {
            setVisible(true);
        }

        stepLabel.setText(STEP_PREFIX + step);
        stats.reset();

        fieldView.preparePaint();

        for (int row = 0; row < field.getDepth(); row++) {
            for (int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if (animal != null) {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                } else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     *
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }

    /**
     * Provide a graphical view of a rectangular field. This is
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR, gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if (!size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if (xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if (yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }

        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale - 1, yScale - 1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if (fieldImage != null) {
                Dimension currentSize = getSize();
                if (size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                } else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
