// Compiled by ClojureScript 1.9.908 {}
goog.provide('garden.core');
goog.require('cljs.core');
goog.require('garden.compiler');
/**
 * Convert a variable number of Clojure data structure to a string of
 *   CSS. The first argument may be a list of flags for the compiler.
 */
garden.core.css = (function garden$core$css(var_args){
var args__31459__auto__ = [];
var len__31452__auto___36083 = arguments.length;
var i__31453__auto___36084 = (0);
while(true){
if((i__31453__auto___36084 < len__31452__auto___36083)){
args__31459__auto__.push((arguments[i__31453__auto___36084]));

var G__36085 = (i__31453__auto___36084 + (1));
i__31453__auto___36084 = G__36085;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((0) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((0)),(0),null)):null);
return garden.core.css.cljs$core$IFn$_invoke$arity$variadic(argseq__31460__auto__);
});

garden.core.css.cljs$core$IFn$_invoke$arity$variadic = (function (rules){
return cljs.core.apply.call(null,garden.compiler.compile_css,rules);
});

garden.core.css.cljs$lang$maxFixedArity = (0);

garden.core.css.cljs$lang$applyTo = (function (seq36082){
return garden.core.css.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq36082));
});

/**
 * Convert a variable number of maps into a string of CSS for use with
 *   the HTML `style` attribute.
 */
garden.core.style = (function garden$core$style(var_args){
var args__31459__auto__ = [];
var len__31452__auto___36087 = arguments.length;
var i__31453__auto___36088 = (0);
while(true){
if((i__31453__auto___36088 < len__31452__auto___36087)){
args__31459__auto__.push((arguments[i__31453__auto___36088]));

var G__36089 = (i__31453__auto___36088 + (1));
i__31453__auto___36088 = G__36089;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((0) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((0)),(0),null)):null);
return garden.core.style.cljs$core$IFn$_invoke$arity$variadic(argseq__31460__auto__);
});

garden.core.style.cljs$core$IFn$_invoke$arity$variadic = (function (maps){
return garden.compiler.compile_style.call(null,maps);
});

garden.core.style.cljs$lang$maxFixedArity = (0);

garden.core.style.cljs$lang$applyTo = (function (seq36086){
return garden.core.style.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq36086));
});


//# sourceMappingURL=core.js.map?rel=1510137272363
