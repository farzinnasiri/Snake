package snake;

import javax.swing.*;
import java.awt.*;

public class Snake {
    public static void main(String[] args){
        JFrame frame = new JFrame("Snake");
        frame.setContentPane(new GamePanel());


        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setPreferredSize(new Dimension(GamePanel.WIDTH, GamePanel.HEIGHT));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }
}
