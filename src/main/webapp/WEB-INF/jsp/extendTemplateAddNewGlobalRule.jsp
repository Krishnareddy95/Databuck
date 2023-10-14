
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />

<jsp:include page="container.jsp" />
<jsp:include page="checkVulnerability.jsp" />
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
							<span class="caption-subject bold "> Create Extended
								Template Rule </span>
						</div>
					</div>
					<div class="portlet-body form">

						<div class="form-body">
							<div class="row">

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="rulenameid"> <label for="rulenameid">Extended
											Template Rule Name *</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="descid"> <label for="descid">Description</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>

							<!-- <div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control " name="ruleCategory"
											id="ruleCategoryid">
											<option value="-1">Choose Rule Category</option>
											<option value="Referential">Referential</option>
											<option value="Cross Referential">Cross Referential</option>
											<option value="Orphan">Orphan</option>
											<option value="Regular Expression">Regular
												Expression</option>
										</select> <label for="ruleCategoryid">Rule Category </label>
									</div>
								</div> -->
									<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
								<!-- <select class="form-control" name="domainfunction"
									id="selectdomain_id">
									<option value="sel">choose Domain</option>
									<option value="Banking">Banking</option>
									<option value="Telecom">Telecom</option>
									<option value="Finance">Finance</option>
									<option value="Medical">Medical</option>
									<option value="Advertisement">Advertisement</option>
									<option value="Others">Others</option>
								</select> <label for="blendfunid">Domain</label><br> -->
								
								<select class="form-control" id="selectdomain_id" name="domainfunction">
										    <option value="">Select Domain</option>
											<c:forEach var="getDomainListobj" items="${listdomain}">
												 
												 <option value="${getDomainListobj.domainName}">${getDomainListobj.domainName}</option>
					
											</c:forEach>
										</select>
										<label for="blendfunid">Domain</label><br>
								</div>
								</div>
								
							</div>
							</div>
								<!--  need to remove the hide later-->
								<div class="col-md-6 col-capture hidden" id="matchTypedividhide">
									<div class="form-group form-md-line-input">
										<select class="form-control " name="matchType" id="matchType">
											<!-- 	<option value="-1">Choose Match Type</option>
											<option value="Exact">Exact</option> -->
											<option value="Pattern">Lookup</option>
										</select> <label for="matchType">Match Type </label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
								</div>
							</div>
							<br />
							<div class="row">
							<%-- 	<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="datasourceid"
											name="datasource" placeholder="">
											<option value="-1">Select Source Template</option>
											<c:forEach var="getlistdatasourcesnameobj"
												items="${getlistdatasourcesname}">
												<option value="${getlistdatasourcesnameobj.idData}">${getlistdatasourcesnameobj.name}</option>
											</c:forEach>
										</select> <label for="columnid">Data Template for Modification</label><br>
									</div>
								</div> --%>
							<%-- 	<div class="col-md-6 col-capture hidden" id="rightdatasourceid">
									<div class="form-group form-md-line-input">
										<select class="form-control" id="rightsourceid"
											name="rightsource" placeholder="">
											<option value="-1">Select Second Source Template</option>
											<c:forEach var="getlistdatasourcesnameobj"
												items="${getlistdatasourcesname}">
												<option value="${getlistdatasourcesnameobj.idData}">${getlistdatasourcesnameobj.name}</option>
											</c:forEach>
										</select> <label for="form_control_1">Second Source Template *</label>
									</div>
									<br /> <span class="required"></span>
								</div> --%>
							</div>
						<!-- 	<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control blendops secondops"
											id="leftitemid" name="leftitem" placeholder="">
										</select> <label for="form_control_1">Select Data Column *</label>
									</div>
								</div>
								<div class="col-md-6 col-capture hidden" id="rightdatasourceid2">
									<div class="form-group form-md-line-input">
										<select class="form-control secondops" id="rightitemid"
											name="rightitem" placeholder="">
										</select> <label for="form_control_1">Select Data Column *</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div> -->
							<div class="row">
							<!-- 	<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control blendops" name="filterfunction">
											<option value="-1">Choose Filter Function</option>
											<option value="CONCAT(">CONCAT</option>
											<option value="SUBSTR(">SUBSTR</option>
											<option value="ROUND(">ROUND</option>
											<option value="MIN(">MIN</option>
											<option value="MAX(">MAX</option>
											<option value="AVG(">AVG</option>
										</select> <label for="blendfunid">Choose Filter Function</label><br>
									</div>
								</div>
 -->

							<%-- 	<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<select class="form-control blendops" name="blendoperator">
											<option value="-1">Choose Operator</option>
											<option value="+">+</option>
											<option value="-">-</option>
											<option value="*">*</option>
											<option value="/">/</option>
											<option value=">">></option>
											<option value="<"><</option>
											<option value=">=">>=</option>
											<option value="<="><=</option>
											<option value="!=">!=</option>
											<option value="==">==</option>
											<option value="&&">&&</option>
											<option value="||">||</option>
										</select> <label for="blendoperid">Choose Operator</label><br>
									</div>
								</div> --%>
							</div>
							<div class="row">
								<div class="col-md-12 col-capture" id="ruleexprdivid">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="formulaid" name="ruleexpr"> <label
											for="form_control_1">Rule Expression *</label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>

							

							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-checkboxes">
										<div class="md-checkbox-list">
											<div class="md-checkbox">
												<input type="checkbox" class="md-check"
													id="configurerulesid" name="configurerule" value="Y">
												<label for="configurerulesid"><span></span> <span
													class="check"></span> <span class="box"></span>configure
													synonyms </label>
											</div>
										</div>
									</div>
								</div>




								<div class="hidden" id="incrementaltype">
									<table id="myTable" style="width: 100%">
										<tr>

											<!--    <th>Domain</th> -->
											<th>FieldName</th>
											<th>synonyms</th>

										</tr>
										<tr>
											<!-- 										 <td><input  class="form-control" type="text" id="rule_id"></td> 
 -->
											<!-- <td  id="x" class = "select" > 
        									<select class="form-control" id="select_id">   
        									     <option value="sel">select</option>
               					 				<option value="Banking">Banking</option>
												<option value="Telecom">Telecom</option>
												<option value="Finance">Finance</option>
												<option value="Medical">Medical</option>
												<option value="Advertisement">Advertisement</option>
												<option value="Others">Others</option>
        									</select>
        									
       										 </td>    -->

											<td><input class="form-control" type="text"
												id="fieldname"></td>
											<td><input class="form-control" type="text"
												id="synonyms"></td>
											<td><input class="form-control" type="button"
												id="addrow" class="button" value="Add Row"
												onclick="addField();"></td>

										</tr>

									</table>




								</div>

							</div>
								<div class="form-actions noborder align-center">
						<button type="submit" id="addnewruleid" class="btn blue">Submit</button>
					</div>
						
						<div class="col-md-6 col-capture"></div>
					</div>




					<div class="row hidden" id="matchexprdivid">
						<div class="col-md-12 col-capture">
							<div class="form-group form-md-line-input">
								<input type="text" class="form-control catch-error"
									id="matchexprid" name="matchexpr"
									placeholder="dataSource1.columnName=dataSource2.columnName">
								<label for="form_control_1">Matching Expression *</label>
							</div>
							<br /> <span class="required"></span>
						</div>
					</div>
				
				</div>
			</div>
			<div class="note note-info hidden">Blend created Successfully</div>
			<!-- END SAMPLE FORM PORTLET-->
		</div>
	</div>
</div>
</div>
<!-- END QUICK SIDEBAR -->
</div>
<!-- END CONTAINER -->
<jsp:include page="footer.jsp" />

<div class="cd-popup" id="popSample" role="alert">
	    <div class="cd-popup-container">
		   	<table class="table" id="makeEditable">
			    <thead>
			      <tr>
			        <th>Field Name</th>
			        <th>Synonym</th>
			       <!--  <th><button id="but_add" >Add New Row</button></span></th> -->
			        <th><input class="btn btn-primary btn-md" type="button"
												id="addrow" class="btn btn-primary btn-md" value="Add New Row"
												onclick="addField();"></th>
												
			      </tr>
			    </thead>
			    <tbody>
			      <tr>
			        <td  contenteditable="true"> age</td>
			        <td contenteditable="true">tage,sage</td>
			      
			      </tr>      
			      <tr>
			       <td contenteditable="true">amount</td>
			        <td contenteditable="true">money,amount,sal</td>
			        
			      </tr>     
			      <tr>
   
  </tr>
  
   
			    </tbody>
			  
			    
		  	</table>
		  	<div>
		    <input class="btn btn-primary btn-md" type="button"
												id="save" class="button" value="Save"/>
			<input class="btn btn-primary btn-md" type="button"
												id="closesynpop" class="button" value="Close"/>
		  </div>
												
			
		  	 
		  
		  	                   	    
	</div>
	   
	</div>
	<jsp:include page="footer.jsp" />
<script>
	$("#datasourceid").change(
			function() {

				var no_error = 1;
				$('.input_error').remove();

				if ($("#datasourceid").val() == '-1') {
					// $('<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>').insertAfter($('#rightsourceid'));
					no_error = 0;
				}

				if (no_error) {
					var rightSourceId = $("#datasourceid :selected").text();

					console.log(rightSourceId);
					var form_data = {
						idData : $("#datasourceid").val(),
					// times: new Date().getTime()
					};

					$.ajax({
						url : './changeDataColumnAjax',
						type : 'POST',
						datatype : 'json',
						headers: { 'token':$("#token").val()},
						data : form_data,
						success : function(message) {
							var j_obj = $.parseJSON(message);
							if (j_obj.hasOwnProperty('success')) {
								var temp = {};
								column_array = JSON.parse(j_obj.success);
								// empty the list from previous selection
								$("#leftitemid").empty();
								$("<option />").attr("value", '-1').html(
										'Select Column')
										.appendTo("#leftitemid");
								temp[this.value] = this.subitems;

								$.each(column_array, function(k1, v1) {
									console.log(v1);
									console.log(k1);
									var column_details = column_array[k1];
									console.log(column_details);
									var sourcename = rightSourceId;
									temp_name = column_details;
									//sourcename = sourcename.concat('.', temp_name);
									sourcename = temp_name;
									$("<option />").attr("value", sourcename)
											.html(temp_name).appendTo(
													"#leftitemid");
									temp[this.value] = this.subitems;
								});
							} else if (j_obj.hasOwnProperty('fail')) {
								console.log(j_obj.fail);
								toastr.info(j_obj.fail);
							}
						},
						error : function(xhr, textStatus, errorThrown) {
							$('#initial').hide();
							$('#fail').show();
						}
					});
				}
			});

	$('#addnewruleid')
			.click(
					function() {
						var no_error = 1;
						$('.input_error').remove();
						
						if(!checkInputText()){ 
							no_error = 0;
							alert('Vulnerability in submitted data, not allowed to submit!');
						}

						if ($("#rulenameid").val().length == 0) {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please enter Extended Template Rule Name</span>')
									.insertAfter($('#rulenameid'));
							no_error = 0;
						}
						if ($("#ruleCategoryid").val() == -1) {
							$(
									'<span class="input_error" style="font-size:12px;color:red">Please Choose Rule Category</span>')
									.insertAfter($('#ruleCategoryid'));
							no_error = 0;
						}
						if (($('#ruleCategoryid').val() == 'Referential')
								|| ($('#ruleCategoryid').val() == 'Cross Referential')) {
							if ($("#formulaid").val().length == 0) {
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please enter Rule Expression</span>')
										.insertAfter($('#formulaid'));
								no_error = 0;
							}
						}
						if (($('#ruleCategoryid').val() == 'Orphan')
								|| ($('#ruleCategoryid').val() == 'Cross Referential')) {
							if ($("#matchexprid").val().length == 0) {
								$(
										'<span class="input_error" style="font-size:12px;color:red">Please enter Matching Expression</span>')
										.insertAfter($('#matchexprid'));
								no_error = 0;
							}
						}
						// if($("#formulaid").val().length == 0){
						//     $('<span class="input_error" style="font-size:12px;color:red">Please enter a formula to be saved</span>').insertAfter($('#formulaid'));
						//     no_error = 0;
						// }
						var targeturl = $("#targeturl").val();
						
						if (no_error) {
							
							
							//datasourceid 
							var form_data = {
									
								//rule_id:$("#rule_id").val(),
								domain : $("#selectdomain_id").val(),
								//fieldname : $("#fieldname").val(),
								//synonyms : $("#synonyms").val(),
								name : $("#rulenameid").val(),
								description : $("#descid").val(),
								//ruleCategory : $("#ruleCategoryid").val(),
								//dataSource1 : $("#datasourceid").val(),
								//dataSource2 : $("#rightsourceid").val(),
								ruleExpression : $("#formulaid").val()
								//matchingExpression : $("#matchexprid").val(),
								/* externalDatasetName : $("#rightsourceid").find(
										"option:selected").text(), */
								//matchType : $("#matchType").val(),
								//regularExprColumnName : $("#leftitemid").find(
										//"option:selected").text()
							//times: new Date().getTime()
							};
							console.log(form_data);

							
							
							$.ajax({
										
										url : './createExtendTemplateRule',
										type : 'POST',
										headers: { 'token':$("#token").val()},
										datatype : 'json',
										data : form_data,
										
										
										
										success : function(message) {
											console.log(message);
											var j_obj = $.parseJSON(message);
											if (j_obj.hasOwnProperty('success')) {
												//alert(message);
												toastr
														.info("Extended Template Rule created successfully");
												setTimeout(
														function() {
															window.location.href = 'viewRules';
															//window.location.reload();

														}, 1000);
											} else if (j_obj
													.hasOwnProperty('fail')) {
												toastr
														.info("This Rule Name or Expression already Exists . Please Change ");
												// console.log(j_obj.fail);
												//satoastr.info(j_obj.fail);
											}
										},
										error : function(xhr, textStatus,
												errorThrown) {
											
											alert("In error");
											
											$('#initial').hide();
											$('#fail').show();
										}
									});
						}
						return false;
					});

	

	/*  var form_data2 = {
			column : $("#column").val(),
			synonyms : $("#synonyms").val(),
			name : $("#rulenameid").val(),
			description : $("#descid").val(),
			ruleCategory : $("#ruleCategoryid").val(),
			dataSource1 : $("#datasourceid").val(),
			dataSource2 : $("#rightsourceid").val(),
			ruleExpression : $("#formulaid").val(),
			matchingExpression : $("#matchexprid").val(),
			externalDatasetName:$("#rightsourceid").find( "option:selected" ).text(), 
			matchType : $("#matchType").val(),
			regularExprColumnName:$("#leftitemid").find( "option:selected" ).text(),
			 
		};
		console.log(form_data2);
	 */
	/* $.ajax({
	url : './createExtendTemplateRule2',
	type : 'POST',
	datatype : 'json',
	data : form_data2,
	 success: function(message){
	   	    console.log(message);
	        var j_obj = $.parseJSON(message);
	             if(j_obj.hasOwnProperty('success'))
	             {
	           	  //alert(message);
	                toastr.info("Extended Template Rule created successfully");
	                   setTimeout(function(){
	                   window.location.href='viewRules';
	                   //window.location.reload();

	               },1000); 
	             }else if(j_obj.hasOwnProperty('fail') )
	             {
	           	  toastr.info("This Rule Name exists for the Data Source. Please Change Rule Name");
	                // console.log(j_obj.fail);
	                 //satoastr.info(j_obj.fail);
	             }
	},
	error : function(xhr, textStatus,
			errorThrown) {
		$('#initial').hide();
		$('#fail').show();
	}
	});
	 */

	$('#configurerulesid').click(function() {
		
		
					var no_error = 1;
					$('.input_error').remove();

					if ($("#formulaid").val().length == 0) {
						
						$(
								'<span class="input_error" style="font-size:12px;color:red">Please enter Rule Expression</span>')
								.insertAfter($('#formulaid'));
					
						
						no_error = 0;
						
					}
					else
						 $('#popSample').addClass('is-visible');
					
		
	});
	 
	 $('#popSample').on('click', function(event){
			if( $(event.target).is('#save')) {
					
			
				event.preventDefault();
				$(this).removeClass('is-visible');
			}
		});

	 /* $('#makeEditable').SetEditable({ $addButton: $('#but_add')}); */
	/*  $('#makeEditable').dataTable().makeEditable(); */
	function addField(argument) {
		/* var myTable = document.getElementById("makeEditable");
		var currentIndex = myTable.rows.length;
		var currentRow = myTable.insertRow(-1);

		// var rule_idBox = document.createElement("input");
		// rule_idBox.setAttribute("id", "rule_id" + currentIndex);
		// rule_idBox.setAttribute("class", "form-control");

		/* var select_idBox = document.createElement("input");
		select_idBox.setAttribute("id", "x" + currentIndex);
		select_idBox.setAttribute("class", "form-control");    */

		/* var columnBox = document.createElement("input");
		columnBox.setAttribute("id", "fieldname" + currentIndex);
		columnBox.setAttribute("class", "form-control");

		var synonymsBox = document.createElement("input");
		synonymsBox.setAttribute("id", "synonyms" + currentIndex);
		synonymsBox.setAttribute("class", "form-control"); */
		
		

		/* var addRowBox = document.createElement("input");
		addRowBox.setAttribute("type", "button");
		addRowBox.setAttribute("value", "Add Row");
		addRowBox.setAttribute("onclick", "addField();");
		addRowBox.setAttribute("class", "form-control");
 */
		// var currentCell = currentRow.insertCell(-1);
		//currentCell.appendChild(rule_idBox);

		/*  currentCell = currentRow.insertCell(-1);
		 currentCell.appendChild(select_idBox); */

		 
		 
		 
		/* currentCell = currentRow.insertCell(-1);
		currentCell.appendChild(columnBox);

		currentCell = currentRow.insertCell(-1);
		currentCell.appendChild(synonymsBox);

		currentCell = currentRow.insertCell(-1);
		currentCell.appendChild(addRowBox); */
		
		

	


		
			//Try to get tbody first with jquery children. works faster!
			var tbody = $('#makeEditable').children('tbody');

			//Then if no tbody just select your table 
			var table = tbody.length ? tbody : $('#makeEditable');


			
			    table.append('<tr><td contenteditable="true">field name</td><td contenteditable="true">synonym</td></tr>');
		
	}
	
	$("#closesynpop").click(
			function() {
				 $('#popSample').removeClass('is-visible');
				
			}
	)
	
	$("#save").click(
			function() {
				
			
			       
			        var myTab = document.getElementById('makeEditable');
			        var str="";
			        // LOOP THROUGH EACH ROW OF THE TABLE AFTER HEADER.
			        for (i = 1; i < myTab.rows.length; i++) {

			            // GET THE CELLS COLLECTION OF THE CURRENT ROW.
			            var objCells = myTab.rows.item(i).cells;

			            // LOOP THROUGH EACH CELL OF THE CURENT ROW TO READ CELL VALUES.
			            for (var j = 0; j < objCells.length; j++) {
			              
			                 str = str + '||' + objCells.item(j).innerHTML;
			                
			                
			                
			            }
			          
			        }
			       alert(str);
			        var json_str=JSON.stringify(str);
			        var data_1 = {
		        			singleRowData : json_str,
		        			domain : $("#selectdomain_id").val(),
							name : $("#rulenameid").val()
					};
			      
			            
			    
			        $.ajax({

			        
			        	type: "POST",
			        	url: "userrule",
			        	headers: { 'token':$("#token").val()},
			        	data: data_1,
			        	datatype : 'json',
			        
			        	

			        	success: function (message) {			        					        							
							
			        	/* alert(message); */

			        	},
			        	error: function (message) {

			        	/* alert("error"+message);
 */
			        	}
			});
			});
	
</script>
