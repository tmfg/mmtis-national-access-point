// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.main');
goog.require('cljs.core');
goog.require('reagent.core');
goog.require('cljsjs.material_ui');
goog.require('cljs_react_material_ui.core');
goog.require('cljs_react_material_ui.reagent');
goog.require('cljs_react_material_ui.icons');
goog.require('cljsjs.react_leaflet');
goog.require('cljsjs.leaflet_draw');
goog.require('tuck.core');
goog.require('ote.app.state');
goog.require('ote.views.main');
goog.require('ote.views.ckan_service_viewer');
goog.require('ote.views.ckan_org_viewer');
goog.require('ote.views.ckan_org_editor');
goog.require('ote.localization');
goog.require('ote.app.routes');
goog.require('stylefy.core');
goog.require('ote.communication');
ote.main.main = (function ote$main$main(){
return ote.localization.load_language_BANG_.call(null,new cljs.core.Keyword(null,"fi","fi",-118863964),(function (lang,_){
cljs.core.reset_BANG_.call(null,ote.localization.selected_language,lang);

stylefy.core.init.call(null);

ote.app.routes.start_BANG_.call(null);

return reagent.core.render_component.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [tuck.core.tuck,ote.app.state.app,ote.views.main.ote_application], null),document.getElementById("oteapp"));
}));
});
goog.exportSymbol('ote.main.main', ote.main.main);
ote.main.reload_hook = (function ote$main$reload_hook(){
return reagent.core.force_update_all.call(null);
});
goog.exportSymbol('ote.main.reload_hook', ote.main.reload_hook);
ote.main.geojson_view = (function ote$main$geojson_view(){
ote.communication.set_base_url_BANG_.call(null,"/ote/");

return ote.localization.load_language_BANG_.call(null,new cljs.core.Keyword(null,"fi","fi",-118863964),(function (lang,_){
cljs.core.reset_BANG_.call(null,ote.localization.selected_language,lang);

stylefy.core.init.call(null);

return reagent.core.render_component.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [tuck.core.tuck,ote.app.state.viewer,ote.views.ckan_service_viewer.viewer], null),document.getElementById("nap_viewer"));
}));
});
goog.exportSymbol('ote.main.geojson_view', ote.main.geojson_view);
ote.main.ckan_org_view = (function ote$main$ckan_org_view(){
ote.communication.set_base_url_BANG_.call(null,"/ote/");

return ote.localization.load_language_BANG_.call(null,new cljs.core.Keyword(null,"fi","fi",-118863964),(function (lang,_){
cljs.core.reset_BANG_.call(null,ote.localization.selected_language,lang);

stylefy.core.init.call(null);

return reagent.core.render_component.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [tuck.core.tuck,ote.app.state.app,ote.views.ckan_org_viewer.viewer], null),document.getElementById("nap_viewer"));
}));
});
goog.exportSymbol('ote.main.ckan_org_view', ote.main.ckan_org_view);
ote.main.ckan_org_edit = (function ote$main$ckan_org_edit(){
ote.communication.set_base_url_BANG_.call(null,"/ote/");

return ote.localization.load_language_BANG_.call(null,new cljs.core.Keyword(null,"fi","fi",-118863964),(function (lang,_){
cljs.core.reset_BANG_.call(null,ote.localization.selected_language,lang);

stylefy.core.init.call(null);

return reagent.core.render_component.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [tuck.core.tuck,ote.app.state.app,ote.views.ckan_org_editor.editor], null),document.getElementById("nap_viewer"));
}));
});
goog.exportSymbol('ote.main.ckan_org_edit', ote.main.ckan_org_edit);

//# sourceMappingURL=main.js.map?rel=1510647245476
