package diego.server;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@WebServlet("/CatchFromWKB")
public class CatchFromWangKeBang extends HttpServlet {

    private JSONObject[] jsonObjects;

    public JSONObject[] getJsonObjects() {
        return jsonObjects;
    }

    public void setJsonObjects(JSONObject[] jsonObjects) {
        this.jsonObjects = jsonObjects;
    }

    private boolean CheckUrl(String url1) throws MalformedURLException {

        URL url = new URL(url1);
        try {
            if((url1.startsWith("https")||url1.startsWith("http")) &&
                    url.getHost().equals("wangkebang.cn") && url.getPath().equals("/")){
                return true;
            }
        }catch (Exception e){
            return  false;
        }
        return false;
    }

    private JSONObject[] getProblemFromWKB1(String url) throws IOException {

        // 尝试从网页抓取  形如下的题目，处理的结果 为JSONObject数组 每个元素为一个题目
        //1、Linux系统由内核和外壳以及外层的应用程序等构成。所有的发行版的内核都是由同一个小组来管理发布的。
        //A:对 B:错
        //正确答案:【对】
        try {
            Document document = Jsoup.connect(url).get();
            Elements p = document.body().select("p");

            List list = new ArrayList();
            Map<String,Object> tmpMap = new HashMap<>();
            Map<String,String> options = new HashMap<>();

            Integer conut = 0;
            for (Element p1 : p){
                if(p1 ==null)break;
                String string = p1.text();
                // 对无用信息进行过滤   以[ABCD]: [ABCD]、 [0-9]、 正确答  【  答案  为开头
                if(Pattern.matches("^(([ABCD](:|\\u3001))|[0-9]*\\u3001|(\\u6b63\\u786e\\u7b54|\\u3010|\\u7b54\\u6848)).*",string)){

                    try {
                        //题目处理
                        if((string.contains("【")||string.contains("、")) && conut ==0){

                            if(string.contains("、"))
                                tmpMap.put("problem",string.split("、",0)[1]);
                            if(string.contains("【"))
                                tmpMap.put("problem",string.split("】",0)[1]);
                            conut++;
                            continue;
                        }

                        // 选项处理
                        if(Pattern.matches("^([ABCD](:|\\u3001)).*",string) && conut >=1 ){

                            if (Pattern.matches("^([ABCD]:).*",string)){
                                String option1 = string.split(":",0)[0];
                                String option2 = string.split(":",0)[1];
                                options.put(option1,option2);
                            }else {
                                String option1 = string.split("、",0)[0];
                                String option2 = string.split("、",0)[1];
                                options.put(option1,option2);
                            }
                            conut++;
                            continue;
                        }

                        //答案处理
                        if(Pattern.matches("^(\\u6b63\\u786e\\u7b54|\\u7b54\\u6848).*",string)) {


                            if (string.contains("：")) {
                                tmpMap.put("answer", string.split("：", 0)[1]);
                            } else if (string.contains("【")) {
                                tmpMap.put("answer", string.split("【")[1].split("】", 0)[0]);
                            }
                            tmpMap.put("options", JSONObject.toJSONString(options));
                            list.add(tmpMap);
                        }

                    }catch (Exception e){
                        options = new HashMap<>();
                        tmpMap = new HashMap<>();
                        conut = 0;
                        continue;
                    }
                    options = new HashMap<>();
                    tmpMap = new HashMap<>();
                    conut = 0;

                }
            }

            JSONObject jsonObject[] = new JSONObject[list.size()];
            conut = 0;

            for(Iterator iterator = list.iterator(); iterator.hasNext();){
                jsonObject[conut] = JSONObject.parseObject(JSONObject.toJSONString(iterator.next()));
                conut++;
            }
            if (jsonObject.length ==0){
                return null;
            }
            return jsonObject;
        }catch (Exception e){
            return null;
        }
    }

    private JSONObject[] getProblemFromWKB2(String url) throws IOException {

        // 尝试从网页抓取  形如下的题目，处理的结果 为JSONObject数组 每个元素为一个题目
        //1、单选题：
        //《伤寒杂病论》成书年代是
        //选项：
        //A:春秋时代
        //B:西汉末年
        //C:战国时代
        //D:晋代
        //E:东汉末年
        //答案: 【东汉末年】
        try {
            Document document = Jsoup.connect(url).get();
            Elements p = document.body().select("p");

            List list = new ArrayList();
            Map<String, Object> tmpMap = new HashMap<>();
            Map<String, String> options = new HashMap<>();

            // 对每个p 标签进行处理
            for (Element p1 : p) {
                if (p1 == null) break;
                String string = p1.toString();
                Integer conut = 0;
                if (Pattern.matches("^([0-9]*\\u3001).*", p1.text())) {
                    String strs[] = string.split("<br>");

                    // 题目描述
                    tmpMap.put("problem",strs[1]);

                    for(String str : strs){

                        try {
                            str = str.trim();

                            //题目处理
                            if(Pattern.matches(".*[0-9]+(\\u3001|\\u3010).*",str) && !str.contains("单选") &&
                            !str.contains("多选") && !str.contains("<p>")
                            ){

                                if(str.contains("、"))
                                    tmpMap.put("problem",str.split("、")[1]);
                                else if(str.contains("【"))
                                    tmpMap.put("problem",str.split("】")[1]);
                                conut++;
                                continue;
                            }
                            // 处理选项
                            if(Pattern.matches("^([ABCDEFG](:|\\u3001)).*",str)){

                                if (Pattern.matches("^([ABCDEFG]:).*",str)){
                                    String option1 = str.split(":")[0];
                                    String option2 = str.split(":")[1];
                                    options.put(option1,option2);
                                }else {
                                    String option1 = str.split("、")[0];
                                    String option2 = str.split("、")[1];
                                    options.put(option1,option2);
                                }
                                conut++;
                                continue;
                            }

                            // 处理答案
                            if(Pattern.matches("^(\\u6b63\\u786e\\u7b54|\\u7b54\\u6848).*",str.trim())){

                                if(str.contains("：")){
                                    tmpMap.put("answer",str.split("：")[1]);
                                }else if(str.contains("【")){
                                    tmpMap.put("answer",str.split("【")[1].split("】")[0]);
                                }
                                tmpMap.put("options",JSONObject.toJSONString(options));
                                list.add(tmpMap);
                                options = new HashMap<>();
                                tmpMap = new HashMap<>();
                                conut = 0;
                            }
                        }catch (Exception e){
                            options = new HashMap<>();
                            tmpMap = new HashMap<>();
                            conut = 0;
                            continue;
                        }
                    }
                }
            }

            JSONObject jsonObject[] = new JSONObject[list.size()];
            Integer conut = 0;

            for(Iterator iterator = list.iterator(); iterator.hasNext();){
                jsonObject[conut] = JSONObject.parseObject(JSONObject.toJSONString(iterator.next()));
                conut++;
            }
            return jsonObject;
        }catch (Exception e){
            return null;
        }
    }

    public static String getParseResult(JSONObject[] jsonObjects,Integer flag){

        // 将解析的结果以 html形式返回
        String result = "<br><br><center><font size=\"3\" color=\"green\">解析结果</font></center>";
        Integer count = 0;
        for(JSONObject jsonObject: jsonObjects ){
            if(count > flag) {result += "<font size=\"3\" color=\"red\">最多显示"+(flag+1)+"条</font>";break;}
            result += "<font size=\"2\" color=\"#00FF00\">&nbsp;&nbsp;&nbsp;&nbsp;题目 ：" + jsonObject.getString("problem") + "</font><br><br>";
            result += "&nbsp;&nbsp;&nbsp;&nbsp;选项 :" + jsonObject.getString("options") + "<br><br>";
            result +=  "&nbsp;&nbsp;&nbsp;&nbsp;<font size=\"2\" color=\"#00FFFF\">答案：" + jsonObject.getString("answer") + "</font><br><br>";
            count++;
        }

        return  result;//.replace("\"","\\\"");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        HttpSession session  = request.getSession();
        PrintWriter writer = response.getWriter();

        // 检测用户是否为空
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
            writer.println("{\"data\":\"未登录！！\"}");
            //writer.println("<script>location.href='/login.jsp';</script>");
            writer.flush();
        }else{
            String url = request.getParameter("url").trim();
            if(CheckUrl(url)){
                JSONObject[] jsonObjects =  getProblemFromWKB1(url);
                if(jsonObjects == null) jsonObjects = getProblemFromWKB2(url);
                if(jsonObjects != null){
                    session.setAttribute("JSONObjectArray",jsonObjects);
                    String res = CatchFromWangKeBang.getParseResult(jsonObjects,4);
                    res += "<br><br><buttun class=\"btn btn-default\" type=\"button\" onclick=\"location.href='/ShowResult'\">查看完整数据</buttun>";
                    res += "<buttun class=\"btn btn-default\" type=\"button\" onclick=\"location.href='/JsonFileToDb'\">导入数据库</buttun>";
                    writer.println("{\"data\":\" "+ res.replace("\"","\\\"") + "\"}");
                }else{
                    writer.println("{\"data\":\"页面解析错误，无法解析该页面！！\"}");
                   // writer.println("<script>location.href='/wkbCatch.jsp';</script>");
                    writer.flush();
                }
            }else{
                writer.println("{\"data\":\"非法URL！！\"}");
                //writer.println("<script>location.href='/wkbCatch.jsp';</script>");
                writer.flush();
            }
        }
    }
}
