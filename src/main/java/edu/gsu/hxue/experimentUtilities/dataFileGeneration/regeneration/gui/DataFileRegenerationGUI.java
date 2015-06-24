package edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.gui;

import edu.gsu.hxue.experimentUtilities.CoordinateSetPresenter;
import edu.gsu.hxue.experimentUtilities.ExpUtilities;
import edu.gsu.hxue.experimentUtilities.FuelPresenter;
import edu.gsu.hxue.experimentUtilities.SlopePresenter;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.*;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.Alignment;
import edu.gsu.hxue.experimentUtilities.dataFileGeneration.regeneration.CoordinateConverter.BorderType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DataFileRegenerationGUI
{

	protected Shell shlDesfireDataRegeneration;
	private Text textFuelPath;
	private Button btnShowFuel;
	private Label lblAspectFile;
	private Text textAspectPath;
	private Text textSlopePath;
	private Text textInitialIgnitionPath;
	private Text textContainedCellPath;
	private Label lblSlopeFile;
	private Label lblInitialIgnitionFile;
	private Label lblContainedCellFile;
	private Button btnOpenAspect;
	private Button btnOpenSlope;
	private Button btnOpenIgnition;
	private Button btnOpenContainedCell;
	private Button btnShowAspect;
	private Button btnShowSlope;
	private Button btnShowIgnition;
	private Button showContainedCell;
	
	private File fuelFile;
	private File aspectFile;
	private File slopeFile;
	private File initialIgnitionFile;
	private File containedCellFile;
	private File finalBurnedAreaFile;
	
	private Integer xDim, yDim;
	private Double cellSize;
	private File newFileFolder;
	
	private Group grpDataRegeneration;
	private Group grpWhenTheNew;
	private Button btnLarger;
	private Button btnSmaller;
	private Group grpAlignTheNew;
	private Text textFolder;
	private Group grpNewCellSize;
	private Label lblOldGridInfo;
	private Button btnGenerate;
	private Button btnRemoveFuel;
	private Button btnRemoveAspect;
	private Button btnRemoveSlope;
	private Button btnRemoveIgnition;
	private Button btnRemoveContainedCell;
	private Label lblFinalBurnedArea;
	private Text textFinalBurnedAreaPath;
	private Button btnOpenBurnedArea;
	private Button btnShowBurnedArea;
	private Button btnRemoveBurnedArea;
	private Label lblThreshold;
	private Label label;
	private Spinner spinContainedCellThreshold;
	private Spinner spinBurnedAreaThreshold;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			DataFileRegenerationGUI window = new DataFileRegenerationGUI();
			window.open();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open()
	{
		Display display = Display.getDefault();
		createContents();
		updateDimInfo();
		shlDesfireDataRegeneration.open();
		shlDesfireDataRegeneration.layout();
		while (!shlDesfireDataRegeneration.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents()
	{
		shlDesfireDataRegeneration = new Shell();
		shlDesfireDataRegeneration.setSize(627, 522);
		shlDesfireDataRegeneration.setText("DESFire Data Regeneration Tool");
		shlDesfireDataRegeneration.setLayout(new GridLayout(7, false));
		
		Label lblFuelFile = new Label(shlDesfireDataRegeneration, SWT.NONE);
		lblFuelFile.setAlignment(SWT.RIGHT);
		GridData gd_lblFuelFile = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblFuelFile.widthHint = 84;
		lblFuelFile.setLayoutData(gd_lblFuelFile);
		lblFuelFile.setText("Fuel File:");
		
		textFuelPath = new Text(shlDesfireDataRegeneration, SWT.BORDER);
		textFuelPath.setEditable(false);
		textFuelPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnOpenFuel = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnOpenFuel.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fd = new FileDialog(shlDesfireDataRegeneration, SWT.OPEN);
				fd.setText("Open");
				fd.setOverwrite(true);
				String path = fd.open();
				if (path != null)
				{
					fuelFile = new File(path);
					if(extractDim(fuelFile)) textFuelPath.setText(path);
				}
			}
		});
		btnOpenFuel.setText("Open");
		
		btnShowFuel = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnShowFuel.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if(fuelFile==null)
				{
					MessageBox messageBox = new MessageBox(shlDesfireDataRegeneration, SWT.OK);
					messageBox.setText("File not opened");
					messageBox.setMessage("Please open the file first");
					messageBox.open();
				}
				else  FuelPresenter.showFuel(fuelFile);
			}
		});
		btnShowFuel.setText("Show");
		
		btnRemoveFuel = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnRemoveFuel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fuelFile=null;
				textFuelPath.setText("");
				updateDimInfo();
			}
		});
		btnRemoveFuel.setText("Remove");
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		
		lblAspectFile = new Label(shlDesfireDataRegeneration, SWT.NONE);
		lblAspectFile.setAlignment(SWT.RIGHT);
		lblAspectFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAspectFile.setText("Aspect File:");
		
		textAspectPath = new Text(shlDesfireDataRegeneration, SWT.BORDER);
		textAspectPath.setEditable(false);
		textAspectPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnOpenAspect = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnOpenAspect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog fd = new FileDialog(shlDesfireDataRegeneration, SWT.OPEN);
				fd.setText("Open");
				fd.setOverwrite(true);
				String path = fd.open();
				if (path != null)
				{
					
					aspectFile = new File(path);
					if(extractDim(aspectFile)) textAspectPath.setText(path);
				}
			}
		});
		btnOpenAspect.setText("Open");
		
		btnShowAspect = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnShowAspect.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			
			{
				if(aspectFile==null)
				{
					MessageBox messageBox = new MessageBox(shlDesfireDataRegeneration, SWT.OK);
					messageBox.setText("File not opened");
					messageBox.setMessage("Please open the file first");
					messageBox.open();
				}
				else 
				ExpUtilities.showAspect(aspectFile);
			}
		});
		btnShowAspect.setText("Show");
		
		btnRemoveAspect = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnRemoveAspect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				aspectFile=null;
				textAspectPath.setText("");
				updateDimInfo();
			}
		});
		btnRemoveAspect.setText("Remove");
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		
		lblSlopeFile = new Label(shlDesfireDataRegeneration, SWT.NONE);
		lblSlopeFile.setAlignment(SWT.RIGHT);
		lblSlopeFile.setText("Slope File:");
		lblSlopeFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		textSlopePath = new Text(shlDesfireDataRegeneration, SWT.BORDER);
		textSlopePath.setEditable(false);
		textSlopePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnOpenSlope = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnOpenSlope.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shlDesfireDataRegeneration, SWT.OPEN);
				fd.setText("Open");
				fd.setOverwrite(true);
				String path = fd.open();
				if (path != null)
				{
					slopeFile = new File(path);
					if(extractDim(slopeFile)) textSlopePath.setText(path);
				}
			}
		});
		btnOpenSlope.setText("Open");
		
		btnShowSlope = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnShowSlope.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(slopeFile==null)
				{
					MessageBox messageBox = new MessageBox(shlDesfireDataRegeneration, SWT.OK);
					messageBox.setText("File not opened");
					messageBox.setMessage("Please open the file first");
					messageBox.open();
				}
				else 
					SlopePresenter.showSlope(slopeFile);
			}
		});
		btnShowSlope.setText("Show");
		
		btnRemoveSlope = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnRemoveSlope.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				slopeFile=null;
				textSlopePath.setText("");
				updateDimInfo();
			}
		});
		btnRemoveSlope.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnRemoveSlope.setText("Remove");
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		
		lblInitialIgnitionFile = new Label(shlDesfireDataRegeneration, SWT.NONE);
		lblInitialIgnitionFile.setAlignment(SWT.RIGHT);
		lblInitialIgnitionFile.setText("Initial Ignition File:");
		lblInitialIgnitionFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		textInitialIgnitionPath = new Text(shlDesfireDataRegeneration, SWT.BORDER);
		textInitialIgnitionPath.setEditable(false);
		textInitialIgnitionPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnOpenIgnition = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnOpenIgnition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shlDesfireDataRegeneration, SWT.OPEN);
				fd.setText("Open");
				fd.setOverwrite(true);
				String path = fd.open();
				if (path != null)
				{
					textInitialIgnitionPath.setText(path);
					initialIgnitionFile = new File(path);
				}
			}
		});
		btnOpenIgnition.setText("Open");
		
		btnShowIgnition = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnShowIgnition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(initialIgnitionFile==null)
				{
					MessageBox messageBox = new MessageBox(shlDesfireDataRegeneration, SWT.OK);
					messageBox.setText("File not opened");
					messageBox.setMessage("Please open the file first");
					messageBox.open();
				}
				else if(xDim!=null && yDim!=null)
					CoordinateSetPresenter.show(initialIgnitionFile, xDim, yDim);
				else
				{
					MessageBox messageBox = new MessageBox(shlDesfireDataRegeneration, SWT.OK);
					messageBox.setText("Unknown Dimensions");
					messageBox.setMessage("a ignition point set has to be opened when at least 1 GIS data file is set");
					messageBox.open();
				}
					
			}
		});
		btnShowIgnition.setText("Show");
		
		btnRemoveIgnition = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnRemoveIgnition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				initialIgnitionFile = null;
				textInitialIgnitionPath.setText("");
			}
		});
		btnRemoveIgnition.setText("Remove");
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		
		lblContainedCellFile = new Label(shlDesfireDataRegeneration, SWT.NONE);
		lblContainedCellFile.setAlignment(SWT.RIGHT);
		lblContainedCellFile.setText("Contained Cell File:");
		lblContainedCellFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		textContainedCellPath = new Text(shlDesfireDataRegeneration, SWT.BORDER);
		textContainedCellPath.setEditable(false);
		textContainedCellPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnOpenContainedCell = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnOpenContainedCell.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shlDesfireDataRegeneration, SWT.OPEN);
				fd.setText("Open");
				fd.setOverwrite(true);
				String path = fd.open();
				if (path != null)
				{
					textContainedCellPath.setText(path);
					containedCellFile = new File(path);
				}
			}
		});
		btnOpenContainedCell.setText("Open");
		
		showContainedCell = new Button(shlDesfireDataRegeneration, SWT.NONE);
		showContainedCell.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(containedCellFile==null)
				{
					MessageBox messageBox = new MessageBox(shlDesfireDataRegeneration, SWT.OK);
					messageBox.setText("File not opened");
					messageBox.setMessage("Please open the file first");
					messageBox.open();
				}
				else if(xDim!=null && yDim!=null)
					CoordinateSetPresenter.show(containedCellFile, xDim, yDim);
				else
				{
					MessageBox messageBox = new MessageBox(shlDesfireDataRegeneration, SWT.OK);
					messageBox.setText("Unknown Dimensions");
					messageBox.setMessage("a contained cell set has to be opened when at least 1 GIS data file is set");
					messageBox.open();
				}
			}
		});
		showContainedCell.setText("Show");
		
		btnRemoveContainedCell = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnRemoveContainedCell.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				containedCellFile=null;
				textContainedCellPath.setText("");
			}
		});
		btnRemoveContainedCell.setText("Remove");
		
		lblThreshold = new Label(shlDesfireDataRegeneration, SWT.NONE);
		lblThreshold.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblThreshold.setText("Threshold:");
		
		spinContainedCellThreshold = new Spinner(shlDesfireDataRegeneration, SWT.BORDER);
		spinContainedCellThreshold.setSelection(50);
		spinContainedCellThreshold.setDigits(2);
		
		lblFinalBurnedArea = new Label(shlDesfireDataRegeneration, SWT.NONE);
		lblFinalBurnedArea.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFinalBurnedArea.setText("Final Burned Area File:");
		lblFinalBurnedArea.setAlignment(SWT.RIGHT);
		
		textFinalBurnedAreaPath = new Text(shlDesfireDataRegeneration, SWT.BORDER);
		textFinalBurnedAreaPath.setEditable(false);
		textFinalBurnedAreaPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnOpenBurnedArea = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnOpenBurnedArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog fd = new FileDialog(shlDesfireDataRegeneration, SWT.OPEN);
				fd.setText("Open");
				fd.setOverwrite(true);
				String path = fd.open();
				if (path != null)
				{
					textFinalBurnedAreaPath.setText(path);
					finalBurnedAreaFile = new File(path);
				}
			}
		});
		btnOpenBurnedArea.setText("Open");
		
		btnShowBurnedArea = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnShowBurnedArea.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(finalBurnedAreaFile==null)
				{
					MessageBox messageBox = new MessageBox(shlDesfireDataRegeneration, SWT.OK);
					messageBox.setText("File not opened");
					messageBox.setMessage("Please open the file first");
					messageBox.open();
				}
				else if(xDim!=null && yDim!=null)
					CoordinateSetPresenter.show(finalBurnedAreaFile, xDim, yDim);
				else
				{
					MessageBox messageBox = new MessageBox(shlDesfireDataRegeneration, SWT.OK);
					messageBox.setText("Unknown Dimensions");
					messageBox.setMessage("a ignition point set has to be opened when at least 1 GIS data file is set");
					messageBox.open();
				}
			}
		});
		btnShowBurnedArea.setText("Show");
		
		btnRemoveBurnedArea = new Button(shlDesfireDataRegeneration, SWT.NONE);
		btnRemoveBurnedArea.setText("Remove");
		
		label = new Label(shlDesfireDataRegeneration, SWT.NONE);
		label.setText("Threshold:");
		
		spinBurnedAreaThreshold = new Spinner(shlDesfireDataRegeneration, SWT.BORDER);
		spinBurnedAreaThreshold.setSelection(50);
		spinBurnedAreaThreshold.setDigits(2);
		
		lblOldGridInfo = new Label(shlDesfireDataRegeneration, SWT.NONE);
		lblOldGridInfo.setAlignment(SWT.CENTER);
		GridData gd_lblOldGridInfo = new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1);
		gd_lblOldGridInfo.heightHint = 27;
		lblOldGridInfo.setLayoutData(gd_lblOldGridInfo);
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		
		grpDataRegeneration = new Group(shlDesfireDataRegeneration, SWT.NONE);
		grpDataRegeneration.setLayout(new GridLayout(1, false));
		GridData gd_grpDataRegeneration = new GridData(SWT.FILL, SWT.FILL, true, false, 7, 1);
		gd_grpDataRegeneration.heightHint = 252;
		grpDataRegeneration.setLayoutData(gd_grpDataRegeneration);
		grpDataRegeneration.setText("Data Regeneration");
		
		
		grpWhenTheNew = new Group(grpDataRegeneration, SWT.NONE);
		grpWhenTheNew.setLayout(new RowLayout(SWT.HORIZONTAL));
		grpWhenTheNew.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpWhenTheNew.setText("When the new grids cannot exactly match the old grids (border type):");
		
		btnLarger = new Button(grpWhenTheNew, SWT.RADIO);
		btnLarger.setText("new grids are LARGER");
		
		btnSmaller = new Button(grpWhenTheNew, SWT.RADIO);
		btnSmaller.setSelection(true);
		btnSmaller.setText("new grids are SMALLER");
		
		grpAlignTheNew = new Group(grpDataRegeneration, SWT.NONE);
		grpAlignTheNew.setLayout(new RowLayout(SWT.HORIZONTAL));
		grpAlignTheNew.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpAlignTheNew.setText("Align the new grids to which corner of the old grids (alignment):");
		
		final Button btnUpperleft = new Button(grpAlignTheNew, SWT.RADIO);
		btnUpperleft.setText("upper-left");
		
		final Button btnUpperright = new Button(grpAlignTheNew, SWT.RADIO);
		btnUpperright.setText("upper-right");
		
		final Button btnLowerleft = new Button(grpAlignTheNew, SWT.RADIO);
		btnLowerleft.setText("lower-left");
		
		final Button btnLowerright = new Button(grpAlignTheNew, SWT.RADIO);
		btnLowerright.setSelection(true);
		btnLowerright.setText("lower-right");
		
		Group grpWhereToPut = new Group(grpDataRegeneration, SWT.NONE);
		grpWhereToPut.setLayout(new GridLayout(2, false));
		grpWhereToPut.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		grpWhereToPut.setText("Where to put the new generated files:");
		
		Button btnChooseFolder = new Button(grpWhereToPut, SWT.NONE);
		btnChooseFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog  dd = new DirectoryDialog(shlDesfireDataRegeneration, SWT.OPEN);
				dd.setText("Choose Folder");
				String path = dd.open();
				if (path != null)
				{
					textFolder.setText(path);
					newFileFolder = new File(path);
				}
			}
		});
		btnChooseFolder.setText("Choose");
		
		textFolder = new Text(grpWhereToPut, SWT.BORDER);
		textFolder.setEditable(false);
		textFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		grpNewCellSize = new Group(grpDataRegeneration, SWT.NONE);
		grpNewCellSize.setLayout(new FillLayout(SWT.HORIZONTAL));
		grpNewCellSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpNewCellSize.setText("New Cell Size");
		
		final Spinner spinNewCellSize = new Spinner(grpNewCellSize, SWT.BORDER);
		spinNewCellSize.setMaximum(9999999);
		spinNewCellSize.setMinimum(100);
		spinNewCellSize.setSelection(30000);
		spinNewCellSize.setDigits(3);
		
		btnGenerate = new Button(grpDataRegeneration, SWT.NONE);
		btnGenerate.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				// parameters
				BorderType borderType = null;
				if( btnLarger.getSelection())
					borderType = BorderType.LARGER;
				else if(btnSmaller.getSelection())
					borderType = BorderType.SMALLER;
				
				Alignment alignment = null;
				if( btnUpperleft.getSelection())
					alignment = Alignment.UPPER_LEFT;
				else if( btnUpperright.getSelection())
					alignment = Alignment.UPPER_RIGHT;
				else if( btnLowerleft.getSelection())
					alignment = Alignment.LOWER_LEFT;
				else if( btnLowerright.getSelection())
					alignment = Alignment.LOWER_RIGHT;
				
				double newCellSize = spinNewCellSize.getSelection()/Math.pow(10, spinNewCellSize.getDigits());
				
				
				try
				{
					if( borderType!=null && alignment!=null && newFileFolder!=null)
					{
						// fuel
						if(fuelFile!=null)
						{
							File oldDataFile = fuelFile;
							File newFile = new File(newFileFolder, "fuel_res" + newCellSize+ ".txt");
							String newFilePath = newFile.getAbsolutePath();
							FuelFileRegenerator tr = new FuelFileRegenerator();
							
								try
								{
									tr.convertFile(oldDataFile, newFilePath, newCellSize, borderType, alignment);
								}
								catch (Exception e1)
								{
									e1.printStackTrace();
								}
							System.out.println("Fuel file generated");
						}
						
						//slope and aspect
						{
								if(slopeFile!=null && aspectFile!=null )
								{
									TerrainFileRegenerator tr = new TerrainFileRegenerator();
									tr.convertFile(aspectFile, slopeFile, 
											new File(newFileFolder, "aspect_res" + newCellSize+ ".txt").getAbsolutePath(), 
											new File(newFileFolder, "slope_res" + newCellSize+ ".txt").getAbsolutePath(), 
											newCellSize, 
											borderType, 
											alignment);
									System.out.println("Aspect and slope files generated");
								}	
								else if( slopeFile==null && aspectFile==null );
								else if( slopeFile==null || aspectFile==null )
								{
									throw new DataRegenerationException("slope file and aspect file have to be set together");
								}
						}
						
						//ignition and contained cells
						{
							CoordinateConverter con = new CoordinateConverter(cellSize, xDim, yDim, newCellSize, borderType, alignment);
							
							if(initialIgnitionFile!=null)
							{
								IgnitionSetConverter.convertFile(initialIgnitionFile, new File(newFileFolder, "initialIgnition_res" + newCellSize + ".txt").getAbsolutePath(),
										con);
								System.out.println("Ignition file generated");
							}

							if (containedCellFile != null)
							{
								double threshold = spinContainedCellThreshold.getSelection() / Math.pow(10, spinContainedCellThreshold.getDigits());
								CoordinateSetFileConverter.convertFile(containedCellFile, new File(newFileFolder, "containedCells_res" + newCellSize + ".txt").getAbsolutePath(), con, threshold);
								System.out.println("Contained cell file generated");
							}
							
							if(finalBurnedAreaFile != null)
							{
								double threshold = spinBurnedAreaThreshold.getSelection() / Math.pow(10, spinBurnedAreaThreshold.getDigits());
								CoordinateSetFileConverter.convertFile(finalBurnedAreaFile, new File(newFileFolder, "finalBurnedAreaFile_res" + newCellSize + ".txt").getAbsolutePath(), con, threshold);
								System.out.println("final burned area file generated");
							}
						}
						
						MessageBox messageBox = new MessageBox(shlDesfireDataRegeneration, SWT.OK);
						messageBox.setText("Successful");
						messageBox.setMessage("Data generated.");
						messageBox.open();
						
						
					}
					else
					{
						MessageBox messageBox = new MessageBox(shlDesfireDataRegeneration, SWT.OK);
						messageBox.setText("Unknown parameters");
						messageBox.setMessage("Please set border type, alignment and new data file location");
						messageBox.open();
					}
				}
				catch (DataRegenerationException ex)
				{
					MessageBox messageBox = new MessageBox(shlDesfireDataRegeneration, SWT.OK);
					messageBox.setText("Exception");
					messageBox.setMessage(ex.getMessage());
					messageBox.open();
					ex.printStackTrace();
				}
			}
		});
		GridData gd_btnGenerate = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnGenerate.widthHint = 116;
		btnGenerate.setLayoutData(gd_btnGenerate);
		btnGenerate.setText("Generate");
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		new Label(shlDesfireDataRegeneration, SWT.NONE);
		new Label(shlDesfireDataRegeneration, SWT.NONE);

	}
	
	private void updateDimInfo()
	{
		if(this.fuelFile==null && this.aspectFile==null && this.slopeFile==null)
		{
			this.xDim =null;
			this.yDim=null;
			this.cellSize=null;
			lblOldGridInfo.setText(String.format("xDim=%d, yDim=%d, cellSize=%.4f", xDim, yDim, cellSize));
		}
			
	}
	
	
	private boolean extractDim(File file)
	{
		Scanner s;
		try
		{
			s = new Scanner(file);
			s.next(); // skip "ncols"
			int xDim = s.nextInt(); // load the value of "ncols"
			s.next(); // skip "nrows"
			int yDim = s.nextInt(); // load the value of "nrows"
			s.next(); // skip "xllcenter"
			s.next(); // skip the value of "xllcenter"
			s.next(); // skip "yllcenter"
			s.next(); // skip the value of "yllcenter"
			s.next(); // skip "cellsize"
			double cellSize = s.nextDouble(); // load the value of "cellsize"
			s.close();
			
			if (this.xDim == null && this.yDim==null && this.cellSize==null)
			{
				this.xDim = xDim;
				this.yDim = yDim;
				this.cellSize = cellSize;
				lblOldGridInfo.setText(String.format("xDim=%d, yDim=%d, cellSize=%.2f", xDim, yDim, cellSize));
			}
			else
			{
				if (this.xDim != xDim || this.yDim!=yDim || this.cellSize!=cellSize)
				{
					MessageBox messageBox = new MessageBox(shlDesfireDataRegeneration, SWT.OK);
					messageBox.setText("Inconsistance Warning");
					messageBox.setMessage("the juse opened file has different dimensions from at least one of another opened files");
					messageBox.open();
					
					return false;
				}
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
}
