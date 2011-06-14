package uk.ac.sanger.artemis.components.variant;

import java.util.EnumSet;

public class VariantFilterOptions {
	
	public static final int defaultFilter = 63;
	private IntEnumPatternResolver filterStore = new IntEnumPatternResolver();
	
	/**
	 * The integer passed here should be a sum of the VariantFilterOption ordinal values (each to the power of 2). If
	 * the integer is null, then the defaultFilter value will be used.  
	 * @param filter
	 */
	public VariantFilterOptions(Integer filter) {
		
		if (filter == null) {
			filter = defaultFilter;
		}
		
		filterStore.set(filter);
		//this.filter = filter;
	}
	
	/**
	 * An enumset containing each of the options. 
	 * @param options
	 */
	public VariantFilterOptions(EnumSet<VariantFilterOption> options) {
		//filter = 1;
		for (VariantFilterOption option : options) {
			enable(option.index());
			//add(option.index());
		}
	}
	
	
	
	public void enable(final int flag) {
		filterStore.enable(flag);
		//filter |= flag;
    }
	
	public void disable(final int flag) {
		filterStore.disable(flag);
    }

	
	public boolean isEnabled(VariantFilterOption option) {
		return filterStore.isEnabled(option.index());
		//return (option.index() & filter) > 0;
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer("[");
		String sep = "";
		for (VariantFilterOption opt : VariantFilterOption.values()) {
			s.append(sep);
			s.append(opt.index());
			s.append(" : ");
			s.append(opt.name());
			s.append(" - enabled? ");
			s.append(isEnabled(opt));
			sep = ",";
		}
		s.append("]");
		return s.toString();
	}
	
}
