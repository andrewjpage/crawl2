package org.genedb.crawl.elasticsearch.plugin;


import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.query.IndexQueryParserModule;
import org.elasticsearch.plugins.AbstractPlugin;

public class RegexPlugin extends AbstractPlugin {

	@Override
	public String description() {
		return "a regex query plugin";
	}

	@Override
	public String name() {
		return "regex";
	}

	@Override
	public void processModule(Module module) {
		if (module instanceof IndexQueryParserModule) {
			IndexQueryParserModule queryParserModule = ((IndexQueryParserModule) module);
			// imodule.addProcessor(null);

			queryParserModule
					.addProcessor(new IndexQueryParserModule.QueryParsersProcessor() {
						@Override
						public void processXContentQueryParsers(
								XContentQueryParsersBindings bindings) {
							bindings.processXContentQueryParser("regex",
									RegexQueryParser.class);
						}
					});

		}
	}

}
