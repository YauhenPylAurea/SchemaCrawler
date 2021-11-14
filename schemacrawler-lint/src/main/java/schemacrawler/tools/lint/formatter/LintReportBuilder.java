package schemacrawler.tools.lint.formatter;

import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.lint.LintReport;

public interface LintReportBuilder {

  void generateLintReport(LintReport report) throws SchemaCrawlerException;
}
