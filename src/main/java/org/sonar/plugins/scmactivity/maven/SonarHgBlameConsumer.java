/*
 * SonarQube SCM Activity Plugin
 * Copyright (C) 2010 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.scmactivity.maven;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.maven.scm.ScmFileStatus;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.provider.hg.command.HgConsumer;

public class SonarHgBlameConsumer extends HgConsumer {

	private List<BlameLine> lines = new ArrayList<BlameLine>();

    private static final String HG_TIMESTAMP_PATTERN = "EEE MMM dd HH:mm:ss yyyy Z";

    public SonarHgBlameConsumer( ScmLogger logger )
    {
        super( logger );
    }
    
    @Override
    public void consumeLine(String line) {
    	if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug( line );
        }
        String trimmedLine = line.trim();
        doConsume( null, trimmedLine );
    }

    public void doConsume( ScmFileStatus status, String trimmedLine )
    {
    	/* FirstName LastName <email@company.com> 15616dds6 Tue Nov 25 13:45:00 2014 +0100: code */
    	int codeIndexSeparation = trimmedLine.indexOf( ": " );
    	String annotation = trimmedLine;
    	if (codeIndexSeparation != -1)
    		annotation = trimmedLine.substring(0,  codeIndexSeparation);
    	
        annotation = annotation.trim();

        String[] tokens = annotation.split( " " );

        int committerIndex = -1;
        StringBuilder author = new StringBuilder();
        for (int i = 0; i < tokens.length - 7; ++i)
        {
        	String currentWord = tokens[i];
        	if (currentWord.startsWith("<") && currentWord.endsWith(">"))
        	{
        		committerIndex = i;
        	}
        	else
        	{
	        	if (author.length() > 0)
	            {
	                author.append(' ');
	            }
	            author.append(tokens[i]);
        	}
        }
        
        String committer;
        if (committerIndex == -1)
        	committer = author.toString();
        else
        	committer = tokens[committerIndex].substring(1, tokens[committerIndex].length() - 1);;

        String revision = tokens[tokens.length - 7];

        StringBuilder dateStr = new StringBuilder();
        for ( int i = tokens.length - 6; i < tokens.length; ++i )
        {
            if ( dateStr.length() > 0 )
            {
                dateStr.append( ' ' );
            }
            dateStr.append( tokens[i] );
        }
        Date dateTime = parseDate( dateStr.toString(), null, HG_TIMESTAMP_PATTERN, Locale.ENGLISH );

        lines.add(new BlameLine(dateTime, revision, author.toString(), committer));
    }

    public List<BlameLine> getLines()
    {
        return lines;
    }
}
