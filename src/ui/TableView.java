package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

public class TableView extends JPanel implements FocusListener, 
												 KeyListener, 
												 MouseListener {
	private TableViewDataSource dataSource;
	private TableViewDelegate delegate;
	
	private int selectedColumn;
	private int selectedRow;
	
	private JLabel[] tableHeaders;
	private ScrollView scrollView;
	private JPanel contentView;
	private JPanel[] tableRows;
	private JTextArea[][] tableCells;
	
	private ArrayList<JLabel> tableHeaderReuseQueue;
	private ArrayList<JPanel> tableRowReuseQueue;
	private ArrayList<JTextArea> tableCellReuseQueue;
	
	public TableView() {
		dataSource = null;
		delegate = null;
		
		selectedColumn = -1;
		selectedRow = -1;
		
		tableHeaders = new JLabel[0];
		
		scrollView = new ScrollView();
		scrollView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollView.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.lightGray));
		
		contentView = new JPanel();
		contentView.setLayout(null);
		contentView.setPreferredSize(new Dimension(0, 0));
		contentView.addMouseListener(this);
		
		scrollView.setViewportView(contentView);
		
		tableRows = new JPanel[0];
		tableCells = new JTextArea[0][0];
		
		tableHeaderReuseQueue = new ArrayList<>(10);
		tableRowReuseQueue = new ArrayList<>(100);
		tableCellReuseQueue = new ArrayList<>(1000);
	}
	
	public TableViewDataSource getDataSource() {
		return dataSource;
	}
	
	public void setDataSource(TableViewDataSource dataSource) {
		this.dataSource = dataSource;
		
		reloadData();
	}
	
	public TableViewDelegate getDelegate() {
		return delegate;
	}
	
	public void setDelegate(TableViewDelegate delegate) {
		this.delegate = delegate;
		
		reloadData();
	}
	
	public int getSelectedColumn() {
		return selectedColumn;
	}
	
	public int getSelectedRow() {
		return selectedRow;
	}
	
	public void setSelectedCell(int columnIndex, int rowIndex) {
		int c = tableHeaders.length;
		int r = tableCells.length;
		
		if (columnIndex < -1 || columnIndex >= c || 
			rowIndex < -1 || rowIndex >= r || 
			(columnIndex == -1) != (rowIndex == -1)) {
			throw new IllegalArgumentException();
		}
		
		if (selectedColumn != -1 && 
			selectedRow != -1) {
			JLabel header = tableHeaders[selectedColumn];
			
			header.setForeground(Color.gray);
			
			JPanel row = tableRows[selectedRow];
			
			row.setBackground(null);
			
			for (int i = 0; i < c; i++) {
				JTextArea cell = tableCells[selectedRow][i];
				
				cell.setForeground(Color.gray);
			}
		}
		
		selectedColumn = columnIndex;
		selectedRow = rowIndex;
		
		if (selectedColumn != -1 && 
			selectedRow != -1) {
			JLabel header = tableHeaders[selectedColumn];
			
			header.setForeground(Color.black);
			
			JPanel row = tableRows[selectedRow];
			
			row.setBackground(Color.lightGray);
			
			for (int i = 0; i < c; i++) {
				JTextArea cell = tableCells[selectedRow][i];
				
				cell.setForeground(Color.black);
			}
			
			JTextArea cell = tableCells[selectedRow][selectedColumn];
			
			cell.requestFocusInWindow();
		}
		
		repositionContents();
		
		if (getDelegate() != null) {
			getDelegate().selectionChanged(this);
		}
	}
	
	public void reloadData() {
		int c = (getDataSource() != null) ? getDataSource().getNumberOfColumns(this) : 0;
		int r = (getDataSource() != null) ? getDataSource().getNumberOfRows(this) : 0;
		
		if (c < 0) c = 0;
		if (r < 0) r = 0;
		
		if (selectedColumn >= c || 
			selectedRow >= r) {
			selectedColumn = -1;
			selectedRow = -1;
		}
		
		int oldC = tableHeaders.length;
		int oldR = tableCells.length;
		
		if (c < oldC) {
			removeColumns(c);
		}
		if (r < oldR) {
			removeRows(r);
		}
		if (c > oldC) {
			addColumns(c);
		}
		if (r > oldR) {
			addRows(r);
		}
		
		for (int i = 0; i < c; i++) {
			String columnTitle = getDataSource().getColumnTitle(this, i);
			JLabel header = tableHeaders[i];
			
			if (selectedColumn != i) {
				header.setForeground(Color.gray);
			}else{
				header.setForeground(Color.black);
			}
			
			header.setText(columnTitle);
		}
		
		for (int i = 0; i < r; i++) {
			JPanel row = tableRows[i];
			
			if (selectedRow != i) {
				row.setBackground(null);
			}else{
				row.setBackground(Color.lightGray);
			}
			
			for (int j = 0; j < c; j++) {
				String cellValue = getDataSource().getCellValue(this, j, i);
				JTextArea cell = tableCells[i][j];
				
				if (selectedRow != i) {
					cell.setForeground(Color.gray);
				}else{
					cell.setForeground(Color.black);
				}
				
				cell.setText(cellValue);
			}
		}
		
		repositionContents();
	}
	
	private void addColumns(int c) {
		int oldC = tableHeaders.length;
		int r = tableCells.length;
		
		tableHeaders = Arrays.copyOf(tableHeaders, c);
		
		for (int i = oldC; i < c; i++) {
			JLabel header = getTableHeader();
			
			add(header);
			
			tableHeaders[i] = header;
		}
		
		for (int i = 0; i < r; i++) {
			JPanel row = tableRows[i];
			
			tableCells[i] = Arrays.copyOf(tableCells[i], c);
			
			for (int j = oldC; j < c; j++) {
				JTextArea cell = getTableCell();
				
				row.add(cell);
				
				tableCells[i][j] = cell;
			}
		}
	}
	
	private void removeColumns(int c) {
		int oldC = tableHeaders.length;
		int r = tableCells.length;
		
		for (int i = c; i < oldC; i++) {
			JLabel header = tableHeaders[i];
			
			remove(header);
			
			tableHeaderReuseQueue.add(header);
		}
		
		tableHeaders = Arrays.copyOf(tableHeaders, c);
		
		for (int i = 0; i < r; i++) {
			JPanel row = tableRows[i];
			
			for (int j = c; j < oldC; j++) {
				JTextArea cell = tableCells[i][j];
				
				row.remove(cell);
				
				tableCellReuseQueue.add(cell);
			}
			
			tableCells[i] = Arrays.copyOf(tableCells[i], c);
		}
	}
	
	private void addRows(int r) {
		int c = tableHeaders.length;
		int oldR = tableCells.length;
		
		tableRows = Arrays.copyOf(tableRows, r);
		tableCells = Arrays.copyOf(tableCells, r);
		
		for (int i = oldR; i < r; i++) {
			JPanel row = getTableRow();
			
			contentView.add(row);
			
			tableRows[i] = row;
			tableCells[i] = new JTextArea[c];
			
			for (int j = 0; j < c; j++) {
				JTextArea cell = getTableCell();
				
				row.add(cell);
				
				tableCells[i][j] = cell;
			}
		}
	}
	
	private void removeRows(int r) {
		int c = tableHeaders.length;
		int oldR = tableCells.length;
		
		for (int i = r; i < oldR; i++) {
			JPanel row = tableRows[i];
			
			contentView.remove(row);
			row.removeAll();
			
			tableRowReuseQueue.add(row);
			
			for (int j = 0; j < c; j++) {
				JTextArea cell = tableCells[i][j];
				
				tableCellReuseQueue.add(cell);
			}
		}
		
		tableRows = Arrays.copyOf(tableRows, r);
		tableCells = Arrays.copyOf(tableCells, r);
	}
	
	private JLabel getTableHeader() {
		int n = tableHeaderReuseQueue.size();
		
		if (n > 0) {
			return tableHeaderReuseQueue.remove(n - 1);
		}else{
			JLabel header = new JLabel();
			
			header.setBackground(null);
			header.setForeground(Color.gray);
			
			return header;
		}
	}
	
	private JPanel getTableRow() {
		int n = tableRowReuseQueue.size();
		
		if (n > 0) {
			return tableRowReuseQueue.remove(n - 1);
		}else{
			JPanel row = new JPanel();
			
			row.setBackground(null);
			row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
			row.setLayout(null);
			
			return row;
		}
	}
	
	private JTextArea getTableCell() {
		int n = tableCellReuseQueue.size();
		
		if (n > 0) {
			return tableCellReuseQueue.remove(n - 1);
		}else{
			JTextArea cell = new JTextArea();
			
			cell.setBackground(null);
			cell.setForeground(Color.gray);
			cell.setBorder(null);
			cell.addFocusListener(this);
			cell.addKeyListener(this);
			
			return cell;
		}
	}
	
	public void setSize(Dimension d) {
		super.setSize(d);
		
		repositionContents();
	}
	
	public void setSize(int width, int height) {
		super.setSize(width, height);
		
		repositionContents();
	}
	
	private void repositionContents() {
		int w = getWidth();
		int h = getHeight();
		
		int c = tableHeaders.length;
		int r = tableCells.length;
		
		int[] columnXs = new int[c];
		int[] columnWidths = new int[c];
		int contentWidth;
		
		if ((w - 16) / c < 200) {
			for (int i = 0; i < c; i++) {
				columnXs[i] = i * 250;
				columnWidths[i] = 250;
			}
			contentWidth = c * 250;
		}else{
			for (int i = 0; i < c; i++) {
				columnXs[i] = (i == 0) ? 0 : columnXs[i - 1] + columnWidths[i - 1];
				columnWidths[i] = (w - 16) / c + ((i < (w - 16) % c) ? 1 : 0);
			}
			contentWidth = w - 16;
		}
		
		for (int i = 0; i < c; i++) {
			JLabel header = tableHeaders[i];
			
			header.setLocation(columnXs[i], 0);
			header.setSize(columnWidths[i], 22);
		}
		
		scrollView.setLocation(0, 23);
		scrollView.setSize(w, h - 23);
		
		if (selectedRow == -1) {
			contentView.setPreferredSize(new Dimension(contentWidth, r * 23 - 1));
		}else{
			contentView.setPreferredSize(new Dimension(contentWidth, r * 23 + 43));
		}
		
		for (int i = 0; i < r; i++) {
			JPanel row = tableRows[i];
			
			int rowY = (i == 0) ? 0 : tableRows[i - 1].getY() + tableRows[i - 1].getHeight();
			int rowH = (selectedRow != i) ? 23 : 67;
			
			row.setLocation(0, rowY);
			row.setSize(contentWidth, rowH);
			
			for (int j = 0; j < c; j++) {
				JTextArea cell = tableCells[i][j];
				
				cell.setLocation(columnXs[j], rowY);
				cell.setSize(columnWidths[j], rowH - 1);
			}
		}
	}
	
	public void focusGained(FocusEvent e) {
		int c = tableHeaders.length;
		int r = tableCells.length;
		
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				JTextArea cell = tableCells[i][j];
				
				if (cell != e.getSource()) continue;
				
				setSelectedCell(j, i);
			}
		}
	}
	
	public void focusLost(FocusEvent e) {
		int c = tableHeaders.length;
		int r = tableCells.length;
		
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				JTextArea cell = tableCells[i][j];
				
				if (cell != e.getSource()) continue;
				
				if (getDataSource() == null) {
					reloadData();
				}else{
					getDataSource().setCellValue(this, j, i, cell.getText());
				}
				
				if (selectedColumn == j && 
					selectedRow == i) {
					setSelectedCell(-1, -1);
				}
			}
		}
	}
	
	public void keyTyped(KeyEvent e) {
		if (e.getKeyCode() != KeyEvent.VK_ESCAPE && 
			e.getSource() instanceof JTextArea) {
			JFrame frame = (JFrame) SwingUtilities.getRoot(this);
			
			if (frame != null) frame.requestFocusInWindow();
			
			return;
		}
		
		if (e.getKeyCode() != KeyEvent.VK_ENTER || 
		    (e.getModifiers() & KeyEvent.ALT_DOWN_MASK) != 0) {
			return;
		}
		
		int c = tableHeaders.length;
		int r = tableCells.length;
		
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				JTextArea cell = tableCells[i][j];
				
				if (cell != e.getSource()) continue;
				
				if (getDataSource() == null) {
					reloadData();
				}else{
					getDataSource().setCellValue(this, j, i, cell.getText());
				}
			}
		}
	}
	
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == contentView) {
			setSelectedCell(-1, -1);
		}
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	//Interfaces:
	public interface TableViewDataSource {
		
		public int getNumberOfColumns(TableView tableView);
		public int getNumberOfRows(TableView tableView);
		public String getColumnTitle(TableView tableView, int columnIndex);
		public String getCellValue(TableView tableView, int columnIndex, int rowIndex);
		public void setCellValue(TableView tableView, int columnIndex, int rowIndex, String value);
		
	}
	
	public interface TableViewDelegate {
		
		public void selectionChanged(TableView tableView);
		
	}
	
}
