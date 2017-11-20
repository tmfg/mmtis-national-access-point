// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.app.controller.passenger_transportation');
goog.require('cljs.core');
goog.require('tuck.core');
goog.require('ote.communication');
goog.require('ote.ui.form');
goog.require('ote.db.transport_operator');
goog.require('ote.db.transport_service');
goog.require('ote.app.controller.place_search');
goog.require('ote.app.controller.transport_service');
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
ote.app.controller.passenger_transportation.EditPassengerTransportationState = (function (data,__meta,__extmap,__hash){
this.data = data;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52030,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52034 = k52030;
var G__52034__$1 = (((G__52034 instanceof cljs.core.Keyword))?G__52034.fqn:null);
switch (G__52034__$1) {
case "data":
return self__.data;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k52030,else__30866__auto__);

}
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.passenger-transportation.EditPassengerTransportationState{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"data","data",-232669377),self__.data],null))], null),self__.__extmap));
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52029){
var self__ = this;
var G__52029__$1 = this;
return (new cljs.core.RecordIter((0),G__52029__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"data","data",-232669377)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.passenger_transportation.EditPassengerTransportationState(self__.data,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-456208925 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52031,other52032){
var self__ = this;
var this52031__$1 = this;
return (!((other52032 == null))) && ((this52031__$1.constructor === other52032.constructor)) && (cljs.core._EQ_.call(null,this52031__$1.data,other52032.data)) && (cljs.core._EQ_.call(null,this52031__$1.__extmap,other52032.__extmap));
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"data","data",-232669377),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.passenger_transportation.EditPassengerTransportationState(self__.data,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52029){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52035 = cljs.core.keyword_identical_QMARK_;
var expr__52036 = k__30871__auto__;
if(cljs.core.truth_(pred__52035.call(null,new cljs.core.Keyword(null,"data","data",-232669377),expr__52036))){
return (new ote.app.controller.passenger_transportation.EditPassengerTransportationState(G__52029,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.passenger_transportation.EditPassengerTransportationState(self__.data,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52029),null));
}
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"data","data",-232669377),self__.data],null))], null),self__.__extmap));
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52029){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.passenger_transportation.EditPassengerTransportationState(self__.data,G__52029,self__.__extmap,self__.__hash));
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"data","data",1407862150,null)], null);
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.cljs$lang$type = true;

ote.app.controller.passenger_transportation.EditPassengerTransportationState.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.passenger-transportation/EditPassengerTransportationState");
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.passenger-transportation/EditPassengerTransportationState");
});

ote.app.controller.passenger_transportation.__GT_EditPassengerTransportationState = (function ote$app$controller$passenger_transportation$__GT_EditPassengerTransportationState(data){
return (new ote.app.controller.passenger_transportation.EditPassengerTransportationState(data,null,null,null));
});

ote.app.controller.passenger_transportation.map__GT_EditPassengerTransportationState = (function ote$app$controller$passenger_transportation$map__GT_EditPassengerTransportationState(G__52033){
return (new ote.app.controller.passenger_transportation.EditPassengerTransportationState(new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(G__52033),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52033,new cljs.core.Keyword(null,"data","data",-232669377))),null));
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
ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse = (function (service,__meta,__extmap,__hash){
this.service = service;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52040,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52044 = k52040;
var G__52044__$1 = (((G__52044 instanceof cljs.core.Keyword))?G__52044.fqn:null);
switch (G__52044__$1) {
case "service":
return self__.service;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k52040,else__30866__auto__);

}
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.passenger-transportation.HandlePassengerTransportationResponse{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"service","service",-1963054559),self__.service],null))], null),self__.__extmap));
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52039){
var self__ = this;
var G__52039__$1 = this;
return (new cljs.core.RecordIter((0),G__52039__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"service","service",-1963054559)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse(self__.service,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-1252964005 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52041,other52042){
var self__ = this;
var this52041__$1 = this;
return (!((other52042 == null))) && ((this52041__$1.constructor === other52042.constructor)) && (cljs.core._EQ_.call(null,this52041__$1.service,other52042.service)) && (cljs.core._EQ_.call(null,this52041__$1.__extmap,other52042.__extmap));
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"service","service",-1963054559),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse(self__.service,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52039){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52045 = cljs.core.keyword_identical_QMARK_;
var expr__52046 = k__30871__auto__;
if(cljs.core.truth_(pred__52045.call(null,new cljs.core.Keyword(null,"service","service",-1963054559),expr__52046))){
return (new ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse(G__52039,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse(self__.service,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52039),null));
}
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"service","service",-1963054559),self__.service],null))], null),self__.__extmap));
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52039){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse(self__.service,G__52039,self__.__extmap,self__.__hash));
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"service","service",-322523032,null)], null);
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.cljs$lang$type = true;

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.passenger-transportation/HandlePassengerTransportationResponse");
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.passenger-transportation/HandlePassengerTransportationResponse");
});

ote.app.controller.passenger_transportation.__GT_HandlePassengerTransportationResponse = (function ote$app$controller$passenger_transportation$__GT_HandlePassengerTransportationResponse(service){
return (new ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse(service,null,null,null));
});

ote.app.controller.passenger_transportation.map__GT_HandlePassengerTransportationResponse = (function ote$app$controller$passenger_transportation$map__GT_HandlePassengerTransportationResponse(G__52043){
return (new ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse(new cljs.core.Keyword(null,"service","service",-1963054559).cljs$core$IFn$_invoke$arity$1(G__52043),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52043,new cljs.core.Keyword(null,"service","service",-1963054559))),null));
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
ote.app.controller.passenger_transportation.CancelPassengerTransportationForm = (function (__meta,__extmap,__hash){
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52050,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52054 = k52050;
switch (G__52054) {
default:
return cljs.core.get.call(null,self__.__extmap,k52050,else__30866__auto__);

}
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.passenger-transportation.CancelPassengerTransportationForm{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52049){
var self__ = this;
var G__52049__$1 = this;
return (new cljs.core.RecordIter((0),G__52049__$1,0,cljs.core.PersistentVector.EMPTY,(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.passenger_transportation.CancelPassengerTransportationForm(self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (0 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1634181792 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52051,other52052){
var self__ = this;
var this52051__$1 = this;
return (!((other52052 == null))) && ((this52051__$1.constructor === other52052.constructor)) && (cljs.core._EQ_.call(null,this52051__$1.__extmap,other52052.__extmap));
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.EMPTY,k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.passenger_transportation.CancelPassengerTransportationForm(self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52049){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52055 = cljs.core.keyword_identical_QMARK_;
var expr__52056 = k__30871__auto__;
return (new ote.app.controller.passenger_transportation.CancelPassengerTransportationForm(self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52049),null));
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52049){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.passenger_transportation.CancelPassengerTransportationForm(G__52049,self__.__extmap,self__.__hash));
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.cljs$lang$type = true;

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.passenger-transportation/CancelPassengerTransportationForm");
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.passenger-transportation/CancelPassengerTransportationForm");
});

ote.app.controller.passenger_transportation.__GT_CancelPassengerTransportationForm = (function ote$app$controller$passenger_transportation$__GT_CancelPassengerTransportationForm(){
return (new ote.app.controller.passenger_transportation.CancelPassengerTransportationForm(null,null,null));
});

ote.app.controller.passenger_transportation.map__GT_CancelPassengerTransportationForm = (function ote$app$controller$passenger_transportation$map__GT_CancelPassengerTransportationForm(G__52053){
return (new ote.app.controller.passenger_transportation.CancelPassengerTransportationForm(null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52053)),null));
});

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.passenger_transportation.EditPassengerTransportationState.prototype.tuck$core$Event$process_event$arity$2 = (function (p__52059,app){
var map__52060 = p__52059;
var map__52060__$1 = ((((!((map__52060 == null)))?((((map__52060.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52060.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52060):map__52060);
var data = cljs.core.get.call(null,map__52060__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var map__52062 = this;
var map__52062__$1 = ((((!((map__52062 == null)))?((((map__52062.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52062.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52062):map__52062);
var data__$1 = cljs.core.get.call(null,map__52062__$1,new cljs.core.Keyword(null,"data","data",-232669377));
return cljs.core.update_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706),new cljs.core.Keyword("ote.db.transport-service","passenger-transportation","ote.db.transport-service/passenger-transportation",-2018752833)], null),cljs.core.merge,data__$1);
});

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.passenger_transportation.HandlePassengerTransportationResponse.prototype.tuck$core$Event$process_event$arity$2 = (function (p__52064,app){
var map__52065 = p__52064;
var map__52065__$1 = ((((!((map__52065 == null)))?((((map__52065.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52065.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52065):map__52065);
var service = cljs.core.get.call(null,map__52065__$1,new cljs.core.Keyword(null,"service","service",-1963054559));
var map__52067 = this;
var map__52067__$1 = ((((!((map__52067 == null)))?((((map__52067.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52067.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52067):map__52067);
var service__$1 = cljs.core.get.call(null,map__52067__$1,new cljs.core.Keyword(null,"service","service",-1963054559));
ote.app.routes.navigate_BANG_.call(null,new cljs.core.Keyword(null,"own-services","own-services",-1593467283));

return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706),service__$1,new cljs.core.Keyword(null,"page","page",849072397),new cljs.core.Keyword(null,"own-services","own-services",-1593467283));
});

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.passenger_transportation.CancelPassengerTransportationForm.prototype.tuck$core$Event$process_event$arity$2 = (function (_,app){
var ___$1 = this;
ote.app.routes.navigate_BANG_.call(null,new cljs.core.Keyword(null,"own-services","own-services",-1593467283));

return app;
});

//# sourceMappingURL=passenger_transportation.js.map?rel=1510137293999
