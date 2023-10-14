/*
	jwfClasses.js : All base classes to implemnt client side framework logic
*/

var jwfGlobal = {
	ALL_LOAD_PAGE_LOAD: 0,
	ALL_LOAD_SB_CLICK: 1,
	ALL_LOAD_NEW_CLICK:	2,
	API_EVT_SEED_DATA: 0,
	API_EVT_SBSEARCH_CLICK: 1,
	API_EVT_SBANKER_CLICK: 2,
	API_EVT_RELEASE_LOCKS: 10,
	API_EVT_RELEASE_LOCK: 11,
	API_EVT_SAVE_ENTITY_GRAPH: 16,
	API_EVT_GET_LOOKUP_DATA: 99,
	API_DEFAULT_MSG: 'Please wait ... Saving Data ...',
	API_LOOKUP_COMPONENT_ID: 1,
	LOOKUP_TYPE_LOCAL_COMBO: 0,
	LOOKUP_TYPE_REMOTE_COMBO: 1,
	LOOKUP_TYPE_DIALOG: 2,
	LOOKUP_DIALOG_GRID: 'jwf-lookup-dialog-grid',
	LOCAL_COMBO_BOX_COLUMNS: '<th style="width: 0%;" data-xtype="text" data-attributes="fieldname: Code;">#</th>' +
			'<th style="width: 100%;" data-xtype="text" data-attributes="fieldname: Text;">Text</th>',
	DYNAMIC_ATTRIBUTE: '(?)',
	CSS_CLASS_ROW_LAYOUT: 'fieldset-row',
	CSS_CLASS_ROW_SELECTED: 'jwf-browse-tr-selected',
	CSS_CLASS_DATA_PANEL: 'jwf-data-panel',
	CSS_CLASS_PANEL_COLLAPSE: 'jwf-data-panel-collapse',
	CSS_CLASS_BASEVALUE_INVALID: 'invalid-base-value',
	PANEL_EXPAND_SYMBOL: '&#9658; ',
	PANEL_COLLAPSE_SYMBOL: '&#9650; ',
	DATA_STATE_NO_CHANGE: 0,
	DATA_STATE_UPDATE: 1,
	DATA_STATE_INSERT: 2,
	DATA_STATE_DELETE: -1,
	LIST_PLACEHOLDER_TEXT: 'please select',
	LIST_PLACEHOLDER_OPTION: { "Code": "-1", "Text": "please select"},
	COMBO_BOX_CONTAINER: 'jwf-combo-box-container',
	COMBO_BOX_GRID: 'jwf-combo-box-grid',
	COMBO_BOX_FOOTER: 'jwf-combo-box-footer',
	GRID_TYPE_BROWSE: 0,
	GRID_TYPE_COMBO_BOX: 1,
	GRID_TYPE_DIALOG_ONE: 2,
	GRID_TYPE_DIALOG_MULTI: 3,
	GRID_TYPE_DATA_NAV: 4,
	GRID_TYPE_DATA_EDIT: 5,
	EVT_KEYCODE_TAB: 9,
	EVT_KEYCODE_ENTER: 13,
	EVT_KEYCODE_PAGEUP: 33,
	EVT_KEYCODE_PAGEDOWN: 34,
	EVT_KEYCODE_HOME: 36,
	EVT_KEYCODE_END: 35,
	EVT_KEYCODE_UPARROW: 38,
	EVT_KEYCODE_DOWNARROW: 40,
	MODAL_DIALOG_ID_LOOKUP: 1,
	EVT_MODAL_DIALOG_ONLOAD: 1,
	EVT_MODAL_DIALOG_ONCLOSE: 2,
	JWF_MULTI_SELECT: 'jwf-multi-select',
	APP_STD_EXCEPTION_MSG: 'Application error, kindly retry last action. If error repeats, contact application support with details below',
	API_STD_EXCEPTION_MSG: 'Server Response',
	WEBTRANS_LOGIN_PAGE: "../../FrmRloginmain.aspx",
	OBJECT_REF_NULL: { "Code": "-1", "Text": null },
	DATE_ENTRY_YEAR_RANGE: {
			MinYear: (new Date()).getFullYear() - 6, MaxYear: (new Date()).getFullYear() + 6,
			MinDate: '01/01/' + ( (new Date()).getFullYear() - 6 ).toString(),
			MaxDate: '01/01/' + ( (new Date()).getFullYear() + 6 ).toString()
		}
}

/**************************************************************************
Base Type Extensions for date and error handling
***************************************************************************/
JSON.dateParser = function(key, value) {
	//var reISO = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*))(?:Z|(\+|-)([\d|:]*))?$/;
	var reISO = /^(\d{4}\-\d\d\-\d\d([tT][\d:\.]*)?)([zZ]|([+\-])(\d\d):(\d\d))?$/;
	var reMsAjax = /^\/Date\((d|-|.*)\)[\/|\\]$/;
	if (typeof value === 'string') {
		var a = reISO.exec(value);
		//if (a) {	return new Date(value);	}
		if (a) return new Date(value.split('T')[0]);
		if (!JSON.parseMsAjaxDate) return value;
		a = reMsAjax.exec(value);
		if (a) {
			var b = a[1].split(/[-+,.]/);
		return new Date(b[0] ? +b[0] : 0 - +b[1]);
		}
	}
	return value;
};

Error.prototype.errorInfo = function() { return (this.stack) ? this.stack : this.message; }
Error.prototype.display = function(sContext) {
	PageCtrl.debug.log('Error occured ' + sContext + '\n' + this.errorInfo());
	PageCtrl.appMessage(jwfGlobal.APP_STD_EXCEPTION_MSG, PageCtrl.debug.debugLog);
}

Date.daysBetween = function( date1, date2 ) {
  //Get 1 day in milliseconds
  var one_day=1000*60*60*24;

  // Convert both dates to milliseconds
  var date1_ms = date1.getTime();
  var date2_ms = date2.getTime();

  // Calculate the difference in milliseconds
  var difference_ms = date2_ms - date1_ms;

  // Convert back to days and return
  return Math.round(difference_ms/one_day);
}

/**************************************************************************
PageController: Most high level class/function
***************************************************************************/
var PageCtrl = new (function() {
	var aHTabs = [], aVTabs = [], oPageDataList = {}, oObjectCollection = {}, oNewEntityCollection = {}, oDataValidations = {};

	this.ComponentId = -1;
	this.ComponentName = 'Default';
	this.DefaultHTab = 0;
	this.DefaultVTab = 0;
	this.ColumnsInfo = {};
	this.FocusGrid = '';
	this.FrameDomObj = null;
	this.ComponentBootstrap = null;

	this.CloseFrame = function() { window.frameElement.src = ''; }

	this.getQueryStrings = function() {
		var oQueryStrings = {}, aQryStrs = [], sPageUrl = document.location.toString();
		var sQryStr = (sPageUrl.split('?').length > 1) ? sPageUrl.split('?')[1] : '';

		if (sQryStr.length > 0) { aQryStrs = sQryStr.split('&'); }

		aQryStrs.forEach( function(sQryParm, nParmIndex) {
			oQueryStrings[sQryParm.split('=')[0]] = sQryParm.split('=')[1];
		});

		return oQueryStrings;
	}

	this.jwfBootStrap = function() {
		var oComponentBootstrapData = null;

		Object.freeze(jwfGlobal);
		Modal.setup();

		window.onerror = function(sMessage, sUrl, nLineNo) { PageCtrl.appMessage(jwfGlobal.APP_STD_EXCEPTION_MSG, sMessage); };
		$(document).unbind('keydown').bind('keydown', nullifyBackSpace(event));
		$(window).bind('unload', function () { PageCtrl.ClosePage() });

		if (typeof(jwfComponentBootstrap) === 'function') {
			this.ComponentBootstrap = jwfComponentBootstrap();
			PageCtrl.getSeedData(this.ComponentBootstrap.ComponentId);
		} else {
			PageCtrl.appMessage(jwfGlobal.APP_STD_EXCEPTION_MSG, "Mandatory 'jwf framework bootstrap events' are not defined for this page, returning to menu.", false, this.CloseFrame);
		}

		this.setObjectById('QueryString', PageCtrl.getQueryStrings());
		this.debug.setup();
		this.setupPageButtons();

		//function CloseFrame() { window.frameElement.src = ''; }

		function nullifyBackSpace (event) {
			var doPrevent = false;
			if (event.keyCode === 8) {
				var d = event.srcElement || event.target;
				if ((d.tagName.toUpperCase() === 'INPUT' &&
					 (
						 d.type.toUpperCase() === 'TEXT' ||
						 d.type.toUpperCase() === 'PASSWORD' ||
						 d.type.toUpperCase() === 'FILE' ||
						 d.type.toUpperCase() === 'SEARCH' ||
						 d.type.toUpperCase() === 'EMAIL' ||
						 d.type.toUpperCase() === 'NUMBER' ||
						 d.type.toUpperCase() === 'DATE' )
					 ) ||
					 d.tagName.toUpperCase() === 'TEXTAREA') {
					doPrevent = d.readOnly || d.disabled;
				}
				else {
					doPrevent = true;
				}
			}
		}
	}

	this.inilialize = function(nComp, sCompName, nDefaultHTab, nDefaultVTab, sSBGridId, sDefaultTabId, oEventCallBacks) {
		this.ComponentId = nComp;
		this.ComponentName = sCompName;
		this.DefaultHTab = nDefaultHTab;
		this.DefaultVTab = nDefaultVTab;
		this.SBGridId = sSBGridId;
		this.DefaultTabId = sDefaultTabId;
		this.EventCallBacks = oEventCallBacks;
		this.ApiCallActive = false;

		// Webtrans integration, call JwfPage opened method of 'RMultiWinDHTMLX.aspx'
		this.FrameDomObj = window.frameElement;

		if (this.FrameDomObj) {
			window.parent.JwfPage.PageOpened(this.FrameDomObj, this.ComponentName);
		}

		// Initialize custom fields
		$(window).resize(function() { Lookup.ComboBox.onResize(); });
		$(window).contextmenu(function() { Lookup.ComboBox.onResize(); });

		//EditDataTab.setup();
		//this.setupPageButtons();
		UIManager.setupCustomInputs();
		UIManager.setupDataPanels();
		//Modal.setup();
		Lookup.setup();
		MultiSelect.setup();
		//this.debug.setup();
	}

	this.debug = {
		debugLog: '',
		debugDialogOpen: false,
		debugLogLimit: 300000,
		developerMode: true,
		setup: function() {
			$(document).on("keydown", function(oEvent) {
				if ( (oEvent.keyCode === 81) && (oEvent.ctrlKey) ) {
					PageCtrl.debug.displayLog();
					return false;
				} else { return true; }
			});
		},
		setDeveloperMode: function(oCheckBox) {
			this.developerMode = oCheckBox.checked;
			this.flushDebugLog();
		},
		log: function(sContext, oLogObject) {
			var sFullContext = new Date().toLocaleDateString() + ' ' + new Date().toLocaleTimeString() + ' ' + sContext;
			var lException = (oLogObject) ? (oLogObject.constructor.name.indexOf('Error') > -1) ? true : false : false;
			var sCurrentMsg = '';

			if (this.debugLog.length > this.debugLog.debugLogLimit) { this.flushDebugLog(); }

			if (lException) {
				sCurrentMsg = sFullContext + ' ' + oLogObject.errorInfo();
			} else {
				sCurrentMsg = sFullContext + ' ' + ( (typeof oLogObject === 'string') ? oLogObject : JSON.stringify(oLogObject));
			}
			this.debugLog = ((this.developerMode) ? (this.debugLog + sCurrentMsg) : sCurrentMsg) + '\n';
			if (lException) { PageCtrl.appMessage(jwfGlobal.APP_STD_EXCEPTION_MSG, this.debugLog); }
		},
		displayLog: function() {
			if ( (this.developerMode) && (!this.debugDialogOpen) ) {
				var sLimitMsg = UTIL.getTokenizedMessage('Debug log below as of now ({0}) bytes.  Limit is ({1}) bytes.', [ this.debugLog.length, this.debugLogLimit ]);
				this.debugDialogOpen = true;
				PageCtrl.appMessage(sLimitMsg, this.debugLog, true, this.closeDebugDialog);
			}
		},
		closeDebugDialog: function() {
			PageCtrl.debug.debugDialogOpen = false;
		},
		flushDebugLog: function() {
			this.debugLog = '';
		}
	}

	this.appMessage = function(sUserMsg, sTechnicalMsg, lExpanded, cbFunction) {
		var oAppMsgDialog = document.getElementsByClassName('jwf-app-message-dialog')[0];
		var oDialogParent = oAppMsgDialog.parentNode, oButton = oAppMsgDialog.querySelector('button');

		oAppMsgDialog.querySelector('h3').innerHTML = 'Client Application';
		oAppMsgDialog.querySelector('p').innerHTML = sUserMsg;

		Overlay.display();
		oAppMsgDialog.querySelector('textarea').style.display = (sTechnicalMsg) ? 'block' : 'none';
		oAppMsgDialog.querySelector('textarea').innerHTML = sTechnicalMsg;
		oAppMsgDialog.querySelector('textarea').setAttribute('data-expanded', (lExpanded) ? 'true' : 'false');
		oAppMsgDialog.style.display = 'block';

		document.body.appendChild(oAppMsgDialog);   		// move dialog to body

		$(oButton).on('click', appMessageClose);
		$(oButton).on('keydown', function(oEvent) {
			if (oEvent.keyCode === 13) { appMessageClose(); }
		});
		oButton.focus();

		function appMessageClose() {
			$(oButton).off('keydown');
			$(oButton).off('click');
			oAppMsgDialog.querySelector('textarea').innerHTML = '';
			oAppMsgDialog.style.display = 'none';
			oDialogParent.appendChild(oAppMsgDialog);		// move dialog back to invisible parent
			Overlay.hide();
			if (cbFunction) { cbFunction(); }
		}
	}

	this.setupPageButtons = function() {
		var aPageButtons = $("button[data-xtype='page-button']"), sBtnType = '', nNoOfButtons = aPageButtons.length;
		var aBtnTypes = ['close-page','close-entity', 'save-entity'], aToolTips = ['Close Page','Close Contract', 'Save Contract'];

		for (var i = 0; i < nNoOfButtons; i++) {
			var oDataAttributes = UTIL.getDataAttributes(aPageButtons[i].getAttribute('data-attributes'));

			sBtnType = oDataAttributes['button-type'];
			//if (!aPageButtons[i].getAttribute('data-tooltip')) { aPageButtons[i].setAttribute('data-tooltip', aToolTips[ aBtnTypes.indexOf(sBtnType) ]); }

			$(aPageButtons[i]).on('click', function(oEvent) {
				var oTarget = oEvent.target, oDataAttributes = UTIL.getDataAttributes(oTarget.getAttribute('data-attributes'));
				var sButtonType = oDataAttributes['button-type'];

				switch(sButtonType) {
					case 'close-page':
						PageCtrl.ClosePage(true);
						break;
					case 'close-entity':
						PageCtrl.CloseEntity(true);
						break;
					case 'save-entity':
						PageCtrl.SaveEntityGraph(oDataAttributes['edit-tab']);
						break;
				}
			});
		}
		return;
	}

	// JWF class instances are set and retrived using below
	this.getObjectById = function(sObjectId) {
		return oObjectCollection[sObjectId];
	}

	this.setObjectById = function(sObjectId, oObject) {
		oObjectCollection[sObjectId] = oObject;
	}

	this.deleteObjectById = function(sObjectId) {
		return delete oObjectCollection[sObjectId];
	}

	this.getObjectsByClassName = function(sClassName) {
		var aObjects = [];
		for (var sKey in oObjectCollection) {
			if (oObjectCollection[sKey].constructor.name === sClassName) { aObjects.push(oObjectCollection[sKey]); }
		}
		return aObjects;
	}

	this.setDataValidations = function(oAllValidationsList) {
		oDataValidations = oAllValidationsList;
	}

	this.getDataValidation = function(sCode) {
		return oDataValidations[sCode];
	}

	this.repalceDataValidationMsg = function(sCode, sValidationMsg) {
		oDataValidations[sCode]['ErrorText'] = sValidationMsg;
	}

	// global general variable data
	this.pageGlobal = new (function () {
		var oGlobalData = {};

		this.setValue = function(sKey, oValue) {
			oGlobalData[sKey] = oValue;
		}
		this.getValue = function(sKey) {
			return oGlobalData[sKey];
		}
	})();

	/* PageController: Tabs related functions #1 - Vertical tabs not generalized */
	this.registerTabs = function (aHorzTabs, aVertTabs) {
		var i = 0, nLength = 0;

		// store horizontal tab buttons as objects + isallowed functions
		aHTabs = [];
		nLength = aHorzTabs.length;
		for (i = 0; i < nLength; i++) {
			var oHorzTab = {};
			oHorzTab.btnobject = document.getElementById(aHorzTabs[i].btnid);
			oHorzTab.isallowed = aHorzTabs[i].isallowed;
			aHTabs.push(oHorzTab);
		}

		// store horizontal tab buttons as objects + isallowed functions + content objects
		aVTabs = [];
		nLength = aVertTabs.length;
		for (i = 0; i < nLength; i++) {
			var oVertTab = {};
			oVertTab.btnobject = document.getElementById(aVertTabs[i].btnid);
			oVertTab.isallowed = aVertTabs[i].isallowed;
			oVertTab.panelobject = document.getElementById(aVertTabs[i].contentid);
			aVTabs.push(oVertTab);
		}
	}

	this.manageVTab = function(nVertTab) {
		var i = 0, nVTabCount = aVTabs.length;
		for (i = 0; i < nVTabCount; i++) {
			aVTabs[i].panelobject.style.display = 'none';
		}
		aVTabs[nVertTab].panelobject.style.display = 'block';
	}

	this.showHTab = function(nHorzTab) {
		if (typeof(aHTabs[nHorzTab].btnobject) === 'object') {
			if (aHTabs[nHorzTab].isallowed(nHorzTab) === true) {
				aHTabs[nHorzTab].btnobject.click();
			}
		}
	}

	this.showVTab = function(nVertTab) {
		var oVTabLabel = null;
		if (aVTabs.length > 0) {
			oVTabLabel = document.querySelector('label[for=' + aVTabs[nVertTab].btnobject.id + ']');
			if (typeof(oVTabLabel) === 'object') {
				oVTabLabel.click();
			}
		}
	}

	// this is not used - can be removed or to be corrected to use it properly
	this.setNewEntityCollection = function(oEmptyObjectCollection) {
		oEmptyEntityCollection = oEmptyObjectCollection;
	}

	this.getNewEntityCollection = function() {
		return oNewEntityCollection;
	}

	this.getNewDataEntity = function(sEntityKey, oCustomFields) {
		var oNewEntity = JSON.parse(JSON.stringify(oNewEntityCollection[sEntityKey]));
		var aEntityKeys = Object.keys(oNewEntity);

		// Add standard filed row id to custom field - so one single loop can take care of root and other entities case elegantly
		oCustomFields = (oCustomFields) ? oCustomFields : {};
		oCustomFields.RowId = parseInt(UTIL.getUniqueId());

		for (var sKey in oCustomFields) {
			if (aEntityKeys.indexOf(sKey) > -1) { oNewEntity[sKey] = oCustomFields[sKey]; }
		}
		return oNewEntity;
	}

	this.getSeedData = function(nComponentId) {
		var oSessionData = getSessionData();
		var oClientSessionInfo = {
				GuiSessionId: UTIL.getUniqueToken(25),
				DotNetSessionId: oSessionData.DotNetSessionId,
				LoginUserId: oSessionData.LoginUserId,
				LoginBranchRowId: oSessionData.LoginBranchRowId,
				LoginBusAccRowId: oSessionData.BusAccRowId
			};

		PageCtrl.setObjectById('ClientSessionInfo', oClientSessionInfo);
		PageCtrl.setObjectById("BusAccRowId", oSessionData.BusAccRowId);
		PageCtrl.setObjectById("UserRowId", oSessionData.LoginUserRowId);
		PageCtrl.setObjectById('SessionData', JSON.parse(oSessionData.AllSessionData));

		ApiDataService( UTIL.getApiDataInWrap(nComponentId, jwfGlobal.API_EVT_SEED_DATA, [PageCtrl.getObjectById('SessionData')['g_bus_acc_rowid']]), setSeedData, -999, { CustomMsg: jwfGlobal.API_DEFAULT_MSG });

		function setSeedData(nResult, oApiResponse, nEvtId, oEvtInfo) {
			var aLocalLists = [], nNoOfLists = 0, aDataValidations = [], lApiCallFailed = false, oApiSeedResult = null;
			var oEditDataTab = null, oMandatoryValidations = {}, sSeedDataError = '', sMoreInfo = '';

			if (nResult === -1) {
				lApiCallFailed = true;
				sSeedDataError = 'Page data could not loaded (network problem or timeout), returning to menu.';
				sMoreInfo = PageCtrl.debug.debugLog;
			} else if (oApiResponse.Result !== 1){
				lApiCallFailed = true;
				sSeedDataError = 'Page data could not loaded (Server call error), returning to menu.';
				sMoreInfo = oApiResponse.ErrorMsg;
			} else { lApiCallFailed = false; }

			if (lApiCallFailed) {
				PageCtrl.appMessage(sSeedDataError,sMoreInfo, false, PageCtrl.ClosePage);
				return;
			}

			// Call jwfProcessComponentSeedData if defined with raw api response else ignore
			if (typeof(PageCtrl.ComponentBootstrap.onSeedDataLoad) === 'function') { PageCtrl.ComponentBootstrap.onSeedDataLoad(oApiResponse); }

			aLocalLists = JSON.parse(oApiResponse.dataOutPacket[0]);
			nNoOfLists = aLocalLists.length;

			// Set local list to be used for local combo box
			for (var i = 0; i < nNoOfLists; i++) {
				oPageDataList[aLocalLists[i].ListName] = aLocalLists[i].ListOptions;
			}

			// Get data validations from out data packet's second element
			aDataValidations = JSON.parse(oApiResponse.dataOutPacket[1]);
			PageCtrl.setDataValidations(JSON.parse(aDataValidations[0]));
			oMandatoryValidations = JSON.parse(aDataValidations[1]);

			// Call Component page load - this is mandatory event, so all EditDataTab, grids etc objects are initialized
			PageCtrl.ComponentBootstrap.onPageLoad();

			oEditDataTab = PageCtrl.getObjectById(PageCtrl.DefaultTabId);

			oEditDataTab.TemplateKeys.forEach( function(sKey, nIndex) {
				if (oMandatoryValidations[sKey]) {
					oEditDataTab.DataTemplate[sKey].MandatoryValidations = oMandatoryValidations[sKey];
				}
			});

			// Get new entities from out data packet's third element
			oNewEntityCollection = JSON.parse(oApiResponse.dataOutPacket[2]);

			// Call users seed data load function
			if (PageCtrl.EventCallBacks.onSeedDataLoad) { PageCtrl.EventCallBacks.onSeedDataLoad(oApiResponse); }
			oEditDataTab.MainEntity = oNewEntityCollection['empty'];

			if (oApiResponse.dataOutPacket.length > 3) {
				PageCtrl.setObjectById('AppVariable', JSON.parse(oApiResponse.dataOutPacket[3]));
			}

			oEditDataTab.LoadAllEntity(jwfGlobal.ALL_LOAD_PAGE_LOAD);

			if (typeof(PageCtrl.ComponentBootstrap.afterPageLoad) === 'function') { PageCtrl.ComponentBootstrap.afterPageLoad(); }
		}
	}

	this.getDataList = function(sListName) {
		return oPageDataList[sListName];
	}

	this.setDataList = function(sListName, aOptions) {
		var aOptionsCopy = aOptions.slice(0);
		oPageDataList[sListName] = aOptionsCopy;
	}

	this.getAllDataLists = function() {
		return oPageDataList;
	}

	/* PageController: special functions to load, save or close window  */
	this.SearchBrowseAnkerClick = function(sRowId) {
		var oEditDataTab = PageCtrl.getObjectById(PageCtrl.DefaultTabId);
		var sApiDataParms = JSON.stringify( {
				EntityId: '',
				HeaderRowId: sRowId
			} );

		if (oEditDataTab.DirtyRecords === 0) {
			ApiDataService({ "Component" : PageCtrl.ComponentId, "Context" : jwfGlobal.API_EVT_SBANKER_CLICK, "dataInPacket": [ sApiDataParms ] },
					null, jwfGlobal.API_EVT_SBANKER_CLICK, {});
		} else {
			PageCtrl.appMessage('Please save or cancel data changes to currently loaded record before moving to other record');
		}
	}

	this.LoadEntityGraph = function() {
		var oEditDataTab = PageCtrl.getObjectById(PageCtrl.DefaultTabId);

		oEditDataTab.LoadAllEntity(jwfGlobal.ALL_LOAD_SB_CLICK);
		PageCtrl.showHTab(this.DefaultHTab);
		PageCtrl.showVTab(this.DefaultVTab);
	}

	this.SaveEntityGraph = function(sEditTab) {
		var oEditTab = PageCtrl.getObjectById(sEditTab), oModifiedEntity = {}, sDataPacket = '';

		if (oEditTab.isEntityReadyToSave()) {
			//oModifiedEntity = cmGetDirtyRecords(oEditTab.MainEntity, oEditTab.SubEntity);
			oModifiedEntity = oEditTab.GetDirtyEntity(oEditTab.MainEntity, oEditTab.SubEntity);
			sDataPacket = JSON.stringify(oModifiedEntity);
			ApiDataService( { "Component" : PageCtrl.ComponentId, "Context" : jwfGlobal.API_EVT_SAVE_ENTITY_GRAPH,
					"dataInPacket": [ sDataPacket ] }, null, jwfGlobal.API_EVT_SAVE_ENTITY_GRAPH, { EditDataTab: oEditTab });
		}
	}

	this.releaseDataLocks = function() {
		ApiDataService( UTIL.getApiDataInWrap(PageCtrl.ComponentId, jwfGlobal.API_EVT_RELEASE_LOCKS, [""]), cbReleaseDataLocks, -999);

		function cbReleaseDataLocks() { return; };
	}

	this.ClosePage = function(lViaPageButton) {
		var oEditDataTab = PageCtrl.getObjectById(PageCtrl.DefaultTabId), nDirtyRecords = 0;

		if (oEditDataTab) { nDirtyRecords = oEditDataTab.DirtyRecords; }

		lViaPageButton = (lViaPageButton) ? true : false;
		if ((lViaPageButton) && (nDirtyRecords > 0)) {
			Modal.confirmDialog( 'Confirm Close',
				UTIL.getTokenizedMessage('There are unsaved changes to current {0}. Are you sure to close?', [ oEditDataTab.EntityName ]),
				['Yes', 'No'], { onDialogClose: ConfirmClose } );
		} else {
			DoClose();
		}

		function ConfirmClose(oEvent) {
			var sEventId = oEvent.EventId;

			switch(sEventId) {
				case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
					sBtnSelected = oEvent.ClickedButton;
					if (sBtnSelected === 'Yes') { DoClose(); }
					break;
			}
		}

		function DoClose() {
			PageCtrl.releaseDataLocks();
			PageCtrl.CloseFrame();
			window.parent.JwfPage.AdjustParentSize(620);
			if (PageCtrl.FrameDomObj) { PageCtrl.FrameDomObj.src = ''; }
		}
	}

	this.CloseEntity = function(lLoadEmptyData) {
		var oEditDataTab = PageCtrl.getObjectById(PageCtrl.DefaultTabId);
		var sHeadEntityKey = oEditDataTab.TemplateKeys[0];   					// Assume every entity has header
		var sHeadRowId = oEditDataTab.MainEntity[sHeadEntityKey].RowId;	// Asumme every head has row id field

		ApiDataService( { "Component" : PageCtrl.ComponentId, "Context" : jwfGlobal.API_EVT_RELEASE_LOCK,
				"dataInPacket": [ sHeadRowId ] }, cbCloseEntity, -999);

		function	cbCloseEntity() {
			if (lLoadEmptyData) {
				oEditDataTab.MainEntity = PageCtrl.getNewDataEntity('empty');
				oEditDataTab.LoadAllEntity(jwfGlobal.ALL_LOAD_PAGE_LOAD);
			}
			return;
		}
	}
})();

/**************************************************************************
EditDataTab: encapsulate all in one tab
***************************************************************************/
function EditDataTab(sEntityId, sEntityName, oErrorPanel, oParmTmpl,
		cbPreLoadEntity, cbGridClick, cbFormChange, cbGetDirtyRecordCount, cbGetDataDependency, cbIsEntityValidToSave, cbGetDirtyEntity, cbEntitySaveResponse, cbFormFocus) {
	var sKey = '', oTemplate = {}, sDataAttributes = '';

	this.EntityId = sEntityId;
	this.EntityName = sEntityName;
	this.ErrorPanel = oErrorPanel;
	this.DirtyRecords	= 0;
	this.DataTemplate = {};
	this.TemplateKeys = [];
	this.MainEntity = {};
	this.SubEntity = {};
	this.SelectedLookupRecords = {};
	this.EmptyTab = true;

	this.PreLoadEntity = cbPreLoadEntity;
	this.GridClick = cbGridClick;
	this.FormChange = cbFormChange;
	this.GetDataDependency = cbGetDataDependency;
	this.GetDirtyRecordCount = cbGetDirtyRecordCount;
	this.IsEntityValidToSave = cbIsEntityValidToSave;
	this.GetDirtyEntity = cbGetDirtyEntity;
	this.EntitySaveResponse = cbEntitySaveResponse;
	this.FormFocus = cbFormFocus;

	// Input must be array even if empty it is ok
	if ((oParmTmpl === null) || (oParmTmpl === undefined)) {
		myDebug("EditDataTab() Debug 00", "Invalid or null data template EditDataTab object creation aborted!");
		return;
	}
	if (typeof cbPreLoadEntity !== 'function') {
		myDebug("EditDataTab() Debug 01", "Invalid or null data path call back functon EditDataTab object creation aborted!");
		return;
	}

	for (sKey in oParmTmpl) {
		var oDataEntry = {}, aFormIds = [];
		var oInputTabOrder = (oParmTmpl[sKey].InputTabOrder) ? oParmTmpl[sKey].InputTabOrder : null;

		oDataEntry.Key = sKey;
		oDataEntry.ParentKey = oParmTmpl[sKey].ParentKey;

		oDataEntry.GridId = oParmTmpl[sKey].GridId;
		oDataEntry.GridDomObj = (oDataEntry.GridId) ? document.getElementById(oDataEntry.GridId) : null;
		oDataEntry.GridData = oParmTmpl[sKey].GridData;
		oDataEntry.MandatoryValidations = [];
		oDataEntry.PendingValidations = [];
		oDataEntry.DataBuffer = null;
		oDataEntry.EditableGrid = false;
		oDataEntry.Target = null;

		oDataEntry.DataButtons = setupDataButtons(sKey);

		if (oDataEntry.GridId) {
			sDataAttributes = 'edit-tab: ' + this.EntityId + '; ' + 'template-key: ' + sKey + ';';
			document.getElementById(oDataEntry.GridId).setAttribute('data-attributes', sDataAttributes);
		}

		aFormIds = UTIL.parseIds(oParmTmpl[sKey].FormId);
		oDataEntry.LogicalFormId = aFormIds[0];
		oDataEntry.ActualFormId = aFormIds[1];

		oDataEntry.LabelsInfo = UIManager.getLabelInfo(sKey);
		if (oDataEntry.LogicalFormId) {
			oDataEntry.ColumnsInfo = UIManager.getFormColumnInfo(oDataEntry.ActualFormId);
			oDataEntry.FormData = oParmTmpl[sKey].FormData;
			PageCtrl.ColumnsInfo[oDataEntry.LogicalFormId] = setupFormTabOrder(oDataEntry.ColumnsInfo, oInputTabOrder);

			// write data attributes so form change event can extract info to get proper JWF objects
			sDataAttributes = 'logical-form-id: ' + oDataEntry.LogicalFormId + '; edit-tab: ' + this.EntityId + '; ' + 'template-key: ' + sKey + ';';
			UIManager.setupFormEvents(oDataEntry.LogicalFormId, oDataEntry.ActualFormId, fixJwfFormChange, sDataAttributes, EditDataTab.jwfFormFocus);
		} else {
			oDataEntry.ColumnsInfo = [];
			oDataEntry.FormData = null;
			oDataEntry.EditableGrid = true;
		}

		oTemplate[sKey] = oDataEntry;
		this.TemplateKeys.push(sKey);    // Push keys in order so LoadPartialEntity can use it
		//PageDebugger.displayDebugLog('new EditDataTab()', sKey);
	}
	this.DataTemplate = oTemplate;
	if (typeof this.GetDataDependency === 'function') { this.DataDependency = this.GetDataDependency(this); } else { this.DataDependency = null; };
	PageCtrl.setObjectById(sEntityId, this);	// add to Page controller by id

	function fixJwfFormChange(oEvent) { EditDataTab.jwfFormChange(oEvent.target, false); }

	function setupFormTabOrder(aFormObjects, oFormTabOrder) {
		var sFieldList = '', nSeedOrder = -1, aFieldList = [], lFieldFound = false, nFieldCounts = 0;

		if (oFormTabOrder) {
			sFieldList = UTIL.replaceAll(oFormTabOrder.FieldList, ' ', '');
			nSeedOrder = oFormTabOrder.SeedOrder;
			aFieldList = (sFieldList.trim().length > 0) ? sFieldList.split(',') : [];
		}

		// if component has specified field order
		if (aFieldList.length > 0) {
			nFieldCounts = aFieldList;
			// if component has specified field order then 'scan all fields, if field found in list set positive tab order'
			for (var i = 0; i < aFieldList.length; i++) {
				lFieldFound = aFormObjects.some( function(oColumnInfo, nIndex) {
					if (oColumnInfo.domObject.id === aFieldList[i]) {
						oColumnInfo.domObject.tabIndex = (nSeedOrder + i + 1);
						return true;
					}
				});

				// if field not found in list then set tab order to 1000
				if ( (!lFieldFound) && (document.getElementById(aFormObjects[i])) ) { aFormObjects[i].domObject.tabIndex = nFieldCounts + i + 1; }
			}

			aFormObjects.sort( function( oFirstObject, oSecondObject ) {
				return (parseInt(oFirstObject.domObject.tabIndex) - parseInt(oSecondObject.domObject.tabIndex));
			});
		}
		return aFormObjects;
	}

	// Setups entity data buttons as each EditDataTab instance getting created
	function setupDataButtons(sKey) {
		var sSearchExpr = "button[data-attributes*='{key}']".replace('{key}',sKey);
		var aDataButtons = $(sSearchExpr), oDataButtons = {}, sBtnType = '';
		var nNoOfButtons = aDataButtons.length;

		var aBtnTypes = ['new','cancel','delete'], aToolTips = ['New','Cancel','Delete'];

		for (var i = 0; i < nNoOfButtons; i++) {
			var oDataAttributes = UTIL.getDataAttributes(aDataButtons[i].getAttribute('data-attributes'));

			sBtnType = oDataAttributes['button-type'];
			oDataButtons[sBtnType] = aDataButtons[i];

			//if (!aDataButtons[i].getAttribute('data-tooltip')) { aDataButtons[i].setAttribute('data-tooltip', aToolTips[ aBtnTypes.indexOf(sBtnType) ]); }

			$(aDataButtons[i]).on('click', function(oEvent) {
				var oTarget = oEvent.target, oDataAttributes = UTIL.getDataAttributes(oTarget.getAttribute('data-attributes'));
				var oEditDataTab = PageCtrl.getObjectById(oDataAttributes['edit-tab']), sButtonType = oDataAttributes['button-type'];
				var oTemplateEntry = oEditDataTab.DataTemplate[oDataAttributes['tmpl-key']];
				var sEntityKey = (oDataAttributes['entity-key']) ? oDataAttributes['entity-key'] : null;
				var lRootKey = (oEditDataTab.TemplateKeys.indexOf(oDataAttributes['tmpl-key']) === 0) ? true : false;

				/* Pradeep 13/05/2017 - Prevent button click event, if click gets fired via accesskey press and logically button is disabled */
				var lAllowBtnClickEvent = ( $(oTarget).closest('div')[0].getAttribute('data-disabled') ) ? false : true;

				if (lAllowBtnClickEvent) {
					switch(sButtonType) {
						case 'new':
							if ( (lRootKey) && (PageCtrl.EventCallBacks.onNewEntityClick)) {
								PageCtrl.EventCallBacks.onNewEntityClick(
									{ EditDataTab: oEditDataTab, TmplKey: oDataAttributes['tmpl-key'], EntityKey: sEntityKey },
									doDefault);
							}  else {
								oEditDataTab.LoadNewEntityTree(oDataAttributes['tmpl-key'],sEntityKey);
							}
							break;
						case 'cancel':
							oEditDataTab.CancelEntityTree(oTemplateEntry, lRootKey);
							break;
						case 'delete':
							oEditDataTab.DeleteEntityTree(oTemplateEntry, oEditDataTab);
							break;
					}
				}
			});

			function doDefault(oEventInfo) {
				var oEditDataTab = oEventInfo.EditDataTab;
				oEditDataTab.LoadNewEntityTree(oEventInfo.TmplKey,oEventInfo.EntityKey);
			}
		}
		return oDataButtons;
	}

	this.isEntityReadyToSave = function() {
		var oEditDataTab = this, aTmplKeys = this.TemplateKeys, sMessage = null;

		/* Pradeep 09/05/2019 Next line added to refresh dirty count for programmitical change to model JSON */
		oEditDataTab.setDirtyRecordCount();

		if (this.DirtyRecords < 1) {
			sMessage = UTIL.getTokenizedMessage('Cannot save {0} as data is not changed.', [ this.EntityName ]);
		} else if (aTmplKeys.some( function(sKey, nIndex) { return (oEditDataTab.DataTemplate[sKey].PendingValidations.length > 0); })) {
			sMessage = UTIL.getTokenizedMessage('Cannot save invalid {0}, kindly correct it as per validations list and click save again.', [ this.EntityName ]);

		} else {
			sMessage = oEditDataTab.IsEntityValidToSave(this);
		}

		if (sMessage) { PageCtrl.appMessage('Incomplete Data',sMessage); }

		return (sMessage) ? false : true;
	}

	// Get latest dirty record count and write it to top right corner of screen
	this.setDirtyRecordCount = function() {
		var oMsgTable = this.ErrorPanel.MsgPanel;

		this.DirtyRecords = this.GetDirtyRecordCount();
		if (oMsgTable.rows[0].cells.length > 2) {
			oMsgTable.rows[0].cells[2].innerHTML = 'Unsaved Records [' + this.DirtyRecords + ']';
		}
	}

	// Set data state buttons at each (Horizonatal) level and cross (Vertical) states of overall data layout
	this.setDataState = function(oFocusTmplEntry) {
		try {
			this.setDirtyRecordCount();
			setHorizonatalDataState(this);
			setVerticalDataState(this);
		} catch(oError) { oError.display('setDataState()'); }

		function setHorizonatalDataState(oEditDataTab) {
			var aTmplKeys = oEditDataTab.TemplateKeys, oTemplateEntry = {}, lBtnState = false;
			var nNoOfKeys = aTmplKeys.length, lDataValid = false, nDBAction = 0, lDataEmpty = false;

			for (var i = 0; i < nNoOfKeys; i++) {
				oTemplateEntry = oEditDataTab.DataTemplate[aTmplKeys[i]];

				// Get basic flags and status for a template entry
				lDataEmpty = (oTemplateEntry.FormData) ? (Object.keys(oTemplateEntry.FormData).length < 1) : true;
				lDataValid = (!lDataEmpty) ? oTemplateEntry.FormData.DataState.Valid : true;
				nDBAction = (!lDataEmpty) ? oTemplateEntry.FormData.DataState.DBAction : -99;

				// Scan all buttons for a template entry and adjust status
				for (var sBtnType in oTemplateEntry.DataButtons) {
					lBtnState = true;
					switch(sBtnType) {
						case 'new':
							lBtnState = (lDataEmpty) ? true : (lDataValid) ? true : false;
							if ((i === 0) && (oEditDataTab.DirtyRecords > 0)) { lBtnState = false; } // root new button
							break;
						case 'cancel':
							lBtnState = (lDataEmpty) ? false : (lDataValid) ? false : true;
							if ((i === 0) && (oEditDataTab.DirtyRecords > 0)) { lBtnState = true; }  // root cancel button
							break;
						case 'delete':
							lBtnState = (lDataEmpty) ? false : (lDataValid) ? true : false;
							break;
					}
					if (oEditDataTab.MainEntity) {
						if (oEditDataTab.MainEntity.ReadOnly) {
							lBtnState = ((sBtnType === 'new') && (i === 0)) ? lBtnState : false;    // if root & new button do not apply logic
						}
					}
					if (lBtnState) { oTemplateEntry.DataButtons[sBtnType].removeAttribute('disabled'); }
					else { oTemplateEntry.DataButtons[sBtnType].setAttribute('disabled', 'disabled'); }
				}
			}
		}

		function setVerticalDataState(oEditTab) {
			var nFirstEmptyIndex = oEditTab.isSomeEmptyEntry(), nFirstInvalid = -1, lApplicableGrid = false;
			var aTemplateKeys = oEditTab.TemplateKeys, nNoOfKeys = oEditTab.TemplateKeys.length;

			// Clear all vertical locking of grid + form (i.e. user can move to anther record)
			oEditTab.TemplateKeys.forEach( function(sKey, nIndex) {
				var oTmplEntry = oEditTab.DataTemplate[sKey], oButtonPanel = null;

				// As we scan to enable buttons, note 1st invalid entry index
				oTmplEntry.FormData = (oTmplEntry.FormData) ? oTmplEntry.FormData : {};
				if ((!oTmplEntry.EditableGrid) && (Object.keys(oTmplEntry.FormData).length > 0) && (nFirstInvalid < 0)) {
					nFirstInvalid = (!oTmplEntry.FormData.DataState.Valid) ? nIndex : -1;
				}

				lApplicableGrid = ((oTmplEntry.GridId) && (oTmplEntry.LogicalFormId)) ? true : false;
				if (lApplicableGrid) {
					oButtonPanel = $(oTmplEntry.DataButtons['new']).closest("div")[0];
					oTmplEntry.GridDomObj.parentNode.removeAttribute('data-disabled');
					oTmplEntry.GridDomObj.removeAttribute('data-disabled');
					oButtonPanel.removeAttribute('data-disabled');
				}
			});

			// Now scan all template entries and if any invalid entry then all grids are disabled (so user canot move to anther record)
			for (var i = (nFirstInvalid < 0) ? nNoOfKeys : 0; i < nNoOfKeys; i++) {
				var oGridTmplEntry = oEditTab.DataTemplate[oEditTab.TemplateKeys[i]];
				var oButtonPanel = null;

				// Apply disable logic to applicable template entries only
				lApplicableGrid = ((oGridTmplEntry.GridId) && (oGridTmplEntry.LogicalFormId));

				// the invalid entry itself need to be handled specially (only grid is disabled, buttons state is as set by horizonal state function)
				if ((i === nFirstInvalid) && (lApplicableGrid)) {
					oGridTmplEntry.GridDomObj.setAttribute('data-disabled','true');
					continue;
				}

				// rest all entries normal handling (grid + all buttons are disabled)
				if (lApplicableGrid) {
					oButtonPanel = $(oGridTmplEntry.DataButtons['new']).closest("div")[0];
					oGridTmplEntry.GridDomObj.parentNode.setAttribute('data-disabled','true');
					oButtonPanel.setAttribute('data-disabled','true');
				}
			}

			// lock all entries below empty entry
			if (nFirstEmptyIndex > -1) {

				nFirstEmptyIndex = nFirstEmptyIndex + 1;
				for (var i = nFirstEmptyIndex; i < nNoOfKeys; i++) {
					var oTmplEntry = oEditTab.DataTemplate[aTemplateKeys[i]], oButtonPanel = null;

					lApplicableGrid = ((oTmplEntry.GridId) && (oTmplEntry.LogicalFormId)) ? true : false;

					if (lApplicableGrid) {
						oButtonPanel = $(oTmplEntry.DataButtons['new']).closest("div")[0];
						oTmplEntry.GridDomObj.parentNode.setAttribute('data-disabled','true');
						oButtonPanel.setAttribute('data-disabled','true');
					}
				}
			}
		}
	}

	this.isSomeEmptyEntry = function() {
		var nEmptyKeyIndex = -1, oEditDataTab = this;

		oEditDataTab.TemplateKeys.some( function(sKey, nIndex) {
			var lEmptyData = (oEditDataTab.DataTemplate[sKey].FormData) ? (Object.keys(oEditDataTab.DataTemplate[sKey].FormData).length < 1) : true;
			var lSearchSuccess = false, lApplicableEntry = false;

			lApplicableEntry = ( ((oEditDataTab.DataTemplate[sKey].GridId) && (oEditDataTab.DataTemplate[sKey].LogicalFormId)) || (nIndex < 1) ) ? true : false;
			lSearchSuccess = ((lApplicableEntry) && (lEmptyData)) ? true : false;
			nEmptyKeyIndex = (lSearchSuccess) ? nIndex : nEmptyKeyIndex;

			return lSearchSuccess;
		});
		return nEmptyKeyIndex;
	}

	// Clears all errors from focus entry - optionally clear all errors in nested entries
	this.clearErrors = function(oFocusEntry, lNested) {
		var nIndex = this.TemplateKeys.indexOf(oFocusEntry.Key), nNoOfKeys = this.TemplateKeys.length;

		if (lNested) {
			for (var i = nIndex; i < nNoOfKeys; i++) {
				this.ErrorPanel.removeErrors("Data value corrected", this.DataTemplate[this.TemplateKeys[i]].PendingValidations);
				this.DataTemplate[this.TemplateKeys[i]].PendingValidations = [];
			}
			this.DirtyRecords = 0;
		} else {
			this.ErrorPanel.removeErrors("Data value corrected", oFocusEntry.PendingValidations);
			oFocusEntry.PendingValidations = [];
		}
		this.ErrorPanel.updateUI();
	}

	this.removeErrors = function(oFocusEntry, aErrorCodes) {
		var sMsg = (this.ErrorPanel.ErrorList.length > aErrorCodes.length) ? "Correct more data as validations rules" : "Data value(s) are corrected";
		this.ErrorPanel.removeErrors(sMsg, aErrorCodes);
		this.ErrorPanel.updateUI();
	}

	this.addErrors = function(oFocusEntry, aErrorCodes, sCustomMsg) {
		var aErrMsgs = [], aErrorCodes = Array.isArray(aErrorCodes) ? aErrorCodes : [];
		var sCustomMsg = (sCustomMsg) ? sCustomMsg : "Kindly correct data value(s)";

		aErrorCodes.sort();
		aErrorCodes.forEach( function(sErrorCode, nIndex) {
			var oValidationMsg = PageCtrl.getDataValidation(sErrorCode);
			var oErrMsg = { key: sErrorCode, Object: oValidationMsg.Object, Field: oValidationMsg.Field, ErrorText: oValidationMsg.ErrorText };

			aErrMsgs.push(oErrMsg);
		});
		this.ErrorPanel.addErrors(sCustomMsg, aErrMsgs);
		this.ErrorPanel.updateUI();
	}

   this.displayPanelMsg = function(sMessage) {
      this.ErrorPanel.addErrors(sMessage, []);
      this.ErrorPanel.updateUI();
   }

	this.LoadAllEntity = function(nAllLoadContext) {
		var sGridId = '', sFormId = '', sRootKey = this.TemplateKeys[0];

		// Call component code which is expected to set json references to all data template entries
		if (!this.PreLoadEntity(this, nAllLoadContext)) { return; }

		// clear all errors
		//this.clearErrors(this.DataTemplate[sRootKey], true);

		// scan entire template and load data
		for (var sKey in this.DataTemplate) {
			// Load grid control if grid id not null
			sGridId = this.DataTemplate[sKey].GridId;
			if (sGridId) {
				if (PageCtrl.getObjectById(sGridId).GridType === jwfGlobal.GRID_TYPE_DATA_EDIT) {
					UIManager.loadDataGrid(this.DataTemplate[sKey].GridData, sGridId);
				} else {
					UIManager.fillViewGrid(sGridId, this.DataTemplate[sKey].GridData);
				}
			}

			// Load form control if for id not null
			sFormId = this.DataTemplate[sKey].LogicalFormId;
			if (sFormId) {
				//if ((sGridId !== null) && (this.DataTemplate[sKey].GridData)) { this.DataTemplate[sKey].FormData = (this.DataTemplate[sKey].GridData.length > 0) ? this.DataTemplate[sKey].GridData[0] : {}; }
				UIManager.fillDataForm(sFormId, this.DataTemplate[sKey].FormData, this.DataTemplate[sKey].ColumnsInfo);
			}
		}
		this.switchEntityMode();
	}


	// Data Scenario (0) Empty data (1) Edit mode (Newly added) (2) Read Only (existing) (3) Edit mode (existing)

	this.switchEntityMode = function() {
		var sHeadEntityKey = this.TemplateKeys[0];   // Assume every entity has header
		var nDataScenario = -1, sModeMsg = 'Not possible data scenario, kindly contact Rafai support';

		var oBtnCloseEntity = document.getElementById('BtnCloseEntity');
		var oBtnSaveEntity = document.getElementById('BtnSaveEntity');

		// Start with disabled state if scnerio 3 it will be enabled
		oBtnCloseEntity.setAttribute('disabled', 'disabled');
		oBtnSaveEntity.setAttribute('disabled', 'disabled');

		// Start with -1 and go on evaluating data scenario
		nDataScenario = ( Object.keys(this.MainEntity[sHeadEntityKey]).length < 1 ) ? 0 : nDataScenario;
		nDataScenario = (nDataScenario === -1) ? (UTIL.isNewRecord(this.MainEntity[sHeadEntityKey])) ? 1 : nDataScenario : nDataScenario;
		nDataScenario = (nDataScenario === -1) ? (this.MainEntity.ReadOnly) ? 2 : 3 : nDataScenario;

		// Now switch nDataScenario and take appropriate actions
		switch (nDataScenario) {
			case 0:
				sModeMsg = UTIL.getTokenizedMessage('Create new {0} or select existing using search browse to edit', [ PageCtrl.ComponentName ]);
				break;
			case 1:
				sModeMsg = UTIL.getTokenizedMessage('Enter all mandatory information as per validation list to save new {0}', [ PageCtrl.ComponentName ]);
				oBtnSaveEntity.removeAttribute('disabled');
				break;
			case 2:
				sModeMsg = "Opened in READONLY MODE, modifications to data can not be saved. User '" + this.MainEntity.LockedByUserId + "' editing it now.";
				sModeMsg = (PageCtrl.getObjectById('ReadOnlyEntityMsg')) ? PageCtrl.getObjectById('ReadOnlyEntityMsg') : sModeMsg;
				break;
			case 3:
				sModeMsg = "Opened in EDIT MODE, modifications to data can be saved";
				oBtnCloseEntity.removeAttribute('disabled');
				oBtnSaveEntity.removeAttribute('disabled');
				break;
		}
		this.ErrorPanel.addErrors(sModeMsg, []);
		this.ErrorPanel.updateUI();
	}

	this.LoadPartialEntity = function(sStartKey, lFillSelectedGrid) {
		var sGridId = '', nGridType = 0, sFormId = '', sKey = '', lFillGrid = false, oFormData = {};
		var nStartIndex = this.TemplateKeys.indexOf(sStartKey), i = 0, nLength = 0;

		if (nStartIndex === -1) {
			myDebug("LoadPartialEntity() Debug 00", "Invalid starting template key partial load of data aborted!");
			return;
		} else {
			nLength = this.TemplateKeys.length;
		}

		for (i = nStartIndex; i < nLength; i++) {
		// scan entire template and load data
			sKey = this.TemplateKeys[i];

			lFillGrid = (i === nStartIndex) ? (lFillSelectedGrid) ? true : false : true;
			myDebug('LoadPartialEntity() Debug 01', 'Is this step entered? ' + i + ' ' + nStartIndex + ' ' + lFillGrid);

			// Bypass grid data load for start index - as typically it will grid itself which got user click
			if (lFillGrid) {
				myDebug('LoadPartialEntity() Debug 02', 'is this step entered? ' + i + ' ' + nStartIndex);

				// Load grid control if grid id not null
				sGridId = this.DataTemplate[sKey].GridId;
				if (sGridId) {
					nGridType = PageCtrl.getObjectById(sGridId).GridType;
					if (nGridType === jwfGlobal.GRID_TYPE_DATA_EDIT) { UIManager.loadDataGrid(this.DataTemplate[sKey].GridData, sGridId); }
					else { UIManager.fillViewGrid(sGridId, this.DataTemplate[sKey].GridData); }
				}
			}

			// Load form control if for id not null
			sFormId = this.DataTemplate[sKey].LogicalFormId;
			if (sFormId !== null) {
				UIManager.fillDataForm(sFormId, this.DataTemplate[sKey].FormData, this.DataTemplate[sKey].ColumnsInfo);
			}
		}
	}

	// As per JWF add data only applicable at (a) Root level or (b) at level where grid and form both are present
	this.LoadNewEntityTree = function(sStartKey, sEntityKey) {
		var sKey = '', nNoOfKeys = 0, oNewEntity = {}, oFocusTarget = null;
		var nStartIndex = this.TemplateKeys.indexOf(sStartKey), sEntityKey = (sEntityKey) ? sEntityKey : sStartKey;
		var sParentKey = this.DataTemplate[sStartKey].ParentKey, lRootKey = (nStartIndex === 0) ? true : false;

		if (nStartIndex < 0) {
			myDebug("LoadNewEntityTree() Debug 00", "Invalid starting template key load empty entity tree of data aborted!");
			return;
		} else if ( (!lRootKey) && (!this.DataTemplate[sStartKey].GridId)) {
			myDebug("LoadNewEntityTree() Debug 01", "Invalid scenario or data template, add new data aborted!");
			return;
		}
		nNoOfKeys = this.TemplateKeys.length;

		this.clearErrors(this.DataTemplate[sStartKey], lRootKey);    // if lRootKey then clear all errors in all levels
		this.DataTemplate[sStartKey].PendingValidations = this.DataTemplate[sStartKey].MandatoryValidations.slice(0);
		this.addErrors(this.DataTemplate[sStartKey], this.DataTemplate[sStartKey].PendingValidations);
		this.DataTemplate[sStartKey].DataBuffer = null;              // part of overall new entry begin process

		if (lRootKey) {
			PageCtrl.showHTab(PageCtrl.DefaultHTab);								// added by Pradeep 14-03-2017
			this.MainEntity = PageCtrl.getNewDataEntity(sEntityKey);      // if root addition then create whole new hierarchy
			this.LoadAllEntity(jwfGlobal.ALL_LOAD_NEW_CLICK);
		} else {
			oNewEntity = PageCtrl.getNewDataEntity(sEntityKey, { 'ParentRowId': this.getParentRowId(sParentKey) } );
			if (!this.DataTemplate[sStartKey].GridData) { this.DataTemplate[sStartKey].GridData = []; }
			this.DataTemplate[sStartKey].GridData.push(oNewEntity);

			// Programatic grid click in refresh row call below automatically refresh GUI as needed
			UIManager.fillViewGrid(this.DataTemplate[sStartKey].GridId, this.DataTemplate[sStartKey].GridData, null, oNewEntity.RowId);
		}

		/* Pradeep : to be refined to make first form element as focused once new record added */
		oFocusTarget = UIManager.getFirstFocusElement(this.DataTemplate[sStartKey].LogicalFormId);
		if (oFocusTarget) { oFocusTarget.focus(); }
	}

	this.getParentRowId = function(sParentKey) {
		return (sParentKey) ? this.DataTemplate[sParentKey].FormData.RowId : null;
	}

	this.CancelEntityTree = function(oFocusEntry, lRootKey) {
		try {
			var lIsNew = null, oDataGrid = null, nNewRowIndex = null;

			if (lRootKey) {
				CancelRootEntity(this, oFocusEntry);

			} else {

				lIsNew = (oFocusEntry.FormData.DataState.DBAction === jwfGlobal.DATA_STATE_INSERT) ? (oFocusEntry.DataBuffer) ? false : true : false;
				oDataGrid = PageCtrl.getObjectById(oFocusEntry.GridId);
				nNewRowIndex = UTIL.indexOfRecord(oFocusEntry.GridData, 'RowId', oFocusEntry.FormData.RowId);

				this.clearErrors(oFocusEntry, false);

				// New means record added but still not valid, once it become valid it is handled as existing (else part)
				if (lIsNew) {
					oFocusEntry.GridData.splice(nNewRowIndex, 1);
					if (oFocusEntry.GridData.length < 1) {
						oFocusEntry.FormData = {};
						this.LoadPartialEntity(oFocusEntry.ParentKey);
					} else { UIManager.fillViewGrid(oFocusEntry.GridId, oFocusEntry.GridData, null, null); }
				} else {
					UTIL.copyJsonRecord(oFocusEntry.FormData, JSON.parse(oFocusEntry.DataBuffer, JSON.dateParser));
					UIManager.fillDataForm(oFocusEntry.LogicalFormId, oFocusEntry.FormData, oFocusEntry.ColumnsInfo);
				}
			}
		} catch(oError) { oError.display('CancelEntityTree()'); }

		// Closure function to confirm cancel of root i.e. entire changes done
		function CancelRootEntity(oEditDataTab, oRootEntry) {
			Modal.confirmDialog( 'Confirm Cancel',
				UTIL.getTokenizedMessage('This will cancel changes done to whole {0}. Are you sure?', [ oEditDataTab.EntityName ]),
				['Yes', 'No'], { onDialogClose: ConfirmCancelRoot } );

			function	ConfirmCancelRoot(oEvent) {
				var sEventId = oEvent.EventId;

				switch(sEventId) {
					case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
						sBtnSelected = oEvent.ClickedButton;
						if (sBtnSelected === 'Yes') { DoCancelRoot(oEditDataTab, oRootEntry); }
						break;
				}
			}
		}

		/*
		Pradeep 28/12/2016
			Below generic code checks contract specific field 'CopyContract' - though it is generic so works for any screen.
		*/
		function DoCancelRoot(oEditDataTab, oRootEntry) {
			var oSBViewGrid = PageCtrl.getObjectById(PageCtrl.SBGridId), oRow = null;
			var lCopyContract = oRootEntry.FormData.CopyContract;
			var sRowId = ( (UTIL.isNewRecord(oRootEntry.FormData)) || (lCopyContract) ) ? null : oRootEntry.FormData.RowId.toString();

			oRow = (sRowId) ? null : oSBViewGrid.getGridRow(0);
			sRowId = (sRowId) ? sRowId : (oRow) ? oRow.cells[0].innerHTML : null;

			oEditDataTab.clearErrors(oRootEntry, true);

			if (sRowId) {
				PageCtrl.SearchBrowseAnkerClick(sRowId);
			} else {
				oEditDataTab.MainEntity = PageCtrl.getNewDataEntity('empty');
				oEditDataTab.LoadAllEntity(jwfGlobal.ALL_LOAD_PAGE_LOAD);
			}
		}
	}

	this.DeleteEntityTree = function(oFocusEntry, oEditDataTab) {
		var lDeleteAllowed = (PageCtrl.EventCallBacks.canDeleteEntity) ? PageCtrl.EventCallBacks.canDeleteEntity(oFocusEntry, oEditDataTab) : true;

		if (lDeleteAllowed) {
			Modal.confirmDialog( 'Confirm Delete',
				'This will mark selected and all child record(s) to be deleted in next save action. Are you sure?',
				['Yes', 'No'], { onDialogClose: ConfirmDeleteEntity } );
		}

		function ConfirmDeleteEntity(oEvent) {
			var sEventId = oEvent.EventId, sBtnSelected = null;

			if (sEventId === jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE) { sBtnSelected = oEvent.ClickedButton; }

			if (sBtnSelected === 'Yes') {
				if (PageCtrl.EventCallBacks.onDeleteEntityClick) {
					PageCtrl.EventCallBacks.onDeleteEntityClick(oFocusEntry, oEditDataTab);
				}
				oEditDataTab.LoadPartialEntity(oFocusEntry.Key, true);
			}
		}
	}


	/*
	// Old/generic version of delete to be refined and make active later

	this.DeleteEntityTree = function(oFocusEntry) {
		var lIsNew = (oFocusEntry.FormData.DataState.DBAction === jwfGlobal.DATA_STATE_INSERT) ? true : false,
			oDataGrid = PageCtrl.getObjectById(oFocusEntry.GridId),
			nRowIndex = UTIL.indexOfRecord(oFocusEntry.GridData, 'RowId', oFocusEntry.FormData.RowId);

		if (lIsNew) {
			oFocusEntry.GridData.splice(nRowIndex, 1);
			if (oFocusEntry.GridData.length < 1) {
				oFocusEntry.FormData = {};
				this.LoadPartialEntity(oFocusEntry.ParentKey);
			} else {
				oDataGrid.deleteGridRow();
			}
		} else {
			oFocusEntry.FormData.DataState.DBAction = jwfGlobal.DATA_STATE_DELETE;
			this.cascadeDeleteFlag(oFocusEntry);
			oDataGrid.deleteGridRow();
		}
	}

	this.cascadeDeleteFlag = function(oFocusEntry) {
		var aChildKeys = this.getChildKeys(oFocusEntry), oEditDataTab = this;

		aChildKeys.forEach( function(sKey, nIndex) {
			var nNoOfRecords = oEditDataTab.DataTemplate[sKey].GridData.length;
			for (var i = 0; i < nNoOfRecords; i++) {
				oEditDataTab.DataTemplate[sKey].GridData[i].DataState.DBAction = jwfGlobal.DATA_STATE_DELETE;
			}
		});
	}
	*/

	this.hasChildRecords = function(oFocusEntry) {
		var nFocusIndex = this.TemplateKeys.indexOf(oFocusEntry.Key), nNoOfKeys = this.TemplateKeys.length;
		var aChildKeys = this.getChildKeys(oFocusEntry);

		return (aChildKeys.length > 0) ? (this.DataTemplate[aChildKeys[0]].GridData.length > 0) : false;
	}

	this.getChildKeys = function(oFocusEntry) {
		var aKeys = [], oEditTab = this;
		this.TemplateKeys.forEach( function(sKey, nIndex) { if (oEditTab.DataTemplate[sKey].ParentKey === oFocusEntry.Key) { aKeys.push(sKey); } });
		return aKeys;
	}

	this.executeDataDependency = function(oTemplateEntry, nContext, oTarget) {
		try {
			if (nContext < 1) { executeDependency(this, oTemplateEntry); } else { executeTriggers(this, oTemplateEntry, oTarget); }
		} catch(oError) { oError.display('executeDataDependency()'); }

		function executeDependency(oEditDataTab, oTemplateEntry) {
			var aDataDependency = oTemplateEntry.DataDependency;
			if (Array.isArray(aDataDependency)) {
				aDataDependency.forEach( function(oDataDependency, nIndex) { processDependency(oDataDependency, oTemplateEntry, 0); });
			}
			return;
		}

		function executeTriggers(oEditDataTab, oTemplateEntry, oTarget) {
			var aDataTriggers = oTemplateEntry.DataTrigger, aDataDependency = oTemplateEntry.DataDependency, isDataTrigger = true;

			if (Array.isArray(aDataTriggers)) {
				aDataTriggers.forEach( function(oDataTrigger, nIndex) {
					if (oDataTrigger.Source === oTarget.id) {
						var sTargets = oDataTrigger.Targets;
						aDataDependency.forEach( function(oDataDependency, nRuleIndex) {
							if (sTargets.indexOf(oDataDependency.Target) > -1) { processDependency(oDataDependency, oTemplateEntry, 1, oTarget); }
						});
					}
				});
			}
			return;
		}

		function processDependency(oDataDependency, oFocusEntry, nContext) {
			var isDataTrigger = (nContext === 1) ? true : false, Source = 'null', oFormData = oFocusEntry.FormData, lCondition = false;

			try {
				lCondition = eval(oDataDependency.Condition);

				// process true action if not null
				if ( (lCondition) && (oDataDependency.TrueAction) ) {
					processAction(oDataDependency.TrueAction, oDataDependency.Target);
				}

				if ( (!lCondition) && (oDataDependency.FalseAction) ) {
					processAction(oDataDependency.FalseAction, oDataDependency.Target);
				}
			} catch (oError) { oError.display('processDependency()'); }
		}

		function processAction(sAction, sTargetIdList) {
			var aTargets = sTargetIdList.split(','), oTarget = null;
			var lEnable = (sAction === 'Enable') ? true : false;

			aTargets.forEach( function(sTargetId, nIndex) {
				oTarget = document.getElementById(sTargetId);
				if (oTarget) { UIManager.switchObjectState(oTarget, lEnable); }
			});
		}

		/*
		function processAction(sAction, sTargetId) {
			var oActionOnTarget = (sTargetId) ? document.getElementById(sTargetId) : null;

			if (oActionOnTarget) {
				switch(sAction) {
					case 'Enable': case 'Disable':
						UIManager.switchObjectState(oActionOnTarget, ((sAction === 'Enable') ? true : false));
						break;
					case 'Clear':
						myDebug('processAction()', sTargetId + ' ' + sAction);
						break;
				}
			}
		}
		*/
	}

	function jwfInputFocus_old(oTarget) {
		var sTarget = oTarget.id, lLookupField = UIManager.isComboBox(oTarget), lDisposeCombo = false;
		var oEditDataTab = UIManager.getObjectsByTarget(oTarget).EditDataTab, sText = '';

		if (oEditDataTab.MainEntity.ReadOnly) { return; }

		// detect if user has not completed combo selection and shifted to other field
		// (1) if not lookup field and combo box is still open? (2) Another lookup field but previous still open?
		if (!lLookupField && (Lookup.ActiveObject)) {
			lDisposeCombo = true;
		} else if (lLookupField && (Lookup.ActiveObject)) {
			lDisposeCombo = (Lookup.ActiveObject.Target.id !== sTarget);
		}

		// dispose uncompleted combo and continue .. allow next combo to open
		if (lDisposeCombo) {
			Lookup.ComboBox.dispose(false);
		}

		// All normal (a) if lookup field display combo, then enter focus (b) else do nothing allow focus into field
		if (UIManager.isComboBox(oTarget)) {
			sText = oTarget.value;
			oTarget.setSelectionRange(sText.length, sText.length);
			//Lookup.ComboBox.promote(oTarget);
		}
	}
}

EditDataTab.addMandatoryValidations = function (sTabId, sTmplKey, aValidationsCodes) {
	var oEditDataTab = null, aMandatoryValidations = null;

	if (!Array.isArray(aValidationsCodes)) { return; }

	try {
		oEditDataTab = PageCtrl.getObjectById(sTabId);
		aMandatoryValidations = oEditDataTab.DataTemplate[sTmplKey].MandatoryValidations;

		aValidationsCodes.forEach( function(sCode, nIndex) {
			if (aMandatoryValidations.indexOf(sCode) < 0) { aMandatoryValidations.push(sCode); }
		});

	} catch(oError) {
		oError.display("Add Mandatory Validations");
	}
}

EditDataTab.removeMandatoryValidations = function (sTabId, sTmplKey, aValidationsCodes) {
	var oEditDataTab = null, aMandatoryValidations = null, nFoundIndex = -1;

	if (!Array.isArray(aValidationsCodes)) { return; }

	try {
		oEditDataTab = PageCtrl.getObjectById(sTabId);
		aMandatoryValidations = oEditDataTab.DataTemplate[sTmplKey].MandatoryValidations;

		aValidationsCodes.forEach( function(sCode, nIndex) {
			nFoundIndex = aMandatoryValidations.indexOf(sCode);
			if (nFoundIndex > -1) { aMandatoryValidations.splice(nFoundIndex, 1); }
		});
	} catch(oError) {
		oError.display("Remove Mandatory Validations");
	}
}

// All data navigation grid clicks handled here
EditDataTab.jwfDataGridClick = function (oDataGrid, oPreviousRow, oClickedRow) {
	var sEditTabId = UTIL.getDataAttributes(oDataGrid.GridDomObj.getAttribute('data-attributes'))['edit-tab'];
	var oEditTab = PageCtrl.getObjectById(sEditTabId), oTemplateEntry = UIManager.getTemplateByGrid(oDataGrid.GridId), lSameRowClicked = false;

	if ((oEditTab.GridClick) && (oDataGrid.hasRows())) {
		//oPreviousRow = (oDataGrid.SelectedIndex) ? oDataGrid.GridDomObj.rows[oDataGrid.SelectedIndex] : null;
		lSameRowClicked = (oPreviousRow) ? (oPreviousRow.rowIndex === oClickedRow.rowIndex) ? true : false : false;

		if (lSameRowClicked) {
			myDebug('Click of Same Row', 'Grid Click by passed');
		} else {
			oEditTab.GridClick(oDataGrid, oPreviousRow, oClickedRow);
		}
	}
	DataGrid.markRowSelected(oClickedRow);
}

/* Pradeep : 14-04-2017 added form focus event for all form elements */
EditDataTab.jwfFormFocus = function (oEvent) {
	var sFormId = $(oEvent.target).closest('form').attr('id');
	var oFormObjects = UIManager.getObjectsByTarget(oEvent.target);
	var oEditDataTab = oFormObjects.EditDataTab;

	if (oEditDataTab.FormFocus) { oEditDataTab.FormFocus(oEvent.target, sFormId, oFormObjects.TemplateEntry); }
}

// All form change events handled here
EditDataTab.jwfFormChange = function (oTarget, lChangeOnSelection) {
   var sFormId = '', oAttributes = null, oEditTab = null; oTemplateEntry = null, lInputValid = true, oColumnInfo = null;
   var lIsDateField = (oTarget.getAttribute('data-xtype') === 'date') ? true : false;
   var aSelectedLookupRecords = [];

   try {
      if (lIsDateField) { if (!UIManager.isDateInputValid(oTarget)) { return; } }         // igonre if base date value invalid, after valid process rest logic
      if (!oTarget.id) { return; }                                                  // ignore if controls do not have id (for all DB linked fields id is must)
      if ((UIManager.isChangeOnSelection(oTarget)) && (!lChangeOnSelection)) { return; }  // ignore jquery change event on user typing for combo fields

		aSelectedLookupRecords = (Lookup.ActiveObject) ? Lookup.getSelectedDataRecords() : [];

		// else get more information about field
		sFormId = $(oTarget).closest('form').attr('id');
		oAttributes = UTIL.getDataAttributes($(oTarget).closest('form').attr('data-attributes'));

		oEditTab = PageCtrl.getObjectById(oAttributes['edit-tab']);
		oTemplateEntry = oEditTab.DataTemplate[oAttributes['template-key']];
		oColumnInfo = oTemplateEntry.ColumnsInfo[UTIL.indexOfRecord(oTemplateEntry.ColumnsInfo, 'id',oTarget.id)];
		oTemplateEntry.Target = oTarget;

		if (oEditTab.MainEntity.ReadOnly) { return; }                                   // if entity fetched as read only ignore all dta changes

		// Update JSON model from controls and then call component code with enhanced information to validate value
		UIManager.syncronizeModelWithForm(oTarget, oTemplateEntry);
		if (oEditTab.FormChange) {
			if (aSelectedLookupRecords.length > 0) { oEditTab.SelectedLookupRecords[oTarget.id] = JSON.parse( JSON.stringify(aSelectedLookupRecords) ); }
			EditDataTab.jwfProcessValidationResult(oTarget, oEditTab, oTemplateEntry, oEditTab.FormChange(oTarget, sFormId, oTemplateEntry, aSelectedLookupRecords));
		}

	} catch(oError) { oError.display("jwfFormChange()"); }
	return lInputValid;
}

// This will be default called by JWF form change event, but it can be called from component form change event
// if API call is involved to validate field value
EditDataTab.jwfProcessValidationResult = function(oTarget, oEditTab, oTemplateEntry, oValidationResult) {
	var lValidBeforeChange = oTemplateEntry.FormData.DataState.Valid, aPendingRules = oTemplateEntry.PendingValidations;
	var oValidation = (oValidationResult) ? (Array.isArray(oValidationResult.Failed)) ? PageCtrl.getDataValidation(oValidationResult.Failed[0]) : null : null;
	var sErrorTitleMsg = (oValidation) ? UTIL.getTokenizedMessage(oValidation.ErrorText, [oTarget.value]) : 'Kindly correct data values as per validation rules listed below.'

	// Fields with no validation then this will be null
	oValidationResult = (oValidationResult) ? (Object.keys(oValidationResult).length < 1) ? null : oValidationResult : null;

	// Steps for field with validation logic added
	if (oValidationResult) {
		aPendingRules = getUpdatedPendingValidations(oValidationResult, oTemplateEntry.PendingValidations);
		oTemplateEntry.FormData.DataState.Valid = (aPendingRules.length > 0) ? false : true;

		oEditTab.clearErrors(oTemplateEntry, false);
		if (!oTemplateEntry.FormData.DataState.Valid) { oEditTab.addErrors(oTemplateEntry, aPendingRules, sErrorTitleMsg); }
		oTemplateEntry.PendingValidations = aPendingRules;
		if ((oValidation) ? (oValidation.Setfocus) ? true : false : false) { oTarget.focus(); }
	}

	// If change over from Invalid record to valid by this field change? If yes copy data record to reset buffer as 'string'
	if ((!lValidBeforeChange) && (oTemplateEntry.FormData.DataState.Valid)) {
		oTemplateEntry.DataBuffer = JSON.stringify(oTemplateEntry.FormData);
		if (PageCtrl.EventCallBacks.onChangeFormDataState) { PageCtrl.EventCallBacks.onChangeFormDataState(oTemplateEntry, false, true); }
	}

	// Refresh grid always for any change of even single field, provided record is valid
	if ((oTemplateEntry.FormData.DataState.Valid) && (oTemplateEntry.GridId)) { UIManager.fillViewGrid(oTemplateEntry.GridId, oTemplateEntry.GridData, null, oTemplateEntry.FormData.RowId); }

	//oEditTab.executeDataDependency(oTemplateEntry, 1, oTarget);
	UIManager.fillDataForm(oTemplateEntry.LogicalFormId, oTemplateEntry.FormData, oTemplateEntry.ColumnsInfo);

	if ((lValidBeforeChange) && (!oTemplateEntry.FormData.DataState.Valid)) {
		if (PageCtrl.EventCallBacks.onChangeFormDataState) { PageCtrl.EventCallBacks.onChangeFormDataState(oTemplateEntry, true, false); }
	}

	function getUpdatedPendingValidations(oValidationResult, aPendindValidations) {
		var aPendingRules = (Array.isArray(aPendindValidations)) ? aPendindValidations.slice(0) : [],
			aPassed = (Array.isArray(oValidationResult.Passed)) ? oValidationResult.Passed : [],
			aFailed = (Array.isArray(oValidationResult.Failed)) ? oValidationResult.Failed : [],
			nIndexFound = 0;

		aPassed.forEach( function(sRuleCode, nRuleIndex) {
			nIndexFound = aPendingRules.indexOf(sRuleCode);
			if (nIndexFound > -1) { aPendingRules.splice(nIndexFound, 1); }
		});

		aFailed.forEach( function(sRuleCode, nRuleIndex) {
			nIndexFound = aPendingRules.indexOf(sRuleCode);
			if (nIndexFound < 0) { aPendingRules.push(sRuleCode); }
		});

		return aPendingRules;
	}
}

EditDataTab.jwfDataGridChange = function(oEvent) {
	var sGridId = $('#' + oEvent.target.id).closest('table').attr('id');
	var oTemplateEntry = UIManager.getTemplateByGrid(sGridId);

	var nRowId = parseInt(oEvent.target.id.split('_')[0]), sFieldName = oEvent.target.id.split('_')[1];
	var nRowIndex = UTIL.indexOfRecord(oTemplateEntry.GridData, 'RowId', nRowId);

	var oDataRecord = oTemplateEntry.GridData[nRowIndex];

	if (PageCtrl.getObjectById(PageCtrl.DefaultTabId).MainEntity.ReadOnly) { return; }

	oDataRecord[sFieldName] = oEvent.target.value;
	if (oDataRecord.DataState.DBAction === jwfGlobal.DATA_STATE_NO_CHANGE) {
		oDataRecord.DataState.DBAction = (oDataRecord.RowId < 0) ? jwfGlobal.DATA_STATE_INSERT : jwfGlobal.DATA_STATE_UPDATE;
		oDataRecord.RowId = (oDataRecord.DataState.DBAction === jwfGlobal.DATA_STATE_INSERT) ? parseInt(UTIL.getUniqueId()) : oDataRecord.RowId;
		oDataRecord.DataState.Valid = true;
	}
	UIManager.loadDataGrid(oTemplateEntry.GridData, sGridId);
	PageCtrl.getObjectById(PageCtrl.DefaultTabId).setDirtyRecordCount();
}

/**************************************************************************
APIDataService: Wrapper function over jQuery Ajax for all API calls
***************************************************************************/
function ApiDataService(oApiInput, cbComplete, nEventId, oEventInfo) {
	var strApiData = '', sWaitMsg = '', nTimeOut = 80000;

	oApiInput.ClientSessionInfo = PageCtrl.getObjectById('ClientSessionInfo');
	strApiData = JSON.stringify(oApiInput);

	/* Pradeep: 15/02/2018 - Only for save entity graph api call is not allowed multiple times */
	if (PageCtrl.ApiCallActive) {
		PageCtrl.debug.log('Warning', 'Alt-S key press problem, save call fired multiple times, it is ingored until previous completes');
		return;
	} else if (nEventId === jwfGlobal.API_EVT_SAVE_ENTITY_GRAPH) {
		PageCtrl.ApiCallActive = true;
	}

	PageCtrl.ApiStartTime = new Date().getTime();

	if (!oEventInfo) { oEventInfo = {}; }
	if (!nEventId) { nEventId = -999; }
	sWaitMsg = (isJwfApiEvent(nEventId) < 0) ? oEventInfo.CustomMsg : getApiDefaultMsg(nEventId);
	if (sWaitMsg) { Modal.waitDialog(sWaitMsg) } else { Overlay.display(true); };
	nTimeOut = (oEventInfo.TimeOut) ? (oEventInfo.TimeOut * 1000) : nTimeOut;

	$.ajax({
		url : '/api/jwfApiDataService/Main',
		type: 'POST',
		contentType: 'application/json',
		processData: false,
		data : strApiData,
		timeout: nTimeOut,
		success: function(data, textStatus, jqXHR) {
			oEventInfo.status = jqXHR.status;
			jwfApiCallComplete();
			jwfApiCallBack(1, data, nEventId, oEventInfo)
		},
		error: function (jqXHR, textStatus, errorThrown) {
			oEventInfo.status = jqXHR.status;
			oEventInfo.textStatus = textStatus;
			PageCtrl.debug.log('Server call error','Failed with code "{textstatus}"'.replace('{textstatus}', textStatus + ' ' + jqXHR.status));
			jwfApiCallComplete();
			jwfApiCallBack(-1, null, nEventId, oEventInfo)
		}
		/*,
		complete: function (jqXHR, textStatus) {
			if (sWaitMsg) { Modal.close(); } else { Overlay.hide(); }
			//PageCtrl.ApiCallActive = false;   // Allow another API call
			myDebug('api duration', (new Date().getTime() - PageCtrl.ApiStartTime) / 1000);
		}*/
	});

	function jwfApiCallComplete() {
		if (sWaitMsg) { Modal.close(); } else { Overlay.hide(); }
		PageCtrl.ApiCallActive = false;   // Allow another API call
		myDebug('api duration', (new Date().getTime() - PageCtrl.ApiStartTime) / 1000);
	}

	function jwfApiCallBack(nResult, oApiResponse, nEvtId, oEvtInfo) {
		var nApiLogicalResult = null, aSBViewList = [], lJwfApiCall = (isJwfApiEvent(nEvtId) > -1) ? true : false;
		var lClientSideFailure = (nResult === -1) ? true : false, lSessionExpired = false;

		// Handle technical failure (i.e. API call could not reach server)
		if ((lClientSideFailure) && (lJwfApiCall)) {
			PageCtrl.appMessage(jwfGlobal.API_STD_EXCEPTION_MSG, PageCtrl.debug.debugLog);
			return;
		} else if ((lClientSideFailure) && (!lJwfApiCall)) {
			cbComplete(nResult, oApiResponse, nEvtId, oEvtInfo);
			return;
		}

		// Handle technical success Ajax call reached server (but logical errors may be there to be handled by call back functions)
		nApiLogicalResult = oApiResponse.Result;
		lSessionExpired = (nApiLogicalResult === -99) ? true : false;

		if (lSessionExpired) {
			//PageCtrl.appMessage("Your login session is expired. Kindly re-login in application",null, false, PageCtrl.ClosePage);
			alert("Your login session is expired. Kindly re-login in application");
			window.top.location.replace(jwfGlobal.WEBTRANS_LOGIN_PAGE);
			return;
		// Non JWF call success
		} else if (isJwfApiEvent(nEvtId) < 0) {
			if (nApiLogicalResult !== 1) { myDebug("User Api Service Failed", 'Event Id ' + nEvtId + ' failed with error '); }
			cbComplete(nResult, oApiResponse, nEvtId, oEvtInfo);

		// JWF API call success
		} else {
			if (nApiLogicalResult !== 1) {
				PageCtrl.appMessage(jwfGlobal.API_STD_EXCEPTION_MSG, oApiResponse.ErrorMsg);
				//myDebug("JWF Api Service Failed", 'Event Id ' + nEvtId + ' failed with error ' + JSON.stringify(oApiResponse));
				nEvtId = -99;
			}

			// JWF events handle predefined way
			switch(nEvtId) {
				/*
				case jwfGlobal.API_EVT_SEED_DATA:
					PageCtrl.setSeedData(oApiResponse);
					break;
				case jwfGlobal.API_EVT_SBSEARCH_CLICK:
					aSBViewList = JSON.parse(oApiResponse.dataOutPacket[0]);
					PageCtrl.getObjectById(PageCtrl.SBGridId).DataList = aSBViewList;
					UIManager.fillViewGrid(PageCtrl.SBGridId, aSBViewList);
					break;
				*/
				case jwfGlobal.API_EVT_SBANKER_CLICK:
					PageCtrl.getObjectById(PageCtrl.DefaultTabId).MainEntity = JSON.parse(oApiResponse.dataOutPacket[0], JSON.dateParser);
					if (oApiResponse.dataOutPacket.length > 1) {
						PageCtrl.getObjectById(PageCtrl.DefaultTabId).SubEntity = JSON.parse(oApiResponse.dataOutPacket[1], JSON.dateParser);
					}
					PageCtrl.LoadEntityGraph();

					break;
				case jwfGlobal.API_EVT_SAVE_ENTITY_GRAPH:
					if (oEvtInfo.EditDataTab.EntitySaveResponse) {
						oEvtInfo.EditDataTab.EntitySaveResponse(oApiResponse);
					} else {
						PageCtrl.appMessage(PageCtrl.ComponentName + ' Save Status', oApiResponse.dataOutPacket[0]);
						PageCtrl.getObjectById(PageCtrl.DefaultTabId).MainEntity = JSON.parse(oApiResponse.dataOutPacket[1], JSON.dateParser);
						PageCtrl.LoadEntityGraph();
					}
					break;
				case jwfGlobal.API_EVT_LOOKUP_DATA:
					Lookup.gotData(JSON.parse(oApiResponse.dataOutPacket[0]), Lookup.ActiveObject.Type, oEvtInfo);
			}
		}
	}

	function isJwfApiEvent(nApiEventId) {
		var aJwfEvents = [ jwfGlobal.API_EVT_SEED_DATA, jwfGlobal.API_EVT_SBSEARCH_CLICK,
				jwfGlobal.API_EVT_SBANKER_CLICK, jwfGlobal.API_EVT_SAVE_ENTITY_GRAPH, jwfGlobal.API_EVT_LOOKUP_DATA ];
		return aJwfEvents.indexOf(nApiEventId);
	}

	function getApiDefaultMsg(nApiEventId) {
		var sMsg = '';
		switch(nApiEventId) {
			case jwfGlobal.API_EVT_SAVE_ENTITY_GRAPH:
				sMsg = 'Please wait ... Saving Data ...';
				break;
			case jwfGlobal.API_EVT_LOOKUP_DATA:
				sMsg = null;
				break;
			default:
				sMsg = 'Please wait .. Loading Data ...';
		}
		return sMsg;
	}
}

function JwfAjaxWrapper(oApiCallParameters) {
	var lShowWaitDialog = false;

	if (oApiCallParameters.WaitMsg) {
		lShowWaitDialog = (oApiCallParameters.WaitMsg.length > 0) ? true : false;
	} else {
		lShowWaitDialog = false;
	}

	if (lShowWaitDialog) { Modal.waitDialog(oApiCallParameters.WaitMsg); }
	$.ajax({
		type: 'POST',
		url: oApiCallParameters.Url,
		data: oApiCallParameters.Data,
		headers:{'token':oApiCallParameters.Headers},
		datatype: 'json',
		success: function(sResponse) {
			if (lShowWaitDialog) { Modal.close(); }
			ApiCallComplete(1, sResponse, oApiCallParameters);
		},
		error: function(sMessage) {
			if (lShowWaitDialog) { Modal.close(); }
			ApiCallComplete(-1, sMessage, oApiCallParameters);
		}
	});

	function ApiCallComplete(nStatus, sResponse, oApiCallParameters) {
		var oResponse = {}, oCallBackFunction = oApiCallParameters.CallBackFunction;

		/* Sanitise response for component code mistakes */
		sResponse = ( (sResponse === '') || (!sResponse) ) ? '{}' : sResponse;

		if (nStatus < 0) {
			toastr.info("Error occured contacting server application\n'{msg}'".replace('{msg}',sResponse));
		} else {
			oResponse = JSON.parse(sResponse);
			oCallBackFunction(oResponse);
		}
	}
}


/**************************************************************************
Overlay & Modal: Dialog management and other classes
***************************************************************************/
var Overlay = {
	Overlay: [],
	ParentNode: null,
	Count: -1,
	Zindex: 99,
	RaisedElements: [],
	display: function(lLowOpacity, aDomElements) {
		var oOverlayDiv = document.createElement('DIV');

		document.body.appendChild(oOverlayDiv);

		this.Count = this.Count + 1;
		this.Zindex = this.Zindex + 1;
		oOverlayDiv.style.zIndex = this.Zindex.toString();
		oOverlayDiv.classList.add('jwf-overlay');
		oOverlayDiv.style.display = 'block';
		this.Overlay.push(oOverlayDiv);

		lLowOpacity = (lLowOpacity) ? lLowOpacity: false;
		oOverlayDiv.style.opacity = (lLowOpacity) ? '0.1' : '0.5';
		oOverlayDiv.style.backgroundColor = (lLowOpacity) ? '#f2f2f2' : 'black';

		if (Array.isArray(aDomElements)) {
			//this.RaisedElements.push(aDomElements);
			aDomElements.forEach(function(oElement, nIndex) {
				//Overlay.RaisedElements.push(oElement);
				oElement.style.zIndex = '300';
			});
		}
	},
	hide: function(aDomElements) {
		var oOverlaydiv = this.Overlay[this.Count], aDomElements = Array.isArray(aDomElements) ? aDomElements : [];

		document.body.removeChild(oOverlaydiv);
		this.Overlay.splice(-1);
		//this.RaisedElements.splice(-1);
		this.Count = this.Count - 1;
		this.Zindex = this.Zindex - 1;

		aDomElements.forEach(function(oElement, nIndex) {
			oElement.style.zIndex = '0';
		});
	}
};

var Modal = {
	Dialog: null,
	Body: null,
	Header: null,
	Footer: null,
	UserParentNode: null,
	UserDomNode: null,
	CallBackOptions: {'ondialogload': null, 'ondialogclose': null},
	EventInfo: { 'DialogID': null, 'EventId': null, 'ClickedButton': null },
	setup: function() {
		var oImgBtn = null;
		this.Dialog = document.getElementById("jwf-modal-container");
		this.Body = document.getElementById("jwf-modal-body");
		this.Header = document.getElementById("jwf-modal-header");
		this.Footer = document.getElementById("jwf-modal-footer");
		this.ParentNode = this.Dialog.parentNode;
		oImgBtn = UIManager.getChildNodes(this.Dialog, 'IMG')[0];
		$(oImgBtn).on('click', function() { Modal.onButtonClick(oImgBtn); });
	},
	waitDialog: function(sMsgText) {
 		this.Dialog.setAttribute('data-xtype', 'wait-dialog');
		this.Body.innerHTML = "<p style='background-color: white; font-size: 16px;'><img style='margin-right: 20px;' src='./assets/global/img/loading-circle.gif'>Please wait, {msg} ...</p>".replace('{msg}',sMsgText);

		//this.Body.innerHTML = "<p>{msg}</p>".replace("{msg}",sMsgText);

		Overlay.display();
		this.display();
	},
	confirmDialog: function(nDialogId, sDialogTitle, sMsgText, aDialogButtons, oCallBackOptions) {
		UIManager.getChildNodes(this.Dialog, 'P')[0].innerHTML = sDialogTitle;
 		this.Dialog.setAttribute('data-xtype', 'confirm-dialog');
		this.Body.innerHTML = '<p>' + sMsgText + '</p>';
		this.CallBackOptions = oCallBackOptions;
		this.addButtons(aDialogButtons, this);
		this.EventInfo = { 'DialogID': nDialogId, 'EventId': jwfGlobal.EVT_MODAL_DIALOG_ONLOAD };

		Overlay.display();
		this.display();
	},
	customDialog: function(nDialogId, sDialogTitle, oHtmlContent, aDialogButtons, oCallBackOptions, oOptions) {
		UIManager.getChildNodes(this.Dialog, 'P')[0].innerHTML = sDialogTitle;
 		this.Dialog.setAttribute('data-xtype', 'custom-dialog');
 		this.UserParentNode = oHtmlContent.BodyDomObj.parentNode;   // get original parent node of user's contents
 		this.UserDomNode = oHtmlContent.BodyDomObj;                 // get object we are inserting into body node
		this.Body.appendChild(oHtmlContent.BodyDomObj);
		this.CallBackOptions = oCallBackOptions;
		this.EventInfo = { 'DialogID': nDialogId, 'EventId': jwfGlobal.EVT_MODAL_DIALOG_ONLOAD };

		if (oHtmlContent.HeaderHtml) { this.Header.innerHTML = oHtmlContent.HeaderHtml; } else { this.Header.innerHTML = ''; }  // this.Header.style.display = 'none';
		this.addButtons(aDialogButtons, this);

		Overlay.display();
		this.display();
		if (this.CallBackOptions.onDialogLoad) { this.CallBackOptions.onDialogLoad(this.EventInfo); }
	},
	addButtons: function(aDialogButtons, oDialog) {
		oDialog.Footer.innerHTML = '';

		aDialogButtons = (Array.isArray(aDialogButtons)) ? aDialogButtons : [ 'Ok'];
		aDialogButtons.forEach(function(sButton, nIndex) {
			var sButtonHtml = '<span id="modal-button-%id%" onclick="Modal.onButtonClick(this)" class="abutton">%id%</span>';
			while (sButtonHtml.indexOf('%id%') > -1) { sButtonHtml = sButtonHtml.replace("%id%", sButton); }
			oDialog.Footer.innerHTML = oDialog.Footer.innerHTML + sButtonHtml;
		});
	},
	onButtonClick: function(oTarget) {
		var sClickedButton = (oTarget.tagName === 'IMG') ? 'Cancel' : oTarget.id.replace('modal-button-','');
		this.EventInfo.EventId = jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE;
		this.EventInfo.ClickedButton = sClickedButton;
		
		if (this.CallBackOptions) {
			if (this.CallBackOptions.onDialogClose) {
				var status = this.CallBackOptions.onDialogClose(this.EventInfo);
				
				if(status != 'data_validation_failed'){
					Modal.close();
				}
				
			}
		} else {
			Modal.close();
		}
	},
	close: function() {
		Overlay.hide();
		this.Dialog.style.display = 'none';
		if (this.Dialog.getAttribute('data-xtype') === 'custom-dialog') {
			this.UserParentNode.appendChild( this.UserDomNode );             // restore user's content's to orginal parent
		}
		this.Body.innerHTML = '';
	},
	display: function() {
		var nDlgX = 0, nDlgY = 0, nTop = 0, nLeft = 0, $window = $(window);

		document.body.appendChild(this.Dialog);
		nDlgX = $(this.Dialog).width();
		nDlgY = $(this.Dialog).height();
		nTop = Math.round(($window.height() - nDlgY)/2);
		nLeft = Math.round(($window.width() - nDlgX)/2);

		nTop = 85;

		this.Dialog.style.display = 'block';
		this.Dialog.style.top = nTop.toString() + 'px';
		this.Dialog.style.left = nLeft.toString() + 'px';
	}
}

function jwfModalCallBacks (oEvent) {
	var nDialogId = oEvent.DialogId, sEventId = oEvent.EventId;

	switch(sEventId) {
		case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
			break;
		case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
			break;
	}
}

/**************************************************************************
UIManager: Worker class to handle all GUI related updates / handling
***************************************************************************/
var UIManager = new (function() {
	var aDataPanels = {};

	this.setupDataPanels = function () {
		var aDataTitles = document.getElementsByClassName(jwfGlobal.CSS_CLASS_DATA_PANEL);
		var nNoOfTitles = aDataTitles.length, sDataTitleId = '', sDisplay = '';
		var oDataDiv = null, oCtrlSpan = null, oTarget = null, oAttributes = {};

		/* Scan all data titles on page */
		for (i = 0; i < nNoOfTitles; i++) {
			var oDataTitle = {};

			oDataDiv = aDataTitles[i];
			oAttributes = UTIL.getDataAttributes(oDataDiv.getAttribute('data-attributes'));
			oCtrlSpan = UIManager.getChildNodes(oDataDiv, 'SPAN')[0];

			sDataTitleId = 'datapanel' + i.toString();
			oDataTitle.id = sDataTitleId;

			// Assign id to td object dynamically so click event can retrieve data
			oCtrlSpan.id = sDataTitleId;

			oDataTitle.OrgText = oCtrlSpan.innerHTML;
			oDataTitle.Collapsible = JSON.parse(oAttributes.collapsible);

			if (oDataTitle.Collapsible === true) {
				oDataTitle.TargetId = oAttributes.target;
				oDataTitle.TargetPanel = document.getElementById(oAttributes.target);

				oTarget = oDataTitle.TargetPanel;
				sDisplay = oTarget.style.display;

				/*	oCtrlSpan.innerHTML = (sDisplay === 'none') ? jwfGlobal.PANEL_EXPAND_SYMBOL + oDataTitle.OrgText : jwfGlobal.PANEL_COLLAPSE_SYMBOL + oDataTitle.OrgText; */
				oCtrlSpan.querySelector('label').innerHTML = (sDisplay === 'none') ? jwfGlobal.PANEL_EXPAND_SYMBOL : jwfGlobal.PANEL_COLLAPSE_SYMBOL;
				if (sDisplay === 'none') { oCtrlSpan.parentNode.classList.add(jwfGlobal.CSS_CLASS_PANEL_COLLAPSE); }

				oCtrlSpan.addEventListener("click", dataPanelClick);
			}

			// All set add object to global variable
			aDataPanels[sDataTitleId] = oDataTitle;
		}

		function dataPanelClick(Evt) {
			var oTarget = (Evt.target.tagName === 'LABEL') ? Evt.target.parentNode : Evt.target;
			var oDataPanel = aDataPanels[oTarget.id];
			var oTargetPanel = oDataPanel.TargetPanel;
			var sDisplay = (oTargetPanel.style.display === 'none') ? 'block' : 'none';

			var sOldText = oTarget.innerHTML.substring(2);
			//Evt.target.innerHTML = (sDisplay === 'none') ? '&#9658; ' + oDataPanel.OrgText : '&#9650; ' + oDataPanel.OrgText;
			//Evt.target.innerHTML = (sDisplay === 'none') ? '&#9658; ' + sOldText : '&#9650; ' + sOldText;
			oTarget.querySelector('label').innerHTML = (sDisplay === 'none') ? jwfGlobal.PANEL_EXPAND_SYMBOL : jwfGlobal.PANEL_COLLAPSE_SYMBOL;
			oTargetPanel.style.display = sDisplay;
			oTarget.parentNode.classList.toggle(jwfGlobal.CSS_CLASS_PANEL_COLLAPSE);
			Lookup.ComboBox.onResize();
		}
	}
	this.fillViewGrid = function(sGridId, aDataList, oDataFilter, nRefreshRowId) {
		var oDataGrid = PageCtrl.getObjectById(sGridId);
		var oGridHeader = oDataGrid.GridDomObj.tHead, oGridBody = oDataGrid.GridDomObj.tBodies[0];
		var nGridType = oDataGrid.GridType, aColumnInfo = oDataGrid.ColumnsInfo

		var lRefreshRow = (nRefreshRowId) ? true : false, nDataIndex = -1, nRowIndex = -1, nNoOfRecords = 0;
		var lApplyFilter = ((oDataFilter) && (!lRefreshRow)) ? true : false, lFilterPassed = false, lDeleteFilter = false;

		var oRowToRefresh = null, oRowToSelect = null;

		// Abort #1 - if input is not array then exit
		if (!Array.isArray(aDataList)) {
			myDebug("fillViewGridRow() Debug 00", "Input data is not array of object, grid data filling aborted!");
			return;
		}

		// Abort #2 - if row to refresh not found in data list then exit
		if (lRefreshRow) {
			nDataIndex = UTIL.indexOfRecord(aDataList, 'RowId', nRefreshRowId);
			if (nDataIndex < 0) {
				myDebug("fillViewGridRow() Debug 01", "New or existing data record not found in data list, row refresh aborted!");
				return;
			} else {
				nRowIndex = UTIL.indexOfGridRow(oDataGrid.GridDomObj, 0, nRefreshRowId.toString());
				oRowToRefresh = (nRowIndex < 0) ? oGridBody.insertRow(-1) : oDataGrid.GridDomObj.rows[nRowIndex];
				fillGridRow(oRowToRefresh, aDataList[nDataIndex], aColumnInfo);
				oRowToSelect = oRowToRefresh;
			}
			//DataGrid.markRowSelected(oRowToSelect);
			oDataGrid.jwfGridClick(oRowToSelect);
		} else {
			nNoOfRecords = aDataList.length;
			oDataGrid.clearRows();

			lDeleteFilter = (nNoOfRecords > 0) ? (Object.keys(aDataList[0]).indexOf('DataState') > -1) ? true : false : false;

			for (var i = 0; i < nNoOfRecords; i++) {
				lFilterPassed = (lDeleteFilter) ? (aDataList[i].DataState.DBAction === jwfGlobal.DATA_STATE_DELETE) ? false : true : true;
				lFilterPassed = (!lFilterPassed) ? false : (lApplyFilter) ? checkDataFilter(oDataFilter, aDataList[i]) : true;

				if (lFilterPassed) {
					var oNewRow = oGridBody.insertRow(-1);
					fillGridRow(oNewRow, aDataList[i], aColumnInfo);
					oRowToSelect = (oNewRow.rowIndex === 1) ? oNewRow : oRowToSelect;
				}
			}
			if (oRowToSelect) { oDataGrid.jwfGridClick(oRowToSelect); } //	DataGrid.markRowSelected(oRowToSelect);
		}
		return;

		// closure function to check data filter passed or not
		function checkDataFilter(oDataFilter, oDataRecord) {
			var aKeys = oDataFilter.Key.split('.');
			var sDataValue = (aKeys.length > 1) ? oDataRecord[aKeys[0]][aKeys[1]] : oDataRecord[oDataFilter.Key];
			var sFilterValue = oDataFilter.Value, lFilterMatch = false;

			if ((typeof sDataValue === 'string') && (typeof sFilterValue === 'string')) {
				sDataValue = sDataValue.toUpperCase();
				sFilterValue = sFilterValue.toUpperCase();
				if (sDataValue.indexOf(sFilterValue) > -1) { lFilterMatch = true; }
			}
			return lFilterMatch;
		}

		// closure function to fill row either new or existing
		function fillGridRow(oGridRow, oDataRecord, aColumnsInfo) {
			var nNoOfColumns = aColumnInfo.length, sFieldType = '', sFieldName = '', oFieldValue = {};
			var sRowId = '', nAnkerPos = 0, sAnker = '', lNewRow = (oGridRow.cells.length < 1) ? true : false;

			// id, domObject, xtype, attributes
			for (var j = 0; j < nNoOfColumns; j++) {
				var oGridCell = (lNewRow) ? oGridRow.insertCell(-1) : oGridRow.cells[j];

				// For view grid below are mandatory atrributes
				sFieldType = aColumnInfo[j].xtype.toLowerCase();
				sFieldName = aColumnInfo[j].attributes.fieldname;

				// Anker reconized only for browse grid and field type as text
				nAnkerPos = ((nGridType === jwfGlobal.GRID_TYPE_BROWSE) && (sFieldType === 'text')) ? sFieldName.indexOf('<a>') : -1;
				sFieldName = (nAnkerPos > 0) ? sFieldName.substr(0, nAnkerPos) : sFieldName;

				// Field name normaliozed get value, may be object or string or number or date
				oFieldValue = oDataRecord[sFieldName];

				switch (sFieldType) {
					case 'multi-select':
						oGridCell.innerHTML = '<input type="checkbox" name="Selected-RowIds" value="%value%">'.replace('%value%', oFieldValue);
						oGridCell.querySelector('input[type=checkbox]').addEventListener('click', function(oEvent) {
								var oRow = $(oEvent.target).closest('tr')[0];
								Lookup.Dialog.onSelectRow(oRow, oEvent.target);
							}, false);
						break;
					case 'data-state':
						oGridCell.innerHTML = UTIL.getDataStateAsSymbol(oFieldValue.DBAction);
						break;
					case 'text':
					case 'float':
					case 'integer':
						if (nAnkerPos > 0) {
							sRowId = oDataRecord.RowId.toString();      // Predefined each record must have it
							sAnker = '<a href="#" id="%rowid%">%value%</a>';
							sAnker = sAnker.replace("%rowid%", sRowId);
							sAnker = sAnker.replace("%value%", oFieldValue);
							oGridCell.innerHTML = sAnker;

							document.getElementById(sRowId).addEventListener("click", function () { PageCtrl.SearchBrowseAnkerClick(this.id); });
						} else {
							oGridCell.innerHTML = oFieldValue;
						}
						break;

					case 'date':
						//if (oFieldValue) { oFieldValue = new Date(oFieldValue.substr(0, oFieldValue.indexOf("T"))).toLocaleDateString(); }
						if (oFieldValue) { oFieldValue = UTIL.dateToInput(oFieldValue); }
						else { oFieldValue = ""; }
						oGridCell.innerHTML = oFieldValue;
						break;

					case 'boolean':
						oGridCell.innerHTML = (parseInt(oFieldValue) === 1) ? 'Yes' : 'No';
						break;

					case 'literal-lookup':
						oGridCell.innerHTML = oFieldValue.Text;   // field value must be object (Code,Text)
						break;
				}
			}
			oGridRow.tabIndex = 0;
			return;
		}
	}

	this.loadDataGrid = function(oDataList, sGridId) {
		var oGridHeader = document.getElementById(sGridId).tHead;
		var oGridBody = document.getElementById(sGridId).tBodies[0];
		var nNoOfRecords = 0, aHeadCells = [], i = 0, j = 0, sRowId = "", nSeedTabOrder = 500;
		var nNoOfColumns = 0, sFieldDefn = "", aFieldAttributes = [];
		var sFieldName = "", sFieldType = "", sDataStatus = "", oFieldValue = "";

		// Input must be array even if empty it is ok
		if (UTIL.isArray(oDataList)=== false) {
			myDebug("loadDataGrid() Debug 00", "Input data is not array grid data load aborted");
			return;
		}

		try {

		$('#' + sGridId).find("tr:not(:first)").remove();
		nNoOfRecords = oDataList.length;

		// Non empty list - main logic begins
		aHeadCells = oGridHeader.rows[0].cells;
		nNoOfColumns = aHeadCells.length;

		// Using var for objected needed else data loading do not happen properly
		for (i = 0; i < nNoOfRecords; i++) {
			var oNewRow = oGridBody.insertRow(-1);

			sRowId = oDataList[i].RowId;          // Predefined each record must have it
			sDataState = oDataList[i].DataState;  // Predefined each record must have it

			// literal-lookup, text, lookup, select, checkbox
			for (j = 0; j < nNoOfColumns; j++) {
				var oNewCell = oNewRow.insertCell(-1);
				var sTextHtml = '';

				/* skip 1st column as it will be data status (+, *, X or space)
				if (j === 0) {
					oNewCell.innerHTML = sDataState;          // testing hardcoded
					continue;
				} */

				sFieldDefn = aHeadCells[j].getAttribute('data-field');
				aFieldAttributes = sFieldDefn.split(',');
				sFieldName = aFieldAttributes[0].trim();
				sFieldType = aFieldAttributes[1].trim().toUpperCase();
				oFieldValue = oDataList[i][sFieldName];

				switch (sFieldType) {
					case 'DATASTATE':
						oFieldValue = oDataList[i].DataState.DBAction;
						oNewCell.innerHTML = UTIL.getDataStateAsSymbol(oFieldValue);
						break;
					case 'LITERAL-LOOKUP':
						oNewCell.innerHTML = oFieldValue.Text;
						break;
					case 'LOOKUP':
						oNewCell.innerHTML = '<input type="text" class="another lookup" id="' + sFieldName + '" value="' + oFieldValue.Text +'">';
						break;
					case 'TEXT':
						oFieldValue = (oFieldValue) ? oFieldValue : '';
						sTextHtml = '<input type="text" data-xtype="float" placeholder="nnn.nn" id="' + sRowId + '_' + sFieldName + '" value="' + oFieldValue +'">'
						oNewCell.innerHTML = sTextHtml;
						$('#' + sRowId + '_' + sFieldName).on('change', function(oEvent) { EditDataTab.jwfDataGridChange(oEvent); });
						$('#' + sRowId + '_' + sFieldName).on('keypress', UIManager.validKeys);
						$('#' + sRowId + '_' + sFieldName).attr('tabindex', nSeedTabOrder + i + 1);
						break;
				}
			}
		}

		} catch(oError) { oError.display("LoadDataGrid()"); }
	}

	this.disableFormState = function(sLogicalFormId) {
		var aColumnInfo = PageCtrl.ColumnsInfo[sLogicalFormId], lPreviousState = false;

		aColumnInfo.forEach( function(oColumnObject, nIndex) {
			lPreviousState = (oColumnObject.domObject.getAttribute('disabled')) ? false : true;
			oColumnObject.PreviousState = lPreviousState;
		});
		UIManager.switchFormState(sLogicalFormId, false, aColumnInfo);
	}

	this.restoreFormState = function(sLogicalFormId) {
		var aColumnInfo = PageCtrl.ColumnsInfo[sLogicalFormId];

		aColumnInfo.forEach( function(oColumnObject, nIndex) {
			UIManager.switchObjectState(oColumnObject.domObject, oColumnObject.PreviousState);
		});
	}

	this.switchFormState = function(sFormId, lSwitchOn, aColumnInfo) {
		var sFieldType = '', nNoOfObjects = (Array.isArray(aColumnInfo)) ? aColumnInfo.length : 0;

		for (var i = 0; i < nNoOfObjects; i++) {
			sFieldType = (aColumnInfo[i].xtype) ? aColumnInfo[i].xtype : aColumnInfo[i].domObject.type.toLowerCase();

			switch (sFieldType) {
				case 'text':
				case 'float':
				case 'integer':
				case 'textarea':
				case 'date':
				case 'checkbox':
				case 'local-combo':
				case 'select-one':
				case 'select-multiple':
					if (lSwitchOn) {
						aColumnInfo[i].domObject.removeAttribute('disabled');
					} else {
						aColumnInfo[i].domObject.setAttribute('disabled', 'disabled');
					}
					break;
				case 'remote-combo':
				case jwfGlobal.JWF_MULTI_SELECT:
					this.switchObjectState(aColumnInfo[i].domObject, lSwitchOn);
					break;
				default:
					break;
			}
		}
	}

	this.fillDataForm = function(sFormId, oFormData, aColumnInfo) {
		var nNoOfObjects = 0, sFieldType = '', sFieldName = '', oFieldValue = '', oDate = null, oDataAttributes = {};
		var aSelectOptions = [], oFormObjectCollection = null, lSetDataBuffer = false;
		var lEmptyData = (oFormData) ? (Object.keys(oFormData).length < 1) ? true : false : true;

		// quick fix if not empty form but it is deleted record then make it empty
		if (!lEmptyData) {
			if (oFormData.DataState.DBAction === jwfGlobal.DATA_STATE_DELETE) {
				oFormData = {};
				lEmptyData = true;
			}
		}

		// Catch errors locally
		try {
			this.switchFormState(sFormId, !lEmptyData, aColumnInfo);
			nNoOfObjects = aColumnInfo.length;
			oFormObjectCollection = (nNoOfObjects > 0) ? this.getObjectsByTarget(aColumnInfo[0].domObject) : null;

			for (i = 0; i < nNoOfObjects; i++) {
				sFieldType = (aColumnInfo[i].xtype !== null) ? aColumnInfo[i].xtype : aColumnInfo[i].domObject.type.toLowerCase();
				sFieldName = aColumnInfo[i].id;
				oDataAttributes = aColumnInfo[i].attributes;
				oFieldValue = (lEmptyData) ? null : oFormData[sFieldName];

				switch (sFieldType) {
					case 'text':
					case 'float':
					case 'integer':
					case 'textarea':
						aColumnInfo[i].domObject.value = (oFieldValue) ? oFieldValue : '';
						break;
					case 'date':
						aColumnInfo[i].domObject.value = UTIL.dateToInput(oFieldValue);
						break;
					case 'local-combo':
					case 'remote-combo':
						if (oFieldValue) {
							aColumnInfo[i].domObject.value = oFieldValue.Text;
							aColumnInfo[i].domObject.setAttribute('data-xvalue', oFieldValue.Code);
						} else {
							aColumnInfo[i].domObject.value = '';
							aColumnInfo[i].domObject.setAttribute('data-xvalue', '');
						}
						break;
					case 'select-one':
						aSelectOptions = (lEmptyData) ? [] : PageCtrl.getDataList(oDataAttributes['local-list']);
						UIManager.fillSelect(aColumnInfo[i].domObject, aSelectOptions, oFieldValue);
						break;
					case 'select-multiple':
					case jwfGlobal.JWF_MULTI_SELECT:
						UIManager.fillMultiSelect(aColumnInfo[i].domObject, oFormData, oDataAttributes['field-multi']);
						break;
					case 'checkbox':
						aColumnInfo[i].domObject.checked = oFieldValue;
						break;
				}
				//if (!lEmptyData) { this.checkDataDependency(aColumnInfo[i], oFormData); }
			}
			//if ((nNoOfObjects > 0) && (!lEmptyData)) {	this.fillDataLabels(oFormObjectCollection['TemplateEntry']); }
			this.fillDataLabels(oFormObjectCollection['TemplateEntry'], lEmptyData);

			// Copy loaded data record to buffer if it is valid (either new with valid data entered or fetched from DB)
			lSetDataBuffer = (lEmptyData) ? false : (oFormData.DataState.Valid);
			if (lSetDataBuffer) { oFormObjectCollection['TemplateEntry'].DataBuffer = JSON.stringify(oFormData); }

			oFormObjectCollection['EditDataTab'].setDataState();
			if (!lEmptyData) {
				oFormObjectCollection['EditDataTab'].executeDataDependency(oFormObjectCollection['TemplateEntry'], 0, null);
			} else {
				if (PageCtrl.EventCallBacks.afterEmptyFormLoad) { PageCtrl.EventCallBacks.afterEmptyFormLoad(sFormId); }
			}
		} catch(oError) { oError.display("fillDataForm()"); }
	}

	this.checkDataTrigger = function(sTargetFields, oTemplateEntry) {
		var aTargetFields = [];

		try {
			if (!sTargetFields) { return; }

			aTargetFields = sTargetFields.split(',');
			for (var i = 0; i < aTargetFields.length; i++) {
				var nFieldIndex = UTIL.indexOfRecord(oTemplateEntry.ColumnsInfo, 'id', aTargetFields[i].trim());
				if (nFieldIndex > -1) { UIManager.checkDataDependency(oTemplateEntry.ColumnsInfo[nFieldIndex], oTemplateEntry.FormData); }
			}
		} catch(oError) { oError.display("checkDataTrigger()"); }
	}

	this.checkDataDependency = function(oColumnInfo, oDataRecord) {
		var sDataKeys = null, sDataValue = null, sAction = null;

		if (!oColumnInfo.dependencyInfo) { return; }

		sDataKeys = oColumnInfo.dependencyInfo.fieldname.split('.')
		sAction = oColumnInfo.dependencyInfo.action;
		sDataValue = (sDataKeys.length > 1) ? oDataRecord[sDataKeys[0]][sDataKeys[1]] : oDataRecord[sDataKeys[0]];
		sDataValue = (sDataValue) ? sDataValue.toString().trim() : '';

		switch(sAction) {
			case 'disable':
				if (sDataValue === oColumnInfo.dependencyInfo.value) { UIManager.switchObjectState(oColumnInfo.domObject, false); }
				else { UIManager.switchObjectState(oColumnInfo.domObject, true); }
				break;
			case 'enable':
				if (sDataValue === oColumnInfo.dependencyInfo.value) { UIManager.switchObjectState(oColumnInfo.domObject, true); }
				else { UIManager.switchObjectState(oColumnInfo.domObject, false); }
				break;
			default:
				break;
		}
	}

	this.switchObjectState = function(oTarget, lEnable) {
		var oFormObjects = {}, lFormElement = ('INPUT,SELECT,TEXTAREA'.indexOf(oTarget.tagName.toUpperCase()) > -1) ? true : false;

		if (lFormElement) {
			oFormObjects = this.getObjectsByTarget(oTarget);
			sType = oFormObjects['ColumnInfoEntry'].xtype;
		} else {
			sType = 'custom';
		}

		switch(sType) {
			case jwfGlobal.JWF_MULTI_SELECT:
				MultiSelect.switchState(oTarget, lEnable);
				break;
			case 'remote-combo':
				Lookup.switchState(oTarget, lEnable);
				break;
			case 'custom':
				if (lEnable) { oTarget.removeAttribute('disabled'); } else { oTarget.setAttribute('disabled', 'disabled'); }
				break;
			default:
				if (lEnable) {
					oFormObjects['ColumnInfoEntry'].domObject.removeAttribute('disabled');
				} else {
					oFormObjects['ColumnInfoEntry'].domObject.setAttribute('disabled', 'disabled');
				}
				break;
		}
	}

	this.fillSelectRaw = function(oSelectDomObj, aOptionData, sSelectedValue) {
		while (oSelectDomObj.options.length > 0) { oSelectDomObj.options.remove(0); }

		if ( Array.isArray(aOptionData) ) {
			managePleaseSelect(aOptionData);

			aOptionData.forEach( function(oOptionData, nIndex) {
				oOption = document.createElement("OPTION");
				oOption.text = oOptionData.text;
				oOption.value = oOptionData.value;

				if  (sSelectedValue)  {
					if (sSelectedValue === oOptionData.value) { oOption.selected = true; }
				}

				oSelectDomObj.add(oOption);
			});
		}

		function managePleaseSelect(aOptionData) {
			var oPleaseSelect = { "text": "Please select", "value": "-1" }, lFound = true;

			lFound = aOptionData.some( function(oOption, nIndex) {
				lRetValue = ( (oOption.value === '-1') || (oOption.text === 'Please select') ) ? true : false;
				return lRetValue;
			});

			if (!lFound) { aOptionData.splice(0, 0, oPleaseSelect); }
		}
	}

	this.fillSelect = function(oSelectDomObj, aListData, nRowId) {
		PageCtrl.debug.log('loadThresholdOptions', 'is input data array? ' + Array.isArray(aListData));

		while (oSelectDomObj.options.length > 0) { oSelectDomObj.options.remove(0); }

		if ( Array.isArray(aListData) ) {
			aListData.forEach( function(oOptionData, nIndex) {
				oOption = document.createElement("OPTION");
				oOption.text = oOptionData.element_text;
				oOption.value = oOptionData.row_id;

				if  (nRowId)  {
					if (nRowId == oOptionData.row_id) { oOption.selected = true; }
				} else {
					oOption.selected = oOptionData.is_default;
				}

				oSelectDomObj.add(oOption);
			});
		}
	}

	this.fillSelect12 = function(oSelectDomObj, aListData, oObjRec) {
		var nLength = 0, oOption = {}, oPlaceHolder = jwfGlobal.LIST_PLACEHOLDER_OPTION;

		try {
			// Input must be array even if empty it is ok
			if (!Array.isArray(aListData)) { throw new TypeError('Input data is not list cannot fill {ddl} list.'.replace('{ddl}',oSelectDomObj.id)); }

			while (oSelectDomObj.options.length > 0) { oSelectDomObj.options.remove(0); }
			if (aListData.length < 1) { return; }

			nLength = (UTIL.indexOfRecord(aListData, 'Code', '-1') < 0) ? aListData.unshift(JSON.parse(JSON.stringify(oPlaceHolder))) : aListData.length;
			oObjRec = (oObjRec) ? (oObjRec.Code === '-1') ? oPlaceHolder : oObjRec : oPlaceHolder;

			for (var i = 0; i < nLength; i++) {
				oOption = document.createElement("OPTION");
				oOption.text = aListData[i].Text;
				oOption.value = aListData[i].Code;

				oSelectDomObj.add(oOption);
				if (oOption.value === oObjRec.Code) { oSelectDomObj.selectedIndex = i; }
			}
		} catch(oError) { oError.display("fillSelect()"); }
	}

	this.fillSelect11 = function(oSelectOrId, aListData, oObjRec) {
		var nLength = 0, oOption = {}, oPlaceHolder = jwfGlobal.LIST_PLACEHOLDER_OPTION;

		try {
			// Input must be array even if empty it is ok
			if (!Array.isArray(aListData)) { throw new TypeError('Input data is not list cannot fill {ddl} list.'.replace('{ddl}',oSelectOrId.id)); }

			nLength = (UTIL.indexOfRecord(aListData, 'Code', '-1') < 0) ? aListData.push(JSON.parse(JSON.stringify(oPlaceHolder))) : aListData.length;
			oObjRec = (oObjRec) ? (oObjRec.Code === '-1') ? oPlaceHolder : oObjRec : oPlaceHolder;
			while (oSelectOrId.options.length > 0) { oSelectOrId.options.remove(0); }

			for (var i = 0; i < nLength; i++) {
				oOption = document.createElement("OPTION");
				oOption.text = aListData[i].Text;
				oOption.value = aListData[i].Code;

				oSelectOrId.add(oOption);
				if (oOption.value === oObjRec.Code) { oSelectOrId.selectedIndex = i; }
			}
		} catch(oError) { oError.display("fillSelect()"); }
	}

	this.fillMultiSelect = function(oSelectOrId, oParentData, sFieldExpr) {
		var sDataList = sFieldExpr.substr(0, sFieldExpr.indexOf('['));
		var sFieldName = sFieldExpr.substr(sFieldExpr.indexOf('.') + 1);
		var oOption = {}, nLength = -1;
		var aDataList = (oParentData) ? oParentData[sDataList] : null;

		while (oSelectOrId.options.length > 0) { oSelectOrId.options.remove(0); }

		if (Array.isArray(aDataList)) {
			aDataList.sort( function( oFirstObject, oSecondObject ) { return oFirstObject[sFieldName].Text.localeCompare(oSecondObject[sFieldName].Text); });
			nLength = aDataList.length;
		} else { return; }

		for (var i = 0; i < nLength; i++) {
			if (aDataList[i].DataState.DBAction !== jwfGlobal.DATA_STATE_DELETE) {
				oOption = document.createElement("OPTION");
				oOption.text = aDataList[i][sFieldName].Text;
				oOption.value = aDataList[i][sFieldName].Code;

				oSelectOrId.add(oOption);
			}
		}
	}

	this.fillDataLabels = function(oTmplKeyOrObject, lEmptyData) {
		var oEditDataTab = null, oTemplateEntry = null, aLabelsInfo = [], sLabelText = '';
		lEmptyData = (lEmptyData) ? true : false;

		try {
			if (typeof oTmplKeyOrObject === 'string') {
				oEditDataTab = PageCtrl.getObjectById(PageCtrl.DefaultTabId);
				oTemplateEntry = oEditDataTab[oTmplKeyOrObject];
			} else { oTemplateEntry = oTmplKeyOrObject; }

			aLabelsInfo = oTemplateEntry.LabelsInfo;

			for (var i = 0; i < aLabelsInfo.length; i++) {
				sLabelText = '';
				if (lEmptyData) {
					if ((aLabelsInfo[i].fieldnames[0] === 'DataState') && (aLabelsInfo[i].fieldnames[1] === 'DBAction')) {
						sLabelText = '[No Data]';
					}
				} else {
					if ((aLabelsInfo[i].fieldnames[0] === 'DataState') && (aLabelsInfo[i].fieldnames[1] === 'DBAction')) {
						sLabelText = '[' + UTIL.getDataStateAsText(oTemplateEntry.FormData.DataState.DBAction) + ']';
						sLabelText = sLabelText + ((oTemplateEntry.FormData.DataState.Valid) ? '[Data Valid]' : '[Data Invalid]');
					} else {
						sLabelText = oTemplateEntry.FormData[aLabelsInfo[i].fieldnames[0]];
					}
				}
				aLabelsInfo[i].domObject.innerHTML = sLabelText;
			}
		} catch(oError) { oError("fillDataLabels()"); }
	}

	this.getFormInputs = function(sFormIds, lIgnoreWithNoId) {
		var sTagName = " INPUT SELECT TEXTAREA "; aFormIds = ((typeof(sFormIds) === 'string') && (sFormIds.length > 0))? sFormIds.split(",") : [];
		var nNoOfForms = aFormIds.length, oHeadForm = {}, nNoOfInputs = 0, aObjInputs = [], lInputIgnore = false;

		for (var i = 0; i < nNoOfForms; i++) {
			oForm = document.getElementById(aFormIds[i].trim());
			oForm.autocomplete = 'off';
			nNoOfInputs = oForm.elements.length;
			for (j = 0; j < nNoOfInputs; j++) {
				lInputIgnore = (!oForm.elements[j].id) && (lIgnoreWithNoId)
				if ( (sTagName.indexOf(oForm.elements[j].tagName) > 0) && (!lInputIgnore) ) { aObjInputs.push(oForm.elements[j]); }
			}
		}
		aObjInputs.sort( function( oFirstObject, oSecondObject ) { return (parseInt(oFirstObject.tabIndex) - parseInt(oSecondObject.tabIndex)); });
		return aObjInputs;
	}

	this.getFormColumnInfo = function(sFormIds) {
		var aFormObjects = this.getFormInputs(sFormIds, true);
		var nNoOfObjects = aFormObjects.length;
		var aColumnsInfo = [];

		for (var i = 0; i < nNoOfObjects; i++) {
			var oColumnInfo = {};
			oColumnInfo.id = aFormObjects[i].id;
			oColumnInfo.domObject = aFormObjects[i];
			oColumnInfo.xtype = (aFormObjects[i].getAttribute('data-xtype')) ? aFormObjects[i].getAttribute('data-xtype').toLowerCase() : aFormObjects[i].type.toLowerCase();
			oColumnInfo.attributes = UTIL.getDataAttributes(aFormObjects[i].getAttribute('data-attributes'));

			oColumnInfo.isComboInput = (oColumnInfo.xtype.indexOf('combo') > -1);
			oColumnInfo.isChangeOnSelection = ( ['local-combo', 'remote-combo', 'select-multiple'].indexOf(oColumnInfo.xtype) > -1 );

			if (oColumnInfo.attributes['data-dependency']) {
				var oDependency = {}, sAttribute = oColumnInfo.attributes['data-dependency'];

				oDependency.fieldname = sAttribute.substring(sAttribute.indexOf('{') + 1, sAttribute.indexOf('=')).trim();
				oDependency.value = sAttribute.substring(sAttribute.indexOf('=') + 1, sAttribute.indexOf('}')).trim();
				oDependency.action = sAttribute.substring(sAttribute.lastIndexOf("=") + 1, sAttribute.lastIndexOf("}")).trim().toLowerCase();

				oColumnInfo.dependencyInfo = oDependency;
			}
			aColumnsInfo.push(oColumnInfo);
		}
		return aColumnsInfo;
	}

	this.getFirstFocusElement = function(sDataForm) {
		var aColumnInfo = [], oRetElement = null;

		try {
			aColumnInfo = PageCtrl.ColumnsInfo[sDataForm];
			for (var i = 0; i < aColumnInfo.length; i++) {
				oRetElement = (aColumnInfo[i].domObject.getAttribute('disabled') !== 'disabled') ? aColumnInfo[i].domObject : null;
				if (oRetElement) { break; }
			}
		} catch(oError) { oError.display('getFirstFocusElement()'); }
		return oRetElement;
	}

	this.getNextTabElement = function(oCurrentElement) {
		var oFormObjects = {}, aInputElements = [], nStartIndex = -1, oRetElement = null;

		try {
			oFormObjects = UIManager.getObjectsByTarget(oCurrentElement);
			aInputElements = oFormObjects['TemplateEntry'].ColumnsInfo;
			nStartIndex = UTIL.indexOfRecord(aInputElements, 'id', oCurrentElement.id);

			if ( (nStartIndex > -1) && (nStartIndex < aInputElements.length) )  {
				nStartIndex = nStartIndex + 1;
				for (var i = nStartIndex; i < aInputElements.length; i++) {
					oRetElement = (aInputElements[i].domObject.getAttribute('disabled') !== 'disabled') ? aInputElements[i].domObject : null;
					if (oRetElement) { break; }
				}
			}
		} catch(oError) { oError.display('getNextTabElement()'); }
		return oRetElement;
	}

	this.getLabelInfo = function(sTmplKey) {
		var aDataLabels = $("label[data-fieldname*='" + sTmplKey + "']");
		var nNoOfLabels = aDataLabels.length, aLabelsInfo = [];

		for (var i = 0; i < nNoOfLabels; i++) {
			var oLabelInfo = {}, sFieldName = aDataLabels[i].getAttribute('data-fieldname').split(':')[1].trim();
			oLabelInfo.domObject = aDataLabels[i];
			oLabelInfo.fieldnames = sFieldName.split('.');
			aLabelsInfo.push(oLabelInfo);
		}
		return aLabelsInfo;
	}

	this.getObjectsByTarget = function(oTarget) {
		var oFormAttr = UTIL.getDataAttributes($(oTarget).closest('form').attr('data-attributes'));
		var oEditDataTab = PageCtrl.getObjectById(oFormAttr['edit-tab']);
		var oTemplateEntry = oEditDataTab.DataTemplate[oFormAttr['template-key']];
		var oColumnInfo = oTemplateEntry.ColumnsInfo[UTIL.indexOfRecord(oTemplateEntry.ColumnsInfo, 'id',oTarget.id)];

		return {'FormAttr': oFormAttr, 'EditDataTab': oEditDataTab, 'TemplateEntry': oTemplateEntry, 'ColumnInfoEntry': oColumnInfo }
	}

	this.getTemplateByGrid = function(sGridId) {
		var oGridAttributes = UTIL.getDataAttributes(document.getElementById(sGridId).getAttribute('data-attributes'));
		var oEditDataTab = null, oTemplateEntry = null;

		if (oGridAttributes['edit-tab']) {
			oEditDataTab = PageCtrl.getObjectById(oGridAttributes['edit-tab']);
			oEditDataTab.TemplateKeys.some( function(sKey, nIndex) {
				if (oEditDataTab.DataTemplate[sKey].GridId === sGridId) {
					oTemplateEntry = oEditDataTab.DataTemplate[sKey];
					return true;
				}
			});
		}
		return oTemplateEntry;
	}

	this.getChildNodes = function(oParentObj, sTagNames) {
		var nNoOfChilds = 0, i = 0, sTagName = '', sTagNames = (sTagNames) ? sTagNames.toLowerCase() : '';
		var aChildNodes = [], aRetChilds = [];

		if (oParentObj) {
			aChildNodes = oParentObj.childNodes;
			nNoOfChilds = aChildNodes.length;

			for (i = 0; i < nNoOfChilds; i++) {
				sTagName = (aChildNodes[i].tagName) ? aChildNodes[i].tagName.toLowerCase() : 'dummy';
				if ((sTagNames.indexOf(sTagName) > -1)) {
					aRetChilds.push(aChildNodes[i]);
				}
			}
		}
		return aRetChilds;
	}

	// Setup custom inputs evetns, validations and place holders etc
	this.setupCustomInputs = function () {
		var aInputs = $("input[data-xtype='date']"), i = 0, nNoOfInputs = 0, aAllCombos = [];

		nNoOfInputs = aInputs.length;
		for (i = 0; i < nNoOfInputs; i++) {
			aInputs[i].placeholder = 'dd/mm/yyyy';
			aInputs[i].setAttribute('maxlength','10');
		}

		aInputs = $("input[data-xtype='float']");
		nNoOfInputs = aInputs.length;
		for (i = 0; i < nNoOfInputs; i++) {
			aInputs[i].placeholder = 'nnn.nn';
		}

		aInputs = $("input[data-xtype='integer']");
		nNoOfInputs = aInputs.length;
		for (i = 0; i < nNoOfInputs; i++) {
			aInputs[i].placeholder = 'nnn';
		}

		$("input[data-xtype='date']").on( "keypress", UIManager.validKeys);
		$("input[data-xtype='float']").on( "keypress", UIManager.validKeys);
		$("input[data-xtype='integer']").on( "keypress", UIManager.validKeys);

		// this event is retained for non database linked form fields - is kind of duplicate of isDateInputValid()
		$("input[data-xtype='date']").on("focusout", function(oEvent) {
			if (UTIL.isValidDate(oEvent.target.value, oEvent.target)) {
				oEvent.target.classList.remove(jwfGlobal.CSS_CLASS_BASEVALUE_INVALID)
			} else {
				if (!oEvent.target.classList.contains(jwfGlobal.CSS_CLASS_BASEVALUE_INVALID)) {
					oEvent.target.classList.add(jwfGlobal.CSS_CLASS_BASEVALUE_INVALID)
				}
				oEvent.target.focus();
			}
		});
	}

	this.isDateInputValid = function(oDateField) {
		lDateInputValid = UTIL.isValidDate(oDateField.value, oDateField);

		if (lDateInputValid) {
			oDateField.classList.remove(jwfGlobal.CSS_CLASS_BASEVALUE_INVALID)
		} else {
			if (!oDateField.classList.contains(jwfGlobal.CSS_CLASS_BASEVALUE_INVALID)) {
				oDateField.classList.add(jwfGlobal.CSS_CLASS_BASEVALUE_INVALID)
			}
			oDateField.focus();
		}
		return lDateInputValid;
	}

	this.validKeys = function(oEvent) {
		var sType = oEvent.target.getAttribute('data-xtype'), nCharCode = 0, lValid = true;
		var sValue = oEvent.target.value, sFractionPart = '', oTarget = oEvent.target;

		// Get char code
		if (window.event) { nCharCode = window.event.keyCode; }
		else if (oEvent) { nCharCode = oEvent.which; }

		// validate as per type of field
		switch(sType) {
			case 'date':

				if ( [48,49,50,51,52,53,54,55,56,57].indexOf(nCharCode) < 0 ) {
					lValid = false;

				} else {
					if (sValue.match(/^\d{2}$/) !== null) {
						oTarget.value = sValue + '/';
					} else if (sValue.match(/^\d{2}\/\d{2}$/) !== null) {
						oTarget.value = sValue + '/';
					}
				}

				/*
				if (nCharCode > 31 && (nCharCode < 47 || nCharCode > 57)) { lValid = false; }
				else if (sValue.length === 10) {	lValid = false; }
				else if ((sValue.split('/').length > 2) && (nCharCode === 47)) { lValid = false;	}
				*/

				break;
			case 'float':
				if ((nCharCode === 46) && (sValue.split('.').length > 1)) {
					lValid = false;
				} else if ((nCharCode > 31) && (nCharCode < 48 || nCharCode > 57)) {
					lValid = (nCharCode === 46) ? true : false;
				} else if (sValue.split('.').length > 1) {
					if (sValue.split('.')[1].length > 2) {
						sValue = parseFloat(sValue).toFixed(2).toString();
						oEvent.target.value = sValue;
					}
				}
				break;
			case 'integer':
				if ((nCharCode > 31) && (nCharCode < 48 || nCharCode > 57)) { lValid = false; }
		}
		return lValid;
	}

	this.setupFormEvents = function(sLogicalForm, sActualForms, cbFormChange, sAttributes, cbInputFocus) {
		var aFormIds = sActualForms.split(','), nNoOfForms = aFormIds.length, aFormElements = [];

		for (var i = 0; i < nNoOfForms; i++) {
			aFormElements = this.getFormInputs(aFormIds[i], true);
			aFormElements.forEach( function(oElement, nElementIndex) { $(oElement).on('focus', cbInputFocus); });

			$('#' + aFormIds[i]).on('change', cbFormChange);
			document.getElementById(aFormIds[i]).setAttribute('data-attributes', sAttributes);
		}
	}

	this.setupFormEvents_old = function(sLogicalForm, sActualForms, cbFormChange, sAttributes, cbInputFocus) {
		var aFormIds = sActualForms.split(','), i = 0;
		var nNoOfForms = aFormIds.length;

		for (i = 0; i < nNoOfForms; i++) {

			$("#" + aFormIds[i]).delegate("input,select,textarea", "change", function() { cbFormChange(this); });
			//$("#" + aFormIds[i]).delegate("input,select,textarea", "focus", function () { cbInputFocus(this) });

			/*
			$("#" + aFormIds[i]).on("change", function() { cbFormChange(this); });
			$("#" + aFormIds[i]).on("focus", function() { cbInputFocus(this) });
			*/
			document.getElementById(aFormIds[i]).setAttribute('data-attributes', sAttributes);
		}
	}

	this.getLogicalFormId = function (oElement) {
		var oForm = document.getElementById($(oElement).closest('form').attr('id'));
		var oDataAttributes = UTIL.getDataAttributes(oForm.getAttribute('data-attributes'));
		var sLogicalName = null;

		if (oDataAttributes['logical-form-id']) { sLogicalName = oDataAttributes['logical-form-id']; }
		return sLogicalName;
	}

	this.syncronizeModelWithForm = function(oInput, oTemplateEntry) {
		var nFieldIndex = UTIL.indexOfRecord(oTemplateEntry.ColumnsInfo, 'id', oInput.id);
		var sFieldType = oTemplateEntry.ColumnsInfo[nFieldIndex].xtype;
		var sFieldId = oTemplateEntry.ColumnsInfo[nFieldIndex].id, nCode = 0;
		var lRequireModelStateChange = ( (sFieldType !== 'model-ignore') && (oTemplateEntry.FormData.DataState.DBAction === jwfGlobal.DATA_STATE_NO_CHANGE) ) ? true : false;

		// if form data josn is not initialized then return
		if (!oTemplateEntry.FormData) { return; }

		// Update data state to updated if state is fresh retrive from DB
		if (lRequireModelStateChange) { oTemplateEntry.FormData.DataState.DBAction = jwfGlobal.DATA_STATE_UPDATE; }

		switch (sFieldType) {
			case 'text':
			case 'textarea':
				oTemplateEntry.FormData[sFieldId] = UTIL.replaceAll(oInput.value, "'", "`");
				break;
			case 'float':
				oTemplateEntry.FormData[sFieldId] = (oInput.value) ? parseFloat(oInput.value) : 0;
				break;
			case 'integer':
				oTemplateEntry.FormData[sFieldId] = (oInput.value) ? parseInt(oInput.value) : 0;
				break;
			case 'date':
				oTemplateEntry.FormData[sFieldId] = UTIL.inputToDate(oInput.value);
				break;
			case 'local-combo':
			case 'remote-combo':
				oTemplateEntry.FormData[sFieldId].Code = oInput.getAttribute('data-xvalue');
				oTemplateEntry.FormData[sFieldId].Text = oInput.value;
				break;
			case 'select-multiple':
				break;
			case 'select-one':
				if (oTemplateEntry.FormData[sFieldId]) {
					nCode = parseInt(oInput.options[oInput.selectedIndex].value);
					oTemplateEntry.FormData[sFieldId].Code = oInput.options[oInput.selectedIndex].value;
					oTemplateEntry.FormData[sFieldId].Text = (nCode < 0) ? '' : oInput.options[oInput.selectedIndex].text;
				}
				break;
			case 'checkbox':
				oTemplateEntry.FormData[sFieldId] = (oInput.checked) ? true : false;
				break;
		}
	}

	this.isComboBox = function(oElement) {
		var sXType = oElement.getAttribute('data-xtype');
		sXType = (sXType) ? sXType.toLowerCase() : 'other';
		return (sXType.indexOf('combo') > -1);
	}

	this.isChangeOnSelection = function(oElement) {
		var xType = (oElement.getAttribute('data-xtype')) ? oElement.getAttribute('data-xtype').toLowerCase() : oElement.type.toLowerCase();
		return ( ['local-combo', 'remote-combo', jwfGlobal.JWF_MULTI_SELECT, 'select-multiple'].indexOf(xType) > -1 );
	}

	/**
	Returns a bounding rect for _el_ with absolute coordinates corrected for scroll positions.
	The native `getBoundingClientRect()` returns coordinates for an element's visual position relative
	to the top left of the viewport, so if the element is part of a scrollable region that has been
	scrolled, its coordinates will be different than if the region hadn't been scrolled. This method corrects
	for scroll offsets all the way up the node tree, so the returned bounding rect will represent an absolute
	position on a virtual canvas, regardless of scrolling.
	**/

	this.getAbsoluteBoundingRect = function (el) {
		 var doc  = document,
			  win  = window,
			  body = doc.body,

			  // pageXOffset and pageYOffset work everywhere except IE <9.
			  offsetX = win.pageXOffset !== undefined ? win.pageXOffset :
					(doc.documentElement || body.parentNode || body).scrollLeft,
			  offsetY = win.pageYOffset !== undefined ? win.pageYOffset :
					(doc.documentElement || body.parentNode || body).scrollTop,

			  rect = el.getBoundingClientRect();

		 if (el !== body) {
			  var parent = el.parentNode;

			  // The element's rect will be affected by the scroll positions of  *all* of its scrollable parents, not
			  // just the window, so we have to walk up the tree and collect every scroll offset. Good times.
			  while (parent !== body) {
					offsetX += parent.scrollLeft;
					offsetY += parent.scrollTop;
					parent   = parent.parentNode;
			  }
		 }

		 return {
			  bottom: rect.bottom + offsetY,
			  height: rect.height,
			  left  : rect.left + offsetX,
			  right : rect.right + offsetX,
			  top   : rect.top + offsetY,
			  width : rect.width
		 };
	}

	this.markRequiredFields = function (sFormFieldList) {
		var aElementList = (typeof(sFormFieldList) === 'string') ? sFormFieldList.split(',') : [];
		var oElement = null;

		aElementList.forEach( function(sElementId, nElemIndex) {
			oElement = document.getElementById(sElementId.trim());
			if (oElement) { oElement.setAttribute('data-required','Y'); }
		});
	}

	this.LabelTabs = {
		Tabs: {},
		create: function(oTabsConfiguration) {
			var oLabelTabs = this, nMinWidth = oTabsConfiguration.TabsMinWidth;

			if (nMinWidth) { $('#' + oTabsConfiguration.TabsId).children("label").css('min-width', (nMinWidth + 'px')); }

			if (!oLabelTabs.Tabs[oTabsConfiguration.TabsId]) {
				oLabelTabs.Tabs[oTabsConfiguration.TabsId] = oTabsConfiguration;
			}

			$('.jwf-label-tabs-bar').children('label').on('click', function (oEvent) {
				var sTabsId = $(oEvent.target).closest( "div" )[0].id, oClickedTab = UIManager.LabelTabs.Tabs[sTabsId];

				if (oClickedTab) {
					oClickedTab.TabsPairInfo.forEach( function(oTabPair) {

						if (oEvent.target.id === oTabPair.TabLabel) {
							$('#' + oTabPair.TabLabel).attr('data-selected','true');
							$('#' + oTabPair.ContentDivId).css('display', 'block');
							
							var dataTableId = oTabPair.dataTableId;
							if(typeof dataTableId !== "undefined" && dataTableId.length > 0){
								var table =  $('#' + dataTableId).DataTable();
           						table.columns.adjust().draw();
							}
						} else {
							$('#' + oTabPair.TabLabel).attr('data-selected','false');
							$('#' + oTabPair.ContentDivId).css('display', 'none');
						}
					});
				}
			});
		}
	}

})();

var MultiSelect = {
	ObjectCollections: {},
	setup: function() {
		var aMultiSelects = document.getElementsByClassName('jwf-multi-select-container');
		var nNoOfElements = aMultiSelects.length;

		// get all key information and attach event handlers
		for (var i = 0; i < nNoOfElements; i++) {
			var oSelectDomObject = null, oChildContainer = null;

			// Store basic information - enhance as needed
			oSelectDomObject = UIManager.getChildNodes(aMultiSelects[i],'SELECT')[0];
			oSelectDomObject.setAttribute('data-xtype', jwfGlobal.JWF_MULTI_SELECT);

			// Attach handlers
			oChildContainer = aMultiSelects[i].childNodes[1];
			$(oChildContainer.querySelector("input[type='text']")).on("input", function(oEvent) { MultiSelect.onInput(oEvent); });
			$(oChildContainer.querySelector("input[type='checkbox']")).on("click", function(oEvent) { MultiSelect.onCheckClick(oEvent); });
			$(oChildContainer.querySelector("button[data-xtype='add']")).on("click", function(oEvent) { oEvent.preventDefault(); MultiSelect.onAddClick(oEvent); });
			$(oChildContainer.querySelector("button[data-xtype='delete']")).on("click", function(oEvent) { oEvent.preventDefault(); MultiSelect.deleteSelectedData(oEvent); });
		}
	},
	switchState: function(oTarget, lEnable) {
		var oContainer = oTarget.parentNode;

		if (lEnable) {
			oContainer.querySelector("input[type='text']").removeAttribute('disabled')
			oContainer.querySelector("input[type='checkbox']").removeAttribute('disabled');
			oContainer.querySelector("button[data-xtype='add']").removeAttribute('disabled');
			oContainer.querySelector("button[data-xtype='delete']").removeAttribute('disabled');
			oTarget.removeAttribute('disabled');
		} else {
			oContainer.querySelector("input[type='text']").setAttribute('disabled', 'disabled');
			oContainer.querySelector("input[type='checkbox']").setAttribute('disabled', 'disabled');
			oContainer.querySelector("button[data-xtype='add']").setAttribute('disabled', 'disabled');
			oContainer.querySelector("button[data-xtype='delete']").setAttribute('disabled', 'disabled');
			oTarget.setAttribute('disabled', 'disabled');
		}
	},
	getData: function(oTarget, lOnlySelected) {
		var aOptions = oTarget.options, nNoOfOptions = oTarget.options.length, aData = [];
		for (var i = 0; i < nNoOfOptions; i++) {
			if (lOnlySelected) {
				if (aOptions[i].selected) {	aData.push( { 'Code': aOptions[i].value, 'Text': aOptions[i].text } ); }
			} else { aData.push( { 'Code': aOptions[i].value, 'Text': aOptions[i].text } ); }
		}
		return aData;
	},
	onInput: function(oEvent) {
	},
	onAddClick: function(oEvent) {
		var oTarget = oEvent.target.parentNode.parentNode.querySelector('select');
		Lookup.Dialog.promote(oTarget);
	},
	addSelectedData: function(oTarget) {
		var nEntryIndex = -1, oColumnEntry = null, sFieldExpr = null, oParentData = null;
		var sChildRecsKey = null, sFieldName = null, aSelectedRecs = Lookup.ActiveObject.SelectedData;
		var nNoOfNewChildRecs = aSelectedRecs.length;

		var oTemplateEntry = UIManager.getObjectsByTarget(oTarget)['TemplateEntry'];

		nEntryIndex = UTIL.indexOfRecord(oTemplateEntry.ColumnsInfo, 'id', oTarget.id);
		oColumnEntry = oTemplateEntry.ColumnsInfo[nEntryIndex];
		sFieldExpr = oColumnEntry.attributes['field-multi'];
		oParentData = oTemplateEntry['FormData'];
		sChildRecsKey = sFieldExpr.substr(0, sFieldExpr.indexOf('['));
		sFieldName = sFieldExpr.substr(sFieldExpr.indexOf('.') + 1);

		// If child records array is still null (as sent by back end API) and return by empty page ctrl get emtpy object function
		if (!oParentData[sChildRecsKey]) { oParentData[sChildRecsKey] = []; }

		for (var i = 0; i < nNoOfNewChildRecs; i++) {
			var oFieldValues = {}, oChildRecord = {};

			oFieldValues[sFieldName] = { 'Code': aSelectedRecs[i].Code, 'Text': aSelectedRecs[i].Text };
			oFieldValues.ParentRowId = oParentData.RowId;
			oChildRecord = PageCtrl.getNewDataEntity(oColumnEntry.attributes['entity-key'], oFieldValues );

			oParentData[sChildRecsKey].push( oChildRecord );
		}
		UIManager.fillMultiSelect(oTarget, oParentData, sFieldExpr);
	},
	deleteSelectedData: function(oEvent) {
		var oTarget = oEvent.target.parentNode.parentNode.querySelector('select');

		var oFormObjects = UIManager.getObjectsByTarget(oTarget);
		var nEntryIndex = UTIL.indexOfRecord(oFormObjects['TemplateEntry'].ColumnsInfo, 'id', oTarget.id);
		var oColumnEntry = oFormObjects['TemplateEntry'].ColumnsInfo[nEntryIndex];

		var sFieldExpr = oColumnEntry.attributes['field-multi'];
		var sChildRecsKey = sFieldExpr.substr(0, sFieldExpr.indexOf('['));
		var sFieldName = sFieldExpr.substr(sFieldExpr.indexOf('.') + 1);
		var oParentData = oFormObjects['TemplateEntry']['FormData'];

		var aAllDataRecs = oParentData[sChildRecsKey], aSelectedData = this.getData(oTarget, true);
		var nNoOfDataRecs = aSelectedData.length, lUpdateParent = false;

		if (oFormObjects.EditDataTab.MainEntity.ReadOnly) { return; }

		for (var i = 0; i < nNoOfDataRecs; i++) {
			var nIndex = UTIL.getIndexOfRecord(aAllDataRecs, sFieldName + '.Code', aSelectedData[i].Code);
			if (nIndex  > -1)  {
				if (aAllDataRecs[nIndex]['DataState'].DBAction === jwfGlobal.DATA_STATE_INSERT) { aAllDataRecs.splice(nIndex, 1); }
				else { aAllDataRecs[nIndex]['DataState'].DBAction = jwfGlobal.DATA_STATE_DELETE; }
				lUpdateParent = true;
			}
		}
		if ((lUpdateParent) && (oParentData['DataState'].DBAction === jwfGlobal.DATA_STATE_NO_CHANGE)) {
			oParentData['DataState'].DBAction = jwfGlobal.DATA_STATE_UPDATE;
		}
		UIManager.fillMultiSelect(oTarget, oParentData, sFieldExpr);
		EditDataTab.jwfFormChange(oTarget, true);
	},
	getCount: function(oTarget) {
		var oFormObjects = UIManager.getObjectsByTarget(oTarget);
		var nEntryIndex = UTIL.indexOfRecord(oFormObjects['TemplateEntry'].ColumnsInfo, 'id', oTarget.id);
		var oColumnEntry = oFormObjects['TemplateEntry'].ColumnsInfo[nEntryIndex];

		var sFieldExpr = oColumnEntry.attributes['field-multi'];
		var sChildRecsKey = sFieldExpr.substr(0, sFieldExpr.indexOf('['));
		var sFieldName = sFieldExpr.substr(sFieldExpr.indexOf('.') + 1);
		var oParentData = oFormObjects['TemplateEntry']['FormData'];

		var aAllDataRecs = oParentData[sChildRecsKey], nNoOfDataRecs = aAllDataRecs.length, nDataCount = 0;

		for (var i = 0; i < nNoOfDataRecs; i++) {
			if (aAllDataRecs[i]['DataState'].DBAction !== jwfGlobal.DATA_STATE_DELETE) { nDataCount++; }
		}
		return nDataCount;
	},
	onCheckClick: function(oEvent) {
		var lChecked = (oEvent.target.checked);
		var oSelectObj = oEvent.target.parentNode.parentNode.querySelector('select');
		var nNoOfItems = oSelectObj.options.length;

		for (var i = 0; i < nNoOfItems; i++) { oSelectObj.options[i].selected = lChecked; }
		oSelectObj.focus();
	}
}

/**************************************************************************
UTIL: Miscellanous utility functions
***************************************************************************/
var UTIL = {
	getApiDataInWrap: function(nComponent, nContext, aDataPacket) {
		return { "Component" : nComponent,	"Context" : nContext,	"dataInPacket": aDataPacket };
	},
	getDataStateAsSymbol: function(nDbAction) {
		var aStateCodes = [ jwfGlobal.DATA_STATE_NO_CHANGE, jwfGlobal.DATA_STATE_UPDATE, jwfGlobal.DATA_STATE_INSERT, jwfGlobal.DATA_STATE_DELETE ];
		var aStateStr = [' ', '&#9728;', '&#10010;', '&#9747;'];
		return aStateStr[aStateCodes.indexOf(nDbAction)];
	},
	getDataStateAsText: function(nDbAction) {
		var aStateCodes = [ jwfGlobal.DATA_STATE_NO_CHANGE, jwfGlobal.DATA_STATE_UPDATE, jwfGlobal.DATA_STATE_INSERT, jwfGlobal.DATA_STATE_DELETE ];
		var aStateStr = ['', 'Record Changed', 'New Record', ''];
		return aStateStr[aStateCodes.indexOf(nDbAction)];
	},
	isNewRecord: function(oFormData) {
		return (oFormData.DataState.DBAction === jwfGlobal.DATA_STATE_INSERT) ? true : false;
	},
	isValidRecord: function(oFormData) {
		var lValid = typeof(oFormData === 'object') ? (Object.keys(oFormData).length > 0) ? true : false : false;
		if (lValid) { lValid = (oFormData.DataState.Valid) ? true : false; }
		return lValid;
	},
	getNonDeletedRecordCount: function(aSourceRecords) {
		var nRecordCount = 0, aSourceRecords = (Array.isArray(aSourceRecords)) ? aSourceRecords : [];
		aSourceRecords.forEach( function(oSourceRecord, nIndex) {
			if (oSourceRecord.DataState.DBAction !== jwfGlobal.DATA_STATE_DELETE) { ++nRecordCount; }
		});
		return nRecordCount;
	},
	getNonDeletedRecords: function(aSourceRecords) {
		var aReturnRecords = [];
		aSourceRecords.forEach( function(oSourceRecord, nIndex) {
			if (oSourceRecord.DataState.DBAction !== jwfGlobal.DATA_STATE_DELETE) { aReturnRecords.push(oSourceRecord); }
		});
		return aReturnRecords;
	},
	getFilteredRecords: function(aDataRecords, oDataFilter) {
		var aKeys = oDataFilter.Key.split('.'), aFilteredRecords = [], sDataValue = null;

		if (!Array.isArray(aDataRecords)) { return aFilteredRecords; }

		oDataFilter.Value = oDataFilter.Value.toUpperCase().trim();
		aDataRecords.forEach( function(oDataRecord, nIndex) {
			sDataValue = (aKeys.length > 1) ? oDataRecord[aKeys[0]][aKeys[1]].toUpperCase() : oDataRecord[oDataFilter.Key].toUpperCase();
			if (sDataValue.indexOf(oDataFilter.Value) > -1) { aFilteredRecords.push( JSON.parse(JSON.stringify(oDataRecord), JSON.dateParser) ); }
		});
		return aFilteredRecords;
	},
	isArray: function (oArrayOrObj) {
		return Object.prototype.toString.call(oArrayOrObj) === '[object Array]';
	},
	replaceAll: function(sInputStr, sTokenStr, sReplaceStr) {
		var sOutputStr = sInputStr.trim(), nIndex = 0;

		if (sOutputStr.indexOf(sTokenStr) > -1) {
			while (sOutputStr.indexOf(sTokenStr) > -1) { sOutputStr = sOutputStr.replace(sTokenStr, sReplaceStr); }
		}
		return sOutputStr;
	},
	isStringsRepeat: function (sTextToSearch, aStrings) {
		sTextToSearch = sTextToSearch.toLowerCase();
		return aStrings.some( function(sText, nIndex) {
			return ( sTextToSearch.indexOf(sText) < sTextToSearch.lastIndexOf(sText) ) ? true : false;
		});
	},
	getUniqueId: function () {
		var nUniqueId = Math.floor(Math.random() * (999999 - 1000 + 1)) + 1000;
		return nUniqueId.toString();
	},
	getUniqueToken: function(nSize) {
		 var sPossibleChars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ!/&^$@*#%?<>abcdefghijklmnopqrstuvwxyz0123456789', sToken = '';
		 for(var i = 0; i < nSize; i++) { sToken += sPossibleChars.charAt(Math.floor(Math.random() * sPossibleChars.length)); }
		 return sToken;
	},
	getDaysInMonth: function (m, y) {
	    return m===2 ? y & 3 || !(y % 25) && y & 15 ? 28 : 29 : 30 + (m +(m >> 3) & 1);
	},
	getDataAttributes: function(sDataAttributes) {
		var aAttribtes = [], nNoOfAttributes = 0, i = 0;
		var oAttributes = {}, sKey = "", sValue = "";
		var sSep = String.fromCharCode(255);

		// continue only if input is string
		if (typeof(sDataAttributes) === 'string') {

			// replace ';' with chr(255) as seperator
			while (sDataAttributes.indexOf(';') > -1) {
				sDataAttributes = sDataAttributes.replace(";",sSep);
				i++;
				if (i > 10) { break; }
			}

			aAttribtes = sDataAttributes.split(sSep);
			nNoOfAttributes = aAttribtes.length - 1;

			if (nNoOfAttributes > 0) {
				for (i = 0; i < nNoOfAttributes; i++) {
					sKey = aAttribtes[i].split(':')[0].trim();
					sValue = aAttribtes[i].split(':')[1].trim();
					oAttributes[sKey] = sValue;
				}
			}
		}
		return oAttributes;
	},
	parseIds: function (sCompositeId) {
		var aIds = [null, null];

		if (typeof(sCompositeId) === 'string') {
			aIds = (sCompositeId.indexOf(':') > 0) ? sCompositeId.split(':') : [sCompositeId, sCompositeId];
		}
		return aIds;
	},
	indexOfRecord: function (aJsonRecords, sKey, oValue) {
		var nRetIndex = -1;
		if (Array.isArray(aJsonRecords)) {
			aJsonRecords.some(function(oRecord, nIndex) {
				if (oRecord[sKey] === oValue) {
					nRetIndex = nIndex;
					return true;
				}
			});
		}
		return nRetIndex;
	},
	getIndexOfRecord: function (aJsonRecords, sKey, oValueToSearch) {
		var nRetIndex = -1, aKeys = sKey.split('.'), oValue = '';
		if ( (Array.isArray(aJsonRecords)) && (aKeys.length < 4) ) {
			aJsonRecords.some(function(oRecord, nIndex) {
				oValue = (aKeys.length < 2) ? oRecord[sKey] : (aKeys.length < 3) ? oRecord[aKeys[0]][aKeys[1]] : oRecord[aKeys[0]][aKeys[1]][aKeys[2]];
				if (oValue === oValueToSearch) {
					nRetIndex = nIndex;
					return true;
				}
			});
		}
		return nRetIndex;
	},
	indexOfGridRow: function (oGridDomObj, nCellNo, sTextToMatch) {
		var nNoOfRows = oGridDomObj.rows.length, nRowIndex = -1;

		if (nNoOfRows > 0) {
			for (var i = 0; i < nNoOfRows; i++) {
				if (oGridDomObj.rows[i].cells[nCellNo].innerHTML === sTextToMatch) {
					nRowIndex = i;
					break;
				}
			}
		}
		return nRowIndex;
	},
	copyJsonRecord: function(oSource, oCopy) {
		for (sKey in oSource) { oSource[sKey] = oCopy[sKey]; }
	},
	isHasDirtyData: function(aDataList) {
		return aDataList.some( function(oDataRecord) { return (oDataRecord.DataState.DBAction !== jwfGlobal.DATA_STATE_NO_CHANGE); });
	},
	getDirtyDataCount: function(oDataObjectOrList) {
		var nLength = 0, nCount = 0, nKeys = 0;

		if (Array.isArray(oDataObjectOrList)) {
			nLength = oDataObjectOrList.length;
			for (var i = 0; i < nLength; i++) {
				if (oDataObjectOrList[i].DataState.DBAction !== jwfGlobal.DATA_STATE_NO_CHANGE) {
					nCount = nCount + 1;
				}
			}
		} else if (oDataObjectOrList) {
			nCount = (Object.keys(oDataObjectOrList).length > 0) ? (oDataObjectOrList.DataState.DBAction !== jwfGlobal.DATA_STATE_NO_CHANGE) ? 1 : 0 : 0;
		}
		return nCount;
	},
	/* assumption this fucntion is always called from focusout of date field */
	isValidDate: function(sDateValue, oDateElement) {
		var aDateParts = sDateValue.split('/'), lValid = false, nValidDays = 0, lRefreshValue = false;

		/* Continue logic top down, if decision reached break to return statement */
		do {

			/* Worst case 1 user tabs out of empty value, pass true, let business validation take care of sitution */
			if (sDateValue.trim().length < 1) {
				lValid = true;
				break;
			}

			/* Worst case 2 user tabs out of incomplete date value, not reached year part */
			if (aDateParts.length < 3) {
				lValid = false;
				break;
			}

			/* Now at least we have valid digits in each day (sure 2 digits), month (sure 2 digits), year (may be 1, 2, 3 or 4 digits) */
			nValidDays = UTIL.getDaysInMonth(parseInt(aDateParts[1]), parseInt(aDateParts[2]));

			/* Normalize year from (1,2 or 3) to (4) digits. dd and mm can never be less then 2 digits else worst case 2 will catch it */
			if (aDateParts[2].toString().length === 2) {
				aDateParts[2] = (new Date()).getFullYear().toString().substring(0,2) + aDateParts[2];
				lRefreshValue = true;
			} else {
				aDateParts[2] = aDateParts[2].padStart(4,'0');
				lRefreshValue = true;
			}

			lValid =
				(
					(parseInt(aDateParts[0]) > nValidDays) ||
					(parseInt(aDateParts[1]) > 12) ||
					(parseInt(aDateParts[1]) < 1) ||
					(!UTIL.isNumberBetween(parseInt(aDateParts[2]), jwfGlobal.DATE_ENTRY_YEAR_RANGE.MinYear, jwfGlobal.DATE_ENTRY_YEAR_RANGE.MaxYear))
				) ? false : true;

			break;

		} while (true);

		if ( (lRefreshValue) && (oDateElement) ) {
			oDateElement.value = '{0}/{1}/{2}'.replace('{0}', aDateParts[0]).replace('{1}', aDateParts[1]).replace('{2}', aDateParts[2]);
		}

		return lValid;
	},
	isValidDate_old: function(sDateValue) {
		var aDateParts = sDateValue.split('/'), lValid = true;
		var nValidDays = UTIL.getDaysInMonth(parseInt(aDateParts[1]), parseInt(aDateParts[2]));

		if (parseInt(aDateParts[0]) > nValidDays) { lValid = false; }
		else if (parseInt(aDateParts[1]) > 12) { lValid = false; }
		else if ((parseInt(aDateParts[2]) < 1970) || (parseInt(aDateParts[2]) > 2025)) { lValid = false; }

		return lValid;
	},
	inputToDate: function(sDateValue) {
		var aDateParts = [], oDate = null;

		sDateValue = (sDateValue.trim().length < 1) ? null : sDateValue.trim();
		aDateParts = (sDateValue) ? sDateValue.split('/') : [];

		aDateParts.forEach( function(sDatePart, nIndex) { if (sDatePart.length < 2) { aDateParts[nIndex] = '0' + sDatePart; } });

		oDate = (aDateParts.length < 3) ? null :  new Date(aDateParts[2] + '-' + aDateParts[1] + '-' + aDateParts[0] + 'T00:00:00.000Z');
		return new Date(oDate.getTime() + 330*60000);
	},
	dateToInput: function(oDateValue) {
		return (oDateValue) ? (oDateValue.getDate() + "/" + (oDateValue.getMonth()+1) + "/" + oDateValue.getFullYear()) : '';
	},
	getFinYearDates: function(sFinYear) {
		var aYears = sFinYear.split('-');
		return { StartDate: new Date(aYears[0] + '-04-01T00:00:00.000Z'), EndDate: new Date(aYears[1] + '-03-31T00:00:00.000Z') };
	},
	getErrorInfo: function(oError) {
		return (oError.stack) ? oError.message + '\n' + oError.stack : oError.message;
	},
	getArrayToString: function(aStringArray, sDelimatedBy) {
		var sToString = '';
		aStringArray.forEach( function(sValue, nIndex) { sToString = sToString + sValue + sDelimatedBy });
		sToString = (sToString.length < 1) ? null : sToString.substring(0, sToString.length-1);
		return sToString;
	},
	getTokenizedMessage: function(sMsg, aTokens) {
		var aTokens = (Array.isArray(aTokens)) ? aTokens : [];
		aTokens.forEach( function(sToken, nIndex) {	sMsg = sMsg.replace('{'+nIndex+'}', sToken);	});
		return sMsg;
	},
	isNumberBetween: function(nValue, nStart, nEnd) {
		return ( (nValue >= nStart) && (nValue <= nEnd) ) ? true : false;
	},
	removeOptionFromDropdownList: function(sItemText, sListName) {
		var nItemIndex = -1, aOriginalList = PageCtrl.getDataList(sListName);

		aOriginalList.some( function(oListData, nIndex) {
			if (oListData.RefText === sItemText) {
				nItemIndex = nIndex;
				return true;
			} else {
				return false;
			}
		});

		if (nItemIndex > -1) { aOriginalList.splice(nItemIndex, 1); }
	},
	getObjectRefByText: function(sItemText, sPageList, lFullObject) {
		var aObjectRefList = [], oObjectRef = { "Code": "-1", "Text": null, "RefText": null };

		if (sItemText.length > 0) {
			aObjectRefList = PageCtrl.getDataList(sPageList);
			aObjectRefList.some( function(oObjectRecord, nIndex) {
				if ( oObjectRecord.RefText === sItemText ) {
					oObjectRef = oObjectRecord;
					return true;
				} else {
					return false;
				}
			});
		}
		return (lFullObject) ? oObjectRef : { "Code": oObjectRef.Code, "Text": oObjectRef.Text };
	},
	getObjectRefByCode: function(nRowId, sPageList, lFullObject) {
		var aObjectRefList = [], oObjectRef = { "Code": "-1", "Text": null, "RefText": null };

		nRowId = (!nRowId) ? 0 : nRowId;
		if (nRowId > 0) {
			aObjectRefList = PageCtrl.getDataList(sPageList);
			aObjectRefList.some( function(oObjectRecord, nIndex) {
				if ( oObjectRecord.Code === nRowId.toString() ) {
					oObjectRef = oObjectRecord;
					return true;
				} else {
					return false;
				}
			});
		}
		return (lFullObject) ? oObjectRef : { "Code": oObjectRef.Code, "Text": oObjectRef.Text };
	},
	getObjectRefListAsRowIds: function(sPageList) {
		var aObjectRefList = PageCtrl.getDataList(sPageList), oRetObjectRef = {};

		if (Array.isArray(aObjectRefList)) {
			aObjectRefList.forEach( function (oObjectRef, nIndex) {
				oRetObjectRef[oObjectRef.RefText] = parseInt(oObjectRef.Code)
			});
		}
		return oRetObjectRef;
	},
	getPercentageOfNumber: function(nNumber, nPercentage) {
		return (nNumber/100)*nPercentage;
	},
	getRoundedAmount: function(dAmount, sRoundType) {
		var dRetAmount = dAmount.toFixed(2);

		switch (sRoundType.toUpperCase()) {
			case 'FLOOR':
				dRetAmount = Math.floor(dAmount);
				break;

			case 'CEILING':
				dRetAmount = Math.ceil(dAmount);
				break;

			case 'NORMAL_ROUNDING':
				dRetAmount = Math.round(dAmount * 100) / 100;
				break;

			case 'ROUNDING':
				dRetAmount = Math.round(dAmount);
				break;
			}
		return dRetAmount;
	},
	ObjectAsString: function(oJsonObject, sDelimitor) {
		var sRetValue = '', nLength = 0;

		Object.keys(oJsonObject).forEach( function(sKey, nKeyIndex) { sRetValue = sRetValue + oJsonObject[sKey] + sDelimitor; } )
		nLength = sRetValue.length;

		return (nLength > 0, sRetValue.substring(0, nLength - 1));
	},
	isValueNull: function(oValueToCheck, oValueToRet) {
		return (oValueToCheck) ? oValueToCheck : oValueToRet;
	},
	extractSubObjectByFields: function(oFullDataObject, sFieldsToExtract) {
		var aDataFields = sFieldsToExtract.split(','), oSubDataObject = {};

		aDataFields.forEach( function(sField, nIndex) { oSubDataObject[sField] = oFullDataObject[sField]; });
		return JSON.parse(JSON.stringify(oSubDataObject));
	},
	getSanitizedSQLString: function(sSqlColumnValue) {
		return sSqlColumnValue.replace("'",String.fromCharCode(255));
	},
	chopLongText: function(sLongStr, nSize, sJoinBy) {
	  const nChunks = Math.ceil(sLongStr.length / nSize);
	  const aChunks = new Array(nChunks);

	  if (!sJoinBy) { sJoinBy = ','; }

	  for (let nIndex = 0, nBeginPos = 0; nIndex < nChunks; ++nIndex, nBeginPos += nSize) {
		 aChunks[nIndex] = sLongStr.substr(nBeginPos, nSize)
	  }

	  return aChunks.join(sJoinBy);
	}
}

/* Error Object */
function MsgPanel(sMsgPanelId, sErrPanelId) {
	PageCtrl.pageGlobal.setValue('SHOW-ERR-TEXT', '&#9658;&nbsp;Show Errors');
	PageCtrl.pageGlobal.setValue('HIDE-ERR-TEXT', '&#9650;&nbsp;Hide Errors');
	PageCtrl.pageGlobal.setValue('NO-ERR-TEXT', '&#216;&nbsp;No Errors');
	PageCtrl.pageGlobal.setValue('NO-ERR-BG', '#ccffcc');
	PageCtrl.pageGlobal.setValue('HAS-ERR-BG', '#ffad99');

	this.MsgPanel = document.getElementById(sMsgPanelId);
	this.ErrPanel = document.getElementById(sErrPanelId);

	this.MsgControl = this.MsgPanel.rows[0].cells[0].firstChild;
	this.ErrTable = this.ErrPanel.childNodes[1];
	this.ErrTable.id = UTIL.getUniqueId();

	this.MsgControl.setAttribute('data-toggle-state', 'disabled');
	this.MsgControl.innerHTML = PageCtrl.pageGlobal.getValue('NO-ERR-TEXT');

	this.TitleMsg = this.MsgPanel.rows[0].cells[1].innerHTML;
	this.ErrorList = [];

	/* Local function scoped inside */
	var checkExists = function(curErrors, objErrMsg) {
		var i = 0, l = curErrors.length;
		var prvErrMsg = {};
		var lResult = false;

		if (l > 0) {
			for (i = 0; i <  l; i++) {
				prvErrMsg = curErrors[i];
				if (prvErrMsg.key === objErrMsg.key) {
					lResult = true;
					break;
				}
			}
		}
		return lResult;
	}

	/* Local function exposed to settimer event */
	this.updateUI = function() {
		LoadErrorList(this.MsgPanel.id);
	}

	this.MsgControl.addEventListener("click", toggleErrors);

	/* public interface functions */
	this.setTitleMsg = function (sMsg) {
		this.TitleMsg = sMsg;
	}

	this.clearErrors = function (sMsg) {
		this.TitleMsg = sMsg;
		this.ErrorList = [];
	}

	this.addErrors = function (sMsg, oErrMsgs) {
		var nErrObj = 0
		var aErrMsgs = UTIL.isArray(oErrMsgs) === true ? oErrMsgs : [ oErrMsgs ];
		var nArrLength = aErrMsgs.length;

		for (nErrObj = 0; nErrObj <  nArrLength; nErrObj++) {
			if ( checkExists(this.ErrorList, aErrMsgs[nErrObj]) === false) { this.ErrorList.push(aErrMsgs[nErrObj]); }
		}
		this.TitleMsg = sMsg;
	}

	this.removeErrors_old = function (sMsg, aKeyList) {
		var nErrObj = 0, nArrLength = aKeyList.length;
		var aIndexList = [];
		var nFactor = 0;

		if (this.ErrorList.length > 0) {
			for (nErrObj = 0; nErrObj < nArrLength; nErrObj++) {
				if (aKeyList[nErrObj] === this.ErrorList[nErrObj].key) {
					aIndexList.push(nErrObj);
				}
			}
			nArrLength = aIndexList.length;
			nFactor = 0;
			for (nErrObj = 0; nErrObj <  nArrLength; nErrObj++) {
				this.ErrorList.splice(aIndexList[nErrObj] - nFactor, 1);
				nFactor++;
			}
		}
		this.TitleMsg = sMsg;
	}

	/* Pradeep 04-Aug-2017 Rewritten below method to bug fix index problem in old method */
	this.removeErrors = function (sMsg, aKeyList) {
		var nErrObj = 0, nArrLength = aKeyList.length, aIndexList = [], nFactor = 0;
		var aErrorList = Array.isArray(this.ErrorList[nErrObj]) ? this.ErrorList[nErrObj] : [];

		aErrorList.forEach( function(oErrObject, nIndex) {
			if (aKeyList.indexOf(oErrObject.key) > -1) { aIndexList.push(nIndex); }
		});

		nArrLength = aIndexList.length;

		if (nArrLength > 0) {
			nFactor = 0;
			for (nErrObj = 0; nErrObj <  nArrLength; nErrObj++) {
				aErrorList.splice(aIndexList[nErrObj] - nFactor, 1);
				nFactor++;
			}
		}
		this.ErrorList = aErrorList;
		this.TitleMsg = sMsg;
	}

	//Add self reference to global structure
	PageCtrl.pageGlobal.setValue(sMsgPanelId, this);
}

function toggleErrors(event) {
	var sMsgPanelId = document.getElementsByClassName("jwf-msg-panel")[0].id;
	var sTest = $(event.target).closest('table').attr('id');

	var oErrPanel = PageCtrl.pageGlobal.getValue(sMsgPanelId).ErrPanel;
	var oMsgPanelControl = PageCtrl.pageGlobal.getValue(sMsgPanelId).MsgControl;
	var sDisplay = oErrPanel.style.display;

	/* if message panel is disabled means errors are zero ignore click event */
	var sState = oMsgPanelControl.getAttribute('data-toggle-state');
	if (sState === 'disabled') {
		return false;
	}

	/* Toggle display of error panel */
	oErrPanel.style.display = (sDisplay === 'block') ? 'none' : 'block';
	oMsgPanelControl.innerHTML = (sDisplay === 'block') ? PageCtrl.pageGlobal.getValue('SHOW-ERR-TEXT') : PageCtrl.pageGlobal.getValue('HIDE-ERR-TEXT');
	oMsgPanelControl.setAttribute('data-toggle-state', (sDisplay === 'block') ? 'off' : 'on');
}

function LoadErrorList(sMsgPanelId) {
	//var sMsgPanelId = document.getElementsByClassName("jwf-msg-panel")[1].id;
	var oMsgPanel = PageCtrl.pageGlobal.getValue(sMsgPanelId).MsgPanel;
	var oMsgPanelControl = PageCtrl.pageGlobal.getValue(sMsgPanelId).MsgControl;

	var aErrorList = PageCtrl.pageGlobal.getValue(sMsgPanelId).ErrorList;
	var nNoOfErrors = aErrorList.length;
	var errTableBody = PageCtrl.pageGlobal.getValue(sMsgPanelId).ErrTable;
	var errRow, errCell;

	// Check if no of errors is zero and not disabled then disable it
	if (nNoOfErrors === 0) {
		if (oMsgPanelControl.getAttribute('data-toggle-state') !== 'disabled') {
			oMsgPanelControl.innerHTML = PageCtrl.pageGlobal.getValue('NO-ERR-TEXT');
			oMsgPanelControl.setAttribute('data-toggle-state','disabled');
			oMsgPanelControl.style.cursor = 'not-allowed';
			oMsgPanel.style.backgroundColor = PageCtrl.pageGlobal.getValue('NO-ERR-BG');
			PageCtrl.pageGlobal.getValue(sMsgPanelId).ErrPanel.style.display = 'none';
		}
	// else if errors are not zero and it ia disabled then make it as show errors
	} else if (oMsgPanelControl.getAttribute('data-toggle-state') === 'disabled') {
			oMsgPanelControl.setAttribute('data-toggle-state','off');
			oMsgPanelControl.style.cursor = 'pointer';
			oMsgPanelControl.innerHTML = PageCtrl.pageGlobal.getValue('SHOW-ERR-TEXT');
			oMsgPanel.style.backgroundColor = PageCtrl.pageGlobal.getValue('HAS-ERR-BG');
	}

	oMsgPanel.rows[0].cells[1].innerHTML = PageCtrl.pageGlobal.getValue(sMsgPanelId).TitleMsg;
	$("#" + errTableBody.id).empty();
	// non zero update error panel with latest error list
	for (i = 0; i < aErrorList.length; i++) {
		errwRow = errTableBody.insertRow(-1);

		errCell = errwRow.insertCell(-1);
		errCell.innerHTML = aErrorList[i].Object;

		errCell = errwRow.insertCell(-1);
		errCell.innerHTML = aErrorList[i].Field;

		errCell = errwRow.insertCell(-1);
		errCell.innerHTML = aErrorList[i].ErrorText;
	}
}

//Key, Object, Field, ErrorText - client side
//ErrorCode, Object, Field, ErrorText - coming from server side

/**************************************************************************
DataGrid: Key class for handling all Grids on the page
***************************************************************************/
function DataGrid(nGridType, sGridId, cbGridClick, oSearchOptions, lLookupGrid) {
	// Initialise instance properties
	this.GridType = nGridType;
	this.GridId = sGridId;
	this.GridDomObj = document.getElementById(sGridId);
	this.GridClick = (typeof cbGridClick === 'function') ? cbGridClick : undefined;
	this.SearchOptions = (oSearchOptions) ? oSearchOptions : { "search": false, "searchButton": null };
	this.DataList = [];
	this.isLookupGrid = (lLookupGrid) ? true : false;
	this.SelectedIndex = null;

	// public interfaces to closure functions
	this.setColumnsInfo = function(oInstanceDataGrid) {
		if (oInstanceDataGrid instanceof DataGrid) {
			if (oInstanceDataGrid.isLookupGrid) { oInstanceDataGrid.ColumnsInfo = getColumnsInfo(oInstanceDataGrid); }
		}
	}

	this.jwfGridClick = function(oRow) {
		jwfGridClick(oRow);
	}

	this.clearRows = function() {
		$('#' + this.GridId).find("tr:not(:first)").remove();
		this.SelectedIndex = null;
	}

	this.hasRows = function() {
		var nNoOfRows = this.GridDomObj.rows.length;
		return (nNoOfRows > 1);
	}

	this.getGridRow = function(nWhich) {
		var nNoOfRows = this.GridDomObj.rows.length, oRow = null;

		nWhich = (this.hasRows()) ? (nWhich > 2) ? 2 : nWhich : -1;

		switch(nWhich) {
			case 0:
				oRow = this.GridDomObj.rows[this.SelectedIndex];
				break;
			case 1:
				oRow = his.GridDomObj.rows[1];
				break;
			case 2:
				oRow = this.GridDomObj.rows[nNoOfRows];
				break;
		}
		return oRow;
	}

	this.deleteGridRow = function() {
		var nRowToDelete = this.SelectedIndex;
		var nNextRow = (this.GridDomObj.rows.length < 2) ? -1 : (nRowToDelete < 2) ? 1 : (nRowToDelete - 1);

 		EditDataTab.jwfDataGridClick(this, this.GridDomObj.rows[nRowToDelete], this.GridDomObj.rows[nNextRow]);
		this.GridDomObj.deleteRow(nRowToDelete);

		if (nNextRow < 0) {
			this.SelectedIndex = null;
		}
	}

	// If not lookup grid then call closure function immidiatley (means it is static template)
	if (!this.isLookupGrid) {
		this.ColumnsInfo = getColumnsInfo(this);
		PageCtrl.ColumnsInfo[sGridId] = this.ColumnsInfo;
	}

	$("#" + sGridId + " tbody").delegate("tr", "click", function () {
		jwfGridClick(this);
	});

	if (this.isLookupGrid) {
		$("#" + sGridId + " tbody").delegate("tr", "dblclick", function () {
			jwfGridDblClick(this);
		});
	}

	$(this.GridDomObj).on("keydown", jwfGridKeyDown);
	$(this.GridDomObj).on("keyup", jwfGridKeyUp);

	//this.GridDomObj.tBodies[0].addEventListener("keydown", jwfGridKeyDown, false);
	//this.GridDomObj.tBodies[0].addEventListener("keyup", jwfGridKeyUp, false);

	// Search options valid for grid types 0,2,3 only
	if ([1,4,5].indexOf(nGridType) > 0) {
		this.SearchOptions = { "search": false, "searchButton": null };
	}
	// Attach click event to search button, add form reference to this search option JSON object
	if (this.SearchOptions.search === true) {
		oSearchBtn = document.getElementById(this.SearchOptions.searchButton);
		oSearchBtn.addEventListener("click", jwfSearchBtnClick);

		aElements = $("#" + this.SearchOptions.searchButton).parents('form:first');
		oSearchForm = (aElements.length > 0) ? aElements[0] : undefined;
		this.SearchOptions.searchForm = oSearchForm;

		PageCtrl.setObjectById(this.SearchOptions.searchButton, this);	// add to global by serach button key
	}

	// Add self reference to page controller
	PageCtrl.setObjectById(sGridId, this);  							// by tabel id key

	// closure function to create columns info
	function getColumnsInfo(oDataGrid) {
		/*  Initialise overall object instance */
		var oGridHeader = oDataGrid.GridDomObj.tHead;
		var aHeadCells = oGridHeader.rows[0].cells;
		var nNoOfColumns = aHeadCells.length;
		var aDataAttributes = [], aColumnsInfo = [], oSearchBtn = {};

		// Make copy of columns to array to data loading will be very fast
		// { id, domobject, xtype, attributes: { <attribute>, <value> } }
		for (var i = 0; i < nNoOfColumns; i++) {
			var oColumnInfo = {};
			oColumnInfo.id = null;               // used only for form kept for rare cases
			oColumnInfo.domObject = undefined;   // used only for form kept for rare cases
			oColumnInfo.xtype = aHeadCells[i].getAttribute('data-xtype');
			oColumnInfo.attributes = UTIL.getDataAttributes(aHeadCells[i].getAttribute('data-attributes'));
			isComboInput = false;               // enhance later once EditDataGrid issues taken up
			isChangeOnSelection = false;			// enhance later once EditDataGrid issues taken up

			aColumnsInfo.push(oColumnInfo);
		}
		if (oDataGrid.GridType === jwfGlobal.GRID_TYPE_DIALOG_MULTI) {
			aColumnsInfo[0].xtype = 'multi-select';
			aHeadCells[0].style.margin = 'auto';
			aHeadCells[0].innerHTML = '<input style="flex: 1; height: 18px; width: 18px;" type=checkbox>';
			aHeadCells[0].querySelector("input[type='checkbox']").addEventListener("click", jwfMultiGridSelectAll, false);
		}
		return aColumnsInfo;
	}

	function jwfMultiGridSelectAll (oEvent) {
		var oDataGrid = PageCtrl.getObjectById($(oEvent.target).closest('table').attr('id'));
		var lChecked = oEvent.target.checked, oHtmlTable = oDataGrid.GridDomObj;
		var nNoOfRows = oHtmlTable.rows.length;

		$('td input:checkbox',oDataGrid.GridDomObj).prop('checked', lChecked);
		Lookup.ActiveObject.SelectedData = [];

		if (lChecked) {
			for (var i = 1; i < nNoOfRows; i++) {
				var oCheckbox = oHtmlTable.rows[i].cells[0].querySelector('input[type=checkbox]');
				Lookup.ActiveObject.SelectedData.push( { 'Code' : oCheckbox.value, 'Text': UTIL.replaceAll(oHtmlTable.rows[i].cells[1].innerHTML,'&nbsp;','') } );
			}
		}
	}

	// closure function to handle all grid clicks
	function jwfGridClick(oRow) {
		var oDataGrid = PageCtrl.getObjectById($(oRow).closest('table').attr('id'));
		var sRow = oRow.cells[0].innerHTML, nGridType = oDataGrid.GridType, nSeletedRow = oDataGrid.SelectedIndex, oPreviousRow = null;

		// To avoid setfocus to non-active grids by markRowSelectedMethod()
		//PageCtrl.FocusGrid = oDataGrid.GridId;

		// for data navigation grids all logic taken care by EditDataTab object [ (4,5) (DataListGrid, DataEditGrid) ]
		if (nGridType > 3) {
			oPreviousRow = (nSeletedRow) ? oDataGrid.GridDomObj.rows[nSeletedRow] : null;
			EditDataTab.jwfDataGridClick(oDataGrid, oPreviousRow, oRow);
			return;
		}

		// Rest is taken here [ (0, 1, 2, 3) (BrowseGrid, ComboBoxGrid, SingleSelectGrid, MultiSelectGrid) ]
		DataGrid.markRowSelected(oRow);
		if ((!oDataGrid.isLookupGrid) && (oDataGrid.GridClick)) { oDataGrid.GridClick(oRow, oDataGrid); }
	}

	function jwfGridDblClick(oRow) {
		var oDataGrid = PageCtrl.getObjectById($(oRow).closest('table').attr('id'));
		//if (oDataGrid.isLookupGrid) { Lookup.onSelect(oDataGrid.GridDomObj.rows[oDataGrid.SelectedIndex]); }
		if (oDataGrid.isLookupGrid) { if (Lookup.ActiveObject.isCombo) { Lookup.onSelect(oDataGrid.GridDomObj.rows[oDataGrid.SelectedIndex]); } }
	}

	// closure function to handle all search button clicks
	function jwfSearchBtnClick() {
		var oSearchBtn = this, aApiData = [], oDataGrid = PageCtrl.getObjectById(oSearchBtn.id);

		aApiData.push( JSON.stringify(convertToJson(oDataGrid.SearchOptions.searchForm.id)) );

		ApiDataService( UTIL.getApiDataInWrap(PageCtrl.ComponentId, jwfGlobal.API_EVT_SBSEARCH_CLICK, aApiData), jwfFillSearchViewData, -999, { CustomMsg: jwfGlobal.API_DEFAULT_MSG });

		function jwfFillSearchViewData(nResult, oApiResponse, nEvtId, oEvtInfo) {
			var lApiCallFailed = (nResult < 0) ? true : (oApiResponse.Result === 1) ? false : true;
			var aSBViewList = [];

			if (!lApiCallFailed) {
				aSBViewList = JSON.parse(oApiResponse.dataOutPacket[0], JSON.dateParser);
				PageCtrl.getObjectById(PageCtrl.SBGridId).DataList = aSBViewList;
				UIManager.fillViewGrid(PageCtrl.SBGridId, aSBViewList);
			}
		}
	}

	// Inner key event hanlders to handle all key events of all types of grids
	function jwfGridKeyDown(oEvent) {
		return processGridKey(oEvent, true);   // true means called from keydown
	}

	function jwfGridKeyUp(oEvent) {
		return processGridKey(oEvent, false); // false means not called from keydown
	}

	// Inner function to process all key events of all types of all types of grids
	function processGridKey(oEvent, lKeyDown) {
		var nKey = oEvent.keyCode;  oDataGrid = null, oHtmlTable = null, nNewRowNo = 0;
		var lPreventDefault = [9,13,27,32,33,34,35,36,38,40].indexOf(nKey) > -1;
		var oWindowScrollPos = { 'x': window.scrollX, 'y': window.scrollY };

		if (!lPreventDefault) { return true; }                   // allow normal behavior if not our special key
		if (lPreventDefault && (!lKeyDown)) { return false;	}  // Ignore keyup event for our special keys

		oDataGrid = PageCtrl.getObjectById($(oEvent.target).closest('table').attr('id'));
		oHtmlTable = oDataGrid.GridDomObj;

		// handle keys datagrid need to trap and avoid browse to handle standard way
		switch(nKey) {
			case -9:
				if (oDataGrid.isLookupGrid) { if (Lookup.ActiveObject.isCombo) { Lookup.ActiveObject.Target.focus(); } }
				break;
			case 13:
			case 9:
				if (oDataGrid.isLookupGrid) { if (Lookup.ActiveObject.isCombo) { Lookup.onSelect(oHtmlTable.rows[oDataGrid.SelectedIndex]); } }
				break;
			case 27:
				if (oDataGrid.isLookupGrid) { if (Lookup.ActiveObject.isCombo) { Lookup.ComboBox.dispose(false, false, true); } }
				break;
			case 32:
				if (oDataGrid.GridType	=== jwfGlobal.GRID_TYPE_DIALOG_MULTI) {
					var oCheckbox = oHtmlTable.rows[oDataGrid.SelectedIndex].querySelector('input[type=checkbox]'), lToggleChecked = true;

					if (oCheckbox) {
						lToggleChecked = (oCheckbox.checked) ? false : true;
						oCheckbox.checked = lToggleChecked;
					}
					Lookup.Dialog.onSelectRow(oHtmlTable.rows[oDataGrid.SelectedIndex], oCheckbox);
				}
				break;
			case 33:
				nNewRowNo = (oDataGrid.SelectedIndex - 10);
				nNewRowNo = (nNewRowNo < 0) ? 1 : nNewRowNo;
				//oDataGrid.SelectedIndex = nNewRowNo;
				jwfGridClick(oHtmlTable.rows[nNewRowNo]);
				break;
			case 34:
				nNewRowNo = (oDataGrid.SelectedIndex + 10);
				nNewRowNo = (nNewRowNo > oHtmlTable.rows.length - 1) ? oHtmlTable.rows.length - 1 : nNewRowNo;
				//oDataGrid.SelectedIndex = nNewRowNo;
				jwfGridClick(oHtmlTable.rows[nNewRowNo]);
				break;
			// end
			case 35:
				nNewRowNo = oHtmlTable.rows.length - 1;
				//oDataGrid.SelectedIndex = oHtmlTable.rows.length - 1;
				jwfGridClick(oHtmlTable.rows[nNewRowNo]);
				break;
			// home
			case 36:
				nNewRowNo = 1;
				//oDataGrid.SelectedIndex = 1;
				jwfGridClick(oHtmlTable.rows[nNewRowNo]);
				break;
			// up arrow
			case 38:
				if (oDataGrid.SelectedIndex > 1) {
					nNewRowNo = oDataGrid.SelectedIndex -1;
					//oDataGrid.SelectedIndex = oDataGrid.SelectedIndex -1;
					jwfGridClick(oHtmlTable.rows[nNewRowNo]);
				}
				break;
			// down arrow
			case 40:
				if (oDataGrid.SelectedIndex < oHtmlTable.rows.length - 1) {
					nNewRowNo = oDataGrid.SelectedIndex + 1;
					//oDataGrid.SelectedIndex = oDataGrid.SelectedIndex + 1;
					jwfGridClick(oHtmlTable.rows[nNewRowNo]);
				}
				break;
			default:
		}
		window.scrollTo(oWindowScrollPos.x, oWindowScrollPos.y);
		return false;
	}
}

DataGrid.markRowSelected = function(oRow) {
	var oDataGrid = PageCtrl.getObjectById($(oRow).closest('table').attr('id'));

	if (!oDataGrid) { return; }

	if ((PageCtrl.FocusGrid === oDataGrid.GridId) || (oDataGrid.isLookupGrid)) {
		oRow.focus();
	}
	$(oRow).addClass(jwfGlobal.CSS_CLASS_ROW_SELECTED).siblings().removeClass(jwfGlobal.CSS_CLASS_ROW_SELECTED);
	oDataGrid.SelectedIndex = oRow.rowIndex;
}

function convertToJson1(sFormId) {
    var jsonData = {};
    //var formData = $("#" + formId).serializeArray();
    var formData = UIManager.getFormInputs(sFormId);

    $.each(formData, function () {
        if (jsonData[this.name]) {
            if (!jsonData[this.name].push) { jsonData[this.name] = [jsonData[this.name]]; }
            jsonData[this.name].push(this.value || '');
        } else {
            jsonData[this.name] = this.value || '';
        }
    });
    return jsonData;
}

function convertToJson(sFormId) {
	var oJsonData = {}, aFormInputs = UIManager.getFormInputs(sFormId);
	var nNoOfElements = aFormInputs.length;

	for (var i = 0; i < nNoOfElements; i++) {
		if ((aFormInputs[i].getAttribute('data-xtype') === 'date') && (aFormInputs[i].value === '')) {
			oJsonData[aFormInputs[i].name] = jwfGlobal.DATE_ENTRY_YEAR_RANGE.MinDate;
		} else { oJsonData[aFormInputs[i].name] = aFormInputs[i].value; }
	}
   return oJsonData;
}

var Lookup = {
	ActiveObject: null,
	setup: function() {
		var aInputs = [], nNoOfInputs = 0;
		var sImgElement = "<img data-xtarget='%id%' src='./Content/find.png'>"

		// Create global instances lookup grid objects
		PageCtrl.setObjectById(jwfGlobal.COMBO_BOX_GRID, new DataGrid(jwfGlobal.GRID_TYPE_COMBO_BOX, jwfGlobal.COMBO_BOX_GRID, null, null, true));
		PageCtrl.setObjectById(jwfGlobal.LOOKUP_DIALOG_GRID, new DataGrid(jwfGlobal.GRID_TYPE_DIALOG_MULTI, jwfGlobal.LOOKUP_DIALOG_GRID, null, null, true));

		// Setup combo box text input to trap key codes
		$("input[data-xtype*='combo']").on("keydown", Lookup.ComboBox.onKeyDown);
		$("input[data-xtype*='combo']").on("keyup", Lookup.ComboBox.onKeyUp);
		$("input[data-xtype*='combo']").on("input", Lookup.ComboBox.onInput);

		aInputs = $("input[data-xtype*='combo']");
		nNoOfInputs = aInputs.length;
		for (var i = 0; i < nNoOfInputs; i++) {
			var oComboLabel = document.querySelector("label[for='" + aInputs[i].id +"']");
			var lComboParentIsRow = (oComboLabel.parentNode.className === jwfGlobal.CSS_CLASS_ROW_LAYOUT);

			var oDataAttributes = UTIL.getDataAttributes(aInputs[i].getAttribute('data-attributes'));
			var sAddMaster = oDataAttributes['add-master-id'];
			var sAddImg = (sAddMaster) ? "<img data-xtarget='%id%' src='./Content/addmaster.png'>".replace('%id%', aInputs[i].id) : "";

			aInputs[i].placeholder = jwfGlobal.LIST_PLACEHOLDER_TEXT;
			aInputs[i].setAttribute('data-xvalue','-1');

			// Add lookup image to all remote combo elements and attach event handlers to label & image elements
			if (aInputs[i].getAttribute('data-xtype').indexOf('remote') > -1) {
				if (lComboParentIsRow) {
					$(aInputs[i]).after( sImgElement.replace('%id%', aInputs[i].id) + sAddImg );
				} else {
					oComboLabel.innerHTML = oComboLabel.innerHTML + sImgElement.replace('%id%', aInputs[i].id) + sAddImg;
				}
				$(oComboLabel).on('click', function(oEvent) { oEvent.preventDefault(); });
			}
		}
		$('img[data-xtarget]').on('click', function(oEvent) {
			var oTarget = document.getElementById(oEvent.target.getAttribute('data-xtarget'));

			var oDataAttributes = UTIL.getDataAttributes(oTarget.getAttribute('data-attributes'));
			var sImgSrc = oEvent.target.src;

			oEvent.preventDefault();

			if ( (sImgSrc.indexOf('addmaster') > -1) && (PageCtrl.EventCallBacks.onAddMasterClick) ) {
				PageCtrl.EventCallBacks.onAddMasterClick(oTarget);
				//PageCtrl.appMessage('Cliked Add Master Icon ' + oTarget.id, "Add '" + oDataAttributes['add-master-id'] + "' dialog will be promoted here", false);
			} else {
				Lookup.Dialog.promote(oTarget);
			}
		});
	},
	resetAsUnselected: function(oTarget) {
		var lIsOkToReset = ( (oTarget.type.toLowerCase().indexOf('multiple') < 0) && (!Lookup.ActiveObject) ) ? true : false;

		if (lIsOkToReset) {
			oTarget.setAttribute('data-xvalue','-1');
			oTarget.value = '';
			EditDataTab.jwfFormChange(oTarget, true);
		}
	},
	/* Pradeep 02/05/2017 - _old replaced as Fix for chrome (v49) XP version, now it works for v49 or above */
	switchState: function(oTarget, lEnable) {
		var oComboLabel = document.querySelector("label[for='" + oTarget.id +"']");

		/* 'Children' syntax is for column layouts combo boxes and 'next' is for row layouts combo boxes image elements */
		if (lEnable) {
			$(oComboLabel).children('img').each( function() { this.removeAttribute('disabled'); });
			$(oTarget).next('img').each( function() { this.removeAttribute('disabled'); });
			oTarget.removeAttribute('disabled');
		} else {
			$(oComboLabel).children('img').each( function() { this.setAttribute('disabled', 'disabled'); });
			$(oTarget).next('img').each( function() { this.setAttribute('disabled', 'disabled'); });
			oTarget.setAttribute('disabled', 'disabled');
		}
	},
	switchState_old: function(oTarget, lEnable) {
		var oParent = oTarget.parentNode;
		var aImgElements = oParent.querySelectorAll("img[data-xtarget='" + oTarget.id + "']");

		if (lEnable) {
			aImgElements.forEach ( function(oElement, nIndex) { oElement.removeAttribute('disabled'); });
			oTarget.removeAttribute('disabled');
		} else {
			aImgElements.forEach ( function(oElement, nIndex) { oElement.setAttribute('disabled', 'disabled'); });
			oTarget.setAttribute('disabled', 'disabled');
		}
	},
	getData: function(lPromoteCall, oDialogForm) {
		var oDataGrid = Lookup.ActiveObject.DataGrid, oLookupParms = {}, oLookupForm = {};
		var sUserInput = '', nUserTextLength = 0, nLookupType = Lookup.ActiveObject.Type, sLookupObject = '', sLookupContext = '';
		var lAdjustUserInput = false, oDataAttributes = UTIL.getDataAttributes(Lookup.ActiveObject.Target.getAttribute('data-attributes'));
		var lCallApi = (nLookupType === jwfGlobal.LOOKUP_TYPE_LOCAL_COMBO) ? false : true;

		// Adjust for if parameter not passed then it is false
		lPromoteCall = (lPromoteCall) ? true : false;

		// Orginal length + '%' returns zero so truncate characters from end
		if (!Lookup.ActiveObject.isMultiSelect) {
			sUserInput = Lookup.ActiveObject.Target.value;
			lAdjustUserInput = ((sUserInput === Lookup.ActiveObject.OrgValue.Text) && (sUserInput.length > 0));

			if (lAdjustUserInput) {
				nUserTextLength = sUserInput.length;
				sUserInput = (nUserTextLength > 30) ? sUserInput.substring(0, 29) : sUserInput;
			}
		}

		sLookupObject = (oDataAttributes['remote-list']) ? oDataAttributes['remote-list'] : 'null';
		sLookupObject = ( sLookupObject.indexOf(jwfGlobal.DYNAMIC_ATTRIBUTE) > -1 ) ? PageCtrl.EventCallBacks.getDynAttribute(Lookup.ActiveObject.Target, 'remote-list') : sLookupObject;

		Lookup.ActiveObject.ExcludeRowIds = ((lPromoteCall) && (lCallApi)) ? this.getExcludeRowIds(Lookup.ActiveObject.Target) : Lookup.ActiveObject.ExcludeRowIds;

		switch(nLookupType) {

			// local combo box - get data list locally from page controller and make got data call
			case jwfGlobal.LOOKUP_TYPE_LOCAL_COMBO:
				if (lPromoteCall) {
					oDataGrid.GridDomObj.tHead.rows[0].innerHTML = jwfGlobal.LOCAL_COMBO_BOX_COLUMNS;
					oDataGrid.setColumnsInfo(oDataGrid);
					oDataGrid.DataList = PageCtrl.getDataList(oDataAttributes['local-list']);
					Lookup.setDataRecords(oDataGrid.DataList);
					this.gotData(null, nLookupType, null);
				}
				break;

			case jwfGlobal.LOOKUP_TYPE_REMOTE_COMBO:
				oLookupForm = { 'UserInputText': sUserInput, 'Parm1': null, 'Parm2': null, 'Parm3': null, 'Parm4': null, 'Parm5': null };
				sLookupContext = oDataAttributes['combo-context'];

				oLookupParms = { 'LookupObjectName': sLookupObject, 'LookupContext': sLookupContext,
						'ExcludeRowIds': Lookup.ActiveObject.ExcludeRowIds, 'RequestedPageNo': 1, 'PromoteCall': lPromoteCall, 'LookupForm': oLookupForm };
				lCallApi = isDataExistLocally(oLookupParms) ? false : true;
				break;
			case jwfGlobal.LOOKUP_TYPE_DIALOG:
				var nPageNo = 0;
				if (lPromoteCall) {
					oLookupForm = { 'UserInputText': sUserInput, 'Parm1': null, 'Parm2': null, 'Parm3': null, 'Parm4': null, 'Parm5': null };
					nPageNo = 1;
				} else {
					oLookupForm = oDialogForm;
					nPageNo = oDialogForm.pageno;
				}

				sLookupContext = oDataAttributes['dialog-context'];
				oLookupParms = { 'LookupObjectName': sLookupObject, 'LookupContext': sLookupContext,
						'ExcludeRowIds': Lookup.ActiveObject.ExcludeRowIds, 'RequestedPageNo': nPageNo, 'PromoteCall': lPromoteCall, 'LookupForm': oLookupForm };
				break;
		}

		/* Pradeep 21/05/2016 - Below is API call preventation if user types too fast in combo box before API can return results, controlled by ApiCallStatus as
			(1) Remote combo box, call is active, so ignore another call
			(0) Remote combo box, call is not active, so make another call
			(0) Used by Lookup Dialog, User can click previous and next buttons too fast before last API call not completed
		*/
		if ( (lCallApi) && (Lookup.ActiveObject.ApiCallStatus !== 1) ) {
			// Set Api status flag to prevent another call before last one is completed
			Lookup.ActiveObject.ApiCallStatus = (Lookup.ActiveObject.ApiCallStatus === 0) ? 1 : Lookup.ActiveObject.ApiCallStatus;

			// Pradeep 27/01/2017 - Quick dirty extend for any future page with too may custom filters (parm1 .. parm5 can be set by user code as needed)
			invokeSetupCustomFilterEvent(Lookup.ActiveObject.Target, oLookupParms);

			ApiDataService( { "Component" : jwfGlobal.API_LOOKUP_COMPONENT_ID, "Context" : 0, "dataInPacket": [ JSON.stringify(oLookupParms) ] },
					gotLookupData, 99, { 'PromoteCall': lPromoteCall, 'LookupType': nLookupType } );
		}

		/* Pradeep 14/02/2018 - local search done using JS indexOf() instead of regexp as user want to type '*\' chars in user input, reg exp gives error */
		function isDataExistLocally(oLookupParms) {
			var lCheckDataLocally = (Lookup.ActiveObject.Data) ? true : false, aLocallyFilteredData = [], lDataExistLocally = false;
			var oLookupData = {}, sUserInput = oLookupParms.LookupForm.UserInputText.toUpperCase();
			var lContainsFilter = (Lookup.ActiveObject.DataAttributes['filter-type'] === 'C') ? true : false;

			if (lCheckDataLocally) {
				aLocallyFilteredData = Lookup.ActiveObject.Data.DataRecords.filter( function(oDataRecord) {
					return (lContainsFilter) ? ((oDataRecord.Text.toUpperCase().indexOf(sUserInput) > -1) ? true : false) : (oDataRecord.Text.toUpperCase().startsWith(sUserInput));
				});

				if (aLocallyFilteredData.length > 0) {
					lDataExistLocally = true;

					oLookupData.LookupData = aLocallyFilteredData;

					oLookupData.ActualPageNo = Lookup.ActiveObject.LastApiResult.ActualPageNo;
					oLookupData.PageSize = Lookup.ActiveObject.LastApiResult.PageSize;
					oLookupData.ReturnedRecords = Lookup.ActiveObject.LastApiResult.ReturnedRecords;
					oLookupData.TotalRecords = Lookup.ActiveObject.LastApiResult.TotalRecords;
					oLookupData.NoOfPages = Lookup.ActiveObject.LastApiResult.NoOfPages;

					Lookup.ComboBox.gotData(oLookupData, jwfGlobal.LOOKUP_TYPE_REMOTE_COMBO,  { PromoteCall: false } );
				}
			}
			return lDataExistLocally;
		}

		function invokeSetupCustomFilterEvent(oTarget, oLookupParms) {
			var oFormObjects = UIManager.getObjectsByTarget(oTarget);
			var aLookupTypes = ['L','C','D'], aSysTypes = [jwfGlobal.LOOKUP_TYPE_LOCAL_COMBO,jwfGlobal.LOOKUP_TYPE_REMOTE_COMBO,jwfGlobal.LOOKUP_TYPE_DIALOG];
			var sLookupType = aLookupTypes[ aSysTypes.indexOf(Lookup.ActiveObject.Type) ];
			var oCustomizeContext = { 'LookupObjectName': oLookupParms.LookupObjectName, 'LookupContext': oLookupParms.LookupContext, 'LookupType': sLookupType };

			// Reset all custom parameters to null values
			oLookupParms.LookupForm = { 'UserInputText': oLookupParms.LookupForm.UserInputText, 'Parm1': null, 'Parm2': null, 'Parm3': null, 'Parm4': null, 'Parm5': null };

			// If defined call component's custom filter logic, which is suppose to modify LookupForm object sent to it by reference
			if (PageCtrl.EventCallBacks.setCustomLookupFilter) {
				PageCtrl.EventCallBacks.setCustomLookupFilter(Lookup.ActiveObject.Target, oLookupParms.LookupForm, oFormObjects.TemplateEntry.FormData, oCustomizeContext);
				'LookupObjectName,LookupContext'.split(',').forEach( function(sKey) { oLookupParms[sKey] = oCustomizeContext[sKey]; });
			}
		}

		function gotLookupData(nResult, oApiResponse, nEvtId, oEvtInfo) {
			var lTechnicalError = (nResult === -1) ? true : false, oLookupResult = null;
			var lLogicalError = (lTechnicalError) ? false : (oApiResponse.Result !== 1) ? true : false;

			if (!Lookup.ActiveObject) { return; }  // Happens when user aborted by esc key press - before API result comes back

			// Reset Api status flag to allow another call
			Lookup.ActiveObject.ApiCallStatus = (Lookup.ActiveObject.ApiCallStatus === 1) ? 0 : Lookup.ActiveObject.ApiCallStatus;

			if ((lTechnicalError) || (lLogicalError)) {
				if (Lookup.ActiveObject.isCombo) { Lookup.ComboBox.dispose(false, true); }
				else {
					PageCtrl.appMessage(jwfGlobal.API_STD_EXCEPTION_MSG, PageCtrl.debug.debugLog);
					Lookup.Dialog.dispose(true);
				}
			} else {
				oLookupResult = JSON.parse(oApiResponse.dataOutPacket[0], JSON.dateParser);
				Lookup.setDataRecords(oLookupResult.LookupData, oLookupResult);
				if (Lookup.ActiveObject.isCombo) { Lookup.ComboBox.gotData(oLookupResult, nLookupType, oEvtInfo); }
				else { Lookup.Dialog.gotData(oLookupResult, oEvtInfo); }
			}
		}
	},
	setDataRecords: function(aDataRecords, oApiResult) {
		if (Lookup.ActiveObject) {
			Lookup.ActiveObject.Data = { DataRecords: aDataRecords, SelectedDataRecords: [] }
			Lookup.ActiveObject.LastApiResult = oApiResult;
		}
	},
	clearDataRecords: function() {
		if (Lookup.ActiveObject) { Lookup.ActiveObject.Data = { DataRecords: [], SelectedDataRecords: [] } }
	},
	setSelectedDataRecord: function(sRowId) {
		var aDataRecords = Lookup.ActiveObject.Data.DataRecords;

		if (!Array.isArray(aDataRecords)) { return; }

		aDataRecords.some( function(oDataRecord, nIndex) {
			if (oDataRecord.Code.toString() === sRowId) {
				Lookup.ActiveObject.Data.SelectedDataRecords.push(nIndex);
				return true;
			} else {
				return false;
			}
		});
	},
	getSelectedDataRecords: function() {
		var aSelectedDataRecords = [], aDataRecords = Lookup.ActiveObject.Data.DataRecords;

		if (!Array.isArray(aDataRecords) || !Array.isArray(Lookup.ActiveObject.Data.SelectedDataRecords)) { return aSelectedDataRecords; }
		Lookup.ActiveObject.Data.SelectedDataRecords.forEach( function(nSelectedIndex, nIndex) { aSelectedDataRecords.push(aDataRecords[nSelectedIndex]); });
		return aSelectedDataRecords;
	},
	getExcludeRowIds: function(oTarget) {
		var lMultiSelect = (oTarget.type.toLowerCase().indexOf('multiple') > -1);
		var aMultiData = [], aRowIds = [], aCustomRowIds = [], sExcludeRowsIds = '';
		var oFormObjects = UIManager.getObjectsByTarget(oTarget);

		aCustomRowIds = (PageCtrl.EventCallBacks.beforeLookupFilter)
			? PageCtrl.EventCallBacks.beforeLookupFilter(oTarget, oFormObjects.TemplateEntry.FormData) : [];

		if (lMultiSelect) {
			aMultiData = MultiSelect.getData(oTarget);
			aMultiData.forEach( function(oData, nIndex) { aRowIds.push(oData.Code) });
		}

		aRowIds = (aCustomRowIds.length < 1) ? aRowIds : aRowIds.concat(aCustomRowIds);
		sExcludeRowsIds = (aRowIds.length < 1) ? '-1' : UTIL.getArrayToString(aRowIds, ',');

		return sExcludeRowsIds;
	},
	gotData: function(oLookupResult, nLookupType, oEventInfo) {
		if (Lookup.ActiveObject.isCombo) { Lookup.ComboBox.gotData(oLookupResult, nLookupType, oEventInfo); }
		else { Lookup.Dialog.gotData(oLookupResult, oEventInfo); }
	},
	getSearchResultText(oLookupResult, oPageNo) {
		var sDialogResults = 'Showing {ReturnedRecords} / {TotalRecords} Records. &nbsp;&nbsp;&nbsp;  No Of Pages {NoOfPages}';
		var sComboResults = '<BR>Records {ReturnedRecords} / {TotalRecords}&nbsp &nbsp Page {ActualPageNo} / {NoOfPages}';
		var sResultText = ''; aSearchControls = [], lDisablePageControls = false;

		if (Lookup.ActiveObject.isCombo) {
			sResultText = sComboResults.replace('{ActualPageNo}', oLookupResult.ActualPageNo)
		} else {
			sResultText = sDialogResults;
			oPageNo.value = oLookupResult.ActualPageNo;
		}

		// Write data to active object
		Lookup.ActiveObject.ReturnedRecords = oLookupResult.ReturnedRecords;
		Lookup.ActiveObject.TotalRecords = oLookupResult.TotalRecords;
		Lookup.ActiveObject.ActualPageNo = oLookupResult.ActualPageNo;
		Lookup.ActiveObject.NoOfPages = oLookupResult.NoOfPages;

		sResultText = sResultText.replace('{ReturnedRecords}', oLookupResult.ReturnedRecords);
		sResultText = sResultText.replace('{TotalRecords}', oLookupResult.TotalRecords);
		sResultText = sResultText.replace('{NoOfPages}', oLookupResult.NoOfPages);

		// isCombo false means it is dialog so search controls need to be managed
		if (!Lookup.ActiveObject.isCombo) {
			aSearchControls = Modal.Header.querySelectorAll('span[data-xtype="search-controls"');
			lDisablePageControls = (parseInt(oLookupResult.NoOfPages) < 2);

			if (lDisablePageControls) { oPageNo.setAttribute('disabled', 'disabled'); }
			else { oPageNo.removeAttribute('disabled'); }

			for (var i = 0; i < aSearchControls.length; i++) {
				if ((lDisablePageControls) && (aSearchControls[i].getAttribute('name') !== 'search')) {
					aSearchControls[i].setAttribute('data-xdisabled', 'Y');
				} else {
					aSearchControls[i].removeAttribute('data-xdisabled');
				}
			}
		}
		return sResultText;
	},
	onSelect: function(oRow) {
		// Selected values for local combo, remote combo and single dialog lookup processed here
		if (!Lookup.ActiveObject.isMultiSelect) {
			Lookup.ActiveObject.Target.value = oRow.cells[1].innerHTML;
			Lookup.ActiveObject.Target.setAttribute('data-xvalue', oRow.cells[0].innerHTML);
			Lookup.setSelectedDataRecord(oRow.cells[0].innerHTML);

		} else { MultiSelect.addSelectedData(Lookup.ActiveObject.Target); }

		// call user's form change and if return true user's lookup select
		if (EditDataTab.jwfFormChange(Lookup.ActiveObject.Target, true)) {
			if (Lookup.ActiveObject.isCombo) { Lookup.ComboBox.dispose(true); } else { Lookup.Dialog.dispose(); }
		}
	},
	ComboBox: {
		isLocalCombo: function (oTarget) {
			return (oTarget.getAttribute('data-xtype').toLowerCase() === 'local-combo') ? true : false;
		},
		onKeyDown: function (oEvent) {
			return Lookup.ComboBox.processTextKey(oEvent, true);   // true means called from keydown
		},
		onKeyUp: function(oEvent) {
			return Lookup.ComboBox.processTextKey(oEvent, false); // false means not called from keydown
		},
		onInput: function(oEvent) {
			Lookup.ComboBox.onFilter(oEvent.target);
		},
		processTextKey:  function(oEvent, lKeyDown) {
			var nKey = oEvent.keyCode, oDataGrid = null, oHtmlTable = null, lRetValue = false;
			var lPreventDefault = (nKey === 46) ? true : (([9,27,123,40].indexOf(nKey) > -1) && (Lookup.ActiveObject)) ? true : false;

			/* Ignore keyup for special keys, all logic happens in keydown i.e here */
			if (lPreventDefault && (!lKeyDown)) {
				lRetValue = false;

			/* If lookup not active Ignore keyup for special keys, all logic happens in keydown i.e here */
			} else if (!lPreventDefault) {
				lRetValue = true;

			/* Evaluate special key handling situations and then ignore (prevent default behavior of) key */
			} else {
				lRetValue = false;

				switch(nKey) {
					case 46:
						Lookup.resetAsUnselected(oEvent.target);
						break;
					case 9:
					case 40:
						oDataGrid = Lookup.ActiveObject.DataGrid;
						oHtmlTable = oDataGrid.GridDomObj;
						if (oHtmlTable.rows.length > 1) { oHtmlTable.rows[oDataGrid.SelectedIndex].focus(); }
						break;
					case 27:
						Lookup.ComboBox.dispose(false, false, true);
						break;
					default:
				}
			}
			return lRetValue;
		},
		promote: function (oTarget) {
			var oComboContent = document.getElementById(jwfGlobal.COMBO_BOX_CONTAINER), oDataAttributes = {};
			var oComboParent = oComboContent.parentNode, oDataGrid = {};  oOrgValue = {}, nLookupType = 0;
			var oComboFooter = document.getElementById(jwfGlobal.COMBO_BOX_FOOTER);
			var oFormObjects = UIManager.getObjectsByTarget(oTarget);

			if (!oTarget.getAttribute('data-attributes')) { return; }

			oDataAttributes = UTIL.getDataAttributes(oTarget.getAttribute('data-attributes'));

			if (!Lookup.ActiveObject) {
				nLookupType = (oTarget.getAttribute('data-xtype').toLowerCase() == 'local-combo')
						? jwfGlobal.LOOKUP_TYPE_LOCAL_COMBO : jwfGlobal.LOOKUP_TYPE_REMOTE_COMBO;

				if (nLookupType === jwfGlobal.LOOKUP_TYPE_LOCAL_COMBO) { oComboFooter.style.display = 'none'; }
				else {
					oComboFooter.querySelector('p').innerHTML = 'Please Wait .. loading data';
					oComboFooter.style.display = 'block';
				}

				// Manage content panel, position and move content to element's parent
				oTarget.parentNode.appendChild(oComboContent);
				oOrgValue = {'Code': oTarget.getAttribute('data-xvalue'), 'Text': oTarget.value };
				oDataGrid = PageCtrl.getObjectById(jwfGlobal.COMBO_BOX_GRID);
				Overlay.display(true, [oTarget]);
				this.setPosition(oTarget,oComboContent);

				// Place current instance properties into Lookup active object property
				Lookup.ActiveObject = {
					'Target': oTarget,
					'DataAttributes': UTIL.getDataAttributes(oTarget.getAttribute('data-attributes')),
					'ComboContent': oComboContent,
					'ComboParent': oComboParent,
					'ComboFooter': oComboFooter,
					'Type': nLookupType,
					'isCombo': true,
					'isMultiSelect': false,
					'ExcludeRowIds': '-1',
					'DataGrid': oDataGrid,
					'TotalRecords': -1,
					'NoOfPages': -1,
					'ReturnedRecords': -1,
					'ActualPageNo': -1,
					'SelectedData': [],
					'OrgValue': oOrgValue,
					'ApiCallStatus': 0
				};
				Lookup.getData(true, null);
			}
		},
		setPosition: function (oTarget, oContent) {
			var oPos = UIManager.getAbsoluteBoundingRect(oTarget), oStyle = oTarget.currentStyle || window.getComputedStyle(oTarget);
			var nMarginBottom = parseInt(oStyle.marginBottom.replace('px',''));
			oContent.style.display = "block";
			oContent.style.top = (oPos.bottom - nMarginBottom) + "px";
			oContent.style.left = oPos.left + "px";
		},
		jumpToSelected: function() {
			var oDataGrid = Lookup.ActiveObject.DataGrid;
			var nInitialSelected = UTIL.indexOfRecord(oDataGrid.DataList, 'Text', Lookup.ActiveObject.Target.value);
			if (nInitialSelected > -1) { DataGrid.markRowSelected(oDataGrid.GridDomObj.rows[nInitialSelected+1]); }
		},
		dispose: function (lNormal, lAbort, lEscapeKey) {
			var oOrgValue = {}, oNextElement = null;
			var oObjCollection = UIManager.getObjectsByTarget(Lookup.ActiveObject.Target);

			if (Lookup.ActiveObject) {
				if (!lNormal) { Lookup.ActiveObject.Target.value = oObjCollection.TemplateEntry.FormData[Lookup.ActiveObject.Target.id].Text; }

				if (lEscapeKey) {
					oNextElement = Lookup.ActiveObject.Target;
				} else {
					oNextElement = UIManager.getNextTabElement(Lookup.ActiveObject.Target);
				}

				// Move content back to original parent, empty grid and hide overlay
				Lookup.ActiveObject.ComboParent.appendChild(Lookup.ActiveObject.ComboContent);
				$('#' + Lookup.ActiveObject.DataGrid.GridId).find("tr:not(:first)").remove();
				Overlay.hide([Lookup.ActiveObject.Target]);
			}
			Lookup.ActiveObject = null;
			if (oNextElement) { oNextElement.focus(); }
		},
		onResize: function () {
			var lDoResize = (Lookup.ActiveObject) ? (Lookup.ActiveObject.Type !== jwfGlobal.LOOKUP_TYPE_DIALOG) : false;
			if (lDoResize) { this.setPosition(Lookup.ActiveObject.Target, Lookup.ActiveObject.ComboContent);	}
		},
		onFilter: function(oTarget) {
			var oDataAttributes = null, nLookupType = -1, sUserInput = oTarget.value, oFilter = {}, oComboMsg = null;
			var lLocalCombo = Lookup.ComboBox.isLocalCombo(oTarget);

			/* if lookup not active and target is local combo promote popup window */
			if ( (!Lookup.ActiveObject) && lLocalCombo ) {
				Lookup.ComboBox.promote(oTarget);

			/* if lookup active and target is local combo then jump to appropriate item as per user input */
			} else if (Lookup.ActiveObject && lLocalCombo) {
				if (sUserInput === Lookup.ActiveObject.OrgValue.Text) {
					oFilter = null;
				} else {
					oFilter = {'Key': 'Text', 'Value': sUserInput};
				}

				UIManager.fillViewGrid(jwfGlobal.COMBO_BOX_GRID, Lookup.ActiveObject.DataGrid.DataList, oFilter);
				Lookup.ActiveObject.Target.focus();

			/* if lookup is not active and target is remote combo and user input length > 3 then promote popup window  */
			} else if ( (!Lookup.ActiveObject) && (!lLocalCombo) ) {
				if (sUserInput.length > 2) { Lookup.ComboBox.promote(oTarget); }

			} else if ( (Lookup.ActiveObject) && (!lLocalCombo) ) {
				oComboMsg = Lookup.ActiveObject.ComboFooter.querySelector('p');
				if (sUserInput.length > 2) {
					Lookup.getData(false, null);
				} else {
					oComboMsg.innerHTML = '<BR>Enter at least 3 letters';
				}
			} else {
				sUserInput = '';
			}
		},
		gotData: function(oLookupResult, nLookupType, oEventInfo) {
			var nNoOfRecords = 0, oDataGrid = Lookup.ActiveObject.DataGrid, oComboMsg = null;
			var lPromoteCall = (oEventInfo) ? oEventInfo.PromoteCall : false;

			try {
				// Local combo will call only once during promote
				if ((!oLookupResult) && (nLookupType === jwfGlobal.LOOKUP_TYPE_LOCAL_COMBO)) {
					UIManager.fillViewGrid(oDataGrid.GridId, oDataGrid.DataList);
					Lookup.ComboBox.jumpToSelected();
					Lookup.ActiveObject.Target.focus();

				// Remote combo will call this multiple times
				} else {
					if (lPromoteCall) {
						oDataGrid.GridDomObj.tHead.rows[0].innerHTML = oLookupResult.GridColumnsHtml;
						oDataGrid.setColumnsInfo(oDataGrid);
					}

					oDataGrid.DataList = oLookupResult.LookupData;
					UIManager.fillViewGrid(oDataGrid.GridId, oDataGrid.DataList);

					oComboMsg = Lookup.ActiveObject.ComboFooter.querySelector('p');
					oComboMsg.innerHTML = Lookup.getSearchResultText(oLookupResult, null);
					Lookup.ComboBox.jumpToSelected();
					Lookup.ActiveObject.Target.focus();
				}
			} catch(oError) { oError.display("gotLookupData()"); }
		}
	},
	Dialog: {
		promote: function(oTarget) {
			var oDataAttributes = {}, nLookupType = jwfGlobal.LOOKUP_TYPE_DIALOG, oDataGrid = {},  oOrgValue = {};
			var lMultiSelect = (oTarget.type.toLowerCase().indexOf('multiple') > -1);
			var oEditDataTab = UIManager.getObjectsByTarget(oTarget).EditDataTab;

			if (oEditDataTab.MainEntity.ReadOnly) { return; }

			if ((Lookup.ActiveObject) || (!oTarget.getAttribute('data-attributes'))) { return; }

			oDataAttributes = UTIL.getDataAttributes(oTarget.getAttribute('data-attributes'));

			oOrgValue = (lMultiSelect) ? oOrgValue : {'Code': oTarget.getAttribute('data-xvalue'), 'Text': oTarget.value };
			oDataGrid = PageCtrl.getObjectById(jwfGlobal.LOOKUP_DIALOG_GRID);

			// Setup type of dialog grid for (a) To create select all check box by code (b) CSS styling
			oDataGrid.GridType = (lMultiSelect) ? jwfGlobal.GRID_TYPE_DIALOG_MULTI : jwfGlobal.GRID_TYPE_DIALOG_ONE;
			oDataGrid.GridDomObj.setAttribute('data-xtype', (lMultiSelect) ? 'multi-select-grid' : 'single-select-grid');

			// Place current instance properties into Lookup active object property
			Lookup.ActiveObject = {
				'Target': oTarget,
				'ComboContent': null,
				'ComboParent': null,
				'ComboFooter': null,
				'Type': nLookupType,
				'isCombo': false,
				'isMultiSelect': lMultiSelect,
				'ExcludeRowIds': '',
				'DataGrid': oDataGrid,
				'TotalRecords': -1,
				'NoOfPages': -1,
				'ReturnedRecords': -1,
				'ActualPageNo': -1,
				'SelectedData': [],
				'OrgValue': oOrgValue,
				'ApiCallStatus': 0
			};
			Lookup.getData(true, null);
		},
		gotData: function(oLookupResult, oEventInfo) {
			var oDataGrid = PageCtrl.getObjectById(jwfGlobal.LOOKUP_DIALOG_GRID), oPageNo = null;
			var lPromoteCall = (oEventInfo) ? oEventInfo.PromoteCall : false;

			// if promote call insert column defination system data into dialog grid object
			if (lPromoteCall) {
				oDataGrid.GridDomObj.tHead.rows[0].innerHTML = oLookupResult.GridColumnsHtml;
				oDataGrid.setColumnsInfo(oDataGrid);
			}

			// fill grid with data (with results matching column defination from system data
			oDataGrid.DataList = oLookupResult.LookupData;
			UIManager.fillViewGrid(oDataGrid.GridId, oDataGrid.DataList);

			// if promote call open modal dialog - then only result and grid data gets updated
			if (lPromoteCall) {
				Modal.customDialog(jwfGlobal.MODAL_DIALOG_ID_LOOKUP, oLookupResult.DialogTitle,
						{ 'BodyDomObj': oDataGrid.GridDomObj,'HeaderHtml': oLookupResult.SearchFormHtml },
						['Select', 'Cancel'], { onDialogClose: Lookup.Dialog.jwfLookupDialogCallBacks, onDialogLoad: Lookup.Dialog.jwfLookupDialogCallBacks });
			}
			// Update search results into modal dialog header
			oPageNo = Modal.Header.querySelector('input[data-xtype="search-controls"');
			Modal.Header.querySelector('label[data-xtype="search-controls"').innerHTML = Lookup.getSearchResultText(oLookupResult, oPageNo);
			Modal.display();
		},
		jwfLookupDialogCallBacks: function(oEvent) {

			var sEventId = oEvent.EventId, sBtnClicked = '', oDataGrid = null, oRow = null;

			switch(sEventId) {
				case jwfGlobal.EVT_MODAL_DIALOG_ONLOAD:
					Lookup.Dialog.setupDataEvents(oEvent, Modal);
					break;
				case jwfGlobal.EVT_MODAL_DIALOG_ONCLOSE:
					sBtnClicked = oEvent.ClickedButton.toLowerCase();
					if (sBtnClicked === 'select')	{
						sBtnClicked = oEvent.ClickedButton.toLowerCase();
						oDataGrid = Lookup.ActiveObject.DataGrid;
						oRow = oDataGrid.GridDomObj.rows[oDataGrid.SelectedIndex];

						if (Lookup.ActiveObject.isMultiSelect) { 	Lookup.onSelect(null); }
						else { if (oRow) { Lookup.onSelect(oRow); } }
					}
					Lookup.Dialog.dispose();
					break;
			}
		},
		setupDataEvents: function(oEvent, oModal) {
			var aSearchControls = Modal.Header.querySelectorAll('span[data-xtype="search-controls"');
			for (var i = 0; i < aSearchControls.length; i++) {
			  $(aSearchControls[i]).on('click', function(oEvent) { Lookup.Dialog.jwfSearchDataClick(oEvent); });
			}
		},
		jwfSearchDataClick: function(oEvent) {
			var sFormId = $(oEvent.target).closest('form').attr('id'), sBtnName = oEvent.target.getAttribute('name').toLowerCase();
			var oPageNo = document.getElementById(sFormId).querySelector('input[data-xtype="search-controls"');
			var nPageNo = parseInt(oPageNo.value);

			if ((Lookup.ActiveObject.NoOfPages === 1) && (sBtnName !== 'search')) { return; }

			switch (sBtnName) {
				case 'first':
					nPageNo = 1;
					break;
				case 'last':
					nPageNo = Lookup.ActiveObject.NoOfPages;
					break;
				case 'previous':
					nPageNo = (nPageNo > 1) ? nPageNo - 1 : nPageNo;
					break;
				case 'next':
					nPageNo = (nPageNo < Lookup.ActiveObject.NoOfPages) ? nPageNo + 1 : nPageNo;
					break;
			}
			oPageNo.value = nPageNo;
			Lookup.getData(false, convertToJson(sFormId));
		},
		onSelectRow: function(oRow, oCheckBox) {
			var sText = oRow.cells[1].innerHTML;
			var oValue = { 'Code': oCheckBox.value, 'Text': UTIL.replaceAll(sText,'&nbsp;','') }, lChecked = oCheckBox.checked, nIndex = -1;

			if (lChecked) {
				Lookup.ActiveObject.SelectedData.push(oValue);
			} else {
				nIndex = UTIL.indexOfRecord(Lookup.ActiveObject.SelectedData, 'Code', oValue.Code);
				if (nIndex > -1) { Lookup.ActiveObject.SelectedData.splice(nIndex , 1); }
			}
		},
		dispose: function() {
			Lookup.ActiveObject = null;
		}
	}
}

function nextTabbedElement(oCurrentElement) {
	var sLogicalFormId = UIManager.getLogicalFormId(oCurrentElement);
	var nNextInput = UTIL.indexOfRecord(PageCtrl.ColumnsInfo[sLogicalFormId], 'id', oCurrentElement.id);
	nNextInput = ((nNextInput - 1) === PageCtrl.ColumnsInfo[sLogicalFormId].length) ? 0 : nNextInput + 1;
	return PageCtrl.ColumnsInfo[sLogicalFormId][nNextInput].domObject;
}

function myDebug(sContext, oLogObject, lClear) {
	PageCtrl.debug.log(sContext, oLogObject);
}

function initializeJwfSpaInfra() {
	addJwfHtmlTemplates();
	PageCtrl.debug.setup();
	Modal.setup();
	PageCtrl.debug.log('initializeJwfSpaInfra()', 'JwfSpaInfra module successfully loaded in browser page!');
}

function addJwfHtmlTemplates() {
	$('body').append('<div id="Jwf-Templates"></div>');
	document.getElementById('Jwf-Templates').innerHTML =
		'<div class="jwf-app-message-dialog">'
		+'			<h3></h3>'
		+'	<p></p>'
		+'	<textarea rows="5" cols="120" disabled></textarea>'
		+'	<button class="abutton">Ok</button>'
		+'</div>'
		+'<div id="jwf-combo-box-container" class="jwf-combo-box-container">'
		+'	<div id="jwf-combo-box-body" class="jwf-combo-box-body">'
		+'		<table id="jwf-combo-box-grid" class="jwf-combo-box-grid">'
		+'			<thead><tr></tr></thead>'
		+'			<tbody>'
		+'			</tbody>'
		+'		</table>'
		+'	</div>'
		+'	<div id="jwf-combo-box-footer" class="jwf-combo-box-footer">'
		+'		<p id="jwf-combo-box-msg"></p>'
		+'	</div>'
		+'</div>'
		+'<table id="jwf-lookup-dialog-grid" class="jwf-browse-grid">'
		+'	<thead><tr></tr></thead>'
		+'	<tbody>'
		+'	</tbody>'
		+'</table>'
		+'<div id="jwf-modal-container" class="jwf-modal-container">'
		+'	<img src="./assets/jwf/content/x.png">'
		+'	<p class="jwf-modal-title"></p>'
		+'	<div class="jwf-modal-header" id="jwf-modal-header"></div>'
		+'	<div class="jwf-modal-body" id="jwf-modal-body"></div>'
		+'	<div class="jwf-modal-footer" id="jwf-modal-footer"></div>'
		+'</div>';
}
