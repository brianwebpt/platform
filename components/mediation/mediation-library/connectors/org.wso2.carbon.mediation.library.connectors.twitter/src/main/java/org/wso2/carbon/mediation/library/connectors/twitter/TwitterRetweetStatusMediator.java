package org.wso2.carbon.mediation.library.connectors.twitter;

import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.mediators.AbstractMediator;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by IntelliJ IDEA.
 * User: charitha
 * Date: 2/17/12
 * Time: 5:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class TwitterRetweetStatusMediator extends AbstractMediator implements ManagedLifecycle {

    public static final String ID = "id";


    public void init(SynapseEnvironment synapseEnvironment) {

    }

    public void destroy() {
    }

    public boolean mediate(MessageContext messageContext) {
        try {
            String id = TwitterMediatorUtils.lookupFunctionParam(messageContext, ID);
            Twitter twitter = new TwitterClientLoader(messageContext).loadApiClient();
            Status status = twitter.retweetStatus(Long.parseLong(id));
            TwitterMediatorUtils.storeResponseStatus(messageContext, status);
            System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
        } catch (TwitterException te) {
            System.out.println("Failed to retweet status: " + te.getMessage());
            TwitterMediatorUtils.storeErrorResponseStatus(messageContext, te);
        }
        System.out.println("testing synapse twitter.......");
        return true;
    }

    public static void main(String[] args) {
        new TwitterRetweetStatusMediator().mediate(null);
    }


}
