// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.views.terminal');
goog.require('cljs.core');
goog.require('reagent.core');
goog.require('cljs_react_material_ui.reagent');
goog.require('cljs_react_material_ui.icons');
goog.require('ote.ui.form');
goog.require('ote.ui.form_groups');
goog.require('ote.ui.buttons');
goog.require('ote.app.controller.terminal');
goog.require('ote.db.transport_service');
goog.require('ote.db.common');
goog.require('ote.localization');
goog.require('ote.views.place_search');
goog.require('tuck.core');
goog.require('ote.style.base');
goog.require('ote.app.controller.transport_service');
goog.require('ote.views.transport_service_common');
ote.views.terminal.terminal_form_options = (function ote$views$terminal$terminal_form_options(e_BANG_){
return new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"name->label","name->label",-1609632952),ote.localization.tr_key.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"field-labels","field-labels",1197457113),new cljs.core.Keyword(null,"terminal","terminal",-927870592)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"field-labels","field-labels",1197457113),new cljs.core.Keyword(null,"transport-service-common","transport-service-common",-1386295207)], null)),new cljs.core.Keyword(null,"update!","update!",-1453508586),(function (p1__52181_SHARP_){
return e_BANG_.call(null,ote.app.controller.terminal.__GT_EditTerminalState.call(null,p1__52181_SHARP_));
}),new cljs.core.Keyword(null,"name","name",1843675177),(function (p1__52182_SHARP_){
return ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"olennaiset-tiedot","olennaiset-tiedot",-632983272),new cljs.core.Keyword(null,"otsikot","otsikot",-259055228),p1__52182_SHARP_], null));
}),new cljs.core.Keyword(null,"footer-fn","footer-fn",1907236041),(function (data){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.transport_service_common.footer,e_BANG_,data], null);
})], null);
});
ote.views.terminal.name_and_type_group = (function ote$views$terminal$name_and_type_group(e_BANG_){
return ote.ui.form.group.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"label","label",1718410804),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"terminal-page","terminal-page",341453917),new cljs.core.Keyword(null,"header-service-info","header-service-info",715153940)], null)),new cljs.core.Keyword(null,"columns","columns",1998437288),(3),new cljs.core.Keyword(null,"layout","layout",-2120940921),new cljs.core.Keyword(null,"row","row",-570139521)], null),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword("ote.db.transport-service","name","ote.db.transport-service/name",1219699930),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"string","string",-1989541586),new cljs.core.Keyword(null,"required?","required?",-872514462),true], null));
});
ote.views.terminal.indoor_map_group = (function ote$views$terminal$indoor_map_group(){
return ote.views.transport_service_common.service_url.call(null,ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"field-labels","field-labels",1197457113),new cljs.core.Keyword(null,"terminal","terminal",-927870592),new cljs.core.Keyword("ote.db.transport-service","indoor-map","ote.db.transport-service/indoor-map",-2030107585)], null)),new cljs.core.Keyword("ote.db.transport-service","indoor-map","ote.db.transport-service/indoor-map",-2030107585));
});
ote.views.terminal.accessibility_and_other_services_group = (function ote$views$terminal$accessibility_and_other_services_group(){
return ote.ui.form.group.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"label","label",1718410804),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"terminal-page","terminal-page",341453917),new cljs.core.Keyword(null,"header-other-services-and-accessibility","header-other-services-and-accessibility",-386172379)], null)),new cljs.core.Keyword(null,"columns","columns",1998437288),(3),new cljs.core.Keyword(null,"layout","layout",-2120940921),new cljs.core.Keyword(null,"row","row",-570139521)], null),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"style","style",-496642736),ote.style.base.long_drowpdown,new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword("ote.db.transport-service","information-service-accessibility","ote.db.transport-service/information-service-accessibility",561461408),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"multiselect-selection","multiselect-selection",-472179423),new cljs.core.Keyword(null,"show-option","show-option",1962057502),ote.localization.tr_key.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"enums","enums",-1800115173),new cljs.core.Keyword("ote.db.transport-service","information-service-accessibility","ote.db.transport-service/information-service-accessibility",561461408)], null)),new cljs.core.Keyword(null,"options","options",99638489),ote.db.transport_service.information_service_accessibility], null),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"style","style",-496642736),ote.style.base.long_drowpdown,new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword("ote.db.transport-service","accessibility-tool","ote.db.transport-service/accessibility-tool",-353691742),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"multiselect-selection","multiselect-selection",-472179423),new cljs.core.Keyword(null,"show-option","show-option",1962057502),ote.localization.tr_key.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"enums","enums",-1800115173),new cljs.core.Keyword("ote.db.transport-service","accessibility-tool","ote.db.transport-service/accessibility-tool",-353691742)], null)),new cljs.core.Keyword(null,"options","options",99638489),ote.db.transport_service.accessibility_tool], null),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"style","style",-496642736),ote.style.base.long_drowpdown,new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword("ote.db.transport-service","accessibility","ote.db.transport-service/accessibility",-447377739),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"multiselect-selection","multiselect-selection",-472179423),new cljs.core.Keyword(null,"show-option","show-option",1962057502),ote.localization.tr_key.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"enums","enums",-1800115173),new cljs.core.Keyword("ote.db.transport-service","accessibility","ote.db.transport-service/accessibility",-447377739)], null)),new cljs.core.Keyword(null,"options","options",99638489),ote.db.transport_service.accessibility], null),new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"style","style",-496642736),ote.style.base.long_drowpdown,new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword("ote.db.transport-service","mobility","ote.db.transport-service/mobility",2082004423),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"multiselect-selection","multiselect-selection",-472179423),new cljs.core.Keyword(null,"show-option","show-option",1962057502),ote.localization.tr_key.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"enums","enums",-1800115173),new cljs.core.Keyword("ote.db.transport-service","mobility","ote.db.transport-service/mobility",2082004423)], null)),new cljs.core.Keyword(null,"options","options",99638489),ote.db.transport_service.mobility], null));
});
ote.views.terminal.accessibility_description_group = (function ote$views$terminal$accessibility_description_group(){
return ote.ui.form.group.call(null,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"label","label",1718410804),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"terminal-page","terminal-page",341453917),new cljs.core.Keyword(null,"header-accessibility-description","header-accessibility-description",-1147711894)], null)),new cljs.core.Keyword(null,"columns","columns",1998437288),(3),new cljs.core.Keyword(null,"layout","layout",-2120940921),new cljs.core.Keyword(null,"row","row",-570139521)], null),new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword("ote.db.transport-service","accessibility-description","ote.db.transport-service/accessibility-description",-1508725838),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"localized-text","localized-text",191039942),new cljs.core.Keyword(null,"rows","rows",850049680),(1),new cljs.core.Keyword(null,"max-rows","max-rows",-2131113613),(5)], null));
});
ote.views.terminal.terminal = (function ote$views$terminal$terminal(e_BANG_,p__52183){
var map__52184 = p__52183;
var map__52184__$1 = ((((!((map__52184 == null)))?((((map__52184.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52184.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52184):map__52184);
var form_data = cljs.core.get.call(null,map__52184__$1,new cljs.core.Keyword("ote.db.transport-service","terminal","ote.db.transport-service/terminal",769260653));
var with_let52186 = reagent.ratom.with_let_values.call(null,new cljs.core.Keyword(null,"with-let52186","with-let52186",-160780955));
var temp__5294__auto___52188 = reagent.ratom._STAR_ratom_context_STAR_;
if((temp__5294__auto___52188 == null)){
} else {
var c__32995__auto___52189 = temp__5294__auto___52188;
if((with_let52186.generation === c__32995__auto___52189.ratomGeneration)){
if(cljs.core.truth_(reagent.debug.has_console)){
(cljs.core.truth_(reagent.debug.tracking)?reagent.debug.track_console:console).error(["Warning: The same with-let is being used more ","than once in the same reactive context."].join(''));
} else {
}
} else {
}

with_let52186.generation = c__32995__auto___52189.ratomGeneration;
}


var init52187 = (with_let52186.length === (0));
var options = ((init52187)?(with_let52186[(0)] = ote.views.terminal.terminal_form_options.call(null,e_BANG_)):(with_let52186[(0)]));
var groups = ((init52187)?(with_let52186[(1)] = new cljs.core.PersistentVector(null, 7, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.terminal.name_and_type_group.call(null,e_BANG_),ote.views.transport_service_common.contact_info_group.call(null),ote.views.transport_service_common.place_search_group.call(null,e_BANG_,new cljs.core.Keyword("ote.db.transport-service","terminal","ote.db.transport-service/terminal",769260653)),ote.views.transport_service_common.external_interfaces.call(null),ote.views.terminal.indoor_map_group.call(null),ote.views.terminal.accessibility_and_other_services_group.call(null),ote.views.terminal.accessibility_description_group.call(null)], null)):(with_let52186[(1)]));
var res__32996__auto__ = new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.row","div.row",133678515),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"class","class",-2030961996),"col-lg-12"], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3","h3",2067611163),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"terminal-page","terminal-page",341453917),new cljs.core.Keyword(null,"header-add-new-terminal","header-add-new-terminal",2045654457)], null))], null)], null),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.form.form,options,groups,form_data], null)], null)], null);

return res__32996__auto__;
});

//# sourceMappingURL=terminal.js.map?rel=1510137294518
