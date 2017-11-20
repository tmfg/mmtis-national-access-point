// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.app.controller.place_search');
goog.require('cljs.core');
goog.require('tuck.core');
goog.require('ote.communication');
goog.require('ote.db.places');
goog.require('clojure.string');
goog.require('taoensso.timbre');
goog.require('ote.db.transport_service');

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
ote.app.controller.place_search.SetPlaceName = (function (name,__meta,__extmap,__hash){
this.name = name;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.place_search.SetPlaceName.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.place_search.SetPlaceName.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51565,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51569 = k51565;
var G__51569__$1 = (((G__51569 instanceof cljs.core.Keyword))?G__51569.fqn:null);
switch (G__51569__$1) {
case "name":
return self__.name;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51565,else__30866__auto__);

}
});

ote.app.controller.place_search.SetPlaceName.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.place-search.SetPlaceName{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"name","name",1843675177),self__.name],null))], null),self__.__extmap));
});

ote.app.controller.place_search.SetPlaceName.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51564){
var self__ = this;
var G__51564__$1 = this;
return (new cljs.core.RecordIter((0),G__51564__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"name","name",1843675177)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.place_search.SetPlaceName.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.place_search.SetPlaceName.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.place_search.SetPlaceName(self__.name,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.SetPlaceName.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.place_search.SetPlaceName.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (389757443 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.place_search.SetPlaceName.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51566,other51567){
var self__ = this;
var this51566__$1 = this;
return (!((other51567 == null))) && ((this51566__$1.constructor === other51567.constructor)) && (cljs.core._EQ_.call(null,this51566__$1.name,other51567.name)) && (cljs.core._EQ_.call(null,this51566__$1.__extmap,other51567.__extmap));
});

ote.app.controller.place_search.SetPlaceName.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"name","name",1843675177),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.place_search.SetPlaceName(self__.name,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.place_search.SetPlaceName.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51564){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51570 = cljs.core.keyword_identical_QMARK_;
var expr__51571 = k__30871__auto__;
if(cljs.core.truth_(pred__51570.call(null,new cljs.core.Keyword(null,"name","name",1843675177),expr__51571))){
return (new ote.app.controller.place_search.SetPlaceName(G__51564,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.place_search.SetPlaceName(self__.name,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51564),null));
}
});

ote.app.controller.place_search.SetPlaceName.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"name","name",1843675177),self__.name],null))], null),self__.__extmap));
});

ote.app.controller.place_search.SetPlaceName.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51564){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.place_search.SetPlaceName(self__.name,G__51564,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.SetPlaceName.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.place_search.SetPlaceName.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"name","name",-810760592,null)], null);
});

ote.app.controller.place_search.SetPlaceName.cljs$lang$type = true;

ote.app.controller.place_search.SetPlaceName.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.place-search/SetPlaceName");
});

ote.app.controller.place_search.SetPlaceName.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.place-search/SetPlaceName");
});

ote.app.controller.place_search.__GT_SetPlaceName = (function ote$app$controller$place_search$__GT_SetPlaceName(name){
return (new ote.app.controller.place_search.SetPlaceName(name,null,null,null));
});

ote.app.controller.place_search.map__GT_SetPlaceName = (function ote$app$controller$place_search$map__GT_SetPlaceName(G__51568){
return (new ote.app.controller.place_search.SetPlaceName(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(G__51568),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51568,new cljs.core.Keyword(null,"name","name",1843675177))),null));
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
ote.app.controller.place_search.AddPlace = (function (id,__meta,__extmap,__hash){
this.id = id;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.place_search.AddPlace.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.place_search.AddPlace.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51575,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51579 = k51575;
var G__51579__$1 = (((G__51579 instanceof cljs.core.Keyword))?G__51579.fqn:null);
switch (G__51579__$1) {
case "id":
return self__.id;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51575,else__30866__auto__);

}
});

ote.app.controller.place_search.AddPlace.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.place-search.AddPlace{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"id","id",-1388402092),self__.id],null))], null),self__.__extmap));
});

ote.app.controller.place_search.AddPlace.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51574){
var self__ = this;
var G__51574__$1 = this;
return (new cljs.core.RecordIter((0),G__51574__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"id","id",-1388402092)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.place_search.AddPlace.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.place_search.AddPlace.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.place_search.AddPlace(self__.id,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.AddPlace.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.place_search.AddPlace.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-796879219 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.place_search.AddPlace.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51576,other51577){
var self__ = this;
var this51576__$1 = this;
return (!((other51577 == null))) && ((this51576__$1.constructor === other51577.constructor)) && (cljs.core._EQ_.call(null,this51576__$1.id,other51577.id)) && (cljs.core._EQ_.call(null,this51576__$1.__extmap,other51577.__extmap));
});

ote.app.controller.place_search.AddPlace.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"id","id",-1388402092),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.place_search.AddPlace(self__.id,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.place_search.AddPlace.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51574){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51580 = cljs.core.keyword_identical_QMARK_;
var expr__51581 = k__30871__auto__;
if(cljs.core.truth_(pred__51580.call(null,new cljs.core.Keyword(null,"id","id",-1388402092),expr__51581))){
return (new ote.app.controller.place_search.AddPlace(G__51574,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.place_search.AddPlace(self__.id,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51574),null));
}
});

ote.app.controller.place_search.AddPlace.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"id","id",-1388402092),self__.id],null))], null),self__.__extmap));
});

ote.app.controller.place_search.AddPlace.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51574){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.place_search.AddPlace(self__.id,G__51574,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.AddPlace.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.place_search.AddPlace.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"id","id",252129435,null)], null);
});

ote.app.controller.place_search.AddPlace.cljs$lang$type = true;

ote.app.controller.place_search.AddPlace.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.place-search/AddPlace");
});

ote.app.controller.place_search.AddPlace.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.place-search/AddPlace");
});

ote.app.controller.place_search.__GT_AddPlace = (function ote$app$controller$place_search$__GT_AddPlace(id){
return (new ote.app.controller.place_search.AddPlace(id,null,null,null));
});

ote.app.controller.place_search.map__GT_AddPlace = (function ote$app$controller$place_search$map__GT_AddPlace(G__51578){
return (new ote.app.controller.place_search.AddPlace(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(G__51578),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51578,new cljs.core.Keyword(null,"id","id",-1388402092))),null));
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
ote.app.controller.place_search.FetchPlaceResponse = (function (response,place,__meta,__extmap,__hash){
this.response = response;
this.place = place;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.place_search.FetchPlaceResponse.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.place_search.FetchPlaceResponse.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51585,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51589 = k51585;
var G__51589__$1 = (((G__51589 instanceof cljs.core.Keyword))?G__51589.fqn:null);
switch (G__51589__$1) {
case "response":
return self__.response;

break;
case "place":
return self__.place;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51585,else__30866__auto__);

}
});

ote.app.controller.place_search.FetchPlaceResponse.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.place-search.FetchPlaceResponse{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"place","place",-819689466),self__.place],null))], null),self__.__extmap));
});

ote.app.controller.place_search.FetchPlaceResponse.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51584){
var self__ = this;
var G__51584__$1 = this;
return (new cljs.core.RecordIter((0),G__51584__$1,2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"response","response",-1068424192),new cljs.core.Keyword(null,"place","place",-819689466)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.place_search.FetchPlaceResponse.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.place_search.FetchPlaceResponse.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.place_search.FetchPlaceResponse(self__.response,self__.place,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.FetchPlaceResponse.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (2 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.place_search.FetchPlaceResponse.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-1129300621 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.place_search.FetchPlaceResponse.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51586,other51587){
var self__ = this;
var this51586__$1 = this;
return (!((other51587 == null))) && ((this51586__$1.constructor === other51587.constructor)) && (cljs.core._EQ_.call(null,this51586__$1.response,other51587.response)) && (cljs.core._EQ_.call(null,this51586__$1.place,other51587.place)) && (cljs.core._EQ_.call(null,this51586__$1.__extmap,other51587.__extmap));
});

ote.app.controller.place_search.FetchPlaceResponse.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"response","response",-1068424192),null,new cljs.core.Keyword(null,"place","place",-819689466),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.place_search.FetchPlaceResponse(self__.response,self__.place,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.place_search.FetchPlaceResponse.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51584){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51590 = cljs.core.keyword_identical_QMARK_;
var expr__51591 = k__30871__auto__;
if(cljs.core.truth_(pred__51590.call(null,new cljs.core.Keyword(null,"response","response",-1068424192),expr__51591))){
return (new ote.app.controller.place_search.FetchPlaceResponse(G__51584,self__.place,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__51590.call(null,new cljs.core.Keyword(null,"place","place",-819689466),expr__51591))){
return (new ote.app.controller.place_search.FetchPlaceResponse(self__.response,G__51584,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.place_search.FetchPlaceResponse(self__.response,self__.place,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51584),null));
}
}
});

ote.app.controller.place_search.FetchPlaceResponse.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"place","place",-819689466),self__.place],null))], null),self__.__extmap));
});

ote.app.controller.place_search.FetchPlaceResponse.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51584){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.place_search.FetchPlaceResponse(self__.response,self__.place,G__51584,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.FetchPlaceResponse.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.place_search.FetchPlaceResponse.getBasis = (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"response","response",572107335,null),new cljs.core.Symbol(null,"place","place",820842061,null)], null);
});

ote.app.controller.place_search.FetchPlaceResponse.cljs$lang$type = true;

ote.app.controller.place_search.FetchPlaceResponse.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.place-search/FetchPlaceResponse");
});

ote.app.controller.place_search.FetchPlaceResponse.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.place-search/FetchPlaceResponse");
});

ote.app.controller.place_search.__GT_FetchPlaceResponse = (function ote$app$controller$place_search$__GT_FetchPlaceResponse(response,place){
return (new ote.app.controller.place_search.FetchPlaceResponse(response,place,null,null,null));
});

ote.app.controller.place_search.map__GT_FetchPlaceResponse = (function ote$app$controller$place_search$map__GT_FetchPlaceResponse(G__51588){
return (new ote.app.controller.place_search.FetchPlaceResponse(new cljs.core.Keyword(null,"response","response",-1068424192).cljs$core$IFn$_invoke$arity$1(G__51588),new cljs.core.Keyword(null,"place","place",-819689466).cljs$core$IFn$_invoke$arity$1(G__51588),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51588,new cljs.core.Keyword(null,"response","response",-1068424192),new cljs.core.Keyword(null,"place","place",-819689466))),null));
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
ote.app.controller.place_search.RemovePlaceById = (function (id,__meta,__extmap,__hash){
this.id = id;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.place_search.RemovePlaceById.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.place_search.RemovePlaceById.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51595,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51599 = k51595;
var G__51599__$1 = (((G__51599 instanceof cljs.core.Keyword))?G__51599.fqn:null);
switch (G__51599__$1) {
case "id":
return self__.id;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51595,else__30866__auto__);

}
});

ote.app.controller.place_search.RemovePlaceById.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.place-search.RemovePlaceById{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"id","id",-1388402092),self__.id],null))], null),self__.__extmap));
});

ote.app.controller.place_search.RemovePlaceById.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51594){
var self__ = this;
var G__51594__$1 = this;
return (new cljs.core.RecordIter((0),G__51594__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"id","id",-1388402092)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.place_search.RemovePlaceById.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.place_search.RemovePlaceById.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.place_search.RemovePlaceById(self__.id,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.RemovePlaceById.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.place_search.RemovePlaceById.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-404365924 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.place_search.RemovePlaceById.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51596,other51597){
var self__ = this;
var this51596__$1 = this;
return (!((other51597 == null))) && ((this51596__$1.constructor === other51597.constructor)) && (cljs.core._EQ_.call(null,this51596__$1.id,other51597.id)) && (cljs.core._EQ_.call(null,this51596__$1.__extmap,other51597.__extmap));
});

ote.app.controller.place_search.RemovePlaceById.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"id","id",-1388402092),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.place_search.RemovePlaceById(self__.id,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.place_search.RemovePlaceById.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51594){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51600 = cljs.core.keyword_identical_QMARK_;
var expr__51601 = k__30871__auto__;
if(cljs.core.truth_(pred__51600.call(null,new cljs.core.Keyword(null,"id","id",-1388402092),expr__51601))){
return (new ote.app.controller.place_search.RemovePlaceById(G__51594,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.place_search.RemovePlaceById(self__.id,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51594),null));
}
});

ote.app.controller.place_search.RemovePlaceById.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"id","id",-1388402092),self__.id],null))], null),self__.__extmap));
});

ote.app.controller.place_search.RemovePlaceById.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51594){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.place_search.RemovePlaceById(self__.id,G__51594,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.RemovePlaceById.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.place_search.RemovePlaceById.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"id","id",252129435,null)], null);
});

ote.app.controller.place_search.RemovePlaceById.cljs$lang$type = true;

ote.app.controller.place_search.RemovePlaceById.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.place-search/RemovePlaceById");
});

ote.app.controller.place_search.RemovePlaceById.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.place-search/RemovePlaceById");
});

ote.app.controller.place_search.__GT_RemovePlaceById = (function ote$app$controller$place_search$__GT_RemovePlaceById(id){
return (new ote.app.controller.place_search.RemovePlaceById(id,null,null,null));
});

ote.app.controller.place_search.map__GT_RemovePlaceById = (function ote$app$controller$place_search$map__GT_RemovePlaceById(G__51598){
return (new ote.app.controller.place_search.RemovePlaceById(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(G__51598),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51598,new cljs.core.Keyword(null,"id","id",-1388402092))),null));
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
ote.app.controller.place_search.PlaceCompletionsResponse = (function (completions,name,__meta,__extmap,__hash){
this.completions = completions;
this.name = name;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.place_search.PlaceCompletionsResponse.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51605,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51609 = k51605;
var G__51609__$1 = (((G__51609 instanceof cljs.core.Keyword))?G__51609.fqn:null);
switch (G__51609__$1) {
case "completions":
return self__.completions;

break;
case "name":
return self__.name;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51605,else__30866__auto__);

}
});

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.place-search.PlaceCompletionsResponse{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"completions","completions",-190930179),self__.completions],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"name","name",1843675177),self__.name],null))], null),self__.__extmap));
});

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51604){
var self__ = this;
var G__51604__$1 = this;
return (new cljs.core.RecordIter((0),G__51604__$1,2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"completions","completions",-190930179),new cljs.core.Keyword(null,"name","name",1843675177)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.place_search.PlaceCompletionsResponse(self__.completions,self__.name,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (2 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-1941740800 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51606,other51607){
var self__ = this;
var this51606__$1 = this;
return (!((other51607 == null))) && ((this51606__$1.constructor === other51607.constructor)) && (cljs.core._EQ_.call(null,this51606__$1.completions,other51607.completions)) && (cljs.core._EQ_.call(null,this51606__$1.name,other51607.name)) && (cljs.core._EQ_.call(null,this51606__$1.__extmap,other51607.__extmap));
});

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"completions","completions",-190930179),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.place_search.PlaceCompletionsResponse(self__.completions,self__.name,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51604){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51610 = cljs.core.keyword_identical_QMARK_;
var expr__51611 = k__30871__auto__;
if(cljs.core.truth_(pred__51610.call(null,new cljs.core.Keyword(null,"completions","completions",-190930179),expr__51611))){
return (new ote.app.controller.place_search.PlaceCompletionsResponse(G__51604,self__.name,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__51610.call(null,new cljs.core.Keyword(null,"name","name",1843675177),expr__51611))){
return (new ote.app.controller.place_search.PlaceCompletionsResponse(self__.completions,G__51604,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.place_search.PlaceCompletionsResponse(self__.completions,self__.name,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51604),null));
}
}
});

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"completions","completions",-190930179),self__.completions],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"name","name",1843675177),self__.name],null))], null),self__.__extmap));
});

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51604){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.place_search.PlaceCompletionsResponse(self__.completions,self__.name,G__51604,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.place_search.PlaceCompletionsResponse.getBasis = (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"completions","completions",1449601348,null),new cljs.core.Symbol(null,"name","name",-810760592,null)], null);
});

ote.app.controller.place_search.PlaceCompletionsResponse.cljs$lang$type = true;

ote.app.controller.place_search.PlaceCompletionsResponse.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.place-search/PlaceCompletionsResponse");
});

ote.app.controller.place_search.PlaceCompletionsResponse.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.place-search/PlaceCompletionsResponse");
});

ote.app.controller.place_search.__GT_PlaceCompletionsResponse = (function ote$app$controller$place_search$__GT_PlaceCompletionsResponse(completions,name){
return (new ote.app.controller.place_search.PlaceCompletionsResponse(completions,name,null,null,null));
});

ote.app.controller.place_search.map__GT_PlaceCompletionsResponse = (function ote$app$controller$place_search$map__GT_PlaceCompletionsResponse(G__51608){
return (new ote.app.controller.place_search.PlaceCompletionsResponse(new cljs.core.Keyword(null,"completions","completions",-190930179).cljs$core$IFn$_invoke$arity$1(G__51608),new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(G__51608),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51608,new cljs.core.Keyword(null,"completions","completions",-190930179),new cljs.core.Keyword(null,"name","name",1843675177))),null));
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
ote.app.controller.place_search.SetMarker = (function (event,__meta,__extmap,__hash){
this.event = event;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.place_search.SetMarker.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.place_search.SetMarker.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51615,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51619 = k51615;
var G__51619__$1 = (((G__51619 instanceof cljs.core.Keyword))?G__51619.fqn:null);
switch (G__51619__$1) {
case "event":
return self__.event;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51615,else__30866__auto__);

}
});

ote.app.controller.place_search.SetMarker.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.place-search.SetMarker{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"event","event",301435442),self__.event],null))], null),self__.__extmap));
});

ote.app.controller.place_search.SetMarker.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51614){
var self__ = this;
var G__51614__$1 = this;
return (new cljs.core.RecordIter((0),G__51614__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"event","event",301435442)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.place_search.SetMarker.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.place_search.SetMarker.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.place_search.SetMarker(self__.event,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.SetMarker.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.place_search.SetMarker.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1448851412 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.place_search.SetMarker.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51616,other51617){
var self__ = this;
var this51616__$1 = this;
return (!((other51617 == null))) && ((this51616__$1.constructor === other51617.constructor)) && (cljs.core._EQ_.call(null,this51616__$1.event,other51617.event)) && (cljs.core._EQ_.call(null,this51616__$1.__extmap,other51617.__extmap));
});

ote.app.controller.place_search.SetMarker.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"event","event",301435442),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.place_search.SetMarker(self__.event,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.place_search.SetMarker.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51614){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51620 = cljs.core.keyword_identical_QMARK_;
var expr__51621 = k__30871__auto__;
if(cljs.core.truth_(pred__51620.call(null,new cljs.core.Keyword(null,"event","event",301435442),expr__51621))){
return (new ote.app.controller.place_search.SetMarker(G__51614,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.place_search.SetMarker(self__.event,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51614),null));
}
});

ote.app.controller.place_search.SetMarker.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"event","event",301435442),self__.event],null))], null),self__.__extmap));
});

ote.app.controller.place_search.SetMarker.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51614){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.place_search.SetMarker(self__.event,G__51614,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.SetMarker.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.place_search.SetMarker.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"event","event",1941966969,null)], null);
});

ote.app.controller.place_search.SetMarker.cljs$lang$type = true;

ote.app.controller.place_search.SetMarker.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.place-search/SetMarker");
});

ote.app.controller.place_search.SetMarker.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.place-search/SetMarker");
});

ote.app.controller.place_search.__GT_SetMarker = (function ote$app$controller$place_search$__GT_SetMarker(event){
return (new ote.app.controller.place_search.SetMarker(event,null,null,null));
});

ote.app.controller.place_search.map__GT_SetMarker = (function ote$app$controller$place_search$map__GT_SetMarker(G__51618){
return (new ote.app.controller.place_search.SetMarker(new cljs.core.Keyword(null,"event","event",301435442).cljs$core$IFn$_invoke$arity$1(G__51618),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51618,new cljs.core.Keyword(null,"event","event",301435442))),null));
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
ote.app.controller.place_search.AddDrawnGeometry = (function (geojson,__meta,__extmap,__hash){
this.geojson = geojson;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.place_search.AddDrawnGeometry.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.place_search.AddDrawnGeometry.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51625,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51629 = k51625;
var G__51629__$1 = (((G__51629 instanceof cljs.core.Keyword))?G__51629.fqn:null);
switch (G__51629__$1) {
case "geojson":
return self__.geojson;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51625,else__30866__auto__);

}
});

ote.app.controller.place_search.AddDrawnGeometry.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.place-search.AddDrawnGeometry{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"geojson","geojson",-719473398),self__.geojson],null))], null),self__.__extmap));
});

ote.app.controller.place_search.AddDrawnGeometry.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51624){
var self__ = this;
var G__51624__$1 = this;
return (new cljs.core.RecordIter((0),G__51624__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"geojson","geojson",-719473398)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.place_search.AddDrawnGeometry.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.place_search.AddDrawnGeometry.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.place_search.AddDrawnGeometry(self__.geojson,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.AddDrawnGeometry.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.place_search.AddDrawnGeometry.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1560279467 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.place_search.AddDrawnGeometry.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51626,other51627){
var self__ = this;
var this51626__$1 = this;
return (!((other51627 == null))) && ((this51626__$1.constructor === other51627.constructor)) && (cljs.core._EQ_.call(null,this51626__$1.geojson,other51627.geojson)) && (cljs.core._EQ_.call(null,this51626__$1.__extmap,other51627.__extmap));
});

ote.app.controller.place_search.AddDrawnGeometry.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"geojson","geojson",-719473398),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.place_search.AddDrawnGeometry(self__.geojson,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.place_search.AddDrawnGeometry.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51624){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51630 = cljs.core.keyword_identical_QMARK_;
var expr__51631 = k__30871__auto__;
if(cljs.core.truth_(pred__51630.call(null,new cljs.core.Keyword(null,"geojson","geojson",-719473398),expr__51631))){
return (new ote.app.controller.place_search.AddDrawnGeometry(G__51624,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.place_search.AddDrawnGeometry(self__.geojson,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51624),null));
}
});

ote.app.controller.place_search.AddDrawnGeometry.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"geojson","geojson",-719473398),self__.geojson],null))], null),self__.__extmap));
});

ote.app.controller.place_search.AddDrawnGeometry.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51624){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.place_search.AddDrawnGeometry(self__.geojson,G__51624,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.AddDrawnGeometry.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.place_search.AddDrawnGeometry.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"geojson","geojson",921058129,null)], null);
});

ote.app.controller.place_search.AddDrawnGeometry.cljs$lang$type = true;

ote.app.controller.place_search.AddDrawnGeometry.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.place-search/AddDrawnGeometry");
});

ote.app.controller.place_search.AddDrawnGeometry.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.place-search/AddDrawnGeometry");
});

ote.app.controller.place_search.__GT_AddDrawnGeometry = (function ote$app$controller$place_search$__GT_AddDrawnGeometry(geojson){
return (new ote.app.controller.place_search.AddDrawnGeometry(geojson,null,null,null));
});

ote.app.controller.place_search.map__GT_AddDrawnGeometry = (function ote$app$controller$place_search$map__GT_AddDrawnGeometry(G__51628){
return (new ote.app.controller.place_search.AddDrawnGeometry(new cljs.core.Keyword(null,"geojson","geojson",-719473398).cljs$core$IFn$_invoke$arity$1(G__51628),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51628,new cljs.core.Keyword(null,"geojson","geojson",-719473398))),null));
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
ote.app.controller.place_search.EditDrawnGeometryName = (function (id,__meta,__extmap,__hash){
this.id = id;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.place_search.EditDrawnGeometryName.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.place_search.EditDrawnGeometryName.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51635,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51639 = k51635;
var G__51639__$1 = (((G__51639 instanceof cljs.core.Keyword))?G__51639.fqn:null);
switch (G__51639__$1) {
case "id":
return self__.id;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51635,else__30866__auto__);

}
});

ote.app.controller.place_search.EditDrawnGeometryName.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.place-search.EditDrawnGeometryName{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"id","id",-1388402092),self__.id],null))], null),self__.__extmap));
});

ote.app.controller.place_search.EditDrawnGeometryName.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51634){
var self__ = this;
var G__51634__$1 = this;
return (new cljs.core.RecordIter((0),G__51634__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"id","id",-1388402092)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.place_search.EditDrawnGeometryName.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.place_search.EditDrawnGeometryName.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.place_search.EditDrawnGeometryName(self__.id,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.EditDrawnGeometryName.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.place_search.EditDrawnGeometryName.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-165092882 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.place_search.EditDrawnGeometryName.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51636,other51637){
var self__ = this;
var this51636__$1 = this;
return (!((other51637 == null))) && ((this51636__$1.constructor === other51637.constructor)) && (cljs.core._EQ_.call(null,this51636__$1.id,other51637.id)) && (cljs.core._EQ_.call(null,this51636__$1.__extmap,other51637.__extmap));
});

ote.app.controller.place_search.EditDrawnGeometryName.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"id","id",-1388402092),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.place_search.EditDrawnGeometryName(self__.id,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.place_search.EditDrawnGeometryName.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51634){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51640 = cljs.core.keyword_identical_QMARK_;
var expr__51641 = k__30871__auto__;
if(cljs.core.truth_(pred__51640.call(null,new cljs.core.Keyword(null,"id","id",-1388402092),expr__51641))){
return (new ote.app.controller.place_search.EditDrawnGeometryName(G__51634,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.place_search.EditDrawnGeometryName(self__.id,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51634),null));
}
});

ote.app.controller.place_search.EditDrawnGeometryName.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"id","id",-1388402092),self__.id],null))], null),self__.__extmap));
});

ote.app.controller.place_search.EditDrawnGeometryName.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51634){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.place_search.EditDrawnGeometryName(self__.id,G__51634,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.EditDrawnGeometryName.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.place_search.EditDrawnGeometryName.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"id","id",252129435,null)], null);
});

ote.app.controller.place_search.EditDrawnGeometryName.cljs$lang$type = true;

ote.app.controller.place_search.EditDrawnGeometryName.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.place-search/EditDrawnGeometryName");
});

ote.app.controller.place_search.EditDrawnGeometryName.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.place-search/EditDrawnGeometryName");
});

ote.app.controller.place_search.__GT_EditDrawnGeometryName = (function ote$app$controller$place_search$__GT_EditDrawnGeometryName(id){
return (new ote.app.controller.place_search.EditDrawnGeometryName(id,null,null,null));
});

ote.app.controller.place_search.map__GT_EditDrawnGeometryName = (function ote$app$controller$place_search$map__GT_EditDrawnGeometryName(G__51638){
return (new ote.app.controller.place_search.EditDrawnGeometryName(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(G__51638),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51638,new cljs.core.Keyword(null,"id","id",-1388402092))),null));
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
ote.app.controller.place_search.SetDrawnGeometryName = (function (id,name,__meta,__extmap,__hash){
this.id = id;
this.name = name;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.place_search.SetDrawnGeometryName.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.place_search.SetDrawnGeometryName.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51645,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51649 = k51645;
var G__51649__$1 = (((G__51649 instanceof cljs.core.Keyword))?G__51649.fqn:null);
switch (G__51649__$1) {
case "id":
return self__.id;

break;
case "name":
return self__.name;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51645,else__30866__auto__);

}
});

ote.app.controller.place_search.SetDrawnGeometryName.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.place-search.SetDrawnGeometryName{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"id","id",-1388402092),self__.id],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"name","name",1843675177),self__.name],null))], null),self__.__extmap));
});

ote.app.controller.place_search.SetDrawnGeometryName.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51644){
var self__ = this;
var G__51644__$1 = this;
return (new cljs.core.RecordIter((0),G__51644__$1,2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"id","id",-1388402092),new cljs.core.Keyword(null,"name","name",1843675177)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.place_search.SetDrawnGeometryName.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.place_search.SetDrawnGeometryName.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.place_search.SetDrawnGeometryName(self__.id,self__.name,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.SetDrawnGeometryName.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (2 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.place_search.SetDrawnGeometryName.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (2111355979 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.place_search.SetDrawnGeometryName.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51646,other51647){
var self__ = this;
var this51646__$1 = this;
return (!((other51647 == null))) && ((this51646__$1.constructor === other51647.constructor)) && (cljs.core._EQ_.call(null,this51646__$1.id,other51647.id)) && (cljs.core._EQ_.call(null,this51646__$1.name,other51647.name)) && (cljs.core._EQ_.call(null,this51646__$1.__extmap,other51647.__extmap));
});

ote.app.controller.place_search.SetDrawnGeometryName.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"name","name",1843675177),null,new cljs.core.Keyword(null,"id","id",-1388402092),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.place_search.SetDrawnGeometryName(self__.id,self__.name,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.place_search.SetDrawnGeometryName.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51644){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51650 = cljs.core.keyword_identical_QMARK_;
var expr__51651 = k__30871__auto__;
if(cljs.core.truth_(pred__51650.call(null,new cljs.core.Keyword(null,"id","id",-1388402092),expr__51651))){
return (new ote.app.controller.place_search.SetDrawnGeometryName(G__51644,self__.name,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__51650.call(null,new cljs.core.Keyword(null,"name","name",1843675177),expr__51651))){
return (new ote.app.controller.place_search.SetDrawnGeometryName(self__.id,G__51644,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.place_search.SetDrawnGeometryName(self__.id,self__.name,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51644),null));
}
}
});

ote.app.controller.place_search.SetDrawnGeometryName.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"id","id",-1388402092),self__.id],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"name","name",1843675177),self__.name],null))], null),self__.__extmap));
});

ote.app.controller.place_search.SetDrawnGeometryName.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51644){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.place_search.SetDrawnGeometryName(self__.id,self__.name,G__51644,self__.__extmap,self__.__hash));
});

ote.app.controller.place_search.SetDrawnGeometryName.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.place_search.SetDrawnGeometryName.getBasis = (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"id","id",252129435,null),new cljs.core.Symbol(null,"name","name",-810760592,null)], null);
});

ote.app.controller.place_search.SetDrawnGeometryName.cljs$lang$type = true;

ote.app.controller.place_search.SetDrawnGeometryName.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.place-search/SetDrawnGeometryName");
});

ote.app.controller.place_search.SetDrawnGeometryName.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.place-search/SetDrawnGeometryName");
});

ote.app.controller.place_search.__GT_SetDrawnGeometryName = (function ote$app$controller$place_search$__GT_SetDrawnGeometryName(id,name){
return (new ote.app.controller.place_search.SetDrawnGeometryName(id,name,null,null,null));
});

ote.app.controller.place_search.map__GT_SetDrawnGeometryName = (function ote$app$controller$place_search$map__GT_SetDrawnGeometryName(G__51648){
return (new ote.app.controller.place_search.SetDrawnGeometryName(new cljs.core.Keyword(null,"id","id",-1388402092).cljs$core$IFn$_invoke$arity$1(G__51648),new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(G__51648),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51648,new cljs.core.Keyword(null,"id","id",-1388402092),new cljs.core.Keyword(null,"name","name",1843675177))),null));
});

/**
 * Return app with a new place added.
 */
ote.app.controller.place_search.add_place = (function ote$app$controller$place_search$add_place(app,place,geojson){
return cljs.core.update_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.Keyword(null,"results","results",-1134170113)], null),(function (p1__51654_SHARP_){
return cljs.core.conj.call(null,(function (){var or__30175__auto__ = p1__51654_SHARP_;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return cljs.core.PersistentVector.EMPTY;
}
})(),new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"place","place",-819689466),place,new cljs.core.Keyword(null,"geojson","geojson",-719473398),geojson], null));
}));
});
ote.app.controller.place_search.update_place_by_id = (function ote$app$controller$place_search$update_place_by_id(var_args){
var args__31459__auto__ = [];
var len__31452__auto___51660 = arguments.length;
var i__31453__auto___51661 = (0);
while(true){
if((i__31453__auto___51661 < len__31452__auto___51660)){
args__31459__auto__.push((arguments[i__31453__auto___51661]));

var G__51662 = (i__31453__auto___51661 + (1));
i__31453__auto___51661 = G__51662;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((3) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((3)),(0),null)):null);
return ote.app.controller.place_search.update_place_by_id.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__31460__auto__);
});

ote.app.controller.place_search.update_place_by_id.cljs$core$IFn$_invoke$arity$variadic = (function (app,id,update_fn,args){
return cljs.core.update_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.Keyword(null,"results","results",-1134170113)], null),(function (results){
return cljs.core.mapv.call(null,(function (p1__51655_SHARP_){
if(cljs.core._EQ_.call(null,cljs.core.get_in.call(null,p1__51655_SHARP_,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place","place",-819689466),new cljs.core.Keyword("ote.db.places","id","ote.db.places/id",772786118)], null)),id)){
return cljs.core.update.call(null,p1__51655_SHARP_,new cljs.core.Keyword(null,"place","place",-819689466),(function (p){
return cljs.core.apply.call(null,update_fn,p,args);
}));
} else {
return p1__51655_SHARP_;
}
}),results);
}));
});

ote.app.controller.place_search.update_place_by_id.cljs$lang$maxFixedArity = (3);

ote.app.controller.place_search.update_place_by_id.cljs$lang$applyTo = (function (seq51656){
var G__51657 = cljs.core.first.call(null,seq51656);
var seq51656__$1 = cljs.core.next.call(null,seq51656);
var G__51658 = cljs.core.first.call(null,seq51656__$1);
var seq51656__$2 = cljs.core.next.call(null,seq51656__$1);
var G__51659 = cljs.core.first.call(null,seq51656__$2);
var seq51656__$3 = cljs.core.next.call(null,seq51656__$2);
return ote.app.controller.place_search.update_place_by_id.cljs$core$IFn$_invoke$arity$variadic(G__51657,G__51658,G__51659,seq51656__$3);
});

ote.app.controller.place_search.SetPlaceName.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.place_search.SetPlaceName.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51666,app){
var map__51667 = p__51666;
var map__51667__$1 = ((((!((map__51667 == null)))?((((map__51667.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51667.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51667):map__51667);
var name = cljs.core.get.call(null,map__51667__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var map__51669 = this;
var map__51669__$1 = ((((!((map__51669 == null)))?((((map__51669.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51669.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51669):map__51669);
var name__$1 = cljs.core.get.call(null,map__51669__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var app__$1 = cljs.core.assoc_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.Keyword(null,"name","name",1843675177)], null),name__$1);
if((cljs.core.count.call(null,name__$1) >= (2))){
ote.communication.get_BANG_.call(null,["place-completions/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(name__$1)].join(''),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-success","on-success",1786904109),tuck.core.send_async_BANG_.call(null,ote.app.controller.place_search.__GT_PlaceCompletionsResponse,name__$1)], null));
} else {
}

return app__$1;
});

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.place_search.PlaceCompletionsResponse.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51671,app){
var map__51672 = p__51671;
var map__51672__$1 = ((((!((map__51672 == null)))?((((map__51672.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51672.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51672):map__51672);
var completions = cljs.core.get.call(null,map__51672__$1,new cljs.core.Keyword(null,"completions","completions",-190930179));
var name = cljs.core.get.call(null,map__51672__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var map__51674 = this;
var map__51674__$1 = ((((!((map__51674 == null)))?((((map__51674.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51674.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51674):map__51674);
var completions__$1 = cljs.core.get.call(null,map__51674__$1,new cljs.core.Keyword(null,"completions","completions",-190930179));
var name__$1 = cljs.core.get.call(null,map__51674__$1,new cljs.core.Keyword(null,"name","name",1843675177));
if(!(cljs.core._EQ_.call(null,name__$1,cljs.core.get_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.Keyword(null,"name","name",1843675177)], null))))){
return app;
} else {
return cljs.core.assoc_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.Keyword(null,"completions","completions",-190930179)], null),(function (){var name_lower = clojure.string.lower_case.call(null,name__$1);
return cljs.core.sort_by.call(null,((function (name_lower,map__51674,map__51674__$1,completions__$1,name__$1,map__51672,map__51672__$1,completions,name){
return (function (p1__51663_SHARP_){
return clojure.string.index_of.call(null,clojure.string.lower_case.call(null,new cljs.core.Keyword("ote.db.places","namefin","ote.db.places/namefin",204883439).cljs$core$IFn$_invoke$arity$1(p1__51663_SHARP_)),name_lower);
});})(name_lower,map__51674,map__51674__$1,completions__$1,name__$1,map__51672,map__51672__$1,completions,name))
,completions__$1);
})());
}
});

ote.app.controller.place_search.AddPlace.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.place_search.AddPlace.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51676,app){
var map__51677 = p__51676;
var map__51677__$1 = ((((!((map__51677 == null)))?((((map__51677.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51677.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51677):map__51677);
var id = cljs.core.get.call(null,map__51677__$1,new cljs.core.Keyword(null,"id","id",-1388402092));
var map__51679 = this;
var map__51679__$1 = ((((!((map__51679 == null)))?((((map__51679.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51679.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51679):map__51679);
var id__$1 = cljs.core.get.call(null,map__51679__$1,new cljs.core.Keyword(null,"id","id",-1388402092));
console.log("ADD PLACE:",id__$1);

if(cljs.core.truth_(cljs.core.some.call(null,((function (map__51679,map__51679__$1,id__$1,map__51677,map__51677__$1,id){
return (function (p1__51664_SHARP_){
return cljs.core._EQ_.call(null,id__$1,new cljs.core.Keyword("ote.db.places","id","ote.db.places/id",772786118).cljs$core$IFn$_invoke$arity$1(new cljs.core.Keyword(null,"place","place",-819689466).cljs$core$IFn$_invoke$arity$1(p1__51664_SHARP_)));
});})(map__51679,map__51679__$1,id__$1,map__51677,map__51677__$1,id))
,cljs.core.get_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.Keyword(null,"results","results",-1134170113)], null))))){
return app;
} else {
var temp__5288__auto__ = cljs.core.some.call(null,((function (map__51679,map__51679__$1,id__$1,map__51677,map__51677__$1,id){
return (function (p1__51665_SHARP_){
if(cljs.core._EQ_.call(null,id__$1,new cljs.core.Keyword("ote.db.places","id","ote.db.places/id",772786118).cljs$core$IFn$_invoke$arity$1(p1__51665_SHARP_))){
return p1__51665_SHARP_;
} else {
return null;
}
});})(map__51679,map__51679__$1,id__$1,map__51677,map__51677__$1,id))
,cljs.core.get_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.Keyword(null,"completions","completions",-190930179)], null)));
if(cljs.core.truth_(temp__5288__auto__)){
var place = temp__5288__auto__;
ote.communication.get_BANG_.call(null,["place/",cljs.core.str.cljs$core$IFn$_invoke$arity$1(id__$1)].join(''),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-success","on-success",1786904109),tuck.core.send_async_BANG_.call(null,ote.app.controller.place_search.__GT_FetchPlaceResponse,place)], null));

return cljs.core.assoc_in.call(null,cljs.core.assoc_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.Keyword(null,"name","name",1843675177)], null),""),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.Keyword(null,"completions","completions",-190930179)], null),null);
} else {
return app;
}
}
});

ote.app.controller.place_search.FetchPlaceResponse.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.place_search.FetchPlaceResponse.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51681,app){
var map__51682 = p__51681;
var map__51682__$1 = ((((!((map__51682 == null)))?((((map__51682.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51682.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51682):map__51682);
var response = cljs.core.get.call(null,map__51682__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
var place = cljs.core.get.call(null,map__51682__$1,new cljs.core.Keyword(null,"place","place",-819689466));
var map__51684 = this;
var map__51684__$1 = ((((!((map__51684 == null)))?((((map__51684.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51684.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51684):map__51684);
var response__$1 = cljs.core.get.call(null,map__51684__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
var place__$1 = cljs.core.get.call(null,map__51684__$1,new cljs.core.Keyword(null,"place","place",-819689466));
return ote.app.controller.place_search.add_place.call(null,app,place__$1,response__$1);
});

ote.app.controller.place_search.RemovePlaceById.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.place_search.RemovePlaceById.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51686,app){
var map__51687 = p__51686;
var map__51687__$1 = ((((!((map__51687 == null)))?((((map__51687.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51687.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51687):map__51687);
var id = cljs.core.get.call(null,map__51687__$1,new cljs.core.Keyword(null,"id","id",-1388402092));
var map__51689 = this;
var map__51689__$1 = ((((!((map__51689 == null)))?((((map__51689.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51689.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51689):map__51689);
var id__$1 = cljs.core.get.call(null,map__51689__$1,new cljs.core.Keyword(null,"id","id",-1388402092));
return cljs.core.update_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.Keyword(null,"results","results",-1134170113)], null),((function (map__51689,map__51689__$1,id__$1,map__51687,map__51687__$1,id){
return (function (results){
return cljs.core.filterv.call(null,cljs.core.comp.call(null,cljs.core.partial.call(null,cljs.core.not_EQ_,id__$1),new cljs.core.Keyword("ote.db.places","id","ote.db.places/id",772786118),new cljs.core.Keyword(null,"place","place",-819689466)),results);
});})(map__51689,map__51689__$1,id__$1,map__51687,map__51687__$1,id))
);
});

ote.app.controller.place_search.SetMarker.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.place_search.SetMarker.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51691,app){
var map__51692 = p__51691;
var map__51692__$1 = ((((!((map__51692 == null)))?((((map__51692.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51692.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51692):map__51692);
var event = cljs.core.get.call(null,map__51692__$1,new cljs.core.Keyword(null,"event","event",301435442));
var map__51694 = this;
var map__51694__$1 = ((((!((map__51694 == null)))?((((map__51694.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51694.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51694):map__51694);
var event__$1 = cljs.core.get.call(null,map__51694__$1,new cljs.core.Keyword(null,"event","event",301435442));
var lat = event__$1.latlng.lat;
var lng = event__$1.latlng.lng;
return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"coordinates","coordinates",-1225332668),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [lat,lng], null));
});

ote.app.controller.place_search.AddDrawnGeometry.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.place_search.AddDrawnGeometry.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51696,p__51697){
var map__51698 = p__51696;
var map__51698__$1 = ((((!((map__51698 == null)))?((((map__51698.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51698.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51698):map__51698);
var geojson = cljs.core.get.call(null,map__51698__$1,new cljs.core.Keyword(null,"geojson","geojson",-719473398));
var map__51699 = p__51697;
var map__51699__$1 = ((((!((map__51699 == null)))?((((map__51699.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51699.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51699):map__51699);
var app = map__51699__$1;
var drawn_geometry_idx = cljs.core.get.call(null,map__51699__$1,new cljs.core.Keyword(null,"drawn-geometry-idx","drawn-geometry-idx",-1714825207));
var map__51702 = this;
var map__51702__$1 = ((((!((map__51702 == null)))?((((map__51702.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51702.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51702):map__51702);
var geojson__$1 = cljs.core.get.call(null,map__51702__$1,new cljs.core.Keyword(null,"geojson","geojson",-719473398));
var type = (geojson__$1["geometry"]["type"]);
return ote.app.controller.place_search.add_place.call(null,cljs.core.update.call(null,app,new cljs.core.Keyword(null,"drawn-geometry-idx","drawn-geometry-idx",-1714825207),cljs.core.fnil.call(null,cljs.core.inc,(1))),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword("ote.db.places","namefin","ote.db.places/namefin",204883439),[cljs.core.str.cljs$core$IFn$_invoke$arity$1(type)," ",cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var or__30175__auto__ = drawn_geometry_idx;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return (1);
}
})())].join(''),new cljs.core.Keyword("ote.db.places","type","ote.db.places/type",-975773958),"drawn",new cljs.core.Keyword("ote.db.places","id","ote.db.places/id",772786118),["drawn",cljs.core.str.cljs$core$IFn$_invoke$arity$1((function (){var or__30175__auto__ = drawn_geometry_idx;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return (1);
}
})())].join('')], null),geojson__$1);
});

ote.app.controller.place_search.EditDrawnGeometryName.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.place_search.EditDrawnGeometryName.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51704,app){
var map__51705 = p__51704;
var map__51705__$1 = ((((!((map__51705 == null)))?((((map__51705.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51705.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51705):map__51705);
var id = cljs.core.get.call(null,map__51705__$1,new cljs.core.Keyword(null,"id","id",-1388402092));
var map__51707 = this;
var map__51707__$1 = ((((!((map__51707 == null)))?((((map__51707.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51707.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51707):map__51707);
var id__$1 = cljs.core.get.call(null,map__51707__$1,new cljs.core.Keyword(null,"id","id",-1388402092));
taoensso.timbre._log_BANG_.call(null,taoensso.timbre._STAR_config_STAR_,new cljs.core.Keyword(null,"info","info",-317069002),"ote.app.controller.place-search","/private/var/folders/3n/rc2vbw0x0_7gr791s82lsh0mfq1hbd/T/form-init3651905751378429726.clj",112,new cljs.core.Keyword(null,"p","p",151049309),new cljs.core.Keyword(null,"auto","auto",-566279492),(new cljs.core.Delay(((function (map__51707,map__51707__$1,id__$1,map__51705,map__51705__$1,id){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["Edit name of ",id__$1], null);
});})(map__51707,map__51707__$1,id__$1,map__51705,map__51705__$1,id))
,null)),null,1160420325);

return ote.app.controller.place_search.update_place_by_id.call(null,app,id__$1,cljs.core.update,new cljs.core.Keyword(null,"editing?","editing?",1646440800),cljs.core.not);
});

ote.app.controller.place_search.SetDrawnGeometryName.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.place_search.SetDrawnGeometryName.prototype.tuck$core$Event$process_event$arity$2 = (function (p__51709,app){
var map__51710 = p__51709;
var map__51710__$1 = ((((!((map__51710 == null)))?((((map__51710.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51710.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51710):map__51710);
var id = cljs.core.get.call(null,map__51710__$1,new cljs.core.Keyword(null,"id","id",-1388402092));
var name = cljs.core.get.call(null,map__51710__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var map__51712 = this;
var map__51712__$1 = ((((!((map__51712 == null)))?((((map__51712.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51712.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51712):map__51712);
var id__$1 = cljs.core.get.call(null,map__51712__$1,new cljs.core.Keyword(null,"id","id",-1388402092));
var name__$1 = cljs.core.get.call(null,map__51712__$1,new cljs.core.Keyword(null,"name","name",1843675177));
return ote.app.controller.place_search.update_place_by_id.call(null,app,id__$1,cljs.core.assoc,new cljs.core.Keyword("ote.db.places","namefin","ote.db.places/namefin",204883439),name__$1);
});
/**
 * Gets a place search app model and returns place references from it.
 *   Place references are sent to the server instead of sending the geometries.
 *   Hand drawn geometries are sent with their geometry.
 */
ote.app.controller.place_search.place_references = (function ote$app$controller$place_search$place_references(app){
return cljs.core.mapv.call(null,(function (p__51714){
var map__51715 = p__51714;
var map__51715__$1 = ((((!((map__51715 == null)))?((((map__51715.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51715.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51715):map__51715);
var geojson = cljs.core.get.call(null,map__51715__$1,new cljs.core.Keyword(null,"geojson","geojson",-719473398));
var place = cljs.core.get.call(null,map__51715__$1,new cljs.core.Keyword(null,"place","place",-819689466));
var G__51717 = new cljs.core.Keyword("ote.db.places","type","ote.db.places/type",-975773958).cljs$core$IFn$_invoke$arity$1(place);
switch (G__51717) {
case "drawn":
return cljs.core.assoc.call(null,place,new cljs.core.Keyword(null,"geojson","geojson",-719473398),JSON.stringify((geojson["geometry"])));

break;
case "stored":
return cljs.core.dissoc.call(null,place,new cljs.core.Keyword(null,"geojson","geojson",-719473398));

break;
default:
return place;

}
}),cljs.core.get_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.Keyword(null,"results","results",-1134170113)], null)));
});
/**
 * Turn an operation area from the backend to a format required by the UI.
 */
ote.app.controller.place_search.operation_area_to_places = (function ote$app$controller$place_search$operation_area_to_places(operation_areas){
return new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"place-search","place-search",-497916414),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"results","results",-1134170113),cljs.core.mapv.call(null,(function (p__51720){
var map__51721 = p__51720;
var map__51721__$1 = ((((!((map__51721 == null)))?((((map__51721.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51721.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51721):map__51721);
var operation_area = map__51721__$1;
var description = cljs.core.get.call(null,map__51721__$1,new cljs.core.Keyword("ote.db.transport-service","description","ote.db.transport-service/description",-1993573841));
var id = cljs.core.get.call(null,map__51721__$1,new cljs.core.Keyword("ote.db.transport-service","id","ote.db.transport-service/id",192939397));
var primary_QMARK_ = cljs.core.get.call(null,map__51721__$1,new cljs.core.Keyword("ote.db.transport-service","primary?","ote.db.transport-service/primary?",-801581077));
var location_geojson = cljs.core.get.call(null,map__51721__$1,new cljs.core.Keyword("ote.db.transport-service","location-geojson","ote.db.transport-service/location-geojson",-1883553974));
return new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"place","place",-819689466),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword("ote.db.places","namefin","ote.db.places/namefin",204883439),cljs.core.some.call(null,((function (map__51721,map__51721__$1,operation_area,description,id,primary_QMARK_,location_geojson){
return (function (p1__51719_SHARP_){
if(cljs.core._EQ_.call(null,"FI",new cljs.core.Keyword("ote.db.transport-service","lang","ote.db.transport-service/lang",902970079).cljs$core$IFn$_invoke$arity$1(p1__51719_SHARP_))){
return new cljs.core.Keyword("ote.db.transport-service","text","ote.db.transport-service/text",134073550).cljs$core$IFn$_invoke$arity$1(p1__51719_SHARP_);
} else {
return null;
}
});})(map__51721,map__51721__$1,operation_area,description,id,primary_QMARK_,location_geojson))
,description),new cljs.core.Keyword("ote.db.places","id","ote.db.places/id",772786118),id,new cljs.core.Keyword("ote.db.places","type","ote.db.places/type",-975773958),"stored"], null),new cljs.core.Keyword(null,"geojson","geojson",-719473398),JSON.parse(location_geojson)], null);
}),operation_areas)], null)], null);
});

//# sourceMappingURL=place_search.js.map?rel=1510137292846
