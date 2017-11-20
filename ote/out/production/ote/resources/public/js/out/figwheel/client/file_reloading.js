// Compiled by ClojureScript 1.9.908 {}
goog.provide('figwheel.client.file_reloading');
goog.require('cljs.core');
goog.require('figwheel.client.utils');
goog.require('goog.Uri');
goog.require('goog.string');
goog.require('goog.object');
goog.require('goog.net.jsloader');
goog.require('goog.html.legacyconversions');
goog.require('clojure.string');
goog.require('clojure.set');
goog.require('cljs.core.async');
goog.require('goog.async.Deferred');
if(typeof figwheel.client.file_reloading.figwheel_meta_pragmas !== 'undefined'){
} else {
figwheel.client.file_reloading.figwheel_meta_pragmas = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
}
figwheel.client.file_reloading.on_jsload_custom_event = (function figwheel$client$file_reloading$on_jsload_custom_event(url){
return figwheel.client.utils.dispatch_custom_event.call(null,"figwheel.js-reload",url);
});
figwheel.client.file_reloading.before_jsload_custom_event = (function figwheel$client$file_reloading$before_jsload_custom_event(files){
return figwheel.client.utils.dispatch_custom_event.call(null,"figwheel.before-js-reload",files);
});
figwheel.client.file_reloading.on_cssload_custom_event = (function figwheel$client$file_reloading$on_cssload_custom_event(files){
return figwheel.client.utils.dispatch_custom_event.call(null,"figwheel.css-reload",files);
});
figwheel.client.file_reloading.namespace_file_map_QMARK_ = (function figwheel$client$file_reloading$namespace_file_map_QMARK_(m){
var or__30175__auto__ = (cljs.core.map_QMARK_.call(null,m)) && (typeof new cljs.core.Keyword(null,"namespace","namespace",-377510372).cljs$core$IFn$_invoke$arity$1(m) === 'string') && (((new cljs.core.Keyword(null,"file","file",-1269645878).cljs$core$IFn$_invoke$arity$1(m) == null)) || (typeof new cljs.core.Keyword(null,"file","file",-1269645878).cljs$core$IFn$_invoke$arity$1(m) === 'string')) && (cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(m),new cljs.core.Keyword(null,"namespace","namespace",-377510372)));
if(or__30175__auto__){
return or__30175__auto__;
} else {
cljs.core.println.call(null,"Error not namespace-file-map",cljs.core.pr_str.call(null,m));

return false;
}
});
figwheel.client.file_reloading.add_cache_buster = (function figwheel$client$file_reloading$add_cache_buster(url){

return goog.Uri.parse(url).makeUnique();
});
figwheel.client.file_reloading.name__GT_path = (function figwheel$client$file_reloading$name__GT_path(ns){

return goog.object.get(goog.dependencies_.nameToPath,ns);
});
figwheel.client.file_reloading.provided_QMARK_ = (function figwheel$client$file_reloading$provided_QMARK_(ns){
return goog.object.get(goog.dependencies_.written,figwheel.client.file_reloading.name__GT_path.call(null,ns));
});
figwheel.client.file_reloading.immutable_ns_QMARK_ = (function figwheel$client$file_reloading$immutable_ns_QMARK_(name){
var or__30175__auto__ = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, ["cljs.nodejs",null,"goog",null,"cljs.core",null], null), null).call(null,name);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
var or__30175__auto____$1 = goog.string.startsWith("clojure.",name);
if(cljs.core.truth_(or__30175__auto____$1)){
return or__30175__auto____$1;
} else {
return goog.string.startsWith("goog.",name);
}
}
});
figwheel.client.file_reloading.get_requires = (function figwheel$client$file_reloading$get_requires(ns){
return cljs.core.set.call(null,cljs.core.filter.call(null,(function (p1__56023_SHARP_){
return cljs.core.not.call(null,figwheel.client.file_reloading.immutable_ns_QMARK_.call(null,p1__56023_SHARP_));
}),goog.object.getKeys(goog.object.get(goog.dependencies_.requires,figwheel.client.file_reloading.name__GT_path.call(null,ns)))));
});
if(typeof figwheel.client.file_reloading.dependency_data !== 'undefined'){
} else {
figwheel.client.file_reloading.dependency_data = cljs.core.atom.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"pathToName","pathToName",-1236616181),cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"dependents","dependents",136812837),cljs.core.PersistentArrayMap.EMPTY], null));
}
figwheel.client.file_reloading.path_to_name_BANG_ = (function figwheel$client$file_reloading$path_to_name_BANG_(path,name){
return cljs.core.swap_BANG_.call(null,figwheel.client.file_reloading.dependency_data,cljs.core.update_in,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"pathToName","pathToName",-1236616181),path], null),cljs.core.fnil.call(null,clojure.set.union,cljs.core.PersistentHashSet.EMPTY),cljs.core.PersistentHashSet.createAsIfByAssoc([name]));
});
/**
 * Setup a path to name dependencies map.
 * That goes from path -> #{ ns-names }
 */
figwheel.client.file_reloading.setup_path__GT_name_BANG_ = (function figwheel$client$file_reloading$setup_path__GT_name_BANG_(){
var nameToPath = goog.object.filter(goog.dependencies_.nameToPath,(function (v,k,o){
return goog.string.startsWith(v,"../");
}));
return goog.object.forEach(nameToPath,((function (nameToPath){
return (function (v,k,o){
return figwheel.client.file_reloading.path_to_name_BANG_.call(null,v,k);
});})(nameToPath))
);
});
/**
 * returns a set of namespaces defined by a path
 */
figwheel.client.file_reloading.path__GT_name = (function figwheel$client$file_reloading$path__GT_name(path){
return cljs.core.get_in.call(null,cljs.core.deref.call(null,figwheel.client.file_reloading.dependency_data),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"pathToName","pathToName",-1236616181),path], null));
});
figwheel.client.file_reloading.name_to_parent_BANG_ = (function figwheel$client$file_reloading$name_to_parent_BANG_(ns,parent_ns){
return cljs.core.swap_BANG_.call(null,figwheel.client.file_reloading.dependency_data,cljs.core.update_in,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"dependents","dependents",136812837),ns], null),cljs.core.fnil.call(null,clojure.set.union,cljs.core.PersistentHashSet.EMPTY),cljs.core.PersistentHashSet.createAsIfByAssoc([parent_ns]));
});
/**
 * This reverses the goog.dependencies_.requires for looking up ns-dependents.
 */
figwheel.client.file_reloading.setup_ns__GT_dependents_BANG_ = (function figwheel$client$file_reloading$setup_ns__GT_dependents_BANG_(){
var requires = goog.object.filter(goog.dependencies_.requires,(function (v,k,o){
return goog.string.startsWith(k,"../");
}));
return goog.object.forEach(requires,((function (requires){
return (function (v,k,_){
return goog.object.forEach(v,((function (requires){
return (function (v_SINGLEQUOTE_,k_SINGLEQUOTE_,___$1){
var seq__56024 = cljs.core.seq.call(null,figwheel.client.file_reloading.path__GT_name.call(null,k));
var chunk__56025 = null;
var count__56026 = (0);
var i__56027 = (0);
while(true){
if((i__56027 < count__56026)){
var n = cljs.core._nth.call(null,chunk__56025,i__56027);
figwheel.client.file_reloading.name_to_parent_BANG_.call(null,k_SINGLEQUOTE_,n);

var G__56028 = seq__56024;
var G__56029 = chunk__56025;
var G__56030 = count__56026;
var G__56031 = (i__56027 + (1));
seq__56024 = G__56028;
chunk__56025 = G__56029;
count__56026 = G__56030;
i__56027 = G__56031;
continue;
} else {
var temp__5290__auto__ = cljs.core.seq.call(null,seq__56024);
if(temp__5290__auto__){
var seq__56024__$1 = temp__5290__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__56024__$1)){
var c__31106__auto__ = cljs.core.chunk_first.call(null,seq__56024__$1);
var G__56032 = cljs.core.chunk_rest.call(null,seq__56024__$1);
var G__56033 = c__31106__auto__;
var G__56034 = cljs.core.count.call(null,c__31106__auto__);
var G__56035 = (0);
seq__56024 = G__56032;
chunk__56025 = G__56033;
count__56026 = G__56034;
i__56027 = G__56035;
continue;
} else {
var n = cljs.core.first.call(null,seq__56024__$1);
figwheel.client.file_reloading.name_to_parent_BANG_.call(null,k_SINGLEQUOTE_,n);

var G__56036 = cljs.core.next.call(null,seq__56024__$1);
var G__56037 = null;
var G__56038 = (0);
var G__56039 = (0);
seq__56024 = G__56036;
chunk__56025 = G__56037;
count__56026 = G__56038;
i__56027 = G__56039;
continue;
}
} else {
return null;
}
}
break;
}
});})(requires))
);
});})(requires))
);
});
figwheel.client.file_reloading.ns__GT_dependents = (function figwheel$client$file_reloading$ns__GT_dependents(ns){
return cljs.core.get_in.call(null,cljs.core.deref.call(null,figwheel.client.file_reloading.dependency_data),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"dependents","dependents",136812837),ns], null));
});
figwheel.client.file_reloading.build_topo_sort = (function figwheel$client$file_reloading$build_topo_sort(get_deps){
var get_deps__$1 = cljs.core.memoize.call(null,get_deps);
var topo_sort_helper_STAR_ = ((function (get_deps__$1){
return (function figwheel$client$file_reloading$build_topo_sort_$_topo_sort_helper_STAR_(x,depth,state){
var deps = get_deps__$1.call(null,x);
if(cljs.core.empty_QMARK_.call(null,deps)){
return null;
} else {
return topo_sort_STAR_.call(null,deps,depth,state);
}
});})(get_deps__$1))
;
var topo_sort_STAR_ = ((function (get_deps__$1){
return (function() {
var figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR_ = null;
var figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR___1 = (function (deps){
return figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR_.call(null,deps,(0),cljs.core.atom.call(null,cljs.core.sorted_map.call(null)));
});
var figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR___3 = (function (deps,depth,state){
cljs.core.swap_BANG_.call(null,state,cljs.core.update_in,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [depth], null),cljs.core.fnil.call(null,cljs.core.into,cljs.core.PersistentHashSet.EMPTY),deps);

var seq__56049_56057 = cljs.core.seq.call(null,deps);
var chunk__56050_56058 = null;
var count__56051_56059 = (0);
var i__56052_56060 = (0);
while(true){
if((i__56052_56060 < count__56051_56059)){
var dep_56061 = cljs.core._nth.call(null,chunk__56050_56058,i__56052_56060);
topo_sort_helper_STAR_.call(null,dep_56061,(depth + (1)),state);

var G__56062 = seq__56049_56057;
var G__56063 = chunk__56050_56058;
var G__56064 = count__56051_56059;
var G__56065 = (i__56052_56060 + (1));
seq__56049_56057 = G__56062;
chunk__56050_56058 = G__56063;
count__56051_56059 = G__56064;
i__56052_56060 = G__56065;
continue;
} else {
var temp__5290__auto___56066 = cljs.core.seq.call(null,seq__56049_56057);
if(temp__5290__auto___56066){
var seq__56049_56067__$1 = temp__5290__auto___56066;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__56049_56067__$1)){
var c__31106__auto___56068 = cljs.core.chunk_first.call(null,seq__56049_56067__$1);
var G__56069 = cljs.core.chunk_rest.call(null,seq__56049_56067__$1);
var G__56070 = c__31106__auto___56068;
var G__56071 = cljs.core.count.call(null,c__31106__auto___56068);
var G__56072 = (0);
seq__56049_56057 = G__56069;
chunk__56050_56058 = G__56070;
count__56051_56059 = G__56071;
i__56052_56060 = G__56072;
continue;
} else {
var dep_56073 = cljs.core.first.call(null,seq__56049_56067__$1);
topo_sort_helper_STAR_.call(null,dep_56073,(depth + (1)),state);

var G__56074 = cljs.core.next.call(null,seq__56049_56067__$1);
var G__56075 = null;
var G__56076 = (0);
var G__56077 = (0);
seq__56049_56057 = G__56074;
chunk__56050_56058 = G__56075;
count__56051_56059 = G__56076;
i__56052_56060 = G__56077;
continue;
}
} else {
}
}
break;
}

if(cljs.core._EQ_.call(null,depth,(0))){
return elim_dups_STAR_.call(null,cljs.core.reverse.call(null,cljs.core.vals.call(null,cljs.core.deref.call(null,state))));
} else {
return null;
}
});
figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR_ = function(deps,depth,state){
switch(arguments.length){
case 1:
return figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR___1.call(this,deps);
case 3:
return figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR___3.call(this,deps,depth,state);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR_.cljs$core$IFn$_invoke$arity$1 = figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR___1;
figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR_.cljs$core$IFn$_invoke$arity$3 = figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR___3;
return figwheel$client$file_reloading$build_topo_sort_$_topo_sort_STAR_;
})()
;})(get_deps__$1))
;
var elim_dups_STAR_ = ((function (get_deps__$1){
return (function figwheel$client$file_reloading$build_topo_sort_$_elim_dups_STAR_(p__56053){
var vec__56054 = p__56053;
var seq__56055 = cljs.core.seq.call(null,vec__56054);
var first__56056 = cljs.core.first.call(null,seq__56055);
var seq__56055__$1 = cljs.core.next.call(null,seq__56055);
var x = first__56056;
var xs = seq__56055__$1;
if((x == null)){
return cljs.core.List.EMPTY;
} else {
return cljs.core.cons.call(null,x,figwheel$client$file_reloading$build_topo_sort_$_elim_dups_STAR_.call(null,cljs.core.map.call(null,((function (vec__56054,seq__56055,first__56056,seq__56055__$1,x,xs,get_deps__$1){
return (function (p1__56040_SHARP_){
return clojure.set.difference.call(null,p1__56040_SHARP_,x);
});})(vec__56054,seq__56055,first__56056,seq__56055__$1,x,xs,get_deps__$1))
,xs)));
}
});})(get_deps__$1))
;
return topo_sort_STAR_;
});
figwheel.client.file_reloading.get_all_dependencies = (function figwheel$client$file_reloading$get_all_dependencies(ns){
var topo_sort_SINGLEQUOTE_ = figwheel.client.file_reloading.build_topo_sort.call(null,figwheel.client.file_reloading.get_requires);
return cljs.core.apply.call(null,cljs.core.concat,topo_sort_SINGLEQUOTE_.call(null,cljs.core.set.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [ns], null))));
});
figwheel.client.file_reloading.get_all_dependents = (function figwheel$client$file_reloading$get_all_dependents(nss){
var topo_sort_SINGLEQUOTE_ = figwheel.client.file_reloading.build_topo_sort.call(null,figwheel.client.file_reloading.ns__GT_dependents);
return cljs.core.filter.call(null,cljs.core.comp.call(null,cljs.core.not,figwheel.client.file_reloading.immutable_ns_QMARK_),cljs.core.reverse.call(null,cljs.core.apply.call(null,cljs.core.concat,topo_sort_SINGLEQUOTE_.call(null,cljs.core.set.call(null,nss)))));
});
figwheel.client.file_reloading.unprovide_BANG_ = (function figwheel$client$file_reloading$unprovide_BANG_(ns){
var path = figwheel.client.file_reloading.name__GT_path.call(null,ns);
goog.object.remove(goog.dependencies_.visited,path);

goog.object.remove(goog.dependencies_.written,path);

return goog.object.remove(goog.dependencies_.written,[cljs.core.str.cljs$core$IFn$_invoke$arity$1(goog.basePath),cljs.core.str.cljs$core$IFn$_invoke$arity$1(path)].join(''));
});
figwheel.client.file_reloading.resolve_ns = (function figwheel$client$file_reloading$resolve_ns(ns){
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(goog.basePath),cljs.core.str.cljs$core$IFn$_invoke$arity$1(figwheel.client.file_reloading.name__GT_path.call(null,ns))].join('');
});
figwheel.client.file_reloading.addDependency = (function figwheel$client$file_reloading$addDependency(path,provides,requires){
var seq__56078 = cljs.core.seq.call(null,provides);
var chunk__56079 = null;
var count__56080 = (0);
var i__56081 = (0);
while(true){
if((i__56081 < count__56080)){
var prov = cljs.core._nth.call(null,chunk__56079,i__56081);
figwheel.client.file_reloading.path_to_name_BANG_.call(null,path,prov);

var seq__56082_56090 = cljs.core.seq.call(null,requires);
var chunk__56083_56091 = null;
var count__56084_56092 = (0);
var i__56085_56093 = (0);
while(true){
if((i__56085_56093 < count__56084_56092)){
var req_56094 = cljs.core._nth.call(null,chunk__56083_56091,i__56085_56093);
figwheel.client.file_reloading.name_to_parent_BANG_.call(null,req_56094,prov);

var G__56095 = seq__56082_56090;
var G__56096 = chunk__56083_56091;
var G__56097 = count__56084_56092;
var G__56098 = (i__56085_56093 + (1));
seq__56082_56090 = G__56095;
chunk__56083_56091 = G__56096;
count__56084_56092 = G__56097;
i__56085_56093 = G__56098;
continue;
} else {
var temp__5290__auto___56099 = cljs.core.seq.call(null,seq__56082_56090);
if(temp__5290__auto___56099){
var seq__56082_56100__$1 = temp__5290__auto___56099;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__56082_56100__$1)){
var c__31106__auto___56101 = cljs.core.chunk_first.call(null,seq__56082_56100__$1);
var G__56102 = cljs.core.chunk_rest.call(null,seq__56082_56100__$1);
var G__56103 = c__31106__auto___56101;
var G__56104 = cljs.core.count.call(null,c__31106__auto___56101);
var G__56105 = (0);
seq__56082_56090 = G__56102;
chunk__56083_56091 = G__56103;
count__56084_56092 = G__56104;
i__56085_56093 = G__56105;
continue;
} else {
var req_56106 = cljs.core.first.call(null,seq__56082_56100__$1);
figwheel.client.file_reloading.name_to_parent_BANG_.call(null,req_56106,prov);

var G__56107 = cljs.core.next.call(null,seq__56082_56100__$1);
var G__56108 = null;
var G__56109 = (0);
var G__56110 = (0);
seq__56082_56090 = G__56107;
chunk__56083_56091 = G__56108;
count__56084_56092 = G__56109;
i__56085_56093 = G__56110;
continue;
}
} else {
}
}
break;
}

var G__56111 = seq__56078;
var G__56112 = chunk__56079;
var G__56113 = count__56080;
var G__56114 = (i__56081 + (1));
seq__56078 = G__56111;
chunk__56079 = G__56112;
count__56080 = G__56113;
i__56081 = G__56114;
continue;
} else {
var temp__5290__auto__ = cljs.core.seq.call(null,seq__56078);
if(temp__5290__auto__){
var seq__56078__$1 = temp__5290__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__56078__$1)){
var c__31106__auto__ = cljs.core.chunk_first.call(null,seq__56078__$1);
var G__56115 = cljs.core.chunk_rest.call(null,seq__56078__$1);
var G__56116 = c__31106__auto__;
var G__56117 = cljs.core.count.call(null,c__31106__auto__);
var G__56118 = (0);
seq__56078 = G__56115;
chunk__56079 = G__56116;
count__56080 = G__56117;
i__56081 = G__56118;
continue;
} else {
var prov = cljs.core.first.call(null,seq__56078__$1);
figwheel.client.file_reloading.path_to_name_BANG_.call(null,path,prov);

var seq__56086_56119 = cljs.core.seq.call(null,requires);
var chunk__56087_56120 = null;
var count__56088_56121 = (0);
var i__56089_56122 = (0);
while(true){
if((i__56089_56122 < count__56088_56121)){
var req_56123 = cljs.core._nth.call(null,chunk__56087_56120,i__56089_56122);
figwheel.client.file_reloading.name_to_parent_BANG_.call(null,req_56123,prov);

var G__56124 = seq__56086_56119;
var G__56125 = chunk__56087_56120;
var G__56126 = count__56088_56121;
var G__56127 = (i__56089_56122 + (1));
seq__56086_56119 = G__56124;
chunk__56087_56120 = G__56125;
count__56088_56121 = G__56126;
i__56089_56122 = G__56127;
continue;
} else {
var temp__5290__auto___56128__$1 = cljs.core.seq.call(null,seq__56086_56119);
if(temp__5290__auto___56128__$1){
var seq__56086_56129__$1 = temp__5290__auto___56128__$1;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__56086_56129__$1)){
var c__31106__auto___56130 = cljs.core.chunk_first.call(null,seq__56086_56129__$1);
var G__56131 = cljs.core.chunk_rest.call(null,seq__56086_56129__$1);
var G__56132 = c__31106__auto___56130;
var G__56133 = cljs.core.count.call(null,c__31106__auto___56130);
var G__56134 = (0);
seq__56086_56119 = G__56131;
chunk__56087_56120 = G__56132;
count__56088_56121 = G__56133;
i__56089_56122 = G__56134;
continue;
} else {
var req_56135 = cljs.core.first.call(null,seq__56086_56129__$1);
figwheel.client.file_reloading.name_to_parent_BANG_.call(null,req_56135,prov);

var G__56136 = cljs.core.next.call(null,seq__56086_56129__$1);
var G__56137 = null;
var G__56138 = (0);
var G__56139 = (0);
seq__56086_56119 = G__56136;
chunk__56087_56120 = G__56137;
count__56088_56121 = G__56138;
i__56089_56122 = G__56139;
continue;
}
} else {
}
}
break;
}

var G__56140 = cljs.core.next.call(null,seq__56078__$1);
var G__56141 = null;
var G__56142 = (0);
var G__56143 = (0);
seq__56078 = G__56140;
chunk__56079 = G__56141;
count__56080 = G__56142;
i__56081 = G__56143;
continue;
}
} else {
return null;
}
}
break;
}
});
figwheel.client.file_reloading.figwheel_require = (function figwheel$client$file_reloading$figwheel_require(src,reload){
goog.require = figwheel.client.file_reloading.figwheel_require;

if(cljs.core._EQ_.call(null,reload,"reload-all")){
var seq__56144_56148 = cljs.core.seq.call(null,figwheel.client.file_reloading.get_all_dependencies.call(null,src));
var chunk__56145_56149 = null;
var count__56146_56150 = (0);
var i__56147_56151 = (0);
while(true){
if((i__56147_56151 < count__56146_56150)){
var ns_56152 = cljs.core._nth.call(null,chunk__56145_56149,i__56147_56151);
figwheel.client.file_reloading.unprovide_BANG_.call(null,ns_56152);

var G__56153 = seq__56144_56148;
var G__56154 = chunk__56145_56149;
var G__56155 = count__56146_56150;
var G__56156 = (i__56147_56151 + (1));
seq__56144_56148 = G__56153;
chunk__56145_56149 = G__56154;
count__56146_56150 = G__56155;
i__56147_56151 = G__56156;
continue;
} else {
var temp__5290__auto___56157 = cljs.core.seq.call(null,seq__56144_56148);
if(temp__5290__auto___56157){
var seq__56144_56158__$1 = temp__5290__auto___56157;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__56144_56158__$1)){
var c__31106__auto___56159 = cljs.core.chunk_first.call(null,seq__56144_56158__$1);
var G__56160 = cljs.core.chunk_rest.call(null,seq__56144_56158__$1);
var G__56161 = c__31106__auto___56159;
var G__56162 = cljs.core.count.call(null,c__31106__auto___56159);
var G__56163 = (0);
seq__56144_56148 = G__56160;
chunk__56145_56149 = G__56161;
count__56146_56150 = G__56162;
i__56147_56151 = G__56163;
continue;
} else {
var ns_56164 = cljs.core.first.call(null,seq__56144_56158__$1);
figwheel.client.file_reloading.unprovide_BANG_.call(null,ns_56164);

var G__56165 = cljs.core.next.call(null,seq__56144_56158__$1);
var G__56166 = null;
var G__56167 = (0);
var G__56168 = (0);
seq__56144_56148 = G__56165;
chunk__56145_56149 = G__56166;
count__56146_56150 = G__56167;
i__56147_56151 = G__56168;
continue;
}
} else {
}
}
break;
}
} else {
}

if(cljs.core.truth_(reload)){
figwheel.client.file_reloading.unprovide_BANG_.call(null,src);
} else {
}

return goog.require_figwheel_backup_(src);
});
/**
 * Reusable browser REPL bootstrapping. Patches the essential functions
 *   in goog.base to support re-loading of namespaces after page load.
 */
figwheel.client.file_reloading.bootstrap_goog_base = (function figwheel$client$file_reloading$bootstrap_goog_base(){
if(cljs.core.truth_(COMPILED)){
return null;
} else {
goog.require_figwheel_backup_ = (function (){var or__30175__auto__ = goog.require__;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return goog.require;
}
})();

goog.isProvided_ = (function (name){
return false;
});

figwheel.client.file_reloading.setup_path__GT_name_BANG_.call(null);

figwheel.client.file_reloading.setup_ns__GT_dependents_BANG_.call(null);

goog.addDependency_figwheel_backup_ = goog.addDependency;

goog.addDependency = (function() { 
var G__56169__delegate = function (args){
cljs.core.apply.call(null,figwheel.client.file_reloading.addDependency,args);

return cljs.core.apply.call(null,goog.addDependency_figwheel_backup_,args);
};
var G__56169 = function (var_args){
var args = null;
if (arguments.length > 0) {
var G__56170__i = 0, G__56170__a = new Array(arguments.length -  0);
while (G__56170__i < G__56170__a.length) {G__56170__a[G__56170__i] = arguments[G__56170__i + 0]; ++G__56170__i;}
  args = new cljs.core.IndexedSeq(G__56170__a,0,null);
} 
return G__56169__delegate.call(this,args);};
G__56169.cljs$lang$maxFixedArity = 0;
G__56169.cljs$lang$applyTo = (function (arglist__56171){
var args = cljs.core.seq(arglist__56171);
return G__56169__delegate(args);
});
G__56169.cljs$core$IFn$_invoke$arity$variadic = G__56169__delegate;
return G__56169;
})()
;

goog.constructNamespace_("cljs.user");

goog.global.CLOSURE_IMPORT_SCRIPT = figwheel.client.file_reloading.queued_file_reload;

return goog.require = figwheel.client.file_reloading.figwheel_require;
}
});
figwheel.client.file_reloading.patch_goog_base = (function figwheel$client$file_reloading$patch_goog_base(){
if(typeof figwheel.client.file_reloading.bootstrapped_cljs !== 'undefined'){
return null;
} else {
return (
figwheel.client.file_reloading.bootstrapped_cljs = (function (){
figwheel.client.file_reloading.bootstrap_goog_base.call(null);

return true;
})()
)
;
}
});
figwheel.client.file_reloading.gloader = ((typeof goog.net.jsloader.safeLoad !== 'undefined')?(function (p1__56172_SHARP_,p2__56173_SHARP_){
return goog.net.jsloader.safeLoad(goog.html.legacyconversions.trustedResourceUrlFromString([cljs.core.str.cljs$core$IFn$_invoke$arity$1(p1__56172_SHARP_)].join('')),p2__56173_SHARP_);
}):((typeof goog.net.jsloader.load !== 'undefined')?(function (p1__56174_SHARP_,p2__56175_SHARP_){
return goog.net.jsloader.load([cljs.core.str.cljs$core$IFn$_invoke$arity$1(p1__56174_SHARP_)].join(''),p2__56175_SHARP_);
}):(function(){throw cljs.core.ex_info.call(null,"No remote script loading function found.",cljs.core.PersistentArrayMap.EMPTY)})()
));
figwheel.client.file_reloading.reload_file_in_html_env = (function figwheel$client$file_reloading$reload_file_in_html_env(request_url,callback){

var G__56176 = figwheel.client.file_reloading.gloader.call(null,figwheel.client.file_reloading.add_cache_buster.call(null,request_url),({"cleanupWhenDone": true}));
G__56176.addCallback(((function (G__56176){
return (function (){
return cljs.core.apply.call(null,callback,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [true], null));
});})(G__56176))
);

G__56176.addErrback(((function (G__56176){
return (function (){
return cljs.core.apply.call(null,callback,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [false], null));
});})(G__56176))
);

return G__56176;
});
figwheel.client.file_reloading.reload_file_STAR_ = (function (){var pred__56177 = cljs.core._EQ_;
var expr__56178 = figwheel.client.utils.host_env_QMARK_.call(null);
if(cljs.core.truth_(pred__56177.call(null,new cljs.core.Keyword(null,"node","node",581201198),expr__56178))){
var node_path_lib = require("path");
var util_pattern = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(node_path_lib.sep),cljs.core.str.cljs$core$IFn$_invoke$arity$1(node_path_lib.join("goog","bootstrap","nodejs.js"))].join('');
var util_path = goog.object.findKey(require.cache,((function (node_path_lib,util_pattern,pred__56177,expr__56178){
return (function (v,k,o){
return goog.string.endsWith(k,util_pattern);
});})(node_path_lib,util_pattern,pred__56177,expr__56178))
);
var parts = cljs.core.pop.call(null,cljs.core.pop.call(null,clojure.string.split.call(null,util_path,/[\/\\]/)));
var root_path = clojure.string.join.call(null,node_path_lib.sep,parts);
return ((function (node_path_lib,util_pattern,util_path,parts,root_path,pred__56177,expr__56178){
return (function (request_url,callback){

var cache_path = node_path_lib.resolve(root_path,request_url);
goog.object.remove(require.cache,cache_path);

return callback.call(null,(function (){try{return require(cache_path);
}catch (e56180){if((e56180 instanceof Error)){
var e = e56180;
figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"error","error",-978969032),["Figwheel: Error loading file ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cache_path)].join(''));

figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"error","error",-978969032),e.stack);

return false;
} else {
throw e56180;

}
}})());
});
;})(node_path_lib,util_pattern,util_path,parts,root_path,pred__56177,expr__56178))
} else {
if(cljs.core.truth_(pred__56177.call(null,new cljs.core.Keyword(null,"html","html",-998796897),expr__56178))){
return figwheel.client.file_reloading.reload_file_in_html_env;
} else {
if(cljs.core.truth_(pred__56177.call(null,new cljs.core.Keyword(null,"react-native","react-native",-1543085138),expr__56178))){
return figwheel.client.file_reloading.reload_file_in_html_env;
} else {
if(cljs.core.truth_(pred__56177.call(null,new cljs.core.Keyword(null,"worker","worker",938239996),expr__56178))){
return ((function (pred__56177,expr__56178){
return (function (request_url,callback){

return callback.call(null,(function (){try{self.importScripts(figwheel.client.file_reloading.add_cache_buster.call(null,request_url));

return true;
}catch (e56181){if((e56181 instanceof Error)){
var e = e56181;
figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"error","error",-978969032),["Figwheel: Error loading file ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(request_url)].join(''));

figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"error","error",-978969032),e.stack);

return false;
} else {
throw e56181;

}
}})());
});
;})(pred__56177,expr__56178))
} else {
return ((function (pred__56177,expr__56178){
return (function (a,b){
throw "Reload not defined for this platform";
});
;})(pred__56177,expr__56178))
}
}
}
}
})();
figwheel.client.file_reloading.reload_file = (function figwheel$client$file_reloading$reload_file(p__56182,callback){
var map__56183 = p__56182;
var map__56183__$1 = ((((!((map__56183 == null)))?((((map__56183.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56183.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56183):map__56183);
var file_msg = map__56183__$1;
var request_url = cljs.core.get.call(null,map__56183__$1,new cljs.core.Keyword(null,"request-url","request-url",2100346596));

figwheel.client.utils.debug_prn.call(null,["FigWheel: Attempting to load ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(request_url)].join(''));

return figwheel.client.file_reloading.reload_file_STAR_.call(null,request_url,((function (map__56183,map__56183__$1,file_msg,request_url){
return (function (success_QMARK_){
if(cljs.core.truth_(success_QMARK_)){
figwheel.client.utils.debug_prn.call(null,["FigWheel: Successfully loaded ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(request_url)].join(''));

return cljs.core.apply.call(null,callback,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.assoc.call(null,file_msg,new cljs.core.Keyword(null,"loaded-file","loaded-file",-168399375),true)], null));
} else {
figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"error","error",-978969032),["Figwheel: Error loading file ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(request_url)].join(''));

return cljs.core.apply.call(null,callback,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [file_msg], null));
}
});})(map__56183,map__56183__$1,file_msg,request_url))
);
});
if(typeof figwheel.client.file_reloading.reload_chan !== 'undefined'){
} else {
figwheel.client.file_reloading.reload_chan = cljs.core.async.chan.call(null);
}
if(typeof figwheel.client.file_reloading.on_load_callbacks !== 'undefined'){
} else {
figwheel.client.file_reloading.on_load_callbacks = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
}
if(typeof figwheel.client.file_reloading.dependencies_loaded !== 'undefined'){
} else {
figwheel.client.file_reloading.dependencies_loaded = cljs.core.atom.call(null,cljs.core.PersistentVector.EMPTY);
}
figwheel.client.file_reloading.blocking_load = (function figwheel$client$file_reloading$blocking_load(url){
var out = cljs.core.async.chan.call(null);
figwheel.client.file_reloading.reload_file.call(null,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"request-url","request-url",2100346596),url], null),((function (out){
return (function (file_msg){
cljs.core.async.put_BANG_.call(null,out,file_msg);

return cljs.core.async.close_BANG_.call(null,out);
});})(out))
);

return out;
});
if(typeof figwheel.client.file_reloading.reloader_loop !== 'undefined'){
} else {
figwheel.client.file_reloading.reloader_loop = (function (){var c__54040__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto__){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto__){
return (function (state_56207){
var state_val_56208 = (state_56207[(1)]);
if((state_val_56208 === (7))){
var inst_56203 = (state_56207[(2)]);
var state_56207__$1 = state_56207;
var statearr_56209_56226 = state_56207__$1;
(statearr_56209_56226[(2)] = inst_56203);

(statearr_56209_56226[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56208 === (1))){
var state_56207__$1 = state_56207;
var statearr_56210_56227 = state_56207__$1;
(statearr_56210_56227[(2)] = null);

(statearr_56210_56227[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56208 === (4))){
var inst_56187 = (state_56207[(7)]);
var inst_56187__$1 = (state_56207[(2)]);
var state_56207__$1 = (function (){var statearr_56211 = state_56207;
(statearr_56211[(7)] = inst_56187__$1);

return statearr_56211;
})();
if(cljs.core.truth_(inst_56187__$1)){
var statearr_56212_56228 = state_56207__$1;
(statearr_56212_56228[(1)] = (5));

} else {
var statearr_56213_56229 = state_56207__$1;
(statearr_56213_56229[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56208 === (6))){
var state_56207__$1 = state_56207;
var statearr_56214_56230 = state_56207__$1;
(statearr_56214_56230[(2)] = null);

(statearr_56214_56230[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56208 === (3))){
var inst_56205 = (state_56207[(2)]);
var state_56207__$1 = state_56207;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_56207__$1,inst_56205);
} else {
if((state_val_56208 === (2))){
var state_56207__$1 = state_56207;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_56207__$1,(4),figwheel.client.file_reloading.reload_chan);
} else {
if((state_val_56208 === (11))){
var inst_56199 = (state_56207[(2)]);
var state_56207__$1 = (function (){var statearr_56215 = state_56207;
(statearr_56215[(8)] = inst_56199);

return statearr_56215;
})();
var statearr_56216_56231 = state_56207__$1;
(statearr_56216_56231[(2)] = null);

(statearr_56216_56231[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56208 === (9))){
var inst_56193 = (state_56207[(9)]);
var inst_56191 = (state_56207[(10)]);
var inst_56195 = inst_56193.call(null,inst_56191);
var state_56207__$1 = state_56207;
var statearr_56217_56232 = state_56207__$1;
(statearr_56217_56232[(2)] = inst_56195);

(statearr_56217_56232[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56208 === (5))){
var inst_56187 = (state_56207[(7)]);
var inst_56189 = figwheel.client.file_reloading.blocking_load.call(null,inst_56187);
var state_56207__$1 = state_56207;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_56207__$1,(8),inst_56189);
} else {
if((state_val_56208 === (10))){
var inst_56191 = (state_56207[(10)]);
var inst_56197 = cljs.core.swap_BANG_.call(null,figwheel.client.file_reloading.dependencies_loaded,cljs.core.conj,inst_56191);
var state_56207__$1 = state_56207;
var statearr_56218_56233 = state_56207__$1;
(statearr_56218_56233[(2)] = inst_56197);

(statearr_56218_56233[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56208 === (8))){
var inst_56193 = (state_56207[(9)]);
var inst_56187 = (state_56207[(7)]);
var inst_56191 = (state_56207[(2)]);
var inst_56192 = cljs.core.deref.call(null,figwheel.client.file_reloading.on_load_callbacks);
var inst_56193__$1 = cljs.core.get.call(null,inst_56192,inst_56187);
var state_56207__$1 = (function (){var statearr_56219 = state_56207;
(statearr_56219[(9)] = inst_56193__$1);

(statearr_56219[(10)] = inst_56191);

return statearr_56219;
})();
if(cljs.core.truth_(inst_56193__$1)){
var statearr_56220_56234 = state_56207__$1;
(statearr_56220_56234[(1)] = (9));

} else {
var statearr_56221_56235 = state_56207__$1;
(statearr_56221_56235[(1)] = (10));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
});})(c__54040__auto__))
;
return ((function (switch__53950__auto__,c__54040__auto__){
return (function() {
var figwheel$client$file_reloading$state_machine__53951__auto__ = null;
var figwheel$client$file_reloading$state_machine__53951__auto____0 = (function (){
var statearr_56222 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_56222[(0)] = figwheel$client$file_reloading$state_machine__53951__auto__);

(statearr_56222[(1)] = (1));

return statearr_56222;
});
var figwheel$client$file_reloading$state_machine__53951__auto____1 = (function (state_56207){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_56207);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e56223){if((e56223 instanceof Object)){
var ex__53954__auto__ = e56223;
var statearr_56224_56236 = state_56207;
(statearr_56224_56236[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_56207);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e56223;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__56237 = state_56207;
state_56207 = G__56237;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
figwheel$client$file_reloading$state_machine__53951__auto__ = function(state_56207){
switch(arguments.length){
case 0:
return figwheel$client$file_reloading$state_machine__53951__auto____0.call(this);
case 1:
return figwheel$client$file_reloading$state_machine__53951__auto____1.call(this,state_56207);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
figwheel$client$file_reloading$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = figwheel$client$file_reloading$state_machine__53951__auto____0;
figwheel$client$file_reloading$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = figwheel$client$file_reloading$state_machine__53951__auto____1;
return figwheel$client$file_reloading$state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto__))
})();
var state__54042__auto__ = (function (){var statearr_56225 = f__54041__auto__.call(null);
(statearr_56225[(6)] = c__54040__auto__);

return statearr_56225;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto__))
);

return c__54040__auto__;
})();
}
figwheel.client.file_reloading.queued_file_reload = (function figwheel$client$file_reloading$queued_file_reload(url){
return cljs.core.async.put_BANG_.call(null,figwheel.client.file_reloading.reload_chan,url);
});
figwheel.client.file_reloading.require_with_callback = (function figwheel$client$file_reloading$require_with_callback(p__56238,callback){
var map__56239 = p__56238;
var map__56239__$1 = ((((!((map__56239 == null)))?((((map__56239.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56239.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56239):map__56239);
var file_msg = map__56239__$1;
var namespace = cljs.core.get.call(null,map__56239__$1,new cljs.core.Keyword(null,"namespace","namespace",-377510372));
var request_url = figwheel.client.file_reloading.resolve_ns.call(null,namespace);
cljs.core.swap_BANG_.call(null,figwheel.client.file_reloading.on_load_callbacks,cljs.core.assoc,request_url,((function (request_url,map__56239,map__56239__$1,file_msg,namespace){
return (function (file_msg_SINGLEQUOTE_){
cljs.core.swap_BANG_.call(null,figwheel.client.file_reloading.on_load_callbacks,cljs.core.dissoc,request_url);

return cljs.core.apply.call(null,callback,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.merge.call(null,file_msg,cljs.core.select_keys.call(null,file_msg_SINGLEQUOTE_,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"loaded-file","loaded-file",-168399375)], null)))], null));
});})(request_url,map__56239,map__56239__$1,file_msg,namespace))
);

return figwheel.client.file_reloading.figwheel_require.call(null,cljs.core.name.call(null,namespace),true);
});
figwheel.client.file_reloading.figwheel_no_load_QMARK_ = (function figwheel$client$file_reloading$figwheel_no_load_QMARK_(p__56241){
var map__56242 = p__56241;
var map__56242__$1 = ((((!((map__56242 == null)))?((((map__56242.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56242.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56242):map__56242);
var file_msg = map__56242__$1;
var namespace = cljs.core.get.call(null,map__56242__$1,new cljs.core.Keyword(null,"namespace","namespace",-377510372));
var meta_pragmas = cljs.core.get.call(null,cljs.core.deref.call(null,figwheel.client.file_reloading.figwheel_meta_pragmas),cljs.core.name.call(null,namespace));
return new cljs.core.Keyword(null,"figwheel-no-load","figwheel-no-load",-555840179).cljs$core$IFn$_invoke$arity$1(meta_pragmas);
});
figwheel.client.file_reloading.reload_file_QMARK_ = (function figwheel$client$file_reloading$reload_file_QMARK_(p__56244){
var map__56245 = p__56244;
var map__56245__$1 = ((((!((map__56245 == null)))?((((map__56245.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56245.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56245):map__56245);
var file_msg = map__56245__$1;
var namespace = cljs.core.get.call(null,map__56245__$1,new cljs.core.Keyword(null,"namespace","namespace",-377510372));

var meta_pragmas = cljs.core.get.call(null,cljs.core.deref.call(null,figwheel.client.file_reloading.figwheel_meta_pragmas),cljs.core.name.call(null,namespace));
var and__30163__auto__ = cljs.core.not.call(null,figwheel.client.file_reloading.figwheel_no_load_QMARK_.call(null,file_msg));
if(and__30163__auto__){
var or__30175__auto__ = new cljs.core.Keyword(null,"figwheel-always","figwheel-always",799819691).cljs$core$IFn$_invoke$arity$1(meta_pragmas);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
var or__30175__auto____$1 = new cljs.core.Keyword(null,"figwheel-load","figwheel-load",1316089175).cljs$core$IFn$_invoke$arity$1(meta_pragmas);
if(cljs.core.truth_(or__30175__auto____$1)){
return or__30175__auto____$1;
} else {
return figwheel.client.file_reloading.provided_QMARK_.call(null,cljs.core.name.call(null,namespace));
}
}
} else {
return and__30163__auto__;
}
});
figwheel.client.file_reloading.js_reload = (function figwheel$client$file_reloading$js_reload(p__56247,callback){
var map__56248 = p__56247;
var map__56248__$1 = ((((!((map__56248 == null)))?((((map__56248.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56248.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56248):map__56248);
var file_msg = map__56248__$1;
var request_url = cljs.core.get.call(null,map__56248__$1,new cljs.core.Keyword(null,"request-url","request-url",2100346596));
var namespace = cljs.core.get.call(null,map__56248__$1,new cljs.core.Keyword(null,"namespace","namespace",-377510372));

if(cljs.core.truth_(figwheel.client.file_reloading.reload_file_QMARK_.call(null,file_msg))){
return figwheel.client.file_reloading.require_with_callback.call(null,file_msg,callback);
} else {
figwheel.client.utils.debug_prn.call(null,["Figwheel: Not trying to load file ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(request_url)].join(''));

return cljs.core.apply.call(null,callback,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [file_msg], null));
}
});
figwheel.client.file_reloading.reload_js_file = (function figwheel$client$file_reloading$reload_js_file(file_msg){
var out = cljs.core.async.chan.call(null);
figwheel.client.file_reloading.js_reload.call(null,file_msg,((function (out){
return (function (url){
cljs.core.async.put_BANG_.call(null,out,url);

return cljs.core.async.close_BANG_.call(null,out);
});})(out))
);

return out;
});
/**
 * Returns a chanel with one collection of loaded filenames on it.
 */
figwheel.client.file_reloading.load_all_js_files = (function figwheel$client$file_reloading$load_all_js_files(files){
var out = cljs.core.async.chan.call(null);
var c__54040__auto___56298 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___56298,out){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___56298,out){
return (function (state_56283){
var state_val_56284 = (state_56283[(1)]);
if((state_val_56284 === (1))){
var inst_56257 = cljs.core.seq.call(null,files);
var inst_56258 = cljs.core.first.call(null,inst_56257);
var inst_56259 = cljs.core.next.call(null,inst_56257);
var inst_56260 = files;
var state_56283__$1 = (function (){var statearr_56285 = state_56283;
(statearr_56285[(7)] = inst_56259);

(statearr_56285[(8)] = inst_56258);

(statearr_56285[(9)] = inst_56260);

return statearr_56285;
})();
var statearr_56286_56299 = state_56283__$1;
(statearr_56286_56299[(2)] = null);

(statearr_56286_56299[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56284 === (2))){
var inst_56266 = (state_56283[(10)]);
var inst_56260 = (state_56283[(9)]);
var inst_56265 = cljs.core.seq.call(null,inst_56260);
var inst_56266__$1 = cljs.core.first.call(null,inst_56265);
var inst_56267 = cljs.core.next.call(null,inst_56265);
var inst_56268 = (inst_56266__$1 == null);
var inst_56269 = cljs.core.not.call(null,inst_56268);
var state_56283__$1 = (function (){var statearr_56287 = state_56283;
(statearr_56287[(11)] = inst_56267);

(statearr_56287[(10)] = inst_56266__$1);

return statearr_56287;
})();
if(inst_56269){
var statearr_56288_56300 = state_56283__$1;
(statearr_56288_56300[(1)] = (4));

} else {
var statearr_56289_56301 = state_56283__$1;
(statearr_56289_56301[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56284 === (3))){
var inst_56281 = (state_56283[(2)]);
var state_56283__$1 = state_56283;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_56283__$1,inst_56281);
} else {
if((state_val_56284 === (4))){
var inst_56266 = (state_56283[(10)]);
var inst_56271 = figwheel.client.file_reloading.reload_js_file.call(null,inst_56266);
var state_56283__$1 = state_56283;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_56283__$1,(7),inst_56271);
} else {
if((state_val_56284 === (5))){
var inst_56277 = cljs.core.async.close_BANG_.call(null,out);
var state_56283__$1 = state_56283;
var statearr_56290_56302 = state_56283__$1;
(statearr_56290_56302[(2)] = inst_56277);

(statearr_56290_56302[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56284 === (6))){
var inst_56279 = (state_56283[(2)]);
var state_56283__$1 = state_56283;
var statearr_56291_56303 = state_56283__$1;
(statearr_56291_56303[(2)] = inst_56279);

(statearr_56291_56303[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56284 === (7))){
var inst_56267 = (state_56283[(11)]);
var inst_56273 = (state_56283[(2)]);
var inst_56274 = cljs.core.async.put_BANG_.call(null,out,inst_56273);
var inst_56260 = inst_56267;
var state_56283__$1 = (function (){var statearr_56292 = state_56283;
(statearr_56292[(12)] = inst_56274);

(statearr_56292[(9)] = inst_56260);

return statearr_56292;
})();
var statearr_56293_56304 = state_56283__$1;
(statearr_56293_56304[(2)] = null);

(statearr_56293_56304[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
});})(c__54040__auto___56298,out))
;
return ((function (switch__53950__auto__,c__54040__auto___56298,out){
return (function() {
var figwheel$client$file_reloading$load_all_js_files_$_state_machine__53951__auto__ = null;
var figwheel$client$file_reloading$load_all_js_files_$_state_machine__53951__auto____0 = (function (){
var statearr_56294 = [null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_56294[(0)] = figwheel$client$file_reloading$load_all_js_files_$_state_machine__53951__auto__);

(statearr_56294[(1)] = (1));

return statearr_56294;
});
var figwheel$client$file_reloading$load_all_js_files_$_state_machine__53951__auto____1 = (function (state_56283){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_56283);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e56295){if((e56295 instanceof Object)){
var ex__53954__auto__ = e56295;
var statearr_56296_56305 = state_56283;
(statearr_56296_56305[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_56283);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e56295;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__56306 = state_56283;
state_56283 = G__56306;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
figwheel$client$file_reloading$load_all_js_files_$_state_machine__53951__auto__ = function(state_56283){
switch(arguments.length){
case 0:
return figwheel$client$file_reloading$load_all_js_files_$_state_machine__53951__auto____0.call(this);
case 1:
return figwheel$client$file_reloading$load_all_js_files_$_state_machine__53951__auto____1.call(this,state_56283);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
figwheel$client$file_reloading$load_all_js_files_$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = figwheel$client$file_reloading$load_all_js_files_$_state_machine__53951__auto____0;
figwheel$client$file_reloading$load_all_js_files_$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = figwheel$client$file_reloading$load_all_js_files_$_state_machine__53951__auto____1;
return figwheel$client$file_reloading$load_all_js_files_$_state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___56298,out))
})();
var state__54042__auto__ = (function (){var statearr_56297 = f__54041__auto__.call(null);
(statearr_56297[(6)] = c__54040__auto___56298);

return statearr_56297;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___56298,out))
);


return cljs.core.async.into.call(null,cljs.core.PersistentVector.EMPTY,out);
});
figwheel.client.file_reloading.eval_body = (function figwheel$client$file_reloading$eval_body(p__56307,opts){
var map__56308 = p__56307;
var map__56308__$1 = ((((!((map__56308 == null)))?((((map__56308.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56308.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56308):map__56308);
var eval_body = cljs.core.get.call(null,map__56308__$1,new cljs.core.Keyword(null,"eval-body","eval-body",-907279883));
var file = cljs.core.get.call(null,map__56308__$1,new cljs.core.Keyword(null,"file","file",-1269645878));
if(cljs.core.truth_((function (){var and__30163__auto__ = eval_body;
if(cljs.core.truth_(and__30163__auto__)){
return typeof eval_body === 'string';
} else {
return and__30163__auto__;
}
})())){
var code = eval_body;
try{figwheel.client.utils.debug_prn.call(null,["Evaling file ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(file)].join(''));

return figwheel.client.utils.eval_helper.call(null,code,opts);
}catch (e56310){var e = e56310;
return figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"error","error",-978969032),["Unable to evaluate ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(file)].join(''));
}} else {
return null;
}
});
figwheel.client.file_reloading.expand_files = (function figwheel$client$file_reloading$expand_files(files){
var deps = figwheel.client.file_reloading.get_all_dependents.call(null,cljs.core.map.call(null,new cljs.core.Keyword(null,"namespace","namespace",-377510372),files));
return cljs.core.filter.call(null,cljs.core.comp.call(null,cljs.core.not,cljs.core.partial.call(null,cljs.core.re_matches,/figwheel\.connect.*/),new cljs.core.Keyword(null,"namespace","namespace",-377510372)),cljs.core.map.call(null,((function (deps){
return (function (n){
var temp__5288__auto__ = cljs.core.first.call(null,cljs.core.filter.call(null,((function (deps){
return (function (p1__56311_SHARP_){
return cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"namespace","namespace",-377510372).cljs$core$IFn$_invoke$arity$1(p1__56311_SHARP_),n);
});})(deps))
,files));
if(cljs.core.truth_(temp__5288__auto__)){
var file_msg = temp__5288__auto__;
return file_msg;
} else {
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"namespace","namespace",-377510372),new cljs.core.Keyword(null,"namespace","namespace",-377510372),n], null);
}
});})(deps))
,deps));
});
figwheel.client.file_reloading.sort_files = (function figwheel$client$file_reloading$sort_files(files){
if((cljs.core.count.call(null,files) <= (1))){
return files;
} else {
var keep_files = cljs.core.set.call(null,cljs.core.keep.call(null,new cljs.core.Keyword(null,"namespace","namespace",-377510372),files));
return cljs.core.filter.call(null,cljs.core.comp.call(null,keep_files,new cljs.core.Keyword(null,"namespace","namespace",-377510372)),figwheel.client.file_reloading.expand_files.call(null,files));
}
});
figwheel.client.file_reloading.get_figwheel_always = (function figwheel$client$file_reloading$get_figwheel_always(){
return cljs.core.map.call(null,(function (p__56312){
var vec__56313 = p__56312;
var k = cljs.core.nth.call(null,vec__56313,(0),null);
var v = cljs.core.nth.call(null,vec__56313,(1),null);
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"namespace","namespace",-377510372),k,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"namespace","namespace",-377510372)], null);
}),cljs.core.filter.call(null,(function (p__56316){
var vec__56317 = p__56316;
var k = cljs.core.nth.call(null,vec__56317,(0),null);
var v = cljs.core.nth.call(null,vec__56317,(1),null);
return new cljs.core.Keyword(null,"figwheel-always","figwheel-always",799819691).cljs$core$IFn$_invoke$arity$1(v);
}),cljs.core.deref.call(null,figwheel.client.file_reloading.figwheel_meta_pragmas)));
});
figwheel.client.file_reloading.reload_js_files = (function figwheel$client$file_reloading$reload_js_files(p__56323,p__56324){
var map__56325 = p__56323;
var map__56325__$1 = ((((!((map__56325 == null)))?((((map__56325.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56325.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56325):map__56325);
var opts = map__56325__$1;
var before_jsload = cljs.core.get.call(null,map__56325__$1,new cljs.core.Keyword(null,"before-jsload","before-jsload",-847513128));
var on_jsload = cljs.core.get.call(null,map__56325__$1,new cljs.core.Keyword(null,"on-jsload","on-jsload",-395756602));
var reload_dependents = cljs.core.get.call(null,map__56325__$1,new cljs.core.Keyword(null,"reload-dependents","reload-dependents",-956865430));
var map__56326 = p__56324;
var map__56326__$1 = ((((!((map__56326 == null)))?((((map__56326.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56326.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56326):map__56326);
var msg = map__56326__$1;
var files = cljs.core.get.call(null,map__56326__$1,new cljs.core.Keyword(null,"files","files",-472457450));
var figwheel_meta = cljs.core.get.call(null,map__56326__$1,new cljs.core.Keyword(null,"figwheel-meta","figwheel-meta",-225970237));
var recompile_dependents = cljs.core.get.call(null,map__56326__$1,new cljs.core.Keyword(null,"recompile-dependents","recompile-dependents",523804171));
if(cljs.core.empty_QMARK_.call(null,figwheel_meta)){
} else {
cljs.core.reset_BANG_.call(null,figwheel.client.file_reloading.figwheel_meta_pragmas,figwheel_meta);
}

var c__54040__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (state_56480){
var state_val_56481 = (state_56480[(1)]);
if((state_val_56481 === (7))){
var inst_56340 = (state_56480[(7)]);
var inst_56341 = (state_56480[(8)]);
var inst_56343 = (state_56480[(9)]);
var inst_56342 = (state_56480[(10)]);
var inst_56348 = cljs.core._nth.call(null,inst_56341,inst_56343);
var inst_56349 = figwheel.client.file_reloading.eval_body.call(null,inst_56348,opts);
var inst_56350 = (inst_56343 + (1));
var tmp56482 = inst_56340;
var tmp56483 = inst_56341;
var tmp56484 = inst_56342;
var inst_56340__$1 = tmp56482;
var inst_56341__$1 = tmp56483;
var inst_56342__$1 = tmp56484;
var inst_56343__$1 = inst_56350;
var state_56480__$1 = (function (){var statearr_56485 = state_56480;
(statearr_56485[(7)] = inst_56340__$1);

(statearr_56485[(8)] = inst_56341__$1);

(statearr_56485[(9)] = inst_56343__$1);

(statearr_56485[(11)] = inst_56349);

(statearr_56485[(10)] = inst_56342__$1);

return statearr_56485;
})();
var statearr_56486_56569 = state_56480__$1;
(statearr_56486_56569[(2)] = null);

(statearr_56486_56569[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (20))){
var inst_56383 = (state_56480[(12)]);
var inst_56391 = figwheel.client.file_reloading.sort_files.call(null,inst_56383);
var state_56480__$1 = state_56480;
var statearr_56487_56570 = state_56480__$1;
(statearr_56487_56570[(2)] = inst_56391);

(statearr_56487_56570[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (27))){
var state_56480__$1 = state_56480;
var statearr_56488_56571 = state_56480__$1;
(statearr_56488_56571[(2)] = null);

(statearr_56488_56571[(1)] = (28));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (1))){
var inst_56332 = (state_56480[(13)]);
var inst_56329 = before_jsload.call(null,files);
var inst_56330 = figwheel.client.file_reloading.before_jsload_custom_event.call(null,files);
var inst_56331 = (function (){return ((function (inst_56332,inst_56329,inst_56330,state_val_56481,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (p1__56320_SHARP_){
return new cljs.core.Keyword(null,"eval-body","eval-body",-907279883).cljs$core$IFn$_invoke$arity$1(p1__56320_SHARP_);
});
;})(inst_56332,inst_56329,inst_56330,state_val_56481,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var inst_56332__$1 = cljs.core.filter.call(null,inst_56331,files);
var inst_56333 = cljs.core.not_empty.call(null,inst_56332__$1);
var state_56480__$1 = (function (){var statearr_56489 = state_56480;
(statearr_56489[(14)] = inst_56330);

(statearr_56489[(15)] = inst_56329);

(statearr_56489[(13)] = inst_56332__$1);

return statearr_56489;
})();
if(cljs.core.truth_(inst_56333)){
var statearr_56490_56572 = state_56480__$1;
(statearr_56490_56572[(1)] = (2));

} else {
var statearr_56491_56573 = state_56480__$1;
(statearr_56491_56573[(1)] = (3));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (24))){
var state_56480__$1 = state_56480;
var statearr_56492_56574 = state_56480__$1;
(statearr_56492_56574[(2)] = null);

(statearr_56492_56574[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (39))){
var inst_56433 = (state_56480[(16)]);
var state_56480__$1 = state_56480;
var statearr_56493_56575 = state_56480__$1;
(statearr_56493_56575[(2)] = inst_56433);

(statearr_56493_56575[(1)] = (40));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (46))){
var inst_56475 = (state_56480[(2)]);
var state_56480__$1 = state_56480;
var statearr_56494_56576 = state_56480__$1;
(statearr_56494_56576[(2)] = inst_56475);

(statearr_56494_56576[(1)] = (31));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (4))){
var inst_56377 = (state_56480[(2)]);
var inst_56378 = cljs.core.List.EMPTY;
var inst_56379 = cljs.core.reset_BANG_.call(null,figwheel.client.file_reloading.dependencies_loaded,inst_56378);
var inst_56380 = (function (){return ((function (inst_56377,inst_56378,inst_56379,state_val_56481,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (p1__56321_SHARP_){
var and__30163__auto__ = new cljs.core.Keyword(null,"namespace","namespace",-377510372).cljs$core$IFn$_invoke$arity$1(p1__56321_SHARP_);
if(cljs.core.truth_(and__30163__auto__)){
return (cljs.core.not.call(null,new cljs.core.Keyword(null,"eval-body","eval-body",-907279883).cljs$core$IFn$_invoke$arity$1(p1__56321_SHARP_))) && (cljs.core.not.call(null,figwheel.client.file_reloading.figwheel_no_load_QMARK_.call(null,p1__56321_SHARP_)));
} else {
return and__30163__auto__;
}
});
;})(inst_56377,inst_56378,inst_56379,state_val_56481,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var inst_56381 = cljs.core.filter.call(null,inst_56380,files);
var inst_56382 = figwheel.client.file_reloading.get_figwheel_always.call(null);
var inst_56383 = cljs.core.concat.call(null,inst_56381,inst_56382);
var state_56480__$1 = (function (){var statearr_56495 = state_56480;
(statearr_56495[(17)] = inst_56379);

(statearr_56495[(18)] = inst_56377);

(statearr_56495[(12)] = inst_56383);

return statearr_56495;
})();
if(cljs.core.truth_(reload_dependents)){
var statearr_56496_56577 = state_56480__$1;
(statearr_56496_56577[(1)] = (16));

} else {
var statearr_56497_56578 = state_56480__$1;
(statearr_56497_56578[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (15))){
var inst_56367 = (state_56480[(2)]);
var state_56480__$1 = state_56480;
var statearr_56498_56579 = state_56480__$1;
(statearr_56498_56579[(2)] = inst_56367);

(statearr_56498_56579[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (21))){
var inst_56393 = (state_56480[(19)]);
var inst_56393__$1 = (state_56480[(2)]);
var inst_56394 = figwheel.client.file_reloading.load_all_js_files.call(null,inst_56393__$1);
var state_56480__$1 = (function (){var statearr_56499 = state_56480;
(statearr_56499[(19)] = inst_56393__$1);

return statearr_56499;
})();
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_56480__$1,(22),inst_56394);
} else {
if((state_val_56481 === (31))){
var inst_56478 = (state_56480[(2)]);
var state_56480__$1 = state_56480;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_56480__$1,inst_56478);
} else {
if((state_val_56481 === (32))){
var inst_56433 = (state_56480[(16)]);
var inst_56438 = inst_56433.cljs$lang$protocol_mask$partition0$;
var inst_56439 = (inst_56438 & (64));
var inst_56440 = inst_56433.cljs$core$ISeq$;
var inst_56441 = (cljs.core.PROTOCOL_SENTINEL === inst_56440);
var inst_56442 = (inst_56439) || (inst_56441);
var state_56480__$1 = state_56480;
if(cljs.core.truth_(inst_56442)){
var statearr_56500_56580 = state_56480__$1;
(statearr_56500_56580[(1)] = (35));

} else {
var statearr_56501_56581 = state_56480__$1;
(statearr_56501_56581[(1)] = (36));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (40))){
var inst_56455 = (state_56480[(20)]);
var inst_56454 = (state_56480[(2)]);
var inst_56455__$1 = cljs.core.get.call(null,inst_56454,new cljs.core.Keyword(null,"figwheel-no-load","figwheel-no-load",-555840179));
var inst_56456 = cljs.core.get.call(null,inst_56454,new cljs.core.Keyword(null,"not-required","not-required",-950359114));
var inst_56457 = cljs.core.not_empty.call(null,inst_56455__$1);
var state_56480__$1 = (function (){var statearr_56502 = state_56480;
(statearr_56502[(21)] = inst_56456);

(statearr_56502[(20)] = inst_56455__$1);

return statearr_56502;
})();
if(cljs.core.truth_(inst_56457)){
var statearr_56503_56582 = state_56480__$1;
(statearr_56503_56582[(1)] = (41));

} else {
var statearr_56504_56583 = state_56480__$1;
(statearr_56504_56583[(1)] = (42));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (33))){
var state_56480__$1 = state_56480;
var statearr_56505_56584 = state_56480__$1;
(statearr_56505_56584[(2)] = false);

(statearr_56505_56584[(1)] = (34));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (13))){
var inst_56353 = (state_56480[(22)]);
var inst_56357 = cljs.core.chunk_first.call(null,inst_56353);
var inst_56358 = cljs.core.chunk_rest.call(null,inst_56353);
var inst_56359 = cljs.core.count.call(null,inst_56357);
var inst_56340 = inst_56358;
var inst_56341 = inst_56357;
var inst_56342 = inst_56359;
var inst_56343 = (0);
var state_56480__$1 = (function (){var statearr_56506 = state_56480;
(statearr_56506[(7)] = inst_56340);

(statearr_56506[(8)] = inst_56341);

(statearr_56506[(9)] = inst_56343);

(statearr_56506[(10)] = inst_56342);

return statearr_56506;
})();
var statearr_56507_56585 = state_56480__$1;
(statearr_56507_56585[(2)] = null);

(statearr_56507_56585[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (22))){
var inst_56396 = (state_56480[(23)]);
var inst_56393 = (state_56480[(19)]);
var inst_56397 = (state_56480[(24)]);
var inst_56401 = (state_56480[(25)]);
var inst_56396__$1 = (state_56480[(2)]);
var inst_56397__$1 = cljs.core.filter.call(null,new cljs.core.Keyword(null,"loaded-file","loaded-file",-168399375),inst_56396__$1);
var inst_56398 = (function (){var all_files = inst_56393;
var res_SINGLEQUOTE_ = inst_56396__$1;
var res = inst_56397__$1;
return ((function (all_files,res_SINGLEQUOTE_,res,inst_56396,inst_56393,inst_56397,inst_56401,inst_56396__$1,inst_56397__$1,state_val_56481,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (p1__56322_SHARP_){
return cljs.core.not.call(null,new cljs.core.Keyword(null,"loaded-file","loaded-file",-168399375).cljs$core$IFn$_invoke$arity$1(p1__56322_SHARP_));
});
;})(all_files,res_SINGLEQUOTE_,res,inst_56396,inst_56393,inst_56397,inst_56401,inst_56396__$1,inst_56397__$1,state_val_56481,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var inst_56399 = cljs.core.filter.call(null,inst_56398,inst_56396__$1);
var inst_56400 = cljs.core.deref.call(null,figwheel.client.file_reloading.dependencies_loaded);
var inst_56401__$1 = cljs.core.filter.call(null,new cljs.core.Keyword(null,"loaded-file","loaded-file",-168399375),inst_56400);
var inst_56402 = cljs.core.not_empty.call(null,inst_56401__$1);
var state_56480__$1 = (function (){var statearr_56508 = state_56480;
(statearr_56508[(23)] = inst_56396__$1);

(statearr_56508[(24)] = inst_56397__$1);

(statearr_56508[(26)] = inst_56399);

(statearr_56508[(25)] = inst_56401__$1);

return statearr_56508;
})();
if(cljs.core.truth_(inst_56402)){
var statearr_56509_56586 = state_56480__$1;
(statearr_56509_56586[(1)] = (23));

} else {
var statearr_56510_56587 = state_56480__$1;
(statearr_56510_56587[(1)] = (24));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (36))){
var state_56480__$1 = state_56480;
var statearr_56511_56588 = state_56480__$1;
(statearr_56511_56588[(2)] = false);

(statearr_56511_56588[(1)] = (37));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (41))){
var inst_56455 = (state_56480[(20)]);
var inst_56459 = cljs.core.comp.call(null,figwheel.client.file_reloading.name__GT_path,new cljs.core.Keyword(null,"namespace","namespace",-377510372));
var inst_56460 = cljs.core.map.call(null,inst_56459,inst_56455);
var inst_56461 = cljs.core.pr_str.call(null,inst_56460);
var inst_56462 = ["figwheel-no-load meta-data: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(inst_56461)].join('');
var inst_56463 = figwheel.client.utils.log.call(null,inst_56462);
var state_56480__$1 = state_56480;
var statearr_56512_56589 = state_56480__$1;
(statearr_56512_56589[(2)] = inst_56463);

(statearr_56512_56589[(1)] = (43));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (43))){
var inst_56456 = (state_56480[(21)]);
var inst_56466 = (state_56480[(2)]);
var inst_56467 = cljs.core.not_empty.call(null,inst_56456);
var state_56480__$1 = (function (){var statearr_56513 = state_56480;
(statearr_56513[(27)] = inst_56466);

return statearr_56513;
})();
if(cljs.core.truth_(inst_56467)){
var statearr_56514_56590 = state_56480__$1;
(statearr_56514_56590[(1)] = (44));

} else {
var statearr_56515_56591 = state_56480__$1;
(statearr_56515_56591[(1)] = (45));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (29))){
var inst_56433 = (state_56480[(16)]);
var inst_56396 = (state_56480[(23)]);
var inst_56393 = (state_56480[(19)]);
var inst_56397 = (state_56480[(24)]);
var inst_56399 = (state_56480[(26)]);
var inst_56401 = (state_56480[(25)]);
var inst_56429 = figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"debug","debug",-1608172596),"Figwheel: NOT loading these files ");
var inst_56432 = (function (){var all_files = inst_56393;
var res_SINGLEQUOTE_ = inst_56396;
var res = inst_56397;
var files_not_loaded = inst_56399;
var dependencies_that_loaded = inst_56401;
return ((function (all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_56433,inst_56396,inst_56393,inst_56397,inst_56399,inst_56401,inst_56429,state_val_56481,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (p__56431){
var map__56516 = p__56431;
var map__56516__$1 = ((((!((map__56516 == null)))?((((map__56516.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56516.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56516):map__56516);
var namespace = cljs.core.get.call(null,map__56516__$1,new cljs.core.Keyword(null,"namespace","namespace",-377510372));
var meta_data = cljs.core.get.call(null,cljs.core.deref.call(null,figwheel.client.file_reloading.figwheel_meta_pragmas),cljs.core.name.call(null,namespace));
if((meta_data == null)){
return new cljs.core.Keyword(null,"not-required","not-required",-950359114);
} else {
if(cljs.core.truth_(meta_data.call(null,new cljs.core.Keyword(null,"figwheel-no-load","figwheel-no-load",-555840179)))){
return new cljs.core.Keyword(null,"figwheel-no-load","figwheel-no-load",-555840179);
} else {
return new cljs.core.Keyword(null,"not-required","not-required",-950359114);

}
}
});
;})(all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_56433,inst_56396,inst_56393,inst_56397,inst_56399,inst_56401,inst_56429,state_val_56481,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var inst_56433__$1 = cljs.core.group_by.call(null,inst_56432,inst_56399);
var inst_56435 = (inst_56433__$1 == null);
var inst_56436 = cljs.core.not.call(null,inst_56435);
var state_56480__$1 = (function (){var statearr_56518 = state_56480;
(statearr_56518[(16)] = inst_56433__$1);

(statearr_56518[(28)] = inst_56429);

return statearr_56518;
})();
if(inst_56436){
var statearr_56519_56592 = state_56480__$1;
(statearr_56519_56592[(1)] = (32));

} else {
var statearr_56520_56593 = state_56480__$1;
(statearr_56520_56593[(1)] = (33));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (44))){
var inst_56456 = (state_56480[(21)]);
var inst_56469 = cljs.core.map.call(null,new cljs.core.Keyword(null,"file","file",-1269645878),inst_56456);
var inst_56470 = cljs.core.pr_str.call(null,inst_56469);
var inst_56471 = ["not required: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(inst_56470)].join('');
var inst_56472 = figwheel.client.utils.log.call(null,inst_56471);
var state_56480__$1 = state_56480;
var statearr_56521_56594 = state_56480__$1;
(statearr_56521_56594[(2)] = inst_56472);

(statearr_56521_56594[(1)] = (46));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (6))){
var inst_56374 = (state_56480[(2)]);
var state_56480__$1 = state_56480;
var statearr_56522_56595 = state_56480__$1;
(statearr_56522_56595[(2)] = inst_56374);

(statearr_56522_56595[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (28))){
var inst_56399 = (state_56480[(26)]);
var inst_56426 = (state_56480[(2)]);
var inst_56427 = cljs.core.not_empty.call(null,inst_56399);
var state_56480__$1 = (function (){var statearr_56523 = state_56480;
(statearr_56523[(29)] = inst_56426);

return statearr_56523;
})();
if(cljs.core.truth_(inst_56427)){
var statearr_56524_56596 = state_56480__$1;
(statearr_56524_56596[(1)] = (29));

} else {
var statearr_56525_56597 = state_56480__$1;
(statearr_56525_56597[(1)] = (30));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (25))){
var inst_56397 = (state_56480[(24)]);
var inst_56413 = (state_56480[(2)]);
var inst_56414 = cljs.core.not_empty.call(null,inst_56397);
var state_56480__$1 = (function (){var statearr_56526 = state_56480;
(statearr_56526[(30)] = inst_56413);

return statearr_56526;
})();
if(cljs.core.truth_(inst_56414)){
var statearr_56527_56598 = state_56480__$1;
(statearr_56527_56598[(1)] = (26));

} else {
var statearr_56528_56599 = state_56480__$1;
(statearr_56528_56599[(1)] = (27));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (34))){
var inst_56449 = (state_56480[(2)]);
var state_56480__$1 = state_56480;
if(cljs.core.truth_(inst_56449)){
var statearr_56529_56600 = state_56480__$1;
(statearr_56529_56600[(1)] = (38));

} else {
var statearr_56530_56601 = state_56480__$1;
(statearr_56530_56601[(1)] = (39));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (17))){
var state_56480__$1 = state_56480;
var statearr_56531_56602 = state_56480__$1;
(statearr_56531_56602[(2)] = recompile_dependents);

(statearr_56531_56602[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (3))){
var state_56480__$1 = state_56480;
var statearr_56532_56603 = state_56480__$1;
(statearr_56532_56603[(2)] = null);

(statearr_56532_56603[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (12))){
var inst_56370 = (state_56480[(2)]);
var state_56480__$1 = state_56480;
var statearr_56533_56604 = state_56480__$1;
(statearr_56533_56604[(2)] = inst_56370);

(statearr_56533_56604[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (2))){
var inst_56332 = (state_56480[(13)]);
var inst_56339 = cljs.core.seq.call(null,inst_56332);
var inst_56340 = inst_56339;
var inst_56341 = null;
var inst_56342 = (0);
var inst_56343 = (0);
var state_56480__$1 = (function (){var statearr_56534 = state_56480;
(statearr_56534[(7)] = inst_56340);

(statearr_56534[(8)] = inst_56341);

(statearr_56534[(9)] = inst_56343);

(statearr_56534[(10)] = inst_56342);

return statearr_56534;
})();
var statearr_56535_56605 = state_56480__$1;
(statearr_56535_56605[(2)] = null);

(statearr_56535_56605[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (23))){
var inst_56396 = (state_56480[(23)]);
var inst_56393 = (state_56480[(19)]);
var inst_56397 = (state_56480[(24)]);
var inst_56399 = (state_56480[(26)]);
var inst_56401 = (state_56480[(25)]);
var inst_56404 = figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"debug","debug",-1608172596),"Figwheel: loaded these dependencies");
var inst_56406 = (function (){var all_files = inst_56393;
var res_SINGLEQUOTE_ = inst_56396;
var res = inst_56397;
var files_not_loaded = inst_56399;
var dependencies_that_loaded = inst_56401;
return ((function (all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_56396,inst_56393,inst_56397,inst_56399,inst_56401,inst_56404,state_val_56481,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (p__56405){
var map__56536 = p__56405;
var map__56536__$1 = ((((!((map__56536 == null)))?((((map__56536.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56536.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56536):map__56536);
var request_url = cljs.core.get.call(null,map__56536__$1,new cljs.core.Keyword(null,"request-url","request-url",2100346596));
return clojure.string.replace.call(null,request_url,goog.basePath,"");
});
;})(all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_56396,inst_56393,inst_56397,inst_56399,inst_56401,inst_56404,state_val_56481,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var inst_56407 = cljs.core.reverse.call(null,inst_56401);
var inst_56408 = cljs.core.map.call(null,inst_56406,inst_56407);
var inst_56409 = cljs.core.pr_str.call(null,inst_56408);
var inst_56410 = figwheel.client.utils.log.call(null,inst_56409);
var state_56480__$1 = (function (){var statearr_56538 = state_56480;
(statearr_56538[(31)] = inst_56404);

return statearr_56538;
})();
var statearr_56539_56606 = state_56480__$1;
(statearr_56539_56606[(2)] = inst_56410);

(statearr_56539_56606[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (35))){
var state_56480__$1 = state_56480;
var statearr_56540_56607 = state_56480__$1;
(statearr_56540_56607[(2)] = true);

(statearr_56540_56607[(1)] = (37));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (19))){
var inst_56383 = (state_56480[(12)]);
var inst_56389 = figwheel.client.file_reloading.expand_files.call(null,inst_56383);
var state_56480__$1 = state_56480;
var statearr_56541_56608 = state_56480__$1;
(statearr_56541_56608[(2)] = inst_56389);

(statearr_56541_56608[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (11))){
var state_56480__$1 = state_56480;
var statearr_56542_56609 = state_56480__$1;
(statearr_56542_56609[(2)] = null);

(statearr_56542_56609[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (9))){
var inst_56372 = (state_56480[(2)]);
var state_56480__$1 = state_56480;
var statearr_56543_56610 = state_56480__$1;
(statearr_56543_56610[(2)] = inst_56372);

(statearr_56543_56610[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (5))){
var inst_56343 = (state_56480[(9)]);
var inst_56342 = (state_56480[(10)]);
var inst_56345 = (inst_56343 < inst_56342);
var inst_56346 = inst_56345;
var state_56480__$1 = state_56480;
if(cljs.core.truth_(inst_56346)){
var statearr_56544_56611 = state_56480__$1;
(statearr_56544_56611[(1)] = (7));

} else {
var statearr_56545_56612 = state_56480__$1;
(statearr_56545_56612[(1)] = (8));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (14))){
var inst_56353 = (state_56480[(22)]);
var inst_56362 = cljs.core.first.call(null,inst_56353);
var inst_56363 = figwheel.client.file_reloading.eval_body.call(null,inst_56362,opts);
var inst_56364 = cljs.core.next.call(null,inst_56353);
var inst_56340 = inst_56364;
var inst_56341 = null;
var inst_56342 = (0);
var inst_56343 = (0);
var state_56480__$1 = (function (){var statearr_56546 = state_56480;
(statearr_56546[(7)] = inst_56340);

(statearr_56546[(8)] = inst_56341);

(statearr_56546[(9)] = inst_56343);

(statearr_56546[(32)] = inst_56363);

(statearr_56546[(10)] = inst_56342);

return statearr_56546;
})();
var statearr_56547_56613 = state_56480__$1;
(statearr_56547_56613[(2)] = null);

(statearr_56547_56613[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (45))){
var state_56480__$1 = state_56480;
var statearr_56548_56614 = state_56480__$1;
(statearr_56548_56614[(2)] = null);

(statearr_56548_56614[(1)] = (46));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (26))){
var inst_56396 = (state_56480[(23)]);
var inst_56393 = (state_56480[(19)]);
var inst_56397 = (state_56480[(24)]);
var inst_56399 = (state_56480[(26)]);
var inst_56401 = (state_56480[(25)]);
var inst_56416 = figwheel.client.utils.log.call(null,new cljs.core.Keyword(null,"debug","debug",-1608172596),"Figwheel: loaded these files");
var inst_56418 = (function (){var all_files = inst_56393;
var res_SINGLEQUOTE_ = inst_56396;
var res = inst_56397;
var files_not_loaded = inst_56399;
var dependencies_that_loaded = inst_56401;
return ((function (all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_56396,inst_56393,inst_56397,inst_56399,inst_56401,inst_56416,state_val_56481,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (p__56417){
var map__56549 = p__56417;
var map__56549__$1 = ((((!((map__56549 == null)))?((((map__56549.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56549.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56549):map__56549);
var namespace = cljs.core.get.call(null,map__56549__$1,new cljs.core.Keyword(null,"namespace","namespace",-377510372));
var file = cljs.core.get.call(null,map__56549__$1,new cljs.core.Keyword(null,"file","file",-1269645878));
if(cljs.core.truth_(namespace)){
return figwheel.client.file_reloading.name__GT_path.call(null,cljs.core.name.call(null,namespace));
} else {
return file;
}
});
;})(all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_56396,inst_56393,inst_56397,inst_56399,inst_56401,inst_56416,state_val_56481,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var inst_56419 = cljs.core.map.call(null,inst_56418,inst_56397);
var inst_56420 = cljs.core.pr_str.call(null,inst_56419);
var inst_56421 = figwheel.client.utils.log.call(null,inst_56420);
var inst_56422 = (function (){var all_files = inst_56393;
var res_SINGLEQUOTE_ = inst_56396;
var res = inst_56397;
var files_not_loaded = inst_56399;
var dependencies_that_loaded = inst_56401;
return ((function (all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_56396,inst_56393,inst_56397,inst_56399,inst_56401,inst_56416,inst_56418,inst_56419,inst_56420,inst_56421,state_val_56481,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents){
return (function (){
figwheel.client.file_reloading.on_jsload_custom_event.call(null,res);

return cljs.core.apply.call(null,on_jsload,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [res], null));
});
;})(all_files,res_SINGLEQUOTE_,res,files_not_loaded,dependencies_that_loaded,inst_56396,inst_56393,inst_56397,inst_56399,inst_56401,inst_56416,inst_56418,inst_56419,inst_56420,inst_56421,state_val_56481,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var inst_56423 = setTimeout(inst_56422,(10));
var state_56480__$1 = (function (){var statearr_56551 = state_56480;
(statearr_56551[(33)] = inst_56416);

(statearr_56551[(34)] = inst_56421);

return statearr_56551;
})();
var statearr_56552_56615 = state_56480__$1;
(statearr_56552_56615[(2)] = inst_56423);

(statearr_56552_56615[(1)] = (28));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (16))){
var state_56480__$1 = state_56480;
var statearr_56553_56616 = state_56480__$1;
(statearr_56553_56616[(2)] = reload_dependents);

(statearr_56553_56616[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (38))){
var inst_56433 = (state_56480[(16)]);
var inst_56451 = cljs.core.apply.call(null,cljs.core.hash_map,inst_56433);
var state_56480__$1 = state_56480;
var statearr_56554_56617 = state_56480__$1;
(statearr_56554_56617[(2)] = inst_56451);

(statearr_56554_56617[(1)] = (40));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (30))){
var state_56480__$1 = state_56480;
var statearr_56555_56618 = state_56480__$1;
(statearr_56555_56618[(2)] = null);

(statearr_56555_56618[(1)] = (31));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (10))){
var inst_56353 = (state_56480[(22)]);
var inst_56355 = cljs.core.chunked_seq_QMARK_.call(null,inst_56353);
var state_56480__$1 = state_56480;
if(inst_56355){
var statearr_56556_56619 = state_56480__$1;
(statearr_56556_56619[(1)] = (13));

} else {
var statearr_56557_56620 = state_56480__$1;
(statearr_56557_56620[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (18))){
var inst_56387 = (state_56480[(2)]);
var state_56480__$1 = state_56480;
if(cljs.core.truth_(inst_56387)){
var statearr_56558_56621 = state_56480__$1;
(statearr_56558_56621[(1)] = (19));

} else {
var statearr_56559_56622 = state_56480__$1;
(statearr_56559_56622[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (42))){
var state_56480__$1 = state_56480;
var statearr_56560_56623 = state_56480__$1;
(statearr_56560_56623[(2)] = null);

(statearr_56560_56623[(1)] = (43));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (37))){
var inst_56446 = (state_56480[(2)]);
var state_56480__$1 = state_56480;
var statearr_56561_56624 = state_56480__$1;
(statearr_56561_56624[(2)] = inst_56446);

(statearr_56561_56624[(1)] = (34));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_56481 === (8))){
var inst_56340 = (state_56480[(7)]);
var inst_56353 = (state_56480[(22)]);
var inst_56353__$1 = cljs.core.seq.call(null,inst_56340);
var state_56480__$1 = (function (){var statearr_56562 = state_56480;
(statearr_56562[(22)] = inst_56353__$1);

return statearr_56562;
})();
if(inst_56353__$1){
var statearr_56563_56625 = state_56480__$1;
(statearr_56563_56625[(1)] = (10));

} else {
var statearr_56564_56626 = state_56480__$1;
(statearr_56564_56626[(1)] = (11));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents))
;
return ((function (switch__53950__auto__,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents){
return (function() {
var figwheel$client$file_reloading$reload_js_files_$_state_machine__53951__auto__ = null;
var figwheel$client$file_reloading$reload_js_files_$_state_machine__53951__auto____0 = (function (){
var statearr_56565 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_56565[(0)] = figwheel$client$file_reloading$reload_js_files_$_state_machine__53951__auto__);

(statearr_56565[(1)] = (1));

return statearr_56565;
});
var figwheel$client$file_reloading$reload_js_files_$_state_machine__53951__auto____1 = (function (state_56480){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_56480);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e56566){if((e56566 instanceof Object)){
var ex__53954__auto__ = e56566;
var statearr_56567_56627 = state_56480;
(statearr_56567_56627[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_56480);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e56566;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__56628 = state_56480;
state_56480 = G__56628;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
figwheel$client$file_reloading$reload_js_files_$_state_machine__53951__auto__ = function(state_56480){
switch(arguments.length){
case 0:
return figwheel$client$file_reloading$reload_js_files_$_state_machine__53951__auto____0.call(this);
case 1:
return figwheel$client$file_reloading$reload_js_files_$_state_machine__53951__auto____1.call(this,state_56480);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
figwheel$client$file_reloading$reload_js_files_$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = figwheel$client$file_reloading$reload_js_files_$_state_machine__53951__auto____0;
figwheel$client$file_reloading$reload_js_files_$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = figwheel$client$file_reloading$reload_js_files_$_state_machine__53951__auto____1;
return figwheel$client$file_reloading$reload_js_files_$_state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents))
})();
var state__54042__auto__ = (function (){var statearr_56568 = f__54041__auto__.call(null);
(statearr_56568[(6)] = c__54040__auto__);

return statearr_56568;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto__,map__56325,map__56325__$1,opts,before_jsload,on_jsload,reload_dependents,map__56326,map__56326__$1,msg,files,figwheel_meta,recompile_dependents))
);

return c__54040__auto__;
});
figwheel.client.file_reloading.current_links = (function figwheel$client$file_reloading$current_links(){
return Array.prototype.slice.call(document.getElementsByTagName("link"));
});
figwheel.client.file_reloading.truncate_url = (function figwheel$client$file_reloading$truncate_url(url){
return clojure.string.replace_first.call(null,clojure.string.replace_first.call(null,clojure.string.replace_first.call(null,clojure.string.replace_first.call(null,cljs.core.first.call(null,clojure.string.split.call(null,url,/\?/)),[cljs.core.str.cljs$core$IFn$_invoke$arity$1(location.protocol),"//"].join(''),""),".*://",""),/^\/\//,""),/[^\\/]*/,"");
});
figwheel.client.file_reloading.matches_file_QMARK_ = (function figwheel$client$file_reloading$matches_file_QMARK_(p__56631,link){
var map__56632 = p__56631;
var map__56632__$1 = ((((!((map__56632 == null)))?((((map__56632.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56632.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56632):map__56632);
var file = cljs.core.get.call(null,map__56632__$1,new cljs.core.Keyword(null,"file","file",-1269645878));
var temp__5290__auto__ = link.href;
if(cljs.core.truth_(temp__5290__auto__)){
var link_href = temp__5290__auto__;
var match = clojure.string.join.call(null,"/",cljs.core.take_while.call(null,cljs.core.identity,cljs.core.map.call(null,((function (link_href,temp__5290__auto__,map__56632,map__56632__$1,file){
return (function (p1__56629_SHARP_,p2__56630_SHARP_){
if(cljs.core._EQ_.call(null,p1__56629_SHARP_,p2__56630_SHARP_)){
return p1__56629_SHARP_;
} else {
return false;
}
});})(link_href,temp__5290__auto__,map__56632,map__56632__$1,file))
,cljs.core.reverse.call(null,clojure.string.split.call(null,file,"/")),cljs.core.reverse.call(null,clojure.string.split.call(null,figwheel.client.file_reloading.truncate_url.call(null,link_href),"/")))));
var match_length = cljs.core.count.call(null,match);
var file_name_length = cljs.core.count.call(null,cljs.core.last.call(null,clojure.string.split.call(null,file,"/")));
if((match_length >= file_name_length)){
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"link","link",-1769163468),link,new cljs.core.Keyword(null,"link-href","link-href",-250644450),link_href,new cljs.core.Keyword(null,"match-length","match-length",1101537310),match_length,new cljs.core.Keyword(null,"current-url-length","current-url-length",380404083),cljs.core.count.call(null,figwheel.client.file_reloading.truncate_url.call(null,link_href))], null);
} else {
return null;
}
} else {
return null;
}
});
figwheel.client.file_reloading.get_correct_link = (function figwheel$client$file_reloading$get_correct_link(f_data){
var temp__5290__auto__ = cljs.core.first.call(null,cljs.core.sort_by.call(null,(function (p__56635){
var map__56636 = p__56635;
var map__56636__$1 = ((((!((map__56636 == null)))?((((map__56636.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56636.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56636):map__56636);
var match_length = cljs.core.get.call(null,map__56636__$1,new cljs.core.Keyword(null,"match-length","match-length",1101537310));
var current_url_length = cljs.core.get.call(null,map__56636__$1,new cljs.core.Keyword(null,"current-url-length","current-url-length",380404083));
return (current_url_length - match_length);
}),cljs.core.keep.call(null,(function (p1__56634_SHARP_){
return figwheel.client.file_reloading.matches_file_QMARK_.call(null,f_data,p1__56634_SHARP_);
}),figwheel.client.file_reloading.current_links.call(null))));
if(cljs.core.truth_(temp__5290__auto__)){
var res = temp__5290__auto__;
return new cljs.core.Keyword(null,"link","link",-1769163468).cljs$core$IFn$_invoke$arity$1(res);
} else {
return null;
}
});
figwheel.client.file_reloading.clone_link = (function figwheel$client$file_reloading$clone_link(link,url){
var clone = document.createElement("link");
clone.rel = "stylesheet";

clone.media = link.media;

clone.disabled = link.disabled;

clone.href = figwheel.client.file_reloading.add_cache_buster.call(null,url);

return clone;
});
figwheel.client.file_reloading.create_link = (function figwheel$client$file_reloading$create_link(url){
var link = document.createElement("link");
link.rel = "stylesheet";

link.href = figwheel.client.file_reloading.add_cache_buster.call(null,url);

return link;
});
figwheel.client.file_reloading.distinctify = (function figwheel$client$file_reloading$distinctify(key,seqq){
return cljs.core.vals.call(null,cljs.core.reduce.call(null,(function (p1__56638_SHARP_,p2__56639_SHARP_){
return cljs.core.assoc.call(null,p1__56638_SHARP_,cljs.core.get.call(null,p2__56639_SHARP_,key),p2__56639_SHARP_);
}),cljs.core.PersistentArrayMap.EMPTY,seqq));
});
figwheel.client.file_reloading.add_link_to_document = (function figwheel$client$file_reloading$add_link_to_document(orig_link,klone,finished_fn){
var parent = orig_link.parentNode;
if(cljs.core._EQ_.call(null,orig_link,parent.lastChild)){
parent.appendChild(klone);
} else {
parent.insertBefore(klone,orig_link.nextSibling);
}

return setTimeout(((function (parent){
return (function (){
parent.removeChild(orig_link);

return finished_fn.call(null);
});})(parent))
,(300));
});
if(typeof figwheel.client.file_reloading.reload_css_deferred_chain !== 'undefined'){
} else {
figwheel.client.file_reloading.reload_css_deferred_chain = cljs.core.atom.call(null,goog.async.Deferred.succeed());
}
figwheel.client.file_reloading.reload_css_file = (function figwheel$client$file_reloading$reload_css_file(f_data,fin){
var temp__5288__auto__ = figwheel.client.file_reloading.get_correct_link.call(null,f_data);
if(cljs.core.truth_(temp__5288__auto__)){
var link = temp__5288__auto__;
return figwheel.client.file_reloading.add_link_to_document.call(null,link,figwheel.client.file_reloading.clone_link.call(null,link,link.href),((function (link,temp__5288__auto__){
return (function (){
return fin.call(null,cljs.core.assoc.call(null,f_data,new cljs.core.Keyword(null,"loaded","loaded",-1246482293),true));
});})(link,temp__5288__auto__))
);
} else {
return fin.call(null,f_data);
}
});
figwheel.client.file_reloading.reload_css_files_STAR_ = (function figwheel$client$file_reloading$reload_css_files_STAR_(deferred,f_datas,on_cssload){
return figwheel.client.utils.liftContD.call(null,figwheel.client.utils.mapConcatD.call(null,deferred,figwheel.client.file_reloading.reload_css_file,f_datas),(function (f_datas_SINGLEQUOTE_,fin){
var loaded_f_datas_56640 = cljs.core.filter.call(null,new cljs.core.Keyword(null,"loaded","loaded",-1246482293),f_datas_SINGLEQUOTE_);
figwheel.client.file_reloading.on_cssload_custom_event.call(null,loaded_f_datas_56640);

if(cljs.core.fn_QMARK_.call(null,on_cssload)){
on_cssload.call(null,loaded_f_datas_56640);
} else {
}

return fin.call(null);
}));
});
figwheel.client.file_reloading.reload_css_files = (function figwheel$client$file_reloading$reload_css_files(p__56641,p__56642){
var map__56643 = p__56641;
var map__56643__$1 = ((((!((map__56643 == null)))?((((map__56643.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56643.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56643):map__56643);
var on_cssload = cljs.core.get.call(null,map__56643__$1,new cljs.core.Keyword(null,"on-cssload","on-cssload",1825432318));
var map__56644 = p__56642;
var map__56644__$1 = ((((!((map__56644 == null)))?((((map__56644.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56644.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56644):map__56644);
var files_msg = map__56644__$1;
var files = cljs.core.get.call(null,map__56644__$1,new cljs.core.Keyword(null,"files","files",-472457450));
if(cljs.core.truth_(figwheel.client.utils.html_env_QMARK_.call(null))){
var temp__5290__auto__ = cljs.core.not_empty.call(null,figwheel.client.file_reloading.distinctify.call(null,new cljs.core.Keyword(null,"file","file",-1269645878),files));
if(cljs.core.truth_(temp__5290__auto__)){
var f_datas = temp__5290__auto__;
return cljs.core.swap_BANG_.call(null,figwheel.client.file_reloading.reload_css_deferred_chain,figwheel.client.file_reloading.reload_css_files_STAR_,f_datas,on_cssload);
} else {
return null;
}
} else {
return null;
}
});

//# sourceMappingURL=file_reloading.js.map?rel=1510137299265
