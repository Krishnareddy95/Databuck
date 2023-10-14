<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<style>
	#tblGroupRoleMapList ,#tblGroupRoleMapList th {
		background: #f2f2f2;
		border-bottom: 2px solid #808080;
	}	
</style>
<!-- BEGIN CONTENT -->

<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">

					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> ADGroup-Role Map
							</span>
						</div>
					</div>
					<div class="portlet-body form">
						<div class="form-body">
							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" name="ldapGroup" id="ldapGroupId">
												<c:forEach items="${LdapGroups}"
														var="LdapGroups">
														<option value=${LdapGroups}>${LdapGroups}</option>
												</c:forEach>
											</select> <label for="ldapGroupId">Choose AD Group</label><br>
									</div>
									<br /> <span class="required"></span>
								</div>
	
								<div class="col-md-6 col-capture">
										<div class="form-group form-md-line-input">
											<select class="form-control" name="databuckRole" id="databuckRoleId">
											
											</select> <label for="databuckRoleId">Choose Role</label><br>
										</div>
								</div>
							</div>
	
							<div class="form-actions noborder align-center">
								<button type="submit" class="btn blue" id="startTaskId">Save</button>
								<button class="btn blue" id="cancelId">Cancel</button>	
							</div>
						</div>
						
					</div>
					
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> ADGroup-Role Map View
							</span>
						</div>
					</div>
					
					<div class="portlet-body">
						<table id="tblGroupRoleMapList" class="table table-striped table-bordered table-hover" width="100%">
								<thead>
									<tr>
										 <th>AD Group</th>
										 <th>Role</th>
										 <th>Edit</th>			 
									</tr>
								</thead>
						</table>
					
					</div>
				</div>
			</div>
			<!-- END SAMPLE FORM PORTLET-->
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />

<head>
	<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
	<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
</head>

<script>
var ManageGroupRoleDataEntry = {
		Dataset : {},
		GroupRoleRecord: {},
		RolesList: {},
		
		setup: function() {
			$('#startTaskId').on('click', function() { ManageGroupRoleDataEntry.submitGroupRoleData(); });
			$('#cancelId').on('click', function() { ManageGroupRoleDataEntry.cancelForm(); });
		},
		
		cancelForm: function() {
			var aSelectFields = document.querySelectorAll('select');
			aSelectFields.forEach( function(oElement, nIndex) {
				oElement.selectedIndex = "0";
			});
			
		},
		
		fillFormData: function() {
			
			UIManager.fillSelect($('#databuckRoleId')[0],ManageGroupRoleDataEntry.RolesList['Roles_list'], '1');
			
			
		},
		
		loadGroupRoleMapList: function(){
			JwfAjaxWrapper({
				WaitMsg: 'Loading ADGroup-Role Mapping',
				Url: 'loadGroupRoleMapList',
				Headers:$("#token").val(),
				Data: {},
				CallBackFunction: function(oResponse) {
					ManageGroupRoleDataEntry.DataSet = oResponse.DataSet;
					PageCtrl.debug.log('ManageGroupRoleDataEntry.DataSet', ManageGroupRoleDataEntry.DataSet);
					ManageGroupRoleDataEntry.RolesList = oResponse.PageList;
					
					ManageGroupRoleDataEntry.fillFormData();
					
					ManageGroupRoleDataEntry.DataTable = $("#tblGroupRoleMapList").DataTable ({
						"data" : ManageGroupRoleDataEntry.DataSet,
						"columns" : [
							
							{ "data" : "ADGroupName" },
							{ "data" : "Role" },
							{ "data" : "Id-edit" }
							
						],
						order: [[ 2, "asc" ]],
						destroy: true,
						drawCallback: function( oSettings ) {
							$('#tblGroupRoleMapList a').on('click', ManageGroupRoleDataEntry.AnkerIconClicked);
						}					 
					});	
				}
				
			});
		},
		
		AnkerIconClicked:  function(oEvent) {
			var lFoundId = false, sClickedAnkerId = oEvent.target.parentElement.id, sRowId = '-1';			
			
			if (sClickedAnkerId.indexOf('delete') > -1) {
				sRowId = sClickedAnkerId.split('-')[1];
				
				
			} else {
				if ( sClickedAnkerId.indexOf('edit') > -1 ) { sRowId = sClickedAnkerId.split('-')[1]; }
				ManageGroupRoleDataEntry.EditFormData(sRowId);
			} 		
		},
		
		EditFormData: function(sRowId) {
			PageCtrl.debug.log('sRowId', sRowId);
			var nRowIndex = UTIL.indexOfRecord(ManageGroupRoleDataEntry.DataSet, 'Id', sRowId);
			var oClickedRowData = JSON.parse(JSON.stringify(ManageGroupRoleDataEntry.DataSet[nRowIndex]));
			PageCtrl.debug.log('oClickedRowData', oClickedRowData);
			

			$('#ldapGroupId').val(oClickedRowData.ADGroupName);
			$('#databuckRoleId').val(oClickedRowData.RoleId);
			document.body.scrollTop = 0;
			
		},
		
		submitGroupRoleData: function() {
			var oGroupRoleRecordToSave = ManageGroupRoleDataEntry.GroupRoleRecord;
			
			oGroupRoleRecordToSave.ADGroupName = $('#ldapGroupId').val();
			oGroupRoleRecordToSave.Role = $('#databuckRoleId').val();
		
			JwfAjaxWrapper({
				WaitMsg: 'Saving Group Role Records...',
				Url: 'SaveGroupRoleRecord',
				Headers:$("#token").val(),
				Data: { GroupRoleRecordToSave: JSON.stringify(oGroupRoleRecordToSave) },
				CallBackFunction: function(oResponse) {				
					if (oResponse.Result) {
						ManageGroupRoleDataEntry.loadGroupRoleMapList();
						ManageGroupRoleDataEntry.cancelForm();
						toastr.info(oResponse.Msg);
						
					} else {					
						ManageGroupRoleDataEntry.cancelForm();
						toastr.info(oResponse.Msg);
					}
				}
			});
		}
		
		
}


$(document).ready( function() {
	initializeJwfSpaInfra();      // Load JWF framework module on this page
	ManageGroupRoleDataEntry.setup();
	ManageGroupRoleDataEntry.loadGroupRoleMapList();
});
</script>