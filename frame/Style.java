package frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 这是一个工具类，用于美化我们的按钮，并实现风格统一
 */
public class Style {

    /**
     * 对按钮对象进行美化，色彩参数等
     * @param button 需要美化的对象
     */
    public static void styleBtn(JButton button) {

        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBackground(new Color(50, 50, 100, 200));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 200)),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.setBackground(new Color(80, 80, 130, 220)); }
            @Override
            public void mouseExited(MouseEvent e) { button.setBackground(new Color(50, 50, 100, 200)); }
        });

    }

    /**
     * 设置Label的相关属性，字体颜色等
     * @param label 需要美化的对象
     */
    public static void styleLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setBorder(new EmptyBorder(2, 3, 0, 0));
    }

}
