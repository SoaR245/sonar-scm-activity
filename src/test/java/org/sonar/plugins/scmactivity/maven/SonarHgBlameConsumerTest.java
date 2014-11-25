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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

import junit.framework.Assert;

import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.log.DefaultLog;
import org.apache.maven.scm.log.ScmLogger;
import org.apache.maven.scm.provider.git.gitexe.command.blame.GitBlameConsumer;
import org.junit.Test;


public class SonarHgBlameConsumerTest {

	  @Test
	  public void non_verbose_output() throws Exception {

		SonarHgBlameConsumer consumer = new SonarHgBlameConsumer(mock(ScmLogger.class));
		
		consumer.consumeLine("Mark 89955f2b2bc0 Mon Nov 24 15:05:47 2014 +0100: <?xml version=\"1.0\" encoding=\"UTF-8\"?><!--");
		consumer.consumeLine("Another efc94016c544 Fri Nov 14 10:37:04 2014 +0100: Licensed to the Apache Software Foundation (ASF) under one");
		consumer.consumeLine("Another efc94016c544 Fri Nov 14 10:37:04 2014 +0100: or more contributor license agreements.  See the NOTICE file");
		consumer.consumeLine(" Mark bb31deeb70a9 Thu Nov 20 17:33:07 2014 +0100: distributed with this work for additional information");
		consumer.consumeLine(" Mark bb31deeb70a9 Thu Nov 20 17:33:07 2014 +0100:");
		consumer.consumeLine(" Another bb31deeb70a9 Thu Nov 20 17:33:07 2014 +0100: with the License.  You may obtain a copy of the License at");
		  
		Assert.assertEquals(6, consumer.getLines().size());
		BlameLine blameLine = consumer.getLines().get(0);
		Assert.assertNotNull(blameLine);
		Assert.assertEquals("89955f2b2bc0", blameLine.getRevision());
		Assert.assertEquals("Mark", blameLine.getAuthor());
		Assert.assertEquals("Mark", blameLine.getCommitter());
		
		blameLine = consumer.getLines().get(5);
		Assert.assertNotNull(blameLine);
		Assert.assertEquals("bb31deeb70a9", blameLine.getRevision());
		Assert.assertEquals("Another", blameLine.getAuthor());
		Assert.assertEquals("Another", blameLine.getCommitter());
	  }
	  
	  @Test
	  public void verbose_output() throws Exception {

		SonarHgBlameConsumer consumer = new SonarHgBlameConsumer(mock(ScmLogger.class));
		
		consumer.consumeLine("Mark Struberg <struberg@yahoo.de> 89955f2b2bc0 Mon Nov 24 15:05:47 2014 +0100: <?xml version=\"1.0\" encoding=\"UTF-8\"?><!--");
		consumer.consumeLine("Another User <another-email@struct.at> efc94016c544 Fri Nov 14 10:37:04 2014 +0100: Licensed to the Apache Software Foundation (ASF) under one");
		consumer.consumeLine("Another User <another-email@struct.at> efc94016c544 Fri Nov 14 10:37:04 2014 +0100: or more contributor license agreements.  See the NOTICE file");
		consumer.consumeLine(" Mark Struberg <struberg@yahoo.de> bb31deeb70a9 Thu Nov 20 17:33:07 2014 +0100: distributed with this work for additional information");
		consumer.consumeLine(" Mark Struberg <struberg@yahoo.de> bb31deeb70a9 Thu Nov 20 17:33:07 2014 +0100:");
		consumer.consumeLine(" Another User <another-email@struct.at> bb31deeb70a9 Thu Nov 20 17:33:07 2014 +0100: with the License.  You may obtain a copy of the License at");
		  
		Assert.assertEquals(6, consumer.getLines().size());
		BlameLine blameLine = consumer.getLines().get(0);
		Assert.assertNotNull(blameLine);
		Assert.assertEquals("89955f2b2bc0", blameLine.getRevision());
		Assert.assertEquals("Mark Struberg", blameLine.getAuthor());
		Assert.assertEquals("struberg@yahoo.de", blameLine.getCommitter());
		
		blameLine = consumer.getLines().get(5);
		Assert.assertNotNull(blameLine);
		Assert.assertEquals("bb31deeb70a9", blameLine.getRevision());
		Assert.assertEquals("Another User", blameLine.getAuthor());
		Assert.assertEquals("another-email@struct.at", blameLine.getCommitter());
	  }
}
