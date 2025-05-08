package view;

public enum Leveled {
    LEVEL1(
            new int[][]{
                    {2, 2, 2, 2, 1},
                    {4, 4, 3, 0, 1},
                    {4, 4, 3, 1, 0},
                    {2, 2, 2, 2, 1}
            }
            ,
            "横刀立马"
    );

    private final int[][] MAP;
    private final String CHINESENAME;

    private Leveled(int[][] MAP, String CHINESENAME) {
        this.MAP = MAP;
        this.CHINESENAME = CHINESENAME;
    }

    public int[][] getMAP() {
        return MAP;
    }

    public int getWidth() {
        return MAP[0].length;
    }

    public int getHeight() {
        return MAP.length;
    }

    public String getCHINESENAME() {
        return CHINESENAME;
    }

    public boolean checkInWidthSize(int col) {
        return col >= 0 && col < getWidth();
    }

    public boolean checkInHeightSize(int row) {
        return row >= 0 && row < getHeight();
    }

}
