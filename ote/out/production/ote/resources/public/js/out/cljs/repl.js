// Compiled by ClojureScript 1.9.908 {}
goog.provide('cljs.repl');
goog.require('cljs.core');
goog.require('cljs.spec.alpha');
cljs.repl.print_doc = (function cljs$repl$print_doc(p__56649){
var map__56650 = p__56649;
var map__56650__$1 = ((((!((map__56650 == null)))?((((map__56650.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56650.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56650):map__56650);
var m = map__56650__$1;
var n = cljs.core.get.call(null,map__56650__$1,new cljs.core.Keyword(null,"ns","ns",441598760));
var nm = cljs.core.get.call(null,map__56650__$1,new cljs.core.Keyword(null,"name","name",1843675177));
cljs.core.println.call(null,"-------------------------");

cljs.core.println.call(null,[cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var temp__5290__auto__ = new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__5290__auto__)){
var ns = temp__5290__auto__;
return [cljs.core.str.cljs$core$IFn$_invoke$arity$1(ns),"/"].join('');
} else {
return null;
}
})()),cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m))].join(''));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Protocol");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m))){
var seq__56652_56674 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m));
var chunk__56653_56675 = null;
var count__56654_56676 = (0);
var i__56655_56677 = (0);
while(true){
if((i__56655_56677 < count__56654_56676)){
var f_56678 = cljs.core._nth.call(null,chunk__56653_56675,i__56655_56677);
cljs.core.println.call(null,"  ",f_56678);

var G__56679 = seq__56652_56674;
var G__56680 = chunk__56653_56675;
var G__56681 = count__56654_56676;
var G__56682 = (i__56655_56677 + (1));
seq__56652_56674 = G__56679;
chunk__56653_56675 = G__56680;
count__56654_56676 = G__56681;
i__56655_56677 = G__56682;
continue;
} else {
var temp__5290__auto___56683 = cljs.core.seq.call(null,seq__56652_56674);
if(temp__5290__auto___56683){
var seq__56652_56684__$1 = temp__5290__auto___56683;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__56652_56684__$1)){
var c__31106__auto___56685 = cljs.core.chunk_first.call(null,seq__56652_56684__$1);
var G__56686 = cljs.core.chunk_rest.call(null,seq__56652_56684__$1);
var G__56687 = c__31106__auto___56685;
var G__56688 = cljs.core.count.call(null,c__31106__auto___56685);
var G__56689 = (0);
seq__56652_56674 = G__56686;
chunk__56653_56675 = G__56687;
count__56654_56676 = G__56688;
i__56655_56677 = G__56689;
continue;
} else {
var f_56690 = cljs.core.first.call(null,seq__56652_56684__$1);
cljs.core.println.call(null,"  ",f_56690);

var G__56691 = cljs.core.next.call(null,seq__56652_56684__$1);
var G__56692 = null;
var G__56693 = (0);
var G__56694 = (0);
seq__56652_56674 = G__56691;
chunk__56653_56675 = G__56692;
count__56654_56676 = G__56693;
i__56655_56677 = G__56694;
continue;
}
} else {
}
}
break;
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m))){
var arglists_56695 = new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_((function (){var or__30175__auto__ = new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m);
}
})())){
cljs.core.prn.call(null,arglists_56695);
} else {
cljs.core.prn.call(null,((cljs.core._EQ_.call(null,new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.first.call(null,arglists_56695)))?cljs.core.second.call(null,arglists_56695):arglists_56695));
}
} else {
}
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"special-form","special-form",-1326536374).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Special Form");

cljs.core.println.call(null," ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.contains_QMARK_.call(null,m,new cljs.core.Keyword(null,"url","url",276297046))){
if(cljs.core.truth_(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))){
return cljs.core.println.call(null,["\n  Please see http://clojure.org/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))].join(''));
} else {
return null;
}
} else {
return cljs.core.println.call(null,["\n  Please see http://clojure.org/special_forms#",cljs.core.str.cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m))].join(''));
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Macro");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"REPL Special Function");
} else {
}

cljs.core.println.call(null," ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
var seq__56656_56696 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"methods","methods",453930866).cljs$core$IFn$_invoke$arity$1(m));
var chunk__56657_56697 = null;
var count__56658_56698 = (0);
var i__56659_56699 = (0);
while(true){
if((i__56659_56699 < count__56658_56698)){
var vec__56660_56700 = cljs.core._nth.call(null,chunk__56657_56697,i__56659_56699);
var name_56701 = cljs.core.nth.call(null,vec__56660_56700,(0),null);
var map__56663_56702 = cljs.core.nth.call(null,vec__56660_56700,(1),null);
var map__56663_56703__$1 = ((((!((map__56663_56702 == null)))?((((map__56663_56702.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56663_56702.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56663_56702):map__56663_56702);
var doc_56704 = cljs.core.get.call(null,map__56663_56703__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists_56705 = cljs.core.get.call(null,map__56663_56703__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println.call(null);

cljs.core.println.call(null," ",name_56701);

cljs.core.println.call(null," ",arglists_56705);

if(cljs.core.truth_(doc_56704)){
cljs.core.println.call(null," ",doc_56704);
} else {
}

var G__56706 = seq__56656_56696;
var G__56707 = chunk__56657_56697;
var G__56708 = count__56658_56698;
var G__56709 = (i__56659_56699 + (1));
seq__56656_56696 = G__56706;
chunk__56657_56697 = G__56707;
count__56658_56698 = G__56708;
i__56659_56699 = G__56709;
continue;
} else {
var temp__5290__auto___56710 = cljs.core.seq.call(null,seq__56656_56696);
if(temp__5290__auto___56710){
var seq__56656_56711__$1 = temp__5290__auto___56710;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__56656_56711__$1)){
var c__31106__auto___56712 = cljs.core.chunk_first.call(null,seq__56656_56711__$1);
var G__56713 = cljs.core.chunk_rest.call(null,seq__56656_56711__$1);
var G__56714 = c__31106__auto___56712;
var G__56715 = cljs.core.count.call(null,c__31106__auto___56712);
var G__56716 = (0);
seq__56656_56696 = G__56713;
chunk__56657_56697 = G__56714;
count__56658_56698 = G__56715;
i__56659_56699 = G__56716;
continue;
} else {
var vec__56665_56717 = cljs.core.first.call(null,seq__56656_56711__$1);
var name_56718 = cljs.core.nth.call(null,vec__56665_56717,(0),null);
var map__56668_56719 = cljs.core.nth.call(null,vec__56665_56717,(1),null);
var map__56668_56720__$1 = ((((!((map__56668_56719 == null)))?((((map__56668_56719.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__56668_56719.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__56668_56719):map__56668_56719);
var doc_56721 = cljs.core.get.call(null,map__56668_56720__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists_56722 = cljs.core.get.call(null,map__56668_56720__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println.call(null);

cljs.core.println.call(null," ",name_56718);

cljs.core.println.call(null," ",arglists_56722);

if(cljs.core.truth_(doc_56721)){
cljs.core.println.call(null," ",doc_56721);
} else {
}

var G__56723 = cljs.core.next.call(null,seq__56656_56711__$1);
var G__56724 = null;
var G__56725 = (0);
var G__56726 = (0);
seq__56656_56696 = G__56723;
chunk__56657_56697 = G__56724;
count__56658_56698 = G__56725;
i__56659_56699 = G__56726;
continue;
}
} else {
}
}
break;
}
} else {
}

if(cljs.core.truth_(n)){
var temp__5290__auto__ = cljs.spec.alpha.get_spec.call(null,cljs.core.symbol.call(null,[cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.ns_name.call(null,n))].join(''),cljs.core.name.call(null,nm)));
if(cljs.core.truth_(temp__5290__auto__)){
var fnspec = temp__5290__auto__;
cljs.core.print.call(null,"Spec");

var seq__56670 = cljs.core.seq.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"args","args",1315556576),new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Keyword(null,"fn","fn",-1175266204)], null));
var chunk__56671 = null;
var count__56672 = (0);
var i__56673 = (0);
while(true){
if((i__56673 < count__56672)){
var role = cljs.core._nth.call(null,chunk__56671,i__56673);
var temp__5290__auto___56727__$1 = cljs.core.get.call(null,fnspec,role);
if(cljs.core.truth_(temp__5290__auto___56727__$1)){
var spec_56728 = temp__5290__auto___56727__$1;
cljs.core.print.call(null,["\n ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name.call(null,role)),":"].join(''),cljs.spec.alpha.describe.call(null,spec_56728));
} else {
}

var G__56729 = seq__56670;
var G__56730 = chunk__56671;
var G__56731 = count__56672;
var G__56732 = (i__56673 + (1));
seq__56670 = G__56729;
chunk__56671 = G__56730;
count__56672 = G__56731;
i__56673 = G__56732;
continue;
} else {
var temp__5290__auto____$1 = cljs.core.seq.call(null,seq__56670);
if(temp__5290__auto____$1){
var seq__56670__$1 = temp__5290__auto____$1;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__56670__$1)){
var c__31106__auto__ = cljs.core.chunk_first.call(null,seq__56670__$1);
var G__56733 = cljs.core.chunk_rest.call(null,seq__56670__$1);
var G__56734 = c__31106__auto__;
var G__56735 = cljs.core.count.call(null,c__31106__auto__);
var G__56736 = (0);
seq__56670 = G__56733;
chunk__56671 = G__56734;
count__56672 = G__56735;
i__56673 = G__56736;
continue;
} else {
var role = cljs.core.first.call(null,seq__56670__$1);
var temp__5290__auto___56737__$2 = cljs.core.get.call(null,fnspec,role);
if(cljs.core.truth_(temp__5290__auto___56737__$2)){
var spec_56738 = temp__5290__auto___56737__$2;
cljs.core.print.call(null,["\n ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.name.call(null,role)),":"].join(''),cljs.spec.alpha.describe.call(null,spec_56738));
} else {
}

var G__56739 = cljs.core.next.call(null,seq__56670__$1);
var G__56740 = null;
var G__56741 = (0);
var G__56742 = (0);
seq__56670 = G__56739;
chunk__56671 = G__56740;
count__56672 = G__56741;
i__56673 = G__56742;
continue;
}
} else {
return null;
}
}
break;
}
} else {
return null;
}
} else {
return null;
}
}
});

//# sourceMappingURL=repl.js.map?rel=1510137299394
