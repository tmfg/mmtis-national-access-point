// Compiled by ClojureScript 1.9.908 {}
goog.provide('garden.stylesheet');
goog.require('cljs.core');
goog.require('garden.util');
goog.require('garden.color');
goog.require('garden.types');
/**
 * Create a rule function for the given selector. The `selector`
 *   argument must be valid selector (ie. a keyword, string, or symbol).
 *   Additional arguments may consist of extra selectors or
 *   declarations.
 * 
 *   The returned function accepts any number of arguments which represent
 *   the rule's children.
 * 
 *   Ex.
 *    (let [text-field (rule "[type="text"])]
 *     (text-field {:border ["1px" :solid "black"]}))
 *    ;; => ["[type="text"] {:boder ["1px" :solid "black"]}]
 */
garden.stylesheet.rule = (function garden$stylesheet$rule(var_args){
var args__31459__auto__ = [];
var len__31452__auto___33914 = arguments.length;
var i__31453__auto___33915 = (0);
while(true){
if((i__31453__auto___33915 < len__31452__auto___33914)){
args__31459__auto__.push((arguments[i__31453__auto___33915]));

var G__33916 = (i__31453__auto___33915 + (1));
i__31453__auto___33915 = G__33916;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return garden.stylesheet.rule.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

garden.stylesheet.rule.cljs$core$IFn$_invoke$arity$variadic = (function (selector,more){
if(!(((selector instanceof cljs.core.Keyword)) || (typeof selector === 'string') || ((selector instanceof cljs.core.Symbol)))){
throw cljs.core.ex_info.call(null,"Selector must be either a keyword, string, or symbol.",cljs.core.PersistentArrayMap.EMPTY);
} else {
return (function() { 
var G__33917__delegate = function (children){
return cljs.core.into.call(null,cljs.core.apply.call(null,cljs.core.vector,selector,more),children);
};
var G__33917 = function (var_args){
var children = null;
if (arguments.length > 0) {
var G__33918__i = 0, G__33918__a = new Array(arguments.length -  0);
while (G__33918__i < G__33918__a.length) {G__33918__a[G__33918__i] = arguments[G__33918__i + 0]; ++G__33918__i;}
  children = new cljs.core.IndexedSeq(G__33918__a,0,null);
} 
return G__33917__delegate.call(this,children);};
G__33917.cljs$lang$maxFixedArity = 0;
G__33917.cljs$lang$applyTo = (function (arglist__33919){
var children = cljs.core.seq(arglist__33919);
return G__33917__delegate(children);
});
G__33917.cljs$core$IFn$_invoke$arity$variadic = G__33917__delegate;
return G__33917;
})()
;
}
});

garden.stylesheet.rule.cljs$lang$maxFixedArity = (1);

garden.stylesheet.rule.cljs$lang$applyTo = (function (seq33912){
var G__33913 = cljs.core.first.call(null,seq33912);
var seq33912__$1 = cljs.core.next.call(null,seq33912);
return garden.stylesheet.rule.cljs$core$IFn$_invoke$arity$variadic(G__33913,seq33912__$1);
});

garden.stylesheet.cssfn = (function garden$stylesheet$cssfn(fn_name){
return (function() { 
var G__33920__delegate = function (args){
return (new garden.types.CSSFunction(fn_name,args,null,null,null));
};
var G__33920 = function (var_args){
var args = null;
if (arguments.length > 0) {
var G__33921__i = 0, G__33921__a = new Array(arguments.length -  0);
while (G__33921__i < G__33921__a.length) {G__33921__a[G__33921__i] = arguments[G__33921__i + 0]; ++G__33921__i;}
  args = new cljs.core.IndexedSeq(G__33921__a,0,null);
} 
return G__33920__delegate.call(this,args);};
G__33920.cljs$lang$maxFixedArity = 0;
G__33920.cljs$lang$applyTo = (function (arglist__33922){
var args = cljs.core.seq(arglist__33922);
return G__33920__delegate(args);
});
G__33920.cljs$core$IFn$_invoke$arity$variadic = G__33920__delegate;
return G__33920;
})()
;
});
garden.stylesheet.at_rule = (function garden$stylesheet$at_rule(identifier,value){
return (new garden.types.CSSAtRule(identifier,value,null,null,null));
});
/**
 * Create a CSS @font-face rule.
 */
garden.stylesheet.at_font_face = (function garden$stylesheet$at_font_face(var_args){
var args__31459__auto__ = [];
var len__31452__auto___33924 = arguments.length;
var i__31453__auto___33925 = (0);
while(true){
if((i__31453__auto___33925 < len__31452__auto___33924)){
args__31459__auto__.push((arguments[i__31453__auto___33925]));

var G__33926 = (i__31453__auto___33925 + (1));
i__31453__auto___33925 = G__33926;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((0) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((0)),(0),null)):null);
return garden.stylesheet.at_font_face.cljs$core$IFn$_invoke$arity$variadic(argseq__31460__auto__);
});

garden.stylesheet.at_font_face.cljs$core$IFn$_invoke$arity$variadic = (function (font_properties){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["@font-face",font_properties], null);
});

garden.stylesheet.at_font_face.cljs$lang$maxFixedArity = (0);

garden.stylesheet.at_font_face.cljs$lang$applyTo = (function (seq33923){
return garden.stylesheet.at_font_face.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq33923));
});

/**
 * Create a CSS @import rule.
 */
garden.stylesheet.at_import = (function garden$stylesheet$at_import(var_args){
var G__33930 = arguments.length;
switch (G__33930) {
case 1:
return garden.stylesheet.at_import.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
var args_arr__31475__auto__ = [];
var len__31452__auto___33932 = arguments.length;
var i__31453__auto___33933 = (0);
while(true){
if((i__31453__auto___33933 < len__31452__auto___33932)){
args_arr__31475__auto__.push((arguments[i__31453__auto___33933]));

var G__33934 = (i__31453__auto___33933 + (1));
i__31453__auto___33933 = G__33934;
continue;
} else {
}
break;
}

var argseq__31476__auto__ = (new cljs.core.IndexedSeq(args_arr__31475__auto__.slice((1)),(0),null));
return garden.stylesheet.at_import.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31476__auto__);

}
});

garden.stylesheet.at_import.cljs$core$IFn$_invoke$arity$1 = (function (url){
return garden.stylesheet.at_rule.call(null,new cljs.core.Keyword(null,"import","import",-1399500709),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"url","url",276297046),url,new cljs.core.Keyword(null,"media-queries","media-queries",-1563277678),null], null));
});

garden.stylesheet.at_import.cljs$core$IFn$_invoke$arity$variadic = (function (url,media_queries){
return garden.stylesheet.at_rule.call(null,new cljs.core.Keyword(null,"import","import",-1399500709),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"url","url",276297046),url,new cljs.core.Keyword(null,"media-queries","media-queries",-1563277678),media_queries], null));
});

garden.stylesheet.at_import.cljs$lang$applyTo = (function (seq33928){
var G__33929 = cljs.core.first.call(null,seq33928);
var seq33928__$1 = cljs.core.next.call(null,seq33928);
return garden.stylesheet.at_import.cljs$core$IFn$_invoke$arity$variadic(G__33929,seq33928__$1);
});

garden.stylesheet.at_import.cljs$lang$maxFixedArity = (1);

/**
 * Create a CSS @media rule.
 */
garden.stylesheet.at_media = (function garden$stylesheet$at_media(var_args){
var args__31459__auto__ = [];
var len__31452__auto___33937 = arguments.length;
var i__31453__auto___33938 = (0);
while(true){
if((i__31453__auto___33938 < len__31452__auto___33937)){
args__31459__auto__.push((arguments[i__31453__auto___33938]));

var G__33939 = (i__31453__auto___33938 + (1));
i__31453__auto___33938 = G__33939;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return garden.stylesheet.at_media.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

garden.stylesheet.at_media.cljs$core$IFn$_invoke$arity$variadic = (function (media_queries,rules){
return garden.stylesheet.at_rule.call(null,new cljs.core.Keyword(null,"media","media",-1066138403),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"media-queries","media-queries",-1563277678),media_queries,new cljs.core.Keyword(null,"rules","rules",1198912366),rules], null));
});

garden.stylesheet.at_media.cljs$lang$maxFixedArity = (1);

garden.stylesheet.at_media.cljs$lang$applyTo = (function (seq33935){
var G__33936 = cljs.core.first.call(null,seq33935);
var seq33935__$1 = cljs.core.next.call(null,seq33935);
return garden.stylesheet.at_media.cljs$core$IFn$_invoke$arity$variadic(G__33936,seq33935__$1);
});

/**
 * Create a CSS @keyframes rule.
 */
garden.stylesheet.at_keyframes = (function garden$stylesheet$at_keyframes(var_args){
var args__31459__auto__ = [];
var len__31452__auto___33942 = arguments.length;
var i__31453__auto___33943 = (0);
while(true){
if((i__31453__auto___33943 < len__31452__auto___33942)){
args__31459__auto__.push((arguments[i__31453__auto___33943]));

var G__33944 = (i__31453__auto___33943 + (1));
i__31453__auto___33943 = G__33944;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return garden.stylesheet.at_keyframes.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

garden.stylesheet.at_keyframes.cljs$core$IFn$_invoke$arity$variadic = (function (identifier,frames){
return garden.stylesheet.at_rule.call(null,new cljs.core.Keyword(null,"keyframes","keyframes",-1437976012),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"identifier","identifier",-805503498),identifier,new cljs.core.Keyword(null,"frames","frames",1765687497),frames], null));
});

garden.stylesheet.at_keyframes.cljs$lang$maxFixedArity = (1);

garden.stylesheet.at_keyframes.cljs$lang$applyTo = (function (seq33940){
var G__33941 = cljs.core.first.call(null,seq33940);
var seq33940__$1 = cljs.core.next.call(null,seq33940);
return garden.stylesheet.at_keyframes.cljs$core$IFn$_invoke$arity$variadic(G__33941,seq33940__$1);
});

/**
 * Create a color from RGB values.
 */
garden.stylesheet.rgb = (function garden$stylesheet$rgb(r,g,b){
return garden.color.rgb.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [r,g,b], null));
});
/**
 * Create a color from HSL values.
 */
garden.stylesheet.hsl = (function garden$stylesheet$hsl(h,s,l){
return garden.color.hsl.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [h,s,l], null));
});

//# sourceMappingURL=stylesheet.js.map?rel=1510137269710
