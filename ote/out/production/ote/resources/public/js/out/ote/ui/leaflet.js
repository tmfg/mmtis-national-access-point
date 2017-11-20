// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.ui.leaflet');
goog.require('cljs.core');
goog.require('cljsjs.react_leaflet');
goog.require('reagent.core');
ote.ui.leaflet.Map = reagent.core.adapt_react_class.call(null,ReactLeaflet.Map);
ote.ui.leaflet.TileLayer = reagent.core.adapt_react_class.call(null,ReactLeaflet.TileLayer);
ote.ui.leaflet.Circle = reagent.core.adapt_react_class.call(null,ReactLeaflet.Circle);
ote.ui.leaflet.Marker = reagent.core.adapt_react_class.call(null,ReactLeaflet.Marker);
ote.ui.leaflet.Popup = reagent.core.adapt_react_class.call(null,ReactLeaflet.Popup);
ote.ui.leaflet.Polygon = reagent.core.adapt_react_class.call(null,ReactLeaflet.Polygon);
ote.ui.leaflet.LayerGroup = reagent.core.adapt_react_class.call(null,ReactLeaflet.LayerGroup);
ote.ui.leaflet.FeatureGroup = reagent.core.adapt_react_class.call(null,ReactLeaflet.FeatureGroup);
ote.ui.leaflet.GeoJSON = reagent.core.adapt_react_class.call(null,ReactLeaflet.GeoJSON);
if(typeof ote.ui.leaflet.geometry !== 'undefined'){
} else {
ote.ui.leaflet.geometry = (function (){var method_table__31228__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var prefer_table__31229__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var method_cache__31230__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var cached_hierarchy__31231__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var hierarchy__31232__auto__ = cljs.core.get.call(null,cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"hierarchy","hierarchy",-1053470341),cljs.core.get_global_hierarchy.call(null));
return (new cljs.core.MultiFn(cljs.core.symbol.call(null,"ote.ui.leaflet","geometry"),((function (method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__,hierarchy__31232__auto__){
return (function (opts,geometry){
return new cljs.core.Keyword(null,"type","type",1174270348).cljs$core$IFn$_invoke$arity$1(geometry);
});})(method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__,hierarchy__31232__auto__))
,new cljs.core.Keyword(null,"default","default",-1987822328),hierarchy__31232__auto__,method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__));
})();
}
cljs.core._add_method.call(null,ote.ui.leaflet.geometry,new cljs.core.Keyword(null,"multipolygon","multipolygon",477364705),(function (style_options,p__52071){
var map__52072 = p__52071;
var map__52072__$1 = ((((!((map__52072 == null)))?((((map__52072.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52072.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52072):map__52072);
var polygons = cljs.core.get.call(null,map__52072__$1,new cljs.core.Keyword(null,"polygons","polygons",-266433925));
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.leaflet.Polygon,cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"positions","positions",-1380538434),cljs.core.clj__GT_js.call(null,cljs.core.map.call(null,new cljs.core.Keyword(null,"coordinates","coordinates",-1225332668),polygons))], null),style_options)], null);
}));
cljs.core._add_method.call(null,ote.ui.leaflet.geometry,new cljs.core.Keyword(null,"polygon","polygon",837053759),(function (style_options,p__52074){
var map__52075 = p__52074;
var map__52075__$1 = ((((!((map__52075 == null)))?((((map__52075.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52075.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52075):map__52075);
var coordinates = cljs.core.get.call(null,map__52075__$1,new cljs.core.Keyword(null,"coordinates","coordinates",-1225332668));
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.leaflet.Polygon,cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"positions","positions",-1380538434),cljs.core.clj__GT_js.call(null,coordinates)], null),style_options)], null);
}));
ote.ui.leaflet.update_map_bounds_from_layers = (function ote$ui$leaflet$update_map_bounds_from_layers(leaflet){
var bounds = cljs.core.atom.call(null,null);
var add_bounds_BANG_ = ((function (bounds){
return (function (nw,se){
var new_bounds = L.latLngBounds(nw,se);
if((cljs.core.deref.call(null,bounds) == null)){
return cljs.core.reset_BANG_.call(null,bounds,new_bounds);
} else {
return cljs.core.deref.call(null,bounds).extend(new_bounds);
}
});})(bounds))
;
leaflet.eachLayer(((function (bounds,add_bounds_BANG_){
return (function (layer){
if((layer instanceof L.Path)){
var path = layer;
var layer_bounds = path.getBounds();
return add_bounds_BANG_.call(null,layer_bounds.getNorthWest(),layer_bounds.getSouthEast());
} else {
if((layer instanceof L.Marker)){
var marker = layer;
var pos = marker.getLatLng();
var d = 0.01;
var lat = pos.lat;
var lng = pos.lng;
return add_bounds_BANG_.call(null,L.latLng((lat - d),(lng - d)),L.latLng((lat + d),(lng + d)));
} else {
return null;

}
}
});})(bounds,add_bounds_BANG_))
);

var temp__5290__auto__ = cljs.core.deref.call(null,bounds);
if(cljs.core.truth_(temp__5290__auto__)){
var bounds__$1 = temp__5290__auto__;
return leaflet.fitBounds(bounds__$1);
} else {
return null;
}
});
goog.exportSymbol('ote.ui.leaflet.update_map_bounds_from_layers', ote.ui.leaflet.update_map_bounds_from_layers);
ote.ui.leaflet.update_bounds_from_layers = (function ote$ui$leaflet$update_bounds_from_layers(this$){
var leaflet = (this$["refs"]["leaflet"]["leafletElement"]);
return ote.ui.leaflet.update_map_bounds_from_layers.call(null,leaflet);
});
ote.ui.leaflet.update_bounds_on_load = (function ote$ui$leaflet$update_bounds_on_load(this$){
var leaflet = (this$["refs"]["leaflet"]["leafletElement"]);
return leaflet.on("layeradd",((function (leaflet){
return (function (m){
return ote.ui.leaflet.update_map_bounds_from_layers.call(null,leaflet);
});})(leaflet))
);
});

//# sourceMappingURL=leaflet.js.map?rel=1510137294044
