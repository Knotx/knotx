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

/** @module knotx-core-common-js/fragment_assembler_service */
var utils = require('vertx-js/util/utils');
var Vertx = require('vertx-js/vertx');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JFragmentAssemblerService = io.knotx.knot.assembler.FragmentAssemblerService;
var KnotContext = io.knotx.dataobjects.KnotContext;

/**
 @class
*/
var FragmentAssemblerService = function(j_val) {

  var j_fragmentAssemblerService = j_val;
  var that = this;

  /**

   @public
   @param knotContext {Object}
   @param result {function}
   */
  this.process = function(knotContext, result) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_fragmentAssemblerService["process(io.knotx.dataobjects.KnotContext,io.vertx.core.Handler)"](knotContext != null ? new KnotContext(new JsonObject(JSON.stringify(knotContext))) : null, function(ar) {
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
  this._jdel = j_fragmentAssemblerService;
};

/**

 @memberof module:knotx-core-common-js/fragment_assembler_service
 @param vertx {Vertx}
 @return {FragmentAssemblerService}
 */
FragmentAssemblerService.create = function(vertx) {
  var __args = arguments;
  if (__args.length === 1 && typeof __args[0] === 'object' && __args[0]._jdel) {
    return utils.convReturnVertxGen(JFragmentAssemblerService["create(io.vertx.core.Vertx)"](vertx._jdel), FragmentAssemblerService);
  } else throw new TypeError('function invoked with invalid arguments');
};

/**

 @memberof module:knotx-core-common-js/fragment_assembler_service
 @param vertx {Vertx}
 @param address {string}
 @return {FragmentAssemblerService}
 */
FragmentAssemblerService.createProxy = function(vertx, address) {
  var __args = arguments;
  if (__args.length === 2 && typeof __args[0] === 'object' && __args[0]._jdel && typeof __args[1] === 'string') {
    return utils.convReturnVertxGen(JFragmentAssemblerService["createProxy(io.vertx.core.Vertx,java.lang.String)"](vertx._jdel, address), FragmentAssemblerService);
  } else throw new TypeError('function invoked with invalid arguments');
};

// We export the Constructor function
module.exports = FragmentAssemblerService;
