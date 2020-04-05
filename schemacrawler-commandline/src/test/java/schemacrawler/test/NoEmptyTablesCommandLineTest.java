/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.test;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.test.utility.DatabaseConnectionInfo;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestDatabaseConnectionParameterResolver;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextDetailType;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.CommandlineTestUtility.commandlineExecution;
import static schemacrawler.test.utility.FileHasContent.*;
import static schemacrawler.test.utility.TestUtility.clean;

@ExtendWith(TestDatabaseConnectionParameterResolver.class)
@ExtendWith(TestContextParameterResolver.class)
public class NoEmptyTablesCommandLineTest
{

  private static final String HIDE_EMPTY_TABLES_OUTPUT =
    "no_empty_tables_output/";

  @Test
  public void noEmptyTables(final TestContext testContext,
                            final DatabaseConnectionInfo connectionInfo)
    throws Exception
  {
    clean(HIDE_EMPTY_TABLES_OUTPUT);

    final String referenceFile = testContext.testMethodName() + ".txt";

    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("-info-level", InfoLevel.maximum.name());
    argsMap.put("-no-info", "true");
    argsMap.put("-load-row-counts", "true");
    argsMap.put("-no-empty-tables", "true");

    assertThat(outputOf(commandlineExecution(connectionInfo,
                                             SchemaTextDetailType.schema.name(),
                                             argsMap,
                                             TextOutputFormat.text)),
               hasSameContentAs(classpathResource(
                 HIDE_EMPTY_TABLES_OUTPUT + referenceFile)));
  }

}
