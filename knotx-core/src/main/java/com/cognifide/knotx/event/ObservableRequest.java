/*
 * Knot.x - Reactive microservice assembler
 *
 * Copyright (C) 2016 Cognifide Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognifide.knotx.event;

import com.google.common.base.Stopwatch;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

public class ObservableRequest extends Observable {

    private final String requestUrl;

    private Stopwatch stopwatch;

    private DateFormat df = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss.SSS");

    public ObservableRequest(String requestUrl) {
        this.requestUrl = requestUrl;
        stopwatch = Stopwatch.createUnstarted();
    }

    public void onStart() {
        stopwatch.start();
        setChanged();
        notifyObservers("[" + df.format(new Date()) + "] request '" + requestUrl + "' started");
    }

    public void onFinish() {
        setChanged();
        notifyObservers("[" + df.format(new Date()) + "] request '" + requestUrl + "' finished in "
                + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + "ms");
    }

}
