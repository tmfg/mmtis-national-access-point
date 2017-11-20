// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.ui.buttons');
goog.require('cljs.core');
goog.require('cljs_react_material_ui.reagent');
goog.require('cljs_react_material_ui.core');
goog.require('stylefy.core');
goog.require('ote.style.base');
ote.ui.buttons.button_container = (function ote$ui$buttons$button_container(button){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),stylefy.core.use_style.call(null,ote.style.base.action_button_container),button], null);
});
ote.ui.buttons.save = (function ote$ui$buttons$save(opts,label){
var button = ((cljs.core._EQ_.call(null,cljs.core.get.call(null,opts,new cljs.core.Keyword(null,"disabled","disabled",-1529784218)),false))?new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.raised_button,cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"button-style","button-style",314949943),ote.style.base.base_button], null),opts),label], null):new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.raised_button,cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"button-style","button-style",314949943),ote.style.base.disabled_button,new cljs.core.Keyword(null,"disabled","disabled",-1529784218),true], null),opts),label], null));
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.buttons.button_container,button], null);
});
ote.ui.buttons.cancel = (function ote$ui$buttons$cancel(opts,label){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.buttons.button_container,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.flat_button,cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"button-style","button-style",314949943),ote.style.base.base_button,new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"padding-left","padding-left",-1180879053),"1.1em",new cljs.core.Keyword(null,"padding-right","padding-right",-1250249681),"1.1em",new cljs.core.Keyword(null,"text-transform","text-transform",1685000676),"uppercase",new cljs.core.Keyword(null,"color","color",1011675173),cljs_react_material_ui.core.color.call(null,new cljs.core.Keyword(null,"blue700","blue700",-750290411)),new cljs.core.Keyword(null,"font-size","font-size",-1847940346),"12px",new cljs.core.Keyword(null,"font-weight","font-weight",2085804583),"bold"], null)], null),opts),label], null)], null);
});

//# sourceMappingURL=buttons.js.map?rel=1510137273071
