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

package org.eclipse.birt.report.item.crosstab.internal.ui.editors.editparts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.birt.report.designer.internal.ui.editors.parts.ISelectionFlitter;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.border.CellBorder;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts.AbstractCellEditPart;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts.ReportElementEditPart;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.editpolicies.ReportComponentEditPolicy;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.editpolicies.ReportContainerEditPolicy;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.figures.CellFigure;
import org.eclipse.birt.report.designer.internal.ui.editors.schematic.tools.CellDragTracker;
import org.eclipse.birt.report.designer.internal.ui.layout.ReportFlowLayout;
import org.eclipse.birt.report.item.crosstab.internal.ui.editors.editpolicies.CrosstabCellFlowLayoutEditPolicy;
import org.eclipse.birt.report.item.crosstab.internal.ui.editors.figures.CrosstabCellFigure;
import org.eclipse.birt.report.item.crosstab.internal.ui.editors.handles.CrosstavCellDragHandle;
import org.eclipse.birt.report.item.crosstab.internal.ui.editors.model.CrosstabCellAdapter;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;

/**
 * Crosstab cell element editpart, the model is CrosstabCellAdapter
 */

// TODO outlne drag the dimensionHandle
public class CrosstabCellEditPart extends AbstractCellEditPart
{

	/**
	 * The all drag column and row handle
	 */
	private List handles = null;

	/**
	 * Constructor
	 * 
	 * @param model
	 */
	public CrosstabCellEditPart( Object model )
	{
		super( model );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	protected List getModelChildren( )
	{
		return getCrosstabCellAdapter( ).getModelList( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts.ReportElementEditPart#createEditPolicies()
	 */
	protected void createEditPolicies( )
	{
		installEditPolicy( EditPolicy.COMPONENT_ROLE,
				new ReportComponentEditPolicy( ) );
		installEditPolicy( EditPolicy.LAYOUT_ROLE,
				new CrosstabCellFlowLayoutEditPolicy( ) );
		installEditPolicy( EditPolicy.CONTAINER_ROLE,
				new ReportContainerEditPolicy( ) );

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts.ReportElementEditPart#refreshFigure()
	 */
	// TODO now only fresh the border and the background.
	public void refreshFigure( )
	{
		CellBorder cborder = new CellBorder( );

		if ( getFigure( ).getBorder( ) instanceof CellBorder )
		{
			cborder.setBorderInsets( ( (CellBorder) getFigure( ).getBorder( ) ).getBorderInsets( ) );
		}

		refreshBorder( getCrosstabCellAdapter( ).getDesignElementHandle( ),
				cborder );
		refreshBackground( getCrosstabCellAdapter( ).getDesignElementHandle( ) );

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts.ReportElementEditPart#activate()
	 */
	public void activate( )
	{
		if ( handles == null )
		{
			handles = getHandleList( );
		}
		// IFigure layer = getLayer( CrosstabTableEditPart.CELL_HANDLE_LAYER );
		IFigure layer = getLayer( LayerConstants.HANDLE_LAYER );
		int size = handles.size( );
		for ( int i = 0; i < size; i++ )
		{
			Figure handle = (Figure) handles.get( i );
			layer.add( handle );
		}
		super.activate( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts.ReportElementEditPart#deactivate()
	 */
	public void deactivate( )
	{
		// IFigure layer = getLayer( CrosstabTableEditPart.CELL_HANDLE_LAYER );
		IFigure layer = getLayer( LayerConstants.HANDLE_LAYER );
		int size = handles.size( );
		for ( int i = 0; i < size; i++ )
		{
			Figure handle = (Figure) handles.get( i );
			layer.remove( handle );
		}
		super.deactivate( );
	}

	/**
	 * Gets the column and rwo drag handle
	 * 
	 * @return
	 */
	protected List getHandleList( )
	{
		List retValue = new ArrayList( );
		CrosstabTableEditPart parent = (CrosstabTableEditPart) getParent( );

		int columnNumner = parent.getColumnCount( );
		int rowNumer = parent.getRowCount( );
		if ( getColumnNumber( ) + getColSpan( ) - 1 < columnNumner )
		{
			CrosstavCellDragHandle column = new CrosstavCellDragHandle( this,
					PositionConstants.EAST,
					getColumnNumber( ) + getColSpan( ) - 1,
					getColumnNumber( ) + getColSpan( ) );
			retValue.add( column );
		}
		if ( getRowNumber( ) + getRowSpan( ) - 1 < rowNumer )
		{
			CrosstavCellDragHandle row = new CrosstavCellDragHandle( this,
					PositionConstants.SOUTH,
					getRowNumber( ) + getRowSpan( ) - 1,
					getRowNumber( ) + getRowSpan( ) );
			retValue.add( row );
		}
		return retValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure( )
	{
		CellFigure figure = new CrosstabCellFigure( );
		ReportFlowLayout rflayout = new ReportFlowLayout( );
		figure.setLayoutManager( rflayout );
		figure.setOpaque( false );

		return figure;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.EditPart#getDragTracker(org.eclipse.gef.Request)
	 */
	public DragTracker getDragTracker( Request req )
	{
		return new CellDragTracker( this );
	}

	/**
	 * @return
	 */
	protected CrosstabCellAdapter getCrosstabCellAdapter( )
	{
		return (CrosstabCellAdapter) getModel( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.designer.internal.ui.layout.ITableLayoutCell#getColSpan()
	 */
	public int getColSpan( )
	{
		return getCrosstabCellAdapter( ).getColumnSpan( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.designer.internal.ui.layout.ITableLayoutCell#getColumnNumber()
	 */
	public int getColumnNumber( )
	{
		return getCrosstabCellAdapter( ).getColumnNumber( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.designer.internal.ui.layout.ITableLayoutCell#getRowNumber()
	 */
	public int getRowNumber( )
	{
		return getCrosstabCellAdapter( ).getRowNumber( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.designer.internal.ui.layout.ITableLayoutCell#getRowSpan()
	 */
	public int getRowSpan( )
	{
		return getCrosstabCellAdapter( ).getRowSpan( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createChild(java.lang.Object)
	 */
	protected EditPart createChild( Object model )
	{
		EditPart part = CrosstabGraphicsFactory.INSTANCEOF.createEditPart( this,
				model );
		if ( part != null )
		{
			return part;
		}
		return super.createChild( model );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#getAdapter(java.lang.Class)
	 */
	public Object getAdapter( Class key )
	{
		if ( key == ISelectionFlitter.class )
		{
			return new ISelectionFlitter( ) {

				public List flitterEditpart( List editparts )
				{
					int size = editparts.size( );
					List copy = new ArrayList( editparts );

					boolean hasCell = false;
					boolean hasOther = false;
					for ( int i = 0; i < size; i++ )
					{
						if ( editparts.get( i ) instanceof CrosstabCellEditPart )
						{
							hasCell = true;
						}
						else
						{
							hasOther = true;
						}
					}
					if ( hasCell && hasOther )
					{

						for ( int i = 0; i < size; i++ )
						{
							if ( editparts.get( i ) instanceof CrosstabCellEditPart )
							{
								copy.remove( editparts.get( i ) );
							}
						}
					}
					editparts = copy;
					return editparts;
				}

			};
		}
		return super.getAdapter( key );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.report.designer.internal.ui.editors.schematic.editparts.ReportElementEditPart#contentChange(java.util.Map)
	 */
	protected void contentChange( Map info )
	{
		( (ReportElementEditPart) getParent( ) ).refresh( );
		if ( getParent( ) != null )
		{
			super.contentChange( info );
		}
	}
}
