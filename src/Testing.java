import java.util.Date;
import java.util.List;

public class Testing {

    static String startingFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    static String anotherFEN = "rnbqkbnr/pp1p1ppp/2p5/4p3/3P4/8/PPPNPPPP/R1BQKBNR w KQkq e6 0 3";

    static String exempluFEN = "1Rkrr3/p4p1p/5b1n/2pP4/2P1P3/P5P1/2K4P/8 b - - 0 32";

    static String randomFEN = "n1n5/PPPk4/8/8/8/8/4Kppp/5N1N b - - 0 1";

    static String trickyFEN = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1";

    static String enPasFEN = "rnbqkbnr/ppppp1pp/8/8/5pP1/8/PPPPPP1P/RNBQKBNR b KQkq g3 0 3";

    static String matein2FEN = "2bqkbn1/2pppp2/np2N3/r3P1p1/p2N2B1/5Q2/PPPPKPP1/RNB2r2 w - - 0 1";

    static String matein7FEN = "r2k3r/pb3pp1/1pp3N1/b1P2QPn/1n4pq/2N3B1/PPP2PPP/R3K2R w KQ - 0 1";
    
    static String zugzwang1FEN = "8/8/p1p5/1p5p/1P5p/8/PPP2K1p/4R1rk w - - 0 1";
    
    static String zugzwang2FEN = "1q1k4/2Rr4/8/2Q3K1/8/8/8/8 w - - 0 1";
    static Move[] moveH;
    static int[][] extenedBoards;
    static {
        moveH = new Move[32];
        extenedBoards = new int[32][120];
    }
 
    public static SearchMove search(Board board, SearchInfo info) {
		
		Move bestMove = null;
		
		SearchMove result = new SearchMove();
		
		int oldAlfa = -MoveGen.MATE_VALUE;
		
		/*
		 * for(int i = 0; i < info.maxDepth; i++) {
		 * 
		 * result = Search.negaAlphaBeta(board, i, i, alpha, beta, player, box);
		 * 
		 * }
		 */
		
		
		
		return result;
		
	}

	public static void testMoveInsert() {
    	MoveList moveList = new MoveList();
    	
    	String FEN = "r3k2r/p2pqpb1/bn1Ppnp1/2p1N3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq c6 0 2";
    	
    	Board board = new Board();
    	board.initFEN(FEN);
    	
    	board.printCrtState();
    	
    	MoveGen mGen = new MoveGen();
    	
    	Move move = new Move(74, 85, 10, 12, false, 0, false);
    	
    	MoveGen.addCaptureMove(board, move, moveList);
    	
    	System.out.println("size = " + moveList.size);
    	
    	System.out.println(moveList.list[moveList.size - 1].score);
    	
    }

	public static void testGenMoves() {
		MoveGen mg = new MoveGen();
		Board board = new Board();
		
		String FEN = "r3k2r/p1ppq1b1/bn2Qnp1/3Pp3/1p2P3/2N4p/PPPBBPPP/R3K2R b KQkq - 1 3";
		
		board.initFEN(FEN);
		
		MoveList moveList = new MoveList();
		
		MoveGen.genAllMoves(board, moveList);
		
		
		for(int i = 0; i < moveList.size; i++) {
			interpretareMutare(board, moveList.list[i]);
		}
		
		System.out.println("am gasit in total " + moveList.size + " mutari (legale sau nu)");
	}
	
	public static void interpretareMutare(Board board, Move move) {
		int forward = board.side == 1 ? -1 : 1;
		
//		System.out.println("Am interpretat mutarea ca fiind");
//        System.out.println("from: " + MoveGen.letters[MoveGen.fileOf[move.from]] + ""
//                + MoveGen.numbers[MoveGen.rankOf[move.from]]);
//        System.out.println("to: " + MoveGen.letters[MoveGen.fileOf[move.to]] + ""
//                + MoveGen.numbers[MoveGen.rankOf[move.to]]);
//        System.out.println("capture: " + board.pieceChar[move.capture]);
//        System.out.println("promotion: " + board.pieceChar[move.promotion]);
//        System.out.println("pawnStart: " + move.pawnStart);
//        System.out.println("Castling: " +
//                ((move.castling != 0) ? (((move.castling & C_PERM.WKC.value) != 0 ? "K" : "") +
//                        ((move.castling & C_PERM.WQC.value) != 0 ? "Q" : "") +
//                        ((move.castling & C_PERM.BKC.value) != 0 ? "k" : "") +
//                        ((move.castling & C_PERM.BQC.value) !=
//                                0 ? "q" : "")) : "-"));
//        System.out.println("enPas: " + move.enPas);
//        System.out.println("score: " + move.score);
//        System.out.println("forward: " + forward);
//        
        String moveString = "";
        
        moveString += MoveGen.letters[MoveGen.fileOf[move.from]] + ""
                + MoveGen.numbers[MoveGen.rankOf[move.from]] + ""
                + MoveGen.letters[MoveGen.fileOf[move.to]] + ""
                + MoveGen.numbers[MoveGen.rankOf[move.to]] + ""
                + (move.promotion == PIECE.EMPTY.value ? "" : board.pieceChar[move.promotion] );
        
        System.out.println("move " + moveString + " score: " + move.score);
	}
	
	public static void testNouGenMoves() {
		
		Board board = new Board();
		
		String FEN = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/Pp2P3/2N2Q1p/1PPBBPPP/R3K2R b KQkq a3 0 1";
		
		board.initFEN(matein2FEN);
		
		board.printCrtState();
        MoveGen mg = new MoveGen();
        // 4865609
        Date date = new Date();
        long before = date.getTime();
        long nr = 0;
        int depth = 5;
        
        nr += testareDepthNou(board, depth, depth);

        Date date2 = new Date();
        long after = date2.getTime();

        board.printCrtState();


        if(before != after) {
            System.out.println("dept = " + depth + " si am trecut prin " + nr + " noduri ");
            System.out.format("%,8d%n", (nr)*1000 / (after - before));
            System.out.println("noduri pe secunda");
        }

	}
	
    private static int testareDepthNou(Board board, int depth, int initDepth){

        if(depth == 0) {
            return 1;
        }

        MoveList moveList = new MoveList();
        MoveGen.genAllMoves(board, moveList);
        
//        System.out.println("TRECE DE PTRIMUL MOVE GEN???");

        int nr = 0;
        int movesMade = 0;
        int res;

        for(int i = 0; i < moveList.size; i++) {
        	movesMade++;
        	if(depth == initDepth)
        	System.out.print("has before make: 0x" + Long.toHexString(board.hash) + "     ");

            if(MoveGen.makeMove(board, moveList.list[i])) {
            	
//            	System.out.print("has after make: 0x" + Long.toHexString(board.hash) + "     ");

                res = testareDepthNou(board, depth - 1, initDepth);

//                board.printCrtState();
                MoveGen.unMakeMove(board, moveList.list[i]);
                
                
                //nr++;
                
                nr += res;
                if(depth == initDepth){
                    System.out.print(MoveGen.letters[MoveGen.fileOf[moveList.list[i].from]] + ""
                                       + MoveGen.numbers[MoveGen.rankOf[moveList.list[i].from]] + ""
                                       + MoveGen.letters[MoveGen.fileOf[moveList.list[i].to]] + ""
                                       + MoveGen.numbers[MoveGen.rankOf[moveList.list[i].to]] + ""
                                       + board.pieceChar[moveList.list[i].promotion] + "  ");
					System.out.println(res  + "   hash after unmake: 0x" + Long.toHexString(board.hash));
                }
            }
        }

        return nr ;
    }
   
    public static void testAttackMoveGen() {
    		
    	Board board = new Board();
    	MoveGen mGen = new MoveGen();
    	
    	String FEN = "r3k2r/p1ppqpb1/bn2P1p1/4N3/1p2P1n1/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 1 2";
    	
    	board.initFEN(FEN);
    	
    	MoveList list = new MoveList();
    	
    	MoveGen.genAllAttackMoves(board, list);
    	
    	System.out.println("am gasit " + list.size + " mutari de atack doar");
    	
    	System.out.println("Acestea sunt:");
    	String moveString = "";
    	Move move;
    	for(int i = 0; i < list.size; i++) {
    		moveString = "";
    		move = list.list[i];
    		moveString += MoveGen.letters[MoveGen.fileOf[move.from]] + ""
                    + MoveGen.numbers[MoveGen.rankOf[move.from]] + ""
                    + MoveGen.letters[MoveGen.fileOf[move.to]] + ""
                    + MoveGen.numbers[MoveGen.rankOf[move.to]] + ""
                    + (move.promotion == PIECE.EMPTY.value ? "" : board.pieceChar[move.promotion] );
    		System.out.println(moveString);
    	}
    	
    	
    	
    	
    	
    }
	
    public static void testPVNodesSearch() {
    	
    	Board board = new Board();
    	MoveGen mGen = new MoveGen();
    	
    	String FEN = "8/8/8/p7/P6p/K1n4P/1pk5/8 b - - 3 57";
    	
    	board.initFEN(FEN);
    	
    	board.printCrtState();
    	
    	MoveList list = new MoveList();
    	
    	int depth = 6;
    	PVLine pline = new PVLine();
    	
    	SearchInfo info = new SearchInfo(0);
    	
    	Search search = new Search(info);
    	
    	
    	Search.search(board);
    	System.out.println(info.nullPruneTries);
    	
    }
    
    public static void newEvaluation() {
    	   Board board = new Board();
           MoveGen mg = new MoveGen();

           String FEN = "5k2/2Q5/5K2/8/8/P7/P5P1/8 w - - 11 55";

           board.initFEN(FEN);
           board.printCrtState();
           board.evaluate();
           
    }
    
    public static void testMobilityScore() {
    	

    	Board board = new Board();
    	MoveGen mGen = new MoveGen();
    	
    	String FEN = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
    	
    	board.initFEN(trickyFEN);
    	
    	board.printCrtState();
    	
    	Score score = MoveGen.getMobilityBonus(board);
    	
    	System.out.println("mg: " + score.mg + " eg: " + score.eg);
    	
    	int totalEval = board.evaluate();
    	
    	System.out.println("evaluated at: " + totalEval + " from white's perspective");
    	
    }
     
    
    
}




// D3
// 97862 <-- corect
// 97819 <-- al meu

// D4
// 4085603 <-- corect
// 4083736 <-- al meu