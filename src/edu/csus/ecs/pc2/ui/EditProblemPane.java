package edu.csus.ecs.pc2.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.Constants;
import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.log.Log;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;

/**
 * Add/Edit Problem Pane.
 * 
 * @author pc2@ecs.csus.edu
 * @version $Id$
 */

// $HeadURL$
public class EditProblemPane extends JPanePlugin {

    public static final String DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND = "{:validator} {:infile} {:outfile} {:ansfile} {:resfile} ";

    /**
     * 
     */
    private static final long serialVersionUID = -1060536964672397704L;

    private JPanel messagePane = null;

    private JPanel buttonPane = null;

    private JButton addButton = null;

    private JButton updateButton = null;

    private JButton cancelButton = null;

    private JLabel messageLabel = null;

    /**
     * The input problem.
     */
    private Problem problem = null;

    private JTabbedPane mainTabbedPane = null;

    private JPanel generalPane = null;

    private JPanel judgingTypePane = null;

    private JTextField problemNameTextField = null;

    private JTextField timeOutSecondTextField = null;

    private JCheckBox problemRequiresDataCheckBox = null;

    private JPanel dataProblemPane = null;

    private JPanel readsFromPane = null;

    private JPanel inputDataFilePane = null;

    private JRadioButton stdinRadioButton = null;

    private JRadioButton fileRadioButton = null;

    private JPanel fileNamePane = null;

    private JButton selectFileButton = null;

    private JCheckBox judgesHaveAnswerFiles = null;

    private JPanel answerFilePane = null;

    private JPanel answerFilenamePane = null;

    private JButton answerBrowseButton = null;

    private JLabel inputDataFileLabel = null;

    private JLabel answerFileNameLabel = null;

    private JLabel problemNameLabel = null;

    private JLabel timeoutLabel = null;

    private Log log = null;

    private boolean populatingGUI = true;

    /**
     * last directory where searched for files.
     */
    private String lastDirectory;

    private ProblemDataFiles newProblemDataFiles;

    private ButtonGroup teamReadsFrombuttonGroup = null; // @jve:decl-index=0:visual-constraint="586,61"

    private ButtonGroup judgingTypeGroup = null; // @jve:decl-index=0:visual-constraint="586,61"

    private JPanel validatorPane = null;

    private JRadioButton useNOValidatatorRadioButton = null;

    private JRadioButton usePC2ValidatorRadioButton = null;

    private JRadioButton useExternalValidatorRadioButton = null;

    private JCheckBox showValidatorToJudges = null;

    private JPanel pc2ValidatorFrame = null;

    private JPanel externalValidatorFrame = null;

    private JLabel validatorOptionsLabel = null;

    private JComboBox pc2ValidatorOptionComboBox = null;

    private JCheckBox ignoreCaseCheckBox = null;

    private JLabel validatorProgramLabel = null;

    private JPanel externalValidatorPane = null;

    private JButton validatorProgramJButton = null;

    private JLabel jLabel = null;

    private JTextField validatorCommandLineTextBox = null;

    private JLabel externalValidatorLabel = null;

    private JCheckBox showCompareCheckBox = null;

    private JCheckBox doShowOutputWindowCheckBox = null;

    private ButtonGroup validatorChoiceButtonGroup = null; // @jve:decl-index=0:visual-constraint="595,128"

    private static final String NL = System.getProperty("line.separator");

    private JRadioButton computerJudging = null;

    private JRadioButton manualJudging = null;

    private JCheckBox manualReview = null;

    private JCheckBox prelimaryNotification = null;

    private JCheckBox deleteProblemCheckBox = null;

    private boolean listenersAdded = false;

    /**
     * This method initializes
     * 
     */
    public EditProblemPane() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        this.setSize(new java.awt.Dimension(539, 511));

        this.add(getMessagePane(), java.awt.BorderLayout.NORTH);
        this.add(getButtonPane(), java.awt.BorderLayout.SOUTH);
        this.add(getMainTabbedPane(), java.awt.BorderLayout.CENTER);
    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        super.setContestAndController(inContest, inController);

        // getContest().addProblemListener(new Proble)

        log = getController().getLog();

        addWindowListeners();

    }
    
    private void addWindowListeners() {
        
        if (listenersAdded){
            // No need to add the listeners twice or more.
            return;
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (getParentFrame() != null) {
                    getParentFrame().addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            handleCancelButton();
                        }
                        @Override
                        public void windowOpened(WindowEvent e) {
                            getProblemNameTextField().requestFocus();
                        }
                        public void windowActivated(WindowEvent e) {
                            getProblemNameTextField().requestFocus();
                        };
                    });
                    listenersAdded = true;
                }
            }
        });
    }

    public String getPluginTitle() {
        return "Edit Problem Pane";
    }

    /**
     * This method initializes messagePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getMessagePane() {
        if (messagePane == null) {
            messageLabel = new JLabel();
            messageLabel.setText("");
            messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            messagePane = new JPanel();
            messagePane.setLayout(new BorderLayout());
            messagePane.setPreferredSize(new java.awt.Dimension(25, 25));
            messagePane.add(messageLabel, java.awt.BorderLayout.CENTER);
        }
        return messagePane;
    }

    /**
     * This method initializes buttonPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPane() {
        if (buttonPane == null) {
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(15);
            buttonPane = new JPanel();
            buttonPane.setLayout(flowLayout);
            buttonPane.add(getAddButton(), null);
            buttonPane.add(getUpdateButton(), null);
            buttonPane.add(getCancelButton(), null);
        }
        return buttonPane;
    }

    /**
     * This method initializes addButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAddButton() {
        if (addButton == null) {
            addButton = new JButton();
            addButton.setText("Add");
            addButton.setMnemonic(java.awt.event.KeyEvent.VK_A);
            addButton.setEnabled(false);
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addProblem();
                }
            });
        }
        return addButton;
    }

    /**
     * Add Problem to the fields.
     * 
     */
    protected void addProblem() {

        if (problemNameTextField.getText().trim().length() < 1) {
            showMessage("Enter a problem name");
            return;
        }

        if (!validateProblemFields()) {
            // new problem is invalid, just return, message issued by validateProblemFields
            return;
        }

        Problem newProblem = null;
        try {
            newProblem = getProblemFromFields(null);
            SerializedFile sFile;
            sFile = newProblemDataFiles.getJudgesDataFile();
            if (sFile != null) {
                checkFileFormat(sFile);
                if (checkFileFormat(sFile)) {
                    newProblemDataFiles.setJudgesDataFile(sFile);
                }
            }
            sFile = newProblemDataFiles.getJudgesAnswerFile();
            if (sFile != null) {
                if (checkFileFormat(sFile)) {
                    newProblemDataFiles.setJudgesAnswerFile(sFile);
                }
            }
            sFile = newProblemDataFiles.getValidatorFile();
            if (sFile != null) {
                if (checkFileFormat(sFile)) {
                    newProblemDataFiles.setValidatorFile(sFile);
                }
            }
        } catch (InvalidFieldValue e) {
            showMessage(e.getMessage());
            return;
        }

        getController().addNewProblem(newProblem, newProblemDataFiles);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    private int getIntegerValue(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Enable or disable Update button based on comparison of run to fields.
     * 
     */
    public void enableUpdateButton() {

        if (populatingGUI) {
            return;
        }

        boolean enableButton = false;
        String updateToolTip = "";

        if (problem != null) {

            try {
                Problem changedProblem = getProblemFromFields(null);
                if (!problem.isSameAs(changedProblem)) {
                    enableButton = true;
                    updateToolTip = "Problem changed";
                }
                ProblemDataFiles pdf = getContest().getProblemDataFile(problem);
                if (pdf != null) {
                    int fileChanged = 0;
                    String fileName=changedProblem.getDataFileName();
                    if (fileName != null && fileName.length() > 0) {
                        if (!fileSameAs(pdf.getJudgesDataFile(), changedProblem.getDataFileName())) {
                            enableButton = true;
                            fileChanged++;
                            if (updateToolTip.equals("")) {
                                updateToolTip="Judges data";
                            } else {
                                updateToolTip=", Judges data";
                            }
                        }
                    }
                    fileName=changedProblem.getAnswerFileName();
                    if (fileName != null && fileName.length() > 0) {
                        if (!fileSameAs(pdf.getJudgesAnswerFile(), changedProblem.getAnswerFileName())) {
                            enableButton = true;
                            fileChanged++;
                            if (updateToolTip.equals("")) {
                                updateToolTip="Judges answer";
                            } else {
                                updateToolTip += ", Judges answer";
                            } 
                        }
                    }
                    fileName=changedProblem.getValidatorProgramName();
                    if (!problem.isUsingPC2Validator() && fileName != null && fileName.length() > 0) { 
                        if (!fileSameAs(pdf.getValidatorFile(), changedProblem.getValidatorProgramName())) {
                            enableButton = true;
                            fileChanged++;
                            if (updateToolTip.equals("")) {
                                updateToolTip="Validator";
                            } else {
                                updateToolTip += ", Validator";
                            }
                        }
                    }
                    if (fileChanged > 0) {
                        if(fileChanged == 1) {
                            updateToolTip += " file changed";
                        } else {
                            updateToolTip += " files changed";
                            
                        }
                    }
                } else {
                    getController().getLog().log(Log.DEBUG, "No ProblemDataFiles for "+problem);
                }

            } catch (InvalidFieldValue e) {
                // invalid field, but that is ok as they are entering data
                // will be caught and reported when they hit update or add.
                getController().getLog().log(Log.DEBUG, "Input Problem (but not saving) ", e);
                enableButton = true;
            } catch (Exception ex){
                getController().getLog().log(Log.DEBUG, "Edit Problem ", ex);
                showMessage("Error, check logs.  "+ex.getMessage());
            }

        } else {
            if (getAddButton().isVisible()) {
                enableButton = true;
            }
        }

        if (updateToolTip.equals("")) {
            // otherwise we get a sliver of a tooltip
            getUpdateButton().setToolTipText(null);
        } else {
            getUpdateButton().setToolTipText(updateToolTip);
        }
        enableUpdateButtons(enableButton);

    }

    /**
     * 
     * @param file
     * @param fileName
     * @return false if fileName exists and has changed checksums
     */
    private boolean fileSameAs(SerializedFile file, String fileName) {
        if (fileName != null && !fileName.trim().equals("")) {
            // files changed, treat that as the same
            if (file != null && !fileName.equals(file.getName())) {
                return true;
            }
            return !needsFreshening(file, fileName);
        }
        // default to true
        return true;
    }

    /**
     * Create a Problem from the fields.
     * 
     * This also populates newProblemDataFiles for the data files.
     * 
     * @param checkProblem
     *            will update this Problem if supplied, if null creates a new Problem
     * @return Problem based on fields
     * @throws InvalidFieldValue
     */
    public Problem getProblemFromFields(Problem checkProblem) throws InvalidFieldValue {
        boolean isAdding = true;

        if (checkProblem == null) {
            checkProblem = new Problem(problemNameTextField.getText());
            isAdding = true;
            // now that we have a title(hopefully)
            newProblemDataFiles = new ProblemDataFiles(checkProblem);
        } else {
            // initialize the new ProblemDataFiles, before we start editting (uses elementId)
            // TODO should this be a deep clone?
            newProblemDataFiles = new ProblemDataFiles(checkProblem);
            checkProblem.setDisplayName(problemNameTextField.getText());
            isAdding = false;
        }

        int secs = getIntegerValue(timeOutSecondTextField.getText());
        checkProblem.setTimeOutInSeconds(secs);

        boolean deleted = getDeleteProblemCheckBox().isSelected();
        checkProblem.setActive(!deleted);

        if (problemRequiresDataCheckBox.isSelected()) {

            String fileName = inputDataFileLabel.getText();
            if (fileName == null || fileName.trim().length() == 0) {
                throw new InvalidFieldValue("Problem Requires Input Data checked, select a file ");
            }

            if (fileName.trim().length() != inputDataFileLabel.getToolTipText().length()) {
                fileName = inputDataFileLabel.getToolTipText() + "";
            }

            if (isAdding) {
                SerializedFile serializedFile = new SerializedFile(fileName);

                if (serializedFile.getBuffer() == null) {
                    throw new InvalidFieldValue("Unable to read file " + fileName + " choose data file again (adding)");
                }

                checkProblem.setDataFileName(serializedFile.getName());
                newProblemDataFiles.setJudgesDataFile(serializedFile);
            } else {

                SerializedFile serializedFile = getController().getProblemDataFiles(checkProblem).getJudgesDataFile();
                if (serializedFile == null || !serializedFile.getAbsolutePath().equals(fileName)) {
                    // they've added a new file
                    serializedFile = new SerializedFile(fileName);
                    checkFileFormat(serializedFile);
                } else {
                    serializedFile = freshenIfNeeded(serializedFile, fileName);
                }
                newProblemDataFiles.setJudgesDataFile(serializedFile);
                checkProblem.setDataFileName(serializedFile.getName());
            }
        } else {
            checkProblem.setDataFileName(null);
        }

        if (judgesHaveAnswerFiles.isSelected()) {
            String fileName = answerFileNameLabel.getText();
            if (fileName == null || fileName.trim().length() == 0) {
                throw new InvalidFieldValue("Judges Have Provided Answer File checked, select a file");
            }

            if (fileName.trim().length() != answerFileNameLabel.getToolTipText().length()) {
                fileName = answerFileNameLabel.getToolTipText() + "";
            }

            if (isAdding) {
                SerializedFile serializedFile = new SerializedFile(fileName);

                if (serializedFile.getBuffer() == null) {
                    throw new InvalidFieldValue("Unable to read file " + fileName + " choose answer file again (adding)");
                }

                checkProblem.setAnswerFileName(serializedFile.getName());
                newProblemDataFiles.setJudgesAnswerFile(serializedFile);
            } else {
                SerializedFile serializedFile = getController().getProblemDataFiles(checkProblem).getJudgesAnswerFile();
                if (serializedFile == null || !serializedFile.getAbsolutePath().equals(fileName)) {
                    // they've added a new file
                    serializedFile = new SerializedFile(fileName);
                    checkFileFormat(serializedFile);
                } else {
                    serializedFile = freshenIfNeeded(serializedFile, fileName);
                }

                newProblemDataFiles.setJudgesAnswerFile(serializedFile);
                checkProblem.setAnswerFileName(serializedFile.getName());
            }
        } else {
            checkProblem.setAnswerFileName(null);
        }

        if (stdinRadioButton.isSelected() && fileRadioButton.isSelected()) {
            throw new InvalidFieldValue("Pick just one radio button");
        }

        if (fileRadioButton.isSelected()) {

            checkProblem.setReadInputDataFromSTDIN(false);

        } else if (stdinRadioButton.isSelected()) {

            checkProblem.setReadInputDataFromSTDIN(true);
        }

        /**
         * The 3 radio buttons for which validator are fit into 2 boolean fields in checkProblem. If the checkProblem is validated and usePC2 validator then use pc2validator if the checkProblem is
         * validated and NOT usePC2 validator then use external validator if the checkProblem is not validated, well, the checkProblem is not validated.
         */

        checkProblem.setValidatedProblem(!useNOValidatatorRadioButton.isSelected());
        if (checkProblem.isValidatedProblem()) {
            checkProblem.setUsingPC2Validator(usePC2ValidatorRadioButton.isSelected());
        }

        checkProblem.setValidatorCommandLine(validatorCommandLineTextBox.getText());
        checkProblem.setWhichPC2Validator(0);
        checkProblem.setIgnoreSpacesOnValidation(false);

        checkProblem.setValidatorProgramName(null);
        if (checkProblem.isUsingPC2Validator()) {

            // java -cp ..\..\lib\pc2.jar edu.csus.ecs.pc2.validator.Validator sumit.dat estdout.pc2 sumit.ans 212XRSAM.txt -pc2 1 false
            // "{:validator} {:infle} {:outfile} {:ansfile} {:resfile} ";

            checkProblem.setWhichPC2Validator(getPc2ValidatorComboBox().getSelectedIndex());
            checkProblem.setIgnoreSpacesOnValidation(getIgnoreCaseCheckBox().isSelected());
            checkProblem.setValidatorCommandLine(DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND + " -pc2 " + checkProblem.getWhichPC2Validator() + " " + checkProblem.isIgnoreSpacesOnValidation());
            checkProblem.setValidatorProgramName(Problem.INTERNAL_VALIDATOR_NAME);
        }

        checkProblem.setShowValidationToJudges(showValidatorToJudges.isSelected());

        checkProblem.setHideOutputWindow(!getDoShowOutputWindowCheckBox().isSelected());
        checkProblem.setShowCompareWindow(getShowCompareCheckBox().isSelected());

        // selecting a file is optional
        String fileNameVal = externalValidatorLabel.getText();
        if (fileNameVal != null) {
            fileNameVal = fileNameVal.trim();
        } else {
            fileNameVal = "";
        }
        if (useExternalValidatorRadioButton.isSelected() && fileNameVal.length() > 0) {
            String fileName = fileNameVal;
            if (fileName.length() != externalValidatorLabel.getToolTipText().length()) {
                fileName = externalValidatorLabel.getToolTipText() + "";
            }

            if (isAdding) {
                SerializedFile serializedFile = new SerializedFile(fileName);

                if (serializedFile.getBuffer() == null) {
                    throw new InvalidFieldValue("Unable to read file " + fileName + " choose validator file again (adding)");
                }

                checkProblem.setValidatorProgramName(serializedFile.getName());
                // for some reason on validator this is borked
                // newProblemDataFiles.setValidatorFile(freshenIfNeeded(serializedFile, fileName));
                newProblemDataFiles.setValidatorFile(serializedFile);
            } else {
                SerializedFile serializedFile = getController().getProblemDataFiles(checkProblem).getValidatorFile();
                if (serializedFile == null || !serializedFile.getAbsolutePath().equals(fileName)) {
                    // they've added a new file
                    serializedFile = new SerializedFile(fileName);
                    checkFileFormat(serializedFile);
                } else {
                    serializedFile = freshenIfNeeded(serializedFile, fileName);
                }

                newProblemDataFiles.setValidatorFile(serializedFile);
                checkProblem.setValidatorProgramName(serializedFile.getName());
            }
        }

        checkProblem.setComputerJudged(computerJudging.isSelected());

        if (computerJudging.isSelected()) {
            checkProblem.setManualReview(manualReview.isSelected());
            if (manualReview.isSelected()) {
                checkProblem.setPrelimaryNotification(prelimaryNotification.isSelected());
            } else {
                checkProblem.setPrelimaryNotification(false);
            }
        } else {
            checkProblem.setManualReview(false);
            checkProblem.setPrelimaryNotification(false);
        }

        return checkProblem;

    }

    /**
     * This method initializes updateButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getUpdateButton() {
        if (updateButton == null) {
            updateButton = new JButton();
            updateButton.setText("Update");
            updateButton.setEnabled(false);
            updateButton.setMnemonic(java.awt.event.KeyEvent.VK_U);
            updateButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    updateProblem();
                }
            });
        }
        return updateButton;
    }

    protected void updateProblem() {

        if (!validateProblemFields()) {
            // new problem is invalid, just return, message issued by validateProblemFields
            return;
        }

        Problem newProblem = null;

        try {
            newProblem = getProblemFromFields(problem);
            // also updates newProblemDataFiles
        } catch (InvalidFieldValue e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            // showMessage(e.getMessage());
            return;
        }

        getController().updateProblem(newProblem, newProblemDataFiles);

        cancelButton.setText("Close");
        addButton.setEnabled(false);
        updateButton.setEnabled(false);

        if (getParentFrame() != null) {
            getParentFrame().setVisible(false);
        }
    }

    /**
     * Validate that all problem fields are ok.
     * 
     * @return
     */
    private boolean validateProblemFields() {

        if (problemNameTextField.getText().trim().length() < 1) {
            showMessage("Enter a problem name");
            return false;
        }

        if (getUsePC2ValidatorRadioButton().isSelected()) {
            if (pc2ValidatorOptionComboBox.getSelectedIndex() < 1) {
                showMessage("Select a Validator option");
                return false;
            }
        }

        if (getProblemRequiresDataCheckBox().isSelected()) {

            String fileName = inputDataFileLabel.getText();
            // this check is outside so we can provide a specific message
            if (fileName == null || fileName.trim().length() == 0) {
                showMessage("Problem Requires Input Data checked, select a file ");
                return false;
            }

            if (fileName.trim().length() != inputDataFileLabel.getToolTipText().length()) {
                fileName = inputDataFileLabel.getToolTipText() + "";
            }

            if (!checkFile(fileName)) {
                // note: if error, then checkFile will showMessage
                return false;
            }
        }

        if (getJudgesHaveAnswerFiles().isSelected()) {

            String answerFileName = answerFileNameLabel.getText();

            // this check is outside so we can provide a specific message
            if (answerFileName == null || answerFileName.trim().length() == 0) {
                showMessage("Problem Requires Judges' Answer File checked, select a file ");
                return false;
            }

            if (answerFileName.trim().length() != answerFileNameLabel.getToolTipText().length()) {
                answerFileName = answerFileNameLabel.getToolTipText() + "";
            }

            if (!checkFile(answerFileName)) {
                // note: if error, then checkFile will showMessage
                return false;
            }
        }
        
        if (getComputerJudging().isSelected()){
            
            if (useNOValidatatorRadioButton.isSelected()){
                showMessage("Computer Judging selected, must select a validator");
                return false;
            }
            
        }

        return true;
    }

    /**
     * Checks to ensure the fileName exists, is a file, and is readable.
     * <P>
     * If error found will show Message to user.
     * 
     * @param fileName
     *            the file to check
     * @return true if the is readable
     */
    private boolean checkFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            if (file.isFile()) {
                if (!file.canRead()) {
                    showMessage("Could not read file " + fileName);
                    return false;
                } // else exists, is a file, and is readable
            } else {
                // not a file
                showMessage(fileName + " is not a file");
                return false;
            }
        } else {
            showMessage(fileName + " does not exist");
            return false;
        }
        return true;
    }

    /**
     * This method initializes cancelButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton();
            cancelButton.setText("Cancel");
            cancelButton.setMnemonic(java.awt.event.KeyEvent.VK_C);
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    handleCancelButton();
                }
            });
        }
        return cancelButton;
    }

    protected void handleCancelButton() {

        if (getAddButton().isEnabled() || getUpdateButton().isEnabled()) {

            // Something changed, are they sure ?

            int result = FrameUtilities.yesNoCancelDialog(getParentFrame(), "Problem modified, save changes?", "Confirm Choice");

            if (result == JOptionPane.YES_OPTION) {
                if (getAddButton().isEnabled()) {
                    addProblem();
                } else {
                    updateProblem();
                }
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            } else if (result == JOptionPane.NO_OPTION) {
                if (getParentFrame() != null) {
                    getParentFrame().setVisible(false);
                }
            }

        } else {
            if (getParentFrame() != null) {
                getParentFrame().setVisible(false);
            }
        }
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(final Problem inProblem, final ProblemDataFiles problemDataFiles) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populatingGUI = true;
                setForm(inProblem,problemDataFiles);
                getAddButton().setVisible(true);
                getUpdateButton().setVisible(false);
                enableUpdateButtons(true);

                enableValidatorComponents();
                enableRequiresInputDataComponents(problemRequiresDataCheckBox.isSelected());
                enableProvideAnswerFileComponents(judgesHaveAnswerFiles.isSelected());

                // select the general tab
                getMainTabbedPane().setSelectedIndex(0);
                populatingGUI = false;
            }
        });
    }
    public void setProblem(final Problem problem) {

        this.problem = problem;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                populateGUI(problem);
                // do not automatically set this to no update, the files may have changed on disk
                if (problem == null) {
                    // new problem
                    enableUpdateButtons(false);
                } else {
                    // enable intelligently
                    enableUpdateButton();
                }
            }
        });
    }

    private void populateGUI(Problem inProblem) {

        populatingGUI = true;

        if (inProblem != null) {

            getAddButton().setVisible(false);
            getUpdateButton().setVisible(true);

            setForm(inProblem, getController().getProblemDataFiles(inProblem));
            
            try {
                @SuppressWarnings("unused")
                Problem changedProblem = getProblemFromFields(inProblem);
            } catch (InvalidFieldValue e) {
                e.printStackTrace();
            }

        } else {
            clearForm();
        }

        enableValidatorComponents();

        enableRequiresInputDataComponents(problemRequiresDataCheckBox.isSelected());

        enableProvideAnswerFileComponents(judgesHaveAnswerFiles.isSelected());

        // select the general tab
        getMainTabbedPane().setSelectedIndex(0);
        populatingGUI = false;
    }

    /**
     * This populates the form, no error checking is performed.
     * populatingGUI should be set to true before calling this.
     * @param inProblem
     * @param problemDataFiles
     */
    private void setForm(Problem inProblem, ProblemDataFiles problemDataFiles) {
        problemNameTextField.setText(inProblem.getDisplayName());
        timeOutSecondTextField.setText(inProblem.getTimeOutInSeconds() + "");
        inputDataFileLabel.setText(inProblem.getDataFileName());
        
        inputDataFileLabel.setToolTipText("");
        
        if (problemDataFiles != null){
            SerializedFile sFile = problemDataFiles.getJudgesDataFile();
            if (sFile != null) {
                inputDataFileLabel.setToolTipText(sFile.getAbsolutePath());
            }
        }

        answerFileNameLabel.setText(inProblem.getAnswerFileName());
        answerFileNameLabel.setToolTipText("");
        
        if (problemDataFiles != null){
            SerializedFile sFile = problemDataFiles.getJudgesAnswerFile();
            if (sFile != null) {
                answerFileNameLabel.setToolTipText(sFile.getAbsolutePath());
            } 
        }

        judgesHaveAnswerFiles.setSelected(inProblem.getAnswerFileName() != null);
        problemRequiresDataCheckBox.setSelected(inProblem.getDataFileName() != null);

        if (inProblem.isReadInputDataFromSTDIN()) {
            fileRadioButton.setSelected(false);
            stdinRadioButton.setSelected(true);
        } else {
            fileRadioButton.setSelected(true);
            stdinRadioButton.setSelected(false);
        }

        getPc2ValidatorComboBox().setSelectedIndex(0);
        getIgnoreCaseCheckBox().setSelected(true);
        externalValidatorLabel.setText("");
        externalValidatorLabel.setToolTipText("");
        ignoreCaseCheckBox.setSelected(false);

        if (inProblem.isValidatedProblem()) {

            if (inProblem.isUsingPC2Validator()) {
                getValidatorCommandLineTextBox().setText(DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND);
                usePC2ValidatorRadioButton.setSelected(true);
                pc2ValidatorOptionComboBox.setSelectedIndex(inProblem.getWhichPC2Validator());
                ignoreCaseCheckBox.setSelected(inProblem.isIgnoreSpacesOnValidation());
            } else {
                getValidatorCommandLineTextBox().setText(inProblem.getValidatorCommandLine());
                useExternalValidatorRadioButton.setSelected(true);
                externalValidatorLabel.setText(inProblem.getValidatorProgramName());
                externalValidatorLabel.setToolTipText(inProblem.getValidatorProgramName());
                SerializedFile sFile = problemDataFiles.getValidatorFile();
                if (sFile != null) {
                    externalValidatorLabel.setToolTipText(sFile.getAbsolutePath());
                } else {
                    externalValidatorLabel.setToolTipText("");
                }
            }

        } else {
            getValidatorCommandLineTextBox().setText(DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND);
            useNOValidatatorRadioButton.setSelected(true);
        }

        getShowValidatorToJudges().setSelected(inProblem.isShowValidationToJudges());
        getDoShowOutputWindowCheckBox().setSelected(!inProblem.isHideOutputWindow());
        getShowCompareCheckBox().setSelected(inProblem.isShowCompareWindow());
        getShowCompareCheckBox().setEnabled(getDoShowOutputWindowCheckBox().isSelected());
        
        getDeleteProblemCheckBox().setSelected(! inProblem.isActive());

        populateJudging(inProblem);
    }

    /*
     * Sets the Judging Type radio and checkboxes in a sane manner.
     */
    private void populateJudging(Problem inProblem) {
        if (inProblem != null && inProblem.isComputerJudged()) {
            computerJudging.setSelected(true);
            manualReview.setSelected(inProblem.isManualReview());
            manualReview.setEnabled(true);

            prelimaryNotification.setSelected(inProblem.isPrelimaryNotification());
            if (manualReview.isSelected()) {
                prelimaryNotification.setEnabled(true);
            } else {
                prelimaryNotification.setEnabled(false);
            }
        } else {
            computerJudging.setSelected(false);
            manualJudging.setSelected(true);
            if (inProblem == null) {
                manualReview.setSelected(false);
                prelimaryNotification.setSelected(false);
            } else {
                manualReview.setSelected(inProblem.isManualReview());
                prelimaryNotification.setSelected(inProblem.isPrelimaryNotification());
            }

            manualReview.setEnabled(false);
            prelimaryNotification.setEnabled(false);
        }
    }

    protected void enableUpdateButtons(boolean fieldsChanged) {
        if (fieldsChanged) {
            cancelButton.setText("Cancel");
        } else {
            cancelButton.setText("Close");
        }
        // only enable the visible one, we are either editing or adding not both
        if (getUpdateButton().isVisible()) {
            getUpdateButton().setEnabled(fieldsChanged);
        } else {
            getAddButton().setEnabled(fieldsChanged);
        }
    }

    /**
     * This method initializes mainTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getMainTabbedPane() {
        if (mainTabbedPane == null) {
            mainTabbedPane = new JTabbedPane();
            mainTabbedPane.setPreferredSize(new java.awt.Dimension(400, 400));
            mainTabbedPane.addTab("General", null, getGeneralPane(), null);
            mainTabbedPane.addTab("Judging Type", null, getJudgingTypePanel(), null);
            mainTabbedPane.addTab("Validator", null, getValidatorPane(), null);
        }
        return mainTabbedPane;
    }

    /**
     * This method initializes generalPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJudgingTypePanel() {
        if (judgingTypePane == null) {
            judgingTypePane = new JPanel();
            judgingTypePane.setLayout(null);
            judgingTypePane.add(getComputerJudging(), null);
            judgingTypePane.add(getManualJudging(), null);
            judgingTypePane.add(getManualReview(), null);
            judgingTypePane.add(getPrelimaryNotification(), null);
            getJudgingTypeGroup().setSelected(getManualJudging().getModel(), true);
        }
        return judgingTypePane;
    }

    /**
     * This method initializes generalPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getGeneralPane() {
        if (generalPane == null) {
            timeoutLabel = new JLabel();
            timeoutLabel.setBounds(new java.awt.Rectangle(23, 46, 175, 16));
            timeoutLabel.setText("Run Timeout Limit (Secs)");
            problemNameLabel = new JLabel();
            problemNameLabel.setBounds(new java.awt.Rectangle(23, 14, 179, 16));
            problemNameLabel.setText("Problem name");
            generalPane = new JPanel();
            generalPane.setLayout(null);
            generalPane.add(getProblemNameTextField(), null);
            generalPane.add(getJTextField(), null);
            generalPane.add(getProblemRequiresDataCheckBox(), null);
            generalPane.add(getDataProblemPane(), null);
            generalPane.add(getJudgesHaveAnswerFiles(), null);
            generalPane.add(getAnswerFilePane(), null);
            generalPane.add(problemNameLabel, null);
            generalPane.add(timeoutLabel, null);
            generalPane.add(getShowCompareCheckBox(), null);
            generalPane.add(getDoShowOutputWindowCheckBox(), null);
            generalPane.add(getDeleteProblemCheckBox(), null);
        }
        return generalPane;
    }

    /**
     * This method initializes problemNameTextField
     * 
     * @return javax.swing.JTextField
     */
    protected JTextField getProblemNameTextField() {
        if (problemNameTextField == null) {
            problemNameTextField = new JTextField();
            problemNameTextField.setPreferredSize(new java.awt.Dimension(120, 20));
            problemNameTextField.setSize(new java.awt.Dimension(273, 20));
            problemNameTextField.setLocation(new java.awt.Point(220, 12));
            problemNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return problemNameTextField;
    }

    /**
     * This method initializes jTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getJTextField() {
        if (timeOutSecondTextField == null) {
            timeOutSecondTextField = new JTextField();
            timeOutSecondTextField.setBounds(new java.awt.Rectangle(220, 44, 120, 20));
            timeOutSecondTextField.setPreferredSize(new java.awt.Dimension(120, 20));
            timeOutSecondTextField.setDocument(new IntegerDocument());
            timeOutSecondTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return timeOutSecondTextField;
    }

    /**
     * This method initializes problemRequiresDataTextField
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getProblemRequiresDataCheckBox() {
        if (problemRequiresDataCheckBox == null) {
            problemRequiresDataCheckBox = new JCheckBox();
            problemRequiresDataCheckBox.setBounds(new java.awt.Rectangle(23, 76, 257, 26));
            problemRequiresDataCheckBox.setText("Problem Requires Input Data");
            problemRequiresDataCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableRequiresInputDataComponents(problemRequiresDataCheckBox.isSelected());
                    enableUpdateButton();
                }
            });
        }
        return problemRequiresDataCheckBox;
    }

    protected void enableRequiresInputDataComponents(boolean enableButtons) {
        getInputDataFilePane().setEnabled(enableButtons);
        getFileRadioButton().setEnabled(enableButtons);
        getStdinRadioButton().setEnabled(enableButtons);
        getReadsFromPane().setEnabled(enableButtons);
        getSelectFileButton().setEnabled(enableButtons);
        getInputDataFilePane().setEnabled(enableButtons);
        getFileNamePane().setEnabled(enableButtons);
        getInputDataFilePane().setEnabled(enableButtons);
        getFileNamePane().setEnabled(enableButtons);
    }

    /**
     * This method initializes DataProblemPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getDataProblemPane() {
        if (dataProblemPane == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(2);
            gridLayout.setHgap(5);
            gridLayout.setVgap(6);
            dataProblemPane = new JPanel();
            dataProblemPane.setLayout(gridLayout);
            dataProblemPane.setBounds(new java.awt.Rectangle(53, 114, 423, 113));
            dataProblemPane.add(getReadsFromPane(), null);
            dataProblemPane.add(getInputDataFilePane(), null);
        }
        return dataProblemPane;
    }

    /**
     * This method initializes readsFromPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getReadsFromPane() {
        if (readsFromPane == null) {
            FlowLayout flowLayout1 = new FlowLayout();
            flowLayout1.setHgap(35);
            flowLayout1.setAlignment(java.awt.FlowLayout.LEFT);
            flowLayout1.setVgap(0);
            readsFromPane = new JPanel();
            readsFromPane.setLayout(flowLayout1);
            readsFromPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Team Reads From", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), new java.awt.Color(51, 51, 51)));
            readsFromPane.add(getFileRadioButton(), null);
            readsFromPane.add(getStdinRadioButton(), null);
            getTeamReadsFrombuttonGroup().setSelected(getFileRadioButton().getModel(), true);
            getValidatorChoiceButtonGroup().setSelected(getUseNOValidatatorRadioButton().getModel(), true);
        }
        return readsFromPane;
    }

    /**
     * This method initializes inputDataFilePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getInputDataFilePane() {
        if (inputDataFilePane == null) {
            BorderLayout borderLayout = new BorderLayout();
            borderLayout.setHgap(15);
            borderLayout.setVgap(5);
            inputDataFilePane = new JPanel();
            inputDataFilePane.setLayout(borderLayout);
            inputDataFilePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Input Data File", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            inputDataFilePane.setPreferredSize(new java.awt.Dimension(98, 45));
            inputDataFilePane.add(getFileNamePane(), java.awt.BorderLayout.CENTER);
            inputDataFilePane.add(getSelectFileButton(), java.awt.BorderLayout.EAST);
        }
        return inputDataFilePane;
    }

    /**
     * This method initializes stdinRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getStdinRadioButton() {
        if (stdinRadioButton == null) {
            stdinRadioButton = new JRadioButton();
            stdinRadioButton.setText("Stdin");
            stdinRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return stdinRadioButton;
    }

    /**
     * This method initializes fileRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getFileRadioButton() {
        if (fileRadioButton == null) {
            fileRadioButton = new JRadioButton();
            fileRadioButton.setText("File");
            fileRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return fileRadioButton;
    }

    /**
     * This method initializes fileNamePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getFileNamePane() {
        if (fileNamePane == null) {
            inputDataFileLabel = new JLabel();
            inputDataFileLabel.setText("");
            fileNamePane = new JPanel();
            fileNamePane.setLayout(new BorderLayout());
            fileNamePane.add(inputDataFileLabel, java.awt.BorderLayout.CENTER);
        }
        return fileNamePane;
    }

    /**
     * This method initializes selectFileButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSelectFileButton() {
        if (selectFileButton == null) {
            selectFileButton = new JButton();
            selectFileButton.setText("Browse");
            selectFileButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectFile(inputDataFileLabel)) {
                        inputDataFileLabel.setToolTipText(inputDataFileLabel.getText());
                    }
                    enableUpdateButton();
                }
            });
        }
        return selectFileButton;
    }

    /**
     * This method initializes jCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getJudgesHaveAnswerFiles() {
        if (judgesHaveAnswerFiles == null) {
            judgesHaveAnswerFiles = new JCheckBox();
            judgesHaveAnswerFiles.setBounds(new java.awt.Rectangle(23, 239, 302, 24));
            judgesHaveAnswerFiles.setText("Judges Have Provided Answer File");
            judgesHaveAnswerFiles.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableProvideAnswerFileComponents(judgesHaveAnswerFiles.isSelected());
                    enableUpdateButton();
                }
            });
        }
        return judgesHaveAnswerFiles;
    }

    protected void enableProvideAnswerFileComponents(boolean enableComponents) {
        getAnswerFilenamePane().setEnabled(enableComponents);
        getAnswerBrowseButton().setEnabled(enableComponents);
        answerFileNameLabel.setEnabled(enableComponents);
    }

    /**
     * This method initializes answerFilePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAnswerFilePane() {
        if (answerFilePane == null) {
            BorderLayout borderLayout1 = new BorderLayout();
            borderLayout1.setHgap(15);
            borderLayout1.setVgap(5);
            answerFilePane = new JPanel();
            answerFilePane.setLayout(borderLayout1);
            answerFilePane.setBounds(new java.awt.Rectangle(53, 275, 423, 52));
            answerFilePane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Answer File", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            answerFilePane.add(getAnswerFilenamePane(), java.awt.BorderLayout.CENTER);
            answerFilePane.add(getAnswerBrowseButton(), java.awt.BorderLayout.EAST);
        }
        return answerFilePane;
    }

    /**
     * This method initializes answerFilenamePane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getAnswerFilenamePane() {
        if (answerFilenamePane == null) {
            answerFileNameLabel = new JLabel();
            answerFileNameLabel.setText("");
            answerFilenamePane = new JPanel();
            answerFilenamePane.setLayout(new BorderLayout());
            answerFilenamePane.add(answerFileNameLabel, java.awt.BorderLayout.CENTER);
        }
        return answerFilenamePane;
    }

    /**
     * This method initializes answerBrowseButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getAnswerBrowseButton() {
        if (answerBrowseButton == null) {
            answerBrowseButton = new JButton();
            answerBrowseButton.setText("Browse");
            answerBrowseButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectFile(answerFileNameLabel)) {
                        answerFileNameLabel.setToolTipText(answerFileNameLabel.getText());
                    }
                    enableUpdateButton();
                }
            });
        }
        return answerBrowseButton;
    }

    public void showMessage(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(null, message);
            }
        });
    }

    /**
     * select file, if file picked updates label.
     * 
     * @param label
     * @return True is a file was select and label updated
     * @throws Exception
     */
    private boolean selectFile(JLabel label) {
        boolean result = false;
        // toolTip should always have the full path
        String oldFile = label.getToolTipText();
        String startDir;
        if (oldFile.equalsIgnoreCase("")) {
            startDir = lastDirectory;
        } else {
            startDir = oldFile;
        }
        JFileChooser chooser = new JFileChooser(startDir);
        try {
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                lastDirectory = chooser.getCurrentDirectory().toString();
                label.setText(chooser.getSelectedFile().getCanonicalFile().toString());
                result = true;
            }
        } catch (Exception e) {
            log.log(Log.INFO, "Error getting selected file, try again.", e);
            result = false;
        }
        chooser = null;
        return result;
    }

    /**
     * This method initializes teamReadsFrombuttonGroup
     * 
     * @return javax.swing.ButtonGroup
     */
    private ButtonGroup getTeamReadsFrombuttonGroup() {
        if (teamReadsFrombuttonGroup == null) {
            teamReadsFrombuttonGroup = new ButtonGroup();
            teamReadsFrombuttonGroup.add(getStdinRadioButton());
            teamReadsFrombuttonGroup.add(getFileRadioButton());
        }
        return teamReadsFrombuttonGroup;
    }

    private ButtonGroup getJudgingTypeGroup() {
        if (judgingTypeGroup == null) {
            judgingTypeGroup = new ButtonGroup();
            judgingTypeGroup.add(getComputerJudging());
            judgingTypeGroup.add(getManualJudging());
        }
        return judgingTypeGroup;
    }

    /**
     * This method initializes validatorPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getValidatorPane() {
        if (validatorPane == null) {
            validatorPane = new JPanel();
            validatorPane.setLayout(null);
            validatorPane.add(getUseNOValidatatorRadioButton(), null);
            validatorPane.add(getUsePC2ValidatorRadioButton(), null);
            validatorPane.add(getUseExternalValidatorRadioButton(), null);
            validatorPane.add(getShowValidatorToJudges(), null);
            validatorPane.add(getPc2ValidatorFrame(), null);
            validatorPane.add(getExternalValidatorFrame(), null);
        }
        return validatorPane;
    }

    /**
     * This method initializes useValidatorRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getUseNOValidatatorRadioButton() {
        if (useNOValidatatorRadioButton == null) {
            useNOValidatatorRadioButton = new JRadioButton();
            useNOValidatatorRadioButton.setBounds(new java.awt.Rectangle(20, 15, 246, 23));
            useNOValidatatorRadioButton.setText("Do not use Validator");
            useNOValidatatorRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableValidatorComponents();
                    enableUpdateButton();
                }
            });
        }
        return useNOValidatatorRadioButton;
    }

    protected void enableValidatorComponents() {
        if (usePC2ValidatorRadioButton.isSelected()) {
            enablePC2ValidatorComponents(true);
            enableExternalValidatorComponents(false);
            getShowValidatorToJudges().setEnabled(true);
        } else if (useExternalValidatorRadioButton.isSelected()) {
            enablePC2ValidatorComponents(false);
            enableExternalValidatorComponents(true);
            getShowValidatorToJudges().setEnabled(true);
        } else {
            // None used
            enablePC2ValidatorComponents(false);
            enableExternalValidatorComponents(false);
            getShowValidatorToJudges().setEnabled(false);
        }
    }

    /**
     * This method initializes jRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getUsePC2ValidatorRadioButton() {
        if (usePC2ValidatorRadioButton == null) {
            usePC2ValidatorRadioButton = new JRadioButton();
            usePC2ValidatorRadioButton.setBounds(new java.awt.Rectangle(21, 49, 246, 23));
            usePC2ValidatorRadioButton.setText("Use PC^2 Validator");
            usePC2ValidatorRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableValidatorComponents();
                    enableUpdateButton();
                }
            });
        }
        return usePC2ValidatorRadioButton;
    }

    protected void enableExternalValidatorComponents(boolean enableComponents) {

        getExternalValidatorFrame().setEnabled(enableComponents);
        getValidatorProgramJButton().setEnabled(enableComponents);
        getValidatorCommandLineTextBox().setEnabled(enableComponents);
    }

    protected void enablePC2ValidatorComponents(boolean enableComponents) {
        ignoreCaseCheckBox.setEnabled(enableComponents);
        pc2ValidatorOptionComboBox.setEnabled(enableComponents);
    }

    /**
     * This method initializes jRadioButton
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getUseExternalValidatorRadioButton() {
        if (useExternalValidatorRadioButton == null) {
            useExternalValidatorRadioButton = new JRadioButton();
            useExternalValidatorRadioButton.setBounds(new java.awt.Rectangle(17, 196, 246, 23));
            useExternalValidatorRadioButton.setText("Use External Validator");
            useExternalValidatorRadioButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableValidatorComponents();
                    enableUpdateButton();
                }
            });
        }
        return useExternalValidatorRadioButton;
    }

    /**
     * This method initializes showValidatorToJudges
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowValidatorToJudges() {
        if (showValidatorToJudges == null) {
            showValidatorToJudges = new JCheckBox();
            showValidatorToJudges.setBounds(new java.awt.Rectangle(38, 368, 306, 24));
            showValidatorToJudges.setText("Show Validator To Judges (SVTJ)");
            showValidatorToJudges.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return showValidatorToJudges;
    }

    /**
     * This method initializes pc2ValidatorFrame
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getPc2ValidatorFrame() {
        if (pc2ValidatorFrame == null) {
            validatorOptionsLabel = new JLabel();
            validatorOptionsLabel.setText("Validator Option");
            validatorOptionsLabel.setBounds(new java.awt.Rectangle(22, 26, 123, 23));
            pc2ValidatorFrame = new JPanel();
            pc2ValidatorFrame.setLayout(null);
            pc2ValidatorFrame.setBounds(new java.awt.Rectangle(40, 80, 471, 108));
            pc2ValidatorFrame.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "PC^2 Validator", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            pc2ValidatorFrame.add(validatorOptionsLabel, null);
            pc2ValidatorFrame.add(getPc2ValidatorComboBox(), null);
            pc2ValidatorFrame.add(getIgnoreCaseCheckBox(), null);
        }
        return pc2ValidatorFrame;
    }

    /**
     * This method initializes externalValidatorFrame
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getExternalValidatorFrame() {
        if (externalValidatorFrame == null) {
            jLabel = new JLabel();
            jLabel.setBounds(new java.awt.Rectangle(14, 53, 177, 16));
            jLabel.setText("Validator Command Line");
            validatorProgramLabel = new JLabel();
            validatorProgramLabel.setText("Validator Program");
            validatorProgramLabel.setBounds(new java.awt.Rectangle(13, 26, 121, 16));
            externalValidatorFrame = new JPanel();
            externalValidatorFrame.setLayout(null);
            externalValidatorFrame.setBounds(new java.awt.Rectangle(39, 231, 470, 127));
            externalValidatorFrame.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "External Validator", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
            externalValidatorFrame.add(validatorProgramLabel, null);
            externalValidatorFrame.add(getExternalValidatorPane(), null);
            externalValidatorFrame.add(getValidatorProgramJButton(), null);
            externalValidatorFrame.add(jLabel, null);
            externalValidatorFrame.add(getValidatorCommandLineTextBox(), null);
        }
        return externalValidatorFrame;
    }

    /**
     * This method initializes pc2ValidatorJOption
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getPc2ValidatorComboBox() {
        if (pc2ValidatorOptionComboBox == null) {
            pc2ValidatorOptionComboBox = new JComboBox();
            pc2ValidatorOptionComboBox.setBounds(new java.awt.Rectangle(158, 24, 255, 26));

            pc2ValidatorOptionComboBox.addItemListener(new java.awt.event.ItemListener() {
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    enableUpdateButton();

                }
            });
            pc2ValidatorOptionComboBox.addItem("None Selected");
            pc2ValidatorOptionComboBox.addItem("1 - diff");
            pc2ValidatorOptionComboBox.addItem("2 - ignore whitespace at start of file");
            pc2ValidatorOptionComboBox.addItem("3 - ignore leading whitespace on lines");
            pc2ValidatorOptionComboBox.addItem("4 - ignore all whitespace on lines");
            pc2ValidatorOptionComboBox.addItem("5 - ignore empty lines");

        }
        return pc2ValidatorOptionComboBox;
    }

    /**
     * This method initializes jCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getIgnoreCaseCheckBox() {
        if (ignoreCaseCheckBox == null) {
            ignoreCaseCheckBox = new JCheckBox();
            ignoreCaseCheckBox.setBounds(new java.awt.Rectangle(27, 62, 263, 24));
            ignoreCaseCheckBox.setText("Ignore Case In Output");
            ignoreCaseCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return ignoreCaseCheckBox;
    }

    /**
     * This method initializes externalValidatorPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getExternalValidatorPane() {
        if (externalValidatorLabel == null) {
            externalValidatorLabel = new JLabel();
            externalValidatorLabel.setText("");
            externalValidatorLabel.setToolTipText("");
            externalValidatorPane = new JPanel();
            externalValidatorPane.setLayout(new BorderLayout());
            externalValidatorPane.setBounds(new java.awt.Rectangle(140, 21, 267, 22));
            externalValidatorPane.add(externalValidatorLabel, java.awt.BorderLayout.CENTER);
        }
        return externalValidatorPane;
    }

    /**
     * This method initializes validatorProgramJButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getValidatorProgramJButton() {
        if (validatorProgramJButton == null) {
            validatorProgramJButton = new JButton();
            validatorProgramJButton.setBounds(new java.awt.Rectangle(425, 21, 34, 25));
            validatorProgramJButton.setText("...");
            validatorProgramJButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (selectFile(externalValidatorLabel)) {
                        externalValidatorLabel.setToolTipText(externalValidatorLabel.getText());
                        enableUpdateButton();
                    }
                }
            });
        }
        return validatorProgramJButton;
    }

    /**
     * This method initializes validatorCommandLineTextBox
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getValidatorCommandLineTextBox() {
        if (validatorCommandLineTextBox == null) {
            validatorCommandLineTextBox = new JTextField();
            validatorCommandLineTextBox.setBounds(new java.awt.Rectangle(17, 78, 432, 29));
            validatorCommandLineTextBox.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return validatorCommandLineTextBox;
    }

    /**
     * This method initializes showComareCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getShowCompareCheckBox() {
        if (showCompareCheckBox == null) {
            showCompareCheckBox = new JCheckBox();
            showCompareCheckBox.setBounds(new java.awt.Rectangle(44,372,207,21));
            showCompareCheckBox.setText("Show Compare");
            showCompareCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return showCompareCheckBox;
    }

    /**
     * This method initializes jCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getDoShowOutputWindowCheckBox() {
        if (doShowOutputWindowCheckBox == null) {
            doShowOutputWindowCheckBox = new JCheckBox();
            doShowOutputWindowCheckBox.setBounds(new java.awt.Rectangle(23,338,303,24));
            doShowOutputWindowCheckBox.setSelected(true);
            doShowOutputWindowCheckBox.setText("Show the output window");
            doShowOutputWindowCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                    getShowCompareCheckBox().setEnabled(getDoShowOutputWindowCheckBox().isSelected());
                }
            });
        }
        return doShowOutputWindowCheckBox;
    }

    /**
     * This method initializes validatorChoiceButtonGroup
     * 
     * @return javax.swing.ButtonGroup
     */
    private ButtonGroup getValidatorChoiceButtonGroup() {
        if (validatorChoiceButtonGroup == null) {
            validatorChoiceButtonGroup = new ButtonGroup();
            validatorChoiceButtonGroup.add(getUseNOValidatatorRadioButton());
            validatorChoiceButtonGroup.add(getUsePC2ValidatorRadioButton());
            validatorChoiceButtonGroup.add(getUseExternalValidatorRadioButton());
        }
        return validatorChoiceButtonGroup;
    }

    /**
     * Checks whether needs to freshen, prompt user before freshening.
     * 
     * @param serializedFile
     * @param fileName
     * @return
     * @throws InvalidFieldValue
     */
    private SerializedFile freshenIfNeeded(SerializedFile serializedFile, String fileName) throws InvalidFieldValue {

        if (serializedFile == null) {
            return null;

        }
        if (serializedFile.getBuffer() == null) {
            throw new InvalidFieldValue("Unable to read file " + fileName + " choose file again (updating)");
        }

        // only check freshening if it is still the same fileName
        if (fileName != null && fileName.equals(serializedFile.getAbsolutePath())) {
            // only do this if we are not populating the gui
            if (!populatingGUI && needsFreshening(serializedFile, fileName)) {
    
                int result = JOptionPane.showConfirmDialog(this, "File (" + fileName + ") has changed; reload from disk?", "Freshen file " + serializedFile.getAbsolutePath() + "?", 
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    
                if (result == JOptionPane.YES_OPTION) {
                    serializedFile = new SerializedFile(serializedFile.getAbsolutePath());
                    checkFileFormat(serializedFile);
                    return serializedFile;
                } else if (result == JOptionPane.CANCEL_OPTION) {
                    throw new InvalidFieldValue("Update cancelled");
                }
            } // else nothing to update
        } else {
            if (fileName != null) {
                serializedFile = new SerializedFile(fileName);
            }
        }

        checkFileFormat(serializedFile);
        return serializedFile;
    }

    /**
     * Has this file been updated on disk ?
     * 
     * @param serializedFile
     *            existing saved file
     * @param fileName
     *            name for file that might need freshening.
     * @return true if file on disk different than saved file.
     */
    public boolean needsFreshening(SerializedFile serializedFile, String fileName) {

        if (serializedFile == null) {
            return false;
        }

        try {
            File f = new File(serializedFile.getAbsolutePath());

            if (f.exists()) {
                // Only can check whether to update if file is on disk

                // Now compare them
                // Can't use SerializeFile.getFile() because it may return null... sigh.

                SerializedFile newSerializedFile = new SerializedFile(f.getAbsolutePath());

                return !serializedFile.getSHA1sum().equals(newSerializedFile.getSHA1sum());
            } // else no need to refresh, no file found.

        } catch (Exception ex99) {
            getController().getLog().log(Log.DEBUG, "Exception ", ex99);
        }

        return false;
    }

    /**
     * @param newFile
     * @return true if the file was converted
     */
    public boolean checkFileFormat(SerializedFile newFile) throws InvalidFieldValue {

        /*
         * DOS FILE 0x0D 0x0A UNIX FILE 0xA MAC FILE 0xD
         */
        int currentOS = 0;
        // compare OS Versions.

        if (NL.length() == 2) {
            currentOS = Constants.FILETYPE_DOS;
        } else if (NL.charAt(0) == 0x0A) {
            currentOS = Constants.FILETYPE_UNIX;
        } else if (NL.charAt(0) == 0x0D) {
            currentOS = Constants.FILETYPE_MAC;
        }

        if ((currentOS != newFile.getFileType()) && (newFile.getFileType() != Constants.FILETYPE_BINARY) && (newFile.getFileType() != Constants.FILETYPE_ASCII_GENERIC)
                && (newFile.getFileType() != Constants.FILETYPE_ASCII_OTHER)) {

            String fileName = newFile.getName();
            String question = "The file (" + fileName + ") you are loading appears to be of type '";

            if (newFile.getFileType() == Constants.FILETYPE_BINARY) {
                question = question + Constants.FILETYPE_BINARY_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_DOS) {
                question = question + Constants.FILETYPE_DOS_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_MAC) {
                question = question + Constants.FILETYPE_MAC_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_UNIX) {
                question = question + Constants.FILETYPE_UNIX_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_ASCII_GENERIC) {
                question = question + Constants.FILETYPE_ASCII_GENERIC_TEXT;
            } else if (newFile.getFileType() == Constants.FILETYPE_ASCII_OTHER) {
                question = question + Constants.FILETYPE_ASCII_OTHER_TEXT;
            }

            question = question + "'." + NL + NL;

            question = question + "The Current OS is '" + System.getProperty("os.name", "?");

            question = question + "'." + NL + NL;

            question = question + "Do you want the file converted to the current OS file format as it is loaded into PC^2?";

            int answer = JOptionPane.showConfirmDialog(this, question, "File Format Mismatch", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            if (answer == JOptionPane.YES_OPTION) {
                newFile.convertFile(currentOS);
                return true;
            } else if (answer == JOptionPane.CANCEL_OPTION) {
                throw new InvalidFieldValue("Update canceled");
            }

        }
        return false;
    }

    /**
     * This resets the form, eg for a new problem.
     */
    void clearForm() {
        getAddButton().setVisible(true);
        getUpdateButton().setVisible(false);
        addButton.setEnabled(true);
        updateButton.setEnabled(false);

        problemNameTextField.setText("");
        timeOutSecondTextField.setText("120");
        judgesHaveAnswerFiles.setSelected(false);
        problemRequiresDataCheckBox.setSelected(false);

        inputDataFileLabel.setText("");
        inputDataFileLabel.setToolTipText("");
        answerFileNameLabel.setText("");
        answerFileNameLabel.setToolTipText("");

        fileRadioButton.setSelected(false);
        stdinRadioButton.setSelected(false);
        useNOValidatatorRadioButton.setSelected(true);
        pc2ValidatorOptionComboBox.setSelectedIndex(0);
        ignoreCaseCheckBox.setSelected(false);

        externalValidatorLabel.setText("");
        externalValidatorLabel.setToolTipText("");

        getValidatorCommandLineTextBox().setText(DEFAULT_INTERNATIONAL_VALIDATOR_COMMAND);
        getShowValidatorToJudges().setSelected(true);
        getDoShowOutputWindowCheckBox().setSelected(true);
        getShowCompareCheckBox().setSelected(true);
        getShowCompareCheckBox().setEnabled(getDoShowOutputWindowCheckBox().isSelected());
        
        getDeleteProblemCheckBox().setSelected(false);

        populateJudging(null);
    }

    /**
     * This method initializes computerJudging
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getComputerJudging() {
        if (computerJudging == null) {
            computerJudging = new JRadioButton();
            computerJudging.setBounds(new java.awt.Rectangle(100, 56, 173, 21));
            computerJudging.setText("Computer Judging");
            computerJudging.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    manualReview.setEnabled(true);
                    prelimaryNotification.setEnabled(manualReview.isSelected());
                    prelimaryNotification.setEnabled(manualReview.isSelected());
                    enableUpdateButton();
                }
            });

        }
        return computerJudging;
    }

    /**
     * This method initializes manualJudging
     * 
     * @return javax.swing.JRadioButton
     */
    private JRadioButton getManualJudging() {
        if (manualJudging == null) {
            manualJudging = new JRadioButton();
            manualJudging.setBounds(new java.awt.Rectangle(100,174,156,21));
            manualJudging.setText("Manual Judging");
            manualJudging.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    manualReview.setEnabled(false);
                    prelimaryNotification.setEnabled(false);
                    enableUpdateButton();
                }
            });
        }

        return manualJudging;
    }

    /**
     * This method initializes manualReview
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getManualReview() {
        if (manualReview == null) {
            manualReview = new JCheckBox();
            manualReview.setBounds(new java.awt.Rectangle(125, 89, 186, 21));
            manualReview.setText("Manual Review");
            manualReview.setEnabled(false);
            manualReview.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    prelimaryNotification.setEnabled(manualReview.isSelected());
                    prelimaryNotification.setEnabled(manualReview.isSelected());
                    enableUpdateButton();
                }
            });

        }
        return manualReview;
    }

    /**
     * This method initializes prelimaryNotification
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getPrelimaryNotification() {
        if (prelimaryNotification == null) {
            prelimaryNotification = new JCheckBox();
            prelimaryNotification.setBounds(new java.awt.Rectangle(168, 122, 328, 21));
            prelimaryNotification.setText("Send Preliminary Notification to the team");
            prelimaryNotification.setEnabled(false);
            prelimaryNotification.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });

        }
        return prelimaryNotification;
    }

    /**
     * This method initializes deleteProblemCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JCheckBox getDeleteProblemCheckBox() {
        if (deleteProblemCheckBox == null) {
            deleteProblemCheckBox = new JCheckBox();
            deleteProblemCheckBox.setBounds(new Rectangle(324, 372, 182, 21));
            deleteProblemCheckBox.setText("Hide Problem");
            deleteProblemCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    enableUpdateButton();
                }
            });
        }
        return deleteProblemCheckBox;
    }

} // @jve:decl-index=0:visual-constraint="10,10"