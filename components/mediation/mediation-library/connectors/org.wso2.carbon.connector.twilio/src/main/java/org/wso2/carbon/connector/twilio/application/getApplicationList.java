/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.connector.twilio.application;

import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.SynapseLog;
import org.wso2.carbon.connector.twilio.util.TwilioUtil;
import org.wso2.carbon.mediation.library.connectors.core.AbstractConnector;
import org.wso2.carbon.mediation.library.connectors.core.util.ConnectorUtils;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestResponse;

/*
 * Class mediator for getting an application instance based on the
 * ApplicationSid parameter
 * For more information, see http://www.twilio.com/docs/api/rest/applications
 */
public class getApplicationList extends AbstractConnector {

	public void connect(MessageContext messageContext) {
		SynapseLog log = getLog(messageContext);
		log.auditLog("Start: get application list");
		String friendlyName =
		                      (String) ConnectorUtils.lookupTemplateParamater(messageContext,
		                                                                  TwilioUtil.PARAM_FRIENDLY_NAME);
		Map<String, String> param = new HashMap<String, String>();

		if (friendlyName != null) {
			param.put(TwilioUtil.TWILIO_FRIENDLY_NAME, friendlyName);
		}
		try {
			TwilioRestClient twilioRestClient = TwilioUtil.getTwilioRestClient(messageContext);
			TwilioRestResponse response =
			                              twilioRestClient.request(TwilioUtil.API_URL +
			                                                               "/" +
			                                                               TwilioUtil.API_VERSION +
			                                                               "/" +
			                                                               TwilioUtil.API_ACCOUNTS +
			                                                               "/" +
			                                                               twilioRestClient.getAccountSid() +
			                                                               "/" +
			                                                               TwilioUtil.API_APPLICATIONS,
			                                                       "GET", param);

			OMElement omResponse = TwilioUtil.parseResponse(response);
			TwilioUtil.preparePayload(messageContext, omResponse);
		} catch (Exception e) {
			log.error(e);
			throw new SynapseException(e);
		}
		log.auditLog("End: get application list");
	}
}
