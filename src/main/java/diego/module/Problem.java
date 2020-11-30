package diego.module;
import com.alibaba.fastjson.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Problem  {
    public int type = 4;
    public String problem;
    public JSONObject options;
    public String answer;
    private Connection dbConnection;

    public Problem(int type ,String problem,JSONObject options,String answer){
        this.type = type;
        this.problem = problem;
        this.options = options;
        this.answer = answer;
    }
    public Problem(){}

    public int getType() {
        return type;
    }
    public String getAnswer() {
        return answer;
    }
    public JSONObject getOptions() {
        return options;
    }
    public String getProblem() {
        return problem;
    }
    public void setDbConnection(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public Connection getDbConnection() {
        // 获取数据库的连接状态
        if (dbConnection == null){
            Db db = new Db();
            setDbConnection(db.getConnection());
        }
        return dbConnection;
    }
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    public void setOptions(JSONObject options) {
        this.options = options;
    }
    public void setProblem(String problem) {
        this.problem = problem;
    }
    public void setType(int type) {
        this.type = type;
    }

    public boolean checkFormat(){
        if(answer.length()<=0 || answer.length()>=50) return  false;
        if(answer == null) return false;
        return true;
    }
    public boolean insertProblemToDb() {
        try {
            if (getDbConnection() != null) {

                // 数据去重
                ResultSet res;
                String sql0 = "select 1 from ti_ku where problem like ? ";
                PreparedStatement pre = this.dbConnection.prepareStatement(sql0);
                pre.setString(1, "%" + getProblem() +"%");
                res = pre.executeQuery();
                while (res.next()){
                    return true;
                }

                //导入数据
                Integer resultSet;
                String sql = "insert into ti_ku(type,problem,options,answer)values(?,?,?,?)";
                pre = this.dbConnection.prepareStatement(sql);
                pre.setString(1, "" + getType());
                pre.setString(2, getProblem());
                pre.setString(3, JSONObject.toJSONString(getOptions()));
                pre.setString(4, getAnswer());
                resultSet = pre.executeUpdate();
                if (resultSet == 1) return true;
                return false;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }
    public Problem JsonObjectToProblem(JSONObject jsonObject){

        // 将jsonobject 转化为 problem 并检查合法性
        try {
            setProblem(jsonObject.getString("problem"));
            setOptions(JSONObject.parseObject(jsonObject.getString("options")));
            setAnswer(jsonObject.getString("answer"));
            if(!checkFormat()){
                return null;
            }
        }catch (Exception e){
          e.printStackTrace();
        }
        return this;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        getDbConnection().close();
    }
}

