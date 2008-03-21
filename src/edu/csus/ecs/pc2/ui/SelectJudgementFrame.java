package edu.csus.ecs.pc2.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.csus.ecs.pc2.core.IInternalController;
import edu.csus.ecs.pc2.core.model.IInternalContest;
import edu.csus.ecs.pc2.core.model.IRunListener;
import edu.csus.ecs.pc2.core.model.Run;
import edu.csus.ecs.pc2.core.model.RunEvent;
import edu.csus.ecs.pc2.core.model.RunEvent.Action;
import edu.csus.ecs.pc2.core.security.Permission;
import edu.csus.ecs.pc2.ui.judge.JudgeView;

/**
 * Judge can chose judgement and execute run.
 * 
 * @author pc2@ecs.csus.edu
 */

// $HeadURL$
// $Id$

public class SelectJudgementFrame extends JFrame implements UIPlugin {

    /**
     * 
     */
    private static final long serialVersionUID = 6532349396307812235L;

    private IInternalContest contest;

    private IInternalController controller;

    private Run run = null;

    private SelectJudgementPaneNew selectJudgementPane = null;

    /**
     * This method initializes
     * 
     */
    public SelectJudgementFrame() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        this.setSize(new java.awt.Dimension(722,767));
        this.setContentPane(getSelectJudgementPane());
        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.setTitle("Select Run Judgement");

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                getSelectJudgementPane().handleCancelButton();
            }
        });
        FrameUtilities.centerFrame(this);

    }

    public void setContestAndController(IInternalContest inContest, IInternalController inController) {
        this.contest = inContest;
        this.controller = inController;

        if (!inContest.getAccount(inContest.getClientId()).getPermissionList().isAllowed(Permission.Type.JUDGE_RUN)) {
            throw new SecurityException("SelectJudgementFame requires JUDGE_RUN permission");
        }
        getSelectJudgementPane().setContestAndController(contest, controller);
        getSelectJudgementPane().setParentFrame(this);

        contest.addRunListener(new RunListenerImplementation());
    }

    public void setRun(Run theRun, boolean rejudgeRun) {
        getSelectJudgementPane().setRun(theRun);
        if (theRun == null) {
            setTitle("Run not loaded");
        } else {
            setTitle("Select Judgement for run " + theRun.getNumber() + " (Site " + theRun.getSiteNumber() + ")");
            run = theRun;
            getSelectJudgementPane().setRunFiles(null);
            if (rejudgeRun){
                controller.checkOutRejudgeRun(theRun);
            } else {
                controller.checkOutRun(theRun, false, false);
            }
        }
    }

    public String getPluginTitle() {
        return "Edit Run Frame";
    }

    /**
     * 
     * 
     * @author pc2@ecs.csus.edu
     */

    // $HeadURL$
    public class RunListenerImplementation implements IRunListener {

        public void runAdded(RunEvent event) {
//            System.out.println("sjf: : "+event.getAction()+" "+event.getSentToClientId()+" "+event.getRun());
            // ignore
        }

        public void runChanged(RunEvent event) {
            
//            System.out.println("sjf: : "+event.getAction()+" "+event.getSentToClientId()+" "+event.getWhoModifiedRun()+" "+event.getRun());
            
            if (run != null) {
                if (event.getRun().getElementId().equals(run.getElementId())) {
                    // RUN_NOT_AVIALABLE is undirected (sentToClient is null)
                    if (event.getAction().equals(Action.RUN_NOT_AVIALABLE)) {
                        getSelectJudgementPane().setRun(run);
                        getSelectJudgementPane().showMessage("Run " + run.getNumber() + " is not available ");
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                getSelectJudgementPane().enableUpdateButtons(false);
                            }
                        });
                        getSelectJudgementPane().regularCursor();
                        JudgeView.setAlreadyJudgingRun(false);
                    } else {
                        if (event.getSentToClientId() != null && event.getSentToClientId().equals(contest.getClientId())) {
                            
                            getSelectJudgementPane().setRunAndFiles(event.getRun(), event.getRunFiles(), event.getRunResultFiles());
                            // stop processing once we get it 
                            // stops both the duplicate checkedout_run and the run_not_available going to other judges
                            run = null;
                        }
                    }
                }
            }
        }

        public void runRemoved(RunEvent event) {
            // TODO Auto-generated method stub
        }
    }

    /**
     * This method initializes selectJudgementPane
     * 
     * @return edu.csus.ecs.pc2.ui.SelectJudgementPane
     */
    private SelectJudgementPaneNew getSelectJudgementPane() {
        if (selectJudgementPane == null) {
            selectJudgementPane = new SelectJudgementPaneNew();
            selectJudgementPane.setPreferredSize(new java.awt.Dimension(600,400));
        }
        return selectJudgementPane;
    }

}  //  @jve:decl-index=0:visual-constraint="10,4"
