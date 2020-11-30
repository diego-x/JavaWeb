package diego.module;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {

    private String url;
    private String dbname;
    private String dbpwd;
    private Connection con;

    public Db(){
        this.url = "jdbc:mysql://localhost:3306/java_web?autoReconnect=true&amp;failOverReadOnly=false";
        this.dbname = "root";
        this.dbpwd = "password";
    }

    public Connection getConnection(){

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url,dbname,dbpwd);
        }catch (Exception e){
            e.printStackTrace();
        }
        return this.con;
    }

    public void close(){
        try {
            con.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
