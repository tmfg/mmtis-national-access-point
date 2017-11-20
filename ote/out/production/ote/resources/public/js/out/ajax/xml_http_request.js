// Compiled by ClojureScript 1.9.908 {}
goog.provide('ajax.xml_http_request');
goog.require('cljs.core');
goog.require('ajax.protocols');
goog.require('goog.string');
ajax.xml_http_request.ready_state = (function ajax$xml_http_request$ready_state(e){
return new cljs.core.PersistentArrayMap(null, 5, [(0),new cljs.core.Keyword(null,"not-initialized","not-initialized",-1937378906),(1),new cljs.core.Keyword(null,"connection-established","connection-established",-1403749733),(2),new cljs.core.Keyword(null,"request-received","request-received",2110590540),(3),new cljs.core.Keyword(null,"processing-request","processing-request",-264947221),(4),new cljs.core.Keyword(null,"response-ready","response-ready",245208276)], null).call(null,e.target.readyState);
});
ajax.xml_http_request.append = (function ajax$xml_http_request$append(current,next){
if(cljs.core.truth_(current)){
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(current),", ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(next)].join('');
} else {
return next;
}
});
ajax.xml_http_request.process_headers = (function ajax$xml_http_request$process_headers(header_str){
if(cljs.core.truth_(header_str)){
return cljs.core.reduce.call(null,(function (headers,header_line){
if(cljs.core.truth_(goog.string.isEmptyOrWhitespace(header_line))){
return headers;
} else {
var key_value = goog.string.splitLimit(header_line,": ",(2));
return cljs.core.update.call(null,headers,(key_value[(0)]),ajax.xml_http_request.append,(key_value[(1)]));
}
}),cljs.core.PersistentArrayMap.EMPTY,header_str.split("\r\n"));
} else {
return cljs.core.PersistentArrayMap.EMPTY;
}
});
ajax.xml_http_request.xmlhttprequest = ((cljs.core._EQ_.call(null,cljs.core._STAR_target_STAR_,"nodejs"))?(function (){var xmlhttprequest = require("xmlhttprequest").XMLHttpRequest;
goog.object.set(global,"XMLHttpRequest",xmlhttprequest);

return xmlhttprequest;
})():window.XMLHttpRequest);
ajax.xml_http_request.xmlhttprequest.prototype.ajax$protocols$AjaxImpl$ = cljs.core.PROTOCOL_SENTINEL;

ajax.xml_http_request.xmlhttprequest.prototype.ajax$protocols$AjaxImpl$_js_ajax_request$arity$3 = (function (this$,p__41586,handler){
var map__41587 = p__41586;
var map__41587__$1 = ((((!((map__41587 == null)))?((((map__41587.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__41587.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__41587):map__41587);
var uri = cljs.core.get.call(null,map__41587__$1,new cljs.core.Keyword(null,"uri","uri",-774711847));
var method = cljs.core.get.call(null,map__41587__$1,new cljs.core.Keyword(null,"method","method",55703592));
var body = cljs.core.get.call(null,map__41587__$1,new cljs.core.Keyword(null,"body","body",-2049205669));
var headers = cljs.core.get.call(null,map__41587__$1,new cljs.core.Keyword(null,"headers","headers",-835030129));
var timeout = cljs.core.get.call(null,map__41587__$1,new cljs.core.Keyword(null,"timeout","timeout",-318625318),(0));
var with_credentials = cljs.core.get.call(null,map__41587__$1,new cljs.core.Keyword(null,"with-credentials","with-credentials",-1163127235),false);
var response_format = cljs.core.get.call(null,map__41587__$1,new cljs.core.Keyword(null,"response-format","response-format",1664465322));
var this$__$1 = this;
this$__$1.withCredentials = with_credentials;

this$__$1.onreadystatechange = ((function (this$__$1,map__41587,map__41587__$1,uri,method,body,headers,timeout,with_credentials,response_format){
return (function (p1__41585_SHARP_){
if(cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"response-ready","response-ready",245208276),ajax.xml_http_request.ready_state.call(null,p1__41585_SHARP_))){
return handler.call(null,this$__$1);
} else {
return null;
}
});})(this$__$1,map__41587,map__41587__$1,uri,method,body,headers,timeout,with_credentials,response_format))
;

this$__$1.open(method,uri,true);

this$__$1.timeout = timeout;

var temp__5290__auto___41599 = new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(response_format);
if(cljs.core.truth_(temp__5290__auto___41599)){
var response_type_41600 = temp__5290__auto___41599;
this$__$1.responseType = cljs.core.name.call(null,response_type_41600);
} else {
}

var seq__41589_41601 = cljs.core.seq.call(null,headers);
var chunk__41590_41602 = null;
var count__41591_41603 = (0);
var i__41592_41604 = (0);
while(true){
if((i__41592_41604 < count__41591_41603)){
var vec__41593_41605 = cljs.core._nth.call(null,chunk__41590_41602,i__41592_41604);
var k_41606 = cljs.core.nth.call(null,vec__41593_41605,(0),null);
var v_41607 = cljs.core.nth.call(null,vec__41593_41605,(1),null);
this$__$1.setRequestHeader(k_41606,v_41607);

var G__41608 = seq__41589_41601;
var G__41609 = chunk__41590_41602;
var G__41610 = count__41591_41603;
var G__41611 = (i__41592_41604 + (1));
seq__41589_41601 = G__41608;
chunk__41590_41602 = G__41609;
count__41591_41603 = G__41610;
i__41592_41604 = G__41611;
continue;
} else {
var temp__5290__auto___41612 = cljs.core.seq.call(null,seq__41589_41601);
if(temp__5290__auto___41612){
var seq__41589_41613__$1 = temp__5290__auto___41612;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__41589_41613__$1)){
var c__31106__auto___41614 = cljs.core.chunk_first.call(null,seq__41589_41613__$1);
var G__41615 = cljs.core.chunk_rest.call(null,seq__41589_41613__$1);
var G__41616 = c__31106__auto___41614;
var G__41617 = cljs.core.count.call(null,c__31106__auto___41614);
var G__41618 = (0);
seq__41589_41601 = G__41615;
chunk__41590_41602 = G__41616;
count__41591_41603 = G__41617;
i__41592_41604 = G__41618;
continue;
} else {
var vec__41596_41619 = cljs.core.first.call(null,seq__41589_41613__$1);
var k_41620 = cljs.core.nth.call(null,vec__41596_41619,(0),null);
var v_41621 = cljs.core.nth.call(null,vec__41596_41619,(1),null);
this$__$1.setRequestHeader(k_41620,v_41621);

var G__41622 = cljs.core.next.call(null,seq__41589_41613__$1);
var G__41623 = null;
var G__41624 = (0);
var G__41625 = (0);
seq__41589_41601 = G__41622;
chunk__41590_41602 = G__41623;
count__41591_41603 = G__41624;
i__41592_41604 = G__41625;
continue;
}
} else {
}
}
break;
}

this$__$1.send((function (){var or__30175__auto__ = body;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return "";
}
})());

return this$__$1;
});

ajax.xml_http_request.xmlhttprequest.prototype.ajax$protocols$AjaxRequest$ = cljs.core.PROTOCOL_SENTINEL;

ajax.xml_http_request.xmlhttprequest.prototype.ajax$protocols$AjaxRequest$_abort$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.abort();
});

ajax.xml_http_request.xmlhttprequest.prototype.ajax$protocols$AjaxResponse$ = cljs.core.PROTOCOL_SENTINEL;

ajax.xml_http_request.xmlhttprequest.prototype.ajax$protocols$AjaxResponse$_body$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.response;
});

ajax.xml_http_request.xmlhttprequest.prototype.ajax$protocols$AjaxResponse$_status$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.status;
});

ajax.xml_http_request.xmlhttprequest.prototype.ajax$protocols$AjaxResponse$_status_text$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.statusText;
});

ajax.xml_http_request.xmlhttprequest.prototype.ajax$protocols$AjaxResponse$_get_all_headers$arity$1 = (function (this$){
var this$__$1 = this;
return ajax.xml_http_request.process_headers.call(null,this$__$1.getAllResponseHeaders());
});

ajax.xml_http_request.xmlhttprequest.prototype.ajax$protocols$AjaxResponse$_get_response_header$arity$2 = (function (this$,header){
var this$__$1 = this;
return this$__$1.getResponseHeader(header);
});

ajax.xml_http_request.xmlhttprequest.prototype.ajax$protocols$AjaxResponse$_was_aborted$arity$1 = (function (this$){
var this$__$1 = this;
return cljs.core._EQ_.call(null,(0),this$__$1.readyState);
});

//# sourceMappingURL=xml_http_request.js.map?rel=1510137278381
