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
package segmentationanalyzer.metricprocessor;

import ij.ImagePlus;
import net.imglib2.img.Img;
import org.scijava.plugin.Plugin;
import segmentationanalyzer.Calculator;

/**
 *
 * @author sethwinfree
 */
@Plugin(type = MetricProcessor.class)

public class PixelWiseProcessor extends AbstractMetricProcessor {
    
//returns an F1-score
    
    public Number process(ImagePlus truth, ImagePlus test){
        
        int falsePositive = 0;
        int falseNegative = 0;
        int truePositive = 0;
        int trueNegative = 0;
        
        int[][] testArray = test.getProcessor().getIntArray();
        int[][] truthArray = truth.getProcessor().getIntArray();
        
        for(int i = 0; i < truth.getWidth()-1; i++){
            for(int j = 0; j < truth.getHeight()-1; j++){
                if(truthArray[i][j] > 0 && testArray[i][j] > 0){
                    truePositive++;
                    
                }
                if(truthArray[i][j] > 0 && testArray[i][j] == 0){
                    falseNegative++;
                   
                }
                if(truthArray[i][j] == 0 && testArray[i][j] > 0){
                    falsePositive++;
                    
                }
                if(truthArray[i][j] == 0 && testArray[i][j] == 0){
                    trueNegative++;
                    
                }
            }  
        }
      
        Calculator cal = new Calculator();
        double F1score = cal.calculateF1(falsePositive, truePositive, falseNegative, trueNegative);
        System.out.println("Pixelwise F1-score: " + F1score);
        return cal.calculateF1(falsePositive, truePositive, falseNegative, trueNegative);
}

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
