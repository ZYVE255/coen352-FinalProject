import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Random;

public class Driver {

	public static void main(String[] args) {
		int size = 10;
		//Initialize Board and Tile array
		Board board = new Board(size);
		Tile[] tiles = new Tile[size*size];
		
		//Populate tiles array
		try {
			File file = new File("test-set");
			Scanner reader = new Scanner(file);
			int i = 0;
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				String[] data = line.split(" ");
				tiles[i++] = new Tile(Integer.parseInt(data[0]),
						Integer.parseInt(data[1]),
						Integer.parseInt(data[2]),
						Integer.parseInt(data[3]));
			}
		} catch (FileNotFoundException e) { }
		
		
		
		genInitialSmart(board, tiles);
		board.print();
		System.out.println("bScore:" + board.bScore + " uScore:" + board.uScore() + "\n");
		
		int max_it = 100000;
		for (int i = 0; i < max_it; i++) {
			board = genAlt(board, 20);
		}
		
		board.print();
		System.out.println("bScore:" + board.bScore + " uScore:" + board.uScore() + "\n");
	}
	
	//Generates an initial board based on a set of tiles using smartInsert
	public static void genInitialSmart(Board board, Tile[] tiles) {
		for (Tile t : tiles)
			board.smartInsert(t);
	}
	
	//Generate an initial board via random placement
	public static void genInitialRandom(Board board, Tile[] tiles) {
		int size = board.size;
		int t = 0;
		
		for (int c = 0; c < size; c++) {
			for (int r = 0; r < size; r++) {
				Tile tile = new Tile(tiles[t++]);
				board.insert(c, r, tile);
			}
		}
	}
	
	//Generate an alternate board with p tiles swapped
	public static Board genAlt(Board board, int p) {
		Tile[] tiles = new Tile[p];
		Board altBoard = new Board(board);
		Random rand = new Random();
		
		int size = board.size;
		int tileIndex = 0;
		while (p > 0) {
			int maxAttempt = 0;
			while (maxAttempt < 100) {
				int c = rand.nextInt(size);
				int r = rand.nextInt(size);
				if (altBoard.tiles[c][r].up != -1 && altBoard.tScore[c][r] != 4) {
					tiles[tileIndex++] = altBoard.remove(c, r);
					break;
				}
				maxAttempt++;
			}
			p--;
		}
		
		for (Tile t : tiles) {
			altBoard.smartInsert(t);
		}
		
		//System.out.println("b:" + board.bScore + " a:" + altBoard.bScore);
		
		if (altBoard.bScore > board.bScore)
			return altBoard;
		return board;
	}
	
	//Legacy
	
	/*
	//Semi-intelligently generate an initial board
	public static void smartInsert(Board board, Tile[] tiles) {
		int size = board.size;
		for (Tile t : tiles) {
			int c_select = -1;
			int r_select = -1;
			int tempScore = -1;
			int rot = 0;
			for (int c = 0; c < size; c++) {
				for (int r = 0; r < size; r++) {
					if (board.tiles[c][r].up == -1) {
						for (int z = 0; z < 360; z += 90) {
							int testScore = board.insertTest(c, r, t, z);
							if (testScore > tempScore) {
								tempScore = testScore;
								r_select = r;
								c_select = c;
								rot = z;
							}
						}
					}
				}
			}
			
			//System.out.println("row:" + r_select + " col:" + c_select + " rot:" + rot + " score:" + tempScore);
			if (c_select == -1)
				break;
			Tile tile = new Tile(t);
			board.insert(c_select, r_select, tile, rot);
		}
	}
	
	//Type: 0-center, 1-edges, 2-corners
	public static Board genAlternativeOld(Board board, int p) {
		Board tempBoard = new Board(board);
		Random rand = new Random();
		Tile[] tiles = new Tile[p];
		int size = board.size;
		int i = 0;
		while (p > 0) {
			int j = 0;
			while (j < 100) {
				int c = rand.nextInt(size);
				int r = rand.nextInt(size);
				if (tempBoard.tiles[c][r].up != -1 && tempBoard.tScore[c][r] != 4) {
					tiles[i++] = tempBoard.remove(c, r);
					break;
				}
				j++;
			}
			p--;
		}
		
		smartInsert(tempBoard, tiles);
		//System.out.println(tempBoard.bScore + " " + board.bScore);
		if (tempBoard.bScore > board.bScore)
			return tempBoard;
		return board;
	}
	*/
}
