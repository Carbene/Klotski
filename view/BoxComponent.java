package view;

import frame.GameFrame;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class BoxComponent extends JComponent {
    private Color color;
    private int row;
    private int col;
    private boolean isSelected;
    private GameFrame owner;
    public static final int GRIDSIZE = 100;
    private int type;


    public BoxComponent(Color color, int row, int col, int type,GameFrame gameFrame) {
        this.color = color;
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

    public BoxComponent(GameFrame gameFrame) {
        this.color = null;
        this.row = 0;
        this.col = 0;
        isSelected = true;
        setEnabled(false);
        this.owner = gameFrame;

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
        Border border;
        if (isSelected) {
            border = BorderFactory.createLineBorder(Color.red, 3);
        } else {
            border = BorderFactory.createLineBorder(Color.darkGray, 1);
        }
        this.setBorder(border);
        this.setOpaque(true);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        this.repaint();
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

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

    public void setOwner(GameFrame owner) {
        this.owner = owner;
    }

    public int getType() {
        return type;
    }




}

