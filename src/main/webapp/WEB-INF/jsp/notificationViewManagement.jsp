<%@page import="com.databuck.service.RBACController"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<style>
	#DemoNotificationBTN {
		margin-bottom:15px;;
	}

	#MsgSubject {
		width: 100% !important; 
		min-height: 50px;
		margin: auto;
		font-size: 12.5px;
		font-family: "Lucida Console", Courier, monospace;
		border: 2px solid !important !important;
	}
	#MsgBody {
		width: 100% !important; 
		min-height: 125px;
		margin: auto;
		font-size: 12.5px;
		font-family: "Lucida Console", Courier, monospace;
		border: 2px solid !important !important;
	}

	#DataButtons {
		text-align: center;
	}
	.DataBtns {
		text-align: center !important;
		min-width: 150px;
	}
	
	.LongTextBox {
		width: 520px; 
	}	
	
	#tblNotificationViewList {
		border-collapse: collapse;
        max-width: 100% !important;
        width: 100%;
        overflow: scroll;
        overflow-wrap: anywhere;
	}
	
	.RowInEditing {
		background-color: #3973ac !important; /* #0066cc */
		color: white;
		weight: bold;
	}
	
	.RowInEditing a, .RowInEditing input[type=checkbox] {
		display: none;
	}
	
	#AssginedProjects {
		border: 2px dotted grey;
		padding: 15px;
	}
	
	#AssginedProjects label, #AssginedProjects input {
		margin-bottom: 12px;
	}
	
	#AssginedProjects input[type=checkbox] {
		width: 20px; 
		height: 20px;
	}
	
	#MessageBody {
		min-height: 125px;
		margin: auto;
		font-size: 13.5px;
		font-family: "Lucida Console", Courier, monospace;
		border: 2px solid !important !important;
	}

	#AssginedProjects label
		margin-top: -5px;
	}
	
	.ProjectLabel {
		color: red !important;			
	}
</style>
<div class="page-content-wrapper">
	<div class="page-content">
		<div class="row">
			<div class="col-md-12">
				<div class="portlet light bordered">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold">Setup Notifications and Subscriptions</span>							
						</div>						
					</div>
					<div class="portlet-body">
						<div id='NotificationMainParent' style="border: 2px solid #d9d9d9; padding: 15px; overflow-x: auto;">
							<table id="data-form-parent" width="100%">
								<tr width="100%">
									<td width="40%" valign="top">
										<label for="TopicTitle">Notification Topic</label></br>
										<input type="text" id="TopicTitle" class='LongTextBox' disabled>
										</br></br>
										
										<label for='TopicVersion' style='margin-right: 15px;'>Version</label>
										<input type="text" id="TopicVersion" value='0' style='width: 60px;' disabled>										
										
										<label for='IsSelected' style='margin-left: 290px;'>Active</label>										
										<input type='text' id='IsSelected' style='margin-left: 5px; display: inline; width: 60px;' disabled>
										<br></br>
										
										<label for='MessageSubject' style='margin-right: 15px;'>Notification Subject</br>
					        			<input type="text" id="MessageSubject" class='LongTextBox'>
					        			<br></br>										
										
										<label for='MessageBody' style='margin-right: 15px;'>Notification Message</label></br>
					        			<textarea class="LongTextBox" id='MessageBody' rows="6">
					        			</textarea>
					        			<br></br>
										
										<label for='BaseMediaIds'>Global Notification IDs</label></br>
										<input type="text" id="BaseMediaIds" class='LongTextBox'></br></br>
										
										<div class="portlet light bordered">
											<label for='PublishUrlFirst'>First Published URL</label></br>
											<input type="text" id="PublishUrlFirst" class='LongTextBox'></br></br>
											
											<label for="Authorization">Authorization</label>
											<select id="Authorization" name=Authorization
													placeholder="Choose Authorization">
													<option value="No Auth" selected>No Auth</option>
													<option value="Basic Auth">Basic Auth</option>
											</select> </br></br>
											
											<label for='Username'>Username</label></br>
											<input type="text" id="Username" class='LongTextBox'></br></br>
											
											<label for='Password'>Password</label></br>
											<input type="password" id="Password" class='LongTextBox'>
										</div>
										
										<div class="portlet light bordered">
											<label for='PublishUrlSecond'>Second Published URL </label></br>
											<input type="text" id="PublishUrlSecond" class='LongTextBox'></br></br>
											
											<label for="url2_Authorization">Authorization</label>
											<select id="url2_Authorization" name="url2_Authorization"
													placeholder="Choose Authorization">
													<option value="No Auth" selected>No Auth</option>
													<option value="Basic Auth">Basic Auth</option>
											</select> </br></br>
											
											<label for='url2_Username'>Username</label></br>
											<input type="text" id="url2_Username" class='LongTextBox'></br></br>
											
											<label for='url2_Password'>Password</label></br>
											<input type="password" id="url2_Password" class='LongTextBox'>
										</div>
									</td>
									<td width="60%" valign="top">
										<label>Subscribed Projects and Email Ids:</label></br>
										<div id="AssginedProjects" class="portlet light bordered">
										</div>
									</td>
								</tr>
								<tr>
							</table>
							<hr>
							<div id="DataButtons">
								<button class="btn btn-primary DataBtns" id="BtnSave" style="margin-left: 15px;">Save</button>															
								<button class="btn btn-primary DataBtns" id="BtnCancel" style="margin-left: 15px;">Reset</button>							
							</div>							
						</div>							
						</br>
						<table id="tblNotificationViewList" class="table table-striped table-bordered table-hover" width="100%">
							<thead>
								<tr>
									 <th>Topic</th>
									 <th>Version</th>
									 <th>Active</th>									 
									 <th>Focus Object</th>
									 <th>Global Notification Ids</th>
									 <th>Subscribed Projects</th>									 
									 <th>Edit</th>
									 <th>Copy</th>
									 <th>Delete</th>
									 <th>Verify</th>		 
								</tr>
							</thead>
						</table>					
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div>
	
</div>

<jsp:include page="footer.jsp" />

<script src="./assets/global/plugins/jquery.min.js" type="text/javascript"> </script>
<script src="./assets/global/plugins/jquery.dataTables.min.js" type="text/javascript"></script>

<head>
	<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
	<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>

<script>
var ManageNotificationDataEntry = {
	Dataset: {},
	ProjectData: {},
	ApplicableTagData: {},
	ProjectHtml: '',
	NotificationDataToSave: {},
	NotificationDataToReset: {},
	setup: function() {
		var sProjectHtml = "";
		
		sProjectHtml = sProjectHtml + "<input type='checkbox' id='project-check-{0}' {1}><label style='width: 220px; left-margin: 5px;' for='project-check-{0}'>&nbsp;&nbsp;{2}</label>";
		sProjectHtml = sProjectHtml + "<input type='text' id='project-email-{3}' value='{4}' class='LongTextBox' style='margin-left: 25px;' {5}>";
		sProjectHtml = sProjectHtml + "</br>";
		
		ManageNotificationDataEntry.ProjectHtml = sProjectHtml;
	
		$('#BtnCancel').on("click",ManageNotificationDataEntry.mainNotificationHandler);
		$('#BtnSave').on("click",ManageNotificationDataEntry.mainNotificationHandler);
	},		
	loadNotificationDataTable: function() {
		var sViewTable = '#tblNotificationViewList';
		JwfAjaxWrapper({
			WaitMsg: 'Loading Notification Records',
			Url: 'loadNotificationDataTable',
			Headers:$("#token").val(),
			Data: {},
			CallBackFunction: function(oResponse) {
				ManageNotificationDataEntry.DataSet = oResponse.DataSet;
				ManageNotificationDataEntry.ProjectData = oResponse.ProjectData;
				ManageNotificationDataEntry.ApplicableTagData = oResponse.ApplicableTagData;
			
				/* Conver JSON data coming from server further to satisfy data rules (a) active version check box is disabled (b) user cannot delete version and active version */
				ManageNotificationDataEntry.DataSet.forEach( function(oData, nIndex) {
					oData.IsSelected = (oData.IsSelected.toLowerCase() === 'true') ? true : false;
										
					if (oData.IsSelected) { 
						oData['VersionRowId-check'] = oData['VersionRowId-check'].replace('>', ' checked disabled>');						
					}											
					if ( (oData.IsSelected) || (oData.TopicVersion === '0') ) { 
						oData['VersionRowId-delete'] = '';         
					}					
				});	
				
				ManageNotificationDataEntry.DataTable = $(sViewTable).DataTable ({
					"data": ManageNotificationDataEntry.DataSet,
					"columns" : [
						{ "data" : "TopicTitle" },
						{ "data" : "TopicVersion" },
						{ "data" : "VersionRowId-check" },							
						{ "data" : "FocusType" },							
						{ "data" : "BaseMediaIds" },
						{ "data" : "SubscribedProjectNames" },													
						{ "data" : "VersionRowId-edit" },
						{ "data" : "VersionRowId-copy" },
						{ "data" : "VersionRowId-delete" },
						{ "data" : "VerifyIcon" }							
					],
					order: [[ 0, "asc" ]],
					orderClasses: false,						
					destroy: true,
					drawCallback: function( oSettings ) {
						$('#tblNotificationViewList a').off('click', ManageNotificationDataEntry.mainNotificationHandler).on('click', ManageNotificationDataEntry.mainNotificationHandler);
						$("#tblNotificationViewList input[type='checkbox']").on("click",ManageNotificationDataEntry.mainNotificationHandler);
					}					 
				});
			}
		});
		
		/* Programmatically generate click event i.e. as if user has clicked first row for editing */
		$(sViewTable).on('init.dt', function () {
			var oPageLoadEvent = {};
			
			try {				
			
				$(sViewTable + ' tr').each(function (nRow, oRow) {				
					if (nRow === 1) {				
						oPageLoadEvent.target = $(oRow).find('i')[0];
						
						ManageNotificationDataEntry.mainNotificationHandler(oPageLoadEvent);
					}
				});					
			
			} catch (oError) { 
				PageCtrl.debug.log('Error occured in data table event', oError.message);
			}
		});	
	},
	resetRowHighlighting: function() {
		$("#tblNotificationViewList > tbody > tr").each(function () {
			$(this).removeClass('RowInEditing');				
		});
	},
	/* All UI actions are centrally landed here and depending on UI action context, routed into client side flow or server side flow (or call) */
	mainNotificationHandler: function(oEvent) {
		var oTarget = oEvent.target, sTagName = oTarget.tagName.toLowerCase(), oEffectiveTarget = ((sTagName === 'button') || (sTagName === 'input')) ? oTarget : oTarget.parentElement;
		var sTargetId = oEffectiveTarget.id, aTargetIdParts = sTargetId.split('-'), sTargetIdLeft = aTargetIdParts[0], sTargetIdRight = (aTargetIdParts.length > 0) ? aTargetIdParts[1] : '';
		
		var oContextTypes = { "BtnSave": "server-flow", "check": "server-flow", "copy": "server-flow", "delete": "server-flow", "verify": "server-flow", "edit": "client-flow", "BtnCancel": "client-flow" }; 
		var sContextType = oContextTypes[sTargetIdLeft], nClickedDataIndex = 0, oNotificationData = {};
		
		/* If click is on control within table row then get data linked to this table row. In this case sTargetIdRight = VersionRowId */
		if (sTagName !== 'button') {
			nClickedDataIndex = UTIL.indexOfRecord(ManageNotificationDataEntry.DataSet, 'VersionRowId',sTargetIdRight); 
			oNotificationData = (nClickedDataIndex > -1) ? ManageNotificationDataEntry.DataSet[nClickedDataIndex] : {};
		}
		
		if (sContextType === 'client-flow') { 
			ManageNotificationDataEntry.clientNotificationFlow(oEffectiveTarget, sTargetId, sTargetIdLeft, oNotificationData); 
		} else {
			ManageNotificationDataEntry.serverNotificationFlow(oEffectiveTarget, sTargetId, sTargetIdLeft, oNotificationData); 
		}			
	},
	clientNotificationFlow(oEffectiveTarget, sTargetId, sTargetIdLeft, oNotificationData){
		var oTableRow = $(oEffectiveTarget).closest('tr');
		
		if (sTargetIdLeft === 'edit') {
			ManageNotificationDataEntry.resetRowHighlighting();
			$(oTableRow).addClass('RowInEditing');			
			ManageNotificationDataEntry.fillNotificationFullFormData(oNotificationData);
			
		} else if (sTargetIdLeft === 'BtnCancel') {
			ManageNotificationDataEntry.fillNotificationFullFormData(ManageNotificationDataEntry.NotificationDataToReset);
		}		
	},
	fillNotificationFullFormData(oNotificationData) {		
		var sProjectHtmlTmpl = '', sProjectFullHtml = '', lProjectSubscribed = false;
		var aSubscribedProjectRowIds = oNotificationData.SubscribedProjectRowIds.split(','), aSubscribedProjectNames = oNotificationData.SubscribedProjectNames.split(',');
		var aProjectRowIds = Object.keys(ManageNotificationDataEntry.ProjectData);		
		
		ManageNotificationDataEntry.NotificationDataToSave = oNotificationData;
		ManageNotificationDataEntry.NotificationDataToReset = JSON.parse(JSON.stringify(oNotificationData));		
	
		$('#TopicTitle').val(oNotificationData.TopicTitle);
		$('#TopicVersion').val(oNotificationData.TopicVersion);
		$('#MessageSubject').val(oNotificationData.MessageSubject);
		$('#MessageBody').val(oNotificationData.MessageBody);
		$('#IsSelected').val( (oNotificationData.IsSelected ? 'Yes' : 'No') );
		$('#BaseMediaIds').val(oNotificationData.BaseMediaIds);	
		$('#PublishUrlFirst').val(oNotificationData.PublishUrlFirst);
		$('#PublishUrlSecond').val(oNotificationData.PublishUrlSecond);
		
		$('#Authorization').val(oNotificationData.Authorization);
		$('#Username').val(oNotificationData.Username);
		$('#Password').val(oNotificationData.Password);
		
		$('#url2_Authorization').val(oNotificationData.url2_Authorization);
		$('#url2_Username').val(oNotificationData.url2_Username);
		$('#url2_Password').val(oNotificationData.url2_Password);

		aProjectRowIds.forEach( function(sProjectRowId, nIndex) {
			sProjectHtmlTmpl = ManageNotificationDataEntry.ProjectHtml;			
			lProjectSubscribed = (aSubscribedProjectRowIds.indexOf(sProjectRowId) > -1) ? true : false;

			sProjectHtmlTmpl = UTIL.replaceAll(sProjectHtmlTmpl, '{0}', sProjectRowId);			
			sProjectHtmlTmpl = UTIL.replaceAll(sProjectHtmlTmpl, '{1}', (lProjectSubscribed ? 'checked' : ''));

			sProjectHtmlTmpl = UTIL.replaceAll(sProjectHtmlTmpl, '{2}', ManageNotificationDataEntry.ProjectData[sProjectRowId].ProjectName);
			sProjectHtmlTmpl = UTIL.replaceAll(sProjectHtmlTmpl, '{3}', sProjectRowId);		
			sProjectHtmlTmpl = UTIL.replaceAll(sProjectHtmlTmpl, '{4}', ManageNotificationDataEntry.ProjectData[sProjectRowId].ProjectEmail);
			sProjectHtmlTmpl = UTIL.replaceAll(sProjectHtmlTmpl, '{5}', (lProjectSubscribed ? '' : 'disabled'));

			sProjectFullHtml = sProjectFullHtml + sProjectHtmlTmpl;
			
			
		});
		
		$('#AssginedProjects').html(sProjectFullHtml);
		$('#NotificationMainParent :input').off('change', ManageNotificationDataEntry.onDataChange).on('change', ManageNotificationDataEntry.onDataChange);
		
		aProjectRowIds.forEach( function(sProjectRowId, nIndex) {
						
			lProjectSubscribed = (aSubscribedProjectRowIds.indexOf(sProjectRowId) > -1) ? true : false;
			if (lProjectSubscribed) {
				var sTargetId = 'project-email-' + sProjectRowId;
				ManageNotificationDataEntry.NotificationDataToSave[sTargetId] = $('#' + sTargetId).val();
			}
			
			
		});
	},
	onDataChange(oEvent) {
		var oTarget = oEvent.target, sTargetId = oTarget.id, sInputType = oTarget.type.toLowerCase(), lProjectCheckValue = false, sProjectRowId = '', nFoundIndex = 0, oEmailTextBox = {};
		var lIsClickedValidInput = ( typeof(sInputType) === 'string') ? true : false, sSubscribedProjectRowIds =  ManageNotificationDataEntry.NotificationDataToSave.SubscribedProjectRowIds.trim();
		var aSubscribedProjectRowIds = (sSubscribedProjectRowIds.length > 0) ? ManageNotificationDataEntry.NotificationDataToSave.SubscribedProjectRowIds.split(',') : [];

		sInputType = (lIsClickedValidInput) ? sInputType : 'dummy_do_no_exist';
		PageCtrl.debug.log('onDataChange sInputType val: ' + sInputType);
		
		switch (sInputType) {
			case 'dummy_do_no_exist':
				PageCtrl.debug.log('onDataChange', "Invalid 'dummy_do_no_exist' input detected");
				break;
			
			case 'text': case 'textarea': case 'password':
				ManageNotificationDataEntry.NotificationDataToSave[sTargetId] = $('#' + sTargetId).val();
				break;
			
			case 'checkbox':
				lProjectCheckValue = oTarget.checked;
				PageCtrl.debug.log('onDataChange checkbox',  sTargetId + ',' + oTarget.checked + ', At Entry row ids = **' + ManageNotificationDataEntry.NotificationDataToSave.SubscribedProjectRowIds + '**');				
				
				if ( sTargetId.split('-').length > 2 ) {				
					sProjectRowId = sTargetId.split('-')[2];					
					$('#project-email-' + sProjectRowId).prop('disabled', !lProjectCheckValue);					
					
					if (lProjectCheckValue) {
						aSubscribedProjectRowIds.push(sProjectRowId);
					} else {	
						nFoundIndex = aSubscribedProjectRowIds.indexOf(sProjectRowId);						
						if (nFoundIndex > -1) { aSubscribedProjectRowIds.splice(nFoundIndex,1); }
					}
					ManageNotificationDataEntry.NotificationDataToSave.SubscribedProjectRowIds = aSubscribedProjectRowIds.join();
				}
				PageCtrl.debug.log('onDataChange checkbox',  sTargetId + ',' + oTarget.checked + ', At Exit row ids = **' + ManageNotificationDataEntry.NotificationDataToSave.SubscribedProjectRowIds + '**');				
				break;
				
			case 'select-one':
				ManageNotificationDataEntry.NotificationDataToSave['Authorization'] = $('#Authorization').val();
				PageCtrl.debug.log('onDataChange Authorization val: ' + $('#Authorization').val());
				
				ManageNotificationDataEntry.NotificationDataToSave['url2_Authorization'] = $('#url2_Authorization').val();
				PageCtrl.debug.log('onDataChange Authorization2 val: ' + $('#url2_Authorization').val());
				break;
		}		
	},
	getNotificationFullFormData() {
		return ManageNotificationDataEntry.NotificationDataToSave;
	},	
	serverNotificationFlow(oEffectiveTarget, sTargetId, sTargetIdLeft, oNotificationData) {
		var oWaitMsg = { "Save": "Saving notificaiton data changes", 	"Verify": "Sending notification with sample data", "Copy": "Making copy of selected notification data" };
		var sWaitMsg = '', lReloadDataTable = true, sConfimDialog = null;		
		
		
		if (sTargetId === 'BtnSave') {
			sWaitMsg = oWaitMsg.Save;
			oNotificationData = { Context: 'saveFullNotificationData', Data: ManageNotificationDataEntry.getNotificationFullFormData() };
			ManageNotificationDataEntry.isValidSubjectAndMessage(sWaitMsg, oNotificationData, lReloadDataTable);
		} else if (sTargetIdLeft === 'check') {
			sWaitMsg = oWaitMsg.Save;
			sVersionRowId = sTargetId.split('-')[1];
			oNotificationData = { Context: 'saveSelectedVersionAsActive', Data: oNotificationData };
			sConfimDialog = 'Current active version will be de-activated and selected version will become active.  Are you sure?';
			
		} else if (sTargetIdLeft === 'copy') {
			
			sWaitMsg = oWaitMsg.Copy;
			sVersionRowId = sTargetId.split('-')[1];
			oNotificationData = { Context: 'makeCopyOfSelectedVersion', Data: oNotificationData };
			sConfimDialog = 'Selected version will be copied.  Are you sure?';

		} else if (sTargetIdLeft === 'delete') {
			sWaitMsg = oWaitMsg.Copy;
			sVersionRowId = sTargetId.split('-')[1];
			oNotificationData = { Context: 'deleteSelectedVersion', Data: oNotificationData };
			sConfimDialog = 'Selected version will deleted permanently.  Are you sure?';
		
		} else if (sTargetIdLeft === 'verify') {
			sWaitMsg = oWaitMsg.Verify;
			sVersionRowId = sTargetId.split('-')[1];
			oNotificationData = { Context: 'verfiySelectedVersion', Data: oNotificationData };
			lReloadDataTable = false;
			sConfimDialog = 'Notification will send RIGHT NOW using sample data filled in selected version for your verification purpose.  Are you sure?';
		}
		
		if (sConfimDialog) {
			window.scrollTo(0,0);
			Modal.confirmDialog(0, 'Confirm Action', sConfimDialog, ['Yes', 'No'], { onDialogClose: cbConfirmAction } );
			
		} else {
			//ManageNotificationDataEntry.invokeMainNotificationHandler(sWaitMsg, oNotificationData, lReloadDataTable);	
		}

		function cbConfirmAction(oEvent) {
			var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;					
			if (sBtnSelected === 'Yes') { 
				ManageNotificationDataEntry.invokeMainNotificationHandler(sWaitMsg, oNotificationData, lReloadDataTable); 
			}
		}		
	},
	isValidSubjectAndMessage(sWaitMsg, oNotificationData, lReloadDataTable){
		var sMsgSubject = '', sMsgBody = '';		
		var sRowId = oNotificationData.Data.TopicRowId;
		var flag = true;
		
		if(!checkInputText()){ 
			Modal.confirmDialog(0, 'Invalid Data To Save',	'Vulnerability in submitted data, not allowed to submit!', ['Ok'] );
			return;
		}
		
		/* filtering applicable tags for clicked rowid*/
		var aApplicableTagData = ManageNotificationDataEntry.ApplicableTagData.filter(function(item){
		    return item.RowId == sRowId ;         
		});
		var aApplicableTags = [];
		
		for (var i = 0; i < aApplicableTagData.length; i++) {
			aApplicableTags[i] = aApplicableTagData[i].TagId;
			}
		PageCtrl.debug.log("aApplicableTags", aApplicableTags);
		
		/* getting tags from UI input for clicked rowid*/
		var aTagsInMsgSubject = [], aTagsInMsgBody = [];
		aTagsInMsgSubject = $('#MessageSubject').val().match(/{\S+/g);
		aTagsInMsgBody = $('#MessageBody').val().match(/{\S+/g);
		
		if ((typeof aTagsInMsgSubject != "undefined" && aTagsInMsgSubject != null) || (typeof aTagsInMsgBody != "undefined" && aTagsInMsgBody != null)){
			if (typeof aTagsInMsgSubject != "undefined" && aTagsInMsgSubject != null && typeof aTagsInMsgBody != "undefined" && aTagsInMsgBody != null){
				var j = aTagsInMsgSubject.length;
				for (var i = 0; i < aTagsInMsgBody.length; i++){
					aTagsInMsgSubject[j] = aTagsInMsgBody[i];
					j++;
				}
			}
			else{
				if ( typeof aTagsInMsgBody != "undefined" && aTagsInMsgBody != null){
					aTagsInMsgSubject = aTagsInMsgBody;
				}
			}	
			PageCtrl.debug.log("aTagsInMsgSubject", aTagsInMsgSubject);
			PageCtrl.debug.log("aTagsInMsgSubject", aTagsInMsgBody);
			for (var i = 0; i < aTagsInMsgSubject.length; i++){
				aTagsInMsgSubject[i] = aTagsInMsgSubject[i].replace(/[^a-zA-Z ]/g, "");
			}
			PageCtrl.debug.log("aTagsInMsgSubject", aTagsInMsgSubject);
			
			/* converting applicabel tags array in string & checking whether all input tags are presetn in it*/
			var sTags = '';
			for (var i = 0; i < aApplicableTags.length; i++){
				sTags = sTags + aApplicableTags[i] + ",";
			}
			PageCtrl.debug.log("sTags", sTags);
			
			for (aTagsInMsgSubjectObj of aTagsInMsgSubject) {
		        if (!(sTags.toUpperCase().includes(aTagsInMsgSubjectObj.toUpperCase()))) {
		        	PageCtrl.debug.log("Non-applicable tag: ", aTagsInMsgSubjectObj);
		        	flag = false;
		        }
		    
		    }
			PageCtrl.debug.log("flag", flag);
		}
		
		// Validate external API's data
		var isAPIDataValid = true;
		var message = "";
		var PublishUrlFirst = $('#PublishUrlFirst').val();
		var PublishUrlSecond = $('#PublishUrlSecond').val()
		var url1_Username = $('#Username').val();
		var url1_Password = $('#Password').val();
		var url2_Username = $('#url2_Username').val();
		var url2_Password = $('#url2_Password').val();
		
		if((url1_Password != "undefined" && url1_Password.length != 0) 
				&& (PublishUrlFirst == "undefined" || PublishUrlFirst.length == 0) 
				&& (url1_Username == "undefined" || url1_Username.length == 0)){
			message = message + "First Publish Url and Username cannot be blank. ";
			isAPIDataValid = false;
			
		} else {
		
			if((url1_Username != "undefined" && url1_Username.length != 0) || 
					(url1_Password != "undefined" && url1_Password.length != 0)){
				if(PublishUrlFirst == "undefined" || PublishUrlFirst.length == 0){
					message = message + "First Publish Url cannot be blank. ";
					isAPIDataValid = false;
				}
			}
		
			if((PublishUrlFirst != "undefined" && PublishUrlFirst.length != 0) ||
					(url1_Password != "undefined" && url1_Password.length != 0)){
				if(url1_Username == "undefined" || url1_Username.length == 0){
					message = message + "Username/KMS key for First Publish Url cannot be blank. ";
					isAPIDataValid = false;
				}
			}
		}
		
		if((url2_Password != "undefined" && url2_Password.length != 0) 
				&& (PublishUrlSecond == "undefined" || PublishUrlSecond.length == 0) 
				&& (url2_Username == "undefined" || url2_Username.length == 0)){
			message = message + "Second Publish Url and Username cannot be blank. ";
			isAPIDataValid = false;
			
		} else {
			if((url2_Username != "undefined" && url2_Username.length != 0) ||
					(url2_Password != "undefined" && url2_Password.length != 0)){
				if(PublishUrlSecond == "undefined" || PublishUrlSecond.length == 0){
					message = message + "Second Publish Url cannot be blank. ";
					isAPIDataValid = false;
				}
			}
			
			if((PublishUrlSecond != "undefined" && PublishUrlSecond.length != 0) ||
					(url2_Password != "undefined" && url2_Password.length != 0) ){
				if(url2_Username == "undefined" || url2_Username.length == 0){
					message = message + "Username/KMS key for Second Publish Url cannot be blank. ";
					isAPIDataValid = false;
				}
			}
		}
		
		if ($("#Authorization").val() == ""|| $("#Authorization").val() == null) {
			message = message + "Please Select Authorization . ";
			isAPIDataValid = false;
			
		}
		else {
			if ($("#url2_Authorization").val() == ""|| $("#url2_Authorization").val() == null) {
				message = message + "Please Select Authorization . ";
				isAPIDataValid = false;	
						}
		}
				
		
		if(flag){
			
			if(isAPIDataValid){
				ManageNotificationDataEntry.invokeMainNotificationHandler(sWaitMsg, oNotificationData, lReloadDataTable);
			} else {
				Modal.confirmDialog(0, 'Invalid Data To Save',	message, ['Ok'] );
			}
		}
		else {
			//alert("Entered Tags are not applicable.")
			//toastr.info("The subject or message body contains data tag, which is not allowed for this notification context, Cannot save notification message.");
			Modal.confirmDialog(0, 'Invalid Data To Save',	"The subject or message body contains data tag, which is not allowed for this notification context, Cannot save notification message.\n\n Applicable tags are: " + sTags, ['Ok'] );
		}
	},
	invokeMainNotificationHandler(sWaitMsg, oNotificationData, lReloadDataTable) {
		//PageCtrl.debug.log('invokeMainNotificationHandler 01', oNotificationData.Data);		
		oNotificationData.Data = deleteVulnerableJsonData('VerifyIcon,VersionRowId-edit,VersionRowId-copy,VersionRowId-delete,VersionRowId-check', oNotificationData.Data);
		//PageCtrl.debug.log('invokeMainNotificationHandler 02', oNotificationData.Data);
		
		JwfAjaxWrapper({
			WaitMsg: sWaitMsg,
			Url: 'mainNotificationHandler',
			Headers:$("#token").val(),
			Data: { NotificationData: JSON.stringify(oNotificationData) },
			CallBackFunction: function(oResponse) {
				if (oResponse.Status) {
					toastr.info(oResponse.Msg);
					if (lReloadDataTable) { ManageNotificationDataEntry.loadNotificationDataTable(); }
				} else {					
					toastr.info(oResponse.Msg);
				}
			}
		});
		
		function deleteVulnerableJsonData(sJsonKeysToRemove, oNotificationData) {
			let oRetValue = oNotificationData, aJsonKeys = sJsonKeysToRemove.split(',');
			
			try {
				aJsonKeys.forEach( function(sJsonKey, nIndex) { 				
					if (oRetValue.hasOwnProperty(sJsonKey)) { delete oRetValue[sJsonKey]; }
				});					
			} catch (oError) {
				console.log(oError.message);
			}
			
			return oRetValue;
		}
		
	}
	
}

$(document).ready(function () {
	initializeJwfSpaInfra();
	ManageNotificationDataEntry.setup();
	ManageNotificationDataEntry.loadNotificationDataTable();		
});

</script>