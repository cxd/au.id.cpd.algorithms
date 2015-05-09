/**
 * 
 */
package au.id.cpd.algorithms.utilities.plot;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.text.*;
import info.monitorenter.gui.chart.*;
import info.monitorenter.gui.chart.io.*;
import info.monitorenter.gui.chart.traces.*;
import info.monitorenter.gui.chart.axis.*;
import info.monitorenter.util.*;
import info.monitorenter.gui.chart.rangepolicies.*;
import info.monitorenter.gui.chart.labelformatters.*;

/**
 * @author Chris Davey cd@cpd.id.au
 * 
 * The gradient descent dialog displays the gradient
 * descent for the current network during training.
 * 
 */
public class GradientDescentFrame extends JFrame implements WindowStateListener {
	
	/**
	 * Internal list for errors from the network.
	 */
	private java.util.List<Double> errors;
	/**
	 * Number of epochs to trace
	 */
	private int epoch;
	/**
	 * Internal 2d trace instance.
	 */
	private ITrace2D trace;
	/**
	 * Internal chart instance.
	 */
	private Chart2D chart;
	/**
	 * Internal data collector.
	 */
	private ADataCollector collector;
	
	/**
	 * Construct the dialog with the parent frame.
	 * @param JFrame parent frame
	 * @param int number of epochs to display
	 */
	public GradientDescentFrame(int epochs) {
		super();
		this.epoch = epochs;
		this.errors = new Vector<Double>();
		this.init();
	}
	
	/**
	 * Construct the dialog with the parent frame.
	 * @param JFrame parent frame
	 * @param int number of epochs to display
	 */
	public GradientDescentFrame(int epochs, java.util.List<Double> e) {
		super();
		this.epoch = epochs;
		this.errors = e;
		this.init();
	}
	
	/**
	 * Initialise the component and display the dialog.
	 */
	private void init() {
		JPanel panel = new JPanel();
		Border border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		panel.setBorder(border);
		panel.setLayout(new BorderLayout());
		this.chart = new Chart2D();
		this.trace = new Trace2DLtd(this.epoch);
		this.trace.setColor(Color.BLUE);
		ARangePolicy policy = new RangePolicyMinimumViewport(new Range(0.0, 0.1));
		NumberFormat d_fmt = NumberFormat.getInstance();
		d_fmt.setMinimumFractionDigits(2);
		d_fmt.setMaximumFractionDigits(20);
		ALabelFormatter fmt = new LabelFormatterNumber(d_fmt);
		chart.getAxisY().setFormatter(fmt);
		chart.getAxisY().setRangePolicy(policy);
		chart.getAxisX().setPaintGrid(true);
		chart.getAxisY().setPaintGrid(true);
		chart.setBackground(Color.WHITE);
		chart.setGridColor(Color.gray);
		chart.addTrace(this.trace);
		
		panel.add(new info.monitorenter.gui.chart.views.ChartPanel(chart), BorderLayout.CENTER);
		this.setSize(640,480);
		this.getContentPane().add(panel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowStateListener(this);
		this.setVisible(true);
		if (errors.size() > 0) {
			startCollection();
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.WindowStateListener#windowStateChanged(java.awt.event.WindowEvent)
	 */
	public void windowStateChanged(WindowEvent e) {
		if ( e.getNewState() == WindowEvent.WINDOW_CLOSING ) {
			stopCollection();
		}
	}

	/**
	 * Start collection of data.
	 *
	 */
	public void startCollection() {
		this.collector = new ErrorDataCollector();
		this.collector.start();
	}
	
	/**
	 * Stop the data collection.
	 *
	 */
	public void stopCollection() {
		if (this.collector != null)
			this.collector.stop();
	}
	
	/**
	 * Update the trace display on the chart.
	 * @param errors
	 */
	public void updateTrace(java.util.List<Double> errors) {
		if (errors.size() == this.errors.size()) return;
		this.errors = errors;
	}
	
	class ErrorDataCollector extends ADataCollector {
		public ErrorDataCollector() {
			super(trace, 1000);
		}
		
		public TracePoint2D collectData() {
			if (errors.size() == 0) {
				try {
					Thread.sleep(500);
				} catch(InterruptedException e) {
				
				} 
				return this.collectData();
			}
			double value = errors.get(errors.size()-1);
			return new TracePoint2D(errors.size(), value);
		}
	}
	

	/**
	 * @return the epoch
	 */
	public int getEpoch() {
		return epoch;
	}

	/**
	 * @param epoch the epoch to set
	 */
	public void setEpoch(int epoch) {
		this.epoch = epoch;
	}

	/**
	 * @return the errors
	 */
	public java.util.List<Double> getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(java.util.List<Double> errors) {
		this.errors = errors;
	}

	/**
	 * @return the trace
	 */
	public ITrace2D getTrace() {
		return trace;
	}

	/**
	 * @param trace the trace to set
	 */
	public void setTrace(ITrace2D trace) {
		this.trace = trace;
	}
	
}
