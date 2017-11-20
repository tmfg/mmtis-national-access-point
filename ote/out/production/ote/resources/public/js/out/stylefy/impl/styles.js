// Compiled by ClojureScript 1.9.908 {}
goog.provide('stylefy.impl.styles');
goog.require('cljs.core');
goog.require('stylefy.impl.dom');
goog.require('garden.core');
goog.require('clojure.string');
stylefy.impl.styles.hash_style = (function stylefy$impl$styles$hash_style(style){
return ["_stylefy_",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.hash.call(null,cljs.core.dissoc.call(null,style,new cljs.core.Keyword("stylefy.core","sub-styles","stylefy.core/sub-styles",-1546489432))))].join('');
});
stylefy.impl.styles.create_style_BANG_ = (function stylefy$impl$styles$create_style_BANG_(p__36119){
var map__36120 = p__36119;
var map__36120__$1 = ((((!((map__36120 == null)))?((((map__36120.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__36120.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__36120):map__36120);
var style = map__36120__$1;
var props = cljs.core.get.call(null,map__36120__$1,new cljs.core.Keyword(null,"props","props",453281727));
var hash = cljs.core.get.call(null,map__36120__$1,new cljs.core.Keyword(null,"hash","hash",-13781596));
stylefy.impl.dom.save_style_BANG_.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"props","props",453281727),props,new cljs.core.Keyword(null,"hash","hash",-13781596),hash], null));

var seq__36122 = cljs.core.seq.call(null,cljs.core.vals.call(null,new cljs.core.Keyword("stylefy.core","sub-styles","stylefy.core/sub-styles",-1546489432).cljs$core$IFn$_invoke$arity$1(props)));
var chunk__36123 = null;
var count__36124 = (0);
var i__36125 = (0);
while(true){
if((i__36125 < count__36124)){
var sub_style = cljs.core._nth.call(null,chunk__36123,i__36125);
stylefy.impl.styles.create_style_BANG_.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"props","props",453281727),sub_style,new cljs.core.Keyword(null,"hash","hash",-13781596),stylefy.impl.styles.hash_style.call(null,sub_style)], null));

var G__36126 = seq__36122;
var G__36127 = chunk__36123;
var G__36128 = count__36124;
var G__36129 = (i__36125 + (1));
seq__36122 = G__36126;
chunk__36123 = G__36127;
count__36124 = G__36128;
i__36125 = G__36129;
continue;
} else {
var temp__5290__auto__ = cljs.core.seq.call(null,seq__36122);
if(temp__5290__auto__){
var seq__36122__$1 = temp__5290__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__36122__$1)){
var c__31106__auto__ = cljs.core.chunk_first.call(null,seq__36122__$1);
var G__36130 = cljs.core.chunk_rest.call(null,seq__36122__$1);
var G__36131 = c__31106__auto__;
var G__36132 = cljs.core.count.call(null,c__31106__auto__);
var G__36133 = (0);
seq__36122 = G__36130;
chunk__36123 = G__36131;
count__36124 = G__36132;
i__36125 = G__36133;
continue;
} else {
var sub_style = cljs.core.first.call(null,seq__36122__$1);
stylefy.impl.styles.create_style_BANG_.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"props","props",453281727),sub_style,new cljs.core.Keyword(null,"hash","hash",-13781596),stylefy.impl.styles.hash_style.call(null,sub_style)], null));

var G__36134 = cljs.core.next.call(null,seq__36122__$1);
var G__36135 = null;
var G__36136 = (0);
var G__36137 = (0);
seq__36122 = G__36134;
chunk__36123 = G__36135;
count__36124 = G__36136;
i__36125 = G__36137;
continue;
}
} else {
return null;
}
}
break;
}
});
stylefy.impl.styles.style_return_value = (function stylefy$impl$styles$style_return_value(style,style_hash,options){
var with_classes = new cljs.core.Keyword("stylefy.core","with-classes","stylefy.core/with-classes",1994369003).cljs$core$IFn$_invoke$arity$1(options);
var contains_media_queries_QMARK_ = !((new cljs.core.Keyword("stylefy.core","media","stylefy.core/media",-1323617834).cljs$core$IFn$_invoke$arity$1(style) == null));
var contains_feature_queries_QMARK_ = !((new cljs.core.Keyword("stylefy.core","supports","stylefy.core/supports",105019324).cljs$core$IFn$_invoke$arity$1(style) == null));
var excluded_modes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"hover","hover",-341141711),null], null), null);
var contains_modes_not_excluded_QMARK_ = !(cljs.core.empty_QMARK_.call(null,cljs.core.filter.call(null,cljs.core.comp.call(null,cljs.core.not,excluded_modes),cljs.core.keys.call(null,new cljs.core.Keyword("stylefy.core","mode","stylefy.core/mode",-1757530234).cljs$core$IFn$_invoke$arity$1(style)))));
var return_map = new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"class","class",-2030961996),clojure.string.join.call(null," ",cljs.core.conj.call(null,with_classes,style_hash))], null);
if(cljs.core.truth_(stylefy.impl.dom.style_in_dom_QMARK_.call(null,style_hash))){
return return_map;
} else {
if((contains_media_queries_QMARK_) || (contains_feature_queries_QMARK_) || (contains_modes_not_excluded_QMARK_)){
return cljs.core.merge.call(null,return_map,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),cljs.core.merge.call(null,style,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"visibility","visibility",1338380893),"hidden"], null))], null));
} else {
return cljs.core.merge.call(null,return_map,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),style], null));
}
}
});
stylefy.impl.styles.use_style_BANG_ = (function stylefy$impl$styles$use_style_BANG_(style,options){
cljs.core.deref.call(null,stylefy.impl.dom.styles_in_use);

if(cljs.core.empty_QMARK_.call(null,style)){
return null;
} else {
var with_classes = new cljs.core.Keyword("stylefy.core","with-classes","stylefy.core/with-classes",1994369003).cljs$core$IFn$_invoke$arity$1(options);
if(((with_classes == null)) || ((cljs.core.vector_QMARK_.call(null,with_classes)) && (cljs.core.every_QMARK_.call(null,cljs.core.string_QMARK_,with_classes)))){
} else {
throw (new Error(["Assert failed: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(["with-classes argument must be a vector of strings, got: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,with_classes))].join('')),"\n","(or (nil? with-classes) (and (vector? with-classes) (every? string? with-classes)))"].join('')));
}

var style_hash = stylefy.impl.styles.hash_style.call(null,style);
var already_created = stylefy.impl.dom.style_by_hash.call(null,style_hash);
if(cljs.core.truth_(already_created)){
} else {
stylefy.impl.styles.create_style_BANG_.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"props","props",453281727),style,new cljs.core.Keyword(null,"hash","hash",-13781596),style_hash], null));
}

return stylefy.impl.styles.style_return_value.call(null,style,style_hash,options);
}
});
stylefy.impl.styles.use_sub_style_BANG_ = (function stylefy$impl$styles$use_sub_style_BANG_(style,sub_style,options){
var resolved_sub_style = cljs.core.get.call(null,new cljs.core.Keyword("stylefy.core","sub-styles","stylefy.core/sub-styles",-1546489432).cljs$core$IFn$_invoke$arity$1(style),sub_style);
if(cljs.core.truth_(resolved_sub_style)){
return stylefy.impl.styles.use_style_BANG_.call(null,resolved_sub_style,options);
} else {
return console.warn(["Sub-style ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,sub_style))," not found in style: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,style))].join(''));
}
});
stylefy.impl.styles.sub_style = (function stylefy$impl$styles$sub_style(var_args){
var args__31459__auto__ = [];
var len__31452__auto___36142 = arguments.length;
var i__31453__auto___36143 = (0);
while(true){
if((i__31453__auto___36143 < len__31452__auto___36142)){
args__31459__auto__.push((arguments[i__31453__auto___36143]));

var G__36144 = (i__31453__auto___36143 + (1));
i__31453__auto___36143 = G__36144;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return stylefy.impl.styles.sub_style.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

stylefy.impl.styles.sub_style.cljs$core$IFn$_invoke$arity$variadic = (function (style,sub_styles){
var resolved_sub_style = cljs.core.reduce.call(null,(function (p1__36138_SHARP_,p2__36139_SHARP_){
return cljs.core.get_in.call(null,p1__36138_SHARP_,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("stylefy.core","sub-styles","stylefy.core/sub-styles",-1546489432),p2__36139_SHARP_], null));
}),style,sub_styles);
if(cljs.core.truth_(resolved_sub_style)){
return resolved_sub_style;
} else {
return console.warn(["Sub-style ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,sub_styles))," not found in style: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,style))].join(''));
}
});

stylefy.impl.styles.sub_style.cljs$lang$maxFixedArity = (1);

stylefy.impl.styles.sub_style.cljs$lang$applyTo = (function (seq36140){
var G__36141 = cljs.core.first.call(null,seq36140);
var seq36140__$1 = cljs.core.next.call(null,seq36140);
return stylefy.impl.styles.sub_style.cljs$core$IFn$_invoke$arity$variadic(G__36141,seq36140__$1);
});

stylefy.impl.styles.prepare_styles = (function stylefy$impl$styles$prepare_styles(styles){
var styles_36149__$1 = cljs.core.remove.call(null,cljs.core.nil_QMARK_,styles);
var seq__36145_36150 = cljs.core.seq.call(null,styles_36149__$1);
var chunk__36146_36151 = null;
var count__36147_36152 = (0);
var i__36148_36153 = (0);
while(true){
if((i__36148_36153 < count__36147_36152)){
var style_36154 = cljs.core._nth.call(null,chunk__36146_36151,i__36148_36153);
stylefy.impl.styles.use_style_BANG_.call(null,style_36154,cljs.core.PersistentArrayMap.EMPTY);

var temp__5290__auto___36155 = cljs.core.vals.call(null,new cljs.core.Keyword("stylefy.core","sub-styles","stylefy.core/sub-styles",-1546489432).cljs$core$IFn$_invoke$arity$1(style_36154));
if(cljs.core.truth_(temp__5290__auto___36155)){
var sub_styles_36156 = temp__5290__auto___36155;
stylefy.impl.styles.prepare_styles.call(null,sub_styles_36156);
} else {
}

var G__36157 = seq__36145_36150;
var G__36158 = chunk__36146_36151;
var G__36159 = count__36147_36152;
var G__36160 = (i__36148_36153 + (1));
seq__36145_36150 = G__36157;
chunk__36146_36151 = G__36158;
count__36147_36152 = G__36159;
i__36148_36153 = G__36160;
continue;
} else {
var temp__5290__auto___36161 = cljs.core.seq.call(null,seq__36145_36150);
if(temp__5290__auto___36161){
var seq__36145_36162__$1 = temp__5290__auto___36161;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__36145_36162__$1)){
var c__31106__auto___36163 = cljs.core.chunk_first.call(null,seq__36145_36162__$1);
var G__36164 = cljs.core.chunk_rest.call(null,seq__36145_36162__$1);
var G__36165 = c__31106__auto___36163;
var G__36166 = cljs.core.count.call(null,c__31106__auto___36163);
var G__36167 = (0);
seq__36145_36150 = G__36164;
chunk__36146_36151 = G__36165;
count__36147_36152 = G__36166;
i__36148_36153 = G__36167;
continue;
} else {
var style_36168 = cljs.core.first.call(null,seq__36145_36162__$1);
stylefy.impl.styles.use_style_BANG_.call(null,style_36168,cljs.core.PersistentArrayMap.EMPTY);

var temp__5290__auto___36169__$1 = cljs.core.vals.call(null,new cljs.core.Keyword("stylefy.core","sub-styles","stylefy.core/sub-styles",-1546489432).cljs$core$IFn$_invoke$arity$1(style_36168));
if(cljs.core.truth_(temp__5290__auto___36169__$1)){
var sub_styles_36170 = temp__5290__auto___36169__$1;
stylefy.impl.styles.prepare_styles.call(null,sub_styles_36170);
} else {
}

var G__36171 = cljs.core.next.call(null,seq__36145_36162__$1);
var G__36172 = null;
var G__36173 = (0);
var G__36174 = (0);
seq__36145_36150 = G__36171;
chunk__36146_36151 = G__36172;
count__36147_36152 = G__36173;
i__36148_36153 = G__36174;
continue;
}
} else {
}
}
break;
}

return stylefy.impl.dom.update_styles_in_dom_BANG_.call(null);
});

//# sourceMappingURL=styles.js.map?rel=1510137272494
