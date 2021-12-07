package segmentationanalyzer;


import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;
import ij.plugin.PlugIn;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import loci.formats.FormatException;
import loci.plugins.BF;

import net.imglib2.img.Img;
import org.scijava.Context;
import org.scijava.Prioritized;
import org.scijava.Priority;
import org.scijava.log.LogService;
import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.PluginService;
import org.scijava.plugin.RichPlugin;
import segmentationanalyzer.cellsegmenter.CellProcessor;
import segmentationanalyzer.metricprocessor.PixelWiseProcessor;

//import segmentationanalyzer.services.MetricService;

/*
 * Copyright (C) 2020 SciJava
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

/**
 *
 * @author Seth
 */
public class _SegmentationAnalyzer implements PlugIn, RichPlugin {
    public Context context;
    public double priority;
    
    public static String LASTDIRECTORY = new String(System.getProperty("user.home") + "/Desktop");
    
    JLabel group = new JLabel("Group 1");
    JLabel truthFile = new JLabel(LASTDIRECTORY);
    JLabel testFile = new JLabel(LASTDIRECTORY);
    JPanel content = new JPanel();
    
    
    
    
    public static void main(String[] args) {
         //set the plugins.dir property to make the plugin appear in the Plugins menu
        Class<?> clazz = _SegmentationAnalyzer.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
        System.setProperty("plugins.dir", pluginsDir);

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {new ImageJ(); }
        });
    }
    //private Server sonicServer;


    @Override
    public void run(String str){
        
                //getUIValues();
        
                context = new Context( LogService.class, PluginService.class );
                priority = Priority.FIRST_PRIORITY;

                System.out.println("Starting up Segmentation Analyzer... ");
                System.out.println("-------------------------------- ");
                System.out.println("Available memory: " + getAvailableMemory()/(1000000000) + " GB");
                System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
                System.out.println("-------------------------------- ");
                System.out.println("Seting JVM configurations...");
                
                System.setProperty("java.util.Arrays.sort", "true");
                
                System.out.println("-------------------------------- ");
                System.out.println("Setting ImageJ configurations...");
                
                IJ.run("Options...", "iterations=1 count=1");

                JFrame window = new JFrame();
                JPanel content = new JPanel();
                content.setLayout(new FlowLayout());
                
                content.add(group);
    JLabel truthFile = new JLabel(LASTDIRECTORY);
    JLabel testFile = new JLabel(LASTDIRECTORY);
                
            
                
         //for(int i = 1; i <= 50; i=i+5){

               //ImagePlus truth = IJ.openImage("D:\\Hackathon\\Truthing Tool\\Test\\CellSegFinal_000-1.tif");
                //ImagePlus test = IJ.openImage("D:\\Hackathon\\Truthing Tool\\Test\\000_mask.tif");
                
                //ImagePlus truth = IJ.openImage("D:\\Hackathon\\Truthing Tool\\Test\\17775703_truth.png");
                //ImagePlus test = IJ.openImage("D:\\Hackathon\\Truthing Tool\\Test\\17775703_mask.tif");
                
                //ImagePlus truth = IJ.openImage("D:\\Hackathon\\Truthing Tool\\Test\\truth_OHSU_slide001_scene004Cell_segmentation_map.tif");
                //ImagePlus test = IJ.openImage("D:\\Hackathon\\Truthing Tool\\Test\\test_OHSU_slide001_scene004Cell_segmentation_map-1.tif");
                
               // Img truth = (Img<T>)imgOpener.openImgs("D:\\Hackathon\\Truthing Tool\\Validation_DataSet_030420-1.tif").get(0);
                
               Opener open = new Opener();
               
               
               
               ImagePlus[] truthGroup;
               ImagePlus[] testGroup;
               
               ImagePlus truth = new ImagePlus();
               ImagePlus test = new ImagePlus();
               
               
               

               
        try {
            truthGroup = BF.openImagePlus("D:\\Hackathon\\Teams\\Truth\\VLC\\Plate114798_N02_17551485.png");
            testGroup = BF.openImagePlus("D:\\Hackathon\\Teams\\Team_2\\VLC\\17551485_Nuclear_segmentation_map.tif");
            
              truth = truthGroup[0];
               test = testGroup[0];
        } catch (FormatException ex) {
            Logger.getLogger(_SegmentationAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(_SegmentationAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }

               
               // ImagePlus test = IJ.openImage("D:\\Hackathon\\Teams\\Team_2\\OHSUN\\OHSU_slide001_scene004Nuclear_segmentation_map.tif");
                
                PixelWiseProcessor pwp = new PixelWiseProcessor();
                pwp.process(truth, test);
                CellProcessor cp = new CellProcessor(1);
                cp.process(truth, test);
        // }    
                    
    }
    
   
    
    

    @Override
    public Context context() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Context getContext() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setContext(Context cntxt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getPriority() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPriority(double d) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int compareTo(Prioritized o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PluginInfo<?> getInfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setInfo(PluginInfo<?> pi) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
        public static long getAvailableMemory(){
        
            long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long freeMemory = Runtime.getRuntime().maxMemory() - usedMemory;
            
            return freeMemory;

    }
        
        public void addFromCSV(String s) {
//        //this method does not assume that all objects get a value
        int dataColumns = 0;

        JFileChooser jf = new JFileChooser(_SegmentationAnalyzer.LASTDIRECTORY);
        int returnVal = jf.showOpenDialog(content);
        File file = jf.getSelectedFile();

        ArrayList<ArrayList<Number>> csvData = new ArrayList();
        

        ArrayList<Number> blank = new ArrayList<Number>();

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedReader csvReader = new BufferedReader(new FileReader(file));
                String row;
                int objectID = 0;
                while ((row = csvReader.readLine()) != null) {
                    String[] data = row.split(",");

                    dataColumns = data.length;

                    ArrayList<Number> dataList = new ArrayList<Number>();

                    for (int j = 0; j < data.length; j++) {
                        dataList.add(Float.parseFloat(data[j]));
                    }

                    csvData.add(dataList);

                }
                csvReader.close();

            } catch (IOException e) {
                System.out.println("ERROR: Could not open the file.");
            }

            for (int k = 0; k < dataColumns; k++) {
                blank.add(-1);
            }
            
            for(int i = 1; i < dataColumns; i++){
                ArrayList<Number> data = getData(i, csvData);
//
//                if (data.size() > 0) {
//                    paddedTable.add(data);
//                } else {
//                    paddedTable.add(blank);
//                }
            }

            String name = file.getName();
            name = name.replace(".", "_");
            
        }
    }
        
            private ArrayList<Number> getData(int columnIndex,
            ArrayList<ArrayList<Number>> data) {
        ArrayList<Number> result = new ArrayList<Number>();
        
//        for (int objectID = 0; objectID < objects.size(); objectID++){
//            ListIterator<ArrayList<Number>> itr = data.listIterator();
//            boolean elementFound = false;
//            while(itr.hasNext()){
//                ArrayList<Number> test = itr.next();
//                if((Float)(test.get(0)) == (float)objectID){
//                    result.add(test.get(columnIndex));
//                    elementFound = true;
//                    break;
//                }
//            }
//            if(elementFound == false){
//                result.add(-1);
//            }
//        }
        return result;
    }
}
