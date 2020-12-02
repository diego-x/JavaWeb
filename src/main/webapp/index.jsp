<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="diego.module.User" %>
<%@ page import="diego.module.JsonFile" %>
<%

    User user = (User)session.getAttribute("userObject");
    if(user == null){
       out.print("<script>alert('请登录！！')</script>");
       out.print("<script>location.href='/login.jsp';</script>");
       return;
   }

%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>站内搜索功能</title>

    <link rel="stylesheet" type="text/css" href="/static/search/styles.css" />

</head>

<body>
<div id="page">

    <h1>Google Powered Site Search</h1>

    <form id="searchForm" method="post">
        <fieldset>
            <input id="s" type="text" />

            <input type="submit" value="Submit" id="submitButton" />

            <ul class="icons">
                <li class="web" title="Web Search" data-searchType="web">Web</li>
                <a href="wkbCatch.jsp"><li class="images" title="爬取网课帮" data-searchType="images">爬取网课帮</li></a>
                <a href="fileUpload.jsp"><li class="news" title="导入数据" data-searchType="news">导入数据</li></a>
                <li class="videos" title="Video Search" data-searchType="video">Videos</li>
            </ul>
        </fieldset>
    </form>
    <div id="resultsDiv"></div>
</div>

<script src="/static/search/js/jquery.min.js"></script>
<script src="/static/search/js/script.js"></script>

</body>
</html>
