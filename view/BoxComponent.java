package view;

import frame.GameFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * 这是方块的视图类，用于生成具体的板块
 */
public class BoxComponent extends JComponent {
    private Image image;
    private int row;
    private int col;
    private boolean isSelected;
    private GameFrame owner;
    public static final int GRIDSIZE = 100;
    private int type;

    /**
     * 一个有参构造器，构造所需的方块
     * @param imagePath 当前图片路径
     *              TODO: 也许需要更新为全新的类似于材质或者图片的东西
     * @param row 当前坐标
     * @param col 当前坐标
     * @param type 类型
     *             TODO: 新的障碍物类（也有bonus)
     * @param gameFrame 归属的游戏框架
     */
    public BoxComponent(String imagePath, int row, int col, int type,GameFrame gameFrame) {
        this.image=getImageFromPath(imagePath);
        this.row = row;
        this.col = col;
        this.type = type;
        isSelected = false;
        setEnabled(true);
        setOpaque(false);
        this.owner = gameFrame;
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e){

                doSelect();

            }

        });
    }

    /**
     * 构造一个占位的方块，用于选择方块时占位
     * @param gameFrame 归属的框架
     */
    public BoxComponent(GameFrame gameFrame) {
        this.image=null;
        this.row = 0;
        this.col = 0;
        isSelected = true;
        setEnabled(false);
        this.owner = gameFrame;

    }
    /**
     *通过路径向容器添加图片
     */
    public Image getImageFromPath(String imagePath){
        try {
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl == null) {
                throw new IOException("Image not found: " + imagePath);
            }
            Image originalImage = ImageIO.read(imageUrl);
            return originalImage.getScaledInstance(
                    getWidthByType(type),
                    getHeightByType(type),
                    Image.SCALE_SMOOTH
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getHeightByType(int type) {
        switch (type){
            case 1: return GRIDSIZE;
            case 2: return GRIDSIZE;
            case 3: return GRIDSIZE * 2;
            case 4: return GRIDSIZE * 2;
            default: return GRIDSIZE;
        }
    }

    private int getWidthByType(int type) {
        switch (type) {
            case 1: return GRIDSIZE;
            case 2: return GRIDSIZE * 2;
            case 3: return GRIDSIZE;
            case 4: return GRIDSIZE * 2;
            default: return GRIDSIZE;
        }
    }
    /**
     * 重写的方法，进行绘制
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        if(image!=null){
            g.drawImage(image,0,0,getWidthByType(type), getHeightByType(type),this);
        }
        Border border;
        if (isSelected) {
            border = BorderFactory.createLineBorder(Color.WHITE, 5);
        } else {
            border = BorderFactory.createLineBorder(Color.darkGray, 1);
        }
        this.setBorder(border);
        this.setOpaque(true);
        /*super.paintComponent(g);
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
        Border border;
        if (isSelected) {
            border = BorderFactory.createLineBorder(Color.WHITE, 5);
        } else {
            border = BorderFactory.createLineBorder(Color.darkGray, 1);
        }
        this.setBorder(border);
        this.setOpaque(true);*/
    }

    /**
     * 切换选中状态
     * @param selected 是否被选中
     */
    public void setSelected(boolean selected) {
        isSelected = selected;
        this.repaint();
    }

    /**
     * 获得坐标
     * @return 坐标
     */
    public int getRow() {
        return row;
    }

    /**
     * 更换坐标
     * @param row 坐标
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * 获得坐标
     * @return 坐标
     */
    public int getCol() {
        return col;
    }

    /**
     * 更换坐标
     * @param col 坐标
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * 实现方块的选中
     */
    private void doSelect() {

        if(this.owner.getSelectedBox() != null && this.owner.getSelectedBox() != this) {

            this.owner.getSelectedBox().setSelected(false);
            this.owner.setSelectedBox(this);
            this.setSelected(true);

        }else if(this.owner.getSelectedBox() == this){

            this.setSelected(false);
            this.owner.setSelectedBox(this.owner.getBoxes().getLast());

        }else if(this.owner.getSelectedBox() == null){

            this.setSelected(true);
            this.owner.setSelectedBox(this);

        }

        this.owner.repaint();

    }

    /**
     * 获取方块类型
     * @return 类型代号
     */
    public int getType() {
        return type;
    }




}

