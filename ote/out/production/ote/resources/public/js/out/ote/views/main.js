// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.views.main');
goog.require('cljs.core');
goog.require('reagent.core');
goog.require('cljs_react_material_ui.reagent');
goog.require('cljs_react_material_ui.core');
goog.require('cljs_react_material_ui.icons');
goog.require('ote.views.transport_operator');
goog.require('ote.views.front_page');
goog.require('ote.app.controller.front_page');
goog.require('ote.views.transport_service');
goog.require('ote.views.passenger_transportation');
goog.require('ote.views.terminal');
goog.require('ote.views.rental');
goog.require('ote.views.parking');
goog.require('ote.views.brokerage');
goog.require('ote.localization');
goog.require('ote.views.place_search');
goog.require('ote.ui.debug');
goog.require('stylefy.core');
goog.require('ote.style.base');
goog.require('ote.app.controller.transport_service');
goog.require('ote.views.theme');
ote.views.main.is_topnav_active = (function ote$views$main$is_topnav_active(give_page,nav_page){
if(cljs.core._EQ_.call(null,give_page,nav_page)){
return "active";
} else {
return null;
}
});
ote.views.main.is_user_menu_active = (function ote$views$main$is_user_menu_active(app){
if(cljs.core._EQ_.call(null,true,cljs.core.get_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ote-service-flags","ote-service-flags",-965917048),new cljs.core.Keyword(null,"user-menu-open","user-menu-open",-1786787308)], null)))){
return "active";
} else {
return null;
}
});
ote.views.main.user_menu = (function ote$views$main$user_menu(e_BANG_,user_name){
return new cljs.core.PersistentVector(null, 8, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.select_field,new cljs.core.PersistentArrayMap(null, 5, [new cljs.core.Keyword(null,"value","value",305978217),(0),new cljs.core.Keyword(null,"label-style","label-style",-1703650121),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"color","color",1011675173),"#f2f2f2"], null),new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (){
return e_BANG_.call(null,ote.app.controller.front_page.__GT_OpenUserMenu.call(null));
}),new cljs.core.Keyword(null,"anchor-origin","anchor-origin",-1341383609),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"horizontal","horizontal",2062109475),"right",new cljs.core.Keyword(null,"vertical","vertical",718696748),"bottom"], null),new cljs.core.Keyword(null,"target-origin","target-origin",-864171810),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"horizontal","horizontal",2062109475),"right",new cljs.core.Keyword(null,"vertical","vertical",718696748),"top"], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.menu_item,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"value","value",305978217),(0),new cljs.core.Keyword(null,"primary-text","primary-text",146474209),user_name,new cljs.core.Keyword(null,"selected-text-color","selected-text-color",-2046274829),cljs_react_material_ui.core.color.call(null,new cljs.core.Keyword(null,"grey900","grey900",-712169247)),new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (){
return e_BANG_.call(null,ote.app.controller.front_page.__GT_ChangePage.call(null,new cljs.core.Keyword(null,"front-page","front-page",-663760939)));
})], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.menu_item,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"primary-text","primary-text",146474209),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"common-texts","common-texts",-934994303),new cljs.core.Keyword(null,"user-menu-service-guide","user-menu-service-guide",1699925880)], null)),new cljs.core.Keyword(null,"selected-text-color","selected-text-color",-2046274829),cljs_react_material_ui.core.color.call(null,new cljs.core.Keyword(null,"grey900","grey900",-712169247)),new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (){
return e_BANG_.call(null,ote.app.controller.front_page.__GT_ChangePage.call(null,new cljs.core.Keyword(null,"front-page","front-page",-663760939)));
})], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.menu_item,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"primary-text","primary-text",146474209),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"common-texts","common-texts",-934994303),new cljs.core.Keyword(null,"user-menu-service-operator","user-menu-service-operator",-117108565)], null)),new cljs.core.Keyword(null,"selected-text-color","selected-text-color",-2046274829),cljs_react_material_ui.core.color.call(null,new cljs.core.Keyword(null,"grey900","grey900",-712169247)),new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (){
return e_BANG_.call(null,ote.app.controller.front_page.__GT_ChangePage.call(null,new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982)));
})], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.menu_item,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"primary-text","primary-text",146474209)," N\u00E4yt\u00E4 debug state",new cljs.core.Keyword(null,"selected-text-color","selected-text-color",-2046274829),cljs_react_material_ui.core.color.call(null,new cljs.core.Keyword(null,"grey900","grey900",-712169247)),new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (){
return e_BANG_.call(null,ote.app.controller.front_page.__GT_ToggleDebugState.call(null));
})], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.menu_item,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"primary-text","primary-text",146474209),"Siirry NAP -palveluun",new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (_){
return document.location = "/";
})], null)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.menu_item,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"primary-text","primary-text",146474209),"Kirjaudu ulos",new cljs.core.Keyword(null,"selected-text-color","selected-text-color",-2046274829),cljs_react_material_ui.core.color.call(null,new cljs.core.Keyword(null,"grey900","grey900",-712169247)),new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (){
return e_BANG_.call(null,ote.app.controller.front_page.__GT_ChangePage.call(null,new cljs.core.Keyword(null,"front-page","front-page",-663760939)));
})], null)], null)], null);
});
ote.views.main.flash_message = (function ote$views$main$flash_message(msg){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.snackbar,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"open","open",-1763596448),cljs.core.boolean$.call(null,msg),new cljs.core.Keyword(null,"message","message",-406056002),(function (){var or__30175__auto__ = msg;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return "";
}
})(),new cljs.core.Keyword(null,"style","style",-496642736),ote.style.base.flash_message,new cljs.core.Keyword(null,"auto-hide-duration","auto-hide-duration",1058363602),(5000)], null)], null);
});
ote.views.main.top_nav = (function ote$views$main$top_nav(e_BANG_,app){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"class","class",-2030961996),"topnav"], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a.main-icon","a.main-icon",1883520650),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"href","href",-793805698),"#",new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (){
return e_BANG_.call(null,ote.app.controller.front_page.__GT_ChangePage.call(null,new cljs.core.Keyword(null,"front-page","front-page",-663760939)));
})], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"img","img",1442687358),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"src","src",-1651076051),"img/icons/nap-logo.svg"], null)], null)], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a.ote-nav","a.ote-nav",-344812187),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"class","class",-2030961996),ote.views.main.is_topnav_active.call(null,new cljs.core.Keyword(null,"front-page","front-page",-663760939),new cljs.core.Keyword(null,"page","page",849072397).cljs$core$IFn$_invoke$arity$1(app)),new cljs.core.Keyword(null,"href","href",-793805698),"#",new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (){
return e_BANG_.call(null,ote.app.controller.front_page.__GT_ChangePage.call(null,new cljs.core.Keyword(null,"front-page","front-page",-663760939)));
})], null),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"common-texts","common-texts",-934994303),new cljs.core.Keyword(null,"navigation-front-page","navigation-front-page",-737658051)], null))], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"a.ote-nav","a.ote-nav",-344812187),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"class","class",-2030961996),ote.views.main.is_topnav_active.call(null,new cljs.core.Keyword(null,"own-services","own-services",-1593467283),new cljs.core.Keyword(null,"page","page",849072397).cljs$core$IFn$_invoke$arity$1(app)),new cljs.core.Keyword(null,"href","href",-793805698),"#",new cljs.core.Keyword(null,"on-click","on-click",1632826543),(function (){
return e_BANG_.call(null,ote.app.controller.front_page.__GT_ChangePage.call(null,new cljs.core.Keyword(null,"own-services","own-services",-1593467283)));
})], null),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"common-texts","common-texts",-934994303),new cljs.core.Keyword(null,"navigation-own-service-list","navigation-own-service-list",1745902173)], null))], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.user-menu","div.user-menu",1298715053),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"class","class",-2030961996),ote.views.main.is_user_menu_active.call(null,app)], null),reagent.core.as_element.call(null,ote.views.main.user_menu.call(null,e_BANG_,cljs.core.get_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"user","user",1532431356),new cljs.core.Keyword(null,"name","name",1843675177)], null))))], null)], null);
});
/**
 * OTE application main view
 */
ote.views.main.ote_application = (function ote$views$main$ote_application(e_BANG_,app){
e_BANG_.call(null,ote.app.controller.front_page.__GT_GetTransportOperatorData.call(null));

return (function (e_BANG___$1,app__$1){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),stylefy.core.use_style.call(null,ote.style.base.body)], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.theme.theme,new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.ote-sovellus.container-fluid","div.ote-sovellus.container-fluid",1714194430),ote.views.main.top_nav.call(null,e_BANG___$1,app__$1),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),(function (){var G__43618 = new cljs.core.Keyword(null,"page","page",849072397).cljs$core$IFn$_invoke$arity$1(app__$1);
var G__43618__$1 = (((G__43618 instanceof cljs.core.Keyword))?G__43618.fqn:null);
switch (G__43618__$1) {
case "front-page":
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.front_page.front_page,e_BANG___$1,app__$1], null);

break;
case "own-services":
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.front_page.own_services,e_BANG___$1,app__$1], null);

break;
case "transport-service":
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.transport_service.select_service_type,e_BANG___$1,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706).cljs$core$IFn$_invoke$arity$1(app__$1)], null);

break;
case "transport-operator":
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.transport_operator.operator,e_BANG___$1,new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982).cljs$core$IFn$_invoke$arity$1(app__$1)], null);

break;
case "passenger-transportation":
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.passenger_transportation.passenger_transportation_info,e_BANG___$1,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706).cljs$core$IFn$_invoke$arity$1(app__$1)], null);

break;
case "terminal":
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.terminal.terminal,e_BANG___$1,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706).cljs$core$IFn$_invoke$arity$1(app__$1)], null);

break;
case "rentals":
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.rental.rental,e_BANG___$1,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706).cljs$core$IFn$_invoke$arity$1(app__$1)], null);

break;
case "parking":
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.parking.parking,e_BANG___$1,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706).cljs$core$IFn$_invoke$arity$1(app__$1)], null);

break;
case "brokerage":
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.brokerage.brokerage,e_BANG___$1,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706).cljs$core$IFn$_invoke$arity$1(app__$1)], null);

break;
case "edit-service":
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.transport_service.edit_service,e_BANG___$1,app__$1], null);

break;
default:
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),"ERROR: no such page ",cljs.core.pr_str.call(null,new cljs.core.Keyword(null,"page","page",849072397).cljs$core$IFn$_invoke$arity$1(app__$1))], null);

}
})()], null),(function (){var temp__5290__auto__ = new cljs.core.Keyword(null,"flash-message","flash-message",51770715).cljs$core$IFn$_invoke$arity$1(app__$1);
if(cljs.core.truth_(temp__5290__auto__)){
var msg = temp__5290__auto__;
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.views.main.flash_message,msg], null);
} else {
return null;
}
})(),((cljs.core._EQ_.call(null,true,cljs.core.get_in.call(null,app__$1,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ote-service-flags","ote-service-flags",-965917048),new cljs.core.Keyword(null,"show-debug","show-debug",267843982)], null))))?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.row","div.row",133678515),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.debug.debug,app__$1], null)], null):null)], null)], null)], null);
});
});

//# sourceMappingURL=main.js.map?rel=1510647245448
