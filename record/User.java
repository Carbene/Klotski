package record;

import view.Leveled;

import java.io.*;
import java.util.ArrayList;

public class User implements Serializable {
    private final String id;
    private final String password;
    private int[][] BestRecord;

    public User(String id, String password) {
        this.id = id;
        this.password = password;
        User.serialize(this,"path/to/save");
        this.BestRecord = new int[Leveled.values().length][2];
    }

    public User(){
        this.id = null;
        this.password = null;
        User.serialize(this,"path/to/save");
    }

    public static void serialize(User user,String path) {
        try {
            FileOutputStream fileOut = new FileOutputStream("object.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(user);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User deserialize(String path){
        User user = null;
        try {
            FileInputStream fileIn = new FileInputStream("object.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            if(in.readObject() instanceof User) {
                user = (User) in.readObject();
            }else{
                return null;
            }
            in.close();
            fileIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static ArrayList<User> deserializeList(String path) {
        ArrayList<User> users = new ArrayList<User>();
        try {
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            File file = new File(path);
            File[] fileList = file.listFiles();
            if(fileList == null) {
                return null;
            }else {
                for (File user : fileList) {
                    users.add(deserialize(user.getAbsolutePath()));
                }
            }
            fileIn.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
    }
        return users;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public int[][] getBestRecord() {
        return BestRecord;
    }

    public static void updateBestRecord(Leveled level, User user,int mode,int achievement){

        user.BestRecord[level.ordinal()][mode] = achievement;

    }

    public static int getBestRecord(Leveled level, User user,int mode){

        return user.BestRecord[level.ordinal()][mode];

    }
}
