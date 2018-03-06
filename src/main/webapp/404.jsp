<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>错误页面</title>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type">
<style type="text/css">
body,input {
	font-size: 12px;
	margin: 0;
	padding: 0;
	font-family: verdana, Arial, Helvetica, sans-serif;
	color: #000
}

body {
	text-align: center;
	margin: 0 auto
}

.title {
	background-color: #283B48;
	font-weight: bold;
	color: #fff;
	padding: 6px 10px 8px 7px;
}

.errMainArea {
	width: 546px;
	margin: 80px auto 0 auto;
	text-align: left;
	border: 1px solid #aaa
}

.errTxtArea {
	padding: 30px 34px 0 110px;
}

.errTxtArea .txt_title {
	font-size: 150%;
	font-weight: bolder;
	font-family: "Microsoft JhengHei", "微軟正黑體", "Microsoft YaHei", "微软雅黑";
}

.errBtmArea {
	padding: 10px 8px 25px 8px;
	background-color: #fff;
	text-align: center;
}

.btnFn1 {
	cursor: pointer !important;
	cursor: hand;
	height: 30px;
	width: 101px;
	padding: 3px 5px 0 0;
	font-weight: bold;
}
</style>
</head>
<body>
	<div class="errMainArea">
		<div class="title">系统提示</div>
		<div class="errTxtArea">
			<p class="txt_title">对不起，您找的文件服务器上找不到 !</p>
		</div>
		<div class="errBtmArea">
<!-- 			<input type="button" -->
<%-- 				onclick="location.href='<%=request.getContextPath()%>/user/login.jsp'" --%>
<!-- 				value="返回首页" class="btnFn1"> -->
		</div>

	</div>

</body>
</html>