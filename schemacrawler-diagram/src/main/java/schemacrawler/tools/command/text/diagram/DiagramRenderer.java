/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.text.diagram;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat.scdot;
import static us.fatehi.utility.IOUtility.createTempFilePath;
import static us.fatehi.utility.IOUtility.readResourceFully;

import java.nio.file.Path;

import schemacrawler.schemacrawler.exceptions.SchemaCrawlerRuntimeException;
import schemacrawler.tools.command.text.diagram.options.DiagramOptions;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import schemacrawler.tools.text.formatter.diagram.SchemaDotFormatter;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.tools.traversal.SchemaTraverser;
import schemacrawler.utility.NamedObjectSort;

public final class DiagramRenderer extends BaseSchemaCrawlerCommand<DiagramOptions> {

  private DiagramOutputFormat diagramOutputFormat;
  private final GraphExecutorFactory graphExecutorFactory;

  public DiagramRenderer(final String command, final GraphExecutorFactory graphExecutorFactory) {
    super(command);
    this.graphExecutorFactory =
        requireNonNull(graphExecutorFactory, "No graph executor factory provided");
  }

  @Override
  public void checkAvailability() {
    graphExecutorFactory.canGenerate(diagramOutputFormat);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() throws Exception {
    checkCatalog();

    // Set the format, in case we are using the default
    outputOptions =
        OutputOptionsBuilder.builder(outputOptions)
            .withOutputFormat(diagramOutputFormat)
            .withOutputFormatValue(diagramOutputFormat.getFormat())
            .toOptions();

    // Create dot file
    final Path dotFile = createTempFilePath("schemacrawler.", "dot");
    final OutputOptions dotFileOutputOptions;
    if (diagramOutputFormat == scdot) {
      dotFileOutputOptions = outputOptions;
    } else {
      dotFileOutputOptions =
          OutputOptionsBuilder.builder(outputOptions)
              .withOutputFormat(scdot)
              .withOutputFile(dotFile)
              .toOptions();
    }

    final SchemaTraversalHandler formatter = getSchemaTraversalHandler(dotFileOutputOptions);

    final SchemaTraverser traverser = new SchemaTraverser();
    traverser.setCatalog(catalog);
    traverser.setHandler(formatter);
    traverser.setTablesComparator(
        NamedObjectSort.getNamedObjectSort(commandOptions.isAlphabeticalSortForTables()));
    traverser.setRoutinesComparator(
        NamedObjectSort.getNamedObjectSort(commandOptions.isAlphabeticalSortForRoutines()));

    traverser.traverse();

    // Set the format, in case we are using the default
    final Path outputFile = outputOptions.getOutputFile(outputOptions.getOutputFormatValue());
    outputOptions =
        OutputOptionsBuilder.builder(outputOptions)
            .withOutputFormat(diagramOutputFormat)
            .withOutputFormatValue(diagramOutputFormat.getFormat())
            .withOutputFile(outputFile)
            .toOptions();

    try {
      final GraphExecutor graphExecutor =
          graphExecutorFactory.getGraphExecutor(
              dotFile, diagramOutputFormat, outputFile, commandOptions);
      graphExecutor.run();
    } catch (final Exception e) {
      final String message = readResourceFully("/dot.error.txt");
      throw new SchemaCrawlerRuntimeException(message);
    }
  }

  @Override
  public void initialize() throws Exception {
    super.initialize();
    diagramOutputFormat = DiagramOutputFormat.fromFormat(outputOptions.getOutputFormatValue());
  }

  @Override
  public boolean usesConnection() {
    return false;
  }

  private SchemaTextDetailType getSchemaTextDetailType() {
    SchemaTextDetailType schemaTextDetailType;
    try {
      schemaTextDetailType = SchemaTextDetailType.valueOf(command);
    } catch (final IllegalArgumentException e) {
      schemaTextDetailType = null;
    }
    return schemaTextDetailType;
  }

  private SchemaTraversalHandler getSchemaTraversalHandler(final OutputOptions outputOptions) {
    final SchemaTraversalHandler formatter;
    final SchemaTextDetailType schemaTextDetailType = getSchemaTextDetailType();

    final String identifierQuoteString = identifiers.getIdentifierQuoteString();
    formatter =
        new SchemaDotFormatter(
            schemaTextDetailType, commandOptions, outputOptions, identifierQuoteString);

    return formatter;
  }
}
