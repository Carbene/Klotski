package view;

/**
 * 枚举常数类，记录模板与预览图（还没截图）
 */
public enum Level {
    LEVEL1(
            new int[][]{
                    {3, 4, 4, 3},
                    {3, 4, 4, 3},
                    {3, 2, 2, 3},
                    {3, 1, 1, 3},
                    {1, 0, 0, 1}
            },1,"src/frame/theme/1.jpg"
    ),
    LEVEL2(
            new int[][]{
                    {3, 4, 4, 3},
                    {3, 4, 4, 3},
                    {1, 2, 2, 1},
                    {3, 1, 1, 3},
                    {3, 0, 0, 3}
    },2,"src/frame/theme/2.jpg"),
    LEVEL3(
            new int[][]{
                    {0, 4, 4, 0},
                    {3, 4, 4, 3},
                    {3, 3, 3, 3},
                    {1, 3, 3, 1},
                    {2, 2, 1, 1}
    },3,"src/frame/theme/3.jpg"),
    LEVEL4(
            new int[][]{
                    {3, 4, 4, 3},
                    {3, 4, 4, 3},
                    {1, 1, 1, 1},
                    {3, 2, 2, 3},
                    {3, 0, 0, 3}
    },4,"src/frame/theme/4.jpg"),
    LEVEL5(
            new int[][]{
                    {1, 4, 4, 1},
                    {3, 4, 4, 3},
                    {3, 0, 0, 3},
                    {3, 0, 0, 3},
                    {3, 0, 0, 3}
    },5,"src/frame/theme/5.jpg");

    private final int[][] MAP;
    private final int CODE;
    private final String PREVIEW_PIC_PATH;

    /**
     * 枚举常量的构造方法
     * @param MAP 地图的数组表示
     * @param code 底层的地图代码
     * @param previewPicPath 预览图的地址
     */
    private Level(int[][] MAP,int code, String previewPicPath) {
        this.MAP = MAP;
        this.CODE = code;
        this.PREVIEW_PIC_PATH = previewPicPath;
    }

    /**
     * 获得地图，便于进行初始化
     * @return 地图的数组表示
     */
    public int[][] getMAP() {
        return MAP;
    }

    /**
     * 获得地图的宽度
     * @return 4
     */
    public int getWidth() {
        return MAP[0].length;
    }

    /**
     * 获得地图的高度
     * @return 5
     */
    public int getHeight() {
        return MAP.length;
    }

    /**
     * 返回地图代码
     * @return 代码
     */
    public int getCODE() {
        return CODE;
    }

    public static Level getLevel(int CODE) {
        for (Level level : Level.values()) {
            if (level.getCODE() == CODE) {
                return level;
            }
        }
        return null;
    }
}
