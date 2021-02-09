import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class MoveGen {

    // in astea retin rankul/ file- ul unui sqare
    // de ex fileOf[A2] = FILE_A  rankOf[A2] = RANK_2
    // de ex fileOf[B7] = FILE_7  rankOf[B7] = RANK_&
    static final int[] rankOf;
    static final int[] fileOf;
    static final int[] sideOf = {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2};
    static final int[] multy = {1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0};
    static final int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8};
    static final int[] knightDir = {-21, -19, -12, -8, 8, 12, 19, 21};
    static final int[] kingDir = {-11, -10, -9, -1, 1, 9, 10, 11};
    static final int[] rookDir = {-10, -1, 1, 10};
    static final int[] bishopDir = {-11, -9, 9, 11};
    static final char[] letters = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    static final int[][] pieceSide = {
    		{0, 1, 2, 3, 4, 5},
    		{6, 7, 8, 9, 10, 11} 
    };
    
    // piesa directe
    static final int[][] pieceDir = {
    		{0, 0, 0, 0, 0, 0, 0, 0},
    		{-21, -19, -12, -8, 8, 12, 19, 21},
    		{-11, -9, 9, 11, 0, 0, 0, 0},
    		{-10, -1, 1, 10, 0, 0, 0, 0},
    		{-11, -10, -9, -1, 1, 9, 10, 11},
    		{-11, -10, -9, -1, 1, 9, 10, 11},
    		{0, 0, 0, 0, 0, 0, 0, 0}
    };
    
    static final int[] pieceDirNumber = { 0, 8, 4, 4, 8, 8, 0};
    static final int[] pieceMaxMoves = {0, 1, 8, 8, 8, 1, 0};
    
    static final int[] forwardDir = {1, -1};
    static final int[] sideCastleMask = {C_PERM.WSC.value, C_PERM.BSC.value};
    			// first/last white/black
    static final int[][] pawnRank = {
    		{RANK.RANK_2.value, RANK.RANK_7.value},
    		{RANK.RANK_7.value, RANK.RANK_2.value}
    };
    static final int[] castleRights = {
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 14, 15, 15, 15, 12, 15, 15, 13, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 11, 15, 15, 15,  3, 15, 15,  7, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15,
            15, 15, 15, 15, 15, 15, 15, 15, 15, 15
    };
    static int[][] material = {
            {128, 781, 825, 1276, 2538, 32000, -128, -781, -825, -1276, -2538, -32000, 0},
            {213, 854, 915, 1380, 2682, 32000, -213, -854, -915, -1380, -2682, -32000, 0}
    };
    
    static final int[] phases_score = {0 ,1, 1, 2, 4, 0, 0 ,1, 1, 2, 4, 0, 0};
    static final int totatlPhase = 24;
    static final int[] captureScore = {100, 200, 300, 400, 500, 600, 100, 200, 300, 400, 500, 600, 0, 0};
    static final int[][] mvvlvaScores;
    
    static final int MATE_VALUE = 32000;
    
    // mobility[piece][no_of_moves];
    static final int[][] mobility_mg = {
    		// knight
    		{-70, -60, -20, -15, -5, 0, 10, 15, 25},
    		// bishop
    		{-50, -30, 5, 15, 25, 40, 45, 50, 50, 60, 70, 70, 80, 90},
    		// rook
    		{-70, -30, -10, -10, 0, 0, 10, 20, 30, 30, 35, 40, 50, 55, 60},
    		// queen
    		{-40, -25, -20, -20, 10, 20, 20, 35, 50, 60, 65, 65, 65, 65, 65, 70, 70, 73, 75, 75, 85, 105, 105, 105, 110, 110, 110, 115}
    };
    // mobility[piece][no_of_moves];
    static final int[][] mobility_eg = {
    		// knight
    		{-80, -50, -30, -15, 10, 15, 20, 25, 35},
    		// bishop
    		{-60, -20, 0, 10, 20, 40, 55, 60, 65, 70, 80, 85, 90, 100},
    		// rook
    		{-80, -20, 20, 40, 70, 90, 95, 110, 120, 130, 140, 150, 160, 165, 170},
    		// queen
    		{-30, -15, 0, 20, 40, 50, 60, 70, 80, 90, 90, 100, 120, 125, 125, 128, 130, 140, 140, 145, 145, 153, 155, 160, 170, 173, 180, 190}
    };
    
    static final int[][] pBonus_mg = {
            {  0,   0,   0,   0,  0,   0,   0,   0},
            {  3,   3,  10,  19, 16,  19,   7,  -5},
            { -9, -15,  11,  15, 32,  22,   5, -22},
            { -8, -23,   6,  20, 40,  17,   4, -12},
            { 13,   0, -13,   1, 11,  -2, -13,   5},
            { -5, -12,  -7,  22, -8,  -5, -15, -18},
            { -7,   7,  -3, -13,  5, -16,  10,  -8},
            {  0,   0,   0,   0,  0,   0,   0,   0}
    };
    
    static final int[][] pBonus_eg = {
            {   0,   0,   0,  0,   0,   0,   0,   0},
            { -10,  -6,  10,  0,  14,   7,  -5, -19},
            { -10, -10, -10,  4,   4,   3,  -6,  -4},
            {   6,  -2,  -8, -4, -13, -12, -10,  -9},
            {  12,   7,   6, -4,  -4,  -2,  19,  15},
            {  35,  25,  24, 32,  34,  12,  13,  19},
            {  15,  15,  17, 25,  29,  27,  17,  21},
            {   0,   0,   0,  0,   0,   0,   0,   0}
    };

    // piece rank file
    static final int[][][] bonus_mg = {
            {
                    {0, 0, 0, 0,},
                    {0, 0, 0, 0,},
                    {0, 0, 0, 0,},
                    {0, 0, 0, 0,},
                    {0, 0, 0, 0,},
                    {0, 0, 0, 0,},
                    {0, 0, 0, 0,},
                    {0, 0, 0, 0,},
            },
            { // knight
                    {-175, -92, -74, -75},
                    { -77, -41, -27, -15},
                    { -61, -17,   6,  12},
                    { -35,   8,  40,  49},
                    { -34,   8,  40,  49},
                    {  -9,  22,  58,  53},
                    { -67, -27,   4,  37},
                    {-201, -83, -56, -26}
            },
            { // bishop
                    {-53,  -5,  -8, -23},
                    {-15,   8,  19,   4},
                    { -7,  21,  -5,  17},
                    { -5,  11,  25,  39},
                    {-12,  29,  22,  31},
                    {-16,   6,   1,  11},
                    {-17, -14,   5,   0},
                    {-48,   1, -14, -23}
            },
            { // rook
                    {-31, -20, -14, -5},
                    {-21, -13,  -8,  6},
                    {-25, -11,  -1,  3},
                    {-13,  -5,  -4, -6},
                    {-27, -15,  -4,  3},
                    {-22,  -2,   6, 12},
                    { -2,  12,  16, 18},
                    {-17, -19,  -1,  9}
            },
            { // queen
                    { 3, -5, -5,  4},
                    {-3,  5,  8, 12},
                    {-3,  6, 13,  7},
                    { 4,  5,  9,  8},
                    { 0, 14, 12,  5},
                    {-4, 10,  6,  8},
                    {-5,  6, 10,  8},
                    {-2, -2,  1, -2}
            },
            { // king
                    {271, 327, 271, 198},
                    {278, 303, 234, 179},
                    {195, 258, 169, 120},
                    {164, 190, 138,  98},
                    {154, 179, 105,  70},
                    {123, 145,  81,  31},
                    { 88, 120,  65,  33},
                    { 59,  89,  45,  -1}
            },
    };
    
    static final int[][][] bonus_eg = {
            {
                {0, 0, 0, 0,},
                {0, 0, 0, 0,},
                {0, 0, 0, 0,},
                {0, 0, 0, 0,},
                {0, 0, 0, 0,},
                {0, 0, 0, 0,},
                {0, 0, 0, 0,},
                {0, 0, 0, 0,},
            },
            { // knight
            	{ -96, -65, -49, -21},
            	{ -67, -54, -18,   8},
            	{ -40, -27,  -8,  29},
            	{ -35,  -2,  13,  28},
            	{ -45, -16,   9,  39},
            	{ -51, -44, -16,  17},
            	{ -69, -50, -51,  12},
            	{-100, -88, -56, -17}
            },
            { // bishop
            	{-57, -30, -37, -12},
            	{-37, -13, -17,   1},
            	{-16,  -1,  -2,  10},
            	{-20,  -6,   0,  17},
            	{-17,  -1, -14,   1},
            	{-30,   6,   4,   6},
            	{-31, -20,  -1,   1},
            	{-46, -42, -37, -24}
            },
            { // rook
            	{ -9, -13, -10, -9},
            	{-12,  -9,  -1, -2},
            	{  6,  -8,  -2, -6},
            	{ -6,   1,  -9,  7},
            	{ -5,   8,   7, -6},
            	{  6,   1,  -7, 10},
            	{  4,   5,  20, -5},
            	{ 18,   0,  19, 13}
            },
            { // queen
            	{-69, -57, -47, -26},
            	{-55, -31, -22,  -4},
            	{-39, -18,  -9,   3},
            	{-23,  -3,  13,  24},
            	{-29,  -6,   9,  21},
            	{-38, -18, -12,   1},
            	{-50, -27, -24,  -8},
            	{-75, -52, -43, -36}
            },
            {
            	{  1,  45,  85,  75},
            	{ 53, 100, 133, 135},
            	{ 88, 130, 169, 175},
            	{103, 156, 172, 172},
            	{ 96, 166, 199, 199},
            	{ 92, 172, 184, 191},
            	{ 47, 121, 116, 131},
            	{ 11,  59,  73,  78}
            }
    };
    
    static final int[] fileMirror = {0, 1, 2, 3, 3, 2, 1, 0};

    // piece square
    static final int [][] psq_mg;
    static final int [][] psq_eg;
    static final int MAX_SIZE = 256;
    
    static final long[] hasheKey;
    static final int BLACK_TO_MOVE_OFFSET = 1440;
    static final int EN_OFFSET = BLACK_TO_MOVE_OFFSET + 1;
    static final int CASTLING_OFFSET = EN_OFFSET + 8;
    
    
    static {
        rankOf = new int[120];
        fileOf = new int[120];

        for (int i = 0; i < 120; i++) {
            rankOf[i] = RANK.RANK_NB.value;
            fileOf[i] = FILE.FILE_NB.value;
        }

        for (int file = FILE.FILE_A.value; file < FILE.FILE_NB.value; file++) {
            for (int rank = RANK.RANK_1.value; rank < RANK.RANK_NB.value; rank++) {
                rankOf[Board.fr2sq120(file, rank)] = rank;
                fileOf[Board.fr2sq120(file, rank)] = file;
            }
        }

        psq_mg = new int[13][120];
        psq_eg = new int[13][120];

        for(int file = FILE.FILE_A.value; file < FILE.FILE_NB.value; file++) {
            for(int rank = RANK.RANK_1.value; rank < RANK.RANK_NB.value; rank++) {
                for(int piece = PIECE.WP.value; piece <= PIECE.WK.value; piece++) {
                    // midgame
                	// piese albe
                    psq_mg[piece][Board.fr2sq120(file, rank)] = piece == PIECE.WP.value ?
                            pBonus_mg[rank][file] : bonus_mg[piece][rank][fileMirror[file]];
                                 
                    // adding some noise
                    if(new Random().nextBoolean()) {
                    	psq_mg[piece][Board.fr2sq120(file, rank)] += 1;
                    }
                    
                    // piese negre
                    psq_mg[piece + 6][Board.fr2sq120m(file, rank)] = -psq_mg[piece][Board.fr2sq120(file
                            , rank)];
                    
                    // endgame
                    // piese albe
                    psq_eg[piece][Board.fr2sq120(file, rank)] = piece == PIECE.WP.value ?
                            pBonus_eg[rank][file] : bonus_eg[piece][rank][fileMirror[file]];
                    
                    // adding some noise
                    if(new Random().nextBoolean()) {
                    	psq_eg[piece][Board.fr2sq120(file, rank)] += 2;
                    }        
                            
                    
                    // piese negre
                    psq_eg[piece + 6][Board.fr2sq120m(file, rank)] = -psq_eg[piece][Board.fr2sq120(file
                            , rank)];
                }
            }
        }

        // score empty space
        for(int i = 0; i < 120; i++){
            psq_mg[12][i] = 0;
            psq_eg[12][i] = 0;
        }
       
       mvvlvaScores = new int[12][12];
        
       for(int victim = 0; victim < PIECE.EMPTY.value; victim++) {
    	   for(int attacker = 0; attacker < PIECE.EMPTY.value; attacker++) {
    		   mvvlvaScores[victim][attacker] = captureScore[victim] + ((600 - captureScore[attacker])/ 100);
//    		   System.out.println(Board.pieceChar[attacker] + "x" + Board.pieceChar[victim] + ": " + mvvlvaScores[victim][attacker]);
    	   }
       }
       
       hasheKey = new long[1470];
       
       for(int i = 0; i < 1470; i++) {
    	   hasheKey[i] = new Random((i<<32 + 12356) % 1000000007).nextLong();
       }
    }

    public static boolean isAttacked(Board board, int square, int side) {
        for (int i = 0; i < 8; i++) {
            if (sideOf[board.extendedBoard[square + knightDir[i]]] == (side ^ 1) &&
                    ((board.extendedBoard[square + knightDir[i]] == PIECE.WN.value) ||
                            (board.extendedBoard[square + knightDir[i]] == PIECE.BN.value))) {
                return true;
            }
        }

        // for pentru rege
        for (int i = 0; i < 8; i++) {
            if (sideOf[board.extendedBoard[square + kingDir[i]]] == (side ^ 1) &&
                    ((board.extendedBoard[square + kingDir[i]] == PIECE.WK.value) ||
                            (board.extendedBoard[square + kingDir[i]] == PIECE.BK.value))) {
                return true;
            }
        }

        // for pentru bishop/ queen
        int sq;

        for (int i = 0; i < 4; i++) {
            for (int j = 1; j < 8; j++) {
                sq = square + j * bishopDir[i];
                if (sideOf[board.extendedBoard[sq]] == (side)) {
                    break;
                } else if (board.extendedBoard[sq] == PIECE.OFF_BOARD.value) {
                    break;
                } else if (sideOf[board.extendedBoard[sq]] == (side ^ 1)) {
                    if (((board.extendedBoard[sq] == PIECE.WB.value) ||
                            (board.extendedBoard[sq] == PIECE.WQ.value)) ||
                            ((board.extendedBoard[sq] == PIECE.BB.value) ||
                                    (board.extendedBoard[sq] == PIECE.BQ.value))) {
                        return true;
                    }
                    break;
                }
            }
            for (int j = 1; j < 8; j++) {
                sq = square + j * rookDir[i];
                if (sideOf[board.extendedBoard[sq]] == (side)) {
                    break;
                } else if (board.extendedBoard[sq] == PIECE.OFF_BOARD.value) {
                    break;
                } else if (sideOf[board.extendedBoard[sq]] == (side ^ 1)) {
                    if (((board.extendedBoard[sq] == PIECE.WR.value) ||
                            (board.extendedBoard[sq] == PIECE.WQ.value)) ||
                            ((board.extendedBoard[sq] == PIECE.BR.value) ||
                                    (board.extendedBoard[sq] == PIECE.BQ.value))) {
                        return true;
                    }
                    break;
                }
            }
        }

        // conditie pt pioni
        int forward = side == SIDE.WHITE.value ? 1 : -1;
        int pawn = side == SIDE.WHITE.value ? PIECE.BP.value : PIECE.WP.value;

        if (board.extendedBoard[square + forward * DIRECTION.NE.value] == pawn ||
                board.extendedBoard[square + forward * DIRECTION.NW.value] == pawn) {
            return true;
        }
        return false;
    }

    public static void addQuietMove(Board board, Move move, MoveList moveList) {
    	moveList.list[moveList.size] = move;
    	moveList.list[moveList.size].score = 0;
    	moveList.size++;
    }
    
    public static void addCaptureMove(Board board, Move move, MoveList moveList) {
    	moveList.list[moveList.size] = move;
    	moveList.list[moveList.size].score = mvvlvaScores[move.capture][board.extendedBoard[move.from]];
    	moveList.size++;
    }
    
    public static void addEnPassantMove(Board board, Move move, MoveList moveList) {
    	moveList.list[moveList.size] = move;
    	moveList.list[moveList.size].score = 105;
    	moveList.size++;
    }
    
    
    public static void genCastling(Board board, MoveList moveList) {
        Move newMove;

        
		if(board.extendedBoard[board.kingSq[0]] != PIECE.WK.value || board.extendedBoard[board.kingSq[1]] != PIECE.BK.value) {
			System.out.println("aici");
		}

        int side = board.side;
        int king;
        int sideMask = sideCastleMask[side];


        king = board.kingSq[side];
        
        
        if (((board.castlePerm & sideMask & C_PERM.KSC.value) != 0) &&
                board.extendedBoard[king + DIRECTION.EAST.value] == PIECE.EMPTY.value &&
                board.extendedBoard[king + 2 * DIRECTION.EAST.value] == PIECE.EMPTY.value) {
            // public Move(int from, int to, int capture, int promotion, int pawnStart, int castling, int enPas)
            if (!isAttacked(board, king, side) &&
                    !isAttacked(board, king + DIRECTION.EAST.value, side) &&
                    !isAttacked(board, king + 2 * DIRECTION.EAST.value, side)) {

                newMove = new Move(
                        king,
                        king + 2 * DIRECTION.EAST.value,
                        PIECE.EMPTY.value,
                        PIECE.EMPTY.value,
                        false,
                        (sideMask & C_PERM.KSC.value),
                        false
                );
                assert(moveList.size < MAX_SIZE);
                addQuietMove(board, newMove, moveList);
            }
        }

        if (((board.castlePerm & sideMask & C_PERM.QSC.value) != 0) &&
                board.extendedBoard[king + DIRECTION.WEST.value] == PIECE.EMPTY.value &&
                board.extendedBoard[king + 2 * DIRECTION.WEST.value] == PIECE.EMPTY.value &&
                board.extendedBoard[king + 3 * DIRECTION.WEST.value] == PIECE.EMPTY.value) {
            if ((!isAttacked(board, king, side)) &&
                    (!isAttacked(board, king + DIRECTION.WEST.value, side)) &&
                    (!isAttacked(board, king + 2 * DIRECTION.WEST.value, side))) {
                // public Move(int from, int to, int capture, int promotion, int pawnStart, int castling, int enPas)
                newMove = new Move(
                        king,
                        king + 2 * DIRECTION.WEST.value,
                        PIECE.EMPTY.value,
                        PIECE.EMPTY.value,
                        false,
                        (sideMask & C_PERM.QSC.value),
                        false
                );
                assert(moveList.size < MAX_SIZE);
                addQuietMove(board, newMove, moveList);
            }
        }
    }
    
    // moveList has to be pre-allocated
    public static void genPawnMove(Board board, MoveList moveList) {
        Move newMove;
        
        
        int side = board.side;
        int piece = pieceSide[side][PIECE.WP.value];
        int pawn;
        int forward = forwardDir[side];
        int firstRank = pawnRank[0][side];
        int lastRank = pawnRank[1][side];

        for (int i = 0; i < board.piecesNum[piece]; i++) {
            pawn = board.pList[piece][i];
            // 2 in fata la alb si negru
            if (rankOf[pawn] == firstRank &&
                    board.extendedBoard[pawn + (forward) * DIRECTION.NORTH.value] == PIECE.EMPTY.value &&
                    board.extendedBoard[pawn + (forward) * 2 * DIRECTION.NORTH.value] == PIECE.EMPTY.value) {
                // public Move(int from, int to, int capture, int promotion, int pawnStart, int castling, int enPas)
                newMove = new Move(
                        pawn,
                        pawn + (forward) * 2 * DIRECTION.NORTH.value,
                        PIECE.EMPTY.value,
                        PIECE.EMPTY.value,
                        true,
                        0,
                        false
                );
                assert(moveList.size < MAX_SIZE);
                addQuietMove(board, newMove, moveList);
            }

            // 1 in fata care ar trb sa mearga si pt alb si pentru negru
            if (board.extendedBoard[pawn + (forward) * DIRECTION.NORTH.value] == PIECE.EMPTY.value) {
                if (rankOf[pawn] != lastRank) {
                    newMove = new Move(
                            pawn,
                            pawn + (forward) * DIRECTION.NORTH.value,
                            PIECE.EMPTY.value,
                            PIECE.EMPTY.value,
                            false,
                            0,
                            false
                    );
                    assert(moveList.size < MAX_SIZE);
                    addQuietMove(board, newMove, moveList);
                } else {
                    for (int j = 0; j < 4; j++) {
                        // public Move(int from, int to, int capture, int promotion, int pawnStart, int castling, int enPas)
                        newMove = new Move(
                                pawn,
                                pawn + (forward) * DIRECTION.NORTH.value,
                                PIECE.EMPTY.value,
                                side * 6 + j + 1,
                                false,
                                0,
                                false
                        );
                        assert(moveList.size < MAX_SIZE);
                        addQuietMove(board, newMove, moveList);
                    }
                }
            }

            // atac dreapta
            // 1 ^ 1 = 0     WHITE ^ 1 => BLACK
            // 0 ^ 1 = 1     BLACK ^ 1 => WHITE
            if (sideOf[board.extendedBoard[pawn + (forward) * DIRECTION.NE.value]] == (side ^ 1)) {
                if (rankOf[pawn] != lastRank) {
                    // public Move(int from, int to, int capture, int promotion, int pawnStart, int castling, int enPas)
                    newMove = new Move(
                            pawn,
                            pawn + (forward) * DIRECTION.NE.value,
                            board.extendedBoard[pawn + (forward) * DIRECTION.NE.value],
                            PIECE.EMPTY.value,
                            false,
                            0,
                            false
                    );
                    assert(moveList.size < MAX_SIZE);
                    addCaptureMove(board, newMove, moveList);
                } else {
                    for (int j = 0; j < 4; j++) {
                        // public Move(int from, int to, int capture, int promotion, int pawnStart, int castling, int enPas)
                        newMove = new Move(
                                pawn,
                                pawn + (forward) * DIRECTION.NE.value,
                                board.extendedBoard[pawn + (forward) * DIRECTION.NE.value],
                                side * 6 + j + 1,
                                false,
                                0,
                                false
                        );
                        assert(moveList.size < MAX_SIZE);
                        addCaptureMove(board, newMove, moveList);
                    }
                }
            }

            // atac stanga
            // 1 ^ 1 = 0     WHITE ^ 1 => BLACK
            // 0 ^ 1 = 1     BLACK ^ 1 => WHITE
            if (sideOf[board.extendedBoard[pawn + (forward) * DIRECTION.NW.value]] == (side ^ 1)) {
                if (rankOf[pawn] != lastRank) {
                    // public Move(int from, int to, int capture, int promotion, int pawnStart, int castling, int enPas)
                    newMove = new Move(
                            pawn,
                            pawn + (forward) * DIRECTION.NW.value,
                            board.extendedBoard[pawn + (forward) * DIRECTION.NW.value],
                            PIECE.EMPTY.value,
                            false,
                            0,
                            false
                    );
                    assert(moveList.size < MAX_SIZE);
                    addCaptureMove(board, newMove, moveList);
                } else {
                    for (int j = 0; j < 4; j++) {
                        newMove = new Move(
                                pawn,
                                pawn + (forward) * DIRECTION.NW.value,
                                board.extendedBoard[pawn + (forward) * DIRECTION.NW.value],
                                side * 6 + j + 1,
                                false,
                                0,
                                false
                        );
                        assert(moveList.size < MAX_SIZE);
                        addCaptureMove(board, newMove, moveList);
                    }
                }
            }

            // 1 in fata -- done
            // atac stanga  --- done
            // atac dreapta  --- deon
            // enPas  --- mai am
//            System.out.println((pawn + DIRECTION.EAST.value) + "~~~" + (pawn + DIRECTION.WEST.value) + "!=" + board.enPas);
            if ((board.enPas != SQUARE.NO_SQ.value) &&
                (pawn + forward * DIRECTION.NE.value == board.enPas ||
                            pawn + forward * DIRECTION.NW.value == board.enPas)) {
                // public Move(int from, int to, int capture, int promotion, int pawnStart, int castling, int enPas)
//                System.out.println("GASIT EN PASSANT");
                newMove = new Move(
                        pawn,
                        board.enPas,
                        board.extendedBoard[board.enPas + forward * DIRECTION.SOUTH.value],
                        PIECE.EMPTY.value,
                        false,
                        0,
                        true
                );
                assert(moveList.size < MAX_SIZE);
                addEnPassantMove(board, newMove, moveList);
            }
        }
    }
    
    // moveList has to be pre-allocated
    public static void genAllMoves(Board board, MoveList moveList) {
    	Move newMove;
    	
    	int side = board.side;
    	int piecePos;
    	int piece;
    	int offset = side * 6;
    	
    	// what a piece of art
    	for(int i = 1; i < 6; i++) {
    		piece = i + offset;
    		for(int j = 0; j < board.piecesNum[piece]; j++) {
    			piecePos = board.pList[piece][j];
    			for (int m = 0; m < pieceDirNumber[i]; m++) {
                    for (int n = 1; n <= pieceMaxMoves[i]; n++) {
                        if (board.extendedBoard[piecePos + pieceDir[i][m] * n] == PIECE.EMPTY.value) {
                            newMove = new Move(
                            		piecePos,
                            		piecePos + pieceDir[i][m] * n,
                                    PIECE.EMPTY.value,
                                    PIECE.EMPTY.value,
                                    false,
                                    0,
                                    false
                            );
                            assert(moveList.size < MAX_SIZE);
                            addQuietMove(board, newMove, moveList);
                        } else if (sideOf[board.extendedBoard[piecePos + pieceDir[i][m] * n]] == (side)) {
                            break;
                        } else if (sideOf[board.extendedBoard[piecePos + pieceDir[i][m] * n]] == (side ^ 1)) {
                            newMove = new Move(
                            		piecePos,
                            		piecePos + pieceDir[i][m] * n,
                                    board.extendedBoard[piecePos + pieceDir[i][m] * n],
                                    PIECE.EMPTY.value,
                                    false,
                                    0,
                                    false
                            );
                            assert(moveList.size < MAX_SIZE);
                            addCaptureMove(board, newMove, moveList);
                            break;
                        } else {
                            break;
                        }
                    }
                }
    		}
    	}
    	
    	
    	genPawnMove(board, moveList);
    	genCastling(board, moveList);
    	
    }
    
    // moveList has to be pre-allocated
    public static void genPawnAttackMove(Board board, MoveList moveList) {
        Move newMove;
        
        
        int side = board.side;
        int piece = pieceSide[side][PIECE.WP.value];
        int pawn;
        int forward = forwardDir[side];
        int firstRank = pawnRank[0][side];
        int lastRank = pawnRank[1][side];

        for (int i = 0; i < board.piecesNum[piece]; i++) {
            pawn = board.pList[piece][i];

            // atac dreapta
            // 1 ^ 1 = 0     WHITE ^ 1 => BLACK
            // 0 ^ 1 = 1     BLACK ^ 1 => WHITE
            if (sideOf[board.extendedBoard[pawn + (forward) * DIRECTION.NE.value]] == (side ^ 1)) {
                if (rankOf[pawn] != lastRank) {
                    // public Move(int from, int to, int capture, int promotion, int pawnStart, int castling, int enPas)
                    newMove = new Move(
                            pawn,
                            pawn + (forward) * DIRECTION.NE.value,
                            board.extendedBoard[pawn + (forward) * DIRECTION.NE.value],
                            PIECE.EMPTY.value,
                            false,
                            0,
                            false
                    );
                    assert(moveList.size < MAX_SIZE);
                    addCaptureMove(board, newMove, moveList);
                } else {
                    for (int j = 0; j < 4; j++) {
                        // public Move(int from, int to, int capture, int promotion, int pawnStart, int castling, int enPas)
                        newMove = new Move(
                                pawn,
                                pawn + (forward) * DIRECTION.NE.value,
                                board.extendedBoard[pawn + (forward) * DIRECTION.NE.value],
                                side * 6 + j + 1,
                                false,
                                0,
                                false
                        );
                        assert(moveList.size < MAX_SIZE);
                        addCaptureMove(board, newMove, moveList);
                    }
                }
            }

            // atac stanga
            // 1 ^ 1 = 0     WHITE ^ 1 => BLACK
            // 0 ^ 1 = 1     BLACK ^ 1 => WHITE
            if (sideOf[board.extendedBoard[pawn + (forward) * DIRECTION.NW.value]] == (side ^ 1)) {
                if (rankOf[pawn] != lastRank) {
                    // public Move(int from, int to, int capture, int promotion, int pawnStart, int castling, int enPas)
                    newMove = new Move(
                            pawn,
                            pawn + (forward) * DIRECTION.NW.value,
                            board.extendedBoard[pawn + (forward) * DIRECTION.NW.value],
                            PIECE.EMPTY.value,
                            false,
                            0,
                            false
                    );
                    assert(moveList.size < MAX_SIZE);
                    addCaptureMove(board, newMove, moveList);
                } else {
                    for (int j = 0; j < 4; j++) {
                        newMove = new Move(
                                pawn,
                                pawn + (forward) * DIRECTION.NW.value,
                                board.extendedBoard[pawn + (forward) * DIRECTION.NW.value],
                                side * 6 + j + 1,
                                false,
                                0,
                                false
                        );
                        assert(moveList.size < MAX_SIZE);
                        addCaptureMove(board, newMove, moveList);
                    }
                }
            }

            // 1 in fata -- done
            // atac stanga  --- done
            // atac dreapta  --- deon
            // enPas  --- mai am
            if ((board.enPas != SQUARE.NO_SQ.value) &&
                (pawn + forward * DIRECTION.NE.value == board.enPas ||
                            pawn + forward * DIRECTION.NW.value == board.enPas)) {
                // public Move(int from, int to, int capture, int promotion, int pawnStart, int castling, int enPas)
                newMove = new Move(
                        pawn,
                        board.enPas,
                        board.extendedBoard[board.enPas + forward * DIRECTION.SOUTH.value],
                        PIECE.EMPTY.value,
                        false,
                        0,
                        true
                );
                assert(moveList.size < MAX_SIZE);
                addEnPassantMove(board, newMove, moveList);
            }
        }
    }
    
    
    // moveList has to be pre-allocated
    public static void genAllAttackMoves(Board board, MoveList moveList) {
    	Move newMove;
    	
    	int side = board.side;
    	int piecePos;
    	int piece;
    	int offset = side * 6;
    	
    	for(int i = 1; i < 6; i++) {
    		piece = i + offset;
    		for(int j = 0; j < board.piecesNum[piece]; j++) {
    			piecePos = board.pList[piece][j];
    			for (int m = 0; m < pieceDirNumber[i]; m++) {
                    for (int n = 1; n <= pieceMaxMoves[i]; n++) {
                        if (board.extendedBoard[piecePos + pieceDir[i][m] * n] == PIECE.EMPTY.value) {
                        	// nothing
                        } else if (sideOf[board.extendedBoard[piecePos + pieceDir[i][m] * n]] == (side ^ 1)) {
                            newMove = new Move(
                            		piecePos,
                            		piecePos + pieceDir[i][m] * n,
                                    board.extendedBoard[piecePos + pieceDir[i][m] * n],
                                    PIECE.EMPTY.value,
                                    false,
                                    0,
                                    false
                            );
                            assert(moveList.size < MAX_SIZE);
                            addCaptureMove(board, newMove, moveList);
                            break;
                        } else {
                            break;
                        }
                    }
                }
    		}
    	}
    	genPawnAttackMove(board, moveList);    	
    }
    
    // moveList has to be pre-allocated
    public static void genPawnNonAttackMove(Board board, MoveList moveList) {
        Move newMove;
        
        
        int side = board.side;
        int piece = pieceSide[side][PIECE.WP.value];
        int pawn;
        int forward = forwardDir[side];
        int firstRank = pawnRank[0][side];
        int lastRank = pawnRank[1][side];

        for (int i = 0; i < board.piecesNum[piece]; i++) {
            pawn = board.pList[piece][i];
            // 2 in fata la alb si negru
            if (rankOf[pawn] == firstRank &&
                    board.extendedBoard[pawn + (forward) * DIRECTION.NORTH.value] == PIECE.EMPTY.value &&
                    board.extendedBoard[pawn + (forward) * 2 * DIRECTION.NORTH.value] == PIECE.EMPTY.value) {
                newMove = new Move(
                        pawn,
                        pawn + (forward) * 2 * DIRECTION.NORTH.value,
                        PIECE.EMPTY.value,
                        PIECE.EMPTY.value,
                        true,
                        0,
                        false
                );
                assert(moveList.size < MAX_SIZE);
                addQuietMove(board, newMove, moveList);
            }

            // 1 in fata care ar trb sa mearga si pt alb si pentru negru
            if (board.extendedBoard[pawn + (forward) * DIRECTION.NORTH.value] == PIECE.EMPTY.value) {
                if (rankOf[pawn] != lastRank) {
                    newMove = new Move(
                            pawn,
                            pawn + (forward) * DIRECTION.NORTH.value,
                            PIECE.EMPTY.value,
                            PIECE.EMPTY.value,
                            false,
                            0,
                            false
                    );
                    assert(moveList.size < MAX_SIZE);
                    addQuietMove(board, newMove, moveList);
                } else {
                    for (int j = 0; j < 4; j++) {
                        newMove = new Move(
                                pawn,
                                pawn + (forward) * DIRECTION.NORTH.value,
                                PIECE.EMPTY.value,
                                side * 6 + j + 1,
                                false,
                                0,
                                false
                        );
                        assert(moveList.size < MAX_SIZE);
                        addQuietMove(board, newMove, moveList);
                    }
                }
            }
        }
    }
    
   
    // moveList has to be pre-allocated
    public static void genAllNonAttackMoves(Board board, MoveList moveList) {
    	Move newMove;
    	
    	int side = board.side;
    	int piecePos;
    	int piece;
    	int offset = side * 6;
    	
    	for(int i = 1; i < 6; i++) {
    		piece = i + offset;
    		for(int j = 0; j < board.piecesNum[piece]; j++) {
    			piecePos = board.pList[piece][j];
    			for (int m = 0; m < pieceDirNumber[i]; m++) {
                    for (int n = 1; n <= pieceMaxMoves[i]; n++) {
                        if (board.extendedBoard[piecePos + pieceDir[i][m] * n] == PIECE.EMPTY.value) {
                            newMove = new Move(
                            		piecePos,
                            		piecePos + pieceDir[i][m] * n,
                                    PIECE.EMPTY.value,
                                    PIECE.EMPTY.value,
                                    false,
                                    0,
                                    false
                            );
                            assert(moveList.size < MAX_SIZE);
                            addQuietMove(board, newMove, moveList);
                        } else {
                            break;
                        }
                    }
                }
    		}
    	}
    	
    	
    	genPawnNonAttackMove(board, moveList);
    	genCastling(board, moveList);
    	
    }
    
    // returneaza true daca mutarea este legala (tot ce mai trebuie verifica, de fapt,
    // este daca regele partii care face mutareea intra in sah ca rez al efectuarii mutarii
    // deoarece restul legalitatilor este esigurat de generarea de mutari)
    // daca returneaza false (i.e. mutare ilegala)
    // BOARD TREBUIE SA RAMANA NEMODIFICAT!!!!

    public static boolean makeMove(Board board, Move move) {

        board.history[board.hisPly].castelPerm = board.castlePerm;
        board.history[board.hisPly].fifty      = board.fifty;
        board.history[board.hisPly].enPas      = board.enPas;
        board.history[board.hisPly].hash       = board.hash;
        board.history[board.hisPly].eval_mg    = board.eval_mg;
        board.history[board.hisPly].eval_eg    = board.eval_eg;
        
        board.fifty++;

        int fromPiece = board.extendedBoard[move.from];
        int toPiece = board.extendedBoard[move.to];
        
        assert(board.extendedBoard[board.kingSq[0]] == PIECE.WK.value && board.extendedBoard[board.kingSq[1]] == PIECE.BK.value);

        assert(fromPiece != PIECE.EMPTY.value);

        int queenSideMask = C_PERM.WQC.value | C_PERM.BQC.value;
        int kingSideMask = C_PERM.WKC.value | C_PERM.BKC.value;

        int from = move.from;
        int to = move.to;
        
        
        // hash out from piece
        board.hash ^= hasheKey[120 * fromPiece + from];
        // hash in from piece
        board.hash ^= hasheKey[120 * fromPiece + to];

        assert(from != to);

        int capturedPiece = move.capture;

        // mutam piesa de la from la to
        board.extendedBoard[to] = fromPiece;
        board.extendedBoard[from] = PIECE.EMPTY.value;

        int side = board.side;

        // assertul daca este fals pica -->> adica da eroare si se termina de rulat programul
        assert (side != SIDE.BOTH.value);

        int forward = side == SIDE.WHITE.value ? 1 : -1;

        int index = -1;

        // daca este mutat chiar regele atunci trebuie modificata casuta aferenta lui
        if (from == board.kingSq[side]) {
            board.kingSq[side] = to;
        }

        int beforeEnPas = board.extendedBoard[to + forward * DIRECTION.SOUTH.value];

        // daca am atacat cu prin en passant
        // scot pionul de pe extended board
        if(move.enPas) {
            board.extendedBoard[to + forward * DIRECTION.SOUTH.value] = PIECE.EMPTY.value;
        }

        // acu verific daca bossul este atacat
        // adica daca regele celui care o mutat

        // daca este atatcat fac undo la ce a facut pana acum si apoi returnez false xd
        if(isAttacked(board, board.kingSq[side], side)) {
            board.extendedBoard[to] = toPiece;
            board.extendedBoard[from] = fromPiece;

            // daca am putat chair regele ii schimb board.kingSq
            if(board.kingSq[side] == to){
                board.kingSq[side] = from;
                board.hash = board.history[board.hisPly].hash;
                return false;
            }

            // daca cumva era atac prin en passant tre sa bag pionul la loc
            if(move.enPas) {
                board.extendedBoard[to + forward * DIRECTION.SOUTH.value] = beforeEnPas;
            }
            board.hash = board.history[board.hisPly].hash;
            return false;
            // aici cred ca am cam terminat cu undo if illegal move
        }

        // de aici pur si simplu fac ce ar trb sa fac de fapt

        // aici actualizez pList (scot baiatul eliminat)
        int offset = 0;
        if (move.capture != PIECE.EMPTY.value) {
        	board.fifty = 0;
            if(move.enPas) {
                offset = forward * DIRECTION.SOUTH.value;
                // hash out capture en passant pawn
                board.hash ^= hasheKey[120 * capturedPiece + to + offset];
            } else {
            	// hash out capture piece
            	board.hash ^= hasheKey[120 * capturedPiece + to];
            }

            for (int i = 0; i < board.piecesNum[capturedPiece]; i++) {
               if(board.pList[capturedPiece][i] == (to + offset)){
                    index = i;
                    break;
               }

            }
            // luam ultima piesa si o punem in locul aleia pe care am scos-o din boad.extendedBoard;
            // si scadem si nr de piese din pList;
            assert (index != -1);
            board.pList[capturedPiece][index] = board.pList[capturedPiece][board.piecesNum[capturedPiece] - 1];
            board.piecesNum[capturedPiece]--;
        }
        else {
            // aici inseamna ca n-a fost realizata nicio captura
            // am putat din to in from

            if (move.castling != 0) {
                // aici inseamna ca am facut castling
                if ((move.castling & queenSideMask) != 0) {
                    // tre sa actualizez si in plist pozitiile turelor
                    int rook = board.extendedBoard[to + 2 * DIRECTION.WEST.value];
                    for(int i = 0; i < board.piecesNum[rook]; i++) {
                        if(board.pList[rook][i] == (to + 2 * DIRECTION.WEST.value)) {
                            board.pList[rook][i] = to + DIRECTION.EAST.value;
                            break;
                        }
                    }
                    
                    board.extendedBoard[to + DIRECTION.EAST.value] = board.extendedBoard[to + 2 * DIRECTION.WEST.value];
                    board.extendedBoard[to + 2 * DIRECTION.WEST.value] = PIECE.EMPTY.value;          

                    board.eval_mg -= psq_mg[rook][to + 2 * DIRECTION.WEST.value];
                    board.eval_mg += psq_mg[rook][to + DIRECTION.EAST.value];
                    
                    board.eval_eg -= psq_eg[rook][to + 2 * DIRECTION.WEST.value];
                    board.eval_eg += psq_eg[rook][to + DIRECTION.EAST.value];
                    
                    // hash out old, hash in new
                     board.hash ^= hasheKey[rook * 120 + to + 2 * DIRECTION.WEST.value];
                     board.hash ^= hasheKey[rook * 120 + to + DIRECTION.EAST.value];

                } else if ((move.castling & kingSideMask) != 0) {
                    // tre sa actualizez si in plist pozitiile turelor
                    int rook = board.extendedBoard[to + DIRECTION.EAST.value];
                    for(int i = 0; i < board.piecesNum[rook]; i++) {
                        if(board.pList[rook][i] == (to + DIRECTION.EAST.value)) {
                            board.pList[rook][i] = to + DIRECTION.WEST.value;
                            break;
                        }
                    }

                    board.extendedBoard[to + DIRECTION.WEST.value] = board.extendedBoard[to + DIRECTION.EAST.value];
                    board.extendedBoard[to + DIRECTION.EAST.value] = PIECE.EMPTY.value;

                    board.eval_mg -= psq_mg[rook][to + DIRECTION.EAST.value];
                    board.eval_mg += psq_mg[rook][to + DIRECTION.WEST.value];
                    
                    board.eval_eg -= psq_eg[rook][to + DIRECTION.EAST.value];
                    board.eval_eg += psq_eg[rook][to + DIRECTION.WEST.value];
                    
                    // hash out old, hash in new
                     board.hash ^= hasheKey[rook * 120 + to + DIRECTION.EAST.value];
                     board.hash ^= hasheKey[rook * 120 + to + DIRECTION.WEST.value];
                } else {
                    assert (1 == 0);
                }
                
            } else {
                // aici inseamna ca n-a fost realizata nicio captura
                // am putat din to in from
                // nu am avut nicio captura, nici castling, inseamna ca
            	if(fromPiece == PIECE.WP.value ||
            		fromPiece == PIECE.BP.value) {
            		board.fifty = 0;
            	}
            }

        }

        if (side == SIDE.BLACK.value) {
            board.ply++;
        }
        
        if(board.enPas != SQUARE.NO_SQ.value) {
        	// we have to hash out the hash for the file of en passant
        	board.hash ^= hasheKey[EN_OFFSET + fileOf[board.enPas]];
        }
        
        // setez/ resetez fieldul pt en passant din board
        if(move.pawnStart) {
        	// hash in en passant file
        	board.hash ^= hasheKey[EN_OFFSET + fileOf[from]];
            board.enPas = to + forward * DIRECTION.SOUTH.value;
        } else {
            board.enPas = SQUARE.NO_SQ.value;
        }

        // modific square-ul asociat piesei mutate din
        index = -1;
        for(int i = 0; i < board.piecesNum[fromPiece]; i++){
            if(board.pList[fromPiece][i] == from) {
                index = i;
                break;
            }
        }

        assert(index != -1);
        board.pList[fromPiece][index] = to;

        // daca am facut promotion
        if(move.promotion != PIECE.EMPTY.value) {
            // 1) scot pionul din lista plist
            // 2) adaug piesa noua
        	board.fifty = 0;
        	
        	// hash out alreade hashed pawn
        	board.hash ^= hasheKey[120 * fromPiece + to];
        	
        	// has in new promoted piece
        	board.hash ^= hasheKey[120 * move.promotion + to];

            // 1)
             for (int i = 0; i < board.piecesNum[fromPiece]; i++) {
                if(board.pList[fromPiece][i] == to){
                    index = i;
                    break;
                }
            }
            // luam ultima piesa si o punem in locul aleia pe care am scos-o din boad.extendedBoard;
            // si scadem si nr de piese din pList;
            assert (index != -1);
            board.pList[fromPiece][index] = board.pList[fromPiece][board.piecesNum[fromPiece] - 1];
            board.piecesNum[fromPiece]--;

            // 2) adaug piesa noua
            board.pList[move.promotion][board.piecesNum[move.promotion]] = to;
            board.piecesNum[move.promotion]++;

            // actualizez board.extendedBoard cu piesa promovata
            board.extendedBoard[to] = move.promotion;

        }

        board.eval_mg -= material[0][move.capture];
        board.eval_mg += material[0][move.promotion];
        
        board.eval_eg -= material[1][move.capture];
        board.eval_eg += material[1][move.promotion];

        board.eval_mg -= psq_mg[fromPiece][from];
        board.eval_mg += psq_mg[fromPiece][to];
        
        board.eval_eg -= psq_eg[fromPiece][from];
        board.eval_eg += psq_eg[fromPiece][to];

        // chiar daca e promition si pionul se duce pe rankul 1/8  psq[pawn][ultim_rank] = 0;
        // chiar daca nu e promotion se scade/ adauga 0
        board.eval_mg += psq_mg[move.promotion][to];
        
        board.eval_eg += psq_eg[move.promotion][to];

        // la en passant e mai sanky
        if(move.enPas) {
            board.eval_mg -= psq_mg[capturedPiece][board.history[board.hisPly].enPas + forward * DIRECTION.SOUTH.value];
            board.eval_eg -= psq_eg[capturedPiece][board.history[board.hisPly].enPas + forward * DIRECTION.SOUTH.value];
        } else {
        	board.eval_mg -= psq_mg[capturedPiece][to];
        	board.eval_eg -= psq_eg[capturedPiece][to];
            
        }
        
        // hash out the old castle perm
    	board.hash ^= hasheKey[CASTLING_OFFSET + board.castlePerm];
    	// hash black to move (this is just toggling)
    	board.hash ^= hasheKey[BLACK_TO_MOVE_OFFSET];

        board.side = board.side ^ 1;
        // actualizam castle permissions
        board.castlePerm &= castleRights[from];
        board.hisPly++;
        
        // hash in the new castle perm
    	board.hash ^= hasheKey[CASTLING_OFFSET + board.castlePerm];

//        System.out.println("end of makeMove");
        return true;
    }

    // procesul invers din makeMove
    // doar ca aici nu trebuie sa testam daca e atacat regel xdddd
    public static void unMakeMove(Board board, Move move) {

        board.castlePerm = board.history[board.hisPly - 1].castelPerm;
        board.fifty      = board.history[board.hisPly - 1].fifty;
        board.enPas      = board.history[board.hisPly - 1].enPas;
        board.hash       = board.history[board.hisPly - 1].hash;
        board.eval_mg    = board.history[board.hisPly - 1].eval_mg;
        board.eval_eg    = board.history[board.hisPly - 1].eval_eg;
        
        board.hisPly--;

        board.side ^= 1;

        int toPiece = board.extendedBoard[move.to];

        int queenSideMask = C_PERM.WQC.value | C_PERM.BQC.value;
        int kingSideMask = C_PERM.WKC.value | C_PERM.BKC.value;

        int from = move.from;
        int to = move.to;

        int side = board.side;

        if(side == SIDE.BLACK.value) {
            board.ply--;
        }

        // assertul daca este fals pica -->> adica da eroare si se termina de rulat programul
        assert (side != SIDE.BOTH.value);

        int forward = side == SIDE.WHITE.value ? 1 : -1;


        int index = -1;

        // undo la move
        // nu pot sa fac asta
        if(move.promotion == PIECE.EMPTY.value) {
            board.extendedBoard[from] = board.extendedBoard[to];
                       
            for(int i = 0; i < board.piecesNum[toPiece]; i++){
                if(board.pList[toPiece][i] == to){
                    board.pList[toPiece][i] = from;
                    break;
                }
            }
            // daca nu  a fost capturare de en passant
            if(!move.enPas) {
                board.extendedBoard[to] = move.capture;
            } else {
                // dar daca a fost clar bagam empty
                board.extendedBoard[to] = PIECE.EMPTY.value;
            }

            // daca insusi regele a fost mutat (cu/ fara captura)
            // atunci trb updatat board.kingSq[side]
            if(board.kingSq[side] == to) {
                board.kingSq[side] = from;
            }

            // asta inseamna ca trebuie sa bagam inapoi in pList pise scoasa
            // pe extended board e bagata inapoi de linia precedenta
            if(move.capture != PIECE.EMPTY.value) {
                // actualizez pozitia de una a fost stearsa

                // daca nu a fost capturaa cu en passant atunci e bine
                if (!move.enPas) {
                    board.pList[move.capture][board.piecesNum[move.capture]] = to;
                    board.piecesNum[move.capture]++;
                } else {
                    // dar daca a fost capturata cu en passant
                    // atunci e mai tricky pentru ca "to" acum e casuta de en passant,
                    // si nu de unde am scos pionul

                    // am schimbat side instant cum am intrat in fucntie
                    // deci gandesc cu side al cui o mutat

                    board.pList[move.capture][board.piecesNum[move.capture]] = to + forward * DIRECTION.SOUTH.value;
                    board.piecesNum[move.capture]++;
                    board.extendedBoard[to + forward * DIRECTION.SOUTH.value] = move.capture;
                }
            } else {
                // daca nu a fost promotion si nici captura inseamna ca poate sa fie castling de ex
                if(move.castling != 0) {
                    // daca a fost castling inseamna ca clar regele a fost mutat
                    // dar regelui i-am actualizat pozitia in board.kingSq[side] anterior
                    // trebuie doar sa mai mut tura si sa pun castling right inapoi

                    if ((move.castling & queenSideMask) != 0) {
                        // tre sa actualizez si in plist pozitiile turelor
                        int rook = board.extendedBoard[to + DIRECTION.EAST.value];
                        for(int i = 0; i < board.piecesNum[rook]; i++) {
                            if(board.pList[rook][i] == (to + DIRECTION.EAST.value)) {
                                board.pList[rook][i] = to + 2 * DIRECTION.WEST.value;
                                break;
                            }
                        }

                        // TODO: psq update la castling unmake
                        board.extendedBoard[to + DIRECTION.EAST.value] = PIECE.EMPTY.value;
                        board.extendedBoard[to + 2 * DIRECTION.WEST.value] = rook;
                    } else if ((move.castling & kingSideMask) != 0) {
                        // tre sa actualizez si in plist pozitiile turelor
                        int rook = board.extendedBoard[to + DIRECTION.WEST.value];
                        for(int i = 0; i < board.piecesNum[rook]; i++) {
                            if(board.pList[rook][i] == (to + DIRECTION.WEST.value)) {
                                board.pList[rook][i] = to + DIRECTION.EAST.value;
                                break;
                            }
                        }

                        board.extendedBoard[to + DIRECTION.WEST.value] = PIECE.EMPTY.value;
                        board.extendedBoard[to + DIRECTION.EAST.value] = rook;

                    }
                }
            }
        } else {
            // intai si intai bagam pionul la locul
            int pawn = side == SIDE.WHITE.value ? PIECE.WP.value : PIECE.BP.value;
            board.pList[pawn][board.piecesNum[pawn]] = from;
            board.piecesNum[pawn]++;

            board.extendedBoard[from] = pawn;

            int promoted = move.promotion;
            index = -1;
            for(int i = 0; i < board.piecesNum[promoted]; i++){
                if(board.pList[promoted][i] == to){
                    index = i;
                    break;
                }
            }
            assert(index != -1);

            board.pList[promoted][index] = board.pList[promoted][board.piecesNum[promoted] - 1];
            board.piecesNum[promoted]--;

            // daca avem promotion cu capture bagam capture ul la loc
            if(move.capture != PIECE.EMPTY.value) {
                int captured = move.capture;
                board.pList[captured][board.piecesNum[captured]] = to;
                board.piecesNum[captured]++;
                board.extendedBoard[to] = captured;
            } else {
                board.extendedBoard[to] = PIECE.EMPTY.value;
            }
        }
    }

  
    public static void makeNullMove(Board board) {
    	
    	board.history[board.hisPly].castelPerm = board.castlePerm;
        board.history[board.hisPly].fifty = board.fifty;
        board.history[board.hisPly].enPas = board.enPas;
        board.history[board.hisPly].hash = board.hash;
        board.history[board.hisPly].eval_mg = board.eval_mg;
        board.history[board.hisPly].eval_eg = board.eval_eg;
        
//        board.fifty++;
        board.side = board.side ^ 1;
        
    	// hash black to move (this is just toggling)
    	board.hash ^= hasheKey[BLACK_TO_MOVE_OFFSET];

        board.hisPly++;
    }
    
    
    public static void unMakeNullMove(Board board) {
        board.castlePerm = board.history[board.hisPly - 1].castelPerm;
        board.fifty = board.history[board.hisPly - 1].fifty;
        board.enPas = board.history[board.hisPly - 1].enPas;
        board.hash = board.history[board.hisPly - 1].hash;
        board.eval_mg = board.history[board.hisPly - 1].eval_mg;
        board.eval_eg = board.history[board.hisPly - 1].eval_eg;
        board.side = board.side ^ 1;
        board.hisPly--;
    	
    }
    
    
    public static Score getMobilityBonus(Board board) {
    	
    	int mg = 0;
    	int eg = 0;
    	int offset = 6;
    	for(int i = PIECE.WB.value; i <= PIECE.WQ.value; i++) {
    		
    	}
    	
    
    	int wside = SIDE.WHITE.value;
    	int bside = SIDE.BLACK.value;
    	int piecePos;
    	int piece;
    	
    	int nrMoves = 0;
    	
    	for(int i = PIECE.WN.value; i <= PIECE.WQ.value; i++) {
    		piece = i;
    		
    		for(int j = 0; j < board.piecesNum[piece]; j++) {
    			
    			nrMoves = 0;
    			
    			piecePos = board.pList[piece][j];
    			for (int m = 0; m < pieceDirNumber[i]; m++) {
                    for (int n = 1; n <= pieceMaxMoves[i]; n++) {
                        if (board.extendedBoard[piecePos + pieceDir[i][m] * n] == PIECE.EMPTY.value) {
//                            addQuietMove(board, newMove, moveList);
                        	nrMoves++;
                        } else if (sideOf[board.extendedBoard[piecePos + pieceDir[i][m] * n]] == (wside)) {
                            break;
                        } else if (sideOf[board.extendedBoard[piecePos + pieceDir[i][m] * n]] == (wside ^ 1)) {
//                            addCaptureMove(board, newMove, moveList);
                        	nrMoves++;
                            break;
                        } else {
                            break;
                        }
                    }
                }
    			// tot din prespectiva lui alb; adica pt alb adunam
    			 // mobility[piece][no_of_moves];
    			mg += mobility_mg[piece - 1][nrMoves];
    			eg += mobility_eg[piece - 1][nrMoves];
    			
    		}
    		
    		for(int j = 0; j < board.piecesNum[piece + offset]; j++) {
    			
    			nrMoves = 0;
    			
    			piecePos = board.pList[piece + offset][j];
    			for (int m = 0; m < pieceDirNumber[i]; m++) {
                    for (int n = 1; n <= pieceMaxMoves[i]; n++) {
                        if (board.extendedBoard[piecePos + pieceDir[i][m] * n] == PIECE.EMPTY.value) {
//                            addQuietMove(board, newMove, moveList);
                        	nrMoves++;
                        } else if (sideOf[board.extendedBoard[piecePos + pieceDir[i][m] * n]] == (bside)) {
                            break;
                        } else if (sideOf[board.extendedBoard[piecePos + pieceDir[i][m] * n]] == (bside ^ 1)) {
//                            addCaptureMove(board, newMove, moveList);
                        	nrMoves++;
                            break;
                        } else {
                            break;
                        }
                    }
                }
    			
    			// tot din prespectiva lui alb; adica pt negru scadem
    			 // mobility[piece][no_of_moves];
    			mg -= mobility_mg[piece - 1][nrMoves];
    			eg -= mobility_eg[piece - 1][nrMoves];
    			
    		}
    		
    	}

    	return new Score(mg, eg);
    }
    
    
}

class Score {
	int mg;
	int eg;
	public Score(int mg, int eg) {
		this.mg = mg;
		this.eg = eg;
	}
}

class MoveList{
	Move[] list;
	int size;
	
	
	public MoveList() {
		list = new Move[MoveGen.MAX_SIZE];
		size = 0;
	}
}

