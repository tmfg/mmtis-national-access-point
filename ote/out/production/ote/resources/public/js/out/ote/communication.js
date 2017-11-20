// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.communication');
goog.require('cljs.core');
goog.require('ajax.core');
goog.require('cognitect.transit');
goog.require('ote.transit');
if(typeof ote.communication.base_url !== 'undefined'){
} else {
ote.communication.base_url = cljs.core.atom.call(null,"");
}
ote.communication.set_base_url_BANG_ = (function ote$communication$set_base_url_BANG_(url){
return cljs.core.reset_BANG_.call(null,ote.communication.base_url,url);
});
ote.communication.transit_request_format = (function ote$communication$transit_request_format(){
return ajax.core.transit_request_format.call(null,ote.transit.write_options);
});
ote.communication.transit_response_format = (function ote$communication$transit_response_format(){
return ajax.core.transit_response_format.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"reader","reader",169660853),cognitect.transit.reader.call(null,new cljs.core.Keyword(null,"json","json",1279968570),ote.transit.read_options),new cljs.core.Keyword(null,"raw","raw",1604651272),true], null));
});
ote.communication.request_url = (function ote$communication$request_url(url){
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.deref.call(null,ote.communication.base_url)),cljs.core.str.cljs$core$IFn$_invoke$arity$1(url)].join('');
});
/**
 * Make a GET request to the given URL.
 *   URL parameters can be given with the `:params` key.
 *   Callbacks for successfull and failure are provided with `:on-success` and `:on-failure`
 *   keys respectively
 */
ote.communication.get_BANG_ = (function ote$communication$get_BANG_(url,p__41952){
var map__41953 = p__41952;
var map__41953__$1 = ((((!((map__41953 == null)))?((((map__41953.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__41953.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__41953):map__41953);
var params = cljs.core.get.call(null,map__41953__$1,new cljs.core.Keyword(null,"params","params",710516235));
var on_success = cljs.core.get.call(null,map__41953__$1,new cljs.core.Keyword(null,"on-success","on-success",1786904109));
var on_failure = cljs.core.get.call(null,map__41953__$1,new cljs.core.Keyword(null,"on-failure","on-failure",842888245));
var response_format = cljs.core.get.call(null,map__41953__$1,new cljs.core.Keyword(null,"response-format","response-format",1664465322));
return ajax.core.GET.call(null,ote.communication.request_url.call(null,url),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"params","params",710516235),params,new cljs.core.Keyword(null,"handler","handler",-195596612),on_success,new cljs.core.Keyword(null,"error-handler","error-handler",-484945776),on_failure,new cljs.core.Keyword(null,"response-format","response-format",1664465322),(function (){var or__30175__auto__ = response_format;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return ote.communication.transit_response_format.call(null);
}
})()], null));
});
/**
 * Make a POST request to the given URL.
 *   URL parameters can be given with the `:body` key.
 *   Callbacks for successfull and failure are provided with `:on-success` and `:on-failure`
 *   keys respectively
 */
ote.communication.post_BANG_ = (function ote$communication$post_BANG_(url,body,p__41955){
var map__41956 = p__41955;
var map__41956__$1 = ((((!((map__41956 == null)))?((((map__41956.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__41956.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__41956):map__41956);
var on_success = cljs.core.get.call(null,map__41956__$1,new cljs.core.Keyword(null,"on-success","on-success",1786904109));
var on_failure = cljs.core.get.call(null,map__41956__$1,new cljs.core.Keyword(null,"on-failure","on-failure",842888245));
var response_format = cljs.core.get.call(null,map__41956__$1,new cljs.core.Keyword(null,"response-format","response-format",1664465322));
return ajax.core.POST.call(null,ote.communication.request_url.call(null,url),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"params","params",710516235),body,new cljs.core.Keyword(null,"handler","handler",-195596612),on_success,new cljs.core.Keyword(null,"error-handler","error-handler",-484945776),on_failure,new cljs.core.Keyword(null,"format","format",-1306924766),ote.communication.transit_request_format.call(null),new cljs.core.Keyword(null,"response-format","response-format",1664465322),(function (){var or__30175__auto__ = response_format;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return ote.communication.transit_response_format.call(null);
}
})()], null));
});

//# sourceMappingURL=communication.js.map?rel=1510137279168
