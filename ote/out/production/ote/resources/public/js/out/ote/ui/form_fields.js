// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.ui.form_fields');
goog.require('cljs.core');
goog.require('reagent.core');
goog.require('cljs_react_material_ui.reagent');
goog.require('clojure.string');
goog.require('ote.localization');
goog.require('cljs_react_material_ui.icons');
goog.require('stylefy.core');
goog.require('ote.style.form_fields');
goog.require('ote.style.base');
goog.require('ote.ui.validation');
goog.require('ote.time');
goog.require('ote.ui.buttons');
ote.ui.form_fields.read_only_atom = (function ote$ui$form_fields$read_only_atom(value){
return reagent.core.wrap.call(null,value,(function (){
throw (new Error(["Assert failed: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(["Can't write to a read-only atom: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,value))].join('')),"\n","false"].join('')));

}));
});
if(typeof ote.ui.form_fields.field !== 'undefined'){
} else {
/**
 * Create an editable form field UI component. Dispatches on `:type` keyword.
 *   A field must always have an `:update!` callback the component calls to update a new value.
 */
ote.ui.form_fields.field = (function (){var method_table__31228__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var prefer_table__31229__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var method_cache__31230__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var cached_hierarchy__31231__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var hierarchy__31232__auto__ = cljs.core.get.call(null,cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"hierarchy","hierarchy",-1053470341),cljs.core.get_global_hierarchy.call(null));
return (new cljs.core.MultiFn(cljs.core.symbol.call(null,"ote.ui.form-fields","field"),((function (method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__,hierarchy__31232__auto__){
return (function (t,_){
return new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(t);
});})(method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__,hierarchy__31232__auto__))
,new cljs.core.Keyword(null,"default","default",-1987822328),hierarchy__31232__auto__,method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__));
})();
}
if(typeof ote.ui.form_fields.show_value !== 'undefined'){
} else {
/**
 * Create a read-only display for a value. Dispatches on `:type` keyword.
 *   This is not meant to be a 'disabled' input field, but for showing a readable value.
 *   Default implementation just converts input value to string.
 */
ote.ui.form_fields.show_value = (function (){var method_table__31228__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var prefer_table__31229__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var method_cache__31230__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var cached_hierarchy__31231__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var hierarchy__31232__auto__ = cljs.core.get.call(null,cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"hierarchy","hierarchy",-1053470341),cljs.core.get_global_hierarchy.call(null));
return (new cljs.core.MultiFn(cljs.core.symbol.call(null,"ote.ui.form-fields","show-value"),((function (method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__,hierarchy__31232__auto__){
return (function (t,_){
return new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(t);
});})(method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__,hierarchy__31232__auto__))
,new cljs.core.Keyword(null,"default","default",-1987822328),hierarchy__31232__auto__,method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__));
})();
}
cljs.core._add_method.call(null,ote.ui.form_fields.show_value,new cljs.core.Keyword(null,"default","default",-1987822328),(function (_,data){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span","span",1394872991),[cljs.core.str.cljs$core$IFn$_invoke$arity$1(data)].join('')], null);
}));
cljs.core._add_method.call(null,ote.ui.form_fields.show_value,new cljs.core.Keyword(null,"component","component",1555936782),(function (skeema,data){
var komponentti = new cljs.core.Keyword(null,"component","component",1555936782).cljs$core$IFn$_invoke$arity$1(skeema);
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [komponentti,data], null);
}));
ote.ui.form_fields.placeholder = (function ote$ui$form_fields$placeholder(p__50990,data){
var map__50991 = p__50990;
var map__50991__$1 = ((((!((map__50991 == null)))?((((map__50991.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__50991.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__50991):map__50991);
var field = map__50991__$1;
var placeholder = cljs.core.get.call(null,map__50991__$1,new cljs.core.Keyword(null,"placeholder","placeholder",-104873083));
var placeholder_fn = cljs.core.get.call(null,map__50991__$1,new cljs.core.Keyword(null,"placeholder-fn","placeholder-fn",1209625186));
var row = cljs.core.get.call(null,map__50991__$1,new cljs.core.Keyword(null,"row","row",-570139521));
var or__30175__auto__ = placeholder;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
var and__30163__auto__ = placeholder_fn;
if(cljs.core.truth_(and__30163__auto__)){
return placeholder_fn.call(null,row);
} else {
return and__30163__auto__;
}
}
});
cljs.core._add_method.call(null,ote.ui.form_fields.field,new cljs.core.Keyword(null,"string","string",-1989541586),(function (p__50995,data){
var map__50996 = p__50995;
var map__50996__$1 = ((((!((map__50996 == null)))?((((map__50996.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__50996.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__50996):map__50996);
var field = map__50996__$1;
var table_QMARK_ = cljs.core.get.call(null,map__50996__$1,new cljs.core.Keyword(null,"table?","table?",-1064705406));
var max_length = cljs.core.get.call(null,map__50996__$1,new cljs.core.Keyword(null,"max-length","max-length",-254826109));
var min_length = cljs.core.get.call(null,map__50996__$1,new cljs.core.Keyword(null,"min-length","min-length",-325792315));
var on_focus = cljs.core.get.call(null,map__50996__$1,new cljs.core.Keyword(null,"on-focus","on-focus",-13737624));
var name = cljs.core.get.call(null,map__50996__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var warning = cljs.core.get.call(null,map__50996__$1,new cljs.core.Keyword(null,"warning","warning",-1685650671));
var label = cljs.core.get.call(null,map__50996__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var update_BANG_ = cljs.core.get.call(null,map__50996__$1,new cljs.core.Keyword(null,"update!","update!",-1453508586));
var focus = cljs.core.get.call(null,map__50996__$1,new cljs.core.Keyword(null,"focus","focus",234677911));
var error = cljs.core.get.call(null,map__50996__$1,new cljs.core.Keyword(null,"error","error",-978969032));
var regex = cljs.core.get.call(null,map__50996__$1,new cljs.core.Keyword(null,"regex","regex",939488856));
var form_QMARK_ = cljs.core.get.call(null,map__50996__$1,new cljs.core.Keyword(null,"form?","form?",-2055688328));
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.text_field,new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"floatingLabelText","floatingLabelText",-431719650),(cljs.core.truth_(table_QMARK_)?null:label),new cljs.core.Keyword(null,"hintText","hintText",-1810446561),ote.ui.form_fields.placeholder.call(null,field,data),new cljs.core.Keyword(null,"on-change","on-change",-732046149),((function (map__50996,map__50996__$1,field,table_QMARK_,max_length,min_length,on_focus,name,warning,label,update_BANG_,focus,error,regex,form_QMARK_){
return (function (p1__50994_SHARP_,p2__50993_SHARP_){
var v = p2__50993_SHARP_;
if(cljs.core.truth_(regex)){
if(cljs.core.truth_(cljs.core.re_matches.call(null,regex,v))){
return update_BANG_.call(null,v);
} else {
return null;
}
} else {
return update_BANG_.call(null,v);
}
});})(map__50996,map__50996__$1,field,table_QMARK_,max_length,min_length,on_focus,name,warning,label,update_BANG_,focus,error,regex,form_QMARK_))
,new cljs.core.Keyword(null,"value","value",305978217),(function (){var or__30175__auto__ = data;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"error-text","error-text",2021893718),(function (){var or__30175__auto__ = error;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
var or__30175__auto____$1 = warning;
if(cljs.core.truth_(or__30175__auto____$1)){
return or__30175__auto____$1;
} else {
return "";
}
}
})(),new cljs.core.Keyword(null,"error-style","error-style",1259113567),(cljs.core.truth_(error)?ote.style.base.error_element:ote.style.base.required_element)], null)], null);
}));
cljs.core._add_method.call(null,ote.ui.form_fields.field,new cljs.core.Keyword(null,"text-area","text-area",-1481158655),(function (p__51000,data){
var map__51001 = p__51000;
var map__51001__$1 = ((((!((map__51001 == null)))?((((map__51001.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51001.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51001):map__51001);
var field = map__51001__$1;
var update_BANG_ = cljs.core.get.call(null,map__51001__$1,new cljs.core.Keyword(null,"update!","update!",-1453508586));
var label = cljs.core.get.call(null,map__51001__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var name = cljs.core.get.call(null,map__51001__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var rows = cljs.core.get.call(null,map__51001__$1,new cljs.core.Keyword(null,"rows","rows",850049680));
var error = cljs.core.get.call(null,map__51001__$1,new cljs.core.Keyword(null,"error","error",-978969032));
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.text_field,new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"floatingLabelText","floatingLabelText",-431719650),label,new cljs.core.Keyword(null,"hintText","hintText",-1810446561),ote.ui.form_fields.placeholder.call(null,field,data),new cljs.core.Keyword(null,"on-change","on-change",-732046149),((function (map__51001,map__51001__$1,field,update_BANG_,label,name,rows,error){
return (function (p1__50999_SHARP_,p2__50998_SHARP_){
return update_BANG_.call(null,p2__50998_SHARP_);
});})(map__51001,map__51001__$1,field,update_BANG_,label,name,rows,error))
,new cljs.core.Keyword(null,"value","value",305978217),(function (){var or__30175__auto__ = data;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"multiLine","multiLine",-23992423),true,new cljs.core.Keyword(null,"rows","rows",850049680),rows,new cljs.core.Keyword(null,"error-text","error-text",2021893718),error], null)], null);
}));
ote.ui.form_fields.languages = new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["FI","SV","EN"], null);
cljs.core._add_method.call(null,ote.ui.form_fields.field,new cljs.core.Keyword(null,"localized-text","localized-text",191039942),(function (p__51006,data){
var map__51007 = p__51006;
var map__51007__$1 = ((((!((map__51007 == null)))?((((map__51007.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51007.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51007):map__51007);
var field = map__51007__$1;
var update_BANG_ = cljs.core.get.call(null,map__51007__$1,new cljs.core.Keyword(null,"update!","update!",-1453508586));
var table_QMARK_ = cljs.core.get.call(null,map__51007__$1,new cljs.core.Keyword(null,"table?","table?",-1064705406));
var label = cljs.core.get.call(null,map__51007__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var name = cljs.core.get.call(null,map__51007__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var rows = cljs.core.get.call(null,map__51007__$1,new cljs.core.Keyword(null,"rows","rows",850049680));
var rows_max = cljs.core.get.call(null,map__51007__$1,new cljs.core.Keyword(null,"rows-max","rows-max",-1902625472));
var error = cljs.core.get.call(null,map__51007__$1,new cljs.core.Keyword(null,"error","error",-978969032));
var with_let51009 = reagent.ratom.with_let_values.call(null,new cljs.core.Keyword(null,"with-let51009","with-let51009",1883906528));
var temp__5294__auto___51015 = reagent.ratom._STAR_ratom_context_STAR_;
if((temp__5294__auto___51015 == null)){
} else {
var c__32995__auto___51016 = temp__5294__auto___51015;
if((with_let51009.generation === c__32995__auto___51016.ratomGeneration)){
if(cljs.core.truth_(reagent.debug.has_console)){
(cljs.core.truth_(reagent.debug.tracking)?reagent.debug.track_console:console).error(["Warning: The same with-let is being used more ","than once in the same reactive context."].join(''));
} else {
}
} else {
}

with_let51009.generation = c__32995__auto___51016.ratomGeneration;
}


var init51010 = (with_let51009.length === (0));
var selected_language = ((init51010)?(with_let51009[(0)] = reagent.core.atom.call(null,cljs.core.first.call(null,ote.ui.form_fields.languages))):(with_let51009[(0)]));
var res__32996__auto__ = (function (){var data__$1 = (function (){var or__30175__auto__ = data;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return cljs.core.PersistentVector.EMPTY;
}
})();
var languages = (function (){var or__30175__auto__ = new cljs.core.Keyword(null,"languages","languages",1471910331).cljs$core$IFn$_invoke$arity$1(field);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return ote.ui.form_fields.languages;
}
})();
var language = cljs.core.deref.call(null,selected_language);
var language_data = cljs.core.some.call(null,((function (data__$1,languages,language,init51010,selected_language,with_let51009,map__51007,map__51007__$1,field,update_BANG_,table_QMARK_,label,name,rows,rows_max,error){
return (function (p1__51003_SHARP_){
if(cljs.core._EQ_.call(null,language,new cljs.core.Keyword("ote.db.transport-service","lang","ote.db.transport-service/lang",902970079).cljs$core$IFn$_invoke$arity$1(p1__51003_SHARP_))){
return p1__51003_SHARP_;
} else {
return null;
}
});})(data__$1,languages,language,init51010,selected_language,with_let51009,map__51007,map__51007__$1,field,update_BANG_,table_QMARK_,label,name,rows,rows_max,error))
,data__$1);
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"table","table",-564943036),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"tr","tr",-1424774646),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.text_field,new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"floatingLabelText","floatingLabelText",-431719650),(cljs.core.truth_(table_QMARK_)?null:label),new cljs.core.Keyword(null,"hintText","hintText",-1810446561),ote.ui.form_fields.placeholder.call(null,field,data__$1),new cljs.core.Keyword(null,"on-change","on-change",-732046149),((function (data__$1,languages,language,language_data,init51010,selected_language,with_let51009,map__51007,map__51007__$1,field,update_BANG_,table_QMARK_,label,name,rows,rows_max,error){
return (function (p1__51005_SHARP_,p2__51004_SHARP_){
var updated_language_data = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword("ote.db.transport-service","lang","ote.db.transport-service/lang",902970079),language,new cljs.core.Keyword("ote.db.transport-service","text","ote.db.transport-service/text",134073550),p2__51004_SHARP_], null);
return update_BANG_.call(null,(cljs.core.truth_(language_data)?cljs.core.mapv.call(null,((function (updated_language_data,data__$1,languages,language,language_data,init51010,selected_language,with_let51009,map__51007,map__51007__$1,field,update_BANG_,table_QMARK_,label,name,rows,rows_max,error){
return (function (lang){
if(cljs.core._EQ_.call(null,new cljs.core.Keyword("ote.db.transport-service","lang","ote.db.transport-service/lang",902970079).cljs$core$IFn$_invoke$arity$1(lang),language)){
return updated_language_data;
} else {
return lang;
}
});})(updated_language_data,data__$1,languages,language,language_data,init51010,selected_language,with_let51009,map__51007,map__51007__$1,field,update_BANG_,table_QMARK_,label,name,rows,rows_max,error))
,data__$1):cljs.core.conj.call(null,data__$1,updated_language_data)));
});})(data__$1,languages,language,language_data,init51010,selected_language,with_let51009,map__51007,map__51007__$1,field,update_BANG_,table_QMARK_,label,name,rows,rows_max,error))
,new cljs.core.Keyword(null,"value","value",305978217),(function (){var or__30175__auto__ = new cljs.core.Keyword("ote.db.transport-service","text","ote.db.transport-service/text",134073550).cljs$core$IFn$_invoke$arity$1(language_data);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"multiLine","multiLine",-23992423),true,new cljs.core.Keyword(null,"rows","rows",850049680),rows,new cljs.core.Keyword(null,"rows-max","rows-max",-1902625472),(function (){var or__30175__auto__ = rows_max;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return rows;
}
})(),new cljs.core.Keyword(null,"error-text","error-text",2021893718),error], null)], null)], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"tr","tr",-1424774646),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"td","td",1479933353),stylefy.core.use_style.call(null,ote.style.form_fields.localized_text_language_links),cljs.core.doall.call(null,(function (){var iter__31057__auto__ = ((function (data__$1,languages,language,language_data,init51010,selected_language,with_let51009,map__51007,map__51007__$1,field,update_BANG_,table_QMARK_,label,name,rows,rows_max,error){
return (function ote$ui$form_fields$iter__51011(s__51012){
return (new cljs.core.LazySeq(null,((function (data__$1,languages,language,language_data,init51010,selected_language,with_let51009,map__51007,map__51007__$1,field,update_BANG_,table_QMARK_,label,name,rows,rows_max,error){
return (function (){
var s__51012__$1 = s__51012;
while(true){
var temp__5290__auto__ = cljs.core.seq.call(null,s__51012__$1);
if(temp__5290__auto__){
var s__51012__$2 = temp__5290__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,s__51012__$2)){
var c__31055__auto__ = cljs.core.chunk_first.call(null,s__51012__$2);
var size__31056__auto__ = cljs.core.count.call(null,c__31055__auto__);
var b__51014 = cljs.core.chunk_buffer.call(null,size__31056__auto__);
if((function (){var i__51013 = (0);
while(true){
if((i__51013 < size__31056__auto__)){
var lang = cljs.core._nth.call(null,c__31055__auto__,i__51013);
cljs.core.chunk_append.call(null,b__51014,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a","a",-2123407586),cljs.core.merge.call(null,stylefy.core.use_style.call(null,((cljs.core._EQ_.call(null,lang,language))?ote.style.form_fields.localized_text_language_selected:ote.style.form_fields.localized_text_language)),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-click","on-click",1632826543),((function (i__51013,lang,c__31055__auto__,size__31056__auto__,b__51014,s__51012__$2,temp__5290__auto__,data__$1,languages,language,language_data,init51010,selected_language,with_let51009,map__51007,map__51007__$1,field,update_BANG_,table_QMARK_,label,name,rows,rows_max,error){
return (function (){
return cljs.core.reset_BANG_.call(null,selected_language,lang);
});})(i__51013,lang,c__31055__auto__,size__31056__auto__,b__51014,s__51012__$2,temp__5290__auto__,data__$1,languages,language,language_data,init51010,selected_language,with_let51009,map__51007,map__51007__$1,field,update_BANG_,table_QMARK_,label,name,rows,rows_max,error))
], null)),lang], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),lang], null)));

var G__51017 = (i__51013 + (1));
i__51013 = G__51017;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__51014),ote$ui$form_fields$iter__51011.call(null,cljs.core.chunk_rest.call(null,s__51012__$2)));
} else {
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__51014),null);
}
} else {
var lang = cljs.core.first.call(null,s__51012__$2);
return cljs.core.cons.call(null,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a","a",-2123407586),cljs.core.merge.call(null,stylefy.core.use_style.call(null,((cljs.core._EQ_.call(null,lang,language))?ote.style.form_fields.localized_text_language_selected:ote.style.form_fields.localized_text_language)),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-click","on-click",1632826543),((function (lang,s__51012__$2,temp__5290__auto__,data__$1,languages,language,language_data,init51010,selected_language,with_let51009,map__51007,map__51007__$1,field,update_BANG_,table_QMARK_,label,name,rows,rows_max,error){
return (function (){
return cljs.core.reset_BANG_.call(null,selected_language,lang);
});})(lang,s__51012__$2,temp__5290__auto__,data__$1,languages,language,language_data,init51010,selected_language,with_let51009,map__51007,map__51007__$1,field,update_BANG_,table_QMARK_,label,name,rows,rows_max,error))
], null)),lang], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),lang], null)),ote$ui$form_fields$iter__51011.call(null,cljs.core.rest.call(null,s__51012__$2)));
}
} else {
return null;
}
break;
}
});})(data__$1,languages,language,language_data,init51010,selected_language,with_let51009,map__51007,map__51007__$1,field,update_BANG_,table_QMARK_,label,name,rows,rows_max,error))
,null,null));
});})(data__$1,languages,language,language_data,init51010,selected_language,with_let51009,map__51007,map__51007__$1,field,update_BANG_,table_QMARK_,label,name,rows,rows_max,error))
;
return iter__31057__auto__.call(null,languages);
})())], null)], null)], null);
})();

return res__32996__auto__;
}));
cljs.core._add_method.call(null,ote.ui.form_fields.field,new cljs.core.Keyword(null,"selection","selection",975998651),(function (p__51020,data){
var map__51021 = p__51020;
var map__51021__$1 = ((((!((map__51021 == null)))?((((map__51021.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51021.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51021):map__51021);
var field = map__51021__$1;
var update_BANG_ = cljs.core.get.call(null,map__51021__$1,new cljs.core.Keyword(null,"update!","update!",-1453508586));
var label = cljs.core.get.call(null,map__51021__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var name = cljs.core.get.call(null,map__51021__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var style = cljs.core.get.call(null,map__51021__$1,new cljs.core.Keyword(null,"style","style",-496642736));
var show_option = cljs.core.get.call(null,map__51021__$1,new cljs.core.Keyword(null,"show-option","show-option",1962057502));
var options = cljs.core.get.call(null,map__51021__$1,new cljs.core.Keyword(null,"options","options",99638489));
var form_QMARK_ = cljs.core.get.call(null,map__51021__$1,new cljs.core.Keyword(null,"form?","form?",-2055688328));
var error = cljs.core.get.call(null,map__51021__$1,new cljs.core.Keyword(null,"error","error",-978969032));
var warning = cljs.core.get.call(null,map__51021__$1,new cljs.core.Keyword(null,"warning","warning",-1685650671));
var option_idx = cljs.core.zipmap.call(null,options,cljs.core.range.call(null));
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.select_field,new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"style","style",-496642736),style,new cljs.core.Keyword(null,"floating-label-text","floating-label-text",740415797),label,new cljs.core.Keyword(null,"value","value",305978217),option_idx.call(null,data),new cljs.core.Keyword(null,"on-change","on-change",-732046149),((function (option_idx,map__51021,map__51021__$1,field,update_BANG_,label,name,style,show_option,options,form_QMARK_,error,warning){
return (function (p1__51019_SHARP_,p2__51018_SHARP_){
return update_BANG_.call(null,cljs.core.nth.call(null,options,p2__51018_SHARP_));
});})(option_idx,map__51021,map__51021__$1,field,update_BANG_,label,name,style,show_option,options,form_QMARK_,error,warning))
,new cljs.core.Keyword(null,"error-text","error-text",2021893718),(function (){var or__30175__auto__ = error;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
var or__30175__auto____$1 = warning;
if(cljs.core.truth_(or__30175__auto____$1)){
return or__30175__auto____$1;
} else {
return "";
}
}
})(),new cljs.core.Keyword(null,"error-style","error-style",1259113567),(cljs.core.truth_(error)?ote.style.base.error_element:ote.style.base.required_element)], null),cljs.core.doall.call(null,cljs.core.map_indexed.call(null,((function (option_idx,map__51021,map__51021__$1,field,update_BANG_,label,name,style,show_option,options,form_QMARK_,error,warning){
return (function (i,option){
return cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.menu_item,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"value","value",305978217),i,new cljs.core.Keyword(null,"primary-text","primary-text",146474209),show_option.call(null,option)], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),i], null));
});})(option_idx,map__51021,map__51021__$1,field,update_BANG_,label,name,style,show_option,options,form_QMARK_,error,warning))
,options))], null);
}));
cljs.core._add_method.call(null,ote.ui.form_fields.field,new cljs.core.Keyword(null,"multiselect-selection","multiselect-selection",-472179423),(function (p__51023,data){
var map__51024 = p__51023;
var map__51024__$1 = ((((!((map__51024 == null)))?((((map__51024.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51024.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51024):map__51024);
var field = map__51024__$1;
var update_BANG_ = cljs.core.get.call(null,map__51024__$1,new cljs.core.Keyword(null,"update!","update!",-1453508586));
var label = cljs.core.get.call(null,map__51024__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var name = cljs.core.get.call(null,map__51024__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var style = cljs.core.get.call(null,map__51024__$1,new cljs.core.Keyword(null,"style","style",-496642736));
var show_option = cljs.core.get.call(null,map__51024__$1,new cljs.core.Keyword(null,"show-option","show-option",1962057502));
var show_option_short = cljs.core.get.call(null,map__51024__$1,new cljs.core.Keyword(null,"show-option-short","show-option-short",-1992844057));
var options = cljs.core.get.call(null,map__51024__$1,new cljs.core.Keyword(null,"options","options",99638489));
var form_QMARK_ = cljs.core.get.call(null,map__51024__$1,new cljs.core.Keyword(null,"form?","form?",-2055688328));
var error = cljs.core.get.call(null,map__51024__$1,new cljs.core.Keyword(null,"error","error",-978969032));
var selected_set = cljs.core.set.call(null,(function (){var or__30175__auto__ = data;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return cljs.core.PersistentHashSet.EMPTY;
}
})());
var option_idx = cljs.core.zipmap.call(null,options,cljs.core.range.call(null));
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.select_field,new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"style","style",-496642736),style,new cljs.core.Keyword(null,"floating-label-text","floating-label-text",740415797),label,new cljs.core.Keyword(null,"multiple","multiple",1244445549),true,new cljs.core.Keyword(null,"value","value",305978217),cljs.core.clj__GT_js.call(null,cljs.core.map.call(null,option_idx,selected_set)),new cljs.core.Keyword(null,"selection-renderer","selection-renderer",-537035544),((function (selected_set,option_idx,map__51024,map__51024__$1,field,update_BANG_,label,name,style,show_option,show_option_short,options,form_QMARK_,error){
return (function (values){
return clojure.string.join.call(null,", ",cljs.core.map.call(null,cljs.core.comp.call(null,(function (){var or__30175__auto__ = show_option_short;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return show_option;
}
})(),cljs.core.partial.call(null,cljs.core.nth,options)),values));
});})(selected_set,option_idx,map__51024,map__51024__$1,field,update_BANG_,label,name,style,show_option,show_option_short,options,form_QMARK_,error))
,new cljs.core.Keyword(null,"on-change","on-change",-732046149),((function (selected_set,option_idx,map__51024,map__51024__$1,field,update_BANG_,label,name,style,show_option,show_option_short,options,form_QMARK_,error){
return (function (event,index,values){
return update_BANG_.call(null,cljs.core.into.call(null,cljs.core.PersistentHashSet.EMPTY,cljs.core.map.call(null,cljs.core.partial.call(null,cljs.core.nth,options)),values));
});})(selected_set,option_idx,map__51024,map__51024__$1,field,update_BANG_,label,name,style,show_option,show_option_short,options,form_QMARK_,error))
], null),cljs.core.doall.call(null,cljs.core.map_indexed.call(null,((function (selected_set,option_idx,map__51024,map__51024__$1,field,update_BANG_,label,name,style,show_option,show_option_short,options,form_QMARK_,error){
return (function (i,option){
return cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.menu_item,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"value","value",305978217),i,new cljs.core.Keyword(null,"primary-text","primary-text",146474209),show_option.call(null,option),new cljs.core.Keyword(null,"inset-children","inset-children",2137916348),true,new cljs.core.Keyword(null,"checked","checked",-50955819),cljs.core.boolean$.call(null,selected_set.call(null,option))], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),i], null));
});})(selected_set,option_idx,map__51024,map__51024__$1,field,update_BANG_,label,name,style,show_option,show_option_short,options,form_QMARK_,error))
,options))], null);
}));
ote.ui.form_fields.phone_regex = /\+?\d+/;
cljs.core._add_method.call(null,ote.ui.form_fields.field,new cljs.core.Keyword(null,"phone","phone",-763596057),(function (opts,data){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.form_fields.field,cljs.core.assoc.call(null,opts,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"string","string",-1989541586),new cljs.core.Keyword(null,"regex","regex",939488856),ote.ui.form_fields.phone_regex)], null);
}));
ote.ui.form_fields.number_regex = /\d*([\.,]\d*)?/;
cljs.core._add_method.call(null,ote.ui.form_fields.field,new cljs.core.Keyword(null,"number","number",1570378438),(function (_,data){
var txt = reagent.core.atom.call(null,(cljs.core.truth_(data)?data.toFixed((2)):""));
return ((function (txt){
return (function (p__51027,data__$1){
var map__51028 = p__51027;
var map__51028__$1 = ((((!((map__51028 == null)))?((((map__51028.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51028.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51028):map__51028);
var opts = map__51028__$1;
var update_BANG_ = cljs.core.get.call(null,map__51028__$1,new cljs.core.Keyword(null,"update!","update!",-1453508586));
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.form_fields.field,cljs.core.assoc.call(null,opts,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"string","string",-1989541586),new cljs.core.Keyword(null,"parse","parse",-1162164619),parseFloat,new cljs.core.Keyword(null,"regex","regex",939488856),ote.ui.form_fields.number_regex,new cljs.core.Keyword(null,"update!","update!",-1453508586),((function (map__51028,map__51028__$1,opts,update_BANG_,txt){
return (function (p1__51026_SHARP_){
cljs.core.reset_BANG_.call(null,txt,p1__51026_SHARP_);

return update_BANG_.call(null,((clojure.string.blank_QMARK_.call(null,p1__51026_SHARP_))?null:parseFloat(clojure.string.replace.call(null,p1__51026_SHARP_,/,/,"."),p1__51026_SHARP_)));
});})(map__51028,map__51028__$1,opts,update_BANG_,txt))
),cljs.core.deref.call(null,txt)], null);
});
;})(txt))
}));
ote.ui.form_fields.time_regex = /\d{0,2}(:\d{0,2})?/;
cljs.core._add_method.call(null,ote.ui.form_fields.field,new cljs.core.Keyword(null,"time","time",1385887882),(function (p__51030,data){
var map__51031 = p__51030;
var map__51031__$1 = ((((!((map__51031 == null)))?((((map__51031.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51031.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51031):map__51031);
var opts = map__51031__$1;
var update_BANG_ = cljs.core.get.call(null,map__51031__$1,new cljs.core.Keyword(null,"update!","update!",-1453508586));
var data__$1 = (function (){var or__30175__auto__ = (function (){var G__51034 = data;
var G__51034__$1 = (((G__51034 == null))?null:cljs.core.meta.call(null,G__51034));
if((G__51034__$1 == null)){
return null;
} else {
return new cljs.core.Keyword("ote.ui.form-fields","incomplete","ote.ui.form-fields/incomplete",-1585472008).cljs$core$IFn$_invoke$arity$1(G__51034__$1);
}
})();
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
var or__30175__auto____$1 = (function (){var and__30163__auto__ = data;
if(cljs.core.truth_(and__30163__auto__)){
return ote.time.format_time.call(null,data);
} else {
return and__30163__auto__;
}
})();
if(cljs.core.truth_(or__30175__auto____$1)){
return or__30175__auto____$1;
} else {
return "";
}
}
})();
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.form_fields.field,cljs.core.assoc.call(null,opts,new cljs.core.Keyword(null,"update!","update!",-1453508586),((function (data__$1,map__51031,map__51031__$1,opts,update_BANG_){
return (function (string){
return update_BANG_.call(null,cljs.core.with_meta.call(null,ote.time.parse_time.call(null,string),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("ote.ui.form-fields","incomplete","ote.ui.form-fields/incomplete",-1585472008),string], null)));
});})(data__$1,map__51031,map__51031__$1,opts,update_BANG_))
,new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"string","string",-1989541586),new cljs.core.Keyword(null,"regex","regex",939488856),ote.ui.form_fields.time_regex),data__$1], null);
}));
cljs.core._add_method.call(null,ote.ui.form_fields.field,new cljs.core.Keyword(null,"time-picker","time-picker",540588491),(function (p__51035,data){
var map__51036 = p__51035;
var map__51036__$1 = ((((!((map__51036 == null)))?((((map__51036.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51036.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51036):map__51036);
var opts = map__51036__$1;
var update_BANG_ = cljs.core.get.call(null,map__51036__$1,new cljs.core.Keyword(null,"update!","update!",-1453508586));
var ok_label = cljs.core.get.call(null,map__51036__$1,new cljs.core.Keyword(null,"ok-label","ok-label",808114315));
var cancel_label = cljs.core.get.call(null,map__51036__$1,new cljs.core.Keyword(null,"cancel-label","cancel-label",-1093310551));
var default_time = cljs.core.get.call(null,map__51036__$1,new cljs.core.Keyword(null,"default-time","default-time",48050636));
var time_picker_time = ((cljs.core._EQ_.call(null,cljs.core.nil_QMARK_,data))?default_time:data);
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.time_picker,new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"format","format",-1306924766),"24hr",new cljs.core.Keyword(null,"cancel-label","cancel-label",-1093310551),cancel_label,new cljs.core.Keyword(null,"ok-label","ok-label",808114315),ok_label,new cljs.core.Keyword(null,"minutes-step","minutes-step",1882764527),(5),new cljs.core.Keyword(null,"default-time","default-time",48050636),ote.time.to_js_time.call(null,time_picker_time),new cljs.core.Keyword(null,"on-change","on-change",-732046149),((function (time_picker_time,map__51036,map__51036__$1,opts,update_BANG_,ok_label,cancel_label,default_time){
return (function (event,value){
return update_BANG_.call(null,ote.time.parse_time.call(null,ote.time.format_js_time.call(null,value)));
});})(time_picker_time,map__51036,map__51036__$1,opts,update_BANG_,ok_label,cancel_label,default_time))
], null)], null);
}));
cljs.core._add_method.call(null,ote.ui.form_fields.field,new cljs.core.Keyword(null,"date-picker","date-picker",882557010),(function (p__51038,data){
var map__51039 = p__51038;
var map__51039__$1 = ((((!((map__51039 == null)))?((((map__51039.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51039.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51039):map__51039);
var opts = map__51039__$1;
var update_BANG_ = cljs.core.get.call(null,map__51039__$1,new cljs.core.Keyword(null,"update!","update!",-1453508586));
var label = cljs.core.get.call(null,map__51039__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var ok_label = cljs.core.get.call(null,map__51039__$1,new cljs.core.Keyword(null,"ok-label","ok-label",808114315));
var cancel_label = cljs.core.get.call(null,map__51039__$1,new cljs.core.Keyword(null,"cancel-label","cancel-label",-1093310551));
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.date_picker,new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"hint-text","hint-text",771272523),label,new cljs.core.Keyword(null,"value","value",305978217),data,new cljs.core.Keyword(null,"on-change","on-change",-732046149),((function (map__51039,map__51039__$1,opts,update_BANG_,label,ok_label,cancel_label){
return (function (_,date){
return update_BANG_.call(null,date);
});})(map__51039,map__51039__$1,opts,update_BANG_,label,ok_label,cancel_label))
,new cljs.core.Keyword(null,"format-date","format-date",-1658495668),ote.time.format_date,new cljs.core.Keyword(null,"ok-label","ok-label",808114315),(function (){var or__30175__auto__ = ok_label;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"buttons","buttons",-1953831197),new cljs.core.Keyword(null,"save","save",1850079149)], null));
}
})(),new cljs.core.Keyword(null,"cancel-label","cancel-label",-1093310551),(function (){var or__30175__auto__ = cancel_label;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"buttons","buttons",-1953831197),new cljs.core.Keyword(null,"cancel","cancel",-1964088360)], null));
}
})(),new cljs.core.Keyword(null,"locale","locale",-2115712697),"fi-FI",new cljs.core.Keyword(null,"Date-time-format","Date-time-format",338928414),Intl.DateTimeFormat], null)], null);
}));
cljs.core._add_method.call(null,ote.ui.form_fields.field,new cljs.core.Keyword(null,"default","default",-1987822328),(function (opts,data){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.error","div.error",314336058),"Missing field type: ",new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(opts)], null);
}));
cljs.core._add_method.call(null,ote.ui.form_fields.field,new cljs.core.Keyword(null,"table","table",-564943036),(function (p__51044,data){
var map__51045 = p__51044;
var map__51045__$1 = ((((!((map__51045 == null)))?((((map__51045.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51045.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51045):map__51045);
var opts = map__51045__$1;
var table_fields = cljs.core.get.call(null,map__51045__$1,new cljs.core.Keyword(null,"table-fields","table-fields",-923733996));
var update_BANG_ = cljs.core.get.call(null,map__51045__$1,new cljs.core.Keyword(null,"update!","update!",-1453508586));
var delete_QMARK_ = cljs.core.get.call(null,map__51045__$1,new cljs.core.Keyword(null,"delete?","delete?",789956376));
var add_label = cljs.core.get.call(null,map__51045__$1,new cljs.core.Keyword(null,"add-label","add-label",28553208));
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.table,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.table_header,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"adjust-for-checkbox","adjust-for-checkbox",-849822919),false,new cljs.core.Keyword(null,"display-select-all","display-select-all",-1148445289),false], null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.table_row,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"selectable","selectable",370587038),false], null),cljs.core.doall.call(null,(function (){var iter__31057__auto__ = ((function (map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label){
return (function ote$ui$form_fields$iter__51047(s__51048){
return (new cljs.core.LazySeq(null,((function (map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label){
return (function (){
var s__51048__$1 = s__51048;
while(true){
var temp__5290__auto__ = cljs.core.seq.call(null,s__51048__$1);
if(temp__5290__auto__){
var s__51048__$2 = temp__5290__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,s__51048__$2)){
var c__31055__auto__ = cljs.core.chunk_first.call(null,s__51048__$2);
var size__31056__auto__ = cljs.core.count.call(null,c__31055__auto__);
var b__51050 = cljs.core.chunk_buffer.call(null,size__31056__auto__);
if((function (){var i__51049 = (0);
while(true){
if((i__51049 < size__31056__auto__)){
var map__51051 = cljs.core._nth.call(null,c__31055__auto__,i__51049);
var map__51051__$1 = ((((!((map__51051 == null)))?((((map__51051.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51051.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51051):map__51051);
var tf = map__51051__$1;
var name = cljs.core.get.call(null,map__51051__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var label = cljs.core.get.call(null,map__51051__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var width = cljs.core.get.call(null,map__51051__$1,new cljs.core.Keyword(null,"width","width",-384071477));
cljs.core.chunk_append.call(null,b__51050,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.table_header_column,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"width","width",-384071477),width], null)], null),label], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),name], null)));

var G__51063 = (i__51049 + (1));
i__51049 = G__51063;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__51050),ote$ui$form_fields$iter__51047.call(null,cljs.core.chunk_rest.call(null,s__51048__$2)));
} else {
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__51050),null);
}
} else {
var map__51053 = cljs.core.first.call(null,s__51048__$2);
var map__51053__$1 = ((((!((map__51053 == null)))?((((map__51053.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51053.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51053):map__51053);
var tf = map__51053__$1;
var name = cljs.core.get.call(null,map__51053__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var label = cljs.core.get.call(null,map__51053__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var width = cljs.core.get.call(null,map__51053__$1,new cljs.core.Keyword(null,"width","width",-384071477));
return cljs.core.cons.call(null,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.table_header_column,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"width","width",-384071477),width], null)], null),label], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),name], null)),ote$ui$form_fields$iter__51047.call(null,cljs.core.rest.call(null,s__51048__$2)));
}
} else {
return null;
}
break;
}
});})(map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label))
,null,null));
});})(map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label))
;
return iter__31057__auto__.call(null,table_fields);
})()),(cljs.core.truth_(delete_QMARK_)?new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.table_header_column,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"width","width",-384071477),"70px"], null)], null),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"buttons","buttons",-1953831197),new cljs.core.Keyword(null,"delete","delete",-1768633620)], null))], null):null)], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.table_body,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"display-row-checkbox","display-row-checkbox",613576250),false], null),cljs.core.map_indexed.call(null,((function (map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label){
return (function (i,row){
return cljs.core.with_meta(new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.table_row,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"selectable","selectable",370587038),false,new cljs.core.Keyword(null,"display-border","display-border",-1286801080),false], null),cljs.core.doall.call(null,(function (){var iter__31057__auto__ = ((function (map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label){
return (function ote$ui$form_fields$iter__51055(s__51056){
return (new cljs.core.LazySeq(null,((function (map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label){
return (function (){
var s__51056__$1 = s__51056;
while(true){
var temp__5290__auto__ = cljs.core.seq.call(null,s__51056__$1);
if(temp__5290__auto__){
var s__51056__$2 = temp__5290__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,s__51056__$2)){
var c__31055__auto__ = cljs.core.chunk_first.call(null,s__51056__$2);
var size__31056__auto__ = cljs.core.count.call(null,c__31055__auto__);
var b__51058 = cljs.core.chunk_buffer.call(null,size__31056__auto__);
if((function (){var i__51057 = (0);
while(true){
if((i__51057 < size__31056__auto__)){
var map__51059 = cljs.core._nth.call(null,c__31055__auto__,i__51057);
var map__51059__$1 = ((((!((map__51059 == null)))?((((map__51059.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51059.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51059):map__51059);
var tf = map__51059__$1;
var name = cljs.core.get.call(null,map__51059__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var read = cljs.core.get.call(null,map__51059__$1,new cljs.core.Keyword(null,"read","read",1140058661));
var write = cljs.core.get.call(null,map__51059__$1,new cljs.core.Keyword(null,"write","write",-1857649168));
var width = cljs.core.get.call(null,map__51059__$1,new cljs.core.Keyword(null,"width","width",-384071477));
var update_fn = (cljs.core.truth_(write)?((function (i__51057,map__51059,map__51059__$1,tf,name,read,write,width,c__31055__auto__,size__31056__auto__,b__51058,s__51056__$2,temp__5290__auto__,map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label){
return (function (p1__51041_SHARP_){
return cljs.core.update.call(null,data,i,write,p1__51041_SHARP_);
});})(i__51057,map__51059,map__51059__$1,tf,name,read,write,width,c__31055__auto__,size__31056__auto__,b__51058,s__51056__$2,temp__5290__auto__,map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label))
:((function (i__51057,map__51059,map__51059__$1,tf,name,read,write,width,c__31055__auto__,size__31056__auto__,b__51058,s__51056__$2,temp__5290__auto__,map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label){
return (function (p1__51042_SHARP_){
return cljs.core.assoc_in.call(null,data,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [i,name], null),p1__51042_SHARP_);
});})(i__51057,map__51059,map__51059__$1,tf,name,read,write,width,c__31055__auto__,size__31056__auto__,b__51058,s__51056__$2,temp__5290__auto__,map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label))
);
cljs.core.chunk_append.call(null,b__51058,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.table_row_column,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"width","width",-384071477),width], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.form_fields.field,cljs.core.assoc.call(null,tf,new cljs.core.Keyword(null,"table?","table?",-1064705406),true,new cljs.core.Keyword(null,"update!","update!",-1453508586),((function (i__51057,update_fn,map__51059,map__51059__$1,tf,name,read,write,width,c__31055__auto__,size__31056__auto__,b__51058,s__51056__$2,temp__5290__auto__,map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label){
return (function (p1__51043_SHARP_){
return update_BANG_.call(null,update_fn.call(null,p1__51043_SHARP_));
});})(i__51057,update_fn,map__51059,map__51059__$1,tf,name,read,write,width,c__31055__auto__,size__31056__auto__,b__51058,s__51056__$2,temp__5290__auto__,map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label))
),(function (){var or__30175__auto__ = read;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return name;
}
})().call(null,row)], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),name], null)));

var G__51064 = (i__51057 + (1));
i__51057 = G__51064;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__51058),ote$ui$form_fields$iter__51055.call(null,cljs.core.chunk_rest.call(null,s__51056__$2)));
} else {
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__51058),null);
}
} else {
var map__51061 = cljs.core.first.call(null,s__51056__$2);
var map__51061__$1 = ((((!((map__51061 == null)))?((((map__51061.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51061.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51061):map__51061);
var tf = map__51061__$1;
var name = cljs.core.get.call(null,map__51061__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var read = cljs.core.get.call(null,map__51061__$1,new cljs.core.Keyword(null,"read","read",1140058661));
var write = cljs.core.get.call(null,map__51061__$1,new cljs.core.Keyword(null,"write","write",-1857649168));
var width = cljs.core.get.call(null,map__51061__$1,new cljs.core.Keyword(null,"width","width",-384071477));
var update_fn = (cljs.core.truth_(write)?((function (map__51061,map__51061__$1,tf,name,read,write,width,s__51056__$2,temp__5290__auto__,map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label){
return (function (p1__51041_SHARP_){
return cljs.core.update.call(null,data,i,write,p1__51041_SHARP_);
});})(map__51061,map__51061__$1,tf,name,read,write,width,s__51056__$2,temp__5290__auto__,map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label))
:((function (map__51061,map__51061__$1,tf,name,read,write,width,s__51056__$2,temp__5290__auto__,map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label){
return (function (p1__51042_SHARP_){
return cljs.core.assoc_in.call(null,data,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [i,name], null),p1__51042_SHARP_);
});})(map__51061,map__51061__$1,tf,name,read,write,width,s__51056__$2,temp__5290__auto__,map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label))
);
return cljs.core.cons.call(null,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.table_row_column,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"width","width",-384071477),width], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.form_fields.field,cljs.core.assoc.call(null,tf,new cljs.core.Keyword(null,"table?","table?",-1064705406),true,new cljs.core.Keyword(null,"update!","update!",-1453508586),((function (update_fn,map__51061,map__51061__$1,tf,name,read,write,width,s__51056__$2,temp__5290__auto__,map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label){
return (function (p1__51043_SHARP_){
return update_BANG_.call(null,update_fn.call(null,p1__51043_SHARP_));
});})(update_fn,map__51061,map__51061__$1,tf,name,read,write,width,s__51056__$2,temp__5290__auto__,map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label))
),(function (){var or__30175__auto__ = read;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return name;
}
})().call(null,row)], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),name], null)),ote$ui$form_fields$iter__51055.call(null,cljs.core.rest.call(null,s__51056__$2)));
}
} else {
return null;
}
break;
}
});})(map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label))
,null,null));
});})(map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label))
;
return iter__31057__auto__.call(null,table_fields);
})()),(cljs.core.truth_(delete_QMARK_)?new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.table_row_column,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"width","width",-384071477),"70px"], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.icon_button,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-click","on-click",1632826543),((function (map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label){
return (function (){
return update_BANG_.call(null,cljs.core.vec.call(null,cljs.core.concat.call(null,(((i > (0)))?cljs.core.take.call(null,i,data):null),cljs.core.drop.call(null,(i + (1)),data))));
});})(map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label))
], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.icons.action_delete], null)], null)], null):null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),i], null));
});})(map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label))
,data)], null)], null),(cljs.core.truth_(add_label)?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.buttons.save,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"on-click","on-click",1632826543),((function (map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label){
return (function (){
return update_BANG_.call(null,cljs.core.conj.call(null,(function (){var or__30175__auto__ = data;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return cljs.core.PersistentVector.EMPTY;
}
})(),cljs.core.PersistentArrayMap.EMPTY));
});})(map__51045,map__51045__$1,opts,table_fields,update_BANG_,delete_QMARK_,add_label))
,new cljs.core.Keyword(null,"label","label",1718410804),add_label,new cljs.core.Keyword(null,"label-style","label-style",-1703650121),ote.style.base.button_label_style,new cljs.core.Keyword(null,"disabled","disabled",-1529784218),false], null)], null):null)], null);
}));

//# sourceMappingURL=form_fields.js.map?rel=1510137290394
