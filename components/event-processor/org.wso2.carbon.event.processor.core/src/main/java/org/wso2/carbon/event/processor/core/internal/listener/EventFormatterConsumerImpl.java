/**
 * Copyright (c) 2005 - 2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.event.processor.core.internal.listener;

import org.apache.log4j.Logger;
import org.wso2.carbon.event.formatter.core.EventFormatterListener;
import org.wso2.carbon.event.processor.core.internal.stream.EventConsumer;
import org.wso2.carbon.event.processor.core.internal.util.EventProcessorConstants;
import org.wso2.siddhi.core.event.Event;

import java.util.Arrays;

public class EventFormatterConsumerImpl implements EventConsumer {

    private Logger trace = Logger.getLogger(EventProcessorConstants.EVENT_TRACE_LOGGER);

    private EventFormatterListener formatterListener;
    private Object owner;

    public EventFormatterConsumerImpl(EventFormatterListener formatterListener, Object owner) {
        this.formatterListener = formatterListener;
        this.owner = owner;
    }

    @Override
    public void consumeEvents(Object[] events) {
        trace.info("Dispatching events to the event formatter. Events: " + Arrays.toString(events));
        for (Object event : events) {
            formatterListener.onEvent(event);
        }
    }

    @Override
    public void consumeEvents(Event[] events) {
        trace.info("Dispatching events to the event formatter. Events: " + Arrays.toString(events));
        for (Event event : events) {
            formatterListener.onEvent(event.getData());
        }
    }

    @Override
    public Object getOwner() {
        return owner;
    }

    public EventFormatterListener getFormatterListener() {
        return formatterListener;
    }
}
