// Compiled by ClojureScript 1.9.908 {}
goog.provide('ajax.core');
goog.require('cljs.core');
goog.require('clojure.string');
goog.require('ajax.url');
goog.require('ajax.json');
goog.require('ajax.transit');
goog.require('ajax.ring');
goog.require('ajax.formats');
goog.require('ajax.util');
goog.require('ajax.interceptors');
goog.require('ajax.simple');
goog.require('ajax.easy');
goog.require('ajax.protocols');
goog.require('ajax.xhrio');
goog.require('ajax.xml_http_request');
ajax.core.to_interceptor = ajax.interceptors.to_interceptor;
ajax.core.abort = (function ajax$core$abort(this$){

return ajax.protocols._abort.call(null,this$);
});
ajax.core.json_request_format = ajax.json.json_request_format;
ajax.core.json_response_format = ajax.json.json_response_format;
ajax.core.transit_request_format = ajax.transit.transit_request_format;
ajax.core.transit_response_format = ajax.transit.transit_response_format;
ajax.core.ring_response_format = ajax.ring.ring_response_format;
ajax.core.url_request_format = ajax.url.url_request_format;
ajax.core.text_request_format = ajax.formats.text_request_format;
ajax.core.text_response_format = ajax.formats.text_response_format;
ajax.core.raw_response_format = ajax.formats.raw_response_format;
ajax.core.default_interceptors = ajax.simple.default_interceptors;
ajax.core.ajax_request = ajax.simple.ajax_request;
ajax.core.default_formats = ajax.easy.default_formats;
ajax.core.detect_response_format = ajax.easy.detect_response_format;
/**
 * accepts the URI and an optional map of options, options include:
 *      :handler - the handler function for successful operation
 *                 should accept a single parameter which is the
 *                 deserialized response
 *      :progress-handler - the handler function for progress events.
 *                          this handler is only available when using the goog.net.XhrIo API
 *      :error-handler - the handler function for errors, should accept a
 *                       map with keys :status and :status-text
 *      :format - the format for the request
 *      :response-format - the format for the response
 *      :params - a map of parameters that will be sent with the request
 */
ajax.core.GET = (function ajax$core$GET(var_args){
var args__31459__auto__ = [];
var len__31452__auto___41844 = arguments.length;
var i__31453__auto___41845 = (0);
while(true){
if((i__31453__auto___41845 < len__31452__auto___41844)){
args__31459__auto__.push((arguments[i__31453__auto___41845]));

var G__41846 = (i__31453__auto___41845 + (1));
i__31453__auto___41845 = G__41846;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return ajax.core.GET.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

ajax.core.GET.cljs$core$IFn$_invoke$arity$variadic = (function (uri,opts){
var f__41441__auto__ = cljs.core.first.call(null,opts);
return ajax.easy.easy_ajax_request.call(null,uri,"GET",(((f__41441__auto__ instanceof cljs.core.Keyword))?cljs.core.apply.call(null,cljs.core.hash_map,opts):f__41441__auto__));
});

ajax.core.GET.cljs$lang$maxFixedArity = (1);

ajax.core.GET.cljs$lang$applyTo = (function (seq41842){
var G__41843 = cljs.core.first.call(null,seq41842);
var seq41842__$1 = cljs.core.next.call(null,seq41842);
return ajax.core.GET.cljs$core$IFn$_invoke$arity$variadic(G__41843,seq41842__$1);
});

/**
 * accepts the URI and an optional map of options, options include:
 *      :handler - the handler function for successful operation
 *                 should accept a single parameter which is the
 *                 deserialized response
 *      :progress-handler - the handler function for progress events.
 *                          this handler is only available when using the goog.net.XhrIo API
 *      :error-handler - the handler function for errors, should accept a
 *                       map with keys :status and :status-text
 *      :format - the format for the request
 *      :response-format - the format for the response
 *      :params - a map of parameters that will be sent with the request
 */
ajax.core.HEAD = (function ajax$core$HEAD(var_args){
var args__31459__auto__ = [];
var len__31452__auto___41849 = arguments.length;
var i__31453__auto___41850 = (0);
while(true){
if((i__31453__auto___41850 < len__31452__auto___41849)){
args__31459__auto__.push((arguments[i__31453__auto___41850]));

var G__41851 = (i__31453__auto___41850 + (1));
i__31453__auto___41850 = G__41851;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return ajax.core.HEAD.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

ajax.core.HEAD.cljs$core$IFn$_invoke$arity$variadic = (function (uri,opts){
var f__41441__auto__ = cljs.core.first.call(null,opts);
return ajax.easy.easy_ajax_request.call(null,uri,"HEAD",(((f__41441__auto__ instanceof cljs.core.Keyword))?cljs.core.apply.call(null,cljs.core.hash_map,opts):f__41441__auto__));
});

ajax.core.HEAD.cljs$lang$maxFixedArity = (1);

ajax.core.HEAD.cljs$lang$applyTo = (function (seq41847){
var G__41848 = cljs.core.first.call(null,seq41847);
var seq41847__$1 = cljs.core.next.call(null,seq41847);
return ajax.core.HEAD.cljs$core$IFn$_invoke$arity$variadic(G__41848,seq41847__$1);
});

/**
 * accepts the URI and an optional map of options, options include:
 *      :handler - the handler function for successful operation
 *                 should accept a single parameter which is the
 *                 deserialized response
 *      :progress-handler - the handler function for progress events.
 *                          this handler is only available when using the goog.net.XhrIo API
 *      :error-handler - the handler function for errors, should accept a
 *                       map with keys :status and :status-text
 *      :format - the format for the request
 *      :response-format - the format for the response
 *      :params - a map of parameters that will be sent with the request
 */
ajax.core.POST = (function ajax$core$POST(var_args){
var args__31459__auto__ = [];
var len__31452__auto___41854 = arguments.length;
var i__31453__auto___41855 = (0);
while(true){
if((i__31453__auto___41855 < len__31452__auto___41854)){
args__31459__auto__.push((arguments[i__31453__auto___41855]));

var G__41856 = (i__31453__auto___41855 + (1));
i__31453__auto___41855 = G__41856;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return ajax.core.POST.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

ajax.core.POST.cljs$core$IFn$_invoke$arity$variadic = (function (uri,opts){
var f__41441__auto__ = cljs.core.first.call(null,opts);
return ajax.easy.easy_ajax_request.call(null,uri,"POST",(((f__41441__auto__ instanceof cljs.core.Keyword))?cljs.core.apply.call(null,cljs.core.hash_map,opts):f__41441__auto__));
});

ajax.core.POST.cljs$lang$maxFixedArity = (1);

ajax.core.POST.cljs$lang$applyTo = (function (seq41852){
var G__41853 = cljs.core.first.call(null,seq41852);
var seq41852__$1 = cljs.core.next.call(null,seq41852);
return ajax.core.POST.cljs$core$IFn$_invoke$arity$variadic(G__41853,seq41852__$1);
});

/**
 * accepts the URI and an optional map of options, options include:
 *      :handler - the handler function for successful operation
 *                 should accept a single parameter which is the
 *                 deserialized response
 *      :progress-handler - the handler function for progress events.
 *                          this handler is only available when using the goog.net.XhrIo API
 *      :error-handler - the handler function for errors, should accept a
 *                       map with keys :status and :status-text
 *      :format - the format for the request
 *      :response-format - the format for the response
 *      :params - a map of parameters that will be sent with the request
 */
ajax.core.PUT = (function ajax$core$PUT(var_args){
var args__31459__auto__ = [];
var len__31452__auto___41859 = arguments.length;
var i__31453__auto___41860 = (0);
while(true){
if((i__31453__auto___41860 < len__31452__auto___41859)){
args__31459__auto__.push((arguments[i__31453__auto___41860]));

var G__41861 = (i__31453__auto___41860 + (1));
i__31453__auto___41860 = G__41861;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return ajax.core.PUT.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

ajax.core.PUT.cljs$core$IFn$_invoke$arity$variadic = (function (uri,opts){
var f__41441__auto__ = cljs.core.first.call(null,opts);
return ajax.easy.easy_ajax_request.call(null,uri,"PUT",(((f__41441__auto__ instanceof cljs.core.Keyword))?cljs.core.apply.call(null,cljs.core.hash_map,opts):f__41441__auto__));
});

ajax.core.PUT.cljs$lang$maxFixedArity = (1);

ajax.core.PUT.cljs$lang$applyTo = (function (seq41857){
var G__41858 = cljs.core.first.call(null,seq41857);
var seq41857__$1 = cljs.core.next.call(null,seq41857);
return ajax.core.PUT.cljs$core$IFn$_invoke$arity$variadic(G__41858,seq41857__$1);
});

/**
 * accepts the URI and an optional map of options, options include:
 *      :handler - the handler function for successful operation
 *                 should accept a single parameter which is the
 *                 deserialized response
 *      :progress-handler - the handler function for progress events.
 *                          this handler is only available when using the goog.net.XhrIo API
 *      :error-handler - the handler function for errors, should accept a
 *                       map with keys :status and :status-text
 *      :format - the format for the request
 *      :response-format - the format for the response
 *      :params - a map of parameters that will be sent with the request
 */
ajax.core.DELETE = (function ajax$core$DELETE(var_args){
var args__31459__auto__ = [];
var len__31452__auto___41864 = arguments.length;
var i__31453__auto___41865 = (0);
while(true){
if((i__31453__auto___41865 < len__31452__auto___41864)){
args__31459__auto__.push((arguments[i__31453__auto___41865]));

var G__41866 = (i__31453__auto___41865 + (1));
i__31453__auto___41865 = G__41866;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return ajax.core.DELETE.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

ajax.core.DELETE.cljs$core$IFn$_invoke$arity$variadic = (function (uri,opts){
var f__41441__auto__ = cljs.core.first.call(null,opts);
return ajax.easy.easy_ajax_request.call(null,uri,"DELETE",(((f__41441__auto__ instanceof cljs.core.Keyword))?cljs.core.apply.call(null,cljs.core.hash_map,opts):f__41441__auto__));
});

ajax.core.DELETE.cljs$lang$maxFixedArity = (1);

ajax.core.DELETE.cljs$lang$applyTo = (function (seq41862){
var G__41863 = cljs.core.first.call(null,seq41862);
var seq41862__$1 = cljs.core.next.call(null,seq41862);
return ajax.core.DELETE.cljs$core$IFn$_invoke$arity$variadic(G__41863,seq41862__$1);
});

/**
 * accepts the URI and an optional map of options, options include:
 *      :handler - the handler function for successful operation
 *                 should accept a single parameter which is the
 *                 deserialized response
 *      :progress-handler - the handler function for progress events.
 *                          this handler is only available when using the goog.net.XhrIo API
 *      :error-handler - the handler function for errors, should accept a
 *                       map with keys :status and :status-text
 *      :format - the format for the request
 *      :response-format - the format for the response
 *      :params - a map of parameters that will be sent with the request
 */
ajax.core.OPTIONS = (function ajax$core$OPTIONS(var_args){
var args__31459__auto__ = [];
var len__31452__auto___41869 = arguments.length;
var i__31453__auto___41870 = (0);
while(true){
if((i__31453__auto___41870 < len__31452__auto___41869)){
args__31459__auto__.push((arguments[i__31453__auto___41870]));

var G__41871 = (i__31453__auto___41870 + (1));
i__31453__auto___41870 = G__41871;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return ajax.core.OPTIONS.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

ajax.core.OPTIONS.cljs$core$IFn$_invoke$arity$variadic = (function (uri,opts){
var f__41441__auto__ = cljs.core.first.call(null,opts);
return ajax.easy.easy_ajax_request.call(null,uri,"OPTIONS",(((f__41441__auto__ instanceof cljs.core.Keyword))?cljs.core.apply.call(null,cljs.core.hash_map,opts):f__41441__auto__));
});

ajax.core.OPTIONS.cljs$lang$maxFixedArity = (1);

ajax.core.OPTIONS.cljs$lang$applyTo = (function (seq41867){
var G__41868 = cljs.core.first.call(null,seq41867);
var seq41867__$1 = cljs.core.next.call(null,seq41867);
return ajax.core.OPTIONS.cljs$core$IFn$_invoke$arity$variadic(G__41868,seq41867__$1);
});

/**
 * accepts the URI and an optional map of options, options include:
 *      :handler - the handler function for successful operation
 *                 should accept a single parameter which is the
 *                 deserialized response
 *      :progress-handler - the handler function for progress events.
 *                          this handler is only available when using the goog.net.XhrIo API
 *      :error-handler - the handler function for errors, should accept a
 *                       map with keys :status and :status-text
 *      :format - the format for the request
 *      :response-format - the format for the response
 *      :params - a map of parameters that will be sent with the request
 */
ajax.core.TRACE = (function ajax$core$TRACE(var_args){
var args__31459__auto__ = [];
var len__31452__auto___41874 = arguments.length;
var i__31453__auto___41875 = (0);
while(true){
if((i__31453__auto___41875 < len__31452__auto___41874)){
args__31459__auto__.push((arguments[i__31453__auto___41875]));

var G__41876 = (i__31453__auto___41875 + (1));
i__31453__auto___41875 = G__41876;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return ajax.core.TRACE.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

ajax.core.TRACE.cljs$core$IFn$_invoke$arity$variadic = (function (uri,opts){
var f__41441__auto__ = cljs.core.first.call(null,opts);
return ajax.easy.easy_ajax_request.call(null,uri,"TRACE",(((f__41441__auto__ instanceof cljs.core.Keyword))?cljs.core.apply.call(null,cljs.core.hash_map,opts):f__41441__auto__));
});

ajax.core.TRACE.cljs$lang$maxFixedArity = (1);

ajax.core.TRACE.cljs$lang$applyTo = (function (seq41872){
var G__41873 = cljs.core.first.call(null,seq41872);
var seq41872__$1 = cljs.core.next.call(null,seq41872);
return ajax.core.TRACE.cljs$core$IFn$_invoke$arity$variadic(G__41873,seq41872__$1);
});

/**
 * accepts the URI and an optional map of options, options include:
 *      :handler - the handler function for successful operation
 *                 should accept a single parameter which is the
 *                 deserialized response
 *      :progress-handler - the handler function for progress events.
 *                          this handler is only available when using the goog.net.XhrIo API
 *      :error-handler - the handler function for errors, should accept a
 *                       map with keys :status and :status-text
 *      :format - the format for the request
 *      :response-format - the format for the response
 *      :params - a map of parameters that will be sent with the request
 */
ajax.core.PATCH = (function ajax$core$PATCH(var_args){
var args__31459__auto__ = [];
var len__31452__auto___41879 = arguments.length;
var i__31453__auto___41880 = (0);
while(true){
if((i__31453__auto___41880 < len__31452__auto___41879)){
args__31459__auto__.push((arguments[i__31453__auto___41880]));

var G__41881 = (i__31453__auto___41880 + (1));
i__31453__auto___41880 = G__41881;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return ajax.core.PATCH.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

ajax.core.PATCH.cljs$core$IFn$_invoke$arity$variadic = (function (uri,opts){
var f__41441__auto__ = cljs.core.first.call(null,opts);
return ajax.easy.easy_ajax_request.call(null,uri,"PATCH",(((f__41441__auto__ instanceof cljs.core.Keyword))?cljs.core.apply.call(null,cljs.core.hash_map,opts):f__41441__auto__));
});

ajax.core.PATCH.cljs$lang$maxFixedArity = (1);

ajax.core.PATCH.cljs$lang$applyTo = (function (seq41877){
var G__41878 = cljs.core.first.call(null,seq41877);
var seq41877__$1 = cljs.core.next.call(null,seq41877);
return ajax.core.PATCH.cljs$core$IFn$_invoke$arity$variadic(G__41878,seq41877__$1);
});

/**
 * accepts the URI and an optional map of options, options include:
 *      :handler - the handler function for successful operation
 *                 should accept a single parameter which is the
 *                 deserialized response
 *      :progress-handler - the handler function for progress events.
 *                          this handler is only available when using the goog.net.XhrIo API
 *      :error-handler - the handler function for errors, should accept a
 *                       map with keys :status and :status-text
 *      :format - the format for the request
 *      :response-format - the format for the response
 *      :params - a map of parameters that will be sent with the request
 */
ajax.core.PURGE = (function ajax$core$PURGE(var_args){
var args__31459__auto__ = [];
var len__31452__auto___41884 = arguments.length;
var i__31453__auto___41885 = (0);
while(true){
if((i__31453__auto___41885 < len__31452__auto___41884)){
args__31459__auto__.push((arguments[i__31453__auto___41885]));

var G__41886 = (i__31453__auto___41885 + (1));
i__31453__auto___41885 = G__41886;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return ajax.core.PURGE.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

ajax.core.PURGE.cljs$core$IFn$_invoke$arity$variadic = (function (uri,opts){
var f__41441__auto__ = cljs.core.first.call(null,opts);
return ajax.easy.easy_ajax_request.call(null,uri,"PURGE",(((f__41441__auto__ instanceof cljs.core.Keyword))?cljs.core.apply.call(null,cljs.core.hash_map,opts):f__41441__auto__));
});

ajax.core.PURGE.cljs$lang$maxFixedArity = (1);

ajax.core.PURGE.cljs$lang$applyTo = (function (seq41882){
var G__41883 = cljs.core.first.call(null,seq41882);
var seq41882__$1 = cljs.core.next.call(null,seq41882);
return ajax.core.PURGE.cljs$core$IFn$_invoke$arity$variadic(G__41883,seq41882__$1);
});


//# sourceMappingURL=core.js.map?rel=1510137278923
