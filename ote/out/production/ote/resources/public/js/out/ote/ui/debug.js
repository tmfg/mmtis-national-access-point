// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.ui.debug');
goog.require('cljs.core');
goog.require('reagent.core');
ote.ui.debug.can_open_QMARK_ = (function ote$ui$debug$can_open_QMARK_(item){
return cljs.core.some.call(null,(function (p1__51535_SHARP_){
return p1__51535_SHARP_.call(null,item);
}),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.map_QMARK_,cljs.core.coll_QMARK_], null));
});
if(typeof ote.ui.debug.debug_show !== 'undefined'){
} else {
ote.ui.debug.debug_show = (function (){var method_table__31228__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var prefer_table__31229__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var method_cache__31230__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var cached_hierarchy__31231__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var hierarchy__31232__auto__ = cljs.core.get.call(null,cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"hierarchy","hierarchy",-1053470341),cljs.core.get_global_hierarchy.call(null));
return (new cljs.core.MultiFn(cljs.core.symbol.call(null,"ote.ui.debug","debug-show"),((function (method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__,hierarchy__31232__auto__){
return (function (item,path,open_paths,toggle_BANG_){
if(cljs.core.map_QMARK_.call(null,item)){
return new cljs.core.Keyword(null,"map","map",1371690461);
} else {
if(cljs.core.coll_QMARK_.call(null,item)){
return new cljs.core.Keyword(null,"coll","coll",1647737163);
} else {
return new cljs.core.Keyword(null,"pr-str","pr-str",587523624);

}
}
});})(method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__,hierarchy__31232__auto__))
,new cljs.core.Keyword(null,"default","default",-1987822328),hierarchy__31232__auto__,method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__));
})();
}
ote.ui.debug.show_value = (function ote$ui$debug$show_value(value,p,open_paths,toggle_BANG_){
var now = (function (){
return Date.now();
});
var flash = reagent.core.atom.call(null,now.call(null));
return ((function (now,flash){
return (function (value__$1,p__$1,open_paths__$1,toggle_BANG___$1){
var cls = ((((now.call(null) - cljs.core.deref.call(null,flash)) > (1000)))?(function (){
setTimeout(((function (now,flash){
return (function (){
return cljs.core.reset_BANG_.call(null,flash,now.call(null));
});})(now,flash))
,(600));

return "debug-animate";
})()
:null);
if(cljs.core.truth_(ote.ui.debug.can_open_QMARK_.call(null,value__$1))){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span","span",1394872991),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"class","class",-2030961996),cls], null),(cljs.core.truth_(open_paths__$1.call(null,p__$1))?ote.ui.debug.debug_show.call(null,value__$1,p__$1,open_paths__$1,toggle_BANG___$1):(function (){var printed = cljs.core.pr_str.call(null,value__$1);
if((cljs.core.count.call(null,printed) > (100))){
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.subs.call(null,printed,(0),(100)))," ..."].join('');
} else {
return printed;
}
})())], null);
} else {
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span","span",1394872991),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"class","class",-2030961996),cls], null),cljs.core.pr_str.call(null,value__$1)], null);
}
});
;})(now,flash))
});
ote.ui.debug.open_cell = (function ote$ui$debug$open_cell(value,p,open_paths,toggle_BANG_){
if(cljs.core.truth_(ote.ui.debug.can_open_QMARK_.call(null,value))){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (){
return toggle_BANG_.call(null,p);
})], null),(cljs.core.truth_(open_paths.call(null,p))?"\u25BC":"\u25B6")], null);
} else {
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353)," "], null);
}
});
cljs.core._add_method.call(null,ote.ui.debug.debug_show,new cljs.core.Keyword(null,"coll","coll",1647737163),(function (data,path,open_paths,toggle_BANG_){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"table.debug-coll","table.debug-coll",-1371981402),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"thead","thead",-291875296),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"th","th",-545608566),"#"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"th","th",-545608566)," "], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"th","th",-545608566),"Value"], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"tbody","tbody",-80678300),cljs.core.doall.call(null,cljs.core.map_indexed.call(null,(function (i,value){
return cljs.core.with_meta(new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"tr","tr",-1424774646),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),i," "], null),ote.ui.debug.open_cell.call(null,value,cljs.core.conj.call(null,path,i),open_paths,toggle_BANG_),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.debug.show_value,value,cljs.core.conj.call(null,path,i),open_paths,toggle_BANG_], null)], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),i], null));
}),data))], null)], null);
}));
cljs.core._add_method.call(null,ote.ui.debug.debug_show,new cljs.core.Keyword(null,"map","map",1371690461),(function (data,path,open_paths,toggle_BANG_){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"table.debug-map","table.debug-map",-430401246),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"thead","thead",-291875296),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"tr","tr",-1424774646),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"th","th",-545608566),"Key"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"th","th",-545608566)," "], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"th","th",-545608566),"Value"], null)], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"tbody","tbody",-80678300),(function (){var iter__31057__auto__ = (function ote$ui$debug$iter__51536(s__51537){
return (new cljs.core.LazySeq(null,(function (){
var s__51537__$1 = s__51537;
while(true){
var temp__5290__auto__ = cljs.core.seq.call(null,s__51537__$1);
if(temp__5290__auto__){
var s__51537__$2 = temp__5290__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,s__51537__$2)){
var c__31055__auto__ = cljs.core.chunk_first.call(null,s__51537__$2);
var size__31056__auto__ = cljs.core.count.call(null,c__31055__auto__);
var b__51539 = cljs.core.chunk_buffer.call(null,size__31056__auto__);
if((function (){var i__51538 = (0);
while(true){
if((i__51538 < size__31056__auto__)){
var vec__51540 = cljs.core._nth.call(null,c__31055__auto__,i__51538);
var key = cljs.core.nth.call(null,vec__51540,(0),null);
var value = cljs.core.nth.call(null,vec__51540,(1),null);
var p = cljs.core.conj.call(null,path,key);
cljs.core.chunk_append.call(null,b__51539,cljs.core.with_meta(new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"tr","tr",-1424774646),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),cljs.core.pr_str.call(null,key)], null),ote.ui.debug.open_cell.call(null,value,p,open_paths,toggle_BANG_),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.debug.show_value,value,p,open_paths,toggle_BANG_], null)], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),key], null)));

var G__51546 = (i__51538 + (1));
i__51538 = G__51546;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__51539),ote$ui$debug$iter__51536.call(null,cljs.core.chunk_rest.call(null,s__51537__$2)));
} else {
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__51539),null);
}
} else {
var vec__51543 = cljs.core.first.call(null,s__51537__$2);
var key = cljs.core.nth.call(null,vec__51543,(0),null);
var value = cljs.core.nth.call(null,vec__51543,(1),null);
var p = cljs.core.conj.call(null,path,key);
return cljs.core.cons.call(null,cljs.core.with_meta(new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"tr","tr",-1424774646),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),cljs.core.pr_str.call(null,key)], null),ote.ui.debug.open_cell.call(null,value,p,open_paths,toggle_BANG_),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.debug.show_value,value,p,open_paths,toggle_BANG_], null)], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),key], null)),ote$ui$debug$iter__51536.call(null,cljs.core.rest.call(null,s__51537__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__31057__auto__.call(null,cljs.core.sort_by.call(null,cljs.core.first,cljs.core.seq.call(null,data)));
})()], null)], null);
}));
cljs.core._add_method.call(null,ote.ui.debug.debug_show,new cljs.core.Keyword(null,"pr-str","pr-str",587523624),(function (data,p,_,___$1){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span","span",1394872991),cljs.core.pr_str.call(null,data)], null);
}));
ote.ui.debug.debug = (function ote$ui$debug$debug(item){
var open_paths = reagent.core.atom.call(null,cljs.core.PersistentHashSet.EMPTY);
var toggle_BANG_ = ((function (open_paths){
return (function (p1__51547_SHARP_){
return cljs.core.swap_BANG_.call(null,open_paths,((function (open_paths){
return (function (paths){
if(cljs.core.truth_(paths.call(null,p1__51547_SHARP_))){
return cljs.core.disj.call(null,paths,p1__51547_SHARP_);
} else {
return cljs.core.conj.call(null,paths,p1__51547_SHARP_);
}
});})(open_paths))
);
});})(open_paths))
;
return ((function (open_paths,toggle_BANG_){
return (function (item__$1){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.debug","div.debug",-1545042863),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.debug.debug_show,item__$1,cljs.core.PersistentVector.EMPTY,cljs.core.deref.call(null,open_paths),toggle_BANG_], null)], null);
});
;})(open_paths,toggle_BANG_))
});

//# sourceMappingURL=debug.js.map?rel=1510137292350
