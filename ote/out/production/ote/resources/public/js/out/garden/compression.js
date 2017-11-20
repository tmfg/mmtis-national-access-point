// Compiled by ClojureScript 1.9.908 {}
goog.provide('garden.compression');
goog.require('cljs.core');
/**
 * Return a function which when given a string will return a map
 *   containing the chunk of text matched by re, it's size, and tag.
 */
garden.compression.token_fn = (function garden$compression$token_fn(p__35556){
var vec__35557 = p__35556;
var tag = cljs.core.nth.call(null,vec__35557,(0),null);
var re = cljs.core.nth.call(null,vec__35557,(1),null);
return ((function (vec__35557,tag,re){
return (function (s){
var temp__5290__auto__ = cljs.core.re_find.call(null,re,s);
if(cljs.core.truth_(temp__5290__auto__)){
var chunk = temp__5290__auto__;
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"tag","tag",-1290361223),tag,new cljs.core.Keyword(null,"chunk","chunk",-1191159620),chunk,new cljs.core.Keyword(null,"size","size",1098693007),cljs.core.count.call(null,chunk)], null);
} else {
return null;
}
});
;})(vec__35557,tag,re))
});
/**
 * Given an arbitrary number of [tag regex] pairs, return a function
 *   which when given a string s will return the first matching token of s.
 *   Token precedence is determined by the order of the pairs. The first
 *   and last pairs have the highest and lowest precedence respectively.
 */
garden.compression.tokenizer = (function garden$compression$tokenizer(var_args){
var args__31459__auto__ = [];
var len__31452__auto___35562 = arguments.length;
var i__31453__auto___35563 = (0);
while(true){
if((i__31453__auto___35563 < len__31452__auto___35562)){
args__31459__auto__.push((arguments[i__31453__auto___35563]));

var G__35564 = (i__31453__auto___35563 + (1));
i__31453__auto___35563 = G__35564;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((0) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((0)),(0),null)):null);
return garden.compression.tokenizer.cljs$core$IFn$_invoke$arity$variadic(argseq__31460__auto__);
});

garden.compression.tokenizer.cljs$core$IFn$_invoke$arity$variadic = (function (tags_PLUS_regexes){
var fs = cljs.core.map.call(null,garden.compression.token_fn,tags_PLUS_regexes);
return ((function (fs){
return (function (s){
return cljs.core.some.call(null,((function (fs){
return (function (p1__35560_SHARP_){
return p1__35560_SHARP_.call(null,s);
});})(fs))
,fs);
});
;})(fs))
});

garden.compression.tokenizer.cljs$lang$maxFixedArity = (0);

garden.compression.tokenizer.cljs$lang$applyTo = (function (seq35561){
return garden.compression.tokenizer.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq35561));
});

/**
 * Tokenizer used during stylesheet compression.
 */
garden.compression.stylesheet_tokenizer = garden.compression.tokenizer.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"string","string",-1989541586),/^\"(?:\\.|[^\"])*\"/], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"r-brace","r-brace",-1335738887),/^\s*\{\s*/], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"l-brace","l-brace",613286657),/^;?\s*}/], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"r-paren","r-paren",-1688338021),/^\s*\(\s*/], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"l-paren","l-paren",2052672514),/^\s*\)/], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"comma","comma",1699024745),/^,\s*/], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"colon","colon",-965200945),/^:\s*/], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"semicolon","semicolon",797086549),/^;/], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"space+","space+",378127624),/^ +/], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"white-space+","white-space+",1452157162),/^\s+/], null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"any","any",1705907423),/^./], null));
/**
 * Compress a string of CSS using a basic compressor.
 */
garden.compression.compress_stylesheet = (function garden$compression$compress_stylesheet(stylesheet){
var s1 = stylesheet;
var s2 = "";
while(true){
var temp__5288__auto__ = garden.compression.stylesheet_tokenizer.call(null,s1);
if(cljs.core.truth_(temp__5288__auto__)){
var map__35565 = temp__5288__auto__;
var map__35565__$1 = ((((!((map__35565 == null)))?((((map__35565.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__35565.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__35565):map__35565);
var tag = cljs.core.get.call(null,map__35565__$1,new cljs.core.Keyword(null,"tag","tag",-1290361223));
var chunk = cljs.core.get.call(null,map__35565__$1,new cljs.core.Keyword(null,"chunk","chunk",-1191159620));
var size = cljs.core.get.call(null,map__35565__$1,new cljs.core.Keyword(null,"size","size",1098693007));
var G__35568 = cljs.core.subs.call(null,s1,size);
var G__35569 = [cljs.core.str.cljs$core$IFn$_invoke$arity$1(s2),cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var G__35567 = tag;
var G__35567__$1 = (((G__35567 instanceof cljs.core.Keyword))?G__35567.fqn:null);
switch (G__35567__$1) {
case "string":
return chunk;

break;
case "r-brace":
return "{";

break;
case "l-brace":
return "}";

break;
case "r-paren":
return "(";

break;
case "l-paren":
return ")";

break;
case "comma":
return ",";

break;
case "semi-comma":
return ";";

break;
case "colon":
return ":";

break;
case "space+":
return " ";

break;
case "white-space+":
return "";

break;
default:
return chunk;

}
})())].join('');
s1 = G__35568;
s2 = G__35569;
continue;
} else {
return s2;
}
break;
}
});

//# sourceMappingURL=compression.js.map?rel=1510137271895
