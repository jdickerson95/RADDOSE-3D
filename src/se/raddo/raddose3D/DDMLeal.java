package se.raddo.raddose3D;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DDMLeal implements DDM {
  
  /**
   * Decay parameters used in leal et al. 2012 (eqn 4).
   * The values were found for a cubic crystal of bovine
   * pancreatic insulin (unpublished)
   */
  private final double beta = 0.316928538944095;
  private final double B0 = 13.854805547210105;
  private final double gamma = 0.029790991953658;

  /**
   * Printed string to tell user the type of dose decay model being used.
   * 
   * @return Informative string about the dose decay model being used. 
   */
  @Override
  public String toString() {
    return "Dose Decay Model from Leal 2012 used.";
  }

  /**
   * Method to calculate the Relative Diffraction Efficiency (RDE).
   * The model used is from the Leal et al. 2012 paper that describes
   * the loss of scattering power of a crystal as a product of the 
   * expected intensity, the Debye-waller factor and an empirically 
   * derived scale factor. 
   * 
   * @param dose This is the absorbed dose within the crystal voxel
   * 
   * @return The Relative Diffraction Efficiency
   */
  @Override
  public double calcDecay(final double dose) {

    DDMLeal DDMObj = new DDMLeal(); 
    /** Relative intensity is the integrated intensity calculated
     *  using the current dose divided by the integrated intensity
     *  at dose = 0 MGy
     */
    double relativeIntensityDecay = DDMObj.getIntegratedIntensity(dose)/DDMObj.getIntegratedIntensity(0);
    return relativeIntensityDecay;
  }
  
  /**
   * Method to calculate the expected integrated intensity.
   * The integrated intensity can be found in the Leal et al. 2012
   * paper equation 4.
   *  
   * @param dose This is the absorbed dose within the crystal voxel
   * 
   * @return The integrated intensity
   */
  public double getIntegratedIntensity(final double dose) {
    // TODO Write a check that makes sure there is an argument, preferably of type double.

    /**
     * The integrated intensity according to leal et al. 2012 (eqn 4) 
     */
    double integratedIntensity;
    
        
    /**
      *Maximum resolution value of X-ray crystallography experiment 
      */
    final double maxResolution = 1.8;
    
    /**
      *Maximum h^2 value (where h = 1/d, d is resolution). 
      */
    final double hsqrdMax;
    
    /**
     * Boolean value used in loop to determine whether
     * the correct BEST intensity values are being used.
     */
    boolean moreThanMaxhsqrd = false;
    
    /**
     * Variable used to denote the row index
     */
    int rowIndex = 0;
    
    /**
      * Array that stores the BEST intensity data  
      */
    final double[][] BEST_DATA = getBESTData();
    
    /**
      *Array containing the squares of the reciprocal resolution values.
      * i.e. if d is the resolution then hsqrd = 1/d^2.
      */
    double[] hsqrd = new double[BEST_DATA.length];
    
    /**
      * Array containing the expected intensity values for a given
      * resolution. 
      */
    double[] expectedIntensity = new double[BEST_DATA.length];
    
    /**
      * Array containing the differences between each resolution value
      * in the BEST intensity data. 
      */
    double[] dh = new double[BEST_DATA.length - 1];
    
    /** Calculate the maximum h^2 value */
    hsqrdMax = 1/Math.pow(maxResolution, 2);
    
    /**
     * Store the h^2 and the corresponding intensity values from the
     * BEST data that are below the specified maximum resolution value.  
     */
    while(!moreThanMaxhsqrd) {
        if (BEST_DATA[rowIndex][0] <= hsqrdMax) {
            hsqrd[rowIndex] = BEST_DATA[rowIndex][0];
            expectedIntensity[rowIndex] = BEST_DATA[rowIndex][1];
            rowIndex++;
        } else {
            moreThanMaxhsqrd = true;
        }           
    }
    
    /**
     * Calculate the dh values, i.e. the differences between each resolution
     * from the BEST data 
     */
    for (int i = 0; i < rowIndex - 1; i++) {
        dh[i] = Math.sqrt(hsqrd[i+1]) - Math.sqrt(hsqrd[i]);
    }
    
    /**
     * Calculate integral of eqn 4 leal et al. 2012
     */
    double integralSum = 0;
    double eachTerm;
    for (int j = 0; j < dh.length; j++) {
        eachTerm = ((hsqrd[j+1] + hsqrd[j])/2) 
                * ((expectedIntensity[j+1]+expectedIntensity[j])/2)
                *Math.exp(-0.5*((hsqrd[j+1] + hsqrd[j])/2)*(this.B0 + (this.beta*dose)))
                *dh[j];
        integralSum = integralSum + eachTerm;
    }
    
    /** 
     * Calculate the integrated intensity of eqn 4 leal et al. 2012
     */
    integratedIntensity = Math.exp(-Math.pow(this.gamma*dose,2)) * integralSum;
    
    return integratedIntensity;
  }
  
  /**
   * Method to extract the BEST intensity data (Popov & Bourenkov 2003)
   * The intensity data is stored in a csv file in 2 columns:
   * column 1 are h^2 values (h = 1/d and d is the resolution in Angstroms)
   * column 2 are the expected intensity values (denoted J in the file).
   * The file contains intensity values for each of the 300 resolution bins
   * (i.e. 300 rows)
   * 
   * @return An array containing the BEST intensity data
   */

  public static double[][] getBESTData() {
    
    /** Path to the file containing the values of the BEST intensity data */
    final String BEST_INTENSITY_FILE = "constants/Intensity_values.csv";
    
    /** Number of columns in BEST intensity data*/
    final int NUMBER_OF_COLUMNS = 2;
    
    
    /** Number of intensity values in BEST intensity data */
    final int NUMBER_OF_BEST_INTENSITY_VALUES = 300;
    
    
    /** Array to contain the BEST intensity data */
    final double[][] BEST_DATA = new double[NUMBER_OF_BEST_INTENSITY_VALUES][NUMBER_OF_COLUMNS];
    
    
    /** Counter for each value read in the BEST intensity file*/
    int csvFileRowNumber = 0;
    
    /**
     * Initialising variable to store data for each row of the BEST
     * intensity .csv file
     */
    String rowData = null;
    
    /**
     * Initialising the variable to hold reference to the .csv file
     * object. 
     */
    BufferedReader CSVFile = null;
    /** Try to create a File reader object*/
    try {
        CSVFile = new BufferedReader(new FileReader(BEST_INTENSITY_FILE));
    } catch (FileNotFoundException e) {
        System.out.println("Couldn't find the file.");
        System.out.println("Has the BEST intensity csv file been moved or deleted?");
        e.printStackTrace();
    }
    
    /**
     * Read the first line from the .csv file.
     * It contains headers so we don't need to store these values.
     */
    try {
        rowData = CSVFile.readLine();
    } catch (IOException e1) {
        System.out.println("There is no 1st line to read in the BEST intensity file.");
        System.out.println("Have you amended the file?");
        e1.printStackTrace();
    }
    
    /**
     * Read the string from each row of the csv file and then split
     * by the ",". The first value (h^2 - reciprocal of the resolution
     * squared) is stored in the first column. 
     * The second value (J - expected intensity) is stored in the 
     * second column.
     * Note: the first row
     */
    while (csvFileRowNumber < NUMBER_OF_BEST_INTENSITY_VALUES) {
        try {
            rowData = CSVFile.readLine();
        } catch (IOException e) {
            System.out.println("There are no more lines to read in the BEST intensity file.");
            System.out.println("Have you amended the file?");
            e.printStackTrace();
        }
        BEST_DATA[csvFileRowNumber][0] = Double.parseDouble(rowData.split(",")[0]);
        BEST_DATA[csvFileRowNumber][1] = Double.parseDouble(rowData.split(",")[1]);
        csvFileRowNumber++;
    }
    
    /** Try to close the file*/
    try {
        CSVFile.close();
    } catch (IOException e) {
        System.out.println("Could not close the BEST intensity .csv file.");
        e.printStackTrace();
    }
    /** Return the BEST intensity data */
    return BEST_DATA;
    
}
  
  

}
