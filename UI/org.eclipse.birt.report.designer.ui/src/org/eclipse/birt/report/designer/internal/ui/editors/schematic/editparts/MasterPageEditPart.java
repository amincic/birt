/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Actuate Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.birt.report.designer.core.model.views.outline.ReportElementModel;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.border.ReportDesignMarginBorder;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.figures.ReportElementFigure;
import org.eclipse.birt.report.designer.internal.ui.layout.MasterPageLayout;
import org.eclipse.birt.report.designer.util.ColorManager;
import org.eclipse.birt.report.model.activity.NotificationEvent;
import org.eclipse.birt.report.model.api.DesignElementHandle;
import org.eclipse.birt.report.model.api.MasterPageHandle;
import org.eclipse.birt.report.model.api.SimpleMasterPageHandle;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.AbstractEditPart;

/**
 * Master Page editor
 */
public class MasterPageEditPart extends ReportElementEditPart
{

	List children = new ArrayList( );

	/**
	 * Constructor
	 * 
	 * @param model
	 */
	public MasterPageEditPart( Object model )
	{
		super( model );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts.ReportElementEditPart#elementChanged(org.eclipse.birt.model.core.DesignElement,
	 *      org.eclipse.birt.model.activity.NotificationEvent)
	 */
	public void elementChanged( DesignElementHandle element,
			NotificationEvent ev )
	{
		switch ( ev.getEventType( ) )
		{
			case NotificationEvent.CONTENT_EVENT :
			case NotificationEvent.ELEMENT_DELETE_EVENT :
			case NotificationEvent.PROPERTY_EVENT :
			case NotificationEvent.STYLE_EVENT :
			{
				refresh( );
				//The children of master page edit part keep
				//virtual model
				//Those edit part will not get notification
				//refresh them explicit
				for ( Iterator it = getChildren( ).iterator( ); it.hasNext( ); )
				{
					( (AbstractEditPart) it.next( ) ).refresh( );
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts.ReportElementEditPart#createEditPolicies()
	 */
	protected void createEditPolicies( )
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure( )
	{
		Figure figure = new ReportElementFigure( );
		figure.setOpaque( true );

		figure.setBounds( new Rectangle( 0,
				0,
				getMasterPageSize( (MasterPageHandle) getModel( ) ).width - 1,
				getMasterPageSize( (MasterPageHandle) getModel( ) ).height - 1 ) );

		figure.setBorder( new ReportDesignMarginBorder( getMasterPageInsets( (MasterPageHandle) getModel( ) ) ) );

		figure.setLayoutManager( new MasterPageLayout( ) );

		return figure;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts.ReportElementEditPart#refreshFigure()
	 */
	public void refreshFigure( )
	{
		int color = getBackgroundColor( (MasterPageHandle) getModel( ) );
		getFigure( ).setBackgroundColor( ColorManager.getColor( color ) );

		getFigure( ).setBounds( new Rectangle( 0,
				0,
				getMasterPageSize( (MasterPageHandle) getModel( ) ).width - 1,
				getMasterPageSize( (MasterPageHandle) getModel( ) ).height - 1 ) );

		getFigure( ).setBorder( new ReportDesignMarginBorder( getMasterPageInsets( (MasterPageHandle) getModel( ) ) ) );

		refreshBackground( (MasterPageHandle) getModel( ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List getModelChildren( )
	{

		ReportElementModel model = new ReportElementModel( ( (SimpleMasterPageHandle) getModel( ) ).getPageHeader( ) );

		if ( !children.contains( model ) )
		{
			children.add( model );
		}

		model = new ReportElementModel( ( (SimpleMasterPageHandle) getModel( ) ).getPageFooter( ) );

		if ( !children.contains( model ) )
		{
			children.add( model );
		}

		return children;
	}
}