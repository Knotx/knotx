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
include("knotx-splitter-html")
include("knotx-assembler")
include("knotx-repository-connector-http")
include("knotx-repository-connector-fs")
include("it-test")

project(":knotx-fragment-api").projectDir = file("fragment-api")
project(":knotx-server-http-api").projectDir = file("server-http/api")
project(":knotx-launcher").projectDir = file("launcher")
project(":knotx-splitter-html").projectDir = file("splitter-html")
project(":knotx-assembler").projectDir = file("assembler")
project(":knotx-repository-connector-http").projectDir = file("repository/connector-http")
project(":knotx-repository-connector-fs").projectDir = file("repository/connector-fs")
