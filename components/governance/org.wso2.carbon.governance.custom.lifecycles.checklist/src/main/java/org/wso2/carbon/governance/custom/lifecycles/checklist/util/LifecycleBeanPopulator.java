/*
 * Copyright (c) 2006, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.governance.custom.lifecycles.checklist.util;

import org.wso2.carbon.governance.custom.lifecycles.checklist.beans.LifecycleBean;
import org.wso2.carbon.registry.core.*;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.exceptions.ResourceNotFoundException;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.registry.common.utils.UserUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.registry.core.utils.UUIDGenerator;
import org.wso2.carbon.user.core.UserRealm;

import java.util.*;

public class LifecycleBeanPopulator {

    private static final Log log = LogFactory.getLog(LifecycleBeanPopulator.class);

    private static Map<String, Boolean> lifecycleAspects = new HashMap<String, Boolean>();

    public static LifecycleBean getLifecycleBean(String path, UserRegistry registry,
                                                 Registry systemRegistry) throws Exception {

        LifecycleBean lifecycleBean;
        ResourcePath resourcePath = new ResourcePath(path);
        UserRealm userRealm;

        try {
            Resource resource = registry.get(path);
            if (resource != null) {
                lifecycleBean = new LifecycleBean();
                lifecycleBean.setMediaType(resource.getMediaType());

                if (resource.getProperty("registry.link") != null &&
                        resource.getProperty("registry.mount") == null) {
                    lifecycleBean.setLink(true);
                    return lifecycleBean;
                }
                if (resource.getProperty("registry.mount") != null) {
                    lifecycleBean.setMounted(true);
                }
                List<String> aspects = resource.getAspects();

                if (aspects != null) {
                    LifecycleActions[] actions = new LifecycleActions[aspects.size()];

                    for (int i = 0; i < actions.length; i++) {
                        String aspect = aspects.get(i);

                        String[] aspectActions = registry.getAspectActions(resourcePath.getPath(), aspect);
                        if (aspectActions == null) continue;

                        LifecycleActions lifecycleActionsEntry = new LifecycleActions();
                        lifecycleActionsEntry.setLifecycle(aspect);
                        lifecycleActionsEntry.setActions(aspectActions);
                        actions[i] = lifecycleActionsEntry;
                    }

                    lifecycleBean.setAvailableActions(actions);
                }

                String[] aspectsToAdd = registry.getAvailableAspects();

                List<String> lifecycleAspectsToAdd = new LinkedList<String>();
                if (aspectsToAdd != null) {
                    boolean isTransactionStarted = false;
                    String tempResourcePath = "/governance/lcm/" + UUIDGenerator.generateUUID();
                    for (String aspectToAdd : aspectsToAdd) {
                        if (systemRegistry.getRegistryContext().isReadOnly()) {
                            lifecycleAspectsToAdd.add(aspectToAdd);
                            continue;
                        }
                        Boolean isLifecycleAspect = lifecycleAspects.get(aspectToAdd);
                        if (isLifecycleAspect == null) {
                            if (!isTransactionStarted) {
                                systemRegistry.beginTransaction();
                                isTransactionStarted = true;
                            }
                            systemRegistry.put(tempResourcePath, systemRegistry.newResource());
                            systemRegistry.associateAspect(tempResourcePath, aspectToAdd);
                            Resource r  = systemRegistry.get(tempResourcePath);
                            Properties props = r.getProperties();
                            Set keys  = props.keySet();
                            for (Object key : keys) {
                                String propKey = (String) key;
                                if (propKey.startsWith("registry.lifecycle.")
                                        || propKey.startsWith("registry.custom_lifecycle.checklist.")) {
                                    isLifecycleAspect = Boolean.TRUE;
                                    break;
                                }
                            }
                            if (isLifecycleAspect == null) {
                                isLifecycleAspect = Boolean.FALSE;
                            }
                            lifecycleAspects.put(aspectToAdd, isLifecycleAspect);
                        }
                        if (isLifecycleAspect) {
                            lifecycleAspectsToAdd.add(aspectToAdd);
                        }
                    }
                    if (isTransactionStarted) {
                        systemRegistry.delete(tempResourcePath);
                        systemRegistry.rollbackTransaction();
                    }
                }
                lifecycleBean.setAspectsToAdd(lifecycleAspectsToAdd.toArray(
                        new String[lifecycleAspectsToAdd.size()]));

                resource = registry.get(path);
                Properties props = resource.getProperties();
                List<Property> propList = new ArrayList<Property>();
                Iterator iKeys = props.keySet().iterator();
                while (iKeys.hasNext()) {
                    String propKey = (String) iKeys.next();

                    if (propKey.startsWith("registry.lifecycle.")
//                            || propKey.equals(Aspect.AVAILABLE_ASPECTS)
                            || propKey.startsWith("registry.custom_lifecycle.checklist.") ){
//                            || propKey.startsWith("registry.custom_lifecycle.js.")) {
                        Property property = new Property();
                        property.setKey(propKey);
                        List<String> propValues = (List<String>) props.get(propKey);
                        property.setValues(propValues.toArray(new String[propValues.size()]));
                        propList.add(property);
                    }
                }

                lifecycleBean.setLifecycleProperties(propList.toArray(new Property[propList.size()]));

                lifecycleBean.setPathWithVersion(resourcePath.getPathWithVersion());
                lifecycleBean.setVersionView(!resourcePath.isCurrentVersion());
                lifecycleBean.setPutAllowed(
                        UserUtil.isPutAllowed(registry.getUserName(), resourcePath.getPath(), registry));
                lifecycleBean.setLoggedIn(!RegistryConstants.ANONYMOUS_USER.equals(registry.getUserName()));
                lifecycleBean.setShowAddDelete(true);

//                This is to add the roles of the current user. We are using this to enable disable actions in UI
                userRealm = registry.getUserRealm();
                String[] rolesList = userRealm.getUserStoreManager().getRoleListOfUser(registry.getUserName());
                lifecycleBean.setRolesOfUser(rolesList);

                resource.discard();
            }
            else {
                lifecycleBean = null;
            }
        }
        catch (ResourceNotFoundException rnfe) {
            lifecycleBean = null;  
        }
        catch (RegistryException e) {
            String msg = "Failed to get life cycle information of resource " +
                    resourcePath + ". " + e.getMessage();
            log.error(msg, e);
            throw new Exception(msg, e);
        }

        return lifecycleBean;
    }
}
