// Compiled by ClojureScript 1.9.908 {}
goog.provide('cljs.core.async');
goog.require('cljs.core');
goog.require('cljs.core.async.impl.protocols');
goog.require('cljs.core.async.impl.channels');
goog.require('cljs.core.async.impl.buffers');
goog.require('cljs.core.async.impl.timers');
goog.require('cljs.core.async.impl.dispatch');
goog.require('cljs.core.async.impl.ioc_helpers');
cljs.core.async.fn_handler = (function cljs$core$async$fn_handler(var_args){
var G__54100 = arguments.length;
switch (G__54100) {
case 1:
return cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$1 = (function (f){
return cljs.core.async.fn_handler.call(null,f,true);
});

cljs.core.async.fn_handler.cljs$core$IFn$_invoke$arity$2 = (function (f,blockable){
if(typeof cljs.core.async.t_cljs$core$async54101 !== 'undefined'){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async54101 = (function (f,blockable,meta54102){
this.f = f;
this.blockable = blockable;
this.meta54102 = meta54102;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async54101.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_54103,meta54102__$1){
var self__ = this;
var _54103__$1 = this;
return (new cljs.core.async.t_cljs$core$async54101(self__.f,self__.blockable,meta54102__$1));
});

cljs.core.async.t_cljs$core$async54101.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_54103){
var self__ = this;
var _54103__$1 = this;
return self__.meta54102;
});

cljs.core.async.t_cljs$core$async54101.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async54101.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
});

cljs.core.async.t_cljs$core$async54101.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.blockable;
});

cljs.core.async.t_cljs$core$async54101.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return self__.f;
});

cljs.core.async.t_cljs$core$async54101.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"blockable","blockable",-28395259,null),new cljs.core.Symbol(null,"meta54102","meta54102",-807142362,null)], null);
});

cljs.core.async.t_cljs$core$async54101.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async54101.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async54101";

cljs.core.async.t_cljs$core$async54101.cljs$lang$ctorPrWriter = (function (this__30846__auto__,writer__30847__auto__,opt__30848__auto__){
return cljs.core._write.call(null,writer__30847__auto__,"cljs.core.async/t_cljs$core$async54101");
});

cljs.core.async.__GT_t_cljs$core$async54101 = (function cljs$core$async$__GT_t_cljs$core$async54101(f__$1,blockable__$1,meta54102){
return (new cljs.core.async.t_cljs$core$async54101(f__$1,blockable__$1,meta54102));
});

}

return (new cljs.core.async.t_cljs$core$async54101(f,blockable,cljs.core.PersistentArrayMap.EMPTY));
});

cljs.core.async.fn_handler.cljs$lang$maxFixedArity = 2;

/**
 * Returns a fixed buffer of size n. When full, puts will block/park.
 */
cljs.core.async.buffer = (function cljs$core$async$buffer(n){
return cljs.core.async.impl.buffers.fixed_buffer.call(null,n);
});
/**
 * Returns a buffer of size n. When full, puts will complete but
 *   val will be dropped (no transfer).
 */
cljs.core.async.dropping_buffer = (function cljs$core$async$dropping_buffer(n){
return cljs.core.async.impl.buffers.dropping_buffer.call(null,n);
});
/**
 * Returns a buffer of size n. When full, puts will complete, and be
 *   buffered, but oldest elements in buffer will be dropped (not
 *   transferred).
 */
cljs.core.async.sliding_buffer = (function cljs$core$async$sliding_buffer(n){
return cljs.core.async.impl.buffers.sliding_buffer.call(null,n);
});
/**
 * Returns true if a channel created with buff will never block. That is to say,
 * puts into this buffer will never cause the buffer to be full. 
 */
cljs.core.async.unblocking_buffer_QMARK_ = (function cljs$core$async$unblocking_buffer_QMARK_(buff){
if(!((buff == null))){
if((false) || ((cljs.core.PROTOCOL_SENTINEL === buff.cljs$core$async$impl$protocols$UnblockingBuffer$))){
return true;
} else {
if((!buff.cljs$lang$protocol_mask$partition$)){
return cljs.core.native_satisfies_QMARK_.call(null,cljs.core.async.impl.protocols.UnblockingBuffer,buff);
} else {
return false;
}
}
} else {
return cljs.core.native_satisfies_QMARK_.call(null,cljs.core.async.impl.protocols.UnblockingBuffer,buff);
}
});
/**
 * Creates a channel with an optional buffer, an optional transducer (like (map f),
 *   (filter p) etc or a composition thereof), and an optional exception handler.
 *   If buf-or-n is a number, will create and use a fixed buffer of that size. If a
 *   transducer is supplied a buffer must be specified. ex-handler must be a
 *   fn of one argument - if an exception occurs during transformation it will be called
 *   with the thrown value as an argument, and any non-nil return value will be placed
 *   in the channel.
 */
cljs.core.async.chan = (function cljs$core$async$chan(var_args){
var G__54107 = arguments.length;
switch (G__54107) {
case 0:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.chan.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs.core.async.chan.call(null,null);
});

cljs.core.async.chan.cljs$core$IFn$_invoke$arity$1 = (function (buf_or_n){
return cljs.core.async.chan.call(null,buf_or_n,null,null);
});

cljs.core.async.chan.cljs$core$IFn$_invoke$arity$2 = (function (buf_or_n,xform){
return cljs.core.async.chan.call(null,buf_or_n,xform,null);
});

cljs.core.async.chan.cljs$core$IFn$_invoke$arity$3 = (function (buf_or_n,xform,ex_handler){
var buf_or_n__$1 = ((cljs.core._EQ_.call(null,buf_or_n,(0)))?null:buf_or_n);
if(cljs.core.truth_(xform)){
if(cljs.core.truth_(buf_or_n__$1)){
} else {
throw (new Error(["Assert failed: ","buffer must be supplied when transducer is","\n","buf-or-n"].join('')));
}
} else {
}

return cljs.core.async.impl.channels.chan.call(null,((typeof buf_or_n__$1 === 'number')?cljs.core.async.buffer.call(null,buf_or_n__$1):buf_or_n__$1),xform,ex_handler);
});

cljs.core.async.chan.cljs$lang$maxFixedArity = 3;

/**
 * Creates a promise channel with an optional transducer, and an optional
 *   exception-handler. A promise channel can take exactly one value that consumers
 *   will receive. Once full, puts complete but val is dropped (no transfer).
 *   Consumers will block until either a value is placed in the channel or the
 *   channel is closed. See chan for the semantics of xform and ex-handler.
 */
cljs.core.async.promise_chan = (function cljs$core$async$promise_chan(var_args){
var G__54110 = arguments.length;
switch (G__54110) {
case 0:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$0();

break;
case 1:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$0 = (function (){
return cljs.core.async.promise_chan.call(null,null);
});

cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$1 = (function (xform){
return cljs.core.async.promise_chan.call(null,xform,null);
});

cljs.core.async.promise_chan.cljs$core$IFn$_invoke$arity$2 = (function (xform,ex_handler){
return cljs.core.async.chan.call(null,cljs.core.async.impl.buffers.promise_buffer.call(null),xform,ex_handler);
});

cljs.core.async.promise_chan.cljs$lang$maxFixedArity = 2;

/**
 * Returns a channel that will close after msecs
 */
cljs.core.async.timeout = (function cljs$core$async$timeout(msecs){
return cljs.core.async.impl.timers.timeout.call(null,msecs);
});
/**
 * takes a val from port. Must be called inside a (go ...) block. Will
 *   return nil if closed. Will park if nothing is available.
 *   Returns true unless port is already closed
 */
cljs.core.async._LT__BANG_ = (function cljs$core$async$_LT__BANG_(port){
throw (new Error("<! used not in (go ...) block"));
});
/**
 * Asynchronously takes a val from port, passing to fn1. Will pass nil
 * if closed. If on-caller? (default true) is true, and value is
 * immediately available, will call fn1 on calling thread.
 * Returns nil.
 */
cljs.core.async.take_BANG_ = (function cljs$core$async$take_BANG_(var_args){
var G__54113 = arguments.length;
switch (G__54113) {
case 2:
return cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$2 = (function (port,fn1){
return cljs.core.async.take_BANG_.call(null,port,fn1,true);
});

cljs.core.async.take_BANG_.cljs$core$IFn$_invoke$arity$3 = (function (port,fn1,on_caller_QMARK_){
var ret = cljs.core.async.impl.protocols.take_BANG_.call(null,port,cljs.core.async.fn_handler.call(null,fn1));
if(cljs.core.truth_(ret)){
var val_54115 = cljs.core.deref.call(null,ret);
if(cljs.core.truth_(on_caller_QMARK_)){
fn1.call(null,val_54115);
} else {
cljs.core.async.impl.dispatch.run.call(null,((function (val_54115,ret){
return (function (){
return fn1.call(null,val_54115);
});})(val_54115,ret))
);
}
} else {
}

return null;
});

cljs.core.async.take_BANG_.cljs$lang$maxFixedArity = 3;

cljs.core.async.nop = (function cljs$core$async$nop(_){
return null;
});
cljs.core.async.fhnop = cljs.core.async.fn_handler.call(null,cljs.core.async.nop);
/**
 * puts a val into port. nil values are not allowed. Must be called
 *   inside a (go ...) block. Will park if no buffer space is available.
 *   Returns true unless port is already closed.
 */
cljs.core.async._GT__BANG_ = (function cljs$core$async$_GT__BANG_(port,val){
throw (new Error(">! used not in (go ...) block"));
});
/**
 * Asynchronously puts a val into port, calling fn0 (if supplied) when
 * complete. nil values are not allowed. Will throw if closed. If
 * on-caller? (default true) is true, and the put is immediately
 * accepted, will call fn0 on calling thread.  Returns nil.
 */
cljs.core.async.put_BANG_ = (function cljs$core$async$put_BANG_(var_args){
var G__54117 = arguments.length;
switch (G__54117) {
case 2:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$2 = (function (port,val){
var temp__5288__auto__ = cljs.core.async.impl.protocols.put_BANG_.call(null,port,val,cljs.core.async.fhnop);
if(cljs.core.truth_(temp__5288__auto__)){
var ret = temp__5288__auto__;
return cljs.core.deref.call(null,ret);
} else {
return true;
}
});

cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$3 = (function (port,val,fn1){
return cljs.core.async.put_BANG_.call(null,port,val,fn1,true);
});

cljs.core.async.put_BANG_.cljs$core$IFn$_invoke$arity$4 = (function (port,val,fn1,on_caller_QMARK_){
var temp__5288__auto__ = cljs.core.async.impl.protocols.put_BANG_.call(null,port,val,cljs.core.async.fn_handler.call(null,fn1));
if(cljs.core.truth_(temp__5288__auto__)){
var retb = temp__5288__auto__;
var ret = cljs.core.deref.call(null,retb);
if(cljs.core.truth_(on_caller_QMARK_)){
fn1.call(null,ret);
} else {
cljs.core.async.impl.dispatch.run.call(null,((function (ret,retb,temp__5288__auto__){
return (function (){
return fn1.call(null,ret);
});})(ret,retb,temp__5288__auto__))
);
}

return ret;
} else {
return true;
}
});

cljs.core.async.put_BANG_.cljs$lang$maxFixedArity = 4;

cljs.core.async.close_BANG_ = (function cljs$core$async$close_BANG_(port){
return cljs.core.async.impl.protocols.close_BANG_.call(null,port);
});
cljs.core.async.random_array = (function cljs$core$async$random_array(n){
var a = (new Array(n));
var n__31218__auto___54119 = n;
var x_54120 = (0);
while(true){
if((x_54120 < n__31218__auto___54119)){
(a[x_54120] = (0));

var G__54121 = (x_54120 + (1));
x_54120 = G__54121;
continue;
} else {
}
break;
}

var i = (1);
while(true){
if(cljs.core._EQ_.call(null,i,n)){
return a;
} else {
var j = cljs.core.rand_int.call(null,i);
(a[i] = (a[j]));

(a[j] = i);

var G__54122 = (i + (1));
i = G__54122;
continue;
}
break;
}
});
cljs.core.async.alt_flag = (function cljs$core$async$alt_flag(){
var flag = cljs.core.atom.call(null,true);
if(typeof cljs.core.async.t_cljs$core$async54123 !== 'undefined'){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async54123 = (function (flag,meta54124){
this.flag = flag;
this.meta54124 = meta54124;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async54123.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (flag){
return (function (_54125,meta54124__$1){
var self__ = this;
var _54125__$1 = this;
return (new cljs.core.async.t_cljs$core$async54123(self__.flag,meta54124__$1));
});})(flag))
;

cljs.core.async.t_cljs$core$async54123.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (flag){
return (function (_54125){
var self__ = this;
var _54125__$1 = this;
return self__.meta54124;
});})(flag))
;

cljs.core.async.t_cljs$core$async54123.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async54123.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = ((function (flag){
return (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.deref.call(null,self__.flag);
});})(flag))
;

cljs.core.async.t_cljs$core$async54123.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = ((function (flag){
return (function (_){
var self__ = this;
var ___$1 = this;
return true;
});})(flag))
;

cljs.core.async.t_cljs$core$async54123.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = ((function (flag){
return (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_.call(null,self__.flag,null);

return true;
});})(flag))
;

cljs.core.async.t_cljs$core$async54123.getBasis = ((function (flag){
return (function (){
return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"flag","flag",-1565787888,null),new cljs.core.Symbol(null,"meta54124","meta54124",126973004,null)], null);
});})(flag))
;

cljs.core.async.t_cljs$core$async54123.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async54123.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async54123";

cljs.core.async.t_cljs$core$async54123.cljs$lang$ctorPrWriter = ((function (flag){
return (function (this__30846__auto__,writer__30847__auto__,opt__30848__auto__){
return cljs.core._write.call(null,writer__30847__auto__,"cljs.core.async/t_cljs$core$async54123");
});})(flag))
;

cljs.core.async.__GT_t_cljs$core$async54123 = ((function (flag){
return (function cljs$core$async$alt_flag_$___GT_t_cljs$core$async54123(flag__$1,meta54124){
return (new cljs.core.async.t_cljs$core$async54123(flag__$1,meta54124));
});})(flag))
;

}

return (new cljs.core.async.t_cljs$core$async54123(flag,cljs.core.PersistentArrayMap.EMPTY));
});
cljs.core.async.alt_handler = (function cljs$core$async$alt_handler(flag,cb){
if(typeof cljs.core.async.t_cljs$core$async54126 !== 'undefined'){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async54126 = (function (flag,cb,meta54127){
this.flag = flag;
this.cb = cb;
this.meta54127 = meta54127;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async54126.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_54128,meta54127__$1){
var self__ = this;
var _54128__$1 = this;
return (new cljs.core.async.t_cljs$core$async54126(self__.flag,self__.cb,meta54127__$1));
});

cljs.core.async.t_cljs$core$async54126.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_54128){
var self__ = this;
var _54128__$1 = this;
return self__.meta54127;
});

cljs.core.async.t_cljs$core$async54126.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async54126.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.active_QMARK_.call(null,self__.flag);
});

cljs.core.async.t_cljs$core$async54126.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return true;
});

cljs.core.async.t_cljs$core$async54126.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.async.impl.protocols.commit.call(null,self__.flag);

return self__.cb;
});

cljs.core.async.t_cljs$core$async54126.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"flag","flag",-1565787888,null),new cljs.core.Symbol(null,"cb","cb",-2064487928,null),new cljs.core.Symbol(null,"meta54127","meta54127",1443187870,null)], null);
});

cljs.core.async.t_cljs$core$async54126.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async54126.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async54126";

cljs.core.async.t_cljs$core$async54126.cljs$lang$ctorPrWriter = (function (this__30846__auto__,writer__30847__auto__,opt__30848__auto__){
return cljs.core._write.call(null,writer__30847__auto__,"cljs.core.async/t_cljs$core$async54126");
});

cljs.core.async.__GT_t_cljs$core$async54126 = (function cljs$core$async$alt_handler_$___GT_t_cljs$core$async54126(flag__$1,cb__$1,meta54127){
return (new cljs.core.async.t_cljs$core$async54126(flag__$1,cb__$1,meta54127));
});

}

return (new cljs.core.async.t_cljs$core$async54126(flag,cb,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * returns derefable [val port] if immediate, nil if enqueued
 */
cljs.core.async.do_alts = (function cljs$core$async$do_alts(fret,ports,opts){
var flag = cljs.core.async.alt_flag.call(null);
var n = cljs.core.count.call(null,ports);
var idxs = cljs.core.async.random_array.call(null,n);
var priority = new cljs.core.Keyword(null,"priority","priority",1431093715).cljs$core$IFn$_invoke$arity$1(opts);
var ret = (function (){var i = (0);
while(true){
if((i < n)){
var idx = (cljs.core.truth_(priority)?i:(idxs[i]));
var port = cljs.core.nth.call(null,ports,idx);
var wport = ((cljs.core.vector_QMARK_.call(null,port))?port.call(null,(0)):null);
var vbox = (cljs.core.truth_(wport)?(function (){var val = port.call(null,(1));
return cljs.core.async.impl.protocols.put_BANG_.call(null,wport,val,cljs.core.async.alt_handler.call(null,flag,((function (i,val,idx,port,wport,flag,n,idxs,priority){
return (function (p1__54129_SHARP_){
return fret.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__54129_SHARP_,wport], null));
});})(i,val,idx,port,wport,flag,n,idxs,priority))
));
})():cljs.core.async.impl.protocols.take_BANG_.call(null,port,cljs.core.async.alt_handler.call(null,flag,((function (i,idx,port,wport,flag,n,idxs,priority){
return (function (p1__54130_SHARP_){
return fret.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [p1__54130_SHARP_,port], null));
});})(i,idx,port,wport,flag,n,idxs,priority))
)));
if(cljs.core.truth_(vbox)){
return cljs.core.async.impl.channels.box.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.deref.call(null,vbox),(function (){var or__30175__auto__ = wport;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return port;
}
})()], null));
} else {
var G__54131 = (i + (1));
i = G__54131;
continue;
}
} else {
return null;
}
break;
}
})();
var or__30175__auto__ = ret;
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
if(cljs.core.contains_QMARK_.call(null,opts,new cljs.core.Keyword(null,"default","default",-1987822328))){
var temp__5290__auto__ = (function (){var and__30163__auto__ = cljs.core.async.impl.protocols.active_QMARK_.call(null,flag);
if(cljs.core.truth_(and__30163__auto__)){
return cljs.core.async.impl.protocols.commit.call(null,flag);
} else {
return and__30163__auto__;
}
})();
if(cljs.core.truth_(temp__5290__auto__)){
var got = temp__5290__auto__;
return cljs.core.async.impl.channels.box.call(null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"default","default",-1987822328).cljs$core$IFn$_invoke$arity$1(opts),new cljs.core.Keyword(null,"default","default",-1987822328)], null));
} else {
return null;
}
} else {
return null;
}
}
});
/**
 * Completes at most one of several channel operations. Must be called
 * inside a (go ...) block. ports is a vector of channel endpoints,
 * which can be either a channel to take from or a vector of
 *   [channel-to-put-to val-to-put], in any combination. Takes will be
 *   made as if by <!, and puts will be made as if by >!. Unless
 *   the :priority option is true, if more than one port operation is
 *   ready a non-deterministic choice will be made. If no operation is
 *   ready and a :default value is supplied, [default-val :default] will
 *   be returned, otherwise alts! will park until the first operation to
 *   become ready completes. Returns [val port] of the completed
 *   operation, where val is the value taken for takes, and a
 *   boolean (true unless already closed, as per put!) for puts.
 * 
 *   opts are passed as :key val ... Supported options:
 * 
 *   :default val - the value to use if none of the operations are immediately ready
 *   :priority true - (default nil) when true, the operations will be tried in order.
 * 
 *   Note: there is no guarantee that the port exps or val exprs will be
 *   used, nor in what order should they be, so they should not be
 *   depended upon for side effects.
 */
cljs.core.async.alts_BANG_ = (function cljs$core$async$alts_BANG_(var_args){
var args__31459__auto__ = [];
var len__31452__auto___54137 = arguments.length;
var i__31453__auto___54138 = (0);
while(true){
if((i__31453__auto___54138 < len__31452__auto___54137)){
args__31459__auto__.push((arguments[i__31453__auto___54138]));

var G__54139 = (i__31453__auto___54138 + (1));
i__31453__auto___54138 = G__54139;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((1) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((1)),(0),null)):null);
return cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),argseq__31460__auto__);
});

cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (ports,p__54134){
var map__54135 = p__54134;
var map__54135__$1 = ((((!((map__54135 == null)))?((((map__54135.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__54135.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__54135):map__54135);
var opts = map__54135__$1;
throw (new Error("alts! used not in (go ...) block"));
});

cljs.core.async.alts_BANG_.cljs$lang$maxFixedArity = (1);

cljs.core.async.alts_BANG_.cljs$lang$applyTo = (function (seq54132){
var G__54133 = cljs.core.first.call(null,seq54132);
var seq54132__$1 = cljs.core.next.call(null,seq54132);
return cljs.core.async.alts_BANG_.cljs$core$IFn$_invoke$arity$variadic(G__54133,seq54132__$1);
});

/**
 * Puts a val into port if it's possible to do so immediately.
 *   nil values are not allowed. Never blocks. Returns true if offer succeeds.
 */
cljs.core.async.offer_BANG_ = (function cljs$core$async$offer_BANG_(port,val){
var ret = cljs.core.async.impl.protocols.put_BANG_.call(null,port,val,cljs.core.async.fn_handler.call(null,cljs.core.async.nop,false));
if(cljs.core.truth_(ret)){
return cljs.core.deref.call(null,ret);
} else {
return null;
}
});
/**
 * Takes a val from port if it's possible to do so immediately.
 *   Never blocks. Returns value if successful, nil otherwise.
 */
cljs.core.async.poll_BANG_ = (function cljs$core$async$poll_BANG_(port){
var ret = cljs.core.async.impl.protocols.take_BANG_.call(null,port,cljs.core.async.fn_handler.call(null,cljs.core.async.nop,false));
if(cljs.core.truth_(ret)){
return cljs.core.deref.call(null,ret);
} else {
return null;
}
});
/**
 * Takes elements from the from channel and supplies them to the to
 * channel. By default, the to channel will be closed when the from
 * channel closes, but can be determined by the close?  parameter. Will
 * stop consuming the from channel if the to channel closes
 */
cljs.core.async.pipe = (function cljs$core$async$pipe(var_args){
var G__54141 = arguments.length;
switch (G__54141) {
case 2:
return cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$2 = (function (from,to){
return cljs.core.async.pipe.call(null,from,to,true);
});

cljs.core.async.pipe.cljs$core$IFn$_invoke$arity$3 = (function (from,to,close_QMARK_){
var c__54040__auto___54187 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___54187){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___54187){
return (function (state_54165){
var state_val_54166 = (state_54165[(1)]);
if((state_val_54166 === (7))){
var inst_54161 = (state_54165[(2)]);
var state_54165__$1 = state_54165;
var statearr_54167_54188 = state_54165__$1;
(statearr_54167_54188[(2)] = inst_54161);

(statearr_54167_54188[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54166 === (1))){
var state_54165__$1 = state_54165;
var statearr_54168_54189 = state_54165__$1;
(statearr_54168_54189[(2)] = null);

(statearr_54168_54189[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54166 === (4))){
var inst_54144 = (state_54165[(7)]);
var inst_54144__$1 = (state_54165[(2)]);
var inst_54145 = (inst_54144__$1 == null);
var state_54165__$1 = (function (){var statearr_54169 = state_54165;
(statearr_54169[(7)] = inst_54144__$1);

return statearr_54169;
})();
if(cljs.core.truth_(inst_54145)){
var statearr_54170_54190 = state_54165__$1;
(statearr_54170_54190[(1)] = (5));

} else {
var statearr_54171_54191 = state_54165__$1;
(statearr_54171_54191[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54166 === (13))){
var state_54165__$1 = state_54165;
var statearr_54172_54192 = state_54165__$1;
(statearr_54172_54192[(2)] = null);

(statearr_54172_54192[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54166 === (6))){
var inst_54144 = (state_54165[(7)]);
var state_54165__$1 = state_54165;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_54165__$1,(11),to,inst_54144);
} else {
if((state_val_54166 === (3))){
var inst_54163 = (state_54165[(2)]);
var state_54165__$1 = state_54165;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_54165__$1,inst_54163);
} else {
if((state_val_54166 === (12))){
var state_54165__$1 = state_54165;
var statearr_54173_54193 = state_54165__$1;
(statearr_54173_54193[(2)] = null);

(statearr_54173_54193[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54166 === (2))){
var state_54165__$1 = state_54165;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_54165__$1,(4),from);
} else {
if((state_val_54166 === (11))){
var inst_54154 = (state_54165[(2)]);
var state_54165__$1 = state_54165;
if(cljs.core.truth_(inst_54154)){
var statearr_54174_54194 = state_54165__$1;
(statearr_54174_54194[(1)] = (12));

} else {
var statearr_54175_54195 = state_54165__$1;
(statearr_54175_54195[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54166 === (9))){
var state_54165__$1 = state_54165;
var statearr_54176_54196 = state_54165__$1;
(statearr_54176_54196[(2)] = null);

(statearr_54176_54196[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54166 === (5))){
var state_54165__$1 = state_54165;
if(cljs.core.truth_(close_QMARK_)){
var statearr_54177_54197 = state_54165__$1;
(statearr_54177_54197[(1)] = (8));

} else {
var statearr_54178_54198 = state_54165__$1;
(statearr_54178_54198[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54166 === (14))){
var inst_54159 = (state_54165[(2)]);
var state_54165__$1 = state_54165;
var statearr_54179_54199 = state_54165__$1;
(statearr_54179_54199[(2)] = inst_54159);

(statearr_54179_54199[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54166 === (10))){
var inst_54151 = (state_54165[(2)]);
var state_54165__$1 = state_54165;
var statearr_54180_54200 = state_54165__$1;
(statearr_54180_54200[(2)] = inst_54151);

(statearr_54180_54200[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54166 === (8))){
var inst_54148 = cljs.core.async.close_BANG_.call(null,to);
var state_54165__$1 = state_54165;
var statearr_54181_54201 = state_54165__$1;
(statearr_54181_54201[(2)] = inst_54148);

(statearr_54181_54201[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
}
}
}
}
}
});})(c__54040__auto___54187))
;
return ((function (switch__53950__auto__,c__54040__auto___54187){
return (function() {
var cljs$core$async$state_machine__53951__auto__ = null;
var cljs$core$async$state_machine__53951__auto____0 = (function (){
var statearr_54182 = [null,null,null,null,null,null,null,null];
(statearr_54182[(0)] = cljs$core$async$state_machine__53951__auto__);

(statearr_54182[(1)] = (1));

return statearr_54182;
});
var cljs$core$async$state_machine__53951__auto____1 = (function (state_54165){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_54165);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e54183){if((e54183 instanceof Object)){
var ex__53954__auto__ = e54183;
var statearr_54184_54202 = state_54165;
(statearr_54184_54202[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_54165);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e54183;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__54203 = state_54165;
state_54165 = G__54203;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$state_machine__53951__auto__ = function(state_54165){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__53951__auto____1.call(this,state_54165);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__53951__auto____0;
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__53951__auto____1;
return cljs$core$async$state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___54187))
})();
var state__54042__auto__ = (function (){var statearr_54185 = f__54041__auto__.call(null);
(statearr_54185[(6)] = c__54040__auto___54187);

return statearr_54185;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___54187))
);


return to;
});

cljs.core.async.pipe.cljs$lang$maxFixedArity = 3;

cljs.core.async.pipeline_STAR_ = (function cljs$core$async$pipeline_STAR_(n,to,xf,from,close_QMARK_,ex_handler,type){
if((n > (0))){
} else {
throw (new Error("Assert failed: (pos? n)"));
}

var jobs = cljs.core.async.chan.call(null,n);
var results = cljs.core.async.chan.call(null,n);
var process = ((function (jobs,results){
return (function (p__54204){
var vec__54205 = p__54204;
var v = cljs.core.nth.call(null,vec__54205,(0),null);
var p = cljs.core.nth.call(null,vec__54205,(1),null);
var job = vec__54205;
if((job == null)){
cljs.core.async.close_BANG_.call(null,results);

return null;
} else {
var res = cljs.core.async.chan.call(null,(1),xf,ex_handler);
var c__54040__auto___54376 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___54376,res,vec__54205,v,p,job,jobs,results){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___54376,res,vec__54205,v,p,job,jobs,results){
return (function (state_54212){
var state_val_54213 = (state_54212[(1)]);
if((state_val_54213 === (1))){
var state_54212__$1 = state_54212;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_54212__$1,(2),res,v);
} else {
if((state_val_54213 === (2))){
var inst_54209 = (state_54212[(2)]);
var inst_54210 = cljs.core.async.close_BANG_.call(null,res);
var state_54212__$1 = (function (){var statearr_54214 = state_54212;
(statearr_54214[(7)] = inst_54209);

return statearr_54214;
})();
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_54212__$1,inst_54210);
} else {
return null;
}
}
});})(c__54040__auto___54376,res,vec__54205,v,p,job,jobs,results))
;
return ((function (switch__53950__auto__,c__54040__auto___54376,res,vec__54205,v,p,job,jobs,results){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0 = (function (){
var statearr_54215 = [null,null,null,null,null,null,null,null];
(statearr_54215[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__);

(statearr_54215[(1)] = (1));

return statearr_54215;
});
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1 = (function (state_54212){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_54212);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e54216){if((e54216 instanceof Object)){
var ex__53954__auto__ = e54216;
var statearr_54217_54377 = state_54212;
(statearr_54217_54377[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_54212);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e54216;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__54378 = state_54212;
state_54212 = G__54378;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__ = function(state_54212){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1.call(this,state_54212);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___54376,res,vec__54205,v,p,job,jobs,results))
})();
var state__54042__auto__ = (function (){var statearr_54218 = f__54041__auto__.call(null);
(statearr_54218[(6)] = c__54040__auto___54376);

return statearr_54218;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___54376,res,vec__54205,v,p,job,jobs,results))
);


cljs.core.async.put_BANG_.call(null,p,res);

return true;
}
});})(jobs,results))
;
var async = ((function (jobs,results,process){
return (function (p__54219){
var vec__54220 = p__54219;
var v = cljs.core.nth.call(null,vec__54220,(0),null);
var p = cljs.core.nth.call(null,vec__54220,(1),null);
var job = vec__54220;
if((job == null)){
cljs.core.async.close_BANG_.call(null,results);

return null;
} else {
var res = cljs.core.async.chan.call(null,(1));
xf.call(null,v,res);

cljs.core.async.put_BANG_.call(null,p,res);

return true;
}
});})(jobs,results,process))
;
var n__31218__auto___54379 = n;
var __54380 = (0);
while(true){
if((__54380 < n__31218__auto___54379)){
var G__54223_54381 = type;
var G__54223_54382__$1 = (((G__54223_54381 instanceof cljs.core.Keyword))?G__54223_54381.fqn:null);
switch (G__54223_54382__$1) {
case "compute":
var c__54040__auto___54384 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (__54380,c__54040__auto___54384,G__54223_54381,G__54223_54382__$1,n__31218__auto___54379,jobs,results,process,async){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (__54380,c__54040__auto___54384,G__54223_54381,G__54223_54382__$1,n__31218__auto___54379,jobs,results,process,async){
return (function (state_54236){
var state_val_54237 = (state_54236[(1)]);
if((state_val_54237 === (1))){
var state_54236__$1 = state_54236;
var statearr_54238_54385 = state_54236__$1;
(statearr_54238_54385[(2)] = null);

(statearr_54238_54385[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54237 === (2))){
var state_54236__$1 = state_54236;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_54236__$1,(4),jobs);
} else {
if((state_val_54237 === (3))){
var inst_54234 = (state_54236[(2)]);
var state_54236__$1 = state_54236;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_54236__$1,inst_54234);
} else {
if((state_val_54237 === (4))){
var inst_54226 = (state_54236[(2)]);
var inst_54227 = process.call(null,inst_54226);
var state_54236__$1 = state_54236;
if(cljs.core.truth_(inst_54227)){
var statearr_54239_54386 = state_54236__$1;
(statearr_54239_54386[(1)] = (5));

} else {
var statearr_54240_54387 = state_54236__$1;
(statearr_54240_54387[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54237 === (5))){
var state_54236__$1 = state_54236;
var statearr_54241_54388 = state_54236__$1;
(statearr_54241_54388[(2)] = null);

(statearr_54241_54388[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54237 === (6))){
var state_54236__$1 = state_54236;
var statearr_54242_54389 = state_54236__$1;
(statearr_54242_54389[(2)] = null);

(statearr_54242_54389[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54237 === (7))){
var inst_54232 = (state_54236[(2)]);
var state_54236__$1 = state_54236;
var statearr_54243_54390 = state_54236__$1;
(statearr_54243_54390[(2)] = inst_54232);

(statearr_54243_54390[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
});})(__54380,c__54040__auto___54384,G__54223_54381,G__54223_54382__$1,n__31218__auto___54379,jobs,results,process,async))
;
return ((function (__54380,switch__53950__auto__,c__54040__auto___54384,G__54223_54381,G__54223_54382__$1,n__31218__auto___54379,jobs,results,process,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0 = (function (){
var statearr_54244 = [null,null,null,null,null,null,null];
(statearr_54244[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__);

(statearr_54244[(1)] = (1));

return statearr_54244;
});
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1 = (function (state_54236){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_54236);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e54245){if((e54245 instanceof Object)){
var ex__53954__auto__ = e54245;
var statearr_54246_54391 = state_54236;
(statearr_54246_54391[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_54236);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e54245;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__54392 = state_54236;
state_54236 = G__54392;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__ = function(state_54236){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1.call(this,state_54236);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__;
})()
;})(__54380,switch__53950__auto__,c__54040__auto___54384,G__54223_54381,G__54223_54382__$1,n__31218__auto___54379,jobs,results,process,async))
})();
var state__54042__auto__ = (function (){var statearr_54247 = f__54041__auto__.call(null);
(statearr_54247[(6)] = c__54040__auto___54384);

return statearr_54247;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(__54380,c__54040__auto___54384,G__54223_54381,G__54223_54382__$1,n__31218__auto___54379,jobs,results,process,async))
);


break;
case "async":
var c__54040__auto___54393 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (__54380,c__54040__auto___54393,G__54223_54381,G__54223_54382__$1,n__31218__auto___54379,jobs,results,process,async){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (__54380,c__54040__auto___54393,G__54223_54381,G__54223_54382__$1,n__31218__auto___54379,jobs,results,process,async){
return (function (state_54260){
var state_val_54261 = (state_54260[(1)]);
if((state_val_54261 === (1))){
var state_54260__$1 = state_54260;
var statearr_54262_54394 = state_54260__$1;
(statearr_54262_54394[(2)] = null);

(statearr_54262_54394[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54261 === (2))){
var state_54260__$1 = state_54260;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_54260__$1,(4),jobs);
} else {
if((state_val_54261 === (3))){
var inst_54258 = (state_54260[(2)]);
var state_54260__$1 = state_54260;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_54260__$1,inst_54258);
} else {
if((state_val_54261 === (4))){
var inst_54250 = (state_54260[(2)]);
var inst_54251 = async.call(null,inst_54250);
var state_54260__$1 = state_54260;
if(cljs.core.truth_(inst_54251)){
var statearr_54263_54395 = state_54260__$1;
(statearr_54263_54395[(1)] = (5));

} else {
var statearr_54264_54396 = state_54260__$1;
(statearr_54264_54396[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54261 === (5))){
var state_54260__$1 = state_54260;
var statearr_54265_54397 = state_54260__$1;
(statearr_54265_54397[(2)] = null);

(statearr_54265_54397[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54261 === (6))){
var state_54260__$1 = state_54260;
var statearr_54266_54398 = state_54260__$1;
(statearr_54266_54398[(2)] = null);

(statearr_54266_54398[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54261 === (7))){
var inst_54256 = (state_54260[(2)]);
var state_54260__$1 = state_54260;
var statearr_54267_54399 = state_54260__$1;
(statearr_54267_54399[(2)] = inst_54256);

(statearr_54267_54399[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
}
}
}
}
}
}
});})(__54380,c__54040__auto___54393,G__54223_54381,G__54223_54382__$1,n__31218__auto___54379,jobs,results,process,async))
;
return ((function (__54380,switch__53950__auto__,c__54040__auto___54393,G__54223_54381,G__54223_54382__$1,n__31218__auto___54379,jobs,results,process,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0 = (function (){
var statearr_54268 = [null,null,null,null,null,null,null];
(statearr_54268[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__);

(statearr_54268[(1)] = (1));

return statearr_54268;
});
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1 = (function (state_54260){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_54260);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e54269){if((e54269 instanceof Object)){
var ex__53954__auto__ = e54269;
var statearr_54270_54400 = state_54260;
(statearr_54270_54400[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_54260);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e54269;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__54401 = state_54260;
state_54260 = G__54401;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__ = function(state_54260){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1.call(this,state_54260);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__;
})()
;})(__54380,switch__53950__auto__,c__54040__auto___54393,G__54223_54381,G__54223_54382__$1,n__31218__auto___54379,jobs,results,process,async))
})();
var state__54042__auto__ = (function (){var statearr_54271 = f__54041__auto__.call(null);
(statearr_54271[(6)] = c__54040__auto___54393);

return statearr_54271;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(__54380,c__54040__auto___54393,G__54223_54381,G__54223_54382__$1,n__31218__auto___54379,jobs,results,process,async))
);


break;
default:
throw (new Error(["No matching clause: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(G__54223_54382__$1)].join('')));

}

var G__54402 = (__54380 + (1));
__54380 = G__54402;
continue;
} else {
}
break;
}

var c__54040__auto___54403 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___54403,jobs,results,process,async){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___54403,jobs,results,process,async){
return (function (state_54293){
var state_val_54294 = (state_54293[(1)]);
if((state_val_54294 === (1))){
var state_54293__$1 = state_54293;
var statearr_54295_54404 = state_54293__$1;
(statearr_54295_54404[(2)] = null);

(statearr_54295_54404[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54294 === (2))){
var state_54293__$1 = state_54293;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_54293__$1,(4),from);
} else {
if((state_val_54294 === (3))){
var inst_54291 = (state_54293[(2)]);
var state_54293__$1 = state_54293;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_54293__$1,inst_54291);
} else {
if((state_val_54294 === (4))){
var inst_54274 = (state_54293[(7)]);
var inst_54274__$1 = (state_54293[(2)]);
var inst_54275 = (inst_54274__$1 == null);
var state_54293__$1 = (function (){var statearr_54296 = state_54293;
(statearr_54296[(7)] = inst_54274__$1);

return statearr_54296;
})();
if(cljs.core.truth_(inst_54275)){
var statearr_54297_54405 = state_54293__$1;
(statearr_54297_54405[(1)] = (5));

} else {
var statearr_54298_54406 = state_54293__$1;
(statearr_54298_54406[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54294 === (5))){
var inst_54277 = cljs.core.async.close_BANG_.call(null,jobs);
var state_54293__$1 = state_54293;
var statearr_54299_54407 = state_54293__$1;
(statearr_54299_54407[(2)] = inst_54277);

(statearr_54299_54407[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54294 === (6))){
var inst_54279 = (state_54293[(8)]);
var inst_54274 = (state_54293[(7)]);
var inst_54279__$1 = cljs.core.async.chan.call(null,(1));
var inst_54280 = cljs.core.PersistentVector.EMPTY_NODE;
var inst_54281 = [inst_54274,inst_54279__$1];
var inst_54282 = (new cljs.core.PersistentVector(null,2,(5),inst_54280,inst_54281,null));
var state_54293__$1 = (function (){var statearr_54300 = state_54293;
(statearr_54300[(8)] = inst_54279__$1);

return statearr_54300;
})();
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_54293__$1,(8),jobs,inst_54282);
} else {
if((state_val_54294 === (7))){
var inst_54289 = (state_54293[(2)]);
var state_54293__$1 = state_54293;
var statearr_54301_54408 = state_54293__$1;
(statearr_54301_54408[(2)] = inst_54289);

(statearr_54301_54408[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54294 === (8))){
var inst_54279 = (state_54293[(8)]);
var inst_54284 = (state_54293[(2)]);
var state_54293__$1 = (function (){var statearr_54302 = state_54293;
(statearr_54302[(9)] = inst_54284);

return statearr_54302;
})();
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_54293__$1,(9),results,inst_54279);
} else {
if((state_val_54294 === (9))){
var inst_54286 = (state_54293[(2)]);
var state_54293__$1 = (function (){var statearr_54303 = state_54293;
(statearr_54303[(10)] = inst_54286);

return statearr_54303;
})();
var statearr_54304_54409 = state_54293__$1;
(statearr_54304_54409[(2)] = null);

(statearr_54304_54409[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
});})(c__54040__auto___54403,jobs,results,process,async))
;
return ((function (switch__53950__auto__,c__54040__auto___54403,jobs,results,process,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0 = (function (){
var statearr_54305 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_54305[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__);

(statearr_54305[(1)] = (1));

return statearr_54305;
});
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1 = (function (state_54293){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_54293);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e54306){if((e54306 instanceof Object)){
var ex__53954__auto__ = e54306;
var statearr_54307_54410 = state_54293;
(statearr_54307_54410[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_54293);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e54306;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__54411 = state_54293;
state_54293 = G__54411;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__ = function(state_54293){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1.call(this,state_54293);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___54403,jobs,results,process,async))
})();
var state__54042__auto__ = (function (){var statearr_54308 = f__54041__auto__.call(null);
(statearr_54308[(6)] = c__54040__auto___54403);

return statearr_54308;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___54403,jobs,results,process,async))
);


var c__54040__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto__,jobs,results,process,async){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto__,jobs,results,process,async){
return (function (state_54346){
var state_val_54347 = (state_54346[(1)]);
if((state_val_54347 === (7))){
var inst_54342 = (state_54346[(2)]);
var state_54346__$1 = state_54346;
var statearr_54348_54412 = state_54346__$1;
(statearr_54348_54412[(2)] = inst_54342);

(statearr_54348_54412[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (20))){
var state_54346__$1 = state_54346;
var statearr_54349_54413 = state_54346__$1;
(statearr_54349_54413[(2)] = null);

(statearr_54349_54413[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (1))){
var state_54346__$1 = state_54346;
var statearr_54350_54414 = state_54346__$1;
(statearr_54350_54414[(2)] = null);

(statearr_54350_54414[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (4))){
var inst_54311 = (state_54346[(7)]);
var inst_54311__$1 = (state_54346[(2)]);
var inst_54312 = (inst_54311__$1 == null);
var state_54346__$1 = (function (){var statearr_54351 = state_54346;
(statearr_54351[(7)] = inst_54311__$1);

return statearr_54351;
})();
if(cljs.core.truth_(inst_54312)){
var statearr_54352_54415 = state_54346__$1;
(statearr_54352_54415[(1)] = (5));

} else {
var statearr_54353_54416 = state_54346__$1;
(statearr_54353_54416[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (15))){
var inst_54324 = (state_54346[(8)]);
var state_54346__$1 = state_54346;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_54346__$1,(18),to,inst_54324);
} else {
if((state_val_54347 === (21))){
var inst_54337 = (state_54346[(2)]);
var state_54346__$1 = state_54346;
var statearr_54354_54417 = state_54346__$1;
(statearr_54354_54417[(2)] = inst_54337);

(statearr_54354_54417[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (13))){
var inst_54339 = (state_54346[(2)]);
var state_54346__$1 = (function (){var statearr_54355 = state_54346;
(statearr_54355[(9)] = inst_54339);

return statearr_54355;
})();
var statearr_54356_54418 = state_54346__$1;
(statearr_54356_54418[(2)] = null);

(statearr_54356_54418[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (6))){
var inst_54311 = (state_54346[(7)]);
var state_54346__$1 = state_54346;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_54346__$1,(11),inst_54311);
} else {
if((state_val_54347 === (17))){
var inst_54332 = (state_54346[(2)]);
var state_54346__$1 = state_54346;
if(cljs.core.truth_(inst_54332)){
var statearr_54357_54419 = state_54346__$1;
(statearr_54357_54419[(1)] = (19));

} else {
var statearr_54358_54420 = state_54346__$1;
(statearr_54358_54420[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (3))){
var inst_54344 = (state_54346[(2)]);
var state_54346__$1 = state_54346;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_54346__$1,inst_54344);
} else {
if((state_val_54347 === (12))){
var inst_54321 = (state_54346[(10)]);
var state_54346__$1 = state_54346;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_54346__$1,(14),inst_54321);
} else {
if((state_val_54347 === (2))){
var state_54346__$1 = state_54346;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_54346__$1,(4),results);
} else {
if((state_val_54347 === (19))){
var state_54346__$1 = state_54346;
var statearr_54359_54421 = state_54346__$1;
(statearr_54359_54421[(2)] = null);

(statearr_54359_54421[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (11))){
var inst_54321 = (state_54346[(2)]);
var state_54346__$1 = (function (){var statearr_54360 = state_54346;
(statearr_54360[(10)] = inst_54321);

return statearr_54360;
})();
var statearr_54361_54422 = state_54346__$1;
(statearr_54361_54422[(2)] = null);

(statearr_54361_54422[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (9))){
var state_54346__$1 = state_54346;
var statearr_54362_54423 = state_54346__$1;
(statearr_54362_54423[(2)] = null);

(statearr_54362_54423[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (5))){
var state_54346__$1 = state_54346;
if(cljs.core.truth_(close_QMARK_)){
var statearr_54363_54424 = state_54346__$1;
(statearr_54363_54424[(1)] = (8));

} else {
var statearr_54364_54425 = state_54346__$1;
(statearr_54364_54425[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (14))){
var inst_54326 = (state_54346[(11)]);
var inst_54324 = (state_54346[(8)]);
var inst_54324__$1 = (state_54346[(2)]);
var inst_54325 = (inst_54324__$1 == null);
var inst_54326__$1 = cljs.core.not.call(null,inst_54325);
var state_54346__$1 = (function (){var statearr_54365 = state_54346;
(statearr_54365[(11)] = inst_54326__$1);

(statearr_54365[(8)] = inst_54324__$1);

return statearr_54365;
})();
if(inst_54326__$1){
var statearr_54366_54426 = state_54346__$1;
(statearr_54366_54426[(1)] = (15));

} else {
var statearr_54367_54427 = state_54346__$1;
(statearr_54367_54427[(1)] = (16));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (16))){
var inst_54326 = (state_54346[(11)]);
var state_54346__$1 = state_54346;
var statearr_54368_54428 = state_54346__$1;
(statearr_54368_54428[(2)] = inst_54326);

(statearr_54368_54428[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (10))){
var inst_54318 = (state_54346[(2)]);
var state_54346__$1 = state_54346;
var statearr_54369_54429 = state_54346__$1;
(statearr_54369_54429[(2)] = inst_54318);

(statearr_54369_54429[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (18))){
var inst_54329 = (state_54346[(2)]);
var state_54346__$1 = state_54346;
var statearr_54370_54430 = state_54346__$1;
(statearr_54370_54430[(2)] = inst_54329);

(statearr_54370_54430[(1)] = (17));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54347 === (8))){
var inst_54315 = cljs.core.async.close_BANG_.call(null,to);
var state_54346__$1 = state_54346;
var statearr_54371_54431 = state_54346__$1;
(statearr_54371_54431[(2)] = inst_54315);

(statearr_54371_54431[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__54040__auto__,jobs,results,process,async))
;
return ((function (switch__53950__auto__,c__54040__auto__,jobs,results,process,async){
return (function() {
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__ = null;
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0 = (function (){
var statearr_54372 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_54372[(0)] = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__);

(statearr_54372[(1)] = (1));

return statearr_54372;
});
var cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1 = (function (state_54346){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_54346);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e54373){if((e54373 instanceof Object)){
var ex__53954__auto__ = e54373;
var statearr_54374_54432 = state_54346;
(statearr_54374_54432[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_54346);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e54373;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__54433 = state_54346;
state_54346 = G__54433;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__ = function(state_54346){
switch(arguments.length){
case 0:
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1.call(this,state_54346);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____0;
cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$pipeline_STAR__$_state_machine__53951__auto____1;
return cljs$core$async$pipeline_STAR__$_state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto__,jobs,results,process,async))
})();
var state__54042__auto__ = (function (){var statearr_54375 = f__54041__auto__.call(null);
(statearr_54375[(6)] = c__54040__auto__);

return statearr_54375;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto__,jobs,results,process,async))
);

return c__54040__auto__;
});
/**
 * Takes elements from the from channel and supplies them to the to
 *   channel, subject to the async function af, with parallelism n. af
 *   must be a function of two arguments, the first an input value and
 *   the second a channel on which to place the result(s). af must close!
 *   the channel before returning.  The presumption is that af will
 *   return immediately, having launched some asynchronous operation
 *   whose completion/callback will manipulate the result channel. Outputs
 *   will be returned in order relative to  the inputs. By default, the to
 *   channel will be closed when the from channel closes, but can be
 *   determined by the close?  parameter. Will stop consuming the from
 *   channel if the to channel closes.
 */
cljs.core.async.pipeline_async = (function cljs$core$async$pipeline_async(var_args){
var G__54435 = arguments.length;
switch (G__54435) {
case 4:
return cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 5:
return cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$5((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$4 = (function (n,to,af,from){
return cljs.core.async.pipeline_async.call(null,n,to,af,from,true);
});

cljs.core.async.pipeline_async.cljs$core$IFn$_invoke$arity$5 = (function (n,to,af,from,close_QMARK_){
return cljs.core.async.pipeline_STAR_.call(null,n,to,af,from,close_QMARK_,null,new cljs.core.Keyword(null,"async","async",1050769601));
});

cljs.core.async.pipeline_async.cljs$lang$maxFixedArity = 5;

/**
 * Takes elements from the from channel and supplies them to the to
 *   channel, subject to the transducer xf, with parallelism n. Because
 *   it is parallel, the transducer will be applied independently to each
 *   element, not across elements, and may produce zero or more outputs
 *   per input.  Outputs will be returned in order relative to the
 *   inputs. By default, the to channel will be closed when the from
 *   channel closes, but can be determined by the close?  parameter. Will
 *   stop consuming the from channel if the to channel closes.
 * 
 *   Note this is supplied for API compatibility with the Clojure version.
 *   Values of N > 1 will not result in actual concurrency in a
 *   single-threaded runtime.
 */
cljs.core.async.pipeline = (function cljs$core$async$pipeline(var_args){
var G__54438 = arguments.length;
switch (G__54438) {
case 4:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
case 5:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$5((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]));

break;
case 6:
return cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$6((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]),(arguments[(4)]),(arguments[(5)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$4 = (function (n,to,xf,from){
return cljs.core.async.pipeline.call(null,n,to,xf,from,true);
});

cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$5 = (function (n,to,xf,from,close_QMARK_){
return cljs.core.async.pipeline.call(null,n,to,xf,from,close_QMARK_,null);
});

cljs.core.async.pipeline.cljs$core$IFn$_invoke$arity$6 = (function (n,to,xf,from,close_QMARK_,ex_handler){
return cljs.core.async.pipeline_STAR_.call(null,n,to,xf,from,close_QMARK_,ex_handler,new cljs.core.Keyword(null,"compute","compute",1555393130));
});

cljs.core.async.pipeline.cljs$lang$maxFixedArity = 6;

/**
 * Takes a predicate and a source channel and returns a vector of two
 *   channels, the first of which will contain the values for which the
 *   predicate returned true, the second those for which it returned
 *   false.
 * 
 *   The out channels will be unbuffered by default, or two buf-or-ns can
 *   be supplied. The channels will close after the source channel has
 *   closed.
 */
cljs.core.async.split = (function cljs$core$async$split(var_args){
var G__54441 = arguments.length;
switch (G__54441) {
case 2:
return cljs.core.async.split.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 4:
return cljs.core.async.split.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.split.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.split.call(null,p,ch,null,null);
});

cljs.core.async.split.cljs$core$IFn$_invoke$arity$4 = (function (p,ch,t_buf_or_n,f_buf_or_n){
var tc = cljs.core.async.chan.call(null,t_buf_or_n);
var fc = cljs.core.async.chan.call(null,f_buf_or_n);
var c__54040__auto___54490 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___54490,tc,fc){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___54490,tc,fc){
return (function (state_54467){
var state_val_54468 = (state_54467[(1)]);
if((state_val_54468 === (7))){
var inst_54463 = (state_54467[(2)]);
var state_54467__$1 = state_54467;
var statearr_54469_54491 = state_54467__$1;
(statearr_54469_54491[(2)] = inst_54463);

(statearr_54469_54491[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54468 === (1))){
var state_54467__$1 = state_54467;
var statearr_54470_54492 = state_54467__$1;
(statearr_54470_54492[(2)] = null);

(statearr_54470_54492[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54468 === (4))){
var inst_54444 = (state_54467[(7)]);
var inst_54444__$1 = (state_54467[(2)]);
var inst_54445 = (inst_54444__$1 == null);
var state_54467__$1 = (function (){var statearr_54471 = state_54467;
(statearr_54471[(7)] = inst_54444__$1);

return statearr_54471;
})();
if(cljs.core.truth_(inst_54445)){
var statearr_54472_54493 = state_54467__$1;
(statearr_54472_54493[(1)] = (5));

} else {
var statearr_54473_54494 = state_54467__$1;
(statearr_54473_54494[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54468 === (13))){
var state_54467__$1 = state_54467;
var statearr_54474_54495 = state_54467__$1;
(statearr_54474_54495[(2)] = null);

(statearr_54474_54495[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54468 === (6))){
var inst_54444 = (state_54467[(7)]);
var inst_54450 = p.call(null,inst_54444);
var state_54467__$1 = state_54467;
if(cljs.core.truth_(inst_54450)){
var statearr_54475_54496 = state_54467__$1;
(statearr_54475_54496[(1)] = (9));

} else {
var statearr_54476_54497 = state_54467__$1;
(statearr_54476_54497[(1)] = (10));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54468 === (3))){
var inst_54465 = (state_54467[(2)]);
var state_54467__$1 = state_54467;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_54467__$1,inst_54465);
} else {
if((state_val_54468 === (12))){
var state_54467__$1 = state_54467;
var statearr_54477_54498 = state_54467__$1;
(statearr_54477_54498[(2)] = null);

(statearr_54477_54498[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54468 === (2))){
var state_54467__$1 = state_54467;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_54467__$1,(4),ch);
} else {
if((state_val_54468 === (11))){
var inst_54444 = (state_54467[(7)]);
var inst_54454 = (state_54467[(2)]);
var state_54467__$1 = state_54467;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_54467__$1,(8),inst_54454,inst_54444);
} else {
if((state_val_54468 === (9))){
var state_54467__$1 = state_54467;
var statearr_54478_54499 = state_54467__$1;
(statearr_54478_54499[(2)] = tc);

(statearr_54478_54499[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54468 === (5))){
var inst_54447 = cljs.core.async.close_BANG_.call(null,tc);
var inst_54448 = cljs.core.async.close_BANG_.call(null,fc);
var state_54467__$1 = (function (){var statearr_54479 = state_54467;
(statearr_54479[(8)] = inst_54447);

return statearr_54479;
})();
var statearr_54480_54500 = state_54467__$1;
(statearr_54480_54500[(2)] = inst_54448);

(statearr_54480_54500[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54468 === (14))){
var inst_54461 = (state_54467[(2)]);
var state_54467__$1 = state_54467;
var statearr_54481_54501 = state_54467__$1;
(statearr_54481_54501[(2)] = inst_54461);

(statearr_54481_54501[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54468 === (10))){
var state_54467__$1 = state_54467;
var statearr_54482_54502 = state_54467__$1;
(statearr_54482_54502[(2)] = fc);

(statearr_54482_54502[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54468 === (8))){
var inst_54456 = (state_54467[(2)]);
var state_54467__$1 = state_54467;
if(cljs.core.truth_(inst_54456)){
var statearr_54483_54503 = state_54467__$1;
(statearr_54483_54503[(1)] = (12));

} else {
var statearr_54484_54504 = state_54467__$1;
(statearr_54484_54504[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
}
}
}
}
}
});})(c__54040__auto___54490,tc,fc))
;
return ((function (switch__53950__auto__,c__54040__auto___54490,tc,fc){
return (function() {
var cljs$core$async$state_machine__53951__auto__ = null;
var cljs$core$async$state_machine__53951__auto____0 = (function (){
var statearr_54485 = [null,null,null,null,null,null,null,null,null];
(statearr_54485[(0)] = cljs$core$async$state_machine__53951__auto__);

(statearr_54485[(1)] = (1));

return statearr_54485;
});
var cljs$core$async$state_machine__53951__auto____1 = (function (state_54467){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_54467);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e54486){if((e54486 instanceof Object)){
var ex__53954__auto__ = e54486;
var statearr_54487_54505 = state_54467;
(statearr_54487_54505[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_54467);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e54486;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__54506 = state_54467;
state_54467 = G__54506;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$state_machine__53951__auto__ = function(state_54467){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__53951__auto____1.call(this,state_54467);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__53951__auto____0;
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__53951__auto____1;
return cljs$core$async$state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___54490,tc,fc))
})();
var state__54042__auto__ = (function (){var statearr_54488 = f__54041__auto__.call(null);
(statearr_54488[(6)] = c__54040__auto___54490);

return statearr_54488;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___54490,tc,fc))
);


return new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [tc,fc], null);
});

cljs.core.async.split.cljs$lang$maxFixedArity = 4;

/**
 * f should be a function of 2 arguments. Returns a channel containing
 *   the single result of applying f to init and the first item from the
 *   channel, then applying f to that result and the 2nd item, etc. If
 *   the channel closes without yielding items, returns init and f is not
 *   called. ch must close before reduce produces a result.
 */
cljs.core.async.reduce = (function cljs$core$async$reduce(f,init,ch){
var c__54040__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto__){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto__){
return (function (state_54527){
var state_val_54528 = (state_54527[(1)]);
if((state_val_54528 === (7))){
var inst_54523 = (state_54527[(2)]);
var state_54527__$1 = state_54527;
var statearr_54529_54547 = state_54527__$1;
(statearr_54529_54547[(2)] = inst_54523);

(statearr_54529_54547[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54528 === (1))){
var inst_54507 = init;
var state_54527__$1 = (function (){var statearr_54530 = state_54527;
(statearr_54530[(7)] = inst_54507);

return statearr_54530;
})();
var statearr_54531_54548 = state_54527__$1;
(statearr_54531_54548[(2)] = null);

(statearr_54531_54548[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54528 === (4))){
var inst_54510 = (state_54527[(8)]);
var inst_54510__$1 = (state_54527[(2)]);
var inst_54511 = (inst_54510__$1 == null);
var state_54527__$1 = (function (){var statearr_54532 = state_54527;
(statearr_54532[(8)] = inst_54510__$1);

return statearr_54532;
})();
if(cljs.core.truth_(inst_54511)){
var statearr_54533_54549 = state_54527__$1;
(statearr_54533_54549[(1)] = (5));

} else {
var statearr_54534_54550 = state_54527__$1;
(statearr_54534_54550[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54528 === (6))){
var inst_54507 = (state_54527[(7)]);
var inst_54514 = (state_54527[(9)]);
var inst_54510 = (state_54527[(8)]);
var inst_54514__$1 = f.call(null,inst_54507,inst_54510);
var inst_54515 = cljs.core.reduced_QMARK_.call(null,inst_54514__$1);
var state_54527__$1 = (function (){var statearr_54535 = state_54527;
(statearr_54535[(9)] = inst_54514__$1);

return statearr_54535;
})();
if(inst_54515){
var statearr_54536_54551 = state_54527__$1;
(statearr_54536_54551[(1)] = (8));

} else {
var statearr_54537_54552 = state_54527__$1;
(statearr_54537_54552[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54528 === (3))){
var inst_54525 = (state_54527[(2)]);
var state_54527__$1 = state_54527;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_54527__$1,inst_54525);
} else {
if((state_val_54528 === (2))){
var state_54527__$1 = state_54527;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_54527__$1,(4),ch);
} else {
if((state_val_54528 === (9))){
var inst_54514 = (state_54527[(9)]);
var inst_54507 = inst_54514;
var state_54527__$1 = (function (){var statearr_54538 = state_54527;
(statearr_54538[(7)] = inst_54507);

return statearr_54538;
})();
var statearr_54539_54553 = state_54527__$1;
(statearr_54539_54553[(2)] = null);

(statearr_54539_54553[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54528 === (5))){
var inst_54507 = (state_54527[(7)]);
var state_54527__$1 = state_54527;
var statearr_54540_54554 = state_54527__$1;
(statearr_54540_54554[(2)] = inst_54507);

(statearr_54540_54554[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54528 === (10))){
var inst_54521 = (state_54527[(2)]);
var state_54527__$1 = state_54527;
var statearr_54541_54555 = state_54527__$1;
(statearr_54541_54555[(2)] = inst_54521);

(statearr_54541_54555[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54528 === (8))){
var inst_54514 = (state_54527[(9)]);
var inst_54517 = cljs.core.deref.call(null,inst_54514);
var state_54527__$1 = state_54527;
var statearr_54542_54556 = state_54527__$1;
(statearr_54542_54556[(2)] = inst_54517);

(statearr_54542_54556[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
}
});})(c__54040__auto__))
;
return ((function (switch__53950__auto__,c__54040__auto__){
return (function() {
var cljs$core$async$reduce_$_state_machine__53951__auto__ = null;
var cljs$core$async$reduce_$_state_machine__53951__auto____0 = (function (){
var statearr_54543 = [null,null,null,null,null,null,null,null,null,null];
(statearr_54543[(0)] = cljs$core$async$reduce_$_state_machine__53951__auto__);

(statearr_54543[(1)] = (1));

return statearr_54543;
});
var cljs$core$async$reduce_$_state_machine__53951__auto____1 = (function (state_54527){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_54527);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e54544){if((e54544 instanceof Object)){
var ex__53954__auto__ = e54544;
var statearr_54545_54557 = state_54527;
(statearr_54545_54557[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_54527);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e54544;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__54558 = state_54527;
state_54527 = G__54558;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$reduce_$_state_machine__53951__auto__ = function(state_54527){
switch(arguments.length){
case 0:
return cljs$core$async$reduce_$_state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$reduce_$_state_machine__53951__auto____1.call(this,state_54527);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$reduce_$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$reduce_$_state_machine__53951__auto____0;
cljs$core$async$reduce_$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$reduce_$_state_machine__53951__auto____1;
return cljs$core$async$reduce_$_state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto__))
})();
var state__54042__auto__ = (function (){var statearr_54546 = f__54041__auto__.call(null);
(statearr_54546[(6)] = c__54040__auto__);

return statearr_54546;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto__))
);

return c__54040__auto__;
});
/**
 * async/reduces a channel with a transformation (xform f).
 *   Returns a channel containing the result.  ch must close before
 *   transduce produces a result.
 */
cljs.core.async.transduce = (function cljs$core$async$transduce(xform,f,init,ch){
var f__$1 = xform.call(null,f);
var c__54040__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto__,f__$1){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto__,f__$1){
return (function (state_54564){
var state_val_54565 = (state_54564[(1)]);
if((state_val_54565 === (1))){
var inst_54559 = cljs.core.async.reduce.call(null,f__$1,init,ch);
var state_54564__$1 = state_54564;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_54564__$1,(2),inst_54559);
} else {
if((state_val_54565 === (2))){
var inst_54561 = (state_54564[(2)]);
var inst_54562 = f__$1.call(null,inst_54561);
var state_54564__$1 = state_54564;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_54564__$1,inst_54562);
} else {
return null;
}
}
});})(c__54040__auto__,f__$1))
;
return ((function (switch__53950__auto__,c__54040__auto__,f__$1){
return (function() {
var cljs$core$async$transduce_$_state_machine__53951__auto__ = null;
var cljs$core$async$transduce_$_state_machine__53951__auto____0 = (function (){
var statearr_54566 = [null,null,null,null,null,null,null];
(statearr_54566[(0)] = cljs$core$async$transduce_$_state_machine__53951__auto__);

(statearr_54566[(1)] = (1));

return statearr_54566;
});
var cljs$core$async$transduce_$_state_machine__53951__auto____1 = (function (state_54564){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_54564);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e54567){if((e54567 instanceof Object)){
var ex__53954__auto__ = e54567;
var statearr_54568_54570 = state_54564;
(statearr_54568_54570[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_54564);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e54567;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__54571 = state_54564;
state_54564 = G__54571;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$transduce_$_state_machine__53951__auto__ = function(state_54564){
switch(arguments.length){
case 0:
return cljs$core$async$transduce_$_state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$transduce_$_state_machine__53951__auto____1.call(this,state_54564);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$transduce_$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$transduce_$_state_machine__53951__auto____0;
cljs$core$async$transduce_$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$transduce_$_state_machine__53951__auto____1;
return cljs$core$async$transduce_$_state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto__,f__$1))
})();
var state__54042__auto__ = (function (){var statearr_54569 = f__54041__auto__.call(null);
(statearr_54569[(6)] = c__54040__auto__);

return statearr_54569;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto__,f__$1))
);

return c__54040__auto__;
});
/**
 * Puts the contents of coll into the supplied channel.
 * 
 *   By default the channel will be closed after the items are copied,
 *   but can be determined by the close? parameter.
 * 
 *   Returns a channel which will close after the items are copied.
 */
cljs.core.async.onto_chan = (function cljs$core$async$onto_chan(var_args){
var G__54573 = arguments.length;
switch (G__54573) {
case 2:
return cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$2 = (function (ch,coll){
return cljs.core.async.onto_chan.call(null,ch,coll,true);
});

cljs.core.async.onto_chan.cljs$core$IFn$_invoke$arity$3 = (function (ch,coll,close_QMARK_){
var c__54040__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto__){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto__){
return (function (state_54598){
var state_val_54599 = (state_54598[(1)]);
if((state_val_54599 === (7))){
var inst_54580 = (state_54598[(2)]);
var state_54598__$1 = state_54598;
var statearr_54600_54621 = state_54598__$1;
(statearr_54600_54621[(2)] = inst_54580);

(statearr_54600_54621[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54599 === (1))){
var inst_54574 = cljs.core.seq.call(null,coll);
var inst_54575 = inst_54574;
var state_54598__$1 = (function (){var statearr_54601 = state_54598;
(statearr_54601[(7)] = inst_54575);

return statearr_54601;
})();
var statearr_54602_54622 = state_54598__$1;
(statearr_54602_54622[(2)] = null);

(statearr_54602_54622[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54599 === (4))){
var inst_54575 = (state_54598[(7)]);
var inst_54578 = cljs.core.first.call(null,inst_54575);
var state_54598__$1 = state_54598;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_54598__$1,(7),ch,inst_54578);
} else {
if((state_val_54599 === (13))){
var inst_54592 = (state_54598[(2)]);
var state_54598__$1 = state_54598;
var statearr_54603_54623 = state_54598__$1;
(statearr_54603_54623[(2)] = inst_54592);

(statearr_54603_54623[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54599 === (6))){
var inst_54583 = (state_54598[(2)]);
var state_54598__$1 = state_54598;
if(cljs.core.truth_(inst_54583)){
var statearr_54604_54624 = state_54598__$1;
(statearr_54604_54624[(1)] = (8));

} else {
var statearr_54605_54625 = state_54598__$1;
(statearr_54605_54625[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54599 === (3))){
var inst_54596 = (state_54598[(2)]);
var state_54598__$1 = state_54598;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_54598__$1,inst_54596);
} else {
if((state_val_54599 === (12))){
var state_54598__$1 = state_54598;
var statearr_54606_54626 = state_54598__$1;
(statearr_54606_54626[(2)] = null);

(statearr_54606_54626[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54599 === (2))){
var inst_54575 = (state_54598[(7)]);
var state_54598__$1 = state_54598;
if(cljs.core.truth_(inst_54575)){
var statearr_54607_54627 = state_54598__$1;
(statearr_54607_54627[(1)] = (4));

} else {
var statearr_54608_54628 = state_54598__$1;
(statearr_54608_54628[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54599 === (11))){
var inst_54589 = cljs.core.async.close_BANG_.call(null,ch);
var state_54598__$1 = state_54598;
var statearr_54609_54629 = state_54598__$1;
(statearr_54609_54629[(2)] = inst_54589);

(statearr_54609_54629[(1)] = (13));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54599 === (9))){
var state_54598__$1 = state_54598;
if(cljs.core.truth_(close_QMARK_)){
var statearr_54610_54630 = state_54598__$1;
(statearr_54610_54630[(1)] = (11));

} else {
var statearr_54611_54631 = state_54598__$1;
(statearr_54611_54631[(1)] = (12));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54599 === (5))){
var inst_54575 = (state_54598[(7)]);
var state_54598__$1 = state_54598;
var statearr_54612_54632 = state_54598__$1;
(statearr_54612_54632[(2)] = inst_54575);

(statearr_54612_54632[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54599 === (10))){
var inst_54594 = (state_54598[(2)]);
var state_54598__$1 = state_54598;
var statearr_54613_54633 = state_54598__$1;
(statearr_54613_54633[(2)] = inst_54594);

(statearr_54613_54633[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54599 === (8))){
var inst_54575 = (state_54598[(7)]);
var inst_54585 = cljs.core.next.call(null,inst_54575);
var inst_54575__$1 = inst_54585;
var state_54598__$1 = (function (){var statearr_54614 = state_54598;
(statearr_54614[(7)] = inst_54575__$1);

return statearr_54614;
})();
var statearr_54615_54634 = state_54598__$1;
(statearr_54615_54634[(2)] = null);

(statearr_54615_54634[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
}
}
}
}
});})(c__54040__auto__))
;
return ((function (switch__53950__auto__,c__54040__auto__){
return (function() {
var cljs$core$async$state_machine__53951__auto__ = null;
var cljs$core$async$state_machine__53951__auto____0 = (function (){
var statearr_54616 = [null,null,null,null,null,null,null,null];
(statearr_54616[(0)] = cljs$core$async$state_machine__53951__auto__);

(statearr_54616[(1)] = (1));

return statearr_54616;
});
var cljs$core$async$state_machine__53951__auto____1 = (function (state_54598){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_54598);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e54617){if((e54617 instanceof Object)){
var ex__53954__auto__ = e54617;
var statearr_54618_54635 = state_54598;
(statearr_54618_54635[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_54598);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e54617;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__54636 = state_54598;
state_54598 = G__54636;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$state_machine__53951__auto__ = function(state_54598){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__53951__auto____1.call(this,state_54598);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__53951__auto____0;
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__53951__auto____1;
return cljs$core$async$state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto__))
})();
var state__54042__auto__ = (function (){var statearr_54619 = f__54041__auto__.call(null);
(statearr_54619[(6)] = c__54040__auto__);

return statearr_54619;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto__))
);

return c__54040__auto__;
});

cljs.core.async.onto_chan.cljs$lang$maxFixedArity = 3;

/**
 * Creates and returns a channel which contains the contents of coll,
 *   closing when exhausted.
 */
cljs.core.async.to_chan = (function cljs$core$async$to_chan(coll){
var ch = cljs.core.async.chan.call(null,cljs.core.bounded_count.call(null,(100),coll));
cljs.core.async.onto_chan.call(null,ch,coll);

return ch;
});

/**
 * @interface
 */
cljs.core.async.Mux = function(){};

cljs.core.async.muxch_STAR_ = (function cljs$core$async$muxch_STAR_(_){
if((!((_ == null))) && (!((_.cljs$core$async$Mux$muxch_STAR_$arity$1 == null)))){
return _.cljs$core$async$Mux$muxch_STAR_$arity$1(_);
} else {
var x__30908__auto__ = (((_ == null))?null:_);
var m__30909__auto__ = (cljs.core.async.muxch_STAR_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,_);
} else {
var m__30909__auto____$1 = (cljs.core.async.muxch_STAR_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,_);
} else {
throw cljs.core.missing_protocol.call(null,"Mux.muxch*",_);
}
}
}
});


/**
 * @interface
 */
cljs.core.async.Mult = function(){};

cljs.core.async.tap_STAR_ = (function cljs$core$async$tap_STAR_(m,ch,close_QMARK_){
if((!((m == null))) && (!((m.cljs$core$async$Mult$tap_STAR_$arity$3 == null)))){
return m.cljs$core$async$Mult$tap_STAR_$arity$3(m,ch,close_QMARK_);
} else {
var x__30908__auto__ = (((m == null))?null:m);
var m__30909__auto__ = (cljs.core.async.tap_STAR_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,m,ch,close_QMARK_);
} else {
var m__30909__auto____$1 = (cljs.core.async.tap_STAR_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,m,ch,close_QMARK_);
} else {
throw cljs.core.missing_protocol.call(null,"Mult.tap*",m);
}
}
}
});

cljs.core.async.untap_STAR_ = (function cljs$core$async$untap_STAR_(m,ch){
if((!((m == null))) && (!((m.cljs$core$async$Mult$untap_STAR_$arity$2 == null)))){
return m.cljs$core$async$Mult$untap_STAR_$arity$2(m,ch);
} else {
var x__30908__auto__ = (((m == null))?null:m);
var m__30909__auto__ = (cljs.core.async.untap_STAR_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,m,ch);
} else {
var m__30909__auto____$1 = (cljs.core.async.untap_STAR_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,m,ch);
} else {
throw cljs.core.missing_protocol.call(null,"Mult.untap*",m);
}
}
}
});

cljs.core.async.untap_all_STAR_ = (function cljs$core$async$untap_all_STAR_(m){
if((!((m == null))) && (!((m.cljs$core$async$Mult$untap_all_STAR_$arity$1 == null)))){
return m.cljs$core$async$Mult$untap_all_STAR_$arity$1(m);
} else {
var x__30908__auto__ = (((m == null))?null:m);
var m__30909__auto__ = (cljs.core.async.untap_all_STAR_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,m);
} else {
var m__30909__auto____$1 = (cljs.core.async.untap_all_STAR_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,m);
} else {
throw cljs.core.missing_protocol.call(null,"Mult.untap-all*",m);
}
}
}
});

/**
 * Creates and returns a mult(iple) of the supplied channel. Channels
 *   containing copies of the channel can be created with 'tap', and
 *   detached with 'untap'.
 * 
 *   Each item is distributed to all taps in parallel and synchronously,
 *   i.e. each tap must accept before the next item is distributed. Use
 *   buffering/windowing to prevent slow taps from holding up the mult.
 * 
 *   Items received when there are no taps get dropped.
 * 
 *   If a tap puts to a closed channel, it will be removed from the mult.
 */
cljs.core.async.mult = (function cljs$core$async$mult(ch){
var cs = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var m = (function (){
if(typeof cljs.core.async.t_cljs$core$async54637 !== 'undefined'){
} else {

/**
* @constructor
 * @implements {cljs.core.async.Mult}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async54637 = (function (ch,cs,meta54638){
this.ch = ch;
this.cs = cs;
this.meta54638 = meta54638;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async54637.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (cs){
return (function (_54639,meta54638__$1){
var self__ = this;
var _54639__$1 = this;
return (new cljs.core.async.t_cljs$core$async54637(self__.ch,self__.cs,meta54638__$1));
});})(cs))
;

cljs.core.async.t_cljs$core$async54637.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (cs){
return (function (_54639){
var self__ = this;
var _54639__$1 = this;
return self__.meta54638;
});})(cs))
;

cljs.core.async.t_cljs$core$async54637.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async54637.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (cs){
return (function (_){
var self__ = this;
var ___$1 = this;
return self__.ch;
});})(cs))
;

cljs.core.async.t_cljs$core$async54637.prototype.cljs$core$async$Mult$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async54637.prototype.cljs$core$async$Mult$tap_STAR_$arity$3 = ((function (cs){
return (function (_,ch__$1,close_QMARK_){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.assoc,ch__$1,close_QMARK_);

return null;
});})(cs))
;

cljs.core.async.t_cljs$core$async54637.prototype.cljs$core$async$Mult$untap_STAR_$arity$2 = ((function (cs){
return (function (_,ch__$1){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.dissoc,ch__$1);

return null;
});})(cs))
;

cljs.core.async.t_cljs$core$async54637.prototype.cljs$core$async$Mult$untap_all_STAR_$arity$1 = ((function (cs){
return (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_.call(null,self__.cs,cljs.core.PersistentArrayMap.EMPTY);

return null;
});})(cs))
;

cljs.core.async.t_cljs$core$async54637.getBasis = ((function (cs){
return (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"cs","cs",-117024463,null),new cljs.core.Symbol(null,"meta54638","meta54638",1969250127,null)], null);
});})(cs))
;

cljs.core.async.t_cljs$core$async54637.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async54637.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async54637";

cljs.core.async.t_cljs$core$async54637.cljs$lang$ctorPrWriter = ((function (cs){
return (function (this__30846__auto__,writer__30847__auto__,opt__30848__auto__){
return cljs.core._write.call(null,writer__30847__auto__,"cljs.core.async/t_cljs$core$async54637");
});})(cs))
;

cljs.core.async.__GT_t_cljs$core$async54637 = ((function (cs){
return (function cljs$core$async$mult_$___GT_t_cljs$core$async54637(ch__$1,cs__$1,meta54638){
return (new cljs.core.async.t_cljs$core$async54637(ch__$1,cs__$1,meta54638));
});})(cs))
;

}

return (new cljs.core.async.t_cljs$core$async54637(ch,cs,cljs.core.PersistentArrayMap.EMPTY));
})()
;
var dchan = cljs.core.async.chan.call(null,(1));
var dctr = cljs.core.atom.call(null,null);
var done = ((function (cs,m,dchan,dctr){
return (function (_){
if((cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec) === (0))){
return cljs.core.async.put_BANG_.call(null,dchan,true);
} else {
return null;
}
});})(cs,m,dchan,dctr))
;
var c__54040__auto___54859 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___54859,cs,m,dchan,dctr,done){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___54859,cs,m,dchan,dctr,done){
return (function (state_54774){
var state_val_54775 = (state_54774[(1)]);
if((state_val_54775 === (7))){
var inst_54770 = (state_54774[(2)]);
var state_54774__$1 = state_54774;
var statearr_54776_54860 = state_54774__$1;
(statearr_54776_54860[(2)] = inst_54770);

(statearr_54776_54860[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (20))){
var inst_54673 = (state_54774[(7)]);
var inst_54685 = cljs.core.first.call(null,inst_54673);
var inst_54686 = cljs.core.nth.call(null,inst_54685,(0),null);
var inst_54687 = cljs.core.nth.call(null,inst_54685,(1),null);
var state_54774__$1 = (function (){var statearr_54777 = state_54774;
(statearr_54777[(8)] = inst_54686);

return statearr_54777;
})();
if(cljs.core.truth_(inst_54687)){
var statearr_54778_54861 = state_54774__$1;
(statearr_54778_54861[(1)] = (22));

} else {
var statearr_54779_54862 = state_54774__$1;
(statearr_54779_54862[(1)] = (23));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (27))){
var inst_54717 = (state_54774[(9)]);
var inst_54722 = (state_54774[(10)]);
var inst_54715 = (state_54774[(11)]);
var inst_54642 = (state_54774[(12)]);
var inst_54722__$1 = cljs.core._nth.call(null,inst_54715,inst_54717);
var inst_54723 = cljs.core.async.put_BANG_.call(null,inst_54722__$1,inst_54642,done);
var state_54774__$1 = (function (){var statearr_54780 = state_54774;
(statearr_54780[(10)] = inst_54722__$1);

return statearr_54780;
})();
if(cljs.core.truth_(inst_54723)){
var statearr_54781_54863 = state_54774__$1;
(statearr_54781_54863[(1)] = (30));

} else {
var statearr_54782_54864 = state_54774__$1;
(statearr_54782_54864[(1)] = (31));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (1))){
var state_54774__$1 = state_54774;
var statearr_54783_54865 = state_54774__$1;
(statearr_54783_54865[(2)] = null);

(statearr_54783_54865[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (24))){
var inst_54673 = (state_54774[(7)]);
var inst_54692 = (state_54774[(2)]);
var inst_54693 = cljs.core.next.call(null,inst_54673);
var inst_54651 = inst_54693;
var inst_54652 = null;
var inst_54653 = (0);
var inst_54654 = (0);
var state_54774__$1 = (function (){var statearr_54784 = state_54774;
(statearr_54784[(13)] = inst_54651);

(statearr_54784[(14)] = inst_54654);

(statearr_54784[(15)] = inst_54652);

(statearr_54784[(16)] = inst_54653);

(statearr_54784[(17)] = inst_54692);

return statearr_54784;
})();
var statearr_54785_54866 = state_54774__$1;
(statearr_54785_54866[(2)] = null);

(statearr_54785_54866[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (39))){
var state_54774__$1 = state_54774;
var statearr_54789_54867 = state_54774__$1;
(statearr_54789_54867[(2)] = null);

(statearr_54789_54867[(1)] = (41));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (4))){
var inst_54642 = (state_54774[(12)]);
var inst_54642__$1 = (state_54774[(2)]);
var inst_54643 = (inst_54642__$1 == null);
var state_54774__$1 = (function (){var statearr_54790 = state_54774;
(statearr_54790[(12)] = inst_54642__$1);

return statearr_54790;
})();
if(cljs.core.truth_(inst_54643)){
var statearr_54791_54868 = state_54774__$1;
(statearr_54791_54868[(1)] = (5));

} else {
var statearr_54792_54869 = state_54774__$1;
(statearr_54792_54869[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (15))){
var inst_54651 = (state_54774[(13)]);
var inst_54654 = (state_54774[(14)]);
var inst_54652 = (state_54774[(15)]);
var inst_54653 = (state_54774[(16)]);
var inst_54669 = (state_54774[(2)]);
var inst_54670 = (inst_54654 + (1));
var tmp54786 = inst_54651;
var tmp54787 = inst_54652;
var tmp54788 = inst_54653;
var inst_54651__$1 = tmp54786;
var inst_54652__$1 = tmp54787;
var inst_54653__$1 = tmp54788;
var inst_54654__$1 = inst_54670;
var state_54774__$1 = (function (){var statearr_54793 = state_54774;
(statearr_54793[(13)] = inst_54651__$1);

(statearr_54793[(14)] = inst_54654__$1);

(statearr_54793[(18)] = inst_54669);

(statearr_54793[(15)] = inst_54652__$1);

(statearr_54793[(16)] = inst_54653__$1);

return statearr_54793;
})();
var statearr_54794_54870 = state_54774__$1;
(statearr_54794_54870[(2)] = null);

(statearr_54794_54870[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (21))){
var inst_54696 = (state_54774[(2)]);
var state_54774__$1 = state_54774;
var statearr_54798_54871 = state_54774__$1;
(statearr_54798_54871[(2)] = inst_54696);

(statearr_54798_54871[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (31))){
var inst_54722 = (state_54774[(10)]);
var inst_54726 = done.call(null,null);
var inst_54727 = cljs.core.async.untap_STAR_.call(null,m,inst_54722);
var state_54774__$1 = (function (){var statearr_54799 = state_54774;
(statearr_54799[(19)] = inst_54726);

return statearr_54799;
})();
var statearr_54800_54872 = state_54774__$1;
(statearr_54800_54872[(2)] = inst_54727);

(statearr_54800_54872[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (32))){
var inst_54716 = (state_54774[(20)]);
var inst_54717 = (state_54774[(9)]);
var inst_54714 = (state_54774[(21)]);
var inst_54715 = (state_54774[(11)]);
var inst_54729 = (state_54774[(2)]);
var inst_54730 = (inst_54717 + (1));
var tmp54795 = inst_54716;
var tmp54796 = inst_54714;
var tmp54797 = inst_54715;
var inst_54714__$1 = tmp54796;
var inst_54715__$1 = tmp54797;
var inst_54716__$1 = tmp54795;
var inst_54717__$1 = inst_54730;
var state_54774__$1 = (function (){var statearr_54801 = state_54774;
(statearr_54801[(20)] = inst_54716__$1);

(statearr_54801[(9)] = inst_54717__$1);

(statearr_54801[(22)] = inst_54729);

(statearr_54801[(21)] = inst_54714__$1);

(statearr_54801[(11)] = inst_54715__$1);

return statearr_54801;
})();
var statearr_54802_54873 = state_54774__$1;
(statearr_54802_54873[(2)] = null);

(statearr_54802_54873[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (40))){
var inst_54742 = (state_54774[(23)]);
var inst_54746 = done.call(null,null);
var inst_54747 = cljs.core.async.untap_STAR_.call(null,m,inst_54742);
var state_54774__$1 = (function (){var statearr_54803 = state_54774;
(statearr_54803[(24)] = inst_54746);

return statearr_54803;
})();
var statearr_54804_54874 = state_54774__$1;
(statearr_54804_54874[(2)] = inst_54747);

(statearr_54804_54874[(1)] = (41));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (33))){
var inst_54733 = (state_54774[(25)]);
var inst_54735 = cljs.core.chunked_seq_QMARK_.call(null,inst_54733);
var state_54774__$1 = state_54774;
if(inst_54735){
var statearr_54805_54875 = state_54774__$1;
(statearr_54805_54875[(1)] = (36));

} else {
var statearr_54806_54876 = state_54774__$1;
(statearr_54806_54876[(1)] = (37));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (13))){
var inst_54663 = (state_54774[(26)]);
var inst_54666 = cljs.core.async.close_BANG_.call(null,inst_54663);
var state_54774__$1 = state_54774;
var statearr_54807_54877 = state_54774__$1;
(statearr_54807_54877[(2)] = inst_54666);

(statearr_54807_54877[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (22))){
var inst_54686 = (state_54774[(8)]);
var inst_54689 = cljs.core.async.close_BANG_.call(null,inst_54686);
var state_54774__$1 = state_54774;
var statearr_54808_54878 = state_54774__$1;
(statearr_54808_54878[(2)] = inst_54689);

(statearr_54808_54878[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (36))){
var inst_54733 = (state_54774[(25)]);
var inst_54737 = cljs.core.chunk_first.call(null,inst_54733);
var inst_54738 = cljs.core.chunk_rest.call(null,inst_54733);
var inst_54739 = cljs.core.count.call(null,inst_54737);
var inst_54714 = inst_54738;
var inst_54715 = inst_54737;
var inst_54716 = inst_54739;
var inst_54717 = (0);
var state_54774__$1 = (function (){var statearr_54809 = state_54774;
(statearr_54809[(20)] = inst_54716);

(statearr_54809[(9)] = inst_54717);

(statearr_54809[(21)] = inst_54714);

(statearr_54809[(11)] = inst_54715);

return statearr_54809;
})();
var statearr_54810_54879 = state_54774__$1;
(statearr_54810_54879[(2)] = null);

(statearr_54810_54879[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (41))){
var inst_54733 = (state_54774[(25)]);
var inst_54749 = (state_54774[(2)]);
var inst_54750 = cljs.core.next.call(null,inst_54733);
var inst_54714 = inst_54750;
var inst_54715 = null;
var inst_54716 = (0);
var inst_54717 = (0);
var state_54774__$1 = (function (){var statearr_54811 = state_54774;
(statearr_54811[(20)] = inst_54716);

(statearr_54811[(9)] = inst_54717);

(statearr_54811[(21)] = inst_54714);

(statearr_54811[(11)] = inst_54715);

(statearr_54811[(27)] = inst_54749);

return statearr_54811;
})();
var statearr_54812_54880 = state_54774__$1;
(statearr_54812_54880[(2)] = null);

(statearr_54812_54880[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (43))){
var state_54774__$1 = state_54774;
var statearr_54813_54881 = state_54774__$1;
(statearr_54813_54881[(2)] = null);

(statearr_54813_54881[(1)] = (44));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (29))){
var inst_54758 = (state_54774[(2)]);
var state_54774__$1 = state_54774;
var statearr_54814_54882 = state_54774__$1;
(statearr_54814_54882[(2)] = inst_54758);

(statearr_54814_54882[(1)] = (26));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (44))){
var inst_54767 = (state_54774[(2)]);
var state_54774__$1 = (function (){var statearr_54815 = state_54774;
(statearr_54815[(28)] = inst_54767);

return statearr_54815;
})();
var statearr_54816_54883 = state_54774__$1;
(statearr_54816_54883[(2)] = null);

(statearr_54816_54883[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (6))){
var inst_54706 = (state_54774[(29)]);
var inst_54705 = cljs.core.deref.call(null,cs);
var inst_54706__$1 = cljs.core.keys.call(null,inst_54705);
var inst_54707 = cljs.core.count.call(null,inst_54706__$1);
var inst_54708 = cljs.core.reset_BANG_.call(null,dctr,inst_54707);
var inst_54713 = cljs.core.seq.call(null,inst_54706__$1);
var inst_54714 = inst_54713;
var inst_54715 = null;
var inst_54716 = (0);
var inst_54717 = (0);
var state_54774__$1 = (function (){var statearr_54817 = state_54774;
(statearr_54817[(20)] = inst_54716);

(statearr_54817[(9)] = inst_54717);

(statearr_54817[(29)] = inst_54706__$1);

(statearr_54817[(21)] = inst_54714);

(statearr_54817[(11)] = inst_54715);

(statearr_54817[(30)] = inst_54708);

return statearr_54817;
})();
var statearr_54818_54884 = state_54774__$1;
(statearr_54818_54884[(2)] = null);

(statearr_54818_54884[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (28))){
var inst_54714 = (state_54774[(21)]);
var inst_54733 = (state_54774[(25)]);
var inst_54733__$1 = cljs.core.seq.call(null,inst_54714);
var state_54774__$1 = (function (){var statearr_54819 = state_54774;
(statearr_54819[(25)] = inst_54733__$1);

return statearr_54819;
})();
if(inst_54733__$1){
var statearr_54820_54885 = state_54774__$1;
(statearr_54820_54885[(1)] = (33));

} else {
var statearr_54821_54886 = state_54774__$1;
(statearr_54821_54886[(1)] = (34));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (25))){
var inst_54716 = (state_54774[(20)]);
var inst_54717 = (state_54774[(9)]);
var inst_54719 = (inst_54717 < inst_54716);
var inst_54720 = inst_54719;
var state_54774__$1 = state_54774;
if(cljs.core.truth_(inst_54720)){
var statearr_54822_54887 = state_54774__$1;
(statearr_54822_54887[(1)] = (27));

} else {
var statearr_54823_54888 = state_54774__$1;
(statearr_54823_54888[(1)] = (28));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (34))){
var state_54774__$1 = state_54774;
var statearr_54824_54889 = state_54774__$1;
(statearr_54824_54889[(2)] = null);

(statearr_54824_54889[(1)] = (35));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (17))){
var state_54774__$1 = state_54774;
var statearr_54825_54890 = state_54774__$1;
(statearr_54825_54890[(2)] = null);

(statearr_54825_54890[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (3))){
var inst_54772 = (state_54774[(2)]);
var state_54774__$1 = state_54774;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_54774__$1,inst_54772);
} else {
if((state_val_54775 === (12))){
var inst_54701 = (state_54774[(2)]);
var state_54774__$1 = state_54774;
var statearr_54826_54891 = state_54774__$1;
(statearr_54826_54891[(2)] = inst_54701);

(statearr_54826_54891[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (2))){
var state_54774__$1 = state_54774;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_54774__$1,(4),ch);
} else {
if((state_val_54775 === (23))){
var state_54774__$1 = state_54774;
var statearr_54827_54892 = state_54774__$1;
(statearr_54827_54892[(2)] = null);

(statearr_54827_54892[(1)] = (24));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (35))){
var inst_54756 = (state_54774[(2)]);
var state_54774__$1 = state_54774;
var statearr_54828_54893 = state_54774__$1;
(statearr_54828_54893[(2)] = inst_54756);

(statearr_54828_54893[(1)] = (29));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (19))){
var inst_54673 = (state_54774[(7)]);
var inst_54677 = cljs.core.chunk_first.call(null,inst_54673);
var inst_54678 = cljs.core.chunk_rest.call(null,inst_54673);
var inst_54679 = cljs.core.count.call(null,inst_54677);
var inst_54651 = inst_54678;
var inst_54652 = inst_54677;
var inst_54653 = inst_54679;
var inst_54654 = (0);
var state_54774__$1 = (function (){var statearr_54829 = state_54774;
(statearr_54829[(13)] = inst_54651);

(statearr_54829[(14)] = inst_54654);

(statearr_54829[(15)] = inst_54652);

(statearr_54829[(16)] = inst_54653);

return statearr_54829;
})();
var statearr_54830_54894 = state_54774__$1;
(statearr_54830_54894[(2)] = null);

(statearr_54830_54894[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (11))){
var inst_54651 = (state_54774[(13)]);
var inst_54673 = (state_54774[(7)]);
var inst_54673__$1 = cljs.core.seq.call(null,inst_54651);
var state_54774__$1 = (function (){var statearr_54831 = state_54774;
(statearr_54831[(7)] = inst_54673__$1);

return statearr_54831;
})();
if(inst_54673__$1){
var statearr_54832_54895 = state_54774__$1;
(statearr_54832_54895[(1)] = (16));

} else {
var statearr_54833_54896 = state_54774__$1;
(statearr_54833_54896[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (9))){
var inst_54703 = (state_54774[(2)]);
var state_54774__$1 = state_54774;
var statearr_54834_54897 = state_54774__$1;
(statearr_54834_54897[(2)] = inst_54703);

(statearr_54834_54897[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (5))){
var inst_54649 = cljs.core.deref.call(null,cs);
var inst_54650 = cljs.core.seq.call(null,inst_54649);
var inst_54651 = inst_54650;
var inst_54652 = null;
var inst_54653 = (0);
var inst_54654 = (0);
var state_54774__$1 = (function (){var statearr_54835 = state_54774;
(statearr_54835[(13)] = inst_54651);

(statearr_54835[(14)] = inst_54654);

(statearr_54835[(15)] = inst_54652);

(statearr_54835[(16)] = inst_54653);

return statearr_54835;
})();
var statearr_54836_54898 = state_54774__$1;
(statearr_54836_54898[(2)] = null);

(statearr_54836_54898[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (14))){
var state_54774__$1 = state_54774;
var statearr_54837_54899 = state_54774__$1;
(statearr_54837_54899[(2)] = null);

(statearr_54837_54899[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (45))){
var inst_54764 = (state_54774[(2)]);
var state_54774__$1 = state_54774;
var statearr_54838_54900 = state_54774__$1;
(statearr_54838_54900[(2)] = inst_54764);

(statearr_54838_54900[(1)] = (44));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (26))){
var inst_54706 = (state_54774[(29)]);
var inst_54760 = (state_54774[(2)]);
var inst_54761 = cljs.core.seq.call(null,inst_54706);
var state_54774__$1 = (function (){var statearr_54839 = state_54774;
(statearr_54839[(31)] = inst_54760);

return statearr_54839;
})();
if(inst_54761){
var statearr_54840_54901 = state_54774__$1;
(statearr_54840_54901[(1)] = (42));

} else {
var statearr_54841_54902 = state_54774__$1;
(statearr_54841_54902[(1)] = (43));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (16))){
var inst_54673 = (state_54774[(7)]);
var inst_54675 = cljs.core.chunked_seq_QMARK_.call(null,inst_54673);
var state_54774__$1 = state_54774;
if(inst_54675){
var statearr_54842_54903 = state_54774__$1;
(statearr_54842_54903[(1)] = (19));

} else {
var statearr_54843_54904 = state_54774__$1;
(statearr_54843_54904[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (38))){
var inst_54753 = (state_54774[(2)]);
var state_54774__$1 = state_54774;
var statearr_54844_54905 = state_54774__$1;
(statearr_54844_54905[(2)] = inst_54753);

(statearr_54844_54905[(1)] = (35));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (30))){
var state_54774__$1 = state_54774;
var statearr_54845_54906 = state_54774__$1;
(statearr_54845_54906[(2)] = null);

(statearr_54845_54906[(1)] = (32));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (10))){
var inst_54654 = (state_54774[(14)]);
var inst_54652 = (state_54774[(15)]);
var inst_54662 = cljs.core._nth.call(null,inst_54652,inst_54654);
var inst_54663 = cljs.core.nth.call(null,inst_54662,(0),null);
var inst_54664 = cljs.core.nth.call(null,inst_54662,(1),null);
var state_54774__$1 = (function (){var statearr_54846 = state_54774;
(statearr_54846[(26)] = inst_54663);

return statearr_54846;
})();
if(cljs.core.truth_(inst_54664)){
var statearr_54847_54907 = state_54774__$1;
(statearr_54847_54907[(1)] = (13));

} else {
var statearr_54848_54908 = state_54774__$1;
(statearr_54848_54908[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (18))){
var inst_54699 = (state_54774[(2)]);
var state_54774__$1 = state_54774;
var statearr_54849_54909 = state_54774__$1;
(statearr_54849_54909[(2)] = inst_54699);

(statearr_54849_54909[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (42))){
var state_54774__$1 = state_54774;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_54774__$1,(45),dchan);
} else {
if((state_val_54775 === (37))){
var inst_54733 = (state_54774[(25)]);
var inst_54642 = (state_54774[(12)]);
var inst_54742 = (state_54774[(23)]);
var inst_54742__$1 = cljs.core.first.call(null,inst_54733);
var inst_54743 = cljs.core.async.put_BANG_.call(null,inst_54742__$1,inst_54642,done);
var state_54774__$1 = (function (){var statearr_54850 = state_54774;
(statearr_54850[(23)] = inst_54742__$1);

return statearr_54850;
})();
if(cljs.core.truth_(inst_54743)){
var statearr_54851_54910 = state_54774__$1;
(statearr_54851_54910[(1)] = (39));

} else {
var statearr_54852_54911 = state_54774__$1;
(statearr_54852_54911[(1)] = (40));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_54775 === (8))){
var inst_54654 = (state_54774[(14)]);
var inst_54653 = (state_54774[(16)]);
var inst_54656 = (inst_54654 < inst_54653);
var inst_54657 = inst_54656;
var state_54774__$1 = state_54774;
if(cljs.core.truth_(inst_54657)){
var statearr_54853_54912 = state_54774__$1;
(statearr_54853_54912[(1)] = (10));

} else {
var statearr_54854_54913 = state_54774__$1;
(statearr_54854_54913[(1)] = (11));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__54040__auto___54859,cs,m,dchan,dctr,done))
;
return ((function (switch__53950__auto__,c__54040__auto___54859,cs,m,dchan,dctr,done){
return (function() {
var cljs$core$async$mult_$_state_machine__53951__auto__ = null;
var cljs$core$async$mult_$_state_machine__53951__auto____0 = (function (){
var statearr_54855 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_54855[(0)] = cljs$core$async$mult_$_state_machine__53951__auto__);

(statearr_54855[(1)] = (1));

return statearr_54855;
});
var cljs$core$async$mult_$_state_machine__53951__auto____1 = (function (state_54774){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_54774);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e54856){if((e54856 instanceof Object)){
var ex__53954__auto__ = e54856;
var statearr_54857_54914 = state_54774;
(statearr_54857_54914[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_54774);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e54856;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__54915 = state_54774;
state_54774 = G__54915;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$mult_$_state_machine__53951__auto__ = function(state_54774){
switch(arguments.length){
case 0:
return cljs$core$async$mult_$_state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$mult_$_state_machine__53951__auto____1.call(this,state_54774);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$mult_$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mult_$_state_machine__53951__auto____0;
cljs$core$async$mult_$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mult_$_state_machine__53951__auto____1;
return cljs$core$async$mult_$_state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___54859,cs,m,dchan,dctr,done))
})();
var state__54042__auto__ = (function (){var statearr_54858 = f__54041__auto__.call(null);
(statearr_54858[(6)] = c__54040__auto___54859);

return statearr_54858;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___54859,cs,m,dchan,dctr,done))
);


return m;
});
/**
 * Copies the mult source onto the supplied channel.
 * 
 *   By default the channel will be closed when the source closes,
 *   but can be determined by the close? parameter.
 */
cljs.core.async.tap = (function cljs$core$async$tap(var_args){
var G__54917 = arguments.length;
switch (G__54917) {
case 2:
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.tap.cljs$core$IFn$_invoke$arity$2 = (function (mult,ch){
return cljs.core.async.tap.call(null,mult,ch,true);
});

cljs.core.async.tap.cljs$core$IFn$_invoke$arity$3 = (function (mult,ch,close_QMARK_){
cljs.core.async.tap_STAR_.call(null,mult,ch,close_QMARK_);

return ch;
});

cljs.core.async.tap.cljs$lang$maxFixedArity = 3;

/**
 * Disconnects a target channel from a mult
 */
cljs.core.async.untap = (function cljs$core$async$untap(mult,ch){
return cljs.core.async.untap_STAR_.call(null,mult,ch);
});
/**
 * Disconnects all target channels from a mult
 */
cljs.core.async.untap_all = (function cljs$core$async$untap_all(mult){
return cljs.core.async.untap_all_STAR_.call(null,mult);
});

/**
 * @interface
 */
cljs.core.async.Mix = function(){};

cljs.core.async.admix_STAR_ = (function cljs$core$async$admix_STAR_(m,ch){
if((!((m == null))) && (!((m.cljs$core$async$Mix$admix_STAR_$arity$2 == null)))){
return m.cljs$core$async$Mix$admix_STAR_$arity$2(m,ch);
} else {
var x__30908__auto__ = (((m == null))?null:m);
var m__30909__auto__ = (cljs.core.async.admix_STAR_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,m,ch);
} else {
var m__30909__auto____$1 = (cljs.core.async.admix_STAR_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,m,ch);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.admix*",m);
}
}
}
});

cljs.core.async.unmix_STAR_ = (function cljs$core$async$unmix_STAR_(m,ch){
if((!((m == null))) && (!((m.cljs$core$async$Mix$unmix_STAR_$arity$2 == null)))){
return m.cljs$core$async$Mix$unmix_STAR_$arity$2(m,ch);
} else {
var x__30908__auto__ = (((m == null))?null:m);
var m__30909__auto__ = (cljs.core.async.unmix_STAR_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,m,ch);
} else {
var m__30909__auto____$1 = (cljs.core.async.unmix_STAR_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,m,ch);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.unmix*",m);
}
}
}
});

cljs.core.async.unmix_all_STAR_ = (function cljs$core$async$unmix_all_STAR_(m){
if((!((m == null))) && (!((m.cljs$core$async$Mix$unmix_all_STAR_$arity$1 == null)))){
return m.cljs$core$async$Mix$unmix_all_STAR_$arity$1(m);
} else {
var x__30908__auto__ = (((m == null))?null:m);
var m__30909__auto__ = (cljs.core.async.unmix_all_STAR_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,m);
} else {
var m__30909__auto____$1 = (cljs.core.async.unmix_all_STAR_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,m);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.unmix-all*",m);
}
}
}
});

cljs.core.async.toggle_STAR_ = (function cljs$core$async$toggle_STAR_(m,state_map){
if((!((m == null))) && (!((m.cljs$core$async$Mix$toggle_STAR_$arity$2 == null)))){
return m.cljs$core$async$Mix$toggle_STAR_$arity$2(m,state_map);
} else {
var x__30908__auto__ = (((m == null))?null:m);
var m__30909__auto__ = (cljs.core.async.toggle_STAR_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,m,state_map);
} else {
var m__30909__auto____$1 = (cljs.core.async.toggle_STAR_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,m,state_map);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.toggle*",m);
}
}
}
});

cljs.core.async.solo_mode_STAR_ = (function cljs$core$async$solo_mode_STAR_(m,mode){
if((!((m == null))) && (!((m.cljs$core$async$Mix$solo_mode_STAR_$arity$2 == null)))){
return m.cljs$core$async$Mix$solo_mode_STAR_$arity$2(m,mode);
} else {
var x__30908__auto__ = (((m == null))?null:m);
var m__30909__auto__ = (cljs.core.async.solo_mode_STAR_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,m,mode);
} else {
var m__30909__auto____$1 = (cljs.core.async.solo_mode_STAR_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,m,mode);
} else {
throw cljs.core.missing_protocol.call(null,"Mix.solo-mode*",m);
}
}
}
});

cljs.core.async.ioc_alts_BANG_ = (function cljs$core$async$ioc_alts_BANG_(var_args){
var args__31459__auto__ = [];
var len__31452__auto___54929 = arguments.length;
var i__31453__auto___54930 = (0);
while(true){
if((i__31453__auto___54930 < len__31452__auto___54929)){
args__31459__auto__.push((arguments[i__31453__auto___54930]));

var G__54931 = (i__31453__auto___54930 + (1));
i__31453__auto___54930 = G__54931;
continue;
} else {
}
break;
}

var argseq__31460__auto__ = ((((3) < args__31459__auto__.length))?(new cljs.core.IndexedSeq(args__31459__auto__.slice((3)),(0),null)):null);
return cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),argseq__31460__auto__);
});

cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic = (function (state,cont_block,ports,p__54923){
var map__54924 = p__54923;
var map__54924__$1 = ((((!((map__54924 == null)))?((((map__54924.cljs$lang$protocol_mask$partition0$ & (64))) || ((cljs.core.PROTOCOL_SENTINEL === map__54924.cljs$core$ISeq$)))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__54924):map__54924);
var opts = map__54924__$1;
var statearr_54926_54932 = state;
(statearr_54926_54932[(1)] = cont_block);


var temp__5290__auto__ = cljs.core.async.do_alts.call(null,((function (map__54924,map__54924__$1,opts){
return (function (val){
var statearr_54927_54933 = state;
(statearr_54927_54933[(2)] = val);


return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state);
});})(map__54924,map__54924__$1,opts))
,ports,opts);
if(cljs.core.truth_(temp__5290__auto__)){
var cb = temp__5290__auto__;
var statearr_54928_54934 = state;
(statearr_54928_54934[(2)] = cljs.core.deref.call(null,cb));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
return null;
}
});

cljs.core.async.ioc_alts_BANG_.cljs$lang$maxFixedArity = (3);

cljs.core.async.ioc_alts_BANG_.cljs$lang$applyTo = (function (seq54919){
var G__54920 = cljs.core.first.call(null,seq54919);
var seq54919__$1 = cljs.core.next.call(null,seq54919);
var G__54921 = cljs.core.first.call(null,seq54919__$1);
var seq54919__$2 = cljs.core.next.call(null,seq54919__$1);
var G__54922 = cljs.core.first.call(null,seq54919__$2);
var seq54919__$3 = cljs.core.next.call(null,seq54919__$2);
return cljs.core.async.ioc_alts_BANG_.cljs$core$IFn$_invoke$arity$variadic(G__54920,G__54921,G__54922,seq54919__$3);
});

/**
 * Creates and returns a mix of one or more input channels which will
 *   be put on the supplied out channel. Input sources can be added to
 *   the mix with 'admix', and removed with 'unmix'. A mix supports
 *   soloing, muting and pausing multiple inputs atomically using
 *   'toggle', and can solo using either muting or pausing as determined
 *   by 'solo-mode'.
 * 
 *   Each channel can have zero or more boolean modes set via 'toggle':
 * 
 *   :solo - when true, only this (ond other soloed) channel(s) will appear
 *        in the mix output channel. :mute and :pause states of soloed
 *        channels are ignored. If solo-mode is :mute, non-soloed
 *        channels are muted, if :pause, non-soloed channels are
 *        paused.
 * 
 *   :mute - muted channels will have their contents consumed but not included in the mix
 *   :pause - paused channels will not have their contents consumed (and thus also not included in the mix)
 */
cljs.core.async.mix = (function cljs$core$async$mix(out){
var cs = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var solo_modes = new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"pause","pause",-2095325672),null,new cljs.core.Keyword(null,"mute","mute",1151223646),null], null), null);
var attrs = cljs.core.conj.call(null,solo_modes,new cljs.core.Keyword(null,"solo","solo",-316350075));
var solo_mode = cljs.core.atom.call(null,new cljs.core.Keyword(null,"mute","mute",1151223646));
var change = cljs.core.async.chan.call(null);
var changed = ((function (cs,solo_modes,attrs,solo_mode,change){
return (function (){
return cljs.core.async.put_BANG_.call(null,change,true);
});})(cs,solo_modes,attrs,solo_mode,change))
;
var pick = ((function (cs,solo_modes,attrs,solo_mode,change,changed){
return (function (attr,chs){
return cljs.core.reduce_kv.call(null,((function (cs,solo_modes,attrs,solo_mode,change,changed){
return (function (ret,c,v){
if(cljs.core.truth_(attr.call(null,v))){
return cljs.core.conj.call(null,ret,c);
} else {
return ret;
}
});})(cs,solo_modes,attrs,solo_mode,change,changed))
,cljs.core.PersistentHashSet.EMPTY,chs);
});})(cs,solo_modes,attrs,solo_mode,change,changed))
;
var calc_state = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick){
return (function (){
var chs = cljs.core.deref.call(null,cs);
var mode = cljs.core.deref.call(null,solo_mode);
var solos = pick.call(null,new cljs.core.Keyword(null,"solo","solo",-316350075),chs);
var pauses = pick.call(null,new cljs.core.Keyword(null,"pause","pause",-2095325672),chs);
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"solos","solos",1441458643),solos,new cljs.core.Keyword(null,"mutes","mutes",1068806309),pick.call(null,new cljs.core.Keyword(null,"mute","mute",1151223646),chs),new cljs.core.Keyword(null,"reads","reads",-1215067361),cljs.core.conj.call(null,(((cljs.core._EQ_.call(null,mode,new cljs.core.Keyword(null,"pause","pause",-2095325672))) && (!(cljs.core.empty_QMARK_.call(null,solos))))?cljs.core.vec.call(null,solos):cljs.core.vec.call(null,cljs.core.remove.call(null,pauses,cljs.core.keys.call(null,chs)))),change)], null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick))
;
var m = (function (){
if(typeof cljs.core.async.t_cljs$core$async54935 !== 'undefined'){
} else {

/**
* @constructor
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mix}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async54935 = (function (out,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,meta54936){
this.out = out;
this.cs = cs;
this.solo_modes = solo_modes;
this.attrs = attrs;
this.solo_mode = solo_mode;
this.change = change;
this.changed = changed;
this.pick = pick;
this.calc_state = calc_state;
this.meta54936 = meta54936;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async54935.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_54937,meta54936__$1){
var self__ = this;
var _54937__$1 = this;
return (new cljs.core.async.t_cljs$core$async54935(self__.out,self__.cs,self__.solo_modes,self__.attrs,self__.solo_mode,self__.change,self__.changed,self__.pick,self__.calc_state,meta54936__$1));
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async54935.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_54937){
var self__ = this;
var _54937__$1 = this;
return self__.meta54936;
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async54935.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async54935.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_){
var self__ = this;
var ___$1 = this;
return self__.out;
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async54935.prototype.cljs$core$async$Mix$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async54935.prototype.cljs$core$async$Mix$admix_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,ch){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.assoc,ch,cljs.core.PersistentArrayMap.EMPTY);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async54935.prototype.cljs$core$async$Mix$unmix_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,ch){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.dissoc,ch);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async54935.prototype.cljs$core$async$Mix$unmix_all_STAR_$arity$1 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_){
var self__ = this;
var ___$1 = this;
cljs.core.reset_BANG_.call(null,self__.cs,cljs.core.PersistentArrayMap.EMPTY);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async54935.prototype.cljs$core$async$Mix$toggle_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,state_map){
var self__ = this;
var ___$1 = this;
cljs.core.swap_BANG_.call(null,self__.cs,cljs.core.partial.call(null,cljs.core.merge_with,cljs.core.merge),state_map);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async54935.prototype.cljs$core$async$Mix$solo_mode_STAR_$arity$2 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (_,mode){
var self__ = this;
var ___$1 = this;
if(cljs.core.truth_(self__.solo_modes.call(null,mode))){
} else {
throw (new Error(["Assert failed: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(["mode must be one of: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(self__.solo_modes)].join('')),"\n","(solo-modes mode)"].join('')));
}

cljs.core.reset_BANG_.call(null,self__.solo_mode,mode);

return self__.changed.call(null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async54935.getBasis = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (){
return new cljs.core.PersistentVector(null, 10, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"out","out",729986010,null),new cljs.core.Symbol(null,"cs","cs",-117024463,null),new cljs.core.Symbol(null,"solo-modes","solo-modes",882180540,null),new cljs.core.Symbol(null,"attrs","attrs",-450137186,null),new cljs.core.Symbol(null,"solo-mode","solo-mode",2031788074,null),new cljs.core.Symbol(null,"change","change",477485025,null),new cljs.core.Symbol(null,"changed","changed",-2083710852,null),new cljs.core.Symbol(null,"pick","pick",1300068175,null),new cljs.core.Symbol(null,"calc-state","calc-state",-349968968,null),new cljs.core.Symbol(null,"meta54936","meta54936",-1328881111,null)], null);
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.t_cljs$core$async54935.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async54935.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async54935";

cljs.core.async.t_cljs$core$async54935.cljs$lang$ctorPrWriter = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function (this__30846__auto__,writer__30847__auto__,opt__30848__auto__){
return cljs.core._write.call(null,writer__30847__auto__,"cljs.core.async/t_cljs$core$async54935");
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

cljs.core.async.__GT_t_cljs$core$async54935 = ((function (cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state){
return (function cljs$core$async$mix_$___GT_t_cljs$core$async54935(out__$1,cs__$1,solo_modes__$1,attrs__$1,solo_mode__$1,change__$1,changed__$1,pick__$1,calc_state__$1,meta54936){
return (new cljs.core.async.t_cljs$core$async54935(out__$1,cs__$1,solo_modes__$1,attrs__$1,solo_mode__$1,change__$1,changed__$1,pick__$1,calc_state__$1,meta54936));
});})(cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state))
;

}

return (new cljs.core.async.t_cljs$core$async54935(out,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,cljs.core.PersistentArrayMap.EMPTY));
})()
;
var c__54040__auto___55099 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___55099,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___55099,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function (state_55039){
var state_val_55040 = (state_55039[(1)]);
if((state_val_55040 === (7))){
var inst_54954 = (state_55039[(2)]);
var state_55039__$1 = state_55039;
var statearr_55041_55100 = state_55039__$1;
(statearr_55041_55100[(2)] = inst_54954);

(statearr_55041_55100[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (20))){
var inst_54966 = (state_55039[(7)]);
var state_55039__$1 = state_55039;
var statearr_55042_55101 = state_55039__$1;
(statearr_55042_55101[(2)] = inst_54966);

(statearr_55042_55101[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (27))){
var state_55039__$1 = state_55039;
var statearr_55043_55102 = state_55039__$1;
(statearr_55043_55102[(2)] = null);

(statearr_55043_55102[(1)] = (28));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (1))){
var inst_54941 = (state_55039[(8)]);
var inst_54941__$1 = calc_state.call(null);
var inst_54943 = (inst_54941__$1 == null);
var inst_54944 = cljs.core.not.call(null,inst_54943);
var state_55039__$1 = (function (){var statearr_55044 = state_55039;
(statearr_55044[(8)] = inst_54941__$1);

return statearr_55044;
})();
if(inst_54944){
var statearr_55045_55103 = state_55039__$1;
(statearr_55045_55103[(1)] = (2));

} else {
var statearr_55046_55104 = state_55039__$1;
(statearr_55046_55104[(1)] = (3));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (24))){
var inst_54999 = (state_55039[(9)]);
var inst_54990 = (state_55039[(10)]);
var inst_55013 = (state_55039[(11)]);
var inst_55013__$1 = inst_54990.call(null,inst_54999);
var state_55039__$1 = (function (){var statearr_55047 = state_55039;
(statearr_55047[(11)] = inst_55013__$1);

return statearr_55047;
})();
if(cljs.core.truth_(inst_55013__$1)){
var statearr_55048_55105 = state_55039__$1;
(statearr_55048_55105[(1)] = (29));

} else {
var statearr_55049_55106 = state_55039__$1;
(statearr_55049_55106[(1)] = (30));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (4))){
var inst_54957 = (state_55039[(2)]);
var state_55039__$1 = state_55039;
if(cljs.core.truth_(inst_54957)){
var statearr_55050_55107 = state_55039__$1;
(statearr_55050_55107[(1)] = (8));

} else {
var statearr_55051_55108 = state_55039__$1;
(statearr_55051_55108[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (15))){
var inst_54984 = (state_55039[(2)]);
var state_55039__$1 = state_55039;
if(cljs.core.truth_(inst_54984)){
var statearr_55052_55109 = state_55039__$1;
(statearr_55052_55109[(1)] = (19));

} else {
var statearr_55053_55110 = state_55039__$1;
(statearr_55053_55110[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (21))){
var inst_54989 = (state_55039[(12)]);
var inst_54989__$1 = (state_55039[(2)]);
var inst_54990 = cljs.core.get.call(null,inst_54989__$1,new cljs.core.Keyword(null,"solos","solos",1441458643));
var inst_54991 = cljs.core.get.call(null,inst_54989__$1,new cljs.core.Keyword(null,"mutes","mutes",1068806309));
var inst_54992 = cljs.core.get.call(null,inst_54989__$1,new cljs.core.Keyword(null,"reads","reads",-1215067361));
var state_55039__$1 = (function (){var statearr_55054 = state_55039;
(statearr_55054[(13)] = inst_54991);

(statearr_55054[(12)] = inst_54989__$1);

(statearr_55054[(10)] = inst_54990);

return statearr_55054;
})();
return cljs.core.async.ioc_alts_BANG_.call(null,state_55039__$1,(22),inst_54992);
} else {
if((state_val_55040 === (31))){
var inst_55021 = (state_55039[(2)]);
var state_55039__$1 = state_55039;
if(cljs.core.truth_(inst_55021)){
var statearr_55055_55111 = state_55039__$1;
(statearr_55055_55111[(1)] = (32));

} else {
var statearr_55056_55112 = state_55039__$1;
(statearr_55056_55112[(1)] = (33));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (32))){
var inst_54998 = (state_55039[(14)]);
var state_55039__$1 = state_55039;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_55039__$1,(35),out,inst_54998);
} else {
if((state_val_55040 === (33))){
var inst_54989 = (state_55039[(12)]);
var inst_54966 = inst_54989;
var state_55039__$1 = (function (){var statearr_55057 = state_55039;
(statearr_55057[(7)] = inst_54966);

return statearr_55057;
})();
var statearr_55058_55113 = state_55039__$1;
(statearr_55058_55113[(2)] = null);

(statearr_55058_55113[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (13))){
var inst_54966 = (state_55039[(7)]);
var inst_54973 = inst_54966.cljs$lang$protocol_mask$partition0$;
var inst_54974 = (inst_54973 & (64));
var inst_54975 = inst_54966.cljs$core$ISeq$;
var inst_54976 = (cljs.core.PROTOCOL_SENTINEL === inst_54975);
var inst_54977 = (inst_54974) || (inst_54976);
var state_55039__$1 = state_55039;
if(cljs.core.truth_(inst_54977)){
var statearr_55059_55114 = state_55039__$1;
(statearr_55059_55114[(1)] = (16));

} else {
var statearr_55060_55115 = state_55039__$1;
(statearr_55060_55115[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (22))){
var inst_54999 = (state_55039[(9)]);
var inst_54998 = (state_55039[(14)]);
var inst_54997 = (state_55039[(2)]);
var inst_54998__$1 = cljs.core.nth.call(null,inst_54997,(0),null);
var inst_54999__$1 = cljs.core.nth.call(null,inst_54997,(1),null);
var inst_55000 = (inst_54998__$1 == null);
var inst_55001 = cljs.core._EQ_.call(null,inst_54999__$1,change);
var inst_55002 = (inst_55000) || (inst_55001);
var state_55039__$1 = (function (){var statearr_55061 = state_55039;
(statearr_55061[(9)] = inst_54999__$1);

(statearr_55061[(14)] = inst_54998__$1);

return statearr_55061;
})();
if(cljs.core.truth_(inst_55002)){
var statearr_55062_55116 = state_55039__$1;
(statearr_55062_55116[(1)] = (23));

} else {
var statearr_55063_55117 = state_55039__$1;
(statearr_55063_55117[(1)] = (24));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (36))){
var inst_54989 = (state_55039[(12)]);
var inst_54966 = inst_54989;
var state_55039__$1 = (function (){var statearr_55064 = state_55039;
(statearr_55064[(7)] = inst_54966);

return statearr_55064;
})();
var statearr_55065_55118 = state_55039__$1;
(statearr_55065_55118[(2)] = null);

(statearr_55065_55118[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (29))){
var inst_55013 = (state_55039[(11)]);
var state_55039__$1 = state_55039;
var statearr_55066_55119 = state_55039__$1;
(statearr_55066_55119[(2)] = inst_55013);

(statearr_55066_55119[(1)] = (31));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (6))){
var state_55039__$1 = state_55039;
var statearr_55067_55120 = state_55039__$1;
(statearr_55067_55120[(2)] = false);

(statearr_55067_55120[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (28))){
var inst_55009 = (state_55039[(2)]);
var inst_55010 = calc_state.call(null);
var inst_54966 = inst_55010;
var state_55039__$1 = (function (){var statearr_55068 = state_55039;
(statearr_55068[(15)] = inst_55009);

(statearr_55068[(7)] = inst_54966);

return statearr_55068;
})();
var statearr_55069_55121 = state_55039__$1;
(statearr_55069_55121[(2)] = null);

(statearr_55069_55121[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (25))){
var inst_55035 = (state_55039[(2)]);
var state_55039__$1 = state_55039;
var statearr_55070_55122 = state_55039__$1;
(statearr_55070_55122[(2)] = inst_55035);

(statearr_55070_55122[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (34))){
var inst_55033 = (state_55039[(2)]);
var state_55039__$1 = state_55039;
var statearr_55071_55123 = state_55039__$1;
(statearr_55071_55123[(2)] = inst_55033);

(statearr_55071_55123[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (17))){
var state_55039__$1 = state_55039;
var statearr_55072_55124 = state_55039__$1;
(statearr_55072_55124[(2)] = false);

(statearr_55072_55124[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (3))){
var state_55039__$1 = state_55039;
var statearr_55073_55125 = state_55039__$1;
(statearr_55073_55125[(2)] = false);

(statearr_55073_55125[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (12))){
var inst_55037 = (state_55039[(2)]);
var state_55039__$1 = state_55039;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_55039__$1,inst_55037);
} else {
if((state_val_55040 === (2))){
var inst_54941 = (state_55039[(8)]);
var inst_54946 = inst_54941.cljs$lang$protocol_mask$partition0$;
var inst_54947 = (inst_54946 & (64));
var inst_54948 = inst_54941.cljs$core$ISeq$;
var inst_54949 = (cljs.core.PROTOCOL_SENTINEL === inst_54948);
var inst_54950 = (inst_54947) || (inst_54949);
var state_55039__$1 = state_55039;
if(cljs.core.truth_(inst_54950)){
var statearr_55074_55126 = state_55039__$1;
(statearr_55074_55126[(1)] = (5));

} else {
var statearr_55075_55127 = state_55039__$1;
(statearr_55075_55127[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (23))){
var inst_54998 = (state_55039[(14)]);
var inst_55004 = (inst_54998 == null);
var state_55039__$1 = state_55039;
if(cljs.core.truth_(inst_55004)){
var statearr_55076_55128 = state_55039__$1;
(statearr_55076_55128[(1)] = (26));

} else {
var statearr_55077_55129 = state_55039__$1;
(statearr_55077_55129[(1)] = (27));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (35))){
var inst_55024 = (state_55039[(2)]);
var state_55039__$1 = state_55039;
if(cljs.core.truth_(inst_55024)){
var statearr_55078_55130 = state_55039__$1;
(statearr_55078_55130[(1)] = (36));

} else {
var statearr_55079_55131 = state_55039__$1;
(statearr_55079_55131[(1)] = (37));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (19))){
var inst_54966 = (state_55039[(7)]);
var inst_54986 = cljs.core.apply.call(null,cljs.core.hash_map,inst_54966);
var state_55039__$1 = state_55039;
var statearr_55080_55132 = state_55039__$1;
(statearr_55080_55132[(2)] = inst_54986);

(statearr_55080_55132[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (11))){
var inst_54966 = (state_55039[(7)]);
var inst_54970 = (inst_54966 == null);
var inst_54971 = cljs.core.not.call(null,inst_54970);
var state_55039__$1 = state_55039;
if(inst_54971){
var statearr_55081_55133 = state_55039__$1;
(statearr_55081_55133[(1)] = (13));

} else {
var statearr_55082_55134 = state_55039__$1;
(statearr_55082_55134[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (9))){
var inst_54941 = (state_55039[(8)]);
var state_55039__$1 = state_55039;
var statearr_55083_55135 = state_55039__$1;
(statearr_55083_55135[(2)] = inst_54941);

(statearr_55083_55135[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (5))){
var state_55039__$1 = state_55039;
var statearr_55084_55136 = state_55039__$1;
(statearr_55084_55136[(2)] = true);

(statearr_55084_55136[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (14))){
var state_55039__$1 = state_55039;
var statearr_55085_55137 = state_55039__$1;
(statearr_55085_55137[(2)] = false);

(statearr_55085_55137[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (26))){
var inst_54999 = (state_55039[(9)]);
var inst_55006 = cljs.core.swap_BANG_.call(null,cs,cljs.core.dissoc,inst_54999);
var state_55039__$1 = state_55039;
var statearr_55086_55138 = state_55039__$1;
(statearr_55086_55138[(2)] = inst_55006);

(statearr_55086_55138[(1)] = (28));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (16))){
var state_55039__$1 = state_55039;
var statearr_55087_55139 = state_55039__$1;
(statearr_55087_55139[(2)] = true);

(statearr_55087_55139[(1)] = (18));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (38))){
var inst_55029 = (state_55039[(2)]);
var state_55039__$1 = state_55039;
var statearr_55088_55140 = state_55039__$1;
(statearr_55088_55140[(2)] = inst_55029);

(statearr_55088_55140[(1)] = (34));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (30))){
var inst_54991 = (state_55039[(13)]);
var inst_54999 = (state_55039[(9)]);
var inst_54990 = (state_55039[(10)]);
var inst_55016 = cljs.core.empty_QMARK_.call(null,inst_54990);
var inst_55017 = inst_54991.call(null,inst_54999);
var inst_55018 = cljs.core.not.call(null,inst_55017);
var inst_55019 = (inst_55016) && (inst_55018);
var state_55039__$1 = state_55039;
var statearr_55089_55141 = state_55039__$1;
(statearr_55089_55141[(2)] = inst_55019);

(statearr_55089_55141[(1)] = (31));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (10))){
var inst_54941 = (state_55039[(8)]);
var inst_54962 = (state_55039[(2)]);
var inst_54963 = cljs.core.get.call(null,inst_54962,new cljs.core.Keyword(null,"solos","solos",1441458643));
var inst_54964 = cljs.core.get.call(null,inst_54962,new cljs.core.Keyword(null,"mutes","mutes",1068806309));
var inst_54965 = cljs.core.get.call(null,inst_54962,new cljs.core.Keyword(null,"reads","reads",-1215067361));
var inst_54966 = inst_54941;
var state_55039__$1 = (function (){var statearr_55090 = state_55039;
(statearr_55090[(16)] = inst_54963);

(statearr_55090[(17)] = inst_54964);

(statearr_55090[(7)] = inst_54966);

(statearr_55090[(18)] = inst_54965);

return statearr_55090;
})();
var statearr_55091_55142 = state_55039__$1;
(statearr_55091_55142[(2)] = null);

(statearr_55091_55142[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (18))){
var inst_54981 = (state_55039[(2)]);
var state_55039__$1 = state_55039;
var statearr_55092_55143 = state_55039__$1;
(statearr_55092_55143[(2)] = inst_54981);

(statearr_55092_55143[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (37))){
var state_55039__$1 = state_55039;
var statearr_55093_55144 = state_55039__$1;
(statearr_55093_55144[(2)] = null);

(statearr_55093_55144[(1)] = (38));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55040 === (8))){
var inst_54941 = (state_55039[(8)]);
var inst_54959 = cljs.core.apply.call(null,cljs.core.hash_map,inst_54941);
var state_55039__$1 = state_55039;
var statearr_55094_55145 = state_55039__$1;
(statearr_55094_55145[(2)] = inst_54959);

(statearr_55094_55145[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__54040__auto___55099,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
;
return ((function (switch__53950__auto__,c__54040__auto___55099,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m){
return (function() {
var cljs$core$async$mix_$_state_machine__53951__auto__ = null;
var cljs$core$async$mix_$_state_machine__53951__auto____0 = (function (){
var statearr_55095 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_55095[(0)] = cljs$core$async$mix_$_state_machine__53951__auto__);

(statearr_55095[(1)] = (1));

return statearr_55095;
});
var cljs$core$async$mix_$_state_machine__53951__auto____1 = (function (state_55039){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_55039);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e55096){if((e55096 instanceof Object)){
var ex__53954__auto__ = e55096;
var statearr_55097_55146 = state_55039;
(statearr_55097_55146[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_55039);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e55096;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__55147 = state_55039;
state_55039 = G__55147;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$mix_$_state_machine__53951__auto__ = function(state_55039){
switch(arguments.length){
case 0:
return cljs$core$async$mix_$_state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$mix_$_state_machine__53951__auto____1.call(this,state_55039);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$mix_$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mix_$_state_machine__53951__auto____0;
cljs$core$async$mix_$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mix_$_state_machine__53951__auto____1;
return cljs$core$async$mix_$_state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___55099,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
})();
var state__54042__auto__ = (function (){var statearr_55098 = f__54041__auto__.call(null);
(statearr_55098[(6)] = c__54040__auto___55099);

return statearr_55098;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___55099,cs,solo_modes,attrs,solo_mode,change,changed,pick,calc_state,m))
);


return m;
});
/**
 * Adds ch as an input to the mix
 */
cljs.core.async.admix = (function cljs$core$async$admix(mix,ch){
return cljs.core.async.admix_STAR_.call(null,mix,ch);
});
/**
 * Removes ch as an input to the mix
 */
cljs.core.async.unmix = (function cljs$core$async$unmix(mix,ch){
return cljs.core.async.unmix_STAR_.call(null,mix,ch);
});
/**
 * removes all inputs from the mix
 */
cljs.core.async.unmix_all = (function cljs$core$async$unmix_all(mix){
return cljs.core.async.unmix_all_STAR_.call(null,mix);
});
/**
 * Atomically sets the state(s) of one or more channels in a mix. The
 *   state map is a map of channels -> channel-state-map. A
 *   channel-state-map is a map of attrs -> boolean, where attr is one or
 *   more of :mute, :pause or :solo. Any states supplied are merged with
 *   the current state.
 * 
 *   Note that channels can be added to a mix via toggle, which can be
 *   used to add channels in a particular (e.g. paused) state.
 */
cljs.core.async.toggle = (function cljs$core$async$toggle(mix,state_map){
return cljs.core.async.toggle_STAR_.call(null,mix,state_map);
});
/**
 * Sets the solo mode of the mix. mode must be one of :mute or :pause
 */
cljs.core.async.solo_mode = (function cljs$core$async$solo_mode(mix,mode){
return cljs.core.async.solo_mode_STAR_.call(null,mix,mode);
});

/**
 * @interface
 */
cljs.core.async.Pub = function(){};

cljs.core.async.sub_STAR_ = (function cljs$core$async$sub_STAR_(p,v,ch,close_QMARK_){
if((!((p == null))) && (!((p.cljs$core$async$Pub$sub_STAR_$arity$4 == null)))){
return p.cljs$core$async$Pub$sub_STAR_$arity$4(p,v,ch,close_QMARK_);
} else {
var x__30908__auto__ = (((p == null))?null:p);
var m__30909__auto__ = (cljs.core.async.sub_STAR_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,p,v,ch,close_QMARK_);
} else {
var m__30909__auto____$1 = (cljs.core.async.sub_STAR_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,p,v,ch,close_QMARK_);
} else {
throw cljs.core.missing_protocol.call(null,"Pub.sub*",p);
}
}
}
});

cljs.core.async.unsub_STAR_ = (function cljs$core$async$unsub_STAR_(p,v,ch){
if((!((p == null))) && (!((p.cljs$core$async$Pub$unsub_STAR_$arity$3 == null)))){
return p.cljs$core$async$Pub$unsub_STAR_$arity$3(p,v,ch);
} else {
var x__30908__auto__ = (((p == null))?null:p);
var m__30909__auto__ = (cljs.core.async.unsub_STAR_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,p,v,ch);
} else {
var m__30909__auto____$1 = (cljs.core.async.unsub_STAR_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,p,v,ch);
} else {
throw cljs.core.missing_protocol.call(null,"Pub.unsub*",p);
}
}
}
});

cljs.core.async.unsub_all_STAR_ = (function cljs$core$async$unsub_all_STAR_(var_args){
var G__55149 = arguments.length;
switch (G__55149) {
case 1:
return cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$1 = (function (p){
if((!((p == null))) && (!((p.cljs$core$async$Pub$unsub_all_STAR_$arity$1 == null)))){
return p.cljs$core$async$Pub$unsub_all_STAR_$arity$1(p);
} else {
var x__30908__auto__ = (((p == null))?null:p);
var m__30909__auto__ = (cljs.core.async.unsub_all_STAR_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,p);
} else {
var m__30909__auto____$1 = (cljs.core.async.unsub_all_STAR_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,p);
} else {
throw cljs.core.missing_protocol.call(null,"Pub.unsub-all*",p);
}
}
}
});

cljs.core.async.unsub_all_STAR_.cljs$core$IFn$_invoke$arity$2 = (function (p,v){
if((!((p == null))) && (!((p.cljs$core$async$Pub$unsub_all_STAR_$arity$2 == null)))){
return p.cljs$core$async$Pub$unsub_all_STAR_$arity$2(p,v);
} else {
var x__30908__auto__ = (((p == null))?null:p);
var m__30909__auto__ = (cljs.core.async.unsub_all_STAR_[goog.typeOf(x__30908__auto__)]);
if(!((m__30909__auto__ == null))){
return m__30909__auto__.call(null,p,v);
} else {
var m__30909__auto____$1 = (cljs.core.async.unsub_all_STAR_["_"]);
if(!((m__30909__auto____$1 == null))){
return m__30909__auto____$1.call(null,p,v);
} else {
throw cljs.core.missing_protocol.call(null,"Pub.unsub-all*",p);
}
}
}
});

cljs.core.async.unsub_all_STAR_.cljs$lang$maxFixedArity = 2;


/**
 * Creates and returns a pub(lication) of the supplied channel,
 *   partitioned into topics by the topic-fn. topic-fn will be applied to
 *   each value on the channel and the result will determine the 'topic'
 *   on which that value will be put. Channels can be subscribed to
 *   receive copies of topics using 'sub', and unsubscribed using
 *   'unsub'. Each topic will be handled by an internal mult on a
 *   dedicated channel. By default these internal channels are
 *   unbuffered, but a buf-fn can be supplied which, given a topic,
 *   creates a buffer with desired properties.
 * 
 *   Each item is distributed to all subs in parallel and synchronously,
 *   i.e. each sub must accept before the next item is distributed. Use
 *   buffering/windowing to prevent slow subs from holding up the pub.
 * 
 *   Items received when there are no matching subs get dropped.
 * 
 *   Note that if buf-fns are used then each topic is handled
 *   asynchronously, i.e. if a channel is subscribed to more than one
 *   topic it should not expect them to be interleaved identically with
 *   the source.
 */
cljs.core.async.pub = (function cljs$core$async$pub(var_args){
var G__55153 = arguments.length;
switch (G__55153) {
case 2:
return cljs.core.async.pub.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.pub.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.pub.cljs$core$IFn$_invoke$arity$2 = (function (ch,topic_fn){
return cljs.core.async.pub.call(null,ch,topic_fn,cljs.core.constantly.call(null,null));
});

cljs.core.async.pub.cljs$core$IFn$_invoke$arity$3 = (function (ch,topic_fn,buf_fn){
var mults = cljs.core.atom.call(null,cljs.core.PersistentArrayMap.EMPTY);
var ensure_mult = ((function (mults){
return (function (topic){
var or__30175__auto__ = cljs.core.get.call(null,cljs.core.deref.call(null,mults),topic);
if(cljs.core.truth_(or__30175__auto__)){
return or__30175__auto__;
} else {
return cljs.core.get.call(null,cljs.core.swap_BANG_.call(null,mults,((function (or__30175__auto__,mults){
return (function (p1__55151_SHARP_){
if(cljs.core.truth_(p1__55151_SHARP_.call(null,topic))){
return p1__55151_SHARP_;
} else {
return cljs.core.assoc.call(null,p1__55151_SHARP_,topic,cljs.core.async.mult.call(null,cljs.core.async.chan.call(null,buf_fn.call(null,topic))));
}
});})(or__30175__auto__,mults))
),topic);
}
});})(mults))
;
var p = (function (){
if(typeof cljs.core.async.t_cljs$core$async55154 !== 'undefined'){
} else {

/**
* @constructor
 * @implements {cljs.core.async.Pub}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.async.Mux}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async55154 = (function (ch,topic_fn,buf_fn,mults,ensure_mult,meta55155){
this.ch = ch;
this.topic_fn = topic_fn;
this.buf_fn = buf_fn;
this.mults = mults;
this.ensure_mult = ensure_mult;
this.meta55155 = meta55155;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async55154.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (mults,ensure_mult){
return (function (_55156,meta55155__$1){
var self__ = this;
var _55156__$1 = this;
return (new cljs.core.async.t_cljs$core$async55154(self__.ch,self__.topic_fn,self__.buf_fn,self__.mults,self__.ensure_mult,meta55155__$1));
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async55154.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (mults,ensure_mult){
return (function (_55156){
var self__ = this;
var _55156__$1 = this;
return self__.meta55155;
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async55154.prototype.cljs$core$async$Mux$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async55154.prototype.cljs$core$async$Mux$muxch_STAR_$arity$1 = ((function (mults,ensure_mult){
return (function (_){
var self__ = this;
var ___$1 = this;
return self__.ch;
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async55154.prototype.cljs$core$async$Pub$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async55154.prototype.cljs$core$async$Pub$sub_STAR_$arity$4 = ((function (mults,ensure_mult){
return (function (p,topic,ch__$1,close_QMARK_){
var self__ = this;
var p__$1 = this;
var m = self__.ensure_mult.call(null,topic);
return cljs.core.async.tap.call(null,m,ch__$1,close_QMARK_);
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async55154.prototype.cljs$core$async$Pub$unsub_STAR_$arity$3 = ((function (mults,ensure_mult){
return (function (p,topic,ch__$1){
var self__ = this;
var p__$1 = this;
var temp__5290__auto__ = cljs.core.get.call(null,cljs.core.deref.call(null,self__.mults),topic);
if(cljs.core.truth_(temp__5290__auto__)){
var m = temp__5290__auto__;
return cljs.core.async.untap.call(null,m,ch__$1);
} else {
return null;
}
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async55154.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$1 = ((function (mults,ensure_mult){
return (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.reset_BANG_.call(null,self__.mults,cljs.core.PersistentArrayMap.EMPTY);
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async55154.prototype.cljs$core$async$Pub$unsub_all_STAR_$arity$2 = ((function (mults,ensure_mult){
return (function (_,topic){
var self__ = this;
var ___$1 = this;
return cljs.core.swap_BANG_.call(null,self__.mults,cljs.core.dissoc,topic);
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async55154.getBasis = ((function (mults,ensure_mult){
return (function (){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"topic-fn","topic-fn",-862449736,null),new cljs.core.Symbol(null,"buf-fn","buf-fn",-1200281591,null),new cljs.core.Symbol(null,"mults","mults",-461114485,null),new cljs.core.Symbol(null,"ensure-mult","ensure-mult",1796584816,null),new cljs.core.Symbol(null,"meta55155","meta55155",-365720207,null)], null);
});})(mults,ensure_mult))
;

cljs.core.async.t_cljs$core$async55154.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async55154.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async55154";

cljs.core.async.t_cljs$core$async55154.cljs$lang$ctorPrWriter = ((function (mults,ensure_mult){
return (function (this__30846__auto__,writer__30847__auto__,opt__30848__auto__){
return cljs.core._write.call(null,writer__30847__auto__,"cljs.core.async/t_cljs$core$async55154");
});})(mults,ensure_mult))
;

cljs.core.async.__GT_t_cljs$core$async55154 = ((function (mults,ensure_mult){
return (function cljs$core$async$__GT_t_cljs$core$async55154(ch__$1,topic_fn__$1,buf_fn__$1,mults__$1,ensure_mult__$1,meta55155){
return (new cljs.core.async.t_cljs$core$async55154(ch__$1,topic_fn__$1,buf_fn__$1,mults__$1,ensure_mult__$1,meta55155));
});})(mults,ensure_mult))
;

}

return (new cljs.core.async.t_cljs$core$async55154(ch,topic_fn,buf_fn,mults,ensure_mult,cljs.core.PersistentArrayMap.EMPTY));
})()
;
var c__54040__auto___55274 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___55274,mults,ensure_mult,p){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___55274,mults,ensure_mult,p){
return (function (state_55228){
var state_val_55229 = (state_55228[(1)]);
if((state_val_55229 === (7))){
var inst_55224 = (state_55228[(2)]);
var state_55228__$1 = state_55228;
var statearr_55230_55275 = state_55228__$1;
(statearr_55230_55275[(2)] = inst_55224);

(statearr_55230_55275[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (20))){
var state_55228__$1 = state_55228;
var statearr_55231_55276 = state_55228__$1;
(statearr_55231_55276[(2)] = null);

(statearr_55231_55276[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (1))){
var state_55228__$1 = state_55228;
var statearr_55232_55277 = state_55228__$1;
(statearr_55232_55277[(2)] = null);

(statearr_55232_55277[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (24))){
var inst_55207 = (state_55228[(7)]);
var inst_55216 = cljs.core.swap_BANG_.call(null,mults,cljs.core.dissoc,inst_55207);
var state_55228__$1 = state_55228;
var statearr_55233_55278 = state_55228__$1;
(statearr_55233_55278[(2)] = inst_55216);

(statearr_55233_55278[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (4))){
var inst_55159 = (state_55228[(8)]);
var inst_55159__$1 = (state_55228[(2)]);
var inst_55160 = (inst_55159__$1 == null);
var state_55228__$1 = (function (){var statearr_55234 = state_55228;
(statearr_55234[(8)] = inst_55159__$1);

return statearr_55234;
})();
if(cljs.core.truth_(inst_55160)){
var statearr_55235_55279 = state_55228__$1;
(statearr_55235_55279[(1)] = (5));

} else {
var statearr_55236_55280 = state_55228__$1;
(statearr_55236_55280[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (15))){
var inst_55201 = (state_55228[(2)]);
var state_55228__$1 = state_55228;
var statearr_55237_55281 = state_55228__$1;
(statearr_55237_55281[(2)] = inst_55201);

(statearr_55237_55281[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (21))){
var inst_55221 = (state_55228[(2)]);
var state_55228__$1 = (function (){var statearr_55238 = state_55228;
(statearr_55238[(9)] = inst_55221);

return statearr_55238;
})();
var statearr_55239_55282 = state_55228__$1;
(statearr_55239_55282[(2)] = null);

(statearr_55239_55282[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (13))){
var inst_55183 = (state_55228[(10)]);
var inst_55185 = cljs.core.chunked_seq_QMARK_.call(null,inst_55183);
var state_55228__$1 = state_55228;
if(inst_55185){
var statearr_55240_55283 = state_55228__$1;
(statearr_55240_55283[(1)] = (16));

} else {
var statearr_55241_55284 = state_55228__$1;
(statearr_55241_55284[(1)] = (17));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (22))){
var inst_55213 = (state_55228[(2)]);
var state_55228__$1 = state_55228;
if(cljs.core.truth_(inst_55213)){
var statearr_55242_55285 = state_55228__$1;
(statearr_55242_55285[(1)] = (23));

} else {
var statearr_55243_55286 = state_55228__$1;
(statearr_55243_55286[(1)] = (24));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (6))){
var inst_55207 = (state_55228[(7)]);
var inst_55209 = (state_55228[(11)]);
var inst_55159 = (state_55228[(8)]);
var inst_55207__$1 = topic_fn.call(null,inst_55159);
var inst_55208 = cljs.core.deref.call(null,mults);
var inst_55209__$1 = cljs.core.get.call(null,inst_55208,inst_55207__$1);
var state_55228__$1 = (function (){var statearr_55244 = state_55228;
(statearr_55244[(7)] = inst_55207__$1);

(statearr_55244[(11)] = inst_55209__$1);

return statearr_55244;
})();
if(cljs.core.truth_(inst_55209__$1)){
var statearr_55245_55287 = state_55228__$1;
(statearr_55245_55287[(1)] = (19));

} else {
var statearr_55246_55288 = state_55228__$1;
(statearr_55246_55288[(1)] = (20));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (25))){
var inst_55218 = (state_55228[(2)]);
var state_55228__$1 = state_55228;
var statearr_55247_55289 = state_55228__$1;
(statearr_55247_55289[(2)] = inst_55218);

(statearr_55247_55289[(1)] = (21));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (17))){
var inst_55183 = (state_55228[(10)]);
var inst_55192 = cljs.core.first.call(null,inst_55183);
var inst_55193 = cljs.core.async.muxch_STAR_.call(null,inst_55192);
var inst_55194 = cljs.core.async.close_BANG_.call(null,inst_55193);
var inst_55195 = cljs.core.next.call(null,inst_55183);
var inst_55169 = inst_55195;
var inst_55170 = null;
var inst_55171 = (0);
var inst_55172 = (0);
var state_55228__$1 = (function (){var statearr_55248 = state_55228;
(statearr_55248[(12)] = inst_55194);

(statearr_55248[(13)] = inst_55169);

(statearr_55248[(14)] = inst_55171);

(statearr_55248[(15)] = inst_55170);

(statearr_55248[(16)] = inst_55172);

return statearr_55248;
})();
var statearr_55249_55290 = state_55228__$1;
(statearr_55249_55290[(2)] = null);

(statearr_55249_55290[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (3))){
var inst_55226 = (state_55228[(2)]);
var state_55228__$1 = state_55228;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_55228__$1,inst_55226);
} else {
if((state_val_55229 === (12))){
var inst_55203 = (state_55228[(2)]);
var state_55228__$1 = state_55228;
var statearr_55250_55291 = state_55228__$1;
(statearr_55250_55291[(2)] = inst_55203);

(statearr_55250_55291[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (2))){
var state_55228__$1 = state_55228;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_55228__$1,(4),ch);
} else {
if((state_val_55229 === (23))){
var state_55228__$1 = state_55228;
var statearr_55251_55292 = state_55228__$1;
(statearr_55251_55292[(2)] = null);

(statearr_55251_55292[(1)] = (25));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (19))){
var inst_55209 = (state_55228[(11)]);
var inst_55159 = (state_55228[(8)]);
var inst_55211 = cljs.core.async.muxch_STAR_.call(null,inst_55209);
var state_55228__$1 = state_55228;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_55228__$1,(22),inst_55211,inst_55159);
} else {
if((state_val_55229 === (11))){
var inst_55169 = (state_55228[(13)]);
var inst_55183 = (state_55228[(10)]);
var inst_55183__$1 = cljs.core.seq.call(null,inst_55169);
var state_55228__$1 = (function (){var statearr_55252 = state_55228;
(statearr_55252[(10)] = inst_55183__$1);

return statearr_55252;
})();
if(inst_55183__$1){
var statearr_55253_55293 = state_55228__$1;
(statearr_55253_55293[(1)] = (13));

} else {
var statearr_55254_55294 = state_55228__$1;
(statearr_55254_55294[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (9))){
var inst_55205 = (state_55228[(2)]);
var state_55228__$1 = state_55228;
var statearr_55255_55295 = state_55228__$1;
(statearr_55255_55295[(2)] = inst_55205);

(statearr_55255_55295[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (5))){
var inst_55166 = cljs.core.deref.call(null,mults);
var inst_55167 = cljs.core.vals.call(null,inst_55166);
var inst_55168 = cljs.core.seq.call(null,inst_55167);
var inst_55169 = inst_55168;
var inst_55170 = null;
var inst_55171 = (0);
var inst_55172 = (0);
var state_55228__$1 = (function (){var statearr_55256 = state_55228;
(statearr_55256[(13)] = inst_55169);

(statearr_55256[(14)] = inst_55171);

(statearr_55256[(15)] = inst_55170);

(statearr_55256[(16)] = inst_55172);

return statearr_55256;
})();
var statearr_55257_55296 = state_55228__$1;
(statearr_55257_55296[(2)] = null);

(statearr_55257_55296[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (14))){
var state_55228__$1 = state_55228;
var statearr_55261_55297 = state_55228__$1;
(statearr_55261_55297[(2)] = null);

(statearr_55261_55297[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (16))){
var inst_55183 = (state_55228[(10)]);
var inst_55187 = cljs.core.chunk_first.call(null,inst_55183);
var inst_55188 = cljs.core.chunk_rest.call(null,inst_55183);
var inst_55189 = cljs.core.count.call(null,inst_55187);
var inst_55169 = inst_55188;
var inst_55170 = inst_55187;
var inst_55171 = inst_55189;
var inst_55172 = (0);
var state_55228__$1 = (function (){var statearr_55262 = state_55228;
(statearr_55262[(13)] = inst_55169);

(statearr_55262[(14)] = inst_55171);

(statearr_55262[(15)] = inst_55170);

(statearr_55262[(16)] = inst_55172);

return statearr_55262;
})();
var statearr_55263_55298 = state_55228__$1;
(statearr_55263_55298[(2)] = null);

(statearr_55263_55298[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (10))){
var inst_55169 = (state_55228[(13)]);
var inst_55171 = (state_55228[(14)]);
var inst_55170 = (state_55228[(15)]);
var inst_55172 = (state_55228[(16)]);
var inst_55177 = cljs.core._nth.call(null,inst_55170,inst_55172);
var inst_55178 = cljs.core.async.muxch_STAR_.call(null,inst_55177);
var inst_55179 = cljs.core.async.close_BANG_.call(null,inst_55178);
var inst_55180 = (inst_55172 + (1));
var tmp55258 = inst_55169;
var tmp55259 = inst_55171;
var tmp55260 = inst_55170;
var inst_55169__$1 = tmp55258;
var inst_55170__$1 = tmp55260;
var inst_55171__$1 = tmp55259;
var inst_55172__$1 = inst_55180;
var state_55228__$1 = (function (){var statearr_55264 = state_55228;
(statearr_55264[(13)] = inst_55169__$1);

(statearr_55264[(17)] = inst_55179);

(statearr_55264[(14)] = inst_55171__$1);

(statearr_55264[(15)] = inst_55170__$1);

(statearr_55264[(16)] = inst_55172__$1);

return statearr_55264;
})();
var statearr_55265_55299 = state_55228__$1;
(statearr_55265_55299[(2)] = null);

(statearr_55265_55299[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (18))){
var inst_55198 = (state_55228[(2)]);
var state_55228__$1 = state_55228;
var statearr_55266_55300 = state_55228__$1;
(statearr_55266_55300[(2)] = inst_55198);

(statearr_55266_55300[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55229 === (8))){
var inst_55171 = (state_55228[(14)]);
var inst_55172 = (state_55228[(16)]);
var inst_55174 = (inst_55172 < inst_55171);
var inst_55175 = inst_55174;
var state_55228__$1 = state_55228;
if(cljs.core.truth_(inst_55175)){
var statearr_55267_55301 = state_55228__$1;
(statearr_55267_55301[(1)] = (10));

} else {
var statearr_55268_55302 = state_55228__$1;
(statearr_55268_55302[(1)] = (11));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__54040__auto___55274,mults,ensure_mult,p))
;
return ((function (switch__53950__auto__,c__54040__auto___55274,mults,ensure_mult,p){
return (function() {
var cljs$core$async$state_machine__53951__auto__ = null;
var cljs$core$async$state_machine__53951__auto____0 = (function (){
var statearr_55269 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_55269[(0)] = cljs$core$async$state_machine__53951__auto__);

(statearr_55269[(1)] = (1));

return statearr_55269;
});
var cljs$core$async$state_machine__53951__auto____1 = (function (state_55228){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_55228);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e55270){if((e55270 instanceof Object)){
var ex__53954__auto__ = e55270;
var statearr_55271_55303 = state_55228;
(statearr_55271_55303[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_55228);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e55270;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__55304 = state_55228;
state_55228 = G__55304;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$state_machine__53951__auto__ = function(state_55228){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__53951__auto____1.call(this,state_55228);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__53951__auto____0;
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__53951__auto____1;
return cljs$core$async$state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___55274,mults,ensure_mult,p))
})();
var state__54042__auto__ = (function (){var statearr_55272 = f__54041__auto__.call(null);
(statearr_55272[(6)] = c__54040__auto___55274);

return statearr_55272;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___55274,mults,ensure_mult,p))
);


return p;
});

cljs.core.async.pub.cljs$lang$maxFixedArity = 3;

/**
 * Subscribes a channel to a topic of a pub.
 * 
 *   By default the channel will be closed when the source closes,
 *   but can be determined by the close? parameter.
 */
cljs.core.async.sub = (function cljs$core$async$sub(var_args){
var G__55306 = arguments.length;
switch (G__55306) {
case 3:
return cljs.core.async.sub.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
case 4:
return cljs.core.async.sub.cljs$core$IFn$_invoke$arity$4((arguments[(0)]),(arguments[(1)]),(arguments[(2)]),(arguments[(3)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.sub.cljs$core$IFn$_invoke$arity$3 = (function (p,topic,ch){
return cljs.core.async.sub.call(null,p,topic,ch,true);
});

cljs.core.async.sub.cljs$core$IFn$_invoke$arity$4 = (function (p,topic,ch,close_QMARK_){
return cljs.core.async.sub_STAR_.call(null,p,topic,ch,close_QMARK_);
});

cljs.core.async.sub.cljs$lang$maxFixedArity = 4;

/**
 * Unsubscribes a channel from a topic of a pub
 */
cljs.core.async.unsub = (function cljs$core$async$unsub(p,topic,ch){
return cljs.core.async.unsub_STAR_.call(null,p,topic,ch);
});
/**
 * Unsubscribes all channels from a pub, or a topic of a pub
 */
cljs.core.async.unsub_all = (function cljs$core$async$unsub_all(var_args){
var G__55309 = arguments.length;
switch (G__55309) {
case 1:
return cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$1 = (function (p){
return cljs.core.async.unsub_all_STAR_.call(null,p);
});

cljs.core.async.unsub_all.cljs$core$IFn$_invoke$arity$2 = (function (p,topic){
return cljs.core.async.unsub_all_STAR_.call(null,p,topic);
});

cljs.core.async.unsub_all.cljs$lang$maxFixedArity = 2;

/**
 * Takes a function and a collection of source channels, and returns a
 *   channel which contains the values produced by applying f to the set
 *   of first items taken from each source channel, followed by applying
 *   f to the set of second items from each channel, until any one of the
 *   channels is closed, at which point the output channel will be
 *   closed. The returned channel will be unbuffered by default, or a
 *   buf-or-n can be supplied
 */
cljs.core.async.map = (function cljs$core$async$map(var_args){
var G__55312 = arguments.length;
switch (G__55312) {
case 2:
return cljs.core.async.map.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.map.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.map.cljs$core$IFn$_invoke$arity$2 = (function (f,chs){
return cljs.core.async.map.call(null,f,chs,null);
});

cljs.core.async.map.cljs$core$IFn$_invoke$arity$3 = (function (f,chs,buf_or_n){
var chs__$1 = cljs.core.vec.call(null,chs);
var out = cljs.core.async.chan.call(null,buf_or_n);
var cnt = cljs.core.count.call(null,chs__$1);
var rets = cljs.core.object_array.call(null,cnt);
var dchan = cljs.core.async.chan.call(null,(1));
var dctr = cljs.core.atom.call(null,null);
var done = cljs.core.mapv.call(null,((function (chs__$1,out,cnt,rets,dchan,dctr){
return (function (i){
return ((function (chs__$1,out,cnt,rets,dchan,dctr){
return (function (ret){
(rets[i] = ret);

if((cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec) === (0))){
return cljs.core.async.put_BANG_.call(null,dchan,rets.slice((0)));
} else {
return null;
}
});
;})(chs__$1,out,cnt,rets,dchan,dctr))
});})(chs__$1,out,cnt,rets,dchan,dctr))
,cljs.core.range.call(null,cnt));
var c__54040__auto___55379 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___55379,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___55379,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function (state_55351){
var state_val_55352 = (state_55351[(1)]);
if((state_val_55352 === (7))){
var state_55351__$1 = state_55351;
var statearr_55353_55380 = state_55351__$1;
(statearr_55353_55380[(2)] = null);

(statearr_55353_55380[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55352 === (1))){
var state_55351__$1 = state_55351;
var statearr_55354_55381 = state_55351__$1;
(statearr_55354_55381[(2)] = null);

(statearr_55354_55381[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55352 === (4))){
var inst_55315 = (state_55351[(7)]);
var inst_55317 = (inst_55315 < cnt);
var state_55351__$1 = state_55351;
if(cljs.core.truth_(inst_55317)){
var statearr_55355_55382 = state_55351__$1;
(statearr_55355_55382[(1)] = (6));

} else {
var statearr_55356_55383 = state_55351__$1;
(statearr_55356_55383[(1)] = (7));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55352 === (15))){
var inst_55347 = (state_55351[(2)]);
var state_55351__$1 = state_55351;
var statearr_55357_55384 = state_55351__$1;
(statearr_55357_55384[(2)] = inst_55347);

(statearr_55357_55384[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55352 === (13))){
var inst_55340 = cljs.core.async.close_BANG_.call(null,out);
var state_55351__$1 = state_55351;
var statearr_55358_55385 = state_55351__$1;
(statearr_55358_55385[(2)] = inst_55340);

(statearr_55358_55385[(1)] = (15));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55352 === (6))){
var state_55351__$1 = state_55351;
var statearr_55359_55386 = state_55351__$1;
(statearr_55359_55386[(2)] = null);

(statearr_55359_55386[(1)] = (11));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55352 === (3))){
var inst_55349 = (state_55351[(2)]);
var state_55351__$1 = state_55351;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_55351__$1,inst_55349);
} else {
if((state_val_55352 === (12))){
var inst_55337 = (state_55351[(8)]);
var inst_55337__$1 = (state_55351[(2)]);
var inst_55338 = cljs.core.some.call(null,cljs.core.nil_QMARK_,inst_55337__$1);
var state_55351__$1 = (function (){var statearr_55360 = state_55351;
(statearr_55360[(8)] = inst_55337__$1);

return statearr_55360;
})();
if(cljs.core.truth_(inst_55338)){
var statearr_55361_55387 = state_55351__$1;
(statearr_55361_55387[(1)] = (13));

} else {
var statearr_55362_55388 = state_55351__$1;
(statearr_55362_55388[(1)] = (14));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55352 === (2))){
var inst_55314 = cljs.core.reset_BANG_.call(null,dctr,cnt);
var inst_55315 = (0);
var state_55351__$1 = (function (){var statearr_55363 = state_55351;
(statearr_55363[(9)] = inst_55314);

(statearr_55363[(7)] = inst_55315);

return statearr_55363;
})();
var statearr_55364_55389 = state_55351__$1;
(statearr_55364_55389[(2)] = null);

(statearr_55364_55389[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55352 === (11))){
var inst_55315 = (state_55351[(7)]);
var _ = cljs.core.async.impl.ioc_helpers.add_exception_frame.call(null,state_55351,(10),Object,null,(9));
var inst_55324 = chs__$1.call(null,inst_55315);
var inst_55325 = done.call(null,inst_55315);
var inst_55326 = cljs.core.async.take_BANG_.call(null,inst_55324,inst_55325);
var state_55351__$1 = state_55351;
var statearr_55365_55390 = state_55351__$1;
(statearr_55365_55390[(2)] = inst_55326);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_55351__$1);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55352 === (9))){
var inst_55315 = (state_55351[(7)]);
var inst_55328 = (state_55351[(2)]);
var inst_55329 = (inst_55315 + (1));
var inst_55315__$1 = inst_55329;
var state_55351__$1 = (function (){var statearr_55366 = state_55351;
(statearr_55366[(10)] = inst_55328);

(statearr_55366[(7)] = inst_55315__$1);

return statearr_55366;
})();
var statearr_55367_55391 = state_55351__$1;
(statearr_55367_55391[(2)] = null);

(statearr_55367_55391[(1)] = (4));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55352 === (5))){
var inst_55335 = (state_55351[(2)]);
var state_55351__$1 = (function (){var statearr_55368 = state_55351;
(statearr_55368[(11)] = inst_55335);

return statearr_55368;
})();
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_55351__$1,(12),dchan);
} else {
if((state_val_55352 === (14))){
var inst_55337 = (state_55351[(8)]);
var inst_55342 = cljs.core.apply.call(null,f,inst_55337);
var state_55351__$1 = state_55351;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_55351__$1,(16),out,inst_55342);
} else {
if((state_val_55352 === (16))){
var inst_55344 = (state_55351[(2)]);
var state_55351__$1 = (function (){var statearr_55369 = state_55351;
(statearr_55369[(12)] = inst_55344);

return statearr_55369;
})();
var statearr_55370_55392 = state_55351__$1;
(statearr_55370_55392[(2)] = null);

(statearr_55370_55392[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55352 === (10))){
var inst_55319 = (state_55351[(2)]);
var inst_55320 = cljs.core.swap_BANG_.call(null,dctr,cljs.core.dec);
var state_55351__$1 = (function (){var statearr_55371 = state_55351;
(statearr_55371[(13)] = inst_55319);

return statearr_55371;
})();
var statearr_55372_55393 = state_55351__$1;
(statearr_55372_55393[(2)] = inst_55320);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_55351__$1);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55352 === (8))){
var inst_55333 = (state_55351[(2)]);
var state_55351__$1 = state_55351;
var statearr_55373_55394 = state_55351__$1;
(statearr_55373_55394[(2)] = inst_55333);

(statearr_55373_55394[(1)] = (5));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
}
}
}
}
}
}
}
});})(c__54040__auto___55379,chs__$1,out,cnt,rets,dchan,dctr,done))
;
return ((function (switch__53950__auto__,c__54040__auto___55379,chs__$1,out,cnt,rets,dchan,dctr,done){
return (function() {
var cljs$core$async$state_machine__53951__auto__ = null;
var cljs$core$async$state_machine__53951__auto____0 = (function (){
var statearr_55374 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_55374[(0)] = cljs$core$async$state_machine__53951__auto__);

(statearr_55374[(1)] = (1));

return statearr_55374;
});
var cljs$core$async$state_machine__53951__auto____1 = (function (state_55351){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_55351);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e55375){if((e55375 instanceof Object)){
var ex__53954__auto__ = e55375;
var statearr_55376_55395 = state_55351;
(statearr_55376_55395[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_55351);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e55375;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__55396 = state_55351;
state_55351 = G__55396;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$state_machine__53951__auto__ = function(state_55351){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__53951__auto____1.call(this,state_55351);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__53951__auto____0;
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__53951__auto____1;
return cljs$core$async$state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___55379,chs__$1,out,cnt,rets,dchan,dctr,done))
})();
var state__54042__auto__ = (function (){var statearr_55377 = f__54041__auto__.call(null);
(statearr_55377[(6)] = c__54040__auto___55379);

return statearr_55377;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___55379,chs__$1,out,cnt,rets,dchan,dctr,done))
);


return out;
});

cljs.core.async.map.cljs$lang$maxFixedArity = 3;

/**
 * Takes a collection of source channels and returns a channel which
 *   contains all values taken from them. The returned channel will be
 *   unbuffered by default, or a buf-or-n can be supplied. The channel
 *   will close after all the source channels have closed.
 */
cljs.core.async.merge = (function cljs$core$async$merge(var_args){
var G__55399 = arguments.length;
switch (G__55399) {
case 1:
return cljs.core.async.merge.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.merge.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.merge.cljs$core$IFn$_invoke$arity$1 = (function (chs){
return cljs.core.async.merge.call(null,chs,null);
});

cljs.core.async.merge.cljs$core$IFn$_invoke$arity$2 = (function (chs,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__54040__auto___55453 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___55453,out){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___55453,out){
return (function (state_55431){
var state_val_55432 = (state_55431[(1)]);
if((state_val_55432 === (7))){
var inst_55411 = (state_55431[(7)]);
var inst_55410 = (state_55431[(8)]);
var inst_55410__$1 = (state_55431[(2)]);
var inst_55411__$1 = cljs.core.nth.call(null,inst_55410__$1,(0),null);
var inst_55412 = cljs.core.nth.call(null,inst_55410__$1,(1),null);
var inst_55413 = (inst_55411__$1 == null);
var state_55431__$1 = (function (){var statearr_55433 = state_55431;
(statearr_55433[(9)] = inst_55412);

(statearr_55433[(7)] = inst_55411__$1);

(statearr_55433[(8)] = inst_55410__$1);

return statearr_55433;
})();
if(cljs.core.truth_(inst_55413)){
var statearr_55434_55454 = state_55431__$1;
(statearr_55434_55454[(1)] = (8));

} else {
var statearr_55435_55455 = state_55431__$1;
(statearr_55435_55455[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55432 === (1))){
var inst_55400 = cljs.core.vec.call(null,chs);
var inst_55401 = inst_55400;
var state_55431__$1 = (function (){var statearr_55436 = state_55431;
(statearr_55436[(10)] = inst_55401);

return statearr_55436;
})();
var statearr_55437_55456 = state_55431__$1;
(statearr_55437_55456[(2)] = null);

(statearr_55437_55456[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55432 === (4))){
var inst_55401 = (state_55431[(10)]);
var state_55431__$1 = state_55431;
return cljs.core.async.ioc_alts_BANG_.call(null,state_55431__$1,(7),inst_55401);
} else {
if((state_val_55432 === (6))){
var inst_55427 = (state_55431[(2)]);
var state_55431__$1 = state_55431;
var statearr_55438_55457 = state_55431__$1;
(statearr_55438_55457[(2)] = inst_55427);

(statearr_55438_55457[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55432 === (3))){
var inst_55429 = (state_55431[(2)]);
var state_55431__$1 = state_55431;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_55431__$1,inst_55429);
} else {
if((state_val_55432 === (2))){
var inst_55401 = (state_55431[(10)]);
var inst_55403 = cljs.core.count.call(null,inst_55401);
var inst_55404 = (inst_55403 > (0));
var state_55431__$1 = state_55431;
if(cljs.core.truth_(inst_55404)){
var statearr_55440_55458 = state_55431__$1;
(statearr_55440_55458[(1)] = (4));

} else {
var statearr_55441_55459 = state_55431__$1;
(statearr_55441_55459[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55432 === (11))){
var inst_55401 = (state_55431[(10)]);
var inst_55420 = (state_55431[(2)]);
var tmp55439 = inst_55401;
var inst_55401__$1 = tmp55439;
var state_55431__$1 = (function (){var statearr_55442 = state_55431;
(statearr_55442[(10)] = inst_55401__$1);

(statearr_55442[(11)] = inst_55420);

return statearr_55442;
})();
var statearr_55443_55460 = state_55431__$1;
(statearr_55443_55460[(2)] = null);

(statearr_55443_55460[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55432 === (9))){
var inst_55411 = (state_55431[(7)]);
var state_55431__$1 = state_55431;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_55431__$1,(11),out,inst_55411);
} else {
if((state_val_55432 === (5))){
var inst_55425 = cljs.core.async.close_BANG_.call(null,out);
var state_55431__$1 = state_55431;
var statearr_55444_55461 = state_55431__$1;
(statearr_55444_55461[(2)] = inst_55425);

(statearr_55444_55461[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55432 === (10))){
var inst_55423 = (state_55431[(2)]);
var state_55431__$1 = state_55431;
var statearr_55445_55462 = state_55431__$1;
(statearr_55445_55462[(2)] = inst_55423);

(statearr_55445_55462[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55432 === (8))){
var inst_55401 = (state_55431[(10)]);
var inst_55412 = (state_55431[(9)]);
var inst_55411 = (state_55431[(7)]);
var inst_55410 = (state_55431[(8)]);
var inst_55415 = (function (){var cs = inst_55401;
var vec__55406 = inst_55410;
var v = inst_55411;
var c = inst_55412;
return ((function (cs,vec__55406,v,c,inst_55401,inst_55412,inst_55411,inst_55410,state_val_55432,c__54040__auto___55453,out){
return (function (p1__55397_SHARP_){
return cljs.core.not_EQ_.call(null,c,p1__55397_SHARP_);
});
;})(cs,vec__55406,v,c,inst_55401,inst_55412,inst_55411,inst_55410,state_val_55432,c__54040__auto___55453,out))
})();
var inst_55416 = cljs.core.filterv.call(null,inst_55415,inst_55401);
var inst_55401__$1 = inst_55416;
var state_55431__$1 = (function (){var statearr_55446 = state_55431;
(statearr_55446[(10)] = inst_55401__$1);

return statearr_55446;
})();
var statearr_55447_55463 = state_55431__$1;
(statearr_55447_55463[(2)] = null);

(statearr_55447_55463[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
}
}
});})(c__54040__auto___55453,out))
;
return ((function (switch__53950__auto__,c__54040__auto___55453,out){
return (function() {
var cljs$core$async$state_machine__53951__auto__ = null;
var cljs$core$async$state_machine__53951__auto____0 = (function (){
var statearr_55448 = [null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_55448[(0)] = cljs$core$async$state_machine__53951__auto__);

(statearr_55448[(1)] = (1));

return statearr_55448;
});
var cljs$core$async$state_machine__53951__auto____1 = (function (state_55431){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_55431);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e55449){if((e55449 instanceof Object)){
var ex__53954__auto__ = e55449;
var statearr_55450_55464 = state_55431;
(statearr_55450_55464[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_55431);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e55449;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__55465 = state_55431;
state_55431 = G__55465;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$state_machine__53951__auto__ = function(state_55431){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__53951__auto____1.call(this,state_55431);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__53951__auto____0;
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__53951__auto____1;
return cljs$core$async$state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___55453,out))
})();
var state__54042__auto__ = (function (){var statearr_55451 = f__54041__auto__.call(null);
(statearr_55451[(6)] = c__54040__auto___55453);

return statearr_55451;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___55453,out))
);


return out;
});

cljs.core.async.merge.cljs$lang$maxFixedArity = 2;

/**
 * Returns a channel containing the single (collection) result of the
 *   items taken from the channel conjoined to the supplied
 *   collection. ch must close before into produces a result.
 */
cljs.core.async.into = (function cljs$core$async$into(coll,ch){
return cljs.core.async.reduce.call(null,cljs.core.conj,coll,ch);
});
/**
 * Returns a channel that will return, at most, n items from ch. After n items
 * have been returned, or ch has been closed, the return chanel will close.
 * 
 *   The output channel is unbuffered by default, unless buf-or-n is given.
 */
cljs.core.async.take = (function cljs$core$async$take(var_args){
var G__55467 = arguments.length;
switch (G__55467) {
case 2:
return cljs.core.async.take.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.take.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.take.cljs$core$IFn$_invoke$arity$2 = (function (n,ch){
return cljs.core.async.take.call(null,n,ch,null);
});

cljs.core.async.take.cljs$core$IFn$_invoke$arity$3 = (function (n,ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__54040__auto___55512 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___55512,out){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___55512,out){
return (function (state_55491){
var state_val_55492 = (state_55491[(1)]);
if((state_val_55492 === (7))){
var inst_55473 = (state_55491[(7)]);
var inst_55473__$1 = (state_55491[(2)]);
var inst_55474 = (inst_55473__$1 == null);
var inst_55475 = cljs.core.not.call(null,inst_55474);
var state_55491__$1 = (function (){var statearr_55493 = state_55491;
(statearr_55493[(7)] = inst_55473__$1);

return statearr_55493;
})();
if(inst_55475){
var statearr_55494_55513 = state_55491__$1;
(statearr_55494_55513[(1)] = (8));

} else {
var statearr_55495_55514 = state_55491__$1;
(statearr_55495_55514[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55492 === (1))){
var inst_55468 = (0);
var state_55491__$1 = (function (){var statearr_55496 = state_55491;
(statearr_55496[(8)] = inst_55468);

return statearr_55496;
})();
var statearr_55497_55515 = state_55491__$1;
(statearr_55497_55515[(2)] = null);

(statearr_55497_55515[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55492 === (4))){
var state_55491__$1 = state_55491;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_55491__$1,(7),ch);
} else {
if((state_val_55492 === (6))){
var inst_55486 = (state_55491[(2)]);
var state_55491__$1 = state_55491;
var statearr_55498_55516 = state_55491__$1;
(statearr_55498_55516[(2)] = inst_55486);

(statearr_55498_55516[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55492 === (3))){
var inst_55488 = (state_55491[(2)]);
var inst_55489 = cljs.core.async.close_BANG_.call(null,out);
var state_55491__$1 = (function (){var statearr_55499 = state_55491;
(statearr_55499[(9)] = inst_55488);

return statearr_55499;
})();
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_55491__$1,inst_55489);
} else {
if((state_val_55492 === (2))){
var inst_55468 = (state_55491[(8)]);
var inst_55470 = (inst_55468 < n);
var state_55491__$1 = state_55491;
if(cljs.core.truth_(inst_55470)){
var statearr_55500_55517 = state_55491__$1;
(statearr_55500_55517[(1)] = (4));

} else {
var statearr_55501_55518 = state_55491__$1;
(statearr_55501_55518[(1)] = (5));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55492 === (11))){
var inst_55468 = (state_55491[(8)]);
var inst_55478 = (state_55491[(2)]);
var inst_55479 = (inst_55468 + (1));
var inst_55468__$1 = inst_55479;
var state_55491__$1 = (function (){var statearr_55502 = state_55491;
(statearr_55502[(8)] = inst_55468__$1);

(statearr_55502[(10)] = inst_55478);

return statearr_55502;
})();
var statearr_55503_55519 = state_55491__$1;
(statearr_55503_55519[(2)] = null);

(statearr_55503_55519[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55492 === (9))){
var state_55491__$1 = state_55491;
var statearr_55504_55520 = state_55491__$1;
(statearr_55504_55520[(2)] = null);

(statearr_55504_55520[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55492 === (5))){
var state_55491__$1 = state_55491;
var statearr_55505_55521 = state_55491__$1;
(statearr_55505_55521[(2)] = null);

(statearr_55505_55521[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55492 === (10))){
var inst_55483 = (state_55491[(2)]);
var state_55491__$1 = state_55491;
var statearr_55506_55522 = state_55491__$1;
(statearr_55506_55522[(2)] = inst_55483);

(statearr_55506_55522[(1)] = (6));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55492 === (8))){
var inst_55473 = (state_55491[(7)]);
var state_55491__$1 = state_55491;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_55491__$1,(11),out,inst_55473);
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
}
}
}
});})(c__54040__auto___55512,out))
;
return ((function (switch__53950__auto__,c__54040__auto___55512,out){
return (function() {
var cljs$core$async$state_machine__53951__auto__ = null;
var cljs$core$async$state_machine__53951__auto____0 = (function (){
var statearr_55507 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_55507[(0)] = cljs$core$async$state_machine__53951__auto__);

(statearr_55507[(1)] = (1));

return statearr_55507;
});
var cljs$core$async$state_machine__53951__auto____1 = (function (state_55491){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_55491);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e55508){if((e55508 instanceof Object)){
var ex__53954__auto__ = e55508;
var statearr_55509_55523 = state_55491;
(statearr_55509_55523[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_55491);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e55508;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__55524 = state_55491;
state_55491 = G__55524;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$state_machine__53951__auto__ = function(state_55491){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__53951__auto____1.call(this,state_55491);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__53951__auto____0;
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__53951__auto____1;
return cljs$core$async$state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___55512,out))
})();
var state__54042__auto__ = (function (){var statearr_55510 = f__54041__auto__.call(null);
(statearr_55510[(6)] = c__54040__auto___55512);

return statearr_55510;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___55512,out))
);


return out;
});

cljs.core.async.take.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.map_LT_ = (function cljs$core$async$map_LT_(f,ch){
if(typeof cljs.core.async.t_cljs$core$async55526 !== 'undefined'){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async55526 = (function (f,ch,meta55527){
this.f = f;
this.ch = ch;
this.meta55527 = meta55527;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async55526.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_55528,meta55527__$1){
var self__ = this;
var _55528__$1 = this;
return (new cljs.core.async.t_cljs$core$async55526(self__.f,self__.ch,meta55527__$1));
});

cljs.core.async.t_cljs$core$async55526.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_55528){
var self__ = this;
var _55528__$1 = this;
return self__.meta55527;
});

cljs.core.async.t_cljs$core$async55526.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async55526.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async55526.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async55526.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async55526.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
var ret = cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,(function (){
if(typeof cljs.core.async.t_cljs$core$async55529 !== 'undefined'){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Handler}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async55529 = (function (f,ch,meta55527,_,fn1,meta55530){
this.f = f;
this.ch = ch;
this.meta55527 = meta55527;
this._ = _;
this.fn1 = fn1;
this.meta55530 = meta55530;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async55529.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = ((function (___$1){
return (function (_55531,meta55530__$1){
var self__ = this;
var _55531__$1 = this;
return (new cljs.core.async.t_cljs$core$async55529(self__.f,self__.ch,self__.meta55527,self__._,self__.fn1,meta55530__$1));
});})(___$1))
;

cljs.core.async.t_cljs$core$async55529.prototype.cljs$core$IMeta$_meta$arity$1 = ((function (___$1){
return (function (_55531){
var self__ = this;
var _55531__$1 = this;
return self__.meta55530;
});})(___$1))
;

cljs.core.async.t_cljs$core$async55529.prototype.cljs$core$async$impl$protocols$Handler$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async55529.prototype.cljs$core$async$impl$protocols$Handler$active_QMARK_$arity$1 = ((function (___$1){
return (function (___$1){
var self__ = this;
var ___$2 = this;
return cljs.core.async.impl.protocols.active_QMARK_.call(null,self__.fn1);
});})(___$1))
;

cljs.core.async.t_cljs$core$async55529.prototype.cljs$core$async$impl$protocols$Handler$blockable_QMARK_$arity$1 = ((function (___$1){
return (function (___$1){
var self__ = this;
var ___$2 = this;
return true;
});})(___$1))
;

cljs.core.async.t_cljs$core$async55529.prototype.cljs$core$async$impl$protocols$Handler$commit$arity$1 = ((function (___$1){
return (function (___$1){
var self__ = this;
var ___$2 = this;
var f1 = cljs.core.async.impl.protocols.commit.call(null,self__.fn1);
return ((function (f1,___$2,___$1){
return (function (p1__55525_SHARP_){
return f1.call(null,(((p1__55525_SHARP_ == null))?null:self__.f.call(null,p1__55525_SHARP_)));
});
;})(f1,___$2,___$1))
});})(___$1))
;

cljs.core.async.t_cljs$core$async55529.getBasis = ((function (___$1){
return (function (){
return new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta55527","meta55527",-1458577499,null),cljs.core.with_meta(new cljs.core.Symbol(null,"_","_",-1201019570,null),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Symbol("cljs.core.async","t_cljs$core$async55526","cljs.core.async/t_cljs$core$async55526",-1580767210,null)], null)),new cljs.core.Symbol(null,"fn1","fn1",895834444,null),new cljs.core.Symbol(null,"meta55530","meta55530",-375682718,null)], null);
});})(___$1))
;

cljs.core.async.t_cljs$core$async55529.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async55529.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async55529";

cljs.core.async.t_cljs$core$async55529.cljs$lang$ctorPrWriter = ((function (___$1){
return (function (this__30846__auto__,writer__30847__auto__,opt__30848__auto__){
return cljs.core._write.call(null,writer__30847__auto__,"cljs.core.async/t_cljs$core$async55529");
});})(___$1))
;

cljs.core.async.__GT_t_cljs$core$async55529 = ((function (___$1){
return (function cljs$core$async$map_LT__$___GT_t_cljs$core$async55529(f__$1,ch__$1,meta55527__$1,___$2,fn1__$1,meta55530){
return (new cljs.core.async.t_cljs$core$async55529(f__$1,ch__$1,meta55527__$1,___$2,fn1__$1,meta55530));
});})(___$1))
;

}

return (new cljs.core.async.t_cljs$core$async55529(self__.f,self__.ch,self__.meta55527,___$1,fn1,cljs.core.PersistentArrayMap.EMPTY));
})()
);
if(cljs.core.truth_((function (){var and__30163__auto__ = ret;
if(cljs.core.truth_(and__30163__auto__)){
return !((cljs.core.deref.call(null,ret) == null));
} else {
return and__30163__auto__;
}
})())){
return cljs.core.async.impl.channels.box.call(null,self__.f.call(null,cljs.core.deref.call(null,ret)));
} else {
return ret;
}
});

cljs.core.async.t_cljs$core$async55526.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async55526.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,val,fn1);
});

cljs.core.async.t_cljs$core$async55526.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta55527","meta55527",-1458577499,null)], null);
});

cljs.core.async.t_cljs$core$async55526.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async55526.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async55526";

cljs.core.async.t_cljs$core$async55526.cljs$lang$ctorPrWriter = (function (this__30846__auto__,writer__30847__auto__,opt__30848__auto__){
return cljs.core._write.call(null,writer__30847__auto__,"cljs.core.async/t_cljs$core$async55526");
});

cljs.core.async.__GT_t_cljs$core$async55526 = (function cljs$core$async$map_LT__$___GT_t_cljs$core$async55526(f__$1,ch__$1,meta55527){
return (new cljs.core.async.t_cljs$core$async55526(f__$1,ch__$1,meta55527));
});

}

return (new cljs.core.async.t_cljs$core$async55526(f,ch,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.map_GT_ = (function cljs$core$async$map_GT_(f,ch){
if(typeof cljs.core.async.t_cljs$core$async55532 !== 'undefined'){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async55532 = (function (f,ch,meta55533){
this.f = f;
this.ch = ch;
this.meta55533 = meta55533;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async55532.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_55534,meta55533__$1){
var self__ = this;
var _55534__$1 = this;
return (new cljs.core.async.t_cljs$core$async55532(self__.f,self__.ch,meta55533__$1));
});

cljs.core.async.t_cljs$core$async55532.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_55534){
var self__ = this;
var _55534__$1 = this;
return self__.meta55533;
});

cljs.core.async.t_cljs$core$async55532.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async55532.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async55532.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async55532.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,fn1);
});

cljs.core.async.t_cljs$core$async55532.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async55532.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,self__.f.call(null,val),fn1);
});

cljs.core.async.t_cljs$core$async55532.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"f","f",43394975,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta55533","meta55533",1972861435,null)], null);
});

cljs.core.async.t_cljs$core$async55532.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async55532.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async55532";

cljs.core.async.t_cljs$core$async55532.cljs$lang$ctorPrWriter = (function (this__30846__auto__,writer__30847__auto__,opt__30848__auto__){
return cljs.core._write.call(null,writer__30847__auto__,"cljs.core.async/t_cljs$core$async55532");
});

cljs.core.async.__GT_t_cljs$core$async55532 = (function cljs$core$async$map_GT__$___GT_t_cljs$core$async55532(f__$1,ch__$1,meta55533){
return (new cljs.core.async.t_cljs$core$async55532(f__$1,ch__$1,meta55533));
});

}

return (new cljs.core.async.t_cljs$core$async55532(f,ch,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.filter_GT_ = (function cljs$core$async$filter_GT_(p,ch){
if(typeof cljs.core.async.t_cljs$core$async55535 !== 'undefined'){
} else {

/**
* @constructor
 * @implements {cljs.core.async.impl.protocols.Channel}
 * @implements {cljs.core.async.impl.protocols.WritePort}
 * @implements {cljs.core.async.impl.protocols.ReadPort}
 * @implements {cljs.core.IMeta}
 * @implements {cljs.core.IWithMeta}
*/
cljs.core.async.t_cljs$core$async55535 = (function (p,ch,meta55536){
this.p = p;
this.ch = ch;
this.meta55536 = meta55536;
this.cljs$lang$protocol_mask$partition0$ = 393216;
this.cljs$lang$protocol_mask$partition1$ = 0;
});
cljs.core.async.t_cljs$core$async55535.prototype.cljs$core$IWithMeta$_with_meta$arity$2 = (function (_55537,meta55536__$1){
var self__ = this;
var _55537__$1 = this;
return (new cljs.core.async.t_cljs$core$async55535(self__.p,self__.ch,meta55536__$1));
});

cljs.core.async.t_cljs$core$async55535.prototype.cljs$core$IMeta$_meta$arity$1 = (function (_55537){
var self__ = this;
var _55537__$1 = this;
return self__.meta55536;
});

cljs.core.async.t_cljs$core$async55535.prototype.cljs$core$async$impl$protocols$Channel$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async55535.prototype.cljs$core$async$impl$protocols$Channel$close_BANG_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.close_BANG_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async55535.prototype.cljs$core$async$impl$protocols$Channel$closed_QMARK_$arity$1 = (function (_){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch);
});

cljs.core.async.t_cljs$core$async55535.prototype.cljs$core$async$impl$protocols$ReadPort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async55535.prototype.cljs$core$async$impl$protocols$ReadPort$take_BANG_$arity$2 = (function (_,fn1){
var self__ = this;
var ___$1 = this;
return cljs.core.async.impl.protocols.take_BANG_.call(null,self__.ch,fn1);
});

cljs.core.async.t_cljs$core$async55535.prototype.cljs$core$async$impl$protocols$WritePort$ = cljs.core.PROTOCOL_SENTINEL;

cljs.core.async.t_cljs$core$async55535.prototype.cljs$core$async$impl$protocols$WritePort$put_BANG_$arity$3 = (function (_,val,fn1){
var self__ = this;
var ___$1 = this;
if(cljs.core.truth_(self__.p.call(null,val))){
return cljs.core.async.impl.protocols.put_BANG_.call(null,self__.ch,val,fn1);
} else {
return cljs.core.async.impl.channels.box.call(null,cljs.core.not.call(null,cljs.core.async.impl.protocols.closed_QMARK_.call(null,self__.ch)));
}
});

cljs.core.async.t_cljs$core$async55535.getBasis = (function (){
return new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"p","p",1791580836,null),new cljs.core.Symbol(null,"ch","ch",1085813622,null),new cljs.core.Symbol(null,"meta55536","meta55536",1644807036,null)], null);
});

cljs.core.async.t_cljs$core$async55535.cljs$lang$type = true;

cljs.core.async.t_cljs$core$async55535.cljs$lang$ctorStr = "cljs.core.async/t_cljs$core$async55535";

cljs.core.async.t_cljs$core$async55535.cljs$lang$ctorPrWriter = (function (this__30846__auto__,writer__30847__auto__,opt__30848__auto__){
return cljs.core._write.call(null,writer__30847__auto__,"cljs.core.async/t_cljs$core$async55535");
});

cljs.core.async.__GT_t_cljs$core$async55535 = (function cljs$core$async$filter_GT__$___GT_t_cljs$core$async55535(p__$1,ch__$1,meta55536){
return (new cljs.core.async.t_cljs$core$async55535(p__$1,ch__$1,meta55536));
});

}

return (new cljs.core.async.t_cljs$core$async55535(p,ch,cljs.core.PersistentArrayMap.EMPTY));
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.remove_GT_ = (function cljs$core$async$remove_GT_(p,ch){
return cljs.core.async.filter_GT_.call(null,cljs.core.complement.call(null,p),ch);
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.filter_LT_ = (function cljs$core$async$filter_LT_(var_args){
var G__55539 = arguments.length;
switch (G__55539) {
case 2:
return cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.filter_LT_.call(null,p,ch,null);
});

cljs.core.async.filter_LT_.cljs$core$IFn$_invoke$arity$3 = (function (p,ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__54040__auto___55579 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___55579,out){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___55579,out){
return (function (state_55560){
var state_val_55561 = (state_55560[(1)]);
if((state_val_55561 === (7))){
var inst_55556 = (state_55560[(2)]);
var state_55560__$1 = state_55560;
var statearr_55562_55580 = state_55560__$1;
(statearr_55562_55580[(2)] = inst_55556);

(statearr_55562_55580[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55561 === (1))){
var state_55560__$1 = state_55560;
var statearr_55563_55581 = state_55560__$1;
(statearr_55563_55581[(2)] = null);

(statearr_55563_55581[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55561 === (4))){
var inst_55542 = (state_55560[(7)]);
var inst_55542__$1 = (state_55560[(2)]);
var inst_55543 = (inst_55542__$1 == null);
var state_55560__$1 = (function (){var statearr_55564 = state_55560;
(statearr_55564[(7)] = inst_55542__$1);

return statearr_55564;
})();
if(cljs.core.truth_(inst_55543)){
var statearr_55565_55582 = state_55560__$1;
(statearr_55565_55582[(1)] = (5));

} else {
var statearr_55566_55583 = state_55560__$1;
(statearr_55566_55583[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55561 === (6))){
var inst_55542 = (state_55560[(7)]);
var inst_55547 = p.call(null,inst_55542);
var state_55560__$1 = state_55560;
if(cljs.core.truth_(inst_55547)){
var statearr_55567_55584 = state_55560__$1;
(statearr_55567_55584[(1)] = (8));

} else {
var statearr_55568_55585 = state_55560__$1;
(statearr_55568_55585[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55561 === (3))){
var inst_55558 = (state_55560[(2)]);
var state_55560__$1 = state_55560;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_55560__$1,inst_55558);
} else {
if((state_val_55561 === (2))){
var state_55560__$1 = state_55560;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_55560__$1,(4),ch);
} else {
if((state_val_55561 === (11))){
var inst_55550 = (state_55560[(2)]);
var state_55560__$1 = state_55560;
var statearr_55569_55586 = state_55560__$1;
(statearr_55569_55586[(2)] = inst_55550);

(statearr_55569_55586[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55561 === (9))){
var state_55560__$1 = state_55560;
var statearr_55570_55587 = state_55560__$1;
(statearr_55570_55587[(2)] = null);

(statearr_55570_55587[(1)] = (10));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55561 === (5))){
var inst_55545 = cljs.core.async.close_BANG_.call(null,out);
var state_55560__$1 = state_55560;
var statearr_55571_55588 = state_55560__$1;
(statearr_55571_55588[(2)] = inst_55545);

(statearr_55571_55588[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55561 === (10))){
var inst_55553 = (state_55560[(2)]);
var state_55560__$1 = (function (){var statearr_55572 = state_55560;
(statearr_55572[(8)] = inst_55553);

return statearr_55572;
})();
var statearr_55573_55589 = state_55560__$1;
(statearr_55573_55589[(2)] = null);

(statearr_55573_55589[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55561 === (8))){
var inst_55542 = (state_55560[(7)]);
var state_55560__$1 = state_55560;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_55560__$1,(11),out,inst_55542);
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
}
}
}
});})(c__54040__auto___55579,out))
;
return ((function (switch__53950__auto__,c__54040__auto___55579,out){
return (function() {
var cljs$core$async$state_machine__53951__auto__ = null;
var cljs$core$async$state_machine__53951__auto____0 = (function (){
var statearr_55574 = [null,null,null,null,null,null,null,null,null];
(statearr_55574[(0)] = cljs$core$async$state_machine__53951__auto__);

(statearr_55574[(1)] = (1));

return statearr_55574;
});
var cljs$core$async$state_machine__53951__auto____1 = (function (state_55560){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_55560);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e55575){if((e55575 instanceof Object)){
var ex__53954__auto__ = e55575;
var statearr_55576_55590 = state_55560;
(statearr_55576_55590[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_55560);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e55575;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__55591 = state_55560;
state_55560 = G__55591;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$state_machine__53951__auto__ = function(state_55560){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__53951__auto____1.call(this,state_55560);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__53951__auto____0;
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__53951__auto____1;
return cljs$core$async$state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___55579,out))
})();
var state__54042__auto__ = (function (){var statearr_55577 = f__54041__auto__.call(null);
(statearr_55577[(6)] = c__54040__auto___55579);

return statearr_55577;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___55579,out))
);


return out;
});

cljs.core.async.filter_LT_.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.remove_LT_ = (function cljs$core$async$remove_LT_(var_args){
var G__55593 = arguments.length;
switch (G__55593) {
case 2:
return cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$2 = (function (p,ch){
return cljs.core.async.remove_LT_.call(null,p,ch,null);
});

cljs.core.async.remove_LT_.cljs$core$IFn$_invoke$arity$3 = (function (p,ch,buf_or_n){
return cljs.core.async.filter_LT_.call(null,cljs.core.complement.call(null,p),ch,buf_or_n);
});

cljs.core.async.remove_LT_.cljs$lang$maxFixedArity = 3;

cljs.core.async.mapcat_STAR_ = (function cljs$core$async$mapcat_STAR_(f,in$,out){
var c__54040__auto__ = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto__){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto__){
return (function (state_55656){
var state_val_55657 = (state_55656[(1)]);
if((state_val_55657 === (7))){
var inst_55652 = (state_55656[(2)]);
var state_55656__$1 = state_55656;
var statearr_55658_55696 = state_55656__$1;
(statearr_55658_55696[(2)] = inst_55652);

(statearr_55658_55696[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (20))){
var inst_55622 = (state_55656[(7)]);
var inst_55633 = (state_55656[(2)]);
var inst_55634 = cljs.core.next.call(null,inst_55622);
var inst_55608 = inst_55634;
var inst_55609 = null;
var inst_55610 = (0);
var inst_55611 = (0);
var state_55656__$1 = (function (){var statearr_55659 = state_55656;
(statearr_55659[(8)] = inst_55609);

(statearr_55659[(9)] = inst_55608);

(statearr_55659[(10)] = inst_55633);

(statearr_55659[(11)] = inst_55610);

(statearr_55659[(12)] = inst_55611);

return statearr_55659;
})();
var statearr_55660_55697 = state_55656__$1;
(statearr_55660_55697[(2)] = null);

(statearr_55660_55697[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (1))){
var state_55656__$1 = state_55656;
var statearr_55661_55698 = state_55656__$1;
(statearr_55661_55698[(2)] = null);

(statearr_55661_55698[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (4))){
var inst_55597 = (state_55656[(13)]);
var inst_55597__$1 = (state_55656[(2)]);
var inst_55598 = (inst_55597__$1 == null);
var state_55656__$1 = (function (){var statearr_55662 = state_55656;
(statearr_55662[(13)] = inst_55597__$1);

return statearr_55662;
})();
if(cljs.core.truth_(inst_55598)){
var statearr_55663_55699 = state_55656__$1;
(statearr_55663_55699[(1)] = (5));

} else {
var statearr_55664_55700 = state_55656__$1;
(statearr_55664_55700[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (15))){
var state_55656__$1 = state_55656;
var statearr_55668_55701 = state_55656__$1;
(statearr_55668_55701[(2)] = null);

(statearr_55668_55701[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (21))){
var state_55656__$1 = state_55656;
var statearr_55669_55702 = state_55656__$1;
(statearr_55669_55702[(2)] = null);

(statearr_55669_55702[(1)] = (23));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (13))){
var inst_55609 = (state_55656[(8)]);
var inst_55608 = (state_55656[(9)]);
var inst_55610 = (state_55656[(11)]);
var inst_55611 = (state_55656[(12)]);
var inst_55618 = (state_55656[(2)]);
var inst_55619 = (inst_55611 + (1));
var tmp55665 = inst_55609;
var tmp55666 = inst_55608;
var tmp55667 = inst_55610;
var inst_55608__$1 = tmp55666;
var inst_55609__$1 = tmp55665;
var inst_55610__$1 = tmp55667;
var inst_55611__$1 = inst_55619;
var state_55656__$1 = (function (){var statearr_55670 = state_55656;
(statearr_55670[(14)] = inst_55618);

(statearr_55670[(8)] = inst_55609__$1);

(statearr_55670[(9)] = inst_55608__$1);

(statearr_55670[(11)] = inst_55610__$1);

(statearr_55670[(12)] = inst_55611__$1);

return statearr_55670;
})();
var statearr_55671_55703 = state_55656__$1;
(statearr_55671_55703[(2)] = null);

(statearr_55671_55703[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (22))){
var state_55656__$1 = state_55656;
var statearr_55672_55704 = state_55656__$1;
(statearr_55672_55704[(2)] = null);

(statearr_55672_55704[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (6))){
var inst_55597 = (state_55656[(13)]);
var inst_55606 = f.call(null,inst_55597);
var inst_55607 = cljs.core.seq.call(null,inst_55606);
var inst_55608 = inst_55607;
var inst_55609 = null;
var inst_55610 = (0);
var inst_55611 = (0);
var state_55656__$1 = (function (){var statearr_55673 = state_55656;
(statearr_55673[(8)] = inst_55609);

(statearr_55673[(9)] = inst_55608);

(statearr_55673[(11)] = inst_55610);

(statearr_55673[(12)] = inst_55611);

return statearr_55673;
})();
var statearr_55674_55705 = state_55656__$1;
(statearr_55674_55705[(2)] = null);

(statearr_55674_55705[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (17))){
var inst_55622 = (state_55656[(7)]);
var inst_55626 = cljs.core.chunk_first.call(null,inst_55622);
var inst_55627 = cljs.core.chunk_rest.call(null,inst_55622);
var inst_55628 = cljs.core.count.call(null,inst_55626);
var inst_55608 = inst_55627;
var inst_55609 = inst_55626;
var inst_55610 = inst_55628;
var inst_55611 = (0);
var state_55656__$1 = (function (){var statearr_55675 = state_55656;
(statearr_55675[(8)] = inst_55609);

(statearr_55675[(9)] = inst_55608);

(statearr_55675[(11)] = inst_55610);

(statearr_55675[(12)] = inst_55611);

return statearr_55675;
})();
var statearr_55676_55706 = state_55656__$1;
(statearr_55676_55706[(2)] = null);

(statearr_55676_55706[(1)] = (8));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (3))){
var inst_55654 = (state_55656[(2)]);
var state_55656__$1 = state_55656;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_55656__$1,inst_55654);
} else {
if((state_val_55657 === (12))){
var inst_55642 = (state_55656[(2)]);
var state_55656__$1 = state_55656;
var statearr_55677_55707 = state_55656__$1;
(statearr_55677_55707[(2)] = inst_55642);

(statearr_55677_55707[(1)] = (9));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (2))){
var state_55656__$1 = state_55656;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_55656__$1,(4),in$);
} else {
if((state_val_55657 === (23))){
var inst_55650 = (state_55656[(2)]);
var state_55656__$1 = state_55656;
var statearr_55678_55708 = state_55656__$1;
(statearr_55678_55708[(2)] = inst_55650);

(statearr_55678_55708[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (19))){
var inst_55637 = (state_55656[(2)]);
var state_55656__$1 = state_55656;
var statearr_55679_55709 = state_55656__$1;
(statearr_55679_55709[(2)] = inst_55637);

(statearr_55679_55709[(1)] = (16));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (11))){
var inst_55608 = (state_55656[(9)]);
var inst_55622 = (state_55656[(7)]);
var inst_55622__$1 = cljs.core.seq.call(null,inst_55608);
var state_55656__$1 = (function (){var statearr_55680 = state_55656;
(statearr_55680[(7)] = inst_55622__$1);

return statearr_55680;
})();
if(inst_55622__$1){
var statearr_55681_55710 = state_55656__$1;
(statearr_55681_55710[(1)] = (14));

} else {
var statearr_55682_55711 = state_55656__$1;
(statearr_55682_55711[(1)] = (15));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (9))){
var inst_55644 = (state_55656[(2)]);
var inst_55645 = cljs.core.async.impl.protocols.closed_QMARK_.call(null,out);
var state_55656__$1 = (function (){var statearr_55683 = state_55656;
(statearr_55683[(15)] = inst_55644);

return statearr_55683;
})();
if(cljs.core.truth_(inst_55645)){
var statearr_55684_55712 = state_55656__$1;
(statearr_55684_55712[(1)] = (21));

} else {
var statearr_55685_55713 = state_55656__$1;
(statearr_55685_55713[(1)] = (22));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (5))){
var inst_55600 = cljs.core.async.close_BANG_.call(null,out);
var state_55656__$1 = state_55656;
var statearr_55686_55714 = state_55656__$1;
(statearr_55686_55714[(2)] = inst_55600);

(statearr_55686_55714[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (14))){
var inst_55622 = (state_55656[(7)]);
var inst_55624 = cljs.core.chunked_seq_QMARK_.call(null,inst_55622);
var state_55656__$1 = state_55656;
if(inst_55624){
var statearr_55687_55715 = state_55656__$1;
(statearr_55687_55715[(1)] = (17));

} else {
var statearr_55688_55716 = state_55656__$1;
(statearr_55688_55716[(1)] = (18));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (16))){
var inst_55640 = (state_55656[(2)]);
var state_55656__$1 = state_55656;
var statearr_55689_55717 = state_55656__$1;
(statearr_55689_55717[(2)] = inst_55640);

(statearr_55689_55717[(1)] = (12));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55657 === (10))){
var inst_55609 = (state_55656[(8)]);
var inst_55611 = (state_55656[(12)]);
var inst_55616 = cljs.core._nth.call(null,inst_55609,inst_55611);
var state_55656__$1 = state_55656;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_55656__$1,(13),out,inst_55616);
} else {
if((state_val_55657 === (18))){
var inst_55622 = (state_55656[(7)]);
var inst_55631 = cljs.core.first.call(null,inst_55622);
var state_55656__$1 = state_55656;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_55656__$1,(20),out,inst_55631);
} else {
if((state_val_55657 === (8))){
var inst_55610 = (state_55656[(11)]);
var inst_55611 = (state_55656[(12)]);
var inst_55613 = (inst_55611 < inst_55610);
var inst_55614 = inst_55613;
var state_55656__$1 = state_55656;
if(cljs.core.truth_(inst_55614)){
var statearr_55690_55718 = state_55656__$1;
(statearr_55690_55718[(1)] = (10));

} else {
var statearr_55691_55719 = state_55656__$1;
(statearr_55691_55719[(1)] = (11));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
}
}
}
}
}
}
}
}
}
}
}
}
}
}
});})(c__54040__auto__))
;
return ((function (switch__53950__auto__,c__54040__auto__){
return (function() {
var cljs$core$async$mapcat_STAR__$_state_machine__53951__auto__ = null;
var cljs$core$async$mapcat_STAR__$_state_machine__53951__auto____0 = (function (){
var statearr_55692 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_55692[(0)] = cljs$core$async$mapcat_STAR__$_state_machine__53951__auto__);

(statearr_55692[(1)] = (1));

return statearr_55692;
});
var cljs$core$async$mapcat_STAR__$_state_machine__53951__auto____1 = (function (state_55656){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_55656);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e55693){if((e55693 instanceof Object)){
var ex__53954__auto__ = e55693;
var statearr_55694_55720 = state_55656;
(statearr_55694_55720[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_55656);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e55693;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__55721 = state_55656;
state_55656 = G__55721;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$mapcat_STAR__$_state_machine__53951__auto__ = function(state_55656){
switch(arguments.length){
case 0:
return cljs$core$async$mapcat_STAR__$_state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$mapcat_STAR__$_state_machine__53951__auto____1.call(this,state_55656);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$mapcat_STAR__$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$mapcat_STAR__$_state_machine__53951__auto____0;
cljs$core$async$mapcat_STAR__$_state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$mapcat_STAR__$_state_machine__53951__auto____1;
return cljs$core$async$mapcat_STAR__$_state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto__))
})();
var state__54042__auto__ = (function (){var statearr_55695 = f__54041__auto__.call(null);
(statearr_55695[(6)] = c__54040__auto__);

return statearr_55695;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto__))
);

return c__54040__auto__;
});
/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.mapcat_LT_ = (function cljs$core$async$mapcat_LT_(var_args){
var G__55723 = arguments.length;
switch (G__55723) {
case 2:
return cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$2 = (function (f,in$){
return cljs.core.async.mapcat_LT_.call(null,f,in$,null);
});

cljs.core.async.mapcat_LT_.cljs$core$IFn$_invoke$arity$3 = (function (f,in$,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
cljs.core.async.mapcat_STAR_.call(null,f,in$,out);

return out;
});

cljs.core.async.mapcat_LT_.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.mapcat_GT_ = (function cljs$core$async$mapcat_GT_(var_args){
var G__55726 = arguments.length;
switch (G__55726) {
case 2:
return cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$2 = (function (f,out){
return cljs.core.async.mapcat_GT_.call(null,f,out,null);
});

cljs.core.async.mapcat_GT_.cljs$core$IFn$_invoke$arity$3 = (function (f,out,buf_or_n){
var in$ = cljs.core.async.chan.call(null,buf_or_n);
cljs.core.async.mapcat_STAR_.call(null,f,in$,out);

return in$;
});

cljs.core.async.mapcat_GT_.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.unique = (function cljs$core$async$unique(var_args){
var G__55729 = arguments.length;
switch (G__55729) {
case 1:
return cljs.core.async.unique.cljs$core$IFn$_invoke$arity$1((arguments[(0)]));

break;
case 2:
return cljs.core.async.unique.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.unique.cljs$core$IFn$_invoke$arity$1 = (function (ch){
return cljs.core.async.unique.call(null,ch,null);
});

cljs.core.async.unique.cljs$core$IFn$_invoke$arity$2 = (function (ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__54040__auto___55776 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___55776,out){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___55776,out){
return (function (state_55753){
var state_val_55754 = (state_55753[(1)]);
if((state_val_55754 === (7))){
var inst_55748 = (state_55753[(2)]);
var state_55753__$1 = state_55753;
var statearr_55755_55777 = state_55753__$1;
(statearr_55755_55777[(2)] = inst_55748);

(statearr_55755_55777[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55754 === (1))){
var inst_55730 = null;
var state_55753__$1 = (function (){var statearr_55756 = state_55753;
(statearr_55756[(7)] = inst_55730);

return statearr_55756;
})();
var statearr_55757_55778 = state_55753__$1;
(statearr_55757_55778[(2)] = null);

(statearr_55757_55778[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55754 === (4))){
var inst_55733 = (state_55753[(8)]);
var inst_55733__$1 = (state_55753[(2)]);
var inst_55734 = (inst_55733__$1 == null);
var inst_55735 = cljs.core.not.call(null,inst_55734);
var state_55753__$1 = (function (){var statearr_55758 = state_55753;
(statearr_55758[(8)] = inst_55733__$1);

return statearr_55758;
})();
if(inst_55735){
var statearr_55759_55779 = state_55753__$1;
(statearr_55759_55779[(1)] = (5));

} else {
var statearr_55760_55780 = state_55753__$1;
(statearr_55760_55780[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55754 === (6))){
var state_55753__$1 = state_55753;
var statearr_55761_55781 = state_55753__$1;
(statearr_55761_55781[(2)] = null);

(statearr_55761_55781[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55754 === (3))){
var inst_55750 = (state_55753[(2)]);
var inst_55751 = cljs.core.async.close_BANG_.call(null,out);
var state_55753__$1 = (function (){var statearr_55762 = state_55753;
(statearr_55762[(9)] = inst_55750);

return statearr_55762;
})();
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_55753__$1,inst_55751);
} else {
if((state_val_55754 === (2))){
var state_55753__$1 = state_55753;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_55753__$1,(4),ch);
} else {
if((state_val_55754 === (11))){
var inst_55733 = (state_55753[(8)]);
var inst_55742 = (state_55753[(2)]);
var inst_55730 = inst_55733;
var state_55753__$1 = (function (){var statearr_55763 = state_55753;
(statearr_55763[(7)] = inst_55730);

(statearr_55763[(10)] = inst_55742);

return statearr_55763;
})();
var statearr_55764_55782 = state_55753__$1;
(statearr_55764_55782[(2)] = null);

(statearr_55764_55782[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55754 === (9))){
var inst_55733 = (state_55753[(8)]);
var state_55753__$1 = state_55753;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_55753__$1,(11),out,inst_55733);
} else {
if((state_val_55754 === (5))){
var inst_55730 = (state_55753[(7)]);
var inst_55733 = (state_55753[(8)]);
var inst_55737 = cljs.core._EQ_.call(null,inst_55733,inst_55730);
var state_55753__$1 = state_55753;
if(inst_55737){
var statearr_55766_55783 = state_55753__$1;
(statearr_55766_55783[(1)] = (8));

} else {
var statearr_55767_55784 = state_55753__$1;
(statearr_55767_55784[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55754 === (10))){
var inst_55745 = (state_55753[(2)]);
var state_55753__$1 = state_55753;
var statearr_55768_55785 = state_55753__$1;
(statearr_55768_55785[(2)] = inst_55745);

(statearr_55768_55785[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55754 === (8))){
var inst_55730 = (state_55753[(7)]);
var tmp55765 = inst_55730;
var inst_55730__$1 = tmp55765;
var state_55753__$1 = (function (){var statearr_55769 = state_55753;
(statearr_55769[(7)] = inst_55730__$1);

return statearr_55769;
})();
var statearr_55770_55786 = state_55753__$1;
(statearr_55770_55786[(2)] = null);

(statearr_55770_55786[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
}
}
});})(c__54040__auto___55776,out))
;
return ((function (switch__53950__auto__,c__54040__auto___55776,out){
return (function() {
var cljs$core$async$state_machine__53951__auto__ = null;
var cljs$core$async$state_machine__53951__auto____0 = (function (){
var statearr_55771 = [null,null,null,null,null,null,null,null,null,null,null];
(statearr_55771[(0)] = cljs$core$async$state_machine__53951__auto__);

(statearr_55771[(1)] = (1));

return statearr_55771;
});
var cljs$core$async$state_machine__53951__auto____1 = (function (state_55753){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_55753);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e55772){if((e55772 instanceof Object)){
var ex__53954__auto__ = e55772;
var statearr_55773_55787 = state_55753;
(statearr_55773_55787[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_55753);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e55772;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__55788 = state_55753;
state_55753 = G__55788;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$state_machine__53951__auto__ = function(state_55753){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__53951__auto____1.call(this,state_55753);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__53951__auto____0;
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__53951__auto____1;
return cljs$core$async$state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___55776,out))
})();
var state__54042__auto__ = (function (){var statearr_55774 = f__54041__auto__.call(null);
(statearr_55774[(6)] = c__54040__auto___55776);

return statearr_55774;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___55776,out))
);


return out;
});

cljs.core.async.unique.cljs$lang$maxFixedArity = 2;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.partition = (function cljs$core$async$partition(var_args){
var G__55790 = arguments.length;
switch (G__55790) {
case 2:
return cljs.core.async.partition.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.partition.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.partition.cljs$core$IFn$_invoke$arity$2 = (function (n,ch){
return cljs.core.async.partition.call(null,n,ch,null);
});

cljs.core.async.partition.cljs$core$IFn$_invoke$arity$3 = (function (n,ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__54040__auto___55856 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___55856,out){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___55856,out){
return (function (state_55828){
var state_val_55829 = (state_55828[(1)]);
if((state_val_55829 === (7))){
var inst_55824 = (state_55828[(2)]);
var state_55828__$1 = state_55828;
var statearr_55830_55857 = state_55828__$1;
(statearr_55830_55857[(2)] = inst_55824);

(statearr_55830_55857[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55829 === (1))){
var inst_55791 = (new Array(n));
var inst_55792 = inst_55791;
var inst_55793 = (0);
var state_55828__$1 = (function (){var statearr_55831 = state_55828;
(statearr_55831[(7)] = inst_55792);

(statearr_55831[(8)] = inst_55793);

return statearr_55831;
})();
var statearr_55832_55858 = state_55828__$1;
(statearr_55832_55858[(2)] = null);

(statearr_55832_55858[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55829 === (4))){
var inst_55796 = (state_55828[(9)]);
var inst_55796__$1 = (state_55828[(2)]);
var inst_55797 = (inst_55796__$1 == null);
var inst_55798 = cljs.core.not.call(null,inst_55797);
var state_55828__$1 = (function (){var statearr_55833 = state_55828;
(statearr_55833[(9)] = inst_55796__$1);

return statearr_55833;
})();
if(inst_55798){
var statearr_55834_55859 = state_55828__$1;
(statearr_55834_55859[(1)] = (5));

} else {
var statearr_55835_55860 = state_55828__$1;
(statearr_55835_55860[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55829 === (15))){
var inst_55818 = (state_55828[(2)]);
var state_55828__$1 = state_55828;
var statearr_55836_55861 = state_55828__$1;
(statearr_55836_55861[(2)] = inst_55818);

(statearr_55836_55861[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55829 === (13))){
var state_55828__$1 = state_55828;
var statearr_55837_55862 = state_55828__$1;
(statearr_55837_55862[(2)] = null);

(statearr_55837_55862[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55829 === (6))){
var inst_55793 = (state_55828[(8)]);
var inst_55814 = (inst_55793 > (0));
var state_55828__$1 = state_55828;
if(cljs.core.truth_(inst_55814)){
var statearr_55838_55863 = state_55828__$1;
(statearr_55838_55863[(1)] = (12));

} else {
var statearr_55839_55864 = state_55828__$1;
(statearr_55839_55864[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55829 === (3))){
var inst_55826 = (state_55828[(2)]);
var state_55828__$1 = state_55828;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_55828__$1,inst_55826);
} else {
if((state_val_55829 === (12))){
var inst_55792 = (state_55828[(7)]);
var inst_55816 = cljs.core.vec.call(null,inst_55792);
var state_55828__$1 = state_55828;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_55828__$1,(15),out,inst_55816);
} else {
if((state_val_55829 === (2))){
var state_55828__$1 = state_55828;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_55828__$1,(4),ch);
} else {
if((state_val_55829 === (11))){
var inst_55808 = (state_55828[(2)]);
var inst_55809 = (new Array(n));
var inst_55792 = inst_55809;
var inst_55793 = (0);
var state_55828__$1 = (function (){var statearr_55840 = state_55828;
(statearr_55840[(10)] = inst_55808);

(statearr_55840[(7)] = inst_55792);

(statearr_55840[(8)] = inst_55793);

return statearr_55840;
})();
var statearr_55841_55865 = state_55828__$1;
(statearr_55841_55865[(2)] = null);

(statearr_55841_55865[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55829 === (9))){
var inst_55792 = (state_55828[(7)]);
var inst_55806 = cljs.core.vec.call(null,inst_55792);
var state_55828__$1 = state_55828;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_55828__$1,(11),out,inst_55806);
} else {
if((state_val_55829 === (5))){
var inst_55796 = (state_55828[(9)]);
var inst_55801 = (state_55828[(11)]);
var inst_55792 = (state_55828[(7)]);
var inst_55793 = (state_55828[(8)]);
var inst_55800 = (inst_55792[inst_55793] = inst_55796);
var inst_55801__$1 = (inst_55793 + (1));
var inst_55802 = (inst_55801__$1 < n);
var state_55828__$1 = (function (){var statearr_55842 = state_55828;
(statearr_55842[(11)] = inst_55801__$1);

(statearr_55842[(12)] = inst_55800);

return statearr_55842;
})();
if(cljs.core.truth_(inst_55802)){
var statearr_55843_55866 = state_55828__$1;
(statearr_55843_55866[(1)] = (8));

} else {
var statearr_55844_55867 = state_55828__$1;
(statearr_55844_55867[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55829 === (14))){
var inst_55821 = (state_55828[(2)]);
var inst_55822 = cljs.core.async.close_BANG_.call(null,out);
var state_55828__$1 = (function (){var statearr_55846 = state_55828;
(statearr_55846[(13)] = inst_55821);

return statearr_55846;
})();
var statearr_55847_55868 = state_55828__$1;
(statearr_55847_55868[(2)] = inst_55822);

(statearr_55847_55868[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55829 === (10))){
var inst_55812 = (state_55828[(2)]);
var state_55828__$1 = state_55828;
var statearr_55848_55869 = state_55828__$1;
(statearr_55848_55869[(2)] = inst_55812);

(statearr_55848_55869[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55829 === (8))){
var inst_55801 = (state_55828[(11)]);
var inst_55792 = (state_55828[(7)]);
var tmp55845 = inst_55792;
var inst_55792__$1 = tmp55845;
var inst_55793 = inst_55801;
var state_55828__$1 = (function (){var statearr_55849 = state_55828;
(statearr_55849[(7)] = inst_55792__$1);

(statearr_55849[(8)] = inst_55793);

return statearr_55849;
})();
var statearr_55850_55870 = state_55828__$1;
(statearr_55850_55870[(2)] = null);

(statearr_55850_55870[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
}
}
}
}
}
}
});})(c__54040__auto___55856,out))
;
return ((function (switch__53950__auto__,c__54040__auto___55856,out){
return (function() {
var cljs$core$async$state_machine__53951__auto__ = null;
var cljs$core$async$state_machine__53951__auto____0 = (function (){
var statearr_55851 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_55851[(0)] = cljs$core$async$state_machine__53951__auto__);

(statearr_55851[(1)] = (1));

return statearr_55851;
});
var cljs$core$async$state_machine__53951__auto____1 = (function (state_55828){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_55828);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e55852){if((e55852 instanceof Object)){
var ex__53954__auto__ = e55852;
var statearr_55853_55871 = state_55828;
(statearr_55853_55871[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_55828);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e55852;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__55872 = state_55828;
state_55828 = G__55872;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$state_machine__53951__auto__ = function(state_55828){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__53951__auto____1.call(this,state_55828);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__53951__auto____0;
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__53951__auto____1;
return cljs$core$async$state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___55856,out))
})();
var state__54042__auto__ = (function (){var statearr_55854 = f__54041__auto__.call(null);
(statearr_55854[(6)] = c__54040__auto___55856);

return statearr_55854;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___55856,out))
);


return out;
});

cljs.core.async.partition.cljs$lang$maxFixedArity = 3;

/**
 * Deprecated - this function will be removed. Use transducer instead
 */
cljs.core.async.partition_by = (function cljs$core$async$partition_by(var_args){
var G__55874 = arguments.length;
switch (G__55874) {
case 2:
return cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$2 = (function (f,ch){
return cljs.core.async.partition_by.call(null,f,ch,null);
});

cljs.core.async.partition_by.cljs$core$IFn$_invoke$arity$3 = (function (f,ch,buf_or_n){
var out = cljs.core.async.chan.call(null,buf_or_n);
var c__54040__auto___55944 = cljs.core.async.chan.call(null,(1));
cljs.core.async.impl.dispatch.run.call(null,((function (c__54040__auto___55944,out){
return (function (){
var f__54041__auto__ = (function (){var switch__53950__auto__ = ((function (c__54040__auto___55944,out){
return (function (state_55916){
var state_val_55917 = (state_55916[(1)]);
if((state_val_55917 === (7))){
var inst_55912 = (state_55916[(2)]);
var state_55916__$1 = state_55916;
var statearr_55918_55945 = state_55916__$1;
(statearr_55918_55945[(2)] = inst_55912);

(statearr_55918_55945[(1)] = (3));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55917 === (1))){
var inst_55875 = [];
var inst_55876 = inst_55875;
var inst_55877 = new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123);
var state_55916__$1 = (function (){var statearr_55919 = state_55916;
(statearr_55919[(7)] = inst_55877);

(statearr_55919[(8)] = inst_55876);

return statearr_55919;
})();
var statearr_55920_55946 = state_55916__$1;
(statearr_55920_55946[(2)] = null);

(statearr_55920_55946[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55917 === (4))){
var inst_55880 = (state_55916[(9)]);
var inst_55880__$1 = (state_55916[(2)]);
var inst_55881 = (inst_55880__$1 == null);
var inst_55882 = cljs.core.not.call(null,inst_55881);
var state_55916__$1 = (function (){var statearr_55921 = state_55916;
(statearr_55921[(9)] = inst_55880__$1);

return statearr_55921;
})();
if(inst_55882){
var statearr_55922_55947 = state_55916__$1;
(statearr_55922_55947[(1)] = (5));

} else {
var statearr_55923_55948 = state_55916__$1;
(statearr_55923_55948[(1)] = (6));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55917 === (15))){
var inst_55906 = (state_55916[(2)]);
var state_55916__$1 = state_55916;
var statearr_55924_55949 = state_55916__$1;
(statearr_55924_55949[(2)] = inst_55906);

(statearr_55924_55949[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55917 === (13))){
var state_55916__$1 = state_55916;
var statearr_55925_55950 = state_55916__$1;
(statearr_55925_55950[(2)] = null);

(statearr_55925_55950[(1)] = (14));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55917 === (6))){
var inst_55876 = (state_55916[(8)]);
var inst_55901 = inst_55876.length;
var inst_55902 = (inst_55901 > (0));
var state_55916__$1 = state_55916;
if(cljs.core.truth_(inst_55902)){
var statearr_55926_55951 = state_55916__$1;
(statearr_55926_55951[(1)] = (12));

} else {
var statearr_55927_55952 = state_55916__$1;
(statearr_55927_55952[(1)] = (13));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55917 === (3))){
var inst_55914 = (state_55916[(2)]);
var state_55916__$1 = state_55916;
return cljs.core.async.impl.ioc_helpers.return_chan.call(null,state_55916__$1,inst_55914);
} else {
if((state_val_55917 === (12))){
var inst_55876 = (state_55916[(8)]);
var inst_55904 = cljs.core.vec.call(null,inst_55876);
var state_55916__$1 = state_55916;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_55916__$1,(15),out,inst_55904);
} else {
if((state_val_55917 === (2))){
var state_55916__$1 = state_55916;
return cljs.core.async.impl.ioc_helpers.take_BANG_.call(null,state_55916__$1,(4),ch);
} else {
if((state_val_55917 === (11))){
var inst_55884 = (state_55916[(10)]);
var inst_55880 = (state_55916[(9)]);
var inst_55894 = (state_55916[(2)]);
var inst_55895 = [];
var inst_55896 = inst_55895.push(inst_55880);
var inst_55876 = inst_55895;
var inst_55877 = inst_55884;
var state_55916__$1 = (function (){var statearr_55928 = state_55916;
(statearr_55928[(7)] = inst_55877);

(statearr_55928[(11)] = inst_55896);

(statearr_55928[(8)] = inst_55876);

(statearr_55928[(12)] = inst_55894);

return statearr_55928;
})();
var statearr_55929_55953 = state_55916__$1;
(statearr_55929_55953[(2)] = null);

(statearr_55929_55953[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55917 === (9))){
var inst_55876 = (state_55916[(8)]);
var inst_55892 = cljs.core.vec.call(null,inst_55876);
var state_55916__$1 = state_55916;
return cljs.core.async.impl.ioc_helpers.put_BANG_.call(null,state_55916__$1,(11),out,inst_55892);
} else {
if((state_val_55917 === (5))){
var inst_55877 = (state_55916[(7)]);
var inst_55884 = (state_55916[(10)]);
var inst_55880 = (state_55916[(9)]);
var inst_55884__$1 = f.call(null,inst_55880);
var inst_55885 = cljs.core._EQ_.call(null,inst_55884__$1,inst_55877);
var inst_55886 = cljs.core.keyword_identical_QMARK_.call(null,inst_55877,new cljs.core.Keyword("cljs.core.async","nothing","cljs.core.async/nothing",-69252123));
var inst_55887 = (inst_55885) || (inst_55886);
var state_55916__$1 = (function (){var statearr_55930 = state_55916;
(statearr_55930[(10)] = inst_55884__$1);

return statearr_55930;
})();
if(cljs.core.truth_(inst_55887)){
var statearr_55931_55954 = state_55916__$1;
(statearr_55931_55954[(1)] = (8));

} else {
var statearr_55932_55955 = state_55916__$1;
(statearr_55932_55955[(1)] = (9));

}

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55917 === (14))){
var inst_55909 = (state_55916[(2)]);
var inst_55910 = cljs.core.async.close_BANG_.call(null,out);
var state_55916__$1 = (function (){var statearr_55934 = state_55916;
(statearr_55934[(13)] = inst_55909);

return statearr_55934;
})();
var statearr_55935_55956 = state_55916__$1;
(statearr_55935_55956[(2)] = inst_55910);

(statearr_55935_55956[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55917 === (10))){
var inst_55899 = (state_55916[(2)]);
var state_55916__$1 = state_55916;
var statearr_55936_55957 = state_55916__$1;
(statearr_55936_55957[(2)] = inst_55899);

(statearr_55936_55957[(1)] = (7));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
if((state_val_55917 === (8))){
var inst_55884 = (state_55916[(10)]);
var inst_55876 = (state_55916[(8)]);
var inst_55880 = (state_55916[(9)]);
var inst_55889 = inst_55876.push(inst_55880);
var tmp55933 = inst_55876;
var inst_55876__$1 = tmp55933;
var inst_55877 = inst_55884;
var state_55916__$1 = (function (){var statearr_55937 = state_55916;
(statearr_55937[(7)] = inst_55877);

(statearr_55937[(8)] = inst_55876__$1);

(statearr_55937[(14)] = inst_55889);

return statearr_55937;
})();
var statearr_55938_55958 = state_55916__$1;
(statearr_55938_55958[(2)] = null);

(statearr_55938_55958[(1)] = (2));


return new cljs.core.Keyword(null,"recur","recur",-437573268);
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
}
}
}
}
}
}
}
});})(c__54040__auto___55944,out))
;
return ((function (switch__53950__auto__,c__54040__auto___55944,out){
return (function() {
var cljs$core$async$state_machine__53951__auto__ = null;
var cljs$core$async$state_machine__53951__auto____0 = (function (){
var statearr_55939 = [null,null,null,null,null,null,null,null,null,null,null,null,null,null,null];
(statearr_55939[(0)] = cljs$core$async$state_machine__53951__auto__);

(statearr_55939[(1)] = (1));

return statearr_55939;
});
var cljs$core$async$state_machine__53951__auto____1 = (function (state_55916){
while(true){
var ret_value__53952__auto__ = (function (){try{while(true){
var result__53953__auto__ = switch__53950__auto__.call(null,state_55916);
if(cljs.core.keyword_identical_QMARK_.call(null,result__53953__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
continue;
} else {
return result__53953__auto__;
}
break;
}
}catch (e55940){if((e55940 instanceof Object)){
var ex__53954__auto__ = e55940;
var statearr_55941_55959 = state_55916;
(statearr_55941_55959[(5)] = ex__53954__auto__);


cljs.core.async.impl.ioc_helpers.process_exception.call(null,state_55916);

return new cljs.core.Keyword(null,"recur","recur",-437573268);
} else {
throw e55940;

}
}})();
if(cljs.core.keyword_identical_QMARK_.call(null,ret_value__53952__auto__,new cljs.core.Keyword(null,"recur","recur",-437573268))){
var G__55960 = state_55916;
state_55916 = G__55960;
continue;
} else {
return ret_value__53952__auto__;
}
break;
}
});
cljs$core$async$state_machine__53951__auto__ = function(state_55916){
switch(arguments.length){
case 0:
return cljs$core$async$state_machine__53951__auto____0.call(this);
case 1:
return cljs$core$async$state_machine__53951__auto____1.call(this,state_55916);
}
throw(new Error('Invalid arity: ' + (arguments.length - 1)));
};
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$0 = cljs$core$async$state_machine__53951__auto____0;
cljs$core$async$state_machine__53951__auto__.cljs$core$IFn$_invoke$arity$1 = cljs$core$async$state_machine__53951__auto____1;
return cljs$core$async$state_machine__53951__auto__;
})()
;})(switch__53950__auto__,c__54040__auto___55944,out))
})();
var state__54042__auto__ = (function (){var statearr_55942 = f__54041__auto__.call(null);
(statearr_55942[(6)] = c__54040__auto___55944);

return statearr_55942;
})();
return cljs.core.async.impl.ioc_helpers.run_state_machine_wrapped.call(null,state__54042__auto__);
});})(c__54040__auto___55944,out))
);


return out;
});

cljs.core.async.partition_by.cljs$lang$maxFixedArity = 3;


//# sourceMappingURL=async.js.map?rel=1510137298602
