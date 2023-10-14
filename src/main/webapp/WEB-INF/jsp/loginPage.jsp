
<!DOCTYPE html>
<!--[if IE 8]> <html lang="en" class="ie8 no-js"> <![endif]-->
<!--[if IE 9]> <html lang="en" class="ie9 no-js"> <![endif]-->
<!--[if !IE]><!-->
<html lang="en">
<!--<![endif]-->
<!-- BEGIN HEAD -->
<%@page isELIgnored="false"%>
<jsp:include page="checkVulnerability.jsp" />
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>DataBuck</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta content="width=device-width, initial-scale=1.0" name="viewport" />
<meta content="DataBuck" name="description" />

<!-- ========================BEGIN GLOBAL MANDATORY STYLES======================== -->

<link
	href="./assets/global/plugins/apis.css"
	rel="stylesheet" type="text/css" />
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

<!-- ============================================END GLOBAL MANDATORY STYLES ============================================-->

<!--======================== BEGIN PAGE LEVEL STYLES ========================-->

<link href="./assets/global/plugins/select2/css/select2.css"
	rel="stylesheet" type="text/css" />
<link href="./assets/admin/pages/css/login-soft.css" rel="stylesheet"
	type="text/css" />

<!-- ========================END PAGE LEVEL SCRIPTS======================== -->

<!--======================== BEGIN THEME STYLES======================== -->

<link href="./assets/global/css/components.css" rel="stylesheet"
	type="text/css" />
<link href="./assets/global/css/plugins.css" rel="stylesheet"
	type="text/css" />
<link href="./assets/admin/layout/css/layout.css" rel="stylesheet"
	type="text/css" />
<link id="style_color"
	href="./assets/admin/layout/css/themes/default.css" rel="stylesheet"
	type="text/css" />
<link href="./assets/admin/layout/css/custom.css" rel="stylesheet"
	type="text/css" />

<!-- ========================END THEME STYLES ========================-->

<!-- <link rel="shortcut icon" href="favicon.ico"/> -->
</head>

<!--======================== END HEAD======================== -->

<!-- ========================BEGIN BODY======================== -->
<!-- DOC: Apply "page-header-fixed-mobile" and "page-footer-fixed-mobile" class to body element to force fixed header or footer in mobile devices -->
<!-- DOC: Apply "page-sidebar-closed" class to the body and "page-sidebar-menu-closed" class to the sidebar menu element to hide the sidebar by default -->
<!-- DOC: Apply "page-sidebar-hide" class to the body to make the sidebar completely hidden on toggle -->
<!-- DOC: Apply "page-sidebar-closed-hide-logo" class to the body element to make the logo hidden on sidebar toggle -->
<!-- DOC: Apply "page-sidebar-hide" class to body element to completely hide the sidebar on sidebar toggle -->
<!-- DOC: Apply "page-sidebar-fixed" class to have fixed sidebar -->
<!-- DOC: Apply "page-footer-fixed" class to the body element to have fixed footer -->
<!-- DOC: Apply "page-sidebar-reversed" class to put the sidebar on the right side -->
<!-- DOC: Apply "page-full-width" class to the body element to have full width page without the sidebar menu -->

<body class="login">

	<!--======================== BEGIN LOGO ========================-->

	<div class="logo">
		<a onClick="validateHrefVulnerability(this)"  href="./"> <img src="./assets/img/logo.jpg" alt="DataBuck" />
		</a>
	</div>

	<!--======================== END LOGO ========================-->

	<!--======================== BEGIN SIDEBAR TOGGLER BUTTON ========================-->

	<div class="menu-toggler sidebar-toggler"></div>

	<!--======================== END SIDEBAR TOGGLER BUTTON ========================-->

	<!-- ===========================================BEGIN LOGIN =============================================-->
	<br />
	<br />
	<br />
	<br />
	<br />
	<br />
	<br />
	<br />
	<br />
	<br />
	<div class="content" style="margin-top: -100px;">

		<!--        ====================== BEGIN LOGIN FORM ================================        -->




		<form action="login_process" method="post" accept-charset="utf-8">


			<input type="hidden" id="tourl" value="/" />
			<h3 class="form-title">Login to your account</h3>
			<div class="alert alert-danger display-hide">
				<button class="close" data-close="alert"></button>
				<span> Enter your email and password. </span> ${msg}

			</div>
			<div class="form-group">
				<!--ie8, ie9 does not support html5 placeholder, so we just show field title for that-->
				<label class="control-label visible-ie8 visible-ie9">Enter
					Email</label>
				<div class="input-icon">
					<i class="fa fa-user"></i>
					<%
						String ADflag = (String) request.getAttribute("ADflag");
						if (ADflag != null && ADflag.equals("N")) {
					%>
					<input class="form-control placeholder-no-fix" type="email"
						autocomplete="off" placeholder="Enter Email" name="email"
						id="usernameid" />
					<%
						} else {
					%>
					<input class="form-control placeholder-no-fix" type="text"
						autocomplete="off" placeholder="Enter UID" name="email"
						id="usernameid" />
					<%
						}
					%>
				</div>
			</div>
			<div class="form-group">
				<label class="control-label visible-ie8 visible-ie9">Password</label>
				<div class="input-icon">
					<i class="fa fa-lock"></i> <input
						class="form-control placeholder-no-fix" type="password"
						autocomplete="off" placeholder="Enter Password" name="password"
						id="passwordid" />
				</div>
			</div>
			<div class="form-actions">
				<!-- <label class="checkbox">
			<input type="checkbox" name="remember" value="1"/> Remember me </label> -->
				<button type="submit" class="btn blue pull-right" id="submitbtnid">
					Login <i class="m-icon-swapright m-icon-white"></i>
				</button>
				<p>
					<font color="red">${msg}</font>
				</p>
			</div>


		</form>

		<!--==================== END LOGIN FORM======================================== -->



	</div>

	<!-- ==============================END LOGIN ================================================-->
	<!-- BEGIN COPYRIGHT -->
	<div class="copyright">
		<%=new java.util.Date().getYear() + 1900%>
		&copy; FirstEigen.
	</div>
	<!--=================== END COPYRIGHT=============================== -->


	<!-- ========================BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) =========-->
	<!-- BEGIN CORE PLUGINS -->

	<!--[if lt IE 9]>
<script src="./assets/global/plugins/respond.min.js"></script>
<script src="./assets/global/plugins/excanvas.min.js"></script> 
<![endif]-->
	<script src="./assets/global/plugins/jquery.min.js"
		type="text/javascript"></script>
	<script src="./assets/global/plugins/jquery-migrate.min.js"
		type="text/javascript"></script>
	<!-- 

  IMPORTANT! Load jquery-ui-1.10.3.custom.min.js before 
bootstrap.min.js to fix bootstrap tooltip
 conflict with jquery ui tooltip 
 -->

	<script src="./assets/global/plugins/jquery-ui/jquery-ui.min.js"
		type="text/javascript"></script>
	<script src="./assets/global/plugins/bootstrap/js/bootstrap.min.js"
		type="text/javascript"></script>
	<script
		src="./assets/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js"
		type="text/javascript"></script>
	<script
		src="./assets/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js"
		type="text/javascript"></script>
	<script src="./assets/global/plugins/jquery.blockui.min.js"
		type="text/javascript"></script>
	<script src="./assets/global/plugins/jquery.cokie.min.js"
		type="text/javascript"></script>
	<script src="./assets/global/plugins/uniform/jquery.uniform.min.js"
		type="text/javascript"></script>
	<script
		src="./assets/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js"
		type="text/javascript"></script>

	<!--================================= END CORE PLUGINS===================================== -->



	<!-- =====================BEGIN PAGE LEVEL PLUGINS ====================-->

	<script
		src="./assets/global/plugins/jquery-validation/js/jquery.validate.min.js"
		type="text/javascript"></script>
	<script
		src="./assets/global/plugins/backstretch/jquery.backstretch.min.js"
		type="text/javascript"></script>
	<script src="./assets/global/plugins/select2/js/select2.min.js"
		type="text/javascript"></script>
	<!-- END PAGE LEVEL PLUGINS -->



	<!--==================================== BEGIN PAGE LEVEL SCRIPTS =============================-->

	<script src="./assets/global/scripts/metronic.js"
		type="text/javascript"></script>
	<script src="./assets/admin/layout/scripts/layout.js"
		type="text/javascript"></script>
	<script src="./assets/admin/layout/scripts/quick-sidebar.js"
		type="text/javascript"></script>
	<script src="./assets/admin/pages/scripts/login-soft.js"
		type="text/javascript"></script>
	<script src="./assets/js/main.js" type="text/javascript"></script>



	<!--============================== END PAGE LEVEL SCRIPTS ===========================-->

	<script>
		jQuery(document).ready(
				function() {
					Metronic.init(); // init metronic core components
					Layout.init(); // init current layout
					QuickSidebar.init() // init quick sidebar
					Login.init();

					// init background slide images
					$.backstretch([ "./assets/admin/pages/media/bg/16.jpg",
							"./assets/admin/pages/media/bg/15.jpg" ], {
						fade : 1000,
						duration : 2000
					});
				});
		$("#usernameid").keyup(function(event) {
			if (event.keyCode == 13) {
				$("#submitbtnid").click();
			}
		});
		$("#passwordid").keyup(function(event) {
			if (event.keyCode == 13) {
				$("#submitbtnid").click();
			}
		});
	</script>
	<!--============================ END JAVASCRIPTS ===========================================================================-->
</body>
<!-- END BODY -->
</html>