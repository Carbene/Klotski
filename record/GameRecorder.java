package record;

import view.Direction;
import view.BoxComponent;

import javax.swing.*;
import java.io.*;
import java.util.Stack;

public class GameRecorder implements Serializable {

    //this class is a utility class, providing basic methods to record and withdraw, also save and read game

    private final Stack<Move> MOVES;
    private final int[][] INITIAL_MAP;
    private int[][] finalMap;
    private User user;

    public GameRecorder(int[][] map, User owner) {

        this.MOVES = new Stack<Move>();
        this.INITIAL_MAP = map;
        this.user = owner;

    }

    public void record(BoxComponent selectedBoxComponent, Direction direction) {

        MOVES.push(new Move(selectedBoxComponent, direction));

    }

    public Stack<Move> getMoves() {
        return MOVES;
    }

    public void saveGame(int[][] map, String Directory,String filename) {

        this.finalMap = map;

        try {
            FileOutputStream fos = new FileOutputStream(Directory+ "\\" +filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }

    }

    public static GameRecorder loadGame(String Directory, GameRecorder save) {

        if (save != null) {

            if(JOptionPane.showConfirmDialog(null,"Do you really want to start a new game?","Warning",JOptionPane.YES_NO_OPTION) == 0){

                try {
                    FileInputStream fis = new FileInputStream("Directory");
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    save = (GameRecorder) ois.readObject();
                    ois.close();
                    fis.close();
                } catch (IOException i) {
                    i.printStackTrace();
                } catch (ClassNotFoundException c) {
                    System.out.println("Employee class not found");
                    c.printStackTrace();
                }

            }

        }else{

            try {
                FileInputStream fis = new FileInputStream("Directory");
                ObjectInputStream ois = new ObjectInputStream(fis);
                save = (GameRecorder) ois.readObject();
                ois.close();
                fis.close();
            } catch (IOException i) {
                i.printStackTrace();
            } catch (ClassNotFoundException c) {
                System.out.println("Employee class not found");
                c.printStackTrace();
            }

        }

        return save;

    }

    public User getUser() {
        return user;
    }
}
