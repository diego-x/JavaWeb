package Filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter("/*")
public class EncodeFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        //System.out.println("EncodeFilter");
/*
        String name = request.getParameter("name");
        if(name.contains("flag")){
            System.out.println("hack");

        }else{
            filterChain.doFilter(request,response);
        }*/

            filterChain.doFilter(request,response);
    }

}
