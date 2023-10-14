


<%@page import="com.databuck.service.RBACController"%>


<%
	if (RBACController.rbac("Results", "R", session)) {
		response.sendRedirect("dashboard_View");
	} else if (RBACController.rbac("Data Connection", "R", session)) {
		response.sendRedirect("dataConnectionView");
	} else if (RBACController.rbac("Data Template", "R", session)) {
		response.sendRedirect("datatemplateview");
	} else if (RBACController.rbac("Extend Template & Rule", "R", session)) {
		response.sendRedirect("tempview");
	} else if (RBACController.rbac("Validation Check", "R", session)) {
		response.sendRedirect("validationCheck_View");
	} else if (RBACController.rbac("Tasks", "R", session)) {
		response.sendRedirect("viewSchedules");
	} else if (RBACController.rbac("User Settings", "R", session)) {
		response.sendRedirect("accessControls");
	} else if (RBACController.rbac("Application Settings", "R", session)) {
		response.sendRedirect("applicationSettingsView?vData=appdb");
	} else
		response.sendRedirect("welcomePage");
%>
<jsp:include page="container.jsp" />

<jsp:include page="footer.jsp" />



