package se.raddo.raddose3D;

/**
 * Takes an experimental grid of beam intensities, and interpolates for
 * the correct intensity using Bilinear interpolation.
 */
public class BeamExperimental implements Beam {
  private final double     pixXSize,
                           beamXSize,
                           pixYSize,
                           beamYSize,
                           totalFlux,
                           beamEnergy;
  private double           beamSum,
                           attenuatedFlux;


  private double[][] beamArray;
  double[][] beamArrayWithBorders;
  private final Double[][] dataStructure;
  
  public boolean isCircular;
  

  /**
   * This takes the arguments, and generates the beamArray double[][] which
   * will be used for interpolation. This array must be in the RADDOSE-3D
   * coordinate system. It also adds a row or 0's around the
   * original array to allow for correct interpolation at the edges.
   *
   * @param totalFlux
   *          total flux of the beam in photons per second.
   * @param datastructure
   *          a 2D grid (image) of the beam. Must be 'true',
   *          i.e. any deconvolution of point spread functions or other
   *          pre-processing must be done upstream.
   * @param beamEnergy
   *          photon energy in keV
   * @param pixelSizeX
   *          the horizontal size of the pixels in micrometres
   * @param pixelSizeY
   *          the vertical size of the pixels in micrometres
   */
  public BeamExperimental(final Double[][] datastructure,
      final Double totalFlux, final Double beamEnergy,
      final Double pixelSizeX,
      final Double pixelSizeY) {
    
    this.dataStructure = datastructure;
    this.beamXSize = (datastructure[0].length + 2) * pixelSizeX;
    this.beamYSize = (datastructure.length + 2) * pixelSizeY;
    this.pixXSize = pixelSizeX;
    this.pixYSize = pixelSizeY;
    this.totalFlux = totalFlux;
    this.beamEnergy = beamEnergy;
    
  }

  /**
   * Generate the beam array from the other instance variables
   */
  @Override
  public void generateBeamArray() {     //This has been added as a new method so is no longer automatically called by BeamExperimental
    //set beam sum to zero
    beamSum = 0;
    // add a zero border
    int sizeHoriz = dataStructure[0].length;
    int sizeVert = dataStructure.length;
    beamArrayWithBorders = new double[sizeVert + 2][sizeHoriz + 2];

    for (int i = 0; i < sizeVert + 2; i++) {
      for (int j = 0; j < sizeHoriz + 2; j++) {
        if (i == 0 || j == 0 || i == sizeVert + 1 || j == sizeHoriz + 1) {
          beamArrayWithBorders[i][j] = 0;
        } else {
          beamArrayWithBorders[i][j] = dataStructure[i - 1][j - 1];
          beamSum += dataStructure[i - 1][j - 1];
        }
      }
    }

    // Dividing by beamSum gives normalised flux/pixelsize
    for (int i = 0; i < sizeVert + 2; i++) {
      for (int j = 0; j < sizeHoriz + 2; j++) {
        beamArrayWithBorders[i][j] = beamArrayWithBorders[i][j]
            * KEVTOJOULES * this.beamEnergy
            * this.attenuatedFlux
            / (beamSum * pixXSize * pixYSize);
      }
    }
    this.beamArray = beamArrayWithBorders;
  }

  /**
   * This uses bilinear interpolation to return beam intensity at
   * coorX, coordY.
   */
  @Override
  public double beamIntensity(final double coordX, final double coordY,
      final double offAxisUM) {
    beamArray = beamArrayWithBorders;
   if (isCircular == false) {
    if (Math.abs(coordX - offAxisUM) <= beamXSize / 2 - pixXSize
        && Math.abs(coordY) <= beamYSize / 2 - pixYSize) {
      return bilinearInterpolation(coordX, coordY, offAxisUM);
    } else {
      return 0d;
    }
   }
   else { // circular collimation
     if (((Math.pow(coordX - offAxisUM, 2)/Math.pow(beamXSize / 2 - pixXSize, 2)) + 
         (Math.pow(coordY, 2)/Math.pow(beamYSize / 2 - pixYSize, 2))) <= 1) {  // if in the ellipse
       return bilinearInterpolation(coordX, coordY, offAxisUM);
     }
     else {
       return 0d;
     }
   }
  }
  
  private double bilinearInterpolation(final double coordX, final double coordY,
      final double offAxisUM) {
    double realX = (coordX - offAxisUM + beamXSize / 2);
    double realY = (coordY + beamYSize / 2);
    int voxelHorizontal = (int) Math.floor(realX / pixXSize - 0.5);
    int voxelVertical = (int) Math.floor(realY / pixYSize - 0.5);
    if (voxelHorizontal < beamArray[0].length     //null pointer exception here
        && voxelVertical < beamArray.length) {
      float fracX = (float) (realX / pixXSize - (voxelHorizontal + 0.5));
      float fracY = (float) (realY / pixYSize - (voxelVertical + 0.5));

      return bilinearInterpolate(beamArray[voxelVertical][voxelHorizontal],
          beamArray[voxelVertical][voxelHorizontal + 1],
          beamArray[voxelVertical + 1][voxelHorizontal],
          beamArray[voxelVertical + 1][voxelHorizontal + 1], fracX,
          fracY);

    } else if (voxelHorizontal == beamArray[0].length
        || voxelVertical == beamArray.length) {
      return beamArray[voxelVertical][voxelHorizontal];

    } else {
      throw new IllegalArgumentException(
          "Image voxel request out of experimental profile");
    }
  }
  

  /**
   * Bilinear interpolation routine.
   *
   * @param v00
   *          Value at x=0, y=0.
   * @param v10
   *          Value at x=1, y=0.
   * @param v01
   *          Value at x=0, y=1.
   * @param v11
   *          Value at x=1, y=1.
   * @param x
   *          x position between 0 and 1.
   * @param y
   *          y position between 0 and 1.
   * @return
   *         Bilinearly interpolated value for coordinates x, y.
   */
  public static double bilinearInterpolate(final double v00, final double v10,
      final double v01, final double v11, final double x, final double y) {
    return v00 * (1 - x) * (1 - y)
        + v10 * (x) * (1 - y)
        + v01 * (1 - x) * (y)
        + v11 * (x) * (y);
  }

  @Override
  public String getDescription() {
    return "Experimental beam profile used";
  }

  @Override
  public double getPhotonsPerSec() {
    return totalFlux;
  }

  @Override
  public double getPhotonEnergy() {
    return beamEnergy;
  }

  @Override
  public void applyContainerAttenuation(Container sampleContainer){
    this.attenuatedFlux = (1 - sampleContainer.getContainerAttenuationFraction())
        * this.totalFlux;

    if (sampleContainer.getContainerMaterial() != null) {
      String s = String.format("Beam photons per second after container "
          + "attenuation is %.2e photons per second", this.attenuatedFlux);

      System.out.println(s);
    }
  }
  
  @Override
  public double beamMinumumDimension() {
    return Math.min(beamXSize, beamYSize);
  }

  @Override
  public double getBeamArea() {
    // TODO Auto-generated method stub
    return 0;
  }

}
