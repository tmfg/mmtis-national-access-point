// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.style.form');
goog.require('cljs.core');
goog.require('stylefy.core');
goog.require('garden.units');
ote.style.form.flex_container = (function ote$style$form$flex_container(dir){
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"display","display",242065432),"flex",new cljs.core.Keyword(null,"flex-direction","flex-direction",364609438),dir], null);
});
ote.style.form.form_group_base = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"margin-bottom","margin-bottom",388334941),"1em",new cljs.core.Keyword("stylefy.core","sub-styles","stylefy.core/sub-styles",-1546489432),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"form-field","form-field",-318915722),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"margin","margin",-995903681),"1em"], null)], null)], null);
ote.style.form.form_group_column = cljs.core.merge.call(null,ote.style.form.form_group_base,ote.style.form.flex_container.call(null,"column"));
ote.style.form.form_group_row = cljs.core.merge.call(null,ote.style.form.form_group_base,ote.style.form.flex_container.call(null,"row"),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"flex-wrap","flex-wrap",455413707),"wrap"], null));
ote.style.form.form_group_container = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"padding-bottom","padding-bottom",-1899795591),"0.33em"], null);
ote.style.form.form_info_text = new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"display","display",242065432),"inline-block",new cljs.core.Keyword(null,"position","position",-2011731912),"relative",new cljs.core.Keyword(null,"top","top",-1856271961),"-0.5em"], null);

//# sourceMappingURL=form.js.map?rel=1510137277261
