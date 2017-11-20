// Compiled by ClojureScript 1.9.908 {}
goog.provide('figwheel.connect.build_dev');
goog.require('cljs.core');
goog.require('ote.main');
goog.require('figwheel.client');
goog.require('figwheel.client.utils');
figwheel.client.start.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"on-jsload","on-jsload",-395756602),(function() { 
var G__43624__delegate = function (x){
if(cljs.core.truth_(ote.main.reload_hook)){
return cljs.core.apply.call(null,ote.main.reload_hook,x);
} else {
return figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"debug","debug",-1608172596),"Figwheel: :on-jsload hook 'ote.main/reload-hook' is missing");
}
};
var G__43624 = function (var_args){
var x = null;
if (arguments.length > 0) {
var G__43625__i = 0, G__43625__a = new Array(arguments.length -  0);
while (G__43625__i < G__43625__a.length) {G__43625__a[G__43625__i] = arguments[G__43625__i + 0]; ++G__43625__i;}
  x = new cljs.core.IndexedSeq(G__43625__a,0,null);
} 
return G__43624__delegate.call(this,x);};
G__43624.cljs$lang$maxFixedArity = 0;
G__43624.cljs$lang$applyTo = (function (arglist__43626){
var x = cljs.core.seq(arglist__43626);
return G__43624__delegate(x);
});
G__43624.cljs$core$IFn$_invoke$arity$variadic = G__43624__delegate;
return G__43624;
})()
,new cljs.core.Keyword(null,"build-id","build-id",1642831089),"dev",new cljs.core.Keyword(null,"websocket-url","websocket-url",-490444938),"ws://localhost:3449/figwheel-ws"], null));

//# sourceMappingURL=build_dev.js.map?rel=1510647245562
