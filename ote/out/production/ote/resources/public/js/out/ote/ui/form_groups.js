// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.ui.form_groups');
goog.require('cljs.core');
goog.require('ote.ui.form');
goog.require('ote.db.common');
goog.require('ote.db.transport_service');
goog.require('ote.ui.buttons');
goog.require('stylefy.core');
goog.require('ote.style.base');
goog.require('ote.localization');
/**
 * Creates a form group for address that creates three form elements street, post-office and postal-code.
 */
ote.ui.form_groups.address = (function ote$ui$form_groups$address(label,address_field){
return ote.ui.form.group.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"label","label",1718410804),label,new cljs.core.Keyword(null,"layout","layout",-2120940921),new cljs.core.Keyword(null,"row","row",-570139521),new cljs.core.Keyword(null,"columns","columns",1998437288),(3)], null),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword("ote.db.common","street","ote.db.common/street",-433179772),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"string","string",-1989541586),new cljs.core.Keyword(null,"read","read",1140058661),cljs.core.comp.call(null,new cljs.core.Keyword("ote.db.common","street","ote.db.common/street",-433179772),address_field),new cljs.core.Keyword(null,"write","write",-1857649168),(function (data,street){
return cljs.core.assoc_in.call(null,data,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [address_field,new cljs.core.Keyword("ote.db.common","street","ote.db.common/street",-433179772)], null),street);
})], null),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword("ote.db.common","post-office","ote.db.common/post-office",1892018074),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"string","string",-1989541586),new cljs.core.Keyword(null,"read","read",1140058661),cljs.core.comp.call(null,new cljs.core.Keyword("ote.db.common","post_office","ote.db.common/post_office",994976709),address_field),new cljs.core.Keyword(null,"write","write",-1857649168),(function (data,post_office){
return cljs.core.assoc_in.call(null,data,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [address_field,new cljs.core.Keyword("ote.db.common","post-office","ote.db.common/post-office",1892018074)], null),post_office);
})], null),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword("ote.db.common","postal-code","ote.db.common/postal-code",-2009063298),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"string","string",-1989541586),new cljs.core.Keyword(null,"read","read",1140058661),cljs.core.comp.call(null,new cljs.core.Keyword("ote.db.common","postal_code","ote.db.common/postal_code",1541120368),address_field),new cljs.core.Keyword(null,"write","write",-1857649168),(function (data,postal_code){
return cljs.core.assoc_in.call(null,data,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [address_field,new cljs.core.Keyword("ote.db.common","postal-code","ote.db.common/postal-code",-2009063298)], null),postal_code);
})], null));
});

//# sourceMappingURL=form_groups.js.map?rel=1510137291864
