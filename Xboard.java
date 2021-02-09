import java.util.Date;
import java.util.Scanner;

public class Xboard {

    int side;
    boolean force;
    Board board;
    MoveGen mg;
    Search search;

    String startFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    
    public SearchInfo info;

    public Xboard () {
    	init();
    }
    
    public void init () {
        MoveGen mg = new MoveGen();
        info = new SearchInfo(0);
        Search search = new Search(info);
    }

    public void newGame () {
    	board = new Board();
        board.initFEN(startFEN);
        side = 1;
        force = false;
    }

   public void parseMove (String moveString) {

        Move move = new Move(0, 0, PIECE.EMPTY.value, PIECE.EMPTY.value, false, 0, false);
        int opponentSide = side ^ 1;
        int pOffset = 6 * opponentSide;
        int forward = opponentSide == 0 ? 1 : -1;

        String[] token = moveString.split(" ");

        assert (token.length == 2);

        int fileFrom;
        int rankFrom;
        int fileTo;
        int rankTo;

        fileFrom = token[1].charAt(0) - 'a';
        rankFrom = token[1].charAt(1) - '1';
        fileTo = token[1].charAt(2) - 'a';
        rankTo = token[1].charAt(3) - '1';

        char promotion;
        if (token[1].length() == 5) {
            promotion = token[1].charAt(4);
        } else {
            promotion = 0;
        }
        int promPiece = -1;


        int sqFrom = Board.fr2sq120(fileFrom, rankFrom);
        int sqTo = Board.fr2sq120(fileTo, rankTo);

        int fromPiece = board.extendedBoard[sqFrom];
        int toPiece = board.extendedBoard[sqTo];

        assert (fromPiece != toPiece);
        assert (sqFrom != sqTo);
        assert (fromPiece != PIECE.EMPTY.value);
        assert (fromPiece != PIECE.OFF_BOARD.value);

        move.from = sqFrom;
        move.to = sqTo;


        if (sqFrom == board.kingSq[opponentSide]) {
            if (sqTo == sqFrom + 2 * DIRECTION.WEST.value) {
                move.castling =
                        opponentSide == SIDE.WHITE.value ? C_PERM.WQC.value : C_PERM.BQC.value;
            } else if (sqTo == sqFrom + 2 * DIRECTION.EAST.value) {
                move.castling =
                        opponentSide == SIDE.WHITE.value ? C_PERM.WKC.value : C_PERM.BKC.value;
            }
        }

        if (board.extendedBoard[sqTo] != PIECE.EMPTY.value) {
            if (MoveGen.sideOf[board.extendedBoard[sqTo]] == (opponentSide ^ 1)) {
                move.capture = board.extendedBoard[sqTo];
            }
        } else {
            // se muta in empty
            if (board.extendedBoard[sqFrom] == PIECE.WP.value ||
                board.extendedBoard[sqFrom] == PIECE.BP.value) {
                if (board.enPas != SQUARE.NO_SQ.value) {
                    if (sqTo == board.enPas) {
                            move.enPas = true;
                            move.capture = board.extendedBoard[board.enPas + forward * DIRECTION.SOUTH.value];
                    }
                }
                if (sqTo == sqFrom + 2 * forward * DIRECTION.NORTH.value) {
                    move.pawnStart = true;
                }
            }
        }

        if (promotion != 0) {
            switch (promotion) {
                case 'n':
                    promPiece = pOffset + 1;
                    break;
                case 'b':
                    promPiece = pOffset + 2;
                    break;
                case 'r':
                    promPiece = pOffset + 3;
                    break;
                case 'q':
                    promPiece = pOffset + 4;
                    break;
                default:
                    assert (1 == 0);
            }

            move.promotion = promPiece;
        }

        System.out.println("Am interpretat mutarea ca fiind");
        System.out.println("from: " + MoveGen.letters[MoveGen.fileOf[move.from]] + ""
                + MoveGen.numbers[MoveGen.rankOf[move.from]]);
        System.out.println("to: " + MoveGen.letters[MoveGen.fileOf[move.to]] + ""
                + MoveGen.numbers[MoveGen.rankOf[move.to]]);
        System.out.println("capture: " + board.pieceChar[move.capture]);
        System.out.println("promotion: " + board.pieceChar[move.promotion]);
        System.out.println("pawnStart: " + move.pawnStart);
        System.out.println("Castling: " +
                ((move.castling != 0) ? (((move.castling & C_PERM.WKC.value) != 0 ? "K" : "") +
                        ((move.castling & C_PERM.WQC.value) != 0 ? "Q" : "") +
                        ((move.castling & C_PERM.BKC.value) != 0 ? "k" : "") +
                        ((move.castling & C_PERM.BQC.value) !=
                                0 ? "q" : "")) : "-"));
        System.out.println("enPas: " + move.enPas);
        System.out.println("forward: " + forward);

        moveString += MoveGen.letters[MoveGen.fileOf[move.from]] + ""
                + MoveGen.numbers[MoveGen.rankOf[move.from]] + ""
                + MoveGen.letters[MoveGen.fileOf[move.to]] + ""
                + MoveGen.numbers[MoveGen.rankOf[move.to]] + ""
                + (move.promotion == PIECE.EMPTY.value ? "" : board.pieceChar[move.promotion] );

        if(!MoveGen.makeMove(board, move)) {
            System.out.println("EROARE LA PARSARE USERMOVE DE LA XBOARD/   posibil illegal move????");
        }

    }

   public String makeMoveAllv1() {
	   assert(side == board.side);
       SearchMove result;
       
       info.fh = 0;
       info.fhf = 0;
       info.nodeCount = 0;
       info.stop = false;
       info.startTime = new Date().getTime();
       
       
       Search.info.ttnewinsert = 0;
       Search.info.ttreplace = 0;
       Search.info.ttuse = 0;
       Search.info.ttcut = 0;
       Search.info.tthit = 0;
       
       result = Search.searchAllv1(board);
       

       System.out.println("am trecut prin " + info.nodeCount + " noduri cu viteza ");
       System.out.format("%,8d%n", (info.nodeCount)*100 / (info.searchTime));
       System.out.println(" noduri pe secunda");
       System.out.println("Move ordering: " + ((float)info.fhf / info.fh));
       System.out.println("~~~~<<<<CU SCORUL " + result.eval + ">>>>~~~~");

       System.out.println("ttnew = " + Search.info.ttnewinsert + " ttuse = " + Search.info.ttuse + " ttcut = " + Search.info.ttcut + " ttreplace = " + Search.info.ttreplace + " tthit = " + Search.info.tthit);
       
       
       Move move = result.move;
       String moveString = "move ";
       

       if(move == null) {
    	   System.out.println("~resign boss");
           return "resign";
       }

       MoveGen.makeMove(board, move);

       moveString += MoveGen.letters[MoveGen.fileOf[move.from]] + ""
                        + MoveGen.numbers[MoveGen.rankOf[move.from]] + ""
                        + MoveGen.letters[MoveGen.fileOf[move.to]] + ""
                        + MoveGen.numbers[MoveGen.rankOf[move.to]] + ""
                        + (move.promotion == PIECE.EMPTY.value ? "" : board.pieceChar[move.promotion] );


       return moveString;
   }
      

   public String makeMoveBestOneWithKillers() {
	   assert(side == board.side);
       SearchMove result;
       
       info.fh = 0;
       info.fhf = 0;
       info.nodeCount = 0;
       info.stop = false;
       info.startTime = new Date().getTime();
       
       result = Search.search(board);
       

       System.out.println("am trecut prin " + info.nodeCount + " noduri cu viteza ");
       System.out.format("%,8d%n", (info.nodeCount)*100 / (info.searchTime));
       System.out.println(" noduri pe secunda");
       System.out.println("Move ordering: " + ((float)info.fhf / info.fh));
       System.out.println("~~~~<<<<CU SCORUL " + result.eval + ">>>>~~~~");

       Move move = result.move;
       String moveString = "move ";
       

       if(move == null) {
           return "resign";
       }

       MoveGen.makeMove(board, move);

       moveString += MoveGen.letters[MoveGen.fileOf[move.from]] + ""
                        + MoveGen.numbers[MoveGen.rankOf[move.from]] + ""
                        + MoveGen.letters[MoveGen.fileOf[move.to]] + ""
                        + MoveGen.numbers[MoveGen.rankOf[move.to]] + ""
                        + (move.promotion == PIECE.EMPTY.value ? "" : Board.pieceChar[move.promotion] );


       return moveString;
   }

   
   
   public void xBoardLoop () {
        String command = "";
        Scanner scanner = new Scanner(System.in);

        while (true) {
            command = scanner.nextLine();
            //switch care trece prin toate comenzile de la xboard prin intermediul semnalelor
            // (constante)

            if (command.equals("xboard")) {
                // nothing
            } else if (command.equals("new")) {
                // new game
                newGame();
            } else if (command.equals("protover 2")) {
                System.out.println("feature sigint=0");
                System.out.println("feature usermove=1");
                System.out.println("feature myname=\"FFCP\"");
                System.out.println("feature san=0");
            } else if (command.contains("usermove ")) {
                // am primit move
                // parse move
                if(!force) {
                    parseMove(command);
                    System.out.println(makeMoveBestOneWithKillers());
                } else {
                    parseMove(command);
                    side = side ^ 1;
                }

                board.printCrtState();
            } else if (command.equals("black")) {
                // suntem black
                side = 1;
            } else if (command.equals("white")) {
                // suntem white
                side = 0;
            } else if (command.equals("quit")) {
                break;
            } else if (command.equals("force")) {
                // force mode
                force = true;
            } else if (command.equals("go")) {
                // go boi go
                force = false;
                side = board.side;
                System.out.println(makeMoveBestOneWithKillers());
            } else if (command.contains("time")) {
            	String[] tokenStrings = command.split(" ");
            	info.allowedTime = Long.parseLong(tokenStrings[1]);
            	System.out.println(tokenStrings[1]);
            	info.searchTime = info.allowedTime / 30;
            } else if (command.contains("otim")){
                // nothing
            } else if (command.contains("print")){
//                board.printCrtState();
            } else {
                // assert (1 == 0);
            }
        }
        
        scanner.close();
    }
}


