package diego.module;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import com.alibaba.fastjson.JSONObject;

public class JsonFile {
    private String filename ;
    private JSONObject[] JsonFileParseResult;

    public JsonFile(){}
    public JsonFile(String filename){this.filename = filename;}

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setJsonFileParseResult(JSONObject[] jsonFileParseResult) {
        JsonFileParseResult = jsonFileParseResult;
    }

    public String getFilename() {
        return filename;
    }
    public JSONObject[] getJsonFileParseResult() {
        return JsonFileParseResult;
    }

    public boolean JsonFileParse(String filename){
        try{
            Path file = Paths.get(filename);
            List<String> allLines = Files.readAllLines(file, StandardCharsets.UTF_8);
            JSONObject[] tmpJsonObject = new JSONObject[allLines.size()];
            this.JsonFileParseResult = tmpJsonObject;
            Integer count = 0;
            for(String line :allLines){
                this.JsonFileParseResult[count] = JSONObject.parseObject(line);
                count ++;
            }
            return true;
        }catch(IOException e){
            return false;
        }
    }

    public boolean JsonFileParse(){

        //处理结果为 一行一个 JSONObject
        try{
            Path file = Paths.get(this.filename);
            List<String> allLines = Files.readAllLines(file, StandardCharsets.UTF_8);
            JSONObject[] tmpJsonObject = new JSONObject[allLines.size()];
            this.JsonFileParseResult = tmpJsonObject;
            Integer count = 0;
            for(String line :allLines){
                this.JsonFileParseResult[count] = JSONObject.parseObject(line);
                count ++;
            }
            return true;
        }catch(IOException e){
            return false;
        }
    }

    public String JSONObjectArrayInsertDb(JSONObject[] jsonObjects){

        Db db = new Db();
        Connection connection =db.getConnection();
        ResultSet res;
        String sql0 = "select 1 from ti_ku where problem like ? ";


        String chongFuIndex = "";
        try {
            // 元素去重 以及构造insert 语句
            PreparedStatement pre = connection.prepareStatement(sql0);
            String sql = "insert into ti_ku(type,problem,options,answer)values";
            Integer count = 0;
            Integer count1 = -1;
            for (JSONObject jsonObject : jsonObjects){
                Integer congfu = 0;
                count1++;
                pre.setString(1,jsonObject.getString("problem"));
                res = pre.executeQuery();
                while (res.next()){ congfu =1;break; }
                if(congfu ==1){chongFuIndex += ","+count1 ;continue;}
                else {

                    if(jsonObject != null){
                        if(count == 0) sql = sql + "(4,?,?,?)";
                        else sql = sql + ",(4,?,?,?)";
                    }
                    count++;
                }
            }

            // 数据导入数据库
            PreparedStatement pre1 = connection.prepareStatement(sql);
            String[] chongFuIndexArray = chongFuIndex.split(",");
            count1 = -1;
            count = 0;
            for(JSONObject jsonObject :jsonObjects){
                count1++;
                if(Arrays.asList(chongFuIndexArray).contains(""+count1)){
                    continue;
                }else{
                    pre1.setString(count+1,jsonObject.getString("problem"));
                    pre1.setString(count+2,jsonObject.getString("options"));
                    pre1.setString(count+3,jsonObject.getString("answer"));
                    count += 3;
                }
            }
            Integer ress = pre1.executeUpdate();
            if(ress != null)return "true";
            else return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
