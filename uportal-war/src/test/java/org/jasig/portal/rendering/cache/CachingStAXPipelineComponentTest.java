/**
 * Copyright (c) 2000-2010, Jasig, Inc.
 * See license distributed with this file and available online at
 * https://www.ja-sig.org/svn/jasig-parent/tags/rel-10/license-header.txt
 */

package org.jasig.portal.rendering.cache;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.jasig.portal.rendering.PipelineEventReader;
import org.jasig.portal.rendering.PipelineEventReaderImpl;
import org.jasig.portal.rendering.StAXPipelineComponent;
import org.jasig.portal.utils.cache.CacheKey;
import org.jasig.portal.xml.stream.XMLEventBufferReader;
import org.jasig.resource.aggr.om.Included;
import org.jasig.resource.aggr.util.ResourcesElementsProvider;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
public class CachingStAXPipelineComponentTest {
    @Test
    public void testCacheMiss() {
        final MockHttpServletRequest mockReq = new MockHttpServletRequest();
        final MockHttpServletResponse mockRes = new MockHttpServletResponse();
        final CacheKey cacheKey = new CacheKey("testCacheKey");
        final List<XMLEvent> eventBuffer = Collections.emptyList();
        final PipelineEventReader<XMLEventReader, XMLEvent> eventReader = new PipelineEventReaderImpl<XMLEventReader, XMLEvent>(new XMLEventBufferReader(eventBuffer.listIterator()));
        
        final Ehcache cache = createMock(Ehcache.class);
        final StAXPipelineComponent targetComponent = createMock(StAXPipelineComponent.class);
        final ResourcesElementsProvider elementsProvider = createMock(ResourcesElementsProvider.class);
        
        expect(elementsProvider.getDefaultIncludedType()).andReturn(Included.AGGREGATED);
        expect(targetComponent.getCacheKey(mockReq, mockRes)).andReturn(cacheKey);
        expect(cache.get(cacheKey)).andReturn(null);
        expect(targetComponent.getEventReader(mockReq, mockRes)).andReturn(eventReader);
        cache.put((Element)notNull());
        expectLastCall();
        
        replay(cache, targetComponent, elementsProvider);
        
        final CachingStAXPipelineComponent cachingComponent = new CachingStAXPipelineComponent();
        cachingComponent.setCache(cache);
        cachingComponent.setWrappedComponent(targetComponent);
        cachingComponent.setResourcesElementsProvider(elementsProvider);
        
        final PipelineEventReader<XMLEventReader, XMLEvent> actualEventReader = cachingComponent.getEventReader(mockReq, mockRes);

        Assert.assertNotNull(actualEventReader);
        Assert.assertNotNull(actualEventReader.getEventReader());
        Assert.assertFalse(actualEventReader.getEventReader().hasNext());
        
        verify(cache, targetComponent, elementsProvider);
    }
    
    @Test
    public void testCacheHit() {
        final MockHttpServletRequest mockReq = new MockHttpServletRequest();
        final MockHttpServletResponse mockRes = new MockHttpServletResponse();
        final CacheKey cacheKey = new CacheKey("testCacheKey");
        final List<XMLEvent> eventBuffer = Collections.emptyList();
        final Element cacheElement = new Element(cacheKey, eventBuffer);
        
        final Ehcache cache = createMock(Ehcache.class);
        final StAXPipelineComponent targetComponent = createMock(StAXPipelineComponent.class);
        final ResourcesElementsProvider elementsProvider = createMock(ResourcesElementsProvider.class);
        
        expect(elementsProvider.getDefaultIncludedType()).andReturn(Included.AGGREGATED);
        expect(targetComponent.getCacheKey(mockReq, mockRes)).andReturn(cacheKey);
        expect(cache.get(cacheKey)).andReturn(cacheElement);
        
        replay(cache, targetComponent, elementsProvider);
        
        final CachingStAXPipelineComponent cachingComponent = new CachingStAXPipelineComponent();
        cachingComponent.setCache(cache);
        cachingComponent.setWrappedComponent(targetComponent);
        cachingComponent.setResourcesElementsProvider(elementsProvider);
        
        final PipelineEventReader<XMLEventReader, XMLEvent> actualEventReader = cachingComponent.getEventReader(mockReq, mockRes);

        Assert.assertNotNull(actualEventReader);
        Assert.assertNotNull(actualEventReader.getEventReader());
        Assert.assertFalse(actualEventReader.getEventReader().hasNext());
        
        verify(cache, targetComponent, elementsProvider);
    }
}
