package edu.gsu.hxue.experimentUtilities.dataFileGeneration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public class UniformDataGenerationTool
{

	protected Shell shlUniformDataFile;
	private Text textNCol;
	private Label lblNumberOfRows;
	private Text textNRow;
	private Label lblCornerXCoordinate;
	private Label lblCornerYCoordinate;
	private Label lblCell;
	private Label lblFilepath;
	private Label lblValue;
	private Button btnChoose;
	private Text textXCor;
	private Text textYCor;
	private Text textCellSize;
	private Text textFilePath;
	private Text textValue;
	private Button btnGenerate;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			UniformDataGenerationTool window = new UniformDataGenerationTool();
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
		shlUniformDataFile.open();
		shlUniformDataFile.layout();
		while (!shlUniformDataFile.isDisposed())
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
		shlUniformDataFile = new Shell();
		shlUniformDataFile.setSize(611, 256);
		shlUniformDataFile.setText("Uniform Data File Generator");
		shlUniformDataFile.setLayout(new GridLayout(3, false));
		
		Label lblNumberOfColumns = new Label(shlUniformDataFile, SWT.NONE);
		lblNumberOfColumns.setAlignment(SWT.RIGHT);
		GridData gd_lblNumberOfColumns = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblNumberOfColumns.widthHint = 174;
		lblNumberOfColumns.setLayoutData(gd_lblNumberOfColumns);
		lblNumberOfColumns.setText("Number of Columns:");
		
		textNCol = new Text(shlUniformDataFile, SWT.BORDER);
		textNCol.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shlUniformDataFile, SWT.NONE);
		
		lblNumberOfRows = new Label(shlUniformDataFile, SWT.NONE);
		lblNumberOfRows.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNumberOfRows.setText("Number of Rows:");
		lblNumberOfRows.setAlignment(SWT.RIGHT);
		
		textNRow = new Text(shlUniformDataFile, SWT.BORDER);
		textNRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shlUniformDataFile, SWT.NONE);
		
		lblCornerXCoordinate = new Label(shlUniformDataFile, SWT.NONE);
		lblCornerXCoordinate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCornerXCoordinate.setText("Corner X Coordinate:");
		lblCornerXCoordinate.setAlignment(SWT.RIGHT);
		
		textXCor = new Text(shlUniformDataFile, SWT.BORDER);
		textXCor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shlUniformDataFile, SWT.NONE);
		
		lblCornerYCoordinate = new Label(shlUniformDataFile, SWT.NONE);
		lblCornerYCoordinate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCornerYCoordinate.setText("Corner Y Coordinate:");
		lblCornerYCoordinate.setAlignment(SWT.RIGHT);
		
		textYCor = new Text(shlUniformDataFile, SWT.BORDER);
		textYCor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shlUniformDataFile, SWT.NONE);
		
		lblCell = new Label(shlUniformDataFile, SWT.NONE);
		lblCell.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCell.setText("Cell Size:");
		lblCell.setAlignment(SWT.RIGHT);
		
		textCellSize = new Text(shlUniformDataFile, SWT.BORDER);
		textCellSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shlUniformDataFile, SWT.NONE);
		
		lblFilepath = new Label(shlUniformDataFile, SWT.NONE);
		lblFilepath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFilepath.setText("FilePath:");
		lblFilepath.setAlignment(SWT.RIGHT);
		
		textFilePath = new Text(shlUniformDataFile, SWT.BORDER);
		textFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnChoose = new Button(shlUniformDataFile, SWT.NONE);
		btnChoose.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				 FileDialog fd = new FileDialog(shlUniformDataFile, SWT.OPEN);
			     fd.setText("Open");
			     fd.setOverwrite(true);
			     String path = fd.open();
			     if( path!=null) textFilePath.setText(path);
			}
		});
		btnChoose.setText("Choose");
		
		lblValue = new Label(shlUniformDataFile, SWT.NONE);
		lblValue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblValue.setText("Value:");
		lblValue.setAlignment(SWT.RIGHT);
		
		textValue = new Text(shlUniformDataFile, SWT.BORDER);
		textValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shlUniformDataFile, SWT.NONE);
		
		btnGenerate = new Button(shlUniformDataFile, SWT.NONE);
		btnGenerate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				try
				{
					String filePath = textFilePath.getText().trim();
					int numberOfColumns = Integer.parseInt(textNCol.getText().trim());
					int numberOfRows = Integer.parseInt(textNRow.getText().trim());;
					double cornerXCoordinate = Double.parseDouble(textXCor.getText().trim());
					double cornerYCoordinate = Double.parseDouble(textYCor.getText().trim());
					double cellSize = Double.parseDouble(textCellSize.getText().trim());
					double value = Double.parseDouble(textValue.getText().trim());
					DataFileGenerationCore.generateUniformDataFile(filePath, numberOfColumns, numberOfRows, cornerXCoordinate, cornerYCoordinate, cellSize, value);
					
					MessageBox messageBox = new MessageBox(shlUniformDataFile, SWT.OK);
					messageBox.setText("Done");
					messageBox.setMessage("Data file is generated");
					messageBox.open();
				}
				catch (Exception e1)
				{
					MessageBox messageBox = new MessageBox(shlUniformDataFile, SWT.OK);
					messageBox.setText("Exception");
					messageBox.setMessage(e1.getMessage());
					messageBox.open();
					e1.printStackTrace();
				}
			}
		});
		GridData gd_btnGenerate = new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1);
		gd_btnGenerate.widthHint = 120;
		btnGenerate.setLayoutData(gd_btnGenerate);
		btnGenerate.setText("Generate");

	}

}
