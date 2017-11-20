// Compiled by ClojureScript 1.9.908 {}
goog.provide('tuck.core');
goog.require('cljs.core');
goog.require('reagent.core');
goog.require('cljs.spec.alpha');
/**
 * Bound during process-event to the current UI send function.
 */
tuck.core._STAR_current_send_function_STAR_ = null;
/**
 * Bound to false when replaying events to disable side-effects.
 */
tuck.core._STAR_allow_actions_STAR_ = true;

/**
 * @interface
 */
tuck.core.Event = function(){};

/**
 * Process this event for the current app state. Must return new state.
 */
tuck.core.process_event = (function tuck$core$process_event(this$,app_state){
if((!((this$ == null))) && (!((this$.tuck$core$Event$process_event$arity$2 == null)))){
return this$.tuck$core$Event$process_event$arity$2(this$,app_state);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (tuck.core.process_event[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$,app_state);
} else {
var m__30909__auto____$1 = (tuck.core.process_event["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$,app_state);
} else {
throw cljs.core.missing_protocol.call(null,"Event.process-event",this$);
}
}
}
});


/**
* @constructor
 * @implements {cljs.core.IRecord}
 * @implements {cljs.core.IEquiv}
 * @implements {cljs.core.IHash}
 * @implements {cljs.core.ICollection}
 * @implements {cljs.core.ICounted}
 * @implements {tuck.core.Event}
 * @implements {cljs.core.ISeqable}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.ICloneable}
 * @implements {cljs.core.IPrintWithWriter}
 * @implements {cljs.core.IIterable}
 * @implements {cljs.core.IWithMeta}
 * @implements {cljs.core.IAssociative}
 * @implements {cljs.core.IMap}
 * @implements {cljs.core.ILookup}
*/
tuck.core.UpdateAt = (function (event,key_path,__meta,__extmap,__hash){
this.event = event;
this.key_path = key_path;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
tuck.core.UpdateAt.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

tuck.core.UpdateAt.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k40628,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__40632 = k40628;
var G__40632__$1 = (((G__40632 instanceof cljs.core.Keyword))?G__40632.fqn:null);
switch (G__40632__$1) {
case "event":
return self__.event;

break;
case "key-path":
return self__.key_path;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k40628,else__30866__auto__);

}
});

tuck.core.UpdateAt.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#tuck.core.UpdateAt{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"event","event",301435442),self__.event],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"key-path","key-path",878473308),self__.key_path],null))], null),self__.__extmap));
});

tuck.core.UpdateAt.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__40627){
var self__ = this;
var G__40627__$1 = this;
return (new cljs.core.RecordIter((0),G__40627__$1,2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"event","event",301435442),new cljs.core.Keyword(null,"key-path","key-path",878473308)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

tuck.core.UpdateAt.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

tuck.core.UpdateAt.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new tuck.core.UpdateAt(self__.event,self__.key_path,self__.__meta,self__.__extmap,self__.__hash));
});

tuck.core.UpdateAt.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (2 + cljs.core.count.call(null,self__.__extmap));
});

tuck.core.UpdateAt.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (181669274 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

tuck.core.UpdateAt.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this40629,other40630){
var self__ = this;
var this40629__$1 = this;
return (!((other40630 == null))) && ((this40629__$1.constructor === other40630.constructor)) && (cljs.core._EQ_.call(null,this40629__$1.event,other40630.event)) && (cljs.core._EQ_.call(null,this40629__$1.key_path,other40630.key_path)) && (cljs.core._EQ_.call(null,this40629__$1.__extmap,other40630.__extmap));
});

tuck.core.UpdateAt.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

tuck.core.UpdateAt.prototype.tuck$core$Event$process_event$arity$2 = (function (_,app){
var self__ = this;
var ___$1 = this;
return cljs.core.update_in.call(null,app,self__.key_path,((function (___$1){
return (function (p1__40626_SHARP_){
return tuck.core.process_event.call(null,self__.event,p1__40626_SHARP_);
});})(___$1))
);
});

tuck.core.UpdateAt.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"event","event",301435442),null,new cljs.core.Keyword(null,"key-path","key-path",878473308),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new tuck.core.UpdateAt(self__.event,self__.key_path,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

tuck.core.UpdateAt.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__40627){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__40633 = cljs.core.keyword_identical_QMARK_;
var expr__40634 = k__30871__auto__;
if(cljs.core.truth_(pred__40633.call(null,new cljs.core.Keyword(null,"event","event",301435442),expr__40634))){
return (new tuck.core.UpdateAt(G__40627,self__.key_path,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__40633.call(null,new cljs.core.Keyword(null,"key-path","key-path",878473308),expr__40634))){
return (new tuck.core.UpdateAt(self__.event,G__40627,self__.__meta,self__.__extmap,null));
} else {
return (new tuck.core.UpdateAt(self__.event,self__.key_path,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__40627),null));
}
}
});

tuck.core.UpdateAt.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"event","event",301435442),self__.event],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"key-path","key-path",878473308),self__.key_path],null))], null),self__.__extmap));
});

tuck.core.UpdateAt.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__40627){
var self__ = this;
var this__30862__auto____$1 = this;
return (new tuck.core.UpdateAt(self__.event,self__.key_path,G__40627,self__.__extmap,self__.__hash));
});

tuck.core.UpdateAt.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

tuck.core.UpdateAt.getBasis = (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"event","event",1941966969,null),new cljs.core.Symbol(null,"key-path","key-path",-1775962461,null)], null);
});

tuck.core.UpdateAt.cljs$lang$type = true;

tuck.core.UpdateAt.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"tuck.core/UpdateAt");
});

tuck.core.UpdateAt.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"tuck.core/UpdateAt");
});

tuck.core.__GT_UpdateAt = (function tuck$core$__GT_UpdateAt(event,key_path){
return (new tuck.core.UpdateAt(event,key_path,null,null,null));
});

tuck.core.map__GT_UpdateAt = (function tuck$core$map__GT_UpdateAt(G__40631){
return (new tuck.core.UpdateAt(new cljs.core.Keyword(null,"event","event",301435442).cljs$core$IFn$_invoke$arity$1(G__40631),new cljs.core.Keyword(null,"key-path","key-path",878473308).cljs$core$IFn$_invoke$arity$1(G__40631),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__40631,new cljs.core.Keyword(null,"event","event",301435442),new cljs.core.Keyword(null,"key-path","key-path",878473308))),null));
});

/**
 * Returns a UI event handler that sends the event's value
 *   to the UI message processing after calling constructor with it.
 */
tuck.core.send_value_BANG_ = (function tuck$core$send_value_BANG_(var_args){
var args__31459__auto__ = [];
var len__31452__auto___40640 = arguments.length;
var i__31453__auto___40641 = (0);
while(true){
if((i__31453__auto___40641 < len__31452__auto___40640)){
args__31459__auto__.push((arguments[i__31453__auto___40641]));

var G__40642 = (i__31453__auto___40641 + (1));
i__31453__auto___40641 = G__40642;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((2) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((2)),(0),null)):null);
return tuck.core.send_value_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__31460__auto__);
});

tuck.core.send_value_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (e_BANG_,constructor$,args){
return (function (e){
e.stopPropagation();

return e_BANG_.call(null,cljs.core.apply.call(null,constructor$,e.target.value,args));
});
});

tuck.core.send_value_BANG_.cljs$lang$maxFixedArity = (2);

tuck.core.send_value_BANG_.cljs$lang$applyTo = (function (seq40637){
var G__40638 = cljs.core.first.call(null,seq40637);
var seq40637__$1 = cljs.core.next.call(null,seq40637);
var G__40639 = cljs.core.first.call(null,seq40637__$1);
var seq40637__$2 = cljs.core.next.call(null,seq40637__$1);
return tuck.core.send_value_BANG_.cljs$core$IFn$_invoke$arity$variadic(G__40638,G__40639,seq40637__$2);
});

/**
 * Get the current send function. Must be called from within process-event.
 */
tuck.core.current_send_function = (function tuck$core$current_send_function(){
if(!((tuck.core._STAR_current_send_function_STAR_ == null))){
} else {
throw (new Error(["Assert failed: ","current-send-function called outside of process-event","\n","(not (nil? *current-send-function*))"].join('')));
}

return tuck.core._STAR_current_send_function_STAR_;
});
/**
 * Returns a callback which sends its argument to the UI after wrapping
 *   it with the given constructor. Must be called from within process-event.
 */
tuck.core.send_async_BANG_ = (function tuck$core$send_async_BANG_(var_args){
var args__31459__auto__ = [];
var len__31452__auto___40645 = arguments.length;
var i__31453__auto___40646 = (0);
while(true){
if((i__31453__auto___40646 < len__31452__auto___40645)){
args__31459__auto__.push((arguments[i__31453__auto___40646]));

var G__40647 = (i__31453__auto___40646 + (1));
i__31453__auto___40646 = G__40647;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return tuck.core.send_async_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

tuck.core.send_async_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (constructor$,args){
if(!((tuck.core._STAR_current_send_function_STAR_ == null))){
} else {
throw (new Error(["Assert failed: ","send-async! called outside of process-event","\n","(not (nil? *current-send-function*))"].join('')));
}

var e_BANG_ = tuck.core._STAR_current_send_function_STAR_;
return ((function (e_BANG_){
return (function() { 
var G__40648__delegate = function (values){
return e_BANG_.call(null,cljs.core.apply.call(null,constructor$,cljs.core.concat.call(null,values,args)));
};
var G__40648 = function (var_args){
var values = null;
if (arguments.length > 0) {
var G__40649__i = 0, G__40649__a = new Array(arguments.length -  0);
while (G__40649__i < G__40649__a.length) {G__40649__a[G__40649__i] = arguments[G__40649__i + 0]; ++G__40649__i;}
  values = new cljs.core.IndexedSeq(G__40649__a,0,null);
} 
return G__40648__delegate.call(this,values);};
G__40648.cljs$lang$maxFixedArity = 0;
G__40648.cljs$lang$applyTo = (function (arglist__40650){
var values = cljs.core.seq(arglist__40650);
return G__40648__delegate(values);
});
G__40648.cljs$core$IFn$_invoke$arity$variadic = G__40648__delegate;
return G__40648;
})()
;
;})(e_BANG_))
});

tuck.core.send_async_BANG_.cljs$lang$maxFixedArity = (1);

tuck.core.send_async_BANG_.cljs$lang$applyTo = (function (seq40643){
var G__40644 = cljs.core.first.call(null,seq40643);
var seq40643__$1 = cljs.core.next.call(null,seq40643);
return tuck.core.send_async_BANG_.cljs$core$IFn$_invoke$arity$variadic(G__40644,seq40643__$1);
});

/**
 * Run an action function that may side-effect and schedule asynchronous actions.
 *   The first parameter of the action is the current send function. Actions only run
 *   when *allow-actions* is true.
 */
tuck.core.action_BANG_ = (function tuck$core$action_BANG_(var_args){
var args__31459__auto__ = [];
var len__31452__auto___40653 = arguments.length;
var i__31453__auto___40654 = (0);
while(true){
if((i__31453__auto___40654 < len__31452__auto___40653)){
args__31459__auto__.push((arguments[i__31453__auto___40654]));

var G__40655 = (i__31453__auto___40654 + (1));
i__31453__auto___40654 = G__40655;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return tuck.core.action_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

tuck.core.action_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (action_fn,args){
if(!((tuck.core._STAR_current_send_function_STAR_ == null))){
} else {
throw (new Error(["Assert failed: ","action! called outside of process-event","\n","(not (nil? *current-send-function*))"].join('')));
}

if(tuck.core._STAR_allow_actions_STAR_){
var e_BANG_ = tuck.core._STAR_current_send_function_STAR_;
return cljs.core.apply.call(null,action_fn,e_BANG_,args);
} else {
return null;
}
});

tuck.core.action_BANG_.cljs$lang$maxFixedArity = (1);

tuck.core.action_BANG_.cljs$lang$applyTo = (function (seq40651){
var G__40652 = cljs.core.first.call(null,seq40651);
var seq40651__$1 = cljs.core.next.call(null,seq40651);
return tuck.core.action_BANG_.cljs$core$IFn$_invoke$arity$variadic(G__40652,seq40651__$1);
});

/**
 * Wrap the given UI send function with the given constructor
 *   and optional arguments. Returns a new UI send function where
 *   each event is mapped with the constructor before being sent.
 */
tuck.core.wrap = (function tuck$core$wrap(var_args){
var args__31459__auto__ = [];
var len__31452__auto___40661 = arguments.length;
var i__31453__auto___40662 = (0);
while(true){
if((i__31453__auto___40662 < len__31452__auto___40661)){
args__31459__auto__.push((arguments[i__31453__auto___40662]));

var G__40663 = (i__31453__auto___40662 + (1));
i__31453__auto___40662 = G__40663;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((2) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((2)),(0),null)):null);
return tuck.core.wrap.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__31460__auto__);
});

tuck.core.wrap.cljs$core$IFn$_invoke$arity$variadic = (function (e_BANG_,wrap_constructor,args){
return (function tuck$core$ui_send(event){
if(((!((event == null)))?(((false) || ((cljs.core.PROTOCOL_SENTINEL === event.tuck$core$Event$)))?true:(((!event.cljs$lang$protocol_mask$partition$))?cljs.core.native_satisfies_QMARK_.call(null,tuck.core.Event,event):false)):cljs.core.native_satisfies_QMARK_.call(null,tuck.core.Event,event))){
} else {
throw (new Error("Assert failed: (satisfies? Event event)"));
}

var _STAR_current_send_function_STAR_40660 = tuck.core._STAR_current_send_function_STAR_;
tuck.core._STAR_current_send_function_STAR_ = (function (){var or__30175__auto__ = tuck.core._STAR_current_send_function_STAR_;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return tuck$core$ui_send;
}
})();

try{return e_BANG_.call(null,cljs.core.apply.call(null,wrap_constructor,event,args));
}finally {tuck.core._STAR_current_send_function_STAR_ = _STAR_current_send_function_STAR_40660;
}});
});

tuck.core.wrap.cljs$lang$maxFixedArity = (2);

tuck.core.wrap.cljs$lang$applyTo = (function (seq40656){
var G__40657 = cljs.core.first.call(null,seq40656);
var seq40656__$1 = cljs.core.next.call(null,seq40656);
var G__40658 = cljs.core.first.call(null,seq40656__$1);
var seq40656__$2 = cljs.core.next.call(null,seq40656__$1);
return tuck.core.wrap.cljs$core$IFn$_invoke$arity$variadic(G__40657,G__40658,seq40656__$2);
});

/**
 * Wrap the given UI send function with an UpdateAt event for
 *   the given key-path.
 */
tuck.core.wrap_path = (function tuck$core$wrap_path(var_args){
var args__31459__auto__ = [];
var len__31452__auto___40666 = arguments.length;
var i__31453__auto___40667 = (0);
while(true){
if((i__31453__auto___40667 < len__31452__auto___40666)){
args__31459__auto__.push((arguments[i__31453__auto___40667]));

var G__40668 = (i__31453__auto___40667 + (1));
i__31453__auto___40667 = G__40668;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return tuck.core.wrap_path.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

tuck.core.wrap_path.cljs$core$IFn$_invoke$arity$variadic = (function (e_BANG_,key_path){
return tuck.core.wrap.call(null,e_BANG_,tuck.core.__GT_UpdateAt,cljs.core.vec.call(null,key_path));
});

tuck.core.wrap_path.cljs$lang$maxFixedArity = (1);

tuck.core.wrap_path.cljs$lang$applyTo = (function (seq40664){
var G__40665 = cljs.core.first.call(null,seq40664);
var seq40664__$1 = cljs.core.next.call(null,seq40664);
return tuck.core.wrap_path.cljs$core$IFn$_invoke$arity$variadic(G__40665,seq40664__$1);
});

tuck.core.validate = (function tuck$core$validate(previous_app_state,event,new_app_state,spec,on_invalid_state){
if(cljs.core.truth_((function (){var or__30175__auto__ = (spec == null);
if(or__30175__auto__){
return or__30175__auto__;
} else {
return cljs.spec.alpha.valid_QMARK_.call(null,spec,new_app_state);
}
})())){
return new_app_state;
} else {
return on_invalid_state.call(null,previous_app_state,event,new_app_state,spec);
}
});
tuck.core.control = (function tuck$core$control(var_args){
var G__40670 = arguments.length;
switch (G__40670) {
case 1:
return tuck.core.control.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 4:
return tuck.core.control.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

tuck.core.control.cljs$core$IFn$_invoke$arity$1 = (function (app){
return tuck.core.control.call(null,app,cljs.core.constantly.call(null,null),null,null);
});

tuck.core.control.cljs$core$IFn$_invoke$arity$4 = (function (app,path_fn,spec,on_invalid_state){
return (function tuck$core$ui_send(event){
if(((!((event == null)))?(((false) || ((cljs.core.PROTOCOL_SENTINEL === event.tuck$core$Event$)))?true:(((!event.cljs$lang$protocol_mask$partition$))?cljs.core.native_satisfies_QMARK_.call(null,tuck.core.Event,event):false)):cljs.core.native_satisfies_QMARK_.call(null,tuck.core.Event,event))){
} else {
throw (new Error("Assert failed: (satisfies? Event event)"));
}

var _STAR_current_send_function_STAR_40672 = tuck.core._STAR_current_send_function_STAR_;
tuck.core._STAR_current_send_function_STAR_ = (function (){var or__30175__auto__ = tuck.core._STAR_current_send_function_STAR_;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return tuck$core$ui_send;
}
})();

try{var path = path_fn.call(null,event);
if(cljs.core.truth_(path)){
return cljs.core.swap_BANG_.call(null,app,((function (path,_STAR_current_send_function_STAR_40672){
return (function (current_app_state){
var new_app_state = cljs.core.update_in.call(null,current_app_state,path,((function (path,_STAR_current_send_function_STAR_40672){
return (function (current_app_state_in_path){
return tuck.core.process_event.call(null,event,current_app_state_in_path);
});})(path,_STAR_current_send_function_STAR_40672))
);
return tuck.core.validate.call(null,current_app_state,event,new_app_state,spec,on_invalid_state);
});})(path,_STAR_current_send_function_STAR_40672))
);
} else {
return cljs.core.swap_BANG_.call(null,app,((function (path,_STAR_current_send_function_STAR_40672){
return (function (current_app_state){
var new_app_state = tuck.core.process_event.call(null,event,current_app_state);
return tuck.core.validate.call(null,current_app_state,event,new_app_state,spec,on_invalid_state);
});})(path,_STAR_current_send_function_STAR_40672))
);
}
}finally {tuck.core._STAR_current_send_function_STAR_ = _STAR_current_send_function_STAR_40672;
}});
});

tuck.core.control.cljs$lang$maxFixedArity = 4;

tuck.core.control_with_paths = (function tuck$core$control_with_paths(app,path_fn){
return tuck.core.control.call(null,app,path_fn,null,null);
});
tuck.core.default_on_invalid_state = (function tuck$core$default_on_invalid_state(previous_state,event,new_state,spec){
console.warn("Discarding invalid state after event: ",cljs.core.pr_str.call(null,event),"\n",cljs.spec.alpha.explain_str.call(null,spec,new_state));

return previous_state;
});
/**
 * Entrypoint for tuck. Takes in a reagent atom and a root component.
 *   The root component will be rendered with two parameters: a ui control
 *   function (for sending events to) and the current state of the app atom.
 * 
 *   The optional options map can have the following keys:
 *   :path-fn   If path-fn is provided, it is called to return a path (for update-in)
 *           for the event. If the path-fn returns nil for the event, the event is
 *           applied to the app root. Path-fn is an alternative to wrapping send
 *           functions for routing events to different parts of the state atom.
 *   :spec      If specified, the app state is validate against the spec after each
 *           event. If the new state is invalid the on-invalid-state handler is
 *           called to fix it.
 * 
 *   :on-invalid-state
 *           Handler to call when the app state after an event fails spec validation.
 *           Must return new (fixed) app state. Takes 4 arguments: the previous state,
 *           the event that caused the invalid state, the new invalid state and the
 *           spec it was validated against.
 *           Default implementation logs the event and clojure.spec explain output
 *           and returns the previous valid state.
 * 
 *   For backwards compatibility, if options is a function, it is interpreted to mean
 *   the path-fn.
 * 
 *   The options are evaluated once, when the component is created and changes to
 *   options don't take effect during the component's lifetime.
 */
tuck.core.tuck = (function tuck$core$tuck(var_args){
var G__40675 = arguments.length;
switch (G__40675) {
case 2:
return tuck.core.tuck.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return tuck.core.tuck.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

tuck.core.tuck.cljs$core$IFn$_invoke$arity$2 = (function (app,root_component){
return new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [tuck.core.tuck,app,root_component,cljs.core.PersistentArrayMap.EMPTY], null);
});

tuck.core.tuck.cljs$core$IFn$_invoke$arity$3 = (function (app,root_component,options){
var options__$1 = ((cljs.core.fn_QMARK_.call(null,options))?new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"path-fn","path-fn",-778614691),options], null):options);
var map__40676 = options__$1;
var map__40676__$1 = ((((!((map__40676 == null)))?((((map__40676.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40676.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40676):map__40676);
var path_fn = cljs.core.get.call(null,map__40676__$1,new cljs.core.Keyword(null,"path-fn","path-fn",-778614691));
var spec = cljs.core.get.call(null,map__40676__$1,new cljs.core.Keyword(null,"spec","spec",347520401));
var on_invalid_state = cljs.core.get.call(null,map__40676__$1,new cljs.core.Keyword(null,"on-invalid-state","on-invalid-state",-1286520209));
var e_BANG_ = tuck.core.control.call(null,app,(function (){var or__30175__auto__ = path_fn;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return cljs.core.constantly.call(null,null);
}
})(),spec,(function (){var or__30175__auto__ = on_invalid_state;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return tuck.core.default_on_invalid_state;
}
})());
return ((function (options__$1,map__40676,map__40676__$1,path_fn,spec,on_invalid_state,e_BANG_){
return (function (app__$1,root_component__$1,_){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [root_component__$1,e_BANG_,cljs.core.deref.call(null,app__$1)], null);
});
;})(options__$1,map__40676,map__40676__$1,path_fn,spec,on_invalid_state,e_BANG_))
});

tuck.core.tuck.cljs$lang$maxFixedArity = 3;


//# sourceMappingURL=core.js.map?rel=1510137276564
