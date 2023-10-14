
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />
<script>
	var request;
	var vals = "";
	function sendInfo() {
		var v = document.getElementById("rulenameid").value;
		//var v=document.vinform.Database.value;  
		var url = "./duplicatedatatemplatename?val=" + v;
		//alert("in ajax code");
		if (window.XMLHttpRequest) {
			request = new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			request = new ActiveXObject("Microsoft.XMLHTTP");
		}

		try {
			request.onreadystatechange = getInfo;
			request.open("POST", url, true);
			request.setRequestHeader('token',$("#token").val());
			request.send();
		} catch (e) {
			alert("Unable to connect to server");
		}
	}

	function getInfo() {
		if (request.readyState == 4) {
			var val = request.responseText;
			//alert("getInfo");
			//alert(val);
			//var sal="Database already exists";
			//console.log(val);
			document.getElementById('amit').innerHTML = val;
			if (val != "") {
				vals = val;
				//alert("please enter another name");
				return false;
			}
			//alert("check123");
		}
	}
</script>
<jsp:include page="container.jsp" />
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
							<span class="caption-subject bold "> Edit Global Rule </span>
						</div>
					</div>
					<div class="portlet-body form">

						<div class="form-body">
							<div class="row">

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="rulenameid" value="${listDataSource.ruleName}"
											readonly="readonly"> <label for="rulenameid">Global
											Rule Name </label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>

								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="descid" value="${listDataSource.description}"
											readonly="readonly"> <label for="descid">Description</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>







							<div class="row">
								<div class="col-md-6 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="descid" value="${listDataSource.domain}"
											readonly="readonly"> <label for="descid">Domain</label>
										<!--  <span class="help-block">Data Set Name </span> -->
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>



							<div class="row" id="ruleexprdivid">
								<div class="col-md-12 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="formulaid" name="ruleexpr"
											value="${listDataSource.expression}" readonly="readonly">
										<label for="form_control_1">Rule Expression </label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>

							<div class="row" id="ruleexprdivid">
								<div class="col-md-12 col-capture">
									<div class="form-group form-md-line-input">
										<input type="text" class="form-control catch-error"
											id="formulaid" name="ruleexpr"
											value="${listDataSource.expression}" readonly="readonly">
										<label for="form_control_1">Rule Expression </label>
									</div>
									<br /> <span class="required"></span>
								</div>
							</div>




							<div class="form-actions noborder align-center">
								<button type="submit" id="addnewruleid" class="btn blue">Submit</button>
							</div>
						</div>
					</div>
					<div class="note note-info hidden">Blend created Successfully
					</div>
					<!-- END SAMPLE FORM PORTLET-->
				</div>
			</div>
		</div>
	</div>
	<!-- END QUICK SIDEBAR -->
</div>
<!-- END CONTAINER -->
<input type="hidden" class="form-control catch-error"
	id="idListColrules" name="idListColrules"
	value="${listDataSource.idListColrules}">
<jsp:include page="footer.jsp" />
<script>

/* $(document).ready(function() {
	if ( $('#ruleCategoryid').val() == 'orphanreferential' || ($('#ruleCategoryid').val() =='referential' && 
		$('#ruleExternal').val() == 'Y'))	
	{
		 $('#rightdatasourceid').removeClass('hidden').show();
		 $('#rightdatasourceid2').removeClass('hidden').show();	
		 $('#matchexprdivid').removeClass('hidden').show();	
	}	
	
	if ( $('#ruleCategoryid').val() == 'referential' || 
			 $('#ruleCategoryid').val() =='Regular Expression')
	{ 
		$('#ruleexprdivid').removeClass('hidden').show();	
	}
}); */

/* $("#datasourceid").ready(function(){

    var no_error = 1;
    $('.input_error').remove();

    if($("#datasourceid").val() == '-1'){
       // $('<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>').insertAfter($('#rightsourceid'));
        no_error = 1;
    } 

    if(no_error)
    {   var rightSourceId = $("#datasourceid :selected").text();
    
    console.log(rightSourceId);
    	var form_data = {
    		idData : $("#datasourceid").val(),
           // times: new Date().getTime()
        };
        
        $.ajax({
          url: './changeDataColumnAjax',
          type: 'POST',
          headers: { 'token':$("#token").val()},
          datatype:'json',
          data: form_data,
          success: function(message){
              var j_obj = $.parseJSON(message);
                  if(j_obj.hasOwnProperty('success'))
                  {
                    var temp = {};
                    column_array = JSON.parse( j_obj.success);
                    // empty the list from previous selection
                    $("#leftitemid").empty();
                    $("<option />")
                           .attr("value", '-1')
                         .html('Select Column')
                         .appendTo("#leftitemid");
                           temp[this.value] = this.subitems;
                           
                           
                     
                    
                    $.each(column_array, function(k1, v1) 
                    {
                    	console.log(v1);
                    	console.log(k1);
                        var column_details = column_array[k1];
                        console.log(column_details);
                        var sourcename = rightSourceId;
                        temp_name = column_details;
                        //sourcename = sourcename.concat('.', temp_name);
                        sourcename = temp_name;
                        $("<option />")
                            .attr("value", sourcename)
                            .html(temp_name)
                            .appendTo("#leftitemid");
                            temp[this.value] = this.subitems;
                    });
					
					
                  }else if(j_obj.hasOwnProperty('fail') )
                  {
                      console.log(j_obj.fail);
                      toastr.info(j_obj.fail);
                  }
          },
          error: function(xhr, textStatus, errorThrown){
                $('#initial').hide();
                $('#fail').show();
          }
       });
    }    
});

$("#rightsourceid").ready(function(){

    var no_error = 1;
    $('.input_error').remove();

    if($("#rightsourceid").val() == '-1'){
       // $('<span class="input_error" style="font-size:12px;color:red">Please select a source to Continue</span>').insertAfter($('#rightsourceid'));
        no_error = 1;
    } 

    if(no_error)
    {   var rightSourceId = $("#rightsourceid :selected").text();
    
    console.log(rightSourceId);
    	var form_data = {
    		idData : $("#rightsourceid").val(),
           // times: new Date().getTime()
        };
        
        $.ajax({
          url: './changeDataColumnAjax',
          type: 'POST',
          headers: { 'token':$("#token").val()},
          datatype:'json',
          data: form_data,
          success: function(message){
              var j_obj = $.parseJSON(message);
                  if(j_obj.hasOwnProperty('success'))
                  {
                                              
                           
                     //Sumeet_30_08_2018
                     var temp1 = {};
                    column_array1 = JSON.parse( j_obj.success);
                    // empty the list from previous selection
                    $("#rightitemid").empty();
                    $("<option />")
                           .attr("value", '-1')
                         .html('Select Column')
                         .appendTo("#rightitemid");
                           temp1[this.value] = this.subitems;
					//
                    					
                    $.each(column_array1, function(k1, v1) 
                            {
                            	console.log(v1);
                            	console.log(k1);
                                var column_details1 = column_array1[k1];
                                console.log(column_details1);
                                var sourcename = rightSourceId;
                                temp_name = column_details1;
                                //sourcename = sourcename.concat('.', temp_name);
                                sourcename = temp_name;
                                $("<option />")
                                    .attr("value", sourcename)
                                    .html(temp_name)
                                    .appendTo("#rightitemid");
                                    temp1[this.value] = this.subitems;
                            });
                  }else if(j_obj.hasOwnProperty('fail') )
                  {
                      console.log(j_obj.fail);
                      toastr.info(j_obj.fail);
                  }
          },
          error: function(xhr, textStatus, errorThrown){
                $('#initial').hide();
                $('#fail').show();
          }
       });
    }    
}); */



	$('#addnewruleid').click(function() {
						var no_error = 1;
						$('.input_error').remove();

						 /* if ($("#rulenameid").val().length == 0) {
							$('<span class="input_error" style="font-size:12px;color:red">Please enter Extended Template Rule Name</span>')
									.insertAfter($('#rulenameid'));
							no_error = 0;
						} 
						 if ($("#ruleCategoryid").val() == -1) {
								$('<span class="input_error" style="font-size:12px;color:red">Please Choose Rule Category</span>')
										.insertAfter($('#ruleCategoryid'));
								no_error = 0;
							} 
						 if( ( $('#ruleCategoryid').val() =='Referential' ) || ( $('#ruleCategoryid').val() =='Cross Referential' )||( $('#ruleCategoryid').val() =='referential' )  ){
							 if ($("#formulaid").val().length == 0) {
									$('<span class="input_error" style="font-size:12px;color:red">Please enter Rule Expression</span>')
											.insertAfter($('#formulaid'));
									no_error = 0;
								} 
						 }
						 if( ( $('#ruleCategoryid').val() =='Orphan' ) || ( $('#ruleCategoryid').val() =='Cross Referential' ) || ( $('#ruleCategoryid').val() =='orphanreferential' )  ){
							 if ($("#matchexprid").val().length == 0) {
									$('<span class="input_error" style="font-size:12px;color:red">Please enter Matching Expression</span>')
											.insertAfter($('#matchexprid'));
									no_error = 0;
								} 
						 } */
						// if($("#formulaid").val().length == 0){
						//     $('<span class="input_error" style="font-size:12px;color:red">Please enter a formula to be saved</span>').insertAfter($('#formulaid'));
						//     no_error = 0;
						// }
						var targeturl = $("#targeturl").val();
						if (no_error) {
							var form_data = {
								name : $("#rulenameid").val(),
								idListColrules :$("#idListColrules").val(),
								description : $("#descid").val(),
								domain:$("#selectdomain_id").val(),
								/* ruleCategory : $("#ruleCategoryid").val(),
								dataSource1 : $("#datasourceid").val(),
								dataSource2 : $("#rightsourceid").val(), */
								ruleExpression : $("#formulaid").val()
								/* matchingExpression : $("#matchexprid").val(),
								externalDatasetName:$("#rightsourceid").find( "option:selected" ).text(), 
								matchType : $("#matchType").val(),
								regularExprColumnName:$("#leftitemid").find( "option:selected" ).text(), */
							//times: new Date().getTime()
							};
							console.log(form_data);
							alert(form_data.domain);

							$.ajax({
								 
								
										url : './updateGlobalRule',
										type : 'POST',
										headers: { 'token':$("#token").val()},
										datatype : 'json',
										data : form_data,
										
									
										 success: function(message){
								        	    console.log(message);
								             var j_obj = $.parseJSON(message);
								                  if(j_obj.hasOwnProperty('success'))
								                  {
								                	  alert(message);
								                     toastr.info("Global Rule updated successfully");
								                        setTimeout(function(){
								                        window.location.href='viewGlobalRules';
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
						}
						return false;
					});
</script>