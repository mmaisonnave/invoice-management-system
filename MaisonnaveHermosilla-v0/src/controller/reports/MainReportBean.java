package controller.reports;

import java.util.List;

public class MainReportBean {
	private List<TransaccionBean> subReportBeanListOriginal;
	private List<TransaccionBean> subReportBeanListCopia;
	
	public MainReportBean(List<TransaccionBean> l) {
		this.subReportBeanListCopia = l;
		this.subReportBeanListOriginal = l;
	}
	
	public List<TransaccionBean> getSubReportBeanListOriginal(){
		return this.subReportBeanListOriginal;
	}
	public List<TransaccionBean> getSubReportBeanListCopia(){
		return this.subReportBeanListCopia;
	}
}
