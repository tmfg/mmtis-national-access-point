// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.ui.validation');
goog.require('cljs.core');
goog.require('reagent.core');
goog.require('clojure.string');
goog.require('cljs_time.core');
goog.require('ote.localization');
goog.require('ote.format');
ote.ui.validation.empty_value_QMARK_ = (function ote$ui$validation$empty_value_QMARK_(arvo){
return ((arvo == null)) || (clojure.string.blank_QMARK_.call(null,arvo));
});
if(typeof ote.ui.validation.validate_rule !== 'undefined'){
} else {
ote.ui.validation.validate_rule = (function (){var method_table__31228__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var prefer_table__31229__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var method_cache__31230__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var cached_hierarchy__31231__auto__ = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var hierarchy__31232__auto__ = cljs.core.get.call(null,cljs.core.PersistentArrayMap.EMPTY,new cljs.core.Keyword(null,"hierarchy","hierarchy",-1053470341),cljs.core.get_global_hierarchy.call(null));
return (new cljs.core.MultiFn(cljs.core.symbol.call(null,"ote.ui.validation","validate-rule"),((function (method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__,hierarchy__31232__auto__){
return (function() { 
var G__46967__delegate = function (rule,name,data,row,table,options){
return rule;
};
var G__46967 = function (rule,name,data,row,table,var_args){
var options = null;
if (arguments.length > 5) {
var G__46968__i = 0, G__46968__a = new Array(arguments.length -  5);
while (G__46968__i < G__46968__a.length) {G__46968__a[G__46968__i] = arguments[G__46968__i + 5]; ++G__46968__i;}
  options = new cljs.core.IndexedSeq(G__46968__a,0,null);
} 
return G__46967__delegate.call(this,rule,name,data,row,table,options);};
G__46967.cljs$lang$maxFixedArity = 5;
G__46967.cljs$lang$applyTo = (function (arglist__46969){
var rule = cljs.core.first(arglist__46969);
arglist__46969 = cljs.core.next(arglist__46969);
var name = cljs.core.first(arglist__46969);
arglist__46969 = cljs.core.next(arglist__46969);
var data = cljs.core.first(arglist__46969);
arglist__46969 = cljs.core.next(arglist__46969);
var row = cljs.core.first(arglist__46969);
arglist__46969 = cljs.core.next(arglist__46969);
var table = cljs.core.first(arglist__46969);
var options = cljs.core.rest(arglist__46969);
return G__46967__delegate(rule,name,data,row,table,options);
});
G__46967.cljs$core$IFn$_invoke$arity$variadic = G__46967__delegate;
return G__46967;
})()
;})(method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__,hierarchy__31232__auto__))
,new cljs.core.Keyword(null,"default","default",-1987822328),hierarchy__31232__auto__,method_table__31228__auto__,prefer_table__31229__auto__,method_cache__31230__auto__,cached_hierarchy__31231__auto__));
})();
}
cljs.core._add_method.call(null,ote.ui.validation.validate_rule,new cljs.core.Keyword(null,"constant-notice","constant-notice",-2068781660),(function() { 
var G__46974__delegate = function (_,___$1,data,___$2,___$3,p__46970){
var vec__46971 = p__46970;
var message = cljs.core.nth.call(null,vec__46971,(0),null);
return message;
};
var G__46974 = function (_,___$1,data,___$2,___$3,var_args){
var p__46970 = null;
if (arguments.length > 5) {
var G__46975__i = 0, G__46975__a = new Array(arguments.length -  5);
while (G__46975__i < G__46975__a.length) {G__46975__a[G__46975__i] = arguments[G__46975__i + 5]; ++G__46975__i;}
  p__46970 = new cljs.core.IndexedSeq(G__46975__a,0,null);
} 
return G__46974__delegate.call(this,_,___$1,data,___$2,___$3,p__46970);};
G__46974.cljs$lang$maxFixedArity = 5;
G__46974.cljs$lang$applyTo = (function (arglist__46976){
var _ = cljs.core.first(arglist__46976);
arglist__46976 = cljs.core.next(arglist__46976);
var ___$1 = cljs.core.first(arglist__46976);
arglist__46976 = cljs.core.next(arglist__46976);
var data = cljs.core.first(arglist__46976);
arglist__46976 = cljs.core.next(arglist__46976);
var ___$2 = cljs.core.first(arglist__46976);
arglist__46976 = cljs.core.next(arglist__46976);
var ___$3 = cljs.core.first(arglist__46976);
var p__46970 = cljs.core.rest(arglist__46976);
return G__46974__delegate(_,___$1,data,___$2,___$3,p__46970);
});
G__46974.cljs$core$IFn$_invoke$arity$variadic = G__46974__delegate;
return G__46974;
})()
);
cljs.core._add_method.call(null,ote.ui.validation.validate_rule,new cljs.core.Keyword(null,"non-empty","non-empty",-1352150228),(function() { 
var G__46981__delegate = function (_,___$1,data,___$2,___$3,p__46977){
var vec__46978 = p__46977;
var message = cljs.core.nth.call(null,vec__46978,(0),null);
if(clojure.string.blank_QMARK_.call(null,data)){
var or__30175__auto__ = message;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return "Anna arvo";
}
} else {
return null;
}
};
var G__46981 = function (_,___$1,data,___$2,___$3,var_args){
var p__46977 = null;
if (arguments.length > 5) {
var G__46982__i = 0, G__46982__a = new Array(arguments.length -  5);
while (G__46982__i < G__46982__a.length) {G__46982__a[G__46982__i] = arguments[G__46982__i + 5]; ++G__46982__i;}
  p__46977 = new cljs.core.IndexedSeq(G__46982__a,0,null);
} 
return G__46981__delegate.call(this,_,___$1,data,___$2,___$3,p__46977);};
G__46981.cljs$lang$maxFixedArity = 5;
G__46981.cljs$lang$applyTo = (function (arglist__46983){
var _ = cljs.core.first(arglist__46983);
arglist__46983 = cljs.core.next(arglist__46983);
var ___$1 = cljs.core.first(arglist__46983);
arglist__46983 = cljs.core.next(arglist__46983);
var data = cljs.core.first(arglist__46983);
arglist__46983 = cljs.core.next(arglist__46983);
var ___$2 = cljs.core.first(arglist__46983);
arglist__46983 = cljs.core.next(arglist__46983);
var ___$3 = cljs.core.first(arglist__46983);
var p__46977 = cljs.core.rest(arglist__46983);
return G__46981__delegate(_,___$1,data,___$2,___$3,p__46977);
});
G__46981.cljs$core$IFn$_invoke$arity$variadic = G__46981__delegate;
return G__46981;
})()
);
cljs.core._add_method.call(null,ote.ui.validation.validate_rule,new cljs.core.Keyword(null,"non-negative-if-key","non-negative-if-key",866357603),(function() { 
var G__46988__delegate = function (_,___$1,data,row,___$2,p__46984){
var vec__46985 = p__46984;
var key = cljs.core.nth.call(null,vec__46985,(0),null);
var value = cljs.core.nth.call(null,vec__46985,(1),null);
var message = cljs.core.nth.call(null,vec__46985,(2),null);
if((cljs.core._EQ_.call(null,key.call(null,row),value)) && ((data < (0)))){
var or__30175__auto__ = message;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return "Value must not be negative";
}
} else {
return null;
}
};
var G__46988 = function (_,___$1,data,row,___$2,var_args){
var p__46984 = null;
if (arguments.length > 5) {
var G__46989__i = 0, G__46989__a = new Array(arguments.length -  5);
while (G__46989__i < G__46989__a.length) {G__46989__a[G__46989__i] = arguments[G__46989__i + 5]; ++G__46989__i;}
  p__46984 = new cljs.core.IndexedSeq(G__46989__a,0,null);
} 
return G__46988__delegate.call(this,_,___$1,data,row,___$2,p__46984);};
G__46988.cljs$lang$maxFixedArity = 5;
G__46988.cljs$lang$applyTo = (function (arglist__46990){
var _ = cljs.core.first(arglist__46990);
arglist__46990 = cljs.core.next(arglist__46990);
var ___$1 = cljs.core.first(arglist__46990);
arglist__46990 = cljs.core.next(arglist__46990);
var data = cljs.core.first(arglist__46990);
arglist__46990 = cljs.core.next(arglist__46990);
var row = cljs.core.first(arglist__46990);
arglist__46990 = cljs.core.next(arglist__46990);
var ___$2 = cljs.core.first(arglist__46990);
var p__46984 = cljs.core.rest(arglist__46990);
return G__46988__delegate(_,___$1,data,row,___$2,p__46984);
});
G__46988.cljs$core$IFn$_invoke$arity$variadic = G__46988__delegate;
return G__46988;
})()
);
cljs.core._add_method.call(null,ote.ui.validation.validate_rule,new cljs.core.Keyword(null,"non-empty-if-other-key-nil","non-empty-if-other-key-nil",-1819764805),(function() { 
var G__46995__delegate = function (_,___$1,data,row,___$2,p__46991){
var vec__46992 = p__46991;
var other_key = cljs.core.nth.call(null,vec__46992,(0),null);
var message = cljs.core.nth.call(null,vec__46992,(1),null);
if((clojure.string.blank_QMARK_.call(null,data)) && (cljs.core.not.call(null,other_key.call(null,row)))){
var or__30175__auto__ = message;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return "Value missing";
}
} else {
return null;
}
};
var G__46995 = function (_,___$1,data,row,___$2,var_args){
var p__46991 = null;
if (arguments.length > 5) {
var G__46996__i = 0, G__46996__a = new Array(arguments.length -  5);
while (G__46996__i < G__46996__a.length) {G__46996__a[G__46996__i] = arguments[G__46996__i + 5]; ++G__46996__i;}
  p__46991 = new cljs.core.IndexedSeq(G__46996__a,0,null);
} 
return G__46995__delegate.call(this,_,___$1,data,row,___$2,p__46991);};
G__46995.cljs$lang$maxFixedArity = 5;
G__46995.cljs$lang$applyTo = (function (arglist__46997){
var _ = cljs.core.first(arglist__46997);
arglist__46997 = cljs.core.next(arglist__46997);
var ___$1 = cljs.core.first(arglist__46997);
arglist__46997 = cljs.core.next(arglist__46997);
var data = cljs.core.first(arglist__46997);
arglist__46997 = cljs.core.next(arglist__46997);
var row = cljs.core.first(arglist__46997);
arglist__46997 = cljs.core.next(arglist__46997);
var ___$2 = cljs.core.first(arglist__46997);
var p__46991 = cljs.core.rest(arglist__46997);
return G__46995__delegate(_,___$1,data,row,___$2,p__46991);
});
G__46995.cljs$core$IFn$_invoke$arity$variadic = G__46995__delegate;
return G__46995;
})()
);
cljs.core._add_method.call(null,ote.ui.validation.validate_rule,new cljs.core.Keyword(null,"non-in-the-future","non-in-the-future",1954539413),(function() { 
var G__47002__delegate = function (_,___$1,data,___$2,___$3,p__46998){
var vec__46999 = p__46998;
var message = cljs.core.nth.call(null,vec__46999,(0),null);
if(cljs.core.truth_((function (){var and__30163__auto__ = data;
if(cljs.core.truth_(and__30163__auto__)){
return cljs_time.core.after_QMARK_.call(null,data,(new Date()));
} else {
return and__30163__auto__;
}
})())){
var or__30175__auto__ = message;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return "Date cannot be in the future";
}
} else {
return null;
}
};
var G__47002 = function (_,___$1,data,___$2,___$3,var_args){
var p__46998 = null;
if (arguments.length > 5) {
var G__47003__i = 0, G__47003__a = new Array(arguments.length -  5);
while (G__47003__i < G__47003__a.length) {G__47003__a[G__47003__i] = arguments[G__47003__i + 5]; ++G__47003__i;}
  p__46998 = new cljs.core.IndexedSeq(G__47003__a,0,null);
} 
return G__47002__delegate.call(this,_,___$1,data,___$2,___$3,p__46998);};
G__47002.cljs$lang$maxFixedArity = 5;
G__47002.cljs$lang$applyTo = (function (arglist__47004){
var _ = cljs.core.first(arglist__47004);
arglist__47004 = cljs.core.next(arglist__47004);
var ___$1 = cljs.core.first(arglist__47004);
arglist__47004 = cljs.core.next(arglist__47004);
var data = cljs.core.first(arglist__47004);
arglist__47004 = cljs.core.next(arglist__47004);
var ___$2 = cljs.core.first(arglist__47004);
arglist__47004 = cljs.core.next(arglist__47004);
var ___$3 = cljs.core.first(arglist__47004);
var p__46998 = cljs.core.rest(arglist__47004);
return G__47002__delegate(_,___$1,data,___$2,___$3,p__46998);
});
G__47002.cljs$core$IFn$_invoke$arity$variadic = G__47002__delegate;
return G__47002;
})()
);
cljs.core._add_method.call(null,ote.ui.validation.validate_rule,new cljs.core.Keyword(null,"one-of","one-of",144367098),(function() { 
var G__47006__delegate = function (_,___$1,___$2,row,___$3,keys_and_message){
var keys = ((typeof cljs.core.last.call(null,keys_and_message) === 'string')?cljs.core.butlast.call(null,keys_and_message):keys_and_message);
var message = ((typeof cljs.core.last.call(null,keys_and_message) === 'string')?cljs.core.last.call(null,keys_and_message):["Must be one of: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(clojure.string.join.call(null,", ",cljs.core.map.call(null,cljs.core.comp.call(null,clojure.string.capitalize,cljs.core.name),keys)))].join(''));
if(cljs.core.truth_(cljs.core.some.call(null,((function (keys,message){
return (function (p1__47005_SHARP_){
return !(clojure.string.blank_QMARK_.call(null,p1__47005_SHARP_.call(null,row)));
});})(keys,message))
,keys))){
return null;
} else {
return message;
}
};
var G__47006 = function (_,___$1,___$2,row,___$3,var_args){
var keys_and_message = null;
if (arguments.length > 5) {
var G__47007__i = 0, G__47007__a = new Array(arguments.length -  5);
while (G__47007__i < G__47007__a.length) {G__47007__a[G__47007__i] = arguments[G__47007__i + 5]; ++G__47007__i;}
  keys_and_message = new cljs.core.IndexedSeq(G__47007__a,0,null);
} 
return G__47006__delegate.call(this,_,___$1,___$2,row,___$3,keys_and_message);};
G__47006.cljs$lang$maxFixedArity = 5;
G__47006.cljs$lang$applyTo = (function (arglist__47008){
var _ = cljs.core.first(arglist__47008);
arglist__47008 = cljs.core.next(arglist__47008);
var ___$1 = cljs.core.first(arglist__47008);
arglist__47008 = cljs.core.next(arglist__47008);
var ___$2 = cljs.core.first(arglist__47008);
arglist__47008 = cljs.core.next(arglist__47008);
var row = cljs.core.first(arglist__47008);
arglist__47008 = cljs.core.next(arglist__47008);
var ___$3 = cljs.core.first(arglist__47008);
var keys_and_message = cljs.core.rest(arglist__47008);
return G__47006__delegate(_,___$1,___$2,row,___$3,keys_and_message);
});
G__47006.cljs$core$IFn$_invoke$arity$variadic = G__47006__delegate;
return G__47006;
})()
);
cljs.core._add_method.call(null,ote.ui.validation.validate_rule,new cljs.core.Keyword(null,"unique","unique",329397282),(function() { 
var G__47013__delegate = function (_,name,data,___$1,table,p__47009){
var vec__47010 = p__47009;
var message = cljs.core.nth.call(null,vec__47010,(0),null);
var rows_by_value = cljs.core.group_by.call(null,name,cljs.core.vals.call(null,table));
if((!((data == null))) && ((cljs.core.count.call(null,cljs.core.get.call(null,rows_by_value,data)) > (1)))){
var or__30175__auto__ = message;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return "Value must be unique";
}
} else {
return null;
}
};
var G__47013 = function (_,name,data,___$1,table,var_args){
var p__47009 = null;
if (arguments.length > 5) {
var G__47014__i = 0, G__47014__a = new Array(arguments.length -  5);
while (G__47014__i < G__47014__a.length) {G__47014__a[G__47014__i] = arguments[G__47014__i + 5]; ++G__47014__i;}
  p__47009 = new cljs.core.IndexedSeq(G__47014__a,0,null);
} 
return G__47013__delegate.call(this,_,name,data,___$1,table,p__47009);};
G__47013.cljs$lang$maxFixedArity = 5;
G__47013.cljs$lang$applyTo = (function (arglist__47015){
var _ = cljs.core.first(arglist__47015);
arglist__47015 = cljs.core.next(arglist__47015);
var name = cljs.core.first(arglist__47015);
arglist__47015 = cljs.core.next(arglist__47015);
var data = cljs.core.first(arglist__47015);
arglist__47015 = cljs.core.next(arglist__47015);
var ___$1 = cljs.core.first(arglist__47015);
arglist__47015 = cljs.core.next(arglist__47015);
var table = cljs.core.first(arglist__47015);
var p__47009 = cljs.core.rest(arglist__47015);
return G__47013__delegate(_,name,data,___$1,table,p__47009);
});
G__47013.cljs$core$IFn$_invoke$arity$variadic = G__47013__delegate;
return G__47013;
})()
);
cljs.core._add_method.call(null,ote.ui.validation.validate_rule,new cljs.core.Keyword(null,"date-after-field","date-after-field",426819006),(function() { 
var G__47020__delegate = function (_,___$1,data,row,___$2,p__47016){
var vec__47017 = p__47016;
var key = cljs.core.nth.call(null,vec__47017,(0),null);
var message = cljs.core.nth.call(null,vec__47017,(1),null);
if(cljs.core.truth_((function (){var and__30163__auto__ = key.call(null,row);
if(cljs.core.truth_(and__30163__auto__)){
return cljs_time.core.before_QMARK_.call(null,data,key.call(null,row));
} else {
return and__30163__auto__;
}
})())){
var or__30175__auto__ = message;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return ["Date must be after ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(ote.format.pvm.call(null,key.call(null,row)))].join('');
}
} else {
return null;
}
};
var G__47020 = function (_,___$1,data,row,___$2,var_args){
var p__47016 = null;
if (arguments.length > 5) {
var G__47021__i = 0, G__47021__a = new Array(arguments.length -  5);
while (G__47021__i < G__47021__a.length) {G__47021__a[G__47021__i] = arguments[G__47021__i + 5]; ++G__47021__i;}
  p__47016 = new cljs.core.IndexedSeq(G__47021__a,0,null);
} 
return G__47020__delegate.call(this,_,___$1,data,row,___$2,p__47016);};
G__47020.cljs$lang$maxFixedArity = 5;
G__47020.cljs$lang$applyTo = (function (arglist__47022){
var _ = cljs.core.first(arglist__47022);
arglist__47022 = cljs.core.next(arglist__47022);
var ___$1 = cljs.core.first(arglist__47022);
arglist__47022 = cljs.core.next(arglist__47022);
var data = cljs.core.first(arglist__47022);
arglist__47022 = cljs.core.next(arglist__47022);
var row = cljs.core.first(arglist__47022);
arglist__47022 = cljs.core.next(arglist__47022);
var ___$2 = cljs.core.first(arglist__47022);
var p__47016 = cljs.core.rest(arglist__47022);
return G__47020__delegate(_,___$1,data,row,___$2,p__47016);
});
G__47020.cljs$core$IFn$_invoke$arity$variadic = G__47020__delegate;
return G__47020;
})()
);
cljs.core._add_method.call(null,ote.ui.validation.validate_rule,new cljs.core.Keyword(null,"date-after","date-after",-901666462),(function() { 
var G__47027__delegate = function (_,___$1,data,___$2,___$3,p__47023){
var vec__47024 = p__47023;
var comparison_date = cljs.core.nth.call(null,vec__47024,(0),null);
var message = cljs.core.nth.call(null,vec__47024,(1),null);
if(cljs.core.truth_((function (){var and__30163__auto__ = comparison_date;
if(cljs.core.truth_(and__30163__auto__)){
return cljs_time.core.before_QMARK_.call(null,data,comparison_date);
} else {
return and__30163__auto__;
}
})())){
var or__30175__auto__ = message;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return ["Date must be after  ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(ote.format.pvm.call(null,comparison_date))].join('');
}
} else {
return null;
}
};
var G__47027 = function (_,___$1,data,___$2,___$3,var_args){
var p__47023 = null;
if (arguments.length > 5) {
var G__47028__i = 0, G__47028__a = new Array(arguments.length -  5);
while (G__47028__i < G__47028__a.length) {G__47028__a[G__47028__i] = arguments[G__47028__i + 5]; ++G__47028__i;}
  p__47023 = new cljs.core.IndexedSeq(G__47028__a,0,null);
} 
return G__47027__delegate.call(this,_,___$1,data,___$2,___$3,p__47023);};
G__47027.cljs$lang$maxFixedArity = 5;
G__47027.cljs$lang$applyTo = (function (arglist__47029){
var _ = cljs.core.first(arglist__47029);
arglist__47029 = cljs.core.next(arglist__47029);
var ___$1 = cljs.core.first(arglist__47029);
arglist__47029 = cljs.core.next(arglist__47029);
var data = cljs.core.first(arglist__47029);
arglist__47029 = cljs.core.next(arglist__47029);
var ___$2 = cljs.core.first(arglist__47029);
arglist__47029 = cljs.core.next(arglist__47029);
var ___$3 = cljs.core.first(arglist__47029);
var p__47023 = cljs.core.rest(arglist__47029);
return G__47027__delegate(_,___$1,data,___$2,___$3,p__47023);
});
G__47027.cljs$core$IFn$_invoke$arity$variadic = G__47027__delegate;
return G__47027;
})()
);
cljs.core._add_method.call(null,ote.ui.validation.validate_rule,new cljs.core.Keyword(null,"date-before","date-before",302148478),(function() { 
var G__47034__delegate = function (_,___$1,data,___$2,___$3,p__47030){
var vec__47031 = p__47030;
var comparison_date = cljs.core.nth.call(null,vec__47031,(0),null);
var message = cljs.core.nth.call(null,vec__47031,(1),null);
if(cljs.core.truth_((function (){var and__30163__auto__ = data;
if(cljs.core.truth_(and__30163__auto__)){
var and__30163__auto____$1 = comparison_date;
if(cljs.core.truth_(and__30163__auto____$1)){
return cljs.core.not.call(null,cljs_time.core.before_QMARK_.call(null,data,comparison_date));
} else {
return and__30163__auto____$1;
}
} else {
return and__30163__auto__;
}
})())){
var or__30175__auto__ = message;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return ["Date must be before ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(ote.format.pvm.call(null,comparison_date))].join('');
}
} else {
return null;
}
};
var G__47034 = function (_,___$1,data,___$2,___$3,var_args){
var p__47030 = null;
if (arguments.length > 5) {
var G__47035__i = 0, G__47035__a = new Array(arguments.length -  5);
while (G__47035__i < G__47035__a.length) {G__47035__a[G__47035__i] = arguments[G__47035__i + 5]; ++G__47035__i;}
  p__47030 = new cljs.core.IndexedSeq(G__47035__a,0,null);
} 
return G__47034__delegate.call(this,_,___$1,data,___$2,___$3,p__47030);};
G__47034.cljs$lang$maxFixedArity = 5;
G__47034.cljs$lang$applyTo = (function (arglist__47036){
var _ = cljs.core.first(arglist__47036);
arglist__47036 = cljs.core.next(arglist__47036);
var ___$1 = cljs.core.first(arglist__47036);
arglist__47036 = cljs.core.next(arglist__47036);
var data = cljs.core.first(arglist__47036);
arglist__47036 = cljs.core.next(arglist__47036);
var ___$2 = cljs.core.first(arglist__47036);
arglist__47036 = cljs.core.next(arglist__47036);
var ___$3 = cljs.core.first(arglist__47036);
var p__47030 = cljs.core.rest(arglist__47036);
return G__47034__delegate(_,___$1,data,___$2,___$3,p__47030);
});
G__47034.cljs$core$IFn$_invoke$arity$variadic = G__47034__delegate;
return G__47034;
})()
);
ote.ui.validation.year_month_and_day = cljs.core.juxt.call(null,cljs_time.core.year,cljs_time.core.month,cljs_time.core.day);
cljs.core._add_method.call(null,ote.ui.validation.validate_rule,new cljs.core.Keyword(null,"same-date","same-date",1532873624),(function() { 
var G__47041__delegate = function (_,___$1,data,___$2,___$3,p__47037){
var vec__47038 = p__47037;
var comparison_date = cljs.core.nth.call(null,vec__47038,(0),null);
var message = cljs.core.nth.call(null,vec__47038,(1),null);
if(cljs.core.truth_((function (){var and__30163__auto__ = data;
if(cljs.core.truth_(and__30163__auto__)){
var and__30163__auto____$1 = comparison_date;
if(cljs.core.truth_(and__30163__auto____$1)){
return cljs.core.not_EQ_.call(null,ote.ui.validation.year_month_and_day.call(null,data),ote.ui.validation.year_month_and_day.call(null,comparison_date));
} else {
return and__30163__auto____$1;
}
} else {
return and__30163__auto__;
}
})())){
var or__30175__auto__ = message;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return ["Date must ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(ote.format.pvm.call(null,comparison_date))].join('');
}
} else {
return null;
}
};
var G__47041 = function (_,___$1,data,___$2,___$3,var_args){
var p__47037 = null;
if (arguments.length > 5) {
var G__47042__i = 0, G__47042__a = new Array(arguments.length -  5);
while (G__47042__i < G__47042__a.length) {G__47042__a[G__47042__i] = arguments[G__47042__i + 5]; ++G__47042__i;}
  p__47037 = new cljs.core.IndexedSeq(G__47042__a,0,null);
} 
return G__47041__delegate.call(this,_,___$1,data,___$2,___$3,p__47037);};
G__47041.cljs$lang$maxFixedArity = 5;
G__47041.cljs$lang$applyTo = (function (arglist__47043){
var _ = cljs.core.first(arglist__47043);
arglist__47043 = cljs.core.next(arglist__47043);
var ___$1 = cljs.core.first(arglist__47043);
arglist__47043 = cljs.core.next(arglist__47043);
var data = cljs.core.first(arglist__47043);
arglist__47043 = cljs.core.next(arglist__47043);
var ___$2 = cljs.core.first(arglist__47043);
arglist__47043 = cljs.core.next(arglist__47043);
var ___$3 = cljs.core.first(arglist__47043);
var p__47037 = cljs.core.rest(arglist__47043);
return G__47041__delegate(_,___$1,data,___$2,___$3,p__47037);
});
G__47041.cljs$core$IFn$_invoke$arity$variadic = G__47041__delegate;
return G__47041;
})()
);
cljs.core._add_method.call(null,ote.ui.validation.validate_rule,new cljs.core.Keyword(null,"number-range","number-range",653647421),(function() { 
var G__47048__delegate = function (_,___$1,data,___$2,___$3,p__47044){
var vec__47045 = p__47044;
var min_value = cljs.core.nth.call(null,vec__47045,(0),null);
var max_value = cljs.core.nth.call(null,vec__47045,(1),null);
var message = cljs.core.nth.call(null,vec__47045,(2),null);
if(((min_value <= data)) && ((data <= max_value))){
return null;
} else {
var or__30175__auto__ = message;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return ["Number must be between ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(min_value)," and ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(max_value)].join('');
}
}
};
var G__47048 = function (_,___$1,data,___$2,___$3,var_args){
var p__47044 = null;
if (arguments.length > 5) {
var G__47049__i = 0, G__47049__a = new Array(arguments.length -  5);
while (G__47049__i < G__47049__a.length) {G__47049__a[G__47049__i] = arguments[G__47049__i + 5]; ++G__47049__i;}
  p__47044 = new cljs.core.IndexedSeq(G__47049__a,0,null);
} 
return G__47048__delegate.call(this,_,___$1,data,___$2,___$3,p__47044);};
G__47048.cljs$lang$maxFixedArity = 5;
G__47048.cljs$lang$applyTo = (function (arglist__47050){
var _ = cljs.core.first(arglist__47050);
arglist__47050 = cljs.core.next(arglist__47050);
var ___$1 = cljs.core.first(arglist__47050);
arglist__47050 = cljs.core.next(arglist__47050);
var data = cljs.core.first(arglist__47050);
arglist__47050 = cljs.core.next(arglist__47050);
var ___$2 = cljs.core.first(arglist__47050);
arglist__47050 = cljs.core.next(arglist__47050);
var ___$3 = cljs.core.first(arglist__47050);
var p__47044 = cljs.core.rest(arglist__47050);
return G__47048__delegate(_,___$1,data,___$2,___$3,p__47044);
});
G__47048.cljs$core$IFn$_invoke$arity$variadic = G__47048__delegate;
return G__47048;
})()
);
cljs.core._add_method.call(null,ote.ui.validation.validate_rule,new cljs.core.Keyword(null,"business-id","business-id",1368965728),(function() { 
var G__47061__delegate = function (_,___$1,data,___$2,___$3,p__47051){
var vec__47052 = p__47051;
var message = cljs.core.nth.call(null,vec__47052,(0),null);
var and__30163__auto__ = data;
if(cljs.core.truth_(and__30163__auto__)){
var vec__47055 = clojure.string.split.call(null,data,/-/);
var id = cljs.core.nth.call(null,vec__47055,(0),null);
var check = cljs.core.nth.call(null,vec__47055,(1),null);
var split = vec__47055;
var vec__47058 = clojure.string.split.call(null,data,/\d+/);
var id_part = cljs.core.nth.call(null,vec__47058,(0),null);
var separator = cljs.core.nth.call(null,vec__47058,(1),null);
var check_part = cljs.core.nth.call(null,vec__47058,(2),null);
if((cljs.core._EQ_.call(null,(9),cljs.core.count.call(null,data))) && (cljs.core._EQ_.call(null,(2),cljs.core.count.call(null,split))) && (cljs.core._EQ_.call(null,(7),cljs.core.count.call(null,id))) && (cljs.core._EQ_.call(null,(1),cljs.core.count.call(null,check))) && (cljs.core.empty_QMARK_.call(null,id_part)) && (cljs.core._EQ_.call(null,"-",separator)) && ((check_part == null))){
return null;
} else {
var or__30175__auto__ = message;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return "Y-tunnuksen pit\u00E4\u00E4 olla 7 numeroa, v\u00E4liviiva, ja tarkastusnumero.";
}
}
} else {
return and__30163__auto__;
}
};
var G__47061 = function (_,___$1,data,___$2,___$3,var_args){
var p__47051 = null;
if (arguments.length > 5) {
var G__47062__i = 0, G__47062__a = new Array(arguments.length -  5);
while (G__47062__i < G__47062__a.length) {G__47062__a[G__47062__i] = arguments[G__47062__i + 5]; ++G__47062__i;}
  p__47051 = new cljs.core.IndexedSeq(G__47062__a,0,null);
} 
return G__47061__delegate.call(this,_,___$1,data,___$2,___$3,p__47051);};
G__47061.cljs$lang$maxFixedArity = 5;
G__47061.cljs$lang$applyTo = (function (arglist__47063){
var _ = cljs.core.first(arglist__47063);
arglist__47063 = cljs.core.next(arglist__47063);
var ___$1 = cljs.core.first(arglist__47063);
arglist__47063 = cljs.core.next(arglist__47063);
var data = cljs.core.first(arglist__47063);
arglist__47063 = cljs.core.next(arglist__47063);
var ___$2 = cljs.core.first(arglist__47063);
arglist__47063 = cljs.core.next(arglist__47063);
var ___$3 = cljs.core.first(arglist__47063);
var p__47051 = cljs.core.rest(arglist__47063);
return G__47061__delegate(_,___$1,data,___$2,___$3,p__47051);
});
G__47061.cljs$core$IFn$_invoke$arity$variadic = G__47061__delegate;
return G__47061;
})()
);
cljs.core._add_method.call(null,ote.ui.validation.validate_rule,new cljs.core.Keyword(null,"postal-code","postal-code",368585871),(function() { 
var G__47068__delegate = function (_,___$1,data,___$2,___$3,p__47064){
var vec__47065 = p__47064;
var message = cljs.core.nth.call(null,vec__47065,(0),null);
if((cljs.core.not.call(null,ote.ui.validation.empty_value_QMARK_.call(null,data))) && (cljs.core.not.call(null,cljs.core.re_matches.call(null,/^\d{5}$/,data)))){
var or__30175__auto__ = message;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"common-texts","common-texts",-934994303),new cljs.core.Keyword(null,"invalid-postal-code","invalid-postal-code",-155556464)], null));
}
} else {
return null;
}
};
var G__47068 = function (_,___$1,data,___$2,___$3,var_args){
var p__47064 = null;
if (arguments.length > 5) {
var G__47069__i = 0, G__47069__a = new Array(arguments.length -  5);
while (G__47069__i < G__47069__a.length) {G__47069__a[G__47069__i] = arguments[G__47069__i + 5]; ++G__47069__i;}
  p__47064 = new cljs.core.IndexedSeq(G__47069__a,0,null);
} 
return G__47068__delegate.call(this,_,___$1,data,___$2,___$3,p__47064);};
G__47068.cljs$lang$maxFixedArity = 5;
G__47068.cljs$lang$applyTo = (function (arglist__47070){
var _ = cljs.core.first(arglist__47070);
arglist__47070 = cljs.core.next(arglist__47070);
var ___$1 = cljs.core.first(arglist__47070);
arglist__47070 = cljs.core.next(arglist__47070);
var data = cljs.core.first(arglist__47070);
arglist__47070 = cljs.core.next(arglist__47070);
var ___$2 = cljs.core.first(arglist__47070);
arglist__47070 = cljs.core.next(arglist__47070);
var ___$3 = cljs.core.first(arglist__47070);
var p__47064 = cljs.core.rest(arglist__47070);
return G__47068__delegate(_,___$1,data,___$2,___$3,p__47064);
});
G__47068.cljs$core$IFn$_invoke$arity$variadic = G__47068__delegate;
return G__47068;
})()
);
/**
 * Returns all validation errors for a field as a sequence. If the sequence is empty,
 *   validation passed without errors.
 */
ote.ui.validation.validate_rules = (function ote$ui$validation$validate_rules(name,data,row,table,rules){
return cljs.core.keep.call(null,(function (rule){
if(cljs.core.fn_QMARK_.call(null,rule)){
return rule.call(null,data,row);
} else {
var vec__47071 = rule;
var seq__47072 = cljs.core.seq.call(null,vec__47071);
var first__47073 = cljs.core.first.call(null,seq__47072);
var seq__47072__$1 = cljs.core.next.call(null,seq__47072);
var rule__$1 = first__47073;
var options = seq__47072__$1;
return cljs.core.apply.call(null,ote.ui.validation.validate_rule,rule__$1,name,data,row,table,options);
}
}),rules);
});
/**
 * Validate all fields of a single row/form of data.
 *   Returns a map of {field-name [errors]}.
 *   Type selects the validations to use and must be one of: `#{:validate :warn :notice}`.
 *   The default type is `:validate`.
 */
ote.ui.validation.validate_row = (function ote$ui$validation$validate_row(var_args){
var G__47075 = arguments.length;
switch (G__47075) {
case 3:
return ote.ui.validation.validate_row.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return ote.ui.validation.validate_row.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

ote.ui.validation.validate_row.cljs$core$IFn$_invoke$arity$3 = (function (table,row,schemas){
return ote.ui.validation.validate_row.call(null,table,row,schemas,new cljs.core.Keyword(null,"validate","validate",-201300827));
});

ote.ui.validation.validate_row.cljs$core$IFn$_invoke$arity$4 = (function (table,row,schemas,type){
var v = cljs.core.PersistentArrayMap.EMPTY;
var G__47079 = schemas;
var vec__47080 = G__47079;
var seq__47081 = cljs.core.seq.call(null,vec__47080);
var first__47082 = cljs.core.first.call(null,seq__47081);
var seq__47081__$1 = cljs.core.next.call(null,seq__47081);
var s = first__47082;
var schemas__$1 = seq__47081__$1;
var v__$1 = v;
var G__47079__$1 = G__47079;
while(true){
var v__$2 = v__$1;
var vec__47083 = G__47079__$1;
var seq__47084 = cljs.core.seq.call(null,vec__47083);
var first__47085 = cljs.core.first.call(null,seq__47084);
var seq__47084__$1 = cljs.core.next.call(null,seq__47084);
var s__$1 = first__47085;
var schemas__$2 = seq__47084__$1;
if(cljs.core.not.call(null,s__$1)){
return v__$2;
} else {
var map__47086 = s__$1;
var map__47086__$1 = ((((!((map__47086 == null)))?((((map__47086.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__47086.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__47086):map__47086);
var name = cljs.core.get.call(null,map__47086__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var read = cljs.core.get.call(null,map__47086__$1,new cljs.core.Keyword(null,"read","read",1140058661));
var validoi = type.call(null,s__$1);
if(cljs.core.empty_QMARK_.call(null,validoi)){
var G__47089 = v__$2;
var G__47090 = schemas__$2;
v__$1 = G__47089;
G__47079__$1 = G__47090;
continue;
} else {
var errors = ote.ui.validation.validate_rules.call(null,name,(cljs.core.truth_(read)?read.call(null,row):cljs.core.get.call(null,row,name)),row,table,validoi);
var G__47091 = ((cljs.core.empty_QMARK_.call(null,errors))?v__$2:cljs.core.assoc.call(null,v__$2,name,errors));
var G__47092 = schemas__$2;
v__$1 = G__47091;
G__47079__$1 = G__47092;
continue;
}
}
break;
}
});

ote.ui.validation.validate_row.cljs$lang$maxFixedArity = 4;

/**
 * Returns a sequence of schemas that are marked as required and are missing a value.
 */
ote.ui.validation.missing_required_fields = (function ote$ui$validation$missing_required_fields(row,skeema){
return cljs.core.keep.call(null,(function (p__47093){
var map__47094 = p__47093;
var map__47094__$1 = ((((!((map__47094 == null)))?((((map__47094.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__47094.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__47094):map__47094);
var s = map__47094__$1;
var required_QMARK_ = cljs.core.get.call(null,map__47094__$1,new cljs.core.Keyword(null,"required?","required?",-872514462));
var read = cljs.core.get.call(null,map__47094__$1,new cljs.core.Keyword(null,"read","read",1140058661));
var name = cljs.core.get.call(null,map__47094__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var type = cljs.core.get.call(null,map__47094__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var is_empty_QMARK_ = cljs.core.get.call(null,map__47094__$1,new cljs.core.Keyword(null,"is-empty?","is-empty?",-1881285798));
if(cljs.core.truth_((function (){var and__30163__auto__ = required_QMARK_;
if(cljs.core.truth_(and__30163__auto__)){
return (function (){var or__30175__auto__ = is_empty_QMARK_;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return ote.ui.validation.empty_value_QMARK_;
}
})().call(null,(cljs.core.truth_(read)?read.call(null,row):cljs.core.get.call(null,row,name)));
} else {
return and__30163__auto__;
}
})())){
return s;
} else {
return null;
}
}),skeema);
});

//# sourceMappingURL=validation.js.map?rel=1510137284856
