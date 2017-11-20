// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.app.controller.transport_operator');
goog.require('cljs.core');
goog.require('tuck.core');
goog.require('ote.communication');
goog.require('ote.ui.form');
goog.require('ote.app.routes');

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
ote.app.controller.transport_operator.EditTransportOperatorState = (function (data,__meta,__extmap,__hash){
this.data = data;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_operator.EditTransportOperatorState.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_operator.EditTransportOperatorState.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51174,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51178 = k51174;
var G__51178__$1 = (((G__51178 instanceof cljs.core.Keyword))?G__51178.fqn:null);
switch (G__51178__$1) {
case "data":
return self__.data;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51174,else__30866__auto__);

}
});

ote.app.controller.transport_operator.EditTransportOperatorState.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-operator.EditTransportOperatorState{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"data","data",-232669377),self__.data],null))], null),self__.__extmap));
});

ote.app.controller.transport_operator.EditTransportOperatorState.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51173){
var self__ = this;
var G__51173__$1 = this;
return (new cljs.core.RecordIter((0),G__51173__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"data","data",-232669377)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_operator.EditTransportOperatorState.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_operator.EditTransportOperatorState.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_operator.EditTransportOperatorState(self__.data,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_operator.EditTransportOperatorState.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_operator.EditTransportOperatorState.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1474041325 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_operator.EditTransportOperatorState.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51175,other51176){
var self__ = this;
var this51175__$1 = this;
return (!((other51176 == null))) && ((this51175__$1.constructor === other51176.constructor)) && (cljs.core._EQ_.call(null,this51175__$1.data,other51176.data)) && (cljs.core._EQ_.call(null,this51175__$1.__extmap,other51176.__extmap));
});

ote.app.controller.transport_operator.EditTransportOperatorState.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"data","data",-232669377),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_operator.EditTransportOperatorState(self__.data,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_operator.EditTransportOperatorState.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51173){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51179 = cljs.core.keyword_identical_QMARK_;
var expr__51180 = k__30871__auto__;
if(cljs.core.truth_(pred__51179.call(null,new cljs.core.Keyword(null,"data","data",-232669377),expr__51180))){
return (new ote.app.controller.transport_operator.EditTransportOperatorState(G__51173,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.transport_operator.EditTransportOperatorState(self__.data,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51173),null));
}
});

ote.app.controller.transport_operator.EditTransportOperatorState.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"data","data",-232669377),self__.data],null))], null),self__.__extmap));
});

ote.app.controller.transport_operator.EditTransportOperatorState.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51173){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_operator.EditTransportOperatorState(self__.data,G__51173,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_operator.EditTransportOperatorState.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_operator.EditTransportOperatorState.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"data","data",1407862150,null)], null);
});

ote.app.controller.transport_operator.EditTransportOperatorState.cljs$lang$type = true;

ote.app.controller.transport_operator.EditTransportOperatorState.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-operator/EditTransportOperatorState");
});

ote.app.controller.transport_operator.EditTransportOperatorState.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-operator/EditTransportOperatorState");
});

ote.app.controller.transport_operator.__GT_EditTransportOperatorState = (function ote$app$controller$transport_operator$__GT_EditTransportOperatorState(data){
return (new ote.app.controller.transport_operator.EditTransportOperatorState(data,null,null,null));
});

ote.app.controller.transport_operator.map__GT_EditTransportOperatorState = (function ote$app$controller$transport_operator$map__GT_EditTransportOperatorState(G__51177){
return (new ote.app.controller.transport_operator.EditTransportOperatorState(new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(G__51177),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51177,new cljs.core.Keyword(null,"data","data",-232669377))),null));
});


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
ote.app.controller.transport_operator.SaveTransportOperator = (function (__meta,__extmap,__hash){
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_operator.SaveTransportOperator.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_operator.SaveTransportOperator.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51184,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51188 = k51184;
switch (G__51188) {
default:
return cljs.core.get.call(null,self__.__extmap,k51184,else__30866__auto__);

}
});

ote.app.controller.transport_operator.SaveTransportOperator.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-operator.SaveTransportOperator{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.transport_operator.SaveTransportOperator.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51183){
var self__ = this;
var G__51183__$1 = this;
return (new cljs.core.RecordIter((0),G__51183__$1,0,cljs.core.PersistentVector.EMPTY,(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_operator.SaveTransportOperator.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_operator.SaveTransportOperator.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_operator.SaveTransportOperator(self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_operator.SaveTransportOperator.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (0 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_operator.SaveTransportOperator.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1922133472 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_operator.SaveTransportOperator.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51185,other51186){
var self__ = this;
var this51185__$1 = this;
return (!((other51186 == null))) && ((this51185__$1.constructor === other51186.constructor)) && (cljs.core._EQ_.call(null,this51185__$1.__extmap,other51186.__extmap));
});

ote.app.controller.transport_operator.SaveTransportOperator.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.EMPTY,k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_operator.SaveTransportOperator(self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_operator.SaveTransportOperator.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51183){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51189 = cljs.core.keyword_identical_QMARK_;
var expr__51190 = k__30871__auto__;
return (new ote.app.controller.transport_operator.SaveTransportOperator(self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51183),null));
});

ote.app.controller.transport_operator.SaveTransportOperator.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.transport_operator.SaveTransportOperator.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51183){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_operator.SaveTransportOperator(G__51183,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_operator.SaveTransportOperator.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_operator.SaveTransportOperator.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

ote.app.controller.transport_operator.SaveTransportOperator.cljs$lang$type = true;

ote.app.controller.transport_operator.SaveTransportOperator.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-operator/SaveTransportOperator");
});

ote.app.controller.transport_operator.SaveTransportOperator.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-operator/SaveTransportOperator");
});

ote.app.controller.transport_operator.__GT_SaveTransportOperator = (function ote$app$controller$transport_operator$__GT_SaveTransportOperator(){
return (new ote.app.controller.transport_operator.SaveTransportOperator(null,null,null));
});

ote.app.controller.transport_operator.map__GT_SaveTransportOperator = (function ote$app$controller$transport_operator$map__GT_SaveTransportOperator(G__51187){
return (new ote.app.controller.transport_operator.SaveTransportOperator(null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51187)),null));
});


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
ote.app.controller.transport_operator.SaveTransportOperatorResponse = (function (data,__meta,__extmap,__hash){
this.data = data;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51194,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51198 = k51194;
var G__51198__$1 = (((G__51198 instanceof cljs.core.Keyword))?G__51198.fqn:null);
switch (G__51198__$1) {
case "data":
return self__.data;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51194,else__30866__auto__);

}
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-operator.SaveTransportOperatorResponse{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"data","data",-232669377),self__.data],null))], null),self__.__extmap));
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51193){
var self__ = this;
var G__51193__$1 = this;
return (new cljs.core.RecordIter((0),G__51193__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"data","data",-232669377)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_operator.SaveTransportOperatorResponse(self__.data,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1149961676 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51195,other51196){
var self__ = this;
var this51195__$1 = this;
return (!((other51196 == null))) && ((this51195__$1.constructor === other51196.constructor)) && (cljs.core._EQ_.call(null,this51195__$1.data,other51196.data)) && (cljs.core._EQ_.call(null,this51195__$1.__extmap,other51196.__extmap));
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"data","data",-232669377),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_operator.SaveTransportOperatorResponse(self__.data,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51193){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51199 = cljs.core.keyword_identical_QMARK_;
var expr__51200 = k__30871__auto__;
if(cljs.core.truth_(pred__51199.call(null,new cljs.core.Keyword(null,"data","data",-232669377),expr__51200))){
return (new ote.app.controller.transport_operator.SaveTransportOperatorResponse(G__51193,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.transport_operator.SaveTransportOperatorResponse(self__.data,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51193),null));
}
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"data","data",-232669377),self__.data],null))], null),self__.__extmap));
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51193){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_operator.SaveTransportOperatorResponse(self__.data,G__51193,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"data","data",1407862150,null)], null);
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.cljs$lang$type = true;

ote.app.controller.transport_operator.SaveTransportOperatorResponse.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-operator/SaveTransportOperatorResponse");
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-operator/SaveTransportOperatorResponse");
});

ote.app.controller.transport_operator.__GT_SaveTransportOperatorResponse = (function ote$app$controller$transport_operator$__GT_SaveTransportOperatorResponse(data){
return (new ote.app.controller.transport_operator.SaveTransportOperatorResponse(data,null,null,null));
});

ote.app.controller.transport_operator.map__GT_SaveTransportOperatorResponse = (function ote$app$controller$transport_operator$map__GT_SaveTransportOperatorResponse(G__51197){
return (new ote.app.controller.transport_operator.SaveTransportOperatorResponse(new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(G__51197),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51197,new cljs.core.Keyword(null,"data","data",-232669377))),null));
});


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
ote.app.controller.transport_operator.TransportOperatorResponse = (function (response,__meta,__extmap,__hash){
this.response = response;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_operator.TransportOperatorResponse.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51204,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51208 = k51204;
var G__51208__$1 = (((G__51208 instanceof cljs.core.Keyword))?G__51208.fqn:null);
switch (G__51208__$1) {
case "response":
return self__.response;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51204,else__30866__auto__);

}
});

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-operator.TransportOperatorResponse{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51203){
var self__ = this;
var G__51203__$1 = this;
return (new cljs.core.RecordIter((0),G__51203__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"response","response",-1068424192)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_operator.TransportOperatorResponse(self__.response,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-391697664 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51205,other51206){
var self__ = this;
var this51205__$1 = this;
return (!((other51206 == null))) && ((this51205__$1.constructor === other51206.constructor)) && (cljs.core._EQ_.call(null,this51205__$1.response,other51206.response)) && (cljs.core._EQ_.call(null,this51205__$1.__extmap,other51206.__extmap));
});

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"response","response",-1068424192),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_operator.TransportOperatorResponse(self__.response,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51203){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51209 = cljs.core.keyword_identical_QMARK_;
var expr__51210 = k__30871__auto__;
if(cljs.core.truth_(pred__51209.call(null,new cljs.core.Keyword(null,"response","response",-1068424192),expr__51210))){
return (new ote.app.controller.transport_operator.TransportOperatorResponse(G__51203,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.transport_operator.TransportOperatorResponse(self__.response,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51203),null));
}
});

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51203){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_operator.TransportOperatorResponse(self__.response,G__51203,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_operator.TransportOperatorResponse.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"response","response",572107335,null)], null);
});

ote.app.controller.transport_operator.TransportOperatorResponse.cljs$lang$type = true;

ote.app.controller.transport_operator.TransportOperatorResponse.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-operator/TransportOperatorResponse");
});

ote.app.controller.transport_operator.TransportOperatorResponse.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-operator/TransportOperatorResponse");
});

ote.app.controller.transport_operator.__GT_TransportOperatorResponse = (function ote$app$controller$transport_operator$__GT_TransportOperatorResponse(response){
return (new ote.app.controller.transport_operator.TransportOperatorResponse(response,null,null,null));
});

ote.app.controller.transport_operator.map__GT_TransportOperatorResponse = (function ote$app$controller$transport_operator$map__GT_TransportOperatorResponse(G__51207){
return (new ote.app.controller.transport_operator.TransportOperatorResponse(new cljs.core.Keyword(null,"response","response",-1068424192).cljs$core$IFn$_invoke$arity$1(G__51207),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51207,new cljs.core.Keyword(null,"response","response",-1068424192))),null));
});


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
ote.app.controller.transport_operator.TransportOperatorDataResponse = (function (response,__meta,__extmap,__hash){
this.response = response;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51214,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51218 = k51214;
var G__51218__$1 = (((G__51218 instanceof cljs.core.Keyword))?G__51218.fqn:null);
switch (G__51218__$1) {
case "response":
return self__.response;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51214,else__30866__auto__);

}
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-operator.TransportOperatorDataResponse{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51213){
var self__ = this;
var G__51213__$1 = this;
return (new cljs.core.RecordIter((0),G__51213__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"response","response",-1068424192)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_operator.TransportOperatorDataResponse(self__.response,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (2133251943 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51215,other51216){
var self__ = this;
var this51215__$1 = this;
return (!((other51216 == null))) && ((this51215__$1.constructor === other51216.constructor)) && (cljs.core._EQ_.call(null,this51215__$1.response,other51216.response)) && (cljs.core._EQ_.call(null,this51215__$1.__extmap,other51216.__extmap));
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"response","response",-1068424192),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_operator.TransportOperatorDataResponse(self__.response,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51213){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51219 = cljs.core.keyword_identical_QMARK_;
var expr__51220 = k__30871__auto__;
if(cljs.core.truth_(pred__51219.call(null,new cljs.core.Keyword(null,"response","response",-1068424192),expr__51220))){
return (new ote.app.controller.transport_operator.TransportOperatorDataResponse(G__51213,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.transport_operator.TransportOperatorDataResponse(self__.response,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51213),null));
}
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51213){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_operator.TransportOperatorDataResponse(self__.response,G__51213,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"response","response",572107335,null)], null);
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.cljs$lang$type = true;

ote.app.controller.transport_operator.TransportOperatorDataResponse.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-operator/TransportOperatorDataResponse");
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-operator/TransportOperatorDataResponse");
});

ote.app.controller.transport_operator.__GT_TransportOperatorDataResponse = (function ote$app$controller$transport_operator$__GT_TransportOperatorDataResponse(response){
return (new ote.app.controller.transport_operator.TransportOperatorDataResponse(response,null,null,null));
});

ote.app.controller.transport_operator.map__GT_TransportOperatorDataResponse = (function ote$app$controller$transport_operator$map__GT_TransportOperatorDataResponse(G__51217){
return (new ote.app.controller.transport_operator.TransportOperatorDataResponse(new cljs.core.Keyword(null,"response","response",-1068424192).cljs$core$IFn$_invoke$arity$1(G__51217),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51217,new cljs.core.Keyword(null,"response","response",-1068424192))),null));
});

ote.app.controller.transport_operator.transport_operator_by_ckan_group_id = (function ote$app$controller$transport_operator$transport_operator_by_ckan_group_id(id){
return ote.communication.get_BANG_.call(null,["transport-operator/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(id)].join(''),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-success","on-success",1786904109),tuck.core.send_async_BANG_.call(null,ote.app.controller.transport_operator.__GT_TransportOperatorResponse)], null));
});
ote.app.controller.transport_operator.transport_operator_data = (function ote$app$controller$transport_operator$transport_operator_data(){
return ote.communication.post_BANG_.call(null,"transport-operator/data",cljs.core.PersistentArrayMap.EMPTY,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-success","on-success",1786904109),tuck.core.send_async_BANG_.call(null,ote.app.controller.transport_operator.__GT_TransportOperatorDataResponse)], null));
});
ote.app.controller.transport_operator.EditTransportOperatorState.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_operator.EditTransportOperatorState.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51223,app){
var map__51224 = p__51223;
var map__51224__$1 = ((((!((map__51224 == null)))?((((map__51224.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51224.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51224):map__51224);
var data = cljs.core.get.call(null,map__51224__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var map__51226 = this;
var map__51226__$1 = ((((!((map__51226 == null)))?((((map__51226.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51226.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51226):map__51226);
var data__$1 = cljs.core.get.call(null,map__51226__$1,new cljs.core.Keyword(null,"data","data",-232669377));
return cljs.core.update.call(null,app,new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982),cljs.core.merge,data__$1);
});

ote.app.controller.transport_operator.SaveTransportOperator.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_operator.SaveTransportOperator.prototype.tuck$core$Event$process_event$arity$2 = (function (_,app){
var ___$1 = this;
var operator_data = ote.ui.form.without_form_metadata.call(null,new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982).cljs$core$IFn$_invoke$arity$1(app));
ote.communication.post_BANG_.call(null,"transport-operator",operator_data,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-success","on-success",1786904109),tuck.core.send_async_BANG_.call(null,ote.app.controller.transport_operator.__GT_SaveTransportOperatorResponse)], null));

return app;
});

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_operator.SaveTransportOperatorResponse.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51228,app){
var map__51229 = p__51228;
var map__51229__$1 = ((((!((map__51229 == null)))?((((map__51229.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51229.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51229):map__51229);
var data = cljs.core.get.call(null,map__51229__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var map__51231 = this;
var map__51231__$1 = ((((!((map__51231 == null)))?((((map__51231.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51231.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51231):map__51231);
var data__$1 = cljs.core.get.call(null,map__51231__$1,new cljs.core.Keyword(null,"data","data",-232669377));
ote.app.routes.navigate_BANG_.call(null,new cljs.core.Keyword(null,"own-services","own-services",-1593467283));

return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982),data__$1,new cljs.core.Keyword(null,"page","page",849072397),new cljs.core.Keyword(null,"own-services","own-services",-1593467283));
});

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_operator.TransportOperatorResponse.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51233,app){
var map__51234 = p__51233;
var map__51234__$1 = ((((!((map__51234 == null)))?((((map__51234.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51234.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51234):map__51234);
var response = cljs.core.get.call(null,map__51234__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
var map__51236 = this;
var map__51236__$1 = ((((!((map__51236 == null)))?((((map__51236.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51236.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51236):map__51236);
var response__$1 = cljs.core.get.call(null,map__51236__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982),response__$1);
});

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_operator.TransportOperatorDataResponse.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51238,app){
var map__51239 = p__51238;
var map__51239__$1 = ((((!((map__51239 == null)))?((((map__51239.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51239.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51239):map__51239);
var response = cljs.core.get.call(null,map__51239__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
var map__51241 = this;
var map__51241__$1 = ((((!((map__51241 == null)))?((((map__51241.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51241.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51241):map__51241);
var response__$1 = cljs.core.get.call(null,map__51241__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982),cljs.core.get.call(null,response__$1,new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982)),new cljs.core.Keyword(null,"transport-services","transport-services",-1601696230),cljs.core.get.call(null,response__$1,new cljs.core.Keyword(null,"transport-service-vector","transport-service-vector",262111307)),new cljs.core.Keyword(null,"user","user",1532431356),cljs.core.get.call(null,response__$1,new cljs.core.Keyword(null,"user","user",1532431356)));
});

//# sourceMappingURL=transport_operator.js.map?rel=1510137290881
