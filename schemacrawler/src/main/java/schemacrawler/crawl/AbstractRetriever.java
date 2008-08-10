/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.crawl;


import java.sql.Connection;

import schemacrawler.schema.DatabaseObject;

/**
 * Base class for retriever that uses database metadata to get the
 * details about the schema.
 * 
 * @author Sualeh Fatehi
 */
abstract class AbstractRetriever
{

  protected static final String COLUMN_NAME = "COLUMN_NAME";

  protected static final String DATA_TYPE = "DATA_TYPE";
  protected static final String KEY_SEQ = "KEY_SEQ";
  protected static final String NULLABLE = "NULLABLE";
  protected static final String ORDINAL_POSITION = "ORDINAL_POSITION";
  protected static final String REMARKS = "REMARKS";
  protected static final String TABLE_NAME = "TABLE_NAME";
  protected static final String TYPE_NAME = "TYPE_NAME";
  protected static final String UNKNOWN = "<unknown>";

  protected static final int FETCHSIZE = 5;

  private final RetrieverConnection retrieverConnection;

  AbstractRetriever()
  {
    this(null);
  }

  AbstractRetriever(final RetrieverConnection retrieverConnection)
  {
    this.retrieverConnection = retrieverConnection;
  }

  /**
   * Checks whether the provided database object belongs to the
   * specified schema.
   * 
   * @param dbObject
   *        Database object to check
   * @param catalog
   *        Database catalog to check against
   * @param schema
   *        Database schema to check against
   * @return Whether the database object belongs to the specified schema
   */
  protected boolean belongsToSchema(final DatabaseObject dbObject,
                                    final String catalog,
                                    final String schema)
  {
    if (dbObject == null)
    {
      return false;
    }

    boolean belongsToCatalog = true;
    boolean belongsToSchema = true;
    final String dbObjectCatalog = dbObject.getCatalogName();
    if (!(catalog == null || catalog.trim().length() == 0)
        && !(dbObjectCatalog == null || dbObjectCatalog.trim().length() == 0)
        && !catalog.equals(dbObjectCatalog))
    {
      belongsToCatalog = false;
    }
    final String dbObjectSchema = dbObject.getSchemaName();
    if (!(schema == null || schema.trim().length() == 0)
        && !(dbObjectSchema == null || dbObjectSchema.trim().length() == 0)
        && !schema.equals(dbObjectSchema))
    {
      belongsToSchema = false;
    }
    return belongsToCatalog && belongsToSchema;
  }

  protected Connection getDatabaseConnection()
  {
    return retrieverConnection.getConnection();
  }

  protected RetrieverConnection getRetrieverConnection()
  {
    return retrieverConnection;
  }

}
