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
package com.sitescape.team.module.file;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sitescape.team.util.NLT;

public class FilesErrors implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private List problems;
	
	public FilesErrors() {
		this.problems = new ArrayList();
	}
	
	public void addProblem(Problem problem) {
		problems.add(problem);
	}
	
	public List getProblems() {
		return problems;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < problems.size(); i++) {
			if(i > 0)
				sb.append("\n");
			sb.append(((Problem) problems.get(i)).toString());
		}
		return sb.toString();
	}
	
	public static class Problem implements Serializable {
		private static final long serialVersionUID = 1L;
		
		// Problem types
		public static int OTHER_PROBLEM								= 0;
		public static int PROBLEM_FILTERING							= 1;
		public static int PROBLEM_STORING_PRIMARY_FILE				= 2;
		public static int PROBLEM_GENERATING_SCALED_FILE			= 3;
		public static int PROBLEM_STORING_SCALED_FILE				= 4;
		public static int PROBLEM_GENERATING_THUMBNAIL_FILE			= 5;
		public static int PROBLEM_STORING_THUMBNAIL_FILE			= 6;
		public static int PROBLEM_DELETING_PRIMARY_FILE				= 7;
		public static int PROBLEM_DELETING_SCALED_FILE				= 8;
		public static int PROBLEM_DELETING_THUMBNAIL_FILE			= 9;
		public static int PROBLEM_CANCELING_LOCK					= 10;
		public static int PROBLEM_LOCKED_BY_ANOTHER_USER			= 11;
		public static int PROBLEM_RESERVED_BY_ANOTHER_USER  		= 12;
		public static int PROBLEM_FILE_EXISTS						= 13;
		public static int PROBLEM_ARCHIVING							= 14;
		public static int PROBLEM_MIRRORED_FILE_IN_REGULAR_FOLDER	= 15;
		public static int PROBLEM_MIRRORED_FILE_MULTIPLE			= 16;
		public static int PROBLEM_REGULAR_FILE_IN_MIRRORED_FOLDER   = 17;
		public static int PROBLEM_MIRRORED_FILE_READONLY_DRIVER		= 18;
		
		// Message codes corresponding to each problem type.
		public static String[] typeCodes = {
			"file.error.other",
			"file.error.filtering",
			"file.error.storing.primary.file",
			"file.error.generating.scaled.file",
			"file.error.storing.scaled.file",
			"file.error.generating.thumbnail.file",
			"file.error.storing.thumbnail.file",
			"file.error.deleting.primary.file",
			"file.error.deleting.scaled.file",
			"file.error.deleting.thumbnail.file",
			"file.error.canceling.lock",
			"file.error.locked.by.another.user",
			"file.error.reserved.by.another.user",
			"file.error.file.exists",
			"file.error.archiving",
			"file.error.mirrored.file.in.regular.folder",
			"file.error.mirrored.file.multiple",
			"file.error.regular.file.in.mirrored.folder",
			"file.error.mirrored.file.readonly.driver"
		};
		
		private String repositoryName; // required
		private String fileName; // required
		private int type; // required - one of the constants defined above
		// at most one of the following two is set.
		private Exception exception; // may be null
		
		public Problem(String repositoryName, String fileName, 
				int type) {
			this.repositoryName = repositoryName;
			this.fileName = fileName;
			this.type = type;
		}
		
		public Problem(String repositoryName, String fileName, 
				int type, Exception exception) {
			this(repositoryName, fileName, type);
			this.exception = exception;
		}
		
		public Exception getException() {
			return exception; // may be null
		}
		
		public String getFileName() {
			return fileName;
		}
		
		public int getType() {
			return type;
		}
		
		public String getTypeCode() {
			return typeCodes[type];
		}
		
		public String getRepositoryName() {
			return repositoryName;
		}
		
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(NLT.get(getTypeCode()))
				.append(": ")
				.append(getFileName())
				.append(" (")
				.append(getRepositoryName())
				.append(")")
				.append(": ");
			if(getException() != null)
				sb.append(getException().getLocalizedMessage());
			return sb.toString();
		}
	}

}
