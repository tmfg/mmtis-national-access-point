// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.views.place_search');
goog.require('cljs.core');
goog.require('reagent.core');
goog.require('ote.app.controller.place_search');
goog.require('ote.ui.form_fields');
goog.require('ote.ui.buttons');
goog.require('ote.ui.leaflet');
goog.require('ote.ui.form');
goog.require('ote.localization');
goog.require('cljs_react_material_ui.reagent');
goog.require('ote.db.transport_service');
goog.require('ote.db.places');
goog.require('goog.object');
goog.require('cljsjs.leaflet');
goog.require('stylefy.core');
goog.require('ote.style.base');
/**
 * Pre 1.0 fix for bug in MaterialUI Chip which unconditionally
 *   cancels a backspace events (causing an embedded input field to
 *   not be able to erase text.
 */
ote.views.place_search.monkey_patch_chip_backspace = (function ote$views$place_search$monkey_patch_chip_backspace(this$){
var refs = (this$["refs"]);
console.log("monkey patching chip bug");

return goog.object.forEach(refs,((function (refs){
return (function (chip,ref,_){
if(cljs.core.truth_((chip["__backspace_monkey_patch"]))){
return null;
} else {
var old = (chip["handleKeyDown"]);
(chip["handleKeyDown"] = ((function (old,refs){
return (function (event){
if(cljs.core._EQ_.call(null,"Backspace",event.key)){
return null;
} else {
return old.call(null,event);
}
});})(old,refs))
);

return (chip["__backspace_monkey_patch"] = true);
}
});})(refs))
);
});
ote.views.place_search.result_chips = (function ote$views$place_search$result_chips(e_BANG_,results){
return reagent.core.create_class.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"component-did-mount","component-did-mount",-1126910518),ote.views.place_search.monkey_patch_chip_backspace,new cljs.core.Keyword(null,"component-did-update","component-did-update",-1468549173),ote.views.place_search.monkey_patch_chip_backspace,new cljs.core.Keyword(null,"reagent-render","reagent-render",-985383853),(function (e_BANG___$1,results__$1){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.place-search-results","div.place-search-results",1426587000),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"display","display",242065432),"flex",new cljs.core.Keyword(null,"flex-wrap","flex-wrap",455413707),"wrap"], null)], null),(function (){var iter__31057__auto__ = (function ote$views$place_search$result_chips_$_iter__52088(s__52089){
return (new cljs.core.LazySeq(null,(function (){
var s__52089__$1 = s__52089;
while(true){
var temp__5290__auto__ = cljs.core.seq.call(null,s__52089__$1);
if(temp__5290__auto__){
var s__52089__$2 = temp__5290__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,s__52089__$2)){
var c__31055__auto__ = cljs.core.chunk_first.call(null,s__52089__$2);
var size__31056__auto__ = cljs.core.count.call(null,c__31055__auto__);
var b__52091 = cljs.core.chunk_buffer.call(null,size__31056__auto__);
if((function (){var i__52090 = (0);
while(true){
if((i__52090 < size__31056__auto__)){
var map__52092 = cljs.core._nth.call(null,c__31055__auto__,i__52090);
var map__52092__$1 = ((((!((map__52092 == null)))?((((map__52092.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52092.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52092):map__52092);
var result = map__52092__$1;
var editing_QMARK_ = cljs.core.get.call(null,map__52092__$1,new cljs.core.Keyword(null,"editing?","editing?",1646440800));
var namefin = cljs.core.get.call(null,map__52092__$1,new cljs.core.Keyword("ote.db.places","namefin","ote.db.places/namefin",204883439));
var type = cljs.core.get.call(null,map__52092__$1,new cljs.core.Keyword("ote.db.places","type","ote.db.places/type",-975773958));
var id = cljs.core.get.call(null,map__52092__$1,new cljs.core.Keyword("ote.db.places","id","ote.db.places/id",772786118));
cljs.core.chunk_append.call(null,b__52091,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span","span",1394872991),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.chip,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"ref","ref",1289896967),id,new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"margin","margin",-995903681),(4)], null),new cljs.core.Keyword(null,"on-click","on-click",1632826543),(((cljs.core._EQ_.call(null,"drawn",type)) && (cljs.core.not.call(null,editing_QMARK_)))?((function (i__52090,map__52092,map__52092__$1,result,editing_QMARK_,namefin,type,id,c__31055__auto__,size__31056__auto__,b__52091,s__52089__$2,temp__5290__auto__){
return (function (){
return e_BANG___$1.call(null,ote.app.controller.place_search.__GT_EditDrawnGeometryName.call(null,id));
});})(i__52090,map__52092,map__52092__$1,result,editing_QMARK_,namefin,type,id,c__31055__auto__,size__31056__auto__,b__52091,s__52089__$2,temp__5290__auto__))
:cljs.core.constantly.call(null,false)),new cljs.core.Keyword(null,"on-request-delete","on-request-delete",174385806),((function (i__52090,map__52092,map__52092__$1,result,editing_QMARK_,namefin,type,id,c__31055__auto__,size__31056__auto__,b__52091,s__52089__$2,temp__5290__auto__){
return (function (){
return e_BANG___$1.call(null,ote.app.controller.place_search.__GT_RemovePlaceById.call(null,id));
});})(i__52090,map__52092,map__52092__$1,result,editing_QMARK_,namefin,type,id,c__31055__auto__,size__31056__auto__,b__52091,s__52089__$2,temp__5290__auto__))
], null),(cljs.core.truth_(editing_QMARK_)?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.text_field,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"value","value",305978217),namefin,new cljs.core.Keyword(null,"floating-label-text","floating-label-text",740415797),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.Keyword(null,"rename-place","rename-place",746287733)], null)),new cljs.core.Keyword(null,"on-key-press","on-key-press",-399563677),((function (i__52090,map__52092,map__52092__$1,result,editing_QMARK_,namefin,type,id,c__31055__auto__,size__31056__auto__,b__52091,s__52089__$2,temp__5290__auto__){
return (function (p1__52085_SHARP_){
if(cljs.core._EQ_.call(null,"Enter",p1__52085_SHARP_.key)){
return e_BANG___$1.call(null,ote.app.controller.place_search.__GT_EditDrawnGeometryName.call(null,id));
} else {
return null;
}
});})(i__52090,map__52092,map__52092__$1,result,editing_QMARK_,namefin,type,id,c__31055__auto__,size__31056__auto__,b__52091,s__52089__$2,temp__5290__auto__))
,new cljs.core.Keyword(null,"on-change","on-change",-732046149),((function (i__52090,map__52092,map__52092__$1,result,editing_QMARK_,namefin,type,id,c__31055__auto__,size__31056__auto__,b__52091,s__52089__$2,temp__5290__auto__){
return (function (p1__52087_SHARP_,p2__52086_SHARP_){
return e_BANG___$1.call(null,ote.app.controller.place_search.__GT_SetDrawnGeometryName.call(null,id,p2__52086_SHARP_));
});})(i__52090,map__52092,map__52092__$1,result,editing_QMARK_,namefin,type,id,c__31055__auto__,size__31056__auto__,b__52091,s__52089__$2,temp__5290__auto__))
], null)], null):namefin)], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),id], null)));

var G__52096 = (i__52090 + (1));
i__52090 = G__52096;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__52091),ote$views$place_search$result_chips_$_iter__52088.call(null,cljs.core.chunk_rest.call(null,s__52089__$2)));
} else {
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__52091),null);
}
} else {
var map__52094 = cljs.core.first.call(null,s__52089__$2);
var map__52094__$1 = ((((!((map__52094 == null)))?((((map__52094.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52094.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52094):map__52094);
var result = map__52094__$1;
var editing_QMARK_ = cljs.core.get.call(null,map__52094__$1,new cljs.core.Keyword(null,"editing?","editing?",1646440800));
var namefin = cljs.core.get.call(null,map__52094__$1,new cljs.core.Keyword("ote.db.places","namefin","ote.db.places/namefin",204883439));
var type = cljs.core.get.call(null,map__52094__$1,new cljs.core.Keyword("ote.db.places","type","ote.db.places/type",-975773958));
var id = cljs.core.get.call(null,map__52094__$1,new cljs.core.Keyword("ote.db.places","id","ote.db.places/id",772786118));
return cljs.core.cons.call(null,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span","span",1394872991),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.chip,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"ref","ref",1289896967),id,new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"margin","margin",-995903681),(4)], null),new cljs.core.Keyword(null,"on-click","on-click",1632826543),(((cljs.core._EQ_.call(null,"drawn",type)) && (cljs.core.not.call(null,editing_QMARK_)))?((function (map__52094,map__52094__$1,result,editing_QMARK_,namefin,type,id,s__52089__$2,temp__5290__auto__){
return (function (){
return e_BANG___$1.call(null,ote.app.controller.place_search.__GT_EditDrawnGeometryName.call(null,id));
});})(map__52094,map__52094__$1,result,editing_QMARK_,namefin,type,id,s__52089__$2,temp__5290__auto__))
:cljs.core.constantly.call(null,false)),new cljs.core.Keyword(null,"on-request-delete","on-request-delete",174385806),((function (map__52094,map__52094__$1,result,editing_QMARK_,namefin,type,id,s__52089__$2,temp__5290__auto__){
return (function (){
return e_BANG___$1.call(null,ote.app.controller.place_search.__GT_RemovePlaceById.call(null,id));
});})(map__52094,map__52094__$1,result,editing_QMARK_,namefin,type,id,s__52089__$2,temp__5290__auto__))
], null),(cljs.core.truth_(editing_QMARK_)?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.text_field,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"value","value",305978217),namefin,new cljs.core.Keyword(null,"floating-label-text","floating-label-text",740415797),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.Keyword(null,"rename-place","rename-place",746287733)], null)),new cljs.core.Keyword(null,"on-key-press","on-key-press",-399563677),((function (map__52094,map__52094__$1,result,editing_QMARK_,namefin,type,id,s__52089__$2,temp__5290__auto__){
return (function (p1__52085_SHARP_){
if(cljs.core._EQ_.call(null,"Enter",p1__52085_SHARP_.key)){
return e_BANG___$1.call(null,ote.app.controller.place_search.__GT_EditDrawnGeometryName.call(null,id));
} else {
return null;
}
});})(map__52094,map__52094__$1,result,editing_QMARK_,namefin,type,id,s__52089__$2,temp__5290__auto__))
,new cljs.core.Keyword(null,"on-change","on-change",-732046149),((function (map__52094,map__52094__$1,result,editing_QMARK_,namefin,type,id,s__52089__$2,temp__5290__auto__){
return (function (p1__52087_SHARP_,p2__52086_SHARP_){
return e_BANG___$1.call(null,ote.app.controller.place_search.__GT_SetDrawnGeometryName.call(null,id,p2__52086_SHARP_));
});})(map__52094,map__52094__$1,result,editing_QMARK_,namefin,type,id,s__52089__$2,temp__5290__auto__))
], null)], null):namefin)], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),id], null)),ote$views$place_search$result_chips_$_iter__52088.call(null,cljs.core.rest.call(null,s__52089__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__31057__auto__.call(null,cljs.core.map.call(null,new cljs.core.Keyword(null,"place","place",-819689466),results__$1));
})()], null);
})], null));
});
ote.views.place_search.result_geometry = (function ote$views$place_search$result_geometry(p__52097){
var map__52098 = p__52097;
var map__52098__$1 = ((((!((map__52098 == null)))?((((map__52098.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52098.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52098):map__52098);
var name = cljs.core.get.call(null,map__52098__$1,new cljs.core.Keyword("ote.db.places","name","ote.db.places/name",-299670885));
var location = cljs.core.get.call(null,map__52098__$1,new cljs.core.Keyword("ote.db.places","location","ote.db.places/location",-318856378));
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.leaflet.FeatureGroup,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.leaflet.geometry,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"color","color",1011675173),"green",new cljs.core.Keyword(null,"dashArray","dashArray",-1716456698),"5,5"], null),location], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.leaflet.Popup,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),name], null)], null)], null);
});
/**
 * Install Leaflet draw plugin to to places-map component.
 */
ote.views.place_search.install_draw_control_BANG_ = (function ote$views$place_search$install_draw_control_BANG_(e_BANG_,this$){
console.log("install draw control");

var m = (this$["refs"]["leaflet"]["leafletElement"]);
var fg = (new L.FeatureGroup());
var dc = (new L.Control.Draw(({"edit": ({"featureGroup": fg, "remove": false})})));
m.addLayer(fg);

m.addControl(dc);

return m.on((L["Draw"]["Event"]["CREATED"]),((function (m,fg,dc){
return (function (p1__52100_SHARP_){
var layer = (p1__52100_SHARP_["layer"]);
var geojson = layer.toGeoJSON();
return e_BANG_.call(null,ote.app.controller.place_search.__GT_AddDrawnGeometry.call(null,geojson));
});})(m,fg,dc))
);
});
ote.views.place_search.places_map = (function ote$views$place_search$places_map(e_BANG_,results){
var feature_group = cljs.core.atom.call(null,null);
return reagent.core.create_class.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"component-did-mount","component-did-mount",-1126910518),((function (feature_group){
return (function (p1__52101_SHARP_){
ote.views.place_search.install_draw_control_BANG_.call(null,e_BANG_,p1__52101_SHARP_);

return ote.ui.leaflet.update_bounds_from_layers.call(null,p1__52101_SHARP_);
});})(feature_group))
,new cljs.core.Keyword(null,"component-did-update","component-did-update",-1468549173),ote.ui.leaflet.update_bounds_from_layers,new cljs.core.Keyword(null,"reagent-render","reagent-render",-985383853),((function (feature_group){
return (function (e_BANG___$1,results__$1){
return new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.leaflet.Map,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"ref","ref",1289896967),"leaflet",new cljs.core.Keyword(null,"center","center",-748944368),[(65),(25)],new cljs.core.Keyword(null,"zoom","zoom",-1827487038),(5)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.leaflet.TileLayer,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"url","url",276297046),"http://{s}.tile.osm.org/{z}/{x}/{y}.png",new cljs.core.Keyword(null,"attribution","attribution",1937239286),"&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors"], null)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.leaflet.FeatureGroup], null),(function (){var iter__31057__auto__ = ((function (feature_group){
return (function ote$views$place_search$places_map_$_iter__52102(s__52103){
return (new cljs.core.LazySeq(null,((function (feature_group){
return (function (){
var s__52103__$1 = s__52103;
while(true){
var temp__5290__auto__ = cljs.core.seq.call(null,s__52103__$1);
if(temp__5290__auto__){
var s__52103__$2 = temp__5290__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,s__52103__$2)){
var c__31055__auto__ = cljs.core.chunk_first.call(null,s__52103__$2);
var size__31056__auto__ = cljs.core.count.call(null,c__31055__auto__);
var b__52105 = cljs.core.chunk_buffer.call(null,size__31056__auto__);
if((function (){var i__52104 = (0);
while(true){
if((i__52104 < size__31056__auto__)){
var map__52106 = cljs.core._nth.call(null,c__31055__auto__,i__52104);
var map__52106__$1 = ((((!((map__52106 == null)))?((((map__52106.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52106.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52106):map__52106);
var place = cljs.core.get.call(null,map__52106__$1,new cljs.core.Keyword(null,"place","place",-819689466));
var geojson = cljs.core.get.call(null,map__52106__$1,new cljs.core.Keyword(null,"geojson","geojson",-719473398));
cljs.core.chunk_append.call(null,b__52105,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.leaflet.GeoJSON,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"data","data",-232669377),geojson,new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"color","color",1011675173),"green"], null)], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),new cljs.core.Keyword("ote.db.places","id","ote.db.places/id",772786118).cljs$core$IFn$_invoke$arity$1(place)], null)));

var G__52110 = (i__52104 + (1));
i__52104 = G__52110;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__52105),ote$views$place_search$places_map_$_iter__52102.call(null,cljs.core.chunk_rest.call(null,s__52103__$2)));
} else {
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__52105),null);
}
} else {
var map__52108 = cljs.core.first.call(null,s__52103__$2);
var map__52108__$1 = ((((!((map__52108 == null)))?((((map__52108.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52108.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52108):map__52108);
var place = cljs.core.get.call(null,map__52108__$1,new cljs.core.Keyword(null,"place","place",-819689466));
var geojson = cljs.core.get.call(null,map__52108__$1,new cljs.core.Keyword(null,"geojson","geojson",-719473398));
return cljs.core.cons.call(null,cljs.core.with_meta(new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.leaflet.GeoJSON,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"data","data",-232669377),geojson,new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"color","color",1011675173),"green"], null)], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),new cljs.core.Keyword("ote.db.places","id","ote.db.places/id",772786118).cljs$core$IFn$_invoke$arity$1(place)], null)),ote$views$place_search$places_map_$_iter__52102.call(null,cljs.core.rest.call(null,s__52103__$2)));
}
} else {
return null;
}
break;
}
});})(feature_group))
,null,null));
});})(feature_group))
;
return iter__31057__auto__.call(null,results__$1);
})()], null);
});})(feature_group))
], null));
});
ote.views.place_search.marker_map = (function ote$views$place_search$marker_map(e_BANG_,coordinate){
console.log("rendering marker map coordinate->",coordinate);

return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.leaflet.Map,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"center","center",-748944368),[65.01212149716532,25.47065377235413],new cljs.core.Keyword(null,"zoom","zoom",-1827487038),(16),new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (p1__52111_SHARP_){
return e_BANG_.call(null,ote.app.controller.place_search.__GT_SetMarker.call(null,p1__52111_SHARP_));
})], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.leaflet.TileLayer,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"url","url",276297046),"http://{s}.tile.osm.org/{z}/{x}/{y}.png",new cljs.core.Keyword(null,"attribution","attribution",1937239286),"&copy; <a href=\"http://osm.org/copyright\">OpenStreetMap</a> contributors"], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.leaflet.Marker,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"position","position",-2011731912),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.first.call(null,cljs.core.get_in.call(null,coordinate,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"coordinates","coordinates",-1225332668),new cljs.core.Keyword(null,"coordinates","coordinates",-1225332668)], null))),cljs.core.second.call(null,cljs.core.get_in.call(null,coordinate,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"coordinates","coordinates",-1225332668),new cljs.core.Keyword(null,"coordinates","coordinates",-1225332668)], null)))], null)], null)], null)], null);
});
ote.views.place_search.completions = (function ote$views$place_search$completions(completions){
return cljs.core.apply.call(null,cljs.core.array,cljs.core.map.call(null,(function (p__52112){
var map__52113 = p__52112;
var map__52113__$1 = ((((!((map__52113 == null)))?((((map__52113.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52113.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52113):map__52113);
var id = cljs.core.get.call(null,map__52113__$1,new cljs.core.Keyword("ote.db.places","id","ote.db.places/id",772786118));
var namefin = cljs.core.get.call(null,map__52113__$1,new cljs.core.Keyword("ote.db.places","namefin","ote.db.places/namefin",204883439));
var type = cljs.core.get.call(null,map__52113__$1,new cljs.core.Keyword("ote.db.places","type","ote.db.places/type",-975773958));
return ({"text": namefin, "id": id, "value": reagent.core.as_element.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.menu_item,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"primary-text","primary-text",146474209),namefin], null)], null))});
}),completions));
});
ote.views.place_search.place_search = (function ote$views$place_search$place_search(e_BANG_,place_search){
var results = new cljs.core.Keyword(null,"results","results",-1134170113).cljs$core$IFn$_invoke$arity$1(place_search);
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.place-search","div.place-search",-1591776080),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.col-xs-12.col-md-3","div.col-xs-12.col-md-3",-2066014239),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.place_search.result_chips,e_BANG_,results], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.auto_complete,new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"floating-label-text","floating-label-text",740415797),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.Keyword(null,"place-auto-complete","place-auto-complete",-1477201415)], null)),new cljs.core.Keyword(null,"filter","filter",-948537934),cljs.core.constantly.call(null,true),new cljs.core.Keyword(null,"dataSource","dataSource",-178401132),ote.views.place_search.completions.call(null,new cljs.core.Keyword(null,"completions","completions",-190930179).cljs$core$IFn$_invoke$arity$1(place_search)),new cljs.core.Keyword(null,"on-update-input","on-update-input",2051403085),((function (results){
return (function (p1__52115_SHARP_){
return e_BANG_.call(null,ote.app.controller.place_search.__GT_SetPlaceName.call(null,p1__52115_SHARP_));
});})(results))
,new cljs.core.Keyword(null,"search-text","search-text",1559451259),(function (){var or__30175__auto__ = new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(place_search);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"on-new-request","on-new-request",-1789507600),((function (results){
return (function (p1__52116_SHARP_){
return e_BANG_.call(null,ote.app.controller.place_search.__GT_AddPlace.call(null,(p1__52116_SHARP_["id"])));
});})(results))
], null)], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.col-xs-12.col-md-8","div.col-xs-12.col-md-8",-718693787),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.place_search.places_map,e_BANG_,results], null)], null)], null);
});
ote.views.place_search.place_search_form_group = (function ote$views$place_search$place_search_form_group(e_BANG_,label,name){
var empty_places_QMARK_ = cljs.core.comp.call(null,cljs.core.empty_QMARK_,new cljs.core.Keyword(null,"results","results",-1134170113),new cljs.core.Keyword(null,"place-search","place-search",-497916414));
return ote.ui.form.group.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"label","label",1718410804),label,new cljs.core.Keyword(null,"columns","columns",1998437288),(3)], null),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"component","component",1555936782),new cljs.core.Keyword(null,"name","name",1843675177),name,new cljs.core.Keyword(null,"required?","required?",-872514462),true,new cljs.core.Keyword(null,"is-empty?","is-empty?",-1881285798),empty_places_QMARK_,new cljs.core.Keyword(null,"component","component",1555936782),((function (empty_places_QMARK_){
return (function (p__52117){
var map__52118 = p__52117;
var map__52118__$1 = ((((!((map__52118 == null)))?((((map__52118.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52118.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52118):map__52118);
var data = cljs.core.get.call(null,map__52118__$1,new cljs.core.Keyword(null,"data","data",-232669377));
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span","span",1394872991),(cljs.core.truth_(empty_places_QMARK_.call(null,data))?new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),stylefy.core.use_style.call(null,ote.style.base.required_element),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"common-texts","common-texts",-934994303),new cljs.core.Keyword(null,"required-field","required-field",1847261386)], null))], null):null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.place_search.place_search,e_BANG_,new cljs.core.Keyword(null,"place-search","place-search",-497916414).cljs$core$IFn$_invoke$arity$1(data)], null)], null);
});})(empty_places_QMARK_))
], null));
});
ote.views.place_search.place_marker = (function ote$views$place_search$place_marker(e_BANG_,coordinates){
var coordinate = coordinates;
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.place_search.marker_map,e_BANG_,coordinates], null)], null);
});
ote.views.place_search.place_marker_form_group = (function ote$views$place_search$place_marker_form_group(e_BANG_,label,name){
return ote.ui.form.group.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"label","label",1718410804),label,new cljs.core.Keyword(null,"columns","columns",1998437288),(3)], null),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"component","component",1555936782),new cljs.core.Keyword(null,"name","name",1843675177),name,new cljs.core.Keyword(null,"component","component",1555936782),(function (p__52120){
var map__52121 = p__52120;
var map__52121__$1 = ((((!((map__52121 == null)))?((((map__52121.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52121.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52121):map__52121);
var data = cljs.core.get.call(null,map__52121__$1,new cljs.core.Keyword(null,"data","data",-232669377));
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.place_search.place_marker,e_BANG_,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"coordinates","coordinates",-1225332668),data], null)], null);
})], null));
});

//# sourceMappingURL=place_search.js.map?rel=1510137294179
