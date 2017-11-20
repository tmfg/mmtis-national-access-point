// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.app.controller.terminal');
goog.require('cljs.core');
goog.require('tuck.core');
goog.require('ote.communication');
goog.require('ote.ui.form');
goog.require('ote.db.transport_operator');
goog.require('ote.db.transport_service');
goog.require('ote.app.controller.place_search');
goog.require('ote.app.controller.transport_service');

/**
* @constructor
 * @implements {cljs.core.IRecord}
 * @implements {cljs.core.IEquiv}
 * @implements {cljs.core.IHash}
 * @implements {cljs.core.ICollection}
 * @implements {cljs.core.ICounted}
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
ote.app.controller.terminal.EditTerminalState = (function (data,__meta,__extmap,__hash){
this.data = data;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.terminal.EditTerminalState.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.terminal.EditTerminalState.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52159,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52163 = k52159;
var G__52163__$1 = (((G__52163 instanceof cljs.core.Keyword))?G__52163.fqn:null);
switch (G__52163__$1) {
case "data":
return self__.data;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k52159,else__30866__auto__);

}
});

ote.app.controller.terminal.EditTerminalState.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.terminal.EditTerminalState{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"data","data",-232669377),self__.data],null))], null),self__.__extmap));
});

ote.app.controller.terminal.EditTerminalState.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52158){
var self__ = this;
var G__52158__$1 = this;
return (new cljs.core.RecordIter((0),G__52158__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"data","data",-232669377)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.terminal.EditTerminalState.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.terminal.EditTerminalState.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.terminal.EditTerminalState(self__.data,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.terminal.EditTerminalState.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.terminal.EditTerminalState.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (254180595 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.terminal.EditTerminalState.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52160,other52161){
var self__ = this;
var this52160__$1 = this;
return (!((other52161 == null))) && ((this52160__$1.constructor === other52161.constructor)) && (cljs.core._EQ_.call(null,this52160__$1.data,other52161.data)) && (cljs.core._EQ_.call(null,this52160__$1.__extmap,other52161.__extmap));
});

ote.app.controller.terminal.EditTerminalState.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"data","data",-232669377),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.terminal.EditTerminalState(self__.data,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.terminal.EditTerminalState.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52158){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52164 = cljs.core.keyword_identical_QMARK_;
var expr__52165 = k__30871__auto__;
if(cljs.core.truth_(pred__52164.call(null,new cljs.core.Keyword(null,"data","data",-232669377),expr__52165))){
return (new ote.app.controller.terminal.EditTerminalState(G__52158,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.terminal.EditTerminalState(self__.data,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52158),null));
}
});

ote.app.controller.terminal.EditTerminalState.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"data","data",-232669377),self__.data],null))], null),self__.__extmap));
});

ote.app.controller.terminal.EditTerminalState.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52158){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.terminal.EditTerminalState(self__.data,G__52158,self__.__extmap,self__.__hash));
});

ote.app.controller.terminal.EditTerminalState.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.terminal.EditTerminalState.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"data","data",1407862150,null)], null);
});

ote.app.controller.terminal.EditTerminalState.cljs$lang$type = true;

ote.app.controller.terminal.EditTerminalState.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.terminal/EditTerminalState");
});

ote.app.controller.terminal.EditTerminalState.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.terminal/EditTerminalState");
});

ote.app.controller.terminal.__GT_EditTerminalState = (function ote$app$controller$terminal$__GT_EditTerminalState(data){
return (new ote.app.controller.terminal.EditTerminalState(data,null,null,null));
});

ote.app.controller.terminal.map__GT_EditTerminalState = (function ote$app$controller$terminal$map__GT_EditTerminalState(G__52162){
return (new ote.app.controller.terminal.EditTerminalState(new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(G__52162),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52162,new cljs.core.Keyword(null,"data","data",-232669377))),null));
});

ote.app.controller.terminal.EditTerminalState.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.terminal.EditTerminalState.prototype.tuck$core$Event$process_event$arity$2 = (function (p__52168,app){
var map__52169 = p__52168;
var map__52169__$1 = ((((!((map__52169 == null)))?((((map__52169.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52169.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52169):map__52169);
var data = cljs.core.get.call(null,map__52169__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var map__52171 = this;
var map__52171__$1 = ((((!((map__52171 == null)))?((((map__52171.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52171.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52171):map__52171);
var data__$1 = cljs.core.get.call(null,map__52171__$1,new cljs.core.Keyword(null,"data","data",-232669377));
return cljs.core.update_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706),new cljs.core.Keyword("ote.db.transport-service","terminal","ote.db.transport-service/terminal",769260653)], null),cljs.core.merge,data__$1);
});

//# sourceMappingURL=terminal.js.map?rel=1510137294441
