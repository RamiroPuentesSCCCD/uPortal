/* Copyright 2004 The JA-SIG Collaborative.  All rights reserved.
*  See license distributed with this file and
*  available online at http://www.uportal.org/license.html
*/

package org.jasig.portal.services.persondir.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.jasig.portal.ldap.LdapServerImpl;

/**
 * Testcase for LdapPersonAttributeDao.
 * @author andrew.petro@yale.edu
 * @version $Revision$ $Date$
 */
public class LdapPersonAttributeDaoImplTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Testcase against Yale University LDAP.
     * 
     * This testcase will cease to work on that fateful day when Andrew
     * no longer appears in Yale University LDAP.
     */
    public void testYaleAttributesForUser() {
        LdapPersonAttributeDaoImpl impl = new LdapPersonAttributeDaoImpl();
        
        Map ldapAttribsToPortalAttribs = new HashMap();
        ldapAttribsToPortalAttribs.put("mail", "email");
        
        impl.setLdapAttributesToPortalAttributes(ldapAttribsToPortalAttribs);
        
        LdapServerImpl ldapServer = new LdapServerImpl("name", "ldap://mrfrumble.its.yale.edu:389/o=yale.edu", null, "uid", null, null, null);
        
        impl.setLdapServer(ldapServer);
        
        impl.setUidQuery("(uid={0})");
        
        Map attribs = impl.attributesForUser("awp9");
        assertEquals("andrew.petro@yale.edu", attribs.get("email"));
    }
    
    /**
     * Test proper reporting of declared attribute names.
     */
    public void testAttributeNames() {
        LdapPersonAttributeDaoImpl impl = new LdapPersonAttributeDaoImpl();
        
        Map ldapAttribsToPortalAttribs = new HashMap();
        ldapAttribsToPortalAttribs.put("mail", "email");
        ldapAttribsToPortalAttribs.put("shirtColor", "dressShirtColor");
        
        Set surNameAttributeNames = new HashSet();
        surNameAttributeNames.add("surName");
        surNameAttributeNames.add("lastName");
        surNameAttributeNames.add("familyName");
        surNameAttributeNames.add("thirdName");
        ldapAttribsToPortalAttribs.put("lastName", surNameAttributeNames);
        
        impl.setLdapAttributesToPortalAttributes(ldapAttribsToPortalAttribs);
        
        Set expectedAttributeNames = new HashSet();
        expectedAttributeNames.addAll(surNameAttributeNames);
        expectedAttributeNames.add("email");
        expectedAttributeNames.add("dressShirtColor");
        
        assertEquals(expectedAttributeNames, impl.getAttributeNames());
    }
    
}