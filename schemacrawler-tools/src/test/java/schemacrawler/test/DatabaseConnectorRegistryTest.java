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
package schemacrawler.test;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry.getDatabaseConnectorRegistry;

import java.util.List;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.DatabaseServerType;
import schemacrawler.tools.databaseconnector.DatabaseConnector;
import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class DatabaseConnectorRegistryTest {

  @Test
  public void databaseConnectorRegistry() {
    final DatabaseConnectorRegistry databaseConnectorRegistry = getDatabaseConnectorRegistry();
    final List<DatabaseServerType> databaseServerTypes =
        StreamSupport.stream(databaseConnectorRegistry.spliterator(), false).collect(toList());

    assertThat(databaseServerTypes, hasSize(1));
    assertThat(databaseConnectorRegistry.hasDatabaseSystemIdentifier("test-db"), is(true));

    final DatabaseConnector testDbConnector =
        databaseConnectorRegistry.findDatabaseConnectorFromDatabaseSystemIdentifier("test-db");
    assertThat(testDbConnector, is(notNullValue()));
    assertThat(
        testDbConnector.getDatabaseServerType().getDatabaseSystemIdentifier(), is("test-db"));

    final DatabaseConnector unknownConnector =
        databaseConnectorRegistry.findDatabaseConnectorFromDatabaseSystemIdentifier("newdb");
    assertThat(unknownConnector, is(notNullValue()));
    assertThat(
        unknownConnector.getDatabaseServerType().getDatabaseSystemIdentifier(), is(nullValue()));
  }
}
