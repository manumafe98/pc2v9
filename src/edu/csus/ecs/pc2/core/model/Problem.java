package edu.csus.ecs.pc2.core.model;

import java.io.File;

import edu.csus.ecs.pc2.core.StringUtilities;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.log.StaticLog;
import edu.csus.ecs.pc2.validator.ClicsValidatorSettings;
import edu.csus.ecs.pc2.validator.PC2ValidatorSettings;
import edu.csus.ecs.pc2.validator.customValidator.CustomValidatorSettings;

/**
 * Problem Definition.
 * 
 * This contains settings for a problem.  Data files
 * are not in this class, data files are in the {@link edu.csus.ecs.pc2.core.model.ProblemDataFiles}
 * class.
 * 
 * @see edu.csus.ecs.pc2.core.list.ProblemList
 * @see edu.csus.ecs.pc2.core.model.ProblemDataFiles
 * 
 * @author pc2@ecs.csus.edu
 */
public class Problem implements IElementObject {

    private static final long serialVersionUID = 1708763261096488240L;

    public static final int DEFAULT_TIMEOUT_SECONDS = 30;
    

    /**
     * Problem title.
     */
    private String displayName = null;

    /**
     * Unique id for problem.
     */
    private ElementId elementId = null;

    /**
     * Sequence number (rank) for problem.
     */
    private int number;

    /**
     * Judge's data file name.
     * 
     * This is the name of the file that the submitted run will read from.
     */
    private String judgesInputDataFileName = null;

    /**
     * Judge's answer file name.
     * 
     * This is the name of the file that the validator will read/use to compare against the submitted run output.
     */
    private String answerFileName = null;
    
    /**
     * List of judge's file names, for multiple test cases.
     *  
     */
    private String [] testCaseDataFilenames = new String[0];
    
    /**
     * List of judge's answer file names, for multiple test cases.
     */
    private String [] testCaseAnswerFilenames = new String[0];;
    
    /**
     * Whether or not the problem should be shown to teams.
     */
    private boolean active = true;

    /**
     * Input is from stdin.
     */
    private boolean readInputDataFromSTDIN = false;

    /**
     * Seconds per problem run.
     */
    private int timeOutInSeconds = DEFAULT_TIMEOUT_SECONDS;

    /**
     * This enum defines the types of Validators which a Problem can have.
     */
    public enum VALIDATOR_TYPE {
        /**
         * The Problem has no associated Validator; it is not a Validated Problem.
         */
        NONE, 
        /**
         * The Problem uses the PC2 Validator, also known as the "Internal" Validator.
         */
        PC2VALIDATOR, 
        /**
         * The Problem uses the PC2 implementation of the CLICS Validator.
         */
        CLICSVALIDATOR, 
        /**
         * The Problem uses a Custom (user-provided) Validator.
         */
        CUSTOMVALIDATOR
        }
    
    //The type of validator associated with this Problem.
    private VALIDATOR_TYPE validatorType = VALIDATOR_TYPE.NONE ;
    
    //the settings for each possible type of validator used by the problem
    private PC2ValidatorSettings pc2ValidatorSettings ;
    private ClicsValidatorSettings clicsValidatorSettings ;
    private CustomValidatorSettings customValidatorSettings ;

    /**
     * Use international judgement method.
     */
    private boolean internationalJudgementReadMethod = true;

    /**
     * This is the command executed before the run is executed.
     */
    private String executionPrepCommand = null;
     
    /**
     * Display validation output window to judges.
     */
    private boolean showValidationToJudges = false;

    /**
     * Hide Output window from Judges.
     */
    private boolean hideOutputWindow = false;
    
    /**
     * Show PC2 Compare Window?.
     * 
     */
    private boolean showCompareWindow = false;

    /**
     * should the problem be "Auto Judged", this will require a validator be defined.
     */
    private boolean computerJudged = false;
    
    /**
     * Should the problem be send to a human for review after it has been autojudged.
     * only used if computerJudged is TRUE
     */
    private boolean manualReview = false;
    
    /**
     * should a team be notified of the Computer Judgement (immediately)
     * only used if manualReview is TRUE
     */
    private boolean prelimaryNotification = false;
    
    private String shortName = "";
    
    private String letter = null;

    private String colorName;

    private String colorRGB;

    /**
     * Files are not stored on pc2 server, they are at an external location 
     * pointed to by {@link #getDataLoadYAMLPath()}.
     */
    private boolean usingExternalDataFiles = false;
    
    /**
     * Base location where external data files are stored.
     */
    private String externalDataFileLocation = null;
    
    /**
     * Problem State.
     * 
     * @author pc2@ecs.csus.edu
     * @version $Id$
     */
    
    // $HeadURL$
    public enum State {
        /**
         * Active.
         */
        ENABLED, 
        /**
         * Not accepting runs
         */
        PAUSED, 
        /**
         * Not accepting runs.
         */
        DISABLED 
    }
    
    private State state = State.ENABLED;

    
    /**
     * Create a problem with the display name.
     * 
     * @param displayName
     */
    public Problem(String displayName) {
        super();
        this.displayName = displayName;
        elementId = new ElementId(displayName);
        setSiteNumber(0);
        this.pc2ValidatorSettings = new PC2ValidatorSettings();
        this.clicsValidatorSettings = new ClicsValidatorSettings();
        this.customValidatorSettings = new CustomValidatorSettings();
    }

    public Problem copy(String newDisplayName) {
        Problem clone = new Problem(newDisplayName);
        // inherited field
        clone.setSiteNumber(getSiteNumber());
        
        // local fields
        clone.setDisplayName(newDisplayName);
        // TODO is number really used?
        clone.setNumber(getNumber());
        // TODO FATAL files need the corresponding ProblemDataFile populated...
        clone.setDataFileName(StringUtilities.cloneString(judgesInputDataFileName));
        clone.setAnswerFileName(StringUtilities.cloneString(answerFileName));
        clone.setActive(isActive());
        clone.setReadInputDataFromSTDIN(isReadInputDataFromSTDIN());
        clone.setTimeOutInSeconds(getTimeOutInSeconds());
        
        //validator settings
        clone.setValidatorType(this.getValidatorType());
        
        if (this.getPC2ValidatorSettings()!=null) {
            clone.setPC2ValidatorSettings(this.getPC2ValidatorSettings().clone());
        } else {
            clone.setPC2ValidatorSettings(null);
        }
        if (this.getClicsValidatorSettings()!=null) {
            clone.setCLICSValidatorSettings(this.getClicsValidatorSettings().clone());
        } else {
            clone.setCLICSValidatorSettings(null);
        }
        if (this.getCustomValidatorSettings()!=null) {
            clone.setCustomValidatorSettings(this.getCustomValidatorSettings().clone());
        } else {
            clone.setCustomValidatorSettings(null);
        }

        clone.setInternationalJudgementReadMethod(isInternationalJudgementReadMethod());

        // TODO Implement Commands to be executed before a problem is run
        // private String executionPrepCommand = "";
        // private SerializedFile executionPrepFile;

        clone.setShowValidationToJudges(isShowValidationToJudges());
        clone.setHideOutputWindow(isHideOutputWindow());
        clone.setShowCompareWindow(isShowCompareWindow());
        clone.setComputerJudged(isComputerJudged());
        clone.setManualReview(isManualReview());
        clone.setPrelimaryNotification(isPrelimaryNotification());
        clone.letter = StringUtilities.cloneString(letter);
        clone.shortName = StringUtilities.cloneString(shortName);
        
        clone.externalDataFileLocation = StringUtilities.cloneString(getExternalDataFileLocation());
        clone.usingExternalDataFiles = usingExternalDataFiles;
        
        if (getNumberTestCases() > 1){
            for (int i = 0 ; i < getNumberTestCases(); i++){
                String datafile = StringUtilities.cloneString(getDataFileName(i + 1));
                String answerfile = StringUtilities.cloneString(getAnswerFileName(i + 1));
                clone.addTestCaseFilenames(datafile, answerfile);
            }
        }
        
        return clone;
    }


    /**
     * @see Object#equals(java.lang.Object).
     */
    public boolean equals(Object obj) {
        if (this==obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Problem) {
            Problem otherProblem = (Problem) obj;
            return elementId.equals(otherProblem.elementId);
        } else {
            throw new ClassCastException("expected a Problem found: " + obj.getClass().getName());
        }
    }

    
    /**
     * Output the title for the problem.
     */
    public String toString() {
        return displayName;
    }
    
    /**
     * Output details of the problem.
     */
    public String toStringDetails() {
        String retStr = "Problem[";
        
        retStr += "displayName=" + displayName;
        retStr += "; elementId=" + elementId;
        retStr += "; number=" + number;
        retStr += "; dataFileName=" + judgesInputDataFileName;
        retStr += "; answerFileName=" + answerFileName;
        retStr += "; testCaseDataFilenames=" + testCaseDataFilenames;
        retStr += "; testCaseAnswerFilenames=" + testCaseAnswerFilenames;
        retStr += "; active=" + active;
        retStr += "; readInputDataFromSTDIN=" + readInputDataFromSTDIN;
        retStr += "; timeOutInSeconds=" + timeOutInSeconds;
        
        boolean validatedProblem = getValidatorType()==VALIDATOR_TYPE.NONE;
        retStr += "; validatedProblem=" + validatedProblem;
        retStr += "; validatorType=" + getValidatorType();
        retStr += "; pc2ValidatorSettings=" + getPC2ValidatorSettings();
        retStr += "; clicsValidatorSettings=" + getClicsValidatorSettings();
        retStr += "; customValidatorSettings=" + getCustomValidatorSettings();
        
        retStr += "; internationalJudgementReadMethod=" + internationalJudgementReadMethod;
        retStr += "; executionPrepCommand=" + executionPrepCommand;

        retStr += "; showValidationToJudges=" + showValidationToJudges;
        retStr += "; hideOutputWindow=" + hideOutputWindow;
        retStr += "; showCompareWindow=" + showCompareWindow;
        retStr += "; computerJudged=" + computerJudged;
        retStr += "; manualReview=" + manualReview;
        retStr += "; prelimaryNotification=" + prelimaryNotification;
        retStr += "; shortName=" + shortName;
        retStr += "; letter=" + letter;
        retStr += "; colorName=" + colorName;
        retStr += "; colorRGB=" + colorRGB;
        retStr += "; usingExternalDataFiles=" + usingExternalDataFiles;
        retStr += "; externalDataFileLocation=" + externalDataFileLocation;
        retStr += "; state=" + state;
      
        retStr += "]";
        return retStr;
    }

    /**
     * @return Returns the elementId.
     */
    public ElementId getElementId() {
        return elementId;
    }

    /**
     * @return Returns the displayName.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName
     *            The displayName to set.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Return whether is active (is not hidden).
     * @return true if active, false if hidden/deleted.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @return Returns the answerFileName.
     */
    public String getAnswerFileName() {
        return answerFileName;
    }
    
    public String getAnswerFileName(int testCaseNumber) {
        if (testCaseNumber == 1 && testCaseAnswerFilenames.length == 0){
            return answerFileName;
        }
        return testCaseAnswerFilenames[testCaseNumber - 1];
    }


    /**
     * @return Returns the dataFileName.
     */
    public String getDataFileName() {
        return judgesInputDataFileName;
    }

    /**
     * Get test case data file name.
     * 
     * Test case numbers start at 1.
     * 
     * @return returns data file name for test case.
     */
    public String getDataFileName(int testCaseNumber) {
        if (testCaseNumber == 1 && testCaseDataFilenames.length == 0){
            return judgesInputDataFileName;
        }
        return testCaseDataFilenames[testCaseNumber - 1];
    }


    /**
     * @return Returns the internationalJudgementReadMethod flag
     */
    public boolean isInternationalJudgementReadMethod() {
        return internationalJudgementReadMethod;
    }

    /**
     * @return Returns the readInputDataFromSTDIN flag
     */
    public boolean isReadInputDataFromSTDIN() {
        return readInputDataFromSTDIN;
    }

    /**
     * @return Returns the showValidationToJudges flag
     */
    public boolean isShowValidationToJudges() {
        return showValidationToJudges;
    }

    /**
     * @return Returns the timeOutInSeconds
     */
    public int getTimeOutInSeconds() {
        return timeOutInSeconds;
    }

    /**
     * Returns the state variable indicating what type of Validator this Problem is using.
     * The returned value will be an element of the enumerated type {@link edu.csus.ecs.pc2.core.Problem.VALIDATOR_TYPE};
     * note that this enumeration includes "NONE" to indicate that a Problem has no Validator attached.
     * 
     * @see {@link edu.csus.ecs.pc2.core.Problem.VALIDATOR_TYPE}
     * @see {@link #isValidatedProblem()}
     */
    public VALIDATOR_TYPE getValidatorType() {
        return this.validatorType;
    }
    
   /**
     * Sets the state variable indicating what type of Validator this problem is using.
     * Note that one possible value of this variable is "NONE", indicating the Problem is not validated.
     * 
     * @see #isValidatedProblem()
     * 
     * @param valType a {@link edu.csus.ecs.pc2.core.model.Problem.VALIDATOR_TYPE} indicating the 
     *              type of validator used by this Problem
     */
    public void setValidatorType(VALIDATOR_TYPE valType) {
        this.validatorType = valType;
    }
    /**
     * Returns whether the Problem is using the PC2Validator (as opposed to a Custom Validator, 
     * the CLICS Validator, or no Validator).  
     *          
     * @return true if the Problem is using the PC2Validator
     */
    public boolean isUsingPC2Validator() {
        return getValidatorType()==VALIDATOR_TYPE.PC2VALIDATOR;
    }

    /**
     * Returns whether this Problem is using the CLICS Validator (as opposed to a Custom Validator, 
     * the  PC2Validator, or no Validator).  
     *          
     * @return true if the Problem is using the CLICS Validator
     */
    public boolean isUsingCLICSValidator() {
        return getValidatorType()==VALIDATOR_TYPE.CLICSVALIDATOR;
    }

    /**
     * Returns whether this Problem is using a Custom (user-supplied) Validator 
     * (as opposed to the CLICS Validator, the PC2Validator, or no Validator).
     *          
     * @return true if the Problem is using a Custom validator
     */
    public boolean isUsingCustomValidator() {
        return getValidatorType()==VALIDATOR_TYPE.CUSTOMVALIDATOR;
    }

    /**
     * @return whether this Problem has a validator or not.
     */
    public boolean isValidatedProblem() {
        return ! (getValidatorType()==VALIDATOR_TYPE.NONE);
    }

    /**
     * Returns the Validator Command Line associated with this Problem, if the Problem is using a Validator;
     * returns null otherwise.
     * 
     * @return the validatorCommandLine for the Problem's validator, or null if the Problem is not using a Validator.
     * 
     * @throws {@link RuntimeException} if the Problem is marked as using a Validator but no corresponding Validator
     *              Settings could be found.
     */
    public String getValidatorCommandLine() {
        if (!isValidatedProblem()) {
            return null;
        }
        
        //search for ValidatorSettings for the currently-specified Validator; if found, return the ValidatorCommandLine
        // from those Settings
        String validatorCommandLine = null;
        boolean found = false;
        if (isUsingPC2Validator()) {
            if (getPC2ValidatorSettings()!=null) {
                validatorCommandLine = getPC2ValidatorSettings().getValidatorCommandLine();
                found = true;
            }
        } else if (isUsingCLICSValidator()) {
            if (getClicsValidatorSettings()!=null) {
                validatorCommandLine = getClicsValidatorSettings().getValidatorCommandLine();
                found = true;
            }
        } else if (isUsingCustomValidator()) {
            if (getCustomValidatorSettings()!=null) {
                validatorCommandLine = getCustomValidatorSettings().getCustomValidatorCommandLine();
                found = true;
            }
        }
        
        if (!found) {
            throw new RuntimeException("getValidatorCommandLine(): unable to locate Settings for currently-specified Validator '"
                    + getValidatorType() + "'");                
        } else {
            return validatorCommandLine;
        }
    }

    /**
     * Sets the Validator Command Line associated with type of Validator configured for this Problem.
     * Note that this Problem class does not maintain a separate "Validator Command Line" field;
     * rather, the current Validator Command Line is always stored within a "Settings" object 
     * corresponding to the currently-assigned Validator type.
     * 
     * @param commandLine the new command line for the currently-specified Validator type associated with the Problem
     * 
     * @see PC2ValidatorSettings
     * @see ClicsValidatorSettings
     * @see CustomValidatorSettings
     * 
     * @throws {@link RuntimeException} if the Problem is not marked as using a Validator, or is marked as using a Validator
     *           but no corresponding Validator Settings object could be found.
     */
    public void setValidatorCommandLine(String commandLine) {
        
        if (!isValidatedProblem()) {
            throw new RuntimeException("setValidatorCommandLine(): no Validator configured for Problem");                
        }
        
        //search for ValidatorSettings for the currently-specified Validator; if found, set the ValidatorCommandLine
        // into those Settings
        boolean found = false;
        if (isUsingPC2Validator()) {
            if (getPC2ValidatorSettings()!=null) {
                getPC2ValidatorSettings().setValidatorCommandLine(commandLine);
                found = true;
            }
        } else if (isUsingCLICSValidator()) {
            if (getClicsValidatorSettings()!=null) {
                getClicsValidatorSettings().setValidatorCommandLine(commandLine);
                found = true;
            }
        } else if (isUsingCustomValidator()) {
            if (getCustomValidatorSettings()!=null) {
                getCustomValidatorSettings().setValidatorCommandLine(commandLine);
                found = true;
            }
        }
        
        if (!found) {
            throw new RuntimeException("setValidatorCommandLine(): unable to locate Settings for currently-specified Validator");                
        } 
    }

    /**
     * @param active
     *            The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @param answerFileName
     *            The answerFileName to set.
     */
    public void setAnswerFileName(String answerFileName) {
        this.answerFileName = answerFileName;
    }

    /**
     * @param dataFileName
     *            The dataFileName to set.
     */
    public void setDataFileName(String dataFileName) {
        this.judgesInputDataFileName = dataFileName;
    }

    /**
     * @param elementId
     *            The elementId to set.
     */
    public void setElementId(ElementId elementId) {
        this.elementId = elementId;
    }

    /**
     * @param internationalJudgementReadMethod
     *            The internationalJudgementReadMethod to set.
     */
    public void setInternationalJudgementReadMethod(boolean internationalJudgementReadMethod) {
        this.internationalJudgementReadMethod = internationalJudgementReadMethod;
    }

    /**
     * @param readInputDataFromSTDIN
     *            The readInputDataFromSTDIN to set.
     */
    public void setReadInputDataFromSTDIN(boolean readInputDataFromSTDIN) {
        this.readInputDataFromSTDIN = readInputDataFromSTDIN;
    }

    /**
     * @param showValidationToJudges
     *            The showValidationToJudges to set.
     */
    public void setShowValidationToJudges(boolean showValidationToJudges) {
        this.showValidationToJudges = showValidationToJudges;
    }

    /**
     * @param timeOutInSeconds
     *            The timeOutInSeconds to set.
     */
    public void setTimeOutInSeconds(int timeOutInSeconds) {
        this.timeOutInSeconds = timeOutInSeconds;
    }

    /**
     * @return Returns the hideOutputWindow.
     */
    public boolean isHideOutputWindow() {
        return hideOutputWindow;
    }

    /**
     * @param hideOutputWindow
     *            The value to which the hideOutputWindow should be set.
     */
    public void setHideOutputWindow(boolean hideOutputWindow) {
        this.hideOutputWindow = hideOutputWindow;
    }

    /**
     * Returns the Validator Program Name if the Problem has a Validator attached; otherwise returns null.
     * 
     * @return the validatorProgramName if there is a validator for the Problem, or null if not
     * 
     * @throws {@link RuntimeException} if the Problem is marked as having a Validator but no Validator Settings could be found
     */
    public String getValidatorProgramName() {

        if (!isValidatedProblem()) {
            return null;
        }

        // search for ValidatorSettings for the currently-specified Validator; if found, return the ValidatorProgramName
        // from those Settings
        String validatorProgName = null;
        boolean found = false;
        if (isUsingPC2Validator()) {
            if (getPC2ValidatorSettings() != null) {
                validatorProgName = getPC2ValidatorSettings().getValidatorProgramName();
                found = true;
            }
        } else if (isUsingCLICSValidator()) {
            if (getClicsValidatorSettings() != null) {
                validatorProgName = getClicsValidatorSettings().getValidatorProgramName();
                found = true;
            }
        } else if (isUsingCustomValidator()) {
            if (getCustomValidatorSettings() != null) {
                validatorProgName = getCustomValidatorSettings().getCustomValidatorProgramName();
                found = true;
            }
        }

        if (!found) {
            throw new RuntimeException("getValidatorProgramName(): unable to locate Settings for currently-specified Validator");
        } else {
            return validatorProgName;
        }
    }

    /**
     * Sets the Validator Program Name for the Validator attached to the Problem.
     * Note that this Problem class does not maintain a separate "Validator Program Name" field;
     * rather, the Validator Program Name is stored within the "Settings" object associated with the
     * type of Validator attached to the Problem.
     *  
     * @param validatorProgramName a String specifying the new Validator Program Name
     *            
     * @see PC2ValidatorSettings
     * @see ClicsValidatorSettings
     * @see CustomValidatorSettings
     * 
     * @throws {@link RuntimeException} if the Problem is not marked as having a Validator when an attempt is made to set
     *          set the Validator Program name, or if the Problem is marked as having a Validator but no Validator Settings 
     *          object could be found
     */
    public void setValidatorProgramName(String validatorProgramName) {
        
        if (!this.isValidatedProblem()) {
            throw new RuntimeException("Cannot set a Validator Program Name on a Problem marked as not using a Validator");
        }
        
        // search for ValidatorSettings for the currently-specified Validator; if found, set the ValidatorProgramName
        // into those Settings
        boolean found = false;
        if (isUsingPC2Validator()) {
            if (getPC2ValidatorSettings() != null) {
                getPC2ValidatorSettings().setValidatorProgramName(validatorProgramName);
                found = true;
            }
        } else if (isUsingCLICSValidator()) {
            if (getClicsValidatorSettings() != null) {
                getClicsValidatorSettings().setValidatorProgramName(validatorProgramName);
                found = true;
            }
        } else if (isUsingCustomValidator()) {
            if (getCustomValidatorSettings() != null) {
                getCustomValidatorSettings().setValidatorProgramName(validatorProgramName);
                found = true;
            }
        }

        if (!found) {
            throw new RuntimeException("setValidatorProgramName(): unable to locate Settings for currently-specified Validator");
        } 
    }

    /**
     * Returns the Problem Number.
     * @return the Problem Number
     */
    protected int getNumber() {
        return number;
    }

    /**
     * Sets the Problem Number.
     * @param number the number for the Problem
     */
    protected void setNumber(int number) {
        this.number = number;
    }

    public int versionNumber() {
        return elementId.getVersionNumber();
    }

    public int getSiteNumber() {
        return elementId.getSiteNumber();
    }

    public void setSiteNumber(int siteNumber) {
        elementId.setSiteNumber(siteNumber);
    }

    /**
     * Returns an indication of which option has been selected when using the PC2Validator.
     * 
     * @return an integer indicating which PC2Validator option has been specified, 
     *              or -1 if no PC2Validator Settings for the Problem could be found
     */
    public int getWhichPC2Validator() {

        if (getPC2ValidatorSettings() != null) {
            return getPC2ValidatorSettings().getWhichPC2Validator();
        } else {
            return -1;
        }
    }

    /**
     * Sets the value indicating which option has been selected when using the PC2Validator.
     * 
     * @param whichPC2Validator -- the integer value to which the PC2Validator option should be set
     * 
     * @throws {@link RuntimeException} if there is no PC2 Validator Settings object attached to the Problem
     */
    public void setWhichPC2Validator(int whichPC2Validator) {
        
        if (getPC2ValidatorSettings()!=null) {
            getPC2ValidatorSettings().setWhichPC2Validator(whichPC2Validator);
        } else {
            throw new RuntimeException("setWhichPC2Validator(): no PC2 Validator Settings found in the Problem");
        }
    }

    public int hashCode() {
        return getElementId().toString().hashCode();
    }
    
    public boolean isSameAs(Problem problem) {

        try {
            if (problem == null){
                return false;
            }
            if (! StringUtilities.stringSame(displayName, problem.getDisplayName())){
                return false;
            }
            if (isActive() != problem.isActive()) {
                return false;
            }
            if (timeOutInSeconds != problem.getTimeOutInSeconds()) {
                return false;
            }

            if (! StringUtilities.stringSame(judgesInputDataFileName, problem.getDataFileName())) {
                return false;
            }
            if (! StringUtilities.stringSame(answerFileName, problem.getAnswerFileName())) {
                return false;
            }
            if (!readInputDataFromSTDIN == problem.isReadInputDataFromSTDIN()) {
                return false;
            }
            
            if (this.isValidatedProblem() != problem.isValidatedProblem()) {
                return false;
            }

            if (this.getValidatorType() != problem.getValidatorType()) {
                return false;
            }

            // check for one PC2ValidatorSettings being null while the other is not (i.e., XOR says they are different)
            if (this.getPC2ValidatorSettings()==null ^ problem.getPC2ValidatorSettings()==null) {
                return false;
            }
            // check that if both Settings are non-null, they are the same (if one is non-null, the other must also be, due to the XOR above)
            if (this.getPC2ValidatorSettings() != null) {
                if (!this.getPC2ValidatorSettings().equals(problem.getPC2ValidatorSettings())) {
                    return false;
                }
            }

            // check for one ClicsValidatorSettings being null while the other is not (i.e., XOR says they are different)
            if (this.getClicsValidatorSettings()==null ^ problem.getClicsValidatorSettings()==null) {
                return false;
            }
            // check that if both Settings are non-null, they are the same (if one is non-null, the other must also be, due to the XOR above)
            if (this.getClicsValidatorSettings() != null) {
                if (!this.getClicsValidatorSettings().equals(problem.getClicsValidatorSettings())) {
                    return false;
                }
            }

            // check for one CustomValidatorSettings being null while the other is not (i.e., XOR says they are different)
            if (this.getCustomValidatorSettings()==null ^ problem.getCustomValidatorSettings()==null) {
                return false;
            }
            // check that if both Settings are non-null, they are the same (if one is non-null, the other must also be, due to the XOR above)
            if (this.getCustomValidatorSettings() != null) {
                if (!this.getCustomValidatorSettings().equals(problem.getCustomValidatorSettings())) {
                    return false;
                }
            }
            
           if (showValidationToJudges != problem.isShowValidationToJudges()) {
                return false;
            }
            
            if (hideOutputWindow != problem.isHideOutputWindow()) {
                return false;
            }
            if (showCompareWindow != problem.isShowCompareWindow()) {
                return false;
            }
            if (computerJudged != problem.isComputerJudged()) {
                return false;
            }
            if (manualReview != problem.isManualReview()) {
                return false;
            }
            if (prelimaryNotification != problem.isPrelimaryNotification()) {
                return false;
            }
            
            if (getSiteNumber() != problem.getSiteNumber()){
                return false;
            }
            
            if (! StringUtilities.stringSame(shortName, problem.getShortName())){
                return false;
            }
            
            if (! StringUtilities.stringSame(externalDataFileLocation, problem.getExternalDataFileLocation())){
                return false;
            }
            
            if (usingExternalDataFiles != problem.usingExternalDataFiles) {
                return false;
            }

            if (! StringUtilities.stringSame(shortName, problem.getShortName())){
                return false;
            }
            // TODO 917 - do isameAs when test case filenames can be added.
//            if (! StringUtilities.stringArraySame(testCaseDataFilenames, problem.getTestCaseDataFilenames())) {
//                return false;
//            }
//            if (! StringUtilities.stringArraySame(testCaseAnswerFilenames, problem.getTestCaseAnswerFilenames())) {
//                return false;
//            }
            
            return true;
        } catch (Exception e) {
            StaticLog.getLog().log(Log.WARNING, "Exception comparing Problem "+e.getMessage(), e);
            e.printStackTrace(System.err);
            return false;
        }
    }

    public boolean isShowCompareWindow() {
        return showCompareWindow;
    }

    public void setShowCompareWindow(boolean showCompareWindow) {
        this.showCompareWindow = showCompareWindow;
    }

    public boolean isComputerJudged() {
        return computerJudged;
    }

    public void setComputerJudged(boolean computerJudged) {
        this.computerJudged = computerJudged;
    }

    public boolean isManualReview() {
        return manualReview;
    }

    public void setManualReview(boolean manualReview) {
        this.manualReview = manualReview;
    }

    public boolean isPrelimaryNotification() {
        return prelimaryNotification;
    }

    public void setPrelimaryNotification(boolean prelimaryNotification) {
        this.prelimaryNotification = prelimaryNotification;
    }

    /**
     * Get short name for problem.
     * 
     * @return
     */
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    
    /**
     * Valid short name?. <br>
     * 
     * A valid short name does not contain path or drive delimiters, these symbols : / \
     * 
     * @param name
     * @return true if valid, false otherwise.
     */
    public boolean isValidShortName(String name) {
        boolean invalidName = name == null || name.contains(File.separator) || //
                name.contains(":") || name.contains("/") || name.contains("\\");
        return !invalidName;
    }

    public boolean isValidShortName() {
        return isValidShortName(shortName);
    }
    
    /**
     * Get letter for problem.
     * 
     * @return
     */
    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }
    
    /**
     * Add data and answer filenames to list of test cases.
     *  
     * @see #removeAllTestCaseFilenames()
     * @param datafile
     * @param answerfile
     */
    public void addTestCaseFilenames (String datafile, String answerfile){
        
        String[] newArray;

        if (datafile != null) {
            newArray = StringUtilities.appendString(testCaseDataFilenames, datafile);
            testCaseDataFilenames = newArray;
        }
        
        if (answerfile != null){
            newArray = StringUtilities.appendString(testCaseAnswerFilenames, answerfile);
            testCaseAnswerFilenames = newArray;
        }
    }
    
    
    /**
     * Remove all test case filenames.
     */
    public void removeAllTestCaseFilenames(){
        testCaseDataFilenames = new String[0];
        testCaseAnswerFilenames = new String[0];
    }
    
    public int getNumberTestCases() {
        if (testCaseDataFilenames != null && testCaseDataFilenames.length > 0) {
            return testCaseDataFilenames.length;
        } else if (getAnswerFileName() != null && getDataFileName() != null) {
            return 1;
        } else {
            return 0;
        }
    }

    public String getExecutionPrepCommand() {
        return executionPrepCommand;
    }

    public void setExecutionPrepCommand(String executionPrepCommand) {
        this.executionPrepCommand = executionPrepCommand;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }
    
    public String getColorName() {
        return colorName;
    }

    public void setColorRGB(String colorRGB) {
        this.colorRGB = colorRGB;
    }
    
    public String getColorRGB() {
        return colorRGB;
    }

    
    /**
     * @return true if not storing files on pc2 server.
     */
    public boolean isUsingExternalDataFiles() {
        return usingExternalDataFiles;
    }

    /**
     * @param usingExternalDataFiles
     */
    public void setUsingExternalDataFiles(boolean usingExternalDataFiles) {
        this.usingExternalDataFiles = usingExternalDataFiles;
    }

    /**
     * Local file path for external data files.
     * 
     * @param dataLoadYAMLPath
     */
    public void setExternalDataFileLocation(String externalDataFileLocation) {
        this.externalDataFileLocation = externalDataFileLocation;
    }
    
    public String getExternalDataFileLocation() {
        return externalDataFileLocation;
    }
    
    /**
     * Return external judges data file (location).
     * 
     * Searches both the dir found in {@link #getExternalDataFileLocation()} or
     * in that location plus the CCS standard location for that file.
     * 
     * @param dataSetNumber 
     * @return null if not found, else the path for the file.
     */
    public File locateJudgesDataFile (int dataSetNumber){
        return locateDataFile(getDataFileName(dataSetNumber));
    }

    /**
     * Return external judges answer file (location).
     * 
     * Searches both the dir found in {@link #getExternalDataFileLocation()} or
     * in that location plus the CCS standard location for that file.
     * 
     * @param dataSetNumber
     * @return
     */
    public File locateJudgesAnswerFile (int dataSetNumber){
        return locateDataFile(getDataFileName(dataSetNumber));
    }
    
    /**
     * Get external CCS standard location for data files.
     * 
     * @return
     */
    public String getCCSfileDirectory() {
        if (getExternalDataFileLocation() == null){
            return null;
        } else {
            return getExternalDataFileLocation() + File.separator + "data" + File.separator + "secret";
        }
    }
    
    private File locateDataFile(String filename) {
        String directoryName = getExternalDataFileLocation();
        if (directoryName == null) {
            return null;
        }
        File dir = new File(directoryName);
        if (!dir.isDirectory()) {
            return null;
        }

        String name = getCCSfileDirectory() + File.separator + filename;
        File file = new File(name);

        if (file.isFile()) {
            return file;
        }

        name = directoryName + File.separator + filename;
        file = new File(name);

        if (file.isFile()) {
            return file;
        }

        return null;
    }

    // TODO 917 - isSameAs methods
//    /**
//     * @return the testCaseDataFilenames
//     */
//    private String[] getTestCaseDataFilenames() {
//        return testCaseDataFilenames;
//    }
//
//    /**
//     * @return the testCaseAnswerFilenames
//     */
//    private String[] getTestCaseAnswerFilenames() {
//        return testCaseAnswerFilenames;
//    }
    
    public State getState() {
        return state;
    }
    
    public void setState(State state) {
        this.state = state;
    }

    public void setElementId(Problem problem) {
        problem.elementId = elementId;
    }

    /**
     * Returns the current {@link PC2ValidatorSettings} object attached to this Problem.
     * 
     * @return the current PC2ValidatorSettings object
     */
    public PC2ValidatorSettings getPC2ValidatorSettings() {
        return this.pc2ValidatorSettings;
    }

    /**
     * Sets the {@link PC2ValidatorSettings} for this Problem to the specified settings object.
     * 
     * @param settings the PC2ValidatorSettings object to attach to this Problem
     */
    public void setPC2ValidatorSettings(PC2ValidatorSettings settings) {
        this.pc2ValidatorSettings = settings ;
    }

    /**
     * Returns a {@link ClicsValidatorSettings} object containing the options which this
     * Problem should apply when using the CLICS validator.
     * 
     * @return the clicsValidatorSettings for this problem
     */
    public ClicsValidatorSettings getClicsValidatorSettings() {
        return clicsValidatorSettings;
    }

    /**
     * Sets the {@link ClicsValidatorSettings} for this problem to the specified value.
     * 
     * @param settings the CLICS Validator Settings to set
     */
    public void setCLICSValidatorSettings(ClicsValidatorSettings settings) {
        this.clicsValidatorSettings = settings;
    }

    /**
     * Returns a {@link CustomValidatorSettings} object describing the custom validator
     * settings associated with this problem (if any).
     * @return the customValidatorSettings for this problem
     */
    public CustomValidatorSettings getCustomValidatorSettings() {
        return customValidatorSettings;
    }

    /**
     * Sets the {@link CustomValidatorSettings} for this problem to the specified value.
     * @param customValidatorSettings the customValidatorSettings to set
     */
    public void setCustomValidatorSettings(CustomValidatorSettings settings) {
        this.customValidatorSettings = settings;
    }
}
