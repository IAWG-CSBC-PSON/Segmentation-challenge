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
package segmentationanalyzer.cellsegmenter;

import ij.ImagePlus;
import ij.ImageStack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import org.scijava.plugin.Plugin;
import segmentationanalyzer.Calculator;
import segmentationanalyzer.metricprocessor.AbstractMetricProcessor;
import segmentationanalyzer.metricprocessor.MetricProcessor;

/**
 *
 * @author Seth
 */

@Plugin(type = MetricProcessor.class)
public class CellProcessor extends AbstractMetricProcessor {
    
    private List<microRegion> truthObjects = Collections.synchronizedList(new ArrayList<microRegion>());
    private List<microRegion> testObjects = Collections.synchronizedList(new ArrayList<microRegion>());
    
//    private List<microRegion> tPObjects = Collections.synchronizedList(new ArrayList<microRegion>());
//    
//    private List<microRegion> fPObjects = Collections.synchronizedList(new ArrayList<microRegion>());
//    private List<microRegion> fNObjects = Collections.synchronizedList(new ArrayList<microRegion>());
    
    private int centroidRange = 5;
    
    private double F1score = 1;
    private double IoUscore = 1;
    
    public CellProcessor(int range){
        centroidRange = range;
    }
   
    
    public void buildObjects(ImagePlus truth, ImagePlus test){
        int[][] truthArray = truth.getProcessor().getIntArray();
        int[][] testArray = test.getProcessor().getIntArray();
        
        int currentIndex = 0;
        
        ArrayList<int[]> objectPixel = new ArrayList();
        
        truthObjects = defineRegions(truth);
        testObjects = defineRegions(test); 
        
        System.out.println("Truth objects: " + truthObjects.size());
        System.out.println("Test objects: " + testObjects.size());
        
        F1score = findCommonCells();
        System.out.println("Object F1-score: " + F1score);
        
        IoUscore = assessCellularOverlap(1/2);
        System.out.println("Average IoU: " + IoUscore);
        
        
    }
    
    public double assessCellularOverlap(float UnionOverlap){
        ArrayList IoUList = new ArrayList();
        //Intersection over Union
        ListIterator<microRegion>  itr = testObjects.listIterator();
        while(itr.hasNext()){
            
            microRegion child = itr.next();
            
            if(child.getParent().size() == 1){
                int index = (int)child.getParent().get(0);
                microRegion parent = truthObjects.get(index);
                int intersection = getIntersection(child,parent);
                int union = (child.getPixelCount() + parent.getPixelCount())-intersection;
                
                //System.out.println("Intersection score: " + intersection);
                //System.out.println("Union score: " + union);
                
                
                if(intersection > (int)(0.5*(union))){
                    //System.out.println("Intersection score: " + (int)(0.5*(union)));
                    //System.out.println("Intersection score: " + intersection);
                    //System.out.println("Union score: " + union);
                    IoUList.add((double)intersection/union);
                }
            }  
        }
        
        ListIterator itrvalue = IoUList.listIterator();
        int n = 0;
        double total = 0;
        while(itrvalue.hasNext()){
            double value = (double)itrvalue.next();
            total = total + value;
            n++;
        }
        
       
       System.out.println("Overlapping Cells for IoU: " + n);
        return (double)(total/IoUList.size());
    }
    
    private int getIntersection(microRegion child, microRegion parent){
            int[] xChild = child.getPixelsX();
            int[] yChild = child.getPixelsY();
            
            int[] xParent = parent.getPixelsX();
            int[] yParent = parent.getPixelsY();
            
            int intersection = 0;
            
            for(int i = 0; i < xParent.length; i++){
                int xParentStart = xParent[i];
                int yParentStart = yParent[i];
                for(int j = 0; j <xChild.length; j++){
                    int xChildStart = xChild[j];
                    int yChildStart = yChild[j];
                    if(xParentStart == xChildStart & yParentStart == yChildStart){
                        intersection++;
                    }
                }
            }
            return intersection;
    }
    

    
    
    public double findCommonCells(){
        
        int falsePositive = 0;    
        int falseNegative = 0;
        int truePositive = 0;
        int trueNegative = 0; 
        
        //System.out.println("Number of objects in test: " + testObjects.size());
        //System.out.println("Number of objects in truth: " + truthObjects.size());
        
        if(Math.abs((double)(testObjects.size()-truthObjects.size())) > (truthObjects.size()*0.25)){   
            System.out.println("WARNING: there is poor overlap of test and truth datasets");   
        }
        
        //truth by test
        
        double[][] ComparisonTable = new double[truthObjects.size()][testObjects.size()];
        
        for(int i = 0; i < truthObjects.size(); i++){
             for(int j = 0; j < testObjects.size(); j++){
                 
                 double[] truthPosition = new double[2];
                 double[] testPosition = new double[2];
                 
                 truthPosition[0] = ((microRegion)truthObjects.get(i)).getBoundCenterX();
                 truthPosition[1] = ((microRegion)truthObjects.get(i)).getBoundCenterY();
                 
                 //System.out.println("Print truth centroid: " +  truthPosition[0] + "," + truthPosition[1]);
                 
                 testPosition[0] = ((microRegion)testObjects.get(j)).getBoundCenterX();
                 testPosition[1] = ((microRegion)testObjects.get(j)).getBoundCenterY();
                 
                 //System.out.println("Print test centroid: " +  testPosition[0] + "," + testPosition[1]);
                 
                 ComparisonTable[i][j] = lengthCart(testPosition,truthPosition); 
                 
                 //System.out.println("Print distance: " + ComparisonTable[i][j]);
             }
        }
        
        //parse the truth and determine rates
        
        for(int k = 0; k < truthObjects.size(); k++){
            for(int l = 0; l < testObjects.size(); l++){
                if(ComparisonTable[k][l] <= centroidRange){
                    ((microRegion)testObjects.get(l)).addParent(k);
                    ((microRegion)truthObjects.get(k)).addChild(l);
                }
            }
        }
        
        
        for(int n = 0; n < truthObjects.size(); n++){
            int childCount = 
                    ((ArrayList)(((microRegion)truthObjects.get(n)).getChildren())).size(); 
            if(childCount > 1){
                falsePositive =+ childCount-1;
            } else if(childCount == 0){
                falseNegative++;
            } else if (childCount == 1){
                truePositive++;
            } else {
                falseNegative++;   
            }
        }
         Calculator cal = new Calculator();
        return cal.calculateF1(falsePositive, truePositive, falseNegative, trueNegative);
    }

        private double lengthCart(double[] position, double[] reference_pt) {
        double distance;
        double part0 = position[0] - reference_pt[0];
        double part1 = position[1] - reference_pt[1];
        distance = Math.sqrt((part0 * part0) + (part1 * part1));
        return distance;
    }
        
    public double getF1score(){
        return F1score;
    }

    @Override
    public Number process(ImagePlus truth, ImagePlus test) {
        buildObjects(truth, test);
        return 0;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private List<microRegion> defineRegions(ImagePlus imp) {
        
        List<microRegion> Result = Collections.synchronizedList(new ArrayList<microRegion>());

            
            int region = 0;
            ArrayList<int[]> pixels = new ArrayList<int[]>();

                for (int p = 0; p < imp.getWidth(); p++) {
                    for (int q = 0; q < imp.getHeight(); q++) {
                        if(imp.getProcessor().getPixelValue(p, q) > 0) {
                            //imp.show();
                            pixels = floodfill_4C(imp, p, q, imp.getWidth(), imp.getHeight(), pixels, (int)imp.getProcessor().getPixelValue(p, q));

                            int[] pixel = new int[3];
                            int[] xPixels = new int[pixels.size()];
                            int[] yPixels = new int[pixels.size()];
                            int j = 0;

                            ListIterator<int[]> itr = pixels.listIterator();
                            
                            while (itr.hasNext()) {
                                pixel = itr.next();
                                xPixels[j] = pixel[0];
                                yPixels[j] = pixel[1];
                                j++;
                            }

                            if(xPixels.length > 0){
                            
                            Result.add(new microRegion(xPixels, 
                                    yPixels, xPixels.length, 1));
                            
                            
                            region++;
                            System.out.println("Adding a new region: " + region);
                            pixels.clear();
                            }
                        }
                    }
                }

            return Result;
        }

    private ArrayList<int[]> floodfill_8C(ImagePlus imp, int x, int y, int width, int height, ArrayList<int[]> pixels, int color, int depth) {

            if ( x < 0 || y < 0 || x >= width || y >= height) {
                return pixels;
            } else if(((int)imp.getProcessor().getPixelValue(x, y)) == color) {
                int[] pixel = new int[2];
                
                //depth++;
                
                imp.getProcessor().putPixel(x,y,0);

                
                pixel[0] = x;
                pixel[1] = y;
                
                pixels.add(pixel);
                

                pixels = floodfill_8C(imp, x + 1, y, width, height,  pixels, color, depth);
                pixels = floodfill_8C(imp, x, y + 1, width, height,  pixels,color,depth);
                pixels = floodfill_8C(imp, x + 1, y + 1, width, height,pixels, color,depth);
                pixels = floodfill_8C(imp, x - 1, y, width, height,  pixels,color,depth);
                pixels = floodfill_8C(imp, x, y - 1, width, height,  pixels,color,depth);
                pixels = floodfill_8C(imp, x - 1, y - 1, width, height,  pixels,color,depth);
                pixels = floodfill_8C(imp, x - 1, y + 1, width, height,  pixels,color,depth);
                pixels = floodfill_8C(imp, x + 1, y - 1, width, height,  pixels,color,depth);
            }
    
            return pixels;
        }
    
    private ArrayList<int[]> floodfill_4C(ImagePlus imp, int x, int y, int width, int height, ArrayList<int[]> pixels, int color) {
               //System.out.println("Analyzing x: " + x + ", " + y +" current value:" + color + " pixel value: " + imp.getProcessor().getPixelValue(x, y));
            if (x < 0 || y < 0 || x >= width || y >= height ||  (((int)imp.getProcessor().getPixelValue(x, y)) != color) || (((int)imp.getProcessor().getPixelValue(x, y)) == (Math.pow(2,imp.getBitDepth()))-1)||(((int)imp.getProcessor().getPixelValue(x, y)) == 0) ) {
                //System.out.println("Exit at x: " + x + ", " + y +" current value:" + color + " pixel value: " + imp.getProcessor().getPixelValue(x, y));
                return pixels;
            } else {
                //System.out.println("Building x: " + x + ", " + y +" current value:" + color + " pixel value: " + (int)imp.getProcessor().getPixelValue(x, y));
                imp.getProcessor().putPixel(x,y,0);

                int[] pixel = new int[2];
                pixel[0] = x;
                pixel[1] = y;
                
                pixels.add(pixel);
                
                try {
                pixels = floodfill_4C(imp, x + 1, y, width, height,pixels,color);
                pixels = floodfill_4C(imp, x, y + 1, width, height,pixels,color);
                pixels = floodfill_4C(imp, x - 1, y, width, height,pixels,color);
                pixels = floodfill_4C(imp, x, y - 1, width, height,pixels,color);
                 } catch(StackOverflowError t) {
            System.out.println("Caught "+t);
            System.out.println("Position "+x + "," + y +", color: " + color);
            
            
            
            t.printStackTrace();
        }
            }
            return pixels;
        }
    
    private ArrayList<int[]> floodfill_6C_3D(ImagePlus imp, int x, int y, int width, int height, ArrayList<int[]> pixels, int color, int depth) {

            if ( x < 0 || y < 0 || x >= width || y >= height) {
                return pixels;
            } else if(((int)imp.getProcessor().getPixelValue(x, y)) == color) {
                int[] pixel = new int[2];
                
                //depth++;
                
                imp.getProcessor().putPixel(x,y,0);

                
                pixel[0] = x;
                pixel[1] = y;
                
                pixels.add(pixel);
                

                pixels = floodfill_8C(imp, x + 1, y, width, height,  pixels, color, depth);
                pixels = floodfill_8C(imp, x, y + 1, width, height,  pixels,color,depth);
                pixels = floodfill_8C(imp, x + 1, y + 1, width, height,pixels, color,depth);
                pixels = floodfill_8C(imp, x - 1, y, width, height,  pixels,color,depth);
                pixels = floodfill_8C(imp, x, y - 1, width, height,  pixels,color,depth);
                pixels = floodfill_8C(imp, x - 1, y - 1, width, height,  pixels,color,depth);
                pixels = floodfill_8C(imp, x - 1, y + 1, width, height,  pixels,color,depth);
                pixels = floodfill_8C(imp, x + 1, y - 1, width, height,  pixels,color,depth);
            }
    
            return pixels;
        }
    

}


