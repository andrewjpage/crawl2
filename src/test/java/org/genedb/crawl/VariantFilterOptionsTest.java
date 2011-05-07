package org.genedb.crawl;

import java.util.EnumSet;

import org.apache.log4j.Logger;

import uk.ac.sanger.artemis.components.variant.VariantFilterOption;
import uk.ac.sanger.artemis.components.variant.VariantFilterOptions;

import junit.framework.TestCase;

public class VariantFilterOptionsTest extends TestCase {
	
	private static Logger logger = Logger.getLogger(VariantFilterOptionsTest.class);
	
	public void test1() {
		
		int hex1 = VariantFilterOption.SHOW_INSERTIONS.index();
		EnumSet<VariantFilterOption> options1 = EnumSet.of(VariantFilterOption.SHOW_INSERTIONS);
		
		checkOptions(hex1, options1);
		checkOptionsSet(options1);
		
		int hex2 = VariantFilterOption.SHOW_INSERTIONS.index() + VariantFilterOption.SHOW_NON_OVERLAPPINGS.index();
		EnumSet<VariantFilterOption> options2 = EnumSet.of(VariantFilterOption.SHOW_INSERTIONS, VariantFilterOption.SHOW_NON_OVERLAPPINGS);
		
		checkOptions(hex2,options2);
		checkOptionsSet(options2);
		
	}
	
	private void checkOptions(int hex, EnumSet<VariantFilterOption> option_set) {
		VariantFilterOptions options = new VariantFilterOptions(hex);
		
		for (VariantFilterOption option : VariantFilterOption.values()) {
			logger.info(String.format("%s : %s", option.name(), options.isEnabled(option)));
			
			if (option.index() == hex) {
				assertTrue(options.isEnabled(option));
				assertTrue(option_set.contains(option));
				assertEquals(option_set.size(), 1);
				logger.info(option + " was the only option specified by in filter and found");
			}
			else if ((option.index() & hex) > 0) {
				assertTrue(options.isEnabled(option));
				assertTrue(option_set.contains(option));
				logger.info(option + " was specified by in filter and found");
			} else {
				assertFalse(options.isEnabled(option));
			}
			
		}
	}
	
	private void checkOptionsSet(EnumSet<VariantFilterOption> option_set) {
		VariantFilterOptions options = new VariantFilterOptions(option_set);
		
		for (VariantFilterOption option : VariantFilterOption.values()) {
			logger.info(String.format("%s : %s", option.name(), options.isEnabled(option)));
			
			if (option_set.contains(option)) {
				assertTrue(options.isEnabled(option));
				logger.info(option + " was specified by in filter and found");
			} else {
				assertFalse(options.isEnabled(option));
			}
			
		}
	}
	
}
