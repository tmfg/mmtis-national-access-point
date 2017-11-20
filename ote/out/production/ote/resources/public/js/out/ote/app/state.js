// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.app.state');
goog.require('cljs.core');
goog.require('reagent.core');
if(typeof ote.app.state.app !== 'undefined'){
} else {
ote.app.state.app = reagent.core.atom.call(null,new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"page","page",849072397),new cljs.core.Keyword(null,"front-page","front-page",-663760939),new cljs.core.Keyword(null,"params","params",710516235),null,new cljs.core.Keyword(null,"query","query",-1288509510),null,new cljs.core.Keyword(null,"user","user",1532431356),cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"ote-service-flags","ote-service-flags",-965917048),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"user-menu-open","user-menu-open",-1786787308),true,new cljs.core.Keyword(null,"show-debug","show-debug",267843982),false], null),new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982),cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("ote.db.transport-service","passenger-transportation","ote.db.transport-service/passenger-transportation",-2018752833),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("ote.db.transport-service","real-time-information","ote.db.transport-service/real-time-information",-1333044881),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("ote.db.transport-service","url","ote.db.transport-service/url",844274149),"www.example.com/url"], null)], null)], null)], null));
}
if(typeof ote.app.state.viewer !== 'undefined'){
} else {
ote.app.state.viewer = reagent.core.atom.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"loading?","loading?",1905707049),true,new cljs.core.Keyword(null,"url","url",276297046),null,new cljs.core.Keyword(null,"geojson","geojson",-719473398),null], null));
}

//# sourceMappingURL=state.js.map?rel=1510137276340
