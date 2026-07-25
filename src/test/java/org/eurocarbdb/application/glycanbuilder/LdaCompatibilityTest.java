package org.eurocarbdb.application.glycanbuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eurocarbdb.application.glycanbuilder.renderutil.GlycanRendererAWT;
import org.eurocarbdb.application.glycanbuilder.renderutil.SVGUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class LdaCompatibilityTest {

	private static final String GSL_GLOBO =
			"Cer--?b1D-Glc,p--4b1D-Gal,p--4a1D-Gal,p--3b1D-GalNAc,p";

	@BeforeClass
	public static void initializeDictionaries() {
		new BuilderWorkspace("/config.xml", true, new GlycanRendererAWT());
	}

	@Test
	public void parsesAndRoundTripsLdaGlycanStructure() {
		Glycan glycan = Glycan.fromString(GSL_GLOBO);
		assertNotNull(glycan);
		assertEquals(glycan.toStringOrdered(), Glycan.fromString(glycan.toString()).toStringOrdered());
		assertEquals("Gal2_GalNAc_Glc_Cer", new GlycanShorthandConverterLDA().buildGlycanShorthand(glycan));
		assertFalse(new Fragmenter().computeAllFragments(glycan).getFragments().isEmpty());
	}

	@Test
	public void serializesConfigurationWithJavaXml() throws Exception {
		Configuration configuration = new Configuration();
		configuration.put("java21", "value", "ok");

		Document document = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder()
				.newDocument();
		Element root = configuration.toXML(document);

		Configuration roundTrip = new Configuration();
		roundTrip.fromXML(root);
		assertEquals("ok", roundTrip.get("java21", "value"));
	}

	@Test
	public void exportsVectorGraphicsWithoutShadedJavaXmlClasses() {
		Glycan glycan = Glycan.fromString(GSL_GLOBO);
		GlycanRendererAWT renderer = new GlycanRendererAWT();

		String svg = SVGUtils.getVectorGraphics(renderer, List.of(glycan));
		assertNotNull(svg);
		assertTrue(svg.contains("<svg"));
	}

	@Test
	public void embeddedBuilderDoesNotChangeApplicationSwingDefaults() throws Exception {
		boolean frameDecorated = JFrame.isDefaultLookAndFeelDecorated();
		boolean dialogDecorated = JDialog.isDefaultLookAndFeelDecorated();
		UIDefaults defaults = UIManager.getDefaults();
		Object oldTooltipSetting = defaults.get("ToolTip.hideAccelerator");
		boolean hadTooltipSetting = defaults.containsKey("ToolTip.hideAccelerator");
		Object marker = new Object();
		try {
			JFrame.setDefaultLookAndFeelDecorated(false);
			JDialog.setDefaultLookAndFeelDecorated(false);
			defaults.put("ToolTip.hideAccelerator", marker);

			new GlycanBuilder(null);

			assertFalse(JFrame.isDefaultLookAndFeelDecorated());
			assertFalse(JDialog.isDefaultLookAndFeelDecorated());
			assertTrue(defaults.get("ToolTip.hideAccelerator") == marker);
		} finally {
			JFrame.setDefaultLookAndFeelDecorated(frameDecorated);
			JDialog.setDefaultLookAndFeelDecorated(dialogDecorated);
			if (hadTooltipSetting)
				defaults.put("ToolTip.hideAccelerator", oldTooltipSetting);
			else
				defaults.remove("ToolTip.hideAccelerator");
		}
	}
}
