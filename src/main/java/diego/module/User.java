package diego.module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.codec.digest.DigestUtils;

public class User implements Serializable {

    private String username;
    private Integer id;
    private Integer searchCount;
    private Connection dbConnection;
    private String uploadPath;

    public User(Integer id, String username){
        this.id = id;
        this.username = username;
    }

    public User(){}
    public void setId(Integer id) {
        this.id = id;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setSearchCount(Integer searchCount) {
        this.searchCount = searchCount;
    }
    public void setDbConnection(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }
    public void setUploadPath(String upload_path) {
        this.uploadPath = upload_path;
    }


    public Connection getDbConnection() {
        if (dbConnection == null){
            Db db = new Db();
            setDbConnection(db.getConnection());
        }
        return dbConnection;
    }
    public Integer getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public Integer getSearchCount() {
        if(searchCount == null ){
            try {
                ResultSet res;
                if(getDbConnection() != null){
                    String sql = "select count from user_search_count where id = ?";
                    PreparedStatement pre = this.dbConnection.prepareStatement(sql);
                    pre.setString(1,"" + this.id);
                    res = pre.executeQuery();
                    while (res.next()){
                        Integer countTemp = Integer.parseInt(res.getString("count"));
                        if(countTemp != null){
                            setSearchCount(countTemp);
                            break;
                        }
                    }
                }
            }catch (SQLException e){
             e.printStackTrace();
            }
        }
        return searchCount;
    }
    public String getUploadPath() {

        // 获取用户的 上传路径
        if(uploadPath == null ){
            try {
                ResultSet res;
                if(getDbConnection() != null){
                    String sql = "select upload_path from users where id = ?";
                    PreparedStatement pre = this.dbConnection.prepareStatement(sql);
                    pre.setString(1,"" + this.id);
                    res = pre.executeQuery();
                    while (res.next()){
                        String pathTemp = res.getString("upload_path");
                        if(pathTemp != null){
                            setUploadPath(pathTemp);
                            break;
                        }
                    }
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return uploadPath;
    }

    public Boolean doUserLogin(String username , String password){
        try {
            if(getDbConnection() != null ){
                ResultSet res ;
                String sql = "select * from users where username = ? and password = ?";
                PreparedStatement pre = this.dbConnection.prepareStatement(sql);
                pre.setString(1,username);
                pre.setString(2,password);
                res = pre.executeQuery();

                while (res.next()){
                    setId(Integer.parseInt(res.getString("id")));
                    setUsername(res.getString("username"));
                    break;
                }
                if(getId() == null || getId() ==0){
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
