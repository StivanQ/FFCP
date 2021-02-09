
public class Enums {


}


enum FILE {
    FILE_A(0),
    FILE_B(1),
    FILE_C(2),
    FILE_D(3),
    FILE_E(4),
    FILE_F(5),
    FILE_G(6),
    FILE_H(7),
    FILE_NB(8);

    int value;
    FILE(int value) {
        this.value = value;
    }

    public int getValue () {
        return value;
    }
}

enum RANK {
    RANK_1(0),
    RANK_2(1),
    RANK_3(2),
    RANK_4(3),
    RANK_5(4),
    RANK_6(5),
    RANK_7(6),
    RANK_8(7),
    RANK_NB(8);

    int value;
    RANK(int value) {
        this.value = value;
    }

    public int getValue () {
        return value;
    }
}

enum PIECE {
    WP(0),
    WN(1),
    WB(2),
    WR(3),
    WQ(4),
    WK(5),
    BP(6),
    BN(7),
    BB(8),
    BR(9),
    BQ(10),
    BK(11),
    EMPTY(12),
    OFF_BOARD(13);

    int value;
    PIECE(int value) {
        this.value = value;
    }

    public int getValue () {
        return value;
    }
}

enum SQUARE {
    A1(21), B1(22), C1(23), D1(24), E1(25), F1(26), G1(27), H1(28),
    A2(31), B2(32), C2(33), D2(34), E2(35), F2(36), G2(37), H2(38),
    A3(41), B3(42), C3(43), D3(44), E3(45), F3(46), G3(47), H3(48),
    A4(51), B4(52), C4(53), D4(54), E4(55), F4(56), G4(57), H4(58),
    A5(61), B5(62), C5(63), D5(64), E5(65), F5(66), G5(67), H5(68),
    A6(71), B6(72), C6(73), D6(74), E6(75), F6(76), G6(77), H6(78),
    A7(81), B7(82), C7(83), D7(84), E7(85), F7(86), G7(87), H7(88),
    A8(91), B8(92), C8(93), D8(94), E8(95), F8(96), G8(97), H8(98),
    NO_SQ(99);

    int value;
    SQUARE(int value) {
        this.value = value;
    }

    public int getValue () {
        return value;
    }
}

enum SIDE {
    WHITE(0),
    BLACK(1),
    BOTH(2);

    int value;
    SIDE(int value) {
        this.value = value;
    }

    public int getValue () {
        return value;
    }
}

enum DIRECTION {
    NORTH(10),
    EAST(1),

    SOUTH(-NORTH.value),
    WEST(-EAST.value),

    NE(NORTH.value + EAST.value),
    SE(SOUTH.value + EAST.value),
    NW(-SE.value),
    SW(-NE.value);


    int value;
    DIRECTION(int value) {
        this.value = value;
    }

    public int getValue () {
        return value;
    }
}

enum C_PERM {

    WQC(1),
    WKC(2),
    BQC(4),
    BKC(8),
	
	WSC(WQC.value | WKC.value),
	BSC(BQC.value | BKC.value),
	
	KSC(WKC.value | BKC.value),
	QSC(WQC.value | BQC.value);

    int value;
    C_PERM(int value) {
        this.value = value;
    }

}

enum NODE_TYPE{
	PV_NODE(0),
	ALL_NODE(1),
	CUT_NODE(2);
	
	
	
    int value;
	NODE_TYPE(int value) {
        this.value = value;
    }
}
