<!DOCTYPE html>
<html lang="zh">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>题目爬取</title>

<link rel="stylesheet" type="text/css" href="/static/wkbCatch/css/bootstrap.css">
<link rel="stylesheet" href="/static/wkbCatch/css/style.css">

</head>
<body>

<div id="top-image" style="color: ">
  <div id="content" class="container center-block">
    <div class="jumbotron">
      <div class="container">
        <h1>题目爬取</h1>
        <font color="yellow"><p>目前只支持https://wangkebang.cn的部分题目页面<br>
		格式如 https://wangkebang.cn/?p=964</p></font>
        <div class="input-group input-group-lg"> <span class="input-group-addon" id="sizing-addon1"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></span>
         
		  <input id="url" type="text" class="form-control" placeholder="输入网址" aria-describedby="sizing-addon1">
          <span class="input-group-btn">
          <button class="btn btn-default" type="button" onclick="send()">爬取</button>
          </span> </div>
      </div>
      <div id="res"><div>
    </div>
  </div>
</div>

<script src="/static/wkbCatch/js/jquery.min.js"></script>
<script src="/static/wkbCatch/js/ios-parallax.js"></script>
<script type="text/javascript">
$(document).ready(function() {
  $('#top-image').iosParallax({
	movementFactor: 50
  });
});

function send(){
    $.ajax({
        //请求方式post /get
        type:'post',
        //请求地址
        url:'http://127.0.0.1/CatchFromWKB',
        //请求数据类型
        dataType:'json',
        //传输数据
        data:{"url":document.getElementById("url").value,"t":Date()},
        //是否异步
        success:function(data){
            document.getElementById("res").innerHTML = data.data;
        },
        error:function(){
            document.getElementById("res").innerHTML = "发生错误";
        }
    });
}

</script>


</body>
</html>
