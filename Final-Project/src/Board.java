
public class Board {
	public int size; //Stores the width of the board
	public int bScore; //Stores the total board score
	public int[][] tScore; //Stores the individual tile scores [col][row]
	public Tile[][] tiles; //Stores the tiles [col][row]
	
	//Creates a nxn board of null tiles
	public Board(int n) {
		size = n;
		bScore = 0;
		tScore = new int[n][n];
		tiles = new Tile[n][n];
		
		//Fill tScore with 0s and tiles with null tiles
		for (int c = 0; c < size; c++) {
			for (int r = 0; r < size; r++) {
				tScore[c][r] = 0;
				tiles[c][r] = new Tile();
			}
		}
	}
	
	public Board(Board b) {
		size = b.size;
		bScore = b.bScore;
		tScore = new int[size][size];
		tiles = new Tile[size][size];
		//Fill tScore with 0s and tiles with null tiles
		for (int c = 0; c < size; c++) {
			for (int r = 0; r < size; r++) {
				tScore[c][r] = b.tScore[c][r];
				tiles[c][r] = new Tile(b.tiles[c][r]);
			}
		}
	}
	
	//Updates tScore and bScore based on a single tile
	private void updateSingle(int c, int r) {
		Tile tile = tiles[c][r];
		int score = 0; //Stores temporary copy of tile's updated score
		
		if (tile.up == -1) { //Null tile case
			score = 0;
			bScore -= tScore[c][r]; //bScore = bScore - oldTileScore + newTileScore
			tScore[c][r] = score;
			bScore += score;
		}
		
		//Check upper edge
		if (c > 0) {
			if (tile.up == tiles[c-1][r].down)
				score++;
		} else { //Border check
			if (tile.up == 0)
				score++;
		}
		
		//Check lower edge
		if (c < size-1) {
			if (tile.down == tiles[c+1][r].up)
				score++;
		} else { //Border check
			if (tile.down == 0)
				score++;
		}
		
		//Check left edge
		if (r > 0) {
			if (tile.left == tiles[c][r-1].right)
				score++;
		} else { //Border check
			if (tile.left == 0)
				score++;
		}
		
		//Check right edge
		if (r < size-1) {
			if (tile.right == tiles[c][r+1].left)
				score++;
		} else { //Border check
			if (tile.right == 0)
				score++;
		}
		
		bScore -= tScore[c][r]; //bScore = bScore - oldTileScore + newTileScore
		tScore[c][r] = score;
		bScore += score;
	}
	
	//Updates tScore and bScore based on a tile c,r and its border tiles
	public void update(int c, int r) {
		updateSingle(c,r);
		
		if (c > 0)
			updateSingle(c-1,r);
		if (c < size-1)
			updateSingle(c+1,r);
		if (r > 0)
			updateSingle(c,r-1);
		if (r < size-1)
			updateSingle(c,r+1);
	}

	//Insert tile t at position [c][r] with rotation rot in deg
	public void insert(int c, int r, Tile t, int rot) {
		Tile tile = new Tile(t);
		tile.rotateCW(rot);
		tiles[c][r] = tile;
		this.update(c,r);
	}
	
	//Insert without rotation
	public void insert(int c, int r, Tile t) {
		this.insert(c, r, t, 0);;
	}
	
	//Removes and return tile [c][r]
	public Tile remove(int c, int r) {
		Tile tile = new Tile(tiles[c][r]);
		tiles[c][r] = new Tile(); //Places null tile in old position
		this.update(c, r);
		return tile;
	}
	
	//Prints the tile scores in a nxn grid
	public void print() {
		for (int c = 0; c < size; c++) {
			String line = "";
			for (int r = 0; r < size; r++) {
				line += tScore[c][r];
			}
			System.out.println(line);
		}
	}
	
	//Returns the 'ultimate' score of the board (computed by iteration), for debugging
	public int uScore() {
		int score = 0;
		for (int c = 0; c < size; c++) {
			for (int r = 0; r < size; r++) {
				score += tScore[c][r];
			}
		}
		return score;
	}
	
	//Returns the potential score of tile t insert at [c][r] with rot rotation
	public int insertTest(int c, int r, Tile t, int rot) {
		Tile tile = new Tile(t);
		tile.rotateCW(rot);
		int score = 0;
		
		if (tile.up == -1)
			return 0;
		
		//Check upper edge
		if (c > 0) {
			if (tile.up == tiles[c-1][r].down)
				score++;
		} else { //Border check
			if (tile.up == 0)
				score++;
		}
		
		//Check lower edge
		if (c < size-1) {
			if (tile.down == tiles[c+1][r].up)
				score++;
		} else { //Border check
			if (tile.down == 0)
				score++;
		}
		
		//Check left edge
		if (r > 0) {
			if (tile.left == tiles[c][r-1].right)
				score++;
		} else { //Border check
			if (tile.left == 0)
				score++;
		}
		
		//Check right edge
		if (r < size-1) {
			if (tile.right == tiles[c][r+1].left)
				score++;
		} else { //Border check
			if (tile.right == 0)
				score++;
		}
		
		return score;
	}
	
}
