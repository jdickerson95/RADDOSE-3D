package se.raddo.raddose3D;

/**
 * @author Helen Ginn
 */

public class Atom
{
  /*
   * These are the constants derived from the text file
   */
  public String     elementName;
  public int        atomicNumber;
  
  public double     absorptionEdgeK;
  public double     absorptionEdgeL;
  public double     absorptionEdgeM;
  
  public double[]   absorptionEdgeKCoeff;
  public double[]   absorptionEdgeLCoeff;
  public double[]   absorptionEdgeMCoeff;
  public double[]   absorptionEdgeNCoeff;
  
  public double     density;
  public double     conversionConstant;
  public double     atomicWeight;
  
  public double[]   coherentScatteringCoeff;
  public double[]   incoherentScatteringCoeff;
  
  public double     kEdgeBeta1;
  public double     kEdgeAlpha1;
  public double     lEdgeBeta1;
  public double     lEdgeAlpha1;
  
  public double     l2, l3, lj3, fek, fel;
  
  public static final double    ATOMIC_MASS_UNIT    = 1.66E-24; // in grams
  public static final double        LJ_1                 = 1.160;
  public static final double        LJ_2                 = 1.41;
  
  
  /*
   * Occurrence - number of times this atom is found in the protein.
   */
  
  public double macromolecularOccurrence;
  public double hetatmOccurrence = 0;
  public double solventConcentration;
  public double solventOccurrence;
  
  public double photoelectricCrossSection;
  public double totalCrossSection;
  public double coherentCrossSection;
  
  
  public Atom(String name, int number)
  {
    elementName = name;
    atomicNumber = number;
    macromolecularOccurrence = 0;
    solventOccurrence = 0;
    solventConcentration = 0;
    photoelectricCrossSection = 0;
    totalCrossSection = 0;
    coherentCrossSection = 0;
  }
  
  public void setCoreParameters(String name, int number)
  {
    this.elementName = name;
    this.atomicNumber = number;
  }
  
  public void setAbsorptionEdges(double edgeK, double edgeL, double edgeM)
  {
    this.absorptionEdgeK = edgeK;
    this.absorptionEdgeL = edgeL;
    this.absorptionEdgeM = edgeM;
  }
  
  public void setAtomicConstants(double dens, double conversion, double atweight)
  {
    this.density = dens;
    this.conversionConstant = conversion;
    this.atomicWeight = atweight;
  }
  
  public void setAbsorptionKEdgeCoeffs(double k0, double k1, double k2, double k3)
  {
    this.absorptionEdgeKCoeff = new double[4];
    
    absorptionEdgeKCoeff[0] = k0;
    absorptionEdgeKCoeff[1] = k1;
    absorptionEdgeKCoeff[2] = k2;
    absorptionEdgeKCoeff[3] = k3; 
  }
  
  public void setAbsorptionLEdgeCoeffs(double l0, double l1, double l2, double l3)
  {
    this.absorptionEdgeLCoeff = new double[4];
    
    absorptionEdgeLCoeff[0] = l0;
    absorptionEdgeLCoeff[1] = l1;
    absorptionEdgeLCoeff[2] = l2;
    absorptionEdgeLCoeff[3] = l3; 
  }
  
  public void setAbsorptionMEdgeCoeffs(double m0, double m1, double m2, double m3)
  {
    this.absorptionEdgeMCoeff = new double[4];
    
    absorptionEdgeMCoeff[0] = m0;
    absorptionEdgeMCoeff[1] = m1;
    absorptionEdgeMCoeff[2] = m2;
    absorptionEdgeMCoeff[3] = m3; 
  }
  
  public void setAbsorptionNEdgeCoeffs(double n0, double n1, double n2, double n3)
  {
    this.absorptionEdgeNCoeff = new double[4];
    
    absorptionEdgeNCoeff[0] = n0;
    absorptionEdgeNCoeff[1] = n1;
    absorptionEdgeNCoeff[2] = n2;
    absorptionEdgeNCoeff[3] = n3; 
  }
  
  public void setCoherentScatteringCoeffs(double coh0, double coh1, double coh2, double coh3)
  {
    this.coherentScatteringCoeff = new double[4];
    
    coherentScatteringCoeff[0] = coh0;
    coherentScatteringCoeff[1] = coh1;
    coherentScatteringCoeff[2] = coh2;
    coherentScatteringCoeff[3] = coh3; 
  }
  
  public void setIncoherentScatteringCoeffs(double incoh0, double incoh1, double incoh2, double incoh3)
  {
    this.incoherentScatteringCoeff = new double[4];
    
    incoherentScatteringCoeff[0] = incoh0;
    incoherentScatteringCoeff[1] = incoh1;
    incoherentScatteringCoeff[2] = incoh2;
    incoherentScatteringCoeff[3] = incoh3; 
  }
  
  public void setAlphaBetaEdges(double kb1, double ka1, double lb1, double la1)
  {
    this.kEdgeAlpha1 = ka1;
    this.kEdgeBeta1 = kb1;
    this.lEdgeAlpha1 = la1;
    this.lEdgeBeta1 = lb1;
  }
  
  public void setLsLJsFEKsFELs(double ltwo, double lthree, double ljthree, double feck, double fell)
  {
    this.l2 = ltwo;
    this.l3 = lthree;
    this.lj3 = ljthree;
    this.fek = feck;
    this.fel = fell;
  }
  
  public double totalAtoms()
  {
    double totalAtoms = this.solventOccurrence + this.macromolecularOccurrence;
    
    return totalAtoms;
  }
  
  public double totalMass()
  {
    double totalAtoms = this.totalAtoms();
    double mass = atomicWeight * totalAtoms * ATOMIC_MASS_UNIT;
    
    return mass;
  }
  
  public double edgeCoefficient(int num, String edge)
  {
    switch (edge.toCharArray()[0])
    {
      case 'K':
        return this.absorptionEdgeKCoeff[num];
      case 'L':
        return this.absorptionEdgeLCoeff[num];
      case 'M':
        return this.absorptionEdgeMCoeff[num];
      case 'N':
        return this.absorptionEdgeNCoeff[num];
      case 'C':
        return this.coherentScatteringCoeff[num];
      case 'I':
        return this.incoherentScatteringCoeff[num];
      default:
        System.out.println("ERROR: Something's gone horribly wrong in the code");
        return -1;
    }
  }
  
  public double baxForEdge(double energy, String edge)
  {
    // calculation from logarithmic coefficients in McMaster tables.
    
    double sum = 0;
    
    for (int i=0; i < 4; i++)
    {
      double coefficient = edgeCoefficient(i, edge);
      
      if (coefficient == -1)
        sum = 0;
      else
        if (energy == 1)
          sum += coefficient;
        else
          sum += coefficient * Math.pow(Math.log(energy), i);
    }
    
    double bax = Math.exp(sum);
    
    return bax;
  }
  
  public void calculateMu(double energy)
  {
    if (energy < absorptionEdgeK && energy > absorptionEdgeK - 0.001)
    {
      System.out.println("Warning: using an energy close to middle of K edge of " + elementName);
      return;
    }
    if (energy < absorptionEdgeL && energy > absorptionEdgeL - 0.001)
    {
      System.out.println("Warning: using an energy close to middle of L edge of " + elementName);
      return;
    }
    if (energy < absorptionEdgeM && energy > absorptionEdgeM - 0.001)
    {
      System.out.println("Warning: using an energy close to middle of M edge of " + elementName);
      return;
    }
    
    double bax = 0;
    
    if (energy > absorptionEdgeK)
      bax = baxForEdge(energy, "K");
    else if (energy < absorptionEdgeK && energy > absorptionEdgeL)
      bax = baxForEdge(energy, "L");
    else if (energy < absorptionEdgeL && energy > absorptionEdgeM)
      bax = baxForEdge(energy, "M");
    else if (energy < absorptionEdgeM)
      bax = baxForEdge(energy, "N");
    
    // Fortran says...
    // correct for L-edges since McMaster uses L1 edge.
    // Use edge jumps for correct X-sections.
    
    if (atomicNumber <= 29)
    {
      if (energy > this.l3 && energy < this.l2)
        bax /= (LJ_1 * LJ_2);
    
      if (energy > this.l2 && energy < this.absorptionEdgeL)
        bax /= LJ_1;
    }
    
    double bcox = 0;
    double binx = 0;
    
    if (!(this.coherentScatteringCoeff[0] == -1 || this.coherentScatteringCoeff[0] == 0))
    {
      bcox = baxForEdge(energy, "C");
    }
    
    if (!(this.coherentScatteringCoeff[0] == -1 || this.coherentScatteringCoeff[0] == 0))
    {
      binx = baxForEdge(energy, "I");
    }
    
    double btox = bax + bcox + binx;
    
    photoelectricCrossSection = bax;    // mu, abs coefficient
    totalCrossSection = btox;           // attenuation
    coherentCrossSection = bcox;        // elastic
  }
}