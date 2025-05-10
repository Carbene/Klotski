package view;

public enum Level {
    LEVEL1(
            new int[][]{
                    {2, 2, 2, 2, 1},
                    {4, 4, 3, 1, 0},
                    {4, 4, 3, 1, 0},
                    {2, 2, 2, 2, 1}
            },1,"src/frame/theme/1.jpg"
    ),
    LEVEL2(
            new int[][]{
                    {2, 2, 1, 2, 2},
                    {4, 4, 3, 1, 0},
                    {4, 4, 3, 1, 0},
                    {2, 2, 1, 2, 2}
    },2,"src/frame/theme/2.jpg"),
    LEVEL3(
            new int[][]{
                    {0, 2, 2, 1, 1},
                    {4, 4, 2, 2, 1},
                    {4, 4, 2, 2, 3},
                    {0, 2, 2, 1, 3}
    },3,"src/frame/theme/3.jpg"),
    LEVEL4(
            new int[][]{
                    {2, 2, 1, 2, 2},
                    {4, 4, 1, 3, 0},
                    {4, 4, 1, 3, 0},
                    {2, 2, 1, 2, 2}
    },4,"src/frame/theme/4.jpg"),
    LEVEL5(
            new int[][]{
                    {1, 2, 2, 2, 2},
                    {4, 4, 3, 1, 0},
                    {4, 4, 3, 1, 0},
                    {1, 2, 2, 2, 2}
    },5,"src/frame/theme/5.jpg");

    private final int[][] MAP;
    private final int CODE;
    private final String PREVIEW_PIC_PATH;


    private Level(int[][] MAP,int code, String previewPicPath) {
        this.MAP = MAP;
        this.CODE = code;
        this.PREVIEW_PIC_PATH = previewPicPath;
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

    public boolean checkInWidthSize(int col) {
        return col >= 0 && col < getWidth();
    }

    public boolean checkInHeightSize(int row) {
        return row >= 0 && row < getHeight();
    }

    public int getCODE() {
        return CODE;
    }

    public String getPreviewPicPath() {
        return PREVIEW_PIC_PATH;
    }

}
