import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Random;

public class Driver {

	public static void main(String[] args) {
		
		String[] keys = {"#","A","B","C","D","E","F","G","H","J",
						 "K","L","M","N","O","P","S","T","Y","Z",
						 "1","2","3","4","5","6","7","8","9"};
		int size = 16;
		//Initialize Board and Tile array
		Board board = new Board(size);
		Tile[] tiles = new Tile[size*size];
		
		//Populate tiles array
		try {
			File file = new File("test-set-e16");
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
		
		board = solve(board, tiles, 300);
		board.print();
		System.out.println("bScore:" + board.bScore + " uScore:" + board.uScore() + "\n");
		board.fullPrint(keys);
		/*
		genInitialSmart(board, tiles);
		board.print();
		System.out.println("bScore:" + board.bScore + " uScore:" + board.uScore() + "\n");
		
		//board.fullPrint(keys);
		
		int max_it = 100000;
		for (int i = 0; i < max_it; i++) {
			board = genAlt(board, 30);
			//board = nPerfReshuffle(board);
		}
		
		board.print();
		System.out.println("bScore:" + board.bScore + " uScore:" + board.uScore() + "\n");
		//board.fullPrint(keys);
		*/
	}
	
	
	public static Board solve(Board board, Tile[] tiles, int time) {
		System.out.println("Board initialized.");
		genInitialSmart(board, tiles);
		long currentTime = System.currentTimeMillis();
		long endTime = currentTime + (time * 1000);
		int prevTime = 0;
		System.out.println("Iterating board...");
		while (System.currentTimeMillis() < endTime) {
			int timeRemaining = (int)(endTime - System.currentTimeMillis())/1000;
			if (timeRemaining % 10 == 0) {
				if (prevTime != timeRemaining) {
					System.out.println("Time reaining: " + timeRemaining + "s");
				}
				prevTime = timeRemaining;
			}
			board = genAlt(board, 30);
		}
		System.out.println("Done.");
		return board;
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
		Random rand = new Random(System.currentTimeMillis());
		
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
	
	//Generate an alternate board with p tiles swapped
	public static Board nPerfReshuffle(Board board) {
		int size = board.size;
		Tile[] tiles = new Tile[size*size];
		Board altBoard = new Board(board);
		
		int tileIndex = 0;
		for (int c = 0; c < size; c++) {
			for (int r = 0; r < size; r++) {
				if (altBoard.tScore[c][r] != 4) {
					tiles[tileIndex++] = altBoard.remove(c, r);
				}
			}
		}
		
		for (int t = tileIndex - 1; t >= 0; t--) {
			if (tiles[t].isNull())
				break;
			altBoard.smartInsert(tiles[t]);
		}
		
		//System.out.println("b:" + board.bScore + " a:" + altBoard.bScore);
		
		if (altBoard.bScore > board.bScore)
			return altBoard;
		return board;
	}
}
