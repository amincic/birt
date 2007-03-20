/*
 *****************************************************************************
 * Copyright (c) 2004, 2007 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *
 ******************************************************************************
 */ 

package org.eclipse.birt.data.engine.odaconsumer;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.eclipse.birt.data.engine.core.DataException;
import org.eclipse.birt.data.engine.executor.ResultClass;
import org.eclipse.birt.data.engine.executor.ResultObject;
import org.eclipse.birt.data.engine.i18n.ResourceConstants;
import org.eclipse.birt.data.engine.odi.IResultClass;
import org.eclipse.birt.data.engine.odi.IResultObject;
import org.eclipse.datatools.connectivity.oda.IBlob;
import org.eclipse.datatools.connectivity.oda.IClob;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * <code>ResultSet</code> maintains an incremental pointer to rows in the  
 * result set.
 */
public class ResultSet
{
	private IResultSet m_resultSet;
	private IResultClass m_resultClass;		// cached result class

	// trace logging variables
	private static String sm_className = ResultSet.class.getName();
	private static String sm_loggerName = ConnectionManager.sm_packageName;
	private static LogHelper sm_logger = LogHelper.getInstance( sm_loggerName );
		
	ResultSet( IResultSet resultSet, IResultClass resultClass )
	{
	    final String methodName = "ResultSet"; //$NON-NLS-1$
		if( sm_logger.isLoggingEnterExitLevel() )
		    sm_logger.entering( sm_className, methodName, 
		            		new Object[] { resultSet, resultClass } );

		assert( resultSet != null && resultClass != null );
		m_resultSet = resultSet;
		m_resultClass = resultClass;

	    sm_logger.exiting( sm_className, methodName, this );
	}
	
	/**
	 * Returns an <code>IResultClass</code> representing the metadata of the 
	 * result set for this <code>ResultSet</code>.
	 * @return	this <code>ResultSet</code>'s metadata
	 * @throws DataException	if data source error occurs.
	 */
	public IResultClass getMetaData() throws DataException
	{
		return m_resultClass;
	}
	
	/**
	 * Specifies the maximum number of <code>IResultObjects</code> that can be 
	 * fetched from this <code>ResultSet</code>.
	 * @param max	the maximum number of <code>IResultObjects</code> that can be 
	 *				fetched; 0 means no limit.
	 * @throws DataException	if data source error occurs.
	 */
	public void setMaxRows( int max ) throws DataException
	{
	    final String methodName = "setMaxRows"; //$NON-NLS-1$
		try
		{
			m_resultSet.setMaxRows( max );
		}
		catch( OdaException ex )
		{
		    sm_logger.logp( Level.SEVERE, sm_className, methodName,
		            		"Cannot set max rows.", ex ); //$NON-NLS-1$
			throw new DataException( ResourceConstants.CANNOT_SET_MAX_ROWS, ex );
		}
		catch( UnsupportedOperationException ex )
		{
		    sm_logger.logp( Level.WARNING, sm_className, methodName,
		            		"Cannot set max rows.", ex ); //$NON-NLS-1$
		}
	}
	
	/**
	 * Returns the IResultObject representing the next row in the result set.
	 * @return 	the IResultObject representing the next row; null if there are 
	 * 			no more rows available or if max rows limit has been reached.
	 * @throws DataException	if data source error occurs.
	 */
	public IResultObject fetch( ) throws DataException
	{
		if ( m_resultSet == null )
			return null;

	    final String methodName = "fetch"; //$NON-NLS-1$
        final String errorCode = ResourceConstants.CANNOT_FETCH_NEXT_ROW;

		try
		{
			if( ! m_resultSet.next( ) )
				return null;
		}
		catch( OdaException ex )
		{
            handleException( ex, errorCode, methodName, -1 );
		}
		catch( UnsupportedOperationException ex )
		{
            handleException( ex, errorCode, methodName, -1 );
		}

		int columnCount = m_resultClass.getFieldCount();
		int[] driverPositions = 
			( (ResultClass) m_resultClass ).getFieldDriverPositions();
		assert( columnCount == driverPositions.length );
		
		Object[] fields = new Object[ columnCount ];
		
		for( int i = 1; i <= columnCount; i++ )
		{
			if ( m_resultClass.isCustomField( i ) == true )
				continue;
			
			Class dataType = m_resultClass.getFieldValueClass( i );
			int driverPosition = driverPositions[i - 1];
			Object colValue = null;
			
			if( dataType == Integer.class )
			{
				int j = getInt( driverPosition );
				if( ! wasNull() )
					colValue = new Integer( j ); 
			}
			else if( dataType == Double.class )
			{
				double d = getDouble( driverPosition );
				if( ! wasNull() )
					colValue = new Double( d );
			}
			else if( dataType == String.class )
				colValue = getString( driverPosition );
			else if( dataType == BigDecimal.class )
				colValue = getBigDecimal( driverPosition );
			else if( dataType == java.util.Date.class )
				colValue = getDate( driverPosition );
			else if( dataType == Time.class )
				colValue = getTime( driverPosition );
			else if( dataType == Timestamp.class )
				colValue = getTimestamp( driverPosition );
			else if( dataType == IBlob.class )
				colValue = getBlob( driverPosition );
			else if( dataType == IClob.class )
				colValue = getClob( driverPosition );
            else if( dataType == Boolean.class )
            {
                boolean val = getBoolean( driverPosition );
                if( ! wasNull() )
                    colValue = new Boolean( val );
            }
			else
				assert false;
			
			if( wasNull( ) )
				colValue = null;
			
			fields[i - 1] = colValue;
		}
		
		IResultObject ret = new ResultObject( m_resultClass, fields );

		sm_logger.logp( Level.FINEST, sm_className, methodName, 
		            		"Fetched next row: {0} .", ret ); //$NON-NLS-1$

		return ret;
	}

    private int getInt( int driverPosition ) throws DataException
	{
        final String methodName = "getInt"; //$NON-NLS-1$
        final String errorCode = ResourceConstants.CANNOT_GET_INT_FROM_COLUMN;

		try
		{
			return m_resultSet.getInt( driverPosition );
		}
		catch( OdaException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
		catch( UnsupportedOperationException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
        return 0;
	}
	
	private double getDouble( int driverPosition ) throws DataException
	{
	    final String methodName = "getDouble"; //$NON-NLS-1$
        final String errorCode = ResourceConstants.CANNOT_GET_DOUBLE_FROM_COLUMN;

		try
		{
			return m_resultSet.getDouble( driverPosition );
		}
		catch( OdaException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
		catch( UnsupportedOperationException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
        return 0;
	}
	
	private String getString( int driverPosition ) throws DataException
	{
	    final String methodName = "getString"; //$NON-NLS-1$
        final String errorCode = ResourceConstants.CANNOT_GET_STRING_FROM_COLUMN;

		try
		{
			return m_resultSet.getString( driverPosition );
		}
		catch( OdaException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
		catch( UnsupportedOperationException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
        return null;
	}
	
	private BigDecimal getBigDecimal( int driverPosition ) throws DataException
	{
	    final String methodName = "getBigDecimal"; //$NON-NLS-1$
        final String errorCode = ResourceConstants.CANNOT_GET_BIGDECIMAL_FROM_COLUMN;

		try
		{
			return m_resultSet.getBigDecimal( driverPosition );
		}
		catch( OdaException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
		catch( UnsupportedOperationException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
        return null;
	}
	
	private java.util.Date getDate( int driverPosition ) throws DataException
	{
	    final String methodName = "getDate"; //$NON-NLS-1$
        final String errorCode = ResourceConstants.CANNOT_GET_DATE_FROM_COLUMN;

		try
		{
			return m_resultSet.getDate( driverPosition );
		}
		catch( OdaException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
		catch( UnsupportedOperationException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
        return null;
	}
	
	private Time getTime( int driverPosition ) throws DataException
	{
	    final String methodName = "getTime"; //$NON-NLS-1$
        final String errorCode = ResourceConstants.CANNOT_GET_TIME_FROM_COLUMN;

		try
		{
			return m_resultSet.getTime( driverPosition );
		}
		catch( OdaException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
		catch( UnsupportedOperationException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
        return null;
	}
	
	private Timestamp getTimestamp( int driverPosition ) throws DataException
	{
	    final String methodName = "getTimestamp"; //$NON-NLS-1$
        final String errorCode = ResourceConstants.CANNOT_GET_TIMESTAMP_FROM_COLUMN;

		try
		{
			return m_resultSet.getTimestamp( driverPosition );
		}
		catch( OdaException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
		catch( UnsupportedOperationException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
        return null;
	}
	
    private IBlob getBlob( int driverPosition ) throws DataException
    {
        final String methodName = "getBlob"; //$NON-NLS-1$
        final String errorCode = ResourceConstants.CANNOT_GET_BLOB_FROM_COLUMN;

		try
		{
			return m_resultSet.getBlob( driverPosition );
		}
		catch( OdaException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
		catch( UnsupportedOperationException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
        return null;
    }

    private IClob getClob( int driverPosition ) throws DataException
    {
        final String methodName = "getClob"; //$NON-NLS-1$
        final String errorCode = ResourceConstants.CANNOT_GET_CLOB_FROM_COLUMN;

		try
		{
			return m_resultSet.getClob( driverPosition );
		}
		catch( OdaException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
		catch( UnsupportedOperationException ex )
		{
            handleException( ex, errorCode, methodName, driverPosition );
		}
        return null;
    }

    private boolean getBoolean( int driverPosition ) throws DataException
    {
        final String methodName = "getBoolean"; //$NON-NLS-1$
        final String errorCode = ResourceConstants.CANNOT_GET_BOOLEAN_FROM_COLUMN;

        try
        {
            return m_resultSet.getBoolean( driverPosition );
        }
        catch( OdaException ex )
        {
            handleException( ex, errorCode, methodName, driverPosition );
        }
        catch( UnsupportedOperationException ex )
        {
            handleException( ex, errorCode, methodName, driverPosition );
        }
        return false;
    }
	
	private boolean wasNull() throws DataException
	{
	    final String methodName = "wasNull"; //$NON-NLS-1$
		try
		{
			return m_resultSet.wasNull();
		}
		catch( OdaException ex )
		{
		    sm_logger.logp( Level.SEVERE, sm_className, methodName,
    						"Cannot check wasNull.", ex ); //$NON-NLS-1$
			throw new DataException( ResourceConstants.CANNOT_DETERMINE_WAS_NULL, ex );
		}
		catch( UnsupportedOperationException ex )
		{
		    sm_logger.logp( Level.WARNING, sm_className, methodName,
    						"Cannot check wasNull. Default to false.", ex ); //$NON-NLS-1$
		    return false;
		}
	}

	/**
	 * Returns the current row's 1-based index position.
	 * @return	current row's 1-based index position.
	 * @throws DataException	if data source error occurs.
	 */
	public int getRowPosition( ) throws DataException
	{
	    final String methodName = "getRowPosition"; //$NON-NLS-1$
		try
		{
			return m_resultSet.getRow( );
		}
		catch( OdaException ex )
		{
		    sm_logger.logp( Level.SEVERE, sm_className, methodName,
    						"Cannot get row position.", ex ); //$NON-NLS-1$
			throw new DataException( ResourceConstants.CANNOT_GET_ROW_POSITION, ex );
		}
		catch( UnsupportedOperationException ex )
		{
		    sm_logger.logp( Level.WARNING, sm_className, methodName,
    						"Cannot get row position.  Default to 0.", ex ); //$NON-NLS-1$
		    return 0;
		}
	}
	
	/**
	 * Closes this <code>ResultSet</code>.
	 * @throws DataException	if data source error occurs.
	 */
	public void close( ) throws DataException
	{
	    final String methodName = "close"; //$NON-NLS-1$
	    sm_logger.entering( sm_className, methodName );
	    
		try
		{
			m_resultSet.close( );
		}
		catch( OdaException ex )
		{
		    sm_logger.logp( Level.SEVERE, sm_className, methodName,
    						"Cannot close result set.", ex ); //$NON-NLS-1$
			throw new DataException( ResourceConstants.CANNOT_CLOSE_RESULT_SET, ex );
		}
		catch( UnsupportedOperationException ex )
		{
		    sm_logger.logp( Level.WARNING, sm_className, methodName,
    						"Cannot close result set.", ex ); //$NON-NLS-1$
		}

		sm_logger.exiting( sm_className, methodName );
	}

    private void handleException( Throwable ex, String errorCode,
            final String methodName, int driverColumnPosition ) throws DataException
    {
        DataException dataEx = ( driverColumnPosition > 0 ) ?
                new DataException( errorCode, ex, new Integer( driverColumnPosition ) ) :
                new DataException( errorCode, ex );
        sm_logger.logp( Level.SEVERE, sm_className, methodName,
                        dataEx.getLocalizedMessage(), ex );
        throw dataEx;
    }
	
}
