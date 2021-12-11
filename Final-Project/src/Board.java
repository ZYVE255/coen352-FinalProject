
public class Board {
	public int size; //Stores the width of the board
	public int bScore; //Stores the total board score
	public int[][] tScore; //Stores the individual tile scores [col][row]
	public Tile[][] tiles; //Stores the tiles [col][row]
	
	//-------------CONSTRUCTORS-------------
	
	/**
	 * Board constructor (creates an nxn board)
	 * 
	 * @param n Board width
	 */
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
	
	/**
	 * Board copy constructor
	 * 
	 * @param b Board to copy
	 */
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
	
	
	//-------------INSERTS AND REMOVES-------------
	
	/**
	 * Inserts a tile into the board at a given position with a specified rotation
	 * 
	 * @param c Insert column
	 * @param r Insert row
	 * @param t Tile to insert
	 * @param rot Insert rotation
	 */
	public void insert(int c, int r, Tile t, int rot) {
		Tile tile = new Tile(t);
		tile.rotateCW(rot);
		tiles[c][r] = tile;
		this.update(c,r);
	}
	
	/**
	 * Inserts a tile into the board at a given position with no rotation
	 * 
	 * @param c Insert column
	 * @param r Insert row
	 * @param t Tile to insert
	 */
	public void insert(int c, int r, Tile t) {
		this.insert(c, r, t, 0);
	}
	
	/**
	 * Inserts a tile into its optimal board location
	 * 
	 * @param t Tile to insert
	 */
	public void smartInsert(Tile t) {
		
		//Find tile type, eCount: 0-center, 1-edge, 2-corner
		int eCount = 0;
		if (t.up == 0)
			eCount++;
		if (t.down == 0)
			eCount++;
		if (t.left == 0)
			eCount++;
		if (t.right == 0)
			eCount++;
		
		int tempScore = -1;
		int rSelect = -1;
		int cSelect = -1;
		int rotation = 0;
		
		if (eCount == 2) {
			//Insert in corner
			for (int c = 0; c < size; c+=(size-1)) {
				for (int r = 0; r < size; r+=(size-1)) {
					if (this.tiles[c][r].up == -1) {
						for (int z = 0; z < 360; z += 90) {
							int testScore = this.insertTest(c, r, t, z);
							if (testScore > tempScore) {
								tempScore = testScore;
								rSelect = r;
								cSelect = c;
								rotation = z;
							}
						}
					}
				}
			}
		} else if (eCount == 1) {
			//Insert in edge
			for (int num = 1; num < size-1; num++) {
				if (this.tiles[num][0].up == -1) {
					for (int z = 0; z < 360; z += 90) {
						int testScore = this.insertTest(num, 0, t, z);
						if (testScore > tempScore) {
							tempScore = testScore;
							rSelect = 0;
							cSelect = num;
							rotation = z;
						}
					}
				}
				if (this.tiles[num][size-1].up == -1) {
					for (int z = 0; z < 360; z += 90) {
						int testScore = this.insertTest(num, size-1, t, z);
						if (testScore > tempScore) {
							tempScore = testScore;
							rSelect = size-1;
							cSelect = num;
							rotation = z;
						}
					}
				}
				if (this.tiles[0][num].up == -1) {
					for (int z = 0; z < 360; z += 90) {
						int testScore = this.insertTest(0, num, t, z);
						if (testScore > tempScore) {
							tempScore = testScore;
							rSelect = num;
							cSelect = 0;
							rotation = z;
						}
					}
				}
				if (this.tiles[size-1][num].up == -1) {
					for (int z = 0; z < 360; z += 90) {
						int testScore = this.insertTest(size-1, num, t, z);
						if (testScore > tempScore) {
							tempScore = testScore;
							rSelect = num;
							cSelect = size-1;
							rotation = z;
						}
					}
				}
				
			}
		} else if (eCount == 0) {
			//Insert in center
			for (int c = 1; c < size-1; c++) {
				for (int r = 1; r < size-1; r++) {
					if (this.tiles[c][r].up == -1) {
						for (int z = 0; z < 360; z += 90) {
							int testScore = this.insertTest(c, r, t, z);
							if (testScore > tempScore) {
								tempScore = testScore;
								rSelect = r;
								cSelect = c;
								rotation = z;
							}
						}
					}
				}
			}
		}
		
		if (cSelect == -1)
			return;
		Tile tile = new Tile(t);
		this.insert(cSelect, rSelect, tile, rotation);
	}
	
	/**
	 * Removes and returns a tile at a specified location
	 * 
	 * @param c Column of tile
	 * @param r Row of tile
	 * @return Tile removed
	 */
	public Tile remove(int c, int r) {
		Tile tile = new Tile(tiles[c][r]);
		tiles[c][r] = new Tile(); //Places null tile in old position
		this.update(c, r);
		return tile;
	}
		
	
	//-------------UPDATES-------------
	
	/**
	 * Updates the tScore and bScore based on a single tile
	 * 
	 * @param c Column of tile
	 * @param r Row of tile
	 */
	private void updateSingle(int c, int r) {
		Tile tile = tiles[c][r];
		int score = 0; //Stores temporary copy of tile's updated score
		
		if (tile.up == -1) { //Null tile case
			bScore -= tScore[c][r]; //bScore = bScore - oldTileScore + newTileScore
			tScore[c][r] = 0;
			return;
		}
		
		//Check upper edge
		if (r > 0) {
			if (tile.up == tiles[c][r-1].down)
				score++;
		} else { //Border check
			if (tile.up == 0)
				score++;
		}
		
		//Check lower edge
		if (r < size-1) {
			if (tile.down == tiles[c][r+1].up)
				score++;
		} else { //Border check
			if (tile.down == 0)
				score++;
		}
		
		//Check left edge
		if (c > 0) {
			if (tile.left == tiles[c-1][r].right)
				score++;
		} else { //Border check
			if (tile.left == 0)
				score++;
		}
		
		//Check right edge
		if (c < size-1) {
			if (tile.right == tiles[c+1][r].left)
				score++;
		} else { //Border check
			if (tile.right == 0)
				score++;
		}
		
		bScore -= tScore[c][r]; //bScore = bScore - oldTileScore + newTileScore
		tScore[c][r] = score;
		bScore += score;
	}
	
	/**
	 * Updates tScore and bScore based on a tile and its adjacent tiles
	 * 
	 * @param c Column of tile
	 * @param r Row of tile
	 */
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

	
	//-------------BOARD SCORES-------------
	
	/**
	 * Returns the ultimate score of the board (equivalent to bScore)
	 * 
	 * @return Total number of matching edges
	 */
	public int uScore() {
		int score = 0;
		for (int c = 0; c < size; c++) {
			for (int r = 0; r < size; r++) {
				score += tScore[c][r];
			}
		}
		return score;
	}
	
	/**
	 * Returns the number of perfect 3x3s
	 * 
	 * @return Total number of perfect 3x3 segments of the board
	 */
	public int threeScore() {
		int score = 0;
		for (int r = 1; r < size-1; r++) {
			for (int c = 1; c < size-1; c++) {
				if ((tiles[c][r].up == tiles[c][r-1].down) && //Check center piece
					(tiles[c][r].down == tiles[c][r+1].up) &&
					(tiles[c][r].right == tiles[c+1][r].left) && 
					(tiles[c][r].left == tiles[c-1][r].right) &&
					(tiles[c][r-1].left == tiles[c-1][r-1].right) && //Check upper piece
					(tiles[c][r-1].right == tiles[c+1][r-1].left) &&
					(tiles[c][r+1].left == tiles[c-1][r+1].right) && //Check lower piece
					(tiles[c][r+1].right == tiles[c+1][r+1].left) &&
					(tiles[c-1][r].up == tiles[c-1][r-1].down) && //Check left piece
					(tiles[c-1][r].down == tiles[c-1][r+1].up) &&
					(tiles[c+1][r].up == tiles[c+1][r-1].down) && //Check right piece
					(tiles[c+1][r].down == tiles[c+1][r+1].up)) {
					score++;
				}
			}
		}
		return score;
	}
	
	/**
	 * Tests the insert of a tile at a location in the board
	 * 
	 * @param c Insert test column
	 * @param r Insert test row
	 * @param t Test tile
	 * @param rot Test tile rotation
	 * @return The potential score of the tile if placed in the location
	 */
	public int insertTest(int c, int r, Tile t, int rot) {
		
		int matchScore = 2;
		int edgeScore = 100;
		int misScore = 0;
		int nullScore = 1;
		
		Tile tile = new Tile(t);
		tile.rotateCW(rot);
		int score = 0;
		
		if (tile.up == -1)
			return 0;
		
		//Check upper edge
		if (r > 0) {
			int temp = tiles[c][r-1].down;
			if (tile.up == temp)
				score += matchScore;
			else if (temp == -1)
				score += nullScore;
			else
				score += misScore;
		} else { //Border check
			if (tile.up == 0)
				score += edgeScore;
		}
		
		//Check lower edge
		if (r < size-1) {
			int temp = tiles[c][r+1].up;
			if (tile.down == temp)
				score += matchScore;
			else if (temp == -1)
				score += nullScore;
			else
				score += misScore;
		} else { //Border check
			if (tile.down == 0)
				score += edgeScore;
		}
		
		//Check left edge
		if (c > 0) {
			int temp = tiles[c-1][r].right;
			if (tile.left == temp)
				score += matchScore;
			else if (temp == -1)
				score += nullScore;
			else
				score += misScore;
		} else { //Border check
			if (tile.left == 0)
				score += edgeScore;
		}
		
		//Check right edge
		if (c < size-1) {
			int temp = tiles[c+1][r].left;
			if (tile.right == temp)
				score += matchScore;
			else if (temp == -1)
				score += nullScore;
			else
				score += misScore;
		} else { //Border check
			if (tile.right == 0)
				score += edgeScore;
		}
		
		return score;
	}
	
	
	//-------------PRINTS-------------
	
	/**
	 * Prints the tile scores of the board
	 */
	public void print() {
		for (int r = 0; r < size; r++) {
			String line = "";
			for (int c = 0; c < size; c++) {
				line += tScore[c][r];
			}
			System.out.println(line);
		}
	}
	
	/**
	 * Prints the current board
	 * 
	 * @param keys The keys used to display the board
	 */
	public void fullPrint(String[] keys) {
		for (int r = 0; r < size; r++) {
			String outputCeil = "-";
			String outputTop = "|";
			String outputMid = "|";
			String outputBot = "|";
				
			for (int c = 0; c < size; c++) {
				if (tiles[c][r].up == -1) {
					outputCeil += "----";
					outputTop += "   |";
					outputMid += "   |";
					outputBot += "   |";
				} else {
					outputCeil += "----";
					outputTop += " " + keys[tiles[c][r].up] + " |";
					outputMid += keys[tiles[c][r].left] + " " + keys[tiles[c][r].right] + "|";
					outputBot += " " + keys[tiles[c][r].down] + " |";
				}
			}
			System.out.println(outputCeil);
			System.out.println(outputTop);
			System.out.println(outputMid);
			System.out.println(outputBot);
		}
		String outputCeil = "-";
		for (int c = 0; c < size; c++) {
			outputCeil += "----";
		}
		System.out.println(outputCeil);
	}
}
