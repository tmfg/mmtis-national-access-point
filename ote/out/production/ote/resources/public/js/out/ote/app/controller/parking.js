// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.app.controller.parking');
goog.require('cljs.core');
goog.require('tuck.core');
goog.require('ote.communication');
goog.require('ote.ui.form');
goog.require('ote.db.transport_operator');
goog.require('ote.db.transport_service');
goog.require('ote.app.controller.place_search');

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
ote.app.controller.parking.EditParkingState = (function (data,__meta,__extmap,__hash){
this.data = data;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.parking.EditParkingState.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.parking.EditParkingState.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52504,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52508 = k52504;
var G__52508__$1 = (((G__52508 instanceof cljs.core.Keyword))?G__52508.fqn:null);
switch (G__52508__$1) {
case "data":
return self__.data;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k52504,else__30866__auto__);

}
});

ote.app.controller.parking.EditParkingState.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.parking.EditParkingState{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"data","data",-232669377),self__.data],null))], null),self__.__extmap));
});

ote.app.controller.parking.EditParkingState.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52503){
var self__ = this;
var G__52503__$1 = this;
return (new cljs.core.RecordIter((0),G__52503__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"data","data",-232669377)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.parking.EditParkingState.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.parking.EditParkingState.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.parking.EditParkingState(self__.data,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.parking.EditParkingState.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.parking.EditParkingState.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (631668262 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.parking.EditParkingState.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52505,other52506){
var self__ = this;
var this52505__$1 = this;
return (!((other52506 == null))) && ((this52505__$1.constructor === other52506.constructor)) && (cljs.core._EQ_.call(null,this52505__$1.data,other52506.data)) && (cljs.core._EQ_.call(null,this52505__$1.__extmap,other52506.__extmap));
});

ote.app.controller.parking.EditParkingState.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"data","data",-232669377),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.parking.EditParkingState(self__.data,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.parking.EditParkingState.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52503){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52509 = cljs.core.keyword_identical_QMARK_;
var expr__52510 = k__30871__auto__;
if(cljs.core.truth_(pred__52509.call(null,new cljs.core.Keyword(null,"data","data",-232669377),expr__52510))){
return (new ote.app.controller.parking.EditParkingState(G__52503,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.parking.EditParkingState(self__.data,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52503),null));
}
});

ote.app.controller.parking.EditParkingState.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"data","data",-232669377),self__.data],null))], null),self__.__extmap));
});

ote.app.controller.parking.EditParkingState.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52503){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.parking.EditParkingState(self__.data,G__52503,self__.__extmap,self__.__hash));
});

ote.app.controller.parking.EditParkingState.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.parking.EditParkingState.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"data","data",1407862150,null)], null);
});

ote.app.controller.parking.EditParkingState.cljs$lang$type = true;

ote.app.controller.parking.EditParkingState.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.parking/EditParkingState");
});

ote.app.controller.parking.EditParkingState.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.parking/EditParkingState");
});

ote.app.controller.parking.__GT_EditParkingState = (function ote$app$controller$parking$__GT_EditParkingState(data){
return (new ote.app.controller.parking.EditParkingState(data,null,null,null));
});

ote.app.controller.parking.map__GT_EditParkingState = (function ote$app$controller$parking$map__GT_EditParkingState(G__52507){
return (new ote.app.controller.parking.EditParkingState(new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(G__52507),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52507,new cljs.core.Keyword(null,"data","data",-232669377))),null));
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
ote.app.controller.parking.SaveParkingToDb = (function (__meta,__extmap,__hash){
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.parking.SaveParkingToDb.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.parking.SaveParkingToDb.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52514,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52518 = k52514;
switch (G__52518) {
default:
return cljs.core.get.call(null,self__.__extmap,k52514,else__30866__auto__);

}
});

ote.app.controller.parking.SaveParkingToDb.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.parking.SaveParkingToDb{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.parking.SaveParkingToDb.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52513){
var self__ = this;
var G__52513__$1 = this;
return (new cljs.core.RecordIter((0),G__52513__$1,0,cljs.core.PersistentVector.EMPTY,(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.parking.SaveParkingToDb.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.parking.SaveParkingToDb.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.parking.SaveParkingToDb(self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.parking.SaveParkingToDb.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (0 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.parking.SaveParkingToDb.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-30737165 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.parking.SaveParkingToDb.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52515,other52516){
var self__ = this;
var this52515__$1 = this;
return (!((other52516 == null))) && ((this52515__$1.constructor === other52516.constructor)) && (cljs.core._EQ_.call(null,this52515__$1.__extmap,other52516.__extmap));
});

ote.app.controller.parking.SaveParkingToDb.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.EMPTY,k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.parking.SaveParkingToDb(self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.parking.SaveParkingToDb.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52513){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52519 = cljs.core.keyword_identical_QMARK_;
var expr__52520 = k__30871__auto__;
return (new ote.app.controller.parking.SaveParkingToDb(self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52513),null));
});

ote.app.controller.parking.SaveParkingToDb.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.parking.SaveParkingToDb.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52513){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.parking.SaveParkingToDb(G__52513,self__.__extmap,self__.__hash));
});

ote.app.controller.parking.SaveParkingToDb.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.parking.SaveParkingToDb.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

ote.app.controller.parking.SaveParkingToDb.cljs$lang$type = true;

ote.app.controller.parking.SaveParkingToDb.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.parking/SaveParkingToDb");
});

ote.app.controller.parking.SaveParkingToDb.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.parking/SaveParkingToDb");
});

ote.app.controller.parking.__GT_SaveParkingToDb = (function ote$app$controller$parking$__GT_SaveParkingToDb(){
return (new ote.app.controller.parking.SaveParkingToDb(null,null,null));
});

ote.app.controller.parking.map__GT_SaveParkingToDb = (function ote$app$controller$parking$map__GT_SaveParkingToDb(G__52517){
return (new ote.app.controller.parking.SaveParkingToDb(null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52517)),null));
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
ote.app.controller.parking.HandleParkingResponse = (function (service,__meta,__extmap,__hash){
this.service = service;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.parking.HandleParkingResponse.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.parking.HandleParkingResponse.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52524,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52528 = k52524;
var G__52528__$1 = (((G__52528 instanceof cljs.core.Keyword))?G__52528.fqn:null);
switch (G__52528__$1) {
case "service":
return self__.service;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k52524,else__30866__auto__);

}
});

ote.app.controller.parking.HandleParkingResponse.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.parking.HandleParkingResponse{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"service","service",-1963054559),self__.service],null))], null),self__.__extmap));
});

ote.app.controller.parking.HandleParkingResponse.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52523){
var self__ = this;
var G__52523__$1 = this;
return (new cljs.core.RecordIter((0),G__52523__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"service","service",-1963054559)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.parking.HandleParkingResponse.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.parking.HandleParkingResponse.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.parking.HandleParkingResponse(self__.service,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.parking.HandleParkingResponse.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.parking.HandleParkingResponse.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-1846601505 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.parking.HandleParkingResponse.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52525,other52526){
var self__ = this;
var this52525__$1 = this;
return (!((other52526 == null))) && ((this52525__$1.constructor === other52526.constructor)) && (cljs.core._EQ_.call(null,this52525__$1.service,other52526.service)) && (cljs.core._EQ_.call(null,this52525__$1.__extmap,other52526.__extmap));
});

ote.app.controller.parking.HandleParkingResponse.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"service","service",-1963054559),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.parking.HandleParkingResponse(self__.service,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.parking.HandleParkingResponse.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52523){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52529 = cljs.core.keyword_identical_QMARK_;
var expr__52530 = k__30871__auto__;
if(cljs.core.truth_(pred__52529.call(null,new cljs.core.Keyword(null,"service","service",-1963054559),expr__52530))){
return (new ote.app.controller.parking.HandleParkingResponse(G__52523,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.parking.HandleParkingResponse(self__.service,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52523),null));
}
});

ote.app.controller.parking.HandleParkingResponse.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"service","service",-1963054559),self__.service],null))], null),self__.__extmap));
});

ote.app.controller.parking.HandleParkingResponse.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52523){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.parking.HandleParkingResponse(self__.service,G__52523,self__.__extmap,self__.__hash));
});

ote.app.controller.parking.HandleParkingResponse.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.parking.HandleParkingResponse.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"service","service",-322523032,null)], null);
});

ote.app.controller.parking.HandleParkingResponse.cljs$lang$type = true;

ote.app.controller.parking.HandleParkingResponse.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.parking/HandleParkingResponse");
});

ote.app.controller.parking.HandleParkingResponse.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.parking/HandleParkingResponse");
});

ote.app.controller.parking.__GT_HandleParkingResponse = (function ote$app$controller$parking$__GT_HandleParkingResponse(service){
return (new ote.app.controller.parking.HandleParkingResponse(service,null,null,null));
});

ote.app.controller.parking.map__GT_HandleParkingResponse = (function ote$app$controller$parking$map__GT_HandleParkingResponse(G__52527){
return (new ote.app.controller.parking.HandleParkingResponse(new cljs.core.Keyword(null,"service","service",-1963054559).cljs$core$IFn$_invoke$arity$1(G__52527),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52527,new cljs.core.Keyword(null,"service","service",-1963054559))),null));
});

ote.app.controller.parking.EditParkingState.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.parking.EditParkingState.prototype.tuck$core$Event$process_event$arity$2 = (function (p__52533,app){
var map__52534 = p__52533;
var map__52534__$1 = ((((!((map__52534 == null)))?((((map__52534.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52534.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52534):map__52534);
var data = cljs.core.get.call(null,map__52534__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var map__52536 = this;
var map__52536__$1 = ((((!((map__52536 == null)))?((((map__52536.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52536.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52536):map__52536);
var data__$1 = cljs.core.get.call(null,map__52536__$1,new cljs.core.Keyword(null,"data","data",-232669377));
return cljs.core.update_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706),new cljs.core.Keyword("ote.db.transport-service","rental","ote.db.transport-service/rental",77770772)], null),cljs.core.merge,data__$1);
});

ote.app.controller.parking.SaveParkingToDb.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.parking.SaveParkingToDb.prototype.tuck$core$Event$process_event$arity$2 = (function (_,p__52538){
var map__52539 = p__52538;
var map__52539__$1 = ((((!((map__52539 == null)))?((((map__52539.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52539.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52539):map__52539);
var app = map__52539__$1;
var service = cljs.core.get.call(null,map__52539__$1,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706));
var ___$1 = this;
var service_data = cljs.core.update_in.call(null,cljs.core.update.call(null,cljs.core.assoc.call(null,service,new cljs.core.Keyword("ote.db.transport-service","type","ote.db.transport-service/type",-1555621317),new cljs.core.Keyword(null,"parking","parking",-952236974),new cljs.core.Keyword("ote.db.transport-service","transport-operator-id","ote.db.transport-service/transport-operator-id",1381166108),cljs.core.get_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982),new cljs.core.Keyword("ote.db.transport-operator","id","ote.db.transport-operator/id",343347306)], null))),new cljs.core.Keyword("ote.db.transport-service","parking","ote.db.transport-service/parking",1703320291),ote.ui.form.without_form_metadata),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("ote.db.transport-service","parking","ote.db.transport-service/parking",1703320291),new cljs.core.Keyword("ote.db.transport-service","operation-area","ote.db.transport-service/operation-area",1530382917)], null),ote.app.controller.place_search.place_references);
ote.communication.post_BANG_.call(null,"parking",service_data,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-success","on-success",1786904109),tuck.core.send_async_BANG_.call(null,ote.app.controller.parking.__GT_HandleParkingResponse)], null));

return app;
});

ote.app.controller.parking.HandleParkingResponse.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.parking.HandleParkingResponse.prototype.tuck$core$Event$process_event$arity$2 = (function (p__52541,app){
var map__52542 = p__52541;
var map__52542__$1 = ((((!((map__52542 == null)))?((((map__52542.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52542.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52542):map__52542);
var service = cljs.core.get.call(null,map__52542__$1,new cljs.core.Keyword(null,"service","service",-1963054559));
var map__52544 = this;
var map__52544__$1 = ((((!((map__52544 == null)))?((((map__52544.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52544.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52544):map__52544);
var service__$1 = cljs.core.get.call(null,map__52544__$1,new cljs.core.Keyword(null,"service","service",-1963054559));
return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706),service__$1,new cljs.core.Keyword(null,"page","page",849072397),new cljs.core.Keyword(null,"own-services","own-services",-1593467283));
});

//# sourceMappingURL=parking.js.map?rel=1510137295893
