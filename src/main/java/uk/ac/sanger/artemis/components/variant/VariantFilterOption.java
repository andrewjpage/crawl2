package uk.ac.sanger.artemis.components.variant;

public enum VariantFilterOption {
	SHOW_SYNONYMOUS,
	SHOW_NON_SYNONYMOUS,
	SHOW_DELETIONS ,
	SHOW_INSERTIONS ,
	SHOW_MULTI_ALLELES,
	SHOW_NON_OVERLAPPINGS,
	SHOW_NON_VARIANTS,
	MARK_NEW_STOPS ;
	
	/**
	 * Enum ordinals being a simple zero based index, this transforms them into the ^2 based index needed
	 * for bitwise operations.
	 * @return
	 */
	public int index() {
		return 1 << ordinal();
	}
	
}