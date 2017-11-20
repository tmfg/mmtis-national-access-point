// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.app.controller.rental');
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
ote.app.controller.rental.EditRentalState = (function (data,__meta,__extmap,__hash){
this.data = data;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.rental.EditRentalState.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.rental.EditRentalState.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51744,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51748 = k51744;
var G__51748__$1 = (((G__51748 instanceof cljs.core.Keyword))?G__51748.fqn:null);
switch (G__51748__$1) {
case "data":
return self__.data;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51744,else__30866__auto__);

}
});

ote.app.controller.rental.EditRentalState.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.rental.EditRentalState{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"data","data",-232669377),self__.data],null))], null),self__.__extmap));
});

ote.app.controller.rental.EditRentalState.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51743){
var self__ = this;
var G__51743__$1 = this;
return (new cljs.core.RecordIter((0),G__51743__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"data","data",-232669377)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.rental.EditRentalState.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.rental.EditRentalState.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.rental.EditRentalState(self__.data,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.rental.EditRentalState.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.rental.EditRentalState.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1393382932 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.rental.EditRentalState.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51745,other51746){
var self__ = this;
var this51745__$1 = this;
return (!((other51746 == null))) && ((this51745__$1.constructor === other51746.constructor)) && (cljs.core._EQ_.call(null,this51745__$1.data,other51746.data)) && (cljs.core._EQ_.call(null,this51745__$1.__extmap,other51746.__extmap));
});

ote.app.controller.rental.EditRentalState.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"data","data",-232669377),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.rental.EditRentalState(self__.data,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.rental.EditRentalState.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51743){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51749 = cljs.core.keyword_identical_QMARK_;
var expr__51750 = k__30871__auto__;
if(cljs.core.truth_(pred__51749.call(null,new cljs.core.Keyword(null,"data","data",-232669377),expr__51750))){
return (new ote.app.controller.rental.EditRentalState(G__51743,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.rental.EditRentalState(self__.data,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51743),null));
}
});

ote.app.controller.rental.EditRentalState.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"data","data",-232669377),self__.data],null))], null),self__.__extmap));
});

ote.app.controller.rental.EditRentalState.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51743){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.rental.EditRentalState(self__.data,G__51743,self__.__extmap,self__.__hash));
});

ote.app.controller.rental.EditRentalState.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.rental.EditRentalState.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"data","data",1407862150,null)], null);
});

ote.app.controller.rental.EditRentalState.cljs$lang$type = true;

ote.app.controller.rental.EditRentalState.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.rental/EditRentalState");
});

ote.app.controller.rental.EditRentalState.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.rental/EditRentalState");
});

ote.app.controller.rental.__GT_EditRentalState = (function ote$app$controller$rental$__GT_EditRentalState(data){
return (new ote.app.controller.rental.EditRentalState(data,null,null,null));
});

ote.app.controller.rental.map__GT_EditRentalState = (function ote$app$controller$rental$map__GT_EditRentalState(G__51747){
return (new ote.app.controller.rental.EditRentalState(new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(G__51747),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51747,new cljs.core.Keyword(null,"data","data",-232669377))),null));
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
ote.app.controller.rental.SaveRentalToDb = (function (__meta,__extmap,__hash){
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.rental.SaveRentalToDb.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.rental.SaveRentalToDb.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51754,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51758 = k51754;
switch (G__51758) {
default:
return cljs.core.get.call(null,self__.__extmap,k51754,else__30866__auto__);

}
});

ote.app.controller.rental.SaveRentalToDb.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.rental.SaveRentalToDb{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.rental.SaveRentalToDb.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51753){
var self__ = this;
var G__51753__$1 = this;
return (new cljs.core.RecordIter((0),G__51753__$1,0,cljs.core.PersistentVector.EMPTY,(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.rental.SaveRentalToDb.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.rental.SaveRentalToDb.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.rental.SaveRentalToDb(self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.rental.SaveRentalToDb.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (0 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.rental.SaveRentalToDb.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (530137618 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.rental.SaveRentalToDb.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51755,other51756){
var self__ = this;
var this51755__$1 = this;
return (!((other51756 == null))) && ((this51755__$1.constructor === other51756.constructor)) && (cljs.core._EQ_.call(null,this51755__$1.__extmap,other51756.__extmap));
});

ote.app.controller.rental.SaveRentalToDb.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.EMPTY,k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.rental.SaveRentalToDb(self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.rental.SaveRentalToDb.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51753){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51759 = cljs.core.keyword_identical_QMARK_;
var expr__51760 = k__30871__auto__;
return (new ote.app.controller.rental.SaveRentalToDb(self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51753),null));
});

ote.app.controller.rental.SaveRentalToDb.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.rental.SaveRentalToDb.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51753){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.rental.SaveRentalToDb(G__51753,self__.__extmap,self__.__hash));
});

ote.app.controller.rental.SaveRentalToDb.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.rental.SaveRentalToDb.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

ote.app.controller.rental.SaveRentalToDb.cljs$lang$type = true;

ote.app.controller.rental.SaveRentalToDb.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.rental/SaveRentalToDb");
});

ote.app.controller.rental.SaveRentalToDb.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.rental/SaveRentalToDb");
});

ote.app.controller.rental.__GT_SaveRentalToDb = (function ote$app$controller$rental$__GT_SaveRentalToDb(){
return (new ote.app.controller.rental.SaveRentalToDb(null,null,null));
});

ote.app.controller.rental.map__GT_SaveRentalToDb = (function ote$app$controller$rental$map__GT_SaveRentalToDb(G__51757){
return (new ote.app.controller.rental.SaveRentalToDb(null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51757)),null));
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
ote.app.controller.rental.HandleRentalResponse = (function (service,__meta,__extmap,__hash){
this.service = service;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.rental.HandleRentalResponse.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.rental.HandleRentalResponse.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51764,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51768 = k51764;
var G__51768__$1 = (((G__51768 instanceof cljs.core.Keyword))?G__51768.fqn:null);
switch (G__51768__$1) {
case "service":
return self__.service;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51764,else__30866__auto__);

}
});

ote.app.controller.rental.HandleRentalResponse.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.rental.HandleRentalResponse{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"service","service",-1963054559),self__.service],null))], null),self__.__extmap));
});

ote.app.controller.rental.HandleRentalResponse.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51763){
var self__ = this;
var G__51763__$1 = this;
return (new cljs.core.RecordIter((0),G__51763__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"service","service",-1963054559)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.rental.HandleRentalResponse.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.rental.HandleRentalResponse.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.rental.HandleRentalResponse(self__.service,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.rental.HandleRentalResponse.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.rental.HandleRentalResponse.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-707113434 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.rental.HandleRentalResponse.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51765,other51766){
var self__ = this;
var this51765__$1 = this;
return (!((other51766 == null))) && ((this51765__$1.constructor === other51766.constructor)) && (cljs.core._EQ_.call(null,this51765__$1.service,other51766.service)) && (cljs.core._EQ_.call(null,this51765__$1.__extmap,other51766.__extmap));
});

ote.app.controller.rental.HandleRentalResponse.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"service","service",-1963054559),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.rental.HandleRentalResponse(self__.service,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.rental.HandleRentalResponse.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51763){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51769 = cljs.core.keyword_identical_QMARK_;
var expr__51770 = k__30871__auto__;
if(cljs.core.truth_(pred__51769.call(null,new cljs.core.Keyword(null,"service","service",-1963054559),expr__51770))){
return (new ote.app.controller.rental.HandleRentalResponse(G__51763,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.rental.HandleRentalResponse(self__.service,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51763),null));
}
});

ote.app.controller.rental.HandleRentalResponse.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"service","service",-1963054559),self__.service],null))], null),self__.__extmap));
});

ote.app.controller.rental.HandleRentalResponse.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51763){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.rental.HandleRentalResponse(self__.service,G__51763,self__.__extmap,self__.__hash));
});

ote.app.controller.rental.HandleRentalResponse.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.rental.HandleRentalResponse.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"service","service",-322523032,null)], null);
});

ote.app.controller.rental.HandleRentalResponse.cljs$lang$type = true;

ote.app.controller.rental.HandleRentalResponse.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.rental/HandleRentalResponse");
});

ote.app.controller.rental.HandleRentalResponse.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.rental/HandleRentalResponse");
});

ote.app.controller.rental.__GT_HandleRentalResponse = (function ote$app$controller$rental$__GT_HandleRentalResponse(service){
return (new ote.app.controller.rental.HandleRentalResponse(service,null,null,null));
});

ote.app.controller.rental.map__GT_HandleRentalResponse = (function ote$app$controller$rental$map__GT_HandleRentalResponse(G__51767){
return (new ote.app.controller.rental.HandleRentalResponse(new cljs.core.Keyword(null,"service","service",-1963054559).cljs$core$IFn$_invoke$arity$1(G__51767),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51767,new cljs.core.Keyword(null,"service","service",-1963054559))),null));
});

ote.app.controller.rental.EditRentalState.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.rental.EditRentalState.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51773,app){
var map__51774 = p__51773;
var map__51774__$1 = ((((!((map__51774 == null)))?((((map__51774.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51774.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51774):map__51774);
var data = cljs.core.get.call(null,map__51774__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var map__51776 = this;
var map__51776__$1 = ((((!((map__51776 == null)))?((((map__51776.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51776.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51776):map__51776);
var data__$1 = cljs.core.get.call(null,map__51776__$1,new cljs.core.Keyword(null,"data","data",-232669377));
return cljs.core.update_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706),new cljs.core.Keyword("ote.db.transport-service","rental","ote.db.transport-service/rental",77770772)], null),cljs.core.merge,data__$1);
});

ote.app.controller.rental.SaveRentalToDb.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.rental.SaveRentalToDb.prototype.tuck$core$Event$process_event$arity$2 = (function (_,p__51778){
var map__51779 = p__51778;
var map__51779__$1 = ((((!((map__51779 == null)))?((((map__51779.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51779.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51779):map__51779);
var app = map__51779__$1;
var service = cljs.core.get.call(null,map__51779__$1,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706));
var ___$1 = this;
var service_data = cljs.core.update_in.call(null,cljs.core.update.call(null,cljs.core.assoc.call(null,service,new cljs.core.Keyword("ote.db.transport-service","type","ote.db.transport-service/type",-1555621317),new cljs.core.Keyword(null,"rental","rental",-1572209245),new cljs.core.Keyword("ote.db.transport-service","transport-operator-id","ote.db.transport-service/transport-operator-id",1381166108),cljs.core.get_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982),new cljs.core.Keyword("ote.db.transport-operator","id","ote.db.transport-operator/id",343347306)], null))),new cljs.core.Keyword("ote.db.transport-service","rental","ote.db.transport-service/rental",77770772),ote.ui.form.without_form_metadata),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("ote.db.transport-service","rental","ote.db.transport-service/rental",77770772),new cljs.core.Keyword("ote.db.transport-service","operation-area","ote.db.transport-service/operation-area",1530382917)], null),ote.app.controller.place_search.place_references);
ote.communication.post_BANG_.call(null,"rental",service_data,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-success","on-success",1786904109),tuck.core.send_async_BANG_.call(null,ote.app.controller.rental.__GT_HandleRentalResponse)], null));

return app;
});

ote.app.controller.rental.HandleRentalResponse.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.rental.HandleRentalResponse.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51781,app){
var map__51782 = p__51781;
var map__51782__$1 = ((((!((map__51782 == null)))?((((map__51782.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51782.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51782):map__51782);
var service = cljs.core.get.call(null,map__51782__$1,new cljs.core.Keyword(null,"service","service",-1963054559));
var map__51784 = this;
var map__51784__$1 = ((((!((map__51784 == null)))?((((map__51784.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51784.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51784):map__51784);
var service__$1 = cljs.core.get.call(null,map__51784__$1,new cljs.core.Keyword(null,"service","service",-1963054559));
return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706),service__$1,new cljs.core.Keyword(null,"page","page",849072397),new cljs.core.Keyword(null,"own-services","own-services",-1593467283));
});

//# sourceMappingURL=rental.js.map?rel=1510137293105
