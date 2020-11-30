package diego.server;

import com.alibaba.fastjson.JSONObject;
import diego.module.JsonFile;
import diego.module.Problem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/JsonFileToDb")
public class JsonFileToDb extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //super.doPost(req, resp);
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = response.getWriter();
        HttpSession session;

        try {
            session  = request.getSession();
        }catch (Exception e){
            writer.println("<script>alert('未登录！！')</script>");
            writer.println("<script>location.href='/login.jsp';</script>");
            writer.flush();
            return;
        }
        if(session.getAttribute("isLogin") == null
                || (boolean)session.getAttribute("isLogin") != true){
            writer.println("<script>alert('未登录！！')</script>");
            writer.println("<script>location.href='/login.jsp';</script>");
            writer.flush();
        }else{

            JSONObject[] jsonObjects = (JSONObject[]) session.getAttribute("JSONObjectArray");
            JsonFile jsonFile = new JsonFile();
            String res = jsonFile.JSONObjectArrayInsertDb(jsonObjects);

            if(res == null){
                writer.println("<script>alert('解析错误！！')</script>");
                writer.println("<script>location.href='/fileUpload.jsp';</script>");
                writer.flush();
            }else{
                if(res == "true"){
                    session.setAttribute("JSONObjectArray","");
                    writer.println("<script>alert('导入成功！！')</script>");
                    writer.println("<script>location.href='/fileUpload.jsp';</script>");
                    writer.flush();
                }else{
                    writer.println("<script>alert('导入失败！！')</script>");
                    writer.println("<script>location.href='/fileUpload.jsp';</script>");
                    writer.flush();
                }
            }

        }
    }
}
