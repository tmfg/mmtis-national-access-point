// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.localization');
goog.require('cljs.core');
goog.require('reagent.core');
goog.require('ote.communication');
goog.require('cljs.spec.alpha');
goog.require('clojure.string');
goog.require('taoensso.timbre');
if(typeof ote.localization.loaded_languages !== 'undefined'){
} else {
ote.localization.loaded_languages = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
}
/**
 * Load the given language translation file, if it has not been loaded yet, and adds the language
 *   to the `loaded-languages` atom.
 *   Calls `on-load` callback, when loading is done.
 */
ote.localization.load_language_BANG_ = (function ote$localization$load_language_BANG_(language,on_load){
var temp__5288__auto__ = cljs.core.get.call(null,cljs.core.deref.call(null,ote.localization.loaded_languages),language);
if(cljs.core.truth_(temp__5288__auto__)){
var translations = temp__5288__auto__;
return on_load.call(null,language,translations);
} else {
return ote.communication.get_BANG_.call(null,["language/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name.call(null,language))].join(''),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-success","on-success",1786904109),((function (temp__5288__auto__){
return (function (translations){
cljs.core.swap_BANG_.call(null,ote.localization.loaded_languages,cljs.core.assoc,language,translations);

return on_load.call(null,language,translations);
});})(temp__5288__auto__))
], null));
}
});
/**
 * (Re)loads the given language translation file and returns the translations.
 */
ote.localization.translations = (function ote$localization$translations(language){
cljs.core.swap_BANG_.call(null,ote.localization.loaded_languages,cljs.core.dissoc,language);

ote.localization.load_language_BANG_.call(null,language,cljs.core.constantly.call(null,null));

return cljs.core.get.call(null,cljs.core.deref.call(null,ote.localization.loaded_languages),language);
});
if(typeof ote.localization.selected_language !== 'undefined'){
} else {
ote.localization.selected_language = reagent.core.atom.call(null,new cljs.core.Keyword(null,"fi","fi",-118863964));
}

ote.localization.set_language_BANG_ = (function ote$localization$set_language_BANG_(language){
return ote.localization.load_language_BANG_.call(null,language,(function (p1__46955_SHARP_){
return cljs.core.reset_BANG_.call(null,ote.localization.selected_language,p1__46955_SHARP_);
}));
});
ote.localization.message_part = (function ote$localization$message_part(part,parameters){
if((part instanceof cljs.core.Keyword)){
return ote.localization.message_part.call(null,cljs.core.get.call(null,parameters,part),parameters);
} else {
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(part)].join('');

}
});
ote.localization.message = (function ote$localization$message(message_definition,parameters){
if(typeof message_definition === 'string'){
return message_definition;
} else {
return cljs.core.reduce.call(null,(function (acc,part){
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(acc),cljs.core.str.cljs$core$IFn$_invoke$arity$1(ote.localization.message_part.call(null,part,parameters))].join('');
}),"",message_definition);
}
});
/**
 * Returns translation for the given message.
 *   If `language` is provided, use that language, otherwise use the default.
 * 
 *   `message-path` is a vector of keywords path to the translation map.
 * 
 *   Optional `parameters` give values to replaceable parts in the message.
 */
ote.localization.tr = (function ote$localization$tr(var_args){
var G__46957 = arguments.length;
switch (G__46957) {
case 1:
return ote.localization.tr.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return ote.localization.tr.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return ote.localization.tr.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

ote.localization.tr.cljs$core$IFn$_invoke$arity$1 = (function (message_path){
return ote.localization.tr.call(null,cljs.core.deref.call(null,ote.localization.selected_language),message_path,cljs.core.PersistentArrayMap.EMPTY);
});

ote.localization.tr.cljs$core$IFn$_invoke$arity$2 = (function (message_path,parameters){
return ote.localization.tr.call(null,cljs.core.deref.call(null,ote.localization.selected_language),message_path,cljs.core.PersistentArrayMap.EMPTY);
});

ote.localization.tr.cljs$core$IFn$_invoke$arity$3 = (function (language,message_path,parameters){
var language__$1 = cljs.core.get.call(null,cljs.core.deref.call(null,ote.localization.loaded_languages),language);
if(cljs.core.truth_(language__$1)){
} else {
throw (new Error(["Assert failed: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(["Language ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(language__$1)," has not been loaded."].join('')),"\n","language"].join('')));
}

return ote.localization.message.call(null,cljs.core.get_in.call(null,language__$1,message_path),parameters);
});

ote.localization.tr.cljs$lang$maxFixedArity = 3;

cljs.spec.alpha.def_impl.call(null,new cljs.core.Symbol("ote.localization","tr-key","ote.localization/tr-key",-741737339,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"path","path",-188191168),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),new cljs.core.Symbol("cljs.core","keyword?","cljs.core/keyword?",713156450,null))),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("cljs.core","fn?","cljs.core/fn?",71876239,null)),cljs.spec.alpha.fspec_impl.call(null,cljs.spec.alpha.spec_impl.call(null,cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"path","path",-188191168),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),new cljs.core.Symbol("cljs.core","keyword?","cljs.core/keyword?",713156450,null))),cljs.spec.alpha.cat_impl.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"path","path",-188191168)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.spec.alpha.every_impl.call(null,new cljs.core.Symbol(null,"keyword?","keyword?",1917797069,null),cljs.core.keyword_QMARK_,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword("cljs.spec.alpha","kind-form","cljs.spec.alpha/kind-form",-1047104697),null,new cljs.core.Keyword("cljs.spec.alpha","cpred","cljs.spec.alpha/cpred",-693471218),(function (G__46959){
return cljs.core.coll_QMARK_.call(null,G__46959);
}),new cljs.core.Keyword("cljs.spec.alpha","conform-all","cljs.spec.alpha/conform-all",45201917),true,new cljs.core.Keyword("cljs.spec.alpha","describe","cljs.spec.alpha/describe",1883026911),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),new cljs.core.Symbol("cljs.core","keyword?","cljs.core/keyword?",713156450,null))], null),null)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),new cljs.core.Symbol("cljs.core","keyword?","cljs.core/keyword?",713156450,null))], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"path","path",-188191168),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","coll-of","cljs.spec.alpha/coll-of",1019430407,null),new cljs.core.Symbol("cljs.core","keyword?","cljs.core/keyword?",713156450,null))),cljs.spec.alpha.spec_impl.call(null,new cljs.core.Symbol("cljs.core","fn?","cljs.core/fn?",71876239,null),cljs.core.fn_QMARK_,null,null),new cljs.core.Symbol("cljs.core","fn?","cljs.core/fn?",71876239,null),null,null,null));
/**
 * Returns a function that translates a keyword under the given `path`.
 *   This is useful for creating a formatting function for keyword enumerations.
 *   Multiple paths may be provided and they are tried in the given order. First
 *   path that has a translation for the requested key, is used.
 */
ote.localization.tr_key = (function ote$localization$tr_key(var_args){
var args__31459__auto__ = [];
var len__31452__auto___46962 = arguments.length;
var i__31453__auto___46963 = (0);
while(true){
if((i__31453__auto___46963 < len__31452__auto___46962)){
args__31459__auto__.push((arguments[i__31453__auto___46963]));

var G__46964 = (i__31453__auto___46963 + (1));
i__31453__auto___46963 = G__46964;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((0) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((0)),(0),null)):null);
return ote.localization.tr_key.cljs$core$IFn$_invoke$arity$variadic(argseq__31460__auto__);
});

ote.localization.tr_key.cljs$core$IFn$_invoke$arity$variadic = (function (paths){
return (function (key){
var or__30175__auto__ = cljs.core.some.call(null,(function (p1__46960_SHARP_){
var message = ote.localization.tr.call(null,cljs.core.conj.call(null,p1__46960_SHARP_,key));
if(clojure.string.blank_QMARK_.call(null,message)){
return null;
} else {
return message;
}
}),paths);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return "";
}
});
});

ote.localization.tr_key.cljs$lang$maxFixedArity = (0);

ote.localization.tr_key.cljs$lang$applyTo = (function (seq46961){
return ote.localization.tr_key.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq46961));
});

/**
 * Utility for returning a default when a translation is not found.
 */
ote.localization.tr_or = (function ote$localization$tr_or(tr_result,default_value){
if(cljs.core._EQ_.call(null,"",tr_result)){
return default_value;
} else {
return tr_result;
}
});

//# sourceMappingURL=localization.js.map?rel=1510137284731
