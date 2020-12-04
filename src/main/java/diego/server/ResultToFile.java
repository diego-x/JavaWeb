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
import java.util.*;

@WebServlet("/ResultToFile")
public class ResultToFile extends HttpServlet {

    private List<String> getJSONObjectFromDb() throws SQLException {

        //JSONObject[] jsonObjects ;
        List<String> list = new ArrayList<>();
        Map<Object,Object> tmpProblem = new HashMap<>();
        Db db= new Db();
        Connection conn =  db.getConnection();
        if(conn != null){
            String sql = "select problem,options,answer from ti_ku";
            PreparedStatement pre = conn.prepareStatement(sql);
            ResultSet res = pre.executeQuery();
            while(res.next()){
                tmpProblem.put("problem",res.getString("problem"));
                tmpProblem.put("answer",res.getString("answer"));
                tmpProblem.put("options",res.getString("options"));
                list.add(JSONObject.toJSONString(tmpProblem));
            }
            return list;
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = response.getWriter();
        HttpSession session;

        try {
            session  = request.getSession();
        }catch (Exception e){
            response.sendRedirect("/login.jsp");
            return;
        }
        if(session.getAttribute("isLogin") == null
                || (boolean)session.getAttribute("isLogin") != true){
            response.sendRedirect("/login.jsp");
        }else{
            try {
                response.setContentType("application/octet-stream");
                response.setHeader("content-disposition", "attachment;filename=db.json");

                List<String> list = getJSONObjectFromDb();
                for (String string :list){
                    writer.println(string);
                }
            } catch (SQLException e) {
                writer.println("<script>alert('服务器异常')</script>");
            }
        }

    }
}
