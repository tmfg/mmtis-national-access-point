// Compiled by ClojureScript 1.9.908 {}
goog.provide('specql.impl.registry');
goog.require('cljs.core');
goog.require('cljs.spec.alpha');
goog.require('clojure.string');
if(typeof specql.impl.registry.table_info_registry !== 'undefined'){
} else {
specql.impl.registry.table_info_registry = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
}
/**
 * Returns a namepaced type keyword for the given database type name.
 *   The database type can be a user define table or type (registered with define-tables)
 *   or a database specified type name (like 'text' or 'point').
 * 
 *   If no type is found for the given name, returns nil.
 */
specql.impl.registry.type_keyword_by_name = (function specql$impl$registry$type_keyword_by_name(var_args){
var G__37311 = arguments.length;
switch (G__37311) {
case 1:
return specql.impl.registry.type_keyword_by_name.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return specql.impl.registry.type_keyword_by_name.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

specql.impl.registry.type_keyword_by_name.cljs$core$IFn$_invoke$arity$1 = (function (type_name){
return specql.impl.registry.type_keyword_by_name.call(null,cljs.core.deref.call(null,specql.impl.registry.table_info_registry),type_name);
});

specql.impl.registry.type_keyword_by_name.cljs$core$IFn$_invoke$arity$2 = (function (table_info_registry,type_name){
var type_name__$1 = ((clojure.string.starts_with_QMARK_.call(null,type_name,"_"))?cljs.core.subs.call(null,type_name,(1)):type_name);
var or__30175__auto__ = cljs.core.some.call(null,((function (type_name__$1){
return (function (p__37318){
var vec__37319 = p__37318;
var kw = cljs.core.nth.call(null,vec__37319,(0),null);
var map__37322 = cljs.core.nth.call(null,vec__37319,(1),null);
var map__37322__$1 = ((((!((map__37322 == null)))?((((map__37322.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37322.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37322):map__37322);
var name = cljs.core.get.call(null,map__37322__$1,new cljs.core.Keyword(null,"name","name",1843675177));
if(cljs.core._EQ_.call(null,name,type_name__$1)){
return kw;
} else {
return null;
}
});})(type_name__$1))
,table_info_registry);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
var dt = cljs.core.keyword.call(null,"specql.data-types",type_name__$1);
if(cljs.core.truth_(cljs.spec.alpha.get_spec.call(null,dt))){
return dt;
} else {
return null;
}
}
});

specql.impl.registry.type_keyword_by_name.cljs$lang$maxFixedArity = 2;

specql.impl.registry.type_by_name = (function specql$impl$registry$type_by_name(var_args){
var G__37326 = arguments.length;
switch (G__37326) {
case 1:
return specql.impl.registry.type_by_name.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return specql.impl.registry.type_by_name.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

specql.impl.registry.type_by_name.cljs$core$IFn$_invoke$arity$1 = (function (type_name){
return specql.impl.registry.type_by_name.call(null,cljs.core.deref.call(null,specql.impl.registry.table_info_registry),type_name);
});

specql.impl.registry.type_by_name.cljs$core$IFn$_invoke$arity$2 = (function (table_info_registry,type_name){
return cljs.core.get.call(null,table_info_registry,specql.impl.registry.type_keyword_by_name.call(null,table_info_registry,type_name));
});

specql.impl.registry.type_by_name.cljs$lang$maxFixedArity = 2;

/**
 * Find user defined composite type from registry by name.
 */
specql.impl.registry.composite_type = (function specql$impl$registry$composite_type(var_args){
var G__37329 = arguments.length;
switch (G__37329) {
case 1:
return specql.impl.registry.composite_type.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return specql.impl.registry.composite_type.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

specql.impl.registry.composite_type.cljs$core$IFn$_invoke$arity$1 = (function (name){
return specql.impl.registry.composite_type.call(null,cljs.core.deref.call(null,specql.impl.registry.table_info_registry),name);
});

specql.impl.registry.composite_type.cljs$core$IFn$_invoke$arity$2 = (function (table_info_registry,name){
return cljs.core.some.call(null,(function (p__37330){
var vec__37331 = p__37330;
var key = cljs.core.nth.call(null,vec__37331,(0),null);
var map__37334 = cljs.core.nth.call(null,vec__37331,(1),null);
var map__37334__$1 = ((((!((map__37334 == null)))?((((map__37334.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37334.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37334):map__37334);
var n = cljs.core.get.call(null,map__37334__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var t = cljs.core.get.call(null,map__37334__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var and__30163__auto__ = cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"composite","composite",-257118970),t);
if(and__30163__auto__){
var and__30163__auto____$1 = cljs.core._EQ_.call(null,name,n);
if(and__30163__auto____$1){
return key;
} else {
return and__30163__auto____$1;
}
} else {
return and__30163__auto__;
}
}),table_info_registry);
});

specql.impl.registry.composite_type.cljs$lang$maxFixedArity = 2;

/**
 * Find an enum type from registry by name.
 */
specql.impl.registry.enum_type = (function specql$impl$registry$enum_type(var_args){
var G__37338 = arguments.length;
switch (G__37338) {
case 1:
return specql.impl.registry.enum_type.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return specql.impl.registry.enum_type.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

specql.impl.registry.enum_type.cljs$core$IFn$_invoke$arity$1 = (function (name){
return specql.impl.registry.enum_type.call(null,cljs.core.deref.call(null,specql.impl.registry.table_info_registry),name);
});

specql.impl.registry.enum_type.cljs$core$IFn$_invoke$arity$2 = (function (table_info_registry,name){
return cljs.core.some.call(null,(function (p__37339){
var vec__37340 = p__37339;
var key = cljs.core.nth.call(null,vec__37340,(0),null);
var map__37343 = cljs.core.nth.call(null,vec__37340,(1),null);
var map__37343__$1 = ((((!((map__37343 == null)))?((((map__37343.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37343.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37343):map__37343);
var n = cljs.core.get.call(null,map__37343__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var t = cljs.core.get.call(null,map__37343__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var and__30163__auto__ = cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"enum","enum",1679018432),t);
if(and__30163__auto__){
var and__30163__auto____$1 = cljs.core._EQ_.call(null,name,n);
if(and__30163__auto____$1){
return key;
} else {
return and__30163__auto____$1;
}
} else {
return and__30163__auto__;
}
}),table_info_registry);
});

specql.impl.registry.enum_type.cljs$lang$maxFixedArity = 2;

/**
 * Add an array element type, if the given column is an array
 */
specql.impl.registry.array_element_type = (function specql$impl$registry$array_element_type(table_info_registry,column){
if(!(cljs.core._EQ_.call(null,"A",new cljs.core.Keyword(null,"category","category",-593092832).cljs$core$IFn$_invoke$arity$1(column)))){
return column;
} else {
var element_type_name = cljs.core.subs.call(null,new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(column),(1));
return cljs.core.assoc.call(null,column,new cljs.core.Keyword(null,"element-type","element-type",-1609504232),(function (){var or__30175__auto__ = specql.impl.registry.composite_type.call(null,table_info_registry,element_type_name);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
var or__30175__auto____$1 = specql.impl.registry.enum_type.call(null,table_info_registry,element_type_name);
if(cljs.core.truth_(or__30175__auto____$1)){
return or__30175__auto____$1;
} else {
return cljs.core.keyword.call(null,"specql.data-types",element_type_name);
}
}
})());
}
});
specql.impl.registry.remap_columns = (function specql$impl$registry$remap_columns(columns,ns_name,column_map,transform_column_name){
return cljs.core.reduce_kv.call(null,(function (columns__$1,name,column){
return cljs.core.assoc.call(null,columns__$1,(function (){var or__30175__auto__ = cljs.core.get.call(null,column_map,name);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
var or__30175__auto____$1 = (cljs.core.truth_(transform_column_name)?transform_column_name.call(null,ns_name,name):null);
if(cljs.core.truth_(or__30175__auto____$1)){
return or__30175__auto____$1;
} else {
return cljs.core.keyword.call(null,ns_name,name);
}
}
})(),column);
}),cljs.core.PersistentArrayMap.EMPTY,columns);
});
/**
 * Add :specql.transform/transform to the column definition
 */
specql.impl.registry.transformed = (function specql$impl$registry$transformed(columns,column_options_map){
return cljs.core.reduce_kv.call(null,(function (columns__$1,name,column){
return cljs.core.assoc.call(null,columns__$1,name,cljs.core.merge.call(null,column,cljs.core.select_keys.call(null,cljs.core.get.call(null,column_options_map,name),new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("specql.transform","transform","specql.transform/transform",-69443696),null], null), null))));
}),cljs.core.PersistentArrayMap.EMPTY,columns);
});
specql.impl.registry.wrap_column_name_check = (function specql$impl$registry$wrap_column_name_check(transform_column_name){
if(cljs.core.truth_(transform_column_name)){
return (function (ns,name){
var column_name = transform_column_name.call(null,ns,name);
if(cljs.core.qualified_keyword_QMARK_.call(null,column_name)){
} else {
throw (new Error(["Assert failed: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(["Column names must be ns qualified keywords, transform-column-name fn returned: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(column_name)].join('')),"\n","(qualified-keyword? column-name)"].join('')));
}

return column_name;
});
} else {
return null;
}
});
specql.impl.registry.process_columns = (function specql$impl$registry$process_columns(p__37346,ns_name,column_options_map,transform_column_name){
var map__37347 = p__37346;
var map__37347__$1 = ((((!((map__37347 == null)))?((((map__37347.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37347.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37347):map__37347);
var table_info = map__37347__$1;
var columns = cljs.core.get.call(null,map__37347__$1,new cljs.core.Keyword(null,"columns","columns",1998437288));
return cljs.core.update.call(null,cljs.core.update.call(null,table_info,new cljs.core.Keyword(null,"columns","columns",1998437288),specql.impl.registry.remap_columns,ns_name,column_options_map,specql.impl.registry.wrap_column_name_check.call(null,transform_column_name)),new cljs.core.Keyword(null,"columns","columns",1998437288),specql.impl.registry.transformed,column_options_map);
});
specql.impl.registry.required_insert_QMARK_ = (function specql$impl$registry$required_insert_QMARK_(p__37349){
var map__37350 = p__37349;
var map__37350__$1 = ((((!((map__37350 == null)))?((((map__37350.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__37350.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__37350):map__37350);
var not_null_QMARK_ = cljs.core.get.call(null,map__37350__$1,new cljs.core.Keyword(null,"not-null?","not-null?",-2042435516));
var has_default_QMARK_ = cljs.core.get.call(null,map__37350__$1,new cljs.core.Keyword(null,"has-default?","has-default?",-1287421436));
var and__30163__auto__ = not_null_QMARK_;
if(cljs.core.truth_(and__30163__auto__)){
return cljs.core.not.call(null,has_default_QMARK_);
} else {
return and__30163__auto__;
}
});
specql.impl.registry.columns = (function specql$impl$registry$columns(table_kw){
var temp__5290__auto__ = new cljs.core.Keyword(null,"columns","columns",1998437288).cljs$core$IFn$_invoke$arity$1(table_kw.call(null,cljs.core.deref.call(null,specql.impl.registry.table_info_registry)));
if(cljs.core.truth_(temp__5290__auto__)){
var cols = temp__5290__auto__;
return cljs.core.set.call(null,cljs.core.keys.call(null,cols));
} else {
return null;
}
});
specql.impl.registry.tables = (function specql$impl$registry$tables(){
return cljs.core.set.call(null,cljs.core.keys.call(null,cljs.core.deref.call(null,specql.impl.registry.table_info_registry)));
});

//# sourceMappingURL=registry.js.map?rel=1510137274669
