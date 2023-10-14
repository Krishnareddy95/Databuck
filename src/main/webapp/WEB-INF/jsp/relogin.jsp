<!DOCTYPE html>
<html>
<head>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<style>
		.relogin-body {
			margin: 30px;
		}

		.reloginReasonMsg {
			text-aling:left; 
			overflow-x:hidden; 
			overflow-y:auto; 
			border:2px inset #b3b3b3; 
			width: 70%; 
			resize: vertical;
			text-align: left;
			padding: 8px;
		}
	</style>
</head>
<body class="relogin-body">

	<h2>Relogin to DataBuck</h2>
	</br>	</br>
	<label for="reloginReasonMsg" style='display: block; margin-bottom: 3px;'>Reason for Re-Login</label>
	<textarea id='reloginReasonMsg' rows='5' cols='50' class="reloginReasonMsg" disabled>${InterceptorErrorMsg}</textarea>

	<p style='margin-top: 20px;'>
		<a href="loginPage">Click here to relogin to Databuck</a>
	</p>
</body>	
</html>





