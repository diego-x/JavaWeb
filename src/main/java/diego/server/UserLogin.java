package diego.server;

import diego.module.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet("/UserLogin")
public class UserLogin  extends HttpServlet  {

    public static String pageResError = "<script>alert('用户名与密码不能为空！！')</script>";
    public static String pageResError1 = "<script>alert('登入失败！！')</script>";
    public static String pageResOK = "<script>alert('登陆成功！！')</script>";



    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //super.doPost(request, response);

        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        HttpSession session  = request.getSession();
        PrintWriter writer = response.getWriter();


        if (request.getParameter("username").equals("") || request.getParameter("password").equals("") ||
        request.getParameter("username") == null || request.getParameter("password") == null
        ){

            String pageResError   = new String();
            writer.println(pageResError);
            writer.println("<script>location.href='/login.jsp';</script>");
            writer.flush();

        }else{
            User user = new User();
            Boolean loginStatus =  user.doUserLogin(request.getParameter("username"),request.getParameter("password"));
            if(loginStatus == false){
                writer.println(pageResError1);
                writer.println("<script>location.href='/login.jsp';</script>");
                writer.flush();
            }else{

                session.setAttribute("isLogin",true);
                session.setAttribute("id",user.getId());
                session.setAttribute("username",user.getUsername());
                session.setAttribute("userObject",user);
                //request.setAttribute("user",user);
                //request.getRequestDispatcher("index.jsp").forward(request,response);
                writer.println(pageResOK);
                writer.println("<script>location.href='/index.jsp';</script>");
                writer.flush();
            }
        }
    }
}
