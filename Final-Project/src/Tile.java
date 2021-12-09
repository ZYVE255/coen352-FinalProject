
public class Tile {
	//Stores the edges
	public int up;
	public int right;
	public int down;
	public int left;
	//Stores the rotation from initial orientation (0,90,180,270)
	public int rotation;
	
	//-------------CONSTRUCTORS-------------
	
	/**
	 * Creates a tile with specified edges
	 * 
	 * @param u Upper edge
	 * @param r Right edge
	 * @param d Bottom edge
	 * @param l Left edge
	 */
	public Tile(int u, int r, int d, int l) {
		up = u;
		right = r;
		down = d;
		left = l;
		rotation = 0;
	}
	
	/**
	 * Default constructor, creates a null tile
	 */
	public Tile() {
		up = -1;
		right = -1;
		down = -1;
		left = -1;
		rotation = 0;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param t Tile
	 */
	public Tile(Tile t) {
		up = t.up;
		right = t.right;
		down = t.down;
		left = t.left;
		rotation = t.rotation;
	}
	
	
	//-------------OTHER-------------
	
	/**
	 * Rotates a tile clockwise by multiples of 90deg
	 * 
	 * @param deg Degree of rotation
	 */
	public void rotateCW(int deg) {
		deg = deg % 360; //Normalizes deg to [0,360[
		int tempUp = up;
		int tempRight = right;
		
		switch (deg) {
			case 0:
				break;
			case 90: //Rotate 90 degrees
				up = left;
				left = down;
				down = right;
				right = tempUp;
				break;
			case 180: //Rotate 180 degrees
				up = down;
				down = tempUp;
				right = left;
				left = tempRight;
				break;
			case 270: //Rotate 270 degrees
				up = right;
				right = down;
				down = left;
				left = tempUp;
				break;
			default: //If its not a multiple of 90deg, don't do anything
				break;
		}
		//Update rotation variable
		rotation += deg;
		rotation = rotation % 360;
		return;
	}
	
	/**
	 * Checks if the tile is null
	 * 
	 * @return True if tile is null, false otherwise
	 */
	public boolean isNull() {
		if (up == -1)
			return true;
		return false;
	}
}
