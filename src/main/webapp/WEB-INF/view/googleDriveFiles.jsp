<%@page import="java.util.ArrayList"%>
<%@page import="com.google.api.services.drive.model.File"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>File List</title>

	<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css"
		  integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
</head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<body>
<table align="center">
	<%ArrayList<File> std = (ArrayList<File>)request.getAttribute("files");
		for(File file : std) {%>
	<tr>
		<td><%=file.getId()%></td>
		<td><%=file.getName()%></td>
		<td>
			<form action="http://localhost:8080/drive/download" method="POST">
				<input type="text" name="Authorization" class="invisible" value="<%=(String)request.getAttribute("jwt")%>">
				<input type="text" name="id" class="invisible" value="<%=file.getId()%>">
				<input type="submit" value="Download">
			</form>
		</td>
	</tr>
	<%}%>
</table>
</body>
</html>