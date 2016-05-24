package aquarium;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.CyclicBarrier;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import swimmable.Swimmable;

/**
 * This class represents the panels and other elements in the main framework.
 * 
 * @author Shai Hod, ID: 304800402
 *
 */

public class AquaPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private AquaFrame aquaFrame;
	private BufferedImage img = null;
	private HashSet<Swimmable> animals = new HashSet<Swimmable>();
	private Iterator<Swimmable> ITE;
	private JScrollPane scrollPane;
	private boolean isFood = false;
	private boolean infoClicked = false;
	public static boolean AQisSuspend = false;

	/**
	 * Constructor that keeps a reference of the main framework (AquaFrame) to
	 * get an easy access. Also builds all the panels for the main framework.
	 * 
	 * @param aquaFrame
	 *            reference of the main framework.
	 */
	public AquaPanel(AquaFrame aquaFrame) {
		this.aquaFrame = aquaFrame;
		this.aquaFrame.setBounds(600, 300, 800, 600);
		this.aquaFrame.setResizable(false);
		this.setBackground(Color.WHITE);
		Panel(); // initialize panel buttons
	}

	/**
	 * This method creates the lower panel of buttons and adds it to the main
	 * framework.
	 */
	private void Panel() {
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
		JPanel Panel = new JPanel();
		Panel.setSize(800, 600);
		Panel.setLayout(new FlowLayout());

		// "Add Animal" button.
		JButton addAnimalBtn = new JButton("Add Animal");
		addAnimalBtn.setPreferredSize(new Dimension(107, 29));
		addAnimalBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Add Animal");
				addAnimalDialog();
			}
		});
		Panel.add(addAnimalBtn);

		// "Sleep" button.
		JButton sleepBtn = new JButton("Sleep");
		sleepBtn.setPreferredSize(new Dimension(107, 29));
		sleepBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Sleep");
				animalSleep();
			}
		});
		Panel.add(sleepBtn);

		// "Wake up" button.
		JButton wakeUpBtn = new JButton("Wake up");
		wakeUpBtn.setPreferredSize(new Dimension(107, 29));
		wakeUpBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Wake UP");
				animalWakeup();
			}
		});
		Panel.add(wakeUpBtn);

		// "Reset" button.
		JButton resetBtn = new JButton("Reset");
		resetBtn.setPreferredSize(new Dimension(107, 29));
		resetBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Reset");
				resetBoard();
			}
		});
		Panel.add(resetBtn);

		// "Food" button.
		JButton foodBtn = new JButton("Food");
		foodBtn.setPreferredSize(new Dimension(107, 29));
		foodBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Food");
				food();
			}
		});
		Panel.add(foodBtn);

		// "Info" button.
		JButton infoBtn = new JButton("Info");
		infoBtn.setPreferredSize(new Dimension(107, 29));
		infoBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Info");
				info();
			}
		});
		Panel.add(infoBtn);

		// "Exit" button.
		JButton exitBtn = new JButton("Exit");
		exitBtn.setPreferredSize(new Dimension(107, 29));
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Exit");
				System.exit(0);
			}
		});
		Panel.add(exitBtn);

		this.add(Panel, BorderLayout.SOUTH);
	}

	/**
	 * This method shows a table with all the information about the animals in
	 * the aquarium.
	 */
	private void info() {
		infoClicked = !infoClicked;

		if (infoClicked) {
			int totalFood = 0;
			DefaultTableModel model = new DefaultTableModel();
			model.setColumnIdentifiers(new String[] { "Animal", "Color",
					"Size", "Hor. Speed", "Ver. Speed", "Eat Count" });
			JTable table = new JTable(model);

			ITE = animals.iterator();
			while (ITE.hasNext()) {
				Swimmable cur = ITE.next();

				model.addRow(new Object[] { cur.getAnimalName(),
						cur.getColor(), cur.getSize(), cur.getHorSpeed(),
						cur.getVerSpeed(), cur.getEatCount() });

				totalFood += cur.getEatCount();
			}
			model.addRow(new Object[] { "Total:", "", "", "", "", totalFood });

			table.setPreferredScrollableViewportSize(table.getPreferredSize());
			scrollPane = new JScrollPane(table);
			add(scrollPane, BorderLayout.NORTH);
			aquaFrame.setVisible(true);
		} else {
			this.remove(scrollPane);
			validate();
			repaint();
			scrollPane = null;
			aquaFrame.setVisible(true);
		}
	}

	/**
	 * This method triggers small a waiting to a specific thread for all the
	 * other different threads will see there is food in the aquarium at the
	 * same moment.
	 */
	private void food() {
		try {
			isFood = true;
			CyclicBarrier b = new CyclicBarrier(animals.size());
			ITE = animals.iterator();
			while (ITE.hasNext()) {
				ITE.next().setBarrier(b);
			}
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(aquaFrame,
					"Please add one animal at least for giving a food.",
					"Error", JOptionPane.INFORMATION_MESSAGE);
			isFood = false;
		}
	}

	/**
	 * This boolean method returns if there is a food in the aquarium.
	 * 
	 * @return true, if there is a food on the panel. otherwise return false.
	 */
	public boolean thereIsFood() {
		return isFood;
	}

	/**
	 * This is CALLBACK method that increments the food counter of a specific
	 * animal.
	 * 
	 * @param anim
	 *            get the instance of a specific animal.
	 */
	public void ateFood(Swimmable anim) {
		synchronized (anim) {
			anim.eatInc(); // increments the food counter of a specific animal.
			isFood = false;
		}
	}

	/**
	 * This method update the boolean reset indicator to true in a specific
	 * animal for stopping all the animals threads.
	 */
	private void resetBoard() {
		ITE = animals.iterator();
		while (ITE.hasNext()) {
			ITE.next().setReset();
		}

		animals.clear(); // clear the main frame from animal.
		isFood = false; // if there is a food on the board it clears it.

		if (img != null)
			img = null;
		this.repaint();
		this.setBackground(Color.WHITE);
	}

	/**
	 * This method causes to all the threads of the aquarium to wake up.
	 */
	private void animalWakeup() {
		ITE = animals.iterator();
		while (ITE.hasNext()) {
			ITE.next().setResume();
		}
	}

	/**
	 * This method causes to all the threads of the aquarium to sleep.
	 */
	private void animalSleep() {
		ITE = animals.iterator();
		while (ITE.hasNext()) {
			ITE.next().setSuspend();
		}
	}

	/**
	 * This method opening the adds animal dialog for adding new animal to the
	 * aquarium. It checks for the number of animals in the aquarium, if less
	 * than 5, add animal dialog will open, otherwise appropriate message will
	 * appear.
	 */
	private void addAnimalDialog() {
		if (animals.size() > 4)
			JOptionPane.showMessageDialog(aquaFrame,
					"Cannot create more than 5 animals.", "Error",
					JOptionPane.INFORMATION_MESSAGE);
		else if (isFood == false)
			new AddAnimalDialog(this);
		else {
			JOptionPane.showMessageDialog(aquaFrame,
					"Wait until animals will eat their food.", "Error",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * This method adds a new animal to the aquarium.
	 * 
	 * @param animal
	 *            gets an animal instance for adding to the panel.
	 */
	protected void addAnimal(Swimmable animal) {
		animals.add(animal);
		animal.start();
	}

	/**
	 * This method sets a background to main framework.
	 * 
	 * @param i
	 *            the number of choice depending on the selection from the
	 *            sub-menu.
	 */
	public void setBackgr(int i) {
		if (i == 0) {
			if (img != null)
				img = null;
			this.repaint();
			this.setBackground(Color.WHITE);
		} else if (i == 1) {
			if (img != null)
				img = null;
			this.repaint();
			this.setBackground(Color.cyan);
		} else if (i == 2) {
			try {
				FileDialog fd = new FileDialog(aquaFrame, "Choose a photo:",
						FileDialog.LOAD);
				fd.setVisible(true);

				img = ImageIO.read(new File(fd.getDirectory() + fd.getFile()));

				repaint();

			} catch (IOException e) {
				System.out.println("Cannot load image");
			}
		}
	}

	/**
	 * This static method gets the RGB color codes, relevant to this program,
	 * and returns the color name.
	 * 
	 * @param obj
	 *            Object of Color.
	 * @return the color name.
	 */
	public static String ColorName(Object obj) {
		if (obj instanceof Color) {
			if (((Color) obj).equals(Color.RED))
				return "Red";
			else if (((Color) obj).equals(Color.BLUE))
				return "Blue";
			else if (((Color) obj).equals(Color.CYAN))
				return "Cyan";
			else if (((Color) obj).equals(Color.GREEN))
				return "Green";
			else if (((Color) obj).equals(Color.MAGENTA))
				return "Magenta";
		}

		return null;
	}

	/**
	 * This method paints the elements in the main framework.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (img != null) {
			g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
		}

		ITE = animals.iterator();
		while (ITE.hasNext()) {
			ITE.next().drawAnimal(g);
		}

		if (isFood == true) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3));
			g2.setColor(Color.red);
			g2.drawArc(getWidth() / 2, getHeight() / 2 - 5, 10, 10, 30, 210);
			g2.drawArc(getWidth() / 2, getHeight() / 2 + 5, 10, 10, 180, 270);
			g2.setStroke(new BasicStroke(1));
		}
	}
}
