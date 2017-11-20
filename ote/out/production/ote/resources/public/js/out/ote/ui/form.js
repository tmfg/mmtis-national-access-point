// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.ui.form');
goog.require('cljs.core');
goog.require('ote.ui.validation');
goog.require('ote.ui.form_fields');
goog.require('cljs_time.core');
goog.require('clojure.string');
goog.require('cljs_react_material_ui.reagent');
goog.require('reagent.core');
goog.require('ote.localization');
goog.require('stylefy.core');
goog.require('ote.style.form');
goog.require('cljs_react_material_ui.icons');
/**
 * Create a new info form element that doesn't have any interaction, just shows a help text.
 */
ote.ui.form.info = (function ote$ui$form$info(text){
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"name","name",1843675177),new cljs.core.Keyword("ote.ui.form","info","ote.ui.form/info",941437990),new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"component","component",1555936782),new cljs.core.Keyword(null,"component","component",1555936782),(function (_){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.icons.action_info_outline], null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div","div",1057191632),stylefy.core.use_style.call(null,ote.style.form.form_info_text),text], null)], null);
})], null);
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
ote.ui.form.Group = (function (label,options,schemas,__meta,__extmap,__hash){
this.label = label;
this.options = options;
this.schemas = schemas;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.ui.form.Group.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.ui.form.Group.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51068,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51072 = k51068;
var G__51072__$1 = (((G__51072 instanceof cljs.core.Keyword))?G__51072.fqn:null);
switch (G__51072__$1) {
case "label":
return self__.label;

break;
case "options":
return self__.options;

break;
case "schemas":
return self__.schemas;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51068,else__30866__auto__);

}
});

ote.ui.form.Group.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.ui.form.Group{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"label","label",1718410804),self__.label],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"options","options",99638489),self__.options],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"schemas","schemas",575070579),self__.schemas],null))], null),self__.__extmap));
});

ote.ui.form.Group.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51067){
var self__ = this;
var G__51067__$1 = this;
return (new cljs.core.RecordIter((0),G__51067__$1,3,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"options","options",99638489),new cljs.core.Keyword(null,"schemas","schemas",575070579)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.ui.form.Group.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.ui.form.Group.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.ui.form.Group(self__.label,self__.options,self__.schemas,self__.__meta,self__.__extmap,self__.__hash));
});

ote.ui.form.Group.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (3 + cljs.core.count.call(null,self__.__extmap));
});

ote.ui.form.Group.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1204724283 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.ui.form.Group.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51069,other51070){
var self__ = this;
var this51069__$1 = this;
return (!((other51070 == null))) && ((this51069__$1.constructor === other51070.constructor)) && (cljs.core._EQ_.call(null,this51069__$1.label,other51070.label)) && (cljs.core._EQ_.call(null,this51069__$1.options,other51070.options)) && (cljs.core._EQ_.call(null,this51069__$1.schemas,other51070.schemas)) && (cljs.core._EQ_.call(null,this51069__$1.__extmap,other51070.__extmap));
});

ote.ui.form.Group.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"schemas","schemas",575070579),null,new cljs.core.Keyword(null,"label","label",1718410804),null,new cljs.core.Keyword(null,"options","options",99638489),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.ui.form.Group(self__.label,self__.options,self__.schemas,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.ui.form.Group.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51067){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51073 = cljs.core.keyword_identical_QMARK_;
var expr__51074 = k__30871__auto__;
if(cljs.core.truth_(pred__51073.call(null,new cljs.core.Keyword(null,"label","label",1718410804),expr__51074))){
return (new ote.ui.form.Group(G__51067,self__.options,self__.schemas,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__51073.call(null,new cljs.core.Keyword(null,"options","options",99638489),expr__51074))){
return (new ote.ui.form.Group(self__.label,G__51067,self__.schemas,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__51073.call(null,new cljs.core.Keyword(null,"schemas","schemas",575070579),expr__51074))){
return (new ote.ui.form.Group(self__.label,self__.options,G__51067,self__.__meta,self__.__extmap,null));
} else {
return (new ote.ui.form.Group(self__.label,self__.options,self__.schemas,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51067),null));
}
}
}
});

ote.ui.form.Group.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"label","label",1718410804),self__.label],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"options","options",99638489),self__.options],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"schemas","schemas",575070579),self__.schemas],null))], null),self__.__extmap));
});

ote.ui.form.Group.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51067){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.ui.form.Group(self__.label,self__.options,self__.schemas,G__51067,self__.__extmap,self__.__hash));
});

ote.ui.form.Group.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.ui.form.Group.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"label","label",-936024965,null),new cljs.core.Symbol(null,"options","options",1740170016,null),new cljs.core.Symbol(null,"schemas","schemas",-2079365190,null)], null);
});

ote.ui.form.Group.cljs$lang$type = true;

ote.ui.form.Group.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.ui.form/Group");
});

ote.ui.form.Group.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.ui.form/Group");
});

ote.ui.form.__GT_Group = (function ote$ui$form$__GT_Group(label,options,schemas){
return (new ote.ui.form.Group(label,options,schemas,null,null,null));
});

ote.ui.form.map__GT_Group = (function ote$ui$form$map__GT_Group(G__51071){
return (new ote.ui.form.Group(new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(G__51071),new cljs.core.Keyword(null,"options","options",99638489).cljs$core$IFn$_invoke$arity$1(G__51071),new cljs.core.Keyword(null,"schemas","schemas",575070579).cljs$core$IFn$_invoke$arity$1(G__51071),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51071,new cljs.core.Keyword(null,"label","label",1718410804),new cljs.core.Keyword(null,"options","options",99638489),new cljs.core.Keyword(null,"schemas","schemas",575070579))),null));
});

/**
 * Create a group of form fields. The first argument
 *   is the label of the group (or an options map containing a label).
 *   The rest of the arguments are field schemas for the fields in the group.
 */
ote.ui.form.group = (function ote$ui$form$group(var_args){
var args__31459__auto__ = [];
var len__31452__auto___51079 = arguments.length;
var i__31453__auto___51080 = (0);
while(true){
if((i__31453__auto___51080 < len__31452__auto___51079)){
args__31459__auto__.push((arguments[i__31453__auto___51080]));

var G__51081 = (i__31453__auto___51080 + (1));
i__31453__auto___51080 = G__51081;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return ote.ui.form.group.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

ote.ui.form.group.cljs$core$IFn$_invoke$arity$variadic = (function (label_or_options,schemas){
var temp__5288__auto__ = (function (){var and__30163__auto__ = cljs.core.map_QMARK_.call(null,label_or_options);
if(and__30163__auto__){
return label_or_options;
} else {
return and__30163__auto__;
}
})();
if(cljs.core.truth_(temp__5288__auto__)){
var options = temp__5288__auto__;
return ote.ui.form.__GT_Group.call(null,new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(options),cljs.core.merge.call(null,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"layout","layout",-2120940921),new cljs.core.Keyword(null,"default","default",-1987822328)], null),options),schemas);
} else {
return ote.ui.form.__GT_Group.call(null,label_or_options,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"layout","layout",-2120940921),new cljs.core.Keyword(null,"default","default",-1987822328)], null),schemas);
}
});

ote.ui.form.group.cljs$lang$maxFixedArity = (1);

ote.ui.form.group.cljs$lang$applyTo = (function (seq51077){
var G__51078 = cljs.core.first.call(null,seq51077);
var seq51077__$1 = cljs.core.next.call(null,seq51077);
return ote.ui.form.group.cljs$core$IFn$_invoke$arity$variadic(G__51078,seq51077__$1);
});

/**
 * Creates an unlabeled group with all fields side-by-side in a row.
 */
ote.ui.form.row = (function ote$ui$form$row(var_args){
var args__31459__auto__ = [];
var len__31452__auto___51083 = arguments.length;
var i__31453__auto___51084 = (0);
while(true){
if((i__31453__auto___51084 < len__31452__auto___51083)){
args__31459__auto__.push((arguments[i__31453__auto___51084]));

var G__51085 = (i__31453__auto___51084 + (1));
i__31453__auto___51084 = G__51085;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((0) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((0)),(0),null)):null);
return ote.ui.form.row.cljs$core$IFn$_invoke$arity$variadic(argseq__31460__auto__);
});

ote.ui.form.row.cljs$core$IFn$_invoke$arity$variadic = (function (schemas){
return ote.ui.form.__GT_Group.call(null,null,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"row?","row?",394970415),true], null),schemas);
});

ote.ui.form.row.cljs$lang$maxFixedArity = (0);

ote.ui.form.row.cljs$lang$applyTo = (function (seq51082){
return ote.ui.form.row.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq.call(null,seq51082));
});

ote.ui.form.group_QMARK_ = (function ote$ui$form$group_QMARK_(x){
return (x instanceof ote.ui.form.Group);
});
/**
 * Check if any fields have been modified.
 */
ote.ui.form.modified_QMARK_ = (function ote$ui$form$modified_QMARK_(data){
return !(cljs.core.empty_QMARK_.call(null,new cljs.core.Keyword("ote.ui.form","modified","ote.ui.form/modified",-37635778).cljs$core$IFn$_invoke$arity$1(data)));
});
/**
 * Returns a (possibly empty) set of required fields that have no value in the input form data.
 */
ote.ui.form.missing_required_fields = (function ote$ui$form$missing_required_fields(data){
return new cljs.core.Keyword("ote.ui.form","missing-required-fields","ote.ui.form/missing-required-fields",-1019625183).cljs$core$IFn$_invoke$arity$1(data);
});
/**
 * Returns true if any required field is missing a value.
 */
ote.ui.form.required_fields_missing_QMARK_ = (function ote$ui$form$required_fields_missing_QMARK_(data){
return !(cljs.core.empty_QMARK_.call(null,ote.ui.form.missing_required_fields.call(null,data)));
});
/**
 * Returns true if there are any validation errors in the input form data.
 */
ote.ui.form.errors_QMARK_ = (function ote$ui$form$errors_QMARK_(data){
return !(cljs.core.empty_QMARK_.call(null,new cljs.core.Keyword("ote.ui.form","errors","ote.ui.form/errors",-2101869710).cljs$core$IFn$_invoke$arity$1(data)));
});
/**
 * Check if input form data is valid. Returns true if there are no validation errors and
 *   all required fields have a value.
 */
ote.ui.form.valid_QMARK_ = (function ote$ui$form$valid_QMARK_(data){
return (cljs.core.not.call(null,ote.ui.form.errors_QMARK_.call(null,data))) && (cljs.core.not.call(null,ote.ui.form.required_fields_missing_QMARK_.call(null,data)));
});
/**
 * Check if form can be saved and that it has been modified.
 */
ote.ui.form.can_save_and_modified_QMARK_ = (function ote$ui$form$can_save_and_modified_QMARK_(data){
var and__30163__auto__ = ote.ui.form.modified_QMARK_.call(null,data);
if(cljs.core.truth_(and__30163__auto__)){
return ote.ui.form.valid_QMARK_.call(null,data);
} else {
return and__30163__auto__;
}
});
/**
 * Check if form can be saved.
 */
ote.ui.form.can_save_QMARK_ = (function ote$ui$form$can_save_QMARK_(data){
return ote.ui.form.valid_QMARK_.call(null,data);
});
/**
 * Check if form save button should be disabled.
 *   Form save should be disabled if there are validation errors,
 *   some required fields are missing values or the form hasn't been
 *   modified at all.
 */
ote.ui.form.disable_save_QMARK_ = (function ote$ui$form$disable_save_QMARK_(data){
return cljs.core.not.call(null,ote.ui.form.can_save_and_modified_QMARK_.call(null,data));
});
/**
 * Returns form data map without form metadata keys
 */
ote.ui.form.without_form_metadata = (function ote$ui$form$without_form_metadata(data){
return cljs.core.dissoc.call(null,data,new cljs.core.Keyword("ote.ui.form","modified","ote.ui.form/modified",-37635778),new cljs.core.Keyword("ote.ui.form","errors","ote.ui.form/errors",-2101869710),new cljs.core.Keyword("ote.ui.form","warnings","ote.ui.form/warnings",-416419939),new cljs.core.Keyword("ote.ui.form","notices","ote.ui.form/notices",-1558257202),new cljs.core.Keyword("ote.ui.form","missing-required-fields","ote.ui.form/missing-required-fields",-1019625183),new cljs.core.Keyword("ote.ui.form","first-modification","ote.ui.form/first-modification",891196454),new cljs.core.Keyword("ote.ui.form","latest-modification","ote.ui.form/latest-modification",676345981),new cljs.core.Keyword("ote.ui.form","schema","ote.ui.form/schema",1420803217),new cljs.core.Keyword("ote.ui.form","closed-groups","ote.ui.form/closed-groups",904145240));
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
ote.ui.form.Label = (function (label,__meta,__extmap,__hash){
this.label = label;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.ui.form.Label.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.ui.form.Label.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k51087,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__51091 = k51087;
var G__51091__$1 = (((G__51091 instanceof cljs.core.Keyword))?G__51091.fqn:null);
switch (G__51091__$1) {
case "label":
return self__.label;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k51087,else__30866__auto__);

}
});

ote.ui.form.Label.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.ui.form.Label{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"label","label",1718410804),self__.label],null))], null),self__.__extmap));
});

ote.ui.form.Label.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__51086){
var self__ = this;
var G__51086__$1 = this;
return (new cljs.core.RecordIter((0),G__51086__$1,1,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"label","label",1718410804)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.ui.form.Label.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.ui.form.Label.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.ui.form.Label(self__.label,self__.__meta,self__.__extmap,self__.__hash));
});

ote.ui.form.Label.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (1 + cljs.core.count.call(null,self__.__extmap));
});

ote.ui.form.Label.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1449102159 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.ui.form.Label.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this51088,other51089){
var self__ = this;
var this51088__$1 = this;
return (!((other51089 == null))) && ((this51088__$1.constructor === other51089.constructor)) && (cljs.core._EQ_.call(null,this51088__$1.label,other51089.label)) && (cljs.core._EQ_.call(null,this51088__$1.__extmap,other51089.__extmap));
});

ote.ui.form.Label.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"label","label",1718410804),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.ui.form.Label(self__.label,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.ui.form.Label.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__51086){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__51092 = cljs.core.keyword_identical_QMARK_;
var expr__51093 = k__30871__auto__;
if(cljs.core.truth_(pred__51092.call(null,new cljs.core.Keyword(null,"label","label",1718410804),expr__51093))){
return (new ote.ui.form.Label(G__51086,self__.__meta,self__.__extmap,null));
} else {
return (new ote.ui.form.Label(self__.label,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__51086),null));
}
});

ote.ui.form.Label.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"label","label",1718410804),self__.label],null))], null),self__.__extmap));
});

ote.ui.form.Label.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__51086){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.ui.form.Label(self__.label,G__51086,self__.__extmap,self__.__hash));
});

ote.ui.form.Label.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.ui.form.Label.getBasis = (function (){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"label","label",-936024965,null)], null);
});

ote.ui.form.Label.cljs$lang$type = true;

ote.ui.form.Label.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.ui.form/Label");
});

ote.ui.form.Label.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.ui.form/Label");
});

ote.ui.form.__GT_Label = (function ote$ui$form$__GT_Label(label){
return (new ote.ui.form.Label(label,null,null,null));
});

ote.ui.form.map__GT_Label = (function ote$ui$form$map__GT_Label(G__51090){
return (new ote.ui.form.Label(new cljs.core.Keyword(null,"label","label",1718410804).cljs$core$IFn$_invoke$arity$1(G__51090),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__51090,new cljs.core.Keyword(null,"label","label",1718410804))),null));
});

ote.ui.form.label_QMARK_ = (function ote$ui$form$label_QMARK_(x){
return (x instanceof ote.ui.form.Label);
});
/**
 * Unpack schemas from groups to a single flat vector without nil values.
 *   Group labels are interleaved with schemas as Label record instances.
 */
ote.ui.form.unpack_groups = (function ote$ui$form$unpack_groups(schemas){
var acc = cljs.core.PersistentVector.EMPTY;
var G__51099 = cljs.core.remove.call(null,cljs.core.nil_QMARK_,schemas);
var vec__51100 = G__51099;
var seq__51101 = cljs.core.seq.call(null,vec__51100);
var first__51102 = cljs.core.first.call(null,seq__51101);
var seq__51101__$1 = cljs.core.next.call(null,seq__51101);
var s = first__51102;
var schemas__$1 = seq__51101__$1;
var acc__$1 = acc;
var G__51099__$1 = G__51099;
while(true){
var acc__$2 = acc__$1;
var vec__51103 = G__51099__$1;
var seq__51104 = cljs.core.seq.call(null,vec__51103);
var first__51105 = cljs.core.first.call(null,seq__51104);
var seq__51104__$1 = cljs.core.next.call(null,seq__51104);
var s__$1 = first__51105;
var schemas__$2 = seq__51104__$1;
if(cljs.core.not.call(null,s__$1)){
return acc__$2;
} else {
if(cljs.core.truth_(ote.ui.form.label_QMARK_.call(null,s__$1))){
var G__51106 = acc__$2;
var G__51107 = schemas__$2;
acc__$1 = G__51106;
G__51099__$1 = G__51107;
continue;
} else {
if(cljs.core.truth_(ote.ui.form.group_QMARK_.call(null,s__$1))){
var G__51108 = acc__$2;
var G__51109 = cljs.core.concat.call(null,cljs.core.remove.call(null,cljs.core.nil_QMARK_,new cljs.core.Keyword(null,"schemas","schemas",575070579).cljs$core$IFn$_invoke$arity$1(s__$1)),schemas__$2);
acc__$1 = G__51108;
G__51099__$1 = G__51109;
continue;
} else {
var G__51110 = cljs.core.conj.call(null,acc__$2,s__$1);
var G__51111 = schemas__$2;
acc__$1 = G__51110;
G__51099__$1 = G__51111;
continue;

}
}
}
break;
}
});
ote.ui.form.validate = (function ote$ui$form$validate(data,schemas){
var all_schemas = ote.ui.form.unpack_groups.call(null,schemas);
var all_errors = ote.ui.validation.validate_row.call(null,null,data,all_schemas,new cljs.core.Keyword(null,"validate","validate",-201300827));
var all_warnings = ote.ui.validation.validate_row.call(null,null,data,all_schemas,new cljs.core.Keyword(null,"warn","warn",-436710552));
var all_notices = ote.ui.validation.validate_row.call(null,null,data,all_schemas,new cljs.core.Keyword(null,"notice","notice",-1121239112));
var missing_required_fields = cljs.core.into.call(null,cljs.core.PersistentHashSet.EMPTY,cljs.core.map.call(null,new cljs.core.Keyword(null,"name","name",1843675177)),ote.ui.validation.missing_required_fields.call(null,data,all_schemas));
return cljs.core.assoc.call(null,data,new cljs.core.Keyword("ote.ui.form","errors","ote.ui.form/errors",-2101869710),all_errors,new cljs.core.Keyword("ote.ui.form","warnings","ote.ui.form/warnings",-416419939),all_warnings,new cljs.core.Keyword("ote.ui.form","notices","ote.ui.form/notices",-1558257202),all_notices,new cljs.core.Keyword("ote.ui.form","missing-required-fields","ote.ui.form/missing-required-fields",-1019625183),missing_required_fields);
});
ote.ui.form.modification_time = (function ote$ui$form$modification_time(p__51112){
var map__51113 = p__51112;
var map__51113__$1 = ((((!((map__51113 == null)))?((((map__51113.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51113.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51113):map__51113);
var data = map__51113__$1;
var first = cljs.core.get.call(null,map__51113__$1,new cljs.core.Keyword("ote.ui.form","first-modification","ote.ui.form/first-modification",891196454));
var latest = cljs.core.get.call(null,map__51113__$1,new cljs.core.Keyword("ote.ui.form","latest-modification","ote.ui.form/latest-modification",676345981));
return cljs.core.assoc.call(null,data,new cljs.core.Keyword("ote.ui.form","first-modification","ote.ui.form/first-modification",891196454),(function (){var or__30175__auto__ = first;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return cljs_time.core.now.call(null);
}
})(),new cljs.core.Keyword("ote.ui.form","latest-modification","ote.ui.form/latest-modification",676345981),cljs_time.core.now.call(null));
});
/**
 * UI for a single form field
 */
ote.ui.form.field_ui = (function ote$ui$form$field_ui(p__51116,data,update_fn,editable_QMARK_,update_form,modified_QMARK_,errors,warnings,notices){
var map__51117 = p__51116;
var map__51117__$1 = ((((!((map__51117 == null)))?((((map__51117.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51117.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51117):map__51117);
var s = map__51117__$1;
var columns = cljs.core.get.call(null,map__51117__$1,new cljs.core.Keyword(null,"columns","columns",1998437288));
var name = cljs.core.get.call(null,map__51117__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var label = cljs.core.get.call(null,map__51117__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var type = cljs.core.get.call(null,map__51117__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var read = cljs.core.get.call(null,map__51117__$1,new cljs.core.Keyword(null,"read","read",1140058661));
var fmt = cljs.core.get.call(null,map__51117__$1,new cljs.core.Keyword(null,"fmt","fmt",332300772));
var col_class = cljs.core.get.call(null,map__51117__$1,new cljs.core.Keyword(null,"col-class","col-class",-1979768310));
var required_QMARK_ = cljs.core.get.call(null,map__51117__$1,new cljs.core.Keyword(null,"required?","required?",-872514462));
var component = cljs.core.get.call(null,map__51117__$1,new cljs.core.Keyword(null,"component","component",1555936782));
if(cljs.core._EQ_.call(null,type,new cljs.core.Keyword(null,"component","component",1555936782))){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.component","div.component",-600683463),component.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"update-form!","update-form!",259937369),((function (map__51117,map__51117__$1,s,columns,name,label,type,read,fmt,col_class,required_QMARK_,component){
return (function (){
return update_form.call(null,s);
});})(map__51117,map__51117__$1,s,columns,name,label,type,read,fmt,col_class,required_QMARK_,component))
,new cljs.core.Keyword(null,"data","data",-232669377),data], null))], null);
} else {
if(cljs.core.truth_(editable_QMARK_)){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.form_fields.field,cljs.core.assoc.call(null,s,new cljs.core.Keyword(null,"form?","form?",-2055688328),true,new cljs.core.Keyword(null,"update!","update!",-1453508586),update_fn,new cljs.core.Keyword(null,"error","error",-978969032),((!(cljs.core.empty_QMARK_.call(null,errors)))?clojure.string.join.call(null," ",errors):null),new cljs.core.Keyword(null,"warning","warning",-1685650671),(cljs.core.truth_((function (){var and__30163__auto__ = required_QMARK_;
if(cljs.core.truth_(and__30163__auto__)){
return ote.ui.validation.empty_value_QMARK_.call(null,data);
} else {
return and__30163__auto__;
}
})())?ote.localization.tr.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"common-texts","common-texts",-934994303),new cljs.core.Keyword(null,"required-field","required-field",1847261386)], null)):null)),data], null);
} else {
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-control-static","div.form-control-static",-1212085731),(cljs.core.truth_(fmt)?fmt.call(null,(function (){var or__30175__auto__ = read;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return ((function (or__30175__auto__,map__51117,map__51117__$1,s,columns,name,label,type,read,fmt,col_class,required_QMARK_,component){
return (function (p1__51115_SHARP_){
return cljs.core.get.call(null,p1__51115_SHARP_,name);
});
;})(or__30175__auto__,map__51117,map__51117__$1,s,columns,name,label,type,read,fmt,col_class,required_QMARK_,component))
}
})().call(null,data)):ote.ui.form_fields.show_value.call(null,s,data))], null);
}
}
});
ote.ui.form.col_classes = new cljs.core.PersistentArrayMap(null, 3, [(1),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["col-xs-12","col-md-4","col-lg-4"], null),(2),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["col-xs-12","col-md-6","col-lg-6"], null),(3),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, ["col-xs-12","col-md-12","col-lg-12"], null)], null);
/**
 * UI for a group of fields in the form
 */
ote.ui.form.group_ui = (function ote$ui$form$group_ui(style,schemas,data,update_fn,can_edit_QMARK_,current_focus,set_focus_BANG_,modified,errors,warnings,notices,update_form){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-group","div.form-group",-1721134770),stylefy.core.use_style.call(null,style),cljs.core.doall.call(null,(function (){var iter__31057__auto__ = (function ote$ui$form$group_ui_$_iter__51120(s__51121){
return (new cljs.core.LazySeq(null,(function (){
var s__51121__$1 = s__51121;
while(true){
var temp__5290__auto__ = cljs.core.seq.call(null,s__51121__$1);
if(temp__5290__auto__){
var s__51121__$2 = temp__5290__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,s__51121__$2)){
var c__31055__auto__ = cljs.core.chunk_first.call(null,s__51121__$2);
var size__31056__auto__ = cljs.core.count.call(null,c__31055__auto__);
var b__51123 = cljs.core.chunk_buffer.call(null,size__31056__auto__);
if((function (){var i__51122 = (0);
while(true){
if((i__51122 < size__31056__auto__)){
var map__51124 = cljs.core._nth.call(null,c__31055__auto__,i__51122);
var map__51124__$1 = ((((!((map__51124 == null)))?((((map__51124.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51124.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51124):map__51124);
var s = map__51124__$1;
var name = cljs.core.get.call(null,map__51124__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var editable_QMARK_ = cljs.core.get.call(null,map__51124__$1,new cljs.core.Keyword(null,"editable?","editable?",-1805477333));
var read = cljs.core.get.call(null,map__51124__$1,new cljs.core.Keyword(null,"read","read",1140058661));
var write = cljs.core.get.call(null,map__51124__$1,new cljs.core.Keyword(null,"write","write",-1857649168));
var editable_QMARK___$1 = (function (){var and__30163__auto__ = can_edit_QMARK_;
if(cljs.core.truth_(and__30163__auto__)){
var or__30175__auto__ = (editable_QMARK_ == null);
if(or__30175__auto__){
return or__30175__auto__;
} else {
return editable_QMARK_.call(null,data);
}
} else {
return and__30163__auto__;
}
})();
cljs.core.chunk_append.call(null,b__51123,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-field","div.form-field",1453349441),stylefy.core.use_sub_style.call(null,style,new cljs.core.Keyword(null,"form-field","form-field",-318915722)),new cljs.core.PersistentVector(null, 10, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.form.field_ui,cljs.core.assoc.call(null,s,new cljs.core.Keyword(null,"focus","focus",234677911),cljs.core._EQ_.call(null,name,current_focus),new cljs.core.Keyword(null,"on-focus","on-focus",-13737624),((function (i__51122,editable_QMARK___$1,map__51124,map__51124__$1,s,name,editable_QMARK_,read,write,c__31055__auto__,size__31056__auto__,b__51123,s__51121__$2,temp__5290__auto__){
return (function (){
return set_focus_BANG_.call(null,name);
});})(i__51122,editable_QMARK___$1,map__51124,map__51124__$1,s,name,editable_QMARK_,read,write,c__31055__auto__,size__31056__auto__,b__51123,s__51121__$2,temp__5290__auto__))
),(function (){var or__30175__auto__ = read;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return name;
}
})().call(null,data),((function (i__51122,editable_QMARK___$1,map__51124,map__51124__$1,s,name,editable_QMARK_,read,write,c__31055__auto__,size__31056__auto__,b__51123,s__51121__$2,temp__5290__auto__){
return (function (p1__51119_SHARP_){
return update_fn.call(null,(function (){var or__30175__auto__ = write;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return name;
}
})(),p1__51119_SHARP_);
});})(i__51122,editable_QMARK___$1,map__51124,map__51124__$1,s,name,editable_QMARK_,read,write,c__31055__auto__,size__31056__auto__,b__51123,s__51121__$2,temp__5290__auto__))
,editable_QMARK___$1,update_form,cljs.core.get.call(null,modified,name),cljs.core.get.call(null,errors,name),cljs.core.get.call(null,warnings,name),cljs.core.get.call(null,notices,name)], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),name], null)));

var G__51128 = (i__51122 + (1));
i__51122 = G__51128;
continue;
} else {
return true;
}
break;
}
})()){
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__51123),ote$ui$form$group_ui_$_iter__51120.call(null,cljs.core.chunk_rest.call(null,s__51121__$2)));
} else {
return cljs.core.chunk_cons.call(null,cljs.core.chunk.call(null,b__51123),null);
}
} else {
var map__51126 = cljs.core.first.call(null,s__51121__$2);
var map__51126__$1 = ((((!((map__51126 == null)))?((((map__51126.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51126.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51126):map__51126);
var s = map__51126__$1;
var name = cljs.core.get.call(null,map__51126__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var editable_QMARK_ = cljs.core.get.call(null,map__51126__$1,new cljs.core.Keyword(null,"editable?","editable?",-1805477333));
var read = cljs.core.get.call(null,map__51126__$1,new cljs.core.Keyword(null,"read","read",1140058661));
var write = cljs.core.get.call(null,map__51126__$1,new cljs.core.Keyword(null,"write","write",-1857649168));
var editable_QMARK___$1 = (function (){var and__30163__auto__ = can_edit_QMARK_;
if(cljs.core.truth_(and__30163__auto__)){
var or__30175__auto__ = (editable_QMARK_ == null);
if(or__30175__auto__){
return or__30175__auto__;
} else {
return editable_QMARK_.call(null,data);
}
} else {
return and__30163__auto__;
}
})();
return cljs.core.cons.call(null,cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-field","div.form-field",1453349441),stylefy.core.use_sub_style.call(null,style,new cljs.core.Keyword(null,"form-field","form-field",-318915722)),new cljs.core.PersistentVector(null, 10, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.form.field_ui,cljs.core.assoc.call(null,s,new cljs.core.Keyword(null,"focus","focus",234677911),cljs.core._EQ_.call(null,name,current_focus),new cljs.core.Keyword(null,"on-focus","on-focus",-13737624),((function (editable_QMARK___$1,map__51126,map__51126__$1,s,name,editable_QMARK_,read,write,s__51121__$2,temp__5290__auto__){
return (function (){
return set_focus_BANG_.call(null,name);
});})(editable_QMARK___$1,map__51126,map__51126__$1,s,name,editable_QMARK_,read,write,s__51121__$2,temp__5290__auto__))
),(function (){var or__30175__auto__ = read;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return name;
}
})().call(null,data),((function (editable_QMARK___$1,map__51126,map__51126__$1,s,name,editable_QMARK_,read,write,s__51121__$2,temp__5290__auto__){
return (function (p1__51119_SHARP_){
return update_fn.call(null,(function (){var or__30175__auto__ = write;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return name;
}
})(),p1__51119_SHARP_);
});})(editable_QMARK___$1,map__51126,map__51126__$1,s,name,editable_QMARK_,read,write,s__51121__$2,temp__5290__auto__))
,editable_QMARK___$1,update_form,cljs.core.get.call(null,modified,name),cljs.core.get.call(null,errors,name),cljs.core.get.call(null,warnings,name),cljs.core.get.call(null,notices,name)], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),name], null)),ote$ui$form$group_ui_$_iter__51120.call(null,cljs.core.rest.call(null,s__51121__$2)));
}
} else {
return null;
}
break;
}
}),null,null));
});
return iter__31057__auto__.call(null,schemas);
})())], null);
});
/**
 * Add an automatically generated `:label` to fields with the given `:name->label` function.
 *   If a schema has a manually given label, it is not overwritten.
 */
ote.ui.form.with_automatic_labels = (function ote$ui$form$with_automatic_labels(name__GT_label,schemas){
if(cljs.core.not.call(null,name__GT_label)){
return schemas;
} else {
return cljs.core.mapv.call(null,(function (p__51129){
var map__51130 = p__51129;
var map__51130__$1 = ((((!((map__51130 == null)))?((((map__51130.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51130.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51130):map__51130);
var s = map__51130__$1;
var name = cljs.core.get.call(null,map__51130__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var label = cljs.core.get.call(null,map__51130__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var type = cljs.core.get.call(null,map__51130__$1,new cljs.core.Keyword(null,"type","type",1174270348));
var schema = cljs.core.assoc.call(null,s,new cljs.core.Keyword(null,"label","label",1718410804),(function (){var or__30175__auto__ = label;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return name__GT_label.call(null,name);
}
})());
if(cljs.core._EQ_.call(null,type,new cljs.core.Keyword(null,"table","table",-564943036))){
return cljs.core.update.call(null,schema,new cljs.core.Keyword(null,"table-fields","table-fields",-923733996),cljs.core.partial.call(null,ote.ui.form.with_automatic_labels,name__GT_label));
} else {
return schema;
}
}),schemas);
}
});
ote.ui.form.toggle = (function ote$ui$form$toggle(set,value){
var set__$1 = (function (){var or__30175__auto__ = set;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return cljs.core.PersistentHashSet.EMPTY;
}
})();
if(cljs.core.truth_(set__$1.call(null,value))){
return cljs.core.disj.call(null,set__$1,value);
} else {
return cljs.core.conj.call(null,set__$1,value);
}
});
/**
 * Create a function to check if form group should be rerendered.
 *   A group is rerendered if its open/close status changes or it is
 *   open and its data has changed.
 */
ote.ui.form.form_group_should_update_QMARK_ = (function ote$ui$form$form_group_should_update_QMARK_(p__51132){
var map__51133 = p__51132;
var map__51133__$1 = ((((!((map__51133 == null)))?((((map__51133.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51133.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51133):map__51133);
var group = map__51133__$1;
var schemas = cljs.core.get.call(null,map__51133__$1,new cljs.core.Keyword(null,"schemas","schemas",575070579));
var opts = cljs.core.get.call(null,map__51133__$1,new cljs.core.Keyword(null,"options","options",99638489));
var read_fn = cljs.core.apply.call(null,cljs.core.juxt,cljs.core.map.call(null,((function (map__51133,map__51133__$1,group,schemas,opts){
return (function (p__51135){
var map__51136 = p__51135;
var map__51136__$1 = ((((!((map__51136 == null)))?((((map__51136.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51136.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51136):map__51136);
var name = cljs.core.get.call(null,map__51136__$1,new cljs.core.Keyword(null,"name","name",1843675177));
var read = cljs.core.get.call(null,map__51136__$1,new cljs.core.Keyword(null,"read","read",1140058661));
var or__30175__auto__ = read;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return name;
}
});})(map__51133,map__51133__$1,group,schemas,opts))
,schemas));
return ((function (read_fn,map__51133,map__51133__$1,group,schemas,opts){
return (function (_,old_argv,new_argv){
var vec__51138 = old_argv;
var ___$1 = cljs.core.nth.call(null,vec__51138,(0),null);
var map__51141 = cljs.core.nth.call(null,vec__51138,(1),null);
var map__51141__$1 = ((((!((map__51141 == null)))?((((map__51141.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51141.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51141):map__51141);
var old_form_options = map__51141__$1;
var old_closed_groups = cljs.core.get.call(null,map__51141__$1,new cljs.core.Keyword(null,"closed-groups","closed-groups",1659046952));
var old_data = cljs.core.get.call(null,map__51141__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var old_group = cljs.core.nth.call(null,vec__51138,(2),null);
var vec__51142 = new_argv;
var ___$2 = cljs.core.nth.call(null,vec__51142,(0),null);
var map__51145 = cljs.core.nth.call(null,vec__51142,(1),null);
var map__51145__$1 = ((((!((map__51145 == null)))?((((map__51145.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51145.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51145):map__51145);
var new_form_options = map__51145__$1;
var new_closed_groups = cljs.core.get.call(null,map__51145__$1,new cljs.core.Keyword(null,"closed-groups","closed-groups",1659046952));
var new_data = cljs.core.get.call(null,map__51145__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var map__51146 = cljs.core.nth.call(null,vec__51142,(2),null);
var map__51146__$1 = ((((!((map__51146 == null)))?((((map__51146.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51146.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51146):map__51146);
var new_groups = map__51146__$1;
var label = cljs.core.get.call(null,map__51146__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var old_closed = old_closed_groups.call(null,label);
var new_closed = new_closed_groups.call(null,label);
var old_group_data = read_fn.call(null,old_data);
var new_group_data = read_fn.call(null,new_data);
return (cljs.core.not_EQ_.call(null,old_closed,new_closed)) || ((cljs.core.not.call(null,new_closed)) && (cljs.core.not_EQ_.call(null,old_group_data,new_group_data)));
});
;})(read_fn,map__51133,map__51133__$1,group,schemas,opts))
});
ote.ui.form.form_group_ui = (function ote$ui$form$form_group_ui(form_options,group){
return reagent.core.create_class.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"should-component-update","should-component-update",2040868163),ote.ui.form.form_group_should_update_QMARK_.call(null,group),new cljs.core.Keyword(null,"reagent-render","reagent-render",-985383853),(function (p__51151,p__51152){
var map__51153 = p__51151;
var map__51153__$1 = ((((!((map__51153 == null)))?((((map__51153.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51153.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51153):map__51153);
var form_options__$1 = map__51153__$1;
var name__GT_label = cljs.core.get.call(null,map__51153__$1,new cljs.core.Keyword(null,"name->label","name->label",-1609632952));
var update_field_fn = cljs.core.get.call(null,map__51153__$1,new cljs.core.Keyword(null,"update-field-fn","update-field-fn",-245960220));
var can_edit_QMARK_ = cljs.core.get.call(null,map__51153__$1,new cljs.core.Keyword(null,"can-edit?","can-edit?",-1977832208));
var focus = cljs.core.get.call(null,map__51153__$1,new cljs.core.Keyword(null,"focus","focus",234677911));
var update_form = cljs.core.get.call(null,map__51153__$1,new cljs.core.Keyword(null,"update-form","update-form",475718790));
var data = cljs.core.get.call(null,map__51153__$1,new cljs.core.Keyword(null,"data","data",-232669377));
var closed_groups = cljs.core.get.call(null,map__51153__$1,new cljs.core.Keyword(null,"closed-groups","closed-groups",1659046952));
var map__51154 = p__51152;
var map__51154__$1 = ((((!((map__51154 == null)))?((((map__51154.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51154.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51154):map__51154);
var group__$1 = map__51154__$1;
var label = cljs.core.get.call(null,map__51154__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var schemas = cljs.core.get.call(null,map__51154__$1,new cljs.core.Keyword(null,"schemas","schemas",575070579));
var options = cljs.core.get.call(null,map__51154__$1,new cljs.core.Keyword(null,"options","options",99638489));
var map__51157 = data;
var map__51157__$1 = ((((!((map__51157 == null)))?((((map__51157.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51157.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51157):map__51157);
var modified = cljs.core.get.call(null,map__51157__$1,new cljs.core.Keyword("ote.ui.form","modified","ote.ui.form/modified",-37635778));
var errors = cljs.core.get.call(null,map__51157__$1,new cljs.core.Keyword("ote.ui.form","errors","ote.ui.form/errors",-2101869710));
var warnings = cljs.core.get.call(null,map__51157__$1,new cljs.core.Keyword("ote.ui.form","warnings","ote.ui.form/warnings",-416419939));
var notices = cljs.core.get.call(null,map__51157__$1,new cljs.core.Keyword("ote.ui.form","notices","ote.ui.form/notices",-1558257202));
var columns = (function (){var or__30175__auto__ = new cljs.core.Keyword(null,"columns","columns",1998437288).cljs$core$IFn$_invoke$arity$1(options);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return (1);
}
})();
var classes = cljs.core.get.call(null,ote.ui.form.col_classes,columns);
var schemas__$1 = ote.ui.form.with_automatic_labels.call(null,name__GT_label,schemas);
var style = ((cljs.core._EQ_.call(null,new cljs.core.Keyword(null,"row","row",-570139521),new cljs.core.Keyword(null,"layout","layout",-2120940921).cljs$core$IFn$_invoke$arity$1(options)))?ote.style.form.form_group_row:ote.style.form.form_group_column);
var group_component = new cljs.core.PersistentVector(null, 13, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.form.group_ui,style,schemas__$1,data,update_field_fn,can_edit_QMARK_,cljs.core.deref.call(null,focus),((function (map__51157,map__51157__$1,modified,errors,warnings,notices,columns,classes,schemas__$1,style,map__51153,map__51153__$1,form_options__$1,name__GT_label,update_field_fn,can_edit_QMARK_,focus,update_form,data,closed_groups,map__51154,map__51154__$1,group__$1,label,schemas,options){
return (function (p1__51150_SHARP_){
return cljs.core.reset_BANG_.call(null,focus,p1__51150_SHARP_);
});})(map__51157,map__51157__$1,modified,errors,warnings,notices,columns,classes,schemas__$1,style,map__51153,map__51153__$1,form_options__$1,name__GT_label,update_field_fn,can_edit_QMARK_,focus,update_form,data,closed_groups,map__51154,map__51154__$1,group__$1,label,schemas,options))
,modified,errors,warnings,notices,update_form], null);
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-group-container","div.form-group-container",-273972733),stylefy.core.use_style.call(null,ote.style.form.form_group_container,new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword("stylefy.core","with-classes","stylefy.core/with-classes",1994369003),classes], null)),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.card,new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"z-depth","z-depth",-334122453),(1),new cljs.core.Keyword(null,"expanded","expanded",-3020742),cljs.core.not.call(null,closed_groups.call(null,label)),new cljs.core.Keyword(null,"on-expand-change","on-expand-change",1735969501),((function (map__51157,map__51157__$1,modified,errors,warnings,notices,columns,classes,schemas__$1,style,group_component,map__51153,map__51153__$1,form_options__$1,name__GT_label,update_field_fn,can_edit_QMARK_,focus,update_form,data,closed_groups,map__51154,map__51154__$1,group__$1,label,schemas,options){
return (function (){
return update_form.call(null,cljs.core.update.call(null,data,new cljs.core.Keyword("ote.ui.form","closed-groups","ote.ui.form/closed-groups",904145240),ote.ui.form.toggle,label));
});})(map__51157,map__51157__$1,modified,errors,warnings,notices,columns,classes,schemas__$1,style,group_component,map__51153,map__51153__$1,form_options__$1,name__GT_label,update_field_fn,can_edit_QMARK_,focus,update_form,data,closed_groups,map__51154,map__51154__$1,group__$1,label,schemas,options))
], null),(cljs.core.truth_(label)?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.card_header,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"title","title",636505583),label,new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"padding-bottom","padding-bottom",-1899795591),"0px"], null),new cljs.core.Keyword(null,"title-style","title-style",-1964867876),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"font-weight","font-weight",2085804583),"bold"], null),new cljs.core.Keyword(null,"show-expandable-button","show-expandable-button",-170959680),true], null)], null):null),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.card_text,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"style","style",-496642736),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"padding-top","padding-top",1929675955),"0px"], null),new cljs.core.Keyword(null,"expandable","expandable",-704609097),true], null),group_component], null),(function (){var temp__5290__auto__ = (function (){var and__30163__auto__ = cljs.core.not.call(null,closed_groups.call(null,label));
if(and__30163__auto__){
return new cljs.core.Keyword(null,"actions","actions",-812656882).cljs$core$IFn$_invoke$arity$1(options);
} else {
return and__30163__auto__;
}
})();
if(cljs.core.truth_(temp__5290__auto__)){
var actions = temp__5290__auto__;
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_react_material_ui.reagent.card_actions,reagent.core.as_element.call(null,actions)], null);
} else {
return null;
}
})()], null)], null);
})], null));
});
/**
 * Generic form component that takes `options`, a vector of field `schemas` and the
 *   current state of the form `data`.
 * 
 *   Supported options:
 * 
 *   :update!      Function to call when the form data changes
 *   :footer-fn    Optional function to create a footer component that is shown under the form.
 *              Receives the current form state with validation added as parameter.
 *   :class        Optional extra CSS classes for the form
 *   :name->label  Optional function to automatically generate a `:label` for field schemas that
 *              is based on the `:name` of the field. This is useful to automatically take
 *              a translation as the label.
 *   
 */
ote.ui.form.form = (function ote$ui$form$form(_,___$1,data){
var focus = cljs.core.atom.call(null,null);
var latest_data = cljs.core.atom.call(null,data);
return reagent.core.create_class.call(null,new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"component-will-receive-props","component-will-receive-props",551608157),((function (focus,latest_data){
return (function (___$2,p__51159){
var vec__51160 = p__51159;
var ___$3 = cljs.core.nth.call(null,vec__51160,(0),null);
var ___$4 = cljs.core.nth.call(null,vec__51160,(1),null);
var ___$5 = cljs.core.nth.call(null,vec__51160,(2),null);
var new_data = cljs.core.nth.call(null,vec__51160,(3),null);
return cljs.core.reset_BANG_.call(null,latest_data,new_data);
});})(focus,latest_data))
,new cljs.core.Keyword(null,"reagent-render","reagent-render",-985383853),((function (focus,latest_data){
return (function (p__51163,schemas,p__51164){
var map__51165 = p__51163;
var map__51165__$1 = ((((!((map__51165 == null)))?((((map__51165.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51165.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51165):map__51165);
var options = map__51165__$1;
var update_BANG_ = cljs.core.get.call(null,map__51165__$1,new cljs.core.Keyword(null,"update!","update!",-1453508586));
var class$ = cljs.core.get.call(null,map__51165__$1,new cljs.core.Keyword(null,"class","class",-2030961996));
var footer_fn = cljs.core.get.call(null,map__51165__$1,new cljs.core.Keyword(null,"footer-fn","footer-fn",1907236041));
var can_edit_QMARK_ = cljs.core.get.call(null,map__51165__$1,new cljs.core.Keyword(null,"can-edit?","can-edit?",-1977832208));
var label = cljs.core.get.call(null,map__51165__$1,new cljs.core.Keyword(null,"label","label",1718410804));
var name__GT_label = cljs.core.get.call(null,map__51165__$1,new cljs.core.Keyword(null,"name->label","name->label",-1609632952));
var map__51166 = p__51164;
var map__51166__$1 = ((((!((map__51166 == null)))?((((map__51166.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51166.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51166):map__51166);
var data__$1 = map__51166__$1;
var modified = cljs.core.get.call(null,map__51166__$1,new cljs.core.Keyword("ote.ui.form","modified","ote.ui.form/modified",-37635778));
var map__51169 = ote.ui.form.validate.call(null,data__$1,schemas);
var map__51169__$1 = ((((!((map__51169 == null)))?((((map__51169.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__51169.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__51169):map__51169);
var validated_data = map__51169__$1;
var errors = cljs.core.get.call(null,map__51169__$1,new cljs.core.Keyword("ote.ui.form","errors","ote.ui.form/errors",-2101869710));
var warnings = cljs.core.get.call(null,map__51169__$1,new cljs.core.Keyword("ote.ui.form","warnings","ote.ui.form/warnings",-416419939));
var notices = cljs.core.get.call(null,map__51169__$1,new cljs.core.Keyword("ote.ui.form","notices","ote.ui.form/notices",-1558257202));
var can_edit_QMARK___$1 = ((!((can_edit_QMARK_ == null)))?can_edit_QMARK_:true);
var update_form = ((function (map__51169,map__51169__$1,validated_data,errors,warnings,notices,can_edit_QMARK___$1,map__51165,map__51165__$1,options,update_BANG_,class$,footer_fn,can_edit_QMARK_,label,name__GT_label,map__51166,map__51166__$1,data__$1,modified,focus,latest_data){
return (function (new_data){
if(cljs.core.truth_(update_BANG_)){
} else {
throw (new Error(["Assert failed: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1([":update! missing, options:",cljs.core.str.cljs$core$IFn$_invoke$arity$1(cljs.core.pr_str.call(null,options))].join('')),"\n","update!"].join('')));
}

return update_BANG_.call(null,cljs.core.assoc.call(null,ote.ui.form.validate.call(null,ote.ui.form.modification_time.call(null,new_data),schemas),new cljs.core.Keyword("ote.ui.form","modified","ote.ui.form/modified",-37635778),cljs.core.conj.call(null,(function (){var or__30175__auto__ = new cljs.core.Keyword("ote.ui.form","modified","ote.ui.form/modified",-37635778).cljs$core$IFn$_invoke$arity$1(new_data);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return cljs.core.PersistentHashSet.EMPTY;
}
})(),cljs.core.name)));
});})(map__51169,map__51169__$1,validated_data,errors,warnings,notices,can_edit_QMARK___$1,map__51165,map__51165__$1,options,update_BANG_,class$,footer_fn,can_edit_QMARK_,label,name__GT_label,map__51166,map__51166__$1,data__$1,modified,focus,latest_data))
;
var update_field_fn = ((function (map__51169,map__51169__$1,validated_data,errors,warnings,notices,can_edit_QMARK___$1,update_form,map__51165,map__51165__$1,options,update_BANG_,class$,footer_fn,can_edit_QMARK_,label,name__GT_label,map__51166,map__51166__$1,data__$1,modified,focus,latest_data){
return (function (name_or_write,value){
var data__$2 = cljs.core.deref.call(null,latest_data);
var new_data = (((name_or_write instanceof cljs.core.Keyword))?cljs.core.assoc.call(null,data__$2,name_or_write,value):name_or_write.call(null,data__$2,value));
return update_form.call(null,new_data);
});})(map__51169,map__51169__$1,validated_data,errors,warnings,notices,can_edit_QMARK___$1,update_form,map__51165,map__51165__$1,options,update_BANG_,class$,footer_fn,can_edit_QMARK_,label,name__GT_label,map__51166,map__51166__$1,data__$1,modified,focus,latest_data))
;
var closed_groups = new cljs.core.Keyword("ote.ui.form","closed-groups","ote.ui.form/closed-groups",904145240).cljs$core$IFn$_invoke$arity$2(data__$1,cljs.core.PersistentHashSet.EMPTY);
return new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form","div.form",-425204148),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"class","class",-2030961996),class$], null),(cljs.core.truth_(label)?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"h3.form-label","h3.form-label",2037496635),label], null):null),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"span.form-groups","span.form-groups",341913088),cljs.core.doall.call(null,cljs.core.map_indexed.call(null,((function (map__51169,map__51169__$1,validated_data,errors,warnings,notices,can_edit_QMARK___$1,update_form,update_field_fn,closed_groups,map__51165,map__51165__$1,options,update_BANG_,class$,footer_fn,can_edit_QMARK_,label,name__GT_label,map__51166,map__51166__$1,data__$1,modified,focus,latest_data){
return (function (i,form_group){
return cljs.core.with_meta(new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [ote.ui.form.form_group_ui,new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"name->label","name->label",-1609632952),name__GT_label,new cljs.core.Keyword(null,"data","data",-232669377),validated_data,new cljs.core.Keyword(null,"update-field-fn","update-field-fn",-245960220),update_field_fn,new cljs.core.Keyword(null,"can-edit?","can-edit?",-1977832208),can_edit_QMARK___$1,new cljs.core.Keyword(null,"focus","focus",234677911),focus,new cljs.core.Keyword(null,"update-form","update-form",475718790),update_form,new cljs.core.Keyword(null,"closed-groups","closed-groups",1659046952),closed_groups], null),form_group], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"key","key",-1516042587),i], null));
});})(map__51169,map__51169__$1,validated_data,errors,warnings,notices,can_edit_QMARK___$1,update_form,update_field_fn,closed_groups,map__51165,map__51165__$1,options,update_BANG_,class$,footer_fn,can_edit_QMARK_,label,name__GT_label,map__51166,map__51166__$1,data__$1,modified,focus,latest_data))
,schemas))], null),(function (){var temp__5290__auto__ = (cljs.core.truth_(footer_fn)?footer_fn.call(null,cljs.core.assoc.call(null,validated_data,new cljs.core.Keyword("ote.ui.form","schema","ote.ui.form/schema",1420803217),schemas)):null);
if(cljs.core.truth_(temp__5290__auto__)){
var footer = temp__5290__auto__;
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.form-footer.row","div.form-footer.row",117685955),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"div.col-md-12","div.col-md-12",-1894925992),footer], null)], null);
} else {
return null;
}
})()], null);
});})(focus,latest_data))
], null));
});

//# sourceMappingURL=form.js.map?rel=1510137290671
