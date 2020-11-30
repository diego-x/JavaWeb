package diego.server;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

public class ShowIndex extends HttpServlet {

    private static String pageError = "<script>alert('请先登录 ！！');</script>";
    private static String pageRedirect = "<script>location.href='/login.jsp';</script>";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        super.doGet(request, response);

        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        HttpSession session = request.getSession();
        PrintWriter writer  = response.getWriter();

        if((boolean)session.getAttribute("isLogin") != true){
            writer.println(pageError);
            writer.println(pageRedirect);
        }else{
            FileInputStream inputStream = new FileInputStream("/");
        }

    }
}
