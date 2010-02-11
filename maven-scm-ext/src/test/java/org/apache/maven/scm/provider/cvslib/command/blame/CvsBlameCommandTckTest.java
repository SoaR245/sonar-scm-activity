/*
 * Copyright (C) 2010 Evgeny Mandrikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.maven.scm.provider.cvslib.command.blame;

import org.apache.maven.scm.ExtScmTckTestCase;
import org.apache.maven.scm.command.blame.BlameLine;
import org.apache.maven.scm.command.blame.BlameScmResult;
import org.apache.maven.scm.provider.cvslib.CvsScmTestUtils;

import java.util.List;

/**
 * @author Evgeny Mandrikov
 */
public abstract class CvsBlameCommandTckTest extends ExtScmTckTestCase {
  @Override
  public String getScmUrl() throws Exception {
    return CvsScmTestUtils.getScmUrl(getRepositoryRoot(), getModule());
  }

  @Override
  protected String getModule() {
    return "test-repo/module";
  }

  @Override
  public void initRepo() throws Exception {
    CvsScmTestUtils.initRepo("src/test/tck-repository/", getRepositoryRoot(), getWorkingDirectory());
  }

  protected void testBlameCommand() throws Exception {
    BlameScmResult result = getScmManager().blame(
        getScmRepository(),
        getScmFileSet(),
        "pom.xml"
    );
    assertResultIsSuccess(result);

    List<BlameLine> lines = result.getLines();
    int size = lines.size();
    assertEquals(1, size);
    assertEquals("1.1", lines.get(0).getRevision());
    assertEquals("Brett", lines.get(0).getAuthor());
  }

}