/*
 * Copyright 2004,2005 The Apache Software Foundation.
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

package org.wso2.carbon.output.transport.adaptor.sms.internal.ds;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.output.transport.adaptor.core.OutputTransportAdaptorFactory;
import org.wso2.carbon.output.transport.adaptor.sms.SMSTransportAdaptorFactory;
import org.wso2.carbon.utils.ConfigurationContextService;


/**
 * @scr.component name="output.sms.TransportAdaptorService.component" immediate="true"
 * @scr.reference name="configurationcontext.service"
 * interface="org.wso2.carbon.utils.ConfigurationContextService" cardinality="1..1"
 * policy="dynamic" bind="setConfigurationContextService" unbind="unsetConfigurationContextService"
 */


public class SMSTransportAdaptorServiceDS {

    private static final Log log = LogFactory.getLog(SMSTransportAdaptorServiceDS.class);

    /**
     * initialize the sms service here service here.
     *
     * @param context
     */
    protected void activate(ComponentContext context) {

        try {
            OutputTransportAdaptorFactory smsTransportAdaptorFactory = new SMSTransportAdaptorFactory();
            context.getBundleContext().registerService(OutputTransportAdaptorFactory.class.getName(), smsTransportAdaptorFactory, null);
            log.info("Successfully deployed the output SMS transport adaptor service");
        } catch (RuntimeException e) {
            log.error("Can not create the output SMS transport adaptor service ", e);
        }
    }

    protected void setConfigurationContextService(
            ConfigurationContextService configurationContextService) {
        SMSTransportAdaptorServiceValueHolder.registerConfigurationContextService(configurationContextService);
    }

    protected void unsetConfigurationContextService(
            ConfigurationContextService configurationContextService) {

    }


}
