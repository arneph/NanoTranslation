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
	
	private ScrollView scrollView;
	private JPanel headerView;
	private JLabel[] tableHeaders;
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
		
		setLayout(null);
		
		tableHeaders = new JLabel[0];
		
		scrollView = new ScrollView();
		scrollView.setBackground(Color.white);
		scrollView.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollView.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollView.setBorder(BorderFactory.createLineBorder(Color.lightGray, 1));
		
		headerView = new JPanel();
		headerView.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
		headerView.setOpaque(false);
		headerView.setLayout(null);
		headerView.setPreferredSize(new Dimension(0, 23));
		
		JPanel topRightCornerView = new JPanel();
		topRightCornerView.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.lightGray));
		topRightCornerView.setOpaque(false);
		topRightCornerView.setPreferredSize(new Dimension(18, 23));
		
		JPanel bottomRightCornerView = new JPanel();
		bottomRightCornerView.setBackground(new Color(223, 223, 223));
		bottomRightCornerView.setOpaque(true);
		bottomRightCornerView.setPreferredSize(new Dimension(18, 18));
		
		contentView = new JPanel();
		contentView.setOpaque(false);
		contentView.setLayout(null);
		contentView.setPreferredSize(new Dimension(0, 0));
		contentView.addMouseListener(this);
		
		scrollView.setViewportView(contentView);
		scrollView.setColumnHeaderView(headerView);
		scrollView.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, topRightCornerView);
		scrollView.setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, bottomRightCornerView);
		scrollView.getViewport().setOpaque(false);
		
		add(scrollView);
		
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
		
		if (selectedColumn == columnIndex && 
			selectedRow == rowIndex) {
			return;
		}
		
		if (selectedColumn != -1 && 
			selectedRow != -1) {
			JLabel header = tableHeaders[selectedColumn];
			
			header.setFont(header.getFont().deriveFont(Font.PLAIN));
			
			JPanel row = tableRows[selectedRow];
			
			row.setBackground(Color.white);
			
			for (int i = 0; i < c; i++) {
				JTextArea cell = tableCells[selectedRow][i];
				
				cell.setForeground(Color.gray);
				
				if (cell.isFocusOwner()) {
					requestFocusInWindow();
				}
			}
		}
		
		selectedColumn = columnIndex;
		selectedRow = rowIndex;
		
		if (selectedColumn != -1 && 
			selectedRow != -1) {
			JLabel header = tableHeaders[selectedColumn];
			
			header.setFont(header.getFont().deriveFont(Font.BOLD));
			
			JPanel row = tableRows[selectedRow];
			
			row.setBackground(new Color(223, 223, 223));
			
			for (int i = 0; i < c; i++) {
				JTextArea cell = tableCells[selectedRow][i];
				
				cell.setForeground(Color.black);
			}
			
			JTextArea cell = tableCells[selectedRow][selectedColumn];
			
			cell.requestFocusInWindow();
			cell.scrollRectToVisible(new Rectangle(0, 0, cell.getWidth(), cell.getHeight()));
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
				header.setFont(header.getFont().deriveFont(Font.PLAIN));
			}else{
				header.setFont(header.getFont().deriveFont(Font.BOLD));
			}
			
			header.setText(columnTitle);
		}
		
		for (int i = 0; i < r; i++) {
			JPanel row = tableRows[i];
			
			if (selectedRow != i) {
				row.setBackground(Color.white);
			}else{
				row.setBackground(new Color(223, 223, 223));
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
		revalidate();
		repaint();
	}
	
	private void addColumns(int c) {
		int oldC = tableHeaders.length;
		int r = tableCells.length;
		
		tableHeaders = Arrays.copyOf(tableHeaders, c);
		
		for (int i = oldC; i < c; i++) {
			JLabel header = getTableHeader();
			
			headerView.add(header);
			
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
			
			headerView.remove(header);
			
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
			header.setForeground(Color.black);
			
			return header;
		}
	}
	
	private JPanel getTableRow() {
		int n = tableRowReuseQueue.size();
		
		if (n > 0) {
			return tableRowReuseQueue.remove(n - 1);
		}else{
			JPanel row = new JPanel();
			
			row.setBackground(Color.white);
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
		
		scrollView.setLocation(0, 0);
		scrollView.setSize(w, h);
		
		int c = tableHeaders.length;
		int r = tableCells.length;
		
		if (c == 0) return;
		
		int[] columnXs = new int[c];
		int[] columnWidths = new int[c];
		int contentWidth;
		
		if ((w - 18) / c < 200) {
			for (int i = 0; i < c; i++) {
				columnXs[i] = i * 200;
				columnWidths[i] = 200;
			}
			contentWidth = c * 200;
		}else{
			for (int i = 0; i < c; i++) {
				columnXs[i] = (i == 0) ? 0 : columnXs[i - 1] + columnWidths[i - 1];
				columnWidths[i] = (w - 18) / c + ((i < (w - 16) % c) ? 1 : 0);
			}
			contentWidth = w - 18;
		}
		
		for (int i = 0; i < c; i++) {
			JLabel header = tableHeaders[i];
			
			header.setLocation(columnXs[i], 0);
			header.setSize(columnWidths[i], 22);
		}
		
		headerView.setPreferredSize(new Dimension(contentWidth, 23));
		
		if (selectedRow == -1) {
			contentView.setPreferredSize(new Dimension(contentWidth, r * 23 - 1));
		}else{
			contentView.setPreferredSize(new Dimension(contentWidth, r * 23 + 43));
		}
		
		for (int i = 0; i < r; i++) {
			JPanel row = tableRows[i];
			
			int rowY = (i == 0) ? 0 : tableRows[i - 1].getY() + tableRows[i - 1].getHeight();
			int rowH = (selectedRow != i) ? 18 :80;
			
			row.setLocation(0, rowY);
			row.setSize(contentWidth, rowH);
			
			for (int j = 0; j < c; j++) {
				JTextArea cell = tableCells[i][j];
				
				cell.setLocation(columnXs[j], 0);
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
	
	public void keyTyped(KeyEvent e) {}
	
	public void keyPressed(KeyEvent e) {
		int c = tableHeaders.length;
		int r = tableCells.length;
		
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				JTextArea cell = tableCells[i][j];
				
				if (cell != e.getSource()) continue;
				
				if (!e.isAltDown()) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						requestFocusInWindow();
					}
					
				}else{
					if (e.getKeyCode() == KeyEvent.VK_ENTER || 
						e.getKeyCode() == KeyEvent.VK_DOWN) {
						setSelectedCell(j, (i + 1) % r);
						
					}else if (e.getKeyCode() == KeyEvent.VK_UP) {
						setSelectedCell(j, (r + i - 1) % r);
						
					}else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
						setSelectedCell((j + 1) % c, i);
						
					}else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
						setSelectedCell((c + j - 1) % c, i);
					}
				}
			}
		}
	}
	
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
