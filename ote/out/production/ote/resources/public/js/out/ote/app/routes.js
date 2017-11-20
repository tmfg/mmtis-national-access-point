// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.app.routes');
goog.require('cljs.core');
goog.require('bide.core');
goog.require('ote.app.state');
ote.app.routes.ote_router = bide.core.router.call(null,new cljs.core.PersistentVector(null, 10, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["/",new cljs.core.Keyword(null,"front-page","front-page",-663760939)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["/own-services",new cljs.core.Keyword(null,"own-services","own-services",-1593467283)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["/transport-operator",new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["/passenger-transportation",new cljs.core.Keyword(null,"passenger-transportation","passenger-transportation",-368634870)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["/terminal",new cljs.core.Keyword(null,"terminal","terminal",-927870592)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["/rentals",new cljs.core.Keyword(null,"rentals","rentals",37930980)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["/brokerage",new cljs.core.Keyword(null,"brokerage","brokerage",1771448945)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["/parking",new cljs.core.Keyword(null,"parking","parking",-952236974)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["/new-service",new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["/edit-service/:id",new cljs.core.Keyword(null,"edit-service","edit-service",1657939624)], null)], null));
ote.app.routes.on_navigate = (function ote$app$routes$on_navigate(name,params,query){
return cljs.core.swap_BANG_.call(null,ote.app.state.app,cljs.core.merge,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"page","page",849072397),name,new cljs.core.Keyword(null,"params","params",710516235),params,new cljs.core.Keyword(null,"query","query",-1288509510),query], null));
});
ote.app.routes.start_BANG_ = (function ote$app$routes$start_BANG_(){
return bide.core.start_BANG_.call(null,ote.app.routes.ote_router,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"default","default",-1987822328),new cljs.core.Keyword(null,"front-page","front-page",-663760939),new cljs.core.Keyword(null,"on-navigate","on-navigate",-297227908),ote.app.routes.on_navigate], null));
});
/**
 * Navigate to given page with optional route and query parameters.
 *   The navigation is done by setting a timeout and can be called from
 *   tuck process-event.
 */
ote.app.routes.navigate_BANG_ = (function ote$app$routes$navigate_BANG_(var_args){
var G__40622 = arguments.length;
switch (G__40622) {
case 1:
return ote.app.routes.navigate_BANG_.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return ote.app.routes.navigate_BANG_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return ote.app.routes.navigate_BANG_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

ote.app.routes.navigate_BANG_.cljs$core$IFn$_invoke$arity$1 = (function (page){
return ote.app.routes.navigate_BANG_.call(null,page,null,null);
});

ote.app.routes.navigate_BANG_.cljs$core$IFn$_invoke$arity$2 = (function (page,params){
return ote.app.routes.navigate_BANG_.call(null,page,params,null);
});

ote.app.routes.navigate_BANG_.cljs$core$IFn$_invoke$arity$3 = (function (page,params,query){
console.log("NAVIGATE: ",cljs.core.pr_str.call(null,page));

return window.setTimeout((function (){
return bide.core.navigate_BANG_.call(null,ote.app.routes.ote_router,page,params,query);
}),(0));
});

ote.app.routes.navigate_BANG_.cljs$lang$maxFixedArity = 3;


//# sourceMappingURL=routes.js.map?rel=1510137276459
