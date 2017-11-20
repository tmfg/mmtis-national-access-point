// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.app.controller.front_page');
goog.require('cljs.core');
goog.require('tuck.core');
goog.require('ote.communication');
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
ote.app.controller.front_page.ChangePage = (function (given_page,__meta,__extmap,__hash){
this.given_page = given_page;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.front_page.ChangePage.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.front_page.ChangePage.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52372,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52376 = k52372;
var G__52376__$1 = (((G__52376 instanceof cljs.core.Keyword))?G__52376.fqn:null);
switch (G__52376__$1) {
case "given-page":
return self__.given_page;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k52372,else__30866__auto__);

}
});

ote.app.controller.front_page.ChangePage.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.front-page.ChangePage{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"given-page","given-page",-2011533217),self__.given_page],null))], null),self__.__extmap));
});

ote.app.controller.front_page.ChangePage.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52371){
var self__ = this;
var G__52371__$1 = this;
return (new cljs.core.RecordIter((0),G__52371__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"given-page","given-page",-2011533217)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.front_page.ChangePage.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.front_page.ChangePage.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.front_page.ChangePage(self__.given_page,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.front_page.ChangePage.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.front_page.ChangePage.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1329437258 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.front_page.ChangePage.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52373,other52374){
var self__ = this;
var this52373__$1 = this;
return (!((other52374 == null))) && ((this52373__$1.constructor === other52374.constructor)) && (cljs.core._EQ_.call(null,this52373__$1.given_page,other52374.given_page)) && (cljs.core._EQ_.call(null,this52373__$1.__extmap,other52374.__extmap));
});

ote.app.controller.front_page.ChangePage.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"given-page","given-page",-2011533217),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.front_page.ChangePage(self__.given_page,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.front_page.ChangePage.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52371){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52377 = cljs.core.keyword_identical_QMARK_;
var expr__52378 = k__30871__auto__;
if(cljs.core.truth_(pred__52377.call(null,new cljs.core.Keyword(null,"given-page","given-page",-2011533217),expr__52378))){
return (new ote.app.controller.front_page.ChangePage(G__52371,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.front_page.ChangePage(self__.given_page,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52371),null));
}
});

ote.app.controller.front_page.ChangePage.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"given-page","given-page",-2011533217),self__.given_page],null))], null),self__.__extmap));
});

ote.app.controller.front_page.ChangePage.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52371){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.front_page.ChangePage(self__.given_page,G__52371,self__.__extmap,self__.__hash));
});

ote.app.controller.front_page.ChangePage.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.front_page.ChangePage.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"given-page","given-page",-371001690,null)], null);
});

ote.app.controller.front_page.ChangePage.cljs$lang$type = true;

ote.app.controller.front_page.ChangePage.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.front-page/ChangePage");
});

ote.app.controller.front_page.ChangePage.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.front-page/ChangePage");
});

ote.app.controller.front_page.__GT_ChangePage = (function ote$app$controller$front_page$__GT_ChangePage(given_page){
return (new ote.app.controller.front_page.ChangePage(given_page,null,null,null));
});

ote.app.controller.front_page.map__GT_ChangePage = (function ote$app$controller$front_page$map__GT_ChangePage(G__52375){
return (new ote.app.controller.front_page.ChangePage(new cljs.core.Keyword(null,"given-page","given-page",-2011533217).cljs$core$IFn$_invoke$arity$1(G__52375),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52375,new cljs.core.Keyword(null,"given-page","given-page",-2011533217))),null));
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
ote.app.controller.front_page.OpenUserMenu = (function (__meta,__extmap,__hash){
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.front_page.OpenUserMenu.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.front_page.OpenUserMenu.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52382,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52386 = k52382;
switch (G__52386) {
default:
return cljs.core.get.call(null,self__.__extmap,k52382,else__30866__auto__);

}
});

ote.app.controller.front_page.OpenUserMenu.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.front-page.OpenUserMenu{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.front_page.OpenUserMenu.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52381){
var self__ = this;
var G__52381__$1 = this;
return (new cljs.core.RecordIter((0),G__52381__$1,0,cljs.core.PersistentVector.EMPTY,(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.front_page.OpenUserMenu.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.front_page.OpenUserMenu.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.front_page.OpenUserMenu(self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.front_page.OpenUserMenu.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (0 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.front_page.OpenUserMenu.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-1003296490 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.front_page.OpenUserMenu.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52383,other52384){
var self__ = this;
var this52383__$1 = this;
return (!((other52384 == null))) && ((this52383__$1.constructor === other52384.constructor)) && (cljs.core._EQ_.call(null,this52383__$1.__extmap,other52384.__extmap));
});

ote.app.controller.front_page.OpenUserMenu.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.EMPTY,k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.front_page.OpenUserMenu(self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.front_page.OpenUserMenu.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52381){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52387 = cljs.core.keyword_identical_QMARK_;
var expr__52388 = k__30871__auto__;
return (new ote.app.controller.front_page.OpenUserMenu(self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52381),null));
});

ote.app.controller.front_page.OpenUserMenu.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.front_page.OpenUserMenu.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52381){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.front_page.OpenUserMenu(G__52381,self__.__extmap,self__.__hash));
});

ote.app.controller.front_page.OpenUserMenu.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.front_page.OpenUserMenu.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

ote.app.controller.front_page.OpenUserMenu.cljs$lang$type = true;

ote.app.controller.front_page.OpenUserMenu.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.front-page/OpenUserMenu");
});

ote.app.controller.front_page.OpenUserMenu.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.front-page/OpenUserMenu");
});

ote.app.controller.front_page.__GT_OpenUserMenu = (function ote$app$controller$front_page$__GT_OpenUserMenu(){
return (new ote.app.controller.front_page.OpenUserMenu(null,null,null));
});

ote.app.controller.front_page.map__GT_OpenUserMenu = (function ote$app$controller$front_page$map__GT_OpenUserMenu(G__52385){
return (new ote.app.controller.front_page.OpenUserMenu(null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52385)),null));
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
ote.app.controller.front_page.ToggleDebugState = (function (__meta,__extmap,__hash){
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.front_page.ToggleDebugState.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.front_page.ToggleDebugState.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52392,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52396 = k52392;
switch (G__52396) {
default:
return cljs.core.get.call(null,self__.__extmap,k52392,else__30866__auto__);

}
});

ote.app.controller.front_page.ToggleDebugState.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.front-page.ToggleDebugState{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.front_page.ToggleDebugState.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52391){
var self__ = this;
var G__52391__$1 = this;
return (new cljs.core.RecordIter((0),G__52391__$1,0,cljs.core.PersistentVector.EMPTY,(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.front_page.ToggleDebugState.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.front_page.ToggleDebugState.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.front_page.ToggleDebugState(self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.front_page.ToggleDebugState.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (0 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.front_page.ToggleDebugState.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (638729321 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.front_page.ToggleDebugState.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52393,other52394){
var self__ = this;
var this52393__$1 = this;
return (!((other52394 == null))) && ((this52393__$1.constructor === other52394.constructor)) && (cljs.core._EQ_.call(null,this52393__$1.__extmap,other52394.__extmap));
});

ote.app.controller.front_page.ToggleDebugState.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.EMPTY,k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.front_page.ToggleDebugState(self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.front_page.ToggleDebugState.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52391){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52397 = cljs.core.keyword_identical_QMARK_;
var expr__52398 = k__30871__auto__;
return (new ote.app.controller.front_page.ToggleDebugState(self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52391),null));
});

ote.app.controller.front_page.ToggleDebugState.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.front_page.ToggleDebugState.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52391){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.front_page.ToggleDebugState(G__52391,self__.__extmap,self__.__hash));
});

ote.app.controller.front_page.ToggleDebugState.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.front_page.ToggleDebugState.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

ote.app.controller.front_page.ToggleDebugState.cljs$lang$type = true;

ote.app.controller.front_page.ToggleDebugState.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.front-page/ToggleDebugState");
});

ote.app.controller.front_page.ToggleDebugState.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.front-page/ToggleDebugState");
});

ote.app.controller.front_page.__GT_ToggleDebugState = (function ote$app$controller$front_page$__GT_ToggleDebugState(){
return (new ote.app.controller.front_page.ToggleDebugState(null,null,null));
});

ote.app.controller.front_page.map__GT_ToggleDebugState = (function ote$app$controller$front_page$map__GT_ToggleDebugState(G__52395){
return (new ote.app.controller.front_page.ToggleDebugState(null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52395)),null));
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
ote.app.controller.front_page.GetTransportOperator = (function (__meta,__extmap,__hash){
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.front_page.GetTransportOperator.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.front_page.GetTransportOperator.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52402,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52406 = k52402;
switch (G__52406) {
default:
return cljs.core.get.call(null,self__.__extmap,k52402,else__30866__auto__);

}
});

ote.app.controller.front_page.GetTransportOperator.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.front-page.GetTransportOperator{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.front_page.GetTransportOperator.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52401){
var self__ = this;
var G__52401__$1 = this;
return (new cljs.core.RecordIter((0),G__52401__$1,0,cljs.core.PersistentVector.EMPTY,(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.front_page.GetTransportOperator.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.front_page.GetTransportOperator.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.front_page.GetTransportOperator(self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.front_page.GetTransportOperator.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (0 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.front_page.GetTransportOperator.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (119286130 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.front_page.GetTransportOperator.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52403,other52404){
var self__ = this;
var this52403__$1 = this;
return (!((other52404 == null))) && ((this52403__$1.constructor === other52404.constructor)) && (cljs.core._EQ_.call(null,this52403__$1.__extmap,other52404.__extmap));
});

ote.app.controller.front_page.GetTransportOperator.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.EMPTY,k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.front_page.GetTransportOperator(self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.front_page.GetTransportOperator.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52401){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52407 = cljs.core.keyword_identical_QMARK_;
var expr__52408 = k__30871__auto__;
return (new ote.app.controller.front_page.GetTransportOperator(self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52401),null));
});

ote.app.controller.front_page.GetTransportOperator.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.front_page.GetTransportOperator.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52401){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.front_page.GetTransportOperator(G__52401,self__.__extmap,self__.__hash));
});

ote.app.controller.front_page.GetTransportOperator.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.front_page.GetTransportOperator.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

ote.app.controller.front_page.GetTransportOperator.cljs$lang$type = true;

ote.app.controller.front_page.GetTransportOperator.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.front-page/GetTransportOperator");
});

ote.app.controller.front_page.GetTransportOperator.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.front-page/GetTransportOperator");
});

ote.app.controller.front_page.__GT_GetTransportOperator = (function ote$app$controller$front_page$__GT_GetTransportOperator(){
return (new ote.app.controller.front_page.GetTransportOperator(null,null,null));
});

ote.app.controller.front_page.map__GT_GetTransportOperator = (function ote$app$controller$front_page$map__GT_GetTransportOperator(G__52405){
return (new ote.app.controller.front_page.GetTransportOperator(null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52405)),null));
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
ote.app.controller.front_page.TransportOperatorResponse = (function (response,__meta,__extmap,__hash){
this.response = response;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.front_page.TransportOperatorResponse.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.front_page.TransportOperatorResponse.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52412,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52416 = k52412;
var G__52416__$1 = (((G__52416 instanceof cljs.core.Keyword))?G__52416.fqn:null);
switch (G__52416__$1) {
case "response":
return self__.response;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k52412,else__30866__auto__);

}
});

ote.app.controller.front_page.TransportOperatorResponse.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.front-page.TransportOperatorResponse{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.front_page.TransportOperatorResponse.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52411){
var self__ = this;
var G__52411__$1 = this;
return (new cljs.core.RecordIter((0),G__52411__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"response","response",-1068424192)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.front_page.TransportOperatorResponse.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.front_page.TransportOperatorResponse.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.front_page.TransportOperatorResponse(self__.response,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.front_page.TransportOperatorResponse.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.front_page.TransportOperatorResponse.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-1025208794 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.front_page.TransportOperatorResponse.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52413,other52414){
var self__ = this;
var this52413__$1 = this;
return (!((other52414 == null))) && ((this52413__$1.constructor === other52414.constructor)) && (cljs.core._EQ_.call(null,this52413__$1.response,other52414.response)) && (cljs.core._EQ_.call(null,this52413__$1.__extmap,other52414.__extmap));
});

ote.app.controller.front_page.TransportOperatorResponse.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"response","response",-1068424192),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.front_page.TransportOperatorResponse(self__.response,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.front_page.TransportOperatorResponse.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52411){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52417 = cljs.core.keyword_identical_QMARK_;
var expr__52418 = k__30871__auto__;
if(cljs.core.truth_(pred__52417.call(null,new cljs.core.Keyword(null,"response","response",-1068424192),expr__52418))){
return (new ote.app.controller.front_page.TransportOperatorResponse(G__52411,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.front_page.TransportOperatorResponse(self__.response,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52411),null));
}
});

ote.app.controller.front_page.TransportOperatorResponse.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.front_page.TransportOperatorResponse.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52411){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.front_page.TransportOperatorResponse(self__.response,G__52411,self__.__extmap,self__.__hash));
});

ote.app.controller.front_page.TransportOperatorResponse.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.front_page.TransportOperatorResponse.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"response","response",572107335,null)], null);
});

ote.app.controller.front_page.TransportOperatorResponse.cljs$lang$type = true;

ote.app.controller.front_page.TransportOperatorResponse.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.front-page/TransportOperatorResponse");
});

ote.app.controller.front_page.TransportOperatorResponse.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.front-page/TransportOperatorResponse");
});

ote.app.controller.front_page.__GT_TransportOperatorResponse = (function ote$app$controller$front_page$__GT_TransportOperatorResponse(response){
return (new ote.app.controller.front_page.TransportOperatorResponse(response,null,null,null));
});

ote.app.controller.front_page.map__GT_TransportOperatorResponse = (function ote$app$controller$front_page$map__GT_TransportOperatorResponse(G__52415){
return (new ote.app.controller.front_page.TransportOperatorResponse(new cljs.core.Keyword(null,"response","response",-1068424192).cljs$core$IFn$_invoke$arity$1(G__52415),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52415,new cljs.core.Keyword(null,"response","response",-1068424192))),null));
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
ote.app.controller.front_page.GetTransportOperatorData = (function (__meta,__extmap,__hash){
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.front_page.GetTransportOperatorData.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.front_page.GetTransportOperatorData.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52422,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52426 = k52422;
switch (G__52426) {
default:
return cljs.core.get.call(null,self__.__extmap,k52422,else__30866__auto__);

}
});

ote.app.controller.front_page.GetTransportOperatorData.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.front-page.GetTransportOperatorData{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.front_page.GetTransportOperatorData.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52421){
var self__ = this;
var G__52421__$1 = this;
return (new cljs.core.RecordIter((0),G__52421__$1,0,cljs.core.PersistentVector.EMPTY,(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.front_page.GetTransportOperatorData.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.front_page.GetTransportOperatorData.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.front_page.GetTransportOperatorData(self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.front_page.GetTransportOperatorData.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (0 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.front_page.GetTransportOperatorData.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (385043618 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.front_page.GetTransportOperatorData.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52423,other52424){
var self__ = this;
var this52423__$1 = this;
return (!((other52424 == null))) && ((this52423__$1.constructor === other52424.constructor)) && (cljs.core._EQ_.call(null,this52423__$1.__extmap,other52424.__extmap));
});

ote.app.controller.front_page.GetTransportOperatorData.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.EMPTY,k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.front_page.GetTransportOperatorData(self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.front_page.GetTransportOperatorData.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52421){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52427 = cljs.core.keyword_identical_QMARK_;
var expr__52428 = k__30871__auto__;
return (new ote.app.controller.front_page.GetTransportOperatorData(self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52421),null));
});

ote.app.controller.front_page.GetTransportOperatorData.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.front_page.GetTransportOperatorData.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52421){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.front_page.GetTransportOperatorData(G__52421,self__.__extmap,self__.__hash));
});

ote.app.controller.front_page.GetTransportOperatorData.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.front_page.GetTransportOperatorData.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

ote.app.controller.front_page.GetTransportOperatorData.cljs$lang$type = true;

ote.app.controller.front_page.GetTransportOperatorData.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.front-page/GetTransportOperatorData");
});

ote.app.controller.front_page.GetTransportOperatorData.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.front-page/GetTransportOperatorData");
});

ote.app.controller.front_page.__GT_GetTransportOperatorData = (function ote$app$controller$front_page$__GT_GetTransportOperatorData(){
return (new ote.app.controller.front_page.GetTransportOperatorData(null,null,null));
});

ote.app.controller.front_page.map__GT_GetTransportOperatorData = (function ote$app$controller$front_page$map__GT_GetTransportOperatorData(G__52425){
return (new ote.app.controller.front_page.GetTransportOperatorData(null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52425)),null));
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
ote.app.controller.front_page.TransportOperatorDataResponse = (function (response,__meta,__extmap,__hash){
this.response = response;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.front_page.TransportOperatorDataResponse.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52432,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52436 = k52432;
var G__52436__$1 = (((G__52436 instanceof cljs.core.Keyword))?G__52436.fqn:null);
switch (G__52436__$1) {
case "response":
return self__.response;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k52432,else__30866__auto__);

}
});

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.front-page.TransportOperatorDataResponse{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52431){
var self__ = this;
var G__52431__$1 = this;
return (new cljs.core.RecordIter((0),G__52431__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"response","response",-1068424192)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.front_page.TransportOperatorDataResponse(self__.response,self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1405671580 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52433,other52434){
var self__ = this;
var this52433__$1 = this;
return (!((other52434 == null))) && ((this52433__$1.constructor === other52434.constructor)) && (cljs.core._EQ_.call(null,this52433__$1.response,other52434.response)) && (cljs.core._EQ_.call(null,this52433__$1.__extmap,other52434.__extmap));
});

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"response","response",-1068424192),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.front_page.TransportOperatorDataResponse(self__.response,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52431){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52437 = cljs.core.keyword_identical_QMARK_;
var expr__52438 = k__30871__auto__;
if(cljs.core.truth_(pred__52437.call(null,new cljs.core.Keyword(null,"response","response",-1068424192),expr__52438))){
return (new ote.app.controller.front_page.TransportOperatorDataResponse(G__52431,self__.__meta,self__.__extmap,null));
} else {
return (new ote.app.controller.front_page.TransportOperatorDataResponse(self__.response,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52431),null));
}
});

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"response","response",-1068424192),self__.response],null))], null),self__.__extmap));
});

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52431){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.front_page.TransportOperatorDataResponse(self__.response,G__52431,self__.__extmap,self__.__hash));
});

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.front_page.TransportOperatorDataResponse.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"response","response",572107335,null)], null);
});

ote.app.controller.front_page.TransportOperatorDataResponse.cljs$lang$type = true;

ote.app.controller.front_page.TransportOperatorDataResponse.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.front-page/TransportOperatorDataResponse");
});

ote.app.controller.front_page.TransportOperatorDataResponse.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.front-page/TransportOperatorDataResponse");
});

ote.app.controller.front_page.__GT_TransportOperatorDataResponse = (function ote$app$controller$front_page$__GT_TransportOperatorDataResponse(response){
return (new ote.app.controller.front_page.TransportOperatorDataResponse(response,null,null,null));
});

ote.app.controller.front_page.map__GT_TransportOperatorDataResponse = (function ote$app$controller$front_page$map__GT_TransportOperatorDataResponse(G__52435){
return (new ote.app.controller.front_page.TransportOperatorDataResponse(new cljs.core.Keyword(null,"response","response",-1068424192).cljs$core$IFn$_invoke$arity$1(G__52435),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52435,new cljs.core.Keyword(null,"response","response",-1068424192))),null));
});

ote.app.controller.front_page.ChangePage.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.front_page.ChangePage.prototype.tuck$core$Event$process_event$arity$2 = (function (p__52441,app){
var map__52442 = p__52441;
var map__52442__$1 = ((((!((map__52442 == null)))?((((map__52442.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52442.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52442):map__52442);
var given_page = cljs.core.get.call(null,map__52442__$1,new cljs.core.Keyword(null,"given-page","given-page",-2011533217));
var map__52444 = this;
var map__52444__$1 = ((((!((map__52444 == null)))?((((map__52444.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52444.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52444):map__52444);
var given_page__$1 = cljs.core.get.call(null,map__52444__$1,new cljs.core.Keyword(null,"given-page","given-page",-2011533217));
ote.app.routes.navigate_BANG_.call(null,given_page__$1);

return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"page","page",849072397),given_page__$1);
});

ote.app.controller.front_page.ToggleDebugState.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.front_page.ToggleDebugState.prototype.tuck$core$Event$process_event$arity$2 = (function (_,app){
var ___$1 = this;
if(cljs.core.truth_(cljs.core.get_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ote-service-flags","ote-service-flags",-965917048),new cljs.core.Keyword(null,"show-debug","show-debug",267843982)], null)))){
return cljs.core.assoc_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ote-service-flags","ote-service-flags",-965917048),new cljs.core.Keyword(null,"show-debug","show-debug",267843982)], null),false);
} else {
return cljs.core.assoc_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ote-service-flags","ote-service-flags",-965917048),new cljs.core.Keyword(null,"show-debug","show-debug",267843982)], null),true);

}
});

ote.app.controller.front_page.OpenUserMenu.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.front_page.OpenUserMenu.prototype.tuck$core$Event$process_event$arity$2 = (function (_,app){
var ___$1 = this;
cljs.core.assoc_in.call(null,app,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"ote-service-flags","ote-service-flags",-965917048),new cljs.core.Keyword(null,"user-menu-open","user-menu-open",-1786787308)], null),true);

return app;
});

ote.app.controller.front_page.GetTransportOperator.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.front_page.GetTransportOperator.prototype.tuck$core$Event$process_event$arity$2 = (function (_,app){
var ___$1 = this;
ote.communication.post_BANG_.call(null,"transport-operator/group",cljs.core.PersistentArrayMap.EMPTY,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-success","on-success",1786904109),tuck.core.send_async_BANG_.call(null,ote.app.controller.front_page.__GT_TransportOperatorResponse)], null));

return app;
});

ote.app.controller.front_page.TransportOperatorResponse.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.front_page.TransportOperatorResponse.prototype.tuck$core$Event$process_event$arity$2 = (function (p__52446,app){
var map__52447 = p__52446;
var map__52447__$1 = ((((!((map__52447 == null)))?((((map__52447.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52447.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52447):map__52447);
var response = cljs.core.get.call(null,map__52447__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
var map__52449 = this;
var map__52449__$1 = ((((!((map__52449 == null)))?((((map__52449.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52449.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52449):map__52449);
var response__$1 = cljs.core.get.call(null,map__52449__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982),response__$1);
});

ote.app.controller.front_page.GetTransportOperatorData.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.front_page.GetTransportOperatorData.prototype.tuck$core$Event$process_event$arity$2 = (function (_,app){
var ___$1 = this;
ote.communication.post_BANG_.call(null,"transport-operator/data",cljs.core.PersistentArrayMap.EMPTY,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"on-success","on-success",1786904109),tuck.core.send_async_BANG_.call(null,ote.app.controller.front_page.__GT_TransportOperatorDataResponse)], null));

return app;
});

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.front_page.TransportOperatorDataResponse.prototype.tuck$core$Event$process_event$arity$2 = (function (p__52451,app){
var map__52452 = p__52451;
var map__52452__$1 = ((((!((map__52452 == null)))?((((map__52452.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52452.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52452):map__52452);
var response = cljs.core.get.call(null,map__52452__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
var map__52454 = this;
var map__52454__$1 = ((((!((map__52454 == null)))?((((map__52454.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__52454.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__52454):map__52454);
var response__$1 = cljs.core.get.call(null,map__52454__$1,new cljs.core.Keyword(null,"response","response",-1068424192));
return cljs.core.assoc.call(null,app,new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982),cljs.core.get.call(null,response__$1,new cljs.core.Keyword(null,"transport-operator","transport-operator",-1434913982)),new cljs.core.Keyword(null,"transport-services","transport-services",-1601696230),cljs.core.get.call(null,response__$1,new cljs.core.Keyword(null,"transport-service-vector","transport-service-vector",262111307)),new cljs.core.Keyword(null,"user","user",1532431356),cljs.core.get.call(null,response__$1,new cljs.core.Keyword(null,"user","user",1532431356)));
});

//# sourceMappingURL=front_page.js.map?rel=1510137295482
