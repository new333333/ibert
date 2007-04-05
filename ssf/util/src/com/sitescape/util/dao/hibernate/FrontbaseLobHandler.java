/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
/*
 * Created on Dec 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.util.dao.hibernate;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;

/**
 * @author Janet McCann
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FrontbaseLobHandler extends DefaultLobHandler {
	public LobCreator getLobCreator() {
		return new FrontbaseLobCreator();
	}
	
	protected class FrontbaseLobCreator implements LobCreator {

		public void setBlobAsBytes(PreparedStatement ps, int paramIndex, byte[] content)
				throws SQLException {
			//ps.setBytes(paramIndex, content);
			if (content == null) 
				ps.setBytes(paramIndex, null);
			else 
				setBlobAsBinaryStream(ps, paramIndex, new ByteArrayInputStream(content), content.length);
			if(logger.isDebugEnabled())
				logger.debug(content != null ? "Set bytes for BLOB with length " + content.length :
					"Set BLOB to null");
		}

		public void setBlobAsBinaryStream(
				PreparedStatement ps, int paramIndex, InputStream binaryStream, int contentLength)
				throws SQLException {
			ps.setObject(paramIndex, binaryStream, Types.BLOB);
			if(logger.isDebugEnabled())
				logger.debug(binaryStream != null ? "Set binary stream for BLOB with length " + contentLength :
					"Set BLOB to null");
		}

		public void setClobAsString(PreparedStatement ps, int paramIndex, String content)
		    throws SQLException {
			//ps.setString(paramIndex, content);
			if (content == null)
				ps.setString(paramIndex, null);
			else
				setClobAsCharacterStream(ps, paramIndex, new StringReader(content), content.length());
			if(logger.isDebugEnabled())
				logger.debug(content != null ? "Set string for CLOB with length " + content.length() :
					"Set CLOB to null");
		}

		public void setClobAsAsciiStream(
				PreparedStatement ps, int paramIndex, InputStream asciiStream, int contentLength)
		    throws SQLException {
			ps.setAsciiStream(paramIndex, asciiStream, contentLength);
			if(logger.isDebugEnabled())
				logger.debug(asciiStream != null ? "Set ASCII stream for CLOB with length " + contentLength :
					"Set CLOB to null");
		}


		public void setClobAsCharacterStream(
				PreparedStatement ps, int paramIndex, Reader characterStream, int contentLength)
		    throws SQLException {
			ps.setCharacterStream(paramIndex, characterStream, contentLength);
			if(logger.isDebugEnabled())
				logger.debug(characterStream != null ? "Set character stream for CLOB with length " + contentLength :
					"Set CLOB to null");
		}

		public void close() {
			// nothing to do here
		}
	}
}
