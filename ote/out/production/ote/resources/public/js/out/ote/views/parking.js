// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.views.parking');
goog.require('cljs.core');
goog.require('reagent.core');
goog.require('cljs_react_material_ui.reagent');
goog.require('cljs_react_material_ui.icons');
goog.require('ote.ui.form');
goog.require('ote.ui.form_groups');
goog.require('ote.ui.buttons');
goog.require('ote.app.controller.transport_service');
goog.require('ote.app.controller.parking');
goog.require('ote.db.transport_service');
goog.require('ote.db.common');
goog.require('ote.localization');
goog.require('ote.views.place_search');
goog.require('tuck.core');
ote.views.parking.parking_form_options = (function ote$views$parking$parking_form_options(e_BANG_){
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"name->label","name->label",-1609632952),ote.localization.tr_key.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"field-labels","field-labels",1197457113),new cljs.core.Keyword(null,"terminal","terminal",-927870592)], null)),new cljs.core.Keyword(null,"update!","update!",-1453508586),(function (p1__52554_SHARP_){
return e_BANG_.call(null,ote.app.controller.parking.__GT_EditParkingState.call(null,p1__52554_SHARP_));
}),new cljs.core.Keyword(null,"name","name",1843675177),(function (p1__52555_SHARP_){
return ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"olennaiset-tiedot","olennaiset-tiedot",-632983272),new cljs.core.Keyword(null,"otsikot","otsikot",-259055228),p1__52555_SHARP_], null));
}),new cljs.core.Keyword(null,"footer-fn","footer-fn",1907236041),(function (data){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.buttons.save,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (){
return e_BANG_.call(null,ote.app.controller.parking.__GT_SaveParkingToDb.call(null));
}),new cljs.core.Keyword(null,"disabled","disabled",-1529784218),ote.ui.form.disable_save_QMARK_.call(null,data)], null),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"buttons","buttons",-1953831197),new cljs.core.Keyword(null,"save","save",1850079149)], null))], null);
})], null);
});
ote.views.parking.parking = (function ote$views$parking$parking(e_BANG_,status){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.row","div.row",133678515),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"class","class",-2030961996),"col-lg-12"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),"T\u00E4ydenn\u00E4 pys\u00E4k\u00F6intiin liittyv\u00E4t tiedot."], null)], null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.form.form,ote.views.parking.parking_form_options.call(null,e_BANG_),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.form.group.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"label","label",1718410804),"Pakkipaikka - tai jotain",new cljs.core.Keyword(null,"columns","columns",1998437288),(1)], null),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword("ote.db.transport-service",":eligibility-requirements","ote.db.transport-service/:eligibility-requirements",384156208),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"localized-text","localized-text",191039942)], null))], null),cljs.core.get.call(null,status,new cljs.core.Keyword("ote.db.transport-service","parking","ote.db.transport-service/parking",1703320291))], null)], null)], null);
});

//# sourceMappingURL=parking.js.map?rel=1510137295959
