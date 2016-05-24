package swimmable;

import java.awt.Graphics;
import java.util.concurrent.CyclicBarrier;

/**
 * This abstract class represents variety types of animals that can swim in the
 * aquarium.
 * 
 * @author Shai Hod, ID: 304800402
 * 
 * @see Fish
 * @see Jellyfish
 */

public abstract class Swimmable extends Thread {
	
	protected int horSpeed;
	protected int verSpeed;
	protected CyclicBarrier barrier;

	public Swimmable() {
		horSpeed = 0;
		verSpeed = 0;
	}

	public Swimmable(int hor, int ver) {
		horSpeed = hor;
		verSpeed = ver;
	}

	public int getHorSpeed() {
		return horSpeed;
	}

	public int getVerSpeed() {
		return verSpeed;
	}

	public void setHorSpeed(int hor) {
		horSpeed = hor;
	}

	public void setVerSpeed(int ver) {
		verSpeed = ver;
	}

	public String getAnimalName(){
		return super.getName();
	}

	abstract public void drawAnimal(Graphics g);

	abstract public void setSuspend();

	abstract public void setResume();

	abstract public void setBarrier(CyclicBarrier b);

	abstract public int getSize();

	abstract public void eatInc();

	abstract public int getEatCount();

	abstract public String getColor();
	
	abstract public void setReset();
}
