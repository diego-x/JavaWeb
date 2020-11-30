package diego.server;

import com.alibaba.fastjson.JSONObject;
import diego.module.JsonFile;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import diego.module.User;


@WebServlet("/FileUpload")
@SuppressWarnings("unchecked")
public class FileUpload  extends HttpServlet {

    private static final long seriaVersionUID = 1L;
    private static final String upload_dir = "G:\\app-data\\idea\\servlet_1\\src\\main\\webapp\\static\\uploads\\";
    private static final Integer memory_threshold =1024 * 1024 * 3;
    private static final Integer max_file_size = 1024 * 1024 * 30;
    private static final Integer max_request_size = 1024 * 1024 *40;

    public static final String pageError1 = "类型错误！";
    public static final String pageError2 = "未登录！！";
    public static final String pageError3 = "无可处理的文件！！";

    private boolean CheckFile(String filename){
        String[] res = filename.split("\\.");
        if(res[res.length -1].equals("json"))return true;
        return false;
    }

    private boolean UploadInit(DiskFileItemFactory diskFileItemFactory,ServletFileUpload servletFileUpload){
        diskFileItemFactory.setSizeThreshold(memory_threshold);
        diskFileItemFactory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        servletFileUpload.setFileSizeMax(max_file_size);
        servletFileUpload.setSizeMax(max_request_size);
        servletFileUpload.setHeaderEncoding("UTF-8");
        return  true;
    }

    private boolean FileUpload(List<FileItem> fileItems,User user, HttpServletRequest request) throws Exception {
        for(FileItem item :fileItems){
            String filename = new File(item.getName()).getName();
            if(CheckFile(filename)) {
                String filepath = upload_dir + '\\' + user.getUploadPath() + "\\" + filename;
                HttpSession  session = request.getSession();
                session.setAttribute("filepath",filepath);
                File storeFile = new File(filepath);
                item.write(storeFile);
            }else{
                return false;
            }
        }
        return true;
    }

    private String getParseResult(HttpServletRequest request){

        // 将上传的文件
        HttpSession session = request.getSession();
        JsonFile jsonFile  = new JsonFile((String)session.getAttribute("filepath"));
        if(jsonFile.JsonFileParse()==false) return null;
        JSONObject[] jsonObjects = jsonFile.getJsonFileParseResult();
        session.setAttribute("JSONObjectArray",jsonObjects);

        // 对上传的文件进行解析并 以html形式返回
        String result = "<center><button class=\"layui-btn\" lay-submit onclick=\"location.href='/JsonFileToDb'\">确认提交</button></center>" +
                "<br><center><font size=\"6\" color=\"green\">解析结果</font></center>";
        Integer count = 0;
        for(JSONObject jsonObject: jsonObjects ){
            if(count > 100) {result += "<font size=\"5\" color=\"red\">最多显示100条</font>";break;}
            result += "<font size=\"4\" color=\"blue\">&nbsp;&nbsp;&nbsp;&nbsp;题目 ：" + jsonObject.getString("problem") + "</font><br><br>";
            result += "&nbsp;&nbsp;&nbsp;&nbsp;选项 :" + jsonObject.getString("options") + "<br><br>";
            result +=  "&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"4\" color=\"red\">答案：" + jsonObject.getString("answer") + "</font><br><br>";
            count++;
        }
        return  result.replace("\"","\\\"");
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //super.doPost(request, response);

        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = response.getWriter();
        HttpSession session;

        // 检测用户是否为空
        try {
            session  = request.getSession();
        }catch (Exception e){
            writer.println(pageError2);
            writer.println("<script>location.href='/login.jsp';</script>");
            writer.flush();
            return;
        }

        if(session.getAttribute("isLogin") == null
                || (boolean)session.getAttribute("isLogin") != true){
            writer.println("{\"code\":1,\"msg\": \""+pageError2+"\"}");
            writer.flush();
        }else{
            // 类型检测
            if (!ServletFileUpload.isMultipartContent(request)){
                writer.println("{\"code\":1,\"msg\": \""+pageError1+"\"}");
                writer.flush();
            }else{
                DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
                ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);
                UploadInit(fileItemFactory,servletFileUpload);

                User user = (User)session.getAttribute("userObject");
                File file = new File(upload_dir + "\\" + user.getUploadPath());
                if (!file.exists()){
                    file.mkdir();
                }
                try {
                    // 尝试文件上传
                    List<FileItem>  fromItems = servletFileUpload.parseRequest(request);
                    if(fromItems != null && fromItems.size()>0){
                        if(FileUpload(fromItems,user,request) == false) {
                            writer.println("{\"code\":1,\"msg\": \"文件只允许json后缀 或 内部错误！！\"}");
                            writer.flush();
                        }else{
                            // 上传后解析
                            String parseResult = getParseResult(request);
                            if(parseResult == null){
                                writer.println("{\"code\":1,\"msg\": \"解析错误\"}");
                                writer.flush();
                            }else{
                                writer.println("{\"code\":0,\"msg\": \""+ parseResult + "\"}");
                                writer.flush();
                            }
                        }
                    }else{
                        writer.println("{\"code\":1,\"msg\": \""+pageError3+"\"}");
                        writer.flush();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //super.doGet(request, response);
        PrintWriter writer = response.getWriter();
        writer.println("<script>location.href='/fileUpload.jsp';</script>");
        writer.flush();

    }
}
