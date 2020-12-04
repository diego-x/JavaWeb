package diego.server;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/ShowResult")
public class ShowResult extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //super.doGet(request, response);

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

            JSONObject[] jsonObjects = (JSONObject[]) session.getAttribute("JSONObjectArray");
            if( jsonObjects!= null){
                writer.println("<title>解析结果</title>");
                writer.println(CatchFromWangKeBang.getParseResult(jsonObjects,300));
                writer.println("<buttun  type=\"button\" onclick=\"location.href='/JsonFileToDb'\">导入数据库</buttun>");
                writer.flush();
            }else{
                writer.println("<script>alert('没有爬取结果！！')</script>");
                writer.println("<script>location.href='/wkbCatch';</script>");
                writer.flush();
            }
        }

    }
}
