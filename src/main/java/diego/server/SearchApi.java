package diego.server;

import com.alibaba.fastjson.JSONObject;
import diego.module.Db;
import diego.module.Problem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/SearchApi")
public class SearchApi extends HttpServlet {

    private Connection dbConnection;


    public void setDbConnection(Connection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public Connection getDbConnection() {
        if (dbConnection == null){
            Db db = new Db();
            setDbConnection(db.getConnection());
        }
        return dbConnection;
    }

    public Problem[] getSearchResult(String q){
        try {
            ResultSet res;
            if(getDbConnection() != null){
                String sql = "select * from ti_ku where problem like ?";
                PreparedStatement pre = this.dbConnection.prepareStatement(sql);
                pre.setString(1,"%"+q+"%");
                res = pre.executeQuery();
                Problem[] problems = new Problem[8];
                Integer count = 0;
                while (res.next()) {
                    if(count==8)break;
                    String problem = res.getString("problem");
                    String options = res.getString("options");
                    String answer = res.getString("answer");
                    Integer type = res.getInt("type");
                    Problem problem1 = new Problem(type, problem,
                            JSONObject.parseObject(options), answer);
                    problems[count] = problem1;
                    count++;
                }
                return problems;
            }
            return null;
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public String ProblemsToString(Problem[] problems){
        Integer count =0;
        if(problems != null){

            String res ="{\"responseData\":{\"results\":[";
            for(Problem problem : problems){
                if (problem == null) break;
                if(count !=0)res +=",";
                res += "{\"GsearchResultClass\":\"xuanze\",\"problem\":\"" + problem.getProblem() +"\",";
                res += "\"options\":" + JSONObject.toJSONString(problem.getOptions()) + ",";
                res += "\"answer\":\"" + problem.getAnswer() + "\"}";
                count++;
            }
            res += "],\"cursor\":{\"estimatedResultCount\":"+count+"}}}";
            return res;
        }
        return null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        getDbConnection().close();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //super.doGet(req, resp);
        // 将处理的结果按照 如下json格式返回
        /* writer.println("{\"responseData\":{\"results\":[" +
        "{\"GsearchResultClass\":\"GnewsSearch\",\"title\":\"aaaa\",\"publisher\":\"bbbbbb\"}," +
        "{\"GsearchResultClass\":\"GnewsSearch\",\"title\":\"cccc\",\"publisher\":\"dddddd\"}" +
         "],\"cursor\":{\"estimatedResultCount\":2}}}");*/


        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = response.getWriter();
        HttpSession session;

        try {
            session  = request.getSession();
        }catch (Exception e){
            writer.println("{\"responseData\":{\"results\":[" +
                    "{\"GsearchResultClass\":\"Error\",\"Url\":\"login.jsp\",\"errorMsg\":\"未登录，点击登录\"}" +
                    "],\"cursor\":{\"estimatedResultCount\":1}}}");
            writer.flush();
            return;
        }
        if(session.getAttribute("isLogin") == null
                || (boolean)session.getAttribute("isLogin") != true){
            writer.println("{\"responseData\":{\"results\":[" +
                    "{\"GsearchResultClass\":\"Error\",\"Url\":\"login.jsp\",\"errorMsg\":\"未登录，点击登录\"}" +
                    "],\"cursor\":{\"estimatedResultCount\":1}}}");
            writer.flush();
        }else{
            String q = request.getParameter("q");
            byte[] bytes=q.getBytes("ISO-8859-1");
            String qq=new String(bytes,"utf-8");
            Problem[] problems = getSearchResult(qq);
            String res = ProblemsToString(problems);
            writer.println(res);
            writer.flush();
        }
    }
}
