// Compiled by ClojureScript 1.9.908 {}
goog.provide('cljs_time.core');
goog.require('cljs.core');
goog.require('cljs_time.internal.core');
goog.require('clojure.string');
goog.require('goog.date.Interval');
goog.require('goog.date');
goog.require('goog.date.Date');
goog.require('goog.date.DateTime');
goog.require('goog.date.UtcDateTime');
cljs_time.core.deprecated = (function cljs_time$core$deprecated(message){
return cljs.core.println.call(null,"DEPRECATION WARNING: ",message);
});
/**
 * **Note:** Equality in goog.date.* (and also with plain
 * javascript dates) is not the same as in Joda/clj-time. Two date
 * objects representing the same instant in time in goog.date.* are not
 * equal.
 * 
 * If you need to test for equality use either `cljs-time.core/=`, or
 * optionally you can require the `cljs-time.extend` namespace which will
 * extend the goog.date.* datatypes, so that clojure.core/= works as
 * expected.
 */
cljs_time.core._EQ_ = cljs_time.internal.core._EQ_;

/**
 * Interface for various date time functions
 * @interface
 */
cljs_time.core.DateTimeProtocol = function(){};

/**
 * Return the year component of the given date/time.
 */
cljs_time.core.year = (function cljs_time$core$year(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$year$arity$1 == null)))){
return this$.cljs_time$core$DateTimeProtocol$year$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.year[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.year["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.year",this$);
}
}
}
});

/**
 * Return the month component of the given date/time.
 */
cljs_time.core.month = (function cljs_time$core$month(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$month$arity$1 == null)))){
return this$.cljs_time$core$DateTimeProtocol$month$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.month[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.month["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.month",this$);
}
}
}
});

/**
 * Return the day of month component of the given date/time.
 */
cljs_time.core.day = (function cljs_time$core$day(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$day$arity$1 == null)))){
return this$.cljs_time$core$DateTimeProtocol$day$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.day[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.day["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.day",this$);
}
}
}
});

/**
 * Return the day of week component of the given date/time. Monday is 1 and Sunday is 7
 */
cljs_time.core.day_of_week = (function cljs_time$core$day_of_week(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$day_of_week$arity$1 == null)))){
return this$.cljs_time$core$DateTimeProtocol$day_of_week$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.day_of_week[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.day_of_week["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.day-of-week",this$);
}
}
}
});

/**
 * Return the hour of day component of the given date/time. A time of 12:01am will have an hour component of 0.
 */
cljs_time.core.hour = (function cljs_time$core$hour(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$hour$arity$1 == null)))){
return this$.cljs_time$core$DateTimeProtocol$hour$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.hour[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.hour["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.hour",this$);
}
}
}
});

/**
 * Return the minute of hour component of the given date/time.
 */
cljs_time.core.minute = (function cljs_time$core$minute(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$minute$arity$1 == null)))){
return this$.cljs_time$core$DateTimeProtocol$minute$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.minute[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.minute["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.minute",this$);
}
}
}
});

/**
 * Return the second of minute component of the given date/time.
 */
cljs_time.core.sec = (function cljs_time$core$sec(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$sec$arity$1 == null)))){
return this$.cljs_time$core$DateTimeProtocol$sec$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.sec[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.sec["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.sec",this$);
}
}
}
});

/**
 * Return the second of minute component of the given date/time.
 */
cljs_time.core.second = (function cljs_time$core$second(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$second$arity$1 == null)))){
return this$.cljs_time$core$DateTimeProtocol$second$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.second[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.second["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.second",this$);
}
}
}
});

/**
 * Return the millisecond of second component of the given date/time.
 */
cljs_time.core.milli = (function cljs_time$core$milli(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$milli$arity$1 == null)))){
return this$.cljs_time$core$DateTimeProtocol$milli$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.milli[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.milli["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.milli",this$);
}
}
}
});

/**
 * Returns true if DateTime 'this' is strictly equal to date/time 'that'.
 */
cljs_time.core.equal_QMARK_ = (function cljs_time$core$equal_QMARK_(this$,that){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$equal_QMARK_$arity$2 == null)))){
return this$.cljs_time$core$DateTimeProtocol$equal_QMARK_$arity$2(this$,that);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.equal_QMARK_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$,that);
} else {
var m__30909__auto____$1 = (cljs_time.core.equal_QMARK_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$,that);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.equal?",this$);
}
}
}
});

/**
 * Returns true if DateTime 'this' is strictly after date/time 'that'.
 */
cljs_time.core.after_QMARK_ = (function cljs_time$core$after_QMARK_(this$,that){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$after_QMARK_$arity$2 == null)))){
return this$.cljs_time$core$DateTimeProtocol$after_QMARK_$arity$2(this$,that);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.after_QMARK_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$,that);
} else {
var m__30909__auto____$1 = (cljs_time.core.after_QMARK_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$,that);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.after?",this$);
}
}
}
});

/**
 * Returns true if DateTime 'this' is strictly before date/time 'that'.
 */
cljs_time.core.before_QMARK_ = (function cljs_time$core$before_QMARK_(this$,that){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$before_QMARK_$arity$2 == null)))){
return this$.cljs_time$core$DateTimeProtocol$before_QMARK_$arity$2(this$,that);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.before_QMARK_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$,that);
} else {
var m__30909__auto____$1 = (cljs_time.core.before_QMARK_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$,that);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.before?",this$);
}
}
}
});

/**
 * Returns a new date/time corresponding to the given date/time moved forwards by the given Period(s).
 */
cljs_time.core.plus_ = (function cljs_time$core$plus_(this$,period){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$plus_$arity$2 == null)))){
return this$.cljs_time$core$DateTimeProtocol$plus_$arity$2(this$,period);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.plus_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$,period);
} else {
var m__30909__auto____$1 = (cljs_time.core.plus_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$,period);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.plus-",this$);
}
}
}
});

/**
 * Returns a new date/time corresponding to the given date/time moved backwards by the given Period(s).
 */
cljs_time.core.minus_ = (function cljs_time$core$minus_(this$,period){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$minus_$arity$2 == null)))){
return this$.cljs_time$core$DateTimeProtocol$minus_$arity$2(this$,period);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.minus_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$,period);
} else {
var m__30909__auto____$1 = (cljs_time.core.minus_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$,period);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.minus-",this$);
}
}
}
});

/**
 * Returns the first day of the month
 */
cljs_time.core.first_day_of_the_month_ = (function cljs_time$core$first_day_of_the_month_(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$first_day_of_the_month_$arity$1 == null)))){
return this$.cljs_time$core$DateTimeProtocol$first_day_of_the_month_$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.first_day_of_the_month_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.first_day_of_the_month_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.first-day-of-the-month-",this$);
}
}
}
});

/**
 * Returns the last day of the month
 */
cljs_time.core.last_day_of_the_month_ = (function cljs_time$core$last_day_of_the_month_(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$last_day_of_the_month_$arity$1 == null)))){
return this$.cljs_time$core$DateTimeProtocol$last_day_of_the_month_$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.last_day_of_the_month_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.last_day_of_the_month_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.last-day-of-the-month-",this$);
}
}
}
});

/**
 * Returs the number of weeks in the year
 */
cljs_time.core.week_number_of_year = (function cljs_time$core$week_number_of_year(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$DateTimeProtocol$week_number_of_year$arity$1 == null)))){
return this$.cljs_time$core$DateTimeProtocol$week_number_of_year$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.week_number_of_year[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.week_number_of_year["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"DateTimeProtocol.week-number-of-year",this$);
}
}
}
});


/**
 * Interface for in-<time unit> functions
 * @interface
 */
cljs_time.core.InTimeUnitProtocol = function(){};

/**
 * Return the time in milliseconds.
 */
cljs_time.core.in_millis = (function cljs_time$core$in_millis(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$InTimeUnitProtocol$in_millis$arity$1 == null)))){
return this$.cljs_time$core$InTimeUnitProtocol$in_millis$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.in_millis[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.in_millis["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"InTimeUnitProtocol.in-millis",this$);
}
}
}
});

/**
 * Return the time in seconds.
 */
cljs_time.core.in_seconds = (function cljs_time$core$in_seconds(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$InTimeUnitProtocol$in_seconds$arity$1 == null)))){
return this$.cljs_time$core$InTimeUnitProtocol$in_seconds$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.in_seconds[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.in_seconds["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"InTimeUnitProtocol.in-seconds",this$);
}
}
}
});

/**
 * Return the time in minutes.
 */
cljs_time.core.in_minutes = (function cljs_time$core$in_minutes(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$InTimeUnitProtocol$in_minutes$arity$1 == null)))){
return this$.cljs_time$core$InTimeUnitProtocol$in_minutes$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.in_minutes[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.in_minutes["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"InTimeUnitProtocol.in-minutes",this$);
}
}
}
});

/**
 * Return the time in hours.
 */
cljs_time.core.in_hours = (function cljs_time$core$in_hours(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$InTimeUnitProtocol$in_hours$arity$1 == null)))){
return this$.cljs_time$core$InTimeUnitProtocol$in_hours$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.in_hours[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.in_hours["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"InTimeUnitProtocol.in-hours",this$);
}
}
}
});

/**
 * Return the time in days.
 */
cljs_time.core.in_days = (function cljs_time$core$in_days(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$InTimeUnitProtocol$in_days$arity$1 == null)))){
return this$.cljs_time$core$InTimeUnitProtocol$in_days$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.in_days[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.in_days["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"InTimeUnitProtocol.in-days",this$);
}
}
}
});

/**
 * Return the time in weeks
 */
cljs_time.core.in_weeks = (function cljs_time$core$in_weeks(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$InTimeUnitProtocol$in_weeks$arity$1 == null)))){
return this$.cljs_time$core$InTimeUnitProtocol$in_weeks$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.in_weeks[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.in_weeks["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"InTimeUnitProtocol.in-weeks",this$);
}
}
}
});

/**
 * Return the time in months
 */
cljs_time.core.in_months = (function cljs_time$core$in_months(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$InTimeUnitProtocol$in_months$arity$1 == null)))){
return this$.cljs_time$core$InTimeUnitProtocol$in_months$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.in_months[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.in_months["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"InTimeUnitProtocol.in-months",this$);
}
}
}
});

/**
 * Return the time in years
 */
cljs_time.core.in_years = (function cljs_time$core$in_years(this$){
if((!((this$ == null))) && (!((this$.cljs_time$core$InTimeUnitProtocol$in_years$arity$1 == null)))){
return this$.cljs_time$core$InTimeUnitProtocol$in_years$arity$1(this$);
} else {
var x__30908__auto__ = (((this$ == null))?null:this$);
var m__30909__auto__ = (cljs_time.core.in_years[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,this$);
} else {
var m__30909__auto____$1 = (cljs_time.core.in_years["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,this$);
} else {
throw cljs.core.missing_protocol.call(null,"InTimeUnitProtocol.in-years",this$);
}
}
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
cljs_time.core.Interval = (function (start,end,__meta,__extmap,__hash){
this.start = start;
this.end = end;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
cljs_time.core.Interval.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

cljs_time.core.Interval.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k40705,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__40709 = k40705;
var G__40709__$1 = (((G__40709 instanceof cljs.core.Keyword))?G__40709.fqn:null);
switch (G__40709__$1) {
case "start":
return self__.start;

break;
case "end":
return self__.end;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k40705,else__30866__auto__);

}
});

cljs_time.core.Interval.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#cljs-time.core.Interval{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"start","start",-355208981),self__.start],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"end","end",-268185958),self__.end],null))], null),self__.__extmap));
});

cljs_time.core.Interval.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__40704){
var self__ = this;
var G__40704__$1 = this;
return (new cljs.core.RecordIter((0),G__40704__$1,2,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"start","start",-355208981),new cljs.core.Keyword(null,"end","end",-268185958)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

cljs_time.core.Interval.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

cljs_time.core.Interval.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new cljs_time.core.Interval(self__.start,self__.end,self__.__meta,self__.__extmap,self__.__hash));
});

cljs_time.core.Interval.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (2 + cljs.core.count.call(null,self__.__extmap));
});

cljs_time.core.Interval.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (534314193 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

cljs_time.core.Interval.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this40706,other40707){
var self__ = this;
var this40706__$1 = this;
return (!((other40707 == null))) && ((this40706__$1.constructor === other40707.constructor)) && (cljs.core._EQ_.call(null,this40706__$1.start,other40707.start)) && (cljs.core._EQ_.call(null,this40706__$1.end,other40707.end)) && (cljs.core._EQ_.call(null,this40706__$1.__extmap,other40707.__extmap));
});

cljs_time.core.Interval.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"start","start",-355208981),null,new cljs.core.Keyword(null,"end","end",-268185958),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new cljs_time.core.Interval(self__.start,self__.end,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

cljs_time.core.Interval.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__40704){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__40710 = cljs.core.keyword_identical_QMARK_;
var expr__40711 = k__30871__auto__;
if(cljs.core.truth_(pred__40710.call(null,new cljs.core.Keyword(null,"start","start",-355208981),expr__40711))){
return (new cljs_time.core.Interval(G__40704,self__.end,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__40710.call(null,new cljs.core.Keyword(null,"end","end",-268185958),expr__40711))){
return (new cljs_time.core.Interval(self__.start,G__40704,self__.__meta,self__.__extmap,null));
} else {
return (new cljs_time.core.Interval(self__.start,self__.end,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__40704),null));
}
}
});

cljs_time.core.Interval.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"start","start",-355208981),self__.start],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"end","end",-268185958),self__.end],null))], null),self__.__extmap));
});

cljs_time.core.Interval.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__40704){
var self__ = this;
var this__30862__auto____$1 = this;
return (new cljs_time.core.Interval(self__.start,self__.end,G__40704,self__.__extmap,self__.__hash));
});

cljs_time.core.Interval.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

cljs_time.core.Interval.getBasis = (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"start","start",1285322546,null),new cljs.core.Symbol(null,"end","end",1372345569,null)], null);
});

cljs_time.core.Interval.cljs$lang$type = true;

cljs_time.core.Interval.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"cljs-time.core/Interval");
});

cljs_time.core.Interval.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"cljs-time.core/Interval");
});

cljs_time.core.__GT_Interval = (function cljs_time$core$__GT_Interval(start,end){
return (new cljs_time.core.Interval(start,end,null,null,null));
});

cljs_time.core.map__GT_Interval = (function cljs_time$core$map__GT_Interval(G__40708){
return (new cljs_time.core.Interval(new cljs.core.Keyword(null,"start","start",-355208981).cljs$core$IFn$_invoke$arity$1(G__40708),new cljs.core.Keyword(null,"end","end",-268185958).cljs$core$IFn$_invoke$arity$1(G__40708),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__40708,new cljs.core.Keyword(null,"start","start",-355208981),new cljs.core.Keyword(null,"end","end",-268185958))),null));
});

/**
 * Returns an Interval representing the span between the two given DateTime.
 *   Note that intervals are closed on the left and open on the right.
 */
cljs_time.core.interval = (function cljs_time$core$interval(start,end){
if((start.getTime() <= end.getTime())){
} else {
throw (new Error("Assert failed: (<= (.getTime start) (.getTime end))"));
}

return cljs_time.core.__GT_Interval.call(null,start,end);
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
cljs_time.core.Period = (function (years,months,weeks,days,hours,minutes,seconds,millis,__meta,__extmap,__hash){
this.years = years;
this.months = months;
this.weeks = weeks;
this.days = days;
this.hours = hours;
this.minutes = minutes;
this.seconds = seconds;
this.millis = millis;
this.__meta = __meta;
this.__extmap = __extmap;
this.__hash = __hash;
this.cljs$lang$protocol_mask$partition0$ = 2229667594;
this.cljs$lang$protocol_mask$partition1$ = 139264;
});
cljs_time.core.Period.prototype.cljs$core$ILookup$_lookup$arity$2 = (function (this__30863__auto__,k__30864__auto__){
var self__ = this;
var this__30863__auto____$1 = this;
return this__30863__auto____$1.cljs$core$ILookup$_lookup$arity$3(null,k__30864__auto__,null);
});

cljs_time.core.Period.prototype.cljs$core$ILookup$_lookup$arity$3 = (function (this__30865__auto__,k40715,else__30866__auto__){
var self__ = this;
var this__30865__auto____$1 = this;
var G__40719 = k40715;
var G__40719__$1 = (((G__40719 instanceof cljs.core.Keyword))?G__40719.fqn:null);
switch (G__40719__$1) {
case "years":
return self__.years;

break;
case "months":
return self__.months;

break;
case "weeks":
return self__.weeks;

break;
case "days":
return self__.days;

break;
case "hours":
return self__.hours;

break;
case "minutes":
return self__.minutes;

break;
case "seconds":
return self__.seconds;

break;
case "millis":
return self__.millis;

break;
default:
return cljs.core.get.call(null,self__.__extmap,k40715,else__30866__auto__);

}
});

cljs_time.core.Period.prototype.cljs$core$IPrintWithWriter$_pr_writer$arity$3 = (function (this__30877__auto__,writer__30878__auto__,opts__30879__auto__){
var self__ = this;
var this__30877__auto____$1 = this;
var pr_pair__30880__auto__ = ((function (this__30877__auto____$1){
return (function (keyval__30881__auto__){
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,cljs.core.pr_writer,""," ","",opts__30879__auto__,keyval__30881__auto__);
});})(this__30877__auto____$1))
;
return cljs.core.pr_sequential_writer.call(null,writer__30878__auto__,pr_pair__30880__auto__,"#cljs-time.core.Period{",", ","}",opts__30879__auto__,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 8, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"years","years",-1298579689),self__.years],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"months","months",-45571637),self__.months],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"weeks","weeks",1844596125),self__.weeks],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"days","days",-1394072564),self__.days],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"hours","hours",58380855),self__.hours],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"minutes","minutes",1319166394),self__.minutes],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"seconds","seconds",-445266194),self__.seconds],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"millis","millis",-1338288387),self__.millis],null))], null),self__.__extmap));
});

cljs_time.core.Period.prototype.cljs$core$IIterable$_iterator$arity$1 = (function (G__40714){
var self__ = this;
var G__40714__$1 = this;
return (new cljs.core.RecordIter((0),G__40714__$1,8,new cljs.core.PersistentVector(null, 8, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"years","years",-1298579689),new cljs.core.Keyword(null,"months","months",-45571637),new cljs.core.Keyword(null,"weeks","weeks",1844596125),new cljs.core.Keyword(null,"days","days",-1394072564),new cljs.core.Keyword(null,"hours","hours",58380855),new cljs.core.Keyword(null,"minutes","minutes",1319166394),new cljs.core.Keyword(null,"seconds","seconds",-445266194),new cljs.core.Keyword(null,"millis","millis",-1338288387)], null),(cljs.core.truth_(self__.__extmap)?cljs.core._iterator.call(null,self__.__extmap):cljs.core.nil_iter.call(null))));
});

cljs_time.core.Period.prototype.cljs$core$IMeta$_meta$arity$1 = (function (this__30861__auto__){
var self__ = this;
var this__30861__auto____$1 = this;
return self__.__meta;
});

cljs_time.core.Period.prototype.cljs$core$ICloneable$_clone$arity$1 = (function (this__30858__auto__){
var self__ = this;
var this__30858__auto____$1 = this;
return (new cljs_time.core.Period(self__.years,self__.months,self__.weeks,self__.days,self__.hours,self__.minutes,self__.seconds,self__.millis,self__.__meta,self__.__extmap,self__.__hash));
});

cljs_time.core.Period.prototype.cljs$core$ICounted$_count$arity$1 = (function (this__30867__auto__){
var self__ = this;
var this__30867__auto____$1 = this;
return (8 + cljs.core.count.call(null,self__.__extmap));
});

cljs_time.core.Period.prototype.cljs$core$IHash$_hash$arity$1 = (function (this__30859__auto__){
var self__ = this;
var this__30859__auto____$1 = this;
var h__30631__auto__ = self__.__hash;
if(!((h__30631__auto__ == null))){
return h__30631__auto__;
} else {
var h__30631__auto____$1 = ((function (h__30631__auto__,this__30859__auto____$1){
return (function (coll__30860__auto__){
return (1393857022 ^ cljs.core.hash_unordered_coll.call(null,coll__30860__auto__));
});})(h__30631__auto__,this__30859__auto____$1))
.call(null,this__30859__auto____$1);
self__.__hash = h__30631__auto____$1;

return h__30631__auto____$1;
}
});

cljs_time.core.Period.prototype.cljs$core$IEquiv$_equiv$arity$2 = (function (this40716,other40717){
var self__ = this;
var this40716__$1 = this;
return (!((other40717 == null))) && ((this40716__$1.constructor === other40717.constructor)) && (cljs.core._EQ_.call(null,this40716__$1.years,other40717.years)) && (cljs.core._EQ_.call(null,this40716__$1.months,other40717.months)) && (cljs.core._EQ_.call(null,this40716__$1.weeks,other40717.weeks)) && (cljs.core._EQ_.call(null,this40716__$1.days,other40717.days)) && (cljs.core._EQ_.call(null,this40716__$1.hours,other40717.hours)) && (cljs.core._EQ_.call(null,this40716__$1.minutes,other40717.minutes)) && (cljs.core._EQ_.call(null,this40716__$1.seconds,other40717.seconds)) && (cljs.core._EQ_.call(null,this40716__$1.millis,other40717.millis)) && (cljs.core._EQ_.call(null,this40716__$1.__extmap,other40717.__extmap));
});

cljs_time.core.Period.prototype.cljs$core$IMap$_dissoc$arity$2 = (function (this__30872__auto__,k__30873__auto__){
var self__ = this;
var this__30872__auto____$1 = this;
if(cljs.core.contains_QMARK_.call(null,new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 8, [new cljs.core.Keyword(null,"months","months",-45571637),null,new cljs.core.Keyword(null,"days","days",-1394072564),null,new cljs.core.Keyword(null,"seconds","seconds",-445266194),null,new cljs.core.Keyword(null,"hours","hours",58380855),null,new cljs.core.Keyword(null,"years","years",-1298579689),null,new cljs.core.Keyword(null,"minutes","minutes",1319166394),null,new cljs.core.Keyword(null,"weeks","weeks",1844596125),null,new cljs.core.Keyword(null,"millis","millis",-1338288387),null], null), null),k__30873__auto__)){
return cljs.core.dissoc.call(null,cljs.core._with_meta.call(null,cljs.core.into.call(null,cljs.core.PersistentArrayMap.EMPTY,this__30872__auto____$1),self__.__meta),k__30873__auto__);
} else {
return (new cljs_time.core.Period(self__.years,self__.months,self__.weeks,self__.days,self__.hours,self__.minutes,self__.seconds,self__.millis,self__.__meta,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,self__.__extmap,k__30873__auto__)),null));
}
});

cljs_time.core.Period.prototype.cljs$core$IAssociative$_assoc$arity$3 = (function (this__30870__auto__,k__30871__auto__,G__40714){
var self__ = this;
var this__30870__auto____$1 = this;
var pred__40720 = cljs.core.keyword_identical_QMARK_;
var expr__40721 = k__30871__auto__;
if(cljs.core.truth_(pred__40720.call(null,new cljs.core.Keyword(null,"years","years",-1298579689),expr__40721))){
return (new cljs_time.core.Period(G__40714,self__.months,self__.weeks,self__.days,self__.hours,self__.minutes,self__.seconds,self__.millis,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__40720.call(null,new cljs.core.Keyword(null,"months","months",-45571637),expr__40721))){
return (new cljs_time.core.Period(self__.years,G__40714,self__.weeks,self__.days,self__.hours,self__.minutes,self__.seconds,self__.millis,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__40720.call(null,new cljs.core.Keyword(null,"weeks","weeks",1844596125),expr__40721))){
return (new cljs_time.core.Period(self__.years,self__.months,G__40714,self__.days,self__.hours,self__.minutes,self__.seconds,self__.millis,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__40720.call(null,new cljs.core.Keyword(null,"days","days",-1394072564),expr__40721))){
return (new cljs_time.core.Period(self__.years,self__.months,self__.weeks,G__40714,self__.hours,self__.minutes,self__.seconds,self__.millis,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__40720.call(null,new cljs.core.Keyword(null,"hours","hours",58380855),expr__40721))){
return (new cljs_time.core.Period(self__.years,self__.months,self__.weeks,self__.days,G__40714,self__.minutes,self__.seconds,self__.millis,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__40720.call(null,new cljs.core.Keyword(null,"minutes","minutes",1319166394),expr__40721))){
return (new cljs_time.core.Period(self__.years,self__.months,self__.weeks,self__.days,self__.hours,G__40714,self__.seconds,self__.millis,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__40720.call(null,new cljs.core.Keyword(null,"seconds","seconds",-445266194),expr__40721))){
return (new cljs_time.core.Period(self__.years,self__.months,self__.weeks,self__.days,self__.hours,self__.minutes,G__40714,self__.millis,self__.__meta,self__.__extmap,null));
} else {
if(cljs.core.truth_(pred__40720.call(null,new cljs.core.Keyword(null,"millis","millis",-1338288387),expr__40721))){
return (new cljs_time.core.Period(self__.years,self__.months,self__.weeks,self__.days,self__.hours,self__.minutes,self__.seconds,G__40714,self__.__meta,self__.__extmap,null));
} else {
return (new cljs_time.core.Period(self__.years,self__.months,self__.weeks,self__.days,self__.hours,self__.minutes,self__.seconds,self__.millis,self__.__meta,cljs.core.assoc.call(null,self__.__extmap,k__30871__auto__,G__40714),null));
}
}
}
}
}
}
}
}
});

cljs_time.core.Period.prototype.cljs$core$ISeqable$_seq$arity$1 = (function (this__30875__auto__){
var self__ = this;
var this__30875__auto____$1 = this;
return cljs.core.seq.call(null,cljs.core.concat.call(null,new cljs.core.PersistentVector(null, 8, 5, cljs.core.PersistentVector.EMPTY_NODE, [(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"years","years",-1298579689),self__.years],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"months","months",-45571637),self__.months],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"weeks","weeks",1844596125),self__.weeks],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"days","days",-1394072564),self__.days],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"hours","hours",58380855),self__.hours],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"minutes","minutes",1319166394),self__.minutes],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"seconds","seconds",-445266194),self__.seconds],null)),(new cljs.core.PersistentVector(null,2,(5),cljs.core.PersistentVector.EMPTY_NODE,[new cljs.core.Keyword(null,"millis","millis",-1338288387),self__.millis],null))], null),self__.__extmap));
});

cljs_time.core.Period.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (this__30862__auto__,G__40714){
var self__ = this;
var this__30862__auto____$1 = this;
return (new cljs_time.core.Period(self__.years,self__.months,self__.weeks,self__.days,self__.hours,self__.minutes,self__.seconds,self__.millis,G__40714,self__.__extmap,self__.__hash));
});

cljs_time.core.Period.prototype.cljs$core$ICollection$_conj$arity$2 = (function (this__30868__auto__,entry__30869__auto__){
var self__ = this;
var this__30868__auto____$1 = this;
if(cljs.core.vector_QMARK_.call(null,entry__30869__auto__)){
return this__30868__auto____$1.cljs$core$IAssociative$_assoc$arity$3(null,cljs.core._nth.call(null,entry__30869__auto__,(0)),cljs.core._nth.call(null,entry__30869__auto__,(1)));
} else {
return cljs.core.reduce.call(null,cljs.core._conj,this__30868__auto____$1,entry__30869__auto__);
}
});

cljs_time.core.Period.getBasis = (function (){
return new cljs.core.PersistentVector(null, 8, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"years","years",341951838,null),new cljs.core.Symbol(null,"months","months",1594959890,null),new cljs.core.Symbol(null,"weeks","weeks",-809839644,null),new cljs.core.Symbol(null,"days","days",246458963,null),new cljs.core.Symbol(null,"hours","hours",1698912382,null),new cljs.core.Symbol(null,"minutes","minutes",-1335269375,null),new cljs.core.Symbol(null,"seconds","seconds",1195265333,null),new cljs.core.Symbol(null,"millis","millis",302243140,null)], null);
});

cljs_time.core.Period.cljs$lang$type = true;

cljs_time.core.Period.cljs$lang$ctorPrSeq = (function (this__30901__auto__){
return cljs.core._conj.call(null,cljs.core.List.EMPTY,"cljs-time.core/Period");
});

cljs_time.core.Period.cljs$lang$ctorPrWriter = (function (this__30901__auto__,writer__30902__auto__){
return cljs.core._write.call(null,writer__30902__auto__,"cljs-time.core/Period");
});

cljs_time.core.__GT_Period = (function cljs_time$core$__GT_Period(years,months,weeks,days,hours,minutes,seconds,millis){
return (new cljs_time.core.Period(years,months,weeks,days,hours,minutes,seconds,millis,null,null,null));
});

cljs_time.core.map__GT_Period = (function cljs_time$core$map__GT_Period(G__40718){
return (new cljs_time.core.Period(new cljs.core.Keyword(null,"years","years",-1298579689).cljs$core$IFn$_invoke$arity$1(G__40718),new cljs.core.Keyword(null,"months","months",-45571637).cljs$core$IFn$_invoke$arity$1(G__40718),new cljs.core.Keyword(null,"weeks","weeks",1844596125).cljs$core$IFn$_invoke$arity$1(G__40718),new cljs.core.Keyword(null,"days","days",-1394072564).cljs$core$IFn$_invoke$arity$1(G__40718),new cljs.core.Keyword(null,"hours","hours",58380855).cljs$core$IFn$_invoke$arity$1(G__40718),new cljs.core.Keyword(null,"minutes","minutes",1319166394).cljs$core$IFn$_invoke$arity$1(G__40718),new cljs.core.Keyword(null,"seconds","seconds",-445266194).cljs$core$IFn$_invoke$arity$1(G__40718),new cljs.core.Keyword(null,"millis","millis",-1338288387).cljs$core$IFn$_invoke$arity$1(G__40718),null,cljs.core.not_empty.call(null,cljs.core.dissoc.call(null,G__40718,new cljs.core.Keyword(null,"years","years",-1298579689),new cljs.core.Keyword(null,"months","months",-45571637),new cljs.core.Keyword(null,"weeks","weeks",1844596125),new cljs.core.Keyword(null,"days","days",-1394072564),new cljs.core.Keyword(null,"hours","hours",58380855),new cljs.core.Keyword(null,"minutes","minutes",1319166394),new cljs.core.Keyword(null,"seconds","seconds",-445266194),new cljs.core.Keyword(null,"millis","millis",-1338288387))),null));
});

cljs_time.core.period = (function cljs_time$core$period(var_args){
var G__40728 = arguments.length;
switch (G__40728) {
case 2:
return cljs_time.core.period.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
var args_arr__31475__auto__ = [];
var len__31452__auto___40730 = arguments.length;
var i__31453__auto___40731 = (0);
while(true){
if((i__31453__auto___40731 < len__31452__auto___40730)){
args_arr__31475__auto__.push((arguments[i__31453__auto___40731]));

var G__40732 = (i__31453__auto___40731 + (1));
i__31453__auto___40731 = G__40732;
continue;
} else {
}
break;
}

var argseq__31476__auto__ = (new cljs.core.IndexedSeq(args_arr__31475__auto__.slice((2)),(0),null));
return cljs_time.core.period.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__31476__auto__);

}
});

cljs_time.core.period.cljs$core$IFn$_invoke$arity$2 = (function (period,value){
return cljs_time.core.map__GT_Period.call(null,cljs.core.PersistentArrayMap.createAsIfByAssoc([period,value]));
});

cljs_time.core.period.cljs$core$IFn$_invoke$arity$variadic = (function (p1,v1,kvs){
return cljs.core.apply.call(null,cljs.core.assoc,cljs_time.core.period.call(null,p1,v1),kvs);
});

cljs_time.core.period.cljs$lang$applyTo = (function (seq40725){
var G__40726 = cljs.core.first.call(null,seq40725);
var seq40725__$1 = cljs.core.next.call(null,seq40725);
var G__40727 = cljs.core.first.call(null,seq40725__$1);
var seq40725__$2 = cljs.core.next.call(null,seq40725__$1);
return cljs_time.core.period.cljs$core$IFn$_invoke$arity$variadic(G__40726,G__40727,seq40725__$2);
});

cljs_time.core.period.cljs$lang$maxFixedArity = (2);

cljs_time.core.period_fns = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"millis","millis",-1338288387),(function (date,op,value){
var ms = op.call(null,date.getTime(),value);
var G__40733 = date;
G__40733.setTime(ms);

return G__40733;
}),new cljs.core.Keyword(null,"weeks","weeks",1844596125),(function (date,op,value){
var days = op.call(null,(0),(value * (7)));
var G__40734 = date;
G__40734.add((new goog.date.Interval(goog.date.Interval.DAYS,days)));

return G__40734;
})], null);
cljs_time.core.periods = new cljs.core.PersistentArrayMap(null, 6, [new cljs.core.Keyword(null,"seconds","seconds",-445266194),goog.date.Interval.SECONDS,new cljs.core.Keyword(null,"minutes","minutes",1319166394),goog.date.Interval.MINUTES,new cljs.core.Keyword(null,"hours","hours",58380855),goog.date.Interval.HOURS,new cljs.core.Keyword(null,"days","days",-1394072564),goog.date.Interval.DAYS,new cljs.core.Keyword(null,"months","months",-45571637),goog.date.Interval.MONTHS,new cljs.core.Keyword(null,"years","years",-1298579689),goog.date.Interval.YEARS], null);
cljs_time.core.period_fn = (function cljs_time$core$period_fn(p){
return (function (operator,date){
var date_SINGLEQUOTE_ = date.clone();
var __GT_goog_interval = ((function (date_SINGLEQUOTE_){
return (function (op,interval,value){
if(cljs.core.truth_((function (){var and__30163__auto__ = interval;
if(cljs.core.truth_(and__30163__auto__)){
return value;
} else {
return and__30163__auto__;
}
})())){
return (new goog.date.Interval(interval,op.call(null,(0),value)));
} else {
return null;
}
});})(date_SINGLEQUOTE_))
;
var seq__40735_40745 = cljs.core.seq.call(null,p);
var chunk__40736_40746 = null;
var count__40737_40747 = (0);
var i__40738_40748 = (0);
while(true){
if((i__40738_40748 < count__40737_40747)){
var vec__40739_40749 = cljs.core._nth.call(null,chunk__40736_40746,i__40738_40748);
var k_40750 = cljs.core.nth.call(null,vec__40739_40749,(0),null);
var v_40751 = cljs.core.nth.call(null,vec__40739_40749,(1),null);
var temp__5288__auto___40752 = cljs_time.core.periods.call(null,k_40750);
if(cljs.core.truth_(temp__5288__auto___40752)){
var period_40753 = temp__5288__auto___40752;
var temp__5290__auto___40754 = __GT_goog_interval.call(null,operator,period_40753,v_40751);
if(cljs.core.truth_(temp__5290__auto___40754)){
var i_40755 = temp__5290__auto___40754;
date_SINGLEQUOTE_.add(i_40755);
} else {
}
} else {
var temp__5290__auto___40756 = cljs_time.core.period_fns.call(null,k_40750);
if(cljs.core.truth_(temp__5290__auto___40756)){
var f_40757 = temp__5290__auto___40756;
f_40757.call(null,date_SINGLEQUOTE_,operator,v_40751);
} else {
}
}

var G__40758 = seq__40735_40745;
var G__40759 = chunk__40736_40746;
var G__40760 = count__40737_40747;
var G__40761 = (i__40738_40748 + (1));
seq__40735_40745 = G__40758;
chunk__40736_40746 = G__40759;
count__40737_40747 = G__40760;
i__40738_40748 = G__40761;
continue;
} else {
var temp__5290__auto___40762 = cljs.core.seq.call(null,seq__40735_40745);
if(temp__5290__auto___40762){
var seq__40735_40763__$1 = temp__5290__auto___40762;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__40735_40763__$1)){
var c__31106__auto___40764 = cljs.core.chunk_first.call(null,seq__40735_40763__$1);
var G__40765 = cljs.core.chunk_rest.call(null,seq__40735_40763__$1);
var G__40766 = c__31106__auto___40764;
var G__40767 = cljs.core.count.call(null,c__31106__auto___40764);
var G__40768 = (0);
seq__40735_40745 = G__40765;
chunk__40736_40746 = G__40766;
count__40737_40747 = G__40767;
i__40738_40748 = G__40768;
continue;
} else {
var vec__40742_40769 = cljs.core.first.call(null,seq__40735_40763__$1);
var k_40770 = cljs.core.nth.call(null,vec__40742_40769,(0),null);
var v_40771 = cljs.core.nth.call(null,vec__40742_40769,(1),null);
var temp__5288__auto___40772 = cljs_time.core.periods.call(null,k_40770);
if(cljs.core.truth_(temp__5288__auto___40772)){
var period_40773 = temp__5288__auto___40772;
var temp__5290__auto___40774__$1 = __GT_goog_interval.call(null,operator,period_40773,v_40771);
if(cljs.core.truth_(temp__5290__auto___40774__$1)){
var i_40775 = temp__5290__auto___40774__$1;
date_SINGLEQUOTE_.add(i_40775);
} else {
}
} else {
var temp__5290__auto___40776__$1 = cljs_time.core.period_fns.call(null,k_40770);
if(cljs.core.truth_(temp__5290__auto___40776__$1)){
var f_40777 = temp__5290__auto___40776__$1;
f_40777.call(null,date_SINGLEQUOTE_,operator,v_40771);
} else {
}
}

var G__40778 = cljs.core.next.call(null,seq__40735_40763__$1);
var G__40779 = null;
var G__40780 = (0);
var G__40781 = (0);
seq__40735_40745 = G__40778;
chunk__40736_40746 = G__40779;
count__40737_40747 = G__40780;
i__40738_40748 = G__40781;
continue;
}
} else {
}
}
break;
}

return date_SINGLEQUOTE_;
});
});
cljs_time.core.compare_local_dates = (function cljs_time$core$compare_local_dates(o,other){
var yo = o.getYear();
var yother = other.getYear();
var dayo = o.getDayOfYear();
var dayother = other.getDayOfYear();
if(cljs.core.not_EQ_.call(null,yo,yother)){
return (yo - yother);
} else {
if(cljs.core.not_EQ_.call(null,dayo,dayother)){
return (dayo - dayother);
} else {
return (0);

}
}
});
goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$ = cljs.core.PROTOCOL_SENTINEL;

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$year$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.getYear();
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$month$arity$1 = (function (this$){
var this$__$1 = this;
return (this$__$1.getMonth() + (1));
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$day$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.getDate();
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$day_of_week$arity$1 = (function (this$){
var this$__$1 = this;
var d = this$__$1.getDay();
if(cljs.core.truth_(cljs_time.core._EQ_.call(null,d,(0)))){
return (7);
} else {
return d;
}
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$hour$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.getHours();
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$minute$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.getMinutes();
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$second$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.getSeconds();
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$milli$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.getMilliseconds();
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$equal_QMARK_$arity$2 = (function (this$,that){
var this$__$1 = this;
return (this$__$1.getTime() === that.getTime());
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$after_QMARK_$arity$2 = (function (this$,that){
var this$__$1 = this;
return (this$__$1.getTime() > that.getTime());
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$before_QMARK_$arity$2 = (function (this$,that){
var this$__$1 = this;
return (this$__$1.getTime() < that.getTime());
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$plus_$arity$2 = (function (this$,period){
var this$__$1 = this;
return cljs_time.core.period_fn.call(null,period).call(null,cljs.core._PLUS_,this$__$1);
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$minus_$arity$2 = (function (this$,period){
var this$__$1 = this;
return cljs_time.core.period_fn.call(null,period).call(null,cljs.core._,this$__$1);
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$first_day_of_the_month_$arity$1 = (function (this$){
var this$__$1 = this;
return (new goog.date.UtcDateTime(this$__$1.getYear(),this$__$1.getMonth(),(1),(0),(0),(0),(0)));
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$last_day_of_the_month_$arity$1 = (function (this$){
var this$__$1 = this;
return cljs_time.core.minus_.call(null,(new goog.date.UtcDateTime(this$__$1.getYear(),(this$__$1.getMonth() + (1)),(1),(0),(0),(0),(0))),cljs_time.core.period.call(null,new cljs.core.Keyword(null,"days","days",-1394072564),(1)));
});

goog.date.UtcDateTime.prototype.cljs_time$core$DateTimeProtocol$week_number_of_year$arity$1 = (function (this$){
var this$__$1 = this;
return goog.date.getWeekNumber(this$__$1.getYear(),this$__$1.getMonth(),this$__$1.getDate());
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$ = cljs.core.PROTOCOL_SENTINEL;

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$year$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.getYear();
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$month$arity$1 = (function (this$){
var this$__$1 = this;
return (this$__$1.getMonth() + (1));
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$day$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.getDate();
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$day_of_week$arity$1 = (function (this$){
var this$__$1 = this;
var d = this$__$1.getDay();
if(cljs.core.truth_(cljs_time.core._EQ_.call(null,d,(0)))){
return (7);
} else {
return d;
}
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$hour$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.getHours();
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$minute$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.getMinutes();
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$second$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.getSeconds();
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$milli$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.getMilliseconds();
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$equal_QMARK_$arity$2 = (function (this$,that){
var this$__$1 = this;
return (this$__$1.getTime() === that.getTime());
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$after_QMARK_$arity$2 = (function (this$,that){
var this$__$1 = this;
return (this$__$1.getTime() > that.getTime());
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$before_QMARK_$arity$2 = (function (this$,that){
var this$__$1 = this;
return (this$__$1.getTime() < that.getTime());
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$plus_$arity$2 = (function (this$,period){
var this$__$1 = this;
return cljs_time.core.period_fn.call(null,period).call(null,cljs.core._PLUS_,this$__$1);
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$minus_$arity$2 = (function (this$,period){
var this$__$1 = this;
return cljs_time.core.period_fn.call(null,period).call(null,cljs.core._,this$__$1);
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$first_day_of_the_month_$arity$1 = (function (this$){
var this$__$1 = this;
return (new goog.date.DateTime(this$__$1.getYear(),this$__$1.getMonth(),(1),(0),(0),(0),(0)));
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$last_day_of_the_month_$arity$1 = (function (this$){
var this$__$1 = this;
return cljs_time.core.minus_.call(null,(new goog.date.DateTime(this$__$1.getYear(),(this$__$1.getMonth() + (1)),(1),(0),(0),(0),(0))),cljs_time.core.period.call(null,new cljs.core.Keyword(null,"days","days",-1394072564),(1)));
});

goog.date.DateTime.prototype.cljs_time$core$DateTimeProtocol$week_number_of_year$arity$1 = (function (this$){
var this$__$1 = this;
return goog.date.getWeekNumber(this$__$1.getYear(),this$__$1.getMonth(),this$__$1.getDate());
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$ = cljs.core.PROTOCOL_SENTINEL;

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$year$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.getYear();
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$month$arity$1 = (function (this$){
var this$__$1 = this;
return (this$__$1.getMonth() + (1));
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$day$arity$1 = (function (this$){
var this$__$1 = this;
return this$__$1.getDate();
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$day_of_week$arity$1 = (function (this$){
var this$__$1 = this;
var d = this$__$1.getDay();
if(cljs.core.truth_(cljs_time.core._EQ_.call(null,d,(0)))){
return (7);
} else {
return d;
}
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$hour$arity$1 = (function (this$){
var this$__$1 = this;
return null;
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$minute$arity$1 = (function (this$){
var this$__$1 = this;
return null;
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$second$arity$1 = (function (this$){
var this$__$1 = this;
return null;
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$milli$arity$1 = (function (this$){
var this$__$1 = this;
return null;
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$equal_QMARK_$arity$2 = (function (this$,that){
var this$__$1 = this;
return this$__$1.equals(that);
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$after_QMARK_$arity$2 = (function (this$,that){
var this$__$1 = this;
return (cljs_time.core.compare_local_dates.call(null,this$__$1,that) > (0));
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$before_QMARK_$arity$2 = (function (this$,that){
var this$__$1 = this;
return (cljs_time.core.compare_local_dates.call(null,this$__$1,that) < (0));
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$plus_$arity$2 = (function (this$,period){
var this$__$1 = this;
return cljs_time.core.period_fn.call(null,period).call(null,cljs.core._PLUS_,this$__$1);
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$minus_$arity$2 = (function (this$,period){
var this$__$1 = this;
return cljs_time.core.period_fn.call(null,period).call(null,cljs.core._,this$__$1);
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$first_day_of_the_month_$arity$1 = (function (this$){
var this$__$1 = this;
return (new goog.date.Date(this$__$1.getYear(),this$__$1.getMonth(),(1)));
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$last_day_of_the_month_$arity$1 = (function (this$){
var this$__$1 = this;
return cljs_time.core.minus_.call(null,(new goog.date.Date(this$__$1.getYear(),(this$__$1.getMonth() + (1)),(1))),cljs_time.core.period.call(null,new cljs.core.Keyword(null,"days","days",-1394072564),(1)));
});

goog.date.Date.prototype.cljs_time$core$DateTimeProtocol$week_number_of_year$arity$1 = (function (this$){
var this$__$1 = this;
return goog.date.getWeekNumber(this$__$1.getYear(),this$__$1.getMonth(),this$__$1.getDate());
});
cljs_time.core.utc = ({"id": "UTC", "std_offset": (0), "names": new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, ["UTC"], null), "transitions": cljs.core.PersistentVector.EMPTY});
cljs_time.core.default_ms_fn = (function cljs_time$core$default_ms_fn(){
return (function (){
return (new goog.date.UtcDateTime()).getTime();
});
});
cljs_time.core.offset_ms_fn = (function cljs_time$core$offset_ms_fn(offset){
return (function (){
return ((new goog.date.UtcDateTime()).getTime() + offset);
});
});
cljs_time.core.static_ms_fn = (function cljs_time$core$static_ms_fn(ms){
return (function (){
return ms;
});
});
cljs_time.core._STAR_ms_fn_STAR_ = cljs_time.core.default_ms_fn.call(null);
/**
 * Returns a DateTime for the current instant in the UTC time zone.
 */
cljs_time.core.now = (function cljs_time$core$now(){
var G__40782 = (new goog.date.UtcDateTime());
G__40782.setTime(cljs_time.core._STAR_ms_fn_STAR_.call(null));

return G__40782;
});
/**
 * Returns a local DateTime for the current instant without date or time zone
 *   in the current time zone.
 */
cljs_time.core.time_now = (function cljs_time$core$time_now(){
var G__40783 = (new goog.date.DateTime());
G__40783.setTime(cljs_time.core._STAR_ms_fn_STAR_.call(null));

return G__40783;
});
cljs_time.core.at_midnight = (function cljs_time$core$at_midnight(datetime){
var datetime__$1 = datetime.clone();
var G__40784 = datetime__$1;
G__40784.setHours((0));

G__40784.setMinutes((0));

G__40784.setSeconds((0));

G__40784.setMilliseconds((0));

return G__40784;
});
/**
 * Returns a DateTime for today at midnight in the UTC time zone.
 */
cljs_time.core.today_at_midnight = (function cljs_time$core$today_at_midnight(){
return cljs_time.core.at_midnight.call(null,cljs_time.core.now.call(null));
});
/**
 * Returns a DateTime for the begining of the Unix epoch in the UTC time zone.
 */
cljs_time.core.epoch = (function cljs_time$core$epoch(){
var G__40785 = (new goog.date.UtcDateTime());
G__40785.setTime((0));

return G__40785;
});
/**
 * Constructs and returns a new DateTime at midnight in UTC.
 * 
 *   Specify the year, month of year, day of month. Note that month and day are
 *   1-indexed. Any number of least-significant components can be ommited, in
 *   which case they will default to 1.
 */
cljs_time.core.date_midnight = (function cljs_time$core$date_midnight(var_args){
var G__40787 = arguments.length;
switch (G__40787) {
case 1:
return cljs_time.core.date_midnight.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs_time.core.date_midnight.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs_time.core.date_midnight.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.date_midnight.cljs$core$IFn$_invoke$arity$1 = (function (year){
return cljs_time.core.date_midnight.call(null,year,(1),(1));
});

cljs_time.core.date_midnight.cljs$core$IFn$_invoke$arity$2 = (function (year,month){
return cljs_time.core.date_midnight.call(null,year,month,(1));
});

cljs_time.core.date_midnight.cljs$core$IFn$_invoke$arity$3 = (function (year,month,day){
return (new goog.date.UtcDateTime(year,(month - (1)),day));
});

cljs_time.core.date_midnight.cljs$lang$maxFixedArity = 3;

/**
 * Constructs and returns a new DateTime in UTC.
 * 
 *   Specify the year, month of year, day of month, hour of day, minute if hour,
 *   second of minute, and millisecond of second. Note that month and day are
 *   1-indexed while hour, second, minute, and millis are 0-indexed.
 * 
 *   Any number of least-significant components can be ommited, in which case
 *   they will default to 1 or 0 as appropriate.
 */
cljs_time.core.date_time = (function cljs_time$core$date_time(var_args){
var G__40790 = arguments.length;
switch (G__40790) {
case 1:
return cljs_time.core.date_time.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs_time.core.date_time.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs_time.core.date_time.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs_time.core.date_time.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 5:
return cljs_time.core.date_time.cljs$core$IFn$_invoke$arity$5((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]));

break;
case 6:
return cljs_time.core.date_time.cljs$core$IFn$_invoke$arity$6((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]),(arguments[(5)]));

break;
case 7:
return cljs_time.core.date_time.cljs$core$IFn$_invoke$arity$7((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]),(arguments[(5)]),(arguments[(6)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.date_time.cljs$core$IFn$_invoke$arity$1 = (function (year){
return cljs_time.core.date_time.call(null,year,(1),(1),(0),(0),(0),(0));
});

cljs_time.core.date_time.cljs$core$IFn$_invoke$arity$2 = (function (year,month){
return cljs_time.core.date_time.call(null,year,month,(1),(0),(0),(0),(0));
});

cljs_time.core.date_time.cljs$core$IFn$_invoke$arity$3 = (function (year,month,day){
return cljs_time.core.date_time.call(null,year,month,day,(0),(0),(0),(0));
});

cljs_time.core.date_time.cljs$core$IFn$_invoke$arity$4 = (function (year,month,day,hour){
return cljs_time.core.date_time.call(null,year,month,day,hour,(0),(0),(0));
});

cljs_time.core.date_time.cljs$core$IFn$_invoke$arity$5 = (function (year,month,day,hour,minute){
return cljs_time.core.date_time.call(null,year,month,day,hour,minute,(0),(0));
});

cljs_time.core.date_time.cljs$core$IFn$_invoke$arity$6 = (function (year,month,day,hour,minute,second){
return cljs_time.core.date_time.call(null,year,month,day,hour,minute,second,(0));
});

cljs_time.core.date_time.cljs$core$IFn$_invoke$arity$7 = (function (year,month,day,hour,minute,second,millis){
return (new goog.date.UtcDateTime(year,(month - (1)),day,hour,minute,second,millis));
});

cljs_time.core.date_time.cljs$lang$maxFixedArity = 7;

/**
 * Constructs and returns a new local DateTime.
 * Specify the year, month of year, day of month, hour of day, minute of hour,
 * second of minute, and millisecond of second. Note that month and day are
 * 1-indexed while hour, second, minute, and millis are 0-indexed.
 * Any number of least-significant components can be ommited, in which case
 * they will default to 1 or 0 as appropriate.
 */
cljs_time.core.local_date_time = (function cljs_time$core$local_date_time(var_args){
var G__40793 = arguments.length;
switch (G__40793) {
case 1:
return cljs_time.core.local_date_time.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs_time.core.local_date_time.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs_time.core.local_date_time.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs_time.core.local_date_time.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 5:
return cljs_time.core.local_date_time.cljs$core$IFn$_invoke$arity$5((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]));

break;
case 6:
return cljs_time.core.local_date_time.cljs$core$IFn$_invoke$arity$6((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]),(arguments[(5)]));

break;
case 7:
return cljs_time.core.local_date_time.cljs$core$IFn$_invoke$arity$7((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]),(arguments[(5)]),(arguments[(6)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.local_date_time.cljs$core$IFn$_invoke$arity$1 = (function (year){
return cljs_time.core.local_date_time.call(null,year,(1),(1),(0),(0),(0),(0));
});

cljs_time.core.local_date_time.cljs$core$IFn$_invoke$arity$2 = (function (year,month){
return cljs_time.core.local_date_time.call(null,year,month,(1),(0),(0),(0),(0));
});

cljs_time.core.local_date_time.cljs$core$IFn$_invoke$arity$3 = (function (year,month,day){
return cljs_time.core.local_date_time.call(null,year,month,day,(0),(0),(0),(0));
});

cljs_time.core.local_date_time.cljs$core$IFn$_invoke$arity$4 = (function (year,month,day,hour){
return cljs_time.core.local_date_time.call(null,year,month,day,hour,(0),(0),(0));
});

cljs_time.core.local_date_time.cljs$core$IFn$_invoke$arity$5 = (function (year,month,day,hour,minute){
return cljs_time.core.local_date_time.call(null,year,month,day,hour,minute,(0),(0));
});

cljs_time.core.local_date_time.cljs$core$IFn$_invoke$arity$6 = (function (year,month,day,hour,minute,second){
return cljs_time.core.local_date_time.call(null,year,month,day,hour,minute,second,(0));
});

cljs_time.core.local_date_time.cljs$core$IFn$_invoke$arity$7 = (function (year,month,day,hour,minute,second,millis){
return (new goog.date.DateTime(year,(month - (1)),day,hour,minute,second,millis));
});

cljs_time.core.local_date_time.cljs$lang$maxFixedArity = 7;

/**
 * Constructs and returns a new goog.date.Date in the local timezone.
 * Specify the year, month, and day.
 */
cljs_time.core.local_date = (function cljs_time$core$local_date(year,month,day){
return (new goog.date.Date(year,(month - (1)),day));
});
/**
 * Constructs and returns a new goog.date.Date representing today in the local timezone.
 */
cljs_time.core.today = (function cljs_time$core$today(){
return (new goog.date.Date((new Date(cljs_time.core._STAR_ms_fn_STAR_.call(null)))));
});
/**
 * Returns a timezone map for the given offset, specified either in hours or
 *   hours and minutes.
 */
cljs_time.core.time_zone_for_offset = (function cljs_time$core$time_zone_for_offset(var_args){
var G__40796 = arguments.length;
switch (G__40796) {
case 1:
return cljs_time.core.time_zone_for_offset.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs_time.core.time_zone_for_offset.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.time_zone_for_offset.cljs$core$IFn$_invoke$arity$1 = (function (hours){
return cljs_time.core.time_zone_for_offset.call(null,hours,null);
});

cljs_time.core.time_zone_for_offset.cljs$core$IFn$_invoke$arity$2 = (function (hours,minutes){
var sign = (((hours < (0)))?new cljs.core.Keyword(null,"-","-",-2112348439):new cljs.core.Keyword(null,"+","+",1913524883));
var fmt = ["UTC%s%02d",cljs.core.str.cljs$core$IFn$_invoke$arity$1((cljs.core.truth_(minutes)?":%02d":null))].join('');
var hours__$1 = (((hours < (0)))?((-1) * hours):hours);
var tz_name = (cljs.core.truth_(minutes)?cljs_time.internal.core.format.call(null,fmt,cljs.core.name.call(null,sign),hours__$1,minutes):cljs_time.internal.core.format.call(null,fmt,cljs.core.name.call(null,sign),hours__$1));
return cljs.core.with_meta.call(null,new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"id","id",-1388402092),tz_name,new cljs.core.Keyword(null,"offset","offset",296498311),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [sign,hours__$1,(function (){var or__30175__auto__ = minutes;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return (0);
}
})(),(0)], null),new cljs.core.Keyword(null,"rules","rules",1198912366),"-",new cljs.core.Keyword(null,"names","names",-1943074658),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [tz_name], null)], null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword("cljs-time.core","time-zone","cljs-time.core/time-zone",751963705)], null));
});

cljs_time.core.time_zone_for_offset.cljs$lang$maxFixedArity = 2;

/**
 * Returns the default timezone map for the current environment.
 */
cljs_time.core.default_time_zone = (function cljs_time$core$default_time_zone(){
var offset = (function (){var G__40798 = (new goog.date.DateTime());
G__40798.setTime(cljs_time.core._STAR_ms_fn_STAR_.call(null));

return G__40798;
})().getTimezoneOffset();
var hours = (((-1) * offset) / (60));
return cljs_time.core.time_zone_for_offset.call(null,(hours | (0)),cljs.core.mod.call(null,hours,(1)));
});
/**
 * Assuming `dt` is in the UTC timezone, returns a DateTime
 *   corresponding to the same absolute instant in time as the given
 *   DateTime, but with calendar fields corresponding to the default
 *   (local) timezone.
 */
cljs_time.core.to_default_time_zone = (function cljs_time$core$to_default_time_zone(dt){
return (new goog.date.DateTime(dt));
});
/**
 * Assuming `dt` is in the Local timezone, returns a UtcDateTime
 *   corresponding to the same absolute instant in time as the given
 *   DateTime, but with calendar fields corresponding to the UTC
 *   timezone.
 */
cljs_time.core.to_utc_time_zone = (function cljs_time$core$to_utc_time_zone(dt){
return goog.date.UtcDateTime.fromTimestamp(dt.getTime());
});
/**
 * Assuming `dt` is in the UTC timezone, returns a DateTime
 *   corresponding to the same point in calendar time as the given
 *   DateTime, but for a correspondingly different absolute instant in
 *   time in the default (local) timezone.
 * 
 *   Note: This implementation uses the ECMAScript 5.1 implementation which
 *   trades some historical daylight savings transition accuracy for simplicity.
 *   see http://es5.github.io/#x15.9.1.8
 *   
 */
cljs_time.core.from_default_time_zone = (function cljs_time$core$from_default_time_zone(dt){
return (new goog.date.DateTime(dt.getYear(),dt.getMonth(),dt.getDate(),dt.getHours(),dt.getMinutes(),dt.getSeconds(),dt.getMilliseconds()));
});
/**
 * Assuming `dt` is in the local timezone, returns a UtcDateTime
 *   corresponding to the same point in calendar time as the given
 *   DateTime, but for a correspondingly different absolute instant in
 *   time in the UTC timezone.
 * 
 *   Note: This implementation uses the ECMAScript 5.1 implementation which
 *   trades some historical daylight savings transition accuracy for simplicity.
 *   see http://es5.github.io/#x15.9.1.8
 *   
 */
cljs_time.core.from_utc_time_zone = (function cljs_time$core$from_utc_time_zone(dt){
var year = dt.getYear();
var month = dt.getMonth();
var date = dt.getDate();
if(cljs.core.truth_(cljs_time.core._EQ_.call(null,goog.date.Date,cljs.core.type.call(null,dt)))){
return (new goog.date.UtcDateTime(year,month,date));
} else {
return (new goog.date.UtcDateTime(year,month,date,dt.getHours(),dt.getMinutes(),dt.getSeconds(),dt.getMilliseconds()));
}
});
/**
 * Given a number, returns a Period representing that many years.
 *   Without an argument, returns a Period representing only years.
 */
cljs_time.core.years = (function cljs_time$core$years(var_args){
var G__40800 = arguments.length;
switch (G__40800) {
case 0:
return cljs_time.core.years.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs_time.core.years.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.years.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs_time.core.years.call(null,null);
});

cljs_time.core.years.cljs$core$IFn$_invoke$arity$1 = (function (n){
return cljs_time.core.period.call(null,new cljs.core.Keyword(null,"years","years",-1298579689),n);
});

cljs_time.core.years.cljs$lang$maxFixedArity = 1;

/**
 * Given a number, returns a Period representing that many months.
 *   Without an argument, returns a Period representing only months.
 */
cljs_time.core.months = (function cljs_time$core$months(var_args){
var G__40803 = arguments.length;
switch (G__40803) {
case 0:
return cljs_time.core.months.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs_time.core.months.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.months.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs_time.core.months.call(null,null);
});

cljs_time.core.months.cljs$core$IFn$_invoke$arity$1 = (function (n){
return cljs_time.core.period.call(null,new cljs.core.Keyword(null,"months","months",-45571637),n);
});

cljs_time.core.months.cljs$lang$maxFixedArity = 1;

/**
 * Given a number, returns a Period representing that many weeks.
 *   Without an argument, returns a Period representing only weeks.
 */
cljs_time.core.weeks = (function cljs_time$core$weeks(var_args){
var G__40806 = arguments.length;
switch (G__40806) {
case 0:
return cljs_time.core.weeks.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs_time.core.weeks.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.weeks.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs_time.core.weeks.call(null,null);
});

cljs_time.core.weeks.cljs$core$IFn$_invoke$arity$1 = (function (n){
return cljs_time.core.period.call(null,new cljs.core.Keyword(null,"weeks","weeks",1844596125),n);
});

cljs_time.core.weeks.cljs$lang$maxFixedArity = 1;

/**
 * Given a number, returns a Period representing that many days.
 *   Without an argument, returns a Period representing only days.
 */
cljs_time.core.days = (function cljs_time$core$days(var_args){
var G__40809 = arguments.length;
switch (G__40809) {
case 0:
return cljs_time.core.days.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs_time.core.days.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.days.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs_time.core.days.call(null,null);
});

cljs_time.core.days.cljs$core$IFn$_invoke$arity$1 = (function (n){
return cljs_time.core.period.call(null,new cljs.core.Keyword(null,"days","days",-1394072564),n);
});

cljs_time.core.days.cljs$lang$maxFixedArity = 1;

/**
 * Given a number, returns a Period representing that many hours.
 *   Without an argument, returns a Period representing only hours.
 */
cljs_time.core.hours = (function cljs_time$core$hours(var_args){
var G__40812 = arguments.length;
switch (G__40812) {
case 0:
return cljs_time.core.hours.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs_time.core.hours.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.hours.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs_time.core.hours.call(null,null);
});

cljs_time.core.hours.cljs$core$IFn$_invoke$arity$1 = (function (n){
return cljs_time.core.period.call(null,new cljs.core.Keyword(null,"hours","hours",58380855),n);
});

cljs_time.core.hours.cljs$lang$maxFixedArity = 1;

/**
 * Given a number, returns a Period representing that many minutes.
 *   Without an argument, returns a Period representing only minutes.
 */
cljs_time.core.minutes = (function cljs_time$core$minutes(var_args){
var G__40815 = arguments.length;
switch (G__40815) {
case 0:
return cljs_time.core.minutes.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs_time.core.minutes.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.minutes.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs_time.core.minutes.call(null,null);
});

cljs_time.core.minutes.cljs$core$IFn$_invoke$arity$1 = (function (n){
return cljs_time.core.period.call(null,new cljs.core.Keyword(null,"minutes","minutes",1319166394),n);
});

cljs_time.core.minutes.cljs$lang$maxFixedArity = 1;

/**
 * Given a number, returns a Period representing that many seconds.
 *   Without an argument, returns a Period representing only seconds.
 */
cljs_time.core.seconds = (function cljs_time$core$seconds(var_args){
var G__40818 = arguments.length;
switch (G__40818) {
case 0:
return cljs_time.core.seconds.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs_time.core.seconds.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.seconds.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs_time.core.seconds.call(null,null);
});

cljs_time.core.seconds.cljs$core$IFn$_invoke$arity$1 = (function (n){
return cljs_time.core.period.call(null,new cljs.core.Keyword(null,"seconds","seconds",-445266194),n);
});

cljs_time.core.seconds.cljs$lang$maxFixedArity = 1;

/**
 * Given a number, returns a Period representing that many milliseconds.
 *   Without an argument, returns a Period representing only milliseconds.
 */
cljs_time.core.millis = (function cljs_time$core$millis(var_args){
var G__40821 = arguments.length;
switch (G__40821) {
case 0:
return cljs_time.core.millis.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs_time.core.millis.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.millis.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs_time.core.millis.call(null,null);
});

cljs_time.core.millis.cljs$core$IFn$_invoke$arity$1 = (function (n){
return cljs_time.core.period.call(null,new cljs.core.Keyword(null,"millis","millis",-1338288387),n);
});

cljs_time.core.millis.cljs$lang$maxFixedArity = 1;

/**
 * Returns a new date/time corresponding to the given date/time moved
 *   forwards by the given Period(s).
 */
cljs_time.core.plus = (function cljs_time$core$plus(var_args){
var G__40827 = arguments.length;
switch (G__40827) {
case 2:
return cljs_time.core.plus.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
var args_arr__31475__auto__ = [];
var len__31452__auto___40829 = arguments.length;
var i__31453__auto___40830 = (0);
while(true){
if((i__31453__auto___40830 < len__31452__auto___40829)){
args_arr__31475__auto__.push((arguments[i__31453__auto___40830]));

var G__40831 = (i__31453__auto___40830 + (1));
i__31453__auto___40830 = G__40831;
continue;
} else {
}
break;
}

var argseq__31476__auto__ = (new cljs.core.IndexedSeq(args_arr__31475__auto__.slice((2)),(0),null));
return cljs_time.core.plus.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__31476__auto__);

}
});

cljs_time.core.plus.cljs$core$IFn$_invoke$arity$2 = (function (dt,p){
return cljs_time.core.plus_.call(null,dt,p);
});

cljs_time.core.plus.cljs$core$IFn$_invoke$arity$variadic = (function (dt,p,ps){
return cljs.core.reduce.call(null,cljs_time.core.plus_,cljs_time.core.plus_.call(null,dt,p),ps);
});

cljs_time.core.plus.cljs$lang$applyTo = (function (seq40824){
var G__40825 = cljs.core.first.call(null,seq40824);
var seq40824__$1 = cljs.core.next.call(null,seq40824);
var G__40826 = cljs.core.first.call(null,seq40824__$1);
var seq40824__$2 = cljs.core.next.call(null,seq40824__$1);
return cljs_time.core.plus.cljs$core$IFn$_invoke$arity$variadic(G__40825,G__40826,seq40824__$2);
});

cljs_time.core.plus.cljs$lang$maxFixedArity = (2);

/**
 * Returns a new date/time object corresponding to the given date/time
 *   moved backwards by the given Period(s).
 */
cljs_time.core.minus = (function cljs_time$core$minus(var_args){
var G__40836 = arguments.length;
switch (G__40836) {
case 2:
return cljs_time.core.minus.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
var args_arr__31475__auto__ = [];
var len__31452__auto___40838 = arguments.length;
var i__31453__auto___40839 = (0);
while(true){
if((i__31453__auto___40839 < len__31452__auto___40838)){
args_arr__31475__auto__.push((arguments[i__31453__auto___40839]));

var G__40840 = (i__31453__auto___40839 + (1));
i__31453__auto___40839 = G__40840;
continue;
} else {
}
break;
}

var argseq__31476__auto__ = (new cljs.core.IndexedSeq(args_arr__31475__auto__.slice((2)),(0),null));
return cljs_time.core.minus.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),argseq__31476__auto__);

}
});

cljs_time.core.minus.cljs$core$IFn$_invoke$arity$2 = (function (dt,p){
return cljs_time.core.minus_.call(null,dt,p);
});

cljs_time.core.minus.cljs$core$IFn$_invoke$arity$variadic = (function (dt,p,ps){
return cljs.core.reduce.call(null,cljs_time.core.minus_,cljs_time.core.minus_.call(null,dt,p),ps);
});

cljs_time.core.minus.cljs$lang$applyTo = (function (seq40833){
var G__40834 = cljs.core.first.call(null,seq40833);
var seq40833__$1 = cljs.core.next.call(null,seq40833);
var G__40835 = cljs.core.first.call(null,seq40833__$1);
var seq40833__$2 = cljs.core.next.call(null,seq40833__$1);
return cljs_time.core.minus.cljs$core$IFn$_invoke$arity$variadic(G__40834,G__40835,seq40833__$2);
});

cljs_time.core.minus.cljs$lang$maxFixedArity = (2);

/**
 * Returns a DateTime a supplied period before the present.
 * 
 *   e.g. `(-> 5 years ago)`
 */
cljs_time.core.ago = (function cljs_time$core$ago(period){
return cljs_time.core.minus.call(null,cljs_time.core.now.call(null),period);
});
/**
 * Returns a DateTime for yesterday relative to now
 */
cljs_time.core.yesterday = (function cljs_time$core$yesterday(){
return cljs_time.core.ago.call(null,cljs_time.core.days.call(null,(1)));
});
/**
 * Returns a DateTime a supplied period after the present.
 *   e.g. `(-> 30 minutes from-now)`
 */
cljs_time.core.from_now = (function cljs_time$core$from_now(period){
return cljs_time.core.plus.call(null,cljs_time.core.now.call(null),period);
});
/**
 * Returns the earliest of the supplied DateTimes
 */
cljs_time.core.earliest = (function cljs_time$core$earliest(var_args){
var G__40842 = arguments.length;
switch (G__40842) {
case 2:
return cljs_time.core.earliest.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 1:
return cljs_time.core.earliest.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.earliest.cljs$core$IFn$_invoke$arity$2 = (function (dt1,dt2){
if(cljs.core.truth_(cljs_time.core.before_QMARK_.call(null,dt1,dt2))){
return dt1;
} else {
return dt2;
}
});

cljs_time.core.earliest.cljs$core$IFn$_invoke$arity$1 = (function (dts){
return cljs.core.reduce.call(null,cljs_time.core.earliest,dts);
});

cljs_time.core.earliest.cljs$lang$maxFixedArity = 2;

/**
 * Returns the latest of the supplied DateTimes
 */
cljs_time.core.latest = (function cljs_time$core$latest(var_args){
var G__40845 = arguments.length;
switch (G__40845) {
case 2:
return cljs_time.core.latest.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 1:
return cljs_time.core.latest.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.latest.cljs$core$IFn$_invoke$arity$2 = (function (dt1,dt2){
if(cljs.core.truth_(cljs_time.core.after_QMARK_.call(null,dt1,dt2))){
return dt1;
} else {
return dt2;
}
});

cljs_time.core.latest.cljs$core$IFn$_invoke$arity$1 = (function (dts){
return cljs.core.reduce.call(null,cljs_time.core.latest,dts);
});

cljs_time.core.latest.cljs$lang$maxFixedArity = 2;

/**
 * Returns the start DateTime of an Interval.
 */
cljs_time.core.start = (function cljs_time$core$start(in$){
return new cljs.core.Keyword(null,"start","start",-355208981).cljs$core$IFn$_invoke$arity$1(in$);
});
/**
 * Returns the end DateTime of an Interval.
 */
cljs_time.core.end = (function cljs_time$core$end(in$){
return new cljs.core.Keyword(null,"end","end",-268185958).cljs$core$IFn$_invoke$arity$1(in$);
});
/**
 * Returns an Interval with an end DateTime the specified Period after the end
 *   of the given Interval
 */
cljs_time.core.extend = (function cljs_time$core$extend(var_args){
var args__31459__auto__ = [];
var len__31452__auto___40849 = arguments.length;
var i__31453__auto___40850 = (0);
while(true){
if((i__31453__auto___40850 < len__31452__auto___40849)){
args__31459__auto__.push((arguments[i__31453__auto___40850]));

var G__40851 = (i__31453__auto___40850 + (1));
i__31453__auto___40850 = G__40851;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return cljs_time.core.extend.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

cljs_time.core.extend.cljs$core$IFn$_invoke$arity$variadic = (function (in$,by){
return cljs.core.assoc.call(null,in$,new cljs.core.Keyword(null,"end","end",-268185958),cljs.core.apply.call(null,cljs_time.core.plus,cljs_time.core.end.call(null,in$),by));
});

cljs_time.core.extend.cljs$lang$maxFixedArity = (1);

cljs_time.core.extend.cljs$lang$applyTo = (function (seq40847){
var G__40848 = cljs.core.first.call(null,seq40847);
var seq40847__$1 = cljs.core.next.call(null,seq40847);
return cljs_time.core.extend.cljs$core$IFn$_invoke$arity$variadic(G__40848,seq40847__$1);
});

cljs_time.core.month_range = (function cljs_time$core$month_range(p__40854){
var map__40855 = p__40854;
var map__40855__$1 = ((((!((map__40855 == null)))?((((map__40855.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40855.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40855):map__40855);
var start = cljs.core.get.call(null,map__40855__$1,new cljs.core.Keyword(null,"start","start",-355208981));
var end = cljs.core.get.call(null,map__40855__$1,new cljs.core.Keyword(null,"end","end",-268185958));
return cljs.core.take_while.call(null,((function (map__40855,map__40855__$1,start,end){
return (function (p1__40853_SHARP_){
return cljs.core.not.call(null,cljs_time.core.after_QMARK_.call(null,p1__40853_SHARP_,end));
});})(map__40855,map__40855__$1,start,end))
,cljs.core.map.call(null,((function (map__40855,map__40855__$1,start,end){
return (function (p1__40852_SHARP_){
return cljs_time.core.plus.call(null,start,cljs_time.core.months.call(null,(p1__40852_SHARP_ + (1))));
});})(map__40855,map__40855__$1,start,end))
,cljs.core.range.call(null)));
});
cljs_time.core.total_days_in_whole_months = (function cljs_time$core$total_days_in_whole_months(interval){
return cljs.core.map.call(null,(function (p1__40857_SHARP_){
return p1__40857_SHARP_.getNumberOfDaysInMonth();
}),cljs_time.core.month_range.call(null,interval));
});
/**
 * Returns the number of months in the given Interval.
 * 
 *   For example, the interval 2nd Jan 2012 midnight to 2nd Feb 2012 midnight,
 *   returns 1 month.
 * 
 *   Likewise, 29th Dec 2011 midnight to 29th Feb 2012 midnight returns 2 months.
 * 
 *   But also, 31st Dec 2011 midnight to 29th Feb 2012 midnight returns 2 months.
 * 
 *   And, 28th Dec 2012 midnight to 28th Feb 2013 midnight returns 2 months.
 */
cljs_time.core.in_months_ = (function cljs_time$core$in_months_(p__40858){
var map__40859 = p__40858;
var map__40859__$1 = ((((!((map__40859 == null)))?((((map__40859.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40859.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40859):map__40859);
var interval = map__40859__$1;
var start = cljs.core.get.call(null,map__40859__$1,new cljs.core.Keyword(null,"start","start",-355208981));
var end = cljs.core.get.call(null,map__40859__$1,new cljs.core.Keyword(null,"end","end",-268185958));
return cljs.core.count.call(null,cljs_time.core.total_days_in_whole_months.call(null,interval));
});
/**
 * Returns the number of standard years in the given Interval.
 */
cljs_time.core.in_years_ = (function cljs_time$core$in_years_(p__40861){
var map__40862 = p__40861;
var map__40862__$1 = ((((!((map__40862 == null)))?((((map__40862.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40862.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40862):map__40862);
var start = cljs.core.get.call(null,map__40862__$1,new cljs.core.Keyword(null,"start","start",-355208981));
var end = cljs.core.get.call(null,map__40862__$1,new cljs.core.Keyword(null,"end","end",-268185958));
var sm = cljs_time.core.month.call(null,start);
var sd = cljs_time.core.day.call(null,start);
var em = cljs_time.core.month.call(null,end);
var ed = cljs_time.core.day.call(null,end);
var d1 = (cljs.core.truth_((function (){var and__30163__auto__ = cljs_time.core._EQ_.call(null,sm,(2));
if(cljs.core.truth_(and__30163__auto__)){
var and__30163__auto____$1 = cljs_time.core._EQ_.call(null,sd,(29));
if(cljs.core.truth_(and__30163__auto____$1)){
var and__30163__auto____$2 = cljs_time.core._EQ_.call(null,em,(2));
if(cljs.core.truth_(and__30163__auto____$2)){
return cljs_time.core._EQ_.call(null,ed,(28));
} else {
return and__30163__auto____$2;
}
} else {
return and__30163__auto____$1;
}
} else {
return and__30163__auto__;
}
})())?(0):(cljs.core.truth_(cljs_time.core.before_QMARK_.call(null,cljs_time.core.date_time.call(null,cljs_time.core.year.call(null,start),sm,sd),cljs_time.core.date_time.call(null,cljs_time.core.year.call(null,start),em,ed)))?(0):(cljs.core.truth_(cljs_time.core.after_QMARK_.call(null,cljs_time.core.date_time.call(null,cljs_time.core.year.call(null,start),sm,sd),cljs_time.core.date_time.call(null,cljs_time.core.year.call(null,start),em,ed)))?(1):(0)
)));
return ((cljs_time.core.year.call(null,end) - cljs_time.core.year.call(null,start)) - d1);
});
cljs_time.core.conversion_error = (function cljs_time$core$conversion_error(from,to){
var from__$1 = clojure.string.capitalize.call(null,cljs.core.name.call(null,from));
var to__$1 = cljs.core.name.call(null,to);
throw cljs.core.ex_info.call(null,cljs_time.internal.core.format.call(null,"%s cannot be converted to %s",from__$1,to__$1),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"unsupported-operation","unsupported-operation",1890540953)], null));
});
cljs_time.core.Period.prototype.cljs_time$core$InTimeUnitProtocol$ = cljs.core.PROTOCOL_SENTINEL;

cljs_time.core.Period.prototype.cljs_time$core$InTimeUnitProtocol$in_millis$arity$1 = (function (p__40864){
var map__40865 = p__40864;
var map__40865__$1 = ((((!((map__40865 == null)))?((((map__40865.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40865.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40865):map__40865);
var millis = cljs.core.get.call(null,map__40865__$1,new cljs.core.Keyword(null,"millis","millis",-1338288387));
var seconds = cljs.core.get.call(null,map__40865__$1,new cljs.core.Keyword(null,"seconds","seconds",-445266194));
var minutes = cljs.core.get.call(null,map__40865__$1,new cljs.core.Keyword(null,"minutes","minutes",1319166394));
var hours = cljs.core.get.call(null,map__40865__$1,new cljs.core.Keyword(null,"hours","hours",58380855));
var days = cljs.core.get.call(null,map__40865__$1,new cljs.core.Keyword(null,"days","days",-1394072564));
var weeks = cljs.core.get.call(null,map__40865__$1,new cljs.core.Keyword(null,"weeks","weeks",1844596125));
var months = cljs.core.get.call(null,map__40865__$1,new cljs.core.Keyword(null,"months","months",-45571637));
var years = cljs.core.get.call(null,map__40865__$1,new cljs.core.Keyword(null,"years","years",-1298579689));
var map__40867 = this;
var map__40867__$1 = ((((!((map__40867 == null)))?((((map__40867.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40867.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40867):map__40867);
var millis__$1 = cljs.core.get.call(null,map__40867__$1,new cljs.core.Keyword(null,"millis","millis",-1338288387));
var seconds__$1 = cljs.core.get.call(null,map__40867__$1,new cljs.core.Keyword(null,"seconds","seconds",-445266194));
var minutes__$1 = cljs.core.get.call(null,map__40867__$1,new cljs.core.Keyword(null,"minutes","minutes",1319166394));
var hours__$1 = cljs.core.get.call(null,map__40867__$1,new cljs.core.Keyword(null,"hours","hours",58380855));
var days__$1 = cljs.core.get.call(null,map__40867__$1,new cljs.core.Keyword(null,"days","days",-1394072564));
var weeks__$1 = cljs.core.get.call(null,map__40867__$1,new cljs.core.Keyword(null,"weeks","weeks",1844596125));
var months__$1 = cljs.core.get.call(null,map__40867__$1,new cljs.core.Keyword(null,"months","months",-45571637));
var years__$1 = cljs.core.get.call(null,map__40867__$1,new cljs.core.Keyword(null,"years","years",-1298579689));
if(cljs.core.truth_(months__$1)){
return cljs_time.core.conversion_error.call(null,new cljs.core.Keyword(null,"months","months",-45571637),new cljs.core.Keyword(null,"millis","millis",-1338288387));
} else {
if(cljs.core.truth_(years__$1)){
return cljs_time.core.conversion_error.call(null,new cljs.core.Keyword(null,"years","years",-1298579689),new cljs.core.Keyword(null,"millis","millis",-1338288387));
} else {
return (((((millis__$1 + (seconds__$1 * (1000))) + ((minutes__$1 * (60)) * (1000))) + (((hours__$1 * (60)) * (60)) * (1000))) + ((((days__$1 * (24)) * (60)) * (60)) * (1000))) + (((((weeks__$1 * (7)) * (24)) * (60)) * (60)) * (1000)));

}
}
});

cljs_time.core.Period.prototype.cljs_time$core$InTimeUnitProtocol$in_seconds$arity$1 = (function (this$){
var this$__$1 = this;
return ((cljs_time.core.in_millis.call(null,this$__$1) / (1000)) | (0));
});

cljs_time.core.Period.prototype.cljs_time$core$InTimeUnitProtocol$in_minutes$arity$1 = (function (this$){
var this$__$1 = this;
return ((cljs_time.core.in_seconds.call(null,this$__$1) / (60)) | (0));
});

cljs_time.core.Period.prototype.cljs_time$core$InTimeUnitProtocol$in_hours$arity$1 = (function (this$){
var this$__$1 = this;
return ((cljs_time.core.in_minutes.call(null,this$__$1) / (60)) | (0));
});

cljs_time.core.Period.prototype.cljs_time$core$InTimeUnitProtocol$in_days$arity$1 = (function (this$){
var this$__$1 = this;
return ((cljs_time.core.in_hours.call(null,this$__$1) / (24)) | (0));
});

cljs_time.core.Period.prototype.cljs_time$core$InTimeUnitProtocol$in_weeks$arity$1 = (function (this$){
var this$__$1 = this;
return ((cljs_time.core.in_days.call(null,this$__$1) / (7)) | (0));
});

cljs_time.core.Period.prototype.cljs_time$core$InTimeUnitProtocol$in_months$arity$1 = (function (p__40869){
var map__40870 = p__40869;
var map__40870__$1 = ((((!((map__40870 == null)))?((((map__40870.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40870.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40870):map__40870);
var millis = cljs.core.get.call(null,map__40870__$1,new cljs.core.Keyword(null,"millis","millis",-1338288387));
var seconds = cljs.core.get.call(null,map__40870__$1,new cljs.core.Keyword(null,"seconds","seconds",-445266194));
var minutes = cljs.core.get.call(null,map__40870__$1,new cljs.core.Keyword(null,"minutes","minutes",1319166394));
var hours = cljs.core.get.call(null,map__40870__$1,new cljs.core.Keyword(null,"hours","hours",58380855));
var days = cljs.core.get.call(null,map__40870__$1,new cljs.core.Keyword(null,"days","days",-1394072564));
var weeks = cljs.core.get.call(null,map__40870__$1,new cljs.core.Keyword(null,"weeks","weeks",1844596125));
var months = cljs.core.get.call(null,map__40870__$1,new cljs.core.Keyword(null,"months","months",-45571637));
var years = cljs.core.get.call(null,map__40870__$1,new cljs.core.Keyword(null,"years","years",-1298579689));
var map__40872 = this;
var map__40872__$1 = ((((!((map__40872 == null)))?((((map__40872.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40872.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40872):map__40872);
var millis__$1 = cljs.core.get.call(null,map__40872__$1,new cljs.core.Keyword(null,"millis","millis",-1338288387));
var seconds__$1 = cljs.core.get.call(null,map__40872__$1,new cljs.core.Keyword(null,"seconds","seconds",-445266194));
var minutes__$1 = cljs.core.get.call(null,map__40872__$1,new cljs.core.Keyword(null,"minutes","minutes",1319166394));
var hours__$1 = cljs.core.get.call(null,map__40872__$1,new cljs.core.Keyword(null,"hours","hours",58380855));
var days__$1 = cljs.core.get.call(null,map__40872__$1,new cljs.core.Keyword(null,"days","days",-1394072564));
var weeks__$1 = cljs.core.get.call(null,map__40872__$1,new cljs.core.Keyword(null,"weeks","weeks",1844596125));
var months__$1 = cljs.core.get.call(null,map__40872__$1,new cljs.core.Keyword(null,"months","months",-45571637));
var years__$1 = cljs.core.get.call(null,map__40872__$1,new cljs.core.Keyword(null,"years","years",-1298579689));
if(cljs.core.truth_(millis__$1)){
return cljs_time.core.conversion_error.call(null,new cljs.core.Keyword(null,"millis","millis",-1338288387),new cljs.core.Keyword(null,"months","months",-45571637));
} else {
if(cljs.core.truth_(seconds__$1)){
return cljs_time.core.conversion_error.call(null,new cljs.core.Keyword(null,"seconds","seconds",-445266194),new cljs.core.Keyword(null,"months","months",-45571637));
} else {
if(cljs.core.truth_(minutes__$1)){
return cljs_time.core.conversion_error.call(null,new cljs.core.Keyword(null,"minutes","minutes",1319166394),new cljs.core.Keyword(null,"months","months",-45571637));
} else {
if(cljs.core.truth_(hours__$1)){
return cljs_time.core.conversion_error.call(null,new cljs.core.Keyword(null,"hours","hours",58380855),new cljs.core.Keyword(null,"months","months",-45571637));
} else {
if(cljs.core.truth_(days__$1)){
return cljs_time.core.conversion_error.call(null,new cljs.core.Keyword(null,"days","days",-1394072564),new cljs.core.Keyword(null,"months","months",-45571637));
} else {
if(cljs.core.truth_(weeks__$1)){
return cljs_time.core.conversion_error.call(null,new cljs.core.Keyword(null,"weeks","weeks",1844596125),new cljs.core.Keyword(null,"months","months",-45571637));
} else {
if(cljs.core.truth_(months__$1)){
return (months__$1 + ((function (){var or__30175__auto__ = years__$1;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return (0);
}
})() * (12)));
} else {
if(cljs.core.truth_(years__$1)){
return (years__$1 * (12));
} else {
return null;
}
}
}
}
}
}
}
}
});

cljs_time.core.Period.prototype.cljs_time$core$InTimeUnitProtocol$in_years$arity$1 = (function (p__40874){
var map__40875 = p__40874;
var map__40875__$1 = ((((!((map__40875 == null)))?((((map__40875.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40875.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40875):map__40875);
var millis = cljs.core.get.call(null,map__40875__$1,new cljs.core.Keyword(null,"millis","millis",-1338288387));
var seconds = cljs.core.get.call(null,map__40875__$1,new cljs.core.Keyword(null,"seconds","seconds",-445266194));
var minutes = cljs.core.get.call(null,map__40875__$1,new cljs.core.Keyword(null,"minutes","minutes",1319166394));
var hours = cljs.core.get.call(null,map__40875__$1,new cljs.core.Keyword(null,"hours","hours",58380855));
var days = cljs.core.get.call(null,map__40875__$1,new cljs.core.Keyword(null,"days","days",-1394072564));
var weeks = cljs.core.get.call(null,map__40875__$1,new cljs.core.Keyword(null,"weeks","weeks",1844596125));
var months = cljs.core.get.call(null,map__40875__$1,new cljs.core.Keyword(null,"months","months",-45571637));
var years = cljs.core.get.call(null,map__40875__$1,new cljs.core.Keyword(null,"years","years",-1298579689));
var map__40877 = this;
var map__40877__$1 = ((((!((map__40877 == null)))?((((map__40877.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40877.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40877):map__40877);
var millis__$1 = cljs.core.get.call(null,map__40877__$1,new cljs.core.Keyword(null,"millis","millis",-1338288387));
var seconds__$1 = cljs.core.get.call(null,map__40877__$1,new cljs.core.Keyword(null,"seconds","seconds",-445266194));
var minutes__$1 = cljs.core.get.call(null,map__40877__$1,new cljs.core.Keyword(null,"minutes","minutes",1319166394));
var hours__$1 = cljs.core.get.call(null,map__40877__$1,new cljs.core.Keyword(null,"hours","hours",58380855));
var days__$1 = cljs.core.get.call(null,map__40877__$1,new cljs.core.Keyword(null,"days","days",-1394072564));
var weeks__$1 = cljs.core.get.call(null,map__40877__$1,new cljs.core.Keyword(null,"weeks","weeks",1844596125));
var months__$1 = cljs.core.get.call(null,map__40877__$1,new cljs.core.Keyword(null,"months","months",-45571637));
var years__$1 = cljs.core.get.call(null,map__40877__$1,new cljs.core.Keyword(null,"years","years",-1298579689));
if(cljs.core.truth_(millis__$1)){
return cljs_time.core.conversion_error.call(null,new cljs.core.Keyword(null,"millis","millis",-1338288387),new cljs.core.Keyword(null,"years","years",-1298579689));
} else {
if(cljs.core.truth_(seconds__$1)){
return cljs_time.core.conversion_error.call(null,new cljs.core.Keyword(null,"seconds","seconds",-445266194),new cljs.core.Keyword(null,"years","years",-1298579689));
} else {
if(cljs.core.truth_(minutes__$1)){
return cljs_time.core.conversion_error.call(null,new cljs.core.Keyword(null,"minutes","minutes",1319166394),new cljs.core.Keyword(null,"years","years",-1298579689));
} else {
if(cljs.core.truth_(hours__$1)){
return cljs_time.core.conversion_error.call(null,new cljs.core.Keyword(null,"hours","hours",58380855),new cljs.core.Keyword(null,"years","years",-1298579689));
} else {
if(cljs.core.truth_(days__$1)){
return cljs_time.core.conversion_error.call(null,new cljs.core.Keyword(null,"days","days",-1394072564),new cljs.core.Keyword(null,"years","years",-1298579689));
} else {
if(cljs.core.truth_(weeks__$1)){
return cljs_time.core.conversion_error.call(null,new cljs.core.Keyword(null,"weeks","weeks",1844596125),new cljs.core.Keyword(null,"years","years",-1298579689));
} else {
if(cljs.core.truth_(months__$1)){
return (((months__$1 / (12)) + years__$1) | (0));
} else {
if(cljs.core.truth_(years__$1)){
return years__$1;
} else {
return null;
}
}
}
}
}
}
}
}
});

cljs_time.core.Interval.prototype.cljs_time$core$InTimeUnitProtocol$ = cljs.core.PROTOCOL_SENTINEL;

cljs_time.core.Interval.prototype.cljs_time$core$InTimeUnitProtocol$in_millis$arity$1 = (function (p__40879){
var map__40880 = p__40879;
var map__40880__$1 = ((((!((map__40880 == null)))?((((map__40880.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40880.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40880):map__40880);
var start = cljs.core.get.call(null,map__40880__$1,new cljs.core.Keyword(null,"start","start",-355208981));
var end = cljs.core.get.call(null,map__40880__$1,new cljs.core.Keyword(null,"end","end",-268185958));
var map__40882 = this;
var map__40882__$1 = ((((!((map__40882 == null)))?((((map__40882.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40882.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40882):map__40882);
var start__$1 = cljs.core.get.call(null,map__40882__$1,new cljs.core.Keyword(null,"start","start",-355208981));
var end__$1 = cljs.core.get.call(null,map__40882__$1,new cljs.core.Keyword(null,"end","end",-268185958));
return (end__$1.getTime() - start__$1.getTime());
});

cljs_time.core.Interval.prototype.cljs_time$core$InTimeUnitProtocol$in_seconds$arity$1 = (function (this$){
var this$__$1 = this;
return ((cljs_time.core.in_millis.call(null,this$__$1) / (1000)) | (0));
});

cljs_time.core.Interval.prototype.cljs_time$core$InTimeUnitProtocol$in_minutes$arity$1 = (function (this$){
var this$__$1 = this;
return ((cljs_time.core.in_seconds.call(null,this$__$1) / (60)) | (0));
});

cljs_time.core.Interval.prototype.cljs_time$core$InTimeUnitProtocol$in_hours$arity$1 = (function (this$){
var this$__$1 = this;
return ((cljs_time.core.in_minutes.call(null,this$__$1) / (60)) | (0));
});

cljs_time.core.Interval.prototype.cljs_time$core$InTimeUnitProtocol$in_days$arity$1 = (function (this$){
var this$__$1 = this;
return ((cljs_time.core.in_hours.call(null,this$__$1) / (24)) | (0));
});

cljs_time.core.Interval.prototype.cljs_time$core$InTimeUnitProtocol$in_weeks$arity$1 = (function (this$){
var this$__$1 = this;
return ((cljs_time.core.in_days.call(null,this$__$1) / (7)) | (0));
});

cljs_time.core.Interval.prototype.cljs_time$core$InTimeUnitProtocol$in_months$arity$1 = (function (this$){
var this$__$1 = this;
return cljs_time.core.in_months_.call(null,this$__$1);
});

cljs_time.core.Interval.prototype.cljs_time$core$InTimeUnitProtocol$in_years$arity$1 = (function (this$){
var this$__$1 = this;
return cljs_time.core.in_years_.call(null,this$__$1);
});
/**
 * With 2 arguments: Returns true if the given Interval contains the given
 *   DateTime. Note that if the DateTime is exactly equal to the
 *   end of the interval, this function returns false.
 * 
 *   With 3 arguments: Returns true if the start DateTime is
 *   equal to or before and the end DateTime is equal to or after the test
 *   DateTime.
 */
cljs_time.core.within_QMARK_ = (function cljs_time$core$within_QMARK_(var_args){
var G__40885 = arguments.length;
switch (G__40885) {
case 2:
return cljs_time.core.within_QMARK_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs_time.core.within_QMARK_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.within_QMARK_.cljs$core$IFn$_invoke$arity$2 = (function (p__40886,date){
var map__40887 = p__40886;
var map__40887__$1 = ((((!((map__40887 == null)))?((((map__40887.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40887.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40887):map__40887);
var start = cljs.core.get.call(null,map__40887__$1,new cljs.core.Keyword(null,"start","start",-355208981));
var end = cljs.core.get.call(null,map__40887__$1,new cljs.core.Keyword(null,"end","end",-268185958));
return cljs_time.core.within_QMARK_.call(null,start,end,date);
});

cljs_time.core.within_QMARK_.cljs$core$IFn$_invoke$arity$3 = (function (start,end,date){
var or__30175__auto__ = cljs_time.core._EQ_.call(null,start,date);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
var and__30163__auto__ = cljs_time.core.before_QMARK_.call(null,start,date);
if(cljs.core.truth_(and__30163__auto__)){
return cljs_time.core.after_QMARK_.call(null,end,date);
} else {
return and__30163__auto__;
}
}
});

cljs_time.core.within_QMARK_.cljs$lang$maxFixedArity = 3;

/**
 * With 2 arguments: Returns true of the two given Intervals overlap.
 *   Note that intervals that satisfy abuts? do not satisfy overlaps?
 * 
 *   With 4 arguments: Returns true if the range specified by start-a and end-a
 *   overlaps with the range specified by start-b and end-b.
 */
cljs_time.core.overlaps_QMARK_ = (function cljs_time$core$overlaps_QMARK_(var_args){
var G__40891 = arguments.length;
switch (G__40891) {
case 2:
return cljs_time.core.overlaps_QMARK_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 4:
return cljs_time.core.overlaps_QMARK_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.overlaps_QMARK_.cljs$core$IFn$_invoke$arity$2 = (function (p__40892,p__40893){
var map__40894 = p__40892;
var map__40894__$1 = ((((!((map__40894 == null)))?((((map__40894.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40894.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40894):map__40894);
var start_a = cljs.core.get.call(null,map__40894__$1,new cljs.core.Keyword(null,"start","start",-355208981));
var end_a = cljs.core.get.call(null,map__40894__$1,new cljs.core.Keyword(null,"end","end",-268185958));
var map__40895 = p__40893;
var map__40895__$1 = ((((!((map__40895 == null)))?((((map__40895.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40895.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40895):map__40895);
var start_b = cljs.core.get.call(null,map__40895__$1,new cljs.core.Keyword(null,"start","start",-355208981));
var end_b = cljs.core.get.call(null,map__40895__$1,new cljs.core.Keyword(null,"end","end",-268185958));
var and__30163__auto__ = cljs.core.not.call(null,(function (){var or__30175__auto__ = cljs_time.core._EQ_.call(null,start_a,end_b);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return cljs_time.core._EQ_.call(null,end_a,start_b);
}
})());
if(and__30163__auto__){
return cljs_time.core.overlaps_QMARK_.call(null,start_a,end_a,start_b,end_b);
} else {
return and__30163__auto__;
}
});

cljs_time.core.overlaps_QMARK_.cljs$core$IFn$_invoke$arity$4 = (function (start_a,end_a,start_b,end_b){
var or__30175__auto__ = (function (){var and__30163__auto__ = cljs_time.core.before_QMARK_.call(null,start_b,end_a);
if(cljs.core.truth_(and__30163__auto__)){
return cljs_time.core.after_QMARK_.call(null,end_b,start_a);
} else {
return and__30163__auto__;
}
})();
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
var or__30175__auto____$1 = (function (){var and__30163__auto__ = cljs_time.core.after_QMARK_.call(null,end_b,start_a);
if(cljs.core.truth_(and__30163__auto__)){
return cljs_time.core.before_QMARK_.call(null,start_b,end_a);
} else {
return and__30163__auto__;
}
})();
if(cljs.core.truth_(or__30175__auto____$1)){
return or__30175__auto____$1;
} else {
var or__30175__auto____$2 = cljs_time.core._EQ_.call(null,start_a,end_b);
if(cljs.core.truth_(or__30175__auto____$2)){
return or__30175__auto____$2;
} else {
return cljs_time.core._EQ_.call(null,start_b,end_a);
}
}
}
});

cljs_time.core.overlaps_QMARK_.cljs$lang$maxFixedArity = 4;

/**
 * Returns an Interval representing the overlap of the specified Intervals.
 *  Returns nil if the Intervals do not overlap.
 *  The first argument must not be nil.
 *  If the second argument is nil then the overlap of the first argument
 *  and a zero duration interval with both start and end times equal to the
 *  current time is returned.
 */
cljs_time.core.overlap = (function cljs_time$core$overlap(i_a,i_b){
if((i_b == null)){
var n = cljs_time.core.now.call(null);
return cljs_time.core.overlap.call(null,i_a,cljs_time.core.interval.call(null,n,n));
} else {
if(cljs.core.truth_(cljs_time.core.overlaps_QMARK_.call(null,i_a,i_b))){
return cljs_time.core.interval.call(null,cljs_time.core.latest.call(null,cljs_time.core.start.call(null,i_a),cljs_time.core.start.call(null,i_b)),cljs_time.core.earliest.call(null,cljs_time.core.end.call(null,i_a),cljs_time.core.end.call(null,i_b)));
} else {
return null;

}
}
});
/**
 * Returns true if Interval a abuts b, i.e. then end of a is exactly the
 *   beginning of b.
 */
cljs_time.core.abuts_QMARK_ = (function cljs_time$core$abuts_QMARK_(p__40899,p__40900){
var map__40901 = p__40899;
var map__40901__$1 = ((((!((map__40901 == null)))?((((map__40901.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40901.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40901):map__40901);
var start_a = cljs.core.get.call(null,map__40901__$1,new cljs.core.Keyword(null,"start","start",-355208981));
var end_a = cljs.core.get.call(null,map__40901__$1,new cljs.core.Keyword(null,"end","end",-268185958));
var map__40902 = p__40900;
var map__40902__$1 = ((((!((map__40902 == null)))?((((map__40902.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40902.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40902):map__40902);
var start_b = cljs.core.get.call(null,map__40902__$1,new cljs.core.Keyword(null,"start","start",-355208981));
var end_b = cljs.core.get.call(null,map__40902__$1,new cljs.core.Keyword(null,"end","end",-268185958));
var or__30175__auto__ = cljs_time.core._EQ_.call(null,start_a,end_b);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return cljs_time.core._EQ_.call(null,end_a,start_b);
}
});
cljs_time.core.date_QMARK_ = (function cljs_time$core$date_QMARK_(x){
if(!((x == null))){
if((false) || ((cljs.core.PROTOCOL_SENTINEL === x.cljs_time$core$DateTimeProtocol$))){
return true;
} else {
if((!x.cljs$lang$protocol_mask$partition$)){
return cljs.core.native_satisfies_QMARK_.call(null,cljs_time.core.DateTimeProtocol,x);
} else {
return false;
}
}
} else {
return cljs.core.native_satisfies_QMARK_.call(null,cljs_time.core.DateTimeProtocol,x);
}
});
cljs_time.core.interval_QMARK_ = (function cljs_time$core$interval_QMARK_(x){
return (x instanceof cljs_time.core.Interval);
});
cljs_time.core.period_QMARK_ = (function cljs_time$core$period_QMARK_(x){
return (x instanceof cljs_time.core.Period);
});
cljs_time.core.period_type_QMARK_ = (function cljs_time$core$period_type_QMARK_(type,x){
var and__30163__auto__ = cljs_time.core.period_QMARK_.call(null,x);
if(cljs.core.truth_(and__30163__auto__)){
return cljs.core.contains_QMARK_.call(null,x,type);
} else {
return and__30163__auto__;
}
});
/**
 * Returns true if the given value is an instance of Years
 */
cljs_time.core.years_QMARK_ = (function cljs_time$core$years_QMARK_(val){
return cljs_time.core.period_type_QMARK_.call(null,new cljs.core.Keyword(null,"years","years",-1298579689),val);
});
/**
 * Returns true if the given value is an instance of Months
 */
cljs_time.core.months_QMARK_ = (function cljs_time$core$months_QMARK_(val){
return cljs_time.core.period_type_QMARK_.call(null,new cljs.core.Keyword(null,"months","months",-45571637),val);
});
/**
 * Returns true if the given value is an instance of Weeks
 */
cljs_time.core.weeks_QMARK_ = (function cljs_time$core$weeks_QMARK_(val){
return cljs_time.core.period_type_QMARK_.call(null,new cljs.core.Keyword(null,"weeks","weeks",1844596125),val);
});
/**
 * Returns true if the given value is an instance of Days
 */
cljs_time.core.days_QMARK_ = (function cljs_time$core$days_QMARK_(val){
return cljs_time.core.period_type_QMARK_.call(null,new cljs.core.Keyword(null,"days","days",-1394072564),val);
});
/**
 * Returns true if the given value is an instance of Hours
 */
cljs_time.core.hours_QMARK_ = (function cljs_time$core$hours_QMARK_(val){
return cljs_time.core.period_type_QMARK_.call(null,new cljs.core.Keyword(null,"hours","hours",58380855),val);
});
/**
 * Returns true if the given value is an instance of Minutes
 */
cljs_time.core.minutes_QMARK_ = (function cljs_time$core$minutes_QMARK_(val){
return cljs_time.core.period_type_QMARK_.call(null,new cljs.core.Keyword(null,"minutes","minutes",1319166394),val);
});
/**
 * Returns true if the given value is an instance of Seconds
 */
cljs_time.core.seconds_QMARK_ = (function cljs_time$core$seconds_QMARK_(val){
return cljs_time.core.period_type_QMARK_.call(null,new cljs.core.Keyword(null,"seconds","seconds",-445266194),val);
});
cljs_time.core.mins_ago = (function cljs_time$core$mins_ago(d){
return cljs_time.core.in_minutes.call(null,cljs_time.core.interval.call(null,d,cljs_time.core.now.call(null)));
});
cljs_time.core.last_day_of_the_month = (function cljs_time$core$last_day_of_the_month(var_args){
var G__40907 = arguments.length;
switch (G__40907) {
case 1:
return cljs_time.core.last_day_of_the_month.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs_time.core.last_day_of_the_month.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.last_day_of_the_month.cljs$core$IFn$_invoke$arity$1 = (function (dt){
return cljs_time.core.last_day_of_the_month_.call(null,dt);
});

cljs_time.core.last_day_of_the_month.cljs$core$IFn$_invoke$arity$2 = (function (year,month){
return cljs_time.core.last_day_of_the_month_.call(null,cljs_time.core.date_time.call(null,year,month));
});

cljs_time.core.last_day_of_the_month.cljs$lang$maxFixedArity = 2;

cljs_time.core.number_of_days_in_the_month = (function cljs_time$core$number_of_days_in_the_month(var_args){
var G__40910 = arguments.length;
switch (G__40910) {
case 1:
return cljs_time.core.number_of_days_in_the_month.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs_time.core.number_of_days_in_the_month.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.number_of_days_in_the_month.cljs$core$IFn$_invoke$arity$1 = (function (dt){
return cljs_time.core.number_of_days_in_the_month.call(null,cljs_time.core.year.call(null,dt),cljs_time.core.month.call(null,dt));
});

cljs_time.core.number_of_days_in_the_month.cljs$core$IFn$_invoke$arity$2 = (function (year,month){
return cljs_time.core.last_day_of_the_month.call(null,year,month).getDate();
});

cljs_time.core.number_of_days_in_the_month.cljs$lang$maxFixedArity = 2;

cljs_time.core.first_day_of_the_month = (function cljs_time$core$first_day_of_the_month(var_args){
var G__40913 = arguments.length;
switch (G__40913) {
case 1:
return cljs_time.core.first_day_of_the_month.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs_time.core.first_day_of_the_month.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.first_day_of_the_month.cljs$core$IFn$_invoke$arity$1 = (function (dt){
return cljs_time.core.first_day_of_the_month_.call(null,dt);
});

cljs_time.core.first_day_of_the_month.cljs$core$IFn$_invoke$arity$2 = (function (year,month){
return cljs_time.core.first_day_of_the_month_.call(null,cljs_time.core.date_time.call(null,year,month));
});

cljs_time.core.first_day_of_the_month.cljs$lang$maxFixedArity = 2;


/**
 * @interface
 */
cljs_time.core.IToPeriod = function(){};

cljs_time.core.__GT_period = (function cljs_time$core$__GT_period(obj){
if((!((obj == null))) && (!((obj.cljs_time$core$IToPeriod$__GT_period$arity$1 == null)))){
return obj.cljs_time$core$IToPeriod$__GT_period$arity$1(obj);
} else {
var x__30908__auto__ = (((obj == null))?null:obj);
var m__30909__auto__ = (cljs_time.core.__GT_period[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,obj);
} else {
var m__30909__auto____$1 = (cljs_time.core.__GT_period["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,obj);
} else {
throw cljs.core.missing_protocol.call(null,"IToPeriod.->period",obj);
}
}
}
});

cljs_time.core.Interval.prototype.cljs_time$core$IToPeriod$ = cljs.core.PROTOCOL_SENTINEL;

cljs_time.core.Interval.prototype.cljs_time$core$IToPeriod$__GT_period$arity$1 = (function (p__40915){
var map__40916 = p__40915;
var map__40916__$1 = ((((!((map__40916 == null)))?((((map__40916.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40916.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40916):map__40916);
var interval = map__40916__$1;
var start = cljs.core.get.call(null,map__40916__$1,new cljs.core.Keyword(null,"start","start",-355208981));
var end = cljs.core.get.call(null,map__40916__$1,new cljs.core.Keyword(null,"end","end",-268185958));
var map__40918 = this;
var map__40918__$1 = ((((!((map__40918 == null)))?((((map__40918.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__40918.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__40918):map__40918);
var interval__$1 = map__40918__$1;
var start__$1 = cljs.core.get.call(null,map__40918__$1,new cljs.core.Keyword(null,"start","start",-355208981));
var end__$1 = cljs.core.get.call(null,map__40918__$1,new cljs.core.Keyword(null,"end","end",-268185958));
var years = cljs_time.core.in_years.call(null,interval__$1);
var start_year = cljs_time.core.year.call(null,start__$1);
var leap_years = cljs.core.count.call(null,cljs.core.remove.call(null,cljs.core.false_QMARK_,cljs.core.map.call(null,cljs_time.internal.core.leap_year_QMARK_,cljs.core.range.call(null,start_year,(start_year + years)))));
var start_month = cljs_time.core.month.call(null,start__$1);
var days_in_months = cljs_time.core.total_days_in_whole_months.call(null,interval__$1);
var months = (cljs.core.count.call(null,days_in_months) - (years * (12)));
var days_to_remove = cljs.core.reduce.call(null,cljs.core._PLUS_,days_in_months);
var days = (cljs_time.core.in_days.call(null,interval__$1) - days_to_remove);
var hours_to_remove = ((24) * (days + days_to_remove));
var hours = (cljs_time.core.in_hours.call(null,interval__$1) - hours_to_remove);
var minutes_to_remove = ((60) * (hours + hours_to_remove));
var minutes = (cljs_time.core.in_minutes.call(null,interval__$1) - minutes_to_remove);
var seconds_to_remove = ((60) * (minutes + minutes_to_remove));
var seconds = (cljs_time.core.in_seconds.call(null,interval__$1) - seconds_to_remove);
return cljs_time.core.period.call(null,new cljs.core.Keyword(null,"years","years",-1298579689),years,new cljs.core.Keyword(null,"months","months",-45571637),months,new cljs.core.Keyword(null,"days","days",-1394072564),days,new cljs.core.Keyword(null,"hours","hours",58380855),hours,new cljs.core.Keyword(null,"minutes","minutes",1319166394),minutes,new cljs.core.Keyword(null,"seconds","seconds",-445266194),seconds,new cljs.core.Keyword(null,"millis","millis",-1338288387),(cljs_time.core.in_millis.call(null,interval__$1) - ((1000) * (seconds + seconds_to_remove))));
});

cljs_time.core.Period.prototype.cljs_time$core$IToPeriod$ = cljs.core.PROTOCOL_SENTINEL;

cljs_time.core.Period.prototype.cljs_time$core$IToPeriod$__GT_period$arity$1 = (function (period){
var period__$1 = this;
return period__$1;
});
cljs_time.core.today_at = (function cljs_time$core$today_at(var_args){
var G__40921 = arguments.length;
switch (G__40921) {
case 4:
return cljs_time.core.today_at.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 3:
return cljs_time.core.today_at.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 2:
return cljs_time.core.today_at.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs_time.core.today_at.cljs$core$IFn$_invoke$arity$4 = (function (hours,minutes,seconds,millis){
var G__40922 = (new goog.date.UtcDateTime());
G__40922.setHours(hours);

G__40922.setMinutes(minutes);

G__40922.setSeconds(seconds);

G__40922.setMilliseconds(millis);

return G__40922;
});

cljs_time.core.today_at.cljs$core$IFn$_invoke$arity$3 = (function (hours,minutes,seconds){
return cljs_time.core.today_at.call(null,hours,minutes,seconds,(0));
});

cljs_time.core.today_at.cljs$core$IFn$_invoke$arity$2 = (function (hours,minutes){
return cljs_time.core.today_at.call(null,hours,minutes,(0));
});

cljs_time.core.today_at.cljs$lang$maxFixedArity = 4;

cljs_time.core.do_at_STAR_ = (function cljs_time$core$do_at_STAR_(base_date_time,body_fn){
var _STAR_ms_fn_STAR_40924 = cljs_time.core._STAR_ms_fn_STAR_;
cljs_time.core._STAR_ms_fn_STAR_ = cljs_time.core.static_ms_fn.call(null,base_date_time.getTime());

try{return body_fn.call(null);
}finally {cljs_time.core._STAR_ms_fn_STAR_ = _STAR_ms_fn_STAR_40924;
}});
/**
 * Floors the given date-time dt to the given time unit dt-fn,
 *  e.g. (floor (now) hour) returns (now) for all units
 *  up to and including the hour
 */
cljs_time.core.floor = (function cljs_time$core$floor(dt,dt_fn){
var dt_fns = new cljs.core.PersistentVector(null, 7, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs_time.core.year,cljs_time.core.month,cljs_time.core.day,cljs_time.core.hour,cljs_time.core.minute,cljs_time.core.second,cljs_time.core.milli], null);
return cljs.core.apply.call(null,cljs_time.core.date_time,cljs.core.map.call(null,cljs.core.apply,cljs.core.concat.call(null,cljs.core.take_while.call(null,cljs.core.partial.call(null,cljs.core.not_EQ_,dt_fn),dt_fns),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [dt_fn], null)),cljs.core.repeat.call(null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [dt], null))));
});

//# sourceMappingURL=core.js.map?rel=1510137277174
