(window.webpackJsonp=window.webpackJsonp||[]).push([[39],{wMS7:function(e,t,n){e.exports=function(){"use strict";function e(t){return(e="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"==typeof Symbol&&e.constructor===Symbol&&e!==Symbol.prototype?"symbol":typeof e})(t)}function t(e,n){return(t=Object.setPrototypeOf||function(e,t){return e.__proto__=t,e})(e,n)}function n(){if("undefined"==typeof Reflect||!Reflect.construct)return!1;if(Reflect.construct.sham)return!1;if("function"==typeof Proxy)return!0;try{return Boolean.prototype.valueOf.call(Reflect.construct(Boolean,[],function(){})),!0}catch(e){return!1}}function r(e,o,a){return(r=n()?Reflect.construct:function(e,n,r){var o=[null];o.push.apply(o,n);var a=new(Function.bind.apply(e,o));return r&&t(a,r.prototype),a}).apply(null,arguments)}function o(e){return function(e){if(Array.isArray(e))return a(e)}(e)||function(e){if("undefined"!=typeof Symbol&&null!=e[Symbol.iterator]||null!=e["@@iterator"])return Array.from(e)}(e)||function(e,t){if(e){if("string"==typeof e)return a(e,undefined);var n=Object.prototype.toString.call(e).slice(8,-1);return"Object"===n&&e.constructor&&(n=e.constructor.name),"Map"===n||"Set"===n?Array.from(e):"Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)?a(e,undefined):void 0}}(e)||function(){throw new TypeError("Invalid attempt to spread non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}()}function a(e,t){(null==t||t>e.length)&&(t=e.length);for(var n=0,r=new Array(t);n<t;n++)r[n]=e[n];return r}var i=Object.hasOwnProperty,l=Object.setPrototypeOf,c=Object.isFrozen,s=Object.getPrototypeOf,u=Object.getOwnPropertyDescriptor,m=Object.freeze,f=Object.seal,p=Object.create,d="undefined"!=typeof Reflect&&Reflect,h=d.apply,g=d.construct;h||(h=function(e,t,n){return e.apply(t,n)}),m||(m=function(e){return e}),f||(f=function(e){return e}),g||(g=function(e,t){return r(e,o(t))});var y,b=L(Array.prototype.forEach),v=L(Array.prototype.pop),T=L(Array.prototype.push),N=L(String.prototype.toLowerCase),E=L(String.prototype.toString),A=L(String.prototype.match),w=L(String.prototype.replace),S=L(String.prototype.indexOf),_=L(String.prototype.trim),x=L(RegExp.prototype.test),k=(y=TypeError,function(){for(var e=arguments.length,t=new Array(e),n=0;n<e;n++)t[n]=arguments[n];return g(y,t)});function L(e){return function(t){for(var n=arguments.length,r=new Array(n>1?n-1:0),o=1;o<n;o++)r[o-1]=arguments[o];return h(e,t,r)}}function O(e,t,n){n=n||N,l&&l(e,null);for(var r=t.length;r--;){var o=t[r];if("string"==typeof o){var a=n(o);a!==o&&(c(t)||(t[r]=a),o=a)}e[o]=!0}return e}function D(e){var t,n=p(null);for(t in e)!0===h(i,e,[t])&&(n[t]=e[t]);return n}function M(e,t){for(;null!==e;){var n=u(e,t);if(n){if(n.get)return L(n.get);if("function"==typeof n.value)return L(n.value)}e=s(e)}return function(e){return console.warn("fallback value for",e),null}}var R=m(["a","abbr","acronym","address","area","article","aside","audio","b","bdi","bdo","big","blink","blockquote","body","br","button","canvas","caption","center","cite","code","col","colgroup","content","data","datalist","dd","decorator","del","details","dfn","dialog","dir","div","dl","dt","element","em","fieldset","figcaption","figure","font","footer","form","h1","h2","h3","h4","h5","h6","head","header","hgroup","hr","html","i","img","input","ins","kbd","label","legend","li","main","map","mark","marquee","menu","menuitem","meter","nav","nobr","ol","optgroup","option","output","p","picture","pre","progress","q","rp","rt","ruby","s","samp","section","select","shadow","small","source","spacer","span","strike","strong","style","sub","summary","sup","table","tbody","td","template","textarea","tfoot","th","thead","time","tr","track","tt","u","ul","var","video","wbr"]),C=m(["svg","a","altglyph","altglyphdef","altglyphitem","animatecolor","animatemotion","animatetransform","circle","clippath","defs","desc","ellipse","filter","font","g","glyph","glyphref","hkern","image","line","lineargradient","marker","mask","metadata","mpath","path","pattern","polygon","polyline","radialgradient","rect","stop","style","switch","symbol","text","textpath","title","tref","tspan","view","vkern"]),I=m(["feBlend","feColorMatrix","feComponentTransfer","feComposite","feConvolveMatrix","feDiffuseLighting","feDisplacementMap","feDistantLight","feFlood","feFuncA","feFuncB","feFuncG","feFuncR","feGaussianBlur","feImage","feMerge","feMergeNode","feMorphology","feOffset","fePointLight","feSpecularLighting","feSpotLight","feTile","feTurbulence"]),F=m(["animate","color-profile","cursor","discard","fedropshadow","font-face","font-face-format","font-face-name","font-face-src","font-face-uri","foreignobject","hatch","hatchpath","mesh","meshgradient","meshpatch","meshrow","missing-glyph","script","set","solidcolor","unknown","use"]),U=m(["math","menclose","merror","mfenced","mfrac","mglyph","mi","mlabeledtr","mmultiscripts","mn","mo","mover","mpadded","mphantom","mroot","mrow","ms","mspace","msqrt","mstyle","msub","msup","msubsup","mtable","mtd","mtext","mtr","munder","munderover"]),H=m(["maction","maligngroup","malignmark","mlongdiv","mscarries","mscarry","msgroup","mstack","msline","msrow","semantics","annotation","annotation-xml","mprescripts","none"]),z=m(["#text"]),P=m(["accept","action","align","alt","autocapitalize","autocomplete","autopictureinpicture","autoplay","background","bgcolor","border","capture","cellpadding","cellspacing","checked","cite","class","clear","color","cols","colspan","controls","controlslist","coords","crossorigin","datetime","decoding","default","dir","disabled","disablepictureinpicture","disableremoteplayback","download","draggable","enctype","enterkeyhint","face","for","headers","height","hidden","high","href","hreflang","id","inputmode","integrity","ismap","kind","label","lang","list","loading","loop","low","max","maxlength","media","method","min","minlength","multiple","muted","name","nonce","noshade","novalidate","nowrap","open","optimum","pattern","placeholder","playsinline","poster","preload","pubdate","radiogroup","readonly","rel","required","rev","reversed","role","rows","rowspan","spellcheck","scope","selected","shape","size","sizes","span","srclang","start","src","srcset","step","style","summary","tabindex","title","translate","type","usemap","valign","value","width","xmlns","slot"]),B=m(["accent-height","accumulate","additive","alignment-baseline","ascent","attributename","attributetype","azimuth","basefrequency","baseline-shift","begin","bias","by","class","clip","clippathunits","clip-path","clip-rule","color","color-interpolation","color-interpolation-filters","color-profile","color-rendering","cx","cy","d","dx","dy","diffuseconstant","direction","display","divisor","dur","edgemode","elevation","end","fill","fill-opacity","fill-rule","filter","filterunits","flood-color","flood-opacity","font-family","font-size","font-size-adjust","font-stretch","font-style","font-variant","font-weight","fx","fy","g1","g2","glyph-name","glyphref","gradientunits","gradienttransform","height","href","id","image-rendering","in","in2","k","k1","k2","k3","k4","kerning","keypoints","keysplines","keytimes","lang","lengthadjust","letter-spacing","kernelmatrix","kernelunitlength","lighting-color","local","marker-end","marker-mid","marker-start","markerheight","markerunits","markerwidth","maskcontentunits","maskunits","max","mask","media","method","mode","min","name","numoctaves","offset","operator","opacity","order","orient","orientation","origin","overflow","paint-order","path","pathlength","patterncontentunits","patterntransform","patternunits","points","preservealpha","preserveaspectratio","primitiveunits","r","rx","ry","radius","refx","refy","repeatcount","repeatdur","restart","result","rotate","scale","seed","shape-rendering","specularconstant","specularexponent","spreadmethod","startoffset","stddeviation","stitchtiles","stop-color","stop-opacity","stroke-dasharray","stroke-dashoffset","stroke-linecap","stroke-linejoin","stroke-miterlimit","stroke-opacity","stroke","stroke-width","style","surfacescale","systemlanguage","tabindex","targetx","targety","transform","transform-origin","text-anchor","text-decoration","text-rendering","textlength","type","u1","u2","unicode","values","viewbox","visibility","version","vert-adv-y","vert-origin-x","vert-origin-y","width","word-spacing","wrap","writing-mode","xchannelselector","ychannelselector","x","x1","x2","xmlns","y","y1","y2","z","zoomandpan"]),j=m(["accent","accentunder","align","bevelled","close","columnsalign","columnlines","columnspan","denomalign","depth","dir","display","displaystyle","encoding","fence","frame","height","href","id","largeop","length","linethickness","lspace","lquote","mathbackground","mathcolor","mathsize","mathvariant","maxsize","minsize","movablelimits","notation","numalign","open","rowalign","rowlines","rowspacing","rowspan","rspace","rquote","scriptlevel","scriptminsize","scriptsizemultiplier","selection","separator","separators","stretchy","subscriptshift","supscriptshift","symmetric","voffset","width","xmlns"]),G=m(["xlink:href","xml:id","xlink:title","xml:space","xmlns:xlink"]),W=f(/\{\{[\w\W]*|[\w\W]*\}\}/gm),q=f(/<%[\w\W]*|[\w\W]*%>/gm),Y=f(/\${[\w\W]*}/gm),$=f(/^data-[\-\w.\u00B7-\uFFFF]/),K=f(/^aria-[\-\w]+$/),V=f(/^(?:(?:(?:f|ht)tps?|mailto|tel|callto|cid|xmpp):|[^a-z]|[a-z+.\-]+(?:[^a-z+.\-:]|$))/i),J=f(/^(?:\w+script|data):/i),X=f(/[\u0000-\u0020\u00A0\u1680\u180E\u2000-\u2029\u205F\u3000]/g),Z=f(/^html$/i),Q=function(){return"undefined"==typeof window?null:window},ee=function(t,n){if("object"!==e(t)||"function"!=typeof t.createPolicy)return null;var r=null;n.currentScript&&n.currentScript.hasAttribute("data-tt-policy-suffix")&&(r=n.currentScript.getAttribute("data-tt-policy-suffix"));var o="dompurify"+(r?"#"+r:"");try{return t.createPolicy(o,{createHTML:function(e){return e},createScriptURL:function(e){return e}})}catch(a){return console.warn("TrustedTypes policy "+o+" could not be created."),null}};return function t(){var n=arguments.length>0&&void 0!==arguments[0]?arguments[0]:Q(),r=function(e){return t(e)};if(r.version="2.4.5",r.removed=[],!n||!n.document||9!==n.document.nodeType)return r.isSupported=!1,r;var a=n.document,i=n.document,l=n.DocumentFragment,c=n.HTMLTemplateElement,s=n.Node,u=n.Element,f=n.NodeFilter,p=n.NamedNodeMap,d=void 0===p?n.NamedNodeMap||n.MozNamedAttrMap:p,h=n.HTMLFormElement,g=n.DOMParser,y=n.trustedTypes,L=u.prototype,te=M(L,"cloneNode"),ne=M(L,"nextSibling"),re=M(L,"childNodes"),oe=M(L,"parentNode");if("function"==typeof c){var ae=i.createElement("template");ae.content&&ae.content.ownerDocument&&(i=ae.content.ownerDocument)}var ie=ee(y,a),le=ie?ie.createHTML(""):"",ce=i.implementation,se=i.createNodeIterator,ue=i.createDocumentFragment,me=i.getElementsByTagName,fe=a.importNode,pe={};try{pe=D(i).documentMode?i.documentMode:{}}catch(Mt){}var de={};r.isSupported="function"==typeof oe&&ce&&void 0!==ce.createHTMLDocument&&9!==pe;var he,ge,ye=W,be=q,ve=Y,Te=$,Ne=K,Ee=J,Ae=X,we=V,Se=null,_e=O({},[].concat(o(R),o(C),o(I),o(U),o(z))),xe=null,ke=O({},[].concat(o(P),o(B),o(j),o(G))),Le=Object.seal(Object.create(null,{tagNameCheck:{writable:!0,configurable:!1,enumerable:!0,value:null},attributeNameCheck:{writable:!0,configurable:!1,enumerable:!0,value:null},allowCustomizedBuiltInElements:{writable:!0,configurable:!1,enumerable:!0,value:!1}})),Oe=null,De=null,Me=!0,Re=!0,Ce=!1,Ie=!0,Fe=!1,Ue=!1,He=!1,ze=!1,Pe=!1,Be=!1,je=!1,Ge=!0,We=!1,qe="user-content-",Ye=!0,$e=!1,Ke={},Ve=null,Je=O({},["annotation-xml","audio","colgroup","desc","foreignobject","head","iframe","math","mi","mn","mo","ms","mtext","noembed","noframes","noscript","plaintext","script","style","svg","template","thead","title","video","xmp"]),Xe=null,Ze=O({},["audio","video","img","source","image","track"]),Qe=null,et=O({},["alt","class","for","id","label","name","pattern","placeholder","role","summary","title","value","style","xmlns"]),tt="http://www.w3.org/1998/Math/MathML",nt="http://www.w3.org/2000/svg",rt="http://www.w3.org/1999/xhtml",ot=rt,at=!1,it=null,lt=O({},[tt,nt,rt],E),ct=["application/xhtml+xml","text/html"],st="text/html",ut=null,mt=i.createElement("form"),ft=function(e){return e instanceof RegExp||e instanceof Function},pt=function(t){ut&&ut===t||(t&&"object"===e(t)||(t={}),t=D(t),he=he=-1===ct.indexOf(t.PARSER_MEDIA_TYPE)?st:t.PARSER_MEDIA_TYPE,ge="application/xhtml+xml"===he?E:N,Se="ALLOWED_TAGS"in t?O({},t.ALLOWED_TAGS,ge):_e,xe="ALLOWED_ATTR"in t?O({},t.ALLOWED_ATTR,ge):ke,it="ALLOWED_NAMESPACES"in t?O({},t.ALLOWED_NAMESPACES,E):lt,Qe="ADD_URI_SAFE_ATTR"in t?O(D(et),t.ADD_URI_SAFE_ATTR,ge):et,Xe="ADD_DATA_URI_TAGS"in t?O(D(Ze),t.ADD_DATA_URI_TAGS,ge):Ze,Ve="FORBID_CONTENTS"in t?O({},t.FORBID_CONTENTS,ge):Je,Oe="FORBID_TAGS"in t?O({},t.FORBID_TAGS,ge):{},De="FORBID_ATTR"in t?O({},t.FORBID_ATTR,ge):{},Ke="USE_PROFILES"in t&&t.USE_PROFILES,Me=!1!==t.ALLOW_ARIA_ATTR,Re=!1!==t.ALLOW_DATA_ATTR,Ce=t.ALLOW_UNKNOWN_PROTOCOLS||!1,Ie=!1!==t.ALLOW_SELF_CLOSE_IN_ATTR,Fe=t.SAFE_FOR_TEMPLATES||!1,Ue=t.WHOLE_DOCUMENT||!1,Pe=t.RETURN_DOM||!1,Be=t.RETURN_DOM_FRAGMENT||!1,je=t.RETURN_TRUSTED_TYPE||!1,ze=t.FORCE_BODY||!1,Ge=!1!==t.SANITIZE_DOM,We=t.SANITIZE_NAMED_PROPS||!1,Ye=!1!==t.KEEP_CONTENT,$e=t.IN_PLACE||!1,we=t.ALLOWED_URI_REGEXP||we,ot=t.NAMESPACE||rt,Le=t.CUSTOM_ELEMENT_HANDLING||{},t.CUSTOM_ELEMENT_HANDLING&&ft(t.CUSTOM_ELEMENT_HANDLING.tagNameCheck)&&(Le.tagNameCheck=t.CUSTOM_ELEMENT_HANDLING.tagNameCheck),t.CUSTOM_ELEMENT_HANDLING&&ft(t.CUSTOM_ELEMENT_HANDLING.attributeNameCheck)&&(Le.attributeNameCheck=t.CUSTOM_ELEMENT_HANDLING.attributeNameCheck),t.CUSTOM_ELEMENT_HANDLING&&"boolean"==typeof t.CUSTOM_ELEMENT_HANDLING.allowCustomizedBuiltInElements&&(Le.allowCustomizedBuiltInElements=t.CUSTOM_ELEMENT_HANDLING.allowCustomizedBuiltInElements),Fe&&(Re=!1),Be&&(Pe=!0),Ke&&(Se=O({},o(z)),xe=[],!0===Ke.html&&(O(Se,R),O(xe,P)),!0===Ke.svg&&(O(Se,C),O(xe,B),O(xe,G)),!0===Ke.svgFilters&&(O(Se,I),O(xe,B),O(xe,G)),!0===Ke.mathMl&&(O(Se,U),O(xe,j),O(xe,G))),t.ADD_TAGS&&(Se===_e&&(Se=D(Se)),O(Se,t.ADD_TAGS,ge)),t.ADD_ATTR&&(xe===ke&&(xe=D(xe)),O(xe,t.ADD_ATTR,ge)),t.ADD_URI_SAFE_ATTR&&O(Qe,t.ADD_URI_SAFE_ATTR,ge),t.FORBID_CONTENTS&&(Ve===Je&&(Ve=D(Ve)),O(Ve,t.FORBID_CONTENTS,ge)),Ye&&(Se["#text"]=!0),Ue&&O(Se,["html","head","body"]),Se.table&&(O(Se,["tbody"]),delete Oe.tbody),m&&m(t),ut=t)},dt=O({},["mi","mo","mn","ms","mtext"]),ht=O({},["foreignobject","desc","title","annotation-xml"]),gt=O({},["title","style","font","a","script"]),yt=O({},C);O(yt,I),O(yt,F);var bt=O({},U);O(bt,H);var vt=function(e){var t=oe(e);t&&t.tagName||(t={namespaceURI:ot,tagName:"template"});var n=N(e.tagName),r=N(t.tagName);return!!it[e.namespaceURI]&&(e.namespaceURI===nt?t.namespaceURI===rt?"svg"===n:t.namespaceURI===tt?"svg"===n&&("annotation-xml"===r||dt[r]):Boolean(yt[n]):e.namespaceURI===tt?t.namespaceURI===rt?"math"===n:t.namespaceURI===nt?"math"===n&&ht[r]:Boolean(bt[n]):e.namespaceURI===rt?!(t.namespaceURI===nt&&!ht[r])&&!(t.namespaceURI===tt&&!dt[r])&&!bt[n]&&(gt[n]||!yt[n]):!("application/xhtml+xml"!==he||!it[e.namespaceURI]))},Tt=function(e){T(r.removed,{element:e});try{e.parentNode.removeChild(e)}catch(Mt){try{e.outerHTML=le}catch(Mt){e.remove()}}},Nt=function(e,t){try{T(r.removed,{attribute:t.getAttributeNode(e),from:t})}catch(Mt){T(r.removed,{attribute:null,from:t})}if(t.removeAttribute(e),"is"===e&&!xe[e])if(Pe||Be)try{Tt(t)}catch(Mt){}else try{t.setAttribute(e,"")}catch(Mt){}},Et=function(e){var t,n;if(ze)e="<remove></remove>"+e;else{var r=A(e,/^[\r\n\t ]+/);n=r&&r[0]}"application/xhtml+xml"===he&&ot===rt&&(e='<html xmlns="http://www.w3.org/1999/xhtml"><head></head><body>'+e+"</body></html>");var o=ie?ie.createHTML(e):e;if(ot===rt)try{t=(new g).parseFromString(o,he)}catch(Mt){}if(!t||!t.documentElement){t=ce.createDocument(ot,"template",null);try{t.documentElement.innerHTML=at?le:o}catch(Mt){}}var a=t.body||t.documentElement;return e&&n&&a.insertBefore(i.createTextNode(n),a.childNodes[0]||null),ot===rt?me.call(t,Ue?"html":"body")[0]:Ue?t.documentElement:a},At=function(e){return se.call(e.ownerDocument||e,e,f.SHOW_ELEMENT|f.SHOW_COMMENT|f.SHOW_TEXT,null,!1)},wt=function(e){return e instanceof h&&("string"!=typeof e.nodeName||"string"!=typeof e.textContent||"function"!=typeof e.removeChild||!(e.attributes instanceof d)||"function"!=typeof e.removeAttribute||"function"!=typeof e.setAttribute||"string"!=typeof e.namespaceURI||"function"!=typeof e.insertBefore||"function"!=typeof e.hasChildNodes)},St=function(t){return"object"===e(s)?t instanceof s:t&&"object"===e(t)&&"number"==typeof t.nodeType&&"string"==typeof t.nodeName},_t=function(e,t,n){de[e]&&b(de[e],function(e){e.call(r,t,n,ut)})},xt=function(e){var t;if(_t("beforeSanitizeElements",e,null),wt(e))return Tt(e),!0;if(x(/[\u0080-\uFFFF]/,e.nodeName))return Tt(e),!0;var n=ge(e.nodeName);if(_t("uponSanitizeElement",e,{tagName:n,allowedTags:Se}),e.hasChildNodes()&&!St(e.firstElementChild)&&(!St(e.content)||!St(e.content.firstElementChild))&&x(/<[/\w]/g,e.innerHTML)&&x(/<[/\w]/g,e.textContent))return Tt(e),!0;if("select"===n&&x(/<template/i,e.innerHTML))return Tt(e),!0;if(!Se[n]||Oe[n]){if(!Oe[n]&&Lt(n)){if(Le.tagNameCheck instanceof RegExp&&x(Le.tagNameCheck,n))return!1;if(Le.tagNameCheck instanceof Function&&Le.tagNameCheck(n))return!1}if(Ye&&!Ve[n]){var o=oe(e)||e.parentNode,a=re(e)||e.childNodes;if(a&&o)for(var i=a.length-1;i>=0;--i)o.insertBefore(te(a[i],!0),ne(e))}return Tt(e),!0}return e instanceof u&&!vt(e)?(Tt(e),!0):"noscript"!==n&&"noembed"!==n||!x(/<\/no(script|embed)/i,e.innerHTML)?(Fe&&3===e.nodeType&&(t=w(t=e.textContent,ye," "),t=w(t,be," "),t=w(t,ve," "),e.textContent!==t&&(T(r.removed,{element:e.cloneNode()}),e.textContent=t)),_t("afterSanitizeElements",e,null),!1):(Tt(e),!0)},kt=function(e,t,n){if(Ge&&("id"===t||"name"===t)&&(n in i||n in mt))return!1;if(Re&&!De[t]&&x(Te,t));else if(Me&&x(Ne,t));else if(!xe[t]||De[t]){if(!(Lt(e)&&(Le.tagNameCheck instanceof RegExp&&x(Le.tagNameCheck,e)||Le.tagNameCheck instanceof Function&&Le.tagNameCheck(e))&&(Le.attributeNameCheck instanceof RegExp&&x(Le.attributeNameCheck,t)||Le.attributeNameCheck instanceof Function&&Le.attributeNameCheck(t))||"is"===t&&Le.allowCustomizedBuiltInElements&&(Le.tagNameCheck instanceof RegExp&&x(Le.tagNameCheck,n)||Le.tagNameCheck instanceof Function&&Le.tagNameCheck(n))))return!1}else if(Qe[t]);else if(x(we,w(n,Ae,"")));else if("src"!==t&&"xlink:href"!==t&&"href"!==t||"script"===e||0!==S(n,"data:")||!Xe[e])if(Ce&&!x(Ee,w(n,Ae,"")));else if(n)return!1;return!0},Lt=function(e){return e.indexOf("-")>0},Ot=function(t){var n,o,a,i;_t("beforeSanitizeAttributes",t,null);var l=t.attributes;if(l){var c={attrName:"",attrValue:"",keepAttr:!0,allowedAttributes:xe};for(i=l.length;i--;){var s=(n=l[i]).name,u=n.namespaceURI;if(o="value"===s?n.value:_(n.value),a=ge(s),c.attrName=a,c.attrValue=o,c.keepAttr=!0,c.forceKeepAttr=void 0,_t("uponSanitizeAttribute",t,c),o=c.attrValue,!c.forceKeepAttr&&(Nt(s,t),c.keepAttr))if(Ie||!x(/\/>/i,o)){Fe&&(o=w(o,ye," "),o=w(o,be," "),o=w(o,ve," "));var m=ge(t.nodeName);if(kt(m,a,o)){if(!We||"id"!==a&&"name"!==a||(Nt(s,t),o=qe+o),ie&&"object"===e(y)&&"function"==typeof y.getAttributeType)if(u);else switch(y.getAttributeType(m,a)){case"TrustedHTML":o=ie.createHTML(o);break;case"TrustedScriptURL":o=ie.createScriptURL(o)}try{u?t.setAttributeNS(u,s,o):t.setAttribute(s,o),v(r.removed)}catch(Mt){}}}else Nt(s,t)}_t("afterSanitizeAttributes",t,null)}},Dt=function e(t){var n,r=At(t);for(_t("beforeSanitizeShadowDOM",t,null);n=r.nextNode();)_t("uponSanitizeShadowNode",n,null),xt(n)||(n.content instanceof l&&e(n.content),Ot(n));_t("afterSanitizeShadowDOM",t,null)};return r.sanitize=function(t){var o,i,c,u,m,f=arguments.length>1&&void 0!==arguments[1]?arguments[1]:{};if((at=!t)&&(t="\x3c!--\x3e"),"string"!=typeof t&&!St(t)){if("function"!=typeof t.toString)throw k("toString is not a function");if("string"!=typeof(t=t.toString()))throw k("dirty is not a string, aborting")}if(!r.isSupported){if("object"===e(n.toStaticHTML)||"function"==typeof n.toStaticHTML){if("string"==typeof t)return n.toStaticHTML(t);if(St(t))return n.toStaticHTML(t.outerHTML)}return t}if(He||pt(f),r.removed=[],"string"==typeof t&&($e=!1),$e){if(t.nodeName){var p=ge(t.nodeName);if(!Se[p]||Oe[p])throw k("root node is forbidden and cannot be sanitized in-place")}}else if(t instanceof s)1===(i=(o=Et("\x3c!----\x3e")).ownerDocument.importNode(t,!0)).nodeType&&"BODY"===i.nodeName||"HTML"===i.nodeName?o=i:o.appendChild(i);else{if(!Pe&&!Fe&&!Ue&&-1===t.indexOf("<"))return ie&&je?ie.createHTML(t):t;if(!(o=Et(t)))return Pe?null:je?le:""}o&&ze&&Tt(o.firstChild);for(var d=At($e?t:o);c=d.nextNode();)3===c.nodeType&&c===u||xt(c)||(c.content instanceof l&&Dt(c.content),Ot(c),u=c);if(u=null,$e)return t;if(Pe){if(Be)for(m=ue.call(o.ownerDocument);o.firstChild;)m.appendChild(o.firstChild);else m=o;return(xe.shadowroot||xe.shadowrootmod)&&(m=fe.call(a,m,!0)),m}var h=Ue?o.outerHTML:o.innerHTML;return Ue&&Se["!doctype"]&&o.ownerDocument&&o.ownerDocument.doctype&&o.ownerDocument.doctype.name&&x(Z,o.ownerDocument.doctype.name)&&(h="<!DOCTYPE "+o.ownerDocument.doctype.name+">\n"+h),Fe&&(h=w(h,ye," "),h=w(h,be," "),h=w(h,ve," ")),ie&&je?ie.createHTML(h):h},r.setConfig=function(e){pt(e),He=!0},r.clearConfig=function(){ut=null,He=!1},r.isValidAttribute=function(e,t,n){ut||pt({});var r=ge(e),o=ge(t);return kt(r,o,n)},r.addHook=function(e,t){"function"==typeof t&&(de[e]=de[e]||[],T(de[e],t))},r.removeHook=function(e){if(de[e])return v(de[e])},r.removeHooks=function(e){de[e]&&(de[e]=[])},r.removeAllHooks=function(){de={}},r}()}()}}]);