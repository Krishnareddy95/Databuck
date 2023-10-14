<!--============== -***************BEGIN CONTAINER-***************========== -->


<!-- BEGIN CONTAINER -->
<%@page import="com.databuck.service.RBACController"%>
<%@page import="java.util.Map"%>
<jsp:include page="checkVulnerability.jsp" />
<!-- <link rel="stylesheet"  href="./assets/css/custom-style.css" type="text/css"  /> -->
<style>
.sub-menu img{ width:4px !important;
margin-right:11px;}
</style>

<div class="page-container">
	<!-- BEGIN SIDEBAR -->
	<div class="page-sidebar-wrapper">
		<!-- BEGIN SIDEBAR -->
		<!-- DOC: Set data-auto-scroll="false" to disable the sidebar from auto scrolling/focusing -->
		<!-- DOC: Change data-auto-speed="200" to adjust the sub menu slide up/down speed -->
		<div class="page-sidebar navbar-collapse collapse">

			<!-- BEGIN SIDEBAR MENU -->
			<!-- DOC: Apply "page-sidebar-menu-light" class right after "page-sidebar-menu" to enable light sidebar menu style(without borders) -->
			<!-- DOC: Apply "page-sidebar-menu-hover-submenu" class right after "page-sidebar-menu" to enable hoverable(hover vs accordion) sub menu mode -->
			<!-- DOC: Apply "page-sidebar-menu-closed" class right after "page-sidebar-menu" to collapse("page-sidebar-closed" class must be applied to the body element) the sidebar sub menu mode -->
			<!-- DOC: Set data-auto-scroll="false" to disable the sidebar from auto scrolling/focusing -->
			<!-- DOC: Set data-keep-expand="true" to keep the submenues expanded -->
			<!-- DOC: Set data-auto-speed="200" to adjust the sub menu slide up/down speed -->
			<ul class="page-sidebar-menu  page-header-fixed " data-keep-expanded="false" data-auto-scroll="true"
				data-slide-speed="200">
				<!-- DOC: To remove the sidebar toggler from the sidebar you just need to completely remove the below "sidebar-toggler-wrapper" LI element -->
				<li class="sidebar-toggler-wrapper hide">
					<!-- BEGIN SIDEBAR TOGGLER BUTTON -->
					<div class="sidebar-toggler"></div> <!-- END SIDEBAR TOGGLER BUTTON -->
				</li>
				<!-- DOC: To remove the search box from the sidebar you just need to completely remove the below "sidebar-search-wrapper" LI element -->

				<%
					Map<String, String> module = (Map<String, String>) session.getAttribute("module");
				//System.out.println("module=" + module);
				for (Map.Entry m : module.entrySet()) {
					//System.out.println("container idTask=" + m.getKey() + "		accessControl=" + m.getValue());
				}
				//System.out.println("currentsection=" + request.getParameter("Data Connection"));
				%>


				<%
					boolean flag = false;
				Object currentsectionObject = request.getAttribute("currentSection");
				//System.out.println("currentSection at obj " + currentsectionObject);
				String currentSection = null;

				if (currentsectionObject != null) {
					currentSection = (String) currentsectionObject;
				}
				//System.out.println("currentLink at obj " + request.getAttribute("currentLink"));
				String currentLink = (String) request.getAttribute("currentLink");
				if (module.containsKey("QuickStart")) {
					%>

					<%
						if (currentSection != null && currentSection.equals("QuickStart")) {
					%>
					<li class="nav-item active open">
						<%
							} else {
						%>

					<li class="nav-item">
						<%
							}
						%> <a onClick="validateHrefVulnerability(this)"  href="getQuickStartHome" class="nav-link nav-toggle"> 
													<img src="./assets/img/svg/rocket.svg" alt="Quickstart" style="width: 20px;">
													 <span
								class="title">Quick Start</span> <span class="arrow"></span>
						</a>
						<ul class="sub-menu">
							<%
								flag = RBACController.rbac("QuickStart", "C", session);
							
							if (flag) {
							%>
							<%
								if (currentLink.equals("quickStartHome")) {
							%>
							<li class="nav-item active">
								<%
									} else {
								%>

							<li class="nav-item">
								<%
									}
								%> <a onClick="validateHrefVulnerability(this)"  href="getQuickStartHome" class="nav-link"> <img src="./assets/img/svg/sub-menu.svg" alt="Local Quick Start" style="width: 7px;">
								<span class="title">Local Quick Start </span>
								</a>
							</li>
							<%
								}
							%>
						
							<%
								if (currentLink.equals("gcpQuickStart")) {
							%>
							<li class="nav-item active">
								<%
									} else {
								%>

							<li class="nav-item">
								<%
									}
								%> <a onClick="validateHrefVulnerability(this)"  href="getGCPQuickStartHome" class="nav-link"> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;">
								<span class="title">GCP Quick Start</span>
								</a>
							</li>
							
							<%
								if (currentLink.equals("awsQuickStart")) {
							%>
							<li class="nav-item active">
								<%
									} else {
								%>

							<li class="nav-item">
								<%
									}
								%> <a onClick="validateHrefVulnerability(this)"  href="getAWSQuickStartHome" class="nav-link"> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;">
								<span class="title">AWS Quick Start</span>
								</a>
							</li>
							
						</ul>
					</li>
					<%
						}
					%>
					
			   <%	
				if (module.containsKey("Alert Inbox")) {
					%>
					<% 
				if (currentSection != null && currentSection.equals("Alert Inbox")) {
				%>
				<li class="nav-item active open">
					<%
						} else {
					%>

				<li class="nav-item">
					<%
						}
					%> <a onClick="validateHrefVulnerability(this)"  href="alertManagementView" class="nav-link nav-toggle"> <img src="./assets/img/icons8-inbox-64.png" alt="Data Connection" style="width: 23px; "> <span
							class="title">Alert Inbox</span> <span class="arrow"></span>
					</a>
					</li>
					</li>
					<%
					}
				%>
					
				<%	
				if (module.containsKey("Data Connection")) {
				%>

				<%
					if (currentSection != null && currentSection.equals("Data Connection")) {
				%>
				<li class="nav-item active open">
					<%
						} else {
					%>

				<li class="nav-item">
					<%
						}
					%> <a onClick="validateHrefVulnerability(this)"  href="dataConnectionView" class="nav-link nav-toggle"> <img src="./assets/img/svg/1849542.svg" alt="Data Connection" style="width: 16px; "> <span
							class="title">Data
							Connection</span> <span class="arrow"></span>
					</a>
					<ul class="sub-menu">
						<%
							flag = RBACController.rbac("Data Connection", "R", session);
						
						if (flag) {
						%>
						<%
							if (currentLink.equals("DCView")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="dataConnectionView" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;">
							 <span class="title">View </span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Data Connection", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("DCAdd New")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="createConnection" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;">
							 <span class="title">Add New</span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Data Connection", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("DCBatchAdd New")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="addNewBatch" class="nav-link"> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Add New Batch</span>
							</a>
						</li>
						<%
							}
						%>
					</ul>
				</li>
				<%
					}
				%>

				<%
					if (module.containsKey("Data Template")) {
					if (currentSection != null && currentSection.equals("Data Template")) {
				%>
				<li class="nav-item active open">
					<%
						} else {
					%>

				<li class="nav-item">
					<%
						}
					%> <a onClick="validateHrefVulnerability(this)"  href="datatemplateview" class="nav-link nav-toggle"> <img src="./assets/img/svg/template.svg" alt="Data Template" style="width: 16px;"> <span
							class="title">Data
							Template</span> <span class="arrow"></span>
					</a>
					<ul class="sub-menu">
						<%
							flag = RBACController.rbac("Data Template", "R", session);
						//System.out.println("flag="+flag);
						if (flag) {
						%>

						<%
							if (currentLink.equals("DTView")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="datatemplateview" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">View </span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Data Template", "C", session);
						//System.out.println("flag="+flag);
						if (flag) {
						%>

						<%
							if (currentLink.equals("DTAdd New")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="dataTemplateAddNew" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Add New</span>
							</a>
                                                </li>
						
						<%
							if (currentLink.equals("DerivedDT Add New")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>
						
						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="derivedTemplateAddNew" class="nav-link"> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span
								class="title">Add New Derived Template</span>
						</a>
						</li>
						<%
							}
						%>
					</ul>
				</li>
				<%
					}
				%>

				<%
					if (module.containsKey("Extend Template & Rule")) {
					if (currentSection != null && currentSection.equals("Extend Template")) {
				%>
				<li class="nav-item active open">
					<%
						} else {
					%>

				<li class="nav-item">
					<%
						}
					%> <a onClick="validateHrefVulnerability(this)"  href="extendTemplateView" class="nav-link nav-toggle"> <img src="./assets/img/svg/data.svg" alt="Extend Template & Rule" style="width: 16px;"> <span class="title">Extend
							Template
							& Rule</span> <span class="arrow"></span>
					</a>
					<ul class="sub-menu">
					<%
							if (currentLink.equals("Custom Microseg")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>
						
						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="customMicrosegments" class="nav-link"> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span
								class="title">Custom Microsegment</span>
						</a>
						</li>
						<%
							flag = RBACController.rbac("Extend Template & Rule", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>

						<%
							if (currentLink.equals("ETView")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="extendTemplateView" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">View Extend Template</span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Extend Template & Rule", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("ETAdd New")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="addNewExtendTemplate" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Add New Extend Template</span>
							</a>
						</li>
						<%
							}
						%>
						<!--  added for import business rules -->
						<%
							flag = RBACController.rbac("Extend Template & Rule", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Import Business Rules")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="importBusinessRules" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Import Business Rules</span>
							</a>
						</li>
						<%
							}
						%>
						<!-- End of Import Business Rules -->
						
						
						<%
							flag = RBACController.rbac("Extend Template & Rule", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							//if (module.containsKey("Rules")) {
						if (currentLink.equals("View Rules")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="viewRules" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">View
									Rules</span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Extend Template & Rule", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Add New Rule")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="addNewRule" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Add New Rule</span>
							</a>
						</li>
						<%
							}
						%>
						<%
							//}
						%>
						
					</ul>
				</li>
				<%
					}
				%>

				<!-- changes for Global Rule -->

				<%
					if (module.containsKey("Global Rule")) {
					if (currentSection != null && currentSection.equals("Global Rule")) {
				%>
				<li class="nav-item active open">
					<%
						} else {
					%>

				<li class="nav-item">
					<%
						}
					%> <a onClick="validateHrefVulnerability(this)"  href="viewGlobalRules" class="nav-link nav-toggle"> <img src="./assets/img/svg/global.svg" alt="Global Rule" style="width: 16px;"> <span class="title">Global
							Rules</span> <span class="arrow"></span>
					</a>
					<ul class="sub-menu">
						<%
							flag = RBACController.rbac("Global Rule", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("View Global Rules")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="viewGlobalRules" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">View </span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Global Rule", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Add New Global Rule")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="AddNewRuleGlobal" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Add New</span>
							</a>
						</li>
						<%
							}
						%>

						<%
							flag = RBACController.rbac("Global Rule", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Synonym Library")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="viewsynonyms" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Synonyms Library</span>
							</a>
						</li>
						<%
							}
						%>

						<%
                          flag = RBACController.rbac("Global Rule", "R", session);
                        //System.out.println("flag=" + flag);
                        if (flag) {
                        %>
                        <%
                            if (currentLink.equals("Global Filter")) {
                        %>
                        <li class="nav-item active">
                            <%
                                } else {
                            %>

                        <li class="nav-item">
                            <%
                                }
                            %> <a onClick="validateHrefVulnerability(this)"  href="viewGlobalFilters" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Global Filters</span>
                            </a>
                        </li>
                        <%
                            }
                        %>

						<%
							flag = RBACController.rbac("Global Rule", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("References View")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="refdatatemplateview" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">References View</span>
							</a>
						</li>
						<%
							}
						%>

						<%
							flag = RBACController.rbac("Global Rule", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Internal References")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> 
							
                             <a onClick="validateHrefVulnerability(this)"  href="addReferences" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Add Internal References</span>
							</a>  
						</li>
						<%
							}
						%>

						<%
							flag = RBACController.rbac("Global Rule", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("External References")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="dataTemplateAddNew" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Add External References</span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Global Rule", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Global Thresholds")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="globalThreshold" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Add Global Thresholds</span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Global Rule", "C", session);
						if (flag) {
						%>
						<%
							if (currentLink.equals("importExportGlobalRules")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="importExportGlobalRules" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Import/Export Global Rules</span>
							</a>
						</li>
						<%
							}
						%>
					</ul>
				</li>
				<%
					}
				%>



				<!-- end -->
				<%
					if (module.containsKey("Validation Check")) {
					if (currentSection != null && currentSection.equals("Validation Check")) {
				%>
				<li class="nav-item active open">
					<%
						} else {
					%>

				<li class="nav-item">
					<%
						}
					%> <a onClick="validateHrefVulnerability(this)"  href="validationCheck_View" class="nav-link nav-toggle">
						<img src="./assets/img/svg/checked.svg" alt="Validation Check" style="width: 16px;"> <span class="title">Validation
							Check</span> <span class="arrow"></span>
					</a>
					<ul class="sub-menu">
						<%
							flag = RBACController.rbac("Validation Check", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("VCView")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="validationCheck_View" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">View </span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Validation Check", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Add New")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="dataApplicationCreateView" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Add New</span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Validation Check", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Add Batch Validation")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="batchValidation" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Add Batch Validation</span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Validation Check", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Exception Data Report")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="ManageExceptionDataReport" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Exception Data Report</span>
							</a>
						</li>
						<%
							}
						%>
					</ul>
				</li>
				<%
					}
				%>


				<%
				if (module.containsKey("Dashboard")) {
				if (currentSection != null && currentSection.equals("Dashboard View")) {
			%>
			<li class="nav-item active open">
				<%
					} else {
				%>

			<li class="nav-item">
				<%
					}
				%> <a onClick="validateHrefVulnerability(this)"   class="nav-link nav-toggle" href="dashboardViewRules">
					<img src="./assets/img/svg/dashboard_view.svg" alt="Dashboard View" style="width: 16px;"> <span class="title">Dashboard
							View</span> <span class="arrow"></span>
				</a>
				<ul class="sub-menu">
					<%
						flag = RBACController.rbac("Dashboard", "R", session);
					//System.out.println("flag=" + flag);
					if (flag) {
					%>
					<%
						if (currentLink.equals("DashboardViews")) {
					%>
					<li class="nav-item active">
						<%
							} else {
						%>

					<li class="nav-item">
						<%
							}
						%> <a onClick="validateHrefVulnerability(this)"  href="dashboardViewRules" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">View </span>
						</a>
					</li>
					<%
						}
					%>
					<%
						flag = RBACController.rbac("Dashboard", "C", session);
					//System.out.println("flag=" + flag);
					if (flag) {
					%>
					<%
						if (currentLink.equals("Add New View")) {
					%>
					<li class="nav-item active">
						<%
							} else {
						%>

					<li class="nav-item">
						<%
							}
						%> <a onClick="validateHrefVulnerability(this)"  href="createView" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Add New</span>
						</a>
					</li>
					<%
						}
					%>
					
					
				</ul>
			</li>
			<%
				}
			%>
				

				<%
					if (module.containsKey("Tasks")) {
					if (currentSection != null && currentSection.equals("Tasks")) {
				%>
				<li class="nav-item active open">
					<%
						} else {
					%>

				<li class="nav-item">
					<%
						}
					%> <a onClick="validateHrefVulnerability(this)"  href="listApplicationsView" class="nav-link nav-toggle">
						<img src="./assets/img/svg/calendar.svg" alt="Test & Schedule" style="width: 16px;"> <span class="title">Test and
							Schedule</span> <span class="arrow"></span>
					</a>
					<ul class="sub-menu">


						<%
							flag = RBACController.rbac("Tasks", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>

						<%
							if (currentLink.equals("Run Task")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="listApplicationsView" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Run</span>
							</a>
						</li>
						<%
							}
						%>

						<%
							flag = RBACController.rbac("Tasks", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>

						<%
							if (currentLink.equals("runAppGroup")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="runAppGroup" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Run AppGroup</span>
							</a>
						</li>
						<%
							}
						%>

						<%
							flag = RBACController.rbac("Tasks", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>

						<%
							if (currentLink.equals("runSchema")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="runSchema" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Run
									Connection</span>
							</a>
						</li>
						<%
							}
						%>

						<%
							flag = RBACController.rbac("Tasks", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>

						<%
							if (currentLink.equals("viewJobStatus")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="runningJobsView" class="nav-link"> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">View JobStatus</span>
							</a>
						</li>
						<%
							}
						%>

						<%
							flag = RBACController.rbac("Tasks", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("viewSchedules")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="viewSchedules" class="nav-link"> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">View Schedules</span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Tasks", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Scheduled Task")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="scheduledTask" class="nav-link"> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Add New Schedule</span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Tasks", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("viewTriggers")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="viewTriggers" class="nav-link"> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">View Triggers</span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Tasks", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Trigger Task")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="triggerTask" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Add New Trigger</span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Tasks", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("viewAppGroups")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="viewAppGroups" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">View AppGroups</span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("Tasks", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Add AppGroup")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="addAppGroup" class="nav-link"> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Add New AppGroup</span>
							</a>
						</li>
						<%
							}
						%>
					</ul>
				</li>
				<%
					}
				%>
				<%
					if (module.containsKey("Dashboard")) {
					if (currentSection != null && currentSection.equals("DashboardConsole")) {
						
				%>

				<li class="nav-item active open">
					<%
						} else {
					%>

				<li class="nav-item">
					<%
						}
					%> <a onClick="validateHrefVulnerability(this)"  href="dashboardConsole" class="nav-link nav-toggle"> <img src="./assets/img/svg/dashboard.svg" alt="Dashboard" style="width: 16px;"> <span
							class="title">Dashboard
						</span>
					</a>
				</li>
				<%
					
				}
				%>
				<%
					if (module.containsKey("Results")) {
					if (currentSection != null && currentSection.equals("Dashboard")) {
				%>
				<li class="nav-item active open">
					<%
						} else {
					%>

				<li class="nav-item">
					<%
						}
					%> <a onClick="validateHrefVulnerability(this)"  href="dashboard_View" class="nav-link nav-toggle"> <img src="./assets/img/svg/medical-result.svg" alt="Results" style="width: 16px;"> <span
							class="title">Results</span> <span class="arrow"></span>
					</a>
					<ul class="sub-menu">
						<%
							flag = RBACController.rbac("Results", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>


						<%
							if (currentLink.equals("View")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="dashboard_View" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Quality</span>
							</a>
						</li>
						<!-- changes for Data Profiling -->

						<%
							if (currentLink.equals("Data Profiling")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="profileDataTemplateView" class="nav-link nav-toggle"> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;">
							<span class="title">Profiling</span><span class="arrow"></span>
								<!-- <a onClick="validateHrefVulnerability(this)"  href="dataProfiling_View" class="nav-link"> <span
								class="title">Profiling</span> -->
							</a>
							<ul class="sub-menu">
							     <%
									if (currentLink.equals("Table Details")) {
								%>
								<li class="nav-item active">
									<%
										} else {
									%>

								<li class="nav-item">
									<%
										}
									%> <a onClick="validateHrefVulnerability(this)"  href="profileDataTemplateView" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Table Profiling</span>
									</a>
								</li>

								<%
									if (currentLink.equals("Column Details")) {
								%>
								<li class="nav-item active">
									<%
										} else {
									%>

								<li class="nav-item">
									<%
										}
									%> <a onClick="validateHrefVulnerability(this)"  href="profileColumnDataTemplateView" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Column Profiling</span>
									</a>
								</li>
							
							
							
							</ul>
						</li>
						
						<!-- Advanced Rules Starts -->
						<%
							if (currentLink.equals("AdvRulesView")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="advancedRulesTemplateView" class="nav-link"> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Auto Discovered Rules</span>
							</a>
						</li>
						<!-- Advanced Rules Ends -->
						
						<%
							if (currentLink.equals("Data Matching") || currentLink.equals("Data Matching Group")
								|| currentLink.equals("Statistical Matching") || currentLink.equals("Rolling DataMatching") || currentLink.equals("Primary Key Matching")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="getDataMatchingResults" class="nav-link nav-toggle"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;">
								<span class="title">Matching</span> <span class="arrow"></span>
							</a>
							<ul class="sub-menu">


								<%
									if (currentLink.equals("Data Matching")) {
								%>
								<li class="nav-item active">
									<%
										} else {
									%>

								<li class="nav-item">
									<%
										}
									%> <a onClick="validateHrefVulnerability(this)"  href="getDataMatchingResults" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Key Measurement Matching</span>
									</a>
								</li>

								<%
									if (currentLink.equals("Rolling DataMatching")) {
								%>
								<li class="nav-item active">
									<%
										} else {
									%>

								<li class="nav-item">
									<%
										}
									%> <a onClick="validateHrefVulnerability(this)"  href="getRollDataMatchingResults" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Roll DataMatching</span>
									</a>
								</li>
								<%-- <%
							if (currentLink.equals("Data Matching Group")) {
						%>
								<li class="nav-item active">
									<%
								} else {
							%>
								<li class="nav-item">
									<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="DataMatchingGroupResultView" class="nav-link"> <span class="title">Group By Matching</span>
									</a>
								</li> --%>

								<%
									if (currentLink.equals("Statistical Matching")) {
								%>
								<li class="nav-item active">
									<%
										} else {
									%>

								<li class="nav-item">
									<%
										}
									%> <a onClick="validateHrefVulnerability(this)"  href="StatisticalMatchingResultView" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;">
										<span class="title">Fingerprint Matching</span>
									</a>
								</li>

								<%
									if (currentLink.equals("Schema Matching")) {
								%>
								<li class="nav-item active">
									<%
										} else {
									%>

								<li class="nav-item">
									<%
										}
									%> <a onClick="validateHrefVulnerability(this)"  href="SchemaMatchingResultView" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Schema Matching</span>
									</a>
								</li>



									<!-- changes for primary key matching -->
								<%
									if (currentLink.equals("Primary Key Matching")) {
								%>
								<li class="nav-item active">
									<%
										} else {
									%>

								<li class="nav-item">
									<%
										}
									%> <a onClick="validateHrefVulnerability(this)"  href="getPrimaryKeyMatchingResultsDetails" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Primary Key Matching</span>
									</a>
								</li>
								
							</ul>
						</li>


						<!-- Added File Monitoring  -->
						<%
							if (currentLink.equals("File Monitoring")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="fileMonitoringView" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">File Monitoring</span>
							</a>
						</li>



						<%
							if (currentLink.equals("File Management")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="FileManagementResultView" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">File Management</span>
							</a>
						</li>
						<%
							if (currentLink.equals("Model Governance")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="ModelGovernanceResultView" class="nav-link"> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Model Governance</span>
							</a>
						</li>

						<%
							if (currentLink.equals("Model Governance Dashboard")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="ModelGovernanceDashboardResultView" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;">
								<span class="title">Model Governance Dashboard</span>
							</a>
						</li>


						<%
							if (currentLink.equals("Log Files")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="downloadLogFiles" class="nav-link"> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Log Files</span>
							</a>
						</li>


						<%
							}
						%>

					</ul>
				</li>
				<%
					}
				%>
				<%
					if (module.containsKey("Dash Configuration")) {
					if (currentSection != null && currentSection.equals("Dash Configuration")) {
				%>

				<li class="nav-item active open">
					<%
						} else {
					%>

				<li class="nav-item">
					<%
						}
					%> <a onClick="validateHrefVulnerability(this)"  href="javascript:;" class="nav-link nav-toggle"> <img src="./assets/img/svg/setting.svg" alt="Dash Configuration" style="width: 16px;"> <span
							class="title">Dash
							Configuration </span> <%
 	if (currentSection != null && currentSection.equals("Dash Configuration")) {
 %> <span class="arrow open"></span> <%
 	} else {
 %> <span class="arrow"></span> <%
 	}
 %>

					</a>
					<ul class="sub-menu">
						<%
							flag = RBACController.rbac("Dash Configuration", "R", session);
						if (flag) {
						%>
						<%
							if (currentLink.equals("definition")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="dashConfiguration" class="nav-link nav-toggle"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Definition & Map</span>
							</a>
						</li>
						<%
							}
						%>
					</ul> <%
 	}
 %>
				</li>
			
				<%
					if (module.containsKey("User Settings")) {
					if (currentSection != null && currentSection.equals("User Settings")) {
				%>
				<li class="nav-item active open">
					<%
						} else {
					%>

				<li class="nav-item">
					<%
						}
					%> <a onClick="validateHrefVulnerability(this)"  href="changePassword" class="nav-link nav-toggle"> <img src="./assets/img/svg/user.svg" alt="User Settings" style="width: 16px;"> <span class="title">User
							Settings</span> <span class="arrow"></span>
					</a>

					<ul class="sub-menu">
					<%
                            if (currentLink.equals("AlertNotifications")) {
                        %>
						<li class="nav-item active">
							<%
							} else {
							%>

						<li class="nav-item">
							<%
							}
							%> <a
							onClick="validateHrefVulnerability(this)" href="alertNotificationView"
							class="nav-link "> <img src="./assets/img/svg/sub-menu.svg"
								alt="Sub Menu" style="width: 7px;"><span class="title">Alert Notifications</span>
						</a>
						</li>

						<%
							if (currentLink.equals("changePassword")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="changePassword" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Change Password </span>
							</a>
						</li>
						<%
							if (currentLink.equals("generateSecureAPI")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="generateSecureAPI" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Generate API Token</span>
							</a>
						</li>
						<%
							if (currentLink.equals("Migrate Database")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="migrateDatabase" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Migrate Database </span>
							</a>
						</li>

						<%
							flag = RBACController.rbac("User Settings", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>


						<%
							if (currentLink.equals("View Modules")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="accessControls" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">View Modules </span>
							</a>
						</li>
						<%
							}
						%>
						
						<%
							flag = RBACController.rbac("User Settings", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>


						<%
							if (currentLink.equals("View Tags")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="databuckTags" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">View Tags </span>
							</a>
						</li>
						<%
							}
						%>
						<!-- Features Access Control Starts -->
						<%
							flag = RBACController.rbac("User Settings", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						
						<%
							if (currentLink.equals("Features Access Control")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="componentAccessControl" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Features Access Control</span>
							</a>
						</li>
						<%
							}
						%>
						<!-- Features Access Control Ends -->
						<%
							flag = RBACController.rbac("User Settings", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("View Roles")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="roleManagement" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">View Roles </span>
							</a>
						</li>
						<%
							}
						%>
						<%-- 			<%
							flag = RBACController.rbac("User Settings", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Add New Role")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="addNewRole" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Add New Role</span>
							</a>
						</li>
						<%
							}
						%>
 --%>						
 					<%
							flag = RBACController.rbac("User Settings", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("View Users")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="viewUsers" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">View
									Users </span>
							</a>
						</li>
						<%
							}
						%>
						<%
							flag = RBACController.rbac("User Settings", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%-- <%
							if (currentLink.equals("Add New User")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%>  --%><!-- <a onClick="validateHrefVulnerability(this)"  href="addNewUser" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Add New User</span>
							</a>  --><%
 	if (currentLink.equals("View Domain")) {
 %>

						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="domainViewList" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Domain Library</span>
							</a>
						</li>
						
						<%
						 	if (currentLink.equals("View Dimension")) {
						 %>

						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="dimensionViewList" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Dimension Library</span>
							</a>
						</li>
						
						<!-- Defect codes Starts -->
						<%
							if (currentLink.equals("defectCodesView")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>
							

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="defectCodesView" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Defect codes</span>
							</a>
						</li>
						
						<!-- Defect codes Ends -->
						
						<%
							if (currentLink.equals("viewProject")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="viewProject" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">View Project</span>
							</a>
						</li>


						<%
							if (currentLink.equals("Add New Project")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%><!--  <a onClick="validateHrefVulnerability(this)"  href="addNewProject" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Add New Project</span>
							</a> -->
						</li>
						<%
							}
						%>
						<!-- Code By : Avishkar Date 06-07-2020 -->
						<!-- LdapGroupRoleMap -->
						<%
							flag = RBACController.rbac("User Settings", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("loginGroupMapping")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="loginGroupMapping" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Login Group Mapping</span>
							</a>
						</li>
						<%
							}
						%>
						<!-- LdapGroupRoleMap ends -->

						<!-- Code By : Anant S. Mahale Date 23-03-2020 -->
						<!-- add new location -->
						<%
							flag = RBACController.rbac("User Settings", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Add New Location")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="addNewLocation" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Add New Location</span>
							</a>
						</li>
						<%
							}
						%>
						<!-- add new location ends -->
						<!-- Map Location & Validation -->
						<%
							flag = RBACController.rbac("User Settings", "C", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("Map Location & Validation")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="mapLocationAndValidation" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">Map Location & Validation</span>
							</a>
						</li>
						<%
							}
						%>
						<!-- Map Location & Validation ends -->
						<%
							flag = RBACController.rbac("User Settings", "R", session);
						//System.out.println("flag=" + flag);
						if (flag) {
						%>
						<%
							if (currentLink.equals("View Users")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="licenseInformation" class="nav-link "> <img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"><span class="title">License Information</span>
							</a>
						</li>
						<%
							}
						%>
                           <%
							flag = RBACController.rbac("User Settings", "R", session);
			
						if (flag) {
						%>
						<%
							if (currentLink.equals("Access Log")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="accessLog" class="nav-link "><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Access Log
									 </span>
							</a>
						</li>
						<%
							}
						%>
						<%
							if (currentLink.equals("exportUI") || currentLink.equals("importUI")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="getImportUI" class="nav-link nav-toggle"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Import and Export</span> <span
									class="arrow"></span>
							</a>
							<ul class="sub-menu">


								<%
									if (currentLink.equals("importUI")) {
								%>
								<li class="nav-item active">
									<%
										} else {
									%>

								<li class="nav-item">
									<%
										}
									%> <a onClick="validateHrefVulnerability(this)"  href="getImportUI" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Import</span>
									</a>
								</li>


								<%
									if (currentLink.equals("exportUI")) {
								%>
								<li class="nav-item active">
									<%
										} else {
									%>

								<li class="nav-item">
									<%
										}
									%> <a onClick="validateHrefVulnerability(this)"  href="getExportUI" class="nav-link"><img src="./assets/img/svg/sub-menu.svg" alt="Sub Menu" style="width: 7px;"> <span class="title">Export</span>
									</a>
								</li>


								<!-- -- -->
							</ul>
						</li>


						<!-- Added File Monitoring -->
						<%-- 		<%
							flag = RBACController.rbac("User Settings", "C", session);
								//System.out.println("flag=" + flag);
								if (flag) {
						%>
						<%
							if (currentLink.equals("File Monitoring")) {
						%>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="fileMonitoringUI" class="nav-link "> <span class="title">File Monitoring</span>
							</a>
						</li>
						<%
							}
						%> --%>

						<!-- --------------------------------------- -->


						<%-- 	<%
							if (currentLink.equals("File Monitoring")) {
					   %>
						<li class="nav-item active">
							<%
								} else {
							%>

						<li class="nav-item">
							<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="javascript:;" class="nav-link nav-toggle"> <span class="title">File Monitoring</span> <span
									class="arrow"></span>
							</a>
							<ul class="sub-menu">


								<%
									if (currentLink.equals("File Monitoring Configuration")) {
								%>
								<li class="nav-item active">
									<%
										} else {
									%>

								<li class="nav-item">
									<%
										}
									%> <a onClick="validateHrefVulnerability(this)"  href="fileMonitoringUI" class="nav-link"> <span class="title">File Monitoring Configuration</span>
									</a>
								</li>


								<%
									if (currentLink.equals("File Monitoring Results")) {
								%>
								<li class="nav-item active">
									<%
										} else {
									%>

								<li class="nav-item">
									<%
										}
									%> <a onClick="validateHrefVulnerability(this)"  href="getExportUI" class="nav-link">
										<span class="title">File Monitoring Results</span>
									</a>
								</li>


								<!-- -- -->
							</ul>
						</li>--%>

						<!-- END OF File Monitoring -->

						<%
							}
						%>
						<%-- <%
					
						if (currentSection != null && currentSection.equals("User Training")) {
				%>
						<li class="nav-item active open">
							<%
						} else {
					%>

						<li class="nav-item">
							<%
						}
					%> <a onClick="validateHrefVulnerability(this)"  href="javascript:;" class="nav-link nav-toggle"> <i class="fa fa-university"></i> <span class="title">User
									Training
								</span> <span class="arrow"></span>
							</a>
							<ul class="sub-menu">

								<%
							flag = RBACController.rbac("Validation Check", "R", session);
								//System.out.println("flag=" + flag);
								if (flag) {
						%>

								<%
							if (currentLink.equals("Videos")) {
						%>
								<li class="nav-item active">
									<%
								} else {
							%>

								<li class="nav-item">
									<%
								}
							%> <a onClick="validateHrefVulnerability(this)"  href="UserTrainingVideos" class="nav-link "> <span class="title">Videos </span>
									</a>
								</li>
								<%
							}
						%>
							</ul>
						</li> --%>

						<!--  </ul>
                            <ul class="sub-menu"> -->
					</ul> <!-- END SIDEBAR MENU -->
					<!-- END SIDEBAR MENU -->
</li>	
					<%	
					if (module.containsKey("Application Settings")) {	
						if (currentSection != null && currentSection.equals("Application Settings")) {	
			%>	
				            <li class="nav-item active open">	
			<%	
						} else {	
			%>	
					
				            <li class="nav-item">	
			<%	
						}	
					%> <a onClick="validateHrefVulnerability(this)"  href="applicationSettingsView?vData=appdb" class="nav-link nav-toggle"> 
					<img src="./assets/img/svg/configuration.svg" alt="Application Settings" style="width: 16px;"> <span class="title">Application	Settings</span> <span class="arrow"></span>	
				</a>	
					<ul class="sub-menu">	
						<%	
							flag = RBACController.rbac("Application Settings", "R", session);	
						    	
								//System.out.println("flag="+flag);	
								if (flag) {	
							
							if (currentLink.equals("AppSettingsView")) {	
						%>	
						<li class="nav-item active">	
							<%	
								} else {	
							%>	
							
						<li class="nav-item">	
							<%	
								}	
							%> <a onClick="validateHrefVulnerability(this)"  href="applicationSettingsView?vData=appdb" class="nav-link"> <span	
								class="title">View / Edit </span>	
						</a>	
						</li>
						<%
									if (currentLink.equals("Application Reboot")) {
								%>
								<li class="nav-item active">
									<%
										} else {
									%>

								<li class="nav-item">
									<%
										}
									%> <a onClick="validateHrefVulnerability(this)"  href="applicationReboot" class="nav-link"> <span class="title">Application Restart</span>
									</a>
								</li>	
						<%	
							}	
						%>	
							
					</ul>
						
				</li>	
				<%	
					}	
				%>
		</div>
	</div>
	<!--*************** END SIDEBAR ********************************************-->