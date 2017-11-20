// Compiled by ClojureScript 1.9.908 {}
goog.provide('ote.time');
goog.require('cljs.core');
goog.require('goog.string');
goog.require('cljs_time.core');
goog.require('cljs_time.format');
goog.require('cljs_time.local');
goog.require('cljs_time.coerce');
goog.require('specql.data_types');
goog.require('cljs.spec.alpha');
goog.require('clojure.string');

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
ote.time.Time = (function (hours,minutes,seconds,__meta,__extmap,__hash){
this.hours = hours;
this.minutes = minutes;
this.seconds = seconds;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
ote.time.Time.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

ote.time.Time.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k41923,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__41927 = k41923;
var G__41927__$1 = (((G__41927 instanceof cljs.core.Keyword))?G__41927.fqn:null);
switch (G__41927__$1) {
case "hours":
return self__.hours;

break;
case "minutes":
return self__.minutes;

break;
case "seconds":
return self__.seconds;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k41923,else__30866__auto__);

}
});

ote.time.Time.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#ote.time.Time{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"hours","hours",58380855),self__.hours],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"minutes","minutes",1319166394),self__.minutes],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"seconds","seconds",-445266194),self__.seconds],null))], null),self__.__extmap));
});

ote.time.Time.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__41922){
var self__ = this;
var G__41922__$1 = this;
return (new cljs.core.RecordIter((0),G__41922__$1,3,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"hours","hours",58380855),new cljs.core.Keyword(null,"minutes","minutes",1319166394),new cljs.core.Keyword(null,"seconds","seconds",-445266194)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

ote.time.Time.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

ote.time.Time.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new ote.time.Time(self__.hours,self__.minutes,self__.seconds,self__.__meta,self__.__extmap,self__.__hash));
});

ote.time.Time.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (3 + cljs.core.count.call(null,self__.__extmap));
});

ote.time.Time.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (-1575790100 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

ote.time.Time.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this41924,other41925){
var self__ = this;
var this41924__$1 = this;
return (!((other41925 == null))) && ((this41924__$1.constructor === other41925.constructor)) && (cljs.core._EQ_.call(null,this41924__$1.hours,other41925.hours)) && (cljs.core._EQ_.call(null,this41924__$1.minutes,other41925.minutes)) && (cljs.core._EQ_.call(null,this41924__$1.seconds,other41925.seconds)) && (cljs.core._EQ_.call(null,this41924__$1.__extmap,other41925.__extmap));
});

ote.time.Time.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"seconds","seconds",-445266194),null,new cljs.core.Keyword(null,"hours","hours",58380855),null,new cljs.core.Keyword(null,"minutes","minutes",1319166394),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new ote.time.Time(self__.hours,self__.minutes,self__.seconds,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

ote.time.Time.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__41922){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__41928 = cljs.core.keyword_identical_QMARK_;
var expr__41929 = k__30871__auto__;
if(cljs.core.truth_(pred__41928.call(null,new cljs.core.Keyword(null,"hours","hours",58380855),expr__41929))){
return (new ote.time.Time(G__41922,self__.minutes,self__.seconds,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__41928.call(null,new cljs.core.Keyword(null,"minutes","minutes",1319166394),expr__41929))){
return (new ote.time.Time(self__.hours,G__41922,self__.seconds,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__41928.call(null,new cljs.core.Keyword(null,"seconds","seconds",-445266194),expr__41929))){
return (new ote.time.Time(self__.hours,self__.minutes,G__41922,self__.__meta,self__.__extmap,null));
} else {
return (new ote.time.Time(self__.hours,self__.minutes,self__.seconds,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__41922),null));
}
}
}
});

ote.time.Time.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"hours","hours",58380855),self__.hours],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"minutes","minutes",1319166394),self__.minutes],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"seconds","seconds",-445266194),self__.seconds],null))], null),self__.__extmap));
});

ote.time.Time.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__41922){
var self__ = this;
var this__30862__auto____$1 = this;
return (new ote.time.Time(self__.hours,self__.minutes,self__.seconds,G__41922,self__.__extmap,self__.__hash));
});

ote.time.Time.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

ote.time.Time.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"hours","hours",1698912382,null),new cljs.core.Symbol(null,"minutes","minutes",-1335269375,null),new cljs.core.Symbol(null,"seconds","seconds",1195265333,null)], null);
});

ote.time.Time.cljs$lang$type = true;

ote.time.Time.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"ote.time/Time");
});

ote.time.Time.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"ote.time/Time");
});

ote.time.__GT_Time = (function ote$time$__GT_Time(hours,minutes,seconds){
return (new ote.time.Time(hours,minutes,seconds,null,null,null));
});

ote.time.map__GT_Time = (function ote$time$map__GT_Time(G__41926){
return (new ote.time.Time(new cljs.core.Keyword(null,"hours","hours",58380855).cljs$core$IFn$_invoke$arity$1(G__41926),new cljs.core.Keyword(null,"minutes","minutes",1319166394).cljs$core$IFn$_invoke$arity$1(G__41926),new cljs.core.Keyword(null,"seconds","seconds",-445266194).cljs$core$IFn$_invoke$arity$1(G__41926),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__41926,new cljs.core.Keyword(null,"hours","hours",58380855),new cljs.core.Keyword(null,"minutes","minutes",1319166394),new cljs.core.Keyword(null,"seconds","seconds",-445266194))),null));
});

cljs.spec.alpha.def_impl.call(null,new cljs.core.Keyword("specql.data-types","time","specql.data-types/time",1056759352),cljs.core.list(new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"%","%",-950237169,null)], null),cljs.core.list(new cljs.core.Symbol("cljs.core","instance?","cljs.core/instance?",2044751870,null),new cljs.core.Symbol("ote.time","Time","ote.time/Time",-2091279189,null),new cljs.core.Symbol(null,"%","%",-950237169,null))),(function (p1__41932_SHARP_){
return (p1__41932_SHARP_ instanceof ote.time.Time);
}));
ote.time.format_timestamp_for_ui = (function ote$time$format_timestamp_for_ui(time){
if((time == null)){
return " ";
} else {
return cljs_time.format.unparse.call(null,cljs_time.format.formatter.call(null,"dd.MM.yyyy HH:mm"),cljs_time.core.to_default_time_zone.call(null,time));
}
});
ote.time.format_js_time = (function ote$time$format_js_time(time){
if((time == null)){
return "";
} else {
return cljs_time.format.unparse.call(null,cljs_time.format.formatter.call(null,"HH:mm:ss"),cljs_time.core.to_default_time_zone.call(null,time));
}
});
ote.time.to_js_time = (function ote$time$to_js_time(db_time){
var hours = cljs.core.get.call(null,db_time,new cljs.core.Keyword(null,"hours","hours",58380855));
var minutes = cljs.core.get.call(null,db_time,new cljs.core.Keyword(null,"minutes","minutes",1319166394));
var seconds = cljs.core.get.call(null,db_time,new cljs.core.Keyword(null,"seconds","seconds",-445266194));
return (new Date(cljs_time.coerce.to_long.call(null,cljs_time.local.to_local_date_time.call(null,cljs_time.core.today_at.call(null,hours,minutes,seconds)))));
});
ote.time.format_time_full = (function ote$time$format_time_full(p__41933){
var map__41934 = p__41933;
var map__41934__$1 = ((((!((map__41934 == null)))?((((map__41934.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__41934.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__41934):map__41934);
var hours = cljs.core.get.call(null,map__41934__$1,new cljs.core.Keyword(null,"hours","hours",58380855));
var minutes = cljs.core.get.call(null,map__41934__$1,new cljs.core.Keyword(null,"minutes","minutes",1319166394));
var seconds = cljs.core.get.call(null,map__41934__$1,new cljs.core.Keyword(null,"seconds","seconds",-445266194));
return goog.string.format("%02d:%02d:%02d",hours,minutes,seconds);
});
ote.time.format_time = (function ote$time$format_time(p__41936){
var map__41937 = p__41936;
var map__41937__$1 = ((((!((map__41937 == null)))?((((map__41937.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__41937.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__41937):map__41937);
var time = map__41937__$1;
var hours = cljs.core.get.call(null,map__41937__$1,new cljs.core.Keyword(null,"hours","hours",58380855));
var minutes = cljs.core.get.call(null,map__41937__$1,new cljs.core.Keyword(null,"minutes","minutes",1319166394));
var seconds = cljs.core.get.call(null,map__41937__$1,new cljs.core.Keyword(null,"seconds","seconds",-445266194));
if(cljs.core.truth_(seconds)){
return ote.time.format_time_full.call(null,time);
} else {
return goog.string.format("%02d:%02d",hours,minutes);
}
});
ote.time.parse_time = (function ote$time$parse_time(string){
var vec__41940 = cljs.core.map.call(null,(function (p1__41939_SHARP_){
return parseInt(p1__41939_SHARP_);
}),clojure.string.split.call(null,string,/:/));
var h = cljs.core.nth.call(null,vec__41940,(0),null);
var m = cljs.core.nth.call(null,vec__41940,(1),null);
var s = cljs.core.nth.call(null,vec__41940,(2),null);
return ote.time.__GT_Time.call(null,h,m,s);
});

/**
 * @interface
 */
ote.time.DateFields = function(){};

/**
 * Return date fields as a map of data.
 */
ote.time.date_fields = (function ote$time$date_fields(this$){
if((!((this$ == null))) && (!((this$.ote$time$DateFields$date_fields$arity$1 == null)))){
return this$.ote$time$DateFields$date_fields$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (ote.time.date_fields[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (ote.time.date_fields["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"DateFields.date-fields",this$);
}
}
}
});

Date.prototype.ote$time$DateFields$ = cljs.core.PROTOCOL_SENTINEL;

Date.prototype.ote$time$DateFields$date_fields$arity$1 = (function (this$){
var this$__$1 = this;
return new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword("ote.time","date","ote.time/date",-489417747),this$__$1.getDate(),new cljs.core.Keyword("ote.time","month","ote.time/month",1555935182),(this$__$1.getMonth() + (1)),new cljs.core.Keyword("ote.time","year","ote.time/year",-1443298674),((1900) + this$__$1.getYear()),new cljs.core.Keyword("ote.time","hours","ote.time/hours",1434715348),this$__$1.getHours(),new cljs.core.Keyword("ote.time","minutes","ote.time/minutes",-2135812829),this$__$1.getMinutes(),new cljs.core.Keyword("ote.time","seconds","ote.time/seconds",-1285384701),this$__$1.getSeconds()], null);
});
/**
 * Format given date in human readable format.
 */
ote.time.format_date = (function ote$time$format_date(date){
var map__41943 = ote.time.date_fields.call(null,date);
var map__41943__$1 = ((((!((map__41943 == null)))?((((map__41943.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__41943.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__41943):map__41943);
var date__$1 = cljs.core.get.call(null,map__41943__$1,new cljs.core.Keyword("ote.time","date","ote.time/date",-489417747));
var month = cljs.core.get.call(null,map__41943__$1,new cljs.core.Keyword("ote.time","month","ote.time/month",1555935182));
var year = cljs.core.get.call(null,map__41943__$1,new cljs.core.Keyword("ote.time","year","ote.time/year",-1443298674));
return goog.string.format("%02d.%02d.%d",date__$1,month,year);
});
/**
 * Format given date in ISO-8601 format.
 */
ote.time.format_date_iso_8601 = (function ote$time$format_date_iso_8601(date){
var map__41945 = ote.time.date_fields.call(null,date);
var map__41945__$1 = ((((!((map__41945 == null)))?((((map__41945.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__41945.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__41945):map__41945);
var date__$1 = cljs.core.get.call(null,map__41945__$1,new cljs.core.Keyword("ote.time","date","ote.time/date",-489417747));
var month = cljs.core.get.call(null,map__41945__$1,new cljs.core.Keyword("ote.time","month","ote.time/month",1555935182));
var year = cljs.core.get.call(null,map__41945__$1,new cljs.core.Keyword("ote.time","year","ote.time/year",-1443298674));
return goog.string.format("%d-%02d-%02d",year,month,date__$1);
});

//# sourceMappingURL=time.js.map?rel=1510137279121
