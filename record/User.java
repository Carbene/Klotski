package record;

import view.Level;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

/**
 * 用户类，所有需要验证的方法都基于这里实现
 */
public class User implements Serializable {
    private final String id;
    private final String password;
    private int[][] bestRecord;
    private static final String DIRECTORY = "./userInfo";
    private int userSymbol = 0;
    /**
     * 有参构造器，构造的是正常的用户，并会本地建立副本
     * @param id 用户名
     * @param password 用户密码
     */
    public User(String id, String password) {
        this.id = id;
        this.password = password;
        this.userSymbol = 1;
        this.bestRecord = new int[Level.values().length][2];
        User.serialize(this);
    }

    /**
     * 无参构造器，构造的是游客，之后不应被调用
     */
    public User(){
        this.id = "Visitor";
        this.password = null;
        this.userSymbol = 0;
        User.serialize(this);

    }

    /**
     * 序列化对象，本地保存信息
     * @param user 需要序列化的用户信息
     */
    public static void serialize(User user) {
        try {
            if(!new File(User.DIRECTORY).exists()) {
                new File(User.DIRECTORY).mkdirs();
            }
            FileOutputStream fileOut = new FileOutputStream(User.DIRECTORY+File.separator+user.id);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(user);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 此方法用于判定是否为游客
     */
    public int getUserSymbol() {
        return userSymbol;
    }

    /**
     * 反序列化，读取本地的信息
     * @param id
     * @return 匹配的用户对象
     */
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 获取本地存储的左右的用户信息
     * @return 本地的用户对象列表
     */
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

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getId() {
        return id;
    }

    /**
     * 获取用户密码
     * @return 用户密码
     */
    public String getPassword() {
        return password;
    }


    /**
     * 获取一个用户的最好的某关卡的某类乘积
     * @param user 目标用户
     * @param level 对应等级
     * @param mode 具体模式
     * @return 成绩数值（步数或时间）
     */
    public static int getBestRecord(User user,int level, int mode) {
        return user.bestRecord[level - 1][mode];
    }


    /**
     * 更新最好成绩
     * @param level 对应地图
     * @param user 目标用户
     * @param mode 对应模式
     * @param achievement 新的成就
     */
    public static void setBestRecord(Level level, User user, int mode, int achievement){
        user.bestRecord[level.getCODE() - 1][mode] = achievement;
        User.serialize(user);
    }

    /**
     * 判断注册用户合不合法
     * @param id 用户名是否重复或为空
     * @param password 密码是否为空
     * @return
     */
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
