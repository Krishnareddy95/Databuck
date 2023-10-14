!function(){function e(e){return function(e){if(Array.isArray(e))return t(e)}(e)||function(e){if("undefined"!=typeof Symbol&&null!=e[Symbol.iterator]||null!=e["@@iterator"])return Array.from(e)}(e)||function(e,n){if(!e)return;if("string"==typeof e)return t(e,n);var i=Object.prototype.toString.call(e).slice(8,-1);"Object"===i&&e.constructor&&(i=e.constructor.name);if("Map"===i||"Set"===i)return Array.from(e);if("Arguments"===i||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(i))return t(e,n)}(e)||function(){throw new TypeError("Invalid attempt to spread non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}()}function t(e,t){(null==t||t>e.length)&&(t=e.length);for(var n=0,i=new Array(t);n<t;n++)i[n]=e[n];return i}function n(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function i(e,t){for(var n=0;n<t.length;n++){var i=t[n];i.enumerable=i.enumerable||!1,i.configurable=!0,"value"in i&&(i.writable=!0),Object.defineProperty(e,(o=i.key,r=void 0,"symbol"==typeof(r=function(e,t){if("object"!=typeof e||null===e)return e;var n=e[Symbol.toPrimitive];if(void 0!==n){var i=n.call(e,t||"default");if("object"!=typeof i)return i;throw new TypeError("@@toPrimitive must return a primitive value.")}return("string"===t?String:Number)(e)}(o,"string"))?r:String(r)),i)}var o,r}function o(e,t,n){return t&&i(e.prototype,t),n&&i(e,n),Object.defineProperty(e,"prototype",{writable:!1}),e}(window.webpackJsonp=window.webpackJsonp||[]).push([[30],{QIUk:function(e,t,i){"use strict";i.d(t,"a",function(){return y}),i.d(t,"b",function(){return x});var r=i("ofXK"),s=i("7zfz"),c=i("fXoL");function a(e,t){1&e&&c.fc(0)}function l(e,t){if(1&e&&(c.jc(0,"div",8),c.zc(1,1),c.Zc(2,a,1,0,"ng-container",6),c.ic()),2&e){var n=c.uc();c.Nb(2),c.Bc("ngTemplateOutlet",n.headerTemplate)}}function d(e,t){1&e&&c.fc(0)}function u(e,t){if(1&e&&(c.jc(0,"div",9),c.bd(1),c.Zc(2,d,1,0,"ng-container",6),c.ic()),2&e){var n=c.uc();c.Nb(1),c.dd(" ",n.header," "),c.Nb(1),c.Bc("ngTemplateOutlet",n.titleTemplate)}}function g(e,t){1&e&&c.fc(0)}function p(e,t){if(1&e&&(c.jc(0,"div",10),c.bd(1),c.Zc(2,g,1,0,"ng-container",6),c.ic()),2&e){var n=c.uc();c.Nb(1),c.dd(" ",n.subheader," "),c.Nb(1),c.Bc("ngTemplateOutlet",n.subtitleTemplate)}}function f(e,t){1&e&&c.fc(0)}function m(e,t){1&e&&c.fc(0)}function b(e,t){if(1&e&&(c.jc(0,"div",11),c.zc(1,2),c.Zc(2,m,1,0,"ng-container",6),c.ic()),2&e){var n=c.uc();c.Nb(2),c.Bc("ngTemplateOutlet",n.footerTemplate)}}var h=["*",[["p-header"]],[["p-footer"]]],v=["*","p-header","p-footer"],y=function(){var e=function(){function e(t){n(this,e),this.el=t}return o(e,[{key:"ngAfterContentInit",value:function(){var e=this;this.templates.forEach(function(t){switch(t.getType()){case"header":e.headerTemplate=t.template;break;case"title":e.titleTemplate=t.template;break;case"subtitle":e.subtitleTemplate=t.template;break;case"content":e.contentTemplate=t.template;break;case"footer":e.footerTemplate=t.template;break;default:e.contentTemplate=t.template}})}},{key:"getBlockableElement",value:function(){return this.el.nativeElement.children[0]}}]),e}();return e.\u0275fac=function(t){return new(t||e)(c.dc(c.n))},e.\u0275cmp=c.Xb({type:e,selectors:[["p-card"]],contentQueries:function(e,t,n){var i;(1&e&&(c.Wb(n,s.f,1),c.Wb(n,s.e,1),c.Wb(n,s.i,0)),2&e)&&(c.Nc(i=c.rc())&&(t.headerFacet=i.first),c.Nc(i=c.rc())&&(t.footerFacet=i.first),c.Nc(i=c.rc())&&(t.templates=i))},inputs:{header:"header",subheader:"subheader",style:"style",styleClass:"styleClass"},ngContentSelectors:v,decls:9,vars:9,consts:[[3,"ngClass","ngStyle"],["class","p-card-header",4,"ngIf"],[1,"p-card-body"],["class","p-card-title",4,"ngIf"],["class","p-card-subtitle",4,"ngIf"],[1,"p-card-content"],[4,"ngTemplateOutlet"],["class","p-card-footer",4,"ngIf"],[1,"p-card-header"],[1,"p-card-title"],[1,"p-card-subtitle"],[1,"p-card-footer"]],template:function(e,t){1&e&&(c.Ac(h),c.jc(0,"div",0),c.Zc(1,l,3,1,"div",1),c.jc(2,"div",2),c.Zc(3,u,3,2,"div",3),c.Zc(4,p,3,2,"div",4),c.jc(5,"div",5),c.zc(6),c.Zc(7,f,1,0,"ng-container",6),c.ic(),c.Zc(8,b,3,1,"div",7),c.ic(),c.ic()),2&e&&(c.Qb(t.styleClass),c.Bc("ngClass","p-card p-component")("ngStyle",t.style),c.Nb(1),c.Bc("ngIf",t.headerFacet||t.headerTemplate),c.Nb(2),c.Bc("ngIf",t.header||t.titleTemplate),c.Nb(1),c.Bc("ngIf",t.subheader||t.subtitleTemplate),c.Nb(3),c.Bc("ngTemplateOutlet",t.contentTemplate),c.Nb(1),c.Bc("ngIf",t.footerFacet||t.footerTemplate))},directives:[r.l,r.o,r.n,r.s],styles:[".p-card-header img{width:100%}"],encapsulation:2,changeDetection:0}),e}(),x=function(){var e=o(function e(){n(this,e)});return e.\u0275fac=function(t){return new(t||e)},e.\u0275mod=c.bc({type:e}),e.\u0275inj=c.ac({imports:[[r.b],s.j]}),e}()},"ct+p":function(t,i,r){"use strict";r.r(i),r.d(i,"HomeModule",function(){return Q});var s,c,a=r("ofXK"),l=r("tyNb"),d=r("fXoL"),u=(r("pLZG"),r("3XJ7")),g=(r("lUod"),r("R0Ic")),p=r("WyaX"),f=((c=o(function e(){n(this,e),this.autoClose=!0,this.insideClick=!1,this.isAnimated=!1})).\u0275fac=function(e){return new(e||c)},c.\u0275prov=d.Zb({token:c,factory:c.\u0275fac}),c),m=((s=o(function e(){var t=this;n(this,e),this.direction="down",this.isOpenChange=new d.p,this.isDisabledChange=new d.p,this.toggleClick=new d.p,this.dropdownMenu=new Promise(function(e){t.resolveDropdownMenu=e})})).\u0275fac=function(e){return new(e||s)},s.\u0275prov=d.Zb({token:s,factory:s.\u0275fac}),s);Object(g.k)({height:0,overflow:"hidden"}),Object(g.e)("220ms cubic-bezier(0, 0, 0.2, 1)",Object(g.k)({height:"*",overflow:"hidden"}));var b,h,v,y=((b=function(){function e(){n(this,e)}return o(e,null,[{key:"forRoot",value:function(t){return{ngModule:e,providers:[u.a,p.a,m,{provide:f,useValue:t||{autoClose:!0,insideClick:!1}}]}}}]),e}()).\u0275fac=function(e){return new(e||b)},b.\u0275mod=d.bc({type:b}),b.\u0275inj=d.ac({}),b),x=r("gren"),C=r("QIUk"),w=((h=o(function e(){n(this,e)})).\u0275fac=function(e){return new(e||h)},h.\u0275mod=d.bc({type:h}),h.\u0275inj=d.ac({imports:[[a.b]]}),h),k=r("tk/3"),S=r("3Pt+"),P=r("ay4d"),O=r("bUwk"),M=((v=o(function e(){n(this,e)})).\u0275fac=function(e){return new(e||v)},v.\u0275mod=d.bc({type:v}),v.\u0275inj=d.ac({providers:[P.a,O.a],imports:[[a.b,k.c,S.j,S.u]]}),v),_=r("ThbA"),I=r("PCNd"),j=r("Nmef"),T=r("JqCM"),L=r("ey9i"),N=r("7kUa"),z=r("jIHw");function B(e,t){1&e&&(d.jc(0,"h6",11),d.bd(1,"Autonomous Trust-Score for Your Data"),d.ic())}function D(e,t){1&e&&(d.jc(0,"label",24),d.bd(1,"Caps Locked On"),d.ic())}function U(e,t){if(1&e){var n=d.kc();d.jc(0,"button",25),d.qc("click",function(){return d.Rc(n),d.uc(2).callSSO()}),d.ic()}}function K(e,t){if(1&e){var n=d.kc();d.jc(0,"div",12),d.jc(1,"div",13),d.jc(2,"label",14),d.bd(3,"User Name"),d.ic(),d.ic(),d.jc(4,"div",15),d.jc(5,"input",16),d.qc("keyup.enter",function(){return d.Rc(n),d.uc().loginUser()})("ngModelChange",function(e){return d.Rc(n),d.uc().username=e}),d.ic(),d.ic(),d.jc(6,"div",17),d.jc(7,"label",18),d.bd(8,"Password"),d.ic(),d.ic(),d.jc(9,"div",17),d.jc(10,"input",19),d.qc("keyup.enter",function(){return d.Rc(n),d.uc().loginUser()})("ngModelChange",function(e){return d.Rc(n),d.uc().password=e}),d.ic(),d.ic(),d.jc(11,"div",20),d.qc("capsLock",function(e){return d.Rc(n),d.uc().capsOn=e}),d.Zc(12,D,2,0,"label",21),d.ic(),d.jc(13,"div",13),d.Zc(14,U,1,0,"button",22),d.jc(15,"button",23),d.qc("click",function(){return d.Rc(n),d.uc().loginUser()}),d.ic(),d.ic(),d.ic()}if(2&e){var i=d.uc();d.Nb(5),d.Bc("ngModel",i.username),d.Nb(5),d.Bc("ngModel",i.password),d.Nb(2),d.Bc("ngIf",i.capsOn),d.Nb(2),d.Bc("ngIf",i.isSSOenabled),d.Nb(1),d.Bc("ngClass",i.isSSOenabled?"css-sso":"")}}function R(e,t){if(1&e){var n=d.kc();d.jc(0,"div"),d.jc(1,"div",26),d.bd(2),d.ic(),d.jc(3,"div",13),d.jc(4,"button",27),d.qc("click",function(){return d.Rc(n),d.uc().continueMigrate()}),d.ic(),d.ic(),d.ic()}if(2&e){var i=d.uc();d.Nb(2),d.dd(" ",i.divMessage," ")}}function A(e,t){if(1&e){var n=d.kc();d.jc(0,"div"),d.jc(1,"h6",28),d.bd(2,"Renew License"),d.ic(),d.jc(3,"div",15),d.jc(4,"span",29),d.ec(5,"i",30),d.jc(6,"input",31),d.qc("ngModelChange",function(e){return d.Rc(n),d.uc().licenseKey=e}),d.ic(),d.ic(),d.ic(),d.jc(7,"div"),d.jc(8,"label",32),d.bd(9,"License expired - please contact info@firsteigen.com"),d.ic(),d.ic(),d.jc(10,"div",13),d.jc(11,"button",33),d.qc("click",function(){return d.Rc(n),d.uc().renewLicenseCall()}),d.ic(),d.ic(),d.ic()}if(2&e){var i=d.uc();d.Nb(6),d.Bc("ngModel",i.licenseKey)}}var E,F,Z,J=function(e){return{"background-image":e}},q=((F=function(){function t(e,i,o,r,s,c){n(this,t),this.router=e,this.httpService=i,this.homeServiceService=o,this.spinner=r,this.messages=s,this.excutiveSummaryService=c,this.username="",this.password="",this.checkStatus=!1,this.isSSOenabled=!1,this.images=["../../../assets/images/Login_BackgroundImage_01.png","../../../assets/images/Login_BackgroundImage_02.png","../../../assets/images/loginimg.jpg"],this.tenentIdToken="394aa0d7-af1a-4788-b38f-523006fd9dbd",this.clientIdToken="930119c1-2e01-4ef8-93bc-886713319192",this.redirectUrl="http%3A%2F%2Flocalhost%3A4200%2Fdbckangui%2Flogin",this.divMessage="",this.backgroundImage="",this.migateTrue=!1,this.isLicenseKey=!1,this.licenseKey=""}return o(t,[{key:"ngOnInit",value:function(){var e=this,t=Math.round(100*Math.random()%2);this.backgroundImage=this.images[t],sessionStorage.removeItem("userType"),sessionStorage.removeItem("tenentIdToken");var n=window.location.href.split("id_token=");2===n.length&&(this.spinner.show(),this.loginSSOUser("","","",n[1])),window.scrollTo(0,0),this.httpService.removeAllSessionStorage(),this.homeServiceService.getActiveDirectoryFlag().subscribe(function(t){"success"===t.status&&(e.checkStatus="Y"===t.activeDirectoryFlag)}),this.homeServiceService.getSSODetails().subscribe(function(t){"success"===t.status&&(e.isSSOenabled="Y"===t.isSSOenabled,!0===e.isSSOenabled&&(null!=t.tenentIdToken&&(e.tenentIdToken=t.tenentIdToken),null!=t.clientIdToken&&(e.clientIdToken=t.clientIdToken),null!=t.redirectUrl&&(e.redirectUrl=t.redirectUrl),sessionStorage.setItem("tenentIdToken",e.tenentIdToken)))})}},{key:"callSSO",value:function(){this.spinner.show(),window.location.href="https://login.microsoftonline.com/"+this.tenentIdToken+"/oauth2/v2.0/authorize?client_id="+this.clientIdToken+"&nonce=defaultNonce&redirect_uri="+this.redirectUrl+"&scope=openid+profile+User.read&response_type=id_token"}},{key:"loginSSOUser",value:function(t,n,i,o){var r=this;sessionStorage.setItem("userType","ssoUser"),this.spinner.show(),this.homeServiceService.loginSSO({userName:t,department:n,role:i,token:o}).subscribe(function(t){var n;if("success"===(null==t?void 0:t.status)){if(!t.licenseDetails.licenseExpired){sessionStorage.setItem("showExpiryDate",t.licenseDetails.expiringInMonth),sessionStorage.setItem("licenseExpiryDate",t.licenseDetails.licenseExpiryDate),sessionStorage.setItem("token",t.token),sessionStorage.setItem("refreshToken",t.refreshToken),sessionStorage.setItem("expiryTime",t.expiryTime),r.excutiveSummaryService.getProperties("appset/getPropertiesForPropertyCategory",{propertyCategory:"appdb"}).subscribe(function(e){var t=e.result.find(function(e){return"idle_time_out"===e.propertyName});sessionStorage.setItem("timer",t.propertyValue)});var i=[];i=e(new Set(t.userProjectList.map(function(e){return e.idProject}))),sessionStorage.setItem("userProjectList",i.toString());var o=t.domainProjectList.sort(function(e,t){var n=e.domainName.toLowerCase(),i=t.domainName.toLowerCase();return n<i?-1:n>i?1:0});sessionStorage.setItem("domainProjectList",JSON.stringify(o)),sessionStorage.setItem("userDetail",JSON.stringify(t.user)),r.homeServiceService.getUserDetailsAsToken().subscribe(function(e){r.homeServiceService.getRolePermissionsByRoleId({idRole:t.user.idRole}).subscribe(function(e){"success"===e.status&&(sessionStorage.setItem("modulePerRole",JSON.stringify(e.result)),r.homeServiceService.getApplicationModuleName().subscribe(function(e){var n,i;if("success"===e.status){sessionStorage.setItem("productModule",JSON.stringify(e.propertyModule));var o=t.domainProjectList;o.length>0?(sessionStorage.setItem("activeProjectId",null===(n=o[0])||void 0===n?void 0:n.idProject),sessionStorage.setItem("activeDomainId",null===(i=o[0])||void 0===i?void 0:i.domainId),sessionStorage.setItem("firstTimeLogin","No"),r.router.navigate(["/dbckangui"])):"Admin"==t.user.roleName?(r.spinner.hide(),r.router.navigate(["/dbckangui/user-setting/view-projects"]),sessionStorage.setItem("firstTimeLogin","Yes")):(r.spinner.hide(),r.messages.displayToaster("Project not assigned to user","warn","Warning!"),r.router.navigate(["/dbckangui/login"]))}}))})})}}else!t.result||0!==t.result.pageContext&&1!==t.result.pageContext||(r.spinner.hide(),r.migateTrue=!0,r.divMessage=null===(n=null==t?void 0:t.result)||void 0===n?void 0:n.message),t.licenseDetails&&t.licenseDetails.licenseExpired&&(r.isLicenseKey=!0,r.spinner.hide()),t.result&&t.result.pageContext&&!t.licenseDetails.licenseExpired&&r.spinner.hide()},function(e){window.location.href=window.location.origin+window.location.pathname})}},{key:"loginUser",value:function(){var t=this;sessionStorage.setItem("userType","normalUser"),""!==this.username&&""!==this.password?(this.spinner.show(),this.homeServiceService.login({email:this.username,password:this.password}).subscribe(function(n){var i;if("success"===(null==n?void 0:n.status)){if(!n.licenseDetails.licenseExpired){sessionStorage.setItem("showExpiryDate",n.licenseDetails.expiringInMonth),sessionStorage.setItem("licenseExpiryDate",n.licenseDetails.licenseExpiryDate),sessionStorage.setItem("token",n.token),sessionStorage.setItem("refreshToken",n.refreshToken),sessionStorage.setItem("expiryTime",n.expiryTime);var o=[];o=e(new Set(n.userProjectList.map(function(e){return e.idProject}))),t.excutiveSummaryService.getProperties("appset/getPropertiesForPropertyCategory",{propertyCategory:"appdb"}).subscribe(function(e){var t=e.result.find(function(e){return"idle_time_out"===e.propertyName});sessionStorage.setItem("timer",t.propertyValue)}),sessionStorage.setItem("userProjectList",o.toString());var r=n.domainProjectList.sort(function(e,t){var n=e.domainName.toLowerCase(),i=t.domainName.toLowerCase();return n<i?-1:n>i?1:0});sessionStorage.setItem("domainProjectList",JSON.stringify(r)),sessionStorage.setItem("userDetail",JSON.stringify(n.user)),t.homeServiceService.getUserDetailsAsToken().subscribe(function(e){t.homeServiceService.getRolePermissionsByRoleId({idRole:n.user.idRole}).subscribe(function(e){"success"===e.status&&(sessionStorage.setItem("modulePerRole",JSON.stringify(e.result)),t.homeServiceService.getApplicationModuleName().subscribe(function(e){var i,o;if("success"===e.status){sessionStorage.setItem("productModule",JSON.stringify(e.propertyModule));var r=n.domainProjectList;r.length>0?(sessionStorage.setItem("activeProjectId",null===(i=r[0])||void 0===i?void 0:i.idProject),sessionStorage.setItem("activeDomainId",null===(o=r[0])||void 0===o?void 0:o.domainId),sessionStorage.setItem("firstTimeLogin","No"),t.spinner.hide(),t.router.navigate(["/dbckangui"])):(t.spinner.hide(),"Admin"==n.user.roleName?(t.router.navigate(["/dbckangui/user-setting/view-projects"]),sessionStorage.setItem("firstTimeLogin","Yes")):(t.messages.displayToaster("Project not assigned to user","warn","Warning!"),t.router.navigate(["/dbckangui/login"])))}}))})})}}else n.licenseDetails&&n.licenseDetails.licenseExpired&&(t.isLicenseKey=!0,t.spinner.hide()),!n.result||0!==n.result.pageContext&&1!==n.result.pageContext||(t.spinner.hide(),t.migateTrue=!0,t.divMessage=null===(i=null==n?void 0:n.result)||void 0===i?void 0:i.message),n.result&&1!==n.result.pageContext&&2!==n.result.pageContext&&0!==n.result.pageContext&&(t.spinner.hide(),t.messages.displayToaster(n.result.message,"warn","Warning!"))})):this.messages.displayToaster(this.checkStatus?"Please enter username and password":"Please enter email and password","warn","Warning!")}},{key:"renewLicenseCall",value:function(){var e=this;""==this.licenseKey?this.messages.displayToaster("Please enter license Key","warn","Warning!"):(this.spinner.show(),this.homeServiceService.renewLicense({licenseKey:this.licenseKey}).subscribe(function(t){"success"===t.status?(e.isLicenseKey=!1,e.spinner.hide(),e.licenseKey=""):e.spinner.hide()}))}},{key:"continueMigrate",value:function(){var e=this;this.spinner.show(),this.homeServiceService.migrateDatabaseChanges({nCallContext:1}).subscribe(function(t){2===t.PageContext?(e.migateTrue=!1,e.spinner.hide(),e.router.navigate(["/dbckangui/login"])):(e.spinner.hide(),e.divMessage="Error occured while upgrade/import data schema. Can not continue to login .. Kindly contact FirstEigen support.")})}}]),t}()).\u0275fac=function(e){return new(e||F)(d.dc(l.i),d.dc(O.a),d.dc(j.a),d.dc(T.c),d.dc(L.b),d.dc(L.c))},F.\u0275cmp=d.Xb({type:F,selectors:[["app-login"]],decls:14,vars:7,consts:[[1,"bg-img",3,"ngStyle"],[1,"containers"],[1,"div-buck-icon"],[1,"flex-buck-icon"],["src","../../../assets/icons/databuckLogo/BUCK_ICON.svg","height","39px","alt","","width","50%",1,"pad-0"],[1,"flex-databuck-icon"],["src","../../../assets/icons/databuckLogo/DATABUCK_TEXT.svg","height","39px","alt","","width","50%"],["class","header login-title-text",4,"ngIf"],["class","margin",4,"ngIf"],[4,"ngIf"],[1,"color-code","copyRText"],[1,"header","login-title-text"],[1,"margin"],[1,"col-md-12","margin-top"],[1,"form-label","color-cd","username-label"],[1,"col-md-12","mar-btm"],["id","username","type","text","pInputText","","placeholder","Username",1,"form-texts",3,"ngModel","keyup.enter","ngModelChange"],[1,"col-md-12"],["id","password-label",1,"form-label","color-cd"],["id","password","type","password","pInputText","","placeholder","Password",1,"form-texts",3,"ngModel","keyup.enter","ngModelChange"],[3,"capsLock"],["class","label-caps capslock",4,"ngIf"],["pButton","","type","button","class","btn-sso margin-top","label","Single Sign On",3,"click",4,"ngIf"],["pButton","","type","submit","form","loginForm","label","Login",1,"btnlogin","margin-top",3,"ngClass","click"],[1,"label-caps","capslock"],["pButton","","type","button","label","Single Sign On",1,"btn-sso","margin-top",3,"click"],[1,"col-md-12","color-migrate"],["pButton","","type","button","form","loginForm","label","Continue",1,"btnlogin","margin-top",3,"click"],[1,"header"],[1,"p-input-icon-left"],[1,"pi","pi-key"],["id","idLicenseKey","type","text","pInputText","","placeholder","Please enter license Key",1,"form-field",3,"ngModel","ngModelChange"],[1,"label-caps"],["pButton","","type","button","form","loginForm","label","Submit",1,"btnlogin","margin-top",3,"click"]],template:function(e,t){1&e&&(d.jc(0,"div",0),d.jc(1,"div",1),d.jc(2,"div"),d.jc(3,"div",2),d.jc(4,"div",3),d.ec(5,"img",4),d.ic(),d.jc(6,"div",5),d.ec(7,"img",6),d.ic(),d.ic(),d.Zc(8,B,2,0,"h6",7),d.Zc(9,K,16,5,"div",8),d.Zc(10,R,5,1,"div",9),d.Zc(11,A,12,1,"div",9),d.jc(12,"div",10),d.bd(13," 2023 \xa9 FirstEigen "),d.ic(),d.ic(),d.ic(),d.ic()),2&e&&(d.Bc("ngStyle",d.Fc(5,J,"url("+t.backgroundImage+")")),d.Nb(8),d.Bc("ngIf",!t.isLicenseKey&&!t.migateTrue),d.Nb(1),d.Bc("ngIf",!t.isLicenseKey&&!t.migateTrue),d.Nb(1),d.Bc("ngIf",t.migateTrue),d.Nb(1),d.Bc("ngIf",t.isLicenseKey))},directives:function(){return[a.o,a.n,S.d,N.a,S.o,S.r,W,z.b,a.l]},styles:[".mar-btm[_ngcontent-%COMP%]{padding-bottom:10px}.margin-top[_ngcontent-%COMP%]{padding-top:3px}.header[_ngcontent-%COMP%]{height:5%;font-family:Lato;font-weight:700;font-size:16px;padding-top:0;color:#fff;margin-top:0;display:flex;justify-content:center;align-items:center;line-height:19px}.containers[_ngcontent-%COMP%]{box-sizing:border-box;position:absolute;width:30%;height:auto;margin:auto;left:60%;right:7%;-webkit-backdrop-filter:blur(7.63798px);backdrop-filter:blur(7.63798px);border-radius:27.1573px;box-shadow:0 0 8.48665px rgba(98,40,95,.2);background:linear-gradient(113.47deg,rgba(255,229,242,.2) 1.02%,rgba(193,54,125,.46) 102.22%);border:1.7px solid hsla(0,0%,100%,.5)}.contanier-box[_ngcontent-%COMP%]{height:auto;width:39.72%}.color-cd[_ngcontent-%COMP%]{color:#fff;font-size:16px}.color-migrate[_ngcontent-%COMP%]{color:#fff;font-size:14px;font-weight:700;font-style:normal;font-family:Lato}.mar[_ngcontent-%COMP%]{margin:0 13%!important}.color-code[_ngcontent-%COMP%]{color:hsla(0,0%,100%,.7);text-align:center;text-shadow:0 0 5.09199px rgba(0,0,0,.25);font-style:normal;font-weight:600;align-items:center;display:flex;font-size:16px;font-family:Lato;height:36px;background-color:none;padding-left:68%}.copyRText[_ngcontent-%COMP%]{position:absolute;left:0;text-align:center;width:100%;display:block;padding:0;margin:15px 0}.form-texts[_ngcontent-%COMP%]{box-shadow:inset 0 0 5.09199px .848665px rgba(0,0,0,.44);height:40px;box-sizing:border-box;width:366px;background:#fff;border:.848665px solid #fff;border-radius:3.39466px}[_nghost-%COMP%]     .p-input-icon-left{display:unset}[_nghost-%COMP%]  .p-card-footer{text-align:right;padding-top:0}[_nghost-%COMP%]  .p-card .p-card-title{text-align:center}[_nghost-%COMP%]  .p-card .p-card-body{box-shadow:none;border-radius:20px}.btm-lbl[_ngcontent-%COMP%]{padding:5px 32%}[_nghost-%COMP%]     .p-card.p-component{border-radius:20px;background:linear-gradient(112.91deg,hsla(0,0%,100%,.2) 1.02%,hsla(0,0%,100%,.9) 98.61%)}.btnlogin[_ngcontent-%COMP%]{width:100%;color:#fff;font-weight:600;font-size:16px;font-style:normal;font-family:Lato;border-radius:3.39466px;box-sizing:border-box}.btnlogin[_ngcontent-%COMP%], .css-sso.btnlogin[_ngcontent-%COMP%]{margin-top:-10px;background-color:#f85745!important;margin-bottom:10%}.css-sso.btnlogin[_ngcontent-%COMP%]{width:49.5%}.btn-sso[_ngcontent-%COMP%]{margin-top:-10px;background-color:#fff!important;width:48%;margin-bottom:10%;color:#f85745!important;font-weight:600;font-size:16px;margin-right:2%;font-style:normal;font-family:Lato;border-radius:3.39466px;border:.848665px solid #f85745}.box-2[_ngcontent-%COMP%]{padding:10px}.box-1[_ngcontent-%COMP%], .box-2[_ngcontent-%COMP%]{width:50%}.margin[_ngcontent-%COMP%]{margin:0 17%!important}.h-1[_ngcontent-%COMP%]{font-size:24px;font-weight:700}.text-muted[_ngcontent-%COMP%]{font-size:14px}.container[_ngcontent-%COMP%]   .box[_ngcontent-%COMP%]{width:100px;height:100px;display:flex;flex-direction:column;align-items:center;justify-content:center;border:2px solid transparent;text-decoration:none;color:#615f5fdd}.box[_ngcontent-%COMP%]:active, .box[_ngcontent-%COMP%]:hover, .box[_ngcontent-%COMP%]:visited{border:2px solid violet}.btn.btn-primary[_ngcontent-%COMP%]{background-color:transparent;color:violet;border:0;padding:0;font-size:14px}.btn.btn-primary[_ngcontent-%COMP%]   .fas.fa-chevron-right[_ngcontent-%COMP%]{font-size:12px}.footer[_ngcontent-%COMP%]   .p-color[_ngcontent-%COMP%]{color:violet}.footer.text-muted[_ngcontent-%COMP%]{font-size:10px}.fas.fa-times[_ngcontent-%COMP%]{position:absolute;top:20px;right:20px;height:20px;width:20px;background-color:#f3cff379;font-size:18px;display:flex;align-items:center;justify-content:center}.fas.fa-times[_ngcontent-%COMP%]:hover{color:red}.bg[_ngcontent-%COMP%]{background:#fff!important}@media (max-width:767px){body[_ngcontent-%COMP%]{padding:10px}.body[_ngcontent-%COMP%]{height:100%}.body[_ngcontent-%COMP%], .box-1[_ngcontent-%COMP%], .box-2[_ngcontent-%COMP%]{width:100%}.box-2[_ngcontent-%COMP%]{height:440px}}@media (max-width:1440px){.containers[_ngcontent-%COMP%]{left:55%;right:12%;width:32%}.color-cd[_ngcontent-%COMP%]{font-size:12px}}@media (max-width:1600px){.containers[_ngcontent-%COMP%]{left:58%;right:8%;width:30%}.color-cd[_ngcontent-%COMP%]{font-size:12px}.btn-sso[_ngcontent-%COMP%], .btnlogin[_ngcontent-%COMP%]{font-size:15px;padding:7px}}@media (max-width:1400px){.containers[_ngcontent-%COMP%]{left:50%;right:25%;width:28%}.color-cd[_ngcontent-%COMP%]{font-size:12px}.btn-sso[_ngcontent-%COMP%], .btnlogin[_ngcontent-%COMP%]{font-size:14px}}@media (max-width:1366px){.containers[_ngcontent-%COMP%]{width:31%;right:15%;left:60%}.label-caps[_ngcontent-%COMP%]{font-size:13px}.btn-sso[_ngcontent-%COMP%]{font-size:14px;padding:7px}.btnlogin[_ngcontent-%COMP%]{padding:7px!important}.btnlogin[_ngcontent-%COMP%], .header[_ngcontent-%COMP%]{font-size:14px}}@media (max-width:1300px){.containers[_ngcontent-%COMP%]{width:36%;right:20%;left:52%}.label-caps[_ngcontent-%COMP%]{font-size:13px}.btn-sso[_ngcontent-%COMP%]{font-size:14px}.btnlogin[_ngcontent-%COMP%], .header[_ngcontent-%COMP%]{font-size:14px;padding:5px}}button[_ngcontent-%COMP%]:focus{background:red}.main[_ngcontent-%COMP%]{display:flex;justify-content:center}.bg-img[_ngcontent-%COMP%]{overflow:hidden;height:99.4vh;background-size:cover;display:flex;justify-content:flex-end;align-items:center}input[type=password][_ngcontent-%COMP%], input[type=text][_ngcontent-%COMP%]{width:100%;border:none;background:#fff!important}.mainTop[_ngcontent-%COMP%]{padding-top:15%}.rBox[_ngcontent-%COMP%]{width:100%}input[type=password][_ngcontent-%COMP%]:focus, input[type=text][_ngcontent-%COMP%]:focus{background:#fff!important;outline:none}.btn[_ngcontent-%COMP%]{background-color:#04aa6d;color:#fff;padding:16px 20px;border:none;cursor:pointer;width:100%;opacity:.9}.btn[_ngcontent-%COMP%]:hover{opacity:1}.label-caps[_ngcontent-%COMP%]{margin-left:15px;margin-top:10px;font-size:15px;color:#fff}.flex-buck-icon[_ngcontent-%COMP%]{display:flex;justify-content:flex-end;width:150px}.div-buck-icon[_ngcontent-%COMP%]{display:flex;justify-content:center;padding:25px 0}.flex-databuck-icon[_ngcontent-%COMP%]{display:flex;justify-content:flex-start;width:250px}.pad-0[_ngcontent-%COMP%]{padding:0}"]}),F),W=((E=function(){function e(){n(this,e),this.capsLock=new d.p}return o(e,[{key:"onKeyDown",value:function(e){this.capsLock.emit(e.getModifierState&&e.getModifierState("CapsLock"))}},{key:"onKeyUp",value:function(e){this.capsLock.emit(e.getModifierState&&e.getModifierState("CapsLock"))}}]),e}()).\u0275fac=function(e){return new(e||E)},E.\u0275dir=d.Yb({type:E,selectors:[["","capsLock",""]],hostBindings:function(e,t){1&e&&d.qc("keydown",function(e){return t.onKeyDown(e)},!1,d.Qc)("keyup",function(e){return t.onKeyUp(e)},!1,d.Qc)},outputs:{capsLock:"capsLock"}}),E),X=[{path:"",pathMatch:"full",component:q}],Q=((Z=o(function e(){n(this,e)})).\u0275fac=function(e){return new(e||Z)},Z.\u0275mod=d.bc({type:Z}),Z.\u0275inj=d.ac({providers:[],imports:[[a.b,C.b,w,_.a,l.l.forChild(X),I.a,M,y.forRoot(),x.a]]}),Z)}}])}();