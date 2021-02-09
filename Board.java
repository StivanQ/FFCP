//import static Enums.functie;

public class Board {
    int[] extendedBoard;

    int[] sq64to120;
    int[] sq120to64;

    int side;

    int fifty;
    int ply;
    int hisPly;

    int enPas; // asta va tine un square
    int castlePerm;

    // material
    int pieceValue = 0;
    int pieceSquareValue = 0;

    int eval_mg;
    int eval_eg;
    int psq_eval_mg;
    int psq_eval_eg;
    int phase;
    
    

    int[][] pList;  // plist [WB][0] = SQ_A1;
    long[] pawn;
    int[] kingSq;
    // ink n-am folosit
    int[] minPieces;
    // ink n-am folosit
    int[] majPieces;
    // am folosit
    int[] piecesNum;
    
    long hash = 0;

    History[] history;

    static char[] pieceChar = {'P', 'N', 'B', 'R', 'Q', 'K', 'p', 'n', 'b', 'r', 'q', 'k', '.'};

    // 0 = midgame
    // 1 = lategame
    int state;

    public Board () {
        extendedBoard = new int[120];

        sq64to120 = new int[64];
        sq120to64 = new int[120];

        pawn = new long[3];

        // 12 piese de fiecare tip, maxim 10 bucati fiecare
        // gen pList[WN][0] = B1
        pList = new int[12][10];

        piecesNum = new int[13];
        kingSq = new int[2];

        history = new History[512];
        for (int i = 0; i < 512; i++) {
            history[i] = new History();
        }
        // all perm;

        initAll();
    }

    // initializarea structurii de board
    private void initAll () {
        initArrays();
    }

    private void initArrays () {
        // s120to64 primeste sqare si ret piesa
        for (int i = 0; i < 120; i++) {
            sq120to64[i] = SQUARE.NO_SQ.value;
        }

        int sq = 0;
        int sq64 = 0;

        for (int rank = RANK.RANK_1.value; rank <= RANK.RANK_8.value; rank++) {
            for (int file = FILE.FILE_A.value; file <= FILE.FILE_H.value; file++) {
                sq = fr2sq120(file, rank);
                sq120to64[sq] = sq64;
                sq64to120[sq64] = sq;
                sq64++;
            }
        }
    }

    public static int fr2sq120 (int file,
                                int rank) {
        return 21 + 10 * rank + file;
    }

    public static int fr2sq120m (int file,
                                int rank) {
        return 21 + 10 * (7 - rank)+ file;
    }

    public void initFEN (String FEN) {
        parseFEN(FEN);
        setBoardAfterFEN();
    }

    // TODO: termina parsarea FEN-ului
    // exemplu FEN
    // 1Rkrr3/p4p1p/5b1n/2pP4/2P1P3/P5P1/2K4P/8 b - - 0 32
    // starting FEN
    // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
    // pare ok pana acum, as zice
    private int parseFEN (String fen) {
        int rank = RANK.RANK_8.value;
        int file = FILE.FILE_A.value;

        int piece = 0;
        int count = 0;
        int sq64 = 0;
        int sq120 = 0;
        int i = 0;
        boolean breakOut = false;
        boolean enPas = false;
        boolean enPasSet = false;
        boolean sideSet = false;

        char c1 = 0;
        char c2 = 0;

        resetBoard();

        // in forul asta parsez tabla fara elementele de dupa de ex cine muta si castling permisions
        for (i = 0; i < fen.length() && rank >= RANK.RANK_1.value; i++) {
            count = 1;
            piece = PIECE.EMPTY.value;
            switch (fen.charAt(i)) {
                case 'p':
                    piece = PIECE.BP.value;
                    break;
                case 'n':
                    piece = PIECE.BN.value;
                    break;
                case 'b':
                    piece = PIECE.BB.value;
                    break;
                case 'r':
                    piece = PIECE.BR.value;
                    break;
                case 'q':
                    piece = PIECE.BQ.value;
                    break;
                case 'k':
                    piece = PIECE.BK.value;
                    break;
                case 'P':
                    piece = PIECE.WP.value;
                    break;
                case 'N':
                    piece = PIECE.WN.value;
                    break;
                case 'B':
                    piece = PIECE.WB.value;
                    break;
                case 'R':
                    piece = PIECE.WR.value;
                    break;
                case 'Q':
                    piece = PIECE.WQ.value;
                    break;
                case 'K':
                    piece = PIECE.WK.value;
                    break;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                    count = Integer.parseInt(fen.substring(i, i + 1));
                    break;
                case '/':
                    rank--;
                    file = FILE.FILE_A.value;
                    continue;
                case ' ':
                    breakOut = true;
                    break;

            }

            if (breakOut) {
                break;
            }

            for (int j = 0; j < count; j++) {
                sq64 = rank * 8 + file;
                sq120 = sq64to120[sq64];
                if (piece != PIECE.EMPTY.value) {
                    extendedBoard[sq120] = piece;
                }
                file++;
            }
        }


        // aici parsez totul dupa tabla in sine, de ex cine muta si castling permisions
        for (; i < fen.length(); i++) {
            switch (fen.charAt(i)) {
                case 'w':
                    side = SIDE.WHITE.value;
                    sideSet = true;
                    break;
                case 'K':
                    castlePerm |= C_PERM.WKC.value;
                    break;
                case 'Q':
                    castlePerm |= C_PERM.WQC.value;
                    break;
                case 'k':
                    castlePerm |= C_PERM.BKC.value;
                    break;
                case 'q':
                    castlePerm |= C_PERM.BQC.value;
                    break;
                case '-':
                    break;
                case 'b':
                    if (!sideSet) {
                        side = SIDE.BLACK.value;
                        sideSet = true;
                        break;
                    }
                case 'a':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                    file = fen.charAt(i) - 'a' + FILE.FILE_A.value;
                    enPas = true;
                    break;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    if (enPas) {
                        System.out.println("inta aci?");
                        rank = fen.charAt(i) - '1' + RANK.RANK_1.value;
                        this.enPas = fr2sq120(file, rank);
                        enPas = false;
                        enPasSet = true;
                    } else {
                        if (c1 == 0) {
                            c1 = fen.charAt(i);
                        } else {
                            c2 = fen.charAt(i);
                        }
                    }
            }
        }

        return 1;
    }

    // fac niste chestii in pus dupa ce parsez FEN-ul
    private void setBoardAfterFEN () {
        // aici trec prin extended board si unde gasesc piesa ii bag pozitia pList
        // si incrementez numarul de piese de tip din piecesNum
        for (int rank = RANK.RANK_1.value; rank <= RANK.RANK_8.value; rank++) {
            for (int file = FILE.FILE_A.value; file <= FILE.FILE_H.value; file++) {
                if (extendedBoard[fr2sq120(file, rank)] != PIECE.EMPTY.value) {
                    pList[extendedBoard[fr2sq120(file, rank)]][piecesNum[extendedBoard[fr2sq120(
                            file, rank)]]] = fr2sq120(file, rank);
                    piecesNum[extendedBoard[fr2sq120(file, rank)]]++;
                }
            }
        }

        kingSq[SIDE.WHITE.value] = pList[PIECE.WK.value][0];
        kingSq[SIDE.BLACK.value] = pList[PIECE.BK.value][0];

        history[1].castelPerm = castlePerm;
        history[1].fifty = fifty;
        history[1].enPas = enPas;
        hisPly = 1;

        // evaluare din perspectiva lui alb
        for(int i = PIECE.WP.value; i < PIECE.EMPTY.value; i++) {
                eval_mg += MoveGen.material[0][i] * piecesNum[i];
                for(int j = 0; j < piecesNum[i]; j++) {
                  eval_mg += MoveGen.psq_mg[i][pList[i][j]];
              }
        }
        
        for(int i = PIECE.WP.value; i < PIECE.EMPTY.value; i++) {
            eval_eg += MoveGen.material[1][i] * piecesNum[i];
            for(int j = 0; j < piecesNum[i]; j++) {
            	eval_eg += MoveGen.psq_eg[i][pList[i][j]];
            }
        }

//        for(int i = PIECE.WP.value; i < PIECE.EMPTY.value; i++) {
//            for(int j = 0; j < piecesNum[i]; j++) {
//                psq_eval_mg += MoveGen.psq_mg[i][pList[i][j]];
//            }
//        }
//        
//        for(int i = PIECE.WP.value; i < PIECE.EMPTY.value; i++) {
//            for(int j = 0; j < piecesNum[i]; j++) {
//            	psq_eval_eg += MoveGen.psq_eg[i][pList[i][j]];
//            }
//        }
        
        phase = MoveGen.totatlPhase;
        for(int i = PIECE.WP.value; i < PIECE.EMPTY.value; i++) {
        	phase -= piecesNum[i] * MoveGen.phases_score[i];
        }
        
        // setting the hash
        for(int i = 0; i < 6; i++) {
        	// white
        	for(int j = 0; j < piecesNum[i]; j++) {
        		hash ^= MoveGen.hasheKey[i * 120 + pList[i][j]];
        	}
        	
        	
        	// black
        	for(int j = 0; j < piecesNum[i + 6]; j++) {
        		hash ^= MoveGen.hasheKey[(i + 6)* 120 + pList[i + 6][j]];
        	}
        }
        
        if(side == SIDE.BLACK.value) {
        	hash ^= MoveGen.hasheKey[MoveGen.BLACK_TO_MOVE_OFFSET];
        }
        
        if(enPas != SQUARE.NO_SQ.value) {
        	hash ^= MoveGen.hasheKey[MoveGen.EN_OFFSET + MoveGen.fileOf[enPas]];
        }
        
        hash ^= MoveGen.hasheKey[MoveGen.CASTLING_OFFSET + castlePerm];
    }

    // reseteeaza tot
    // a se folosi inainte de citirea unui FEN
    private void resetBoard () {
        for (int i = 0; i < PIECE.EMPTY.value; i++) {
            for (int j = 0; j < 10; j++) {
                pList[i][j] = SQUARE.NO_SQ.value;
            }
        }

        for (int i = 0; i < 120; i++) {
            extendedBoard[i] = PIECE.OFF_BOARD.value;
        }

        for (int i = 0; i < 64; i++) {
            extendedBoard[sq64to120[i]] = PIECE.EMPTY.value;
        }

        kingSq[SIDE.WHITE.value] = SQUARE.NO_SQ.value;
        kingSq[SIDE.BLACK.value] = SQUARE.NO_SQ.value;
        
        psq_eval_eg = 0;
        psq_eval_mg = 0;
        
        phase = 0;

        side = SIDE.BOTH.value;
        enPas = SQUARE.NO_SQ.value;
        fifty = 0;

        ply = 1;
        hisPly = 1;

        castlePerm = 0;

        // pornim din midgame
        state = 0;
        pieceValue = 0;
        pieceSquareValue = 0;

        eval_mg = 0;
        eval_eg = 0;
        
    }

    public void printCrtState () {
    	
    	//setBoardAfterFEN();
    	
        System.out.println("");

        int i = 1;

        for (int rank = RANK.RANK_8.value; rank >= RANK.RANK_1.value; rank--) {
            System.out.print(rank + 1 + "  ");
            for (int file = FILE.FILE_A.value; file <= FILE.FILE_H.value; file++) {
                System.out.print(pieceChar[extendedBoard[fr2sq120(file, rank)]] + " ");
                //                System.out.print(extendedBoard[fr2sq120(file, rank)] + " ");
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("   a b c d e f g h");

        System.out.println((side == SIDE.WHITE.value ? "white" : (
                side == SIDE.BLACK.value ? "black" : "both?")) + " to move");
        if (enPas != SQUARE.NO_SQ.value) {
            System.out.println(
                    "enPas este setat pe " + MoveGen.letters[MoveGen.fileOf[enPas]] + "" +
                    MoveGen.numbers[MoveGen.rankOf[enPas]]);
        } else {
            System.out.println("enPas este setat pe NO_SQ");
        }
        System.out.println("Castiling perm:" +
                           ((castlePerm != 0) ? (((castlePerm & C_PERM.WKC.value) != 0 ? "K" : "") +
                                                 ((castlePerm & C_PERM.WQC.value) != 0 ? "Q" : "") +
                                                 ((castlePerm & C_PERM.BKC.value) != 0 ? "k" : "") +
                                                 ((castlePerm & C_PERM.BQC.value) !=
                                                  0 ? "q" : "")) : "-"));

        /*System.out.println("There are :\n");
        for(int piece = 0; piece < PIECE.EMPTY.value; piece++) {
            System.out.println(piecesNum[piece] + "" + pieceChar[piece]);
        }*/


/*        for (int j = 0; j < 12; j++) {
            System.out.println("there are " + piecesNum[j] + " pieces of " + pieceChar[j]);
        }*/

/*        for (int j = 0; j < 12; j++) {
            System.out.println("these are the squares for: " + pieceChar[j]);
            for (int k = 0; k < piecesNum[j]; k++) {
                System.out.print(MoveGen.letters[MoveGen.fileOf[pList[j][k]]] + ""+MoveGen.numbers[MoveGen.rankOf[pList[j][k]]] + "  ");
            }
            System.out.println();
        }*/
        System.out.println("HASH: 0x" + Long.toHexString(hash));
//        System.out.println("evaluated at " + (eval_mg)+ " from white's " +
//                           "perspective");
        System.out.println("fifty:" + fifty);
//        System.out.println("crt phase (mg = 0; eg = 24):" + phase);
        System.out.println("evaluated at " + evaluate() + " from white's " +
        					"perspective");

    }
    
    // avaluates the board from white's perspective
    public int evaluate() {
    	int result = 0;
    	int phase = MoveGen.totatlPhase;
    	
    	// phase
    	
    	for(int i = 0; i < PIECE.EMPTY.value; i++) {
    		phase -= piecesNum[i] * MoveGen.phases_score[i];
    	}
    	
    	
    	int mg = eval_mg;
    	int eg = eval_eg;
    	
    	
    	// bishop pair bonus
    	if(piecesNum[PIECE.WB.value] >= 2) {
    		mg += 50;
    		eg += 20;
    	}
    	
    	if(piecesNum[PIECE.WB.value] >= 2) {
    		mg -= 50;
    		eg -= 20;
    	}
    	
    	
    	Score mobilityScore = MoveGen.getMobilityBonus(this);
    	
//    	System.out.println(mg + " " + eg);
    	
    	mg += mobilityScore.mg;
    	eg += mobilityScore.eg;
    	
//    	System.out.println(mg + " " + eg);
    	
//    	System.out.println("phase: " + phase);
    	
    	result = (mg * (MoveGen.totatlPhase - phase) + eg * phase) / MoveGen.totatlPhase;
//    	System.out.println("score: " + result);
    	
    	// small tempo to the side to move
    	result = result + (side == SIDE.BLACK.value ? -15 : 15);
    	
    	
    	return result;
    }
    
    
}
