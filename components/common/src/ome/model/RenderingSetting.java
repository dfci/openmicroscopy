package ome.model;

import ome.util.BaseModelUtils;
import ome.util.Filterable;
import ome.util.Filter;


import java.util.*;




/**
 * RenderingSetting generated by hbm2java
 */
public class
RenderingSetting 
implements java.io.Serializable ,
ome.api.OMEModel,
ome.util.Filterable {

    // Fields    

     private Integer attributeId;
     private Integer theT;
     private Integer cdEnd;
     private Double inputEnd;
     private Integer model;
     private Integer cdStart;
     private Integer green;
     private Integer alpha;
     private Integer blue;
     private Integer red;
     private Integer bitResolution;
     private Boolean active;
     private Double inputStart;
     private Integer theC;
     private Double coefficient;
     private Integer theZ;
     private Integer family;
     private Experimenter experimenter;
     private Image image;
     private ModuleExecution moduleExecution;


    // Constructors

    /** default constructor */
    public RenderingSetting() {
    }
    
    /** constructor with id */
    public RenderingSetting(Integer attributeId) {
        this.attributeId = attributeId;
    }
   
    
    

    // Property accessors

    /**
     * 
     */
    public Integer getAttributeId() {
        return this.attributeId;
    }
    
    public void setAttributeId(Integer attributeId) {
        this.attributeId = attributeId;
    }

    /**
     * 
     */
    public Integer getTheT() {
        return this.theT;
    }
    
    public void setTheT(Integer theT) {
        this.theT = theT;
    }

    /**
     * 
     */
    public Integer getCdEnd() {
        return this.cdEnd;
    }
    
    public void setCdEnd(Integer cdEnd) {
        this.cdEnd = cdEnd;
    }

    /**
     * 
     */
    public Double getInputEnd() {
        return this.inputEnd;
    }
    
    public void setInputEnd(Double inputEnd) {
        this.inputEnd = inputEnd;
    }

    /**
     * 
     */
    public Integer getModel() {
        return this.model;
    }
    
    public void setModel(Integer model) {
        this.model = model;
    }

    /**
     * 
     */
    public Integer getCdStart() {
        return this.cdStart;
    }
    
    public void setCdStart(Integer cdStart) {
        this.cdStart = cdStart;
    }

    /**
     * 
     */
    public Integer getGreen() {
        return this.green;
    }
    
    public void setGreen(Integer green) {
        this.green = green;
    }

    /**
     * 
     */
    public Integer getAlpha() {
        return this.alpha;
    }
    
    public void setAlpha(Integer alpha) {
        this.alpha = alpha;
    }

    /**
     * 
     */
    public Integer getBlue() {
        return this.blue;
    }
    
    public void setBlue(Integer blue) {
        this.blue = blue;
    }

    /**
     * 
     */
    public Integer getRed() {
        return this.red;
    }
    
    public void setRed(Integer red) {
        this.red = red;
    }

    /**
     * 
     */
    public Integer getBitResolution() {
        return this.bitResolution;
    }
    
    public void setBitResolution(Integer bitResolution) {
        this.bitResolution = bitResolution;
    }

    /**
     * 
     */
    public Boolean getActive() {
        return this.active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * 
     */
    public Double getInputStart() {
        return this.inputStart;
    }
    
    public void setInputStart(Double inputStart) {
        this.inputStart = inputStart;
    }

    /**
     * 
     */
    public Integer getTheC() {
        return this.theC;
    }
    
    public void setTheC(Integer theC) {
        this.theC = theC;
    }

    /**
     * 
     */
    public Double getCoefficient() {
        return this.coefficient;
    }
    
    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    /**
     * 
     */
    public Integer getTheZ() {
        return this.theZ;
    }
    
    public void setTheZ(Integer theZ) {
        this.theZ = theZ;
    }

    /**
     * 
     */
    public Integer getFamily() {
        return this.family;
    }
    
    public void setFamily(Integer family) {
        this.family = family;
    }

    /**
     * 
     */
    public Experimenter getExperimenter() {
        return this.experimenter;
    }
    
    public void setExperimenter(Experimenter experimenter) {
        this.experimenter = experimenter;
    }

    /**
     * 
     */
    public Image getImage() {
        return this.image;
    }
    
    public void setImage(Image image) {
        this.image = image;
    }

    /**
     * 
     */
    public ModuleExecution getModuleExecution() {
        return this.moduleExecution;
    }
    
    public void setModuleExecution(ModuleExecution moduleExecution) {
        this.moduleExecution = moduleExecution;
    }






  public boolean acceptFilter(Filter filter){


	  // Visiting: AttributeId ------------------------------------------
	  Integer _AttributeId = null;
	  try {
	     _AttributeId = getAttributeId();
	  } catch (Exception e) {
		 setAttributeId(null);
	  }

	  setAttributeId((Integer) filter.filter(ATTRIBUTEID,_AttributeId)); 

	  // Visiting: TheT ------------------------------------------
	  Integer _TheT = null;
	  try {
	     _TheT = getTheT();
	  } catch (Exception e) {
		 setTheT(null);
	  }

	  setTheT((Integer) filter.filter(THET,_TheT)); 

	  // Visiting: CdEnd ------------------------------------------
	  Integer _CdEnd = null;
	  try {
	     _CdEnd = getCdEnd();
	  } catch (Exception e) {
		 setCdEnd(null);
	  }

	  setCdEnd((Integer) filter.filter(CDEND,_CdEnd)); 

	  // Visiting: InputEnd ------------------------------------------
	  Double _InputEnd = null;
	  try {
	     _InputEnd = getInputEnd();
	  } catch (Exception e) {
		 setInputEnd(null);
	  }

	  setInputEnd((Double) filter.filter(INPUTEND,_InputEnd)); 

	  // Visiting: Model ------------------------------------------
	  Integer _Model = null;
	  try {
	     _Model = getModel();
	  } catch (Exception e) {
		 setModel(null);
	  }

	  setModel((Integer) filter.filter(MODEL,_Model)); 

	  // Visiting: CdStart ------------------------------------------
	  Integer _CdStart = null;
	  try {
	     _CdStart = getCdStart();
	  } catch (Exception e) {
		 setCdStart(null);
	  }

	  setCdStart((Integer) filter.filter(CDSTART,_CdStart)); 

	  // Visiting: Green ------------------------------------------
	  Integer _Green = null;
	  try {
	     _Green = getGreen();
	  } catch (Exception e) {
		 setGreen(null);
	  }

	  setGreen((Integer) filter.filter(GREEN,_Green)); 

	  // Visiting: Alpha ------------------------------------------
	  Integer _Alpha = null;
	  try {
	     _Alpha = getAlpha();
	  } catch (Exception e) {
		 setAlpha(null);
	  }

	  setAlpha((Integer) filter.filter(ALPHA,_Alpha)); 

	  // Visiting: Blue ------------------------------------------
	  Integer _Blue = null;
	  try {
	     _Blue = getBlue();
	  } catch (Exception e) {
		 setBlue(null);
	  }

	  setBlue((Integer) filter.filter(BLUE,_Blue)); 

	  // Visiting: Red ------------------------------------------
	  Integer _Red = null;
	  try {
	     _Red = getRed();
	  } catch (Exception e) {
		 setRed(null);
	  }

	  setRed((Integer) filter.filter(RED,_Red)); 

	  // Visiting: BitResolution ------------------------------------------
	  Integer _BitResolution = null;
	  try {
	     _BitResolution = getBitResolution();
	  } catch (Exception e) {
		 setBitResolution(null);
	  }

	  setBitResolution((Integer) filter.filter(BITRESOLUTION,_BitResolution)); 

	  // Visiting: Active ------------------------------------------
	  Boolean _Active = null;
	  try {
	     _Active = getActive();
	  } catch (Exception e) {
		 setActive(null);
	  }

	  setActive((Boolean) filter.filter(ACTIVE,_Active)); 

	  // Visiting: InputStart ------------------------------------------
	  Double _InputStart = null;
	  try {
	     _InputStart = getInputStart();
	  } catch (Exception e) {
		 setInputStart(null);
	  }

	  setInputStart((Double) filter.filter(INPUTSTART,_InputStart)); 

	  // Visiting: TheC ------------------------------------------
	  Integer _TheC = null;
	  try {
	     _TheC = getTheC();
	  } catch (Exception e) {
		 setTheC(null);
	  }

	  setTheC((Integer) filter.filter(THEC,_TheC)); 

	  // Visiting: Coefficient ------------------------------------------
	  Double _Coefficient = null;
	  try {
	     _Coefficient = getCoefficient();
	  } catch (Exception e) {
		 setCoefficient(null);
	  }

	  setCoefficient((Double) filter.filter(COEFFICIENT,_Coefficient)); 

	  // Visiting: TheZ ------------------------------------------
	  Integer _TheZ = null;
	  try {
	     _TheZ = getTheZ();
	  } catch (Exception e) {
		 setTheZ(null);
	  }

	  setTheZ((Integer) filter.filter(THEZ,_TheZ)); 

	  // Visiting: Family ------------------------------------------
	  Integer _Family = null;
	  try {
	     _Family = getFamily();
	  } catch (Exception e) {
		 setFamily(null);
	  }

	  setFamily((Integer) filter.filter(FAMILY,_Family)); 

	  // Visiting: Experimenter ------------------------------------------
	  Experimenter _Experimenter = null;
	  try {
	     _Experimenter = getExperimenter();
	  } catch (Exception e) {
		 setExperimenter(null);
	  }

	  setExperimenter((Experimenter) filter.filter(EXPERIMENTER,_Experimenter)); 

	  // Visiting: Image ------------------------------------------
	  Image _Image = null;
	  try {
	     _Image = getImage();
	  } catch (Exception e) {
		 setImage(null);
	  }

	  setImage((Image) filter.filter(IMAGE,_Image)); 

	  // Visiting: ModuleExecution ------------------------------------------
	  ModuleExecution _ModuleExecution = null;
	  try {
	     _ModuleExecution = getModuleExecution();
	  } catch (Exception e) {
		 setModuleExecution(null);
	  }

	  setModuleExecution((ModuleExecution) filter.filter(MODULEEXECUTION,_ModuleExecution)); 
   	 return true;
  }
  
  public String toString(){
	return "RenderingSetting"+(attributeId==null ? ":Hash_"+this.hashCode() : ":Id_"+attributeId);
  }
  
  // FIELD-FIELDS
  
	public final static String ATTRIBUTEID = "RenderingSetting_AttributeId";
	public final static String THET = "RenderingSetting_TheT";
	public final static String CDEND = "RenderingSetting_CdEnd";
	public final static String INPUTEND = "RenderingSetting_InputEnd";
	public final static String MODEL = "RenderingSetting_Model";
	public final static String CDSTART = "RenderingSetting_CdStart";
	public final static String GREEN = "RenderingSetting_Green";
	public final static String ALPHA = "RenderingSetting_Alpha";
	public final static String BLUE = "RenderingSetting_Blue";
	public final static String RED = "RenderingSetting_Red";
	public final static String BITRESOLUTION = "RenderingSetting_BitResolution";
	public final static String ACTIVE = "RenderingSetting_Active";
	public final static String INPUTSTART = "RenderingSetting_InputStart";
	public final static String THEC = "RenderingSetting_TheC";
	public final static String COEFFICIENT = "RenderingSetting_Coefficient";
	public final static String THEZ = "RenderingSetting_TheZ";
	public final static String FAMILY = "RenderingSetting_Family";
	public final static String EXPERIMENTER = "RenderingSetting_Experimenter";
	public final static String IMAGE = "RenderingSetting_Image";
	public final static String MODULEEXECUTION = "RenderingSetting_ModuleExecution";
 	public final static Set FIELDS = new HashSet();
	static {
	   FIELDS.add(ATTRIBUTEID);
	   FIELDS.add(THET);
	   FIELDS.add(CDEND);
	   FIELDS.add(INPUTEND);
	   FIELDS.add(MODEL);
	   FIELDS.add(CDSTART);
	   FIELDS.add(GREEN);
	   FIELDS.add(ALPHA);
	   FIELDS.add(BLUE);
	   FIELDS.add(RED);
	   FIELDS.add(BITRESOLUTION);
	   FIELDS.add(ACTIVE);
	   FIELDS.add(INPUTSTART);
	   FIELDS.add(THEC);
	   FIELDS.add(COEFFICIENT);
	   FIELDS.add(THEZ);
	   FIELDS.add(FAMILY);
	   FIELDS.add(EXPERIMENTER);
	   FIELDS.add(IMAGE);
	   FIELDS.add(MODULEEXECUTION);
 	}


}
