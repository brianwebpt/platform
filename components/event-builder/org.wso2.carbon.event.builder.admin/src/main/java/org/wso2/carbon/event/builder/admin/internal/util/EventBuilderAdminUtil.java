/*
 * Copyright (c) 2005-2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.event.builder.admin.internal.util;

import org.wso2.carbon.event.builder.core.internal.util.DeploymentStatus;

import java.util.Map;

public class EventBuilderAdminUtil {
    public static DeploymentStatus getDeploymentStatusForString(String deploymentStatus) {
        for (Map.Entry<DeploymentStatus, String> depStatusEntry : EventBuilderAdminConstants.DEP_STATUS_MAP.entrySet()) {
            if (depStatusEntry.getValue().equals(deploymentStatus)) {
                return depStatusEntry.getKey();
            }
        }

        return null;
    }

    public static String splitCamelCase(String s) {
        // Code from http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-into-human-readable-names-in-java
        return s.replaceAll(
                String.format("%s|%s|%s",
                              "(?<=[A-Z])(?=[A-Z][a-z])",
                              "(?<=[^A-Z])(?=[A-Z])",
                              "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }
}
