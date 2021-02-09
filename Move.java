public class Move {
    // sq de unde pleaca
    int from;
    // sq unde ajunge
    int to;
    // daca a fost capturat ceva -> ce a fost captura else EMPTY
    int capture;
    // daca a fost promovat -> la ce a fost promovat else EMPTY
    int promotion;
    // daca a fost prima mutare a prionului
    boolean pawnStart;
    // daca am efectuat castling si de care 0 =  nu am efectuat
    int castling;
    // daca am efectuat atac de enpassant
    boolean enPas;
    
    int score;
    long hash;

    public Move(){}

    public Move(int from, int to, int capture, int promotion, boolean pawnStart, int castling, boolean enPas) {
        this.from = from;
        this.to = to;
        this.capture = capture;
        this.promotion = promotion;
        this.pawnStart = pawnStart;
        this.castling = castling;
        this.enPas = enPas;
    }
}
