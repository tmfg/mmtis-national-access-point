// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.app.controller.transport_service');
goog.require('cljs.core');
goog.require('tuck.core');
goog.require('ote.communication');
goog.require('ote.db.transport_service');
goog.require('ote.ui.form');
goog.require('ote.app.routes');
goog.require('ote.time');
goog.require('taoensso.timbre');
goog.require('ote.db.transport_operator');
goog.require('ote.localization');
goog.require('ote.app.controller.place_search');
ote.app.controller.transport_service.service_level_keys = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword("ote.db.transport-service","contact-email","ote.db.transport-service/contact-email",-447985279),null,new cljs.core.Keyword("ote.db.transport-service","sub-type","ote.db.transport-service/sub-type",1455227203),null,new cljs.core.Keyword("ote.db.transport-service","operation-area","ote.db.transport-service/operation-area",1530382917),null,new cljs.core.Keyword("ote.db.transport-service","external-interfaces","ote.db.transport-service/external-interfaces",-953510767),null,new cljs.core.Keyword("ote.db.transport-service","contact-address","ote.db.transport-service/contact-address",-188308046),null,new cljs.core.Keyword("ote.db.transport-service","homepage","ote.db.transport-service/homepage",-91375112),null,new cljs.core.Keyword("ote.db.transport-service","name","ote.db.transport-service/name",1219699930),null,new cljs.core.Keyword("ote.db.transport-service","contact-phone","ote.db.transport-service/contact-phone",-1602195908),null], null), null);
/**
 * Returns service type keyword from combined type-subtype key.
 */
ote.app.controller.transport_service.service_type_from_combined_service_type = (function ote$app$controller$transport_service$service_type_from_combined_service_type(type){
var G__51806 = type;
var G__51806__$1 = (((G__51806 instanceof cljs.core.Keyword))?G__51806.fqn:null);
switch (G__51806__$1) {
case "passenger-transportation-taxi":
return new cljs.core.Keyword(null,"passenger-transportation","passenger-transportation",-368634870);

break;
case "passenger-transportation-request":
return new cljs.core.Keyword(null,"passenger-transportation","passenger-transportation",-368634870);

break;
case "passenger-transportation-schedule":
return new cljs.core.Keyword(null,"passenger-transportation","passenger-transportation",-368634870);

break;
case "terminal":
return new cljs.core.Keyword(null,"terminal","terminal",-927870592);

break;
case "rentals":
return new cljs.core.Keyword(null,"rentals","rentals",37930980);

break;
case "parking":
return new cljs.core.Keyword(null,"parking","parking",-952236974);

break;
case "brokerage":
return new cljs.core.Keyword(null,"brokerage","brokerage",1771448945);

break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__51806__$1)].join('')));

}
});
/**
 * Returns service subtype keyword from combined type-subtype key.
 */
ote.app.controller.transport_service.subtype_from_combined_service_type = (function ote$app$controller$transport_service$subtype_from_combined_service_type(type){
var G__51808 = type;
var G__51808__$1 = (((G__51808 instanceof cljs.core.Keyword))?G__51808.fqn:null);
switch (G__51808__$1) {
case "passenger-transportation-taxi":
return new cljs.core.Keyword(null,"taxi","taxi",1533748116);

break;
case "passenger-transportation-request":
return new cljs.core.Keyword(null,"request","request",1772954723);

break;
case "passenger-transportation-schedule":
return new cljs.core.Keyword(null,"schedule","schedule",349275266);

break;
case "terminal":
return new cljs.core.Keyword(null,"terminal","terminal",-927870592);

break;
case "rentals":
return new cljs.core.Keyword(null,"rentals","rentals",37930980);

break;
case "parking":
return new cljs.core.Keyword(null,"parking","parking",-952236974);

break;
case "brokerage":
return new cljs.core.Keyword(null,"brokerage","brokerage",1771448945);

break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__51808__$1)].join('')));

}
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
ote.app.controller.transport_service.AddPriceClassRow = (function (__meta,__extmap,__hash){
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_service.AddPriceClassRow.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_service.AddPriceClassRow.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51811,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51815 = k51811;
switch (G__51815) {
default:
return cljs.core.get.call(null,self__.__extmap,k51811,else__30866__auto__);

}
});

ote.app.controller.transport_service.AddPriceClassRow.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-service.AddPriceClassRow{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.transport_service.AddPriceClassRow.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51810){
var self__ = this;
var G__51810__$1 = this;
return (new cljs.core.RecordIter((0),G__51810__$1,0,cljs.core.PersistentVector.EMPTY,(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_service.AddPriceClassRow.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_service.AddPriceClassRow.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_service.AddPriceClassRow(self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.AddPriceClassRow.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (0 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_service.AddPriceClassRow.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (517660586 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_service.AddPriceClassRow.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51812,other51813){
var self__ = this;
var this51812__$1 = this;
return (!((other51813 == null))) && ((this51812__$1.constructor === other51813.constructor)) && (cljs.core._EQ_.call(null,this51812__$1.__extmap,other51813.__extmap));
});

ote.app.controller.transport_service.AddPriceClassRow.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.EMPTY,k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_service.AddPriceClassRow(self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_service.AddPriceClassRow.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51810){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51816 = cljs.core.keyword_identical_QMARK_;
var expr__51817 = k__30871__auto__;
return (new ote.app.controller.transport_service.AddPriceClassRow(self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51810),null));
});

ote.app.controller.transport_service.AddPriceClassRow.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.transport_service.AddPriceClassRow.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51810){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_service.AddPriceClassRow(G__51810,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.AddPriceClassRow.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_service.AddPriceClassRow.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

ote.app.controller.transport_service.AddPriceClassRow.cljs$lang$type = true;

ote.app.controller.transport_service.AddPriceClassRow.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-service/AddPriceClassRow");
});

ote.app.controller.transport_service.AddPriceClassRow.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-service/AddPriceClassRow");
});

ote.app.controller.transport_service.__GT_AddPriceClassRow = (function ote$app$controller$transport_service$__GT_AddPriceClassRow(){
return (new ote.app.controller.transport_service.AddPriceClassRow(null,null,null));
});

ote.app.controller.transport_service.map__GT_AddPriceClassRow = (function ote$app$controller$transport_service$map__GT_AddPriceClassRow(G__51814){
return (new ote.app.controller.transport_service.AddPriceClassRow(null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51814)),null));
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
ote.app.controller.transport_service.AddServiceHourRow = (function (__meta,__extmap,__hash){
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_service.AddServiceHourRow.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_service.AddServiceHourRow.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51821,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51825 = k51821;
switch (G__51825) {
default:
return cljs.core.get.call(null,self__.__extmap,k51821,else__30866__auto__);

}
});

ote.app.controller.transport_service.AddServiceHourRow.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-service.AddServiceHourRow{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.transport_service.AddServiceHourRow.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51820){
var self__ = this;
var G__51820__$1 = this;
return (new cljs.core.RecordIter((0),G__51820__$1,0,cljs.core.PersistentVector.EMPTY,(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_service.AddServiceHourRow.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_service.AddServiceHourRow.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_service.AddServiceHourRow(self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.AddServiceHourRow.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (0 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_service.AddServiceHourRow.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-955727428 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_service.AddServiceHourRow.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51822,other51823){
var self__ = this;
var this51822__$1 = this;
return (!((other51823 == null))) && ((this51822__$1.constructor === other51823.constructor)) && (cljs.core._EQ_.call(null,this51822__$1.__extmap,other51823.__extmap));
});

ote.app.controller.transport_service.AddServiceHourRow.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.EMPTY,k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_service.AddServiceHourRow(self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_service.AddServiceHourRow.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51820){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51826 = cljs.core.keyword_identical_QMARK_;
var expr__51827 = k__30871__auto__;
return (new ote.app.controller.transport_service.AddServiceHourRow(self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51820),null));
});

ote.app.controller.transport_service.AddServiceHourRow.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.transport_service.AddServiceHourRow.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51820){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_service.AddServiceHourRow(G__51820,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.AddServiceHourRow.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_service.AddServiceHourRow.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

ote.app.controller.transport_service.AddServiceHourRow.cljs$lang$type = true;

ote.app.controller.transport_service.AddServiceHourRow.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-service/AddServiceHourRow");
});

ote.app.controller.transport_service.AddServiceHourRow.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-service/AddServiceHourRow");
});

ote.app.controller.transport_service.__GT_AddServiceHourRow = (function ote$app$controller$transport_service$__GT_AddServiceHourRow(){
return (new ote.app.controller.transport_service.AddServiceHourRow(null,null,null));
});

ote.app.controller.transport_service.map__GT_AddServiceHourRow = (function ote$app$controller$transport_service$map__GT_AddServiceHourRow(G__51824){
return (new ote.app.controller.transport_service.AddServiceHourRow(null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51824)),null));
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
ote.app.controller.transport_service.RemovePriceClassRow = (function (__meta,__extmap,__hash){
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_service.RemovePriceClassRow.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_service.RemovePriceClassRow.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51831,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51835 = k51831;
switch (G__51835) {
default:
return cljs.core.get.call(null,self__.__extmap,k51831,else__30866__auto__);

}
});

ote.app.controller.transport_service.RemovePriceClassRow.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-service.RemovePriceClassRow{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.transport_service.RemovePriceClassRow.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51830){
var self__ = this;
var G__51830__$1 = this;
return (new cljs.core.RecordIter((0),G__51830__$1,0,cljs.core.PersistentVector.EMPTY,(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_service.RemovePriceClassRow.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_service.RemovePriceClassRow.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_service.RemovePriceClassRow(self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.RemovePriceClassRow.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (0 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_service.RemovePriceClassRow.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-1877305282 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_service.RemovePriceClassRow.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51832,other51833){
var self__ = this;
var this51832__$1 = this;
return (!((other51833 == null))) && ((this51832__$1.constructor === other51833.constructor)) && (cljs.core._EQ_.call(null,this51832__$1.__extmap,other51833.__extmap));
});

ote.app.controller.transport_service.RemovePriceClassRow.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.EMPTY,k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_service.RemovePriceClassRow(self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_service.RemovePriceClassRow.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51830){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51836 = cljs.core.keyword_identical_QMARK_;
var expr__51837 = k__30871__auto__;
return (new ote.app.controller.transport_service.RemovePriceClassRow(self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51830),null));
});

ote.app.controller.transport_service.RemovePriceClassRow.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.transport_service.RemovePriceClassRow.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51830){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_service.RemovePriceClassRow(G__51830,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.RemovePriceClassRow.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_service.RemovePriceClassRow.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

ote.app.controller.transport_service.RemovePriceClassRow.cljs$lang$type = true;

ote.app.controller.transport_service.RemovePriceClassRow.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-service/RemovePriceClassRow");
});

ote.app.controller.transport_service.RemovePriceClassRow.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-service/RemovePriceClassRow");
});

ote.app.controller.transport_service.__GT_RemovePriceClassRow = (function ote$app$controller$transport_service$__GT_RemovePriceClassRow(){
return (new ote.app.controller.transport_service.RemovePriceClassRow(null,null,null));
});

ote.app.controller.transport_service.map__GT_RemovePriceClassRow = (function ote$app$controller$transport_service$map__GT_RemovePriceClassRow(G__51834){
return (new ote.app.controller.transport_service.RemovePriceClassRow(null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51834)),null));
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
ote.app.controller.transport_service.SelectTransportServiceType = (function (data,__meta,__extmap,__hash){
this.data = data;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_service.SelectTransportServiceType.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_service.SelectTransportServiceType.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51841,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51845 = k51841;
var G__51845__$1 = (((G__51845 instanceof cljs.core.Keyword))?G__51845.fqn:null);
switch (G__51845__$1) {
case "data":
return self__.data;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51841,else__30866__auto__);

}
});

ote.app.controller.transport_service.SelectTransportServiceType.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-service.SelectTransportServiceType{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"data","data",-232669377),self__.data],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.SelectTransportServiceType.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51840){
var self__ = this;
var G__51840__$1 = this;
return (new cljs.core.RecordIter((0),G__51840__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"data","data",-232669377)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_service.SelectTransportServiceType.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_service.SelectTransportServiceType.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_service.SelectTransportServiceType(self__.data,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.SelectTransportServiceType.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_service.SelectTransportServiceType.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-1740610493 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_service.SelectTransportServiceType.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51842,other51843){
var self__ = this;
var this51842__$1 = this;
return (!((other51843 == null))) && ((this51842__$1.constructor === other51843.constructor)) && (cljs.core._EQ_.call(null,this51842__$1.data,other51843.data)) && (cljs.core._EQ_.call(null,this51842__$1.__extmap,other51843.__extmap));
});

ote.app.controller.transport_service.SelectTransportServiceType.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"data","data",-232669377),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_service.SelectTransportServiceType(self__.data,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_service.SelectTransportServiceType.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51840){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51846 = cljs.core.keyword_identical_QMARK_;
var expr__51847 = k__30871__auto__;
if(cljs.core.truth_(pred__51846.call(null,new cljs.core.Keyword(null,"data","data",-232669377),expr__51847))){
return (new ote.app.controller.transport_service.SelectTransportServiceType(G__51840,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.transport_service.SelectTransportServiceType(self__.data,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51840),null));
}
});

ote.app.controller.transport_service.SelectTransportServiceType.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"data","data",-232669377),self__.data],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.SelectTransportServiceType.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51840){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_service.SelectTransportServiceType(self__.data,G__51840,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.SelectTransportServiceType.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_service.SelectTransportServiceType.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"data","data",1407862150,null)], null);
});

ote.app.controller.transport_service.SelectTransportServiceType.cljs$lang$type = true;

ote.app.controller.transport_service.SelectTransportServiceType.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-service/SelectTransportServiceType");
});

ote.app.controller.transport_service.SelectTransportServiceType.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-service/SelectTransportServiceType");
});

ote.app.controller.transport_service.__GT_SelectTransportServiceType = (function ote$app$controller$transport_service$__GT_SelectTransportServiceType(data){
return (new ote.app.controller.transport_service.SelectTransportServiceType(data,null,null,null));
});

ote.app.controller.transport_service.map__GT_SelectTransportServiceType = (function ote$app$controller$transport_service$map__GT_SelectTransportServiceType(G__51844){
return (new ote.app.controller.transport_service.SelectTransportServiceType(new cljs.core.Keyword(null,"data","data",-232669377).cljs$core$IFn$_invoke$arity$1(G__51844),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51844,new cljs.core.Keyword(null,"data","data",-232669377))),null));
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
ote.app.controller.transport_service.ModifyTransportService = (function (id,__meta,__extmap,__hash){
this.id = id;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_service.ModifyTransportService.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_service.ModifyTransportService.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51851,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51855 = k51851;
var G__51855__$1 = (((G__51855 instanceof cljs.core.Keyword))?G__51855.fqn:null);
switch (G__51855__$1) {
case "id":
return self__.id;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51851,else__30866__auto__);

}
});

ote.app.controller.transport_service.ModifyTransportService.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-service.ModifyTransportService{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"id","id",-1388402092),self__.id],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.ModifyTransportService.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51850){
var self__ = this;
var G__51850__$1 = this;
return (new cljs.core.RecordIter((0),G__51850__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"id","id",-1388402092)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_service.ModifyTransportService.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_service.ModifyTransportService.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_service.ModifyTransportService(self__.id,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.ModifyTransportService.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_service.ModifyTransportService.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1637107696 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_service.ModifyTransportService.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51852,other51853){
var self__ = this;
var this51852__$1 = this;
return (!((other51853 == null))) && ((this51852__$1.constructor === other51853.constructor)) && (cljs.core._EQ_.call(null,this51852__$1.id,other51853.id)) && (cljs.core._EQ_.call(null,this51852__$1.__extmap,other51853.__extmap));
});

ote.app.controller.transport_service.ModifyTransportService.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"id","id",-1388402092),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_service.ModifyTransportService(self__.id,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_service.ModifyTransportService.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51850){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51856 = cljs.core.keyword_identical_QMARK_;
var expr__51857 = k__30871__auto__;
if(cljs.core.truth_(pred__51856.call(null,new cljs.core.Keyword(null,"id","id",-1388402092),expr__51857))){
return (new ote.app.controller.transport_service.ModifyTransportService(G__51850,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.transport_service.ModifyTransportService(self__.id,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51850),null));
}
});

ote.app.controller.transport_service.ModifyTransportService.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"id","id",-1388402092),self__.id],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.ModifyTransportService.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51850){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_service.ModifyTransportService(self__.id,G__51850,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.ModifyTransportService.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_service.ModifyTransportService.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"id","id",252129435,null)], null);
});

ote.app.controller.transport_service.ModifyTransportService.cljs$lang$type = true;

ote.app.controller.transport_service.ModifyTransportService.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-service/ModifyTransportService");
});

ote.app.controller.transport_service.ModifyTransportService.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-service/ModifyTransportService");
});

ote.app.controller.transport_service.__GT_ModifyTransportService = (function ote$app$controller$transport_service$__GT_ModifyTransportService(id){
return (new ote.app.controller.transport_service.ModifyTransportService(id,null,null,null));
});

ote.app.controller.transport_service.map__GT_ModifyTransportService = (function ote$app$controller$transport_service$map__GT_ModifyTransportService(G__51854){
return (new ote.app.controller.transport_service.ModifyTransportService(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(G__51854),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51854,new cljs.core.Keyword(null,"id","id",-1388402092))),null));
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
ote.app.controller.transport_service.ModifyTransportServiceResponse = (function (response,__meta,__extmap,__hash){
this.response = response;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51861,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51865 = k51861;
var G__51865__$1 = (((G__51865 instanceof cljs.core.Keyword))?G__51865.fqn:null);
switch (G__51865__$1) {
case "response":
return self__.response;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51861,else__30866__auto__);

}
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-service.ModifyTransportServiceResponse{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51860){
var self__ = this;
var G__51860__$1 = this;
return (new cljs.core.RecordIter((0),G__51860__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"response","response",-1068424192)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_service.ModifyTransportServiceResponse(self__.response,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-133045093 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51862,other51863){
var self__ = this;
var this51862__$1 = this;
return (!((other51863 == null))) && ((this51862__$1.constructor === other51863.constructor)) && (cljs.core._EQ_.call(null,this51862__$1.response,other51863.response)) && (cljs.core._EQ_.call(null,this51862__$1.__extmap,other51863.__extmap));
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"response","response",-1068424192),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_service.ModifyTransportServiceResponse(self__.response,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51860){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51866 = cljs.core.keyword_identical_QMARK_;
var expr__51867 = k__30871__auto__;
if(cljs.core.truth_(pred__51866.call(null,new cljs.core.Keyword(null,"response","response",-1068424192),expr__51867))){
return (new ote.app.controller.transport_service.ModifyTransportServiceResponse(G__51860,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.transport_service.ModifyTransportServiceResponse(self__.response,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51860),null));
}
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51860){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_service.ModifyTransportServiceResponse(self__.response,G__51860,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"response","response",572107335,null)], null);
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.cljs$lang$type = true;

ote.app.controller.transport_service.ModifyTransportServiceResponse.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-service/ModifyTransportServiceResponse");
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-service/ModifyTransportServiceResponse");
});

ote.app.controller.transport_service.__GT_ModifyTransportServiceResponse = (function ote$app$controller$transport_service$__GT_ModifyTransportServiceResponse(response){
return (new ote.app.controller.transport_service.ModifyTransportServiceResponse(response,null,null,null));
});

ote.app.controller.transport_service.map__GT_ModifyTransportServiceResponse = (function ote$app$controller$transport_service$map__GT_ModifyTransportServiceResponse(G__51864){
return (new ote.app.controller.transport_service.ModifyTransportServiceResponse(new cljs.core.Keyword(null,"response","response",-1068424192).cljs$core$IFn$_invoke$arity$1(G__51864),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51864,new cljs.core.Keyword(null,"response","response",-1068424192))),null));
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
ote.app.controller.transport_service.OpenTransportServicePage = (function (id,__meta,__extmap,__hash){
this.id = id;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_service.OpenTransportServicePage.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_service.OpenTransportServicePage.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51871,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51875 = k51871;
var G__51875__$1 = (((G__51875 instanceof cljs.core.Keyword))?G__51875.fqn:null);
switch (G__51875__$1) {
case "id":
return self__.id;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51871,else__30866__auto__);

}
});

ote.app.controller.transport_service.OpenTransportServicePage.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-service.OpenTransportServicePage{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"id","id",-1388402092),self__.id],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.OpenTransportServicePage.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51870){
var self__ = this;
var G__51870__$1 = this;
return (new cljs.core.RecordIter((0),G__51870__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"id","id",-1388402092)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_service.OpenTransportServicePage.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_service.OpenTransportServicePage.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_service.OpenTransportServicePage(self__.id,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.OpenTransportServicePage.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_service.OpenTransportServicePage.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-1211022699 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_service.OpenTransportServicePage.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51872,other51873){
var self__ = this;
var this51872__$1 = this;
return (!((other51873 == null))) && ((this51872__$1.constructor === other51873.constructor)) && (cljs.core._EQ_.call(null,this51872__$1.id,other51873.id)) && (cljs.core._EQ_.call(null,this51872__$1.__extmap,other51873.__extmap));
});

ote.app.controller.transport_service.OpenTransportServicePage.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"id","id",-1388402092),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_service.OpenTransportServicePage(self__.id,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_service.OpenTransportServicePage.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51870){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51876 = cljs.core.keyword_identical_QMARK_;
var expr__51877 = k__30871__auto__;
if(cljs.core.truth_(pred__51876.call(null,new cljs.core.Keyword(null,"id","id",-1388402092),expr__51877))){
return (new ote.app.controller.transport_service.OpenTransportServicePage(G__51870,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.transport_service.OpenTransportServicePage(self__.id,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51870),null));
}
});

ote.app.controller.transport_service.OpenTransportServicePage.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"id","id",-1388402092),self__.id],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.OpenTransportServicePage.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51870){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_service.OpenTransportServicePage(self__.id,G__51870,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.OpenTransportServicePage.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_service.OpenTransportServicePage.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"id","id",252129435,null)], null);
});

ote.app.controller.transport_service.OpenTransportServicePage.cljs$lang$type = true;

ote.app.controller.transport_service.OpenTransportServicePage.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-service/OpenTransportServicePage");
});

ote.app.controller.transport_service.OpenTransportServicePage.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-service/OpenTransportServicePage");
});

ote.app.controller.transport_service.__GT_OpenTransportServicePage = (function ote$app$controller$transport_service$__GT_OpenTransportServicePage(id){
return (new ote.app.controller.transport_service.OpenTransportServicePage(id,null,null,null));
});

ote.app.controller.transport_service.map__GT_OpenTransportServicePage = (function ote$app$controller$transport_service$map__GT_OpenTransportServicePage(G__51874){
return (new ote.app.controller.transport_service.OpenTransportServicePage(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(G__51874),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51874,new cljs.core.Keyword(null,"id","id",-1388402092))),null));
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
ote.app.controller.transport_service.DeleteTransportService = (function (id,__meta,__extmap,__hash){
this.id = id;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_service.DeleteTransportService.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_service.DeleteTransportService.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51881,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51885 = k51881;
var G__51885__$1 = (((G__51885 instanceof cljs.core.Keyword))?G__51885.fqn:null);
switch (G__51885__$1) {
case "id":
return self__.id;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51881,else__30866__auto__);

}
});

ote.app.controller.transport_service.DeleteTransportService.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-service.DeleteTransportService{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"id","id",-1388402092),self__.id],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.DeleteTransportService.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51880){
var self__ = this;
var G__51880__$1 = this;
return (new cljs.core.RecordIter((0),G__51880__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"id","id",-1388402092)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_service.DeleteTransportService.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_service.DeleteTransportService.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_service.DeleteTransportService(self__.id,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.DeleteTransportService.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_service.DeleteTransportService.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-1556124456 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_service.DeleteTransportService.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51882,other51883){
var self__ = this;
var this51882__$1 = this;
return (!((other51883 == null))) && ((this51882__$1.constructor === other51883.constructor)) && (cljs.core._EQ_.call(null,this51882__$1.id,other51883.id)) && (cljs.core._EQ_.call(null,this51882__$1.__extmap,other51883.__extmap));
});

ote.app.controller.transport_service.DeleteTransportService.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"id","id",-1388402092),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_service.DeleteTransportService(self__.id,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_service.DeleteTransportService.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51880){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51886 = cljs.core.keyword_identical_QMARK_;
var expr__51887 = k__30871__auto__;
if(cljs.core.truth_(pred__51886.call(null,new cljs.core.Keyword(null,"id","id",-1388402092),expr__51887))){
return (new ote.app.controller.transport_service.DeleteTransportService(G__51880,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.transport_service.DeleteTransportService(self__.id,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51880),null));
}
});

ote.app.controller.transport_service.DeleteTransportService.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"id","id",-1388402092),self__.id],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.DeleteTransportService.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51880){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_service.DeleteTransportService(self__.id,G__51880,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.DeleteTransportService.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_service.DeleteTransportService.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"id","id",252129435,null)], null);
});

ote.app.controller.transport_service.DeleteTransportService.cljs$lang$type = true;

ote.app.controller.transport_service.DeleteTransportService.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-service/DeleteTransportService");
});

ote.app.controller.transport_service.DeleteTransportService.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-service/DeleteTransportService");
});

ote.app.controller.transport_service.__GT_DeleteTransportService = (function ote$app$controller$transport_service$__GT_DeleteTransportService(id){
return (new ote.app.controller.transport_service.DeleteTransportService(id,null,null,null));
});

ote.app.controller.transport_service.map__GT_DeleteTransportService = (function ote$app$controller$transport_service$map__GT_DeleteTransportService(G__51884){
return (new ote.app.controller.transport_service.DeleteTransportService(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(G__51884),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51884,new cljs.core.Keyword(null,"id","id",-1388402092))),null));
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
ote.app.controller.transport_service.DeleteTransportServiceResponse = (function (response,__meta,__extmap,__hash){
this.response = response;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51891,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51895 = k51891;
var G__51895__$1 = (((G__51895 instanceof cljs.core.Keyword))?G__51895.fqn:null);
switch (G__51895__$1) {
case "response":
return self__.response;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51891,else__30866__auto__);

}
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-service.DeleteTransportServiceResponse{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51890){
var self__ = this;
var G__51890__$1 = this;
return (new cljs.core.RecordIter((0),G__51890__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"response","response",-1068424192)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_service.DeleteTransportServiceResponse(self__.response,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1230793007 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51892,other51893){
var self__ = this;
var this51892__$1 = this;
return (!((other51893 == null))) && ((this51892__$1.constructor === other51893.constructor)) && (cljs.core._EQ_.call(null,this51892__$1.response,other51893.response)) && (cljs.core._EQ_.call(null,this51892__$1.__extmap,other51893.__extmap));
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"response","response",-1068424192),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_service.DeleteTransportServiceResponse(self__.response,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51890){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51896 = cljs.core.keyword_identical_QMARK_;
var expr__51897 = k__30871__auto__;
if(cljs.core.truth_(pred__51896.call(null,new cljs.core.Keyword(null,"response","response",-1068424192),expr__51897))){
return (new ote.app.controller.transport_service.DeleteTransportServiceResponse(G__51890,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.transport_service.DeleteTransportServiceResponse(self__.response,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51890),null));
}
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51890){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_service.DeleteTransportServiceResponse(self__.response,G__51890,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"response","response",572107335,null)], null);
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.cljs$lang$type = true;

ote.app.controller.transport_service.DeleteTransportServiceResponse.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-service/DeleteTransportServiceResponse");
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-service/DeleteTransportServiceResponse");
});

ote.app.controller.transport_service.__GT_DeleteTransportServiceResponse = (function ote$app$controller$transport_service$__GT_DeleteTransportServiceResponse(response){
return (new ote.app.controller.transport_service.DeleteTransportServiceResponse(response,null,null,null));
});

ote.app.controller.transport_service.map__GT_DeleteTransportServiceResponse = (function ote$app$controller$transport_service$map__GT_DeleteTransportServiceResponse(G__51894){
return (new ote.app.controller.transport_service.DeleteTransportServiceResponse(new cljs.core.Keyword(null,"response","response",-1068424192).cljs$core$IFn$_invoke$arity$1(G__51894),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51894,new cljs.core.Keyword(null,"response","response",-1068424192))),null));
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
ote.app.controller.transport_service.PublishTransportService = (function (transport_service_id,__meta,__extmap,__hash){
this.transport_service_id = transport_service_id;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_service.PublishTransportService.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_service.PublishTransportService.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51901,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51905 = k51901;
var G__51905__$1 = (((G__51905 instanceof cljs.core.Keyword))?G__51905.fqn:null);
switch (G__51905__$1) {
case "transport-service-id":
return self__.transport_service_id;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51901,else__30866__auto__);

}
});

ote.app.controller.transport_service.PublishTransportService.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-service.PublishTransportService{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966),self__.transport_service_id],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.PublishTransportService.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51900){
var self__ = this;
var G__51900__$1 = this;
return (new cljs.core.RecordIter((0),G__51900__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_service.PublishTransportService.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_service.PublishTransportService.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_service.PublishTransportService(self__.transport_service_id,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.PublishTransportService.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_service.PublishTransportService.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (529072172 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_service.PublishTransportService.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51902,other51903){
var self__ = this;
var this51902__$1 = this;
return (!((other51903 == null))) && ((this51902__$1.constructor === other51903.constructor)) && (cljs.core._EQ_.call(null,this51902__$1.transport_service_id,other51903.transport_service_id)) && (cljs.core._EQ_.call(null,this51902__$1.__extmap,other51903.__extmap));
});

ote.app.controller.transport_service.PublishTransportService.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_service.PublishTransportService(self__.transport_service_id,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_service.PublishTransportService.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51900){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51906 = cljs.core.keyword_identical_QMARK_;
var expr__51907 = k__30871__auto__;
if(cljs.core.truth_(pred__51906.call(null,new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966),expr__51907))){
return (new ote.app.controller.transport_service.PublishTransportService(G__51900,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.transport_service.PublishTransportService(self__.transport_service_id,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51900),null));
}
});

ote.app.controller.transport_service.PublishTransportService.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966),self__.transport_service_id],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.PublishTransportService.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51900){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_service.PublishTransportService(self__.transport_service_id,G__51900,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.PublishTransportService.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_service.PublishTransportService.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"transport-service-id","transport-service-id",660801561,null)], null);
});

ote.app.controller.transport_service.PublishTransportService.cljs$lang$type = true;

ote.app.controller.transport_service.PublishTransportService.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-service/PublishTransportService");
});

ote.app.controller.transport_service.PublishTransportService.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-service/PublishTransportService");
});

ote.app.controller.transport_service.__GT_PublishTransportService = (function ote$app$controller$transport_service$__GT_PublishTransportService(transport_service_id){
return (new ote.app.controller.transport_service.PublishTransportService(transport_service_id,null,null,null));
});

ote.app.controller.transport_service.map__GT_PublishTransportService = (function ote$app$controller$transport_service$map__GT_PublishTransportService(G__51904){
return (new ote.app.controller.transport_service.PublishTransportService(new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966).cljs$core$IFn$_invoke$arity$1(G__51904),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51904,new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966))),null));
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
ote.app.controller.transport_service.PublishTransportServiceResponse = (function (success_QMARK_,transport_service_id,__meta,__extmap,__hash){
this.success_QMARK_ = success_QMARK_;
this.transport_service_id = transport_service_id;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51911,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51915 = k51911;
var G__51915__$1 = (((G__51915 instanceof cljs.core.Keyword))?G__51915.fqn:null);
switch (G__51915__$1) {
case "success?":
return self__.success_QMARK_;

break;
case "transport-service-id":
return self__.transport_service_id;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51911,else__30866__auto__);

}
});

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-service.PublishTransportServiceResponse{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"success?","success?",-122854052),self__.success_QMARK_],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966),self__.transport_service_id],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51910){
var self__ = this;
var G__51910__$1 = this;
return (new cljs.core.RecordIter((0),G__51910__$1,2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"success?","success?",-122854052),new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_service.PublishTransportServiceResponse(self__.success_QMARK_,self__.transport_service_id,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (2 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (710791352 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51912,other51913){
var self__ = this;
var this51912__$1 = this;
return (!((other51913 == null))) && ((this51912__$1.constructor === other51913.constructor)) && (cljs.core._EQ_.call(null,this51912__$1.success_QMARK_,other51913.success_QMARK_)) && (cljs.core._EQ_.call(null,this51912__$1.transport_service_id,other51913.transport_service_id)) && (cljs.core._EQ_.call(null,this51912__$1.__extmap,other51913.__extmap));
});

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966),null,new cljs.core.Keyword(null,"success?","success?",-122854052),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_service.PublishTransportServiceResponse(self__.success_QMARK_,self__.transport_service_id,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51910){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51916 = cljs.core.keyword_identical_QMARK_;
var expr__51917 = k__30871__auto__;
if(cljs.core.truth_(pred__51916.call(null,new cljs.core.Keyword(null,"success?","success?",-122854052),expr__51917))){
return (new ote.app.controller.transport_service.PublishTransportServiceResponse(G__51910,self__.transport_service_id,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__51916.call(null,new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966),expr__51917))){
return (new ote.app.controller.transport_service.PublishTransportServiceResponse(self__.success_QMARK_,G__51910,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.transport_service.PublishTransportServiceResponse(self__.success_QMARK_,self__.transport_service_id,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51910),null));
}
}
});

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"success?","success?",-122854052),self__.success_QMARK_],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966),self__.transport_service_id],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51910){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_service.PublishTransportServiceResponse(self__.success_QMARK_,self__.transport_service_id,G__51910,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_service.PublishTransportServiceResponse.getBasis = (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"success?","success?",1517677475,null),new cljs.core.Symbol(null,"transport-service-id","transport-service-id",660801561,null)], null);
});

ote.app.controller.transport_service.PublishTransportServiceResponse.cljs$lang$type = true;

ote.app.controller.transport_service.PublishTransportServiceResponse.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-service/PublishTransportServiceResponse");
});

ote.app.controller.transport_service.PublishTransportServiceResponse.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-service/PublishTransportServiceResponse");
});

ote.app.controller.transport_service.__GT_PublishTransportServiceResponse = (function ote$app$controller$transport_service$__GT_PublishTransportServiceResponse(success_QMARK_,transport_service_id){
return (new ote.app.controller.transport_service.PublishTransportServiceResponse(success_QMARK_,transport_service_id,null,null,null));
});

ote.app.controller.transport_service.map__GT_PublishTransportServiceResponse = (function ote$app$controller$transport_service$map__GT_PublishTransportServiceResponse(G__51914){
return (new ote.app.controller.transport_service.PublishTransportServiceResponse(new cljs.core.Keyword(null,"success?","success?",-122854052).cljs$core$IFn$_invoke$arity$1(G__51914),new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966).cljs$core$IFn$_invoke$arity$1(G__51914),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51914,new cljs.core.Keyword(null,"success?","success?",-122854052),new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966))),null));
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
ote.app.controller.transport_service.SaveTransportService = (function (publish_QMARK_,__meta,__extmap,__hash){
this.publish_QMARK_ = publish_QMARK_;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_service.SaveTransportService.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_service.SaveTransportService.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51921,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51925 = k51921;
var G__51925__$1 = (((G__51925 instanceof cljs.core.Keyword))?G__51925.fqn:null);
switch (G__51925__$1) {
case "publish?":
return self__.publish_QMARK_;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51921,else__30866__auto__);

}
});

ote.app.controller.transport_service.SaveTransportService.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-service.SaveTransportService{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"publish?","publish?",1504204953),self__.publish_QMARK_],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.SaveTransportService.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51920){
var self__ = this;
var G__51920__$1 = this;
return (new cljs.core.RecordIter((0),G__51920__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"publish?","publish?",1504204953)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_service.SaveTransportService.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_service.SaveTransportService.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_service.SaveTransportService(self__.publish_QMARK_,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.SaveTransportService.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_service.SaveTransportService.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1296434274 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_service.SaveTransportService.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51922,other51923){
var self__ = this;
var this51922__$1 = this;
return (!((other51923 == null))) && ((this51922__$1.constructor === other51923.constructor)) && (cljs.core._EQ_.call(null,this51922__$1.publish_QMARK_,other51923.publish_QMARK_)) && (cljs.core._EQ_.call(null,this51922__$1.__extmap,other51923.__extmap));
});

ote.app.controller.transport_service.SaveTransportService.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"publish?","publish?",1504204953),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_service.SaveTransportService(self__.publish_QMARK_,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_service.SaveTransportService.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51920){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51926 = cljs.core.keyword_identical_QMARK_;
var expr__51927 = k__30871__auto__;
if(cljs.core.truth_(pred__51926.call(null,new cljs.core.Keyword(null,"publish?","publish?",1504204953),expr__51927))){
return (new ote.app.controller.transport_service.SaveTransportService(G__51920,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.transport_service.SaveTransportService(self__.publish_QMARK_,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51920),null));
}
});

ote.app.controller.transport_service.SaveTransportService.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"publish?","publish?",1504204953),self__.publish_QMARK_],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.SaveTransportService.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51920){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_service.SaveTransportService(self__.publish_QMARK_,G__51920,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.SaveTransportService.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_service.SaveTransportService.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"publish?","publish?",-1150230816,null)], null);
});

ote.app.controller.transport_service.SaveTransportService.cljs$lang$type = true;

ote.app.controller.transport_service.SaveTransportService.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-service/SaveTransportService");
});

ote.app.controller.transport_service.SaveTransportService.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-service/SaveTransportService");
});

ote.app.controller.transport_service.__GT_SaveTransportService = (function ote$app$controller$transport_service$__GT_SaveTransportService(publish_QMARK_){
return (new ote.app.controller.transport_service.SaveTransportService(publish_QMARK_,null,null,null));
});

ote.app.controller.transport_service.map__GT_SaveTransportService = (function ote$app$controller$transport_service$map__GT_SaveTransportService(G__51924){
return (new ote.app.controller.transport_service.SaveTransportService(new cljs.core.Keyword(null,"publish?","publish?",1504204953).cljs$core$IFn$_invoke$arity$1(G__51924),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51924,new cljs.core.Keyword(null,"publish?","publish?",1504204953))),null));
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
ote.app.controller.transport_service.SaveTransportServiceResponse = (function (response,__meta,__extmap,__hash){
this.response = response;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51931,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51935 = k51931;
var G__51935__$1 = (((G__51935 instanceof cljs.core.Keyword))?G__51935.fqn:null);
switch (G__51935__$1) {
case "response":
return self__.response;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51931,else__30866__auto__);

}
});

ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-service.SaveTransportServiceResponse{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51930){
var self__ = this;
var G__51930__$1 = this;
return (new cljs.core.RecordIter((0),G__51930__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"response","response",-1068424192)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_service.SaveTransportServiceResponse(self__.response,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-1968365706 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51932,other51933){
var self__ = this;
var this51932__$1 = this;
return (!((other51933 == null))) && ((this51932__$1.constructor === other51933.constructor)) && (cljs.core._EQ_.call(null,this51932__$1.response,other51933.response)) && (cljs.core._EQ_.call(null,this51932__$1.__extmap,other51933.__extmap));
});

ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"response","response",-1068424192),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_service.SaveTransportServiceResponse(self__.response,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51930){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51936 = cljs.core.keyword_identical_QMARK_;
var expr__51937 = k__30871__auto__;
if(cljs.core.truth_(pred__51936.call(null,new cljs.core.Keyword(null,"response","response",-1068424192),expr__51937))){
return (new ote.app.controller.transport_service.SaveTransportServiceResponse(G__51930,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.transport_service.SaveTransportServiceResponse(self__.response,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51930),null));
}
});

ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51930){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_service.SaveTransportServiceResponse(self__.response,G__51930,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_service.SaveTransportServiceResponse.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"response","response",572107335,null)], null);
});

ote.app.controller.transport_service.SaveTransportServiceResponse.cljs$lang$type = true;

ote.app.controller.transport_service.SaveTransportServiceResponse.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-service/SaveTransportServiceResponse");
});

ote.app.controller.transport_service.SaveTransportServiceResponse.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-service/SaveTransportServiceResponse");
});

ote.app.controller.transport_service.__GT_SaveTransportServiceResponse = (function ote$app$controller$transport_service$__GT_SaveTransportServiceResponse(response){
return (new ote.app.controller.transport_service.SaveTransportServiceResponse(response,null,null,null));
});

ote.app.controller.transport_service.map__GT_SaveTransportServiceResponse = (function ote$app$controller$transport_service$map__GT_SaveTransportServiceResponse(G__51934){
return (new ote.app.controller.transport_service.SaveTransportServiceResponse(new cljs.core.Keyword(null,"response","response",-1068424192).cljs$core$IFn$_invoke$arity$1(G__51934),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51934,new cljs.core.Keyword(null,"response","response",-1068424192))),null));
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
ote.app.controller.transport_service.CancelTransportServiceForm = (function (__meta,__extmap,__hash){
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.transport_service.CancelTransportServiceForm.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51941,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51945 = k51941;
switch (G__51945) {
default:
return cljs.core.get.call(null,self__.__extmap,k51941,else__30866__auto__);

}
});

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.transport-service.CancelTransportServiceForm{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51940){
var self__ = this;
var G__51940__$1 = this;
return (new cljs.core.RecordIter((0),G__51940__$1,0,cljs.core.PersistentVector.EMPTY,(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.transport_service.CancelTransportServiceForm(self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (0 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-1399963671 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51942,other51943){
var self__ = this;
var this51942__$1 = this;
return (!((other51943 == null))) && ((this51942__$1.constructor === other51943.constructor)) && (cljs.core._EQ_.call(null,this51942__$1.__extmap,other51943.__extmap));
});

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.EMPTY,k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.transport_service.CancelTransportServiceForm(self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51940){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51946 = cljs.core.keyword_identical_QMARK_;
var expr__51947 = k__30871__auto__;
return (new ote.app.controller.transport_service.CancelTransportServiceForm(self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51940),null));
});

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51940){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.transport_service.CancelTransportServiceForm(G__51940,self__.__extmap,self__.__hash));
});

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.transport_service.CancelTransportServiceForm.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

ote.app.controller.transport_service.CancelTransportServiceForm.cljs$lang$type = true;

ote.app.controller.transport_service.CancelTransportServiceForm.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.transport-service/CancelTransportServiceForm");
});

ote.app.controller.transport_service.CancelTransportServiceForm.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.transport-service/CancelTransportServiceForm");
});

ote.app.controller.transport_service.__GT_CancelTransportServiceForm = (function ote$app$controller$transport_service$__GT_CancelTransportServiceForm(){
return (new ote.app.controller.transport_service.CancelTransportServiceForm(null,null,null));
});

ote.app.controller.transport_service.map__GT_CancelTransportServiceForm = (function ote$app$controller$transport_service$map__GT_CancelTransportServiceForm(G__51944){
return (new ote.app.controller.transport_service.CancelTransportServiceForm(null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51944)),null));
});


ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_service.SaveTransportServiceResponse.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51953,app){
var map__51954 = p__51953;
var map__51954__$1 = ((((!((map__51954 == null)))?((((map__51954.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51954.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51954):map__51954);
var response = cljs.core.get.call(null,map__51954__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
var map__51956 = this;
var map__51956__$1 = ((((!((map__51956 == null)))?((((map__51956.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51956.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51956):map__51956);
var response__$1 = cljs.core.get.call(null,map__51956__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
ote.app.routes.navigate_BANG_.call(null,new cljs.core.Keyword(null,"own-services","own-services",-1593467283));

return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"flash-message","flash-message",51770715),ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"common-texts","common-texts",-934994303),new cljs.core.Keyword(null,"transport-service-saved","transport-service-saved",1815560649)], null)));
});

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_service.ModifyTransportServiceResponse.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51958,app){
var map__51959 = p__51958;
var map__51959__$1 = ((((!((map__51959 == null)))?((((map__51959.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51959.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51959):map__51959);
var response = cljs.core.get.call(null,map__51959__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
var map__51961 = this;
var map__51961__$1 = ((((!((map__51961 == null)))?((((map__51961.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51961.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51961):map__51961);
var response__$1 = cljs.core.get.call(null,map__51961__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
var type = new cljs.core.Keyword("ote.db.transport-service","type","ote.db.transport-service/type",-1555621317).cljs$core$IFn$_invoke$arity$1(response__$1);
return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"transport-service-loaded?","transport-service-loaded?",-1691222650),true,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706),ote.app.controller.transport_service.move_service_level_keys_to_form.call(null,cljs.core.update.call(null,response__$1,new cljs.core.Keyword("ote.db.transport-service","operation-area","ote.db.transport-service/operation-area",1530382917),ote.app.controller.place_search.operation_area_to_places),ote.db.transport_service.service_key_by_type.call(null,type)));
});

ote.app.controller.transport_service.PublishTransportService.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_service.PublishTransportService.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51963,app){
var map__51964 = p__51963;
var map__51964__$1 = ((((!((map__51964 == null)))?((((map__51964.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51964.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51964):map__51964);
var transport_service_id = cljs.core.get.call(null,map__51964__$1,new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966));
var map__51966 = this;
var map__51966__$1 = ((((!((map__51966 == null)))?((((map__51966.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51966.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51966):map__51966);
var transport_service_id__$1 = cljs.core.get.call(null,map__51966__$1,new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966));
ote.communication.post_BANG_.call(null,["transport-service/publish"].join(''),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966),transport_service_id__$1], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-success","on-success",1786904109),tuck.core.send_async_BANG_.call(null,ote.app.controller.transport_service.__GT_PublishTransportServiceResponse,transport_service_id__$1)], null));

return app;
});

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_service.DeleteTransportServiceResponse.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51968,app){
var map__51969 = p__51968;
var map__51969__$1 = ((((!((map__51969 == null)))?((((map__51969.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51969.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51969):map__51969);
var response = cljs.core.get.call(null,map__51969__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
var map__51971 = this;
var map__51971__$1 = ((((!((map__51971 == null)))?((((map__51971.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51971.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51971):map__51971);
var response__$1 = cljs.core.get.call(null,map__51971__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
var filtered_map = cljs.core.filter.call(null,((function (map__51971,map__51971__$1,response__$1,map__51969,map__51969__$1,response){
return (function (p1__51952_SHARP_){
return cljs.core.not_EQ_.call(null,new cljs.core.Keyword("ote.db.transport-service","id","ote.db.transport-service/id",192939397).cljs$core$IFn$_invoke$arity$1(p1__51952_SHARP_),(response__$1 | (0)));
});})(map__51971,map__51971__$1,response__$1,map__51969,map__51969__$1,response))
,cljs.core.get.call(null,app,new cljs.core.Keyword(null,"transport-services","transport-services",-1601696230)));
return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"transport-services","transport-services",-1601696230),filtered_map);
});

ote.app.controller.transport_service.OpenTransportServicePage.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_service.OpenTransportServicePage.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51973,app){
var map__51974 = p__51973;
var map__51974__$1 = ((((!((map__51974 == null)))?((((map__51974.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51974.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51974):map__51974);
var id = cljs.core.get.call(null,map__51974__$1,new cljs.core.Keyword(null,"id","id",-1388402092));
var map__51976 = this;
var map__51976__$1 = ((((!((map__51976 == null)))?((((map__51976.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51976.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51976):map__51976);
var id__$1 = cljs.core.get.call(null,map__51976__$1,new cljs.core.Keyword(null,"id","id",-1388402092));
window.location = ["/ote/index.html#/edit-service/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(id__$1)].join('');

return app;
});

ote.app.controller.transport_service.DeleteTransportService.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_service.DeleteTransportService.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51978,app){
var map__51979 = p__51978;
var map__51979__$1 = ((((!((map__51979 == null)))?((((map__51979.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51979.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51979):map__51979);
var id = cljs.core.get.call(null,map__51979__$1,new cljs.core.Keyword(null,"id","id",-1388402092));
var map__51981 = this;
var map__51981__$1 = ((((!((map__51981 == null)))?((((map__51981.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51981.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51981):map__51981);
var id__$1 = cljs.core.get.call(null,map__51981__$1,new cljs.core.Keyword(null,"id","id",-1388402092));
ote.communication.get_BANG_.call(null,["transport-service/delete/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(id__$1)].join(''),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-success","on-success",1786904109),tuck.core.send_async_BANG_.call(null,ote.app.controller.transport_service.__GT_DeleteTransportServiceResponse)], null));

return app;
});

ote.app.controller.transport_service.AddPriceClassRow.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_service.AddPriceClassRow.prototype.tuck$core$Event$process_event$arity$2 = (function (_,app){
var ___$1 = this;
return cljs.core.update_in.call(null,app,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706),new cljs.core.Keyword("ote.db.transport-service","passenger-transportation","ote.db.transport-service/passenger-transportation",-2018752833),new cljs.core.Keyword("ote.db.transport-service","price-classes","ote.db.transport-service/price-classes",-1345531548)], null),((function (___$1){
return (function (p1__51950_SHARP_){
return cljs.core.conj.call(null,(function (){var or__30175__auto__ = p1__51950_SHARP_;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return cljs.core.PersistentVector.EMPTY;
}
})(),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("ote.db.transport-service","currency","ote.db.transport-service/currency",1754918975),"EUR"], null));
});})(___$1))
);
});

ote.app.controller.transport_service.SaveTransportService.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_service.SaveTransportService.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51983,p__51984){
var map__51985 = p__51983;
var map__51985__$1 = ((((!((map__51985 == null)))?((((map__51985.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51985.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51985):map__51985);
var publish_QMARK_ = cljs.core.get.call(null,map__51985__$1,new cljs.core.Keyword(null,"publish?","publish?",1504204953));
var map__51986 = p__51984;
var map__51986__$1 = ((((!((map__51986 == null)))?((((map__51986.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51986.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51986):map__51986);
var app = map__51986__$1;
var service = cljs.core.get.call(null,map__51986__$1,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706));
var operator = cljs.core.get.call(null,map__51986__$1,new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982));
var map__51989 = this;
var map__51989__$1 = ((((!((map__51989 == null)))?((((map__51989.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51989.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51989):map__51989);
var publish_QMARK___$1 = cljs.core.get.call(null,map__51989__$1,new cljs.core.Keyword(null,"publish?","publish?",1504204953));
var key = ote.db.transport_service.service_key_by_type.call(null,new cljs.core.Keyword("ote.db.transport-service","type","ote.db.transport-service/type",-1555621317).cljs$core$IFn$_invoke$arity$1(service));
var service_data = cljs.core.update.call(null,ote.app.controller.transport_service.move_service_level_keys_from_form.call(null,cljs.core.update.call(null,cljs.core.assoc.call(null,service,new cljs.core.Keyword("ote.db.transport-service","published?","ote.db.transport-service/published?",178049784),publish_QMARK___$1,new cljs.core.Keyword("ote.db.transport-service","transport-operator-id","ote.db.transport-service/transport-operator-id",1381166108),new cljs.core.Keyword("ote.db.transport-operator","id","ote.db.transport-operator/id",343347306).cljs$core$IFn$_invoke$arity$1(operator)),key,ote.ui.form.without_form_metadata),key),new cljs.core.Keyword("ote.db.transport-service","operation-area","ote.db.transport-service/operation-area",1530382917),ote.app.controller.place_search.place_references);
ote.communication.post_BANG_.call(null,"transport-service",service_data,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-success","on-success",1786904109),tuck.core.send_async_BANG_.call(null,ote.app.controller.transport_service.__GT_SaveTransportServiceResponse)], null));

return app;
});

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_service.CancelTransportServiceForm.prototype.tuck$core$Event$process_event$arity$2 = (function (_,app){
var ___$1 = this;
ote.app.routes.navigate_BANG_.call(null,new cljs.core.Keyword(null,"own-services","own-services",-1593467283));

return cljs.core.dissoc.call(null,app,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706));
});

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_service.PublishTransportServiceResponse.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51991,app){
var map__51992 = p__51991;
var map__51992__$1 = ((((!((map__51992 == null)))?((((map__51992.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51992.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51992):map__51992);
var e = map__51992__$1;
var success_QMARK_ = cljs.core.get.call(null,map__51992__$1,new cljs.core.Keyword(null,"success?","success?",-122854052));
var transport_service_id = cljs.core.get.call(null,map__51992__$1,new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966));
var map__51994 = this;
var map__51994__$1 = ((((!((map__51994 == null)))?((((map__51994.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51994.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51994):map__51994);
var e__$1 = map__51994__$1;
var success_QMARK___$1 = cljs.core.get.call(null,map__51994__$1,new cljs.core.Keyword(null,"success?","success?",-122854052));
var transport_service_id__$1 = cljs.core.get.call(null,map__51994__$1,new cljs.core.Keyword(null,"transport-service-id","transport-service-id",-979729966));
if(cljs.core.truth_(success_QMARK___$1)){
return cljs.core.update.call(null,app,new cljs.core.Keyword(null,"transport-services","transport-services",-1601696230),((function (map__51994,map__51994__$1,e__$1,success_QMARK___$1,transport_service_id__$1,map__51992,map__51992__$1,e,success_QMARK_,transport_service_id){
return (function (services){
return cljs.core.map.call(null,((function (map__51994,map__51994__$1,e__$1,success_QMARK___$1,transport_service_id__$1,map__51992,map__51992__$1,e,success_QMARK_,transport_service_id){
return (function (p__51996){
var map__51997 = p__51996;
var map__51997__$1 = ((((!((map__51997 == null)))?((((map__51997.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51997.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51997):map__51997);
var service = map__51997__$1;
var id = cljs.core.get.call(null,map__51997__$1,new cljs.core.Keyword("ote.db.transport-service","id","ote.db.transport-service/id",192939397));
if(cljs.core._EQ_.call(null,id,transport_service_id__$1)){
return cljs.core.assoc.call(null,service,new cljs.core.Keyword("ote.db.transport-service","published?","ote.db.transport-service/published?",178049784),true);
} else {
return service;
}
});})(map__51994,map__51994__$1,e__$1,success_QMARK___$1,transport_service_id__$1,map__51992,map__51992__$1,e,success_QMARK_,transport_service_id))
,services);
});})(map__51994,map__51994__$1,e__$1,success_QMARK___$1,transport_service_id__$1,map__51992,map__51992__$1,e,success_QMARK_,transport_service_id))
);
} else {
return app;
}
});

ote.app.controller.transport_service.ModifyTransportService.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_service.ModifyTransportService.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51999,app){
var map__52000 = p__51999;
var map__52000__$1 = ((((!((map__52000 == null)))?((((map__52000.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52000.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52000):map__52000);
var id = cljs.core.get.call(null,map__52000__$1,new cljs.core.Keyword(null,"id","id",-1388402092));
var map__52002 = this;
var map__52002__$1 = ((((!((map__52002 == null)))?((((map__52002.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52002.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52002):map__52002);
var id__$1 = cljs.core.get.call(null,map__52002__$1,new cljs.core.Keyword(null,"id","id",-1388402092));
ote.communication.get_BANG_.call(null,["transport-service/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(id__$1)].join(''),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-success","on-success",1786904109),tuck.core.send_async_BANG_.call(null,ote.app.controller.transport_service.__GT_ModifyTransportServiceResponse)], null));

return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"transport-service-loaded?","transport-service-loaded?",-1691222650),false);
});

ote.app.controller.transport_service.AddServiceHourRow.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_service.AddServiceHourRow.prototype.tuck$core$Event$process_event$arity$2 = (function (_,app){
var ___$1 = this;
return cljs.core.update_in.call(null,app,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706),new cljs.core.Keyword("ote.db.transport-service","passenger-transportation","ote.db.transport-service/passenger-transportation",-2018752833),new cljs.core.Keyword("ote.db.transport-service","service-hours","ote.db.transport-service/service-hours",-203742050)], null),((function (___$1){
return (function (p1__51951_SHARP_){
return cljs.core.conj.call(null,(function (){var or__30175__auto__ = p1__51951_SHARP_;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return cljs.core.PersistentVector.EMPTY;
}
})(),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("ote.db.transport-service","from","ote.db.transport-service/from",233462179),ote.time.parse_time.call(null,"08:00")], null));
});})(___$1))
);
});

ote.app.controller.transport_service.RemovePriceClassRow.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_service.RemovePriceClassRow.prototype.tuck$core$Event$process_event$arity$2 = (function (_,app){
var ___$1 = this;
return cljs.core.assoc_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"t-service","t-service",-1499493621),new cljs.core.Keyword(null,"price-class-open","price-class-open",769463924)], null),false);
});

ote.app.controller.transport_service.SelectTransportServiceType.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.transport_service.SelectTransportServiceType.prototype.tuck$core$Event$process_event$arity$2 = (function (p__52004,app){
var map__52005 = p__52004;
var map__52005__$1 = ((((!((map__52005 == null)))?((((map__52005.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52005.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52005):map__52005);
var data = cljs.core.get.call(null,map__52005__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var map__52007 = this;
var map__52007__$1 = ((((!((map__52007 == null)))?((((map__52007.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52007.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52007):map__52007);
var data__$1 = cljs.core.get.call(null,map__52007__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var service_type_subtype = cljs.core.get.call(null,data__$1,new cljs.core.Keyword(null,"transport-service-type-subtype","transport-service-type-subtype",-1833764223));
var type = ote.app.controller.transport_service.service_type_from_combined_service_type.call(null,service_type_subtype);
var sub_type = ote.app.controller.transport_service.subtype_from_combined_service_type.call(null,service_type_subtype);
ote.app.routes.navigate_BANG_.call(null,type);

return cljs.core.assoc_in.call(null,cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("ote.db.transport-service","type","ote.db.transport-service/type",-1555621317),type], null)),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"transport-service","transport-service",-1754331706),ote.db.transport_service.service_key_by_type.call(null,type)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("ote.db.transport-service","sub-type","ote.db.transport-service/sub-type",1455227203),sub_type], null));
});
/**
 * The form only sees the type specific level, move keys that are stored in the
 *   transport-service level there.
 */
ote.app.controller.transport_service.move_service_level_keys_from_form = (function ote$app$controller$transport_service$move_service_level_keys_from_form(service,from){
return cljs.core.reduce.call(null,(function (service__$1,key){
return cljs.core.update.call(null,cljs.core.assoc.call(null,service__$1,key,cljs.core.get_in.call(null,service__$1,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [from,key], null))),from,cljs.core.dissoc,key);
}),service,ote.app.controller.transport_service.service_level_keys);
});
/**
 * Reverse of `move-service-level-keys-from-form`.
 */
ote.app.controller.transport_service.move_service_level_keys_to_form = (function ote$app$controller$transport_service$move_service_level_keys_to_form(service,to){
return cljs.core.reduce.call(null,(function (service__$1,key){
return cljs.core.dissoc.call(null,cljs.core.assoc_in.call(null,service__$1,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [to,key], null),cljs.core.get.call(null,service__$1,key)),key);
}),service,ote.app.controller.transport_service.service_level_keys);
});

//# sourceMappingURL=transport_service.js.map?rel=1510137293728
