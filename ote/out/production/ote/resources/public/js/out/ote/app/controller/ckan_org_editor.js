// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.app.controller.ckan_org_editor');
goog.require('cljs.core');
goog.require('tuck.core');
goog.require('ote.communication');
goog.require('taoensso.timbre');
goog.require('ote.app.routes');
goog.require('ote.app.controller.transport_operator');

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
ote.app.controller.ckan_org_editor.StartEditor = (function (__meta,__extmap,__hash){
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.app.controller.ckan_org_editor.StartEditor.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.app.controller.ckan_org_editor.StartEditor.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k52258,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__52262 = k52258;
switch (G__52262) {
default:
return cljs.core.get.call(null,self__.__extmap,k52258,else__30866__auto__);

}
});

ote.app.controller.ckan_org_editor.StartEditor.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.app.controller.ckan-org-editor.StartEditor{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.ckan_org_editor.StartEditor.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__52257){
var self__ = this;
var G__52257__$1 = this;
return (new cljs.core.RecordIter((0),G__52257__$1,0,cljs.core.PersistentVector.EMPTY,(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.app.controller.ckan_org_editor.StartEditor.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.app.controller.ckan_org_editor.StartEditor.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.app.controller.ckan_org_editor.StartEditor(self__.__meta,self__.__extmap,self__.__hash));
});

ote.app.controller.ckan_org_editor.StartEditor.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (0 + cljs.core.count.call(null,self__.__extmap));
});

ote.app.controller.ckan_org_editor.StartEditor.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-578923752 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.app.controller.ckan_org_editor.StartEditor.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this52259,other52260){
var self__ = this;
var this52259__$1 = this;
return (!((other52260 == null))) && ((this52259__$1.constructor === other52260.constructor)) && (cljs.core._EQ_.call(null,this52259__$1.__extmap,other52260.__extmap));
});

ote.app.controller.ckan_org_editor.StartEditor.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,cljs.core.PersistentHashSet.EMPTY,k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.app.controller.ckan_org_editor.StartEditor(self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.app.controller.ckan_org_editor.StartEditor.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__52257){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__52263 = cljs.core.keyword_identical_QMARK_;
var expr__52264 = k__30871__auto__;
return (new ote.app.controller.ckan_org_editor.StartEditor(self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__52257),null));
});

ote.app.controller.ckan_org_editor.StartEditor.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,cljs.core.PersistentVector.EMPTY,self__.__extmap));
});

ote.app.controller.ckan_org_editor.StartEditor.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__52257){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.app.controller.ckan_org_editor.StartEditor(G__52257,self__.__extmap,self__.__hash));
});

ote.app.controller.ckan_org_editor.StartEditor.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.app.controller.ckan_org_editor.StartEditor.getBasis = (function (){
return cljs.core.PersistentVector.EMPTY;
});

ote.app.controller.ckan_org_editor.StartEditor.cljs$lang$type = true;

ote.app.controller.ckan_org_editor.StartEditor.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.app.controller.ckan-org-editor/StartEditor");
});

ote.app.controller.ckan_org_editor.StartEditor.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.app.controller.ckan-org-editor/StartEditor");
});

ote.app.controller.ckan_org_editor.__GT_StartEditor = (function ote$app$controller$ckan_org_editor$__GT_StartEditor(){
return (new ote.app.controller.ckan_org_editor.StartEditor(null,null,null));
});

ote.app.controller.ckan_org_editor.map__GT_StartEditor = (function ote$app$controller$ckan_org_editor$map__GT_StartEditor(G__52261){
return (new ote.app.controller.ckan_org_editor.StartEditor(null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__52261)),null));
});

ote.app.controller.ckan_org_editor.StartEditor.prototype.tuck$core$Event$ = cljs.core.PROTOCOL_SENTINEL;

ote.app.controller.ckan_org_editor.StartEditor.prototype.tuck$core$Event$process_event$arity$2 = (function (_,app){
var ___$1 = this;
var ckan_group_id_52267 = document.getElementById("nap_viewer").getAttribute("data-group-id");
ote.app.controller.transport_operator.transport_operator_data.call(null);

return app;
});

//# sourceMappingURL=ckan_org_editor.js.map?rel=1510137294890
