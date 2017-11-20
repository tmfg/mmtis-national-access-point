// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.transit');
goog.require('cljs.core');
goog.require('cognitect.transit');
goog.require('ote.time');
ote.transit.write_options = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"handlers","handlers",79528781),cljs.core.PersistentArrayMap.createAsIfByAssoc([ote.time.Time,cognitect.transit.write_handler.call(null,cljs.core.constantly.call(null,"time"),ote.time.format_time)])], null);
ote.transit.read_options = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"handlers","handlers",79528781),new cljs.core.PersistentArrayMap(null, 2, ["time",ote.time.parse_time,"f",parseFloat], null)], null);
/**
 * Convert given Clojure `data` to transit+json string.
 */
ote.transit.clj__GT_transit = (function ote$transit$clj__GT_transit(data){
return cognitect.transit.write.call(null,cognitect.transit.writer.call(null,new cljs.core.Keyword(null,"json","json",1279968570),ote.transit.write_options),data);
});
/**
 * Parse transit+json `in` to Clojure data.
 */
ote.transit.transit__GT_clj = (function ote$transit$transit__GT_clj(in$){
return cognitect.transit.read.call(null,cognitect.transit.reader.call(null,new cljs.core.Keyword(null,"json","json",1279968570),ote.transit.read_options),in$);
});

//# sourceMappingURL=transit.js.map?rel=1510137279140
