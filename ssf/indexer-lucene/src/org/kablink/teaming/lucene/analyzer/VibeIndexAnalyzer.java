/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.lucene.analyzer;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.kablink.teaming.lucene.util.SsfTokenFilter;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

public class VibeIndexAnalyzer extends VibeAnalyzer {
	
	public VibeIndexAnalyzer() {
		super();
	}
	
	public VibeIndexAnalyzer(String stemmerName) {
		super(stemmerName);
	}

	public VibeIndexAnalyzer(boolean foldToAscii) {
		super(foldToAscii);
	}
	
	public VibeIndexAnalyzer(Set stopWords, boolean ignoreCaseForStop,
			String stemmerName, boolean foldToAscii) {
		super(stopWords, ignoreCaseForStop, stemmerName, foldToAscii);
	}

	public VibeIndexAnalyzer(File stopWords, boolean ignoreCaseForStop,
			String stemmerName, boolean foldToAscii) throws IOException {
		super(stopWords, ignoreCaseForStop, stemmerName, foldToAscii);
	}

	public VibeIndexAnalyzer(Reader stopWords, boolean ignoreCaseForStop,
			String stemmerName, boolean foldToAscii) throws IOException {
		super(stopWords, ignoreCaseForStop, stemmerName, foldToAscii);
	}

	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream result = new StandardTokenizer(VERSION, reader);
		result = new StandardFilter(result);
		if(foldToAscii)
			result = new ASCIIFoldingFilter(result);
		result = new SsfTokenFilter(result);
		if (stopSet != null && stopSet.size() > 0)
			result = new StopFilter(true, result, stopSet, ignoreCaseForStop);
		if(stemmerName != null && !stemmerName.equals(""))
			result = new SnowballFilter(result, stemmerName);
		return result;
	}

	public TokenStream reusableTokenStream(String fieldName, Reader reader)
			throws IOException {
		if (overridesTokenStreamMethod) {
			// LUCENE-1678: force fallback to tokenStream() if we
			// have been subclassed and that subclass overrides
			// tokenStream but not reusableTokenStream
			return tokenStream(fieldName, reader);
		}

		SavedStreams streams = (SavedStreams) getPreviousTokenStream();
		if (streams == null) {
			streams = new SavedStreams();
			streams.source = new StandardTokenizer(VERSION, reader);
			streams.result = new StandardFilter(streams.source);
			if(foldToAscii)
				streams.result = new ASCIIFoldingFilter(streams.result);
			streams.result = new SsfTokenFilter(streams.result);
			if (stopSet != null && stopSet.size() > 0)
				streams.result = new StopFilter(true, streams.result, stopSet,
						ignoreCaseForStop);
			if(stemmerName != null && !stemmerName.equals(""))
				streams.result = new SnowballFilter(streams.result, stemmerName);
			setPreviousTokenStream(streams);
		} else {
			streams.source.reset(reader);
		}
		return streams.result;
	}

}
