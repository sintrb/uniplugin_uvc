!function(t){var e={};function n(r){if(e[r])return e[r].exports;var i=e[r]={i:r,l:!1,exports:{}};return t[r].call(i.exports,i,i.exports,n),i.l=!0,i.exports}n.m=t,n.c=e,n.d=function(t,e,r){n.o(t,e)||Object.defineProperty(t,e,{enumerable:!0,get:r})},n.r=function(t){"undefined"!=typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(t,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(t,"__esModule",{value:!0})},n.t=function(t,e){if(1&e&&(t=n(t)),8&e)return t;if(4&e&&"object"==typeof t&&t&&t.__esModule)return t;var r=Object.create(null);if(n.r(r),Object.defineProperty(r,"default",{enumerable:!0,value:t}),2&e&&"string"!=typeof t)for(var i in t)n.d(r,i,function(e){return t[e]}.bind(null,i));return r},n.n=function(t){var e=t&&t.__esModule?function(){return t.default}:function(){return t};return n.d(e,"a",e),e},n.o=function(t,e){return Object.prototype.hasOwnProperty.call(t,e)},n.p="",n(n.s=8)}([function(t,e,n){"use strict";var r=n(5),i=n(2),o=n(7);var a=Object(o.a)(i.default,r.b,r.c,!1,null,null,"44b1c770",!1,r.a,void 0);(function(t){this.options.style||(this.options.style={}),Vue.prototype.__merge_style&&Vue.prototype.__$appStyle__&&Vue.prototype.__merge_style(Vue.prototype.__$appStyle__,this.options.style),Vue.prototype.__merge_style?Vue.prototype.__merge_style(n(6).default,this.options.style):Object.assign(this.options.style,n(6).default)}).call(a),e.default=a.exports},function(t,e){t.exports={"uni-icon":{fontFamily:"uniicons",fontWeight:"normal"},"uni-bg-red":{backgroundColor:"#F76260",color:"#FFFFFF"},"uni-bg-green":{backgroundColor:"#09BB07",color:"#FFFFFF"},"uni-bg-blue":{backgroundColor:"#007AFF",color:"#FFFFFF"},"uni-container":{flex:1,paddingTop:"15",paddingRight:"15",paddingBottom:"15",paddingLeft:"15",backgroundColor:"#f8f8f8"},"uni-padding-lr":{paddingLeft:"15",paddingRight:"15"},"uni-padding-tb":{paddingTop:"15",paddingBottom:"15"},"uni-header-logo":{paddingTop:"15",paddingRight:"15",paddingBottom:"15",paddingLeft:"15",flexDirection:"column",justifyContent:"center",alignItems:"center",marginTop:"10upx"},"uni-header-image":{width:"80",height:"80"},"uni-hello-text":{marginBottom:"20"},"hello-text":{color:"#7A7E83",fontSize:"14",lineHeight:"20"},"hello-link":{color:"#7A7E83",fontSize:"14",lineHeight:"20"},"uni-panel":{marginBottom:"12"},"uni-panel-h":{backgroundColor:"#ffffff",flexDirection:"row",alignItems:"center",paddingTop:"12",paddingRight:"12",paddingBottom:"12",paddingLeft:"12"},"uni-panel-h-on":{backgroundColor:"#f0f0f0"},"uni-panel-text":{flex:1,color:"#000000",fontSize:"14",fontWeight:"normal"},"uni-panel-icon":{marginLeft:"15",color:"#999999",fontSize:"14",fontWeight:"normal",transform:"rotate(0deg)",transitionDuration:0,transitionProperty:"transform"},"uni-panel-icon-on":{transform:"rotate(180deg)"},"uni-navigate-item":{flexDirection:"row",alignItems:"center",backgroundColor:"#FFFFFF",borderTopStyle:"solid",borderTopColor:"#f0f0f0",borderTopWidth:"1",paddingTop:"12",paddingRight:"12",paddingBottom:"12",paddingLeft:"12","backgroundColor:active":"#f8f8f8"},"uni-navigate-text":{flex:1,color:"#000000",fontSize:"14",fontWeight:"normal"},"uni-navigate-icon":{marginLeft:"15",color:"#999999",fontSize:"14",fontWeight:"normal"},"uni-list-cell":{position:"relative",flexDirection:"row",justifyContent:"flex-start",alignItems:"center"},"uni-list-cell-pd":{paddingTop:"22upx",paddingRight:"30upx",paddingBottom:"22upx",paddingLeft:"30upx"},"flex-r":{flexDirection:"row"},"flex-c":{flexDirection:"column"},"a-i-c":{alignItems:"center"},"j-c-c":{justifyContent:"center"},"list-item":{flexDirection:"row",paddingTop:"10",paddingRight:"10",paddingBottom:"10",paddingLeft:"10"},"@VERSION":2}},function(t,e,n){"use strict";var r=n(3),i=n.n(r);e.default=i.a},function(t,e,n){"use strict";(function(t,r){Object.defineProperty(e,"__esModule",{value:!0}),e.default=void 0;var i,o=(i=n(14))&&i.__esModule?i:{default:i};function a(t,e,n,r,i,o,a){try{var c=t[o](a),u=c.value}catch(t){return void n(t)}c.done?e(u):Promise.resolve(u).then(r,i)}var c=t("sintrb-uvcmodule"),u={data:function(){return{show:!0,previewSizeI:-1,previewSizeList:[],rotation:0,logs:[],images:[]}},computed:{},watch:{previewSizeI:function(){this.addLog("previewSizeI "+this.previewSizeI),this.doIVAction("setPreviewSize",{index:this.previewSizeI})}},methods:{getUvcDevices:function(){var t=this;c.getUvcDevices({},(function(e){e.data.devices.map((function(t){JSON.parse(JSON.stringify(t));return t.showJson=!1,t.showPreview=!1,t})),r("log",JSON.stringify(e)," at pages/index/index.nvue:78"),t.devices=e.data.devices,t.addLog(e)}))},getSupportedPreviewSizes:function(){var t=this;this.doIVAction("getSupportedPreviewSizes",null,(function(e){e&&e.data&&(t.previewSizeList=e.data.items)}))},getSnap:function(){var t=this;this.doIVAction("snap",{},(function(e){t.addLog(e),e&&e.data&&t.addImg(e.data.path)}))},doIVAction:function(t,e,n){var r,i=this;return(r=o.default.mark((function r(){var a,c,u;return o.default.wrap((function(r){for(;;)switch(r.prev=r.next){case 0:if(a=i.$refs.iv){r.next=4;break}return i.res="\u6ca1\u6709iv "+Object.keys(i.$refs).join(","),r.abrupt("return");case 4:if(c=a[t]){r.next=8;break}return i.addLog("\u6ca1\u6709iv."+t+" "+Object.keys(a).join(",")),r.abrupt("return");case 8:u=[],e&&u.push(e),u.push((function(t){i.addLog(t),n&&n(t)})),i.res=u;try{c.apply(a,u)}catch(t){i.addLog("ERR "+t)}case 13:case"end":return r.stop()}}),r)})),function(){var t=this,e=arguments;return new Promise((function(n,i){var o=r.apply(t,e);function c(t){a(o,n,i,c,u,"next",t)}function u(t){a(o,n,i,c,u,"throw",t)}c(void 0)}))})()},onStatusChange:function(t){this.addLog(t.detail),5100!==t.detail.status||this.previewSizeList.length||this.getSupportedPreviewSizes()},addLog:function(t){"string"!=typeof t&&(t=JSON.stringify(t)),this.logs.unshift(t)},viewImg:function(t,e){uni.previewImage({urls:this.images.map((function(t){return t.src})),index:e})},addImg:function(t){this.images.splice(0,0,{src:t,key:Date.now()}),this.addLog(t)}}};e.default=u}).call(this,n(12).default,n(13).default)},function(t,e){t.exports={"mini-btn":{paddingTop:"5rpx",paddingRight:"5rpx",paddingBottom:"5rpx",paddingLeft:"5rpx"},btns:{display:"flex",flexDirection:"row",flexWrap:"wrap",alignItems:"center"},button:{paddingTop:"3",paddingRight:"5",paddingBottom:"3",paddingLeft:"5"},previews:{display:"flex",flexDirection:"row",flexWrap:"wrap",alignItems:"center",justifyContent:"center",marginBottom:"10"},"preview-wrap":{backgroundColor:"#000000",marginTop:"2",marginRight:"2",marginBottom:"2",marginLeft:"2",minWidth:"320",minHeight:"240"},preview:{width:"400",height:"300"},selected:{backgroundColor:"#FF0000"},"@VERSION":2}},function(t,e,n){"use strict";n.d(e,"b",(function(){return r})),n.d(e,"c",(function(){return i})),n.d(e,"a",(function(){}));var r=function(){var t=this,e=t.$createElement,n=t._self._c||e;return n("scroll-view",{staticStyle:{flexDirection:"column"},attrs:{scrollY:!0,showScrollbar:!0,enableBackToTop:!0,bubble:"true"}},[n("view",{staticStyle:{display:"flex",flexDirection:"column",fontSize:"12px"}},[t.show?n("view",{staticClass:["previews"]},[n("view",{staticClass:["preview-wrap"]},[n("sintrb-uvcviewer",{ref:"iv",staticClass:["preview"],attrs:{rotation:t.rotation},on:{onStatusChange:t.onStatusChange}})],1)]):t._e(),t.previewSizeList.length?n("view",{staticStyle:{display:"flex",flexDirection:"row",flexWrap:"wrap"}},t._l(t.previewSizeList,(function(e,r){return n("view",{class:{selected:t.previewSizeI===r},staticStyle:{border:"2rpx solid #eee",padding:"5rpx"},on:{click:function(e){t.previewSizeI=r}}},[n("u-text",[t._v(t._s(e.width)+"x"+t._s(e.height))])])})),0):t._e(),n("view",{staticClass:["flex","btns"]},[n("button",{staticClass:["grow1","button"],attrs:{size:"mini",type:"default"},on:{click:function(e){t.show=!t.show}}},[t._v(t._s(t.show?"\u5173\u95ed":"\u663e\u793a"))]),n("button",{staticClass:["grow1","button"],attrs:{size:"mini",type:"default"},on:{click:function(e){t.doIVAction("test")}}},[t._v("\u6d4b\u8bd5")]),n("button",{staticClass:["grow1","button"],attrs:{size:"mini",type:"default"},on:{click:function(e){t.doIVAction("start")}}},[t._v("\u5f00\u59cb")]),n("button",{staticClass:["grow1","button"],attrs:{size:"mini",type:"default"},on:{click:function(e){t.doIVAction("stop")}}},[t._v("\u505c\u6b62")]),n("button",{staticClass:["grow1","button"],attrs:{size:"mini",type:"default"},on:{click:function(e){t.getSnap()}}},[t._v("\u622a\u56fe")]),n("button",{staticClass:["grow1","button"],attrs:{size:"mini",type:"default"},on:{click:function(e){t.rotation=(t.rotation+90)%360}}},[t._v("\u65cb\u8f6c")]),n("button",{staticClass:["grow1","button"],attrs:{size:"mini",type:"default"},on:{click:function(e){t.getSupportedPreviewSizes()}}},[t._v("\u83b7\u53d6\u652f\u6301\u7684\u5c3a\u5bf8")]),n("button",{staticClass:["grow1","button"],attrs:{size:"mini",type:"default"},on:{click:function(e){t.getUvcDevices()}}},[t._v("\u83b7\u53d6USB\u8bbe\u5907\u5217\u8868")])],1),t.images.length?n("scroll-view",{staticStyle:{flexDirection:"row",marginTop:"5px"},attrs:{scrollX:"true"}},[n("view",{staticStyle:{display:"flex",flexDirection:"row"}},t._l(t.images,(function(e,r){return n("u-image",{key:e.key,staticStyle:{maxWidth:"60px",height:"60px",border:"1px solid red",marginRight:"1px"},attrs:{src:e.src,mode:"heightFix"},on:{click:function(n){t.viewImg(e,r)}}})})),1)]):t._e(),n("scroll-view",{staticClass:["logs"],staticStyle:{flexDirection:"column",marginTop:"5px"},attrs:{scrollY:"true"}},[n("view",{staticStyle:{display:"flex",flexDirection:"column"}},t._l(t.logs,(function(e){return n("view",{staticStyle:{marginTop:"1rpx",fontSize:"8px",width:"auto",border:"1rpx solid #EEEEEE",padding:"10rpx"}},[n("u-text",{appendAsTree:!0,attrs:{append:"tree"}},[t._v(t._s(e))])])})),0)])],1)])},i=[]},function(t,e,n){"use strict";n.r(e);var r=n(4),i=n.n(r);for(var o in r)"default"!==o&&function(t){n.d(e,t,(function(){return r[t]}))}(o);e.default=i.a},function(t,e,n){"use strict";function r(t,e,n,r,i,o,a,c,u,s){var l,f="function"==typeof t?t.options:t;if(u){f.components||(f.components={});var p=Object.prototype.hasOwnProperty;for(var d in u)p.call(u,d)&&!p.call(f.components,d)&&(f.components[d]=u[d])}if(s&&((s.beforeCreate||(s.beforeCreate=[])).unshift((function(){this[s.__module]=this})),(f.mixins||(f.mixins=[])).push(s)),e&&(f.render=e,f.staticRenderFns=n,f._compiled=!0),r&&(f.functional=!0),o&&(f._scopeId="data-v-"+o),a?(l=function(t){(t=t||this.$vnode&&this.$vnode.ssrContext||this.parent&&this.parent.$vnode&&this.parent.$vnode.ssrContext)||"undefined"==typeof __VUE_SSR_CONTEXT__||(t=__VUE_SSR_CONTEXT__),i&&i.call(this,t),t&&t._registeredComponents&&t._registeredComponents.add(a)},f._ssrRegister=l):i&&(l=c?function(){i.call(this,this.$root.$options.shadowRoot)}:i),l)if(f.functional){f._injectStyles=l;var h=f.render;f.render=function(t,e){return l.call(e),h(t,e)}}else{var g=f.beforeCreate;f.beforeCreate=g?[].concat(g,l):[l]}return{exports:t,options:f}}n.d(e,"a",(function(){return r}))},function(t,e,n){"use strict";n.r(e);n(9),n(11);var r=n(0);r.default.mpType="page",r.default.route="pages/index/index",r.default.el="#root",new Vue(r.default)},function(t,e,n){Vue.prototype.__$appStyle__={},Vue.prototype.__merge_style&&Vue.prototype.__merge_style(n(10).default,Vue.prototype.__$appStyle__)},function(t,e,n){"use strict";n.r(e);var r=n(1),i=n.n(r);for(var o in r)"default"!==o&&function(t){n.d(e,t,(function(){return r[t]}))}(o);e.default=i.a},function(t,e){if("undefined"==typeof Promise||Promise.prototype.finally||(Promise.prototype.finally=function(t){var e=this.constructor;return this.then((function(n){return e.resolve(t()).then((function(){return n}))}),(function(n){return e.resolve(t()).then((function(){throw n}))}))}),"undefined"!=typeof uni&&uni&&uni.requireGlobal){var n=uni.requireGlobal();ArrayBuffer=n.ArrayBuffer,Int8Array=n.Int8Array,Uint8Array=n.Uint8Array,Uint8ClampedArray=n.Uint8ClampedArray,Int16Array=n.Int16Array,Uint16Array=n.Uint16Array,Int32Array=n.Int32Array,Uint32Array=n.Uint32Array,Float32Array=n.Float32Array,Float64Array=n.Float64Array,BigInt64Array=n.BigInt64Array,BigUint64Array=n.BigUint64Array}},function(t,e,n){"use strict";Object.defineProperty(e,"__esModule",{value:!0}),e.default=function(t){return weex.requireModule(t)}},function(t,e,n){"use strict";function r(t){var e=Object.prototype.toString.call(t);return e.substring(8,e.length-1)}function i(){return"string"==typeof __channelId__&&__channelId__}function o(t,e){switch(r(e)){case"Function":return"function() { [native code] }";default:return e}}Object.defineProperty(e,"__esModule",{value:!0}),e.log=function(t){for(var e=arguments.length,n=new Array(e>1?e-1:0),r=1;r<e;r++)n[r-1]=arguments[r];console[t].apply(console,n)},e.default=function(){for(var t=arguments.length,e=new Array(t),n=0;n<t;n++)e[n]=arguments[n];var a=e.shift();if(i())return e.push(e.pop().replace("at ","uni-app:///")),console[a].apply(console,e);var c=e.map((function(t){var e=Object.prototype.toString.call(t).toLowerCase();if("[object object]"===e||"[object array]"===e)try{t="---BEGIN:JSON---"+JSON.stringify(t,o)+"---END:JSON---"}catch(n){t=e}else if(null===t)t="---NULL---";else if(void 0===t)t="---UNDEFINED---";else{var n=r(t).toUpperCase();t="NUMBER"===n||"BOOLEAN"===n?"---BEGIN:"+n+"---"+t+"---END:"+n+"---":String(t)}return t})),u="";if(c.length>1){var s=c.pop();u=c.join("---COMMA---"),0===s.indexOf(" at ")?u+=s:u+="---COMMA---"+s}else u=c[0];console[a](u)}},function(t,e,n){t.exports=n(15)},function(t,e,n){var r=function(t){"use strict";var e=Object.prototype,n=e.hasOwnProperty,r="function"==typeof Symbol?Symbol:{},i=r.iterator||"@@iterator",o=r.asyncIterator||"@@asyncIterator",a=r.toStringTag||"@@toStringTag";function c(t,e,n){return Object.defineProperty(t,e,{value:n,enumerable:!0,configurable:!0,writable:!0}),t[e]}try{c({},"")}catch(t){c=function(t,e,n){return t[e]=n}}function u(t,e,n,r){var i=e&&e.prototype instanceof f?e:f,o=Object.create(i.prototype),a=new S(r||[]);return o._invoke=function(t,e,n){var r="suspendedStart";return function(i,o){if("executing"===r)throw new Error("Generator is already running");if("completed"===r){if("throw"===i)throw o;return C()}for(n.method=i,n.arg=o;;){var a=n.delegate;if(a){var c=x(a,n);if(c){if(c===l)continue;return c}}if("next"===n.method)n.sent=n._sent=n.arg;else if("throw"===n.method){if("suspendedStart"===r)throw r="completed",n.arg;n.dispatchException(n.arg)}else"return"===n.method&&n.abrupt("return",n.arg);r="executing";var u=s(t,e,n);if("normal"===u.type){if(r=n.done?"completed":"suspendedYield",u.arg===l)continue;return{value:u.arg,done:n.done}}"throw"===u.type&&(r="completed",n.method="throw",n.arg=u.arg)}}}(t,n,a),o}function s(t,e,n){try{return{type:"normal",arg:t.call(e,n)}}catch(t){return{type:"throw",arg:t}}}t.wrap=u;var l={};function f(){}function p(){}function d(){}var h={};h[i]=function(){return this};var g=Object.getPrototypeOf,v=g&&g(g(L([])));v&&v!==e&&n.call(v,i)&&(h=v);var y=d.prototype=f.prototype=Object.create(h);function m(t){["next","throw","return"].forEach((function(e){c(t,e,(function(t){return this._invoke(e,t)}))}))}function w(t,e){var r;this._invoke=function(i,o){function a(){return new e((function(r,a){!function r(i,o,a,c){var u=s(t[i],t,o);if("throw"!==u.type){var l=u.arg,f=l.value;return f&&"object"==typeof f&&n.call(f,"__await")?e.resolve(f.__await).then((function(t){r("next",t,a,c)}),(function(t){r("throw",t,a,c)})):e.resolve(f).then((function(t){l.value=t,a(l)}),(function(t){return r("throw",t,a,c)}))}c(u.arg)}(i,o,r,a)}))}return r=r?r.then(a,a):a()}}function x(t,e){var n=t.iterator[e.method];if(void 0===n){if(e.delegate=null,"throw"===e.method){if(t.iterator.return&&(e.method="return",e.arg=void 0,x(t,e),"throw"===e.method))return l;e.method="throw",e.arg=new TypeError("The iterator does not provide a 'throw' method")}return l}var r=s(n,t.iterator,e.arg);if("throw"===r.type)return e.method="throw",e.arg=r.arg,e.delegate=null,l;var i=r.arg;return i?i.done?(e[t.resultName]=i.value,e.next=t.nextLoc,"return"!==e.method&&(e.method="next",e.arg=void 0),e.delegate=null,l):i:(e.method="throw",e.arg=new TypeError("iterator result is not an object"),e.delegate=null,l)}function _(t){var e={tryLoc:t[0]};1 in t&&(e.catchLoc=t[1]),2 in t&&(e.finallyLoc=t[2],e.afterLoc=t[3]),this.tryEntries.push(e)}function b(t){var e=t.completion||{};e.type="normal",delete e.arg,t.completion=e}function S(t){this.tryEntries=[{tryLoc:"root"}],t.forEach(_,this),this.reset(!0)}function L(t){if(t){var e=t[i];if(e)return e.call(t);if("function"==typeof t.next)return t;if(!isNaN(t.length)){var r=-1,o=function e(){for(;++r<t.length;)if(n.call(t,r))return e.value=t[r],e.done=!1,e;return e.value=void 0,e.done=!0,e};return o.next=o}}return{next:C}}function C(){return{value:void 0,done:!0}}return p.prototype=y.constructor=d,d.constructor=p,p.displayName=c(d,a,"GeneratorFunction"),t.isGeneratorFunction=function(t){var e="function"==typeof t&&t.constructor;return!!e&&(e===p||"GeneratorFunction"===(e.displayName||e.name))},t.mark=function(t){return Object.setPrototypeOf?Object.setPrototypeOf(t,d):(t.__proto__=d,c(t,a,"GeneratorFunction")),t.prototype=Object.create(y),t},t.awrap=function(t){return{__await:t}},m(w.prototype),w.prototype[o]=function(){return this},t.AsyncIterator=w,t.async=function(e,n,r,i,o){void 0===o&&(o=Promise);var a=new w(u(e,n,r,i),o);return t.isGeneratorFunction(n)?a:a.next().then((function(t){return t.done?t.value:a.next()}))},m(y),c(y,a,"Generator"),y[i]=function(){return this},y.toString=function(){return"[object Generator]"},t.keys=function(t){var e=[];for(var n in t)e.push(n);return e.reverse(),function n(){for(;e.length;){var r=e.pop();if(r in t)return n.value=r,n.done=!1,n}return n.done=!0,n}},t.values=L,S.prototype={constructor:S,reset:function(t){if(this.prev=0,this.next=0,this.sent=this._sent=void 0,this.done=!1,this.delegate=null,this.method="next",this.arg=void 0,this.tryEntries.forEach(b),!t)for(var e in this)"t"===e.charAt(0)&&n.call(this,e)&&!isNaN(+e.slice(1))&&(this[e]=void 0)},stop:function(){this.done=!0;var t=this.tryEntries[0].completion;if("throw"===t.type)throw t.arg;return this.rval},dispatchException:function(t){if(this.done)throw t;var e=this;function r(n,r){return a.type="throw",a.arg=t,e.next=n,r&&(e.method="next",e.arg=void 0),!!r}for(var i=this.tryEntries.length-1;i>=0;--i){var o=this.tryEntries[i],a=o.completion;if("root"===o.tryLoc)return r("end");if(o.tryLoc<=this.prev){var c=n.call(o,"catchLoc"),u=n.call(o,"finallyLoc");if(c&&u){if(this.prev<o.catchLoc)return r(o.catchLoc,!0);if(this.prev<o.finallyLoc)return r(o.finallyLoc)}else if(c){if(this.prev<o.catchLoc)return r(o.catchLoc,!0)}else{if(!u)throw new Error("try statement without catch or finally");if(this.prev<o.finallyLoc)return r(o.finallyLoc)}}}},abrupt:function(t,e){for(var r=this.tryEntries.length-1;r>=0;--r){var i=this.tryEntries[r];if(i.tryLoc<=this.prev&&n.call(i,"finallyLoc")&&this.prev<i.finallyLoc){var o=i;break}}o&&("break"===t||"continue"===t)&&o.tryLoc<=e&&e<=o.finallyLoc&&(o=null);var a=o?o.completion:{};return a.type=t,a.arg=e,o?(this.method="next",this.next=o.finallyLoc,l):this.complete(a)},complete:function(t,e){if("throw"===t.type)throw t.arg;return"break"===t.type||"continue"===t.type?this.next=t.arg:"return"===t.type?(this.rval=this.arg=t.arg,this.method="return",this.next="end"):"normal"===t.type&&e&&(this.next=e),l},finish:function(t){for(var e=this.tryEntries.length-1;e>=0;--e){var n=this.tryEntries[e];if(n.finallyLoc===t)return this.complete(n.completion,n.afterLoc),b(n),l}},catch:function(t){for(var e=this.tryEntries.length-1;e>=0;--e){var n=this.tryEntries[e];if(n.tryLoc===t){var r=n.completion;if("throw"===r.type){var i=r.arg;b(n)}return i}}throw new Error("illegal catch attempt")},delegateYield:function(t,e,n){return this.delegate={iterator:L(t),resultName:e,nextLoc:n},"next"===this.method&&(this.arg=void 0),l}},t}(t.exports);try{regeneratorRuntime=r}catch(t){Function("r","regeneratorRuntime = r")(r)}}]);