/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.integration.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.ExecutableTestUtility.executableExecution;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.DatabaseTestUtility;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestLoggingExtension;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;

@ExtendWith(TestLoggingExtension.class)
@ExtendWith(TestContextParameterResolver.class)
public class ForeignKeyWithoutReferencedPrimaryKeyTest extends BaseSqliteTest {

  @Test
  public void foreignKeyWithoutReferencedPrimaryKey(final TestContext testContext)
      throws Exception {
    run(testContext.testMethodName(), "/foreignKeyWithoutReferencedPrimaryKey.sql", "schema");
  }

  private void run(
      final String currentMethodName, final String databaseSqlResource, final String command)
      throws Exception {

    final DataSource dataSource = createDatabaseInMemoryFromScript(databaseSqlResource);

    final SchemaCrawlerOptions options =
        DatabaseTestUtility.schemaCrawlerOptionsWithMaximumSchemaInfoLevel;

    final SchemaTextOptions textOptions = SchemaTextOptionsBuilder.newSchemaTextOptions();

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable.setAdditionalConfiguration(SchemaTextOptionsBuilder.builder(textOptions).toConfig());

    assertThat(
        outputOf(executableExecution(dataSource.getConnection(), executable)),
        hasSameContentAs(classpathResource(currentMethodName)));
  }
}
