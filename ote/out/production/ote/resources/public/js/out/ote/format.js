// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.format');
goog.require('cljs.core');
goog.require('cljs_time.format');
ote.format.pvm_format = cljs_time.format.formatter.call(null,"dd.MM.yyyy");
/**
 * Formatoi päivämäärän suomalaisessa muodossa pp.kk.vvvv
 */
ote.format.pvm = (function ote$format$pvm(date){
return cljs_time.format.unparse.call(null,ote.format.pvm_format,date);
});

//# sourceMappingURL=format.js.map?rel=1510137277753
