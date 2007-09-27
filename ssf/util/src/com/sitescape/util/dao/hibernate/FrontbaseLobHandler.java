/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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
