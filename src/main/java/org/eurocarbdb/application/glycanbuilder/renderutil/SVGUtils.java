/*
*   EuroCarbDB, a framework for carbohydrate bioinformatics
*
*   Copyright (c) 2006-2009, Eurocarb project, or third-party contributors as
*   indicated by the @author tags or express copyright attribution
*   statements applied by the authors.  
*
*   This copyrighted material is made available to anyone wishing to use, modify,
*   copy, or redistribute it subject to the terms and conditions of the GNU
*   Lesser General Public License, as published by the Free Software Foundation.
*   A copy of this license accompanies this distribution in the file LICENSE.txt.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
*   or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
*   for more details.
*
*   Last commit: $Rev$ by $Author$ on $Date::             $  
*/

package org.eurocarbdb.application.glycanbuilder.renderutil;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.*;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.logutility.LogUtils;
import org.eurocarbdb.application.glycanbuilder.util.GraphicUtils;

/**
   Utility class containing functions to export glycan structures and
   other renderable objects into graphic formats.

   @author Alessio Ceroni (a.ceroni@imperial.ac.uk)
*/

public class SVGUtils   {  
  
    /**
       Interface that must be implemented by an objects to be rendered
       as an image and exported into a graphic format.
     */
    public interface Renderable {

    /**
       Function called before the rendering process is started.
     */
    public void beforeRendering();

    /**
       Function called to render the object into a graphic context
    */
    public void paintRenderable(Graphics2D g);

    /**
       Return the dimension of the rendered object
     */
    public Dimension getRenderableSize();

    /**
       Function called after the rendering process is completed
     */
    public void afterRendering();
    }

    private SVGUtils() {}

    /**
       Return a map containing all the supported graphical formats.
       The map is composed by pairs containing the identifier and the
       description of the format.
     */
    public static Map<String,String> getExportFormats() {
    TreeMap<String,String> map = new TreeMap<String,String>();
    map.put("svg","SVG");
    map.put("bmp","BMP");
    map.put("png","PNG");
    //map.put("gif","GIF");
    map.put("jpg","JPG");
    return map;
    }

    /**
       Return <code>true</code> if the identifier represent a
       supported graphical format.
     */
    static public boolean isGraphicFormat(String format) {
    return getExportFormats().containsKey(format);
    }

    /**
       Return a representation of a set of glycan structure as a
       string in SVG format.
       @param gr the GlycanRenderer used to render the structures
       @param structures the structures to be rendered
     */
    static public String getVectorGraphics(GlycanRendererAWT gr, Collection<Glycan> structures) {
    	return getVectorGraphics(gr, structures, false, false);
    }
    
    static public String getVectorGraphics(GlycanRendererAWT gr, LinkedList<Glycan> structures) {
    	return getVectorGraphics(gr, structures, false, false);
    }
    
    /**
       Return a representation of a set of glycan structure as a
       string in SVG format.
       @param gr the GlycanRenderer used to render the structures
       @param structures the structures to be rendered
       @param show_masses <code>true</code> if the mass information
       should be included in the graphical representation
       @param show_redend <code>true</code> if the reducing end marker
       should be included in the graphical representation
     */
    static public String getVectorGraphics(GlycanRendererAWT gr, Collection<Glycan> structures, boolean show_masses, boolean show_redend) {
        if (structures == null)
            structures = new LinkedList<>();

        try {
            // Create an instance of the SVG Generator
            DOMImplementation domImpl = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().getDOMImplementation();
            Document document = domImpl.createDocument(null, "svg", null);
            GroupingSVGGraphics2D g2d = new GroupingSVGGraphics2D(document, true);

            // Render into the SVG Graphics2D
            SVGGlycanRenderer sgr = new SVGGlycanRenderer(gr);
            PositionManager posManager = new PositionManager();
            BBoxManager bboxManager = new BBoxManager();
            Rectangle all_bbox = sgr.computeBoundingBoxes(structures, show_masses, show_redend, posManager, bboxManager);
            Dimension d = sgr.computeSize(all_bbox);

            // clear background
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            g2d.setBackground(Color.white);
            g2d.clearRect(0, 0, d.width, d.height);

            // paint
            for (Glycan s : structures)
                sgr.paint(g2d, s, null, null, show_masses, show_redend, posManager, bboxManager);

            // Set viewBox attribute
            String viewBox = "0 0 " + d.width + " " + d.height;
            Element root = document.getDocumentElement();
            root.setAttribute(SVGGraphics2D.SVG_VIEW_BOX_ATTRIBUTE, viewBox);
            g2d.getRoot(root);

            // Stream out SVG to a string
            StringWriter out = new StringWriter();
            g2d.stream(root, out, true, false);

            //return out.toString();
            return out.toString().replaceAll("shape-rendering:crispEdges;\\s?", "");
        } catch (Exception e) {
            LogUtils.report(e);
            return null;
        }
    }
    
    /**
     * JsonGraphicUtil
     */
    static public String getVectorGraphics(GlycanRendererAWT gr, LinkedList<Glycan> structures, boolean show_masses, boolean show_redend) {
    	if( structures == null ) structures = new LinkedList<>();

    	try {
    		// Create an instance of the SVG Generator
			DOMImplementation domImpl = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().getDOMImplementation();
    		Document document = domImpl.createDocument(null, "svg", null);
    		GroupingSVGGraphics2D g2d = new GroupingSVGGraphics2D(document,true);

    		// Render into the SVG Graphics2D
    		SVGGlycanRenderer sgr = new SVGGlycanRenderer(gr);
    		PositionManager posManager = new PositionManager();
    		BBoxManager bboxManager = new BBoxManager();
    		Rectangle all_bbox = sgr.computeBoundingBoxes(structures,show_masses,show_redend,posManager,bboxManager);
    		Dimension d = sgr.computeSize(all_bbox);

            // clear background
    		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    		g2d.setBackground(Color.white);
    		g2d.clearRect(0, 0, d.width, d.height);

            // paint
    		for(Glycan structure : structures )
    			sgr.paint(g2d,structure,null,null,show_masses,show_redend,posManager,bboxManager);

            // Set viewBox attribute
            String viewBox = "0 0 "+d.width+" "+d.height;
            Element root = document.getDocumentElement();
            root.setAttribute(SVGGraphics2D.SVG_VIEW_BOX_ATTRIBUTE, viewBox);
            g2d.getRoot(root);

            // Stream out SVG to a string
    		StringWriter out = new StringWriter();
            g2d.stream(root, out, true, false);

            //return out.toString();
            return out.toString().replaceAll("shape-rendering:crispEdges;\\s?", "");
    	}catch(Exception e) {
    		LogUtils.report(e);
    		return null;
    	}
    }    

    static private org.apache.batik.svggen.SVGGraphics2D prepareGraphics(Dimension all_dim) throws Exception {

    // Create an instance of the SVG Generator
    DOMImplementation domImpl = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder().getDOMImplementation();
    Document document = domImpl.createDocument(null, "svg", null);       
    org.apache.batik.svggen.SVGGraphics2D g2d = new org.apache.batik.svggen.SVGGraphics2D(document);

    // compute scale factor to fit 400x400 (otherwise it does not display)
    /*double sf = Math.min(400./all_dim.width,400./all_dim.height);   
    if( sf<1. )
        g2d.scale(sf,sf);
    else
        sf = 1.;
    all_dim.width = (int)(all_dim.width*sf);
    all_dim.height = (int)(all_dim.height*sf);
    */
    g2d.setBackground(Color.white);
    g2d.setSVGCanvasSize(all_dim);
    
    return g2d;
    }


    static private byte[] getRenderableVectorGraphics(Renderable renderable) {
    try {
        renderable.beforeRendering();
        
        // prepare g2d
        Dimension all_dim = renderable.getRenderableSize();
        org.apache.batik.svggen.SVGGraphics2D g2d = prepareGraphics(all_dim);

        // paint
        renderable.paintRenderable(g2d);
        
        renderable.afterRendering();
        
        StringWriter out = new StringWriter();
        g2d.stream(out, true);
        return out.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    } 
    catch(Exception e) {        
        LogUtils.report(e);
        return null;
    }
    }               

    /**
       Return an image on which a Renderable object has been painted
     */
    static public BufferedImage getImage(Renderable renderable) {
    return getImage(renderable,true);
    }

    /**
       Return an image on which a Renderable object has been painted
       @param opaque <code>false</code> if the background of the image
       must be transparent
     */
    static public BufferedImage getImage(Renderable renderable, boolean opaque) {
    renderable.beforeRendering();
    
    // Create an image that supports transparent pixels
    Dimension d = renderable.getRenderableSize();
    BufferedImage img = GraphicUtils.createCompatibleImage(d.width,d.height,opaque);    

    // prepare graphics context
    Graphics2D g2d = img.createGraphics();
    
    if(!opaque) {
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);    
        g2d.setBackground(new Color(255,255,255,0));
    }

    // paint
    renderable.paintRenderable(g2d);

    renderable.afterRendering();

    return img;
    }

    /*public byte[] getWMFGraphics(Collection<Glycan> structures) {
    try {
        // compute sizes
        PositionManager posManager = new PositionManager();       
        BBoxManager bboxManager = new BBoxManager();
        Rectangle all_bbox = computeBoundingBoxes(structures,false,posManager,bboxManager);    
        Dimension d = computeSize(all_bbox);

        // Create an instance of the WMF writer
        WMF wmf = new WMF();
        WMFGraphics2D g2d = new WMFGraphics2D(wmf, d.width, d.height);

        // Render into the WMF Graphics2D 
        HashSet<Residue> selected = new HashSet<Residue>();
        paintStructures(g2d,structures,selected,false,posManager,bboxManager);
        
        // Stream out WMF
        int dpi = 100;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //wmf.writePlaceableWMF(out, 0, 0, d.width, d.height, dpi);
        wmf.writeWMF(out);

        //FileOutputStream fos = new FileOutputStream("prova.wmf");
        //wmf.writePlaceableWMF(fos, 0, 0, d.width, d.height, dpi);
        //wmf.writeWMF(fos);
        //fos.close();
    
        g2d.dispose();
        return out.toByteArray();
    }
    catch(Exception e) {
        LogUtils.report(e);
        return null;
    }
    }
    */


    /**
       Export a representation of a set of glycan structure to a file
       in a certain graphical format.
       @param gr the GlycanRenderer used to render the structures
       @param filename the path to the destination file
       @param structures the structures to be rendered
       @param show_masses <code>true</code> if the mass information
       should be included in the graphical representation
       @param show_redend <code>true</code> if the reducing end marker
       should be included in the graphical representation
       @param format the graphical format to be used
       @return <code>true</code> if the file was successfully created
     */
    static public boolean export(GlycanRendererAWT gr, String filename, Collection<Glycan> structures, boolean show_masses, boolean show_redend, String format) {
    return export(gr,filename,structures,show_masses,show_redend,1.,format);
    }
    
    /**
       Export a representation of a set of glycan structure to a file
       in a certain graphical format.
       @param gr the GlycanRenderer used to render the structures
       @param filename the path to the destination file
       @param structures the structures to be rendered
       @param show_masses <code>true</code> if the mass information
       should be included in the graphical representation
       @param show_redend <code>true</code> if the reducing end marker
       should be included in the graphical representation
       @param scale the scaling factor to be applied to the image
       @param format the graphical format to be used       
       @return <code>true</code> if the file was successfully created
     */
    static public boolean export(GlycanRendererAWT gr, String filename, Collection<Glycan> structures, boolean show_masses, boolean show_redend, double scale, String format) {
    try {
        OutputStream os = new FileOutputStream(filename);
        export(os,gr,structures,show_masses,show_redend,scale,format);
        os.close();
        return true;
    }
    catch(Exception e) {
        LogUtils.report(e);
        return false;
    }
    }

    /**
       Create a representation of a set of glycan structure as an
       array of bytes in a certain graphical format.
       @param gr the GlycanRenderer used to render the structures
       @param structures the structures to be rendered
       @param show_masses <code>true</code> if the mass information
       should be included in the graphical representation
       @param show_redend <code>true</code> if the reducing end marker
       should be included in the graphical representation
       @param format the graphical format to be used
    */
    static public byte[] export(GlycanRendererAWT gr, Collection<Glycan> structures, boolean show_masses, boolean show_redend, String format) {
    return export(gr,structures,show_masses,show_redend,1.,format);
    }
    
    static public byte[] export(GlycanRendererAWT gr, Collection<Glycan> structures, boolean show_masses, boolean show_redend, double scale, String format) {
    	return export(gr, structures, show_masses, show_redend, scale, format, new PositionManager(), new BBoxManager());
    }

    /**
       Create a representation of a set of glycan structure as an
       array of bytes in a certain graphical format.
       @param gr the GlycanRenderer used to render the structures
       @param structures the structures to be rendered
       @param show_masses <code>true</code> if the mass information
       should be included in the graphical representation
       @param show_redend <code>true</code> if the reducing end marker
       should be included in the graphical representation
       @param scale the scaling factor to be applied to the image     
       @param format the graphical format to be used
    */
    static public byte[] export(GlycanRendererAWT gr, Collection<Glycan> structures, boolean show_masses, boolean show_redend, double scale, String format,PositionManager posManager,BBoxManager bboxManager) {
    try {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        export(bos,gr,structures,show_masses,show_redend,scale,format,posManager,bboxManager);
        return bos.toByteArray();
    }
    catch(Exception e) {
        LogUtils.report(e);
        return null;
    }
    }

    /**
       Export a representation of a set of glycan structure to a stream
       in a certain graphical format.
       @param os the destination output stream
       @param gr the GlycanRenderer used to render the structures
       @param structures the structures to be rendered
       @param show_masses <code>true</code> if the mass information
       should be included in the graphical representation
       @param show_redend <code>true</code> if the reducing end marker
       should be included in the graphical representation
       @param format the graphical format to be used
       @throws Exception if the format is not supported
    */
    static public void export(OutputStream os, GlycanRendererAWT gr, Collection<Glycan> structures, boolean show_masses, boolean show_redend, String format) throws Exception {
    export(os,gr,structures,show_masses,show_redend,1.,format);
    }
    
    static public void  export(OutputStream os, GlycanRendererAWT gr, Collection<Glycan> structures, boolean show_masses, boolean show_redend, double scale, String format) throws Exception {
    	export(os, gr, structures, show_masses, show_redend, scale, format, new PositionManager(), new BBoxManager());
    }

    /**
       Export a representation of a set of glycan structure to a stream
       in a certain graphical format.
       @param os the destination output stream
       @param gr the GlycanRenderer used to render the structures
       @param structures the structures to be rendered
       @param show_masses <code>true</code> if the mass information
       should be included in the graphical representation
       @param show_redend <code>true</code> if the reducing end marker
       should be included in the graphical representation
       @param scale the scaling factor to be applied to the image         
       @param format the graphical format to be used
       @throws Exception if the format is not supported
    */
    static public void export(OutputStream os, GlycanRendererAWT gr, Collection<Glycan> structures, boolean show_masses, boolean show_redend, double scale, String format,PositionManager posManager,BBoxManager bboxManager) throws Exception {
    if( format.equals("svg") )
        os.write(getVectorGraphics(gr,structures,show_masses,show_redend).getBytes());
    else if( format.equals("bmp") || format.equals("png") || format.equals("jpg") )        
        javax.imageio.ImageIO.write(gr.getImage(structures,true,show_masses,show_redend,scale,posManager,bboxManager),format,os);
    else
        throw new Exception("Unrecognized graphic format: " + format);    
    }

    /**
       Export a representation of a Renderable object to a filename in
       a certain graphical format.
       @param filename the path to the destination file
       @param renderable the renderable object to be exported
       @param format the graphical format to be used
       @throws Exception if the format is not supported
    */
    static public void export(String filename, Renderable renderable, String format) throws Exception {
    OutputStream os = new FileOutputStream(filename);
    export(os,renderable,format);
    os.close();
    }
    
    /**
       Export a representation of a Renderable object to a stream in
       a certain graphical format.
       @param os the destination output stream
       @param renderable the renderable object to be exported
       @param format the graphical format to be used
       @throws Exception if the format is not supported
    */
    static public void export(OutputStream os, Renderable renderable, String format) throws Exception {
    if( format.equals("svg") )
        os.write(getRenderableVectorGraphics(renderable));
    else if( format.equals("bmp") || format.equals("png") || format.equals("jpg") )        
        javax.imageio.ImageIO.write(getImage(renderable),format,os);
    else
        throw new Exception("Unrecognized graphic format: " + format);    
    }
}
