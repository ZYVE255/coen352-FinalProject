import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.Random;

public class Driver {

	public static void main(String[] args) {
		
		//Used for board display
		String[] keys = {"#","A","B","C","D","E","F","G","H","J",
						 "K","L","M","N","O","P","S","T","Y","Z",
						 "1","2","3","4","5","6","7","8","9"};
		
		
		//Initialize Board and Tile array
		int size = 16;
		Board board = new Board(size);
		Tile[] tiles = new Tile[size*size];
		
		
		//Populate tiles array
		try {
			File file = new File("test-set-16");
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
		
		
		//board = solve(board, tiles, 30);
		long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		board = solveIterations(board, tiles, 1, 0, 10000, 10000, 40, 15, false);
		long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long actualMemUsed = afterUsedMem - beforeUsedMem;
		System.out.println(actualMemUsed/1000000);
		board.print();
		System.out.println("bScore:" + board.bScore + " uScore:" + board.uScore() + " 3Score:" + board.threeScore() + "\n");
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
		long endTime = currentTime + (time/2 * 1000);
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
			board = genAlt(board, 30, 1);
		}
		
		currentTime = System.currentTimeMillis();
		endTime = currentTime + (time/2 * 1000);
		while (System.currentTimeMillis() < endTime) {
			board = genAlt(board, 10, 0);
		}
		
		System.out.println("Done.");
		return board;
	}
	
	public static Board solveIterations(Board board, Tile[] tiles, int p1_type, int p2_type, 
			int p1_its, int p2_its, int p1_swap, int p2_swap, boolean export) {
		
		int[] scores = new int[p1_its + p2_its];
		long start = System.currentTimeMillis();
		genInitialSmart(board, tiles);
		System.out.println("Board initialized.");
		System.out.println("Initial matching edges: " + scoreToEdges(board.bScore, board.size));
		System.out.println("Iterating phase 1...");
		for (int it = 0; it < p1_its; it++) {
			board = genAlt(board, p1_swap, p1_type);
			scores[it] = board.bScore;
		}
		System.out.println("Phase 1 matching edges: " + scoreToEdges(board.bScore, board.size));
		System.out.println("Iterating phase 2...");
		for (int it = 0; it < p2_its; it++) {
			board = genAlt(board, p2_swap, p2_type);
			scores[p1_its + it] = board.bScore;
		}
		long end = System.currentTimeMillis();
		System.out.println("Phase 2 matching edges: " + scoreToEdges(board.bScore, board.size));
		System.out.println("Done.");
		System.out.println("Run time (ms): " + (end-start));
		System.out.println("Final matching edges: " + scoreToEdges(board.bScore, board.size));
		
		if (export) {
			try {
				FileWriter writer = new FileWriter("testExport.csv");
	
				for (int i = 0; i < scores.length; i++) {
				    writer.append(String.valueOf(scores[i]));
				    writer.append("\n");
				}
				writer.close();
			} catch (Exception e) {
			}
		}
		
		return board;
	}
	
	
	/**
	 * Generate an initial board based on smart insertion
	 * 
	 * @param board Initial board
	 * @param tiles Tile set to insert
	 */
	public static void genInitialSmart(Board board, Tile[] tiles) {
		for (Tile t : tiles)
			board.smartInsert(t);
	}
	
	/**
	 * Generates an initial board randomly
	 * 
	 * @param board Input board
	 * @param tiles Input tile set
	 */
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
	
	/**
	 * Generates an alternate board
	 * 
	 * @param board Input board
	 * @param p Total tile swaps
	 * @param type Type of selection criterion (0:Total matched edges , 1:Perfect 3x3s )
	 * @return Alternate board or initial board based on selection criterion
	 */
	public static Board genAlt(Board board, int p, int type) {
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
		switch (type) {
			case 0:
				if (altBoard.bScore > board.bScore)
					return altBoard;
				return board;
			case 1:
				if (altBoard.threeScore() > board.threeScore())
					return altBoard;
				return board;
			default:
				if (altBoard.bScore > board.bScore)
					return altBoard;
				return board;
		}
	}
	
	/**
	 * Generates an alternate board by swapping all non-perfect tiles
	 * 
	 * @param board Input board
	 * @return Alternate board or initial board based on total correct tiles
	 */
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

	public static int scoreToEdges(int score, int size) {
		return (score - (size*size))/2;
	}
}
