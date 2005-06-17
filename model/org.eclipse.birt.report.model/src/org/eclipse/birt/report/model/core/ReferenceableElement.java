/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.model.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.birt.report.model.api.activity.NotificationEvent;
import org.eclipse.birt.report.model.elements.ReportDesign;
import org.eclipse.birt.report.model.metadata.ElementPropertyDefn;

/**
 * Represents an element that can be referenced using an element reference. This
 * element maintains a cached set of back-references to the "clients" so that
 * changes can be automatically propagated.
 *  
 */

public abstract class ReferenceableElement extends DesignElement implements IReferencable
{

	/**
	 * The list of cached clients.
	 */

	protected ArrayList clients = new ArrayList( );

	/**
	 * Default constructor.
	 */

	public ReferenceableElement( )
	{
	}

	/**
	 * Constructs the ReferenceableElement with the element name.
	 * 
	 * @param theName
	 *            the element name
	 */

	public ReferenceableElement( String theName )
	{
		super( theName );
	}

	/**
	 * Makes a clone of this referencable element. The cloned element has an
	 * empty client list,which is used to point to the clients who reference
	 * this element.
	 * 
	 * @return Object the cloned referencable element.
	 * 
	 * 
	 * @see java.lang.Object#clone()
	 */
	public Object clone( ) throws CloneNotSupportedException
	{
		ReferenceableElement element = (ReferenceableElement) super.clone( );
		element.clients = new ArrayList( );
		return element;
	}

	/**
	 * Adds a client. Should be called only from
	 * {@link DesignElement#setProperty( ElementPropertyDefn, Object )}.
	 * 
	 * @param client
	 *            The client to add.
	 * @param propName
	 *            the property name.
	 */

	public void addClient( DesignElement client, String propName )
	{
		clients.add( new BackRef( client, propName ) );
	}

	/**
	 * Drops a client. Should be called only from
	 * {@link DesignElement#setProperty( ElementPropertyDefn, Object )}.
	 * 
	 * @param client
	 *            The client to drop.
	 */

	public void dropClient( DesignElement client )
	{
		for ( int i = 0; i < clients.size( ); i++ )
		{
			if ( ( (BackRef) clients.get( i ) ).element == client )
			{
				clients.remove( i );
				return;
			}
		}
		assert false;
	}

	/**
	 * Returns the list of clients for this element.
	 * 
	 * @return The list of clients.
	 */

	public List getClientList( )
	{
		return clients;
	}

	/**
	 * Checks if this element is referenced by others.
	 * 
	 * @return true if it has client, otherwise return false.
	 *  
	 */
	public boolean hasReferences( )
	{
		return !clients.isEmpty( );
	}

	/**
	 * Sends the event to all clients in addition to the routing for a design
	 * element.
	 * 
	 * @param ev
	 *            the event to send
	 * @param design
	 *            the root node of the design tree.
	 */

	public void broadcast( NotificationEvent ev, ReportDesign design )
	{
		super.broadcast( ev, design );

		adjustDeliveryPath( ev );
		broadcastToClients( ev, design );
	}

	/**
	 * Sets the path type for the notification event.
	 * 
	 * @param ev
	 *            the notification event
	 */

	abstract protected void adjustDeliveryPath( NotificationEvent ev );

	/**
	 * Broadcasts the event to clients.
	 * 
	 * @param ev
	 *            the event to broadcast
	 * @param design
	 *            the report design
	 */

	protected void broadcastToClients( NotificationEvent ev, ReportDesign design )
	{
		for ( int i = 0; i < clients.size( ); i++ )
		{
			( (BackRef) clients.get( i ) ).element.broadcast( ev, design );
		}
	}
}