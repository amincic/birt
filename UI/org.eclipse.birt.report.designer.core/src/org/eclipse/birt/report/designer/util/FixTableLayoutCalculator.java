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

package org.eclipse.birt.report.designer.util;

/**
 * Implment Fix layout algorithm of table column width. The class assume the
 * table width and column width are setted to fix size, eg fix num or fix
 * percentage.
 *  
 */

public class FixTableLayoutCalculator implements ITableLayoutCalculator
{

	private float tableWidth;

	private int colNum;

	private String[] definedWidthValues;

	private float[] tableWidthValues = null;

	private float fixWidthAmt;

	private float colMinSize;

	private float minWidthAmt;

	private int minAmt;

	/**
	 * 
	 * @see org.eclipse.birt.report.designer.util.ITableLayoutCalculator#getColWidth()
	 */
	public float[] getFloatColWidth( )
	{
		if ( definedWidthValues.length > 0 )
		{
			colNum = definedWidthValues.length;
			tableWidthValues = new float[colNum];

			return calcColumnWidth( );
		}
		return null;
	}

	/**
	 * Set predefined column widths.
	 * @param widthValues
	 */
	public void setDefinedColWidth( String[] widthValues )
	{
		definedWidthValues = widthValues;
	}

	/**
	 * 
	 * @see org.eclipse.birt.report.designer.util.ITableLayoutCalculator#getRowHeight()
	 */
	public float[] getRowHeight( )
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Set width of table.
	 * @param width
	 */
	public void setTableWidth( float width )
	{
		tableWidth = width;
	}

	private float[] calcColumnWidth( ) throws NumberFormatException
	{
		fixWidthAmt = 0;

		getFixNumColWidth( );
		try
		{
			calPercentAndNullColWidth( );
		}
		catch ( NumberFormatException e )
		{
			throw e;
		}
		adjustWidth( );

		return tableWidthValues;
	}

	/**
	 *  
	 */
	private void adjustWidth( )
	{
		float amt = 0;
		if ( minWidthAmt > tableWidth )
		{
			for ( int i = 0; i < tableWidthValues.length; i++ )
			{
				tableWidthValues[i] = colMinSize;
			}

			return;
		}

		for ( int i = 0; i < tableWidthValues.length; i++ )
		{
			amt = amt + tableWidthValues[i];
		}
		float adjustValue = 0;

		if ( tableWidth - amt > 0 )
		{
			adjustValue = ( tableWidth - amt ) / minAmt;
			if ( ( adjustValue - colMinSize ) < 0 )
			{
				adjustValue = colMinSize;
			}
			for ( int i = 0; i < tableWidthValues.length; i++ )
			{
				if ( tableWidthValues[i] == 0 )
				{
					tableWidthValues[i] = adjustValue;
				}
			}
		}

		amt = 0;
		for ( int i = 0; i < tableWidthValues.length; i++ )
		{
			amt = amt + tableWidthValues[i];
		}
		
		//amt < table mini with
		//make every column be minisize
		if(tableWidth <= colNum * colMinSize)
		{
			for ( int i = 0; i < tableWidthValues.length; i++ )
			{
					tableWidthValues[i] = colMinSize;
			}
		}
		else
		{
			while ( Math.abs( amt - tableWidth ) > 0.5 )
			{
				adjustValue = tableWidth / amt;
				for ( int i = 0; i < tableWidthValues.length; i++ )
				{
					tableWidthValues[i] = tableWidthValues[i] * adjustValue;
					if ( tableWidthValues[i] < colMinSize )
					{
						tableWidthValues[i] = colMinSize;
					}
				}
	
				amt = 0;
	
				for ( int i = 0; i < tableWidthValues.length; i++ )
				{
					amt = amt + tableWidthValues[i];
				}
			}
		}
		return;
	}

	/**
	 *  
	 */
	private void calPercentAndNullColWidth( ) throws NumberFormatException
	{
		for ( int i = 0; i < definedWidthValues.length; i++ )
		{
			try
			{
				float width = Float.parseFloat( definedWidthValues[i] );

			}
			catch ( NumberFormatException e )
			{
				if ( definedWidthValues[i].endsWith( "%" ) )
				{
					tableWidthValues[i] = tableWidth
							* getPercentValue( definedWidthValues[i] )
							/ 100;
					if ( tableWidthValues[i] < colMinSize )
					{
						tableWidthValues[i] = 0;
						minWidthAmt = minWidthAmt + colMinSize;
						minAmt++;
					}
					fixWidthAmt = fixWidthAmt + tableWidthValues[i];
				}
				else if ( definedWidthValues[i] == null
						| "".equalsIgnoreCase( definedWidthValues[i] ) )
				{
					tableWidthValues[i] = 0;
					fixWidthAmt = fixWidthAmt + tableWidthValues[i];
					minWidthAmt = minWidthAmt + colMinSize;
					minAmt++;
				}
				else
				{
					throw e;
				}

			}
		}
		setFixWidthAmt( fixWidthAmt );
	}

	/**
	 * @param string
	 * @return
	 */
	private float getPercentValue( String value )
	{
		return Float.parseFloat( value.substring( 0, value.indexOf( "%" ) ) );

	}

	/**
	 *  
	 */
	private void getFixNumColWidth( )
	{
		float fixWidthAmt = 0;
		for ( int i = 0; i < definedWidthValues.length; i++ )
		{
			try
			{
				float width = Float.parseFloat( definedWidthValues[i] );
				tableWidthValues[i] = width;
				if ( tableWidthValues[i] < colMinSize )
				{
					tableWidthValues[i] = colMinSize;
					minWidthAmt = minWidthAmt + colMinSize;
					minAmt++;
				}
				fixWidthAmt = fixWidthAmt + width;

			}
			catch ( NumberFormatException e )
			{
			}
		}

		setFixWidthAmt( fixWidthAmt );

	}

	/**
	 * @param fixWidthAmt
	 */
	private void setFixWidthAmt( float widthAmt )
	{
		fixWidthAmt = widthAmt;

	}

	/**
	 * Set the minimum size of column
	 * 
	 * @param minSize
	 */
	public void setColMinSize( float minSize )
	{
		colMinSize = minSize;
	}

	/**
	 * 
	 * @see org.eclipse.birt.report.designer.util.ITableLayoutCalculator#getIntColWidth()
	 */
	public int[] getIntColWidth( ) throws NumberFormatException
	{

		float[] width = null;
		try
		{
			width = this.getFloatColWidth( );
		}
		catch ( NumberFormatException e )
		{
			throw e;
		}

		int amt = 0;

		if ( width == null )
		{
			throw new NumberFormatException( "The input value can't be parse" );
		}

		int[] intWidth = new int[width.length];

		for ( int i = 0; i < width.length; i++ )
		{
			intWidth[i] = Math.round( width[i] );
			amt = amt + intWidth[i];
		}

		int balance = amt - (int) tableWidth;
		int i = 0;
		while ( balance > 0 )
		{
			if ( intWidth[i] > colMinSize )
			{
				intWidth[i]--;
			}

			i++;
			if ( i >= intWidth.length - 1 )
			{
				break;
			}
			balance--;
		}
		return intWidth;
	}

	/**
	 * @see org.eclipse.birt.report.designer.util.ITableLayoutCalculator#getFloatRowHeight()
	 */
	public float[] getFloatRowHeight( )
	{
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * @see org.eclipse.birt.report.designer.util.ITableLayoutCalculator#getIntRowHeight()
	 */
	public float[] getIntRowHeight( )
	{
		// TODO Auto-generated method stub
		return null;
	}
}