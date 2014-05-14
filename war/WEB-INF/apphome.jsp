<%@ page language="java" contentType="text/html; charset=US-ASCII"
	pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Morgan Admissions - Web App</title>
<link href='http://getbootstrap.com/dist/css/bootstrap.min.css' rel='stylesheet'/>
</head>
<body style='background-color:#eee;'>
	<div class='header container' style='width:700px'>
		<img src='http://www.morgan.edu/images/shared/logo-headerRt.png' style='width: 700px;' />
	</div>
<h3 align='center'>Do NOT use Internet Explorer. Use Google Chrome or Firefox instead.</h3>\n
<h4 align='center'>Select a .XLS file to Upload: </h4><br/>
<div class='container' style='width:250px'>
	<form action='/UploadFile' method='post' enctype='multipart/form-data'>
		<input type="file" name="dataFile" id="fileChooser" /><br/>
		<input type="submit" value="Send" class="btn btn-primary" style='width:230px' />
	</form>
</div>
</body>
</html>