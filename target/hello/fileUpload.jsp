<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="diego.module.User" %>
<%
    User user = (User)session.getAttribute("userObject");
    if(user == null){
        out.print("<script>alert('请登录！！')</script>");
        out.print("<script>location.href='/login.jsp';</script>");
        return;
    }
%>
<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>文件上传</title>

    <link href="/static/upload/layui/css/layui.css" rel="stylesheet" />

    <script src="/static/upload/js/jquery.min.js" type="text/javascript"></script>
    <script src="/static/upload/layui/layui.js" type="text/javascript"></script>
    <script src="/static/upload/js/upload.js" type="text/javascript"></script>

</head>

<body>
<br><br><br><br>
<center><font size="4" color="#483d8b">批量导入题目，将json文件进行解析，并写入数据库<br>
    对应json格式如下<br><br></font>
    <font size="3" color="#dc143c">
    {"problem":"第一题目描述","options":{"A":"","B":"","C":"","D":""},"answer":"A"}<br>
    {"problem":"第二题目描述","options":{"A":"","B":"","C":"","D":""},"answer":"A"}<br><br>
    </font>
    <font size="4" color="#483d8b">
        每行一个json 对应一个完整题目
    </font><br><br>
    样例<br>
    <img src="/static/images/样例.png"/>
</center>


<div class="layui-main" style="margin-top:20px;">
    <form class="layui-form" method="post" action="javascript:;">
        <div class="layui-form-item">
            <label class="layui-form-label"></label>

            <div class="layui-form-mid layui-word-aux"></div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label"><br></label>
            <div class="layui-inline">
                <div class="layui-upload-drag" id="upload">
                    <i class="layui-icon"></i>
                    <p>点击上传，或将文件拖拽到此处</p>
                </div>

            </div>
            <div class="layui-inline" id="upload_preview"></div>
        </div>
        <div class="layui-form-item layui-hide" id="upload_progress">
            <label class="layui-form-label"></label>
            <div class="layui-input-inline" style="width:21%;">
                <div class="layui-progress" lay-showpercent="true" lay-filter="upload_progress">
                    <div class="layui-progress-bar layui-bg-blue" lay-percent="0%"></div>
                </div>
            </div>
        </div>
        <div class="layui-form-item layui-hide" id="upload_progress">
            <label class="layui-form-label"></label>
            <div class="layui-input-inline" style="width:21%;">
                <div class="layui-progress" lay-showpercent="true" lay-filter="upload_progress">
                    <div class="layui-progress-bar layui-bg-blue" lay-percent="0%"></div>
                </div>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label"></label>
            <div class="layui-input-inline">
                <button class="layui-btn" lay-submit>提交</button>
            </div>
        </div>
    </form>

    <p id = "res"></p>
</div>

</body>
</html>
