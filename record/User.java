package record;

import view.Level;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class User implements Serializable {
    private final String id;
    private final String password;
    private int[][] bestRecord;
    private static final String DIRECTORY = "src/record/userInfo";

    public User(String id, String password) {
        this.id = id;
        this.password = password;
        this.bestRecord = new int[Level.values().length][2];
        User.serialize(this);
    }

    public User(){
        this.id = "Visitor";
        this.password = null;
        User.serialize(this);
    }

    public static void serialize(User user) {
        try {
            FileOutputStream fileOut = new FileOutputStream(User.DIRECTORY+File.separator+user.id);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(user);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User deserialize(String id){
        User user = null;
        String filePath = DIRECTORY + File.separator + id;
        File userFile = new File(filePath);
        if (!userFile.exists() || !userFile.isFile()) {
            System.err.println("File not found or is not a file: " + filePath);
            return null;
        }
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Object obj = in.readObject();
            if(obj instanceof User) {
                user = (User) obj;
            } else {
                System.err.println("Object read is not an instance of User: " + filePath);
                return null;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static ArrayList<User> deserializeList() {
        ArrayList<User> users = new ArrayList<>();
        File directory = new File(User.DIRECTORY);
        File[] fileList = directory.listFiles();
        if (fileList == null) {
            return users;
        }

        for (File userFile : fileList) {
            if (userFile.isFile()) {
                User deserializedUser = deserialize(userFile.getName());
                if (deserializedUser != null) {
                    users.add(deserializedUser);
                }
            }
        }
        return users;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public static int getBestRecord(User user,int level, int mode) {
        return user.bestRecord[level - 1][mode];
    }

    public static void setBestRecord(Level level, User user, int mode, int achievement){
        user.bestRecord[level.ordinal()][mode] = achievement;
    }

    public static boolean registerUser(String id,String password) {
        if(id == null || password == null || id.isEmpty() || password.isEmpty()) {
            return false;
        }
        for(User user : User.deserializeList()) {
            if(user.getId().equals(id)) {
                JOptionPane.showMessageDialog(null, "Username has already been taken, please rethink one!");
                return false;
            }
        }
        User user = new User(id, password);
        return true;
    }
}
