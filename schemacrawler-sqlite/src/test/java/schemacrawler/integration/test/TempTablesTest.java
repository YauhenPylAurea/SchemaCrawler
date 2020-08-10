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

package schemacrawler.integration.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.Connection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.test.utility.BaseSqliteTest;
import schemacrawler.test.utility.TestLoggingExtension;
import schemacrawler.testdb.SqlScript;
import schemacrawler.testdb.TestSchemaCreatorMain;
import schemacrawler.utility.SchemaCrawlerUtility;
import us.fatehi.utility.IOUtility;

@ExtendWith(TestLoggingExtension.class)
public class TempTablesTest
  extends BaseSqliteTest
{

  @Test
  public void tempTables()
    throws Exception
  {
    final Path sqliteDbFile = IOUtility
      .createTempFilePath("sc", ".db")
      .normalize()
      .toAbsolutePath();

    TestSchemaCreatorMain.call("--url", "jdbc:sqlite:" + sqliteDbFile);
    final Connection connection = executeSqlInTestDatabase(sqliteDbFile,
                                                           "/db/books/05_temp_tables_01_B.sql");

    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder
      .builder()
      .tableTypes("GLOBAL TEMPORARY");
    final LoadOptionsBuilder loadOptionsBuilder = LoadOptionsBuilder
      .builder()
      .withSchemaInfoLevel(SchemaInfoLevelBuilder.minimum());
    final SchemaCrawlerOptionsBuilder schemaCrawlerOptionsBuilder =
      SchemaCrawlerOptionsBuilder
        .builder()
        .withLimitOptionsBuilder(limitOptionsBuilder)
        .withLoadOptionsBuilder(loadOptionsBuilder);
    final SchemaCrawlerOptions schemaCrawlerOptions =
      schemaCrawlerOptionsBuilder.toOptions();

    final Catalog catalog =
      SchemaCrawlerUtility.getCatalog(connection, schemaCrawlerOptions);
    final Schema[] schemas = catalog
      .getSchemas()
      .toArray(new Schema[0]);
    assertThat("Schema count does not match", schemas, is(arrayWithSize(1)));
    final Table[] tables = catalog
      .getTables(schemas[0])
      .toArray(new Table[0]);
    assertThat("Table count does not match", tables, is(arrayWithSize(1)));
    final Table table = tables[0];
    assertThat("Table name does not match",
               table.getFullName(),
               is("TEMP_AUTHOR_LIST"));
  }

  protected Connection executeSqlInTestDatabase(final Path sqliteDbFile,
                                                final String databaseSqlResource)
    throws Exception
  {
    final DataSource dataSource = createDataSource(sqliteDbFile);

    final Connection connection = dataSource.getConnection();
    connection.setAutoCommit(false);

    final SqlScript sqlScript = new SqlScript(databaseSqlResource, connection);
    sqlScript.run();

    return connection;
  }

}
