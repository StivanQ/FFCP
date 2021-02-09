import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.print.attribute.HashAttributeSet;

public class Search {

	static int[] playerOf = { 1, -1 };

	static Move[] moveH;
	static int[][] extenedBoards;
	
	public static int EXACT = 0;
	public static int LOWER_BOUND = 1;
	public static int UPPER_BOUND = 2;
	
	
	
	
	
	public static SearchInfo info;
	public static Random rand = new Random();
	
	public Search(SearchInfo info) {
		this.info = info;
	}

	static {
		moveH = new Move[32];
		extenedBoards = new int[32][120];
		//SearchInfo info = new SearchInfo(512);
//		System.out.println("just once");
	}

	private static void peek(SearchInfo info) {
		info.crtTime = new Date().getTime();
		// how much we've searched for >= allowed time + some error
		if(info.crtTime - info.startTime >= (info.searchTime - 10) * 10) {
			info.stop = true;
		}
	}



	public static int quiescenceInt(Board board, int alpha, int beta, int ply) {
		info.nodeCount++;
		int standingPat = playerOf[board.side] * board.evaluate();
		if(ply > info.maxReachedDepth) {
			info.maxReachedDepth = ply;
		}
		if(standingPat >= beta) {
			return beta;
		}
		if (alpha < standingPat) {
			alpha = standingPat;
		}
		
		MoveList moveList = new MoveList();
		MoveGen.genAllAttackMoves(board, moveList);

//		SearchMove result = new SearchMove();
		
		int result;
		
		SearchMove aux;
		int movesTried = 0;
		int max = 0;
		int val;

		Move auxiliar;

		int size = moveList.size;

		for (int i = 0; i < size; i++) {
			// move ordering
			auxiliar = moveList.list[i];
			for (int j = i + 1; j < size; j++) {
				if (moveList.list[j].score >= auxiliar.score) {
					auxiliar = moveList.list[j];
					max = j;
				}
			}
			moveList.list[max] = moveList.list[i];
			moveList.list[i] = auxiliar;

			if (!MoveGen.makeMove(board, auxiliar)) {
				continue;
			}
			movesTried++;

			val = -quiescenceInt(board, -beta, -alpha, ply + 1);

			MoveGen.unMakeMove(board, auxiliar);
			
			if(val > alpha) {
				alpha = val;
			}
			
			if(alpha >= beta) {
				if (movesTried == 1) {
					info.fhf++;
				}
				info.fh++;
				result = alpha;
				return result;
			}

		}
		
		result = alpha;

		return result;
		
	}
	

	public static void storeHash(int depth, int eval, int flag, long key, Move move) {
		TranspositionTable ttentry = info.tt[(int)(key & (info.ttsize - 1))];
		
		if(depth < ttentry.depth)
			return;
		
		if(ttentry.key == 0) {
			info.ttnewinsert++;
		} else {
			info.ttreplace++;
		}
		
		ttentry.depth = depth;
		ttentry.move = move;
		ttentry.eval = eval;
		ttentry.flag = flag;
		ttentry.key = key;
		
	}
	
	
	public static HashMove probeHash(long key, int depth, int alpha, int beta) {
		HashMove hMove = null;
		
		TranspositionTable ttentry = info.tt[(int)(key & (info.ttsize - 1))];
		
		if(ttentry.key != key) {
			return hMove;
		}
		
		hMove = new HashMove();
		hMove.move = ttentry.move;
		
		info.tthit++;
		if(ttentry.depth >= depth) {
			info.ttuse++;
			if(ttentry.flag == EXACT) {
				hMove.eval = ttentry.eval;
				return hMove;
			} else if (ttentry.flag == UPPER_BOUND) {
//				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~upper");
				if(ttentry.eval <= alpha) {
					hMove.eval = alpha;
					info.ttcut++;
					return hMove;
				}
			} else if (ttentry.flag == LOWER_BOUND) {
//				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~lower");
				if(ttentry.eval >= beta) {
					hMove.eval = beta;
					info.ttcut++;
					return hMove;
				}
			}
		}
		
		hMove.err = 1;
		
		return hMove;
	}
	
	// nega with TT
	public static int negamaxAllv1(Board board, int depth, int alpha, int beta, int ply, PVLine pline, boolean nullMove, int nodeType) {
		
		int flag = UPPER_BOUND;
		
		HashMove probe = probeHash(board.hash, depth, alpha, beta);
		Move hashMove = null;
		
		if(probe != null) {
			if(probe.err == 0) {
				if(ply != 0)
					return probe.eval;
			}
			hashMove = probe.move;
		}
		
		if (depth <= 0) {
			int result = quiescenceInt(board, alpha, beta, ply);
			storeHash(0, result, EXACT, board.hash, null);	
			return result;
		}
		
		info.nodeCount++;
		if((info.nodeCount & 0x400) != 0) {
			peek(info);
		}
		
		
		// incercam mutarea din PV si setam ca intram intr un expected PV node;
		// in expected pv node nu facem null prune
		
		boolean firstPVused = false;
		
		PVLine line = new PVLine();
		Move pvMove = info.pvLine.argmove[ply];
		int result;
		
			
		// vedem daca suntem in sah;
		// daca da: 1) atunci o sa extinem cautarea cu 1 ply
		//			2) nu facen null move pruning
		//			3) IDK 
		boolean inCheck = MoveGen.isAttacked(board, board.kingSq[board.side], board.side);
		
		
		
		int aux;
		
		int R = 2;
		
		if(nullMove && depth > 3 && !inCheck) {
			
			// make null move
			MoveGen.makeNullMove(board);
			info.nullPruneTries++;
		
			aux = -negamaxAllv1(board, depth - R - 1, -beta, -beta + 1, ply + 1, line, false, NODE_TYPE.CUT_NODE.value);
			
			// unmake null move
			MoveGen.unMakeNullMove(board);
			
			if(aux >= beta) {
				info.nullPrunes++;
				info.fhf++;
				info.fh++;
				return beta;
			}
			
			
		}

		
		
		

		MoveList moveList = new MoveList();

		MoveGen.genAllMoves(board, moveList);

		
		Move iterator;
		for(int i = 0; i < moveList.size && pvMove != null; i++) {
			iterator = moveList.list[i];
			
			if(iterator.from == pvMove.from &&
					iterator.to == pvMove.to &&
					iterator.capture == pvMove.capture &&
					iterator.castling == pvMove.castling &&
					iterator.promotion == pvMove.promotion &&
					iterator.enPas == pvMove.enPas &&
					iterator.pawnStart == pvMove.pawnStart) {
//				System.out.println("setam score");
				iterator.score = 9000;
				break;
			}
			
		}
		
		boolean pvFound = false;
		
		int movesTried = 0;
		int minim = -MoveGen.MATE_VALUE;

		Move bestMove = null;

		int max = 0;
		Move auxiliar;
		int extension = 0;

		int size = moveList.size;
		
		
		
		int nextNodeType = NODE_TYPE.ALL_NODE.value;;
		
		if (nodeType == NODE_TYPE.PV_NODE.value) {
			nextNodeType = NODE_TYPE.CUT_NODE.value;
		} else if (nodeType == NODE_TYPE.CUT_NODE.value) {
			nextNodeType = NODE_TYPE.ALL_NODE.value;
		} else {
			nextNodeType = NODE_TYPE.CUT_NODE.value;
		}
		
		for (int i = 0; i < size && !info.stop; i++) {
			auxiliar = moveList.list[i];
			for (int j = i + 1; j < size; j++) {
				if (moveList.list[j].score >= auxiliar.score) {
					auxiliar = moveList.list[j];
					max = j;
				}
			}

			moveList.list[max] = moveList.list[i];
			moveList.list[i] = auxiliar;
			
			extension = 0;
		
			if (!MoveGen.makeMove(board, auxiliar)) {
				continue;
			}
			
			// if in check search more
			if(inCheck) {
				extension++;
			}
			
			// if promotion search more or pawn to 7-th rank
			if(auxiliar.promotion != PIECE.EMPTY.value) {
				extension++;
			}
			
			// if pawn to 7-th rank
			if(
					(board.extendedBoard[auxiliar.to] == PIECE.WP.value ||
					board.extendedBoard[auxiliar.to] == PIECE.BP.value)
					&& 
					(MoveGen.rankOf[auxiliar.to] == RANK.RANK_7.value || 
					MoveGen.rankOf[auxiliar.to] == RANK.RANK_2.value)) {
//				System.out.println("PAWN EXTENSION");
//				
//				board.printCrtState();
				extension++;
			}
			
			if(!pvFound) {
			
				aux = -negamaxAllv1(board, depth + extension - 1, -beta, -alpha, ply + 1, line, false, nextNodeType);
			
			} else {
				
				if(!inCheck && depth > 2 && movesTried > 4) {
					aux = -negamaxAllv1(board, depth + extension - 2, -alpha - 1, -alpha, ply + 1, line, false, nextNodeType);
				} else {
					// TODO: late move reduction (if moves tried > 4) or something then reduce the depth with 1 or 2
					aux = -negamaxAllv1(board, depth + extension - 1, -alpha - 1, -alpha, ply + 1, line, false, nextNodeType);
				}
//				System.out.println("null window");
				
				if(aux > alpha && aux < beta) {
					aux = -negamaxAllv1(board, depth + extension - 1, -beta, -alpha, ply + 1, line, false, nextNodeType);
//					System.out.println("REFACEM null window");
				}
				
			}
			
			
			
			// 3-fold repetition
			// TODO: contempt
			// TODO: sa incerci s-o muti in afara loopului
			int times = 0;
			for(int j = board.hisPly; j > (board.hisPly - board.fifty) && j > 0; j--) {
				if(board.hash == board.history[j].hash) {
					times++;
				}
			}
			
			if (times == 3) {
				aux = 0;
			}
			
				
			MoveGen.unMakeMove(board, auxiliar);
			
			movesTried++;
			if (minim < aux) {
				minim = aux;
				bestMove = auxiliar;
			}
			if (alpha < minim) {
				alpha = minim;
				
				flag = EXACT;
				
				// storing the PV-Node
				auxiliar.hash = board.hash;
				pline.argmove[0] = auxiliar;
				for(int k = 0; k < line.cmove; k++) {
					pline.argmove[k + 1] = line.argmove[k];
				}
				pline.cmove = line.cmove + 1;
			}
			if (alpha >= beta) {
				if (movesTried == 1) {
					info.fhf++;
				}
				info.fh++;
				storeHash(depth, beta, LOWER_BOUND, board.hash, bestMove);
				return beta;
			}

		}
		if (movesTried == 0 && !info.stop) {
			if (inCheck) {
				return -(MoveGen.MATE_VALUE - ply);
			} else {
				return 0;
			}
		}

		storeHash(depth, alpha, flag, board.hash, bestMove);
		
		return alpha;
		
	}

	
	public static SearchMove searchAllv1(Board board) {

		SearchMove result = new SearchMove();
	
    	String moveString = "";
    	Move move;
    	int resultMove;
    	Date date = new Date();
    	info.startTime = date.getTime();
    	
     	
    	int alpha = -MoveGen.MATE_VALUE;
    	int beta = MoveGen.MATE_VALUE;
    	int windowValue = 32;
    	int outputScore;
    	int fails = 0;
		
    	
    	
		for (int i = 1; i <= info.maxDepth && !info.stop; i++) {
			
			// public static int negaPV2Refactored(Board board, int depth, int alpha, int beta, int ply, PVLine pline, boolean nullMove, int nodeType)
			resultMove = negamaxAllv1(board, i, alpha, beta, 0, info.pvLine, false, NODE_TYPE.PV_NODE.value);
			if(!info.stop) {
				result.eval = resultMove;
				result.move = info.pvLine.argmove[0];
			}
			
			if(!info.stop) {
				long crtTime = new Date().getTime();
				
				if(resultMove < -(MoveGen.MATE_VALUE - 100)) {
					outputScore = -100000 - (MoveGen.MATE_VALUE + resultMove);
				} else if (resultMove > (MoveGen.MATE_VALUE - 100)) {
					outputScore = +100000 - resultMove + MoveGen.MATE_VALUE;
				} else {
					outputScore = (resultMove * 100 / 128);
				}
				
				System.out.print(i + " " + outputScore + " " + ((crtTime - info.startTime) / 10) + " " + info.nodeCount );
				
				for(int j = 0; j < info.pvLine.cmove && !info.stop; j++) {
					moveString = "";
		    		move = info.pvLine.argmove[j];
		    		moveString += MoveGen.letters[MoveGen.fileOf[move.from]] + ""
		                    + MoveGen.numbers[MoveGen.rankOf[move.from]] + ""
		                    + MoveGen.letters[MoveGen.fileOf[move.to]] + ""
		                    + MoveGen.numbers[MoveGen.rankOf[move.to]] + ""
		                    + (move.promotion == PIECE.EMPTY.value ? "" : board.pieceChar[move.promotion] );
		    		System.out.print(" " +  moveString);
				}
				System.out.println("");
			}
			
			
    		if(Math.abs(resultMove) >= MoveGen.MATE_VALUE - 100) {
    			break;
    		}

			
			if(resultMove <= alpha) {
				fails++;
				// marim window-ul in mod exponential
				alpha -= windowValue * 1 << (fails > 1 ? fails - 1 : fails);
				i--;
		    	continue;
			} else if (resultMove >= beta) {
				fails++;
				beta += windowValue * 1 << (fails > 1 ? fails - 1 : fails);
				i--;
		    	continue;
			}
			
			alpha = resultMove - windowValue; 
			beta = resultMove + windowValue;
			fails = 0;
		}
		
//		result.move = info.pvLine.argmove[0];
		
		return result;

	}


	public static SearchMove search(Board board) {

		SearchMove result = new SearchMove();
	
    	String moveString = "";
    	Move move;
    	int resultMove;
    	Date date = new Date();
    	info.startTime = date.getTime();
    	
     	
    	int alpha = -MoveGen.MATE_VALUE;
    	int beta = MoveGen.MATE_VALUE;
    	int windowValue = 32;
    	int outputScore;
    	int fails = 0;
		
    	
    	
		for (int i = 1; i <= info.maxDepth && !info.stop; i++) {
			resetKillers();
			// public static int negaPV2Refactored(Board board, int depth, int alpha, int beta, int ply, PVLine pline, boolean nullMove, int nodeType)
			resultMove = alphaBetaBun(board, i, alpha, beta, 0, info.pvLine, false, NODE_TYPE.PV_NODE.value);
			if(!info.stop) {
				result.eval = resultMove;
				result.move = info.pvLine.argmove[0];
			}
			
			if(!info.stop) {
				long crtTime = new Date().getTime();
				
				if(resultMove < -(MoveGen.MATE_VALUE - 100)) {
					outputScore = -100000 - (MoveGen.MATE_VALUE + resultMove);
				} else if (resultMove > (MoveGen.MATE_VALUE - 100)) {
					outputScore = +100000 - resultMove + MoveGen.MATE_VALUE;
				} else {
					outputScore = (resultMove * 100 / 128);
				}
				
				System.out.print(i + " " + outputScore + " " + ((crtTime - info.startTime) / 10) + " " + info.nodeCount );
				
				for(int j = 0; j < info.pvLine.cmove && !info.stop; j++) {
					moveString = "";
		    		move = info.pvLine.argmove[j];
		    		moveString += MoveGen.letters[MoveGen.fileOf[move.from]] + ""
		                    + MoveGen.numbers[MoveGen.rankOf[move.from]] + ""
		                    + MoveGen.letters[MoveGen.fileOf[move.to]] + ""
		                    + MoveGen.numbers[MoveGen.rankOf[move.to]] + ""
		                    + (move.promotion == PIECE.EMPTY.value ? "" : board.pieceChar[move.promotion] );
		    		System.out.print(" " +  moveString);
				}
				System.out.println("");
			}
			
			
    		if(Math.abs(resultMove) >= MoveGen.MATE_VALUE - 100) {
    			break;
    		}

			
			if(resultMove <= alpha) {
				fails++;
				// marim window-ul in mod exponential
				alpha -= windowValue * 1 << (fails > 1 ? fails - 1 : fails);
				i--;
		    	continue;
			} else if (resultMove >= beta) {
				fails++;
				beta += windowValue * 1 << (fails > 1 ? fails - 1 : fails);
				i--;
		    	continue;
			}
			
			alpha = resultMove - windowValue; 
			beta = resultMove + windowValue;
			fails = 0;
		}
		
//		result.move = info.pvLine.argmove[0];
		
		return result;

	}

	
	// nega without TT
	public static int alphaBetaBun(Board board, int depth, int alpha, int beta, int ply, PVLine pline, boolean nullMove, int nodeType) {
		
		// ASTA E AIA BLANAO
		
		if (depth <= 0) {
			return quiescenceInt(board, alpha, beta, ply);
		}
		
		info.nodeCount++;
		if((info.nodeCount & 0x400) != 0) {
			peek(info);
		}
		
		
		// incercam mutarea din PV si setam ca intram intr un expected PV node;
		// in expected pv node nu facem null prune
		
		boolean firstPVused = false;
		
		PVLine line = new PVLine();
		Move pvMove = info.pvLine.argmove[ply];
		int result;
		
			
		// vedem daca suntem in sah;
		// daca da: 1) atunci o sa extinem cautarea cu 1 ply
		//			2) nu facen null move pruning
		//			3) IDK 
		boolean inCheck = MoveGen.isAttacked(board, board.kingSq[board.side], board.side);
		
		
		
		int aux;
		
		int R = 2;
		
		if(nullMove && depth > 3 && !inCheck) {
			
			// make null move
			MoveGen.makeNullMove(board);
			info.nullPruneTries++;
		
			aux = -alphaBetaBun(board, depth - R - 1, -beta, -beta + 1, ply + 1, line, false, NODE_TYPE.CUT_NODE.value);
			
			// unmake null move
			MoveGen.unMakeNullMove(board);
			
			if(aux >= beta) {
				info.nullPrunes++;
				info.fhf++;
				info.fh++;
				return beta;
			}
			
			
		}

		
		
		

		MoveList moveList = new MoveList();

		MoveGen.genAllMoves(board, moveList);

		
		Move iterator;
		Move killer1;
		Move killer2;
		
		for(int i = 0; 
				i < moveList.size && 
				(pvMove != null || 
				info.killers[ply][0] != null || 
				info.killers[ply][1] != null); 
				i++) {
			iterator = moveList.list[i];
			
			if(pvMove != null) {	
				// what an ugly piece of code
				if(iterator.from == pvMove.from &&
						iterator.to == pvMove.to &&
						iterator.capture == pvMove.capture &&
						iterator.castling == pvMove.castling &&
						iterator.promotion == pvMove.promotion &&
						iterator.enPas == pvMove.enPas &&
						iterator.pawnStart == pvMove.pawnStart) {
					iterator.score = 9000;
					continue;
				}
			}
			
			if(info.killers[ply][0] != null) {
				killer1 = info.killers[ply][0];
				// what an ugly piece of code
				if(iterator.from == killer1.from &&
						iterator.to == killer1.to &&
						iterator.capture == killer1.capture &&
						iterator.castling == killer1.castling &&
						iterator.promotion == killer1.promotion &&
						iterator.enPas == killer1.enPas &&
						iterator.pawnStart == killer1.pawnStart) {
//					System.out.println("setam score killer1");
					iterator.score = 400;
					continue;
				}
			}
			
			if(info.killers[ply][1] != null) {
				killer2 = info.killers[ply][1];
				// what an ugly piece of code
				if(iterator.from == killer2.from &&
						iterator.to == killer2.to &&
						iterator.capture == killer2.capture &&
						iterator.castling == killer2.castling &&
						iterator.promotion == killer2.promotion &&
						iterator.enPas == killer2.enPas &&
						iterator.pawnStart == killer2.pawnStart) {
//					System.out.println("setam score killer1");
					iterator.score = 300;
					continue;
				}
			}
			
		}
		
		boolean pvFound = false;
		
		int movesTried = 0;
		int minim = -MoveGen.MATE_VALUE;

		Move bestMove = null;

		int max = 0;
		Move auxiliar;
		int extension = 0;

		int size = moveList.size;
		
		
		for (int i = 0; i < size && !info.stop; i++) {
			auxiliar = moveList.list[i];
			for (int j = i + 1; j < size; j++) {
				if (moveList.list[j].score >= auxiliar.score) {
					auxiliar = moveList.list[j];
					max = j;
				}
			}

			moveList.list[max] = moveList.list[i];
			moveList.list[i] = auxiliar;
			
			extension = 0;
		
			if (!MoveGen.makeMove(board, auxiliar)) {
				continue;
			}
			
			// if in check search more
			if(inCheck) {
				extension++;
			}
			
			// if promotion search more or pawn to 7-th rank
			if(auxiliar.promotion != PIECE.EMPTY.value) {
				extension++;
			}
			
			// if pawn to 7-th rank
			if(
					(board.extendedBoard[auxiliar.to] == PIECE.WP.value ||
					board.extendedBoard[auxiliar.to] == PIECE.BP.value)
					&& 
					(MoveGen.rankOf[auxiliar.to] == RANK.RANK_7.value || 
					MoveGen.rankOf[auxiliar.to] == RANK.RANK_2.value)) {
//				System.out.println("PAWN EXTENSION");
//				
//				board.printCrtState();
				extension++;
			}
			
			if(!pvFound) {
			
				aux = -alphaBetaBun(board, depth + extension - 1, -beta, -alpha, ply + 1, line, true, NODE_TYPE.PV_NODE.value);
			
			} else {
				
				if(!inCheck && depth > 2 && movesTried > 4) {
					aux = -alphaBetaBun(board, depth + extension - 2, -alpha - 1, -alpha, ply + 1, line, true, NODE_TYPE.ALL_NODE.value);
				} else {
					aux = -alphaBetaBun(board, depth + extension - 1, -alpha - 1, -alpha, ply + 1, line, true, NODE_TYPE.ALL_NODE.value);
				}

				if(aux > alpha && aux < beta) {
					aux = -alphaBetaBun(board, depth + extension - 1, -beta, -alpha, ply + 1, line, true, NODE_TYPE.PV_NODE.value);
				}
				
			}
			
			
			
			// 3-fold repetition
			// TODO: contempt
			// TODO: sa incerci s-o muti in afara loopului
			int times = 0;
			for(int j = board.hisPly; j > (board.hisPly - board.fifty) && j > 0; j--) {
				if(board.hash == board.history[j].hash) {
					times++;
				}
			}
			
			if (times == 3) {
				aux = 0;
			}
			
				
			MoveGen.unMakeMove(board, auxiliar);
			
			movesTried++;
			if (minim < aux) {
				minim = aux;
				bestMove = auxiliar;
			}
			if (alpha < minim) {
				alpha = minim;
				
				// storing the PV-Node
				auxiliar.hash = board.hash;
				pline.argmove[0] = auxiliar;
				for(int k = 0; k < line.cmove; k++) {
					pline.argmove[k + 1] = line.argmove[k];
				}
				pline.cmove = line.cmove + 1;
			}
			if (alpha >= beta) {
				if (movesTried == 1) {
					info.fhf++;
				}
				info.fh++;
				
				// killers are non-captures
				if(auxiliar.capture == PIECE.EMPTY.value) {	
					info.killers[ply][1] = info.killers[ply][0];
					info.killers[ply][0] = auxiliar;
					
				}
				return beta;
			}

		}
		if (movesTried == 0 && !info.stop) {
			if (inCheck) {
				return -(MoveGen.MATE_VALUE - ply);
			} else {
				return 0;
			}
		}
		
		return alpha;
		
	}
	

	

	public static void resetKillers() {
		for(int i = 0; i < info.maxDepth; i++) {
			info.killers[i][0] = null;
			info.killers[i][1] = null;
		}
	}
	
	
}

class HashMove {
	Move move;
	int eval;
	int err;
}

class SearchMove {
	Move move;
	int eval;
	int score;
	long hash;

	public SearchMove() {
	}

	public SearchMove(int eval) {
		this.eval = eval;
	}
	public SearchMove(int eval, Move move) {
		this.eval = eval;
		this.move = move;
	}
}

class Box {
	long count;
}


class PVLine {
	int cmove;
	Move[] argmove;
	
	public PVLine() {
		argmove = new Move[64];
	}
	
}

class SearchInfo {
	long startTime;
	long crtTime;

	int crtDepth;
	int maxDepth;

	// allowedTime in centiseconds (1/100 sec) / remainingTime
	long allowedTime;
	long searchTime;
	boolean stop;

	long nodeCount;
	long terminalNodes;

//	SearchMove[] pvMoves;
	
	long ttnewinsert;
	long ttuse;
	long ttcut;
	long ttreplace;
	long tthit;
	
	PVLine pvLine;
	
	int nullPruneTries;
	int nullPrunes;
	
	TranspositionTable[] tt;
	int ttsize;
	
	int drawByRep;

	long fh;
	long fhf;
	
	int maxReachedDepth;
	int ply;

	Move[][] killers;
	
	public SearchInfo(int size) {
//		pvMoves = new SearchMove[30];
		ttsize = size * 1024 * 1024;
		tt = new TranspositionTable[ttsize];
		for(int i = 0; i < ttsize; i++) {
			tt[i] = new TranspositionTable();
		}
		pvLine = new PVLine();
		maxDepth = 28;
		
		killers = new Move[maxDepth][2];
	}

}

class TranspositionTable{
	long key;
	Move move;
	int flag;
	int depth;
	int eval;
	
	public TranspositionTable() {}
	
	public TranspositionTable(long key, Move move) {
		this.key = key;
		this.move = move;
	}
	
	public TranspositionTable(long key, Move move, int flag, int depth, int eval) {
		this.key = key;
		this.move = move;
		this.flag = flag;
		this.depth = depth;
		this.eval = eval;
	}
	
}
