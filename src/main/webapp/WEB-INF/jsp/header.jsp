
<html lang="en">
<%@page import="java.util.List"%>
<%@page import="com.databuck.bean.Project"%>
<%@page import="com.databuck.bean.DomainProject"%>
<%@page import="com.databuck.service.RBACController"%>
<!--<![endif]-->
<!-- BEGIN HEAD -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="checkVulnerability.jsp" />
<head>
<meta charset="utf-8" />
<title>DataBuck</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width, initial-scale=1" name="viewport" />
<meta content="DataBuck" name="description" />
<meta content="DataBuck" name="author" />
<!-- BEGIN GLOBAL MANDATORY STYLES -->
<link href="./assets/global/plugins/apis.css" rel="stylesheet"
	type="text/css" />
<link
	href="./assets/global/plugins/font-awesome/css/font-awesome.min.css"
	rel="stylesheet" type="text/css" />
<link
	href="./assets/global/plugins/simple-line-icons/simple-line-icons.min.css"
	rel="stylesheet" type="text/css" />
<link href="./assets/global/plugins/bootstrap/css/bootstrap.min.css"
	rel="stylesheet" type="text/css" />
<link href="./assets/global/plugins/uniform/css/uniform.default.css"
	rel="stylesheet" type="text/css" />
<link
	href="./assets/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css"
	rel="stylesheet" type="text/css" />
<link
	href="./assets/global/plugins/bootstrap-timepicker/css/bootstrap-timepicker.min.css"
	rel="stylesheet" type="text/css" />
<link
	href="./assets/global/plugins/bootstrap-datepicker/css/bootstrap-datepicker3.min.css"
	rel="stylesheet" type="text/css" />

<!-- END GLOBAL MANDATORY STYLES -->
<link rel="stylesheet" type="text/css"
	href="./assets/global/plugins/jquery.dataTables.min.css">
<link rel="stylesheet" type="text/css"
	href="./assets/global/plugins/fixedColumns.dataTables.min.css">
<!--   <link href="//cdnjs.cloudflare.com/ajax/libs/datatables/1.10.13/css/dataTables.bootstrap.min.css" rel="stylesheet" type="text/css" /> -->
<script type="text/javascript" defer
	src="./assets/global/plugins/jquery.dataTables.min.js"></script>
<script type="text/javascript" defer
	src="./assets/global/plugins/dataTables.bootstrap.min.js"></script>

<script src="./assets/global/plugins/jquery.form.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>

<script>
$(document).ready(function() {
	 jQuery.ajaxSetup({
	      beforeSend: function (jqXHR, settings) {
	    	  jqXHR.setRequestHeader("Cache-Control","no-transform, public, max-age=0,no-cache,no-store");
	      }
	})
});
</script>
<!--  <link href="https://cdn.datatables.net/1.10.11/css/dataTables.bootstrap.min.css" rel="stylesheet" type="text/css" />
        
        <link href="./assets/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.css" rel="stylesheet" type="text/css" /> -->
<link href="./assets/global/plugins/datatables/datatables.min.css"
	rel="stylesheet" type="text/css" />

<!-- BEGIN THEME GLOBAL STYLES -->
<link href="./assets/global/css/components-md.min.css" rel="stylesheet"
	id="style_components" type="text/css" />
<link href="./assets/global/css/plugins-md.min.css" rel="stylesheet"
	type="text/css" />
<link href="./assets/global/css/toastr.min.css" rel="stylesheet"
	type="text/css" />
<link href="./assets/global/css/popup.css" rel="stylesheet"
	type="text/css" />
<link href="./assets/global/css/datapopup.css" rel="stylesheet"
	type="text/css" />
<!-- END THEME GLOBAL STYLES -->
<!-- BEGIN THEME LAYOUT STYLES -->
<link href="./assets/layouts/layout4/css/layout.min.css"
	rel="stylesheet" type="text/css" />
<link href="./assets/layouts/layout4/css/themes/light.min.css"
	rel="stylesheet" type="text/css" id="style_color" />
<link href="./assets/layouts/layout4/css/custom.min.css"
	rel="stylesheet" type="text/css" />
<link href="./assets/css/styles.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="./assets/global/plugins/bootstrap.min.css"
	integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u"
	crossorigin="anonymous">
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-theme.min.css"
	integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp"
	crossorigin="anonymous">

<!-- <link rel="stylesheet"  href="./assets/css/custom-style.css" type="text/css"  /> -->
<!-- END THEME LAYOUT STYLES -->
<!-- <link rel="shortcut icon" href="favicon.ico" />  -->

</head>
<body
	class="page-container-bg-solid page-header-fixed page-sidebar-closed-hide-logo page-md">

	<!-- BEGIN HEADER -->
	<div class="page-header navbar navbar-fixed-top">
		<!-- BEGIN HEADER INNER -->
		<div class="page-header-inner ">
			<!-- BEGIN LOGO -->
			<div class="page-logo">
				<a onClick="validateHrefVulnerability(this)" href="#"> <img
					src="./assets/img/logo-small.jpg" alt="logo" class="logo-default"
					id="top-nav-logo" />
				</a>
				<div class="menu-toggler sidebar-toggler"></div>

			</div>
			<%
			List<Project> projectList = (List<Project>) session.getAttribute("userProjectList");
			List<DomainProject> domainproject = (List<DomainProject>) session.getAttribute("domainProjectList");
			if (session == null || session.isNew() || projectList == null) {
			%>
			<script type="text/javascript">
				window.location.href = "loginPage";
				</script>
			<%
			}
			%>
			<!-- END LOGO -->
			<!-- BEGIN TOP NAVIGATION MENU -->
			<!--  <div>
                        	<a onClick="validateHrefVulnerability(this)" ><i class="fa fa-sign-out"></i> Your License will expire soon. </a>
                      
                 </div> -->

			<div class="top-menu">

				<%
				String expireDate = (String) session.getAttribute("licenseExpired");
				java.text.DateFormat df = new java.text.SimpleDateFormat("dd.MMMM.yyyy");
				if (expireDate.equalsIgnoreCase("true")) {
				%>
				<div class="nav navbar-nav pull-left">
					<a onClick="validateHrefVulnerability(this)">Your License will
						expire on <%=df.format(session.getAttribute("licenseExpiryDate"))%>
					</a>
				</div>
				<%
				}
				%>


				<ul class="nav navbar-nav pull-right">
					<!-- BEGIN USER LOGIN DROPDOWN -->
					<li class="dropdown dropdown-user"><a
						onClick="validateHrefVulnerability(this)" href=""
						class="dropdown-toggle" data-toggle="dropdown"
						data-hover="dropdown" data-close-others="true"> <!-- <img alt="" class="img-circle" src="../../assets/admin/layout/img/avatar3_small.jpg"/> -->
							<span class="username"> <%
 Object firstName = session.getAttribute("firstName");
 %> ${firstName}
						</span> <i class="fa fa-angle-down"></i>
					</a>
						<ul class="dropdown-menu">

							<li><a onClick="validateHrefVulnerability(this)"
								href="logout"> <i class="fa fa-sign-out"></i> Log Out
							</a></li>
						</ul></li>
					<!-- END USER LOGIN DROPDOWN -->

				</ul>
			</div>
			<form>
				<input id="token" type="hidden" value="${sessionScope.csrfToken}" />
			</form>
			<div class="top-menu project_dropdown">
				<ul class="nav navbar-nav pull-right">
					<!-- BEGIN USER LOGIN DROPDOWN -->
					<li class="dropdown dropdown-user"><a
						onClick="validateHrefVulnerability(this)" href=""
						class="dropdown-toggle" data-toggle="dropdown"
						data-hover="dropdown" data-close-others="true"> <!-- <img alt="" class="img-circle" src="../../assets/admin/layout/img/avatar3_small.jpg"/> -->
							<span class="userproject">Select Project (DomainAccessKey)
						</span> <i class="projectangle fa fa-angle-down" style="color: #E26A6A"></i>
					</a>
						<ul class="dropdown-menu"
							style="width: 215px; height: 300px; overflow: auto">
							<li><c:forEach var="projObj" items="${domainProjectList}">
									<ul id="projectid">
										<c:choose>
											<c:when
												test="${projObj.idProject == projectId && projObj.domainId == domainId}">

												<span style="color: #006DF0;">${projObj.projectName}
													(${projObj.domainName})</span>
												<%-- <img
													src="${pageContext.request.contextPath}/assets/img/correct-sign.png" /> --%>
											</c:when>
											<c:otherwise>

												<a
													onClick="validateHrefVulnerability(this);updateDomainProjectId(${projObj.idProject},${projObj.domainId});">${projObj.projectName}
													(${projObj.domainName})</a>
											</c:otherwise>
										</c:choose>
									</ul>
								</c:forEach></li>
						</ul> <%--                     <ul>
        <c:forEach var="faculty" items="${faculties}">
            <li><a onClick="validateHrefVulnerability(this)"  href="???">${faculty.name}</a></li>
        </c:forEach>
    </ul> --%></li>
					<!-- END USER LOGIN DROPDOWN -->

				</ul>
			</div>
			<%-- <div class="top-menu">
                    <ul class="nav navbar-nav pull-right">
                        <!-- BEGIN USER LOGIN DROPDOWN -->
                        <li class="dropdown dropdown-user">
                            <a onClick="validateHrefVulnerability(this)"  href="" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
                            <!-- <img alt="" class="img-circle" src="../../assets/admin/layout/img/avatar3_small.jpg"/> -->
                            <span class="userproject">Select Project </span>
                            <i class="projectangle fa fa-angle-down" style="color:#E26A6A"></i>
                            </a>
                            <ul class="dropdown-menu" >                                
                                <li>
										<c:forEach var="projObj" items="${userProjectList}">
											<ul id= "projectid">
											 <c:choose>															
													<c:when test="${projObj.idProject == projectId}">											
														
														<span style="color:red">${projObj.projectName}</span><img src="${pageContext.request.contextPath}/assets/img/correct-sign.png"/>
													</c:when>
													<c:otherwise>	 
																											
														<a onClick="validateHrefVulnerability(this)"  href="updateProjectIdInSession?projectId=${projObj.idProject}">${projObj.projectName}</a>
												</c:otherwise>	 											
											</c:choose>  
											</ul>
										</c:forEach>
								</li>
                            </ul>
                             <ul>
        <c:forEach var="faculty" items="${faculties}">
            <li><a onClick="validateHrefVulnerability(this)"  href="???">${faculty.name}</a></li>
        </c:forEach>
    </ul>
                        </li>
                        <!-- END USER LOGIN DROPDOWN -->
                        
                    </ul>
                </div>  --%>
			<div class="top-menu project_dropdown">
				<ul class="nav navbar-text">
					<li style="background-color: #D0E5FB;"><a
						onClick="validateHrefVulnerability(this)"
						style="color: #006DF0; font-size: 16px; height: 35px"><%=(session.getAttribute("projectName"))%>
							(<%=(session.getAttribute("domainName"))%>) </a></li>
				</ul>
			</div>
			<!-- END TOP NAVIGATION MENU -->
		</div>
		<!-- END HEADER INNER -->
	</div>
	<!-- **********************END HEADER ******************-->
	<script src="./assets/global/plugins/jquery.min.js"
		type="text/javascript"></script>
	<script src="./assets/global/plugins/jquery-ui.min.js"></script>
	<script type="text/javascript">
	
		window.open(url,'_blank');
		function updateDomainProjectId(projectId, domainId) {		
			var form_data = {
					projectId : projectId,
					domainId : domainId
			};
			$.ajax({
				url : './updateDomainProjectIdInSession',
				type : 'GET',					
				datatype : 'json',
				data : form_data,
				success: function(message) {	
					var j_obj = $.parseJSON(message);
					if(j_obj.hasOwnProperty('status') && j_obj.status == 'success'){
						window.location.reload();
					} else {
						window.location.href = "loginPage";
					}						
				},
				error : function() {
					window.location.href = "loginPage";
				},					
			});	
		}
  	</script>
	<script type="text/javascript">
     var path = '${pageContext.request.contextPath}';
     $('#projectid').change(    	
		function() {				
				var form_data = {
						projectId : $("#projectid").val(),						
				};
				$.ajax({
					url : './updateProjectIdInSession',
					type : 'GET',					
					datatype : 'json',
					data : form_data,
					success: function(obj) {						 
						window.location.href = path+"/dataConnectionView";			
					},
					error : function() {
						
					},					
				})	
		});
  </script>