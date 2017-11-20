// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.app.controller.viewer');
goog.require('cljs.core');
goog.require('tuck.core');
goog.require('ote.communication');
goog.require('taoensso.timbre');

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
ote.app.controller.viewer.StartViewer = (function (__meta,__extmap,__hash){
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.viewer.StartViewer.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.viewer.StartViewer.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52302,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52306 = k52302;
switch (G__52306) {
default:
return cljs.core.get.call(null,self__.__extmap,k52302,else__30866__auto__);

}
});

ote.app.controller.viewer.StartViewer.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.viewer.StartViewer{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.viewer.StartViewer.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52301){
var self__ = this;
var G__52301__$1 = this;
return (new cljs.core.RecordIter((0),G__52301__$1,0,cljs.core.PersistentVector.EMPTY,(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.viewer.StartViewer.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.viewer.StartViewer.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.viewer.StartViewer(self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.viewer.StartViewer.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (0 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.viewer.StartViewer.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (501996771 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.viewer.StartViewer.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52303,other52304){
var self__ = this;
var this52303__$1 = this;
return (!((other52304 == null))) && ((this52303__$1.constructor === other52304.constructor)) && (cljs.core._EQ_.call(null,this52303__$1.__extmap,other52304.__extmap));
});

ote.app.controller.viewer.StartViewer.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.EMPTY,k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.viewer.StartViewer(self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.viewer.StartViewer.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52301){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52307 = cljs.core.keyword_identical_QMARK_;
var expr__52308 = k__30871__auto__;
return (new ote.app.controller.viewer.StartViewer(self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52301),null));
});

ote.app.controller.viewer.StartViewer.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.viewer.StartViewer.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52301){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.viewer.StartViewer(G__52301,self__.__extmap,self__.__hash));
});

ote.app.controller.viewer.StartViewer.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.viewer.StartViewer.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

ote.app.controller.viewer.StartViewer.cljs$lang$type = true;

ote.app.controller.viewer.StartViewer.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.viewer/StartViewer");
});

ote.app.controller.viewer.StartViewer.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.viewer/StartViewer");
});

ote.app.controller.viewer.__GT_StartViewer = (function ote$app$controller$viewer$__GT_StartViewer(){
return (new ote.app.controller.viewer.StartViewer(null,null,null));
});

ote.app.controller.viewer.map__GT_StartViewer = (function ote$app$controller$viewer$map__GT_StartViewer(G__52305){
return (new ote.app.controller.viewer.StartViewer(null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52305)),null));
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
ote.app.controller.viewer.ResourceFetched = (function (response,__meta,__extmap,__hash){
this.response = response;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.viewer.ResourceFetched.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.viewer.ResourceFetched.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52312,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52316 = k52312;
var G__52316__$1 = (((G__52316 instanceof cljs.core.Keyword))?G__52316.fqn:null);
switch (G__52316__$1) {
case "response":
return self__.response;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k52312,else__30866__auto__);

}
});

ote.app.controller.viewer.ResourceFetched.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.viewer.ResourceFetched{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.viewer.ResourceFetched.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52311){
var self__ = this;
var G__52311__$1 = this;
return (new cljs.core.RecordIter((0),G__52311__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"response","response",-1068424192)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.viewer.ResourceFetched.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.viewer.ResourceFetched.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.viewer.ResourceFetched(self__.response,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.viewer.ResourceFetched.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.viewer.ResourceFetched.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-1612641116 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.viewer.ResourceFetched.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52313,other52314){
var self__ = this;
var this52313__$1 = this;
return (!((other52314 == null))) && ((this52313__$1.constructor === other52314.constructor)) && (cljs.core._EQ_.call(null,this52313__$1.response,other52314.response)) && (cljs.core._EQ_.call(null,this52313__$1.__extmap,other52314.__extmap));
});

ote.app.controller.viewer.ResourceFetched.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"response","response",-1068424192),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.viewer.ResourceFetched(self__.response,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.viewer.ResourceFetched.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52311){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52317 = cljs.core.keyword_identical_QMARK_;
var expr__52318 = k__30871__auto__;
if(cljs.core.truth_(pred__52317.call(null,new cljs.core.Keyword(null,"response","response",-1068424192),expr__52318))){
return (new ote.app.controller.viewer.ResourceFetched(G__52311,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.viewer.ResourceFetched(self__.response,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52311),null));
}
});

ote.app.controller.viewer.ResourceFetched.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.viewer.ResourceFetched.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52311){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.viewer.ResourceFetched(self__.response,G__52311,self__.__extmap,self__.__hash));
});

ote.app.controller.viewer.ResourceFetched.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.viewer.ResourceFetched.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"response","response",572107335,null)], null);
});

ote.app.controller.viewer.ResourceFetched.cljs$lang$type = true;

ote.app.controller.viewer.ResourceFetched.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.viewer/ResourceFetched");
});

ote.app.controller.viewer.ResourceFetched.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.viewer/ResourceFetched");
});

ote.app.controller.viewer.__GT_ResourceFetched = (function ote$app$controller$viewer$__GT_ResourceFetched(response){
return (new ote.app.controller.viewer.ResourceFetched(response,null,null,null));
});

ote.app.controller.viewer.map__GT_ResourceFetched = (function ote$app$controller$viewer$map__GT_ResourceFetched(G__52315){
return (new ote.app.controller.viewer.ResourceFetched(new cljs.core.Keyword(null,"response","response",-1068424192).cljs$core$IFn$_invoke$arity$1(G__52315),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52315,new cljs.core.Keyword(null,"response","response",-1068424192))),null));
});

ote.app.controller.viewer.StartViewer.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.viewer.StartViewer.prototype.tuck$core$Event$process_event$arity$2 = (function (_,app){
var ___$1 = this;
var url = document.getElementById("nap_viewer").getAttribute("data-resource-url");
ote.communication.get_BANG_.call(null,"viewer",new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"params","params",710516235),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"url","url",276297046),url], null),new cljs.core.Keyword(null,"on-success","on-success",1786904109),tuck.core.send_async_BANG_.call(null,ote.app.controller.viewer.__GT_ResourceFetched),new cljs.core.Keyword(null,"response-format","response-format",1664465322),new cljs.core.Keyword(null,"json","json",1279968570)], null));

return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"url","url",276297046),url,new cljs.core.Keyword(null,"loading?","loading?",1905707049),true);
});

ote.app.controller.viewer.ResourceFetched.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.viewer.ResourceFetched.prototype.tuck$core$Event$process_event$arity$2 = (function (p__52321,app){
var map__52322 = p__52321;
var map__52322__$1 = ((((!((map__52322 == null)))?((((map__52322.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52322.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52322):map__52322);
var response = cljs.core.get.call(null,map__52322__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
var map__52324 = this;
var map__52324__$1 = ((((!((map__52324 == null)))?((((map__52324.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52324.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52324):map__52324);
var response__$1 = cljs.core.get.call(null,map__52324__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"resource","resource",251898836),response__$1,new cljs.core.Keyword(null,"geojson","geojson",-719473398),cljs.core.clj__GT_js.call(null,response__$1),new cljs.core.Keyword(null,"loading?","loading?",1905707049),false);
});

//# sourceMappingURL=viewer.js.map?rel=1510137295143
