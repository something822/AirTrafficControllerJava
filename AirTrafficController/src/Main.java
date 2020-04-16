import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


import java.util.*;

import org.eclipse.swt.widgets.MessageBox;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;


public class Main {
	private static Text txtID;

	/*
	 * COnstants
	 */
	private static String BUTTON_STARTSTOP_START_TEXT = "Start";
    private static String BUTTON_STARTSTOP_STOP_TEXT = "Stop";
    
	/*
	 * Form Variables
	 */
	private static Display display;
	private static Shell shlAirtrafficController;
	private static Group grpAircraftCreator;
	private static Group grpAircrafts;
	private static Label lblId;
	private static Label lblType;
	private static Label lblSize;
	private static Combo cBxAircraftType;
	private static Combo cBxAircraftSize;
	private static Button btnEnqueueAircraft;
	
	private static List<Aircraft> _aircrafts = new ArrayList<Aircraft>();
	private static int _aircraftOrder = 1;
	private static Button btnStartStopSystem;
	private static Button btnDequeueAircraft;
	private static ListViewer listViewer;
	private static Label lblJustABoringLabel;
	private static Label lblLastDequeue;
	
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		initializeForm();
		
		btnStartStopSystem.setText(BUTTON_STARTSTOP_START_TEXT);
       

		initComboBoxes();
		
		disableForm();
		while (!shlAirtrafficController.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	private static void aqm_request_process(AircraftControlRequest request)
    {
        try
        {
            switch (request)
            {
                case SystemStart:

                    startSystem();

                    break;

                case SystemStop:

                    stopSystem();

                    break;

                case EnqueueAC:

                    enqueueAircraft();

                    break;

                case DequeueAC:

                    dequeueAircraft();

                    break;
            }
        }
        catch(Exception ex)
        {
        	showMessageBox("Error", String.format("Encountered An Error Enqueueing Aircraft.\n\n %s", ex.getMessage()), SWT.ICON_ERROR | SWT.OK);
        }
    }
	
	/*
	 * Aircraft Control Methods
	 * ************************************************************************
	 */
	private static void enqueueAircraft()
    {
        try
        {
        	String idString = txtID.getText();
            if(idString == null || idString.isEmpty())
            {
                throw new Exception("Must have a valid ID");
            }

            if(cBxAircraftType.getSelectionIndex() < 0)
            {
            	throw new Exception("Must have a valid Aircraft Type");
            }
            
            if(cBxAircraftSize.getSelectionIndex() < 0)
            {
            	throw new Exception("Must have a valid Aircraft Size");
            }
            
            AircraftType type = AircraftType.valueOf(cBxAircraftType.getItem(cBxAircraftType.getSelectionIndex()).toString());
        	AircraftSize size = AircraftSize.valueOf(cBxAircraftSize.getItem(cBxAircraftSize.getSelectionIndex()).toString());

            _aircrafts.add(new Aircraft(idString, size, type, _aircraftOrder++));

            populateAircraftTable();

            
        }
        catch(Exception ex)
        {
        	showMessageBox("Error", String.format("Encountered An Error Enqueueing Aircraft.\n\n %s", ex.getMessage()), SWT.ICON_ERROR | SWT.OK);
        }
    }

	private static void dequeueAircraft()
    {
        try
        {
            if(_aircrafts == null || _aircrafts.size() <= 0)
            {
                return ;
            }

            Aircraft aircraftToDequeue = null;
            for(Aircraft aircraft : _aircrafts)
            {
                if(aircraftToDequeue == null)
                {
                    aircraftToDequeue = aircraft;
                    continue;
                }
                //If aircraft is passenger, and aircraftToDequeue is not aircraft moves up in line.
                else if(aircraftToDequeue.getType() != AircraftType.Passenger && aircraft.getType() == AircraftType.Passenger)
                {
                    aircraftToDequeue = aircraft;
                }
                //If aircraft is larger than aircraftToDequeue aircraft moves up in line.
                else if (aircraftToDequeue.getSize() != AircraftSize.Large && aircraft.getSize() == AircraftSize.Large)
                {
                    aircraftToDequeue = aircraft;
                }
                else if(aircraftToDequeue.getOrder() > aircraft.getOrder())
                {
                    aircraftToDequeue = aircraft;
                }
            }
            
            if(aircraftToDequeue != null)
            {
                _aircrafts.remove(aircraftToDequeue);
                
                lblLastDequeue.setText(aircraftToDequeue.toString());
                if (_aircrafts.size() == 0) _aircraftOrder = 1;

                populateAircraftTable();
            }

        }
        catch (Exception ex)
        {
        	showMessageBox("Error", String.format("Encountered An Error Dequeueing Aircraft.\n\n %s", ex.getMessage()), SWT.ICON_ERROR | SWT.OK);
        }
    }

    private static void stopSystem()
    {
    	if (_aircrafts != null && _aircrafts.size() > 0)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("There Are Still Aircraft In The Air!!!!\n\n");

            for(Aircraft aircraft : _aircrafts)
            {
                sb.append(aircraft.toString());
                sb.append("\n");
            }
            sb.append("Are you sure you still want to shut down?");


            if (showMessageBox("Look Up!!", sb.toString(), SWT.ICON_ERROR | SWT.OK | SWT.CANCEL) == SWT.CANCEL)
            {
                return;
            }
            else
            {
                _aircrafts.clear();
                _aircraftOrder = 1;

                populateAircraftTable();
            }
        }
    	
    	txtID.setText("");
    	cBxAircraftType.select(-1);
    	cBxAircraftSize.select(-1);

        btnStartStopSystem.setText(BUTTON_STARTSTOP_START_TEXT);
       

        disableForm();
    }

    private static void startSystem()
    {
    	btnStartStopSystem.setText(BUTTON_STARTSTOP_STOP_TEXT);
        

        enableForm();
    }
	/*
	 * Utility Methods
	 * ************************************************************************
	 */
	private static void initComboBoxes()
	{
		for (AircraftType type : AircraftType.values()) { 
		    cBxAircraftType.add(type.toString());
		}
		
		for (AircraftSize size : AircraftSize.values()) { 
		    cBxAircraftSize.add(size.toString());
		}
	}
	
    private static int showMessageBox(String title, String message, int style) {
       
        MessageBox dia = new MessageBox(shlAirtrafficController, style);
        dia.setText(title);
        dia.setMessage(message); 
        
        return dia.open();
    }
    
    private static void populateAircraftTable()
    {
    	
    	listViewer.setInput(_aircrafts);
    	listViewer.refresh();
    }
    
   
    
	private static void initializeForm()
	{
		display = Display.getDefault();
		
		
		
		shlAirtrafficController = new Shell();
		shlAirtrafficController.setSize(525, 473);
		shlAirtrafficController.setText("AirTraffic Controller");
		
		grpAircraftCreator = new Group(shlAirtrafficController, SWT.NONE);
		grpAircraftCreator.setText("Aircraft Creator");
		grpAircraftCreator.setBounds(10, 10, 488, 113);
		
		txtID = new Text(grpAircraftCreator, SWT.BORDER);
		txtID.setBounds(30, 23, 76, 23);
		
		lblId = new Label(grpAircraftCreator, SWT.NONE);
		lblId.setBounds(10, 26, 14, 15);
		lblId.setText("ID:");
		
		lblType = new Label(grpAircraftCreator, SWT.NONE);
		lblType.setText("Type:");
		lblType.setBounds(143, 26, 27, 15);
		
		lblSize = new Label(grpAircraftCreator, SWT.NONE);
		lblSize.setText("Size:");
		lblSize.setBounds(332, 26, 24, 15);
		
		cBxAircraftType = new Combo(grpAircraftCreator, SWT.NONE);
		cBxAircraftType.setBounds(176, 23, 107, 23);
		
		cBxAircraftSize = new Combo(grpAircraftCreator, SWT.NONE);
		cBxAircraftSize.setBounds(362, 23, 107, 23);
		
		btnStartStopSystem = new Button(shlAirtrafficController, SWT.NONE);
		btnStartStopSystem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(btnStartStopSystem.getText() == BUTTON_STARTSTOP_START_TEXT)
	            {
	                aqm_request_process(AircraftControlRequest.SystemStart);
	            }
	            //Stop the Form
	            else if(btnStartStopSystem.getText() == BUTTON_STARTSTOP_STOP_TEXT)
	            {
	                aqm_request_process(AircraftControlRequest.SystemStop);
	            }
			}
		});

		btnStartStopSystem.setBounds(10, 383, 488, 41);
		btnStartStopSystem.setText("Start");
		
		btnEnqueueAircraft = new Button(grpAircraftCreator, SWT.NONE);
		btnEnqueueAircraft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				aqm_request_process(AircraftControlRequest.EnqueueAC);
			}
		});
		btnEnqueueAircraft.setBounds(362, 70, 107, 25);
		btnEnqueueAircraft.setText("Enqueue Aircraft");
		
		grpAircrafts = new Group(shlAirtrafficController, SWT.NONE);
		grpAircrafts.setText("Aircrafts");
		grpAircrafts.setBounds(10, 130, 488, 232);
		
		btnDequeueAircraft = new Button(grpAircrafts, SWT.NONE);
		btnDequeueAircraft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				aqm_request_process(AircraftControlRequest.DequeueAC);
			}
		});
		btnDequeueAircraft.setText("Dequeue Aircraft");
		btnDequeueAircraft.setBounds(371, 197, 107, 25);
		
		listViewer = new ListViewer(grpAircrafts, SWT.BORDER | SWT.V_SCROLL);
		listViewer.setContentProvider(new IStructuredContentProvider() {
    		public Object[] getElements(Object inputElement) {
    			if ( inputElement instanceof java.util.List )
    			{
    				return ( (java.util.List<?>) inputElement ).toArray( );
    			}
    			return (Object[]) inputElement;
    		}
    		public void dispose() {
    		}
    		
    	});
    	listViewer.setLabelProvider(new LabelProvider() {
    		public String getText(Object element) {
    			return ((Aircraft) element).toString();
    		}
    	});
		org.eclipse.swt.widgets.List list = listViewer.getList();
		list.setBounds(10, 20, 468, 136);
		
		lblJustABoringLabel = new Label(grpAircrafts, SWT.NONE);
		lblJustABoringLabel.setBounds(20, 162, 82, 15);
		lblJustABoringLabel.setText("Last Dequeue: ");
		
		lblLastDequeue = new Label(grpAircrafts, SWT.NONE);
		lblLastDequeue.setBounds(108, 162, 370, 15);
		
		shlAirtrafficController.open();
		shlAirtrafficController.layout();
	}
	
	private static void enableForm()
	{
		grpAircraftCreator.setEnabled(true);
		
		grpAircrafts.setEnabled(true);
	}
	
	private static void disableForm()
    {
		grpAircraftCreator.setEnabled(false);

        grpAircrafts.setEnabled(false);
    }
}
