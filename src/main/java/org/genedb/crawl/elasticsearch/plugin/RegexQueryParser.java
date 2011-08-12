package org.genedb.crawl.elasticsearch.plugin;

import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.regex.JakartaRegexpCapabilities;
import org.apache.lucene.search.regex.JavaUtilRegexCapabilities;
import org.apache.lucene.search.regex.RegexQuery;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.index.AbstractIndexComponent;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.query.QueryParsingException;
import org.elasticsearch.index.query.QueryParseContext;
import org.elasticsearch.index.query.QueryParser;

public class RegexQueryParser extends AbstractIndexComponent
		implements QueryParser {

	@Inject protected RegexQueryParser(Index index, Settings indexSettings) {
		super(index, indexSettings);
	}

	private String[] names = new String[] { "regex" };

	@Override
	public String[] names() {
		return names;
	}

	@Override
	public Query parse(QueryParseContext parseContext) throws IOException,
			QueryParsingException {

		XContentParser parser = parseContext.parser();

		XContentParser.Token token = parser.nextToken();
        assert token == XContentParser.Token.FIELD_NAME;
        String fieldName = parser.currentName();

        String value = null;
        float boost = 1.0f;
        
        token = parser.nextToken();
        if (token == XContentParser.Token.START_OBJECT) {
            String currentFieldName = null;
            while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
                if (token == XContentParser.Token.FIELD_NAME) {
                    currentFieldName = parser.currentName();
                } else {
                    if ("term".equals(currentFieldName)) {
                        value = parser.text();
                    } else if ("value".equals(currentFieldName)) {
                        value = parser.text();
                    } else if ("boost".equals(currentFieldName)) {
                        boost = parser.floatValue();
                    }
                }
            }
            parser.nextToken();
        } else {
            value = parser.text();
            // move to the next token
            parser.nextToken();
        }

        if (value == null) {
            throw new QueryParsingException(index, "No value specified for regex query");
        }

        RegexQuery query = new RegexQuery(new Term(fieldName, value));
        
        //JakartaRegexpCapabilities capabilities = new JakartaRegexpCapabilities();
        JavaUtilRegexCapabilities capabilites = 
        	new JavaUtilRegexCapabilities(
        			JavaUtilRegexCapabilities.FLAG_CASE_INSENSITIVE + 
        			JavaUtilRegexCapabilities.FLAG_DOTALL);
        query.setRegexImplementation(capabilites);
        
        query.setBoost(boost);
        
        
		logger.info(fieldName + ":" + value);
		logger.info("???");
		logger.info(query.getRegexImplementation().getClass().toString());
		logger.info("___");
		
		logger.info("terms number : " + query.getTotalNumberOfTerms());
		logger.info("rewrite method : " + query.getRewriteMethod());
		
        return query;

	}

}