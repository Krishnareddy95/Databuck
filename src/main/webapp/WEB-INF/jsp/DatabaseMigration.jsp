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

	<link	href="./assets/global/plugins/apis.css" rel="stylesheet" type="text/css" />
	<link href="./assets/global/plugins/font-awesome/css/font-awesome.min.css"	rel="stylesheet" type="text/css" />
	<link	href="./assets/global/plugins/simple-line-icons/simple-line-icons.min.css"	rel="stylesheet" type="text/css" />
	<link href="./assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	<link href="./assets/global/plugins/uniform/css/uniform.default.css" rel="stylesheet" type="text/css" />
	<link	href="./assets/global/plugins/bootstrap-switch/css/bootstrap-switch.min.css" rel="stylesheet" type="text/css" />

	<link href="./assets/global/plugins/select2/css/select2.css" rel="stylesheet" type="text/css" />
	<link href="./assets/admin/pages/css/login-soft.css" rel="stylesheet" type="text/css" />

	<link href="./assets/global/css/components.css" rel="stylesheet" type="text/css" />
	<link href="./assets/global/css/plugins.css" rel="stylesheet" type="text/css" />
	<link href="./assets/admin/layout/css/layout.css" rel="stylesheet" type="text/css" />
	<link id="style_color" href="./assets/admin/layout/css/themes/default.css" rel="stylesheet" type="text/css" />
	<link href="./assets/admin/layout/css/custom.css" rel="stylesheet" type="text/css" />
	
	<script src="./assets/js/maindb98.js?db98" type="text/javascript"></script>
	
</head>

<body class="login">
	<style>
		#migration_msg { 
			width: 100%;
			font-size: 14px;
			padding: 10px;
			border: 1px solid #888;
			resize: none;
		}

		#migration_done { 
			font-size: 14px;
			margin-top: 25px;
			font-size: 14px;
			color: white;
			cursor: pointer;
			text-decoration: underline;
			display: none;
		}		
		
		#migration_done:hover {
		  color: lightblue;
		}		
	</style>	

	<div class="logo">
		<a onClick="validateHrefVulnerability(this)"  href="./"> <img src="./assets/img/logo.jpg" alt="DataBuck" /></a>
	</div>
	<div class="menu-toggler sidebar-toggler"></div>
	<form>
				<input id="token" type="hidden" value="${sessionScope.csrfToken}" />
	</form>
	
	<br /><br /><br /><br /><br /><br /><br /><br /><br /><br />
	
	<div class="content" style="margin-top: -100px; width: 50% !important;">
		<h3 id="migration_title" class="form-title">Welcome</h3>
		<textarea id="migration_msg" rows="3" readonly></textarea>		
		<form>
			<button id="migration_button" type="submit" class="btn blue pull-right">
				Proceed<i class="m-icon-swapright m-icon-white"></i>
			</button>
			<br />
			<a onClick="validateHrefVulnerability(this)"  id="migration_done">Click here to login into DataBuck</a>
		</form>
		<br /><br /><br /><br />
		<div class="copyright">
			<%=new java.util.Date().getYear() + 1900%>&copy; FirstEigen.
		</div>			
	</div>	
	

	<!--[if lt IE 9]>
		<script src="./assets/global/plugins/respond.min.js"></script>
		<script src="./assets/global/plugins/excanvas.min.js"></script> 
	<![endif]-->
	<script src="./assets/global/plugins/jquery.min.js"type="text/javascript"></script>
	<script src="./assets/global/plugins/jquery-migrate.min.js"	type="text/javascript"></script>

	<script src="./assets/global/plugins/jquery-ui/jquery-ui.min.js" type="text/javascript"></script>
	<script src="./assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
	<script src="./assets/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js" type="text/javascript"></script>
	<script src="./assets/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js"	type="text/javascript"></script>
	<script src="./assets/global/plugins/jquery.blockui.min.js" type="text/javascript"></script>
	<script src="./assets/global/plugins/jquery.cokie.min.js" type="text/javascript"></script>
	<script src="./assets/global/plugins/uniform/jquery.uniform.min.js" type="text/javascript"></script>
	<script src="./assets/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js" type="text/javascript"></script>

	<script src="./assets/global/plugins/jquery-validation/js/jquery.validate.min.js" type="text/javascript"></script>
	<script src="./assets/global/plugins/backstretch/jquery.backstretch.min.js" type="text/javascript"></script>
	<script src="./assets/global/plugins/select2/js/select2.min.js" type="text/javascript"></script>

	<script src="./assets/global/scripts/metronic.js" type="text/javascript"></script>
	<script src="./assets/admin/layout/scripts/layout.js"	type="text/javascript"></script>
	<script src="./assets/admin/layout/scripts/quick-sidebar.js" type="text/javascript"></script>
	<script src="./assets/admin/pages/scripts/login-soft.js"	type="text/javascript"></script>

	<script>
		/* Server side processing of page content of JSP page - Begin */
		var sPageContext = '${PageContext}';
		var sWelcomeMsg = "Welcome to Databuck {msg}!";
		var sContinueMsg = "Kindly click proceed to continue ..";
		var sSupportMsg = "Cannot continue to login .. Kindly contact FirstEigen support.";
		
		var aPageContextMsgs = { 
			"-3": "Program encounted technical error while submitting request to application server.\n" + sSupportMsg,		
			"-2": "Inconsistent state of DataBuck database detected or error occured.\n" + sSupportMsg,
			"-1": "Error occured while upgrade/import data schema.\n" + sSupportMsg,
			"0" : "The DataBuck product need to import all database schema before you use it.\n" + sContinueMsg,
			"1" : "The DataBuck database need to be upgraded before you use new features/bug fixes.\n" + sContinueMsg,
			"2" : "The DataBuck database upgraded/schema imported successfully, kindly login to DataBuck.",
			"3" : "" 
		};
		
		/* 
			Pradeep 22-Feb-2021
			
			Made variables global as for manual migratin proceed document ready will not get call again, so proceed click 
			need global value to check if nPageContext = 0 or 1 or 3.  For 3 no controll do migration called.  
		*/			
		var oPageContext = {}, nPageContext = 0;				
		
		/* Server side processing of page content of JSP page - End */
		
		/* All java script below now gets processed in browser life cycle events */		
		jQuery(document).ready(function() {
			var sByPassMsg = "${ByPassMigrationMsg}";
			
			// just set values variable are created in global space
			oPageContext = JSON.parse(sPageContext);
			nPageContext = parseInt(oPageContext.PageContext.toString());    
			
			/* Routine page look and feel chors */
			Metronic.init();		// init metronic core components
			Layout.init();			// init current layout
			QuickSidebar.init()	// init quick sidebar
			Login.init();
			$.backstretch([ "./assets/admin/pages/media/bg/16.jpg", "./assets/admin/pages/media/bg/15.jpg" ], { fade : 1000, duration : 2000	});	// init background slide images		
			
			/* Actual logic of migration page begins here */
			$('#migration_done').on('click', migrationDone);
			$('#migration_button').on('click', doMigration);
			
			/* LoginController Responses (data coming view JSP model object) => Status 0, 1 and -2 handled here at page load */
			if (nPageContext < 0) {
				$('#migration_title').css('color','red');
				$('#migration_msg').css('color','red');
			
				$('#migration_title').html('Error Occured');							
				$('#migration_done').css('display','none');
				$('#migration_button').css('display','none');
			} else {			
				sWelcomeMsg = sWelcomeMsg.replace("{msg}", ((oPageContext.PageContext === 0) ? "Product" : "Upgrade Page"));			
				$('#migration_title').html(sWelcomeMsg);
				
				$('#migration_title').css('color','black');
				$('#migration_msg').css('color','black');				
				$('#migration_button').css('display','block');
			}
			
			if (nPageContext === 3) {
				while (sByPassMsg.indexOf('CRLF') > -1) { sByPassMsg = sByPassMsg.replace('CRLF', '\n'); }				
				$('#migration_msg').css('color','black');	
				$('#migration_msg').css('min-height','200px');	
				$('#migration_msg').css('overflow-y','auto');
				
				$('#migration_msg').val(sByPassMsg);
			} else {
				$('#migration_msg').val(aPageContextMsgs[oPageContext.PageContext.toString()]);
			}			
		});
		
		function doMigration(oEVent) {
		
			if ( (nPageContext === 0) || (nPageContext === 1) ) {
		
				$('#migration_button').css('display','none');
				$('#migration_msg').html('\nPlease wait, upgrading/importing database schema ...');
				// Call ajax with 10 mins timeout
				$.ajax({
					type: "POST",
					headers:{'token':$("#token").val()},
					datatype: 'json',					
					data: { "nCallContext": 1 },
					timeout: 600000,               
					cache: false,					
					url: "migrateDatabase",
					success: function (sResponse) {
						handleMigrationResponse(sResponse);
					},
					error: function (oError) {
						handleMigrationResponse('{ "PageContext": "-3", "Msg": "" }');
					}
				});
			
			} else if (nPageContext === 3) {
				migrationDone();
			}			
			oEvent.preventDefault();
		}
		
		function migrationDone(oEvent) {
			
			//get Web based Url
			var sWebAppBaseUrl = getWebAppBaseUrl();
			window.location = sWebAppBaseUrl;
			
			//window.location.href = "/databuck";
			oEvent.preventDefault();		
		}
		
		/* UserSettingsController Responses (via Ajax JSON response) => Status -3,-1 and 2 handled here post ajax call success and failure * */		
		function handleMigrationResponse(sResponse) {
			var oResponse = JSON.parse(sResponse), nPageContext = parseInt(oResponse.PageContext.toString());
			var sMsgText = aPageContextMsgs[oResponse.PageContext.toString()];
			var sMsgWithMoreInfo = sMsgText + '\nMore information on error:\n' + oResponse.Msg;
			
			if (nPageContext < 0) {
				$('#migration_title').css('color','red');
				$('#migration_msg').css('color','red');
			
				$('#migration_title').html('Error Occured');							
				$('#migration_done').css('display','none');
				$('#migration_button').css('display','none');
			} else {				
				$('#migration_title').css('color','black');
				$('#migration_msg').css('color','black');
				
				$('#migration_title').html('Migration Done');
				$('#migration_done').css('display','block');
			}
			
			/* For migration error reported by server call, user will be shown it as more info on error to report to us for support */			
			$('#migration_msg').html( ((nPageContext === -1) ? sMsgWithMoreInfo : sMsgText) );
		}
		
	</script>
</body>
<!-- END BODY -->
</html>