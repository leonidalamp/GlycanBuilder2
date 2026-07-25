package org.eurocarbdb.application.glycanbuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eurocarbdb.application.glycanbuilder.linkage.Linkage;

/**
 * Builds the residue-composition shorthand used by Lipid Data Analyzer.
 */
public class GlycanShorthandConverterLDA {

	public String buildGlycanShorthand(Glycan structure) {
		List<Residue> residues = new ArrayList<Residue>();
		if (structure.getRoot() != null)
			residues.addAll(collectChildResidues(structure.getRoot()));

		Map<String, Integer> residueCounts = new TreeMap<String, Integer>();
		for (Residue residue : residues) {
			String residueName = residue.getType().getName();
			residueCounts.put(residueName, residueCounts.getOrDefault(residueName, 0) + 1);
		}

		List<String> residueTypes = new ArrayList<String>(residueCounts.keySet());
		Collections.sort(residueTypes);
		if (residueTypes.remove("Cer"))
			residueTypes.add("Cer");

		StringBuilder shorthand = new StringBuilder();
		for (int i = 0; i < residueTypes.size(); i++) {
			String residueType = residueTypes.get(i);
			shorthand.append(residueType);
			int count = residueCounts.get(residueType);
			if (count > 1)
				shorthand.append(count);
			if (i < residueTypes.size() - 1)
				shorthand.append('_');
		}
		return shorthand.toString();
	}

	private List<Residue> collectChildResidues(Residue residue) {
		List<Residue> residues = new ArrayList<Residue>();
		residues.add(residue);
		for (Linkage linkage : residue.getChildrenLinkages())
			residues.addAll(collectChildResidues(linkage.getChildResidue()));
		return residues;
	}
}
