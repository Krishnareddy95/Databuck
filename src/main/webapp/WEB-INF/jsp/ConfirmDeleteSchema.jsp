
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp"/>
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp"/>


		
		<!--============= BEGIN CONTENT BODY============= -->


                    <!--*****************      BEGIN CONTENT **********************-->
		
		<div class="page-content-wrapper">
			<!-- BEGIN CONTENT BODY -->
			<div class="page-content">
				<!-- BEGIN PAGE TITLE-->
				<!-- END PAGE TITLE-->
				<!-- END PAGE HEADER-->
				<div class="row">
					<div class="col-md-12">
					
                  <!--****         BEGIN EXAMPLE TABLE PORTLET       **********-->
						
						<div class="portlet light bordered">
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold ">Are you sure you want to delete ${name}  ?</span>
								</div>
							</div>
							<div class="portlet-body">
								<!-- <table class="table table-striped table-bordered table-hover"
									id="showDataTable"> -->
									<span class="required"></span> <br /> <br />
								<!--   <button type="submit" formaction="deleteApplication?selected=yes" class="btn blue dropdowns">Yes</button>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
								  <button type="submit" formaction="deleteApplication?selected=cancel" id="deleteApplicationcancel" value="customize" class="btn blue dropdowns">Cancel</button> -->
								  <a onClick="validateHrefVulnerability(this)"  href="deleteApplicationInView?selected=yes&idApp=${idApp}"><button class="btn blue dropdowns">Yes</button></a>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
								   <a onClick="validateHrefVulnerability(this)"  href="deleteApplicationInView?selected=cancel&idApp=${idApp}&listOfListApplicationsObjs=${listOfListApplicationsObjs}"><button class="btn blue dropdowns">Cancel</button></a>
								<!-- </table> -->
							</div>
						</div>
		<!--       *************END EXAMPLE TABLE PORTLET*************************-->
	
					</div>
				</div>
			</div>
		</div>
	

	
	                  <!--********     BEGIN CONTENT ********************-->

<jsp:include page="footer.jsp"/>