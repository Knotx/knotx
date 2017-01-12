/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/** @module knotx-eb-api-js/adapter_proxy */
var utils = require('vertx-js/util/utils');
var Vertx = require('vertx-js/vertx');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JAdapterProxy = com.cognifide.knotx.proxy.AdapterProxy;
var AdapterRequest = com.cognifide.knotx.dataobjects.AdapterRequest;
var AdapterResponse = com.cognifide.knotx.dataobjects.AdapterResponse;

/**
 @class
*/
var AdapterProxy = function(j_val) {

  var j_adapterProxy = j_val;
  var that = this;

  /**

   @public
   @param request {Object} 
   @param result {function} 
   */
  this.process = function(request, result) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_adapterProxy["process(com.cognifide.knotx.dataobjects.AdapterRequest,io.vertx.core.Handler)"](request != null ? new AdapterRequest(new JsonObject(JSON.stringify(request))) : null, function(ar) {
      if (ar.succeeded()) {
        result(utils.convReturnDataObject(ar.result()), null);
      } else {
        result(null, ar.cause());
      }
    });
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_adapterProxy;
};

/**

 @memberof module:knotx-eb-api-js/adapter_proxy
 @param vertx {Vertx} 
 @param address {string} 
 @return {AdapterProxy}
 */
AdapterProxy.createProxy = function(vertx, address) {
  var __args = arguments;
  if (__args.length === 2 && typeof __args[0] === 'object' && __args[0]._jdel && typeof __args[1] === 'string') {
    return utils.convReturnVertxGen(JAdapterProxy["createProxy(io.vertx.core.Vertx,java.lang.String)"](vertx._jdel, address), AdapterProxy);
  } else throw new TypeError('function invoked with invalid arguments');
};

// We export the Constructor function
module.exports = AdapterProxy;