package org.eurocarbdb.application.glycanbuilder.converter.converterLDA;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

import org.eurocarbdb.application.glycanbuilder.Configuration;
import org.eurocarbdb.application.glycanbuilder.FragmentCollection;
import org.eurocarbdb.application.glycanbuilder.FragmentEntry;
import org.eurocarbdb.application.glycanbuilder.FragmentOptions;
import org.eurocarbdb.application.glycanbuilder.Fragmenter;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.Residue;
import org.eurocarbdb.application.glycanbuilder.linkage.Linkage;
import org.eurocarbdb.application.glycanbuilder.massutil.Molecule;

/**
	Read and write glycan structures in the LDA internal
	format.
	
	@author Leonida M. Lamp (leonida.lamp@uni-graz.at)
*/

public class LDAParser
{
	private boolean writePos = false;
	private String filePath = "C:\\Users\\leoni\\Development\\Kathi\\Paper3\\fragRulesExport\\";
	private Glycan structure;
	private String glycanName = "";
	private String[] posIonMode = new String[]{"H", "2H", "3H"};
	private String[] negIonMode = new String[]{"-H", "-2H", "-3H"};
	
	public LDAParser (boolean writePos, Glycan structure)
	{
		this.writePos = writePos;
		this.structure = structure;
		this.glycanName = buildGlycanName(structure);
	}
	
	private String buildGlycanName(Glycan structure)
	{
		StringBuilder sb = new StringBuilder();
		ArrayList<Residue> allResidues = new ArrayList<Residue>();
		if( structure.getRoot()!=null ) {
			allResidues.addAll(collectChildResidues(structure.getRoot()));
//			if( structure.getBracket()!=null ) 
//				allResidues.addAll(collectChildResidues(structure.getBracket()));
		}
		TreeMap<String,Integer> residueCount = new TreeMap<String,Integer>();
		for (Residue res : allResidues)
		{
			String resDesc = res.getType().getMSDefaultDescriptor();
			if (!residueCount.containsKey(resDesc)) residueCount.put(resDesc, 0);
			residueCount.put(resDesc, residueCount.get(resDesc)+1);
		}
		String fragment = buildFragmentShorthand(residueCount);
		ArrayList<String> resTypes = new ArrayList(residueCount.keySet());
		Collections.sort(resTypes);
		if (resTypes.contains("Cer")) //ensuring Cer is at the end
		{
			resTypes.remove("Cer");
			resTypes.add("Cer");
		}
		if (fragment != null) //ensuring fragment is at the beginnign
		{
			sb.append(fragment);
			sb.append("_");
		}
		
		for (int i=0;i<resTypes.size();i++)
		{
			String res = resTypes.get(i);
			sb.append(res);
			sb.append(residueCount.get(res) > 1 ? residueCount.get(res) : "");
			if (i<resTypes.size()-1)
				sb.append("_");
		}
		return sb.toString();
	}
	
	private String buildFragmentShorthand(TreeMap<String,Integer> residueCount)
	{
		String b = "#bcleavage";
		String c = "#ccleavage";
		String y = "#ycleavage";
		String z = "#zcleavage";
		String fragment = null;
		if (residueCount.containsKey(b) || 
				residueCount.containsKey(c) || 
				residueCount.containsKey(y) || 
				residueCount.containsKey(z))
		{
			int w = 0;
			if (residueCount.containsKey(b))
			{
				w = w+residueCount.get(b);
				residueCount.remove(b);
			}
			if (residueCount.containsKey(c)) residueCount.remove(c);
			if (residueCount.containsKey(y)) residueCount.remove(y);
			if (residueCount.containsKey(z))
			{
				w = w+residueCount.get(z);
				residueCount.remove(z);
			}
			fragment = "W"+w;
		}
		return fragment;
	}
	
	private ArrayList<Residue> collectChildResidues(Residue r)
	{
		ArrayList<Residue> children = new ArrayList<Residue>();
		children.add(r);
		for( Linkage l : r.getChildrenLinkages() )
			children.addAll(collectChildResidues(l.getChildResidue()));
		return children;
	}
	
	public void writeGlycan()
	{
		String[] ionMode = writePos ? posIonMode : negIonMode;
		for (int i=0; i<ionMode.length; i++)
		{
			String fileName = String.format("%s%s\\%s_%s.%s", filePath, writePos ? "pos" : "neg", glycanName,ionMode[i],"frag.txt");
			try (FileOutputStream out= new FileOutputStream(fileName);)
			{
				out.write(buildGeneralSettings().getBytes());
				out.write(buildHeadHeader().getBytes());
				
				out.write(buildGlycanFragRules().getBytes());
				if (writePos)
					out.write(buildChainRulesPositive().getBytes());
				else
					out.write(buildChainRulesNegative().getBytes());
				
			}
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		}
	}
	
	private String buildGlycanFragRules()
	{
		StringBuilder sb = new StringBuilder();
		Configuration config = new Configuration();
		config.open("src/main/resources/config.xml");
		FragmentOptions opt = new FragmentOptions();
		opt.retrieve(config);
		Fragmenter frag = new Fragmenter(opt);
		FragmentCollection fragColl = frag.computeAllFragments(structure);
		
		ArrayList<String> fragmentNames = new ArrayList();
		for (FragmentEntry entry : fragColl.getFragments())
		{
			if (entry.getStructure().contains("Cer")) continue;
			try {
//				String fragmentName = buildLDAFragmentAnnotation(entry.getFragment());
				String fragmentName = buildGlycanName(entry.getFragment());
				if (fragmentNames.contains(fragmentName)) continue;
				else fragmentNames.add(fragmentName);
				Molecule molecule = entry.getFragment().computeMolecule();
				
				if (writePos)
					molecule.add("H");
				else
					molecule.remove("H");
				String formula = molecule.toString();
				sb.append(String.format("Name=%s \t Formula=%s \t Charge=1 \t MSLevel=2 \t mandatory=false\n", fragmentName, formula));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for (FragmentEntry entry : fragColl.getFragments())
		{
			if (!entry.getStructure().contains("Cer")) continue;
			try {
//				String fragmentName = buildLDAFragmentAnnotation(entry.getFragment());
				String fragmentName = buildGlycanName(entry.getFragment());
				if (fragmentNames.contains(fragmentName) || fragmentName.equals(glycanName)) continue;
				else fragmentNames.add(fragmentName);
				Molecule molecule = structure.computeMolecule();
				molecule.remove(entry.getFragment().computeMolecule());
				String formula = String.format("%s-%s", "$PRECURSOR", molecule.toString());
				sb.append(String.format("Name=%s \t Formula=%s \t Charge=1 \t MSLevel=2 \t mandatory=false\n", fragmentName, formula));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		sb.append("\n\n\n");
		return sb.toString();
	}
	
	private String buildGeneralSettings()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[GENERAL]\n");
		sb.append("AmountOfChains=2\n");
		sb.append("AmountOfLCBs=1\n");
		sb.append("ChainLibrary=fattyAcidChains.xlsx\n");
		sb.append("LCBLibrary=dSPB_Ganglio.xlsx	\n");
		sb.append("CAtomsFromName=\\D*(\\d+):\\d+\n");
		sb.append("DoubleBondsFromName=\\D*\\d+:(\\d+)\n");
		sb.append("ChainCutoff=50%\n");
		sb.append("FaHydroxylationRange=0-1\n");
		sb.append("LcbHydroxylationRange=2-3\n");
		sb.append("RetentionTimePostprocessing=true\n");
		sb.append("SingleChainIdentification=false\n");
		sb.append("\n\n\n");
		return sb.toString();
	}
	
	private String buildHeadHeader()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[HEAD]\n");
		sb.append("!FRAGMENTS\n");
		return sb.toString();
	}
	
	private String buildChainRulesNegative()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[CHAINS]\n");
		sb.append("!FRAGMENTS\n");
		sb.append("Name=FA_FA \t Formula=$CHAIN \t Charge=1 \t MSLevel=2 \t mandatory=false\n");
		sb.append("Name=FA-H_FA-1 \t Formula=$CHAIN-H \t Charge=1 \t MSLevel=2 \t mandatory=false\n");
		sb.append("Name=FA-H2O_FA-18 \t Formula=$CHAIN-H2O \t Charge=1 \t MSLevel=2 \t mandatory=false\n");
		sb.append("Name=FA-H3O_FA-19 \t Formula=$CHAIN-H3O \t Charge=1 \t MSLevel=2 \t mandatory=false\n");
		sb.append("Name=LCB-H2O_LCB-18 \t Formula=$LCB-H3O \t Charge=1 \t MSLevel=2 \t mandatory=false\n");
		sb.append("Name=LCB-2H2O_LCB-36 \t Formula=$LCB-H5O2 \t Charge=1 \t MSLevel=2 \t mandatory=false\n");
		sb.append("Name=LCB-H6ON_LCB-36 \t Formula=$LCB-H6ON \t Charge=1 \t MSLevel=2 \t mandatory=false \t oh=2\n");
		sb.append("Name=LCB-CH4O2_LCB-48 \t Formula=$LCB-CH5O2 \t Charge=1 \t MSLevel=2 \t mandatory=false\n");
		sb.append("Name=LCB-H6ON_LCB-50 \t Formula=$LCB-CH8ON \t Charge=1 \t MSLevel=2 \t mandatory=false \t oh=3\n");
		sb.append("Name=G \t Formula=$PRECURSOR-$CHAIN+CH2NO \t Charge=1 \t MSLevel=2 \t mandatory=false\n");
		sb.append("Name=G-NeuAc_G-291 \t Formula=$PRECURSOR-$CHAIN+CH2NO-C11H17NO8 \t Charge=1 \t MSLevel=2 \t mandatory=false\n");
		sb.append("\n\n\n");
		sb.append("!INTENSITIES\n");
		sb.append("Equation=G|G-NeuAc_G-291|G-NeuAc-NeuAc_G-582 \t mandatory=true\n");
		sb.append("\n\n\n");
		sb.append("[POSITION]\n");
		sb.append("!INTENSITIES\n");
		sb.append("Equation=G[2]>0*$BASEPEAK \t mandatory=true\n");
		return sb.toString();
	}
	
	private String buildChainRulesPositive()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[CHAINS]\n");
		sb.append("!FRAGMENTS\n");
		sb.append("Name=FA_FA \t Formula=$CHAIN \t Charge=1 \t MSLevel=2 \t mandatory=false\n");
		sb.append("Name=FA-H2O_FA-18 \t Formula=$CHAIN-H2O \t Charge=1 \t MSLevel=2 \t mandatory=false\n");
		sb.append("Name=LCB-H2O_LCB-18 \t Formula=$LCB-HO \t Charge=1 \t MSLevel=2 \t mandatory=false \t oh=1=class,2,3=true\n");
		sb.append("Name=LCB-2H2O_LCB-36 \t Formula=$LCB-H3O2 \t Charge=1 \t MSLevel=2 \t mandatory=false \t oh=2=class,3=class\n");
		sb.append("Name=LCB-3H2O_LCB-54 \t Formula=$LCB-H5O3 \t Charge=1 \t MSLevel=2 \t mandatory=false \t oh=3\n");
		sb.append("Name=LCB-C1H2O_LCB-30 \t Formula=$LCB-C1H3O2 \t Charge=1 \t MSLevel=2 \t mandatory=false \t oh=2,3\n");
		sb.append("Name=LCB-CH4O2_LCB-48 \t Formula=$LCB-CH3O2 \t Charge=1 \t MSLevel=2 \t mandatory=false\n");
		sb.append("Name=LCB-CH5O3_LCB-65 \t Formula=$LCB-CH5O3 \t Charge=1 \t MSLevel=2 \t mandatory=false \t oh=3\n");
		sb.append("Name=NL_LCB_M-LCB \t Formula=$PRECURSOR-$LCB \t Charge=1 \t MSLevel=2 \t mandatory=false \t oh=1\n");
		sb.append("Name=NL_LCB+H2O_M-LCB+H2O \t Formula=$PRECURSOR-$LCB+H2O \t Charge=1 \t MSLevel=2 \t mandatory=false \t oh=1\n");
		return sb.toString();
	}		
	
	
}
