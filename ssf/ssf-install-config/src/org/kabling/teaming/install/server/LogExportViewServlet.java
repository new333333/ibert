/*
 * ========================================================================
 *
 * Copyright (c) 2012 Unpublished Work of Novell, Inc. All Rights Reserved.
 *
 * THIS WORK IS AN UNPUBLISHED WORK AND CONTAINS CONFIDENTIAL,
 * PROPRIETARY AND TRADE SECRET INFORMATION OF NOVELL, INC. ACCESS TO
 * THIS WORK IS RESTRICTED TO (I) NOVELL, INC. EMPLOYEES WHO HAVE A NEED
 * TO KNOW HOW TO PERFORM TASKS WITHIN THE SCOPE OF THEIR ASSIGNMENTS AND
 * (II) ENTITIES OTHER THAN NOVELL, INC. WHO HAVE ENTERED INTO
 * APPROPRIATE LICENSE AGREEMENTS. NO PART OF THIS WORK MAY BE USED,
 * PRACTICED, PERFORMED, COPIED, DISTRIBUTED, REVISED, MODIFIED,
 * TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED,
 * LINKED, RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN
 * CONSENT OF NOVELL, INC. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT
 * AUTHORIZATION COULD SUBJECT THE PERPETRATOR TO CRIMINAL AND CIVIL
 * LIABILITY.
 *
 * ========================================================================
 */
package org.kabling.teaming.install.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.kabling.teaming.install.shared.InstallerConfig;
import org.kabling.teaming.install.shared.LicenseInformation;
import org.kabling.teaming.install.shared.ShellCommandInfo;

/**
 * @author Rajesh
 * 
 */
public class LogExportViewServlet extends HttpServlet
{
	Logger logger = Logger.getLogger("org.kabling.teaming.install.server.LogExportViewServlet");
	private static final long serialVersionUID = 1L;

	private static final String CONFIG_ZIP_NAME = "filr_logs.zip";
	private static final String ZIP_MIME_TYPE = "application/zip";

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		try
		{
			// return the current configurations settings as a zip file
			getFilrConfigurationSettings(resp);
		}
		catch (Exception e)
		{
			logger.error("Error trying to get filr configuation as a zip file " + e.getMessage());
		}
	}



	private void getFilrConfigurationSettings(HttpServletResponse response) throws Exception
	{
		ServletOutputStream op = response.getOutputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);

		// Zip all the catalina logs
		File file = new File("/opt/novell/filr/apache-tomcat/logs");
		
		String[] files = file.list(new FilenameFilter()
		{
			
			@Override
			public boolean accept(File arg0, String arg1)
			{
				if (arg0.getName().startsWith("catalina"))
					return true;
				
				return false;
			}
		});
		
		if (files != null)
		{
			for (String fileName : files)
			{
				zos.putNextEntry(new ZipEntry(fileName));
				byte[] b = new byte[1024];
				int len;
				FileInputStream fis = new FileInputStream(file.getAbsolutePath() + File.separator + fileName);
				while ((len = fis.read(b)) != -1)
				{
					zos.write(b, 0, len);
				}
				fis.close();
				zos.closeEntry();
			}
		}
		zos.close();

		response.setContentType(ZIP_MIME_TYPE);
		response.setHeader("Content-Disposition", "attachment;filename=\"" + CONFIG_ZIP_NAME + "\"");

		op.write(baos.toByteArray());
		op.flush();

	}
}