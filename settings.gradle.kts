/*
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

rootProject.name = "knotx-root"
include("knotx-fragment-api")
include("knotx-server-http-api")
include("knotx-core")
include("knotx-launcher")
include("it-test")

include("knotx-knot-engine-api")
include("knotx-knot-engine-core")


project(":knotx-fragment-api").projectDir = file("fragment-api")
project(":knotx-server-http-api").projectDir = file("server-http/api")
project(":knotx-launcher").projectDir = file("launcher")

project(":knotx-knot-engine-api").projectDir = file("knot-engine/api")
project(":knotx-knot-engine-core").projectDir = file("knot-engine/core")
