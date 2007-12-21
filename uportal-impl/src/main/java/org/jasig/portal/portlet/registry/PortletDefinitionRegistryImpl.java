/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package org.jasig.portal.portlet.registry;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.ChannelDefinition;
import org.jasig.portal.ChannelParameter;
import org.jasig.portal.ChannelRegistryStoreFactory;
import org.jasig.portal.IChannelRegistryStore;
import org.jasig.portal.channels.portlet.IPortletAdaptor;
import org.jasig.portal.portlet.dao.IPortletDefinitionDao;
import org.jasig.portal.portlet.om.IPortletDefinition;
import org.jasig.portal.portlet.om.IPortletDefinitionId;
import org.springframework.beans.factory.annotation.Required;

/**
 * Implementation of the definition registry, pulls together the related parts of the framework for creation and access
 * of {@link IPortletDefinition}s.
 * 
 * TODO this needs to listen for channel deletion events and remove the corresponding portlet definition, this would likley need a hook in ChannelRegistryManager.removeChannel
 * TODO should this class be using ChannelRegistryManager instead of ChannelRegistryStore to get ChannelDefintion objects?
 * 
 * @author Eric Dalquist
 * @version $Revision$
 */
public class PortletDefinitionRegistryImpl implements IPortletDefinitionRegistry {
    protected final Log logger = LogFactory.getLog(this.getClass());
    
    private IPortletDefinitionDao portletDefinitionDao;
    
    /**
     * @return the portletDefinitionDao
     */
    public IPortletDefinitionDao getPortletDefinitionDao() {
        return this.portletDefinitionDao;
    }
    /**
     * @param portletDefinitionDao the portletDefinitionDao to set
     */
    @Required
    public void setPortletDefinitionDao(IPortletDefinitionDao portletDefinitionDao) {
        this.portletDefinitionDao = portletDefinitionDao;
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.portlet.registry.IPortletDefinitionRegistry#createPortletDefinition(int)
     */
    public IPortletDefinition createPortletDefinition(int channelPublishId) {
        //Lookup the ChannelDefinition
        final IChannelRegistryStore channelRegistryStore = ChannelRegistryStoreFactory.getChannelRegistryStoreImpl();
        final ChannelDefinition channelDefinition;
        try {
            channelDefinition = channelRegistryStore.getChannelDefinition(channelPublishId);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Failed to retrieve required ChannelDefinition for channelPublishId: " + channelPublishId, e);
        }
        
        //Grab the parameters that describe the pluto descriptor objects
        final ChannelParameter portletApplicationIdParam = channelDefinition.getParameter(IPortletAdaptor.CHANNEL_PARAM__PORTLET_APPLICATION_ID);
        if (portletApplicationIdParam == null) {
            throw new IllegalArgumentException("No portletApplicationId available under ChannelParameter '" + IPortletAdaptor.CHANNEL_PARAM__PORTLET_APPLICATION_ID + "' for channelId:" + channelPublishId);
        }
        final String portletApplicationId = portletApplicationIdParam.getValue();
        
        final ChannelParameter portletNameParam = channelDefinition.getParameter(IPortletAdaptor.CHANNEL_PARAM__PORTLET_NAME);
        if (portletNameParam == null) {
            throw new IllegalArgumentException("No portletName available under ChannelParameter '" + IPortletAdaptor.CHANNEL_PARAM__PORTLET_NAME + "' for channelId:" + channelPublishId);
        }
        final String portletName = portletNameParam.getValue();

        return this.createPortletDefinition(channelPublishId, portletApplicationId, portletName);
    }
    
    /* (non-Javadoc)
     * @see org.jasig.portal.portlet.registry.IPortletDefinitionRegistry#createPortletDefinition(int, java.lang.String, java.lang.String)
     */
    public IPortletDefinition createPortletDefinition(int channelPublishId, String portletApplicationId, String portletName) {
        Validate.notNull(portletApplicationId, "portletApplicationId can not be null");
        Validate.notNull(portletName, "portletName can not be null");
        
        //Create and return the defintion
        return this.portletDefinitionDao.createPortletDefinition(channelPublishId, portletApplicationId, portletName);
    }
    
    /* (non-Javadoc)
     * @see org.jasig.portal.portlet.registry.IPortletDefinitionRegistry#getPortletDefinition(int)
     */
    public IPortletDefinition getPortletDefinition(int channelPublishId) {
        return this.portletDefinitionDao.getPortletDefinition(channelPublishId);
    }

    /* (non-Javadoc)
     * @see org.jasig.portal.portlet.registry.IPortletDefinitionRegistry#getOrCreatePortletDefinition(int)
     */
    public IPortletDefinition getOrCreatePortletDefinition(int channelPublishId) {
        final IPortletDefinition portletDefinition = this.getPortletDefinition(channelPublishId);
        if (portletDefinition != null) {
            return portletDefinition;
        }
        
        return this.createPortletDefinition(channelPublishId);
    }
    
    /* (non-Javadoc)
     * @see org.jasig.portal.portlet.registry.IPortletDefinitionRegistry#getOrCreatePortletDefinition(int, java.lang.String, java.lang.String)
     */
    public IPortletDefinition getOrCreatePortletDefinition(int channelPublishId, String portletApplicationId, String portletName) {
        final IPortletDefinition portletDefinition = this.getPortletDefinition(channelPublishId);
        if (portletDefinition != null) {
            return portletDefinition;
        }
        
        return this.createPortletDefinition(channelPublishId, portletApplicationId, portletName);
    }
    
    
    /* (non-Javadoc)
     * @see org.jasig.portal.portlet.registry.IPortletDefinitionRegistry#getPortletDefinition(org.jasig.portal.portlet.om.IPortletDefinitionId)
     */
    public IPortletDefinition getPortletDefinition(IPortletDefinitionId portletDefinitionId) {
        Validate.notNull(portletDefinitionId, "portletDefinitionId can not be null");
        
        return this.portletDefinitionDao.getPortletDefinition(portletDefinitionId);
    }
    /* (non-Javadoc)
     * @see org.jasig.portal.portlet.registry.IPortletDefinitionRegistry#updatePortletDefinition(org.jasig.portal.portlet.om.IPortletDefinition)
     */
    public void updatePortletDefinition(IPortletDefinition portletDefinition) {
        Validate.notNull(portletDefinition, "portletDefinition can not be null");
        
        this.portletDefinitionDao.updatePortletDefinition(portletDefinition);
    }
}
