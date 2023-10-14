
<%@page import="java.util.List"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="header.jsp" />
<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
<link rel="stylesheet" href="./assets/css/custom-style.css"
	type="text/css" />

<style>
/* #thrsholds_apply_option_container {
		float: right;
		margin-top: -45px;
		margin-right: 43px;
	} */
#global_discovery_tabs {
	width: 100%;
}

#content_global_rules, #content_reference_rules,
	#content_global_thresholds, #content_global_synonym,
	#content_other_global_rules {
	margin-top: 0px !important;
	margin-right: auto;
	padding-top: 50px;
	margin-left: auto;
	min-height: 200px;
	border-top: 2px solid var(- -label-tabs-color);
}

#table_global_rules tr[data-linkedrule='Y'] {
	color: #0099e6 !important;
}

.form .form-body, .portlet-form .form-body {
	padding: 0px !important;
}

#form-body-add-edit-synonyms {
	padding: 25px;
}

#form-body-add-edit-synonyms label {
	height: 32px;
	padding: 0px;
	maring: 0px;
	font-size: 13.5px;
}

#form-body-add-edit-synonyms select, #form-body-add-edit-synonyms input[type="text"]
	{
	padding: 0px;
	maring: 0px;
	width: 650px;
	height: 32px;
	font-size: 13.5px;
	/*font-family: "Helvetica Neue",Helvetica,Arial,sans-serif;*/
	font-family: inherit;
}

#form-body-add-edit-synonyms textarea {
	width: 100%;
	min-height: 125px;
	margin-top: 10px;
	padding: 5px;
	background: #f2f2f2;
	border: 2px solid #e6e6e6;
	font-size: 12.5px;
	pointer-events: none;
	font-family: "Lucida Console", Courier, monospace;
}

#form-body-add-edit-synonyms textarea[data-msgstate="hint"] {
	color: black;
}

#form-body-add-edit-synonyms textarea[data-msgstate="error"] {
	color: brown;
	border: 2px solid brown;
}

#tblSynonymsViewList th {
	/*background: #f2f2f2;*/
	border-bottom: 2px solid #808080;
}

.datatable-checkbox {
	width: 18px;
	height: 18px;
	margin-left: auto;
	margin-right: auto;
	margin-top: 8px;
	margin-bottom: 5px;
}

.datatable-anker {
	margin-left: auto;
	margin-right: auto;
	margin-top: 8px;
	margin-bottom: 5px;
}

#BtnAddNewSynonyms, #Btn_add_global_rule {
	margin-bottom: 15px;
}
</style>
<!-- BEGIN CONTENT -->

<div class="page-content-wrapper">
	<!-- BEGIN CONTENT BODY -->
	<div class="page-content">
		<!-- BEGIN PAGE TITLE-->
		<!-- END PAGE TITLE-->
		<!-- END PAGE HEADER-->
		<div class="row">
			<div class="col-md-6" style="width: 100%;">
				<!-- BEGIN SAMPLE FORM PORTLET-->
				<div class="portlet light bordered init">
					<div class="portlet-title">
						<div class="caption font-red-sunglo">
							<span class="caption-subject bold "> Global Rule Discovery
								: ${appName} </span><br> 
								<input type="hidden" id="idData" value="${idData}">
								<input type="hidden" id="idApp" value="${idApp}">
								<input type="hidden" id="templateColumns" value="${templateColumnList}">
						</div>
						<span class="caption-subject bold" style="float: right;">
							<button class="btn btn-primary" id="BtnBackward" data-fromMapping="${fromMapping}">Back</button>
						</span>
					</div>

					<div id='global_discovery_tabs' class='jwf-label-tabs-bar'>
						<label id='lbl_select_global_rules' data-selected='true'>Global Rules</label> 
						<label id='lbl_select_other_global_rules' data-selected='false'>Other Global Rules</label> 
						<label id='lbl_select_reference_rules' data-selected='false'>Reference Rules</label>
						<label id='lbl_select_global_thresholds' data-selected='false'>Thresholds</label>
						<label id='lbl_select_global_synonym' data-selected='false'>Synonyms</label>
					</div>
					
					<div class="form-body">
						<div class="portlet-body form">

							<div class="form-body">
								<div id="content_global_rules" class="globalRules">
								 	<span class="caption-subject bold">
                            		  <a href="AddNewRuleGlobal" target="_blank" class="btn btn-primary" id="Btn_add_global_rule">Add Global Rule</a>
                            		</span>
									<table id="table_global_rules" class="table table-striped table-bordered table-hover dataTable" style="width: 100%;">
										<thead>
											<tr style="width:">
												<th><input type='checkbox' id='all_rules' style="box-shadow: 0 0 0 0px grey;" onchange='ManageGlobalRules.selectAllDataRows("rules");'></th>
												<th>Domain</th>
												<th>Rule Name</th>
												<th>Rule Type</th>
												<th>Template Column</th>
												<th>Filter Condition</th>
												<th>Right Template Filter Condition</th>
												<th>Matching Rules</th>
												<th>Expression</th>
											</tr>
										</thead>
									</table>

									<div class="form-actions noborder align-center">
										<button type="submit" id="btn_linkGlobalRules" class="btn blue" onclick="ManageGlobalRules.linkSelectedGlobalRules()">Submit</button>
									</div>
								</div>
							</div>

							<div id="content_other_global_rules" class="otherGlobalRules">
								<table id="table_other_global_rules" class="table table-striped table-bordered table-hover dataTable" style="width: 100%;">
									<thead>
										<tr style="width:">
											<th><input type='checkbox' id='other_rules' style="box-shadow: 0 0 0 0px grey;" onchange='ManageGlobalRules.selectAllDataRows("otherrules");'></th>
											<th>Domain</th>
											<th>Rule Name</th>
											<th>Rule Type</th>
											<th>Template Column</th>
											<th>Filter Condition</th>
											<th>Right Template Filter Condition</th>
											<th>Matching Rules</th>
											<th>Expression</th>
											<th>Unmatched Synonyms</th>
											<th>Right Template Unmatched Synonyms</th>
										</tr>
									</thead>
								</table>

								<div class="form-actions noborder align-center">
									<button type="submit" id="btn_linkOtherGlobalRules" class="btn blue" data-toggle="modal"
											data-target="#synonym_mapping_id" onclick="ManageGlobalRules.mapUnmatchedSynonymnsToTemplateColumns()">Submit</button>
								</div>
							</div>
							
							<div id="content_reference_rules" class="referenceRules">
								<table id="table_ref_rules" class="table table-striped table-bordered table-hover dataTable" style="width: 100%;">
									<thead>
										<tr>
											<th><input type='checkbox' id='all_refrules' style="box-shadow: 0 0 0 0px grey;" onchange='ManageGlobalRules.selectAllDataRows("refrules");'>
											</th>
											<th>Template Column</th>
											<th>Connection</th>
											<th>Table Name</th>
											<th>Template Name</th>
											<th>Reference Column</th>
											<th>Confidence</th>
										</tr>
									</thead>
								</table>
								
								<div class="form-actions noborder align-center">
									<button type="submit" id="btn_linkReferenceRules" class="btn blue" onclick="ManageGlobalRules.linkSelectedReferenceRules()">Submit</button>
								</div>
							</div>

							<div id="content_global_thresholds" class="globalThresholds">
								<table id="table_global_thresholds" class="table table-striped table-bordered table-hover dataTable" style="width: 100%;">
									<thead>
										<tr>
											<th><input type='checkbox' id='all_thresholds'
												style="box-shadow: 0 0 0 0px grey;"
												onchange='ManageGlobalRules.selectAllDataRows("thresholds");'>
											</th>
											<th>Name</th>
											<th>Null</th>
											<th>Numeric</th>
											<th>Length</th>
											<th>Data Drift</th>
											<th>Record Anomaly</th>
										</tr>
									</thead>
								</table>
								
								<div class="form-actions noborder align-center">
									<button type="submit" id="btn_linkGlobalThresholds" class="btn blue" onclick="ManageGlobalRules.linkSelectedGlobalThresholds()">Submit</button>
								</div>
							</div>
							
							<div id="content_global_synonym" class="globalSynonym">
								<button class="btn btn-primary" id="BtnAddNewSynonyms" data-toggle="modal" data-target="#myModal">Add New Synonyms</button>
								
								<table id="tblSynonymsViewList" class="table table-striped table-bordered table-hover" style="width: 100%;">
									<thead>
										<tr>
											<th>Domain Name</th>
											<th>Synonym Name</th>
											<th>User Fields</th>
											<th>Edit</th>
										</tr>
									</thead>
								</table>
							</div>

							<br> <br>
						</div>

					</div>
				</div>
			</div>
			<div style='display: none; min-width: 550px;'>
				<fieldset id='form-body-add-edit-synonyms' class="fieldset-row">
					<label for="DomainId">Domain Name</label> 
					<select id="DomainId"></select>
					<br> 
					<label for="SynonymsName">Synonym Name</label> 
					<input type="text" id="SynonymsName"> 
					<br> 
					<label for="UserFields">User Fields</label>
					<input type="text" id="UserFields">
					<br>
					<textarea id='form-validation-msg' readonly disabled data-msgstate="hint"></textarea>
				</fieldset>
			</div>
			<!-- The Dynamic popup for synonym mapping -->
			<div class="modal" id="synonym_mapping_id">
				<div class="modal-dialog"
					style="display: inline-block; position: fixed; top: 0; bottom: 0; left: 0; right: 0; height: 500px; margin: auto; overflow:auto;">
					<div class="modal-content">

						<!-- Modal Header -->
						<div class="modal-header">
							<h4 class="modal-title">Synonym Mapping</h4>
							<button type="button" class="close" data-dismiss="modal">&times;</button>
						</div>

						<!-- Modal body -->
						<div class="modal-body" id="newValues"></div>

						<div id="popup_form_validate_msg_div" class="hidden">
							<span style="padding-left:20px;color:red;">Please fill the values</span>
						</div>
							
						<!-- Modal footer -->
						<div class="modal-footer">
							<button type="submit" class="btn btn-success" id="id_saveSynonymMappings" text-align: center; >Submit</button>
							<button type="button" class="btn btn-danger"
								data-dismiss="modal">Cancel</button>
						</div>
						
					</div>
				</div>
			</div>
			
			<!-- The Dynamic popup for Right Template synonym mapping -->
			<div class="modal" id="right_template_synonym_mapping_id">
				<div class="modal-dialog"
					style="display: inline-block; position: fixed; top: 0; bottom: 0; left: 0; right: 0; height: 500px; margin: auto; overflow:auto;">
					<div class="modal-content">

						<!-- Modal Header -->
						<div class="modal-header">
							<h4 class="modal-title">Synonym Mapping</h4>
							<button type="button" class="close" data-dismiss="modal">&times;</button>
						</div>

						<!-- Modal body -->
						<div class="modal-body" id="newValues"></div>

						<div id="popup_form_validate_msg_div" class="hidden">
							<span style="padding-left:20px;color:red;">Please fill the values</span>
						</div>
							
						<!-- Modal footer -->
						<div class="modal-footer">
							<button type="submit" class="btn btn-success" id="id_rightTemplateSaveSynonymMappings" text-align: center; >Submit</button>
							<button type="button" class="btn btn-danger"
								data-dismiss="modal">Cancel</button>
						</div>
						
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<jsp:include page="footer.jsp" />

<!-- Load JWF framework module on this page -->
<head>
<script src="./assets/global/plugins/jquery.min.js"
	type="text/javascript"></script>
<script src="./assets/global/plugins/jquery-ui.min.js"></script>
<script src="./assets/js/bootstrap.min.js"></script>
<script src="./assets/global/plugins/bootstrap-multiselect.js"></script>
<link rel="stylesheet"
	href="./assets/global/plugins/bootstrap-multiselect.css">
<link rel="stylesheet" href="./assets/jwf/content/JwfSpaInfra.css">
<script src="./assets/jwf/scripts/JwfSpaInfra.js"></script>
<script src="../../extensions/Editor/js/dataTables.editor.min.js"></script>
<script src="./assets/global/plugins/jquery.dataTables.min.js" type="text/javascript">
</script>
</head>

<script>
	function loadGlobalRuleDiscoveryTabs() {

		document.documentElement.style.setProperty('--label-tabs-color', '#337ab7');

		UIManager.LabelTabs.create({
			TabsId: 'global_discovery_tabs',
			TabsPairInfo:
					[
						{ "TabLabel": "lbl_select_global_rules", "ContentDivId": "content_global_rules", "dataTableId":"table_global_rules"},
					    { "TabLabel": "lbl_select_other_global_rules", "ContentDivId": "content_other_global_rules", "dataTableId":"table_other_global_rules" },
						{ "TabLabel": "lbl_select_reference_rules", "ContentDivId": "content_reference_rules", "dataTableId":"table_ref_rules" },
						{ "TabLabel": "lbl_select_global_thresholds", "ContentDivId": "content_global_thresholds", "dataTableId":"table_global_thresholds" },
						{ "TabLabel": "lbl_select_global_synonym", "ContentDivId": "content_global_synonym" }
					],
			TabsMinWidth: 150
		});
        $('#content_other_global_rules').css('display','none');
		$('#content_reference_rules').css('display','none');
		$('#content_global_thresholds').css('display','none');
        $('#content_global_synonym').css('display','none');

	}
	
	$(document).ready(function() {

          var idData=$('#idData').val();
          var templateColumns = $('#templateColumns').val();
		  var arr_templateColumns = templateColumns.replace('[', '').replace(']', '').split(",");
          var alreadyLinkedGlobalRules=[];
          
          // Save template columns to variable
          ManageGlobalRules.templateColumns = arr_templateColumns;
          
          // Configuring back button
	      if($('#BtnBackward').attr('data-fromMapping') == 'validationView') {
	  		$('#BtnBackward').click(function() { window.location.href = 'validationCheck_View'});
	  	  } 
	  	  else if($('#BtnBackward').attr('data-fromMapping') == 'datatemplateview') {
	  		$('#BtnBackward').click(function() { window.location.href = 'datatemplateview'});
	  	  }
	  	  else if($('#BtnBackward').attr('data-fromMapping') == 'dashboard_View') {
	  		$('#BtnBackward').click(function() { window.location.href = 'dashboard_View'});
	  	  }
	  	  else {
	  		$('#BtnBackward').click(function() { window.location.href = 'validationCheck_View'});
	  	  }
          
          // Load Global Rules
          $('#table_global_rules').dataTable( {
                 'bProcessing': false,
                 'bServerSide': false,
                 'scrollX': true,
                 'bJQueryUI': true,
                 'ajax': {
                        'type': 'POST',
                        'url':  './getEligibleGlobalRulesForTemplate',
                        'headers': { 'token':$("#token").val()},
                        'data': {
                          idData: idData,
                         },
                         'dataSrc': function ( json ) {
                        	 // Get already linked global rules Ids into variable
                             var linkedGlobalRules = json.AlreadyLinkedGlobalRulesToDataTemplate;
                             
                        	 if(typeof linkedGlobalRules !== "undefined" && linkedGlobalRules.length > 0){
                        		 alreadyLinkedGlobalRules = linkedGlobalRules.split(",");
                        		 
                        		 // Saving the rules list for enable/disable functionality
                        		 ManageGlobalRules. alreadyLinkedGlobalRules = linkedGlobalRules.split(","); 
                        		 
                        		 // Saving all the linked rule Ids to SelectedRowIds
                        		 ManageGlobalRules.SelectedRowIds.Rules = alreadyLinkedGlobalRules;
                        	 }
                             
                             // Save the eligible global rules list to variable
                             ManageGlobalRules.DataSets.Rules = json.Rules;
                             
                             // Return eligible global rules list
                             return json.Rules;
                         }
                  },
                  'aoColumns': [
                        { "data": "Id",
                           render: function (data) {
                        	   if($.inArray(data, alreadyLinkedGlobalRules) != -1){
                                   return '<input type="checkbox" id="gr-check-'+data+'"  value="'+data+'" style="margin-left:32px;" data-tablename="rules" checked>';
                        	   } else {
                                   return '<input type="checkbox" id="gr-check-'+data+'"  value="'+data+'" style="margin-left:32px;" data-tablename="rules">';
                        	   }
                           }
                        },
                        { "data": "DomainName" },
                        { "data": "Name" },
                        { "data": "RuleType" },
                        { "data": "AnchorColumns" },
                        { "data" : "FilterCondition" },
                        { "data" : "RightTemplateFilterCondition" },
                        { "data" : "MatchingRules" },
                        { "data" : "EffectiveExpression" }

                  ],
                  'drawCallback': function (settings) {
						$('#table_global_rules tr td :input[type=checkbox]').off('click', ManageGlobalRules.dataRowCheckBoxClicked).on('click', ManageGlobalRules.dataRowCheckBoxClicked);
				  },
                  'createdRow': function(row, data, index){
                	  	var ruleId = data["Id"];
						if ($.inArray(ruleId, alreadyLinkedGlobalRules) != -1) { 
							$(row).attr('data-linkedrule', 'Y'); 
						}
					    
					 },
                  'columnDefs': [ 
                	  { 'orderable': false,
                		'targets': [0] 
                	  }
                  ]
            });
          
           	// Load other Global Rules
            $('#table_other_global_rules').dataTable( {
                 'bProcessing': false,
                 'bServerSide': false,
                 'scrollX' : true,
                 'bJQueryUI': true,
                 'ajax': {
                        'type': 'POST',
                        'url':  './getNonEligibleGlobalRulesForTemplate',
                        'headers': { 'token':$("#token").val()},
                        'data': {
                          idData: idData,
                        },
                        'dataSrc': function ( json ) {
                        	// Save the other global rules list to variable
                            ManageGlobalRules.DataSets.OtherRules = json.Rules;
                        	
                        	// Return other global rules list
                            return json.Rules;
                        }
                  },
                  'aoColumns': [
                        { "data": "Id",
                           render: function (data) {
                                           return '<input type="checkbox" id="ogr-check-'+data+'"  value="'+data+'" style="margin-left:32px;" data-tablename="otherrules">';
                           }
                        },
                        { "data": "DomainName" },
                        { "data": "Name" },
                        { "data": "RuleType" },
                        { "data": "AnchorColumns" },
                        { "data" : "FilterCondition" },
                        { "data" : "RightTemplateFilterCondition" },
                        { "data" : "MatchingRules" },
                        { "data" : "EffectiveExpression" },
                        { "data" : "UnmatchedSynonyms" },
                        { "data" : "RightTemplateUnmatchedSynonyms",
                            render: function (data, type, row, meta) {
                            	   var rowId = row.Id;
                            	   
                            	    if(data.length > 0){
                            	    	data = data + ' <button class="btn-link" data-toggle="modal" data-target="#right_template_synonym_mapping_id" onclick="ManageGlobalRules.mapUnmatchedSynonymnsToRightTemplateColumns('+rowId+')"><i class="fa fa-plus-square-o" aria-hidden="true" style="color:green;font-size: large;"></i></button>';
                            	    }
                                 return data;
                            }
                         }
                        
                  ],
                  'drawCallback': function (settings) {
						$('#table_other_global_rules tr td :input[type=checkbox]').off('click', ManageGlobalRules.dataRowCheckBoxClicked).on('click', ManageGlobalRules.dataRowCheckBoxClicked);
				  },
                  'columnDefs': [ 
                	  { 'orderable': false,
                		'targets': [0] 
                	  }
                  ]
            });
           	
           	// Load eligible reference rules
            $('#table_ref_rules').dataTable( {
                   'bProcessing': false,
                   'bServerSide': false,
                   'scrollX' : true,
                   'bJQueryUI': true,
                   'ajax': {
                          'type': 'POST',
                          'url':  './getEligibleReferenceRulesForTemplate',
                          'headers': { 'token':$("#token").val()},
                          'data': {
                            idData: idData,
                          },
                           'dataSrc': function ( json ) {
                        	   // Save the Reference rules list to variable
                               ManageGlobalRules.DataSets.ReferenceRules = json.ReferenceRules;
                        	   
                        	   // Return the reference rules list
                        	   return json.ReferenceRules;
                           }
                    },
                    'aoColumns': [
                         { "data": "Id",
                            render: function (data) {
                                 return '<input type="checkbox" id="rf-check-'+data+'"  value="'+data+'" style="margin-left:32px;" data-tablename="refrules">';
                            }
                          },
                         { "data": "NewColumnName" },
                         { "data": "MasterConnectionName" },
                         { "data": "MasterTableName" },
                         { "data": "MasterTemplateName" },
                         { "data": "MasterColumnName" },
                         { "data": "ConfidenceLevel" }

                    ],
                    'drawCallback': function (settings) {
						$('#table_ref_rules tr td :input[type=checkbox]').off('click', ManageGlobalRules.dataRowCheckBoxClicked).on('click', ManageGlobalRules.dataRowCheckBoxClicked);
				    },
                    'columnDefs': [ 
                  	  { 'orderable': false,
                  		'targets': [0] 
                  	  }
                    ]
            });
           	
        	// Load global thresholds
            $('#table_global_thresholds').dataTable( {
                        'bProcessing': false,
                        'bServerSide': false,
                        'scrollX' : true,
                        'bJQueryUI': true,
                        'ajax': {
                               'type': 'POST',
                               'url':  './getEligibleGlobalThresholdsForTemplate',
                               'headers': { 'token':$("#token").val()},
                               'data': {
                                 idData: idData,
                               },
                                'dataSrc': function ( json ) {
                                	 // Save the global thresholds list to variable
                                    ManageGlobalRules.DataSets.Thresholds = json.Thresholds;
                             	   
                             	   // Return the global thresholds
                             	   return json.Thresholds;
                                }
                         },
                         'aoColumns': [
                               { "data": "Id",
                                   render: function (data) {
                                       return '<input type="checkbox" id="gt-check-'+data+'"  value="'+data+'" style="margin-left:32px;" data-tablename="thresholds">';
                                   }
                                },
								{ "data": "globalColumnName" },
								{ "data": "nullCountThreshold" },
								{ "data": "numericalThreshold" },
								{ "data": "lengthCheckThreshold" },
								{ "data": "dataDriftThreshold" },
								{ "data": "recordAnomalyThreshold" }

                         ],
                         'drawCallback': function (settings) {
     						$('#table_global_thresholds tr td :input[type=checkbox]').off('click', ManageGlobalRules.dataRowCheckBoxClicked).on('click', ManageGlobalRules.dataRowCheckBoxClicked);
     				     },
                         'columnDefs': [ 
                       	  { 'orderable': false,
                       		'targets': [0] 
                       	  }
                         ]

              });
        	
            $("#id_saveSynonymMappings").on("click", function() {
            	ManageGlobalRules.saveSynonymMappings();
            });
            
            $("#id_rightTemplateSaveSynonymMappings").on("click", function() {
            	ManageGlobalRules.saveSynonymMappingsForRightTemplate();
            });
           	
            initializeJwfSpaInfra();       // Load JWF framework module on this page
            loadGlobalRuleDiscoveryTabs();
            ManageSynonymsDataEntry.setup();
            ManageSynonymsDataEntry.loadSynonymsViewList(0);
	});
	
	var ManageGlobalRules = {
			// Variable to hold template columns
			templateColumns: [],
			
			// Variable to hold unmatched synonyms list
			unmatched_synonyms: [],
				 
			// Variable to hold already linked global rules
			alreadyLinkedGlobalRules: [],
			
			// Variable to store selected row Ids 
			SelectedRowIds: { Rules: [], OtherRules: [], ReferenceRules: [], Thresholds: [] },
			
			// Variable to store the result datasets
			DataSets: { Rules: {}, OtherRules: {}, ReferenceRules: {}, Thresholds: {} },
			
			isGlobalRuleAlreadyLinked : function(selectedRuleId){
				var isLinkedRule = ($.inArray(selectedRuleId, ManageGlobalRules.alreadyLinkedGlobalRules) != -1) ? true : false;
				return isLinkedRule;
			},
			
			// Check/Uncheck all the checkboxes
			selectAllDataRows: function (selectedTableName) {
				var sTableName = selectedTableName;
				
				var enableAllRows = false;

				if ((sTableName === 'rules' && $('#all_rules').prop("checked") == true) || (sTableName === 'refrules' && $('#all_refrules').prop("checked") == true) 
						|| (sTableName === 'otherrules' && $('#other_rules').prop("checked") == true)|| (sTableName === 'thresholds' && $('#all_thresholds').prop("checked") == true)) {
					enableAllRows = true;
				}
				
				// Variable to get the Key name for each dataset and datatable
				var dataset_key_name = (sTableName === 'rules') ? 'Rules' : (sTableName === 'otherrules')? 'OtherRules': (sTableName === 'refrules') ? 'ReferenceRules' : 'Thresholds';
				
				// Variable to get Jquery datatable name
				var datatable_name = (sTableName === 'rules') ? 'table_global_rules' : (sTableName === 'otherrules')? 'table_other_global_rules': (sTableName === 'refrules') ? 'table_ref_rules' : 'table_global_thresholds';
					
				// Create new list of selected row Ids
				var updated_selected_Ids_list = [];
				
				if (enableAllRows) {
					// Enable all the checkboxes
					$('#'+datatable_name).DataTable().$("input[type='checkbox']").prop('checked', true);
					
					// Read all the rows data from datatable
					var allDataRows = ManageGlobalRules.DataSets[dataset_key_name];
					
					// Add all the ruleIds to selectedRowIds list
					allDataRows.some(function (oDataRow) {
						var ruleId = oDataRow.Id;
						updated_selected_Ids_list.push(ruleId);
					});	
					
				} else {
					// Uncheck all the checkboxes and Clear the selected row Ids
					$('#'+datatable_name).DataTable().$("input[type='checkbox']").prop('checked', false);
					
					// For global rules Tab, keep the already linked rules checked
					if (sTableName === 'rules'){
						
						// Keep already linked rules checked and save to selectedRowIds list
						$.each(ManageGlobalRules.alreadyLinkedGlobalRules, function( index, value ) {
							var ruleId = value;
							$('#table_global_rules').DataTable().$("#gr-check-"+ruleId).prop('checked', true);
							
							// Add rowId to list
							updated_selected_Ids_list.push(ruleId);
						});
						
					}
				}
				
				// Update the selectedRowIds list
				ManageGlobalRules.SelectedRowIds[dataset_key_name] = updated_selected_Ids_list;
			},
			
			dataRowCheckBoxClicked: function (oEvent) {
				var sTableName = $(oEvent.target).attr('data-tablename');
				var dataset_key_name = (sTableName === 'rules') ? 'Rules' : (sTableName === 'otherrules')? 'OtherRules': (sTableName === 'refrules') ? 'ReferenceRules' : 'Thresholds';
				
				// Get Select-All checkbox Id for the table
				var select_all_checkbox_id = (sTableName === 'rules') ? '#all_rules' : (sTableName === 'otherrules')? '#other_rules': (sTableName === 'refrules') ? '#all_refrules' : '#all_thresholds';

				// Get the rowId from the id by splitting eg: gr-check-12 output: 12 
				var sRowId = oEvent.target.id.split('-')[2];
				console.log("sRowId: " + sRowId);
				console.log("checked: " + oEvent.target.checked);
				
				// Get the list of rowIds selected till now
				var selectedRowIds = ManageGlobalRules.SelectedRowIds[dataset_key_name];
				
				// Read all the rows data from datatable
				var allDataRows = ManageGlobalRules.DataSets[dataset_key_name];
				
				// check if the checkbox is checked or unchecked
				if(oEvent.target.checked == true){
					// Add this newly selected Id to list
					selectedRowIds.push(sRowId.toString());
					
					// Check if all the rules are selected
					if(allDataRows.length == selectedRowIds.length){
						// Enable select_all checkbox
						$(select_all_checkbox_id).prop('checked', true);
					}
					
				} else if (oEvent.target.checked == false){
					// Uncheck select-all check box
					$(select_all_checkbox_id).prop('checked', false);
					
					// remove the deselected Id from list
					var updatedSelectedRowIds = [];
					$.each(selectedRowIds, function( index, ruleId ) {
						if(ruleId != sRowId){
							updatedSelectedRowIds.push(ruleId);
						}
						
					});
					selectedRowIds = updatedSelectedRowIds;
				}
				
				// Update in the variable
				ManageGlobalRules.SelectedRowIds[dataset_key_name] = selectedRowIds;
			},
	
			linkSelectedGlobalRules: function() {
				var selectedRowsJson = { Rules: [] , DelinkedRules: []};
				
				// Get Selected Rules Ids list
				var selectedRowIds = ManageGlobalRules.SelectedRowIds['Rules'];
				
				// Read all the rows data from datatable
				var allDataRows = ManageGlobalRules.DataSets.Rules;
				
				selectedRowIds.forEach(function (sRowId, nIndex) {
					
					allDataRows.some(function (oDataRow) {
						if(oDataRow.Id == sRowId){
							// Check if rule is not already linked
							if(!ManageGlobalRules.isGlobalRuleAlreadyLinked(sRowId)){
								console.log("linked sRowId: " + sRowId);
								selectedRowsJson.Rules.push(oDataRow);
							}
						}
					});

				});
				
				// Get delinked Rules list
				ManageGlobalRules.alreadyLinkedGlobalRules.forEach(function (sRowId, nIndex) {
					
					allDataRows.some(function (oDataRow) {
						if(oDataRow.Id == sRowId){
							// Check if rule is not present in selected Ids list
							if($.inArray(sRowId, selectedRowIds) == -1){
								console.log("delinked sRowId: " + sRowId);
								var ruleId = parseInt(sRowId);
								var req_data_req = { "Id": ruleId };
								selectedRowsJson.DelinkedRules.push(req_data_req);
							}
						}
					});

				});
				
				// Make ajax call
				var form_data = {
					idData : $('#idData').val(),
					selectedGlobalRules :  JSON.stringify(selectedRowsJson)
				};
				
				$.ajax({
					url: 'linkSelectedGlobalRulesToTemplate',
					type: 'POST',
					headers: { 'token':$("#token").val()},
					datatype: 'json',
					data: form_data,
					success: function (data) {
						var j_obj = $.parseJSON(data);
						var msg = j_obj.message;
						var status = j_obj.status;
						
						if (status == 'success' || status == 'failed') {
							toastr.info(msg);
						} else{
							toastr.info("Unexpected error");
						}
						
						// Reload the page
						setTimeout(function() {window.location.reload();}, 1000);
					},
					error: function (xhr, textStatus,
						errorThrown) {
					}
				});

			},
			
			mapUnmatchedSynonymnsToTemplateColumns: function() {

				ManageGlobalRules.unmatched_synonyms = [];
				
				// Get Selected Reference rules list
				var selectedRowIds = ManageGlobalRules.SelectedRowIds['OtherRules'];
				
				// Check if any rules are selected
				if(selectedRowIds.length > 0){
					// Read all the rows data from datatable
					var allDataRows = ManageGlobalRules.DataSets.OtherRules;
					
					// Unique unmatched synonyms list
					var unmatched_synonym_array = [];
					
					selectedRowIds.forEach(function (sRowId, nIndex) {
						
						allDataRows.some(function (oDataRow) {
							if(oDataRow.Id == sRowId){							
								// Get unmatched synonyms
								var unmatchedSynonyms = oDataRow.UnmatchedSynonyms;
								var syn_array = unmatchedSynonyms.split(",");
								
								// Check if synonym already present in list else add
								$.each(syn_array,function(i, new_synonym){
									
									if($.inArray(new_synonym, unmatched_synonym_array) == -1){
										unmatched_synonym_array.push(new_synonym);
									} 
									
								});
								
							}
						});
					});
					
					ManageGlobalRules.unmatched_synonyms = unmatched_synonym_array;
					console.log("unmatched_synonym_array: " + unmatched_synonym_array);
					
					// Remove the div if already exists
					$("#synonym_mapping_id #syn_map_div").remove();
					
					$("#synonym_mapping_id .input_error").remove();
					
					
					// Prepare the body
					$.each(unmatched_synonym_array,function(i,synonym)
	                {
						console.log("i: " +i);
						console.log("synonym: " +synonym);
						
						var body = "<div id=\"syn_map_div\"><label><b style='color:blue;'>"+synonym+"</b></label><select class=\"form-control\" id="+synonym+" name="+synonym+"><option value=\"-1\">Select column</option>";
						$.each(ManageGlobalRules.templateColumns,function(i,column_name)
				        {
							body = body + "<option value=\""+column_name+"\">"+column_name+"</option>";
				        });
						body = body + "</select></div>";
						
						$('#synonym_mapping_id .modal-body').append(body);

	                });
					// End body
				
				} else {
					
					// Remove the modal body div if already exists
					$("#synonym_mapping_id #syn_map_div").remove();
					
					// Hide the modal
					$("#synonym_mapping_id").modal("hide");
					
					toastr.error("No global rules are selected");
				}
				
			},
			
			mapUnmatchedSynonymnsToRightTemplateColumns: function(sRowId) {

				ManageGlobalRules.right_template_unmatched_synonyms = [];
				ManageGlobalRules.right_template_Id = 0;
				
				// Read all the rows data from datatable
				var allDataRows = ManageGlobalRules.DataSets.OtherRules;
				
				// Unique unmatched synonyms list
				var unmatched_synonym_array = [];
				
				allDataRows.some(function (oDataRow) {
					if(oDataRow.Id == sRowId){
						// Get rightTemplateId
						ManageGlobalRules.right_template_Id = oDataRow.RightTemplateId;

						// Get unmatched synonyms
						var unmatchedSynonyms = oDataRow.RightTemplateUnmatchedSynonyms;
						var syn_array = unmatchedSynonyms.split(",");
						
						// Check if synonym already present in list else add
						$.each(syn_array,function(i, new_synonym){
							
							if($.inArray(new_synonym, unmatched_synonym_array) == -1){
								unmatched_synonym_array.push(new_synonym);
							} 
							
						});
						
					}
				});
				
				ManageGlobalRules.right_template_unmatched_synonyms = unmatched_synonym_array;
				console.log("unmatched_synonym_array: " + unmatched_synonym_array);
				
				// Remove the div if already exists
				$("#right_template_synonym_mapping_id #syn_map_div").remove();
				
				$("#right_template_synonym_mapping_id .input_error").remove();
				
				var form_data = {
					idData : ManageGlobalRules.right_template_Id
				};
				
				// Get Right Template columns
				$.ajax({
						url: 'changeDataColumnAjax',
						type: 'POST',
						headers: { 'token':$("#token").val()},
						datatype: 'json',
						data: form_data,
						success: function (data) {
							var j_obj = $.parseJSON(data);
							var columnsList = JSON.parse(j_obj.success);
							
							if (j_obj.hasOwnProperty('success')) {
								
								// Prepare the body
								$.each(unmatched_synonym_array,function(i,synonym)
				                {
									console.log("i: " +i);
									console.log("synonym: " +synonym);
									
									var body = "<div id=\"syn_map_div\"><label><b style='color:blue;'>"+synonym+"</b></label><select class=\"form-control\" id="+synonym+" name="+synonym+"><option value=\"-1\">Select column</option>";
									$.each(columnsList,function(i,column_name)
							        {
										body = body + "<option value=\""+column_name+"\">"+column_name+"</option>";
							        });
									body = body + "</select></div>";
									
									$('#right_template_synonym_mapping_id .modal-body').append(body);

				                });
								// End body
							}
							else{
								
								toastr.info("Failed to fetch Right Template columns for synonym mapping");
								
								// Reload the page
								setTimeout(function() {window.location.reload();}, 2000);
							} 
							
						},
						error: function (xhr, textStatus,
							errorThrown) {
							toastr.info("Unexpected error occurred");
							
							// Reload the page
							setTimeout(function() {window.location.reload();}, 2000);
						}
					});
				
			},
			
			saveSynonymMappingsForRightTemplate : function(){
				var emptyValueCount = 0;
				var synonym_mapping_json = {};
				
				$("#right_template_synonym_mapping_id .input_error").remove();
				
				// Read the synonymn and value from modal
				$.each(ManageGlobalRules.right_template_unmatched_synonyms,function(i,synonym)
                {
					console.log("i: " +i);
					console.log("synonym: " +synonym);
					
            	   	var value =  $('#right_template_synonym_mapping_id #'+synonym).val();
            	   	console.log("value: " +value);
            	   
            	   	if(value == -1){
            		   emptyValueCount = emptyValueCount + 1; 
            		   
            		   $('<span class="input_error" style="font-size:12px;color:red">Please select column for '+synonym+'</span>')
   						.insertAfter($('#'+synonym)); 
            	   	} else{
            	   		synonym_mapping_json[synonym] = value;
            	   	}
            	   
                });
				
				console.log("emptyValueCount: " + emptyValueCount);
				if(emptyValueCount == 0){
					console.log("inside..");
					
					console.log("synonym_mapping_json: " + JSON.stringify(synonym_mapping_json));
					
					$("#right_template_synonym_mapping_id").modal('hide');
					$('.modal-backdrop').remove();
					
					// Ajax call
					var form_data = {
						idData : $('#idData').val(),
						synonymMappings :  JSON.stringify(synonym_mapping_json),
					};
					
					$.ajax({
						url: 'saveSynonymMappings',
						type: 'POST',
						headers: { 'token':$("#token").val()},
						datatype: 'json',
						data: form_data,
						success: function (data) {
							var j_obj = $.parseJSON(data);
							var msg = j_obj.message;
							var status = j_obj.status;
							
							if (status == 'success') {
								toastr.info(msg);
								
								// Reload the page
								setTimeout(function() {window.location.reload();}, 1000);
							}
							else if (status == 'failed'){
								
								// Displaying mapping errors
								if(j_obj.hasOwnProperty("mappingErrors")){
									
									var mappingErrors = j_obj.mappingErrors;
									
									if(mappingErrors.length <= 6){
										$.each(mappingErrors,function(i){
											toastr.info(mappingErrors[i]);
										});
									}
								}
								
								toastr.info(msg);
								
								// Reload the page
								setTimeout(function() {window.location.reload();}, 2000);
							} 
							
						},
						error: function (xhr, textStatus,
							errorThrown) {
							toastr.info("Unexpected error occurred");
							
							// Reload the page
							setTimeout(function() {window.location.reload();}, 2000);
						}
					});
					
				} 
			},
			
			saveSynonymMappings : function(){
				var emptyValueCount = 0;
				var synonym_mapping_json = {};
				
				$("#synonym_mapping_id .input_error").remove();
				
				// Read the synonymn and value from modal
				$.each(ManageGlobalRules.unmatched_synonyms,function(i,synonym)
                {
					console.log("i: " +i);
					console.log("synonym: " +synonym);
					
            	   	var value =  $('#synonym_mapping_id #'+synonym).val();
            	   	console.log("value: " +value);
            	   
            	   	if(value == -1){
            		   emptyValueCount = emptyValueCount + 1; 
            		   
            		   $('<span class="input_error" style="font-size:12px;color:red">Please select column for '+synonym+'</span>')
   						.insertAfter($('#'+synonym)); 
            	   	} else{
            	   		synonym_mapping_json[synonym] = value;
            	   	}
            	   
                });
				
				console.log("emptyValueCount: " + emptyValueCount);
				if(emptyValueCount == 0){
					console.log("inside..");
					
					console.log("synonym_mapping_json: " + JSON.stringify(synonym_mapping_json));
					
					$("#synonym_mapping_id").modal('hide');
					$('.modal-backdrop').remove();
					
					// Ajax call
					var form_data = {
						idData : $('#idData').val(),
						synonymMappings :  JSON.stringify(synonym_mapping_json),
					};
					
					$.ajax({
						url: 'saveSynonymMappings',
						type: 'POST',
						headers: { 'token':$("#token").val()},
						datatype: 'json',
						data: form_data,
						success: function (data) {
							var j_obj = $.parseJSON(data);
							var msg = j_obj.message;
							var status = j_obj.status;
							
							if (status == 'success') {
								toastr.info(msg);
								
								ManageGlobalRules.linkSelectedOtherGlobalRules();
							}
							else if (status == 'failed'){
								
								// Displaying mapping errors
								if(j_obj.hasOwnProperty("mappingErrors")){
									
									var mappingErrors = j_obj.mappingErrors;
									
									if(mappingErrors.length <= 6){
										$.each(mappingErrors,function(i){
											toastr.info(mappingErrors[i]);
										});
									}
								}
								
								toastr.info(msg);
								
								// Reload the page
								setTimeout(function() {window.location.reload();}, 2000);
							} 
							
						},
						error: function (xhr, textStatus,
							errorThrown) {
							toastr.info("Unexpected error occurred");
							
							// Reload the page
							setTimeout(function() {window.location.reload();}, 2000);
						}
					});
					
				} 
				
			},
			
			linkSelectedOtherGlobalRules: function() {
				console.log("linkSelectedOtherGlobalRules function");
				
				var selectedRowsJson = { Rules: [] };
				
				// Get Selected Reference rules list
				var selectedRowIds = ManageGlobalRules.SelectedRowIds['OtherRules'];
				
				// Read all the rows data from datatable
				var allDataRows = ManageGlobalRules.DataSets.OtherRules;
				
				selectedRowIds.forEach(function (sRowId, nIndex) {
					
					allDataRows.some(function (oDataRow) {
						if(oDataRow.Id == sRowId){
							var req_data_req = { "Id": oDataRow.Id};
							selectedRowsJson.Rules.push(req_data_req);
						}
					});
				});
				
				// Make ajax call
				var form_data = {
					idData : $('#idData').val(),
					selectedGlobalRules :  JSON.stringify(selectedRowsJson),
				};
				
				$.ajax({
					url: 'linkSelectedOtherGlobalRulesToTemplate',
					type: 'POST',
					headers: { 'token':$("#token").val()},
					datatype: 'json',
					data: form_data,
					success: function (data) {
						var j_obj = $.parseJSON(data);
						var msg = j_obj.message;
						var status = j_obj.status;
						
						if (status == 'success' || status == 'failed') {
							toastr.info(msg);
						} else{
							toastr.info("Unexpected error");
						}
						
						// Reload the page
						setTimeout(function() {window.location.reload();}, 2000);
					},
					error: function (xhr, textStatus,
						errorThrown) {
					}
				});
			},
			
			linkSelectedReferenceRules: function() {
				var selectedRowsJson = { ReferenceRules: [] };
				
				// Get Selected Reference rules list
				var selectedRowIds = ManageGlobalRules.SelectedRowIds['ReferenceRules'];
				
				// Read all the rows data from datatable
				var allDataRows = ManageGlobalRules.DataSets.ReferenceRules;
				
				selectedRowIds.forEach(function (sRowId, nIndex) {
					
					allDataRows.some(function (oDataRow) {
						if(oDataRow.Id == sRowId){
							selectedRowsJson.ReferenceRules.push(oDataRow);
						}
					});
				});
				
				// Make ajax call
				var form_data = {
					idData : $('#idData').val(),
					selectedReferenceRules :  JSON.stringify(selectedRowsJson)
				};
				
				$.ajax({
					url: 'linkSelectedReferenceRulesToTemplate',
					type: 'POST',
					headers: { 'token':$("#token").val()},
					datatype: 'json',
					data: form_data,
					success: function (data) {
						var j_obj = $.parseJSON(data);
						var msg = j_obj.message;
						var status = j_obj.status;
						
						if (status == 'success' || status == 'failed') {
							toastr.info(msg);
						} else{
							toastr.info("Unexpected error");
						}
						
						// Reload the page
						setTimeout(function() {window.location.reload();}, 1000);
					},
					error: function (xhr, textStatus,
						errorThrown) {
					}
				});
			},
			
			linkSelectedGlobalThresholds: function() {
				var selectedRowsJson = { Thresholds: [] };
				
				// Get Linked Rules list
				var selectedRowIds = ManageGlobalRules.SelectedRowIds['Thresholds'];
				
				// Read all the rows data from datatable
				var allDataRows = ManageGlobalRules.DataSets.Thresholds;
				
				selectedRowIds.forEach(function (sRowId, nIndex) {
					
					allDataRows.some(function (oDataRow) {
						if(oDataRow.Id == sRowId){
							var req_data_req = { "Id": oDataRow.Id, "ColumnName": oDataRow.globalColumnName };
							selectedRowsJson.Thresholds.push(req_data_req);
						}
					});
				});
				
				// Make ajax call
				var form_data = {
					idData : $('#idData').val(),
					selectedThresholds :  JSON.stringify(selectedRowsJson)
				};
				
				$.ajax({
					url: 'linkSelectedGlobalThresholdsToTemplate',
					type: 'POST',
					headers: { 'token':$("#token").val()},
					datatype: 'json',
					data: form_data,
					success: function (data) {
						var j_obj = $.parseJSON(data);
						var msg = j_obj.message;
						var status = j_obj.status;
						
						if (status == 'success') {
							toastr.info(msg);
							
						} else if (status == 'failed') {
							
							// Displaying mapping errors
							if(j_obj.hasOwnProperty("mappingErrors")){
								
								var mappingErrors = j_obj.mappingErrors;
								
								if(mappingErrors.length <= 6){
									$.each(mappingErrors,function(i){
										toastr.info(mappingErrors[i]);
									});
								}
							}
							
							toastr.info(msg);
							
						} else{
							toastr.info("Unexpected error");
						}
						
						// Reload the page
						setTimeout(function() {window.location.reload();}, 1000);
					},
					error: function (xhr, textStatus,
						errorThrown) {
					}
				});
			}
	}
	
	var ManageSynonymsDataEntry = {
			DataSet: {},
			PageOptionLists: {},
			SynonymsRecordToSave: {},
			SynonymsRecordValidToSave: false,
			MsgTextArea: null,
			DialogBody: null,
			setup: function() {
				ManageSynonymsDataEntry.MsgTextArea = $('#form-validation-msg')[0];
				ManageSynonymsDataEntry.DialogBody = $('#form-body-add-edit-synonyms')[0];

				$('#BtnAddNewSynonyms').on('click', function() { ManageSynonymsDataEntry.promoteAddEditDialog('-1') });
				$('#SynonymsName').on('change', ManageSynonymsDataEntry.onChangeData);
				$('#UserFields').on('change', ManageSynonymsDataEntry.onChangeData);

			},
			setValidationMsg: function(sMsg, lError) {
				var sMsgMode = (lError) ? "error" : "hint";

				ManageSynonymsDataEntry.MsgTextArea.value = sMsg;
				ManageSynonymsDataEntry.MsgTextArea.setAttribute("data-msgstate", sMsgMode);
			},
			onChangeData: function(oEvent) {
				var sFieldName = oEvent.target.id, sFieldValue = $('#' + sFieldName).val().trim(), lValidData = true, sErrorMsg = '', sUserField = '';
				var sSynonymsName = $('#SynonymsName').val().trim(),  sUserFields = $('#UserFields').val().trim();
				var lIsMainDataBlank = ( (sSynonymsName.length < 1) || (sUserFields.length < 1) ) ? true : false;

				if (sFieldName === 'SynonymsName') {
					lValidData = /^[a-zA-Z0-9_]+$/.test(sFieldValue);
					sErrorMsg = (lValidData) ? "" : "{field}: Only alphanumeric and underscore characters allowed\nExamples: 'emp_id' Or 'employee_id' Or 'empid' Or 'empno'";
					sUserField = 'Synonyms Name';
				} else {
					lValidData = /^[a-zA-Z0-9_ ,]+$/.test(sFieldValue);
					sErrorMsg = (lValidData) ? '' :
							  "{field}: Only alphanumeric and underscore characters allowed in User Fields. MUST use comma (,) optionally along with space to seprate multiple user fields.  "
							+ "If space entered between two words without comma may result into global rules and thresholds based DQ checks to work wrongly or even not getting applied.\n"
							+ "Example: 'emp_id1, emp_id2,   emp_no,emp_no1'"
					sUserField = 'User Fields';
				}

				if (sErrorMsg.length > 0) {
					sErrorMsg = sErrorMsg.replace('{field}', sUserField);
					ManageSynonymsDataEntry.setValidationMsg(sErrorMsg,true);
					ManageSynonymsDataEntry.SynonymsRecordValidToSave = false;
				} else {
					ManageSynonymsDataEntry.setValidationMsg( ('{field}: Entered value passed validation'.replace('{field}', sUserField)) ,false);
					ManageSynonymsDataEntry.SynonymsRecordValidToSave = true;
				}

				if (lIsMainDataBlank) { ManageSynonymsDataEntry.SynonymsRecordValidToSave = false; }
			},
			loadSynonymsViewList: function(nLoadContext) {
				JwfAjaxWrapper({
					WaitMsg: 'Reloading Synonyms',
					Url: 'loadSynonymsViewList',
					Headers:$("#token").val(),
					Data: { LoadContext: nLoadContext },
					CallBackFunction: function(oResponse) {
						ManageSynonymsDataEntry.DataSet = oResponse.DataSet;
						ManageSynonymsDataEntry.PageOptionLists = (nLoadContext < 1) ? oResponse.PageOptionLists : ManageSynonymsDataEntry.PageOptionLists;

						PageCtrl.debug.log('ManageSynonymsDataEntry loadSynonymsViewList', Array.isArray(ManageSynonymsDataEntry.DataSet) + ',' + Array.isArray(ManageSynonymsDataEntry.PageOptionLists['DOMAIN_LIST']) + ',' + nLoadContext);

						ManageSynonymsDataEntry.DataTable = $("#tblSynonymsViewList").DataTable ({
							"data" : ManageSynonymsDataEntry.DataSet,
							"columns" : [
								{ "data" : "DomainName" },
								{ "data" : "SynonymsName" },
								{ "data" : "UserFields" },
								{ "data" : "SynonymsId-edit" }

							],
							order: [[ 0, "desc" ]],
							destroy: true,
							drawCallback: function( oSettings ) {
								$('#tblSynonymsViewList a').off('click', ManageSynonymsDataEntry.AnkerIconClicked).on('click', ManageSynonymsDataEntry.AnkerIconClicked);
							}
						});
					}
				});
			},
			AnkerIconClicked:  function(oEvent) {
				var lFoundId = false, sClickedAnkerId = oEvent.target.parentElement.id, sRowId = '-1';
				var sDeleteDisabledMsg = 'Delete is disabled as Synonyms affect global rules and thresholds functionality directly.   '
						+ 'Hence accidental or incorrect delete of Synonyms may result into global rules and thresholds based DQ checks to work wrongly or even not getting applied.';

				if (sClickedAnkerId.indexOf('delete') > -1) {
					Modal.confirmDialog(2, 'Delete Disabled',	sDeleteDisabledMsg, ['Ok'] );
				} else {
					if ( sClickedAnkerId.split('-').length	> 1 ) { sRowId = sClickedAnkerId.split('-')[1]; }
					ManageSynonymsDataEntry.promoteAddEditDialog(sRowId);
				}
			},
			fillSynonymsForm: function(lNewSynonyms, nRowIndex) {
				var oSynonymsRecordToSave = (lNewSynonyms) ? { "DomainId":"5", "UserFields":"","SynonymsName":"","SynonymsId":"-1"} : JSON.parse(JSON.stringify(ManageSynonymsDataEntry.DataSet[nRowIndex]));
				var sDefaultMsg = 'Enter or modify synonyms details';

				ManageSynonymsDataEntry.SynonymsRecordToSave = oSynonymsRecordToSave;
				$('#SynonymsName').val(oSynonymsRecordToSave.SynonymsName);
				$('#UserFields').val(oSynonymsRecordToSave.UserFields);
				UIManager.fillSelect($('#DomainId')[0],ManageSynonymsDataEntry.PageOptionLists['DOMAIN_LIST'], oSynonymsRecordToSave.DomainId);

				$('#DomainId').prop("disabled", ( (lNewSynonyms) ? false : true ) );

				if (lNewSynonyms) {
					$('#DomainId').focus();
					ManageSynonymsDataEntry.SynonymsRecordValidToSave = false;
					ManageSynonymsDataEntry.setValidationMsg(sDefaultMsg, true);
				} else {
					$('#SynonymsName').focus();
					ManageSynonymsDataEntry.SynonymsRecordValidToSave = true;
					ManageSynonymsDataEntry.setValidationMsg(sDefaultMsg, false);
				}
			},
			saveSynonymsData: function() {
				var oSynonymsRecordToSave = UTIL.extractSubObjectByFields(ManageSynonymsDataEntry.SynonymsRecordToSave, 'DomainId,UserFields,DomainName,SynonymsName,SynonymsId');
				oSynonymsRecordToSave.SynonymsName = $('#SynonymsName').val();
				oSynonymsRecordToSave.UserFields = $('#UserFields').val();
				oSynonymsRecordToSave.DomainId = $('#DomainId').val();

				sValidationMsg = ManageSynonymsDataEntry.SynonymsRecordValidToSave;
				PageCtrl.debug.log('Is record valid to save', ManageSynonymsDataEntry.SynonymsRecordValidToSave);
				var sValidationMsg = checkInputText() ? true : false;

				if (sValidationMsg) {
					ManageSynonymsDataEntry.submitSynonymsData(oSynonymsRecordToSave);
				} else {
					alert('Vulnerability in submitted data, not allowed to submit!');
					/* Modal.confirmDialog(4, 'Invalid data', 'Kindly click save button after validation text box shows all data as valid. Your changes to Synonyms are NOT saved.', ['Ok'] ); */
				}
			},
			submitSynonymsData: function(oSynonymsRecordToSave) {

				JwfAjaxWrapper({
					WaitMsg: 'Saving Synonyms',
					Url: 'SaveSynonymsFromViewList',
					Headers:$("#token").val(),
					Data: { SynonymsRecordToSave: JSON.stringify(oSynonymsRecordToSave) },
					CallBackFunction: function(oResponse) {
						if (oResponse.Result) {

							setTimeout(
									function() {
										window.location.reload();
									}, 500);

							//ManageSynonymsDataEntry.loadSynonymsViewList(1);
						} else {
							Modal.confirmDialog(3, 'Save Failed', 'Problem in one of the input parameters', ['Ok'] );
							setTimeout(
									function() {
										window.location.reload();
									}, 1000);
						}
					}
				});
			},
			promoteAddEditDialog: function(sRowId) {
				var lNewSynonyms = (sRowId === '-1') ? true : false;
				var sModeTitle = (lNewSynonyms) ? 'Add New Synonyms' : 'Edit Synonyms';

				var nRowIndex = (lNewSynonyms) ? -1 : UTIL.indexOfRecord(ManageSynonymsDataEntry.DataSet, 'SynonymsId', sRowId);

				ManageSynonymsDataEntry.fillSynonymsForm(lNewSynonyms, nRowIndex);

				Modal.customDialog(1, sModeTitle,
						{ 'BodyDomObj': ManageSynonymsDataEntry.DialogBody,'HeaderHtml': null },
						['Save', 'Cancel'], { onDialogClose: cbPromoteAddEditDialog, onDialogLoad: cbPromoteAddEditDialog });

				return;

				function	cbPromoteAddEditDialog(oEvent) {
					var sEventId = oEvent.EventId, sBtnSelected = oEvent.ClickedButton;

					switch(sEventId) {
						case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
							PageCtrl.debug.log('on Dialog load','ok');
							break;

						case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
							if (sBtnSelected === 'Save') { ManageSynonymsDataEntry.saveSynonymsData(); }
							break;
					}
				}
			}
		}

</script>