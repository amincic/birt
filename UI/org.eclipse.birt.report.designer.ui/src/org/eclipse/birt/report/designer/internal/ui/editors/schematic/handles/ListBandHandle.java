/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation .
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.designer.internal.ui.editors.schematic.handles;

import org.eclipse.birt.report.designer.internal.ui.editors.schematic.figures.ListBandFigure;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.MoveHandle;
import org.eclipse.gef.handles.MoveHandleLocator;

/**
 * Provides mouse action handle to ListBandEditPart.
 */
public class ListBandHandle extends MoveHandle
{

	/**
	 * @param owner
	 */
	public ListBandHandle( GraphicalEditPart owner )
	{
		super( owner, new ListBandLocator( owner.getFigure( ) ) );
		setBorder( new LineBorder( 2 ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.handles.AbstractHandle#createDragTracker()
	 */
	protected DragTracker createDragTracker( )
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean containsPoint( int x, int y )
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.draw2d.Figure#paintFigure(org.eclipse.draw2d.Graphics)
	 */
	protected void paintFigure( Graphics graphics )
	{
		super.paintFigure( graphics );
	}

	private static class ListBandLocator extends MoveHandleLocator
	{

		/**
		 * @param ref
		 */
		public ListBandLocator( IFigure ref )
		{
			super( ref );
			// TODO Auto-generated constructor stub
		}

		/*
		 * Sets the handle the bounds
		 * 
		 * @see org.eclipse.draw2d.Locator#relocate(org.eclipse.draw2d.IFigure)
		 */
		public void relocate( IFigure target )
		{
			Insets insets = target.getInsets( );
			Rectangle bounds;
			if ( getReference( ) instanceof ListBandFigure )
			{
				//bounds = ( (HandleBounds) getReference( ) ).getHandleBounds(
				// );
				ListBandFigure parent = (ListBandFigure) getReference( );
				Figure content = (Figure) parent.getContent( );
				bounds = content.getBounds( ).getCopy( );
			}
			else
				bounds = getReference( ).getBounds( );

			getReference( ).translateToAbsolute( bounds );
			target.translateToRelative( bounds );

			target.setBounds( bounds );
		}

	}
}